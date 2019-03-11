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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link ConfigKeyImpl}.
 */
public class ConfigKeyImplTest {
    @Test
    public void testConfigKeyOf() {
        assertThatKey(((ConfigKeyImpl) (Key.create(""))), true, Matchers.nullValue(), "", "");
        assertThatKey(((ConfigKeyImpl) (Key.create("aaa"))), false, Matchers.not(Matchers.nullValue()), "aaa", "aaa");
        assertThatKey(((ConfigKeyImpl) (Key.create("aaa.bbb.ccc"))), false, Matchers.not(Matchers.nullValue()), "ccc", "aaa.bbb.ccc");
    }

    @Test
    public void testOfRoot() {
        assertThatKey(ConfigKeyImpl.of(), true, Matchers.nullValue(), "", "");
        assertThatKey(ConfigKeyImpl.of(""), true, Matchers.nullValue(), "", "");
        assertThatKey(ConfigKeyImpl.of().child(""), true, Matchers.nullValue(), "", "");
        assertThatKey(ConfigKeyImpl.of().child(ConfigKeyImpl.of()), true, Matchers.nullValue(), "", "");
    }

    @Test
    public void testOf() {
        assertThatKey(ConfigKeyImpl.of("aaa"), false, Matchers.not(Matchers.nullValue()), "aaa", "aaa");
        assertThatKey(ConfigKeyImpl.of("aaa.bbb"), false, Matchers.not(Matchers.nullValue()), "bbb", "aaa.bbb");
        assertThatKey(ConfigKeyImpl.of("aaa.bbb.ccc"), false, Matchers.not(Matchers.nullValue()), "ccc", "aaa.bbb.ccc");
    }

    @Test
    public void testChildLevel1() {
        assertThatKey(ConfigKeyImpl.of().child("aaa"), false, Matchers.not(Matchers.nullValue()), "aaa", "aaa");
        assertThatKey(ConfigKeyImpl.of().child(ConfigKeyImpl.of("aaa")), false, Matchers.not(Matchers.nullValue()), "aaa", "aaa");
    }

    @Test
    public void testChildLevel2() {
        assertThatKey(ConfigKeyImpl.of("aaa").child("bbb"), false, Matchers.not(Matchers.nullValue()), "bbb", "aaa.bbb");
        assertThatKey(ConfigKeyImpl.of("aaa").child(ConfigKeyImpl.of("bbb")), false, Matchers.not(Matchers.nullValue()), "bbb", "aaa.bbb");
        assertThatKey(ConfigKeyImpl.of().child("aaa.bbb"), false, Matchers.not(Matchers.nullValue()), "bbb", "aaa.bbb");
        assertThatKey(ConfigKeyImpl.of().child(ConfigKeyImpl.of("aaa.bbb")), false, Matchers.not(Matchers.nullValue()), "bbb", "aaa.bbb");
    }

    @Test
    public void testChildLevel3() {
        assertThatKey(ConfigKeyImpl.of().child("aaa").child("bbb").child("ccc"), false, Matchers.not(Matchers.nullValue()), "ccc", "aaa.bbb.ccc");
        assertThatKey(ConfigKeyImpl.of().child("aaa.bbb").child("ccc"), false, Matchers.not(Matchers.nullValue()), "ccc", "aaa.bbb.ccc");
        assertThatKey(ConfigKeyImpl.of().child("aaa").child("bbb.ccc"), false, Matchers.not(Matchers.nullValue()), "ccc", "aaa.bbb.ccc");
        assertThatKey(ConfigKeyImpl.of().child("aaa.bbb.ccc"), false, Matchers.not(Matchers.nullValue()), "ccc", "aaa.bbb.ccc");
    }

    @Test
    public void testEquals() {
        MatcherAssert.assertThat(ConfigKeyImpl.of(""), Matchers.is(ConfigKeyImpl.of()));
        MatcherAssert.assertThat(ConfigKeyImpl.of("aaa").child(ConfigKeyImpl.of()), Matchers.is(ConfigKeyImpl.of("aaa")));
        MatcherAssert.assertThat(ConfigKeyImpl.of("bbb"), Matchers.is(ConfigKeyImpl.of("bbb")));
        MatcherAssert.assertThat(ConfigKeyImpl.of("aaa").child(ConfigKeyImpl.of("bbb")), Matchers.is(ConfigKeyImpl.of("aaa.bbb")));
    }

    @Test
    public void testCompareTo() {
        MatcherAssert.assertThat(ConfigKeyImpl.of("").compareTo(ConfigKeyImpl.of()), Matchers.is(0));
        MatcherAssert.assertThat(ConfigKeyImpl.of("aaa").compareTo(ConfigKeyImpl.of("bbb")), Matchers.is(Matchers.lessThan(0)));
        MatcherAssert.assertThat(ConfigKeyImpl.of("bbb").compareTo(ConfigKeyImpl.of("aaa")), Matchers.is(Matchers.greaterThan(0)));
        MatcherAssert.assertThat(ConfigKeyImpl.of("aaa").compareTo(ConfigKeyImpl.of("aaa.bbb")), Matchers.is(Matchers.lessThan(0)));
        MatcherAssert.assertThat(ConfigKeyImpl.of("aaa.bbb").compareTo(ConfigKeyImpl.of("aaa")), Matchers.is(Matchers.greaterThan(0)));
    }
}

