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
package org.apache.geode.management.internal.cli.commands;


import CliFunctionResult.StatusState;
import CliStrings.CREATE_GATEWAYRECEIVER__BINDADDRESS;
import CliStrings.CREATE_GATEWAYRECEIVER__ENDPORT;
import CliStrings.CREATE_GATEWAYRECEIVER__GATEWAYTRANSPORTFILTER;
import CliStrings.CREATE_GATEWAYRECEIVER__HOSTNAMEFORSENDERS;
import CliStrings.CREATE_GATEWAYRECEIVER__MANUALSTART;
import CliStrings.CREATE_GATEWAYRECEIVER__MAXTIMEBETWEENPINGS;
import CliStrings.CREATE_GATEWAYRECEIVER__SOCKETBUFFERSIZE;
import CliStrings.CREATE_GATEWAYRECEIVER__STARTPORT;
import CliStrings.GROUP;
import CliStrings.IFNOTEXISTS;
import CliStrings.MEMBER;
import java.util.List;
import java.util.Set;
import org.apache.geode.distributed.internal.InternalConfigurationPersistenceService;
import org.apache.geode.management.internal.cli.GfshParseResult;
import org.apache.geode.management.internal.cli.functions.CliFunctionResult;
import org.apache.geode.test.junit.rules.GfshParserRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CreateGatewayReceiverCommandTest {
    @ClassRule
    public static GfshParserRule gfsh = new GfshParserRule();

    private CreateGatewayReceiverCommand command;

    private List<CliFunctionResult> functionResults;

    private InternalConfigurationPersistenceService ccService;

    private CliFunctionResult result1;

    @Test
    public void testUnspecifiedDefaultValues() {
        GfshParseResult parseResult = CreateGatewayReceiverCommandTest.gfsh.parse("create gateway-receiver");
        assertThat(parseResult.getParamValue(MEMBER)).isNull();
        assertThat(parseResult.getParamValue(GROUP)).isNull();
        assertThat(parseResult.getParamValue(CREATE_GATEWAYRECEIVER__STARTPORT)).isNull();
        assertThat(parseResult.getParamValue(CREATE_GATEWAYRECEIVER__ENDPORT)).isNull();
        assertThat(parseResult.getParamValue(CREATE_GATEWAYRECEIVER__BINDADDRESS)).isNull();
        assertThat(parseResult.getParamValue(CREATE_GATEWAYRECEIVER__MAXTIMEBETWEENPINGS)).isNull();
        assertThat(parseResult.getParamValue(CREATE_GATEWAYRECEIVER__SOCKETBUFFERSIZE)).isNull();
        assertThat(parseResult.getParamValue(CREATE_GATEWAYRECEIVER__GATEWAYTRANSPORTFILTER)).isNull();
        assertThat(parseResult.getParamValue(CREATE_GATEWAYRECEIVER__HOSTNAMEFORSENDERS)).isNull();
        assertThat(parseResult.getParamValue(IFNOTEXISTS)).isEqualTo(false);
        assertThat(parseResult.getParamValue(CREATE_GATEWAYRECEIVER__MANUALSTART)).isEqualTo(false);
    }

    @Test
    public void testSpecifiedDefaultValues() {
        GfshParseResult parseResult = CreateGatewayReceiverCommandTest.gfsh.parse("create gateway-receiver --manual-start --if-not-exists");
        assertThat(parseResult.getParamValue(MEMBER)).isNull();
        assertThat(parseResult.getParamValue(GROUP)).isNull();
        assertThat(parseResult.getParamValue(CREATE_GATEWAYRECEIVER__STARTPORT)).isNull();
        assertThat(parseResult.getParamValue(CREATE_GATEWAYRECEIVER__ENDPORT)).isNull();
        assertThat(parseResult.getParamValue(CREATE_GATEWAYRECEIVER__BINDADDRESS)).isNull();
        assertThat(parseResult.getParamValue(CREATE_GATEWAYRECEIVER__MAXTIMEBETWEENPINGS)).isNull();
        assertThat(parseResult.getParamValue(CREATE_GATEWAYRECEIVER__SOCKETBUFFERSIZE)).isNull();
        assertThat(parseResult.getParamValue(CREATE_GATEWAYRECEIVER__GATEWAYTRANSPORTFILTER)).isNull();
        assertThat(parseResult.getParamValue(CREATE_GATEWAYRECEIVER__HOSTNAMEFORSENDERS)).isNull();
        assertThat(parseResult.getParamValue(IFNOTEXISTS)).isEqualTo(true);
        assertThat(parseResult.getParamValue(CREATE_GATEWAYRECEIVER__MANUALSTART)).isEqualTo(true);
    }

    @Test
    public void endPortMustBeLargerThanStartPort() {
        CreateGatewayReceiverCommandTest.gfsh.executeAndAssertThat(command, "create gateway-receiver --end-port=1").statusIsError().containsOutput("start-port must be smaller than end-port");
        CreateGatewayReceiverCommandTest.gfsh.executeAndAssertThat(command, "create gateway-receiver --start-port=60000").statusIsError().containsOutput("start-port must be smaller than end-port");
        CreateGatewayReceiverCommandTest.gfsh.executeAndAssertThat(command, "create gateway-receiver --end-port=1 --start-port=2").statusIsError().containsOutput("start-port must be smaller than end-port");
    }

    @Test
    public void gatewayReceiverCanBeCreatedButIsNotPersistedWithoutConfigurationService() {
        Mockito.doReturn(Mockito.mock(Set.class)).when(command).getMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(null).when(command).getConfigurationPersistenceService();
        result1 = new CliFunctionResult("member", StatusState.OK, "result1");
        functionResults.add(result1);
        CreateGatewayReceiverCommandTest.gfsh.executeAndAssertThat(command, "create gateway-receiver").statusIsSuccess().containsOutput("Cluster configuration service is not running. Configuration change is not persisted");
        Mockito.verify(ccService, Mockito.never()).addXmlEntity(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(ccService, Mockito.never()).updateCacheConfig(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void gatewayReceiverIsCreatedButNotPersistedWithMemberOption() {
        Mockito.doReturn(Mockito.mock(Set.class)).when(command).getMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(ccService).when(command).getConfigurationPersistenceService();
        result1 = new CliFunctionResult("member", StatusState.OK, "result1");
        functionResults.add(result1);
        CreateGatewayReceiverCommandTest.gfsh.executeAndAssertThat(command, "create gateway-receiver --member=xyz").statusIsSuccess().containsOutput("Configuration change is not persisted because the command is executed on specific member");
        Mockito.verify(ccService, Mockito.never()).addXmlEntity(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(ccService, Mockito.never()).updateCacheConfig(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void configurationIsNotPersistedWhenCreationOnOnlyMemberFails() {
        Mockito.doReturn(Mockito.mock(Set.class)).when(command).getMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(ccService).when(command).getConfigurationPersistenceService();
        result1 = new CliFunctionResult("member", StatusState.ERROR, "result1");
        functionResults.add(result1);
        // does not delete because command failed, so hasNoFailToPersistError should still be true
        CreateGatewayReceiverCommandTest.gfsh.executeAndAssertThat(command, "create gateway-receiver").statusIsError();
        Mockito.verify(ccService, Mockito.never()).updateCacheConfig(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void configurationIsPersistedWhenCreationOnAnyMemberFails() {
        Mockito.doReturn(Mockito.mock(Set.class)).when(command).getMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(ccService).when(command).getConfigurationPersistenceService();
        result1 = new CliFunctionResult("member", StatusState.ERROR, "result1");
        functionResults.add(result1);
        CliFunctionResult result2 = new CliFunctionResult("member", StatusState.OK, "result2");
        functionResults.add(result2);
        // does not delete because command failed, so hasNoFailToPersistError should still be true
        CreateGatewayReceiverCommandTest.gfsh.executeAndAssertThat(command, "create gateway-receiver").statusIsSuccess();
        Mockito.verify(ccService, Mockito.times(1)).updateCacheConfig(ArgumentMatchers.any(), ArgumentMatchers.any());
    }
}

