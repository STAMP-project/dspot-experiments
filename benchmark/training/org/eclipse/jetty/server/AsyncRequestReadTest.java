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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.IO;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class AsyncRequestReadTest {
    private static Server server;

    private static ServerConnector connector;

    private static final BlockingQueue<Long> __total = new org.eclipse.jetty.util.BlockingArrayQueue();

    @Test
    public void testPipelined() throws Exception {
        AsyncRequestReadTest.server.setHandler(new AsyncRequestReadTest.AsyncStreamHandler());
        AsyncRequestReadTest.server.start();
        try (final Socket socket = new Socket("localhost", AsyncRequestReadTest.connector.getLocalPort())) {
            socket.setSoTimeout(1000);
            byte[] content = new byte[32 * 4096];
            Arrays.fill(content, ((byte) (120)));
            OutputStream out = socket.getOutputStream();
            String header = (((("POST / HTTP/1.1\r\n" + ("Host: localhost\r\n" + "Content-Length: ")) + (content.length)) + "\r\n") + "Content-Type: bytes\r\n") + "\r\n";
            byte[] h = header.getBytes(StandardCharsets.ISO_8859_1);
            out.write(h);
            out.write(content);
            header = ((((("POST / HTTP/1.1\r\n" + ("Host: localhost\r\n" + "Content-Length: ")) + (content.length)) + "\r\n") + "Content-Type: bytes\r\n") + "Connection: close\r\n") + "\r\n";
            h = header.getBytes(StandardCharsets.ISO_8859_1);
            out.write(h);
            out.write(content);
            out.flush();
            InputStream in = socket.getInputStream();
            String response = IO.toString(in);
            Assertions.assertTrue(((response.indexOf("200 OK")) > 0));
            long total = AsyncRequestReadTest.__total.poll(5, TimeUnit.SECONDS);
            Assertions.assertEquals(content.length, total);
            total = AsyncRequestReadTest.__total.poll(5, TimeUnit.SECONDS);
            Assertions.assertEquals(content.length, total);
        }
    }

    @Test
    public void testAsyncReadsWithDelays() throws Exception {
        AsyncRequestReadTest.server.setHandler(new AsyncRequestReadTest.AsyncStreamHandler());
        AsyncRequestReadTest.server.start();
        asyncReadTest(64, 4, 4, 20);
        asyncReadTest(256, 16, 16, 50);
        asyncReadTest(256, 1, 128, 10);
        asyncReadTest((128 * 1024), 1, 64, 10);
        asyncReadTest((256 * 1024), 5321, 10, 100);
        asyncReadTest((512 * 1024), (32 * 1024), 10, 10);
    }

    private static class AsyncStreamHandler extends AbstractHandler {
        @Override
        public void handle(String path, final Request request, HttpServletRequest httpRequest, final HttpServletResponse httpResponse) throws IOException, ServletException {
            httpResponse.setStatus(500);
            request.setHandled(true);
            final AsyncContext async = request.startAsync();
            // System.err.println("handle "+request.getContentLength());
            new Thread() {
                @Override
                public void run() {
                    long total = 0;
                    try (InputStream in = request.getInputStream()) {
                        // System.err.println("reading...");
                        byte[] b = new byte[4 * 4096];
                        int read;
                        while ((read = in.read(b)) >= 0)
                            total += read;

                    } catch (Exception e) {
                        e.printStackTrace();
                        total = -1;
                    } finally {
                        httpResponse.setStatus(200);
                        async.complete();
                        // System.err.println("read "+total);
                        AsyncRequestReadTest.__total.offer(total);
                    }
                }
            }.start();
        }
    }

    @Test
    public void testPartialRead() throws Exception {
        AsyncRequestReadTest.server.setHandler(new AsyncRequestReadTest.PartialReaderHandler());
        AsyncRequestReadTest.server.start();
        try (final Socket socket = new Socket("localhost", AsyncRequestReadTest.connector.getLocalPort())) {
            socket.setSoTimeout(10000);
            byte[] content = new byte[32 * 4096];
            Arrays.fill(content, ((byte) (88)));
            OutputStream out = socket.getOutputStream();
            String header = (((("POST /?read=10 HTTP/1.1\r\n" + ("Host: localhost\r\n" + "Content-Length: ")) + (content.length)) + "\r\n") + "Content-Type: bytes\r\n") + "\r\n";
            byte[] h = header.getBytes(StandardCharsets.ISO_8859_1);
            out.write(h);
            out.write(content);
            header = ((((("POST /?read=10 HTTP/1.1\r\n" + ("Host: localhost\r\n" + "Content-Length: ")) + (content.length)) + "\r\n") + "Content-Type: bytes\r\n") + "Connection: close\r\n") + "\r\n";
            h = header.getBytes(StandardCharsets.ISO_8859_1);
            out.write(h);
            out.write(content);
            out.flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            MatcherAssert.assertThat(in.readLine(), Matchers.containsString("HTTP/1.1 200 OK"));
            MatcherAssert.assertThat(in.readLine(), Matchers.containsString("Content-Length: 11"));
            MatcherAssert.assertThat(in.readLine(), Matchers.containsString("Server:"));
            in.readLine();
            MatcherAssert.assertThat(in.readLine(), Matchers.containsString("XXXXXXX"));
            MatcherAssert.assertThat(in.readLine(), Matchers.containsString("HTTP/1.1 200 OK"));
            MatcherAssert.assertThat(in.readLine(), Matchers.containsString("Connection: close"));
            MatcherAssert.assertThat(in.readLine(), Matchers.containsString("Content-Length: 11"));
            MatcherAssert.assertThat(in.readLine(), Matchers.containsString("Server:"));
            in.readLine();
            MatcherAssert.assertThat(in.readLine(), Matchers.containsString("XXXXXXX"));
        }
    }

    @Test
    public void testPartialReadThenShutdown() throws Exception {
        AsyncRequestReadTest.server.setHandler(new AsyncRequestReadTest.PartialReaderHandler());
        AsyncRequestReadTest.server.start();
        try (final Socket socket = new Socket("localhost", AsyncRequestReadTest.connector.getLocalPort())) {
            socket.setSoTimeout(10000);
            byte[] content = new byte[32 * 4096];
            Arrays.fill(content, ((byte) (88)));
            OutputStream out = socket.getOutputStream();
            String header = (((("POST /?read=10 HTTP/1.1\r\n" + ("Host: localhost\r\n" + "Content-Length: ")) + (content.length)) + "\r\n") + "Content-Type: bytes\r\n") + "\r\n";
            byte[] h = header.getBytes(StandardCharsets.ISO_8859_1);
            out.write(h);
            out.write(content, 0, 4096);
            out.flush();
            socket.shutdownOutput();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            MatcherAssert.assertThat(in.readLine(), Matchers.containsString("HTTP/1.1 200 OK"));
            MatcherAssert.assertThat(in.readLine(), Matchers.containsString("Content-Length:"));
            MatcherAssert.assertThat(in.readLine(), Matchers.containsString("Connection: close"));
            MatcherAssert.assertThat(in.readLine(), Matchers.containsString("Server:"));
            in.readLine();
            MatcherAssert.assertThat(in.readLine(), Matchers.containsString("XXXXXXX"));
        }
    }

    @Test
    public void testPartialReadThenClose() throws Exception {
        AsyncRequestReadTest.server.setHandler(new AsyncRequestReadTest.PartialReaderHandler());
        AsyncRequestReadTest.server.start();
        try (final Socket socket = new Socket("localhost", AsyncRequestReadTest.connector.getLocalPort())) {
            socket.setSoTimeout(1000);
            byte[] content = new byte[32 * 4096];
            Arrays.fill(content, ((byte) (88)));
            OutputStream out = socket.getOutputStream();
            String header = (((("POST /?read=10 HTTP/1.1\r\n" + ("Host: localhost\r\n" + "Content-Length: ")) + (content.length)) + "\r\n") + "Content-Type: bytes\r\n") + "\r\n";
            byte[] h = header.getBytes(StandardCharsets.ISO_8859_1);
            out.write(h);
            out.write(content, 0, 4096);
            out.flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            MatcherAssert.assertThat(in.readLine(), Matchers.containsString("HTTP/1.1 200 OK"));
            MatcherAssert.assertThat(in.readLine(), Matchers.containsString("Content-Length:"));
            MatcherAssert.assertThat(in.readLine(), Matchers.containsString("Server:"));
            in.readLine();
            MatcherAssert.assertThat(in.readLine(), Matchers.containsString("XXXXXXX"));
            socket.close();
        }
    }

    private static class PartialReaderHandler extends AbstractHandler {
        @Override
        public void handle(String path, final Request request, HttpServletRequest httpRequest, final HttpServletResponse httpResponse) throws IOException, ServletException {
            httpResponse.setStatus(200);
            request.setHandled(true);
            BufferedReader in = request.getReader();
            PrintWriter out = httpResponse.getWriter();
            int read = Integer.parseInt(request.getParameter("read"));
            // System.err.println("read="+read);
            for (int i = read; (i--) > 0;) {
                int c = in.read();
                // System.err.println("in="+c);
                if (c < 0)
                    break;

                out.write(c);
            }
            out.write('\n');
        }
    }
}

