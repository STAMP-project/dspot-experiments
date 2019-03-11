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
package org.apache.geode.management.internal.cli.domain;


import java.util.HashMap;
import java.util.Map;
import org.junit.Test;


public class RegionDescriptionJUnitTest {
    private static final String evictionKeyShared = "sharedEvictionKey";

    private static final String partKeyShared = "sharedPartitionKey";

    private static final String regKeyShared = "sharedRegionKey";

    private static final String evictionValueShared = "sharedEvictionValue";

    private static final String partValueShared = "sharedPartitionValue";

    private static final String regValueShared = "sharedRegionValue";

    private static final String evictionKeyA = "uniqueEvictionKey_A";

    private static final String partKeyA = "uniquePartitionKey_A";

    private static final String regKeyA = "uniqueRegionKey_A";

    private static final String evictionValueA = "uniqueEvictionValue_A";

    private static final String partValueA = "uniquePartitionValue_A";

    private static final String regValueA = "uniqueRegionValue_A";

    private static final String evictionKeyB = "uniqueEvictionKey_B";

    private static final String partKeyB = "uniquePartitionKey_B";

    private static final String regKeyB = "uniqueRegionKey_B";

    private static final String evictionValueB = "uniqueEvictionValue_B";

    private static final String partValueB = "uniquePartitionValue_B";

    private static final String regValueB = "uniqueRegionValue_B";

    public static final String regionName = "mockRegion1";

    @Test
    public void findCommonRemovesUnsharedKeys() {
        Map<String, String> commonMap = new HashMap<>();
        commonMap.put(RegionDescriptionJUnitTest.evictionKeyShared, RegionDescriptionJUnitTest.evictionValueShared);
        commonMap.put(RegionDescriptionJUnitTest.partKeyShared, RegionDescriptionJUnitTest.partValueShared);
        commonMap.put(RegionDescriptionJUnitTest.regKeyShared, RegionDescriptionJUnitTest.regValueShared);
        commonMap.put(RegionDescriptionJUnitTest.evictionKeyA, RegionDescriptionJUnitTest.evictionValueA);
        commonMap.put(RegionDescriptionJUnitTest.partKeyA, RegionDescriptionJUnitTest.partValueA);
        Map<String, String> comparisonMap = new HashMap<>();
        comparisonMap.put(RegionDescriptionJUnitTest.evictionKeyShared, RegionDescriptionJUnitTest.evictionValueShared);
        comparisonMap.put(RegionDescriptionJUnitTest.partKeyShared, RegionDescriptionJUnitTest.partValueShared);
        comparisonMap.put(RegionDescriptionJUnitTest.regKeyShared, RegionDescriptionJUnitTest.regValueShared);
        comparisonMap.put(RegionDescriptionJUnitTest.evictionKeyB, RegionDescriptionJUnitTest.evictionValueB);
        comparisonMap.put(RegionDescriptionJUnitTest.regKeyB, RegionDescriptionJUnitTest.regValueB);
        RegionDescription.findCommon(commonMap, comparisonMap);
        assertThat(commonMap).containsOnlyKeys(RegionDescriptionJUnitTest.evictionKeyShared, RegionDescriptionJUnitTest.partKeyShared, RegionDescriptionJUnitTest.regKeyShared);
    }

    @Test
    public void findCommonRemovesKeysWithDisagreeingValues() {
        Map<String, String> commonMap = new HashMap<>();
        commonMap.put(RegionDescriptionJUnitTest.evictionKeyShared, RegionDescriptionJUnitTest.evictionValueShared);
        commonMap.put(RegionDescriptionJUnitTest.partKeyShared, RegionDescriptionJUnitTest.partValueA);
        commonMap.put(RegionDescriptionJUnitTest.regKeyShared, RegionDescriptionJUnitTest.regValueA);
        Map<String, String> comparisonMap = new HashMap<>();
        comparisonMap.put(RegionDescriptionJUnitTest.evictionKeyShared, RegionDescriptionJUnitTest.evictionValueShared);
        comparisonMap.put(RegionDescriptionJUnitTest.partKeyShared, RegionDescriptionJUnitTest.partValueB);
        comparisonMap.put(RegionDescriptionJUnitTest.regKeyShared, RegionDescriptionJUnitTest.regValueB);
        RegionDescription.findCommon(commonMap, comparisonMap);
        assertThat(commonMap).containsOnlyKeys(RegionDescriptionJUnitTest.evictionKeyShared);
    }

    @Test
    public void findCommonRemovesDisagreeingKeysInvolvingNull() {
        Map<String, String> commonMap = new HashMap<>();
        commonMap.put(RegionDescriptionJUnitTest.evictionKeyShared, RegionDescriptionJUnitTest.evictionValueShared);
        commonMap.put(RegionDescriptionJUnitTest.partKeyShared, RegionDescriptionJUnitTest.partValueA);
        commonMap.put(RegionDescriptionJUnitTest.regKeyShared, null);
        Map<String, String> comparisonMap = new HashMap<>();
        comparisonMap.put(RegionDescriptionJUnitTest.evictionKeyShared, RegionDescriptionJUnitTest.evictionValueShared);
        comparisonMap.put(RegionDescriptionJUnitTest.partKeyShared, null);
        comparisonMap.put(RegionDescriptionJUnitTest.regKeyShared, RegionDescriptionJUnitTest.regValueB);
        RegionDescription.findCommon(commonMap, comparisonMap);
        assertThat(commonMap).containsOnlyKeys(RegionDescriptionJUnitTest.evictionKeyShared);
    }

    @Test
    public void singleAddDefinesDescription() {
        RegionDescriptionPerMember mockA = getMockRegionDescriptionPerMember_A();
        RegionDescription description = new RegionDescription();
        description.add(mockA);
        assertThat(description.getCndEvictionAttributes()).isEqualTo(mockA.getNonDefaultEvictionAttributes());
        assertThat(description.getCndPartitionAttributes()).isEqualTo(mockA.getNonDefaultPartitionAttributes());
        assertThat(description.getCndRegionAttributes()).isEqualTo(mockA.getNonDefaultRegionAttributes());
    }

    @Test
    public void multipleAddsMergeAsExpected() {
        RegionDescriptionPerMember mockA = getMockRegionDescriptionPerMember_A();
        RegionDescriptionPerMember mockB = getMockRegionDescriptionPerMember_B();
        RegionDescription description = new RegionDescription();
        description.add(mockA);
        description.add(mockB);
        Map<String, String> sharedEviction = new HashMap<>();
        sharedEviction.put(RegionDescriptionJUnitTest.evictionKeyShared, RegionDescriptionJUnitTest.evictionValueShared);
        Map<String, String> sharedRegion = new HashMap<>();
        sharedRegion.put(RegionDescriptionJUnitTest.regKeyShared, RegionDescriptionJUnitTest.regValueShared);
        Map<String, String> sharedPartition = new HashMap<>();
        sharedPartition.put(RegionDescriptionJUnitTest.partKeyShared, RegionDescriptionJUnitTest.partValueShared);
        assertThat(description.getCndEvictionAttributes()).isEqualTo(sharedEviction);
        assertThat(description.getCndPartitionAttributes()).isEqualTo(sharedPartition);
        assertThat(description.getCndRegionAttributes()).isEqualTo(sharedRegion);
        assertThat(description.getRegionDescriptionPerMemberMap()).containsOnlyKeys(mockA.getHostingMember(), mockB.getHostingMember()).containsEntry(mockA.getHostingMember(), mockA).containsEntry(mockB.getHostingMember(), mockB);
    }

    @Test
    public void outOfScopeAddGetsIgnored() {
        RegionDescriptionPerMember mockA = getMockRegionDescriptionPerMember_A();
        RegionDescriptionPerMember mockB = getMockRegionDescriptionPerMember_OutOfScope();
        RegionDescription description = new RegionDescription();
        description.add(mockA);
        description.add(mockB);
        assertThat(description.getCndEvictionAttributes()).isEqualTo(mockA.getNonDefaultEvictionAttributes());
        assertThat(description.getCndPartitionAttributes()).isEqualTo(mockA.getNonDefaultPartitionAttributes());
        assertThat(description.getCndRegionAttributes()).isEqualTo(mockA.getNonDefaultRegionAttributes());
    }
}

