/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.dns;


import DnsException.UNKNOWN_CODE;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.cloud.BaseServiceException;
import com.google.cloud.RetryHelper.RetryHelperException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import org.junit.Assert;
import org.junit.Test;


public class DnsExceptionTest {
    @Test
    public void testDnsException() throws Exception {
        IOException cause = new SocketTimeoutException("socketTimeoutMessage");
        DnsException exception = new DnsException(500, "message", cause);
        Assert.assertEquals(500, exception.getCode());
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertNull(exception.getReason());
        Assert.assertTrue(exception.isRetryable());
        Assert.assertSame(cause, exception.getCause());
        exception = new DnsException(502, "message", cause);
        Assert.assertEquals(502, exception.getCode());
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertNull(exception.getReason());
        Assert.assertTrue(exception.isRetryable());
        Assert.assertSame(cause, exception.getCause());
        exception = new DnsException(503, "message", cause);
        Assert.assertEquals(503, exception.getCode());
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertNull(exception.getReason());
        Assert.assertTrue(exception.isRetryable());
        Assert.assertSame(cause, exception.getCause());
        exception = new DnsException(429, "message", cause);
        Assert.assertEquals(429, exception.getCode());
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertNull(exception.getReason());
        Assert.assertTrue(exception.isRetryable());
        Assert.assertSame(cause, exception.getCause());
        exception = new DnsException(404, "message", cause);
        Assert.assertEquals(404, exception.getCode());
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertNull(exception.getReason());
        Assert.assertFalse(exception.isRetryable());
        Assert.assertSame(cause, exception.getCause());
        exception = new DnsException(cause, true);
        Assert.assertEquals(UNKNOWN_CODE, exception.getCode());
        Assert.assertNull(exception.getReason());
        Assert.assertEquals("socketTimeoutMessage", exception.getMessage());
        Assert.assertEquals(cause, exception.getCause());
        Assert.assertTrue(exception.isRetryable());
        Assert.assertSame(cause, exception.getCause());
        GoogleJsonError error = new GoogleJsonError();
        error.setCode(503);
        error.setMessage("message");
        exception = new DnsException(error, true);
        Assert.assertEquals(503, exception.getCode());
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertTrue(exception.isRetryable());
    }

    @Test
    public void testTranslateAndThrow() throws Exception {
        IOException timeoutException = new SocketTimeoutException("message");
        Exception cause = new DnsException(timeoutException, true);
        RetryHelperException exceptionMock = createMock(RetryHelperException.class);
        expect(exceptionMock.getCause()).andReturn(cause).times(2);
        replay(exceptionMock);
        try {
            DnsException.translateAndThrow(exceptionMock);
        } catch (BaseServiceException ex) {
            Assert.assertEquals(UNKNOWN_CODE, ex.getCode());
            Assert.assertNull(ex.getReason());
            Assert.assertEquals("message", ex.getMessage());
            Assert.assertEquals(timeoutException, ex.getCause());
            Assert.assertTrue(ex.isRetryable());
        } finally {
            verify(exceptionMock);
        }
        cause = new IllegalArgumentException("message");
        exceptionMock = createMock(RetryHelperException.class);
        expect(exceptionMock.getMessage()).andReturn("message").times(1);
        expect(exceptionMock.getCause()).andReturn(cause).times(2);
        replay(exceptionMock);
        try {
            DnsException.translateAndThrow(exceptionMock);
        } catch (BaseServiceException ex) {
            Assert.assertEquals(UNKNOWN_CODE, ex.getCode());
            Assert.assertEquals("message", ex.getMessage());
            Assert.assertFalse(ex.isRetryable());
            Assert.assertSame(cause, ex.getCause());
        } finally {
            verify(exceptionMock);
        }
    }
}

