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
package org.apache.hadoop.hbase.client.replication;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MediumTests.class, ClientTests.class })
public class TestReplicationAdminWithTwoDifferentZKClusters {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestReplicationAdminWithTwoDifferentZKClusters.class);

    private static Configuration conf1 = HBaseConfiguration.create();

    private static Configuration conf2;

    private static HBaseTestingUtility utility1;

    private static HBaseTestingUtility utility2;

    private static ReplicationAdmin admin;

    private static final TableName tableName = TableName.valueOf("test");

    private static final byte[] famName = Bytes.toBytes("f");

    private static final String peerId = "peer1";

    /* Test for HBASE-15393 */
    @Test
    public void testEnableTableReplication() throws Exception {
        TestReplicationAdminWithTwoDifferentZKClusters.admin.enableTableRep(TestReplicationAdminWithTwoDifferentZKClusters.tableName);
        Assert.assertTrue(TestReplicationAdminWithTwoDifferentZKClusters.utility2.getAdmin().tableExists(TestReplicationAdminWithTwoDifferentZKClusters.tableName));
    }

    @Test
    public void testDisableTableReplication() throws Exception {
        TestReplicationAdminWithTwoDifferentZKClusters.admin.disableTableRep(TestReplicationAdminWithTwoDifferentZKClusters.tableName);
        Assert.assertTrue(TestReplicationAdminWithTwoDifferentZKClusters.utility2.getAdmin().tableExists(TestReplicationAdminWithTwoDifferentZKClusters.tableName));
    }
}

