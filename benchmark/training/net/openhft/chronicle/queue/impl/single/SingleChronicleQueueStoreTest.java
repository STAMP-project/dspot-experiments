package net.openhft.chronicle.queue.impl.single;


import java.util.concurrent.atomic.AtomicLong;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.RollCycles;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class SingleChronicleQueueStoreTest {
    private static final int INDEX_SPACING = 4;

    private static final int RECORD_COUNT = (SingleChronicleQueueStoreTest.INDEX_SPACING) * 10;

    private static final RollCycles ROLL_CYCLE = RollCycles.DAILY;

    private final AtomicLong clock = new AtomicLong(System.currentTimeMillis());

    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    @Test
    public void shouldPerformIndexingOnAppend() throws Exception {
        runTest(( queue) -> {
            final ExcerptAppender appender = queue.acquireAppender();
            final long[] indices = writeMessagesStoreIndices(appender, queue.createTailer());
            assertExcerptsAreIndexed(queue, indices, ( i) -> (i % (INDEX_SPACING)) == 0, ScanResult.FOUND);
        });
    }
}

