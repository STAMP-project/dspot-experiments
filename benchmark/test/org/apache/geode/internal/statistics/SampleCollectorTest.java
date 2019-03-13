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
import org.apache.geode.StatisticDescriptor;
import org.apache.geode.Statistics;
import org.apache.geode.StatisticsType;
import org.apache.geode.internal.NanoTimer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link SampleCollector}.
 *
 * @since GemFire 7.0
 */
public class SampleCollectorTest {
    private TestStatisticsManager manager;

    private SampleCollector sampleCollector;

    @Test
    public void testAddHandlerBeforeSample() {
        TestSampleHandler handler = new TestSampleHandler();
        this.sampleCollector.addSampleHandler(handler);
        StatisticDescriptor[] statsST1 = new StatisticDescriptor[]{ manager.createIntCounter("ST1_1_name", "ST1_1_desc", "ST1_1_units") };
        StatisticsType ST1 = manager.createType("ST1_name", "ST1_desc", statsST1);
        Statistics st1_1 = manager.createAtomicStatistics(ST1, "st1_1_text", 1);
        this.sampleCollector.sample(NanoTimer.getTime());
        Assert.assertEquals(3, handler.getNotificationCount());
        List<TestSampleHandler.Info> notifications = handler.getNotifications();
        // validate the allocatedResourceType notification
        Assert.assertTrue(((notifications.get(0)) instanceof TestSampleHandler.ResourceTypeInfo));
        TestSampleHandler.ResourceTypeInfo allocatedResourceTypeInfo = ((TestSampleHandler.ResourceTypeInfo) (notifications.get(0)));
        Assert.assertNotNull(allocatedResourceTypeInfo);
        Assert.assertEquals("allocatedResourceType", allocatedResourceTypeInfo.getName());
        ResourceType resourceType = allocatedResourceTypeInfo.getResourceType();
        Assert.assertNotNull(resourceType);
        Assert.assertEquals(0, resourceType.getId());
        Assert.assertEquals(1, resourceType.getStatisticDescriptors().length);
        StatisticsType statisticsType = resourceType.getStatisticsType();
        Assert.assertNotNull(statisticsType);
        Assert.assertTrue((statisticsType == ST1));
        Assert.assertEquals("ST1_name", statisticsType.getName());
        Assert.assertEquals("ST1_desc", statisticsType.getDescription());
        Assert.assertEquals(1, statisticsType.getStatistics().length);
        // validate the allocatedResourceInstance notification
        Assert.assertTrue(((notifications.get(1)) instanceof TestSampleHandler.ResourceInstanceInfo));
        TestSampleHandler.ResourceInstanceInfo allocatedResourceInstanceInfo = ((TestSampleHandler.ResourceInstanceInfo) (notifications.get(1)));
        Assert.assertNotNull(allocatedResourceInstanceInfo);
        Assert.assertEquals("allocatedResourceInstance", allocatedResourceInstanceInfo.getName());
        ResourceInstance resourceInstance = allocatedResourceInstanceInfo.getResourceInstance();
        Assert.assertNotNull(resourceInstance);
        Assert.assertEquals(0, resourceInstance.getId());
        Assert.assertEquals(1, resourceInstance.getUpdatedStats().length);
        Assert.assertEquals(1, resourceInstance.getLatestStatValues().length);// TODO: is this correct?

        Statistics statistics = resourceInstance.getStatistics();
        Assert.assertNotNull(statistics);
        Assert.assertTrue((statistics == st1_1));
        Assert.assertEquals(1, statistics.getUniqueId());
        Assert.assertEquals(1, statistics.getNumericId());
        Assert.assertEquals("st1_1_text", statistics.getTextId());
        Assert.assertEquals("ST1_name", statistics.getType().getName());
        Assert.assertTrue((resourceType == (resourceInstance.getResourceType())));
        // validate the sampled notification
        Assert.assertTrue(((notifications.get(2)) instanceof TestSampleHandler.SampledInfo));
        TestSampleHandler.SampledInfo sampledInfo = ((TestSampleHandler.SampledInfo) (notifications.get(2)));
        Assert.assertNotNull(sampledInfo);
        Assert.assertEquals("sampled", sampledInfo.getName());
        Assert.assertEquals(1, sampledInfo.getResourceCount());
    }

    @Test
    public void testAddHandlerAfterSamples() {
        StatisticDescriptor[] statsST1 = new StatisticDescriptor[]{ manager.createIntCounter("ST1_1_name", "ST1_1_desc", "ST1_1_units") };
        StatisticsType ST1 = manager.createType("ST1_name", "ST1_desc", statsST1);
        Statistics st1_1 = manager.createAtomicStatistics(ST1, "st1_1_text", 1);
        Statistics st1_2 = manager.createAtomicStatistics(ST1, "st1_2_text", 1);
        StatisticDescriptor[] statsST2 = new StatisticDescriptor[]{ manager.createIntCounter("ST2_1_name", "ST2_1_desc", "ST2_1_units") };
        StatisticsType ST2 = manager.createType("ST2_name", "ST2_desc", statsST2);
        Statistics st2_1 = manager.createAtomicStatistics(ST2, "st2_1_text", 1);
        st1_1.incInt("ST1_1_name", 1);
        st1_2.incInt("ST1_1_name", 1);
        st2_1.incInt("ST2_1_name", 1);
        long sampleTime = NanoTimer.getTime();
        this.sampleCollector.sample(sampleTime);
        st1_1.incInt("ST1_1_name", 2);
        st2_1.incInt("ST2_1_name", 1);
        sampleTime += NanoTimer.millisToNanos(1000);
        this.sampleCollector.sample(sampleTime);
        st1_1.incInt("ST1_1_name", 1);
        st1_1.incInt("ST1_1_name", 2);
        sampleTime += NanoTimer.millisToNanos(1000);
        this.sampleCollector.sample(sampleTime);
        TestSampleHandler handler = new TestSampleHandler();
        this.sampleCollector.addSampleHandler(handler);
        Assert.assertEquals(("TestSampleHandler = " + handler), 0, handler.getNotificationCount());
        st1_2.incInt("ST1_1_name", 1);
        st2_1.incInt("ST2_1_name", 1);
        sampleTime += NanoTimer.millisToNanos(1000);
        this.sampleCollector.sample(sampleTime);
        Assert.assertEquals(6, handler.getNotificationCount());
        List<TestSampleHandler.Info> notifications = handler.getNotifications();
        // validate the allocatedResourceType notification for ST1
        int notificationIdx = 0;
        Assert.assertTrue(((notifications.get(notificationIdx)) instanceof TestSampleHandler.ResourceTypeInfo));
        TestSampleHandler.ResourceTypeInfo allocatedResourceTypeInfo = ((TestSampleHandler.ResourceTypeInfo) (notifications.get(notificationIdx)));
        Assert.assertNotNull(allocatedResourceTypeInfo);
        Assert.assertEquals("allocatedResourceType", allocatedResourceTypeInfo.getName());
        ResourceType resourceType = allocatedResourceTypeInfo.getResourceType();
        Assert.assertNotNull(resourceType);
        Assert.assertEquals(0, resourceType.getId());
        Assert.assertEquals(1, resourceType.getStatisticDescriptors().length);
        StatisticsType statisticsType = resourceType.getStatisticsType();
        Assert.assertNotNull(statisticsType);
        Assert.assertTrue((statisticsType == ST1));
        Assert.assertEquals("ST1_name", statisticsType.getName());
        Assert.assertEquals("ST1_desc", statisticsType.getDescription());
        Assert.assertEquals(1, statisticsType.getStatistics().length);
        // validate the allocatedResourceInstance notification for st1_1
        notificationIdx++;
        Assert.assertTrue(((notifications.get(notificationIdx)) instanceof TestSampleHandler.ResourceInstanceInfo));
        TestSampleHandler.ResourceInstanceInfo allocatedResourceInstanceInfo = ((TestSampleHandler.ResourceInstanceInfo) (notifications.get(notificationIdx)));
        Assert.assertNotNull(allocatedResourceInstanceInfo);
        Assert.assertEquals("allocatedResourceInstance", allocatedResourceInstanceInfo.getName());
        ResourceInstance resourceInstance = allocatedResourceInstanceInfo.getResourceInstance();
        Assert.assertNotNull(resourceInstance);
        Assert.assertEquals(0, resourceInstance.getId());
        Assert.assertEquals(0, resourceInstance.getUpdatedStats().length);
        Assert.assertEquals(1, resourceInstance.getLatestStatValues().length);// TODO: is this correct?

        Statistics statistics = resourceInstance.getStatistics();
        Assert.assertNotNull(statistics);
        Assert.assertTrue((statistics == st1_1));
        Assert.assertEquals(1, statistics.getUniqueId());
        Assert.assertEquals(1, statistics.getNumericId());
        Assert.assertEquals("st1_1_text", statistics.getTextId());
        Assert.assertEquals("ST1_name", statistics.getType().getName());
        Assert.assertTrue((resourceType == (resourceInstance.getResourceType())));
        // validate the allocatedResourceInstance notification for st1_2
        notificationIdx++;
        Assert.assertTrue(((notifications.get(notificationIdx)) instanceof TestSampleHandler.ResourceInstanceInfo));
        allocatedResourceInstanceInfo = ((TestSampleHandler.ResourceInstanceInfo) (notifications.get(notificationIdx)));
        Assert.assertNotNull(allocatedResourceInstanceInfo);
        Assert.assertEquals("allocatedResourceInstance", allocatedResourceInstanceInfo.getName());
        resourceInstance = allocatedResourceInstanceInfo.getResourceInstance();
        Assert.assertNotNull(resourceInstance);
        Assert.assertEquals(1, resourceInstance.getId());
        Assert.assertEquals(1, resourceInstance.getUpdatedStats().length);
        Assert.assertEquals(1, resourceInstance.getLatestStatValues().length);// TODO: is this correct?

        statistics = resourceInstance.getStatistics();
        Assert.assertNotNull(statistics);
        Assert.assertTrue((statistics == st1_2));
        Assert.assertEquals(2, statistics.getUniqueId());
        Assert.assertEquals(1, statistics.getNumericId());
        Assert.assertEquals("st1_2_text", statistics.getTextId());
        Assert.assertEquals("ST1_name", statistics.getType().getName());
        Assert.assertTrue((resourceType == (resourceInstance.getResourceType())));
        // validate the allocatedResourceType notification for ST2
        notificationIdx++;
        Assert.assertTrue(((notifications.get(notificationIdx)) instanceof TestSampleHandler.ResourceTypeInfo));
        allocatedResourceTypeInfo = ((TestSampleHandler.ResourceTypeInfo) (notifications.get(notificationIdx)));
        Assert.assertNotNull(allocatedResourceTypeInfo);
        Assert.assertEquals("allocatedResourceType", allocatedResourceTypeInfo.getName());
        resourceType = allocatedResourceTypeInfo.getResourceType();
        Assert.assertNotNull(resourceType);
        Assert.assertEquals(1, resourceType.getId());
        Assert.assertEquals(1, resourceType.getStatisticDescriptors().length);
        statisticsType = resourceType.getStatisticsType();
        Assert.assertNotNull(statisticsType);
        Assert.assertTrue((statisticsType == ST2));
        Assert.assertEquals("ST2_name", statisticsType.getName());
        Assert.assertEquals("ST2_desc", statisticsType.getDescription());
        Assert.assertEquals(1, statisticsType.getStatistics().length);
        // validate the allocatedResourceInstance notification for st2_1
        notificationIdx++;
        Assert.assertTrue(((notifications.get(notificationIdx)) instanceof TestSampleHandler.ResourceInstanceInfo));
        allocatedResourceInstanceInfo = ((TestSampleHandler.ResourceInstanceInfo) (notifications.get(notificationIdx)));
        Assert.assertNotNull(allocatedResourceInstanceInfo);
        Assert.assertEquals("allocatedResourceInstance", allocatedResourceInstanceInfo.getName());
        resourceInstance = allocatedResourceInstanceInfo.getResourceInstance();
        Assert.assertNotNull(resourceInstance);
        Assert.assertEquals(2, resourceInstance.getId());
        Assert.assertEquals(1, resourceInstance.getUpdatedStats().length);
        Assert.assertEquals(1, resourceInstance.getLatestStatValues().length);// TODO: is this correct?

        statistics = resourceInstance.getStatistics();
        Assert.assertNotNull(statistics);
        Assert.assertTrue((statistics == st2_1));
        Assert.assertEquals(3, statistics.getUniqueId());
        Assert.assertEquals(1, statistics.getNumericId());
        Assert.assertEquals("st2_1_text", statistics.getTextId());
        Assert.assertEquals("ST2_name", statistics.getType().getName());
        Assert.assertTrue((resourceType == (resourceInstance.getResourceType())));
        // validate the sampled notification
        notificationIdx++;
        Assert.assertTrue(((notifications.get(notificationIdx)) instanceof TestSampleHandler.SampledInfo));
        TestSampleHandler.SampledInfo sampledInfo = ((TestSampleHandler.SampledInfo) (notifications.get(notificationIdx)));
        Assert.assertNotNull(sampledInfo);
        Assert.assertEquals("sampled", sampledInfo.getName());
        Assert.assertEquals(3, sampledInfo.getResourceCount());
    }

    @Test
    public void testGetStatMonitorHandler() {
        StatMonitorHandler handler = SampleCollector.getStatMonitorHandler();
        Assert.assertNotNull(handler);
        Assert.assertTrue(handler.getMonitorsSnapshot().isEmpty());
        Assert.assertNull(handler.getStatMonitorNotifier());
    }

    @Test
    public void testGetStatMonitorHandlerAfterClose() {
        this.sampleCollector.close();
        try {
            SampleCollector.getStatMonitorHandler();
            Assert.fail("getStatMonitorHandler should throw IllegalStateException when SampleCollector is closed");
        } catch (IllegalStateException expected) {
            // passed
        }
    }

    @Test
    public void testGetStatMonitorHandlerBeforeAndAfterClose() {
        StatMonitorHandler handler = SampleCollector.getStatMonitorHandler();
        Assert.assertNotNull(handler);
        this.sampleCollector.close();
        try {
            handler = SampleCollector.getStatMonitorHandler();
            Assert.fail("getStatMonitorHandler should throw IllegalStateException when SampleCollector is closed");
        } catch (IllegalStateException expected) {
            // passed
        }
    }

    @Test
    public void testGetStatArchiveHandler() {
        StatArchiveHandler handler = this.sampleCollector.getStatArchiveHandler();
        Assert.assertNotNull(handler);
    }
}

