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
package org.eclipse.jetty.http2.client.http;


import HttpMethod.GET;
import HttpStatus.OK_200;
import Stream.Listener;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.client.HttpRequest;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http2.api.Stream;
import org.eclipse.jetty.http2.api.server.ServerSessionListener;
import org.eclipse.jetty.http2.frames.HeadersFrame;
import org.eclipse.jetty.http2.frames.ResetFrame;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.Promise;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class PushedResourcesTest extends AbstractTest {
    @Test
    public void testPushedResourceCancelled() throws Exception {
        String pushPath = "/secondary";
        CountDownLatch latch = new CountDownLatch(1);
        start(new ServerSessionListener.Adapter() {
            @Override
            public Listener onNewStream(Stream stream, HeadersFrame frame) {
                HttpURI pushURI = new HttpURI((("http://localhost:" + (connector.getLocalPort())) + pushPath));
                MetaData.Request pushRequest = new MetaData.Request(GET.asString(), pushURI, HttpVersion.HTTP_2, new HttpFields());
                stream.push(new org.eclipse.jetty.http2.frames.PushPromiseFrame(stream.getId(), 0, pushRequest), new Promise.Adapter<Stream>() {
                    @Override
                    public void succeeded(Stream pushStream) {
                        // Just send the normal response and wait for the reset.
                        MetaData.Response response = new MetaData.Response(HttpVersion.HTTP_2, HttpStatus.OK_200, new HttpFields());
                        stream.headers(new HeadersFrame(stream.getId(), response, null, true), Callback.NOOP);
                    }
                }, new Stream.Listener.Adapter() {
                    @Override
                    public void onReset(Stream stream, ResetFrame frame) {
                        latch.countDown();
                    }
                });
                return null;
            }
        });
        HttpRequest request = ((HttpRequest) (client.newRequest("localhost", connector.getLocalPort())));
        ContentResponse response = request.pushListener(( mainRequest, pushedRequest) -> null).timeout(5, TimeUnit.SECONDS).send();
        Assertions.assertEquals(OK_200, response.getStatus());
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void testPushedResources() throws Exception {
        Random random = new Random();
        byte[] bytes = new byte[512];
        random.nextBytes(bytes);
        byte[] pushBytes1 = new byte[1024];
        random.nextBytes(pushBytes1);
        byte[] pushBytes2 = new byte[2048];
        random.nextBytes(pushBytes2);
        String path1 = "/secondary1";
        String path2 = "/secondary2";
        start(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                baseRequest.setHandled(true);
                if (target.equals(path1)) {
                    response.getOutputStream().write(pushBytes1);
                } else
                    if (target.equals(path2)) {
                        response.getOutputStream().write(pushBytes2);
                    } else {
                        baseRequest.getPushBuilder().path(path1).push();
                        baseRequest.getPushBuilder().path(path2).push();
                        response.getOutputStream().write(bytes);
                    }

            }
        });
        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);
        HttpRequest request = ((HttpRequest) (client.newRequest("localhost", connector.getLocalPort())));
        ContentResponse response = request.pushListener(( mainRequest, pushedRequest) -> new BufferingResponseListener() {
            @Override
            public void onComplete(Result result) {
                assertTrue(result.isSucceeded());
                if (pushedRequest.getPath().equals(path1)) {
                    assertArrayEquals(pushBytes1, getContent());
                    latch1.countDown();
                } else
                    if (pushedRequest.getPath().equals(path2)) {
                        assertArrayEquals(pushBytes2, getContent());
                        latch2.countDown();
                    }

            }
        }).timeout(5, TimeUnit.SECONDS).send();
        Assertions.assertEquals(OK_200, response.getStatus());
        Assertions.assertArrayEquals(bytes, response.getContent());
        Assertions.assertTrue(latch1.await(5, TimeUnit.SECONDS));
        Assertions.assertTrue(latch2.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void testPushedResourceRedirect() throws Exception {
        Random random = new Random();
        byte[] pushBytes = new byte[512];
        random.nextBytes(pushBytes);
        String oldPath = "/old";
        String newPath = "/new";
        start(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                baseRequest.setHandled(true);
                if (target.equals(oldPath))
                    response.sendRedirect(newPath);
                else
                    if (target.equals(newPath))
                        response.getOutputStream().write(pushBytes);
                    else
                        baseRequest.getPushBuilder().path(oldPath).push();


            }
        });
        CountDownLatch latch = new CountDownLatch(1);
        HttpRequest request = ((HttpRequest) (client.newRequest("localhost", connector.getLocalPort())));
        ContentResponse response = request.pushListener(( mainRequest, pushedRequest) -> new BufferingResponseListener() {
            @Override
            public void onComplete(Result result) {
                assertTrue(result.isSucceeded());
                assertEquals(oldPath, pushedRequest.getPath());
                assertEquals(newPath, result.getRequest().getPath());
                assertArrayEquals(pushBytes, getContent());
                latch.countDown();
            }
        }).timeout(5, TimeUnit.SECONDS).send();
        Assertions.assertEquals(OK_200, response.getStatus());
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
    }
}

