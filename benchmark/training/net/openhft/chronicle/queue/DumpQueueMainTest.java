package net.openhft.chronicle.queue;


import SingleChronicleQueue.SUFFIX;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class DumpQueueMainTest {
    @Test
    public void shouldBeAbleToDumpReadOnlyQueueFile() throws Exception {
        if (OS.isWindows())
            return;

        final File dataDir = DirectoryUtils.tempDir(DumpQueueMainTest.class.getSimpleName());
        try (final ChronicleQueue queue = SingleChronicleQueueBuilder.binary(dataDir).build()) {
            final ExcerptAppender excerptAppender = queue.acquireAppender();
            excerptAppender.writeText("first");
            excerptAppender.writeText("last");
            final Path queueFile = Files.list(dataDir.toPath()).filter(( p) -> p.toString().endsWith(SUFFIX)).findFirst().orElseThrow(() -> new AssertionError(("Could not find queue file in directory " + dataDir)));
            Assert.assertThat(queueFile.toFile().setWritable(false), CoreMatchers.is(true));
            final DumpQueueMainTest.CountingOutputStream countingOutputStream = new DumpQueueMainTest.CountingOutputStream();
            DumpQueueMain.dump(queueFile.toFile(), new PrintStream(countingOutputStream), Long.MAX_VALUE);
            Assert.assertThat(countingOutputStream.bytes, CoreMatchers.is(CoreMatchers.not(0L)));
        }
    }

    @Test
    public void shouldDumpDirectoryListing() throws Exception {
        final File dataDir = DirectoryUtils.tempDir(DumpQueueMainTest.class.getSimpleName());
        try (final ChronicleQueue queue = SingleChronicleQueueBuilder.binary(dataDir).build()) {
            final ExcerptAppender excerptAppender = queue.acquireAppender();
            excerptAppender.writeText("first");
            excerptAppender.writeText("last");
            final ByteArrayOutputStream capture = new ByteArrayOutputStream();
            DumpQueueMain.dump(dataDir, new PrintStream(capture), Long.MAX_VALUE);
            final String capturedOutput = new String(capture.toByteArray());
            Assert.assertThat(capturedOutput, CoreMatchers.containsString("listing.highestCycle"));
            Assert.assertThat(capturedOutput, CoreMatchers.containsString("listing.lowestCycle"));
        }
    }

    private static final class CountingOutputStream extends OutputStream {
        private long bytes;

        @Override
        public void write(final int b) throws IOException {
            (bytes)++;
        }
    }
}

