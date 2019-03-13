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
package com.gs.collections.impl.multimap.list;


import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.tuple.Tuples;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test of {@link MultiReaderFastListMultimap}.
 */
public class MultiReaderFastListMultimapTest extends AbstractMutableListMultimapTestCase {
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
        MutableList<Pair<Integer, String>> testList = FastList.<Pair<Integer, String>>newListWith(pair1, pair2, pair3, pair4, pair11, pair22, pair33, pair44, pair111, pair222, pair333, pair444);
        MultiReaderFastListMultimap<Integer, String> actual = MultiReaderFastListMultimap.newMultimap(testList);
        Assert.assertEquals(FastList.newListWith(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4)), actual.keysView().toList());
        Assert.assertEquals(FastList.newListWith("One", "OneOne", "One"), actual.get(Integer.valueOf(1)).toList());
        Assert.assertEquals(FastList.newListWith("Two", "TwoTwo", "Two"), actual.get(Integer.valueOf(2)).toList());
        Assert.assertEquals(FastList.newListWith("Three", "ThreeThree", "Three"), actual.get(Integer.valueOf(3)).toList());
        Assert.assertEquals(FastList.newListWith("Four", "FourFour", "Four"), actual.get(Integer.valueOf(4)).toList());
    }
}

