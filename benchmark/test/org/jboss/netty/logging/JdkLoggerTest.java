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


import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;


public class JdkLoggerTest {
    private static final Exception e = new Exception();

    @Test
    public void testIsDebugEnabled() {
        Logger mock = createStrictMock(Logger.class);
        expect(mock.isLoggable(Level.FINE)).andReturn(true);
        replay(mock);
        InternalLogger logger = new JdkLogger(mock, "foo");
        Assert.assertTrue(logger.isDebugEnabled());
        verify(mock);
    }

    @Test
    public void testIsInfoEnabled() {
        Logger mock = createStrictMock(Logger.class);
        expect(mock.isLoggable(Level.INFO)).andReturn(true);
        replay(mock);
        InternalLogger logger = new JdkLogger(mock, "foo");
        Assert.assertTrue(logger.isInfoEnabled());
        verify(mock);
    }

    @Test
    public void testIsWarnEnabled() {
        Logger mock = createStrictMock(Logger.class);
        expect(mock.isLoggable(Level.WARNING)).andReturn(true);
        replay(mock);
        InternalLogger logger = new JdkLogger(mock, "foo");
        Assert.assertTrue(logger.isWarnEnabled());
        verify(mock);
    }

    @Test
    public void testIsErrorEnabled() {
        Logger mock = createStrictMock(Logger.class);
        expect(mock.isLoggable(Level.SEVERE)).andReturn(true);
        replay(mock);
        InternalLogger logger = new JdkLogger(mock, "foo");
        Assert.assertTrue(logger.isErrorEnabled());
        verify(mock);
    }

    @Test
    public void testDebug() {
        Logger mock = createStrictMock(Logger.class);
        mock.logp(Level.FINE, "foo", null, "a");
        replay(mock);
        InternalLogger logger = new JdkLogger(mock, "foo");
        logger.debug("a");
        verify(mock);
    }

    @Test
    public void testDebugWithException() {
        Logger mock = createStrictMock(Logger.class);
        mock.logp(Level.FINE, "foo", null, "a", JdkLoggerTest.e);
        replay(mock);
        InternalLogger logger = new JdkLogger(mock, "foo");
        logger.debug("a", JdkLoggerTest.e);
        verify(mock);
    }

    @Test
    public void testInfo() {
        Logger mock = createStrictMock(Logger.class);
        mock.logp(Level.INFO, "foo", null, "a");
        replay(mock);
        InternalLogger logger = new JdkLogger(mock, "foo");
        logger.info("a");
        verify(mock);
    }

    @Test
    public void testInfoWithException() {
        Logger mock = createStrictMock(Logger.class);
        mock.logp(Level.INFO, "foo", null, "a", JdkLoggerTest.e);
        replay(mock);
        InternalLogger logger = new JdkLogger(mock, "foo");
        logger.info("a", JdkLoggerTest.e);
        verify(mock);
    }

    @Test
    public void testWarn() {
        Logger mock = createStrictMock(Logger.class);
        mock.logp(Level.WARNING, "foo", null, "a");
        replay(mock);
        InternalLogger logger = new JdkLogger(mock, "foo");
        logger.warn("a");
        verify(mock);
    }

    @Test
    public void testWarnWithException() {
        Logger mock = createStrictMock(Logger.class);
        mock.logp(Level.WARNING, "foo", null, "a", JdkLoggerTest.e);
        replay(mock);
        InternalLogger logger = new JdkLogger(mock, "foo");
        logger.warn("a", JdkLoggerTest.e);
        verify(mock);
    }

    @Test
    public void testError() {
        Logger mock = createStrictMock(Logger.class);
        mock.logp(Level.SEVERE, "foo", null, "a");
        replay(mock);
        InternalLogger logger = new JdkLogger(mock, "foo");
        logger.error("a");
        verify(mock);
    }

    @Test
    public void testErrorWithException() {
        Logger mock = createStrictMock(Logger.class);
        mock.logp(Level.SEVERE, "foo", null, "a", JdkLoggerTest.e);
        replay(mock);
        InternalLogger logger = new JdkLogger(mock, "foo");
        logger.error("a", JdkLoggerTest.e);
        verify(mock);
    }
}

