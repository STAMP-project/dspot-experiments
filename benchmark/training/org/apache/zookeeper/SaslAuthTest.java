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
package org.apache.zookeeper;


import CreateMode.PERSISTENT;
import CreateMode.PERSISTENT_SEQUENTIAL;
import Ids.CREATOR_ALL_ACL;
import Ids.OPEN_ACL_UNSAFE;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.zookeeper.ClientCnxn.EventThread;
import org.apache.zookeeper.ClientCnxn.SendThread;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.test.ClientBase;
import org.junit.Assert;
import org.junit.Test;


public class SaslAuthTest extends ClientBase {
    private final CountDownLatch authFailed = new CountDownLatch(1);

    private class MyWatcher extends ClientBase.CountdownWatcher {
        @Override
        public synchronized void process(WatchedEvent event) {
            if ((event.getState()) == (KeeperState.AuthFailed)) {
                authFailed.countDown();
            } else {
                super.process(event);
            }
        }
    }

    @Test
    public void testAuth() throws Exception {
        ZooKeeper zk = createClient();
        try {
            zk.create("/path1", null, CREATOR_ALL_ACL, PERSISTENT);
            Thread.sleep(1000);
        } finally {
            zk.close();
        }
    }

    @Test
    public void testValidSaslIds() throws Exception {
        ZooKeeper zk = createClient();
        List<String> validIds = new ArrayList<String>();
        validIds.add("user");
        validIds.add("service/host.name.com");
        validIds.add("user@KERB.REALM");
        validIds.add("service/host.name.com@KERB.REALM");
        int i = 0;
        for (String validId : validIds) {
            List<ACL> aclList = new ArrayList<ACL>();
            ACL acl = new ACL(0, new Id("sasl", validId));
            aclList.add(acl);
            zk.create(("/valid" + i), null, aclList, PERSISTENT);
            i++;
        }
    }

    @Test
    public void testInvalidSaslIds() throws Exception {
        ZooKeeper zk = createClient();
        List<String> invalidIds = new ArrayList<String>();
        invalidIds.add("user@KERB.REALM/server.com");
        invalidIds.add("user@KERB.REALM1@KERB.REALM2");
        int i = 0;
        for (String invalidId : invalidIds) {
            List<ACL> aclList = new ArrayList<ACL>();
            try {
                ACL acl = new ACL(0, new Id("sasl", invalidId));
                aclList.add(acl);
                zk.create(("/invalid" + i), null, aclList, PERSISTENT);
                Assert.fail("SASLAuthenticationProvider.isValid() failed to catch invalid Id.");
            } catch (KeeperException e) {
                // ok.
            } finally {
                i++;
            }
        }
    }

    @Test
    public void testZKOperationsAfterClientSaslAuthFailure() throws Exception {
        ClientBase.CountdownWatcher watcher = new ClientBase.CountdownWatcher();
        ZooKeeper zk = new ZooKeeper(hostPort, ClientBase.CONNECTION_TIMEOUT, watcher);
        watcher.waitForConnected(ClientBase.CONNECTION_TIMEOUT);
        try {
            setSaslFailureFlag(zk);
            // try node creation for around 15 second,
            int totalTry = 10;
            int tryCount = 0;
            boolean success = false;
            while ((!success) && ((tryCount++) <= totalTry)) {
                try {
                    zk.create("/saslAuthFail", "data".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT_SEQUENTIAL);
                    success = true;
                } catch (KeeperException e) {
                    Thread.sleep(1000);
                    // do nothing
                }
            } 
            Assert.assertTrue("ZNode creation is failing continuously after Sasl auth failure.", success);
        } finally {
            zk.close();
        }
    }

    @Test
    public void testThreadsShutdownOnAuthFailed() throws Exception {
        SaslAuthTest.MyWatcher watcher = new SaslAuthTest.MyWatcher();
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(hostPort, ClientBase.CONNECTION_TIMEOUT, watcher);
            watcher.waitForConnected(ClientBase.CONNECTION_TIMEOUT);
            try {
                zk.addAuthInfo("FOO", "BAR".getBytes());
                zk.getData("/path1", false, null);
                Assert.fail("Should get auth state error");
            } catch (KeeperException e) {
                if (!(authFailed.await(ClientBase.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS))) {
                    Assert.fail("Should have called my watcher");
                }
            }
            Field cnxnField = zk.getClass().getDeclaredField("cnxn");
            cnxnField.setAccessible(true);
            ClientCnxn clientCnxn = ((ClientCnxn) (cnxnField.get(zk)));
            Field sendThreadField = clientCnxn.getClass().getDeclaredField("sendThread");
            sendThreadField.setAccessible(true);
            SendThread sendThread = ((SendThread) (sendThreadField.get(clientCnxn)));
            Field eventThreadField = clientCnxn.getClass().getDeclaredField("eventThread");
            eventThreadField.setAccessible(true);
            EventThread eventThread = ((EventThread) (eventThreadField.get(clientCnxn)));
            sendThread.join(ClientBase.CONNECTION_TIMEOUT);
            eventThread.join(ClientBase.CONNECTION_TIMEOUT);
            Assert.assertFalse("SendThread did not shutdown after authFail", sendThread.isAlive());
            Assert.assertFalse("EventThread did not shutdown after authFail", eventThread.isAlive());
        } finally {
            if (zk != null) {
                zk.close();
            }
        }
    }
}

