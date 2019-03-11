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
package io.helidon.config.internal;


import Config.Key;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link OverrideConfigFilter}.
 */
public class OverrideConfigFilterTest {
    private static final LinkedHashMap<String, String> ORDERED_MAP = new LinkedHashMap<String, String>() {
        {
            put("*", "libor");
        }
    };

    @Test
    public void testCreateFilterWithNullParam() {
        OverrideConfigFilter filter = new OverrideConfigFilter(() -> null);
        MatcherAssert.assertThat(filter, Matchers.notNullValue());
        MatcherAssert.assertThat(filter.apply(Key.create("name"), "ondrej"), Is.is("ondrej"));
    }

    @Test
    public void testCreateFilterWithEmptyParam() {
        OverrideConfigFilter filter = new OverrideConfigFilter(CollectionsHelper::listOf);
        MatcherAssert.assertThat(filter, Matchers.notNullValue());
        MatcherAssert.assertThat(filter.apply(Key.create("name"), "ondrej"), Is.is("ondrej"));
    }

    @Test
    public void testCreateFilterWithParam() {
        OverrideConfigFilter filter = new OverrideConfigFilter(() -> OverrideSource.OverrideData.createFromWildcards(ORDERED_MAP.entrySet().stream().map(( e) -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue())).collect(Collectors.toList())).data());
        MatcherAssert.assertThat(filter, Matchers.notNullValue());
        MatcherAssert.assertThat(filter.apply(Key.create("name"), "ondrej"), Is.is("libor"));
    }

    @Test
    public void testWithRegexizeFunction() {
        OverrideConfigFilter filter = new OverrideConfigFilter(() -> OverrideSource.OverrideData.createFromWildcards(ORDERED_MAP.entrySet().stream().map(( e) -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue())).collect(Collectors.toList())).data());
        MatcherAssert.assertThat(filter, Matchers.notNullValue());
        MatcherAssert.assertThat(filter.apply(Key.create("name"), "ondrej"), Is.is("libor"));
    }
}

