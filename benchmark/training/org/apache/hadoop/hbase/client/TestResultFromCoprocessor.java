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
package org.apache.hadoop.hbase.client;


import KeyValue.Type.Put;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.CoprocessorEnvironment;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MediumTests.class, ClientTests.class })
public class TestResultFromCoprocessor {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestResultFromCoprocessor.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final byte[] ROW = Bytes.toBytes("normal_row");

    private static final byte[] FAMILY = Bytes.toBytes("fm");

    private static final byte[] QUAL = Bytes.toBytes("qual");

    private static final byte[] VALUE = Bytes.toBytes(100L);

    private static final byte[] FIXED_VALUE = Bytes.toBytes("fixed_value");

    private static final Cell FIXED_CELL = CellUtil.createCell(TestResultFromCoprocessor.ROW, TestResultFromCoprocessor.FAMILY, TestResultFromCoprocessor.QUAL, 0, Put.getCode(), TestResultFromCoprocessor.FIXED_VALUE);

    private static final Result FIXED_RESULT = Result.create(Arrays.asList(TestResultFromCoprocessor.FIXED_CELL));

    private static final TableName TABLE_NAME = TableName.valueOf("TestResultFromCoprocessor");

    @Test
    public void testAppend() throws IOException {
        try (Table t = TestResultFromCoprocessor.TEST_UTIL.getConnection().getTable(TestResultFromCoprocessor.TABLE_NAME)) {
            Put put = new Put(TestResultFromCoprocessor.ROW);
            put.addColumn(TestResultFromCoprocessor.FAMILY, TestResultFromCoprocessor.QUAL, TestResultFromCoprocessor.VALUE);
            t.put(put);
            TestResultFromCoprocessor.assertRowAndValue(t.get(new Get(TestResultFromCoprocessor.ROW)), TestResultFromCoprocessor.ROW, TestResultFromCoprocessor.VALUE);
            Append append = new Append(TestResultFromCoprocessor.ROW);
            append.addColumn(TestResultFromCoprocessor.FAMILY, TestResultFromCoprocessor.QUAL, TestResultFromCoprocessor.FIXED_VALUE);
            TestResultFromCoprocessor.assertRowAndValue(t.append(append), TestResultFromCoprocessor.ROW, TestResultFromCoprocessor.FIXED_VALUE);
            TestResultFromCoprocessor.assertRowAndValue(t.get(new Get(TestResultFromCoprocessor.ROW)), TestResultFromCoprocessor.ROW, Bytes.add(TestResultFromCoprocessor.VALUE, TestResultFromCoprocessor.FIXED_VALUE));
        }
    }

    @Test
    public void testIncrement() throws IOException {
        try (Table t = TestResultFromCoprocessor.TEST_UTIL.getConnection().getTable(TestResultFromCoprocessor.TABLE_NAME)) {
            Put put = new Put(TestResultFromCoprocessor.ROW);
            put.addColumn(TestResultFromCoprocessor.FAMILY, TestResultFromCoprocessor.QUAL, TestResultFromCoprocessor.VALUE);
            t.put(put);
            TestResultFromCoprocessor.assertRowAndValue(t.get(new Get(TestResultFromCoprocessor.ROW)), TestResultFromCoprocessor.ROW, TestResultFromCoprocessor.VALUE);
            Increment inc = new Increment(TestResultFromCoprocessor.ROW);
            inc.addColumn(TestResultFromCoprocessor.FAMILY, TestResultFromCoprocessor.QUAL, 99);
            TestResultFromCoprocessor.assertRowAndValue(t.increment(inc), TestResultFromCoprocessor.ROW, TestResultFromCoprocessor.FIXED_VALUE);
            TestResultFromCoprocessor.assertRowAndValue(t.get(new Get(TestResultFromCoprocessor.ROW)), TestResultFromCoprocessor.ROW, Bytes.toBytes(199L));
        }
    }

    public static class MyObserver implements RegionCoprocessor , RegionObserver {
        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public Result postAppend(final ObserverContext<RegionCoprocessorEnvironment> c, final Append append, final Result result) {
            return TestResultFromCoprocessor.FIXED_RESULT;
        }

        @Override
        public Result postIncrement(final ObserverContext<RegionCoprocessorEnvironment> c, final Increment increment, final Result result) {
            return TestResultFromCoprocessor.FIXED_RESULT;
        }

        @Override
        public void start(CoprocessorEnvironment env) throws IOException {
        }

        @Override
        public void stop(CoprocessorEnvironment env) throws IOException {
        }
    }
}

