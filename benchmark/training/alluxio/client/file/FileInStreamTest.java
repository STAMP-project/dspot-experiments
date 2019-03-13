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


import PreconditionMessage.ERR_BUFFER_STATE;
import PreconditionMessage.ERR_SEEK_NEGATIVE;
import PreconditionMessage.ERR_SEEK_PAST_END_OF_FILE;
import ReadPType.CACHE_PROMOTE;
import alluxio.ConfigurationTestUtils;
import alluxio.client.block.AlluxioBlockStore;
import alluxio.client.block.stream.BlockInStream;
import alluxio.client.block.stream.BlockInStream.BlockInStreamSource;
import alluxio.client.block.stream.TestBlockInStream;
import alluxio.client.file.options.InStreamOptions;
import alluxio.conf.InstancedConfiguration;
import alluxio.exception.status.UnavailableException;
import alluxio.grpc.OpenFilePOptions;
import alluxio.util.io.BufferUtils;
import alluxio.wire.FileInfo;
import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;


/**
 * Tests for the {@link FileInStream} class.
 *
 * It is a parameterized test that checks different caching behaviors when the blocks are located at
 * different locations.
 */
@RunWith(Parameterized.class)
@PrepareForTest({ FileSystemContext.class, AlluxioBlockStore.class, BlockInStream.class })
public final class FileInStreamTest {
    @Rule
    public PowerMockRule mPowerMockRule = new PowerMockRule();

    private static final long BLOCK_LENGTH = 100L;

    private static final long FILE_LENGTH = 350L;

    private static final long NUM_STREAMS = (((FileInStreamTest.FILE_LENGTH) - 1) / (FileInStreamTest.BLOCK_LENGTH)) + 1;

    private AlluxioBlockStore mBlockStore;

    private BlockInStreamSource mBlockSource;

    private FileSystemContext mContext;

    private FileInfo mInfo;

    private URIStatus mStatus;

    private static InstancedConfiguration sConf = ConfigurationTestUtils.defaults();

    private List<TestBlockInStream> mInStreams;

    private FileInStream mTestStream;

    /**
     *
     *
     * @param blockSource
     * 		the source of the block to read
     */
    public FileInStreamTest(BlockInStreamSource blockSource) {
        mBlockSource = blockSource;
    }

    /**
     * Tests that reading through the file one byte at a time will yield the correct data.
     */
    @Test
    public void singleByteRead() throws Exception {
        for (int i = 0; i < (FileInStreamTest.FILE_LENGTH); i++) {
            Assert.assertEquals((i & 255), mTestStream.read());
        }
        mTestStream.close();
    }

    /**
     * Tests that reading half of a file works.
     */
    @Test
    public void readHalfFile() throws Exception {
        testReadBuffer(((int) ((FileInStreamTest.FILE_LENGTH) / 2)));
    }

    /**
     * Tests that reading a part of a file works.
     */
    @Test
    public void readPartialBlock() throws Exception {
        testReadBuffer(((int) ((FileInStreamTest.BLOCK_LENGTH) / 2)));
    }

    /**
     * Tests that reading the complete block works.
     */
    @Test
    public void readBlock() throws Exception {
        testReadBuffer(((int) (FileInStreamTest.BLOCK_LENGTH)));
    }

    /**
     * Tests that reading the complete file works.
     */
    @Test
    public void readFile() throws Exception {
        testReadBuffer(((int) (FileInStreamTest.FILE_LENGTH)));
    }

    /**
     * Tests that reading a buffer at an offset writes the bytes to the correct places.
     */
    @Test
    public void readOffset() throws IOException {
        int offset = ((int) ((FileInStreamTest.BLOCK_LENGTH) / 3));
        int len = ((int) (FileInStreamTest.BLOCK_LENGTH));
        byte[] buffer = new byte[offset + len];
        // Create expectedBuffer containing `offset` 0's followed by `len` increasing bytes
        byte[] expectedBuffer = new byte[offset + len];
        System.arraycopy(BufferUtils.getIncreasingByteArray(len), 0, expectedBuffer, offset, len);
        mTestStream.read(buffer, offset, len);
        Assert.assertArrayEquals(expectedBuffer, buffer);
    }

    /**
     * Read through the file in small chunks and verify each chunk.
     */
    @Test
    public void readManyChunks() throws IOException {
        int chunksize = 10;
        // chunksize must divide FILE_LENGTH evenly for this test to work
        Assert.assertEquals(0, ((FileInStreamTest.FILE_LENGTH) % chunksize));
        byte[] buffer = new byte[chunksize];
        int offset = 0;
        for (int i = 0; i < ((FileInStreamTest.FILE_LENGTH) / chunksize); i++) {
            mTestStream.read(buffer, 0, chunksize);
            Assert.assertArrayEquals(BufferUtils.getIncreasingByteArray(offset, chunksize), buffer);
            offset += chunksize;
        }
        mTestStream.close();
    }

    /**
     * Tests that {@link FileInStream#remaining()} is correctly updated during reads, skips, and
     * seeks.
     */
    @Test
    public void testRemaining() throws IOException {
        Assert.assertEquals(FileInStreamTest.FILE_LENGTH, mTestStream.remaining());
        mTestStream.read();
        Assert.assertEquals(((FileInStreamTest.FILE_LENGTH) - 1), mTestStream.remaining());
        mTestStream.read(new byte[150]);
        Assert.assertEquals(((FileInStreamTest.FILE_LENGTH) - 151), mTestStream.remaining());
        mTestStream.skip(140);
        Assert.assertEquals(((FileInStreamTest.FILE_LENGTH) - 291), mTestStream.remaining());
        mTestStream.seek(310);
        Assert.assertEquals(((FileInStreamTest.FILE_LENGTH) - 310), mTestStream.remaining());
        mTestStream.seek(130);
        Assert.assertEquals(((FileInStreamTest.FILE_LENGTH) - 130), mTestStream.remaining());
    }

    /**
     * Tests seek, particularly that seeking over part of a block will cause us not to cache it, and
     * cancels the existing cache stream.
     */
    @Test
    public void testSeek() throws IOException {
        int seekAmount = ((int) ((FileInStreamTest.BLOCK_LENGTH) / 2));
        int readAmount = ((int) ((FileInStreamTest.BLOCK_LENGTH) * 2));
        byte[] buffer = new byte[readAmount];
        // Seek halfway into block 1
        mTestStream.seek(seekAmount);
        // Read two blocks from 0.5 to 2.5
        mTestStream.read(buffer);
        Assert.assertArrayEquals(BufferUtils.getIncreasingByteArray(seekAmount, readAmount), buffer);
        // second block is cached if the block is not local
        byte[] expected = ((mBlockSource) != (BlockInStreamSource.REMOTE)) ? new byte[0] : BufferUtils.getIncreasingByteArray(((int) (FileInStreamTest.BLOCK_LENGTH)), ((int) (FileInStreamTest.BLOCK_LENGTH)));
        // Seek to current position (does nothing)
        mTestStream.seek((seekAmount + readAmount));
        // Seek a short way past start of block 3
        mTestStream.seek(((long) ((FileInStreamTest.BLOCK_LENGTH) * 3.1)));
        Assert.assertEquals(BufferUtils.byteToInt(((byte) ((FileInStreamTest.BLOCK_LENGTH) * 3.1))), mTestStream.read());
        mTestStream.seek(FileInStreamTest.FILE_LENGTH);
    }

    /**
     * Tests seeking back to the beginning of a block after the block's remaining is 0.
     */
    @Test
    public void seekToBeginningAfterReadingWholeBlock() throws IOException {
        // Read the whole block.
        int blockSize = ((int) (FileInStreamTest.BLOCK_LENGTH));
        byte[] block = new byte[blockSize];
        mTestStream.read(block);
        Assert.assertArrayEquals(BufferUtils.getIncreasingByteArray(0, blockSize), block);
        // Seek to the beginning of the current block, then read half of it.
        mTestStream.seek(0);
        int halfBlockSize = blockSize / 2;
        byte[] halfBlock = new byte[halfBlockSize];
        mTestStream.read(halfBlock);
        Assert.assertArrayEquals(BufferUtils.getIncreasingByteArray(0, halfBlockSize), halfBlock);
    }

    /**
     * Tests seeking to the beginning of the last block after reaching EOF.
     */
    @Test
    public void seekToLastBlockAfterReachingEOF() throws IOException {
        mTestStream.read(new byte[((int) (FileInStreamTest.FILE_LENGTH))]);
        mTestStream.seek(((FileInStreamTest.FILE_LENGTH) - (FileInStreamTest.BLOCK_LENGTH)));
        byte[] block = new byte[((int) (FileInStreamTest.BLOCK_LENGTH))];
        mTestStream.read(block);
        Assert.assertArrayEquals(BufferUtils.getIncreasingByteArray(((int) ((FileInStreamTest.FILE_LENGTH) - (FileInStreamTest.BLOCK_LENGTH))), ((int) (FileInStreamTest.BLOCK_LENGTH))), block);
    }

    /**
     * Tests seeking to EOF, then seeking to position 0 and read the whole file.
     */
    @Test
    public void seekToEOFBeforeReadingFirstBlock() throws IOException {
        mTestStream.seek(FileInStreamTest.FILE_LENGTH);
        mTestStream.seek(0);
        byte[] block = new byte[((int) (FileInStreamTest.BLOCK_LENGTH))];
        mTestStream.read(block);
        Assert.assertArrayEquals(BufferUtils.getIncreasingByteArray(0, ((int) (FileInStreamTest.BLOCK_LENGTH))), block);
    }

    /**
     * Tests seeking with incomplete block caching enabled. It seeks backward for more than a block.
     */
    @Test
    public void longSeekBackwardCachingPartiallyReadBlocks() throws IOException {
        OpenFilePOptions options = OpenFilePOptions.newBuilder().setReadType(CACHE_PROMOTE).build();
        mTestStream = new FileInStream(mStatus, new InStreamOptions(mStatus, options, FileInStreamTest.sConf), mContext);
        int seekAmount = ((int) (((FileInStreamTest.BLOCK_LENGTH) / 4) + (FileInStreamTest.BLOCK_LENGTH)));
        int readAmount = ((int) (((FileInStreamTest.BLOCK_LENGTH) * 3) - ((FileInStreamTest.BLOCK_LENGTH) / 2)));
        byte[] buffer = new byte[readAmount];
        mTestStream.read(buffer);
        // Seek backward.
        mTestStream.seek((readAmount - seekAmount));
        // Block 2 is cached though it is not fully read.
        validatePartialCaching(2, (((int) (FileInStreamTest.BLOCK_LENGTH)) / 2));
    }

    /**
     * Tests reading and seeking with no local worker. Nothing should be cached.
     */
    @Test
    public void testSeekWithNoLocalWorker() throws IOException {
        // Overrides the get local worker call
        PowerMockito.when(mContext.getLocalWorker()).thenReturn(null);
        OpenFilePOptions options = OpenFilePOptions.newBuilder().setReadType(CACHE_PROMOTE).build();
        mTestStream = new FileInStream(mStatus, new InStreamOptions(mStatus, options, FileInStreamTest.sConf), mContext);
        int readAmount = ((int) ((FileInStreamTest.BLOCK_LENGTH) / 2));
        byte[] buffer = new byte[readAmount];
        // read and seek several times
        mTestStream.read(buffer);
        Assert.assertEquals(readAmount, mInStreams.get(0).getBytesRead());
        mTestStream.seek(((FileInStreamTest.BLOCK_LENGTH) + ((FileInStreamTest.BLOCK_LENGTH) / 2)));
        mTestStream.seek(0);
        // only reads the read amount, regardless of block source
        Assert.assertEquals(readAmount, mInStreams.get(0).getBytesRead());
        Assert.assertEquals(0, mInStreams.get(1).getBytesRead());
    }

    @Test
    public void seekAndClose() throws IOException {
        OpenFilePOptions options = OpenFilePOptions.newBuilder().setReadType(CACHE_PROMOTE).build();
        mTestStream = new FileInStream(mStatus, new InStreamOptions(mStatus, options, FileInStreamTest.sConf), mContext);
        int seekAmount = ((int) ((FileInStreamTest.BLOCK_LENGTH) / 2));
        mTestStream.seek(seekAmount);
        mTestStream.close();
        // Block 0 is cached though it is not fully read.
        validatePartialCaching(0, 0);
    }

    /**
     * Tests seeking with incomplete block caching enabled. It seeks backward within 1 block.
     */
    @Test
    public void shortSeekBackwardCachingPartiallyReadBlocks() throws IOException {
        OpenFilePOptions options = OpenFilePOptions.newBuilder().setReadType(CACHE_PROMOTE).build();
        mTestStream = new FileInStream(mStatus, new InStreamOptions(mStatus, options, FileInStreamTest.sConf), mContext);
        int seekAmount = ((int) ((FileInStreamTest.BLOCK_LENGTH) / 4));
        int readAmount = ((int) (((FileInStreamTest.BLOCK_LENGTH) * 2) - ((FileInStreamTest.BLOCK_LENGTH) / 2)));
        byte[] buffer = new byte[readAmount];
        mTestStream.read(buffer);
        // Seek backward.
        mTestStream.seek((readAmount - seekAmount));
        // Block 1 is cached though it is not fully read.
        validatePartialCaching(1, (((int) (FileInStreamTest.BLOCK_LENGTH)) / 2));
        // Seek many times. It will cache block 1 only once.
        for (int i = 0; i <= seekAmount; i++) {
            mTestStream.seek(((readAmount - seekAmount) - i));
        }
        validatePartialCaching(1, (((int) (FileInStreamTest.BLOCK_LENGTH)) / 2));
    }

    /**
     * Tests seeking with incomplete block caching enabled. It seeks forward for more than a block.
     */
    @Test
    public void longSeekForwardCachingPartiallyReadBlocks() throws IOException {
        OpenFilePOptions options = OpenFilePOptions.newBuilder().setReadType(CACHE_PROMOTE).build();
        mTestStream = new FileInStream(mStatus, new InStreamOptions(mStatus, options, FileInStreamTest.sConf), mContext);
        int seekAmount = ((int) (((FileInStreamTest.BLOCK_LENGTH) / 4) + (FileInStreamTest.BLOCK_LENGTH)));
        int readAmount = ((int) ((FileInStreamTest.BLOCK_LENGTH) / 2));
        byte[] buffer = new byte[readAmount];
        mTestStream.read(buffer);
        // Seek backward.
        mTestStream.seek((readAmount + seekAmount));
        // Block 0 is cached though it is not fully read.
        validatePartialCaching(0, readAmount);
        // Block 1 is being cached though its prefix it not read.
        validatePartialCaching(1, 0);
        mTestStream.close();
        validatePartialCaching(1, 0);
    }

    /**
     * Tests seeking with incomplete block caching enabled. It seeks forward within a block.
     */
    @Test
    public void shortSeekForwardCachingPartiallyReadBlocks() throws IOException {
        OpenFilePOptions options = OpenFilePOptions.newBuilder().setReadType(CACHE_PROMOTE).build();
        mTestStream = new FileInStream(mStatus, new InStreamOptions(mStatus, options, FileInStreamTest.sConf), mContext);
        int seekAmount = ((int) ((FileInStreamTest.BLOCK_LENGTH) / 4));
        int readAmount = ((int) (((FileInStreamTest.BLOCK_LENGTH) * 2) - ((FileInStreamTest.BLOCK_LENGTH) / 2)));
        byte[] buffer = new byte[readAmount];
        mTestStream.read(buffer);
        // Seek backward.
        mTestStream.seek((readAmount + seekAmount));
        // Block 1 (till seek pos) is being cached.
        validatePartialCaching(1, (((int) (FileInStreamTest.BLOCK_LENGTH)) / 2));
        // Seek forward many times. The prefix is always cached.
        for (int i = 0; i < seekAmount; i++) {
            mTestStream.seek(((readAmount + seekAmount) + i));
            validatePartialCaching(1, (((int) (FileInStreamTest.BLOCK_LENGTH)) / 2));
        }
    }

    /**
     * Tests skipping backwards when the seek buffer size is smaller than block size.
     */
    @Test
    public void seekBackwardSmallSeekBuffer() throws IOException {
        OpenFilePOptions options = OpenFilePOptions.newBuilder().setReadType(CACHE_PROMOTE).build();
        mTestStream = new FileInStream(mStatus, new InStreamOptions(mStatus, options, FileInStreamTest.sConf), mContext);
        int readAmount = ((int) ((FileInStreamTest.BLOCK_LENGTH) / 2));
        byte[] buffer = new byte[readAmount];
        mTestStream.read(buffer);
        mTestStream.seek((readAmount - 1));
        validatePartialCaching(0, readAmount);
    }

    /**
     * Tests seeking with incomplete block caching enabled. It seeks forward for more than a block
     * and then seek to the file beginning.
     */
    @Test
    public void seekBackwardToFileBeginning() throws IOException {
        OpenFilePOptions options = OpenFilePOptions.newBuilder().setReadType(CACHE_PROMOTE).build();
        mTestStream = new FileInStream(mStatus, new InStreamOptions(mStatus, options, FileInStreamTest.sConf), mContext);
        int seekAmount = ((int) (((FileInStreamTest.BLOCK_LENGTH) / 4) + (FileInStreamTest.BLOCK_LENGTH)));
        // Seek forward.
        mTestStream.seek(seekAmount);
        // Block 1 is partially cached though it is not fully read.
        validatePartialCaching(1, 0);
        // Seek backward.
        mTestStream.seek(0);
        // Block 1 is fully cached though it is not fully read.
        validatePartialCaching(1, 0);
        mTestStream.close();
        // block 0 is cached
        validatePartialCaching(0, 0);
    }

    /**
     * Tests skip, particularly that skipping the start of a block will cause us not to cache it, and
     * cancels the existing cache stream.
     */
    @Test
    public void testSkip() throws IOException {
        int skipAmount = ((int) ((FileInStreamTest.BLOCK_LENGTH) / 2));
        int readAmount = ((int) ((FileInStreamTest.BLOCK_LENGTH) * 2));
        byte[] buffer = new byte[readAmount];
        // Skip halfway into block 1
        mTestStream.skip(skipAmount);
        // Read two blocks from 0.5 to 2.5
        mTestStream.read(buffer);
        Assert.assertArrayEquals(BufferUtils.getIncreasingByteArray(skipAmount, readAmount), buffer);
        Assert.assertEquals(0, mTestStream.skip(0));
        // Skip the next half block, bringing us to block 3
        Assert.assertEquals(((FileInStreamTest.BLOCK_LENGTH) / 2), mTestStream.skip(((FileInStreamTest.BLOCK_LENGTH) / 2)));
        Assert.assertEquals(BufferUtils.byteToInt(((byte) ((FileInStreamTest.BLOCK_LENGTH) * 3))), mTestStream.read());
    }

    /**
     * Tests that {@link IOException}s thrown by the {@link AlluxioBlockStore} are properly
     * propagated.
     */
    @Test
    public void failGetInStream() throws IOException {
        Mockito.when(mBlockStore.getInStream(ArgumentMatchers.anyLong(), ArgumentMatchers.any(InStreamOptions.class), ArgumentMatchers.any())).thenThrow(new UnavailableException("test exception"));
        try {
            mTestStream.read();
            Assert.fail("block store should throw exception");
        } catch (IOException e) {
            Assert.assertEquals("test exception", e.getMessage());
        }
    }

    /**
     * Tests that reading out of bounds properly returns -1.
     */
    @Test
    public void readOutOfBounds() throws IOException {
        mTestStream.read(new byte[((int) (FileInStreamTest.FILE_LENGTH))]);
        Assert.assertEquals((-1), mTestStream.read());
        Assert.assertEquals((-1), mTestStream.read(new byte[10]));
    }

    /**
     * Tests that specifying an invalid offset/length for a buffer read throws the right exception.
     */
    @Test
    public void readBadBuffer() throws IOException {
        try {
            mTestStream.read(new byte[10], 5, 6);
            Assert.fail("the buffer read of invalid offset/length should fail");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(String.format(ERR_BUFFER_STATE.toString(), 10, 5, 6), e.getMessage());
        }
    }

    /**
     * Tests that seeking to a negative position will throw the right exception.
     */
    @Test
    public void seekNegative() throws IOException {
        try {
            mTestStream.seek((-1));
            Assert.fail("seeking negative position should fail");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(String.format(ERR_SEEK_NEGATIVE.toString(), (-1)), e.getMessage());
        }
    }

    /**
     * Tests that seeking past the end of the stream will throw the right exception.
     */
    @Test
    public void seekPastEnd() throws IOException {
        try {
            mTestStream.seek(((FileInStreamTest.FILE_LENGTH) + 1));
            Assert.fail("seeking past the end of the stream should fail");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(String.format(ERR_SEEK_PAST_END_OF_FILE.toString(), ((FileInStreamTest.FILE_LENGTH) + 1)), e.getMessage());
        }
    }

    /**
     * Tests that skipping a negative amount correctly reports that 0 bytes were skipped.
     */
    @Test
    public void skipNegative() throws IOException {
        Assert.assertEquals(0, mTestStream.skip((-10)));
    }

    @Test
    public void positionedRead() throws IOException {
        byte[] b = new byte[((int) (FileInStreamTest.BLOCK_LENGTH))];
        mTestStream.positionedRead(FileInStreamTest.BLOCK_LENGTH, b, 0, b.length);
        Assert.assertArrayEquals(BufferUtils.getIncreasingByteArray(((int) (FileInStreamTest.BLOCK_LENGTH)), ((int) (FileInStreamTest.BLOCK_LENGTH))), b);
    }

    @Test
    public void multiBlockPositionedRead() throws IOException {
        byte[] b = new byte[((int) (FileInStreamTest.BLOCK_LENGTH)) * 2];
        mTestStream.positionedRead(((FileInStreamTest.BLOCK_LENGTH) / 2), b, 0, b.length);
        Assert.assertArrayEquals(BufferUtils.getIncreasingByteArray((((int) (FileInStreamTest.BLOCK_LENGTH)) / 2), (((int) (FileInStreamTest.BLOCK_LENGTH)) * 2)), b);
    }

    @Test
    public void readOneRetry() throws Exception {
        long offset = 37;
        // Setups a broken stream for the first block to throw an exception.
        TestBlockInStream workingStream = mInStreams.get(0);
        TestBlockInStream brokenStream = Mockito.mock(TestBlockInStream.class);
        Mockito.when(mBlockStore.getInStream(ArgumentMatchers.eq(0L), ArgumentMatchers.any(InStreamOptions.class), ArgumentMatchers.any())).thenReturn(brokenStream).thenReturn(workingStream);
        Mockito.when(brokenStream.read()).thenThrow(new UnavailableException("test exception"));
        Mockito.when(getPos()).thenReturn(offset);
        mTestStream.seek(offset);
        int b = mTestStream.read();
        Mockito.doReturn(0).when(brokenStream).read();
        Mockito.verify(brokenStream, Mockito.times(1)).read();
        Assert.assertEquals(offset, b);
    }

    @Test
    public void readBufferRetry() throws Exception {
        TestBlockInStream workingStream = mInStreams.get(0);
        TestBlockInStream brokenStream = Mockito.mock(TestBlockInStream.class);
        Mockito.when(mBlockStore.getInStream(ArgumentMatchers.eq(0L), ArgumentMatchers.any(InStreamOptions.class), ArgumentMatchers.any())).thenReturn(brokenStream).thenReturn(workingStream);
        Mockito.when(brokenStream.read(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenThrow(new UnavailableException("test exception"));
        Mockito.when(getPos()).thenReturn(((FileInStreamTest.BLOCK_LENGTH) / 2));
        mTestStream.seek(((FileInStreamTest.BLOCK_LENGTH) / 2));
        byte[] b = new byte[((int) (FileInStreamTest.BLOCK_LENGTH)) * 2];
        mTestStream.read(b, 0, b.length);
        Mockito.doReturn(0).when(brokenStream).read(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.verify(brokenStream, Mockito.times(1)).read(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Assert.assertArrayEquals(BufferUtils.getIncreasingByteArray((((int) (FileInStreamTest.BLOCK_LENGTH)) / 2), (((int) (FileInStreamTest.BLOCK_LENGTH)) * 2)), b);
    }

    @Test
    public void positionedReadRetry() throws Exception {
        TestBlockInStream workingStream = mInStreams.get(0);
        TestBlockInStream brokenStream = Mockito.mock(TestBlockInStream.class);
        Mockito.when(mBlockStore.getInStream(ArgumentMatchers.eq(0L), ArgumentMatchers.any(InStreamOptions.class), ArgumentMatchers.any())).thenReturn(brokenStream).thenReturn(workingStream);
        Mockito.when(brokenStream.positionedRead(ArgumentMatchers.anyLong(), ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenThrow(new UnavailableException("test exception"));
        byte[] b = new byte[((int) (FileInStreamTest.BLOCK_LENGTH)) * 2];
        mTestStream.positionedRead(((FileInStreamTest.BLOCK_LENGTH) / 2), b, 0, b.length);
        Mockito.doReturn(0).when(brokenStream).positionedRead(ArgumentMatchers.anyLong(), ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.verify(brokenStream, Mockito.times(1)).positionedRead(ArgumentMatchers.anyLong(), ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Assert.assertArrayEquals(BufferUtils.getIncreasingByteArray((((int) (FileInStreamTest.BLOCK_LENGTH)) / 2), (((int) (FileInStreamTest.BLOCK_LENGTH)) * 2)), b);
    }

    /**
     * Tests that when the underlying blocks are inconsistent with the metadata in terms of block
     * length, an exception is thrown rather than client hanging indefinitely. This case may happen if
     * the file in Alluxio and UFS is out of sync.
     */
    @Test
    public void blockInStreamOutOfSync() throws Exception {
        Mockito.when(mBlockStore.getInStream(ArgumentMatchers.anyLong(), ArgumentMatchers.any(InStreamOptions.class), ArgumentMatchers.any())).thenAnswer(new Answer<BlockInStream>() {
            @Override
            public BlockInStream answer(InvocationOnMock invocation) throws Throwable {
                return new TestBlockInStream(new byte[1], 0, FileInStreamTest.BLOCK_LENGTH, false, mBlockSource);
            }
        });
        byte[] buffer = new byte[((int) (FileInStreamTest.BLOCK_LENGTH))];
        try {
            mTestStream.read(buffer, 0, ((int) (FileInStreamTest.BLOCK_LENGTH)));
            Assert.fail("BlockInStream is inconsistent, an Exception is expected");
        } catch (IllegalStateException e) {
            // expect an exception to throw
        }
    }
}

