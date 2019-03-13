/**
 * Copyright 2018 ThoughtWorks, Inc.
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
package com.thoughtworks.go.server.util;


import SystemEnvironment.GO_SSL_CONFIG_CLEAR_JETTY_DEFAULT_EXCLUSIONS;
import com.thoughtworks.go.server.Jetty9Server;
import com.thoughtworks.go.server.config.GoSSLConfig;
import com.thoughtworks.go.util.SystemEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.Mockito;


public class GoSslSocketConnectorTest {
    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private File truststore;

    private File keystore;

    private GoSslSocketConnector sslSocketConnector;

    @Mock
    private GoSSLConfig goSSLConfig;

    @Mock
    private Jetty9Server jettyServer;

    @Mock
    private SystemEnvironment systemEnvironment;

    @Test
    public void shouldCreateSslConnectorWithRelevantPortAndTimeout() {
        Assert.assertThat(((sslSocketConnector.getConnector()) instanceof ServerConnector), is(true));
        ServerConnector connector = ((ServerConnector) (sslSocketConnector.getConnector()));
        Assert.assertThat(connector.getPort(), is(1234));
        Assert.assertThat(connector.getHost(), is("foo"));
        Assert.assertThat(connector.getIdleTimeout(), is(200L));
    }

    @Test
    public void shouldSetupSslContextWithKeystoreAndTruststore() throws IOException {
        ServerConnector connector = ((ServerConnector) (sslSocketConnector.getConnector()));
        Collection<ConnectionFactory> connectionFactories = connector.getConnectionFactories();
        SslContextFactory sslContextFactory = findSslContextFactory(connectionFactories);
        Assert.assertThat(sslContextFactory.getKeyStorePath(), is(keystore.getCanonicalFile().toPath().toAbsolutePath().toUri().toString()));
        Assert.assertThat(sslContextFactory.getTrustStorePath(), is(truststore.getCanonicalFile().toPath().toAbsolutePath().toUri().toString()));
        Assert.assertThat(sslContextFactory.getWantClientAuth(), is(true));
    }

    @Test
    public void shouldSetupCipherSuitesToBeIncluded() {
        ServerConnector connector = ((ServerConnector) (sslSocketConnector.getConnector()));
        Collection<ConnectionFactory> connectionFactories = connector.getConnectionFactories();
        SslContextFactory sslContextFactory = findSslContextFactory(connectionFactories);
        List<String> includedCipherSuites = new java.util.ArrayList(Arrays.asList(sslContextFactory.getIncludeCipherSuites()));
        Assert.assertThat(includedCipherSuites.size(), is(1));
        Assert.assertThat(includedCipherSuites.contains("FOO"), is(true));
    }

    @Test
    public void shouldSetupHttpConnectionFactory() {
        ServerConnector connector = ((ServerConnector) (sslSocketConnector.getConnector()));
        Collection<ConnectionFactory> connectionFactories = connector.getConnectionFactories();
        HttpConnectionFactory httpConnectionFactory = getHttpConnectionFactory(connectionFactories);
        Assert.assertThat(httpConnectionFactory.getHttpConfiguration().getOutputBufferSize(), is(100));
        Assert.assertThat(httpConnectionFactory.getHttpConfiguration().getCustomizers().size(), is(2));
        Assert.assertThat(httpConnectionFactory.getHttpConfiguration().getCustomizers().get(0), instanceOf(SecureRequestCustomizer.class));
        Assert.assertThat(httpConnectionFactory.getHttpConfiguration().getCustomizers().get(1), instanceOf(ForwardedRequestCustomizer.class));
    }

    @Test
    public void shouldNotSendAServerHeaderForSecurityReasons() throws Exception {
        HttpConnectionFactory httpConnectionFactory = getHttpConnectionFactory(sslSocketConnector.getConnector().getConnectionFactories());
        HttpConfiguration configuration = httpConnectionFactory.getHttpConfiguration();
        Assert.assertThat(configuration.getSendServerVersion(), is(false));
    }

    @Test
    public void shouldLeaveTheDefaultCipherSuiteInclusionAndExclusionListUnTouchedIfNotOverridden() {
        Mockito.when(goSSLConfig.getCipherSuitesToBeIncluded()).thenReturn(null);
        Mockito.when(goSSLConfig.getCipherSuitesToBeExcluded()).thenReturn(null);
        sslSocketConnector = new GoSslSocketConnector(jettyServer, "password", systemEnvironment, goSSLConfig);
        ServerConnector connector = ((ServerConnector) (sslSocketConnector.getConnector()));
        Collection<ConnectionFactory> connectionFactories = connector.getConnectionFactories();
        SslContextFactory sslContextFactory = findSslContextFactory(connectionFactories);
        Assert.assertThat(sslContextFactory.getExcludeCipherSuites(), is(arrayWithSize(5)));
        Assert.assertThat(sslContextFactory.getExcludeCipherSuites(), is(arrayContainingInAnyOrder("^.*_(MD5|SHA|SHA1)$", "^TLS_RSA_.*$", "^SSL_.*$", "^.*_NULL_.*$", "^.*_anon_.*$")));
        Assert.assertThat(sslContextFactory.getIncludeCipherSuites(), is(emptyArray()));
    }

    @Test
    public void shouldClearOutDefaultProtocolsAndCipherSetByJettyIfFlagIsSet() {
        Mockito.when(systemEnvironment.get(GO_SSL_CONFIG_CLEAR_JETTY_DEFAULT_EXCLUSIONS)).thenReturn(true);
        Mockito.when(goSSLConfig.getProtocolsToBeExcluded()).thenReturn(null);
        Mockito.when(goSSLConfig.getProtocolsToBeIncluded()).thenReturn(null);
        Mockito.when(goSSLConfig.getCipherSuitesToBeIncluded()).thenReturn(null);
        Mockito.when(goSSLConfig.getCipherSuitesToBeExcluded()).thenReturn(null);
        sslSocketConnector = new GoSslSocketConnector(jettyServer, "password", systemEnvironment, goSSLConfig);
        ServerConnector connector = ((ServerConnector) (sslSocketConnector.getConnector()));
        Collection<ConnectionFactory> connectionFactories = connector.getConnectionFactories();
        SslContextFactory sslContextFactory = findSslContextFactory(connectionFactories);
        Assert.assertThat(sslContextFactory.getExcludeProtocols().length, is(0));
        Assert.assertThat(sslContextFactory.getIncludeProtocols().length, is(0));
        Assert.assertThat(sslContextFactory.getExcludeCipherSuites().length, is(0));
        Assert.assertThat(sslContextFactory.getIncludeCipherSuites().length, is(0));
    }

    @Test
    public void shouldOverrideTheDefaultCipherSuiteExclusionListIfConfigured() {
        Mockito.when(goSSLConfig.getCipherSuitesToBeExcluded()).thenReturn(new String[]{ "*MD5*" });
        Mockito.when(goSSLConfig.getCipherSuitesToBeIncluded()).thenReturn(new String[]{ "*ECDHE*" });
        sslSocketConnector = new GoSslSocketConnector(jettyServer, "password", systemEnvironment, goSSLConfig);
        ServerConnector connector = ((ServerConnector) (sslSocketConnector.getConnector()));
        Collection<ConnectionFactory> connectionFactories = connector.getConnectionFactories();
        SslContextFactory sslContextFactory = findSslContextFactory(connectionFactories);
        Assert.assertThat(sslContextFactory.getExcludeCipherSuites().length, is(1));
        Assert.assertThat(sslContextFactory.getExcludeCipherSuites()[0], is("*MD5*"));
        Assert.assertThat(sslContextFactory.getIncludeCipherSuites().length, is(1));
        Assert.assertThat(sslContextFactory.getIncludeCipherSuites()[0], is("*ECDHE*"));
    }

    @Test
    public void shouldLeaveTheDefaultProtocolInclusionAndExclusionListUnTouchedIfNotOverridden() {
        Mockito.when(goSSLConfig.getProtocolsToBeIncluded()).thenReturn(null);
        Mockito.when(goSSLConfig.getProtocolsToBeExcluded()).thenReturn(null);
        sslSocketConnector = new GoSslSocketConnector(jettyServer, "password", systemEnvironment, goSSLConfig);
        ServerConnector connector = ((ServerConnector) (sslSocketConnector.getConnector()));
        Collection<ConnectionFactory> connectionFactories = connector.getConnectionFactories();
        SslContextFactory sslContextFactory = findSslContextFactory(connectionFactories);
        Assert.assertThat(sslContextFactory.getExcludeProtocols().length, is(4));
        Assert.assertThat(Arrays.asList(sslContextFactory.getExcludeProtocols()).containsAll(Arrays.asList("SSL", "SSLv2", "SSLv2Hello", "SSLv3")), is(true));
        Assert.assertThat(sslContextFactory.getIncludeProtocols().length, is(0));
    }

    @Test
    public void shouldOverrideTheDefaultProtocolExclusionListIfConfigured() {
        Mockito.when(goSSLConfig.getProtocolsToBeExcluded()).thenReturn(new String[]{ "SSL", "TLS1.0", "TLS1.1" });
        Mockito.when(goSSLConfig.getProtocolsToBeIncluded()).thenReturn(new String[]{ "TLS1.2" });
        sslSocketConnector = new GoSslSocketConnector(jettyServer, "password", systemEnvironment, goSSLConfig);
        ServerConnector connector = ((ServerConnector) (sslSocketConnector.getConnector()));
        Collection<ConnectionFactory> connectionFactories = connector.getConnectionFactories();
        SslContextFactory sslContextFactory = findSslContextFactory(connectionFactories);
        Assert.assertThat(sslContextFactory.getExcludeProtocols().length, is(3));
        Assert.assertThat(Arrays.asList(sslContextFactory.getExcludeProtocols()).containsAll(Arrays.asList("SSL", "TLS1.0", "TLS1.1")), is(true));
        Assert.assertThat(sslContextFactory.getIncludeProtocols().length, is(1));
        Assert.assertThat(sslContextFactory.getIncludeProtocols()[0], is("TLS1.2"));
    }
}

