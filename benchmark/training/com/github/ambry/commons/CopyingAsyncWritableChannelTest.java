/**
 * Copyright 2018 LinkedIn Corp. All rights reserved.
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


import RestServiceErrorCode.RequestTooLarge;
import com.github.ambry.rest.RestServiceException;
import com.github.ambry.utils.TestUtils;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;


/**
 * Tests for {@link CopyingAsyncWritableChannel}.
 */
public class CopyingAsyncWritableChannelTest {
    /**
     * Test that {@link CopyingAsyncWritableChannel} behaves as expected: chunks are copied, callback completed
     * immediately after {@link CopyingAsyncWritableChannel#write} method completes.
     */
    @Test
    public void basicsTest() throws Exception {
        List<byte[]> inputBuffers = CopyingAsyncWritableChannelTest.getBuffers(1000, 20, 201, 0, 79, 1005);
        CopyingAsyncWritableChannel channel = new CopyingAsyncWritableChannel();
        for (int i = 0; i < (inputBuffers.size()); i++) {
            ByteBuffer buf = ByteBuffer.wrap(inputBuffers.get(i));
            CopyingAsyncWritableChannelTest.writeAndCheckCallback(buf, channel, buf.remaining(), null, null);
            CopyingAsyncWritableChannelTest.checkStream(inputBuffers.subList(0, (i + 1)), channel);
        }
        channel.close();
        CopyingAsyncWritableChannelTest.writeAndCheckCallback(ByteBuffer.allocate(0), channel, 0, ClosedChannelException.class, null);
    }

    /**
     * Ensure that buffers are copied and changes to the input buffers after a write call are not reflected in the
     * returned stream.
     */
    @Test
    public void bufferModificationTest() throws Exception {
        byte[] inputBuffer = TestUtils.getRandomBytes(100);
        CopyingAsyncWritableChannel channel = new CopyingAsyncWritableChannel();
        CopyingAsyncWritableChannelTest.writeAndCheckCallback(ByteBuffer.wrap(inputBuffer), channel, inputBuffer.length, null, null);
        CopyingAsyncWritableChannelTest.checkStream(Collections.singletonList(inputBuffer), channel);
        // mutate the input array and check that stream still matches the original content.
        byte[] originalBuffer = Arrays.copyOf(inputBuffer, inputBuffer.length);
        (inputBuffer[50])++;
        CopyingAsyncWritableChannelTest.checkStream(Collections.singletonList(originalBuffer), channel);
    }

    /**
     * Test that the size limit for bytes received is obeyed.
     */
    @Test
    public void sizeLimitTest() throws Exception {
        List<byte[]> inputBuffers = CopyingAsyncWritableChannelTest.getBuffers(1000, 20, 5);
        CopyingAsyncWritableChannel channel = new CopyingAsyncWritableChannel(1023);
        for (Iterator<byte[]> iter = inputBuffers.iterator(); iter.hasNext();) {
            ByteBuffer buf = ByteBuffer.wrap(iter.next());
            if (iter.hasNext()) {
                CopyingAsyncWritableChannelTest.writeAndCheckCallback(buf, channel, buf.remaining(), null, null);
            } else {
                CopyingAsyncWritableChannelTest.writeAndCheckCallback(buf, channel, buf.remaining(), RestServiceException.class, RequestTooLarge);
            }
        }
        // test that no more writes are accepted after size limit exceeded.
        CopyingAsyncWritableChannelTest.writeAndCheckCallback(ByteBuffer.wrap(TestUtils.getRandomBytes(10)), channel, 0, RestServiceException.class, RequestTooLarge);
    }
}

