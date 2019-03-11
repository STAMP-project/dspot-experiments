/**
 * Copyright 2015 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License, version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.netty.handler.codec.http2;


import Http2Error.INTERNAL_ERROR;
import StreamByteDistributor.Writer;
import io.netty.util.collection.IntObjectMap;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link UniformStreamByteDistributor}.
 */
public class UniformStreamByteDistributorTest {
    private static final int CHUNK_SIZE = Http2CodecUtil.DEFAULT_MIN_ALLOCATION_CHUNK;

    private static final int STREAM_A = 1;

    private static final int STREAM_B = 3;

    private static final int STREAM_C = 5;

    private static final int STREAM_D = 7;

    private Http2Connection connection;

    private UniformStreamByteDistributor distributor;

    private IntObjectMap<Http2TestUtil.TestStreamByteDistributorStreamState> stateMap;

    @Mock
    private Writer writer;

    @Test
    public void bytesUnassignedAfterProcessing() throws Http2Exception {
        initState(UniformStreamByteDistributorTest.STREAM_A, 1, true);
        initState(UniformStreamByteDistributorTest.STREAM_B, 2, true);
        initState(UniformStreamByteDistributorTest.STREAM_C, 3, true);
        initState(UniformStreamByteDistributorTest.STREAM_D, 4, true);
        Assert.assertFalse(write(10));
        verifyWrite(UniformStreamByteDistributorTest.STREAM_A, 1);
        verifyWrite(UniformStreamByteDistributorTest.STREAM_B, 2);
        verifyWrite(UniformStreamByteDistributorTest.STREAM_C, 3);
        verifyWrite(UniformStreamByteDistributorTest.STREAM_D, 4);
        Mockito.verifyNoMoreInteractions(writer);
        Assert.assertFalse(write(10));
        Mockito.verifyNoMoreInteractions(writer);
    }

    @Test
    public void connectionErrorForWriterException() throws Http2Exception {
        initState(UniformStreamByteDistributorTest.STREAM_A, 1, true);
        initState(UniformStreamByteDistributorTest.STREAM_B, 2, true);
        initState(UniformStreamByteDistributorTest.STREAM_C, 3, true);
        initState(UniformStreamByteDistributorTest.STREAM_D, 4, true);
        Exception fakeException = new RuntimeException("Fake exception");
        Mockito.doThrow(fakeException).when(writer).write(ArgumentMatchers.same(stream(UniformStreamByteDistributorTest.STREAM_C)), ArgumentMatchers.eq(3));
        try {
            write(10);
            Assert.fail("Expected an exception");
        } catch (Http2Exception e) {
            Assert.assertFalse(Http2Exception.isStreamError(e));
            Assert.assertEquals(INTERNAL_ERROR, e.error());
            Assert.assertSame(fakeException, e.getCause());
        }
        verifyWrite(Mockito.atMost(1), UniformStreamByteDistributorTest.STREAM_A, 1);
        verifyWrite(Mockito.atMost(1), UniformStreamByteDistributorTest.STREAM_B, 2);
        verifyWrite(UniformStreamByteDistributorTest.STREAM_C, 3);
        verifyWrite(Mockito.atMost(1), UniformStreamByteDistributorTest.STREAM_D, 4);
        Mockito.doNothing().when(writer).write(ArgumentMatchers.same(stream(UniformStreamByteDistributorTest.STREAM_C)), ArgumentMatchers.eq(3));
        write(10);
        verifyWrite(UniformStreamByteDistributorTest.STREAM_A, 1);
        verifyWrite(UniformStreamByteDistributorTest.STREAM_B, 2);
        verifyWrite(UniformStreamByteDistributorTest.STREAM_C, 3);
        verifyWrite(UniformStreamByteDistributorTest.STREAM_D, 4);
    }

    /**
     * In this test, we verify that each stream is allocated a minimum chunk size. When bytes
     * run out, the remaining streams will be next in line for the next iteration.
     */
    @Test
    public void minChunkShouldBeAllocatedPerStream() throws Http2Exception {
        // Re-assign weights.
        setPriority(UniformStreamByteDistributorTest.STREAM_A, 0, ((short) (50)), false);
        setPriority(UniformStreamByteDistributorTest.STREAM_B, 0, ((short) (200)), false);
        setPriority(UniformStreamByteDistributorTest.STREAM_C, UniformStreamByteDistributorTest.STREAM_A, ((short) (100)), false);
        setPriority(UniformStreamByteDistributorTest.STREAM_D, UniformStreamByteDistributorTest.STREAM_A, ((short) (100)), false);
        // Update the streams.
        initState(UniformStreamByteDistributorTest.STREAM_A, UniformStreamByteDistributorTest.CHUNK_SIZE, true);
        initState(UniformStreamByteDistributorTest.STREAM_B, UniformStreamByteDistributorTest.CHUNK_SIZE, true);
        initState(UniformStreamByteDistributorTest.STREAM_C, UniformStreamByteDistributorTest.CHUNK_SIZE, true);
        initState(UniformStreamByteDistributorTest.STREAM_D, UniformStreamByteDistributorTest.CHUNK_SIZE, true);
        // Only write 3 * chunkSize, so that we'll only write to the first 3 streams.
        int written = 3 * (UniformStreamByteDistributorTest.CHUNK_SIZE);
        Assert.assertTrue(write(written));
        Assert.assertEquals(UniformStreamByteDistributorTest.CHUNK_SIZE, captureWrite(UniformStreamByteDistributorTest.STREAM_A));
        Assert.assertEquals(UniformStreamByteDistributorTest.CHUNK_SIZE, captureWrite(UniformStreamByteDistributorTest.STREAM_B));
        Assert.assertEquals(UniformStreamByteDistributorTest.CHUNK_SIZE, captureWrite(UniformStreamByteDistributorTest.STREAM_C));
        Mockito.verifyNoMoreInteractions(writer);
        resetWriter();
        // Now write again and verify that the last stream is written to.
        Assert.assertFalse(write(UniformStreamByteDistributorTest.CHUNK_SIZE));
        Assert.assertEquals(UniformStreamByteDistributorTest.CHUNK_SIZE, captureWrite(UniformStreamByteDistributorTest.STREAM_D));
        Mockito.verifyNoMoreInteractions(writer);
    }

    @Test
    public void streamWithMoreDataShouldBeEnqueuedAfterWrite() throws Http2Exception {
        // Give the stream a bunch of data.
        initState(UniformStreamByteDistributorTest.STREAM_A, (2 * (UniformStreamByteDistributorTest.CHUNK_SIZE)), true);
        // Write only part of the data.
        Assert.assertTrue(write(UniformStreamByteDistributorTest.CHUNK_SIZE));
        Assert.assertEquals(UniformStreamByteDistributorTest.CHUNK_SIZE, captureWrite(UniformStreamByteDistributorTest.STREAM_A));
        Mockito.verifyNoMoreInteractions(writer);
        resetWriter();
        // Now write the rest of the data.
        Assert.assertFalse(write(UniformStreamByteDistributorTest.CHUNK_SIZE));
        Assert.assertEquals(UniformStreamByteDistributorTest.CHUNK_SIZE, captureWrite(UniformStreamByteDistributorTest.STREAM_A));
        Mockito.verifyNoMoreInteractions(writer);
    }

    @Test
    public void emptyFrameAtHeadIsWritten() throws Http2Exception {
        initState(UniformStreamByteDistributorTest.STREAM_A, 10, true);
        initState(UniformStreamByteDistributorTest.STREAM_B, 0, true);
        initState(UniformStreamByteDistributorTest.STREAM_C, 0, true);
        initState(UniformStreamByteDistributorTest.STREAM_D, 10, true);
        Assert.assertTrue(write(10));
        verifyWrite(UniformStreamByteDistributorTest.STREAM_A, 10);
        verifyWrite(UniformStreamByteDistributorTest.STREAM_B, 0);
        verifyWrite(UniformStreamByteDistributorTest.STREAM_C, 0);
        Mockito.verifyNoMoreInteractions(writer);
    }

    @Test
    public void streamWindowExhaustedDoesNotWrite() throws Http2Exception {
        initState(UniformStreamByteDistributorTest.STREAM_A, 0, true, false);
        initState(UniformStreamByteDistributorTest.STREAM_B, 0, true);
        initState(UniformStreamByteDistributorTest.STREAM_C, 0, true);
        initState(UniformStreamByteDistributorTest.STREAM_D, 0, true, false);
        Assert.assertFalse(write(10));
        verifyWrite(UniformStreamByteDistributorTest.STREAM_B, 0);
        verifyWrite(UniformStreamByteDistributorTest.STREAM_C, 0);
        Mockito.verifyNoMoreInteractions(writer);
    }

    @Test
    public void streamWindowLargerThanIntDoesNotInfiniteLoop() throws Http2Exception {
        initState(UniformStreamByteDistributorTest.STREAM_A, ((Integer.MAX_VALUE) + 1L), true, true);
        Assert.assertTrue(write(Integer.MAX_VALUE));
        verifyWrite(UniformStreamByteDistributorTest.STREAM_A, Integer.MAX_VALUE);
        Assert.assertFalse(write(1));
        verifyWrite(UniformStreamByteDistributorTest.STREAM_A, 1);
    }
}

