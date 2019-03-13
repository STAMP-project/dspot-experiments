/**
 * Copyright 2014-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.agrona.collections;


import java.util.HashSet;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;


public class BiInt2ObjectMapTest {
    private final BiInt2ObjectMap<String> map = new BiInt2ObjectMap();

    @Test
    public void shouldInitialiseUnderlyingImplementation() {
        final int initialCapacity = 10;
        final float loadFactor = 0.6F;
        final BiInt2ObjectMap<String> map = new BiInt2ObjectMap(initialCapacity, loadFactor);
        Assert.assertThat(map.capacity(), CoreMatchers.either(CoreMatchers.is(initialCapacity)).or(Matchers.greaterThan(initialCapacity)));
        Assert.assertThat(map.loadFactor(), CoreMatchers.is(loadFactor));
    }

    @Test
    public void shouldReportEmpty() {
        Assert.assertThat(map.isEmpty(), CoreMatchers.is(true));
    }

    @Test
    public void shouldPutItem() {
        final String testValue = "Test";
        final int keyPartA = 3;
        final int keyPartB = 7;
        Assert.assertNull(map.put(keyPartA, keyPartB, testValue));
        Assert.assertThat(map.size(), CoreMatchers.is(1));
    }

    @Test
    public void shouldPutAndGetItem() {
        final String testValue = "Test";
        final int keyPartA = 3;
        final int keyPartB = 7;
        Assert.assertNull(map.put(keyPartA, keyPartB, testValue));
        Assert.assertThat(map.get(keyPartA, keyPartB), CoreMatchers.is(testValue));
    }

    @Test
    public void shouldReturnNullWhenNotFoundItem() {
        final int keyPartA = 3;
        final int keyPartB = 7;
        Assert.assertNull(map.get(keyPartA, keyPartB));
    }

    @Test
    public void shouldRemoveItem() {
        final String testValue = "Test";
        final int keyPartA = 3;
        final int keyPartB = 7;
        map.put(keyPartA, keyPartB, testValue);
        Assert.assertThat(map.remove(keyPartA, keyPartB), CoreMatchers.is(testValue));
        Assert.assertNull(map.get(keyPartA, keyPartB));
    }

    @Test
    public void shouldIterateValues() {
        final Set<String> expectedSet = new HashSet<>();
        final int count = 7;
        for (int i = 0; i < count; i++) {
            final String value = String.valueOf(i);
            expectedSet.add(value);
            map.put(i, (i + 97), value);
        }
        final Set<String> actualSet = new HashSet<>();
        map.forEach(actualSet::add);
        Assert.assertThat(actualSet, IsEqual.equalTo(expectedSet));
    }

    @Test
    public void shouldIterateEntries() {
        final Set<BiInt2ObjectMapTest.EntryCapture<String>> expectedSet = new HashSet<>();
        final int count = 7;
        for (int i = 0; i < count; i++) {
            final String value = String.valueOf(i);
            expectedSet.add(new BiInt2ObjectMapTest.EntryCapture<>(i, (i + 97), value));
            map.put(i, (i + 97), value);
        }
        final Set<BiInt2ObjectMapTest.EntryCapture<String>> actualSet = new HashSet<>();
        map.forEach(( keyPartA, keyPartB, value) -> actualSet.add(new EntryCapture<>(keyPartA, keyPartB, value)));
        Assert.assertThat(actualSet, IsEqual.equalTo(expectedSet));
    }

    @Test
    public void shouldToString() {
        final int count = 7;
        for (int i = 0; i < count; i++) {
            final String value = String.valueOf(i);
            map.put(i, (i + 97), value);
        }
        Assert.assertThat(map.toString(), CoreMatchers.is("{1_98=1, 3_100=3, 2_99=2, 5_102=5, 6_103=6, 4_101=4, 0_97=0}"));
    }

    @Test
    public void shouldPutAndGetKeysOfNegativeValue() {
        map.put(721632679, 333118496, "a");
        Assert.assertThat(map.get(721632679, 333118496), CoreMatchers.is("a"));
        map.put(721632719, (-659033725), "b");
        Assert.assertThat(map.get(721632719, (-659033725)), CoreMatchers.is("b"));
        map.put(721632767, (-235401032), "c");
        Assert.assertThat(map.get(721632767, (-235401032)), CoreMatchers.is("c"));
        map.put(721632839, 1791470537, "d");
        Assert.assertThat(map.get(721632839, 1791470537), CoreMatchers.is("d"));
        map.put(721633069, (-939458690), "e");
        Assert.assertThat(map.get(721633069, (-939458690)), CoreMatchers.is("e"));
        map.put(721633127, 1620485039, "f");
        Assert.assertThat(map.get(721633127, 1620485039), CoreMatchers.is("f"));
        map.put(721633163, (-1503337805), "g");
        Assert.assertThat(map.get(721633163, (-1503337805)), CoreMatchers.is("g"));
        map.put(721633229, (-2073657736), "h");
        Assert.assertThat(map.get(721633229, (-2073657736)), CoreMatchers.is("h"));
        map.put(721633255, (-1278969172), "i");
        Assert.assertThat(map.get(721633255, (-1278969172)), CoreMatchers.is("i"));
        map.put(721633257, (-1230662585), "j");
        Assert.assertThat(map.get(721633257, (-1230662585)), CoreMatchers.is("j"));
        map.put(721633319, (-532637417), "k");
        Assert.assertThat(map.get(721633319, (-532637417)), CoreMatchers.is("k"));
    }

    public static class EntryCapture<V> {
        public final int keyPartA;

        public final int keyPartB;

        public final V value;

        public EntryCapture(final int keyPartA, final int keyPartB, final V value) {
            this.keyPartA = keyPartA;
            this.keyPartB = keyPartB;
            this.value = value;
        }

        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            final BiInt2ObjectMapTest.EntryCapture that = ((BiInt2ObjectMapTest.EntryCapture) (o));
            return (((keyPartA) == (that.keyPartA)) && ((keyPartB) == (that.keyPartB))) && (value.equals(that.value));
        }

        public int hashCode() {
            int result = keyPartA;
            result = (31 * result) + (keyPartB);
            result = (31 * result) + (value.hashCode());
            return result;
        }
    }
}

