package com.zendesk.maxwell;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.zendesk.maxwell.producer.AbstractProducer;
import com.zendesk.maxwell.producer.ProducerFactory;
import com.zendesk.maxwell.row.RowMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Geoff Lywood (geoff@addepar.com)
 */
public class EmbeddedMaxwellTest extends MaxwellTestWithIsolatedServer {
    @Test
    public void testCustomMetricsAndProducer() throws Exception {
        MaxwellConfig config = getConfig(MaxwellTestWithIsolatedServer.server);
        MetricRegistry metrics = new MetricRegistry();
        HealthCheckRegistry healthChecks = new HealthCheckRegistry();
        final BlockingQueue<RowMap> rowBuffer = new LinkedBlockingQueue<>();
        config.metricsReportingType = "embedded";
        config.metricRegistry = metrics;
        config.healthCheckRegistry = healthChecks;
        config.metricsPrefix = "prefix";
        config.producerFactory = new ProducerFactory() {
            @Override
            public AbstractProducer createProducer(MaxwellContext context) {
                return new EmbeddedMaxwellTest.EmbeddedTestProducer(context, rowBuffer);
            }
        };
        final CountDownLatch latch = new CountDownLatch(1);
        Maxwell maxwell = new Maxwell(config) {
            @Override
            protected void onReplicatorStart() {
                latch.countDown();
            }
        };
        new Thread(maxwell).start();
        latch.await();
        MaxwellTestWithIsolatedServer.server.execute("insert into minimal set account_id = 1, text_field='hello'");
        RowMap rowMap = rowBuffer.poll(10, TimeUnit.SECONDS);
        maxwell.terminate();
        Exception maxwellError = maxwell.context.getError();
        if (maxwellError != null) {
            throw maxwellError;
        }
        Assert.assertThat(rowMap, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertTrue(((metrics.getCounters().get("prefix.row.count").getCount()) > 0));
    }

    private static class EmbeddedTestProducer extends AbstractProducer {
        private BlockingQueue<RowMap> rowBuffer;

        EmbeddedTestProducer(MaxwellContext context, BlockingQueue<RowMap> rowBuffer) {
            super(context);
            this.rowBuffer = rowBuffer;
        }

        @Override
        public void push(RowMap r) throws Exception {
            rowBuffer.put(r);
        }
    }
}

