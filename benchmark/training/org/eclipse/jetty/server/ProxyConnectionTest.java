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


import org.eclipse.jetty.util.log.StacklessLogging;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 */
public class ProxyConnectionTest {
    private Server _server;

    private LocalConnector _connector;

    @Test
    public void testSimple() throws Exception {
        String response = _connector.getResponse(("PROXY TCP 1.2.3.4 5.6.7.8 111 222\r\n" + ((("GET /path HTTP/1.1\n" + "Host: server:80\n") + "Connection: close\n") + "\n")));
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 200"));
        MatcherAssert.assertThat(response, Matchers.containsString("pathInfo=/path"));
        MatcherAssert.assertThat(response, Matchers.containsString("local=5.6.7.8:222"));
        MatcherAssert.assertThat(response, Matchers.containsString("remote=1.2.3.4:111"));
    }

    @Test
    public void testIPv6() throws Exception {
        String response = _connector.getResponse(("PROXY UNKNOWN eeee:eeee:eeee:eeee:eeee:eeee:eeee:eeee ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff 65535 65535\r\n" + ((("GET /path HTTP/1.1\n" + "Host: server:80\n") + "Connection: close\n") + "\n")));
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 200"));
        MatcherAssert.assertThat(response, Matchers.containsString("pathInfo=/path"));
        MatcherAssert.assertThat(response, Matchers.containsString("remote=eeee:eeee:eeee:eeee:eeee:eeee:eeee:eeee:65535"));
        MatcherAssert.assertThat(response, Matchers.containsString("local=ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff:65535"));
    }

    @Test
    public void testTooLong() throws Exception {
        String response = _connector.getResponse(("PROXY TOOLONG!!! eeee:eeee:eeee:eeee:0000:0000:0000:0000 ffff:ffff:ffff:ffff:0000:0000:0000:0000 65535 65535\r\n" + ((("GET /path HTTP/1.1\n" + "Host: server:80\n") + "Connection: close\n") + "\n")));
        Assertions.assertNull(response);
    }

    @Test
    public void testNotComplete() throws Exception {
        _connector.setIdleTimeout(100);
        String response = _connector.getResponse("PROXY TIMEOUT");
        Assertions.assertNull(response);
    }

    @Test
    public void testBadChar() throws Exception {
        String response = _connector.getResponse(("PROXY\tTCP 1.2.3.4 5.6.7.8 111 222\r\n" + ((("GET /path HTTP/1.1\n" + "Host: server:80\n") + "Connection: close\n") + "\n")));
        Assertions.assertNull(response);
    }

    @Test
    public void testBadCRLF() throws Exception {
        String response = _connector.getResponse(("PROXY TCP 1.2.3.4 5.6.7.8 111 222\r \n" + ((("GET /path HTTP/1.1\n" + "Host: server:80\n") + "Connection: close\n") + "\n")));
        Assertions.assertNull(response);
    }

    @Test
    public void testBadPort() throws Exception {
        try (StacklessLogging stackless = new StacklessLogging(ProxyConnectionFactory.class)) {
            String response = _connector.getResponse(("PROXY TCP 1.2.3.4 5.6.7.8 9999999999999 222\r\n" + ((("GET /path HTTP/1.1\n" + "Host: server:80\n") + "Connection: close\n") + "\n")));
            Assertions.assertNull(response);
        }
    }

    @Test
    public void testMissingField() throws Exception {
        String response = _connector.getResponse(("PROXY TCP 1.2.3.4 5.6.7.8 222\r\n" + ((("GET /path HTTP/1.1\n" + "Host: server:80\n") + "Connection: close\n") + "\n")));
        Assertions.assertNull(response);
    }

    @Test
    public void testHTTP() throws Exception {
        String response = _connector.getResponse(("GET /path HTTP/1.1\n" + (("Host: server:80\n" + "Connection: close\n") + "\n")));
        Assertions.assertNull(response);
    }
}

