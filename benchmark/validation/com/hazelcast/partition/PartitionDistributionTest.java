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
package com.hazelcast.partition;


import com.hazelcast.config.Config;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.SlowTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(SlowTest.class)
public class PartitionDistributionTest extends HazelcastTestSupport {
    private Config hostAwareConfig = new Config();

    private Config hostAwareLiteMemberConfig = new Config().setLiteMember(true);

    @Test
    public void testTwoNodes_defaultPartitions() {
        testPartitionDistribution(271, 2, 0);
    }

    @Test
    public void testTwoNodes_withTwoLiteNodes_defaultPartitions() {
        testPartitionDistribution(271, 2, 2);
    }

    @Test
    public void testTwoNodes_1111Partitions() {
        testPartitionDistribution(1111, 2, 0);
    }

    @Test
    public void testTwoNodes_withTwoLiteNodes_1111Partitions() {
        testPartitionDistribution(1111, 2, 2);
    }

    @Test
    public void testTwoNodes_defaultPartitions_HostAware() {
        testPartitionDistribution(271, 2, 0, hostAwareConfig, hostAwareLiteMemberConfig);
    }

    @Test
    public void testTwoNodes_withTwoLiteNodes_defaultPartitions_HostAware() {
        testPartitionDistribution(271, 2, 2, hostAwareConfig, hostAwareLiteMemberConfig);
    }

    @Test
    public void testThreeNodes_defaultPartitions() {
        testPartitionDistribution(271, 3, 0);
    }

    @Test(expected = AssertionError.class)
    public void testThreeNodes_defaultPartitions_HostAware() {
        testPartitionDistribution(271, 3, 0, hostAwareConfig, hostAwareLiteMemberConfig);
    }

    @Test
    public void testFourNodes_defaultPartitions_HostAware() {
        testPartitionDistribution(271, 4, 0, hostAwareConfig, hostAwareLiteMemberConfig);
    }

    @Test
    public void testFiveNodes_defaultPartitions() {
        testPartitionDistribution(271, 5, 0);
    }

    @Test
    public void testFiveNodes_1111Partitions() {
        testPartitionDistribution(1111, 5, 0);
    }

    @Test(expected = AssertionError.class)
    public void testFiveNodes_defaultPartitions_HostAware() {
        testPartitionDistribution(271, 5, 0, hostAwareConfig, hostAwareLiteMemberConfig);
    }

    @Test
    public void testTenNodes_defaultPartitions() {
        testPartitionDistribution(271, 10, 0);
    }

    @Test
    public void testTenNodes_1111Partitions() {
        testPartitionDistribution(1111, 10, 0);
    }

    @Test
    public void testTenNodes_defaultPartitions_HostAware() {
        testPartitionDistribution(271, 10, 0, hostAwareConfig, hostAwareLiteMemberConfig);
    }

    @Test(expected = AssertionError.class)
    public void testFifteenNodes_defaultPartitions_HostAware() {
        testPartitionDistribution(271, 15, 0, hostAwareConfig, hostAwareLiteMemberConfig);
    }
}

