/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja.standalone;


import NinjaJetty.SilentErrorHandler;
import NinjaMode.prod;
import NinjaMode.test;
import Standalone.DEFAULT_DEV_NINJA_SSL_KEYSTORE_PASSWORD;
import Standalone.DEFAULT_DEV_NINJA_SSL_TRUSTSTORE_PASSWORD;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.inject.CreationException;
import java.net.URI;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;

import static Standalone.DEFAULT_DEV_NINJA_SSL_KEYSTORE_URI;
import static Standalone.DEFAULT_DEV_NINJA_SSL_TRUSTSTORE_URI;


public class NinjaJettyTest {
    static int RANDOM_PORT = StandaloneHelper.findAvailablePort(8081, 9000);

    @Test
    public void minimal() throws Exception {
        NinjaJetty standalone = new NinjaJetty().externalConfigurationPath("conf/jetty.minimal.conf").port(NinjaJettyTest.RANDOM_PORT);
        try {
            Assert.assertThat(standalone.getPort(), CoreMatchers.is(NinjaJettyTest.RANDOM_PORT));
            Assert.assertThat(standalone.getHost(), CoreMatchers.is(CoreMatchers.nullValue()));
            Assert.assertThat(standalone.getContextPath(), CoreMatchers.is(CoreMatchers.nullValue()));
            Assert.assertThat(standalone.getNinjaMode(), CoreMatchers.is(prod));
            standalone.start();
            Assert.assertThat(standalone.getServerUrls().get(0), CoreMatchers.is(("http://localhost:" + (NinjaJettyTest.RANDOM_PORT))));
            Assert.assertThat(standalone.contextHandler, CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
            Assert.assertNotNull(standalone.ninjaServletListener);
            Assert.assertThat(standalone.contextHandler.isAvailable(), CoreMatchers.is(true));
            Assert.assertThat(standalone.contextHandler.isStarted(), CoreMatchers.is(true));
            Assert.assertThat(standalone.jetty.isStarted(), CoreMatchers.is(true));
            standalone.shutdown();
            Assert.assertThat(standalone.contextHandler.isStopped(), CoreMatchers.is(true));
            Assert.assertThat(standalone.jetty.isStopped(), CoreMatchers.is(true));
        } finally {
            standalone.shutdown();
        }
    }

    @Test
    public void minimalWithContext() throws Exception {
        NinjaJetty standalone = new NinjaJetty().externalConfigurationPath("conf/jetty.minimal.conf").ninjaMode(test).port(NinjaJettyTest.RANDOM_PORT).host("localhost").contextPath("/mycontext");
        try {
            standalone.start();
            Assert.assertThat(standalone.getPort(), CoreMatchers.is(NinjaJettyTest.RANDOM_PORT));
            Assert.assertThat(standalone.getHost(), CoreMatchers.is("localhost"));
            Assert.assertThat(standalone.getContextPath(), CoreMatchers.is("/mycontext"));
            Assert.assertThat(standalone.getNinjaMode(), CoreMatchers.is(test));
            Assert.assertEquals("/mycontext", standalone.contextHandler.getContextPath());
            Assert.assertThat(standalone.getContextPath(), CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
            Assert.assertThat(standalone.ninjaServletListener, CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
            Assert.assertThat(standalone.contextHandler.isAvailable(), CoreMatchers.is(true));
            Assert.assertThat(standalone.contextHandler.isStarted(), CoreMatchers.is(true));
            Assert.assertThat(standalone.jetty.isStarted(), CoreMatchers.is(true));
            standalone.shutdown();
            Assert.assertThat(standalone.contextHandler.isStopped(), CoreMatchers.is(true));
            Assert.assertThat(standalone.jetty.isStopped(), CoreMatchers.is(true));
        } finally {
            standalone.shutdown();
        }
    }

    @Test
    public void missingConfigurationThrowsException() throws Exception {
        NinjaJetty standalone = new NinjaJetty().externalConfigurationPath("conf/jetty.empty.conf").port(NinjaJettyTest.RANDOM_PORT);
        try {
            standalone.start();
            Assert.fail("start() should have thrown exception");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("application.secret not set"));
        } finally {
            standalone.shutdown();
        }
    }

    @Test
    public void missingLanguageThrowsInjectorException() throws Exception {
        // bad configuration file will throw exception when creating NinjaPropertiesImpl
        // that exception occurs in NinjaBootstrap during injector creation
        NinjaJetty standalone = new NinjaJetty().externalConfigurationPath("conf/jetty.missinglang.conf").port(NinjaJettyTest.RANDOM_PORT);
        try {
            standalone.start();
            Assert.fail("start() should have thrown exception");
        } catch (CreationException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("not retrieve application languages from ninjaProperties"));
        } finally {
            standalone.shutdown();
        }
    }

    @Test
    public void jettyConfiguration() throws Exception {
        // use test resource of "jetty.xml" but we need to swap into a new
        // random port and then write the file back out
        String jettyConfiguration = NinjaJettyTest.createJettyConfiguration("jetty.xml", NinjaJettyTest.RANDOM_PORT);
        NinjaJetty standalone = new NinjaJetty().externalConfigurationPath("conf/jetty.com.example.conf").jettyConfiguration(jettyConfiguration);
        try {
            standalone.start();
            // port won't be correct b/c actually configured via jetty file
            Assert.assertThat(standalone.ninjaServletListener, CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
            Assert.assertThat(standalone.contextHandler.isAvailable(), CoreMatchers.is(true));
            Assert.assertThat(standalone.contextHandler.isStarted(), CoreMatchers.is(true));
            Assert.assertThat(standalone.jetty.isStarted(), CoreMatchers.is(true));
            String page = NinjaJettyTest.get((("http://localhost:" + (NinjaJettyTest.RANDOM_PORT)) + "/home"));
            Assert.assertThat(page, CoreMatchers.containsString("Hello World!"));
        } finally {
            standalone.shutdown();
        }
    }

    @Test
    public void jettyConfigurationWithContext() throws Exception {
        // use test resource of "jetty.xml" but we need to swap into a new
        // random port and then write the file back out
        String jettyConfiguration = NinjaJettyTest.createJettyConfiguration("jetty.xml", NinjaJettyTest.RANDOM_PORT);
        NinjaJetty standalone = new NinjaJetty().externalConfigurationPath("conf/jetty.com.example.conf").contextPath("/mycontext").jettyConfiguration(jettyConfiguration);
        try {
            standalone.start();
            // port won't be correct b/c actually configured via jetty file
            Assert.assertThat(standalone.ninjaServletListener, CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
            Assert.assertThat(standalone.contextHandler.isAvailable(), CoreMatchers.is(true));
            Assert.assertThat(standalone.contextHandler.isStarted(), CoreMatchers.is(true));
            Assert.assertThat(standalone.jetty.isStarted(), CoreMatchers.is(true));
            String page;
            page = NinjaJettyTest.get((("http://localhost:" + (NinjaJettyTest.RANDOM_PORT)) + "/mycontext/home"));
            Assert.assertThat(page, CoreMatchers.containsString("Hello World!"));
            page = NinjaJettyTest.get((("http://localhost:" + (NinjaJettyTest.RANDOM_PORT)) + "/mycontext/context_path"));
            // requestPath removes contextPath
            Assert.assertThat(page, CoreMatchers.containsString("/mycontext"));
            page = NinjaJettyTest.get((("http://localhost:" + (NinjaJettyTest.RANDOM_PORT)) + "/mycontext/request_path"));
            // requestPath removes contextPath
            Assert.assertThat(page, CoreMatchers.containsString("/request_path"));
            // is the port correct (otherwise logging will be wrong)
            Assert.assertThat(standalone.getPort(), CoreMatchers.is(NinjaJettyTest.RANDOM_PORT));
        } finally {
            standalone.shutdown();
        }
    }

    @Test
    public void ssl() throws Exception {
        NinjaJetty standalone = new NinjaJetty().externalConfigurationPath("conf/jetty.minimal.conf").ninjaMode(test).port((-1)).sslPort(NinjaJettyTest.RANDOM_PORT);
        try {
            standalone.start();
            Assert.assertThat(standalone.getPort(), CoreMatchers.is((-1)));
            Assert.assertThat(standalone.getHost(), CoreMatchers.is(CoreMatchers.nullValue()));
            Assert.assertThat(standalone.getContextPath(), CoreMatchers.is(""));
            Assert.assertThat(standalone.getNinjaMode(), CoreMatchers.is(test));
            Assert.assertThat(standalone.getSslPort(), CoreMatchers.is(NinjaJettyTest.RANDOM_PORT));
            Assert.assertThat(standalone.getSslKeystoreUri(), CoreMatchers.is(new URI(DEFAULT_DEV_NINJA_SSL_KEYSTORE_URI)));
            Assert.assertThat(standalone.getSslKeystorePassword(), CoreMatchers.is(DEFAULT_DEV_NINJA_SSL_KEYSTORE_PASSWORD));
            Assert.assertThat(standalone.getSslTruststoreUri(), CoreMatchers.is(new URI(DEFAULT_DEV_NINJA_SSL_TRUSTSTORE_URI)));
            Assert.assertThat(standalone.getSslTruststorePassword(), CoreMatchers.is(DEFAULT_DEV_NINJA_SSL_TRUSTSTORE_PASSWORD));
            Assert.assertThat(standalone.getServerUrls().get(0), CoreMatchers.is(("https://localhost:" + (NinjaJettyTest.RANDOM_PORT))));
            Assert.assertThat(standalone.contextHandler, CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
            Assert.assertNotNull(standalone.ninjaServletListener);
            Assert.assertThat(standalone.contextHandler.isAvailable(), CoreMatchers.is(true));
            Assert.assertThat(standalone.contextHandler.isStarted(), CoreMatchers.is(true));
            Assert.assertThat(standalone.jetty.isStarted(), CoreMatchers.is(true));
            standalone.shutdown();
            Assert.assertThat(standalone.contextHandler.isStopped(), CoreMatchers.is(true));
            Assert.assertThat(standalone.jetty.isStopped(), CoreMatchers.is(true));
        } finally {
            standalone.shutdown();
        }
    }

    @Test
    public void sessionsAreNotSharedOnSingleResultInstances() throws Exception {
        // this test is not really specific to jetty, but its easier to test here
        NinjaJetty standalone = new NinjaJetty().externalConfigurationPath("conf/jetty.com.session.conf").port(NinjaJettyTest.RANDOM_PORT);
        try {
            standalone.start();
            // establish session with a set-cookie header
            HttpRequest client0 = HttpRequest.get(((standalone.getBaseUrls().get(0)) + "/getOrCreateSession"));
            Assert.assertThat(client0.code(), CoreMatchers.is(200));
            Assert.assertThat(client0.header("Set-Cookie"), CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
            // call redirect so its session is processed first time (triggers bug for issue #450)
            HttpRequest client1 = HttpRequest.get(((standalone.getBaseUrls().get(0)) + "/badRoute")).header("Cookie", client0.header("Set-Cookie")).followRedirects(false);
            Assert.assertThat(client1.code(), CoreMatchers.is(303));
            Assert.assertThat(client1.header("Set-Cookie"), CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
            // call redirect with a mock new browser (no cookie header) -- we
            // should not get a session value back
            HttpRequest client2 = HttpRequest.get(((standalone.getBaseUrls().get(0)) + "/badRoute")).followRedirects(false);
            Assert.assertThat(client2.code(), CoreMatchers.is(303));
            // if cookie is NOT null then someone elses cookie (from the previous
            // request above) got assigned to us!
            Assert.assertThat(client2.header("Set-Cookie"), CoreMatchers.is(CoreMatchers.nullValue()));
        } finally {
            standalone.shutdown();
        }
    }

    @Test
    public void directoryListingIsForbidden() throws Exception {
        NinjaJetty standalone = new NinjaJetty().externalConfigurationPath("conf/jetty.minimal.conf").port(NinjaJettyTest.RANDOM_PORT);
        try {
            standalone.start();
            String directoryLisingAllowed = standalone.contextHandler.getInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed");
            Assert.assertThat(directoryLisingAllowed, CoreMatchers.is("false"));
        } finally {
            standalone.shutdown();
        }
    }

    @Test
    public void checkThatSilentErrorHandlerIsRegistered() throws Exception {
        NinjaJetty standalone = new NinjaJetty().externalConfigurationPath("conf/jetty.minimal.conf").port(NinjaJettyTest.RANDOM_PORT);
        try {
            standalone.start();
            ErrorHandler errorHandler = standalone.contextHandler.getErrorHandler();
            Assert.assertThat(errorHandler, IsInstanceOf.instanceOf(SilentErrorHandler.class));
        } finally {
            standalone.shutdown();
        }
    }
}

