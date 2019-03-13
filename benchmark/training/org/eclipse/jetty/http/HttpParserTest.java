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
package org.eclipse.jetty.http;


import BufferUtil.EMPTY_BUFFER;
import HttpCompliance.CUSTOM0;
import HttpComplianceSection.CASE_INSENSITIVE_FIELD_VALUE_CACHE;
import HttpComplianceSection.FIELD_NAME_CASE_INSENSITIVE;
import HttpComplianceSection.METHOD_CASE_SENSITIVE;
import HttpComplianceSection.NO_HTTP_0_9;
import HttpComplianceSection.NO_WS_AFTER_FIELD_NAME;
import HttpComplianceSection.TRANSFER_ENCODING_WITH_CONTENT_LENGTH;
import HttpMethod.GET;
import HttpMethod.MOVE;
import HttpParser.RequestHandler;
import HttpParser.ResponseHandler;
import HttpParser.State.CLOSE;
import HttpParser.State.CLOSED;
import State.END;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.log.StacklessLogging;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static HttpCompliance.CUSTOM0;
import static HttpCompliance.LEGACY;
import static HttpCompliance.RFC2616;
import static HttpCompliance.RFC2616_LEGACY;
import static HttpCompliance.RFC7230;
import static HttpCompliance.RFC7230_LEGACY;


public class HttpParserTest {
    static {
        CUSTOM0.sections().remove(NO_WS_AFTER_FIELD_NAME);
    }

    @Test
    public void HttpMethodTest() {
        Assertions.assertNull(HttpMethod.lookAheadGet(BufferUtil.toBuffer("Wibble ")));
        Assertions.assertNull(HttpMethod.lookAheadGet(BufferUtil.toBuffer("GET")));
        Assertions.assertNull(HttpMethod.lookAheadGet(BufferUtil.toBuffer("MO")));
        Assertions.assertEquals(GET, HttpMethod.lookAheadGet(BufferUtil.toBuffer("GET ")));
        Assertions.assertEquals(MOVE, HttpMethod.lookAheadGet(BufferUtil.toBuffer("MOVE ")));
        ByteBuffer b = BufferUtil.allocateDirect(128);
        BufferUtil.append(b, BufferUtil.toBuffer("GET"));
        Assertions.assertNull(HttpMethod.lookAheadGet(b));
        BufferUtil.append(b, BufferUtil.toBuffer(" "));
        Assertions.assertEquals(GET, HttpMethod.lookAheadGet(b));
    }

    @Test
    public void testLineParse_Mock_IP() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("POST /mock/127.0.0.1 HTTP/1.1\r\n" + "\r\n"));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertEquals("POST", _methodOrVersion);
        Assertions.assertEquals("/mock/127.0.0.1", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.1", _versionOrReason);
        Assertions.assertEquals((-1), _headers);
    }

    @Test
    public void testLineParse0() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("POST /foo HTTP/1.0\r\n" + "\r\n"));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertEquals("POST", _methodOrVersion);
        Assertions.assertEquals("/foo", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals((-1), _headers);
    }

    @Test
    public void testLineParse1_RFC2616() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer("GET /999\r\n");
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler, RFC2616_LEGACY);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertNull(_bad);
        Assertions.assertEquals("GET", _methodOrVersion);
        Assertions.assertEquals("/999", _uriOrStatus);
        Assertions.assertEquals("HTTP/0.9", _versionOrReason);
        Assertions.assertEquals((-1), _headers);
        MatcherAssert.assertThat(_complianceViolation, Matchers.contains(NO_HTTP_0_9));
    }

    @Test
    public void testLineParse1() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer("GET /999\r\n");
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertEquals("HTTP/0.9 not supported", _bad);
        MatcherAssert.assertThat(_complianceViolation, Matchers.empty());
    }

    @Test
    public void testLineParse2_RFC2616() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer("POST /222  \r\n");
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler, RFC2616_LEGACY);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertNull(_bad);
        Assertions.assertEquals("POST", _methodOrVersion);
        Assertions.assertEquals("/222", _uriOrStatus);
        Assertions.assertEquals("HTTP/0.9", _versionOrReason);
        Assertions.assertEquals((-1), _headers);
        MatcherAssert.assertThat(_complianceViolation, Matchers.contains(NO_HTTP_0_9));
    }

    @Test
    public void testLineParse2() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer("POST /222  \r\n");
        _versionOrReason = null;
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertEquals("HTTP/0.9 not supported", _bad);
        MatcherAssert.assertThat(_complianceViolation, Matchers.empty());
    }

    @Test
    public void testLineParse3() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("POST /fo\u0690 HTTP/1.0\r\n" + "\r\n"), StandardCharsets.UTF_8);
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertEquals("POST", _methodOrVersion);
        Assertions.assertEquals("/fo\u0690", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals((-1), _headers);
    }

    @Test
    public void testLineParse4() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("POST /foo?param=\u0690 HTTP/1.0\r\n" + "\r\n"), StandardCharsets.UTF_8);
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertEquals("POST", _methodOrVersion);
        Assertions.assertEquals("/foo?param=\u0690", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals((-1), _headers);
    }

    @Test
    public void testLongURLParse() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("POST /123456789abcdef/123456789abcdef/123456789abcdef/123456789abcdef/123456789abcdef/123456789abcdef/123456789abcdef/123456789abcdef/123456789abcdef/123456789abcdef/123456789abcdef/123456789abcdef/123456789abcdef/123456789abcdef/123456789abcdef/123456789abcdef/ HTTP/1.0\r\n" + "\r\n"));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertEquals("POST", _methodOrVersion);
        Assertions.assertEquals("/123456789abcdef/123456789abcdef/123456789abcdef/123456789abcdef/123456789abcdef/123456789abcdef/123456789abcdef/123456789abcdef/123456789abcdef/123456789abcdef/123456789abcdef/123456789abcdef/123456789abcdef/123456789abcdef/123456789abcdef/123456789abcdef/", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals((-1), _headers);
    }

    @Test
    public void testAllowedLinePreamble() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer("\r\n\r\nGET / HTTP/1.0\r\n");
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertEquals("GET", _methodOrVersion);
        Assertions.assertEquals("/", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals((-1), _headers);
    }

    @Test
    public void testDisallowedLinePreamble() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer("\r\n \r\nGET / HTTP/1.0\r\n");
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertEquals("Illegal character SPACE=' '", _bad);
    }

    @Test
    public void testConnect() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("CONNECT 192.168.1.2:80 HTTP/1.1\r\n" + "\r\n"));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertEquals("CONNECT", _methodOrVersion);
        Assertions.assertEquals("192.168.1.2:80", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.1", _versionOrReason);
        Assertions.assertEquals((-1), _headers);
    }

    @Test
    public void testSimple() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.0\r\n" + (("Host: localhost\r\n" + "Connection: close\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertTrue(_headerCompleted);
        Assertions.assertTrue(_messageCompleted);
        Assertions.assertEquals("GET", _methodOrVersion);
        Assertions.assertEquals("/", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals("Host", _hdr[0]);
        Assertions.assertEquals("localhost", _val[0]);
        Assertions.assertEquals("Connection", _hdr[1]);
        Assertions.assertEquals("close", _val[1]);
        Assertions.assertEquals(1, _headers);
    }

    @Test
    public void testFoldedField2616() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.0\r\n" + ((((("Host: localhost\r\n" + "Name: value\r\n") + " extra\r\n") + "Name2: \r\n") + "\tvalue2\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler, RFC2616_LEGACY);
        HttpParserTest.parseAll(parser, buffer);
        MatcherAssert.assertThat(_bad, Matchers.nullValue());
        Assertions.assertEquals("Host", _hdr[0]);
        Assertions.assertEquals("localhost", _val[0]);
        Assertions.assertEquals(2, _headers);
        Assertions.assertEquals("Name", _hdr[1]);
        Assertions.assertEquals("value extra", _val[1]);
        Assertions.assertEquals("Name2", _hdr[2]);
        Assertions.assertEquals("value2", _val[2]);
        MatcherAssert.assertThat(_complianceViolation, Matchers.contains(HttpComplianceSection.NO_FIELD_FOLDING, HttpComplianceSection.NO_FIELD_FOLDING));
    }

    @Test
    public void testFoldedField7230() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.0\r\n" + ((("Host: localhost\r\n" + "Name: value\r\n") + " extra\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler, 4096, RFC7230_LEGACY);
        HttpParserTest.parseAll(parser, buffer);
        MatcherAssert.assertThat(_bad, Matchers.notNullValue());
        MatcherAssert.assertThat(_bad, Matchers.containsString("Header Folding"));
        MatcherAssert.assertThat(_complianceViolation, Matchers.empty());
    }

    @Test
    public void testWhiteSpaceInName() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.0\r\n" + (("Host: localhost\r\n" + "N ame: value\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler, 4096, RFC7230_LEGACY);
        HttpParserTest.parseAll(parser, buffer);
        MatcherAssert.assertThat(_bad, Matchers.notNullValue());
        MatcherAssert.assertThat(_bad, Matchers.containsString("Illegal character"));
    }

    @Test
    public void testWhiteSpaceAfterName() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.0\r\n" + (("Host: localhost\r\n" + "Name : value\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler, 4096, RFC7230_LEGACY);
        HttpParserTest.parseAll(parser, buffer);
        MatcherAssert.assertThat(_bad, Matchers.notNullValue());
        MatcherAssert.assertThat(_bad, Matchers.containsString("Illegal character"));
    }

    // TODO: Parameterize Test
    @Test
    public void testWhiteSpaceBeforeRequest() {
        HttpCompliance[] compliances = new HttpCompliance[]{ RFC7230, RFC2616 };
        String[][] whitespaces = new String[][]{ new String[]{ " ", "Illegal character SPACE" }, new String[]{ "\t", "Illegal character HTAB" }, new String[]{ "\n", null }, new String[]{ "\r", "Bad EOL" }, new String[]{ "\r\n", null }, new String[]{ "\r\n\r\n", null }, new String[]{ "\r\n \r\n", "Illegal character SPACE" }, new String[]{ "\r\n\t\r\n", "Illegal character HTAB" }, new String[]{ "\r\t\n", "Bad EOL" }, new String[]{ "\r\r\n", "Bad EOL" }, new String[]{ "\t\r\t\r\n", "Illegal character HTAB" }, new String[]{ " \t \r \t \n\n", "Illegal character SPACE" }, new String[]{ " \r \t \r\n\r\n\r\n", "Illegal character SPACE" } };
        for (int i = 0; i < (compliances.length); i++) {
            HttpCompliance compliance = compliances[i];
            for (int j = 0; j < (whitespaces.length); j++) {
                String request = (((((((whitespaces[j][0]) + "GET / HTTP/1.1\r\n") + "Host: localhost\r\n") + "Name: value") + j) + "\r\n") + "Connection: close\r\n") + "\r\n";
                ByteBuffer buffer = BufferUtil.toBuffer(request);
                HttpParser.RequestHandler handler = new HttpParserTest.Handler();
                HttpParser parser = new HttpParser(handler, 4096, compliance);
                _bad = null;
                HttpParserTest.parseAll(parser, buffer);
                String test = ((("whitespace.[" + compliance) + "].[") + j) + "]";
                String expected = whitespaces[j][1];
                if (expected == null)
                    MatcherAssert.assertThat(test, _bad, Matchers.is(Matchers.nullValue()));
                else
                    MatcherAssert.assertThat(test, _bad, Matchers.containsString(expected));

            }
        }
    }

    @Test
    public void testNoValue() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.0\r\n" + ((("Host: localhost\r\n" + "Name0: \r\n") + "Name1:\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertTrue(_headerCompleted);
        Assertions.assertTrue(_messageCompleted);
        Assertions.assertEquals("GET", _methodOrVersion);
        Assertions.assertEquals("/", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals("Host", _hdr[0]);
        Assertions.assertEquals("localhost", _val[0]);
        Assertions.assertEquals("Name0", _hdr[1]);
        Assertions.assertEquals("", _val[1]);
        Assertions.assertEquals("Name1", _hdr[2]);
        Assertions.assertEquals("", _val[2]);
        Assertions.assertEquals(2, _headers);
    }

    @Test
    public void testSpaceinNameCustom0() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.0\r\n" + ((("Host: localhost\r\n" + "Name with space: value\r\n") + "Other: value\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler, CUSTOM0);
        HttpParserTest.parseAll(parser, buffer);
        MatcherAssert.assertThat(_bad, Matchers.containsString("Illegal character"));
        MatcherAssert.assertThat(_complianceViolation, Matchers.contains(NO_WS_AFTER_FIELD_NAME));
    }

    @Test
    public void testNoColonCustom0() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.0\r\n" + ((("Host: localhost\r\n" + "Name \r\n") + "Other: value\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler, CUSTOM0);
        HttpParserTest.parseAll(parser, buffer);
        MatcherAssert.assertThat(_bad, Matchers.containsString("Illegal character"));
        MatcherAssert.assertThat(_complianceViolation, Matchers.contains(NO_WS_AFTER_FIELD_NAME));
    }

    @Test
    public void testTrailingSpacesInHeaderNameInCustom0Mode() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("HTTP/1.1 204 No Content\r\n" + (("Access-Control-Allow-Headers : Origin\r\n" + "Other\t : value\r\n") + "\r\n")));
        HttpParser.ResponseHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler, (-1), CUSTOM0);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertTrue(_headerCompleted);
        Assertions.assertTrue(_messageCompleted);
        Assertions.assertEquals("HTTP/1.1", _methodOrVersion);
        Assertions.assertEquals("204", _uriOrStatus);
        Assertions.assertEquals("No Content", _versionOrReason);
        Assertions.assertEquals(null, _content);
        Assertions.assertEquals(1, _headers);
        System.out.println(Arrays.asList(_hdr));
        System.out.println(Arrays.asList(_val));
        Assertions.assertEquals("Access-Control-Allow-Headers", _hdr[0]);
        Assertions.assertEquals("Origin", _val[0]);
        Assertions.assertEquals("Other", _hdr[1]);
        Assertions.assertEquals("value", _val[1]);
        MatcherAssert.assertThat(_complianceViolation, Matchers.contains(NO_WS_AFTER_FIELD_NAME, NO_WS_AFTER_FIELD_NAME));
    }

    @Test
    public void testTrailingSpacesInHeaderNameNoCustom0() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("HTTP/1.1 204 No Content\r\n" + (("Access-Control-Allow-Headers : Origin\r\n" + "Other: value\r\n") + "\r\n")));
        HttpParser.ResponseHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertEquals("HTTP/1.1", _methodOrVersion);
        Assertions.assertEquals("204", _uriOrStatus);
        Assertions.assertEquals("No Content", _versionOrReason);
        MatcherAssert.assertThat(_bad, Matchers.containsString("Illegal character "));
    }

    @Test
    public void testNoColon7230() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.0\r\n" + (("Host: localhost\r\n" + "Name\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler, RFC7230_LEGACY);
        HttpParserTest.parseAll(parser, buffer);
        MatcherAssert.assertThat(_bad, Matchers.containsString("Illegal character"));
        MatcherAssert.assertThat(_complianceViolation, Matchers.empty());
    }

    @Test
    public void testHeaderParseDirect() throws Exception {
        ByteBuffer b0 = BufferUtil.toBuffer(("GET / HTTP/1.0\r\n" + (((((((((("Host: localhost\r\n" + "Header1: value1\r\n") + "Header2:   value 2a  \r\n") + "Header3: 3\r\n") + "Header4:value4\r\n") + "Server5: notServer\r\n") + "HostHeader: notHost\r\n") + "Connection: close\r\n") + "Accept-Encoding: gzip, deflated\r\n") + "Accept: unknown\r\n") + "\r\n")));
        ByteBuffer buffer = BufferUtil.allocateDirect(b0.capacity());
        int pos = BufferUtil.flipToFill(buffer);
        BufferUtil.put(b0, buffer);
        BufferUtil.flipToFlush(buffer, pos);
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertEquals("GET", _methodOrVersion);
        Assertions.assertEquals("/", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals("Host", _hdr[0]);
        Assertions.assertEquals("localhost", _val[0]);
        Assertions.assertEquals("Header1", _hdr[1]);
        Assertions.assertEquals("value1", _val[1]);
        Assertions.assertEquals("Header2", _hdr[2]);
        Assertions.assertEquals("value 2a", _val[2]);
        Assertions.assertEquals("Header3", _hdr[3]);
        Assertions.assertEquals("3", _val[3]);
        Assertions.assertEquals("Header4", _hdr[4]);
        Assertions.assertEquals("value4", _val[4]);
        Assertions.assertEquals("Server5", _hdr[5]);
        Assertions.assertEquals("notServer", _val[5]);
        Assertions.assertEquals("HostHeader", _hdr[6]);
        Assertions.assertEquals("notHost", _val[6]);
        Assertions.assertEquals("Connection", _hdr[7]);
        Assertions.assertEquals("close", _val[7]);
        Assertions.assertEquals("Accept-Encoding", _hdr[8]);
        Assertions.assertEquals("gzip, deflated", _val[8]);
        Assertions.assertEquals("Accept", _hdr[9]);
        Assertions.assertEquals("unknown", _val[9]);
        Assertions.assertEquals(9, _headers);
    }

    @Test
    public void testHeaderParseCRLF() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.0\r\n" + (((((((((("Host: localhost\r\n" + "Header1: value1\r\n") + "Header2:   value 2a  \r\n") + "Header3: 3\r\n") + "Header4:value4\r\n") + "Server5: notServer\r\n") + "HostHeader: notHost\r\n") + "Connection: close\r\n") + "Accept-Encoding: gzip, deflated\r\n") + "Accept: unknown\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertEquals("GET", _methodOrVersion);
        Assertions.assertEquals("/", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals("Host", _hdr[0]);
        Assertions.assertEquals("localhost", _val[0]);
        Assertions.assertEquals("Header1", _hdr[1]);
        Assertions.assertEquals("value1", _val[1]);
        Assertions.assertEquals("Header2", _hdr[2]);
        Assertions.assertEquals("value 2a", _val[2]);
        Assertions.assertEquals("Header3", _hdr[3]);
        Assertions.assertEquals("3", _val[3]);
        Assertions.assertEquals("Header4", _hdr[4]);
        Assertions.assertEquals("value4", _val[4]);
        Assertions.assertEquals("Server5", _hdr[5]);
        Assertions.assertEquals("notServer", _val[5]);
        Assertions.assertEquals("HostHeader", _hdr[6]);
        Assertions.assertEquals("notHost", _val[6]);
        Assertions.assertEquals("Connection", _hdr[7]);
        Assertions.assertEquals("close", _val[7]);
        Assertions.assertEquals("Accept-Encoding", _hdr[8]);
        Assertions.assertEquals("gzip, deflated", _val[8]);
        Assertions.assertEquals("Accept", _hdr[9]);
        Assertions.assertEquals("unknown", _val[9]);
        Assertions.assertEquals(9, _headers);
    }

    @Test
    public void testHeaderParseLF() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.0\n" + (((((((((("Host: localhost\n" + "Header1: value1\n") + "Header2:   value 2a value 2b  \n") + "Header3: 3\n") + "Header4:value4\n") + "Server5: notServer\n") + "HostHeader: notHost\n") + "Connection: close\n") + "Accept-Encoding: gzip, deflated\n") + "Accept: unknown\n") + "\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertEquals("GET", _methodOrVersion);
        Assertions.assertEquals("/", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals("Host", _hdr[0]);
        Assertions.assertEquals("localhost", _val[0]);
        Assertions.assertEquals("Header1", _hdr[1]);
        Assertions.assertEquals("value1", _val[1]);
        Assertions.assertEquals("Header2", _hdr[2]);
        Assertions.assertEquals("value 2a value 2b", _val[2]);
        Assertions.assertEquals("Header3", _hdr[3]);
        Assertions.assertEquals("3", _val[3]);
        Assertions.assertEquals("Header4", _hdr[4]);
        Assertions.assertEquals("value4", _val[4]);
        Assertions.assertEquals("Server5", _hdr[5]);
        Assertions.assertEquals("notServer", _val[5]);
        Assertions.assertEquals("HostHeader", _hdr[6]);
        Assertions.assertEquals("notHost", _val[6]);
        Assertions.assertEquals("Connection", _hdr[7]);
        Assertions.assertEquals("close", _val[7]);
        Assertions.assertEquals("Accept-Encoding", _hdr[8]);
        Assertions.assertEquals("gzip, deflated", _val[8]);
        Assertions.assertEquals("Accept", _hdr[9]);
        Assertions.assertEquals("unknown", _val[9]);
        Assertions.assertEquals(9, _headers);
    }

    @Test
    public void testQuoted() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.0\n" + ((("Name0: \"value0\"\t\n" + "Name1: \"value\t1\"\n") + "Name2: \"value\t2A\",\"value,2B\"\t\n") + "\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertEquals("GET", _methodOrVersion);
        Assertions.assertEquals("/", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals("Name0", _hdr[0]);
        Assertions.assertEquals("\"value0\"", _val[0]);
        Assertions.assertEquals("Name1", _hdr[1]);
        Assertions.assertEquals("\"value\t1\"", _val[1]);
        Assertions.assertEquals("Name2", _hdr[2]);
        Assertions.assertEquals("\"value\t2A\",\"value,2B\"", _val[2]);
        Assertions.assertEquals(2, _headers);
    }

    @Test
    public void testEncodedHeader() throws Exception {
        ByteBuffer buffer = BufferUtil.allocate(4096);
        BufferUtil.flipToFill(buffer);
        BufferUtil.put(BufferUtil.toBuffer("GET "), buffer);
        buffer.put("/foo/\u0690/".getBytes(StandardCharsets.UTF_8));
        BufferUtil.put(BufferUtil.toBuffer(" HTTP/1.0\r\n"), buffer);
        BufferUtil.put(BufferUtil.toBuffer("Header1: "), buffer);
        buffer.put("\u00e6 \u00e6".getBytes(StandardCharsets.ISO_8859_1));
        BufferUtil.put(BufferUtil.toBuffer("  \r\nHeader2: "), buffer);
        buffer.put(((byte) (-1)));
        BufferUtil.put(BufferUtil.toBuffer("\r\n\r\n"), buffer);
        BufferUtil.flipToFlush(buffer, 0);
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertEquals("GET", _methodOrVersion);
        Assertions.assertEquals("/foo/\u0690/", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals("Header1", _hdr[0]);
        Assertions.assertEquals("\u00e6 \u00e6", _val[0]);
        Assertions.assertEquals("Header2", _hdr[1]);
        Assertions.assertEquals(("" + ((char) (255))), _val[1]);
        Assertions.assertEquals(1, _headers);
        Assertions.assertEquals(null, _bad);
    }

    @Test
    public void testResponseBufferUpgradeFrom() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("HTTP/1.1 101 Upgrade\r\n" + (((("Connection: upgrade\r\n" + "Content-Length: 0\r\n") + "Sec-WebSocket-Accept: 4GnyoUP4Sc1JD+2pCbNYAhFYVVA\r\n") + "\r\n") + "FOOGRADE")));
        HttpParser.ResponseHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        while (!(parser.isState(END))) {
            parser.parseNext(buffer);
        } 
        MatcherAssert.assertThat(BufferUtil.toUTF8String(buffer), Matchers.is("FOOGRADE"));
    }

    @Test
    public void testBadMethodEncoding() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer("G\u00e6T / HTTP/1.0\r\nHeader0: value0\r\n\n\n");
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        MatcherAssert.assertThat(_bad, Matchers.notNullValue());
    }

    @Test
    public void testBadVersionEncoding() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer("GET / H\u00e6P/1.0\r\nHeader0: value0\r\n\n\n");
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        MatcherAssert.assertThat(_bad, Matchers.notNullValue());
    }

    @Test
    public void testBadHeaderEncoding() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.0\r\n" + ("H\u00e6der0: value0\r\n" + "\n\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        MatcherAssert.assertThat(_bad, Matchers.notNullValue());
    }

    // TODO: Parameterize Test
    @Test
    public void testBadHeaderNames() throws Exception {
        String[] bad = new String[]{ "Foo\\Bar: value\r\n", "Foo@Bar: value\r\n", "Foo,Bar: value\r\n", "Foo}Bar: value\r\n", "Foo{Bar: value\r\n", "Foo=Bar: value\r\n", "Foo>Bar: value\r\n", "Foo<Bar: value\r\n", "Foo)Bar: value\r\n", "Foo(Bar: value\r\n", "Foo?Bar: value\r\n", "Foo\"Bar: value\r\n", "Foo/Bar: value\r\n", "Foo]Bar: value\r\n", "Foo[Bar: value\r\n" };
        for (int i = 0; i < (bad.length); i++) {
            ByteBuffer buffer = BufferUtil.toBuffer((("GET / HTTP/1.0\r\n" + (bad[i])) + "\r\n"));
            HttpParser.RequestHandler handler = new HttpParserTest.Handler();
            HttpParser parser = new HttpParser(handler);
            HttpParserTest.parseAll(parser, buffer);
            MatcherAssert.assertThat(bad[i], _bad, Matchers.notNullValue());
        }
    }

    @Test
    public void testHeaderTab() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.1\r\n" + (("Host: localhost\r\n" + "Header: value\talternate\r\n") + "\n\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertEquals("GET", _methodOrVersion);
        Assertions.assertEquals("/", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.1", _versionOrReason);
        Assertions.assertEquals("Host", _hdr[0]);
        Assertions.assertEquals("localhost", _val[0]);
        Assertions.assertEquals("Header", _hdr[1]);
        Assertions.assertEquals("value\talternate", _val[1]);
    }

    @Test
    public void testCaseSensitiveMethod() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("gEt / http/1.0\r\n" + (("Host: localhost\r\n" + "Connection: close\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler, (-1), RFC7230_LEGACY);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertNull(_bad);
        Assertions.assertEquals("GET", _methodOrVersion);
        MatcherAssert.assertThat(_complianceViolation, Matchers.contains(METHOD_CASE_SENSITIVE));
    }

    @Test
    public void testCaseSensitiveMethodLegacy() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("gEt / http/1.0\r\n" + (("Host: localhost\r\n" + "Connection: close\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler, (-1), LEGACY);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertNull(_bad);
        Assertions.assertEquals("gEt", _methodOrVersion);
        MatcherAssert.assertThat(_complianceViolation, Matchers.empty());
    }

    @Test
    public void testCaseInsensitiveHeader() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / http/1.0\r\n" + (("HOST: localhost\r\n" + "cOnNeCtIoN: ClOsE\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler, (-1), RFC7230_LEGACY);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertNull(_bad);
        Assertions.assertEquals("GET", _methodOrVersion);
        Assertions.assertEquals("/", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals("Host", _hdr[0]);
        Assertions.assertEquals("localhost", _val[0]);
        Assertions.assertEquals("Connection", _hdr[1]);
        Assertions.assertEquals("close", _val[1]);
        Assertions.assertEquals(1, _headers);
        MatcherAssert.assertThat(_complianceViolation, Matchers.empty());
    }

    @Test
    public void testCaseInSensitiveHeaderLegacy() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / http/1.0\r\n" + (("HOST: localhost\r\n" + "cOnNeCtIoN: ClOsE\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler, (-1), LEGACY);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertNull(_bad);
        Assertions.assertEquals("GET", _methodOrVersion);
        Assertions.assertEquals("/", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals("HOST", _hdr[0]);
        Assertions.assertEquals("localhost", _val[0]);
        Assertions.assertEquals("cOnNeCtIoN", _hdr[1]);
        Assertions.assertEquals("ClOsE", _val[1]);
        Assertions.assertEquals(1, _headers);
        MatcherAssert.assertThat(_complianceViolation, Matchers.contains(FIELD_NAME_CASE_INSENSITIVE, FIELD_NAME_CASE_INSENSITIVE, CASE_INSENSITIVE_FIELD_VALUE_CACHE));
    }

    @Test
    public void testSplitHeaderParse() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("XXXXSPLIT / HTTP/1.0\r\n" + (((((("Host: localhost\r\n" + "Header1: value1\r\n") + "Header2:   value 2a  \r\n") + "Header3: 3\r\n") + "Header4:value4\r\n") + "Server5: notServer\r\n") + "\r\nZZZZ")));
        buffer.position(2);
        buffer.limit(((buffer.capacity()) - 2));
        buffer = buffer.slice();
        for (int i = 0; i < ((buffer.capacity()) - 4); i++) {
            HttpParser.RequestHandler handler = new HttpParserTest.Handler();
            HttpParser parser = new HttpParser(handler);
            buffer.position(2);
            buffer.limit((2 + i));
            if (!(parser.parseNext(buffer))) {
                // consumed all
                Assertions.assertEquals(0, buffer.remaining());
                // parse the rest
                buffer.limit(((buffer.capacity()) - 2));
                parser.parseNext(buffer);
            }
            Assertions.assertEquals("SPLIT", _methodOrVersion);
            Assertions.assertEquals("/", _uriOrStatus);
            Assertions.assertEquals("HTTP/1.0", _versionOrReason);
            Assertions.assertEquals("Host", _hdr[0]);
            Assertions.assertEquals("localhost", _val[0]);
            Assertions.assertEquals("Header1", _hdr[1]);
            Assertions.assertEquals("value1", _val[1]);
            Assertions.assertEquals("Header2", _hdr[2]);
            Assertions.assertEquals("value 2a", _val[2]);
            Assertions.assertEquals("Header3", _hdr[3]);
            Assertions.assertEquals("3", _val[3]);
            Assertions.assertEquals("Header4", _hdr[4]);
            Assertions.assertEquals("value4", _val[4]);
            Assertions.assertEquals("Server5", _hdr[5]);
            Assertions.assertEquals("notServer", _val[5]);
            Assertions.assertEquals(5, _headers);
        }
    }

    @Test
    public void testChunkParse() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET /chunk HTTP/1.0\r\n" + (((((((("Header1: value1\r\n" + "Transfer-Encoding: chunked\r\n") + "\r\n") + "a;\r\n") + "0123456789\r\n") + "1a\r\n") + "ABCDEFGHIJKLMNOPQRSTUVWXYZ\r\n") + "0\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertEquals("GET", _methodOrVersion);
        Assertions.assertEquals("/chunk", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals(1, _headers);
        Assertions.assertEquals("Header1", _hdr[0]);
        Assertions.assertEquals("value1", _val[0]);
        Assertions.assertEquals("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ", _content);
        Assertions.assertTrue(_headerCompleted);
        Assertions.assertTrue(_messageCompleted);
    }

    @Test
    public void testBadChunkParse() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET /chunk HTTP/1.0\r\n" + (((((((("Header1: value1\r\n" + "Transfer-Encoding: chunked, identity\r\n") + "\r\n") + "a;\r\n") + "0123456789\r\n") + "1a\r\n") + "ABCDEFGHIJKLMNOPQRSTUVWXYZ\r\n") + "0\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertEquals("GET", _methodOrVersion);
        Assertions.assertEquals("/chunk", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        MatcherAssert.assertThat(_bad, Matchers.containsString("Bad chunking"));
    }

    @Test
    public void testChunkParseTrailer() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET /chunk HTTP/1.0\r\n" + ((((((((("Header1: value1\r\n" + "Transfer-Encoding: chunked\r\n") + "\r\n") + "a;\r\n") + "0123456789\r\n") + "1a\r\n") + "ABCDEFGHIJKLMNOPQRSTUVWXYZ\r\n") + "0\r\n") + "Trailer: value\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertEquals("GET", _methodOrVersion);
        Assertions.assertEquals("/chunk", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals(1, _headers);
        Assertions.assertEquals("Header1", _hdr[0]);
        Assertions.assertEquals("value1", _val[0]);
        Assertions.assertEquals("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ", _content);
        Assertions.assertEquals(1, _trailers.size());
        HttpField trailer1 = _trailers.get(0);
        Assertions.assertEquals("Trailer", trailer1.getName());
        Assertions.assertEquals("value", trailer1.getValue());
        Assertions.assertTrue(_headerCompleted);
        Assertions.assertTrue(_messageCompleted);
    }

    @Test
    public void testChunkParseTrailers() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET /chunk HTTP/1.0\r\n" + ((((((((("Transfer-Encoding: chunked\r\n" + "\r\n") + "a;\r\n") + "0123456789\r\n") + "1a\r\n") + "ABCDEFGHIJKLMNOPQRSTUVWXYZ\r\n") + "0\r\n") + "Trailer: value\r\n") + "Foo: bar\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertEquals("GET", _methodOrVersion);
        Assertions.assertEquals("/chunk", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals(0, _headers);
        Assertions.assertEquals("Transfer-Encoding", _hdr[0]);
        Assertions.assertEquals("chunked", _val[0]);
        Assertions.assertEquals("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ", _content);
        Assertions.assertEquals(2, _trailers.size());
        HttpField trailer1 = _trailers.get(0);
        Assertions.assertEquals("Trailer", trailer1.getName());
        Assertions.assertEquals("value", trailer1.getValue());
        HttpField trailer2 = _trailers.get(1);
        Assertions.assertEquals("Foo", trailer2.getName());
        Assertions.assertEquals("bar", trailer2.getValue());
        Assertions.assertTrue(_headerCompleted);
        Assertions.assertTrue(_messageCompleted);
    }

    @Test
    public void testChunkParseBadTrailer() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET /chunk HTTP/1.0\r\n" + (((((((("Header1: value1\r\n" + "Transfer-Encoding: chunked\r\n") + "\r\n") + "a;\r\n") + "0123456789\r\n") + "1a\r\n") + "ABCDEFGHIJKLMNOPQRSTUVWXYZ\r\n") + "0\r\n") + "Trailer: value")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        parser.atEOF();
        parser.parseNext(EMPTY_BUFFER);
        Assertions.assertEquals("GET", _methodOrVersion);
        Assertions.assertEquals("/chunk", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals(1, _headers);
        Assertions.assertEquals("Header1", _hdr[0]);
        Assertions.assertEquals("value1", _val[0]);
        Assertions.assertEquals("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ", _content);
        Assertions.assertTrue(_headerCompleted);
        Assertions.assertTrue(_early);
    }

    @Test
    public void testChunkParseNoTrailer() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET /chunk HTTP/1.0\r\n" + ((((((("Header1: value1\r\n" + "Transfer-Encoding: chunked\r\n") + "\r\n") + "a;\r\n") + "0123456789\r\n") + "1a\r\n") + "ABCDEFGHIJKLMNOPQRSTUVWXYZ\r\n") + "0\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        parser.atEOF();
        parser.parseNext(EMPTY_BUFFER);
        Assertions.assertEquals("GET", _methodOrVersion);
        Assertions.assertEquals("/chunk", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals(1, _headers);
        Assertions.assertEquals("Header1", _hdr[0]);
        Assertions.assertEquals("value1", _val[0]);
        Assertions.assertEquals("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ", _content);
        Assertions.assertTrue(_headerCompleted);
        Assertions.assertTrue(_messageCompleted);
    }

    @Test
    public void testStartEOF() throws Exception {
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.atEOF();
        parser.parseNext(EMPTY_BUFFER);
        Assertions.assertTrue(_early);
        Assertions.assertEquals(null, _bad);
    }

    @Test
    public void testEarlyEOF() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET /uri HTTP/1.0\r\n" + (("Content-Length: 20\r\n" + "\r\n") + "0123456789")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.atEOF();
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertEquals("GET", _methodOrVersion);
        Assertions.assertEquals("/uri", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals("0123456789", _content);
        Assertions.assertTrue(_early);
    }

    @Test
    public void testChunkEarlyEOF() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET /chunk HTTP/1.0\r\n" + (((("Header1: value1\r\n" + "Transfer-Encoding: chunked\r\n") + "\r\n") + "a;\r\n") + "0123456789\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.atEOF();
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertEquals("GET", _methodOrVersion);
        Assertions.assertEquals("/chunk", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals(1, _headers);
        Assertions.assertEquals("Header1", _hdr[0]);
        Assertions.assertEquals("value1", _val[0]);
        Assertions.assertEquals("0123456789", _content);
        Assertions.assertTrue(_early);
    }

    @Test
    public void testMultiParse() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET /mp HTTP/1.0\r\n" + (((((((((((((((((((("Connection: Keep-Alive\r\n" + "Header1: value1\r\n") + "Transfer-Encoding: chunked\r\n") + "\r\n") + "a;\r\n") + "0123456789\r\n") + "1a\r\n") + "ABCDEFGHIJKLMNOPQRSTUVWXYZ\r\n") + "0\r\n") + "\r\n") + "POST /foo HTTP/1.0\r\n") + "Connection: Keep-Alive\r\n") + "Header2: value2\r\n") + "Content-Length: 0\r\n") + "\r\n") + "PUT /doodle HTTP/1.0\r\n") + "Connection: close\r\n") + "Header3: value3\r\n") + "Content-Length: 10\r\n") + "\r\n") + "0123456789\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals("GET", _methodOrVersion);
        Assertions.assertEquals("/mp", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals(2, _headers);
        Assertions.assertEquals("Header1", _hdr[1]);
        Assertions.assertEquals("value1", _val[1]);
        Assertions.assertEquals("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ", _content);
        parser.reset();
        init();
        parser.parseNext(buffer);
        Assertions.assertEquals("POST", _methodOrVersion);
        Assertions.assertEquals("/foo", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals(2, _headers);
        Assertions.assertEquals("Header2", _hdr[1]);
        Assertions.assertEquals("value2", _val[1]);
        Assertions.assertEquals(null, _content);
        parser.reset();
        init();
        parser.parseNext(buffer);
        parser.atEOF();
        Assertions.assertEquals("PUT", _methodOrVersion);
        Assertions.assertEquals("/doodle", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals(2, _headers);
        Assertions.assertEquals("Header3", _hdr[1]);
        Assertions.assertEquals("value3", _val[1]);
        Assertions.assertEquals("0123456789", _content);
    }

    @Test
    public void testMultiParseEarlyEOF() throws Exception {
        ByteBuffer buffer0 = BufferUtil.toBuffer(("GET /mp HTTP/1.0\r\n" + "Connection: Keep-Alive\r\n"));
        ByteBuffer buffer1 = BufferUtil.toBuffer(("Header1: value1\r\n" + (((((((((((((((((("Transfer-Encoding: chunked\r\n" + "\r\n") + "a;\r\n") + "0123456789\r\n") + "1a\r\n") + "ABCDEFGHIJKLMNOPQRSTUVWXYZ\r\n") + "0\r\n") + "\r\n") + "POST /foo HTTP/1.0\r\n") + "Connection: Keep-Alive\r\n") + "Header2: value2\r\n") + "Content-Length: 0\r\n") + "\r\n") + "PUT /doodle HTTP/1.0\r\n") + "Connection: close\r\n") + "Header3: value3\r\n") + "Content-Length: 10\r\n") + "\r\n") + "0123456789\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer0);
        parser.atEOF();
        parser.parseNext(buffer1);
        Assertions.assertEquals("GET", _methodOrVersion);
        Assertions.assertEquals("/mp", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals(2, _headers);
        Assertions.assertEquals("Header1", _hdr[1]);
        Assertions.assertEquals("value1", _val[1]);
        Assertions.assertEquals("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ", _content);
        parser.reset();
        init();
        parser.parseNext(buffer1);
        Assertions.assertEquals("POST", _methodOrVersion);
        Assertions.assertEquals("/foo", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals(2, _headers);
        Assertions.assertEquals("Header2", _hdr[1]);
        Assertions.assertEquals("value2", _val[1]);
        Assertions.assertEquals(null, _content);
        parser.reset();
        init();
        parser.parseNext(buffer1);
        Assertions.assertEquals("PUT", _methodOrVersion);
        Assertions.assertEquals("/doodle", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.0", _versionOrReason);
        Assertions.assertEquals(2, _headers);
        Assertions.assertEquals("Header3", _hdr[1]);
        Assertions.assertEquals("value3", _val[1]);
        Assertions.assertEquals("0123456789", _content);
    }

    @Test
    public void testResponseParse0() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("HTTP/1.1 200 Correct\r\n" + ((("Content-Length: 10\r\n" + "Content-Type: text/plain\r\n") + "\r\n") + "0123456789\r\n")));
        HttpParser.ResponseHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals("HTTP/1.1", _methodOrVersion);
        Assertions.assertEquals("200", _uriOrStatus);
        Assertions.assertEquals("Correct", _versionOrReason);
        Assertions.assertEquals(10, _content.length());
        Assertions.assertTrue(_headerCompleted);
        Assertions.assertTrue(_messageCompleted);
    }

    @Test
    public void testResponseParse1() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("HTTP/1.1 304 Not-Modified\r\n" + ("Connection: close\r\n" + "\r\n")));
        HttpParser.ResponseHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals("HTTP/1.1", _methodOrVersion);
        Assertions.assertEquals("304", _uriOrStatus);
        Assertions.assertEquals("Not-Modified", _versionOrReason);
        Assertions.assertTrue(_headerCompleted);
        Assertions.assertTrue(_messageCompleted);
    }

    @Test
    public void testResponseParse2() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("HTTP/1.1 204 No-Content\r\n" + (((((("Header: value\r\n" + "\r\n") + "HTTP/1.1 200 Correct\r\n") + "Content-Length: 10\r\n") + "Content-Type: text/plain\r\n") + "\r\n") + "0123456789\r\n")));
        HttpParser.ResponseHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals("HTTP/1.1", _methodOrVersion);
        Assertions.assertEquals("204", _uriOrStatus);
        Assertions.assertEquals("No-Content", _versionOrReason);
        Assertions.assertTrue(_headerCompleted);
        Assertions.assertTrue(_messageCompleted);
        parser.reset();
        init();
        parser.parseNext(buffer);
        parser.atEOF();
        Assertions.assertEquals("HTTP/1.1", _methodOrVersion);
        Assertions.assertEquals("200", _uriOrStatus);
        Assertions.assertEquals("Correct", _versionOrReason);
        Assertions.assertEquals(_content.length(), 10);
        Assertions.assertTrue(_headerCompleted);
        Assertions.assertTrue(_messageCompleted);
    }

    @Test
    public void testResponseParse3() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("HTTP/1.1 200\r\n" + ((("Content-Length: 10\r\n" + "Content-Type: text/plain\r\n") + "\r\n") + "0123456789\r\n")));
        HttpParser.ResponseHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals("HTTP/1.1", _methodOrVersion);
        Assertions.assertEquals("200", _uriOrStatus);
        Assertions.assertEquals(null, _versionOrReason);
        Assertions.assertEquals(_content.length(), 10);
        Assertions.assertTrue(_headerCompleted);
        Assertions.assertTrue(_messageCompleted);
    }

    @Test
    public void testResponseParse4() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("HTTP/1.1 200 \r\n" + ((("Content-Length: 10\r\n" + "Content-Type: text/plain\r\n") + "\r\n") + "0123456789\r\n")));
        HttpParser.ResponseHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals("HTTP/1.1", _methodOrVersion);
        Assertions.assertEquals("200", _uriOrStatus);
        Assertions.assertEquals(null, _versionOrReason);
        Assertions.assertEquals(_content.length(), 10);
        Assertions.assertTrue(_headerCompleted);
        Assertions.assertTrue(_messageCompleted);
    }

    @Test
    public void testResponseEOFContent() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("HTTP/1.1 200 \r\n" + (("Content-Type: text/plain\r\n" + "\r\n") + "0123456789\r\n")));
        HttpParser.ResponseHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.atEOF();
        parser.parseNext(buffer);
        Assertions.assertEquals("HTTP/1.1", _methodOrVersion);
        Assertions.assertEquals("200", _uriOrStatus);
        Assertions.assertEquals(null, _versionOrReason);
        Assertions.assertEquals(12, _content.length());
        Assertions.assertEquals("0123456789\r\n", _content);
        Assertions.assertTrue(_headerCompleted);
        Assertions.assertTrue(_messageCompleted);
    }

    @Test
    public void testResponse304WithContentLength() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("HTTP/1.1 304 found\r\n" + ("Content-Length: 10\r\n" + "\r\n")));
        HttpParser.ResponseHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals("HTTP/1.1", _methodOrVersion);
        Assertions.assertEquals("304", _uriOrStatus);
        Assertions.assertEquals("found", _versionOrReason);
        Assertions.assertEquals(null, _content);
        Assertions.assertTrue(_headerCompleted);
        Assertions.assertTrue(_messageCompleted);
    }

    @Test
    public void testResponse101WithTransferEncoding() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("HTTP/1.1 101 switching protocols\r\n" + ("Transfer-Encoding: chunked\r\n" + "\r\n")));
        HttpParser.ResponseHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals("HTTP/1.1", _methodOrVersion);
        Assertions.assertEquals("101", _uriOrStatus);
        Assertions.assertEquals("switching protocols", _versionOrReason);
        Assertions.assertEquals(null, _content);
        Assertions.assertTrue(_headerCompleted);
        Assertions.assertTrue(_messageCompleted);
    }

    @Test
    public void testResponseReasonIso8859_1() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("HTTP/1.1 302 d\u00e9plac\u00e9 temporairement\r\n" + ("Content-Length: 0\r\n" + "\r\n")), StandardCharsets.ISO_8859_1);
        HttpParser.ResponseHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals("HTTP/1.1", _methodOrVersion);
        Assertions.assertEquals("302", _uriOrStatus);
        Assertions.assertEquals("d?plac? temporairement", _versionOrReason);
    }

    @Test
    public void testSeekEOF() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("HTTP/1.1 200 OK\r\n" + (((("Content-Length: 0\r\n" + "Connection: close\r\n") + "\r\n") + "\r\n")// extra CRLF ignored
         + "HTTP/1.1 400 OK\r\n")));// extra data causes close ??

        HttpParser.ResponseHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals("HTTP/1.1", _methodOrVersion);
        Assertions.assertEquals("200", _uriOrStatus);
        Assertions.assertEquals("OK", _versionOrReason);
        Assertions.assertEquals(null, _content);
        Assertions.assertTrue(_headerCompleted);
        Assertions.assertTrue(_messageCompleted);
        parser.close();
        parser.reset();
        parser.parseNext(buffer);
        Assertions.assertFalse(buffer.hasRemaining());
        Assertions.assertEquals(CLOSE, parser.getState());
        parser.atEOF();
        parser.parseNext(EMPTY_BUFFER);
        Assertions.assertEquals(CLOSED, parser.getState());
    }

    @Test
    public void testNoURI() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET\r\n" + (("Content-Length: 0\r\n" + "Connection: close\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals(null, _methodOrVersion);
        Assertions.assertEquals("No URI", _bad);
        Assertions.assertFalse(buffer.hasRemaining());
        Assertions.assertEquals(CLOSE, parser.getState());
        parser.atEOF();
        parser.parseNext(EMPTY_BUFFER);
        Assertions.assertEquals(CLOSED, parser.getState());
    }

    @Test
    public void testNoURI2() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET \r\n" + (("Content-Length: 0\r\n" + "Connection: close\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals(null, _methodOrVersion);
        Assertions.assertEquals("No URI", _bad);
        Assertions.assertFalse(buffer.hasRemaining());
        Assertions.assertEquals(CLOSE, parser.getState());
        parser.atEOF();
        parser.parseNext(EMPTY_BUFFER);
        Assertions.assertEquals(CLOSED, parser.getState());
    }

    @Test
    public void testUnknownReponseVersion() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("HPPT/7.7 200 OK\r\n" + (("Content-Length: 0\r\n" + "Connection: close\r\n") + "\r\n")));
        HttpParser.ResponseHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals(null, _methodOrVersion);
        Assertions.assertEquals("Unknown Version", _bad);
        Assertions.assertFalse(buffer.hasRemaining());
        Assertions.assertEquals(CLOSE, parser.getState());
        parser.atEOF();
        parser.parseNext(EMPTY_BUFFER);
        Assertions.assertEquals(CLOSED, parser.getState());
    }

    @Test
    public void testNoStatus() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("HTTP/1.1\r\n" + (("Content-Length: 0\r\n" + "Connection: close\r\n") + "\r\n")));
        HttpParser.ResponseHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals(null, _methodOrVersion);
        Assertions.assertEquals("No Status", _bad);
        Assertions.assertFalse(buffer.hasRemaining());
        Assertions.assertEquals(CLOSE, parser.getState());
        parser.atEOF();
        parser.parseNext(EMPTY_BUFFER);
        Assertions.assertEquals(CLOSED, parser.getState());
    }

    @Test
    public void testNoStatus2() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("HTTP/1.1 \r\n" + (("Content-Length: 0\r\n" + "Connection: close\r\n") + "\r\n")));
        HttpParser.ResponseHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals(null, _methodOrVersion);
        Assertions.assertEquals("No Status", _bad);
        Assertions.assertFalse(buffer.hasRemaining());
        Assertions.assertEquals(CLOSE, parser.getState());
        parser.atEOF();
        parser.parseNext(EMPTY_BUFFER);
        Assertions.assertEquals(CLOSED, parser.getState());
    }

    @Test
    public void testBadRequestVersion() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HPPT/7.7\r\n" + (("Content-Length: 0\r\n" + "Connection: close\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals(null, _methodOrVersion);
        Assertions.assertEquals("Unknown Version", _bad);
        Assertions.assertFalse(buffer.hasRemaining());
        Assertions.assertEquals(CLOSE, parser.getState());
        parser.atEOF();
        parser.parseNext(EMPTY_BUFFER);
        Assertions.assertEquals(CLOSED, parser.getState());
        buffer = BufferUtil.toBuffer(("GET / HTTP/1.01\r\n" + (("Content-Length: 0\r\n" + "Connection: close\r\n") + "\r\n")));
        handler = new HttpParserTest.Handler();
        parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals(null, _methodOrVersion);
        Assertions.assertEquals("Unknown Version", _bad);
        Assertions.assertFalse(buffer.hasRemaining());
        Assertions.assertEquals(CLOSE, parser.getState());
        parser.atEOF();
        parser.parseNext(EMPTY_BUFFER);
        Assertions.assertEquals(CLOSED, parser.getState());
    }

    @Test
    public void testBadCR() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.0\r\n" + (("Content-Length: 0\r" + "Connection: close\r") + "\r")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals("Bad EOL", _bad);
        Assertions.assertFalse(buffer.hasRemaining());
        Assertions.assertEquals(CLOSE, parser.getState());
        parser.atEOF();
        parser.parseNext(EMPTY_BUFFER);
        Assertions.assertEquals(CLOSED, parser.getState());
        buffer = BufferUtil.toBuffer(("GET / HTTP/1.0\r" + (("Content-Length: 0\r" + "Connection: close\r") + "\r")));
        handler = new HttpParserTest.Handler();
        parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals("Bad EOL", _bad);
        Assertions.assertFalse(buffer.hasRemaining());
        Assertions.assertEquals(CLOSE, parser.getState());
        parser.atEOF();
        parser.parseNext(EMPTY_BUFFER);
        Assertions.assertEquals(CLOSED, parser.getState());
    }

    @Test
    public void testBadContentLength0() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.0\r\n" + (("Content-Length: abc\r\n" + "Connection: close\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals("GET", _methodOrVersion);
        Assertions.assertEquals("Invalid Content-Length Value", _bad);
        Assertions.assertFalse(buffer.hasRemaining());
        Assertions.assertEquals(CLOSE, parser.getState());
        parser.atEOF();
        parser.parseNext(EMPTY_BUFFER);
        Assertions.assertEquals(CLOSED, parser.getState());
    }

    @Test
    public void testBadContentLength1() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.0\r\n" + (("Content-Length: 9999999999999999999999999999999999999999999999\r\n" + "Connection: close\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals("GET", _methodOrVersion);
        Assertions.assertEquals("Invalid Content-Length Value", _bad);
        Assertions.assertFalse(buffer.hasRemaining());
        Assertions.assertEquals(CLOSE, parser.getState());
        parser.atEOF();
        parser.parseNext(EMPTY_BUFFER);
        Assertions.assertEquals(CLOSED, parser.getState());
    }

    @Test
    public void testBadContentLength2() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.0\r\n" + (("Content-Length: 1.5\r\n" + "Connection: close\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals("GET", _methodOrVersion);
        Assertions.assertEquals("Invalid Content-Length Value", _bad);
        Assertions.assertFalse(buffer.hasRemaining());
        Assertions.assertEquals(CLOSE, parser.getState());
        parser.atEOF();
        parser.parseNext(EMPTY_BUFFER);
        Assertions.assertEquals(CLOSED, parser.getState());
    }

    @Test
    public void testMultipleContentLengthWithLargerThenCorrectValue() {
        ByteBuffer buffer = BufferUtil.toBuffer(("POST / HTTP/1.1\r\n" + (((("Content-Length: 2\r\n" + "Content-Length: 1\r\n") + "Connection: close\r\n") + "\r\n") + "X")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals("POST", _methodOrVersion);
        Assertions.assertEquals("Multiple Content-Lengths", _bad);
        Assertions.assertFalse(buffer.hasRemaining());
        Assertions.assertEquals(CLOSE, parser.getState());
        parser.atEOF();
        parser.parseNext(EMPTY_BUFFER);
        Assertions.assertEquals(CLOSED, parser.getState());
    }

    @Test
    public void testMultipleContentLengthWithCorrectThenLargerValue() {
        ByteBuffer buffer = BufferUtil.toBuffer(("POST / HTTP/1.1\r\n" + (((("Content-Length: 1\r\n" + "Content-Length: 2\r\n") + "Connection: close\r\n") + "\r\n") + "X")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals("POST", _methodOrVersion);
        Assertions.assertEquals("Multiple Content-Lengths", _bad);
        Assertions.assertFalse(buffer.hasRemaining());
        Assertions.assertEquals(CLOSE, parser.getState());
        parser.atEOF();
        parser.parseNext(EMPTY_BUFFER);
        Assertions.assertEquals(CLOSED, parser.getState());
    }

    @Test
    public void testTransferEncodingChunkedThenContentLength() {
        ByteBuffer buffer = BufferUtil.toBuffer(("POST /chunk HTTP/1.1\r\n" + ((((((("Host: localhost\r\n" + "Transfer-Encoding: chunked\r\n") + "Content-Length: 1\r\n") + "\r\n") + "1\r\n") + "X\r\n") + "0\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler, RFC2616_LEGACY);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertEquals("POST", _methodOrVersion);
        Assertions.assertEquals("/chunk", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.1", _versionOrReason);
        Assertions.assertEquals("X", _content);
        Assertions.assertTrue(_headerCompleted);
        Assertions.assertTrue(_messageCompleted);
        MatcherAssert.assertThat(_complianceViolation, Matchers.contains(TRANSFER_ENCODING_WITH_CONTENT_LENGTH));
    }

    @Test
    public void testContentLengthThenTransferEncodingChunked() {
        ByteBuffer buffer = BufferUtil.toBuffer(("POST /chunk HTTP/1.1\r\n" + ((((((("Host: localhost\r\n" + "Content-Length: 1\r\n") + "Transfer-Encoding: chunked\r\n") + "\r\n") + "1\r\n") + "X\r\n") + "0\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler, RFC2616_LEGACY);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertEquals("POST", _methodOrVersion);
        Assertions.assertEquals("/chunk", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.1", _versionOrReason);
        Assertions.assertEquals("X", _content);
        Assertions.assertTrue(_headerCompleted);
        Assertions.assertTrue(_messageCompleted);
        MatcherAssert.assertThat(_complianceViolation, Matchers.contains(TRANSFER_ENCODING_WITH_CONTENT_LENGTH));
    }

    @Test
    public void testHost() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.1\r\n" + (("Host: host\r\n" + "Connection: close\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals("host", _host);
        Assertions.assertEquals(0, _port);
    }

    @Test
    public void testUriHost11() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET http://host/ HTTP/1.1\r\n" + ("Connection: close\r\n" + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals("No Host", _bad);
        Assertions.assertEquals("http://host/", _uriOrStatus);
        Assertions.assertEquals(0, _port);
    }

    @Test
    public void testUriHost10() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET http://host/ HTTP/1.0\r\n" + "\r\n"));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertNull(_bad);
        Assertions.assertEquals("http://host/", _uriOrStatus);
        Assertions.assertEquals(0, _port);
    }

    @Test
    public void testNoHost() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.1\r\n" + ("Connection: close\r\n" + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals("No Host", _bad);
    }

    @Test
    public void testIPHost() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.1\r\n" + (("Host: 192.168.0.1\r\n" + "Connection: close\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals("192.168.0.1", _host);
        Assertions.assertEquals(0, _port);
    }

    @Test
    public void testIPv6Host() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.1\r\n" + (("Host: [::1]\r\n" + "Connection: close\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals("[::1]", _host);
        Assertions.assertEquals(0, _port);
    }

    @Test
    public void testBadIPv6Host() throws Exception {
        try (StacklessLogging s = new StacklessLogging(HttpParser.class)) {
            ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.1\r\n" + (("Host: [::1\r\n" + "Connection: close\r\n") + "\r\n")));
            HttpParser.RequestHandler handler = new HttpParserTest.Handler();
            HttpParser parser = new HttpParser(handler);
            parser.parseNext(buffer);
            MatcherAssert.assertThat(_bad, Matchers.containsString("Bad"));
        }
    }

    @Test
    public void testHostPort() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.1\r\n" + (("Host: myhost:8888\r\n" + "Connection: close\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals("myhost", _host);
        Assertions.assertEquals(8888, _port);
    }

    @Test
    public void testHostBadPort() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.1\r\n" + (("Host: myhost:testBadPort\r\n" + "Connection: close\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        MatcherAssert.assertThat(_bad, Matchers.containsString("Bad Host"));
    }

    @Test
    public void testIPHostPort() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.1\r\n" + (("Host: 192.168.0.1:8888\r\n" + "Connection: close\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals("192.168.0.1", _host);
        Assertions.assertEquals(8888, _port);
    }

    @Test
    public void testIPv6HostPort() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.1\r\n" + (("Host: [::1]:8888\r\n" + "Connection: close\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals("[::1]", _host);
        Assertions.assertEquals(8888, _port);
    }

    @Test
    public void testEmptyHostPort() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.1\r\n" + (("Host:\r\n" + "Connection: close\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals(null, _host);
        Assertions.assertEquals(null, _bad);
    }

    @Test
    @SuppressWarnings("ReferenceEquality")
    public void testCachedField() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.1\r\n" + ("Host: www.smh.com.au\r\n" + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertEquals("www.smh.com.au", parser.getFieldCache().get("Host: www.smh.com.au").getValue());
        HttpField field = _fields.get(0);
        buffer.position(0);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertTrue((field == (_fields.get(0))));
    }

    @Test
    public void testParseRequest() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("GET / HTTP/1.1\r\n" + ((((("Host: localhost\r\n" + "Header1: value1\r\n") + "Connection: close\r\n") + "Accept-Encoding: gzip, deflated\r\n") + "Accept: unknown\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        parser.parseNext(buffer);
        Assertions.assertEquals("GET", _methodOrVersion);
        Assertions.assertEquals("/", _uriOrStatus);
        Assertions.assertEquals("HTTP/1.1", _versionOrReason);
        Assertions.assertEquals("Host", _hdr[0]);
        Assertions.assertEquals("localhost", _val[0]);
        Assertions.assertEquals("Connection", _hdr[2]);
        Assertions.assertEquals("close", _val[2]);
        Assertions.assertEquals("Accept-Encoding", _hdr[3]);
        Assertions.assertEquals("gzip, deflated", _val[3]);
        Assertions.assertEquals("Accept", _hdr[4]);
        Assertions.assertEquals("unknown", _val[4]);
    }

    @Test
    public void testHTTP2Preface() throws Exception {
        ByteBuffer buffer = BufferUtil.toBuffer(("PRI * HTTP/2.0\r\n" + (("\r\n" + "SM\r\n") + "\r\n")));
        HttpParser.RequestHandler handler = new HttpParserTest.Handler();
        HttpParser parser = new HttpParser(handler);
        HttpParserTest.parseAll(parser, buffer);
        Assertions.assertTrue(_headerCompleted);
        Assertions.assertTrue(_messageCompleted);
        Assertions.assertEquals("PRI", _methodOrVersion);
        Assertions.assertEquals("*", _uriOrStatus);
        Assertions.assertEquals("HTTP/2.0", _versionOrReason);
        Assertions.assertEquals((-1), _headers);
        Assertions.assertEquals(null, _bad);
    }

    private String _host;

    private int _port;

    private String _bad;

    private String _content;

    private String _methodOrVersion;

    private String _uriOrStatus;

    private String _versionOrReason;

    private List<HttpField> _fields = new ArrayList<>();

    private List<HttpField> _trailers = new ArrayList<>();

    private String[] _hdr;

    private String[] _val;

    private int _headers;

    private boolean _early;

    private boolean _headerCompleted;

    private boolean _messageCompleted;

    private final List<HttpComplianceSection> _complianceViolation = new ArrayList<>();

    private class Handler implements HttpParser.ComplianceHandler , HttpParser.RequestHandler , HttpParser.ResponseHandler {
        @Override
        public boolean content(ByteBuffer ref) {
            if ((_content) == null)
                _content = "";

            String c = BufferUtil.toString(ref, StandardCharsets.UTF_8);
            _content = (_content) + c;
            ref.position(ref.limit());
            return false;
        }

        @Override
        public boolean startRequest(String method, String uri, HttpVersion version) {
            _fields.clear();
            _trailers.clear();
            _headers = -1;
            _hdr = new String[10];
            _val = new String[10];
            _methodOrVersion = method;
            _uriOrStatus = uri;
            _versionOrReason = (version == null) ? null : version.asString();
            _messageCompleted = false;
            _headerCompleted = false;
            _early = false;
            return false;
        }

        @Override
        public void parsedHeader(HttpField field) {
            _fields.add(field);
            _hdr[(++(_headers))] = field.getName();
            _val[_headers] = field.getValue();
            if (field instanceof HostPortHttpField) {
                HostPortHttpField hpfield = ((HostPortHttpField) (field));
                _host = hpfield.getHost();
                _port = hpfield.getPort();
            }
        }

        @Override
        public boolean headerComplete() {
            _content = null;
            _headerCompleted = true;
            return false;
        }

        @Override
        public void parsedTrailer(HttpField field) {
            _trailers.add(field);
        }

        @Override
        public boolean contentComplete() {
            return false;
        }

        @Override
        public boolean messageComplete() {
            _messageCompleted = true;
            return true;
        }

        @Override
        public void badMessage(BadMessageException failure) {
            String reason = failure.getReason();
            _bad = (reason == null) ? String.valueOf(failure.getCode()) : reason;
        }

        @Override
        public boolean startResponse(HttpVersion version, int status, String reason) {
            _fields.clear();
            _trailers.clear();
            _methodOrVersion = version.asString();
            _uriOrStatus = Integer.toString(status);
            _versionOrReason = reason;
            _headers = -1;
            _hdr = new String[10];
            _val = new String[10];
            _messageCompleted = false;
            _headerCompleted = false;
            return false;
        }

        @Override
        public void earlyEOF() {
            _early = true;
        }

        @Override
        public int getHeaderCacheSize() {
            return 4096;
        }

        @Override
        public void onComplianceViolation(HttpCompliance compliance, HttpComplianceSection violation, String reason) {
            _complianceViolation.add(violation);
        }
    }
}

