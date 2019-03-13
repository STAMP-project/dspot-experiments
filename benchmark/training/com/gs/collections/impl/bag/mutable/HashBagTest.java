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
package com.gs.collections.impl.bag.mutable;


import Bags.mutable;
import com.gs.collections.api.bag.MutableBag;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.test.Verify;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


public class HashBagTest extends MutableBagTestCase {
    @Test
    public void newBagWith() {
        HashBag<String> bag = new HashBag<String>().with("apple", "apple");
        MutableBagTestCase.assertBagsEqual(HashBag.newBagWith("apple", "apple"), bag);
        bag.with("hope", "hope", "hope");
        MutableBagTestCase.assertBagsEqual(HashBag.newBagWith("apple", "apple", "hope", "hope", "hope"), bag);
        bag.withAll(Collections.nCopies(5, "ubermench"));
        Assert.assertEquals(UnifiedMap.newWithKeysValues("apple", 2, "hope", 3, "ubermench", 5), bag.toMapOfItemToCount());
    }

    @Override
    @Test
    public void addAll() {
        super.addAll();
        MutableBag<Integer> bag1 = this.newWith();
        Assert.assertTrue(bag1.addAll(this.newWith(1, 1, 2, 3)));
        Verify.assertContainsAll(bag1, 1, 2, 3);
        Assert.assertTrue(bag1.addAll(this.newWith(1, 2, 3)));
        Verify.assertSize(7, bag1);
        Assert.assertFalse(bag1.addAll(this.newWith()));
        Verify.assertContainsAll(bag1, 1, 2, 3);
        MutableBag<Integer> bag2 = this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        bag2.addAll(this.newWith(5, 5, 5, 5, 5));
        Verify.assertBagsEqual(this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 5), bag2);
        MutableBag<Integer> bag3 = this.newWith(1, 2, 2, 3, 3, 3);
        bag3.addAll(this.newWith(1));
        Verify.assertBagsEqual(this.newWith(1, 1, 2, 2, 3, 3, 3), bag3);
    }

    @Override
    @Test
    public void removeAll() {
        super.removeAll();
        MutableBag<Integer> bag1 = this.newWith(1, 2, 3);
        Assert.assertTrue(bag1.removeAll(this.newWith(1, 2, 4)));
        Assert.assertEquals(mutable.of(3), bag1);
        MutableBag<Integer> bag2 = this.newWith(1, 1, 1, 2, 2, 3, 4);
        Verify.assertSize(7, bag2);
        Assert.assertTrue(bag2.removeAll(this.newWith(1, 2, 2, 4)));
        Verify.assertSize(1, bag2);
        Assert.assertEquals(mutable.of(3), bag2);
        MutableBag<Integer> bag3 = this.newWith(1, 2, 3);
        Assert.assertFalse(bag3.removeAll(this.newWith(4, 5)));
        Assert.assertEquals(mutable.of(1, 2, 3), bag3);
    }

    @Test
    public void newBagFromIterable() {
        MutableBagTestCase.assertBagsEqual(HashBag.newBagWith(1, 2, 2, 3, 3, 3), HashBag.newBag(FastList.newListWith(1, 2, 2, 3, 3, 3)));
    }

    @Test
    public void newBagFromBag() {
        Assert.assertEquals(HashBag.newBagWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4), HashBag.newBag(HashBag.newBagWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4)));
    }
}

