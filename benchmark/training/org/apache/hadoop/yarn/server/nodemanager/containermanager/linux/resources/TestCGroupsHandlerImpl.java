/**
 * *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements. See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership. The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */
package org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources;


import CGroupsHandler.CGroupController;
import CGroupsHandler.CGroupController.BLKIO;
import CGroupsHandler.CGroupController.CPU;
import PrivilegedOperation.OperationType;
import YarnConfiguration.NM_LINUX_CONTAINER_CGROUPS_HIERARCHY;
import YarnConfiguration.NM_LINUX_CONTAINER_CGROUPS_MOUNT_PATH;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Permission;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperation;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperationException;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperationExecutor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static CGroupsHandler.CGROUP_PARAM_CLASSID;
import static CGroupsHandler.CGROUP_PROCS_FILE;


/**
 * Tests for the CGroups handler implementation.
 */
public class TestCGroupsHandlerImpl {
    private static final Logger LOG = LoggerFactory.getLogger(TestCGroupsHandlerImpl.class);

    private PrivilegedOperationExecutor privilegedOperationExecutorMock;

    private String tmpPath;

    private String hierarchy;

    private CGroupController controller;

    private String controllerPath;

    /**
     * Security manager simulating access denied.
     */
    private class MockSecurityManagerDenyWrite extends SecurityManager {
        @Override
        public void checkPermission(Permission perm) {
            if (perm.getActions().equals("write")) {
                throw new SecurityException("Mock not allowed");
            }
        }
    }

    @Test
    public void testMountController() throws IOException {
        File parentDir = new File(tmpPath);
        File cgroup = new File(parentDir, controller.getName());
        Assert.assertTrue("cgroup dir should be cerated", cgroup.mkdirs());
        // Since we enabled (deferred) cgroup controller mounting, no interactions
        // should have occurred, with this mock
        Mockito.verifyZeroInteractions(privilegedOperationExecutorMock);
        File emptyMtab = createEmptyCgroups();
        try {
            CGroupsHandler cGroupsHandler = new CGroupsHandlerImpl(createMountConfiguration(), privilegedOperationExecutorMock, emptyMtab.getAbsolutePath());
            PrivilegedOperation expectedOp = new PrivilegedOperation(OperationType.MOUNT_CGROUPS);
            // This is expected to be of the form :
            // net_cls=<mount_path>/net_cls
            String controllerKV = ((((controller.getName()) + "=") + (tmpPath)) + (Path.SEPARATOR)) + (controller.getName());
            expectedOp.appendArgs(hierarchy, controllerKV);
            cGroupsHandler.initializeCGroupController(controller);
            try {
                ArgumentCaptor<PrivilegedOperation> opCaptor = ArgumentCaptor.forClass(PrivilegedOperation.class);
                Mockito.verify(privilegedOperationExecutorMock).executePrivilegedOperation(opCaptor.capture(), ArgumentMatchers.eq(false));
                // we'll explicitly capture and assert that the
                // captured op and the expected op are identical.
                Assert.assertEquals(expectedOp, opCaptor.getValue());
                Mockito.verifyNoMoreInteractions(privilegedOperationExecutorMock);
                // Try mounting the same controller again - this should be a no-op
                cGroupsHandler.initializeCGroupController(controller);
                Mockito.verifyNoMoreInteractions(privilegedOperationExecutorMock);
            } catch (PrivilegedOperationException e) {
                TestCGroupsHandlerImpl.LOG.error(("Caught exception: " + e));
                Assert.assertTrue("Unexpected PrivilegedOperationException from mock!", false);
            }
        } catch (ResourceHandlerException e) {
            TestCGroupsHandlerImpl.LOG.error(("Caught exception: " + e));
            Assert.assertTrue("Unexpected ResourceHandler Exception!", false);
        }
    }

    @Test
    public void testCGroupPaths() throws IOException {
        // As per junit behavior, we expect a new mock object to be available
        // in this test.
        Mockito.verifyZeroInteractions(privilegedOperationExecutorMock);
        CGroupsHandler cGroupsHandler = null;
        File mtab = createEmptyCgroups();
        // Lets manually create a path to (partially) simulate a controller mounted
        // later in the test. This is required because the handler uses a mocked
        // privileged operation executor
        Assert.assertTrue("Sample subsystem should be created", new File(controllerPath).mkdirs());
        try {
            cGroupsHandler = new CGroupsHandlerImpl(createMountConfiguration(), privilegedOperationExecutorMock, mtab.getAbsolutePath());
            cGroupsHandler.initializeCGroupController(controller);
        } catch (ResourceHandlerException e) {
            TestCGroupsHandlerImpl.LOG.error(("Caught exception: " + e));
            Assert.assertTrue("Unexpected ResourceHandlerException when mounting controller!", false);
        }
        String testCGroup = "container_01";
        String expectedPath = ((controllerPath) + (Path.SEPARATOR)) + testCGroup;
        String path = cGroupsHandler.getPathForCGroup(controller, testCGroup);
        Assert.assertEquals(expectedPath, path);
        String expectedPathTasks = (expectedPath + (Path.SEPARATOR)) + (CGROUP_PROCS_FILE);
        path = cGroupsHandler.getPathForCGroupTasks(controller, testCGroup);
        Assert.assertEquals(expectedPathTasks, path);
        String param = CGROUP_PARAM_CLASSID;
        String expectedPathParam = (((expectedPath + (Path.SEPARATOR)) + (controller.getName())) + ".") + param;
        path = cGroupsHandler.getPathForCGroupParam(controller, testCGroup, param);
        Assert.assertEquals(expectedPathParam, path);
    }

    @Test
    public void testCGroupOperations() throws IOException {
        // As per junit behavior, we expect a new mock object to be available
        // in this test.
        Mockito.verifyZeroInteractions(privilegedOperationExecutorMock);
        CGroupsHandler cGroupsHandler = null;
        File mtab = createEmptyCgroups();
        // Lets manually create a path to (partially) simulate a controller mounted
        // later in the test. This is required because the handler uses a mocked
        // privileged operation executor
        Assert.assertTrue("Sample subsystem should be created", new File(controllerPath).mkdirs());
        try {
            cGroupsHandler = new CGroupsHandlerImpl(createMountConfiguration(), privilegedOperationExecutorMock, mtab.getAbsolutePath());
            cGroupsHandler.initializeCGroupController(controller);
        } catch (ResourceHandlerException e) {
            TestCGroupsHandlerImpl.LOG.error(("Caught exception: " + e));
            Assert.assertTrue("Unexpected ResourceHandlerException when mounting controller!", false);
        }
        String testCGroup = "container_01";
        String expectedPath = ((controllerPath) + (Path.SEPARATOR)) + testCGroup;
        try {
            String path = cGroupsHandler.createCGroup(controller, testCGroup);
            Assert.assertTrue(new File(expectedPath).exists());
            Assert.assertEquals(expectedPath, path);
            // update param and read param tests.
            // We don't use net_cls.classid because as a test param here because
            // cgroups provides very specific read/write semantics for classid (only
            // numbers can be written - potentially as hex but can be read out only
            // as decimal)
            String param = "test_param";
            String paramValue = "test_param_value";
            cGroupsHandler.updateCGroupParam(controller, testCGroup, param, paramValue);
            String paramPath = (((expectedPath + (Path.SEPARATOR)) + (controller.getName())) + ".") + param;
            File paramFile = new File(paramPath);
            Assert.assertTrue(paramFile.exists());
            try {
                Assert.assertEquals(paramValue, new String(Files.readAllBytes(paramFile.toPath())));
            } catch (IOException e) {
                TestCGroupsHandlerImpl.LOG.error(("Caught exception: " + e));
                Assert.fail("Unexpected IOException trying to read cgroup param!");
            }
            Assert.assertEquals(paramValue, cGroupsHandler.getCGroupParam(controller, testCGroup, param));
            // We can't really do a delete test here. Linux cgroups
            // implementation provides additional semantics - the cgroup cannot be
            // deleted if there are any tasks still running in the cgroup even if
            // the user attempting the delete has the file permissions to do so - we
            // cannot simulate that here. Even if we create a dummy 'tasks' file, we
            // wouldn't be able to simulate the delete behavior we need, since a cgroup
            // can be deleted using using 'rmdir' if the tasks file is empty. Such a
            // delete is not possible with a regular non-empty directory.
        } catch (ResourceHandlerException e) {
            TestCGroupsHandlerImpl.LOG.error(("Caught exception: " + e));
            Assert.fail("Unexpected ResourceHandlerException during cgroup operations!");
        }
    }

    /**
     * Tests whether mtab parsing works as expected with a valid hierarchy set.
     *
     * @throws Exception
     * 		the test will fail
     */
    @Test
    public void testMtabParsing() throws Exception {
        // Initialize mtab and cgroup dir
        File parentDir = new File(tmpPath);
        // create mock cgroup
        File mockMtabFile = TestCGroupsHandlerImpl.createPremountedCgroups(parentDir, false);
        // Run mtabs parsing
        Map<String, Set<String>> newMtab = CGroupsHandlerImpl.parseMtab(mockMtabFile.getAbsolutePath());
        Map<CGroupsHandler.CGroupController, String> controllerPaths = CGroupsHandlerImpl.initializeControllerPathsFromMtab(newMtab);
        // Verify
        Assert.assertEquals(2, controllerPaths.size());
        Assert.assertTrue(controllerPaths.containsKey(CPU));
        Assert.assertTrue(controllerPaths.containsKey(BLKIO));
        String cpuDir = controllerPaths.get(CPU);
        String blkioDir = controllerPaths.get(BLKIO);
        Assert.assertEquals(((parentDir.getAbsolutePath()) + "/cpu"), cpuDir);
        Assert.assertEquals(((parentDir.getAbsolutePath()) + "/blkio"), blkioDir);
    }

    @Test
    public void testSelectCgroup() throws Exception {
        File cpu = new File(tmpPath, "cpu");
        File cpuNoExist = new File(tmpPath, "cpuNoExist");
        File memory = new File(tmpPath, "memory");
        try {
            CGroupsHandlerImpl handler = new CGroupsHandlerImpl(createNoMountConfiguration(tmpPath), privilegedOperationExecutorMock);
            Map<String, Set<String>> cgroups = new LinkedHashMap<>();
            Assert.assertTrue("temp dir should be created", cpu.mkdirs());
            Assert.assertTrue("temp dir should be created", memory.mkdirs());
            Assert.assertFalse("temp dir should not be created", cpuNoExist.exists());
            cgroups.put(memory.getAbsolutePath(), Collections.singleton("memory"));
            cgroups.put(cpuNoExist.getAbsolutePath(), Collections.singleton("cpu"));
            cgroups.put(cpu.getAbsolutePath(), Collections.singleton("cpu"));
            String selectedCPU = handler.findControllerInMtab("cpu", cgroups);
            Assert.assertEquals("Wrong CPU mount point selected", cpu.getAbsolutePath(), selectedCPU);
        } finally {
            FileUtils.deleteQuietly(cpu);
            FileUtils.deleteQuietly(memory);
        }
    }

    /**
     * Tests whether mtab parsing works as expected with an empty hierarchy set.
     *
     * @throws Exception
     * 		the test will fail
     */
    @Test
    public void testPreMountedControllerEmpty() throws Exception {
        testPreMountedControllerInitialization("");
    }

    /**
     * Tests whether mtab parsing works as expected with a / hierarchy set.
     *
     * @throws Exception
     * 		the test will fail
     */
    @Test
    public void testPreMountedControllerRoot() throws Exception {
        testPreMountedControllerInitialization("/");
    }

    /**
     * Tests whether mtab parsing works as expected with the specified hierarchy.
     *
     * @throws Exception
     * 		the test will fail
     */
    @Test
    public void testRemount() throws Exception {
        // Initialize mount point
        File parentDir = new File(tmpPath);
        final String oldMountPointDir = "oldmount";
        final String newMountPointDir = "newmount";
        File oldMountPoint = new File(parentDir, oldMountPointDir);
        File mtab = TestCGroupsHandlerImpl.createPremountedCgroups(oldMountPoint, true);
        File newMountPoint = new File(parentDir, newMountPointDir);
        Assert.assertTrue("Could not create dirs", new File(newMountPoint, "cpu").mkdirs());
        // Initialize YARN classes
        Configuration confMount = createMountConfiguration();
        confMount.set(NM_LINUX_CONTAINER_CGROUPS_MOUNT_PATH, (((parentDir.getAbsolutePath()) + (Path.SEPARATOR)) + newMountPointDir));
        CGroupsHandlerImpl cGroupsHandler = new CGroupsHandlerImpl(confMount, privilegedOperationExecutorMock, mtab.getAbsolutePath());
        cGroupsHandler.initializeCGroupController(CPU);
        ArgumentCaptor<PrivilegedOperation> opCaptor = ArgumentCaptor.forClass(PrivilegedOperation.class);
        Mockito.verify(privilegedOperationExecutorMock).executePrivilegedOperation(opCaptor.capture(), ArgumentMatchers.eq(false));
        File hierarchyFile = new File(new File(newMountPoint, "cpu"), this.hierarchy);
        Assert.assertTrue("Yarn cgroup should exist", hierarchyFile.exists());
    }

    @Test
    public void testManualCgroupSetting() throws ResourceHandlerException {
        YarnConfiguration conf = new YarnConfiguration();
        conf.set(NM_LINUX_CONTAINER_CGROUPS_MOUNT_PATH, tmpPath);
        conf.set(NM_LINUX_CONTAINER_CGROUPS_HIERARCHY, "/hadoop-yarn");
        File cpu = new File(new File(tmpPath, "cpuacct,cpu"), "/hadoop-yarn");
        try {
            Assert.assertTrue("temp dir should be created", cpu.mkdirs());
            CGroupsHandlerImpl cGroupsHandler = new CGroupsHandlerImpl(conf, null);
            cGroupsHandler.initializeCGroupController(CPU);
            Assert.assertEquals("CPU CGRoup path was not set", cpu.getAbsolutePath(), new File(cGroupsHandler.getPathForCGroup(CPU, "")).getAbsolutePath());
        } finally {
            FileUtils.deleteQuietly(cpu);
        }
    }

    // Remove leading and trailing slashes
    @Test
    public void testCgroupsHierarchySetting() throws ResourceHandlerException {
        YarnConfiguration conf = new YarnConfiguration();
        conf.set(NM_LINUX_CONTAINER_CGROUPS_MOUNT_PATH, tmpPath);
        conf.set(NM_LINUX_CONTAINER_CGROUPS_HIERARCHY, "/hadoop-yarn");
        CGroupsHandlerImpl cGroupsHandler = new CGroupsHandlerImpl(conf, null);
        String expectedRelativePath = "hadoop-yarn/c1";
        Assert.assertEquals(expectedRelativePath, cGroupsHandler.getRelativePathForCGroup("c1"));
        conf.set(NM_LINUX_CONTAINER_CGROUPS_HIERARCHY, "hadoop-yarn");
        cGroupsHandler = new CGroupsHandlerImpl(conf, null);
        Assert.assertEquals(expectedRelativePath, cGroupsHandler.getRelativePathForCGroup("c1"));
        conf.set(NM_LINUX_CONTAINER_CGROUPS_HIERARCHY, "hadoop-yarn/");
        cGroupsHandler = new CGroupsHandlerImpl(conf, null);
        Assert.assertEquals(expectedRelativePath, cGroupsHandler.getRelativePathForCGroup("c1"));
        conf.set(NM_LINUX_CONTAINER_CGROUPS_HIERARCHY, "//hadoop-yarn//");
        cGroupsHandler = new CGroupsHandlerImpl(conf, null);
        Assert.assertEquals(expectedRelativePath, cGroupsHandler.getRelativePathForCGroup("c1"));
        expectedRelativePath = "hadoop-yarn/root/c1";
        conf.set(NM_LINUX_CONTAINER_CGROUPS_HIERARCHY, "//hadoop-yarn/root//");
        cGroupsHandler = new CGroupsHandlerImpl(conf, null);
        Assert.assertEquals(expectedRelativePath, cGroupsHandler.getRelativePathForCGroup("c1"));
    }
}

