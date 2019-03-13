/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.api.services.stackadvisor.recommendations;


import RecommendationResponse.HostGroup;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;

import static RecommendationResponse.HostGroup;


public class RecommendationResponseTest {
    private final RecommendationResponse response = new RecommendationResponse();

    @Test
    public void blueprint_getHostgroupComponentMap() {
        ImmutableMap<String, Set<String>> expected = ImmutableMap.of("host_group_1", ImmutableSet.of("NAMENODE", "ZOOKEEPER_SERVER"), "host_group_2", ImmutableSet.of("DATANODE", "HDFS_CLIENT", "ZOOKEEPER_CLIENT"));
        Assert.assertEquals(expected, response.getRecommendations().getBlueprint().getHostgroupComponentMap());
    }

    @Test
    public void hostgGroup_getComponentNames() {
        Map<String, RecommendationResponse.HostGroup> hostGroups = response.getRecommendations().getBlueprint().getHostGroups().stream().collect(Collectors.toMap(HostGroup::getName, Function.identity()));
        Assert.assertEquals(ImmutableSet.of("NAMENODE", "ZOOKEEPER_SERVER"), hostGroups.get("host_group_1").getComponentNames());
        Assert.assertEquals(ImmutableSet.of("DATANODE", "HDFS_CLIENT", "ZOOKEEPER_CLIENT"), hostGroups.get("host_group_2").getComponentNames());
    }

    @Test
    public void blueprintClusterBinding_getHostgroupHostMap() {
        ImmutableMap<String, Set<String>> expected = ImmutableMap.of("host_group_1", ImmutableSet.of("c7401", "c7402"), "host_group_2", ImmutableSet.of("c7403", "c7404", "c7405"));
        Assert.assertEquals(expected, response.getRecommendations().getBlueprintClusterBinding().getHostgroupHostMap());
    }
}

