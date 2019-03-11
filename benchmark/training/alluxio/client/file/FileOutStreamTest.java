/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.client.file;


import ExceptionMessage.FAILED_CACHE;
import ExceptionMessage.NO_WORKER_AVAILABLE;
import PreconditionMessage.ERR_BUFFER_STATE;
import PreconditionMessage.ERR_WRITE_BUFFER_NULL;
import WriteType.ASYNC_THROUGH;
import WriteType.CACHE_THROUGH;
import WriteType.MUST_CACHE;
import alluxio.AlluxioURI;
import alluxio.ConfigurationTestUtils;
import alluxio.LoginUserRule;
import alluxio.client.WriteType;
import alluxio.client.block.AlluxioBlockStore;
import alluxio.client.block.BlockWorkerInfo;
import alluxio.client.block.stream.BlockOutStream;
import alluxio.client.block.stream.TestBlockOutStream;
import alluxio.client.block.stream.TestUnderFileSystemFileOutStream;
import alluxio.client.block.stream.UnderFileSystemFileOutStream;
import alluxio.client.file.options.OutStreamOptions;
import alluxio.client.file.policy.FileWriteLocationPolicy;
import alluxio.conf.InstancedConfiguration;
import alluxio.exception.status.UnavailableException;
import alluxio.grpc.CompleteFilePOptions;
import alluxio.grpc.ScheduleAsyncPersistencePOptions;
import alluxio.util.io.BufferUtils;
import alluxio.wire.WorkerNetAddress;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Tests for the {@link FileOutStream} class.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ FileSystemContext.class, FileSystemMasterClient.class, AlluxioBlockStore.class, UnderFileSystemFileOutStream.class })
public class FileOutStreamTest {
    private static InstancedConfiguration sConf = ConfigurationTestUtils.defaults();

    @Rule
    public LoginUserRule mLoginUser = new LoginUserRule("Test", FileOutStreamTest.sConf);

    @Rule
    public ExpectedException mException = ExpectedException.none();

    private static final long BLOCK_LENGTH = 100L;

    private static final AlluxioURI FILE_NAME = new AlluxioURI("/file");

    private FileSystemContext mFileSystemContext;

    private AlluxioBlockStore mBlockStore;

    private FileSystemMasterClient mFileSystemMasterClient;

    private Map<Long, TestBlockOutStream> mAlluxioOutStreamMap;

    private TestUnderFileSystemFileOutStream mUnderStorageOutputStream;

    private AtomicBoolean mUnderStorageFlushed;

    private FileOutStream mTestStream;

    /**
     * Tests that a single byte is written to the out stream correctly.
     */
    @Test
    public void singleByteWrite() throws Exception {
        mTestStream.write(5);
        mTestStream.close();
        Assert.assertArrayEquals(new byte[]{ 5 }, mAlluxioOutStreamMap.get(0L).getWrittenData());
    }

    /**
     * Tests that many bytes, written one at a time, are written to the out streams correctly.
     */
    @Test
    public void manyBytesWrite() throws IOException {
        int bytesToWrite = ((int) (((FileOutStreamTest.BLOCK_LENGTH) * 5) + ((FileOutStreamTest.BLOCK_LENGTH) / 2)));
        for (int i = 0; i < bytesToWrite; i++) {
            mTestStream.write(i);
        }
        mTestStream.close();
        verifyIncreasingBytesWritten(bytesToWrite);
    }

    /**
     * Tests that writing a buffer all at once will write bytes to the out streams correctly.
     */
    @Test
    public void writeBuffer() throws IOException {
        int bytesToWrite = ((int) (((FileOutStreamTest.BLOCK_LENGTH) * 5) + ((FileOutStreamTest.BLOCK_LENGTH) / 2)));
        mTestStream.write(BufferUtils.getIncreasingByteArray(bytesToWrite));
        mTestStream.close();
        verifyIncreasingBytesWritten(bytesToWrite);
    }

    /**
     * Tests writing a buffer at an offset.
     */
    @Test
    public void writeOffset() throws IOException {
        int bytesToWrite = ((int) (((FileOutStreamTest.BLOCK_LENGTH) * 5) + ((FileOutStreamTest.BLOCK_LENGTH) / 2)));
        int offset = ((int) ((FileOutStreamTest.BLOCK_LENGTH) / 3));
        mTestStream.write(BufferUtils.getIncreasingByteArray((bytesToWrite + offset)), offset, bytesToWrite);
        mTestStream.close();
        verifyIncreasingBytesWritten(offset, bytesToWrite);
    }

    /**
     * Tests that {@link FileOutStream#close()} will close but not cancel the underlying out streams.
     * Also checks that {@link FileOutStream#close()} persists and completes the file.
     */
    @Test
    public void close() throws Exception {
        mTestStream.write(BufferUtils.getIncreasingByteArray(((int) ((FileOutStreamTest.BLOCK_LENGTH) * 1.5))));
        mTestStream.close();
        for (long streamIndex = 0; streamIndex < 2; streamIndex++) {
            Assert.assertFalse(mAlluxioOutStreamMap.get(streamIndex).isCanceled());
            Assert.assertTrue(mAlluxioOutStreamMap.get(streamIndex).isClosed());
        }
        Mockito.verify(mFileSystemMasterClient).completeFile(ArgumentMatchers.eq(FileOutStreamTest.FILE_NAME), ArgumentMatchers.any(CompleteFilePOptions.class));
    }

    /**
     * Tests that {@link FileOutStream#cancel()} will cancel and close the underlying out streams, and
     * delete from the under file system when the delegation flag is set. Also makes sure that
     * cancel() doesn't persist or complete the file.
     */
    @Test
    public void cancelWithDelegation() throws Exception {
        mTestStream.write(BufferUtils.getIncreasingByteArray(((int) ((FileOutStreamTest.BLOCK_LENGTH) * 1.5))));
        mTestStream.cancel();
        for (long streamIndex = 0; streamIndex < 2; streamIndex++) {
            Assert.assertTrue(mAlluxioOutStreamMap.get(streamIndex).isClosed());
            Assert.assertTrue(mAlluxioOutStreamMap.get(streamIndex).isCanceled());
        }
        // Don't complete the file if the stream was canceled
        Mockito.verify(mFileSystemMasterClient, Mockito.times(0)).completeFile(ArgumentMatchers.any(AlluxioURI.class), ArgumentMatchers.any(CompleteFilePOptions.class));
    }

    /**
     * Tests that {@link FileOutStream#flush()} will flush the under store stream.
     */
    @Test
    public void flush() throws IOException {
        Assert.assertFalse(mUnderStorageFlushed.get());
        mTestStream.flush();
        Assert.assertTrue(mUnderStorageFlushed.get());
    }

    /**
     * Tests that if an exception is thrown by the underlying out stream, and the user is using
     * {@link UnderStorageType#NO_PERSIST} for their under storage type, the correct exception
     * message will be thrown.
     */
    @Test
    public void cacheWriteExceptionNonSyncPersist() throws IOException {
        OutStreamOptions options = OutStreamOptions.defaults(FileOutStreamTest.sConf).setBlockSizeBytes(FileOutStreamTest.BLOCK_LENGTH).setWriteType(MUST_CACHE);
        BlockOutStream stream = Mockito.mock(BlockOutStream.class);
        Mockito.when(mBlockStore.getOutStream(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(OutStreamOptions.class))).thenReturn(stream);
        mTestStream = createTestStream(FileOutStreamTest.FILE_NAME, options);
        Mockito.when(stream.remaining()).thenReturn(FileOutStreamTest.BLOCK_LENGTH);
        Mockito.doThrow(new IOException("test error")).when(stream).write(((byte) (7)));
        try {
            mTestStream.write(7);
            Assert.fail("the test should fail");
        } catch (IOException e) {
            Assert.assertEquals(FAILED_CACHE.getMessage("test error"), e.getMessage());
        }
    }

    /**
     * Tests that if an exception is thrown by the underlying out stream, and the user is using
     * {@link UnderStorageType#SYNC_PERSIST} for their under storage type, the error is recovered
     * from by writing the data to the under storage out stream.
     */
    @Test
    public void cacheWriteExceptionSyncPersist() throws IOException {
        BlockOutStream stream = Mockito.mock(BlockOutStream.class);
        Mockito.when(mBlockStore.getOutStream(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(OutStreamOptions.class))).thenReturn(stream);
        Mockito.when(stream.remaining()).thenReturn(FileOutStreamTest.BLOCK_LENGTH);
        Mockito.doThrow(new IOException("test error")).when(stream).write(((byte) (7)));
        mTestStream.write(7);
        mTestStream.write(8);
        Assert.assertArrayEquals(new byte[]{ 7, 8 }, mUnderStorageOutputStream.getWrittenData());
        // The cache stream is written to only once - the FileInStream gives up on it after it throws
        // the first exception.
        Mockito.verify(stream, Mockito.times(1)).write(ArgumentMatchers.anyByte());
    }

    /**
     * Tests that write only writes a byte.
     */
    @Test
    public void truncateWrite() throws IOException {
        // Only writes the lowest byte
        mTestStream.write(536870656);
        mTestStream.write(536870657);
        mTestStream.close();
        verifyIncreasingBytesWritten(2);
    }

    /**
     * Tests that the correct exception is thrown when a buffer is written with invalid offset/length.
     */
    @Test
    public void writeBadBufferOffset() throws IOException {
        try {
            mTestStream.write(new byte[10], 5, 6);
            Assert.fail("buffer write with invalid offset/length should fail");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(String.format(ERR_BUFFER_STATE.toString(), 10, 5, 6), e.getMessage());
        }
    }

    /**
     * Tests that writing a null buffer throws the correct exception.
     */
    @Test
    public void writeNullBuffer() throws IOException {
        try {
            mTestStream.write(null);
            Assert.fail("writing null should fail");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(ERR_WRITE_BUFFER_NULL.toString(), e.getMessage());
        }
    }

    /**
     * Tests that writing a null buffer with offset/length information throws the correct exception.
     */
    @Test
    public void writeNullBufferOffset() throws IOException {
        try {
            mTestStream.write(null, 0, 0);
            Assert.fail("writing null should fail");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(ERR_WRITE_BUFFER_NULL.toString(), e.getMessage());
        }
    }

    /**
     * Tests that the async write invokes the expected client APIs.
     */
    @Test
    public void asyncWrite() throws Exception {
        OutStreamOptions options = OutStreamOptions.defaults(FileOutStreamTest.sConf).setBlockSizeBytes(FileOutStreamTest.BLOCK_LENGTH).setWriteType(ASYNC_THROUGH);
        mTestStream = createTestStream(FileOutStreamTest.FILE_NAME, options);
        mTestStream.write(BufferUtils.getIncreasingByteArray(((int) ((FileOutStreamTest.BLOCK_LENGTH) * 1.5))));
        mTestStream.close();
        Mockito.verify(mFileSystemMasterClient).completeFile(ArgumentMatchers.eq(FileOutStreamTest.FILE_NAME), ArgumentMatchers.any(CompleteFilePOptions.class));
        Mockito.verify(mFileSystemMasterClient).scheduleAsyncPersist(ArgumentMatchers.eq(FileOutStreamTest.FILE_NAME), ArgumentMatchers.any(ScheduleAsyncPersistencePOptions.class));
    }

    /**
     * Tests that the number of bytes written is correct when the stream is created with different
     * under storage types.
     */
    @Test
    public void getBytesWrittenWithDifferentUnderStorageType() throws IOException {
        for (WriteType type : WriteType.values()) {
            OutStreamOptions options = OutStreamOptions.defaults(FileOutStreamTest.sConf).setBlockSizeBytes(FileOutStreamTest.BLOCK_LENGTH).setWriteType(type).setUfsPath(FileOutStreamTest.FILE_NAME.getPath());
            mTestStream = createTestStream(FileOutStreamTest.FILE_NAME, options);
            mTestStream.write(BufferUtils.getIncreasingByteArray(((int) (FileOutStreamTest.BLOCK_LENGTH))));
            mTestStream.flush();
            Assert.assertEquals(FileOutStreamTest.BLOCK_LENGTH, mTestStream.getBytesWritten());
        }
    }

    @Test
    public void createWithNoWorker() throws Exception {
        OutStreamOptions options = OutStreamOptions.defaults(FileOutStreamTest.sConf).setLocationPolicy(new FileWriteLocationPolicy() {
            @Override
            public WorkerNetAddress getWorkerForNextBlock(Iterable<BlockWorkerInfo> workerInfoList, long blockSizeBytes) {
                return null;
            }
        }).setWriteType(CACHE_THROUGH);
        mException.expect(UnavailableException.class);
        mException.expectMessage(NO_WORKER_AVAILABLE.getMessage());
        mTestStream = createTestStream(FileOutStreamTest.FILE_NAME, options);
    }
}

