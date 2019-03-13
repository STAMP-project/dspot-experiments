/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.config.etcd;


import EtcdApi.v2;
import io.helidon.config.Config;
import io.helidon.config.ConfigException;
import io.helidon.config.etcd.internal.client.EtcdClient;
import io.helidon.config.spi.ConfigParser;
import java.io.StringReader;
import java.net.URI;
import java.util.concurrent.atomic.AtomicLong;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;


/**
 * Tests {@link EtcdConfigSource} with {@link MockEtcdClient}.
 */
public class EtcdConfigSourceTest {
    static final String MEDIA_TYPE_APPLICATION_HOCON = "application/hocon";

    private static final URI DEFAULT_URI = URI.create("http://localhost:2379");

    private EtcdClient etcdClient;

    @Test
    public void testConfigSourceBuilder() {
        EtcdConfigSource etcdConfigSource = ((EtcdConfigSource) (EtcdConfigSourceBuilder.create(EtcdConfigSourceTest.DEFAULT_URI, "key", v2).mediaType(EtcdConfigSourceTest.MEDIA_TYPE_APPLICATION_HOCON).build()));
        MatcherAssert.assertThat(etcdConfigSource, CoreMatchers.notNullValue());
    }

    @Test
    public void testBadUri() {
        Assertions.assertThrows(ConfigException.class, () -> {
            EtcdConfigSource etcdConfigSource = ((EtcdConfigSource) (EtcdConfigSourceBuilder.create(URI.create("http://localhost:1111"), "configuration", v2).mediaType(EtcdConfigSourceTest.MEDIA_TYPE_APPLICATION_HOCON).build()));
            etcdConfigSource.content();
        });
    }

    @Test
    public void testBadKey() {
        Assertions.assertThrows(ConfigException.class, () -> {
            EtcdConfigSource etcdConfigSource = ((EtcdConfigSource) (EtcdConfigSourceBuilder.create(EtcdConfigSourceTest.DEFAULT_URI, "non-existing-key-23323423424234", v2).mediaType(EtcdConfigSourceTest.MEDIA_TYPE_APPLICATION_HOCON).build()));
            etcdConfigSource.content();
        });
    }

    @Test
    public void testConfig() {
        final AtomicLong revision = new AtomicLong(0);
        EtcdConfigSource configSource = ((EtcdConfigSource) (EtcdConfigSourceBuilder.create(EtcdConfigSourceTest.DEFAULT_URI, "configuration", v2).mediaType(EtcdConfigSourceTest.MEDIA_TYPE_APPLICATION_HOCON).build()));
        EtcdConfigSource mockedConfigSource = Mockito.spy(configSource);
        Mockito.when(mockedConfigSource.etcdClient()).thenReturn(etcdClient);
        Mockito.when(mockedConfigSource.content()).thenReturn(new ConfigParser.Content<Long>() {
            @Override
            public String mediaType() {
                return EtcdConfigSourceTest.MEDIA_TYPE_APPLICATION_HOCON;
            }

            @Override
            public Readable asReadable() {
                try {
                    return new StringReader(etcdClient.get("configuration"));
                } catch ( e) {
                    fail(e);
                    return null;
                }
            }

            @Override
            public Optional<Long> stamp() {
                return java.util.Optional.of(revision.getAndIncrement());
            }
        });
        Config config = Config.builder().sources(mockedConfigSource).build();
        MatcherAssert.assertThat(config.get("security").asNodeList().get(), Matchers.hasSize(1));
    }
}

