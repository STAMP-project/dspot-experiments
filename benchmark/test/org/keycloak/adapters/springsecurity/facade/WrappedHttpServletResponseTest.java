/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.adapters.springsecurity.facade;


import HttpStatus.OK;
import HttpStatus.UNAUTHORIZED;
import javax.servlet.http.Cookie;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletResponse;


public class WrappedHttpServletResponseTest {
    private static final String COOKIE_DOMAIN = ".keycloak.org";

    private static final String COOKIE_NAME = "foo";

    private static final String COOKIE_PATH = "/bar";

    private static final String COOKIE_VALUE = "onegreatcookie";

    private static final String HEADER = "Test";

    private WrappedHttpServletResponse response;

    private MockHttpServletResponse mockResponse;

    @Test
    public void testResetCookie() throws Exception {
        response.resetCookie(WrappedHttpServletResponseTest.COOKIE_NAME, WrappedHttpServletResponseTest.COOKIE_PATH);
        Mockito.verify(mockResponse).addCookie(ArgumentMatchers.any(Cookie.class));
        Assert.assertEquals(WrappedHttpServletResponseTest.COOKIE_NAME, mockResponse.getCookie(WrappedHttpServletResponseTest.COOKIE_NAME).getName());
        Assert.assertEquals(WrappedHttpServletResponseTest.COOKIE_PATH, mockResponse.getCookie(WrappedHttpServletResponseTest.COOKIE_NAME).getPath());
        Assert.assertEquals(0, mockResponse.getCookie(WrappedHttpServletResponseTest.COOKIE_NAME).getMaxAge());
        Assert.assertEquals("", mockResponse.getCookie(WrappedHttpServletResponseTest.COOKIE_NAME).getValue());
    }

    @Test
    public void testSetCookie() throws Exception {
        int maxAge = 300;
        response.setCookie(WrappedHttpServletResponseTest.COOKIE_NAME, WrappedHttpServletResponseTest.COOKIE_VALUE, WrappedHttpServletResponseTest.COOKIE_PATH, WrappedHttpServletResponseTest.COOKIE_DOMAIN, maxAge, false, true);
        Mockito.verify(mockResponse).addCookie(ArgumentMatchers.any(Cookie.class));
        Assert.assertEquals(WrappedHttpServletResponseTest.COOKIE_NAME, mockResponse.getCookie(WrappedHttpServletResponseTest.COOKIE_NAME).getName());
        Assert.assertEquals(WrappedHttpServletResponseTest.COOKIE_PATH, mockResponse.getCookie(WrappedHttpServletResponseTest.COOKIE_NAME).getPath());
        Assert.assertEquals(WrappedHttpServletResponseTest.COOKIE_DOMAIN, mockResponse.getCookie(WrappedHttpServletResponseTest.COOKIE_NAME).getDomain());
        Assert.assertEquals(maxAge, mockResponse.getCookie(WrappedHttpServletResponseTest.COOKIE_NAME).getMaxAge());
        Assert.assertEquals(WrappedHttpServletResponseTest.COOKIE_VALUE, mockResponse.getCookie(WrappedHttpServletResponseTest.COOKIE_NAME).getValue());
        Assert.assertEquals(true, mockResponse.getCookie(WrappedHttpServletResponseTest.COOKIE_NAME).isHttpOnly());
    }

    @Test
    public void testSetStatus() throws Exception {
        int status = OK.value();
        response.setStatus(status);
        Mockito.verify(mockResponse).setStatus(ArgumentMatchers.eq(status));
        Assert.assertEquals(status, mockResponse.getStatus());
    }

    @Test
    public void testAddHeader() throws Exception {
        String headerValue = "foo";
        response.addHeader(WrappedHttpServletResponseTest.HEADER, headerValue);
        Mockito.verify(mockResponse).addHeader(ArgumentMatchers.eq(WrappedHttpServletResponseTest.HEADER), ArgumentMatchers.eq(headerValue));
        Assert.assertTrue(mockResponse.containsHeader(WrappedHttpServletResponseTest.HEADER));
    }

    @Test
    public void testSetHeader() throws Exception {
        String headerValue = "foo";
        response.setHeader(WrappedHttpServletResponseTest.HEADER, headerValue);
        Mockito.verify(mockResponse).setHeader(ArgumentMatchers.eq(WrappedHttpServletResponseTest.HEADER), ArgumentMatchers.eq(headerValue));
        Assert.assertTrue(mockResponse.containsHeader(WrappedHttpServletResponseTest.HEADER));
    }

    @Test
    public void testGetOutputStream() throws Exception {
        Assert.assertNotNull(response.getOutputStream());
        Mockito.verify(mockResponse).getOutputStream();
    }

    @Test
    public void testSendError() throws Exception {
        int status = UNAUTHORIZED.value();
        String reason = UNAUTHORIZED.getReasonPhrase();
        response.sendError(status, reason);
        Mockito.verify(mockResponse).sendError(ArgumentMatchers.eq(status), ArgumentMatchers.eq(reason));
        Assert.assertEquals(status, mockResponse.getStatus());
        Assert.assertEquals(reason, mockResponse.getErrorMessage());
    }
}

