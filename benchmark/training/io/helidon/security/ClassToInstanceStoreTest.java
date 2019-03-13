/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.security;


import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Test of class to instance map.
 */
@SuppressWarnings("ConstantConditions")
public class ClassToInstanceStoreTest {
    @Test
    public void simpleInstance() {
        ClassToInstanceStore<Object> ctim = new ClassToInstanceStore();
        String first = "a String";
        String second = "a second String";
        Optional<String> existing = ctim.putInstance(String.class, first);
        MatcherAssert.assertThat(existing.isPresent(), CoreMatchers.is(false));
        existing = ctim.putInstance(String.class, second);
        MatcherAssert.assertThat(existing.isPresent(), CoreMatchers.is(true));
        MatcherAssert.assertThat(existing.get(), CoreMatchers.is(first));
        existing = ctim.getInstance(String.class);
        MatcherAssert.assertThat(existing.isPresent(), CoreMatchers.is(true));
        MatcherAssert.assertThat(existing.get(), CoreMatchers.is(second));
        MatcherAssert.assertThat(ctim.getInstance(CharSequence.class).isPresent(), CoreMatchers.is(false));
        Optional<String> removed = ctim.removeInstance(String.class);
        MatcherAssert.assertThat(removed.get(), CoreMatchers.is(second));
        removed = ctim.getInstance(String.class);
        MatcherAssert.assertThat(removed.isPresent(), CoreMatchers.is(false));
    }

    @Test
    public void ifaceAndImpl() {
        ClassToInstanceStore<Object> ctim = new ClassToInstanceStore();
        CharSequence first = "a String";
        CharSequence second = "a second String";
        Optional<CharSequence> existing = ctim.putInstance(CharSequence.class, first);
        MatcherAssert.assertThat(existing.isPresent(), CoreMatchers.is(false));
        existing = ctim.putInstance(CharSequence.class, second);
        MatcherAssert.assertThat(existing.isPresent(), CoreMatchers.is(true));
        MatcherAssert.assertThat(existing.get(), CoreMatchers.is(first));
        existing = ctim.getInstance(CharSequence.class);
        MatcherAssert.assertThat(existing.isPresent(), CoreMatchers.is(true));
        MatcherAssert.assertThat(existing.get(), CoreMatchers.is(second));
        MatcherAssert.assertThat(ctim.getInstance(String.class).isPresent(), CoreMatchers.is(false));
    }

    @Test
    public void putAll() {
        ClassToInstanceStore<Object> ctim = new ClassToInstanceStore();
        String first = "a String";
        String second = "a second String";
        ctim.putInstance(CharSequence.class, first);
        ctim.putInstance(String.class, second);
        ClassToInstanceStore<Object> copy = new ClassToInstanceStore();
        copy.putAll(ctim);
        MatcherAssert.assertThat(copy.getInstance(CharSequence.class).get(), CoreMatchers.is(first));
        MatcherAssert.assertThat(copy.getInstance(String.class).get(), CoreMatchers.is(second));
    }

    @Test
    public void containsKey() {
        ClassToInstanceStore<Object> ctim = new ClassToInstanceStore();
        String first = "a String";
        String second = "a second String";
        ctim.putInstance(CharSequence.class, first);
        ctim.putInstance(String.class, second);
        MatcherAssert.assertThat(ctim.containsKey(CharSequence.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(ctim.containsKey(String.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(ctim.containsKey(Serializable.class), CoreMatchers.is(false));
    }

    @Test
    public void testIsEmpty() {
        ClassToInstanceStore<CharSequence> ctis = new ClassToInstanceStore();
        MatcherAssert.assertThat(ctis.isEmpty(), CoreMatchers.is(true));
        ctis.putInstance(String.class, "test");
        MatcherAssert.assertThat(ctis.isEmpty(), CoreMatchers.is(false));
    }

    @Test
    public void testPutInstanceNoClass() {
        ClassToInstanceStore<CharSequence> ctis = new ClassToInstanceStore();
        Optional<String> optional = ctis.putInstance("test");
        if (optional.isPresent()) {
            Assertions.fail("There should have been no existing mapping");
        }
        ctis.putInstance(new StringBuilder("content"));
        // the class returned by "dd".getClass()
        MatcherAssert.assertThat(ctis.containsKey(String.class), CoreMatchers.is(true));
        // string builder
        MatcherAssert.assertThat(ctis.containsKey(StringBuilder.class), CoreMatchers.is(true));
        // interface should not be there
        MatcherAssert.assertThat(ctis.containsKey(CharSequence.class), CoreMatchers.is(false));
        MatcherAssert.assertThat(ctis.getInstance(String.class).get(), CoreMatchers.is("test"));
        Optional<String> other = ctis.putInstance("other");
        if (other.isPresent()) {
            MatcherAssert.assertThat(other.get(), CoreMatchers.is("test"));
        } else {
            Assertions.fail("There should have been an existing mapping");
        }
    }

    @Test
    public void testKeysAndValues() {
        ClassToInstanceStore<CharSequence> ctis = new ClassToInstanceStore();
        String value1 = "aValue";
        StringBuffer value2 = new StringBuffer("anotherValue");
        StringBuilder value3 = new StringBuilder("someValue");
        ctis.putInstance(String.class, value1);
        ctis.putInstance(StringBuffer.class, value2);
        ctis.putInstance(CharSequence.class, value3);
        Collection<Class<? extends CharSequence>> keys = ctis.keys();
        Collection<CharSequence> values = ctis.values();
        MatcherAssert.assertThat(keys.size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(values.size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(keys, CoreMatchers.hasItems(String.class, CharSequence.class, StringBuffer.class));
        MatcherAssert.assertThat(values, CoreMatchers.hasItems(value1, value2, value3));
    }

    @Test
    public void testToString() {
        ClassToInstanceStore<CharSequence> ctis = new ClassToInstanceStore();
        ctis.putInstance("MyValue");
        ctis.putInstance(new StringBuilder("Some value"));
        String result = ctis.toString();
        MatcherAssert.assertThat(result, CoreMatchers.containsString("MyValue"));
        MatcherAssert.assertThat(result, CoreMatchers.containsString("Some value"));
    }
}

