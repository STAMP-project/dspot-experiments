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


import DestroyAsyncEventQueueCommand.DESTROY_ASYNC_EVENT_QUEUE__AEQ_0_DESTROYED;
import DestroyAsyncEventQueueCommand.DESTROY_ASYNC_EVENT_QUEUE__AEQ_0_NOT_FOUND;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.internal.InternalConfigurationPersistenceService;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.management.internal.cli.functions.CliFunctionResult;
import org.apache.geode.management.internal.cli.functions.DestroyAsyncEventQueueFunctionArgs;
import org.apache.geode.management.internal.configuration.domain.Configuration;
import org.apache.geode.test.junit.rules.GfshParserRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static DestroyAsyncEventQueueCommand.DESTROY_ASYNC_EVENT_QUEUE__AEQ_0_NOT_FOUND;


public class DestroyAsyncEventQueueCommandTest {
    @ClassRule
    public static GfshParserRule gfsh = new GfshParserRule();

    private DestroyAsyncEventQueueCommand command;

    private InternalConfigurationPersistenceService service;

    private Map<String, Configuration> configurationMap;

    private DistributedMember member1 = Mockito.mock(DistributedMember.class);

    private DistributedMember member2 = Mockito.mock(DistributedMember.class);

    private Set<DistributedMember> members;

    private ResultCollector collector;

    private InternalCache cache;

    private List<CliFunctionResult> functionResults;

    @Test
    public void mandatoryOption() throws Exception {
        DestroyAsyncEventQueueCommandTest.gfsh.executeAndAssertThat(command, "destroy async-event-queue").statusIsError().containsOutput("Invalid command");
    }

    @Test
    public void noOptionalGroup_successful() throws Exception {
        members.add(member1);
        members.add(member2);
        functionResults.add(new CliFunctionResult("member2", true, String.format(DESTROY_ASYNC_EVENT_QUEUE__AEQ_0_DESTROYED, "queue1")));
        functionResults.add(new CliFunctionResult("member1", true, String.format(DESTROY_ASYNC_EVENT_QUEUE__AEQ_0_DESTROYED, "queue1")));
        DestroyAsyncEventQueueCommandTest.gfsh.executeAndAssertThat(command, "destroy async-event-queue --id=queue1").statusIsSuccess().containsOutput("\\\"queue1\\\" destroyed").tableHasRowCount("Member", 2);
    }

    @Test
    public void ifExistsSpecified_defaultIsTrue() throws Exception {
        members.add(member1);
        members.add(member2);
        functionResults.add(new CliFunctionResult("member1", true, String.format(("Skipping: " + (DESTROY_ASYNC_EVENT_QUEUE__AEQ_0_NOT_FOUND)), "nonexistentQueue")));
        functionResults.add(new CliFunctionResult("member2", true, String.format(("Skipping: " + (DESTROY_ASYNC_EVENT_QUEUE__AEQ_0_NOT_FOUND)), "nonexistentQueue")));
        ArgumentCaptor<DestroyAsyncEventQueueFunctionArgs> argCaptor = ArgumentCaptor.forClass(DestroyAsyncEventQueueFunctionArgs.class);
        DestroyAsyncEventQueueCommandTest.gfsh.executeAndAssertThat(command, "destroy async-event-queue --id=nonexistentQueue --if-exists").tableHasRowCount("Member", 2);
        Mockito.verify(command).executeAndGetFunctionResult(ArgumentMatchers.any(), argCaptor.capture(), ArgumentMatchers.eq(members));
        assertThat(argCaptor.getValue().isIfExists()).isEqualTo(true);
    }

    @Test
    public void ifExistsNotSpecified_isFalse() throws Exception {
        members.add(member1);
        members.add(member2);
        functionResults.add(new CliFunctionResult("member1", false, String.format(DESTROY_ASYNC_EVENT_QUEUE__AEQ_0_NOT_FOUND, "nonexistentQueue")));
        functionResults.add(new CliFunctionResult("member2", false, String.format(DESTROY_ASYNC_EVENT_QUEUE__AEQ_0_NOT_FOUND, "nonexistentQueue")));
        ArgumentCaptor<DestroyAsyncEventQueueFunctionArgs> argCaptor = ArgumentCaptor.forClass(DestroyAsyncEventQueueFunctionArgs.class);
        DestroyAsyncEventQueueCommandTest.gfsh.executeAndAssertThat(command, "destroy async-event-queue --id=nonexistentQueue").statusIsError().tableHasRowCount("Member", 2);
        Mockito.verify(command).executeAndGetFunctionResult(ArgumentMatchers.any(), argCaptor.capture(), ArgumentMatchers.eq(members));
        assertThat(argCaptor.getValue().isIfExists()).isEqualTo(false);
    }

    @Test
    public void ifExistsSpecifiedFalse() throws Exception {
        members.add(member1);
        members.add(member2);
        ArgumentCaptor<DestroyAsyncEventQueueFunctionArgs> argCaptor = ArgumentCaptor.forClass(DestroyAsyncEventQueueFunctionArgs.class);
        DestroyAsyncEventQueueCommandTest.gfsh.executeAndAssertThat(command, "destroy async-event-queue --id=nonexistentQueue --if-exists=false");
        Mockito.verify(command).executeAndGetFunctionResult(ArgumentMatchers.any(), argCaptor.capture(), ArgumentMatchers.eq(members));
        assertThat(argCaptor.getValue().isIfExists()).isEqualTo(false);
    }

    @Test
    public void ifExistsSpecifiedTrue() throws Exception {
        members.add(member1);
        members.add(member2);
        functionResults.add(new CliFunctionResult("member1", false, String.format(DESTROY_ASYNC_EVENT_QUEUE__AEQ_0_NOT_FOUND, "nonexistentQueue")));
        functionResults.add(new CliFunctionResult("member2", false, String.format(DESTROY_ASYNC_EVENT_QUEUE__AEQ_0_NOT_FOUND, "nonexistentQueue")));
        ArgumentCaptor<DestroyAsyncEventQueueFunctionArgs> argCaptor = ArgumentCaptor.forClass(DestroyAsyncEventQueueFunctionArgs.class);
        DestroyAsyncEventQueueCommandTest.gfsh.executeAndAssertThat(command, "destroy async-event-queue --id=nonexistentQueue --if-exists=true");
        Mockito.verify(command).executeAndGetFunctionResult(ArgumentMatchers.any(), argCaptor.capture(), ArgumentMatchers.eq(members));
        assertThat(argCaptor.getValue().isIfExists()).isEqualTo(true);
    }

    @Test
    public void mixedFunctionResults_returnsSuccess() throws Exception {
        members.add(member1);
        members.add(member2);
        functionResults.add(new CliFunctionResult("member2", false, String.format(DESTROY_ASYNC_EVENT_QUEUE__AEQ_0_NOT_FOUND, "queue1")));
        functionResults.add(new CliFunctionResult("member1", true, String.format(DESTROY_ASYNC_EVENT_QUEUE__AEQ_0_DESTROYED, "queue1")));
        DestroyAsyncEventQueueCommandTest.gfsh.executeAndAssertThat(command, "destroy async-event-queue --id=queue1").statusIsSuccess();
    }

    @Test
    public void mixedFunctionResultsWithIfExists_returnsSuccess() throws Exception {
        members.add(member1);
        members.add(member2);
        functionResults.add(new CliFunctionResult("member1", true, String.format(("Skipping: " + (DESTROY_ASYNC_EVENT_QUEUE__AEQ_0_NOT_FOUND)), "queue1")));
        functionResults.add(new CliFunctionResult("member1", true, String.format(DESTROY_ASYNC_EVENT_QUEUE__AEQ_0_DESTROYED, "queue1")));
        DestroyAsyncEventQueueCommandTest.gfsh.executeAndAssertThat(command, "destroy async-event-queue --id=queue1 --if-exists").statusIsSuccess();
    }
}

