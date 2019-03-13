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


import org.apache.commons.logging.Log;
import org.junit.Assert;
import org.junit.Test;


public class CommonsLoggerTest {
    private static final Exception e = new Exception();

    @Test
    public void testIsDebugEnabled() {
        Log mock = createStrictMock(Log.class);
        expect(mock.isDebugEnabled()).andReturn(true);
        replay(mock);
        InternalLogger logger = new CommonsLogger(mock, "foo");
        Assert.assertTrue(logger.isDebugEnabled());
        verify(mock);
    }

    @Test
    public void testIsInfoEnabled() {
        Log mock = createStrictMock(Log.class);
        expect(mock.isInfoEnabled()).andReturn(true);
        replay(mock);
        InternalLogger logger = new CommonsLogger(mock, "foo");
        Assert.assertTrue(logger.isInfoEnabled());
        verify(mock);
    }

    @Test
    public void testIsWarnEnabled() {
        Log mock = createStrictMock(Log.class);
        expect(mock.isWarnEnabled()).andReturn(true);
        replay(mock);
        InternalLogger logger = new CommonsLogger(mock, "foo");
        Assert.assertTrue(logger.isWarnEnabled());
        verify(mock);
    }

    @Test
    public void testIsErrorEnabled() {
        Log mock = createStrictMock(Log.class);
        expect(mock.isErrorEnabled()).andReturn(true);
        replay(mock);
        InternalLogger logger = new CommonsLogger(mock, "foo");
        Assert.assertTrue(logger.isErrorEnabled());
        verify(mock);
    }

    @Test
    public void testDebug() {
        Log mock = createStrictMock(Log.class);
        mock.debug("a");
        replay(mock);
        InternalLogger logger = new CommonsLogger(mock, "foo");
        logger.debug("a");
        verify(mock);
    }

    @Test
    public void testDebugWithException() {
        Log mock = createStrictMock(Log.class);
        mock.debug("a", CommonsLoggerTest.e);
        replay(mock);
        InternalLogger logger = new CommonsLogger(mock, "foo");
        logger.debug("a", CommonsLoggerTest.e);
        verify(mock);
    }

    @Test
    public void testInfo() {
        Log mock = createStrictMock(Log.class);
        mock.info("a");
        replay(mock);
        InternalLogger logger = new CommonsLogger(mock, "foo");
        logger.info("a");
        verify(mock);
    }

    @Test
    public void testInfoWithException() {
        Log mock = createStrictMock(Log.class);
        mock.info("a", CommonsLoggerTest.e);
        replay(mock);
        InternalLogger logger = new CommonsLogger(mock, "foo");
        logger.info("a", CommonsLoggerTest.e);
        verify(mock);
    }

    @Test
    public void testWarn() {
        Log mock = createStrictMock(Log.class);
        mock.warn("a");
        replay(mock);
        InternalLogger logger = new CommonsLogger(mock, "foo");
        logger.warn("a");
        verify(mock);
    }

    @Test
    public void testWarnWithException() {
        Log mock = createStrictMock(Log.class);
        mock.warn("a", CommonsLoggerTest.e);
        replay(mock);
        InternalLogger logger = new CommonsLogger(mock, "foo");
        logger.warn("a", CommonsLoggerTest.e);
        verify(mock);
    }

    @Test
    public void testError() {
        Log mock = createStrictMock(Log.class);
        mock.error("a");
        replay(mock);
        InternalLogger logger = new CommonsLogger(mock, "foo");
        logger.error("a");
        verify(mock);
    }

    @Test
    public void testErrorWithException() {
        Log mock = createStrictMock(Log.class);
        mock.error("a", CommonsLoggerTest.e);
        replay(mock);
        InternalLogger logger = new CommonsLogger(mock, "foo");
        logger.error("a", CommonsLoggerTest.e);
        verify(mock);
    }
}

