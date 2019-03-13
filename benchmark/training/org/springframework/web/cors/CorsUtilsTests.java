/**
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.cors;


import HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD;
import HttpHeaders.ORIGIN;
import HttpMethod.OPTIONS;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletRequest;


/**
 * Test case for {@link CorsUtils}.
 *
 * @author Sebastien Deleuze
 */
public class CorsUtilsTests {
    @Test
    public void isCorsRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(ORIGIN, "http://domain.com");
        Assert.assertTrue(CorsUtils.isCorsRequest(request));
    }

    @Test
    public void isNotCorsRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Assert.assertFalse(CorsUtils.isCorsRequest(request));
    }

    @Test
    public void isPreFlightRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(OPTIONS.name());
        request.addHeader(ORIGIN, "http://domain.com");
        request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        Assert.assertTrue(CorsUtils.isPreFlightRequest(request));
    }

    @Test
    public void isNotPreFlightRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Assert.assertFalse(CorsUtils.isPreFlightRequest(request));
        request = new MockHttpServletRequest();
        request.setMethod(OPTIONS.name());
        request.addHeader(ORIGIN, "http://domain.com");
        Assert.assertFalse(CorsUtils.isPreFlightRequest(request));
        request = new MockHttpServletRequest();
        request.setMethod(OPTIONS.name());
        request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        Assert.assertFalse(CorsUtils.isPreFlightRequest(request));
    }
}

