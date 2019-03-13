/**
 * Copyright 2016 LINE Corporation
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
package com.linecorp.armeria.common.logback;


import com.google.common.collect.ImmutableMap;
import org.junit.Test;


public class UnionMapTest {
    @Test
    public void testSize() {
        assertThat(new UnionMap(ImmutableMap.of("1", "a"), ImmutableMap.of("2", "b"))).hasSize(2);
        assertThat(new UnionMap(ImmutableMap.of("1", "a", "2", "b"), ImmutableMap.of("1", "b"))).hasSize(2);
        assertThat(new UnionMap(ImmutableMap.of("1", "a"), ImmutableMap.of("1", "b", "2", "b"))).hasSize(2);
    }

    @Test
    public void testIsEmpty() {
        assertThat(new UnionMap<String, String>(ImmutableMap.of(), ImmutableMap.of())).isEmpty();
        assertThat(new UnionMap(ImmutableMap.of(), ImmutableMap.of("1", "a"))).isNotEmpty();
        assertThat(new UnionMap(ImmutableMap.of("1", "a"), ImmutableMap.of())).isNotEmpty();
    }

    @Test
    public void testContainsKey() {
        assertThat(new UnionMap(ImmutableMap.of("1", "a"), ImmutableMap.of("2", "b"))).containsKey("1");
        assertThat(new UnionMap(ImmutableMap.of("1", "a"), ImmutableMap.of("2", "b"))).containsKey("2");
    }

    @Test
    public void testContainsValue() {
        assertThat(new UnionMap(ImmutableMap.of("1", "a"), ImmutableMap.of("2", "b"))).containsValue("a");
        assertThat(new UnionMap(ImmutableMap.of("1", "a"), ImmutableMap.of("2", "b"))).containsValue("b");
    }

    @Test
    public void testGet() {
        assertThat(new UnionMap(ImmutableMap.of("1", "a"), ImmutableMap.of("2", "b"))).containsEntry("1", "a");
        assertThat(new UnionMap(ImmutableMap.of("1", "a"), ImmutableMap.of("2", "b"))).containsEntry("2", "b");
        assertThat(new UnionMap(ImmutableMap.of("1", "a"), ImmutableMap.of("1", "b"))).containsEntry("1", "a");
    }

    @Test
    public void testImmutability() {
        final UnionMap<String, String> map = new UnionMap(ImmutableMap.of("1", "a"), ImmutableMap.of("2", "b"));
        assertThatThrownBy(() -> map.put("foo", "bar")).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> map.remove("foo")).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(map::clear).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> {
            final Iterator<Entry<String, String>> i = map.entrySet().iterator();
            i.next();
            i.remove();
        }).isInstanceOf(UnsupportedOperationException.class);
    }
}

