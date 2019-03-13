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
package org.apache.hadoop.yarn.server.nodemanager;


import ContainerReapContext.Builder;
import Shell.WINUTILS;
import YarnConfiguration.DEFAULT_NM_VCORES;
import YarnConfiguration.NM_CONTAINER_EXECUTOR_SCHED_PRIORITY;
import YarnConfiguration.NM_ENABLE_HARDWARE_CAPABILITY_DETECTION;
import YarnConfiguration.NM_RESOURCE_PERCENTAGE_PHYSICAL_CPU_LIMIT;
import YarnConfiguration.NM_WINDOWS_CONTAINER_CPU_LIMIT_ENABLED;
import YarnConfiguration.NM_WINDOWS_CONTAINER_MEMORY_LIMIT_ENABLED;
import com.google.common.collect.Lists;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.Shell;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.runtime.ContainerExecutionException;
import org.apache.hadoop.yarn.server.nodemanager.executor.ContainerExecContext;
import org.apache.hadoop.yarn.server.nodemanager.executor.ContainerReapContext;
import org.apache.hadoop.yarn.server.nodemanager.util.NodeManagerHardwareUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


@SuppressWarnings("deprecation")
public class TestContainerExecutor {
    private ContainerExecutor containerExecutor = new DefaultContainerExecutor();

    @Test(timeout = 5000)
    public void testRunCommandNoPriority() throws Exception {
        Configuration conf = new Configuration();
        String[] command = containerExecutor.getRunCommand("echo", "group1", "user", null, conf);
        Assert.assertTrue("first command should be the run command for the platform", ((command[0].equals(WINUTILS)) || (command[0].equals("bash"))));
    }

    @Test(timeout = 5000)
    public void testRunCommandwithPriority() throws Exception {
        Configuration conf = new Configuration();
        conf.setInt(NM_CONTAINER_EXECUTOR_SCHED_PRIORITY, 2);
        String[] command = containerExecutor.getRunCommand("echo", "group1", "user", null, conf);
        if (Shell.WINDOWS) {
            // windows doesn't currently support
            Assert.assertEquals("first command should be the run command for the platform", WINUTILS, command[0]);
        } else {
            Assert.assertEquals("first command should be nice", "nice", command[0]);
            Assert.assertEquals("second command should be -n", "-n", command[1]);
            Assert.assertEquals("third command should be the priority", Integer.toString(2), command[2]);
        }
        // test with negative number
        conf.setInt(NM_CONTAINER_EXECUTOR_SCHED_PRIORITY, (-5));
        command = containerExecutor.getRunCommand("echo", "group1", "user", null, conf);
        if (Shell.WINDOWS) {
            // windows doesn't currently support
            Assert.assertEquals("first command should be the run command for the platform", WINUTILS, command[0]);
        } else {
            Assert.assertEquals("first command should be nice", "nice", command[0]);
            Assert.assertEquals("second command should be -n", "-n", command[1]);
            Assert.assertEquals("third command should be the priority", Integer.toString((-5)), command[2]);
        }
    }

    @Test(timeout = 5000)
    public void testRunCommandWithNoResources() {
        assumeWindows();
        Configuration conf = new Configuration();
        String[] command = containerExecutor.getRunCommand("echo", "group1", null, null, conf, Resource.newInstance(1024, 1));
        // Assert the cpu and memory limits are set correctly in the command
        String[] expected = new String[]{ Shell.WINUTILS, "task", "create", "-m", "-1", "-c", "-1", "group1", "cmd /c " + "echo" };
        Assert.assertTrue(Arrays.equals(expected, command));
    }

    @Test(timeout = 5000)
    public void testRunCommandWithMemoryOnlyResources() {
        assumeWindows();
        Configuration conf = new Configuration();
        conf.set(NM_WINDOWS_CONTAINER_MEMORY_LIMIT_ENABLED, "true");
        String[] command = containerExecutor.getRunCommand("echo", "group1", null, null, conf, Resource.newInstance(1024, 1));
        // Assert the cpu and memory limits are set correctly in the command
        String[] expected = new String[]{ Shell.WINUTILS, "task", "create", "-m", "1024", "-c", "-1", "group1", "cmd /c " + "echo" };
        Assert.assertTrue(Arrays.equals(expected, command));
    }

    @Test(timeout = 5000)
    public void testRunCommandWithCpuAndMemoryResources() {
        assumeWindows();
        int containerCores = 1;
        Configuration conf = new Configuration();
        conf.set(NM_WINDOWS_CONTAINER_CPU_LIMIT_ENABLED, "true");
        conf.set(NM_WINDOWS_CONTAINER_MEMORY_LIMIT_ENABLED, "true");
        String[] command = containerExecutor.getRunCommand("echo", "group1", null, null, conf, Resource.newInstance(1024, 1));
        int nodeVCores = NodeManagerHardwareUtils.getVCores(conf);
        Assert.assertEquals(DEFAULT_NM_VCORES, nodeVCores);
        int cpuRate = Math.min(10000, ((containerCores * 10000) / nodeVCores));
        // Assert the cpu and memory limits are set correctly in the command
        String[] expected = new String[]{ Shell.WINUTILS, "task", "create", "-m", "1024", "-c", String.valueOf(cpuRate), "group1", "cmd /c " + "echo" };
        Assert.assertEquals(Arrays.toString(expected), Arrays.toString(command));
        conf.setBoolean(NM_ENABLE_HARDWARE_CAPABILITY_DETECTION, true);
        int nodeCPUs = NodeManagerHardwareUtils.getNodeCPUs(conf);
        float yarnCPUs = NodeManagerHardwareUtils.getContainersCPUs(conf);
        nodeVCores = NodeManagerHardwareUtils.getVCores(conf);
        Assert.assertEquals(nodeCPUs, ((int) (yarnCPUs)));
        Assert.assertEquals(nodeCPUs, nodeVCores);
        command = containerExecutor.getRunCommand("echo", "group1", null, null, conf, Resource.newInstance(1024, 1));
        cpuRate = Math.min(10000, ((containerCores * 10000) / nodeVCores));
        expected[6] = String.valueOf(cpuRate);
        Assert.assertEquals(Arrays.toString(expected), Arrays.toString(command));
        int yarnCpuLimit = 80;
        conf.setInt(NM_RESOURCE_PERCENTAGE_PHYSICAL_CPU_LIMIT, yarnCpuLimit);
        yarnCPUs = NodeManagerHardwareUtils.getContainersCPUs(conf);
        nodeVCores = NodeManagerHardwareUtils.getVCores(conf);
        Assert.assertEquals((nodeCPUs * 0.8), yarnCPUs, 0.01);
        if (nodeCPUs == 1) {
            Assert.assertEquals(1, nodeVCores);
        } else {
            Assert.assertEquals(((int) (nodeCPUs * 0.8)), nodeVCores);
        }
        command = containerExecutor.getRunCommand("echo", "group1", null, null, conf, Resource.newInstance(1024, 1));
        // we should get 100 * (1/nodeVcores) of 80% of CPU
        int containerPerc = (yarnCpuLimit * containerCores) / nodeVCores;
        cpuRate = Math.min(10000, (100 * containerPerc));
        expected[6] = String.valueOf(cpuRate);
        Assert.assertEquals(Arrays.toString(expected), Arrays.toString(command));
    }

    @Test
    public void testReapContainer() throws Exception {
        Container container = Mockito.mock(Container.class);
        ContainerReapContext.Builder builder = new ContainerReapContext.Builder();
        builder.setContainer(container).setUser("foo");
        Assert.assertTrue(containerExecutor.reapContainer(builder.build()));
    }

    @Test
    public void testExecContainer() throws Exception {
        Container container = Mockito.mock(Container.class);
        try {
            ContainerExecContext.Builder builder = new ContainerExecContext.Builder();
            builder.setUser("foo").setAppId("app1").setContainer(container);
            ContainerExecContext ctx = builder.build();
            containerExecutor.execContainer(ctx);
        } catch (Exception e) {
            // socket exception should be thrown immediately, without RPC retries.
            Assert.assertTrue((e instanceof ContainerExecutionException));
        }
    }

    @Test
    public void testCleanupBeforeLaunch() throws Exception {
        Container container = Mockito.mock(Container.class);
        Path linkName = Paths.get("target/linkName");
        Path target = Paths.get("target");
        // deletes the link if it already exists because of previous test failures
        FileUtils.deleteQuietly(linkName.toFile());
        Files.createSymbolicLink(linkName.toAbsolutePath(), target.toAbsolutePath());
        Map<org.apache.hadoop.fs.Path, List<String>> localResources = new HashMap<>();
        localResources.put(new org.apache.hadoop.fs.Path(target.toFile().getAbsolutePath()), Lists.newArrayList(linkName.toFile().getAbsolutePath()));
        Mockito.when(container.getLocalizedResources()).thenReturn(localResources);
        Mockito.when(container.getUser()).thenReturn(System.getProperty("user.name"));
        containerExecutor.cleanupBeforeRelaunch(container);
        Assert.assertTrue((!(Files.exists(linkName))));
    }
}

