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
package org.apache.ambari.server.controller;


import AgentCommandType.BACKGROUND_EXECUTION_COMMAND;
import com.google.inject.Injector;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.apache.ambari.server.actionmanager.ActionManager;
import org.apache.ambari.server.actionmanager.ExecutionCommandWrapper;
import org.apache.ambari.server.actionmanager.Request;
import org.apache.ambari.server.actionmanager.Stage;
import org.apache.ambari.server.agent.ExecutionCommand;
import org.apache.ambari.server.controller.internal.RequestResourceFilter;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.StackId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class BackgroundCustomCommandExecutionTest {
    private Injector injector;

    private AmbariManagementController controller;

    private Clusters clusters;

    private static final String REQUEST_CONTEXT_PROPERTY = "context";

    @Captor
    ArgumentCaptor<Request> requestCapture;

    @Mock
    ActionManager am;

    private final String STACK_VERSION = "2.0.6";

    private final String REPO_VERSION = "2.0.6-1234";

    private final StackId STACK_ID = new StackId("HDP", STACK_VERSION);

    private RepositoryVersionEntity m_repositoryVersion;

    @SuppressWarnings("serial")
    @Test
    public void testRebalanceHdfsCustomCommand() {
        try {
            createClusterFixture();
            Map<String, String> requestProperties = new HashMap<String, String>() {
                {
                    put(BackgroundCustomCommandExecutionTest.REQUEST_CONTEXT_PROPERTY, "Refresh YARN Capacity Scheduler");
                    put("command", "REBALANCEHDFS");
                    put("namenode", "{\"threshold\":13}");// case is important here

                }
            };
            ExecuteActionRequest actionRequest = new ExecuteActionRequest("c1", "REBALANCEHDFS", new HashMap(), false);
            actionRequest.getResourceFilters().add(new RequestResourceFilter("HDFS", "NAMENODE", Collections.singletonList("c6401")));
            controller.createAction(actionRequest, requestProperties);
            Mockito.verify(am, Mockito.times(1)).sendActions(requestCapture.capture(), ArgumentMatchers.any(ExecuteActionRequest.class));
            Request request = requestCapture.getValue();
            Assert.assertNotNull(request);
            Assert.assertNotNull(request.getStages());
            Assert.assertEquals(1, request.getStages().size());
            Stage stage = request.getStages().iterator().next();
            System.out.println(stage);
            Assert.assertEquals(1, stage.getHosts().size());
            List<ExecutionCommandWrapper> commands = stage.getExecutionCommands("c6401");
            Assert.assertEquals(1, commands.size());
            ExecutionCommand command = commands.get(0).getExecutionCommand();
            Assert.assertEquals(BACKGROUND_EXECUTION_COMMAND, command.getCommandType());
            Assert.assertEquals("{\"threshold\":13}", command.getCommandParams().get("namenode"));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}

