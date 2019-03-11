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
package org.apache.hadoop.hdfs.server.namenode;


import DFSConfigKeys.DFS_STORAGE_POLICY_SATISFIER_MODE_KEY;
import StoragePolicySatisfierMode.NONE;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.ReconfigurationException;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests that StoragePolicySatisfier is able to work with HA enabled.
 */
public class TestStoragePolicySatisfierWithHA {
    private MiniDFSCluster cluster = null;

    private final Configuration config = new HdfsConfiguration();

    private static final int DEFAULT_BLOCK_SIZE = 1024;

    private DistributedFileSystem dfs = null;

    private StorageType[][] allDiskTypes = new StorageType[][]{ new StorageType[]{ StorageType.DISK, StorageType.DISK }, new StorageType[]{ StorageType.DISK, StorageType.DISK }, new StorageType[]{ StorageType.DISK, StorageType.DISK } };

    private int numOfDatanodes = 3;

    private int storagesPerDatanode = 2;

    private long capacity = ((2 * 256) * 1024) * 1024;

    private int nnIndex = 0;

    /**
     * Tests to verify that SPS should run/stop automatically when NN state
     * changes between Standby and Active.
     */
    @Test(timeout = 90000)
    public void testWhenNNHAStateChanges() throws IOException {
        try {
            createCluster();
            // NN transits from Active to Standby
            cluster.transitionToStandby(0);
            cluster.waitActive();
            try {
                cluster.getNameNode(0).reconfigurePropertyImpl(DFS_STORAGE_POLICY_SATISFIER_MODE_KEY, NONE.toString());
                Assert.fail(("It's not allowed to enable or disable" + " StoragePolicySatisfier on Standby NameNode"));
            } catch (ReconfigurationException e) {
                GenericTestUtils.assertExceptionContains((("Could not change property " + (DFSConfigKeys.DFS_STORAGE_POLICY_SATISFIER_MODE_KEY)) + " from 'EXTERNAL' to 'NONE'"), e);
                GenericTestUtils.assertExceptionContains(("Enabling or disabling storage policy satisfier service on " + "standby NameNode is not allowed"), e.getCause());
            }
        } finally {
            cluster.shutdown();
        }
    }
}

