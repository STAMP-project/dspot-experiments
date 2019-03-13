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
package org.apache.hadoop.hbase.zookeeper;


import AsyncCallback.DataCallback;
import Code.NONODE;
import Code.OK;
import ZooKeeper.States.CONNECTED;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutionException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseZKTestingUtility;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.ZKTests;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@Category({ ZKTests.class, MediumTests.class })
public class TestReadOnlyZKClient {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestReadOnlyZKClient.class);

    private static HBaseZKTestingUtility UTIL = new HBaseZKTestingUtility();

    private static int PORT;

    private static String PATH = "/test";

    private static byte[] DATA;

    private static int CHILDREN = 5;

    private static ReadOnlyZKClient RO_ZK;

    @Test
    public void testGetAndExists() throws Exception {
        Assert.assertArrayEquals(TestReadOnlyZKClient.DATA, TestReadOnlyZKClient.RO_ZK.get(TestReadOnlyZKClient.PATH).get());
        Assert.assertEquals(TestReadOnlyZKClient.CHILDREN, TestReadOnlyZKClient.RO_ZK.exists(TestReadOnlyZKClient.PATH).get().getNumChildren());
        Assert.assertNotNull(TestReadOnlyZKClient.RO_ZK.zookeeper);
        waitForIdleConnectionClosed();
    }

    @Test
    public void testNoNode() throws InterruptedException, ExecutionException {
        String pathNotExists = (TestReadOnlyZKClient.PATH) + "_whatever";
        try {
            TestReadOnlyZKClient.RO_ZK.get(pathNotExists).get();
            Assert.fail((("should fail because of " + pathNotExists) + " does not exist"));
        } catch (ExecutionException e) {
            Assert.assertThat(e.getCause(), CoreMatchers.instanceOf(KeeperException.class));
            KeeperException ke = ((KeeperException) (e.getCause()));
            Assert.assertEquals(NONODE, ke.code());
            Assert.assertEquals(pathNotExists, ke.getPath());
        }
        // exists will not throw exception.
        Assert.assertNull(TestReadOnlyZKClient.RO_ZK.exists(pathNotExists).get());
    }

    @Test
    public void testSessionExpire() throws Exception {
        Assert.assertArrayEquals(TestReadOnlyZKClient.DATA, TestReadOnlyZKClient.RO_ZK.get(TestReadOnlyZKClient.PATH).get());
        ZooKeeper zk = TestReadOnlyZKClient.RO_ZK.zookeeper;
        long sessionId = zk.getSessionId();
        TestReadOnlyZKClient.UTIL.getZkCluster().getZooKeeperServers().get(0).closeSession(sessionId);
        // should not reach keep alive so still the same instance
        Assert.assertSame(zk, TestReadOnlyZKClient.RO_ZK.zookeeper);
        byte[] got = TestReadOnlyZKClient.RO_ZK.get(TestReadOnlyZKClient.PATH).get();
        Assert.assertArrayEquals(TestReadOnlyZKClient.DATA, got);
        Assert.assertNotNull(TestReadOnlyZKClient.RO_ZK.zookeeper);
        Assert.assertNotSame(zk, TestReadOnlyZKClient.RO_ZK.zookeeper);
        Assert.assertNotEquals(sessionId, TestReadOnlyZKClient.RO_ZK.zookeeper.getSessionId());
    }

    @Test
    public void testNotCloseZkWhenPending() throws Exception {
        ZooKeeper mockedZK = Mockito.mock(ZooKeeper.class);
        Exchanger<AsyncCallback.DataCallback> exchanger = new Exchanger<>();
        Mockito.doAnswer(( i) -> {
            exchanger.exchange(i.getArgument(2));
            return null;
        }).when(mockedZK).getData(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(DataCallback.class), ArgumentMatchers.any());
        Mockito.doAnswer(( i) -> null).when(mockedZK).close();
        Mockito.when(mockedZK.getState()).thenReturn(CONNECTED);
        TestReadOnlyZKClient.RO_ZK.zookeeper = mockedZK;
        CompletableFuture<byte[]> future = TestReadOnlyZKClient.RO_ZK.get(TestReadOnlyZKClient.PATH);
        AsyncCallback.DataCallback callback = exchanger.exchange(null);
        // 2 * keep alive time to ensure that we will not close the zk when there are pending requests
        Thread.sleep(6000);
        Assert.assertNotNull(TestReadOnlyZKClient.RO_ZK.zookeeper);
        Mockito.verify(mockedZK, Mockito.never()).close();
        callback.processResult(OK.intValue(), TestReadOnlyZKClient.PATH, null, TestReadOnlyZKClient.DATA, null);
        Assert.assertArrayEquals(TestReadOnlyZKClient.DATA, future.get());
        // now we will close the idle connection.
        waitForIdleConnectionClosed();
        Mockito.verify(mockedZK, Mockito.times(1)).close();
    }
}

