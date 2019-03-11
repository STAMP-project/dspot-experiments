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


import ByteBufferAsyncWritableChannel.EventType;
import com.github.ambry.utils.Utils;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link ByteBufferAsyncWritableChannel}.
 */
public class ByteBufferAsyncWritableChannelTest {
    @Test
    public void commonCaseTest() throws Exception {
        ByteBufferAsyncWritableChannel channel = new ByteBufferAsyncWritableChannel();
        Assert.assertTrue("Channel is not open", channel.isOpen());
        Assert.assertNull("There should have been no chunk returned", channel.getNextChunk(0));
        ChannelWriter channelWriter = new ChannelWriter(channel);
        channelWriter.writeToChannel(10);
        int chunkCount = 0;
        ByteBuffer chunk = channel.getNextChunk();
        while (chunk != null) {
            WriteData writeData = channelWriter.writes.get(chunkCount);
            int chunkSize = chunk.remaining();
            byte[] writtenChunk = writeData.writtenChunk;
            byte[] readChunk = new byte[writtenChunk.length];
            chunk.get(readChunk);
            Assert.assertArrayEquals("Data unequal", writtenChunk, readChunk);
            channel.resolveOldestChunk(null);
            Assert.assertEquals("Unexpected write size (future)", chunkSize, writeData.future.get().longValue());
            Assert.assertEquals("Unexpected write size (callback)", chunkSize, writeData.writeCallback.bytesWritten);
            chunkCount++;
            chunk = channel.getNextChunk(0);
        } 
        Assert.assertEquals("Mismatch in number of chunks", channelWriter.writes.size(), chunkCount);
        channel.close();
        Assert.assertFalse("Channel is still open", channel.isOpen());
        Assert.assertNull("There should have been no chunk returned", channel.getNextChunk());
        Assert.assertNull("There should have been no chunk returned", channel.getNextChunk(0));
    }

    @Test
    public void checkoutMultipleChunksAndResolveTest() throws Exception {
        ByteBufferAsyncWritableChannel channel = new ByteBufferAsyncWritableChannel();
        ChannelWriter channelWriter = new ChannelWriter(channel);
        channelWriter.writeToChannel(5);
        // get all chunks without resolving any
        ByteBuffer chunk = channel.getNextChunk(0);
        while (chunk != null) {
            chunk = channel.getNextChunk(0);
        } 
        // now resolve them one by one and check that ordering is respected.
        for (int i = 0; i < (channelWriter.writes.size()); i++) {
            channel.resolveOldestChunk(null);
            ensureCallbackOrder(channelWriter, i);
        }
        channel.close();
    }

    @Test
    public void closeBeforeFullReadTest() throws Exception {
        ByteBufferAsyncWritableChannel channel = new ByteBufferAsyncWritableChannel();
        Assert.assertTrue("Channel is not open", channel.isOpen());
        ChannelWriter channelWriter = new ChannelWriter(channel);
        channelWriter.writeToChannel(10);
        // read some chunks
        int i = 0;
        for (; i < 3; i++) {
            channel.getNextChunk(0);
            channel.resolveOldestChunk(null);
        }
        channel.close();
        Assert.assertFalse("Channel is still open", channel.isOpen());
        for (; i < (channelWriter.writes.size()); i++) {
            WriteData writeData = channelWriter.writes.get(i);
            try {
                writeData.future.get();
            } catch (ExecutionException e) {
                Exception exception = ((Exception) (Utils.getRootCause(e)));
                Assert.assertTrue("Unexpected exception (future)", (exception instanceof ClosedChannelException));
                Assert.assertTrue("Unexpected exception (callback)", ((writeData.writeCallback.exception) instanceof ClosedChannelException));
            }
        }
    }

    @Test
    public void writeExceptionsTest() throws Exception {
        // null input.
        ByteBufferAsyncWritableChannel channel = new ByteBufferAsyncWritableChannel();
        try {
            channel.write(null, null);
            Assert.fail("Write should have failed");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
        // exception should be piped correctly.
        WriteCallback writeCallback = new WriteCallback(0);
        Future<Long> future = channel.write(ByteBuffer.allocate(1), writeCallback);
        String errMsg = "@@randomMsg@@";
        channel.getNextChunk(0);
        channel.resolveOldestChunk(new Exception(errMsg));
        try {
            future.get();
        } catch (ExecutionException e) {
            Exception exception = ((Exception) (Utils.getRootCause(e)));
            Assert.assertEquals("Unexpected exception message (future)", errMsg, exception.getMessage());
            Assert.assertEquals("Unexpected exception message (callback)", errMsg, writeCallback.exception.getMessage());
        }
    }

    /**
     * Checks the case where a {@link ByteBufferAsyncWritableChannel} is used after it has been closed.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void useAfterCloseTest() throws Exception {
        ByteBufferAsyncWritableChannel channel = new ByteBufferAsyncWritableChannel();
        channel.write(ByteBuffer.allocate(5), null);
        channel.getNextChunk();
        channel.close();
        Assert.assertFalse("Channel is still open", channel.isOpen());
        // ok to close again
        channel.close();
        // ok to resolve chunk
        channel.resolveOldestChunk(null);
        // not ok to write.
        WriteCallback writeCallback = new WriteCallback(0);
        try {
            channel.write(ByteBuffer.allocate(0), writeCallback).get();
            Assert.fail("Write should have failed");
        } catch (ExecutionException e) {
            Exception exception = ((Exception) (Utils.getRootCause(e)));
            Assert.assertTrue("Unexpected exception (future)", (exception instanceof ClosedChannelException));
            Assert.assertTrue("Unexpected exception (callback)", ((writeCallback.exception) instanceof ClosedChannelException));
        }
        // no chunks on getNextChunk()
        Assert.assertNull("There should have been no chunk returned", channel.getNextChunk());
        Assert.assertNull("There should have been no chunk returned", channel.getNextChunk(0));
    }

    /**
     * Test to verify notification for all channel events.
     */
    @Test
    public void testChannelEventNotification() throws Exception {
        final AtomicBoolean writeNotified = new AtomicBoolean(false);
        final AtomicBoolean closeNotified = new AtomicBoolean(false);
        ByteBufferAsyncWritableChannel channel = new ByteBufferAsyncWritableChannel(new ByteBufferAsyncWritableChannel.ChannelEventListener() {
            @Override
            public void onEvent(ByteBufferAsyncWritableChannel.EventType e) {
                if (e == (EventType.Write)) {
                    writeNotified.set(true);
                } else
                    if (e == (EventType.Close)) {
                        closeNotified.set(true);
                    }

            }
        });
        Assert.assertFalse("No write notification should have come in before any write", writeNotified.get());
        channel.write(ByteBuffer.allocate(5), null);
        Assert.assertTrue("Write should have been notified", writeNotified.get());
        Assert.assertFalse("No close event notification should have come in before a close", closeNotified.get());
        channel.close();
        Assert.assertTrue("Close should have been notified", closeNotified.get());
    }
}

