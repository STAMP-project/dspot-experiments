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
package org.apache.hadoop.hdfs.server.datanode.fsdataset.impl;


import StorageType.ARCHIVE;
import StorageType.DISK;
import StorageType.RAM_DISK;
import StorageType.SSD;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.DF;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.junit.Test;


/**
 * Unit testing for different types of ReservedSpace calculators.
 */
public class TestReservedSpaceCalculator {
    private Configuration conf;

    private DF usage;

    private ReservedSpaceCalculator reserved;

    @Test
    public void testReservedSpaceAbsolute() {
        conf.setClass(DFSConfigKeys.DFS_DATANODE_DU_RESERVED_CALCULATOR_KEY, ReservedSpaceCalculator.ReservedSpaceCalculatorAbsolute.class, ReservedSpaceCalculator.class);
        // Test both using global configuration
        conf.setLong(DFSConfigKeys.DFS_DATANODE_DU_RESERVED_KEY, 900);
        checkReserved(DISK, 10000, 900);
        checkReserved(SSD, 10000, 900);
        checkReserved(ARCHIVE, 10000, 900);
    }

    @Test
    public void testReservedSpaceAbsolutePerStorageType() {
        conf.setClass(DFSConfigKeys.DFS_DATANODE_DU_RESERVED_CALCULATOR_KEY, ReservedSpaceCalculator.ReservedSpaceCalculatorAbsolute.class, ReservedSpaceCalculator.class);
        // Test DISK
        conf.setLong(((DFSConfigKeys.DFS_DATANODE_DU_RESERVED_KEY) + ".disk"), 500);
        checkReserved(DISK, 2300, 500);
        // Test SSD
        conf.setLong(((DFSConfigKeys.DFS_DATANODE_DU_RESERVED_KEY) + ".ssd"), 750);
        checkReserved(SSD, 1550, 750);
    }

    @Test
    public void testReservedSpacePercentage() {
        conf.setClass(DFSConfigKeys.DFS_DATANODE_DU_RESERVED_CALCULATOR_KEY, ReservedSpaceCalculator.ReservedSpaceCalculatorPercentage.class, ReservedSpaceCalculator.class);
        // Test both using global configuration
        conf.setLong(DFSConfigKeys.DFS_DATANODE_DU_RESERVED_PERCENTAGE_KEY, 10);
        checkReserved(DISK, 10000, 1000);
        checkReserved(SSD, 10000, 1000);
        checkReserved(ARCHIVE, 10000, 1000);
        conf.setLong(DFSConfigKeys.DFS_DATANODE_DU_RESERVED_PERCENTAGE_KEY, 50);
        checkReserved(DISK, 4000, 2000);
        checkReserved(SSD, 4000, 2000);
        checkReserved(ARCHIVE, 4000, 2000);
    }

    @Test
    public void testReservedSpacePercentagePerStorageType() {
        conf.setClass(DFSConfigKeys.DFS_DATANODE_DU_RESERVED_CALCULATOR_KEY, ReservedSpaceCalculator.ReservedSpaceCalculatorPercentage.class, ReservedSpaceCalculator.class);
        // Test DISK
        conf.setLong(((DFSConfigKeys.DFS_DATANODE_DU_RESERVED_PERCENTAGE_KEY) + ".disk"), 20);
        checkReserved(DISK, 1600, 320);
        // Test SSD
        conf.setLong(((DFSConfigKeys.DFS_DATANODE_DU_RESERVED_PERCENTAGE_KEY) + ".ssd"), 50);
        checkReserved(SSD, 8001, 4000);
    }

    @Test
    public void testReservedSpaceConservativePerStorageType() {
        // This policy should take the maximum of the two
        conf.setClass(DFSConfigKeys.DFS_DATANODE_DU_RESERVED_CALCULATOR_KEY, ReservedSpaceCalculator.ReservedSpaceCalculatorConservative.class, ReservedSpaceCalculator.class);
        // Test DISK + taking the reserved bytes over percentage,
        // as that gives more reserved space
        conf.setLong(((DFSConfigKeys.DFS_DATANODE_DU_RESERVED_KEY) + ".disk"), 800);
        conf.setLong(((DFSConfigKeys.DFS_DATANODE_DU_RESERVED_PERCENTAGE_KEY) + ".disk"), 20);
        checkReserved(DISK, 1600, 800);
        // Test ARCHIVE + taking reserved space based on the percentage,
        // as that gives more reserved space
        conf.setLong(((DFSConfigKeys.DFS_DATANODE_DU_RESERVED_KEY) + ".archive"), 1300);
        conf.setLong(((DFSConfigKeys.DFS_DATANODE_DU_RESERVED_PERCENTAGE_KEY) + ".archive"), 50);
        checkReserved(ARCHIVE, 6200, 3100);
    }

    @Test
    public void testReservedSpaceAggresivePerStorageType() {
        // This policy should take the maximum of the two
        conf.setClass(DFSConfigKeys.DFS_DATANODE_DU_RESERVED_CALCULATOR_KEY, ReservedSpaceCalculator.ReservedSpaceCalculatorAggressive.class, ReservedSpaceCalculator.class);
        // Test RAM_DISK + taking the reserved bytes over percentage,
        // as that gives less reserved space
        conf.setLong(((DFSConfigKeys.DFS_DATANODE_DU_RESERVED_KEY) + ".ram_disk"), 100);
        conf.setLong(((DFSConfigKeys.DFS_DATANODE_DU_RESERVED_PERCENTAGE_KEY) + ".ram_disk"), 10);
        checkReserved(RAM_DISK, 1600, 100);
        // Test ARCHIVE + taking reserved space based on the percentage,
        // as that gives less reserved space
        conf.setLong(((DFSConfigKeys.DFS_DATANODE_DU_RESERVED_KEY) + ".archive"), 20000);
        conf.setLong(((DFSConfigKeys.DFS_DATANODE_DU_RESERVED_PERCENTAGE_KEY) + ".archive"), 5);
        checkReserved(ARCHIVE, 100000, 5000);
    }

    @Test(expected = IllegalStateException.class)
    public void testInvalidCalculator() {
        conf.set(DFSConfigKeys.DFS_DATANODE_DU_RESERVED_CALCULATOR_KEY, "INVALIDTYPE");
        reserved = new ReservedSpaceCalculator.Builder(conf).setUsage(usage).setStorageType(DISK).build();
    }
}

