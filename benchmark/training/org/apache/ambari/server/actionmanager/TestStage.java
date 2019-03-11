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


import ExecutionCommand.KeyNames.COMMAND_TIMEOUT;
import Role.DATANODE;
import Role.HBASE_MASTER;
import RoleCommand.INSTALL;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.util.Map;
import java.util.TreeMap;
import org.apache.ambari.server.utils.StageUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestStage {
    private static final String CLUSTER_HOST_INFO = "cluster_host_info";

    Injector injector;

    @Inject
    StageFactory stageFactory;

    @Inject
    StageUtils stageUtils;

    @Test
    public void testTaskTimeout() {
        Stage s = StageUtils.getATestStage(1, 1, "h1", "{\"host_param\":\"param_value\"}", "{\"stage_param\":\"param_value\"}");
        s.addHostRoleExecutionCommand("h1", DATANODE, INSTALL, null, "c1", "HDFS", false, false);
        s.addHostRoleExecutionCommand("h1", HBASE_MASTER, INSTALL, null, "c1", "HBASE", false, false);
        for (ExecutionCommandWrapper wrapper : s.getExecutionCommands("h1")) {
            Map<String, String> commandParams = new TreeMap<>();
            commandParams.put(COMMAND_TIMEOUT, "600");
            wrapper.getExecutionCommand().setCommandParams(commandParams);
        }
        Assert.assertEquals((3 * 600000), s.getStageTimeout());
    }

    @Test
    public void testGetRequestContext() {
        Stage stage = stageFactory.createNew(1, "/logDir", "c1", 1L, "My Context", "", "");
        Assert.assertEquals("My Context", stage.getRequestContext());
    }
}

