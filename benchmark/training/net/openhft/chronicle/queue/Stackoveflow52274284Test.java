package net.openhft.chronicle.queue;


import java.nio.file.Files;
import java.nio.file.Paths;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.wire.DocumentContext;
import net.openhft.chronicle.wire.Wire;
import org.junit.Test;


public class Stackoveflow52274284Test {
    @Test
    public void fails() throws Exception {
        String basePath = OS.TMP;
        String path = Files.createTempDirectory(Paths.get(basePath), "chronicle-").toAbsolutePath().toString();
        System.out.printf("Using temp path '%s'%n", path);
        try (ChronicleQueue chronicleQueue = ChronicleQueue.single(path)) {
            // Create Appender
            ExcerptAppender appender = chronicleQueue.acquireAppender();
            // Create Tailer
            ExcerptTailer tailer = chronicleQueue.createTailer();
            tailer.toStart();
            int numberOfRecords = 10;
            // Write
            for (int i = 0; i <= numberOfRecords; i++) {
                System.out.println(("Writing " + i));
                try (final DocumentContext dc = appender.writingDocument()) {
                    dc.wire().write(() -> "msg").text("Hello World!");
                    System.out.println(("your data was store to index=" + (dc.index())));
                } catch (Exception e) {
                    System.err.println("Unable to store value to chronicle");
                    e.printStackTrace();
                }
            }
            // Read
            for (int i = 0; i <= numberOfRecords; i++) {
                System.out.println(("Reading " + i));
                try (DocumentContext documentContext = tailer.readingDocument()) {
                    long currentOffset = documentContext.index();
                    System.out.println(("Current offset: " + currentOffset));
                    Wire wire = documentContext.wire();
                    if (wire != null) {
                        String msg = wire.read("msg").text();
                        System.out.println(msg);
                    }
                }
            }
        }
    }
}

