package net.openhft.chronicle.queue;


import java.io.File;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.core.io.IOTools;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import org.junit.Assert;
import org.junit.Test;


public class MarshallableTest {
    @Test
    public void testWriteText() {
        File dir = DirectoryUtils.tempDir("testWriteText");
        try (ChronicleQueue queue = SingleChronicleQueueBuilder.binary(dir).testBlockSize().build()) {
            ExcerptAppender appender = queue.acquireAppender();
            ExcerptTailer tailer = queue.createTailer();
            ExcerptTailer tailer2 = queue.createTailer();
            int runs = 1000;
            for (int i = 0; i < runs; i++)
                appender.writeText(("" + i));

            for (int i = 0; i < runs; i++)
                Assert.assertEquals(("" + i), tailer.readText());

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < runs; i++) {
                Assert.assertTrue(tailer2.readText(sb));
                Assert.assertEquals(("" + i), sb.toString());
            }
        } finally {
            try {
                IOTools.deleteDirWithFiles(dir, 2);
            } catch (IORuntimeException e) {
                // ignored
            }
        }
    }
}

