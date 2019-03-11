package azkaban.metric;


import azkaban.metric.inmemoryemitter.InMemoryHistoryNode;
import azkaban.metric.inmemoryemitter.InMemoryMetricEmitter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;


/**
 * Azkaban Metric Manager Tests
 */
public class MetricManagerTest {
    MetricReportManager manager;

    FakeMetric metric;

    InMemoryMetricEmitter emitter;

    MetricManagerTest.MetricEmitterWrapper emitterWrapper;

    /**
     * Test enable disable and status methods
     */
    @Test
    public void managerStatusTest() {
        Assert.assertNotNull("Singleton Failed to instantiate", this.manager);
        Assert.assertTrue("Failed to enable metric manager", MetricReportManager.isAvailable());
        this.manager.disableManager();
        Assert.assertFalse("Failed to disable metric manager", MetricReportManager.isAvailable());
        this.manager.enableManager();
        Assert.assertTrue("Failed to enable metric manager", MetricReportManager.isAvailable());
    }

    /**
     * Test adding and accessing metric methods
     */
    @Test
    public void managerMetricMaintenanceTest() {
        Assert.assertEquals("Failed to add metric", this.manager.getAllMetrics().size(), 1);
        Assert.assertTrue("Failed to add metric", this.manager.getAllMetrics().contains(this.metric));
        Assert.assertEquals("Failed to get metric by Name", this.manager.getMetricFromName("FakeMetric"), this.metric);
    }

    /**
     * Test adding, removing and accessing metric emitter.
     */
    @Test
    public void managerEmitterMaintenanceTest() {
        Assert.assertTrue("Failed to add Emitter", this.manager.getMetricEmitters().contains(this.emitterWrapper));
        final int originalSize = this.manager.getMetricEmitters().size();
        this.manager.removeMetricEmitter(this.emitterWrapper);
        Assert.assertEquals("Failed to remove emitter", this.manager.getMetricEmitters().size(), (originalSize - 1));
        this.manager.addMetricEmitter(this.emitterWrapper);
    }

    /**
     * Test metric reporting methods, including InMemoryMetricEmitter methods
     */
    @Test
    public void managerEmitterHandlingTest() throws Exception {
        // metrics use System.currentTimeMillis, so that method should be the millis provider
        final DateTime aboutNow = new DateTime(System.currentTimeMillis());
        this.emitter.purgeAllData();
        final Date from = aboutNow.minusMinutes(1).toDate();
        notifyManager();
        this.emitterWrapper.countDownLatch.await(10L, TimeUnit.SECONDS);
        final Date to = aboutNow.plusMinutes(1).toDate();
        final List<InMemoryHistoryNode> nodes = this.emitter.getMetrics("FakeMetric", from, to, false);
        Assert.assertEquals("Failed to report metric", 1, nodes.size());
        Assert.assertEquals("Failed to report metric", nodes.get(0).getValue(), 4);
    }

    private class MetricEmitterWrapper implements IMetricEmitter {
        private final CountDownLatch countDownLatch = new CountDownLatch(1);

        @Override
        public void reportMetric(final IMetric<?> metric) throws MetricException {
            MetricManagerTest.this.emitter.reportMetric(metric);
            this.countDownLatch.countDown();
        }

        @Override
        public void purgeAllData() throws MetricException {
            MetricManagerTest.this.emitter.purgeAllData();
        }
    }
}

