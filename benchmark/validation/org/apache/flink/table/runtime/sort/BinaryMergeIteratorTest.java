/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.table.runtime.sort;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.flink.api.common.typeutils.TypeComparator;
import org.apache.flink.api.common.typeutils.base.IntComparator;
import org.apache.flink.table.dataformat.BinaryRow;
import org.apache.flink.table.typeutils.BinaryRowSerializer;
import org.apache.flink.util.MutableObjectIterator;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test of {@link BinaryMergeIterator}.
 */
public class BinaryMergeIteratorTest {
    private RecordComparator comparator;

    private BinaryRowSerializer serializer;

    @Test
    public void testOneStream() throws Exception {
        List<MutableObjectIterator<BinaryRow>> iterators = new ArrayList<>();
        iterators.add(newIterator(new int[]{ 1, 2, 4, 5, 10 }, new String[]{ "1", "2", "4", "5", "10" }));
        final int[] expected = new int[]{ 1, 2, 4, 5, 10 };
        MutableObjectIterator<BinaryRow> iterator = new BinaryMergeIterator(iterators, Collections.singletonList(serializer.createInstance()), ( o1, o2) -> this.comparator.compare(o1, o2));
        BinaryRow row = serializer.createInstance();
        int pos = 0;
        while ((row = iterator.next(row)) != null) {
            Assert.assertEquals(expected[(pos++)], row.getInt(0));
        } 
    }

    @Test
    public void testMergeOfTwoStreams() throws Exception {
        List<MutableObjectIterator<BinaryRow>> iterators = new ArrayList<>();
        iterators.add(newIterator(new int[]{ 1, 2, 4, 5, 10 }, new String[]{ "1", "2", "4", "5", "10" }));
        iterators.add(newIterator(new int[]{ 3, 6, 7, 10, 12 }, new String[]{ "3", "6", "7", "10", "12" }));
        final int[] expected = new int[]{ 1, 2, 3, 4, 5, 6, 7, 10, 10, 12 };
        MutableObjectIterator<BinaryRow> iterator = new BinaryMergeIterator(iterators, reused(2), ( o1, o2) -> this.comparator.compare(o1, o2));
        BinaryRow row = serializer.createInstance();
        int pos = 0;
        while ((row = iterator.next(row)) != null) {
            Assert.assertEquals(expected[(pos++)], row.getInt(0));
        } 
    }

    @Test
    public void testMergeOfTenStreams() throws Exception {
        List<MutableObjectIterator<BinaryRow>> iterators = new ArrayList<>();
        iterators.add(newIterator(new int[]{ 1, 2, 17, 23, 23 }, new String[]{ "A", "B", "C", "D", "E" }));
        iterators.add(newIterator(new int[]{ 2, 6, 7, 8, 9 }, new String[]{ "A", "B", "C", "D", "E" }));
        iterators.add(newIterator(new int[]{ 4, 10, 11, 11, 12 }, new String[]{ "A", "B", "C", "D", "E" }));
        iterators.add(newIterator(new int[]{ 3, 6, 7, 10, 12 }, new String[]{ "A", "B", "C", "D", "E" }));
        iterators.add(newIterator(new int[]{ 7, 10, 15, 19, 44 }, new String[]{ "A", "B", "C", "D", "E" }));
        iterators.add(newIterator(new int[]{ 6, 6, 11, 17, 18 }, new String[]{ "A", "B", "C", "D", "E" }));
        iterators.add(newIterator(new int[]{ 1, 2, 4, 5, 10 }, new String[]{ "A", "B", "C", "D", "E" }));
        iterators.add(newIterator(new int[]{ 5, 10, 19, 23, 29 }, new String[]{ "A", "B", "C", "D", "E" }));
        iterators.add(newIterator(new int[]{ 9, 9, 9, 9, 9 }, new String[]{ "A", "B", "C", "D", "E" }));
        iterators.add(newIterator(new int[]{ 8, 8, 14, 14, 15 }, new String[]{ "A", "B", "C", "D", "E" }));
        TypeComparator<Integer> comparator = new IntComparator(true);
        MutableObjectIterator<BinaryRow> iterator = new BinaryMergeIterator(iterators, reused(10), ( o1, o2) -> this.comparator.compare(o1, o2));
        BinaryRow row = serializer.createInstance();
        int pre = 0;
        while ((row = iterator.next(row)) != null) {
            Assert.assertTrue(((comparator.compare(row.getInt(0), pre)) >= 0));
            pre = row.getInt(0);
        } 
    }
}

