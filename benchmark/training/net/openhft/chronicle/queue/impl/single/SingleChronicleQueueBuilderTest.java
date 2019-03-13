package net.openhft.chronicle.queue.impl.single;


import OS.USER_DIR;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.openhft.chronicle.core.io.IOTools;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.wire.Wires;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class SingleChronicleQueueBuilderTest {
    private static final String TEST_QUEUE_FILE = "src/test/resources/tr2/20170320.cq4";

    @Test
    public void shouldDetermineQueueDirectoryFromQueueFile() {
        final Path path = Paths.get(USER_DIR, SingleChronicleQueueBuilderTest.TEST_QUEUE_FILE);
        try (final ChronicleQueue queue = ChronicleQueue.singleBuilder(path).testBlockSize().build()) {
            Assert.assertThat(queue.createTailer().readingDocument().isPresent(), CoreMatchers.is(false));
        } finally {
            IOTools.deleteDirWithFiles(path.toFile(), 20);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfQueuePathIsFileWithIncorrectExtension() throws Exception {
        final File tempFile = File.createTempFile(SingleChronicleQueueBuilderTest.class.getSimpleName(), ".txt");
        tempFile.deleteOnExit();
        SingleChronicleQueueBuilder.binary(tempFile);
    }

    @Test
    public void setAllNullFields() {
        SingleChronicleQueueBuilder b1 = SingleChronicleQueueBuilder.builder();
        SingleChronicleQueueBuilder b2 = SingleChronicleQueueBuilder.builder();
        b1.blockSize(1234567);
        b2.bufferCapacity(98765);
        b2.setAllNullFields(b1);
        Assert.assertEquals(1234567, b2.blockSize());
        Assert.assertEquals(98765, b2.bufferCapacity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setAllNullFieldsShouldFailWithDifferentHierarchy() {
        SingleChronicleQueueBuilder b1 = Wires.tupleFor(SingleChronicleQueueBuilder.class, "ChronicleQueueBuilder");
        SingleChronicleQueueBuilder b2 = SingleChronicleQueueBuilder.builder();
        b2.bufferCapacity(98765);
        b1.blockSize(1234567);
        b2.setAllNullFields(b1);
    }
}

