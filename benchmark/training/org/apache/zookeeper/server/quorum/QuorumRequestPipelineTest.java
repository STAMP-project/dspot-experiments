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
package org.apache.zookeeper.server.quorum;


import CreateMode.PERSISTENT;
import Ids.OPEN_ACL_UNSAFE;
import Ids.READ_ACL_UNSAFE;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.zookeeper.AsyncCallback.VoidCallback;
import org.apache.zookeeper.TestableZooKeeper;
import org.apache.zookeeper.ZKParameterized;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.quorum.QuorumPeer.ServerState;
import org.apache.zookeeper.test.QuorumBase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(ZKParameterized.RunnerFactory.class)
public class QuorumRequestPipelineTest extends QuorumBase {
    protected ServerState serverState;

    protected final CountDownLatch callComplete = new CountDownLatch(1);

    protected boolean complete = false;

    protected static final String PARENT_PATH = "/foo";

    protected static final Set<String> CHILDREN = new HashSet<String>(Arrays.asList("1", "2", "3"));

    protected static final String AUTH_PROVIDER = "digest";

    protected static final byte[] AUTH = "hello".getBytes();

    protected static final byte[] DATA = "Hint Water".getBytes();

    protected TestableZooKeeper zkClient;

    public QuorumRequestPipelineTest(ServerState state) {
        this.serverState = state;
    }

    @Test
    public void testCreate() throws Exception {
        zkClient.create(QuorumRequestPipelineTest.PARENT_PATH, QuorumRequestPipelineTest.DATA, OPEN_ACL_UNSAFE, PERSISTENT);
        Assert.assertArrayEquals(String.format("%s Node created (create) with expected value", serverState), QuorumRequestPipelineTest.DATA, getData(QuorumRequestPipelineTest.PARENT_PATH, false, null));
    }

    @Test
    public void testCreate2() throws Exception {
        zkClient.create(QuorumRequestPipelineTest.PARENT_PATH, QuorumRequestPipelineTest.DATA, OPEN_ACL_UNSAFE, PERSISTENT, null);
        Assert.assertArrayEquals(String.format("%s Node created (create2) with expected value", serverState), QuorumRequestPipelineTest.DATA, getData(QuorumRequestPipelineTest.PARENT_PATH, false, null));
    }

    @Test
    public void testDelete() throws Exception {
        create2EmptyNode(zkClient, QuorumRequestPipelineTest.PARENT_PATH);
        delete(QuorumRequestPipelineTest.PARENT_PATH, (-1));
        Assert.assertNull(String.format("%s Node no longer exists", serverState), exists(QuorumRequestPipelineTest.PARENT_PATH, false));
    }

    @Test
    public void testExists() throws Exception {
        Stat stat = create2EmptyNode(zkClient, QuorumRequestPipelineTest.PARENT_PATH);
        Assert.assertEquals(String.format("%s Exists returns correct node stat", serverState), stat, exists(QuorumRequestPipelineTest.PARENT_PATH, false));
    }

    @Test
    public void testSetAndGetData() throws Exception {
        create2EmptyNode(zkClient, QuorumRequestPipelineTest.PARENT_PATH);
        setData(QuorumRequestPipelineTest.PARENT_PATH, QuorumRequestPipelineTest.DATA, (-1));
        Assert.assertArrayEquals(String.format("%s Node updated with expected value", serverState), QuorumRequestPipelineTest.DATA, getData(QuorumRequestPipelineTest.PARENT_PATH, false, null));
    }

    @Test
    public void testSetAndGetACL() throws Exception {
        create2EmptyNode(zkClient, QuorumRequestPipelineTest.PARENT_PATH);
        Assert.assertEquals(String.format("%s Node has open ACL", serverState), OPEN_ACL_UNSAFE, zkClient.getACL(QuorumRequestPipelineTest.PARENT_PATH, new Stat()));
        zkClient.setACL(QuorumRequestPipelineTest.PARENT_PATH, READ_ACL_UNSAFE, (-1));
        Assert.assertEquals(String.format("%s Node has world read-only ACL", serverState), READ_ACL_UNSAFE, zkClient.getACL(QuorumRequestPipelineTest.PARENT_PATH, new Stat()));
    }

    @Test
    public void testSetAndGetChildren() throws Exception {
        create2EmptyNode(zkClient, QuorumRequestPipelineTest.PARENT_PATH);
        for (String child : QuorumRequestPipelineTest.CHILDREN) {
            create2EmptyNode(zkClient, (((QuorumRequestPipelineTest.PARENT_PATH) + "/") + child));
        }
        Assert.assertEquals(String.format("%s Parent has expected children", serverState), QuorumRequestPipelineTest.CHILDREN, new HashSet<String>(zkClient.getChildren(QuorumRequestPipelineTest.PARENT_PATH, false)));
    }

    @Test
    public void testSetAndGetChildren2() throws Exception {
        create2EmptyNode(zkClient, QuorumRequestPipelineTest.PARENT_PATH);
        for (String child : QuorumRequestPipelineTest.CHILDREN) {
            create2EmptyNode(zkClient, (((QuorumRequestPipelineTest.PARENT_PATH) + "/") + child));
        }
        Assert.assertEquals(String.format("%s Parent has expected children", serverState), QuorumRequestPipelineTest.CHILDREN, new HashSet<String>(getChildren(QuorumRequestPipelineTest.PARENT_PATH, false, null)));
    }

    @Test
    public void testSync() throws Exception {
        complete = false;
        create2EmptyNode(zkClient, QuorumRequestPipelineTest.PARENT_PATH);
        VoidCallback onSync = new VoidCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx) {
                complete = true;
                callComplete.countDown();
            }
        };
        zkClient.sync(QuorumRequestPipelineTest.PARENT_PATH, onSync, null);
        callComplete.await(30, TimeUnit.SECONDS);
        Assert.assertTrue(String.format("%s Sync completed", serverState), complete);
    }
}

