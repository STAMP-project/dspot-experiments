/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.statistics;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.geode.internal.NanoTimer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link StatMonitorHandler}.
 *
 * @since GemFire 7.0
 */
public class StatMonitorHandlerTest {
    @Test
    public void testAddNewMonitor() throws Exception {
        StatMonitorHandler handler = new StatMonitorHandler();
        Assert.assertTrue(handler.getMonitorsSnapshot().isEmpty());
        StatMonitorHandlerTest.TestStatisticsMonitor monitor = new StatMonitorHandlerTest.TestStatisticsMonitor();
        Assert.assertTrue(handler.addMonitor(monitor));
        Assert.assertFalse(handler.getMonitorsSnapshot().isEmpty());
        Assert.assertTrue(handler.getMonitorsSnapshot().contains(monitor));
        handler.sampled(NanoTimer.getTime(), Collections.<ResourceInstance>emptyList());
        StatMonitorHandlerTest.waitForNotificationCount(monitor, 1, (2 * 1000), 10, false);
        Assert.assertEquals(1, monitor.getNotificationCount());
    }

    @Test
    public void testAddExistingMonitorReturnsFalse() throws Exception {
        StatMonitorHandler handler = new StatMonitorHandler();
        Assert.assertTrue(handler.getMonitorsSnapshot().isEmpty());
        StatisticsMonitor monitor = new StatMonitorHandlerTest.TestStatisticsMonitor();
        Assert.assertTrue(handler.addMonitor(monitor));
        Assert.assertFalse(handler.getMonitorsSnapshot().isEmpty());
        Assert.assertTrue(handler.getMonitorsSnapshot().contains(monitor));
        Assert.assertFalse(handler.addMonitor(monitor));
    }

    @Test
    public void testRemoveExistingMonitor() throws Exception {
        StatMonitorHandler handler = new StatMonitorHandler();
        Assert.assertTrue(handler.getMonitorsSnapshot().isEmpty());
        StatMonitorHandlerTest.TestStatisticsMonitor monitor = new StatMonitorHandlerTest.TestStatisticsMonitor();
        Assert.assertTrue(handler.addMonitor(monitor));
        Assert.assertFalse(handler.getMonitorsSnapshot().isEmpty());
        Assert.assertTrue(handler.getMonitorsSnapshot().contains(monitor));
        Assert.assertTrue(handler.removeMonitor(monitor));
        Assert.assertFalse(handler.getMonitorsSnapshot().contains(monitor));
        Assert.assertTrue(handler.getMonitorsSnapshot().isEmpty());
        handler.sampled(NanoTimer.getTime(), Collections.<ResourceInstance>emptyList());
        Assert.assertEquals(0, monitor.getNotificationCount());
    }

    @Test
    public void testRemoveMissingMonitorReturnsFalse() throws Exception {
        StatMonitorHandler handler = new StatMonitorHandler();
        Assert.assertTrue(handler.getMonitorsSnapshot().isEmpty());
        StatisticsMonitor monitor = new StatMonitorHandlerTest.TestStatisticsMonitor();
        Assert.assertFalse(handler.getMonitorsSnapshot().contains(monitor));
        Assert.assertFalse(handler.removeMonitor(monitor));
        Assert.assertTrue(handler.getMonitorsSnapshot().isEmpty());
    }

    @Test
    public void testNotificationSampleFrequencyDefault() throws Exception {
        final int sampleFrequency = 1;
        StatMonitorHandler handler = new StatMonitorHandler();
        StatMonitorHandlerTest.TestStatisticsMonitor monitor = new StatMonitorHandlerTest.TestStatisticsMonitor();
        handler.addMonitor(monitor);
        final int sampleCount = 100;
        for (int i = 0; i < sampleCount; i++) {
            handler.sampled(NanoTimer.getTime(), Collections.<ResourceInstance>emptyList());
            StatMonitorHandlerTest.waitForNotificationCount(monitor, (1 + i), (2 * 1000), 10, false);
        }
        Assert.assertEquals((sampleCount / sampleFrequency), monitor.getNotificationCount());
    }

    @Test
    public void testNotificationSampleTimeMillis() throws Exception {
        final long currentTime = System.currentTimeMillis();
        StatMonitorHandler handler = new StatMonitorHandler();
        StatMonitorHandlerTest.TestStatisticsMonitor monitor = new StatMonitorHandlerTest.TestStatisticsMonitor();
        handler.addMonitor(monitor);
        long nanoTimeStamp = NanoTimer.getTime();
        handler.sampled(nanoTimeStamp, Collections.<ResourceInstance>emptyList());
        StatMonitorHandlerTest.waitForNotificationCount(monitor, 1, (2 * 1000), 10, false);
        Assert.assertTrue(((monitor.getTimeStamp()) != nanoTimeStamp));
        Assert.assertTrue(((monitor.getTimeStamp()) >= currentTime));
    }

    @Test
    public void testNotificationResourceInstances() throws Exception {
        final int resourceInstanceCount = 100;
        final List<ResourceInstance> resourceInstances = new ArrayList<ResourceInstance>();
        for (int i = 0; i < resourceInstanceCount; i++) {
            resourceInstances.add(new ResourceInstance(i, null, null));
        }
        StatMonitorHandler handler = new StatMonitorHandler();
        StatMonitorHandlerTest.TestStatisticsMonitor monitor = new StatMonitorHandlerTest.TestStatisticsMonitor();
        handler.addMonitor(monitor);
        handler.sampled(NanoTimer.getTime(), Collections.unmodifiableList(resourceInstances));
        StatMonitorHandlerTest.waitForNotificationCount(monitor, 1, (2 * 1000), 10, false);
        final List<ResourceInstance> notificationResourceInstances = monitor.getResourceInstances();
        Assert.assertNotNull(notificationResourceInstances);
        Assert.assertEquals(resourceInstances, notificationResourceInstances);
        Assert.assertEquals(resourceInstanceCount, notificationResourceInstances.size());
        int i = 0;
        for (ResourceInstance resourceInstance : notificationResourceInstances) {
            Assert.assertEquals(i, resourceInstance.getId());
            i++;
        }
    }

    /**
     *
     *
     * @since GemFire 7.0
     */
    private static class TestStatisticsMonitor extends StatisticsMonitor {
        private volatile long timeStamp;

        private volatile List<ResourceInstance> resourceInstances;

        private volatile int notificationCount;

        public TestStatisticsMonitor() {
            super();
        }

        @Override
        protected void monitor(long timeStamp, List<ResourceInstance> resourceInstances) {
            this.timeStamp = timeStamp;
            this.resourceInstances = resourceInstances;
            (this.notificationCount)++;
        }

        long getTimeStamp() {
            return this.timeStamp;
        }

        List<ResourceInstance> getResourceInstances() {
            return this.resourceInstances;
        }

        int getNotificationCount() {
            return this.notificationCount;
        }
    }
}

