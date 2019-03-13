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
import org.junit.Test;


public class TestAppNameMappingPlacementRule {
    private static final String APP_NAME = "DistributedShell";

    private YarnConfiguration conf = new YarnConfiguration();

    @Test
    public void testMapping() throws YarnException {
        // simple base case for mapping user to queue
        verifyQueueMapping(new QueueMappingEntity(TestAppNameMappingPlacementRule.APP_NAME, "q1"), "user_1", "q1");
        verifyQueueMapping(new QueueMappingEntity("%application", "q2"), "user_1", "q2");
        verifyQueueMapping(new QueueMappingEntity("%application", "%application"), "user_1", TestAppNameMappingPlacementRule.APP_NAME);
        // specify overwritten, and see if user specified a queue, and it will be
        // overridden
        verifyQueueMapping(new QueueMappingEntity(TestAppNameMappingPlacementRule.APP_NAME, "q1"), "1", "q2", "q1", true);
        // if overwritten not specified, it should be which user specified
        verifyQueueMapping(new QueueMappingEntity(TestAppNameMappingPlacementRule.APP_NAME, "q1"), "1", "q2", "q2", false);
    }
}

