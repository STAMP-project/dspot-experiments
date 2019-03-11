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
package org.eclipse.jetty.maven.plugin.it;


import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.client.HttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 */
public class TestGetContent {
    @Test
    public void get_content_response() throws Exception {
        int port = TestGetContent.getPort();
        Assertions.assertTrue((port > 0));
        HttpClient httpClient = new HttpClient();
        try {
            httpClient.start();
            if (Boolean.getBoolean("helloServlet")) {
                String response = httpClient.GET((("http://localhost:" + port) + "/hello?name=beer")).getContentAsString();
                Assertions.assertEquals("Hello beer", response.trim(), ("it test " + (System.getProperty("maven.it.name"))));
                response = httpClient.GET((("http://localhost:" + port) + "/hello?name=foo")).getContentAsString();
                Assertions.assertEquals("Hello foo", response.trim(), ("it test " + (System.getProperty("maven.it.name"))));
                System.out.println("helloServlet");
            }
            if (Boolean.getBoolean("pingServlet")) {
                System.out.println("pingServlet");
                String response = httpClient.GET((("http://localhost:" + port) + "/ping?name=beer")).getContentAsString();
                Assertions.assertEquals("pong beer", response.trim(), ("it test " + (System.getProperty("maven.it.name"))));
                System.out.println("pingServlet ok");
            }
            String contentCheck = System.getProperty("contentCheck");
            String pathToCheck = System.getProperty("pathToCheck");
            if (StringUtils.isNotBlank(contentCheck)) {
                String url = "http://localhost:" + port;
                if (pathToCheck != null) {
                    url += pathToCheck;
                }
                String response = httpClient.GET(url).getContentAsString();
                Assertions.assertTrue(response.contains(contentCheck), ((((("it test " + (System.getProperty("maven.it.name"))) + ", response not contentCheck: ") + contentCheck) + ", response:") + response));
                System.out.println("contentCheck");
            }
        } finally {
            httpClient.stop();
        }
    }
}

