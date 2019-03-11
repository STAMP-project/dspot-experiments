/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2016, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.classic.jmx;


import Level.DEBUG;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.testUtil.RandomUtil;
import java.lang.management.ManagementFactory;
import java.util.List;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.junit.Assert;
import org.junit.Test;

import static org.slf4j.Logger.ROOT_LOGGER_NAME;


public class JMXConfiguratorTest {
    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

    LoggerContext lc = new LoggerContext();

    Logger testLogger = lc.getLogger(this.getClass());

    List<LoggerContextListener> listenerList;

    int diff = RandomUtil.getPositiveInt();

    @Test
    public void contextReset() throws Exception {
        String randomizedObjectNameAsStr = (((("ch.qos.logback." + (diff)) + ":Name=") + (lc.getName())) + ",Type=") + (this.getClass().getName());
        ObjectName objectName = MBeanUtil.string2ObjectName(lc, this, randomizedObjectNameAsStr);
        JMXConfigurator jmxConfigurator = new JMXConfigurator(lc, mbs, objectName);
        mbs.registerMBean(jmxConfigurator, objectName);
        listenerList = lc.getCopyOfListenerList();
        Assert.assertEquals(1, listenerList.size());
        lc.reset();
        // check that after lc.reset, jmxConfigurator should still be
        // registered as a listener in the loggerContext and also as an
        // MBean in mbs
        listenerList = lc.getCopyOfListenerList();
        Assert.assertEquals(1, listenerList.size());
        Assert.assertTrue(listenerList.contains(jmxConfigurator));
        Assert.assertTrue(mbs.isRegistered(objectName));
    }

    @Test
    public void contextStop() throws Exception {
        String randomizedObjectNameAsStr = (((("ch.qos.logback." + (diff)) + ":Name=") + (lc.getName())) + ",Type=") + (this.getClass().getName());
        ObjectName objectName = MBeanUtil.string2ObjectName(lc, this, randomizedObjectNameAsStr);
        JMXConfigurator jmxConfigurator = new JMXConfigurator(lc, mbs, objectName);
        mbs.registerMBean(jmxConfigurator, objectName);
        listenerList = lc.getCopyOfListenerList();
        Assert.assertEquals(1, listenerList.size());
        lc.stop();
        // check that after lc.processPriorToRemoval, jmxConfigurator is no longer
        // registered as a listener in the loggerContext nor as an
        // MBean in mbs
        listenerList = lc.getCopyOfListenerList();
        Assert.assertEquals(0, listenerList.size());
        Assert.assertFalse(mbs.isRegistered(objectName));
    }

    @Test
    public void testNonRemovalOfPreviousIntanceFromTheContextListenerList() {
        String objectNameAsStr = ((("ch.qos.logback.toto" + ":Name=") + (lc.getName())) + ",Type=") + (this.getClass().getName());
        ObjectName objectName = MBeanUtil.string2ObjectName(lc, this, objectNameAsStr);
        JMXConfigurator jmxConfigurator0 = new JMXConfigurator(lc, mbs, objectName);
        listenerList = lc.getCopyOfListenerList();
        Assert.assertTrue(listenerList.contains(jmxConfigurator0));
        JMXConfigurator jmxConfigurator1 = new JMXConfigurator(lc, mbs, objectName);
        listenerList = lc.getCopyOfListenerList();
        Assert.assertEquals(1, listenerList.size());
        Assert.assertTrue("old configurator should be present", listenerList.contains(jmxConfigurator0));
        Assert.assertFalse("new configurator should be absent", listenerList.contains(jmxConfigurator1));
    }

    @Test
    public void getLoggerLevel_LBCLASSIC_78() {
        String objectNameAsStr = (((("ch.qos" + (diff)) + ":Name=") + (lc.getName())) + ",Type=") + (this.getClass().getName());
        ObjectName on = MBeanUtil.string2ObjectName(lc, this, objectNameAsStr);
        JMXConfigurator configurator = new JMXConfigurator(lc, mbs, on);
        Assert.assertEquals("", configurator.getLoggerLevel(testLogger.getName()));
        MBeanUtil.unregister(lc, mbs, on, this);
    }

    @Test
    public void setLoggerLevel_LBCLASSIC_79() {
        String objectNameAsStr = (((("ch.qos" + (diff)) + ":Name=") + (lc.getName())) + ",Type=") + (this.getClass().getName());
        ObjectName on = MBeanUtil.string2ObjectName(lc, this, objectNameAsStr);
        JMXConfigurator configurator = new JMXConfigurator(lc, mbs, on);
        configurator.setLoggerLevel(testLogger.getName(), "DEBUG");
        Assert.assertEquals(DEBUG, testLogger.getLevel());
        configurator.setLoggerLevel(testLogger.getName(), "null");
        Assert.assertNull(testLogger.getLevel());
        MBeanUtil.unregister(lc, mbs, on, this);
    }

    @Test
    public void testReloadDefaultConfiguration() throws Exception {
        String objectNameAsStr = (((("ch.qos" + (diff)) + ":Name=") + (lc.getName())) + ",Type=") + (this.getClass().getName());
        ObjectName on = MBeanUtil.string2ObjectName(lc, this, objectNameAsStr);
        JMXConfigurator configurator = new JMXConfigurator(lc, mbs, on);
        configurator.setLoggerLevel(testLogger.getName(), "DEBUG");
        Assert.assertEquals(DEBUG, testLogger.getLevel());
        configurator.reloadDefaultConfiguration();
        Assert.assertNull(testLogger.getLevel());
        Assert.assertEquals(DEBUG, lc.getLogger(ROOT_LOGGER_NAME).getLevel());
        MBeanUtil.unregister(lc, mbs, on, this);
    }
}

