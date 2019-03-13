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
package com.gs.collections.test.bag;


import com.gs.collections.api.bag.Bag;
import com.gs.collections.test.IterableTestCase;
import com.gs.collections.test.RichIterableWithDuplicatesTestCase;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public interface BagTestCase extends RichIterableWithDuplicatesTestCase {
    @Test
    default void Bag_sizeDistinct() {
        Bag<Integer> bag = newWith(3, 3, 3, 2, 2, 1);
        IterableTestCase.assertEquals(3, bag.sizeDistinct());
    }

    @Test
    default void Bag_occurrencesOf() {
        Bag<Integer> bag = newWith(3, 3, 3, 2, 2, 1);
        IterableTestCase.assertEquals(0, bag.occurrencesOf(0));
        IterableTestCase.assertEquals(1, bag.occurrencesOf(1));
        IterableTestCase.assertEquals(2, bag.occurrencesOf(2));
        IterableTestCase.assertEquals(3, bag.occurrencesOf(3));
    }

    @Test
    default void Bag_toStringOfItemToCount() {
        IterableTestCase.assertEquals("{}", this.newWith().toStringOfItemToCount());
        Assert.assertThat(this.newWith(2, 2, 1).toStringOfItemToCount(), Matchers.isOneOf("{1=1, 2=2}", "{2=2, 1=1}"));
    }
}

