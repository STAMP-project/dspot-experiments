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
package org.eclipse.jetty.servlet;


import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDir;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDirExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(WorkDirExtension.class)
public class DefaultServletRangesTest {
    public static final String DATA = "01234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWZYZ!@#$%^&*()_+/.,[]";

    public WorkDir testdir;

    private Server server;

    private LocalConnector connector;

    private ServletContextHandler context;

    @Test
    public void testNoRangeRequests() throws Exception {
        String response;
        response = connector.getResponse(("GET /context/data.txt HTTP/1.1\r\n" + (("Host: localhost\r\n" + "Connection: close\r\n") + "\r\n")));
        assertResponseContains("200 OK", response);
        assertResponseContains("Accept-Ranges: bytes", response);
        assertResponseContains(DefaultServletRangesTest.DATA, response);
    }

    @Test
    public void testPrefixRangeRequests() throws Exception {
        String response;
        response = connector.getResponse(("GET /context/data.txt HTTP/1.1\r\n" + ((("Host: localhost\r\n" + "Connection: close\r\n") + "Range: bytes=0-9\r\n") + "\r\n")));
        assertResponseContains("206 Partial", response);
        assertResponseContains("Content-Type: text/plain", response);
        assertResponseContains("Content-Range: bytes 0-9/80", response);
        assertResponseContains(DefaultServletRangesTest.DATA.substring(0, 10), response);
    }

    @Test
    public void testSingleRangeRequests() throws Exception {
        String response;
        response = connector.getResponse(("GET /context/data.txt HTTP/1.1\r\n" + ((("Host: localhost\r\n" + "Connection: close\r\n") + "Range: bytes=3-9\r\n") + "\r\n")));
        assertResponseContains("206 Partial", response);
        assertResponseContains("Content-Type: text/plain", response);
        assertResponseContains("Content-Range: bytes 3-9/80", response);
        assertResponseContains(DefaultServletRangesTest.DATA.substring(3, 10), response);
    }

    @Test
    public void testMultipleRangeRequests() throws Exception {
        String response;
        response = connector.getResponse(("GET /context/data.txt HTTP/1.1\r\n" + ((("Host: localhost\r\n" + "Connection: close\r\n") + "Range: bytes=0-9,20-29,40-49\r\n") + "\r\n")));
        int start = response.indexOf("--jetty");
        String body = response.substring(start);
        String boundary = body.substring(0, body.indexOf("\r\n"));
        assertResponseContains("206 Partial", response);
        assertResponseContains("Content-Type: multipart/byteranges; boundary=", response);
        assertResponseContains("Content-Range: bytes 0-9/80", response);
        assertResponseContains("Content-Range: bytes 20-29/80", response);
        assertResponseContains("Content-Range: bytes 40-49/80", response);
        assertResponseContains(DefaultServletRangesTest.DATA.substring(0, 10), response);
        assertResponseContains(DefaultServletRangesTest.DATA.substring(20, 30), response);
        assertResponseContains(DefaultServletRangesTest.DATA.substring(40, 50), response);
        Assertions.assertTrue(body.endsWith((boundary + "--\r\n")));
    }

    @Test
    public void testMultipleSameRangeRequests() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            stringBuilder.append("10-60,");
        }
        String response;
        response = connector.getResponse((((("GET /context/data.txt HTTP/1.1\r\n" + (("Host: localhost\r\n" + "Connection: close\r\n") + "Range: bytes=")) + (stringBuilder.toString())) + "0-2\r\n") + "\r\n"));
        int start = response.indexOf("--jetty");
        String body = response.substring(start);
        String boundary = body.substring(0, body.indexOf("\r\n"));
        assertResponseContains("206 Partial", response);
        assertResponseContains("Content-Type: multipart/byteranges; boundary=", response);
        assertResponseContains("Content-Range: bytes 10-60/80", response);
        assertResponseContains("Content-Range: bytes 0-2/80", response);
        // 
        Assertions.assertEquals(2, response.split("Content-Range: bytes 10-60/80").length, ("Content range 0-60/80 in response not only 1:" + response));
        Assertions.assertTrue(body.endsWith((boundary + "--\r\n")));
    }

    @Test
    public void testMultipleSameRangeRequestsTooLargeHeader() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 2000; i++) {
            stringBuilder.append("10-60,");
        }
        String response;
        response = connector.getResponse((((("GET /context/data.txt HTTP/1.1\r\n" + (("Host: localhost\r\n" + "Connection: close\r\n") + "Range: bytes=")) + (stringBuilder.toString())) + "0-2\r\n") + "\r\n"));
        int start = response.indexOf("--jetty");
        Assertions.assertEquals((-1), start);
        assertResponseContains("HTTP/1.1 431 Request Header Fields Too Large", response);
    }

    @Test
    public void testOpenEndRange() throws Exception {
        String response;
        response = connector.getResponse(("GET /context/data.txt HTTP/1.1\r\n" + ((("Host: localhost\r\n" + "Connection: close\r\n") + "Range: bytes=20-\r\n") + "\r\n")));
        assertResponseContains("206 Partial", response);
        assertResponseNotContains("Content-Type: multipart/byteranges; boundary=", response);
        assertResponseContains("Content-Range: bytes 20-79/80", response);
        assertResponseContains(DefaultServletRangesTest.DATA.substring(60), response);
    }

    @Test
    public void testOpenStartRange() throws Exception {
        String response;
        response = connector.getResponse(("GET /context/data.txt HTTP/1.1\r\n" + ((("Host: localhost\r\n" + "Connection: close\r\n") + "Range: bytes=-20\r\n") + "\r\n")));
        assertResponseContains("206 Partial", response);
        assertResponseNotContains("Content-Type: multipart/byteranges; boundary=", response);
        assertResponseContains("Content-Range: bytes 60-79/80", response);// yes the spec says it is these bytes

        assertResponseContains(DefaultServletRangesTest.DATA.substring(60), response);
    }

    @Test
    public void testUnsatisfiableRanges() throws Exception {
        String response;
        response = connector.getResponse(("GET /context/data.txt HTTP/1.1\r\n" + ((("Host: localhost\r\n" + "Connection: close\r\n") + "Range: bytes=100-110\r\n") + "\r\n")));
        assertResponseContains("416 Range Not Satisfiable", response);
    }
}

