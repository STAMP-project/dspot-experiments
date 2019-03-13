package com.netflix.hystrix.contrib.codahalemetricspublisher;


import HystrixCommandKey.Factory;
import com.codahale.metrics.MetricRegistry;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class HystrixCodaHaleMetricsPublisherCommandTest {
    private final MetricRegistry metricRegistry = new MetricRegistry();

    @Test
    public void testCommandSuccess() throws InterruptedException {
        HystrixCodaHaleMetricsPublisherCommandTest.Command command = new HystrixCodaHaleMetricsPublisherCommandTest.Command();
        execute();
        Thread.sleep(1000);
        Assert.assertThat(((Long) (metricRegistry.getGauges().get("hystrix.testGroup.testCommand.countSuccess").getValue())), Is.is(1L));
        Assert.assertThat(((Long) (metricRegistry.getGauges().get("hystrix.HystrixThreadPool.threadGroup.totalTaskCount").getValue())), Is.is(1L));
    }

    private static class Command extends HystrixCommand<Void> {
        static final HystrixCommandKey hystrixCommandKey = Factory.asKey("testCommand");

        static final HystrixCommandGroupKey hystrixCommandGroupKey = HystrixCommandGroupKey.Factory.asKey("testGroup");

        static final HystrixThreadPoolKey hystrixThreadPool = HystrixThreadPoolKey.Factory.asKey("threadGroup");

        Command() {
            super(Setter.withGroupKey(HystrixCodaHaleMetricsPublisherCommandTest.Command.hystrixCommandGroupKey).andCommandKey(HystrixCodaHaleMetricsPublisherCommandTest.Command.hystrixCommandKey).andThreadPoolKey(HystrixCodaHaleMetricsPublisherCommandTest.Command.hystrixThreadPool));
        }

        @Override
        protected Void run() throws Exception {
            return null;
        }
    }
}

