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
package org.apache.ambari.server.serveraction.kerberos;


import CaptureType.ALL;
import KerberosServerAction.DATA_DIRECTORY;
import com.google.inject.Injector;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.ambari.server.agent.ExecutionCommand;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.ConfigHelper;
import org.apache.ambari.server.state.StackId;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class UpdateKerberosConfigsServerActionTest extends EasyMockSupport {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    String dataDir;

    private Injector injector;

    private UpdateKerberosConfigsServerAction action;

    @Test
    public void testUpdateConfig() throws Exception {
        Map<String, String> commandParams = new HashMap<>();
        commandParams.put(DATA_DIRECTORY, dataDir);
        ExecutionCommand executionCommand = new ExecutionCommand();
        executionCommand.setCommandParams(commandParams);
        ConfigHelper configHelper = injector.getInstance(ConfigHelper.class);
        configHelper.updateConfigType(anyObject(Cluster.class), anyObject(StackId.class), anyObject(AmbariManagementController.class), anyObject(String.class), EasyMock.anyObject(), EasyMock.anyObject(), anyObject(String.class), anyObject(String.class));
        expectLastCall().atLeastOnce();
        replayAll();
        action.setExecutionCommand(executionCommand);
        action.execute(null);
        verifyAll();
    }

    @Test
    public void testUpdateConfigMissingDataDirectory() throws Exception {
        ExecutionCommand executionCommand = new ExecutionCommand();
        Map<String, String> commandParams = new HashMap<>();
        executionCommand.setCommandParams(commandParams);
        replayAll();
        action.setExecutionCommand(executionCommand);
        action.execute(null);
        verifyAll();
    }

    @Test
    public void testUpdateConfigEmptyDataDirectory() throws Exception {
        ExecutionCommand executionCommand = new ExecutionCommand();
        Map<String, String> commandParams = new HashMap<>();
        commandParams.put(DATA_DIRECTORY, testFolder.newFolder().getAbsolutePath());
        executionCommand.setCommandParams(commandParams);
        replayAll();
        action.setExecutionCommand(executionCommand);
        action.execute(null);
        verifyAll();
    }

    @Test
    public void testUpdateConfigForceSecurityEnabled() throws Exception {
        Map<String, String> commandParams = new HashMap<>();
        commandParams.put(DATA_DIRECTORY, dataDir);
        ExecutionCommand executionCommand = new ExecutionCommand();
        executionCommand.setCommandParams(commandParams);
        ConfigHelper configHelper = injector.getInstance(ConfigHelper.class);
        Capture<String> configTypes = Capture.newInstance(ALL);
        Capture<Map<String, String>> configUpdates = Capture.newInstance(ALL);
        configHelper.updateConfigType(anyObject(Cluster.class), anyObject(StackId.class), anyObject(AmbariManagementController.class), capture(configTypes), capture(configUpdates), anyObject(Collection.class), anyObject(String.class), anyObject(String.class));
        expectLastCall().atLeastOnce();
        replayAll();
        action.setExecutionCommand(executionCommand);
        action.execute(null);
        Assert.assertTrue(configTypes.getValues().contains("cluster-env"));
        boolean containsSecurityEnabled = false;
        for (Map<String, String> properties : configUpdates.getValues()) {
            if (properties.containsKey("security_enabled")) {
                containsSecurityEnabled = true;
                break;
            }
        }
        Assert.assertTrue(containsSecurityEnabled);
        verifyAll();
    }
}

