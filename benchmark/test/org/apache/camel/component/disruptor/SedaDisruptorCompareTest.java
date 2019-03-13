/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.disruptor;


import ExchangePattern.InOnly;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.HdrHistogram.Histogram;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * This class does not perform any functional test, but instead makes a comparison between the performance of the
 * Disruptor and SEDA component in several use cases.
 * <p/>
 * As memory management may have great impact on the results, it is adviced to run this test with a large, fixed heap (e.g. run with -Xmx1024m -Xms1024m JVM parameters)
 */
@Ignore
@RunWith(Parameterized.class)
public class SedaDisruptorCompareTest extends CamelTestSupport {
    // Use '0' for default value, '1'+ for specific value to be used by both SEDA and DISRUPTOR.
    private static final int SIZE_PARAMETER_VALUE = 1024;

    private static final int SPEED_TEST_EXCHANGE_COUNT = 80000;

    private static final long[] LATENCY_HISTOGRAM_BOUNDS = new long[]{ 1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, 2000, 5000 };

    private static final long[] DISRUPTOR_SIZE_HISTOGRAM_BOUNDS = SedaDisruptorCompareTest.generateLinearHistogramBounds(((SedaDisruptorCompareTest.SIZE_PARAMETER_VALUE) == 0 ? 1024 : SedaDisruptorCompareTest.SIZE_PARAMETER_VALUE), 8);

    private static final long[] SEDA_SIZE_HISTOGRAM_BOUNDS = SedaDisruptorCompareTest.generateLinearHistogramBounds(((SedaDisruptorCompareTest.SIZE_PARAMETER_VALUE) == 0 ? SedaDisruptorCompareTest.SPEED_TEST_EXCHANGE_COUNT : SedaDisruptorCompareTest.SIZE_PARAMETER_VALUE), 10);

    @Produce
    protected ProducerTemplate producerTemplate;

    private final SedaDisruptorCompareTest.ExchangeAwaiter[] exchangeAwaiters;

    private final String componentName;

    private final String endpointUri;

    private final int amountProducers;

    private final long[] sizeHistogramBounds;

    private final Queue<Integer> endpointSizeQueue = new ConcurrentLinkedQueue<>();

    public SedaDisruptorCompareTest(final String componentName, final String endpointUri, final int amountProducers, final int amountConsumers, final int concurrentConsumerThreads, final long[] sizeHistogramBounds) {
        this.componentName = componentName;
        this.endpointUri = endpointUri;
        this.amountProducers = amountProducers;
        this.sizeHistogramBounds = sizeHistogramBounds;
        exchangeAwaiters = new SedaDisruptorCompareTest.ExchangeAwaiter[amountConsumers];
        for (int i = 0; i < amountConsumers; ++i) {
            exchangeAwaiters[i] = new SedaDisruptorCompareTest.ExchangeAwaiter(SedaDisruptorCompareTest.SPEED_TEST_EXCHANGE_COUNT);
        }
    }

    @Test
    public void speedTestDisruptor() throws InterruptedException {
        System.out.println(("Warming up for test of: " + (componentName)));
        performTest(true);
        System.out.println(("Starting real test of: " + (componentName)));
        forceGC();
        Thread.sleep(1000);
        performTest(false);
    }

    private static final class ExchangeAwaiter implements Processor {
        private CountDownLatch latch;

        private final int count;

        private long countDownReachedTime;

        private Queue<Long> latencyQueue = new ConcurrentLinkedQueue<>();

        ExchangeAwaiter(final int count) {
            this.count = count;
        }

        public void reset() {
            latencyQueue = new ConcurrentLinkedQueue<>();
            latch = new CountDownLatch(count);
            countDownReachedTime = 0;
        }

        public boolean awaitMessagesReceived(final long timeout, final TimeUnit unit) throws InterruptedException {
            return latch.await(timeout, unit);
        }

        public String getStatus() {
            final StringBuilder sb = new StringBuilder(100);
            sb.append("processed ");
            sb.append(((count) - (latch.getCount())));
            sb.append('/');
            sb.append(count);
            sb.append(" messages");
            return sb.toString();
        }

        @Override
        public void process(final Exchange exchange) throws Exception {
            final long sentTimeNs = exchange.getIn().getBody(Long.class);
            latencyQueue.offer(Long.valueOf(((System.nanoTime()) - sentTimeNs)));
            countDownReachedTime = System.currentTimeMillis();
            latch.countDown();
        }

        public long getCountDownReachedTime() {
            // Make sure we wait until all exchanges have been processed. Otherwise the time value doesn't make sense.
            try {
                latch.await();
            } catch (InterruptedException e) {
                countDownReachedTime = 0;
            }
            return countDownReachedTime;
        }

        public Histogram getLatencyHistogram() {
            final Histogram histogram = new Histogram(SedaDisruptorCompareTest.LATENCY_HISTOGRAM_BOUNDS[((SedaDisruptorCompareTest.LATENCY_HISTOGRAM_BOUNDS.length) - 1)], 4);
            for (final Long latencyValue : latencyQueue) {
                histogram.recordValue((latencyValue / 1000000));
            }
            return histogram;
        }
    }

    private final class ProducerThread extends Thread {
        private final int totalMessageCount;

        private int producedMessageCount;

        ProducerThread(final int totalMessageCount) {
            super("TestDataProducerThread");
            this.totalMessageCount = totalMessageCount;
        }

        public void run() {
            final Endpoint endpoint = context().getEndpoint(endpointUri);
            while (((producedMessageCount)++) < (totalMessageCount)) {
                producerTemplate.sendBody(endpoint, InOnly, System.nanoTime());
            } 
        }
    }
}

