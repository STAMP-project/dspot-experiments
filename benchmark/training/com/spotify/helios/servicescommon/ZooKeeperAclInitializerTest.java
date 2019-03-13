/**
 * -
 * -\-\-
 * Helios Services
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios.servicescommon;


import ZooDefs.Ids.OPEN_ACL_UNSAFE;
import com.google.common.collect.Lists;
import com.spotify.helios.ZooKeeperTestManager;
import com.spotify.helios.master.MasterZooKeeperRegistrar;
import com.spotify.helios.servicescommon.coordination.Paths;
import com.spotify.helios.servicescommon.coordination.ZooKeeperClient;
import java.util.List;
import org.apache.curator.framework.AuthInfo;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.zookeeper.data.ACL;
import org.junit.Assert;
import org.junit.Test;


public class ZooKeeperAclInitializerTest {
    private static final String AGENT_USER = "agent-user";

    private static final String AGENT_PASSWORD = "agent-pass";

    private static final String MASTER_USER = "master-user";

    private static final String MASTER_PASSWORD = "master-pass";

    private static final List<AuthInfo> MASTER_AUTH = Lists.newArrayList(new AuthInfo("digest", String.format("%s:%s", ZooKeeperAclInitializerTest.MASTER_USER, ZooKeeperAclInitializerTest.MASTER_PASSWORD).getBytes()));

    private static final String CLUSTER_ID = "helios";

    private ZooKeeperTestManager zk;

    private ACLProvider aclProvider;

    @Test
    public void testInitializeAcl() throws Exception {
        // setup the initial helios tree
        final ZooKeeperClient zkClient = new com.spotify.helios.servicescommon.coordination.DefaultZooKeeperClient(zk.curatorWithSuperAuth());
        zkClient.ensurePath(Paths.configId(ZooKeeperAclInitializerTest.CLUSTER_ID));
        new MasterZooKeeperRegistrar("helios-master").tryToRegister(zkClient);
        // to start with, nothing should have permissions
        for (final String path : zkClient.listRecursive("/")) {
            Assert.assertEquals(OPEN_ACL_UNSAFE, zkClient.getAcl(path));
        }
        // initialize ACL's
        ZooKeeperAclInitializer.initializeAcl(zk.connectString(), ZooKeeperAclInitializerTest.CLUSTER_ID, ZooKeeperAclInitializerTest.MASTER_USER, ZooKeeperAclInitializerTest.MASTER_PASSWORD, ZooKeeperAclInitializerTest.AGENT_USER, ZooKeeperAclInitializerTest.AGENT_PASSWORD);
        for (final String path : zkClient.listRecursive("/")) {
            final List<ACL> expected = aclProvider.getAclForPath(path);
            final List<ACL> actual = zkClient.getAcl(path);
            Assert.assertEquals(expected.size(), actual.size());
            Assert.assertTrue(expected.containsAll(actual));
        }
    }
}

