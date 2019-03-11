/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.segment;


import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Test;


public class MergeIntIteratorTest {
    @Test(expected = NoSuchElementException.class)
    public void testNoIterators() {
        IntIterator it = IntIteratorUtils.mergeAscending(Collections.emptyList());
        MergeIntIteratorTest.assertEmpty(it);
    }

    @Test(expected = NoSuchElementException.class)
    public void testMergeEmptyIterators() {
        IntIterator it = IntIteratorUtils.mergeAscending(Arrays.asList(IntIterators.EMPTY_ITERATOR, IntIterators.EMPTY_ITERATOR));
        MergeIntIteratorTest.assertEmpty(it);
    }

    /**
     * Check for some possible corner cases, because {@link IntIteratorUtils.MergeIntIterator} is
     * implemented using packing ints within longs, that is prone to some overflow or sign bit extension bugs
     */
    @Test
    public void testOverflow() {
        List<IntList> lists = Lists.newArrayList(IntLists.singleton(Integer.MIN_VALUE), IntLists.singleton(Integer.MIN_VALUE), IntLists.singleton((-1)), IntLists.singleton(0), IntLists.singleton(Integer.MAX_VALUE));
        for (int i = 0; i < ((lists.size()) + 1); i++) {
            MergeIntIteratorTest.assertAscending(IntIteratorUtils.mergeAscending(MergeIntIteratorTest.iteratorsFromLists(lists)));
            Collections.rotate(lists, 1);
        }
        Collections.shuffle(lists);
        MergeIntIteratorTest.assertAscending(IntIteratorUtils.mergeAscending(MergeIntIteratorTest.iteratorsFromLists(lists)));
    }

    @Test
    public void smokeTest() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        for (int i = 0; i < 1000; i++) {
            int numIterators = r.nextInt(1, 11);
            List<IntList> lists = new ArrayList<>(numIterators);
            for (int j = 0; j < numIterators; j++) {
                lists.add(new IntArrayList());
            }
            for (int j = 0; j < 50; j++) {
                lists.get(r.nextInt(numIterators)).add(j);
            }
            for (int j = 0; j < ((lists.size()) + 1); j++) {
                MergeIntIteratorTest.assertAscending(IntIteratorUtils.mergeAscending(MergeIntIteratorTest.iteratorsFromLists(lists)));
                Collections.rotate(lists, 1);
            }
            for (int j = 0; j < 10; j++) {
                Collections.shuffle(lists);
                MergeIntIteratorTest.assertAscending(IntIteratorUtils.mergeAscending(MergeIntIteratorTest.iteratorsFromLists(lists)));
            }
        }
    }
}

