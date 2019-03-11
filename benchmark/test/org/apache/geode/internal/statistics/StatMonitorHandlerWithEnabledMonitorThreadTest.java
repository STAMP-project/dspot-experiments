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


import java.util.Collections;
import java.util.List;
import org.apache.geode.internal.NanoTimer;
import org.apache.geode.internal.statistics.StatMonitorHandler.StatMonitorNotifier;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;


/**
 * Extracted tests from StatMonitorHandlerTest that require enableMonitorThread
 */
public class StatMonitorHandlerWithEnabledMonitorThreadTest {
    @Rule
    public RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    @Test
    public void testStatMonitorNotifierAliveButWaiting() throws Exception {
        StatMonitorHandler handler = new StatMonitorHandler();
        StatMonitorHandlerWithEnabledMonitorThreadTest.TestStatisticsMonitor monitor = new StatMonitorHandlerWithEnabledMonitorThreadTest.TestStatisticsMonitor();
        handler.addMonitor(monitor);
        final StatMonitorNotifier notifier = handler.getStatMonitorNotifier();
        Assert.assertTrue(notifier.isAlive());
        StatMonitorHandlerWithEnabledMonitorThreadTest.waitUntilWaiting(notifier);
        for (int i = 0; i < 20; i++) {
            Assert.assertTrue(notifier.isWaiting());
            Thread.sleep(10);
        }
    }

    @Test
    public void testStatMonitorNotifierWakesUpForWork() throws Exception {
        StatMonitorHandler handler = new StatMonitorHandler();
        StatMonitorHandlerWithEnabledMonitorThreadTest.TestStatisticsMonitor monitor = new StatMonitorHandlerWithEnabledMonitorThreadTest.TestStatisticsMonitor();
        handler.addMonitor(monitor);
        final StatMonitorNotifier notifier = handler.getStatMonitorNotifier();
        Assert.assertTrue(notifier.isAlive());
        StatMonitorHandlerWithEnabledMonitorThreadTest.waitUntilWaiting(notifier);
        // if notification occurs then notifier woke up...
        Assert.assertEquals(0, monitor.getNotificationCount());
        handler.sampled(NanoTimer.getTime(), Collections.<ResourceInstance>emptyList());
        StatMonitorHandlerWithEnabledMonitorThreadTest.waitForNotificationCount(monitor, 1, (2 * 1000), 10, false);
        Assert.assertEquals(1, monitor.getNotificationCount());
        // and goes back to waiting...
        StatMonitorHandlerWithEnabledMonitorThreadTest.waitUntilWaiting(notifier);
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

