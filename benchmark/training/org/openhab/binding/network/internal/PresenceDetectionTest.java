/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.network.internal;


import ArpPingUtilEnum.IPUTILS_ARPING;
import IpPingMethodEnum.WINDOWS_PING;
import PresenceDetectionType.ICMP_PING;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openhab.binding.network.internal.toberemoved.cache.ExpiringCacheHelper;
import org.openhab.binding.network.internal.utils.NetworkUtils;


/**
 * Tests cases for {@see PresenceDetectionValue}
 *
 * @author David Graeff - Initial contribution
 */
public class PresenceDetectionTest {
    static long CACHETIME = 2000L;

    @Mock
    NetworkUtils networkUtils;

    @Mock
    PresenceDetectionListener listener;

    @Mock
    ExecutorService executorService;

    @Mock
    Consumer<PresenceDetectionValue> callback;

    PresenceDetection subject;

    // Depending on the amount of test methods an according amount of threads is spawned.
    // We will check if they spawn and return in time.
    @Test
    public void threadCountTest() {
        Assert.assertNull(subject.executorService);
        Mockito.doNothing().when(subject).performARPping(ArgumentMatchers.any());
        Mockito.doNothing().when(subject).performJavaPing();
        Mockito.doNothing().when(subject).performSystemPing();
        Mockito.doNothing().when(subject).performServicePing(ArgumentMatchers.anyInt());
        subject.performPresenceDetection(false);
        // Thread count: ARP + ICMP + 1*TCP
        Assert.assertThat(subject.detectionChecks, CoreMatchers.is(3));
        Assert.assertNotNull(subject.executorService);
        subject.waitForPresenceDetection();
        Assert.assertThat(subject.detectionChecks, CoreMatchers.is(0));
        Assert.assertNull(subject.executorService);
    }

    @Test
    public void partialAndFinalCallbackTests() throws IOException, InterruptedException {
        Mockito.doReturn(true).when(networkUtils).nativePing(ArgumentMatchers.eq(WINDOWS_PING), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt());
        Mockito.doReturn(true).when(networkUtils).nativeARPPing(ArgumentMatchers.eq(IPUTILS_ARPING), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        Mockito.doReturn(true).when(networkUtils).servicePing(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Assert.assertTrue(subject.performPresenceDetection(false));
        subject.waitForPresenceDetection();
        Mockito.verify(subject, Mockito.times(0)).performJavaPing();
        Mockito.verify(subject).performSystemPing();
        Mockito.verify(subject).performARPping(ArgumentMatchers.any());
        Mockito.verify(subject).performServicePing(ArgumentMatchers.anyInt());
        Mockito.verify(listener, Mockito.times(3)).partialDetectionResult(ArgumentMatchers.any());
        ArgumentCaptor<PresenceDetectionValue> capture = ArgumentCaptor.forClass(PresenceDetectionValue.class);
        Mockito.verify(listener, Mockito.times(1)).finalDetectionResult(capture.capture());
        Assert.assertThat(capture.getValue().getSuccessfulDetectionTypes(), CoreMatchers.is("ARP_PING, ICMP_PING, TCP_CONNECTION"));
    }

    @Test
    public void cacheTest() throws IOException, InterruptedException {
        Mockito.doReturn(true).when(networkUtils).nativePing(ArgumentMatchers.eq(WINDOWS_PING), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt());
        Mockito.doReturn(true).when(networkUtils).nativeARPPing(ArgumentMatchers.eq(IPUTILS_ARPING), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        Mockito.doReturn(true).when(networkUtils).servicePing(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.doReturn(executorService).when(subject).getThreadsFor(ArgumentMatchers.anyInt());
        // We expect no valid value
        Assert.assertTrue(subject.cache.isExpired());
        // Get value will issue a PresenceDetection internally.
        subject.getValue(callback);
        Mockito.verify(subject).performPresenceDetection(ArgumentMatchers.eq(false));
        Assert.assertNotNull(subject.executorService);
        // There should be no straight callback yet
        Mockito.verify(callback, Mockito.times(0)).accept(ArgumentMatchers.any());
        // Perform the different presence detection threads now
        ArgumentCaptor<Runnable> capture = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(executorService, Mockito.times(3)).execute(capture.capture());
        for (Runnable r : capture.getAllValues()) {
            r.run();
        }
        // "Wait" for the presence detection to finish
        subject.waitForPresenceDetection();
        // Although there are multiple partial results and a final result,
        // the getValue() consumers get the fastest response possible, and only once.
        Mockito.verify(callback, Mockito.times(1)).accept(ArgumentMatchers.any());
        // As long as the cache is valid, we can get the result back again
        subject.getValue(callback);
        Mockito.verify(callback, Mockito.times(2)).accept(ArgumentMatchers.any());
        // Invalidate value, we should not get a new callback immediately again
        subject.cache.invalidateValue();
        subject.getValue(callback);
        Mockito.verify(callback, Mockito.times(2)).accept(ArgumentMatchers.any());
    }

    @Test
    public void reuseValueTests() throws IOException, InterruptedException {
        final long START_TIME = 1000L;
        Mockito.when(subject.cache.getCurrentNanoTime()).thenReturn(TimeUnit.MILLISECONDS.toNanos(START_TIME));
        // The PresenceDetectionValue.getLowestLatency() should return the smallest latency
        PresenceDetectionValue v = subject.updateReachableValue(ICMP_PING, 20);
        PresenceDetectionValue v2 = subject.updateReachableValue(ICMP_PING, 19);
        Assert.assertEquals(v, v2);
        Assert.assertThat(v.getLowestLatency(), CoreMatchers.is(19.0));
        // Advance in time but not expire the cache (1ms left)
        final long ALMOST_EXPIRE = (START_TIME + (PresenceDetectionTest.CACHETIME)) - 1;
        Mockito.when(subject.cache.getCurrentNanoTime()).thenReturn(TimeUnit.MILLISECONDS.toNanos(ALMOST_EXPIRE));
        // Updating should reset the expire timer of the cache
        v2 = subject.updateReachableValue(ICMP_PING, 28);
        Assert.assertEquals(v, v2);
        Assert.assertThat(v2.getLowestLatency(), CoreMatchers.is(19.0));
        Assert.assertThat(ExpiringCacheHelper.expireTime(subject.cache), CoreMatchers.is(TimeUnit.MILLISECONDS.toNanos((ALMOST_EXPIRE + (PresenceDetectionTest.CACHETIME)))));
        // Cache expire. A new PresenceDetectionValue instance will be returned
        Mockito.when(subject.cache.getCurrentNanoTime()).thenReturn(TimeUnit.MILLISECONDS.toNanos((((ALMOST_EXPIRE + (PresenceDetectionTest.CACHETIME)) + (PresenceDetectionTest.CACHETIME)) + 1)));
        v2 = subject.updateReachableValue(ICMP_PING, 25);
        Assert.assertNotEquals(v, v2);
        Assert.assertThat(v2.getLowestLatency(), CoreMatchers.is(25.0));
    }
}

