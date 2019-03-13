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
package alluxio.server.ft;


import PropertyKey.Name;
import PropertyKey.USER_BLOCK_SIZE_BYTES_DEFAULT;
import PropertyKey.USER_FILE_BUFFER_BYTES;
import PropertyKey.WORKER_MEMORY_SIZE;
import PropertyKey.WORKER_TIERED_STORE_RESERVER_ENABLED;
import WritePType.MUST_CACHE;
import alluxio.AlluxioURI;
import alluxio.Constants;
import alluxio.client.block.BlockWorkerInfo;
import alluxio.client.file.FileInStream;
import alluxio.client.file.FileSystem;
import alluxio.client.file.FileSystemTestUtils;
import alluxio.client.file.URIStatus;
import alluxio.client.file.policy.FileWriteLocationPolicy;
import alluxio.client.file.policy.RoundRobinPolicy;
import alluxio.conf.AlluxioConfiguration;
import alluxio.grpc.CreateFilePOptions;
import alluxio.grpc.OpenFilePOptions;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.testutils.LocalAlluxioClusterResource;
import alluxio.util.io.BufferUtils;
import alluxio.wire.WorkerNetAddress;
import java.util.stream.StreamSupport;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import static FindFirstFileWriteLocationPolicy.sWorkerAddress;


/**
 * Tests a cluster containing multiple workers.
 */
public final class MultiWorkerIntegrationTest extends BaseIntegrationTest {
    private static final int NUM_WORKERS = 4;

    private static final int WORKER_MEMORY_SIZE_BYTES = Constants.MB;

    private static final int BLOCK_SIZE_BYTES = (MultiWorkerIntegrationTest.WORKER_MEMORY_SIZE_BYTES) / 2;

    public static class FindFirstFileWriteLocationPolicy implements FileWriteLocationPolicy {
        // Set this prior to sending the create request to FSM.
        private static WorkerNetAddress sWorkerAddress;

        public FindFirstFileWriteLocationPolicy(AlluxioConfiguration alluxioConf) {
        }

        @Override
        public WorkerNetAddress getWorkerForNextBlock(Iterable<BlockWorkerInfo> workerInfoList, long blockSizeBytes) {
            return StreamSupport.stream(workerInfoList.spliterator(), false).filter(( x) -> x.getNetAddress().equals(FindFirstFileWriteLocationPolicy.sWorkerAddress)).findFirst().get().getNetAddress();
        }
    }

    @Rule
    public LocalAlluxioClusterResource mResource = new LocalAlluxioClusterResource.Builder().setProperty(WORKER_MEMORY_SIZE, MultiWorkerIntegrationTest.WORKER_MEMORY_SIZE_BYTES).setProperty(USER_BLOCK_SIZE_BYTES_DEFAULT, MultiWorkerIntegrationTest.BLOCK_SIZE_BYTES).setProperty(USER_FILE_BUFFER_BYTES, MultiWorkerIntegrationTest.BLOCK_SIZE_BYTES).setProperty(WORKER_TIERED_STORE_RESERVER_ENABLED, false).setNumWorkers(MultiWorkerIntegrationTest.NUM_WORKERS).build();

    @Test
    public void writeLargeFile() throws Exception {
        int fileSize = (MultiWorkerIntegrationTest.NUM_WORKERS) * (MultiWorkerIntegrationTest.WORKER_MEMORY_SIZE_BYTES);
        AlluxioURI file = new AlluxioURI("/test");
        FileSystem fs = mResource.get().getClient();
        FileSystemTestUtils.createByteFile(fs, file.getPath(), fileSize, CreateFilePOptions.newBuilder().setWriteType(MUST_CACHE).setFileWriteLocationPolicy(RoundRobinPolicy.class.getCanonicalName()).build());
        URIStatus status = fs.getStatus(file);
        Assert.assertEquals(100, status.getInAlluxioPercentage());
        try (FileInStream inStream = fs.openFile(file)) {
            Assert.assertEquals(fileSize, IOUtils.toByteArray(inStream).length);
        }
    }

    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.USER_SHORT_CIRCUIT_ENABLED, "false", Name.USER_BLOCK_SIZE_BYTES_DEFAULT, "16MB", Name.USER_NETWORK_READER_CHUNK_SIZE_BYTES, "64KB", Name.WORKER_MEMORY_SIZE, "1GB" })
    public void readRecoverFromLostWorker() throws Exception {
        int offset = 17 * (Constants.MB);
        int length = 33 * (Constants.MB);
        int total = offset + length;
        // creates a test file on one worker
        AlluxioURI filePath = new AlluxioURI("/test");
        createFileOnWorker(total, filePath, mResource.get().getWorkerAddress());
        FileSystem fs = mResource.get().getClient();
        try (FileInStream in = fs.openFile(filePath, OpenFilePOptions.getDefaultInstance())) {
            byte[] buf = new byte[total];
            int size = in.read(buf, 0, offset);
            replicateFileBlocks(filePath);
            mResource.get().getWorkerProcess().stop();
            size += in.read(buf, offset, length);
            Assert.assertEquals(total, size);
            Assert.assertTrue(BufferUtils.equalIncreasingByteArray(offset, size, buf));
        }
    }

    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.USER_SHORT_CIRCUIT_ENABLED, "false", Name.USER_BLOCK_SIZE_BYTES_DEFAULT, "4MB", Name.USER_NETWORK_READER_CHUNK_SIZE_BYTES, "64KB", Name.WORKER_MEMORY_SIZE, "1GB" })
    public void readOneRecoverFromLostWorker() throws Exception {
        int offset = 1 * (Constants.MB);
        int length = 5 * (Constants.MB);
        int total = offset + length;
        // creates a test file on one worker
        AlluxioURI filePath = new AlluxioURI("/test");
        FileSystem fs = mResource.get().getClient();
        createFileOnWorker(total, filePath, mResource.get().getWorkerAddress());
        try (FileInStream in = fs.openFile(filePath, OpenFilePOptions.getDefaultInstance())) {
            byte[] buf = new byte[total];
            int size = in.read(buf, 0, offset);
            replicateFileBlocks(filePath);
            mResource.get().getWorkerProcess().stop();
            for (int i = 0; i < length; i++) {
                int result = in.read();
                Assert.assertEquals(result, ((i + size) & 255));
            }
        }
    }

    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.USER_SHORT_CIRCUIT_ENABLED, "false", Name.USER_BLOCK_SIZE_BYTES_DEFAULT, "4MB", Name.USER_NETWORK_READER_CHUNK_SIZE_BYTES, "64KB", Name.WORKER_MEMORY_SIZE, "1GB" })
    public void positionReadRecoverFromLostWorker() throws Exception {
        int offset = 1 * (Constants.MB);
        int length = 7 * (Constants.MB);
        int total = offset + length;
        // creates a test file on one worker
        AlluxioURI filePath = new AlluxioURI("/test");
        FileSystem fs = mResource.get().getClient();
        createFileOnWorker(total, filePath, mResource.get().getWorkerAddress());
        try (FileInStream in = fs.openFile(filePath, OpenFilePOptions.getDefaultInstance())) {
            byte[] buf = new byte[length];
            replicateFileBlocks(filePath);
            mResource.get().getWorkerProcess().stop();
            int size = in.positionedRead(offset, buf, 0, length);
            Assert.assertEquals(length, size);
            Assert.assertTrue(BufferUtils.equalIncreasingByteArray(offset, size, buf));
        }
    }
}

