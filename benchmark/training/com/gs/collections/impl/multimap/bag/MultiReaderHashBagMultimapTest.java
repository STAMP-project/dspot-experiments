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
package com.gs.collections.impl.multimap.bag;


import com.gs.collections.api.bag.MutableBag;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.bag.mutable.HashBag;
import com.gs.collections.impl.tuple.Tuples;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test of {@link MultiReaderHashBagMultimap}.
 */
public class MultiReaderHashBagMultimapTest extends AbstractMutableBagMultimapTestCase {
    @Test
    public void pairIterableConstructorTest() {
        Pair<Integer, String> pair1 = Tuples.pair(Integer.valueOf(1), "One");
        Pair<Integer, String> pair2 = Tuples.pair(Integer.valueOf(2), "Two");
        Pair<Integer, String> pair3 = Tuples.pair(Integer.valueOf(3), "Three");
        Pair<Integer, String> pair4 = Tuples.pair(Integer.valueOf(4), "Four");
        Pair<Integer, String> pair11 = Tuples.pair(Integer.valueOf(1), "OneOne");
        Pair<Integer, String> pair22 = Tuples.pair(Integer.valueOf(2), "TwoTwo");
        Pair<Integer, String> pair33 = Tuples.pair(Integer.valueOf(3), "ThreeThree");
        Pair<Integer, String> pair44 = Tuples.pair(Integer.valueOf(4), "FourFour");
        Pair<Integer, String> pair111 = Tuples.pair(Integer.valueOf(1), "One");
        Pair<Integer, String> pair222 = Tuples.pair(Integer.valueOf(2), "Two");
        Pair<Integer, String> pair333 = Tuples.pair(Integer.valueOf(3), "Three");
        Pair<Integer, String> pair444 = Tuples.pair(Integer.valueOf(4), "Four");
        MutableBag<Pair<Integer, String>> testBag = HashBag.<Pair<Integer, String>>newBagWith(pair1, pair2, pair3, pair4, pair11, pair22, pair33, pair44, pair111, pair222, pair333, pair444);
        MultiReaderHashBagMultimap<Integer, String> actual = MultiReaderHashBagMultimap.newMultimap(testBag);
        Assert.assertEquals(HashBag.newBagWith(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4)), actual.keysView().toBag());
        Assert.assertEquals(HashBag.newBagWith("One", "OneOne", "One"), actual.get(Integer.valueOf(1)));
        Assert.assertEquals(HashBag.newBagWith("Two", "TwoTwo", "Two"), actual.get(Integer.valueOf(2)));
        Assert.assertEquals(HashBag.newBagWith("Three", "ThreeThree", "Three"), actual.get(Integer.valueOf(3)));
        Assert.assertEquals(HashBag.newBagWith("Four", "FourFour", "Four"), actual.get(Integer.valueOf(4)));
    }
}

