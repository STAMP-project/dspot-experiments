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
package org.apache.hadoop.yarn.server.resourcemanager.metrics;


import YarnConfiguration.TIMELINE_SERVICE_ENABLED;
import YarnConfiguration.TIMELINE_SERVICE_VERSION;
import YarnConfiguration.TIMELINE_SERVICE_VERSIONS;
import java.io.File;
import java.util.concurrent.ConcurrentMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.DrainDispatcher;
import org.apache.hadoop.yarn.server.applicationhistoryservice.ApplicationHistoryServer;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.timelineservice.RMTimelineCollectorManager;
import org.apache.hadoop.yarn.server.timeline.TimelineStore;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests that a CombinedSystemMetricsPublisher publishes metrics for timeline
 * services (v1/v2) as specified by the configuration.
 */
public class TestCombinedSystemMetricsPublisher {
    /**
     * The folder where the FileSystemTimelineWriterImpl writes the entities.
     */
    private static File testRootDir = new File("target", ((TestCombinedSystemMetricsPublisher.class.getName()) + "-localDir")).getAbsoluteFile();

    private static ApplicationHistoryServer timelineServer;

    private static CombinedSystemMetricsPublisher metricsPublisher;

    private static TimelineStore store;

    private static ConcurrentMap<ApplicationId, RMApp> rmAppsMapInContext;

    private static RMTimelineCollectorManager rmTimelineCollectorManager;

    private static DrainDispatcher dispatcher;

    private static YarnConfiguration conf;

    private static TimelineServiceV1Publisher publisherV1;

    private static TimelineServiceV2Publisher publisherV2;

    private static ApplicationAttemptId appAttemptId;

    private static RMApp app;

    @Test(timeout = 10000)
    public void testTimelineServiceEventPublishingV1V2Enabled() throws Exception {
        runTest(true, true);
    }

    @Test(timeout = 10000)
    public void testTimelineServiceEventPublishingV1Enabled() throws Exception {
        runTest(true, false);
    }

    @Test(timeout = 10000)
    public void testTimelineServiceEventPublishingV2Enabled() throws Exception {
        runTest(false, true);
    }

    @Test(timeout = 10000)
    public void testTimelineServiceEventPublishingNoService() throws Exception {
        runTest(false, false);
    }

    @Test(timeout = 10000)
    public void testTimelineServiceConfiguration() throws Exception {
        Configuration config = new Configuration(false);
        config.setBoolean(TIMELINE_SERVICE_ENABLED, true);
        config.set(TIMELINE_SERVICE_VERSIONS, "2.0,1.5");
        config.set(TIMELINE_SERVICE_VERSION, "2.0");
        Assert.assertTrue(YarnConfiguration.timelineServiceV2Enabled(config));
        Assert.assertTrue(YarnConfiguration.timelineServiceV15Enabled(config));
        Assert.assertTrue(YarnConfiguration.timelineServiceV1Enabled(config));
        config.set(TIMELINE_SERVICE_VERSIONS, "2.0,1");
        config.set(TIMELINE_SERVICE_VERSION, "1.5");
        Assert.assertTrue(YarnConfiguration.timelineServiceV2Enabled(config));
        Assert.assertFalse(YarnConfiguration.timelineServiceV15Enabled(config));
        Assert.assertTrue(YarnConfiguration.timelineServiceV1Enabled(config));
        config.set(TIMELINE_SERVICE_VERSIONS, "2.0");
        config.set(TIMELINE_SERVICE_VERSION, "1.5");
        Assert.assertTrue(YarnConfiguration.timelineServiceV2Enabled(config));
        Assert.assertFalse(YarnConfiguration.timelineServiceV15Enabled(config));
        Assert.assertFalse(YarnConfiguration.timelineServiceV1Enabled(config));
    }
}

