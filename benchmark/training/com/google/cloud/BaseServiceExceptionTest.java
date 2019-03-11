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
package com.google.cloud;


import RetryHelper.RetryHelperException;
import com.google.cloud.BaseServiceException.ExceptionData;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link BaseServiceException}.
 */
public class BaseServiceExceptionTest {
    private static final int CODE = 1;

    private static final int CODE_NO_REASON = 2;

    private static final String MESSAGE = "some message";

    private static final String REASON = "some reason";

    private static final boolean NOT_RETRYABLE = false;

    private static final boolean RETRYABLE = true;

    private static final boolean IDEMPOTENT = true;

    private static final String DEBUG_INFO = "debugInfo";

    private static final String LOCATION = "location";

    private static class CustomServiceException extends BaseServiceException {
        private static final long serialVersionUID = -195251309124875103L;

        public CustomServiceException(int code, String message, String reason, boolean idempotent) {
            super(ExceptionData.from(code, message, reason, BaseServiceException.isRetryable(code, reason, idempotent, BaseServiceExceptionTest.CustomServiceException.RETRYABLE_ERRORS)));
        }

        private static final Set RETRYABLE_ERRORS = ImmutableSet.of(new Error(BaseServiceExceptionTest.CODE, BaseServiceExceptionTest.REASON), new Error(null, BaseServiceExceptionTest.REASON), new Error(BaseServiceExceptionTest.CODE_NO_REASON, null));
    }

    @Test
    public void testBaseServiceException() {
        BaseServiceException serviceException = new BaseServiceException(ExceptionData.from(BaseServiceExceptionTest.CODE, BaseServiceExceptionTest.MESSAGE, BaseServiceExceptionTest.REASON, BaseServiceExceptionTest.NOT_RETRYABLE));
        Assert.assertEquals(BaseServiceExceptionTest.CODE, serviceException.getCode());
        Assert.assertEquals(BaseServiceExceptionTest.MESSAGE, serviceException.getMessage());
        Assert.assertEquals(BaseServiceExceptionTest.REASON, serviceException.getReason());
        Assert.assertFalse(serviceException.isRetryable());
        Assert.assertNull(serviceException.getCause());
        Exception cause = new RuntimeException();
        serviceException = new BaseServiceException(ExceptionData.from(BaseServiceExceptionTest.CODE, BaseServiceExceptionTest.MESSAGE, BaseServiceExceptionTest.REASON, BaseServiceExceptionTest.NOT_RETRYABLE, cause));
        Assert.assertEquals(BaseServiceExceptionTest.CODE, serviceException.getCode());
        Assert.assertEquals(BaseServiceExceptionTest.MESSAGE, serviceException.getMessage());
        Assert.assertEquals(BaseServiceExceptionTest.REASON, serviceException.getReason());
        Assert.assertFalse(serviceException.isRetryable());
        Assert.assertEquals(cause, serviceException.getCause());
        serviceException = new BaseServiceException(ExceptionData.newBuilder().setMessage(BaseServiceExceptionTest.MESSAGE).setCause(cause).setCode(BaseServiceExceptionTest.CODE).setReason(BaseServiceExceptionTest.REASON).setRetryable(BaseServiceExceptionTest.RETRYABLE).setDebugInfo(BaseServiceExceptionTest.DEBUG_INFO).setLocation(BaseServiceExceptionTest.LOCATION).build());
        Assert.assertEquals(BaseServiceExceptionTest.CODE, serviceException.getCode());
        Assert.assertEquals(BaseServiceExceptionTest.MESSAGE, serviceException.getMessage());
        Assert.assertEquals(BaseServiceExceptionTest.REASON, serviceException.getReason());
        Assert.assertTrue(serviceException.isRetryable());
        Assert.assertEquals(cause, serviceException.getCause());
        Assert.assertEquals(BaseServiceExceptionTest.DEBUG_INFO, serviceException.getDebugInfo());
        Assert.assertEquals(BaseServiceExceptionTest.LOCATION, serviceException.getLocation());
        serviceException = new BaseServiceExceptionTest.CustomServiceException(BaseServiceExceptionTest.CODE, BaseServiceExceptionTest.MESSAGE, BaseServiceExceptionTest.REASON, BaseServiceExceptionTest.IDEMPOTENT);
        Assert.assertEquals(BaseServiceExceptionTest.CODE, serviceException.getCode());
        Assert.assertEquals(BaseServiceExceptionTest.MESSAGE, serviceException.getMessage());
        Assert.assertEquals(BaseServiceExceptionTest.REASON, serviceException.getReason());
        Assert.assertTrue(serviceException.isRetryable());
        serviceException = new BaseServiceExceptionTest.CustomServiceException(BaseServiceExceptionTest.CODE_NO_REASON, BaseServiceExceptionTest.MESSAGE, null, BaseServiceExceptionTest.IDEMPOTENT);
        Assert.assertEquals(BaseServiceExceptionTest.CODE_NO_REASON, serviceException.getCode());
        Assert.assertEquals(BaseServiceExceptionTest.MESSAGE, serviceException.getMessage());
        Assert.assertNull(serviceException.getReason());
        Assert.assertTrue(serviceException.isRetryable());
        serviceException = new BaseServiceExceptionTest.CustomServiceException(BaseServiceException.UNKNOWN_CODE, BaseServiceExceptionTest.MESSAGE, BaseServiceExceptionTest.REASON, BaseServiceExceptionTest.IDEMPOTENT);
        Assert.assertEquals(BaseServiceException.UNKNOWN_CODE, serviceException.getCode());
        Assert.assertEquals(BaseServiceExceptionTest.MESSAGE, serviceException.getMessage());
        Assert.assertEquals(BaseServiceExceptionTest.REASON, serviceException.getReason());
        Assert.assertTrue(serviceException.isRetryable());
    }

    @Test
    public void testTranslateAndThrow() throws Exception {
        BaseServiceException cause = new BaseServiceException(ExceptionData.from(BaseServiceExceptionTest.CODE, BaseServiceExceptionTest.MESSAGE, BaseServiceExceptionTest.REASON, BaseServiceExceptionTest.NOT_RETRYABLE));
        RetryHelper.RetryHelperException exceptionMock = createMock(RetryHelperException.class);
        expect(exceptionMock.getCause()).andReturn(cause).times(2);
        replay(exceptionMock);
        try {
            BaseServiceException.translate(exceptionMock);
        } catch (BaseServiceException ex) {
            Assert.assertEquals(BaseServiceExceptionTest.CODE, ex.getCode());
            Assert.assertEquals(BaseServiceExceptionTest.MESSAGE, ex.getMessage());
            Assert.assertFalse(ex.isRetryable());
        } finally {
            verify(exceptionMock);
        }
    }

    @Test
    public void testError_Equal() {
        BaseServiceException.Error error = new BaseServiceException.Error(0, "reason", true);
        assertThat(error).isEqualTo(error);
        assertThat(error.hashCode()).isEqualTo(error.hashCode());
        BaseServiceException.Error sameError = new BaseServiceException.Error(0, "reason", true);
        assertThat(error).isEqualTo(sameError);
        assertThat(error.hashCode()).isEqualTo(sameError.hashCode());
        BaseServiceException.Error error2 = new BaseServiceException.Error(1, "other reason", false);
        assertThat(error).isNotEqualTo(error2);
        assertThat(error.hashCode()).isNotEqualTo(error2.hashCode());
    }
}

