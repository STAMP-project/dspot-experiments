package net.openhft.chronicle.queue.impl.single;


import java.io.IOException;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.impl.RollingChronicleQueue;
import net.openhft.chronicle.wire.DocumentContext;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class QueueInspectorTest {
    private static final String PROPERTY_KEY = "wire.encodeTidInHeader";

    private static String previousValue = null;

    @Test
    public void shouldDetermineWritingProcessIdWhenDocumentIsNotComplete() throws IOException {
        try (final RollingChronicleQueue queue = ChronicleQueue.singleBuilder(getTmpDir()).testBlockSize().build()) {
            final ExcerptAppender appender = queue.acquireAppender();
            appender.writeDocument(37L, ValueOut::int64);
            try (final DocumentContext ctx = appender.writingDocument()) {
                ctx.wire().write("foo").int32(17L);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void shouldIndicateNoProcessIdWhenDocumentIsComplete() throws IOException {
        try (final RollingChronicleQueue queue = ChronicleQueue.singleBuilder(getTmpDir()).testBlockSize().build()) {
            final QueueInspector inspector = new QueueInspector(queue);
            final ExcerptAppender appender = queue.acquireAppender();
            appender.writeDocument(37L, ValueOut::int64);
            try (final DocumentContext ctx = appender.writingDocument()) {
                ctx.wire().write("foo").int32(17L);
            }
            final int writingThreadId = inspector.getWritingThreadId();
            Assert.assertThat(writingThreadId, CoreMatchers.is(CoreMatchers.not(OS.getProcessId())));
            Assert.assertThat(QueueInspector.isValidThreadId(writingThreadId), CoreMatchers.is(false));
        }
    }
}

