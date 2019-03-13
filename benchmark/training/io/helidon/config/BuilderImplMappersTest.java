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
package io.helidon.config;


import io.helidon.common.CollectionsHelper;
import io.helidon.config.ConfigMapperManager.MapperProviders;
import java.util.Map;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * Tests part of {@link BuilderImpl} related to mapping functions or {@link io.helidon.config.spi.ConfigMapperProvider}.
 */
public class BuilderImplMappersTest {
    @Test
    public void testUserDefinedHasPrecedenceInteger() {
        MapperProviders providers = MapperProviders.create();
        providers.add(() -> CollectionsHelper.mapOf(.class, ( config) -> 42));
        ConfigMapperManager manager = BuilderImpl.buildMappers(false, providers);
        Config config = Config.builder().sources(ConfigSources.create(CollectionsHelper.mapOf("int-p", "2147483647"))).build();
        MatcherAssert.assertThat(manager.map(config.get("int-p"), Integer.class), Matchers.is(42));
        MatcherAssert.assertThat(manager.map(config.get("int-p"), OptionalInt.class).getAsInt(), Matchers.is(2147483647));
    }

    @Test
    public void testUserDefinedHasPrecedenceOptionalInt() {
        MapperProviders providers = MapperProviders.create();
        providers.add(() -> CollectionsHelper.mapOf(.class, ( config) -> OptionalInt.of(42)));
        ConfigMapperManager manager = BuilderImpl.buildMappers(false, providers);
        Config config = Config.builder().sources(ConfigSources.create(CollectionsHelper.mapOf("int-p", "2147483647"))).build();
        MatcherAssert.assertThat(manager.map(config.get("int-p"), Integer.class), Matchers.is(2147483647));
        MatcherAssert.assertThat(manager.map(config.get("int-p"), OptionalInt.class).getAsInt(), Matchers.is(42));
    }

    @Test
    public void testUserDefinedMapperProviderHasPrecedenceInteger() {
        Config config = Config.builder().sources(ConfigSources.create(CollectionsHelper.mapOf("int-p", "2147483647"))).addMapper(() -> mapOf(.class, ( c) -> 43)).build();
        MatcherAssert.assertThat(config.get("int-p").asInt().get(), Matchers.is(43));
    }

    @Test
    public void testUserOverrideMapperFromMapperProvider() {
        Config config = Config.builder().sources(ConfigSources.create(CollectionsHelper.mapOf("int-p", "2147483647"))).addMapper(() -> mapOf(.class, ( c) -> 43)).addStringMapper(Integer.class, ((Function<String, Integer>) (( s) -> 44))).build();
        MatcherAssert.assertThat(config.get("int-p").asInt().get(), Matchers.is(44));
    }

    @Test
    public void testDefaultMapMapper() {
        Config config = Config.create(ConfigSources.create(CollectionsHelper.mapOf("int-p", "2147483647")));
        MatcherAssert.assertThat(config.asMap().get().get("int-p"), Matchers.is("2147483647"));
    }

    @Test
    public void testUserDefinedHasPrecedenceStringMapMapper() {
        Config config = Config.builder(ConfigSources.create(CollectionsHelper.mapOf("int-p", "2147483647"))).addMapper(Map.class, new BuilderImplMappersTest.CustomStringMapMapper()).build();
        MatcherAssert.assertThat(config.asMap().get().get("int-p"), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(config.asMap().get().get("prefix-int-p"), Matchers.is("[2147483647]"));
    }

    @Test
    public void testGenericTypeMapper() {
        Config config = Config.builder(ConfigSources.create(CollectionsHelper.mapOf("int-p", "2147483647"))).addMapper(new io.helidon.common.GenericType<java.util.Set<Integer>>() {}, ( config1) -> config1.asInt().map(CollectionsHelper::setOf).orElse(CollectionsHelper.setOf())).build();
        java.util.Set<Integer> integers = config.get("int-p").as(new io.helidon.common.GenericType<java.util.Set<Integer>>() {}).get();
        MatcherAssert.assertThat(integers, Matchers.contains(2147483647));
    }

    @Test
    public void testUserDefinedHasPrecedenceStringBuilderMapMapper() {
        Config config = Config.builder(ConfigSources.create(CollectionsHelper.mapOf("int-p", "2147483647"))).addMapper(Map.class, new BuilderImplMappersTest.CustomStringBuilderMapMapper()).build();
        MatcherAssert.assertThat(config.asMap().get().get("int-p"), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(config.asMap().get().get("prefix2-int-p"), Matchers.is("{2147483647}"));
    }

    private static class CustomStringMapMapper implements Function<Config, Map> {
        @Override
        public Map apply(Config config) throws ConfigMappingException, MissingValueException {
            return ConfigMappers.toMap(config).entrySet().stream().map(( entry) -> CollectionsHelper.mapEntry(("prefix-" + (entry.getKey())), (("[" + (entry.getValue())) + "]"))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
    }

    private static class CustomStringBuilderMapMapper implements Function<Config, Map> {
        @Override
        public Map apply(Config config) throws ConfigMappingException, MissingValueException {
            return ConfigMappers.toMap(config).entrySet().stream().map(( entry) -> CollectionsHelper.mapEntry(new StringBuilder(("prefix2-" + (entry.getKey()))), new StringBuilder((("{" + (entry.getValue())) + "}")))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
    }
}

