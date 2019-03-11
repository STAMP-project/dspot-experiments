/**
 * Copyright (c) 2015, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.lambda.serving;


import RequestDispatcher.ERROR_EXCEPTION;
import RequestDispatcher.ERROR_MESSAGE;
import RequestDispatcher.ERROR_REQUEST_URI;
import RequestDispatcher.ERROR_STATUS_CODE;
import com.cloudera.oryx.common.OryxTest;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;


public final class ErrorResourceTest extends OryxTest {
    @Test
    public void testError() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setAttribute(ERROR_STATUS_CODE, 500);
        mockRequest.setAttribute(ERROR_REQUEST_URI, "http://foo/bar");
        mockRequest.setAttribute(ERROR_MESSAGE, "Something was wrong");
        mockRequest.setAttribute(ERROR_EXCEPTION, new IllegalStateException());
        ErrorResourceTest.testResponse(new ErrorResource().errorHTML(mockRequest), false);
        ErrorResourceTest.testResponse(new ErrorResource().errorText(mockRequest), false);
        ErrorResourceTest.testResponse(new ErrorResource().errorEmpty(mockRequest), true);
    }
}

