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


import Config.Key;
import Config.Type.OBJECT;
import ConfigNode.ObjectNode;
import io.helidon.config.spi.OverrideSource;
import java.util.LinkedHashMap;
import java.util.function.Supplier;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Tests the resolving of key token on config sources like:
 * <pre>
 *     region = region-eu
 *     $region.proxy = http://proxy-eu.company.com
 *
 * </pre>
 */
public class KeyTokenResolvingTest {
    @Test
    public void testResolveTokenConfig() {
        Config config = Config.builder().sources(ConfigSources.create(ObjectNode.builder().addValue("ad", "ad1").addValue("$region.$ad.url", "http://localhost:8080").addValue("region", "region-eu1").build())).disableSystemPropertiesSource().disableEnvironmentVariablesSource().build();
        MatcherAssert.assertThat(config.asMap().get().entrySet(), Matchers.hasSize(3));
        MatcherAssert.assertThat(config.get("ad").asString().get(), Matchers.is("ad1"));
        MatcherAssert.assertThat(config.get("region").asString().get(), Matchers.is("region-eu1"));
        MatcherAssert.assertThat(config.get("region-eu1.ad1.url").asString().get(), Matchers.is("http://localhost:8080"));
        MatcherAssert.assertThat(config.get("$region").exists(), Matchers.is(false));
        MatcherAssert.assertThat(config.get("$region.$ad").exists(), Matchers.is(false));
    }

    @Test
    public void testDisableResolveTokenConfig() {
        Config config = Config.builder().sources(ConfigSources.create(ObjectNode.builder().addValue("ad", "ad1").addValue("$region.$ad.url", "http://localhost:8080").addValue("region", "region-eu1").build())).disableKeyResolving().disableSystemPropertiesSource().disableEnvironmentVariablesSource().build();
        MatcherAssert.assertThat(config.asMap().get().entrySet(), Matchers.hasSize(3));
        MatcherAssert.assertThat(config.get("ad").asString().get(), Matchers.is("ad1"));
        MatcherAssert.assertThat(config.get("region").asString().get(), Matchers.is("region-eu1"));
        MatcherAssert.assertThat(config.get("$region").exists(), Matchers.is(true));
        MatcherAssert.assertThat(config.get("$region.$ad").exists(), Matchers.is(true));
        MatcherAssert.assertThat(config.get("$region.$ad.url").asString().get(), Matchers.is("http://localhost:8080"));
    }

    @Test
    public void testResolveTokenConfig2() {
        Config config = Config.builder().sources(ConfigSources.create(ObjectNode.builder().addValue("env.ad", "ad1").addValue("env.region", "region-eu1").addValue("${env.region}.${env.ad}.url", "http://localhost:8080").build())).disableSystemPropertiesSource().disableEnvironmentVariablesSource().build();
        MatcherAssert.assertThat(config.asMap().get().entrySet(), Matchers.hasSize(3));
        MatcherAssert.assertThat(config.get("env.ad").asString().get(), Matchers.is("ad1"));
        MatcherAssert.assertThat(config.get("env.region").asString().get(), Matchers.is("region-eu1"));
        MatcherAssert.assertThat(config.get("region-eu1.ad1.url").asString().get(), Matchers.is("http://localhost:8080"));
        MatcherAssert.assertThat(config.get("$region").exists(), Matchers.is(false));
        MatcherAssert.assertThat(config.get("$region.$ad").exists(), Matchers.is(false));
    }

    @Test
    public void testResolveTokenConfig3() {
        Config config = Config.builder().sources(ConfigSources.create(ObjectNode.builder().addValue("env.ad", "ad1").addValue("env.region", "region-eu1").addObject("${env.region}", ObjectNode.builder().addObject("${env.ad}", ObjectNode.builder().addValue("url", "http://localhost:8080").build()).build()).build())).disableSystemPropertiesSource().disableEnvironmentVariablesSource().build();
        MatcherAssert.assertThat(config.asMap().get().entrySet(), Matchers.hasSize(3));
        MatcherAssert.assertThat(config.get("env.ad").asString().get(), Matchers.is("ad1"));
        MatcherAssert.assertThat(config.get("env.region").asString().get(), Matchers.is("region-eu1"));
        MatcherAssert.assertThat(config.get("region-eu1.ad1.url").asString().get(), Matchers.is("http://localhost:8080"));
        MatcherAssert.assertThat(config.get("$region").exists(), Matchers.is(false));
        MatcherAssert.assertThat(config.get("$region.$ad").exists(), Matchers.is(false));
    }

    @Test
    public void testResolveTokenConfig4() {
        Supplier<OverrideSource> overrideSource = OverrideSources.create(new LinkedHashMap<String, String>() {
            {
                put("prod.inventory.logging.level", "WARN");
                put("test.*.logging.level", "FINE");
                put("*.*.logging.level", "ERROR");
            }
        });
        // configuration with a source that declares the environment is 'test'
        Config testConfig = Config.builder().sources(ConfigSources.create(// the only difference in config source
        ObjectNode.builder().addValue("env", "test").addValue("component", "inventory").addValue("$env.$component.logging.level", "INFO").build())).overrides(overrideSource).disableSystemPropertiesSource().disableEnvironmentVariablesSource().build();
        // configuration with a source that declares the environment is 'prod'
        Config prodConfig = Config.builder().sources(ConfigSources.create(// the only difference in config source
        ObjectNode.builder().addValue("env", "prod").addValue("component", "inventory").addValue("$env.$component.logging.level", "INFO").build())).overrides(overrideSource).disableSystemPropertiesSource().disableEnvironmentVariablesSource().build();
        MatcherAssert.assertThat(testConfig.asMap().get().entrySet(), Matchers.hasSize(3));
        MatcherAssert.assertThat(testConfig.get("test.inventory.logging.level").asString().get(), Matchers.is("FINE"));
        MatcherAssert.assertThat(prodConfig.get("prod.inventory.logging.level").asString().get(), Matchers.is("WARN"));
    }

    @Test
    public void testResolveChainedTokensConfig() {
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            Config config = Config.builder().sources(ConfigSources.create(ObjectNode.builder().addValue("region", "").addValue("$region", "missing").build())).disableSystemPropertiesSource().disableEnvironmentVariablesSource().build();
            config.traverse().forEach(System.out::println);
        });
        MatcherAssert.assertThat(ex.getMessage(), CoreMatchers.startsWith("Missing value in token 'region' definition"));
    }

    @Test
    public void testResolveTokenMissingValue() {
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            Config.builder().sources(ConfigSources.create(ObjectNode.builder().addValue("$region.$ad.url", "http://localhost:8080").addValue("region", "region-eu1").build())).disableSystemPropertiesSource().disableEnvironmentVariablesSource().build();
        });
        MatcherAssert.assertThat(ex.getMessage(), CoreMatchers.startsWith("Missing token 'ad' to resolve"));
    }

    @Test
    public void testResolveTokenReferenceToReference() {
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            Config config = Config.builder().sources(ConfigSources.create(ObjectNode.builder().addValue("env.region", "eu").addValue("$region.url", "http://localhost:8080").addValue("region", "${env.region}").build())).disableSystemPropertiesSource().disableEnvironmentVariablesSource().build();
            config.traverse().forEach(System.out::println);
        });
        MatcherAssert.assertThat(ex.getMessage(), CoreMatchers.startsWith("Key token 'region' references to a reference in value. A recursive references is not allowed"));
    }

    @Test
    public void testResolveTokenWithDottedValue() {
        Config config = Config.builder().sources(ConfigSources.create(ObjectNode.builder().addValue("domain", "oracle.com").addValue("$domain.sso", "on").addValue(((Key.escapeName("seznam.cz")) + ".sso"), "off").build())).disableSystemPropertiesSource().disableEnvironmentVariablesSource().build();
        config.traverse().forEach(System.out::println);
        MatcherAssert.assertThat(config.get("oracle").exists(), Matchers.is(false));
        MatcherAssert.assertThat(config.get("oracle~1com").exists(), Matchers.is(true));
        MatcherAssert.assertThat(config.get("oracle~1com").type(), Matchers.is(OBJECT));
        MatcherAssert.assertThat(config.get("oracle~1com.sso").asString().get(), Matchers.is("on"));
        MatcherAssert.assertThat(config.get("seznam").exists(), Matchers.is(false));
        MatcherAssert.assertThat(config.get("seznam~1cz").exists(), Matchers.is(true));
        MatcherAssert.assertThat(config.get("seznam~1cz").type(), Matchers.is(OBJECT));
        MatcherAssert.assertThat(config.get("seznam~1cz.sso").asString().get(), Matchers.is("off"));
    }
}

