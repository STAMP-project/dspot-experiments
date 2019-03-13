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


import CommonConfigurationKeys.HA_FC_GRACEFUL_FENCE_TIMEOUT_DEFAULT;
import HAServiceState.ACTIVE;
import HAServiceState.STANDBY;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ha.HAServiceProtocol.HAServiceState;
import org.apache.hadoop.security.AccessControlException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.ThrowsException;

import static org.apache.hadoop.ha.TestNodeFencer.AlwaysSucceedFencer.fenceCalled;
import static org.apache.hadoop.ha.TestNodeFencer.AlwaysSucceedFencer.fencedSvc;


public class TestFailoverController {
    private InetSocketAddress svc1Addr = new InetSocketAddress("svc1", 1234);

    private InetSocketAddress svc2Addr = new InetSocketAddress("svc2", 5678);

    private Configuration conf = new Configuration();

    HAServiceStatus STATE_NOT_READY = setNotReadyToBecomeActive("injected not ready");

    @Test
    public void testFailoverAndFailback() throws Exception {
        DummyHAService svc1 = new DummyHAService(HAServiceState.ACTIVE, svc1Addr);
        DummyHAService svc2 = new DummyHAService(HAServiceState.STANDBY, svc2Addr);
        svc1.fencer = svc2.fencer = TestNodeFencer.setupFencer(TestNodeFencer.AlwaysSucceedFencer.class.getName());
        fenceCalled = 0;
        doFailover(svc1, svc2, false, false);
        Assert.assertEquals(0, fenceCalled);
        Assert.assertEquals(STANDBY, svc1.state);
        Assert.assertEquals(ACTIVE, svc2.state);
        fenceCalled = 0;
        doFailover(svc2, svc1, false, false);
        Assert.assertEquals(0, fenceCalled);
        Assert.assertEquals(ACTIVE, svc1.state);
        Assert.assertEquals(STANDBY, svc2.state);
    }

    @Test
    public void testFailoverFromStandbyToStandby() throws Exception {
        DummyHAService svc1 = new DummyHAService(HAServiceState.STANDBY, svc1Addr);
        DummyHAService svc2 = new DummyHAService(HAServiceState.STANDBY, svc2Addr);
        svc1.fencer = svc2.fencer = TestNodeFencer.setupFencer(TestNodeFencer.AlwaysSucceedFencer.class.getName());
        doFailover(svc1, svc2, false, false);
        Assert.assertEquals(STANDBY, svc1.state);
        Assert.assertEquals(ACTIVE, svc2.state);
    }

    @Test
    public void testFailoverFromActiveToActive() throws Exception {
        DummyHAService svc1 = new DummyHAService(HAServiceState.ACTIVE, svc1Addr);
        DummyHAService svc2 = new DummyHAService(HAServiceState.ACTIVE, svc2Addr);
        svc1.fencer = svc2.fencer = TestNodeFencer.setupFencer(TestNodeFencer.AlwaysSucceedFencer.class.getName());
        try {
            doFailover(svc1, svc2, false, false);
            Assert.fail("Can't failover to an already active service");
        } catch (FailoverFailedException ffe) {
            // Expected
        }
        Assert.assertEquals(ACTIVE, svc1.state);
        Assert.assertEquals(ACTIVE, svc2.state);
    }

    @Test
    public void testFailoverWithoutPermission() throws Exception {
        DummyHAService svc1 = new DummyHAService(HAServiceState.ACTIVE, svc1Addr);
        Mockito.doThrow(new AccessControlException("Access denied")).when(svc1.proxy).getServiceStatus();
        DummyHAService svc2 = new DummyHAService(HAServiceState.STANDBY, svc2Addr);
        Mockito.doThrow(new AccessControlException("Access denied")).when(svc2.proxy).getServiceStatus();
        svc1.fencer = svc2.fencer = TestNodeFencer.setupFencer(TestNodeFencer.AlwaysSucceedFencer.class.getName());
        try {
            doFailover(svc1, svc2, false, false);
            Assert.fail("Can't failover when access is denied");
        } catch (FailoverFailedException ffe) {
            Assert.assertTrue(ffe.getCause().getMessage().contains("Access denied"));
        }
    }

    @Test
    public void testFailoverToUnreadyService() throws Exception {
        DummyHAService svc1 = new DummyHAService(HAServiceState.ACTIVE, svc1Addr);
        DummyHAService svc2 = new DummyHAService(HAServiceState.STANDBY, svc2Addr);
        Mockito.doReturn(STATE_NOT_READY).when(svc2.proxy).getServiceStatus();
        svc1.fencer = svc2.fencer = TestNodeFencer.setupFencer(TestNodeFencer.AlwaysSucceedFencer.class.getName());
        try {
            doFailover(svc1, svc2, false, false);
            Assert.fail("Can't failover to a service that's not ready");
        } catch (FailoverFailedException ffe) {
            // Expected
            if (!(ffe.getMessage().contains("injected not ready"))) {
                throw ffe;
            }
        }
        Assert.assertEquals(ACTIVE, svc1.state);
        Assert.assertEquals(STANDBY, svc2.state);
        // Forcing it means we ignore readyToBecomeActive
        doFailover(svc1, svc2, false, true);
        Assert.assertEquals(STANDBY, svc1.state);
        Assert.assertEquals(ACTIVE, svc2.state);
    }

    @Test
    public void testFailoverToUnhealthyServiceFailsAndFailsback() throws Exception {
        DummyHAService svc1 = new DummyHAService(HAServiceState.ACTIVE, svc1Addr);
        DummyHAService svc2 = new DummyHAService(HAServiceState.STANDBY, svc2Addr);
        Mockito.doThrow(new HealthCheckFailedException("Failed!")).when(svc2.proxy).monitorHealth();
        svc1.fencer = svc2.fencer = TestNodeFencer.setupFencer(TestNodeFencer.AlwaysSucceedFencer.class.getName());
        try {
            doFailover(svc1, svc2, false, false);
            Assert.fail("Failover to unhealthy service");
        } catch (FailoverFailedException ffe) {
            // Expected
        }
        Assert.assertEquals(ACTIVE, svc1.state);
        Assert.assertEquals(STANDBY, svc2.state);
    }

    @Test
    public void testFailoverFromFaultyServiceSucceeds() throws Exception {
        DummyHAService svc1 = new DummyHAService(HAServiceState.ACTIVE, svc1Addr);
        Mockito.doThrow(new ServiceFailedException("Failed!")).when(svc1.proxy).transitionToStandby(anyReqInfo());
        DummyHAService svc2 = new DummyHAService(HAServiceState.STANDBY, svc2Addr);
        svc1.fencer = svc2.fencer = TestNodeFencer.setupFencer(TestNodeFencer.AlwaysSucceedFencer.class.getName());
        fenceCalled = 0;
        try {
            doFailover(svc1, svc2, false, false);
        } catch (FailoverFailedException ffe) {
            Assert.fail("Faulty active prevented failover");
        }
        // svc1 still thinks it's active, that's OK, it was fenced
        Assert.assertEquals(1, fenceCalled);
        Assert.assertSame(svc1, fencedSvc);
        Assert.assertEquals(ACTIVE, svc1.state);
        Assert.assertEquals(ACTIVE, svc2.state);
    }

    @Test
    public void testFailoverFromFaultyServiceFencingFailure() throws Exception {
        DummyHAService svc1 = new DummyHAService(HAServiceState.ACTIVE, svc1Addr);
        Mockito.doThrow(new ServiceFailedException("Failed!")).when(svc1.proxy).transitionToStandby(anyReqInfo());
        DummyHAService svc2 = new DummyHAService(HAServiceState.STANDBY, svc2Addr);
        svc1.fencer = svc2.fencer = TestNodeFencer.setupFencer(TestNodeFencer.AlwaysFailFencer.class.getName());
        TestNodeFencer.AlwaysFailFencer.fenceCalled = 0;
        try {
            doFailover(svc1, svc2, false, false);
            Assert.fail("Failed over even though fencing failed");
        } catch (FailoverFailedException ffe) {
            // Expected
        }
        Assert.assertEquals(1, TestNodeFencer.AlwaysFailFencer.fenceCalled);
        Assert.assertSame(svc1, TestNodeFencer.AlwaysFailFencer.fencedSvc);
        Assert.assertEquals(ACTIVE, svc1.state);
        Assert.assertEquals(STANDBY, svc2.state);
    }

    @Test
    public void testFencingFailureDuringFailover() throws Exception {
        DummyHAService svc1 = new DummyHAService(HAServiceState.ACTIVE, svc1Addr);
        DummyHAService svc2 = new DummyHAService(HAServiceState.STANDBY, svc2Addr);
        svc1.fencer = svc2.fencer = TestNodeFencer.setupFencer(TestNodeFencer.AlwaysFailFencer.class.getName());
        TestNodeFencer.AlwaysFailFencer.fenceCalled = 0;
        try {
            doFailover(svc1, svc2, true, false);
            Assert.fail("Failed over even though fencing requested and failed");
        } catch (FailoverFailedException ffe) {
            // Expected
        }
        // If fencing was requested and it failed we don't try to make
        // svc2 active anyway, and we don't failback to svc1.
        Assert.assertEquals(1, TestNodeFencer.AlwaysFailFencer.fenceCalled);
        Assert.assertSame(svc1, TestNodeFencer.AlwaysFailFencer.fencedSvc);
        Assert.assertEquals(STANDBY, svc1.state);
        Assert.assertEquals(STANDBY, svc2.state);
    }

    @Test
    public void testFailoverFromNonExistantServiceWithFencer() throws Exception {
        DummyHAService svc1 = Mockito.spy(new DummyHAService(null, svc1Addr));
        // Getting a proxy to a dead server will throw IOException on call,
        // not on creation of the proxy.
        HAServiceProtocol errorThrowingProxy = Mockito.mock(HAServiceProtocol.class, Mockito.withSettings().defaultAnswer(new ThrowsException(new IOException("Could not connect to host"))).extraInterfaces(Closeable.class));
        Mockito.doNothing().when(((Closeable) (errorThrowingProxy))).close();
        Mockito.doReturn(errorThrowingProxy).when(svc1).getProxy(Mockito.<Configuration>any(), Mockito.anyInt());
        DummyHAService svc2 = new DummyHAService(HAServiceState.STANDBY, svc2Addr);
        svc1.fencer = svc2.fencer = TestNodeFencer.setupFencer(TestNodeFencer.AlwaysSucceedFencer.class.getName());
        try {
            doFailover(svc1, svc2, false, false);
        } catch (FailoverFailedException ffe) {
            Assert.fail("Non-existant active prevented failover");
        }
        // Verify that the proxy created to try to make it go to standby
        // gracefully used the right rpc timeout
        Mockito.verify(svc1).getProxy(Mockito.<Configuration>any(), Mockito.eq(HA_FC_GRACEFUL_FENCE_TIMEOUT_DEFAULT));
        // Don't check svc1 because we can't reach it, but that's OK, it's been fenced.
        Assert.assertEquals(ACTIVE, svc2.state);
    }

    @Test
    public void testFailoverToNonExistantServiceFails() throws Exception {
        DummyHAService svc1 = new DummyHAService(HAServiceState.ACTIVE, svc1Addr);
        DummyHAService svc2 = Mockito.spy(new DummyHAService(null, svc2Addr));
        Mockito.doThrow(new IOException("Failed to connect")).when(svc2).getProxy(Mockito.<Configuration>any(), Mockito.anyInt());
        svc1.fencer = svc2.fencer = TestNodeFencer.setupFencer(TestNodeFencer.AlwaysSucceedFencer.class.getName());
        try {
            doFailover(svc1, svc2, false, false);
            Assert.fail("Failed over to a non-existant standby");
        } catch (FailoverFailedException ffe) {
            // Expected
        }
        Assert.assertEquals(ACTIVE, svc1.state);
    }

    @Test
    public void testFailoverToFaultyServiceFailsbackOK() throws Exception {
        DummyHAService svc1 = Mockito.spy(new DummyHAService(HAServiceState.ACTIVE, svc1Addr));
        DummyHAService svc2 = new DummyHAService(HAServiceState.STANDBY, svc2Addr);
        Mockito.doThrow(new ServiceFailedException("Failed!")).when(svc2.proxy).transitionToActive(anyReqInfo());
        svc1.fencer = svc2.fencer = TestNodeFencer.setupFencer(TestNodeFencer.AlwaysSucceedFencer.class.getName());
        try {
            doFailover(svc1, svc2, false, false);
            Assert.fail("Failover to already active service");
        } catch (FailoverFailedException ffe) {
            // Expected
        }
        // svc1 went standby then back to active
        Mockito.verify(svc1.proxy).transitionToStandby(anyReqInfo());
        Mockito.verify(svc1.proxy).transitionToActive(anyReqInfo());
        Assert.assertEquals(ACTIVE, svc1.state);
        Assert.assertEquals(STANDBY, svc2.state);
    }

    @Test
    public void testWeDontFailbackIfActiveWasFenced() throws Exception {
        DummyHAService svc1 = new DummyHAService(HAServiceState.ACTIVE, svc1Addr);
        DummyHAService svc2 = new DummyHAService(HAServiceState.STANDBY, svc2Addr);
        Mockito.doThrow(new ServiceFailedException("Failed!")).when(svc2.proxy).transitionToActive(anyReqInfo());
        svc1.fencer = svc2.fencer = TestNodeFencer.setupFencer(TestNodeFencer.AlwaysSucceedFencer.class.getName());
        try {
            doFailover(svc1, svc2, true, false);
            Assert.fail("Failed over to service that won't transition to active");
        } catch (FailoverFailedException ffe) {
            // Expected
        }
        // We failed to failover and did not failback because we fenced
        // svc1 (we forced it), therefore svc1 and svc2 should be standby.
        Assert.assertEquals(STANDBY, svc1.state);
        Assert.assertEquals(STANDBY, svc2.state);
    }

    @Test
    public void testWeFenceOnFailbackIfTransitionToActiveFails() throws Exception {
        DummyHAService svc1 = new DummyHAService(HAServiceState.ACTIVE, svc1Addr);
        DummyHAService svc2 = new DummyHAService(HAServiceState.STANDBY, svc2Addr);
        Mockito.doThrow(new ServiceFailedException("Failed!")).when(svc2.proxy).transitionToActive(anyReqInfo());
        svc1.fencer = svc2.fencer = TestNodeFencer.setupFencer(TestNodeFencer.AlwaysSucceedFencer.class.getName());
        fenceCalled = 0;
        try {
            doFailover(svc1, svc2, false, false);
            Assert.fail("Failed over to service that won't transition to active");
        } catch (FailoverFailedException ffe) {
            // Expected
        }
        // We failed to failover. We did not fence svc1 because it cooperated
        // and we didn't force it, so we failed back to svc1 and fenced svc2.
        // Note svc2 still thinks it's active, that's OK, we fenced it.
        Assert.assertEquals(ACTIVE, svc1.state);
        Assert.assertEquals(1, fenceCalled);
        Assert.assertSame(svc2, fencedSvc);
    }

    @Test
    public void testFailureToFenceOnFailbackFailsTheFailback() throws Exception {
        DummyHAService svc1 = new DummyHAService(HAServiceState.ACTIVE, svc1Addr);
        DummyHAService svc2 = new DummyHAService(HAServiceState.STANDBY, svc2Addr);
        Mockito.doThrow(new IOException("Failed!")).when(svc2.proxy).transitionToActive(anyReqInfo());
        svc1.fencer = svc2.fencer = TestNodeFencer.setupFencer(TestNodeFencer.AlwaysFailFencer.class.getName());
        TestNodeFencer.AlwaysFailFencer.fenceCalled = 0;
        try {
            doFailover(svc1, svc2, false, false);
            Assert.fail("Failed over to service that won't transition to active");
        } catch (FailoverFailedException ffe) {
            // Expected
        }
        // We did not fence svc1 because it cooperated and we didn't force it,
        // we failed to failover so we fenced svc2, we failed to fence svc2
        // so we did not failback to svc1, ie it's still standby.
        Assert.assertEquals(STANDBY, svc1.state);
        Assert.assertEquals(1, TestNodeFencer.AlwaysFailFencer.fenceCalled);
        Assert.assertSame(svc2, TestNodeFencer.AlwaysFailFencer.fencedSvc);
    }

    @Test
    public void testFailbackToFaultyServiceFails() throws Exception {
        DummyHAService svc1 = new DummyHAService(HAServiceState.ACTIVE, svc1Addr);
        Mockito.doThrow(new ServiceFailedException("Failed!")).when(svc1.proxy).transitionToActive(anyReqInfo());
        DummyHAService svc2 = new DummyHAService(HAServiceState.STANDBY, svc2Addr);
        Mockito.doThrow(new ServiceFailedException("Failed!")).when(svc2.proxy).transitionToActive(anyReqInfo());
        svc1.fencer = svc2.fencer = TestNodeFencer.setupFencer(TestNodeFencer.AlwaysSucceedFencer.class.getName());
        try {
            doFailover(svc1, svc2, false, false);
            Assert.fail("Failover to already active service");
        } catch (FailoverFailedException ffe) {
            // Expected
        }
        Assert.assertEquals(STANDBY, svc1.state);
        Assert.assertEquals(STANDBY, svc2.state);
    }

    @Test
    public void testSelfFailoverFails() throws Exception {
        DummyHAService svc1 = new DummyHAService(HAServiceState.ACTIVE, svc1Addr);
        DummyHAService svc2 = new DummyHAService(HAServiceState.STANDBY, svc2Addr);
        svc1.fencer = svc2.fencer = TestNodeFencer.setupFencer(TestNodeFencer.AlwaysSucceedFencer.class.getName());
        fenceCalled = 0;
        try {
            doFailover(svc1, svc1, false, false);
            Assert.fail("Can't failover to yourself");
        } catch (FailoverFailedException ffe) {
            // Expected
        }
        Assert.assertEquals(0, fenceCalled);
        Assert.assertEquals(ACTIVE, svc1.state);
        try {
            doFailover(svc2, svc2, false, false);
            Assert.fail("Can't failover to yourself");
        } catch (FailoverFailedException ffe) {
            // Expected
        }
        Assert.assertEquals(0, fenceCalled);
        Assert.assertEquals(STANDBY, svc2.state);
    }
}

