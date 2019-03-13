/**
 * Copyright 2017 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.kafka.cruisecontrol.monitor.task;


import com.codahale.metrics.MetricRegistry;
import com.linkedin.cruisecontrol.metricdef.MetricDef;
import com.linkedin.cruisecontrol.metricdef.MetricInfo;
import com.linkedin.kafka.clients.utils.tests.AbstractKafkaIntegrationTestHarness;
import com.linkedin.kafka.cruisecontrol.common.MetadataClient;
import com.linkedin.kafka.cruisecontrol.config.KafkaCruiseControlConfig;
import com.linkedin.kafka.cruisecontrol.exception.MetricSamplingException;
import com.linkedin.kafka.cruisecontrol.monitor.metricdefinition.KafkaMetricDef;
import com.linkedin.kafka.cruisecontrol.monitor.sampling.MetricFetcherManager;
import com.linkedin.kafka.cruisecontrol.monitor.sampling.MetricSampler;
import com.linkedin.kafka.cruisecontrol.monitor.sampling.PartitionMetricSample;
import com.linkedin.kafka.cruisecontrol.monitor.sampling.aggregator.KafkaBrokerMetricSampleAggregator;
import com.linkedin.kafka.cruisecontrol.monitor.sampling.aggregator.KafkaPartitionMetricSampleAggregator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.Metadata;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.Time;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


/**
 * The unit test for metric fetcher manager.
 * <p>
 * TODO: We copy the test harness code from likafka-clients code. This should be removed after likafka-clients is open sourced.
 */
public class LoadMonitorTaskRunnerTest extends AbstractKafkaIntegrationTestHarness {
    private static final long WINDOW_MS = 10000L;

    private static final int NUM_WINDOWS = 5;

    private static final int NUM_TOPICS = 100;

    private static final int NUM_PARTITIONS = 4;

    private static final int NUM_METRIC_FETCHERS = 4;

    private static final long SAMPLING_INTERVAL = 100000L;

    private static final MetricDef METRIC_DEF = KafkaMetricDef.commonMetricDef();

    // Using autoTick = 1
    private static final Time TIME = new LoadMonitorTaskRunnerTest.MockTime(1L);

    @Test
    public void testSimpleFetch() throws InterruptedException {
        KafkaCruiseControlConfig config = new KafkaCruiseControlConfig(getLoadMonitorProperties());
        Metadata metadata = new Metadata(10, 10, false);
        MetadataClient metadataClient = new MetadataClient(config, metadata, (-1L), LoadMonitorTaskRunnerTest.TIME);
        LoadMonitorTaskRunnerTest.MockPartitionMetricSampleAggregator mockPartitionMetricSampleAggregator = new LoadMonitorTaskRunnerTest.MockPartitionMetricSampleAggregator(config, metadata);
        KafkaBrokerMetricSampleAggregator mockBrokerMetricSampleAggregator = EasyMock.mock(KafkaBrokerMetricSampleAggregator.class);
        List<MetricSampler> samplers = new ArrayList<>();
        MetricRegistry dropwizardMetricRegistry = new MetricRegistry();
        for (int i = 0; i < (LoadMonitorTaskRunnerTest.NUM_METRIC_FETCHERS); i++) {
            samplers.add(new LoadMonitorTaskRunnerTest.MockSampler(0));
        }
        MetricFetcherManager fetcherManager = new MetricFetcherManager(config, mockPartitionMetricSampleAggregator, mockBrokerMetricSampleAggregator, metadataClient, LoadMonitorTaskRunnerTest.METRIC_DEF, LoadMonitorTaskRunnerTest.TIME, dropwizardMetricRegistry, samplers);
        LoadMonitorTaskRunner loadMonitorTaskRunner = new LoadMonitorTaskRunner(config, fetcherManager, mockPartitionMetricSampleAggregator, mockBrokerMetricSampleAggregator, metadataClient, LoadMonitorTaskRunnerTest.TIME);
        while ((metadata.fetch().topics().size()) < (LoadMonitorTaskRunnerTest.NUM_TOPICS)) {
            Thread.sleep(10);
            metadataClient.refreshMetadata();
        } 
        loadMonitorTaskRunner.start(true);
        Set<TopicPartition> partitionsToSample = new HashSet<>(((LoadMonitorTaskRunnerTest.NUM_TOPICS) * (LoadMonitorTaskRunnerTest.NUM_PARTITIONS)));
        for (int i = 0; i < (LoadMonitorTaskRunnerTest.NUM_TOPICS); i++) {
            for (int j = 0; j < (LoadMonitorTaskRunnerTest.NUM_PARTITIONS); j++) {
                partitionsToSample.add(new TopicPartition(("topic-" + i), j));
            }
        }
        long startMs = System.currentTimeMillis();
        BlockingQueue<PartitionMetricSample> sampleQueue = mockPartitionMetricSampleAggregator.metricSampleQueue();
        while ((!(partitionsToSample.isEmpty())) && ((System.currentTimeMillis()) < (startMs + 10000))) {
            PartitionMetricSample sample = sampleQueue.poll();
            if (sample != null) {
                Assert.assertTrue("The topic partition should have been sampled and sampled only once.", partitionsToSample.contains(sample.entity().tp()));
                partitionsToSample.remove(sample.entity().tp());
            }
        } 
        Assert.assertTrue(("Did not see sample for partitions " + (Arrays.toString(partitionsToSample.toArray()))), partitionsToSample.isEmpty());
        fetcherManager.shutdown();
        Assert.assertTrue(sampleQueue.isEmpty());
    }

    @Test
    public void testSamplingError() {
        KafkaCruiseControlConfig config = new KafkaCruiseControlConfig(getLoadMonitorProperties());
        Metadata metadata = new Metadata(10, 10, false);
        MetadataClient metadataClient = new MetadataClient(config, metadata, (-1L), LoadMonitorTaskRunnerTest.TIME);
        LoadMonitorTaskRunnerTest.MockPartitionMetricSampleAggregator mockMetricSampleAggregator = new LoadMonitorTaskRunnerTest.MockPartitionMetricSampleAggregator(config, metadata);
        KafkaBrokerMetricSampleAggregator mockBrokerMetricSampleAggregator = EasyMock.mock(KafkaBrokerMetricSampleAggregator.class);
        List<MetricSampler> samplers = new ArrayList<>();
        MetricRegistry dropwizardMetricRegistry = new MetricRegistry();
        for (int i = 0; i < (LoadMonitorTaskRunnerTest.NUM_METRIC_FETCHERS); i++) {
            samplers.add(new LoadMonitorTaskRunnerTest.MockSampler(i));
        }
        MetricFetcherManager fetcherManager = new MetricFetcherManager(config, mockMetricSampleAggregator, mockBrokerMetricSampleAggregator, metadataClient, LoadMonitorTaskRunnerTest.METRIC_DEF, LoadMonitorTaskRunnerTest.TIME, dropwizardMetricRegistry, samplers);
        LoadMonitorTaskRunner loadMonitorTaskRunner = new LoadMonitorTaskRunner(config, fetcherManager, mockMetricSampleAggregator, mockBrokerMetricSampleAggregator, metadataClient, LoadMonitorTaskRunnerTest.TIME);
        while ((metadata.fetch().topics().size()) < 100) {
            metadataClient.refreshMetadata();
        } 
        loadMonitorTaskRunner.start(true);
        int numSamples = 0;
        long startMs = System.currentTimeMillis();
        BlockingQueue<PartitionMetricSample> sampleQueue = mockMetricSampleAggregator.metricSampleQueue();
        while ((numSamples < (((LoadMonitorTaskRunnerTest.NUM_PARTITIONS) * (LoadMonitorTaskRunnerTest.NUM_TOPICS)) * 10)) && ((System.currentTimeMillis()) < (startMs + 10000))) {
            PartitionMetricSample sample = sampleQueue.poll();
            if (sample != null) {
                numSamples++;
            }
        } 
        // We should have NUM_METRIC_FETCHER rounds of sampling. The first round only has one metric fetcher returns
        // samples, two fetchers return samples for the second round, three for the third and four for the forth round.
        // So the first round only has 1/4 of the total samples, then 2/4, 3/4 and all the samples.
        int expectedNumSamples = 0;
        for (int i = 0; i < (LoadMonitorTaskRunnerTest.NUM_METRIC_FETCHERS); i++) {
            expectedNumSamples += (((LoadMonitorTaskRunnerTest.NUM_TOPICS) * (LoadMonitorTaskRunnerTest.NUM_PARTITIONS)) * (i + 1)) / (LoadMonitorTaskRunnerTest.NUM_METRIC_FETCHERS);
        }
        Assert.assertEquals((((("Only see " + numSamples) + " samples. Expecting ") + expectedNumSamples) + " samples"), expectedNumSamples, numSamples);
        fetcherManager.shutdown();
    }

    // A simple metric sampler that increment the mock time by 1
    private class MockSampler implements MetricSampler {
        private int _exceptionsLeft = 0;

        MockSampler(int numExceptions) {
            _exceptionsLeft = numExceptions;
        }

        @Override
        public Samples getSamples(Cluster cluster, Set<TopicPartition> assignedPartitions, long startTime, long endTime, SamplingMode mode, MetricDef metricDef) throws MetricSamplingException {
            if ((_exceptionsLeft) > 0) {
                (_exceptionsLeft)--;
                throw new MetricSamplingException("Error");
            }
            Set<PartitionMetricSample> partitionMetricSamples = new HashSet(assignedPartitions.size());
            for (TopicPartition tp : assignedPartitions) {
                PartitionMetricSample sample = new PartitionMetricSample(cluster.partition(tp).leader().id(), tp);
                long now = LoadMonitorTaskRunnerTest.TIME.milliseconds();
                for (com.linkedin.kafka.cruisecontrol.common.Resource resource : com.linkedin.kafka.cruisecontrol.common.Resource.cachedValues()) {
                    for (MetricInfo metricInfo : KafkaMetricDef.resourceToMetricInfo(resource)) {
                        sample.record(metricInfo, now);
                    }
                }
                sample.close(now);
                partitionMetricSamples.add(sample);
            }
            return new Samples(partitionMetricSamples, Collections.emptySet());
        }

        @Override
        public void configure(Map<String, ?> configs) {
        }

        @Override
        public void close() throws Exception {
        }
    }

    /**
     * A clock that you can manually advance by calling sleep.
     */
    private static class MockTime implements Time {
        private long nanos = 0;

        private long autoTickMs = 0;

        public MockTime(long autoTickMs) {
            this.nanos = 0;
            this.autoTickMs = autoTickMs;
        }

        @Override
        public long milliseconds() {
            this.sleep(autoTickMs);
            return TimeUnit.MILLISECONDS.convert(this.nanos, TimeUnit.NANOSECONDS);
        }

        @Override
        public long hiResClockMs() {
            return 0;
        }

        @Override
        public long nanoseconds() {
            this.sleep(autoTickMs);
            return nanos;
        }

        @Override
        public void sleep(long ms) {
            this.nanos += TimeUnit.NANOSECONDS.convert(ms, TimeUnit.MILLISECONDS);
        }
    }

    private static class MockPartitionMetricSampleAggregator extends KafkaPartitionMetricSampleAggregator {
        private final BlockingQueue<PartitionMetricSample> _partitionMetricSamples;

        /**
         * Construct the metric sample aggregator.
         *
         * @param config
         * 		The load monitor configurations.
         * @param metadata
         * 		The metadata of the cluster.
         */
        MockPartitionMetricSampleAggregator(KafkaCruiseControlConfig config, Metadata metadata) {
            super(config, metadata);
            _partitionMetricSamples = new ArrayBlockingQueue(10000);
        }

        @Override
        public synchronized boolean addSample(PartitionMetricSample sample) {
            _partitionMetricSamples.add(sample);
            return true;
        }

        @Override
        public synchronized boolean addSample(PartitionMetricSample sample, boolean skipLeaderCheck) {
            _partitionMetricSamples.add(sample);
            return true;
        }

        public BlockingQueue<PartitionMetricSample> metricSampleQueue() {
            return _partitionMetricSamples;
        }
    }
}

