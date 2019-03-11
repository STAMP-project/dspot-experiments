package net.openhft.chronicle.queue.impl.single;


import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import net.openhft.chronicle.queue.DirectoryUtils;
import org.junit.Test;


public final class FileModificationTimeTest {
    private final AtomicInteger fileCount = new AtomicInteger();

    @Test
    public void shouldUpdateDirectoryModificationTime() throws Exception {
        final File dir = DirectoryUtils.tempDir(FileModificationTimeTest.class.getSimpleName());
        dir.mkdirs();
        final long startModTime = dir.lastModified();
        modifyDirectoryContentsUntilVisible(dir, startModTime);
        final long afterOneFile = dir.lastModified();
        modifyDirectoryContentsUntilVisible(dir, afterOneFile);
    }
}

