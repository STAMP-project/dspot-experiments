package com.zendesk.maxwell.replication;


import BinlogConnectorDiagnostic.HeartbeatObserver;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.ExecutionException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class HeartbeatObserverTest {
    @Test
    public void testObserverIsAddedToNotifier() {
        // Given
        HeartbeatNotifier notifier = new HeartbeatNotifier();
        new BinlogConnectorDiagnostic.HeartbeatObserver(notifier, Clock.systemUTC());
        // When
        // Then
        MatcherAssert.assertThat(notifier.countObservers(), CoreMatchers.is(1));
    }

    @Test
    public void testObserverIsRemovedAfterUpdate() {
        // Given
        HeartbeatNotifier notifier = new HeartbeatNotifier();
        new BinlogConnectorDiagnostic.HeartbeatObserver(notifier, Clock.systemUTC());
        // When
        notifier.heartbeat(12345);
        // Then
        MatcherAssert.assertThat(notifier.countObservers(), CoreMatchers.is(0));
    }

    @Test
    public void testLatencyFutureShouldComplete() throws InterruptedException, ExecutionException {
        // Given
        HeartbeatNotifier notifier = new HeartbeatNotifier();
        Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
        BinlogConnectorDiagnostic.HeartbeatObserver observer = new BinlogConnectorDiagnostic.HeartbeatObserver(notifier, clock);
        long now = clock.millis();
        long heartbeat = now - 10;
        // When
        notifier.heartbeat(heartbeat);
        // Then
        MatcherAssert.assertThat(observer.latency.get(), CoreMatchers.is(10L));
    }

    @Test
    public void testFail() {
        // Given
        BinlogConnectorDiagnostic.HeartbeatObserver observer = new BinlogConnectorDiagnostic.HeartbeatObserver(new HeartbeatNotifier(), Clock.systemUTC());
        // When
        RuntimeException ex = new RuntimeException("blah");
        observer.fail(ex);
        // Then
        Assert.assertTrue(observer.latency.isCompletedExceptionally());
        try {
            observer.latency.get();
            Assert.fail();
        } catch (InterruptedException | ExecutionException e) {
            MatcherAssert.assertThat(e.getCause(), CoreMatchers.is(ex));
        }
    }
}

