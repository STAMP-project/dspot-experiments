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


public class InternalLoggerFactoryTest {
    private static final Exception e = new Exception();

    private InternalLoggerFactory oldLoggerFactory;

    private InternalLogger mockLogger;

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullDefaultFactory() {
        InternalLoggerFactory.setDefaultFactory(null);
    }

    @Test
    public void shouldGetInstance() {
        InternalLoggerFactory.setDefaultFactory(oldLoggerFactory);
        String helloWorld = "Hello, world!";
        InternalLogger one = InternalLoggerFactory.getInstance("helloWorld");
        InternalLogger two = InternalLoggerFactory.getInstance(helloWorld.getClass());
        Assert.assertNotNull(one);
        Assert.assertNotNull(two);
        Assert.assertNotSame(one, two);
    }

    @Test
    public void testIsTraceEnabled() {
        Mockito.when(mockLogger.isTraceEnabled()).thenReturn(true);
        InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        Assert.assertTrue(logger.isTraceEnabled());
        Mockito.verify(mockLogger).isTraceEnabled();
    }

    @Test
    public void testIsDebugEnabled() {
        Mockito.when(mockLogger.isDebugEnabled()).thenReturn(true);
        InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        Assert.assertTrue(logger.isDebugEnabled());
        Mockito.verify(mockLogger).isDebugEnabled();
    }

    @Test
    public void testIsInfoEnabled() {
        Mockito.when(mockLogger.isInfoEnabled()).thenReturn(true);
        InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        Assert.assertTrue(logger.isInfoEnabled());
        Mockito.verify(mockLogger).isInfoEnabled();
    }

    @Test
    public void testIsWarnEnabled() {
        Mockito.when(mockLogger.isWarnEnabled()).thenReturn(true);
        InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        Assert.assertTrue(logger.isWarnEnabled());
        Mockito.verify(mockLogger).isWarnEnabled();
    }

    @Test
    public void testIsErrorEnabled() {
        Mockito.when(mockLogger.isErrorEnabled()).thenReturn(true);
        InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        Assert.assertTrue(logger.isErrorEnabled());
        Mockito.verify(mockLogger).isErrorEnabled();
    }

    @Test
    public void testTrace() {
        final InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        logger.trace("a");
        Mockito.verify(mockLogger).trace("a");
    }

    @Test
    public void testTraceWithException() {
        final InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        logger.trace("a", InternalLoggerFactoryTest.e);
        Mockito.verify(mockLogger).trace("a", InternalLoggerFactoryTest.e);
    }

    @Test
    public void testDebug() {
        final InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        logger.debug("a");
        Mockito.verify(mockLogger).debug("a");
    }

    @Test
    public void testDebugWithException() {
        final InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        logger.debug("a", InternalLoggerFactoryTest.e);
        Mockito.verify(mockLogger).debug("a", InternalLoggerFactoryTest.e);
    }

    @Test
    public void testInfo() {
        final InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        logger.info("a");
        Mockito.verify(mockLogger).info("a");
    }

    @Test
    public void testInfoWithException() {
        final InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        logger.info("a", InternalLoggerFactoryTest.e);
        Mockito.verify(mockLogger).info("a", InternalLoggerFactoryTest.e);
    }

    @Test
    public void testWarn() {
        final InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        logger.warn("a");
        Mockito.verify(mockLogger).warn("a");
    }

    @Test
    public void testWarnWithException() {
        final InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        logger.warn("a", InternalLoggerFactoryTest.e);
        Mockito.verify(mockLogger).warn("a", InternalLoggerFactoryTest.e);
    }

    @Test
    public void testError() {
        final InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        logger.error("a");
        Mockito.verify(mockLogger).error("a");
    }

    @Test
    public void testErrorWithException() {
        final InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        logger.error("a", InternalLoggerFactoryTest.e);
        Mockito.verify(mockLogger).error("a", InternalLoggerFactoryTest.e);
    }
}

