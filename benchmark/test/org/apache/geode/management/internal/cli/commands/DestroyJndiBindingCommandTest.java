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


import JndiBindingsType.JndiBinding;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.geode.cache.configuration.CacheConfig;
import org.apache.geode.cache.configuration.JndiBindingsType;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.internal.InternalConfigurationPersistenceService;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.management.internal.cli.functions.CliFunctionResult;
import org.apache.geode.management.internal.cli.functions.DestroyJndiBindingFunction;
import org.apache.geode.test.junit.rules.GfshParserRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DestroyJndiBindingCommandTest {
    @ClassRule
    public static GfshParserRule gfsh = new GfshParserRule();

    private DestroyJndiBindingCommand command;

    private InternalCache cache;

    private CacheConfig cacheConfig;

    private InternalConfigurationPersistenceService ccService;

    private static String COMMAND = "destroy jndi-binding ";

    @Test
    public void missingMandatory() {
        DestroyJndiBindingCommandTest.gfsh.executeAndAssertThat(command, DestroyJndiBindingCommandTest.COMMAND).statusIsError().containsOutput("Invalid command");
    }

    @Test
    public void returnsErrorIfBindingDoesNotExistAndIfExistsUnspecified() {
        DestroyJndiBindingCommandTest.gfsh.executeAndAssertThat(command, ((DestroyJndiBindingCommandTest.COMMAND) + " --name=name")).statusIsError().containsOutput("does not exist.");
    }

    @Test
    public void skipsIfBindingDoesNotExistAndIfExistsSpecified() {
        DestroyJndiBindingCommandTest.gfsh.executeAndAssertThat(command, ((DestroyJndiBindingCommandTest.COMMAND) + " --name=name --if-exists")).statusIsSuccess().containsOutput("does not exist.");
    }

    @Test
    public void skipsIfBindingDoesNotExistAndIfExistsSpecifiedTrue() {
        DestroyJndiBindingCommandTest.gfsh.executeAndAssertThat(command, ((DestroyJndiBindingCommandTest.COMMAND) + " --name=name --if-exists=true")).statusIsSuccess().containsOutput("does not exist.");
    }

    @Test
    public void returnsErrorIfBindingDoesNotExistAndIfExistsSpecifiedFalse() {
        DestroyJndiBindingCommandTest.gfsh.executeAndAssertThat(command, ((DestroyJndiBindingCommandTest.COMMAND) + " --name=name --if-exists=false")).statusIsError().containsOutput("does not exist.");
    }

    @Test
    public void whenNoMembersFoundAndNoClusterConfigServiceRunningThenError() {
        Mockito.doReturn(Collections.emptySet()).when(command).findMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(null).when(command).getConfigurationPersistenceService();
        DestroyJndiBindingCommandTest.gfsh.executeAndAssertThat(command, ((DestroyJndiBindingCommandTest.COMMAND) + " --name=name")).statusIsSuccess().containsOutput("No members found").containsOutput("Cluster configuration service is not running. Configuration change is not persisted.");
    }

    @Test
    public void whenNoMembersFoundAndClusterConfigRunningThenUpdateClusterConfig() {
        List<JndiBindingsType.JndiBinding> bindings = new ArrayList<>();
        JndiBindingsType.JndiBinding jndiBinding = new JndiBindingsType.JndiBinding();
        jndiBinding.setJndiName("name");
        bindings.add(jndiBinding);
        Mockito.doReturn(bindings).when(cacheConfig).getJndiBindings();
        DestroyJndiBindingCommandTest.gfsh.executeAndAssertThat(command, ((DestroyJndiBindingCommandTest.COMMAND) + " --name=name")).statusIsSuccess().containsOutput("No members found.").containsOutput("Changes to configuration for group 'cluster' are persisted.");
        Mockito.verify(ccService).updateCacheConfig(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(command).updateConfigForGroup(ArgumentMatchers.eq("cluster"), ArgumentMatchers.eq(cacheConfig), ArgumentMatchers.any());
    }

    @Test
    public void whenMembersFoundAndNoClusterConfigRunningThenOnlyInvokeFunction() {
        Set<DistributedMember> members = new HashSet<>();
        members.add(Mockito.mock(DistributedMember.class));
        CliFunctionResult result = new CliFunctionResult("server1", true, "Jndi binding \"name\" destroyed on \"server1\"");
        List<CliFunctionResult> results = new ArrayList<>();
        results.add(result);
        Mockito.doReturn(members).when(command).findMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(null).when(command).getConfigurationPersistenceService();
        Mockito.doReturn(results).when(command).executeAndGetFunctionResult(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        DestroyJndiBindingCommandTest.gfsh.executeAndAssertThat(command, ((DestroyJndiBindingCommandTest.COMMAND) + " --name=name")).statusIsSuccess().tableHasColumnOnlyWithValues("Member", "server1").tableHasColumnOnlyWithValues("Status", "OK").tableHasColumnOnlyWithValues("Message", "Jndi binding \"name\" destroyed on \"server1\"");
        Mockito.verify(ccService, Mockito.times(0)).updateCacheConfig(ArgumentMatchers.any(), ArgumentMatchers.any());
        ArgumentCaptor<DestroyJndiBindingFunction> function = ArgumentCaptor.forClass(DestroyJndiBindingFunction.class);
        ArgumentCaptor<Object[]> arguments = ArgumentCaptor.forClass(Object[].class);
        ArgumentCaptor<Set<DistributedMember>> targetMembers = ArgumentCaptor.forClass(Set.class);
        Mockito.verify(command, Mockito.times(1)).executeAndGetFunctionResult(function.capture(), arguments.capture(), targetMembers.capture());
        String jndiName = ((String) (arguments.getValue()[0]));
        boolean destroyingDataSource = ((boolean) (arguments.getValue()[1]));
        assertThat(function.getValue()).isInstanceOf(DestroyJndiBindingFunction.class);
        assertThat(jndiName).isEqualTo("name");
        assertThat(destroyingDataSource).isEqualTo(false);
        assertThat(targetMembers.getValue()).isEqualTo(members);
    }

    @Test
    public void whenMembersFoundAndClusterConfigRunningThenUpdateClusterConfigAndInvokeFunction() {
        List<JndiBindingsType.JndiBinding> bindings = new ArrayList<>();
        JndiBindingsType.JndiBinding jndiBinding = new JndiBindingsType.JndiBinding();
        jndiBinding.setJndiName("name");
        bindings.add(jndiBinding);
        Mockito.doReturn(bindings).when(cacheConfig).getJndiBindings();
        Set<DistributedMember> members = new HashSet<>();
        members.add(Mockito.mock(DistributedMember.class));
        CliFunctionResult result = new CliFunctionResult("server1", true, "Jndi binding \"name\" destroyed on \"server1\"");
        List<CliFunctionResult> results = new ArrayList<>();
        results.add(result);
        Mockito.doReturn(members).when(command).findMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(results).when(command).executeAndGetFunctionResult(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        DestroyJndiBindingCommandTest.gfsh.executeAndAssertThat(command, ((DestroyJndiBindingCommandTest.COMMAND) + " --name=name")).statusIsSuccess().tableHasColumnOnlyWithValues("Member", "server1").tableHasColumnOnlyWithValues("Status", "OK").tableHasColumnOnlyWithValues("Message", "Jndi binding \"name\" destroyed on \"server1\"");
        assertThat(cacheConfig.getJndiBindings().isEmpty()).isTrue();
        Mockito.verify(command).updateConfigForGroup(ArgumentMatchers.eq("cluster"), ArgumentMatchers.eq(cacheConfig), ArgumentMatchers.any());
        ArgumentCaptor<DestroyJndiBindingFunction> function = ArgumentCaptor.forClass(DestroyJndiBindingFunction.class);
        ArgumentCaptor<Object[]> arguments = ArgumentCaptor.forClass(Object[].class);
        ArgumentCaptor<Set<DistributedMember>> targetMembers = ArgumentCaptor.forClass(Set.class);
        Mockito.verify(command, Mockito.times(1)).executeAndGetFunctionResult(function.capture(), arguments.capture(), targetMembers.capture());
        String jndiName = ((String) (arguments.getValue()[0]));
        boolean destroyingDataSource = ((boolean) (arguments.getValue()[1]));
        assertThat(function.getValue()).isInstanceOf(DestroyJndiBindingFunction.class);
        assertThat(jndiName).isEqualTo("name");
        assertThat(destroyingDataSource).isEqualTo(false);
        assertThat(targetMembers.getValue()).isEqualTo(members);
    }
}

