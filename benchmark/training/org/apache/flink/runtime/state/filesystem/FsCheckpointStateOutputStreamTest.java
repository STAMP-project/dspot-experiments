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
package org.apache.flink.runtime.state.filesystem;


import ConfigConstants.DEFAULT_CHARSET;
import FileSystem.WriteMode;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.function.Supplier;
import org.apache.flink.core.fs.FSDataOutputStream;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.state.CheckpointStreamFactory;
import org.apache.flink.runtime.state.CheckpointStreamFactory.CheckpointStateOutputStream;
import org.apache.flink.runtime.state.StreamStateHandle;
import org.apache.flink.runtime.state.filesystem.FsCheckpointStreamFactory.FsCheckpointStateOutputStream;
import org.apache.flink.runtime.state.memory.ByteStreamStateHandle;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for the {@link FsCheckpointStateOutputStream}.
 */
public class FsCheckpointStateOutputStreamTest {
    @Rule
    public final TemporaryFolder tempDir = new TemporaryFolder();

    @Test(expected = IllegalArgumentException.class)
    public void testWrongParameters() throws Exception {
        // this should fail
        new FsCheckpointStreamFactory.FsCheckpointStateOutputStream(Path.fromLocalFile(tempDir.newFolder()), FileSystem.getLocalFileSystem(), 4000, 5000);
    }

    @Test
    public void testEmptyState() throws Exception {
        FsCheckpointStreamFactory.CheckpointStateOutputStream stream = new FsCheckpointStreamFactory.FsCheckpointStateOutputStream(Path.fromLocalFile(tempDir.newFolder()), FileSystem.getLocalFileSystem(), 1024, 512);
        StreamStateHandle handle = stream.closeAndGetHandle();
        Assert.assertTrue((handle == null));
    }

    @Test
    public void testStateBelowMemThreshold() throws Exception {
        runTest(999, 1024, 1000, false);
    }

    @Test
    public void testStateOneBufferAboveThreshold() throws Exception {
        runTest(896, 1024, 15, true);
    }

    @Test
    public void testStateAboveMemThreshold() throws Exception {
        runTest(576446, 259, 17, true);
    }

    @Test
    public void testZeroThreshold() throws Exception {
        runTest(16678, 4096, 0, true);
    }

    @Test
    public void testGetPos() throws Exception {
        FsCheckpointStreamFactory.CheckpointStateOutputStream stream = new FsCheckpointStreamFactory.FsCheckpointStateOutputStream(Path.fromLocalFile(tempDir.newFolder()), FileSystem.getLocalFileSystem(), 31, 17);
        for (int i = 0; i < 64; ++i) {
            Assert.assertEquals(i, stream.getPos());
            stream.write(66);
        }
        stream.closeAndGetHandle();
        // ----------------------------------------------------
        stream = new FsCheckpointStreamFactory.FsCheckpointStateOutputStream(Path.fromLocalFile(tempDir.newFolder()), FileSystem.getLocalFileSystem(), 31, 17);
        byte[] data = "testme!".getBytes(DEFAULT_CHARSET);
        for (int i = 0; i < 7; ++i) {
            Assert.assertEquals((i * (1 + (data.length))), stream.getPos());
            stream.write(66);
            stream.write(data);
        }
        stream.closeAndGetHandle();
    }

    /**
     * Tests that the underlying stream file is deleted upon calling close.
     */
    @Test
    public void testCleanupWhenClosingStream() throws IOException {
        final FileSystem fs = Mockito.mock(FileSystem.class);
        final FSDataOutputStream outputStream = Mockito.mock(FSDataOutputStream.class);
        final ArgumentCaptor<Path> pathCaptor = ArgumentCaptor.forClass(Path.class);
        Mockito.when(fs.create(pathCaptor.capture(), ArgumentMatchers.any(WriteMode.class))).thenReturn(outputStream);
        CheckpointStreamFactory.CheckpointStateOutputStream stream = new FsCheckpointStreamFactory.FsCheckpointStateOutputStream(Path.fromLocalFile(tempDir.newFolder()), fs, 4, 0);
        // this should create the underlying file stream
        stream.write(new byte[]{ 1, 2, 3, 4, 5 });
        Mockito.verify(fs).create(ArgumentMatchers.any(Path.class), ArgumentMatchers.any(WriteMode.class));
        stream.close();
        Mockito.verify(fs).delete(ArgumentMatchers.eq(pathCaptor.getValue()), ArgumentMatchers.anyBoolean());
    }

    /**
     * Tests that the underlying stream file is deleted if the closeAndGetHandle method fails.
     */
    @Test
    public void testCleanupWhenFailingCloseAndGetHandle() throws IOException {
        final FileSystem fs = Mockito.mock(FileSystem.class);
        final FSDataOutputStream outputStream = Mockito.mock(FSDataOutputStream.class);
        final ArgumentCaptor<Path> pathCaptor = ArgumentCaptor.forClass(Path.class);
        Mockito.when(fs.create(pathCaptor.capture(), ArgumentMatchers.any(WriteMode.class))).thenReturn(outputStream);
        Mockito.doThrow(new IOException("Test IOException.")).when(outputStream).close();
        CheckpointStreamFactory.CheckpointStateOutputStream stream = new FsCheckpointStreamFactory.FsCheckpointStateOutputStream(Path.fromLocalFile(tempDir.newFolder()), fs, 4, 0);
        // this should create the underlying file stream
        stream.write(new byte[]{ 1, 2, 3, 4, 5 });
        Mockito.verify(fs).create(ArgumentMatchers.any(Path.class), ArgumentMatchers.any(WriteMode.class));
        try {
            stream.closeAndGetHandle();
            Assert.fail("Expected IOException");
        } catch (IOException ioE) {
            // expected exception
        }
        Mockito.verify(fs).delete(ArgumentMatchers.eq(pathCaptor.getValue()), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testWriteFailsFastWhenClosed() throws Exception {
        FsCheckpointStateOutputStream stream = new FsCheckpointStateOutputStream(Path.fromLocalFile(tempDir.newFolder()), FileSystem.getLocalFileSystem(), 1024, 512);
        Assert.assertFalse(stream.isClosed());
        stream.close();
        Assert.assertTrue(stream.isClosed());
        try {
            stream.write(1);
            Assert.fail();
        } catch (IOException e) {
            // expected
        }
        try {
            stream.write(new byte[4], 1, 2);
            Assert.fail();
        } catch (IOException e) {
            // expected
        }
    }

    @Test
    public void testMixedBelowAndAboveThreshold() throws Exception {
        final byte[] state1 = new byte[1274673];
        final byte[] state2 = new byte[1];
        final byte[] state3 = new byte[0];
        final byte[] state4 = new byte[177];
        final Random rnd = new Random();
        rnd.nextBytes(state1);
        rnd.nextBytes(state2);
        rnd.nextBytes(state3);
        rnd.nextBytes(state4);
        final File directory = tempDir.newFolder();
        final Path basePath = Path.fromLocalFile(directory);
        final Supplier<CheckpointStateOutputStream> factory = () -> new FsCheckpointStateOutputStream(basePath, FileSystem.getLocalFileSystem(), 1024, 15);
        CheckpointStateOutputStream stream1 = factory.get();
        CheckpointStateOutputStream stream2 = factory.get();
        CheckpointStateOutputStream stream3 = factory.get();
        stream1.write(state1);
        stream2.write(state2);
        stream3.write(state3);
        FileStateHandle handle1 = ((FileStateHandle) (stream1.closeAndGetHandle()));
        ByteStreamStateHandle handle2 = ((ByteStreamStateHandle) (stream2.closeAndGetHandle()));
        ByteStreamStateHandle handle3 = ((ByteStreamStateHandle) (stream3.closeAndGetHandle()));
        // use with try-with-resources
        StreamStateHandle handle4;
        try (CheckpointStreamFactory.CheckpointStateOutputStream stream4 = factory.get()) {
            stream4.write(state4);
            handle4 = stream4.closeAndGetHandle();
        }
        // close before accessing handle
        CheckpointStreamFactory.CheckpointStateOutputStream stream5 = factory.get();
        stream5.write(state4);
        stream5.close();
        try {
            stream5.closeAndGetHandle();
            Assert.fail();
        } catch (IOException e) {
            // uh-huh
        }
        FsCheckpointStateOutputStreamTest.validateBytesInStream(handle1.openInputStream(), state1);
        handle1.discardState();
        Assert.assertFalse(FsCheckpointStateOutputStreamTest.isDirectoryEmpty(directory));
        FsCheckpointStateOutputStreamTest.ensureLocalFileDeleted(handle1.getFilePath());
        FsCheckpointStateOutputStreamTest.validateBytesInStream(handle2.openInputStream(), state2);
        handle2.discardState();
        Assert.assertFalse(FsCheckpointStateOutputStreamTest.isDirectoryEmpty(directory));
        // nothing was written to the stream, so it will return nothing
        Assert.assertNull(handle3);
        Assert.assertFalse(FsCheckpointStateOutputStreamTest.isDirectoryEmpty(directory));
        FsCheckpointStateOutputStreamTest.validateBytesInStream(handle4.openInputStream(), state4);
        handle4.discardState();
        Assert.assertTrue(FsCheckpointStateOutputStreamTest.isDirectoryEmpty(directory));
    }

    // ------------------------------------------------------------------------
    // Not deleting parent directories
    // ------------------------------------------------------------------------
    /**
     * This test checks that the stream does not check and clean the parent directory
     * when encountering a write error.
     */
    @Test
    public void testStreamDoesNotTryToCleanUpParentOnError() throws Exception {
        final File directory = tempDir.newFolder();
        // prevent creation of files in that directory
        Assert.assertTrue(directory.setWritable(false, true));
        FsCheckpointStateOutputStreamTest.checkDirectoryNotWritable(directory);
        FileSystem fs = Mockito.spy(FileSystem.getLocalFileSystem());
        FsCheckpointStateOutputStream stream1 = new FsCheckpointStateOutputStream(Path.fromLocalFile(directory), fs, 1024, 1);
        FsCheckpointStateOutputStream stream2 = new FsCheckpointStateOutputStream(Path.fromLocalFile(directory), fs, 1024, 1);
        stream1.write(new byte[61]);
        stream2.write(new byte[61]);
        try {
            stream1.closeAndGetHandle();
            Assert.fail("this should fail with an exception");
        } catch (IOException ignored) {
        }
        stream2.close();
        // no delete call must have happened
        Mockito.verify(fs, Mockito.times(0)).delete(ArgumentMatchers.any(Path.class), ArgumentMatchers.anyBoolean());
        // the directory must still exist as a proper directory
        Assert.assertTrue(directory.exists());
        Assert.assertTrue(directory.isDirectory());
    }
}

