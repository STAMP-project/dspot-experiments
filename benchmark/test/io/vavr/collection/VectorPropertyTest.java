/**
 * __    __  __  __    __  ___
 * \  \  /  /    \  \  /  /  __/
 *  \  \/  /  /\  \  \/  /  /
 *   \____/__/  \__\____/__/
 *
 * Copyright 2014-2019 Vavr, http://vavr.io
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
package io.vavr.collection;


import io.vavr.API;
import io.vavr.Tuple2;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static BitMappedTrie.BRANCHING_FACTOR;


public class VectorPropertyTest {
    @Test
    public void shouldCreateAndGet() {
        for (int i = 0; i < 500; i++) {
            final Seq<Integer> expected = Array.range(0, i);
            final Vector<Integer> actual = Vector.ofAll(expected);
            for (int j = 0; j < (actual.size()); j++) {
                assertThat(expected.get(j)).isEqualTo(actual.get(j));
            }
            assert (i == 0) || (!(actual.trie.type.type().isPrimitive()));
            /* boolean */
            final Seq<Boolean> expectedBoolean = expected.map(( v) -> v > 0);
            final Vector<Boolean> actualBoolean = Vector.ofAll(ArrayType.<boolean[]>asPrimitives(boolean.class, expectedBoolean));
            assert (i == 0) || ((actualBoolean.trie.type.type()) == (boolean.class));
            VectorPropertyTest.assertAreEqual(expectedBoolean, actualBoolean);
            VectorPropertyTest.assertAreEqual(expectedBoolean.append(null), actualBoolean.append(null));
            /* byte */
            final Seq<Byte> expectedByte = expected.map(Integer::byteValue);
            final Vector<Byte> actualByte = Vector.ofAll(ArrayType.<byte[]>asPrimitives(byte.class, expectedByte));
            assert (i == 0) || ((actualByte.trie.type.type()) == (byte.class));
            VectorPropertyTest.assertAreEqual(expectedByte, actualByte);
            VectorPropertyTest.assertAreEqual(expectedByte.append(null), actualByte.append(null));
            /* char */
            final Seq<Character> expectedChar = expected.map(( v) -> ((char) (v.intValue())));
            final Vector<Character> actualChar = Vector.ofAll(ArrayType.<char[]>asPrimitives(char.class, expectedChar));
            assert (i == 0) || ((actualChar.trie.type.type()) == (char.class));
            VectorPropertyTest.assertAreEqual(expectedChar, actualChar);
            VectorPropertyTest.assertAreEqual(expectedChar.append(null), actualChar.append(null));
            /* double */
            final Seq<Double> expectedDouble = expected.map(Integer::doubleValue);
            final Vector<Double> actualDouble = Vector.ofAll(ArrayType.<double[]>asPrimitives(double.class, expectedDouble));
            assert (i == 0) || ((actualDouble.trie.type.type()) == (double.class));
            VectorPropertyTest.assertAreEqual(expectedDouble, actualDouble);
            VectorPropertyTest.assertAreEqual(expectedDouble.append(null), actualDouble.append(null));
            /* float */
            final Seq<Float> expectedFloat = expected.map(Integer::floatValue);
            final Vector<Float> actualFloat = Vector.ofAll(ArrayType.<float[]>asPrimitives(float.class, expectedFloat));
            assert (i == 0) || ((actualFloat.trie.type.type()) == (float.class));
            VectorPropertyTest.assertAreEqual(expectedFloat, actualFloat);
            VectorPropertyTest.assertAreEqual(expectedFloat.append(null), actualFloat.append(null));
            /* int */
            final Vector<Integer> actualInt = Vector.ofAll(ArrayType.<int[]>asPrimitives(int.class, expected));
            assert (i == 0) || ((actualInt.trie.type.type()) == (int.class));
            VectorPropertyTest.assertAreEqual(expected, actualInt);
            VectorPropertyTest.assertAreEqual(expected.append(null), actual.append(null));
            /* long */
            final Seq<Long> expectedLong = expected.map(Integer::longValue);
            final Vector<Long> actualLong = Vector.ofAll(ArrayType.<long[]>asPrimitives(long.class, expectedLong));
            assert (i == 0) || ((actualLong.trie.type.type()) == (long.class));
            VectorPropertyTest.assertAreEqual(expectedLong, actualLong);
            VectorPropertyTest.assertAreEqual(expectedLong.append(null), actualLong.append(null));
            /* short */
            final Seq<Short> expectedShort = expected.map(Integer::shortValue);
            final Vector<Short> actualShort = Vector.ofAll(ArrayType.<short[]>asPrimitives(short.class, expectedShort));
            assert (i == 0) || ((actualShort.trie.type.type()) == (short.class));
            VectorPropertyTest.assertAreEqual(expectedShort, actualShort);
            VectorPropertyTest.assertAreEqual(expectedShort.append(null), actualShort.append(null));
        }
    }

    @Test
    public void shouldIterate() {
        for (byte depth = 0; depth <= 2; depth++) {
            for (int i = 0; i < 5000; i++) {
                final Seq<Integer> expected = Array.range(0, i);
                final Vector<Integer> actual = Vector.ofAll(expected);
                VectorPropertyTest.assertAreEqual(actual, expected);
            }
        }
        Seq<Integer> expected = Array.range(0, 1000);
        Vector<Integer> actual = Vector.ofAll(ArrayType.<int[]>asPrimitives(int.class, expected));
        for (int drop = 0; drop <= ((BRANCHING_FACTOR) + 1); drop++) {
            final Iterator<Integer> expectedIterator = expected.iterator();
            actual.trie.<int[]>visit(( index, leaf, start, end) -> {
                for (int i = start; i < end; i++) {
                    assertThat(leaf[i]).isEqualTo(expectedIterator.next());
                }
                return -1;
            });
            expected = expected.tail().init();
            actual = actual.tail().init();
        }
    }

    @Test
    public void shouldPrepend() {
        Seq<Integer> expected = Array.empty();
        Vector<Integer> actual = Vector.empty();
        for (int drop = 0; drop <= ((BRANCHING_FACTOR) + 1); drop++) {
            for (Integer value : Iterator.range(0, 1000)) {
                expected = expected.drop(drop);
                actual = VectorPropertyTest.assertAreEqual(actual, drop, Vector::drop, expected);
                expected = expected.prepend(value);
                actual = VectorPropertyTest.assertAreEqual(actual, value, Vector::prepend, expected);
            }
        }
    }

    @Test
    public void shouldAppend() {
        Seq<Integer> expected = Array.empty();
        Vector<Integer> actual = Vector.empty();
        for (int drop = 0; drop <= ((BRANCHING_FACTOR) + 1); drop++) {
            for (Integer value : Iterator.range(0, 500)) {
                expected = expected.drop(drop);
                actual = VectorPropertyTest.assertAreEqual(actual, drop, Vector::drop, expected);
                expected = expected.append(value);
                actual = VectorPropertyTest.assertAreEqual(actual, value, Vector::append, expected);
            }
        }
    }

    @Test
    public void shouldUpdate() {
        final Function<Integer, Integer> mapper = ( i) -> i + 1;
        for (byte depth = 0; depth <= 2; depth++) {
            final int length = 10000;
            for (int drop = 0; drop <= ((BRANCHING_FACTOR) + 1); drop++) {
                Seq<Integer> expected = Array.range(0, length);
                Vector<Integer> actual = Vector.ofAll(expected);
                expected = expected.drop(drop);// test the `trailing` drops and the internal tree offset

                actual = VectorPropertyTest.assertAreEqual(actual, drop, Vector::drop, expected);
                for (int i = 0; i < (actual.length()); i++) {
                    final Integer newValue = mapper.apply(actual.get(i));
                    actual = actual.update(i, newValue);
                }
                VectorPropertyTest.assertAreEqual(actual, 0, ( a, p) -> a, expected.map(mapper));
            }
        }
    }

    @Test
    public void shouldDrop() {
        final Seq<Integer> expected = Array.range(0, 2000);
        final Vector<Integer> actual = Vector.ofAll(expected);
        Vector<Integer> actualSingleDrop = actual;
        for (int i = 0; i <= (expected.length()); i++) {
            final Seq<Integer> expectedDrop = expected.drop(i);
            VectorPropertyTest.assertAreEqual(actual, i, Vector::drop, expectedDrop);
            VectorPropertyTest.assertAreEqual(actualSingleDrop, null, ( a, p) -> a, expectedDrop);
            actualSingleDrop = actualSingleDrop.drop(1);
        }
    }

    @Test
    public void shouldDropRight() {
        final Seq<Integer> expected = Array.range(0, 2000);
        final Vector<Integer> actual = Vector.ofAll(expected);
        Vector<Integer> actualSingleDrop = actual;
        for (int i = 0; i <= (expected.length()); i++) {
            final Seq<Integer> expectedDrop = expected.dropRight(i);
            VectorPropertyTest.assertAreEqual(actual, i, Vector::dropRight, expectedDrop);
            VectorPropertyTest.assertAreEqual(actualSingleDrop, null, ( a, p) -> a, expectedDrop);
            actualSingleDrop = actualSingleDrop.dropRight(1);
        }
    }

    @Test
    public void shouldSlice() {
        for (int length = 1, end = 500; length <= end; length++) {
            Seq<Integer> expected = Array.range(0, length);
            Vector<Integer> actual = Vector.ofAll(expected);
            for (int i = 0; i <= (expected.length()); i++) {
                expected = expected.slice(1, ((expected.size()) - 1));
                actual = VectorPropertyTest.assertAreEqual(actual, i, ( a, p) -> a.slice(1, ((a.size()) - 1)), expected);
            }
        }
    }

    @Test
    public void shouldBehaveLikeArray() {
        final Random random = new Random(13579);
        for (int i = 1; i < 10; i++) {
            Seq<Object> expected = Array.empty();
            Vector<Object> actual = Vector.empty();
            for (int j = 0; j < 20000; j++) {
                Seq<Tuple2<Seq<Object>, Vector<Object>>> history = Array.empty();
                if ((percent(random)) < 20) {
                    expected = Array.ofAll(Vector.ofAll(randomValues(random, 100)).filter(( v) -> v instanceof Integer));
                    actual = ((percent(random)) < 30) ? Vector.narrow(Vector.ofAll(ArrayType.<int[]>asPrimitives(int.class, expected))) : Vector.ofAll(expected);
                    VectorPropertyTest.assertAreEqual(expected, actual);
                    history = history.append(API.Tuple(expected, actual));
                }
                if ((percent(random)) < 50) {
                    final Object value = randomValue(random);
                    expected = expected.append(value);
                    actual = VectorPropertyTest.assertAreEqual(actual, value, Vector::append, expected);
                    history = history.append(API.Tuple(expected, actual));
                }
                if ((percent(random)) < 10) {
                    Iterable<Object> values = randomValues(random, random.nextInt((2 * (BRANCHING_FACTOR))));
                    expected = expected.appendAll(values);
                    values = ((percent(random)) < 50) ? Iterator.ofAll(values.iterator()) : values;/* not traversable again */

                    actual = VectorPropertyTest.assertAreEqual(actual, values, Vector::appendAll, expected);
                    history = history.append(API.Tuple(expected, actual));
                }
                if ((percent(random)) < 50) {
                    final Object value = randomValue(random);
                    expected = expected.prepend(value);
                    actual = VectorPropertyTest.assertAreEqual(actual, value, Vector::prepend, expected);
                    history = history.append(API.Tuple(expected, actual));
                }
                if ((percent(random)) < 10) {
                    Iterable<Object> values = randomValues(random, random.nextInt((2 * (BRANCHING_FACTOR))));
                    expected = expected.prependAll(values);
                    values = ((percent(random)) < 50) ? Iterator.ofAll(values) : values;/* not traversable again */

                    actual = VectorPropertyTest.assertAreEqual(actual, values, Vector::prependAll, expected);
                    history = history.append(API.Tuple(expected, actual));
                }
                if ((percent(random)) < 30) {
                    final int n = random.nextInt(((expected.size()) + 1));
                    expected = expected.drop(n);
                    actual = VectorPropertyTest.assertAreEqual(actual, n, Vector::drop, expected);
                    history = history.append(API.Tuple(expected, actual));
                }
                if ((percent(random)) < 10) {
                    final int index = random.nextInt(((expected.size()) + 1));
                    Iterable<Object> values = randomValues(random, random.nextInt((2 * (BRANCHING_FACTOR))));
                    expected = expected.insertAll(index, values);
                    values = ((percent(random)) < 50) ? Iterator.ofAll(values) : values;/* not traversable again */

                    actual = VectorPropertyTest.assertAreEqual(actual, values, ( a, p) -> a.insertAll(index, p), expected);
                    history = history.append(API.Tuple(expected, actual));
                }
                if ((percent(random)) < 30) {
                    final int n = random.nextInt(((expected.size()) + 1));
                    expected = expected.take(n);
                    actual = VectorPropertyTest.assertAreEqual(actual, n, Vector::take, expected);
                    history = history.append(API.Tuple(expected, actual));
                }
                if (!(expected.isEmpty())) {
                    assertThat(actual.head()).isEqualTo(expected.head());
                    Assertions.assertThat(actual.tail().toJavaList()).isEqualTo(expected.tail().toJavaList());
                    history = history.append(API.Tuple(expected, actual));
                }
                if (!(expected.isEmpty())) {
                    final int index = random.nextInt(expected.size());
                    assertThat(actual.get(index)).isEqualTo(expected.get(index));
                    history = history.append(API.Tuple(expected, actual));
                }
                if ((percent(random)) < 50) {
                    if (!(expected.isEmpty())) {
                        final int index = random.nextInt(expected.size());
                        final Object value = randomValue(random);
                        expected = expected.update(index, value);
                        actual = VectorPropertyTest.assertAreEqual(actual, null, ( a, p) -> a.update(index, value), expected);
                        history = history.append(API.Tuple(expected, actual));
                    }
                }
                if ((percent(random)) < 20) {
                    final Function<Object, Object> mapper = ( val) -> val instanceof Integer ? ((Integer) (val)) + 1 : val;
                    expected = expected.map(mapper);
                    actual = VectorPropertyTest.assertAreEqual(actual, null, ( a, p) -> a.map(mapper), expected);
                    history = history.append(API.Tuple(expected, actual));
                }
                if ((percent(random)) < 30) {
                    final Predicate<Object> filter = ( val) -> ((String.valueOf(val).length()) % 10) == 0;
                    expected = expected.filter(filter);
                    actual = VectorPropertyTest.assertAreEqual(actual, null, ( a, p) -> a.filter(filter), expected);
                    history = history.append(API.Tuple(expected, actual));
                }
                if ((percent(random)) < 30) {
                    for (int k = 0; k < 2; k++) {
                        if (!(expected.isEmpty())) {
                            final int to = random.nextInt(expected.size());
                            final int from = random.nextInt((to + 1));
                            expected = expected.slice(from, to);
                            actual = VectorPropertyTest.assertAreEqual(actual, null, ( a, p) -> a.slice(from, to), expected);
                            history = history.append(API.Tuple(expected, actual));
                        }
                    }
                }
                history.forEach(( t) -> assertAreEqual(t._1, t._2));// test that the modifications are persistent

            }
        }
    }
}

