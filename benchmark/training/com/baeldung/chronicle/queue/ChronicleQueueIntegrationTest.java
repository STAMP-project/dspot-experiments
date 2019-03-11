package com.baeldung.chronicle.queue;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ChronicleQueueBuilder;
import net.openhft.chronicle.ExcerptTailer;
import net.openhft.chronicle.tools.ChronicleTools;
import org.junit.Assert;
import org.junit.Test;


public class ChronicleQueueIntegrationTest {
    @Test
    public void givenSetOfValues_whenWriteToQueue_thenWriteSuccesfully() throws IOException {
        File queueDir = Files.createTempDirectory("chronicle-queue").toFile();
        ChronicleTools.deleteOnExit(queueDir.getPath());
        Chronicle chronicle = ChronicleQueueBuilder.indexed(queueDir).build();
        String stringVal = "Hello World";
        int intVal = 101;
        long longVal = System.currentTimeMillis();
        double doubleVal = 90.00192091;
        ChronicleQueue.writeToQueue(chronicle, stringVal, intVal, longVal, doubleVal);
        ExcerptTailer tailer = chronicle.createTailer();
        while (tailer.nextIndex()) {
            Assert.assertEquals(stringVal, tailer.readUTF());
            Assert.assertEquals(intVal, tailer.readInt());
            Assert.assertEquals(longVal, tailer.readLong());
            Assert.assertEquals(((Double) (doubleVal)), ((Double) (tailer.readDouble())));
        } 
        tailer.finish();
        tailer.close();
        chronicle.close();
    }
}

