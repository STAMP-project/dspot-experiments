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


import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.servlet.server.Jsp;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.boot.web.servlet.server.Session.Cookie;


/**
 * Tests for {@link ServletWebServerFactoryCustomizer}.
 *
 * @author Brian Clozel
 * @author Yunkun Huang
 */
public class ServletWebServerFactoryCustomizerTests {
    private final ServerProperties properties = new ServerProperties();

    private ServletWebServerFactoryCustomizer customizer;

    @Test
    public void testDefaultDisplayName() {
        ConfigurableServletWebServerFactory factory = Mockito.mock(ConfigurableServletWebServerFactory.class);
        this.customizer.customize(factory);
        Mockito.verify(factory).setDisplayName("application");
    }

    @Test
    public void testCustomizeDisplayName() {
        ConfigurableServletWebServerFactory factory = Mockito.mock(ConfigurableServletWebServerFactory.class);
        this.properties.getServlet().setApplicationDisplayName("TestName");
        this.customizer.customize(factory);
        Mockito.verify(factory).setDisplayName("TestName");
    }

    @Test
    public void testCustomizeSsl() {
        ConfigurableServletWebServerFactory factory = Mockito.mock(ConfigurableServletWebServerFactory.class);
        Ssl ssl = Mockito.mock(Ssl.class);
        this.properties.setSsl(ssl);
        this.customizer.customize(factory);
        Mockito.verify(factory).setSsl(ssl);
    }

    @Test
    public void testCustomizeJsp() {
        ConfigurableServletWebServerFactory factory = Mockito.mock(ConfigurableServletWebServerFactory.class);
        this.customizer.customize(factory);
        Mockito.verify(factory).setJsp(ArgumentMatchers.any(Jsp.class));
    }

    @Test
    public void customizeSessionProperties() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("server.servlet.session.timeout", "123");
        map.put("server.servlet.session.tracking-modes", "cookie,url");
        map.put("server.servlet.session.cookie.name", "testname");
        map.put("server.servlet.session.cookie.domain", "testdomain");
        map.put("server.servlet.session.cookie.path", "/testpath");
        map.put("server.servlet.session.cookie.comment", "testcomment");
        map.put("server.servlet.session.cookie.http-only", "true");
        map.put("server.servlet.session.cookie.secure", "true");
        map.put("server.servlet.session.cookie.max-age", "60");
        bindProperties(map);
        ConfigurableServletWebServerFactory factory = Mockito.mock(ConfigurableServletWebServerFactory.class);
        this.customizer.customize(factory);
        ArgumentCaptor<Session> sessionCaptor = ArgumentCaptor.forClass(Session.class);
        Mockito.verify(factory).setSession(sessionCaptor.capture());
        assertThat(sessionCaptor.getValue().getTimeout()).isEqualTo(Duration.ofSeconds(123));
        Cookie cookie = sessionCaptor.getValue().getCookie();
        assertThat(cookie.getName()).isEqualTo("testname");
        assertThat(cookie.getDomain()).isEqualTo("testdomain");
        assertThat(cookie.getPath()).isEqualTo("/testpath");
        assertThat(cookie.getComment()).isEqualTo("testcomment");
        assertThat(cookie.getHttpOnly()).isTrue();
        assertThat(cookie.getMaxAge()).isEqualTo(Duration.ofSeconds(60));
    }

    @Test
    public void testCustomizeTomcatPort() {
        ConfigurableServletWebServerFactory factory = Mockito.mock(ConfigurableServletWebServerFactory.class);
        this.properties.setPort(8080);
        this.customizer.customize(factory);
        Mockito.verify(factory).setPort(8080);
    }

    @Test
    public void customizeServletDisplayName() {
        Map<String, String> map = new HashMap<>();
        map.put("server.servlet.application-display-name", "MyBootApp");
        bindProperties(map);
        ConfigurableServletWebServerFactory factory = Mockito.mock(ConfigurableServletWebServerFactory.class);
        this.customizer.customize(factory);
        Mockito.verify(factory).setDisplayName("MyBootApp");
    }

    @Test
    public void testCustomizeTomcatMinSpareThreads() {
        Map<String, String> map = new HashMap<>();
        map.put("server.tomcat.min-spare-threads", "10");
        bindProperties(map);
        assertThat(this.properties.getTomcat().getMinSpareThreads()).isEqualTo(10);
    }

    @Test
    public void sessionStoreDir() {
        Map<String, String> map = new HashMap<>();
        map.put("server.servlet.session.store-dir", "myfolder");
        bindProperties(map);
        ConfigurableServletWebServerFactory factory = Mockito.mock(ConfigurableServletWebServerFactory.class);
        this.customizer.customize(factory);
        ArgumentCaptor<Session> sessionCaptor = ArgumentCaptor.forClass(Session.class);
        Mockito.verify(factory).setSession(sessionCaptor.capture());
        assertThat(sessionCaptor.getValue().getStoreDir()).isEqualTo(new File("myfolder"));
    }
}

