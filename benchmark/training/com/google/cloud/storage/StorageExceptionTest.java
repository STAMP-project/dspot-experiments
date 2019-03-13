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
package com.google.cloud.storage;


import StorageException.UNKNOWN_CODE;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpResponseException;
import com.google.cloud.BaseServiceException;
import com.google.cloud.RetryHelper.RetryHelperException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import org.junit.Assert;
import org.junit.Test;


public class StorageExceptionTest {
    @Test
    public void testStorageException() {
        StorageException exception = new StorageException(500, "message");
        Assert.assertEquals(500, exception.getCode());
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertNull(exception.getReason());
        Assert.assertTrue(exception.isRetryable());
        exception = new StorageException(502, "message");
        Assert.assertEquals(502, exception.getCode());
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertNull(exception.getReason());
        Assert.assertTrue(exception.isRetryable());
        exception = new StorageException(503, "message");
        Assert.assertEquals(503, exception.getCode());
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertNull(exception.getReason());
        Assert.assertTrue(exception.isRetryable());
        exception = new StorageException(504, "message");
        Assert.assertEquals(504, exception.getCode());
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertNull(exception.getReason());
        Assert.assertTrue(exception.isRetryable());
        exception = new StorageException(429, "message");
        Assert.assertEquals(429, exception.getCode());
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertNull(exception.getReason());
        Assert.assertTrue(exception.isRetryable());
        exception = new StorageException(408, "message");
        Assert.assertEquals(408, exception.getCode());
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertNull(exception.getReason());
        Assert.assertTrue(exception.isRetryable());
        exception = new StorageException(400, "message");
        Assert.assertEquals(400, exception.getCode());
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertNull(exception.getReason());
        Assert.assertFalse(exception.isRetryable());
        IOException cause = new SocketTimeoutException();
        exception = new StorageException(cause);
        Assert.assertNull(exception.getReason());
        Assert.assertNull(exception.getMessage());
        Assert.assertTrue(exception.isRetryable());
        Assert.assertSame(cause, exception.getCause());
        GoogleJsonError error = new GoogleJsonError();
        error.setCode(503);
        error.setMessage("message");
        exception = new StorageException(error);
        Assert.assertEquals(503, exception.getCode());
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertTrue(exception.isRetryable());
        exception = new StorageException(400, "message", cause);
        Assert.assertEquals(400, exception.getCode());
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertNull(exception.getReason());
        Assert.assertFalse(exception.isRetryable());
        Assert.assertSame(cause, exception.getCause());
        HttpResponseException httpResponseException = build();
        exception = new StorageException(httpResponseException);
        Assert.assertEquals(404, exception.getCode());
        Assert.assertFalse(exception.isRetryable());
        httpResponseException = new HttpResponseException.Builder(503, null, new com.google.api.client.http.HttpHeaders()).build();
        exception = new StorageException(httpResponseException);
        Assert.assertEquals(503, exception.getCode());
        Assert.assertTrue(exception.isRetryable());
        httpResponseException = new HttpResponseException.Builder(502, null, new com.google.api.client.http.HttpHeaders()).build();
        exception = new StorageException(httpResponseException);
        Assert.assertEquals(502, exception.getCode());
        Assert.assertTrue(exception.isRetryable());
        httpResponseException = new HttpResponseException.Builder(500, null, new com.google.api.client.http.HttpHeaders()).build();
        exception = new StorageException(httpResponseException);
        Assert.assertEquals(500, exception.getCode());
        Assert.assertTrue(exception.isRetryable());
        httpResponseException = new HttpResponseException.Builder(429, null, new com.google.api.client.http.HttpHeaders()).build();
        exception = new StorageException(httpResponseException);
        Assert.assertEquals(429, exception.getCode());
        Assert.assertTrue(exception.isRetryable());
        httpResponseException = new HttpResponseException.Builder(408, null, new com.google.api.client.http.HttpHeaders()).build();
        exception = new StorageException(httpResponseException);
        Assert.assertEquals(408, exception.getCode());
        Assert.assertTrue(exception.isRetryable());
    }

    @Test
    public void testTranslateAndThrow() throws Exception {
        Exception cause = new StorageException(503, "message");
        RetryHelperException exceptionMock = createMock(RetryHelperException.class);
        expect(exceptionMock.getCause()).andReturn(cause).times(2);
        replay(exceptionMock);
        try {
            StorageException.translateAndThrow(exceptionMock);
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
            StorageException.translateAndThrow(exceptionMock);
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

