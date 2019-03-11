/**
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.facebook.stetho.inspector.network;


import Console.MessageLevel;
import Console.MessageSource;
import com.facebook.stetho.inspector.console.CLog;
import com.facebook.stetho.inspector.helper.ChromePeerManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest(CLog.class)
public class ResponseHandlingInputStreamTest {
    private static final String TEST_REQUEST_ID = "1234";

    private static final byte[] TEST_RESPONSE_BODY;

    static {
        int responseBodyLength = (4096 * 2) + 2048;// span multiple buffers when tee-ing

        TEST_RESPONSE_BODY = new byte[responseBodyLength];
        for (int i = 0; i < responseBodyLength; i++) {
            ResponseHandlingInputStreamTest.TEST_RESPONSE_BODY[i] = ResponseHandlingInputStreamTest.positionToByte(i);
        }
    }

    private ByteArrayOutputStream mTestOutputStream;

    private ResponseHandlingInputStream mResponseHandlingInputStream;

    private NetworkPeerManager mNetworkPeerManager;

    private NetworkEventReporterImpl mNetworkEventReporter;

    @Test
    public void testReadOneByte() throws IOException {
        int result = mResponseHandlingInputStream.read();
        Assert.assertEquals(ResponseHandlingInputStreamTest.TEST_RESPONSE_BODY[0], ResponseHandlingInputStreamTest.positionToByte(result));
        ResponseHandlingInputStreamTest.assertBufferMatchesResponseBody(mTestOutputStream.toByteArray(), 1);
        PowerMockito.mockStatic(CLog.class);
        PowerMockito.doNothing().when(CLog.class);
        CLog.writeToConsole(Mockito.any(ChromePeerManager.class), Mockito.any(MessageLevel.class), Mockito.any(MessageSource.class), Mockito.anyString());
        mResponseHandlingInputStream.close();
        PowerMockito.verifyStatic();
    }

    @Test
    public void testReadPartial() throws IOException {
        int numBytesToRead = (ResponseHandlingInputStreamTest.TEST_RESPONSE_BODY.length) / 2;
        byte[] tempReadingBuffer = new byte[numBytesToRead];
        int result = mResponseHandlingInputStream.read(tempReadingBuffer, 0, numBytesToRead);
        Assert.assertEquals(numBytesToRead, result);
        ResponseHandlingInputStreamTest.assertBufferMatchesResponseBody(tempReadingBuffer, numBytesToRead);
        ResponseHandlingInputStreamTest.assertBufferMatchesResponseBody(mTestOutputStream.toByteArray(), numBytesToRead);
        PowerMockito.mockStatic(CLog.class);
        PowerMockito.doNothing().when(CLog.class);
        CLog.writeToConsole(Mockito.any(ChromePeerManager.class), Mockito.any(MessageLevel.class), Mockito.any(MessageSource.class), Mockito.anyString());
        mResponseHandlingInputStream.close();
        PowerMockito.verifyStatic();
    }

    @Test
    public void testReadFully() throws IOException {
        byte[] tempReadingBuffer = new byte[ResponseHandlingInputStreamTest.TEST_RESPONSE_BODY.length];
        int result = mResponseHandlingInputStream.read(tempReadingBuffer);
        Assert.assertEquals(ResponseHandlingInputStreamTest.TEST_RESPONSE_BODY.length, result);
        ResponseHandlingInputStreamTest.assertBufferMatchesResponseBody(tempReadingBuffer, ResponseHandlingInputStreamTest.TEST_RESPONSE_BODY.length);
        ResponseHandlingInputStreamTest.assertBufferMatchesResponseBody(mTestOutputStream.toByteArray(), ResponseHandlingInputStreamTest.TEST_RESPONSE_BODY.length);
        PowerMockito.mockStatic(CLog.class);
        PowerMockito.verifyZeroInteractions(CLog.class);
        mResponseHandlingInputStream.close();
        PowerMockito.verifyStatic();
    }

    @Test
    public void testSkipFew() throws IOException {
        long numBytesToSkip = (ResponseHandlingInputStreamTest.TEST_RESPONSE_BODY.length) / 2;
        long result = mResponseHandlingInputStream.skip(numBytesToSkip);
        Assert.assertEquals(numBytesToSkip, result);
        ResponseHandlingInputStreamTest.assertBufferMatchesResponseBody(mTestOutputStream.toByteArray(), ((int) (numBytesToSkip)));
        PowerMockito.mockStatic(CLog.class);
        PowerMockito.doNothing().when(CLog.class);
        CLog.writeToConsole(Mockito.any(ChromePeerManager.class), Mockito.any(MessageLevel.class), Mockito.any(MessageSource.class), Mockito.anyString());
        mResponseHandlingInputStream.close();
        PowerMockito.verifyStatic();
    }

    @Test
    public void testSkipMany() throws IOException {
        long numBytesToSkip = (ResponseHandlingInputStreamTest.TEST_RESPONSE_BODY.length) * 2;
        long result = mResponseHandlingInputStream.skip(numBytesToSkip);
        Assert.assertEquals(((long) (ResponseHandlingInputStreamTest.TEST_RESPONSE_BODY.length)), result);
        ResponseHandlingInputStreamTest.assertBufferMatchesResponseBody(mTestOutputStream.toByteArray(), ResponseHandlingInputStreamTest.TEST_RESPONSE_BODY.length);
        PowerMockito.verifyZeroInteractions(CLog.class);
        mResponseHandlingInputStream.close();
    }

    private static final class TestIOException extends IOException {}

    @Test
    public void testSwallowException() throws IOException {
        OutputStream exceptionOutputStream = new OutputStream() {
            @Override
            public void write(int oneByte) throws IOException {
                throw new ResponseHandlingInputStreamTest.TestIOException();
            }
        };
        ResponseHandlingInputStream responseHandlingInputStream = /* decompressedCounter */
        new ResponseHandlingInputStream(new ByteArrayInputStream(ResponseHandlingInputStreamTest.TEST_RESPONSE_BODY), ResponseHandlingInputStreamTest.TEST_REQUEST_ID, exceptionOutputStream, null, mNetworkPeerManager, new DefaultResponseHandler(mNetworkEventReporter, ResponseHandlingInputStreamTest.TEST_REQUEST_ID));
        PowerMockito.mockStatic(CLog.class);
        responseHandlingInputStream.read();
        PowerMockito.verifyStatic();
    }
}

