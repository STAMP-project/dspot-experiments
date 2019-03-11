/**
 * Copyright 2014 Goldman Sachs.
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
package com.gs.collections.impl;


import Lists.mutable;
import com.gs.collections.api.RichIterable;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.Procedures;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.test.Verify;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class tests various algorithms for calculating anagrams from a list of words.
 */
public class AnagramTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnagramTest.class);

    private static final int SIZE_THRESHOLD = 10;

    @Test
    public void anagramsWithMultimapInlined() {
        MutableList<RichIterable<String>> results = this.getWords().groupBy(AnagramTest.Alphagram::new).multiValuesView().select(( iterable) -> (iterable.size()) >= (SIZE_THRESHOLD)).toSortedList(Functions.toIntComparator(RichIterable::size));
        results.asReversed().collect(( iterable) -> ((iterable.size()) + ": ") + iterable).each(AnagramTest.LOGGER::info);
        Verify.assertIterableSize(AnagramTest.SIZE_THRESHOLD, results.getFirst());
    }

    @Test
    public void anagramsWithMultimapGSCollections1() {
        MutableList<RichIterable<String>> results = this.getWords().groupBy(AnagramTest.Alphagram::new).multiValuesView().select(( iterable) -> (iterable.size()) >= (SIZE_THRESHOLD)).toSortedList(Functions.toIntComparator(( iterable) -> -(iterable.size())));
        results.collect(( iterable) -> ((iterable.size()) + ": ") + iterable).each(AnagramTest.LOGGER::info);
        Verify.assertIterableSize(AnagramTest.SIZE_THRESHOLD, results.getLast());
    }

    @Test
    public void anagramsWithMultimapGSCollections3() {
        MutableList<RichIterable<String>> results = this.getWords().groupBy(AnagramTest.Alphagram::new).multiValuesView().toSortedList(Functions.toIntComparator(( iterable) -> -(iterable.size())));
        results.collectIf(( iterable) -> (iterable.size()) >= (SIZE_THRESHOLD), ( iterable) -> ((iterable.size()) + ": ") + iterable).each(AnagramTest.LOGGER::info);
        Verify.assertIterableSize(AnagramTest.SIZE_THRESHOLD, results.getLast());
    }

    @Test
    public void anagramsWithMultimapGSCollections4() {
        MutableList<RichIterable<String>> results = this.getWords().groupBy(AnagramTest.Alphagram::new).multiValuesView().toSortedList(Functions.toIntComparator(( iterable) -> -(iterable.size())));
        results.forEach(Procedures.ifTrue(( iterable) -> (iterable.size()) >= (SIZE_THRESHOLD), Functions.bind(Procedures.cast(AnagramTest.LOGGER::info), ( iterable) -> ((iterable.size()) + ": ") + iterable)));
        Verify.assertIterableSize(AnagramTest.SIZE_THRESHOLD, results.getLast());
    }

    @Test
    public void anagramsWithMultimapLazyIterable1() {
        MutableList<RichIterable<String>> results = this.getWords().groupBy(AnagramTest.Alphagram::new).multiValuesView().toSortedList(Functions.toIntComparator(RichIterable::size));
        results.asReversed().collectIf(( iterable) -> (iterable.size()) >= (SIZE_THRESHOLD), ( iterable) -> ((iterable.size()) + ": ") + iterable).forEach(Procedures.cast(AnagramTest.LOGGER::info));
        Verify.assertIterableSize(AnagramTest.SIZE_THRESHOLD, results.getFirst());
    }

    @Test
    public void anagramsWithMultimapForEachMultiValue() {
        MutableList<RichIterable<String>> results = mutable.of();
        this.getWords().groupBy(AnagramTest.Alphagram::new).multiValuesView().forEach(Procedures.ifTrue(( iterable) -> (iterable.size()) >= (SIZE_THRESHOLD), results::add));
        results.sortThisByInt(( iterable) -> -(iterable.size())).forEach(Functions.bind(Procedures.cast(AnagramTest.LOGGER::info), ( iterable) -> ((iterable.size()) + ": ") + iterable));
        Verify.assertIterableSize(AnagramTest.SIZE_THRESHOLD, results.getLast());
    }

    @Test
    public void anagramsUsingMapGetIfAbsentPutInsteadOfGroupBy() {
        MutableMap<AnagramTest.Alphagram, MutableList<String>> map = UnifiedMap.newMap();
        this.getWords().each(( word) -> map.getIfAbsentPut(new com.gs.collections.impl.Alphagram(word), FastList::new).add(word));
        MutableList<MutableList<String>> results = map.select(( iterable) -> (iterable.size()) >= (SIZE_THRESHOLD), mutable.<MutableList<String>>of()).sortThisByInt(( iterable) -> -(iterable.size()));
        results.forEach(Functions.bind(Procedures.cast(AnagramTest.LOGGER::info), ( iterable) -> ((iterable.size()) + ": ") + iterable));
        Assert.assertTrue(this.listContainsTestGroupAtElementsOneOrTwo(results));
        Verify.assertSize(AnagramTest.SIZE_THRESHOLD, results.getLast());
    }

    private static final class Alphagram {
        private final char[] key;

        private Alphagram(String string) {
            this.key = string.toCharArray();
            Arrays.sort(this.key);
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((this.getClass()) != (o.getClass()))) {
                return false;
            }
            AnagramTest.Alphagram alphagram = ((AnagramTest.Alphagram) (o));
            return Arrays.equals(this.key, alphagram.key);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(this.key);
        }

        @Override
        public String toString() {
            return new String(this.key);
        }
    }
}

