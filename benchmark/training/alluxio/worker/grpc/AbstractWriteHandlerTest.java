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
package alluxio.worker.grpc;


import Status.CANCELLED;
import Status.Code.INVALID_ARGUMENT;
import alluxio.grpc.WriteResponse;
import alluxio.network.protocol.databuffer.DataBuffer;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.Random;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link AbstractWriteHandler}.
 */
public abstract class AbstractWriteHandlerTest {
    private static final Random RANDOM = new Random();

    protected static final int CHUNK_SIZE = 1024;

    protected static final long TEST_BLOCK_ID = 1L;

    protected static final long TEST_MOUNT_ID = 10L;

    protected AbstractWriteHandler mWriteHandler;

    protected StreamObserver<WriteResponse> mResponseObserver;

    @Rule
    public ExpectedException mExpectedException = ExpectedException.none();

    @Rule
    public TemporaryFolder mTestFolder = new TemporaryFolder();

    @Test
    public void writeEmptyFile() throws Exception {
        mWriteHandler.write(newWriteRequestCommand(0));
        mWriteHandler.onCompleted();
        checkComplete(mResponseObserver);
    }

    @Test
    public void writeNonEmptyFile() throws Exception {
        long len = 0;
        long checksum = 0;
        mWriteHandler.write(newWriteRequestCommand(0));
        for (int i = 0; i < 128; i++) {
            DataBuffer dataBuffer = newDataBuffer(AbstractWriteHandlerTest.CHUNK_SIZE);
            checksum += AbstractWriteHandlerTest.getChecksum(dataBuffer);
            mWriteHandler.write(newWriteRequest(dataBuffer));
            len += AbstractWriteHandlerTest.CHUNK_SIZE;
        }
        // EOF.
        mWriteHandler.onCompleted();
        checkComplete(mResponseObserver);
        checkWriteData(checksum, len);
    }

    @Test
    public void cancel() throws Exception {
        long len = 0;
        long checksum = 0;
        mWriteHandler.write(newWriteRequestCommand(0));
        for (int i = 0; i < 1; i++) {
            DataBuffer dataBuffer = newDataBuffer(AbstractWriteHandlerTest.CHUNK_SIZE);
            checksum += AbstractWriteHandlerTest.getChecksum(dataBuffer);
            mWriteHandler.write(newWriteRequest(dataBuffer));
            len += AbstractWriteHandlerTest.CHUNK_SIZE;
        }
        // Cancel.
        mWriteHandler.onCancel();
        checkComplete(mResponseObserver);
        // Our current implementation does not really abort the file when the write is cancelled.
        // The client issues another request to block worker to abort it.
        checkWriteData(checksum, len);
    }

    @Test
    public void cancelIgnoreError() throws Exception {
        long len = 0;
        long checksum = 0;
        mWriteHandler.write(newWriteRequestCommand(0));
        for (int i = 0; i < 1; i++) {
            DataBuffer dataBuffer = newDataBuffer(AbstractWriteHandlerTest.CHUNK_SIZE);
            checksum += AbstractWriteHandlerTest.getChecksum(dataBuffer);
            mWriteHandler.write(newWriteRequest(dataBuffer));
            len += AbstractWriteHandlerTest.CHUNK_SIZE;
        }
        // Cancel.
        mWriteHandler.onCancel();
        mWriteHandler.onError(CANCELLED.asRuntimeException());
        checkComplete(mResponseObserver);
        checkWriteData(checksum, len);
        Mockito.verify(mResponseObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void writeInvalidOffsetFirstRequest() throws Exception {
        // The write request contains an invalid offset
        mWriteHandler.write(newWriteRequestCommand(1));
        checkErrorCode(mResponseObserver, INVALID_ARGUMENT);
    }

    @Test
    public void writeInvalidOffsetLaterRequest() throws Exception {
        mWriteHandler.write(newWriteRequestCommand(0));
        // The write request contains an invalid offset
        mWriteHandler.write(newWriteRequestCommand(1));
        checkErrorCode(mResponseObserver, INVALID_ARGUMENT);
    }

    @Test
    public void ErrorReceived() throws Exception {
        mWriteHandler.onError(new IOException("test exception"));
    }

    @Test
    public void ErrorReceivedAfterRequest() throws Exception {
        mWriteHandler.write(newWriteRequestCommand(0));
        mWriteHandler.onCompleted();
        mWriteHandler.onError(new IOException("test exception"));
    }
}

