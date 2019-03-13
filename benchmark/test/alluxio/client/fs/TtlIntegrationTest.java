/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.client.fs;


import HeartbeatContext.MASTER_TTL_CHECK;
import PropertyKey.MASTER_TTL_CHECKER_INTERVAL_MS;
import WritePType.THROUGH;
import alluxio.AlluxioURI;
import alluxio.client.file.FileSystem;
import alluxio.grpc.CreateFilePOptions;
import alluxio.grpc.FileSystemMasterCommonPOptions;
import alluxio.heartbeat.HeartbeatContext;
import alluxio.heartbeat.HeartbeatScheduler;
import alluxio.heartbeat.ManuallyScheduleHeartbeat;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.testutils.LocalAlluxioClusterResource;
import alluxio.util.CommonUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;


/**
 * Integration tests for handling file TTLs (times to live).
 */
public class TtlIntegrationTest extends BaseIntegrationTest {
    private static final int TTL_INTERVAL_MS = 50;

    private FileSystem mFileSystem;

    @ClassRule
    public static ManuallyScheduleHeartbeat sManuallySchedule = new ManuallyScheduleHeartbeat(HeartbeatContext.MASTER_TTL_CHECK);

    @Rule
    public LocalAlluxioClusterResource mLocalAlluxioClusterResource = new LocalAlluxioClusterResource.Builder().setProperty(MASTER_TTL_CHECKER_INTERVAL_MS, TtlIntegrationTest.TTL_INTERVAL_MS).build();

    /**
     * Tests that when many TTLs expire at the same time, files are deleted properly.
     */
    @Test
    public void expireManyAfterDelete() throws Exception {
        int numFiles = 100;
        AlluxioURI[] files = new AlluxioURI[numFiles];
        for (int i = 0; i < numFiles; i++) {
            files[i] = new AlluxioURI(("/file" + i));
            // Only the even-index files should expire.
            long ttl = ((i % 2) == 0) ? (TtlIntegrationTest.TTL_INTERVAL_MS) / 2 : (TtlIntegrationTest.TTL_INTERVAL_MS) * 1000;
            mFileSystem.createFile(files[i], CreateFilePOptions.newBuilder().setWriteType(THROUGH).setCommonOptions(FileSystemMasterCommonPOptions.newBuilder().setTtl(ttl)).build()).close();
            // Delete some of the even files to make sure this doesn't trip up the TTL checker.
            if ((i % 20) == 0) {
                mFileSystem.delete(files[i]);
            }
        }
        CommonUtils.sleepMs((2 * (TtlIntegrationTest.TTL_INTERVAL_MS)));
        HeartbeatScheduler.execute(MASTER_TTL_CHECK);
        for (int i = 0; i < numFiles; i++) {
            if ((i % 2) == 0) {
                Assert.assertFalse(mFileSystem.exists(files[i]));
            } else {
                Assert.assertTrue(mFileSystem.exists(files[i]));
            }
        }
    }
}

