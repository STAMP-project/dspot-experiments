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
package org.apache.camel.itest.ftp;


import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class FtpInitialConnectTimeoutTest extends CamelTestSupport {
    private static final int CONNECT_TIMEOUT = 11223;

    /**
     * Create the answer for the socket factory that causes a SocketTimeoutException to occur in connect.
     */
    private static class SocketAnswer implements Answer<Socket> {
        @Override
        public Socket answer(InvocationOnMock invocation) throws Throwable {
            final Socket socket = Mockito.spy(new Socket());
            final AtomicBoolean timeout = new AtomicBoolean();
            try {
                Mockito.doAnswer(new Answer<InputStream>() {
                    @Override
                    public InputStream answer(InvocationOnMock invocation) throws Throwable {
                        final InputStream stream = ((InputStream) (invocation.callRealMethod()));
                        InputStream inputStream = new InputStream() {
                            @Override
                            public int read() throws IOException {
                                if (timeout.get()) {
                                    // emulate a timeout occurring in _getReply()
                                    throw new SocketTimeoutException();
                                }
                                return stream.read();
                            }
                        };
                        return inputStream;
                    }
                }).when(socket).getInputStream();
            } catch (IOException ignored) {
            }
            try {
                Mockito.doAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        if (((Integer) (invocation.getArguments()[0])) == (FtpInitialConnectTimeoutTest.CONNECT_TIMEOUT)) {
                            // setting of connect timeout
                            timeout.set(true);
                        } else {
                            // non-connect timeout
                            timeout.set(false);
                        }
                        return invocation.callRealMethod();
                    }
                }).when(socket).setSoTimeout(ArgumentMatchers.anyInt());
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
            return socket;
        }
    }

    private FakeFtpServer fakeFtpServer;

    @Test
    public void testReConnect() throws Exception {
        // we should fail, but we are testing that we are not in a deadlock which could potentially happen
        getMockEndpoint("mock:done").expectedMessageCount(0);
        getMockEndpoint("mock:dead").expectedMessageCount(1);
        sendBody("direct:start", "test");
        assertMockEndpointsSatisfied();
    }
}

