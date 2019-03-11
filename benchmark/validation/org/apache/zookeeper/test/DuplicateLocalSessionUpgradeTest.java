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


import org.apache.zookeeper.ZKTestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * When request are route incorrectly, both follower and the leader will perform
 * local session upgrade. So we saw CreateSession twice in txnlog This doesn't
 * affect the correctness but cause the ensemble to see more load than
 * necessary.
 */
public class DuplicateLocalSessionUpgradeTest extends ZKTestCase {
    protected static final Logger LOG = LoggerFactory.getLogger(DuplicateLocalSessionUpgradeTest.class);

    private final QuorumBase qb = new QuorumBase();

    private static final int CONNECTION_TIMEOUT = ClientBase.CONNECTION_TIMEOUT;

    @Test
    public void testLocalSessionUpgradeOnFollower() throws Exception {
        testLocalSessionUpgrade(false);
    }

    @Test
    public void testLocalSessionUpgradeOnLeader() throws Exception {
        testLocalSessionUpgrade(true);
    }
}

