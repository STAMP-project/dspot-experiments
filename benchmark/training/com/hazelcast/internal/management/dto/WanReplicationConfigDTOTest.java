/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.management.dto;


import com.hazelcast.config.ConfigCompatibilityChecker;
import com.hazelcast.config.WanConsumerConfig;
import com.hazelcast.config.WanPublisherConfig;
import com.hazelcast.config.WanReplicationConfig;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class WanReplicationConfigDTOTest {
    private static final ConfigCompatibilityChecker.WanReplicationConfigChecker WAN_REPLICATION_CONFIG_CHECKER = new ConfigCompatibilityChecker.WanReplicationConfigChecker();

    @Test
    public void testSerialization() {
        WanReplicationConfig expected = new WanReplicationConfig().setName("myName").setWanConsumerConfig(new WanConsumerConfig()).addWanPublisherConfig(new WanPublisherConfig().setGroupName("group1")).addWanPublisherConfig(new WanPublisherConfig().setGroupName("group2"));
        WanReplicationConfig actual = cloneThroughJson(expected);
        Assert.assertTrue(((("Expected: " + expected) + ", got:") + actual), WanReplicationConfigDTOTest.WAN_REPLICATION_CONFIG_CHECKER.check(expected, actual));
    }
}

