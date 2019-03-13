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
package org.apache.hadoop.hbase.filter;


import Durability.SKIP_WAL;
import java.nio.ByteBuffer;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.FilterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
@Category({ FilterTests.class, MediumTests.class })
public class TestFuzzyRowAndColumnRangeFilter {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFuzzyRowAndColumnRangeFilter.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final Logger LOG = LoggerFactory.getLogger(TestFuzzyRowAndColumnRangeFilter.class);

    @Rule
    public TestName name = new TestName();

    @Test
    public void Test() throws Exception {
        String cf = "f";
        Table ht = TestFuzzyRowAndColumnRangeFilter.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), Bytes.toBytes(cf), Integer.MAX_VALUE);
        // 10 byte row key - (2 bytes 4 bytes 4 bytes)
        // 4 byte qualifier
        // 4 byte value
        for (int i1 = 0; i1 < 2; i1++) {
            for (int i2 = 0; i2 < 5; i2++) {
                byte[] rk = new byte[10];
                ByteBuffer buf = ByteBuffer.wrap(rk);
                buf.clear();
                buf.putShort(((short) (2)));
                buf.putInt(i1);
                buf.putInt(i2);
                for (int c = 0; c < 5; c++) {
                    byte[] cq = new byte[4];
                    Bytes.putBytes(cq, 0, Bytes.toBytes(c), 0, 4);
                    Put p = new Put(rk);
                    p.setDurability(SKIP_WAL);
                    p.addColumn(Bytes.toBytes(cf), cq, Bytes.toBytes(c));
                    ht.put(p);
                    TestFuzzyRowAndColumnRangeFilter.LOG.info(((("Inserting: rk: " + (Bytes.toStringBinary(rk))) + " cq: ") + (Bytes.toStringBinary(cq))));
                }
            }
        }
        TestFuzzyRowAndColumnRangeFilter.TEST_UTIL.flush();
        // test passes
        runTest(ht, 0, 10);
        // test fails
        runTest(ht, 1, 8);
    }
}

