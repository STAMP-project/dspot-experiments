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


import GfshParserRule.CommandCandidate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.internal.InternalConfigurationPersistenceService;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.test.junit.rules.GfshParserRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ConfigurePDXCommandTest {
    private static final String BASE_COMMAND_STRING = "configure pdx ";

    private InternalCache cache;

    private ConfigurePDXCommand command;

    private InternalConfigurationPersistenceService clusterConfigurationService;

    @Rule
    public GfshParserRule gfshParserRule = new GfshParserRule();

    @Test
    public void errorOutIfCCNotRunning() {
        Mockito.doReturn(null).when(command).getConfigurationPersistenceService();
        gfshParserRule.executeAndAssertThat(command, ConfigurePDXCommandTest.BASE_COMMAND_STRING).statusIsError().containsOutput("Configure pdx failed because cluster configuration is disabled.");
    }

    @Test
    public void parsingShouldSucceedWithoutArguments() {
        assertThat(gfshParserRule.parse(ConfigurePDXCommandTest.BASE_COMMAND_STRING)).isNotNull();
    }

    @Test
    public void parsingAutoCompleteShouldSucceed() throws Exception {
        GfshParserRule.CommandCandidate candidate = gfshParserRule.complete(ConfigurePDXCommandTest.BASE_COMMAND_STRING);
        assertThat(candidate).isNotNull();
        assertThat(candidate.getCandidates()).isNotNull();
        assertThat(candidate.getCandidates().size()).isEqualTo(5);
    }

    @Test
    public void executionShouldHandleInternalFailures() {
        Mockito.doReturn(Collections.emptySet()).when(command).getAllNormalMembers();
        Mockito.doThrow(new RuntimeException("Can't create ReflectionBasedAutoSerializer.")).when(command).createReflectionBasedAutoSerializer(ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
        gfshParserRule.executeAndAssertThat(command, (((ConfigurePDXCommandTest.BASE_COMMAND_STRING) + "--auto-serializable-classes=") + new String[]{  })).statusIsError().containsOutput("Error while processing command").containsOutput("Can't create ReflectionBasedAutoSerializer.");
        gfshParserRule.executeAndAssertThat(command, (((ConfigurePDXCommandTest.BASE_COMMAND_STRING) + "--portable-auto-serializable-classes=") + new String[]{  })).statusIsError().containsOutput("Error while processing command").containsOutput("Can't create ReflectionBasedAutoSerializer.");
        Mockito.verify(command, Mockito.times(0)).updateConfigForGroup(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void executionShouldFailIfBothPortableAndNonPortableClassesParametersAreSpecifiedAtTheSameTime() {
        gfshParserRule.executeAndAssertThat(command, ((ConfigurePDXCommandTest.BASE_COMMAND_STRING) + "--auto-serializable-classes=org.apache.geode --portable-auto-serializable-classes=org.apache.geode")).statusIsError().containsOutput("The autoserializer cannot support both portable and non-portable classes at the same time.");
        Mockito.verify(command, Mockito.times(0)).updateConfigForGroup(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void executionShouldIncludeWarningMessageWhenThereAreMembersAlreadyRunning() {
        Set<DistributedMember> members = new HashSet<>();
        DistributedMember mockMember = Mockito.mock(DistributedMember.class);
        Mockito.when(mockMember.getId()).thenReturn("member0");
        members.add(mockMember);
        Mockito.doReturn(members).when(command).getAllNormalMembers();
        gfshParserRule.executeAndAssertThat(command, ConfigurePDXCommandTest.BASE_COMMAND_STRING).statusIsSuccess().containsOutput("The command would only take effect on new data members joining the distributed system. It won't affect the existing data members");
    }

    @Test
    public void executionShouldWorkCorrectlyWhenDefaultsAreUsed() {
        gfshParserRule.executeAndAssertThat(command, ConfigurePDXCommandTest.BASE_COMMAND_STRING).statusIsSuccess().containsOutput("persistent = false").containsOutput("read-serialized = false").containsOutput("ignore-unread-fields = false");
    }

    @Test
    public void executionShouldCorrectlyConfigurePersistenceWhenDefaultDiskStoreIsUsed() {
        // Default Disk Store
        gfshParserRule.executeAndAssertThat(command, ((ConfigurePDXCommandTest.BASE_COMMAND_STRING) + "--disk-store")).statusIsSuccess().containsOutput("persistent = true").containsOutput("disk-store = DEFAULT").containsOutput("read-serialized = false").containsOutput("ignore-unread-fields = false");
        Mockito.verify(clusterConfigurationService, Mockito.times(1)).updateCacheConfig(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(command, Mockito.times(0)).createReflectionBasedAutoSerializer(ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
    }

    @Test
    public void executionShouldCorrectlyConfigurePersistenceWhenCustomDiskStoreIsUsed() {
        // Custom Disk Store
        gfshParserRule.executeAndAssertThat(command, ((ConfigurePDXCommandTest.BASE_COMMAND_STRING) + "--disk-store=myDiskStore")).statusIsSuccess().containsOutput("persistent = true").containsOutput("disk-store = myDiskStore").containsOutput("read-serialized = false").containsOutput("ignore-unread-fields = false");
        Mockito.verify(clusterConfigurationService, Mockito.times(1)).updateCacheConfig(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(command, Mockito.times(0)).createReflectionBasedAutoSerializer(ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
    }

    @Test
    public void executionShouldCorrectlyConfigureReadSerializedWhenFlagIsSetAsTrue() {
        // Custom Configuration as True
        gfshParserRule.executeAndAssertThat(command, ((ConfigurePDXCommandTest.BASE_COMMAND_STRING) + "--read-serialized=true")).statusIsSuccess().containsOutput("persistent = false").containsOutput("read-serialized = true").containsOutput("ignore-unread-fields = false");
        Mockito.verify(clusterConfigurationService, Mockito.times(1)).updateCacheConfig(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(command, Mockito.times(0)).createReflectionBasedAutoSerializer(ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
    }

    @Test
    public void executionShouldCorrectlyConfigureReadSerializedWhenFlagIsSetAsFalse() {
        // Custom Configuration as False
        gfshParserRule.executeAndAssertThat(command, ((ConfigurePDXCommandTest.BASE_COMMAND_STRING) + "--read-serialized=false")).statusIsSuccess().containsOutput("persistent = false").containsOutput("read-serialized = false").containsOutput("ignore-unread-fields = false");
        Mockito.verify(clusterConfigurationService, Mockito.times(1)).updateCacheConfig(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(command, Mockito.times(0)).createReflectionBasedAutoSerializer(ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
    }

    @Test
    public void executionShouldCorrectlyConfigureIgnoreUnreadFieldsWhenFlagIsSetAsTrue() {
        // Custom Configuration as True
        gfshParserRule.executeAndAssertThat(command, ((ConfigurePDXCommandTest.BASE_COMMAND_STRING) + "--ignore-unread-fields=true")).statusIsSuccess().containsOutput("persistent = false").containsOutput("read-serialized = false").containsOutput("ignore-unread-fields = true");
        Mockito.verify(clusterConfigurationService, Mockito.times(1)).updateCacheConfig(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(command, Mockito.times(0)).createReflectionBasedAutoSerializer(ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
    }

    @Test
    public void executionShouldCorrectlyConfigureIgnoreUnreadFieldsWhenFlagIsSetAsFalse() {
        // Custom Configuration as False
        gfshParserRule.executeAndAssertThat(command, ((ConfigurePDXCommandTest.BASE_COMMAND_STRING) + "--ignore-unread-fields=false")).statusIsSuccess().containsOutput("persistent = false").containsOutput("read-serialized = false").containsOutput("ignore-unread-fields = false");
        Mockito.verify(clusterConfigurationService, Mockito.times(1)).updateCacheConfig(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(command, Mockito.times(0)).createReflectionBasedAutoSerializer(ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
    }

    @Test
    public void executionShouldCorrectlyConfigurePortableAutoSerializableClassesWhenUsingCustomPattern() {
        String[] patterns = new String[]{ "com.company.DomainObject.*#identity=id" };
        // Custom Settings
        gfshParserRule.executeAndAssertThat(command, (((ConfigurePDXCommandTest.BASE_COMMAND_STRING) + "--portable-auto-serializable-classes=") + (patterns[0]))).statusIsSuccess().containsOutput("persistent = false").containsOutput("read-serialized = false").containsOutput("ignore-unread-fields = false").containsOutput("Portable Classes = [com.company.DomainObject.*#identity=id]").containsOutput("PDX Serializer = org.apache.geode.pdx.ReflectionBasedAutoSerializer");
        Mockito.verify(clusterConfigurationService, Mockito.times(1)).updateCacheConfig(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(command, Mockito.times(1)).createReflectionBasedAutoSerializer(true, patterns);
    }

    @Test
    public void executionShouldCorrectlyConfigureAutoSerializableClassesWhenUsingCustomPattern() {
        String[] patterns = new String[]{ "com.company.DomainObject.*#identity=id" };
        // Custom Settings
        gfshParserRule.executeAndAssertThat(command, (((ConfigurePDXCommandTest.BASE_COMMAND_STRING) + "--auto-serializable-classes=") + (patterns[0]))).statusIsSuccess().containsOutput("persistent = false").containsOutput("read-serialized = false").containsOutput("ignore-unread-fields = false").containsOutput("Non Portable Classes = [com.company.DomainObject.*#identity=id]").containsOutput("PDX Serializer = org.apache.geode.pdx.ReflectionBasedAutoSerializer");
        Mockito.verify(clusterConfigurationService, Mockito.times(1)).updateCacheConfig(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(command, Mockito.times(1)).createReflectionBasedAutoSerializer(false, patterns);
    }
}

