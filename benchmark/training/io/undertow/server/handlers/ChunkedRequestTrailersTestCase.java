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


import io.undertow.server.ServerConnection;
import io.undertow.testutils.AjpIgnore;
import io.undertow.testutils.DefaultServer;
import java.io.IOException;
import java.net.Socket;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xnio.OptionMap;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
@AjpIgnore
public class ChunkedRequestTrailersTestCase {
    private static volatile ServerConnection connection;

    private static OptionMap existing;

    /**
     * We send our request manually, as apache HTTP client does not support this.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testChunkedRequestsWithTrailers() throws IOException {
        ChunkedRequestTrailersTestCase.connection = null;
        String request = "POST / HTTP/1.1\r\nHost: default\r\nTrailer:foo, bar\r\nTransfer-Encoding: chunked\r\n\r\n9\r\nabcdefghi\r\n0\r\nfoo: fooVal\r\n bar: barVal\r\n\r\n";
        String response1 = "HTTP/1.1 200 OK\r\nConnection: keep-alive\r\nContent-Length: 26\r\n\r\nfoo: fooVal\r\nbar: barVal\r\n";// header order is not guaranteed, we really should be parsing this properly

        String response2 = "HTTP/1.1 200 OK\r\nConnection: keep-alive\r\nContent-Length: 26\r\n\r\nfoo: fooVal\r\nbar: barVal\r\n";// TODO: parse the response properly, or better yet ues a client that supports trailers

        Socket s = new Socket(DefaultServer.getDefaultServerAddress().getAddress(), DefaultServer.getDefaultServerAddress().getPort());
        try {
            s.getOutputStream().write(request.getBytes());
            StringBuilder sb = new StringBuilder();
            int read = 0;
            byte[] buf = new byte[300];
            while (read < (response1.length())) {
                int r = s.getInputStream().read(buf);
                if (r <= 0)
                    break;

                if (r > 0) {
                    read += r;
                    sb.append(new String(buf, 0, r));
                }
            } 
            String actual = sb.toString();
            actual = actual.replaceAll("\r\nDate:.*", "");
            actual = actual.replaceAll("content-length", "Content-Length");
            try {
                // this is pretty yuck
                Assert.assertEquals(response1, actual);
            } catch (AssertionError e) {
                Assert.assertEquals(response2, actual);
            }
            s.getOutputStream().write(request.getBytes());
            sb = new StringBuilder();
            read = 0;
            while (read < (response1.length())) {
                int r = s.getInputStream().read(buf);
                if (r <= 0)
                    break;

                if (r > 0) {
                    read += r;
                    sb.append(new String(buf, 0, r));
                }
            } 
            actual = sb.toString();
            actual = actual.replaceAll("\r\nDate:.*", "");
            actual = actual.replaceAll("content-length", "Content-Length");
            try {
                Assert.assertEquals(response1, actual);
            } catch (AssertionError e) {
                Assert.assertEquals(response2, actual);
            }
        } finally {
            s.close();
        }
    }
}

