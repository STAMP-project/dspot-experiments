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


import alluxio.ConfigurationRule;
import alluxio.ConfigurationTestUtils;
import alluxio.client.file.FileSystemContext;
import alluxio.conf.InstancedConfiguration;
import alluxio.conf.PropertyKey;
import alluxio.grpc.WriteRequest;
import alluxio.util.ThreadFactoryUtils;
import alluxio.wire.WorkerNetAddress;
import io.grpc.stub.ClientCallStreamObserver;
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
public final class GrpcDataWriterTest {
    private static final Logger LOG = LoggerFactory.getLogger(GrpcDataWriterTest.class);

    private static final int CHUNK_SIZE = 1024;

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4, ThreadFactoryUtils.build("test-executor-%d", true));

    private static final Random RANDOM = new Random();

    private static final long BLOCK_ID = 1L;

    private static final int TIER = 0;

    private FileSystemContext mContext;

    private WorkerNetAddress mAddress;

    private BlockWorkerClient mClient;

    private ClientCallStreamObserver<WriteRequest> mRequestObserver;

    private InstancedConfiguration mConf = ConfigurationTestUtils.defaults();

    @Rule
    public ConfigurationRule mConfigurationRule = new ConfigurationRule(PropertyKey.USER_NETWORK_WRITER_CHUNK_SIZE_BYTES, String.valueOf(GrpcDataWriterTest.CHUNK_SIZE), mConf);

    /**
     * Writes an empty file.
     */
    @Test(timeout = 1000 * 60)
    public void writeEmptyFile() throws Exception {
        long checksumActual;
        try (DataWriter writer = create(10)) {
            checksumActual = verifyWriteRequests(mClient, 0, 10);
        }
        Assert.assertEquals(0, checksumActual);
    }

    /**
     * Writes a file with file length matches what is given and verifies the checksum of the whole
     * file.
     */
    @Test(timeout = 1000 * 60)
    public void writeFullFile() throws Exception {
        long checksumActual;
        Future<Long> checksumExpected;
        long length = ((GrpcDataWriterTest.CHUNK_SIZE) * 1024) + ((GrpcDataWriterTest.CHUNK_SIZE) / 3);
        try (DataWriter writer = create(length)) {
            checksumExpected = writeFile(writer, length, 0, (length - 1));
            checksumExpected.get();
            checksumActual = verifyWriteRequests(mClient, 0, (length - 1));
        }
        Assert.assertEquals(checksumExpected.get().longValue(), checksumActual);
    }

    /**
     * Writes a file with file length matches what is given and verifies the checksum of the whole
     * file.
     */
    @Test(timeout = 1000 * 60)
    public void writeFileChecksumOfPartialFile() throws Exception {
        long checksumActual;
        Future<Long> checksumExpected;
        long length = ((GrpcDataWriterTest.CHUNK_SIZE) * 1024) + ((GrpcDataWriterTest.CHUNK_SIZE) / 3);
        try (DataWriter writer = create(length)) {
            checksumExpected = writeFile(writer, length, 10, (length / 3));
            checksumExpected.get();
            checksumActual = verifyWriteRequests(mClient, 10, (length / 3));
        }
        Assert.assertEquals(checksumExpected.get().longValue(), checksumActual);
    }

    /**
     * Writes a file with unknown length.
     */
    @Test(timeout = 1000 * 60)
    public void writeFileUnknownLength() throws Exception {
        long checksumActual;
        Future<Long> checksumExpected;
        long length = (GrpcDataWriterTest.CHUNK_SIZE) * 1024;
        try (DataWriter writer = create(Long.MAX_VALUE)) {
            checksumExpected = writeFile(writer, length, 10, (length / 3));
            checksumExpected.get();
            checksumActual = verifyWriteRequests(mClient, 10, (length / 3));
        }
        Assert.assertEquals(checksumExpected.get().longValue(), checksumActual);
    }

    /**
     * Writes lots of chunks.
     */
    @Test(timeout = 1000 * 60)
    public void writeFileManyChunks() throws Exception {
        long checksumActual;
        Future<Long> checksumExpected;
        long length = ((GrpcDataWriterTest.CHUNK_SIZE) * 30000) + ((GrpcDataWriterTest.CHUNK_SIZE) / 3);
        try (DataWriter writer = create(Long.MAX_VALUE)) {
            checksumExpected = writeFile(writer, length, 10, (length / 3));
            checksumExpected.get();
            checksumActual = verifyWriteRequests(mClient, 10, (length / 3));
        }
        Assert.assertEquals(checksumExpected.get().longValue(), checksumActual);
    }
}

