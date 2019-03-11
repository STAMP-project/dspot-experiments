/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.master.metrics;


import HeartbeatContext.MASTER_CLUSTER_METRICS_UPDATER;
import MetricsSystem.InstanceType;
import alluxio.clock.ManualClock;
import alluxio.heartbeat.HeartbeatContext;
import alluxio.heartbeat.HeartbeatScheduler;
import alluxio.heartbeat.ManuallyScheduleHeartbeat;
import alluxio.master.MasterRegistry;
import alluxio.metrics.Metric;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


/**
 * Unit tests for {@link MetricsMaster}.
 */
public class MetricsMasterTest {
    @ClassRule
    public static ManuallyScheduleHeartbeat sManuallyScheduleRule = new ManuallyScheduleHeartbeat(HeartbeatContext.MASTER_CLUSTER_METRICS_UPDATER);

    private DefaultMetricsMaster mMetricsMaster;

    private MasterRegistry mRegistry;

    private ManualClock mClock;

    private ExecutorService mExecutorService;

    @Test
    public void testAggregator() {
        mMetricsMaster.addAggregator(new alluxio.metrics.aggregator.SumInstancesAggregator("metricA", InstanceType.WORKER, "metricA"));
        mMetricsMaster.addAggregator(new alluxio.metrics.aggregator.SumInstancesAggregator("metricB", InstanceType.WORKER, "metricB"));
        List<Metric> metrics1 = Lists.newArrayList(Metric.from("worker.192_1_1_1.metricA", 10), Metric.from("worker.192_1_1_1.metricB", 20));
        mMetricsMaster.workerHeartbeat("192_1_1_1", metrics1);
        List<Metric> metrics2 = Lists.newArrayList(Metric.from("worker.192_1_1_2.metricA", 1), Metric.from("worker.192_1_1_2.metricB", 2));
        mMetricsMaster.workerHeartbeat("192_1_1_2", metrics2);
        Assert.assertEquals(11L, getGauge("metricA"));
        Assert.assertEquals(22L, getGauge("metricB"));
        // override metrics from hostname 192_1_1_2
        List<Metric> metrics3 = Lists.newArrayList(Metric.from("worker.192_1_1_2.metricA", 3));
        mMetricsMaster.workerHeartbeat("192_1_1_2", metrics3);
        Assert.assertEquals(13L, getGauge("metricA"));
        Assert.assertEquals(20L, getGauge("metricB"));
    }

    @Test
    public void testMultiValueAggregator() throws Exception {
        mMetricsMaster.addAggregator(new alluxio.metrics.aggregator.SingleTagValueAggregator("metric", InstanceType.WORKER, "metric", "tag"));
        List<Metric> metrics1 = Lists.newArrayList(Metric.from("worker.192_1_1_1.metric.tag:v1", 10), Metric.from("worker.192_1_1_1.metric.tag:v2", 20));
        mMetricsMaster.workerHeartbeat("192_1_1_1", metrics1);
        List<Metric> metrics2 = Lists.newArrayList(Metric.from("worker.192_1_1_2.metric.tag:v1", 1), Metric.from("worker.192_1_1_2.metric.tag:v2", 2));
        mMetricsMaster.workerHeartbeat("192_1_1_2", metrics2);
        HeartbeatScheduler.execute(MASTER_CLUSTER_METRICS_UPDATER);
        Assert.assertEquals(11L, getGauge("metric", "tag", "v1"));
        Assert.assertEquals(22L, getGauge("metric", "tag", "v2"));
    }

    @Test
    public void testClientHeartbeat() {
        mMetricsMaster.addAggregator(new alluxio.metrics.aggregator.SumInstancesAggregator("metric1", InstanceType.CLIENT, "metric1"));
        mMetricsMaster.addAggregator(new alluxio.metrics.aggregator.SumInstancesAggregator("metric2", InstanceType.CLIENT, "metric2"));
        List<Metric> metrics1 = Lists.newArrayList(Metric.from("client.192_1_1_1:A.metric1", 10), Metric.from("client.192_1_1_1:A.metric2", 20));
        mMetricsMaster.clientHeartbeat("A", "192.1.1.1", metrics1);
        List<Metric> metrics2 = Lists.newArrayList(Metric.from("client.192_1_1_1:B.metric1", 15), Metric.from("client.192_1_1_1:B.metric2", 25));
        mMetricsMaster.clientHeartbeat("B", "192.1.1.1", metrics2);
        List<Metric> metrics3 = Lists.newArrayList(Metric.from("client.192_1_1_2:C.metric1", 1), Metric.from("client.192_1_1_2:C.metric2", 2));
        mMetricsMaster.clientHeartbeat("C", "192.1.1.2", metrics3);
        Assert.assertEquals(26L, getGauge("metric1"));
        Assert.assertEquals(47L, getGauge("metric2"));
    }
}

