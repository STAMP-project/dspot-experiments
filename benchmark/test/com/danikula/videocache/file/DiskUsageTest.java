package com.danikula.videocache.file;


import com.danikula.videocache.BaseTest;
import java.io.File;
import org.junit.Test;


/**
 * Tests for implementations of {@link DiskUsage}.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class DiskUsageTest extends BaseTest {
    private File cacheFolder;

    @Test
    public void testMaxSizeCacheLimit() throws Exception {
        DiskUsage diskUsage = new TotalSizeLruDiskUsage(300);
        long now = System.currentTimeMillis();
        createFile(file("b"), 101, (now - 10000));
        createFile(file("c"), 102, (now - 8000));
        createFile(file("a"), 104, (now - 4000));// exceeds

        diskUsage.touch(file("c"));
        waitForAsyncTrimming();
        assertThat(file("b")).doesNotExist();
        assertThat(file("c")).exists();
        assertThat(file("a")).exists();
        createFile(file("d"), 500, now);// exceeds all

        diskUsage.touch(file("d"));
        waitForAsyncTrimming();
        assertThat(file("a")).doesNotExist();
        assertThat(file("c")).doesNotExist();
        assertThat(file("d")).doesNotExist();
    }

    @Test
    public void testMaxFilesCount() throws Exception {
        DiskUsage diskUsage = new TotalCountLruDiskUsage(2);
        long now = System.currentTimeMillis();
        createFile(file("b"), 101, (now - 10000));
        createFile(file("c"), 102, (now - 8000));
        createFile(file("a"), 104, (now - 4000));
        diskUsage.touch(file("c"));
        waitForAsyncTrimming();
        assertThat(file("b")).doesNotExist();
        assertThat(file("a")).exists();
        assertThat(file("c")).exists();
        createFile(file("d"), 500, now);
        diskUsage.touch(file("d"));
        waitForAsyncTrimming();
        assertThat(file("a")).doesNotExist();
        assertThat(file("c")).exists();
        assertThat(file("d")).exists();
    }

    @Test
    public void testTouch() throws Exception {
        DiskUsage diskUsage = new TotalCountLruDiskUsage(2);
        long now = System.currentTimeMillis();
        createFile(file("b"), 101, (now - 10000));
        createFile(file("c"), 102, (now - 8000));
        createFile(file("a"), 104, (now - 4000));
        diskUsage.touch(file("b"));
        waitForAsyncTrimming();
        assertThat(file("b")).exists();
        assertThat(file("a")).exists();
        assertThat(file("c")).doesNotExist();
        Thread.sleep(1000);// last modified is rounded to seconds, so wait for sec

        new TotalCountLruDiskUsage(1).touch(file("a"));
        waitForAsyncTrimming();
        assertThat(file("a")).exists();
        assertThat(file("b")).doesNotExist();
    }
}

