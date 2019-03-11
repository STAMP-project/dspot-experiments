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


import PropertyKey.USER_NETWORK_READER_CHUNK_SIZE_BYTES;
import Status.Code.INVALID_ARGUMENT;
import alluxio.conf.ServerConfiguration;
import alluxio.grpc.ReadResponse;
import io.grpc.stub.ServerCallStreamObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


public abstract class ReadHandlerTest {
    protected static final long CHUNK_SIZE = ServerConfiguration.getBytes(USER_NETWORK_READER_CHUNK_SIZE_BYTES);

    private final Random mRandom = new Random();

    protected String mFile;

    protected AbstractReadHandler mReadHandlerNoException;

    protected AbstractReadHandler mReadHandler;

    protected ServerCallStreamObserver<ReadResponse> mResponseObserver;

    protected List<ReadResponse> mResponses = new ArrayList<>();

    protected boolean mResponseCompleted;

    protected Throwable mError;

    @Rule
    public TemporaryFolder mTestFolder = new TemporaryFolder();

    @Rule
    public ExpectedException mExpectedException = ExpectedException.none();

    /**
     * Reads all bytes of a file.
     */
    @Test
    public void readFullFile() throws Exception {
        long checksumExpected = populateInputFile(((ReadHandlerTest.CHUNK_SIZE) * 10), 0, (((ReadHandlerTest.CHUNK_SIZE) * 10) - 1));
        mReadHandler.onNext(buildReadRequest(0, ((ReadHandlerTest.CHUNK_SIZE) * 10)));
        checkAllReadResponses(mResponses, checksumExpected);
    }

    /**
     * Reads a sub-region of a file.
     */
    @Test
    public void readPartialFile() throws Exception {
        long start = 3;
        long end = ((ReadHandlerTest.CHUNK_SIZE) * 10) - 99;
        long checksumExpected = populateInputFile(((ReadHandlerTest.CHUNK_SIZE) * 10), start, end);
        mReadHandler.onNext(buildReadRequest(start, ((end + 1) - start)));
        checkAllReadResponses(mResponses, checksumExpected);
    }

    /**
     * Fails if the read request tries to read an empty file.
     */
    @Test
    public void readEmptyFile() throws Exception {
        populateInputFile(0, 0, 0);
        mReadHandlerNoException.onNext(buildReadRequest(0, 0));
        checkErrorCode(mResponseObserver, INVALID_ARGUMENT);
    }

    /**
     * Cancels the read request immediately after the read request is sent.
     */
    @Test
    public void cancelRequest() throws Exception {
        long fileSize = ((ReadHandlerTest.CHUNK_SIZE) * 100) + 1;
        populateInputFile(fileSize, 0, (fileSize - 1));
        mReadHandler.onNext(buildReadRequest(0, fileSize));
        mReadHandler.onCompleted();
        checkCancel(mResponseObserver);
    }

    @Test
    public void ErrorReceived() throws Exception {
        mReadHandler.onError(new IOException("test error"));
    }

    @Test
    public void ErrorReceivedAfterRequest() throws Exception {
        populateInputFile(((ReadHandlerTest.CHUNK_SIZE) * 10), 0, (((ReadHandlerTest.CHUNK_SIZE) * 10) - 1));
        mReadHandler.onNext(buildReadRequest(0, ((ReadHandlerTest.CHUNK_SIZE) * 10)));
        mReadHandler.onError(new IOException("test error"));
    }
}

