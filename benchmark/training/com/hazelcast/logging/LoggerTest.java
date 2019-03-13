/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.logging;


import Log4j2Factory.Log4j2Logger;
import Log4jFactory.Log4jLogger;
import NoLogFactory.NoLogger;
import Slf4jFactory.Slf4jLogger;
import StandardLoggerFactory.StandardLogger;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.IsolatedLoggingRule;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Unit tests for {@link com.hazelcast.logging.Logger} class.
 */
@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class LoggerTest extends HazelcastTestSupport {
    @Rule
    public final IsolatedLoggingRule isolatedLoggingRule = new IsolatedLoggingRule();

    @Test
    public void testConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(Logger.class);
    }

    @Test
    public void getLogger_thenLog4j_thenReturnLog4jLogger() {
        isolatedLoggingRule.setLoggingType(IsolatedLoggingRule.LOGGING_TYPE_LOG4J);
        HazelcastTestSupport.assertInstanceOf(Log4jLogger.class, Logger.getLogger(getClass()));
    }

    @Test
    public void getLogger_thenLog4j2_thenReturnLog4j2Logger() {
        isolatedLoggingRule.setLoggingType(IsolatedLoggingRule.LOGGING_TYPE_LOG4J2);
        HazelcastTestSupport.assertInstanceOf(Log4j2Logger.class, Logger.getLogger(getClass()));
    }

    @Test
    public void getLogger_whenSlf4j_thenReturnSlf4jLogger() {
        isolatedLoggingRule.setLoggingType(IsolatedLoggingRule.LOGGING_TYPE_SLF4J);
        HazelcastTestSupport.assertInstanceOf(Slf4jLogger.class, Logger.getLogger(getClass()));
    }

    @Test
    public void getLogger_whenJdk_thenReturnStandardLogger() {
        isolatedLoggingRule.setLoggingType(IsolatedLoggingRule.LOGGING_TYPE_JDK);
        HazelcastTestSupport.assertInstanceOf(StandardLogger.class, Logger.getLogger(getClass()));
    }

    @Test
    public void getLogger_whenNone_thenReturnNoLogger() {
        isolatedLoggingRule.setLoggingType(IsolatedLoggingRule.LOGGING_TYPE_NONE);
        HazelcastTestSupport.assertInstanceOf(NoLogger.class, Logger.getLogger(getClass()));
    }

    @Test
    public void getLogger_whenInvalidConfiguration_thenCreateStandardLogger() {
        isolatedLoggingRule.setLoggingType("invalid");
        HazelcastTestSupport.assertInstanceOf(StandardLogger.class, Logger.getLogger(getClass()));
    }

    @Test
    public void noLogger() {
        HazelcastTestSupport.assertInstanceOf(NoLogger.class, Logger.noLogger());
    }

    @Test
    public void getLogger_whenTypeConfiguredForInstance_thenReturnLoggerOfConfiguredType() {
        final ILogger loggerBeforeInstanceStartup = Logger.getLogger(getClass());
        final Config config = new Config();
        config.setProperty(IsolatedLoggingRule.LOGGING_TYPE_PROPERTY, IsolatedLoggingRule.LOGGING_TYPE_LOG4J2);
        final HazelcastInstance instance = Hazelcast.newHazelcastInstance(config);
        try {
            final ILogger loggerAfterInstanceStartup = Logger.getLogger(getClass());
            HazelcastTestSupport.assertInstanceOf(StandardLogger.class, loggerBeforeInstanceStartup);
            HazelcastTestSupport.assertInstanceOf(Log4j2Logger.class, loggerAfterInstanceStartup);
        } finally {
            instance.shutdown();
        }
    }

    @Test
    public void newLoggerFactory_whenClassConfigured_thenShareLoggerFactoryWithGetLogger() {
        isolatedLoggingRule.setLoggingClass(Log4j2Factory.class);
        final ILogger loggerViaGetLogger = Logger.getLogger(getClass().getName());
        final LoggerFactory loggerFactory = Logger.newLoggerFactory("irrelevant");
        final ILogger loggerViaFactory = loggerFactory.getLogger(getClass().getName());
        HazelcastTestSupport.assertInstanceOf(Log4j2Logger.class, loggerViaGetLogger);
        HazelcastTestSupport.assertInstanceOf(Log4j2Logger.class, loggerViaFactory);
        Assert.assertEquals(loggerFactory, isolatedLoggingRule.getLoggerFactory());
    }

    @Test
    public void newLoggerFactory_whenTypeConfigured_thenShareLoggerFactoryWithGetLoggerIfTypesMatch() {
        isolatedLoggingRule.setLoggingType(IsolatedLoggingRule.LOGGING_TYPE_LOG4J2);
        final ILogger loggerViaGetLogger = Logger.getLogger(getClass().getName());
        final LoggerFactory loggerFactory = Logger.newLoggerFactory(IsolatedLoggingRule.LOGGING_TYPE_LOG4J2);
        final ILogger loggerViaFactory = loggerFactory.getLogger(getClass().getName());
        HazelcastTestSupport.assertInstanceOf(Log4j2Logger.class, loggerViaGetLogger);
        HazelcastTestSupport.assertInstanceOf(Log4j2Logger.class, loggerViaFactory);
        Assert.assertEquals(loggerFactory, isolatedLoggingRule.getLoggerFactory());
    }

    @Test
    public void newLoggerFactory_whenTypeConfigured_thenDoNotShareLoggerFactoryWithGetLoggerIfTypesDoNotMatch() {
        isolatedLoggingRule.setLoggingType(IsolatedLoggingRule.LOGGING_TYPE_LOG4J2);
        final ILogger loggerViaGetLogger = Logger.getLogger(getClass().getName());
        final LoggerFactory loggerFactory = Logger.newLoggerFactory(IsolatedLoggingRule.LOGGING_TYPE_LOG4J);
        final ILogger loggerViaFactory = loggerFactory.getLogger(getClass().getName());
        HazelcastTestSupport.assertInstanceOf(Log4j2Logger.class, loggerViaGetLogger);
        HazelcastTestSupport.assertInstanceOf(Log4jLogger.class, loggerViaFactory);
        Assert.assertNotEquals(loggerFactory, isolatedLoggingRule.getLoggerFactory());
    }
}

