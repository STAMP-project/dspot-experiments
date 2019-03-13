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


import HConstants.CATALOG_FAMILY;
import HConstants.REGIONINFO_QUALIFIER;
import TableName.META_TABLE_NAME;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MetaTableAccessor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessor;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.MasterObserver;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestEnableTable {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestEnableTable.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final Logger LOG = LoggerFactory.getLogger(TestEnableTable.class);

    private static final byte[] FAMILYNAME = Bytes.toBytes("fam");

    @Rule
    public TestName name = new TestName();

    /**
     * We were only clearing rows that had a hregioninfo column in hbase:meta.  Mangled rows that
     * were missing the hregioninfo because of error were being left behind messing up any
     * subsequent table made with the same name. HBASE-12980
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testDeleteForSureClearsAllTableRowsFromMeta() throws IOException, InterruptedException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final Admin admin = TestEnableTable.TEST_UTIL.getAdmin();
        final HTableDescriptor desc = new HTableDescriptor(tableName);
        desc.addFamily(new HColumnDescriptor(TestEnableTable.FAMILYNAME));
        try {
            TestEnableTable.createTable(TestEnableTable.TEST_UTIL, desc, HBaseTestingUtility.KEYS_FOR_HBA_CREATE_TABLE);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(("Got an exception while creating " + tableName));
        }
        // Now I have a nice table, mangle it by removing the HConstants.REGIONINFO_QUALIFIER_STR
        // content from a few of the rows.
        try (Table metaTable = TestEnableTable.TEST_UTIL.getConnection().getTable(META_TABLE_NAME)) {
            try (ResultScanner scanner = metaTable.getScanner(MetaTableAccessor.getScanForTableName(TestEnableTable.TEST_UTIL.getConnection(), tableName))) {
                for (Result result : scanner) {
                    // Just delete one row.
                    Delete d = new Delete(result.getRow());
                    d.addColumn(CATALOG_FAMILY, REGIONINFO_QUALIFIER);
                    TestEnableTable.LOG.info(("Mangled: " + d));
                    metaTable.delete(d);
                    break;
                }
            }
            admin.disableTable(tableName);
            TestEnableTable.TEST_UTIL.waitTableDisabled(tableName.getName());
            // Rely on the coprocessor based latch to make the operation synchronous.
            try {
                TestEnableTable.deleteTable(TestEnableTable.TEST_UTIL, tableName);
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail(("Got an exception while deleting " + tableName));
            }
            int rowCount = 0;
            try (ResultScanner scanner = metaTable.getScanner(MetaTableAccessor.getScanForTableName(TestEnableTable.TEST_UTIL.getConnection(), tableName))) {
                for (Result result : scanner) {
                    TestEnableTable.LOG.info(("Found when none expected: " + result));
                    rowCount++;
                }
            }
            Assert.assertEquals(0, rowCount);
        }
    }

    public static class MasterSyncObserver implements MasterCoprocessor , MasterObserver {
        volatile CountDownLatch tableCreationLatch = null;

        volatile CountDownLatch tableDeletionLatch = null;

        @Override
        public Optional<MasterObserver> getMasterObserver() {
            return Optional.of(this);
        }

        @Override
        public void postCompletedCreateTableAction(final ObserverContext<MasterCoprocessorEnvironment> ctx, final TableDescriptor desc, final RegionInfo[] regions) throws IOException {
            // the AccessController test, some times calls only and directly the
            // postCompletedCreateTableAction()
            if ((tableCreationLatch) != null) {
                tableCreationLatch.countDown();
            }
        }

        @Override
        public void postCompletedDeleteTableAction(final ObserverContext<MasterCoprocessorEnvironment> ctx, final TableName tableName) throws IOException {
            // the AccessController test, some times calls only and directly the postDeleteTableHandler()
            if ((tableDeletionLatch) != null) {
                tableDeletionLatch.countDown();
            }
        }
    }
}

