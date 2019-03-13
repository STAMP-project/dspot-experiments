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
package org.jboss.netty.logging;


import org.junit.Assert;
import org.junit.Test;


public class InternalLoggerFactoryTest {
    private static final Exception e = new Exception();

    private InternalLoggerFactory oldLoggerFactory;

    private InternalLogger mock;

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullDefaultFactory() {
        InternalLoggerFactory.setDefaultFactory(null);
    }

    @Test
    public void shouldReturnWrappedLogger() {
        Assert.assertNotSame(mock, InternalLoggerFactory.getInstance("mock"));
    }

    @Test
    public void testIsDebugEnabled() {
        expect(mock.isDebugEnabled()).andReturn(true);
        replay(mock);
        InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        Assert.assertTrue(logger.isDebugEnabled());
        verify(mock);
    }

    @Test
    public void testIsInfoEnabled() {
        expect(mock.isInfoEnabled()).andReturn(true);
        replay(mock);
        InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        Assert.assertTrue(logger.isInfoEnabled());
        verify(mock);
    }

    @Test
    public void testIsWarnEnabled() {
        expect(mock.isWarnEnabled()).andReturn(true);
        replay(mock);
        InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        Assert.assertTrue(logger.isWarnEnabled());
        verify(mock);
    }

    @Test
    public void testIsErrorEnabled() {
        expect(mock.isErrorEnabled()).andReturn(true);
        replay(mock);
        InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        Assert.assertTrue(logger.isErrorEnabled());
        verify(mock);
    }

    @Test
    public void testDebug() {
        mock.debug("a");
        replay(mock);
        InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        logger.debug("a");
        verify(mock);
    }

    @Test
    public void testDebugWithException() {
        mock.debug("a", InternalLoggerFactoryTest.e);
        replay(mock);
        InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        logger.debug("a", InternalLoggerFactoryTest.e);
        verify(mock);
    }

    @Test
    public void testInfo() {
        mock.info("a");
        replay(mock);
        InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        logger.info("a");
        verify(mock);
    }

    @Test
    public void testInfoWithException() {
        mock.info("a", InternalLoggerFactoryTest.e);
        replay(mock);
        InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        logger.info("a", InternalLoggerFactoryTest.e);
        verify(mock);
    }

    @Test
    public void testWarn() {
        mock.warn("a");
        replay(mock);
        InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        logger.warn("a");
        verify(mock);
    }

    @Test
    public void testWarnWithException() {
        mock.warn("a", InternalLoggerFactoryTest.e);
        replay(mock);
        InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        logger.warn("a", InternalLoggerFactoryTest.e);
        verify(mock);
    }

    @Test
    public void testError() {
        mock.error("a");
        replay(mock);
        InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        logger.error("a");
        verify(mock);
    }

    @Test
    public void testErrorWithException() {
        mock.error("a", InternalLoggerFactoryTest.e);
        replay(mock);
        InternalLogger logger = InternalLoggerFactory.getInstance("mock");
        logger.error("a", InternalLoggerFactoryTest.e);
        verify(mock);
    }
}

