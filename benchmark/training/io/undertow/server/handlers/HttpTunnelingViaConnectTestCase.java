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


import io.undertow.Undertow;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.ProxyIgnore;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import org.apache.http.HttpHost;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.ProxyClient;
import org.apache.http.protocol.HTTP;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
@ProxyIgnore
public class HttpTunnelingViaConnectTestCase {
    private static Undertow server;

    @Test
    public void testConnectViaProxy() throws Exception {
        final HttpHost proxy = new HttpHost(DefaultServer.getHostAddress("default"), ((DefaultServer.getHostPort("default")) + 1), "http");
        final HttpHost target = new HttpHost(DefaultServer.getHostAddress("default"), DefaultServer.getHostPort("default"), "http");
        ProxyClient proxyClient = new ProxyClient();
        Socket socket = proxyClient.tunnel(proxy, target, new UsernamePasswordCredentials("a", "b"));
        try {
            Writer out = new OutputStreamWriter(socket.getOutputStream(), DEF_CONTENT_CHARSET);
            out.write("GET / HTTP/1.1\r\n");
            out.write((("Host: " + (target.toHostString())) + "\r\n"));
            out.write("Connection: close\r\n");
            out.write("\r\n");
            out.flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), DEF_CONTENT_CHARSET));
            String line = null;
            boolean found = false;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                if (line.equals("MyHeader: MyValue")) {
                    found = true;
                }
            } 
            Assert.assertTrue(found);
        } finally {
            socket.close();
        }
    }
}

