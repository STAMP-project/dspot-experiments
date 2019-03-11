/**
 * Copyright 2017-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.commonutil.iterable;


import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.IntConsumer;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;


public class LimitedInMemorySortedIterableTest {
    @Test
    public void shouldLimitEntries() {
        // Given
        final LimitedInMemorySortedIterable<Integer> list = new LimitedInMemorySortedIterable<Integer>(Comparator.naturalOrder(), 100);
        final List<Integer> expectedItems = new ArrayList<>();
        IntStream.rangeClosed(1, 100).forEach(expectedItems::add);
        // When
        for (int i = 200; 0 < i; i--) {
            list.add(i);
        }
        // Then
        Assert.assertEquals(expectedItems, Lists.newArrayList(list));
    }

    @Test
    public void shouldLimitAndDeduplicateEntries() {
        // Given
        final LimitedInMemorySortedIterable<Integer> list = new LimitedInMemorySortedIterable<Integer>(Comparator.naturalOrder(), 2, true);
        // When
        list.add(1);
        list.add(1);
        list.add(2);
        list.add(1);
        list.add(2);
        list.add(10);
        // Then
        Assert.assertEquals(Arrays.asList(1, 2), Lists.newArrayList(list));
    }

    @Test
    public void shouldDeduplicateEntries() {
        // Given
        final LimitedInMemorySortedIterable<Integer> list = new LimitedInMemorySortedIterable<Integer>(Comparator.naturalOrder(), 100, true);
        // When
        list.add(1);
        list.add(1);
        // Then
        Assert.assertEquals(Collections.singletonList(1), Lists.newArrayList(list));
    }

    @Test
    public void shouldNotDeduplicateEntries() {
        // Given
        final LimitedInMemorySortedIterable<Integer> list = new LimitedInMemorySortedIterable<Integer>(Comparator.naturalOrder(), 100, false);
        // When
        list.add(1);
        list.add(1);
        // Then
        Assert.assertEquals(Arrays.asList(1, 1), Lists.newArrayList(list));
    }

    @Test
    public void shouldLimitAndNotDeduplicateEntries() {
        // Given
        final LimitedInMemorySortedIterable<Integer> list = new LimitedInMemorySortedIterable<Integer>(Comparator.naturalOrder(), 4, false);
        // When
        list.add(1);
        list.add(2);
        list.add(1);
        list.add(2);
        list.add(10);
        // Then
        Assert.assertEquals(Arrays.asList(1, 1, 2, 2), Lists.newArrayList(list));
    }

    @Test
    public void shouldAddAll() {
        // Given
        final LimitedInMemorySortedIterable<Integer> itr = new LimitedInMemorySortedIterable<Integer>(Comparator.naturalOrder(), 100);
        // When/Then
        final List<Integer> evens = IntStream.iterate(0, ( i) -> i + 2).limit(10).boxed().collect(Collectors.toList());
        final boolean evensResult = itr.addAll(evens);
        MatcherAssert.assertThat(evens, hasSize(10));
        MatcherAssert.assertThat(evensResult, IsEqual.equalTo(true));
        Assert.assertEquals(10, itr.size());
        List<Integer> list = Lists.newArrayList(itr);
        MatcherAssert.assertThat(list.get(0), IsEqual.equalTo(0));
        MatcherAssert.assertThat(list.get(((list.size()) - 1)), IsEqual.equalTo(18));
        final List<Integer> odds = IntStream.iterate(1, ( i) -> i + 2).limit(10).boxed().collect(Collectors.toList());
        final boolean oddsResult = itr.addAll(odds);
        list = Lists.newArrayList(itr);
        MatcherAssert.assertThat(odds, hasSize(10));
        MatcherAssert.assertThat(oddsResult, IsEqual.equalTo(true));
        MatcherAssert.assertThat(list, hasSize(20));
        MatcherAssert.assertThat(list.get(0), IsEqual.equalTo(0));
        MatcherAssert.assertThat(list.get(((itr.size()) - 1)), IsEqual.equalTo(19));
    }

    @Test
    public void shouldLimitEntriesOnAddAll() {
        // Given
        final LimitedInMemorySortedIterable<Integer> itr = new LimitedInMemorySortedIterable<Integer>(Comparator.naturalOrder(), 10);
        // When/Then
        final List<Integer> evens = IntStream.iterate(0, ( i) -> i + 2).limit(100).boxed().collect(Collectors.toList());
        final boolean evensResult = itr.addAll(evens);
        List<Integer> list = Lists.newArrayList(itr);
        MatcherAssert.assertThat(evens, hasSize(100));
        MatcherAssert.assertThat(evensResult, IsEqual.equalTo(true));
        MatcherAssert.assertThat(list, hasSize(10));
        MatcherAssert.assertThat(list.get(0), IsEqual.equalTo(0));
        MatcherAssert.assertThat(list.get(((itr.size()) - 1)), IsEqual.equalTo(18));
        final List<Integer> odds = IntStream.iterate(1, ( i) -> i + 2).limit(100).boxed().collect(Collectors.toList());
        final boolean oddsResult = itr.addAll(odds);
        list = Lists.newArrayList(itr);
        MatcherAssert.assertThat(odds, hasSize(100));
        MatcherAssert.assertThat(oddsResult, IsEqual.equalTo(true));
        MatcherAssert.assertThat(list, hasSize(10));
        MatcherAssert.assertThat(list.get(0), IsEqual.equalTo(0));
        MatcherAssert.assertThat(list.get(((itr.size()) - 1)), IsEqual.equalTo(9));
    }

    @Test
    public void shouldSortLargeNumberOfItems() {
        // Given
        final int streamSize = 1000000;
        final int resultLimit = 10000;
        final IntStream stream = // generate a few extra in case there are duplicates
        new Random().ints((streamSize * 2)).distinct().limit(streamSize);
        final LimitedInMemorySortedIterable<Integer> list = new LimitedInMemorySortedIterable<Integer>(Comparator.naturalOrder(), resultLimit, false);
        // When
        stream.forEach(( i) -> list.add(Math.abs(i)));
        // Then
        final List<Integer> elements = Lists.newArrayList(list);
        final List<Integer> sortedElements = Lists.newArrayList(list);
        sortedElements.sort(Comparator.naturalOrder());
        Assert.assertEquals(elements, sortedElements);
    }
}

