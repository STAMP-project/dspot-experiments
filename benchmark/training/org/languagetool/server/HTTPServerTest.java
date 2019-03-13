/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2006 Daniel Naber (http://www.danielnaber.de)
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
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.language.German;
import org.languagetool.language.GermanyGerman;
import org.languagetool.tools.StringTools;


public class HTTPServerTest {
    private static final int MAX_LENGTH = 50000;// needs to be in sync with server conf!


    private static final String LOAD_TEST_URL = "http://localhost:<PORT>/v2/check";

    @Test
    public void testHTTPServer() throws Exception {
        HTTPServer server = new HTTPServer(new HTTPServerConfig(HTTPTools.getDefaultPort(), true));
        Assert.assertFalse(server.isRunning());
        try {
            server.run();
            Assert.assertTrue(server.isRunning());
            runTestsV2();
            runDataTests();
        } finally {
            server.stop();
            Assert.assertFalse(server.isRunning());
        }
    }

    @Test
    public void testTimeout() {
        HTTPServerConfig config = new HTTPServerConfig(HTTPTools.getDefaultPort(), false);
        config.setMaxCheckTimeMillis(1);
        HTTPServer server = new HTTPServer(config, false);
        try {
            server.run();
            try {
                System.out.println("=== Testing timeout now, please ignore the following exception ===");
                long t = System.currentTimeMillis();
                checkV2(new GermanyGerman(), ("Einq Tesz miit fieln Fehlan, desshalb sehee laagnsam bee dr Rechtschriebp?rfung. " + "hir stet noc mer text mt nochh meh feheln. vielleict brucht es soagr nohc mehrr, damt es klapt"));
                Assert.fail(("Check was expected to be stopped because it took too long (> 1ms), it took " + (((System.currentTimeMillis()) - t) + "ms when measured from client side")));
            } catch (IOException expected) {
                if (!(expected.toString().contains(" 500 "))) {
                    Assert.fail(("Expected exception with error 500, got: " + expected));
                }
            }
        } finally {
            server.stop();
        }
    }

    @Test
    public void testHealthcheck() throws Exception {
        HTTPServerConfig config = new HTTPServerConfig(HTTPTools.getDefaultPort(), false);
        HTTPServer server = new HTTPServer(config, false);
        try {
            server.run();
            URL url = new URL("http://localhost:<PORT>/v2/healthcheck".replace("<PORT>", String.valueOf(HTTPTools.getDefaultPort())));
            InputStream stream = ((InputStream) (url.getContent()));
            String response = StringTools.streamToString(stream, "UTF-8");
            Assert.assertThat(response, Is.is("OK"));
        } finally {
            server.stop();
        }
    }

    @Test
    public void testAccessDenied() throws Exception {
        HTTPServer server = new HTTPServer(new HTTPServerConfig(HTTPTools.getDefaultPort()), false, new HashSet());
        try {
            server.run();
            try {
                System.out.println("=== Testing 'access denied' check now, please ignore the following exception ===");
                checkV1(new German(), "no ip address allowed, so this cannot work");
                Assert.fail();
            } catch (IOException expected) {
                if (!(expected.toString().contains(" 403 "))) {
                    Assert.fail(("Expected exception with error 403, got: " + expected));
                }
            }
            try {
                System.out.println("=== Testing 'access denied' check now, please ignore the following exception ===");
                checkV2(new German(), "no ip address allowed, so this cannot work");
                Assert.fail();
            } catch (IOException expected) {
                if (!(expected.toString().contains(" 403 "))) {
                    Assert.fail(("Expected exception with error 403, got: " + expected));
                }
            }
        } finally {
            server.stop();
        }
    }

    @Test
    public void testEnabledOnlyParameter() throws Exception {
        HTTPServer server = new HTTPServer(new HTTPServerConfig(HTTPTools.getDefaultPort()), false);
        try {
            server.run();
            try {
                System.out.println("=== Testing 'enabledOnly parameter' now, please ignore the following exception ===");
                URL url = new URL((("http://localhost:" + (HTTPTools.getDefaultPort())) + "/?text=foo&language=en-US&disabled=EN_A_VS_AN&enabledOnly=yes"));
                HTTPTools.checkAtUrl(url);
                Assert.fail();
            } catch (IOException expected) {
                if (!(expected.toString().contains(" 400 "))) {
                    Assert.fail(("Expected exception with error 400, got: " + expected));
                }
            }
        } finally {
            server.stop();
        }
    }

    @Test
    public void testMissingLanguageParameter() throws Exception {
        HTTPServer server = new HTTPServer(new HTTPServerConfig(HTTPTools.getDefaultPort()), false);
        try {
            server.run();
            try {
                System.out.println("=== Testing 'missing language parameter' now, please ignore the following exception ===");
                URL url = new URL((("http://localhost:" + (HTTPTools.getDefaultPort())) + "/?text=foo"));
                HTTPTools.checkAtUrl(url);
                Assert.fail();
            } catch (IOException expected) {
                if (!(expected.toString().contains(" 400 "))) {
                    Assert.fail(("Expected exception with error 400, got: " + expected));
                }
            }
        } finally {
            server.stop();
        }
    }
}

