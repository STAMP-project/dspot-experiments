package net.openhft.chronicle.queue;


import java.io.File;
import java.util.Arrays;
import net.openhft.chronicle.wire.DocumentContext;
import org.junit.Assert;
import org.junit.Test;


/**
 * see https://github.com/OpenHFT/Chronicle-Queue/issues/535
 * Created by Rob Austin
 */
public class MappedFileSafeLimitTooSmallTest extends ChronicleQueueTestBase {
    @Test
    public void testMappedFileSafeLimitTooSmall() {
        final int arraySize = 40000;
        final int blockSize = arraySize * 6;
        byte[] data = new byte[arraySize];
        Arrays.fill(data, ((byte) ('x')));
        File tmpDir = getTmpDir();
        try (final ChronicleQueue queue = net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder.builder(tmpDir, WireType.BINARY).blockSize(blockSize).build()) {
            for (int i = 0; i < 5; i++) {
                try (DocumentContext dc = queue.acquireAppender().writingDocument()) {
                    System.out.println(dc.wire().bytes().writeRemaining());
                    dc.wire().write("data").bytes(data);
                }
            }
        }
        try (final ChronicleQueue queue = net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder.builder(tmpDir, WireType.BINARY).blockSize(blockSize).build()) {
            for (int i = 0; i < 5; i++) {
                try (DocumentContext dc = queue.createTailer().readingDocument()) {
                    Assert.assertArrayEquals(data, dc.wire().read("data").bytes());
                }
            }
        }
    }
}

