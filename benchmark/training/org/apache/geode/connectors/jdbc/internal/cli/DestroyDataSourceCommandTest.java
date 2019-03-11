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
package org.apache.geode.connectors.jdbc.internal.cli;


import CreateJndiBindingCommand.DATASOURCE_TYPE.MANAGED;
import CreateJndiBindingCommand.DATASOURCE_TYPE.POOLED;
import CreateJndiBindingCommand.DATASOURCE_TYPE.SIMPLE;
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


public class DestroyDataSourceCommandTest {
    @ClassRule
    public static GfshParserRule gfsh = new GfshParserRule();

    private DestroyDataSourceCommand command;

    private InternalCache cache;

    private CacheConfig cacheConfig;

    private InternalConfigurationPersistenceService ccService;

    private static String COMMAND = "destroy data-source ";

    @Test
    public void missingMandatory() {
        DestroyDataSourceCommandTest.gfsh.executeAndAssertThat(command, DestroyDataSourceCommandTest.COMMAND).statusIsError().containsOutput("Invalid command");
    }

    @Test
    public void returnsErrorIfBindingDoesNotExistAndIfExistsUnspecified() {
        DestroyDataSourceCommandTest.gfsh.executeAndAssertThat(command, ((DestroyDataSourceCommandTest.COMMAND) + " --name=name")).statusIsError().containsOutput("does not exist.");
    }

    @Test
    public void skipsIfBindingDoesNotExistAndIfExistsSpecified() {
        DestroyDataSourceCommandTest.gfsh.executeAndAssertThat(command, ((DestroyDataSourceCommandTest.COMMAND) + " --name=name --if-exists")).statusIsSuccess().containsOutput("does not exist.");
    }

    @Test
    public void skipsIfBindingDoesNotExistAndIfExistsSpecifiedTrue() {
        DestroyDataSourceCommandTest.gfsh.executeAndAssertThat(command, ((DestroyDataSourceCommandTest.COMMAND) + " --name=name --if-exists=true")).statusIsSuccess().containsOutput("does not exist.");
    }

    @Test
    public void returnsErrorIfBindingDoesNotExistAndIfExistsSpecifiedFalse() {
        DestroyDataSourceCommandTest.gfsh.executeAndAssertThat(command, ((DestroyDataSourceCommandTest.COMMAND) + " --name=name --if-exists=false")).statusIsError().containsOutput("does not exist.");
    }

    @Test
    public void whenNoMembersFoundAndNoClusterConfigServiceRunningThenError() {
        Mockito.doReturn(Collections.emptySet()).when(command).findMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(null).when(command).getConfigurationPersistenceService();
        DestroyDataSourceCommandTest.gfsh.executeAndAssertThat(command, ((DestroyDataSourceCommandTest.COMMAND) + " --name=name")).statusIsError().containsOutput("No members found and cluster configuration disabled.");
    }

    @Test
    public void whenClusterConfigRunningAndJndiBindingFoundThenError() {
        List<JndiBindingsType.JndiBinding> bindings = new ArrayList<>();
        JndiBindingsType.JndiBinding jndiBinding = new JndiBindingsType.JndiBinding();
        jndiBinding.setJndiName("name");
        jndiBinding.setType(MANAGED.getType());
        bindings.add(jndiBinding);
        Mockito.doReturn(bindings).when(cacheConfig).getJndiBindings();
        DestroyDataSourceCommandTest.gfsh.executeAndAssertThat(command, ((DestroyDataSourceCommandTest.COMMAND) + " --name=name")).statusIsError().containsOutput("Data source named \\\"name\\\" does not exist. A jndi-binding was found with that name.");
    }

    @Test
    public void whenClusterConfigRunningAndDataSourceInUseByRegionThenError() {
        String DATA_SOURCE_NAME = "myDataSourceName";
        List<JndiBindingsType.JndiBinding> bindings = new ArrayList<>();
        JndiBindingsType.JndiBinding jndiBinding = new JndiBindingsType.JndiBinding();
        jndiBinding.setJndiName(DATA_SOURCE_NAME);
        jndiBinding.setType(SIMPLE.getType());
        bindings.add(jndiBinding);
        Mockito.doReturn(bindings).when(cacheConfig).getJndiBindings();
        setupRegionConfigToUseDataSource(DATA_SOURCE_NAME);
        DestroyDataSourceCommandTest.gfsh.executeAndAssertThat(command, (((DestroyDataSourceCommandTest.COMMAND) + " --name=") + DATA_SOURCE_NAME)).statusIsError().containsOutput(((("Data source named \\\"" + DATA_SOURCE_NAME) + "\\\" is still being used by region \\\"regionUsingDataSource\\\".") + " Use destroy jdbc-mapping --region=regionUsingDataSource and then try again."));
    }

    @Test
    public void whenNoMembersFoundAndClusterConfigRunningAndRegionUsingOtherDataSourceThenUpdateClusterConfig() {
        List<JndiBindingsType.JndiBinding> bindings = new ArrayList<>();
        JndiBindingsType.JndiBinding jndiBinding = new JndiBindingsType.JndiBinding();
        jndiBinding.setJndiName("name");
        jndiBinding.setType(SIMPLE.getType());
        bindings.add(jndiBinding);
        Mockito.doReturn(bindings).when(cacheConfig).getJndiBindings();
        setupRegionConfigToUseDataSource("otherDataSource");
        DestroyDataSourceCommandTest.gfsh.executeAndAssertThat(command, ((DestroyDataSourceCommandTest.COMMAND) + " --name=name")).statusIsSuccess().containsOutput("No members found, data source removed from cluster configuration.").containsOutput("Changes to configuration for group 'cluster' are persisted.");
        Mockito.verify(ccService).updateCacheConfig(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(command).updateConfigForGroup(ArgumentMatchers.eq("cluster"), ArgumentMatchers.eq(cacheConfig), ArgumentMatchers.isNotNull());
    }

    @Test
    public void whenNoMembersFoundAndClusterConfigRunningThenUpdateClusterConfig() {
        List<JndiBindingsType.JndiBinding> bindings = new ArrayList<>();
        JndiBindingsType.JndiBinding jndiBinding = new JndiBindingsType.JndiBinding();
        jndiBinding.setJndiName("name");
        jndiBinding.setType(SIMPLE.getType());
        bindings.add(jndiBinding);
        Mockito.doReturn(bindings).when(cacheConfig).getJndiBindings();
        DestroyDataSourceCommandTest.gfsh.executeAndAssertThat(command, ((DestroyDataSourceCommandTest.COMMAND) + " --name=name")).statusIsSuccess().containsOutput("No members found, data source removed from cluster configuration.").containsOutput("Changes to configuration for group 'cluster' are persisted.");
        Mockito.verify(ccService).updateCacheConfig(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(command).updateConfigForGroup(ArgumentMatchers.eq("cluster"), ArgumentMatchers.eq(cacheConfig), ArgumentMatchers.isNotNull());
    }

    @Test
    public void whenNoMembersFoundAndClusterConfigRunningWithPooledTypeThenUpdateClusterConfig() {
        List<JndiBindingsType.JndiBinding> bindings = new ArrayList<>();
        JndiBindingsType.JndiBinding jndiBinding = new JndiBindingsType.JndiBinding();
        jndiBinding.setJndiName("name");
        jndiBinding.setType(POOLED.getType());
        bindings.add(jndiBinding);
        Mockito.doReturn(bindings).when(cacheConfig).getJndiBindings();
        DestroyDataSourceCommandTest.gfsh.executeAndAssertThat(command, ((DestroyDataSourceCommandTest.COMMAND) + " --name=name")).statusIsSuccess().containsOutput("No members found, data source removed from cluster configuration.").containsOutput("Changes to configuration for group 'cluster' are persisted.");
        Mockito.verify(ccService).updateCacheConfig(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(command).updateConfigForGroup(ArgumentMatchers.eq("cluster"), ArgumentMatchers.eq(cacheConfig), ArgumentMatchers.isNotNull());
    }

    @Test
    public void whenMembersFoundAllReturnErrorAndIfExistsFalseThenError() {
        Set<DistributedMember> members = new HashSet<>();
        members.add(Mockito.mock(DistributedMember.class));
        CliFunctionResult result = new CliFunctionResult("server1", true, "Data source \"name\" not found on \"server1\"");
        List<CliFunctionResult> results = new ArrayList<>();
        results.add(result);
        Mockito.doReturn(members).when(command).findMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(null).when(command).getConfigurationPersistenceService();
        Mockito.doReturn(results).when(command).executeAndGetFunctionResult(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        DestroyDataSourceCommandTest.gfsh.executeAndAssertThat(command, ((DestroyDataSourceCommandTest.COMMAND) + " --name=name --if-exists=false")).statusIsError().containsOutput("Data source named \\\"name\\\" does not exist.");
    }

    @Test
    public void whenMembersFoundAllReturnErrorAndIfExistsTrueThenSuccess() {
        Set<DistributedMember> members = new HashSet<>();
        members.add(Mockito.mock(DistributedMember.class));
        CliFunctionResult result = new CliFunctionResult("server1", true, "Data source \"name\" not found on \"server1\"");
        List<CliFunctionResult> results = new ArrayList<>();
        results.add(result);
        Mockito.doReturn(members).when(command).findMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(null).when(command).getConfigurationPersistenceService();
        Mockito.doReturn(results).when(command).executeAndGetFunctionResult(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        DestroyDataSourceCommandTest.gfsh.executeAndAssertThat(command, ((DestroyDataSourceCommandTest.COMMAND) + " --name=name --if-exists=true")).statusIsSuccess().tableHasColumnOnlyWithValues("Member", "server1").tableHasColumnOnlyWithValues("Status", "OK").tableHasColumnOnlyWithValues("Message", "Data source \"name\" not found on \"server1\"");
    }

    @Test
    public void whenMembersFoundPartialReturnErrorAndIfExistsFalseThenSuccess() {
        Set<DistributedMember> members = new HashSet<>();
        members.add(Mockito.mock(DistributedMember.class));
        members.add(Mockito.mock(DistributedMember.class));
        CliFunctionResult result = new CliFunctionResult("server1", true, "Data source \"name\" not found on \"server1\"");
        CliFunctionResult result2 = new CliFunctionResult("server2", true, "Data source \"name\" destroyed on \"server2\"");
        List<CliFunctionResult> results = new ArrayList<>();
        results.add(result);
        results.add(result2);
        Mockito.doReturn(members).when(command).findMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(null).when(command).getConfigurationPersistenceService();
        Mockito.doReturn(results).when(command).executeAndGetFunctionResult(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        DestroyDataSourceCommandTest.gfsh.executeAndAssertThat(command, ((DestroyDataSourceCommandTest.COMMAND) + " --name=name --if-exists=false")).statusIsSuccess().tableHasColumnOnlyWithValues("Member", "server1", "server2").tableHasColumnOnlyWithValues("Status", "OK", "OK").tableHasColumnOnlyWithValues("Message", "Data source \"name\" not found on \"server1\"", "Data source \"name\" destroyed on \"server2\"");
    }

    @Test
    public void whenMembersFoundAndNoClusterConfigRunningThenOnlyInvokeFunction() {
        Set<DistributedMember> members = new HashSet<>();
        members.add(Mockito.mock(DistributedMember.class));
        CliFunctionResult result = new CliFunctionResult("server1", true, "Data source \"name\" destroyed on \"server1\"");
        List<CliFunctionResult> results = new ArrayList<>();
        results.add(result);
        Mockito.doReturn(members).when(command).findMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(null).when(command).getConfigurationPersistenceService();
        Mockito.doReturn(results).when(command).executeAndGetFunctionResult(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        DestroyDataSourceCommandTest.gfsh.executeAndAssertThat(command, ((DestroyDataSourceCommandTest.COMMAND) + " --name=name")).statusIsSuccess().tableHasColumnOnlyWithValues("Member", "server1").tableHasColumnOnlyWithValues("Status", "OK").tableHasColumnOnlyWithValues("Message", "Data source \"name\" destroyed on \"server1\"");
        Mockito.verify(ccService, Mockito.times(0)).updateCacheConfig(ArgumentMatchers.any(), ArgumentMatchers.any());
        ArgumentCaptor<DestroyJndiBindingFunction> function = ArgumentCaptor.forClass(DestroyJndiBindingFunction.class);
        ArgumentCaptor<Object[]> arguments = ArgumentCaptor.forClass(Object[].class);
        ArgumentCaptor<Set<DistributedMember>> targetMembers = ArgumentCaptor.forClass(Set.class);
        Mockito.verify(command, Mockito.times(1)).executeAndGetFunctionResult(function.capture(), arguments.capture(), targetMembers.capture());
        String jndiName = ((String) (arguments.getValue()[0]));
        boolean destroyingDataSource = ((boolean) (arguments.getValue()[1]));
        assertThat(function.getValue()).isInstanceOf(DestroyJndiBindingFunction.class);
        assertThat(jndiName).isEqualTo("name");
        assertThat(destroyingDataSource).isEqualTo(true);
        assertThat(targetMembers.getValue()).isEqualTo(members);
    }

    @Test
    public void whenMembersFoundAndClusterConfigRunningThenUpdateClusterConfigAndInvokeFunction() {
        List<JndiBindingsType.JndiBinding> bindings = new ArrayList<>();
        JndiBindingsType.JndiBinding jndiBinding = new JndiBindingsType.JndiBinding();
        jndiBinding.setJndiName("name");
        jndiBinding.setType(SIMPLE.getType());
        bindings.add(jndiBinding);
        Mockito.doReturn(bindings).when(cacheConfig).getJndiBindings();
        Set<DistributedMember> members = new HashSet<>();
        members.add(Mockito.mock(DistributedMember.class));
        CliFunctionResult result = new CliFunctionResult("server1", true, "Data source \"name\" destroyed on \"server1\"");
        List<CliFunctionResult> results = new ArrayList<>();
        results.add(result);
        Mockito.doReturn(members).when(command).findMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(results).when(command).executeAndGetFunctionResult(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        DestroyDataSourceCommandTest.gfsh.executeAndAssertThat(command, ((DestroyDataSourceCommandTest.COMMAND) + " --name=name")).statusIsSuccess().tableHasColumnOnlyWithValues("Member", "server1").tableHasColumnOnlyWithValues("Status", "OK").tableHasColumnOnlyWithValues("Message", "Data source \"name\" destroyed on \"server1\"");
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
        assertThat(destroyingDataSource).isEqualTo(true);
        assertThat(targetMembers.getValue()).isEqualTo(members);
    }
}

