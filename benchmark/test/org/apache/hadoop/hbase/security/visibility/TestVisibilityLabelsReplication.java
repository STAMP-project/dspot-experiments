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
package org.apache.hadoop.hbase.security.visibility;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.ArrayBackedTag;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellScanner;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.KeyValueUtil;
import org.apache.hadoop.hbase.PrivateCellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.Tag;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.replication.ReplicationEndpoint;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.WAL.Entry;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.apache.hadoop.hbase.zookeeper.ZKWatcher;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ SecurityTests.class, MediumTests.class })
public class TestVisibilityLabelsReplication {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestVisibilityLabelsReplication.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestVisibilityLabelsReplication.class);

    protected static final int NON_VIS_TAG_TYPE = 100;

    protected static final String TEMP = "temp";

    protected static Configuration conf;

    protected static Configuration conf1;

    protected static TableName TABLE_NAME = TableName.valueOf("TABLE_NAME");

    protected static Admin admin;

    public static final String TOPSECRET = "topsecret";

    public static final String PUBLIC = "public";

    public static final String PRIVATE = "private";

    public static final String CONFIDENTIAL = "confidential";

    public static final String COPYRIGHT = "\u00a9ABC";

    public static final String ACCENT = "\u0941";

    public static final String SECRET = "secret";

    public static final String UNICODE_VIS_TAG = ((((((TestVisibilityLabelsReplication.COPYRIGHT) + "\"") + (TestVisibilityLabelsReplication.ACCENT)) + "\\") + (TestVisibilityLabelsReplication.SECRET)) + "\"") + "\'&\\";

    public static HBaseTestingUtility TEST_UTIL;

    public static HBaseTestingUtility TEST_UTIL1;

    public static final byte[] row1 = Bytes.toBytes("row1");

    public static final byte[] row2 = Bytes.toBytes("row2");

    public static final byte[] row3 = Bytes.toBytes("row3");

    public static final byte[] row4 = Bytes.toBytes("row4");

    public static final byte[] fam = Bytes.toBytes("info");

    public static final byte[] qual = Bytes.toBytes("qual");

    public static final byte[] value = Bytes.toBytes("value");

    protected static ZKWatcher zkw1;

    protected static ZKWatcher zkw2;

    protected static int[] expected = new int[]{ 4, 6, 4, 0, 3 };

    private static final String NON_VISIBILITY = "non-visibility";

    protected static String[] expectedVisString = new String[]{ "(\"secret\"&\"topsecret\"&\"public\")|(\"topsecret\"&\"confidential\")", "(\"public\"&\"private\")|(\"topsecret\"&\"private\")|" + "(\"confidential\"&\"public\")|(\"topsecret\"&\"confidential\")", "(!\"topsecret\"&\"secret\")|(!\"topsecret\"&\"confidential\")", ((((((("(\"secret\"&\"" + (TestVisibilityLabelsReplication.COPYRIGHT)) + "\\\"") + (TestVisibilityLabelsReplication.ACCENT)) + "\\\\") + (TestVisibilityLabelsReplication.SECRET)) + "\\\"") + "\'&\\\\") + "\")" };

    @Rule
    public final TestName TEST_NAME = new TestName();

    public static User SUPERUSER;

    public static User USER1;

    @Test
    public void testVisibilityReplication() throws Exception {
        int retry = 0;
        try (Table table = TestVisibilityLabelsReplication.writeData(TestVisibilityLabelsReplication.TABLE_NAME, ((((((((("(" + (TestVisibilityLabelsReplication.SECRET)) + "&") + (TestVisibilityLabelsReplication.PUBLIC)) + ")") + "|(") + (TestVisibilityLabelsReplication.CONFIDENTIAL)) + ")&(") + (TestVisibilityLabelsReplication.TOPSECRET)) + ")"), (((((((("(" + (TestVisibilityLabelsReplication.PRIVATE)) + "|") + (TestVisibilityLabelsReplication.CONFIDENTIAL)) + ")&(") + (TestVisibilityLabelsReplication.PUBLIC)) + "|") + (TestVisibilityLabelsReplication.TOPSECRET)) + ")"), ((((((("(" + (TestVisibilityLabelsReplication.SECRET)) + "|") + (TestVisibilityLabelsReplication.CONFIDENTIAL)) + ")") + "&") + "!") + (TestVisibilityLabelsReplication.TOPSECRET)), (((CellVisibility.quote(TestVisibilityLabelsReplication.UNICODE_VIS_TAG)) + "&") + (TestVisibilityLabelsReplication.SECRET)))) {
            Scan s = new Scan();
            s.setAuthorizations(new Authorizations(TestVisibilityLabelsReplication.SECRET, TestVisibilityLabelsReplication.CONFIDENTIAL, TestVisibilityLabelsReplication.PRIVATE, TestVisibilityLabelsReplication.TOPSECRET, TestVisibilityLabelsReplication.UNICODE_VIS_TAG));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(4);
            Assert.assertTrue(((next.length) == 4));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), TestVisibilityLabelsReplication.row1, 0, TestVisibilityLabelsReplication.row1.length));
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), TestVisibilityLabelsReplication.row2, 0, TestVisibilityLabelsReplication.row2.length));
            cellScanner = next[2].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), TestVisibilityLabelsReplication.row3, 0, TestVisibilityLabelsReplication.row3.length));
            cellScanner = next[3].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), TestVisibilityLabelsReplication.row4, 0, TestVisibilityLabelsReplication.row4.length));
            try (Table table2 = TestVisibilityLabelsReplication.TEST_UTIL1.getConnection().getTable(TestVisibilityLabelsReplication.TABLE_NAME)) {
                s = new Scan();
                // Ensure both rows are replicated
                scanner = table2.getScanner(s);
                next = scanner.next(4);
                while (((next.length) == 0) && (retry <= 10)) {
                    scanner = table2.getScanner(s);
                    next = scanner.next(4);
                    Thread.sleep(2000);
                    retry++;
                } 
                Assert.assertTrue(((next.length) == 4));
                verifyGet(TestVisibilityLabelsReplication.row1, TestVisibilityLabelsReplication.expectedVisString[0], TestVisibilityLabelsReplication.expected[0], false, TestVisibilityLabelsReplication.TOPSECRET, TestVisibilityLabelsReplication.CONFIDENTIAL);
                TestVisibilityLabelsReplication.TestCoprocessorForTagsAtSink.tags.clear();
                verifyGet(TestVisibilityLabelsReplication.row2, TestVisibilityLabelsReplication.expectedVisString[1], TestVisibilityLabelsReplication.expected[1], false, TestVisibilityLabelsReplication.CONFIDENTIAL, TestVisibilityLabelsReplication.PUBLIC);
                TestVisibilityLabelsReplication.TestCoprocessorForTagsAtSink.tags.clear();
                verifyGet(TestVisibilityLabelsReplication.row3, TestVisibilityLabelsReplication.expectedVisString[2], TestVisibilityLabelsReplication.expected[2], false, TestVisibilityLabelsReplication.PRIVATE, TestVisibilityLabelsReplication.SECRET);
                verifyGet(TestVisibilityLabelsReplication.row3, "", TestVisibilityLabelsReplication.expected[3], true, TestVisibilityLabelsReplication.TOPSECRET, TestVisibilityLabelsReplication.SECRET);
                verifyGet(TestVisibilityLabelsReplication.row4, TestVisibilityLabelsReplication.expectedVisString[3], TestVisibilityLabelsReplication.expected[4], false, TestVisibilityLabelsReplication.UNICODE_VIS_TAG, TestVisibilityLabelsReplication.SECRET);
            }
        }
    }

    // A simple BaseRegionbserver impl that allows to add a non-visibility tag from the
    // attributes of the Put mutation.  The existing cells in the put mutation is overwritten
    // with a new cell that has the visibility tags and the non visibility tag
    public static class SimpleCP implements RegionCoprocessor , RegionObserver {
        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void prePut(ObserverContext<RegionCoprocessorEnvironment> e, Put m, WALEdit edit, Durability durability) throws IOException {
            byte[] attribute = m.getAttribute(TestVisibilityLabelsReplication.NON_VISIBILITY);
            byte[] cf = null;
            List<Cell> updatedCells = new ArrayList<>();
            if (attribute != null) {
                for (List<? extends Cell> edits : m.getFamilyCellMap().values()) {
                    for (Cell cell : edits) {
                        KeyValue kv = KeyValueUtil.ensureKeyValue(cell);
                        if (cf == null) {
                            cf = CellUtil.cloneFamily(kv);
                        }
                        Tag tag = new ArrayBackedTag(((byte) (TestVisibilityLabelsReplication.NON_VIS_TAG_TYPE)), attribute);
                        List<Tag> tagList = new ArrayList(((PrivateCellUtil.getTags(cell).size()) + 1));
                        tagList.add(tag);
                        tagList.addAll(PrivateCellUtil.getTags(cell));
                        Cell newcell = PrivateCellUtil.createCell(kv, tagList);
                        ((List<Cell>) (updatedCells)).add(newcell);
                    }
                }
                m.getFamilyCellMap().remove(cf);
                // Update the family map
                m.getFamilyCellMap().put(cf, updatedCells);
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
                    TestVisibilityLabelsReplication.TestCoprocessorForTagsAtSink.tags = PrivateCellUtil.getTags(cell);
                }
            }
        }
    }

    /**
     * An extn of VisibilityReplicationEndpoint to verify the tags that are replicated
     */
    public static class VisibilityReplicationEndPointForTest extends VisibilityReplicationEndpoint {
        static AtomicInteger replicateCount = new AtomicInteger();

        static volatile List<Entry> lastEntries = null;

        public VisibilityReplicationEndPointForTest(ReplicationEndpoint endpoint, VisibilityLabelService visibilityLabelsService) {
            super(endpoint, visibilityLabelsService);
        }

        @Override
        public boolean replicate(ReplicateContext replicateContext) {
            boolean ret = super.replicate(replicateContext);
            TestVisibilityLabelsReplication.VisibilityReplicationEndPointForTest.lastEntries = replicateContext.getEntries();
            TestVisibilityLabelsReplication.VisibilityReplicationEndPointForTest.replicateCount.incrementAndGet();
            return ret;
        }
    }
}

