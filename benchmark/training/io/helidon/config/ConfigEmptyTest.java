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


import Config.Type.OBJECT;
import io.helidon.common.CollectionsHelper;
import java.util.List;
import java.util.function.Supplier;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Tests {@link Config#empty()} implementation.
 */
public class ConfigEmptyTest {
    @Test
    public void testKey() {
        MatcherAssert.assertThat(Config.empty().get("one.two").key().toString(), Is.is("one.two"));
    }

    @Test
    public void testChildren() {
        MatcherAssert.assertThat(Config.empty().asNodeList().get().size(), Is.is(0));
    }

    @Test
    public void testTraverse() {
        MatcherAssert.assertThat(Config.empty().traverse().count(), Is.is(0L));
    }

    @Test
    public void testAsString() {
        Assertions.assertThrows(MissingValueException.class, () -> {
            // separate lines to see the line that fails
            Config empty = Config.empty();
            empty.asString().get();
        });
    }

    @Test
    public void testAsStringDefault() {
        MatcherAssert.assertThat(Config.empty().asString().orElse("default"), Is.is("default"));
    }

    @Test
    public void testAsInt() {
        Assertions.assertThrows(MissingValueException.class, () -> {
            Config.empty().asInt().get();
        });
    }

    @Test
    public void testAsIntDefault() {
        MatcherAssert.assertThat(Config.empty().asInt().orElse(5), Is.is(5));
    }

    @Test
    public void testAsStringList() {
        MatcherAssert.assertThat(Config.empty().asList(String.class).get(), Is.is(Matchers.empty()));
    }

    @Test
    public void testAsStringListDefault() {
        List<String> list = CollectionsHelper.listOf("record");
        MatcherAssert.assertThat(Config.empty().asList(String.class).orElse(list), Is.is(Matchers.empty()));
    }

    @Test
    public void testAsIntList() {
        MatcherAssert.assertThat(Config.empty().asList(Integer.class).get(), Is.is(Matchers.empty()));
    }

    @Test
    public void testAsIntListDefault() {
        List<Integer> list = CollectionsHelper.listOf(5);
        MatcherAssert.assertThat(Config.empty().asList(Integer.class).orElse(list), Is.is(Matchers.empty()));
    }

    @Test
    public void testType() {
        MatcherAssert.assertThat(Config.empty().type(), Is.is(OBJECT));
    }

    @Test
    public void testMap() {
        Assertions.assertThrows(MissingValueException.class, () -> {
            Config.empty().asString().as(ConfigMappers::toBigInteger).get();
        });
    }

    @Test
    public void testKeyViaSupplier() {
        MatcherAssert.assertThat(Config.empty().asNode().optionalSupplier().get().get().get("one.two").key().toString(), Is.is("one.two"));
    }

    @Test
    public void testChildrenSupplier() {
        MatcherAssert.assertThat(Config.empty().asNodeList().supplier().get().size(), Is.is(0));
    }

    @Test
    public void testTraverseSupplier() {
        MatcherAssert.assertThat(Config.empty().asNode().optionalSupplier().get().get().traverse().count(), Is.is(0L));
    }

    @Test
    public void testAsStringSupplier() {
        Assertions.assertThrows(MissingValueException.class, () -> {
            // separate lines to see which statement fails
            Config empty = Config.empty();
            Supplier<String> supp = empty.asString().supplier();
            supp.get();
        });
    }

    @Test
    public void testAsStringDefaultSupplier() {
        MatcherAssert.assertThat(Config.empty().asString().supplier("default").get(), Is.is("default"));
    }

    @Test
    public void testAsIntSupplier() {
        Assertions.assertThrows(MissingValueException.class, () -> {
            Config.empty().asInt().supplier().get();
        });
    }

    @Test
    public void testAsIntDefaultSupplier() {
        MatcherAssert.assertThat(Config.empty().asInt().supplier(5).get(), Is.is(5));
    }

    @Test
    public void testAsStringListSupplier() {
        MatcherAssert.assertThat(Config.empty().asList(String.class).supplier().get(), Is.is(Matchers.empty()));
    }

    @Test
    public void testAsStringListDefaultSupplier() {
        List<String> list = CollectionsHelper.listOf("record");
        MatcherAssert.assertThat(Config.empty().asList(String.class).supplier(list).get(), Is.is(Matchers.empty()));
    }

    @Test
    public void testAsIntListSupplier() {
        MatcherAssert.assertThat(Config.empty().asList(Integer.class).supplier().get(), Is.is(Matchers.empty()));
    }

    @Test
    public void testAsIntListDefaultSupplier() {
        List<Integer> list = CollectionsHelper.listOf(5);
        MatcherAssert.assertThat(Config.empty().asList(Integer.class).supplier(list).get(), Is.is(Matchers.empty()));
    }

    @Test
    public void testTypeSupplier() {
        MatcherAssert.assertThat(Config.empty().asNode().optionalSupplier().get().get().type(), Is.is(OBJECT));
    }

    @Test
    public void testMapSupplier() {
        Assertions.assertThrows(MissingValueException.class, () -> {
            Config.empty().asString().as(ConfigMappers::toBigInteger).supplier().get();
        });
    }
}

