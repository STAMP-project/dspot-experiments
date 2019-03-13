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
package com.hazelcast.internal.networking.nio;


import GroupProperty.MAX_JOIN_SECONDS;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class AdvancedNetworkIntegrationTest extends AbstractAdvancedNetworkIntegrationTest {
    @Rule
    public ExpectedException expect = ExpectedException.none();

    @Test
    public void testCompleteMultisocketConfig() {
        Config config = createCompleteMultiSocketConfig();
        newHazelcastInstance(config);
        assertLocalPortsOpen(AbstractAdvancedNetworkIntegrationTest.MEMBER_PORT, AbstractAdvancedNetworkIntegrationTest.CLIENT_PORT, AbstractAdvancedNetworkIntegrationTest.WAN1_PORT, AbstractAdvancedNetworkIntegrationTest.WAN2_PORT, AbstractAdvancedNetworkIntegrationTest.REST_PORT, AbstractAdvancedNetworkIntegrationTest.MEMCACHE_PORT);
    }

    @Test
    public void testMembersReportAllAddresses() {
        Config config = createCompleteMultiSocketConfig();
        for (int i = 0; i < 3; i++) {
            newHazelcastInstance(config);
        }
        HazelcastTestSupport.assertClusterSizeEventually(3, instances);
        for (HazelcastInstance hz : instances) {
            Set<Member> members = hz.getCluster().getMembers();
            for (Member member : members) {
                Assert.assertEquals(6, member.getAddressMap().size());
            }
        }
    }

    @Test(expected = AssertionError.class)
    public void testLocalPortAssertionWorks() {
        assertLocalPortsOpen(AbstractAdvancedNetworkIntegrationTest.MEMBER_PORT);
    }

    @Test
    public void testConnectionToWrongPort() {
        int firstMemberPort = 6000;
        int firstClientPort = 7000;
        int secondMemberPort = 8000;
        Config config = HazelcastTestSupport.smallInstanceConfig();
        config.getAdvancedNetworkConfig().setEnabled(true);
        config.getAdvancedNetworkConfig().setMemberEndpointConfig(createServerSocketConfig(firstMemberPort)).setClientEndpointConfig(createServerSocketConfig(firstClientPort));
        JoinConfig joinConfig = config.getAdvancedNetworkConfig().getJoin();
        joinConfig.getMulticastConfig().setEnabled(false);
        joinConfig.getTcpIpConfig().setEnabled(true).addMember(("127.0.0.1:" + secondMemberPort));
        HazelcastInstance hz = newHazelcastInstance(config);
        Config other = HazelcastTestSupport.smallInstanceConfig();
        other.getAdvancedNetworkConfig().setEnabled(true);
        other.getAdvancedNetworkConfig().setMemberEndpointConfig(createServerSocketConfig(secondMemberPort));
        JoinConfig otherJoinConfig = other.getAdvancedNetworkConfig().getJoin();
        otherJoinConfig.getMulticastConfig().setEnabled(false);
        // Mis-configured to point to Client port of 1st member
        otherJoinConfig.getTcpIpConfig().setEnabled(true).addMember(("127.0.0.1:" + firstClientPort));
        other.setProperty(MAX_JOIN_SECONDS.getName(), "1");
        expect.expect(IllegalStateException.class);
        expect.expectMessage("Node failed to start!");
        HazelcastInstance hz2 = newHazelcastInstance(other);
    }
}

