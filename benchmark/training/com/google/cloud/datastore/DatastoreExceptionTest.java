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
package com.google.cloud.datastore;


import DatastoreException.UNKNOWN_CODE;
import RetryHelper.RetryHelperException;
import com.google.cloud.BaseServiceException;
import com.google.cloud.RetryHelper;
import java.io.IOException;
import java.net.SocketTimeoutException;
import org.junit.Assert;
import org.junit.Test;


public class DatastoreExceptionTest {
    @Test
    public void testDatastoreException() throws Exception {
        DatastoreException exception = new DatastoreException(10, "message", "ABORTED");
        Assert.assertEquals(10, exception.getCode());
        Assert.assertEquals("ABORTED", exception.getReason());
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertTrue(exception.isRetryable());
        exception = new DatastoreException(4, "message", "DEADLINE_EXCEEDED");
        Assert.assertEquals(4, exception.getCode());
        Assert.assertEquals("DEADLINE_EXCEEDED", exception.getReason());
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertTrue(exception.isRetryable());
        exception = new DatastoreException(14, "message", "UNAVAILABLE");
        Assert.assertEquals(14, exception.getCode());
        Assert.assertEquals("UNAVAILABLE", exception.getReason());
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertTrue(exception.isRetryable());
        exception = new DatastoreException(2, "message", "INTERNAL");
        Assert.assertEquals(2, exception.getCode());
        Assert.assertEquals("INTERNAL", exception.getReason());
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertFalse(exception.isRetryable());
        IOException cause = new SocketTimeoutException("socketTimeoutMessage");
        exception = new DatastoreException(cause);
        Assert.assertEquals(UNKNOWN_CODE, exception.getCode());
        Assert.assertNull(exception.getReason());
        Assert.assertEquals("socketTimeoutMessage", exception.getMessage());
        Assert.assertEquals(cause, exception.getCause());
        Assert.assertTrue(exception.isRetryable());
        Assert.assertSame(cause, exception.getCause());
        exception = new DatastoreException(2, "message", "INTERNAL", cause);
        Assert.assertEquals(2, exception.getCode());
        Assert.assertEquals("INTERNAL", exception.getReason());
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertFalse(exception.isRetryable());
        Assert.assertSame(cause, exception.getCause());
    }

    @Test
    public void testTranslateAndThrow() throws Exception {
        Exception cause = new DatastoreException(14, "message", "UNAVAILABLE");
        RetryHelper.RetryHelperException exceptionMock = createMock(RetryHelperException.class);
        expect(exceptionMock.getCause()).andReturn(cause).times(2);
        replay(exceptionMock);
        try {
            DatastoreException.translateAndThrow(exceptionMock);
        } catch (BaseServiceException ex) {
            Assert.assertEquals(14, ex.getCode());
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
            DatastoreException.translateAndThrow(exceptionMock);
        } catch (BaseServiceException ex) {
            Assert.assertEquals(UNKNOWN_CODE, ex.getCode());
            Assert.assertEquals("message", ex.getMessage());
            Assert.assertFalse(ex.isRetryable());
            Assert.assertSame(cause, ex.getCause());
        } finally {
            verify(exceptionMock);
        }
    }

    @Test
    public void testThrowInvalidRequest() throws Exception {
        try {
            DatastoreException.throwInvalidRequest("message %s %d", "a", 1);
            Assert.fail("Exception expected");
        } catch (DatastoreException ex) {
            Assert.assertEquals("FAILED_PRECONDITION", ex.getReason());
            Assert.assertEquals("message a 1", ex.getMessage());
        }
    }
}

