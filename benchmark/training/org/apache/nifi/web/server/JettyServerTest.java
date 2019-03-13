/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.web.server;


import BouncyCastleProvider.PROVIDER_NAME;
import KeystoreType.JKS;
import KeystoreType.PKCS12;
import NiFiProperties.SECURITY_KEYSTORE_PASSWD;
import NiFiProperties.SECURITY_KEYSTORE_TYPE;
import NiFiProperties.SECURITY_KEY_PASSWD;
import NiFiProperties.SECURITY_TRUSTSTORE_TYPE;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import org.apache.nifi.util.NiFiProperties;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class JettyServerTest {
    @Test
    public void testConfigureSslContextFactoryWithKeystorePasswordAndKeyPassword() {
        // Expect that if we set both passwords, KeyStore password is used for KeyStore, Key password is used for Key Manager
        String testKeystorePassword = "testKeystorePassword";
        String testKeyPassword = "testKeyPassword";
        final Map<String, String> addProps = new HashMap<>();
        addProps.put(SECURITY_KEYSTORE_PASSWD, testKeystorePassword);
        addProps.put(SECURITY_KEY_PASSWD, testKeyPassword);
        NiFiProperties nifiProperties = NiFiProperties.createBasicNiFiProperties(null, addProps);
        SslContextFactory contextFactory = Mockito.mock(SslContextFactory.class);
        JettyServer.configureSslContextFactory(contextFactory, nifiProperties);
        Mockito.verify(contextFactory).setKeyStorePassword(testKeystorePassword);
        Mockito.verify(contextFactory).setKeyManagerPassword(testKeyPassword);
    }

    @Test
    public void testConfigureSslContextFactoryWithKeyPassword() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        // Expect that with no KeyStore password, we will only need to set Key Manager Password
        String testKeyPassword = "testKeyPassword";
        final Map<String, String> addProps = new HashMap<>();
        addProps.put(SECURITY_KEY_PASSWD, testKeyPassword);
        NiFiProperties nifiProperties = NiFiProperties.createBasicNiFiProperties(null, addProps);
        SslContextFactory contextFactory = Mockito.mock(SslContextFactory.class);
        JettyServer.configureSslContextFactory(contextFactory, nifiProperties);
        Mockito.verify(contextFactory).setKeyManagerPassword(testKeyPassword);
        Mockito.verify(contextFactory, Mockito.never()).setKeyStorePassword(ArgumentMatchers.anyString());
    }

    @Test
    public void testConfigureSslContextFactoryWithKeystorePassword() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        // Expect that with no KeyPassword, we use the same one from the KeyStore
        String testKeystorePassword = "testKeystorePassword";
        final Map<String, String> addProps = new HashMap<>();
        addProps.put(SECURITY_KEYSTORE_PASSWD, testKeystorePassword);
        NiFiProperties nifiProperties = NiFiProperties.createBasicNiFiProperties(null, addProps);
        SslContextFactory contextFactory = Mockito.mock(SslContextFactory.class);
        JettyServer.configureSslContextFactory(contextFactory, nifiProperties);
        Mockito.verify(contextFactory).setKeyStorePassword(testKeystorePassword);
        Mockito.verify(contextFactory).setKeyManagerPassword(testKeystorePassword);
    }

    @Test
    public void testConfigureSslContextFactoryWithJksKeyStore() {
        // Expect that we will not set provider for jks keystore
        final Map<String, String> addProps = new HashMap<>();
        String keyStoreType = JKS.toString();
        addProps.put(SECURITY_KEYSTORE_TYPE, keyStoreType);
        NiFiProperties nifiProperties = NiFiProperties.createBasicNiFiProperties(null, addProps);
        SslContextFactory contextFactory = Mockito.mock(SslContextFactory.class);
        JettyServer.configureSslContextFactory(contextFactory, nifiProperties);
        Mockito.verify(contextFactory).setKeyStoreType(keyStoreType);
        Mockito.verify(contextFactory).setKeyStoreProvider(SUN_PROVIDER_NAME);
    }

    @Test
    public void testConfigureSslContextFactoryWithPkcsKeyStore() {
        // Expect that we will set Bouncy Castle provider for pkcs12 keystore
        final Map<String, String> addProps = new HashMap<>();
        String keyStoreType = PKCS12.toString();
        addProps.put(SECURITY_KEYSTORE_TYPE, keyStoreType);
        NiFiProperties nifiProperties = NiFiProperties.createBasicNiFiProperties(null, addProps);
        SslContextFactory contextFactory = Mockito.mock(SslContextFactory.class);
        JettyServer.configureSslContextFactory(contextFactory, nifiProperties);
        Mockito.verify(contextFactory).setKeyStoreType(keyStoreType);
        Mockito.verify(contextFactory).setKeyStoreProvider(PROVIDER_NAME);
    }

    @Test
    public void testConfigureSslContextFactoryWithJksTrustStore() {
        // Expect that we will not set provider for jks truststore
        final Map<String, String> addProps = new HashMap<>();
        String trustStoreType = JKS.toString();
        addProps.put(SECURITY_TRUSTSTORE_TYPE, trustStoreType);
        NiFiProperties nifiProperties = NiFiProperties.createBasicNiFiProperties(null, addProps);
        SslContextFactory contextFactory = Mockito.mock(SslContextFactory.class);
        JettyServer.configureSslContextFactory(contextFactory, nifiProperties);
        Mockito.verify(contextFactory).setTrustStoreType(trustStoreType);
        Mockito.verify(contextFactory).setTrustStoreProvider(SUN_PROVIDER_NAME);
    }

    @Test
    public void testConfigureSslContextFactoryWithPkcsTrustStore() {
        // Expect that we will set Bouncy Castle provider for pkcs12 truststore
        final Map<String, String> addProps = new HashMap<>();
        String trustStoreType = PKCS12.toString();
        addProps.put(SECURITY_TRUSTSTORE_TYPE, trustStoreType);
        NiFiProperties nifiProperties = NiFiProperties.createBasicNiFiProperties(null, addProps);
        SslContextFactory contextFactory = Mockito.mock(SslContextFactory.class);
        JettyServer.configureSslContextFactory(contextFactory, nifiProperties);
        Mockito.verify(contextFactory).setTrustStoreType(trustStoreType);
        Mockito.verify(contextFactory).setTrustStoreProvider(PROVIDER_NAME);
    }
}

