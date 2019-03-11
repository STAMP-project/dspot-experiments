/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.net;


import SSLConfigurationFactory.JAVAX_KEYSTORE;
import SSLConfigurationFactory.JAVAX_KEYSTORE_PASSWORD;
import SSLConfigurationFactory.JAVAX_KEYSTORE_TYPE;
import SSLConfigurationFactory.JAVAX_TRUSTSTORE;
import SSLConfigurationFactory.JAVAX_TRUSTSTORE_PASSWORD;
import SSLConfigurationFactory.JAVAX_TRUSTSTORE_TYPE;
import SecurableCommunicationChannel.CLUSTER;
import SecurableCommunicationChannel.GATEWAY;
import SecurableCommunicationChannel.JMX;
import SecurableCommunicationChannel.LOCATOR;
import SecurableCommunicationChannel.SERVER;
import SecurableCommunicationChannel.WEB;
import java.security.KeyStore;
import java.util.Properties;
import org.apache.geode.GemFireConfigException;
import org.apache.geode.distributed.ConfigurationProperties;
import org.apache.geode.distributed.internal.DistributionConfigImpl;
import org.apache.geode.internal.admin.SSLConfig;
import org.apache.geode.internal.security.SecurableCommunicationChannel;
import org.apache.geode.test.junit.categories.MembershipTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.experimental.categories.Category;


@Category(MembershipTest.class)
public class SSLConfigurationFactoryJUnitTest {
    @Rule
    public RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    @Test
    public void getNonSSLConfiguration() {
        Properties properties = new Properties();
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(properties);
        SSLConfigurationFactory.setDistributionConfig(distributionConfig);
        for (SecurableCommunicationChannel securableComponent : SecurableCommunicationChannel.values()) {
            assertSSLConfig(properties, SSLConfigurationFactory.getSSLConfigForComponent(securableComponent), securableComponent, distributionConfig);
        }
    }

    @Test
    public void getSSLConfigForComponentShouldThrowExceptionForUnknownComponents() {
        Properties properties = new Properties();
        properties.setProperty(ConfigurationProperties.SSL_ENABLED_COMPONENTS, "none");
        assertThatThrownBy(() -> new DistributionConfigImpl(properties)).isInstanceOf(IllegalArgumentException.class).hasCauseInstanceOf(GemFireConfigException.class).hasMessageContaining("There is no registered component for the name: none");
    }

    @Test
    public void getSSLConfigWithCommaDelimitedProtocols() {
        Properties properties = new Properties();
        properties.setProperty(ConfigurationProperties.SSL_ENABLED_COMPONENTS, "all");
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE, "someKeyStore");
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE_PASSWORD, "keystorePassword");
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE_TYPE, "JKS");
        properties.setProperty(ConfigurationProperties.SSL_TRUSTSTORE, "someKeyStore");
        properties.setProperty(ConfigurationProperties.SSL_TRUSTSTORE_PASSWORD, "keystorePassword");
        properties.setProperty(ConfigurationProperties.SSL_DEFAULT_ALIAS, "defaultAlias");
        properties.setProperty(ConfigurationProperties.SSL_CIPHERS, "Cipher1,Cipher2");
        properties.setProperty(ConfigurationProperties.SSL_PROTOCOLS, "Protocol1,Protocol2");
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(properties);
        SSLConfigurationFactory.setDistributionConfig(distributionConfig);
        assertSSLConfigForChannels(properties, distributionConfig);
    }

    @Test
    public void getSSLConfigWithCommaDelimitedCiphers() {
        Properties properties = new Properties();
        properties.setProperty(ConfigurationProperties.SSL_ENABLED_COMPONENTS, "all");
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE, "someKeyStore");
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE_PASSWORD, "keystorePassword");
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE_TYPE, "JKS");
        properties.setProperty(ConfigurationProperties.SSL_TRUSTSTORE, "someKeyStore");
        properties.setProperty(ConfigurationProperties.SSL_TRUSTSTORE_PASSWORD, "keystorePassword");
        properties.setProperty(ConfigurationProperties.SSL_DEFAULT_ALIAS, "defaultAlias");
        properties.setProperty(ConfigurationProperties.SSL_CIPHERS, "Cipher1,Cipher2");
        properties.setProperty(ConfigurationProperties.SSL_PROTOCOLS, "any");
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(properties);
        SSLConfigurationFactory.setDistributionConfig(distributionConfig);
        assertSSLConfigForChannels(properties, distributionConfig);
    }

    @Test
    public void getSSLConfigForComponentALL() {
        Properties properties = new Properties();
        properties.setProperty(ConfigurationProperties.SSL_ENABLED_COMPONENTS, "all");
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE, "someKeyStore");
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE_PASSWORD, "keystorePassword");
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE_TYPE, "JKS");
        properties.setProperty(ConfigurationProperties.SSL_TRUSTSTORE, "someKeyStore");
        properties.setProperty(ConfigurationProperties.SSL_TRUSTSTORE_PASSWORD, "keystorePassword");
        properties.setProperty(ConfigurationProperties.SSL_DEFAULT_ALIAS, "defaultAlias");
        properties.setProperty(ConfigurationProperties.SSL_CIPHERS, "any");
        properties.setProperty(ConfigurationProperties.SSL_PROTOCOLS, "any");
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(properties);
        SSLConfigurationFactory.setDistributionConfig(distributionConfig);
        assertSSLConfigForChannels(properties, distributionConfig);
    }

    @Test
    public void getSSLConfigForComponentHTTPService() {
        Properties properties = new Properties();
        properties.setProperty(ConfigurationProperties.SSL_ENABLED_COMPONENTS, WEB.getConstant());
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE, "someKeyStore");
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE_PASSWORD, "keystorePassword");
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE_TYPE, "JKS");
        properties.setProperty(ConfigurationProperties.SSL_TRUSTSTORE, "someKeyStore");
        properties.setProperty(ConfigurationProperties.SSL_TRUSTSTORE_PASSWORD, "keystorePassword");
        properties.setProperty(ConfigurationProperties.SSL_DEFAULT_ALIAS, "defaultAlias");
        properties.setProperty(ConfigurationProperties.SSL_CIPHERS, "any");
        properties.setProperty(ConfigurationProperties.SSL_PROTOCOLS, "any");
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(properties);
        SSLConfigurationFactory.setDistributionConfig(distributionConfig);
        assertSSLConfigForChannels(properties, distributionConfig);
    }

    @Test
    public void getSSLConfigForComponentHTTPServiceWithAlias() {
        Properties properties = new Properties();
        properties.setProperty(ConfigurationProperties.SSL_ENABLED_COMPONENTS, WEB.getConstant());
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE, "someKeyStore");
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE_PASSWORD, "keystorePassword");
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE_TYPE, "JKS");
        properties.setProperty(ConfigurationProperties.SSL_TRUSTSTORE, "someKeyStore");
        properties.setProperty(ConfigurationProperties.SSL_TRUSTSTORE_PASSWORD, "keystorePassword");
        properties.setProperty(ConfigurationProperties.SSL_DEFAULT_ALIAS, "defaultAlias");
        properties.setProperty(ConfigurationProperties.SSL_WEB_ALIAS, "httpAlias");
        properties.setProperty(ConfigurationProperties.SSL_CIPHERS, "any");
        properties.setProperty(ConfigurationProperties.SSL_PROTOCOLS, "any");
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(properties);
        SSLConfigurationFactory.setDistributionConfig(distributionConfig);
        assertSSLConfigForChannels(properties, distributionConfig);
    }

    @Test
    public void getSSLConfigForComponentHTTPServiceWithMutualAuth() {
        Properties properties = new Properties();
        properties.setProperty(ConfigurationProperties.SSL_ENABLED_COMPONENTS, WEB.getConstant());
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE, "someKeyStore");
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE_PASSWORD, "keystorePassword");
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE_TYPE, "JKS");
        properties.setProperty(ConfigurationProperties.SSL_TRUSTSTORE, "someKeyStore");
        properties.setProperty(ConfigurationProperties.SSL_TRUSTSTORE_PASSWORD, "keystorePassword");
        properties.setProperty(ConfigurationProperties.SSL_DEFAULT_ALIAS, "defaultAlias");
        properties.setProperty(ConfigurationProperties.SSL_WEB_ALIAS, "httpAlias");
        properties.setProperty(ConfigurationProperties.SSL_WEB_SERVICE_REQUIRE_AUTHENTICATION, "true");
        properties.setProperty(ConfigurationProperties.SSL_CIPHERS, "any");
        properties.setProperty(ConfigurationProperties.SSL_PROTOCOLS, "any");
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(properties);
        SSLConfigurationFactory.setDistributionConfig(distributionConfig);
        assertSSLConfigForChannels(properties, distributionConfig);
    }

    @Test
    public void getSSLConfigUsingJavaProperties() {
        Properties properties = new Properties();
        properties.setProperty(ConfigurationProperties.CLUSTER_SSL_ENABLED, "true");
        properties.setProperty(ConfigurationProperties.MCAST_PORT, "0");
        System.setProperty(JAVAX_KEYSTORE, "keystore");
        System.setProperty(JAVAX_KEYSTORE_TYPE, KeyStore.getDefaultType());
        System.setProperty(JAVAX_KEYSTORE_PASSWORD, "keystorePassword");
        System.setProperty(JAVAX_TRUSTSTORE, "truststore");
        System.setProperty(JAVAX_TRUSTSTORE_PASSWORD, "truststorePassword");
        System.setProperty(JAVAX_TRUSTSTORE_TYPE, KeyStore.getDefaultType());
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(properties);
        SSLConfigurationFactory.setDistributionConfig(distributionConfig);
        SSLConfig sslConfig = SSLConfigurationFactory.getSSLConfigForComponent(CLUSTER);
        assertThat(sslConfig.isEnabled()).isTrue();
        assertThat(sslConfig.getKeystore()).isEqualTo(System.getProperty(JAVAX_KEYSTORE));
        assertThat(sslConfig.getKeystorePassword()).isEqualTo(System.getProperty(JAVAX_KEYSTORE_PASSWORD));
        assertThat(sslConfig.getKeystoreType()).isEqualTo(System.getProperty(JAVAX_KEYSTORE_TYPE));
        assertThat(sslConfig.getTruststore()).isEqualTo(System.getProperty(JAVAX_TRUSTSTORE));
        assertThat(sslConfig.getTruststorePassword()).isEqualTo(System.getProperty(JAVAX_TRUSTSTORE_PASSWORD));
        assertThat(sslConfig.getTruststoreType()).isEqualTo(System.getProperty(JAVAX_TRUSTSTORE_TYPE));
        assertThat(sslConfig.isEnabled()).isTrue();
        assertThat(sslConfig.getKeystore()).isEqualTo(System.getProperty(JAVAX_KEYSTORE));
        assertThat(sslConfig.getKeystorePassword()).isEqualTo(System.getProperty(JAVAX_KEYSTORE_PASSWORD));
        assertThat(sslConfig.getKeystoreType()).isEqualTo(System.getProperty(JAVAX_KEYSTORE_TYPE));
        assertThat(sslConfig.getTruststore()).isEqualTo(System.getProperty(JAVAX_TRUSTSTORE));
        assertThat(sslConfig.getTruststorePassword()).isEqualTo(System.getProperty(JAVAX_TRUSTSTORE_PASSWORD));
        assertThat(sslConfig.getTruststoreType()).isEqualTo(System.getProperty(JAVAX_TRUSTSTORE_TYPE));
    }

    @Test
    public void getSSLHTTPMutualAuthenticationOffWithDefaultConfiguration() {
        Properties properties = new Properties();
        properties.setProperty(ConfigurationProperties.CLUSTER_SSL_ENABLED, "true");
        properties.setProperty(ConfigurationProperties.MCAST_PORT, "0");
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(properties);
        SSLConfigurationFactory.setDistributionConfig(distributionConfig);
        SSLConfig sslConfig = SSLConfigurationFactory.getSSLConfigForComponent(WEB);
        assertThat(sslConfig.isRequireAuth()).isFalse();
        assertThat(sslConfig.isEnabled()).isTrue();
        sslConfig = SSLConfigurationFactory.getSSLConfigForComponent(CLUSTER);
        assertThat(sslConfig.isRequireAuth()).isTrue();
        assertThat(sslConfig.isEnabled()).isTrue();
        sslConfig = SSLConfigurationFactory.getSSLConfigForComponent(GATEWAY);
        assertThat(sslConfig.isRequireAuth()).isTrue();
        assertThat(sslConfig.isEnabled()).isTrue();
        sslConfig = SSLConfigurationFactory.getSSLConfigForComponent(SERVER);
        assertThat(sslConfig.isRequireAuth()).isTrue();
        assertThat(sslConfig.isEnabled()).isTrue();
        sslConfig = SSLConfigurationFactory.getSSLConfigForComponent(JMX);
        assertThat(sslConfig.isRequireAuth()).isTrue();
        assertThat(sslConfig.isEnabled()).isTrue();
    }

    @Test
    public void setDistributionConfig() {
        Properties properties = new Properties();
        properties.setProperty(ConfigurationProperties.SSL_ENABLED_COMPONENTS, "all");
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE, "someKeyStore");
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(properties);
        SSLConfigurationFactory.setDistributionConfig(distributionConfig);
        SSLConfig sslConfig = SSLConfigurationFactory.getSSLConfigForComponent(LOCATOR);
        assertThat(sslConfig.isEnabled()).isTrue();
        assertThat(sslConfig.getKeystore()).isEqualTo("someKeyStore");
        properties.setProperty(ConfigurationProperties.SSL_ENABLED_COMPONENTS, JMX.getConstant());
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE, "someOtherKeyStore");
        sslConfig = SSLConfigurationFactory.getSSLConfigForComponent(properties, LOCATOR);
        assertThat(sslConfig.isEnabled()).isFalse();
        assertThat(sslConfig.getKeystore()).isEqualTo("someOtherKeyStore");
    }
}

