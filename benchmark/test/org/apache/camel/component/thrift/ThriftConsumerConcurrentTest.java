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
package org.apache.camel.component.thrift;


import com.googlecode.junittoolbox.RunnableAssert;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.component.thrift.generated.Calculator;
import org.apache.camel.component.thrift.generated.Operation;
import org.apache.camel.component.thrift.generated.Work;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TNonblockingTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ThriftConsumerConcurrentTest extends CamelTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(ThriftConsumerConcurrentTest.class);

    private static final int THRIFT_SYNC_REQUEST_TEST_PORT = AvailablePortFinder.getNextAvailable();

    private static final int THRIFT_ASYNC_REQUEST_TEST_PORT = AvailablePortFinder.getNextAvailable();

    private static final int THRIFT_TEST_NUM1 = 12;

    private static final int CONCURRENT_THREAD_COUNT = 30;

    private static final int ROUNDS_PER_THREAD_COUNT = 10;

    private static AtomicInteger idCounter = new AtomicInteger();

    @Test
    public void testSyncWithConcurrentThreads() throws Exception {
        RunnableAssert ra = new RunnableAssert("testSyncWithConcurrentThreads") {
            @Override
            public void run() throws TTransportException {
                TTransport transport = new TSocket("localhost", ThriftConsumerConcurrentTest.THRIFT_SYNC_REQUEST_TEST_PORT);
                transport.open();
                TProtocol protocol = new TBinaryProtocol(new org.apache.thrift.transport.TFramedTransport(transport));
                Calculator.Client client = new Calculator.Client.Factory().getClient(protocol);
                int instanceId = ThriftConsumerConcurrentTest.createId();
                int calculateResponse = 0;
                try {
                    calculateResponse = client.calculate(1, new Work(instanceId, ThriftConsumerConcurrentTest.THRIFT_TEST_NUM1, Operation.MULTIPLY));
                } catch (TException e) {
                    ThriftConsumerConcurrentTest.LOG.info("Exception", e);
                }
                assertNotNull(("instanceId = " + instanceId), calculateResponse);
                assertEquals((instanceId * (ThriftConsumerConcurrentTest.THRIFT_TEST_NUM1)), calculateResponse);
                transport.close();
            }
        };
        new com.googlecode.junittoolbox.MultithreadingTester().add(ra).numThreads(ThriftConsumerConcurrentTest.CONCURRENT_THREAD_COUNT).numRoundsPerThread(ThriftConsumerConcurrentTest.ROUNDS_PER_THREAD_COUNT).run();
    }

    @Test
    public void testAsyncWithConcurrentThreads() throws Exception {
        RunnableAssert ra = new RunnableAssert("testAsyncWithConcurrentThreads") {
            @Override
            public void run() throws IOException, InterruptedException, TTransportException {
                final CountDownLatch latch = new CountDownLatch(1);
                TNonblockingTransport transport = new TNonblockingSocket("localhost", ThriftConsumerConcurrentTest.THRIFT_ASYNC_REQUEST_TEST_PORT);
                Calculator.AsyncClient client = new Calculator.AsyncClient.Factory(new TAsyncClientManager(), new TBinaryProtocol.Factory()).getAsyncClient(transport);
                int instanceId = ThriftConsumerConcurrentTest.createId();
                ThriftConsumerConcurrentTest.CalculateAsyncMethodCallback calculateCallback = new ThriftConsumerConcurrentTest.CalculateAsyncMethodCallback(latch);
                try {
                    calculate(1, new Work(instanceId, ThriftConsumerConcurrentTest.THRIFT_TEST_NUM1, Operation.MULTIPLY), calculateCallback);
                } catch (TException e) {
                    ThriftConsumerConcurrentTest.LOG.info("Exception", e);
                }
                latch.await(5, TimeUnit.SECONDS);
                int calculateResponse = calculateCallback.getCalculateResponse();
                assertNotNull(("instanceId = " + instanceId), calculateResponse);
                assertEquals((instanceId * (ThriftConsumerConcurrentTest.THRIFT_TEST_NUM1)), calculateResponse);
                transport.close();
            }
        };
        new com.googlecode.junittoolbox.MultithreadingTester().add(ra).numThreads(ThriftConsumerConcurrentTest.CONCURRENT_THREAD_COUNT).numRoundsPerThread(ThriftConsumerConcurrentTest.ROUNDS_PER_THREAD_COUNT).run();
    }

    public class CalculateAsyncMethodCallback implements AsyncMethodCallback<Integer> {
        private final CountDownLatch latch;

        private Integer calculateResponse;

        public CalculateAsyncMethodCallback(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onComplete(Integer response) {
            calculateResponse = response;
            latch.countDown();
        }

        @Override
        public void onError(Exception exception) {
            ThriftConsumerConcurrentTest.LOG.info("Exception", exception);
            latch.countDown();
        }

        public Integer getCalculateResponse() {
            return calculateResponse;
        }
    }

    public class CalculatorMessageBuilder {
        public Integer multiply(Work work) {
            return (work.num1) * (work.num2);
        }
    }
}

