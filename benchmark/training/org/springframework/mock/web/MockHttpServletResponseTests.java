/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.mock.web;


import HttpHeaders.SET_COOKIE;
import HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import HttpServletResponse.SC_MOVED_TEMPORARILY;
import HttpServletResponse.SC_NOT_FOUND;
import HttpServletResponse.SC_OK;
import WebUtils.DEFAULT_CHARACTER_ENCODING;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import javax.servlet.http.Cookie;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link MockHttpServletResponse}.
 *
 * @author Juergen Hoeller
 * @author Rick Evans
 * @author Rossen Stoyanchev
 * @author Rob Winch
 * @author Sam Brannen
 * @author Brian Clozel
 * @since 19.02.2006
 */
public class MockHttpServletResponseTests {
    private MockHttpServletResponse response = new MockHttpServletResponse();

    @Test
    public void setContentType() {
        String contentType = "test/plain";
        response.setContentType(contentType);
        Assert.assertEquals(contentType, response.getContentType());
        Assert.assertEquals(contentType, response.getHeader("Content-Type"));
        Assert.assertEquals(DEFAULT_CHARACTER_ENCODING, response.getCharacterEncoding());
    }

    @Test
    public void setContentTypeUTF8() {
        String contentType = "test/plain;charset=UTF-8";
        response.setContentType(contentType);
        Assert.assertEquals("UTF-8", response.getCharacterEncoding());
        Assert.assertEquals(contentType, response.getContentType());
        Assert.assertEquals(contentType, response.getHeader("Content-Type"));
    }

    @Test
    public void contentTypeHeader() {
        String contentType = "test/plain";
        response.addHeader("Content-Type", contentType);
        Assert.assertEquals(contentType, response.getContentType());
        Assert.assertEquals(contentType, response.getHeader("Content-Type"));
        Assert.assertEquals(DEFAULT_CHARACTER_ENCODING, response.getCharacterEncoding());
        response = new MockHttpServletResponse();
        response.setHeader("Content-Type", contentType);
        Assert.assertEquals(contentType, response.getContentType());
        Assert.assertEquals(contentType, response.getHeader("Content-Type"));
        Assert.assertEquals(DEFAULT_CHARACTER_ENCODING, response.getCharacterEncoding());
    }

    @Test
    public void contentTypeHeaderUTF8() {
        String contentType = "test/plain;charset=UTF-8";
        response.setHeader("Content-Type", contentType);
        Assert.assertEquals(contentType, response.getContentType());
        Assert.assertEquals(contentType, response.getHeader("Content-Type"));
        Assert.assertEquals("UTF-8", response.getCharacterEncoding());
        response = new MockHttpServletResponse();
        response.addHeader("Content-Type", contentType);
        Assert.assertEquals(contentType, response.getContentType());
        Assert.assertEquals(contentType, response.getHeader("Content-Type"));
        Assert.assertEquals("UTF-8", response.getCharacterEncoding());
    }

    // SPR-12677
    @Test
    public void contentTypeHeaderWithMoreComplexCharsetSyntax() {
        String contentType = "test/plain;charset=\"utf-8\";foo=\"charset=bar\";foocharset=bar;foo=bar";
        response.setHeader("Content-Type", contentType);
        Assert.assertEquals(contentType, response.getContentType());
        Assert.assertEquals(contentType, response.getHeader("Content-Type"));
        Assert.assertEquals("UTF-8", response.getCharacterEncoding());
        response = new MockHttpServletResponse();
        response.addHeader("Content-Type", contentType);
        Assert.assertEquals(contentType, response.getContentType());
        Assert.assertEquals(contentType, response.getHeader("Content-Type"));
        Assert.assertEquals("UTF-8", response.getCharacterEncoding());
    }

    @Test
    public void setContentTypeThenCharacterEncoding() {
        response.setContentType("test/plain");
        response.setCharacterEncoding("UTF-8");
        Assert.assertEquals("test/plain", response.getContentType());
        Assert.assertEquals("test/plain;charset=UTF-8", response.getHeader("Content-Type"));
        Assert.assertEquals("UTF-8", response.getCharacterEncoding());
    }

    @Test
    public void setCharacterEncodingThenContentType() {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("test/plain");
        Assert.assertEquals("test/plain", response.getContentType());
        Assert.assertEquals("test/plain;charset=UTF-8", response.getHeader("Content-Type"));
        Assert.assertEquals("UTF-8", response.getCharacterEncoding());
    }

    @Test
    public void contentLength() {
        response.setContentLength(66);
        Assert.assertEquals(66, response.getContentLength());
        Assert.assertEquals("66", response.getHeader("Content-Length"));
    }

    @Test
    public void contentLengthHeader() {
        response.addHeader("Content-Length", "66");
        Assert.assertEquals(66, response.getContentLength());
        Assert.assertEquals("66", response.getHeader("Content-Length"));
    }

    @Test
    public void contentLengthIntHeader() {
        response.addIntHeader("Content-Length", 66);
        Assert.assertEquals(66, response.getContentLength());
        Assert.assertEquals("66", response.getHeader("Content-Length"));
    }

    @Test
    public void httpHeaderNameCasingIsPreserved() throws Exception {
        final String headerName = "Header1";
        response.addHeader(headerName, "value1");
        Collection<String> responseHeaders = response.getHeaderNames();
        Assert.assertNotNull(responseHeaders);
        Assert.assertEquals(1, responseHeaders.size());
        Assert.assertEquals("HTTP header casing not being preserved", headerName, responseHeaders.iterator().next());
    }

    @Test
    public void cookies() {
        Cookie cookie = new Cookie("foo", "bar");
        cookie.setPath("/path");
        cookie.setDomain("example.com");
        cookie.setMaxAge(0);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        Assert.assertEquals(("foo=bar; Path=/path; Domain=example.com; " + ("Max-Age=0; Expires=Thu, 1 Jan 1970 00:00:00 GMT; " + "Secure; HttpOnly")), response.getHeader(SET_COOKIE));
    }

    @Test
    public void servletOutputStreamCommittedWhenBufferSizeExceeded() throws IOException {
        Assert.assertFalse(response.isCommitted());
        response.getOutputStream().write('X');
        Assert.assertFalse(response.isCommitted());
        int size = response.getBufferSize();
        response.getOutputStream().write(new byte[size]);
        Assert.assertTrue(response.isCommitted());
        Assert.assertEquals((size + 1), response.getContentAsByteArray().length);
    }

    @Test
    public void servletOutputStreamCommittedOnFlushBuffer() throws IOException {
        Assert.assertFalse(response.isCommitted());
        response.getOutputStream().write('X');
        Assert.assertFalse(response.isCommitted());
        response.flushBuffer();
        Assert.assertTrue(response.isCommitted());
        Assert.assertEquals(1, response.getContentAsByteArray().length);
    }

    @Test
    public void servletWriterCommittedWhenBufferSizeExceeded() throws IOException {
        Assert.assertFalse(response.isCommitted());
        response.getWriter().write("X");
        Assert.assertFalse(response.isCommitted());
        int size = response.getBufferSize();
        char[] data = new char[size];
        Arrays.fill(data, 'p');
        response.getWriter().write(data);
        Assert.assertTrue(response.isCommitted());
        Assert.assertEquals((size + 1), response.getContentAsByteArray().length);
    }

    @Test
    public void servletOutputStreamCommittedOnOutputStreamFlush() throws IOException {
        Assert.assertFalse(response.isCommitted());
        response.getOutputStream().write('X');
        Assert.assertFalse(response.isCommitted());
        response.getOutputStream().flush();
        Assert.assertTrue(response.isCommitted());
        Assert.assertEquals(1, response.getContentAsByteArray().length);
    }

    @Test
    public void servletWriterCommittedOnWriterFlush() throws IOException {
        Assert.assertFalse(response.isCommitted());
        response.getWriter().write("X");
        Assert.assertFalse(response.isCommitted());
        response.getWriter().flush();
        Assert.assertTrue(response.isCommitted());
        Assert.assertEquals(1, response.getContentAsByteArray().length);
    }

    // SPR-16683
    @Test
    public void servletWriterCommittedOnWriterClose() throws IOException {
        Assert.assertFalse(response.isCommitted());
        response.getWriter().write("X");
        Assert.assertFalse(response.isCommitted());
        response.getWriter().close();
        Assert.assertTrue(response.isCommitted());
        Assert.assertEquals(1, response.getContentAsByteArray().length);
    }

    @Test
    public void servletWriterAutoFlushedForString() throws IOException {
        response.getWriter().write("X");
        Assert.assertEquals("X", response.getContentAsString());
    }

    @Test
    public void servletWriterAutoFlushedForChar() throws IOException {
        response.getWriter().write('X');
        Assert.assertEquals("X", response.getContentAsString());
    }

    @Test
    public void servletWriterAutoFlushedForCharArray() throws IOException {
        response.getWriter().write("XY".toCharArray());
        Assert.assertEquals("XY", response.getContentAsString());
    }

    @Test
    public void sendRedirect() throws IOException {
        String redirectUrl = "/redirect";
        response.sendRedirect(redirectUrl);
        Assert.assertEquals(SC_MOVED_TEMPORARILY, response.getStatus());
        Assert.assertEquals(redirectUrl, response.getHeader("Location"));
        Assert.assertEquals(redirectUrl, response.getRedirectedUrl());
        Assert.assertTrue(response.isCommitted());
    }

    @Test
    public void locationHeaderUpdatesGetRedirectedUrl() {
        String redirectUrl = "/redirect";
        response.setHeader("Location", redirectUrl);
        Assert.assertEquals(redirectUrl, response.getRedirectedUrl());
    }

    @Test
    public void setDateHeader() {
        response.setDateHeader("Last-Modified", 1437472800000L);
        Assert.assertEquals("Tue, 21 Jul 2015 10:00:00 GMT", response.getHeader("Last-Modified"));
    }

    @Test
    public void addDateHeader() {
        response.addDateHeader("Last-Modified", 1437472800000L);
        response.addDateHeader("Last-Modified", 1437472801000L);
        Assert.assertEquals("Tue, 21 Jul 2015 10:00:00 GMT", response.getHeaders("Last-Modified").get(0));
        Assert.assertEquals("Tue, 21 Jul 2015 10:00:01 GMT", response.getHeaders("Last-Modified").get(1));
    }

    @Test
    public void getDateHeader() {
        long time = 1437472800000L;
        response.setDateHeader("Last-Modified", time);
        Assert.assertEquals("Tue, 21 Jul 2015 10:00:00 GMT", response.getHeader("Last-Modified"));
        Assert.assertEquals(time, response.getDateHeader("Last-Modified"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getInvalidDateHeader() {
        response.setHeader("Last-Modified", "invalid");
        Assert.assertEquals("invalid", response.getHeader("Last-Modified"));
        response.getDateHeader("Last-Modified");
    }

    // SPR-16160
    @Test
    public void getNonExistentDateHeader() {
        Assert.assertNull(response.getHeader("Last-Modified"));
        Assert.assertEquals((-1), response.getDateHeader("Last-Modified"));
    }

    // SPR-10414
    @Test
    public void modifyStatusAfterSendError() throws IOException {
        response.sendError(SC_NOT_FOUND);
        response.setStatus(SC_OK);
        Assert.assertEquals(SC_NOT_FOUND, response.getStatus());
    }

    // SPR-10414
    @Test
    @SuppressWarnings("deprecation")
    public void modifyStatusMessageAfterSendError() throws IOException {
        response.sendError(SC_NOT_FOUND);
        response.setStatus(SC_INTERNAL_SERVER_ERROR, "Server Error");
        Assert.assertEquals(SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void setCookieHeaderValid() {
        response.addHeader(SET_COOKIE, "SESSION=123; Path=/; Secure; HttpOnly; SameSite=Lax");
        Cookie cookie = response.getCookie("SESSION");
        Assert.assertNotNull(cookie);
        Assert.assertTrue((cookie instanceof MockCookie));
        Assert.assertEquals("SESSION", cookie.getName());
        Assert.assertEquals("123", cookie.getValue());
        Assert.assertEquals("/", cookie.getPath());
        Assert.assertTrue(cookie.getSecure());
        Assert.assertTrue(cookie.isHttpOnly());
        Assert.assertEquals("Lax", getSameSite());
    }

    @Test
    public void addMockCookie() {
        MockCookie mockCookie = new MockCookie("SESSION", "123");
        mockCookie.setPath("/");
        mockCookie.setDomain("example.com");
        mockCookie.setMaxAge(0);
        mockCookie.setSecure(true);
        mockCookie.setHttpOnly(true);
        mockCookie.setSameSite("Lax");
        response.addCookie(mockCookie);
        Assert.assertEquals(("SESSION=123; Path=/; Domain=example.com; Max-Age=0; " + "Expires=Thu, 1 Jan 1970 00:00:00 GMT; Secure; HttpOnly; SameSite=Lax"), response.getHeader(SET_COOKIE));
    }
}

