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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * HttpServer Tester.
 */
public abstract class ConnectorCloseTestBase extends HttpServerTestFixture {
    private static String __content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. In quis felis nunc. " + (((((((((("Quisque suscipit mauris et ante auctor ornare rhoncus lacus aliquet. Pellentesque " + "habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. ") + "Vestibulum sit amet felis augue, vel convallis dolor. Cras accumsan vehicula diam ") + "at faucibus. Etiam in urna turpis, sed congue mi. Morbi et lorem eros. Donec vulputate ") + "velit in risus suscipit lobortis. Aliquam id urna orci, nec sollicitudin ipsum. ") + "Cras a orci turpis. Donec suscipit vulputate cursus. Mauris nunc tellus, fermentum ") + "eu auctor ut, mollis at diam. Quisque porttitor ultrices metus, vitae tincidunt massa ") + "sollicitudin a. Vivamus porttitor libero eget purus hendrerit cursus. Integer aliquam ") + "consequat mauris quis luctus. Cras enim nibh, dignissim eu faucibus ac, mollis nec neque. ") + "Aliquam purus mauris, consectetur nec convallis lacinia, porta sed ante. Suspendisse ") + "et cursus magna. Donec orci enim, molestie a lobortis eu, imperdiet vitae neque.");

    private static int __length = ConnectorCloseTestBase.__content.length();

    /* ------------------------------------------------------------ */
    @Test
    public void testCloseBetweenRequests() throws Exception {
        final int requestCount = 32;
        final CountDownLatch latch = new CountDownLatch(requestCount);
        configureServer(new HttpServerTestFixture.HelloWorldHandler());
        URI uri = _server.getURI();
        Socket client = newSocket(uri.getHost(), uri.getPort());
        try {
            OutputStream os = client.getOutputStream();
            ConnectorCloseTestBase.ResponseReader reader = new ConnectorCloseTestBase.ResponseReader(client) {
                private int _index = 0;

                /* ------------------------------------------------------------ */
                @Override
                protected int doRead() throws IOException, InterruptedException {
                    int count = super.doRead();
                    if (count > 0) {
                        int idx;
                        while ((idx = _response.indexOf("HTTP/1.1 200 OK", _index)) >= 0) {
                            latch.countDown();
                            _index = idx + 15;
                        } 
                    }
                    return count;
                }
            };
            Thread runner = new Thread(reader);
            runner.start();
            for (int pipeline = 1; pipeline <= requestCount; pipeline++) {
                if (pipeline == (requestCount / 2)) {
                    // wait for at least 1 request to have been received
                    if ((latch.getCount()) == requestCount)
                        Thread.sleep(1);

                    _connector.close();
                }
                String request = (((((((((("GET /data?writes=1&block=16&id=" + pipeline) + " HTTP/1.1\r\n") + "host: ") + (uri.getHost())) + ":") + (uri.getPort())) + "\r\n") + "user-agent: testharness/1.0 (blah foo/bar)\r\n") + "accept-encoding: nothing\r\n") + "cookie: aaa=1234567890\r\n") + "\r\n";
                os.write(request.getBytes());
                os.flush();
                Thread.sleep(25);
            }
            Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
            reader.setDone();
            runner.join();
        } finally {
            client.close();
        }
    }

    /* ------------------------------------------------------------ */
    @Test
    public void testCloseBetweenChunks() throws Exception {
        configureServer(new HttpServerTestFixture.EchoHandler());
        URI uri = _server.getURI();
        Socket client = newSocket(uri.getHost(), uri.getPort());
        try {
            OutputStream os = client.getOutputStream();
            ConnectorCloseTestBase.ResponseReader reader = new ConnectorCloseTestBase.ResponseReader(client);
            Thread runner = new Thread(reader);
            runner.start();
            byte[] bytes = ConnectorCloseTestBase.__content.getBytes("utf-8");
            os.write((((((((((("POST /echo?charset=utf-8 HTTP/1.1\r\n" + "host: ") + (uri.getHost())) + ":") + (uri.getPort())) + "\r\n") + "content-type: text/plain; charset=utf-8\r\n") + "content-length: ") + (bytes.length)) + "\r\n") + "\r\n").getBytes(StandardCharsets.ISO_8859_1));
            int len = bytes.length;
            int offset = 0;
            int stop = len / 2;
            while (offset < stop) {
                os.write(bytes, offset, 64);
                offset += 64;
                Thread.sleep(25);
            } 
            _connector.close();
            while (offset < len) {
                os.write(bytes, offset, ((len - offset) <= 64 ? len - offset : 64));
                offset += 64;
                Thread.sleep(25);
            } 
            os.flush();
            reader.setDone();
            runner.join();
            String in = reader.getResponse().toString();
            Assertions.assertTrue(((in.indexOf(ConnectorCloseTestBase.__content.substring(((ConnectorCloseTestBase.__length) - 64)))) > 0));
        } finally {
            client.close();
        }
    }

    /* ------------------------------------------------------------ */
    public class ResponseReader implements Runnable {
        private boolean _done = false;

        protected char[] _buffer;

        protected StringBuffer _response;

        protected BufferedReader _reader;

        /* ------------------------------------------------------------ */
        public ResponseReader(Socket client) throws IOException {
            _buffer = new char[256];
            _response = new StringBuffer();
            _reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        }

        /* ------------------------------------------------------------ */
        public void setDone() {
            _done = true;
        }

        public StringBuffer getResponse() {
            return _response;
        }

        /* ------------------------------------------------------------ */
        /**
         *
         *
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            try {
                int count = 0;
                while ((!(_done)) || (count > 0)) {
                    count = doRead();
                } 
            } catch (IOException ex) {
            } catch (InterruptedException ex) {
            } finally {
                try {
                    _reader.close();
                } catch (IOException e) {
                }
            }
        }

        /* ------------------------------------------------------------ */
        protected int doRead() throws IOException, InterruptedException {
            if (!(_reader.ready())) {
                Thread.sleep(25);
            }
            int count = 0;
            if (_reader.ready()) {
                count = _reader.read(_buffer);
                if (count > 0) {
                    _response.append(_buffer, 0, count);
                }
            }
            return count;
        }
    }
}

