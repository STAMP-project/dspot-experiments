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
package org.eclipse.jetty.client;


import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.FutureResponseListener;
import org.eclipse.jetty.util.Callback;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class HttpClientChunkedContentTest {
    private HttpClient client;

    @Test
    public void test_Server_HeadersPauseTerminal_Client_Response() throws Exception {
        startClient();
        try (ServerSocket server = new ServerSocket()) {
            server.bind(new InetSocketAddress("localhost", 0));
            final AtomicReference<Result> resultRef = new AtomicReference<>();
            final CountDownLatch completeLatch = new CountDownLatch(1);
            client.newRequest("localhost", server.getLocalPort()).timeout(5, TimeUnit.SECONDS).send(new Response.CompleteListener() {
                @Override
                public void onComplete(Result result) {
                    resultRef.set(result);
                    completeLatch.countDown();
                }
            });
            try (Socket socket = server.accept()) {
                consumeRequestHeaders(socket);
                OutputStream output = socket.getOutputStream();
                String headers = "" + (("HTTP/1.1 200 OK\r\n" + "Transfer-Encoding: chunked\r\n") + "\r\n");
                output.write(headers.getBytes(StandardCharsets.UTF_8));
                output.flush();
                Thread.sleep(1000);
                String terminal = "" + ("0\r\n" + "\r\n");
                output.write(terminal.getBytes(StandardCharsets.UTF_8));
                output.flush();
                Assertions.assertTrue(completeLatch.await(5, TimeUnit.SECONDS));
                Result result = resultRef.get();
                Assertions.assertTrue(result.isSucceeded());
                Response response = result.getResponse();
                Assertions.assertEquals(200, response.getStatus());
            }
        }
    }

    @Test
    public void test_Server_ContentTerminal_Client_ContentDelay() throws Exception {
        startClient();
        try (ServerSocket server = new ServerSocket()) {
            server.bind(new InetSocketAddress("localhost", 0));
            final AtomicReference<Callback> callbackRef = new AtomicReference<>();
            final CountDownLatch firstContentLatch = new CountDownLatch(1);
            final AtomicReference<Result> resultRef = new AtomicReference<>();
            final CountDownLatch completeLatch = new CountDownLatch(1);
            client.newRequest("localhost", server.getLocalPort()).onResponseContentAsync(new Response.AsyncContentListener() {
                @Override
                public void onContent(Response response, ByteBuffer content, Callback callback) {
                    if (callbackRef.compareAndSet(null, callback))
                        firstContentLatch.countDown();
                    else
                        callback.succeeded();

                }
            }).timeout(5, TimeUnit.SECONDS).send(new Response.CompleteListener() {
                @Override
                public void onComplete(Result result) {
                    resultRef.set(result);
                    completeLatch.countDown();
                }
            });
            try (Socket socket = server.accept()) {
                consumeRequestHeaders(socket);
                OutputStream output = socket.getOutputStream();
                String response = "" + (((((("HTTP/1.1 200 OK\r\n" + "Transfer-Encoding: chunked\r\n") + "\r\n") + "8\r\n") + "01234567\r\n") + "0\r\n") + "\r\n");
                output.write(response.getBytes(StandardCharsets.UTF_8));
                output.flush();
                // Simulate a delay in consuming the content.
                Assertions.assertTrue(firstContentLatch.await(5, TimeUnit.SECONDS));
                Thread.sleep(1000);
                callbackRef.get().succeeded();
                // Wait for the client to read 0 and become idle.
                Thread.sleep(1000);
                Assertions.assertTrue(completeLatch.await(5, TimeUnit.SECONDS));
                Result result = resultRef.get();
                Assertions.assertTrue(result.isSucceeded());
                Assertions.assertEquals(200, result.getResponse().getStatus());
                // Issue another request to be sure the connection is sane.
                Request request = client.newRequest("localhost", server.getLocalPort()).timeout(5, TimeUnit.SECONDS);
                FutureResponseListener listener = new FutureResponseListener(request);
                request.send(listener);
                consumeRequestHeaders(socket);
                output.write(response.getBytes(StandardCharsets.UTF_8));
                output.flush();
                Assertions.assertEquals(200, listener.get(5, TimeUnit.SECONDS).getStatus());
            }
        }
    }
}

