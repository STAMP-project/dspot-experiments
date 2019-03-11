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


import com.github.ambry.rest.MockRestRequest;
import com.github.ambry.router.ReadableStreamChannel;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests functionality of {@link ReadableStreamChannelInputStream}.
 */
public class ReadableStreamChannelInputStreamTest {
    private static final int CONTENT_SPLIT_PART_COUNT = 5;

    // intentionally unequal
    private static final int READ_PART_COUNT = 4;

    /**
     * Tests the common cases i.e reading byte by byte, reading by parts and reading all at once.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void commonCaseTest() throws Exception {
        int[] sizes = new int[]{ 0, 1024 * (ReadableStreamChannelInputStreamTest.CONTENT_SPLIT_PART_COUNT) };
        for (int size : sizes) {
            byte[] in = new byte[size];
            new Random().nextBytes(in);
            readByteByByteTest(in);
            readPartByPartTest(in);
            readAllAtOnceTest(in);
        }
    }

    /**
     * Tests cases where read() methods get incorrect input.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void readErrorCasesTest() throws IOException {
        byte[] in = new byte[1024];
        new Random().nextBytes(in);
        ReadableStreamChannel channel = new ByteBufferReadableStreamChannel(ByteBuffer.wrap(in));
        InputStream dstInputStream = new ReadableStreamChannelInputStream(channel);
        try {
            dstInputStream.read(null, 0, in.length);
            Assert.fail("The read should have failed");
        } catch (NullPointerException e) {
            // expected. nothing to do.
        }
        byte[] out = new byte[in.length];
        try {
            dstInputStream.read(out, (-1), out.length);
            Assert.fail("The read should have failed");
        } catch (IndexOutOfBoundsException e) {
            // expected. nothing to do.
        }
        try {
            dstInputStream.read(out, 0, (-1));
            Assert.fail("The read should have failed");
        } catch (IndexOutOfBoundsException e) {
            // expected. nothing to do.
        }
        try {
            dstInputStream.read(out, 0, ((out.length) + 1));
            Assert.fail("The read should have failed");
        } catch (IndexOutOfBoundsException e) {
            // expected. nothing to do.
        }
        Assert.assertEquals("Bytes read should have been 0 because passed len was 0", 0, dstInputStream.read(out, 0, 0));
    }

    /**
     * Tests correctness of {@link ReadableStreamChannelInputStream#available()}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void availableTest() throws Exception {
        int[] sizes = new int[]{ 0, 1024 * (ReadableStreamChannelInputStreamTest.CONTENT_SPLIT_PART_COUNT) };
        for (int size : sizes) {
            byte[] in = new byte[size];
            new Random().nextBytes(in);
            // channel with size and one piece of content.
            ReadableStreamChannel channel = new ByteBufferReadableStreamChannel(ByteBuffer.wrap(in));
            InputStream stream = new ReadableStreamChannelInputStream(channel);
            doAvailableTest(stream, in, in.length);
            stream.close();
            // channel with no size and multiple pieces of content.
            channel = new NoSizeRSC(ByteBuffer.wrap(in));
            stream = new ReadableStreamChannelInputStream(channel);
            doAvailableTest(stream, in, in.length);
            stream.close();
            // channel with no size and multiple pieces of content.
            List<ByteBuffer> contents = splitContent(in, ReadableStreamChannelInputStreamTest.CONTENT_SPLIT_PART_COUNT);
            contents.add(null);
            // assuming all parts are the same length.
            int partLength = contents.get(0).remaining();
            channel = new MockRestRequest(MockRestRequest.DUMMY_DATA, contents);
            stream = new ReadableStreamChannelInputStream(channel);
            doAvailableTest(stream, in, partLength);
            stream.close();
        }
    }

    /**
     * Tests for the case when reads are incomplete either because exceptions were thrown or the read simply did not
     * complete.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void incompleteReadsTest() throws IOException {
        // Exception during read
        String exceptionMsg = "@@randomMsg@@";
        Exception exceptionToThrow = new Exception(exceptionMsg);
        ReadableStreamChannel channel = new IncompleteReadReadableStreamChannel(exceptionToThrow);
        InputStream inputStream = new ReadableStreamChannelInputStream(channel);
        try {
            inputStream.read();
            Assert.fail("The read should have failed");
        } catch (Exception e) {
            while ((e.getCause()) != null) {
                e = ((Exception) (e.getCause()));
            } 
            Assert.assertEquals("Exception messages do not match", exceptionMsg, e.getMessage());
        }
        // incomplete read
        channel = new IncompleteReadReadableStreamChannel(null);
        inputStream = new ReadableStreamChannelInputStream(channel);
        try {
            inputStream.read();
            Assert.fail("The read should have failed");
        } catch (IllegalStateException e) {
            // expected. Nothing to do.
        }
    }
}

