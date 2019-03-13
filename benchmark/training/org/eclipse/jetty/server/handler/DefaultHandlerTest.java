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
package org.eclipse.jetty.server.handler;


import HttpHeader.CONTENT_TYPE;
import HttpStatus.NOT_FOUND_404;
import HttpStatus.OK_200;
import HttpTester.Input;
import HttpTester.Response;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class DefaultHandlerTest {
    private Server server;

    private ServerConnector connector;

    private DefaultHandler handler;

    @Test
    public void testRoot() throws Exception {
        try (Socket socket = new Socket("localhost", connector.getLocalPort())) {
            String request = "" + (("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "\r\n");
            OutputStream output = socket.getOutputStream();
            output.write(request.getBytes(StandardCharsets.UTF_8));
            output.flush();
            HttpTester.Input input = HttpTester.from(socket.getInputStream());
            HttpTester.Response response = HttpTester.parseResponse(input);
            Assertions.assertEquals(NOT_FOUND_404, response.getStatus());
            Assertions.assertEquals("text/html;charset=ISO-8859-1", response.get(CONTENT_TYPE));
            String content = new String(response.getContentBytes(), StandardCharsets.ISO_8859_1);
            MatcherAssert.assertThat(content, Matchers.containsString("Contexts known to this server are:"));
            MatcherAssert.assertThat(content, Matchers.containsString("/foo"));
            MatcherAssert.assertThat(content, Matchers.containsString("/bar"));
        }
    }

    @Test
    public void testSomePath() throws Exception {
        try (Socket socket = new Socket("localhost", connector.getLocalPort())) {
            String request = "" + (("GET /some/path HTTP/1.1\r\n" + "Host: localhost\r\n") + "\r\n");
            OutputStream output = socket.getOutputStream();
            output.write(request.getBytes(StandardCharsets.UTF_8));
            output.flush();
            HttpTester.Input input = HttpTester.from(socket.getInputStream());
            HttpTester.Response response = HttpTester.parseResponse(input);
            Assertions.assertEquals(NOT_FOUND_404, response.getStatus());
            Assertions.assertEquals("text/html;charset=ISO-8859-1", response.get(CONTENT_TYPE));
            String content = new String(response.getContentBytes(), StandardCharsets.ISO_8859_1);
            MatcherAssert.assertThat(content, Matchers.not(Matchers.containsString("Contexts known to this server are:")));
            MatcherAssert.assertThat(content, Matchers.not(Matchers.containsString("/foo")));
            MatcherAssert.assertThat(content, Matchers.not(Matchers.containsString("/bar")));
        }
    }

    @Test
    public void testFavIcon() throws Exception {
        try (Socket socket = new Socket("localhost", connector.getLocalPort())) {
            String request = "" + (("GET /favicon.ico HTTP/1.1\r\n" + "Host: localhost\r\n") + "\r\n");
            OutputStream output = socket.getOutputStream();
            output.write(request.getBytes(StandardCharsets.UTF_8));
            output.flush();
            HttpTester.Input input = HttpTester.from(socket.getInputStream());
            HttpTester.Response response = HttpTester.parseResponse(input);
            Assertions.assertEquals(OK_200, response.getStatus());
            Assertions.assertEquals("image/x-icon", response.get(CONTENT_TYPE));
        }
    }
}

