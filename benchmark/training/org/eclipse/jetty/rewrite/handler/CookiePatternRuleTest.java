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
package org.eclipse.jetty.rewrite.handler;


import HttpHeader.SET_COOKIE;
import HttpTester.Response;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


public class CookiePatternRuleTest {
    private Server server;

    private LocalConnector localConnector;

    @Test
    public void testSetAlready() throws Exception {
        CookiePatternRule rule = new CookiePatternRule();
        rule.setPattern("*");
        rule.setName("set");
        rule.setValue("already");
        startServer(rule);
        StringBuilder rawRequest = new StringBuilder();
        rawRequest.append("GET / HTTP/1.1\r\n");
        rawRequest.append("Host: local\r\n");
        rawRequest.append("Connection: close\r\n");
        rawRequest.append("Cookie: set=already\r\n");// already present on request

        rawRequest.append("\r\n");
        String rawResponse = localConnector.getResponse(rawRequest.toString());
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        // verify
        MatcherAssert.assertThat("response should not have Set-Cookie", response.getField(SET_COOKIE), nullValue());
    }
}

