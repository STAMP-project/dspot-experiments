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


import SecurableCommunicationChannel.ALL;
import SecurableCommunicationChannel.CLUSTER;
import SecurableCommunicationChannel.GATEWAY;
import SecurableCommunicationChannel.JMX;
import SecurableCommunicationChannel.LOCATOR;
import SecurableCommunicationChannel.SERVER;
import SecurableCommunicationChannel.WEB;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.apache.geode.distributed.ConfigurationProperties;
import org.apache.geode.distributed.internal.DistributionConfigImpl;
import org.apache.geode.test.junit.categories.MembershipTest;
import org.apache.geode.util.test.TestUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MembershipTest.class })
public class SocketCreatorFactoryJUnitTest {
    @Test
    public void testNewSSLConfigSSLComponentLocator() throws Exception {
        Properties properties = configureSSLProperties(LOCATOR.getConstant());
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(properties);
        SocketCreatorFactory.setDistributionConfig(distributionConfig);
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(LOCATOR).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(CLUSTER).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(GATEWAY).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(JMX).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(SERVER).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(WEB).useSSL());
    }

    @Test
    public void testNewSSLConfigSSLComponentALL() throws Exception {
        Properties properties = configureSSLProperties(ALL.getConstant());
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(properties);
        SocketCreatorFactory.setDistributionConfig(distributionConfig);
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(CLUSTER).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(LOCATOR).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(GATEWAY).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(JMX).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(SERVER).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(WEB).useSSL());
    }

    @Test
    public void testNewSSLConfigSSLComponentCLUSTER() throws Exception {
        Properties properties = configureSSLProperties(CLUSTER.getConstant());
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(properties);
        SocketCreatorFactory.setDistributionConfig(distributionConfig);
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(CLUSTER).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(GATEWAY).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(JMX).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(SERVER).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(WEB).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(LOCATOR).useSSL());
    }

    @Test
    public void testNewSSLConfigSSLComponentGATEWAY() throws Exception {
        Properties properties = configureSSLProperties(GATEWAY.getConstant());
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(properties);
        SocketCreatorFactory.setDistributionConfig(distributionConfig);
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(CLUSTER).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(GATEWAY).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(JMX).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(SERVER).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(WEB).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(LOCATOR).useSSL());
    }

    @Test
    public void testNewSSLConfigSSLComponentHTTP_SERVICE() throws Exception {
        Properties properties = configureSSLProperties(WEB.getConstant());
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(properties);
        SocketCreatorFactory.setDistributionConfig(distributionConfig);
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(CLUSTER).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(GATEWAY).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(JMX).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(SERVER).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(WEB).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(LOCATOR).useSSL());
    }

    @Test
    public void testNewSSLConfigSSLComponentJMX() throws Exception {
        Properties properties = configureSSLProperties(JMX.getConstant());
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(properties);
        SocketCreatorFactory.setDistributionConfig(distributionConfig);
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(CLUSTER).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(GATEWAY).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(JMX).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(SERVER).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(WEB).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(LOCATOR).useSSL());
    }

    @Test
    public void testNewSSLConfigSSLComponentSERVER() throws Exception {
        Properties properties = configureSSLProperties(SERVER.getConstant());
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(properties);
        SocketCreatorFactory.setDistributionConfig(distributionConfig);
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(CLUSTER).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(GATEWAY).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(JMX).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(SERVER).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(WEB).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(LOCATOR).useSSL());
    }

    @Test
    public void testNewSSLConfigSSLComponentCombinations1() throws Exception {
        Properties properties = configureSSLProperties(commaDelimitedString(CLUSTER.getConstant(), SERVER.getConstant()));
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(properties);
        SocketCreatorFactory.setDistributionConfig(distributionConfig);
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(CLUSTER).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(GATEWAY).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(JMX).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(SERVER).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(WEB).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(LOCATOR).useSSL());
    }

    @Test
    public void testNewSSLConfigSSLComponentCombinations2() throws Exception {
        Properties properties = configureSSLProperties(commaDelimitedString(CLUSTER.getConstant(), SERVER.getConstant(), WEB.getConstant(), JMX.getConstant()));
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(properties);
        SocketCreatorFactory.setDistributionConfig(distributionConfig);
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(CLUSTER).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(GATEWAY).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(JMX).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(SERVER).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(WEB).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(LOCATOR).useSSL());
    }

    @Test
    public void testNewSSLConfigSSLComponentAliasWithMultiKeyStore() throws Exception {
        Properties properties = configureSSLProperties(ALL.getConstant());
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE, TestUtil.getResourcePath(getClass(), "/org/apache/geode/internal/net/multiKey.jks"));
        properties.setProperty(ConfigurationProperties.SSL_TRUSTSTORE, TestUtil.getResourcePath(getClass(), "/org/apache/geode/internal/net/multiKeyTrust.jks"));
        properties.setProperty(ConfigurationProperties.SSL_CLUSTER_ALIAS, "clusterKey");
        properties.setProperty(ConfigurationProperties.SSL_DEFAULT_ALIAS, "serverKey");
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(properties);
        SocketCreatorFactory.setDistributionConfig(distributionConfig);
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(CLUSTER).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(GATEWAY).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(JMX).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(SERVER).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(WEB).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(LOCATOR).useSSL());
    }

    @Test
    public void testNewSSLConfigSSLComponentWithoutAliasWithMultiKeyStore() throws Exception {
        Properties properties = configureSSLProperties(ALL.getConstant());
        properties.setProperty(ConfigurationProperties.SSL_KEYSTORE, TestUtil.getResourcePath(getClass(), "/org/apache/geode/internal/net/multiKey.jks"));
        properties.setProperty(ConfigurationProperties.SSL_TRUSTSTORE, TestUtil.getResourcePath(getClass(), "/org/apache/geode/internal/net/multiKeyTrust.jks"));
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(properties);
        SocketCreatorFactory.setDistributionConfig(distributionConfig);
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(CLUSTER).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(GATEWAY).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(JMX).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(SERVER).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(WEB).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(LOCATOR).useSSL());
    }

    @Test
    public void testLegacyServerSSLConfig() throws IOException {
        File jks = findTestJKS();
        Properties properties = new Properties();
        properties.setProperty(ConfigurationProperties.MCAST_PORT, "0");
        properties.setProperty(ConfigurationProperties.SERVER_SSL_REQUIRE_AUTHENTICATION, "true");
        properties.setProperty(ConfigurationProperties.SERVER_SSL_ENABLED, "true");
        properties.setProperty(ConfigurationProperties.SERVER_SSL_CIPHERS, "TLS_RSA_WITH_AES_256_CBC_SHA256,TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384,TLS_DHE_RSA_WITH_AES_256_CBC_SHA256,TLS_DHE_DSS_WITH_AES_256_CBC_SHA256,TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA");
        properties.setProperty(ConfigurationProperties.SERVER_SSL_PROTOCOLS, "TLSv1,TLSv1.1,TLSv1.2");
        properties.setProperty(ConfigurationProperties.SERVER_SSL_KEYSTORE, jks.getCanonicalPath());
        properties.setProperty(ConfigurationProperties.SERVER_SSL_KEYSTORE_PASSWORD, "password");
        properties.setProperty(ConfigurationProperties.SERVER_SSL_KEYSTORE_TYPE, "JKS");
        properties.setProperty(ConfigurationProperties.SERVER_SSL_TRUSTSTORE, jks.getCanonicalPath());
        properties.setProperty(ConfigurationProperties.SERVER_SSL_TRUSTSTORE_PASSWORD, "password");
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(properties);
        SocketCreatorFactory.setDistributionConfig(distributionConfig);
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(CLUSTER).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(GATEWAY).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(JMX).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(SERVER).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(WEB).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(LOCATOR).useSSL());
    }

    @Test
    public void testLegacyClusterSSLConfig() throws IOException {
        File jks = findTestJKS();
        Properties properties = new Properties();
        properties.setProperty(ConfigurationProperties.MCAST_PORT, "0");
        properties.setProperty(ConfigurationProperties.CLUSTER_SSL_REQUIRE_AUTHENTICATION, "true");
        properties.setProperty(ConfigurationProperties.CLUSTER_SSL_ENABLED, "true");
        properties.setProperty(ConfigurationProperties.CLUSTER_SSL_CIPHERS, "TLS_RSA_WITH_AES_256_CBC_SHA256,TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384,TLS_DHE_RSA_WITH_AES_256_CBC_SHA256,TLS_DHE_DSS_WITH_AES_256_CBC_SHA256,TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA");
        properties.setProperty(ConfigurationProperties.CLUSTER_SSL_PROTOCOLS, "TLSv1,TLSv1.1,TLSv1.2");
        properties.setProperty(ConfigurationProperties.CLUSTER_SSL_KEYSTORE, jks.getCanonicalPath());
        properties.setProperty(ConfigurationProperties.CLUSTER_SSL_KEYSTORE_PASSWORD, "password");
        properties.setProperty(ConfigurationProperties.CLUSTER_SSL_KEYSTORE_TYPE, "JKS");
        properties.setProperty(ConfigurationProperties.CLUSTER_SSL_TRUSTSTORE, jks.getCanonicalPath());
        properties.setProperty(ConfigurationProperties.CLUSTER_SSL_TRUSTSTORE_PASSWORD, "password");
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(properties);
        SocketCreatorFactory.setDistributionConfig(distributionConfig);
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(CLUSTER).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(GATEWAY).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(JMX).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(SERVER).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(WEB).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(LOCATOR).useSSL());
    }

    @Test
    public void testLegacyJMXSSLConfig() throws IOException {
        File jks = findTestJKS();
        Properties properties = new Properties();
        properties.setProperty(ConfigurationProperties.MCAST_PORT, "0");
        properties.setProperty(ConfigurationProperties.JMX_MANAGER_SSL_REQUIRE_AUTHENTICATION, "true");
        properties.setProperty(ConfigurationProperties.JMX_MANAGER_SSL_ENABLED, "true");
        properties.setProperty(ConfigurationProperties.JMX_MANAGER_SSL_CIPHERS, "TLS_RSA_WITH_AES_256_CBC_SHA256,TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384,TLS_DHE_RSA_WITH_AES_256_CBC_SHA256,TLS_DHE_DSS_WITH_AES_256_CBC_SHA256,TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA");
        properties.setProperty(ConfigurationProperties.JMX_MANAGER_SSL_PROTOCOLS, "TLSv1,TLSv1.1,TLSv1.2");
        properties.setProperty(ConfigurationProperties.JMX_MANAGER_SSL_KEYSTORE, jks.getCanonicalPath());
        properties.setProperty(ConfigurationProperties.JMX_MANAGER_SSL_KEYSTORE_PASSWORD, "password");
        properties.setProperty(ConfigurationProperties.JMX_MANAGER_SSL_KEYSTORE_TYPE, "JKS");
        properties.setProperty(ConfigurationProperties.JMX_MANAGER_SSL_TRUSTSTORE, jks.getCanonicalPath());
        properties.setProperty(ConfigurationProperties.JMX_MANAGER_SSL_TRUSTSTORE_PASSWORD, "password");
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(properties);
        SocketCreatorFactory.setDistributionConfig(distributionConfig);
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(CLUSTER).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(GATEWAY).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(JMX).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(SERVER).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(WEB).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(LOCATOR).useSSL());
    }

    @Test
    public void testLegacyGatewaySSLConfig() throws IOException {
        File jks = findTestJKS();
        Properties properties = new Properties();
        properties.setProperty(ConfigurationProperties.MCAST_PORT, "0");
        properties.setProperty(ConfigurationProperties.GATEWAY_SSL_REQUIRE_AUTHENTICATION, "true");
        properties.setProperty(ConfigurationProperties.GATEWAY_SSL_ENABLED, "true");
        properties.setProperty(ConfigurationProperties.GATEWAY_SSL_CIPHERS, "TLS_RSA_WITH_AES_256_CBC_SHA256,TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384,TLS_DHE_RSA_WITH_AES_256_CBC_SHA256,TLS_DHE_DSS_WITH_AES_256_CBC_SHA256,TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA");
        properties.setProperty(ConfigurationProperties.GATEWAY_SSL_PROTOCOLS, "TLSv1,TLSv1.1,TLSv1.2");
        properties.setProperty(ConfigurationProperties.GATEWAY_SSL_KEYSTORE, jks.getCanonicalPath());
        properties.setProperty(ConfigurationProperties.GATEWAY_SSL_KEYSTORE_PASSWORD, "password");
        properties.setProperty(ConfigurationProperties.GATEWAY_SSL_KEYSTORE_TYPE, "JKS");
        properties.setProperty(ConfigurationProperties.GATEWAY_SSL_TRUSTSTORE, jks.getCanonicalPath());
        properties.setProperty(ConfigurationProperties.GATEWAY_SSL_TRUSTSTORE_PASSWORD, "password");
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(properties);
        SocketCreatorFactory.setDistributionConfig(distributionConfig);
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(CLUSTER).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(GATEWAY).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(JMX).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(SERVER).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(WEB).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(LOCATOR).useSSL());
    }

    @Test
    public void testLegacyHttpServiceSSLConfig() throws IOException {
        File jks = findTestJKS();
        Properties properties = new Properties();
        properties.setProperty(ConfigurationProperties.MCAST_PORT, "0");
        properties.setProperty(ConfigurationProperties.HTTP_SERVICE_SSL_REQUIRE_AUTHENTICATION, "true");
        properties.setProperty(ConfigurationProperties.HTTP_SERVICE_SSL_ENABLED, "true");
        properties.setProperty(ConfigurationProperties.HTTP_SERVICE_SSL_CIPHERS, "TLS_RSA_WITH_AES_256_CBC_SHA256,TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384,TLS_DHE_RSA_WITH_AES_256_CBC_SHA256,TLS_DHE_DSS_WITH_AES_256_CBC_SHA256,TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA");
        properties.setProperty(ConfigurationProperties.HTTP_SERVICE_SSL_PROTOCOLS, "TLSv1,TLSv1.1,TLSv1.2");
        properties.setProperty(ConfigurationProperties.HTTP_SERVICE_SSL_KEYSTORE, jks.getCanonicalPath());
        properties.setProperty(ConfigurationProperties.HTTP_SERVICE_SSL_KEYSTORE_PASSWORD, "password");
        properties.setProperty(ConfigurationProperties.HTTP_SERVICE_SSL_KEYSTORE_TYPE, "JKS");
        properties.setProperty(ConfigurationProperties.HTTP_SERVICE_SSL_TRUSTSTORE, jks.getCanonicalPath());
        properties.setProperty(ConfigurationProperties.HTTP_SERVICE_SSL_TRUSTSTORE_PASSWORD, "password");
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(properties);
        SocketCreatorFactory.setDistributionConfig(distributionConfig);
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(CLUSTER).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(GATEWAY).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(JMX).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(SERVER).useSSL());
        Assert.assertTrue(SocketCreatorFactory.getSocketCreatorForComponent(WEB).useSSL());
        Assert.assertFalse(SocketCreatorFactory.getSocketCreatorForComponent(LOCATOR).useSSL());
    }
}

