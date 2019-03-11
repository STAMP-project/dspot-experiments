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
package io.undertow.server.protocol.ajp;


import io.undertow.Undertow;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.ProxyIgnore;
import io.undertow.util.FileUtils;
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
@ProxyIgnore
public class AjpCharacterEncodingTestCase {
    private static final int PORT = (DefaultServer.getHostPort()) + 10;

    private static Undertow undertow;

    private static OptionMap old;

    @Test
    public void sendHttpRequest() throws IOException {
        Socket socket = new Socket(DefaultServer.getHostAddress(), DefaultServer.getHostPort());
        socket.getOutputStream().write("GET /path?p=\ud55c%20\uae00 HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n".getBytes("MS949"));
        String result = FileUtils.readFile(socket.getInputStream());
        Assert.assertTrue(("Failed to find expected result \n" + result), result.contains("? ?"));
    }
}

