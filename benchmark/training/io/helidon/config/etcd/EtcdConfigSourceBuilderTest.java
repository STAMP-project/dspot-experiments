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
import EtcdApi.v3;
import io.helidon.common.CollectionsHelper;
import io.helidon.common.reactive.Flow;
import io.helidon.config.Config;
import io.helidon.config.ConfigParsers;
import io.helidon.config.ConfigSources;
import io.helidon.config.MissingValueException;
import io.helidon.config.etcd.EtcdConfigSourceBuilder.EtcdEndpoint;
import io.helidon.config.spi.ConfigNode.ObjectNode;
import io.helidon.config.spi.ConfigSource;
import io.helidon.config.spi.PollingStrategy;
import java.net.URI;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Tests {@link EtcdConfigSourceBuilder}.
 */
public class EtcdConfigSourceBuilderTest {
    @Test
    public void testBuilderSuccessful() {
        EtcdConfigSource etcdConfigSource = EtcdConfigSourceBuilder.create(URI.create("http://localhost:2379"), "/registry", v2).mediaType("my/media/type").build();
        MatcherAssert.assertThat(etcdConfigSource, Matchers.notNullValue());
    }

    @Test
    public void testBuilderWithoutUri() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            EtcdConfigSourceBuilder.create(null, "/registry", v2).mediaType("my/media/type").parser(ConfigParsers.properties()).build();
        });
    }

    @Test
    public void testBuilderWithoutKey() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            EtcdConfigSourceBuilder.create(URI.create("http://localhost:2379"), null, v2).mediaType("my/media/type").parser(ConfigParsers.properties()).build();
        });
    }

    @Test
    public void testBuilderWithoutVersion() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            EtcdConfigSourceBuilder.create(URI.create("http://localhost:2379"), "/registry", null).mediaType("my/media/type").parser(ConfigParsers.properties()).build();
        });
    }

    @Test
    public void testEtcdConfigSourceDescription() {
        MatcherAssert.assertThat(EtcdConfigSourceBuilder.create(URI.create("http://localhost:2379"), "/registry", v2).mediaType("my/media/type").parser(ConfigParsers.properties()).build().description(), CoreMatchers.is("EtcdConfig[http://localhost:2379#/registry]"));
    }

    @Test
    public void testPollingStrategy() {
        URI uri = URI.create("http://localhost:2379");
        EtcdConfigSourceBuilder builder = EtcdConfigSourceBuilder.create(uri, "/registry", v2).pollingStrategy(EtcdConfigSourceBuilderTest.TestingEtcdEndpointPollingStrategy::new);
        MatcherAssert.assertThat(builder.pollingStrategyInternal(), CoreMatchers.is(Matchers.instanceOf(EtcdConfigSourceBuilderTest.TestingEtcdEndpointPollingStrategy.class)));
        EtcdEndpoint strategyEndpoint = ((EtcdConfigSourceBuilderTest.TestingEtcdEndpointPollingStrategy) (builder.pollingStrategyInternal())).etcdEndpoint();
        MatcherAssert.assertThat(strategyEndpoint.uri(), CoreMatchers.is(uri));
        MatcherAssert.assertThat(strategyEndpoint.key(), CoreMatchers.is("/registry"));
        MatcherAssert.assertThat(strategyEndpoint.api(), CoreMatchers.is(v2));
    }

    @Test
    public void testFromConfigNothing() {
        Assertions.assertThrows(MissingValueException.class, () -> {
            EtcdConfigSourceBuilder.create(Config.empty());
        });
    }

    @Test
    public void testFromConfigAll() {
        EtcdConfigSourceBuilder builder = EtcdConfigSourceBuilder.create(Config.create(ConfigSources.create(CollectionsHelper.mapOf("uri", "http://localhost:2379", "key", "/registry", "api", "v3"))));
        MatcherAssert.assertThat(builder.target().uri(), CoreMatchers.is(URI.create("http://localhost:2379")));
        MatcherAssert.assertThat(builder.target().key(), CoreMatchers.is("/registry"));
        MatcherAssert.assertThat(builder.target().api(), CoreMatchers.is(v3));
    }

    @Test
    public void testFromConfigWithCustomPollingStrategy() {
        EtcdConfigSourceBuilder builder = EtcdConfigSourceBuilder.create(Config.create(ConfigSources.create(CollectionsHelper.mapOf("uri", "http://localhost:2379", "key", "/registry", "api", "v3", "polling-strategy.class", EtcdConfigSourceBuilderTest.TestingEtcdEndpointPollingStrategy.class.getName()))));
        MatcherAssert.assertThat(builder.target().uri(), CoreMatchers.is(URI.create("http://localhost:2379")));
        MatcherAssert.assertThat(builder.target().key(), CoreMatchers.is("/registry"));
        MatcherAssert.assertThat(builder.target().api(), CoreMatchers.is(v3));
        MatcherAssert.assertThat(builder.pollingStrategyInternal(), CoreMatchers.is(Matchers.instanceOf(EtcdConfigSourceBuilderTest.TestingEtcdEndpointPollingStrategy.class)));
        EtcdEndpoint strategyEndpoint = ((EtcdConfigSourceBuilderTest.TestingEtcdEndpointPollingStrategy) (builder.pollingStrategyInternal())).etcdEndpoint();
        MatcherAssert.assertThat(strategyEndpoint.uri(), CoreMatchers.is(URI.create("http://localhost:2379")));
        MatcherAssert.assertThat(strategyEndpoint.key(), CoreMatchers.is("/registry"));
        MatcherAssert.assertThat(strategyEndpoint.api(), CoreMatchers.is(v3));
    }

    @Test
    public void testFromConfigEtcdWatchPollingStrategy() {
        EtcdConfigSourceBuilder builder = EtcdConfigSourceBuilder.create(Config.create(ConfigSources.create(CollectionsHelper.mapOf("uri", "http://localhost:2379", "key", "/registry", "api", "v3", "polling-strategy.class", EtcdWatchPollingStrategy.class.getName()))));
        MatcherAssert.assertThat(builder.target().uri(), CoreMatchers.is(URI.create("http://localhost:2379")));
        MatcherAssert.assertThat(builder.target().key(), CoreMatchers.is("/registry"));
        MatcherAssert.assertThat(builder.target().api(), CoreMatchers.is(v3));
        MatcherAssert.assertThat(builder.pollingStrategyInternal(), CoreMatchers.is(Matchers.instanceOf(EtcdWatchPollingStrategy.class)));
        EtcdEndpoint strategyEndpoint = etcdEndpoint();
        MatcherAssert.assertThat(strategyEndpoint.uri(), CoreMatchers.is(URI.create("http://localhost:2379")));
        MatcherAssert.assertThat(strategyEndpoint.key(), CoreMatchers.is("/registry"));
        MatcherAssert.assertThat(strategyEndpoint.api(), CoreMatchers.is(v3));
    }

    @Test
    public void testSourceFromConfigByClass() {
        Config metaConfig = Config.create(ConfigSources.create(ObjectNode.builder().addValue("class", EtcdConfigSource.class.getName()).addObject("properties", ObjectNode.builder().addValue("uri", "http://localhost:2379").addValue("key", "/registry").addValue("api", "v3").build()).build()));
        ConfigSource source = metaConfig.as(ConfigSource::create).get();
        MatcherAssert.assertThat(source, CoreMatchers.is(Matchers.instanceOf(EtcdConfigSource.class)));
        EtcdConfigSource etcdSource = ((EtcdConfigSource) (source));
        MatcherAssert.assertThat(etcdSource.etcdEndpoint().uri(), CoreMatchers.is(URI.create("http://localhost:2379")));
        MatcherAssert.assertThat(etcdSource.etcdEndpoint().key(), CoreMatchers.is("/registry"));
        MatcherAssert.assertThat(etcdSource.etcdEndpoint().api(), CoreMatchers.is(v3));
    }

    @Test
    public void testSourceFromConfigByType() {
        Config metaConfig = Config.create(ConfigSources.create(ObjectNode.builder().addValue("type", "etcd").addObject("properties", ObjectNode.builder().addValue("uri", "http://localhost:2379").addValue("key", "/registry").addValue("api", "v3").build()).build()));
        ConfigSource source = metaConfig.as(ConfigSource::create).get();
        MatcherAssert.assertThat(source.get(), CoreMatchers.is(Matchers.instanceOf(EtcdConfigSource.class)));
        EtcdConfigSource etcdSource = ((EtcdConfigSource) (source));
        MatcherAssert.assertThat(etcdSource.etcdEndpoint().uri(), CoreMatchers.is(URI.create("http://localhost:2379")));
        MatcherAssert.assertThat(etcdSource.etcdEndpoint().key(), CoreMatchers.is("/registry"));
        MatcherAssert.assertThat(etcdSource.etcdEndpoint().api(), CoreMatchers.is(v3));
    }

    public static class TestingEtcdEndpointPollingStrategy implements PollingStrategy {
        private final EtcdEndpoint etcdEndpoint;

        public TestingEtcdEndpointPollingStrategy(EtcdEndpoint etcdEndpoint) {
            this.etcdEndpoint = etcdEndpoint;
            MatcherAssert.assertThat(etcdEndpoint, Matchers.notNullValue());
        }

        @Override
        public Flow.Publisher<PollingEvent> ticks() {
            return Flow.Subscriber::onComplete;
        }

        public EtcdEndpoint etcdEndpoint() {
            return etcdEndpoint;
        }
    }
}

