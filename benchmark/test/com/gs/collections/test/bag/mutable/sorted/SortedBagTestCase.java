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
package com.gs.collections.test.bag.mutable.sorted;


import Lists.immutable;
import Lists.mutable;
import com.gs.collections.api.bag.sorted.SortedBag;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.test.IterableTestCase;
import com.gs.collections.test.SortedIterableTestCase;
import com.gs.collections.test.bag.BagTestCase;
import com.gs.collections.test.domain.A;
import com.gs.collections.test.domain.B;
import com.gs.collections.test.domain.C;
import com.gs.collections.test.list.TransformsToListTrait;
import org.junit.Test;


// TODO linked bag
public interface SortedBagTestCase extends SortedIterableTestCase , BagTestCase , TransformsToListTrait {
    @Override
    @Test
    default void RichIterable_selectInstancesOf() {
        // Must test with two classes that are mutually Comparable
        SortedBag<A> numbers = this.<A>newWith(new C(4.0), new C(4.0), new C(4.0), new C(4.0), new B(3), new B(3), new B(3), new C(2.0), new C(2.0), new B(1));
        IterableTestCase.assertEquals(this.<B>getExpectedFiltered(new B(3), new B(3), new B(3), new B(1)), numbers.selectInstancesOf(B.class));
        IterableTestCase.assertEquals(this.getExpectedFiltered(new C(4.0), new C(4.0), new C(4.0), new C(4.0), new B(3), new B(3), new B(3), new C(2.0), new C(2.0), new B(1)), numbers.selectInstancesOf(A.class));
    }

    @Override
    @Test
    default void Bag_sizeDistinct() {
        SortedBag<Integer> bag = newWith(3, 3, 3, 2, 2, 1);
        IterableTestCase.assertEquals(3, bag.sizeDistinct());
    }

    @Override
    @Test
    default void Bag_occurrencesOf() {
        SortedBag<Integer> bag = newWith(3, 3, 3, 2, 2, 1);
        IterableTestCase.assertEquals(0, bag.occurrencesOf(0));
        IterableTestCase.assertEquals(1, bag.occurrencesOf(1));
        IterableTestCase.assertEquals(2, bag.occurrencesOf(2));
        IterableTestCase.assertEquals(3, bag.occurrencesOf(3));
    }

    @Override
    @Test
    default void Bag_toStringOfItemToCount() {
        IterableTestCase.assertEquals("{}", this.newWith().toStringOfItemToCount());
        IterableTestCase.assertEquals("{3=3, 2=2, 1=1}", this.newWith(3, 3, 3, 2, 2, 1).toStringOfItemToCount());
    }

    @Test
    default void SortedBag_forEachWith() {
        SortedBag<Integer> bag = newWith(3, 3, 3, 2, 2, 1);
        MutableList<Integer> result = mutable.with();
        bag.forEachWith(( argument1, argument2) -> {
            result.add(argument1);
            result.add(argument2);
        }, 0);
        IterableTestCase.assertEquals(immutable.with(3, 0, 3, 0, 3, 0, 2, 0, 2, 0, 1, 0), result);
    }
}

