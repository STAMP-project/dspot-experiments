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
package org.eclipse.jetty.servlets;


import HttpServletResponse.SC_CREATED;
import HttpServletResponse.SC_FORBIDDEN;
import HttpServletResponse.SC_NOT_FOUND;
import HttpServletResponse.SC_NO_CONTENT;
import HttpServletResponse.SC_OK;
import HttpTester.Request;
import HttpTester.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.servlet.ServletTester;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.StringUtil;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class PutFilterTest {
    private File _dir;

    private ServletTester tester;

    @Test
    public void testHandlePut() throws Exception {
        // generated and parsed test
        HttpTester.Request request = HttpTester.newRequest();
        HttpTester.Response response;
        // test GET
        request.setMethod("GET");
        request.setVersion("HTTP/1.0");
        request.setHeader("Host", "tester");
        request.setURI("/context/file.txt");
        response = HttpTester.parseResponse(tester.getResponses(request.generate()));
        Assertions.assertEquals(SC_NOT_FOUND, response.getStatus());
        // test PUT0
        request.setMethod("PUT");
        request.setURI("/context/file.txt");
        request.setHeader("Content-Type", "text/plain");
        String data0 = "Now is the time for all good men to come to the aid of the party";
        request.setContent(data0);
        response = HttpTester.parseResponse(tester.getResponses(request.generate()));
        Assertions.assertEquals(SC_CREATED, response.getStatus());
        File file = new File(_dir, "file.txt");
        Assertions.assertTrue(file.exists());
        Assertions.assertEquals(data0, IO.toString(new FileInputStream(file)));
        // test GET1
        request.setMethod("GET");
        request.setVersion("HTTP/1.0");
        request.setHeader("Host", "tester");
        request.setURI("/context/file.txt");
        response = HttpTester.parseResponse(tester.getResponses(request.generate()));
        Assertions.assertEquals(SC_OK, response.getStatus());
        Assertions.assertEquals(data0, response.getContent());
        // test PUT1
        request.setMethod("PUT");
        request.setURI("/context/file.txt");
        request.setHeader("Content-Type", "text/plain");
        String data1 = "How Now BROWN COW!!!!";
        request.setContent(data1);
        response = HttpTester.parseResponse(tester.getResponses(request.generate()));
        Assertions.assertEquals(SC_OK, response.getStatus());
        file = new File(_dir, "file.txt");
        Assertions.assertTrue(file.exists());
        Assertions.assertEquals(data1, IO.toString(new FileInputStream(file)));
        // test PUT2
        request.setMethod("PUT");
        request.setURI("/context/file.txt");
        request.setHeader("Content-Type", "text/plain");
        String data2 = "Blah blah blah Blah blah";
        request.setContent(data2);
        String to_send = BufferUtil.toString(request.generate());
        URL url = new URL(tester.createConnector(true));
        Socket socket = new Socket(url.getHost(), url.getPort());
        OutputStream out = socket.getOutputStream();
        int l = to_send.length();
        out.write(to_send.substring(0, (l - 10)).getBytes());
        out.flush();
        Thread.sleep(100);
        out.write(to_send.substring((l - 10), (l - 5)).getBytes());
        out.flush();
        // loop until the resource is hidden (ie the PUT is starting to
        // read the file
        do {
            Thread.sleep(100);
            // test GET
            request.setMethod("GET");
            request.setVersion("HTTP/1.0");
            request.setHeader("Host", "tester");
            request.setURI("/context/file.txt");
            response = HttpTester.parseResponse(tester.getResponses(request.generate()));
        } while ((response.getStatus()) == 200 );
        Assertions.assertEquals(SC_NOT_FOUND, response.getStatus());
        out.write(to_send.substring((l - 5)).getBytes());
        out.flush();
        String in = IO.toString(socket.getInputStream());
        request.setMethod("GET");
        request.setVersion("HTTP/1.0");
        request.setHeader("Host", "tester");
        request.setURI("/context/file.txt");
        response = HttpTester.parseResponse(tester.getResponses(request.generate()));
        Assertions.assertEquals(SC_OK, response.getStatus());
        Assertions.assertEquals(data2, response.getContent());
    }

    @Test
    public void testHandleDelete() throws Exception {
        // generated and parsed test
        HttpTester.Request request = HttpTester.newRequest();
        HttpTester.Response response;
        // test PUT1
        request.setMethod("PUT");
        request.setVersion("HTTP/1.0");
        request.setHeader("Host", "tester");
        request.setURI("/context/file.txt");
        request.setHeader("Content-Type", "text/plain");
        String data1 = "How Now BROWN COW!!!!";
        request.setContent(data1);
        response = HttpTester.parseResponse(tester.getResponses(request.generate()));
        Assertions.assertEquals(SC_CREATED, response.getStatus());
        File file = new File(_dir, "file.txt");
        Assertions.assertTrue(file.exists());
        try (InputStream fis = new FileInputStream(file)) {
            Assertions.assertEquals(data1, IO.toString(fis));
        }
        request.setMethod("DELETE");
        request.setURI("/context/file.txt");
        response = HttpTester.parseResponse(tester.getResponses(request.generate()));
        Assertions.assertEquals(SC_NO_CONTENT, response.getStatus());
        Assertions.assertTrue((!(file.exists())));
        request.setMethod("DELETE");
        request.setURI("/context/file.txt");
        response = HttpTester.parseResponse(tester.getResponses(request.generate()));
        Assertions.assertEquals(SC_FORBIDDEN, response.getStatus());
    }

    @Test
    public void testHandleMove() throws Exception {
        // generated and parsed test
        HttpTester.Request request = HttpTester.newRequest();
        HttpTester.Response response;
        // test PUT1
        request.setMethod("PUT");
        request.setVersion("HTTP/1.0");
        request.setHeader("Host", "tester");
        request.setURI("/context/file.txt");
        request.setHeader("Content-Type", "text/plain");
        String data1 = "How Now BROWN COW!!!!";
        request.setContent(data1);
        response = HttpTester.parseResponse(tester.getResponses(request.generate()));
        Assertions.assertEquals(SC_CREATED, response.getStatus());
        File file = new File(_dir, "file.txt");
        Assertions.assertTrue(file.exists());
        try (InputStream fis = new FileInputStream(file)) {
            Assertions.assertEquals(data1, IO.toString(fis));
        }
        request.setMethod("MOVE");
        request.setURI("/context/file.txt");
        request.setHeader("new-uri", "/context/blah.txt");
        response = HttpTester.parseResponse(tester.getResponses(request.generate()));
        Assertions.assertEquals(SC_NO_CONTENT, response.getStatus());
        Assertions.assertTrue((!(file.exists())));
        File n_file = new File(_dir, "blah.txt");
        Assertions.assertTrue(n_file.exists());
    }

    @Test
    public void testHandleOptions() throws Exception {
        // generated and parsed test
        HttpTester.Request request = HttpTester.newRequest();
        HttpTester.Response response;
        // test PUT1
        request.setMethod("OPTIONS");
        request.setVersion("HTTP/1.0");
        request.put("Host", "tester");
        request.setURI("/context/file.txt");
        response = HttpTester.parseResponse(tester.getResponses(request.generate()));
        Assertions.assertEquals(SC_OK, response.getStatus());
        Set<String> options = new HashSet<String>();
        String allow = response.get("Allow");
        options.addAll(StringUtil.csvSplit(null, allow, 0, allow.length()));
        MatcherAssert.assertThat("GET", Matchers.isIn(options));
        MatcherAssert.assertThat("POST", Matchers.isIn(options));
        MatcherAssert.assertThat("PUT", Matchers.isIn(options));
        MatcherAssert.assertThat("MOVE", Matchers.isIn(options));
    }

    @Test
    public void testPassConditionalHeaders() {
        // TODO implement
    }
}

