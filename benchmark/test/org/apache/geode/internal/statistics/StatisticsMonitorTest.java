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


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for the StatisticsMonitor class. No disk IO.
 *
 * @since GemFire 7.0
 */
public class StatisticsMonitorTest {
    private TestStatisticsManager manager;

    private SampleCollector sampleCollector;

    @Test
    public void testAddListener() {
        StatisticsMonitorTest.TestStatisticsMonitor monitor = new StatisticsMonitorTest.TestStatisticsMonitor();
        Assert.assertTrue(getStatisticsListenersSnapshot().isEmpty());
        StatisticsListener listener = new StatisticsListener() {
            @Override
            public void handleNotification(StatisticsNotification notification) {
            }
        };
        Assert.assertNull(this.sampleCollector.getStatMonitorHandlerSnapshot());
        monitor.addListener(listener);
        Assert.assertFalse(getStatisticsListenersSnapshot().isEmpty());
        Assert.assertTrue(getStatisticsListenersSnapshot().contains(listener));
        Assert.assertEquals(1, getStatisticsListenersSnapshot().size());
        Assert.assertNotNull(this.sampleCollector.getStatMonitorHandlerSnapshot());
        Assert.assertFalse(this.sampleCollector.getStatMonitorHandlerSnapshot().getMonitorsSnapshot().isEmpty());
    }

    @Test
    public void testAddExistingListener() {
        StatisticsMonitorTest.TestStatisticsMonitor monitor = new StatisticsMonitorTest.TestStatisticsMonitor();
        Assert.assertTrue(getStatisticsListenersSnapshot().isEmpty());
        StatisticsListener listener = new StatisticsListener() {
            @Override
            public void handleNotification(StatisticsNotification notification) {
            }
        };
        monitor.addListener(listener);
        Assert.assertFalse(getStatisticsListenersSnapshot().isEmpty());
        Assert.assertTrue(getStatisticsListenersSnapshot().contains(listener));
        Assert.assertEquals(1, getStatisticsListenersSnapshot().size());
        monitor.addListener(listener);
        Assert.assertFalse(getStatisticsListenersSnapshot().isEmpty());
        Assert.assertTrue(getStatisticsListenersSnapshot().contains(listener));
        Assert.assertEquals(1, getStatisticsListenersSnapshot().size());
    }

    @Test
    public void testRemoveListener() {
        StatisticsMonitorTest.TestStatisticsMonitor monitor = new StatisticsMonitorTest.TestStatisticsMonitor();
        Assert.assertTrue(getStatisticsListenersSnapshot().isEmpty());
        StatisticsListener listener = new StatisticsListener() {
            @Override
            public void handleNotification(StatisticsNotification notification) {
            }
        };
        Assert.assertNull(this.sampleCollector.getStatMonitorHandlerSnapshot());
        monitor.addListener(listener);
        Assert.assertFalse(getStatisticsListenersSnapshot().isEmpty());
        Assert.assertTrue(getStatisticsListenersSnapshot().contains(listener));
        Assert.assertEquals(1, getStatisticsListenersSnapshot().size());
        Assert.assertNotNull(this.sampleCollector.getStatMonitorHandlerSnapshot());
        Assert.assertFalse(this.sampleCollector.getStatMonitorHandlerSnapshot().getMonitorsSnapshot().isEmpty());
        monitor.removeListener(listener);
        Assert.assertTrue(getStatisticsListenersSnapshot().isEmpty());
        Assert.assertFalse(getStatisticsListenersSnapshot().contains(listener));
        Assert.assertNotNull(this.sampleCollector.getStatMonitorHandlerSnapshot());
        Assert.assertTrue(this.sampleCollector.getStatMonitorHandlerSnapshot().getMonitorsSnapshot().isEmpty());
    }

    @Test
    public void testRemoveMissingListener() {
        StatisticsMonitorTest.TestStatisticsMonitor monitor = new StatisticsMonitorTest.TestStatisticsMonitor();
        Assert.assertTrue(getStatisticsListenersSnapshot().isEmpty());
        StatisticsListener listener = new StatisticsListener() {
            @Override
            public void handleNotification(StatisticsNotification notification) {
            }
        };
        Assert.assertTrue(getStatisticsListenersSnapshot().isEmpty());
        Assert.assertFalse(getStatisticsListenersSnapshot().contains(listener));
        monitor.removeListener(listener);
        Assert.assertTrue(getStatisticsListenersSnapshot().isEmpty());
        Assert.assertFalse(getStatisticsListenersSnapshot().contains(listener));
    }

    // TODO: test addStatistic
    // TODO: test removeStatistic
    // TODO: test monitor and/or monitorStatisticIds
    // TODO: test notifyListeners
    /**
     *
     *
     * @since GemFire 7.0
     */
    static class TestStatisticsMonitor extends StatisticsMonitor {
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

