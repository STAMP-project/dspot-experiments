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
package org.springframework.boot.web.embedded.undertow;


import io.undertow.Undertow.Builder;
import io.undertow.servlet.api.DeploymentInfo;
import java.io.IOException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.boot.testsupport.web.servlet.ExampleServlet;
import org.springframework.boot.web.servlet.server.AbstractServletWebServerFactory;
import org.springframework.boot.web.servlet.server.AbstractServletWebServerFactoryTests;
import org.springframework.http.HttpStatus;


/**
 * Tests for {@link UndertowServletWebServerFactory}.
 *
 * @author Ivan Sopov
 * @author Andy Wilkinson
 */
public class UndertowServletWebServerFactoryTests extends AbstractServletWebServerFactoryTests {
    @Test
    public void errorPage404() throws Exception {
        AbstractServletWebServerFactory factory = getFactory();
        factory.addErrorPages(new org.springframework.boot.web.server.ErrorPage(HttpStatus.NOT_FOUND, "/hello"));
        this.webServer = factory.getWebServer(new org.springframework.boot.web.servlet.ServletRegistrationBean(new ExampleServlet(), "/hello"));
        this.webServer.start();
        assertThat(getResponse(getLocalUrl("/hello"))).isEqualTo("Hello World");
        assertThat(getResponse(getLocalUrl("/not-found"))).isEqualTo("Hello World");
    }

    @Test
    public void setNullBuilderCustomizersThrows() {
        UndertowServletWebServerFactory factory = getFactory();
        assertThatIllegalArgumentException().isThrownBy(() -> factory.setBuilderCustomizers(null)).withMessageContaining("Customizers must not be null");
    }

    @Test
    public void addNullAddBuilderCustomizersThrows() {
        UndertowServletWebServerFactory factory = getFactory();
        assertThatIllegalArgumentException().isThrownBy(() -> factory.addBuilderCustomizers(((UndertowBuilderCustomizer[]) (null)))).withMessageContaining("Customizers must not be null");
    }

    @Test
    public void builderCustomizers() {
        UndertowServletWebServerFactory factory = getFactory();
        UndertowBuilderCustomizer[] customizers = new UndertowBuilderCustomizer[4];
        Arrays.setAll(customizers, ( i) -> mock(.class));
        factory.setBuilderCustomizers(Arrays.asList(customizers[0], customizers[1]));
        factory.addBuilderCustomizers(customizers[2], customizers[3]);
        this.webServer = factory.getWebServer();
        InOrder ordered = Mockito.inOrder(((Object[]) (customizers)));
        for (UndertowBuilderCustomizer customizer : customizers) {
            ordered.verify(customizer).customize(ArgumentMatchers.any(Builder.class));
        }
    }

    @Test
    public void setNullDeploymentInfoCustomizersThrows() {
        UndertowServletWebServerFactory factory = getFactory();
        assertThatIllegalArgumentException().isThrownBy(() -> factory.setDeploymentInfoCustomizers(null)).withMessageContaining("Customizers must not be null");
    }

    @Test
    public void addNullAddDeploymentInfoCustomizersThrows() {
        UndertowServletWebServerFactory factory = getFactory();
        assertThatIllegalArgumentException().isThrownBy(() -> factory.addDeploymentInfoCustomizers(((UndertowDeploymentInfoCustomizer[]) (null)))).withMessageContaining("Customizers must not be null");
    }

    @Test
    public void deploymentInfo() {
        UndertowServletWebServerFactory factory = getFactory();
        UndertowDeploymentInfoCustomizer[] customizers = new UndertowDeploymentInfoCustomizer[4];
        Arrays.setAll(customizers, ( i) -> mock(.class));
        factory.setDeploymentInfoCustomizers(Arrays.asList(customizers[0], customizers[1]));
        factory.addDeploymentInfoCustomizers(customizers[2], customizers[3]);
        this.webServer = factory.getWebServer();
        InOrder ordered = Mockito.inOrder(((Object[]) (customizers)));
        for (UndertowDeploymentInfoCustomizer customizer : customizers) {
            ordered.verify(customizer).customize(ArgumentMatchers.any(DeploymentInfo.class));
        }
    }

    @Test
    public void basicSslClasspathKeyStore() throws Exception {
        testBasicSslWithKeyStore("classpath:test.jks");
    }

    @Test
    public void defaultContextPath() {
        UndertowServletWebServerFactory factory = getFactory();
        final AtomicReference<String> contextPath = new AtomicReference<>();
        factory.addDeploymentInfoCustomizers(( deploymentInfo) -> contextPath.set(deploymentInfo.getContextPath()));
        this.webServer = factory.getWebServer();
        assertThat(contextPath.get()).isEqualTo("/");
    }

    @Test
    public void useForwardHeaders() throws Exception {
        UndertowServletWebServerFactory factory = getFactory();
        factory.setUseForwardHeaders(true);
        assertForwardHeaderIsUsed(factory);
    }

    @Test
    public void eachFactoryUsesADiscreteServletContainer() {
        assertThat(getServletContainerFromNewFactory()).isNotEqualTo(getServletContainerFromNewFactory());
    }

    @Test
    public void accessLogCanBeEnabled() throws IOException, InterruptedException, URISyntaxException {
        testAccessLog(null, null, "access_log.log");
    }

    @Test
    public void accessLogCanBeCustomized() throws IOException, InterruptedException, URISyntaxException {
        testAccessLog("my_access.", "logz", "my_access.logz");
    }

    @Test
    public void sslRestrictedProtocolsEmptyCipherFailure() throws Exception {
        assertThatIOException().isThrownBy(() -> testRestrictedSSLProtocolsAndCipherSuites(new String[]{ "TLSv1.2" }, new String[]{ "TLS_EMPTY_RENEGOTIATION_INFO_SCSV" })).isInstanceOfAny(SSLException.class, SSLHandshakeException.class, SocketException.class);
    }

    @Test
    public void sslRestrictedProtocolsECDHETLS1Failure() throws Exception {
        assertThatIOException().isThrownBy(() -> testRestrictedSSLProtocolsAndCipherSuites(new String[]{ "TLSv1" }, new String[]{ "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256" })).isInstanceOfAny(SSLException.class, SocketException.class);
    }

    @Test
    public void sslRestrictedProtocolsECDHESuccess() throws Exception {
        testRestrictedSSLProtocolsAndCipherSuites(new String[]{ "TLSv1.2" }, new String[]{ "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256" });
    }

    @Test
    public void sslRestrictedProtocolsRSATLS12Success() throws Exception {
        testRestrictedSSLProtocolsAndCipherSuites(new String[]{ "TLSv1.2" }, new String[]{ "TLS_RSA_WITH_AES_128_CBC_SHA256" });
    }

    @Test
    public void sslRestrictedProtocolsRSATLS11Failure() throws Exception {
        assertThatIOException().isThrownBy(() -> testRestrictedSSLProtocolsAndCipherSuites(new String[]{ "TLSv1.1" }, new String[]{ "TLS_RSA_WITH_AES_128_CBC_SHA256" })).isInstanceOfAny(SSLException.class, SocketException.class);
    }
}

