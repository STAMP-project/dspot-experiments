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


import NinjaConstant.MODE_KEY_NAME;
import NinjaConstant.serverName;
import NinjaMode.dev;
import NinjaMode.prod;
import NinjaMode.test;
import Standalone.DEFAULT_CONTEXT_PATH;
import Standalone.DEFAULT_HOST;
import Standalone.DEFAULT_IDLE_TIMEOUT;
import Standalone.DEFAULT_PORT;
import Standalone.KEY_NINJA_CONTEXT_PATH;
import Standalone.KEY_NINJA_HOST;
import Standalone.KEY_NINJA_IDLE_TIMEOUT;
import Standalone.KEY_NINJA_PORT;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class AbstractStandaloneTest {
    @Test
    public void ninjaModeOnConstructor() {
        System.setProperty(MODE_KEY_NAME, "dev");
        MockStandalone standalone = new MockStandalone();
        Assert.assertThat(getNinjaMode(), CoreMatchers.is(dev));
    }

    @Test
    public void manuallySetExternalConfiguration() throws Exception {
        MockStandalone standalone = externalConfigurationPath("conf/standalone.conf");
        // port is still null (before configuration)
        Assert.assertThat(getPort(), CoreMatchers.is(CoreMatchers.nullValue()));
        configure();
        // ninja.port in explicit external config worked to override default
        Assert.assertThat(getPort(), CoreMatchers.is(9000));
    }

    @Test
    public void configurationPropertyPriority() throws Exception {
        MockStandalone standalone;
        standalone = new MockStandalone().configure();
        // defaultValue
        Assert.assertThat(getNinjaMode(), CoreMatchers.is(prod));
        Assert.assertThat(getExternalConfigurationPath(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(getContextPath(), CoreMatchers.is(DEFAULT_CONTEXT_PATH));
        Assert.assertThat(getHost(), CoreMatchers.is(DEFAULT_HOST));
        Assert.assertThat(getPort(), CoreMatchers.is(DEFAULT_PORT));
        Assert.assertThat(getIdleTimeout(), CoreMatchers.is(DEFAULT_IDLE_TIMEOUT));
        // configProperty > defaultValue
        standalone = new MockStandalone().externalConfigurationPath("conf/standalone.conf").configure();
        Assert.assertThat(getNinjaMode(), CoreMatchers.is(prod));
        Assert.assertThat(getExternalConfigurationPath(), CoreMatchers.is("conf/standalone.conf"));
        Assert.assertThat(getContextPath(), CoreMatchers.is("/mycontext"));
        Assert.assertThat(getHost(), CoreMatchers.is("1.1.1.1"));
        Assert.assertThat(getPort(), CoreMatchers.is(9000));
        Assert.assertThat(getIdleTimeout(), CoreMatchers.is(60000L));
        // systemProperty > configProperty
        System.setProperty(MODE_KEY_NAME, "dev");
        System.setProperty(KEY_NINJA_HOST, "2.2.2.2");
        System.setProperty(KEY_NINJA_PORT, "9001");
        System.setProperty(KEY_NINJA_CONTEXT_PATH, "/yourcontext");
        System.setProperty(KEY_NINJA_IDLE_TIMEOUT, "80000");
        try {
            standalone = new MockStandalone().externalConfigurationPath("conf/standalone.conf").configure();
            Assert.assertThat(getNinjaMode(), CoreMatchers.is(dev));
            Assert.assertThat(getExternalConfigurationPath(), CoreMatchers.is("conf/standalone.conf"));
            Assert.assertThat(getContextPath(), CoreMatchers.is("/yourcontext"));
            Assert.assertThat(getHost(), CoreMatchers.is("2.2.2.2"));
            Assert.assertThat(getPort(), CoreMatchers.is(9001));
            Assert.assertThat(getIdleTimeout(), CoreMatchers.is(80000L));
            // currentValue > systemProperty
            standalone = new MockStandalone().externalConfigurationPath("conf/standalone.conf").host("3.3.3.3").port(9002).contextPath("/othercontext").idleTimeout(70000L).ninjaMode(test).configure();
            Assert.assertThat(getNinjaMode(), CoreMatchers.is(test));
            Assert.assertThat(getExternalConfigurationPath(), CoreMatchers.is("conf/standalone.conf"));
            Assert.assertThat(getContextPath(), CoreMatchers.is("/othercontext"));
            Assert.assertThat(getHost(), CoreMatchers.is("3.3.3.3"));
            Assert.assertThat(getPort(), CoreMatchers.is(9002));
            Assert.assertThat(getIdleTimeout(), CoreMatchers.is(70000L));
        } finally {
            System.clearProperty(KEY_NINJA_HOST);
            System.clearProperty(KEY_NINJA_PORT);
            System.clearProperty(KEY_NINJA_CONTEXT_PATH);
            System.clearProperty(KEY_NINJA_IDLE_TIMEOUT);
        }
    }

    @Test
    public void ninjaPropertiesThrowsExceptionUntilConfigured() throws Exception {
        MockStandalone standalone = externalConfigurationPath("conf/standalone.conf");
        try {
            getNinjaProperties();
            Assert.fail("exception expected");
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("configure() not called"));
        }
        configure();
        Assert.assertThat(getNinjaProperties(), CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
    }

    @Test
    public void urlUsesLocalhostInLieuOfNull() throws Exception {
        MockStandalone standalone = new MockStandalone().configure();
        Assert.assertThat(getServerUrls().get(0), CoreMatchers.is("http://localhost:8080"));
        Assert.assertThat(getBaseUrls().get(0), CoreMatchers.is("http://localhost:8080"));
    }

    @Test
    public void urlIncludesContext() throws Exception {
        MockStandalone standalone = new MockStandalone().host("1.1.1.1").contextPath("/mycontext").configure();
        Assert.assertThat(getServerUrls().get(0), CoreMatchers.is("http://1.1.1.1:8080"));
        Assert.assertThat(getBaseUrls().get(0), CoreMatchers.is("http://1.1.1.1:8080/mycontext"));
    }

    @Test
    public void urlExcludesWellKnownPorts() throws Exception {
        MockStandalone standalone = new MockStandalone().host("1.1.1.1").port(80).contextPath("/mycontext").configure();
        Assert.assertThat(getServerUrls().get(0), CoreMatchers.is("http://1.1.1.1"));
        Assert.assertThat(getBaseUrls().get(0), CoreMatchers.is("http://1.1.1.1/mycontext"));
    }

    @Test
    public void ninjaPropertiesServerNameSetAfterConfigure() throws Exception {
        MockStandalone standalone = new MockStandalone().host("1.1.1.1").configure();
        Assert.assertThat(getNinjaProperties().get(serverName), CoreMatchers.is("http://1.1.1.1:8080"));
    }

    @Test
    public void ninjaPropertiesServerNameSetButOnlyIfNotInConfigFile() throws Exception {
        MockStandalone standalone = new MockStandalone().externalConfigurationPath("conf/standalone.with.servername.conf").host("1.1.1.1").configure();
        Assert.assertThat(getNinjaProperties().get(serverName), CoreMatchers.is("http://www.example.com:8080"));
    }

    @Test
    public void injectorOnlyAvailableAfterStart() throws Exception {
        MockStandalone standalone = new MockStandalone();
        try {
            standalone.getInjector();
            Assert.fail("exception expected");
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("start() not called"));
        }
        start();
        Assert.assertThat(standalone.getInjector(), CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
    }

    @Test
    public void validateContextPath() throws Exception {
        MockStandalone standalone = externalConfigurationPath("conf/standalone.with.badcontext.conf");
        try {
            configure();
            Assert.fail("exception expected");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("context"));
        }
        configure();
    }

    @Test
    public void noPortsEnabled() throws Exception {
        try {
            MockStandalone standalone = new MockStandalone().port((-1)).configure();
            Assert.fail("exception expected");
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("ports were disabled"));
        }
    }

    @Test
    public void randomPortAssigned() throws Exception {
        MockStandalone standalone = new MockStandalone().port(0).configure();
        Assert.assertThat(getPort(), CoreMatchers.is(CoreMatchers.not(0)));
        Assert.assertThat(isPortEnabled(), CoreMatchers.is(true));
        Assert.assertThat(isSslPortEnabled(), CoreMatchers.is(false));
    }

    @Test
    public void sslRandomPortAssigned() throws Exception {
        MockStandalone standalone = new MockStandalone().port((-1)).sslPort(0).configure();
        Assert.assertThat(getSslPort(), CoreMatchers.is(CoreMatchers.not(0)));
    }

    @Test
    public void sslConfiguration() throws Exception {
        MockStandalone standalone = new MockStandalone().port((-1)).sslPort(8443).start();
        Assert.assertThat(getSslPort(), CoreMatchers.is(CoreMatchers.not(0)));
        Assert.assertThat(isPortEnabled(), CoreMatchers.is(false));
        Assert.assertThat(isSslPortEnabled(), CoreMatchers.is(true));
        Assert.assertThat(getServerUrls().size(), CoreMatchers.is(1));
        Assert.assertThat(getServerUrls().get(0), CoreMatchers.endsWith(":8443"));
        Assert.assertThat(getServerUrls().get(0), CoreMatchers.startsWith("https://"));
    }
}

