/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources;


import CGroupsHandler.CGroupController.MEMORY;
import ContainerExecutor.Signal.KILL;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.server.nodemanager.ContainerExecutor;
import org.apache.hadoop.yarn.server.nodemanager.Context;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.apache.hadoop.yarn.server.nodemanager.executor.ContainerSignalContext;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Test default out of memory handler.
 */
public class TestDefaultOOMHandler {
    /**
     * Test an OOM situation where there are no containers that can be killed.
     */
    @Test(expected = YarnRuntimeException.class)
    public void testExceptionThrownWithNoContainersToKill() throws Exception {
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getContainers()).thenReturn(new ConcurrentHashMap(0));
        CGroupsHandler cGroupsHandler = Mockito.mock(CGroupsHandler.class);
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, "", CGroupsHandler.CGROUP_PARAM_MEMORY_OOM_CONTROL)).thenReturn("under_oom 1").thenReturn("under_oom 0");
        DefaultOOMHandler handler = new DefaultOOMHandler(context, false) {
            @Override
            protected CGroupsHandler getCGroupsHandler() {
                return cGroupsHandler;
            }
        };
        handler.run();
    }

    /**
     * Test an OOM situation where there are no running containers that
     * can be killed.
     */
    @Test(expected = YarnRuntimeException.class)
    public void testExceptionThrownWithNoRunningContainersToKill() throws Exception {
        ConcurrentHashMap<ContainerId, Container> containers = new ConcurrentHashMap<>();
        Container c1 = TestDefaultOOMHandler.createContainer(1, true, 1L, false);
        containers.put(c1.getContainerId(), c1);
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getContainers()).thenReturn(containers);
        CGroupsHandler cGroupsHandler = Mockito.mock(CGroupsHandler.class);
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, "", CGroupsHandler.CGROUP_PARAM_MEMORY_OOM_CONTROL)).thenReturn("under_oom 1").thenReturn("under_oom 0");
        DefaultOOMHandler handler = new DefaultOOMHandler(context, false) {
            @Override
            protected CGroupsHandler getCGroupsHandler() {
                return cGroupsHandler;
            }
        };
        handler.run();
    }

    /**
     * We have two running guaranteed containers, both of which are out of limit.
     * We should kill the later one.
     */
    @Test
    public void testBothRunningGuaranteedContainersOverLimitUponOOM() throws Exception {
        ConcurrentHashMap<ContainerId, Container> containers = new ConcurrentHashMap<>();
        Container c1 = TestDefaultOOMHandler.createContainer(1, true, 1L, true);
        containers.put(c1.getContainerId(), c1);
        Container c2 = TestDefaultOOMHandler.createContainer(2, true, 2L, true);
        containers.put(c2.getContainerId(), c2);
        ContainerExecutor ex = TestDefaultOOMHandler.createContainerExecutor(containers);
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getContainers()).thenReturn(containers);
        Mockito.when(context.getContainerExecutor()).thenReturn(ex);
        CGroupsHandler cGroupsHandler = Mockito.mock(CGroupsHandler.class);
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, "", CGroupsHandler.CGROUP_PARAM_MEMORY_OOM_CONTROL)).thenReturn("under_oom 1").thenReturn("under_oom 0");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1234").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(11));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(11));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1235").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(11));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(11));
        DefaultOOMHandler handler = new DefaultOOMHandler(context, false) {
            @Override
            protected CGroupsHandler getCGroupsHandler() {
                return cGroupsHandler;
            }
        };
        handler.run();
        Mockito.verify(ex, Mockito.times(1)).signalContainer(new ContainerSignalContext.Builder().setPid("1235").setContainer(c2).setSignal(KILL).build());
        Mockito.verify(ex, Mockito.times(1)).signalContainer(ArgumentMatchers.any());
    }

    /**
     * We have two running GUARANTEED containers, one of which is out of limit.
     * We should kill the one that's out of its limit. This should
     * happen even if it was launched earlier than the other one.
     */
    @Test
    public void testOneGuaranteedContainerOverLimitUponOOM() throws Exception {
        ConcurrentHashMap<ContainerId, Container> containers = new ConcurrentHashMap<>();
        Container c1 = TestDefaultOOMHandler.createContainer(1, true, 2L, true);
        containers.put(c1.getContainerId(), c1);
        Container c2 = TestDefaultOOMHandler.createContainer(2, true, 1L, true);
        containers.put(c2.getContainerId(), c2);
        ContainerExecutor ex = TestDefaultOOMHandler.createContainerExecutor(containers);
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getContainers()).thenReturn(containers);
        Mockito.when(context.getContainerExecutor()).thenReturn(ex);
        CGroupsHandler cGroupsHandler = Mockito.mock(CGroupsHandler.class);
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, "", CGroupsHandler.CGROUP_PARAM_MEMORY_OOM_CONTROL)).thenReturn("under_oom 1").thenReturn("under_oom 0");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1234").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(9));
        // container c2 is out of its limit
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1235").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(11));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(11));
        DefaultOOMHandler handler = new DefaultOOMHandler(context, false) {
            @Override
            protected CGroupsHandler getCGroupsHandler() {
                return cGroupsHandler;
            }
        };
        handler.run();
        Mockito.verify(ex, Mockito.times(1)).signalContainer(new ContainerSignalContext.Builder().setPid("1235").setContainer(c2).setSignal(KILL).build());
        Mockito.verify(ex, Mockito.times(1)).signalContainer(ArgumentMatchers.any());
    }

    /**
     * We have two running GUARANTEE containers, neither of which is out of limit.
     * We should kill the later launched one.
     */
    @Test
    public void testNoGuaranteedContainerOverLimitOOM() throws Exception {
        ConcurrentHashMap<ContainerId, Container> containers = new ConcurrentHashMap<>();
        Container c1 = TestDefaultOOMHandler.createContainer(1, true, 1L, true);
        containers.put(c1.getContainerId(), c1);
        Container c2 = TestDefaultOOMHandler.createContainer(2, true, 2L, true);
        containers.put(c2.getContainerId(), c2);
        ContainerExecutor ex = TestDefaultOOMHandler.createContainerExecutor(containers);
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getContainers()).thenReturn(containers);
        Mockito.when(context.getContainerExecutor()).thenReturn(ex);
        CGroupsHandler cGroupsHandler = Mockito.mock(CGroupsHandler.class);
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, "", CGroupsHandler.CGROUP_PARAM_MEMORY_OOM_CONTROL)).thenReturn("under_oom 1").thenReturn("under_oom 0");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1234").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1235").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(9));
        DefaultOOMHandler handler = new DefaultOOMHandler(context, false) {
            @Override
            protected CGroupsHandler getCGroupsHandler() {
                return cGroupsHandler;
            }
        };
        handler.run();
        Mockito.verify(ex, Mockito.times(1)).signalContainer(new ContainerSignalContext.Builder().setPid("1235").setContainer(c2).setSignal(KILL).build());
        Mockito.verify(ex, Mockito.times(1)).signalContainer(ArgumentMatchers.any());
    }

    /**
     * We have two OPPORTUNISTIC containers, one running and the other not.
     * We should kill the running one.
     */
    @Test
    public void testKillOnlyRunningContainersUponOOM() throws Exception {
        ConcurrentHashMap<ContainerId, Container> containers = new ConcurrentHashMap<>();
        Container c1 = TestDefaultOOMHandler.createContainer(1, false, 1L, false);
        containers.put(c1.getContainerId(), c1);
        Container c2 = TestDefaultOOMHandler.createContainer(2, false, 2L, true);
        containers.put(c2.getContainerId(), c2);
        ContainerExecutor ex = TestDefaultOOMHandler.createContainerExecutor(containers);
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getContainers()).thenReturn(containers);
        Mockito.when(context.getContainerExecutor()).thenReturn(ex);
        CGroupsHandler cGroupsHandler = Mockito.mock(CGroupsHandler.class);
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, "", CGroupsHandler.CGROUP_PARAM_MEMORY_OOM_CONTROL)).thenReturn("under_oom 1").thenReturn("under_oom 0");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1234").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(9));
        DefaultOOMHandler handler = new DefaultOOMHandler(context, false) {
            @Override
            protected CGroupsHandler getCGroupsHandler() {
                return cGroupsHandler;
            }
        };
        handler.run();
        Mockito.verify(ex, Mockito.times(1)).signalContainer(new ContainerSignalContext.Builder().setPid("1235").setContainer(c2).setSignal(KILL).build());
        Mockito.verify(ex, Mockito.times(1)).signalContainer(ArgumentMatchers.any());
    }

    /**
     * We have two 'running' OPPORTUNISTIC containers. Killing the most-
     * recently launched one fails because its cgroup.procs file is not
     * available. The other OPPORTUNISTIC containers should be killed in
     * this case.
     */
    @Test
    public void testKillOpportunisticContainerWithKillFailuresUponOOM() throws Exception {
        ConcurrentHashMap<ContainerId, Container> containers = new ConcurrentHashMap<>();
        Container c1 = TestDefaultOOMHandler.createContainer(1, false, 1L, true);
        containers.put(c1.getContainerId(), c1);
        Container c2 = TestDefaultOOMHandler.createContainer(2, false, 2L, true);
        containers.put(c2.getContainerId(), c2);
        ContainerExecutor ex = TestDefaultOOMHandler.createContainerExecutor(containers);
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getContainers()).thenReturn(containers);
        Mockito.when(context.getContainerExecutor()).thenReturn(ex);
        CGroupsHandler cGroupsHandler = Mockito.mock(CGroupsHandler.class);
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, "", CGroupsHandler.CGROUP_PARAM_MEMORY_OOM_CONTROL)).thenReturn("under_oom 1").thenReturn("under_oom 0");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1234").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(9));
        // c2 process has not started, hence no cgroup.procs file yet
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenThrow(new ResourceHandlerException(((CGroupsHandler.CGROUP_PROCS_FILE) + " not found")));
        DefaultOOMHandler handler = new DefaultOOMHandler(context, false) {
            @Override
            protected CGroupsHandler getCGroupsHandler() {
                return cGroupsHandler;
            }
        };
        handler.run();
        Mockito.verify(ex, Mockito.times(1)).signalContainer(new ContainerSignalContext.Builder().setPid("1235").setContainer(c1).setSignal(KILL).build());
        Mockito.verify(ex, Mockito.times(1)).signalContainer(ArgumentMatchers.any());
    }

    /**
     * We have two 'running' OPPORTUNISTIC containers and one GUARANTEED
     * container. Killing two OPPORTUNISTIC containers fails because they
     * have not really started running as processes since the root cgroup
     * is under oom. We should try to kill one container successfully. In
     * this case, the GUARANTEED container should be killed.
     */
    @Test
    public void testKillGuaranteedContainerWithKillFailuresUponOOM() throws Exception {
        ConcurrentHashMap<ContainerId, Container> containers = new ConcurrentHashMap<>();
        Container c1 = TestDefaultOOMHandler.createContainer(1, false, 1L, true);
        containers.put(c1.getContainerId(), c1);
        Container c2 = TestDefaultOOMHandler.createContainer(2, false, 2L, true);
        containers.put(c2.getContainerId(), c2);
        Container c3 = TestDefaultOOMHandler.createContainer(3, true, 2L, true);
        containers.put(c3.getContainerId(), c3);
        ContainerExecutor ex = TestDefaultOOMHandler.createContainerExecutor(containers);
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getContainers()).thenReturn(containers);
        Mockito.when(context.getContainerExecutor()).thenReturn(ex);
        CGroupsHandler cGroupsHandler = Mockito.mock(CGroupsHandler.class);
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, "", CGroupsHandler.CGROUP_PARAM_MEMORY_OOM_CONTROL)).thenReturn("under_oom 1").thenReturn("under_oom 0");
        // c1 process has not started, hence no cgroup.procs file yet
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenThrow(new ResourceHandlerException(((CGroupsHandler.CGROUP_PROCS_FILE) + " not found")));
        // c2 process has not started, hence no cgroup.procs file yet
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenThrow(new ResourceHandlerException(((CGroupsHandler.CGROUP_PROCS_FILE) + " not found")));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c3.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1234").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c3.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c3.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(9));
        DefaultOOMHandler handler = new DefaultOOMHandler(context, false) {
            @Override
            protected CGroupsHandler getCGroupsHandler() {
                return cGroupsHandler;
            }
        };
        handler.run();
        Mockito.verify(ex, Mockito.times(1)).signalContainer(new ContainerSignalContext.Builder().setPid("1235").setContainer(c3).setSignal(KILL).build());
        Mockito.verify(ex, Mockito.times(1)).signalContainer(ArgumentMatchers.any());
    }

    /**
     * Test an OOM situation where no containers are killed successfully.
     *
     * We have two 'running' containers, none of which are actually
     * running as processes. Their cgroup.procs file is not available,
     * so kill them won't succeed.
     */
    @Test(expected = YarnRuntimeException.class)
    public void testExceptionThrownWhenNoContainersKilledSuccessfully() throws Exception {
        ConcurrentHashMap<ContainerId, Container> containers = new ConcurrentHashMap<>();
        Container c1 = TestDefaultOOMHandler.createContainer(1, false, 1L, true);
        containers.put(c1.getContainerId(), c1);
        Container c2 = TestDefaultOOMHandler.createContainer(2, false, 2L, true);
        containers.put(c2.getContainerId(), c2);
        ContainerExecutor ex = TestDefaultOOMHandler.createContainerExecutor(containers);
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getContainers()).thenReturn(containers);
        Mockito.when(context.getContainerExecutor()).thenReturn(ex);
        CGroupsHandler cGroupsHandler = Mockito.mock(CGroupsHandler.class);
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, "", CGroupsHandler.CGROUP_PARAM_MEMORY_OOM_CONTROL)).thenReturn("under_oom 1").thenReturn("under_oom 0");
        // c1 process has not started, hence no cgroup.procs file yet
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenThrow(new ResourceHandlerException(((CGroupsHandler.CGROUP_PROCS_FILE) + " not found")));
        // c2 process has not started, hence no cgroup.procs file yet
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenThrow(new ResourceHandlerException(((CGroupsHandler.CGROUP_PROCS_FILE) + " not found")));
        DefaultOOMHandler handler = new DefaultOOMHandler(context, false) {
            @Override
            protected CGroupsHandler getCGroupsHandler() {
                return cGroupsHandler;
            }
        };
        handler.run();
    }

    /**
     * We have two running opportunistic containers, both of which are out of
     * limit. We should kill the later one.
     */
    @Test
    public void testBothOpportunisticContainersOverLimitUponOOM() throws Exception {
        ConcurrentHashMap<ContainerId, Container> containers = new ConcurrentHashMap<>();
        Container c1 = TestDefaultOOMHandler.createContainer(1, false, 1L, true);
        containers.put(c1.getContainerId(), c1);
        Container c2 = TestDefaultOOMHandler.createContainer(2, false, 2L, true);
        containers.put(c2.getContainerId(), c2);
        ContainerExecutor ex = TestDefaultOOMHandler.createContainerExecutor(containers);
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getContainers()).thenReturn(containers);
        Mockito.when(context.getContainerExecutor()).thenReturn(ex);
        CGroupsHandler cGroupsHandler = Mockito.mock(CGroupsHandler.class);
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, "", CGroupsHandler.CGROUP_PARAM_MEMORY_OOM_CONTROL)).thenReturn("under_oom 1").thenReturn("under_oom 0");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1234").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(11));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(11));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1235").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(11));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(11));
        DefaultOOMHandler handler = new DefaultOOMHandler(context, false) {
            @Override
            protected CGroupsHandler getCGroupsHandler() {
                return cGroupsHandler;
            }
        };
        handler.run();
        Mockito.verify(ex, Mockito.times(1)).signalContainer(new ContainerSignalContext.Builder().setPid("1235").setContainer(c2).setSignal(KILL).build());
        Mockito.verify(ex, Mockito.times(1)).signalContainer(ArgumentMatchers.any());
    }

    /**
     * We have two running OPPORTUNISTIC containers, one of which is out of
     * limit. We should kill the one that's out of its limit. This should
     * happen even if it was launched earlier than the other one.
     */
    @Test
    public void testOneOpportunisticContainerOverLimitUponOOM() throws Exception {
        ConcurrentHashMap<ContainerId, Container> containers = new ConcurrentHashMap<>();
        Container c1 = TestDefaultOOMHandler.createContainer(1, false, 2L, true);
        containers.put(c1.getContainerId(), c1);
        Container c2 = TestDefaultOOMHandler.createContainer(2, false, 1L, true);
        containers.put(c2.getContainerId(), c2);
        ContainerExecutor ex = TestDefaultOOMHandler.createContainerExecutor(containers);
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getContainers()).thenReturn(containers);
        Mockito.when(context.getContainerExecutor()).thenReturn(ex);
        CGroupsHandler cGroupsHandler = Mockito.mock(CGroupsHandler.class);
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, "", CGroupsHandler.CGROUP_PARAM_MEMORY_OOM_CONTROL)).thenReturn("under_oom 1").thenReturn("under_oom 0");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1234").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(9));
        // contnainer c2 is out of its limit
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1235").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(11));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(11));
        DefaultOOMHandler handler = new DefaultOOMHandler(context, false) {
            @Override
            protected CGroupsHandler getCGroupsHandler() {
                return cGroupsHandler;
            }
        };
        handler.run();
        Mockito.verify(ex, Mockito.times(1)).signalContainer(new ContainerSignalContext.Builder().setPid("1235").setContainer(c2).setSignal(KILL).build());
        Mockito.verify(ex, Mockito.times(1)).signalContainer(ArgumentMatchers.any());
    }

    /**
     * We have two running OPPORTUNISTIC containers, neither of which is out of
     * limit. We should kill the later one.
     */
    @Test
    public void testNoOpportunisticContainerOverLimitOOM() throws Exception {
        ConcurrentHashMap<ContainerId, Container> containers = new ConcurrentHashMap<>();
        Container c1 = TestDefaultOOMHandler.createContainer(1, false, 1L, true);
        containers.put(c1.getContainerId(), c1);
        Container c2 = TestDefaultOOMHandler.createContainer(2, false, 2L, true);
        containers.put(c2.getContainerId(), c2);
        ContainerExecutor ex = TestDefaultOOMHandler.createContainerExecutor(containers);
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getContainers()).thenReturn(containers);
        Mockito.when(context.getContainerExecutor()).thenReturn(ex);
        CGroupsHandler cGroupsHandler = Mockito.mock(CGroupsHandler.class);
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, "", CGroupsHandler.CGROUP_PARAM_MEMORY_OOM_CONTROL)).thenReturn("under_oom 1").thenReturn("under_oom 0");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1234").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1235").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(9));
        DefaultOOMHandler handler = new DefaultOOMHandler(context, false) {
            @Override
            protected CGroupsHandler getCGroupsHandler() {
                return cGroupsHandler;
            }
        };
        handler.run();
        Mockito.verify(ex, Mockito.times(1)).signalContainer(new ContainerSignalContext.Builder().setPid("1235").setContainer(c2).setSignal(KILL).build());
        Mockito.verify(ex, Mockito.times(1)).signalContainer(ArgumentMatchers.any());
    }

    /**
     * We have two running OPPORTUNISTIC containers and one running GUARANTEED
     * container. One of the OPPORTUNISTIC container is out of limit.
     * OOM is resolved after killing the OPPORTUNISTIC container that
     * exceeded its limit even though it is launched earlier than the
     * other OPPORTUNISTIC container.
     */
    @Test
    public void testKillOneOverLimitOpportunisticContainerUponOOM() throws Exception {
        ConcurrentHashMap<ContainerId, Container> containers = new ConcurrentHashMap<>();
        int currentContainerId = 0;
        Container c1 = TestDefaultOOMHandler.createContainer((currentContainerId++), false, 2, true);
        containers.put(c1.getContainerId(), c1);
        Container c2 = TestDefaultOOMHandler.createContainer((currentContainerId++), false, 1, true);
        containers.put(c2.getContainerId(), c2);
        Container c3 = TestDefaultOOMHandler.createContainer((currentContainerId++), true, 1, true);
        containers.put(c3.getContainerId(), c3);
        ContainerExecutor ex = TestDefaultOOMHandler.createContainerExecutor(containers);
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getContainers()).thenReturn(containers);
        Mockito.when(context.getContainerExecutor()).thenReturn(ex);
        CGroupsHandler cGroupsHandler = Mockito.mock(CGroupsHandler.class);
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, "", CGroupsHandler.CGROUP_PARAM_MEMORY_OOM_CONTROL)).thenReturn("under_oom 1").thenReturn("under_oom 0");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1234").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(9));
        // container c2 is out of its limit
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1235").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(11));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(11));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c3.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1236").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c3.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c3.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(9));
        DefaultOOMHandler handler = new DefaultOOMHandler(context, false) {
            @Override
            protected CGroupsHandler getCGroupsHandler() {
                return cGroupsHandler;
            }
        };
        handler.run();
        Mockito.verify(ex, Mockito.times(1)).signalContainer(new ContainerSignalContext.Builder().setPid("1235").setContainer(c2).setSignal(KILL).build());
        Mockito.verify(ex, Mockito.times(1)).signalContainer(ArgumentMatchers.any());
    }

    /**
     * We have two running OPPORTUNISTIC containers and one running GUARANTEED
     * container. None of the containers exceeded its memory limit.
     * OOM is resolved after killing the most recently launched OPPORTUNISTIC
     * container.
     */
    @Test
    public void testKillOneLaterOpportunisticContainerUponOOM() throws Exception {
        ConcurrentHashMap<ContainerId, Container> containers = new ConcurrentHashMap<>();
        int currentContainerId = 0;
        Container c1 = TestDefaultOOMHandler.createContainer((currentContainerId++), false, 1, true);
        containers.put(c1.getContainerId(), c1);
        Container c2 = TestDefaultOOMHandler.createContainer((currentContainerId++), false, 2, true);
        containers.put(c2.getContainerId(), c2);
        Container c3 = TestDefaultOOMHandler.createContainer((currentContainerId++), true, 1, true);
        containers.put(c3.getContainerId(), c3);
        ContainerExecutor ex = TestDefaultOOMHandler.createContainerExecutor(containers);
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getContainers()).thenReturn(containers);
        Mockito.when(context.getContainerExecutor()).thenReturn(ex);
        CGroupsHandler cGroupsHandler = Mockito.mock(CGroupsHandler.class);
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, "", CGroupsHandler.CGROUP_PARAM_MEMORY_OOM_CONTROL)).thenReturn("under_oom 1").thenReturn("under_oom 0");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1234").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1235").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c3.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1236").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c3.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c3.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(9));
        DefaultOOMHandler handler = new DefaultOOMHandler(context, false) {
            @Override
            protected CGroupsHandler getCGroupsHandler() {
                return cGroupsHandler;
            }
        };
        handler.run();
        Mockito.verify(ex, Mockito.times(1)).signalContainer(new ContainerSignalContext.Builder().setPid("1235").setContainer(c2).setSignal(KILL).build());
        Mockito.verify(ex, Mockito.times(1)).signalContainer(ArgumentMatchers.any());
    }

    /**
     * We have two running OPPORTUNISTIC containers and one running GUARANTEED
     * container. One of the OPPORTUNISTIC container is out of limit.
     * OOM is resolved after killing both OPPORTUNISTIC containers.
     */
    @Test
    public void testKillBothOpportunisticContainerUponOOM() throws Exception {
        int currentContainerId = 0;
        ConcurrentHashMap<ContainerId, Container> containers = new ConcurrentHashMap<>();
        Container c1 = TestDefaultOOMHandler.createContainer((currentContainerId++), false, 2, true);
        containers.put(c1.getContainerId(), c1);
        Container c2 = TestDefaultOOMHandler.createContainer((currentContainerId++), false, 1, true);
        containers.put(c2.getContainerId(), c2);
        Container c3 = TestDefaultOOMHandler.createContainer((currentContainerId++), true, 1, true);
        containers.put(c3.getContainerId(), c3);
        ContainerExecutor ex = TestDefaultOOMHandler.createContainerExecutor(containers);
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getContainers()).thenReturn(containers);
        Mockito.when(context.getContainerExecutor()).thenReturn(ex);
        CGroupsHandler cGroupsHandler = Mockito.mock(CGroupsHandler.class);
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, "", CGroupsHandler.CGROUP_PARAM_MEMORY_OOM_CONTROL)).thenReturn("under_oom 1").thenReturn("under_oom 1").thenReturn("under_oom 0");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1234").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1235").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(11));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(11));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c3.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1236").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c3.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c3.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(9));
        DefaultOOMHandler handler = new DefaultOOMHandler(context, false) {
            @Override
            protected CGroupsHandler getCGroupsHandler() {
                return cGroupsHandler;
            }
        };
        handler.run();
        Mockito.verify(ex, Mockito.times(1)).signalContainer(new ContainerSignalContext.Builder().setPid("1235").setContainer(c1).setSignal(KILL).build());
        Mockito.verify(ex, Mockito.times(1)).signalContainer(new ContainerSignalContext.Builder().setPid("1234").setContainer(c2).setSignal(KILL).build());
        Mockito.verify(ex, Mockito.times(2)).signalContainer(ArgumentMatchers.any());
    }

    /**
     * We have two running OPPORTUNISTIC containers and one running GUARANTEED
     * container. The GUARANTEED container is out of limit. OOM is resolved
     * after first killing the two OPPORTUNISTIC containers and then the
     * GUARANTEED container.
     */
    @Test
    public void testKillGuaranteedContainerUponOOM() throws Exception {
        int currentContainerId = 0;
        ConcurrentHashMap<ContainerId, Container> containers = new ConcurrentHashMap<>();
        Container c1 = TestDefaultOOMHandler.createContainer((currentContainerId++), false, 2, true);
        containers.put(c1.getContainerId(), c1);
        Container c2 = TestDefaultOOMHandler.createContainer((currentContainerId++), false, 1, true);
        containers.put(c2.getContainerId(), c2);
        Container c3 = TestDefaultOOMHandler.createContainer((currentContainerId++), true, 1, true);
        containers.put(c3.getContainerId(), c3);
        ContainerExecutor ex = TestDefaultOOMHandler.createContainerExecutor(containers);
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getContainers()).thenReturn(containers);
        Mockito.when(context.getContainerExecutor()).thenReturn(ex);
        CGroupsHandler cGroupsHandler = Mockito.mock(CGroupsHandler.class);
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, "", CGroupsHandler.CGROUP_PARAM_MEMORY_OOM_CONTROL)).thenReturn("under_oom 1").thenReturn("under_oom 1").thenReturn("under_oom 1").thenReturn("under_oom 0");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1234").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1235").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c3.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1236").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c3.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(11));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c3.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(11));
        DefaultOOMHandler handler = new DefaultOOMHandler(context, false) {
            @Override
            protected CGroupsHandler getCGroupsHandler() {
                return cGroupsHandler;
            }
        };
        handler.run();
        Mockito.verify(ex, Mockito.times(1)).signalContainer(new ContainerSignalContext.Builder().setPid("1234").setContainer(c1).setSignal(KILL).build());
        Mockito.verify(ex, Mockito.times(1)).signalContainer(new ContainerSignalContext.Builder().setPid("1235").setContainer(c1).setSignal(KILL).build());
        Mockito.verify(ex, Mockito.times(1)).signalContainer(new ContainerSignalContext.Builder().setPid("1236").setContainer(c1).setSignal(KILL).build());
        Mockito.verify(ex, Mockito.times(3)).signalContainer(ArgumentMatchers.any());
    }

    /**
     * We have two running OPPORTUNISTIC containers and one running GUARANTEED
     * container. None of the containers exceeded its memory limit.
     * OOM is resolved after killing all running containers.
     */
    @Test
    public void testKillAllContainersUponOOM() throws Exception {
        int currentContainerId = 0;
        ConcurrentHashMap<ContainerId, Container> containers = new ConcurrentHashMap<>();
        Container c1 = TestDefaultOOMHandler.createContainer((currentContainerId++), false, 1, true);
        containers.put(c1.getContainerId(), c1);
        Container c2 = TestDefaultOOMHandler.createContainer((currentContainerId++), false, 2, true);
        containers.put(c2.getContainerId(), c2);
        Container c3 = TestDefaultOOMHandler.createContainer((currentContainerId++), true, 1, true);
        containers.put(c3.getContainerId(), c3);
        ContainerExecutor ex = TestDefaultOOMHandler.createContainerExecutor(containers);
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getContainers()).thenReturn(containers);
        Mockito.when(context.getContainerExecutor()).thenReturn(ex);
        CGroupsHandler cGroupsHandler = Mockito.mock(CGroupsHandler.class);
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, "", CGroupsHandler.CGROUP_PARAM_MEMORY_OOM_CONTROL)).thenReturn("under_oom 1").thenReturn("under_oom 1").thenReturn("under_oom 1").thenReturn("under_oom 0");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1234").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1235").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c3.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1236").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c3.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c3.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(9));
        DefaultOOMHandler handler = new DefaultOOMHandler(context, false) {
            @Override
            protected CGroupsHandler getCGroupsHandler() {
                return cGroupsHandler;
            }
        };
        handler.run();
        Mockito.verify(ex, Mockito.times(1)).signalContainer(new ContainerSignalContext.Builder().setPid("1234").setContainer(c2).setSignal(KILL).build());
        Mockito.verify(ex, Mockito.times(1)).signalContainer(new ContainerSignalContext.Builder().setPid("1235").setContainer(c1).setSignal(KILL).build());
        Mockito.verify(ex, Mockito.times(1)).signalContainer(new ContainerSignalContext.Builder().setPid("1236").setContainer(c3).setSignal(KILL).build());
        Mockito.verify(ex, Mockito.times(3)).signalContainer(ArgumentMatchers.any());
    }

    /**
     * We have two running OPPORTUNISTIC containers and one running
     * GUARANTEED container.
     * None of the containers exceeded its memory limit.
     * OOM is not resolved even after killing all running containers.
     * A YarnRuntimeException is excepted to be thrown.
     */
    @Test(expected = YarnRuntimeException.class)
    public void testOOMUnresolvedAfterKillingAllContainers() throws Exception {
        int currentContainerId = 0;
        ConcurrentHashMap<ContainerId, Container> containers = new ConcurrentHashMap<>();
        Container c1 = TestDefaultOOMHandler.createContainer((currentContainerId++), false, 1, true);
        containers.put(c1.getContainerId(), c1);
        Container c2 = TestDefaultOOMHandler.createContainer((currentContainerId++), false, 2, true);
        containers.put(c2.getContainerId(), c2);
        Container c3 = TestDefaultOOMHandler.createContainer((currentContainerId++), true, 3, true);
        containers.put(c3.getContainerId(), c3);
        ContainerExecutor ex = TestDefaultOOMHandler.createContainerExecutor(containers);
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getContainers()).thenReturn(containers);
        Mockito.when(context.getContainerExecutor()).thenReturn(ex);
        CGroupsHandler cGroupsHandler = Mockito.mock(CGroupsHandler.class);
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, "", CGroupsHandler.CGROUP_PARAM_MEMORY_OOM_CONTROL)).thenReturn("under_oom 1").thenReturn("under_oom 1").thenReturn("under_oom 1").thenReturn("under_oom 1");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1234").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c1.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1235").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c2.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c3.getContainerId().toString(), CGroupsHandler.CGROUP_PROCS_FILE)).thenReturn("1236").thenReturn("");
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c3.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_USAGE_BYTES)).thenReturn(getMB(9));
        Mockito.when(cGroupsHandler.getCGroupParam(MEMORY, c3.getContainerId().toString(), CGroupsHandler.CGROUP_PARAM_MEMORY_MEMSW_USAGE_BYTES)).thenReturn(getMB(9));
        DefaultOOMHandler handler = new DefaultOOMHandler(context, false) {
            @Override
            protected CGroupsHandler getCGroupsHandler() {
                return cGroupsHandler;
            }
        };
        handler.run();
    }
}

