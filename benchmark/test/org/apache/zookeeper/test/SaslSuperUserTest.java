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
package org.apache.zookeeper.test;


import CreateMode.PERSISTENT;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Id;
import org.junit.Assert;
import org.junit.Test;


public class SaslSuperUserTest extends ClientBase {
    private static Id otherSaslUser = new Id("sasl", "joe");

    private static Id otherDigestUser;

    private static String oldAuthProvider;

    private static String oldLoginConfig;

    private static String oldSuperUser;

    private AtomicInteger authFailed = new AtomicInteger(0);

    private class MyWatcher extends ClientBase.CountdownWatcher {
        @Override
        public synchronized void process(WatchedEvent event) {
            if ((event.getState()) == (KeeperState.AuthFailed)) {
                authFailed.incrementAndGet();
            } else {
                super.process(event);
            }
        }
    }

    @Test
    public void testSuperIsSuper() throws Exception {
        ZooKeeper zk = createClient();
        try {
            zk.create("/digest_read", null, Arrays.asList(new org.apache.zookeeper.data.ACL(Perms.READ, SaslSuperUserTest.otherDigestUser)), PERSISTENT);
            zk.create("/digest_read/sub", null, Arrays.asList(new org.apache.zookeeper.data.ACL(Perms.READ, SaslSuperUserTest.otherDigestUser)), PERSISTENT);
            zk.create("/sasl_read", null, Arrays.asList(new org.apache.zookeeper.data.ACL(Perms.READ, SaslSuperUserTest.otherSaslUser)), PERSISTENT);
            zk.create("/sasl_read/sub", null, Arrays.asList(new org.apache.zookeeper.data.ACL(Perms.READ, SaslSuperUserTest.otherSaslUser)), PERSISTENT);
            zk.delete("/digest_read/sub", (-1));
            zk.delete("/digest_read", (-1));
            zk.delete("/sasl_read/sub", (-1));
            zk.delete("/sasl_read", (-1));
            // If the test failes it will most likely fail with a NoAuth exception before it ever gets to this assertion
            Assert.assertEquals(authFailed.get(), 0);
        } finally {
            zk.close();
        }
    }
}

