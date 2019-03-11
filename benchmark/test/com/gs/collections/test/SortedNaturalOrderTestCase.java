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


import Lists.immutable;
import Lists.mutable;
import com.gs.collections.api.RichIterable;
import com.gs.collections.api.ordered.SortedIterable;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.tuple.Tuples;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public interface SortedNaturalOrderTestCase extends OrderedIterableTestCase {
    @Override
    @Test
    default void RichIterable_collect() {
        RichIterable<Integer> iterable = newWith(1, 1, 2, 2, 3, 3, 11, 11, 12, 12, 13, 13);
        IterableTestCase.assertEquals(this.getExpectedTransformed(1, 1, 2, 2, 3, 3, 1, 1, 2, 2, 3, 3), iterable.collect(( i) -> i % 10));
        IterableTestCase.assertEquals(this.newMutableForTransform(1, 1, 2, 2, 3, 3, 1, 1, 2, 2, 3, 3), iterable.collect(( i) -> i % 10, this.newMutableForTransform()));
        IterableTestCase.assertEquals(this.getExpectedTransformed(1, 1, 2, 2, 3, 3, 1, 1, 2, 2, 3, 3), iterable.collectWith(( i, mod) -> i % mod, 10));
        IterableTestCase.assertEquals(this.newMutableForTransform(1, 1, 2, 2, 3, 3, 1, 1, 2, 2, 3, 3), iterable.collectWith(( i, mod) -> i % mod, 10, this.newMutableForTransform()));
    }

    @Override
    @Test
    default void RichIterable_collectIf() {
        RichIterable<Integer> iterable = newWith(1, 1, 2, 2, 3, 3, 11, 11, 12, 12, 13, 13);
        IterableTestCase.assertEquals(this.getExpectedTransformed(1, 1, 3, 3, 1, 1, 3, 3), iterable.collectIf(( i) -> (i % 2) != 0, ( i) -> i % 10));
        IterableTestCase.assertEquals(this.newMutableForTransform(1, 1, 3, 3, 1, 1, 3, 3), iterable.collectIf(( i) -> (i % 2) != 0, ( i) -> i % 10, this.newMutableForTransform()));
    }

    @Override
    @Test
    default void RichIterable_collectPrimitive() {
        IterableTestCase.assertEquals(this.getExpectedBoolean(false, false, true, true, false, false), this.newWith(1, 1, 2, 2, 3, 3).collectBoolean(( each) -> (each % 2) == 0));
        IterableTestCase.assertEquals(this.newBooleanForTransform(false, false, true, true, false, false), this.newWith(1, 1, 2, 2, 3, 3).collectBoolean(( each) -> (each % 2) == 0, this.newBooleanForTransform()));
        RichIterable<Integer> iterable = newWith(1, 1, 2, 2, 3, 3, 11, 11, 12, 12, 13, 13);
        IterableTestCase.assertEquals(this.getExpectedByte(((byte) (1)), ((byte) (1)), ((byte) (2)), ((byte) (2)), ((byte) (3)), ((byte) (3)), ((byte) (1)), ((byte) (1)), ((byte) (2)), ((byte) (2)), ((byte) (3)), ((byte) (3))), iterable.collectByte(( each) -> ((byte) (each % 10))));
        IterableTestCase.assertEquals(this.newByteForTransform(((byte) (1)), ((byte) (1)), ((byte) (2)), ((byte) (2)), ((byte) (3)), ((byte) (3)), ((byte) (1)), ((byte) (1)), ((byte) (2)), ((byte) (2)), ((byte) (3)), ((byte) (3))), iterable.collectByte(( each) -> ((byte) (each % 10)), this.newByteForTransform()));
        IterableTestCase.assertEquals(this.getExpectedChar(((char) (1)), ((char) (1)), ((char) (2)), ((char) (2)), ((char) (3)), ((char) (3)), ((char) (1)), ((char) (1)), ((char) (2)), ((char) (2)), ((char) (3)), ((char) (3))), iterable.collectChar(( each) -> ((char) (each % 10))));
        IterableTestCase.assertEquals(this.newCharForTransform(((char) (1)), ((char) (1)), ((char) (2)), ((char) (2)), ((char) (3)), ((char) (3)), ((char) (1)), ((char) (1)), ((char) (2)), ((char) (2)), ((char) (3)), ((char) (3))), iterable.collectChar(( each) -> ((char) (each % 10)), this.newCharForTransform()));
        IterableTestCase.assertEquals(this.getExpectedDouble(1.0, 1.0, 2.0, 2.0, 3.0, 3.0, 1.0, 1.0, 2.0, 2.0, 3.0, 3.0), iterable.collectDouble(( each) -> ((double) (each % 10))));
        IterableTestCase.assertEquals(this.newDoubleForTransform(1.0, 1.0, 2.0, 2.0, 3.0, 3.0, 1.0, 1.0, 2.0, 2.0, 3.0, 3.0), iterable.collectDouble(( each) -> ((double) (each % 10)), this.newDoubleForTransform()));
        IterableTestCase.assertEquals(this.getExpectedFloat(1.0F, 1.0F, 2.0F, 2.0F, 3.0F, 3.0F, 1.0F, 1.0F, 2.0F, 2.0F, 3.0F, 3.0F), iterable.collectFloat(( each) -> ((float) (each % 10))));
        IterableTestCase.assertEquals(this.newFloatForTransform(1.0F, 1.0F, 2.0F, 2.0F, 3.0F, 3.0F, 1.0F, 1.0F, 2.0F, 2.0F, 3.0F, 3.0F), iterable.collectFloat(( each) -> ((float) (each % 10)), this.newFloatForTransform()));
        IterableTestCase.assertEquals(this.getExpectedInt(1, 1, 2, 2, 3, 3, 1, 1, 2, 2, 3, 3), iterable.collectInt(( each) -> each % 10));
        IterableTestCase.assertEquals(this.newIntForTransform(1, 1, 2, 2, 3, 3, 1, 1, 2, 2, 3, 3), iterable.collectInt(( each) -> each % 10, this.newIntForTransform()));
        IterableTestCase.assertEquals(this.getExpectedLong(1, 1, 2, 2, 3, 3, 1, 1, 2, 2, 3, 3), iterable.collectLong(( each) -> each % 10));
        IterableTestCase.assertEquals(this.newLongForTransform(1, 1, 2, 2, 3, 3, 1, 1, 2, 2, 3, 3), iterable.collectLong(( each) -> each % 10, this.newLongForTransform()));
        IterableTestCase.assertEquals(this.getExpectedShort(((short) (1)), ((short) (1)), ((short) (2)), ((short) (2)), ((short) (3)), ((short) (3)), ((short) (1)), ((short) (1)), ((short) (2)), ((short) (2)), ((short) (3)), ((short) (3))), iterable.collectShort(( each) -> ((short) (each % 10))));
        IterableTestCase.assertEquals(this.newShortForTransform(((short) (1)), ((short) (1)), ((short) (2)), ((short) (2)), ((short) (3)), ((short) (3)), ((short) (1)), ((short) (1)), ((short) (2)), ((short) (2)), ((short) (3)), ((short) (3))), iterable.collectShort(( each) -> ((short) (each % 10)), this.newShortForTransform()));
    }

    @Override
    @Test
    default void RichIterable_flatCollect() {
        IterableTestCase.assertEquals(this.getExpectedTransformed(1, 1, 2, 1, 2, 1, 2, 3), this.newWith(1, 2, 2, 3).flatCollect(Interval::oneTo));
        IterableTestCase.assertEquals(this.newMutableForTransform(1, 1, 2, 1, 2, 1, 2, 3), this.newWith(1, 2, 2, 3).flatCollect(Interval::oneTo, this.newMutableForTransform()));
    }

    @Override
    @Test
    default void RichIterable_detect() {
        RichIterable<Integer> iterable = newWith(1, 2, 3);
        Assert.assertThat(iterable.detect(Predicates.greaterThan(0)), Matchers.is(1));
        Assert.assertThat(iterable.detect(Predicates.greaterThan(1)), Matchers.is(2));
        Assert.assertThat(iterable.detect(Predicates.greaterThan(2)), Matchers.is(3));
        Assert.assertThat(iterable.detect(Predicates.greaterThan(3)), Matchers.nullValue());
        Assert.assertThat(iterable.detect(Predicates.lessThan(1)), Matchers.nullValue());
        Assert.assertThat(iterable.detect(Predicates.lessThan(2)), Matchers.is(1));
        Assert.assertThat(iterable.detect(Predicates.lessThan(3)), Matchers.is(1));
        Assert.assertThat(iterable.detect(Predicates.lessThan(4)), Matchers.is(1));
        Assert.assertThat(iterable.detectWith(Predicates2.greaterThan(), 0), Matchers.is(1));
        Assert.assertThat(iterable.detectWith(Predicates2.greaterThan(), 1), Matchers.is(2));
        Assert.assertThat(iterable.detectWith(Predicates2.greaterThan(), 2), Matchers.is(3));
        Assert.assertThat(iterable.detectWith(Predicates2.greaterThan(), 3), Matchers.nullValue());
        Assert.assertThat(iterable.detectWith(Predicates2.lessThan(), 1), Matchers.nullValue());
        Assert.assertThat(iterable.detectWith(Predicates2.lessThan(), 2), Matchers.is(1));
        Assert.assertThat(iterable.detectWith(Predicates2.lessThan(), 3), Matchers.is(1));
        Assert.assertThat(iterable.detectWith(Predicates2.lessThan(), 4), Matchers.is(1));
        Assert.assertThat(iterable.detectIfNone(Predicates.greaterThan(0), () -> 4), Matchers.is(1));
        Assert.assertThat(iterable.detectIfNone(Predicates.greaterThan(1), () -> 4), Matchers.is(2));
        Assert.assertThat(iterable.detectIfNone(Predicates.greaterThan(2), () -> 4), Matchers.is(3));
        Assert.assertThat(iterable.detectIfNone(Predicates.greaterThan(3), () -> 4), Matchers.is(4));
        Assert.assertThat(iterable.detectIfNone(Predicates.lessThan(1), () -> 4), Matchers.is(4));
        Assert.assertThat(iterable.detectIfNone(Predicates.lessThan(2), () -> 4), Matchers.is(1));
        Assert.assertThat(iterable.detectIfNone(Predicates.lessThan(3), () -> 4), Matchers.is(1));
        Assert.assertThat(iterable.detectIfNone(Predicates.lessThan(4), () -> 4), Matchers.is(1));
        Assert.assertThat(iterable.detectWithIfNone(Predicates2.greaterThan(), 0, () -> 4), Matchers.is(1));
        Assert.assertThat(iterable.detectWithIfNone(Predicates2.greaterThan(), 1, () -> 4), Matchers.is(2));
        Assert.assertThat(iterable.detectWithIfNone(Predicates2.greaterThan(), 2, () -> 4), Matchers.is(3));
        Assert.assertThat(iterable.detectWithIfNone(Predicates2.greaterThan(), 3, () -> 4), Matchers.is(4));
        Assert.assertThat(iterable.detectWithIfNone(Predicates2.lessThan(), 1, () -> 4), Matchers.is(4));
        Assert.assertThat(iterable.detectWithIfNone(Predicates2.lessThan(), 2, () -> 4), Matchers.is(1));
        Assert.assertThat(iterable.detectWithIfNone(Predicates2.lessThan(), 3, () -> 4), Matchers.is(1));
        Assert.assertThat(iterable.detectWithIfNone(Predicates2.lessThan(), 4, () -> 4), Matchers.is(1));
    }

    @Override
    @Test
    default void RichIterable_minBy_maxBy() {
        IterableTestCase.assertEquals("ca", this.newWith("ab", "bc", "ca", "da", "ed").minBy(( string) -> string.charAt(((string.length()) - 1))));
        assertThrows(NoSuchElementException.class, () -> this.<String>newWith().minBy(( string) -> string.charAt(((string.length()) - 1))));
        IterableTestCase.assertEquals("cz", this.newWith("ew", "dz", "cz", "bx", "ay").maxBy(( string) -> string.charAt(((string.length()) - 1))));
        assertThrows(NoSuchElementException.class, () -> this.<String>newWith().maxBy(( string) -> string.charAt(((string.length()) - 1))));
    }

    @Override
    @Test
    default void RichIterable_makeString_appendString() {
        RichIterable<Integer> iterable = newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        IterableTestCase.assertEquals("1, 2, 2, 3, 3, 3, 4, 4, 4, 4", iterable.makeString());
        IterableTestCase.assertEquals("1/2/2/3/3/3/4/4/4/4", iterable.makeString("/"));
        IterableTestCase.assertEquals("[1/2/2/3/3/3/4/4/4/4]", iterable.makeString("[", "/", "]"));
        StringBuilder builder1 = new StringBuilder();
        iterable.appendString(builder1);
        IterableTestCase.assertEquals("1, 2, 2, 3, 3, 3, 4, 4, 4, 4", builder1.toString());
        StringBuilder builder2 = new StringBuilder();
        iterable.appendString(builder2, "/");
        IterableTestCase.assertEquals("1/2/2/3/3/3/4/4/4/4", builder2.toString());
        StringBuilder builder3 = new StringBuilder();
        iterable.appendString(builder3, "[", "/", "]");
        IterableTestCase.assertEquals("[1/2/2/3/3/3/4/4/4/4]", builder3.toString());
    }

    @Override
    @Test
    default void RichIterable_toString() {
        RichIterable<Integer> iterable = newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        IterableTestCase.assertEquals("[1, 2, 2, 3, 3, 3, 4, 4, 4, 4]", iterable.toString());
    }

    @Override
    @Test
    default void RichIterable_toList() {
        IterableTestCase.assertEquals(immutable.with(1, 2, 2, 3, 3, 3, 4, 4, 4, 4), this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4).toList());
    }

    @Override
    @Test
    default void OrderedIterable_getFirst() {
        IterableTestCase.assertEquals(Integer.valueOf(1), this.newWith(1, 2, 2, 3, 3, 3).getFirst());
    }

    @Override
    @Test
    default void OrderedIterable_getLast() {
        IterableTestCase.assertEquals(Integer.valueOf(3), this.newWith(1, 2, 2, 3, 3, 3).getLast());
    }

    @Override
    @Test
    default void OrderedIterable_next() {
        Iterator<Integer> iterator = this.newWith(1, 2, 3).iterator();
        IterableTestCase.assertEquals(Integer.valueOf(1), iterator.next());
        IterableTestCase.assertEquals(Integer.valueOf(2), iterator.next());
        IterableTestCase.assertEquals(Integer.valueOf(3), iterator.next());
    }

    @Override
    @Test
    default void OrderedIterable_zipWithIndex() {
        RichIterable<Integer> iterable = newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        Assert.assertEquals(immutable.with(Tuples.pair(1, 0), Tuples.pair(2, 1), Tuples.pair(2, 2), Tuples.pair(3, 3), Tuples.pair(3, 4), Tuples.pair(3, 5), Tuples.pair(4, 6), Tuples.pair(4, 7), Tuples.pair(4, 8), Tuples.pair(4, 9)), iterable.zipWithIndex().toList());
    }

    @Override
    @Test
    default void OrderedIterable_zipWithIndex_target() {
        RichIterable<Integer> iterable = newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        Assert.assertEquals(immutable.with(Tuples.pair(1, 0), Tuples.pair(2, 1), Tuples.pair(2, 2), Tuples.pair(3, 3), Tuples.pair(3, 4), Tuples.pair(3, 5), Tuples.pair(4, 6), Tuples.pair(4, 7), Tuples.pair(4, 8), Tuples.pair(4, 9)), iterable.zipWithIndex(mutable.empty()));
    }

    @Test
    default void SortedIterable_comparator() {
        SortedIterable<Object> iterable = ((SortedIterable<Object>) (this.newWith()));
        Assert.assertNull(iterable.comparator());
    }
}

