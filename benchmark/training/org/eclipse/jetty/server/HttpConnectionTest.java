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
/**
 * Created on 9/01/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.jetty.server;


import HttpCompliance.RFC2616;
import HttpCompliance.RFC2616_LEGACY;
import HttpCompliance.RFC7230;
import HttpHeader.CONTENT_TYPE;
import HttpParser.LOG;
import HttpServletResponse.SC_BAD_REQUEST;
import HttpTester.Response;
import MimeTypes.Type.TEXT_HTML;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpParser;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.server.LocalConnector.LocalEndPoint;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.log.StacklessLogging;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class HttpConnectionTest {
    private Server server;

    private LocalConnector connector;

    @Test
    public void testFragmentedChunk() throws Exception {
        String response = null;
        try {
            int offset = 0;
            // Chunk last
            response = connector.getResponse(("GET /R1 HTTP/1.1\r\n" + (((((((("Host: localhost\r\n" + "Transfer-Encoding: chunked\r\n") + "Content-Type: text/plain\r\n") + "Connection: close\r\n") + "\r\n") + "5;\r\n") + "12345\r\n") + "0;\r\n") + "\r\n")));
            offset = checkContains(response, offset, "HTTP/1.1 200");
            offset = checkContains(response, offset, "/R1");
            checkContains(response, offset, "12345");
            offset = 0;
            response = connector.getResponse(("GET /R2 HTTP/1.1\r\n" + (((((((("Host: localhost\r\n" + "Transfer-Encoding: chunked\r\n") + "Content-Type: text/plain\r\n") + "Connection: close\r\n") + "\r\n") + "5;\r\n") + "ABCDE\r\n") + "0;\r\n") + "\r\n")));
            offset = checkContains(response, offset, "HTTP/1.1 200");
            offset = checkContains(response, offset, "/R2");
            checkContains(response, offset, "ABCDE");
        } catch (Exception e) {
            if (response != null)
                System.err.println(response);

            throw e;
        }
    }

    /**
     * HTTP/0.9 does not support HttpVersion (this is a bad request)
     */
    @Test
    public void testHttp09_NoVersion() throws Exception {
        connector.getConnectionFactory(HttpConnectionFactory.class).setHttpCompliance(RFC2616);
        String request = "GET / HTTP/0.9\r\n\r\n";
        String response = connector.getResponse(request);
        MatcherAssert.assertThat(response, CoreMatchers.containsString("400 Bad Version"));
        connector.getConnectionFactory(HttpConnectionFactory.class).setHttpCompliance(RFC7230);
        request = "GET / HTTP/0.9\r\n\r\n";
        response = connector.getResponse(request);
        MatcherAssert.assertThat(response, CoreMatchers.containsString("400 Bad Version"));
    }

    /**
     * HTTP/0.9 does not support headers
     */
    @Test
    public void testHttp09_NoHeaders() throws Exception {
        connector.getConnectionFactory(HttpConnectionFactory.class).setHttpCompliance(RFC2616);
        // header looking like another request is ignored
        String request = "GET /one\r\nGET :/two\r\n\r\n";
        String response = BufferUtil.toString(connector.executeRequest(request).waitForOutput(10, TimeUnit.SECONDS));
        MatcherAssert.assertThat(response, CoreMatchers.containsString("pathInfo=/"));
        MatcherAssert.assertThat(response, CoreMatchers.not(CoreMatchers.containsString("two")));
    }

    /**
     * Http/0.9 does not support pipelining.
     */
    @Test
    public void testHttp09_MultipleRequests() throws Exception {
        connector.getConnectionFactory(HttpConnectionFactory.class).setHttpCompliance(RFC2616);
        // Verify that pipelining does not work with HTTP/0.9.
        String requests = "GET /?id=123\r\n\r\nGET /?id=456\r\n\r\n";
        LocalEndPoint endp = connector.executeRequest(requests);
        String response = BufferUtil.toString(endp.waitForOutput(10, TimeUnit.SECONDS));
        MatcherAssert.assertThat(response, CoreMatchers.containsString("id=123"));
        MatcherAssert.assertThat(response, CoreMatchers.not(CoreMatchers.containsString("id=456")));
    }

    /**
     * Ensure that excessively large hexadecimal chunk body length is parsed properly.
     */
    @Test
    public void testHttp11_ChunkedBodyTruncation() throws Exception {
        String request = "POST /?id=123 HTTP/1.1\r\n" + (((((((((((((("Host: local\r\n" + "Transfer-Encoding: chunked\r\n") + "Content-Type: text/plain\r\n") + "Connection: close\r\n") + "\r\n") + "1ff00000008\r\n") + "abcdefgh\r\n") + "\r\n") + "0\r\n") + "\r\n") + "POST /?id=bogus HTTP/1.1\r\n") + "Content-Length: 5\r\n") + "Host: dummy-host.example.com\r\n") + "\r\n") + "12345");
        String response = connector.getResponse(request);
        MatcherAssert.assertThat(response, CoreMatchers.containsString(" 200 OK"));
        MatcherAssert.assertThat(response, CoreMatchers.containsString("Connection: close"));
        MatcherAssert.assertThat(response, CoreMatchers.containsString("Early EOF"));
    }

    /**
     * More then 1 Content-Length is a bad requests per HTTP rfcs.
     */
    @Test
    public void testHttp11_MultipleContentLength() throws Exception {
        LOG.info("badMessage: 400 Bad messages EXPECTED...");
        int[][] contentLengths = new int[][]{ new int[]{ 0, 8 }, new int[]{ 8, 0 }, new int[]{ 8, 8 }, new int[]{ 0, 8, 0 }, new int[]{ 1, 2, 3, 4, 5, 6, 7, 8 }, new int[]{ 8, 2, 1 }, new int[]{ 0, 0 }, new int[]{ 8, 0, 8 }, new int[]{ -1, 8 }, new int[]{ 8, -1 }, new int[]{ -1, 8, -1 }, new int[]{ -1, -1 }, new int[]{ 8, -1, 8 } };
        for (int x = 0; x < (contentLengths.length); x++) {
            StringBuilder request = new StringBuilder();
            request.append("POST /?id=").append(Integer.toString(x)).append(" HTTP/1.1\r\n");
            request.append("Host: local\r\n");
            int[] clen = contentLengths[x];
            for (int n = 0; n < (clen.length); n++) {
                request.append("Content-Length: ").append(Integer.toString(clen[n])).append("\r\n");
            }
            request.append("Content-Type: text/plain\r\n");
            request.append("Connection: close\r\n");
            request.append("\r\n");
            request.append("abcdefgh");// actual content of 8 bytes

            String rawResponse = connector.getResponse(request.toString());
            HttpTester.Response response = HttpTester.parseResponse(rawResponse);
            MatcherAssert.assertThat("Response.status", response.getStatus(), CoreMatchers.is(SC_BAD_REQUEST));
        }
    }

    /**
     * More then 1 Content-Length is a bad requests per HTTP rfcs.
     */
    @Test
    public void testHttp11_ContentLengthAndChunk() throws Exception {
        LOG.info("badMessage: 400 Bad messages EXPECTED...");
        int[][] contentLengths = new int[][]{ new int[]{ -1, 8 }, new int[]{ 8, -1 }, new int[]{ 8, -1, 8 } };
        for (int x = 0; x < (contentLengths.length); x++) {
            StringBuilder request = new StringBuilder();
            request.append("POST /?id=").append(Integer.toString(x)).append(" HTTP/1.1\r\n");
            request.append("Host: local\r\n");
            int[] clen = contentLengths[x];
            for (int n = 0; n < (clen.length); n++) {
                if ((clen[n]) == (-1))
                    request.append("Transfer-Encoding: chunked\r\n");
                else
                    request.append("Content-Length: ").append(Integer.toString(clen[n])).append("\r\n");

            }
            request.append("Content-Type: text/plain\r\n");
            request.append("Connection: close\r\n");
            request.append("\r\n");
            request.append("8;\r\n");// chunk header

            request.append("abcdefgh");// actual content of 8 bytes

            request.append("\r\n0;\r\n");// last chunk

            String rawResponse = connector.getResponse(request.toString());
            HttpTester.Response response = HttpTester.parseResponse(rawResponse);
            MatcherAssert.assertThat("Response.status", response.getStatus(), CoreMatchers.is(SC_BAD_REQUEST));
        }
    }

    @Test
    public void testNoPath() throws Exception {
        String response = connector.getResponse(("GET http://localhost:80 HTTP/1.1\r\n" + (("Host: localhost:80\r\n" + "Connection: close\r\n") + "\r\n")));
        int offset = 0;
        offset = checkContains(response, offset, "HTTP/1.1 200");
        checkContains(response, offset, "pathInfo=/");
    }

    @Test
    public void testDate() throws Exception {
        String response = connector.getResponse(("GET / HTTP/1.1\r\n" + (("Host: localhost:80\r\n" + "Connection: close\r\n") + "\r\n")));
        int offset = 0;
        offset = checkContains(response, offset, "HTTP/1.1 200");
        offset = checkContains(response, offset, "Date: ");
        checkContains(response, offset, "pathInfo=/");
    }

    @Test
    public void testSetDate() throws Exception {
        String response = connector.getResponse(("GET /?date=1+Jan+1970 HTTP/1.1\r\n" + (("Host: localhost:80\r\n" + "Connection: close\r\n") + "\r\n")));
        int offset = 0;
        offset = checkContains(response, offset, "HTTP/1.1 200");
        offset = checkContains(response, offset, "Date: 1 Jan 1970");
        checkContains(response, offset, "pathInfo=/");
    }

    @Test
    public void testBadNoPath() throws Exception {
        String response = connector.getResponse(("GET http://localhost:80/../cheat HTTP/1.1\r\n" + ("Host: localhost:80\r\n" + "\r\n")));
        checkContains(response, 0, "HTTP/1.1 400");
    }

    @Test
    public void testOKPathDotDotPath() throws Exception {
        String response = connector.getResponse("GET /ooops/../path HTTP/1.0\r\nHost: localhost:80\r\n\n");
        checkContains(response, 0, "HTTP/1.1 200 OK");
        checkContains(response, 0, "pathInfo=/path");
    }

    @Test
    public void testBadPathDotDotPath() throws Exception {
        String response = connector.getResponse("GET /ooops/../../path HTTP/1.0\r\nHost: localhost:80\r\n\n");
        checkContains(response, 0, "HTTP/1.1 400 Bad URI");
    }

    @Test
    public void testOKPathEncodedDotDotPath() throws Exception {
        String response = connector.getResponse("GET /ooops/%2e%2e/path HTTP/1.0\r\nHost: localhost:80\r\n\n");
        checkContains(response, 0, "HTTP/1.1 200 OK");
        checkContains(response, 0, "pathInfo=/path");
    }

    @Test
    public void testBadPathEncodedDotDotPath() throws Exception {
        String response = connector.getResponse("GET /ooops/%2e%2e/%2e%2e/path HTTP/1.0\r\nHost: localhost:80\r\n\n");
        checkContains(response, 0, "HTTP/1.1 400 Bad URI");
    }

    @Test
    public void testBadDotDotPath() throws Exception {
        String response = connector.getResponse("GET ../path HTTP/1.0\r\nHost: localhost:80\r\n\n");
        checkContains(response, 0, "HTTP/1.1 400 Bad URI");
    }

    @Test
    public void testBadSlashDotDotPath() throws Exception {
        String response = connector.getResponse("GET /../path HTTP/1.0\r\nHost: localhost:80\r\n\n");
        checkContains(response, 0, "HTTP/1.1 400 Bad URI");
    }

    @Test
    public void testEncodedBadDotDotPath() throws Exception {
        String response = connector.getResponse("GET %2e%2e/path HTTP/1.0\r\nHost: localhost:80\r\n\n");
        checkContains(response, 0, "HTTP/1.1 400 Bad URI");
    }

    @Test
    public void test_0_9() throws Exception {
        connector.getConnectionFactory(HttpConnectionFactory.class).setHttpCompliance(RFC2616_LEGACY);
        LocalEndPoint endp = connector.executeRequest("GET /R1\n");
        endp.waitUntilClosed();
        String response = BufferUtil.toString(endp.takeOutput());
        int offset = 0;
        checkNotContained(response, offset, "HTTP/1.1");
        checkNotContained(response, offset, "200");
        checkContains(response, offset, "pathInfo=/R1");
    }

    @Test
    public void testSimple() throws Exception {
        String response = connector.getResponse(("GET /R1 HTTP/1.1\r\n" + (("Host: localhost\r\n" + "Connection: close\r\n") + "\r\n")));
        int offset = 0;
        offset = checkContains(response, offset, "HTTP/1.1 200");
        checkContains(response, offset, "/R1");
    }

    @Test
    public void testEmptyNotPersistent() throws Exception {
        String response = connector.getResponse(("GET /R1?empty=true HTTP/1.0\r\n" + ("Host: localhost\r\n" + "\r\n")));
        int offset = 0;
        offset = checkContains(response, offset, "HTTP/1.1 200");
        checkNotContained(response, offset, "Content-Length");
        response = connector.getResponse(("GET /R1?empty=true HTTP/1.1\r\n" + (("Host: localhost\r\n" + "Connection: close\r\n") + "\r\n")));
        offset = 0;
        offset = checkContains(response, offset, "HTTP/1.1 200");
        checkContains(response, offset, "Connection: close");
        checkNotContained(response, offset, "Content-Length");
    }

    @Test
    public void testEmptyPersistent() throws Exception {
        String response = connector.getResponse(("GET /R1?empty=true HTTP/1.0\r\n" + (("Host: localhost\r\n" + "Connection: keep-alive\r\n") + "\r\n")));
        int offset = 0;
        offset = checkContains(response, offset, "HTTP/1.1 200");
        checkContains(response, offset, "Content-Length: 0");
        checkNotContained(response, offset, "Connection: close");
        response = connector.getResponse(("GET /R1?empty=true HTTP/1.1\r\n" + ("Host: localhost\r\n" + "\r\n")));
        offset = 0;
        offset = checkContains(response, offset, "HTTP/1.1 200");
        checkContains(response, offset, "Content-Length: 0");
        checkNotContained(response, offset, "Connection: close");
    }

    @Test
    public void testEmptyChunk() throws Exception {
        String response = connector.getResponse(("GET /R1 HTTP/1.1\r\n" + (((((("Host: localhost\r\n" + "Transfer-Encoding: chunked\r\n") + "Content-Type: text/plain\r\n") + "Connection: close\r\n") + "\r\n") + "0\r\n") + "\r\n")));
        int offset = 0;
        offset = checkContains(response, offset, "HTTP/1.1 200");
        checkContains(response, offset, "/R1");
    }

    @Test
    public void testChunk() throws Exception {
        String response = connector.getResponse(("GET /R1 HTTP/1.1\r\n" + (((((((("Host: localhost\r\n" + "Transfer-Encoding: chunked\r\n") + "Content-Type: text/plain\r\n") + "Connection: close\r\n") + "\r\n") + "A\r\n") + "0123456789\r\n") + "0\r\n") + "\r\n")));
        int offset = 0;
        offset = checkContains(response, offset, "HTTP/1.1 200");
        offset = checkContains(response, offset, "/R1");
        checkContains(response, offset, "0123456789");
    }

    @Test
    public void testChunkTrailer() throws Exception {
        String response = connector.getResponse(("GET /R1 HTTP/1.1\r\n" + ((((((((("Host: localhost\r\n" + "Transfer-Encoding: chunked\r\n") + "Content-Type: text/plain\r\n") + "Connection: close\r\n") + "\r\n") + "A\r\n") + "0123456789\r\n") + "0\r\n") + "Trailer: ignored\r\n") + "\r\n")));
        int offset = 0;
        offset = checkContains(response, offset, "HTTP/1.1 200");
        offset = checkContains(response, offset, "/R1");
        checkContains(response, offset, "0123456789");
    }

    @Test
    public void testChunkNoTrailer() throws Exception {
        // Expect TimeoutException logged
        String response = connector.getResponse(("GET /R1 HTTP/1.1\r\n" + ((((((("Host: localhost\r\n" + "Transfer-Encoding: chunked\r\n") + "Content-Type: text/plain\r\n") + "Connection: close\r\n") + "\r\n") + "A\r\n") + "0123456789\r\n") + "0\r\n")));
        int offset = 0;
        offset = checkContains(response, offset, "HTTP/1.1 200");
        offset = checkContains(response, offset, "/R1");
        checkContains(response, offset, "0123456789");
    }

    @Test
    public void testHead() throws Exception {
        String responsePOST = connector.getResponse(("POST /R1 HTTP/1.1\r\n" + (("Host: localhost\r\n" + "Connection: close\r\n") + "\r\n")));
        String responseHEAD = connector.getResponse(("HEAD /R1 HTTP/1.1\r\n" + (("Host: localhost\r\n" + "Connection: close\r\n") + "\r\n")));
        String postLine;
        boolean postDate = false;
        Set<String> postHeaders = new HashSet<>();
        try (BufferedReader in = new BufferedReader(new StringReader(responsePOST))) {
            postLine = in.readLine();
            String line = in.readLine();
            while ((line != null) && ((line.length()) > 0)) {
                if (line.startsWith("Date:"))
                    postDate = true;
                else
                    postHeaders.add(line);

                line = in.readLine();
            } 
        }
        String headLine;
        boolean headDate = false;
        Set<String> headHeaders = new HashSet<>();
        try (BufferedReader in = new BufferedReader(new StringReader(responseHEAD))) {
            headLine = in.readLine();
            String line = in.readLine();
            while ((line != null) && ((line.length()) > 0)) {
                if (line.startsWith("Date:"))
                    headDate = true;
                else
                    headHeaders.add(line);

                line = in.readLine();
            } 
        }
        MatcherAssert.assertThat(postLine, Matchers.equalTo(headLine));
        MatcherAssert.assertThat(postDate, Matchers.equalTo(headDate));
        Assertions.assertTrue(postHeaders.equals(headHeaders));
    }

    @Test
    public void testHeadChunked() throws Exception {
        String responsePOST = connector.getResponse(("POST /R1?no-content-length=true HTTP/1.1\r\n" + ("Host: localhost\r\n" + "\r\n")), false, 1, TimeUnit.SECONDS);
        String responseHEAD = connector.getResponse(("HEAD /R1?no-content-length=true HTTP/1.1\r\n" + ("Host: localhost\r\n" + "\r\n")), true, 1, TimeUnit.SECONDS);
        String postLine;
        boolean postDate = false;
        Set<String> postHeaders = new HashSet<>();
        try (BufferedReader in = new BufferedReader(new StringReader(responsePOST))) {
            postLine = in.readLine();
            String line = in.readLine();
            while ((line != null) && ((line.length()) > 0)) {
                if (line.startsWith("Date:"))
                    postDate = true;
                else
                    postHeaders.add(line);

                line = in.readLine();
            } 
        }
        String headLine;
        boolean headDate = false;
        Set<String> headHeaders = new HashSet<>();
        try (BufferedReader in = new BufferedReader(new StringReader(responseHEAD))) {
            headLine = in.readLine();
            String line = in.readLine();
            while ((line != null) && ((line.length()) > 0)) {
                if (line.startsWith("Date:"))
                    headDate = true;
                else
                    headHeaders.add(line);

                line = in.readLine();
            } 
        }
        MatcherAssert.assertThat(postLine, Matchers.equalTo(headLine));
        MatcherAssert.assertThat(postDate, Matchers.equalTo(headDate));
        Assertions.assertTrue(postHeaders.equals(headHeaders));
    }

    @Test
    public void testBadHostPort() throws Exception {
        Log.getLogger(HttpParser.class).info("badMessage: Number formate exception expected ...");
        String response;
        response = connector.getResponse(("GET http://localhost:EXPECTED_NUMBER_FORMAT_EXCEPTION/ HTTP/1.1\r\n" + (("Host: localhost\r\n" + "Connection: close\r\n") + "\r\n")));
        checkContains(response, 0, "HTTP/1.1 400");
    }

    @Test
    public void testNoHost() throws Exception {
        String response;
        response = connector.getResponse(("GET / HTTP/1.1\r\n" + "\r\n"));
        checkContains(response, 0, "HTTP/1.1 400");
    }

    @Test
    public void testEmptyHost() throws Exception {
        String response;
        response = connector.getResponse(("GET / HTTP/1.1\r\n" + ("Host:\r\n" + "\r\n")));
        checkContains(response, 0, "HTTP/1.1 200");
    }

    @Test
    public void testBadURIencoding() throws Exception {
        Log.getLogger(HttpParser.class).info("badMessage: bad encoding expected ...");
        String response;
        try (StacklessLogging stackless = new StacklessLogging(HttpParser.class)) {
            response = connector.getResponse(("GET /bad/encoding%1 HTTP/1.1\r\n" + (("Host: localhost\r\n" + "Connection: close\r\n") + "\r\n")));
            checkContains(response, 0, "HTTP/1.1 400");
        }
    }

    @Test
    public void testBadUTF8FallsbackTo8859() throws Exception {
        Log.getLogger(HttpParser.class).info("badMessage: bad encoding expected ...");
        String response;
        response = connector.getResponse(("GET /foo/bar%c0%00 HTTP/1.1\r\n" + (("Host: localhost\r\n" + "Connection: close\r\n") + "\r\n")));
        checkContains(response, 0, "HTTP/1.1 200");// now fallback to iso-8859-1

        response = connector.getResponse(("GET /bad/utf8%c1 HTTP/1.1\r\n" + (("Host: localhost\r\n" + "Connection: close\r\n") + "\r\n")));
        checkContains(response, 0, "HTTP/1.1 200");// now fallback to iso-8859-1

    }

    @Test
    public void testAutoFlush() throws Exception {
        int offset = 0;
        String response = connector.getResponse(("GET /R1 HTTP/1.1\r\n" + (((((((("Host: localhost\r\n" + "Transfer-Encoding: chunked\r\n") + "Content-Type: text/plain\r\n") + "Connection: close\r\n") + "\r\n") + "5;\r\n") + "12345\r\n") + "0;\r\n") + "\r\n")));
        offset = checkContains(response, offset, "HTTP/1.1 200");
        checkNotContained(response, offset, "IgnoreMe");
        offset = checkContains(response, offset, "/R1");
        checkContains(response, offset, "12345");
    }

    @Test
    public void testEmptyFlush() throws Exception {
        server.stop();
        server.setHandler(new AbstractHandler() {
            @SuppressWarnings("unused")
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                response.setStatus(200);
                OutputStream out = response.getOutputStream();
                out.flush();
                out.flush();
            }
        });
        server.start();
        String response = connector.getResponse(("GET / HTTP/1.1\r\n" + (("Host: localhost\r\n" + "Connection: close\r\n") + "\r\n")));
        MatcherAssert.assertThat(response, Matchers.containsString("200 OK"));
    }

    @Test
    public void testCharset() throws Exception {
        String response = null;
        try {
            int offset = 0;
            response = connector.getResponse(("GET /R1 HTTP/1.1\r\n" + (((((((("Host: localhost\r\n" + "Transfer-Encoding: chunked\r\n") + "Content-Type: text/plain; charset=utf-8\r\n") + "Connection: close\r\n") + "\r\n") + "5;\r\n") + "12345\r\n") + "0;\r\n") + "\r\n")));
            offset = checkContains(response, offset, "HTTP/1.1 200");
            offset = checkContains(response, offset, "/R1");
            offset = checkContains(response, offset, "encoding=UTF-8");
            checkContains(response, offset, "12345");
            offset = 0;
            response = connector.getResponse(("GET /R1 HTTP/1.1\r\n" + (((((((("Host: localhost\r\n" + "Transfer-Encoding: chunked\r\n") + "Content-Type: text/plain; charset =  iso-8859-1 ; other=value\r\n") + "Connection: close\r\n") + "\r\n") + "5;\r\n") + "12345\r\n") + "0;\r\n") + "\r\n")));
            offset = checkContains(response, offset, "HTTP/1.1 200");
            offset = checkContains(response, offset, "encoding=iso-8859-1");
            offset = checkContains(response, offset, "/R1");
            checkContains(response, offset, "12345");
            offset = 0;
            Log.getLogger(DumpHandler.class).info("Expecting java.io.UnsupportedEncodingException");
            response = connector.getResponse(("GET /R1 HTTP/1.1\r\n" + (((((((("Host: localhost\r\n" + "Transfer-Encoding: chunked\r\n") + "Content-Type: text/plain; charset=unknown\r\n") + "Connection: close\r\n") + "\r\n") + "5;\r\n") + "12345\r\n") + "0;\r\n") + "\r\n")));
            offset = checkContains(response, offset, "HTTP/1.1 200");
            offset = checkContains(response, offset, "encoding=unknown");
            offset = checkContains(response, offset, "/R1");
            checkContains(response, offset, "UnsupportedEncodingException");
        } catch (Exception e) {
            if (response != null)
                System.err.println(response);

            throw e;
        }
    }

    @Test
    public void testUnconsumed() throws Exception {
        int offset = 0;
        String requests = "GET /R1?read=4 HTTP/1.1\r\n" + (((((((((((((((("Host: localhost\r\n" + "Transfer-Encoding: chunked\r\n") + "Content-Type: text/plain; charset=utf-8\r\n") + "\r\n") + "5;\r\n") + "12345\r\n") + "5;\r\n") + "67890\r\n") + "0;\r\n") + "\r\n") + "GET /R2 HTTP/1.1\r\n") + "Host: localhost\r\n") + "Content-Type: text/plain; charset=utf-8\r\n") + "Content-Length: 10\r\n") + "Connection: close\r\n") + "\r\n") + "abcdefghij\r\n");
        LocalEndPoint endp = connector.executeRequest(requests);
        String response = (endp.getResponse()) + (endp.getResponse());
        offset = checkContains(response, offset, "HTTP/1.1 200");
        offset = checkContains(response, offset, "pathInfo=/R1");
        offset = checkContains(response, offset, "1234");
        checkNotContained(response, offset, "56789");
        offset = checkContains(response, offset, "HTTP/1.1 200");
        offset = checkContains(response, offset, "pathInfo=/R2");
        offset = checkContains(response, offset, "encoding=UTF-8");
        checkContains(response, offset, "abcdefghij");
    }

    @Test
    public void testUnconsumedTimeout() throws Exception {
        connector.setIdleTimeout(500);
        int offset = 0;
        String requests = "GET /R1?read=4 HTTP/1.1\r\n" + ((((("Host: localhost\r\n" + "Transfer-Encoding: chunked\r\n") + "Content-Type: text/plain; charset=utf-8\r\n") + "\r\n") + "5;\r\n") + "12345\r\n");
        long start = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        String response = connector.getResponse(requests, 2000, TimeUnit.MILLISECONDS);
        MatcherAssert.assertThat(((TimeUnit.NANOSECONDS.toMillis(System.nanoTime())) - start), Matchers.lessThanOrEqualTo(2000L));
        offset = checkContains(response, offset, "HTTP/1.1 200");
        offset = checkContains(response, offset, "pathInfo=/R1");
        offset = checkContains(response, offset, "1234");
        checkNotContained(response, offset, "56789");
    }

    @Test
    public void testUnconsumedErrorRead() throws Exception {
        int offset = 0;
        String requests = "GET /R1?read=1&error=499 HTTP/1.1\r\n" + (((((((((((((((("Host: localhost\r\n" + "Transfer-Encoding: chunked\r\n") + "Content-Type: text/plain; charset=utf-8\r\n") + "\r\n") + "5;\r\n") + "12345\r\n") + "5;\r\n") + "67890\r\n") + "0;\r\n") + "\r\n") + "GET /R2 HTTP/1.1\r\n") + "Host: localhost\r\n") + "Content-Type: text/plain; charset=utf-8\r\n") + "Content-Length: 10\r\n") + "Connection: close\r\n") + "\r\n") + "abcdefghij\r\n");
        LocalEndPoint endp = connector.executeRequest(requests);
        String response = (endp.getResponse()) + (endp.getResponse());
        offset = checkContains(response, offset, "HTTP/1.1 499");
        offset = checkContains(response, offset, "HTTP/1.1 200");
        offset = checkContains(response, offset, "/R2");
        offset = checkContains(response, offset, "encoding=UTF-8");
        checkContains(response, offset, "abcdefghij");
    }

    @Test
    public void testUnconsumedErrorStream() throws Exception {
        int offset = 0;
        String requests = "GET /R1?error=599 HTTP/1.1\r\n" + (((((((((((((((("Host: localhost\r\n" + "Transfer-Encoding: chunked\r\n") + "Content-Type: application/data; charset=utf-8\r\n") + "\r\n") + "5;\r\n") + "12345\r\n") + "5;\r\n") + "67890\r\n") + "0;\r\n") + "\r\n") + "GET /R2 HTTP/1.1\r\n") + "Host: localhost\r\n") + "Content-Type: text/plain; charset=utf-8\r\n") + "Content-Length: 10\r\n") + "Connection: close\r\n") + "\r\n") + "abcdefghij\r\n");
        LocalEndPoint endp = connector.executeRequest(requests);
        String response = (endp.getResponse()) + (endp.getResponse());
        offset = checkContains(response, offset, "HTTP/1.1 599");
        offset = checkContains(response, offset, "HTTP/1.1 200");
        offset = checkContains(response, offset, "/R2");
        offset = checkContains(response, offset, "encoding=UTF-8");
        checkContains(response, offset, "abcdefghij");
    }

    @Test
    public void testUnconsumedException() throws Exception {
        int offset = 0;
        String requests = "GET /R1?read=1&ISE=true HTTP/1.1\r\n" + ((((((((((((((("Host: localhost\r\n" + "Transfer-Encoding: chunked\r\n") + "Content-Type: text/plain; charset=utf-8\r\n") + "\r\n") + "5;\r\n") + "12345\r\n") + "5;\r\n") + "67890\r\n") + "0;\r\n") + "\r\n") + "GET /R2 HTTP/1.1\r\n") + "Host: localhost\r\n") + "Content-Type: text/plain; charset=utf-8\r\n") + "Content-Length: 10\r\n") + "\r\n") + "abcdefghij\r\n");
        Logger logger = Log.getLogger(HttpChannel.class);
        try (StacklessLogging stackless = new StacklessLogging(logger)) {
            logger.info("EXPECTING: java.lang.IllegalStateException...");
            String response = connector.getResponse(requests);
            offset = checkContains(response, offset, "HTTP/1.1 500");
            offset = checkContains(response, offset, "Connection: close");
            checkNotContained(response, offset, "HTTP/1.1 200");
        }
    }

    @Test
    public void testConnection() throws Exception {
        String response = null;
        try {
            int offset = 0;
            response = connector.getResponse(("GET /R1 HTTP/1.1\r\n" + (((((((("Host: localhost\r\n" + "Connection: TE, close\r\n") + "Transfer-Encoding: chunked\r\n") + "Content-Type: text/plain; charset=utf-8\r\n") + "\r\n") + "5;\r\n") + "12345\r\n") + "0;\r\n") + "\r\n")));
            checkContains(response, offset, "Connection: close");
        } catch (Exception e) {
            if (response != null)
                System.err.println(response);

            throw e;
        }
    }

    /**
     * Creates a request header over 1k in size, by creating a single header entry with an huge value.
     *
     * @throws Exception
     * 		if test failure
     */
    @Test
    public void testOversizedBuffer() throws Exception {
        String response = null;
        try {
            int offset = 0;
            String cookie = "thisisastringthatshouldreachover1kbytes";
            for (int i = 0; i < 100; i++)
                cookie += "xxxxxxxxxxxx";

            response = connector.getResponse((((("GET / HTTP/1.1\r\n" + ("Host: localhost\r\n" + "Cookie: ")) + cookie) + "\r\n") + "\r\n"));
            checkContains(response, offset, "HTTP/1.1 431");
        } catch (Exception e) {
            if (response != null)
                System.err.println(response);

            throw e;
        }
    }

    /**
     * Creates a request header with over 1000 entries.
     *
     * @throws Exception
     * 		if test failure
     */
    @Test
    public void testExcessiveHeader() throws Exception {
        int offset = 0;
        StringBuilder request = new StringBuilder();
        request.append("GET / HTTP/1.1\r\n");
        request.append("Host: localhost\r\n");
        request.append("Cookie: thisisastring\r\n");
        for (int i = 0; i < 1000; i++) {
            request.append(String.format("X-Header-%04d: %08x\r\n", i, i));
        }
        request.append("\r\n");
        String response = connector.getResponse(request.toString());
        offset = checkContains(response, offset, "HTTP/1.1 431");
        checkContains(response, offset, "<h1>Bad Message 431</h1>");
    }

    @Test
    public void testOversizedResponse() throws Exception {
        String str = "thisisastringthatshouldreachover1kbytes-";
        for (int i = 0; i < 500; i++)
            str += "xxxxxxxxxxxx";

        final String longstr = str;
        final CountDownLatch checkError = new CountDownLatch(1);
        server.stop();
        server.setHandler(new AbstractHandler.ErrorDispatchHandler() {
            @SuppressWarnings("unused")
            @Override
            protected void doNonErrorHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                baseRequest.setHandled(true);
                response.setHeader(CONTENT_TYPE.toString(), TEXT_HTML.toString());
                response.setHeader("LongStr", longstr);
                PrintWriter writer = response.getWriter();
                writer.write("<html><h1>FOO</h1></html>");
                writer.flush();
                if (writer.checkError())
                    checkError.countDown();

                response.flushBuffer();
            }
        });
        server.start();
        Logger logger = Log.getLogger(HttpChannel.class);
        String response = null;
        try (StacklessLogging stackless = new StacklessLogging(logger)) {
            logger.info("Expect IOException: Response header too large...");
            response = connector.getResponse(("GET / HTTP/1.1\r\n" + ("Host: localhost\r\n" + "\r\n")));
            checkContains(response, 0, "HTTP/1.1 500");
            Assertions.assertTrue(checkError.await(1, TimeUnit.SECONDS));
        } catch (Exception e) {
            if (response != null)
                System.err.println(response);

            throw e;
        }
    }

    @Test
    public void testAsterisk() throws Exception {
        String response = null;
        try (StacklessLogging stackless = new StacklessLogging(HttpParser.LOG)) {
            int offset = 0;
            response = connector.getResponse(("OPTIONS * HTTP/1.1\r\n" + (((((((("Host: localhost\r\n" + "Transfer-Encoding: chunked\r\n") + "Content-Type: text/plain; charset=utf-8\r\n") + "Connection: close\r\n") + "\r\n") + "5;\r\n") + "12345\r\n") + "0;\r\n") + "\r\n")));
            checkContains(response, offset, "HTTP/1.1 200");
            offset = 0;
            response = connector.getResponse(("GET * HTTP/1.1\r\n" + (((((((("Host: localhost\r\n" + "Transfer-Encoding: chunked\r\n") + "Content-Type: text/plain; charset=utf-8\r\n") + "Connection: close\r\n") + "\r\n") + "5;\r\n") + "12345\r\n") + "0;\r\n") + "\r\n")));
            checkContains(response, offset, "HTTP/1.1 400");
            offset = 0;
            response = connector.getResponse(("GET ** HTTP/1.1\r\n" + (((((((("Host: localhost\r\n" + "Transfer-Encoding: chunked\r\n") + "Content-Type: text/plain; charset=utf-8\r\n") + "Connection: close\r\n") + "\r\n") + "5;\r\n") + "12345\r\n") + "0;\r\n") + "\r\n")));
            checkContains(response, offset, "HTTP/1.1 400 Bad Request");
        } catch (Exception e) {
            if (response != null)
                System.err.println(response);

            throw e;
        }
    }

    @Test
    public void testCONNECT() throws Exception {
        String response = null;
        try {
            int offset = 0;
            response = connector.getResponse(("CONNECT www.webtide.com:8080 HTTP/1.1\r\n" + ("Host: myproxy:8888\r\n" + "\r\n")), 200, TimeUnit.MILLISECONDS);
            checkContains(response, offset, "HTTP/1.1 200");
        } catch (Exception e) {
            if (response != null)
                System.err.println(response);

            throw e;
        }
    }
}

