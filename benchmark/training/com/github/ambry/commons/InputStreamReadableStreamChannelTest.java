/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.commons;


import TestUtils.RANDOM;
import com.github.ambry.utils.ByteBufferInputStream;
import com.github.ambry.utils.TestUtils;
import com.github.ambry.utils.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;

import static InputStreamReadableStreamChannel.BUFFER_SIZE;


/**
 * Tests functionality of {@link InputStreamReadableStreamChannel}.
 */
public class InputStreamReadableStreamChannelTest {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    /**
     * Tests different types of {@link InputStream} and different sizes of the stream and ensures that the data is read
     * correctly.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void commonCasesTest() throws Exception {
        int bufSize = BUFFER_SIZE;
        int randSizeLessThanBuffer = (RANDOM.nextInt((bufSize - 2))) + 2;
        int randMultiplier = RANDOM.nextInt(10);
        int[] testStreamSizes = new int[]{ 0, 1, randSizeLessThanBuffer, bufSize, bufSize + 1, bufSize * randMultiplier, (bufSize * randMultiplier) + 1 };
        for (int size : testStreamSizes) {
            byte[] src = TestUtils.getRandomBytes(size);
            InputStream stream = new ByteBufferInputStream(ByteBuffer.wrap(src));
            doReadTest(stream, src, src.length);
            stream = new ByteBufferInputStream(ByteBuffer.wrap(src));
            doReadTest(stream, src, (-1));
            stream = new HaltingInputStream(new ByteBufferInputStream(ByteBuffer.wrap(src)));
            doReadTest(stream, src, src.length);
            stream = new HaltingInputStream(new ByteBufferInputStream(ByteBuffer.wrap(src)));
            doReadTest(stream, src, (-1));
        }
    }

    /**
     * Verfies behavior of {@link InputStreamReadableStreamChannel#close()}.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void closeTest() throws IOException {
        final AtomicBoolean streamOpen = new AtomicBoolean(true);
        InputStream stream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IllegalStateException("Not implemented");
            }

            @Override
            public void close() {
                streamOpen.set(false);
            }
        };
        InputStreamReadableStreamChannel channel = new InputStreamReadableStreamChannel(stream, InputStreamReadableStreamChannelTest.EXECUTOR_SERVICE);
        Assert.assertTrue("Channel should be open", channel.isOpen());
        channel.close();
        Assert.assertFalse("Channel should be closed", channel.isOpen());
        Assert.assertFalse("Stream should be closed", streamOpen.get());
        // close again is ok
        channel.close();
    }

    /**
     * Tests that the right exceptions are thrown when reading into {@link AsyncWritableChannel} fails.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void readIntoAWCFailureTest() throws Exception {
        String errMsg = "@@ExpectedExceptionMessage@@";
        InputStream stream = new ByteBufferInputStream(ByteBuffer.allocate(1));
        // Bad AWC.
        InputStreamReadableStreamChannel channel = new InputStreamReadableStreamChannel(stream, InputStreamReadableStreamChannelTest.EXECUTOR_SERVICE);
        ReadIntoCallback callback = new ReadIntoCallback();
        try {
            channel.readInto(new BadAsyncWritableChannel(new IOException(errMsg)), callback).get();
            Assert.fail("Should have failed because BadAsyncWritableChannel would have thrown exception");
        } catch (ExecutionException e) {
            Exception exception = ((Exception) (Utils.getRootCause(e)));
            Assert.assertEquals("Exception message does not match expected (future)", errMsg, exception.getMessage());
            callback.awaitCallback();
            Assert.assertEquals("Exception message does not match expected (callback)", errMsg, callback.exception.getMessage());
        }
        // Read after close.
        channel = new InputStreamReadableStreamChannel(stream, InputStreamReadableStreamChannelTest.EXECUTOR_SERVICE);
        channel.close();
        CopyingAsyncWritableChannel writeChannel = new CopyingAsyncWritableChannel();
        callback = new ReadIntoCallback();
        try {
            channel.readInto(writeChannel, callback).get();
            Assert.fail("InputStreamReadableStreamChannel has been closed, so read should have thrown ClosedChannelException");
        } catch (ExecutionException e) {
            Exception exception = ((Exception) (Utils.getRootCause(e)));
            Assert.assertTrue("Exception is not ClosedChannelException", (exception instanceof ClosedChannelException));
            callback.awaitCallback();
            Assert.assertEquals("Exceptions of callback and future differ", exception.getMessage(), callback.exception.getMessage());
        }
        // Reading more than once.
        channel = new InputStreamReadableStreamChannel(stream, InputStreamReadableStreamChannelTest.EXECUTOR_SERVICE);
        writeChannel = new CopyingAsyncWritableChannel();
        channel.readInto(writeChannel, null);
        try {
            channel.readInto(writeChannel, null);
            Assert.fail("Should have failed because readInto cannot be called more than once");
        } catch (IllegalStateException e) {
            // expected. Nothing to do.
        }
    }

    /**
     * Tests that the right exceptions are thrown when the provided {@link InputStream} has unexpected behavior.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void badInputStreamTest() throws Exception {
        final String errMsg = "@@ExpectedExceptionMessage@@";
        InputStream stream = new InputStream() {
            @Override
            public int read() throws IOException {
                // this represents any exception - bad behavior or closure before being read completely.
                throw new IllegalStateException(errMsg);
            }
        };
        InputStreamReadableStreamChannel channel = new InputStreamReadableStreamChannel(stream, InputStreamReadableStreamChannelTest.EXECUTOR_SERVICE);
        ReadIntoCallback callback = new ReadIntoCallback();
        try {
            channel.readInto(new BadAsyncWritableChannel(new IOException(errMsg)), callback).get();
            Assert.fail("Should have failed because the InputStream would have thrown exception");
        } catch (ExecutionException e) {
            Exception exception = ((Exception) (Utils.getRootCause(e)));
            Assert.assertEquals("Exception message does not match expected (future)", errMsg, exception.getMessage());
            callback.awaitCallback();
            Assert.assertEquals("Exception message does not match expected (callback)", errMsg, callback.exception.getMessage());
        }
    }
}

