/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2012 Daniel Naber (http://www.danielnaber.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.server;


import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.language.German;
import org.languagetool.language.GermanyGerman;

import static HTTPServerConfig.DEFAULT_HOST;


public class HTTPSServerTest {
    private static final String KEYSTORE = "/org/languagetool/server/test-keystore.jks";

    private static final String KEYSTORE_PASSWORD = "mytest";

    @Test
    public void runRequestLimitationTest() throws Exception {
        HTTPTools.disableCertChecks();
        HTTPSServerConfig serverConfig = new HTTPSServerConfig(HTTPTools.getDefaultPort(), false, getKeystoreFile(), HTTPSServerTest.KEYSTORE_PASSWORD, 2, 120);
        serverConfig.setBlockedReferrers(Arrays.asList("http://foo.org", "bar.org"));
        HTTPSServer server = new HTTPSServer(serverConfig, false, DEFAULT_HOST, null);
        try {
            server.run();
            check(new GermanyGerman(), "foo");
            check(new GermanyGerman(), "foo");
            try {
                System.out.println("=== Testing too many requests now, please ignore the following error ===");
                String result = check(new German(), "foo");
                Assert.fail((("Expected exception not thrown, got this result instead: '" + result) + "'"));
            } catch (IOException ignored) {
            }
        } finally {
            server.stop();
        }
    }

    @Test
    public void runReferrerLimitationTest() throws Exception {
        HTTPTools.disableCertChecks();
        HTTPSServerConfig serverConfig = new HTTPSServerConfig(HTTPTools.getDefaultPort(), false, getKeystoreFile(), HTTPSServerTest.KEYSTORE_PASSWORD);
        serverConfig.setBlockedReferrers(Arrays.asList("http://foo.org", "bar.org"));
        HTTPSServer server = new HTTPSServer(serverConfig, false, DEFAULT_HOST, null);
        try {
            server.run();
            HashMap<String, String> map = new HashMap<>();
            URL url = new URL((("https://localhost:" + (HTTPTools.getDefaultPort())) + "/v2/check"));
            try {
                map.put("Referer", "http://foo.org/myref");
                HTTPTools.checkAtUrlByPost(url, "language=en&text=a test", map);
                Assert.fail("Request should fail because of blocked referrer");
            } catch (IOException ignored) {
            }
            try {
                map.put("Referer", "http://bar.org/myref");
                HTTPTools.checkAtUrlByPost(url, "language=en&text=a test", map);
                Assert.fail("Request should fail because of blocked referrer");
            } catch (IOException ignored) {
            }
            try {
                map.put("Referer", "https://bar.org/myref");
                HTTPTools.checkAtUrlByPost(url, "language=en&text=a test", map);
                Assert.fail("Request should fail because of blocked referrer");
            } catch (IOException ignored) {
            }
            try {
                map.put("Referer", "https://www.bar.org/myref");
                HTTPTools.checkAtUrlByPost(url, "language=en&text=a test", map);
                Assert.fail("Request should fail because of blocked referrer");
            } catch (IOException ignored) {
            }
            map.put("Referer", "https://www.something-else.org/myref");
            HTTPTools.checkAtUrlByPost(url, "language=en&text=a test", map);
        } finally {
            server.stop();
        }
    }

    @Test
    public void testHTTPSServer() throws Exception {
        HTTPTools.disableCertChecks();
        HTTPSServerConfig config = new HTTPSServerConfig(HTTPTools.getDefaultPort(), false, getKeystoreFile(), HTTPSServerTest.KEYSTORE_PASSWORD);
        config.setMaxTextLength(500);
        HTTPSServer server = new HTTPSServer(config, false, DEFAULT_HOST, null);
        try {
            server.run();
            runTests();
        } finally {
            server.stop();
        }
    }
}

