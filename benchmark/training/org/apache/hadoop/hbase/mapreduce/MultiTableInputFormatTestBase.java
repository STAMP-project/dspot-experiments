/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.mapreduce;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Base set of tests and setup for input formats touching multiple tables.
 */
public abstract class MultiTableInputFormatTestBase {
    static final Logger LOG = LoggerFactory.getLogger(TestMultiTableInputFormat.class);

    public static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    static final String TABLE_NAME = "scantest";

    static final byte[] INPUT_FAMILY = Bytes.toBytes("contents");

    static final String KEY_STARTROW = "startRow";

    static final String KEY_LASTROW = "stpRow";

    static List<String> TABLES = Lists.newArrayList();

    static {
        for (int i = 0; i < 3; i++) {
            MultiTableInputFormatTestBase.TABLES.add(((MultiTableInputFormatTestBase.TABLE_NAME) + (String.valueOf(i))));
        }
    }

    /**
     * Pass the key and value to reducer.
     */
    public static class ScanMapper extends TableMapper<ImmutableBytesWritable, ImmutableBytesWritable> {
        /**
         * Pass the key and value to reduce.
         *
         * @param key
         * 		The key, here "aaa", "aab" etc.
         * @param value
         * 		The value is the same as the key.
         * @param context
         * 		The task context.
         * @throws IOException
         * 		When reading the rows fails.
         */
        @Override
        public void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
            makeAssertions(key, value);
            context.write(key, key);
        }

        public void makeAssertions(ImmutableBytesWritable key, Result value) throws IOException {
            if ((value.size()) != 1) {
                throw new IOException("There should only be one input column");
            }
            Map<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> cf = value.getMap();
            if (!(cf.containsKey(MultiTableInputFormatTestBase.INPUT_FAMILY))) {
                throw new IOException((("Wrong input columns. Missing: '" + (Bytes.toString(MultiTableInputFormatTestBase.INPUT_FAMILY))) + "'."));
            }
            String val = Bytes.toStringBinary(value.getValue(MultiTableInputFormatTestBase.INPUT_FAMILY, null));
            MultiTableInputFormatTestBase.LOG.debug(((("map: key -> " + (Bytes.toStringBinary(key.get()))) + ", value -> ") + val));
        }
    }

    /**
     * Checks the last and first keys seen against the scanner boundaries.
     */
    public static class ScanReducer extends Reducer<ImmutableBytesWritable, ImmutableBytesWritable, NullWritable, NullWritable> {
        private String first = null;

        private String last = null;

        @Override
        protected void reduce(ImmutableBytesWritable key, Iterable<ImmutableBytesWritable> values, Context context) throws IOException, InterruptedException {
            makeAssertions(key, values);
        }

        protected void makeAssertions(ImmutableBytesWritable key, Iterable<ImmutableBytesWritable> values) {
            int count = 0;
            for (ImmutableBytesWritable value : values) {
                String val = Bytes.toStringBinary(value.get());
                MultiTableInputFormatTestBase.LOG.debug(((((("reduce: key[" + count) + "] -> ") + (Bytes.toStringBinary(key.get()))) + ", value -> ") + val));
                if ((first) == null)
                    first = val;

                last = val;
                count++;
            }
            Assert.assertEquals(3, count);
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            Configuration c = context.getConfiguration();
            cleanup(c);
        }

        protected void cleanup(Configuration c) {
            String startRow = c.get(MultiTableInputFormatTestBase.KEY_STARTROW);
            String lastRow = c.get(MultiTableInputFormatTestBase.KEY_LASTROW);
            MultiTableInputFormatTestBase.LOG.info((((("cleanup: first -> \"" + (first)) + "\", start row -> \"") + startRow) + "\""));
            MultiTableInputFormatTestBase.LOG.info((((("cleanup: last -> \"" + (last)) + "\", last row -> \"") + lastRow) + "\""));
            if ((startRow != null) && ((startRow.length()) > 0)) {
                Assert.assertEquals(startRow, first);
            }
            if ((lastRow != null) && ((lastRow.length()) > 0)) {
                Assert.assertEquals(lastRow, last);
            }
        }
    }

    @Test
    public void testScanEmptyToEmpty() throws IOException, ClassNotFoundException, InterruptedException {
        testScan(null, null, null);
    }

    @Test
    public void testScanEmptyToAPP() throws IOException, ClassNotFoundException, InterruptedException {
        testScan(null, "app", "apo");
    }

    @Test
    public void testScanOBBToOPP() throws IOException, ClassNotFoundException, InterruptedException {
        testScan("obb", "opp", "opo");
    }

    @Test
    public void testScanYZYToEmpty() throws IOException, ClassNotFoundException, InterruptedException {
        testScan("yzy", null, "zzz");
    }
}

