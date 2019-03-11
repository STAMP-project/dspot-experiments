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
import io.helidon.config.internal.OverrideConfigFilter;
import java.util.AbstractMap;
import java.util.stream.Collectors;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Tests {@link InMemoryOverrideSource}.
 */
public class InMemoryOverrideSourceTest {
    @Test
    public void testWildcards() {
        Config config = Config.builder().sources(ConfigSources.create(CollectionsHelper.mapOf("aaa.bbb.name", "app-name", "aaa.bbb.url", "URL0", "aaa.anything", "1", "bbb", "ahoy"))).overrides(OverrideSources.create(CollectionsHelper.mapOf("*.*.url", "URL1"))).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        MatcherAssert.assertThat(config.get("aaa.bbb.name").asString(), Matchers.is(ConfigValues.simpleValue("app-name")));
        MatcherAssert.assertThat(config.get("aaa.bbb.url").asString().get(), Matchers.is("URL1"));
        MatcherAssert.assertThat(config.get("aaa.ccc.url").exists(), Matchers.is(false));
        MatcherAssert.assertThat(config.get("aaa.anything").asInt().get(), Matchers.is(1));
        MatcherAssert.assertThat(config.get("bbb").asString().get(), Matchers.is("ahoy"));
        MatcherAssert.assertThat(config.get("bbb.ccc.url").exists(), Matchers.is(false));
    }

    @Test
    public void testWildcards2() {
        Config config = Config.builder().sources(ConfigSources.create(CollectionsHelper.mapOf("aaa.bbb.name", "app-name", "aaa.bbb.url", "URL0", "aaa.anything", "1", "bbb", "ahoy"))).overrides(OverrideSources.create(CollectionsHelper.mapOf("*.url", "URL1"))).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        MatcherAssert.assertThat(config.get("aaa.bbb.name").asString(), Matchers.is(ConfigValues.simpleValue("app-name")));
        MatcherAssert.assertThat(config.get("aaa.bbb.url").asString(), Matchers.is(ConfigValues.simpleValue("URL0")));
        MatcherAssert.assertThat(config.get("aaa.ccc.url").exists(), Matchers.is(false));
        MatcherAssert.assertThat(config.get("aaa.anything").asInt(), Matchers.is(ConfigValues.simpleValue(1)));
        MatcherAssert.assertThat(config.get("bbb").asString(), Matchers.is(ConfigValues.simpleValue("ahoy")));
        MatcherAssert.assertThat(config.get("bbb.ccc.url").exists(), Matchers.is(false));
    }

    @Test
    public void testAsConfigFilter() {
        Config config = Config.builder().sources(ConfigSources.create(CollectionsHelper.mapOf("aaa.bbb.name", "app-name", "aaa.bbb.url", "URL0", "aaa.anything", "1", "bbb", "ahoy"))).addFilter(new OverrideConfigFilter(() -> OverrideSource.OverrideData.createFromWildcards(CollectionsHelper.mapOf("*.*.url", "URL1").entrySet().stream().map(( e) -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue())).collect(Collectors.toList())).data())).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        MatcherAssert.assertThat(config.get("aaa.bbb.name").asString(), Matchers.is(ConfigValues.simpleValue("app-name")));
        MatcherAssert.assertThat(config.get("aaa.bbb.url").asString(), Matchers.is(ConfigValues.simpleValue("URL1")));
        MatcherAssert.assertThat(config.get("aaa.ccc.url").exists(), Matchers.is(false));
        MatcherAssert.assertThat(config.get("aaa.anything").asInt(), Matchers.is(ConfigValues.simpleValue(1)));
        MatcherAssert.assertThat(config.get("bbb").asString(), Matchers.is(ConfigValues.simpleValue("ahoy")));
        MatcherAssert.assertThat(config.get("bbb.ccc.url").exists(), Matchers.is(false));
    }

    @Test
    public void testBuilderNullOverrideValues() {
        NullPointerException ex = Assertions.assertThrows(NullPointerException.class, () -> {
            new InMemoryOverrideSource.Builder(null);
        });
        MatcherAssert.assertThat(ex.getMessage(), CoreMatchers.startsWith("overrideValues cannot be null"));
    }
}

