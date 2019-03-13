package net.openhft.chronicle.queue.impl.single;


import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;
import net.openhft.chronicle.queue.DirectoryUtils;
import net.openhft.chronicle.queue.RollCycles;
import net.openhft.chronicle.wire.DocumentContext;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static RollCycles.TEST_SECONDLY;


public final class DocumentOrderingTest {
    private static final RollCycles ROLL_CYCLE = TEST_SECONDLY;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final AtomicLong clock = new AtomicLong(System.currentTimeMillis());

    private final AtomicInteger counter = new AtomicInteger(0);

    @Test
    public void queuedWriteInPreviousCycleShouldRespectTotalOrdering() throws Exception {
        try (final ChronicleQueue queue = builder(DirectoryUtils.tempDir("document-ordering"), 1000L).build()) {
            final ExcerptAppender excerptAppender = queue.acquireAppender();
            // write initial document
            excerptAppender.writeDocument("foo", ValueOut::text);
            // begin a record in the first cycle file
            final DocumentContext firstOpenDocument = excerptAppender.writingDocument();
            firstOpenDocument.wire().getValueOut().int32(counter.getAndIncrement());
            // start another record in the first cycle file
            // this may be written to either the first or the second cycle file
            final Future<DocumentOrderingTest.RecordInfo> secondDocumentInFirstCycle = attemptToWriteDocument(queue);
            // move time to beyond the next cycle
            clock.addAndGet(TimeUnit.SECONDS.toMillis(2L));
            @SuppressWarnings("unused")
            final Future<DocumentOrderingTest.RecordInfo> otherDocumentWriter = attemptToWriteDocument(queue);
            firstOpenDocument.close();
            secondDocumentInFirstCycle.get(5L, TimeUnit.SECONDS);
            final ExcerptTailer tailer = queue.createTailer();
            // discard first record
            tailer.readingDocument().close();
            // assert that records are committed in order
            DocumentOrderingTest.expectValue(0, tailer);
            DocumentOrderingTest.expectValue(1, tailer);
            DocumentOrderingTest.expectValue(2, tailer);
            Assert.assertThat(tailer.readingDocument().isPresent(), CoreMatchers.is(false));
        }
    }

    @Test
    public void shouldRecoverFromUnfinishedFirstMessageInPreviousQueue() throws Exception {
        // as below, but don't actually close the initial context
        try (final ChronicleQueue queue = builder(DirectoryUtils.tempDir("document-ordering"), 1000L).build()) {
            final ExcerptAppender excerptAppender = queue.acquireAppender();
            final Future<DocumentOrderingTest.RecordInfo> otherDocumentWriter;
            // begin a record in the first cycle file
            final DocumentContext documentContext = excerptAppender.writingDocument();
            documentContext.wire().getValueOut().int32(counter.getAndIncrement());
            // move time to beyond the next cycle
            clock.addAndGet(TimeUnit.SECONDS.toMillis(2L));
            otherDocumentWriter = attemptToWriteDocument(queue);
            Assert.assertEquals(1, otherDocumentWriter.get(5L, TimeUnit.SECONDS).counterValue);
            final ExcerptTailer tailer = queue.createTailer();
            DocumentOrderingTest.expectValue(1, tailer);
            Assert.assertThat(tailer.readingDocument().isPresent(), CoreMatchers.is(false));
        }
    }

    @Test
    public void multipleThreadsMustWaitUntilPreviousCycleFileIsCompleted() throws Exception {
        final File dir = DirectoryUtils.tempDir("document-ordering");
        // must be different instances of queue to work around synchronization on acquireStore()
        try (final ChronicleQueue queue = builder(dir, 5000L).build();final ChronicleQueue queue2 = builder(dir, 5000L).build();final ChronicleQueue queue3 = builder(dir, 5000L).build();final ChronicleQueue queue4 = builder(dir, 5000L).build()) {
            final ExcerptAppender excerptAppender = queue.acquireAppender();
            final Future<DocumentOrderingTest.RecordInfo> firstWriter;
            final Future<DocumentOrderingTest.RecordInfo> secondWriter;
            final Future<DocumentOrderingTest.RecordInfo> thirdWriter;
            try (final DocumentContext documentContext = excerptAppender.writingDocument()) {
                // move time to beyond the next cycle
                clock.addAndGet(TimeUnit.SECONDS.toMillis(2L));
                // add some jitter to allow threads to race
                firstWriter = attemptToWriteDocument(queue2);
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(10L));
                secondWriter = attemptToWriteDocument(queue3);
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(10L));
                thirdWriter = attemptToWriteDocument(queue4);
                // stall this thread, other threads should not be able to advance,
                // since this DocumentContext is still open
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2L));
                documentContext.wire().getValueOut().int32(counter.getAndIncrement());
            }
            firstWriter.get(5L, TimeUnit.SECONDS);
            secondWriter.get(5L, TimeUnit.SECONDS);
            thirdWriter.get(5L, TimeUnit.SECONDS);
            final ExcerptTailer tailer = queue.createTailer();
            DocumentOrderingTest.expectValue(0, tailer);
            DocumentOrderingTest.expectValue(1, tailer);
            DocumentOrderingTest.expectValue(2, tailer);
            DocumentOrderingTest.expectValue(3, tailer);
        }
    }

    @Test
    public void codeWithinPriorDocumentMustExecuteBeforeSubsequentDocumentWhenQueueIsEmpty() throws Exception {
        try (final ChronicleQueue queue = builder(DirectoryUtils.tempDir("document-ordering"), 3000L).build()) {
            final ExcerptAppender excerptAppender = queue.acquireAppender();
            final Future<DocumentOrderingTest.RecordInfo> otherDocumentWriter;
            try (final DocumentContext documentContext = excerptAppender.writingDocument()) {
                // move time to beyond the next cycle
                clock.addAndGet(TimeUnit.SECONDS.toMillis(2L));
                otherDocumentWriter = attemptToWriteDocument(queue);
                // stall this thread, other thread should not be able to advance,
                // since this DocumentContext is still open
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2L));
                documentContext.wire().getValueOut().int32(counter.getAndIncrement());
            }
            Assert.assertEquals(1, otherDocumentWriter.get(5L, TimeUnit.SECONDS).counterValue);
            final ExcerptTailer tailer = queue.createTailer();
            DocumentOrderingTest.expectValue(0, tailer);
            DocumentOrderingTest.expectValue(1, tailer);
        }
    }

    @Test
    public void codeWithinPriorDocumentMustExecuteBeforeSubsequentDocumentWhenQueueIsNotEmpty() throws Exception {
        try (final ChronicleQueue queue = builder(DirectoryUtils.tempDir("document-ordering"), 3000L).build()) {
            final ExcerptAppender excerptAppender = queue.acquireAppender();
            excerptAppender.writeDocument("foo", ValueOut::text);
            final Future<DocumentOrderingTest.RecordInfo> otherDocumentWriter;
            try (final DocumentContext documentContext = excerptAppender.writingDocument()) {
                // move time to beyond the next cycle
                clock.addAndGet(TimeUnit.SECONDS.toMillis(2L));
                otherDocumentWriter = attemptToWriteDocument(queue);
                // stall this thread, other thread should not be able to advance,
                // since this DocumentContext is still open
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2L));
                documentContext.wire().getValueOut().int32(counter.getAndIncrement());
            }
            Assert.assertEquals(1, otherDocumentWriter.get(5L, TimeUnit.SECONDS).counterValue);
            final ExcerptTailer tailer = queue.createTailer();
            final DocumentContext documentContext = tailer.readingDocument();
            Assert.assertTrue(documentContext.isPresent());
            documentContext.close();
            DocumentOrderingTest.expectValue(0, tailer);
            DocumentOrderingTest.expectValue(1, tailer);
        }
    }

    private static final class RecordInfo {
        private final int counterValue;

        RecordInfo(final int counterValue) {
            this.counterValue = counterValue;
        }
    }
}

