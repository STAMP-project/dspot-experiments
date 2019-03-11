/**
 * Copyright 2017 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.kafka.cruisecontrol.monitor.sampling;


import MetricSampler.Samples;
import MetricSampler.SamplingMode.ALL;
import com.linkedin.kafka.cruisecontrol.common.TestConstants;
import com.linkedin.kafka.cruisecontrol.metricsreporter.exception.UnknownVersionException;
import com.linkedin.kafka.cruisecontrol.metricsreporter.metric.CruiseControlMetric;
import com.linkedin.kafka.cruisecontrol.metricsreporter.metric.PartitionMetric;
import com.linkedin.kafka.cruisecontrol.metricsreporter.metric.RawMetricType;
import com.linkedin.kafka.cruisecontrol.metricsreporter.metric.TopicMetric;
import com.linkedin.kafka.cruisecontrol.model.ModelUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.common.utils.Time;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for CruiseControlMetricsProcessor
 */
public class CruiseControlMetricsProcessorTest {
    private static final int BYTES_IN_KB = 1024;

    private static final int BYTES_IN_MB = 1024 * 1024;

    private static final int P0 = 0;

    private static final int P1 = 1;

    private static final TopicPartition T1P0 = new TopicPartition(TestConstants.TOPIC1, CruiseControlMetricsProcessorTest.P0);

    private static final TopicPartition T1P1 = new TopicPartition(TestConstants.TOPIC1, CruiseControlMetricsProcessorTest.P1);

    private static final TopicPartition T2P0 = new TopicPartition(TestConstants.TOPIC2, CruiseControlMetricsProcessorTest.P0);

    private static final TopicPartition T2P1 = new TopicPartition(TestConstants.TOPIC2, CruiseControlMetricsProcessorTest.P1);

    private static final int BROKER_ID_0 = 0;

    private static final int BROKER_ID_1 = 1;

    private static final double DELTA = 0.001;

    private static final double B0_CPU = 50.0;

    private static final double B1_CPU = 30.0;

    private static final double B0_ALL_TOPIC_BYTES_IN = 820.0;

    private static final double B1_ALL_TOPIC_BYTES_IN = 500.0;

    private static final double B0_ALL_TOPIC_BYTES_OUT = 1380.0;

    private static final double B1_ALL_TOPIC_BYTES_OUT = 500.0;

    private static final double B0_TOPIC1_BYTES_IN = 20.0;

    private static final double B1_TOPIC1_BYTES_IN = 500.0;

    private static final double B0_TOPIC2_BYTES_IN = 800.0;

    private static final double B0_TOPIC1_BYTES_OUT = 80.0;

    private static final double B1_TOPIC1_BYTES_OUT = 500.0;

    private static final double B0_TOPIC2_BYTES_OUT = 1300.0;

    private static final double B1_TOPIC1_REPLICATION_BYTES_IN = 20.0;

    private static final double B0_TOPIC1_REPLICATION_BYTES_IN = 500.0;

    private static final double B1_TOPIC2_REPLICATION_BYTES_IN = 800.0;

    private static final double B0_TOPIC1_REPLICATION_BYTES_OUT = 20.0;

    private static final double B1_TOPIC1_REPLICATION_BYTES_OUT = 500.0;

    private static final double B0_TOPIC2_REPLICATION_BYTES_OUT = 800.0;

    private static final double T1P0_BYTES_SIZE = 100.0;

    private static final double T1P1_BYTES_SIZE = 300.0;

    private static final double T2P0_BYTES_SIZE = 200.0;

    private static final double T2P1_BYTES_SIZE = 500.0;

    private static final Map<TopicPartition, Double> CPU_UTIL = new HashMap<>();

    static {
        CruiseControlMetricsProcessorTest.CPU_UTIL.put(CruiseControlMetricsProcessorTest.T1P0, ModelUtils.estimateLeaderCpuUtil(CruiseControlMetricsProcessorTest.B0_CPU, CruiseControlMetricsProcessorTest.B0_ALL_TOPIC_BYTES_IN, (((CruiseControlMetricsProcessorTest.B0_ALL_TOPIC_BYTES_OUT) + (CruiseControlMetricsProcessorTest.B0_TOPIC1_REPLICATION_BYTES_OUT)) + (CruiseControlMetricsProcessorTest.B0_TOPIC2_REPLICATION_BYTES_OUT)), CruiseControlMetricsProcessorTest.B0_TOPIC1_REPLICATION_BYTES_IN, CruiseControlMetricsProcessorTest.B0_TOPIC1_BYTES_IN, ((CruiseControlMetricsProcessorTest.B0_TOPIC1_BYTES_OUT) + (CruiseControlMetricsProcessorTest.B0_TOPIC1_REPLICATION_BYTES_OUT))));
        CruiseControlMetricsProcessorTest.CPU_UTIL.put(CruiseControlMetricsProcessorTest.T1P1, ModelUtils.estimateLeaderCpuUtil(CruiseControlMetricsProcessorTest.B1_CPU, CruiseControlMetricsProcessorTest.B1_ALL_TOPIC_BYTES_IN, ((CruiseControlMetricsProcessorTest.B1_ALL_TOPIC_BYTES_OUT) + (CruiseControlMetricsProcessorTest.B1_TOPIC1_REPLICATION_BYTES_OUT)), ((CruiseControlMetricsProcessorTest.B1_TOPIC1_REPLICATION_BYTES_IN) + (CruiseControlMetricsProcessorTest.B1_TOPIC2_REPLICATION_BYTES_IN)), CruiseControlMetricsProcessorTest.B1_TOPIC1_BYTES_IN, ((CruiseControlMetricsProcessorTest.B1_TOPIC1_BYTES_OUT) + (CruiseControlMetricsProcessorTest.B1_TOPIC1_REPLICATION_BYTES_OUT))));
        CruiseControlMetricsProcessorTest.CPU_UTIL.put(CruiseControlMetricsProcessorTest.T2P0, ModelUtils.estimateLeaderCpuUtil(CruiseControlMetricsProcessorTest.B0_CPU, CruiseControlMetricsProcessorTest.B0_ALL_TOPIC_BYTES_IN, (((CruiseControlMetricsProcessorTest.B0_ALL_TOPIC_BYTES_OUT) + (CruiseControlMetricsProcessorTest.B0_TOPIC1_REPLICATION_BYTES_OUT)) + (CruiseControlMetricsProcessorTest.B0_TOPIC2_REPLICATION_BYTES_OUT)), CruiseControlMetricsProcessorTest.B0_TOPIC1_REPLICATION_BYTES_IN, ((CruiseControlMetricsProcessorTest.B0_TOPIC2_BYTES_IN) / 2), (((CruiseControlMetricsProcessorTest.B0_TOPIC2_BYTES_OUT) + (CruiseControlMetricsProcessorTest.B0_TOPIC2_REPLICATION_BYTES_OUT)) / 2)));
        CruiseControlMetricsProcessorTest.CPU_UTIL.put(CruiseControlMetricsProcessorTest.T2P1, ModelUtils.estimateLeaderCpuUtil(CruiseControlMetricsProcessorTest.B0_CPU, CruiseControlMetricsProcessorTest.B0_ALL_TOPIC_BYTES_IN, (((CruiseControlMetricsProcessorTest.B0_ALL_TOPIC_BYTES_OUT) + (CruiseControlMetricsProcessorTest.B0_TOPIC1_REPLICATION_BYTES_OUT)) + (CruiseControlMetricsProcessorTest.B0_TOPIC2_REPLICATION_BYTES_OUT)), CruiseControlMetricsProcessorTest.B0_TOPIC1_REPLICATION_BYTES_IN, ((CruiseControlMetricsProcessorTest.B0_TOPIC2_BYTES_IN) / 2), (((CruiseControlMetricsProcessorTest.B0_TOPIC2_BYTES_OUT) + (CruiseControlMetricsProcessorTest.B0_TOPIC2_REPLICATION_BYTES_OUT)) / 2)));
    }

    private final Time _time = new MockTime(0, 100L, TimeUnit.NANOSECONDS.convert(100L, TimeUnit.MILLISECONDS));

    @Test
    public void testBasic() throws UnknownVersionException {
        CruiseControlMetricsProcessor processor = new CruiseControlMetricsProcessor();
        Set<CruiseControlMetric> metrics = getCruiseControlMetrics();
        metrics.forEach(processor::addMetric);
        MetricSampler.Samples samples = processor.process(getCluster(), Arrays.asList(CruiseControlMetricsProcessorTest.T1P0, CruiseControlMetricsProcessorTest.T1P1, CruiseControlMetricsProcessorTest.T2P0, CruiseControlMetricsProcessorTest.T2P1), ALL);
        Assert.assertEquals(4, samples.partitionMetricSamples().size());
        Assert.assertEquals(2, samples.brokerMetricSamples().size());
        for (PartitionMetricSample sample : samples.partitionMetricSamples()) {
            if (sample.entity().tp().equals(CruiseControlMetricsProcessorTest.T1P0)) {
                validatePartitionMetricSample(sample, ((_time.milliseconds()) + 2), CruiseControlMetricsProcessorTest.CPU_UTIL.get(CruiseControlMetricsProcessorTest.T1P0), CruiseControlMetricsProcessorTest.B0_TOPIC1_BYTES_IN, CruiseControlMetricsProcessorTest.B0_TOPIC1_BYTES_OUT, CruiseControlMetricsProcessorTest.T1P0_BYTES_SIZE);
            } else
                if (sample.entity().tp().equals(CruiseControlMetricsProcessorTest.T1P1)) {
                    validatePartitionMetricSample(sample, ((_time.milliseconds()) + 2), CruiseControlMetricsProcessorTest.CPU_UTIL.get(CruiseControlMetricsProcessorTest.T1P1), CruiseControlMetricsProcessorTest.B1_TOPIC1_BYTES_IN, CruiseControlMetricsProcessorTest.B1_TOPIC1_BYTES_OUT, CruiseControlMetricsProcessorTest.T1P1_BYTES_SIZE);
                } else
                    if (sample.entity().tp().equals(CruiseControlMetricsProcessorTest.T2P0)) {
                        validatePartitionMetricSample(sample, ((_time.milliseconds()) + 2), CruiseControlMetricsProcessorTest.CPU_UTIL.get(CruiseControlMetricsProcessorTest.T2P0), ((CruiseControlMetricsProcessorTest.B0_TOPIC2_BYTES_IN) / 2), ((CruiseControlMetricsProcessorTest.B0_TOPIC2_BYTES_OUT) / 2), CruiseControlMetricsProcessorTest.T2P0_BYTES_SIZE);
                    } else
                        if (sample.entity().tp().equals(CruiseControlMetricsProcessorTest.T2P1)) {
                            validatePartitionMetricSample(sample, ((_time.milliseconds()) + 2), CruiseControlMetricsProcessorTest.CPU_UTIL.get(CruiseControlMetricsProcessorTest.T2P1), ((CruiseControlMetricsProcessorTest.B0_TOPIC2_BYTES_IN) / 2), ((CruiseControlMetricsProcessorTest.B0_TOPIC2_BYTES_OUT) / 2), CruiseControlMetricsProcessorTest.T2P1_BYTES_SIZE);
                        } else {
                            Assert.fail(("Should never have partition " + (sample.entity().tp())));
                        }



        }
        for (BrokerMetricSample sample : samples.brokerMetricSamples()) {
            if ((sample.metricValue(CPU_USAGE)) == (CruiseControlMetricsProcessorTest.B0_CPU)) {
                Assert.assertEquals(CruiseControlMetricsProcessorTest.B0_TOPIC1_REPLICATION_BYTES_IN, sample.metricValue(REPLICATION_BYTES_IN_RATE), CruiseControlMetricsProcessorTest.DELTA);
            } else
                if ((sample.metricValue(CPU_USAGE)) == (CruiseControlMetricsProcessorTest.B1_CPU)) {
                    Assert.assertEquals(((CruiseControlMetricsProcessorTest.B1_TOPIC1_REPLICATION_BYTES_IN) + (CruiseControlMetricsProcessorTest.B1_TOPIC2_REPLICATION_BYTES_IN)), sample.metricValue(REPLICATION_BYTES_IN_RATE), CruiseControlMetricsProcessorTest.DELTA);
                } else {
                    Assert.fail(("Should never have broker cpu util " + (sample.metricValue(CPU_USAGE))));
                }

        }
        Assert.assertTrue((!(samples.partitionMetricSamples().isEmpty())));
    }

    @Test
    public void testMissingBrokerCpuUtilization() throws UnknownVersionException {
        CruiseControlMetricsProcessor processor = new CruiseControlMetricsProcessor();
        Set<CruiseControlMetric> metrics = getCruiseControlMetrics();
        for (CruiseControlMetric metric : metrics) {
            if (((metric.rawMetricType()) == (RawMetricType.BROKER_CPU_UTIL)) && ((metric.brokerId()) == (CruiseControlMetricsProcessorTest.BROKER_ID_0))) {
                // Do nothing and skip the metric.
            } else {
                processor.addMetric(metric);
            }
        }
        MetricSampler.Samples samples = processor.process(getCluster(), Arrays.asList(CruiseControlMetricsProcessorTest.T1P0, CruiseControlMetricsProcessorTest.T1P1, CruiseControlMetricsProcessorTest.T2P0, CruiseControlMetricsProcessorTest.T2P1), ALL);
        Assert.assertEquals("Should have ignored partitions on broker 0", 1, samples.partitionMetricSamples().size());
        Assert.assertEquals("Should have ignored broker 0", 1, samples.brokerMetricSamples().size());
    }

    @Test
    public void testMissingOtherBrokerMetrics() throws UnknownVersionException {
        CruiseControlMetricsProcessor processor = new CruiseControlMetricsProcessor();
        Set<CruiseControlMetric> metrics = getCruiseControlMetrics();
        for (CruiseControlMetric metric : metrics) {
            if (((metric.rawMetricType()) == (RawMetricType.BROKER_CONSUMER_FETCH_LOCAL_TIME_MS_MAX)) && ((metric.brokerId()) == (CruiseControlMetricsProcessorTest.BROKER_ID_0))) {
                // Do nothing and skip the metric.
            } else {
                processor.addMetric(metric);
            }
        }
        MetricSampler.Samples samples = processor.process(getCluster(), Arrays.asList(CruiseControlMetricsProcessorTest.T1P0, CruiseControlMetricsProcessorTest.T1P1, CruiseControlMetricsProcessorTest.T2P0, CruiseControlMetricsProcessorTest.T2P1), ALL);
        Assert.assertEquals("Should have all 4 partition metrics.", 4, samples.partitionMetricSamples().size());
        Assert.assertEquals("Should have ignored broker 0", 1, samples.brokerMetricSamples().size());
    }

    @Test
    public void testMissingPartitionSizeMetric() throws UnknownVersionException {
        CruiseControlMetricsProcessor processor = new CruiseControlMetricsProcessor();
        Set<CruiseControlMetric> metrics = getCruiseControlMetrics();
        for (CruiseControlMetric metric : metrics) {
            boolean shouldAdd = true;
            if ((metric.rawMetricType()) == (RawMetricType.PARTITION_SIZE)) {
                PartitionMetric pm = ((PartitionMetric) (metric));
                if ((pm.topic().equals(TestConstants.TOPIC1)) && ((pm.partition()) == (CruiseControlMetricsProcessorTest.P0))) {
                    shouldAdd = false;
                }
            }
            if (shouldAdd) {
                processor.addMetric(metric);
            }
        }
        MetricSampler.Samples samples = processor.process(getCluster(), Arrays.asList(CruiseControlMetricsProcessorTest.T1P0, CruiseControlMetricsProcessorTest.T1P1, CruiseControlMetricsProcessorTest.T2P0, CruiseControlMetricsProcessorTest.T2P1), ALL);
        Assert.assertEquals(("Should have ignored partition " + (CruiseControlMetricsProcessorTest.T1P0)), 3, samples.partitionMetricSamples().size());
        Assert.assertEquals("Should have reported both brokers", 2, samples.brokerMetricSamples().size());
    }

    @Test
    public void testMissingTopicBytesInMetric() throws UnknownVersionException {
        CruiseControlMetricsProcessor processor = new CruiseControlMetricsProcessor();
        Set<CruiseControlMetric> metrics = getCruiseControlMetrics();
        Set<RawMetricType> metricTypeToExclude = new java.util.HashSet(Arrays.asList(TOPIC_BYTES_IN, TOPIC_BYTES_OUT, TOPIC_REPLICATION_BYTES_IN, TOPIC_REPLICATION_BYTES_OUT));
        for (CruiseControlMetric metric : metrics) {
            if (metricTypeToExclude.contains(metric.rawMetricType())) {
                TopicMetric tm = ((TopicMetric) (metric));
                if (((tm.brokerId()) == (CruiseControlMetricsProcessorTest.BROKER_ID_0)) && (tm.topic().equals(TestConstants.TOPIC1))) {
                    continue;
                }
            }
            processor.addMetric(metric);
        }
        MetricSampler.Samples samples = processor.process(getCluster(), Arrays.asList(CruiseControlMetricsProcessorTest.T1P0, CruiseControlMetricsProcessorTest.T1P1, CruiseControlMetricsProcessorTest.T2P0, CruiseControlMetricsProcessorTest.T2P1), ALL);
        Assert.assertEquals(4, samples.partitionMetricSamples().size());
        Assert.assertEquals(2, samples.brokerMetricSamples().size());
        for (PartitionMetricSample sample : samples.partitionMetricSamples()) {
            if (sample.entity().tp().equals(CruiseControlMetricsProcessorTest.T1P0)) {
                // T1P0 should not have any IO or CPU usage.
                validatePartitionMetricSample(sample, ((_time.milliseconds()) + 2), 0.0, 0.0, 0.0, CruiseControlMetricsProcessorTest.T1P0_BYTES_SIZE);
            }
        }
    }
}

