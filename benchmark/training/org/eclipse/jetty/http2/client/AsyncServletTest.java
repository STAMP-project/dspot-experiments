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
package org.eclipse.jetty.http2.client;


import Callback.NOOP;
import ErrorCode.CANCEL_STREAM_ERROR;
import HttpStatus.INTERNAL_SERVER_ERROR_500;
import MetaData.Request;
import MetaData.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http2.api.Session;
import org.eclipse.jetty.http2.api.Stream;
import org.eclipse.jetty.http2.frames.DataFrame;
import org.eclipse.jetty.http2.frames.HeadersFrame;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.FuturePromise;
import org.eclipse.jetty.util.Promise;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class AsyncServletTest extends AbstractTest {
    @Test
    public void testStartAsyncThenDispatch() throws Exception {
        byte[] content = new byte[1024];
        new Random().nextBytes(content);
        start(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                AsyncContext asyncContext = ((AsyncContext) (request.getAttribute(AsyncContext.class.getName())));
                if (asyncContext == null) {
                    AsyncContext context = request.startAsync();
                    context.setTimeout(0);
                    request.setAttribute(AsyncContext.class.getName(), context);
                    context.start(() -> {
                        sleep(1000);
                        context.dispatch();
                    });
                } else {
                    response.getOutputStream().write(content);
                }
            }
        });
        Session session = newClient(new Session.Listener.Adapter());
        HttpFields fields = new HttpFields();
        MetaData.Request metaData = newRequest("GET", fields);
        HeadersFrame frame = new HeadersFrame(metaData, null, true);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        CountDownLatch latch = new CountDownLatch(1);
        session.newStream(frame, new Promise.Adapter<>(), new Stream.Listener.Adapter() {
            @Override
            public void onData(Stream stream, DataFrame frame, Callback callback) {
                try {
                    BufferUtil.writeTo(frame.getData(), buffer);
                    callback.succeeded();
                    if (frame.isEndStream())
                        latch.countDown();

                } catch (IOException x) {
                    callback.failed(x);
                }
            }
        });
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        Assertions.assertArrayEquals(content, buffer.toByteArray());
    }

    @Test
    public void testStartAsyncThenClientSessionIdleTimeout() throws Exception {
        CountDownLatch serverLatch = new CountDownLatch(1);
        start(new AsyncServletTest.AsyncOnErrorServlet(serverLatch));
        long idleTimeout = 1000;
        client.setIdleTimeout(idleTimeout);
        Session session = newClient(new Session.Listener.Adapter());
        HttpFields fields = new HttpFields();
        MetaData.Request metaData = newRequest("GET", fields);
        HeadersFrame frame = new HeadersFrame(metaData, null, true);
        FuturePromise<Stream> promise = new FuturePromise();
        CountDownLatch clientLatch = new CountDownLatch(1);
        session.newStream(frame, promise, new Stream.Listener.Adapter() {
            @Override
            public void onHeaders(Stream stream, HeadersFrame frame) {
                MetaData.Response response = ((MetaData.Response) (frame.getMetaData()));
                if (((response.getStatus()) == (HttpStatus.INTERNAL_SERVER_ERROR_500)) && (frame.isEndStream()))
                    clientLatch.countDown();

            }
        });
        Stream stream = promise.get(5, TimeUnit.SECONDS);
        stream.setIdleTimeout((10 * idleTimeout));
        // When the client closes, the server receives the
        // corresponding frame and acts by notifying the failure,
        // which sends back to the client the error response.
        Assertions.assertTrue(serverLatch.await((2 * idleTimeout), TimeUnit.MILLISECONDS));
        Assertions.assertTrue(clientLatch.await((2 * idleTimeout), TimeUnit.MILLISECONDS));
    }

    @Test
    public void testStartAsyncThenClientStreamIdleTimeout() throws Exception {
        CountDownLatch serverLatch = new CountDownLatch(1);
        start(new AsyncServletTest.AsyncOnErrorServlet(serverLatch));
        long idleTimeout = 1000;
        client.setIdleTimeout((10 * idleTimeout));
        Session session = newClient(new Session.Listener.Adapter());
        HttpFields fields = new HttpFields();
        MetaData.Request metaData = newRequest("GET", fields);
        HeadersFrame frame = new HeadersFrame(metaData, null, true);
        FuturePromise<Stream> promise = new FuturePromise();
        CountDownLatch clientLatch = new CountDownLatch(1);
        session.newStream(frame, promise, new Stream.Listener.Adapter() {
            @Override
            public boolean onIdleTimeout(Stream stream, Throwable x) {
                clientLatch.countDown();
                return true;
            }
        });
        Stream stream = promise.get(5, TimeUnit.SECONDS);
        stream.setIdleTimeout(idleTimeout);
        // When the client resets, the server receives the
        // corresponding frame and acts by notifying the failure,
        // but the response is not sent back to the client.
        Assertions.assertTrue(serverLatch.await((2 * idleTimeout), TimeUnit.MILLISECONDS));
        Assertions.assertTrue(clientLatch.await((2 * idleTimeout), TimeUnit.MILLISECONDS));
    }

    @Test
    public void testStartAsyncThenClientResetWithoutRemoteErrorNotification() throws Exception {
        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setNotifyRemoteAsyncErrors(false);
        prepareServer(new org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory(httpConfiguration));
        ServletContextHandler context = new ServletContextHandler(server, "/");
        AtomicReference<AsyncContext> asyncContextRef = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        context.addServlet(new ServletHolder(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                AsyncContext asyncContext = request.startAsync();
                asyncContext.setTimeout(0);
                asyncContextRef.set(asyncContext);
                latch.countDown();
            }
        }), ((servletPath) + "/*"));
        server.start();
        prepareClient();
        client.start();
        Session session = newClient(new Session.Listener.Adapter());
        HttpFields fields = new HttpFields();
        MetaData.Request metaData = newRequest("GET", fields);
        HeadersFrame frame = new HeadersFrame(metaData, null, true);
        FuturePromise<Stream> promise = new FuturePromise();
        session.newStream(frame, promise, new Stream.Listener.Adapter());
        Stream stream = promise.get(5, TimeUnit.SECONDS);
        // Wait for the server to be in ASYNC_WAIT.
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        sleep(500);
        stream.reset(new org.eclipse.jetty.http2.frames.ResetFrame(stream.getId(), CANCEL_STREAM_ERROR.code), NOOP);
        // Wait for the reset to be processed by the server.
        sleep(500);
        AsyncContext asyncContext = asyncContextRef.get();
        ServletResponse response = asyncContext.getResponse();
        ServletOutputStream output = response.getOutputStream();
        Assertions.assertThrows(IOException.class, () -> {
            // Large writes or explicit flush() must
            // fail because the stream has been reset.
            output.flush();
        });
    }

    @Test
    public void testStartAsyncThenServerSessionIdleTimeout() throws Exception {
        testStartAsyncThenServerIdleTimeout(1000, (10 * 1000));
    }

    @Test
    public void testStartAsyncThenServerStreamIdleTimeout() throws Exception {
        testStartAsyncThenServerIdleTimeout((10 * 1000), 1000);
    }

    private static class AsyncOnErrorServlet extends HttpServlet implements AsyncListener {
        private final CountDownLatch latch;

        public AsyncOnErrorServlet(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            AsyncContext asyncContext = ((AsyncContext) (request.getAttribute(AsyncContext.class.getName())));
            if (asyncContext == null) {
                AsyncContext context = request.startAsync();
                context.setTimeout(0);
                request.setAttribute(AsyncContext.class.getName(), context);
                context.addListener(this);
            } else {
                throw new ServletException();
            }
        }

        @Override
        public void onComplete(AsyncEvent event) throws IOException {
        }

        @Override
        public void onTimeout(AsyncEvent event) throws IOException {
        }

        @Override
        public void onError(AsyncEvent event) throws IOException {
            HttpServletResponse response = ((HttpServletResponse) (event.getSuppliedResponse()));
            response.setStatus(INTERNAL_SERVER_ERROR_500);
            event.getAsyncContext().complete();
            latch.countDown();
        }

        @Override
        public void onStartAsync(AsyncEvent event) throws IOException {
        }
    }
}

