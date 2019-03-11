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


import CGroupsBlkioResourceHandlerImpl.DEFAULT_WEIGHT;
import CGroupsHandler.CGROUP_PARAM_BLKIO_WEIGHT;
import CGroupsHandler.CGroupController.BLKIO;
import PrivilegedOperation.OperationType.ADD_PID_TO_CGROUP;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperation;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for the cgroups disk handler implementation.
 */
public class TestCGroupsBlkioResourceHandlerImpl {
    private CGroupsHandler mockCGroupsHandler;

    private CGroupsBlkioResourceHandlerImpl cGroupsBlkioResourceHandlerImpl;

    @Test
    public void testBootstrap() throws Exception {
        Configuration conf = new YarnConfiguration();
        List<PrivilegedOperation> ret = cGroupsBlkioResourceHandlerImpl.bootstrap(conf);
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).initializeCGroupController(BLKIO);
        Assert.assertNull(ret);
    }

    @Test
    public void testPreStart() throws Exception {
        String id = "container_01_01";
        String path = "test-path/" + id;
        ContainerId mockContainerId = Mockito.mock(ContainerId.class);
        Mockito.when(mockContainerId.toString()).thenReturn(id);
        Container mockContainer = Mockito.mock(Container.class);
        Mockito.when(mockContainer.getContainerId()).thenReturn(mockContainerId);
        Mockito.when(mockCGroupsHandler.getPathForCGroupTasks(BLKIO, id)).thenReturn(path);
        List<PrivilegedOperation> ret = cGroupsBlkioResourceHandlerImpl.preStart(mockContainer);
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).createCGroup(BLKIO, id);
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).updateCGroupParam(BLKIO, id, CGROUP_PARAM_BLKIO_WEIGHT, DEFAULT_WEIGHT);
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
        Assert.assertNull(cGroupsBlkioResourceHandlerImpl.reacquireContainer(containerIdMock));
    }

    @Test
    public void testPostComplete() throws Exception {
        String id = "container_01_01";
        ContainerId mockContainerId = Mockito.mock(ContainerId.class);
        Mockito.when(mockContainerId.toString()).thenReturn(id);
        Assert.assertNull(cGroupsBlkioResourceHandlerImpl.postComplete(mockContainerId));
        Mockito.verify(mockCGroupsHandler, Mockito.times(1)).deleteCGroup(BLKIO, id);
    }

    @Test
    public void testTeardown() throws Exception {
        Assert.assertNull(cGroupsBlkioResourceHandlerImpl.teardown());
    }
}

