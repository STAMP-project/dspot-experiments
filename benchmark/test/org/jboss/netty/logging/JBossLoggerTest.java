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


import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;


public class JBossLoggerTest {
    private static final Exception e = new Exception();

    @Test
    @SuppressWarnings("deprecation")
    public void testIsDebugEnabled() {
        Logger mock = createStrictMock(Logger.class);
        expect(mock.isDebugEnabled()).andReturn(true);
        replay(mock);
        InternalLogger logger = new JBossLogger(mock);
        Assert.assertTrue(logger.isDebugEnabled());
        verify(mock);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testIsInfoEnabled() {
        Logger mock = createStrictMock(Logger.class);
        expect(mock.isInfoEnabled()).andReturn(true);
        replay(mock);
        InternalLogger logger = new JBossLogger(mock);
        Assert.assertTrue(logger.isInfoEnabled());
        verify(mock);
    }

    @Test
    public void testIsWarnEnabled() {
        Logger mock = createStrictMock(Logger.class);
        replay(mock);
        InternalLogger logger = new JBossLogger(mock);
        Assert.assertTrue(logger.isWarnEnabled());
        verify(mock);
    }

    @Test
    public void testIsErrorEnabled() {
        Logger mock = createStrictMock(Logger.class);
        replay(mock);
        InternalLogger logger = new JBossLogger(mock);
        Assert.assertTrue(logger.isErrorEnabled());
        verify(mock);
    }

    @Test
    public void testDebug() {
        Logger mock = createStrictMock(Logger.class);
        mock.debug("a");
        replay(mock);
        InternalLogger logger = new JBossLogger(mock);
        logger.debug("a");
        verify(mock);
    }

    @Test
    public void testDebugWithException() {
        Logger mock = createStrictMock(Logger.class);
        mock.debug("a", JBossLoggerTest.e);
        replay(mock);
        InternalLogger logger = new JBossLogger(mock);
        logger.debug("a", JBossLoggerTest.e);
        verify(mock);
    }

    @Test
    public void testInfo() {
        Logger mock = createStrictMock(Logger.class);
        mock.info("a");
        replay(mock);
        InternalLogger logger = new JBossLogger(mock);
        logger.info("a");
        verify(mock);
    }

    @Test
    public void testInfoWithException() {
        Logger mock = createStrictMock(Logger.class);
        mock.info("a", JBossLoggerTest.e);
        replay(mock);
        InternalLogger logger = new JBossLogger(mock);
        logger.info("a", JBossLoggerTest.e);
        verify(mock);
    }

    @Test
    public void testWarn() {
        Logger mock = createStrictMock(Logger.class);
        mock.warn("a");
        replay(mock);
        InternalLogger logger = new JBossLogger(mock);
        logger.warn("a");
        verify(mock);
    }

    @Test
    public void testWarnWithException() {
        Logger mock = createStrictMock(Logger.class);
        mock.warn("a", JBossLoggerTest.e);
        replay(mock);
        InternalLogger logger = new JBossLogger(mock);
        logger.warn("a", JBossLoggerTest.e);
        verify(mock);
    }

    @Test
    public void testError() {
        Logger mock = createStrictMock(Logger.class);
        mock.error("a");
        replay(mock);
        InternalLogger logger = new JBossLogger(mock);
        logger.error("a");
        verify(mock);
    }

    @Test
    public void testErrorWithException() {
        Logger mock = createStrictMock(Logger.class);
        mock.error("a", JBossLoggerTest.e);
        replay(mock);
        InternalLogger logger = new JBossLogger(mock);
        logger.error("a", JBossLoggerTest.e);
        verify(mock);
    }
}

