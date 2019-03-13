package net.openhft.chronicle.queue;


import RollCycles.DAILY;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Random;
import net.openhft.chronicle.core.annotation.RequiredForClient;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.DocumentContext;
import net.openhft.chronicle.wire.Wire;
import org.jetbrains.annotations.NotNull;
import org.junit.Ignore;
import org.junit.Test;


/* Created by AM on 3/20/17. Mainly for documentation: Write counterpart for FsFullRestTest.
Fails on Linux with a JVM SIGBUS unfortunately. Would be great if it's a "normal" runtime
exception.
 */
@Ignore
@RequiredForClient
public class FsFullWriteTest {
    /* as root:
    mount -o size=100M -t tmpfs none /mnt/tmpfs
    mkdir /mnt/tmpfs/cq && chown <user>:<group> /mnt/tmpfs/cq
    dd if=/dev/zero of=/mnt/tmpfs/fillspace bs=1M count=90
     */
    @NotNull
    private static String basePath = "/mnt/tmpfs/cq/testqueue";

    @Test
    public void testAppenderFullFs() throws Exception {
        ChronicleQueue queue = SingleChronicleQueueBuilder.binary(FsFullWriteTest.basePath).blockSize((256 << 1000)).rollCycle(DAILY).build();
        ExcerptAppender appender = queue.acquireAppender();
        byte[] payload = new byte[1024];
        Random r = new Random();
        r.nextBytes(payload);
        final LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        for (int i = 0; i < (1024 * 200); i++) {
            DocumentContext dc = appender.writingDocument();
            try {
                Wire w = dc.wire();
                w.write().dateTime(now);
                w.write().bytes(payload);
            } finally {
                dc.close();
            }
        }
    }
}

