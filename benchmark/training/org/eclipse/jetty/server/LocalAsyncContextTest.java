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


import DispatcherType.REQUEST;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.junit.jupiter.api.Test;


public class LocalAsyncContextTest {
    protected Server _server;

    protected LocalAsyncContextTest.SuspendHandler _handler;

    protected Connector _connector;

    @Test
    public void testSuspendTimeout() throws Exception {
        String response;
        _handler.setRead(0);
        _handler.setSuspendFor(1000);
        _handler.setResumeAfter((-1));
        _handler.setCompleteAfter((-1));
        response = process(null);
        check(response, "TIMEOUT");
    }

    @Test
    public void testSuspendResume0() throws Exception {
        String response;
        _handler.setRead(0);
        _handler.setSuspendFor(10000);
        _handler.setResumeAfter(0);
        _handler.setCompleteAfter((-1));
        response = process(null);
        check(response, "STARTASYNC", "DISPATCHED");
    }

    @Test
    public void testSuspendResume100() throws Exception {
        String response;
        _handler.setRead(0);
        _handler.setSuspendFor(10000);
        _handler.setResumeAfter(100);
        _handler.setCompleteAfter((-1));
        response = process(null);
        check(response, "STARTASYNC", "DISPATCHED");
    }

    @Test
    public void testSuspendComplete0() throws Exception {
        String response;
        _handler.setRead(0);
        _handler.setSuspendFor(10000);
        _handler.setResumeAfter((-1));
        _handler.setCompleteAfter(0);
        response = process(null);
        check(response, "STARTASYNC", "COMPLETED");
    }

    @Test
    public void testSuspendComplete200() throws Exception {
        String response;
        _handler.setRead(0);
        _handler.setSuspendFor(10000);
        _handler.setResumeAfter((-1));
        _handler.setCompleteAfter(200);
        response = process(null);
        check(response, "STARTASYNC", "COMPLETED");
    }

    @Test
    public void testSuspendReadResume0() throws Exception {
        String response;
        _handler.setSuspendFor(10000);
        _handler.setRead((-1));
        _handler.setResumeAfter(0);
        _handler.setCompleteAfter((-1));
        response = process("wibble");
        check(response, "STARTASYNC", "DISPATCHED");
    }

    @Test
    public void testSuspendReadResume100() throws Exception {
        String response;
        _handler.setSuspendFor(10000);
        _handler.setRead((-1));
        _handler.setResumeAfter(100);
        _handler.setCompleteAfter((-1));
        response = process("wibble");
        check(response, "DISPATCHED");
    }

    @Test
    public void testSuspendOther() throws Exception {
        String response;
        _handler.setSuspendFor(10000);
        _handler.setRead((-1));
        _handler.setResumeAfter((-1));
        _handler.setCompleteAfter(0);
        response = process("wibble");
        check(response, "COMPLETED");
        _handler.setResumeAfter((-1));
        _handler.setCompleteAfter(100);
        response = process("wibble");
        check(response, "COMPLETED");
        _handler.setRead(6);
        _handler.setResumeAfter(0);
        _handler.setCompleteAfter((-1));
        response = process("wibble");
        check(response, "DISPATCHED");
        _handler.setResumeAfter(100);
        _handler.setCompleteAfter((-1));
        response = process("wibble");
        check(response, "DISPATCHED");
        _handler.setResumeAfter((-1));
        _handler.setCompleteAfter(0);
        response = process("wibble");
        check(response, "COMPLETED");
        _handler.setResumeAfter((-1));
        _handler.setCompleteAfter(100);
        response = process("wibble");
        check(response, "COMPLETED");
    }

    @Test
    public void testTwoCycles() throws Exception {
        String response;
        _handler.setRead(0);
        _handler.setSuspendFor(1000);
        _handler.setResumeAfter(100);
        _handler.setCompleteAfter((-1));
        _handler.setSuspendFor2(1000);
        _handler.setResumeAfter2(200);
        _handler.setCompleteAfter2((-1));
        response = process(null);
        check(response, "STARTASYNC", "DISPATCHED", "startasync", "STARTASYNC2", "DISPATCHED");
    }

    private class SuspendHandler extends HandlerWrapper {
        private int _read;

        private long _suspendFor = -1;

        private long _resumeAfter = -1;

        private long _completeAfter = -1;

        private long _suspendFor2 = -1;

        private long _resumeAfter2 = -1;

        private long _completeAfter2 = -1;

        public SuspendHandler() {
        }

        public void setRead(int read) {
            _read = read;
        }

        public void setSuspendFor(long suspendFor) {
            _suspendFor = suspendFor;
        }

        public void setResumeAfter(long resumeAfter) {
            _resumeAfter = resumeAfter;
        }

        public void setCompleteAfter(long completeAfter) {
            _completeAfter = completeAfter;
        }

        public void setSuspendFor2(long suspendFor2) {
            _suspendFor2 = suspendFor2;
        }

        public void setResumeAfter2(long resumeAfter2) {
            _resumeAfter2 = resumeAfter2;
        }

        public void setCompleteAfter2(long completeAfter2) {
            _completeAfter2 = completeAfter2;
        }

        @Override
        public void handle(String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
            if (REQUEST.equals(baseRequest.getDispatcherType())) {
                if ((_read) > 0) {
                    int read = _read;
                    byte[] buf = new byte[read];
                    while (read > 0)
                        read -= request.getInputStream().read(buf);

                } else
                    if ((_read) < 0) {
                        InputStream in = request.getInputStream();
                        int b = in.read();
                        while (b != (-1))
                            b = in.read();

                    }

                final AsyncContext asyncContext = baseRequest.startAsync();
                response.getOutputStream().println("STARTASYNC");
                asyncContext.addListener(__asyncListener);
                if ((_suspendFor) > 0)
                    asyncContext.setTimeout(_suspendFor);

                if ((_completeAfter) > 0) {
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(_completeAfter);
                                response.getOutputStream().println("COMPLETED");
                                response.setStatus(200);
                                baseRequest.setHandled(true);
                                asyncContext.complete();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } else
                    if ((_completeAfter) == 0) {
                        response.getOutputStream().println("COMPLETED");
                        response.setStatus(200);
                        baseRequest.setHandled(true);
                        asyncContext.complete();
                    }

                if ((_resumeAfter) > 0) {
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(_resumeAfter);
                                if ((getSession(true).getId()) != null)
                                    asyncContext.dispatch();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } else
                    if ((_resumeAfter) == 0) {
                        asyncContext.dispatch();
                    }

            } else {
                if ((request.getAttribute("TIMEOUT")) != null)
                    response.getOutputStream().println("TIMEOUT");
                else
                    response.getOutputStream().println("DISPATCHED");

                if ((_suspendFor2) >= 0) {
                    final AsyncContext asyncContext = baseRequest.startAsync();
                    response.getOutputStream().println("STARTASYNC2");
                    if ((_suspendFor2) > 0)
                        asyncContext.setTimeout(_suspendFor2);

                    _suspendFor2 = -1;
                    if ((_completeAfter2) > 0) {
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(_completeAfter2);
                                    response.getOutputStream().println("COMPLETED2");
                                    response.setStatus(200);
                                    baseRequest.setHandled(true);
                                    asyncContext.complete();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    } else
                        if ((_completeAfter2) == 0) {
                            response.getOutputStream().println("COMPLETED2");
                            response.setStatus(200);
                            baseRequest.setHandled(true);
                            asyncContext.complete();
                        }

                    if ((_resumeAfter2) > 0) {
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(_resumeAfter2);
                                    asyncContext.dispatch();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    } else
                        if ((_resumeAfter2) == 0) {
                            asyncContext.dispatch();
                        }

                } else {
                    response.setStatus(200);
                    baseRequest.setHandled(true);
                }
            }
        }
    }

    private AsyncListener __asyncListener = new AsyncListener() {
        @Override
        public void onComplete(AsyncEvent event) throws IOException {
        }

        @Override
        public void onError(AsyncEvent event) throws IOException {
        }

        @Override
        public void onStartAsync(AsyncEvent event) throws IOException {
            event.getSuppliedResponse().getOutputStream().println("startasync");
            event.getAsyncContext().addListener(this);
        }

        @Override
        public void onTimeout(AsyncEvent event) throws IOException {
            event.getSuppliedRequest().setAttribute("TIMEOUT", Boolean.TRUE);
            event.getAsyncContext().dispatch();
        }
    };
}

