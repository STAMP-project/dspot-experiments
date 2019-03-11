/**
 * Copyright 2015 Goldman Sachs.
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
package com.gs.collections.test;


import com.gs.collections.api.RichIterable;
import com.gs.collections.api.collection.MutableCollection;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public interface UnorderedIterableTestCase extends RichIterableTestCase {
    @Override
    @Test
    default void Iterable_next() {
        Iterable<Integer> iterable = newWith(3, 3, 3, 2, 2, 1);
        MutableCollection<Integer> mutableCollection = this.newMutableForFilter();
        Iterator<Integer> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            Integer integer = iterator.next();
            mutableCollection.add(integer);
        } 
        IterableTestCase.assertEquals(this.getExpectedFiltered(3, 3, 3, 2, 2, 1), mutableCollection);
        Assert.assertFalse(iterator.hasNext());
    }

    @Override
    @Test
    default void RichIterable_getFirst() {
        RichIterable<Integer> integers = this.newWith(3, 2, 1);
        Integer first = integers.getFirst();
        Assert.assertThat(first, Matchers.isOneOf(3, 2, 1));
        IterableTestCase.assertEquals(integers.iterator().next(), first);
        Assert.assertNotEquals(integers.getLast(), first);
    }

    @Override
    @Test
    default void RichIterable_getLast() {
        RichIterable<Integer> integers = this.newWith(3, 2, 1);
        Integer last = integers.getLast();
        Assert.assertThat(last, Matchers.isOneOf(3, 2, 1));
        Iterator<Integer> iterator = integers.iterator();
        Integer iteratorLast = null;
        while (iterator.hasNext()) {
            iteratorLast = iterator.next();
        } 
        IterableTestCase.assertEquals(iteratorLast, last);
        Assert.assertNotEquals(integers.getFirst(), last);
    }

    @Override
    @Test
    default void RichIterable_detect() {
        Assert.assertThat(this.newWith(3, 2, 1).detect(Predicates.greaterThan(0)), Matchers.isOneOf(3, 2, 1));
        Assert.assertThat(this.newWith(3, 2, 1).detect(Predicates.greaterThan(1)), Matchers.isOneOf(3, 2));
        Assert.assertThat(this.newWith(3, 2, 1).detect(Predicates.greaterThan(2)), Matchers.is(3));
        Assert.assertThat(this.newWith(3, 2, 1).detect(Predicates.greaterThan(3)), Matchers.nullValue());
        Assert.assertThat(this.newWith(3, 2, 1).detect(Predicates.lessThan(1)), Matchers.nullValue());
        Assert.assertThat(this.newWith(3, 2, 1).detect(Predicates.lessThan(2)), Matchers.is(1));
        Assert.assertThat(this.newWith(3, 2, 1).detect(Predicates.lessThan(3)), Matchers.isOneOf(2, 1));
        Assert.assertThat(this.newWith(3, 2, 1).detect(Predicates.lessThan(4)), Matchers.isOneOf(3, 2, 1));
        Assert.assertThat(this.newWith(3, 2, 1).detectWith(Predicates2.greaterThan(), 0), Matchers.isOneOf(3, 2, 1));
        Assert.assertThat(this.newWith(3, 2, 1).detectWith(Predicates2.greaterThan(), 1), Matchers.isOneOf(3, 2));
        Assert.assertThat(this.newWith(3, 2, 1).detectWith(Predicates2.greaterThan(), 2), Matchers.is(3));
        Assert.assertThat(this.newWith(3, 2, 1).detectWith(Predicates2.greaterThan(), 3), Matchers.nullValue());
        Assert.assertThat(this.newWith(3, 2, 1).detectWith(Predicates2.lessThan(), 1), Matchers.nullValue());
        Assert.assertThat(this.newWith(3, 2, 1).detectWith(Predicates2.lessThan(), 2), Matchers.is(1));
        Assert.assertThat(this.newWith(3, 2, 1).detectWith(Predicates2.lessThan(), 3), Matchers.isOneOf(2, 1));
        Assert.assertThat(this.newWith(3, 2, 1).detectWith(Predicates2.lessThan(), 4), Matchers.isOneOf(3, 2, 1));
        Assert.assertThat(this.newWith(3, 2, 1).detectIfNone(Predicates.greaterThan(0), () -> 4), Matchers.isOneOf(3, 2, 1));
        Assert.assertThat(this.newWith(3, 2, 1).detectIfNone(Predicates.greaterThan(1), () -> 4), Matchers.isOneOf(3, 2));
        Assert.assertThat(this.newWith(3, 2, 1).detectIfNone(Predicates.greaterThan(2), () -> 4), Matchers.is(3));
        Assert.assertThat(this.newWith(3, 2, 1).detectIfNone(Predicates.greaterThan(3), () -> 4), Matchers.is(4));
        Assert.assertThat(this.newWith(3, 2, 1).detectIfNone(Predicates.lessThan(1), () -> 4), Matchers.is(4));
        Assert.assertThat(this.newWith(3, 2, 1).detectIfNone(Predicates.lessThan(2), () -> 4), Matchers.is(1));
        Assert.assertThat(this.newWith(3, 2, 1).detectIfNone(Predicates.lessThan(3), () -> 4), Matchers.isOneOf(2, 1));
        Assert.assertThat(this.newWith(3, 2, 1).detectIfNone(Predicates.lessThan(4), () -> 4), Matchers.isOneOf(3, 2, 1));
        Assert.assertThat(this.newWith(3, 2, 1).detectWithIfNone(Predicates2.greaterThan(), 0, () -> 4), Matchers.isOneOf(3, 2, 1));
        Assert.assertThat(this.newWith(3, 2, 1).detectWithIfNone(Predicates2.greaterThan(), 1, () -> 4), Matchers.isOneOf(3, 2));
        Assert.assertThat(this.newWith(3, 2, 1).detectWithIfNone(Predicates2.greaterThan(), 2, () -> 4), Matchers.is(3));
        Assert.assertThat(this.newWith(3, 2, 1).detectWithIfNone(Predicates2.greaterThan(), 3, () -> 4), Matchers.is(4));
        Assert.assertThat(this.newWith(3, 2, 1).detectWithIfNone(Predicates2.lessThan(), 1, () -> 4), Matchers.is(4));
        Assert.assertThat(this.newWith(3, 2, 1).detectWithIfNone(Predicates2.lessThan(), 2, () -> 4), Matchers.is(1));
        Assert.assertThat(this.newWith(3, 2, 1).detectWithIfNone(Predicates2.lessThan(), 3, () -> 4), Matchers.isOneOf(2, 1));
        Assert.assertThat(this.newWith(3, 2, 1).detectWithIfNone(Predicates2.lessThan(), 4, () -> 4), Matchers.isOneOf(3, 2, 1));
    }

    @Override
    @Test
    default void RichIterable_minBy_maxBy() {
        // Without an ordering, min can be either ca or da
        RichIterable<String> minIterable = this.newWith("ed", "da", "ca", "bc", "ab");
        String actualMin = minIterable.minBy(( string) -> string.charAt(((string.length()) - 1)));
        Assert.assertThat(actualMin, Matchers.isOneOf("ca", "da"));
        IterableTestCase.assertEquals(minIterable.detect(( each) -> (each.equals("ca")) || (each.equals("da"))), actualMin);
        assertThrows(NoSuchElementException.class, () -> this.<String>newWith().minBy(( string) -> string.charAt(((string.length()) - 1))));
        // Without an ordering, max can be either ca or da
        RichIterable<String> maxIterable = this.newWith("ew", "dz", "cz", "bx", "ay");
        String actualMax = maxIterable.maxBy(( string) -> string.charAt(((string.length()) - 1)));
        Assert.assertThat(actualMax, Matchers.isOneOf("cz", "dz"));
        IterableTestCase.assertEquals(maxIterable.detect(( each) -> (each.equals("cz")) || (each.equals("dz"))), actualMax);
        assertThrows(NoSuchElementException.class, () -> this.<String>newWith().maxBy(( string) -> string.charAt(((string.length()) - 1))));
    }
}

