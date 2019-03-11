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
package org.apache.ambari.server.actionmanager;


import HostRoleStatus.ABORTED;
import HostRoleStatus.COMPLETED;
import HostRoleStatus.FAILED;
import HostRoleStatus.HOLDING;
import HostRoleStatus.HOLDING_FAILED;
import HostRoleStatus.HOLDING_TIMEDOUT;
import HostRoleStatus.IN_PROGRESS;
import HostRoleStatus.PENDING;
import HostRoleStatus.QUEUED;
import HostRoleStatus.TIMEDOUT;
import org.junit.Assert;
import org.junit.Test;


/**
 * HostRoleStatus Tests.
 */
public class HostRoleStatusTest {
    @Test
    public void testIsFailedState() throws Exception {
        Assert.assertTrue(ABORTED.isFailedState());
        Assert.assertFalse(COMPLETED.isFailedState());
        Assert.assertTrue(FAILED.isFailedState());
        Assert.assertFalse(IN_PROGRESS.isFailedState());
        Assert.assertFalse(PENDING.isFailedState());
        Assert.assertFalse(QUEUED.isFailedState());
        Assert.assertTrue(TIMEDOUT.isFailedState());
        Assert.assertFalse(HOLDING.isFailedState());
        Assert.assertFalse(HOLDING_FAILED.isFailedState());
        Assert.assertFalse(HOLDING_TIMEDOUT.isFailedState());
    }

    @Test
    public void testIsCompletedState() throws Exception {
        Assert.assertTrue(ABORTED.isCompletedState());
        Assert.assertTrue(COMPLETED.isCompletedState());
        Assert.assertTrue(FAILED.isCompletedState());
        Assert.assertFalse(IN_PROGRESS.isCompletedState());
        Assert.assertFalse(PENDING.isCompletedState());
        Assert.assertFalse(QUEUED.isCompletedState());
        Assert.assertTrue(TIMEDOUT.isCompletedState());
        Assert.assertFalse(HOLDING.isCompletedState());
        Assert.assertFalse(HOLDING_FAILED.isCompletedState());
        Assert.assertFalse(HOLDING_TIMEDOUT.isCompletedState());
    }

    @Test
    public void testIsHoldingState() throws Exception {
        Assert.assertFalse(ABORTED.isHoldingState());
        Assert.assertFalse(COMPLETED.isHoldingState());
        Assert.assertFalse(FAILED.isHoldingState());
        Assert.assertFalse(IN_PROGRESS.isHoldingState());
        Assert.assertFalse(PENDING.isHoldingState());
        Assert.assertFalse(QUEUED.isHoldingState());
        Assert.assertFalse(TIMEDOUT.isHoldingState());
        Assert.assertTrue(HOLDING.isHoldingState());
        Assert.assertTrue(HOLDING_FAILED.isHoldingState());
        Assert.assertTrue(HOLDING_TIMEDOUT.isHoldingState());
    }
}

