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
package alluxio.client.block.stream;


import alluxio.ClientContext;
import alluxio.ConfigurationRule;
import alluxio.ConfigurationTestUtils;
import alluxio.client.file.FileSystemContext;
import alluxio.conf.InstancedConfiguration;
import alluxio.conf.PropertyKey;
import alluxio.exception.status.ResourceExhaustedException;
import alluxio.grpc.WriteRequest;
import alluxio.util.ThreadFactoryUtils;
import alluxio.wire.WorkerNetAddress;
import io.grpc.stub.ClientCallStreamObserver;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ FileSystemContext.class, WorkerNetAddress.class })
public class UfsFallbackLocalFileDataWriterTest {
    private static final Logger LOG = LoggerFactory.getLogger(GrpcDataWriterTest.class);

    /**
     * A data writer implementation which will throw a ResourceExhaustedException on writes when the
     * given ByteBuffer is full.
     */
    public static class FixedCapacityTestDataWriter extends TestDataWriter {
        private final long mCapacity;

        private final ByteBuffer mBuffer;

        private boolean mIsLocalWorkerFull = false;

        private boolean mClosed = false;

        private boolean mCanceled = false;

        public FixedCapacityTestDataWriter(ByteBuffer buffer) {
            super(buffer);
            mCapacity = buffer.capacity();
            mBuffer = buffer;
        }

        @Override
        public void writeChunk(ByteBuf chunk) throws IOException {
            if (((pos()) + (chunk.readableBytes())) > (mCapacity)) {
                mIsLocalWorkerFull = true;
            }
            if (mIsLocalWorkerFull) {
                throw new ResourceExhaustedException("no more space!");
            }
            synchronized(mBuffer) {
                super.writeChunk(chunk);
            }
        }

        @Override
        public void close() {
            super.close();
            if (mClosed) {
                return;
            }
            mClosed = true;
        }

        @Override
        public void cancel() {
            super.cancel();
            if (mCanceled) {
                return;
            }
            mCanceled = true;
            mClosed = true;
        }

        public boolean isClosed() {
            return mClosed;
        }

        public boolean isCanceled() {
            return mCanceled;
        }
    }

    private static final int CHUNK_SIZE = 1024;

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4, ThreadFactoryUtils.build("test-executor-%d", true));

    private static final Random RANDOM = new Random();

    private static final long BLOCK_ID = 1L;

    private static final long MOUNT_ID = 9L;

    private ByteBuffer mBuffer;

    private UfsFallbackLocalFileDataWriterTest.FixedCapacityTestDataWriter mLocalWriter;

    private ClientContext mClientContext;

    private FileSystemContext mContext;

    private WorkerNetAddress mAddress;

    private BlockWorkerClient mClient;

    private ClientCallStreamObserver<WriteRequest> mRequestObserver;

    private InstancedConfiguration mConf = ConfigurationTestUtils.defaults();

    @Rule
    public ConfigurationRule mConfigurationRule = new ConfigurationRule(PropertyKey.USER_NETWORK_WRITER_CHUNK_SIZE_BYTES, String.valueOf(UfsFallbackLocalFileDataWriterTest.CHUNK_SIZE), mConf);

    @Test
    public void emptyBlock() throws Exception {
        try (DataWriter writer = create(1, 1)) {
            writer.flush();
            Assert.assertEquals(0, writer.pos());
        }
        Assert.assertEquals(0, mBuffer.position());
    }

    @Test(timeout = 1000 * 60)
    public void noFallback() throws Exception {
        Future<UfsFallbackLocalFileDataWriterTest.WriteSummary> expected;
        Future<UfsFallbackLocalFileDataWriterTest.WriteSummary> actualLocal;
        long blockSize = ((UfsFallbackLocalFileDataWriterTest.CHUNK_SIZE) * 1024) + ((UfsFallbackLocalFileDataWriterTest.CHUNK_SIZE) / 3);
        try (DataWriter writer = create(blockSize, blockSize)) {
            expected = writeData(writer, blockSize);
            actualLocal = getLocalWrite(mBuffer);
            expected.get();
        }
        Assert.assertEquals(expected.get().getBytes(), actualLocal.get().getBytes());
        Assert.assertEquals(expected.get().getChecksum(), actualLocal.get().getChecksum());
    }

    @Test(timeout = 1000 * 60)
    public void fallbackOnLastChunk() throws Exception {
        Future<UfsFallbackLocalFileDataWriterTest.WriteSummary> expected;
        Future<UfsFallbackLocalFileDataWriterTest.WriteSummary> actualLocal;
        Future<UfsFallbackLocalFileDataWriterTest.WriteSummary> actualUfs;
        long blockSize = ((UfsFallbackLocalFileDataWriterTest.CHUNK_SIZE) * 1024) + ((UfsFallbackLocalFileDataWriterTest.CHUNK_SIZE) / 3);
        try (DataWriter writer = create(blockSize, ((UfsFallbackLocalFileDataWriterTest.CHUNK_SIZE) * 1024))) {
            expected = writeData(writer, blockSize);
            expected.get();
            actualLocal = getLocalWrite(mBuffer);
            actualUfs = getUfsWrite(mClient);
        }
        Assert.assertEquals(blockSize, expected.get().getBytes());
        Assert.assertEquals(((UfsFallbackLocalFileDataWriterTest.CHUNK_SIZE) * 1024), actualLocal.get().getBytes());
        Assert.assertEquals((blockSize - ((UfsFallbackLocalFileDataWriterTest.CHUNK_SIZE) * 1024)), actualUfs.get().getBytes());
        Assert.assertEquals(expected.get().getChecksum(), ((actualLocal.get().getChecksum()) + (actualUfs.get().getChecksum())));
    }

    class WriteSummary {
        private final long mBytes;

        private final long mChecksum;

        public WriteSummary(long bytes, long checksum) {
            mBytes = bytes;
            mChecksum = checksum;
        }

        public long getBytes() {
            return mBytes;
        }

        public long getChecksum() {
            return mChecksum;
        }
    }
}

