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
package org.apache.hadoop.hbase.wal;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ RegionServerTests.class, MediumTests.class })
public class TestDisabledWAL {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestDisabledWAL.class);

    @Rule
    public TestName name = new TestName();

    private static final Logger LOG = LoggerFactory.getLogger(TestDisabledWAL.class);

    static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private Table table;

    private TableName tableName;

    private byte[] fam = Bytes.toBytes("f1");

    @Test
    public void testDisabledWAL() throws Exception {
        TestDisabledWAL.LOG.info(("Writing data to table " + (tableName)));
        Put p = new Put(Bytes.toBytes("row"));
        p.addColumn(fam, Bytes.toBytes("qual"), Bytes.toBytes("val"));
        table.put(p);
        TestDisabledWAL.LOG.info(("Flushing table " + (tableName)));
        TestDisabledWAL.TEST_UTIL.flush(tableName);
        TestDisabledWAL.LOG.info(("Getting data from table " + (tableName)));
        Get get = new Get(Bytes.toBytes("row"));
        Result result = table.get(get);
        Assert.assertNotNull(result.getValue(fam, Bytes.toBytes("qual")));
    }
}

