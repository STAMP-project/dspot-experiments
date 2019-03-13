/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.server;


import CookieCompliance.RFC2965;
import HttpHeader.CACHE_CONTROL;
import HttpHeader.SET_COOKIE;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.net.HttpCookie;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.io.RuntimeIOException;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.session.DefaultSessionCache;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.NullSessionDataStore;
import org.eclipse.jetty.server.session.Session;
import org.eclipse.jetty.server.session.SessionData;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.util.BufferUtil;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class ResponseTest {
    static final InetSocketAddress LOCALADDRESS;

    static {
        InetAddress ip = null;
        try {
            ip = Inet4Address.getByName("127.0.0.42");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } finally {
            LOCALADDRESS = new InetSocketAddress(ip, 8888);
        }
    }

    private Server _server;

    private HttpChannel _channel;

    private ByteBuffer _content = BufferUtil.allocate((16 * 1024));

    // to allow for invalid encoding strings in this testcase
    @SuppressWarnings("InjectedReferences")
    @Test
    public void testContentType() throws Exception {
        Response response = getResponse();
        Assertions.assertEquals(null, response.getContentType());
        response.setHeader("Content-Type", "text/something");
        Assertions.assertEquals("text/something", response.getContentType());
        response.setContentType("foo/bar");
        Assertions.assertEquals("foo/bar", response.getContentType());
        response.getWriter();
        Assertions.assertEquals("foo/bar;charset=iso-8859-1", response.getContentType());
        response.setContentType("foo2/bar2");
        Assertions.assertEquals("foo2/bar2;charset=iso-8859-1", response.getContentType());
        response.setHeader("name", "foo");
        Iterator<String> en = response.getHeaders("name").iterator();
        Assertions.assertEquals("foo", en.next());
        Assertions.assertFalse(en.hasNext());
        response.addHeader("name", "bar");
        en = response.getHeaders("name").iterator();
        Assertions.assertEquals("foo", en.next());
        Assertions.assertEquals("bar", en.next());
        Assertions.assertFalse(en.hasNext());
        response.recycle();
        response.setContentType("text/html");
        Assertions.assertEquals("text/html", response.getContentType());
        response.getWriter();
        Assertions.assertEquals("text/html;charset=utf-8", response.getContentType());
        response.setContentType("foo2/bar2;charset=utf-8");
        Assertions.assertEquals("foo2/bar2;charset=utf-8", response.getContentType());
        response.recycle();
        response.setContentType("text/xml;charset=ISO-8859-7");
        response.getWriter();
        Assertions.assertEquals("text/xml;charset=ISO-8859-7", response.getContentType());
        response.setContentType("text/html;charset=UTF-8");
        Assertions.assertEquals("text/html;charset=ISO-8859-7", response.getContentType());
        response.recycle();
        response.setContentType("text/html;charset=US-ASCII");
        response.getWriter();
        Assertions.assertEquals("text/html;charset=US-ASCII", response.getContentType());
        response.recycle();
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter();
        Assertions.assertEquals("text/html;charset=utf-8", response.getContentType());
        response.recycle();
        response.setContentType("text/json");
        response.getWriter();
        Assertions.assertEquals("text/json", response.getContentType());
        response.recycle();
        response.setContentType("text/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter();
        Assertions.assertEquals("text/json;charset=utf-8", response.getContentType());
        response.recycle();
        response.setCharacterEncoding("xyz");
        response.setContentType("foo/bar");
        Assertions.assertEquals("foo/bar;charset=xyz", response.getContentType());
        response.recycle();
        response.setContentType("foo/bar");
        response.setCharacterEncoding("xyz");
        Assertions.assertEquals("foo/bar;charset=xyz", response.getContentType());
        response.recycle();
        response.setCharacterEncoding("xyz");
        response.setContentType("foo/bar;charset=abc");
        Assertions.assertEquals("foo/bar;charset=abc", response.getContentType());
        response.recycle();
        response.setContentType("foo/bar;charset=abc");
        response.setCharacterEncoding("xyz");
        Assertions.assertEquals("foo/bar;charset=xyz", response.getContentType());
        response.recycle();
        response.setCharacterEncoding("xyz");
        response.setContentType("foo/bar");
        response.setCharacterEncoding(null);
        Assertions.assertEquals("foo/bar", response.getContentType());
        response.recycle();
        response.setCharacterEncoding("xyz");
        response.setCharacterEncoding(null);
        response.setContentType("foo/bar");
        Assertions.assertEquals("foo/bar", response.getContentType());
        response.recycle();
        response.addHeader("Content-Type", "text/something");
        Assertions.assertEquals("text/something", response.getContentType());
        response.recycle();
        response.addHeader("Content-Type", "application/json");
        response.getWriter();
        Assertions.assertEquals("application/json", response.getContentType());
    }

    @Test
    public void testInferredCharset() throws Exception {
        // Inferred from encoding.properties
        Response response = getResponse();
        Assertions.assertEquals(null, response.getContentType());
        response.setHeader("Content-Type", "application/xhtml+xml");
        Assertions.assertEquals("application/xhtml+xml", response.getContentType());
        response.getWriter();
        Assertions.assertEquals("application/xhtml+xml;charset=utf-8", response.getContentType());
        Assertions.assertEquals("utf-8", response.getCharacterEncoding());
    }

    @Test
    public void testAssumedCharset() throws Exception {
        Response response = getResponse();
        // Assumed from known types
        Assertions.assertEquals(null, response.getContentType());
        response.setHeader("Content-Type", "text/json");
        Assertions.assertEquals("text/json", response.getContentType());
        response.getWriter();
        Assertions.assertEquals("text/json", response.getContentType());
        Assertions.assertEquals("utf-8", response.getCharacterEncoding());
        response.recycle();
        // Assumed from encoding.properties
        Assertions.assertEquals(null, response.getContentType());
        response.setHeader("Content-Type", "application/vnd.api+json");
        Assertions.assertEquals("application/vnd.api+json", response.getContentType());
        response.getWriter();
        Assertions.assertEquals("application/vnd.api+json", response.getContentType());
        Assertions.assertEquals("utf-8", response.getCharacterEncoding());
    }

    @Test
    public void testStrangeContentType() throws Exception {
        Response response = getResponse();
        Assertions.assertEquals(null, response.getContentType());
        response.recycle();
        response.setContentType("text/html;charset=utf-8;charset=UTF-8");
        response.getWriter();
        Assertions.assertEquals("text/html;charset=utf-8;charset=UTF-8", response.getContentType());
        Assertions.assertEquals("utf-8", response.getCharacterEncoding().toLowerCase(Locale.ENGLISH));
    }

    @Test
    public void testLocale() throws Exception {
        Response response = getResponse();
        ContextHandler context = new ContextHandler();
        context.addLocaleEncoding(Locale.ENGLISH.toString(), "ISO-8859-1");
        context.addLocaleEncoding(Locale.ITALIAN.toString(), "ISO-8859-2");
        response.getHttpChannel().getRequest().setContext(context.getServletContext());
        response.setLocale(Locale.ITALIAN);
        Assertions.assertEquals(null, response.getContentType());
        response.setContentType("text/plain");
        Assertions.assertEquals("text/plain;charset=ISO-8859-2", response.getContentType());
        response.recycle();
        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");
        response.setLocale(Locale.ITALIAN);
        Assertions.assertEquals("text/plain;charset=utf-8", response.getContentType());
        Assertions.assertTrue(((response.toString().indexOf("charset=utf-8")) > 0));
    }

    @Test
    public void testLocaleFormat() throws Exception {
        Response response = getResponse();
        ContextHandler context = new ContextHandler();
        context.addLocaleEncoding(Locale.ENGLISH.toString(), "ISO-8859-1");
        context.addLocaleEncoding(Locale.ITALIAN.toString(), "ISO-8859-2");
        response.getHttpChannel().getRequest().setContext(context.getServletContext());
        response.setLocale(Locale.ITALIAN);
        PrintWriter out = response.getWriter();
        out.format("TestA1 %,.2f%n", 1234567.89);
        out.format("TestA2 %,.2f%n", 1234567.89);
        out.format(((Locale) (null)), "TestB1 %,.2f%n", 1234567.89);
        out.format(((Locale) (null)), "TestB2 %,.2f%n", 1234567.89);
        out.format(Locale.ENGLISH, "TestC1 %,.2f%n", 1234567.89);
        out.format(Locale.ENGLISH, "TestC2 %,.2f%n", 1234567.89);
        out.format(Locale.ITALIAN, "TestD1 %,.2f%n", 1234567.89);
        out.format(Locale.ITALIAN, "TestD2 %,.2f%n", 1234567.89);
        out.close();
        /* Test A */
        MatcherAssert.assertThat(BufferUtil.toString(_content), Matchers.containsString("TestA1 1.234.567,89"));
        MatcherAssert.assertThat(BufferUtil.toString(_content), Matchers.containsString("TestA2 1.234.567,89"));
        /* Test B */
        MatcherAssert.assertThat(BufferUtil.toString(_content), Matchers.containsString("TestB1 1.234.567,89"));
        MatcherAssert.assertThat(BufferUtil.toString(_content), Matchers.containsString("TestB2 1.234.567,89"));
        /* Test C */
        MatcherAssert.assertThat(BufferUtil.toString(_content), Matchers.containsString("TestC1 1,234,567.89"));
        MatcherAssert.assertThat(BufferUtil.toString(_content), Matchers.containsString("TestC2 1,234,567.89"));
        /* Test D */
        MatcherAssert.assertThat(BufferUtil.toString(_content), Matchers.containsString("TestD1 1.234.567,89"));
        MatcherAssert.assertThat(BufferUtil.toString(_content), Matchers.containsString("TestD2 1.234.567,89"));
    }

    @Test
    public void testContentTypeCharacterEncoding() throws Exception {
        Response response = getResponse();
        response.setContentType("foo/bar");
        response.setCharacterEncoding("utf-8");
        Assertions.assertEquals("foo/bar;charset=utf-8", response.getContentType());
        response.getWriter();
        Assertions.assertEquals("foo/bar;charset=utf-8", response.getContentType());
        response.setContentType("foo2/bar2");
        Assertions.assertEquals("foo2/bar2;charset=utf-8", response.getContentType());
        response.setCharacterEncoding("ISO-8859-1");
        Assertions.assertEquals("foo2/bar2;charset=utf-8", response.getContentType());
        response.recycle();
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        Assertions.assertEquals("text/html;charset=utf-8", response.getContentType());
        response.getWriter();
        Assertions.assertEquals("text/html;charset=utf-8", response.getContentType());
        response.setContentType("text/xml");
        Assertions.assertEquals("text/xml;charset=utf-8", response.getContentType());
        response.setCharacterEncoding("ISO-8859-1");
        Assertions.assertEquals("text/xml;charset=utf-8", response.getContentType());
    }

    @Test
    public void testCharacterEncodingContentType() throws Exception {
        Response response = getResponse();
        response.setCharacterEncoding("utf-8");
        response.setContentType("foo/bar");
        Assertions.assertEquals("foo/bar;charset=utf-8", response.getContentType());
        response.getWriter();
        Assertions.assertEquals("foo/bar;charset=utf-8", response.getContentType());
        response.setContentType("foo2/bar2");
        Assertions.assertEquals("foo2/bar2;charset=utf-8", response.getContentType());
        response.setCharacterEncoding("ISO-8859-1");
        Assertions.assertEquals("foo2/bar2;charset=utf-8", response.getContentType());
        response.recycle();
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html");
        Assertions.assertEquals("text/html;charset=utf-8", response.getContentType());
        response.getWriter();
        Assertions.assertEquals("text/html;charset=utf-8", response.getContentType());
        response.setContentType("text/xml");
        Assertions.assertEquals("text/xml;charset=utf-8", response.getContentType());
        response.setCharacterEncoding("iso-8859-1");
        Assertions.assertEquals("text/xml;charset=utf-8", response.getContentType());
    }

    @Test
    public void testContentTypeWithCharacterEncoding() throws Exception {
        Response response = getResponse();
        response.setCharacterEncoding("utf16");
        response.setContentType("foo/bar; charset=UTF-8");
        Assertions.assertEquals("foo/bar; charset=UTF-8", response.getContentType());
        response.getWriter();
        Assertions.assertEquals("foo/bar; charset=UTF-8", response.getContentType());
        response.setContentType("foo2/bar2");
        Assertions.assertEquals("foo2/bar2;charset=utf-8", response.getContentType());
        response.setCharacterEncoding("ISO-8859-1");
        Assertions.assertEquals("foo2/bar2;charset=utf-8", response.getContentType());
        response.recycle();
        response.setCharacterEncoding("utf16");
        response.setContentType("text/html; charset=utf-8");
        Assertions.assertEquals("text/html;charset=utf-8", response.getContentType());
        response.getWriter();
        Assertions.assertEquals("text/html;charset=utf-8", response.getContentType());
        response.setContentType("text/xml");
        Assertions.assertEquals("text/xml;charset=utf-8", response.getContentType());
        response.setCharacterEncoding("iso-8859-1");
        Assertions.assertEquals("text/xml;charset=utf-8", response.getContentType());
        response.recycle();
        response.setCharacterEncoding("utf-16");
        response.setContentType("foo/bar");
        Assertions.assertEquals("foo/bar;charset=utf-16", response.getContentType());
        response.getOutputStream();
        response.setCharacterEncoding("utf-8");
        Assertions.assertEquals("foo/bar;charset=utf-8", response.getContentType());
        response.flushBuffer();
        response.setCharacterEncoding("utf-16");
        Assertions.assertEquals("foo/bar;charset=utf-8", response.getContentType());
    }

    @Test
    public void testResetWithNewSession() throws Exception {
        Response response = getResponse();
        Request request = response.getHttpChannel().getRequest();
        SessionHandler session_handler = new SessionHandler();
        session_handler.setServer(_server);
        session_handler.setUsingCookies(true);
        session_handler.start();
        request.setSessionHandler(session_handler);
        HttpSession session = request.getSession(true);
        MatcherAssert.assertThat(session, Matchers.not(Matchers.nullValue()));
        Assertions.assertTrue(session.isNew());
        HttpField set_cookie = response.getHttpFields().getField(SET_COOKIE);
        MatcherAssert.assertThat(set_cookie, Matchers.not(Matchers.nullValue()));
        MatcherAssert.assertThat(set_cookie.getValue(), Matchers.startsWith("JSESSIONID"));
        MatcherAssert.assertThat(set_cookie.getValue(), Matchers.containsString(session.getId()));
        response.setHeader("Some", "Header");
        response.addCookie(new Cookie("Some", "Cookie"));
        response.getOutputStream().print("X");
        MatcherAssert.assertThat(response.getHttpFields().size(), Matchers.is(4));
        response.reset();
        set_cookie = response.getHttpFields().getField(SET_COOKIE);
        MatcherAssert.assertThat(set_cookie, Matchers.not(Matchers.nullValue()));
        MatcherAssert.assertThat(set_cookie.getValue(), Matchers.startsWith("JSESSIONID"));
        MatcherAssert.assertThat(set_cookie.getValue(), Matchers.containsString(session.getId()));
        MatcherAssert.assertThat(response.getHttpFields().size(), Matchers.is(2));
        response.getWriter();
    }

    @Test
    public void testResetContentTypeWithoutCharacterEncoding() throws Exception {
        Response response = getResponse();
        response.setCharacterEncoding("utf-8");
        response.setContentType("wrong/answer");
        response.setContentType("foo/bar");
        Assertions.assertEquals("foo/bar;charset=utf-8", response.getContentType());
        response.getWriter();
        response.setContentType("foo2/bar2");
        Assertions.assertEquals("foo2/bar2;charset=utf-8", response.getContentType());
    }

    @Test
    public void testResetContentTypeWithCharacterEncoding() throws Exception {
        Response response = getResponse();
        response.setContentType("wrong/answer;charset=utf-8");
        response.setContentType("foo/bar");
        Assertions.assertEquals("foo/bar", response.getContentType());
        response.setContentType("wrong/answer;charset=utf-8");
        response.getWriter();
        response.setContentType("foo2/bar2;charset=utf-16");
        Assertions.assertEquals("foo2/bar2;charset=utf-8", response.getContentType());
    }

    @Test
    public void testPrintln() throws Exception {
        Response response = getResponse();
        Request request = response.getHttpChannel().getRequest();
        SessionHandler session_handler = new SessionHandler();
        session_handler.setServer(_server);
        session_handler.setUsingCookies(true);
        session_handler.start();
        request.setSessionHandler(session_handler);
        HttpSession session = request.getSession(true);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        MatcherAssert.assertThat(session, Matchers.not(Matchers.nullValue()));
        Assertions.assertTrue(session.isNew());
        String expected = "";
        response.getOutputStream().print("ABC");
        expected += "ABC";
        response.getOutputStream().println("XYZ");
        expected += "XYZ\r\n";
        String s = "";
        for (int i = 0; i < 100; i++)
            s += "\u20ac\u20ac\u20ac\u20ac\u20ac\u20ac\u20ac\u20ac\u20ac\u20ac";

        response.getOutputStream().println(s);
        expected += s + "\r\n";
        response.getOutputStream().close();
        Assertions.assertEquals(expected, BufferUtil.toString(_content, StandardCharsets.UTF_8));
    }

    @Test
    public void testContentTypeWithOther() throws Exception {
        Response response = getResponse();
        response.setContentType("foo/bar; other=xyz");
        Assertions.assertEquals("foo/bar; other=xyz", response.getContentType());
        response.getWriter();
        Assertions.assertEquals("foo/bar; other=xyz;charset=iso-8859-1", response.getContentType());
        response.setContentType("foo2/bar2");
        Assertions.assertEquals("foo2/bar2;charset=iso-8859-1", response.getContentType());
        response.recycle();
        response.setCharacterEncoding("uTf-8");
        response.setContentType("text/html; other=xyz");
        Assertions.assertEquals("text/html; other=xyz;charset=utf-8", response.getContentType());
        response.getWriter();
        Assertions.assertEquals("text/html; other=xyz;charset=utf-8", response.getContentType());
        response.setContentType("text/xml");
        Assertions.assertEquals("text/xml;charset=utf-8", response.getContentType());
    }

    @Test
    public void testContentTypeWithCharacterEncodingAndOther() throws Exception {
        Response response = getResponse();
        response.setCharacterEncoding("utf16");
        response.setContentType("foo/bar; charset=utf-8 other=xyz");
        Assertions.assertEquals("foo/bar; charset=utf-8 other=xyz", response.getContentType());
        response.getWriter();
        Assertions.assertEquals("foo/bar; charset=utf-8 other=xyz", response.getContentType());
        response.recycle();
        response.setCharacterEncoding("utf16");
        response.setContentType("text/html; other=xyz charset=utf-8");
        Assertions.assertEquals("text/html; other=xyz charset=utf-8;charset=utf-16", response.getContentType());
        response.getWriter();
        Assertions.assertEquals("text/html; other=xyz charset=utf-8;charset=utf-16", response.getContentType());
        response.recycle();
        response.setCharacterEncoding("utf16");
        response.setContentType("foo/bar; other=pq charset=utf-8 other=xyz");
        Assertions.assertEquals("foo/bar; other=pq charset=utf-8 other=xyz;charset=utf-16", response.getContentType());
        response.getWriter();
        Assertions.assertEquals("foo/bar; other=pq charset=utf-8 other=xyz;charset=utf-16", response.getContentType());
    }

    @Test
    public void testStatusCodes() throws Exception {
        Response response = getResponse();
        response.sendError(404);
        Assertions.assertEquals(404, response.getStatus());
        Assertions.assertEquals("Not Found", response.getReason());
        response = getResponse();
        response.sendError(500, "Database Error");
        Assertions.assertEquals(500, response.getStatus());
        Assertions.assertEquals("Database Error", response.getReason());
        Assertions.assertEquals("must-revalidate,no-cache,no-store", response.getHeader(CACHE_CONTROL.asString()));
        response = getResponse();
        response.setStatus(200);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(null, response.getReason());
        response = getResponse();
        response.sendError(406, "Super Nanny");
        Assertions.assertEquals(406, response.getStatus());
        Assertions.assertEquals("Super Nanny", response.getReason());
        Assertions.assertEquals("must-revalidate,no-cache,no-store", response.getHeader(CACHE_CONTROL.asString()));
    }

    @Test
    public void testStatusCodesNoErrorHandler() throws Exception {
        _server.removeBean(_server.getBean(ErrorHandler.class));
        Response response = getResponse();
        response.sendError(404);
        Assertions.assertEquals(404, response.getStatus());
        Assertions.assertEquals("Not Found", response.getReason());
        response = getResponse();
        response.sendError(500, "Database Error");
        Assertions.assertEquals(500, response.getStatus());
        Assertions.assertEquals("Database Error", response.getReason());
        MatcherAssert.assertThat(response.getHeader(CACHE_CONTROL.asString()), Matchers.nullValue());
        response = getResponse();
        response.setStatus(200);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(null, response.getReason());
        response = getResponse();
        response.sendError(406, "Super Nanny");
        Assertions.assertEquals(406, response.getStatus());
        Assertions.assertEquals("Super Nanny", response.getReason());
        MatcherAssert.assertThat(response.getHeader(CACHE_CONTROL.asString()), Matchers.nullValue());
    }

    @Test
    public void testWriteRuntimeIOException() throws Exception {
        Response response = getResponse();
        PrintWriter writer = response.getWriter();
        writer.println("test");
        writer.flush();
        Assertions.assertFalse(writer.checkError());
        Throwable cause = new IOException("problem at mill");
        _channel.abort(cause);
        writer.println("test");
        Assertions.assertTrue(writer.checkError());
        RuntimeIOException e = Assertions.assertThrows(RuntimeIOException.class, () -> writer.println("test"));
        Assertions.assertEquals(cause, e.getCause());
    }

    @Test
    public void testEncodeRedirect() throws Exception {
        Response response = getResponse();
        Request request = response.getHttpChannel().getRequest();
        request.setAuthority("myhost", 8888);
        request.setContextPath("/path");
        Assertions.assertEquals("http://myhost:8888/path/info;param?query=0&more=1#target", response.encodeURL("http://myhost:8888/path/info;param?query=0&more=1#target"));
        request.setRequestedSessionId("12345");
        request.setRequestedSessionIdFromCookie(false);
        SessionHandler handler = new SessionHandler();
        DefaultSessionCache ss = new DefaultSessionCache(handler);
        NullSessionDataStore ds = new NullSessionDataStore();
        ss.setSessionDataStore(ds);
        DefaultSessionIdManager idMgr = new DefaultSessionIdManager(_server);
        idMgr.setWorkerName(null);
        handler.setSessionIdManager(idMgr);
        request.setSessionHandler(handler);
        ResponseTest.TestSession tsession = new ResponseTest.TestSession(handler, "12345");
        tsession.setExtendedId(handler.getSessionIdManager().getExtendedId("12345", null));
        request.setSession(tsession);
        handler.setCheckingRemoteSessionIdEncoding(false);
        Assertions.assertEquals("http://myhost:8888/path/info;param;jsessionid=12345?query=0&more=1#target", response.encodeURL("http://myhost:8888/path/info;param?query=0&more=1#target"));
        Assertions.assertEquals("http://other:8888/path/info;param;jsessionid=12345?query=0&more=1#target", response.encodeURL("http://other:8888/path/info;param?query=0&more=1#target"));
        Assertions.assertEquals("http://myhost/path/info;param;jsessionid=12345?query=0&more=1#target", response.encodeURL("http://myhost/path/info;param?query=0&more=1#target"));
        Assertions.assertEquals("http://myhost:8888/other/info;param;jsessionid=12345?query=0&more=1#target", response.encodeURL("http://myhost:8888/other/info;param?query=0&more=1#target"));
        handler.setCheckingRemoteSessionIdEncoding(true);
        Assertions.assertEquals("http://myhost:8888/path/info;param;jsessionid=12345?query=0&more=1#target", response.encodeURL("http://myhost:8888/path/info;param?query=0&more=1#target"));
        Assertions.assertEquals("http://other:8888/path/info;param?query=0&more=1#target", response.encodeURL("http://other:8888/path/info;param?query=0&more=1#target"));
        Assertions.assertEquals("http://myhost/path/info;param?query=0&more=1#target", response.encodeURL("http://myhost/path/info;param?query=0&more=1#target"));
        Assertions.assertEquals("http://myhost:8888/other/info;param?query=0&more=1#target", response.encodeURL("http://myhost:8888/other/info;param?query=0&more=1#target"));
        request.setContextPath("");
        Assertions.assertEquals("http://myhost:8888/;jsessionid=12345", response.encodeURL("http://myhost:8888"));
        Assertions.assertEquals("https://myhost:8888/;jsessionid=12345", response.encodeURL("https://myhost:8888"));
        Assertions.assertEquals("mailto:/foo", response.encodeURL("mailto:/foo"));
        Assertions.assertEquals("http://myhost:8888/;jsessionid=12345", response.encodeURL("http://myhost:8888/"));
        Assertions.assertEquals("http://myhost:8888/;jsessionid=12345", response.encodeURL("http://myhost:8888/;jsessionid=7777"));
        Assertions.assertEquals("http://myhost:8888/;param;jsessionid=12345?query=0&more=1#target", response.encodeURL("http://myhost:8888/;param?query=0&more=1#target"));
        Assertions.assertEquals("http://other:8888/path/info;param?query=0&more=1#target", response.encodeURL("http://other:8888/path/info;param?query=0&more=1#target"));
        handler.setCheckingRemoteSessionIdEncoding(false);
        Assertions.assertEquals("/foo;jsessionid=12345", response.encodeURL("/foo"));
        Assertions.assertEquals("/;jsessionid=12345", response.encodeURL("/"));
        Assertions.assertEquals("/foo.html;jsessionid=12345#target", response.encodeURL("/foo.html#target"));
        Assertions.assertEquals(";jsessionid=12345", response.encodeURL(""));
    }

    @Test
    public void testSendRedirect() throws Exception {
        String[][] tests = new String[][]{ // No cookie
        new String[]{ "http://myhost:8888/other/location;jsessionid=12345?name=value", "http://myhost:8888/other/location;jsessionid=12345?name=value" }, new String[]{ "/other/location;jsessionid=12345?name=value", "http://@HOST@@PORT@/other/location;jsessionid=12345?name=value" }, new String[]{ "./location;jsessionid=12345?name=value", "http://@HOST@@PORT@/path/location;jsessionid=12345?name=value" }, // From cookie
        new String[]{ "/other/location", "http://@HOST@@PORT@/other/location" }, new String[]{ "/other/l%20cation", "http://@HOST@@PORT@/other/l%20cation" }, new String[]{ "location", "http://@HOST@@PORT@/path/location" }, new String[]{ "./location", "http://@HOST@@PORT@/path/location" }, new String[]{ "../location", "http://@HOST@@PORT@/location" }, new String[]{ "/other/l%20cation", "http://@HOST@@PORT@/other/l%20cation" }, new String[]{ "l%20cation", "http://@HOST@@PORT@/path/l%20cation" }, new String[]{ "./l%20cation", "http://@HOST@@PORT@/path/l%20cation" }, new String[]{ "../l%20cation", "http://@HOST@@PORT@/l%20cation" }, new String[]{ "../locati%C3%abn", "http://@HOST@@PORT@/locati%C3%abn" }, new String[]{ "../other%2fplace", "http://@HOST@@PORT@/other%2fplace" }, new String[]{ "http://somehost.com/other/location", "http://somehost.com/other/location" } };
        int[] ports = new int[]{ 8080, 80 };
        String[] hosts = new String[]{ null, "myhost", "192.168.0.1", "0::1" };
        for (int port : ports) {
            for (String host : hosts) {
                for (int i = 0; i < (tests.length); i++) {
                    // System.err.printf("%s %d %s%n",host,port,tests[i][0]);
                    Response response = getResponse();
                    Request request = response.getHttpChannel().getRequest();
                    request.setScheme("http");
                    if (host != null)
                        request.setAuthority(host, port);

                    request.setURIPathQuery("/path/info;param;jsessionid=12345?query=0&more=1#target");
                    request.setContextPath("/path");
                    request.setRequestedSessionId("12345");
                    request.setRequestedSessionIdFromCookie((i > 2));
                    SessionHandler handler = new SessionHandler();
                    NullSessionDataStore ds = new NullSessionDataStore();
                    DefaultSessionCache ss = new DefaultSessionCache(handler);
                    handler.setSessionCache(ss);
                    ss.setSessionDataStore(ds);
                    DefaultSessionIdManager idMgr = new DefaultSessionIdManager(_server);
                    idMgr.setWorkerName(null);
                    handler.setSessionIdManager(idMgr);
                    request.setSessionHandler(handler);
                    request.setSession(new ResponseTest.TestSession(handler, "12345"));
                    handler.setCheckingRemoteSessionIdEncoding(false);
                    response.sendRedirect(tests[i][0]);
                    String location = response.getHeader("Location");
                    String expected = tests[i][1].replace("@HOST@", (host == null ? request.getLocalAddr() : host.contains(":") ? ("[" + host) + "]" : host)).replace("@PORT@", (host == null ? ":8888" : port == 80 ? "" : ":" + port));
                    Assertions.assertEquals(expected, location, ((((("test-" + i) + " ") + host) + ":") + port));
                }
            }
        }
    }

    @Test
    public void testInvalidSendRedirect() throws Exception {
        // Request is /path/info, so we need 3 ".." for an invalid redirect.
        Response response = getResponse();
        Assertions.assertThrows(IllegalStateException.class, () -> response.sendRedirect("../../../invalid"));
    }

    @Test
    public void testSetBufferSizeAfterHavingWrittenContent() throws Exception {
        Response response = getResponse();
        response.setBufferSize((20 * 1024));
        response.getWriter().print("hello");
        Assertions.assertThrows(IllegalStateException.class, () -> response.setBufferSize((21 * 1024)));
    }

    @Test
    public void testZeroContent() throws Exception {
        Response response = getResponse();
        PrintWriter writer = response.getWriter();
        response.setContentLength(0);
        Assertions.assertTrue((!(response.isCommitted())));
        Assertions.assertTrue((!(writer.checkError())));
        writer.print("");
        Assertions.assertTrue((!(writer.checkError())));
        Assertions.assertTrue(response.isCommitted());
    }

    @Test
    public void testHead() throws Exception {
        Server server = new Server(0);
        try {
            server.setHandler(new AbstractHandler() {
                @Override
                public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                    response.setStatus(200);
                    response.setContentType("text/plain");
                    PrintWriter w = response.getWriter();
                    w.flush();
                    w.println("Geht");
                    w.flush();
                    w.println("Doch");
                    w.flush();
                    setHandled(true);
                }
            });
            server.start();
            try (Socket socket = new Socket("localhost", getLocalPort())) {
                socket.setSoTimeout(500000);
                socket.getOutputStream().write("HEAD / HTTP/1.1\r\nHost: localhost\r\n\r\n".getBytes());
                socket.getOutputStream().write("GET / HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n".getBytes());
                socket.getOutputStream().flush();
                LineNumberReader reader = new LineNumberReader(new InputStreamReader(socket.getInputStream()));
                String line = reader.readLine();
                MatcherAssert.assertThat(line, Matchers.startsWith("HTTP/1.1 200 OK"));
                // look for blank line
                while ((line != null) && ((line.length()) > 0))
                    line = reader.readLine();

                // Read the first line of the GET
                line = reader.readLine();
                MatcherAssert.assertThat(line, Matchers.startsWith("HTTP/1.1 200 OK"));
                String last = null;
                while (line != null) {
                    last = line;
                    line = reader.readLine();
                } 
                Assertions.assertEquals("Doch", last);
            }
        } finally {
            server.stop();
        }
    }

    @Test
    public void testAddCookie() throws Exception {
        Response response = getResponse();
        Cookie cookie = new Cookie("name", "value");
        cookie.setDomain("domain");
        cookie.setPath("/path");
        cookie.setSecure(true);
        cookie.setComment("comment__HTTP_ONLY__");
        response.addCookie(cookie);
        String set = response.getHttpFields().get("Set-Cookie");
        Assertions.assertEquals("name=value;Path=/path;Domain=domain;Secure;HttpOnly", set);
    }

    @Test
    public void testAddCookieComplianceRFC2965() throws Exception {
        Response response = getResponse();
        response.getHttpChannel().getHttpConfiguration().setResponseCookieCompliance(RFC2965);
        Cookie cookie = new Cookie("name", "value");
        cookie.setDomain("domain");
        cookie.setPath("/path");
        cookie.setSecure(true);
        cookie.setComment("comment__HTTP_ONLY__");
        response.addCookie(cookie);
        String set = response.getHttpFields().get("Set-Cookie");
        Assertions.assertEquals("name=value;Version=1;Path=/path;Domain=domain;Secure;HttpOnly;Comment=comment", set);
    }

    /**
     * Testing behavior documented in Chrome bug
     * https://bugs.chromium.org/p/chromium/issues/detail?id=700618
     */
    @Test
    public void testAddCookie_JavaxServletHttp() throws Exception {
        Response response = getResponse();
        Cookie cookie = new Cookie("foo", URLEncoder.encode("bar;baz", StandardCharsets.UTF_8.toString()));
        cookie.setPath("/secure");
        response.addCookie(cookie);
        String set = response.getHttpFields().get("Set-Cookie");
        Assertions.assertEquals("foo=bar%3Bbaz;Path=/secure", set);
    }

    /**
     * Testing behavior documented in Chrome bug
     * https://bugs.chromium.org/p/chromium/issues/detail?id=700618
     */
    @Test
    public void testAddCookie_JavaNet() throws Exception {
        HttpCookie cookie = new HttpCookie("foo", URLEncoder.encode("bar;baz", StandardCharsets.UTF_8.toString()));
        cookie.setPath("/secure");
        Assertions.assertEquals("foo=\"bar%3Bbaz\";$Path=\"/secure\"", cookie.toString());
    }

    @Test
    public void testCookiesWithReset() throws Exception {
        Response response = getResponse();
        Cookie cookie = new Cookie("name", "value");
        cookie.setDomain("domain");
        cookie.setPath("/path");
        cookie.setSecure(true);
        cookie.setComment("comment__HTTP_ONLY__");
        response.addCookie(cookie);
        Cookie cookie2 = new Cookie("name2", "value2");
        cookie2.setDomain("domain");
        cookie2.setPath("/path");
        response.addCookie(cookie2);
        // keep the cookies
        response.reset(true);
        Enumeration<String> set = response.getHttpFields().getValues("Set-Cookie");
        Assertions.assertNotNull(set);
        ArrayList<String> list = Collections.list(set);
        MatcherAssert.assertThat(list, Matchers.containsInAnyOrder("name=value;Path=/path;Domain=domain;Secure;HttpOnly", "name2=value2;Path=/path;Domain=domain"));
        // get rid of the cookies
        response.reset();
        set = response.getHttpFields().getValues("Set-Cookie");
        Assertions.assertFalse(set.hasMoreElements());
    }

    @Test
    public void testReplaceHttpCookie() {
        Response response = getResponse();
        response.replaceCookie(new org.eclipse.jetty.http.HttpCookie("Foo", "123456"));
        response.replaceCookie(new org.eclipse.jetty.http.HttpCookie("Foo", "123456", "A", "/path"));
        response.replaceCookie(new org.eclipse.jetty.http.HttpCookie("Foo", "123456", "B", "/path"));
        response.replaceCookie(new org.eclipse.jetty.http.HttpCookie("Bar", "123456"));
        response.replaceCookie(new org.eclipse.jetty.http.HttpCookie("Bar", "123456", null, "/left"));
        response.replaceCookie(new org.eclipse.jetty.http.HttpCookie("Bar", "123456", null, "/right"));
        response.replaceCookie(new org.eclipse.jetty.http.HttpCookie("Bar", "value", null, "/right"));
        response.replaceCookie(new org.eclipse.jetty.http.HttpCookie("Bar", "value", null, "/left"));
        response.replaceCookie(new org.eclipse.jetty.http.HttpCookie("Bar", "value"));
        response.replaceCookie(new org.eclipse.jetty.http.HttpCookie("Foo", "value", "B", "/path"));
        response.replaceCookie(new org.eclipse.jetty.http.HttpCookie("Foo", "value", "A", "/path"));
        response.replaceCookie(new org.eclipse.jetty.http.HttpCookie("Foo", "value"));
        MatcherAssert.assertThat(Collections.list(response.getHttpFields().getValues("Set-Cookie")), Matchers.contains("Foo=value", "Foo=value;Path=/path;Domain=A", "Foo=value;Path=/path;Domain=B", "Bar=value", "Bar=value;Path=/left", "Bar=value;Path=/right"));
    }

    @Test
    public void testFlushAfterFullContent() throws Exception {
        Response response = getResponse();
        byte[] data = new byte[]{ ((byte) (202)), ((byte) (254)) };
        ServletOutputStream output = response.getOutputStream();
        response.setContentLength(data.length);
        // Write the whole content
        output.write(data);
        // Must not throw
        output.flush();
    }

    @Test
    public void testSetRFC2965Cookie() throws Exception {
        Response response = _channel.getResponse();
        HttpFields fields = response.getHttpFields();
        response.addSetRFC2965Cookie("null", null, null, null, (-1), null, false, false, (-1));
        Assertions.assertEquals("null=", fields.get("Set-Cookie"));
        fields.clear();
        response.addSetRFC2965Cookie("minimal", "value", null, null, (-1), null, false, false, (-1));
        Assertions.assertEquals("minimal=value", fields.get("Set-Cookie"));
        fields.clear();
        // test cookies with same name, domain and path
        response.addSetRFC2965Cookie("everything", "something", "domain", "path", 0, "noncomment", true, true, 0);
        response.addSetRFC2965Cookie("everything", "value", "domain", "path", 0, "comment", true, true, 0);
        Enumeration<String> e = fields.getValues("Set-Cookie");
        Assertions.assertTrue(e.hasMoreElements());
        Assertions.assertEquals("everything=something;Version=1;Path=path;Domain=domain;Expires=Thu, 01-Jan-1970 00:00:00 GMT;Max-Age=0;Secure;HttpOnly;Comment=noncomment", e.nextElement());
        Assertions.assertEquals("everything=value;Version=1;Path=path;Domain=domain;Expires=Thu, 01-Jan-1970 00:00:00 GMT;Max-Age=0;Secure;HttpOnly;Comment=comment", e.nextElement());
        Assertions.assertFalse(e.hasMoreElements());
        Assertions.assertEquals("Thu, 01 Jan 1970 00:00:00 GMT", fields.get("Expires"));
        Assertions.assertFalse(e.hasMoreElements());
        // test cookies with same name, different domain
        fields.clear();
        response.addSetRFC2965Cookie("everything", "other", "domain1", "path", 0, "blah", true, true, 0);
        response.addSetRFC2965Cookie("everything", "value", "domain2", "path", 0, "comment", true, true, 0);
        e = fields.getValues("Set-Cookie");
        Assertions.assertTrue(e.hasMoreElements());
        Assertions.assertEquals("everything=other;Version=1;Path=path;Domain=domain1;Expires=Thu, 01-Jan-1970 00:00:00 GMT;Max-Age=0;Secure;HttpOnly;Comment=blah", e.nextElement());
        Assertions.assertTrue(e.hasMoreElements());
        Assertions.assertEquals("everything=value;Version=1;Path=path;Domain=domain2;Expires=Thu, 01-Jan-1970 00:00:00 GMT;Max-Age=0;Secure;HttpOnly;Comment=comment", e.nextElement());
        Assertions.assertFalse(e.hasMoreElements());
        // test cookies with same name, same path, one with domain, one without
        fields.clear();
        response.addSetRFC2965Cookie("everything", "other", "domain1", "path", 0, "blah", true, true, 0);
        response.addSetRFC2965Cookie("everything", "value", "", "path", 0, "comment", true, true, 0);
        e = fields.getValues("Set-Cookie");
        Assertions.assertTrue(e.hasMoreElements());
        Assertions.assertEquals("everything=other;Version=1;Path=path;Domain=domain1;Expires=Thu, 01-Jan-1970 00:00:00 GMT;Max-Age=0;Secure;HttpOnly;Comment=blah", e.nextElement());
        Assertions.assertTrue(e.hasMoreElements());
        Assertions.assertEquals("everything=value;Version=1;Path=path;Expires=Thu, 01-Jan-1970 00:00:00 GMT;Max-Age=0;Secure;HttpOnly;Comment=comment", e.nextElement());
        Assertions.assertFalse(e.hasMoreElements());
        // test cookies with same name, different path
        fields.clear();
        response.addSetRFC2965Cookie("everything", "other", "domain1", "path1", 0, "blah", true, true, 0);
        response.addSetRFC2965Cookie("everything", "value", "domain1", "path2", 0, "comment", true, true, 0);
        e = fields.getValues("Set-Cookie");
        Assertions.assertTrue(e.hasMoreElements());
        Assertions.assertEquals("everything=other;Version=1;Path=path1;Domain=domain1;Expires=Thu, 01-Jan-1970 00:00:00 GMT;Max-Age=0;Secure;HttpOnly;Comment=blah", e.nextElement());
        Assertions.assertTrue(e.hasMoreElements());
        Assertions.assertEquals("everything=value;Version=1;Path=path2;Domain=domain1;Expires=Thu, 01-Jan-1970 00:00:00 GMT;Max-Age=0;Secure;HttpOnly;Comment=comment", e.nextElement());
        Assertions.assertFalse(e.hasMoreElements());
        // test cookies with same name, same domain, one with path, one without
        fields.clear();
        response.addSetRFC2965Cookie("everything", "other", "domain1", "path1", 0, "blah", true, true, 0);
        response.addSetRFC2965Cookie("everything", "value", "domain1", "", 0, "comment", true, true, 0);
        e = fields.getValues("Set-Cookie");
        Assertions.assertTrue(e.hasMoreElements());
        Assertions.assertEquals("everything=other;Version=1;Path=path1;Domain=domain1;Expires=Thu, 01-Jan-1970 00:00:00 GMT;Max-Age=0;Secure;HttpOnly;Comment=blah", e.nextElement());
        Assertions.assertTrue(e.hasMoreElements());
        Assertions.assertEquals("everything=value;Version=1;Domain=domain1;Expires=Thu, 01-Jan-1970 00:00:00 GMT;Max-Age=0;Secure;HttpOnly;Comment=comment", e.nextElement());
        Assertions.assertFalse(e.hasMoreElements());
        // test cookies same name only, no path, no domain
        fields.clear();
        response.addSetRFC2965Cookie("everything", "other", "", "", 0, "blah", true, true, 0);
        response.addSetRFC2965Cookie("everything", "value", "", "", 0, "comment", true, true, 0);
        e = fields.getValues("Set-Cookie");
        Assertions.assertTrue(e.hasMoreElements());
        Assertions.assertEquals("everything=other;Version=1;Expires=Thu, 01-Jan-1970 00:00:00 GMT;Max-Age=0;Secure;HttpOnly;Comment=blah", e.nextElement());
        Assertions.assertEquals("everything=value;Version=1;Expires=Thu, 01-Jan-1970 00:00:00 GMT;Max-Age=0;Secure;HttpOnly;Comment=comment", e.nextElement());
        Assertions.assertFalse(e.hasMoreElements());
        fields.clear();
        response.addSetRFC2965Cookie("ev erything", "va lue", "do main", "pa th", 1, "co mment", true, true, 1);
        String setCookie = fields.get("Set-Cookie");
        MatcherAssert.assertThat(setCookie, Matchers.startsWith("\"ev erything\"=\"va lue\";Version=1;Path=\"pa th\";Domain=\"do main\";Expires="));
        MatcherAssert.assertThat(setCookie, Matchers.endsWith(" GMT;Max-Age=1;Secure;HttpOnly;Comment=\"co mment\""));
        fields.clear();
        response.addSetRFC2965Cookie("name", "value", null, null, (-1), null, false, false, 0);
        setCookie = fields.get("Set-Cookie");
        Assertions.assertEquals((-1), setCookie.indexOf("Version="));
        fields.clear();
        response.addSetRFC2965Cookie("name", "v a l u e", null, null, (-1), null, false, false, 0);
        setCookie = fields.get("Set-Cookie");
        fields.clear();
        response.addSetRFC2965Cookie("json", "{\"services\":[\"cwa\", \"aa\"]}", null, null, (-1), null, false, false, (-1));
        Assertions.assertEquals("json=\"{\\\"services\\\":[\\\"cwa\\\", \\\"aa\\\"]}\"", fields.get("Set-Cookie"));
        fields.clear();
        response.addSetRFC2965Cookie("name", "value", "domain", null, (-1), null, false, false, (-1));
        response.addSetRFC2965Cookie("name", "other", "domain", null, (-1), null, false, false, (-1));
        response.addSetRFC2965Cookie("name", "more", "domain", null, (-1), null, false, false, (-1));
        e = fields.getValues("Set-Cookie");
        Assertions.assertTrue(e.hasMoreElements());
        MatcherAssert.assertThat(e.nextElement(), Matchers.startsWith("name=value"));
        MatcherAssert.assertThat(e.nextElement(), Matchers.startsWith("name=other"));
        MatcherAssert.assertThat(e.nextElement(), Matchers.startsWith("name=more"));
        response.addSetRFC2965Cookie("foo", "bar", "domain", null, (-1), null, false, false, (-1));
        response.addSetRFC2965Cookie("foo", "bob", "domain", null, (-1), null, false, false, (-1));
        MatcherAssert.assertThat(fields.get("Set-Cookie"), Matchers.startsWith("name=value"));
        fields.clear();
        response.addSetRFC2965Cookie("name", "value%=", null, null, (-1), null, false, false, 0);
        setCookie = fields.get("Set-Cookie");
        Assertions.assertEquals("name=value%=", setCookie);
    }

    @Test
    public void testSetRFC6265Cookie() throws Exception {
        Response response = _channel.getResponse();
        HttpFields fields = response.getHttpFields();
        response.addSetRFC6265Cookie("null", null, null, null, (-1), false, false);
        Assertions.assertEquals("null=", fields.get("Set-Cookie"));
        fields.clear();
        response.addSetRFC6265Cookie("minimal", "value", null, null, (-1), false, false);
        Assertions.assertEquals("minimal=value", fields.get("Set-Cookie"));
        fields.clear();
        // test cookies with same name, domain and path
        response.addSetRFC6265Cookie("everything", "something", "domain", "path", 0, true, true);
        response.addSetRFC6265Cookie("everything", "value", "domain", "path", 0, true, true);
        Enumeration<String> e = fields.getValues("Set-Cookie");
        Assertions.assertTrue(e.hasMoreElements());
        Assertions.assertEquals("everything=something;Path=path;Domain=domain;Expires=Thu, 01-Jan-1970 00:00:00 GMT;Max-Age=0;Secure;HttpOnly", e.nextElement());
        Assertions.assertEquals("everything=value;Path=path;Domain=domain;Expires=Thu, 01-Jan-1970 00:00:00 GMT;Max-Age=0;Secure;HttpOnly", e.nextElement());
        Assertions.assertFalse(e.hasMoreElements());
        Assertions.assertEquals("Thu, 01 Jan 1970 00:00:00 GMT", fields.get("Expires"));
        Assertions.assertFalse(e.hasMoreElements());
        // test cookies with same name, different domain
        fields.clear();
        response.addSetRFC6265Cookie("everything", "other", "domain1", "path", 0, true, true);
        response.addSetRFC6265Cookie("everything", "value", "domain2", "path", 0, true, true);
        e = fields.getValues("Set-Cookie");
        Assertions.assertTrue(e.hasMoreElements());
        Assertions.assertEquals("everything=other;Path=path;Domain=domain1;Expires=Thu, 01-Jan-1970 00:00:00 GMT;Max-Age=0;Secure;HttpOnly", e.nextElement());
        Assertions.assertTrue(e.hasMoreElements());
        Assertions.assertEquals("everything=value;Path=path;Domain=domain2;Expires=Thu, 01-Jan-1970 00:00:00 GMT;Max-Age=0;Secure;HttpOnly", e.nextElement());
        Assertions.assertFalse(e.hasMoreElements());
        // test cookies with same name, same path, one with domain, one without
        fields.clear();
        response.addSetRFC6265Cookie("everything", "other", "domain1", "path", 0, true, true);
        response.addSetRFC6265Cookie("everything", "value", "", "path", 0, true, true);
        e = fields.getValues("Set-Cookie");
        Assertions.assertTrue(e.hasMoreElements());
        Assertions.assertEquals("everything=other;Path=path;Domain=domain1;Expires=Thu, 01-Jan-1970 00:00:00 GMT;Max-Age=0;Secure;HttpOnly", e.nextElement());
        Assertions.assertTrue(e.hasMoreElements());
        Assertions.assertEquals("everything=value;Path=path;Expires=Thu, 01-Jan-1970 00:00:00 GMT;Max-Age=0;Secure;HttpOnly", e.nextElement());
        Assertions.assertFalse(e.hasMoreElements());
        // test cookies with same name, different path
        fields.clear();
        response.addSetRFC6265Cookie("everything", "other", "domain1", "path1", 0, true, true);
        response.addSetRFC6265Cookie("everything", "value", "domain1", "path2", 0, true, true);
        e = fields.getValues("Set-Cookie");
        Assertions.assertTrue(e.hasMoreElements());
        Assertions.assertEquals("everything=other;Path=path1;Domain=domain1;Expires=Thu, 01-Jan-1970 00:00:00 GMT;Max-Age=0;Secure;HttpOnly", e.nextElement());
        Assertions.assertTrue(e.hasMoreElements());
        Assertions.assertEquals("everything=value;Path=path2;Domain=domain1;Expires=Thu, 01-Jan-1970 00:00:00 GMT;Max-Age=0;Secure;HttpOnly", e.nextElement());
        Assertions.assertFalse(e.hasMoreElements());
        // test cookies with same name, same domain, one with path, one without
        fields.clear();
        response.addSetRFC6265Cookie("everything", "other", "domain1", "path1", 0, true, true);
        response.addSetRFC6265Cookie("everything", "value", "domain1", "", 0, true, true);
        e = fields.getValues("Set-Cookie");
        Assertions.assertTrue(e.hasMoreElements());
        Assertions.assertEquals("everything=other;Path=path1;Domain=domain1;Expires=Thu, 01-Jan-1970 00:00:00 GMT;Max-Age=0;Secure;HttpOnly", e.nextElement());
        Assertions.assertTrue(e.hasMoreElements());
        Assertions.assertEquals("everything=value;Domain=domain1;Expires=Thu, 01-Jan-1970 00:00:00 GMT;Max-Age=0;Secure;HttpOnly", e.nextElement());
        Assertions.assertFalse(e.hasMoreElements());
        // test cookies same name only, no path, no domain
        fields.clear();
        response.addSetRFC6265Cookie("everything", "other", "", "", 0, true, true);
        response.addSetRFC6265Cookie("everything", "value", "", "", 0, true, true);
        e = fields.getValues("Set-Cookie");
        Assertions.assertTrue(e.hasMoreElements());
        Assertions.assertEquals("everything=other;Expires=Thu, 01-Jan-1970 00:00:00 GMT;Max-Age=0;Secure;HttpOnly", e.nextElement());
        Assertions.assertEquals("everything=value;Expires=Thu, 01-Jan-1970 00:00:00 GMT;Max-Age=0;Secure;HttpOnly", e.nextElement());
        Assertions.assertFalse(e.hasMoreElements());
        String[] badNameExamples = new String[]{ "\"name\"", "name\t", "na me", "name\u0082", "na\tme", "na;me", "{name}", "[name]", "\"" };
        for (String badNameExample : badNameExamples) {
            fields.clear();
            try {
                response.addSetRFC6265Cookie(badNameExample, "value", null, "/", 1, true, true);
            } catch (IllegalArgumentException ex) {
                // System.err.printf("%s: %s%n", ex.getClass().getSimpleName(), ex.getMessage());
                MatcherAssert.assertThat((("Testing bad name: [" + badNameExample) + "]"), ex.getMessage(), CoreMatchers.allOf(Matchers.containsString("RFC6265"), Matchers.containsString("RFC2616")));
            }
        }
        String[] badValueExamples = new String[]{ "va\tlue", "\t", "value\u0000", "val\u0082ue", "va lue", "va;lue", "\"value", "value\"", "val\\ue", "val\"ue", "\"" };
        for (String badValueExample : badValueExamples) {
            fields.clear();
            try {
                response.addSetRFC6265Cookie("name", badValueExample, null, "/", 1, true, true);
            } catch (IllegalArgumentException ex) {
                // System.err.printf("%s: %s%n", ex.getClass().getSimpleName(), ex.getMessage());
                MatcherAssert.assertThat((("Testing bad value [" + badValueExample) + "]"), ex.getMessage(), Matchers.containsString("RFC6265"));
            }
        }
        String[] goodNameExamples = new String[]{ "name", "n.a.m.e", "na-me", "+name", "na*me", "na$me", "#name" };
        for (String goodNameExample : goodNameExamples) {
            fields.clear();
            response.addSetRFC6265Cookie(goodNameExample, "value", null, "/", 1, true, true);
            // should not throw an exception
        }
        String[] goodValueExamples = new String[]{ "value", "", null, "val=ue", "val-ue", "val/ue", "v.a.l.u.e" };
        for (String goodValueExample : goodValueExamples) {
            fields.clear();
            response.addSetRFC6265Cookie("name", goodValueExample, null, "/", 1, true, true);
            // should not throw an exception
        }
        fields.clear();
        response.addSetRFC6265Cookie("name", "value", "domain", null, (-1), false, false);
        response.addSetRFC6265Cookie("name", "other", "domain", null, (-1), false, false);
        response.addSetRFC6265Cookie("name", "more", "domain", null, (-1), false, false);
        e = fields.getValues("Set-Cookie");
        Assertions.assertTrue(e.hasMoreElements());
        MatcherAssert.assertThat(e.nextElement(), Matchers.startsWith("name=value"));
        MatcherAssert.assertThat(e.nextElement(), Matchers.startsWith("name=other"));
        MatcherAssert.assertThat(e.nextElement(), Matchers.startsWith("name=more"));
        response.addSetRFC6265Cookie("foo", "bar", "domain", null, (-1), false, false);
        response.addSetRFC6265Cookie("foo", "bob", "domain", null, (-1), false, false);
        MatcherAssert.assertThat(fields.get("Set-Cookie"), Matchers.startsWith("name=value"));
    }

    private static class TestSession extends Session {
        protected TestSession(SessionHandler handler, String id) {
            super(handler, new SessionData(id, "", "0.0.0.0", 0, 0, 0, 300));
        }
    }
}

