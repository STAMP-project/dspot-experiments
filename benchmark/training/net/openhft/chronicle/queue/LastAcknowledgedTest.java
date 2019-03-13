package net.openhft.chronicle.queue;


import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.annotation.RequiredForClient;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import org.junit.Assert;
import org.junit.Test;


@RequiredForClient
public class LastAcknowledgedTest {
    @Test
    public void testLastAcknowledge() {
        String name = ((OS.getTarget()) + "/testLastAcknowledge-") + (System.nanoTime());
        try (ChronicleQueue q = SingleChronicleQueueBuilder.single(name).testBlockSize().build()) {
            q.acquireAppender().writeText("Hello World");
        }
        try (ChronicleQueue q = SingleChronicleQueueBuilder.single(name).testBlockSize().build()) {
            Assert.assertEquals((-1), q.lastAcknowledgedIndexReplicated());
        }
    }
}

