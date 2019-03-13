/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.jdk.connector.internal;


import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import static State.CLOSED;
import static State.CONNECTING;
import static State.CONNECT_TIMEOUT;
import static State.ERROR;
import static State.IDLE;
import static State.IDLE_TIMEOUT;
import static State.RECEIVED;
import static State.RECEIVING_BODY;
import static State.RECEIVING_HEADER;
import static State.RESPONSE_TIMEOUT;
import static State.SENDING_REQUEST;


/**
 *
 *
 * @author Petr Janouch (petr.janouch at oracle.com)
 */
public class HttpConnectionTest extends JerseyTest {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private static final Throwable testError = new Throwable();

    @Test
    public void testBasic() {
        HttpConnection[] expectedStates = new HttpConnection.State[]{ CONNECTING, IDLE, SENDING_REQUEST, RECEIVING_HEADER, RECEIVING_BODY, RECEIVED, IDLE };
        HttpRequest request = HttpRequest.createBodyless("GET", target("hello").getUri());
        doTest(HttpConnectionTest.ERROR_STATE.NONE, expectedStates, request);
    }

    @Test
    public void testMultipleRequests() {
        HttpConnection[] expectedStates = new HttpConnection.State[]{ CONNECTING, IDLE, SENDING_REQUEST, RECEIVING_HEADER, RECEIVING_BODY, RECEIVED, IDLE, SENDING_REQUEST, RECEIVING_HEADER, RECEIVING_BODY, RECEIVED, IDLE };
        HttpRequest request = HttpRequest.createBodyless("GET", target("hello").getUri());
        doTest(HttpConnectionTest.ERROR_STATE.NONE, expectedStates, request, request);
    }

    @Test
    public void testErrorSending() {
        HttpConnection[] expectedStates = new HttpConnection.State[]{ CONNECTING, IDLE, SENDING_REQUEST, ERROR, CLOSED };
        HttpRequest request = HttpRequest.createBodyless("GET", target("hello").getUri());
        doTest(HttpConnectionTest.ERROR_STATE.SENDING, expectedStates, request);
    }

    @Test
    public void testErrorReceiving() {
        HttpConnection[] expectedStates = new HttpConnection.State[]{ CONNECTING, IDLE, SENDING_REQUEST, RECEIVING_HEADER, ERROR, CLOSED };
        HttpRequest request = HttpRequest.createBodyless("GET", target("hello").getUri());
        doTest(HttpConnectionTest.ERROR_STATE.RECEIVING_HEADER, expectedStates, request);
    }

    @Test
    public void testTimeoutConnecting() {
        HttpConnection[] expectedStates = new HttpConnection.State[]{ CONNECTING, CONNECT_TIMEOUT, CLOSED };
        HttpRequest request = HttpRequest.createBodyless("GET", target("hello").getUri());
        ConnectorConfiguration configuration = new ConnectorConfiguration(client(), client().getConfiguration()) {
            @Override
            int getConnectTimeout() {
                return 100;
            }
        };
        doTest(HttpConnectionTest.ERROR_STATE.LOST_CONNECT, configuration, expectedStates, request);
    }

    @Test
    public void testResponseTimeout() {
        HttpConnection[] expectedStates = new HttpConnection.State[]{ CONNECTING, IDLE, SENDING_REQUEST, RECEIVING_HEADER, RESPONSE_TIMEOUT, CLOSED };
        HttpRequest request = HttpRequest.createBodyless("GET", target("hello").getUri());
        ConnectorConfiguration configuration = new ConnectorConfiguration(client(), client().getConfiguration()) {
            @Override
            int getResponseTimeout() {
                return 100;
            }
        };
        doTest(HttpConnectionTest.ERROR_STATE.LOST_REQUEST, configuration, expectedStates, request);
    }

    @Test
    public void testIdleTimeout() {
        HttpConnection[] expectedStates = new HttpConnection.State[]{ CONNECTING, IDLE, SENDING_REQUEST, RECEIVING_HEADER, RECEIVING_BODY, RECEIVED, IDLE, IDLE_TIMEOUT, CLOSED };
        HttpRequest request = HttpRequest.createBodyless("GET", target("hello").getUri());
        ConnectorConfiguration configuration = new ConnectorConfiguration(client(), client().getConfiguration()) {
            @Override
            int getConnectionIdleTimeout() {
                return 500;
            }
        };
        doTest(HttpConnectionTest.ERROR_STATE.NONE, configuration, expectedStates, request);
    }

    @Path("/hello")
    public static class EchoResource {
        @GET
        public String getHello() {
            return "Hello";
        }
    }

    private static class TestStateListener implements HttpConnection.StateChangeListener {
        private final List<HttpConnection.State> observedStates = new ArrayList<>();

        private final HttpRequest[] httpRequests;

        private final AtomicInteger sentRequests = new AtomicInteger(0);

        private final CountDownLatch latch;

        private final Queue<HttpConnection.State> expectedStates;

        public TestStateListener(HttpConnection[] expectedStates, CountDownLatch latch, HttpRequest... httpRequests) {
            this.httpRequests = httpRequests;
            this.latch = latch;
            this.expectedStates = new java.util.LinkedList(Arrays.asList(expectedStates));
        }

        @Override
        public void onStateChanged(HttpConnection connection, HttpConnection.State oldState, HttpConnection.State newState) {
            System.out.printf("Connection [%s] state change: %s -> %s\n", connection, oldState, newState);
            observedStates.add(newState);
            HttpConnection.State expectedState = expectedStates.poll();
            if (expectedState != newState) {
                latch.countDown();
            }
            if ((newState == (State.IDLE)) && ((httpRequests.length) > (sentRequests.get()))) {
                connection.send(httpRequests[sentRequests.get()]);
                sentRequests.incrementAndGet();
            }
            if ((expectedStates.peek()) == null) {
                latch.countDown();
            }
        }

        public List<HttpConnection.State> getObservedStates() {
            return observedStates;
        }
    }

    private static class InterceptorFilter extends Filter<HttpRequest, HttpResponse, HttpRequest, HttpResponse> {
        private final HttpConnectionTest.ERROR_STATE errorState;

        InterceptorFilter(Filter<HttpRequest, HttpResponse, HttpRequest, HttpResponse> downstreamFilter, HttpConnectionTest.ERROR_STATE errroState) {
            super(downstreamFilter);
            this.errorState = errroState;
        }

        @Override
        void write(HttpRequest data, final CompletionHandler<HttpRequest> completionHandler) {
            if ((errorState) == (HttpConnectionTest.ERROR_STATE.LOST_REQUEST)) {
                completionHandler.completed(data);
                return;
            }
            if ((errorState) == (HttpConnectionTest.ERROR_STATE.SENDING)) {
                completionHandler.failed(HttpConnectionTest.testError);
                return;
            }
            if ((errorState) == (HttpConnectionTest.ERROR_STATE.RECEIVING_HEADER)) {
                downstreamFilter.write(data, new CompletionHandler<HttpRequest>() {
                    @Override
                    public void completed(HttpRequest result) {
                        completionHandler.completed(result);
                    }
                });
                downstreamFilter.onError(HttpConnectionTest.testError);
                return;
            }
            downstreamFilter.write(data, completionHandler);
        }

        @Override
        void connect(SocketAddress address, Filter<?, ?, HttpRequest, HttpResponse> upstreamFilter) {
            if ((errorState) == (HttpConnectionTest.ERROR_STATE.LOST_CONNECT)) {
                return;
            }
            if ((errorState) == (HttpConnectionTest.ERROR_STATE.CONNECTING)) {
                return;
            }
            super.connect(address, upstreamFilter);
        }
    }

    private enum ERROR_STATE {

        NONE,
        CONNECTING,
        SENDING,
        RECEIVING_HEADER,
        LOST_REQUEST,
        LOST_CONNECT;}
}

