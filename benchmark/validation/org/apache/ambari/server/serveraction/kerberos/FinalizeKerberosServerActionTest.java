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


import KerberosServerAction.DATA_DIRECTORY;
import com.google.inject.Injector;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import junit.framework.Assert;
import org.apache.ambari.server.actionmanager.HostRoleCommand;
import org.apache.ambari.server.agent.CommandReport;
import org.apache.ambari.server.agent.ExecutionCommand;
import org.apache.ambari.server.agent.stomp.TopologyHolder;
import org.easymock.EasyMockSupport;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class FinalizeKerberosServerActionTest extends EasyMockSupport {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private final TopologyHolder topologyHolder = createNiceMock(TopologyHolder.class);

    @Test
    public void executeManualOption() throws Exception {
        String clusterName = "c1";
        String clusterId = "1";
        Injector injector = setup(clusterName);
        File dataDirectory = createDataDirectory();
        Map<String, String> commandParams = new HashMap<>();
        commandParams.put(DATA_DIRECTORY, dataDirectory.getAbsolutePath());
        ExecutionCommand executionCommand = createMockExecutionCommand(clusterId, clusterName, commandParams);
        HostRoleCommand hostRoleCommand = createMockHostRoleCommand();
        replayAll();
        ConcurrentMap<String, Object> requestSharedDataContext = new ConcurrentHashMap<>();
        FinalizeKerberosServerAction action = new FinalizeKerberosServerAction(topologyHolder);
        action.setExecutionCommand(executionCommand);
        action.setHostRoleCommand(hostRoleCommand);
        Assert.assertTrue(dataDirectory.exists());
        CommandReport commandReport = action.execute(requestSharedDataContext);
        assertSuccess(commandReport);
        Assert.assertTrue((!(dataDirectory.exists())));
        verifyAll();
    }
}

