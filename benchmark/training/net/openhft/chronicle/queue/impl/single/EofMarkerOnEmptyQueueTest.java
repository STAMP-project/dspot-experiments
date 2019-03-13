package net.openhft.chronicle.queue.impl.single;


import RollCycles.TEST_SECONDLY;
import Wires.END_OF_DATA;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.RollingChronicleQueue;
import net.openhft.chronicle.queue.impl.WireStore;
import net.openhft.chronicle.wire.DocumentContext;
import net.openhft.chronicle.wire.ValueIn;
import net.openhft.chronicle.wire.Wires;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public final class EofMarkerOnEmptyQueueTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void shouldRecoverFromEmptyQueueOnRoll() throws Exception {
        final AtomicLong clock = new AtomicLong(System.currentTimeMillis());
        try (final RollingChronicleQueue queue = ChronicleQueue.singleBuilder(tmpFolder.newFolder()).rollCycle(TEST_SECONDLY).timeProvider(clock::get).timeoutMS(1000).testBlockSize().build()) {
            final ExcerptAppender appender = queue.acquireAppender();
            final DocumentContext context = appender.writingDocument();
            // start to write a message, but don't close the context - simulates crashed writer
            final long expectedEofMarkerPosition = (context.wire().bytes().writePosition()) - (Wires.SPB_HEADER_SIZE);
            context.wire().writeEventName("foo").int32(1);
            final int startCycle = queue.cycle();
            clock.addAndGet(TimeUnit.SECONDS.toMillis(1L));
            final int nextCycle = queue.cycle();
            // ensure that the cycle file will roll
            Assert.assertThat(startCycle, CoreMatchers.is(CoreMatchers.not(nextCycle)));
            Executors.newSingleThreadExecutor().submit(() -> {
                try (final DocumentContext nextCtx = queue.acquireAppender().writingDocument()) {
                    nextCtx.wire().writeEventName("bar").int32(7);
                }
            }).get((Jvm.isDebug() ? 3000 : 3), TimeUnit.SECONDS);
            final WireStore firstCycleStore = queue.storeForCycle(startCycle, 0, false);
            final long firstCycleWritePosition = firstCycleStore.writePosition();
            // assert that no write was completed
            Assert.assertThat(firstCycleWritePosition, CoreMatchers.is(0L));
            final ExcerptTailer tailer = queue.createTailer();
            int recordCount = 0;
            int lastItem = -1;
            while (true) {
                try (final DocumentContext readCtx = tailer.readingDocument()) {
                    if (!(readCtx.isPresent())) {
                        break;
                    }
                    final StringBuilder name = new StringBuilder();
                    final ValueIn field = readCtx.wire().readEventName(name);
                    recordCount++;
                    lastItem = field.int32();
                }
            } 
            Assert.assertThat(firstCycleStore.bytes().readVolatileInt(expectedEofMarkerPosition), CoreMatchers.is(END_OF_DATA));
            Assert.assertThat(recordCount, CoreMatchers.is(1));
            Assert.assertThat(lastItem, CoreMatchers.is(7));
        }
    }
}

