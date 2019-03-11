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


import CreateMode.EPHEMERAL;
import CreateMode.PERSISTENT;
import ZooDefs.Ids.OPEN_ACL_UNSAFE;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Op;
import org.apache.zookeeper.OpResult;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.server.quorum.QuorumZooKeeperServer;
import org.apache.zookeeper.server.quorum.UpgradeableSessionTracker;
import org.apache.zookeeper.test.QuorumBase;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MultiOpSessionUpgradeTest extends QuorumBase {
    protected static final Logger LOG = LoggerFactory.getLogger(MultiOpSessionUpgradeTest.class);

    @Test
    public void ephemeralCreateMultiOpTest() throws IOException, InterruptedException, KeeperException {
        final ZooKeeper zk = createClient();
        String data = "test";
        String path = "/ephemeralcreatemultiop";
        zk.create(path, new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        QuorumZooKeeperServer server = getConnectedServer(zk.getSessionId());
        Assert.assertNotNull("unable to find server interlocutor", server);
        UpgradeableSessionTracker sessionTracker = ((UpgradeableSessionTracker) (server.getSessionTracker()));
        Assert.assertFalse("session already global", sessionTracker.isGlobalSession(zk.getSessionId()));
        List<OpResult> multi = null;
        try {
            multi = zk.multi(Arrays.asList(Op.setData(path, data.getBytes(), 0), Op.create((path + "/e"), data.getBytes(), OPEN_ACL_UNSAFE, EPHEMERAL), Op.create((path + "/p"), data.getBytes(), OPEN_ACL_UNSAFE, PERSISTENT), Op.create((path + "/q"), data.getBytes(), OPEN_ACL_UNSAFE, EPHEMERAL)));
        } catch (KeeperException e) {
            // the scenario that inspired this unit test
            Assert.fail("received session expired for a session promotion in a multi-op");
        }
        Assert.assertNotNull(multi);
        Assert.assertEquals(4, multi.size());
        Assert.assertEquals(data, new String(zk.getData((path + "/e"), false, null)));
        Assert.assertEquals(data, new String(zk.getData((path + "/p"), false, null)));
        Assert.assertEquals(data, new String(zk.getData((path + "/q"), false, null)));
        Assert.assertTrue("session not promoted", sessionTracker.isGlobalSession(zk.getSessionId()));
    }

    @Test
    public void directCheckUpgradeSessionTest() throws IOException, InterruptedException, KeeperException {
        final ZooKeeper zk = createClient();
        String path = "/directcheckupgradesession";
        zk.create(path, new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        QuorumZooKeeperServer server = getConnectedServer(zk.getSessionId());
        Assert.assertNotNull("unable to find server interlocutor", server);
        Request readRequest = makeGetDataRequest(path, zk.getSessionId());
        Request createRequest = makeCreateRequest((path + "/e"), zk.getSessionId());
        Assert.assertNull("tried to upgrade on a read", server.checkUpgradeSession(readRequest));
        Assert.assertNotNull("failed to upgrade on a create", server.checkUpgradeSession(createRequest));
        Assert.assertNull("tried to upgrade after successful promotion", server.checkUpgradeSession(createRequest));
    }
}

