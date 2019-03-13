package net.openhft.chronicle.queue;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.openhft.chronicle.core.io.IOTools;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.DocumentContext;
import org.junit.Assert;
import org.junit.Test;


public class ChronicleQueuePeekDocumentTest {
    boolean firstMessage = true;

    @Test
    public void testUsingPeekDocument() throws IOException {
        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("ChronicleQueueLoggerTest");
            // Read back the data
            try (ChronicleQueue queue = SingleChronicleQueueBuilder.binary(tempDir).build()) {
                ExcerptTailer tailer = queue.createTailer();
                try (ChronicleQueue writeQueue = SingleChronicleQueueBuilder.binary(tempDir).build()) {
                    ExcerptAppender appender = writeQueue.acquireAppender();
                    try (DocumentContext dc = appender.writingDocument()) {
                        dc.wire().write("field1").int32(123534).write("field2").float64(123.423).write("time").int64(12053432432L);
                    }
                    try (DocumentContext dc = appender.writingDocument()) {
                        dc.wire().write("field1").int32(323242).write("field2").float64(543.1233).write("time").int64(12053432900L);
                    }
                }
                Assert.assertEquals(("field1: !int 123534\n" + ("field2: 123.423\n" + "time: 12053432432\n")), read(tailer));
                Assert.assertEquals(("field1: !int 323242\n" + ("field2: 543.1233\n" + "time: 12053432900\n")), read(tailer));
            }
        } finally {
            if (tempDir != null) {
                IOTools.deleteDirWithFiles(tempDir.toFile(), 2);
            }
        }
    }
}

