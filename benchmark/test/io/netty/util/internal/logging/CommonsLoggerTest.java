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


import org.apache.commons.logging.Log;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class CommonsLoggerTest {
    private static final Exception e = new Exception();

    @Test
    public void testIsTraceEnabled() {
        Log mockLog = Mockito.mock(Log.class);
        Mockito.when(mockLog.isTraceEnabled()).thenReturn(true);
        InternalLogger logger = new CommonsLogger(mockLog, "foo");
        Assert.assertTrue(logger.isTraceEnabled());
        Mockito.verify(mockLog).isTraceEnabled();
    }

    @Test
    public void testIsDebugEnabled() {
        Log mockLog = Mockito.mock(Log.class);
        Mockito.when(mockLog.isDebugEnabled()).thenReturn(true);
        InternalLogger logger = new CommonsLogger(mockLog, "foo");
        Assert.assertTrue(logger.isDebugEnabled());
        Mockito.verify(mockLog).isDebugEnabled();
    }

    @Test
    public void testIsInfoEnabled() {
        Log mockLog = Mockito.mock(Log.class);
        Mockito.when(mockLog.isInfoEnabled()).thenReturn(true);
        InternalLogger logger = new CommonsLogger(mockLog, "foo");
        Assert.assertTrue(logger.isInfoEnabled());
        Mockito.verify(mockLog).isInfoEnabled();
    }

    @Test
    public void testIsWarnEnabled() {
        Log mockLog = Mockito.mock(Log.class);
        Mockito.when(mockLog.isWarnEnabled()).thenReturn(true);
        InternalLogger logger = new CommonsLogger(mockLog, "foo");
        Assert.assertTrue(logger.isWarnEnabled());
        Mockito.verify(mockLog).isWarnEnabled();
    }

    @Test
    public void testIsErrorEnabled() {
        Log mockLog = Mockito.mock(Log.class);
        Mockito.when(mockLog.isErrorEnabled()).thenReturn(true);
        InternalLogger logger = new CommonsLogger(mockLog, "foo");
        Assert.assertTrue(logger.isErrorEnabled());
        Mockito.verify(mockLog).isErrorEnabled();
    }

    @Test
    public void testTrace() {
        Log mockLog = Mockito.mock(Log.class);
        InternalLogger logger = new CommonsLogger(mockLog, "foo");
        logger.trace("a");
        Mockito.verify(mockLog).trace("a");
    }

    @Test
    public void testTraceWithException() {
        Log mockLog = Mockito.mock(Log.class);
        InternalLogger logger = new CommonsLogger(mockLog, "foo");
        logger.trace("a", CommonsLoggerTest.e);
        Mockito.verify(mockLog).trace("a", CommonsLoggerTest.e);
    }

    @Test
    public void testDebug() {
        Log mockLog = Mockito.mock(Log.class);
        InternalLogger logger = new CommonsLogger(mockLog, "foo");
        logger.debug("a");
        Mockito.verify(mockLog).debug("a");
    }

    @Test
    public void testDebugWithException() {
        Log mockLog = Mockito.mock(Log.class);
        InternalLogger logger = new CommonsLogger(mockLog, "foo");
        logger.debug("a", CommonsLoggerTest.e);
        Mockito.verify(mockLog).debug("a", CommonsLoggerTest.e);
    }

    @Test
    public void testInfo() {
        Log mockLog = Mockito.mock(Log.class);
        InternalLogger logger = new CommonsLogger(mockLog, "foo");
        logger.info("a");
        Mockito.verify(mockLog).info("a");
    }

    @Test
    public void testInfoWithException() {
        Log mockLog = Mockito.mock(Log.class);
        InternalLogger logger = new CommonsLogger(mockLog, "foo");
        logger.info("a", CommonsLoggerTest.e);
        Mockito.verify(mockLog).info("a", CommonsLoggerTest.e);
    }

    @Test
    public void testWarn() {
        Log mockLog = Mockito.mock(Log.class);
        InternalLogger logger = new CommonsLogger(mockLog, "foo");
        logger.warn("a");
        Mockito.verify(mockLog).warn("a");
    }

    @Test
    public void testWarnWithException() {
        Log mockLog = Mockito.mock(Log.class);
        InternalLogger logger = new CommonsLogger(mockLog, "foo");
        logger.warn("a", CommonsLoggerTest.e);
        Mockito.verify(mockLog).warn("a", CommonsLoggerTest.e);
    }

    @Test
    public void testError() {
        Log mockLog = Mockito.mock(Log.class);
        InternalLogger logger = new CommonsLogger(mockLog, "foo");
        logger.error("a");
        Mockito.verify(mockLog).error("a");
    }

    @Test
    public void testErrorWithException() {
        Log mockLog = Mockito.mock(Log.class);
        InternalLogger logger = new CommonsLogger(mockLog, "foo");
        logger.error("a", CommonsLoggerTest.e);
        Mockito.verify(mockLog).error("a", CommonsLoggerTest.e);
    }
}

