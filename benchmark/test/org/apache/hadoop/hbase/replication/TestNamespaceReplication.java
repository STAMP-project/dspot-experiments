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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
@Category({ MediumTests.class })
public class TestNamespaceReplication extends TestReplicationBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestNamespaceReplication.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestNamespaceReplication.class);

    private static String ns1 = "ns1";

    private static String ns2 = "ns2";

    private static final TableName tabAName = TableName.valueOf("ns1:TA");

    private static final TableName tabBName = TableName.valueOf("ns2:TB");

    private static final byte[] f1Name = Bytes.toBytes("f1");

    private static final byte[] f2Name = Bytes.toBytes("f2");

    private static final byte[] val = Bytes.toBytes("myval");

    private static Connection connection1;

    private static Connection connection2;

    private static Admin admin1;

    private static Admin admin2;

    @Parameterized.Parameter
    public boolean serialPeer;

    @Test
    public void testNamespaceReplication() throws Exception {
        String peerId = "2";
        Table htab1A = TestNamespaceReplication.connection1.getTable(TestNamespaceReplication.tabAName);
        Table htab2A = TestNamespaceReplication.connection2.getTable(TestNamespaceReplication.tabAName);
        Table htab1B = TestNamespaceReplication.connection1.getTable(TestNamespaceReplication.tabBName);
        Table htab2B = TestNamespaceReplication.connection2.getTable(TestNamespaceReplication.tabBName);
        ReplicationPeerConfig rpc = TestNamespaceReplication.admin1.getReplicationPeerConfig(peerId);
        TestNamespaceReplication.admin1.updateReplicationPeerConfig(peerId, ReplicationPeerConfig.newBuilder(rpc).setReplicateAllUserTables(false).build());
        // add ns1 to peer config which replicate to cluster2
        rpc = TestNamespaceReplication.admin1.getReplicationPeerConfig(peerId);
        Set<String> namespaces = new HashSet<>();
        namespaces.add(TestNamespaceReplication.ns1);
        TestNamespaceReplication.admin1.updateReplicationPeerConfig(peerId, ReplicationPeerConfig.newBuilder(rpc).setNamespaces(namespaces).build());
        TestNamespaceReplication.LOG.info("update peer config");
        // Table A can be replicated to cluster2
        put(htab1A, TestReplicationBase.row, TestNamespaceReplication.f1Name, TestNamespaceReplication.f2Name);
        ensureRowExisted(htab2A, TestReplicationBase.row, TestNamespaceReplication.f1Name, TestNamespaceReplication.f2Name);
        delete(htab1A, TestReplicationBase.row, TestNamespaceReplication.f1Name, TestNamespaceReplication.f2Name);
        ensureRowNotExisted(htab2A, TestReplicationBase.row, TestNamespaceReplication.f1Name, TestNamespaceReplication.f2Name);
        // Table B can not be replicated to cluster2
        put(htab1B, TestReplicationBase.row, TestNamespaceReplication.f1Name, TestNamespaceReplication.f2Name);
        ensureRowNotExisted(htab2B, TestReplicationBase.row, TestNamespaceReplication.f1Name, TestNamespaceReplication.f2Name);
        // add ns1:TA => 'f1' and ns2 to peer config which replicate to cluster2
        rpc = TestNamespaceReplication.admin1.getReplicationPeerConfig(peerId);
        namespaces = new HashSet<>();
        namespaces.add(TestNamespaceReplication.ns2);
        Map<TableName, List<String>> tableCfs = new HashMap<>();
        tableCfs.put(TestNamespaceReplication.tabAName, new ArrayList());
        tableCfs.get(TestNamespaceReplication.tabAName).add("f1");
        TestNamespaceReplication.admin1.updateReplicationPeerConfig(peerId, ReplicationPeerConfig.newBuilder(rpc).setNamespaces(namespaces).setTableCFsMap(tableCfs).build());
        TestNamespaceReplication.LOG.info("update peer config");
        // Only family f1 of Table A can replicated to cluster2
        put(htab1A, TestReplicationBase.row, TestNamespaceReplication.f1Name, TestNamespaceReplication.f2Name);
        ensureRowExisted(htab2A, TestReplicationBase.row, TestNamespaceReplication.f1Name);
        delete(htab1A, TestReplicationBase.row, TestNamespaceReplication.f1Name, TestNamespaceReplication.f2Name);
        ensureRowNotExisted(htab2A, TestReplicationBase.row, TestNamespaceReplication.f1Name);
        // All cfs of table B can replicated to cluster2
        put(htab1B, TestReplicationBase.row, TestNamespaceReplication.f1Name, TestNamespaceReplication.f2Name);
        ensureRowExisted(htab2B, TestReplicationBase.row, TestNamespaceReplication.f1Name, TestNamespaceReplication.f2Name);
        delete(htab1B, TestReplicationBase.row, TestNamespaceReplication.f1Name, TestNamespaceReplication.f2Name);
        ensureRowNotExisted(htab2B, TestReplicationBase.row, TestNamespaceReplication.f1Name, TestNamespaceReplication.f2Name);
        TestNamespaceReplication.admin1.removeReplicationPeer(peerId);
    }
}

