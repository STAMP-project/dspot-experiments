/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.datanode.metrics;


import com.google.common.base.Supplier;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.metrics2.lib.MetricsTestHelper;
import org.apache.hadoop.test.GenericTestUtils;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test that the {@link DataNodePeerMetrics} class is able to detect
 * outliers i.e. slow nodes via the metrics it maintains.
 */
public class TestDataNodeOutlierDetectionViaMetrics {
    public static final Logger LOG = LoggerFactory.getLogger(TestDataNodeOutlierDetectionViaMetrics.class);

    /**
     * Set a timeout for every test case.
     */
    @Rule
    public Timeout testTimeout = new Timeout(300000);

    // A few constants to keep the test run time short.
    private static final int WINDOW_INTERVAL_SECONDS = 3;

    private static final int ROLLING_AVERAGE_WINDOWS = 10;

    private static final int SLOW_NODE_LATENCY_MS = 20000;

    private static final int FAST_NODE_MAX_LATENCY_MS = 5;

    private static final long MIN_OUTLIER_DETECTION_PEERS = 10;

    private Random random = new Random(System.currentTimeMillis());

    /**
     * Test that a very slow peer is detected as an outlier.
     */
    @Test
    public void testOutlierIsDetected() throws Exception {
        final String slowNodeName = "SlowNode";
        DataNodePeerMetrics peerMetrics = new DataNodePeerMetrics("PeerMetrics-For-Test");
        MetricsTestHelper.replaceRollingAveragesScheduler(peerMetrics.getSendPacketDownstreamRollingAverages(), TestDataNodeOutlierDetectionViaMetrics.ROLLING_AVERAGE_WINDOWS, TestDataNodeOutlierDetectionViaMetrics.WINDOW_INTERVAL_SECONDS, TimeUnit.SECONDS);
        injectFastNodesSamples(peerMetrics);
        injectSlowNodeSamples(peerMetrics, slowNodeName);
        // Trigger a snapshot.
        peerMetrics.dumpSendPacketDownstreamAvgInfoAsJson();
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return (peerMetrics.getOutliers().size()) > 0;
            }
        }, 500, 100000);
        final Map<String, Double> outliers = peerMetrics.getOutliers();
        TestDataNodeOutlierDetectionViaMetrics.LOG.info("Got back outlier nodes: {}", outliers);
        Assert.assertThat(outliers.size(), Is.is(1));
        Assert.assertTrue(outliers.containsKey(slowNodeName));
    }

    /**
     * Test that when there are no outliers, we get back nothing.
     */
    @Test
    public void testWithNoOutliers() throws Exception {
        DataNodePeerMetrics peerMetrics = new DataNodePeerMetrics("PeerMetrics-For-Test");
        MetricsTestHelper.replaceRollingAveragesScheduler(peerMetrics.getSendPacketDownstreamRollingAverages(), TestDataNodeOutlierDetectionViaMetrics.ROLLING_AVERAGE_WINDOWS, TestDataNodeOutlierDetectionViaMetrics.WINDOW_INTERVAL_SECONDS, TimeUnit.SECONDS);
        injectFastNodesSamples(peerMetrics);
        // Trigger a snapshot.
        peerMetrics.dumpSendPacketDownstreamAvgInfoAsJson();
        // Ensure that we get back the outlier.
        Assert.assertTrue(peerMetrics.getOutliers().isEmpty());
    }
}

