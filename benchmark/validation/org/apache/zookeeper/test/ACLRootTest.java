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
import Ids.CREATOR_ALL_ACL;
import Ids.OPEN_ACL_UNSAFE;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Assert;
import org.junit.Test;


public class ACLRootTest extends ClientBase {
    @Test
    public void testRootAcl() throws Exception {
        ZooKeeper zk = createClient();
        try {
            // set auth using digest
            zk.addAuthInfo("digest", "pat:test".getBytes());
            zk.setACL("/", CREATOR_ALL_ACL, (-1));
            zk.getData("/", false, null);
            zk.close();
            // verify no access
            zk = createClient();
            try {
                zk.getData("/", false, null);
                Assert.fail("validate auth");
            } catch (KeeperException e) {
                // expected
            }
            try {
                zk.create("/apps", null, CREATOR_ALL_ACL, PERSISTENT);
                Assert.fail("validate auth");
            } catch (KeeperException e) {
                // expected
            }
            zk.addAuthInfo("digest", "world:anyone".getBytes());
            try {
                zk.create("/apps", null, CREATOR_ALL_ACL, PERSISTENT);
                Assert.fail("validate auth");
            } catch (KeeperException e) {
                // expected
            }
            zk.close();
            // verify access using original auth
            zk = createClient();
            zk.addAuthInfo("digest", "pat:test".getBytes());
            zk.getData("/", false, null);
            zk.create("/apps", null, CREATOR_ALL_ACL, PERSISTENT);
            zk.delete("/apps", (-1));
            // reset acl (back to open) and verify accessible again
            zk.setACL("/", OPEN_ACL_UNSAFE, (-1));
            zk.close();
            zk = createClient();
            zk.getData("/", false, null);
            zk.create("/apps", null, OPEN_ACL_UNSAFE, PERSISTENT);
            try {
                zk.create("/apps", null, CREATOR_ALL_ACL, PERSISTENT);
                Assert.fail("validate auth");
            } catch (KeeperException e) {
                // expected
            }
            zk.delete("/apps", (-1));
            zk.addAuthInfo("digest", "world:anyone".getBytes());
            zk.create("/apps", null, CREATOR_ALL_ACL, PERSISTENT);
            zk.close();
            zk = createClient();
            zk.delete("/apps", (-1));
        } finally {
            zk.close();
        }
    }
}

