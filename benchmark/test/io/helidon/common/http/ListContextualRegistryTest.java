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
package io.helidon.common.http;


import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link ListContextualRegistry} and {@link ContextualRegistry}.
 */
public class ListContextualRegistryTest {
    @Test
    public void create() {
        MatcherAssert.assertThat(ContextualRegistry.create(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(ContextualRegistry.create(null), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(ContextualRegistry.create(ContextualRegistry.create()), CoreMatchers.notNullValue());
    }

    @Test
    public void registerAndGetLast() {
        ContextualRegistry context = ContextualRegistry.create();
        MatcherAssert.assertThat(context.get(String.class), CoreMatchers.is(Optional.empty()));
        MatcherAssert.assertThat(context.get(Integer.class), CoreMatchers.is(Optional.empty()));
        context.register("aaa");
        MatcherAssert.assertThat(context.get(String.class), CoreMatchers.is(Optional.of("aaa")));
        MatcherAssert.assertThat(context.get(Integer.class), CoreMatchers.is(Optional.empty()));
        context.register(1);
        MatcherAssert.assertThat(context.get(String.class), CoreMatchers.is(Optional.of("aaa")));
        MatcherAssert.assertThat(context.get(Integer.class), CoreMatchers.is(Optional.of(1)));
        MatcherAssert.assertThat(context.get(Object.class), CoreMatchers.is(Optional.of(1)));
        context.register("bbb");
        MatcherAssert.assertThat(context.get(String.class), CoreMatchers.is(Optional.of("bbb")));
        MatcherAssert.assertThat(context.get(Object.class), CoreMatchers.is(Optional.of("bbb")));
    }

    @Test
    public void registerAndGetLastClassifier() {
        ContextualRegistry context = ContextualRegistry.create();
        String classifier = "classifier";
        MatcherAssert.assertThat(context.get(classifier, String.class), CoreMatchers.is(Optional.empty()));
        MatcherAssert.assertThat(context.get(classifier, Integer.class), CoreMatchers.is(Optional.empty()));
        context.register(classifier, "aaa");
        MatcherAssert.assertThat(context.get(classifier, String.class), CoreMatchers.is(Optional.of("aaa")));
        MatcherAssert.assertThat(context.get(String.class), CoreMatchers.is(Optional.empty()));
        MatcherAssert.assertThat(context.get(classifier, Integer.class), CoreMatchers.is(Optional.empty()));
        context.register(classifier, 1);
        MatcherAssert.assertThat(context.get(classifier, String.class), CoreMatchers.is(Optional.of("aaa")));
        MatcherAssert.assertThat(context.get(classifier, Integer.class), CoreMatchers.is(Optional.of(1)));
        MatcherAssert.assertThat(context.get(classifier, Object.class), CoreMatchers.is(Optional.of(1)));
        context.register(classifier, "bbb");
        MatcherAssert.assertThat(context.get(classifier, String.class), CoreMatchers.is(Optional.of("bbb")));
        context.register("ccc");
        MatcherAssert.assertThat(context.get(classifier, String.class), CoreMatchers.is(Optional.of("bbb")));
        MatcherAssert.assertThat(context.get(classifier, Object.class), CoreMatchers.is(Optional.of("bbb")));
        MatcherAssert.assertThat(context.get(String.class), CoreMatchers.is(Optional.of("ccc")));
    }

    @Test
    public void emptyParent() {
        ContextualRegistry parent = ContextualRegistry.create();
        ContextualRegistry context = ContextualRegistry.create(parent);
        MatcherAssert.assertThat(context.get(String.class), CoreMatchers.is(Optional.empty()));
        context.register("aaa");
        MatcherAssert.assertThat(context.get(String.class), CoreMatchers.is(Optional.of("aaa")));
    }

    @Test
    public void testParent() {
        ContextualRegistry parent = ContextualRegistry.create();
        parent.register("ppp");
        ContextualRegistry context = ContextualRegistry.create(parent);
        MatcherAssert.assertThat(context.get(String.class), CoreMatchers.is(Optional.of("ppp")));
        context.register(1);
        MatcherAssert.assertThat(context.get(String.class), CoreMatchers.is(Optional.of("ppp")));
        context.register("aaa");
        MatcherAssert.assertThat(context.get(String.class), CoreMatchers.is(Optional.of("aaa")));
        MatcherAssert.assertThat(parent.get(String.class), CoreMatchers.is(Optional.of("ppp")));
    }

    @Test
    public void testParentWithClassifier() {
        String classifier = "classifier";
        ContextualRegistry parent = ContextualRegistry.create();
        parent.register(classifier, "ppp");
        ContextualRegistry context = ContextualRegistry.create(parent);
        MatcherAssert.assertThat(context.get(classifier, String.class), CoreMatchers.is(Optional.of("ppp")));
        context.register(classifier, 1);
        MatcherAssert.assertThat(context.get(classifier, String.class), CoreMatchers.is(Optional.of("ppp")));
        context.register(classifier, "aaa");
        MatcherAssert.assertThat(context.get(classifier, String.class), CoreMatchers.is(Optional.of("aaa")));
        MatcherAssert.assertThat(parent.get(classifier, String.class), CoreMatchers.is(Optional.of("ppp")));
    }

    @Test
    public void testSupply() {
        AtomicInteger counter = new AtomicInteger(0);
        ContextualRegistry context = ContextualRegistry.create();
        context.register(1);
        Date date = new Date();
        context.register(date);
        context.register("aaa");
        context.supply(String.class, () -> {
            counter.incrementAndGet();
            return "bbb";
        });
        context.register(2);
        MatcherAssert.assertThat(context.get(Date.class), CoreMatchers.is(Optional.of(date)));
        MatcherAssert.assertThat(counter.get(), CoreMatchers.is(0));
        MatcherAssert.assertThat(context.get(String.class), CoreMatchers.is(Optional.of("bbb")));
        MatcherAssert.assertThat(counter.get(), CoreMatchers.is(1));
        MatcherAssert.assertThat(context.get(String.class), CoreMatchers.is(Optional.of("bbb")));
        MatcherAssert.assertThat(counter.get(), CoreMatchers.is(1));
        MatcherAssert.assertThat(context.get(Date.class), CoreMatchers.is(Optional.of(date)));
        MatcherAssert.assertThat(context.get(String.class), CoreMatchers.is(Optional.of("bbb")));
        MatcherAssert.assertThat(counter.get(), CoreMatchers.is(1));
    }

    @Test
    public void testSupplyClassifier() {
        String classifier = "classifier";
        AtomicInteger counter = new AtomicInteger(0);
        ContextualRegistry context = ContextualRegistry.create();
        context.register(classifier, 1);
        Date date = new Date();
        context.register(classifier, date);
        context.register(classifier, "aaa");
        context.supply(classifier, String.class, () -> {
            counter.incrementAndGet();
            return "bbb";
        });
        context.register(classifier, 2);
        MatcherAssert.assertThat(context.get(classifier, Date.class), CoreMatchers.is(Optional.of(date)));
        MatcherAssert.assertThat(counter.get(), CoreMatchers.is(0));
        MatcherAssert.assertThat(context.get(classifier, String.class), CoreMatchers.is(Optional.of("bbb")));
        MatcherAssert.assertThat(counter.get(), CoreMatchers.is(1));
        MatcherAssert.assertThat(context.get(classifier, String.class), CoreMatchers.is(Optional.of("bbb")));
        MatcherAssert.assertThat(counter.get(), CoreMatchers.is(1));
        MatcherAssert.assertThat(context.get(classifier, Date.class), CoreMatchers.is(Optional.of(date)));
        MatcherAssert.assertThat(context.get(classifier, String.class), CoreMatchers.is(Optional.of("bbb")));
        MatcherAssert.assertThat(counter.get(), CoreMatchers.is(1));
    }
}

