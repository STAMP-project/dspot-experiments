package net.openhft.chronicle.queue.impl.single;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.wire.DocumentContext;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public final class RollAtEndOfCycleTest {
    private final AtomicLong clock = new AtomicLong(System.currentTimeMillis());

    @Test
    public void shouldRollAndAppendToNewFile() throws Exception {
        Assume.assumeFalse(Jvm.isArm());
        try (final SingleChronicleQueue queue = createQueue()) {
            final ExcerptAppender appender = queue.acquireAppender();
            appender.writeDocument(1, ( w, i) -> {
                w.int32(i);
            });
            final ExcerptTailer tailer = queue.createTailer();
            try (final DocumentContext context = tailer.readingDocument()) {
                Assert.assertTrue(context.isPresent());
            }
            RollAtEndOfCycleTest.assertQueueFileCount(queue.path.toPath(), 1);
            clock.addAndGet(TimeUnit.SECONDS.toMillis(2));
            Assert.assertFalse(tailer.readingDocument().isPresent());
            appender.writeDocument(2, ( w, i) -> {
                w.int32(i);
            });
            RollAtEndOfCycleTest.assertQueueFileCount(queue.path.toPath(), 2);
            try (final DocumentContext context = tailer.readingDocument()) {
                Assert.assertTrue(context.isPresent());
            }
            final ExcerptTailer newTailer = queue.createTailer();
            int totalCount = 0;
            while (true) {
                final DocumentContext context = newTailer.readingDocument();
                if ((context.isPresent()) && (context.isData())) {
                    Assert.assertTrue(((context.wire().read().int32()) != 0));
                    totalCount++;
                } else
                    if (!(context.isPresent())) {
                        break;
                    }

            } 
            Assert.assertThat(totalCount, CoreMatchers.is(2));
        }
    }

    @Test
    public void shouldAppendToExistingQueueFile() throws Exception {
        try (final SingleChronicleQueue queue = createQueue()) {
            final ExcerptAppender appender = queue.acquireAppender();
            appender.writeDocument(1, ( w, i) -> {
                w.int32(i);
            });
            final ExcerptTailer tailer = queue.createTailer();
            try (final DocumentContext context = tailer.readingDocument()) {
                Assert.assertTrue(context.isPresent());
            }
            RollAtEndOfCycleTest.assertQueueFileCount(queue.path.toPath(), 1);
            Assert.assertFalse(tailer.readingDocument().isPresent());
            appender.writeDocument(2, ( w, i) -> {
                w.int32(i);
            });
            RollAtEndOfCycleTest.assertQueueFileCount(queue.path.toPath(), 1);
            try (final DocumentContext context = tailer.readingDocument()) {
                Assert.assertTrue(context.isPresent());
            }
        }
    }
}

