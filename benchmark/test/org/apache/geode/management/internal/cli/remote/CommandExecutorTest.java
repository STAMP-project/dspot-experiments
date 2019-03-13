/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal.cli.remote;


import Result.Status.ERROR;
import Result.Status.OK;
import java.util.HashSet;
import java.util.Set;
import org.apache.geode.distributed.internal.InternalConfigurationPersistenceService;
import org.apache.geode.management.cli.SingleGfshCommand;
import org.apache.geode.management.internal.cli.GfshParseResult;
import org.apache.geode.management.internal.cli.exceptions.UserErrorException;
import org.apache.geode.management.internal.cli.result.model.ResultModel;
import org.apache.geode.management.internal.exceptions.EntityNotFoundException;
import org.apache.geode.security.NotAuthorizedException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CommandExecutorTest {
    private GfshParseResult parseResult;

    private CommandExecutor executor;

    private ResultModel result;

    private SingleGfshCommand testCommand;

    private InternalConfigurationPersistenceService ccService;

    private org.apache.geode.cache.Region configRegion;

    @Test
    public void executeWhenGivenDummyParseResult() throws Exception {
        Object result = executor.execute(parseResult);
        assertThat(result).isInstanceOf(ResultModel.class);
        assertThat(result.toString()).contains("Error while processing command");
    }

    @Test
    public void returnsResultAsExpected() throws Exception {
        Mockito.doReturn(result).when(executor).invokeCommand(ArgumentMatchers.any(), ArgumentMatchers.any());
        Object thisResult = executor.execute(parseResult);
        assertThat(thisResult).isSameAs(result);
    }

    @Test
    public void testNullResult() throws Exception {
        Mockito.doReturn(null).when(executor).invokeCommand(ArgumentMatchers.any(), ArgumentMatchers.any());
        Object thisResult = executor.execute(parseResult);
        assertThat(thisResult.toString()).contains("Command returned null");
    }

    @Test
    public void anyRuntimeExceptionGetsCaught() throws Exception {
        Mockito.doThrow(new RuntimeException("my message here")).when(executor).invokeCommand(ArgumentMatchers.any(), ArgumentMatchers.any());
        Object thisResult = executor.execute(parseResult);
        assertThat(getStatus()).isEqualTo(ERROR);
        assertThat(thisResult.toString()).contains("my message here");
    }

    @Test
    public void notAuthorizedExceptionGetsThrown() throws Exception {
        Mockito.doThrow(new NotAuthorizedException("Not Authorized")).when(executor).invokeCommand(ArgumentMatchers.any(), ArgumentMatchers.any());
        assertThatThrownBy(() -> executor.execute(parseResult)).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void anyIllegalArgumentExceptionGetsCaught() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("my message here")).when(executor).invokeCommand(ArgumentMatchers.any(), ArgumentMatchers.any());
        Object thisResult = executor.execute(parseResult);
        assertThat(getStatus()).isEqualTo(ERROR);
        assertThat(thisResult.toString()).contains("my message here");
    }

    @Test
    public void anyIllegalStateExceptionGetsCaught() throws Exception {
        Mockito.doThrow(new IllegalStateException("my message here")).when(executor).invokeCommand(ArgumentMatchers.any(), ArgumentMatchers.any());
        Object thisResult = executor.execute(parseResult);
        assertThat(getStatus()).isEqualTo(ERROR);
        assertThat(thisResult.toString()).contains("my message here");
    }

    @Test
    public void anyUserErrorExceptionGetsCaught() throws Exception {
        Mockito.doThrow(new UserErrorException("my message here")).when(executor).invokeCommand(ArgumentMatchers.any(), ArgumentMatchers.any());
        Object thisResult = executor.execute(parseResult);
        assertThat(getStatus()).isEqualTo(ERROR);
        assertThat(thisResult.toString()).contains("my message here");
    }

    @Test
    public void anyEntityNotFoundException_statusOK() throws Exception {
        Mockito.doThrow(new EntityNotFoundException("my message here", true)).when(executor).invokeCommand(ArgumentMatchers.any(), ArgumentMatchers.any());
        Object thisResult = executor.execute(parseResult);
        assertThat(getStatus()).isEqualTo(OK);
        assertThat(thisResult.toString()).contains("Skipping: my message here");
    }

    @Test
    public void anyEntityNotFoundException_statusERROR() throws Exception {
        Mockito.doThrow(new EntityNotFoundException("my message here")).when(executor).invokeCommand(ArgumentMatchers.any(), ArgumentMatchers.any());
        Object thisResult = executor.execute(parseResult);
        assertThat(getStatus()).isEqualTo(ERROR);
        assertThat(thisResult.toString()).contains("my message here");
    }

    @Test
    public void invokeCommandWithUpdateAllConfigsInterface_multipleGroupOptionSpecifiedWhenSingleConfiguredGroups_CallsUpdateConfigForGroupTwice() {
        Set<String> configuredGroups = new HashSet<>();
        configuredGroups.add("group1");
        Mockito.when(parseResult.getParamValueAsString("group")).thenReturn("Group1,Group2");
        Mockito.doReturn(result).when(executor).callInvokeMethod(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(configuredGroups).when(ccService).getGroups();
        Object thisResult = executor.invokeCommand(testCommand, parseResult);
        Mockito.verify(testCommand, Mockito.times(1)).updateConfigForGroup(ArgumentMatchers.eq("Group1"), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(testCommand, Mockito.times(1)).updateConfigForGroup(ArgumentMatchers.eq("Group2"), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void invokeCommandWithUpdateAllConfigsInterface_singleGroupOptionSpecifiedWhenMultipleConfiguredGroups_CallsUpdateConfigForGroup() {
        Set<String> configuredGroups = new HashSet<>();
        configuredGroups.add("group1");
        configuredGroups.add("group2");
        Mockito.when(parseResult.getParamValueAsString("group")).thenReturn("group1");
        Mockito.doReturn(result).when(executor).callInvokeMethod(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(configuredGroups).when(ccService).getGroups();
        Object thisResult = executor.invokeCommand(testCommand, parseResult);
        Mockito.verify(testCommand, Mockito.times(1)).updateConfigForGroup(ArgumentMatchers.eq("group1"), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void invokeCommandWithUpdateAllConfigsInterface_noGroupOptionSpecifiedWhenSingleConfiguredGroups_CallsUpdateConfigForGroup() {
        Set<String> configuredGroups = new HashSet<>();
        configuredGroups.add("group1");
        Mockito.when(parseResult.getParamValueAsString("group")).thenReturn(null);
        Mockito.doReturn(result).when(executor).callInvokeMethod(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(configuredGroups).when(ccService).getGroups();
        Object thisResult = executor.invokeCommand(testCommand, parseResult);
        Mockito.verify(testCommand, Mockito.times(1)).updateConfigForGroup(ArgumentMatchers.eq("group1"), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void invokeCommandWithOutUpdateAllConfigsInterface_noGroupOptionSpecifiedWhenSingleConfiguredGroups_CallsUpdateConfigForCluster() {
        testCommand = Mockito.mock(SingleGfshCommand.class);
        Mockito.doReturn(ccService).when(testCommand).getConfigurationPersistenceService();
        Set<String> configuredGroups = new HashSet<>();
        configuredGroups.add("group1");
        Mockito.when(parseResult.getParamValueAsString("group")).thenReturn(null);
        Mockito.doReturn(result).when(executor).callInvokeMethod(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(configuredGroups).when(ccService).getGroups();
        Object thisResult = executor.invokeCommand(testCommand, parseResult);
        Mockito.verify(testCommand, Mockito.times(1)).updateConfigForGroup(ArgumentMatchers.eq("cluster"), ArgumentMatchers.any(), ArgumentMatchers.any());
    }
}

