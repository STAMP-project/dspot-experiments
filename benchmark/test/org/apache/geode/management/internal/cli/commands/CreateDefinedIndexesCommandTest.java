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


import CreateDefinedIndexesCommand.CREATE_DEFINED_INDEXES_SECTION;
import IndexType.FUNCTIONAL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.internal.InternalConfigurationPersistenceService;
import org.apache.geode.management.internal.cli.functions.CliFunctionResult;
import org.apache.geode.management.internal.cli.result.CommandResult;
import org.apache.geode.test.junit.rules.GfshParserRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CreateDefinedIndexesCommandTest {
    @Rule
    public GfshParserRule gfshParser = new GfshParserRule();

    private CommandResult result;

    private ResultCollector resultCollector;

    private CreateDefinedIndexesCommand command;

    @Test
    public void noDefinitions() throws Exception {
        result = gfshParser.executeCommandWithInstance(command, "create defined indexes");
        assertThat(result.getStatus()).isEqualTo(OK);
        assertThat(result.getMessageFromContent()).contains("No indexes defined");
    }

    @Test
    public void noMembers() throws Exception {
        IndexDefinition.addIndex("indexName", "indexedExpression", "TestRegion", FUNCTIONAL);
        Mockito.doReturn(Collections.EMPTY_SET).when(command).findMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        result = gfshParser.executeCommandWithInstance(command, "create defined indexes");
        assertThat(result.getStatus()).isEqualTo(ERROR);
        assertThat(result.getMessageFromContent()).contains("No Members Found");
    }

    @Test
    public void creationFailure() throws Exception {
        DistributedMember member = Mockito.mock(DistributedMember.class);
        Mockito.when(member.getId()).thenReturn("memberId");
        InternalConfigurationPersistenceService mockService = Mockito.mock(InternalConfigurationPersistenceService.class);
        Mockito.doReturn(mockService).when(command).getConfigurationPersistenceService();
        Mockito.doReturn(Collections.singleton(member)).when(command).findMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(Arrays.asList(new CliFunctionResult(member.getId(), new Exception("MockException"), "Exception Message."))).when(resultCollector).getResult();
        IndexDefinition.addIndex("index1", "value1", "TestRegion", FUNCTIONAL);
        result = gfshParser.executeCommandWithInstance(command, "create defined indexes");
        assertThat(result.getStatus()).isEqualTo(ERROR);
    }

    @Test
    public void creationSuccess() throws Exception {
        DistributedMember member = Mockito.mock(DistributedMember.class);
        Mockito.when(member.getId()).thenReturn("memberId");
        InternalConfigurationPersistenceService mockService = Mockito.mock(InternalConfigurationPersistenceService.class);
        Mockito.doReturn(mockService).when(command).getConfigurationPersistenceService();
        Mockito.doReturn(Collections.singleton(member)).when(command).findMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        List<String> indexes = new ArrayList<>();
        indexes.add("index1");
        Mockito.doReturn(Arrays.asList(new CliFunctionResult(member.getId(), indexes))).when(resultCollector).getResult();
        IndexDefinition.addIndex("index1", "value1", "TestRegion", FUNCTIONAL);
        result = gfshParser.executeCommandWithInstance(command, "create defined indexes");
        assertThat(result.getStatus()).isEqualTo(OK);
        Mockito.verify(command, Mockito.times(0)).updateConfigForGroup(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void multipleIndexesOnMultipleRegions() throws Exception {
        DistributedMember member1 = Mockito.mock(DistributedMember.class);
        DistributedMember member2 = Mockito.mock(DistributedMember.class);
        Mockito.when(member1.getId()).thenReturn("memberId_1");
        Mockito.when(member2.getId()).thenReturn("memberId_2");
        InternalConfigurationPersistenceService mockService = Mockito.mock(InternalConfigurationPersistenceService.class);
        CliFunctionResult resultMember1 = new CliFunctionResult(member1.getId(), Arrays.asList("index1", "index2"));
        CliFunctionResult resultMember2 = new CliFunctionResult(member2.getId(), Arrays.asList("index1", "index2"));
        Mockito.doReturn(mockService).when(command).getConfigurationPersistenceService();
        Mockito.doReturn(new java.util.HashSet(Arrays.asList(member1, member2))).when(command).findMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(Arrays.asList(resultMember1, resultMember2)).when(resultCollector).getResult();
        IndexDefinition.addIndex("index1", "value1", "TestRegion1", FUNCTIONAL);
        IndexDefinition.addIndex("index2", "value2", "TestRegion2", FUNCTIONAL);
        result = gfshParser.executeCommandWithInstance(command, "create defined indexes");
        assertThat(result.getStatus()).isEqualTo(OK);
        Map<String, List<String>> table = result.getMapFromTableContent(CREATE_DEFINED_INDEXES_SECTION);
        assertThat(table.get("Status")).contains("OK", "OK", "OK", "OK");
    }
}

