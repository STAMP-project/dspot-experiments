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
package org.apache.hadoop.ha;


import Code.APIERROR;
import Code.CONNECTIONLOSS;
import Code.NODEEXISTS;
import Code.NONODE;
import Code.OK;
import Code.RUNTIMEINCONSISTENCY;
import Code.SESSIONEXPIRED;
import CommonConfigurationKeys.HA_FC_ELECTOR_ZK_OP_RETRIES_DEFAULT;
import CreateMode.EPHEMERAL;
import CreateMode.PERSISTENT;
import Event.EventType.NodeChildrenChanged;
import Event.EventType.NodeDataChanged;
import Event.EventType.NodeDeleted;
import Event.EventType.None;
import Event.KeeperState.AuthFailed;
import Event.KeeperState.Disconnected;
import Event.KeeperState.Expired;
import Event.KeeperState.SyncConnected;
import Ids.OPEN_ACL_UNSAFE;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.ha.ActiveStandbyElector.ActiveNotFoundException;
import org.apache.hadoop.ha.ActiveStandbyElector.ActiveStandbyElectorCallback;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.ZKUtil.ZKAuthInfo;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static ActiveStandbyElector.BREADCRUMB_FILENAME;
import static ActiveStandbyElector.LOCK_FILENAME;


public class TestActiveStandbyElector {
    private ZooKeeper mockZK;

    private int count;

    private ActiveStandbyElectorCallback mockApp;

    private final byte[] data = new byte[8];

    private TestActiveStandbyElector.ActiveStandbyElectorTester elector;

    class ActiveStandbyElectorTester extends ActiveStandbyElector {
        private int sleptFor = 0;

        ActiveStandbyElectorTester(String hostPort, int timeout, String parent, List<ACL> acl, ActiveStandbyElectorCallback app) throws IOException, KeeperException {
            super(hostPort, timeout, parent, acl, Collections.<ZKAuthInfo>emptyList(), app, HA_FC_ELECTOR_ZK_OP_RETRIES_DEFAULT);
        }

        @Override
        public ZooKeeper connectToZooKeeper() {
            ++(count);
            return mockZK;
        }

        @Override
        protected void sleepFor(int ms) {
            // don't sleep in unit tests! Instead, just record the amount of
            // time slept
            LOG.info((("Would have slept for " + ms) + "ms"));
            sleptFor += ms;
        }
    }

    private static final String ZK_PARENT_NAME = "/parent/node";

    private static final String ZK_LOCK_NAME = ((TestActiveStandbyElector.ZK_PARENT_NAME) + "/") + (LOCK_FILENAME);

    private static final String ZK_BREADCRUMB_NAME = ((TestActiveStandbyElector.ZK_PARENT_NAME) + "/") + (BREADCRUMB_FILENAME);

    /**
     * verify that joinElection checks for null data
     */
    @Test(expected = HadoopIllegalArgumentException.class)
    public void testJoinElectionException() {
        elector.joinElection(null);
    }

    /**
     * verify that joinElection tries to create ephemeral lock znode
     */
    @Test
    public void testJoinElection() {
        joinElection(data);
        Mockito.verify(mockZK, Mockito.times(1)).create(TestActiveStandbyElector.ZK_LOCK_NAME, data, OPEN_ACL_UNSAFE, EPHEMERAL, elector, mockZK);
    }

    /**
     * verify that successful znode create result becomes active and monitoring is
     * started
     */
    @Test
    public void testCreateNodeResultBecomeActive() throws Exception {
        mockNoPriorActive();
        joinElection(data);
        elector.processResult(OK.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, TestActiveStandbyElector.ZK_LOCK_NAME);
        Mockito.verify(mockApp, Mockito.times(1)).becomeActive();
        verifyExistCall(1);
        // monitor callback verifies the leader is ephemeral owner of lock but does
        // not call becomeActive since its already active
        Stat stat = new Stat();
        stat.setEphemeralOwner(1L);
        Mockito.when(mockZK.getSessionId()).thenReturn(1L);
        elector.processResult(OK.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, stat);
        // should not call neutral mode/standby/active
        Mockito.verify(mockApp, Mockito.times(0)).enterNeutralMode();
        Mockito.verify(mockApp, Mockito.times(0)).becomeStandby();
        Mockito.verify(mockApp, Mockito.times(1)).becomeActive();
        // another joinElection not called.
        Mockito.verify(mockZK, Mockito.times(1)).create(TestActiveStandbyElector.ZK_LOCK_NAME, data, OPEN_ACL_UNSAFE, EPHEMERAL, elector, mockZK);
        // no new monitor called
        verifyExistCall(1);
    }

    /**
     * Verify that, when the callback fails to enter active state,
     * the elector rejoins the election after sleeping for a short period.
     */
    @Test
    public void testFailToBecomeActive() throws Exception {
        mockNoPriorActive();
        joinElection(data);
        Assert.assertEquals(0, elector.sleptFor);
        Mockito.doThrow(new ServiceFailedException("failed to become active")).when(mockApp).becomeActive();
        elector.processResult(OK.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, TestActiveStandbyElector.ZK_LOCK_NAME);
        // Should have tried to become active
        Mockito.verify(mockApp).becomeActive();
        // should re-join
        Mockito.verify(mockZK, Mockito.times(2)).create(TestActiveStandbyElector.ZK_LOCK_NAME, data, OPEN_ACL_UNSAFE, EPHEMERAL, elector, mockZK);
        Assert.assertEquals(2, count);
        Assert.assertTrue(((elector.sleptFor) > 0));
    }

    /**
     * Verify that, when the callback fails to enter active state, after
     * a ZK disconnect (i.e from the StatCallback), that the elector rejoins
     * the election after sleeping for a short period.
     */
    @Test
    public void testFailToBecomeActiveAfterZKDisconnect() throws Exception {
        mockNoPriorActive();
        joinElection(data);
        Assert.assertEquals(0, elector.sleptFor);
        elector.processResult(CONNECTIONLOSS.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, TestActiveStandbyElector.ZK_LOCK_NAME);
        Mockito.verify(mockZK, Mockito.times(2)).create(TestActiveStandbyElector.ZK_LOCK_NAME, data, OPEN_ACL_UNSAFE, EPHEMERAL, elector, mockZK);
        elector.processResult(NODEEXISTS.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, TestActiveStandbyElector.ZK_LOCK_NAME);
        verifyExistCall(1);
        Stat stat = new Stat();
        stat.setEphemeralOwner(1L);
        Mockito.when(mockZK.getSessionId()).thenReturn(1L);
        // Fake failure to become active from within the stat callback
        Mockito.doThrow(new ServiceFailedException("fail to become active")).when(mockApp).becomeActive();
        elector.processResult(OK.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, stat);
        Mockito.verify(mockApp, Mockito.times(1)).becomeActive();
        // should re-join
        Mockito.verify(mockZK, Mockito.times(3)).create(TestActiveStandbyElector.ZK_LOCK_NAME, data, OPEN_ACL_UNSAFE, EPHEMERAL, elector, mockZK);
        Assert.assertEquals(2, count);
        Assert.assertTrue(((elector.sleptFor) > 0));
    }

    /**
     * Verify that, if there is a record of a prior active node, the
     * elector asks the application to fence it before becoming active.
     */
    @Test
    public void testFencesOldActive() throws Exception {
        byte[] fakeOldActiveData = new byte[0];
        mockPriorActive(fakeOldActiveData);
        joinElection(data);
        elector.processResult(OK.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, TestActiveStandbyElector.ZK_LOCK_NAME);
        // Application fences active.
        Mockito.verify(mockApp, Mockito.times(1)).fenceOldActive(fakeOldActiveData);
        // Updates breadcrumb node to new data
        Mockito.verify(mockZK, Mockito.times(1)).setData(Mockito.eq(TestActiveStandbyElector.ZK_BREADCRUMB_NAME), Mockito.eq(data), Mockito.eq(0));
        // Then it becomes active itself
        Mockito.verify(mockApp, Mockito.times(1)).becomeActive();
    }

    @Test
    public void testQuitElectionRemovesBreadcrumbNode() throws Exception {
        mockNoPriorActive();
        joinElection(data);
        elector.processResult(OK.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, TestActiveStandbyElector.ZK_LOCK_NAME);
        // Writes its own active info
        Mockito.verify(mockZK, Mockito.times(1)).create(Mockito.eq(TestActiveStandbyElector.ZK_BREADCRUMB_NAME), Mockito.eq(data), Mockito.eq(OPEN_ACL_UNSAFE), Mockito.eq(PERSISTENT));
        mockPriorActive(data);
        quitElection(false);
        // Deletes its own active data
        Mockito.verify(mockZK, Mockito.times(1)).delete(Mockito.eq(TestActiveStandbyElector.ZK_BREADCRUMB_NAME), Mockito.eq(0));
    }

    /**
     * verify that znode create for existing node and no retry becomes standby and
     * monitoring is started
     */
    @Test
    public void testCreateNodeResultBecomeStandby() {
        joinElection(data);
        elector.processResult(NODEEXISTS.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, TestActiveStandbyElector.ZK_LOCK_NAME);
        Mockito.verify(mockApp, Mockito.times(1)).becomeStandby();
        verifyExistCall(1);
    }

    /**
     * verify that znode create error result in fatal error
     */
    @Test
    public void testCreateNodeResultError() {
        joinElection(data);
        elector.processResult(APIERROR.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, TestActiveStandbyElector.ZK_LOCK_NAME);
        Mockito.verify(mockApp, Mockito.times(1)).notifyFatalError((("Received create error from Zookeeper. code:APIERROR " + "for path ") + (TestActiveStandbyElector.ZK_LOCK_NAME)));
    }

    /**
     * verify that retry of network errors verifies master by session id and
     * becomes active if they match. monitoring is started.
     */
    @Test
    public void testCreateNodeResultRetryBecomeActive() throws Exception {
        mockNoPriorActive();
        joinElection(data);
        elector.processResult(CONNECTIONLOSS.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, TestActiveStandbyElector.ZK_LOCK_NAME);
        elector.processResult(CONNECTIONLOSS.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, TestActiveStandbyElector.ZK_LOCK_NAME);
        elector.processResult(CONNECTIONLOSS.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, TestActiveStandbyElector.ZK_LOCK_NAME);
        elector.processResult(CONNECTIONLOSS.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, TestActiveStandbyElector.ZK_LOCK_NAME);
        // 4 errors results in fatalError
        Mockito.verify(mockApp, Mockito.times(1)).notifyFatalError((((("Received create error from Zookeeper. code:CONNECTIONLOSS " + "for path ") + (TestActiveStandbyElector.ZK_LOCK_NAME)) + ". ") + "Not retrying further znode create connection errors."));
        joinElection(data);
        // recreate connection via getNewZooKeeper
        Assert.assertEquals(2, count);
        elector.processResult(CONNECTIONLOSS.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, TestActiveStandbyElector.ZK_LOCK_NAME);
        elector.processResult(NODEEXISTS.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, TestActiveStandbyElector.ZK_LOCK_NAME);
        verifyExistCall(1);
        Stat stat = new Stat();
        stat.setEphemeralOwner(1L);
        Mockito.when(mockZK.getSessionId()).thenReturn(1L);
        elector.processResult(OK.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, stat);
        Mockito.verify(mockApp, Mockito.times(1)).becomeActive();
        verifyExistCall(1);
        Mockito.verify(mockZK, Mockito.times(6)).create(TestActiveStandbyElector.ZK_LOCK_NAME, data, OPEN_ACL_UNSAFE, EPHEMERAL, elector, mockZK);
    }

    /**
     * verify that retry of network errors verifies active by session id and
     * becomes standby if they dont match. monitoring is started.
     */
    @Test
    public void testCreateNodeResultRetryBecomeStandby() {
        joinElection(data);
        elector.processResult(CONNECTIONLOSS.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, TestActiveStandbyElector.ZK_LOCK_NAME);
        elector.processResult(NODEEXISTS.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, TestActiveStandbyElector.ZK_LOCK_NAME);
        verifyExistCall(1);
        Stat stat = new Stat();
        stat.setEphemeralOwner(0);
        Mockito.when(mockZK.getSessionId()).thenReturn(1L);
        elector.processResult(OK.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, stat);
        Mockito.verify(mockApp, Mockito.times(1)).becomeStandby();
        verifyExistCall(1);
    }

    /**
     * verify that if create znode results in nodeexists and that znode is deleted
     * before exists() watch is set then the return of the exists() method results
     * in attempt to re-create the znode and become active
     */
    @Test
    public void testCreateNodeResultRetryNoNode() {
        joinElection(data);
        elector.processResult(CONNECTIONLOSS.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, TestActiveStandbyElector.ZK_LOCK_NAME);
        elector.processResult(CONNECTIONLOSS.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, TestActiveStandbyElector.ZK_LOCK_NAME);
        elector.processResult(NODEEXISTS.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, TestActiveStandbyElector.ZK_LOCK_NAME);
        verifyExistCall(1);
        elector.processResult(NONODE.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, ((Stat) (null)));
        Mockito.verify(mockApp, Mockito.times(1)).enterNeutralMode();
        Mockito.verify(mockZK, Mockito.times(4)).create(TestActiveStandbyElector.ZK_LOCK_NAME, data, OPEN_ACL_UNSAFE, EPHEMERAL, elector, mockZK);
    }

    /**
     * verify that more than 3 network error retries result fatalError
     */
    @Test
    public void testStatNodeRetry() {
        joinElection(data);
        elector.processResult(CONNECTIONLOSS.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, ((Stat) (null)));
        elector.processResult(CONNECTIONLOSS.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, ((Stat) (null)));
        elector.processResult(CONNECTIONLOSS.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, ((Stat) (null)));
        elector.processResult(CONNECTIONLOSS.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, ((Stat) (null)));
        Mockito.verify(mockApp, Mockito.times(1)).notifyFatalError(("Received stat error from Zookeeper. code:CONNECTIONLOSS. " + "Not retrying further znode monitoring connection errors."));
    }

    /**
     * verify error in exists() callback results in fatal error
     */
    @Test
    public void testStatNodeError() {
        joinElection(data);
        elector.processResult(RUNTIMEINCONSISTENCY.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, ((Stat) (null)));
        Mockito.verify(mockApp, Mockito.times(0)).enterNeutralMode();
        Mockito.verify(mockApp, Mockito.times(1)).notifyFatalError("Received stat error from Zookeeper. code:RUNTIMEINCONSISTENCY");
    }

    /**
     * verify behavior of watcher.process callback with non-node event
     */
    @Test
    public void testProcessCallbackEventNone() throws Exception {
        mockNoPriorActive();
        joinElection(data);
        WatchedEvent mockEvent = Mockito.mock(WatchedEvent.class);
        Mockito.when(mockEvent.getType()).thenReturn(None);
        // first SyncConnected should not do anything
        Mockito.when(mockEvent.getState()).thenReturn(SyncConnected);
        elector.processWatchEvent(mockZK, mockEvent);
        Mockito.verify(mockZK, Mockito.times(0)).exists(Mockito.anyString(), Mockito.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.any());
        // disconnection should enter safe mode
        Mockito.when(mockEvent.getState()).thenReturn(Disconnected);
        elector.processWatchEvent(mockZK, mockEvent);
        Mockito.verify(mockApp, Mockito.times(1)).enterNeutralMode();
        // re-connection should monitor master status
        Mockito.when(mockEvent.getState()).thenReturn(SyncConnected);
        elector.processWatchEvent(mockZK, mockEvent);
        verifyExistCall(1);
        Assert.assertTrue(isMonitorLockNodePending());
        elector.processResult(SESSIONEXPIRED.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, new Stat());
        Assert.assertFalse(isMonitorLockNodePending());
        // session expired should enter safe mode and initiate re-election
        // re-election checked via checking re-creation of new zookeeper and
        // call to create lock znode
        Mockito.when(mockEvent.getState()).thenReturn(Expired);
        elector.processWatchEvent(mockZK, mockEvent);
        // already in safe mode above. should not enter safe mode again
        Mockito.verify(mockApp, Mockito.times(1)).enterNeutralMode();
        // called getNewZooKeeper to create new session. first call was in
        // constructor
        Assert.assertEquals(2, count);
        // once in initial joinElection and one now
        Mockito.verify(mockZK, Mockito.times(2)).create(TestActiveStandbyElector.ZK_LOCK_NAME, data, OPEN_ACL_UNSAFE, EPHEMERAL, elector, mockZK);
        // create znode success. become master and monitor
        elector.processResult(OK.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, TestActiveStandbyElector.ZK_LOCK_NAME);
        Mockito.verify(mockApp, Mockito.times(1)).becomeActive();
        verifyExistCall(2);
        // error event results in fatal error
        Mockito.when(mockEvent.getState()).thenReturn(AuthFailed);
        elector.processWatchEvent(mockZK, mockEvent);
        Mockito.verify(mockApp, Mockito.times(1)).notifyFatalError("Unexpected Zookeeper watch event state: AuthFailed");
        // only 1 state change callback is called at a time
        Mockito.verify(mockApp, Mockito.times(1)).enterNeutralMode();
    }

    /**
     * verify behavior of watcher.process with node event
     */
    @Test
    public void testProcessCallbackEventNode() throws Exception {
        mockNoPriorActive();
        joinElection(data);
        // make the object go into the monitoring state
        elector.processResult(NODEEXISTS.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, TestActiveStandbyElector.ZK_LOCK_NAME);
        Mockito.verify(mockApp, Mockito.times(1)).becomeStandby();
        verifyExistCall(1);
        Assert.assertTrue(isMonitorLockNodePending());
        Stat stat = new Stat();
        stat.setEphemeralOwner(0L);
        Mockito.when(mockZK.getSessionId()).thenReturn(1L);
        elector.processResult(OK.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, stat);
        Assert.assertFalse(isMonitorLockNodePending());
        WatchedEvent mockEvent = Mockito.mock(WatchedEvent.class);
        Mockito.when(mockEvent.getPath()).thenReturn(TestActiveStandbyElector.ZK_LOCK_NAME);
        // monitoring should be setup again after event is received
        Mockito.when(mockEvent.getType()).thenReturn(NodeDataChanged);
        elector.processWatchEvent(mockZK, mockEvent);
        verifyExistCall(2);
        Assert.assertTrue(isMonitorLockNodePending());
        elector.processResult(OK.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, stat);
        Assert.assertFalse(isMonitorLockNodePending());
        // monitoring should be setup again after event is received
        Mockito.when(mockEvent.getType()).thenReturn(NodeChildrenChanged);
        elector.processWatchEvent(mockZK, mockEvent);
        verifyExistCall(3);
        Assert.assertTrue(isMonitorLockNodePending());
        elector.processResult(OK.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, stat);
        Assert.assertFalse(isMonitorLockNodePending());
        // lock node deletion when in standby mode should create znode again
        // successful znode creation enters active state and sets monitor
        Mockito.when(mockEvent.getType()).thenReturn(NodeDeleted);
        elector.processWatchEvent(mockZK, mockEvent);
        // enterNeutralMode not called when app is standby and leader is lost
        Mockito.verify(mockApp, Mockito.times(0)).enterNeutralMode();
        // once in initial joinElection() and one now
        Mockito.verify(mockZK, Mockito.times(2)).create(TestActiveStandbyElector.ZK_LOCK_NAME, data, OPEN_ACL_UNSAFE, EPHEMERAL, elector, mockZK);
        elector.processResult(OK.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, TestActiveStandbyElector.ZK_LOCK_NAME);
        Mockito.verify(mockApp, Mockito.times(1)).becomeActive();
        verifyExistCall(4);
        Assert.assertTrue(isMonitorLockNodePending());
        stat.setEphemeralOwner(1L);
        elector.processResult(OK.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, stat);
        Assert.assertFalse(isMonitorLockNodePending());
        // lock node deletion in active mode should enter neutral mode and create
        // znode again successful znode creation enters active state and sets
        // monitor
        Mockito.when(mockEvent.getType()).thenReturn(NodeDeleted);
        elector.processWatchEvent(mockZK, mockEvent);
        Mockito.verify(mockApp, Mockito.times(1)).enterNeutralMode();
        // another joinElection called
        Mockito.verify(mockZK, Mockito.times(3)).create(TestActiveStandbyElector.ZK_LOCK_NAME, data, OPEN_ACL_UNSAFE, EPHEMERAL, elector, mockZK);
        elector.processResult(OK.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, TestActiveStandbyElector.ZK_LOCK_NAME);
        Mockito.verify(mockApp, Mockito.times(2)).becomeActive();
        verifyExistCall(5);
        Assert.assertTrue(isMonitorLockNodePending());
        elector.processResult(OK.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, stat);
        Assert.assertFalse(isMonitorLockNodePending());
        // bad path name results in fatal error
        Mockito.when(mockEvent.getPath()).thenReturn(null);
        elector.processWatchEvent(mockZK, mockEvent);
        Mockito.verify(mockApp, Mockito.times(1)).notifyFatalError("Unexpected watch error from Zookeeper");
        // fatal error means no new connection other than one from constructor
        Assert.assertEquals(1, count);
        // no new watches after fatal error
        verifyExistCall(5);
    }

    /**
     * verify becomeStandby is not called if already in standby
     */
    @Test
    public void testSuccessiveStandbyCalls() {
        joinElection(data);
        // make the object go into the monitoring standby state
        elector.processResult(NODEEXISTS.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, TestActiveStandbyElector.ZK_LOCK_NAME);
        Mockito.verify(mockApp, Mockito.times(1)).becomeStandby();
        verifyExistCall(1);
        Assert.assertTrue(isMonitorLockNodePending());
        Stat stat = new Stat();
        stat.setEphemeralOwner(0L);
        Mockito.when(mockZK.getSessionId()).thenReturn(1L);
        elector.processResult(OK.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, stat);
        Assert.assertFalse(isMonitorLockNodePending());
        WatchedEvent mockEvent = Mockito.mock(WatchedEvent.class);
        Mockito.when(mockEvent.getPath()).thenReturn(TestActiveStandbyElector.ZK_LOCK_NAME);
        // notify node deletion
        // monitoring should be setup again after event is received
        Mockito.when(mockEvent.getType()).thenReturn(NodeDeleted);
        elector.processWatchEvent(mockZK, mockEvent);
        // is standby. no need to notify anything now
        Mockito.verify(mockApp, Mockito.times(0)).enterNeutralMode();
        // another joinElection called.
        Mockito.verify(mockZK, Mockito.times(2)).create(TestActiveStandbyElector.ZK_LOCK_NAME, data, OPEN_ACL_UNSAFE, EPHEMERAL, elector, mockZK);
        // lost election
        elector.processResult(NODEEXISTS.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, TestActiveStandbyElector.ZK_LOCK_NAME);
        // still standby. so no need to notify again
        Mockito.verify(mockApp, Mockito.times(1)).becomeStandby();
        // monitor is set again
        verifyExistCall(2);
    }

    /**
     * verify quit election terminates connection and there are no new watches.
     * next call to joinElection creates new connection and performs election
     */
    @Test
    public void testQuitElection() throws Exception {
        joinElection(data);
        Mockito.verify(mockZK, Mockito.times(0)).close();
        quitElection(true);
        Mockito.verify(mockZK, Mockito.times(1)).close();
        // no watches added
        verifyExistCall(0);
        byte[] data = new byte[8];
        joinElection(data);
        // getNewZooKeeper called 2 times. once in constructor and once now
        Assert.assertEquals(2, count);
        elector.processResult(NODEEXISTS.intValue(), TestActiveStandbyElector.ZK_LOCK_NAME, mockZK, TestActiveStandbyElector.ZK_LOCK_NAME);
        Mockito.verify(mockApp, Mockito.times(1)).becomeStandby();
        verifyExistCall(1);
    }

    /**
     * verify that receiveActiveData gives data when active exists, tells that
     * active does not exist and reports error in getting active information
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     * @throws KeeperException
     * 		
     * @throws ActiveNotFoundException
     * 		
     */
    @Test
    public void testGetActiveData() throws IOException, InterruptedException, ActiveNotFoundException, KeeperException {
        // get valid active data
        byte[] data = new byte[8];
        Mockito.when(mockZK.getData(Mockito.eq(TestActiveStandbyElector.ZK_LOCK_NAME), Mockito.eq(false), ArgumentMatchers.any())).thenReturn(data);
        Assert.assertEquals(data, getActiveData());
        Mockito.verify(mockZK, Mockito.times(1)).getData(Mockito.eq(TestActiveStandbyElector.ZK_LOCK_NAME), Mockito.eq(false), ArgumentMatchers.any());
        // active does not exist
        Mockito.when(mockZK.getData(Mockito.eq(TestActiveStandbyElector.ZK_LOCK_NAME), Mockito.eq(false), ArgumentMatchers.any())).thenThrow(new KeeperException.NoNodeException());
        try {
            getActiveData();
            Assert.fail("ActiveNotFoundException expected");
        } catch (ActiveNotFoundException e) {
            Mockito.verify(mockZK, Mockito.times(2)).getData(Mockito.eq(TestActiveStandbyElector.ZK_LOCK_NAME), Mockito.eq(false), ArgumentMatchers.any());
        }
        // error getting active data rethrows keeperexception
        try {
            Mockito.when(mockZK.getData(Mockito.eq(TestActiveStandbyElector.ZK_LOCK_NAME), Mockito.eq(false), ArgumentMatchers.any())).thenThrow(new KeeperException.AuthFailedException());
            getActiveData();
            Assert.fail("KeeperException.AuthFailedException expected");
        } catch (KeeperException ke) {
            Mockito.verify(mockZK, Mockito.times(3)).getData(Mockito.eq(TestActiveStandbyElector.ZK_LOCK_NAME), Mockito.eq(false), ArgumentMatchers.any());
        }
    }

    /**
     * Test that ensureBaseNode() recursively creates the specified dir
     */
    @Test
    public void testEnsureBaseNode() throws Exception {
        ensureParentZNode();
        StringBuilder prefix = new StringBuilder();
        for (String part : TestActiveStandbyElector.ZK_PARENT_NAME.split("/")) {
            if (part.isEmpty())
                continue;

            prefix.append("/").append(part);
            if (!("/".equals(prefix.toString()))) {
                Mockito.verify(mockZK).create(Mockito.eq(prefix.toString()), Mockito.<byte[]>any(), Mockito.eq(OPEN_ACL_UNSAFE), Mockito.eq(PERSISTENT));
            }
        }
    }

    /**
     * Test that ACLs are set on parent zNode even if the node already exists.
     */
    @Test
    public void testParentZNodeACLs() throws Exception {
        KeeperException ke = new KeeperException(Code.NODEEXISTS) {
            @Override
            public Code code() {
                return super.code();
            }
        };
        Mockito.when(mockZK.create(Mockito.anyString(), Mockito.eq(new byte[]{  }), Mockito.anyList(), Mockito.eq(PERSISTENT))).thenThrow(ke);
        ensureParentZNode();
        StringBuilder prefix = new StringBuilder();
        for (String part : TestActiveStandbyElector.ZK_PARENT_NAME.split("/")) {
            if (part.isEmpty())
                continue;

            prefix.append("/").append(part);
            if (!("/".equals(prefix.toString()))) {
                Mockito.verify(mockZK).getACL(Mockito.eq(prefix.toString()), Mockito.eq(new Stat()));
                Mockito.verify(mockZK).setACL(Mockito.eq(prefix.toString()), Mockito.eq(OPEN_ACL_UNSAFE), Mockito.anyInt());
            }
        }
    }

    /**
     * Test for a bug encountered during development of HADOOP-8163:
     * ensureBaseNode() should throw an exception if it has to retry
     * more than 3 times to create any part of the path.
     */
    @Test
    public void testEnsureBaseNodeFails() throws Exception {
        Mockito.doThrow(new KeeperException.ConnectionLossException()).when(mockZK).create(Mockito.eq(TestActiveStandbyElector.ZK_PARENT_NAME), Mockito.<byte[]>any(), Mockito.eq(OPEN_ACL_UNSAFE), Mockito.eq(PERSISTENT));
        try {
            ensureParentZNode();
            Assert.fail("Did not throw!");
        } catch (IOException ioe) {
            if (!((ioe.getCause()) instanceof KeeperException.ConnectionLossException)) {
                throw ioe;
            }
        }
        // Should have tried three times
        Mockito.verify(mockZK, Mockito.times(3)).create(Mockito.eq(TestActiveStandbyElector.ZK_PARENT_NAME), Mockito.<byte[]>any(), Mockito.eq(OPEN_ACL_UNSAFE), Mockito.eq(PERSISTENT));
    }

    /**
     * verify the zookeeper connection establishment
     */
    @Test
    public void testWithoutZKServer() throws Exception {
        try {
            new ActiveStandbyElector("127.0.0.1", 2000, TestActiveStandbyElector.ZK_PARENT_NAME, Ids.OPEN_ACL_UNSAFE, Collections.<ZKAuthInfo>emptyList(), mockApp, CommonConfigurationKeys.HA_FC_ELECTOR_ZK_OP_RETRIES_DEFAULT) {
                @Override
                protected ZooKeeper createZooKeeper() throws IOException {
                    return Mockito.mock(ZooKeeper.class);
                }
            };
            Assert.fail("Did not throw zookeeper connection loss exceptions!");
        } catch (KeeperException ke) {
            GenericTestUtils.assertExceptionContains("ConnectionLoss", ke);
        }
    }

    /**
     * joinElection(..) should happen only after SERVICE_HEALTHY.
     */
    @Test
    public void testBecomeActiveBeforeServiceHealthy() throws Exception {
        mockNoPriorActive();
        WatchedEvent mockEvent = Mockito.mock(WatchedEvent.class);
        Mockito.when(mockEvent.getType()).thenReturn(None);
        // session expired should enter safe mode
        // But for first time, before the SERVICE_HEALTY i.e. appData is set,
        // should not enter the election.
        Mockito.when(mockEvent.getState()).thenReturn(Expired);
        elector.processWatchEvent(mockZK, mockEvent);
        // joinElection should not be called.
        Mockito.verify(mockZK, Mockito.times(0)).create(TestActiveStandbyElector.ZK_LOCK_NAME, null, OPEN_ACL_UNSAFE, EPHEMERAL, elector, mockZK);
    }
}

