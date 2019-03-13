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
package com.gs.collections.impl.set.mutable;


import com.gs.collections.api.LazyIterable;
import com.gs.collections.api.RichIterable;
import com.gs.collections.api.bag.sorted.MutableSortedBag;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.collection.MutableCollection;
import com.gs.collections.api.map.MapIterable;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.set.UnsortedSetIterable;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.bag.sorted.mutable.TreeBag;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.test.Verify;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;


public class MultiReaderUnifiedSetTest extends MultiReaderMutableCollectionTestCase {
    @Override
    @Test
    public void asSynchronized() {
        Verify.assertInstanceOf(SynchronizedMutableSet.class, MultiReaderUnifiedSet.newSet().asSynchronized());
    }

    @Override
    @Test
    public void select() {
        super.select();
        Verify.assertContainsAll(MultiReaderUnifiedSet.newSetWith(1, 2, 3, 4, 5).select(Predicates.lessThan(3)), 1, 2);
        Verify.assertContainsAll(MultiReaderUnifiedSet.newSetWith((-1), 2, 3, 4, 5).select(Predicates.lessThan(3), com.gs.collections.impl.list.mutable.FastList.<Integer>newList()), (-1), 2);
    }

    @Override
    @Test
    public void reject() {
        super.reject();
        Verify.assertContainsAll(MultiReaderUnifiedSet.newSetWith(1, 2, 3, 4).reject(Predicates.lessThan(3)), 3, 4);
        Verify.assertContainsAll(MultiReaderUnifiedSet.newSetWith(1, 2, 3, 4).reject(Predicates.lessThan(3), com.gs.collections.impl.list.mutable.FastList.<Integer>newList()), 3, 4);
    }

    @Override
    @Test
    public void getFirst() {
        Assert.assertNotNull(this.newWith(1, 2, 3).getFirst());
        Assert.assertNull(this.newWith().getFirst());
    }

    @Override
    @Test
    public void getLast() {
        Assert.assertNotNull(this.newWith(1, 2, 3).getLast());
        Assert.assertNull(this.newWith().getLast());
    }

    @Override
    @Test
    public void iterator() {
        Verify.assertThrows(UnsupportedOperationException.class, () -> MultiReaderUnifiedSet.newSet().iterator());
    }

    @Test
    public void unifiedSetToString() {
        MutableSet<Integer> set = MultiReaderUnifiedSet.newSetWith(1, 2);
        String s = set.toString();
        Assert.assertTrue((("[1, 2]".equals(s)) || ("[2, 1]".equals(s))));
    }

    @Override
    @Test
    public void isEmpty() {
        MultiReaderUnifiedSet<String> set = MultiReaderUnifiedSet.newSet();
        this.assertIsEmpty(true, set);
        set.add("stuff");
        this.assertIsEmpty(false, set);
        set.remove("stuff");
        this.assertIsEmpty(true, set);
        set.add("Bon");
        set.add("Jovi");
        this.assertIsEmpty(false, set);
        set.remove("Jovi");
        this.assertIsEmpty(false, set);
        set.clear();
        this.assertIsEmpty(true, set);
    }

    @Override
    @Test
    public void testToString() {
        MutableCollection<Object> collection = this.newWith(1, 2);
        Assert.assertTrue((("[1, 2]".equals(collection.toString())) || ("[2, 1]".equals(collection.toString()))));
    }

    @Override
    @Test
    public void makeString() {
        MutableCollection<Object> collection = this.newWith(1, 2, 3);
        Assert.assertEquals(collection.toString(), (('[' + (collection.makeString())) + ']'));
    }

    @Override
    @Test
    public void appendString() {
        MutableCollection<Object> collection = this.newWith(1, 2, 3);
        Appendable builder = new StringBuilder();
        collection.appendString(builder);
        Assert.assertEquals(collection.toString(), (('[' + (builder.toString())) + ']'));
    }

    @Override
    @Test
    public void asUnmodifiable() {
        Verify.assertInstanceOf(UnmodifiableMutableSet.class, this.newWith().asUnmodifiable());
    }

    @Test
    public void union() {
        MutableSet<String> set = MultiReaderUnifiedSet.newSetWith("1", "2", "3", "4");
        MutableSet<String> union = set.union(UnifiedSet.newSetWith("a", "b", "c", "1"));
        Verify.assertSize(((set.size()) + 3), union);
        Assert.assertTrue(union.containsAllIterable(Interval.oneTo(set.size()).collect(String::valueOf)));
        Verify.assertContainsAll(union, "a", "b", "c");
        Assert.assertEquals(set, set.union(UnifiedSet.newSetWith("1")));
    }

    @Test
    public void unionInto() {
        MutableSet<String> set = MultiReaderUnifiedSet.newSetWith("1", "2", "3", "4");
        MutableSet<String> union = set.unionInto(UnifiedSet.newSetWith("a", "b", "c", "1"), UnifiedSet.<String>newSet());
        Verify.assertSize(((set.size()) + 3), union);
        Assert.assertTrue(union.containsAllIterable(Interval.oneTo(set.size()).collect(String::valueOf)));
        Verify.assertContainsAll(union, "a", "b", "c");
        Assert.assertEquals(set, set.unionInto(UnifiedSet.newSetWith("1"), UnifiedSet.<String>newSet()));
    }

    @Test
    public void intersect() {
        MutableSet<String> set = MultiReaderUnifiedSet.newSetWith("1", "2", "3", "4");
        MutableSet<String> intersect = set.intersect(UnifiedSet.newSetWith("a", "b", "c", "1"));
        Verify.assertSize(1, intersect);
        Assert.assertEquals(UnifiedSet.newSetWith("1"), intersect);
        Verify.assertEmpty(set.intersect(UnifiedSet.newSetWith("not present")));
    }

    @Test
    public void intersectInto() {
        MutableSet<String> set = MultiReaderUnifiedSet.newSetWith("1", "2", "3", "4");
        MutableSet<String> intersect = set.intersectInto(UnifiedSet.newSetWith("a", "b", "c", "1"), UnifiedSet.<String>newSet());
        Verify.assertSize(1, intersect);
        Assert.assertEquals(UnifiedSet.newSetWith("1"), intersect);
        Verify.assertEmpty(set.intersectInto(UnifiedSet.newSetWith("not present"), UnifiedSet.<String>newSet()));
    }

    @Test
    public void difference() {
        MutableSet<String> set = MultiReaderUnifiedSet.newSetWith("1", "2", "3", "4");
        MutableSet<String> difference = set.difference(UnifiedSet.newSetWith("2", "3", "4", "not present"));
        Assert.assertEquals(UnifiedSet.newSetWith("1"), difference);
        Assert.assertEquals(set, set.difference(UnifiedSet.newSetWith("not present")));
    }

    @Test
    public void differenceInto() {
        MutableSet<String> set = MultiReaderUnifiedSet.newSetWith("1", "2", "3", "4");
        MutableSet<String> difference = set.differenceInto(UnifiedSet.newSetWith("2", "3", "4", "not present"), UnifiedSet.<String>newSet());
        Assert.assertEquals(UnifiedSet.newSetWith("1"), difference);
        Assert.assertEquals(set, set.differenceInto(UnifiedSet.newSetWith("not present"), UnifiedSet.<String>newSet()));
    }

    @Test
    public void symmetricDifference() {
        MutableSet<String> set = MultiReaderUnifiedSet.newSetWith("1", "2", "3", "4");
        MutableSet<String> difference = set.symmetricDifference(UnifiedSet.newSetWith("2", "3", "4", "5", "not present"));
        Verify.assertContains("1", difference);
        Assert.assertTrue(difference.containsAllIterable(Interval.fromTo(((set.size()) + 1), 5).collect(String::valueOf)));
        for (int i = 2; i <= (set.size()); i++) {
            Verify.assertNotContains(String.valueOf(i), difference);
        }
        Verify.assertSize(((set.size()) + 1), set.symmetricDifference(UnifiedSet.newSetWith("not present")));
    }

    @Test
    public void symmetricDifferenceInto() {
        MutableSet<String> set = MultiReaderUnifiedSet.newSetWith("1", "2", "3", "4");
        MutableSet<String> difference = set.symmetricDifferenceInto(UnifiedSet.newSetWith("2", "3", "4", "5", "not present"), UnifiedSet.<String>newSet());
        Verify.assertContains("1", difference);
        Assert.assertTrue(difference.containsAllIterable(Interval.fromTo(((set.size()) + 1), 5).collect(String::valueOf)));
        for (int i = 2; i <= (set.size()); i++) {
            Verify.assertNotContains(String.valueOf(i), difference);
        }
        Verify.assertSize(((set.size()) + 1), set.symmetricDifferenceInto(UnifiedSet.newSetWith("not present"), UnifiedSet.<String>newSet()));
    }

    @Test
    public void isSubsetOf() {
        MutableSet<String> set = MultiReaderUnifiedSet.newSetWith("1", "2", "3", "4");
        Assert.assertTrue(set.isSubsetOf(UnifiedSet.newSetWith("1", "2", "3", "4", "5")));
    }

    @Test
    public void isProperSubsetOf() {
        MutableSet<String> set = MultiReaderUnifiedSet.newSetWith("1", "2", "3", "4");
        Assert.assertTrue(set.isProperSubsetOf(UnifiedSet.newSetWith("1", "2", "3", "4", "5")));
        Assert.assertFalse(set.isProperSubsetOf(set));
    }

    @Test
    public void powerSet() {
        MutableSet<String> set = MultiReaderUnifiedSet.newSetWith("1", "2", "3", "4");
        MutableSet<UnsortedSetIterable<String>> powerSet = set.powerSet();
        Verify.assertSize(((int) (StrictMath.pow(2, set.size()))), powerSet);
        Verify.assertContains(UnifiedSet.<String>newSet(), powerSet);
        Verify.assertContains(set, powerSet);
    }

    @Test
    public void cartesianProduct() {
        MutableSet<String> set = MultiReaderUnifiedSet.newSetWith("1", "2", "3", "4");
        LazyIterable<Pair<String, String>> cartesianProduct = set.cartesianProduct(UnifiedSet.newSetWith("One", "Two"));
        Verify.assertIterableSize(((set.size()) * 2), cartesianProduct);
        Assert.assertEquals(set, cartesianProduct.select(Predicates.attributeEqual(((Function<Pair<?, String>, String>) (Pair::getTwo)), "One")).collect(((Function<Pair<String, ?>, String>) (Pair::getOne))).toSet());
    }

    @Override
    @Test
    public void aggregateByMutating() {
        Function0<AtomicInteger> valueCreator = AtomicInteger::new;
        MutableCollection<Integer> collection = this.newWith(1, 1, 1, 2, 2, 3);
        MapIterable<String, AtomicInteger> aggregation = collection.aggregateInPlaceBy(String::valueOf, valueCreator, AtomicInteger::addAndGet);
        Assert.assertEquals(1, aggregation.get("1").intValue());
        Assert.assertEquals(2, aggregation.get("2").intValue());
        Assert.assertEquals(3, aggregation.get("3").intValue());
    }

    @Override
    @Test
    public void aggregateByNonMutating() {
        Function0<Integer> valueCreator = () -> 0;
        Function2<Integer, Integer, Integer> sumAggregator = ( integer1, integer2) -> integer1 + integer2;
        MutableCollection<Integer> collection = this.newWith(1, 1, 1, 2, 2, 3);
        MapIterable<String, Integer> aggregation = collection.aggregateBy(String::valueOf, valueCreator, sumAggregator);
        Assert.assertEquals(1, aggregation.get("1").intValue());
        Assert.assertEquals(2, aggregation.get("2").intValue());
        Assert.assertEquals(3, aggregation.get("3").intValue());
    }

    @Test
    public void withReadLockAndDelegate() {
        MultiReaderUnifiedSet<Integer> set = MultiReaderUnifiedSet.newSetWith(1);
        Object[] result = new Object[1];
        set.withReadLockAndDelegate(( delegate) -> {
            result[0] = delegate.getFirst();
            this.verifyDelegateIsUnmodifiable(delegate);
        });
        Assert.assertNotNull(result[0]);
    }

    @Test
    public void withWriteLockAndDelegate() {
        MultiReaderUnifiedSet<Integer> set = MultiReaderUnifiedSet.newSetWith(2);
        AtomicReference<MutableSet<?>> delegateList = new AtomicReference<>();
        AtomicReference<Iterator<?>> iterator = new AtomicReference<>();
        set.withWriteLockAndDelegate(( delegate) -> {
            delegate.add(1);
            delegate.add(2);
            delegate.add(3);
            delegate.add(4);
            delegateList.set(delegate);
            iterator.set(delegate.iterator());
        });
        Assert.assertEquals(UnifiedSet.newSetWith(1, 2, 3, 4), set);
        Verify.assertThrows(NullPointerException.class, () -> iterator.get().hasNext());
        Verify.assertThrows(NullPointerException.class, () -> delegateList.get().iterator());
    }

    @Override
    @Test
    public void toSortedBag_natural_ordering() {
        RichIterable<Integer> integers = this.newWith(1, 2, 5, 3, 4);
        MutableSortedBag<Integer> bag = integers.toSortedBag();
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(1, 2, 3, 4, 5), bag);
    }

    @Override
    @Test
    public void toSortedBag_with_comparator() {
        RichIterable<Integer> integers = this.newWith(2, 4, 1, 3);
        MutableSortedBag<Integer> bag = integers.toSortedBag(Collections.<Integer>reverseOrder());
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Collections.<Integer>reverseOrder(), 4, 3, 2, 1), bag);
    }

    @Override
    @Test(expected = NullPointerException.class)
    public void toSortedBag_with_null() {
        this.newWith(3, 4, null, 1, 2).toSortedBag();
    }

    @Override
    @Test
    public void toSortedBagBy() {
        RichIterable<Integer> integers = this.newWith(2, 4, 1, 3);
        MutableSortedBag<Integer> bag = integers.toSortedBagBy(String::valueOf);
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(1, 2, 3, 4), bag);
    }
}

