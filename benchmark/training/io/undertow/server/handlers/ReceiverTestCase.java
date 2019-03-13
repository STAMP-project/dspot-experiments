/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.server.handlers;


import Receiver.ErrorCallback;
import io.undertow.io.Receiver;
import io.undertow.server.HttpServerExchange;
import io.undertow.testutils.DefaultServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class ReceiverTestCase {
    public static final String HELLO_WORLD = "Hello World";

    private static final LinkedBlockingDeque<IOException> EXCEPTIONS = new LinkedBlockingDeque<>();

    public static final ErrorCallback ERROR_CALLBACK = new Receiver.ErrorCallback() {
        @Override
        public void error(HttpServerExchange exchange, IOException e) {
            ReceiverTestCase.EXCEPTIONS.add(e);
            exchange.endExchange();
        }
    };

    @Test
    public void testAsyncReceiveWholeString() {
        doTest("/fullstring");
    }

    @Test
    public void testAsyncReceivePartialString() {
        doTest("/partialstring");
    }

    @Test
    public void testAsyncReceiveWholeBytes() {
        doTest("/fullbytes");
    }

    @Test
    public void testAsyncReceiveWholeBytesFailed() throws Exception {
        ReceiverTestCase.EXCEPTIONS.clear();
        Socket socket = new Socket();
        socket.connect(DefaultServer.getDefaultServerAddress());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; ++i) {
            sb.append("hello world\r\n");
        }
        // send a large request that is too small, then kill the socket
        String request = ((("POST /fullbytes HTTP/1.1\r\nHost:localhost\r\nContent-Length:" + (sb.length())) + 100) + "\r\n\r\n") + (sb.toString());
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(request.getBytes("US-ASCII"));
        socket.getInputStream().close();
        outputStream.close();
        IOException e = ReceiverTestCase.EXCEPTIONS.poll(2, TimeUnit.SECONDS);
        Assert.assertNotNull(e);
    }

    @Test
    public void testAsyncReceivePartialBytes() {
        doTest("/partialbytes");
    }

    @Test
    public void testBlockingReceiveWholeString() {
        doTest("/fullstring?blocking");
    }

    @Test
    public void testBlockingReceivePartialString() {
        doTest("/partialstring?blocking");
    }

    @Test
    public void testBlockingReceiveWholeBytes() {
        doTest("/fullbytes?blocking");
    }

    @Test
    public void testBlockingReceivePartialBytes() {
        doTest("/partialbytes?blocking");
    }
}

