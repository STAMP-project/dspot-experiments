/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.apache.storm;


import Config.STORM_CGROUP_HIERARCHY_DIR;
import DaemonConfig.STORM_CGROUP_CGEXEC_CMD;
import DaemonConfig.STORM_RESOURCE_ISOLATION_PLUGIN_ENABLE;
import DaemonConfig.STORM_SUPERVISOR_CGROUP_ROOTDIR;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.storm.container.cgroup.CgroupManager;
import org.apache.storm.utils.Utils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Unit tests for CGroups
 */
public class TestCgroups {
    private static final Logger LOG = LoggerFactory.getLogger(TestCgroups.class);

    /**
     * Test whether cgroups are setup up correctly for use.  Also tests whether Cgroups produces the right command to
     * start a worker and cleans up correctly after the worker is shutdown
     */
    @Test
    public void testSetupAndTearDown() throws IOException {
        Config config = new Config();
        config.putAll(Utils.readDefaultConfig());
        // We don't want to run the test is CGroups are not setup
        Assume.assumeTrue("Check if CGroups are setup", (((boolean) (config.get(STORM_RESOURCE_ISOLATION_PLUGIN_ENABLE))) == true));
        Assert.assertTrue("Check if STORM_CGROUP_HIERARCHY_DIR exists", stormCgroupHierarchyExists(config));
        Assert.assertTrue("Check if STORM_SUPERVISOR_CGROUP_ROOTDIR exists", stormCgroupSupervisorRootDirExists(config));
        CgroupManager manager = new CgroupManager();
        manager.prepare(config);
        String workerId = UUID.randomUUID().toString();
        manager.reserveResourcesForWorker(workerId, 1024, 200);
        List<String> commandList = manager.getLaunchCommand(workerId, new ArrayList<String>());
        StringBuilder command = new StringBuilder();
        for (String entry : commandList) {
            command.append(entry).append(" ");
        }
        String correctCommand1 = (((((config.get(STORM_CGROUP_CGEXEC_CMD)) + " -g memory,cpu:/") + (config.get(STORM_SUPERVISOR_CGROUP_ROOTDIR))) + "/") + workerId) + " ";
        String correctCommand2 = (((((config.get(STORM_CGROUP_CGEXEC_CMD)) + " -g cpu,memory:/") + (config.get(STORM_SUPERVISOR_CGROUP_ROOTDIR))) + "/") + workerId) + " ";
        Assert.assertTrue("Check if cgroup launch command is correct", ((command.toString().equals(correctCommand1)) || (command.toString().equals(correctCommand2))));
        String pathToWorkerCgroupDir = (((((String) (config.get(STORM_CGROUP_HIERARCHY_DIR))) + "/") + ((String) (config.get(STORM_SUPERVISOR_CGROUP_ROOTDIR)))) + "/") + workerId;
        Assert.assertTrue("Check if cgroup directory exists for worker", dirExists(pathToWorkerCgroupDir));
        /* validate cpu settings */
        String pathToCpuShares = pathToWorkerCgroupDir + "/cpu.shares";
        Assert.assertTrue("Check if cpu.shares file exists", fileExists(pathToCpuShares));
        Assert.assertEquals("Check if the correct value is written into cpu.shares", "200", readFileAll(pathToCpuShares));
        /* validate memory settings */
        String pathTomemoryLimitInBytes = pathToWorkerCgroupDir + "/memory.limit_in_bytes";
        Assert.assertTrue("Check if memory.limit_in_bytes file exists", fileExists(pathTomemoryLimitInBytes));
        Assert.assertEquals("Check if the correct value is written into memory.limit_in_bytes", String.valueOf(((1024 * 1024) * 1024)), readFileAll(pathTomemoryLimitInBytes));
        manager.releaseResourcesForWorker(workerId);
        Assert.assertFalse("Make sure cgroup was removed properly", dirExists(pathToWorkerCgroupDir));
    }
}

