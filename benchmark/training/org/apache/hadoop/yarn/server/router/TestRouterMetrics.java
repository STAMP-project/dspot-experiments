/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.router;


import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class validates the correctness of Router Federation Interceptor
 * Metrics.
 */
public class TestRouterMetrics {
    public static final Logger LOG = LoggerFactory.getLogger(TestRouterMetrics.class);

    // All the operations in the bad subcluster failed.
    private TestRouterMetrics.MockBadSubCluster badSubCluster = new TestRouterMetrics.MockBadSubCluster();

    // All the operations in the bad subcluster succeed.
    private TestRouterMetrics.MockGoodSubCluster goodSubCluster = new TestRouterMetrics.MockGoodSubCluster();

    private static RouterMetrics metrics = RouterMetrics.getMetrics();

    /**
     * This test validates the correctness of the metric: Created Apps
     * successfully.
     */
    @Test
    public void testSucceededAppsCreated() {
        long totalGoodBefore = TestRouterMetrics.metrics.getNumSucceededAppsCreated();
        goodSubCluster.getNewApplication(100);
        Assert.assertEquals((totalGoodBefore + 1), TestRouterMetrics.metrics.getNumSucceededAppsCreated());
        Assert.assertEquals(100, TestRouterMetrics.metrics.getLatencySucceededAppsCreated(), 0);
        goodSubCluster.getNewApplication(200);
        Assert.assertEquals((totalGoodBefore + 2), TestRouterMetrics.metrics.getNumSucceededAppsCreated());
        Assert.assertEquals(150, TestRouterMetrics.metrics.getLatencySucceededAppsCreated(), 0);
    }

    /**
     * This test validates the correctness of the metric: Failed to create Apps.
     */
    @Test
    public void testAppsFailedCreated() {
        long totalBadbefore = TestRouterMetrics.metrics.getAppsFailedCreated();
        badSubCluster.getNewApplication();
        Assert.assertEquals((totalBadbefore + 1), TestRouterMetrics.metrics.getAppsFailedCreated());
    }

    /**
     * This test validates the correctness of the metric: Submitted Apps
     * successfully.
     */
    @Test
    public void testSucceededAppsSubmitted() {
        long totalGoodBefore = TestRouterMetrics.metrics.getNumSucceededAppsSubmitted();
        goodSubCluster.submitApplication(100);
        Assert.assertEquals((totalGoodBefore + 1), TestRouterMetrics.metrics.getNumSucceededAppsSubmitted());
        Assert.assertEquals(100, TestRouterMetrics.metrics.getLatencySucceededAppsSubmitted(), 0);
        goodSubCluster.submitApplication(200);
        Assert.assertEquals((totalGoodBefore + 2), TestRouterMetrics.metrics.getNumSucceededAppsSubmitted());
        Assert.assertEquals(150, TestRouterMetrics.metrics.getLatencySucceededAppsSubmitted(), 0);
    }

    /**
     * This test validates the correctness of the metric: Failed to submit Apps.
     */
    @Test
    public void testAppsFailedSubmitted() {
        long totalBadbefore = TestRouterMetrics.metrics.getAppsFailedSubmitted();
        badSubCluster.submitApplication();
        Assert.assertEquals((totalBadbefore + 1), TestRouterMetrics.metrics.getAppsFailedSubmitted());
    }

    /**
     * This test validates the correctness of the metric: Killed Apps
     * successfully.
     */
    @Test
    public void testSucceededAppsKilled() {
        long totalGoodBefore = TestRouterMetrics.metrics.getNumSucceededAppsKilled();
        goodSubCluster.forceKillApplication(100);
        Assert.assertEquals((totalGoodBefore + 1), TestRouterMetrics.metrics.getNumSucceededAppsKilled());
        Assert.assertEquals(100, TestRouterMetrics.metrics.getLatencySucceededAppsKilled(), 0);
        goodSubCluster.forceKillApplication(200);
        Assert.assertEquals((totalGoodBefore + 2), TestRouterMetrics.metrics.getNumSucceededAppsKilled());
        Assert.assertEquals(150, TestRouterMetrics.metrics.getLatencySucceededAppsKilled(), 0);
    }

    /**
     * This test validates the correctness of the metric: Failed to kill Apps.
     */
    @Test
    public void testAppsFailedKilled() {
        long totalBadbefore = TestRouterMetrics.metrics.getAppsFailedKilled();
        badSubCluster.forceKillApplication();
        Assert.assertEquals((totalBadbefore + 1), TestRouterMetrics.metrics.getAppsFailedKilled());
    }

    /**
     * This test validates the correctness of the metric: Retrieved Apps
     * successfully.
     */
    @Test
    public void testSucceededAppsReport() {
        long totalGoodBefore = TestRouterMetrics.metrics.getNumSucceededAppsRetrieved();
        goodSubCluster.getApplicationReport(100);
        Assert.assertEquals((totalGoodBefore + 1), TestRouterMetrics.metrics.getNumSucceededAppsRetrieved());
        Assert.assertEquals(100, TestRouterMetrics.metrics.getLatencySucceededGetAppReport(), 0);
        goodSubCluster.getApplicationReport(200);
        Assert.assertEquals((totalGoodBefore + 2), TestRouterMetrics.metrics.getNumSucceededAppsRetrieved());
        Assert.assertEquals(150, TestRouterMetrics.metrics.getLatencySucceededGetAppReport(), 0);
    }

    /**
     * This test validates the correctness of the metric: Failed to retrieve Apps.
     */
    @Test
    public void testAppsReportFailed() {
        long totalBadbefore = TestRouterMetrics.metrics.getAppsFailedRetrieved();
        badSubCluster.getApplicationReport();
        Assert.assertEquals((totalBadbefore + 1), TestRouterMetrics.metrics.getAppsFailedRetrieved());
    }

    /**
     * This test validates the correctness of the metric: Retrieved Multiple Apps
     * successfully.
     */
    @Test
    public void testSucceededMultipleAppsReport() {
        long totalGoodBefore = TestRouterMetrics.metrics.getNumSucceededMultipleAppsRetrieved();
        goodSubCluster.getApplicationsReport(100);
        Assert.assertEquals((totalGoodBefore + 1), TestRouterMetrics.metrics.getNumSucceededMultipleAppsRetrieved());
        Assert.assertEquals(100, TestRouterMetrics.metrics.getLatencySucceededMultipleGetAppReport(), 0);
        goodSubCluster.getApplicationsReport(200);
        Assert.assertEquals((totalGoodBefore + 2), TestRouterMetrics.metrics.getNumSucceededMultipleAppsRetrieved());
        Assert.assertEquals(150, TestRouterMetrics.metrics.getLatencySucceededMultipleGetAppReport(), 0);
    }

    /**
     * This test validates the correctness of the metric: Failed to retrieve
     * Multiple Apps.
     */
    @Test
    public void testMulipleAppsReportFailed() {
        long totalBadbefore = TestRouterMetrics.metrics.getMultipleAppsFailedRetrieved();
        badSubCluster.getApplicationsReport();
        Assert.assertEquals((totalBadbefore + 1), TestRouterMetrics.metrics.getMultipleAppsFailedRetrieved());
    }

    // Records failures for all calls
    private class MockBadSubCluster {
        public void getNewApplication() {
            TestRouterMetrics.LOG.info("Mocked: failed getNewApplication call");
            TestRouterMetrics.metrics.incrAppsFailedCreated();
        }

        public void submitApplication() {
            TestRouterMetrics.LOG.info("Mocked: failed submitApplication call");
            TestRouterMetrics.metrics.incrAppsFailedSubmitted();
        }

        public void forceKillApplication() {
            TestRouterMetrics.LOG.info("Mocked: failed forceKillApplication call");
            TestRouterMetrics.metrics.incrAppsFailedKilled();
        }

        public void getApplicationReport() {
            TestRouterMetrics.LOG.info("Mocked: failed getApplicationReport call");
            TestRouterMetrics.metrics.incrAppsFailedRetrieved();
        }

        public void getApplicationsReport() {
            TestRouterMetrics.LOG.info("Mocked: failed getApplicationsReport call");
            TestRouterMetrics.metrics.incrMultipleAppsFailedRetrieved();
        }
    }

    // Records successes for all calls
    private class MockGoodSubCluster {
        public void getNewApplication(long duration) {
            TestRouterMetrics.LOG.info("Mocked: successful getNewApplication call with duration {}", duration);
            TestRouterMetrics.metrics.succeededAppsCreated(duration);
        }

        public void submitApplication(long duration) {
            TestRouterMetrics.LOG.info("Mocked: successful submitApplication call with duration {}", duration);
            TestRouterMetrics.metrics.succeededAppsSubmitted(duration);
        }

        public void forceKillApplication(long duration) {
            TestRouterMetrics.LOG.info("Mocked: successful forceKillApplication call with duration {}", duration);
            TestRouterMetrics.metrics.succeededAppsKilled(duration);
        }

        public void getApplicationReport(long duration) {
            TestRouterMetrics.LOG.info("Mocked: successful getApplicationReport call with duration {}", duration);
            TestRouterMetrics.metrics.succeededAppsRetrieved(duration);
        }

        public void getApplicationsReport(long duration) {
            TestRouterMetrics.LOG.info("Mocked: successful getApplicationsReport call with duration {}", duration);
            TestRouterMetrics.metrics.succeededMultipleAppsRetrieved(duration);
        }
    }
}

