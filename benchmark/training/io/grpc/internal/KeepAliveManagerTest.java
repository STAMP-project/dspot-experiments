/**
 * Copyright 2016 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc.internal;


import ClientTransport.PingCallback;
import KeepAliveManager.Ticker;
import Status.Code.UNAVAILABLE;
import io.grpc.Status;
import io.grpc.internal.KeepAliveManager.KeepAlivePinger;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


@RunWith(JUnit4.class)
public final class KeepAliveManagerTest {
    private final KeepAliveManagerTest.FakeTicker ticker = new KeepAliveManagerTest.FakeTicker();

    private KeepAliveManager keepAliveManager;

    @Rule
    public final MockitoRule mocks = MockitoJUnit.rule();

    @Mock
    private KeepAlivePinger keepAlivePinger;

    @Mock
    private ConnectionClientTransport transport;

    @Mock
    private ScheduledExecutorService scheduler;

    @Captor
    private ArgumentCaptor<Status> statusCaptor;

    static class FakeTicker extends KeepAliveManager.Ticker {
        long time;

        @Override
        public long read() {
            return time;
        }
    }

    @Test
    public void sendKeepAlivePings() {
        ticker.time = 1;
        // Transport becomes active. We should schedule keepalive pings.
        keepAliveManager.onTransportActive();
        ArgumentCaptor<Long> delayCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Runnable> sendPingCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(scheduler, Mockito.times(1)).schedule(sendPingCaptor.capture(), delayCaptor.capture(), ArgumentMatchers.isA(TimeUnit.class));
        Runnable sendPing = sendPingCaptor.getValue();
        Long delay = delayCaptor.getValue();
        Assert.assertEquals((1000 - 1), delay.longValue());
        ScheduledFuture<?> shutdownFuture = Mockito.mock(ScheduledFuture.class);
        Mockito.doReturn(shutdownFuture).when(scheduler).schedule(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        // Mannually running the Runnable will send the ping. Shutdown task should be scheduled.
        ticker.time = 1000;
        sendPing.run();
        Mockito.verify(keepAlivePinger).ping();
        Mockito.verify(scheduler, Mockito.times(2)).schedule(ArgumentMatchers.isA(Runnable.class), delayCaptor.capture(), ArgumentMatchers.isA(TimeUnit.class));
        delay = delayCaptor.getValue();
        // Keepalive timeout is 2000.
        Assert.assertEquals(2000, delay.longValue());
        // Ping succeeds. Reschedule another ping.
        ticker.time = 1100;
        keepAliveManager.onDataReceived();
        Mockito.verify(scheduler, Mockito.times(3)).schedule(ArgumentMatchers.isA(Runnable.class), delayCaptor.capture(), ArgumentMatchers.isA(TimeUnit.class));
        // Shutdown task has been cancelled.
        Mockito.verify(shutdownFuture).cancel(ArgumentMatchers.isA(Boolean.class));
        delay = delayCaptor.getValue();
        // Next ping should be exactly 1000 nanoseconds later.
        Assert.assertEquals(1000, delay.longValue());
    }

    @Test
    public void keepAlivePingDelayedByIncomingData() {
        ScheduledFuture<?> future = Mockito.mock(ScheduledFuture.class);
        Mockito.doReturn(future).when(scheduler).schedule(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        // Transport becomes active. We should schedule keepalive pings.
        keepAliveManager.onTransportActive();
        ArgumentCaptor<Runnable> sendPingCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(scheduler, Mockito.times(1)).schedule(sendPingCaptor.capture(), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        Runnable sendPing = sendPingCaptor.getValue();
        // We receive some data. We may need to delay the ping.
        ticker.time = 1500;
        keepAliveManager.onDataReceived();
        ticker.time = 1600;
        sendPing.run();
        // We didn't send the ping.
        Mockito.verify(transport, Mockito.times(0)).ping(ArgumentMatchers.isA(PingCallback.class), ArgumentMatchers.isA(Executor.class));
        // Instead we reschedule.
        ArgumentCaptor<Long> delayCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(scheduler, Mockito.times(2)).schedule(ArgumentMatchers.isA(Runnable.class), delayCaptor.capture(), ArgumentMatchers.isA(TimeUnit.class));
        Long delay = delayCaptor.getValue();
        Assert.assertEquals(((1500 + 1000) - 1600), delay.longValue());
    }

    @Test
    public void clientKeepAlivePinger_pingTimeout() {
        keepAlivePinger = new io.grpc.internal.KeepAliveManager.ClientKeepAlivePinger(transport);
        keepAlivePinger.onPingTimeout();
        Mockito.verify(transport).shutdownNow(statusCaptor.capture());
        Status status = statusCaptor.getValue();
        assertThat(status.getCode()).isEqualTo(UNAVAILABLE);
        assertThat(status.getDescription()).isEqualTo("Keepalive failed. The connection is likely gone");
    }

    @Test
    public void clientKeepAlivePinger_pingFailure() {
        keepAlivePinger = new io.grpc.internal.KeepAliveManager.ClientKeepAlivePinger(transport);
        keepAlivePinger.ping();
        ArgumentCaptor<ClientTransport.PingCallback> pingCallbackCaptor = ArgumentCaptor.forClass(PingCallback.class);
        Mockito.verify(transport).ping(pingCallbackCaptor.capture(), ArgumentMatchers.isA(Executor.class));
        ClientTransport.PingCallback pingCallback = pingCallbackCaptor.getValue();
        pingCallback.onFailure(new Throwable());
        Mockito.verify(transport).shutdownNow(statusCaptor.capture());
        Status status = statusCaptor.getValue();
        assertThat(status.getCode()).isEqualTo(UNAVAILABLE);
        assertThat(status.getDescription()).isEqualTo("Keepalive failed. The connection is likely gone");
    }

    @Test
    public void onTransportTerminationCancelsShutdownFuture() {
        // Transport becomes active. We should schedule keepalive pings.
        keepAliveManager.onTransportActive();
        ArgumentCaptor<Runnable> sendPingCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(scheduler, Mockito.times(1)).schedule(sendPingCaptor.capture(), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        Runnable sendPing = sendPingCaptor.getValue();
        ScheduledFuture<?> shutdownFuture = Mockito.mock(ScheduledFuture.class);
        Mockito.doReturn(shutdownFuture).when(scheduler).schedule(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        // Mannually running the Runnable will send the ping. Shutdown task should be scheduled.
        ticker.time = 1000;
        sendPing.run();
        keepAliveManager.onTransportTermination();
        // Shutdown task has been cancelled.
        Mockito.verify(shutdownFuture).cancel(ArgumentMatchers.isA(Boolean.class));
    }

    @Test
    public void keepAlivePingTimesOut() {
        // Transport becomes active. We should schedule keepalive pings.
        keepAliveManager.onTransportActive();
        ArgumentCaptor<Runnable> sendPingCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(scheduler, Mockito.times(1)).schedule(sendPingCaptor.capture(), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        Runnable sendPing = sendPingCaptor.getValue();
        ScheduledFuture<?> shutdownFuture = Mockito.mock(ScheduledFuture.class);
        Mockito.doReturn(shutdownFuture).when(scheduler).schedule(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        // Mannually running the Runnable will send the ping. Shutdown task should be scheduled.
        ticker.time = 1000;
        sendPing.run();
        Mockito.verify(keepAlivePinger).ping();
        ArgumentCaptor<Runnable> shutdownCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(scheduler, Mockito.times(2)).schedule(shutdownCaptor.capture(), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        Runnable shutdown = shutdownCaptor.getValue();
        // We do not receive the ping response. Shutdown runnable runs.
        // TODO(zdapeng): use FakeClock.ScheduledExecutorService
        ticker.time = 3000;
        shutdown.run();
        Mockito.verify(keepAlivePinger).onPingTimeout();
        // We receive the ping response too late.
        keepAliveManager.onDataReceived();
        // No more ping should be scheduled.
        Mockito.verify(scheduler, Mockito.times(2)).schedule(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
    }

    @Test
    public void transportGoesIdle() {
        ScheduledFuture<?> pingFuture = Mockito.mock(ScheduledFuture.class);
        Mockito.doReturn(pingFuture).when(scheduler).schedule(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        // Transport becomes active. We should schedule keepalive pings.
        keepAliveManager.onTransportActive();
        ArgumentCaptor<Runnable> sendPingCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(scheduler, Mockito.times(1)).schedule(sendPingCaptor.capture(), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        Runnable sendPing = sendPingCaptor.getValue();
        // Transport becomes idle. Nothing should happen when ping runnable runs.
        keepAliveManager.onTransportIdle();
        sendPing.run();
        // Ping was not sent.
        Mockito.verify(transport, Mockito.times(0)).ping(ArgumentMatchers.isA(PingCallback.class), ArgumentMatchers.isA(Executor.class));
        // No new ping got scheduled.
        Mockito.verify(scheduler, Mockito.times(1)).schedule(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        // But when transport goes back to active
        keepAliveManager.onTransportActive();
        // Then we do schedule another ping
        Mockito.verify(scheduler, Mockito.times(2)).schedule(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
    }

    @Test
    public void transportGoesIdle_doesntCauseIdleWhenEnabled() {
        keepAliveManager.onTransportTermination();
        keepAliveManager = new KeepAliveManager(keepAlivePinger, scheduler, ticker, 1000, 2000, true);
        keepAliveManager.onTransportStarted();
        // Keepalive scheduling should have started immediately.
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(scheduler).schedule(runnableCaptor.capture(), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        Runnable sendPing = runnableCaptor.getValue();
        keepAliveManager.onTransportActive();
        // Transport becomes idle. Should not impact the sending of the ping.
        keepAliveManager.onTransportIdle();
        sendPing.run();
        // Ping was sent.
        Mockito.verify(keepAlivePinger).ping();
        // Shutdown is scheduled.
        Mockito.verify(scheduler, Mockito.times(2)).schedule(runnableCaptor.capture(), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        // Shutdown is triggered.
        runnableCaptor.getValue().run();
        Mockito.verify(keepAlivePinger).onPingTimeout();
    }

    @Test
    public void transportGoesIdleAfterPingSent() {
        // Transport becomes active. We should schedule keepalive pings.
        keepAliveManager.onTransportActive();
        ArgumentCaptor<Runnable> sendPingCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(scheduler, Mockito.times(1)).schedule(sendPingCaptor.capture(), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        Runnable sendPing = sendPingCaptor.getValue();
        ScheduledFuture<?> shutdownFuture = Mockito.mock(ScheduledFuture.class);
        Mockito.doReturn(shutdownFuture).when(scheduler).schedule(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        // Mannually running the Runnable will send the ping. Shutdown task should be scheduled.
        // TODO(zdapeng): user FakeClock.ScheduledExecutorService
        ticker.time = 1000;
        sendPing.run();
        Mockito.verify(keepAlivePinger).ping();
        Mockito.verify(scheduler, Mockito.times(2)).schedule(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        // Transport becomes idle. No more ping should be scheduled after we receive a ping response.
        keepAliveManager.onTransportIdle();
        ticker.time = 1100;
        keepAliveManager.onDataReceived();
        Mockito.verify(scheduler, Mockito.times(2)).schedule(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        // Shutdown task has been cancelled.
        Mockito.verify(shutdownFuture).cancel(ArgumentMatchers.isA(Boolean.class));
        // Transport becomes active again. Another ping is scheduled.
        keepAliveManager.onTransportActive();
        Mockito.verify(scheduler, Mockito.times(3)).schedule(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
    }

    @Test
    public void transportGoesIdleBeforePingSent() {
        // Transport becomes active. We should schedule keepalive pings.
        ScheduledFuture<?> pingFuture = Mockito.mock(ScheduledFuture.class);
        Mockito.doReturn(pingFuture).when(scheduler).schedule(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        keepAliveManager.onTransportActive();
        Mockito.verify(scheduler, Mockito.times(1)).schedule(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        // Data is received, and we go to ping delayed
        keepAliveManager.onDataReceived();
        // Transport becomes idle while the 1st ping is still scheduled
        keepAliveManager.onTransportIdle();
        // Transport becomes active again, we don't need to reschedule another ping
        keepAliveManager.onTransportActive();
        Mockito.verify(scheduler, Mockito.times(1)).schedule(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
    }

    @Test
    public void transportShutsdownAfterPingScheduled() {
        ScheduledFuture<?> pingFuture = Mockito.mock(ScheduledFuture.class);
        Mockito.doReturn(pingFuture).when(scheduler).schedule(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        // Ping will be scheduled.
        keepAliveManager.onTransportActive();
        Mockito.verify(scheduler, Mockito.times(1)).schedule(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        // Transport is shutting down.
        keepAliveManager.onTransportTermination();
        // Ping future should have been cancelled.
        Mockito.verify(pingFuture).cancel(ArgumentMatchers.isA(Boolean.class));
    }

    @Test
    public void transportShutsdownAfterPingSent() {
        keepAliveManager.onTransportActive();
        ArgumentCaptor<Runnable> sendPingCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(scheduler, Mockito.times(1)).schedule(sendPingCaptor.capture(), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        Runnable sendPing = sendPingCaptor.getValue();
        ScheduledFuture<?> shutdownFuture = Mockito.mock(ScheduledFuture.class);
        Mockito.doReturn(shutdownFuture).when(scheduler).schedule(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        // Mannually running the Runnable will send the ping. Shutdown task should be scheduled.
        ticker.time = 1000;
        sendPing.run();
        Mockito.verify(keepAlivePinger).ping();
        Mockito.verify(scheduler, Mockito.times(2)).schedule(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        // Transport is shutting down.
        keepAliveManager.onTransportTermination();
        // Shutdown task has been cancelled.
        Mockito.verify(shutdownFuture).cancel(ArgumentMatchers.isA(Boolean.class));
    }

    @Test
    public void pingSentThenIdleThenActiveThenAck() {
        keepAliveManager.onTransportActive();
        ArgumentCaptor<Runnable> sendPingCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(scheduler, Mockito.times(1)).schedule(sendPingCaptor.capture(), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        Runnable sendPing = sendPingCaptor.getValue();
        ScheduledFuture<?> shutdownFuture = Mockito.mock(ScheduledFuture.class);
        // ping scheduled
        Mockito.doReturn(shutdownFuture).when(scheduler).schedule(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        // Mannually running the Runnable will send the ping.
        ticker.time = 1000;
        sendPing.run();
        // shutdown scheduled
        Mockito.verify(scheduler, Mockito.times(2)).schedule(sendPingCaptor.capture(), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
        Mockito.verify(keepAlivePinger).ping();
        keepAliveManager.onTransportIdle();
        keepAliveManager.onTransportActive();
        keepAliveManager.onDataReceived();
        // another ping scheduled
        Mockito.verify(scheduler, Mockito.times(3)).schedule(sendPingCaptor.capture(), ArgumentMatchers.isA(Long.class), ArgumentMatchers.isA(TimeUnit.class));
    }
}

