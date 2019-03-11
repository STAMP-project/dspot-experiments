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


import GrpcDataReader.Factory;
import alluxio.client.file.FileSystemContext;
import alluxio.grpc.ReadRequest;
import alluxio.wire.WorkerNetAddress;
import io.grpc.stub.ClientCallStreamObserver;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ FileSystemContext.class, WorkerNetAddress.class })
public final class GrpcDataReaderTest {
    private static final int CHUNK_SIZE = 1024;

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4);

    private static final Random RANDOM = new Random();

    private static final long BLOCK_ID = 1L;

    private FileSystemContext mContext;

    private WorkerNetAddress mAddress;

    private BlockWorkerClient mClient;

    private Factory mFactory;

    private ClientCallStreamObserver<ReadRequest> mRequestObserver;

    /**
     * Reads an empty file.
     */
    @Test
    public void readEmptyFile() throws Exception {
        try (DataReader reader = create(0, 10)) {
            setReadResponses(mClient, 0, 0, 0);
            Assert.assertEquals(null, reader.readChunk());
        }
        validateReadRequestSent(mClient, 0, 10, true, GrpcDataReaderTest.CHUNK_SIZE);
    }

    /**
     * Reads all contents in a file.
     */
    @Test(timeout = 1000 * 60)
    public void readFullFile() throws Exception {
        long length = ((GrpcDataReaderTest.CHUNK_SIZE) * 1024) + ((GrpcDataReaderTest.CHUNK_SIZE) / 3);
        try (DataReader reader = create(0, length)) {
            long checksum = setReadResponses(mClient, length, 0, (length - 1));
            long checksumActual = checkChunks(reader, 0, length);
            Assert.assertEquals(checksum, checksumActual);
        }
        validateReadRequestSent(mClient, 0, length, true, GrpcDataReaderTest.CHUNK_SIZE);
    }

    /**
     * Reads part of a file and checks the checksum of the part that is read.
     */
    @Test(timeout = 1000 * 60)
    public void readPartialFile() throws Exception {
        long length = ((GrpcDataReaderTest.CHUNK_SIZE) * 1024) + ((GrpcDataReaderTest.CHUNK_SIZE) / 3);
        long offset = 10;
        long checksumStart = 100;
        long bytesToRead = length / 3;
        try (DataReader reader = create(offset, length)) {
            long checksum = setReadResponses(mClient, length, checksumStart, (bytesToRead - 1));
            long checksumActual = checkChunks(reader, checksumStart, bytesToRead);
            Assert.assertEquals(checksum, checksumActual);
        }
        validateReadRequestSent(mClient, offset, length, true, GrpcDataReaderTest.CHUNK_SIZE);
    }

    /**
     * Reads a file with unknown length.
     */
    @Test(timeout = 1000 * 60)
    public void fileLengthUnknown() throws Exception {
        long lengthActual = ((GrpcDataReaderTest.CHUNK_SIZE) * 1024) + ((GrpcDataReaderTest.CHUNK_SIZE) / 3);
        long checksumStart = 100;
        long bytesToRead = lengthActual / 3;
        try (DataReader reader = create(0, Long.MAX_VALUE)) {
            long checksum = setReadResponses(mClient, lengthActual, checksumStart, (bytesToRead - 1));
            long checksumActual = checkChunks(reader, checksumStart, bytesToRead);
            Assert.assertEquals(checksum, checksumActual);
        }
        validateReadRequestSent(mClient, 0, Long.MAX_VALUE, true, GrpcDataReaderTest.CHUNK_SIZE);
    }
}

