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
package org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources;


import CGroupsHandler.CGROUP_PARAM_MEMORY_HARD_LIMIT_BYTES;
import CGroupsHandler.CGROUP_PARAM_MEMORY_SOFT_LIMIT_BYTES;
import CGroupsHandler.CGROUP_PARAM_MEMORY_SWAPPINESS;
import CGroupsHandler.CGroupController.MEMORY;
import ExecutionType.OPPORTUNISTIC;
import PrivilegedOperation.OperationType.ADD_PID_TO_CGROUP;
import YarnConfiguration.NM_MEMORY_RESOURCE_CGROUPS_SWAPPINESS;
import YarnConfiguration.NM_MEMORY_RESOURCE_ENFORCED;
import YarnConfiguration.NM_PMEM_CHECK_ENABLED;
import YarnConfiguration.NM_VMEM_CHECK_ENABLED;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.security.ContainerTokenIdentifier;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperation;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Unit test for CGroupsMemoryResourceHandlerImpl.
 */
public class TestCGroupsMemoryResourceHandlerImpl {
    private CGroupsHandler mockCGroupsHandler;

    private CGroupsMemoryResourceHandlerImpl cGroupsMemoryResourceHandler;

    @Test
    public void testBootstrap() throws Exception {
        Configuration conf = new YarnConfiguration();
        conf.setBoolean(NM_PMEM_CHECK_ENABLED, false);
        conf.setBoolean(NM_VMEM_CHECK_ENABLED, false);
        List<PrivilegedOperation> ret = cGroupsMemoryResourceHandler.bootstrap(conf);
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).initializeCGroupController(MEMORY);
        Assert.assertNull(ret);
        Assert.assertEquals("Default swappiness value incorrect", 0, cGroupsMemoryResourceHandler.getSwappiness());
        conf.setBoolean(NM_PMEM_CHECK_ENABLED, true);
        try {
            cGroupsMemoryResourceHandler.bootstrap(conf);
        } catch (ResourceHandlerException re) {
            Assert.fail("Pmem check should be allowed to run with cgroups");
        }
        conf.setBoolean(NM_PMEM_CHECK_ENABLED, false);
        conf.setBoolean(NM_VMEM_CHECK_ENABLED, true);
        try {
            cGroupsMemoryResourceHandler.bootstrap(conf);
        } catch (ResourceHandlerException re) {
            Assert.fail("Vmem check should be allowed to run with cgroups");
        }
    }

    @Test
    public void testSwappinessValues() throws Exception {
        Configuration conf = new YarnConfiguration();
        conf.setBoolean(NM_PMEM_CHECK_ENABLED, false);
        conf.setBoolean(NM_VMEM_CHECK_ENABLED, false);
        conf.setInt(NM_MEMORY_RESOURCE_CGROUPS_SWAPPINESS, (-1));
        try {
            cGroupsMemoryResourceHandler.bootstrap(conf);
            Assert.fail("Negative values for swappiness should not be allowed.");
        } catch (ResourceHandlerException re) {
            // do nothing
        }
        try {
            conf.setInt(NM_MEMORY_RESOURCE_CGROUPS_SWAPPINESS, 101);
            cGroupsMemoryResourceHandler.bootstrap(conf);
            Assert.fail(("Values greater than 100 for swappiness" + " should not be allowed."));
        } catch (ResourceHandlerException re) {
            // do nothing
        }
        conf.setInt(NM_MEMORY_RESOURCE_CGROUPS_SWAPPINESS, 60);
        cGroupsMemoryResourceHandler.bootstrap(conf);
        Assert.assertEquals("Swappiness value incorrect", 60, cGroupsMemoryResourceHandler.getSwappiness());
    }

    @Test
    public void testPreStart() throws Exception {
        Configuration conf = new Configuration();
        conf.setBoolean(NM_PMEM_CHECK_ENABLED, false);
        conf.setBoolean(NM_VMEM_CHECK_ENABLED, false);
        cGroupsMemoryResourceHandler.bootstrap(conf);
        String id = "container_01_01";
        String path = "test-path/" + id;
        ContainerId mockContainerId = Mockito.mock(ContainerId.class);
        Mockito.when(mockContainerId.toString()).thenReturn(id);
        Container mockContainer = Mockito.mock(Container.class);
        Mockito.when(mockContainer.getContainerId()).thenReturn(mockContainerId);
        Mockito.when(mockCGroupsHandler.getPathForCGroupTasks(MEMORY, id)).thenReturn(path);
        int memory = 1024;
        Mockito.when(mockContainer.getResource()).thenReturn(Resource.newInstance(memory, 1));
        List<PrivilegedOperation> ret = cGroupsMemoryResourceHandler.preStart(mockContainer);
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).createCGroup(MEMORY, id);
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).updateCGroupParam(MEMORY, id, CGROUP_PARAM_MEMORY_HARD_LIMIT_BYTES, ((String.valueOf(memory)) + "M"));
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).updateCGroupParam(MEMORY, id, CGROUP_PARAM_MEMORY_SOFT_LIMIT_BYTES, ((String.valueOf(((int) (memory * 0.9)))) + "M"));
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).updateCGroupParam(MEMORY, id, CGROUP_PARAM_MEMORY_SWAPPINESS, String.valueOf(0));
        Assert.assertNotNull(ret);
        Assert.assertEquals(1, ret.size());
        PrivilegedOperation op = ret.get(0);
        Assert.assertEquals(ADD_PID_TO_CGROUP, op.getOperationType());
        List<String> args = op.getArguments();
        Assert.assertEquals(1, args.size());
        Assert.assertEquals(((PrivilegedOperation.CGROUP_ARG_PREFIX) + path), args.get(0));
    }

    @Test
    public void testPreStartNonEnforced() throws Exception {
        Configuration conf = new Configuration();
        conf.setBoolean(NM_PMEM_CHECK_ENABLED, false);
        conf.setBoolean(NM_VMEM_CHECK_ENABLED, false);
        conf.setBoolean(NM_MEMORY_RESOURCE_ENFORCED, false);
        cGroupsMemoryResourceHandler.bootstrap(conf);
        String id = "container_01_01";
        String path = "test-path/" + id;
        ContainerId mockContainerId = Mockito.mock(ContainerId.class);
        Mockito.when(mockContainerId.toString()).thenReturn(id);
        Container mockContainer = Mockito.mock(Container.class);
        Mockito.when(mockContainer.getContainerId()).thenReturn(mockContainerId);
        Mockito.when(mockCGroupsHandler.getPathForCGroupTasks(MEMORY, id)).thenReturn(path);
        int memory = 1024;
        Mockito.when(mockContainer.getResource()).thenReturn(Resource.newInstance(memory, 1));
        List<PrivilegedOperation> ret = cGroupsMemoryResourceHandler.preStart(mockContainer);
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).createCGroup(MEMORY, id);
        Mockito.verify(mockCGroupsHandler, Mockito.times(0)).updateCGroupParam(MEMORY, id, CGROUP_PARAM_MEMORY_HARD_LIMIT_BYTES, ((String.valueOf(memory)) + "M"));
        Mockito.verify(mockCGroupsHandler, Mockito.times(0)).updateCGroupParam(MEMORY, id, CGROUP_PARAM_MEMORY_SOFT_LIMIT_BYTES, ((String.valueOf(((int) (memory * 0.9)))) + "M"));
        Mockito.verify(mockCGroupsHandler, Mockito.times(0)).updateCGroupParam(MEMORY, id, CGROUP_PARAM_MEMORY_SWAPPINESS, String.valueOf(0));
        Assert.assertNotNull(ret);
        Assert.assertEquals(1, ret.size());
        PrivilegedOperation op = ret.get(0);
        Assert.assertEquals(ADD_PID_TO_CGROUP, op.getOperationType());
        List<String> args = op.getArguments();
        Assert.assertEquals(1, args.size());
        Assert.assertEquals(((PrivilegedOperation.CGROUP_ARG_PREFIX) + path), args.get(0));
    }

    @Test
    public void testReacquireContainer() throws Exception {
        ContainerId containerIdMock = Mockito.mock(ContainerId.class);
        Assert.assertNull(cGroupsMemoryResourceHandler.reacquireContainer(containerIdMock));
    }

    @Test
    public void testPostComplete() throws Exception {
        String id = "container_01_01";
        ContainerId mockContainerId = Mockito.mock(ContainerId.class);
        Mockito.when(mockContainerId.toString()).thenReturn(id);
        Assert.assertNull(cGroupsMemoryResourceHandler.postComplete(mockContainerId));
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).deleteCGroup(MEMORY, id);
    }

    @Test
    public void testTeardown() throws Exception {
        Assert.assertNull(cGroupsMemoryResourceHandler.teardown());
    }

    @Test
    public void testOpportunistic() throws Exception {
        Configuration conf = new YarnConfiguration();
        conf.setBoolean(NM_PMEM_CHECK_ENABLED, false);
        conf.setBoolean(NM_VMEM_CHECK_ENABLED, false);
        cGroupsMemoryResourceHandler.bootstrap(conf);
        ContainerTokenIdentifier tokenId = Mockito.mock(ContainerTokenIdentifier.class);
        Mockito.when(tokenId.getExecutionType()).thenReturn(OPPORTUNISTIC);
        Container container = Mockito.mock(Container.class);
        String id = "container_01_01";
        ContainerId mockContainerId = Mockito.mock(ContainerId.class);
        Mockito.when(mockContainerId.toString()).thenReturn(id);
        Mockito.when(container.getContainerId()).thenReturn(mockContainerId);
        Mockito.when(container.getContainerTokenIdentifier()).thenReturn(tokenId);
        Mockito.when(container.getResource()).thenReturn(Resource.newInstance(1024, 2));
        cGroupsMemoryResourceHandler.preStart(container);
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).updateCGroupParam(MEMORY, id, CGROUP_PARAM_MEMORY_SOFT_LIMIT_BYTES, "0M");
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).updateCGroupParam(MEMORY, id, CGROUP_PARAM_MEMORY_SWAPPINESS, "100");
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).updateCGroupParam(MEMORY, id, CGROUP_PARAM_MEMORY_HARD_LIMIT_BYTES, "1024M");
    }
}

