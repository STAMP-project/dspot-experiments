/**
 * Copyright 2017 Google LLC
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
package com.google.cloud.grpc;


import Code.INTERNAL;
import RetryHelper.RetryHelperException;
import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.rpc.InternalException;
import com.google.cloud.BaseServiceException;
import com.google.cloud.RetryHelper;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import org.junit.Assert;
import org.junit.Test;


public class BaseGrpcServiceExceptionTest {
    private static final String MESSAGE = "some message";

    private static final boolean NOT_RETRYABLE = false;

    private static final boolean IDEMPOTENT = true;

    @Test
    public void testBaseServiceException() {
        BaseGrpcServiceException serviceException = null;
        IOException exception = new SocketTimeoutException();
        serviceException = new BaseGrpcServiceException(exception, BaseGrpcServiceExceptionTest.IDEMPOTENT);
        Assert.assertTrue(serviceException.isRetryable());
        Assert.assertNull(serviceException.getMessage());
        Assert.assertEquals(exception, serviceException.getCause());
        Assert.assertNull(serviceException.getReason());
        Assert.assertNull(serviceException.getLocation());
        Assert.assertNull(serviceException.getDebugInfo());
        exception = new SocketException();
        serviceException = new BaseGrpcServiceException(exception, BaseGrpcServiceExceptionTest.IDEMPOTENT);
        Assert.assertTrue(serviceException.isRetryable());
        Assert.assertNull(serviceException.getMessage());
        Assert.assertEquals(exception, serviceException.getCause());
        Assert.assertNull(serviceException.getReason());
        Assert.assertNull(serviceException.getLocation());
        Assert.assertNull(serviceException.getDebugInfo());
        exception = new IOException("insufficient data written");
        serviceException = new BaseGrpcServiceException(exception, BaseGrpcServiceExceptionTest.IDEMPOTENT);
        Assert.assertTrue(serviceException.isRetryable());
        Assert.assertEquals("insufficient data written", serviceException.getMessage());
        Assert.assertEquals(exception, serviceException.getCause());
        Assert.assertNull(serviceException.getReason());
        Assert.assertNull(serviceException.getLocation());
        Assert.assertNull(serviceException.getDebugInfo());
        Exception cause = new IllegalArgumentException("bad arg");
        InternalException apiException = new InternalException(BaseGrpcServiceExceptionTest.MESSAGE, cause, GrpcStatusCode.of(INTERNAL), BaseGrpcServiceExceptionTest.NOT_RETRYABLE);
        serviceException = new BaseGrpcServiceException(apiException);
        Assert.assertFalse(serviceException.isRetryable());
        Assert.assertEquals(BaseGrpcServiceExceptionTest.MESSAGE, serviceException.getMessage());
        Assert.assertEquals(apiException, serviceException.getCause());
        Assert.assertEquals(500, serviceException.getCode());
        Assert.assertEquals(INTERNAL.name(), serviceException.getReason());
        Assert.assertNull(serviceException.getLocation());
        Assert.assertNull(serviceException.getDebugInfo());
    }

    @Test
    public void testTranslateAndThrow() throws Exception {
        IOException exception = new SocketTimeoutException();
        BaseGrpcServiceException cause = new BaseGrpcServiceException(exception, BaseGrpcServiceExceptionTest.IDEMPOTENT);
        RetryHelper.RetryHelperException exceptionMock = createMock(RetryHelperException.class);
        expect(exceptionMock.getCause()).andReturn(cause).times(2);
        replay(exceptionMock);
        try {
            BaseServiceException.translate(exceptionMock);
        } catch (BaseServiceException ex) {
            Assert.assertEquals(0, ex.getCode());
            Assert.assertNull(ex.getMessage());
            Assert.assertTrue(ex.isRetryable());
        } finally {
            verify(exceptionMock);
        }
    }
}

