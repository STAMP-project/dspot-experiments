/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.serveraction.users;


import SecurityType.KERBEROS;
import UserHookParams.CLUSTER_SECURITY_TYPE;
import UserHookParams.CMD_HDFS_USER;
import UserHookParams.CMD_INPUT_FILE;
import UserHookParams.CMD_TIME_FRAME;
import UserHookParams.PAYLOAD;
import UserHookParams.SCRIPT;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.actionmanager.HostRoleCommand;
import org.apache.ambari.server.agent.CommandReport;
import org.apache.ambari.server.agent.ExecutionCommand;
import org.apache.ambari.server.utils.ShellCommandUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test suite for the PostUserCreationHookServer action class.
 */
public class PostUserCreationHookServerActionTest extends EasyMockSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostUserCreationHookServerActionTest.class);

    @Rule
    public EasyMockRule mocks = new EasyMockRule(this);

    @Mock
    private ShellCommandUtilityWrapper shellCommandUtilityWrapper;

    @Mock
    private ExecutionCommand executionCommand;

    @Mock
    private HostRoleCommand hostRoleCommand;

    @Mock
    private ObjectMapper objectMapperMock;

    @Mock
    private CollectionPersisterServiceFactory collectionPersisterServiceFactoryMock;

    @Mock
    private CsvFilePersisterService collectionPersisterService;

    @TestSubject
    private PostUserCreationHookServerAction customScriptServerAction = new PostUserCreationHookServerAction();

    private ConcurrentMap<String, Object> requestSharedDataContext = Maps.newConcurrentMap();

    private Capture<String[]> commandCapture = null;

    private Map<String, List<String>> payload = new HashMap<>();

    private ObjectMapper om = new ObjectMapper();

    @Test
    public void shouldCommandStringBeAssembledCorrectlyForSingleUser() throws Exception {
        // GIVEN
        payload = mockPayload(1);
        mockExecutionCommand(payload.size());
        String payloadJson = om.writeValueAsString(payload);
        // command params as passed to the serveraction implementation
        Map<String, String> commandParams = new HashMap<>();
        commandParams.put(PAYLOAD.param(), payloadJson);
        commandParams.put(SCRIPT.param(), "/hookfolder/hook.name");
        commandParams.put(CMD_TIME_FRAME.param(), "1000");
        commandParams.put(CMD_INPUT_FILE.param(), "/test/user_data.csv");
        commandParams.put(CLUSTER_SECURITY_TYPE.param(), KERBEROS.name());
        commandParams.put(CMD_HDFS_USER.param(), "test-hdfs-user");
        EasyMock.expect(executionCommand.getCommandParams()).andReturn(commandParams);
        EasyMock.expect(objectMapperMock.readValue(payloadJson, Map.class)).andReturn(payload);
        // captures the command arguments passed to the shell callable through the factory
        commandCapture = EasyMock.newCapture();
        // the callable mock returns a dummy result, no assertions made on the result
        EasyMock.expect(shellCommandUtilityWrapper.runCommand(EasyMock.capture(commandCapture))).andReturn(new ShellCommandUtil.Result(0, null, null)).times(payload.size());
        customScriptServerAction.setExecutionCommand(executionCommand);
        EasyMock.expect(collectionPersisterServiceFactoryMock.createCsvFilePersisterService(EasyMock.anyString())).andReturn(collectionPersisterService);
        EasyMock.expect(collectionPersisterService.persistMap(EasyMock.anyObject())).andReturn(Boolean.TRUE);
        replayAll();
        // WHEN
        CommandReport commandReport = customScriptServerAction.execute(requestSharedDataContext);
        // THEN
        String[] commandArray = commandCapture.getValue();
        Assert.assertNotNull("The command to be executed must not be null!", commandArray);
        Assert.assertEquals("The command argument array length is not as expected!", 6, commandArray.length);
        Assert.assertEquals("The command script is not as expected", "/hookfolder/hook.name", commandArray[0]);
    }

    @Test(expected = AmbariException.class)
    public void shouldServerActionFailWhenCommandParametersAreMissing() throws Exception {
        // GIVEN
        Map<String, String> commandParams = new HashMap<>();
        // the execution command lacks the required command parameters (commandparams is an empty list)
        EasyMock.expect(executionCommand.getCommandParams()).andReturn(commandParams).times(2);
        customScriptServerAction.setExecutionCommand(executionCommand);
        replayAll();
        // WHEN
        CommandReport commandReport = customScriptServerAction.execute(requestSharedDataContext);
        // THEN
        // exception is thrown
    }
}

