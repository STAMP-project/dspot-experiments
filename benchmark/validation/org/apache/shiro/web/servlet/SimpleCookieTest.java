/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.web.servlet;


import SimpleCookie.ATTRIBUTE_DELIMITER;
import SimpleCookie.DEFAULT_VERSION;
import SimpleCookie.NAME_VALUE_DELIMITER;
import SimpleCookie.PATH_ATTRIBUTE_NAME;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import junit.framework.TestCase;
import org.junit.Test;


/**
 * TODO - Class JavaDoc
 *
 * @since Apr 22, 2010 9:40:47 PM
 */
public class SimpleCookieTest extends TestCase {
    private SimpleCookie cookie;

    private HttpServletRequest mockRequest;

    private HttpServletResponse mockResponse;

    // Verifies fix for JSEC-94
    @Test
    public void testRemoveValue() throws Exception {
        // verify that the cookie header starts with what we want
        // we can't verify the exact date format string that is appended, so we resort to just
        // simple 'startsWith' matching, which is good enough:
        String name = "test";
        String value = "deleteMe";
        String path = "/somepath";
        String headerValue = this.cookie.buildHeaderValue(name, value, null, null, path, 0, DEFAULT_VERSION, false, false);
        String expectedStart = new StringBuilder().append(name).append(NAME_VALUE_DELIMITER).append(value).append(ATTRIBUTE_DELIMITER).append(PATH_ATTRIBUTE_NAME).append(NAME_VALUE_DELIMITER).append(path).toString();
        TestCase.assertTrue(headerValue.startsWith(expectedStart));
        expect(mockRequest.getContextPath()).andReturn(path).times(1);
        mockResponse.addHeader(eq(SimpleCookie.COOKIE_HEADER_NAME), isA(String.class));// can't calculate the date format in the test

        replay(mockRequest);
        replay(mockResponse);
        this.cookie.removeFrom(mockRequest, mockResponse);
        verify(mockRequest);
        verify(mockResponse);
    }

    /**
     * Verifies fix for <a href="http://issues.apache.org/jira/browse/JSEC-34">JSEC-34</a> (1 of 2)
     */
    @Test
    public void testEmptyContextPath() throws Exception {
        testRootContextPath("");
    }

    /**
     * Verifies fix for <a href="http://issues.apache.org/jira/browse/JSEC-34">JSEC-34</a> (2 of 2)
     */
    @Test
    public void testNullContextPath() throws Exception {
        testRootContextPath(null);
    }

    @Test
    public void testReadValueInvalidPath() throws Exception {
        expect(mockRequest.getRequestURI()).andStubReturn("/foo/index.jsp");
        expect(mockRequest.getCookies()).andStubReturn(new Cookie[]{ new Cookie(this.cookie.getName(), "value") });
        replay(mockRequest);
        replay(mockResponse);
        this.cookie.setPath("/bar/index.jsp");
        TestCase.assertEquals(null, this.cookie.readValue(mockRequest, mockResponse));
    }

    @Test
    public void testReadValuePrefixPath() throws Exception {
        expect(mockRequest.getRequestURI()).andStubReturn("/bar/index.jsp");
        expect(mockRequest.getCookies()).andStubReturn(new Cookie[]{ new Cookie(this.cookie.getName(), "value") });
        replay(mockRequest);
        replay(mockResponse);
        this.cookie.setPath("/bar");
        TestCase.assertEquals("value", this.cookie.readValue(mockRequest, mockResponse));
    }

    @Test
    public void testReadValueInvalidPrefixPath() throws Exception {
        expect(mockRequest.getRequestURI()).andStubReturn("/foobar/index.jsp");
        expect(mockRequest.getCookies()).andStubReturn(new Cookie[]{ new Cookie(this.cookie.getName(), "value") });
        replay(mockRequest);
        replay(mockResponse);
        this.cookie.setPath("/foo");
        TestCase.assertEquals(null, this.cookie.readValue(mockRequest, mockResponse));
    }
}

