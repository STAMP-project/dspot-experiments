package net.openhft.chronicle.queue.impl.single;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.ExcerptTailer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public final class MoveToIndexTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void shouldMoveToPreviousIndexAfterDocumentIsConsumed() throws Exception {
        File queuePath = tmpFolder.newFolder("cq");
        try (ChronicleQueue queue = ChronicleQueue.singleBuilder(queuePath).build()) {
            ExcerptAppender appender = queue.acquireAppender();
            for (int i = 1; i < 10; ++i) {
                appender.writeText(("id" + i));
            }
            ExcerptTailer tailer = queue.createTailer();
            assertNext(tailer, "id1");
            long index = tailer.index();
            assertNext(tailer, "id2");
            tailer.moveToIndex(index);
            assertNext(tailer, "id2");
            tailer.moveToIndex(index);
            assertNext(tailer, "id2");
        }
    }

    // https://github.com/OpenHFT/Chronicle-Queue/issues/401
    @Test
    public void testRandomMove() throws Exception {
        final Map<Long, String> messageByIndex = new HashMap<>();
        try (ChronicleQueue queue = SingleChronicleQueueBuilder.binary(tmpFolder.newFolder()).build()) {
            // create a queue and add some excerpts
            final ExcerptAppender appender = queue.acquireAppender();
            for (int i = 0; i < 10; i++) {
                final String message = "msg" + i;
                appender.writeDocument(( w) -> w.write("message").object(message));
                final long appendIndex = appender.lastIndexAppended();
                messageByIndex.put(appendIndex, message);
            }
            final Random random = new Random(1510298038000L);
            final List<Long> indices = new ArrayList<>(messageByIndex.keySet());
            final ExcerptTailer tailer = queue.createTailer();
            final AtomicReference<String> capturedMessage = new AtomicReference<>();
            for (int i = 0; i < 100; i++) {
                final long randomIndex = indices.get(random.nextInt(messageByIndex.keySet().size()));
                tailer.moveToIndex(randomIndex);
                tailer.readDocument(( w) -> capturedMessage.set(((String) (w.read("message").object()))));
                Assert.assertEquals(messageByIndex.get(randomIndex), capturedMessage.get());
                tailer.readDocument(( w) -> w.read("message").object());
            }
        }
    }
}

