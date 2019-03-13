/**
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util.internal.logging;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;


public class Slf4JLoggerTest {
    private static final Exception e = new Exception();

    @Test
    public void testIsTraceEnabled() {
        Logger mockLogger = Mockito.mock(Logger.class);
        Mockito.when(mockLogger.getName()).thenReturn("foo");
        Mockito.when(mockLogger.isTraceEnabled()).thenReturn(true);
        InternalLogger logger = new Slf4JLogger(mockLogger);
        Assert.assertTrue(logger.isTraceEnabled());
        Mockito.verify(mockLogger).getName();
        Mockito.verify(mockLogger).isTraceEnabled();
    }

    @Test
    public void testIsDebugEnabled() {
        Logger mockLogger = Mockito.mock(Logger.class);
        Mockito.when(mockLogger.getName()).thenReturn("foo");
        Mockito.when(mockLogger.isDebugEnabled()).thenReturn(true);
        InternalLogger logger = new Slf4JLogger(mockLogger);
        Assert.assertTrue(logger.isDebugEnabled());
        Mockito.verify(mockLogger).getName();
        Mockito.verify(mockLogger).isDebugEnabled();
    }

    @Test
    public void testIsInfoEnabled() {
        Logger mockLogger = Mockito.mock(Logger.class);
        Mockito.when(mockLogger.getName()).thenReturn("foo");
        Mockito.when(mockLogger.isInfoEnabled()).thenReturn(true);
        InternalLogger logger = new Slf4JLogger(mockLogger);
        Assert.assertTrue(logger.isInfoEnabled());
        Mockito.verify(mockLogger).getName();
        Mockito.verify(mockLogger).isInfoEnabled();
    }

    @Test
    public void testIsWarnEnabled() {
        Logger mockLogger = Mockito.mock(Logger.class);
        Mockito.when(mockLogger.getName()).thenReturn("foo");
        Mockito.when(mockLogger.isWarnEnabled()).thenReturn(true);
        InternalLogger logger = new Slf4JLogger(mockLogger);
        Assert.assertTrue(logger.isWarnEnabled());
        Mockito.verify(mockLogger).getName();
        Mockito.verify(mockLogger).isWarnEnabled();
    }

    @Test
    public void testIsErrorEnabled() {
        Logger mockLogger = Mockito.mock(Logger.class);
        Mockito.when(mockLogger.getName()).thenReturn("foo");
        Mockito.when(mockLogger.isErrorEnabled()).thenReturn(true);
        InternalLogger logger = new Slf4JLogger(mockLogger);
        Assert.assertTrue(logger.isErrorEnabled());
        Mockito.verify(mockLogger).getName();
        Mockito.verify(mockLogger).isErrorEnabled();
    }

    @Test
    public void testTrace() {
        Logger mockLogger = Mockito.mock(Logger.class);
        Mockito.when(mockLogger.getName()).thenReturn("foo");
        InternalLogger logger = new Slf4JLogger(mockLogger);
        logger.trace("a");
        Mockito.verify(mockLogger).getName();
        Mockito.verify(mockLogger).trace("a");
    }

    @Test
    public void testTraceWithException() {
        Logger mockLogger = Mockito.mock(Logger.class);
        Mockito.when(mockLogger.getName()).thenReturn("foo");
        InternalLogger logger = new Slf4JLogger(mockLogger);
        logger.trace("a", Slf4JLoggerTest.e);
        Mockito.verify(mockLogger).getName();
        Mockito.verify(mockLogger).trace("a", Slf4JLoggerTest.e);
    }

    @Test
    public void testDebug() {
        Logger mockLogger = Mockito.mock(Logger.class);
        Mockito.when(mockLogger.getName()).thenReturn("foo");
        InternalLogger logger = new Slf4JLogger(mockLogger);
        logger.debug("a");
        Mockito.verify(mockLogger).getName();
        Mockito.verify(mockLogger).debug("a");
    }

    @Test
    public void testDebugWithException() {
        Logger mockLogger = Mockito.mock(Logger.class);
        Mockito.when(mockLogger.getName()).thenReturn("foo");
        InternalLogger logger = new Slf4JLogger(mockLogger);
        logger.debug("a", Slf4JLoggerTest.e);
        Mockito.verify(mockLogger).getName();
        Mockito.verify(mockLogger).debug("a", Slf4JLoggerTest.e);
    }

    @Test
    public void testInfo() {
        Logger mockLogger = Mockito.mock(Logger.class);
        Mockito.when(mockLogger.getName()).thenReturn("foo");
        InternalLogger logger = new Slf4JLogger(mockLogger);
        logger.info("a");
        Mockito.verify(mockLogger).getName();
        Mockito.verify(mockLogger).info("a");
    }

    @Test
    public void testInfoWithException() {
        Logger mockLogger = Mockito.mock(Logger.class);
        Mockito.when(mockLogger.getName()).thenReturn("foo");
        InternalLogger logger = new Slf4JLogger(mockLogger);
        logger.info("a", Slf4JLoggerTest.e);
        Mockito.verify(mockLogger).getName();
        Mockito.verify(mockLogger).info("a", Slf4JLoggerTest.e);
    }

    @Test
    public void testWarn() {
        Logger mockLogger = Mockito.mock(Logger.class);
        Mockito.when(mockLogger.getName()).thenReturn("foo");
        InternalLogger logger = new Slf4JLogger(mockLogger);
        logger.warn("a");
        Mockito.verify(mockLogger).getName();
        Mockito.verify(mockLogger).warn("a");
    }

    @Test
    public void testWarnWithException() {
        Logger mockLogger = Mockito.mock(Logger.class);
        Mockito.when(mockLogger.getName()).thenReturn("foo");
        InternalLogger logger = new Slf4JLogger(mockLogger);
        logger.warn("a", Slf4JLoggerTest.e);
        Mockito.verify(mockLogger).getName();
        Mockito.verify(mockLogger).warn("a", Slf4JLoggerTest.e);
    }

    @Test
    public void testError() {
        Logger mockLogger = Mockito.mock(Logger.class);
        Mockito.when(mockLogger.getName()).thenReturn("foo");
        InternalLogger logger = new Slf4JLogger(mockLogger);
        logger.error("a");
        Mockito.verify(mockLogger).getName();
        Mockito.verify(mockLogger).error("a");
    }

    @Test
    public void testErrorWithException() {
        Logger mockLogger = Mockito.mock(Logger.class);
        Mockito.when(mockLogger.getName()).thenReturn("foo");
        InternalLogger logger = new Slf4JLogger(mockLogger);
        logger.error("a", Slf4JLoggerTest.e);
        Mockito.verify(mockLogger).getName();
        Mockito.verify(mockLogger).error("a", Slf4JLoggerTest.e);
    }
}

