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


import java.io.IOException;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Test;


/**
 * The test verifies that legacy storage IDs in older DataNode
 * images are replaced with UUID-based storage IDs. The startup may
 * or may not involve a Datanode Layout upgrade. Each test case uses
 * the following resource files.
 *
 *    1. testCaseName.tgz - NN and DN directories corresponding
 *                          to a specific layout version.
 *    2. testCaseName.txt - Text file listing the checksum of each file
 *                          in the cluster and overall checksum. See
 *                          TestUpgradeFromImage for the file format.
 *
 * If any test case is renamed then the corresponding resource files must
 * also be renamed.
 */
public class TestDatanodeStartupFixesLegacyStorageIDs {
    /**
     * Upgrade from 2.2 (no storage IDs per volume) correctly generates
     * GUID-based storage IDs. Test case for HDFS-7575.
     */
    @Test(timeout = 300000)
    public void testUpgradeFrom22FixesStorageIDs() throws IOException {
        TestDatanodeStartupFixesLegacyStorageIDs.runLayoutUpgradeTest(GenericTestUtils.getMethodName(), null);
    }

    /**
     * Startup from a 2.6-layout that has legacy storage IDs correctly
     * generates new storage IDs.
     * Test case for HDFS-7575.
     */
    @Test(timeout = 300000)
    public void testUpgradeFrom22via26FixesStorageIDs() throws IOException {
        TestDatanodeStartupFixesLegacyStorageIDs.runLayoutUpgradeTest(GenericTestUtils.getMethodName(), null);
    }

    /**
     * Startup from a 2.6-layout that already has unique storage IDs does
     * not regenerate the storage IDs.
     * Test case for HDFS-7575.
     */
    @Test(timeout = 300000)
    public void testUpgradeFrom26PreservesStorageIDs() throws IOException {
        // StorageId present in the image testUpgradeFrom26PreservesStorageId.tgz
        TestDatanodeStartupFixesLegacyStorageIDs.runLayoutUpgradeTest(GenericTestUtils.getMethodName(), "DS-a0e39cfa-930f-4abd-813c-e22b59223774");
    }
}

