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


import HColumnDescriptor.DFS_REPLICATION;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;


@Category({ LargeTests.class, ClientTests.class })
public class TestIllegalTableDescriptor {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestIllegalTableDescriptor.class);

    // NOTE: Increment tests were moved to their own class, TestIncrementsFromClientSide.
    private static final Logger masterLogger;

    protected static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static byte[] FAMILY = Bytes.toBytes("testFamily");

    @Rule
    public TestName name = new TestName();

    static {
        masterLogger = Mockito.mock(Logger.class);
    }

    @Test
    public void testIllegalTableDescriptor() throws Exception {
        HTableDescriptor htd = new HTableDescriptor(TableName.valueOf(name.getMethodName()));
        HColumnDescriptor hcd = new HColumnDescriptor(TestIllegalTableDescriptor.FAMILY);
        // create table with 0 families
        checkTableIsIllegal(htd);
        htd.addFamily(hcd);
        checkTableIsLegal(htd);
        htd.setMaxFileSize(1024);// 1K

        checkTableIsIllegal(htd);
        htd.setMaxFileSize(0);
        checkTableIsIllegal(htd);
        htd.setMaxFileSize(((1024 * 1024) * 1024));// 1G

        checkTableIsLegal(htd);
        htd.setMemStoreFlushSize(1024);
        checkTableIsIllegal(htd);
        htd.setMemStoreFlushSize(0);
        checkTableIsIllegal(htd);
        htd.setMemStoreFlushSize(((128 * 1024) * 1024));// 128M

        checkTableIsLegal(htd);
        htd.setRegionSplitPolicyClassName("nonexisting.foo.class");
        checkTableIsIllegal(htd);
        htd.setRegionSplitPolicyClassName(null);
        checkTableIsLegal(htd);
        hcd.setBlocksize(0);
        checkTableIsIllegal(htd);
        hcd.setBlocksize(((1024 * 1024) * 128));// 128M

        checkTableIsIllegal(htd);
        hcd.setBlocksize(1024);
        checkTableIsLegal(htd);
        hcd.setTimeToLive(0);
        checkTableIsIllegal(htd);
        hcd.setTimeToLive((-1));
        checkTableIsIllegal(htd);
        hcd.setTimeToLive(1);
        checkTableIsLegal(htd);
        hcd.setMinVersions((-1));
        checkTableIsIllegal(htd);
        hcd.setMinVersions(3);
        try {
            hcd.setMaxVersions(2);
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            // expected
            hcd.setMaxVersions(10);
        }
        checkTableIsLegal(htd);
        // HBASE-13776 Setting illegal versions for HColumnDescriptor
        // does not throw IllegalArgumentException
        // finally, minVersions must be less than or equal to maxVersions
        hcd.setMaxVersions(4);
        hcd.setMinVersions(5);
        checkTableIsIllegal(htd);
        hcd.setMinVersions(3);
        hcd.setScope((-1));
        checkTableIsIllegal(htd);
        hcd.setScope(0);
        checkTableIsLegal(htd);
        try {
            hcd.setDFSReplication(((short) (-1)));
            Assert.fail("Illegal value for setDFSReplication did not throw");
        } catch (IllegalArgumentException e) {
            // pass
        }
        // set an illegal DFS replication value by hand
        hcd.setValue(DFS_REPLICATION, "-1");
        checkTableIsIllegal(htd);
        try {
            hcd.setDFSReplication(((short) (-1)));
            Assert.fail("Should throw exception if an illegal value is explicitly being set");
        } catch (IllegalArgumentException e) {
            // pass
        }
        // check the conf settings to disable sanity checks
        htd.setMemStoreFlushSize(0);
        // Check that logs warn on invalid table but allow it.
        htd.setConfiguration("hbase.table.sanity.checks", Boolean.FALSE.toString());
        checkTableIsLegal(htd);
        Mockito.verify(TestIllegalTableDescriptor.masterLogger).warn(ArgumentMatchers.contains(("MEMSTORE_FLUSHSIZE for table " + ("descriptor or \"hbase.hregion.memstore.flush.size\" (0) is too small, which might " + "cause very frequent flushing."))));
    }
}

