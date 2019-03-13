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
package alluxio.worker.block;


import PooledByteBufAllocator.DEFAULT;
import PropertyKey.MASTER_MOUNT_TABLE_ROOT_UFS;
import PropertyKey.WORKER_TIERED_STORE_LEVEL0_DIRS_PATH;
import PropertyKey.WORKER_TIERED_STORE_LEVELS;
import Protocol.OpenUfsBlockOptions;
import alluxio.AlluxioTestDirectory;
import alluxio.ConfigurationRule;
import alluxio.conf.PropertyKey;
import alluxio.conf.ServerConfiguration;
import alluxio.exception.WorkerOutOfSpaceException;
import alluxio.underfs.UfsManager;
import alluxio.util.io.BufferUtils;
import alluxio.worker.block.meta.UnderFileSystemBlockMeta;
import io.netty.buffer.ByteBuf;
import java.nio.ByteBuffer;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public final class UnderFileSystemBlockReaderTest {
    private static final long TEST_BLOCK_SIZE = 1024;

    private static final long SESSION_ID = 1;

    private static final long BLOCK_ID = 2;

    private UnderFileSystemBlockReader mReader;

    private BlockStore mAlluxioBlockStore;

    private UnderFileSystemBlockMeta mUnderFileSystemBlockMeta;

    private UfsManager mUfsManager;

    private UfsInputStreamManager mUfsInstreamManager;

    private OpenUfsBlockOptions mOpenUfsBlockOptions;

    /**
     * Rule to create a new temporary folder during each test.
     */
    @Rule
    public TemporaryFolder mFolder = new TemporaryFolder();

    @Rule
    public ConfigurationRule mConfigurationRule = new ConfigurationRule(new HashMap<PropertyKey, String>() {
        {
            put(MASTER_MOUNT_TABLE_ROOT_UFS, AlluxioTestDirectory.createTemporaryDirectory("UnderFileSystemBlockReaderTest-RootUfs").getAbsolutePath());
            // ensure tiered storage uses different tmp dir for each test case
            put(WORKER_TIERED_STORE_LEVEL0_DIRS_PATH, AlluxioTestDirectory.createTemporaryDirectory("UnderFileSystemBlockReaderTest-WorkerDataFolder").getAbsolutePath());
            put(WORKER_TIERED_STORE_LEVELS, "1");
        }
    }, ServerConfiguration.global());

    @Test
    public void readFullBlock() throws Exception {
        mReader = UnderFileSystemBlockReader.create(mUnderFileSystemBlockMeta, 0, mAlluxioBlockStore, mUfsManager, mUfsInstreamManager);
        ByteBuffer buffer = mReader.read(0, UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE);
        Assert.assertTrue(BufferUtils.equalIncreasingByteBuffer(0, ((int) (UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE)), buffer));
        mReader.close();
        checkTempBlock(0, UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE);
    }

    @Test
    public void readPartialBlock() throws Exception {
        mReader = UnderFileSystemBlockReader.create(mUnderFileSystemBlockMeta, 0, mAlluxioBlockStore, mUfsManager, mUfsInstreamManager);
        ByteBuffer buffer = mReader.read(0, ((UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE) - 1));
        Assert.assertTrue(BufferUtils.equalIncreasingByteBuffer(0, (((int) (UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE)) - 1), buffer));
        mReader.close();
        // partial block should not be cached
        Assert.assertNull(mAlluxioBlockStore.getTempBlockMeta(UnderFileSystemBlockReaderTest.SESSION_ID, UnderFileSystemBlockReaderTest.BLOCK_ID));
    }

    @Test
    public void offset() throws Exception {
        mReader = UnderFileSystemBlockReader.create(mUnderFileSystemBlockMeta, 0, mAlluxioBlockStore, mUfsManager, mUfsInstreamManager);
        ByteBuffer buffer = mReader.read(2, ((UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE) - 2));
        Assert.assertTrue(BufferUtils.equalIncreasingByteBuffer(2, (((int) (UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE)) - 2), buffer));
        mReader.close();
        // partial block should not be cached
        Assert.assertNull(mAlluxioBlockStore.getTempBlockMeta(UnderFileSystemBlockReaderTest.SESSION_ID, UnderFileSystemBlockReaderTest.BLOCK_ID));
    }

    @Test
    public void readOverlap() throws Exception {
        mReader = UnderFileSystemBlockReader.create(mUnderFileSystemBlockMeta, 2, mAlluxioBlockStore, mUfsManager, mUfsInstreamManager);
        ByteBuffer buffer = mReader.read(2, ((UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE) - 2));
        Assert.assertTrue(BufferUtils.equalIncreasingByteBuffer(2, (((int) (UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE)) - 2), buffer));
        buffer = mReader.read(0, ((UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE) - 2));
        Assert.assertTrue(BufferUtils.equalIncreasingByteBuffer(0, (((int) (UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE)) - 2), buffer));
        buffer = mReader.read(3, UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE);
        Assert.assertTrue(BufferUtils.equalIncreasingByteBuffer(3, (((int) (UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE)) - 3), buffer));
        mReader.close();
        // block should be cached as two reads covers the full block
        checkTempBlock(0, UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE);
    }

    @Test
    public void readFullBlockNoCache() throws Exception {
        mUnderFileSystemBlockMeta = new UnderFileSystemBlockMeta(UnderFileSystemBlockReaderTest.SESSION_ID, UnderFileSystemBlockReaderTest.BLOCK_ID, mOpenUfsBlockOptions.toBuilder().setNoCache(true).build());
        mReader = UnderFileSystemBlockReader.create(mUnderFileSystemBlockMeta, 0, mAlluxioBlockStore, mUfsManager, mUfsInstreamManager);
        ByteBuffer buffer = mReader.read(0, UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE);
        // read should succeed even if error is thrown when caching
        Assert.assertTrue(BufferUtils.equalIncreasingByteBuffer(0, ((int) (UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE)), buffer));
        mReader.close();
        Assert.assertNull(mAlluxioBlockStore.getTempBlockMeta(UnderFileSystemBlockReaderTest.SESSION_ID, UnderFileSystemBlockReaderTest.BLOCK_ID));
    }

    @Test
    public void readFullBlockRequestSpaceError() throws Exception {
        BlockStore errorThrowingBlockStore = Mockito.spy(mAlluxioBlockStore);
        Mockito.doThrow(new WorkerOutOfSpaceException("Ignored")).when(errorThrowingBlockStore).requestSpace(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        mReader = UnderFileSystemBlockReader.create(mUnderFileSystemBlockMeta, 0, errorThrowingBlockStore, mUfsManager, mUfsInstreamManager);
        ByteBuffer buffer = mReader.read(0, UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE);
        Assert.assertTrue(BufferUtils.equalIncreasingByteBuffer(0, ((int) (UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE)), buffer));
        mReader.close();
        Assert.assertNull(mAlluxioBlockStore.getTempBlockMeta(UnderFileSystemBlockReaderTest.SESSION_ID, UnderFileSystemBlockReaderTest.BLOCK_ID));
    }

    @Test
    public void readFullBlockRequestCreateBlockError() throws Exception {
        BlockStore errorThrowingBlockStore = Mockito.spy(mAlluxioBlockStore);
        Mockito.doThrow(new WorkerOutOfSpaceException("Ignored")).when(errorThrowingBlockStore).createBlock(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(BlockStoreLocation.class), ArgumentMatchers.anyLong());
        mReader = UnderFileSystemBlockReader.create(mUnderFileSystemBlockMeta, 0, errorThrowingBlockStore, mUfsManager, mUfsInstreamManager);
        ByteBuffer buffer = mReader.read(0, UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE);
        Assert.assertTrue(BufferUtils.equalIncreasingByteBuffer(0, ((int) (UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE)), buffer));
        mReader.close();
        Assert.assertNull(mAlluxioBlockStore.getTempBlockMeta(UnderFileSystemBlockReaderTest.SESSION_ID, UnderFileSystemBlockReaderTest.BLOCK_ID));
    }

    @Test
    public void transferFullBlock() throws Exception {
        mReader = UnderFileSystemBlockReader.create(mUnderFileSystemBlockMeta, 0, mAlluxioBlockStore, mUfsManager, mUfsInstreamManager);
        ByteBuf buf = DEFAULT.buffer((((int) (UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE)) * 2), (((int) (UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE)) * 2));
        try {
            while (((buf.writableBytes()) > 0) && ((mReader.transferTo(buf)) != (-1))) {
            } 
            Assert.assertTrue(BufferUtils.equalIncreasingByteBuffer(0, ((int) (UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE)), buf.nioBuffer()));
            mReader.close();
        } finally {
            buf.release();
        }
        checkTempBlock(0, UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE);
    }

    @Test
    public void transferPartialBlock() throws Exception {
        mReader = UnderFileSystemBlockReader.create(mUnderFileSystemBlockMeta, 0, mAlluxioBlockStore, mUfsManager, mUfsInstreamManager);
        ByteBuf buf = DEFAULT.buffer((((int) (UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE)) / 2), (((int) (UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE)) / 2));
        try {
            while (((buf.writableBytes()) > 0) && ((mReader.transferTo(buf)) != (-1))) {
            } 
            Assert.assertTrue(BufferUtils.equalIncreasingByteBuffer(0, (((int) (UnderFileSystemBlockReaderTest.TEST_BLOCK_SIZE)) / 2), buf.nioBuffer()));
            mReader.close();
        } finally {
            buf.release();
        }
        // partial block should not be cached
        Assert.assertNull(mAlluxioBlockStore.getTempBlockMeta(UnderFileSystemBlockReaderTest.SESSION_ID, UnderFileSystemBlockReaderTest.BLOCK_ID));
    }
}

