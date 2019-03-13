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
package org.apache.flink.yarn.highavailability;


import HighAvailabilityServicesUtils.AddressResolution;
import YarnConfigOptions.APP_MASTER_RPC_ADDRESS;
import YarnConfigOptions.APP_MASTER_RPC_PORT;
import java.io.FileNotFoundException;
import org.apache.flink.api.common.JobID;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.IllegalConfigurationException;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.Path;
import org.apache.flink.util.TestLogger;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static YarnHighAvailabilityServices.FLINK_RECOVERY_DATA_DIR;


/**
 * Tests for YarnPreConfiguredMasterNonHaServices.
 */
public class YarnPreConfiguredMasterHaServicesTest extends TestLogger {
    @ClassRule
    public static final TemporaryFolder TEMP_DIR = new TemporaryFolder();

    private static MiniDFSCluster hdfsCluster;

    private static Path hdfsRootPath;

    private Configuration hadoopConfig;

    // ------------------------------------------------------------------------
    // Tests
    // ------------------------------------------------------------------------
    @Test
    public void testMissingRmConfiguration() throws Exception {
        final Configuration flinkConfig = new Configuration();
        // missing resource manager address
        try {
            new YarnPreConfiguredMasterNonHaServices(flinkConfig, hadoopConfig, AddressResolution.NO_ADDRESS_RESOLUTION);
            Assert.fail();
        } catch (IllegalConfigurationException e) {
            // expected
        }
        flinkConfig.setString(APP_MASTER_RPC_ADDRESS, "localhost");
        // missing resource manager port
        try {
            new YarnPreConfiguredMasterNonHaServices(flinkConfig, hadoopConfig, AddressResolution.NO_ADDRESS_RESOLUTION);
            Assert.fail();
        } catch (IllegalConfigurationException e) {
            // expected
        }
        flinkConfig.setInteger(APP_MASTER_RPC_PORT, 1427);
        // now everything is good ;-)
        closeAndCleanupAllData();
    }

    @Test
    public void testCloseAndCleanup() throws Exception {
        final Configuration flinkConfig = new Configuration();
        flinkConfig.setString(APP_MASTER_RPC_ADDRESS, "localhost");
        flinkConfig.setInteger(APP_MASTER_RPC_PORT, 1427);
        // create the services
        YarnHighAvailabilityServices services = new YarnPreConfiguredMasterNonHaServices(flinkConfig, hadoopConfig, AddressResolution.NO_ADDRESS_RESOLUTION);
        services.closeAndCleanupAllData();
        final FileSystem fileSystem = YarnPreConfiguredMasterHaServicesTest.hdfsRootPath.getFileSystem();
        final Path workDir = new Path(YarnPreConfiguredMasterHaServicesTest.hdfsCluster.getFileSystem().getWorkingDirectory().toString());
        try {
            fileSystem.getFileStatus(new Path(workDir, FLINK_RECOVERY_DATA_DIR));
            Assert.fail("Flink recovery data directory still exists");
        } catch (FileNotFoundException e) {
            // expected, because the directory should have been cleaned up
        }
        Assert.assertTrue(services.isClosed());
        // doing another cleanup when the services are closed should fail
        try {
            services.closeAndCleanupAllData();
            Assert.fail("should fail with an IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    @Test
    public void testCallsOnClosedServices() throws Exception {
        final Configuration flinkConfig = new Configuration();
        flinkConfig.setString(APP_MASTER_RPC_ADDRESS, "localhost");
        flinkConfig.setInteger(APP_MASTER_RPC_PORT, 1427);
        YarnHighAvailabilityServices services = new YarnPreConfiguredMasterNonHaServices(flinkConfig, hadoopConfig, AddressResolution.NO_ADDRESS_RESOLUTION);
        // this method is not supported
        try {
            services.getSubmittedJobGraphStore();
            Assert.fail();
        } catch (UnsupportedOperationException ignored) {
        }
        services.close();
        // all these methods should fail now
        try {
            services.createBlobStore();
            Assert.fail();
        } catch (IllegalStateException ignored) {
        }
        try {
            services.getCheckpointRecoveryFactory();
            Assert.fail();
        } catch (IllegalStateException ignored) {
        }
        try {
            services.getJobManagerLeaderElectionService(new JobID());
            Assert.fail();
        } catch (IllegalStateException ignored) {
        }
        try {
            services.getJobManagerLeaderRetriever(new JobID());
            Assert.fail();
        } catch (IllegalStateException ignored) {
        }
        try {
            services.getRunningJobsRegistry();
            Assert.fail();
        } catch (IllegalStateException ignored) {
        }
        try {
            services.getResourceManagerLeaderElectionService();
            Assert.fail();
        } catch (IllegalStateException ignored) {
        }
        try {
            services.getResourceManagerLeaderRetriever();
            Assert.fail();
        } catch (IllegalStateException ignored) {
        }
    }
}

