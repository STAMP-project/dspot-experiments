/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.jdk.connector.internal;


import java.io.IOException;
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Petr Janouch (petr.janouch at oracle.com)
 */
public class HttpParserTest {
    private static final Charset responseEncoding = Charset.forName("ISO-8859-1");

    private HttpParser httpParser;

    @Test
    public void testResponseLineInOnePiece() throws ParseException {
        testResponseLine(Integer.MAX_VALUE);
    }

    @Test
    public void testResponseLineSegmented() throws ParseException {
        testResponseLine(20);
    }

    @Test
    public void testHeadersInOnePiece() throws ParseException {
        testHeaders(Integer.MAX_VALUE);
    }

    @Test
    public void testHeadersSegmented() throws ParseException {
        testHeaders(20);
    }

    @Test
    public void testFixedLengthBodyInOnePiece() throws IOException, ParseException {
        testFixedLengthBody(Integer.MAX_VALUE);
    }

    @Test
    public void testFixedLengthBodySegmented() throws IOException, ParseException {
        testFixedLengthBody(20);
    }

    @Test
    public void testChunkedBodyInOnePiece() throws IOException, ParseException {
        testChunkedBody(Integer.MAX_VALUE, 25, generateBody());
    }

    @Test
    public void testChunkedBodySegmentedWithSmallChunk() throws IOException, ParseException {
        testChunkedBody(20, 15, generateBody());
    }

    @Test
    public void testChunkedBodySegmentedWithLargerChunk() throws IOException, ParseException {
        testChunkedBody(20, 23, generateBody());
    }

    @Test
    public void testEmptyChunkedBody() throws IOException, ParseException {
        testChunkedBody(Integer.MAX_VALUE, 25, "");
    }

    @Test
    public void testMultilineHeaderInOnePiece() throws ParseException {
        testMultilineHeader(Integer.MAX_VALUE);
    }

    @Test
    public void testMultilineHeaderSegmented() throws ParseException {
        testMultilineHeader(10);
    }

    @Test
    public void testMultilineHeaderNInOnePiece() throws ParseException {
        testMultilineHeaderN(Integer.MAX_VALUE);
    }

    @Test
    public void testMultilineHeaderNSegmented() throws ParseException {
        testMultilineHeaderN(10);
    }

    @Test
    public void testOverflowProtocol() {
        try {
            testOverflow("HTTP/1.0 404 Not found\n\n", 2);
            Assert.fail();
        } catch (ParseException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testOverflowCode() {
        try {
            testOverflow("HTTP/1.0 404 Not found\n\n", 11);
            Assert.fail();
        } catch (ParseException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testOverflowPhrase() {
        try {
            testOverflow("HTTP/1.0 404 Not found\n\n", 19);
            Assert.fail();
        } catch (ParseException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testOverflowHeader() {
        try {
            testOverflow("HTTP/1.0 404 Not found\nHeader1: somevalue\n\n", 30);
            Assert.fail();
        } catch (ParseException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testTrailerHeadersInOnePiece() throws IOException, ParseException {
        testTrailerHeaders(Integer.MAX_VALUE, 15);
    }

    @Test
    public void testTrailerHeadersSegmented() throws IOException, ParseException {
        testTrailerHeaders(20, 15);
    }

    @Test
    public void testSpacesInChunkSizeHeader() throws Exception {
        httpParser.reset(true);
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 123 A meaningful code\r\n").append("Transfer-Encoding: chunked\r\n\r\n");
        String body = "ABCDE";
        String bodyLen = Integer.toHexString(body.length());
        response.append("  ").append(bodyLen).append("  ").append("\r\n").append(body).append("\r\n");
        response.append("  0  ").append("\r\n").append("\r\n");
        feedParser(response.toString(), Integer.MAX_VALUE);
        Assert.assertTrue(httpParser.isHeaderParsed());
        Assert.assertTrue(httpParser.isComplete());
        verifyReceivedBody(body);
    }

    @Test
    public void testSameHeaders() throws ParseException {
        httpParser.reset(false);
        StringBuilder request = new StringBuilder();
        request.append("HTTP/1.1 123 A meaningful code\r\n").append("name1: value1\r\n").append("name2: value2\r\n").append("name3: value3\r\n").append("name2: value4\r\n\r\n");
        feedParser(request.toString(), Integer.MAX_VALUE);
        Assert.assertTrue(httpParser.isHeaderParsed());
        Assert.assertTrue(httpParser.isComplete());
        verifyHeaderValue("name1", "value1");
        verifyHeaderValue("name2", "value2", "value2");
        verifyHeaderValue("name3", "value3");
    }

    @Test
    public void testSameHeadersCommaSeparated() throws ParseException {
        httpParser.reset(false);
        StringBuilder request = new StringBuilder();
        request.append("HTTP/1.1 123 A meaningful code\r\n").append("name1: value1\r\n").append("name2: value2, value4\r\n").append("name3: value3\r\n\r\n");
        feedParser(request.toString(), Integer.MAX_VALUE);
        Assert.assertTrue(httpParser.isHeaderParsed());
        Assert.assertTrue(httpParser.isComplete());
        verifyHeaderValue("name1", "value1");
        verifyHeaderValue("name2", "value2", "value4");
        verifyHeaderValue("name3", "value3");
    }

    @Test
    public void testInseparableHeaders() throws ParseException {
        httpParser.reset(false);
        StringBuilder request = new StringBuilder();
        request.append("HTTP/1.1 123 A meaningful code\r\n").append("name1: value1\r\n").append("WWW-Authenticate: value2, value4\r\n").append("name3: value3, value5\r\n\r\n");
        feedParser(request.toString(), Integer.MAX_VALUE);
        Assert.assertTrue(httpParser.isHeaderParsed());
        Assert.assertTrue(httpParser.isComplete());
        verifyHeaderValue("name1", "value1");
        verifyHeaderValue("WWW-Authenticate", "value2, value4");
        verifyHeaderValue("name3", "value3", "value5");
    }
}

