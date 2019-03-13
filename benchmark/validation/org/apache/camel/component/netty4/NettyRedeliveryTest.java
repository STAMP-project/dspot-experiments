/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.netty4;


import java.io.IOException;
import java.net.Socket;
import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * Test the effect of redelivery in association with netty component.
 */
public class NettyRedeliveryTest extends CamelTestSupport {
    /**
     * Body of sufficient size such that it doesn't fit into the TCP buffer and has to be read.
     */
    private static final byte[] LARGE_BUFFER_BODY = new byte[1000000];

    /**
     * Failure will occur with 2 redeliveries however is increasingly more likely the more it retries.
     */
    private static final int REDELIVERY_COUNT = 100;

    private ExecutorService listener = Executors.newSingleThreadExecutor();

    @EndpointInject(uri = "mock:exception")
    private MockEndpoint exception;

    @EndpointInject(uri = "mock:downstream")
    private MockEndpoint downstream;

    private Deque<Object> tasks = new LinkedBlockingDeque<>();

    private int port;

    private boolean alive = true;

    @Test
    public void testExceptionHandler() throws Exception {
        /* We should have 0 for this as it should never be successful however it is usual that this actually returns 1.

        This is because two or more threads run concurrently and will setException(null) which is checked during
        redelivery to ascertain whether the delivery was successful, this leads to multiple downstream invocations being
        possible.
         */
        downstream.setExpectedMessageCount(0);
        downstream.setAssertPeriod(1000);
        exception.setExpectedMessageCount(1);
        sendBody("direct:start", NettyRedeliveryTest.LARGE_BUFFER_BODY);
        exception.assertIsSatisfied();
        // given 100 retries usually yields somewhere around -95
        // assertEquals(0, context.getInflightRepository().size("start"));
        // Verify the number of tasks submitted - sometimes both callbacks add a task
        assertEquals(NettyRedeliveryTest.REDELIVERY_COUNT, tasks.size());
        // Verify the downstream completed messages - othertimes one callback gets treated as done
        downstream.assertIsSatisfied();
    }

    /**
     * Handler for client connection.
     */
    private class ClosingClientRunnable implements Runnable {
        private final Socket socket;

        ClosingClientRunnable(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(10);
                socket.close();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}

