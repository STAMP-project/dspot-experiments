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


import KeeperException.Code;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZKTestCase;
import org.apache.zookeeper.admin.ZooKeeperAdmin;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ReconfigMisconfigTest extends ZKTestCase {
    private static final Logger LOG = LoggerFactory.getLogger(ReconfigMisconfigTest.class);

    private QuorumUtil qu;

    private ZooKeeperAdmin zkAdmin;

    private static String errorMsg = "Reconfig should fail without configuring the super " + "user's password on server side first.";

    @Test(timeout = 10000)
    public void testReconfigFailWithoutSuperuserPasswordConfiguredOnServer() throws InterruptedException {
        // This tests the case where ZK ensemble does not have the super user's password configured.
        // Reconfig should fail as the super user has to be explicitly configured via
        // zookeeper.DigestAuthenticationProvider.superDigest.
        try {
            reconfigPort();
            Assert.fail(ReconfigMisconfigTest.errorMsg);
        } catch (KeeperException e) {
            Assert.assertTrue(((e.getCode()) == (Code.NoAuth)));
        }
        try {
            zkAdmin.addAuthInfo("digest", "super:".getBytes());
            reconfigPort();
            Assert.fail(ReconfigMisconfigTest.errorMsg);
        } catch (KeeperException e) {
            Assert.assertTrue(((e.getCode()) == (Code.NoAuth)));
        }
    }
}

