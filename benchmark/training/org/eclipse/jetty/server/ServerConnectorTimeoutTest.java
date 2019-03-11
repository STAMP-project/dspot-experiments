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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.log.StacklessLogging;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class ServerConnectorTimeoutTest extends ConnectorTimeoutTest {
    @Test
    public void testStartStopStart() throws Exception {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            _server.stop();
            _server.start();
        });
    }

    @Test
    public void testIdleTimeoutAfterSuspend() throws Exception {
        _server.stop();
        SuspendHandler _handler = new SuspendHandler();
        SessionHandler session = new SessionHandler();
        session.setHandler(_handler);
        _server.setHandler(session);
        _server.start();
        _handler.setSuspendFor(100);
        _handler.setResumeAfter(25);
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            String process = process(null).toUpperCase(Locale.ENGLISH);
            MatcherAssert.assertThat(process, Matchers.containsString("RESUMED"));
        });
    }

    @Test
    public void testIdleTimeoutAfterTimeout() throws Exception {
        SuspendHandler _handler = new SuspendHandler();
        _server.stop();
        SessionHandler session = new SessionHandler();
        session.setHandler(_handler);
        _server.setHandler(session);
        _server.start();
        _handler.setSuspendFor(50);
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            String process = process(null).toUpperCase(Locale.ENGLISH);
            MatcherAssert.assertThat(process, Matchers.containsString("TIMEOUT"));
        });
    }

    @Test
    public void testIdleTimeoutAfterComplete() throws Exception {
        SuspendHandler _handler = new SuspendHandler();
        _server.stop();
        SessionHandler session = new SessionHandler();
        session.setHandler(_handler);
        _server.setHandler(session);
        _server.start();
        _handler.setSuspendFor(100);
        _handler.setCompleteAfter(25);
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            String process = process(null).toUpperCase(Locale.ENGLISH);
            MatcherAssert.assertThat(process, Matchers.containsString("COMPLETED"));
        });
    }

    @Test
    public void testHttpWriteIdleTimeout() throws Exception {
        _httpConfiguration.setIdleTimeout(500);
        configureServer(new AbstractHandler.ErrorDispatchHandler() {
            @Override
            protected void doNonErrorHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                baseRequest.setHandled(true);
                IO.copy(request.getInputStream(), response.getOutputStream());
            }
        });
        Socket client = newSocket(_serverURI.getHost(), _serverURI.getPort());
        client.setSoTimeout(10000);
        Assertions.assertFalse(client.isClosed());
        final OutputStream os = client.getOutputStream();
        final InputStream is = client.getInputStream();
        final StringBuilder response = new StringBuilder();
        CompletableFuture<Void> responseFuture = CompletableFuture.runAsync(() -> {
            try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                int c;
                while ((c = reader.read()) != (-1)) {
                    response.append(((char) (c)));
                } 
            } catch (IOException e) {
                // Valid path (as connection is forcibly closed)
                // t.printStackTrace(System.err);
            }
        });
        CompletableFuture<Void> requestFuture = CompletableFuture.runAsync(() -> {
            try {
                os.write((((((((("POST /echo HTTP/1.0\r\n" + "host: ") + (_serverURI.getHost())) + ":") + (_serverURI.getPort())) + "\r\n") + "content-type: text/plain; charset=utf-8\r\n") + "content-length: 20\r\n") + "\r\n").getBytes("utf-8"));
                os.flush();
                os.write("123456789\n".getBytes("utf-8"));
                os.flush();
                TimeUnit.SECONDS.sleep(1);
                os.write("=========\n".getBytes("utf-8"));
                os.flush();
            } catch (InterruptedException | IOException e) {
                // Valid path, as write of second half of content can fail
                // e.printStackTrace(System.err);
            }
        });
        try (StacklessLogging ignore = new StacklessLogging(HttpChannel.class)) {
            requestFuture.get(2, TimeUnit.SECONDS);
            responseFuture.get(3, TimeUnit.SECONDS);
            MatcherAssert.assertThat(response.toString(), Matchers.containsString(" 500 "));
            MatcherAssert.assertThat(response.toString(), Matchers.not(Matchers.containsString("=========")));
        }
    }
}

