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
package org.apache.hadoop.util;


import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparator;
import org.junit.Assert;
import org.junit.Test;


public class TestIndexedSort {
    @Test
    public void testQuickSort() throws Exception {
        QuickSort sorter = new QuickSort();
        sortRandom(sorter);
        sortSingleRecord(sorter);
        sortSequential(sorter);
        sortSorted(sorter);
        sortAllEqual(sorter);
        sortWritable(sorter);
        // test degenerate case for median-of-three partitioning
        // a_n, a_1, a_2, ..., a_{n-1}
        final int DSAMPLE = 500;
        int[] values = new int[DSAMPLE];
        for (int i = 0; i < DSAMPLE; ++i) {
            values[i] = i;
        }
        values[0] = (values[(DSAMPLE - 1)]) + 1;
        TestIndexedSort.SampleSortable s = new TestIndexedSort.SampleSortable(values);
        values = s.getValues();
        final int DSS = (DSAMPLE / 2) * (DSAMPLE / 2);
        // Worst case is (N/2)^2 comparisons, not including those effecting
        // the median-of-three partitioning; impl should handle this case
        TestIndexedSort.MeasuredSortable m = new TestIndexedSort.MeasuredSortable(s, DSS);
        sorter.sort(m, 0, DSAMPLE);
        System.out.println((((((("QuickSort degen cmp/swp: " + (m.getCmp())) + "/") + (m.getSwp())) + "(") + (sorter.getClass().getName())) + ")"));
        Arrays.sort(values);
        int[] check = s.getSorted();
        Assert.assertTrue(Arrays.equals(values, check));
    }

    @Test
    public void testHeapSort() throws Exception {
        HeapSort sorter = new HeapSort();
        sortRandom(sorter);
        sortSingleRecord(sorter);
        sortSequential(sorter);
        sortSorted(sorter);
        sortAllEqual(sorter);
        sortWritable(sorter);
    }

    // Sortables //
    private static class SampleSortable implements IndexedSortable {
        private int[] valindex;

        private int[] valindirect;

        private int[] values;

        private final long seed;

        public SampleSortable() {
            this(50);
        }

        public SampleSortable(int j) {
            Random r = new Random();
            seed = r.nextLong();
            r.setSeed(seed);
            values = new int[j];
            valindex = new int[j];
            valindirect = new int[j];
            for (int i = 0; i < j; ++i) {
                valindex[i] = valindirect[i] = i;
                values[i] = r.nextInt(1000);
            }
        }

        public SampleSortable(int[] values) {
            this.values = values;
            valindex = new int[values.length];
            valindirect = new int[values.length];
            for (int i = 0; i < (values.length); ++i) {
                valindex[i] = valindirect[i] = i;
            }
            seed = 0;
        }

        public long getSeed() {
            return seed;
        }

        @Override
        public int compare(int i, int j) {
            // assume positive
            return (values[valindirect[valindex[i]]]) - (values[valindirect[valindex[j]]]);
        }

        @Override
        public void swap(int i, int j) {
            int tmp = valindex[i];
            valindex[i] = valindex[j];
            valindex[j] = tmp;
        }

        public int[] getSorted() {
            int[] ret = new int[values.length];
            for (int i = 0; i < (ret.length); ++i) {
                ret[i] = values[valindirect[valindex[i]]];
            }
            return ret;
        }

        public int[] getValues() {
            int[] ret = new int[values.length];
            System.arraycopy(values, 0, ret, 0, values.length);
            return ret;
        }
    }

    public static class MeasuredSortable implements IndexedSortable {
        private int comparisions;

        private int swaps;

        private final int maxcmp;

        private final int maxswp;

        private IndexedSortable s;

        public MeasuredSortable(IndexedSortable s) {
            this(s, Integer.MAX_VALUE);
        }

        public MeasuredSortable(IndexedSortable s, int maxcmp) {
            this(s, maxcmp, Integer.MAX_VALUE);
        }

        public MeasuredSortable(IndexedSortable s, int maxcmp, int maxswp) {
            this.s = s;
            this.maxcmp = maxcmp;
            this.maxswp = maxswp;
        }

        public int getCmp() {
            return comparisions;
        }

        public int getSwp() {
            return swaps;
        }

        @Override
        public int compare(int i, int j) {
            Assert.assertTrue((("Expected fewer than " + (maxcmp)) + " comparisons"), ((++(comparisions)) < (maxcmp)));
            return s.compare(i, j);
        }

        @Override
        public void swap(int i, int j) {
            Assert.assertTrue((("Expected fewer than " + (maxswp)) + " swaps"), ((++(swaps)) < (maxswp)));
            s.swap(i, j);
        }
    }

    private static class WritableSortable implements IndexedSortable {
        private static Random r = new Random();

        private final int eob;

        private final int[] indices;

        private final int[] offsets;

        private final byte[] bytes;

        private final WritableComparator comparator;

        private final String[] check;

        private final long seed;

        public WritableSortable() throws IOException {
            this(100);
        }

        public WritableSortable(int j) throws IOException {
            seed = TestIndexedSort.WritableSortable.r.nextLong();
            TestIndexedSort.WritableSortable.r.setSeed(seed);
            Text t = new Text();
            StringBuilder sb = new StringBuilder();
            indices = new int[j];
            offsets = new int[j];
            check = new String[j];
            DataOutputBuffer dob = new DataOutputBuffer();
            for (int i = 0; i < j; ++i) {
                indices[i] = i;
                offsets[i] = dob.getLength();
                TestIndexedSort.WritableSortable.genRandom(t, ((TestIndexedSort.WritableSortable.r.nextInt(15)) + 1), sb);
                t.write(dob);
                check[i] = t.toString();
            }
            eob = dob.getLength();
            bytes = dob.getData();
            comparator = WritableComparator.get(Text.class);
        }

        public long getSeed() {
            return seed;
        }

        private static void genRandom(Text t, int len, StringBuilder sb) {
            sb.setLength(0);
            for (int i = 0; i < len; ++i) {
                sb.append(Integer.toString(((TestIndexedSort.WritableSortable.r.nextInt(26)) + 10), 36));
            }
            t.set(sb.toString());
        }

        @Override
        public int compare(int i, int j) {
            final int ii = indices[i];
            final int ij = indices[j];
            return comparator.compare(bytes, offsets[ii], (((ii + 1) == (indices.length) ? eob : offsets[(ii + 1)]) - (offsets[ii])), bytes, offsets[ij], (((ij + 1) == (indices.length) ? eob : offsets[(ij + 1)]) - (offsets[ij])));
        }

        @Override
        public void swap(int i, int j) {
            int tmp = indices[i];
            indices[i] = indices[j];
            indices[j] = tmp;
        }

        public String[] getValues() {
            return check;
        }

        public String[] getSorted() throws IOException {
            String[] ret = new String[indices.length];
            Text t = new Text();
            DataInputBuffer dib = new DataInputBuffer();
            for (int i = 0; i < (ret.length); ++i) {
                int ii = indices[i];
                dib.reset(bytes, offsets[ii], (((ii + 1) == (indices.length) ? eob : offsets[(ii + 1)]) - (offsets[ii])));
                t.readFields(dib);
                ret[i] = t.toString();
            }
            return ret;
        }
    }
}

