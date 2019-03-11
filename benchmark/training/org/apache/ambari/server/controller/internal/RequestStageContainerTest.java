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
package org.apache.ambari.server.controller.internal;


import RoleCommand.INSTALL;
import RoleCommand.SERVICE_CHECK;
import State.INSTALLED;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.Role;
import org.apache.ambari.server.RoleCommand;
import org.apache.ambari.server.actionmanager.ActionManager;
import org.apache.ambari.server.actionmanager.HostRoleCommand;
import org.apache.ambari.server.actionmanager.HostRoleStatus;
import org.apache.ambari.server.actionmanager.Request;
import org.apache.ambari.server.actionmanager.RequestFactory;
import org.apache.ambari.server.actionmanager.Stage;
import org.apache.ambari.server.controller.RequestStatusResponse;
import org.apache.ambari.server.controller.ShortTaskStatus;
import org.junit.Assert;
import org.junit.Test;


/**
 * RequestStageContainer unit tests.
 */
public class RequestStageContainerTest {
    @Test
    public void testGetId() {
        RequestStageContainer requestStages = new RequestStageContainer(500L, null, null, null);
        Assert.assertEquals(500, requestStages.getId().longValue());
    }

    @Test
    public void testGetAddStages() {
        RequestStageContainer requestStages = new RequestStageContainer(500L, null, null, null);
        Assert.assertTrue(requestStages.getStages().isEmpty());
        Stage stage = createNiceMock(Stage.class);
        requestStages.addStages(Collections.singletonList(stage));
        Assert.assertEquals(1, requestStages.getStages().size());
        Assert.assertTrue(requestStages.getStages().contains(stage));
        Stage stage2 = createNiceMock(Stage.class);
        Stage stage3 = createNiceMock(Stage.class);
        List<Stage> listStages = new ArrayList<>();
        listStages.add(stage2);
        listStages.add(stage3);
        requestStages.addStages(listStages);
        Assert.assertEquals(3, requestStages.getStages().size());
        listStages = requestStages.getStages();
        Assert.assertEquals(stage, listStages.get(0));
        Assert.assertEquals(stage2, listStages.get(1));
        Assert.assertEquals(stage3, listStages.get(2));
    }

    @Test
    public void testGetLastStageId() {
        RequestStageContainer requestStages = new RequestStageContainer(1L, null, null, null);
        Assert.assertEquals((-1), requestStages.getLastStageId());
        Stage stage1 = createNiceMock(Stage.class);
        Stage stage2 = createNiceMock(Stage.class);
        List<Stage> listStages = new ArrayList<>();
        listStages.add(stage1);
        listStages.add(stage2);
        expect(stage2.getStageId()).andReturn(22L);
        replay(stage1, stage2);
        requestStages = new RequestStageContainer(1L, listStages, null, null);
        Assert.assertEquals(22, requestStages.getLastStageId());
    }

    @Test
    public void testGetProjectedState() {
        String hostname = "host";
        String componentName = "component";
        Stage stage1 = createNiceMock(Stage.class);
        Stage stage2 = createNiceMock(Stage.class);
        Stage stage3 = createNiceMock(Stage.class);
        Stage stage4 = createNiceMock(Stage.class);
        HostRoleCommand command1 = createNiceMock(HostRoleCommand.class);
        HostRoleCommand command2 = createNiceMock(HostRoleCommand.class);
        HostRoleCommand command3 = createNiceMock(HostRoleCommand.class);
        List<Stage> stages = new ArrayList<>();
        stages.add(stage1);
        stages.add(stage2);
        stages.add(stage3);
        stages.add(stage4);
        // expectations
        expect(stage1.getHostRoleCommands()).andReturn(Collections.singletonMap(hostname, Collections.singletonMap(componentName, command1))).anyTimes();
        expect(stage2.getHostRoleCommands()).andReturn(Collections.singletonMap(hostname, Collections.singletonMap(componentName, command2))).anyTimes();
        expect(stage3.getHostRoleCommands()).andReturn(Collections.singletonMap(hostname, Collections.singletonMap(componentName, command3))).anyTimes();
        expect(stage4.getHostRoleCommands()).andReturn(Collections.emptyMap()).anyTimes();
        expect(command3.getRoleCommand()).andReturn(SERVICE_CHECK).anyTimes();
        expect(command2.getRoleCommand()).andReturn(INSTALL).anyTimes();
        replay(stage1, stage2, stage3, stage4, command1, command2, command3);
        RequestStageContainer requestStages = new RequestStageContainer(1L, stages, null, null);
        Assert.assertEquals(INSTALLED, requestStages.getProjectedState(hostname, componentName));
        verify(stage1, stage2, stage3, stage4, command1, command2, command3);
    }

    @Test
    public void testPersist() throws AmbariException {
        ActionManager actionManager = createStrictMock(ActionManager.class);
        RequestFactory requestFactory = createStrictMock(RequestFactory.class);
        Request request = createStrictMock(Request.class);
        Stage stage1 = createNiceMock(Stage.class);
        Stage stage2 = createNiceMock(Stage.class);
        List<Stage> stages = new ArrayList<>();
        stages.add(stage1);
        stages.add(stage2);
        // expectations
        expect(requestFactory.createNewFromStages(stages, "{}")).andReturn(request);
        request.setUserName(null);
        expectLastCall().once();
        expect(request.getStages()).andReturn(stages).anyTimes();
        actionManager.sendActions(request, null);
        replay(actionManager, requestFactory, request, stage1, stage2);
        RequestStageContainer requestStages = new RequestStageContainer(1L, stages, requestFactory, actionManager);
        requestStages.persist();
        verify(actionManager, requestFactory, request, stage1, stage2);
    }

    @Test
    public void testPersist_noStages() throws AmbariException {
        ActionManager actionManager = createStrictMock(ActionManager.class);
        RequestFactory requestFactory = createStrictMock(RequestFactory.class);
        // no expectations due to empty stage list
        replay(actionManager, requestFactory);
        RequestStageContainer requestStages = new RequestStageContainer(1L, null, requestFactory, actionManager);
        requestStages.persist();
        verify(actionManager, requestFactory);
    }

    @Test
    public void testGetRequestStatusResponse() {
        ActionManager actionManager = createStrictMock(ActionManager.class);
        Stage stage1 = createNiceMock(Stage.class);
        Stage stage2 = createNiceMock(Stage.class);
        HostRoleCommand command1 = createNiceMock(HostRoleCommand.class);
        Role role = createNiceMock(Role.class);
        List<Stage> stages = new ArrayList<>();
        RoleCommand roleCommand = RoleCommand.INSTALL;
        HostRoleStatus status = HostRoleStatus.IN_PROGRESS;
        stages.add(stage1);
        stages.add(stage2);
        List<HostRoleCommand> hostRoleCommands = new ArrayList<>();
        hostRoleCommands.add(command1);
        expect(actionManager.getRequestTasks(100)).andReturn(hostRoleCommands);
        expect(actionManager.getRequestContext(100)).andReturn("test");
        expect(command1.getTaskId()).andReturn(1L);
        expect(command1.getRoleCommand()).andReturn(roleCommand);
        expect(command1.getRole()).andReturn(role);
        expect(command1.getStatus()).andReturn(status);
        replay(actionManager, stage1, stage2, command1, role);
        RequestStageContainer requestStages = new RequestStageContainer(100L, stages, null, actionManager);
        RequestStatusResponse response = requestStages.getRequestStatusResponse();
        Assert.assertEquals(100, response.getRequestId());
        List<ShortTaskStatus> tasks = response.getTasks();
        Assert.assertEquals(1, tasks.size());
        ShortTaskStatus task = tasks.get(0);
        Assert.assertEquals(1, task.getTaskId());
        Assert.assertEquals(roleCommand.toString(), task.getCommand());
        Assert.assertEquals(status.toString(), task.getStatus());
        Assert.assertEquals("test", response.getRequestContext());
        verify(actionManager, stage1, stage2, command1, role);
    }
}

