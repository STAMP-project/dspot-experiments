package org.testcontainers.containers.wait.internal;


import com.google.common.collect.ImmutableSet;
import java.net.ServerSocket;
import org.junit.Test;
import org.rnorth.visibleassertions.VisibleAssertions;
import org.testcontainers.containers.wait.strategy.WaitStrategyTarget;


public class ExternalPortListeningCheckTest {
    private ServerSocket listeningSocket1;

    private ServerSocket listeningSocket2;

    private ServerSocket nonListeningSocket;

    private WaitStrategyTarget mockContainer;

    @Test
    public void singleListening() {
        final ExternalPortListeningCheck check = new ExternalPortListeningCheck(mockContainer, ImmutableSet.of(listeningSocket1.getLocalPort()));
        final Boolean result = check.call();
        VisibleAssertions.assertTrue("ExternalPortListeningCheck identifies a single listening port", result);
    }

    @Test
    public void multipleListening() {
        final ExternalPortListeningCheck check = new ExternalPortListeningCheck(mockContainer, ImmutableSet.of(listeningSocket1.getLocalPort(), listeningSocket2.getLocalPort()));
        final Boolean result = check.call();
        VisibleAssertions.assertTrue("ExternalPortListeningCheck identifies multiple listening port", result);
    }

    @Test
    public void oneNotListening() {
        final ExternalPortListeningCheck check = new ExternalPortListeningCheck(mockContainer, ImmutableSet.of(listeningSocket1.getLocalPort(), nonListeningSocket.getLocalPort()));
        assertThrows("ExternalPortListeningCheck detects a non-listening port among many", IllegalStateException.class, ((Runnable) (check::call)));
    }
}

