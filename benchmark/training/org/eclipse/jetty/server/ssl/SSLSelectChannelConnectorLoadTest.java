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
package org.eclipse.jetty.server.ssl;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class SSLSelectChannelConnectorLoadTest {
    private static Server server;

    private static ServerConnector connector;

    private static SSLContext sslContext;

    @Test
    public void testGetURI() {
        URI uri = SSLSelectChannelConnectorLoadTest.server.getURI();
        MatcherAssert.assertThat("Server.uri.scheme", uri.getScheme(), Matchers.is("https"));
        MatcherAssert.assertThat("Server.uri.port", uri.getPort(), Matchers.is(SSLSelectChannelConnectorLoadTest.connector.getLocalPort()));
        MatcherAssert.assertThat("Server.uri.path", uri.getPath(), Matchers.is("/"));
    }

    @Test
    public void testLongLivedConnections() throws Exception {
        SSLSelectChannelConnectorLoadTest.Worker.totalIterations.set(0);
        int mebiByte = 1048510;
        int clients = 1;
        int iterations = 2;
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(clients, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
        threadPool.prestartAllCoreThreads();
        SSLSelectChannelConnectorLoadTest.Worker[] workers = new SSLSelectChannelConnectorLoadTest.Worker[clients];
        Future[] tasks = new Future[clients];
        for (int i = 0; i < clients; ++i) {
            workers[i] = new SSLSelectChannelConnectorLoadTest.Worker(SSLSelectChannelConnectorLoadTest.sslContext, iterations, false, mebiByte, (64 * mebiByte));
            workers[i].open();
            tasks[i] = threadPool.submit(workers[i]);
        }
        long start = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        while (true) {
            Thread.sleep(1000);
            boolean done = true;
            for (Future task : tasks)
                done &= task.isDone();

            // System.err.print("\rIterations: " + Worker.totalIterations.get() + "/" + clients * iterations);
            if (done)
                break;

        } 
        long end = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        // System.err.println();
        // System.err.println("Elapsed time: " + TimeUnit.MILLISECONDS.toSeconds(end - start) + "s");
        for (SSLSelectChannelConnectorLoadTest.Worker worker : workers)
            worker.close();

        threadPool.shutdown();
        // Throw exceptions if any
        for (Future task : tasks)
            task.get();

        // Keep the JVM running
        // new CountDownLatch(1).await();
    }

    @Test
    public void testShortLivedConnections() throws Exception {
        SSLSelectChannelConnectorLoadTest.Worker.totalIterations.set(0);
        int mebiByte = 1048510;
        int clients = 1;
        int iterations = 2;
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(clients, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
        threadPool.prestartAllCoreThreads();
        SSLSelectChannelConnectorLoadTest.Worker[] workers = new SSLSelectChannelConnectorLoadTest.Worker[clients];
        Future[] tasks = new Future[clients];
        for (int i = 0; i < clients; ++i) {
            workers[i] = new SSLSelectChannelConnectorLoadTest.Worker(SSLSelectChannelConnectorLoadTest.sslContext, iterations, true, mebiByte, (64 * mebiByte));
            tasks[i] = threadPool.submit(workers[i]);
        }
        long start = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        while (true) {
            Thread.sleep(1000);
            boolean done = true;
            for (Future task : tasks)
                done &= task.isDone();

            // System.err.print("\rIterations: " + Worker.totalIterations.get() + "/" + clients * iterations);
            if (done)
                break;

        } 
        long end = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        // System.err.println();
        // System.err.println("Elapsed time: " + TimeUnit.MILLISECONDS.toSeconds(end - start) + "s");
        threadPool.shutdown();
        // Throw exceptions if any
        for (Future task : tasks)
            task.get();

        // Keep the JVM running
        // new CountDownLatch(1).await();
    }

    private static class Worker implements Runnable {
        private static final AtomicLong totalIterations = new AtomicLong();

        private final SSLContext sslContext;

        private volatile SSLSocket sslSocket;

        private final int iterations;

        private final boolean closeConnection;

        private final int minContent;

        private final int maxContent;

        public Worker(SSLContext sslContext, int iterations, boolean closeConnection, int minContent, int maxContent) {
            this.sslContext = sslContext;
            this.iterations = iterations;
            this.closeConnection = closeConnection;
            this.minContent = minContent;
            this.maxContent = maxContent;
        }

        public void open() throws IOException {
            this.sslSocket = ((SSLSocket) (sslContext.getSocketFactory().createSocket("localhost", SSLSelectChannelConnectorLoadTest.connector.getLocalPort())));
        }

        public void close() throws IOException {
            sslSocket.close();
        }

        public void run() {
            try {
                Random random = new Random();
                StringBuilder builder = new StringBuilder();
                OutputStream out = null;
                InputStream in = null;
                if (!(closeConnection)) {
                    open();
                    out = sslSocket.getOutputStream();
                    in = sslSocket.getInputStream();
                }
                for (int i = 0; i < (iterations); ++i) {
                    if (closeConnection) {
                        open();
                        out = sslSocket.getOutputStream();
                        in = sslSocket.getInputStream();
                    }
                    int contentSize = (random.nextInt(((maxContent) - (minContent)))) + (minContent);
                    // System.err.println("Writing " + content + " request bytes");
                    out.write("POST / HTTP/1.1\r\n".getBytes());
                    out.write("Host: localhost\r\n".getBytes());
                    out.write((("Content-Length: " + contentSize) + "\r\n").getBytes());
                    out.write("Content-Type: application/octect-stream\r\n".getBytes());
                    if (closeConnection)
                        out.write("Connection: close\r\n".getBytes());

                    out.write("\r\n".getBytes());
                    out.flush();
                    byte[] contentChunk = new byte[minContent];
                    int content = contentSize;
                    while (content > 0) {
                        int chunk = Math.min(content, contentChunk.length);
                        Arrays.fill(contentChunk, 0, chunk, ((byte) ('x')));
                        out.write(contentChunk, 0, chunk);
                        content -= chunk;
                    } 
                    out.flush();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    int responseLength = 0;
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // System.err.println(line);
                        String contentLength = "Content-Length:";
                        if (line.startsWith(contentLength)) {
                            responseLength = Integer.parseInt(line.substring(contentLength.length()).trim());
                        } else
                            if ((line.length()) == 0) {
                                if (responseLength == 0)
                                    line = reader.readLine();

                                break;
                            }

                    } 
                    builder.setLength(0);
                    if (responseLength > 0) {
                        for (int j = 0; j < responseLength; ++j)
                            builder.append(((char) (reader.read())));

                    } else {
                        builder.append(line);
                    }
                    if (closeConnection)
                        close();

                    if (contentSize != (Integer.parseInt(builder.toString())))
                        throw new IllegalStateException();

                    Thread.sleep(random.nextInt(1000));
                    SSLSelectChannelConnectorLoadTest.Worker.totalIterations.incrementAndGet();
                }
                if (!(closeConnection))
                    close();

            } catch (Exception x) {
                throw new RuntimeException(x);
            }
        }
    }

    private static class EmptyHandler extends AbstractHandler {
        public void handle(String path, Request request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException, ServletException {
            request.setHandled(true);
            InputStream in = request.getInputStream();
            int total = 0;
            byte[] b = new byte[1024 * 1024];
            int read;
            while ((read = in.read(b)) >= 0)
                total += read;

            // System.err.println("Read " + total + " request bytes");
            httpResponse.getOutputStream().write(String.valueOf(total).getBytes());
        }
    }
}

