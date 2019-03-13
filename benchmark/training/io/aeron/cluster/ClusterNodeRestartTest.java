/**
 * Copyright 2014-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.aeron.cluster;


import ClusterControl.ToggleState.NEUTRAL;
import ClusterControl.ToggleState.SNAPSHOT;
import io.aeron.Counter;
import io.aeron.cluster.client.AeronCluster;
import io.aeron.cluster.service.ClusteredServiceContainer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.agrona.ExpandableArrayBuffer;
import org.agrona.concurrent.status.AtomicCounter;
import org.agrona.concurrent.status.CountersReader;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ClusterNodeRestartTest {
    private static final long MAX_CATALOG_ENTRIES = 1024;

    private static final int MESSAGE_LENGTH = SIZE_OF_INT;

    private static final int TIMER_MESSAGE_LENGTH = ((SIZE_OF_INT) + (SIZE_OF_LONG)) + (SIZE_OF_LONG);

    private static final int MESSAGE_VALUE_OFFSET = 0;

    private static final int TIMER_MESSAGE_ID_OFFSET = (ClusterNodeRestartTest.MESSAGE_VALUE_OFFSET) + (SIZE_OF_INT);

    private static final int TIMER_MESSAGE_DELAY_OFFSET = (ClusterNodeRestartTest.TIMER_MESSAGE_ID_OFFSET) + (SIZE_OF_LONG);

    private ClusteredMediaDriver clusteredMediaDriver;

    private ClusteredServiceContainer container;

    private final ExpandableArrayBuffer msgBuffer = new ExpandableArrayBuffer();

    private AeronCluster aeronCluster;

    private final AtomicReference<String> serviceState = new AtomicReference<>();

    private final AtomicLong snapshotCount = new AtomicLong();

    private final Counter mockSnapshotCounter = Mockito.mock(Counter.class);

    @Test(timeout = 10000)
    public void shouldRestartServiceWithReplay() {
        final AtomicLong serviceMsgCounter = new AtomicLong(0);
        final AtomicLong restartServiceMsgCounter = new AtomicLong(0);
        launchService(serviceMsgCounter);
        connectClient();
        sendCountedMessageIntoCluster(0);
        while ((serviceMsgCounter.get()) == 0) {
            TestUtil.checkInterruptedStatus();
            Thread.yield();
        } 
        forceCloseForRestart();
        launchClusteredMediaDriver(false);
        launchService(restartServiceMsgCounter);
        connectClient();
        while ((restartServiceMsgCounter.get()) == 0) {
            TestUtil.checkInterruptedStatus();
            Thread.yield();
        } 
    }

    @Test(timeout = 10000)
    public void shouldRestartServiceWithReplayAndContinue() {
        final AtomicLong serviceMsgCounter = new AtomicLong(0);
        final AtomicLong restartServiceMsgCounter = new AtomicLong(0);
        launchService(serviceMsgCounter);
        connectClient();
        sendCountedMessageIntoCluster(0);
        while ((serviceMsgCounter.get()) == 0) {
            TestUtil.checkInterruptedStatus();
            Thread.yield();
        } 
        forceCloseForRestart();
        launchClusteredMediaDriver(false);
        launchService(restartServiceMsgCounter);
        connectClient();
        sendCountedMessageIntoCluster(1);
        while ((restartServiceMsgCounter.get()) == 1) {
            TestUtil.checkInterruptedStatus();
            Thread.yield();
        } 
    }

    @Test(timeout = 10000)
    public void shouldRestartServiceFromEmptySnapshot() throws Exception {
        final AtomicLong serviceMsgCounter = new AtomicLong(0);
        launchService(serviceMsgCounter);
        connectClient();
        final CountersReader counters = container.context().aeron().countersReader();
        final AtomicCounter controlToggle = ClusterControl.findControlToggle(counters);
        Assert.assertNotNull(controlToggle);
        Assert.assertTrue(SNAPSHOT.toggle(controlToggle));
        while ((snapshotCount.get()) == 0) {
            TestUtil.checkInterruptedStatus();
            Thread.sleep(1);
        } 
        forceCloseForRestart();
        serviceState.set(null);
        launchClusteredMediaDriver(false);
        launchService(serviceMsgCounter);
        connectClient();
        while (null == (serviceState.get())) {
            TestUtil.checkInterruptedStatus();
            Thread.yield();
        } 
        Assert.assertThat(serviceState.get(), CoreMatchers.is("0"));
    }

    @Test(timeout = 10000)
    public void shouldRestartServiceFromSnapshot() throws Exception {
        final AtomicLong serviceMsgCounter = new AtomicLong(0);
        launchService(serviceMsgCounter);
        connectClient();
        sendCountedMessageIntoCluster(0);
        sendCountedMessageIntoCluster(1);
        sendCountedMessageIntoCluster(2);
        while ((serviceMsgCounter.get()) != 3) {
            Thread.yield();
        } 
        final CountersReader counters = aeronCluster.context().aeron().countersReader();
        final AtomicCounter controlToggle = ClusterControl.findControlToggle(counters);
        Assert.assertNotNull(controlToggle);
        Assert.assertTrue(SNAPSHOT.toggle(controlToggle));
        while ((snapshotCount.get()) == 0) {
            TestUtil.checkInterruptedStatus();
            Thread.sleep(1);
        } 
        forceCloseForRestart();
        serviceState.set(null);
        launchClusteredMediaDriver(false);
        launchService(serviceMsgCounter);
        connectClient();
        while (null == (serviceState.get())) {
            TestUtil.checkInterruptedStatus();
            Thread.yield();
        } 
        Assert.assertThat(serviceState.get(), CoreMatchers.is("3"));
    }

    @Test(timeout = 10000)
    public void shouldRestartServiceFromSnapshotWithFurtherLog() throws Exception {
        final AtomicLong serviceMsgCounter = new AtomicLong(0);
        launchService(serviceMsgCounter);
        connectClient();
        sendCountedMessageIntoCluster(0);
        sendCountedMessageIntoCluster(1);
        sendCountedMessageIntoCluster(2);
        while ((serviceMsgCounter.get()) != 3) {
            Thread.yield();
        } 
        final CountersReader counters = aeronCluster.context().aeron().countersReader();
        final AtomicCounter controlToggle = ClusterControl.findControlToggle(counters);
        Assert.assertNotNull(controlToggle);
        Assert.assertTrue(SNAPSHOT.toggle(controlToggle));
        while ((snapshotCount.get()) == 0) {
            TestUtil.checkInterruptedStatus();
            Thread.sleep(1);
        } 
        sendCountedMessageIntoCluster(3);
        while ((serviceMsgCounter.get()) != 4) {
            TestUtil.checkInterruptedStatus();
            Thread.yield();
        } 
        forceCloseForRestart();
        serviceMsgCounter.set(0);
        launchClusteredMediaDriver(false);
        launchService(serviceMsgCounter);
        connectClient();
        while ((serviceMsgCounter.get()) != 1) {
            TestUtil.checkInterruptedStatus();
            Thread.yield();
        } 
        Assert.assertThat(serviceState.get(), CoreMatchers.is("4"));
    }

    @Test(timeout = 10000)
    public void shouldTakeMultipleSnapshots() throws Exception {
        final AtomicLong serviceMsgCounter = new AtomicLong(0);
        launchService(serviceMsgCounter);
        connectClient();
        final CountersReader counters = aeronCluster.context().aeron().countersReader();
        final AtomicCounter controlToggle = ClusterControl.findControlToggle(counters);
        Assert.assertNotNull(controlToggle);
        for (int i = 0; i < 3; i++) {
            Assert.assertTrue(SNAPSHOT.toggle(controlToggle));
            while ((controlToggle.get()) != (NEUTRAL.code())) {
                TestUtil.checkInterruptedStatus();
                Thread.sleep(1);
            } 
        }
        Assert.assertThat(snapshotCount.get(), CoreMatchers.is(3L));
    }

    @Test(timeout = 10000)
    public void shouldRestartServiceWithTimerFromSnapshotWithFurtherLog() throws Exception {
        final AtomicLong serviceMsgCounter = new AtomicLong(0);
        launchService(serviceMsgCounter);
        connectClient();
        sendCountedMessageIntoCluster(0);
        sendCountedMessageIntoCluster(1);
        sendCountedMessageIntoCluster(2);
        sendTimerMessageIntoCluster(3, 1, TimeUnit.HOURS.toMillis(10));
        while ((serviceMsgCounter.get()) != 4) {
            Thread.yield();
        } 
        final CountersReader counters = aeronCluster.context().aeron().countersReader();
        final AtomicCounter controlToggle = ClusterControl.findControlToggle(counters);
        Assert.assertNotNull(controlToggle);
        Assert.assertTrue(SNAPSHOT.toggle(controlToggle));
        while ((snapshotCount.get()) == 0) {
            TestUtil.checkInterruptedStatus();
            Thread.sleep(1);
        } 
        sendCountedMessageIntoCluster(4);
        while ((serviceMsgCounter.get()) != 5) {
            TestUtil.checkInterruptedStatus();
            Thread.yield();
        } 
        forceCloseForRestart();
        serviceMsgCounter.set(0);
        launchClusteredMediaDriver(false);
        launchService(serviceMsgCounter);
        connectClient();
        while ((serviceMsgCounter.get()) != 1) {
            TestUtil.checkInterruptedStatus();
            Thread.yield();
        } 
        Assert.assertThat(serviceState.get(), CoreMatchers.is("5"));
    }
}

