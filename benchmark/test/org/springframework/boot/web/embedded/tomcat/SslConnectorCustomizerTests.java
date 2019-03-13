/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.web.embedded.tomcat;


import LifecycleState.STARTED;
import SslStoreProviderUrlStreamHandlerFactory.KEY_STORE_URL;
import SslStoreProviderUrlStreamHandlerFactory.TRUST_STORE_URL;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.testsupport.rule.OutputCapture;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.SslStoreProvider;
import org.springframework.boot.web.server.WebServerException;


/**
 * Tests for {@link SslConnectorCustomizer}
 *
 * @author Brian Clozel
 */
public class SslConnectorCustomizerTests {
    private Tomcat tomcat;

    private Connector connector;

    @Rule
    public OutputCapture output = new OutputCapture();

    @Test
    public void sslCiphersConfiguration() throws Exception {
        Ssl ssl = new Ssl();
        ssl.setKeyStore("test.jks");
        ssl.setKeyStorePassword("secret");
        ssl.setCiphers(new String[]{ "ALPHA", "BRAVO", "CHARLIE" });
        SslConnectorCustomizer customizer = new SslConnectorCustomizer(ssl, null);
        Connector connector = this.tomcat.getConnector();
        customizer.customize(connector);
        this.tomcat.start();
        SSLHostConfig[] sslHostConfigs = connector.getProtocolHandler().findSslHostConfigs();
        assertThat(sslHostConfigs[0].getCiphers()).isEqualTo("ALPHA:BRAVO:CHARLIE");
    }

    @Test
    public void sslEnabledMultipleProtocolsConfiguration() throws Exception {
        Ssl ssl = new Ssl();
        ssl.setKeyPassword("password");
        ssl.setKeyStore("src/test/resources/test.jks");
        ssl.setEnabledProtocols(new String[]{ "TLSv1.1", "TLSv1.2" });
        ssl.setCiphers(new String[]{ "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256", "BRAVO" });
        SslConnectorCustomizer customizer = new SslConnectorCustomizer(ssl, null);
        Connector connector = this.tomcat.getConnector();
        customizer.customize(connector);
        this.tomcat.start();
        SSLHostConfig sslHostConfig = connector.getProtocolHandler().findSslHostConfigs()[0];
        assertThat(sslHostConfig.getSslProtocol()).isEqualTo("TLS");
        assertThat(sslHostConfig.getEnabledProtocols()).containsExactlyInAnyOrder("TLSv1.1", "TLSv1.2");
    }

    @Test
    public void sslEnabledProtocolsConfiguration() throws Exception {
        Ssl ssl = new Ssl();
        ssl.setKeyPassword("password");
        ssl.setKeyStore("src/test/resources/test.jks");
        ssl.setEnabledProtocols(new String[]{ "TLSv1.2" });
        ssl.setCiphers(new String[]{ "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256", "BRAVO" });
        SslConnectorCustomizer customizer = new SslConnectorCustomizer(ssl, null);
        Connector connector = this.tomcat.getConnector();
        customizer.customize(connector);
        this.tomcat.start();
        SSLHostConfig sslHostConfig = connector.getProtocolHandler().findSslHostConfigs()[0];
        assertThat(sslHostConfig.getSslProtocol()).isEqualTo("TLS");
        assertThat(sslHostConfig.getEnabledProtocols()).containsExactly("TLSv1.2");
    }

    @Test
    public void customizeWhenSslStoreProviderProvidesOnlyKeyStoreShouldUseDefaultTruststore() throws Exception {
        Ssl ssl = new Ssl();
        ssl.setKeyPassword("password");
        ssl.setTrustStore("src/test/resources/test.jks");
        SslStoreProvider sslStoreProvider = Mockito.mock(SslStoreProvider.class);
        BDDMockito.given(sslStoreProvider.getKeyStore()).willReturn(loadStore());
        SslConnectorCustomizer customizer = new SslConnectorCustomizer(ssl, sslStoreProvider);
        Connector connector = this.tomcat.getConnector();
        customizer.customize(connector);
        this.tomcat.start();
        SSLHostConfig sslHostConfig = connector.getProtocolHandler().findSslHostConfigs()[0];
        SSLHostConfig sslHostConfigWithDefaults = new SSLHostConfig();
        assertThat(sslHostConfig.getTruststoreFile()).isEqualTo(sslHostConfigWithDefaults.getTruststoreFile());
        assertThat(sslHostConfig.getCertificateKeystoreFile()).isEqualTo(KEY_STORE_URL);
    }

    @Test
    public void customizeWhenSslStoreProviderProvidesOnlyTrustStoreShouldUseDefaultKeystore() throws Exception {
        Ssl ssl = new Ssl();
        ssl.setKeyPassword("password");
        ssl.setKeyStore("src/test/resources/test.jks");
        SslStoreProvider sslStoreProvider = Mockito.mock(SslStoreProvider.class);
        BDDMockito.given(sslStoreProvider.getTrustStore()).willReturn(loadStore());
        SslConnectorCustomizer customizer = new SslConnectorCustomizer(ssl, sslStoreProvider);
        Connector connector = this.tomcat.getConnector();
        customizer.customize(connector);
        this.tomcat.start();
        SSLHostConfig sslHostConfig = connector.getProtocolHandler().findSslHostConfigs()[0];
        SSLHostConfig sslHostConfigWithDefaults = new SSLHostConfig();
        assertThat(sslHostConfig.getTruststoreFile()).isEqualTo(TRUST_STORE_URL);
        assertThat(sslHostConfig.getCertificateKeystoreFile()).contains(sslHostConfigWithDefaults.getCertificateKeystoreFile());
    }

    @Test
    public void customizeWhenSslStoreProviderPresentShouldIgnorePasswordFromSsl() throws Exception {
        System.setProperty("javax.net.ssl.trustStorePassword", "trustStoreSecret");
        Ssl ssl = new Ssl();
        ssl.setKeyPassword("password");
        ssl.setKeyStorePassword("secret");
        SslStoreProvider sslStoreProvider = Mockito.mock(SslStoreProvider.class);
        BDDMockito.given(sslStoreProvider.getTrustStore()).willReturn(loadStore());
        BDDMockito.given(sslStoreProvider.getKeyStore()).willReturn(loadStore());
        SslConnectorCustomizer customizer = new SslConnectorCustomizer(ssl, sslStoreProvider);
        Connector connector = this.tomcat.getConnector();
        customizer.customize(connector);
        this.tomcat.start();
        assertThat(connector.getState()).isEqualTo(STARTED);
        assertThat(this.output.toString()).doesNotContain("Password verification failed");
    }

    @Test
    public void customizeWhenSslIsEnabledWithNoKeyStoreThrowsWebServerException() {
        assertThatExceptionOfType(WebServerException.class).isThrownBy(() -> new SslConnectorCustomizer(new Ssl(), null).customize(this.tomcat.getConnector())).withMessageContaining("Could not load key store 'null'");
    }
}

