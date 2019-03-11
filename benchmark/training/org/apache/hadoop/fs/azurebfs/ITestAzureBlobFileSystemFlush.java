/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs.azurebfs;


import StreamCapabilities.DROPBEHIND;
import StreamCapabilities.HFLUSH;
import StreamCapabilities.HSYNC;
import StreamCapabilities.READAHEAD;
import StreamCapabilities.UNBUFFER;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test flush operation.
 * This class cannot be run in parallel test mode--check comments in
 * testWriteHeavyBytesToFileSyncFlush().
 */
public class ITestAzureBlobFileSystemFlush extends AbstractAbfsScaleTest {
    private static final int BASE_SIZE = 1024;

    private static final int ONE_THOUSAND = 1000;

    private static final int TEST_BUFFER_SIZE = (5 * (ITestAzureBlobFileSystemFlush.ONE_THOUSAND)) * (ITestAzureBlobFileSystemFlush.BASE_SIZE);

    private static final int ONE_MB = 1024 * 1024;

    private static final int FLUSH_TIMES = 200;

    private static final int THREAD_SLEEP_TIME = 1000;

    private static final int TEST_FILE_LENGTH = (1024 * 1024) * 8;

    private static final int WAITING_TIME = 1000;

    public ITestAzureBlobFileSystemFlush() throws Exception {
        super();
    }

    @Test
    public void testAbfsOutputStreamAsyncFlushWithRetainUncommittedData() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        final Path testFilePath = path(methodName.getMethodName());
        final byte[] b;
        try (FSDataOutputStream stream = fs.create(testFilePath)) {
            b = new byte[ITestAzureBlobFileSystemFlush.TEST_BUFFER_SIZE];
            new Random().nextBytes(b);
            for (int i = 0; i < 2; i++) {
                stream.write(b);
                for (int j = 0; j < (ITestAzureBlobFileSystemFlush.FLUSH_TIMES); j++) {
                    stream.flush();
                    Thread.sleep(10);
                }
            }
        }
        final byte[] r = new byte[ITestAzureBlobFileSystemFlush.TEST_BUFFER_SIZE];
        try (FSDataInputStream inputStream = fs.open(testFilePath, (4 * (ITestAzureBlobFileSystemFlush.ONE_MB)))) {
            while ((inputStream.available()) != 0) {
                int result = inputStream.read(r);
                Assert.assertNotEquals("read returned -1", (-1), result);
                Assert.assertArrayEquals("buffer read from stream", r, b);
            } 
        }
    }

    @Test
    public void testAbfsOutputStreamSyncFlush() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        final Path testFilePath = path(methodName.getMethodName());
        final byte[] b;
        try (FSDataOutputStream stream = fs.create(testFilePath)) {
            b = new byte[ITestAzureBlobFileSystemFlush.TEST_BUFFER_SIZE];
            new Random().nextBytes(b);
            stream.write(b);
            for (int i = 0; i < (ITestAzureBlobFileSystemFlush.FLUSH_TIMES); i++) {
                stream.hsync();
                stream.hflush();
                Thread.sleep(10);
            }
        }
        final byte[] r = new byte[ITestAzureBlobFileSystemFlush.TEST_BUFFER_SIZE];
        try (FSDataInputStream inputStream = fs.open(testFilePath, (4 * (ITestAzureBlobFileSystemFlush.ONE_MB)))) {
            int result = inputStream.read(r);
            Assert.assertNotEquals((-1), result);
            Assert.assertArrayEquals(r, b);
        }
    }

    @Test
    public void testWriteHeavyBytesToFileSyncFlush() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        final Path testFilePath = path(methodName.getMethodName());
        ExecutorService es;
        try (FSDataOutputStream stream = fs.create(testFilePath)) {
            es = Executors.newFixedThreadPool(10);
            final byte[] b = new byte[ITestAzureBlobFileSystemFlush.TEST_BUFFER_SIZE];
            new Random().nextBytes(b);
            List<Future<Void>> tasks = new ArrayList<>();
            for (int i = 0; i < (ITestAzureBlobFileSystemFlush.FLUSH_TIMES); i++) {
                Callable<Void> callable = new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        stream.write(b);
                        return null;
                    }
                };
                tasks.add(es.submit(callable));
            }
            boolean shouldStop = false;
            while (!shouldStop) {
                shouldStop = true;
                for (Future<Void> task : tasks) {
                    if (!(task.isDone())) {
                        stream.hsync();
                        shouldStop = false;
                        Thread.sleep(ITestAzureBlobFileSystemFlush.THREAD_SLEEP_TIME);
                    }
                }
            } 
            tasks.clear();
        }
        es.shutdownNow();
        FileStatus fileStatus = fs.getFileStatus(testFilePath);
        long expectedWrites = ((long) (ITestAzureBlobFileSystemFlush.TEST_BUFFER_SIZE)) * (ITestAzureBlobFileSystemFlush.FLUSH_TIMES);
        Assert.assertEquals(("Wrong file length in " + testFilePath), expectedWrites, fileStatus.getLen());
    }

    @Test
    public void testWriteHeavyBytesToFileAsyncFlush() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        ExecutorService es = Executors.newFixedThreadPool(10);
        final Path testFilePath = path(methodName.getMethodName());
        try (FSDataOutputStream stream = fs.create(testFilePath)) {
            final byte[] b = new byte[ITestAzureBlobFileSystemFlush.TEST_BUFFER_SIZE];
            new Random().nextBytes(b);
            List<Future<Void>> tasks = new ArrayList<>();
            for (int i = 0; i < (ITestAzureBlobFileSystemFlush.FLUSH_TIMES); i++) {
                Callable<Void> callable = new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        stream.write(b);
                        return null;
                    }
                };
                tasks.add(es.submit(callable));
            }
            boolean shouldStop = false;
            while (!shouldStop) {
                shouldStop = true;
                for (Future<Void> task : tasks) {
                    if (!(task.isDone())) {
                        stream.flush();
                        shouldStop = false;
                    }
                }
            } 
            Thread.sleep(ITestAzureBlobFileSystemFlush.THREAD_SLEEP_TIME);
            tasks.clear();
        }
        es.shutdownNow();
        FileStatus fileStatus = fs.getFileStatus(testFilePath);
        Assert.assertEquals((((long) (ITestAzureBlobFileSystemFlush.TEST_BUFFER_SIZE)) * (ITestAzureBlobFileSystemFlush.FLUSH_TIMES)), fileStatus.getLen());
    }

    @Test
    public void testFlushWithFlushEnabled() throws Exception {
        testFlush(true);
    }

    @Test
    public void testFlushWithFlushDisabled() throws Exception {
        testFlush(false);
    }

    @Test
    public void testHflushWithFlushEnabled() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        byte[] buffer = getRandomBytesArray();
        String fileName = UUID.randomUUID().toString();
        final Path testFilePath = path(fileName);
        try (FSDataOutputStream stream = getStreamAfterWrite(fs, testFilePath, buffer, true)) {
            stream.hflush();
            validate(fs, testFilePath, buffer, true);
        }
    }

    @Test
    public void testHflushWithFlushDisabled() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        byte[] buffer = getRandomBytesArray();
        final Path testFilePath = path(methodName.getMethodName());
        try (FSDataOutputStream stream = getStreamAfterWrite(fs, testFilePath, buffer, false)) {
            stream.hflush();
            validate(fs, testFilePath, buffer, false);
        }
    }

    @Test
    public void testHsyncWithFlushEnabled() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        byte[] buffer = getRandomBytesArray();
        final Path testFilePath = path(methodName.getMethodName());
        try (FSDataOutputStream stream = getStreamAfterWrite(fs, testFilePath, buffer, true)) {
            stream.hsync();
            validate(fs, testFilePath, buffer, true);
        }
    }

    @Test
    public void testStreamCapabilitiesWithFlushDisabled() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        byte[] buffer = getRandomBytesArray();
        final Path testFilePath = path(methodName.getMethodName());
        try (FSDataOutputStream stream = getStreamAfterWrite(fs, testFilePath, buffer, false)) {
            Assert.assertFalse(stream.hasCapability(HFLUSH));
            Assert.assertFalse(stream.hasCapability(HSYNC));
            Assert.assertFalse(stream.hasCapability(DROPBEHIND));
            Assert.assertFalse(stream.hasCapability(READAHEAD));
            Assert.assertFalse(stream.hasCapability(UNBUFFER));
        }
    }

    @Test
    public void testStreamCapabilitiesWithFlushEnabled() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        byte[] buffer = getRandomBytesArray();
        final Path testFilePath = path(methodName.getMethodName());
        try (FSDataOutputStream stream = getStreamAfterWrite(fs, testFilePath, buffer, true)) {
            Assert.assertTrue(stream.hasCapability(HFLUSH));
            Assert.assertTrue(stream.hasCapability(HSYNC));
            Assert.assertFalse(stream.hasCapability(DROPBEHIND));
            Assert.assertFalse(stream.hasCapability(READAHEAD));
            Assert.assertFalse(stream.hasCapability(UNBUFFER));
        }
    }

    @Test
    public void testHsyncWithFlushDisabled() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        byte[] buffer = getRandomBytesArray();
        final Path testFilePath = path(methodName.getMethodName());
        try (FSDataOutputStream stream = getStreamAfterWrite(fs, testFilePath, buffer, false)) {
            stream.hsync();
            validate(fs, testFilePath, buffer, false);
        }
    }
}

