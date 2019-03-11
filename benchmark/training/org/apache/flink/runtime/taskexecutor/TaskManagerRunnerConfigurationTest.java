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
package org.apache.flink.runtime.taskexecutor;


import HostBindPolicy.NAME;
import TaskManagerOptions.RPC_PORT;
import java.io.File;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.URI;
import net.jcip.annotations.NotThreadSafe;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.runtime.highavailability.HighAvailabilityServices;
import org.apache.flink.runtime.rpc.RpcService;
import org.apache.flink.util.IOUtils;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Validates that the TaskManagerRunner startup properly obeys the configuration
 * values.
 *
 * <p>NOTE: at least {@link #testDefaultFsParameterLoading()} should not be run in parallel to other
 * tests in the same JVM as it modifies a static (private) member of the {@link FileSystem} class
 * and verifies its content.
 */
@NotThreadSafe
public class TaskManagerRunnerConfigurationTest extends TestLogger {
    private static final int TEST_TIMEOUT_SECONDS = 10;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testTaskManagerRpcServiceShouldBindToConfiguredTaskManagerHostname() throws Exception {
        final String taskmanagerHost = "testhostname";
        final Configuration config = TaskManagerRunnerConfigurationTest.createFlinkConfigWithPredefinedTaskManagerHostname(taskmanagerHost);
        final HighAvailabilityServices highAvailabilityServices = createHighAvailabilityServices(config);
        RpcService taskManagerRpcService = null;
        try {
            taskManagerRpcService = TaskManagerRunner.createRpcService(config, highAvailabilityServices);
            Assert.assertThat(taskManagerRpcService.getPort(), Matchers.is(Matchers.greaterThanOrEqualTo(0)));
            Assert.assertThat(taskManagerRpcService.getAddress(), Matchers.is(Matchers.equalTo(taskmanagerHost)));
        } finally {
            TaskManagerRunnerConfigurationTest.maybeCloseRpcService(taskManagerRpcService);
            highAvailabilityServices.closeAndCleanupAllData();
        }
    }

    @Test
    public void testTaskManagerRpcServiceShouldBindToHostnameAddress() throws Exception {
        final Configuration config = TaskManagerRunnerConfigurationTest.createFlinkConfigWithHostBindPolicy(NAME);
        final HighAvailabilityServices highAvailabilityServices = createHighAvailabilityServices(config);
        RpcService taskManagerRpcService = null;
        try {
            taskManagerRpcService = TaskManagerRunner.createRpcService(config, highAvailabilityServices);
            Assert.assertThat(taskManagerRpcService.getAddress(), Matchers.not(Matchers.isEmptyOrNullString()));
        } finally {
            TaskManagerRunnerConfigurationTest.maybeCloseRpcService(taskManagerRpcService);
            highAvailabilityServices.closeAndCleanupAllData();
        }
    }

    @Test
    public void testTaskManagerRpcServiceShouldBindToIpAddressDeterminedByConnectingToResourceManager() throws Exception {
        final ServerSocket testJobManagerSocket = TaskManagerRunnerConfigurationTest.openServerSocket();
        final Configuration config = TaskManagerRunnerConfigurationTest.createFlinkConfigWithJobManagerPort(testJobManagerSocket.getLocalPort());
        final HighAvailabilityServices highAvailabilityServices = createHighAvailabilityServices(config);
        RpcService taskManagerRpcService = null;
        try {
            taskManagerRpcService = TaskManagerRunner.createRpcService(config, highAvailabilityServices);
            Assert.assertThat(taskManagerRpcService.getAddress(), Matchers.is(TaskManagerRunnerConfigurationTest.ipAddress()));
        } finally {
            TaskManagerRunnerConfigurationTest.maybeCloseRpcService(taskManagerRpcService);
            highAvailabilityServices.closeAndCleanupAllData();
            IOUtils.closeQuietly(testJobManagerSocket);
        }
    }

    @Test
    public void testCreatingTaskManagerRpcServiceShouldFailIfRpcPortRangeIsInvalid() throws Exception {
        final Configuration config = new Configuration(TaskManagerRunnerConfigurationTest.createFlinkConfigWithPredefinedTaskManagerHostname("example.org"));
        config.setString(RPC_PORT, "-1");
        final HighAvailabilityServices highAvailabilityServices = createHighAvailabilityServices(config);
        try {
            TaskManagerRunner.createRpcService(config, highAvailabilityServices);
            Assert.fail("Should fail because -1 is not a valid port range");
        } catch (final IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("Invalid port range definition: -1"));
        } finally {
            highAvailabilityServices.closeAndCleanupAllData();
        }
    }

    @Test
    public void testDefaultFsParameterLoading() throws Exception {
        try {
            final File tmpDir = temporaryFolder.newFolder();
            final File confFile = new File(tmpDir, GlobalConfiguration.FLINK_CONF_FILENAME);
            final URI defaultFS = new URI("otherFS", null, "localhost", 1234, null, null, null);
            final PrintWriter pw1 = new PrintWriter(confFile);
            pw1.println(("fs.default-scheme: " + defaultFS));
            pw1.close();
            String[] args = new String[]{ "--configDir", tmpDir.toString() };
            Configuration configuration = TaskManagerRunner.loadConfiguration(args);
            FileSystem.initialize(configuration);
            Assert.assertEquals(defaultFS, FileSystem.getDefaultFsUri());
        } finally {
            // reset FS settings
            FileSystem.initialize(new Configuration());
        }
    }
}

