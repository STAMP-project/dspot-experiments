/**
 * Copyright 2012 Square, Inc.
 */
package com.squareup.tape2;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class QueueFileLoadingTest {
    private File testFile;

    @Test
    public void testMissingFileInitializes() throws Exception {
        testFile = File.createTempFile(QueueTestUtils.FRESH_SERIALIZED_QUEUE, "test");
        Assert.assertTrue(testFile.delete());
        Assert.assertFalse(testFile.exists());
        QueueFile queue = new QueueFile.Builder(testFile).build();
        Assert.assertEquals(0, queue.size());
        Assert.assertTrue(testFile.exists());
        queue.close();
    }

    @Test
    public void testEmptyFileInitializes() throws Exception {
        testFile = QueueTestUtils.copyTestFile(QueueTestUtils.EMPTY_SERIALIZED_QUEUE);
        QueueFile queue = new QueueFile.Builder(testFile).build();
        Assert.assertEquals(0, queue.size());
        queue.close();
    }

    @Test
    public void testSingleEntryFileInitializes() throws Exception {
        testFile = QueueTestUtils.copyTestFile(QueueTestUtils.ONE_ENTRY_SERIALIZED_QUEUE);
        QueueFile queue = new QueueFile.Builder(testFile).build();
        Assert.assertEquals(1, queue.size());
        queue.close();
    }

    @Test(expected = IOException.class)
    public void testTruncatedEmptyFileThrows() throws Exception {
        testFile = QueueTestUtils.copyTestFile(QueueTestUtils.TRUNCATED_EMPTY_SERIALIZED_QUEUE);
        new QueueFile.Builder(testFile).build();
    }

    @Test(expected = IOException.class)
    public void testTruncatedOneEntryFileThrows() throws Exception {
        testFile = QueueTestUtils.copyTestFile(QueueTestUtils.TRUNCATED_ONE_ENTRY_SERIALIZED_QUEUE);
        new QueueFile.Builder(testFile).build();
    }

    @Test(expected = IOException.class)
    public void testCreateWithReadOnlyFileThrowsException() throws Exception {
        testFile = QueueTestUtils.copyTestFile(QueueTestUtils.TRUNCATED_ONE_ENTRY_SERIALIZED_QUEUE);
        Assert.assertTrue(testFile.setWritable(false));
        // Should throw an exception.
        new QueueFile.Builder(testFile).build();
    }

    @Test(expected = IOException.class)
    public void testAddWithReadOnlyFileMissesMonitor() throws Exception {
        testFile = QueueTestUtils.copyTestFile(QueueTestUtils.EMPTY_SERIALIZED_QUEUE);
        QueueFile qf = new QueueFile.Builder(testFile).build();
        // Should throw an exception.
        FileObjectQueue<String> queue = new FileObjectQueue(qf, new FileObjectQueue.Converter<String>() {
            @Override
            public String from(byte[] bytes) throws IOException {
                return null;
            }

            @Override
            public void toStream(String o, OutputStream bytes) throws IOException {
                throw new IOException("fake Permission denied");
            }
        });
        // Should throw an exception.
        try {
            queue.add("trouble");
        } finally {
            queue.close();
        }
    }
}

