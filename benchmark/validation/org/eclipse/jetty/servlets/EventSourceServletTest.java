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
package org.eclipse.jetty.servlets;


import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class EventSourceServletTest {
    private Server server;

    private NetworkConnector connector;

    private ServletContextHandler context;

    @Test
    public void testBasicFunctionality() throws Exception {
        final AtomicReference<EventSource.Emitter> emitterRef = new AtomicReference<EventSource.Emitter>();
        final CountDownLatch emitterLatch = new CountDownLatch(1);
        final CountDownLatch closeLatch = new CountDownLatch(1);
        class S extends EventSourceServlet {
            @Override
            protected EventSource newEventSource(HttpServletRequest request) {
                return new EventSource() {
                    @Override
                    public void onOpen(Emitter emitter) throws IOException {
                        emitterRef.set(emitter);
                        emitterLatch.countDown();
                    }

                    @Override
                    public void onClose() {
                        closeLatch.countDown();
                    }
                };
            }
        }
        String servletPath = "/eventsource";
        ServletHolder servletHolder = new ServletHolder(new S());
        int heartBeatPeriod = 2;
        servletHolder.setInitParameter("heartBeatPeriod", String.valueOf(heartBeatPeriod));
        context.addServlet(servletHolder, servletPath);
        Socket socket = new Socket("localhost", connector.getLocalPort());
        writeHTTPRequest(socket, servletPath);
        BufferedReader reader = readAndDiscardHTTPResponse(socket);
        Assertions.assertTrue(emitterLatch.await(1, TimeUnit.SECONDS));
        EventSource.Emitter emitter = emitterRef.get();
        Assertions.assertNotNull(emitter);
        String data = "foo";
        emitter.data(data);
        String line = reader.readLine();
        String received = "";
        while (line != null) {
            received += line;
            if ((line.length()) == 0)
                break;

            line = reader.readLine();
        } 
        Assertions.assertEquals(("data: " + data), received);
        socket.close();
        Assertions.assertTrue(closeLatch.await((heartBeatPeriod * 3), TimeUnit.SECONDS));
    }

    @Test
    public void testServerSideClose() throws Exception {
        final AtomicReference<EventSource.Emitter> emitterRef = new AtomicReference<EventSource.Emitter>();
        final CountDownLatch emitterLatch = new CountDownLatch(1);
        class S extends EventSourceServlet {
            @Override
            protected EventSource newEventSource(HttpServletRequest request) {
                return new EventSource() {
                    @Override
                    public void onOpen(Emitter emitter) throws IOException {
                        emitterRef.set(emitter);
                        emitterLatch.countDown();
                    }

                    @Override
                    public void onClose() {
                    }
                };
            }
        }
        String servletPath = "/eventsource";
        context.addServlet(new ServletHolder(new S()), servletPath);
        Socket socket = new Socket("localhost", connector.getLocalPort());
        writeHTTPRequest(socket, servletPath);
        BufferedReader reader = readAndDiscardHTTPResponse(socket);
        Assertions.assertTrue(emitterLatch.await(1, TimeUnit.SECONDS));
        EventSource.Emitter emitter = emitterRef.get();
        Assertions.assertNotNull(emitter);
        String comment = "foo";
        emitter.comment(comment);
        String line = reader.readLine();
        String received = "";
        while (line != null) {
            received += line;
            if ((line.length()) == 0)
                break;

            line = reader.readLine();
        } 
        Assertions.assertEquals((": " + comment), received);
        emitter.close();
        line = reader.readLine();
        Assertions.assertNull(line);
        socket.close();
    }

    @Test
    public void testEncoding() throws Exception {
        // The EURO symbol
        final String data = "\u20ac";
        class S extends EventSourceServlet {
            @Override
            protected EventSource newEventSource(HttpServletRequest request) {
                return new EventSource() {
                    @Override
                    public void onOpen(Emitter emitter) throws IOException {
                        emitter.data(data);
                    }

                    @Override
                    public void onClose() {
                    }
                };
            }
        }
        String servletPath = "/eventsource";
        context.addServlet(new ServletHolder(new S()), servletPath);
        Socket socket = new Socket("localhost", connector.getLocalPort());
        writeHTTPRequest(socket, servletPath);
        BufferedReader reader = readAndDiscardHTTPResponse(socket);
        String line = reader.readLine();
        String received = "";
        while (line != null) {
            received += line;
            if ((line.length()) == 0)
                break;

            line = reader.readLine();
        } 
        Assertions.assertEquals(("data: " + data), received);
        socket.close();
    }

    @Test
    public void testMultiLineData() throws Exception {
        String data1 = "data1";
        String data2 = "data2";
        String data3 = "data3";
        String data4 = "data4";
        final String data = (((((data1 + "\r\n") + data2) + "\r") + data3) + "\n") + data4;
        class S extends EventSourceServlet {
            @Override
            protected EventSource newEventSource(HttpServletRequest request) {
                return new EventSource() {
                    @Override
                    public void onOpen(Emitter emitter) throws IOException {
                        emitter.data(data);
                    }

                    @Override
                    public void onClose() {
                    }
                };
            }
        }
        String servletPath = "/eventsource";
        context.addServlet(new ServletHolder(new S()), servletPath);
        Socket socket = new Socket("localhost", connector.getLocalPort());
        writeHTTPRequest(socket, servletPath);
        BufferedReader reader = readAndDiscardHTTPResponse(socket);
        String line1 = reader.readLine();
        Assertions.assertEquals(("data: " + data1), line1);
        String line2 = reader.readLine();
        Assertions.assertEquals(("data: " + data2), line2);
        String line3 = reader.readLine();
        Assertions.assertEquals(("data: " + data3), line3);
        String line4 = reader.readLine();
        Assertions.assertEquals(("data: " + data4), line4);
        String line5 = reader.readLine();
        Assertions.assertEquals(0, line5.length());
        socket.close();
    }

    @Test
    public void testEvents() throws Exception {
        final String name = "event1";
        final String data = "data2";
        class S extends EventSourceServlet {
            @Override
            protected EventSource newEventSource(HttpServletRequest request) {
                return new EventSource() {
                    @Override
                    public void onOpen(Emitter emitter) throws IOException {
                        emitter.event(name, data);
                    }

                    @Override
                    public void onClose() {
                    }
                };
            }
        }
        String servletPath = "/eventsource";
        context.addServlet(new ServletHolder(new S()), servletPath);
        Socket socket = new Socket("localhost", connector.getLocalPort());
        writeHTTPRequest(socket, servletPath);
        BufferedReader reader = readAndDiscardHTTPResponse(socket);
        String line1 = reader.readLine();
        Assertions.assertEquals(("event: " + name), line1);
        String line2 = reader.readLine();
        Assertions.assertEquals(("data: " + data), line2);
        String line3 = reader.readLine();
        Assertions.assertEquals(0, line3.length());
        socket.close();
    }
}

