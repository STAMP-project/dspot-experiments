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
package org.apache.hadoop.hbase.replication;


import KeyValue.Type;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.ArrayBackedTag;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.KeyValueUtil;
import org.apache.hadoop.hbase.PrivateCellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.Tag;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.replication.ReplicationAdmin;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ ReplicationTests.class, LargeTests.class })
public class TestReplicationWithTags {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestReplicationWithTags.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestReplicationWithTags.class);

    private static final byte TAG_TYPE = 1;

    private static Configuration conf1 = HBaseConfiguration.create();

    private static Configuration conf2;

    private static ReplicationAdmin replicationAdmin;

    private static Connection connection1;

    private static Connection connection2;

    private static Table htable1;

    private static Table htable2;

    private static HBaseTestingUtility utility1;

    private static HBaseTestingUtility utility2;

    private static final long SLEEP_TIME = 500;

    private static final int NB_RETRIES = 10;

    private static final TableName TABLE_NAME = TableName.valueOf("TestReplicationWithTags");

    private static final byte[] FAMILY = Bytes.toBytes("f");

    private static final byte[] ROW = Bytes.toBytes("row");

    @Test
    public void testReplicationWithCellTags() throws Exception {
        TestReplicationWithTags.LOG.info("testSimplePutDelete");
        Put put = new Put(TestReplicationWithTags.ROW);
        put.setAttribute("visibility", Bytes.toBytes("myTag3"));
        put.addColumn(TestReplicationWithTags.FAMILY, TestReplicationWithTags.ROW, TestReplicationWithTags.ROW);
        TestReplicationWithTags.htable1 = TestReplicationWithTags.utility1.getConnection().getTable(TestReplicationWithTags.TABLE_NAME);
        TestReplicationWithTags.htable1.put(put);
        Get get = new Get(TestReplicationWithTags.ROW);
        try {
            for (int i = 0; i < (TestReplicationWithTags.NB_RETRIES); i++) {
                if (i == ((TestReplicationWithTags.NB_RETRIES) - 1)) {
                    Assert.fail("Waited too much time for put replication");
                }
                Result res = TestReplicationWithTags.htable2.get(get);
                if (res.isEmpty()) {
                    TestReplicationWithTags.LOG.info("Row not available");
                    Thread.sleep(TestReplicationWithTags.SLEEP_TIME);
                } else {
                    Assert.assertArrayEquals(TestReplicationWithTags.ROW, res.value());
                    Assert.assertEquals(1, TestReplicationWithTags.TestCoprocessorForTagsAtSink.tags.size());
                    Tag tag = TestReplicationWithTags.TestCoprocessorForTagsAtSink.tags.get(0);
                    Assert.assertEquals(TestReplicationWithTags.TAG_TYPE, tag.getType());
                    break;
                }
            }
        } finally {
            TestReplicationWithTags.TestCoprocessorForTagsAtSink.tags = null;
        }
    }

    public static class TestCoprocessorForTagsAtSource implements RegionCoprocessor , RegionObserver {
        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void prePut(final ObserverContext<RegionCoprocessorEnvironment> e, final Put put, final WALEdit edit, final Durability durability) throws IOException {
            byte[] attribute = put.getAttribute("visibility");
            byte[] cf = null;
            List<Cell> updatedCells = new ArrayList<>();
            if (attribute != null) {
                for (List<? extends Cell> edits : put.getFamilyCellMap().values()) {
                    for (Cell cell : edits) {
                        KeyValue kv = KeyValueUtil.ensureKeyValue(cell);
                        if (cf == null) {
                            cf = CellUtil.cloneFamily(kv);
                        }
                        Tag tag = new ArrayBackedTag(TestReplicationWithTags.TAG_TYPE, attribute);
                        List<Tag> tagList = new ArrayList<>(1);
                        tagList.add(tag);
                        KeyValue newKV = new KeyValue(CellUtil.cloneRow(kv), 0, kv.getRowLength(), CellUtil.cloneFamily(kv), 0, kv.getFamilyLength(), CellUtil.cloneQualifier(kv), 0, kv.getQualifierLength(), kv.getTimestamp(), Type.codeToType(kv.getTypeByte()), CellUtil.cloneValue(kv), 0, kv.getValueLength(), tagList);
                        ((List<Cell>) (updatedCells)).add(newKV);
                    }
                }
                put.getFamilyCellMap().remove(cf);
                // Update the family map
                put.getFamilyCellMap().put(cf, updatedCells);
            }
        }
    }

    public static class TestCoprocessorForTagsAtSink implements RegionCoprocessor , RegionObserver {
        public static List<Tag> tags = null;

        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void postGetOp(ObserverContext<RegionCoprocessorEnvironment> e, Get get, List<Cell> results) throws IOException {
            if ((results.size()) > 0) {
                // Check tag presence in the 1st cell in 1st Result
                if (!(results.isEmpty())) {
                    Cell cell = results.get(0);
                    TestReplicationWithTags.TestCoprocessorForTagsAtSink.tags = PrivateCellUtil.getTags(cell);
                }
            }
        }
    }
}

