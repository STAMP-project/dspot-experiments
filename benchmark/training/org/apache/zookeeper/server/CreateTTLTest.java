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


import AsyncCallback.Create2Callback;
import Code.BADARGUMENTS;
import Code.OK;
import CreateMode.PERSISTENT;
import CreateMode.PERSISTENT_SEQUENTIAL_WITH_TTL;
import CreateMode.PERSISTENT_WITH_TTL;
import EphemeralType.TTL;
import KeeperException.UnimplementedException;
import ZooDefs.Ids;
import ZooDefs.Ids.OPEN_ACL_UNSAFE;
import ZooDefs.OpCode;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Op;
import org.apache.zookeeper.OpResult;
import org.apache.zookeeper.TestableZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.proto.CreateResponse;
import org.apache.zookeeper.proto.CreateTTLRequest;
import org.apache.zookeeper.proto.ReplyHeader;
import org.apache.zookeeper.proto.RequestHeader;
import org.apache.zookeeper.test.ClientBase;
import org.junit.Assert;
import org.junit.Test;


public class CreateTTLTest extends ClientBase {
    private TestableZooKeeper zk;

    private static final Collection<String> disabledTests = Collections.singleton("testDisabled");

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
    public void testBadTTLs() throws InterruptedException, KeeperException {
        RequestHeader h = new RequestHeader(1, OpCode.createTTL);
        String path = "/bad_ttl";
        CreateTTLRequest request = new CreateTTLRequest(path, new byte[0], Ids.OPEN_ACL_UNSAFE, PERSISTENT_WITH_TTL.toFlag(), (-100));
        CreateResponse response = new CreateResponse();
        ReplyHeader r = zk.submitRequest(h, request, response, null);
        Assert.assertEquals("An invalid CreateTTLRequest should throw BadArguments", r.getErr(), BADARGUMENTS.intValue());
        Assert.assertNull("An invalid CreateTTLRequest should not result in znode creation", exists(path, false));
        request = new CreateTTLRequest(path, new byte[0], Ids.OPEN_ACL_UNSAFE, PERSISTENT_WITH_TTL.toFlag(), ((TTL.maxValue()) + 1));
        response = new CreateResponse();
        r = zk.submitRequest(h, request, response, null);
        Assert.assertEquals("An invalid CreateTTLRequest should throw BadArguments", r.getErr(), BADARGUMENTS.intValue());
        Assert.assertNull("An invalid CreateTTLRequest should not result in znode creation", exists(path, false));
    }

    @Test
    public void testMaxTTLs() throws InterruptedException, KeeperException {
        RequestHeader h = new RequestHeader(1, OpCode.createTTL);
        String path = "/bad_ttl";
        CreateTTLRequest request = new CreateTTLRequest(path, new byte[0], Ids.OPEN_ACL_UNSAFE, PERSISTENT_WITH_TTL.toFlag(), TTL.maxValue());
        CreateResponse response = new CreateResponse();
        ReplyHeader r = zk.submitRequest(h, request, response, null);
        Assert.assertEquals("EphemeralType.getMaxTTL() should succeed", r.getErr(), OK.intValue());
        Assert.assertNotNull("Node should exist", exists(path, false));
    }

    @Test
    public void testCreateSequential() throws InterruptedException, KeeperException {
        Stat stat = new Stat();
        String path = zk.create("/foo", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT_SEQUENTIAL_WITH_TTL, stat, 100);
        Assert.assertEquals(0, stat.getEphemeralOwner());
        final AtomicLong fakeElapsed = new AtomicLong(0);
        ContainerManager containerManager = newContainerManager(fakeElapsed);
        containerManager.checkContainers();
        Assert.assertNotNull("Ttl node should not have been deleted yet", exists(path, false));
        fakeElapsed.set(1000);
        containerManager.checkContainers();
        Assert.assertNull("Ttl node should have been deleted", exists(path, false));
    }

    @Test
    public void testCreateAsync() throws InterruptedException, KeeperException {
        AsyncCallback.Create2Callback callback = new AsyncCallback.Create2Callback() {
            @Override
            public void processResult(int rc, String path, Object ctx, String name, Stat stat) {
                // NOP
            }
        };
        zk.create("/foo", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT_WITH_TTL, callback, null, 100);
        final AtomicLong fakeElapsed = new AtomicLong(0);
        ContainerManager containerManager = newContainerManager(fakeElapsed);
        containerManager.checkContainers();
        Assert.assertNotNull("Ttl node should not have been deleted yet", exists("/foo", false));
        fakeElapsed.set(1000);
        containerManager.checkContainers();
        Assert.assertNull("Ttl node should have been deleted", exists("/foo", false));
    }

    @Test
    public void testModifying() throws InterruptedException, KeeperException {
        Stat stat = new Stat();
        zk.create("/foo", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT_WITH_TTL, stat, 100);
        Assert.assertEquals(0, stat.getEphemeralOwner());
        final AtomicLong fakeElapsed = new AtomicLong(0);
        ContainerManager containerManager = newContainerManager(fakeElapsed);
        containerManager.checkContainers();
        Assert.assertNotNull("Ttl node should not have been deleted yet", exists("/foo", false));
        for (int i = 0; i < 10; ++i) {
            fakeElapsed.set(50);
            setData("/foo", new byte[i + 1], (-1));
            containerManager.checkContainers();
            Assert.assertNotNull("Ttl node should not have been deleted yet", exists("/foo", false));
        }
        fakeElapsed.set(200);
        containerManager.checkContainers();
        Assert.assertNull("Ttl node should have been deleted", exists("/foo", false));
    }

    @Test
    public void testMulti() throws InterruptedException, KeeperException {
        Op createTtl = Op.create("/a", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT_WITH_TTL, 100);
        Op createTtlSequential = Op.create("/b", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT_SEQUENTIAL_WITH_TTL, 200);
        Op createNonTtl = Op.create("/c", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        List<OpResult> results = zk.multi(Arrays.asList(createTtl, createTtlSequential, createNonTtl));
        String sequentialPath = getPath();
        final AtomicLong fakeElapsed = new AtomicLong(0);
        ContainerManager containerManager = newContainerManager(fakeElapsed);
        containerManager.checkContainers();
        Assert.assertNotNull("node should not have been deleted yet", exists("/a", false));
        Assert.assertNotNull("node should not have been deleted yet", exists(sequentialPath, false));
        Assert.assertNotNull("node should never be deleted", exists("/c", false));
        fakeElapsed.set(110);
        containerManager.checkContainers();
        Assert.assertNull("node should have been deleted", exists("/a", false));
        Assert.assertNotNull("node should not have been deleted yet", exists(sequentialPath, false));
        Assert.assertNotNull("node should never be deleted", exists("/c", false));
        fakeElapsed.set(210);
        containerManager.checkContainers();
        Assert.assertNull("node should have been deleted", exists("/a", false));
        Assert.assertNull("node should have been deleted", exists(sequentialPath, false));
        Assert.assertNotNull("node should never be deleted", exists("/c", false));
    }

    @Test
    public void testBadUsage() throws InterruptedException, KeeperException {
        for (CreateMode createMode : CreateMode.values()) {
            try {
                zk.create("/foo", new byte[0], OPEN_ACL_UNSAFE, createMode, new Stat(), (createMode.isTTL() ? 0 : 100));
                Assert.fail("should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException dummy) {
                // correct
            }
        }
        for (CreateMode createMode : CreateMode.values()) {
            AsyncCallback.Create2Callback callback = new AsyncCallback.Create2Callback() {
                @Override
                public void processResult(int rc, String path, Object ctx, String name, Stat stat) {
                    // NOP
                }
            };
            try {
                zk.create("/foo", new byte[0], OPEN_ACL_UNSAFE, createMode, callback, null, (createMode.isTTL() ? 0 : 100));
                Assert.fail("should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException dummy) {
                // correct
            }
        }
        try {
            Op op = Op.create("/foo", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT_WITH_TTL, 0);
            zk.multi(Collections.singleton(op));
            Assert.fail("should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException dummy) {
            // correct
        }
        try {
            Op op = Op.create("/foo", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT_SEQUENTIAL_WITH_TTL, 0);
            zk.multi(Collections.singleton(op));
            Assert.fail("should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException dummy) {
            // correct
        }
    }

    @Test(expected = UnimplementedException.class)
    public void testDisabled() throws InterruptedException, KeeperException {
        // note, setUp() enables this test based on the test name
        zk.create("/foo", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT_WITH_TTL, new Stat(), 100);
    }
}

