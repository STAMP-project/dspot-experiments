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
package org.apache.zookeeper.server;


import CreateMode.PERSISTENT_WITH_TTL;
import EphemeralType.CONTAINER;
import EphemeralType.CONTAINER_EPHEMERAL_OWNER;
import EphemeralType.TTL;
import ZooDefs.Ids.OPEN_ACL_UNSAFE;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.TestableZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.test.ClientBase;
import org.apache.zookeeper.test.ServerCnxnFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class Emulate353TTLTest extends ClientBase {
    private TestableZooKeeper zk;

    @Test
    public void testCreate() throws InterruptedException, KeeperException {
        Stat stat = new Stat();
        zk.create("/foo", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT_WITH_TTL, stat, 100);
        Assert.assertEquals(0, stat.getEphemeralOwner());
        final AtomicLong fakeElapsed = new AtomicLong(0);
        ContainerManager containerManager = newContainerManager(fakeElapsed);
        containerManager.checkContainers();
        Assert.assertNotNull("Ttl node should not have been deleted yet", exists("/foo", false));
        fakeElapsed.set(1000);
        containerManager.checkContainers();
        Assert.assertNull("Ttl node should have been deleted", exists("/foo", false));
    }

    @Test
    public void test353TTL() throws InterruptedException, KeeperException {
        DataTree dataTree = serverFactory.zkServer.getZKDatabase().dataTree;
        long ephemeralOwner = EphemeralTypeEmulate353.ttlToEphemeralOwner(100);
        dataTree.createNode("/foo", new byte[0], OPEN_ACL_UNSAFE, ephemeralOwner, ((dataTree.getNode("/").stat.getCversion()) + 1), 1, 1);
        final AtomicLong fakeElapsed = new AtomicLong(0);
        ContainerManager containerManager = newContainerManager(fakeElapsed);
        containerManager.checkContainers();
        Assert.assertNotNull("Ttl node should not have been deleted yet", exists("/foo", false));
        fakeElapsed.set(1000);
        containerManager.checkContainers();
        Assert.assertNull("Ttl node should have been deleted", exists("/foo", false));
    }

    @Test
    public void testEphemeralOwner_emulationTTL() {
        Assert.assertThat(EphemeralType.get((-1)), CoreMatchers.equalTo(TTL));
    }

    @Test
    public void testEphemeralOwner_emulationContainer() {
        Assert.assertThat(EphemeralType.get(CONTAINER_EPHEMERAL_OWNER), CoreMatchers.equalTo(CONTAINER));
    }
}

