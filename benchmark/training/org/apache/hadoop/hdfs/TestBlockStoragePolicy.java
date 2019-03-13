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
package org.apache.hadoop.hdfs;


import DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY;
import DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY;
import DFSConfigKeys.DFS_NAMENODE_NAME_DIR_KEY;
import DFSConfigKeys.DFS_NAMENODE_REDUNDANCY_INTERVAL_SECONDS_KEY;
import DFSConfigKeys.DFS_STORAGE_POLICY_ENABLED_KEY;
import DatanodeStorage.State;
import HdfsConstants.COLD_STORAGE_POLICY_NAME;
import HdfsConstants.HOT_STORAGE_POLICY_NAME;
import HdfsConstants.WARM_STORAGE_POLICY_NAME;
import HdfsFileStatus.EMPTY_NAME;
import SafeModeAction.SAFEMODE_ENTER;
import SafeModeAction.SAFEMODE_LEAVE;
import StorageType.ARCHIVE;
import StorageType.DISK;
import StorageType.EMPTY_ARRAY;
import StorageType.RAM_DISK;
import StorageType.SSD;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockStoragePolicySpi;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManagerTestUtil;
import org.apache.hadoop.hdfs.server.blockmanagement.TestReplicationPolicy;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.hdfs.server.namenode.snapshot.SnapshotTestHelper;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.net.NetworkTopology;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.PathUtils;
import org.junit.Assert;
import org.junit.Test;

import static HdfsConstants.ALLSSD_STORAGE_POLICY_ID;
import static HdfsConstants.COLD_STORAGE_POLICY_ID;
import static HdfsConstants.HOT_STORAGE_POLICY_ID;
import static HdfsConstants.MEMORY_STORAGE_POLICY_ID;
import static HdfsConstants.ONESSD_STORAGE_POLICY_ID;
import static HdfsConstants.PROVIDED_STORAGE_POLICY_ID;
import static HdfsConstants.WARM_STORAGE_POLICY_ID;


/**
 * Test {@link BlockStoragePolicy}
 */
public class TestBlockStoragePolicy {
    public static final BlockStoragePolicySuite POLICY_SUITE;

    public static final BlockStoragePolicy DEFAULT_STORAGE_POLICY;

    public static final Configuration conf;

    static {
        conf = new HdfsConfiguration();
        TestBlockStoragePolicy.conf.setLong(DFS_HEARTBEAT_INTERVAL_KEY, 1);
        TestBlockStoragePolicy.conf.setInt(DFS_NAMENODE_REDUNDANCY_INTERVAL_SECONDS_KEY, 1);
        POLICY_SUITE = BlockStoragePolicySuite.createDefaultSuite();
        DEFAULT_STORAGE_POLICY = TestBlockStoragePolicy.POLICY_SUITE.getDefaultPolicy();
    }

    static final EnumSet<StorageType> none = EnumSet.noneOf(StorageType.class);

    static final EnumSet<StorageType> archive = EnumSet.of(ARCHIVE);

    static final EnumSet<StorageType> disk = EnumSet.of(DISK);

    static final EnumSet<StorageType> ssd = EnumSet.of(SSD);

    static final EnumSet<StorageType> disk_archive = EnumSet.of(DISK, ARCHIVE);

    static final EnumSet<StorageType> all = EnumSet.of(SSD, DISK, ARCHIVE);

    static final long FILE_LEN = 1024;

    static final short REPLICATION = 3;

    static final byte COLD = COLD_STORAGE_POLICY_ID;

    static final byte WARM = WARM_STORAGE_POLICY_ID;

    static final byte HOT = HOT_STORAGE_POLICY_ID;

    static final byte ONESSD = ONESSD_STORAGE_POLICY_ID;

    static final byte ALLSSD = ALLSSD_STORAGE_POLICY_ID;

    static final byte LAZY_PERSIST = MEMORY_STORAGE_POLICY_ID;

    static final byte PROVIDED = PROVIDED_STORAGE_POLICY_ID;

    @Test(timeout = 300000)
    public void testConfigKeyEnabled() throws IOException {
        Configuration conf = new HdfsConfiguration();
        conf.setBoolean(DFS_STORAGE_POLICY_ENABLED_KEY, true);
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
        try {
            cluster.waitActive();
            cluster.getFileSystem().setStoragePolicy(new Path("/"), COLD_STORAGE_POLICY_NAME);
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * Ensure that setStoragePolicy throws IOException when
     * dfs.storage.policy.enabled is set to false.
     *
     * @throws IOException
     * 		
     */
    @Test(timeout = 300000, expected = IOException.class)
    public void testConfigKeyDisabled() throws IOException {
        Configuration conf = new HdfsConfiguration();
        conf.setBoolean(DFS_STORAGE_POLICY_ENABLED_KEY, false);
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
        try {
            cluster.waitActive();
            cluster.getFileSystem().setStoragePolicy(new Path("/"), COLD_STORAGE_POLICY_NAME);
        } finally {
            cluster.shutdown();
        }
    }

    @Test
    public void testDefaultPolicies() {
        final Map<Byte, String> expectedPolicyStrings = new HashMap<Byte, String>();
        expectedPolicyStrings.put(TestBlockStoragePolicy.COLD, ((("BlockStoragePolicy{COLD:" + (TestBlockStoragePolicy.COLD)) + ", storageTypes=[ARCHIVE], ") + "creationFallbacks=[], replicationFallbacks=[]}"));
        expectedPolicyStrings.put(TestBlockStoragePolicy.WARM, (((("BlockStoragePolicy{WARM:" + (TestBlockStoragePolicy.WARM)) + ", storageTypes=[DISK, ARCHIVE], ") + "creationFallbacks=[DISK, ARCHIVE], ") + "replicationFallbacks=[DISK, ARCHIVE]}"));
        expectedPolicyStrings.put(TestBlockStoragePolicy.HOT, ((("BlockStoragePolicy{HOT:" + (TestBlockStoragePolicy.HOT)) + ", storageTypes=[DISK], ") + "creationFallbacks=[], replicationFallbacks=[ARCHIVE]}"));
        expectedPolicyStrings.put(TestBlockStoragePolicy.LAZY_PERSIST, ((("BlockStoragePolicy{LAZY_PERSIST:" + (TestBlockStoragePolicy.LAZY_PERSIST)) + ", storageTypes=[RAM_DISK, DISK], ") + "creationFallbacks=[DISK], replicationFallbacks=[DISK]}"));
        expectedPolicyStrings.put(TestBlockStoragePolicy.ONESSD, ((("BlockStoragePolicy{ONE_SSD:" + (TestBlockStoragePolicy.ONESSD)) + ", storageTypes=[SSD, DISK], creationFallbacks=[SSD, DISK], ") + "replicationFallbacks=[SSD, DISK]}"));
        expectedPolicyStrings.put(TestBlockStoragePolicy.ALLSSD, ((("BlockStoragePolicy{ALL_SSD:" + (TestBlockStoragePolicy.ALLSSD)) + ", storageTypes=[SSD], creationFallbacks=[DISK], ") + "replicationFallbacks=[DISK]}"));
        expectedPolicyStrings.put(TestBlockStoragePolicy.PROVIDED, (((("BlockStoragePolicy{PROVIDED:" + (TestBlockStoragePolicy.PROVIDED)) + ", storageTypes=[PROVIDED, DISK], ") + "creationFallbacks=[PROVIDED, DISK], ") + "replicationFallbacks=[PROVIDED, DISK]}"));
        for (byte i = 1; i < 16; i++) {
            final BlockStoragePolicy policy = TestBlockStoragePolicy.POLICY_SUITE.getPolicy(i);
            if (policy != null) {
                final String s = policy.toString();
                Assert.assertEquals(expectedPolicyStrings.get(i), s);
            }
        }
        Assert.assertEquals(TestBlockStoragePolicy.POLICY_SUITE.getPolicy(TestBlockStoragePolicy.HOT), TestBlockStoragePolicy.POLICY_SUITE.getDefaultPolicy());
        {
            // check Cold policy
            final BlockStoragePolicy cold = TestBlockStoragePolicy.POLICY_SUITE.getPolicy(TestBlockStoragePolicy.COLD);
            for (short replication = 1; replication < 6; replication++) {
                final List<StorageType> computed = cold.chooseStorageTypes(replication);
                TestBlockStoragePolicy.assertStorageType(computed, replication, ARCHIVE);
            }
            TestBlockStoragePolicy.assertCreationFallback(cold, null, null, null, null, null);
            TestBlockStoragePolicy.assertReplicationFallback(cold, null, null, null, null);
        }
        {
            // check Warm policy
            final BlockStoragePolicy warm = TestBlockStoragePolicy.POLICY_SUITE.getPolicy(TestBlockStoragePolicy.WARM);
            for (short replication = 1; replication < 6; replication++) {
                final List<StorageType> computed = warm.chooseStorageTypes(replication);
                TestBlockStoragePolicy.assertStorageType(computed, replication, DISK, ARCHIVE);
            }
            TestBlockStoragePolicy.assertCreationFallback(warm, DISK, DISK, ARCHIVE, DISK, null);
            TestBlockStoragePolicy.assertReplicationFallback(warm, DISK, DISK, ARCHIVE, DISK);
        }
        {
            // check Hot policy
            final BlockStoragePolicy hot = TestBlockStoragePolicy.POLICY_SUITE.getPolicy(TestBlockStoragePolicy.HOT);
            for (short replication = 1; replication < 6; replication++) {
                final List<StorageType> computed = hot.chooseStorageTypes(replication);
                TestBlockStoragePolicy.assertStorageType(computed, replication, DISK);
            }
            TestBlockStoragePolicy.assertCreationFallback(hot, null, null, null, null, null);
            TestBlockStoragePolicy.assertReplicationFallback(hot, ARCHIVE, null, ARCHIVE, ARCHIVE);
        }
        {
            // check ONE_SSD policy
            final BlockStoragePolicy onessd = TestBlockStoragePolicy.POLICY_SUITE.getPolicy(TestBlockStoragePolicy.ONESSD);
            for (short replication = 1; replication < 6; replication++) {
                final List<StorageType> computed = onessd.chooseStorageTypes(replication);
                TestBlockStoragePolicy.assertStorageType(computed, replication, SSD, DISK);
            }
            TestBlockStoragePolicy.assertCreationFallback(onessd, SSD, SSD, SSD, DISK, SSD);
            TestBlockStoragePolicy.assertReplicationFallback(onessd, SSD, SSD, SSD, DISK);
        }
        {
            // check ALL_SSD policy
            final BlockStoragePolicy allssd = TestBlockStoragePolicy.POLICY_SUITE.getPolicy(TestBlockStoragePolicy.ALLSSD);
            for (short replication = 1; replication < 6; replication++) {
                final List<StorageType> computed = allssd.chooseStorageTypes(replication);
                TestBlockStoragePolicy.assertStorageType(computed, replication, SSD);
            }
            TestBlockStoragePolicy.assertCreationFallback(allssd, DISK, DISK, null, DISK, null);
            TestBlockStoragePolicy.assertReplicationFallback(allssd, DISK, DISK, null, DISK);
        }
        {
            // check LAZY_PERSIST policy
            final BlockStoragePolicy lazyPersist = TestBlockStoragePolicy.POLICY_SUITE.getPolicy(TestBlockStoragePolicy.LAZY_PERSIST);
            for (short replication = 1; replication < 6; replication++) {
                final List<StorageType> computed = lazyPersist.chooseStorageTypes(replication);
                TestBlockStoragePolicy.assertStorageType(computed, replication, DISK);
            }
            TestBlockStoragePolicy.assertCreationFallback(lazyPersist, DISK, DISK, null, DISK, null);
            TestBlockStoragePolicy.assertReplicationFallback(lazyPersist, DISK, DISK, null, DISK);
        }
    }

    private static interface CheckChooseStorageTypes {
        public void checkChooseStorageTypes(BlockStoragePolicy p, short replication, List<StorageType> chosen, StorageType... expected);

        /**
         * Basic case: pass only replication and chosen
         */
        static final TestBlockStoragePolicy.CheckChooseStorageTypes Basic = new TestBlockStoragePolicy.CheckChooseStorageTypes() {
            @Override
            public void checkChooseStorageTypes(BlockStoragePolicy p, short replication, List<StorageType> chosen, StorageType... expected) {
                final List<StorageType> types = p.chooseStorageTypes(replication, chosen);
                TestBlockStoragePolicy.assertStorageTypes(types, expected);
            }
        };

        /**
         * With empty unavailables and isNewBlock=true
         */
        static final TestBlockStoragePolicy.CheckChooseStorageTypes EmptyUnavailablesAndNewBlock = new TestBlockStoragePolicy.CheckChooseStorageTypes() {
            @Override
            public void checkChooseStorageTypes(BlockStoragePolicy p, short replication, List<StorageType> chosen, StorageType... expected) {
                final List<StorageType> types = p.chooseStorageTypes(replication, chosen, TestBlockStoragePolicy.none, true);
                TestBlockStoragePolicy.assertStorageTypes(types, expected);
            }
        };

        /**
         * With empty unavailables and isNewBlock=false
         */
        static final TestBlockStoragePolicy.CheckChooseStorageTypes EmptyUnavailablesAndNonNewBlock = new TestBlockStoragePolicy.CheckChooseStorageTypes() {
            @Override
            public void checkChooseStorageTypes(BlockStoragePolicy p, short replication, List<StorageType> chosen, StorageType... expected) {
                final List<StorageType> types = p.chooseStorageTypes(replication, chosen, TestBlockStoragePolicy.none, false);
                TestBlockStoragePolicy.assertStorageTypes(types, expected);
            }
        };

        /**
         * With both DISK and ARCHIVE unavailables and isNewBlock=true
         */
        static final TestBlockStoragePolicy.CheckChooseStorageTypes BothUnavailableAndNewBlock = new TestBlockStoragePolicy.CheckChooseStorageTypes() {
            @Override
            public void checkChooseStorageTypes(BlockStoragePolicy p, short replication, List<StorageType> chosen, StorageType... expected) {
                final List<StorageType> types = p.chooseStorageTypes(replication, chosen, TestBlockStoragePolicy.disk_archive, true);
                TestBlockStoragePolicy.assertStorageTypes(types, expected);
            }
        };

        /**
         * With both DISK and ARCHIVE unavailable and isNewBlock=false
         */
        static final TestBlockStoragePolicy.CheckChooseStorageTypes BothUnavailableAndNonNewBlock = new TestBlockStoragePolicy.CheckChooseStorageTypes() {
            @Override
            public void checkChooseStorageTypes(BlockStoragePolicy p, short replication, List<StorageType> chosen, StorageType... expected) {
                final List<StorageType> types = p.chooseStorageTypes(replication, chosen, TestBlockStoragePolicy.disk_archive, false);
                TestBlockStoragePolicy.assertStorageTypes(types, expected);
            }
        };

        /**
         * With ARCHIVE unavailable and isNewBlock=true
         */
        static final TestBlockStoragePolicy.CheckChooseStorageTypes ArchivalUnavailableAndNewBlock = new TestBlockStoragePolicy.CheckChooseStorageTypes() {
            @Override
            public void checkChooseStorageTypes(BlockStoragePolicy p, short replication, List<StorageType> chosen, StorageType... expected) {
                final List<StorageType> types = p.chooseStorageTypes(replication, chosen, TestBlockStoragePolicy.archive, true);
                TestBlockStoragePolicy.assertStorageTypes(types, expected);
            }
        };

        /**
         * With ARCHIVE unavailable and isNewBlock=true
         */
        static final TestBlockStoragePolicy.CheckChooseStorageTypes ArchivalUnavailableAndNonNewBlock = new TestBlockStoragePolicy.CheckChooseStorageTypes() {
            @Override
            public void checkChooseStorageTypes(BlockStoragePolicy p, short replication, List<StorageType> chosen, StorageType... expected) {
                final List<StorageType> types = p.chooseStorageTypes(replication, chosen, TestBlockStoragePolicy.archive, false);
                TestBlockStoragePolicy.assertStorageTypes(types, expected);
            }
        };
    }

    @Test
    public void testChooseStorageTypes() {
        TestBlockStoragePolicy.run(TestBlockStoragePolicy.CheckChooseStorageTypes.Basic);
        TestBlockStoragePolicy.run(TestBlockStoragePolicy.CheckChooseStorageTypes.EmptyUnavailablesAndNewBlock);
        TestBlockStoragePolicy.run(TestBlockStoragePolicy.CheckChooseStorageTypes.EmptyUnavailablesAndNonNewBlock);
    }

    @Test
    public void testChooseStorageTypesWithBothUnavailable() {
        TestBlockStoragePolicy.runWithBothUnavailable(TestBlockStoragePolicy.CheckChooseStorageTypes.BothUnavailableAndNewBlock);
        TestBlockStoragePolicy.runWithBothUnavailable(TestBlockStoragePolicy.CheckChooseStorageTypes.BothUnavailableAndNonNewBlock);
    }

    @Test
    public void testChooseStorageTypesWithDiskUnavailableAndNewBlock() {
        final BlockStoragePolicy hot = TestBlockStoragePolicy.POLICY_SUITE.getPolicy(TestBlockStoragePolicy.HOT);
        final BlockStoragePolicy warm = TestBlockStoragePolicy.POLICY_SUITE.getPolicy(TestBlockStoragePolicy.WARM);
        final BlockStoragePolicy cold = TestBlockStoragePolicy.POLICY_SUITE.getPolicy(TestBlockStoragePolicy.COLD);
        final short replication = 3;
        final EnumSet<StorageType> unavailables = TestBlockStoragePolicy.disk;
        final boolean isNewBlock = true;
        {
            final List<StorageType> chosen = Lists.newArrayList();
            TestBlockStoragePolicy.checkChooseStorageTypes(hot, replication, chosen, unavailables, isNewBlock);
            TestBlockStoragePolicy.checkChooseStorageTypes(warm, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(cold, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE, ARCHIVE);
        }
        {
            final List<StorageType> chosen = Arrays.asList(DISK);
            TestBlockStoragePolicy.checkChooseStorageTypes(hot, replication, chosen, unavailables, isNewBlock);
            TestBlockStoragePolicy.checkChooseStorageTypes(warm, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(cold, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE, ARCHIVE);
        }
        {
            final List<StorageType> chosen = Arrays.asList(ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(hot, replication, chosen, unavailables, isNewBlock);
            TestBlockStoragePolicy.checkChooseStorageTypes(warm, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(cold, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE);
        }
        {
            final List<StorageType> chosen = Arrays.asList(DISK, DISK);
            TestBlockStoragePolicy.checkChooseStorageTypes(hot, replication, chosen, unavailables, isNewBlock);
            TestBlockStoragePolicy.checkChooseStorageTypes(warm, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(cold, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE, ARCHIVE);
        }
        {
            final List<StorageType> chosen = Arrays.asList(DISK, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(hot, replication, chosen, unavailables, isNewBlock);
            TestBlockStoragePolicy.checkChooseStorageTypes(warm, replication, chosen, unavailables, isNewBlock, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(cold, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE);
        }
        {
            final List<StorageType> chosen = Arrays.asList(ARCHIVE, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(hot, replication, chosen, unavailables, isNewBlock);
            TestBlockStoragePolicy.checkChooseStorageTypes(warm, replication, chosen, unavailables, isNewBlock, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(cold, replication, chosen, unavailables, isNewBlock, ARCHIVE);
        }
        {
            final List<StorageType> chosen = Arrays.asList(DISK, DISK, DISK);
            TestBlockStoragePolicy.checkChooseStorageTypes(hot, replication, chosen, unavailables, isNewBlock);
            TestBlockStoragePolicy.checkChooseStorageTypes(warm, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(cold, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE, ARCHIVE);
        }
        {
            final List<StorageType> chosen = Arrays.asList(DISK, DISK, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(hot, replication, chosen, unavailables, isNewBlock);
            TestBlockStoragePolicy.checkChooseStorageTypes(warm, replication, chosen, unavailables, isNewBlock, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(cold, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE);
        }
        {
            final List<StorageType> chosen = Arrays.asList(DISK, ARCHIVE, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(hot, replication, chosen, unavailables, isNewBlock);
            TestBlockStoragePolicy.checkChooseStorageTypes(warm, replication, chosen, unavailables, isNewBlock);
            TestBlockStoragePolicy.checkChooseStorageTypes(cold, replication, chosen, unavailables, isNewBlock, ARCHIVE);
        }
        {
            final List<StorageType> chosen = Arrays.asList(ARCHIVE, ARCHIVE, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(hot, replication, chosen, unavailables, isNewBlock);
            TestBlockStoragePolicy.checkChooseStorageTypes(warm, replication, chosen, unavailables, isNewBlock);
            TestBlockStoragePolicy.checkChooseStorageTypes(cold, replication, chosen, unavailables, isNewBlock);
        }
    }

    @Test
    public void testChooseStorageTypesWithArchiveUnavailable() {
        TestBlockStoragePolicy.runWithArchiveUnavailable(TestBlockStoragePolicy.CheckChooseStorageTypes.ArchivalUnavailableAndNewBlock);
        TestBlockStoragePolicy.runWithArchiveUnavailable(TestBlockStoragePolicy.CheckChooseStorageTypes.ArchivalUnavailableAndNonNewBlock);
    }

    @Test
    public void testChooseStorageTypesWithDiskUnavailableAndNonNewBlock() {
        final BlockStoragePolicy hot = TestBlockStoragePolicy.POLICY_SUITE.getPolicy(TestBlockStoragePolicy.HOT);
        final BlockStoragePolicy warm = TestBlockStoragePolicy.POLICY_SUITE.getPolicy(TestBlockStoragePolicy.WARM);
        final BlockStoragePolicy cold = TestBlockStoragePolicy.POLICY_SUITE.getPolicy(TestBlockStoragePolicy.COLD);
        final short replication = 3;
        final EnumSet<StorageType> unavailables = TestBlockStoragePolicy.disk;
        final boolean isNewBlock = false;
        {
            final List<StorageType> chosen = Lists.newArrayList();
            TestBlockStoragePolicy.checkChooseStorageTypes(hot, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(warm, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(cold, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE, ARCHIVE);
        }
        {
            final List<StorageType> chosen = Arrays.asList(DISK);
            TestBlockStoragePolicy.checkChooseStorageTypes(hot, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(warm, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(cold, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE, ARCHIVE);
        }
        {
            final List<StorageType> chosen = Arrays.asList(ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(hot, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(warm, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(cold, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE);
        }
        {
            final List<StorageType> chosen = Arrays.asList(DISK, DISK);
            TestBlockStoragePolicy.checkChooseStorageTypes(hot, replication, chosen, unavailables, isNewBlock, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(warm, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(cold, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE, ARCHIVE);
        }
        {
            final List<StorageType> chosen = Arrays.asList(DISK, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(hot, replication, chosen, unavailables, isNewBlock, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(warm, replication, chosen, unavailables, isNewBlock, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(cold, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE);
        }
        {
            final List<StorageType> chosen = Arrays.asList(ARCHIVE, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(hot, replication, chosen, unavailables, isNewBlock, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(warm, replication, chosen, unavailables, isNewBlock, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(cold, replication, chosen, unavailables, isNewBlock, ARCHIVE);
        }
        {
            final List<StorageType> chosen = Arrays.asList(DISK, DISK, DISK);
            TestBlockStoragePolicy.checkChooseStorageTypes(hot, replication, chosen, unavailables, isNewBlock);
            TestBlockStoragePolicy.checkChooseStorageTypes(warm, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(cold, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE, ARCHIVE);
        }
        {
            final List<StorageType> chosen = Arrays.asList(DISK, DISK, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(hot, replication, chosen, unavailables, isNewBlock);
            TestBlockStoragePolicy.checkChooseStorageTypes(warm, replication, chosen, unavailables, isNewBlock, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(cold, replication, chosen, unavailables, isNewBlock, ARCHIVE, ARCHIVE);
        }
        {
            final List<StorageType> chosen = Arrays.asList(DISK, ARCHIVE, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(hot, replication, chosen, unavailables, isNewBlock);
            TestBlockStoragePolicy.checkChooseStorageTypes(warm, replication, chosen, unavailables, isNewBlock);
            TestBlockStoragePolicy.checkChooseStorageTypes(cold, replication, chosen, unavailables, isNewBlock, ARCHIVE);
        }
        {
            final List<StorageType> chosen = Arrays.asList(ARCHIVE, ARCHIVE, ARCHIVE);
            TestBlockStoragePolicy.checkChooseStorageTypes(hot, replication, chosen, unavailables, isNewBlock);
            TestBlockStoragePolicy.checkChooseStorageTypes(warm, replication, chosen, unavailables, isNewBlock);
            TestBlockStoragePolicy.checkChooseStorageTypes(cold, replication, chosen, unavailables, isNewBlock);
        }
    }

    @Test
    public void testChooseExcess() {
        final BlockStoragePolicy hot = TestBlockStoragePolicy.POLICY_SUITE.getPolicy(TestBlockStoragePolicy.HOT);
        final BlockStoragePolicy warm = TestBlockStoragePolicy.POLICY_SUITE.getPolicy(TestBlockStoragePolicy.WARM);
        final BlockStoragePolicy cold = TestBlockStoragePolicy.POLICY_SUITE.getPolicy(TestBlockStoragePolicy.COLD);
        final short replication = 3;
        for (int n = 0; n <= 6; n++) {
            for (int d = 0; d <= n; d++) {
                final int a = n - d;
                final List<StorageType> chosen = TestBlockStoragePolicy.asList(d, a);
                {
                    final int nDisk = Math.max(0, (d - replication));
                    final int nArchive = a;
                    final StorageType[] expected = TestBlockStoragePolicy.newStorageTypes(nDisk, nArchive);
                    TestBlockStoragePolicy.checkChooseExcess(hot, replication, chosen, expected);
                }
                {
                    final int nDisk = Math.max(0, (d - 1));
                    final int nArchive = Math.max(0, ((a - replication) + 1));
                    final StorageType[] expected = TestBlockStoragePolicy.newStorageTypes(nDisk, nArchive);
                    TestBlockStoragePolicy.checkChooseExcess(warm, replication, chosen, expected);
                }
                {
                    final int nDisk = d;
                    final int nArchive = Math.max(0, (a - replication));
                    final StorageType[] expected = TestBlockStoragePolicy.newStorageTypes(nDisk, nArchive);
                    TestBlockStoragePolicy.checkChooseExcess(cold, replication, chosen, expected);
                }
            }
        }
    }

    @Test
    public void testSetStoragePolicy() throws Exception {
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(TestBlockStoragePolicy.conf).numDataNodes(TestBlockStoragePolicy.REPLICATION).build();
        cluster.waitActive();
        final DistributedFileSystem fs = cluster.getFileSystem();
        try {
            final Path dir = new Path("/testSetStoragePolicy");
            final Path fooFile = new Path(dir, "foo");
            final Path barDir = new Path(dir, "bar");
            final Path barFile1 = new Path(barDir, "f1");
            final Path barFile2 = new Path(barDir, "f2");
            DFSTestUtil.createFile(fs, fooFile, TestBlockStoragePolicy.FILE_LEN, TestBlockStoragePolicy.REPLICATION, 0L);
            DFSTestUtil.createFile(fs, barFile1, TestBlockStoragePolicy.FILE_LEN, TestBlockStoragePolicy.REPLICATION, 0L);
            DFSTestUtil.createFile(fs, barFile2, TestBlockStoragePolicy.FILE_LEN, TestBlockStoragePolicy.REPLICATION, 0L);
            final String invalidPolicyName = "INVALID-POLICY";
            try {
                fs.setStoragePolicy(fooFile, invalidPolicyName);
                Assert.fail("Should throw a HadoopIllegalArgumentException");
            } catch (RemoteException e) {
                GenericTestUtils.assertExceptionContains(invalidPolicyName, e);
            }
            // check storage policy
            HdfsFileStatus[] dirList = fs.getClient().listPaths(dir.toString(), EMPTY_NAME, true).getPartialListing();
            HdfsFileStatus[] barList = fs.getClient().listPaths(barDir.toString(), EMPTY_NAME, true).getPartialListing();
            checkDirectoryListing(dirList, org.apache.hadoop.hdfs.protocol.HdfsConstants.BLOCK_STORAGE_POLICY_ID_UNSPECIFIED, org.apache.hadoop.hdfs.protocol.HdfsConstants.BLOCK_STORAGE_POLICY_ID_UNSPECIFIED);
            checkDirectoryListing(barList, org.apache.hadoop.hdfs.protocol.HdfsConstants.BLOCK_STORAGE_POLICY_ID_UNSPECIFIED, org.apache.hadoop.hdfs.protocol.HdfsConstants.BLOCK_STORAGE_POLICY_ID_UNSPECIFIED);
            final Path invalidPath = new Path("/invalidPath");
            try {
                fs.setStoragePolicy(invalidPath, WARM_STORAGE_POLICY_NAME);
                Assert.fail("Should throw a FileNotFoundException");
            } catch (FileNotFoundException e) {
                GenericTestUtils.assertExceptionContains(invalidPath.toString(), e);
            }
            try {
                fs.getStoragePolicy(invalidPath);
                Assert.fail("Should throw a FileNotFoundException");
            } catch (FileNotFoundException e) {
                GenericTestUtils.assertExceptionContains(invalidPath.toString(), e);
            }
            fs.setStoragePolicy(fooFile, COLD_STORAGE_POLICY_NAME);
            fs.setStoragePolicy(barDir, WARM_STORAGE_POLICY_NAME);
            fs.setStoragePolicy(barFile2, HOT_STORAGE_POLICY_NAME);
            Assert.assertEquals("File storage policy should be COLD", COLD_STORAGE_POLICY_NAME, fs.getStoragePolicy(fooFile).getName());
            Assert.assertEquals("File storage policy should be WARM", WARM_STORAGE_POLICY_NAME, fs.getStoragePolicy(barDir).getName());
            Assert.assertEquals("File storage policy should be HOT", HOT_STORAGE_POLICY_NAME, fs.getStoragePolicy(barFile2).getName());
            dirList = fs.getClient().listPaths(dir.toString(), EMPTY_NAME).getPartialListing();
            barList = fs.getClient().listPaths(barDir.toString(), EMPTY_NAME).getPartialListing();
            checkDirectoryListing(dirList, TestBlockStoragePolicy.WARM, TestBlockStoragePolicy.COLD);// bar is warm, foo is cold

            checkDirectoryListing(barList, TestBlockStoragePolicy.WARM, TestBlockStoragePolicy.HOT);
            // restart namenode to make sure the editlog is correct
            cluster.restartNameNode(true);
            dirList = fs.getClient().listPaths(dir.toString(), EMPTY_NAME, true).getPartialListing();
            barList = fs.getClient().listPaths(barDir.toString(), EMPTY_NAME, true).getPartialListing();
            checkDirectoryListing(dirList, TestBlockStoragePolicy.WARM, TestBlockStoragePolicy.COLD);// bar is warm, foo is cold

            checkDirectoryListing(barList, TestBlockStoragePolicy.WARM, TestBlockStoragePolicy.HOT);
            // restart namenode with checkpoint to make sure the fsimage is correct
            fs.setSafeMode(SAFEMODE_ENTER);
            fs.saveNamespace();
            fs.setSafeMode(SAFEMODE_LEAVE);
            cluster.restartNameNode(true);
            dirList = fs.getClient().listPaths(dir.toString(), EMPTY_NAME).getPartialListing();
            barList = fs.getClient().listPaths(barDir.toString(), EMPTY_NAME).getPartialListing();
            checkDirectoryListing(dirList, TestBlockStoragePolicy.WARM, TestBlockStoragePolicy.COLD);// bar is warm, foo is cold

            checkDirectoryListing(barList, TestBlockStoragePolicy.WARM, TestBlockStoragePolicy.HOT);
        } finally {
            cluster.shutdown();
        }
    }

    @Test
    public void testGetStoragePolicy() throws Exception {
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(TestBlockStoragePolicy.conf).numDataNodes(TestBlockStoragePolicy.REPLICATION).build();
        cluster.waitActive();
        final DistributedFileSystem fs = cluster.getFileSystem();
        try {
            final Path dir = new Path("/testGetStoragePolicy");
            final Path fooFile = new Path(dir, "foo");
            DFSTestUtil.createFile(fs, fooFile, TestBlockStoragePolicy.FILE_LEN, TestBlockStoragePolicy.REPLICATION, 0L);
            DFSClient client = new DFSClient(cluster.getNameNode(0).getNameNodeAddress(), TestBlockStoragePolicy.conf);
            client.setStoragePolicy("/testGetStoragePolicy/foo", COLD_STORAGE_POLICY_NAME);
            String policyName = client.getStoragePolicy("/testGetStoragePolicy/foo").getName();
            Assert.assertEquals("File storage policy should be COLD", COLD_STORAGE_POLICY_NAME, policyName);
        } finally {
            cluster.shutdown();
        }
    }

    @Test
    public void testSetStoragePolicyWithSnapshot() throws Exception {
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(TestBlockStoragePolicy.conf).numDataNodes(TestBlockStoragePolicy.REPLICATION).build();
        cluster.waitActive();
        final DistributedFileSystem fs = cluster.getFileSystem();
        try {
            final Path dir = new Path("/testSetStoragePolicyWithSnapshot");
            final Path fooDir = new Path(dir, "foo");
            final Path fooFile1 = new Path(fooDir, "f1");
            final Path fooFile2 = new Path(fooDir, "f2");
            DFSTestUtil.createFile(fs, fooFile1, TestBlockStoragePolicy.FILE_LEN, TestBlockStoragePolicy.REPLICATION, 0L);
            DFSTestUtil.createFile(fs, fooFile2, TestBlockStoragePolicy.FILE_LEN, TestBlockStoragePolicy.REPLICATION, 0L);
            fs.setStoragePolicy(fooDir, WARM_STORAGE_POLICY_NAME);
            HdfsFileStatus[] dirList = fs.getClient().listPaths(dir.toString(), EMPTY_NAME, true).getPartialListing();
            checkDirectoryListing(dirList, TestBlockStoragePolicy.WARM);
            HdfsFileStatus[] fooList = fs.getClient().listPaths(fooDir.toString(), EMPTY_NAME, true).getPartialListing();
            checkDirectoryListing(fooList, TestBlockStoragePolicy.WARM, TestBlockStoragePolicy.WARM);
            // take snapshot
            SnapshotTestHelper.createSnapshot(fs, dir, "s1");
            // change the storage policy of fooFile1
            fs.setStoragePolicy(fooFile1, COLD_STORAGE_POLICY_NAME);
            fooList = fs.getClient().listPaths(fooDir.toString(), EMPTY_NAME).getPartialListing();
            checkDirectoryListing(fooList, TestBlockStoragePolicy.COLD, TestBlockStoragePolicy.WARM);
            // check the policy for /dir/.snapshot/s1/foo/f1. Note we always return
            // the latest storage policy for a file/directory.
            Path s1f1 = SnapshotTestHelper.getSnapshotPath(dir, "s1", "foo/f1");
            DirectoryListing f1Listing = fs.getClient().listPaths(s1f1.toString(), EMPTY_NAME);
            checkDirectoryListing(f1Listing.getPartialListing(), TestBlockStoragePolicy.COLD);
            // delete f1
            fs.delete(fooFile1, true);
            fooList = fs.getClient().listPaths(fooDir.toString(), EMPTY_NAME).getPartialListing();
            checkDirectoryListing(fooList, TestBlockStoragePolicy.WARM);
            // check the policy for /dir/.snapshot/s1/foo/f1 again after the deletion
            checkDirectoryListing(fs.getClient().listPaths(s1f1.toString(), EMPTY_NAME).getPartialListing(), TestBlockStoragePolicy.COLD);
            // change the storage policy of foo dir
            fs.setStoragePolicy(fooDir, HOT_STORAGE_POLICY_NAME);
            // /dir/foo is now hot
            dirList = fs.getClient().listPaths(dir.toString(), EMPTY_NAME, true).getPartialListing();
            checkDirectoryListing(dirList, TestBlockStoragePolicy.HOT);
            // /dir/foo/f2 is hot
            fooList = fs.getClient().listPaths(fooDir.toString(), EMPTY_NAME).getPartialListing();
            checkDirectoryListing(fooList, TestBlockStoragePolicy.HOT);
            // check storage policy of snapshot path
            Path s1 = SnapshotTestHelper.getSnapshotRoot(dir, "s1");
            Path s1foo = SnapshotTestHelper.getSnapshotPath(dir, "s1", "foo");
            checkDirectoryListing(fs.getClient().listPaths(s1.toString(), EMPTY_NAME).getPartialListing(), TestBlockStoragePolicy.HOT);
            // /dir/.snapshot/.s1/foo/f1 and /dir/.snapshot/.s1/foo/f2 should still
            // follow the latest
            checkDirectoryListing(fs.getClient().listPaths(s1foo.toString(), EMPTY_NAME).getPartialListing(), TestBlockStoragePolicy.COLD, TestBlockStoragePolicy.HOT);
            // delete foo
            fs.delete(fooDir, true);
            checkDirectoryListing(fs.getClient().listPaths(s1.toString(), EMPTY_NAME).getPartialListing(), TestBlockStoragePolicy.HOT);
            checkDirectoryListing(fs.getClient().listPaths(s1foo.toString(), EMPTY_NAME).getPartialListing(), TestBlockStoragePolicy.COLD, TestBlockStoragePolicy.HOT);
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * Consider a File with Hot storage policy. Increase replication factor of
     * that file from 3 to 5. Make sure all replications are created in DISKS.
     */
    @Test
    public void testChangeHotFileRep() throws Exception {
        testChangeFileRep(HOT_STORAGE_POLICY_NAME, TestBlockStoragePolicy.HOT, new StorageType[]{ StorageType.DISK, StorageType.DISK, StorageType.DISK }, new StorageType[]{ StorageType.DISK, StorageType.DISK, StorageType.DISK, StorageType.DISK, StorageType.DISK });
    }

    /**
     * Consider a File with Warm temperature. Increase replication factor of
     * that file from 3 to 5. Make sure all replicas are created in DISKS
     * and ARCHIVE.
     */
    @Test
    public void testChangeWarmRep() throws Exception {
        testChangeFileRep(WARM_STORAGE_POLICY_NAME, TestBlockStoragePolicy.WARM, new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE, StorageType.ARCHIVE }, new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE, StorageType.ARCHIVE, StorageType.ARCHIVE, StorageType.ARCHIVE });
    }

    /**
     * Consider a File with Cold temperature. Increase replication factor of
     * that file from 3 to 5. Make sure all replicas are created in ARCHIVE.
     */
    @Test
    public void testChangeColdRep() throws Exception {
        testChangeFileRep(COLD_STORAGE_POLICY_NAME, TestBlockStoragePolicy.COLD, new StorageType[]{ StorageType.ARCHIVE, StorageType.ARCHIVE, StorageType.ARCHIVE }, new StorageType[]{ StorageType.ARCHIVE, StorageType.ARCHIVE, StorageType.ARCHIVE, StorageType.ARCHIVE, StorageType.ARCHIVE });
    }

    @Test
    public void testChooseTargetWithTopology() throws Exception {
        BlockStoragePolicy policy1 = new BlockStoragePolicy(((byte) (9)), "TEST1", new StorageType[]{ StorageType.SSD, StorageType.DISK, StorageType.ARCHIVE }, new StorageType[]{  }, new StorageType[]{  });
        BlockStoragePolicy policy2 = new BlockStoragePolicy(((byte) (11)), "TEST2", new StorageType[]{ StorageType.DISK, StorageType.SSD, StorageType.ARCHIVE }, new StorageType[]{  }, new StorageType[]{  });
        final String[] racks = new String[]{ "/d1/r1", "/d1/r2", "/d1/r2" };
        final String[] hosts = new String[]{ "host1", "host2", "host3" };
        final StorageType[] types = new StorageType[]{ StorageType.DISK, StorageType.SSD, StorageType.ARCHIVE };
        final DatanodeStorageInfo[] storages = DFSTestUtil.createDatanodeStorageInfos(3, racks, hosts, types);
        final DatanodeDescriptor[] dataNodes = DFSTestUtil.toDatanodeDescriptor(storages);
        FileSystem.setDefaultUri(TestBlockStoragePolicy.conf, "hdfs://localhost:0");
        TestBlockStoragePolicy.conf.set(DFS_NAMENODE_HTTP_ADDRESS_KEY, "0.0.0.0:0");
        File baseDir = PathUtils.getTestDir(TestReplicationPolicy.class);
        TestBlockStoragePolicy.conf.set(DFS_NAMENODE_NAME_DIR_KEY, new File(baseDir, "name").getPath());
        DFSTestUtil.formatNameNode(TestBlockStoragePolicy.conf);
        NameNode namenode = new NameNode(TestBlockStoragePolicy.conf);
        final BlockManager bm = namenode.getNamesystem().getBlockManager();
        BlockPlacementPolicy replicator = bm.getBlockPlacementPolicy();
        NetworkTopology cluster = bm.getDatanodeManager().getNetworkTopology();
        for (DatanodeDescriptor datanode : dataNodes) {
            cluster.add(datanode);
        }
        DatanodeStorageInfo[] targets = replicator.chooseTarget("/foo", 3, dataNodes[0], Collections.<DatanodeStorageInfo>emptyList(), false, new HashSet<org.apache.hadoop.net.Node>(), 0, policy1, null);
        System.out.println(Arrays.asList(targets));
        Assert.assertEquals(3, targets.length);
        targets = replicator.chooseTarget("/foo", 3, dataNodes[0], Collections.<DatanodeStorageInfo>emptyList(), false, new HashSet<org.apache.hadoop.net.Node>(), 0, policy2, null);
        System.out.println(Arrays.asList(targets));
        Assert.assertEquals(3, targets.length);
    }

    @Test
    public void testChooseSsdOverDisk() throws Exception {
        BlockStoragePolicy policy = new BlockStoragePolicy(((byte) (9)), "TEST1", new StorageType[]{ StorageType.SSD, StorageType.DISK, StorageType.ARCHIVE }, new StorageType[]{  }, new StorageType[]{  });
        final String[] racks = new String[]{ "/d1/r1", "/d1/r1", "/d1/r1" };
        final String[] hosts = new String[]{ "host1", "host2", "host3" };
        final StorageType[] disks = new StorageType[]{ StorageType.DISK, StorageType.DISK, StorageType.DISK };
        final DatanodeStorageInfo[] diskStorages = DFSTestUtil.createDatanodeStorageInfos(3, racks, hosts, disks);
        final DatanodeDescriptor[] dataNodes = DFSTestUtil.toDatanodeDescriptor(diskStorages);
        for (int i = 0; i < (dataNodes.length); i++) {
            BlockManagerTestUtil.updateStorage(dataNodes[i], new org.apache.hadoop.hdfs.server.protocol.DatanodeStorage(("ssd" + i), State.NORMAL, StorageType.SSD));
        }
        FileSystem.setDefaultUri(TestBlockStoragePolicy.conf, "hdfs://localhost:0");
        TestBlockStoragePolicy.conf.set(DFS_NAMENODE_HTTP_ADDRESS_KEY, "0.0.0.0:0");
        File baseDir = PathUtils.getTestDir(TestReplicationPolicy.class);
        TestBlockStoragePolicy.conf.set(DFS_NAMENODE_NAME_DIR_KEY, new File(baseDir, "name").getPath());
        DFSTestUtil.formatNameNode(TestBlockStoragePolicy.conf);
        NameNode namenode = new NameNode(TestBlockStoragePolicy.conf);
        final BlockManager bm = namenode.getNamesystem().getBlockManager();
        BlockPlacementPolicy replicator = bm.getBlockPlacementPolicy();
        NetworkTopology cluster = bm.getDatanodeManager().getNetworkTopology();
        for (DatanodeDescriptor datanode : dataNodes) {
            cluster.add(datanode);
        }
        DatanodeStorageInfo[] targets = replicator.chooseTarget("/foo", 3, dataNodes[0], Collections.<DatanodeStorageInfo>emptyList(), false, new HashSet<org.apache.hadoop.net.Node>(), 0, policy, null);
        System.out.println((((policy.getName()) + ": ") + (Arrays.asList(targets))));
        Assert.assertEquals(2, targets.length);
        Assert.assertEquals(SSD, targets[0].getStorageType());
        Assert.assertEquals(DISK, targets[1].getStorageType());
    }

    @Test
    public void testGetFileStoragePolicyAfterRestartNN() throws Exception {
        // HDFS8219
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(TestBlockStoragePolicy.conf).numDataNodes(TestBlockStoragePolicy.REPLICATION).storageTypes(new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE }).build();
        cluster.waitActive();
        final DistributedFileSystem fs = cluster.getFileSystem();
        try {
            final String file = "/testScheduleWithinSameNode/file";
            Path dir = new Path("/testScheduleWithinSameNode");
            fs.mkdirs(dir);
            // 2. Set Dir policy
            fs.setStoragePolicy(dir, "COLD");
            // 3. Create file
            final FSDataOutputStream out = fs.create(new Path(file));
            out.writeChars("testScheduleWithinSameNode");
            out.close();
            // 4. Set Dir policy
            fs.setStoragePolicy(dir, "HOT");
            HdfsFileStatus status = fs.getClient().getFileInfo(file);
            // 5. get file policy, it should be parent policy.
            Assert.assertTrue("File storage policy should be HOT", ((status.getStoragePolicy()) == (TestBlockStoragePolicy.HOT)));
            // 6. restart NameNode for reloading edits logs.
            cluster.restartNameNode(true);
            // 7. get file policy, it should be parent policy.
            status = fs.getClient().getFileInfo(file);
            Assert.assertTrue("File storage policy should be HOT", ((status.getStoragePolicy()) == (TestBlockStoragePolicy.HOT)));
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * Verify that {@link FileSystem#getAllStoragePolicies} returns all
     * known storage policies for DFS.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testGetAllStoragePoliciesFromFs() throws IOException {
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(TestBlockStoragePolicy.conf).numDataNodes(TestBlockStoragePolicy.REPLICATION).storageTypes(new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE }).build();
        try {
            cluster.waitActive();
            // Get policies via {@link FileSystem#getAllStoragePolicies}
            Set<String> policyNamesSet1 = new HashSet<>();
            for (BlockStoragePolicySpi policy : cluster.getFileSystem().getAllStoragePolicies()) {
                policyNamesSet1.add(policy.getName());
            }
            // Get policies from the default BlockStoragePolicySuite.
            BlockStoragePolicySuite suite = BlockStoragePolicySuite.createDefaultSuite();
            Set<String> policyNamesSet2 = new HashSet<>();
            for (BlockStoragePolicy policy : suite.getAllPolicies()) {
                policyNamesSet2.add(policy.getName());
            }
            // Ensure that we got the same set of policies in both cases.
            Assert.assertTrue(Sets.difference(policyNamesSet1, policyNamesSet2).isEmpty());
            Assert.assertTrue(Sets.difference(policyNamesSet2, policyNamesSet1).isEmpty());
        } finally {
            cluster.shutdown();
        }
    }

    @Test
    public void testStorageType() {
        final EnumMap<StorageType, Integer> map = new EnumMap(StorageType.class);
        // put storage type is reversed order
        map.put(ARCHIVE, 1);
        map.put(DISK, 1);
        map.put(SSD, 1);
        map.put(RAM_DISK, 1);
        {
            final Iterator<StorageType> i = map.keySet().iterator();
            Assert.assertEquals(RAM_DISK, i.next());
            Assert.assertEquals(SSD, i.next());
            Assert.assertEquals(DISK, i.next());
            Assert.assertEquals(ARCHIVE, i.next());
        }
        {
            final Iterator<Map.Entry<StorageType, Integer>> i = map.entrySet().iterator();
            Assert.assertEquals(RAM_DISK, i.next().getKey());
            Assert.assertEquals(SSD, i.next().getKey());
            Assert.assertEquals(DISK, i.next().getKey());
            Assert.assertEquals(ARCHIVE, i.next().getKey());
        }
    }

    @Test
    public void testStorageTypeCheckAccess() {
        testStorageTypeCheckAccessResult(new StorageType[]{ StorageType.DEFAULT }, new StorageType[]{ StorageType.DEFAULT }, true);
        testStorageTypeCheckAccessResult(EMPTY_ARRAY, EMPTY_ARRAY, false);
        testStorageTypeCheckAccessResult(new StorageType[]{ StorageType.DISK }, EMPTY_ARRAY, false);
        testStorageTypeCheckAccessResult(EMPTY_ARRAY, new StorageType[]{ StorageType.RAM_DISK }, true);
        testStorageTypeCheckAccessResult(new StorageType[]{ StorageType.DISK }, new StorageType[]{ StorageType.DISK }, true);
        testStorageTypeCheckAccessResult(new StorageType[]{ StorageType.DISK }, new StorageType[]{ StorageType.DISK, StorageType.DISK, StorageType.DISK }, false);
        testStorageTypeCheckAccessResult(new StorageType[]{ StorageType.DISK, StorageType.DISK, StorageType.DISK }, new StorageType[]{ StorageType.DISK, StorageType.DISK, StorageType.DISK }, true);
        testStorageTypeCheckAccessResult(new StorageType[]{ StorageType.RAM_DISK, StorageType.SSD }, new StorageType[]{ StorageType.DISK, StorageType.RAM_DISK, StorageType.SSD }, false);
        testStorageTypeCheckAccessResult(new StorageType[]{ StorageType.DISK, StorageType.SSD }, new StorageType[]{ StorageType.SSD }, true);
        testStorageTypeCheckAccessResult(new StorageType[]{ StorageType.DISK }, new StorageType[]{ StorageType.RAM_DISK }, false);
        testStorageTypeCheckAccessResult(new StorageType[]{ StorageType.DISK }, new StorageType[]{ StorageType.RAM_DISK, StorageType.SSD, StorageType.ARCHIVE }, false);
        testStorageTypeCheckAccessResult(new StorageType[]{ StorageType.RAM_DISK, StorageType.SSD, StorageType.ARCHIVE }, new StorageType[]{ StorageType.DISK }, false);
        testStorageTypeCheckAccessResult(new StorageType[]{ StorageType.DISK, StorageType.SSD }, new StorageType[]{ StorageType.SSD }, true);
        testStorageTypeCheckAccessResult(new StorageType[]{ StorageType.RAM_DISK }, new StorageType[]{ StorageType.DISK }, false);
        testStorageTypeCheckAccessResult(new StorageType[]{ StorageType.RAM_DISK, StorageType.SSD, StorageType.ARCHIVE }, new StorageType[]{ StorageType.DISK }, false);
        testStorageTypeCheckAccessResult(new StorageType[]{ StorageType.RAM_DISK, StorageType.SSD, StorageType.ARCHIVE }, new StorageType[]{ StorageType.DISK }, false);
    }

    @Test
    public void testStorageIDCheckAccess() {
        testStorageIDCheckAccessResult(new String[]{ "DN1-Storage1" }, new String[]{ "DN1-Storage1" }, true);
        testStorageIDCheckAccessResult(new String[]{ "DN1-Storage1", "DN2-Storage1" }, new String[]{ "DN1-Storage1" }, true);
        testStorageIDCheckAccessResult(new String[]{ "DN1-Storage1", "DN2-Storage1" }, new String[]{ "DN1-Storage1", "DN1-Storage2" }, false);
        testStorageIDCheckAccessResult(new String[]{ "DN1-Storage1", "DN1-Storage2" }, new String[]{ "DN1-Storage1" }, true);
        testStorageIDCheckAccessResult(new String[]{ "DN1-Storage1", "DN1-Storage2" }, new String[]{ "DN2-Storage1" }, false);
        testStorageIDCheckAccessResult(new String[]{ "DN1-Storage2", "DN2-Storage2" }, new String[]{ "DN1-Storage1", "DN2-Storage1" }, false);
        testStorageIDCheckAccessResult(new String[0], new String[0], false);
        testStorageIDCheckAccessResult(new String[0], new String[]{ "DN1-Storage1" }, true);
        testStorageIDCheckAccessResult(new String[]{ "DN1-Storage1" }, new String[0], false);
    }
}

