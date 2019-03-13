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


import ReplicationProtos.TableCF;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.replication.ReplicationAdmin;
import org.apache.hadoop.hbase.client.replication.ReplicationPeerConfigUtil;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ReplicationProtos;
import org.apache.hadoop.hbase.testclassification.FlakeyTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ FlakeyTests.class, LargeTests.class })
public class TestPerTableCFReplication {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestPerTableCFReplication.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestPerTableCFReplication.class);

    private static Configuration conf1;

    private static Configuration conf2;

    private static Configuration conf3;

    private static HBaseTestingUtility utility1;

    private static HBaseTestingUtility utility2;

    private static HBaseTestingUtility utility3;

    private static final long SLEEP_TIME = 500;

    private static final int NB_RETRIES = 100;

    private static final TableName tableName = TableName.valueOf("test");

    private static final TableName tabAName = TableName.valueOf("TA");

    private static final TableName tabBName = TableName.valueOf("TB");

    private static final TableName tabCName = TableName.valueOf("TC");

    private static final byte[] famName = Bytes.toBytes("f");

    private static final byte[] f1Name = Bytes.toBytes("f1");

    private static final byte[] f2Name = Bytes.toBytes("f2");

    private static final byte[] f3Name = Bytes.toBytes("f3");

    private static final byte[] row1 = Bytes.toBytes("row1");

    private static final byte[] row2 = Bytes.toBytes("row2");

    private static final byte[] noRepfamName = Bytes.toBytes("norep");

    private static final byte[] val = Bytes.toBytes("myval");

    private static HTableDescriptor table;

    private static HTableDescriptor tabA;

    private static HTableDescriptor tabB;

    private static HTableDescriptor tabC;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testParseTableCFsFromConfig() {
        Map<TableName, List<String>> tabCFsMap = null;
        // 1. null or empty string, result should be null
        tabCFsMap = ReplicationPeerConfigUtil.parseTableCFsFromConfig(null);
        Assert.assertEquals(null, tabCFsMap);
        tabCFsMap = ReplicationPeerConfigUtil.parseTableCFsFromConfig("");
        Assert.assertEquals(null, tabCFsMap);
        tabCFsMap = ReplicationPeerConfigUtil.parseTableCFsFromConfig("   ");
        Assert.assertEquals(null, tabCFsMap);
        final TableName tableName1 = TableName.valueOf(((name.getMethodName()) + "1"));
        final TableName tableName2 = TableName.valueOf(((name.getMethodName()) + "2"));
        final TableName tableName3 = TableName.valueOf(((name.getMethodName()) + "3"));
        // 2. single table: "tableName1" / "tableName2:cf1" / "tableName3:cf1,cf3"
        tabCFsMap = ReplicationPeerConfigUtil.parseTableCFsFromConfig(tableName1.getNameAsString());
        Assert.assertEquals(1, tabCFsMap.size());// only one table

        Assert.assertTrue(tabCFsMap.containsKey(tableName1));// its table name is "tableName1"

        Assert.assertFalse(tabCFsMap.containsKey(tableName2));// not other table

        Assert.assertEquals(null, tabCFsMap.get(tableName1));// null cf-list,

        tabCFsMap = ReplicationPeerConfigUtil.parseTableCFsFromConfig((tableName2 + ":cf1"));
        Assert.assertEquals(1, tabCFsMap.size());// only one table

        Assert.assertTrue(tabCFsMap.containsKey(tableName2));// its table name is "tableName2"

        Assert.assertFalse(tabCFsMap.containsKey(tableName1));// not other table

        Assert.assertEquals(1, tabCFsMap.get(tableName2).size());// cf-list contains only 1 cf

        Assert.assertEquals("cf1", tabCFsMap.get(tableName2).get(0));// the only cf is "cf1"

        tabCFsMap = ReplicationPeerConfigUtil.parseTableCFsFromConfig((tableName3 + " : cf1 , cf3"));
        Assert.assertEquals(1, tabCFsMap.size());// only one table

        Assert.assertTrue(tabCFsMap.containsKey(tableName3));// its table name is "tableName2"

        Assert.assertFalse(tabCFsMap.containsKey(tableName1));// not other table

        Assert.assertEquals(2, tabCFsMap.get(tableName3).size());// cf-list contains 2 cf

        Assert.assertTrue(tabCFsMap.get(tableName3).contains("cf1"));// contains "cf1"

        Assert.assertTrue(tabCFsMap.get(tableName3).contains("cf3"));// contains "cf3"

        // 3. multiple tables: "tableName1 ; tableName2:cf1 ; tableName3:cf1,cf3"
        tabCFsMap = ReplicationPeerConfigUtil.parseTableCFsFromConfig((((((tableName1 + " ; ") + tableName2) + ":cf1 ; ") + tableName3) + ":cf1,cf3"));
        // 3.1 contains 3 tables : "tableName1", "tableName2" and "tableName3"
        Assert.assertEquals(3, tabCFsMap.size());
        Assert.assertTrue(tabCFsMap.containsKey(tableName1));
        Assert.assertTrue(tabCFsMap.containsKey(tableName2));
        Assert.assertTrue(tabCFsMap.containsKey(tableName3));
        // 3.2 table "tab1" : null cf-list
        Assert.assertEquals(null, tabCFsMap.get(tableName1));
        // 3.3 table "tab2" : cf-list contains a single cf "cf1"
        Assert.assertEquals(1, tabCFsMap.get(tableName2).size());
        Assert.assertEquals("cf1", tabCFsMap.get(tableName2).get(0));
        // 3.4 table "tab3" : cf-list contains "cf1" and "cf3"
        Assert.assertEquals(2, tabCFsMap.get(tableName3).size());
        Assert.assertTrue(tabCFsMap.get(tableName3).contains("cf1"));
        Assert.assertTrue(tabCFsMap.get(tableName3).contains("cf3"));
        // 4. contiguous or additional ";"(table delimiter) or ","(cf delimiter) can be tolerated
        // still use the example of multiple tables: "tableName1 ; tableName2:cf1 ; tableName3:cf1,cf3"
        tabCFsMap = ReplicationPeerConfigUtil.parseTableCFsFromConfig((((((tableName1 + " ; ; ") + tableName2) + ":cf1 ; ") + tableName3) + ":cf1,,cf3 ;"));
        // 4.1 contains 3 tables : "tableName1", "tableName2" and "tableName3"
        Assert.assertEquals(3, tabCFsMap.size());
        Assert.assertTrue(tabCFsMap.containsKey(tableName1));
        Assert.assertTrue(tabCFsMap.containsKey(tableName2));
        Assert.assertTrue(tabCFsMap.containsKey(tableName3));
        // 4.2 table "tab1" : null cf-list
        Assert.assertEquals(null, tabCFsMap.get(tableName1));
        // 4.3 table "tab2" : cf-list contains a single cf "cf1"
        Assert.assertEquals(1, tabCFsMap.get(tableName2).size());
        Assert.assertEquals("cf1", tabCFsMap.get(tableName2).get(0));
        // 4.4 table "tab3" : cf-list contains "cf1" and "cf3"
        Assert.assertEquals(2, tabCFsMap.get(tableName3).size());
        Assert.assertTrue(tabCFsMap.get(tableName3).contains("cf1"));
        Assert.assertTrue(tabCFsMap.get(tableName3).contains("cf3"));
        // 5. invalid format "tableName1:tt:cf1 ; tableName2::cf1 ; tableName3:cf1,cf3"
        // "tableName1:tt:cf1" and "tableName2::cf1" are invalid and will be ignored totally
        tabCFsMap = ReplicationPeerConfigUtil.parseTableCFsFromConfig((((((tableName1 + ":tt:cf1 ; ") + tableName2) + "::cf1 ; ") + tableName3) + ":cf1,cf3"));
        // 5.1 no "tableName1" and "tableName2", only "tableName3"
        Assert.assertEquals(1, tabCFsMap.size());// only one table

        Assert.assertFalse(tabCFsMap.containsKey(tableName1));
        Assert.assertFalse(tabCFsMap.containsKey(tableName2));
        Assert.assertTrue(tabCFsMap.containsKey(tableName3));
        // 5.2 table "tableName3" : cf-list contains "cf1" and "cf3"
        Assert.assertEquals(2, tabCFsMap.get(tableName3).size());
        Assert.assertTrue(tabCFsMap.get(tableName3).contains("cf1"));
        Assert.assertTrue(tabCFsMap.get(tableName3).contains("cf3"));
    }

    @Test
    public void testTableCFsHelperConverter() {
        ReplicationProtos[] tableCFs = null;
        Map<TableName, List<String>> tabCFsMap = null;
        // 1. null or empty string, result should be null
        Assert.assertNull(ReplicationPeerConfigUtil.convert(tabCFsMap));
        tabCFsMap = new HashMap();
        tableCFs = ReplicationPeerConfigUtil.convert(tabCFsMap);
        Assert.assertEquals(0, tableCFs.length);
        final TableName tableName1 = TableName.valueOf(((name.getMethodName()) + "1"));
        final TableName tableName2 = TableName.valueOf(((name.getMethodName()) + "2"));
        final TableName tableName3 = TableName.valueOf(((name.getMethodName()) + "3"));
        // 2. single table: "tab1" / "tab2:cf1" / "tab3:cf1,cf3"
        tabCFsMap.clear();
        tabCFsMap.put(tableName1, null);
        tableCFs = ReplicationPeerConfigUtil.convert(tabCFsMap);
        Assert.assertEquals(1, tableCFs.length);// only one table

        Assert.assertEquals(tableName1.toString(), tableCFs[0].getTableName().getQualifier().toStringUtf8());
        Assert.assertEquals(0, tableCFs[0].getFamiliesCount());
        tabCFsMap.clear();
        tabCFsMap.put(tableName2, new ArrayList());
        tabCFsMap.get(tableName2).add("cf1");
        tableCFs = ReplicationPeerConfigUtil.convert(tabCFsMap);
        Assert.assertEquals(1, tableCFs.length);// only one table

        Assert.assertEquals(tableName2.toString(), tableCFs[0].getTableName().getQualifier().toStringUtf8());
        Assert.assertEquals(1, tableCFs[0].getFamiliesCount());
        Assert.assertEquals("cf1", tableCFs[0].getFamilies(0).toStringUtf8());
        tabCFsMap.clear();
        tabCFsMap.put(tableName3, new ArrayList());
        tabCFsMap.get(tableName3).add("cf1");
        tabCFsMap.get(tableName3).add("cf3");
        tableCFs = ReplicationPeerConfigUtil.convert(tabCFsMap);
        Assert.assertEquals(1, tableCFs.length);
        Assert.assertEquals(tableName3.toString(), tableCFs[0].getTableName().getQualifier().toStringUtf8());
        Assert.assertEquals(2, tableCFs[0].getFamiliesCount());
        Assert.assertEquals("cf1", tableCFs[0].getFamilies(0).toStringUtf8());
        Assert.assertEquals("cf3", tableCFs[0].getFamilies(1).toStringUtf8());
        tabCFsMap.clear();
        tabCFsMap.put(tableName1, null);
        tabCFsMap.put(tableName2, new ArrayList());
        tabCFsMap.get(tableName2).add("cf1");
        tabCFsMap.put(tableName3, new ArrayList());
        tabCFsMap.get(tableName3).add("cf1");
        tabCFsMap.get(tableName3).add("cf3");
        tableCFs = ReplicationPeerConfigUtil.convert(tabCFsMap);
        Assert.assertEquals(3, tableCFs.length);
        Assert.assertNotNull(ReplicationPeerConfigUtil.getTableCF(tableCFs, tableName1.toString()));
        Assert.assertNotNull(ReplicationPeerConfigUtil.getTableCF(tableCFs, tableName2.toString()));
        Assert.assertNotNull(ReplicationPeerConfigUtil.getTableCF(tableCFs, tableName3.toString()));
        Assert.assertEquals(0, ReplicationPeerConfigUtil.getTableCF(tableCFs, tableName1.toString()).getFamiliesCount());
        Assert.assertEquals(1, ReplicationPeerConfigUtil.getTableCF(tableCFs, tableName2.toString()).getFamiliesCount());
        Assert.assertEquals("cf1", ReplicationPeerConfigUtil.getTableCF(tableCFs, tableName2.toString()).getFamilies(0).toStringUtf8());
        Assert.assertEquals(2, ReplicationPeerConfigUtil.getTableCF(tableCFs, tableName3.toString()).getFamiliesCount());
        Assert.assertEquals("cf1", ReplicationPeerConfigUtil.getTableCF(tableCFs, tableName3.toString()).getFamilies(0).toStringUtf8());
        Assert.assertEquals("cf3", ReplicationPeerConfigUtil.getTableCF(tableCFs, tableName3.toString()).getFamilies(1).toStringUtf8());
        tabCFsMap = ReplicationPeerConfigUtil.convert2Map(tableCFs);
        Assert.assertEquals(3, tabCFsMap.size());
        Assert.assertTrue(tabCFsMap.containsKey(tableName1));
        Assert.assertTrue(tabCFsMap.containsKey(tableName2));
        Assert.assertTrue(tabCFsMap.containsKey(tableName3));
        // 3.2 table "tab1" : null cf-list
        Assert.assertEquals(null, tabCFsMap.get(tableName1));
        // 3.3 table "tab2" : cf-list contains a single cf "cf1"
        Assert.assertEquals(1, tabCFsMap.get(tableName2).size());
        Assert.assertEquals("cf1", tabCFsMap.get(tableName2).get(0));
        // 3.4 table "tab3" : cf-list contains "cf1" and "cf3"
        Assert.assertEquals(2, tabCFsMap.get(tableName3).size());
        Assert.assertTrue(tabCFsMap.get(tableName3).contains("cf1"));
        Assert.assertTrue(tabCFsMap.get(tableName3).contains("cf3"));
    }

    @Test
    public void testPerTableCFReplication() throws Exception {
        TestPerTableCFReplication.LOG.info("testPerTableCFReplication");
        ReplicationAdmin replicationAdmin = new ReplicationAdmin(TestPerTableCFReplication.conf1);
        Connection connection1 = ConnectionFactory.createConnection(TestPerTableCFReplication.conf1);
        Connection connection2 = ConnectionFactory.createConnection(TestPerTableCFReplication.conf2);
        Connection connection3 = ConnectionFactory.createConnection(TestPerTableCFReplication.conf3);
        try {
            Admin admin1 = connection1.getAdmin();
            Admin admin2 = connection2.getAdmin();
            Admin admin3 = connection3.getAdmin();
            admin1.createTable(TestPerTableCFReplication.tabA);
            admin1.createTable(TestPerTableCFReplication.tabB);
            admin1.createTable(TestPerTableCFReplication.tabC);
            admin2.createTable(TestPerTableCFReplication.tabA);
            admin2.createTable(TestPerTableCFReplication.tabB);
            admin2.createTable(TestPerTableCFReplication.tabC);
            admin3.createTable(TestPerTableCFReplication.tabA);
            admin3.createTable(TestPerTableCFReplication.tabB);
            admin3.createTable(TestPerTableCFReplication.tabC);
            Table htab1A = connection1.getTable(TestPerTableCFReplication.tabAName);
            Table htab2A = connection2.getTable(TestPerTableCFReplication.tabAName);
            Table htab3A = connection3.getTable(TestPerTableCFReplication.tabAName);
            Table htab1B = connection1.getTable(TestPerTableCFReplication.tabBName);
            Table htab2B = connection2.getTable(TestPerTableCFReplication.tabBName);
            Table htab3B = connection3.getTable(TestPerTableCFReplication.tabBName);
            Table htab1C = connection1.getTable(TestPerTableCFReplication.tabCName);
            Table htab2C = connection2.getTable(TestPerTableCFReplication.tabCName);
            Table htab3C = connection3.getTable(TestPerTableCFReplication.tabCName);
            // A. add cluster2/cluster3 as peers to cluster1
            ReplicationPeerConfig rpc2 = new ReplicationPeerConfig();
            rpc2.setClusterKey(TestPerTableCFReplication.utility2.getClusterKey());
            rpc2.setReplicateAllUserTables(false);
            Map<TableName, List<String>> tableCFs = new HashMap<>();
            tableCFs.put(TestPerTableCFReplication.tabCName, null);
            tableCFs.put(TestPerTableCFReplication.tabBName, new ArrayList());
            tableCFs.get(TestPerTableCFReplication.tabBName).add("f1");
            tableCFs.get(TestPerTableCFReplication.tabBName).add("f3");
            replicationAdmin.addPeer("2", rpc2, tableCFs);
            ReplicationPeerConfig rpc3 = new ReplicationPeerConfig();
            rpc3.setClusterKey(TestPerTableCFReplication.utility3.getClusterKey());
            rpc3.setReplicateAllUserTables(false);
            tableCFs.clear();
            tableCFs.put(TestPerTableCFReplication.tabAName, null);
            tableCFs.put(TestPerTableCFReplication.tabBName, new ArrayList());
            tableCFs.get(TestPerTableCFReplication.tabBName).add("f1");
            tableCFs.get(TestPerTableCFReplication.tabBName).add("f2");
            replicationAdmin.addPeer("3", rpc3, tableCFs);
            // A1. tableA can only replicated to cluster3
            putAndWaitWithFamily(TestPerTableCFReplication.row1, TestPerTableCFReplication.f1Name, htab1A, htab3A);
            ensureRowNotReplicated(TestPerTableCFReplication.row1, TestPerTableCFReplication.f1Name, htab2A);
            deleteAndWaitWithFamily(TestPerTableCFReplication.row1, TestPerTableCFReplication.f1Name, htab1A, htab3A);
            putAndWaitWithFamily(TestPerTableCFReplication.row1, TestPerTableCFReplication.f2Name, htab1A, htab3A);
            ensureRowNotReplicated(TestPerTableCFReplication.row1, TestPerTableCFReplication.f2Name, htab2A);
            deleteAndWaitWithFamily(TestPerTableCFReplication.row1, TestPerTableCFReplication.f2Name, htab1A, htab3A);
            putAndWaitWithFamily(TestPerTableCFReplication.row1, TestPerTableCFReplication.f3Name, htab1A, htab3A);
            ensureRowNotReplicated(TestPerTableCFReplication.row1, TestPerTableCFReplication.f3Name, htab2A);
            deleteAndWaitWithFamily(TestPerTableCFReplication.row1, TestPerTableCFReplication.f3Name, htab1A, htab3A);
            // A2. cf 'f1' of tableB can replicated to both cluster2 and cluster3
            putAndWaitWithFamily(TestPerTableCFReplication.row1, TestPerTableCFReplication.f1Name, htab1B, htab2B, htab3B);
            deleteAndWaitWithFamily(TestPerTableCFReplication.row1, TestPerTableCFReplication.f1Name, htab1B, htab2B, htab3B);
            // cf 'f2' of tableB can only replicated to cluster3
            putAndWaitWithFamily(TestPerTableCFReplication.row1, TestPerTableCFReplication.f2Name, htab1B, htab3B);
            ensureRowNotReplicated(TestPerTableCFReplication.row1, TestPerTableCFReplication.f2Name, htab2B);
            deleteAndWaitWithFamily(TestPerTableCFReplication.row1, TestPerTableCFReplication.f2Name, htab1B, htab3B);
            // cf 'f3' of tableB can only replicated to cluster2
            putAndWaitWithFamily(TestPerTableCFReplication.row1, TestPerTableCFReplication.f3Name, htab1B, htab2B);
            ensureRowNotReplicated(TestPerTableCFReplication.row1, TestPerTableCFReplication.f3Name, htab3B);
            deleteAndWaitWithFamily(TestPerTableCFReplication.row1, TestPerTableCFReplication.f3Name, htab1B, htab2B);
            // A3. tableC can only replicated to cluster2
            putAndWaitWithFamily(TestPerTableCFReplication.row1, TestPerTableCFReplication.f1Name, htab1C, htab2C);
            ensureRowNotReplicated(TestPerTableCFReplication.row1, TestPerTableCFReplication.f1Name, htab3C);
            deleteAndWaitWithFamily(TestPerTableCFReplication.row1, TestPerTableCFReplication.f1Name, htab1C, htab2C);
            putAndWaitWithFamily(TestPerTableCFReplication.row1, TestPerTableCFReplication.f2Name, htab1C, htab2C);
            ensureRowNotReplicated(TestPerTableCFReplication.row1, TestPerTableCFReplication.f2Name, htab3C);
            deleteAndWaitWithFamily(TestPerTableCFReplication.row1, TestPerTableCFReplication.f2Name, htab1C, htab2C);
            putAndWaitWithFamily(TestPerTableCFReplication.row1, TestPerTableCFReplication.f3Name, htab1C, htab2C);
            ensureRowNotReplicated(TestPerTableCFReplication.row1, TestPerTableCFReplication.f3Name, htab3C);
            deleteAndWaitWithFamily(TestPerTableCFReplication.row1, TestPerTableCFReplication.f3Name, htab1C, htab2C);
            // B. change peers' replicable table-cf config
            tableCFs.clear();
            tableCFs.put(TestPerTableCFReplication.tabAName, new ArrayList());
            tableCFs.get(TestPerTableCFReplication.tabAName).add("f1");
            tableCFs.get(TestPerTableCFReplication.tabAName).add("f2");
            tableCFs.put(TestPerTableCFReplication.tabCName, new ArrayList());
            tableCFs.get(TestPerTableCFReplication.tabCName).add("f2");
            tableCFs.get(TestPerTableCFReplication.tabCName).add("f3");
            replicationAdmin.setPeerTableCFs("2", tableCFs);
            tableCFs.clear();
            tableCFs.put(TestPerTableCFReplication.tabBName, null);
            tableCFs.put(TestPerTableCFReplication.tabCName, new ArrayList());
            tableCFs.get(TestPerTableCFReplication.tabCName).add("f3");
            replicationAdmin.setPeerTableCFs("3", tableCFs);
            // B1. cf 'f1' of tableA can only replicated to cluster2
            putAndWaitWithFamily(TestPerTableCFReplication.row2, TestPerTableCFReplication.f1Name, htab1A, htab2A);
            ensureRowNotReplicated(TestPerTableCFReplication.row2, TestPerTableCFReplication.f1Name, htab3A);
            deleteAndWaitWithFamily(TestPerTableCFReplication.row2, TestPerTableCFReplication.f1Name, htab1A, htab2A);
            // cf 'f2' of tableA can only replicated to cluster2
            putAndWaitWithFamily(TestPerTableCFReplication.row2, TestPerTableCFReplication.f2Name, htab1A, htab2A);
            ensureRowNotReplicated(TestPerTableCFReplication.row2, TestPerTableCFReplication.f2Name, htab3A);
            deleteAndWaitWithFamily(TestPerTableCFReplication.row2, TestPerTableCFReplication.f2Name, htab1A, htab2A);
            // cf 'f3' of tableA isn't replicable to either cluster2 or cluster3
            putAndWaitWithFamily(TestPerTableCFReplication.row2, TestPerTableCFReplication.f3Name, htab1A);
            ensureRowNotReplicated(TestPerTableCFReplication.row2, TestPerTableCFReplication.f3Name, htab2A, htab3A);
            deleteAndWaitWithFamily(TestPerTableCFReplication.row2, TestPerTableCFReplication.f3Name, htab1A);
            // B2. tableB can only replicated to cluster3
            putAndWaitWithFamily(TestPerTableCFReplication.row2, TestPerTableCFReplication.f1Name, htab1B, htab3B);
            ensureRowNotReplicated(TestPerTableCFReplication.row2, TestPerTableCFReplication.f1Name, htab2B);
            deleteAndWaitWithFamily(TestPerTableCFReplication.row2, TestPerTableCFReplication.f1Name, htab1B, htab3B);
            putAndWaitWithFamily(TestPerTableCFReplication.row2, TestPerTableCFReplication.f2Name, htab1B, htab3B);
            ensureRowNotReplicated(TestPerTableCFReplication.row2, TestPerTableCFReplication.f2Name, htab2B);
            deleteAndWaitWithFamily(TestPerTableCFReplication.row2, TestPerTableCFReplication.f2Name, htab1B, htab3B);
            putAndWaitWithFamily(TestPerTableCFReplication.row2, TestPerTableCFReplication.f3Name, htab1B, htab3B);
            ensureRowNotReplicated(TestPerTableCFReplication.row2, TestPerTableCFReplication.f3Name, htab2B);
            deleteAndWaitWithFamily(TestPerTableCFReplication.row2, TestPerTableCFReplication.f3Name, htab1B, htab3B);
            // B3. cf 'f1' of tableC non-replicable to either cluster
            putAndWaitWithFamily(TestPerTableCFReplication.row2, TestPerTableCFReplication.f1Name, htab1C);
            ensureRowNotReplicated(TestPerTableCFReplication.row2, TestPerTableCFReplication.f1Name, htab2C, htab3C);
            deleteAndWaitWithFamily(TestPerTableCFReplication.row2, TestPerTableCFReplication.f1Name, htab1C);
            // cf 'f2' of tableC can only replicated to cluster2
            putAndWaitWithFamily(TestPerTableCFReplication.row2, TestPerTableCFReplication.f2Name, htab1C, htab2C);
            ensureRowNotReplicated(TestPerTableCFReplication.row2, TestPerTableCFReplication.f2Name, htab3C);
            deleteAndWaitWithFamily(TestPerTableCFReplication.row2, TestPerTableCFReplication.f2Name, htab1C, htab2C);
            // cf 'f3' of tableC can replicated to cluster2 and cluster3
            putAndWaitWithFamily(TestPerTableCFReplication.row2, TestPerTableCFReplication.f3Name, htab1C, htab2C, htab3C);
            deleteAndWaitWithFamily(TestPerTableCFReplication.row2, TestPerTableCFReplication.f3Name, htab1C, htab2C, htab3C);
        } finally {
            connection1.close();
            connection2.close();
            connection3.close();
        }
    }
}

