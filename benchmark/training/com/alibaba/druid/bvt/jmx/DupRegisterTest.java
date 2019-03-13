package com.alibaba.druid.bvt.jmx;


import com.alibaba.druid.pool.DruidDataSource;
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import junit.framework.TestCase;
import org.junit.Assert;


public class DupRegisterTest extends TestCase {
    private DruidDataSource dataSource;

    public void test_0() throws Exception {
        dataSource.init();
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        mbeanServer.registerMBean(dataSource, dataSource.getObjectName());
        Assert.assertTrue(mbeanServer.isRegistered(dataSource.getObjectName()));
    }
}

