package net.openhft.chronicle.queue.impl.single;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import net.openhft.chronicle.bytes.NewChunkListener;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.threads.InvalidEventHandlerException;
import net.openhft.chronicle.queue.DirectoryUtils;
import net.openhft.chronicle.wire.DocumentContext;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class PretoucherTest {
    private final AtomicLong clock = new AtomicLong(System.currentTimeMillis());

    private final List<Integer> capturedCycles = new ArrayList<>();

    private final PretoucherTest.CapturingChunkListener chunkListener = new PretoucherTest.CapturingChunkListener();

    @Test
    public void shouldHandleEarlyCycleRoll() {
        Assume.assumeNotNull(OS.isWindows());
        assert (System.getProperty("SingleChronicleQueueExcerpts.earlyAcquireNextCycle")) == null;
        assert (System.getProperty("SingleChronicleQueueExcerpts.pretoucherPrerollTimeMs")) == null;
        System.setProperty("SingleChronicleQueueExcerpts.earlyAcquireNextCycle", "true");
        System.setProperty("SingleChronicleQueueExcerpts.pretoucherPrerollTimeMs", "100");
        File dir = DirectoryUtils.tempDir("shouldHandleEarlyCycleRoll");
        try (final SingleChronicleQueue queue = PretoucherTest.createQueue(dir, clock::get);final Pretoucher pretoucher = new Pretoucher(PretoucherTest.createQueue(dir, clock::get), chunkListener, capturedCycles::add)) {
            IntStream.range(0, 10).forEach(( i) -> {
                try (final DocumentContext ctx = queue.acquireAppender().writingDocument()) {
                    Assert.assertThat(capturedCycles.size(), CoreMatchers.is((i == 0 ? 0 : i + 1)));
                    ctx.wire().write().int32(i);
                    ctx.wire().write().bytes(new byte[1024]);
                }
                try {
                    pretoucher.execute();
                } catch (InvalidEventHandlerException e) {
                    e.printStackTrace();
                }
                Assert.assertThat(capturedCycles.size(), CoreMatchers.is((i + 1)));
                clock.addAndGet(950);
                try {
                    pretoucher.execute();
                } catch (InvalidEventHandlerException e) {
                    e.printStackTrace();
                }
                clock.addAndGet(50);
                Assert.assertThat(capturedCycles.size(), CoreMatchers.is((i + 2)));
            });
            Assert.assertThat(capturedCycles.size(), CoreMatchers.is(11));
            Assert.assertThat(chunkListener.chunkMap.isEmpty(), CoreMatchers.is(false));
        } finally {
            System.clearProperty("SingleChronicleQueueExcerpts.earlyAcquireNextCycle");
            System.clearProperty("SingleChronicleQueueExcerpts.pretoucherPrerollTimeMs");
        }
    }

    private static final class CapturingChunkListener implements NewChunkListener {
        private final TreeMap<String, List<Integer>> chunkMap = new TreeMap<>();

        @Override
        public void onNewChunk(final String filename, final int chunk, final long delayMicros) {
            chunkMap.computeIfAbsent(filename, ( f) -> new ArrayList<>()).add(chunk);
        }
    }
}

