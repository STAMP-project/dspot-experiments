/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.broker.jmx;


import java.util.List;
import javax.management.MBeanServer;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import org.apache.activemq.EmbeddedBrokerTestSupport;
import org.apache.log4j.Level;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.log4j.Logger.getRootLogger;


public class Log4JConfigTest extends EmbeddedBrokerTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(Log4JConfigTest.class);

    private static final String BROKER_LOGGER = "org.apache.activemq.broker.BrokerService";

    protected MBeanServer mbeanServer;

    protected String domain = "org.apache.activemq";

    @Test
    public void testLog4JConfigViewExists() throws Exception {
        String brokerObjectName = broker.getBrokerObjectName().toString();
        String log4jConfigViewName = BrokerMBeanSupport.createLog4JConfigViewName(brokerObjectName).toString();
        assertRegisteredObjectName(log4jConfigViewName);
    }

    @Test
    public void testLog4JConfigViewGetLoggers() throws Throwable {
        String brokerObjectName = broker.getBrokerObjectName().toString();
        ObjectName log4jConfigViewName = BrokerMBeanSupport.createLog4JConfigViewName(brokerObjectName);
        Log4JConfigViewMBean log4jConfigView = MBeanServerInvocationHandler.newProxyInstance(mbeanServer, log4jConfigViewName, Log4JConfigViewMBean.class, true);
        List<String> loggers = log4jConfigView.getLoggers();
        assertNotNull(loggers);
        assertFalse(loggers.isEmpty());
    }

    @Test
    public void testLog4JConfigViewGetLevel() throws Throwable {
        String brokerObjectName = broker.getBrokerObjectName().toString();
        ObjectName log4jConfigViewName = BrokerMBeanSupport.createLog4JConfigViewName(brokerObjectName);
        Log4JConfigViewMBean log4jConfigView = MBeanServerInvocationHandler.newProxyInstance(mbeanServer, log4jConfigViewName, Log4JConfigViewMBean.class, true);
        String level = log4jConfigView.getLogLevel(Log4JConfigTest.BROKER_LOGGER);
        assertNotNull(level);
        assertFalse(level.isEmpty());
    }

    @Test
    public void testLog4JConfigViewGetLevelUnknownLoggerName() throws Throwable {
        String brokerObjectName = broker.getBrokerObjectName().toString();
        ObjectName log4jConfigViewName = BrokerMBeanSupport.createLog4JConfigViewName(brokerObjectName);
        Log4JConfigViewMBean log4jConfigView = MBeanServerInvocationHandler.newProxyInstance(mbeanServer, log4jConfigViewName, Log4JConfigViewMBean.class, true);
        // Non-existent loggers will return a name equal to the root level.
        String level = log4jConfigView.getLogLevel("not.a.logger");
        assertNotNull(level);
        assertFalse(level.isEmpty());
        assertEquals(getRootLogger().getLevel().toString(), level);
    }

    @Test
    public void testLog4JConfigViewSetLevel() throws Throwable {
        String brokerObjectName = broker.getBrokerObjectName().toString();
        ObjectName log4jConfigViewName = BrokerMBeanSupport.createLog4JConfigViewName(brokerObjectName);
        Log4JConfigViewMBean log4jConfigView = MBeanServerInvocationHandler.newProxyInstance(mbeanServer, log4jConfigViewName, Log4JConfigViewMBean.class, true);
        String level = log4jConfigView.getLogLevel(Log4JConfigTest.BROKER_LOGGER);
        assertNotNull(level);
        assertFalse(level.isEmpty());
        log4jConfigView.setLogLevel(Log4JConfigTest.BROKER_LOGGER, "WARN");
        level = log4jConfigView.getLogLevel(Log4JConfigTest.BROKER_LOGGER);
        assertNotNull(level);
        assertEquals("WARN", level);
        log4jConfigView.setLogLevel(Log4JConfigTest.BROKER_LOGGER, "INFO");
        level = log4jConfigView.getLogLevel(Log4JConfigTest.BROKER_LOGGER);
        assertNotNull(level);
        assertEquals("INFO", level);
    }

    @Test
    public void testLog4JConfigViewSetLevelNoChangeIfLevelIsBad() throws Throwable {
        String brokerObjectName = broker.getBrokerObjectName().toString();
        ObjectName log4jConfigViewName = BrokerMBeanSupport.createLog4JConfigViewName(brokerObjectName);
        Log4JConfigViewMBean log4jConfigView = MBeanServerInvocationHandler.newProxyInstance(mbeanServer, log4jConfigViewName, Log4JConfigViewMBean.class, true);
        log4jConfigView.setLogLevel(Log4JConfigTest.BROKER_LOGGER, "INFO");
        String level = log4jConfigView.getLogLevel(Log4JConfigTest.BROKER_LOGGER);
        assertNotNull(level);
        assertEquals("INFO", level);
        log4jConfigView.setLogLevel(Log4JConfigTest.BROKER_LOGGER, "BAD");
        level = log4jConfigView.getLogLevel(Log4JConfigTest.BROKER_LOGGER);
        assertNotNull(level);
        assertEquals("INFO", level);
    }

    @Test
    public void testLog4JConfigViewGetRootLogLevel() throws Throwable {
        String brokerObjectName = broker.getBrokerObjectName().toString();
        ObjectName log4jConfigViewName = BrokerMBeanSupport.createLog4JConfigViewName(brokerObjectName);
        Log4JConfigViewMBean log4jConfigView = MBeanServerInvocationHandler.newProxyInstance(mbeanServer, log4jConfigViewName, Log4JConfigViewMBean.class, true);
        String level = log4jConfigView.getRootLogLevel();
        assertNotNull(level);
        assertFalse(level.isEmpty());
        String currentRootLevel = getRootLogger().getLevel().toString();
        assertEquals(currentRootLevel, level);
    }

    @Test
    public void testLog4JConfigViewSetRootLevel() throws Throwable {
        String brokerObjectName = broker.getBrokerObjectName().toString();
        ObjectName log4jConfigViewName = BrokerMBeanSupport.createLog4JConfigViewName(brokerObjectName);
        Log4JConfigViewMBean log4jConfigView = MBeanServerInvocationHandler.newProxyInstance(mbeanServer, log4jConfigViewName, Log4JConfigViewMBean.class, true);
        String currentRootLevel = getRootLogger().getLevel().toString();
        log4jConfigView.setRootLogLevel("WARN");
        currentRootLevel = getRootLogger().getLevel().toString();
        assertEquals("WARN", currentRootLevel);
        log4jConfigView.setRootLogLevel("INFO");
        currentRootLevel = getRootLogger().getLevel().toString();
        assertEquals("INFO", currentRootLevel);
        Level level;
    }
}

