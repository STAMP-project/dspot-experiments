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


import HConstants.REPLICATION_SCOPE_GLOBAL;
import HConstants.REPLICATION_SCOPE_LOCAL;
import RegionInfoBuilder.FIRST_META_REGIONINFO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.WAL.Entry;
import org.apache.hadoop.hbase.wal.WALKeyImpl;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category({ ReplicationTests.class, SmallTests.class })
public class TestReplicationWALEntryFilters {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestReplicationWALEntryFilters.class);

    static byte[] a = new byte[]{ 'a' };

    static byte[] b = new byte[]{ 'b' };

    static byte[] c = new byte[]{ 'c' };

    static byte[] d = new byte[]{ 'd' };

    @Test
    public void testSystemTableWALEntryFilter() {
        SystemTableWALEntryFilter filter = new SystemTableWALEntryFilter();
        // meta
        WALKeyImpl key1 = new WALKeyImpl(FIRST_META_REGIONINFO.getEncodedNameAsBytes(), TableName.META_TABLE_NAME, System.currentTimeMillis());
        Entry metaEntry = new Entry(key1, null);
        Assert.assertNull(filter.filter(metaEntry));
        // user table
        WALKeyImpl key3 = new WALKeyImpl(new byte[0], TableName.valueOf("foo"), System.currentTimeMillis());
        Entry userEntry = new Entry(key3, null);
        assertEquals(userEntry, filter.filter(userEntry));
    }

    @Test
    public void testScopeWALEntryFilter() {
        WALEntryFilter filter = new ChainWALEntryFilter(new ScopeWALEntryFilter());
        Entry userEntry = createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b);
        Entry userEntryA = createEntry(null, TestReplicationWALEntryFilters.a);
        Entry userEntryB = createEntry(null, TestReplicationWALEntryFilters.b);
        Entry userEntryEmpty = createEntry(null);
        // no scopes
        // now we will not filter out entries without a replication scope since serial replication still
        // need the sequence id, but the cells will all be filtered out.
        Assert.assertTrue(filter.filter(userEntry).getEdit().isEmpty());
        // empty scopes
        // ditto
        TreeMap<byte[], Integer> scopes = new TreeMap(Bytes.BYTES_COMPARATOR);
        userEntry = createEntry(scopes, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b);
        Assert.assertTrue(filter.filter(userEntry).getEdit().isEmpty());
        // different scope
        scopes = new TreeMap(Bytes.BYTES_COMPARATOR);
        scopes.put(TestReplicationWALEntryFilters.c, REPLICATION_SCOPE_GLOBAL);
        userEntry = createEntry(scopes, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b);
        // all kvs should be filtered
        assertEquals(userEntryEmpty, filter.filter(userEntry));
        // local scope
        scopes = new TreeMap(Bytes.BYTES_COMPARATOR);
        scopes.put(TestReplicationWALEntryFilters.a, REPLICATION_SCOPE_LOCAL);
        userEntry = createEntry(scopes, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b);
        assertEquals(userEntryEmpty, filter.filter(userEntry));
        scopes.put(TestReplicationWALEntryFilters.b, REPLICATION_SCOPE_LOCAL);
        assertEquals(userEntryEmpty, filter.filter(userEntry));
        // only scope a
        scopes = new TreeMap(Bytes.BYTES_COMPARATOR);
        scopes.put(TestReplicationWALEntryFilters.a, REPLICATION_SCOPE_GLOBAL);
        userEntry = createEntry(scopes, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b);
        assertEquals(userEntryA, filter.filter(userEntry));
        scopes.put(TestReplicationWALEntryFilters.b, REPLICATION_SCOPE_LOCAL);
        assertEquals(userEntryA, filter.filter(userEntry));
        // only scope b
        scopes = new TreeMap(Bytes.BYTES_COMPARATOR);
        scopes.put(TestReplicationWALEntryFilters.b, REPLICATION_SCOPE_GLOBAL);
        userEntry = createEntry(scopes, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b);
        assertEquals(userEntryB, filter.filter(userEntry));
        scopes.put(TestReplicationWALEntryFilters.a, REPLICATION_SCOPE_LOCAL);
        assertEquals(userEntryB, filter.filter(userEntry));
        // scope a and b
        scopes = new TreeMap(Bytes.BYTES_COMPARATOR);
        scopes.put(TestReplicationWALEntryFilters.b, REPLICATION_SCOPE_GLOBAL);
        userEntry = createEntry(scopes, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b);
        assertEquals(userEntryB, filter.filter(userEntry));
        scopes.put(TestReplicationWALEntryFilters.a, REPLICATION_SCOPE_LOCAL);
        assertEquals(userEntryB, filter.filter(userEntry));
    }

    WALEntryFilter nullFilter = new WALEntryFilter() {
        @Override
        public Entry filter(Entry entry) {
            return null;
        }
    };

    WALEntryFilter passFilter = new WALEntryFilter() {
        @Override
        public Entry filter(Entry entry) {
            return entry;
        }
    };

    @Test
    public void testChainWALEntryFilter() {
        Entry userEntry = createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c);
        ChainWALEntryFilter filter = new ChainWALEntryFilter(passFilter);
        assertEquals(createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c), filter.filter(userEntry));
        filter = new ChainWALEntryFilter(passFilter, passFilter);
        assertEquals(createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c), filter.filter(userEntry));
        filter = new ChainWALEntryFilter(passFilter, passFilter, passFilter);
        assertEquals(createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c), filter.filter(userEntry));
        filter = new ChainWALEntryFilter(nullFilter);
        assertEquals(null, filter.filter(userEntry));
        filter = new ChainWALEntryFilter(nullFilter, passFilter);
        assertEquals(null, filter.filter(userEntry));
        filter = new ChainWALEntryFilter(passFilter, nullFilter);
        assertEquals(null, filter.filter(userEntry));
        filter = new ChainWALEntryFilter(nullFilter, passFilter, nullFilter);
        assertEquals(null, filter.filter(userEntry));
        filter = new ChainWALEntryFilter(nullFilter, nullFilter);
        assertEquals(null, filter.filter(userEntry));
        // flatten
        filter = new ChainWALEntryFilter(new ChainWALEntryFilter(passFilter, new ChainWALEntryFilter(passFilter, passFilter), new ChainWALEntryFilter(passFilter), new ChainWALEntryFilter(passFilter)), new ChainWALEntryFilter(passFilter));
        assertEquals(createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c), filter.filter(userEntry));
        filter = new ChainWALEntryFilter(new ChainWALEntryFilter(passFilter, new ChainWALEntryFilter(passFilter, new ChainWALEntryFilter(nullFilter))), new ChainWALEntryFilter(passFilter));
        assertEquals(null, filter.filter(userEntry));
    }

    @Test
    public void testNamespaceTableCfWALEntryFilter() {
        ReplicationPeer peer = Mockito.mock(ReplicationPeer.class);
        ReplicationPeerConfig peerConfig = Mockito.mock(ReplicationPeerConfig.class);
        // 1. replicate_all flag is false, no namespaces and table-cfs config
        Mockito.when(peerConfig.replicateAllUserTables()).thenReturn(false);
        Mockito.when(peerConfig.getNamespaces()).thenReturn(null);
        Mockito.when(peerConfig.getTableCFsMap()).thenReturn(null);
        Mockito.when(peer.getPeerConfig()).thenReturn(peerConfig);
        Entry userEntry = createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c);
        ChainWALEntryFilter filter = new ChainWALEntryFilter(new NamespaceTableCfWALEntryFilter(peer));
        assertEquals(null, filter.filter(userEntry));
        // 2. replicate_all flag is false, and only config table-cfs in peer
        // empty map
        userEntry = createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c);
        Map<TableName, List<String>> tableCfs = new HashMap<>();
        Mockito.when(peerConfig.replicateAllUserTables()).thenReturn(false);
        Mockito.when(peerConfig.getTableCFsMap()).thenReturn(tableCfs);
        Mockito.when(peer.getPeerConfig()).thenReturn(peerConfig);
        filter = new ChainWALEntryFilter(new NamespaceTableCfWALEntryFilter(peer));
        assertEquals(null, filter.filter(userEntry));
        // table bar
        userEntry = createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c);
        tableCfs = new HashMap();
        tableCfs.put(TableName.valueOf("bar"), null);
        Mockito.when(peerConfig.replicateAllUserTables()).thenReturn(false);
        Mockito.when(peerConfig.getTableCFsMap()).thenReturn(tableCfs);
        Mockito.when(peer.getPeerConfig()).thenReturn(peerConfig);
        filter = new ChainWALEntryFilter(new NamespaceTableCfWALEntryFilter(peer));
        assertEquals(null, filter.filter(userEntry));
        // table foo:a
        userEntry = createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c);
        tableCfs = new HashMap();
        tableCfs.put(TableName.valueOf("foo"), Lists.newArrayList("a"));
        Mockito.when(peerConfig.replicateAllUserTables()).thenReturn(false);
        Mockito.when(peerConfig.getTableCFsMap()).thenReturn(tableCfs);
        Mockito.when(peer.getPeerConfig()).thenReturn(peerConfig);
        filter = new ChainWALEntryFilter(new NamespaceTableCfWALEntryFilter(peer));
        assertEquals(createEntry(null, TestReplicationWALEntryFilters.a), filter.filter(userEntry));
        // table foo:a,c
        userEntry = createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c, TestReplicationWALEntryFilters.d);
        tableCfs = new HashMap();
        tableCfs.put(TableName.valueOf("foo"), Lists.newArrayList("a", "c"));
        Mockito.when(peerConfig.replicateAllUserTables()).thenReturn(false);
        Mockito.when(peerConfig.getTableCFsMap()).thenReturn(tableCfs);
        Mockito.when(peer.getPeerConfig()).thenReturn(peerConfig);
        filter = new ChainWALEntryFilter(new NamespaceTableCfWALEntryFilter(peer));
        assertEquals(createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.c), filter.filter(userEntry));
        // 3. replicate_all flag is false, and only config namespaces in peer
        Mockito.when(peer.getTableCFs()).thenReturn(null);
        // empty set
        Set<String> namespaces = new HashSet<>();
        Mockito.when(peerConfig.replicateAllUserTables()).thenReturn(false);
        Mockito.when(peerConfig.getNamespaces()).thenReturn(namespaces);
        Mockito.when(peerConfig.getTableCFsMap()).thenReturn(null);
        Mockito.when(peer.getPeerConfig()).thenReturn(peerConfig);
        userEntry = createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c);
        filter = new ChainWALEntryFilter(new NamespaceTableCfWALEntryFilter(peer));
        assertEquals(null, filter.filter(userEntry));
        // namespace default
        namespaces.add("default");
        Mockito.when(peerConfig.replicateAllUserTables()).thenReturn(false);
        Mockito.when(peerConfig.getNamespaces()).thenReturn(namespaces);
        Mockito.when(peer.getPeerConfig()).thenReturn(peerConfig);
        userEntry = createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c);
        filter = new ChainWALEntryFilter(new NamespaceTableCfWALEntryFilter(peer));
        assertEquals(createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c), filter.filter(userEntry));
        // namespace ns1
        namespaces = new HashSet<>();
        namespaces.add("ns1");
        Mockito.when(peerConfig.replicateAllUserTables()).thenReturn(false);
        Mockito.when(peerConfig.getNamespaces()).thenReturn(namespaces);
        Mockito.when(peer.getPeerConfig()).thenReturn(peerConfig);
        userEntry = createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c);
        filter = new ChainWALEntryFilter(new NamespaceTableCfWALEntryFilter(peer));
        assertEquals(null, filter.filter(userEntry));
        // 4. replicate_all flag is false, and config namespaces and table-cfs both
        // Namespaces config should not confict with table-cfs config
        namespaces = new HashSet<>();
        tableCfs = new HashMap();
        namespaces.add("ns1");
        tableCfs.put(TableName.valueOf("foo"), Lists.newArrayList("a", "c"));
        Mockito.when(peerConfig.replicateAllUserTables()).thenReturn(false);
        Mockito.when(peerConfig.getNamespaces()).thenReturn(namespaces);
        Mockito.when(peerConfig.getTableCFsMap()).thenReturn(tableCfs);
        Mockito.when(peer.getPeerConfig()).thenReturn(peerConfig);
        userEntry = createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c);
        filter = new ChainWALEntryFilter(new NamespaceTableCfWALEntryFilter(peer));
        assertEquals(createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.c), filter.filter(userEntry));
        namespaces = new HashSet<>();
        tableCfs = new HashMap();
        namespaces.add("default");
        tableCfs.put(TableName.valueOf("ns1:foo"), Lists.newArrayList("a", "c"));
        Mockito.when(peerConfig.replicateAllUserTables()).thenReturn(false);
        Mockito.when(peerConfig.getNamespaces()).thenReturn(namespaces);
        Mockito.when(peerConfig.getTableCFsMap()).thenReturn(tableCfs);
        Mockito.when(peer.getPeerConfig()).thenReturn(peerConfig);
        userEntry = createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c);
        filter = new ChainWALEntryFilter(new NamespaceTableCfWALEntryFilter(peer));
        assertEquals(createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c), filter.filter(userEntry));
        namespaces = new HashSet<>();
        tableCfs = new HashMap();
        namespaces.add("ns1");
        tableCfs.put(TableName.valueOf("bar"), null);
        Mockito.when(peerConfig.replicateAllUserTables()).thenReturn(false);
        Mockito.when(peerConfig.getNamespaces()).thenReturn(namespaces);
        Mockito.when(peerConfig.getTableCFsMap()).thenReturn(tableCfs);
        Mockito.when(peer.getPeerConfig()).thenReturn(peerConfig);
        userEntry = createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c);
        filter = new ChainWALEntryFilter(new NamespaceTableCfWALEntryFilter(peer));
        assertEquals(null, filter.filter(userEntry));
    }

    @Test
    public void testNamespaceTableCfWALEntryFilter2() {
        ReplicationPeer peer = Mockito.mock(ReplicationPeer.class);
        ReplicationPeerConfig peerConfig = Mockito.mock(ReplicationPeerConfig.class);
        // 1. replicate_all flag is true
        // and no exclude namespaces and no exclude table-cfs config
        Mockito.when(peerConfig.replicateAllUserTables()).thenReturn(true);
        Mockito.when(peerConfig.getExcludeNamespaces()).thenReturn(null);
        Mockito.when(peerConfig.getExcludeTableCFsMap()).thenReturn(null);
        Mockito.when(peer.getPeerConfig()).thenReturn(peerConfig);
        Entry userEntry = createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c);
        ChainWALEntryFilter filter = new ChainWALEntryFilter(new NamespaceTableCfWALEntryFilter(peer));
        assertEquals(createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c), filter.filter(userEntry));
        // 2. replicate_all flag is true, and only config exclude namespaces
        // empty set
        Set<String> namespaces = new HashSet<String>();
        Mockito.when(peerConfig.getExcludeNamespaces()).thenReturn(namespaces);
        Mockito.when(peerConfig.getExcludeTableCFsMap()).thenReturn(null);
        Mockito.when(peer.getPeerConfig()).thenReturn(peerConfig);
        userEntry = createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c);
        filter = new ChainWALEntryFilter(new NamespaceTableCfWALEntryFilter(peer));
        assertEquals(createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c), filter.filter(userEntry));
        // exclude namespace default
        namespaces.add("default");
        Mockito.when(peerConfig.getExcludeNamespaces()).thenReturn(namespaces);
        Mockito.when(peerConfig.getExcludeTableCFsMap()).thenReturn(null);
        Mockito.when(peer.getPeerConfig()).thenReturn(peerConfig);
        userEntry = createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c);
        filter = new ChainWALEntryFilter(new NamespaceTableCfWALEntryFilter(peer));
        assertEquals(null, filter.filter(userEntry));
        // exclude namespace ns1
        namespaces = new HashSet<String>();
        namespaces.add("ns1");
        Mockito.when(peerConfig.getExcludeNamespaces()).thenReturn(namespaces);
        Mockito.when(peerConfig.getExcludeTableCFsMap()).thenReturn(null);
        Mockito.when(peer.getPeerConfig()).thenReturn(peerConfig);
        userEntry = createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c);
        filter = new ChainWALEntryFilter(new NamespaceTableCfWALEntryFilter(peer));
        assertEquals(createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c), filter.filter(userEntry));
        // 3. replicate_all flag is true, and only config exclude table-cfs
        // empty table-cfs map
        Map<TableName, List<String>> tableCfs = new HashMap<TableName, List<String>>();
        Mockito.when(peerConfig.getExcludeNamespaces()).thenReturn(null);
        Mockito.when(peerConfig.getExcludeTableCFsMap()).thenReturn(tableCfs);
        Mockito.when(peer.getPeerConfig()).thenReturn(peerConfig);
        userEntry = createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c);
        filter = new ChainWALEntryFilter(new NamespaceTableCfWALEntryFilter(peer));
        assertEquals(createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c), filter.filter(userEntry));
        // exclude table bar
        tableCfs = new HashMap<TableName, List<String>>();
        tableCfs.put(TableName.valueOf("bar"), null);
        Mockito.when(peerConfig.getExcludeNamespaces()).thenReturn(null);
        Mockito.when(peerConfig.getExcludeTableCFsMap()).thenReturn(tableCfs);
        Mockito.when(peer.getPeerConfig()).thenReturn(peerConfig);
        userEntry = createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c);
        filter = new ChainWALEntryFilter(new NamespaceTableCfWALEntryFilter(peer));
        assertEquals(createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c), filter.filter(userEntry));
        // exclude table foo:a
        tableCfs = new HashMap<TableName, List<String>>();
        tableCfs.put(TableName.valueOf("foo"), Lists.newArrayList("a"));
        Mockito.when(peerConfig.getExcludeNamespaces()).thenReturn(null);
        Mockito.when(peerConfig.getExcludeTableCFsMap()).thenReturn(tableCfs);
        Mockito.when(peer.getPeerConfig()).thenReturn(peerConfig);
        userEntry = createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c);
        filter = new ChainWALEntryFilter(new NamespaceTableCfWALEntryFilter(peer));
        assertEquals(createEntry(null, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c), filter.filter(userEntry));
        // 4. replicate_all flag is true, and config exclude namespaces and table-cfs both
        // exclude ns1 and table foo:a,c
        namespaces = new HashSet<String>();
        tableCfs = new HashMap<TableName, List<String>>();
        namespaces.add("ns1");
        tableCfs.put(TableName.valueOf("foo"), Lists.newArrayList("a", "c"));
        Mockito.when(peerConfig.getExcludeNamespaces()).thenReturn(namespaces);
        Mockito.when(peerConfig.getExcludeTableCFsMap()).thenReturn(tableCfs);
        Mockito.when(peer.getPeerConfig()).thenReturn(peerConfig);
        userEntry = createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c);
        filter = new ChainWALEntryFilter(new NamespaceTableCfWALEntryFilter(peer));
        assertEquals(createEntry(null, TestReplicationWALEntryFilters.b), filter.filter(userEntry));
        // exclude namespace default and table ns1:bar
        namespaces = new HashSet<String>();
        tableCfs = new HashMap<TableName, List<String>>();
        namespaces.add("default");
        tableCfs.put(TableName.valueOf("ns1:bar"), new ArrayList<String>());
        Mockito.when(peerConfig.getExcludeNamespaces()).thenReturn(namespaces);
        Mockito.when(peerConfig.getExcludeTableCFsMap()).thenReturn(tableCfs);
        Mockito.when(peer.getPeerConfig()).thenReturn(peerConfig);
        userEntry = createEntry(null, TestReplicationWALEntryFilters.a, TestReplicationWALEntryFilters.b, TestReplicationWALEntryFilters.c);
        filter = new ChainWALEntryFilter(new NamespaceTableCfWALEntryFilter(peer));
        assertEquals(null, filter.filter(userEntry));
    }
}

