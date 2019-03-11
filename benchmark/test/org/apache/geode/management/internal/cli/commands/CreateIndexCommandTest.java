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


import RegionConfig.Index;
import java.util.Collections;
import org.apache.geode.cache.configuration.CacheConfig;
import org.apache.geode.cache.configuration.RegionConfig;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.management.internal.cli.result.CommandResult;
import org.apache.geode.test.junit.rules.GfshParserRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CreateIndexCommandTest {
    @Rule
    public GfshParserRule gfshParser = new GfshParserRule();

    private CreateIndexCommand command;

    private CommandResult result;

    private ResultCollector rc;

    @Test
    public void missingName() throws Exception {
        gfshParser.executeAndAssertThat(command, "create index --expression=abc --region=abc").statusIsError().containsOutput("Invalid command");
    }

    @Test
    public void missingExpression() throws Exception {
        result = gfshParser.executeCommandWithInstance(command, "create index --name=abc --region=abc");
        assertThat(result.getStatus()).isEqualTo(ERROR);
        assertThat(result.getMessageFromContent()).contains("Invalid command");
    }

    @Test
    public void missingRegion() throws Exception {
        result = gfshParser.executeCommandWithInstance(command, "create index --name=abc --expression=abc");
        assertThat(result.getStatus()).isEqualTo(ERROR);
        assertThat(result.getMessageFromContent()).contains("Invalid command");
    }

    @Test
    public void invalidIndexType() throws Exception {
        result = gfshParser.executeCommandWithInstance(command, "create index --name=abc --expression=abc --region=abc --type=abc");
        assertThat(result.getStatus()).isEqualTo(ERROR);
        assertThat(result.getMessageFromContent()).contains("Invalid command");
    }

    @Test
    public void validIndexType() throws Exception {
        Mockito.doReturn(Collections.EMPTY_SET).when(command).findMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        result = gfshParser.executeCommandWithInstance(command, "create index --name=abc --expression=abc --region=abc --type=range");
        assertThat(result.getStatus()).isEqualTo(ERROR);
        assertThat(result.getMessageFromContent()).contains("No Members Found");
    }

    @Test
    public void validIndexType2() throws Exception {
        Mockito.doReturn(Collections.EMPTY_SET).when(command).findMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        result = gfshParser.executeCommandWithInstance(command, "create index --name=abc --expression=abc --region=abc --type=hash");
        assertThat(result.getStatus()).isEqualTo(ERROR);
        assertThat(result.getMessageFromContent()).contains("No Members Found");
    }

    @Test
    public void noMemberFound() throws Exception {
        Mockito.doReturn(Collections.EMPTY_SET).when(command).findMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        result = gfshParser.executeCommandWithInstance(command, "create index --name=abc --expression=abc --region=abc");
        assertThat(result.getStatus()).isEqualTo(ERROR);
        assertThat(result.getMessageFromContent()).contains("No Members Found");
    }

    @Test
    public void defaultInexType() throws Exception {
        DistributedMember member = Mockito.mock(DistributedMember.class);
        Mockito.doReturn(Collections.singleton(member)).when(command).findMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        ArgumentCaptor<RegionConfig.Index> indexTypeCaptor = ArgumentCaptor.forClass(Index.class);
        gfshParser.executeAndAssertThat(command, "create index --name=abc --expression=abc --region=abc");
        Mockito.verify(command).executeAndGetFunctionResult(ArgumentMatchers.any(), indexTypeCaptor.capture(), ArgumentMatchers.eq(Collections.singleton(member)));
        assertThat(indexTypeCaptor.getValue().getType()).isEqualTo("range");
    }

    @Test
    public void getValidRegionName() {
        CacheConfig cacheConfig = Mockito.mock(CacheConfig.class);
        RegionConfig region = new RegionConfig("regionA.regionB", "REPLICATE");
        Mockito.when(cacheConfig.findRegionConfiguration("/regionA.regionB")).thenReturn(region);
        assertThat(command.getValidRegionName("regionB", cacheConfig)).isEqualTo("regionB");
        assertThat(command.getValidRegionName("/regionB", cacheConfig)).isEqualTo("/regionB");
        assertThat(command.getValidRegionName("/regionB b", cacheConfig)).isEqualTo("/regionB");
        assertThat(command.getValidRegionName("/regionB.entrySet()", cacheConfig)).isEqualTo("/regionB");
        assertThat(command.getValidRegionName("/regionA.regionB.entrySet() A", cacheConfig)).isEqualTo("/regionA.regionB");
        assertThat(command.getValidRegionName("/regionB.regionA.entrySet() B", cacheConfig)).isEqualTo("/regionB");
    }
}

