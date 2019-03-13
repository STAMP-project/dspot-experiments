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
package io.crate.protocols.http;


import CrateNettyHttpServerTransport.CrateHttpChannelHandler;
import HttpServerTransport.Dispatcher;
import SslConfigSettings.SSL_HTTP_ENABLED;
import SslConfigSettings.SSL_KEYSTORE_FILEPATH;
import SslConfigSettings.SSL_KEYSTORE_KEY_PASSWORD;
import SslConfigSettings.SSL_KEYSTORE_PASSWORD;
import SslConfigSettings.SSL_TRUSTSTORE_FILEPATH;
import SslConfigSettings.SSL_TRUSTSTORE_PASSWORD;
import io.crate.plugin.PipelineRegistry;
import io.crate.test.integration.CrateUnitTest;
import io.netty.channel.Channel;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.ssl.SslHandler;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import org.elasticsearch.common.network.InetAddresses;
import org.elasticsearch.common.network.NetworkService;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.util.BigArrays;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.threadpool.ThreadPool;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;


public class CrateHttpsTransportTest extends CrateUnitTest {
    private static File trustStoreFile;

    private static File keyStoreFile;

    @Test
    public void testPipelineConfiguration() throws Exception {
        Settings settings = Settings.builder().put(PATH_HOME_SETTING.getKey(), "/tmp").put(SSL_HTTP_ENABLED.getKey(), true).put(SSL_TRUSTSTORE_FILEPATH.getKey(), CrateHttpsTransportTest.trustStoreFile.getAbsolutePath()).put(SSL_TRUSTSTORE_PASSWORD.getKey(), "truststorePassword").put(SSL_KEYSTORE_FILEPATH.getKey(), CrateHttpsTransportTest.keyStoreFile.getAbsolutePath()).put(SSL_KEYSTORE_PASSWORD.getKey(), "keystorePassword").put(SSL_KEYSTORE_KEY_PASSWORD.getKey(), "serverKeyPassword").build();
        NetworkService networkService = new NetworkService(Collections.singletonList(new NetworkService.CustomNameResolver() {
            @Override
            public InetAddress[] resolveDefault() {
                return new InetAddress[]{ InetAddresses.forString("127.0.0.1") };
            }

            @Override
            public InetAddress[] resolveIfPossible(String value) throws IOException {
                return new InetAddress[]{ InetAddresses.forString("127.0.0.1") };
            }
        }));
        PipelineRegistry pipelineRegistry = new PipelineRegistry();
        new io.crate.protocols.ssl.SslContextProvider(settings, pipelineRegistry);
        CrateNettyHttpServerTransport transport = new CrateNettyHttpServerTransport(settings, networkService, BigArrays.NON_RECYCLING_INSTANCE, Mockito.mock(ThreadPool.class), NamedXContentRegistry.EMPTY, Mockito.mock(Dispatcher.class), pipelineRegistry);
        Channel channel = new EmbeddedChannel();
        try {
            transport.start();
            CrateNettyHttpServerTransport.CrateHttpChannelHandler httpChannelHandler = ((CrateNettyHttpServerTransport.CrateHttpChannelHandler) (transport.configureServerChannelHandler()));
            httpChannelHandler.initChannel(channel);
            assertThat(channel.pipeline().first(), Matchers.instanceOf(SslHandler.class));
        } finally {
            transport.stop();
            transport.close();
            channel.close().awaitUninterruptibly();
        }
    }
}

