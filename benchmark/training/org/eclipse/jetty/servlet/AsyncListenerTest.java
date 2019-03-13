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


import HttpStatus.BAD_GATEWAY_502;
import HttpStatus.INTERNAL_SERVER_ERROR_500;
import HttpStatus.OK_200;
import HttpTester.Response;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.TimeUnit;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class AsyncListenerTest {
    private QueuedThreadPool threadPool;

    private Server server;

    private LocalConnector connector;

    @Test
    public void test_StartAsync_Throw_OnError_Dispatch() throws Exception {
        test_StartAsync_Throw_OnError(( event) -> event.getAsyncContext().dispatch("/dispatch"));
        String httpResponse = connector.getResponse(("" + ((("GET /ctx/path HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "\r\n")));
        MatcherAssert.assertThat(httpResponse, Matchers.containsString("HTTP/1.1 200 "));
    }

    @Test
    public void test_StartAsync_Throw_OnError_Complete() throws Exception {
        test_StartAsync_Throw_OnError(( event) -> {
            HttpServletResponse response = ((HttpServletResponse) (event.getAsyncContext().getResponse()));
            response.setStatus(INTERNAL_SERVER_ERROR_500);
            ServletOutputStream output = response.getOutputStream();
            output.println(event.getThrowable().getClass().getName());
            if ((event.getThrowable().getCause()) != null)
                output.println(event.getThrowable().getCause().getClass().getName());

            output.println("COMPLETE");
            event.getAsyncContext().complete();
        });
        String httpResponse = connector.getResponse(("" + ((("GET /ctx/path HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "\r\n")));
        MatcherAssert.assertThat(httpResponse, Matchers.containsString("HTTP/1.1 500 "));
        MatcherAssert.assertThat(httpResponse, Matchers.containsString(AsyncListenerTest.TestRuntimeException.class.getName()));
        MatcherAssert.assertThat(httpResponse, Matchers.containsString("COMPLETE"));
    }

    @Test
    public void test_StartAsync_Throw_OnError_Throw() throws Exception {
        test_StartAsync_Throw_OnError(( event) -> {
            throw new IOException();
        });
        String httpResponse = connector.getResponse(("" + ((("GET /ctx/path HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "\r\n")));
        MatcherAssert.assertThat(httpResponse, Matchers.containsString("HTTP/1.1 500 "));
        MatcherAssert.assertThat(httpResponse, Matchers.containsString(AsyncListenerTest.TestRuntimeException.class.getName()));
    }

    @Test
    public void test_StartAsync_Throw_OnError_Nothing() throws Exception {
        test_StartAsync_Throw_OnError(( event) -> {
        });
        String httpResponse = connector.getResponse(("" + ((("GET /ctx/path HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "\r\n")));
        MatcherAssert.assertThat(httpResponse, Matchers.containsString("HTTP/1.1 500 "));
        MatcherAssert.assertThat(httpResponse, Matchers.containsString(AsyncListenerTest.TestRuntimeException.class.getName()));
    }

    @Test
    public void test_StartAsync_Throw_OnError_SendError() throws Exception {
        test_StartAsync_Throw_OnError(( event) -> {
            HttpServletResponse response = ((HttpServletResponse) (event.getAsyncContext().getResponse()));
            response.sendError(BAD_GATEWAY_502);
        });
        String httpResponse = connector.getResponse(("" + ((("GET /ctx/path HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "\r\n")));
        MatcherAssert.assertThat(httpResponse, Matchers.containsString("HTTP/1.1 502 "));
        MatcherAssert.assertThat(httpResponse, Matchers.containsString(AsyncListenerTest.TestRuntimeException.class.getName()));
    }

    @Test
    public void test_StartAsync_Throw_OnError_SendError_CustomErrorPage() throws Exception {
        test_StartAsync_Throw_OnError(( event) -> {
            HttpServletResponse response = ((HttpServletResponse) (event.getAsyncContext().getResponse()));
            response.sendError(BAD_GATEWAY_502);
        });
        // Add a custom error page.
        ErrorHandler errorHandler = new ErrorHandler() {
            @Override
            protected void writeErrorPageMessage(HttpServletRequest request, Writer writer, int code, String message, String uri) throws IOException {
                writer.write("CUSTOM\n");
                super.writeErrorPageMessage(request, writer, code, message, uri);
            }
        };
        server.setErrorHandler(errorHandler);
        String httpResponse = connector.getResponse(("" + ((("GET /ctx/path HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "\r\n")), 10, TimeUnit.MINUTES);
        MatcherAssert.assertThat(httpResponse, Matchers.containsString("HTTP/1.1 502 "));
        MatcherAssert.assertThat(httpResponse, Matchers.containsString("CUSTOM"));
    }

    @Test
    public void test_StartAsync_OnTimeout_Dispatch() throws Exception {
        test_StartAsync_OnTimeout(500, ( event) -> event.getAsyncContext().dispatch("/dispatch"));
        String httpResponse = connector.getResponse(("" + ((("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "\r\n")));
        MatcherAssert.assertThat(httpResponse, Matchers.containsString("HTTP/1.1 200 "));
    }

    @Test
    public void test_StartAsync_OnTimeout_Complete() throws Exception {
        test_StartAsync_OnTimeout(500, ( event) -> {
            HttpServletResponse response = ((HttpServletResponse) (event.getAsyncContext().getResponse()));
            response.setStatus(OK_200);
            ServletOutputStream output = response.getOutputStream();
            output.println("COMPLETE");
            event.getAsyncContext().complete();
        });
        String httpResponse = connector.getResponse(("" + ((("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "\r\n")));
        MatcherAssert.assertThat(httpResponse, Matchers.containsString("HTTP/1.1 200 "));
        MatcherAssert.assertThat(httpResponse, Matchers.containsString("COMPLETE"));
    }

    @Test
    public void test_StartAsync_OnTimeout_Throw() throws Exception {
        test_StartAsync_OnTimeout(500, ( event) -> {
            throw new AsyncListenerTest.TestRuntimeException();
        });
        String httpResponse = connector.getResponse(("" + ((("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "\r\n")));
        MatcherAssert.assertThat(httpResponse, Matchers.containsString("HTTP/1.1 500 "));
        MatcherAssert.assertThat(httpResponse, Matchers.containsString(AsyncListenerTest.TestRuntimeException.class.getName()));
    }

    @Test
    public void test_StartAsync_OnTimeout_Nothing() throws Exception {
        test_StartAsync_OnTimeout(500, ( event) -> {
        });
        String httpResponse = connector.getResponse(("" + ((("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "\r\n")));
        MatcherAssert.assertThat(httpResponse, Matchers.containsString("HTTP/1.1 500 "));
    }

    @Test
    public void test_StartAsync_OnTimeout_SendError() throws Exception {
        test_StartAsync_OnTimeout(500, ( event) -> {
            HttpServletResponse response = ((HttpServletResponse) (event.getAsyncContext().getResponse()));
            response.sendError(BAD_GATEWAY_502);
        });
        String httpResponse = connector.getResponse(("" + ((("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "\r\n")));
        MatcherAssert.assertThat(httpResponse, Matchers.containsString("HTTP/1.1 502 "));
    }

    @Test
    public void test_StartAsync_OnTimeout_SendError_CustomErrorPage() throws Exception {
        test_StartAsync_OnTimeout(500, ( event) -> {
            AsyncContext asyncContext = event.getAsyncContext();
            HttpServletResponse response = ((HttpServletResponse) (asyncContext.getResponse()));
            response.sendError(BAD_GATEWAY_502);
            asyncContext.complete();
        });
        // Add a custom error page.
        ErrorHandler errorHandler = new ErrorHandler() {
            @Override
            protected void writeErrorPageMessage(HttpServletRequest request, Writer writer, int code, String message, String uri) throws IOException {
                writer.write("CUSTOM\n");
                super.writeErrorPageMessage(request, writer, code, message, uri);
            }
        };
        errorHandler.setServer(server);
        server.setErrorHandler(errorHandler);
        String httpResponse = connector.getResponse(("" + ((("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "\r\n")));
        MatcherAssert.assertThat(httpResponse, Matchers.containsString("HTTP/1.1 502 "));
        MatcherAssert.assertThat(httpResponse, Matchers.containsString("CUSTOM"));
    }

    @Test
    public void test_StartAsync_OnComplete_Throw() throws Exception {
        ServletContextHandler context = new ServletContextHandler();
        context.addServlet(new ServletHolder(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                AsyncContext asyncContext = request.startAsync();
                asyncContext.setTimeout(0);
                asyncContext.addListener(new AsyncListenerTest.AsyncListenerAdapter() {
                    @Override
                    public void onComplete(AsyncEvent event) throws IOException {
                        throw new AsyncListenerTest.TestRuntimeException();
                    }
                });
                response.getOutputStream().print("DATA");
                asyncContext.complete();
            }
        }), "/*");
        startServer(context);
        String httpResponse = connector.getResponse(("" + ((("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "\r\n")));
        MatcherAssert.assertThat(httpResponse, Matchers.containsString("HTTP/1.1 200 "));
        MatcherAssert.assertThat(httpResponse, Matchers.containsString("DATA"));
    }

    @Test
    public void test_StartAsync_OnTimeout_CalledBy_PooledThread() throws Exception {
        String threadNamePrefix = "async_listener";
        threadPool = new QueuedThreadPool();
        threadPool.setName(threadNamePrefix);
        ServletContextHandler context = new ServletContextHandler();
        context.addServlet(new ServletHolder(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                AsyncContext asyncContext = request.startAsync();
                asyncContext.setTimeout(1000);
                asyncContext.addListener(new AsyncListenerTest.AsyncListenerAdapter() {
                    @Override
                    public void onTimeout(AsyncEvent event) throws IOException {
                        if (Thread.currentThread().getName().startsWith(threadNamePrefix))
                            response.setStatus(OK_200);
                        else
                            response.setStatus(INTERNAL_SERVER_ERROR_500);

                        asyncContext.complete();
                    }
                });
            }
        }), "/*");
        startServer(context);
        HttpTester.Response response = HttpTester.parseResponse(connector.getResponse(("" + (("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "\r\n"))));
        Assertions.assertEquals(OK_200, response.getStatus());
    }

    // Unique named RuntimeException to help during debugging / assertions.
    public static class TestRuntimeException extends RuntimeException {}

    public static class AsyncListenerAdapter implements AsyncListener {
        @Override
        public void onComplete(AsyncEvent event) throws IOException {
        }

        @Override
        public void onTimeout(AsyncEvent event) throws IOException {
        }

        @Override
        public void onError(AsyncEvent event) throws IOException {
        }

        @Override
        public void onStartAsync(AsyncEvent event) throws IOException {
        }
    }

    @FunctionalInterface
    private interface IOConsumer<T> {
        void accept(T t) throws IOException;
    }
}

