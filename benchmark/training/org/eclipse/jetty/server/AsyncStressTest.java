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
import java.net.InetAddress;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


@Disabled
@Tag("stress")
public class AsyncStressTest {
    private static final Logger LOG = Log.getLogger(AsyncStressTest.class);

    protected QueuedThreadPool _threads = new QueuedThreadPool();

    protected Server _server = new Server(_threads);

    protected AsyncStressTest.SuspendHandler _handler = new AsyncStressTest.SuspendHandler();

    protected ServerConnector _connector;

    protected InetAddress _addr;

    protected int _port;

    protected Random _random = new Random();

    private static final String[][] __paths = new String[][]{ new String[]{ "/path", "NORMAL" }, new String[]{ "/path/info", "NORMAL" }, new String[]{ "/path?sleep=<PERIOD>", "SLEPT" }, new String[]{ "/path?suspend=<PERIOD>", "TIMEOUT" }, new String[]{ "/path?suspend=60000&resume=<PERIOD>", "RESUMED" }, new String[]{ "/path?suspend=60000&complete=<PERIOD>", "COMPLETED" } };

    @Test
    public void testAsync() throws Throwable {
        doConnections(1600, 240);
    }

    private static class SuspendHandler extends HandlerWrapper {
        private final Timer _timer;

        private SuspendHandler() {
            _timer = new Timer();
        }

        @Override
        public void handle(String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
            int read_before = 0;
            long sleep_for = -1;
            long suspend_for = -1;
            long resume_after = -1;
            long complete_after = -1;
            final String uri = baseRequest.getHttpURI().toString();
            if ((request.getParameter("read")) != null)
                read_before = Integer.parseInt(request.getParameter("read"));

            if ((request.getParameter("sleep")) != null)
                sleep_for = Integer.parseInt(request.getParameter("sleep"));

            if ((request.getParameter("suspend")) != null)
                suspend_for = Integer.parseInt(request.getParameter("suspend"));

            if ((request.getParameter("resume")) != null)
                resume_after = Integer.parseInt(request.getParameter("resume"));

            if ((request.getParameter("complete")) != null)
                complete_after = Integer.parseInt(request.getParameter("complete"));

            if (REQUEST.equals(baseRequest.getDispatcherType())) {
                if (read_before > 0) {
                    byte[] buf = new byte[read_before];
                    request.getInputStream().read(buf);
                } else
                    if (read_before < 0) {
                        InputStream in = request.getInputStream();
                        int b = in.read();
                        while (b != (-1))
                            b = in.read();

                    }

                if (suspend_for >= 0) {
                    final AsyncContext asyncContext = baseRequest.startAsync();
                    asyncContext.addListener(AsyncStressTest.__asyncListener);
                    if (suspend_for > 0)
                        asyncContext.setTimeout(suspend_for);

                    if (complete_after > 0) {
                        TimerTask complete = new TimerTask() {
                            @Override
                            public void run() {
                                try {
                                    response.setStatus(200);
                                    response.getOutputStream().println(("COMPLETED " + (request.getHeader("result"))));
                                    baseRequest.setHandled(true);
                                    asyncContext.complete();
                                } catch (Exception e) {
                                    Request br = ((Request) (asyncContext.getRequest()));
                                    System.err.println(("\n" + (e.toString())));
                                    System.err.println(((baseRequest + "==") + br));
                                    System.err.println(((uri + "==") + (br.getHttpURI())));
                                    System.err.println(((asyncContext + "==") + (br.getHttpChannelState())));
                                    AsyncStressTest.LOG.warn(e);
                                    System.exit(1);
                                }
                            }
                        };
                        synchronized(_timer) {
                            _timer.schedule(complete, complete_after);
                        }
                    } else
                        if (complete_after == 0) {
                            response.setStatus(200);
                            response.getOutputStream().println(("COMPLETED " + (request.getHeader("result"))));
                            baseRequest.setHandled(true);
                            asyncContext.complete();
                        } else
                            if (resume_after > 0) {
                                TimerTask resume = new TimerTask() {
                                    @Override
                                    public void run() {
                                        asyncContext.dispatch();
                                    }
                                };
                                synchronized(_timer) {
                                    _timer.schedule(resume, resume_after);
                                }
                            } else
                                if (resume_after == 0) {
                                    asyncContext.dispatch();
                                }



                } else
                    if (sleep_for >= 0) {
                        try {
                            Thread.sleep(sleep_for);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        response.setStatus(200);
                        response.getOutputStream().println(("SLEPT " + (request.getHeader("result"))));
                        baseRequest.setHandled(true);
                    } else {
                        response.setStatus(200);
                        response.getOutputStream().println(("NORMAL " + (request.getHeader("result"))));
                        baseRequest.setHandled(true);
                    }

            } else
                if ((request.getAttribute("TIMEOUT")) != null) {
                    response.setStatus(200);
                    response.getOutputStream().println(("TIMEOUT " + (request.getHeader("result"))));
                    baseRequest.setHandled(true);
                } else {
                    response.setStatus(200);
                    response.getOutputStream().println(("RESUMED " + (request.getHeader("result"))));
                    baseRequest.setHandled(true);
                }

        }
    }

    private static AsyncListener __asyncListener = new AsyncListener() {
        @Override
        public void onComplete(AsyncEvent event) throws IOException {
        }

        @Override
        public void onTimeout(AsyncEvent event) throws IOException {
            event.getSuppliedRequest().setAttribute("TIMEOUT", Boolean.TRUE);
            event.getSuppliedRequest().getAsyncContext().dispatch();
        }

        @Override
        public void onError(AsyncEvent event) throws IOException {
        }

        @Override
        public void onStartAsync(AsyncEvent event) throws IOException {
        }
    };
}

