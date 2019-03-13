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


import SslConfigSettings.SSL_HTTP_ENABLED;
import SslConfigSettings.SSL_KEYSTORE_FILEPATH;
import SslConfigSettings.SSL_KEYSTORE_KEY_PASSWORD;
import SslConfigSettings.SSL_KEYSTORE_PASSWORD;
import SslConfigSettings.SSL_PSQL_ENABLED;
import SslConfigSettings.SSL_TRUSTSTORE_FILEPATH;
import SslConfigSettings.SSL_TRUSTSTORE_PASSWORD;
import io.crate.plugin.PipelineRegistry;
import io.crate.test.integration.CrateUnitTest;
import io.netty.handler.ssl.SslContext;
import java.io.File;
import org.elasticsearch.common.settings.Settings;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class SslContextProviderTest extends CrateUnitTest {
    private static File trustStoreFile;

    private static File keyStoreFile;

    @Test
    public void testClassLoadingWithInvalidConfiguration() {
        // empty ssl configuration which is invalid
        Settings settings = Settings.builder().put(SSL_HTTP_ENABLED.getKey(), true).put(SSL_PSQL_ENABLED.getKey(), true).build();
        PipelineRegistry pipelineRegistry = new PipelineRegistry();
        expectedException.expect(SslConfigurationException.class);
        expectedException.expectMessage("Failed to build SSL configuration");
        get();
    }

    @Test
    public void testClassLoadingWithValidConfiguration() {
        Settings settings = Settings.builder().put(SSL_HTTP_ENABLED.getKey(), true).put(SSL_PSQL_ENABLED.getKey(), true).put(SSL_TRUSTSTORE_FILEPATH.getKey(), SslContextProviderTest.trustStoreFile.getAbsolutePath()).put(SSL_TRUSTSTORE_PASSWORD.getKey(), "truststorePassword").put(SSL_KEYSTORE_FILEPATH.getKey(), SslContextProviderTest.keyStoreFile.getAbsolutePath()).put(SSL_KEYSTORE_PASSWORD.getKey(), "keystorePassword").put(SSL_KEYSTORE_KEY_PASSWORD.getKey(), "serverKeyPassword").build();
        PipelineRegistry pipelineRegistry = Mockito.mock(PipelineRegistry.class);
        SslContext sslContext = new SslContextProvider(settings, pipelineRegistry).get();
        assertThat(sslContext, Matchers.instanceOf(SslContext.class));
        Mockito.verify(pipelineRegistry).registerSslContextProvider(ArgumentMatchers.any());
    }
}

