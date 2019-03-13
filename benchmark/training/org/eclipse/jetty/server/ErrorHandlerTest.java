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


import HttpHeader.CONTENT_TYPE;
import HttpTester.Response;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpTester;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class ErrorHandlerTest {
    static Server server;

    static LocalConnector connector;

    @Test
    public void test404NoAccept() throws Exception {
        String response = ErrorHandlerTest.connector.getResponse(("GET / HTTP/1.1\r\n" + ("Host: Localhost\r\n" + "\r\n")));
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 404 "));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Content-Length: 0")));
        MatcherAssert.assertThat(response, Matchers.containsString("Content-Type: text/html;charset=iso-8859-1"));
    }

    @Test
    public void test404EmptyAccept() throws Exception {
        String response = ErrorHandlerTest.connector.getResponse(("GET / HTTP/1.1\r\n" + (("Accept: \r\n" + "Host: Localhost\r\n") + "\r\n")));
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 404 "));
        MatcherAssert.assertThat(response, Matchers.containsString("Content-Length: 0"));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Content-Type")));
    }

    @Test
    public void test404UnAccept() throws Exception {
        String response = ErrorHandlerTest.connector.getResponse(("GET / HTTP/1.1\r\n" + (("Accept: text/*;q=0\r\n" + "Host: Localhost\r\n") + "\r\n")));
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 404 "));
        MatcherAssert.assertThat(response, Matchers.containsString("Content-Length: 0"));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Content-Type")));
    }

    @Test
    public void test404AllAccept() throws Exception {
        String response = ErrorHandlerTest.connector.getResponse(("GET / HTTP/1.1\r\n" + (("Host: Localhost\r\n" + "Accept: */*\r\n") + "\r\n")));
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 404 "));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Content-Length: 0")));
        MatcherAssert.assertThat(response, Matchers.containsString("Content-Type: text/html;charset=iso-8859-1"));
    }

    @Test
    public void test404HtmlAccept() throws Exception {
        String response = ErrorHandlerTest.connector.getResponse(("GET / HTTP/1.1\r\n" + (("Host: Localhost\r\n" + "Accept: text/html\r\n") + "\r\n")));
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 404 "));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Content-Length: 0")));
        MatcherAssert.assertThat(response, Matchers.containsString("Content-Type: text/html;charset=iso-8859-1"));
    }

    @Test
    public void test404HtmlAcceptAnyCharset() throws Exception {
        String response = ErrorHandlerTest.connector.getResponse(("GET / HTTP/1.1\r\n" + ((("Host: Localhost\r\n" + "Accept: text/html\r\n") + "Accept-Charset: *\r\n") + "\r\n")));
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 404 "));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Content-Length: 0")));
        MatcherAssert.assertThat(response, Matchers.containsString("Content-Type: text/html;charset=utf-8"));
    }

    @Test
    public void test404HtmlAcceptUtf8Charset() throws Exception {
        String response = ErrorHandlerTest.connector.getResponse(("GET / HTTP/1.1\r\n" + ((("Host: Localhost\r\n" + "Accept: text/html\r\n") + "Accept-Charset: utf-8\r\n") + "\r\n")));
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 404 "));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Content-Length: 0")));
        MatcherAssert.assertThat(response, Matchers.containsString("Content-Type: text/html;charset=utf-8"));
    }

    @Test
    public void test404HtmlAcceptNotUtf8Charset() throws Exception {
        String response = ErrorHandlerTest.connector.getResponse(("GET / HTTP/1.1\r\n" + ((("Host: Localhost\r\n" + "Accept: text/html\r\n") + "Accept-Charset: utf-8;q=0\r\n") + "\r\n")));
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 404 "));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Content-Length: 0")));
        MatcherAssert.assertThat(response, Matchers.containsString("Content-Type: text/html;charset=iso-8859-1"));
    }

    @Test
    public void test404HtmlAcceptNotUtf8UnknownCharset() throws Exception {
        String response = ErrorHandlerTest.connector.getResponse(("GET / HTTP/1.1\r\n" + ((("Host: Localhost\r\n" + "Accept: text/html\r\n") + "Accept-Charset: utf-8;q=0,unknown\r\n") + "\r\n")));
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 404 "));
        MatcherAssert.assertThat(response, Matchers.containsString("Content-Length: 0"));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Content-Type")));
    }

    @Test
    public void test404HtmlAcceptUnknownUtf8Charset() throws Exception {
        String response = ErrorHandlerTest.connector.getResponse(("GET / HTTP/1.1\r\n" + ((("Host: Localhost\r\n" + "Accept: text/html\r\n") + "Accept-Charset: utf-8;q=0.1,unknown\r\n") + "\r\n")));
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 404 "));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Content-Length: 0")));
        MatcherAssert.assertThat(response, Matchers.containsString("Content-Type: text/html;charset=utf-8"));
    }

    @Test
    public void test404PreferHtml() throws Exception {
        String response = ErrorHandlerTest.connector.getResponse(("GET / HTTP/1.1\r\n" + ((("Host: Localhost\r\n" + "Accept: text/html;q=1.0,text/json;q=0.5,*/*\r\n") + "Accept-Charset: *\r\n") + "\r\n")));
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 404 "));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Content-Length: 0")));
        MatcherAssert.assertThat(response, Matchers.containsString("Content-Type: text/html;charset=utf-8"));
    }

    @Test
    public void test404PreferJson() throws Exception {
        String response = ErrorHandlerTest.connector.getResponse(("GET / HTTP/1.1\r\n" + ((("Host: Localhost\r\n" + "Accept: text/html;q=0.5,text/json;q=1.0,*/*\r\n") + "Accept-Charset: *\r\n") + "\r\n")));
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 404 "));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Content-Length: 0")));
        MatcherAssert.assertThat(response, Matchers.containsString("Content-Type: text/json"));
    }

    @Test
    public void testCharEncoding() throws Exception {
        String rawResponse = ErrorHandlerTest.connector.getResponse(("GET /charencoding/foo HTTP/1.1\r\n" + (("Host: Localhost\r\n" + "Accept: text/plain\r\n") + "\r\n")));
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat("Response status code", response.getStatus(), Matchers.is(404));
        HttpField contentType = response.getField(CONTENT_TYPE);
        MatcherAssert.assertThat("Response Content-Type", contentType, Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat("Response Content-Type value", contentType.getValue(), Matchers.not(Matchers.containsString("null")));
    }

    @Test
    public void testBadMessage() throws Exception {
        String rawResponse = ErrorHandlerTest.connector.getResponse(("GET /badmessage/444 HTTP/1.1\r\n" + ("Host: Localhost\r\n" + "\r\n")));
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat("Response status code", response.getStatus(), Matchers.is(444));
    }
}

