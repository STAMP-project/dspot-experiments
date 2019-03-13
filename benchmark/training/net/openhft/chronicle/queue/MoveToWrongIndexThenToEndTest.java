package net.openhft.chronicle.queue;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import org.junit.Assert;
import org.junit.Test;


/**
 * The ChronicleQueueIT class implements a test that causes Chronicle Queue to
 * fail with a BufferUnderflowException whilst executing a tailer.toEnd() call.
 */
public class MoveToWrongIndexThenToEndTest {
    private static final int msgSize = 64;

    private static final int numOfToEndCalls = 100;

    private static final long noIndex = 0;

    private static final RollCycle rollCycle = RollCycles.DAILY;

    private final Path basePath;

    private ExcerptAppender appender;

    private Bytes<ByteBuffer> outbound;

    public MoveToWrongIndexThenToEndTest() throws IOException {
        basePath = Files.createTempDirectory("MoveToWrongIndexThenToEndTest");
        basePath.toFile().deleteOnExit();
        SingleChronicleQueue appenderChronicle = createChronicle(basePath);
        appender = appenderChronicle.acquireAppender();
        outbound = Bytes.elasticByteBuffer();
    }

    @Test
    public void testBufferUnderflowException() throws InterruptedException {
        append();
        append();
        long lastIndex = getLastIndex(basePath);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Semaphore l0 = new Semaphore(0);
            Semaphore l1 = new Semaphore(0);
            AtomicReference<Throwable> refThrowable = new AtomicReference<>();
            executor.execute(() -> {
                try (SingleChronicleQueue chronicle = createChronicle(basePath)) {
                    ExcerptTailer tailer = chronicle.createTailer();
                    tailer.moveToIndex(lastIndex);
                    l0.release();
                    for (int i = 0; i < (MoveToWrongIndexThenToEndTest.numOfToEndCalls); ++i) {
                        tailer.toEnd();// BufferUnderflowException in readSkip()

                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    refThrowable.set(e);
                } finally {
                    l1.release();
                }
            });
            waitFor(l0, "tailer start");
            append();
            append();
            waitFor(l1, "tailer finish");
            Assert.assertNull("refThrowable", refThrowable.get());
        } finally {
            try {
                executor.shutdown();
            } finally {
                if (!(executor.isShutdown())) {
                    executor.shutdownNow();
                }
            }
        }
    }
}

