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


import com.google.common.primitives.Longs;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.druid.segment.selector.settable.SettableLongColumnValueSelector;
import org.junit.Test;

import static ColumnValueSelector.EMPTY_ARRAY;


public class MergingRowIteratorTest {
    @Test
    public void testEmpty() {
        MergingRowIteratorTest.testMerge();
    }

    @Test
    public void testOneEmptyIterator() {
        MergingRowIteratorTest.testMerge(Collections.emptyList());
    }

    @Test
    public void testMultipleEmptyIterators() {
        MergingRowIteratorTest.testMerge(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    @Test
    public void testConstantIterator() {
        MergingRowIteratorTest.testMerge(Longs.asList(1, 1, 1));
    }

    @Test
    public void testMultipleConstantIterators() {
        MergingRowIteratorTest.testMerge(Longs.asList(2, 2, 2), Longs.asList(3, 3, 3), Longs.asList(1, 1, 1));
    }

    @Test
    public void testAllPossible5ElementSequences() {
        List<List<Long>> possibleSequences = new ArrayList<>();
        MergingRowIteratorTest.populateSequences(possibleSequences, new ArrayDeque<>(), 1, 6, 5);
        for (int i1 = 0; i1 < (possibleSequences.size()); i1++) {
            for (int i2 = i1; i2 < (possibleSequences.size()); i2++) {
                for (int i3 = i2; i3 < (possibleSequences.size()); i3++) {
                    MergingRowIteratorTest.testMerge(possibleSequences.get(i1), possibleSequences.get(i2), possibleSequences.get(i3));
                }
            }
        }
    }

    private static class TestRowIterator implements TransformableRowIterator {
        private final Iterator<Long> timestamps;

        private final RowPointer rowPointer;

        private final SettableLongColumnValueSelector currentTimestamp = new SettableLongColumnValueSelector();

        private final RowNumCounter rowNumCounter = new RowNumCounter();

        private final SettableLongColumnValueSelector markedTimestamp = new SettableLongColumnValueSelector();

        private final TimeAndDimsPointer markedRowPointer;

        private TestRowIterator(Iterable<Long> timestamps) {
            this.timestamps = timestamps.iterator();
            this.rowPointer = new RowPointer(currentTimestamp, EMPTY_ARRAY, Collections.emptyList(), EMPTY_ARRAY, Collections.emptyList(), rowNumCounter);
            this.markedRowPointer = new TimeAndDimsPointer(markedTimestamp, EMPTY_ARRAY, Collections.emptyList(), EMPTY_ARRAY, Collections.emptyList());
        }

        @Override
        public void mark() {
            markedTimestamp.setValueFrom(currentTimestamp);
        }

        @Override
        public boolean hasTimeAndDimsChangedSinceMark() {
            return (markedTimestamp.getLong()) != (currentTimestamp.getLong());
        }

        @Override
        public RowPointer getPointer() {
            return rowPointer;
        }

        @Override
        public TimeAndDimsPointer getMarkedPointer() {
            return markedRowPointer;
        }

        @Override
        public boolean moveToNext() {
            if (!(timestamps.hasNext())) {
                return false;
            }
            currentTimestamp.setValue(timestamps.next());
            rowNumCounter.increment();
            return true;
        }

        @Override
        public void close() {
            // do nothing
        }
    }
}

