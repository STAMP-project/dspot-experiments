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
package org.eclipse.jetty.server.handler;


import HttpStatus.INTERNAL_SERVER_ERROR_500;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.io.ConnectionStatistics;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class StatisticsHandlerTest {
    private Server _server;

    private ConnectionStatistics _statistics;

    private LocalConnector _connector;

    private StatisticsHandlerTest.LatchHandler _latchHandler;

    private StatisticsHandler _statsHandler;

    @Test
    public void testRequest() throws Exception {
        final CyclicBarrier[] barrier = new CyclicBarrier[]{ new CyclicBarrier(2), new CyclicBarrier(2) };
        _statsHandler.setHandler(new AbstractHandler() {
            @Override
            public void handle(String path, Request request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
                request.setHandled(true);
                try {
                    barrier[0].await();
                    barrier[1].await();
                } catch (Exception x) {
                    Thread.currentThread().interrupt();
                    throw new IOException(x);
                }
            }
        });
        _server.start();
        String request = "GET / HTTP/1.1\r\n" + ("Host: localhost\r\n" + "\r\n");
        _connector.executeRequest(request);
        barrier[0].await();
        Assertions.assertEquals(1, _statistics.getConnections());
        Assertions.assertEquals(1, _statsHandler.getRequests());
        Assertions.assertEquals(1, _statsHandler.getRequestsActive());
        Assertions.assertEquals(1, _statsHandler.getRequestsActiveMax());
        Assertions.assertEquals(1, _statsHandler.getDispatched());
        Assertions.assertEquals(1, _statsHandler.getDispatchedActive());
        Assertions.assertEquals(1, _statsHandler.getDispatchedActiveMax());
        barrier[1].await();
        Assertions.assertTrue(_latchHandler.await());
        Assertions.assertEquals(1, _statsHandler.getRequests());
        Assertions.assertEquals(0, _statsHandler.getRequestsActive());
        Assertions.assertEquals(1, _statsHandler.getRequestsActiveMax());
        Assertions.assertEquals(1, _statsHandler.getDispatched());
        Assertions.assertEquals(0, _statsHandler.getDispatchedActive());
        Assertions.assertEquals(1, _statsHandler.getDispatchedActiveMax());
        Assertions.assertEquals(0, _statsHandler.getAsyncRequests());
        Assertions.assertEquals(0, _statsHandler.getAsyncDispatches());
        Assertions.assertEquals(0, _statsHandler.getExpires());
        Assertions.assertEquals(1, _statsHandler.getResponses2xx());
        _latchHandler.reset();
        barrier[0].reset();
        barrier[1].reset();
        _connector.executeRequest(request);
        barrier[0].await();
        Assertions.assertEquals(2, _statistics.getConnections());
        Assertions.assertEquals(2, _statsHandler.getRequests());
        Assertions.assertEquals(1, _statsHandler.getRequestsActive());
        Assertions.assertEquals(1, _statsHandler.getRequestsActiveMax());
        Assertions.assertEquals(2, _statsHandler.getDispatched());
        Assertions.assertEquals(1, _statsHandler.getDispatchedActive());
        Assertions.assertEquals(1, _statsHandler.getDispatchedActiveMax());
        barrier[1].await();
        Assertions.assertTrue(_latchHandler.await());
        Assertions.assertEquals(2, _statsHandler.getRequests());
        Assertions.assertEquals(0, _statsHandler.getRequestsActive());
        Assertions.assertEquals(1, _statsHandler.getRequestsActiveMax());
        Assertions.assertEquals(2, _statsHandler.getDispatched());
        Assertions.assertEquals(0, _statsHandler.getDispatchedActive());
        Assertions.assertEquals(1, _statsHandler.getDispatchedActiveMax());
        Assertions.assertEquals(0, _statsHandler.getAsyncRequests());
        Assertions.assertEquals(0, _statsHandler.getAsyncDispatches());
        Assertions.assertEquals(0, _statsHandler.getExpires());
        Assertions.assertEquals(2, _statsHandler.getResponses2xx());
    }

    @Test
    public void testTwoRequests() throws Exception {
        final CyclicBarrier[] barrier = new CyclicBarrier[]{ new CyclicBarrier(3), new CyclicBarrier(3) };
        _latchHandler.reset(2);
        _statsHandler.setHandler(new AbstractHandler() {
            @Override
            public void handle(String path, Request request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
                request.setHandled(true);
                try {
                    barrier[0].await();
                    barrier[1].await();
                } catch (Exception x) {
                    Thread.currentThread().interrupt();
                    throw new IOException(x);
                }
            }
        });
        _server.start();
        String request = "GET / HTTP/1.1\r\n" + ("Host: localhost\r\n" + "\r\n");
        _connector.executeRequest(request);
        _connector.executeRequest(request);
        barrier[0].await();
        Assertions.assertEquals(2, _statistics.getConnections());
        Assertions.assertEquals(2, _statsHandler.getRequests());
        Assertions.assertEquals(2, _statsHandler.getRequestsActive());
        Assertions.assertEquals(2, _statsHandler.getRequestsActiveMax());
        Assertions.assertEquals(2, _statsHandler.getDispatched());
        Assertions.assertEquals(2, _statsHandler.getDispatchedActive());
        Assertions.assertEquals(2, _statsHandler.getDispatchedActiveMax());
        barrier[1].await();
        Assertions.assertTrue(_latchHandler.await());
        Assertions.assertEquals(2, _statsHandler.getRequests());
        Assertions.assertEquals(0, _statsHandler.getRequestsActive());
        Assertions.assertEquals(2, _statsHandler.getRequestsActiveMax());
        Assertions.assertEquals(2, _statsHandler.getDispatched());
        Assertions.assertEquals(0, _statsHandler.getDispatchedActive());
        Assertions.assertEquals(2, _statsHandler.getDispatchedActiveMax());
        Assertions.assertEquals(0, _statsHandler.getAsyncRequests());
        Assertions.assertEquals(0, _statsHandler.getAsyncDispatches());
        Assertions.assertEquals(0, _statsHandler.getExpires());
        Assertions.assertEquals(2, _statsHandler.getResponses2xx());
    }

    @Test
    public void testSuspendResume() throws Exception {
        final long dispatchTime = 10;
        final long requestTime = 50;
        final AtomicReference<AsyncContext> asyncHolder = new AtomicReference<>();
        final CyclicBarrier[] barrier = new CyclicBarrier[]{ new CyclicBarrier(2), new CyclicBarrier(2), new CyclicBarrier(2) };
        _statsHandler.setHandler(new AbstractHandler() {
            @Override
            public void handle(String path, Request request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException {
                request.setHandled(true);
                try {
                    barrier[0].await();
                    Thread.sleep(dispatchTime);
                    if ((asyncHolder.get()) == null)
                        asyncHolder.set(request.startAsync());

                } catch (Exception x) {
                    throw new ServletException(x);
                } finally {
                    try {
                        barrier[1].await();
                    } catch (Exception ignored) {
                    }
                }
            }
        });
        _server.start();
        String request = "GET / HTTP/1.1\r\n" + ("Host: localhost\r\n" + "\r\n");
        _connector.executeRequest(request);
        barrier[0].await();
        Assertions.assertEquals(1, _statistics.getConnections());
        Assertions.assertEquals(1, _statsHandler.getRequests());
        Assertions.assertEquals(1, _statsHandler.getRequestsActive());
        Assertions.assertEquals(1, _statsHandler.getDispatched());
        Assertions.assertEquals(1, _statsHandler.getDispatchedActive());
        barrier[1].await();
        Assertions.assertTrue(_latchHandler.await());
        Assertions.assertNotNull(asyncHolder.get());
        Assertions.assertEquals(1, _statsHandler.getRequests());
        Assertions.assertEquals(1, _statsHandler.getRequestsActive());
        Assertions.assertEquals(1, _statsHandler.getDispatched());
        Assertions.assertEquals(0, _statsHandler.getDispatchedActive());
        _latchHandler.reset();
        barrier[0].reset();
        barrier[1].reset();
        Thread.sleep(requestTime);
        asyncHolder.get().addListener(new AsyncListener() {
            @Override
            public void onTimeout(AsyncEvent event) {
            }

            @Override
            public void onStartAsync(AsyncEvent event) {
            }

            @Override
            public void onError(AsyncEvent event) {
            }

            @Override
            public void onComplete(AsyncEvent event) {
                try {
                    barrier[2].await();
                } catch (Exception ignored) {
                }
            }
        });
        asyncHolder.get().dispatch();
        barrier[0].await();// entered app handler

        Assertions.assertEquals(1, _statistics.getConnections());
        Assertions.assertEquals(1, _statsHandler.getRequests());
        Assertions.assertEquals(1, _statsHandler.getRequestsActive());
        Assertions.assertEquals(2, _statsHandler.getDispatched());
        Assertions.assertEquals(1, _statsHandler.getDispatchedActive());
        barrier[1].await();// exiting app handler

        Assertions.assertTrue(_latchHandler.await());// exited stats handler

        barrier[2].await();// onComplete called

        Assertions.assertEquals(1, _statsHandler.getRequests());
        Assertions.assertEquals(0, _statsHandler.getRequestsActive());
        Assertions.assertEquals(2, _statsHandler.getDispatched());
        Assertions.assertEquals(0, _statsHandler.getDispatchedActive());
        Assertions.assertEquals(1, _statsHandler.getAsyncRequests());
        Assertions.assertEquals(1, _statsHandler.getAsyncDispatches());
        Assertions.assertEquals(0, _statsHandler.getExpires());
        Assertions.assertEquals(1, _statsHandler.getResponses2xx());
        MatcherAssert.assertThat(_statsHandler.getRequestTimeTotal(), Matchers.greaterThanOrEqualTo(((requestTime * 3) / 4)));
        Assertions.assertEquals(_statsHandler.getRequestTimeTotal(), _statsHandler.getRequestTimeMax());
        Assertions.assertEquals(_statsHandler.getRequestTimeTotal(), _statsHandler.getRequestTimeMean(), 0.01);
        MatcherAssert.assertThat(_statsHandler.getDispatchedTimeTotal(), Matchers.greaterThanOrEqualTo((((dispatchTime * 2) * 3) / 4)));
        Assertions.assertTrue((((_statsHandler.getDispatchedTimeMean()) + dispatchTime) <= (_statsHandler.getDispatchedTimeTotal())));
        Assertions.assertTrue((((_statsHandler.getDispatchedTimeMax()) + dispatchTime) <= (_statsHandler.getDispatchedTimeTotal())));
    }

    @Test
    public void testSuspendExpire() throws Exception {
        final long dispatchTime = 10;
        final long timeout = 100;
        final AtomicReference<AsyncContext> asyncHolder = new AtomicReference<>();
        final CyclicBarrier[] barrier = new CyclicBarrier[]{ new CyclicBarrier(2), new CyclicBarrier(2), new CyclicBarrier(2) };
        _statsHandler.setHandler(new AbstractHandler() {
            @Override
            public void handle(String path, Request request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException {
                request.setHandled(true);
                try {
                    barrier[0].await();
                    Thread.sleep(dispatchTime);
                    if ((asyncHolder.get()) == null) {
                        AsyncContext async = request.startAsync();
                        asyncHolder.set(async);
                        async.setTimeout(timeout);
                    }
                } catch (Exception x) {
                    throw new ServletException(x);
                } finally {
                    try {
                        barrier[1].await();
                    } catch (Exception ignored) {
                    }
                }
            }
        });
        _server.start();
        String request = "GET / HTTP/1.1\r\n" + ("Host: localhost\r\n" + "\r\n");
        _connector.executeRequest(request);
        barrier[0].await();
        Assertions.assertEquals(1, _statistics.getConnections());
        Assertions.assertEquals(1, _statsHandler.getRequests());
        Assertions.assertEquals(1, _statsHandler.getRequestsActive());
        Assertions.assertEquals(1, _statsHandler.getDispatched());
        Assertions.assertEquals(1, _statsHandler.getDispatchedActive());
        barrier[1].await();
        Assertions.assertTrue(_latchHandler.await());
        Assertions.assertNotNull(asyncHolder.get());
        asyncHolder.get().addListener(new AsyncListener() {
            @Override
            public void onTimeout(AsyncEvent event) {
                event.getAsyncContext().complete();
            }

            @Override
            public void onStartAsync(AsyncEvent event) {
            }

            @Override
            public void onError(AsyncEvent event) {
            }

            @Override
            public void onComplete(AsyncEvent event) {
                try {
                    barrier[2].await();
                } catch (Exception ignored) {
                }
            }
        });
        Assertions.assertEquals(1, _statsHandler.getRequests());
        Assertions.assertEquals(1, _statsHandler.getRequestsActive());
        Assertions.assertEquals(1, _statsHandler.getDispatched());
        Assertions.assertEquals(0, _statsHandler.getDispatchedActive());
        barrier[2].await();
        Assertions.assertEquals(1, _statsHandler.getRequests());
        Assertions.assertEquals(0, _statsHandler.getRequestsActive());
        Assertions.assertEquals(1, _statsHandler.getDispatched());
        Assertions.assertEquals(0, _statsHandler.getDispatchedActive());
        Assertions.assertEquals(1, _statsHandler.getAsyncRequests());
        Assertions.assertEquals(0, _statsHandler.getAsyncDispatches());
        Assertions.assertEquals(1, _statsHandler.getExpires());
        Assertions.assertEquals(1, _statsHandler.getResponses2xx());
        Assertions.assertTrue(((_statsHandler.getRequestTimeTotal()) >= (((timeout + dispatchTime) * 3) / 4)));
        Assertions.assertEquals(_statsHandler.getRequestTimeTotal(), _statsHandler.getRequestTimeMax());
        Assertions.assertEquals(_statsHandler.getRequestTimeTotal(), _statsHandler.getRequestTimeMean(), 0.01);
        MatcherAssert.assertThat(_statsHandler.getDispatchedTimeTotal(), Matchers.greaterThanOrEqualTo(((dispatchTime * 3) / 4)));
    }

    @Test
    public void testSuspendComplete() throws Exception {
        final long dispatchTime = 10;
        final AtomicReference<AsyncContext> asyncHolder = new AtomicReference<>();
        final CyclicBarrier[] barrier = new CyclicBarrier[]{ new CyclicBarrier(2), new CyclicBarrier(2) };
        final CountDownLatch latch = new CountDownLatch(1);
        _statsHandler.setHandler(new AbstractHandler() {
            @Override
            public void handle(String path, Request request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException {
                request.setHandled(true);
                try {
                    barrier[0].await();
                    Thread.sleep(dispatchTime);
                    if ((asyncHolder.get()) == null) {
                        AsyncContext async = request.startAsync();
                        asyncHolder.set(async);
                    }
                } catch (Exception x) {
                    throw new ServletException(x);
                } finally {
                    try {
                        barrier[1].await();
                    } catch (Exception ignored) {
                    }
                }
            }
        });
        _server.start();
        String request = "GET / HTTP/1.1\r\n" + ("Host: localhost\r\n" + "\r\n");
        _connector.executeRequest(request);
        barrier[0].await();
        Assertions.assertEquals(1, _statistics.getConnections());
        Assertions.assertEquals(1, _statsHandler.getRequests());
        Assertions.assertEquals(1, _statsHandler.getRequestsActive());
        Assertions.assertEquals(1, _statsHandler.getDispatched());
        Assertions.assertEquals(1, _statsHandler.getDispatchedActive());
        barrier[1].await();
        Assertions.assertTrue(_latchHandler.await());
        Assertions.assertNotNull(asyncHolder.get());
        Assertions.assertEquals(1, _statsHandler.getRequests());
        Assertions.assertEquals(1, _statsHandler.getRequestsActive());
        Assertions.assertEquals(1, _statsHandler.getDispatched());
        Assertions.assertEquals(0, _statsHandler.getDispatchedActive());
        asyncHolder.get().addListener(new AsyncListener() {
            @Override
            public void onTimeout(AsyncEvent event) {
            }

            @Override
            public void onStartAsync(AsyncEvent event) {
            }

            @Override
            public void onError(AsyncEvent event) {
            }

            @Override
            public void onComplete(AsyncEvent event) {
                try {
                    latch.countDown();
                } catch (Exception ignored) {
                }
            }
        });
        long requestTime = 20;
        Thread.sleep(requestTime);
        asyncHolder.get().complete();
        latch.await();
        Assertions.assertEquals(1, _statsHandler.getRequests());
        Assertions.assertEquals(0, _statsHandler.getRequestsActive());
        Assertions.assertEquals(1, _statsHandler.getDispatched());
        Assertions.assertEquals(0, _statsHandler.getDispatchedActive());
        Assertions.assertEquals(1, _statsHandler.getAsyncRequests());
        Assertions.assertEquals(0, _statsHandler.getAsyncDispatches());
        Assertions.assertEquals(0, _statsHandler.getExpires());
        Assertions.assertEquals(1, _statsHandler.getResponses2xx());
        Assertions.assertTrue(((_statsHandler.getRequestTimeTotal()) >= (((dispatchTime + requestTime) * 3) / 4)));
        Assertions.assertEquals(_statsHandler.getRequestTimeTotal(), _statsHandler.getRequestTimeMax());
        Assertions.assertEquals(_statsHandler.getRequestTimeTotal(), _statsHandler.getRequestTimeMean(), 0.01);
        Assertions.assertTrue(((_statsHandler.getDispatchedTimeTotal()) >= ((dispatchTime * 3) / 4)));
        Assertions.assertTrue(((_statsHandler.getDispatchedTimeTotal()) < (_statsHandler.getRequestTimeTotal())));
        Assertions.assertEquals(_statsHandler.getDispatchedTimeTotal(), _statsHandler.getDispatchedTimeMax());
        Assertions.assertEquals(_statsHandler.getDispatchedTimeTotal(), _statsHandler.getDispatchedTimeMean(), 0.01);
    }

    @Test
    public void testAsyncRequestWithShutdown() throws Exception {
        long delay = 500;
        CountDownLatch serverLatch = new CountDownLatch(1);
        _statsHandler.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
                AsyncContext asyncContext = request.startAsync();
                asyncContext.setTimeout(0);
                new Thread(() -> {
                    try {
                        Thread.sleep(delay);
                        asyncContext.complete();
                    } catch (InterruptedException e) {
                        response.setStatus(INTERNAL_SERVER_ERROR_500);
                        asyncContext.complete();
                    }
                }).start();
                serverLatch.countDown();
            }
        });
        _server.start();
        String request = "GET / HTTP/1.1\r\n" + ("Host: localhost\r\n" + "\r\n");
        _connector.executeRequest(request);
        Assertions.assertTrue(serverLatch.await(5, TimeUnit.SECONDS));
        Future<Void> shutdown = _statsHandler.shutdown();
        Assertions.assertFalse(shutdown.isDone());
        Thread.sleep((delay / 2));
        Assertions.assertFalse(shutdown.isDone());
        Thread.sleep(delay);
        Assertions.assertTrue(shutdown.isDone());
    }

    /**
     * This handler is external to the statistics handler and it is used to ensure that statistics handler's
     * handle() is fully executed before asserting its values in the tests, to avoid race conditions with the
     * tests' code where the test executes but the statistics handler has not finished yet.
     */
    private static class LatchHandler extends HandlerWrapper {
        private volatile CountDownLatch _latch = new CountDownLatch(1);

        @Override
        public void handle(String path, Request request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException, ServletException {
            final CountDownLatch latch = _latch;
            try {
                super.handle(path, request, httpRequest, httpResponse);
            } finally {
                latch.countDown();
            }
        }

        private void reset() {
            _latch = new CountDownLatch(1);
        }

        private void reset(int count) {
            _latch = new CountDownLatch(count);
        }

        private boolean await() throws InterruptedException {
            return _latch.await(10000, TimeUnit.MILLISECONDS);
        }
    }
}

