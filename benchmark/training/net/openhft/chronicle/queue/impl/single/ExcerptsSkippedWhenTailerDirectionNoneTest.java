package net.openhft.chronicle.queue.impl.single;


import TailerDirection.FORWARD;
import TailerDirection.NONE;
import java.io.File;
import net.openhft.chronicle.queue.DirectoryUtils;
import net.openhft.chronicle.queue.RollCycles;
import net.openhft.chronicle.wire.DocumentContext;
import net.openhft.chronicle.wire.ValueIn;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class ExcerptsSkippedWhenTailerDirectionNoneTest {
    @Test
    public void shouldNotSkipMessageAtStartOfQueue() throws Exception {
        final File tmpDir = DirectoryUtils.tempDir(ExcerptsSkippedWhenTailerDirectionNoneTest.class.getSimpleName());
        try (final ChronicleQueue writeQueue = ChronicleQueue.singleBuilder(tmpDir).testBlockSize().rollCycle(RollCycles.TEST_DAILY).build()) {
            final ExcerptAppender excerptAppender = writeQueue.acquireAppender();
            try (final DocumentContext ctx = excerptAppender.writingDocument()) {
                ctx.wire().getValueOut().object("first");
            }
            try (final DocumentContext ctx = excerptAppender.writingDocument()) {
                ctx.wire().getValueOut().object("second");
            }
        }
        try (final ChronicleQueue readQueue = ChronicleQueue.singleBuilder(tmpDir).testBlockSize().rollCycle(RollCycles.TEST_DAILY).build()) {
            final ExcerptTailer tailer = readQueue.createTailer();
            final RollCycle rollCycle = readQueue.rollCycle();
            Assert.assertThat(rollCycle.toSequenceNumber(tailer.index()), CoreMatchers.is(0L));
            try (final DocumentContext ctx = tailer.direction(NONE).readingDocument()) {
                // access the first document without incrementing sequence number
            }
            Assert.assertThat(rollCycle.toSequenceNumber(tailer.index()), CoreMatchers.is(0L));
            String value;
            try (DocumentContext dc = tailer.direction(FORWARD).readingDocument()) {
                ValueIn valueIn = dc.wire().getValueIn();
                value = ((String) (valueIn.object()));
            }
            Assert.assertThat(rollCycle.toSequenceNumber(tailer.index()), CoreMatchers.is(1L));
            Assert.assertThat(value, CoreMatchers.is("first"));
            try (DocumentContext dc = tailer.direction(NONE).readingDocument()) {
                ValueIn valueIn = dc.wire().getValueIn();
                value = ((String) (valueIn.object()));
            }
            Assert.assertThat(rollCycle.toSequenceNumber(tailer.index()), CoreMatchers.is(1L));
            Assert.assertThat(value, CoreMatchers.is("second"));
            try (DocumentContext dc = tailer.direction(NONE).readingDocument()) {
                ValueIn valueIn = dc.wire().getValueIn();
                value = ((String) (valueIn.object()));
            }
            Assert.assertThat(rollCycle.toSequenceNumber(tailer.index()), CoreMatchers.is(1L));
            Assert.assertThat(value, CoreMatchers.is("second"));
        }
    }
}

