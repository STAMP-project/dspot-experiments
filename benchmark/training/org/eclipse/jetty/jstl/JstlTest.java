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
package org.eclipse.jetty.jstl;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import javax.servlet.jsp.JspException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.IO;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class JstlTest {
    private static Server server;

    private static URI baseUri;

    @Test
    public void testUrlsBasic() throws IOException {
        HttpURLConnection http = ((HttpURLConnection) (JstlTest.baseUri.resolve("/urls.jsp").toURL().openConnection()));
        MatcherAssert.assertThat("http response", http.getResponseCode(), Matchers.is(200));
        try (InputStream input = http.getInputStream()) {
            String resp = IO.toString(input, StandardCharsets.UTF_8);
            MatcherAssert.assertThat("Response should be JSP processed", resp, Matchers.not(Matchers.containsString("<c:url")));
            MatcherAssert.assertThat("Response", resp, Matchers.containsString("[c:url value] = /ref.jsp;jsessionid="));
            MatcherAssert.assertThat("Response", resp, Matchers.containsString("[c:url param] = ref.jsp;key=value;jsessionid="));
        }
    }

    @Test
    public void testCatchBasic() throws IOException {
        HttpURLConnection http = ((HttpURLConnection) (JstlTest.baseUri.resolve("/catch-basic.jsp").toURL().openConnection()));
        MatcherAssert.assertThat("http response", http.getResponseCode(), Matchers.is(200));
        try (InputStream input = http.getInputStream()) {
            String resp = IO.toString(input, StandardCharsets.UTF_8);
            MatcherAssert.assertThat("Response should be JSP processed", resp, Matchers.not(Matchers.containsString("<c:catch")));
            MatcherAssert.assertThat("Response", resp, Matchers.containsString(("[c:catch] exception : " + (JspException.class.getName()))));
            MatcherAssert.assertThat("Response", resp, Matchers.containsString("[c:catch] exception.message : In &lt;parseNumber&gt;"));
        }
    }

    @Test
    public void testCatchTaglib() throws IOException {
        HttpURLConnection http = ((HttpURLConnection) (JstlTest.baseUri.resolve("/catch-taglib.jsp").toURL().openConnection()));
        MatcherAssert.assertThat("http response", http.getResponseCode(), Matchers.is(200));
        try (InputStream input = http.getInputStream()) {
            String resp = IO.toString(input, StandardCharsets.UTF_8);
            MatcherAssert.assertThat("Response should be JSP processed", resp, Matchers.not(Matchers.containsString("<c:catch>")));
            MatcherAssert.assertThat("Response should be JSP processed", resp, Matchers.not(Matchers.containsString("<jtest:errorhandler>")));
            MatcherAssert.assertThat("Response", resp, Matchers.not(Matchers.containsString("[jtest:errorhandler] exception is null")));
        }
    }
}

