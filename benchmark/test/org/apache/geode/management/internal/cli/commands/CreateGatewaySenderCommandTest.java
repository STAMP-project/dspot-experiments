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
import CliStrings.CREATE_GATEWAYSENDER__MSG__CAN_NOT_CREATE_DIFFERENT_VERSIONS;
import GatewaySender.OrderPolicy.THREAD;
import GfshParserRule.CommandCandidate;
import Version.CURRENT;
import Version.GEODE_1_4_0;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.geode.cache.configuration.CacheConfig;
import org.apache.geode.cache.wan.GatewaySender;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.management.internal.cli.functions.CliFunctionResult;
import org.apache.geode.management.internal.cli.functions.GatewaySenderFunctionArgs;
import org.apache.geode.management.internal.cli.result.model.ResultModel;
import org.apache.geode.test.junit.rules.GfshParserRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CreateGatewaySenderCommandTest {
    @ClassRule
    public static GfshParserRule gfsh = new GfshParserRule();

    private CreateGatewaySenderCommand command;

    private InternalCache cache;

    private List<CliFunctionResult> functionResults;

    private CliFunctionResult cliFunctionResult;

    ArgumentCaptor<GatewaySenderFunctionArgs> argsArgumentCaptor = ArgumentCaptor.forClass(GatewaySenderFunctionArgs.class);

    @Test
    public void missingId() {
        CreateGatewaySenderCommandTest.gfsh.executeAndAssertThat(command, "create gateway-sender --remote-distributed-system-id=1").statusIsError().containsOutput("Invalid command");
    }

    @Test
    public void missingRemoteId() {
        CreateGatewaySenderCommandTest.gfsh.executeAndAssertThat(command, "create gateway-sender --id=ln").statusIsError().containsOutput("Invalid command");
    }

    @Test
    public void missingOrderPolicy() {
        CreateGatewaySenderCommandTest.gfsh.executeAndAssertThat(command, ("create gateway-sender --id=ln --remote-distributed-system-id=1 " + "--dispatcher-threads=2")).statusIsError().containsOutput("Must specify --order-policy when --dispatcher-threads is larger than 1");
        Mockito.doReturn(Collections.EMPTY_SET).when(command).findMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        CreateGatewaySenderCommandTest.gfsh.executeAndAssertThat(command, ("create gateway-sender --id=ln --remote-distributed-system-id=1 " + "--dispatcher-threads=1")).statusIsError().containsOutput("No Members Found");
    }

    @Test
    public void paralleAndThreadOrderPolicy() {
        CreateGatewaySenderCommandTest.gfsh.executeAndAssertThat(command, ("create gateway-sender --id=ln --remote-distributed-system-id=1 " + "--parallel --order-policy=THREAD")).statusIsError().containsOutput("Parallel Gateway Sender can not be created with THREAD OrderPolicy");
    }

    @Test
    public void orderPolicyAutoComplete() {
        String command = "create gateway-sender --id=ln --remote-distributed-system-id=1 --order-policy";
        GfshParserRule.CommandCandidate candidate = CreateGatewaySenderCommandTest.gfsh.complete(command);
        assertThat(candidate.getCandidates()).hasSize(3);
        assertThat(candidate.getFirstCandidate()).isEqualTo((command + "=KEY"));
    }

    @Test
    public void whenCommandOnMember() {
        Mockito.doReturn(Mockito.mock(Set.class)).when(command).getMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        cliFunctionResult = new CliFunctionResult("member", StatusState.OK, "cliFunctionResult");
        functionResults.add(cliFunctionResult);
        CreateGatewaySenderCommandTest.gfsh.executeAndAssertThat(command, "create gateway-sender --member=xyz --id=1 --remote-distributed-system-id=1").statusIsSuccess();
    }

    @Test
    public void testFunctionArgs() {
        Mockito.doReturn(Mockito.mock(Set.class)).when(command).getMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        cliFunctionResult = new CliFunctionResult("member", StatusState.OK, "cliFunctionResult");
        functionResults.add(cliFunctionResult);
        CreateGatewaySenderCommandTest.gfsh.executeAndAssertThat(command, ("create gateway-sender --member=xyz --id=1 --remote-distributed-system-id=1" + (" --order-policy=thread --dispatcher-threads=2 " + "--gateway-event-filter=test1,test2 --gateway-transport-filter=test1,test2"))).statusIsSuccess();
        Mockito.verify(command).executeAndGetFunctionResult(ArgumentMatchers.any(), argsArgumentCaptor.capture(), ArgumentMatchers.any(Set.class));
        assertThat(argsArgumentCaptor.getValue().getOrderPolicy()).isEqualTo(THREAD.toString());
        assertThat(argsArgumentCaptor.getValue().getRemoteDistributedSystemId()).isEqualTo(1);
        assertThat(argsArgumentCaptor.getValue().getDispatcherThreads()).isEqualTo(2);
        assertThat(argsArgumentCaptor.getValue().getGatewayEventFilter()).containsExactly("test1", "test2");
        assertThat(argsArgumentCaptor.getValue().getGatewayTransportFilter()).containsExactly("test1", "test2");
    }

    @Test
    public void testReturnsConfigInResultModel() {
        Mockito.doReturn(Mockito.mock(Set.class)).when(command).getMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        cliFunctionResult = new CliFunctionResult("member", StatusState.OK, "cliFunctionResult");
        functionResults.add(cliFunctionResult);
        ResultModel resultModel = CreateGatewaySenderCommandTest.gfsh.executeAndAssertThat(command, ("create gateway-sender --group=xyz --id=1 --remote-distributed-system-id=1" + (" --order-policy=thread --dispatcher-threads=2 " + "--gateway-event-filter=test1,test2 --gateway-transport-filter=test1,test2"))).getResultModel();
        assertThat(resultModel.getConfigObject()).isNotNull();
        CacheConfig.GatewaySender sender = ((CacheConfig.GatewaySender) (resultModel.getConfigObject()));
        assertThat(sender.getId()).isEqualTo("1");
        assertThat(sender.getRemoteDistributedSystemId()).isEqualTo("1");
        assertThat(sender.getOrderPolicy()).isEqualTo("THREAD");
    }

    @Test
    public void whenMembersAreDifferentVersions() {
        // Create a set of mixed version members
        Set<DistributedMember> members = new HashSet<>();
        InternalDistributedMember currentVersionMember = Mockito.mock(InternalDistributedMember.class);
        Mockito.doReturn(CURRENT).when(currentVersionMember).getVersionObject();
        InternalDistributedMember oldVersionMember = Mockito.mock(InternalDistributedMember.class);
        Mockito.doReturn(GEODE_1_4_0).when(oldVersionMember).getVersionObject();
        members.add(currentVersionMember);
        members.add(oldVersionMember);
        Mockito.doReturn(members).when(command).getMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        // Verify executing the command fails
        CreateGatewaySenderCommandTest.gfsh.executeAndAssertThat(command, "create gateway-sender --id=1 --remote-distributed-system-id=1").statusIsError().containsOutput(CREATE_GATEWAYSENDER__MSG__CAN_NOT_CREATE_DIFFERENT_VERSIONS);
    }
}

