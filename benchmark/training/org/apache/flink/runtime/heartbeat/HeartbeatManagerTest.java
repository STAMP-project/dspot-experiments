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
package org.apache.flink.runtime.heartbeat;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.flink.core.testutils.OneShotLatch;
import org.apache.flink.runtime.clusterframework.types.ResourceID;
import org.apache.flink.runtime.concurrent.Executors;
import org.apache.flink.runtime.concurrent.ScheduledExecutor;
import org.apache.flink.runtime.concurrent.ScheduledExecutorServiceAdapter;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for the {@link HeartbeatManager}.
 */
public class HeartbeatManagerTest extends TestLogger {
    private static final Logger LOG = LoggerFactory.getLogger(HeartbeatManagerTest.class);

    /**
     * Tests that regular heartbeat signal triggers the right callback functions in the
     * {@link HeartbeatListener}.
     */
    @Test
    public void testRegularHeartbeat() {
        long heartbeatTimeout = 1000L;
        ResourceID ownResourceID = new ResourceID("foobar");
        ResourceID targetResourceID = new ResourceID("barfoo");
        @SuppressWarnings("unchecked")
        HeartbeatListener<Object, Object> heartbeatListener = Mockito.mock(HeartbeatListener.class);
        ScheduledExecutor scheduledExecutor = Mockito.mock(ScheduledExecutor.class);
        Object expectedObject = new Object();
        Mockito.when(heartbeatListener.retrievePayload(ArgumentMatchers.any(ResourceID.class))).thenReturn(CompletableFuture.completedFuture(expectedObject));
        HeartbeatManagerImpl<Object, Object> heartbeatManager = new HeartbeatManagerImpl(heartbeatTimeout, ownResourceID, heartbeatListener, Executors.directExecutor(), scheduledExecutor, HeartbeatManagerTest.LOG);
        @SuppressWarnings("unchecked")
        HeartbeatTarget<Object> heartbeatTarget = Mockito.mock(HeartbeatTarget.class);
        heartbeatManager.monitorTarget(targetResourceID, heartbeatTarget);
        heartbeatManager.requestHeartbeat(targetResourceID, expectedObject);
        Mockito.verify(heartbeatListener, Mockito.times(1)).reportPayload(targetResourceID, expectedObject);
        Mockito.verify(heartbeatListener, Mockito.times(1)).retrievePayload(ArgumentMatchers.any(ResourceID.class));
        Mockito.verify(heartbeatTarget, Mockito.times(1)).receiveHeartbeat(ownResourceID, expectedObject);
        heartbeatManager.receiveHeartbeat(targetResourceID, expectedObject);
        Mockito.verify(heartbeatListener, Mockito.times(2)).reportPayload(targetResourceID, expectedObject);
    }

    /**
     * Tests that the heartbeat monitors are updated when receiving a new heartbeat signal.
     */
    @Test
    public void testHeartbeatMonitorUpdate() {
        long heartbeatTimeout = 1000L;
        ResourceID ownResourceID = new ResourceID("foobar");
        ResourceID targetResourceID = new ResourceID("barfoo");
        @SuppressWarnings("unchecked")
        HeartbeatListener<Object, Object> heartbeatListener = Mockito.mock(HeartbeatListener.class);
        ScheduledExecutor scheduledExecutor = Mockito.mock(ScheduledExecutor.class);
        ScheduledFuture<?> scheduledFuture = Mockito.mock(ScheduledFuture.class);
        Mockito.doReturn(scheduledFuture).when(scheduledExecutor).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        Object expectedObject = new Object();
        Mockito.when(heartbeatListener.retrievePayload(ArgumentMatchers.any(ResourceID.class))).thenReturn(CompletableFuture.completedFuture(expectedObject));
        HeartbeatManagerImpl<Object, Object> heartbeatManager = new HeartbeatManagerImpl(heartbeatTimeout, ownResourceID, heartbeatListener, Executors.directExecutor(), scheduledExecutor, HeartbeatManagerTest.LOG);
        @SuppressWarnings("unchecked")
        HeartbeatTarget<Object> heartbeatTarget = Mockito.mock(HeartbeatTarget.class);
        heartbeatManager.monitorTarget(targetResourceID, heartbeatTarget);
        heartbeatManager.receiveHeartbeat(targetResourceID, expectedObject);
        Mockito.verify(scheduledFuture, Mockito.times(1)).cancel(true);
        Mockito.verify(scheduledExecutor, Mockito.times(2)).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.eq(heartbeatTimeout), ArgumentMatchers.eq(TimeUnit.MILLISECONDS));
    }

    /**
     * Tests that a heartbeat timeout is signaled if the heartbeat is not reported in time.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHeartbeatTimeout() throws Exception {
        long heartbeatTimeout = 100L;
        int numHeartbeats = 10;
        long heartbeatInterval = 20L;
        Object payload = new Object();
        ResourceID ownResourceID = new ResourceID("foobar");
        ResourceID targetResourceID = new ResourceID("barfoo");
        HeartbeatManagerTest.TestingHeartbeatListener heartbeatListener = new HeartbeatManagerTest.TestingHeartbeatListener(payload);
        ScheduledExecutorService scheduledExecutorService = Mockito.mock(ScheduledExecutorService.class);
        ScheduledFuture<?> scheduledFuture = Mockito.mock(ScheduledFuture.class);
        Mockito.doReturn(scheduledFuture).when(scheduledExecutorService).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        Object expectedObject = new Object();
        HeartbeatManagerImpl<Object, Object> heartbeatManager = new HeartbeatManagerImpl(heartbeatTimeout, ownResourceID, heartbeatListener, Executors.directExecutor(), new ScheduledExecutorServiceAdapter(new ScheduledThreadPoolExecutor(1)), HeartbeatManagerTest.LOG);
        @SuppressWarnings("unchecked")
        HeartbeatTarget<Object> heartbeatTarget = Mockito.mock(HeartbeatTarget.class);
        CompletableFuture<ResourceID> timeoutFuture = heartbeatListener.getTimeoutFuture();
        heartbeatManager.monitorTarget(targetResourceID, heartbeatTarget);
        for (int i = 0; i < numHeartbeats; i++) {
            heartbeatManager.receiveHeartbeat(targetResourceID, expectedObject);
            Thread.sleep(heartbeatInterval);
        }
        Assert.assertFalse(timeoutFuture.isDone());
        ResourceID timeoutResourceID = timeoutFuture.get((2 * heartbeatTimeout), TimeUnit.MILLISECONDS);
        Assert.assertEquals(targetResourceID, timeoutResourceID);
    }

    /**
     * Tests the heartbeat interplay between the {@link HeartbeatManagerImpl} and the
     * {@link HeartbeatManagerSenderImpl}. The sender should regularly trigger heartbeat requests
     * which are fulfilled by the receiver. Upon stopping the receiver, the sender should notify
     * the heartbeat listener about the heartbeat timeout.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHeartbeatCluster() throws Exception {
        long heartbeatTimeout = 100L;
        long heartbeatPeriod = 20L;
        Object object = new Object();
        Object object2 = new Object();
        ResourceID resourceID = new ResourceID("foobar");
        ResourceID resourceID2 = new ResourceID("barfoo");
        @SuppressWarnings("unchecked")
        HeartbeatListener<Object, Object> heartbeatListener = Mockito.mock(HeartbeatListener.class);
        Mockito.when(heartbeatListener.retrievePayload(ArgumentMatchers.any(ResourceID.class))).thenReturn(CompletableFuture.completedFuture(object));
        HeartbeatManagerTest.TestingHeartbeatListener heartbeatListener2 = new HeartbeatManagerTest.TestingHeartbeatListener(object2);
        CompletableFuture<ResourceID> futureTimeout = heartbeatListener2.getTimeoutFuture();
        HeartbeatManagerImpl<Object, Object> heartbeatManager = new HeartbeatManagerImpl(heartbeatTimeout, resourceID, heartbeatListener, Executors.directExecutor(), new ScheduledExecutorServiceAdapter(new ScheduledThreadPoolExecutor(1)), HeartbeatManagerTest.LOG);
        HeartbeatManagerSenderImpl<Object, Object> heartbeatManager2 = new HeartbeatManagerSenderImpl(heartbeatPeriod, heartbeatTimeout, resourceID2, heartbeatListener2, Executors.directExecutor(), new ScheduledExecutorServiceAdapter(new ScheduledThreadPoolExecutor(1)), HeartbeatManagerTest.LOG);
        heartbeatManager.monitorTarget(resourceID2, heartbeatManager2);
        heartbeatManager2.monitorTarget(resourceID, heartbeatManager);
        Thread.sleep((2 * heartbeatTimeout));
        Assert.assertFalse(futureTimeout.isDone());
        heartbeatManager.stop();
        ResourceID timeoutResourceID = futureTimeout.get((2 * heartbeatTimeout), TimeUnit.MILLISECONDS);
        Assert.assertEquals(resourceID, timeoutResourceID);
        int numberHeartbeats = ((int) ((2 * heartbeatTimeout) / heartbeatPeriod));
        Mockito.verify(heartbeatListener, Mockito.atLeast((numberHeartbeats / 2))).reportPayload(resourceID2, object2);
        Assert.assertTrue(((heartbeatListener2.getNumberHeartbeatReports()) >= (numberHeartbeats / 2)));
    }

    /**
     * Tests that after unmonitoring a target, there won't be a timeout triggered.
     */
    @Test
    public void testTargetUnmonitoring() throws InterruptedException, ExecutionException {
        // this might be too aggressive for Travis, let's see...
        long heartbeatTimeout = 100L;
        ResourceID resourceID = new ResourceID("foobar");
        ResourceID targetID = new ResourceID("target");
        Object object = new Object();
        HeartbeatManagerTest.TestingHeartbeatListener heartbeatListener = new HeartbeatManagerTest.TestingHeartbeatListener(object);
        HeartbeatManager<Object, Object> heartbeatManager = new HeartbeatManagerImpl(heartbeatTimeout, resourceID, heartbeatListener, Executors.directExecutor(), new ScheduledExecutorServiceAdapter(new ScheduledThreadPoolExecutor(1)), HeartbeatManagerTest.LOG);
        @SuppressWarnings("unchecked")
        final HeartbeatTarget<Object> heartbeatTarget = Mockito.mock(HeartbeatTarget.class);
        heartbeatManager.monitorTarget(targetID, heartbeatTarget);
        heartbeatManager.unmonitorTarget(targetID);
        CompletableFuture<ResourceID> timeout = heartbeatListener.getTimeoutFuture();
        try {
            timeout.get((2 * heartbeatTimeout), TimeUnit.MILLISECONDS);
            Assert.fail("Timeout should time out.");
        } catch (TimeoutException e) {
            // the timeout should not be completed since we unmonitored the target
        }
    }

    /**
     * Tests that the last heartbeat from an unregistered target equals -1.
     */
    @Test
    public void testLastHeartbeatFromUnregisteredTarget() {
        final long heartbeatTimeout = 100L;
        final ResourceID resourceId = ResourceID.generate();
        @SuppressWarnings("unchecked")
        final HeartbeatListener<Object, Object> heartbeatListener = Mockito.mock(HeartbeatListener.class);
        HeartbeatManager<?, ?> heartbeatManager = new HeartbeatManagerImpl(heartbeatTimeout, resourceId, heartbeatListener, Executors.directExecutor(), Mockito.mock(ScheduledExecutor.class), HeartbeatManagerTest.LOG);
        try {
            Assert.assertEquals((-1L), heartbeatManager.getLastHeartbeatFrom(ResourceID.generate()));
        } finally {
            heartbeatManager.stop();
        }
    }

    /**
     * Tests that we can correctly retrieve the last heartbeat for registered targets.
     */
    @Test
    public void testLastHeartbeatFrom() {
        final long heartbeatTimeout = 100L;
        final ResourceID resourceId = ResourceID.generate();
        @SuppressWarnings("unchecked")
        final HeartbeatListener<Object, Object> heartbeatListener = Mockito.mock(HeartbeatListener.class);
        @SuppressWarnings("unchecked")
        final HeartbeatTarget<Object> heartbeatTarget = Mockito.mock(HeartbeatTarget.class);
        final ResourceID target = ResourceID.generate();
        HeartbeatManager<Object, Object> heartbeatManager = new HeartbeatManagerImpl(heartbeatTimeout, resourceId, heartbeatListener, Executors.directExecutor(), Mockito.mock(ScheduledExecutor.class), HeartbeatManagerTest.LOG);
        try {
            heartbeatManager.monitorTarget(target, heartbeatTarget);
            Assert.assertEquals(0L, heartbeatManager.getLastHeartbeatFrom(target));
            final long currentTime = System.currentTimeMillis();
            heartbeatManager.receiveHeartbeat(target, null);
            Assert.assertTrue(((heartbeatManager.getLastHeartbeatFrom(target)) >= currentTime));
        } finally {
            heartbeatManager.stop();
        }
    }

    /**
     * Tests that the heartbeat target {@link ResourceID} is properly passed to the {@link HeartbeatListener} by the
     * {@link HeartbeatManagerImpl}.
     */
    @Test
    public void testHeartbeatManagerTargetPayload() {
        final long heartbeatTimeout = 100L;
        final ResourceID someTargetId = ResourceID.generate();
        final ResourceID specialTargetId = ResourceID.generate();
        final HeartbeatManagerTest.TargetDependentHeartbeatReceiver someHeartbeatTarget = new HeartbeatManagerTest.TargetDependentHeartbeatReceiver();
        final HeartbeatManagerTest.TargetDependentHeartbeatReceiver specialHeartbeatTarget = new HeartbeatManagerTest.TargetDependentHeartbeatReceiver();
        final int defaultResponse = 0;
        final int specialResponse = 1;
        HeartbeatManager<?, Integer> heartbeatManager = new HeartbeatManagerImpl(heartbeatTimeout, ResourceID.generate(), new HeartbeatManagerTest.TargetDependentHeartbeatSender(specialTargetId, specialResponse, defaultResponse), Executors.directExecutor(), Mockito.mock(ScheduledExecutor.class), HeartbeatManagerTest.LOG);
        try {
            heartbeatManager.monitorTarget(someTargetId, someHeartbeatTarget);
            heartbeatManager.monitorTarget(specialTargetId, specialHeartbeatTarget);
            heartbeatManager.requestHeartbeat(someTargetId, null);
            Assert.assertEquals(defaultResponse, someHeartbeatTarget.getLastReceivedHeartbeatPayload());
            heartbeatManager.requestHeartbeat(specialTargetId, null);
            Assert.assertEquals(specialResponse, specialHeartbeatTarget.getLastReceivedHeartbeatPayload());
        } finally {
            heartbeatManager.stop();
        }
    }

    /**
     * Tests that the heartbeat target {@link ResourceID} is properly passed to the {@link HeartbeatListener} by the
     * {@link HeartbeatManagerSenderImpl}.
     */
    @Test
    public void testHeartbeatManagerSenderTargetPayload() throws Exception {
        final long heartbeatTimeout = 100L;
        final long heartbeatPeriod = 2000L;
        final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        final ResourceID someTargetId = ResourceID.generate();
        final ResourceID specialTargetId = ResourceID.generate();
        final OneShotLatch someTargetReceivedLatch = new OneShotLatch();
        final OneShotLatch specialTargetReceivedLatch = new OneShotLatch();
        final HeartbeatManagerTest.TargetDependentHeartbeatReceiver someHeartbeatTarget = new HeartbeatManagerTest.TargetDependentHeartbeatReceiver(someTargetReceivedLatch);
        final HeartbeatManagerTest.TargetDependentHeartbeatReceiver specialHeartbeatTarget = new HeartbeatManagerTest.TargetDependentHeartbeatReceiver(specialTargetReceivedLatch);
        final int defaultResponse = 0;
        final int specialResponse = 1;
        HeartbeatManager<?, Integer> heartbeatManager = new HeartbeatManagerSenderImpl(heartbeatPeriod, heartbeatTimeout, ResourceID.generate(), new HeartbeatManagerTest.TargetDependentHeartbeatSender(specialTargetId, specialResponse, defaultResponse), Executors.directExecutor(), new ScheduledExecutorServiceAdapter(scheduledThreadPoolExecutor), HeartbeatManagerTest.LOG);
        try {
            heartbeatManager.monitorTarget(someTargetId, someHeartbeatTarget);
            heartbeatManager.monitorTarget(specialTargetId, specialHeartbeatTarget);
            someTargetReceivedLatch.await(5, TimeUnit.SECONDS);
            specialTargetReceivedLatch.await(5, TimeUnit.SECONDS);
            Assert.assertEquals(defaultResponse, someHeartbeatTarget.getLastRequestedHeartbeatPayload());
            Assert.assertEquals(specialResponse, specialHeartbeatTarget.getLastRequestedHeartbeatPayload());
        } finally {
            heartbeatManager.stop();
            scheduledThreadPoolExecutor.shutdown();
        }
    }

    /**
     * Test {@link HeartbeatTarget} that exposes the last received payload.
     */
    private static class TargetDependentHeartbeatReceiver implements HeartbeatTarget<Integer> {
        private volatile int lastReceivedHeartbeatPayload = -1;

        private volatile int lastRequestedHeartbeatPayload = -1;

        private final OneShotLatch latch;

        public TargetDependentHeartbeatReceiver() {
            this(new OneShotLatch());
        }

        public TargetDependentHeartbeatReceiver(OneShotLatch latch) {
            this.latch = latch;
        }

        @Override
        public void receiveHeartbeat(ResourceID heartbeatOrigin, Integer heartbeatPayload) {
            this.lastReceivedHeartbeatPayload = heartbeatPayload;
            latch.trigger();
        }

        @Override
        public void requestHeartbeat(ResourceID requestOrigin, Integer heartbeatPayload) {
            this.lastRequestedHeartbeatPayload = heartbeatPayload;
            latch.trigger();
        }

        public int getLastReceivedHeartbeatPayload() {
            return lastReceivedHeartbeatPayload;
        }

        public int getLastRequestedHeartbeatPayload() {
            return lastRequestedHeartbeatPayload;
        }
    }

    /**
     * Test {@link HeartbeatListener} that returns different payloads based on the target {@link ResourceID}.
     */
    private static class TargetDependentHeartbeatSender implements HeartbeatListener<Object, Integer> {
        private final ResourceID specialId;

        private final int specialResponse;

        private final int defaultResponse;

        TargetDependentHeartbeatSender(ResourceID specialId, int specialResponse, int defaultResponse) {
            this.specialId = specialId;
            this.specialResponse = specialResponse;
            this.defaultResponse = defaultResponse;
        }

        @Override
        public void notifyHeartbeatTimeout(ResourceID resourceID) {
        }

        @Override
        public void reportPayload(ResourceID resourceID, Object payload) {
        }

        @Override
        public CompletableFuture<Integer> retrievePayload(ResourceID resourceID) {
            if (resourceID.equals(specialId)) {
                return CompletableFuture.completedFuture(specialResponse);
            } else {
                return CompletableFuture.completedFuture(defaultResponse);
            }
        }
    }

    static class TestingHeartbeatListener implements HeartbeatListener<Object, Object> {
        private final CompletableFuture<ResourceID> future = new CompletableFuture<>();

        private final Object payload;

        private int numberHeartbeatReports;

        TestingHeartbeatListener(Object payload) {
            this.payload = payload;
        }

        CompletableFuture<ResourceID> getTimeoutFuture() {
            return future;
        }

        public int getNumberHeartbeatReports() {
            return numberHeartbeatReports;
        }

        @Override
        public void notifyHeartbeatTimeout(ResourceID resourceID) {
            future.complete(resourceID);
        }

        @Override
        public void reportPayload(ResourceID resourceID, Object payload) {
            (numberHeartbeatReports)++;
        }

        @Override
        public CompletableFuture<Object> retrievePayload(ResourceID resourceID) {
            return CompletableFuture.completedFuture(payload);
        }
    }
}

