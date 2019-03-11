/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources;


import CGroupsCpuResourceHandlerImpl.CPU_DEFAULT_WEIGHT;
import CGroupsCpuResourceHandlerImpl.MAX_QUOTA_US;
import CGroupsHandler.CGROUP_CPU_PERIOD_US;
import CGroupsHandler.CGROUP_CPU_QUOTA_US;
import CGroupsHandler.CGROUP_CPU_SHARES;
import CGroupsHandler.CGroupController.CPU;
import ExecutionType.OPPORTUNISTIC;
import PrivilegedOperation.OperationType.ADD_PID_TO_CGROUP;
import YarnConfiguration.NM_LINUX_CONTAINER_CGROUPS_STRICT_RESOURCE_USAGE;
import YarnConfiguration.NM_RESOURCE_PERCENTAGE_PHYSICAL_CPU_LIMIT;
import java.io.File;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.security.ContainerTokenIdentifier;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperation;
import org.apache.hadoop.yarn.util.ResourceCalculatorPlugin;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static CGroupsCpuResourceHandlerImpl.CPU_DEFAULT_WEIGHT;
import static CGroupsCpuResourceHandlerImpl.MAX_QUOTA_US;
import static CGroupsHandler.CGROUP_CPU_QUOTA_US;


public class TestCGroupsCpuResourceHandlerImpl {
    private CGroupsHandler mockCGroupsHandler;

    private CGroupsCpuResourceHandlerImpl cGroupsCpuResourceHandler;

    private ResourceCalculatorPlugin plugin;

    final int numProcessors = 4;

    @Test
    public void testBootstrap() throws Exception {
        Configuration conf = new YarnConfiguration();
        List<PrivilegedOperation> ret = cGroupsCpuResourceHandler.bootstrap(plugin, conf);
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).initializeCGroupController(CPU);
        Mockito.verify(mockCGroupsHandler, Mockito.times(0)).updateCGroupParam(CPU, "", CGROUP_CPU_PERIOD_US, "");
        Mockito.verify(mockCGroupsHandler, Mockito.times(0)).updateCGroupParam(CPU, "", CGROUP_CPU_QUOTA_US, "");
        Assert.assertNull(ret);
    }

    @Test
    public void testBootstrapLimits() throws Exception {
        Configuration conf = new YarnConfiguration();
        int cpuPerc = 80;
        conf.setInt(NM_RESOURCE_PERCENTAGE_PHYSICAL_CPU_LIMIT, cpuPerc);
        int period = ((MAX_QUOTA_US) * 100) / (cpuPerc * (numProcessors));
        List<PrivilegedOperation> ret = cGroupsCpuResourceHandler.bootstrap(plugin, conf);
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).initializeCGroupController(CPU);
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).updateCGroupParam(CPU, "", CGROUP_CPU_PERIOD_US, String.valueOf(period));
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).updateCGroupParam(CPU, "", CGROUP_CPU_QUOTA_US, String.valueOf(MAX_QUOTA_US));
        Assert.assertNull(ret);
    }

    @Test
    public void testBootstrapExistingLimits() throws Exception {
        File existingLimit = new File((((CPU.getName()) + ".") + (CGROUP_CPU_QUOTA_US)));
        try {
            FileUtils.write(existingLimit, "10000");// value doesn't matter

            Mockito.when(mockCGroupsHandler.getPathForCGroup(CPU, "")).thenReturn(".");
            Configuration conf = new YarnConfiguration();
            List<PrivilegedOperation> ret = cGroupsCpuResourceHandler.bootstrap(plugin, conf);
            Mockito.verify(mockCGroupsHandler, Mockito.times(1)).initializeCGroupController(CPU);
            Mockito.verify(mockCGroupsHandler, Mockito.times(1)).updateCGroupParam(CPU, "", CGROUP_CPU_QUOTA_US, "-1");
            Assert.assertNull(ret);
        } finally {
            FileUtils.deleteQuietly(existingLimit);
        }
    }

    @Test
    public void testPreStart() throws Exception {
        String id = "container_01_01";
        String path = "test-path/" + id;
        ContainerId mockContainerId = Mockito.mock(ContainerId.class);
        Mockito.when(mockContainerId.toString()).thenReturn(id);
        Container mockContainer = Mockito.mock(Container.class);
        Mockito.when(mockContainer.getContainerId()).thenReturn(mockContainerId);
        Mockito.when(mockCGroupsHandler.getPathForCGroupTasks(CPU, id)).thenReturn(path);
        Mockito.when(mockContainer.getResource()).thenReturn(Resource.newInstance(1024, 2));
        List<PrivilegedOperation> ret = cGroupsCpuResourceHandler.preStart(mockContainer);
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).createCGroup(CPU, id);
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).updateCGroupParam(CPU, id, CGROUP_CPU_SHARES, String.valueOf(((CPU_DEFAULT_WEIGHT) * 2)));
        // don't set quota or period
        Mockito.verify(mockCGroupsHandler, Mockito.never()).updateCGroupParam(ArgumentMatchers.eq(CPU), ArgumentMatchers.eq(id), ArgumentMatchers.eq(CGROUP_CPU_PERIOD_US), ArgumentMatchers.anyString());
        Mockito.verify(mockCGroupsHandler, Mockito.never()).updateCGroupParam(ArgumentMatchers.eq(CPU), ArgumentMatchers.eq(id), ArgumentMatchers.eq(CGROUP_CPU_QUOTA_US), ArgumentMatchers.anyString());
        Assert.assertNotNull(ret);
        Assert.assertEquals(1, ret.size());
        PrivilegedOperation op = ret.get(0);
        Assert.assertEquals(ADD_PID_TO_CGROUP, op.getOperationType());
        List<String> args = op.getArguments();
        Assert.assertEquals(1, args.size());
        Assert.assertEquals(((PrivilegedOperation.CGROUP_ARG_PREFIX) + path), args.get(0));
    }

    @Test
    public void testPreStartStrictUsage() throws Exception {
        String id = "container_01_01";
        String path = "test-path/" + id;
        ContainerId mockContainerId = Mockito.mock(ContainerId.class);
        Mockito.when(mockContainerId.toString()).thenReturn(id);
        Container mockContainer = Mockito.mock(Container.class);
        Mockito.when(mockContainer.getContainerId()).thenReturn(mockContainerId);
        Mockito.when(mockCGroupsHandler.getPathForCGroupTasks(CPU, id)).thenReturn(path);
        Mockito.when(mockContainer.getResource()).thenReturn(Resource.newInstance(1024, 1));
        Configuration conf = new YarnConfiguration();
        conf.setBoolean(NM_LINUX_CONTAINER_CGROUPS_STRICT_RESOURCE_USAGE, true);
        cGroupsCpuResourceHandler.bootstrap(plugin, conf);
        int defaultVCores = 8;
        float share = ((float) (numProcessors)) / ((float) (defaultVCores));
        List<PrivilegedOperation> ret = cGroupsCpuResourceHandler.preStart(mockContainer);
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).createCGroup(CPU, id);
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).updateCGroupParam(CPU, id, CGROUP_CPU_SHARES, String.valueOf(CPU_DEFAULT_WEIGHT));
        // set quota and period
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).updateCGroupParam(CPU, id, CGROUP_CPU_PERIOD_US, String.valueOf(MAX_QUOTA_US));
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).updateCGroupParam(CPU, id, CGROUP_CPU_QUOTA_US, String.valueOf(((int) ((MAX_QUOTA_US) * share))));
        Assert.assertNotNull(ret);
        Assert.assertEquals(1, ret.size());
        PrivilegedOperation op = ret.get(0);
        Assert.assertEquals(ADD_PID_TO_CGROUP, op.getOperationType());
        List<String> args = op.getArguments();
        Assert.assertEquals(1, args.size());
        Assert.assertEquals(((PrivilegedOperation.CGROUP_ARG_PREFIX) + path), args.get(0));
    }

    @Test
    public void testPreStartRestrictedContainers() throws Exception {
        String id = "container_01_01";
        String path = "test-path/" + id;
        int defaultVCores = 8;
        Configuration conf = new YarnConfiguration();
        conf.setBoolean(NM_LINUX_CONTAINER_CGROUPS_STRICT_RESOURCE_USAGE, true);
        int cpuPerc = 75;
        conf.setInt(NM_RESOURCE_PERCENTAGE_PHYSICAL_CPU_LIMIT, cpuPerc);
        cGroupsCpuResourceHandler.bootstrap(plugin, conf);
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).updateCGroupParam(CPU, "", CGROUP_CPU_PERIOD_US, String.valueOf("333333"));
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).updateCGroupParam(CPU, "", CGROUP_CPU_QUOTA_US, String.valueOf(MAX_QUOTA_US));
        float yarnCores = (cpuPerc * (numProcessors)) / 100;
        int[] containerVCores = new int[]{ 2, 4 };
        for (int cVcores : containerVCores) {
            ContainerId mockContainerId = Mockito.mock(ContainerId.class);
            Mockito.when(mockContainerId.toString()).thenReturn(id);
            Container mockContainer = Mockito.mock(Container.class);
            Mockito.when(mockContainer.getContainerId()).thenReturn(mockContainerId);
            Mockito.when(mockCGroupsHandler.getPathForCGroupTasks(CPU, id)).thenReturn(path);
            Mockito.when(mockContainer.getResource()).thenReturn(Resource.newInstance(1024, cVcores));
            Mockito.when(mockCGroupsHandler.getPathForCGroupTasks(CPU, id)).thenReturn(path);
            float share = (cVcores * yarnCores) / defaultVCores;
            int quotaUS;
            int periodUS;
            if (share > 1.0F) {
                quotaUS = MAX_QUOTA_US;
                periodUS = ((int) (((float) (MAX_QUOTA_US)) / share));
            } else {
                quotaUS = ((int) ((MAX_QUOTA_US) * share));
                periodUS = MAX_QUOTA_US;
            }
            cGroupsCpuResourceHandler.preStart(mockContainer);
            Mockito.verify(mockCGroupsHandler, Mockito.times(1)).updateCGroupParam(CPU, id, CGROUP_CPU_SHARES, String.valueOf(((CPU_DEFAULT_WEIGHT) * cVcores)));
            // set quota and period
            Mockito.verify(mockCGroupsHandler, Mockito.times(1)).updateCGroupParam(CPU, id, CGROUP_CPU_PERIOD_US, String.valueOf(periodUS));
            Mockito.verify(mockCGroupsHandler, Mockito.times(1)).updateCGroupParam(CPU, id, CGROUP_CPU_QUOTA_US, String.valueOf(quotaUS));
        }
    }

    @Test
    public void testReacquireContainer() throws Exception {
        ContainerId containerIdMock = Mockito.mock(ContainerId.class);
        Assert.assertNull(cGroupsCpuResourceHandler.reacquireContainer(containerIdMock));
    }

    @Test
    public void testPostComplete() throws Exception {
        String id = "container_01_01";
        ContainerId mockContainerId = Mockito.mock(ContainerId.class);
        Mockito.when(mockContainerId.toString()).thenReturn(id);
        Assert.assertNull(cGroupsCpuResourceHandler.postComplete(mockContainerId));
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).deleteCGroup(CPU, id);
    }

    @Test
    public void testTeardown() throws Exception {
        Assert.assertNull(cGroupsCpuResourceHandler.teardown());
    }

    @Test
    public void testStrictResourceUsage() throws Exception {
        Assert.assertNull(cGroupsCpuResourceHandler.teardown());
    }

    @Test
    public void testOpportunistic() throws Exception {
        Configuration conf = new YarnConfiguration();
        cGroupsCpuResourceHandler.bootstrap(plugin, conf);
        ContainerTokenIdentifier tokenId = Mockito.mock(ContainerTokenIdentifier.class);
        Mockito.when(tokenId.getExecutionType()).thenReturn(OPPORTUNISTIC);
        Container container = Mockito.mock(Container.class);
        String id = "container_01_01";
        ContainerId mockContainerId = Mockito.mock(ContainerId.class);
        Mockito.when(mockContainerId.toString()).thenReturn(id);
        Mockito.when(container.getContainerId()).thenReturn(mockContainerId);
        Mockito.when(container.getContainerTokenIdentifier()).thenReturn(tokenId);
        Mockito.when(container.getResource()).thenReturn(Resource.newInstance(1024, 2));
        cGroupsCpuResourceHandler.preStart(container);
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).updateCGroupParam(CPU, id, CGROUP_CPU_SHARES, "2");
    }
}

