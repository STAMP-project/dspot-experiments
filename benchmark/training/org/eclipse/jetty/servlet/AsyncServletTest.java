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


import RequestDispatcher.ERROR_MESSAGE;
import RequestDispatcher.ERROR_STATUS_CODE;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.QuietServletException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.log.StacklessLogging;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class AsyncServletTest {
    protected AsyncServletTest.AsyncServlet _servlet = new AsyncServletTest.AsyncServlet();

    protected int _port;

    protected Server _server = new Server();

    protected ServletHandler _servletHandler;

    protected ErrorPageErrorHandler _errorHandler;

    protected ServerConnector _connector;

    protected List<String> _log;

    protected int _expectedLogs;

    protected String _expectedCode;

    protected static List<String> __history = new CopyOnWriteArrayList<>();

    protected static CountDownLatch __latch;

    @Test
    public void testNormal() throws Exception {
        String response = process(null, null);
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("REQUEST /ctx/path/info", "initial"));
        assertContains("NORMAL", response);
        Assertions.assertFalse(AsyncServletTest.__history.contains("onTimeout"));
        Assertions.assertFalse(AsyncServletTest.__history.contains("onComplete"));
    }

    @Test
    public void testSleep() throws Exception {
        String response = process("sleep=200", null);
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("REQUEST /ctx/path/info", "initial"));
        assertContains("SLEPT", response);
        Assertions.assertFalse(AsyncServletTest.__history.contains("onTimeout"));
        Assertions.assertFalse(AsyncServletTest.__history.contains("onComplete"));
    }

    @Test
    public void testNonAsync() throws Exception {
        String response = process("", null);
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("REQUEST /ctx/path/info", "initial"));
        assertContains("NORMAL", response);
    }

    @Test
    public void testAsyncNotSupportedNoAsync() throws Exception {
        _expectedCode = "200 ";
        String response = process("noasync", "", null);
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("REQUEST /ctx/noasync/info", "initial"));
        assertContains("NORMAL", response);
    }

    @Test
    public void testAsyncNotSupportedAsync() throws Exception {
        try (StacklessLogging stackless = new StacklessLogging(HttpChannel.class)) {
            _expectedCode = "500 ";
            String response = process("noasync", "start=200", null);
            MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 500 "));
            MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("REQUEST /ctx/noasync/info", "initial", "ERROR /ctx/error/custom", "!initial"));
            assertContains("500", response);
            assertContains("!asyncSupported", response);
            assertContains("AsyncServletTest$AsyncServlet", response);
        }
    }

    @Test
    public void testStart() throws Exception {
        _expectedCode = "500 ";
        String response = process("start=200", null);
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 500 Server Error"));
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("REQUEST /ctx/path/info", "initial", "start", "onTimeout", "ERROR /ctx/error/custom", "!initial", "onComplete"));
        assertContains("ERROR DISPATCH: /ctx/error/custom", response);
    }

    @Test
    public void testStartOnTimeoutDispatch() throws Exception {
        String response = process("start=200&timeout=dispatch", null);
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("REQUEST /ctx/path/info", "initial", "start", "onTimeout", "dispatch", "ASYNC /ctx/path/info", "!initial", "onComplete"));
        assertContains("DISPATCHED", response);
    }

    @Test
    public void testStartOnTimeoutError() throws Exception {
        _expectedCode = "500 ";
        String response = process("start=200&timeout=error", null);
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 500 Server Error"));
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("REQUEST /ctx/path/info", "initial", "start", "onTimeout", "error", "onError", "ERROR /ctx/error/custom", "!initial", "onComplete"));
        assertContains("ERROR DISPATCH", response);
    }

    @Test
    public void testStartOnTimeoutErrorComplete() throws Exception {
        String response = process("start=200&timeout=error&error=complete", null);
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("REQUEST /ctx/path/info", "initial", "start", "onTimeout", "error", "onError", "complete", "onComplete"));
        assertContains("COMPLETED", response);
    }

    @Test
    public void testStartOnTimeoutErrorDispatch() throws Exception {
        String response = process("start=200&timeout=error&error=dispatch", null);
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("REQUEST /ctx/path/info", "initial", "start", "onTimeout", "error", "onError", "dispatch", "ASYNC /ctx/path/info", "!initial", "onComplete"));
        assertContains("DISPATCHED", response);
    }

    @Test
    public void testStartOnTimeoutComplete() throws Exception {
        String response = process("start=200&timeout=complete", null);
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("REQUEST /ctx/path/info", "initial", "start", "onTimeout", "complete", "onComplete"));
        assertContains("COMPLETED", response);
    }

    @Test
    public void testStartWaitDispatch() throws Exception {
        String response = process("start=200&dispatch=10", null);
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("REQUEST /ctx/path/info", "initial", "start", "dispatch", "ASYNC /ctx/path/info", "!initial", "onComplete"));
        Assertions.assertFalse(AsyncServletTest.__history.contains("onTimeout"));
    }

    @Test
    public void testStartDispatch() throws Exception {
        String response = process("start=200&dispatch=0", null);
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("REQUEST /ctx/path/info", "initial", "start", "dispatch", "ASYNC /ctx/path/info", "!initial", "onComplete"));
    }

    @Test
    public void testStartError() throws Exception {
        _expectedCode = "500 ";
        String response = process("start=200&throw=1", null);
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 500 Server Error"));
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("REQUEST /ctx/path/info", "initial", "start", "onError", "ERROR /ctx/error/custom", "!initial", "onComplete"));
        assertContains("ERROR DISPATCH: /ctx/error/custom", response);
    }

    @Test
    public void testStartWaitComplete() throws Exception {
        String response = process("start=200&complete=50", null);
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("REQUEST /ctx/path/info", "initial", "start", "complete", "onComplete"));
        assertContains("COMPLETED", response);
        Assertions.assertFalse(AsyncServletTest.__history.contains("onTimeout"));
        Assertions.assertFalse(AsyncServletTest.__history.contains("!initial"));
    }

    @Test
    public void testStartComplete() throws Exception {
        String response = process("start=200&complete=0", null);
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("REQUEST /ctx/path/info", "initial", "start", "complete", "onComplete"));
        assertContains("COMPLETED", response);
        Assertions.assertFalse(AsyncServletTest.__history.contains("onTimeout"));
        Assertions.assertFalse(AsyncServletTest.__history.contains("!initial"));
    }

    @Test
    public void testStartWaitDispatchStartWaitDispatch() throws Exception {
        String response = process("start=1000&dispatch=10&start2=1000&dispatch2=10", null);
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("REQUEST /ctx/path/info", "initial", "start", "dispatch", "ASYNC /ctx/path/info", "!initial", "onStartAsync", "start", "dispatch", "ASYNC /ctx/path/info", "!initial", "onComplete"));
        assertContains("DISPATCHED", response);
    }

    @Test
    public void testStartWaitDispatchStartComplete() throws Exception {
        String response = process("start=1000&dispatch=10&start2=1000&complete2=10", null);
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("REQUEST /ctx/path/info", "initial", "start", "dispatch", "ASYNC /ctx/path/info", "!initial", "onStartAsync", "start", "complete", "onComplete"));
        assertContains("COMPLETED", response);
    }

    @Test
    public void testStartWaitDispatchStart() throws Exception {
        _expectedCode = "500 ";
        String response = process("start=1000&dispatch=10&start2=10", null);
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 500 Server Error"));
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("REQUEST /ctx/path/info", "initial", "start", "dispatch", "ASYNC /ctx/path/info", "!initial", "onStartAsync", "start", "onTimeout", "ERROR /ctx/error/custom", "!initial", "onComplete"));
        assertContains("ERROR DISPATCH: /ctx/error/custom", response);
    }

    @Test
    public void testStartTimeoutStartDispatch() throws Exception {
        String response = process("start=10&start2=1000&dispatch2=10", null);
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("REQUEST /ctx/path/info", "initial", "start", "onTimeout", "ERROR /ctx/error/custom", "!initial", "onStartAsync", "start", "dispatch", "ASYNC /ctx/path/info", "!initial", "onComplete"));
        assertContains("DISPATCHED", response);
    }

    @Test
    public void testStartTimeoutStartComplete() throws Exception {
        String response = process("start=10&start2=1000&complete2=10", null);
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("REQUEST /ctx/path/info", "initial", "start", "onTimeout", "ERROR /ctx/error/custom", "!initial", "onStartAsync", "start", "complete", "onComplete"));
        assertContains("COMPLETED", response);
    }

    @Test
    public void testStartTimeoutStart() throws Exception {
        _expectedCode = "500 ";
        _errorHandler.addErrorPage(500, "/path/error");
        String response = process("start=10&start2=10", null);
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("REQUEST /ctx/path/info", "initial", "start", "onTimeout", "ERROR /ctx/path/error", "!initial", "onStartAsync", "start", "onTimeout", "onComplete"));// Error Page Loop!

        assertContains("HTTP ERROR 500", response);
    }

    @Test
    public void testWrapStartDispatch() throws Exception {
        String response = process("wrap=true&start=200&dispatch=20", null);
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("REQUEST /ctx/path/info", "initial", "start", "dispatch", "ASYNC /ctx/path/info", "wrapped REQ RSP", "!initial", "onComplete"));
        assertContains("DISPATCHED", response);
    }

    @Test
    public void testStartDispatchEncodedPath() throws Exception {
        String response = process("start=200&dispatch=20&path=/p%20th3", null);
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("REQUEST /ctx/path/info", "initial", "start", "dispatch", "ASYNC /ctx/p%20th3", "!initial", "onComplete"));
        assertContains("DISPATCHED", response);
    }

    @Test
    public void testFwdStartDispatch() throws Exception {
        String response = process("fwd", "start=200&dispatch=20", null);
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("FWD REQUEST /ctx/fwd/info", "FORWARD /ctx/path1", "initial", "start", "dispatch", "FWD ASYNC /ctx/fwd/info", "FORWARD /ctx/path1", "!initial", "onComplete"));
        assertContains("DISPATCHED", response);
    }

    @Test
    public void testFwdStartDispatchPath() throws Exception {
        String response = process("fwd", "start=200&dispatch=20&path=/path2", null);
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("FWD REQUEST /ctx/fwd/info", "FORWARD /ctx/path1", "initial", "start", "dispatch", "ASYNC /ctx/path2", "!initial", "onComplete"));
        assertContains("DISPATCHED", response);
    }

    @Test
    public void testFwdWrapStartDispatch() throws Exception {
        String response = process("fwd", "wrap=true&start=200&dispatch=20", null);
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("FWD REQUEST /ctx/fwd/info", "FORWARD /ctx/path1", "initial", "start", "dispatch", "ASYNC /ctx/path1", "wrapped REQ RSP", "!initial", "onComplete"));
        assertContains("DISPATCHED", response);
    }

    @Test
    public void testFwdWrapStartDispatchPath() throws Exception {
        String response = process("fwd", "wrap=true&start=200&dispatch=20&path=/path2", null);
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("FWD REQUEST /ctx/fwd/info", "FORWARD /ctx/path1", "initial", "start", "dispatch", "ASYNC /ctx/path2", "wrapped REQ RSP", "!initial", "onComplete"));
        assertContains("DISPATCHED", response);
    }

    @Test
    public void testAsyncRead() throws Exception {
        String header = "GET /ctx/path/info?start=2000&dispatch=1500 HTTP/1.1\r\n" + ((("Host: localhost\r\n" + "Content-Length: 10\r\n") + "Connection: close\r\n") + "\r\n");
        String body = "12345678\r\n";
        try (Socket socket = new Socket("localhost", _port)) {
            socket.setSoTimeout(10000);
            socket.getOutputStream().write(header.getBytes(StandardCharsets.ISO_8859_1));
            socket.getOutputStream().write(body.getBytes(StandardCharsets.ISO_8859_1), 0, 2);
            Thread.sleep(500);
            socket.getOutputStream().write(body.getBytes(StandardCharsets.ISO_8859_1), 2, 8);
            String response = IO.toString(socket.getInputStream());
            AsyncServletTest.__latch.await(1, TimeUnit.SECONDS);
            MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
            MatcherAssert.assertThat(AsyncServletTest.__history, Matchers.contains("REQUEST /ctx/path/info", "initial", "start", "async-read=10", "dispatch", "ASYNC /ctx/path/info", "!initial", "onComplete"));
        }
    }

    private static class FwdServlet extends HttpServlet {
        @Override
        public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
            AsyncServletTest.historyAdd(((("FWD " + (request.getDispatcherType())) + " ") + (request.getRequestURI())));
            if ((request instanceof ServletRequestWrapper) || (response instanceof ServletResponseWrapper))
                AsyncServletTest.historyAdd((("wrapped" + (request instanceof ServletRequestWrapper ? " REQ" : "")) + (response instanceof ServletResponseWrapper ? " RSP" : "")));

            request.getServletContext().getRequestDispatcher("/path1").forward(request, response);
        }
    }

    private static class AsyncServlet extends HttpServlet {
        private static final long serialVersionUID = -8161977157098646562L;

        private final Timer _timer = new Timer();

        @Override
        public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
            // this should always fail at this point
            try {
                request.getAsyncContext();
                throw new IllegalStateException();
            } catch (IllegalStateException e) {
                // ignored
            }
            AsyncServletTest.historyAdd((((request.getDispatcherType()) + " ") + (request.getRequestURI())));
            if ((request instanceof ServletRequestWrapper) || (response instanceof ServletResponseWrapper))
                AsyncServletTest.historyAdd((("wrapped" + (request instanceof ServletRequestWrapper ? " REQ" : "")) + (response instanceof ServletResponseWrapper ? " RSP" : "")));

            boolean wrap = "true".equals(request.getParameter("wrap"));
            int read_before = 0;
            long sleep_for = -1;
            long start_for = -1;
            long start2_for = -1;
            long dispatch_after = -1;
            long dispatch2_after = -1;
            long complete_after = -1;
            long complete2_after = -1;
            if ((request.getParameter("read")) != null)
                read_before = Integer.parseInt(request.getParameter("read"));

            if ((request.getParameter("sleep")) != null)
                sleep_for = Integer.parseInt(request.getParameter("sleep"));

            if ((request.getParameter("start")) != null)
                start_for = Integer.parseInt(request.getParameter("start"));

            if ((request.getParameter("start2")) != null)
                start2_for = Integer.parseInt(request.getParameter("start2"));

            if ((request.getParameter("dispatch")) != null)
                dispatch_after = Integer.parseInt(request.getParameter("dispatch"));

            final String path = request.getParameter("path");
            if ((request.getParameter("dispatch2")) != null)
                dispatch2_after = Integer.parseInt(request.getParameter("dispatch2"));

            if ((request.getParameter("complete")) != null)
                complete_after = Integer.parseInt(request.getParameter("complete"));

            if ((request.getParameter("complete2")) != null)
                complete2_after = Integer.parseInt(request.getParameter("complete2"));

            if ((request.getAttribute("State")) == null) {
                request.setAttribute("State", new Integer(1));
                AsyncServletTest.historyAdd("initial");
                if (read_before > 0) {
                    byte[] buf = new byte[read_before];
                    request.getInputStream().read(buf);
                } else
                    if (read_before < 0) {
                        InputStream in = request.getInputStream();
                        int b = in.read();
                        while (b != (-1))
                            b = in.read();

                    } else
                        if ((request.getContentLength()) > 0) {
                            new Thread() {
                                @Override
                                public void run() {
                                    int c = 0;
                                    try {
                                        InputStream in = request.getInputStream();
                                        int b = 0;
                                        while (b != (-1))
                                            if ((b = in.read()) >= 0)
                                                c++;


                                        AsyncServletTest.historyAdd(("async-read=" + c));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();
                        }


                if (start_for >= 0) {
                    final AsyncContext async = (wrap) ? request.startAsync(new javax.servlet.http.HttpServletRequestWrapper(request), new javax.servlet.http.HttpServletResponseWrapper(response)) : request.startAsync();
                    if (start_for > 0)
                        async.setTimeout(start_for);

                    async.addListener(AsyncServletTest.__listener);
                    AsyncServletTest.historyAdd("start");
                    if ("1".equals(request.getParameter("throw")))
                        throw new QuietServletException(new Exception("test throw in async 1"));

                    if (complete_after > 0) {
                        TimerTask complete = new TimerTask() {
                            @Override
                            public void run() {
                                try {
                                    response.setStatus(200);
                                    response.getOutputStream().println("COMPLETED\n");
                                    AsyncServletTest.historyAdd("complete");
                                    async.complete();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        synchronized(_timer) {
                            _timer.schedule(complete, complete_after);
                        }
                    } else
                        if (complete_after == 0) {
                            response.setStatus(200);
                            response.getOutputStream().println("COMPLETED\n");
                            AsyncServletTest.historyAdd("complete");
                            async.complete();
                        } else
                            if (dispatch_after > 0) {
                                TimerTask dispatch = new TimerTask() {
                                    @Override
                                    public void run() {
                                        AsyncServletTest.historyAdd("dispatch");
                                        if (path != null) {
                                            int q = path.indexOf('?');
                                            String uriInContext = (q >= 0) ? (URIUtil.encodePath(path.substring(0, q))) + (path.substring(q)) : URIUtil.encodePath(path);
                                            async.dispatch(uriInContext);
                                        } else
                                            async.dispatch();

                                    }
                                };
                                synchronized(_timer) {
                                    _timer.schedule(dispatch, dispatch_after);
                                }
                            } else
                                if (dispatch_after == 0) {
                                    AsyncServletTest.historyAdd("dispatch");
                                    if (path != null)
                                        async.dispatch(path);
                                    else
                                        async.dispatch();

                                }



                } else
                    if (sleep_for >= 0) {
                        try {
                            Thread.sleep(sleep_for);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        response.setStatus(200);
                        response.getOutputStream().println("SLEPT\n");
                    } else {
                        response.setStatus(200);
                        response.getOutputStream().println("NORMAL\n");
                    }

            } else {
                AsyncServletTest.historyAdd("!initial");
                if ((start2_for >= 0) && ((request.getAttribute("2nd")) == null)) {
                    final AsyncContext async = (wrap) ? request.startAsync(new javax.servlet.http.HttpServletRequestWrapper(request), new javax.servlet.http.HttpServletResponseWrapper(response)) : request.startAsync();
                    async.addListener(AsyncServletTest.__listener);
                    request.setAttribute("2nd", "cycle");
                    if (start2_for > 0) {
                        async.setTimeout(start2_for);
                    }
                    AsyncServletTest.historyAdd("start");
                    if ("2".equals(request.getParameter("throw")))
                        throw new QuietServletException(new Exception("test throw in async 2"));

                    if (complete2_after > 0) {
                        TimerTask complete = new TimerTask() {
                            @Override
                            public void run() {
                                try {
                                    response.setStatus(200);
                                    response.getOutputStream().println("COMPLETED\n");
                                    AsyncServletTest.historyAdd("complete");
                                    async.complete();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        synchronized(_timer) {
                            _timer.schedule(complete, complete2_after);
                        }
                    } else
                        if (complete2_after == 0) {
                            response.setStatus(200);
                            response.getOutputStream().println("COMPLETED\n");
                            AsyncServletTest.historyAdd("complete");
                            async.complete();
                        } else
                            if (dispatch2_after > 0) {
                                TimerTask dispatch = new TimerTask() {
                                    @Override
                                    public void run() {
                                        AsyncServletTest.historyAdd("dispatch");
                                        async.dispatch();
                                    }
                                };
                                synchronized(_timer) {
                                    _timer.schedule(dispatch, dispatch2_after);
                                }
                            } else
                                if (dispatch2_after == 0) {
                                    AsyncServletTest.historyAdd("dispatch");
                                    async.dispatch();
                                }



                } else
                    if ((request.getDispatcherType()) == (DispatcherType.ERROR)) {
                        response.getOutputStream().println(((("ERROR DISPATCH: " + (request.getContextPath())) + (request.getServletPath())) + (request.getPathInfo())));
                        response.getOutputStream().println(("" + (request.getAttribute(ERROR_STATUS_CODE))));
                        response.getOutputStream().println(("" + (request.getAttribute(ERROR_MESSAGE))));
                    } else {
                        response.setStatus(200);
                        response.getOutputStream().println("DISPATCHED");
                    }

            }
        }
    }

    private static AsyncListener __listener = new AsyncListener() {
        @Override
        public void onTimeout(AsyncEvent event) throws IOException {
            AsyncServletTest.historyAdd("onTimeout");
            String action = event.getSuppliedRequest().getParameter("timeout");
            if (action != null) {
                AsyncServletTest.historyAdd(action);
                switch (action) {
                    case "dispatch" :
                        event.getAsyncContext().dispatch();
                        break;
                    case "complete" :
                        event.getSuppliedResponse().getOutputStream().println("COMPLETED\n");
                        event.getAsyncContext().complete();
                        break;
                    case "error" :
                        throw new RuntimeException("error in onTimeout");
                }
            }
        }

        @Override
        public void onStartAsync(AsyncEvent event) throws IOException {
            AsyncServletTest.historyAdd("onStartAsync");
        }

        @Override
        public void onError(AsyncEvent event) throws IOException {
            AsyncServletTest.historyAdd("onError");
            String action = event.getSuppliedRequest().getParameter("error");
            if (action != null) {
                AsyncServletTest.historyAdd(action);
                switch (action) {
                    case "dispatch" :
                        event.getAsyncContext().dispatch();
                        break;
                    case "complete" :
                        event.getSuppliedResponse().getOutputStream().println("COMPLETED\n");
                        event.getAsyncContext().complete();
                        break;
                }
            }
        }

        @Override
        public void onComplete(AsyncEvent event) throws IOException {
            AsyncServletTest.historyAdd("onComplete");
        }
    };

    class Log extends AbstractLifeCycle implements RequestLog {
        @Override
        public void log(Request request, Response response) {
            int status = response.getCommittedMetaData().getStatus();
            long written = response.getHttpChannel().getBytesWritten();
            _log.add(((((status + " ") + written) + " ") + (request.getRequestURI())));
        }
    }
}

