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
package org.apache.hadoop.hbase.master;


import HConstants.CATALOG_FAMILY_STR;
import TableState.State.DISABLED;
import TableState.State.ENABLED;
import junit.framework.TestCase;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.zookeeper.ZKWatcher;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


/**
 * Tests that table state is mirrored out to zookeeper for hbase-1.x clients.
 * Also tests that table state gets migrated from zookeeper on master start.
 */
@Category({ MasterTests.class, LargeTests.class })
public class TestMirroringTableStateManager {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMirroringTableStateManager.class);

    @Rule
    public TestName name = new TestName();

    private final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    @Test
    public void testMirroring() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TEST_UTIL.createTable(tableName, CATALOG_FAMILY_STR);
        ZKWatcher zkw = getZooKeeperWatcher();
        TestCase.assertTrue(ENABLED.equals(getTableStateInZK(zkw, tableName)));
        TEST_UTIL.getAdmin().disableTable(tableName);
        TestCase.assertTrue(DISABLED.equals(getTableStateInZK(zkw, tableName)));
        TEST_UTIL.getAdmin().deleteTable(tableName);
        TestCase.assertTrue(((getTableStateInZK(zkw, tableName)) == null));
    }
}

