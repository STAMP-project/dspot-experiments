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


import LocationAwareSlf4JLogger.FQCN;
import Slf4JLoggerFactory.INSTANCE;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.spi.LocationAwareLogger;


public class Slf4JLoggerFactoryTest {
    @Test
    public void testCreation() {
        InternalLogger logger = INSTANCE.newInstance("foo");
        Assert.assertTrue(((logger instanceof Slf4JLogger) || (logger instanceof LocationAwareSlf4JLogger)));
        Assert.assertEquals("foo", logger.name());
    }

    @Test
    public void testCreationLogger() {
        Logger logger = Mockito.mock(Logger.class);
        Mockito.when(logger.getName()).thenReturn("testlogger");
        InternalLogger internalLogger = Slf4JLoggerFactory.wrapLogger(logger);
        Assert.assertTrue((internalLogger instanceof Slf4JLogger));
        Assert.assertEquals("testlogger", internalLogger.name());
    }

    @Test
    public void testCreationLocationAwareLogger() {
        Logger logger = Mockito.mock(LocationAwareLogger.class);
        Mockito.when(logger.getName()).thenReturn("testlogger");
        InternalLogger internalLogger = Slf4JLoggerFactory.wrapLogger(logger);
        Assert.assertTrue((internalLogger instanceof LocationAwareSlf4JLogger));
        Assert.assertEquals("testlogger", internalLogger.name());
    }

    @Test
    public void testFormatMessage() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        LocationAwareLogger logger = Mockito.mock(LocationAwareLogger.class);
        Mockito.when(logger.isDebugEnabled()).thenReturn(true);
        Mockito.when(logger.isErrorEnabled()).thenReturn(true);
        Mockito.when(logger.isInfoEnabled()).thenReturn(true);
        Mockito.when(logger.isTraceEnabled()).thenReturn(true);
        Mockito.when(logger.isWarnEnabled()).thenReturn(true);
        Mockito.when(logger.getName()).thenReturn("testlogger");
        InternalLogger internalLogger = Slf4JLoggerFactory.wrapLogger(logger);
        internalLogger.debug("{}", "debug");
        internalLogger.debug("{} {}", "debug1", "debug2");
        internalLogger.debug("{} {} {}", "debug1", "debug2", "debug3");
        internalLogger.error("{}", "error");
        internalLogger.error("{} {}", "error1", "error2");
        internalLogger.error("{} {} {}", "error1", "error2", "error3");
        internalLogger.info("{}", "info");
        internalLogger.info("{} {}", "info1", "info2");
        internalLogger.info("{} {} {}", "info1", "info2", "info3");
        internalLogger.trace("{}", "trace");
        internalLogger.trace("{} {}", "trace1", "trace2");
        internalLogger.trace("{} {} {}", "trace1", "trace2", "trace3");
        internalLogger.warn("{}", "warn");
        internalLogger.warn("{} {}", "warn1", "warn2");
        internalLogger.warn("{} {} {}", "warn1", "warn2", "warn3");
        Mockito.verify(logger, Mockito.times(3)).log(ArgumentMatchers.<Marker>isNull(), ArgumentMatchers.eq(FQCN), ArgumentMatchers.eq(LocationAwareLogger.DEBUG_INT), captor.capture(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.<Throwable>isNull());
        Mockito.verify(logger, Mockito.times(3)).log(ArgumentMatchers.<Marker>isNull(), ArgumentMatchers.eq(FQCN), ArgumentMatchers.eq(LocationAwareLogger.ERROR_INT), captor.capture(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.<Throwable>isNull());
        Mockito.verify(logger, Mockito.times(3)).log(ArgumentMatchers.<Marker>isNull(), ArgumentMatchers.eq(FQCN), ArgumentMatchers.eq(LocationAwareLogger.INFO_INT), captor.capture(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.<Throwable>isNull());
        Mockito.verify(logger, Mockito.times(3)).log(ArgumentMatchers.<Marker>isNull(), ArgumentMatchers.eq(FQCN), ArgumentMatchers.eq(LocationAwareLogger.TRACE_INT), captor.capture(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.<Throwable>isNull());
        Mockito.verify(logger, Mockito.times(3)).log(ArgumentMatchers.<Marker>isNull(), ArgumentMatchers.eq(FQCN), ArgumentMatchers.eq(LocationAwareLogger.WARN_INT), captor.capture(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.<Throwable>isNull());
        Iterator<String> logMessages = captor.getAllValues().iterator();
        Assert.assertEquals("debug", logMessages.next());
        Assert.assertEquals("debug1 debug2", logMessages.next());
        Assert.assertEquals("debug1 debug2 debug3", logMessages.next());
        Assert.assertEquals("error", logMessages.next());
        Assert.assertEquals("error1 error2", logMessages.next());
        Assert.assertEquals("error1 error2 error3", logMessages.next());
        Assert.assertEquals("info", logMessages.next());
        Assert.assertEquals("info1 info2", logMessages.next());
        Assert.assertEquals("info1 info2 info3", logMessages.next());
        Assert.assertEquals("trace", logMessages.next());
        Assert.assertEquals("trace1 trace2", logMessages.next());
        Assert.assertEquals("trace1 trace2 trace3", logMessages.next());
        Assert.assertEquals("warn", logMessages.next());
        Assert.assertEquals("warn1 warn2", logMessages.next());
        Assert.assertEquals("warn1 warn2 warn3", logMessages.next());
        Assert.assertFalse(logMessages.hasNext());
    }
}

