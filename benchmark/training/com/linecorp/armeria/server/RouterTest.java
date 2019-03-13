/**
 * Copyright 2017 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.server;


import HttpMethod.GET;
import HttpMethod.POST;
import MediaType.JSON_UTF_8;
import MediaType.PLAIN_TEXT_UTF_8;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RouterTest {
    private static final Logger logger = LoggerFactory.getLogger(RouterTest.class);

    private static final BiConsumer<PathMapping, PathMapping> REJECT = ( a, b) -> {
        throw new IllegalStateException(((("duplicate path mapping: " + a) + "vs. ") + b));
    };

    @Test
    public void testRouters() {
        final List<PathMapping> mappings = // router 1
        // router 2
        // router 3
        // router 4
        // router 5
        Lists.newArrayList(PathMapping.of("exact:/a"), PathMapping.of("/b/{var}"), PathMapping.of("prefix:/c"), PathMapping.of("regex:/d([^/]+)"), PathMapping.of("glob:/e/**/z"), PathMapping.of("exact:/f"), PathMapping.of("/g/{var}"), PathMapping.of("glob:/h/**/z"), PathMapping.of("prefix:/i"));
        final List<Router<PathMapping>> routers = Routers.routers(mappings, Function.identity(), RouterTest.REJECT);
        assertThat(routers.size()).isEqualTo(5);
        // Map of a path string and a router index
        final List<Map.Entry<String, Integer>> args = Lists.newArrayList(Maps.immutableEntry("/a", 0), Maps.immutableEntry("/b/1", 0), Maps.immutableEntry("/c/1", 0), Maps.immutableEntry("/dxxx/", 1), Maps.immutableEntry("/e/1/2/3/z", 1), Maps.immutableEntry("/f", 2), Maps.immutableEntry("/g/1", 2), Maps.immutableEntry("/h/1/2/3/z", 3), Maps.immutableEntry("/i/1/2/3", 4));
        final PathMappingContext mappingCtx = Mockito.mock(PathMappingContext.class);
        args.forEach(( entry) -> {
            RouterTest.logger.debug("Entry: path {} router {}", entry.getKey(), entry.getValue());
            for (int i = 0; i < 5; i++) {
                Mockito.when(mappingCtx.path()).thenReturn(entry.getKey());
                final PathMapped<PathMapping> result = routers.get(i).find(mappingCtx);
                assertThat(result.isPresent()).isEqualTo((i == (entry.getValue())));
            }
        });
    }

    @Test
    public void duplicateMappings() {
        // Simple cases
        RouterTest.testDuplicateMappings(PathMapping.of("exact:/a"), PathMapping.of("exact:/a"));
        RouterTest.testDuplicateMappings(PathMapping.of("exact:/a"), PathMapping.of("/a"));
        RouterTest.testDuplicateMappings(PathMapping.of("prefix:/"), PathMapping.ofCatchAll());
    }

    /**
     * Should detect the duplicates even if the mappings are split into more than one router.
     */
    @Test
    public void duplicateMappingsWithRegex() {
        // Ensure that 3 routers are created first really.
        assertThat(Routers.routers(ImmutableList.of(PathMapping.of("/foo/:bar"), PathMapping.ofRegex("not-trie-compatible"), PathMapping.of("/bar/:baz")), Function.identity(), RouterTest.REJECT)).hasSize(3);
        RouterTest.testDuplicateMappings(PathMapping.of("/foo/:bar"), PathMapping.ofRegex("not-trie-compatible"), PathMapping.of("/foo/:qux"));
    }

    @Test
    public void duplicateMappingsWithHeaders() {
        // Not a duplicate if complexity is different.
        RouterTest.testNonDuplicateMappings(PathMapping.of("/foo"), new HttpHeaderPathMapping(PathMapping.of("/foo"), ImmutableSet.of(GET), ImmutableList.of(), ImmutableList.of()));
        // Duplicate if supported methods overlap.
        RouterTest.testNonDuplicateMappings(new HttpHeaderPathMapping(PathMapping.of("/foo"), ImmutableSet.of(GET), ImmutableList.of(), ImmutableList.of()), new HttpHeaderPathMapping(PathMapping.of("/foo"), ImmutableSet.of(POST), ImmutableList.of(), ImmutableList.of()));
        RouterTest.testDuplicateMappings(new HttpHeaderPathMapping(PathMapping.of("/foo"), ImmutableSet.of(GET), ImmutableList.of(), ImmutableList.of()), new HttpHeaderPathMapping(PathMapping.of("/foo"), ImmutableSet.of(GET, POST), ImmutableList.of(), ImmutableList.of()));
        // Duplicate if consume types overlap.
        RouterTest.testNonDuplicateMappings(new HttpHeaderPathMapping(PathMapping.of("/foo"), ImmutableSet.of(POST), ImmutableList.of(PLAIN_TEXT_UTF_8), ImmutableList.of()), new HttpHeaderPathMapping(PathMapping.of("/foo"), ImmutableSet.of(POST), ImmutableList.of(JSON_UTF_8), ImmutableList.of()));
        RouterTest.testDuplicateMappings(new HttpHeaderPathMapping(PathMapping.of("/foo"), ImmutableSet.of(POST), ImmutableList.of(PLAIN_TEXT_UTF_8, JSON_UTF_8), ImmutableList.of()), new HttpHeaderPathMapping(PathMapping.of("/foo"), ImmutableSet.of(POST), ImmutableList.of(JSON_UTF_8), ImmutableList.of()));
        // Duplicate if produce types overlap.
        RouterTest.testNonDuplicateMappings(new HttpHeaderPathMapping(PathMapping.of("/foo"), ImmutableSet.of(POST), ImmutableList.of(), ImmutableList.of(PLAIN_TEXT_UTF_8)), new HttpHeaderPathMapping(PathMapping.of("/foo"), ImmutableSet.of(POST), ImmutableList.of(), ImmutableList.of(JSON_UTF_8)));
        RouterTest.testDuplicateMappings(new HttpHeaderPathMapping(PathMapping.of("/foo"), ImmutableSet.of(POST), ImmutableList.of(), ImmutableList.of(PLAIN_TEXT_UTF_8, JSON_UTF_8)), new HttpHeaderPathMapping(PathMapping.of("/foo"), ImmutableSet.of(POST), ImmutableList.of(), ImmutableList.of(PLAIN_TEXT_UTF_8)));
    }
}

