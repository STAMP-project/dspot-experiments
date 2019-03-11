package net.openhft.chronicle.queue.impl.single;


import RollCycles.TEST_SECONDLY;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.queue.ChronicleQueue;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public final class MappedMemoryUnmappingTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void shouldUnmapMemoryAsCycleRolls() throws Exception {
        final AtomicLong clock = new AtomicLong(System.currentTimeMillis());
        long initialQueueMappedMemory = 0L;
        try (final ChronicleQueue queue = SingleChronicleQueueBuilder.binary(tmp.newFolder()).testBlockSize().rollCycle(TEST_SECONDLY).timeProvider(clock::get).build()) {
            for (int i = 0; i < 100; i++) {
                queue.acquireAppender().writeDocument(System.nanoTime(), ( d, t) -> d.int64(t));
                clock.addAndGet(TimeUnit.SECONDS.toMillis(1L));
                if (initialQueueMappedMemory == 0L) {
                    initialQueueMappedMemory = OS.memoryMapped();
                }
            }
        }
        GcControls.waitForGcCycle();
        final long timeoutAt = (System.currentTimeMillis()) + (TimeUnit.SECONDS.toMillis(10L));
        while ((System.currentTimeMillis()) < timeoutAt) {
            if ((OS.memoryMapped()) < (2 * initialQueueMappedMemory)) {
                return;
            }
        } 
        Assert.fail(String.format("Mapped memory (%dB) did not fall below threshold (%dB)", OS.memoryMapped(), (2 * initialQueueMappedMemory)));
    }
}

