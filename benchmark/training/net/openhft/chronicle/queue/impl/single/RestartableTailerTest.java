package net.openhft.chronicle.queue.impl.single;


import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptTailer;
import org.junit.Assert;
import org.junit.Test;


public class RestartableTailerTest {
    @Test
    public void restartable() {
        String tmp = ((OS.TARGET) + "/restartable-") + (System.nanoTime());
        try (ChronicleQueue cq = SingleChronicleQueueBuilder.binary(tmp).build()) {
            for (int i = 0; i < 7; i++)
                cq.acquireAppender().writeText(("test " + i));

        }
        try (ChronicleQueue cq = SingleChronicleQueueBuilder.binary(tmp).build()) {
            ExcerptTailer atailer = cq.createTailer("a");
            Assert.assertEquals("test 0", atailer.readText());
            Assert.assertEquals("test 1", atailer.readText());
            Assert.assertEquals("test 2", atailer.readText());
            ExcerptTailer btailer = cq.createTailer("b");
            Assert.assertEquals("test 0", btailer.readText());
        }
        try (ChronicleQueue cq = SingleChronicleQueueBuilder.binary(tmp).build()) {
            ExcerptTailer atailer = cq.createTailer("a");
            Assert.assertEquals("test 3", atailer.readText());
            Assert.assertEquals("test 4", atailer.readText());
            Assert.assertEquals("test 5", atailer.readText());
            ExcerptTailer btailer = cq.createTailer("b");
            Assert.assertEquals("test 1", btailer.readText());
        }
        try (ChronicleQueue cq = SingleChronicleQueueBuilder.binary(tmp).build()) {
            ExcerptTailer atailer = cq.createTailer("a");
            Assert.assertEquals("test 6", atailer.readText());
            Assert.assertEquals(null, atailer.readText());
            ExcerptTailer btailer = cq.createTailer("b");
            Assert.assertEquals("test 2", btailer.readText());
        }
        try (ChronicleQueue cq = SingleChronicleQueueBuilder.binary(tmp).build()) {
            ExcerptTailer atailer = cq.createTailer("a");
            Assert.assertEquals(null, atailer.readText());
            ExcerptTailer btailer = cq.createTailer("b");
            Assert.assertEquals("test 3", btailer.readText());
        }
    }
}

