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
package org.apache.hadoop.yarn.server.resourcemanager.placement;


import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.resourcemanager.placement.UserGroupMappingPlacementRule.QueueMapping.MappingType;
import org.junit.Test;


public class TestUserGroupMappingPlacementRule {
    YarnConfiguration conf = new YarnConfiguration();

    @Test
    public void testMapping() throws YarnException {
        // simple base case for mapping user to queue
        verifyQueueMapping(new org.apache.hadoop.yarn.server.resourcemanager.placement.UserGroupMappingPlacementRule.QueueMapping(MappingType.USER, "a", "q1"), "a", "q1");
        verifyQueueMapping(new org.apache.hadoop.yarn.server.resourcemanager.placement.UserGroupMappingPlacementRule.QueueMapping(MappingType.GROUP, "agroup", "q1"), "a", "q1");
        verifyQueueMapping(new org.apache.hadoop.yarn.server.resourcemanager.placement.UserGroupMappingPlacementRule.QueueMapping(MappingType.USER, "%user", "q2"), "a", "q2");
        verifyQueueMapping(new org.apache.hadoop.yarn.server.resourcemanager.placement.UserGroupMappingPlacementRule.QueueMapping(MappingType.USER, "%user", "%user"), "a", "a");
        verifyQueueMapping(new org.apache.hadoop.yarn.server.resourcemanager.placement.UserGroupMappingPlacementRule.QueueMapping(MappingType.USER, "%user", "%primary_group"), "a", "agroup");
        verifyQueueMapping(new org.apache.hadoop.yarn.server.resourcemanager.placement.UserGroupMappingPlacementRule.QueueMapping(MappingType.GROUP, "asubgroup1", "q1"), "a", "q1");
        // specify overwritten, and see if user specified a queue, and it will be
        // overridden
        verifyQueueMapping(new org.apache.hadoop.yarn.server.resourcemanager.placement.UserGroupMappingPlacementRule.QueueMapping(MappingType.USER, "user", "q1"), "user", "q2", "q1", true);
        // if overwritten not specified, it should be which user specified
        verifyQueueMapping(new org.apache.hadoop.yarn.server.resourcemanager.placement.UserGroupMappingPlacementRule.QueueMapping(MappingType.USER, "user", "q1"), "user", "q2", "q2", false);
        // if overwritten not specified, it should be which user specified
        verifyQueueMapping(new org.apache.hadoop.yarn.server.resourcemanager.placement.UserGroupMappingPlacementRule.QueueMapping(MappingType.GROUP, "usergroup", "%user", "usergroup"), "user", "default", "user", false);
        // if overwritten not specified, it should be which user specified
        verifyQueueMapping(new org.apache.hadoop.yarn.server.resourcemanager.placement.UserGroupMappingPlacementRule.QueueMapping(MappingType.GROUP, "usergroup", "%user", "usergroup"), "user", "agroup", "user", true);
        // If user specific queue is enabled for a specified group under a given
        // parent queue
        verifyQueueMapping(new org.apache.hadoop.yarn.server.resourcemanager.placement.UserGroupMappingPlacementRule.QueueMapping(MappingType.GROUP, "agroup", "%user", "parent1"), "a", "a");
        // If user specific queue is enabled for a specified group without parent
        // queue
        verifyQueueMapping(new org.apache.hadoop.yarn.server.resourcemanager.placement.UserGroupMappingPlacementRule.QueueMapping(MappingType.GROUP, "agroup", "%user"), "a", "a");
    }
}

