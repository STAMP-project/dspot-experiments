/**
 * Copyright 2015 Google LLC
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
package com.google.cloud.resourcemanager;


import ResourceManagerException.UNKNOWN_CODE;
import com.google.cloud.BaseServiceException;
import com.google.cloud.RetryHelper.RetryHelperException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import org.junit.Assert;
import org.junit.Test;


public class ResourceManagerExceptionTest {
    @Test
    public void testResourceManagerException() {
        ResourceManagerException exception = new ResourceManagerException(500, "message");
        Assert.assertEquals(500, exception.getCode());
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertNull(exception.getReason());
        Assert.assertTrue(exception.isRetryable());
        exception = new ResourceManagerException(503, "message");
        Assert.assertEquals(503, exception.getCode());
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertNull(exception.getReason());
        Assert.assertTrue(exception.isRetryable());
        exception = new ResourceManagerException(429, "message");
        Assert.assertEquals(429, exception.getCode());
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertNull(exception.getReason());
        Assert.assertTrue(exception.isRetryable());
        exception = new ResourceManagerException(403, "message");
        Assert.assertEquals(403, exception.getCode());
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertNull(exception.getReason());
        Assert.assertFalse(exception.isRetryable());
        IOException cause = new SocketTimeoutException();
        exception = new ResourceManagerException(cause);
        Assert.assertNull(exception.getReason());
        Assert.assertNull(exception.getMessage());
        Assert.assertTrue(exception.isRetryable());
        Assert.assertSame(cause, exception.getCause());
        exception = new ResourceManagerException(404, "message", cause);
        Assert.assertEquals(404, exception.getCode());
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertNull(exception.getReason());
        Assert.assertFalse(exception.isRetryable());
        Assert.assertSame(cause, exception.getCause());
    }

    @Test
    public void testTranslateAndThrow() throws Exception {
        Exception cause = new ResourceManagerException(503, "message");
        RetryHelperException exceptionMock = createMock(RetryHelperException.class);
        expect(exceptionMock.getCause()).andReturn(cause).times(2);
        replay(exceptionMock);
        try {
            ResourceManagerException.translateAndThrow(exceptionMock);
        } catch (BaseServiceException ex) {
            Assert.assertEquals(503, ex.getCode());
            Assert.assertEquals("message", ex.getMessage());
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
            ResourceManagerException.translateAndThrow(exceptionMock);
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

