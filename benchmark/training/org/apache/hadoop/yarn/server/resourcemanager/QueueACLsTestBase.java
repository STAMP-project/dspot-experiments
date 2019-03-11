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
package org.apache.hadoop.yarn.server.resourcemanager;


import org.junit.Test;


public abstract class QueueACLsTestBase extends ACLsTestBase {
    @Test
    public void testApplicationACLs() throws Exception {
        verifyKillAppSuccess(ACLsTestBase.QUEUE_A_USER, ACLsTestBase.QUEUE_A_USER, ACLsTestBase.QUEUEA, true);
        verifyKillAppSuccess(ACLsTestBase.QUEUE_A_USER, ACLsTestBase.QUEUE_A_ADMIN, ACLsTestBase.QUEUEA, true);
        verifyKillAppSuccess(ACLsTestBase.QUEUE_A_USER, ACLsTestBase.COMMON_USER, ACLsTestBase.QUEUEA, true);
        verifyKillAppSuccess(ACLsTestBase.QUEUE_A_USER, ACLsTestBase.ROOT_ADMIN, ACLsTestBase.QUEUEA, true);
        verifyKillAppFailure(ACLsTestBase.QUEUE_A_USER, ACLsTestBase.QUEUE_B_USER, ACLsTestBase.QUEUEA, true);
        verifyKillAppFailure(ACLsTestBase.QUEUE_A_USER, ACLsTestBase.QUEUE_B_ADMIN, ACLsTestBase.QUEUEA, true);
        verifyKillAppSuccess(ACLsTestBase.QUEUE_B_USER, ACLsTestBase.QUEUE_B_USER, ACLsTestBase.QUEUEB, true);
        verifyKillAppSuccess(ACLsTestBase.QUEUE_B_USER, ACLsTestBase.QUEUE_B_ADMIN, ACLsTestBase.QUEUEB, true);
        verifyKillAppSuccess(ACLsTestBase.QUEUE_B_USER, ACLsTestBase.COMMON_USER, ACLsTestBase.QUEUEB, true);
        verifyKillAppSuccess(ACLsTestBase.QUEUE_B_USER, ACLsTestBase.ROOT_ADMIN, ACLsTestBase.QUEUEB, true);
        verifyKillAppFailure(ACLsTestBase.QUEUE_B_USER, ACLsTestBase.QUEUE_A_USER, ACLsTestBase.QUEUEB, true);
        verifyKillAppFailure(ACLsTestBase.QUEUE_B_USER, ACLsTestBase.QUEUE_A_ADMIN, ACLsTestBase.QUEUEB, true);
        verifyKillAppSuccess(ACLsTestBase.ROOT_ADMIN, ACLsTestBase.ROOT_ADMIN, ACLsTestBase.QUEUEA, false);
        verifyKillAppSuccess(ACLsTestBase.ROOT_ADMIN, ACLsTestBase.ROOT_ADMIN, ACLsTestBase.QUEUEB, false);
        verifyGetClientAMToken(ACLsTestBase.QUEUE_A_USER, ACLsTestBase.ROOT_ADMIN, ACLsTestBase.QUEUEA, true);
    }
}

