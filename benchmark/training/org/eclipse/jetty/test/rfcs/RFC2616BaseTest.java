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
package org.eclipse.jetty.test.rfcs;


import HttpHeader.CONTENT_RANGE;
import HttpStatus.BAD_REQUEST_400;
import HttpStatus.CONTINUE_100;
import HttpStatus.EXPECTATION_FAILED_417;
import HttpStatus.FOUND_302;
import HttpStatus.LENGTH_REQUIRED_411;
import HttpStatus.NOT_IMPLEMENTED_501;
import HttpStatus.OK_200;
import HttpStatus.PARTIAL_CONTENT_206;
import HttpTester.Response;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.TimeZone;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpParser;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.test.support.StringUtil;
import org.eclipse.jetty.test.support.TestableJettyServer;
import org.eclipse.jetty.test.support.rawhttp.HttpTesting;
import org.eclipse.jetty.toolchain.test.StringAssert;
import org.eclipse.jetty.util.log.StacklessLogging;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * <a href="http://tools.ietf.org/html/rfc2616">RFC 2616</a> (HTTP/1.1) Test Case
 */
public abstract class RFC2616BaseTest {
    private static final String ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ\n";

    /**
     * STRICT RFC TESTS
     */
    private static final boolean STRICT = false;

    private static TestableJettyServer server;

    private HttpTesting http;

    class TestFile {
        String name;

        String modDate;

        String data;

        long length;

        public TestFile(String name) {
            this.name = name;
            // HTTP-Date format - see RFC 2616 section 14.29
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            this.modDate = sdf.format(new Date());
        }

        public void setData(String data) {
            this.data = data;
            this.length = data.length();
        }
    }

    /**
     * Test Date/Time format Specs.
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-3.3">RFC 2616 (section 3.3)</a>
     */
    @Test
    public void test3_3() {
        Calendar expected = Calendar.getInstance();
        expected.set(Calendar.YEAR, 1994);
        expected.set(Calendar.MONTH, Calendar.NOVEMBER);
        expected.set(Calendar.DAY_OF_MONTH, 6);
        expected.set(Calendar.HOUR_OF_DAY, 8);
        expected.set(Calendar.MINUTE, 49);
        expected.set(Calendar.SECOND, 37);
        expected.set(Calendar.MILLISECOND, 0);// Milliseconds is not compared

        expected.set(Calendar.ZONE_OFFSET, 0);// Use GMT+0:00

        expected.set(Calendar.DST_OFFSET, 0);// No Daylight Savings Offset

        HttpFields fields = new HttpFields();
        // RFC 822 Preferred Format
        fields.put("D1", "Sun, 6 Nov 1994 08:49:37 GMT");
        // RFC 822 / RFC 850 Format
        fields.put("D2", "Sunday, 6-Nov-94 08:49:37 GMT");
        // RFC 850 / ANSIC C Format
        fields.put("D3", "Sun Nov  6 08:49:37 1994");
        // Test parsing
        assertDate("3.3.1 RFC 822 Preferred", expected, fields.getDateField("D1"));
        assertDate("3.3.1 RFC 822 / RFC 850", expected, fields.getDateField("D2"));
        assertDate("3.3.1 RFC 850 / ANSI C", expected, fields.getDateField("D3"));
        // Test formatting
        fields.putDateField("Date", expected.getTime().getTime());
        Assertions.assertEquals("Sun, 06 Nov 1994 08:49:37 GMT", fields.get("Date"), "3.3.1 RFC 822 preferred");
    }

    /**
     * Test Transfer Codings
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-3.6">RFC 2616 (section 3.6)</a>
     */
    @Test
    public void test3_6() throws Throwable {
        // Chunk last
        StringBuffer req1 = new StringBuffer();
        req1.append("GET /tests/R1 HTTP/1.1\n");
        req1.append("Host: localhost\n");
        req1.append("Transfer-Encoding: chunked,identity\n");// Invalid Transfer-Encoding

        req1.append("Content-Type: text/plain\n");
        req1.append("Connection: close\n");
        req1.append("\r\n");
        req1.append("5;\r\n");
        req1.append("123\r\n\r\n");
        req1.append("0;\r\n\r\n");
        HttpTester.Response response = http.request(req1);
        MatcherAssert.assertThat("3.6 Transfer Coding / Bad 400", response.getStatus(), Matchers.is(BAD_REQUEST_400));
    }

    /**
     * Test Transfer Codings
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-3.6">RFC 2616 (section 3.6)</a>
     */
    @Test
    public void test3_6_2() throws Throwable {
        // Chunked
        StringBuffer req2 = new StringBuffer();
        req2.append("GET /echo/R1 HTTP/1.1\n");
        req2.append("Host: localhost\n");
        req2.append("Transfer-Encoding: chunked\n");
        req2.append("Content-Type: text/plain\n");
        req2.append("\n");
        req2.append("2;\n");// 2 chars

        req2.append("12\n");
        req2.append("3;\n");// 3 chars

        req2.append("345\n");
        req2.append("0;\n\n");
        req2.append("GET /echo/R2 HTTP/1.1\n");
        req2.append("Host: localhost\n");
        req2.append("Transfer-Encoding: chunked\n");
        req2.append("Content-Type: text/plain\n");
        req2.append("\n");
        req2.append("4;\n");// 4 chars

        req2.append("6789\n");
        req2.append("5;\n");// 5 chars

        req2.append("abcde\n");
        req2.append("0;\n\n");// 0 chars

        req2.append("GET /echo/R3 HTTP/1.1\n");
        req2.append("Host: localhost\n");
        req2.append("Connection: close\n");
        req2.append("\n");
        List<HttpTester.Response> responses = http.requests(req2);
        Assertions.assertEquals(3, responses.size(), "Response Count");
        HttpTester.Response response = responses.get(0);// Response 1

        MatcherAssert.assertThat("3.6.1 Transfer Codings / Response 1 Code", response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat("3.6.1 Transfer Codings / Chunked String", response.getContent(), Matchers.containsString("12345\n"));
        response = responses.get(1);// Response 2

        MatcherAssert.assertThat("3.6.1 Transfer Codings / Response 2 Code", response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat("3.6.1 Transfer Codings / Chunked String", response.getContent(), Matchers.containsString("6789abcde\n"));
        response = responses.get(2);// Response 3

        MatcherAssert.assertThat("3.6.1 Transfer Codings / Response 3 Code", response.getStatus(), Matchers.is(OK_200));
        Assertions.assertEquals("", response.getContent(), "3.6.1 Transfer Codings / No Body");
    }

    /**
     * Test Transfer Codings
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-3.6">RFC 2616 (section 3.6)</a>
     */
    @Test
    public void test3_6_3() throws Throwable {
        // Chunked
        StringBuffer req3 = new StringBuffer();
        req3.append("POST /echo/R1 HTTP/1.1\n");
        req3.append("Host: localhost\n");
        req3.append("Transfer-Encoding: chunked\n");
        req3.append("Content-Type: text/plain\n");
        req3.append("\n");
        req3.append("3;\n");// 3 chars

        req3.append("fgh\n");
        req3.append("3;\n");// 3 chars

        req3.append("Ijk\n");
        req3.append("0;\n\n");// 0 chars

        req3.append("POST /echo/R2 HTTP/1.1\n");
        req3.append("Host: localhost\n");
        req3.append("Transfer-Encoding: chunked\n");
        req3.append("Content-Type: text/plain\n");
        req3.append("\n");
        req3.append("4;\n");// 4 chars

        req3.append("lmno\n");
        req3.append("5;\n");// 5 chars

        req3.append("Pqrst\n");
        req3.append("0;\n\n");// 0 chars

        req3.append("GET /echo/R3 HTTP/1.1\n");
        req3.append("Host: localhost\n");
        req3.append("Connection: close\n");
        req3.append("\n");
        List<HttpTester.Response> responses = http.requests(req3);
        Assertions.assertEquals(3, responses.size(), "Response Count");
        HttpTester.Response response = responses.get(0);// Response 1

        MatcherAssert.assertThat("3.6.1 Transfer Codings / Response 1 Code", response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat("3.6.1 Transfer Codings / Chunked String", response.getContent(), Matchers.containsString("fghIjk\n"));// Complete R1 string

        response = responses.get(1);// Response 2

        MatcherAssert.assertThat("3.6.1 Transfer Codings / Response 2 Code", response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat("3.6.1 Transfer Codings / Chunked String", response.getContent(), Matchers.containsString("lmnoPqrst\n"));// Complete R2 string

        response = responses.get(2);// Response 3

        MatcherAssert.assertThat("3.6.1 Transfer Codings / Response 3 Code", response.getStatus(), Matchers.is(OK_200));
        Assertions.assertEquals("", response.getContent(), "3.6.1 Transfer Codings / No Body");
    }

    /**
     * Test Transfer Codings
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-3.6">RFC 2616 (section 3.6)</a>
     */
    @Test
    public void test3_6_4() throws Throwable {
        // Chunked and keep alive
        StringBuffer req4 = new StringBuffer();
        req4.append("GET /echo/R1 HTTP/1.1\n");
        req4.append("Host: localhost\n");
        req4.append("Transfer-Encoding: chunked\n");
        req4.append("Content-Type: text/plain\n");
        req4.append("Connection: keep-alive\n");// keep-alive

        req4.append("\n");
        req4.append("3;\n");// 3 chars

        req4.append("123\n");
        req4.append("3;\n");// 3 chars

        req4.append("456\n");
        req4.append("0;\n\n");// 0 chars

        req4.append("GET /echo/R2 HTTP/1.1\n");
        req4.append("Host: localhost\n");
        req4.append("Connection: close\n");// close

        req4.append("\n");
        List<HttpTester.Response> responses = http.requests(req4);
        Assertions.assertEquals(2, responses.size(), "Response Count");
        HttpTester.Response response = responses.get(0);// Response 1

        MatcherAssert.assertThat("3.6.1 Transfer Codings / Response 1 Code", response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat("3.6.1 Transfer Codings / Chunked String", response.getContent(), Matchers.containsString("123456\n"));// Complete R1 string

        response = responses.get(1);// Response 2

        MatcherAssert.assertThat("3.6.1 Transfer Codings / Response 2 Code", response.getStatus(), Matchers.is(OK_200));
        Assertions.assertEquals("", response.getContent(), "3.6.1 Transfer Codings / No Body");
    }

    /**
     * Test Quality Values
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-3.9">RFC 2616 (section 3.9)</a>
     */
    @Test
    public void test3_9() {
        HttpFields fields = new HttpFields();
        fields.put("Q", "bbb;q=0.5,aaa,ccc;q=0.002,d;q=0,e;q=0.0001,ddd;q=0.001,aa2,abb;q=0.7");
        Enumeration<String> qualities = fields.getValues("Q", ", \t");
        List<?> list = HttpFields.qualityList(qualities);
        Assertions.assertEquals("aaa", HttpFields.valueParameters(list.get(0).toString(), null), "Quality parameters");
        Assertions.assertEquals("aa2", HttpFields.valueParameters(list.get(1).toString(), null), "Quality parameters");
        Assertions.assertEquals("abb", HttpFields.valueParameters(list.get(2).toString(), null), "Quality parameters");
        Assertions.assertEquals("bbb", HttpFields.valueParameters(list.get(3).toString(), null), "Quality parameters");
        Assertions.assertEquals("ccc", HttpFields.valueParameters(list.get(4).toString(), null), "Quality parameters");
        Assertions.assertEquals("ddd", HttpFields.valueParameters(list.get(5).toString(), null), "Quality parameters");
    }

    /**
     * Test Message Length
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-4.4">RFC 2616 (section 4.4)</a>
     */
    @Test
    public void test4_4() throws Exception {
        // 4.4.2 - transfer length is 'chunked' when the 'Transfer-Encoding' header
        // is provided with a value other than 'identity', unless the
        // request message is terminated with a 'Connection: close'.
        StringBuffer req1 = new StringBuffer();
        req1.append("GET /echo/R1 HTTP/1.1\n");
        req1.append("Host: localhost\n");
        req1.append("Transfer-Encoding: identity\n");
        req1.append("Content-Type: text/plain\n");
        req1.append("Content-Length: 5\n");
        req1.append("\n");
        req1.append("123\r\n");
        req1.append("GET /echo/R2 HTTP/1.1\n");
        req1.append("Host: localhost\n");
        req1.append("Connection: close\n");
        req1.append("\n");
        List<HttpTester.Response> responses = http.requests(req1);
        Assertions.assertEquals(2, responses.size(), "Response Count");
        HttpTester.Response response = responses.get(0);
        MatcherAssert.assertThat("4.4.2 Message Length / Response Code", response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat("4.4.2 Message Length / Body", response.getContent(), Matchers.containsString("123\n"));
        response = responses.get(1);
        MatcherAssert.assertThat("4.4.2 Message Length / Response Code", response.getStatus(), Matchers.is(OK_200));
        Assertions.assertEquals("", response.getContent(), "4.4.2 Message Length / No Body");
        // 4.4.3 -
        // Client - do not send 'Content-Length' if entity-length
        // and the transfer-length are different.
        // Server - ignore 'Content-Length' if 'Transfer-Encoding' is provided.
        StringBuffer req2 = new StringBuffer();
        req2.append("GET /echo/R1 HTTP/1.1\n");
        req2.append("Host: localhost\n");
        req2.append("Transfer-Encoding: chunked\n");
        req2.append("Content-Type: text/plain\n");
        req2.append("Content-Length: 100\n");
        req2.append("\n");
        req2.append("3;\n");
        req2.append("123\n");
        req2.append("3;\n");
        req2.append("456\n");
        req2.append("0;\n");
        req2.append("\n");
        req2.append("GET /echo/R2 HTTP/1.1\n");
        req2.append("Host: localhost\n");
        req2.append("Connection: close\n");
        req2.append("Content-Type: text/plain\n");
        req2.append("Content-Length: 6\n");
        req2.append("\n");
        req2.append("7890AB");
        responses = http.requests(req2);
        Assertions.assertEquals(1, responses.size(), "Response Count");
        response = responses.get(0);// response 1

        Assertions.assertEquals(BAD_REQUEST_400, response.getStatus(), "4.4.3 Ignore Content-Length / Response Code");
        // 4.4 - Server can request valid Content-Length from client if client
        // fails to provide a Content-Length.
        // Server can respond with 400 (Bad Request) or 411 (Length Required).
        // NOTE: MSIE breaks this rule
        // TODO: Document which version of MSIE Breaks Rule.
        // TODO: Document which versions of MSIE pass this rule (if any).
        if (RFC2616BaseTest.STRICT) {
            StringBuffer req3 = new StringBuffer();
            req3.append("GET /echo/R2 HTTP/1.1\n");
            req3.append("Host: localhost\n");
            req3.append("Content-Type: text/plain\n");
            req3.append("Connection: close\n");
            req3.append("\n");
            req3.append("123456");
            response = http.request(req3);
            MatcherAssert.assertThat("4.4 Valid Content-Length Required", response.getStatus(), Matchers.is(LENGTH_REQUIRED_411));
            Assertions.assertTrue(((response.getContent()) == null), "4.4 Valid Content-Length Required");
            StringBuffer req4 = new StringBuffer();
            req4.append("GET /echo/R2 HTTP/1.0\n");
            req4.append("Content-Type: text/plain\n");
            req4.append("\n");
            req4.append("123456");
            response = http.request(req4);
            MatcherAssert.assertThat("4.4 Valid Content-Length Required", response.getStatus(), Matchers.is(LENGTH_REQUIRED_411));
            Assertions.assertTrue(((response.getContent()) == null), "4.4 Valid Content-Length Required");
        }
    }

    /**
     * Test The Resource Identified by a Request
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-5.2">RFC 2616 (section 5.2)</a>
     */
    @Test
    public void test5_2_DefaultHost() throws Exception {
        // Default Host
        StringBuffer req1 = new StringBuffer();
        req1.append("GET /tests/index.html HTTP/1.1\n");
        req1.append("Host: localhost\n");// default host

        req1.append("Connection: close\n");
        req1.append("\r\n");
        HttpTester.Response response = http.request(req1);
        MatcherAssert.assertThat("5.2 Default Host", response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat("5.2 Default Host", response.getContent(), Matchers.containsString("Default DOCRoot"));
    }

    /**
     * Test The Resource Identified by a Request
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-5.2">RFC 2616 (section 5.2)</a>
     */
    @Test
    public void test5_2_VirtualHost() throws Exception {
        // Virtual Host
        StringBuffer req2 = new StringBuffer();
        req2.append("GET /tests/ HTTP/1.1\n");
        req2.append("Host: VirtualHost\n");// simple virtual host

        req2.append("Connection: close\n");
        req2.append("\r\n");
        HttpTester.Response response = http.request(req2);
        MatcherAssert.assertThat("5.2 Virtual Host", response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat("5.2 Virtual Host", response.getContent(), Matchers.containsString("VirtualHost DOCRoot"));
    }

    /**
     * Test The Resource Identified by a Request
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-5.2">RFC 2616 (section 5.2)</a>
     */
    @Test
    public void test5_2_VirtualHostInsensitive() throws Exception {
        // Virtual Host case insensitive
        StringBuffer req3 = new StringBuffer();
        req3.append("GET /tests/ HTTP/1.1\n");
        req3.append("Host: ViRtUalhOst\n");// mixed case host

        req3.append("Connection: close\n");
        req3.append("\n");
        HttpTester.Response response = http.request(req3);
        Assertions.assertEquals(OK_200, response.getStatus(), "5.2 Virtual Host (mixed case)");
        MatcherAssert.assertThat("5.2 Virtual Host (mixed case)", response.getContent(), Matchers.containsString("VirtualHost DOCRoot"));
    }

    /**
     * Test The Resource Identified by a Request
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-5.2">RFC 2616 (section 5.2)</a>
     */
    @Test
    public void test5_2_NoVirtualHost() throws Exception {
        // No Virtual Host
        StringBuffer req4 = new StringBuffer();
        req4.append("GET /tests/ HTTP/1.1\n");
        req4.append("Connection: close\n");
        req4.append("\n");// no virtual host

        HttpTester.Response response = http.request(req4);
        MatcherAssert.assertThat("5.2 No Host", response.getStatus(), Matchers.is(BAD_REQUEST_400));
    }

    /**
     * Test The Resource Identified by a Request
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-5.2">RFC 2616 (section 5.2)</a>
     */
    @Test
    public void test5_2_BadVirtualHost() throws Exception {
        // Bad Virtual Host
        StringBuffer req5 = new StringBuffer();
        req5.append("GET /tests/ HTTP/1.1\n");
        req5.append("Host: bad.eclipse.org\n");// Bad virtual host

        req5.append("Connection: close\n");
        req5.append("\n");
        HttpTester.Response response = http.request(req5);
        MatcherAssert.assertThat("5.2 Bad Host", response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat("5.2 Bad Host", response.getContent(), Matchers.containsString("Default DOCRoot"));// served by default context

    }

    /**
     * Test The Resource Identified by a Request
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-5.2">RFC 2616 (section 5.2)</a>
     */
    @Test
    public void test5_2_VirtualHostAbsoluteURI_Http11_WithoutHostHeader() throws Exception {
        // Virtual Host as Absolute URI
        StringBuffer req6 = new StringBuffer();
        req6.append("GET http://VirtualHost/tests/ HTTP/1.1\n");
        req6.append("Connection: close\n");
        req6.append("\n");
        HttpTester.Response response = http.request(req6);
        // No host header should always return a 400 Bad Request by 19.6.1.1
        Assertions.assertEquals(BAD_REQUEST_400, response.getStatus(), "5.2 Virtual Host as AbsoluteURI (No Host Header / HTTP 1.1)");
    }

    /**
     * Test The Resource Identified by a Request
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-5.2">RFC 2616 (section 5.2)</a>
     */
    @Test
    public void test5_2_VirtualHostAbsoluteURI_Http10_WithoutHostHeader() throws Exception {
        // Virtual Host as Absolute URI
        StringBuffer req6 = new StringBuffer();
        req6.append("GET http://VirtualHost/tests/ HTTP/1.0\n");
        req6.append("Connection: close\n");
        req6.append("\n");
        HttpTester.Response response = http.request(req6);
        Assertions.assertEquals(OK_200, response.getStatus(), "5.2 Virtual Host as AbsoluteURI (No Host Header / HTTP 1.0)");
        MatcherAssert.assertThat("5.2 Virtual Host as AbsoluteURI (No Host Header / HTTP 1.1)", response.getContent(), Matchers.containsString("VirtualHost DOCRoot"));
    }

    /**
     * Test The Resource Identified by a Request
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-5.2">RFC 2616 (section 5.2)</a>
     */
    @Test
    public void test5_2_VirtualHostAbsoluteURI_WithHostHeader() throws Exception {
        // Virtual Host as Absolute URI (with Host header)
        StringBuffer req7 = new StringBuffer();
        req7.append("GET http://VirtualHost/tests/ HTTP/1.1\n");
        req7.append("Host: localhost\n");// is ignored (would normally trigger default context)

        req7.append("Connection: close\n");
        req7.append("\n");
        HttpTester.Response response = http.request(req7);
        Assertions.assertEquals(OK_200, response.getStatus(), "5.2 Virtual Host as AbsoluteURI (and Host header)");
        MatcherAssert.assertThat("5.2 Virtual Host as AbsoluteURI (and Host header)", response.getContent(), Matchers.containsString("VirtualHost DOCRoot"));
    }

    /**
     * Test Persistent Connections
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-8.1">RFC 2616 (section 8.1)</a>
     */
    @Test
    public void test8_1() throws Exception {
        StringBuffer req1 = new StringBuffer();
        req1.append("GET /tests/R1.txt HTTP/1.1\n");
        req1.append("Host: localhost\n");
        req1.append("Connection: close\n");
        req1.append("\n");
        HttpTester.Response response = http.request(req1);
        MatcherAssert.assertThat("8.1 Persistent Connections", response.getStatus(), Matchers.is(OK_200));
        Assertions.assertTrue(((response.get("Content-Length")) != null), "8.1 Persistent Connections");
        MatcherAssert.assertThat("8.1 Persistent Connections", response.getContent(), Matchers.containsString("Resource=R1"));
        StringBuffer req2 = new StringBuffer();
        req2.append("GET /tests/R1.txt HTTP/1.1\n");
        req2.append("Host: localhost\n");
        req2.append("\n");
        req2.append("GET /tests/R2.txt HTTP/1.1\n");
        req2.append("Host: localhost\n");
        req2.append("Connection: close\n");
        req2.append("\n");
        req2.append("GET /tests/R3.txt HTTP/1.1\n");
        req2.append("Host: localhost\n");
        req2.append("Connection: close\n");
        req2.append("\n");
        List<HttpTester.Response> responses = http.requests(req2);
        Assertions.assertEquals(2, responses.size(), "Response Count");// Should not have a R3 response.

        response = responses.get(0);// response 1

        MatcherAssert.assertThat("8.1 Persistent Connections", response.getStatus(), Matchers.is(OK_200));
        Assertions.assertTrue(((response.get("Content-Length")) != null), "8.1 Persistent Connections");
        MatcherAssert.assertThat("8.1 Peristent Connections", response.getContent(), Matchers.containsString("Resource=R1"));
        response = responses.get(1);// response 2

        MatcherAssert.assertThat("8.1.2.2 Persistent Connections / Pipeline", response.getStatus(), Matchers.is(OK_200));
        Assertions.assertTrue(((response.get("Content-Length")) != null), "8.1.2.2 Persistent Connections / Pipeline");
        Assertions.assertEquals("close", response.get("Connection"), "8.1.2.2 Persistent Connections / Pipeline");
        MatcherAssert.assertThat("8.1.2.2 Peristent Connections / Pipeline", response.getContent(), Matchers.containsString("Resource=R2"));
    }

    /**
     * Test Message Transmission Requirements -- Bad client behaviour, invalid Expect header.
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-8.2">RFC 2616 (section 8.2)</a>
     */
    @Test
    public void test8_2_ExpectInvalid() throws Exception {
        // Expect Failure
        StringBuffer req2 = new StringBuffer();
        req2.append("GET /echo/R1 HTTP/1.1\n");
        req2.append("Host: localhost\n");
        req2.append("Expect: unknown\n");// Invalid Expect header.

        req2.append("Content-Type: text/plain\n");
        req2.append("Content-Length: 8\n");
        req2.append("\n");
        req2.append("12345678\n");
        HttpTester.Response response = http.request(req2);
        MatcherAssert.assertThat("8.2.3 expect failure", response.getStatus(), Matchers.is(EXPECTATION_FAILED_417));
    }

    /**
     * Test Message Transmission Requirements -- Acceptable bad client behavior, Expect 100 with body content.
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-8.2">RFC 2616 (section 8.2)</a>
     */
    @Test
    public void test8_2_ExpectWithBody() throws Exception {
        // Expect with body
        StringBuffer req3 = new StringBuffer();
        req3.append("GET /echo/R1 HTTP/1.1\n");
        req3.append("Host: localhost\n");
        req3.append("Expect: 100-continue\n");// Valid Expect header.

        req3.append("Content-Type: text/plain\n");
        req3.append("Content-Length: 8\n");
        req3.append("Connection: close\n");
        req3.append("\n");
        req3.append("123456\r\n");// Body

        // Should only expect 1 response.
        // The existence of 2 responses usually means a bad "HTTP/1.1 100" was received.
        HttpTester.Response response = http.request(req3);
        MatcherAssert.assertThat("8.2.3 expect 100", response.getStatus(), Matchers.is(OK_200));
    }

    /**
     * Test Message Transmission Requirements -- Acceptable bad client behavior, Expect 100 with body content.
     *
     * @throws Exception
     * 		failure
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-8.2">RFC 2616 (section 8.2)</a>
     */
    @Test
    public void test8_2_UnexpectWithBody() throws Exception {
        // Expect with body
        StringBuffer req3 = new StringBuffer();
        req3.append("GET /redirect/R1 HTTP/1.1\n");
        req3.append("Host: localhost\n");
        req3.append("Expect: 100-continue\n");// Valid Expect header.

        req3.append("Content-Type: text/plain\n");
        req3.append("Content-Length: 8\n");
        req3.append("\n");
        req3.append("123456\r\n");
        req3.append("GET /echo/R1 HTTP/1.1\n");
        req3.append("Host: localhost\n");
        req3.append("Content-Type: text/plain\n");
        req3.append("Content-Length: 8\n");
        req3.append("Connection: close\n");
        req3.append("\n");
        req3.append("87654321");// Body

        List<HttpTester.Response> responses = http.requests(req3);
        HttpTester.Response response = responses.get(0);
        Assertions.assertEquals(302, response.getStatus(), "8.2.3 ignored no 100");
        Assertions.assertEquals("close", response.get("Connection"));
        Assertions.assertEquals(1, responses.size());
    }

    /**
     * Test Message Transmission Requirements
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-8.2">RFC 2616 (section 8.2)</a>
     */
    @Test
    public void test8_2_ExpectNormal() throws Exception {
        // Expect 100
        StringBuffer req4 = new StringBuffer();
        req4.append("GET /echo/R1 HTTP/1.1\n");
        req4.append("Host: localhost\n");
        req4.append("Connection: close\n");
        req4.append("Expect: 100-continue\n");// Valid Expect header.

        req4.append("Content-Type: text/plain\n");
        req4.append("Content-Length: 7\n");
        req4.append("\n");// No body

        Socket sock = http.open();
        try {
            http.send(sock, req4);
            http.setTimeoutMillis(2000);
            HttpTester.Response response = http.readAvailable(sock);
            MatcherAssert.assertThat("8.2.3 expect 100", response.getStatus(), Matchers.is(CONTINUE_100));
            http.send(sock, "654321\n");// Now send the data

            response = http.read(sock);
            MatcherAssert.assertThat("8.2.3 expect 100", response.getStatus(), Matchers.is(OK_200));
            MatcherAssert.assertThat("8.2.3 expect 100", response.getContent(), Matchers.containsString("654321\n"));
        } finally {
            http.close(sock);
        }
    }

    /**
     * Test OPTIONS (HTTP) method - Server Options
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-9.2">RFC 2616 (section 9.2)</a>
     */
    @Test
    public void test9_2_ServerOptions() throws Exception {
        // Unsupported in Jetty.
        // Server can handle many webapps, each with their own set of supported OPTIONS.
        // Both www.cnn.com and www.apache.org do NOT support this request as well.
        if (RFC2616BaseTest.STRICT) {
            // Server OPTIONS
            StringBuffer req1 = new StringBuffer();
            req1.append("OPTIONS * HTTP/1.1\n");// Apply to server in general, rather than a specific resource

            req1.append("Connection: close\n");
            req1.append("Host: localhost\n");
            req1.append("\n");
            HttpTester.Response response = http.request(req1);
            MatcherAssert.assertThat("9.2 OPTIONS", response.getStatus(), Matchers.is(OK_200));
            Assertions.assertTrue(((response.get("Allow")) != null), "9.2 OPTIONS");
            // Header expected ...
            // Allow: GET, HEAD, POST, PUT, DELETE, MOVE, OPTIONS, TRACE
            String allow = response.get("Allow");
            String[] expectedMethods = new String[]{ "GET", "HEAD", "POST", "PUT", "DELETE", "MOVE", "OPTIONS", "TRACE" };
            for (String expectedMethod : expectedMethods) {
                MatcherAssert.assertThat(allow, Matchers.containsString(expectedMethod));
            }
            Assertions.assertEquals("0", response.get("Content-Length"), "9.2 OPTIONS");// Required if no response body.

        }
    }

    /**
     * Test OPTIONS (HTTP) method - Resource Options
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-9.2">RFC 2616 (section 9.2)</a>
     */
    @Test
    public void test9_2_ResourceOptions() throws Exception {
        // Jetty is conditionally compliant.
        // Possible Bug in the Spec.
        // The content-length: 0 in the spec is not appropriate if the connection is being closed.
        // Resource specific OPTIONS
        StringBuffer req2 = new StringBuffer();
        req2.append("OPTIONS /rfc2616-webapp/httpmethods HTTP/1.1\n");// Apply to specific resource

        req2.append("Host: localhost\n");
        req2.append("\n");
        // Test issues 2 requests. first as OPTIONS (not closed),
        // second as GET (closed), this is to allow the 2 conflicting aspects of the
        // RFC2616 rules with regards to section 9.2 (OPTIONS) and section 4.4 (Message Length)
        // to not conflict with each other.
        req2.append("GET /rfc2616-webapp/httpmethods HTTP/1.1\n");
        req2.append("Host: localhost\n");
        req2.append("Connection: close\n");// Close this second request

        req2.append("\n");
        List<HttpTester.Response> responses = http.requests(req2);
        Assertions.assertEquals(2, responses.size(), "Response Count");// Should have 2 responses

        HttpTester.Response response = responses.get(0);// Only interested in first response

        Assertions.assertTrue(((response.get("Allow")) != null), "9.2 OPTIONS");
        // Header expected ...
        // Allow: GET, HEAD, POST, TRACE, OPTIONS
        String allow = response.get("Allow");
        String[] expectedMethods = new String[]{ "GET", "HEAD", "POST", "OPTIONS", "TRACE" };
        for (String expectedMethod : expectedMethods) {
            MatcherAssert.assertThat(allow, Matchers.containsString(expectedMethod));
        }
        Assertions.assertEquals("0", response.get("Content-Length"), "9.2 OPTIONS");// Required if no response body.

    }

    /**
     * Test HEAD (HTTP) method
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-9.4">RFC 2616 (section 9.4)</a>
     */
    @Test
    public void test9_4() throws Exception {
        /* Test GET first. (should have body) */
        StringBuffer req1 = new StringBuffer();
        req1.append("GET /tests/R1.txt HTTP/1.1\n");
        req1.append("Host: localhost\n");
        req1.append("Connection: close\n");
        req1.append("\n");
        HttpTester.Response response = http.request(req1);
        MatcherAssert.assertThat("9.4 GET / Response Code", response.getStatus(), Matchers.is(OK_200));
        Assertions.assertEquals("text/plain", response.get("Content-Type"), "9.4 GET / Content Type");
        Assertions.assertEquals("25", response.get("Content-Length"), "9.4 HEAD / Content Type");
        MatcherAssert.assertThat("9.4 GET / Body", response.getContent(), Matchers.containsString("Host=Default\nResource=R1\n"));
        /* Test HEAD next. (should have no body) */
        StringBuffer req2 = new StringBuffer();
        req2.append("HEAD /tests/R1.txt HTTP/1.1\n");
        req2.append("Host: localhost\n");
        req2.append("Connection: close\n");
        req2.append("\n");
        // Need to get the HEAD response in a RAW format, as HttpParser.parse()
        // can't properly parse a HEAD response.
        Socket sock = http.open();
        try {
            http.send(sock, req2);
            String rawHeadResponse = http.readRaw(sock);
            int headResponseLength = rawHeadResponse.length();
            // Only interested in the response header from the GET request above.
            String rawGetResponse = response.toString().substring(0, headResponseLength);
            // As there is a possibility that the time between GET and HEAD requests
            // can cross the second mark. (eg: GET at 11:00:00.999 and HEAD at 11:00:01.001)
            // So with that knowledge, we will remove the 'Date:' header from both sides before comparing.
            List<String> linesGet = StringUtil.asLines(rawGetResponse.trim());
            List<String> linesHead = StringUtil.asLines(rawHeadResponse.trim());
            StringUtil.removeStartsWith("Date: ", linesGet);
            StringUtil.removeStartsWith("Date: ", linesHead);
            // Compare the 2 lists of lines to make sure they contain the same information
            // Do not worry about order of the headers, as that's not important to test,
            // just the existence of the same headers
            StringAssert.assertContainsSame("9.4 HEAD equals GET", linesGet, linesHead);
        } finally {
            http.close(sock);
        }
    }

    /**
     * Test 206 Partial Content (Response Code)
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.2.7">RFC 2616 (section 10.2.7)</a>
     */
    @Test
    public void test10_2_7() throws Exception {
        // check to see if corresponding GET w/o range would return
        // a) ETag
        // b) Content-Location
        // these same headers will be required for corresponding
        // sub range requests
        StringBuffer req1 = new StringBuffer();
        req1.append("GET /rfc2616-webapp/alpha.txt HTTP/1.1\n");
        req1.append("Host: localhost\n");
        req1.append("Connection: close\n");
        req1.append("\n");
        HttpTester.Response response = http.request(req1);
        boolean noRangeHasContentLocation = (response.get("Content-Location")) != null;
        boolean noRangeHasETag = (response.get("ETag")) != null;
        // now try again for the same resource but this time WITH range header
        StringBuffer req2 = new StringBuffer();
        req2.append("GET /rfc2616-webapp/alpha.txt HTTP/1.1\n");
        req2.append("Host: localhost\n");
        req2.append("Connection: close\n");
        req2.append("Range: bytes=1-3\n");// request first 3 bytes

        req2.append("\n");
        response = http.request(req2);
        MatcherAssert.assertThat("10.2.7 Partial Content", response.getStatus(), Matchers.is(PARTIAL_CONTENT_206));
        // (point 1) A 206 response MUST contain either a Content-Range header
        // field (section 14.16) indicating the range included with this
        // response, or a multipart/byteranges Content-Type including Content-Range
        // fields for each part. If a Content-Length header field is present
        // in the response, its value MUST match the actual number of OCTETs
        // transmitted in the message-body.
        if ((response.get("Content-Range")) != null) {
            Assertions.assertEquals("bytes 1-3/27", response.get("Content-Range"), "10.2.7 Partial Content / Response / Content Range");
        }
        if ((response.get("Content-Length")) != null) {
            Assertions.assertEquals("3", response.get("Content-Length"), "10.2.7 Patial Content / Response / Content Length");
        }
        // (point 2) A 206 response MUST contain a Date header
        Assertions.assertTrue(((response.get("Date")) != null), "10.2.7 Partial Content / Response / Date");
        // (point 3) A 206 response MUST contain ETag and/or Content-Location,
        // if the header would have been sent in a 200 response to the same request
        if (noRangeHasContentLocation) {
            Assertions.assertTrue(((response.get("Content-Location")) != null), "10.2.7 Partial Content / Content-Location");
        }
        if (noRangeHasETag) {
            Assertions.assertTrue(((response.get("ETag")) != null), "10.2.7 Partial Content / Content-Location");
        }
        // (point 4) A 206 response MUST contain Expires, Cache-Control, and/or Vary,
        // if the field-value might differ from that sent in any previous response
        // for the same variant
        // TODO: Not sure how to test this condition.
        // Test the body sent
        MatcherAssert.assertThat("10.2.7 Partial Content", response.getContent(), Matchers.containsString("BCD"));// should only have bytes 1-3

    }

    /**
     * Test Redirection 3xx (Response Code)
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.3">RFC 2616 (section 10.3)</a>
     */
    @Test
    public void test10_3_RedirectHttp10Path() throws Exception {
        String specId;
        String serverURI = RFC2616BaseTest.server.getServerURI().toASCIIString();
        // HTTP/1.0
        StringBuffer req1 = new StringBuffer();
        req1.append("GET /redirect/ HTTP/1.0\n");
        req1.append("Connection: Close\n");
        req1.append("\n");
        HttpTester.Response response = http.request(req1);
        specId = "10.3 Redirection HTTP/1.0 - basic";
        MatcherAssert.assertThat(specId, response.getStatus(), Matchers.is(FOUND_302));
        Assertions.assertEquals((serverURI + "/tests/"), response.get("Location"), specId);
    }

    /**
     * Test Redirection 3xx (Response Code)
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.3">RFC 2616 (section 10.3)</a>
     */
    @Test
    public void test10_3_RedirectHttp11Path() throws Exception {
        // HTTP/1.1
        StringBuffer req2 = new StringBuffer();
        req2.append("GET /redirect/ HTTP/1.1\n");
        req2.append("Host: localhost\n");
        req2.append("\n");
        req2.append("GET /redirect/ HTTP/1.1\n");
        req2.append("Host: localhost\n");
        req2.append("Connection: close\n");
        req2.append("\n");
        List<HttpTester.Response> responses = http.requests(req2);
        Assertions.assertEquals(2, responses.size(), "Response Count");
        HttpTester.Response response = responses.get(0);
        String specId = "10.3 Redirection HTTP/1.1 - basic (response 1)";
        MatcherAssert.assertThat(specId, response.getStatus(), Matchers.is(FOUND_302));
        Assertions.assertEquals(((RFC2616BaseTest.server.getScheme()) + "://localhost/tests/"), response.get("Location"), specId);
        response = responses.get(1);
        specId = "10.3 Redirection HTTP/1.1 - basic (response 2)";
        MatcherAssert.assertThat(specId, response.getStatus(), Matchers.is(FOUND_302));
        Assertions.assertEquals(((RFC2616BaseTest.server.getScheme()) + "://localhost/tests/"), response.get("Location"), specId);
        Assertions.assertEquals("close", response.get("Connection"), specId);
    }

    /**
     * Test Redirection 3xx (Response Code)
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.3">RFC 2616 (section 10.3)</a>
     */
    @Test
    public void test10_3_RedirectHttp10Resource() throws Exception {
        // HTTP/1.0 - redirect with resource/content
        StringBuffer req3 = new StringBuffer();
        req3.append("GET /redirect/R1.txt HTTP/1.0\n");
        req3.append("Host: localhost\n");
        req3.append("Connection: close\n");
        req3.append("\n");
        HttpTester.Response response = http.request(req3);
        String specId = "10.3 Redirection HTTP/1.0 w/content";
        MatcherAssert.assertThat(specId, response.getStatus(), Matchers.is(FOUND_302));
        Assertions.assertEquals(((RFC2616BaseTest.server.getScheme()) + "://localhost/tests/R1.txt"), response.get("Location"), specId);
    }

    /**
     * Test Redirection 3xx (Response Code)
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.3">RFC 2616 (section 10.3)</a>
     */
    @Test
    public void test10_3_RedirectHttp11Resource() throws Exception {
        // HTTP/1.1 - redirect with resource/content
        StringBuffer req4 = new StringBuffer();
        req4.append("GET /redirect/R2.txt HTTP/1.1\n");
        req4.append("Host: localhost\n");
        req4.append("Connection: close\n");
        req4.append("\n");
        HttpTester.Response response = http.request(req4);
        String specId = "10.3 Redirection HTTP/1.1 w/content";
        MatcherAssert.assertThat((specId + " [status]"), response.getStatus(), Matchers.is(FOUND_302));
        MatcherAssert.assertThat((specId + " [location]"), response.get("Location"), Matchers.is(((RFC2616BaseTest.server.getScheme()) + "://localhost/tests/R2.txt")));
        MatcherAssert.assertThat((specId + " [connection]"), response.get("Connection"), Matchers.is("close"));
        MatcherAssert.assertThat((specId + " [content-length]"), response.get("Content-Length"), Matchers.nullValue());
    }

    /**
     * Test Accept-Encoding (Header Field)
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-14.3">RFC 2616 (section 14.3)</a>
     */
    @Test
    public void test14_3_AcceptEncodingGzip() throws Exception {
        String specId;
        // Gzip accepted
        StringBuffer req1 = new StringBuffer();
        req1.append("GET /rfc2616-webapp/solutions.html HTTP/1.1\n");
        req1.append("Host: localhost\n");
        req1.append("Accept-Encoding: gzip\n");
        req1.append("Connection: close\n");
        req1.append("\n");
        HttpTester.Response response = http.request(req1);
        specId = "14.3 Accept-Encoding Header";
        MatcherAssert.assertThat(specId, response.getStatus(), Matchers.is(OK_200));
        Assertions.assertEquals("gzip", response.get("Content-Encoding"), specId);
        Assertions.assertEquals("text/html", response.get("Content-Type"), specId);
    }

    /**
     * Test Content-Range (Header Field)
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-14.16">RFC 2616 (section 14.16)</a>
     */
    @Test
    public void test14_16_NoRange() throws Exception {
        // 
        // calibrate with normal request (no ranges); if this doesnt
        // work, dont expect ranges to work either
        // 
        StringBuffer req1 = new StringBuffer();
        req1.append("GET /rfc2616-webapp/alpha.txt HTTP/1.1\n");
        req1.append("Host: localhost\n");
        req1.append("Connection: close\n");
        req1.append("\n");
        HttpTester.Response response = http.request(req1);
        MatcherAssert.assertThat(response.toString(), response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(response.getContent(), Matchers.containsString(RFC2616BaseTest.ALPHA));
    }

    /**
     * Test Content-Range (Header Field) - Tests multiple ranges, where all defined ranges are syntactically valid, however some ranges are outside of the
     * limits of the available data
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-14.16">RFC 2616 (section 14.16)</a>
     */
    @Test
    public void test14_16_PartialRange() throws Exception {
        String alpha = RFC2616BaseTest.ALPHA;
        // server should not return a 416 if at least one syntactically valid ranges
        // are is satisfiable
        assertPartialContentRange("bytes=5-8,50-60", "5-8/27", alpha.substring(5, (8 + 1)));
        assertPartialContentRange("bytes=50-60,5-8", "5-8/27", alpha.substring(5, (8 + 1)));
    }

    /**
     * Test Content-Range (Header Field) - Tests single Range request header with 2 ranges defined, where there is a mixed case of validity, 1 range invalid,
     * another 1 valid.
     *
     * Only the valid range should be processed. The invalid range should be ignored.
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-14.16">RFC 2616 (section 14.16)</a>
     */
    @Test
    public void test14_16_PartialRange_MixedRanges() throws Exception {
        String alpha = RFC2616BaseTest.ALPHA;
        // server should not return a 416 if at least one syntactically valid ranges
        // are is satisfiable
        // 
        // should test for combinations of good and syntactically
        // invalid ranges here, but I am not certain what the right
        // behavior is anymore
        // 
        // return data for valid ranges while ignoring unsatisfiable
        // ranges
        // a) Range: bytes=a-b,5-8
        StringBuffer req1 = new StringBuffer();
        req1.append("GET /rfc2616-webapp/alpha.txt HTTP/1.1\n");
        req1.append("Host: localhost\n");
        req1.append("Range: bytes=a-b,5-8\n");// Invalid range, then Valid range

        req1.append("Connection: close\n");
        req1.append("\n");
        http.setTimeoutMillis(60000);
        HttpTester.Response response = http.request(req1);
        String specId = "Partial Range (Mixed): 'bytes=a-b,5-8'";
        MatcherAssert.assertThat(specId, response.getStatus(), Matchers.is(PARTIAL_CONTENT_206));
        MatcherAssert.assertThat(specId, response.get(CONTENT_RANGE), Matchers.is("bytes 5-8/27"));
        MatcherAssert.assertThat(specId, response.getContent(), Matchers.containsString(alpha.substring(5, (8 + 1))));
    }

    /**
     * Test Content-Range (Header Field) - Tests single Range request header with 2 ranges defined, where there is a mixed case of validity, 1 range invalid,
     * another 1 valid.
     *
     * Only the valid range should be processed. The invalid range should be ignored.
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-14.16">RFC 2616 (section 14.16)</a>
     */
    @Test
    public void test14_16_PartialRange_MixedBytes() throws Exception {
        String alpha = RFC2616BaseTest.ALPHA;
        // server should not return a 416 if at least one syntactically valid ranges
        // are is satisfiable
        // 
        // should test for combinations of good and syntactically
        // invalid ranges here, but I am not certain what the right
        // behavior is anymore
        // 
        // return data for valid ranges while ignoring unsatisfiable
        // ranges
        // b) Range: bytes=a-b,bytes=5-8
        StringBuffer req1 = new StringBuffer();
        req1.append("GET /rfc2616-webapp/alpha.txt HTTP/1.1\n");
        req1.append("Host: localhost\n");
        req1.append("Range: bytes=a-b,bytes=5-8\n");// Invalid range, then Valid range

        req1.append("Connection: close\n");
        req1.append("\n");
        HttpTester.Response response = http.request(req1);
        String specId = "Partial Range (Mixed): 'bytes=a-b,bytes=5-8'";
        MatcherAssert.assertThat(specId, response.getStatus(), Matchers.is(PARTIAL_CONTENT_206));
        MatcherAssert.assertThat(specId, response.get(CONTENT_RANGE), Matchers.is("bytes 5-8/27"));
        MatcherAssert.assertThat(specId, response.getContent(), Matchers.containsString(alpha.substring(5, (8 + 1))));
    }

    /**
     * Test Content-Range (Header Field) - Tests multiple Range request headers, where there is a mixed case of validity, 1 range invalid, another 1 valid.
     *
     * Only the valid range should be processed. The invalid range should be ignored.
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-14.16">RFC 2616 (section 14.16)</a>
     */
    @Test
    public void test14_16_PartialRange_MixedMultiple() throws Exception {
        String alpha = RFC2616BaseTest.ALPHA;
        // server should not return a 416 if at least one syntactically valid ranges
        // are is satisfiable
        // 
        // should test for combinations of good and syntactically
        // invalid ranges here, but I am not certain what the right
        // behavior is anymore
        // 
        // return data for valid ranges while ignoring unsatisfiable
        // ranges
        // c) Range: bytes=a-b
        // Range: bytes=5-8
        StringBuffer req1 = new StringBuffer();
        req1.append("GET /rfc2616-webapp/alpha.txt HTTP/1.1\n");
        req1.append("Host: localhost\n");
        req1.append("Range: bytes=a-b\n");// Invalid range

        req1.append("Range: bytes=5-8\n");// Valid range

        req1.append("Connection: close\n");
        req1.append("\n");
        HttpTester.Response response = http.request(req1);
        String specId = "Partial Range (Mixed): 'bytes=a-b' 'bytes=5-8'";
        MatcherAssert.assertThat(specId, response.getStatus(), Matchers.is(PARTIAL_CONTENT_206));
        MatcherAssert.assertThat(specId, response.get(CONTENT_RANGE), Matchers.is("bytes 5-8/27"));
        MatcherAssert.assertThat(specId, response.getContent(), Matchers.containsString(alpha.substring(5, (8 + 1))));
    }

    /**
     * Test Host (Header Field)
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-14.23">RFC 2616 (section 14.23)</a>
     */
    @Test
    public void test14_23_Http10_NoHostHeader() throws Exception {
        // HTTP/1.0 OK with no host
        StringBuffer req1 = new StringBuffer();
        req1.append("GET /tests/R1.txt HTTP/1.0\n");
        req1.append("Connection: close\n");
        req1.append("\n");
        HttpTester.Response response = http.request(req1);
        MatcherAssert.assertThat("14.23 HTTP/1.0 - No Host", response.getStatus(), Matchers.is(OK_200));
    }

    /**
     * Test Host (Header Field)
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-14.23">RFC 2616 (section 14.23)</a>
     */
    @Test
    public void test14_23_Http11_NoHost() throws Exception {
        // HTTP/1.1 400 (bad request) with no host
        StringBuffer req2 = new StringBuffer();
        req2.append("GET /tests/R1.txt HTTP/1.1\n");
        req2.append("Connection: close\n");
        req2.append("\n");
        HttpTester.Response response = http.request(req2);
        MatcherAssert.assertThat("14.23 HTTP/1.1 - No Host", response.getStatus(), Matchers.is(BAD_REQUEST_400));
    }

    /**
     * Test Host (Header Field)
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-14.23">RFC 2616 (section 14.23)</a>
     */
    @Test
    public void test14_23_ValidHost() throws Exception {
        // HTTP/1.1 - Valid host
        StringBuffer req3 = new StringBuffer();
        req3.append("GET /tests/R1.txt HTTP/1.1\n");
        req3.append("Host: localhost\n");
        req3.append("Connection: close\n");
        req3.append("\n");
        HttpTester.Response response = http.request(req3);
        MatcherAssert.assertThat("14.23 HTTP/1.1 - Valid Host", response.getStatus(), Matchers.is(OK_200));
    }

    /**
     * Test Host (Header Field)
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-14.23">RFC 2616 (section 14.23)</a>
     */
    @Test
    public void test14_23_IncompleteHostHeader() throws Exception {
        // HTTP/1.1 - Incomplete (empty) Host header
        try (StacklessLogging stackless = new StacklessLogging(HttpParser.class)) {
            StringBuffer req4 = new StringBuffer();
            req4.append("GET /tests/R1.txt HTTP/1.1\n");
            req4.append("Host:\n");
            req4.append("Connection: close\n");
            req4.append("\n");
            HttpTester.Response response = http.request(req4);
            MatcherAssert.assertThat("14.23 HTTP/1.1 - Empty Host", response.getStatus(), Matchers.is(OK_200));
        }
    }

    /**
     * Test Range (Header Field)
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-14.35">RFC 2616 (section 14.35)</a>
     */
    @Test
    public void test14_35_Range() throws Exception {
        // 
        // test various valid range specs that have not been
        // tested yet
        // 
        String alpha = RFC2616BaseTest.ALPHA;
        // First 3 bytes
        assertByteRange("bytes=0-2", "0-2/27", alpha.substring(0, (2 + 1)));
        // From byte offset 23 thru the end of the content
        assertByteRange("bytes=23-", "23-26/27", alpha.substring(23));
        // Request byte offset 23 thru 42 (only 26 bytes in content)
        // The last 3 bytes are returned.
        assertByteRange("bytes=23-42", "23-26/27", alpha.substring(23, (26 + 1)));
        // Request the last 3 bytes
        assertByteRange("bytes=-3", "24-26/27", alpha.substring(24, (26 + 1)));
    }

    /**
     * Test Range (Header Field)
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-14.35">RFC 2616 (section 14.35)</a>
     */
    @Test
    public void test14_35_Range_Multipart1() throws Exception {
        String rangedef = "23-23,-2";// Request byte at offset 23, and the last 2 bytes

        StringBuffer req1 = new StringBuffer();
        req1.append("GET /rfc2616-webapp/alpha.txt HTTP/1.1\n");
        req1.append("Host: localhost\n");
        req1.append("Range: ").append(rangedef).append("\n");
        req1.append("Connection: close\n");
        req1.append("\n");
        HttpTester.Response response = http.request(req1);
        String specId = ("Partial (Byte) Range: '" + rangedef) + "'";
        MatcherAssert.assertThat(specId, response.getStatus(), Matchers.is(PARTIAL_CONTENT_206));
        String contentType = response.get("Content-Type");
        // RFC states that multiple parts should result in multipart/byteranges Content type.
        StringAssert.assertContains((specId + " Content-Type"), contentType, "multipart/byteranges");
        // Collect 'boundary' string
        String boundary = null;
        String[] parts = StringUtil.split(contentType, ';');
        for (int i = 0; i < (parts.length); i++) {
            if (parts[i].trim().startsWith("boundary=")) {
                String[] boundparts = StringUtil.split(parts[i], '=');
                Assertions.assertEquals(2, boundparts.length, (specId + " Boundary parts.length"));
                boundary = boundparts[1];
            }
        }
        Assertions.assertNotNull(boundary, (specId + " Should have found boundary in Content-Type header"));
        List<String> lines = StringUtil.asLines(response.getContent().trim());
        int i = 0;
        Assertions.assertEquals(("--" + boundary), lines.get((i++)));
        Assertions.assertEquals("Content-Type: text/plain", lines.get((i++)));
        Assertions.assertEquals("Content-Range: bytes 23-23/27", lines.get((i++)));
        Assertions.assertEquals("", lines.get((i++)));
        Assertions.assertEquals("X", lines.get((i++)));
        Assertions.assertEquals(("--" + boundary), lines.get((i++)));
        Assertions.assertEquals("Content-Type: text/plain", lines.get((i++)));
        Assertions.assertEquals("Content-Range: bytes 25-26/27", lines.get((i++)));
        Assertions.assertEquals("", lines.get((i++)));
        Assertions.assertEquals("Z", lines.get((i++)));
        Assertions.assertEquals("", lines.get((i++)));
        Assertions.assertEquals((("--" + boundary) + "--"), lines.get((i++)));
    }

    /**
     * Test Range (Header Field)
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-14.35">RFC 2616 (section 14.35)</a>
     */
    @Test
    public void test14_35_PartialRange() throws Exception {
        // 
        // test various valid range specs that have not been
        // tested yet
        // 
        String alpha = RFC2616BaseTest.ALPHA;
        // server should not return a 416 if at least one syntactically valid ranges
        // are is satisfiable
        assertByteRange("bytes=5-8,50-60", "5-8/27", alpha.substring(5, (8 + 1)));
        assertByteRange("bytes=50-60,5-8", "5-8/27", alpha.substring(5, (8 + 1)));
    }

    /**
     * Test Range (Header Field) - Bad Range Request
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-14.35">RFC 2616 (section 14.35)</a>
     */
    @Test
    public void test14_35_BadRange_InvalidSyntax() throws Exception {
        // server should ignore all range headers which include
        // at least one syntactically invalid range
        assertBadByteRange("bytes=a-b");// Invalid due to non-digit entries

        assertBadByteRange("bytes=-");// Invalid due to missing range ends

        assertBadByteRange("bytes=-1-");// Invalid due negative to end range

        assertBadByteRange("doublehalfwords=1-2");// Invalid due to bad key 'doublehalfwords'

    }

    /**
     * Test TE (Header Field) / Transfer Codings
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-14.39">RFC 2616 (section 14.39)</a>
     */
    @Test
    public void test14_39_TEGzip() throws Exception {
        if (RFC2616BaseTest.STRICT) {
            String specId;
            // Gzip accepted
            StringBuffer req1 = new StringBuffer();
            req1.append("GET /rfc2616-webapp/solutions.html HTTP/1.1\n");
            req1.append("Host: localhost\n");
            req1.append("TE: gzip\n");
            req1.append("Connection: close\n");
            req1.append("\n");
            HttpTester.Response response = http.request(req1);
            specId = "14.39 TE Header";
            MatcherAssert.assertThat(specId, response.getStatus(), Matchers.is(OK_200));
            Assertions.assertEquals("gzip", response.get("Transfer-Encoding"), specId);
        }
    }

    /**
     * Test TE (Header Field) / Transfer Codings
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-14.39">RFC 2616 (section 14.39)</a>
     */
    @Test
    public void test14_39_TEDeflate() throws Exception {
        if (RFC2616BaseTest.STRICT) {
            String specId;
            // Deflate not accepted
            StringBuffer req2 = new StringBuffer();
            req2.append("GET /rfc2616-webapp/solutions.html HTTP/1.1\n");
            req2.append("Host: localhost\n");
            req2.append("TE: deflate\n");// deflate not accepted

            req2.append("Connection: close\n");
            req2.append("\n");
            HttpTester.Response response = http.request(req2);
            specId = "14.39 TE Header";
            MatcherAssert.assertThat(specId, response.getStatus(), Matchers.is(NOT_IMPLEMENTED_501));// Error on TE (deflate not supported)

        }
    }

    /**
     * Test Compatibility with Previous (HTTP) Versions.
     *
     * @see <a href="http://tools.ietf.org/html/rfc2616#section-19.6">RFC 2616 (section 19.6)</a>
     */
    @Test
    public void test19_6() throws Exception {
        String specId;
        /* Compatibility with HTTP/1.0 */
        StringBuffer req1 = new StringBuffer();
        req1.append("GET /tests/R1.txt HTTP/1.0\n");
        req1.append("\n");
        HttpTester.Response response = http.request(req1);
        specId = "19.6 Compatibility with HTTP/1.0 - simple request";
        MatcherAssert.assertThat(specId, response.getStatus(), Matchers.is(OK_200));
        Assertions.assertTrue(((response.get("Connection")) == null), (specId + " - connection closed not assumed"));
        /* Compatibility with HTTP/1.0 */
        StringBuffer req2 = new StringBuffer();
        req2.append("GET /tests/R1.txt HTTP/1.0\n");
        req2.append("Host: localhost\n");
        req2.append("Connection: keep-alive\n");
        req2.append("\n");
        req2.append("GET /tests/R2.txt HTTP/1.0\n");
        req2.append("Host: localhost\n");
        req2.append("Connection: close\n");// Connection closed here

        req2.append("\n");
        req2.append("GET /tests/R3.txt HTTP/1.0\n");// This request should not be handled

        req2.append("Host: localhost\n");
        req2.append("Connection: close\n");
        req2.append("\n");
        List<HttpTester.Response> responses = http.requests(req2);
        // Since R2 closes the connection, should only get 2 responses (R1 &
        // R2), not (R3)
        Assertions.assertEquals(2, responses.size(), "Response Count");
        response = responses.get(0);// response 1

        specId = "19.6.2 Compatibility with previous HTTP - Keep-alive";
        MatcherAssert.assertThat(specId, response.getStatus(), Matchers.is(OK_200));
        Assertions.assertEquals("keep-alive", response.get("Connection"), specId);
        MatcherAssert.assertThat(specId, response.getContent(), Matchers.containsString("Resource=R1"));
        response = responses.get(1);// response 2

        MatcherAssert.assertThat(specId, response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(specId, response.getContent(), Matchers.containsString("Resource=R2"));
        /* Compatibility with HTTP/1.0 */
        StringBuffer req3 = new StringBuffer();
        req3.append("GET /echo/R1 HTTP/1.0\n");
        req3.append("Host: localhost\n");
        req3.append("Connection: keep-alive\n");
        req3.append("Content-Length: 10\n");
        req3.append("\n");
        req3.append("1234567890\n");
        req3.append("GET /echo/RA HTTP/1.0\n");
        req3.append("Host: localhost\n");
        req3.append("Connection: keep-alive\n");
        req3.append("Content-Length: 10\n");
        req3.append("\n");
        req3.append("ABCDEFGHIJ\n");
        req3.append("GET /tests/R2.txt HTTP/1.0\n");
        req3.append("Host: localhost\n");
        req3.append("Connection: close\n");// Close connection here

        req3.append("\n");
        req3.append("GET /tests/R3.txt HTTP/1.0\n");// This request should not

        // be handled.
        req3.append("Host: localhost\n");
        req3.append("Connection: close\n");
        req3.append("\n");
        responses = http.requests(req3);
        Assertions.assertEquals(3, responses.size(), "Response Count");
        specId = "19.6.2 Compatibility with HTTP/1.0- Keep-alive";
        response = responses.get(0);
        MatcherAssert.assertThat(specId, response.getStatus(), Matchers.is(OK_200));
        Assertions.assertEquals("keep-alive", response.get("Connection"), specId);
        MatcherAssert.assertThat(specId, response.getContent(), Matchers.containsString("1234567890\n"));
        response = responses.get(1);
        MatcherAssert.assertThat(specId, response.getStatus(), Matchers.is(OK_200));
        Assertions.assertEquals("keep-alive", response.get("Connection"), specId);
        MatcherAssert.assertThat(specId, response.getContent(), Matchers.containsString("ABCDEFGHIJ\n"));
        response = responses.get(2);
        MatcherAssert.assertThat(specId, response.getStatus(), Matchers.is(OK_200));
        MatcherAssert.assertThat(specId, response.getContent(), Matchers.containsString("Host=Default\nResource=R2\n"));
    }
}

