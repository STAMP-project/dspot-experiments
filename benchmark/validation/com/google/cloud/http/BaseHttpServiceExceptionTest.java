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
package com.google.cloud.http;


import RetryHelper.RetryHelperException;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.cloud.BaseServiceException;
import com.google.cloud.RetryHelper;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class BaseHttpServiceExceptionTest {
    private static final int CODE = 1;

    private static final int CODE_NO_REASON = 2;

    private static final String MESSAGE = "some message";

    private static final String REASON = "some reason";

    private static final boolean RETRYABLE = true;

    private static final boolean IDEMPOTENT = true;

    private static final boolean NOT_IDEMPOTENT = false;

    private static final Set<BaseServiceException.Error> EMPTY_RETRYABLE_ERRORS = Collections.emptySet();

    private static class CustomServiceException extends BaseHttpServiceException {
        private static final long serialVersionUID = -195251309124875103L;

        public CustomServiceException(int code, String message, String reason, boolean idempotent) {
            super(code, message, reason, idempotent, BaseHttpServiceExceptionTest.CustomServiceException.RETRYABLE_ERRORS);
        }

        private static final Set<Error> RETRYABLE_ERRORS = ImmutableSet.of(new Error(BaseHttpServiceExceptionTest.CODE, BaseHttpServiceExceptionTest.REASON), new Error(null, BaseHttpServiceExceptionTest.REASON), new Error(BaseHttpServiceExceptionTest.CODE_NO_REASON, null));
    }

    @Test
    public void testBaseServiceException() {
        BaseServiceException serviceException = new BaseHttpServiceException(BaseHttpServiceExceptionTest.CODE, BaseHttpServiceExceptionTest.MESSAGE, BaseHttpServiceExceptionTest.REASON, BaseHttpServiceExceptionTest.IDEMPOTENT, BaseHttpServiceExceptionTest.EMPTY_RETRYABLE_ERRORS);
        Assert.assertEquals(BaseHttpServiceExceptionTest.CODE, serviceException.getCode());
        Assert.assertEquals(BaseHttpServiceExceptionTest.MESSAGE, serviceException.getMessage());
        Assert.assertEquals(BaseHttpServiceExceptionTest.REASON, serviceException.getReason());
        Assert.assertFalse(serviceException.isRetryable());
        Assert.assertNull(serviceException.getCause());
        serviceException = new BaseHttpServiceException(BaseHttpServiceExceptionTest.CODE, BaseHttpServiceExceptionTest.MESSAGE, BaseHttpServiceExceptionTest.REASON, BaseHttpServiceExceptionTest.IDEMPOTENT, BaseHttpServiceExceptionTest.EMPTY_RETRYABLE_ERRORS);
        Assert.assertEquals(BaseHttpServiceExceptionTest.CODE, serviceException.getCode());
        Assert.assertEquals(BaseHttpServiceExceptionTest.MESSAGE, serviceException.getMessage());
        Assert.assertEquals(BaseHttpServiceExceptionTest.REASON, serviceException.getReason());
        Assert.assertFalse(serviceException.isRetryable());
        Assert.assertNull(serviceException.getCause());
        Exception cause = new RuntimeException();
        serviceException = new BaseHttpServiceException(BaseHttpServiceExceptionTest.CODE, BaseHttpServiceExceptionTest.MESSAGE, BaseHttpServiceExceptionTest.REASON, BaseHttpServiceExceptionTest.IDEMPOTENT, BaseHttpServiceExceptionTest.EMPTY_RETRYABLE_ERRORS, cause);
        Assert.assertEquals(BaseHttpServiceExceptionTest.CODE, serviceException.getCode());
        Assert.assertEquals(BaseHttpServiceExceptionTest.MESSAGE, serviceException.getMessage());
        Assert.assertEquals(BaseHttpServiceExceptionTest.REASON, serviceException.getReason());
        Assert.assertFalse(serviceException.isRetryable());
        Assert.assertEquals(cause, serviceException.getCause());
        serviceException = new BaseHttpServiceException(BaseHttpServiceExceptionTest.CODE, BaseHttpServiceExceptionTest.MESSAGE, BaseHttpServiceExceptionTest.REASON, BaseHttpServiceExceptionTest.NOT_IDEMPOTENT, BaseHttpServiceExceptionTest.EMPTY_RETRYABLE_ERRORS, cause);
        Assert.assertEquals(BaseHttpServiceExceptionTest.CODE, serviceException.getCode());
        Assert.assertEquals(BaseHttpServiceExceptionTest.MESSAGE, serviceException.getMessage());
        Assert.assertEquals(BaseHttpServiceExceptionTest.REASON, serviceException.getReason());
        Assert.assertFalse(serviceException.isRetryable());
        Assert.assertEquals(cause, serviceException.getCause());
        IOException exception = new SocketTimeoutException();
        serviceException = new BaseHttpServiceException(exception, BaseHttpServiceExceptionTest.IDEMPOTENT, BaseHttpServiceExceptionTest.EMPTY_RETRYABLE_ERRORS);
        Assert.assertTrue(serviceException.isRetryable());
        Assert.assertNull(serviceException.getMessage());
        Assert.assertEquals(exception, serviceException.getCause());
        exception = new SocketException();
        serviceException = new BaseHttpServiceException(exception, BaseHttpServiceExceptionTest.IDEMPOTENT, BaseHttpServiceExceptionTest.EMPTY_RETRYABLE_ERRORS);
        Assert.assertTrue(serviceException.isRetryable());
        Assert.assertNull(serviceException.getMessage());
        Assert.assertEquals(exception, serviceException.getCause());
        exception = new IOException("insufficient data written");
        serviceException = new BaseHttpServiceException(exception, BaseHttpServiceExceptionTest.IDEMPOTENT, BaseHttpServiceExceptionTest.EMPTY_RETRYABLE_ERRORS);
        Assert.assertTrue(serviceException.isRetryable());
        Assert.assertEquals("insufficient data written", serviceException.getMessage());
        Assert.assertEquals(exception, serviceException.getCause());
        GoogleJsonError error = new GoogleJsonError();
        error.setCode(BaseHttpServiceExceptionTest.CODE);
        error.setMessage(BaseHttpServiceExceptionTest.MESSAGE);
        serviceException = new BaseHttpServiceException(error, BaseHttpServiceExceptionTest.IDEMPOTENT, BaseHttpServiceExceptionTest.EMPTY_RETRYABLE_ERRORS);
        Assert.assertEquals(BaseHttpServiceExceptionTest.CODE, serviceException.getCode());
        Assert.assertEquals(BaseHttpServiceExceptionTest.MESSAGE, serviceException.getMessage());
        Assert.assertFalse(serviceException.isRetryable());
        serviceException = new BaseHttpServiceExceptionTest.CustomServiceException(BaseHttpServiceExceptionTest.CODE, BaseHttpServiceExceptionTest.MESSAGE, BaseHttpServiceExceptionTest.REASON, BaseHttpServiceExceptionTest.IDEMPOTENT);
        Assert.assertEquals(BaseHttpServiceExceptionTest.CODE, serviceException.getCode());
        Assert.assertEquals(BaseHttpServiceExceptionTest.MESSAGE, serviceException.getMessage());
        Assert.assertEquals(BaseHttpServiceExceptionTest.REASON, serviceException.getReason());
        Assert.assertEquals(BaseHttpServiceExceptionTest.RETRYABLE, serviceException.isRetryable());
        serviceException = new BaseHttpServiceExceptionTest.CustomServiceException(BaseHttpServiceExceptionTest.CODE_NO_REASON, BaseHttpServiceExceptionTest.MESSAGE, null, BaseHttpServiceExceptionTest.IDEMPOTENT);
        Assert.assertEquals(BaseHttpServiceExceptionTest.CODE_NO_REASON, serviceException.getCode());
        Assert.assertEquals(BaseHttpServiceExceptionTest.MESSAGE, serviceException.getMessage());
        Assert.assertNull(serviceException.getReason());
        Assert.assertEquals(BaseHttpServiceExceptionTest.RETRYABLE, serviceException.isRetryable());
        serviceException = new BaseHttpServiceExceptionTest.CustomServiceException(BaseServiceException.UNKNOWN_CODE, BaseHttpServiceExceptionTest.MESSAGE, BaseHttpServiceExceptionTest.REASON, BaseHttpServiceExceptionTest.IDEMPOTENT);
        Assert.assertEquals(BaseServiceException.UNKNOWN_CODE, serviceException.getCode());
        Assert.assertEquals(BaseHttpServiceExceptionTest.MESSAGE, serviceException.getMessage());
        Assert.assertEquals(BaseHttpServiceExceptionTest.REASON, serviceException.getReason());
        Assert.assertEquals(BaseHttpServiceExceptionTest.RETRYABLE, serviceException.isRetryable());
    }

    @Test
    public void testTranslateAndThrow() throws Exception {
        BaseServiceException cause = new BaseHttpServiceException(BaseHttpServiceExceptionTest.CODE, BaseHttpServiceExceptionTest.MESSAGE, BaseHttpServiceExceptionTest.REASON, BaseHttpServiceExceptionTest.IDEMPOTENT, BaseHttpServiceExceptionTest.EMPTY_RETRYABLE_ERRORS);
        RetryHelper.RetryHelperException exceptionMock = createMock(RetryHelperException.class);
        expect(exceptionMock.getCause()).andReturn(cause).times(2);
        replay(exceptionMock);
        try {
            BaseServiceException.translate(exceptionMock);
        } catch (BaseServiceException ex) {
            Assert.assertEquals(BaseHttpServiceExceptionTest.CODE, ex.getCode());
            Assert.assertEquals(BaseHttpServiceExceptionTest.MESSAGE, ex.getMessage());
            Assert.assertFalse(ex.isRetryable());
        } finally {
            verify(exceptionMock);
        }
    }
}

