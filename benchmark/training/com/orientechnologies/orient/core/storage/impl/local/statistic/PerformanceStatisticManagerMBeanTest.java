package com.orientechnologies.orient.core.storage.impl.local.statistic;


import java.util.Arrays;
import java.util.HashSet;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class PerformanceStatisticManagerMBeanTest {
    @Test
    public void testMbeanInfo() {
        final OPerformanceStatisticManager manager = Mockito.mock(OPerformanceStatisticManager.class);
        Mockito.when(manager.getComponentNames()).thenReturn(new HashSet<String>(Arrays.asList("com1", "com2")));
        final OPerformanceStatisticManagerMBean mBean = new OPerformanceStatisticManagerMBean(manager);
        final MBeanInfo mBeanInfo = mBean.getMBeanInfo();
        final MBeanOperationInfo[] operations = mBeanInfo.getOperations();
        Assert.assertEquals(operations.length, 2);
        assertOperation(operations, "startMonitoring");
        assertOperation(operations, "stopMonitoring");
        final MBeanAttributeInfo[] attributes = mBeanInfo.getAttributes();
        Assert.assertEquals(attributes.length, 30);
        assertAttribute(attributes, "cacheHits", int.class);
        assertAttribute(attributes, "cacheHits_com1", int.class);
        assertAttribute(attributes, "cacheHits_com2", int.class);
        assertAttribute(attributes, "commitTime", long.class);
        assertAttribute(attributes, "readSpeedFromCache", long.class);
        assertAttribute(attributes, "readSpeedFromCache_com1", long.class);
        assertAttribute(attributes, "readSpeedFromCache_com2", long.class);
        assertAttribute(attributes, "readSpeedFromFile", long.class);
        assertAttribute(attributes, "readSpeedFromFile_com1", long.class);
        assertAttribute(attributes, "readSpeedFromFile_com2", long.class);
        assertAttribute(attributes, "writeSpeedInCache", long.class);
        assertAttribute(attributes, "writeSpeedInCache_com1", long.class);
        assertAttribute(attributes, "writeSpeedInCache_com2", long.class);
        assertAttribute(attributes, "writeCachePagesPerFlush", long.class);
        assertAttribute(attributes, "writeCacheFlushOperationTime", long.class);
        assertAttribute(attributes, "writeCacheFuzzyCheckpointTime", long.class);
        assertAttribute(attributes, "fullCheckpointTime", long.class);
        Assert.assertEquals(mBeanInfo.getConstructors().length, 0);
        Assert.assertEquals(mBeanInfo.getNotifications().length, 0);
    }
}

