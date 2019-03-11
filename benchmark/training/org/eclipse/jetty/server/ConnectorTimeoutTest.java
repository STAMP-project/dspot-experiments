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


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.ssl.SslConnection;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public abstract class ConnectorTimeoutTest extends HttpServerTestFixture {
    protected static final Logger LOG = Log.getLogger(ConnectorTimeoutTest.class);

    protected static final int MAX_IDLE_TIME = 2000;

    private int sleepTime = (ConnectorTimeoutTest.MAX_IDLE_TIME) + ((ConnectorTimeoutTest.MAX_IDLE_TIME) / 5);

    private int minimumTestRuntime = (ConnectorTimeoutTest.MAX_IDLE_TIME) - ((ConnectorTimeoutTest.MAX_IDLE_TIME) / 5);

    private int maximumTestRuntime = (ConnectorTimeoutTest.MAX_IDLE_TIME) * 10;

    static {
        System.setProperty("org.eclipse.jetty.io.nio.IDLE_TICK", "500");
    }

    @Test
    public void testMaxIdleWithRequest10() throws Exception {
        configureServer(new HttpServerTestFixture.HelloWorldHandler());
        Socket client = newSocket(_serverURI.getHost(), _serverURI.getPort());
        client.setSoTimeout(10000);
        Assertions.assertFalse(client.isClosed());
        OutputStream os = client.getOutputStream();
        InputStream is = client.getInputStream();
        long start = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            os.write(((((((("GET / HTTP/1.0\r\n" + "host: ") + (_serverURI.getHost())) + ":") + (_serverURI.getPort())) + "\r\n") + "connection: keep-alive\r\n") + "\r\n").getBytes("utf-8"));
            os.flush();
            IO.toString(is);
            Thread.sleep(sleepTime);
            Assertions.assertEquals((-1), is.read());
        });
        Assertions.assertTrue((((TimeUnit.NANOSECONDS.toMillis(System.nanoTime())) - start) > (minimumTestRuntime)));
        Assertions.assertTrue((((TimeUnit.NANOSECONDS.toMillis(System.nanoTime())) - start) < (maximumTestRuntime)));
    }

    @Test
    public void testMaxIdleWithRequest11() throws Exception {
        configureServer(new HttpServerTestFixture.EchoHandler());
        Socket client = newSocket(_serverURI.getHost(), _serverURI.getPort());
        client.setSoTimeout(10000);
        Assertions.assertFalse(client.isClosed());
        OutputStream os = client.getOutputStream();
        InputStream is = client.getInputStream();
        long start = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            String content = "Wibble";
            byte[] contentB = content.getBytes("utf-8");
            os.write((((((((((("POST /echo HTTP/1.1\r\n" + "host: ") + (_serverURI.getHost())) + ":") + (_serverURI.getPort())) + "\r\n") + "content-type: text/plain; charset=utf-8\r\n") + "content-length: ") + (contentB.length)) + "\r\n") + "\r\n").getBytes("utf-8"));
            os.write(contentB);
            os.flush();
            IO.toString(is);
            Thread.sleep(sleepTime);
            Assertions.assertEquals((-1), is.read());
        });
        Assertions.assertTrue((((TimeUnit.NANOSECONDS.toMillis(System.nanoTime())) - start) > (minimumTestRuntime)));
        Assertions.assertTrue((((TimeUnit.NANOSECONDS.toMillis(System.nanoTime())) - start) < (maximumTestRuntime)));
    }

    @Test
    public void testMaxIdleWithRequest10NoClientClose() throws Exception {
        final Exchanger<EndPoint> exchanger = new Exchanger<>();
        configureServer(new HttpServerTestFixture.HelloWorldHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                try {
                    exchanger.exchange(baseRequest.getHttpChannel().getEndPoint());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.handle(target, baseRequest, request, response);
            }
        });
        Socket client = newSocket(_serverURI.getHost(), _serverURI.getPort());
        client.setSoTimeout(10000);
        Assertions.assertFalse(client.isClosed());
        OutputStream os = client.getOutputStream();
        InputStream is = client.getInputStream();
        os.write(((((((("GET / HTTP/1.0\r\n" + "host: ") + (_serverURI.getHost())) + ":") + (_serverURI.getPort())) + "\r\n") + "connection: close\r\n") + "\r\n").getBytes("utf-8"));
        os.flush();
        // Get the server side endpoint
        EndPoint endPoint = exchanger.exchange(null, 10, TimeUnit.SECONDS);
        if (endPoint instanceof SslConnection.DecryptedEndPoint)
            endPoint = getSslConnection().getEndPoint();

        // read the response
        String result = IO.toString(is);
        MatcherAssert.assertThat("OK", result, Matchers.containsString("200 OK"));
        // check client reads EOF
        Assertions.assertEquals((-1), is.read());
        Assertions.assertTrue(endPoint.isOutputShutdown());
        // wait for idle timeout
        TimeUnit.MILLISECONDS.sleep((2 * (ConnectorTimeoutTest.MAX_IDLE_TIME)));
        // check the server side is closed
        Assertions.assertFalse(endPoint.isOpen());
        Object transport = endPoint.getTransport();
        if (transport instanceof Channel)
            Assertions.assertFalse(((Channel) (transport)).isOpen());

    }

    @Test
    public void testMaxIdleWithRequest11NoClientClose() throws Exception {
        final Exchanger<EndPoint> exchanger = new Exchanger<>();
        configureServer(new HttpServerTestFixture.EchoHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                try {
                    exchanger.exchange(baseRequest.getHttpChannel().getEndPoint());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.handle(target, baseRequest, request, response);
            }
        });
        Socket client = newSocket(_serverURI.getHost(), _serverURI.getPort());
        client.setSoTimeout(10000);
        Assertions.assertFalse(client.isClosed());
        OutputStream os = client.getOutputStream();
        InputStream is = client.getInputStream();
        String content = "Wibble";
        byte[] contentB = content.getBytes("utf-8");
        os.write(((((((((((("POST /echo HTTP/1.1\r\n" + "host: ") + (_serverURI.getHost())) + ":") + (_serverURI.getPort())) + "\r\n") + "content-type: text/plain; charset=utf-8\r\n") + "content-length: ") + (contentB.length)) + "\r\n") + "connection: close\r\n") + "\r\n").getBytes("utf-8"));
        os.write(contentB);
        os.flush();
        // Get the server side endpoint
        EndPoint endPoint = exchanger.exchange(null, 10, TimeUnit.SECONDS);
        if (endPoint instanceof SslConnection.DecryptedEndPoint)
            endPoint = getSslConnection().getEndPoint();

        // read the response
        IO.toString(is);
        // check client reads EOF
        Assertions.assertEquals((-1), is.read());
        Assertions.assertTrue(endPoint.isOutputShutdown());
        // The server has shutdown the output, the client does not close,
        // the server should idle timeout and close the connection.
        TimeUnit.MILLISECONDS.sleep((2 * (ConnectorTimeoutTest.MAX_IDLE_TIME)));
        Assertions.assertFalse(endPoint.isOpen());
        Object transport = endPoint.getTransport();
        if (transport instanceof Channel)
            Assertions.assertFalse(((Channel) (transport)).isOpen());

    }

    @Test
    public void testMaxIdleNoRequest() throws Exception {
        configureServer(new HttpServerTestFixture.EchoHandler());
        Socket client = newSocket(_serverURI.getHost(), _serverURI.getPort());
        client.setSoTimeout(10000);
        InputStream is = client.getInputStream();
        Assertions.assertFalse(client.isClosed());
        OutputStream os = client.getOutputStream();
        long start = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        os.write("GET ".getBytes("utf-8"));
        os.flush();
        Thread.sleep(sleepTime);
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            try {
                String response = IO.toString(is);
                MatcherAssert.assertThat(response, Matchers.is(""));
                Assertions.assertEquals((-1), is.read());
            } catch (Exception e) {
                ConnectorTimeoutTest.LOG.warn(e.getMessage());
            }
        });
        Assertions.assertTrue((((TimeUnit.NANOSECONDS.toMillis(System.nanoTime())) - start) < (maximumTestRuntime)));
    }

    @Test
    public void testMaxIdleNothingSent() throws Exception {
        configureServer(new HttpServerTestFixture.EchoHandler());
        long start = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        Socket client = newSocket(_serverURI.getHost(), _serverURI.getPort());
        client.setSoTimeout(10000);
        InputStream is = client.getInputStream();
        Assertions.assertFalse(client.isClosed());
        Thread.sleep(sleepTime);
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            try {
                String response = IO.toString(is);
                MatcherAssert.assertThat(response, Matchers.is(""));
                Assertions.assertEquals((-1), is.read());
            } catch (Exception e) {
                ConnectorTimeoutTest.LOG.warn(e);
            }
        });
        Assertions.assertTrue((((TimeUnit.NANOSECONDS.toMillis(System.nanoTime())) - start) < (maximumTestRuntime)));
    }

    @Test
    public void testMaxIdleDelayedDispatch() throws Exception {
        configureServer(new HttpServerTestFixture.EchoHandler());
        Socket client = newSocket(_serverURI.getHost(), _serverURI.getPort());
        client.setSoTimeout(10000);
        InputStream is = client.getInputStream();
        Assertions.assertFalse(client.isClosed());
        OutputStream os = client.getOutputStream();
        long start = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        os.write((((((((((("GET / HTTP/1.1\r\n" + "host: ") + (_serverURI.getHost())) + ":") + (_serverURI.getPort())) + "\r\n") + "connection: keep-alive\r\n") + "Content-Length: 20\r\n") + "Content-Type: text/plain\r\n") + "Connection: close\r\n") + "\r\n").getBytes("utf-8"));
        os.flush();
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            try {
                String response = IO.toString(is);
                MatcherAssert.assertThat(response, Matchers.containsString("500"));
                Assertions.assertEquals((-1), is.read());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        int duration = ((int) ((TimeUnit.NANOSECONDS.toMillis(System.nanoTime())) - start));
        MatcherAssert.assertThat(duration, Matchers.greaterThanOrEqualTo(ConnectorTimeoutTest.MAX_IDLE_TIME));
        MatcherAssert.assertThat(duration, Matchers.lessThan(maximumTestRuntime));
    }

    @Test
    public void testMaxIdleDispatch() throws Exception {
        configureServer(new HttpServerTestFixture.EchoHandler());
        Socket client = newSocket(_serverURI.getHost(), _serverURI.getPort());
        client.setSoTimeout(10000);
        InputStream is = client.getInputStream();
        Assertions.assertFalse(client.isClosed());
        OutputStream os = client.getOutputStream();
        long start = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        os.write(((((((((((("GET / HTTP/1.1\r\n" + "host: ") + (_serverURI.getHost())) + ":") + (_serverURI.getPort())) + "\r\n") + "connection: keep-alive\r\n") + "Content-Length: 20\r\n") + "Content-Type: text/plain\r\n") + "Connection: close\r\n") + "\r\n") + "1234567890").getBytes("utf-8"));
        os.flush();
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            try {
                String response = IO.toString(is);
                MatcherAssert.assertThat(response, Matchers.containsString("500"));
                Assertions.assertEquals((-1), is.read());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        int duration = ((int) ((TimeUnit.NANOSECONDS.toMillis(System.nanoTime())) - start));
        MatcherAssert.assertThat((duration + 100), Matchers.greaterThanOrEqualTo(ConnectorTimeoutTest.MAX_IDLE_TIME));
        MatcherAssert.assertThat((duration - 100), Matchers.lessThan(maximumTestRuntime));
    }

    @Test
    public void testMaxIdleWithSlowRequest() throws Exception {
        configureServer(new HttpServerTestFixture.EchoHandler());
        Socket client = newSocket(_serverURI.getHost(), _serverURI.getPort());
        client.setSoTimeout(10000);
        Assertions.assertFalse(client.isClosed());
        OutputStream os = client.getOutputStream();
        InputStream is = client.getInputStream();
        String content = "Wibble\r\n";
        byte[] contentB = content.getBytes("utf-8");
        os.write((((((((((((("GET / HTTP/1.0\r\n" + "host: ") + (_serverURI.getHost())) + ":") + (_serverURI.getPort())) + "\r\n") + "connection: keep-alive\r\n") + "Content-Length: ") + ((contentB.length) * 20)) + "\r\n") + "Content-Type: text/plain\r\n") + "Connection: close\r\n") + "\r\n").getBytes("utf-8"));
        os.flush();
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            for (int i = 0; i < 20; i++) {
                Thread.sleep(50);
                os.write(contentB);
                os.flush();
            }
            String in = IO.toString(is);
            int offset = 0;
            for (int i = 0; i < 20; i++) {
                offset = in.indexOf("Wibble", (offset + 1));
                Assertions.assertTrue((offset > 0), ("" + i));
            }
        });
    }

    @Test
    public void testMaxIdleWithSlowResponse() throws Exception {
        configureServer(new ConnectorTimeoutTest.SlowResponseHandler());
        Socket client = newSocket(_serverURI.getHost(), _serverURI.getPort());
        client.setSoTimeout(10000);
        Assertions.assertFalse(client.isClosed());
        OutputStream os = client.getOutputStream();
        InputStream is = client.getInputStream();
        os.write((((((((("GET / HTTP/1.0\r\n" + "host: ") + (_serverURI.getHost())) + ":") + (_serverURI.getPort())) + "\r\n") + "connection: keep-alive\r\n") + "Connection: close\r\n") + "\r\n").getBytes("utf-8"));
        os.flush();
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            String in = IO.toString(is);
            int offset = 0;
            for (int i = 0; i < 20; i++) {
                offset = in.indexOf("Hello World", (offset + 1));
                Assertions.assertTrue((offset > 0), ("" + i));
            }
        });
    }

    @Test
    public void testMaxIdleWithWait() throws Exception {
        configureServer(new ConnectorTimeoutTest.WaitHandler());
        Socket client = newSocket(_serverURI.getHost(), _serverURI.getPort());
        client.setSoTimeout(10000);
        Assertions.assertFalse(client.isClosed());
        OutputStream os = client.getOutputStream();
        InputStream is = client.getInputStream();
        os.write((((((((("GET / HTTP/1.0\r\n" + "host: ") + (_serverURI.getHost())) + ":") + (_serverURI.getPort())) + "\r\n") + "connection: keep-alive\r\n") + "Connection: close\r\n") + "\r\n").getBytes("utf-8"));
        os.flush();
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            String in = IO.toString(is);
            MatcherAssert.assertThat(in, Matchers.containsString("Hello World"));
        });
    }

    protected static class SlowResponseHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            baseRequest.setHandled(true);
            response.setStatus(200);
            OutputStream out = response.getOutputStream();
            for (int i = 0; i < 20; i++) {
                out.write("Hello World\r\n".getBytes());
                out.flush();
                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            out.close();
        }
    }

    protected static class HugeResponseHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            baseRequest.setHandled(true);
            response.setStatus(200);
            OutputStream out = response.getOutputStream();
            byte[] buffer = new byte[(128 * 1024) * 1024];
            Arrays.fill(buffer, ((byte) ('x')));
            for (int i = 0; i < (128 * 1024); i++) {
                buffer[((i * 1024) + 1022)] = '\r';
                buffer[((i * 1024) + 1023)] = '\n';
            }
            ByteBuffer bb = ByteBuffer.wrap(buffer);
            sendContent(bb);
            out.close();
        }
    }

    protected static class WaitHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            baseRequest.setHandled(true);
            response.setStatus(200);
            OutputStream out = response.getOutputStream();
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            out.write("Hello World\r\n".getBytes());
            out.flush();
        }
    }
}

