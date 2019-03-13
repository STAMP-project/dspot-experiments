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
package com.hazelcast.cluster;


import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class LiteMemberJoinTest {
    private final String name = HazelcastTestSupport.randomString();

    private final String pw = HazelcastTestSupport.randomString();

    @Test
    public void test_liteMemberIsCreated() {
        final Config liteConfig = new Config().setLiteMember(true);
        final HazelcastInstance liteInstance = Hazelcast.newHazelcastInstance(liteConfig);
        Assert.assertTrue(HazelcastTestSupport.getNode(liteInstance).isLiteMember());
        final Member liteMember = liteInstance.getCluster().getLocalMember();
        Assert.assertTrue(liteMember.isLiteMember());
    }

    @Test
    public void test_liteMemberBecomesMaster_tcp() {
        test_liteMemberBecomesMaster(LiteMemberJoinTest.ConfigCreator.TCP_CONFIG_CREATOR);
    }

    @Test
    public void test_liteMemberBecomesMaster_multicast() {
        test_liteMemberBecomesMaster(LiteMemberJoinTest.ConfigCreator.MULTICAST_CONFIG_CREATOR);
    }

    @Test
    public void test_liteMemberJoinsToCluster_tcp() {
        test_liteMemberJoinsToCluster(LiteMemberJoinTest.ConfigCreator.TCP_CONFIG_CREATOR);
    }

    @Test
    public void test_liteMemberJoinsToCluster_multicast() {
        test_liteMemberJoinsToCluster(LiteMemberJoinTest.ConfigCreator.MULTICAST_CONFIG_CREATOR);
    }

    @Test
    public void test_liteMemberBecomesVisibleTo2ndNode_tcp() {
        test_liteMemberBecomesVisibleTo2ndNode(LiteMemberJoinTest.ConfigCreator.TCP_CONFIG_CREATOR);
    }

    @Test
    public void test_liteMemberBecomesVisibleTo2ndNode_multicast() {
        test_liteMemberBecomesVisibleTo2ndNode(LiteMemberJoinTest.ConfigCreator.MULTICAST_CONFIG_CREATOR);
    }

    @Test
    public void test_liteMemberBecomesVisibleTo3rdNode_tcp() {
        test_liteMemberBecomesVisibleTo3rdNode(LiteMemberJoinTest.ConfigCreator.TCP_CONFIG_CREATOR);
    }

    @Test
    public void test_liteMemberBecomesVisibleTo3rdNode_multicast() {
        test_liteMemberBecomesVisibleTo3rdNode(LiteMemberJoinTest.ConfigCreator.MULTICAST_CONFIG_CREATOR);
    }

    @Test
    public void test_liteMemberReconnects_tcp() {
        test_liteMemberReconnects(LiteMemberJoinTest.ConfigCreator.TCP_CONFIG_CREATOR);
    }

    @Test
    public void test_liteMemberReconnects_multicast() {
        test_liteMemberReconnects(LiteMemberJoinTest.ConfigCreator.MULTICAST_CONFIG_CREATOR);
    }

    private enum ConfigCreator {

        TCP_CONFIG_CREATOR() {
            @Override
            public Config create(String name, String pw, boolean liteMember) {
                Config config = new Config();
                config.getGroupConfig().setName(name);
                config.getGroupConfig().setPassword(pw);
                config.setLiteMember(liteMember);
                NetworkConfig networkConfig = config.getNetworkConfig();
                JoinConfig join = networkConfig.getJoin();
                join.getMulticastConfig().setEnabled(false);
                TcpIpConfig tcpIpConfig = join.getTcpIpConfig();
                tcpIpConfig.setEnabled(true);
                tcpIpConfig.addMember("127.0.0.1");
                return config;
            }
        },
        MULTICAST_CONFIG_CREATOR() {
            @Override
            public Config create(String name, String pw, boolean liteMember) {
                Config config = new Config();
                config.getGroupConfig().setName(name);
                config.getGroupConfig().setPassword(pw);
                config.setLiteMember(liteMember);
                NetworkConfig networkConfig = config.getNetworkConfig();
                JoinConfig join = networkConfig.getJoin();
                join.getTcpIpConfig().setEnabled(false);
                join.getMulticastConfig().setEnabled(true);
                return config;
            }
        };
        public abstract Config create(String name, String pw, boolean liteMember);
    }
}

