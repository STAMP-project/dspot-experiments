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


import com.github.ambry.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests functionality of {@link ByteBufferReadableStreamChannel}.
 */
public class ByteBufferReadableStreamChannelTest {
    /**
     * Tests the common case read operations i.e
     * 1. Create {@link ByteBufferReadableStreamChannel} with random bytes.
     * 2. Calls the different read operations of {@link ByteBufferReadableStreamChannel} and checks that the data read
     * matches the data used to create the {@link ByteBufferReadableStreamChannel}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void commonCaseTest() throws Exception {
        ByteBuffer content = ByteBuffer.wrap(fillRandomBytes(new byte[1024]));
        ByteBufferReadableStreamChannel readableStreamChannel = new ByteBufferReadableStreamChannel(content);
        Assert.assertTrue("ByteBufferReadableStreamChannel is not open", readableStreamChannel.isOpen());
        Assert.assertEquals("Size returned by ByteBufferReadableStreamChannel did not match source array size", content.capacity(), readableStreamChannel.getSize());
        ByteBufferAsyncWritableChannel writeChannel = new ByteBufferAsyncWritableChannel();
        ReadIntoCallback callback = new ReadIntoCallback();
        Future<Long> future = readableStreamChannel.readInto(writeChannel, callback);
        ByteBuffer contentWrapper = ByteBuffer.wrap(content.array());
        while (contentWrapper.hasRemaining()) {
            ByteBuffer recvdContent = writeChannel.getNextChunk();
            Assert.assertNotNull("Written content lesser than original content", recvdContent);
            while (recvdContent.hasRemaining()) {
                Assert.assertTrue("Written content is more than original content", contentWrapper.hasRemaining());
                Assert.assertEquals("Unexpected byte", contentWrapper.get(), recvdContent.get());
            } 
            writeChannel.resolveOldestChunk(null);
        } 
        Assert.assertNull("There should have been no more data in the channel", writeChannel.getNextChunk(0));
        writeChannel.close();
        callback.awaitCallback();
        if ((callback.exception) != null) {
            throw callback.exception;
        }
        long futureBytesRead = future.get();
        Assert.assertEquals("Total bytes written does not match (callback)", content.limit(), callback.bytesRead);
        Assert.assertEquals("Total bytes written does not match (future)", content.limit(), futureBytesRead);
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
        byte[] in = fillRandomBytes(new byte[1]);
        // Bad AWC.
        ByteBufferReadableStreamChannel readableStreamChannel = new ByteBufferReadableStreamChannel(ByteBuffer.wrap(in));
        ReadIntoCallback callback = new ReadIntoCallback();
        try {
            readableStreamChannel.readInto(new BadAsyncWritableChannel(new IOException(errMsg)), callback).get();
            Assert.fail("Should have failed because BadAsyncWritableChannel would have thrown exception");
        } catch (ExecutionException e) {
            Exception exception = ((Exception) (Utils.getRootCause(e)));
            Assert.assertEquals("Exception message does not match expected (future)", errMsg, exception.getMessage());
            callback.awaitCallback();
            Assert.assertEquals("Exception message does not match expected (callback)", errMsg, callback.exception.getMessage());
        }
        // Reading more than once.
        readableStreamChannel = new ByteBufferReadableStreamChannel(ByteBuffer.wrap(in));
        ByteBufferAsyncWritableChannel writeChannel = new ByteBufferAsyncWritableChannel();
        readableStreamChannel.readInto(writeChannel, null);
        try {
            readableStreamChannel.readInto(writeChannel, null);
            Assert.fail("Should have failed because readInto cannot be called more than once");
        } catch (IllegalStateException e) {
            // expected. Nothing to do.
        }
        // Read after close.
        readableStreamChannel = new ByteBufferReadableStreamChannel(ByteBuffer.wrap(in));
        readableStreamChannel.close();
        writeChannel = new ByteBufferAsyncWritableChannel();
        callback = new ReadIntoCallback();
        try {
            readableStreamChannel.readInto(writeChannel, callback).get();
            Assert.fail("ByteBufferReadableStreamChannel has been closed, so read should have thrown ClosedChannelException");
        } catch (ExecutionException e) {
            Exception exception = ((Exception) (Utils.getRootCause(e)));
            Assert.assertTrue("Exception is not ClosedChannelException", (exception instanceof ClosedChannelException));
            callback.awaitCallback();
            Assert.assertEquals("Exceptions of callback and future differ", exception.getMessage(), callback.exception.getMessage());
        }
    }

    /**
     * Tests behavior of read operations on some corner cases.
     * <p/>
     * Corner case list:
     * 1. Blob size is 0.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void readAndWriteCornerCasesTest() throws Exception {
        // 0 sized blob.
        ByteBufferReadableStreamChannel readableStreamChannel = new ByteBufferReadableStreamChannel(ByteBuffer.allocate(0));
        Assert.assertTrue("ByteBufferReadableStreamChannel is not open", readableStreamChannel.isOpen());
        Assert.assertEquals("Size returned by ByteBufferReadableStreamChannel is not 0", 0, readableStreamChannel.getSize());
        ByteBufferAsyncWritableChannel writeChannel = new ByteBufferAsyncWritableChannel();
        ReadIntoCallback callback = new ReadIntoCallback();
        Future<Long> future = readableStreamChannel.readInto(writeChannel, callback);
        ByteBuffer chunk = writeChannel.getNextChunk(0);
        while (chunk != null) {
            writeChannel.resolveOldestChunk(null);
            chunk = writeChannel.getNextChunk(0);
        } 
        callback.awaitCallback();
        Assert.assertEquals("There should have no bytes to read (future)", 0, future.get().longValue());
        Assert.assertEquals("There should have no bytes to read (callback)", 0, callback.bytesRead);
        if ((callback.exception) != null) {
            throw callback.exception;
        }
        writeChannel.close();
        readableStreamChannel.close();
    }

    /**
     * Tests that no exceptions are thrown on repeating idempotent operations. Does <b><i>not</i></b> currently test that
     * state changes are idempotent.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void idempotentOperationsTest() throws IOException {
        byte[] in = fillRandomBytes(new byte[1]);
        ByteBufferReadableStreamChannel byteBufferReadableStreamChannel = new ByteBufferReadableStreamChannel(ByteBuffer.wrap(in));
        Assert.assertTrue("ByteBufferReadableStreamChannel is not open", byteBufferReadableStreamChannel.isOpen());
        byteBufferReadableStreamChannel.close();
        Assert.assertFalse("ByteBufferReadableStreamChannel is not closed", byteBufferReadableStreamChannel.isOpen());
        // should not throw exception.
        byteBufferReadableStreamChannel.close();
        Assert.assertFalse("ByteBufferReadableStreamChannel is not closed", byteBufferReadableStreamChannel.isOpen());
    }
}

