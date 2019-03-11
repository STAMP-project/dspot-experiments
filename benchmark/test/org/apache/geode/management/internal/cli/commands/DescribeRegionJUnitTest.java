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


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.geode.management.internal.cli.GfshParseResult;
import org.apache.geode.management.internal.cli.domain.RegionDescriptionPerMember;
import org.apache.geode.test.junit.assertions.CommandResultAssert;
import org.apache.geode.test.junit.rules.GfshParserRule;
import org.junit.ClassRule;
import org.junit.Test;


public class DescribeRegionJUnitTest {
    @ClassRule
    public static GfshParserRule gfsh = new GfshParserRule();

    private DescribeRegionCommand command;

    private static String COMMAND = "describe region";

    private List<RegionDescriptionPerMember> functionResults;

    private static final String regionName = "testRegion";

    @Test
    public void nameIsMandatory() throws Exception {
        DescribeRegionJUnitTest.gfsh.executeAndAssertThat(command, DescribeRegionJUnitTest.COMMAND).statusIsError().containsOutput("Invalid command");
    }

    @Test
    public void regionPathConverted() throws Exception {
        GfshParseResult parseResult = DescribeRegionJUnitTest.gfsh.parse(((DescribeRegionJUnitTest.COMMAND) + " --name=test"));
        assertThat(parseResult.getParamValueAsString("name")).isEqualTo("/test");
    }

    @Test
    public void gettingDescriptionFromOneMember() throws Exception {
        Map<String, String> evictionAttr = new HashMap<>();
        Map<String, String> partitionAttr = new HashMap<>();
        Map<String, String> regionAttr = new HashMap<>();
        evictionAttr.put("evictKey", "evictVal");
        partitionAttr.put("partKey", "partVal");
        regionAttr.put("regKey", "regVal");
        RegionDescriptionPerMember descriptionPerMember = createRegionDescriptionPerMember("mockA", evictionAttr, partitionAttr, regionAttr);
        functionResults.add(descriptionPerMember);
        CommandResultAssert commandAssert = DescribeRegionJUnitTest.gfsh.executeAndAssertThat(command, (((DescribeRegionJUnitTest.COMMAND) + " --name=") + (DescribeRegionJUnitTest.regionName))).statusIsSuccess().doesNotContainOutput("Non-Default Attributes Specific To");
        Map<String, List<String>> shared = commandAssert.getCommandResult().getMapFromTableContent("0", "0");
        Map<String, List<String>> memberSpecific = commandAssert.getCommandResult().getMapFromTableContent("0", "1");
        assertThat(shared.get("Name")).contains("regKey", "evictKey", "partKey");
        assertThat(shared.get("Value")).contains("regVal", "evictVal", "partVal");
        assertThat(memberSpecific).isEmpty();
    }

    @Test
    public void gettingDescriptionFromTwoIdenticalMembers() throws Exception {
        Map<String, String> evictionAttr = new HashMap<>();
        Map<String, String> partitionAttr = new HashMap<>();
        Map<String, String> regionAttr = new HashMap<>();
        evictionAttr.put("evictKey", "evictVal");
        partitionAttr.put("partKey", "partVal");
        regionAttr.put("regKey", "regVal");
        RegionDescriptionPerMember descriptionPerMemberA = createRegionDescriptionPerMember("mockA", evictionAttr, partitionAttr, regionAttr);
        RegionDescriptionPerMember descriptionPerMemberB = createRegionDescriptionPerMember("mockB", evictionAttr, partitionAttr, regionAttr);
        functionResults.add(descriptionPerMemberA);
        functionResults.add(descriptionPerMemberB);
        CommandResultAssert commandAssert = DescribeRegionJUnitTest.gfsh.executeAndAssertThat(command, (((DescribeRegionJUnitTest.COMMAND) + " --name=") + (DescribeRegionJUnitTest.regionName))).statusIsSuccess().doesNotContainOutput("Non-Default Attributes Specific To");
        Map<String, List<String>> shared = commandAssert.getCommandResult().getMapFromTableContent("0", "0");
        Map<String, List<String>> memberSpecific = commandAssert.getCommandResult().getMapFromTableContent("0", "1");
        assertThat(shared.get("Name")).contains("regKey", "evictKey", "partKey");
        assertThat(shared.get("Value")).contains("regVal", "evictVal", "partVal");
        assertThat(memberSpecific).isEmpty();
    }

    @Test
    public void gettingDescriptionFromTwoDifferentMembers() throws Exception {
        Map<String, String> evictionAttrA = new HashMap<>();
        Map<String, String> partitionAttrA = new HashMap<>();
        Map<String, String> regionAttrA = new HashMap<>();
        evictionAttrA.put("sharedEvictionKey", "sharedEvictionValue");
        partitionAttrA.put("sharedPartitionKey", "uniquePartitionValue_A");
        regionAttrA.put("uniqueRegionKey_A", "uniqueRegionValue_A");
        Map<String, String> evictionAttrB = new HashMap<>();
        Map<String, String> partitionAttrB = new HashMap<>();
        Map<String, String> regionAttrB = new HashMap<>();
        evictionAttrB.put("sharedEvictionKey", "sharedEvictionValue");
        partitionAttrB.put("sharedPartitionKey", "uniquePartitionValue_B");
        regionAttrB.put("uniqueRegionKey_B", "uniqueRegionValue_B");
        RegionDescriptionPerMember descriptionPerMemberA = createRegionDescriptionPerMember("mockA", evictionAttrA, partitionAttrA, regionAttrA);
        RegionDescriptionPerMember descriptionPerMemberB = createRegionDescriptionPerMember("mockB", evictionAttrB, partitionAttrB, regionAttrB);
        functionResults.add(descriptionPerMemberA);
        functionResults.add(descriptionPerMemberB);
        CommandResultAssert commandAssert = DescribeRegionJUnitTest.gfsh.executeAndAssertThat(command, (((DescribeRegionJUnitTest.COMMAND) + " --name=") + (DescribeRegionJUnitTest.regionName))).statusIsSuccess();
        Map<String, List<String>> shared = commandAssert.getCommandResult().getMapFromTableContent("0", "0");
        Map<String, List<String>> memberSpecific = commandAssert.getCommandResult().getMapFromTableContent("0", "1");
        assertThat(shared.get("Type")).containsExactly("Eviction");
        assertThat(shared.get("Name")).containsExactly("sharedEvictionKey");
        assertThat(shared.get("Value")).containsExactly("sharedEvictionValue");
        assertThat(memberSpecific.get("Member")).containsExactly("mockA", "", "mockB", "");
        assertThat(memberSpecific.get("Type")).containsExactly("Region", "Partition", "Region", "Partition");
        assertThat(memberSpecific.get("Name")).containsExactly("uniqueRegionKey_A", "sharedPartitionKey", "uniqueRegionKey_B", "sharedPartitionKey");
        assertThat(memberSpecific.get("Value")).containsExactly("uniqueRegionValue_A", "uniquePartitionValue_A", "uniqueRegionValue_B", "uniquePartitionValue_B");
    }
}

