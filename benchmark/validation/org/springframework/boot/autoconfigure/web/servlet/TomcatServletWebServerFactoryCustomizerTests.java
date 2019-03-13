/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.autoconfigure.web.servlet;


import ServerProperties.Tomcat;
import org.apache.catalina.Context;
import org.junit.Test;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.mock.env.MockEnvironment;


/**
 * Tests for {@link TomcatServletWebServerFactoryCustomizer}.
 *
 * @author Phillip Webb
 */
public class TomcatServletWebServerFactoryCustomizerTests {
    private TomcatServletWebServerFactoryCustomizer customizer;

    private MockEnvironment environment;

    private ServerProperties serverProperties;

    @Test
    public void customTldSkip() {
        bind("server.tomcat.additional-tld-skip-patterns=foo.jar,bar.jar");
        testCustomTldSkip("foo.jar", "bar.jar");
    }

    @Test
    public void customTldSkipAsList() {
        bind("server.tomcat.additional-tld-skip-patterns[0]=biz.jar", "server.tomcat.additional-tld-skip-patterns[1]=bah.jar");
        testCustomTldSkip("biz.jar", "bah.jar");
    }

    @Test
    public void redirectContextRootCanBeConfigured() {
        bind("server.tomcat.redirect-context-root=false");
        ServerProperties.Tomcat tomcat = this.serverProperties.getTomcat();
        assertThat(tomcat.getRedirectContextRoot()).isFalse();
        TomcatWebServer server = customizeAndGetServer();
        Context context = ((Context) (server.getTomcat().getHost().findChildren()[0]));
        assertThat(context.getMapperContextRootRedirectEnabled()).isFalse();
    }

    @Test
    public void useRelativeRedirectsCanBeConfigured() {
        bind("server.tomcat.use-relative-redirects=true");
        assertThat(this.serverProperties.getTomcat().getUseRelativeRedirects()).isTrue();
        TomcatWebServer server = customizeAndGetServer();
        Context context = ((Context) (server.getTomcat().getHost().findChildren()[0]));
        assertThat(context.getUseRelativeRedirects()).isTrue();
    }
}

