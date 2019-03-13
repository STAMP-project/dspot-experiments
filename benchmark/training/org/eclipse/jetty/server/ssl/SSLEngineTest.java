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
 * JettyTest.java --
 */
/**
 *
 */
/**
 * Junit test that shows the Jetty SSL bug.
 */
/**
 *
 */
package org.eclipse.jetty.server.ssl;


import SslContextFactory.TRUST_ALL_CERTS;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.security.SecureRandom;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.IO;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 */
public class SSLEngineTest {
    // Useful constants
    private static final String HELLO_WORLD = "Hello world. The quick brown fox jumped over the lazy dog. How now brown cow. The rain in spain falls mainly on the plain.\n";

    private static final String JETTY_VERSION = Server.getVersion();

    private static final String PROTOCOL_VERSION = "2.0";

    /**
     * The request.
     */
    private static final String REQUEST0_HEADER = "POST /r0 HTTP/1.1\n" + (("Host: localhost\n" + "Content-Type: text/xml\n") + "Content-Length: ");

    private static final String REQUEST1_HEADER = "POST /r1 HTTP/1.1\n" + ((("Host: localhost\n" + "Content-Type: text/xml\n") + "Connection: close\n") + "Content-Length: ");

    private static final String REQUEST_CONTENT = ((("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<requests xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + "        xsi:noNamespaceSchemaLocation=\"commander.xsd\" version=\"")) + (SSLEngineTest.PROTOCOL_VERSION)) + "\">\n") + "</requests>";

    private static final String REQUEST0 = (((SSLEngineTest.REQUEST0_HEADER) + (SSLEngineTest.REQUEST_CONTENT.getBytes().length)) + "\n\n") + (SSLEngineTest.REQUEST_CONTENT);

    private static final String REQUEST1 = (((SSLEngineTest.REQUEST1_HEADER) + (SSLEngineTest.REQUEST_CONTENT.getBytes().length)) + "\n\n") + (SSLEngineTest.REQUEST_CONTENT);

    /**
     * The expected response.
     */
    private static final String RESPONSE0 = ((((((("HTTP/1.1 200 OK\n" + "Content-Length: ") + (SSLEngineTest.HELLO_WORLD.length())) + "\n") + "Server: Jetty(") + (SSLEngineTest.JETTY_VERSION)) + ")\n") + '\n') + (SSLEngineTest.HELLO_WORLD);

    private static final String RESPONSE1 = ((((((("HTTP/1.1 200 OK\n" + ("Connection: close\n" + "Content-Length: ")) + (SSLEngineTest.HELLO_WORLD.length())) + "\n") + "Server: Jetty(") + (SSLEngineTest.JETTY_VERSION)) + ")\n") + '\n') + (SSLEngineTest.HELLO_WORLD);

    private static final int BODY_SIZE = 300;

    private Server server;

    private ServerConnector connector;

    @Test
    public void testHelloWorld() throws Exception {
        server.setHandler(new SSLEngineTest.HelloWorldHandler());
        server.start();
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, TRUST_ALL_CERTS, new SecureRandom());
        int port = connector.getLocalPort();
        Socket client = ctx.getSocketFactory().createSocket("localhost", port);
        OutputStream os = client.getOutputStream();
        String request = (((("GET / HTTP/1.1\r\n" + "Host: localhost:") + port) + "\r\n") + "Connection: close\r\n") + "\r\n";
        os.write(request.getBytes());
        os.flush();
        String response = IO.toString(client.getInputStream());
        MatcherAssert.assertThat(response, Matchers.containsString("200 OK"));
        MatcherAssert.assertThat(response, Matchers.containsString(SSLEngineTest.HELLO_WORLD));
    }

    @Test
    public void testBigResponse() throws Exception {
        server.setHandler(new SSLEngineTest.HelloWorldHandler());
        server.start();
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, TRUST_ALL_CERTS, new SecureRandom());
        int port = connector.getLocalPort();
        Socket client = ctx.getSocketFactory().createSocket("localhost", port);
        OutputStream os = client.getOutputStream();
        String request = (((("GET /?dump=102400 HTTP/1.1\r\n" + "Host: localhost:") + port) + "\r\n") + "Connection: close\r\n") + "\r\n";
        os.write(request.getBytes());
        os.flush();
        String response = IO.toString(client.getInputStream());
        MatcherAssert.assertThat(response.length(), Matchers.greaterThan(102400));
    }

    @Test
    public void testRequestJettyHttps() throws Exception {
        server.setHandler(new SSLEngineTest.HelloWorldHandler());
        server.start();
        final int loops = 10;
        final int numConns = 20;
        Socket[] client = new Socket[numConns];
        SSLContext ctx = SSLContext.getInstance("TLSv1.2");
        ctx.init(null, TRUST_ALL_CERTS, new SecureRandom());
        int port = connector.getLocalPort();
        try {
            for (int l = 0; l < loops; l++) {
                // System.err.print('.');
                try {
                    for (int i = 0; i < numConns; ++i) {
                        // System.err.println("write:"+i);
                        client[i] = ctx.getSocketFactory().createSocket("localhost", port);
                        OutputStream os = client[i].getOutputStream();
                        os.write(SSLEngineTest.REQUEST0.getBytes());
                        os.write(SSLEngineTest.REQUEST0.getBytes());
                        os.flush();
                    }
                    for (int i = 0; i < numConns; ++i) {
                        // System.err.println("flush:"+i);
                        OutputStream os = client[i].getOutputStream();
                        os.write(SSLEngineTest.REQUEST1.getBytes());
                        os.flush();
                    }
                    for (int i = 0; i < numConns; ++i) {
                        // System.err.println("read:"+i);
                        // Read the response.
                        String responses = SSLEngineTest.readResponse(client[i]);
                        // Check the responses
                        MatcherAssert.assertThat(String.format("responses loop=%d connection=%d", l, i), (((SSLEngineTest.RESPONSE0) + (SSLEngineTest.RESPONSE0)) + (SSLEngineTest.RESPONSE1)), Matchers.is(responses));
                    }
                } finally {
                    for (int i = 0; i < numConns; ++i) {
                        if ((client[i]) != null) {
                            try {
                                MatcherAssert.assertThat("Client should read EOF", client[i].getInputStream().read(), Matchers.is((-1)));
                            } catch (SocketException e) {
                            }
                        }
                    }
                }
            }
        } finally {
            // System.err.println();
        }
    }

    @Test
    public void testURLConnectionChunkedPost() throws Exception {
        SSLEngineTest.StreamHandler handler = new SSLEngineTest.StreamHandler();
        server.setHandler(handler);
        server.start();
        SSLContext context = SSLContext.getInstance("SSL");
        context.init(null, TRUST_ALL_CERTS, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        URL url = new URL((("https://localhost:" + (connector.getLocalPort())) + "/test"));
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        if (conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection) (conn)).setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String urlHostName, SSLSession session) {
                    return true;
                }
            });
        }
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(100000);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/plain");
        conn.setChunkedStreamingMode(128);
        conn.connect();
        byte[] b = new byte[SSLEngineTest.BODY_SIZE];
        for (int i = 0; i < (SSLEngineTest.BODY_SIZE); i++) {
            b[i] = 'x';
        }
        OutputStream os = conn.getOutputStream();
        os.write(b);
        os.flush();
        int len = 0;
        InputStream is = conn.getInputStream();
        int bytes = 0;
        while ((len = is.read(b)) > (-1))
            bytes += len;

        is.close();
        Assertions.assertEquals(SSLEngineTest.BODY_SIZE, handler.bytes);
        Assertions.assertEquals(SSLEngineTest.BODY_SIZE, bytes);
    }

    private static class HelloWorldHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            // System.err.println("HANDLE "+request.getRequestURI());
            String ssl_id = ((String) (request.getAttribute("javax.servlet.request.ssl_session_id")));
            Assertions.assertNotNull(ssl_id);
            if ((request.getParameter("dump")) != null) {
                ServletOutputStream out = response.getOutputStream();
                byte[] buf = new byte[Integer.parseInt(request.getParameter("dump"))];
                // System.err.println("DUMP "+buf.length);
                for (int i = 0; i < (buf.length); i++)
                    buf[i] = ((byte) ('0' + (i % 10)));

                out.write(buf);
                out.close();
            } else {
                PrintWriter out = response.getWriter();
                out.print(SSLEngineTest.HELLO_WORLD);
                out.close();
            }
        }
    }

    private static class StreamHandler extends AbstractHandler {
        private int bytes = 0;

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.setContentType("text/plain");
            response.setBufferSize(128);
            byte[] b = new byte[SSLEngineTest.BODY_SIZE];
            int len = 0;
            InputStream is = request.getInputStream();
            while ((len = is.read(b)) > (-1)) {
                bytes += len;
            } 
            OutputStream os = response.getOutputStream();
            for (int i = 0; i < (SSLEngineTest.BODY_SIZE); i++) {
                b[i] = 'x';
            }
            os.write(b);
            response.flushBuffer();
        }
    }
}

