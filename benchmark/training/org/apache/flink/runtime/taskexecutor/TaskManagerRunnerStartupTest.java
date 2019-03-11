/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.taskexecutor;


import CoreOptions.TMP_DIRS;
import TaskManagerOptions.DATA_PORT;
import TaskManagerOptions.MANAGED_MEMORY_PRE_ALLOCATE;
import TaskManagerOptions.MANAGED_MEMORY_SIZE;
import TaskManagerOptions.MEMORY_SEGMENT_SIZE;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import org.apache.commons.io.FileUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.IllegalConfigurationException;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.runtime.highavailability.TestingHighAvailabilityServices;
import org.apache.flink.runtime.rpc.RpcService;
import org.apache.flink.util.IOUtils;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests that check how the {@link TaskManagerRunner} behaves when encountering startup problems.
 */
public class TaskManagerRunnerStartupTest extends TestLogger {
    private static final String LOCAL_HOST = "localhost";

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    private final RpcService rpcService = TaskManagerRunnerStartupTest.createRpcService();

    private TestingHighAvailabilityServices highAvailabilityServices;

    /**
     * Tests that the TaskManagerRunner startup fails synchronously when the I/O
     * directories are not writable.
     */
    @Test
    public void testIODirectoryNotWritable() throws Exception {
        File nonWritable = tempFolder.newFolder();
        Assume.assumeTrue("Cannot create non-writable temporary file directory. Skipping test.", nonWritable.setWritable(false, false));
        try {
            Configuration cfg = new Configuration();
            cfg.setString(TMP_DIRS, nonWritable.getAbsolutePath());
            try {
                TaskManagerRunnerStartupTest.startTaskManager(cfg, rpcService, highAvailabilityServices);
                Assert.fail("Should fail synchronously with an IOException");
            } catch (IOException e) {
                // splendid!
            }
        } finally {
            // noinspection ResultOfMethodCallIgnored
            nonWritable.setWritable(true, false);
            try {
                FileUtils.deleteDirectory(nonWritable);
            } catch (IOException e) {
                // best effort
            }
        }
    }

    /**
     * Tests that the TaskManagerRunner startup fails synchronously when the memory configuration is wrong.
     */
    @Test
    public void testMemoryConfigWrong() throws Exception {
        Configuration cfg = new Configuration();
        cfg.setBoolean(MANAGED_MEMORY_PRE_ALLOCATE, true);
        // something invalid
        cfg.setString(MANAGED_MEMORY_SIZE, "-42m");
        try {
            TaskManagerRunnerStartupTest.startTaskManager(cfg, rpcService, highAvailabilityServices);
            Assert.fail("Should fail synchronously with an exception");
        } catch (IllegalConfigurationException e) {
            // splendid!
        }
        // something ridiculously high
        final long memSize = ((((long) (Integer.MAX_VALUE)) - 1) * (MemorySize.parse(MEMORY_SEGMENT_SIZE.defaultValue()).getBytes())) >> 20;
        cfg.setString(MANAGED_MEMORY_SIZE, (memSize + "m"));
        try {
            TaskManagerRunnerStartupTest.startTaskManager(cfg, rpcService, highAvailabilityServices);
            Assert.fail("Should fail synchronously with an exception");
        } catch (Exception e) {
            // splendid!
            Assert.assertTrue(((e.getCause()) instanceof OutOfMemoryError));
        }
    }

    /**
     * Tests that the TaskManagerRunner startup fails if the network stack cannot be initialized.
     */
    @Test
    public void testStartupWhenNetworkStackFailsToInitialize() throws Exception {
        final ServerSocket blocker = new ServerSocket(0, 50, InetAddress.getByName(TaskManagerRunnerStartupTest.LOCAL_HOST));
        try {
            final Configuration cfg = new Configuration();
            cfg.setInteger(DATA_PORT, blocker.getLocalPort());
            TaskManagerRunnerStartupTest.startTaskManager(cfg, rpcService, highAvailabilityServices);
            Assert.fail("Should throw IOException when the network stack cannot be initialized.");
        } catch (IOException e) {
            // ignored
        } finally {
            IOUtils.closeQuietly(blocker);
        }
    }
}

