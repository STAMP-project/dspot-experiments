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


import io.vavr.control.Option;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Spliterator;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class HashMapTest extends AbstractMapTest {
    // -- static narrow
    @Test
    public void shouldNarrowHashMap() {
        final HashMap<Integer, Double> int2doubleMap = mapOf(1, 1.0);
        final HashMap<Number, Number> number2numberMap = HashMap.narrow(int2doubleMap);
        final int actual = number2numberMap.put(new BigDecimal("2"), new BigDecimal("2.0")).values().sum().intValue();
        assertThat(actual).isEqualTo(3);
    }

    @Test
    public void shouldWrapMap() {
        final Map<Integer, Integer> source = new java.util.HashMap<>();
        source.put(1, 2);
        source.put(3, 4);
        assertThat(HashMap.ofAll(source)).isEqualTo(put(3, 4));
    }

    // -- specific
    @Test
    public void shouldCalculateHashCodeOfCollision() {
        Assertions.assertThat(put(0, 2).hashCode()).isEqualTo(put(0, 2).put(null, 1).hashCode());
        Assertions.assertThat(put(0, 2).hashCode()).isEqualTo(put(0, 2).hashCode());
    }

    @Test
    public void shouldCheckHashCodeInLeafList() {
        HashMap<Integer, Integer> trie = HashMap.empty();
        trie = trie.put(0, 1).put(null, 2);// LeafList.hash == 0

        final Option<Integer> none = trie.get((1 << 6));// (key.hash & BUCKET_BITS) == 0

        Assertions.assertThat(none).isEqualTo(Option.none());
    }

    @Test
    public void shouldCalculateBigHashCode() {
        HashMap<Integer, Integer> h1 = HashMap.empty();
        HashMap<Integer, Integer> h2 = HashMap.empty();
        final int count = 1234;
        for (int i = 0; i <= count; i++) {
            h1 = h1.put(i, i);
            h2 = h2.put((count - i), (count - i));
        }
        Assertions.assertThat(((h1.hashCode()) == (h2.hashCode()))).isTrue();
    }

    @Test
    public void shouldEqualsIgnoreOrder() {
        HashMap<String, Integer> map = HashMap.<String, Integer>empty().put("Aa", 1).put("BB", 2);
        HashMap<String, Integer> map2 = HashMap.<String, Integer>empty().put("BB", 2).put("Aa", 1);
        Assertions.assertThat(map.hashCode()).isEqualTo(map2.hashCode());
        Assertions.assertThat(map).isEqualTo(map2);
    }

    // -- spliterator
    @Test
    public void shouldNotHaveSortedSpliterator() {
        assertThat(of(1, 2, 3).spliterator().hasCharacteristics(Spliterator.SORTED)).isFalse();
    }

    @Test
    public void shouldNotHaveOrderedSpliterator() {
        assertThat(of(1, 2, 3).spliterator().hasCharacteristics(Spliterator.ORDERED)).isFalse();
    }

    // -- isSequential()
    @Test
    public void shouldReturnFalseWhenIsSequentialCalled() {
        assertThat(of(1, 2, 3).isSequential()).isFalse();
    }
}

