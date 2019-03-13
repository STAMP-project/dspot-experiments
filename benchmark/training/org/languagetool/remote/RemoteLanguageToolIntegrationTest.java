/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2016 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.remote;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.server.HTTPSServer;
import org.languagetool.server.HTTPSServerConfig;
import org.languagetool.server.HTTPServer;
import org.languagetool.server.HTTPServerConfig;


public class RemoteLanguageToolIntegrationTest {
    private static final String serverUrl = (("http://" + (HTTPServerConfig.DEFAULT_HOST)) + ":") + (HTTPTools.getDefaultPort());

    @Test
    public void testClient() throws MalformedURLException {
        HTTPServerConfig config = new HTTPServerConfig(HTTPTools.getDefaultPort());
        HTTPServer server = new HTTPServer(config);
        try {
            server.run();
            RemoteLanguageTool lt = new RemoteLanguageTool(new URL(RemoteLanguageToolIntegrationTest.serverUrl));
            Assert.assertThat(lt.check("This is a correct sentence.", "en").getMatches().size(), CoreMatchers.is(0));
            Assert.assertThat(lt.check("Sentence wiht a typo not detected.", "en").getMatches().size(), CoreMatchers.is(0));
            Assert.assertThat(lt.check("Sentence wiht a typo detected.", "en-US").getMatches().size(), CoreMatchers.is(1));
            Assert.assertThat(lt.check("A sentence with a error.", "en").getMatches().size(), CoreMatchers.is(1));
            Assert.assertThat(lt.check("Test escape: %", "en").getMatches().size(), CoreMatchers.is(0));
            RemoteResult result1 = lt.check("A sentence with a error, and and another one", "en");
            Assert.assertThat(result1.getLanguage(), CoreMatchers.is("English"));
            Assert.assertThat(result1.getLanguageCode(), CoreMatchers.is("en"));
            Assert.assertThat(result1.getRemoteServer().getSoftware(), CoreMatchers.is("LanguageTool"));
            Assert.assertNotNull(result1.getRemoteServer().getVersion());
            Assert.assertThat(result1.getMatches().size(), CoreMatchers.is(2));
            Assert.assertThat(result1.getMatches().get(0).getRuleId(), CoreMatchers.is("EN_A_VS_AN"));
            Assert.assertThat(result1.getMatches().get(1).getRuleId(), CoreMatchers.is("ENGLISH_WORD_REPEAT_RULE"));
            CheckConfiguration disabledConfig = new CheckConfigurationBuilder("en").disabledRuleIds("EN_A_VS_AN").build();
            RemoteResult result2 = lt.check("A sentence with a error, and and another one", disabledConfig);
            Assert.assertThat(result2.getMatches().size(), CoreMatchers.is(1));
            Assert.assertThat(result2.getMatches().get(0).getRuleId(), CoreMatchers.is("ENGLISH_WORD_REPEAT_RULE"));
            CheckConfiguration enabledConfig = new CheckConfigurationBuilder("en").enabledRuleIds("EN_A_VS_AN").build();
            RemoteResult result3 = lt.check("A sentence with a error, and and another one", enabledConfig);
            Assert.assertThat(result3.getMatches().size(), CoreMatchers.is(2));
            CheckConfiguration enabledOnlyConfig = new CheckConfigurationBuilder("en").enabledRuleIds("EN_A_VS_AN").enabledOnly().build();
            RemoteResult result4 = lt.check("A sentence with a error, and and another one", enabledOnlyConfig);
            Assert.assertThat(result4.getMatches().size(), CoreMatchers.is(1));
            Assert.assertThat(result4.getMatches().get(0).getRuleId(), CoreMatchers.is("EN_A_VS_AN"));
            CheckConfiguration config1 = new CheckConfigurationBuilder().build();
            RemoteResult result5 = lt.check("Ein Satz in Deutsch, mit etwas mehr Text, damit es auch geht.", config1);
            Assert.assertThat(result5.getLanguage(), CoreMatchers.is("German (Germany)"));
            Assert.assertThat(result5.getLanguageCode(), CoreMatchers.is("de-DE"));
            RemoteResult result7 = lt.check("Das H?user ist sch?n.", "de");
            Assert.assertThat(result7.getMatches().size(), CoreMatchers.is(1));
            Assert.assertThat(result7.getMatches().get(0).getRuleId(), CoreMatchers.is("DE_AGREEMENT"));
            try {
                System.err.println("=== Testing invalid language code - ignore the following exception: ===");
                lt.check("foo", "xy");
                Assert.fail();
            } catch (RuntimeException e) {
                Assert.assertTrue(e.getMessage().contains("is not a language code known to LanguageTool"));
            }
        } finally {
            server.stop();
        }
    }

    @Test
    public void testClientWithHTTPS() throws MalformedURLException, KeyManagementException, NoSuchAlgorithmException {
        disableCertChecks();
        String keyStore = RemoteLanguageToolIntegrationTest.class.getResource("/org/languagetool/remote/test-keystore.jks").getFile();
        HTTPSServerConfig config = new HTTPSServerConfig(HTTPTools.getDefaultPort(), false, new File(keyStore), "mytest");
        HTTPSServer server = new HTTPSServer(config, false, "localhost", Collections.singleton("127.0.0.1"));
        try {
            server.run();
            RemoteLanguageTool lt = new RemoteLanguageTool(new URL(RemoteLanguageToolIntegrationTest.serverUrl.replace("http:", "https:")));
            Assert.assertThat(lt.check("This is a correct sentence.", "en").getMatches().size(), CoreMatchers.is(0));
        } finally {
            server.stop();
        }
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidServer() throws MalformedURLException {
        RemoteLanguageTool lt = new RemoteLanguageTool(new URL("http://does-not-exist"));
        lt.check("foo", "en");
    }

    @Test(expected = RuntimeException.class)
    public void testWrongProtocol() throws MalformedURLException {
        String httpsUrl = (("https://" + (HTTPServerConfig.DEFAULT_HOST)) + ":") + (HTTPServerConfig.DEFAULT_PORT);
        RemoteLanguageTool lt = new RemoteLanguageTool(new URL(httpsUrl));
        lt.check("foo", "en");
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidProtocol() throws MalformedURLException {
        String httpsUrl = (("ftp://" + (HTTPServerConfig.DEFAULT_HOST)) + ":") + (HTTPServerConfig.DEFAULT_PORT);
        RemoteLanguageTool lt = new RemoteLanguageTool(new URL(httpsUrl));
        lt.check("foo", "en");
    }

    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    @Test(expected = MalformedURLException.class)
    public void testProtocolTypo() throws MalformedURLException {
        String httpsUrl = (("htp://" + (HTTPServerConfig.DEFAULT_HOST)) + ":") + (HTTPServerConfig.DEFAULT_PORT);
        new RemoteLanguageTool(new URL(httpsUrl));
    }
}

