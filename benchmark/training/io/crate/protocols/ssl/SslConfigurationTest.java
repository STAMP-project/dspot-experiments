/**
 * This file is part of a module with proprietary Enterprise Features.
 *
 * Licensed to Crate.io Inc. ("Crate.io") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 *
 * To use this file, Crate.io must have given you permission to enable and
 * use such Enterprise Features and you must have a valid Enterprise or
 * Subscription Agreement with Crate.io.  If you enable or use the Enterprise
 * Features, you represent and warrant that you have a valid Enterprise or
 * Subscription Agreement with Crate.io.  Your use of the Enterprise Features
 * if governed by the terms and conditions of your Enterprise or Subscription
 * Agreement with Crate.io.
 */
package io.crate.protocols.ssl;


import Settings.Builder;
import Settings.EMPTY;
import SslConfigSettings.SSL_KEYSTORE_FILEPATH_SETTING_NAME;
import SslConfigSettings.SSL_KEYSTORE_KEY_PASSWORD_SETTING_NAME;
import SslConfigSettings.SSL_KEYSTORE_PASSWORD_SETTING_NAME;
import SslConfigSettings.SSL_TRUSTSTORE_FILEPATH_SETTING_NAME;
import SslConfigSettings.SSL_TRUSTSTORE_PASSWORD_SETTING_NAME;
import io.crate.test.integration.CrateUnitTest;
import io.netty.handler.ssl.SslContext;
import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import org.elasticsearch.common.settings.Settings;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;


public class SslConfigurationTest extends CrateUnitTest {
    private static final String KEYSTORE_PASSWORD = "keystorePassword";

    private static final String KEYSTORE_KEY_PASSWORD = "serverKeyPassword";

    private static final String TRUSTSTORE_PASSWORD = "truststorePassword";

    private static final String ROOT_CA_ALIAS = "theCAroot";

    private static File trustStoreFile;

    private static File keyStoreFile;

    @Test
    public void testSslContextCreation() {
        Settings settings = Settings.builder().put(SSL_TRUSTSTORE_FILEPATH_SETTING_NAME, SslConfigurationTest.trustStoreFile.getAbsolutePath()).put(SSL_TRUSTSTORE_PASSWORD_SETTING_NAME, SslConfigurationTest.TRUSTSTORE_PASSWORD).put(SSL_KEYSTORE_FILEPATH_SETTING_NAME, SslConfigurationTest.keyStoreFile.getAbsolutePath()).put(SSL_KEYSTORE_PASSWORD_SETTING_NAME, SslConfigurationTest.KEYSTORE_PASSWORD).put(SSL_KEYSTORE_KEY_PASSWORD_SETTING_NAME, SslConfigurationTest.KEYSTORE_KEY_PASSWORD).build();
        SslContext sslContext = SslConfiguration.buildSslContext(settings);
        assertThat(sslContext.isServer(), Matchers.is(true));
        assertThat(sslContext.cipherSuites(), CoreMatchers.not(Matchers.empty()));
        // check that we don't offer NULL ciphers which do not encrypt
        assertThat(sslContext.cipherSuites(), CoreMatchers.not(CoreMatchers.hasItem(Matchers.containsString("NULL"))));
    }

    @Test
    public void testTrustStoreLoading() throws Exception {
        Settings.Builder settingsBuilder = Settings.builder();
        settingsBuilder.put(SSL_TRUSTSTORE_FILEPATH_SETTING_NAME, SslConfigurationTest.trustStoreFile.getAbsolutePath());
        settingsBuilder.put(SSL_TRUSTSTORE_PASSWORD_SETTING_NAME, SslConfigurationTest.TRUSTSTORE_PASSWORD);
        SslConfiguration.TrustStoreSettings trustStoreSettings = SslConfiguration.TrustStoreSettings.tryLoad(settingsBuilder.build()).orElse(null);
        assertThat(trustStoreSettings, Matchers.is(Matchers.notNullValue()));
        assertThat(trustStoreSettings.trustManagers.length, Matchers.is(1));
        assertThat(trustStoreSettings.keyStore.getType(), Matchers.is("jks"));
    }

    @Test
    public void testTrustStoreOptional() throws Exception {
        SslConfiguration.TrustStoreSettings trustStoreSettings = SslConfiguration.TrustStoreSettings.tryLoad(EMPTY).orElse(null);
        assertThat(trustStoreSettings, Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testTrustStoreLoadingFail() throws Exception {
        expectedException.expect(IOException.class);
        expectedException.expectMessage("Keystore was tampered with, or password was incorrect");
        Settings.Builder settingsBuilder = Settings.builder();
        settingsBuilder.put(SSL_TRUSTSTORE_FILEPATH_SETTING_NAME, SslConfigurationTest.trustStoreFile.getAbsolutePath());
        settingsBuilder.put(SSL_TRUSTSTORE_PASSWORD_SETTING_NAME, "wrongpassword");
        SslConfiguration.TrustStoreSettings.tryLoad(settingsBuilder.build());
    }

    @Test
    public void testKeyStoreLoading() throws Exception {
        Settings.Builder settingsBuilder = Settings.builder();
        settingsBuilder.put(SSL_KEYSTORE_FILEPATH_SETTING_NAME, SslConfigurationTest.keyStoreFile.getAbsolutePath());
        settingsBuilder.put(SSL_KEYSTORE_PASSWORD_SETTING_NAME, SslConfigurationTest.KEYSTORE_PASSWORD);
        settingsBuilder.put(SSL_KEYSTORE_KEY_PASSWORD_SETTING_NAME, SslConfigurationTest.KEYSTORE_KEY_PASSWORD);
        SslConfiguration.KeyStoreSettings keyStoreSettings = new SslConfiguration.KeyStoreSettings(settingsBuilder.build());
        assertThat(keyStoreSettings.keyManagers.length, Matchers.is(1));
        assertThat(keyStoreSettings.keyStore.getType(), Matchers.is("jks"));
        assertThat(keyStoreSettings.keyStore.getCertificate(SslConfigurationTest.ROOT_CA_ALIAS), Matchers.notNullValue());
    }

    @Test
    public void testKeyStoreLoadingFailWrongPassword() throws Exception {
        expectedException.expect(IOException.class);
        expectedException.expectMessage("Keystore was tampered with, or password was incorrect");
        Settings.Builder settingsBuilder = Settings.builder();
        settingsBuilder.put(SSL_KEYSTORE_FILEPATH_SETTING_NAME, SslConfigurationTest.keyStoreFile.getAbsolutePath());
        settingsBuilder.put(SSL_KEYSTORE_PASSWORD_SETTING_NAME, "wrongpassword");
        settingsBuilder.put(SSL_KEYSTORE_KEY_PASSWORD_SETTING_NAME, SslConfigurationTest.KEYSTORE_KEY_PASSWORD);
        new SslConfiguration.KeyStoreSettings(settingsBuilder.build());
    }

    @Test
    public void testKeyStoreLoadingFailWrongKeyPassword() throws Exception {
        expectedException.expect(UnrecoverableKeyException.class);
        expectedException.expectMessage("Cannot recover key");
        Settings.Builder settingsBuilder = Settings.builder();
        settingsBuilder.put(SSL_KEYSTORE_FILEPATH_SETTING_NAME, SslConfigurationTest.keyStoreFile.getAbsolutePath());
        settingsBuilder.put(SSL_KEYSTORE_PASSWORD_SETTING_NAME, SslConfigurationTest.KEYSTORE_PASSWORD);
        settingsBuilder.put(SSL_KEYSTORE_KEY_PASSWORD_SETTING_NAME, "wrongpassword");
        SslConfiguration.KeyStoreSettings ks = new SslConfiguration.KeyStoreSettings(settingsBuilder.build());
        assertThat(ks.exportDecryptedKey(), Matchers.is(Matchers.notNullValue()));
    }

    @Test
    public void testKeyStoreLoadingNoKeyPasswordFail() throws Exception {
        expectedException.expect(UnrecoverableKeyException.class);
        expectedException.expectMessage("Cannot recover key");
        Settings.Builder settingsBuilder = Settings.builder();
        settingsBuilder.put(SSL_KEYSTORE_FILEPATH_SETTING_NAME, SslConfigurationTest.keyStoreFile.getAbsolutePath());
        settingsBuilder.put(SSL_KEYSTORE_PASSWORD_SETTING_NAME, SslConfigurationTest.KEYSTORE_PASSWORD);
        settingsBuilder.put(SSL_KEYSTORE_KEY_PASSWORD_SETTING_NAME, "wrongpassword");
        SslConfiguration.KeyStoreSettings ks = new SslConfiguration.KeyStoreSettings(settingsBuilder.build());
        assertThat(ks.exportDecryptedKey(), Matchers.is(Matchers.notNullValue()));
    }

    @Test
    public void testExportRootCertificates() throws Exception {
        Settings.Builder settingsBuilder = Settings.builder();
        settingsBuilder.put(SSL_TRUSTSTORE_FILEPATH_SETTING_NAME, SslConfigurationTest.trustStoreFile.getAbsolutePath());
        settingsBuilder.put(SSL_TRUSTSTORE_PASSWORD_SETTING_NAME, SslConfigurationTest.TRUSTSTORE_PASSWORD);
        settingsBuilder.put(SSL_KEYSTORE_FILEPATH_SETTING_NAME, SslConfigurationTest.keyStoreFile.getAbsolutePath());
        settingsBuilder.put(SSL_KEYSTORE_PASSWORD_SETTING_NAME, SslConfigurationTest.KEYSTORE_PASSWORD);
        settingsBuilder.put(SSL_KEYSTORE_KEY_PASSWORD_SETTING_NAME, SslConfigurationTest.KEYSTORE_KEY_PASSWORD);
        SslConfiguration.KeyStoreSettings keyStoreSettings = new SslConfiguration.KeyStoreSettings(settingsBuilder.build());
        SslConfiguration.TrustStoreSettings trustStoreSettings = SslConfiguration.TrustStoreSettings.tryLoad(settingsBuilder.build()).orElse(null);
        assertThat(trustStoreSettings, Matchers.is(Matchers.notNullValue()));
        X509Certificate[] x509Certificates = keyStoreSettings.exportRootCertificates(trustStoreSettings.exportRootCertificates());
        assertEquals(2, x509Certificates.length);
        assertThat(x509Certificates[0].getIssuerDN().getName(), Matchers.containsString("CN=*.crate.io"));
        assertThat(x509Certificates[0].getNotAfter().getTime(), Matchers.is(4651463625000L));
        assertThat(x509Certificates[1].getIssuerDN().getName(), Matchers.containsString("CN=*.crate.io"));
        assertThat(x509Certificates[1].getNotAfter().getTime(), Matchers.is(4651463625000L));
    }

    @Test
    public void testExportServerCertChain() throws Exception {
        Settings.Builder settingsBuilder = Settings.builder();
        settingsBuilder.put(SSL_KEYSTORE_FILEPATH_SETTING_NAME, SslConfigurationTest.keyStoreFile.getAbsolutePath());
        settingsBuilder.put(SSL_KEYSTORE_PASSWORD_SETTING_NAME, SslConfigurationTest.KEYSTORE_PASSWORD);
        settingsBuilder.put(SSL_KEYSTORE_KEY_PASSWORD_SETTING_NAME, SslConfigurationTest.KEYSTORE_KEY_PASSWORD);
        SslConfiguration.KeyStoreSettings keyStoreSettings = new SslConfiguration.KeyStoreSettings(settingsBuilder.build());
        X509Certificate[] x509Certificates = keyStoreSettings.exportServerCertChain();
        assertThat(x509Certificates.length, Matchers.is(4));
        assertThat(x509Certificates[0].getIssuerDN().getName(), Matchers.containsString("CN=*.crate.io"));
        assertThat(x509Certificates[1].getIssuerDN().getName(), Matchers.containsString("CN=*.crate.io"));
        assertThat(x509Certificates[2].getIssuerDN().getName(), Matchers.containsString("CN=*.crate.io"));
        assertThat(x509Certificates[3].getIssuerDN().getName(), Matchers.containsString("CN=*.crate.io"));
        assertThat(x509Certificates[0].getSubjectDN().getName(), Matchers.containsString("CN=localhost"));
        assertThat(x509Certificates[0].getSubjectDN().getName(), Matchers.containsString("OU=Testing Department"));
        assertThat(x509Certificates[0].getSubjectDN().getName(), Matchers.containsString("L=San Francisco"));
        assertThat(x509Certificates[1].getSubjectDN().getName(), Matchers.containsString("CN=*.crate.io"));
        assertThat(x509Certificates[1].getSubjectDN().getName(), Matchers.containsString("OU=Cryptography Department"));
        assertThat(x509Certificates[1].getSubjectDN().getName(), Matchers.containsString("L=Dornbirn"));
        assertThat(x509Certificates[2].getSubjectDN().getName(), Matchers.containsString("CN=ssl.crate.io"));
        assertThat(x509Certificates[2].getSubjectDN().getName(), Matchers.containsString("OU=Cryptography Department"));
        assertThat(x509Certificates[2].getSubjectDN().getName(), Matchers.containsString("L=Berlin"));
        assertThat(x509Certificates[3].getSubjectDN().getName(), Matchers.containsString("CN=*.crate.io"));
        assertThat(x509Certificates[3].getSubjectDN().getName(), Matchers.containsString("OU=Cryptography Department"));
        assertThat(x509Certificates[3].getSubjectDN().getName(), Matchers.containsString("L=Dornbirn"));
    }

    @Test
    public void testExportDecryptedKey() throws Exception {
        Settings.Builder settingsBuilder = Settings.builder();
        settingsBuilder.put(SSL_KEYSTORE_FILEPATH_SETTING_NAME, SslConfigurationTest.keyStoreFile.getAbsolutePath());
        settingsBuilder.put(SSL_KEYSTORE_PASSWORD_SETTING_NAME, SslConfigurationTest.KEYSTORE_PASSWORD);
        settingsBuilder.put(SSL_KEYSTORE_KEY_PASSWORD_SETTING_NAME, SslConfigurationTest.KEYSTORE_KEY_PASSWORD);
        SslConfiguration.KeyStoreSettings keyStoreSettings = new SslConfiguration.KeyStoreSettings(settingsBuilder.build());
        PrivateKey privateKey = keyStoreSettings.exportDecryptedKey();
        assertNotNull(privateKey);
    }
}

