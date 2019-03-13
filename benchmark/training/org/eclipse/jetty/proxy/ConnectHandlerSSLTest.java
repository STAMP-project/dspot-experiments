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
package org.eclipse.jetty.proxy;


import HttpStatus.OK_200;
import HttpTester.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.SSLSocket;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ConnectHandlerSSLTest extends AbstractConnectHandlerTest {
    private SslContextFactory sslContextFactory;

    @Test
    public void testGETRequest() throws Exception {
        String hostPort = "localhost:" + (serverConnector.getLocalPort());
        String request = (((((("" + "CONNECT ") + hostPort) + " HTTP/1.1\r\n") + "Host: ") + hostPort) + "\r\n") + "\r\n";
        try (Socket socket = newSocket()) {
            OutputStream output = socket.getOutputStream();
            output.write(request.getBytes(StandardCharsets.UTF_8));
            output.flush();
            // Expect 200 OK from the CONNECT request
            HttpTester.Response response = HttpTester.parseResponse(HttpTester.from(socket.getInputStream()));
            Assertions.assertEquals(OK_200, response.getStatus());
            // Upgrade the socket to SSL
            try (SSLSocket sslSocket = wrapSocket(socket)) {
                output = sslSocket.getOutputStream();
                request = ((("GET /echo HTTP/1.1\r\n" + "Host: ") + hostPort) + "\r\n") + "\r\n";
                output.write(request.getBytes(StandardCharsets.UTF_8));
                output.flush();
                response = HttpTester.parseResponse(HttpTester.from(sslSocket.getInputStream()));
                Assertions.assertEquals(OK_200, response.getStatus());
                Assertions.assertEquals("GET /echo", response.getContent());
            }
        }
    }

    @Test
    public void testPOSTRequests() throws Exception {
        String hostPort = "localhost:" + (serverConnector.getLocalPort());
        String request = (((((("" + "CONNECT ") + hostPort) + " HTTP/1.1\r\n") + "Host: ") + hostPort) + "\r\n") + "\r\n";
        try (Socket socket = newSocket()) {
            OutputStream output = socket.getOutputStream();
            output.write(request.getBytes(StandardCharsets.UTF_8));
            output.flush();
            // Expect 200 OK from the CONNECT request
            HttpTester.Response response = HttpTester.parseResponse(HttpTester.from(socket.getInputStream()));
            Assertions.assertEquals(OK_200, response.getStatus());
            // Upgrade the socket to SSL
            try (SSLSocket sslSocket = wrapSocket(socket)) {
                output = sslSocket.getOutputStream();
                for (int i = 0; i < 10; ++i) {
                    request = (((((((("" + "POST /echo?param=") + i) + " HTTP/1.1\r\n") + "Host: ") + hostPort) + "\r\n") + "Content-Length: 5\r\n") + "\r\n") + "HELLO";
                    output.write(request.getBytes(StandardCharsets.UTF_8));
                    output.flush();
                    response = HttpTester.parseResponse(HttpTester.from(sslSocket.getInputStream()));
                    Assertions.assertEquals(OK_200, response.getStatus());
                    Assertions.assertEquals((("POST /echo?param=" + i) + "\r\nHELLO"), response.getContent());
                }
            }
        }
    }

    private static class ServerHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException, ServletException {
            request.setHandled(true);
            String uri = httpRequest.getRequestURI();
            if ("/echo".equals(uri)) {
                StringBuilder builder = new StringBuilder();
                builder.append(httpRequest.getMethod()).append(" ").append(uri);
                if ((httpRequest.getQueryString()) != null)
                    builder.append("?").append(httpRequest.getQueryString());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                InputStream input = httpRequest.getInputStream();
                int read;
                while ((read = input.read()) >= 0)
                    baos.write(read);

                baos.close();
                byte[] bytes = baos.toByteArray();
                ServletOutputStream output = httpResponse.getOutputStream();
                if ((bytes.length) == 0)
                    output.print(builder.toString());
                else
                    output.println(builder.toString());

                output.write(bytes);
            } else {
                throw new ServletException();
            }
        }
    }
}

