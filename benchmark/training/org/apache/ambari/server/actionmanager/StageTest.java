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
package org.apache.ambari.server.actionmanager;


import Role.AMBARI_SERVER_ACTION;
import RoleCommand.EXECUTE;
import ServerAction.ACTION_USER_NAME;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.util.Collections;
import java.util.List;
import org.apache.ambari.server.serveraction.upgrades.ConfigureAction;
import org.apache.ambari.server.utils.StageUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Stage tests.
 */
public class StageTest {
    private static final String SERVER_HOST_NAME = StageUtils.getHostName();

    private static final String CLUSTER_HOST_INFO = ((("{all_hosts=[" + (StageTest.SERVER_HOST_NAME)) + "], slave_hosts=[") + (StageTest.SERVER_HOST_NAME)) + "]}";

    Injector injector;

    @Inject
    StageFactory stageFactory;

    @Test
    public void testAddServerActionCommand_userName() throws Exception {
        final Stage stage = stageFactory.createNew(1, "/tmp", "cluster1", 978, "context", "{\"host_param\":\"param_value\"}", "{\"stage_param\":\"param_value\"}");
        stage.addServerActionCommand(ConfigureAction.class.getName(), "user1", AMBARI_SERVER_ACTION, EXECUTE, "cluster1", new org.apache.ambari.server.state.svccomphost.ServiceComponentHostServerActionEvent(StageUtils.getHostName(), System.currentTimeMillis()), Collections.emptyMap(), null, null, 1200, false, false);
        List<ExecutionCommandWrapper> executionCommands = stage.getExecutionCommands(null);
        Assert.assertEquals(1, executionCommands.size());
        String actionUserName = executionCommands.get(0).getExecutionCommand().getRoleParams().get(ACTION_USER_NAME);
        Assert.assertEquals("user1", actionUserName);
    }
}

