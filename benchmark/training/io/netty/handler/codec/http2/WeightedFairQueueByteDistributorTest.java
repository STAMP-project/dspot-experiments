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
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class WeightedFairQueueByteDistributorTest extends AbstractWeightedFairQueueByteDistributorDependencyTest {
    private static final int STREAM_A = 1;

    private static final int STREAM_B = 3;

    private static final int STREAM_C = 5;

    private static final int STREAM_D = 7;

    private static final int STREAM_E = 9;

    private static final int ALLOCATION_QUANTUM = 100;

    /**
     * In this test, we block B such that it has no frames. We distribute enough bytes for all streams and stream B
     * should be preserved in the priority queue structure until it has no "active" children, but it should not be
     * doubly added to stream 0.
     *
     * <pre>
     *         0
     *         |
     *         A
     *         |
     *        [B]
     *         |
     *         C
     *         |
     *         D
     * </pre>
     *
     * After the write:
     * <pre>
     *         0
     * </pre>
     */
    @Test
    public void writeWithNonActiveStreamShouldNotDobuleAddToPriorityQueue() throws Http2Exception {
        initState(WeightedFairQueueByteDistributorTest.STREAM_A, 400, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_B, 500, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_C, 600, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_D, 700, true);
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_B, WeightedFairQueueByteDistributorTest.STREAM_A, Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT, true);
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_D, WeightedFairQueueByteDistributorTest.STREAM_C, Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT, true);
        // Block B, but it should still remain in the queue/tree structure.
        initState(WeightedFairQueueByteDistributorTest.STREAM_B, 0, false);
        // Get the streams before the write, because they may be be closed.
        Http2Stream streamA = stream(WeightedFairQueueByteDistributorTest.STREAM_A);
        Http2Stream streamB = stream(WeightedFairQueueByteDistributorTest.STREAM_B);
        Http2Stream streamC = stream(WeightedFairQueueByteDistributorTest.STREAM_C);
        Http2Stream streamD = stream(WeightedFairQueueByteDistributorTest.STREAM_D);
        Mockito.reset(writer);
        Mockito.doAnswer(writeAnswer(true)).when(writer).write(ArgumentMatchers.any(Http2Stream.class), ArgumentMatchers.anyInt());
        Assert.assertFalse(write(((400 + 600) + 700)));
        Assert.assertEquals(400, captureWrites(streamA));
        verifyNeverWrite(streamB);
        Assert.assertEquals(600, captureWrites(streamC));
        Assert.assertEquals(700, captureWrites(streamD));
    }

    @Test
    public void bytesUnassignedAfterProcessing() throws Http2Exception {
        initState(WeightedFairQueueByteDistributorTest.STREAM_A, 1, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_B, 2, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_C, 3, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_D, 4, true);
        Assert.assertFalse(write(10));
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_A, 1);
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_B, 2);
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_C, 3);
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_D, 4);
        Assert.assertFalse(write(10));
        verifyAnyWrite(WeightedFairQueueByteDistributorTest.STREAM_A, 1);
        verifyAnyWrite(WeightedFairQueueByteDistributorTest.STREAM_B, 1);
        verifyAnyWrite(WeightedFairQueueByteDistributorTest.STREAM_C, 1);
        verifyAnyWrite(WeightedFairQueueByteDistributorTest.STREAM_D, 1);
    }

    @Test
    public void connectionErrorForWriterException() throws Http2Exception {
        initState(WeightedFairQueueByteDistributorTest.STREAM_A, 1, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_B, 2, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_C, 3, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_D, 4, true);
        Exception fakeException = new RuntimeException("Fake exception");
        Mockito.doThrow(fakeException).when(writer).write(ArgumentMatchers.same(stream(WeightedFairQueueByteDistributorTest.STREAM_C)), ArgumentMatchers.eq(3));
        try {
            write(10);
            Assert.fail("Expected an exception");
        } catch (Http2Exception e) {
            Assert.assertFalse(Http2Exception.isStreamError(e));
            Assert.assertEquals(INTERNAL_ERROR, e.error());
            Assert.assertSame(fakeException, e.getCause());
        }
        verifyWrite(Mockito.atMost(1), WeightedFairQueueByteDistributorTest.STREAM_A, 1);
        verifyWrite(Mockito.atMost(1), WeightedFairQueueByteDistributorTest.STREAM_B, 2);
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_C, 3);
        verifyWrite(Mockito.atMost(1), WeightedFairQueueByteDistributorTest.STREAM_D, 4);
        Mockito.doAnswer(writeAnswer(false)).when(writer).write(ArgumentMatchers.same(stream(WeightedFairQueueByteDistributorTest.STREAM_C)), ArgumentMatchers.eq(3));
        Assert.assertFalse(write(10));
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_A, 1);
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_B, 2);
        verifyWrite(Mockito.times(2), WeightedFairQueueByteDistributorTest.STREAM_C, 3);
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_D, 4);
    }

    /**
     * In this test, we verify that each stream is allocated a minimum chunk size. When bytes
     * run out, the remaining streams will be next in line for the next iteration.
     */
    @Test
    public void minChunkShouldBeAllocatedPerStream() throws Http2Exception {
        // Re-assign weights.
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_A, 0, ((short) (50)), false);
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_B, 0, ((short) (200)), false);
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_C, WeightedFairQueueByteDistributorTest.STREAM_A, ((short) (100)), false);
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_D, WeightedFairQueueByteDistributorTest.STREAM_A, ((short) (100)), false);
        // Update the streams.
        initState(WeightedFairQueueByteDistributorTest.STREAM_A, WeightedFairQueueByteDistributorTest.ALLOCATION_QUANTUM, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_B, WeightedFairQueueByteDistributorTest.ALLOCATION_QUANTUM, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_C, WeightedFairQueueByteDistributorTest.ALLOCATION_QUANTUM, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_D, WeightedFairQueueByteDistributorTest.ALLOCATION_QUANTUM, true);
        // Only write 3 * chunkSize, so that we'll only write to the first 3 streams.
        int written = 3 * (WeightedFairQueueByteDistributorTest.ALLOCATION_QUANTUM);
        Assert.assertTrue(write(written));
        Assert.assertEquals(WeightedFairQueueByteDistributorTest.ALLOCATION_QUANTUM, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_A));
        Assert.assertEquals(WeightedFairQueueByteDistributorTest.ALLOCATION_QUANTUM, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_B));
        Assert.assertEquals(WeightedFairQueueByteDistributorTest.ALLOCATION_QUANTUM, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_C));
        verifyWrite(Mockito.atMost(1), WeightedFairQueueByteDistributorTest.STREAM_D, 0);
        // Now write again and verify that the last stream is written to.
        Assert.assertFalse(write(WeightedFairQueueByteDistributorTest.ALLOCATION_QUANTUM));
        Assert.assertEquals(WeightedFairQueueByteDistributorTest.ALLOCATION_QUANTUM, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_A));
        Assert.assertEquals(WeightedFairQueueByteDistributorTest.ALLOCATION_QUANTUM, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_B));
        Assert.assertEquals(WeightedFairQueueByteDistributorTest.ALLOCATION_QUANTUM, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_C));
        Assert.assertEquals(WeightedFairQueueByteDistributorTest.ALLOCATION_QUANTUM, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_D));
    }

    /**
     * In this test, we verify that the highest priority frame which has 0 bytes to send, but an empty frame is able
     * to send that empty frame.
     *
     * <pre>
     *         0
     *        / \
     *       A   B
     *      / \
     *     C   D
     * </pre>
     *
     * After the tree shift:
     *
     * <pre>
     *         0
     *         |
     *         A
     *         |
     *         B
     *        / \
     *       C   D
     * </pre>
     */
    @Test
    public void emptyFrameAtHeadIsWritten() throws Http2Exception {
        initState(WeightedFairQueueByteDistributorTest.STREAM_A, 0, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_B, 0, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_C, 0, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_D, 10, true);
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_B, WeightedFairQueueByteDistributorTest.STREAM_A, Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT, true);
        Assert.assertFalse(write(10));
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_A, 0);
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_B, 0);
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_C, 0);
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_D, 10);
    }

    /**
     * In this test, we block A which allows bytes to be written by C and D. Here's a view of the tree (stream A is
     * blocked).
     *
     * <pre>
     *         0
     *        / \
     *      [A]  B
     *      / \
     *     C   D
     * </pre>
     */
    @Test
    public void blockedStreamNoDataShouldSpreadDataToChildren() throws Http2Exception {
        blockedStreamShouldSpreadDataToChildren(false);
    }

    /**
     * In this test, we block A and also give it an empty data frame to send.
     * All bytes should be delegated to by C and D. Here's a view of the tree (stream A is blocked).
     *
     * <pre>
     *           0
     *         /   \
     *      [A](0)  B
     *      / \
     *     C   D
     * </pre>
     */
    @Test
    public void blockedStreamWithDataAndNotAllowedToSendShouldSpreadDataToChildren() throws Http2Exception {
        // A cannot stream.
        initState(WeightedFairQueueByteDistributorTest.STREAM_A, 0, true, false);
        blockedStreamShouldSpreadDataToChildren(false);
    }

    /**
     * In this test, we allow A to send, but expect the flow controller will only write to the stream 1 time.
     * This is because we give the stream a chance to write its empty frame 1 time, and the stream will not
     * be written to again until a update stream is called.
     *
     * <pre>
     *         0
     *        / \
     *       A   B
     *      / \
     *     C   D
     * </pre>
     */
    @Test
    public void streamWithZeroFlowControlWindowAndDataShouldWriteOnlyOnce() throws Http2Exception {
        initState(WeightedFairQueueByteDistributorTest.STREAM_A, 0, true, true);
        blockedStreamShouldSpreadDataToChildren(true);
        // Make sure if we call update stream again, A should write 1 more time.
        initState(WeightedFairQueueByteDistributorTest.STREAM_A, 0, true, true);
        Assert.assertFalse(write(1));
        verifyWrite(Mockito.times(2), WeightedFairQueueByteDistributorTest.STREAM_A, 0);
        // Try to write again, but since no initState A should not write again
        Assert.assertFalse(write(1));
        verifyWrite(Mockito.times(2), WeightedFairQueueByteDistributorTest.STREAM_A, 0);
    }

    /**
     * In this test, we block B which allows all bytes to be written by A. A should not share the data with its children
     * since it's not blocked.
     *
     * <pre>
     *         0
     *        / \
     *       A  [B]
     *      / \
     *     C   D
     * </pre>
     */
    @Test
    public void childrenShouldNotSendDataUntilParentBlocked() throws Http2Exception {
        // B cannot stream.
        initState(WeightedFairQueueByteDistributorTest.STREAM_A, 10, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_C, 10, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_D, 10, true);
        // Write up to 10 bytes.
        Assert.assertTrue(write(10));
        // A is assigned all of the bytes.
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_A, 10);
        verifyNeverWrite(WeightedFairQueueByteDistributorTest.STREAM_B);
        verifyWrite(Mockito.atMost(1), WeightedFairQueueByteDistributorTest.STREAM_C, 0);
        verifyWrite(Mockito.atMost(1), WeightedFairQueueByteDistributorTest.STREAM_D, 0);
    }

    /**
     * In this test, we block B which allows all bytes to be written by A. Once A is complete, it will spill over the
     * remaining of its portion to its children.
     *
     * <pre>
     *         0
     *        / \
     *       A  [B]
     *      / \
     *     C   D
     * </pre>
     */
    @Test
    public void parentShouldWaterFallDataToChildren() throws Http2Exception {
        // B cannot stream.
        initState(WeightedFairQueueByteDistributorTest.STREAM_A, 5, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_C, 10, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_D, 10, true);
        // Write up to 10 bytes.
        Assert.assertTrue(write(10));
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_A, 5);
        verifyNeverWrite(WeightedFairQueueByteDistributorTest.STREAM_B);
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_C, 5);
        verifyNeverWrite(WeightedFairQueueByteDistributorTest.STREAM_D);
        Assert.assertFalse(write(15));
        verifyAnyWrite(WeightedFairQueueByteDistributorTest.STREAM_A, 1);
        verifyNeverWrite(WeightedFairQueueByteDistributorTest.STREAM_B);
        verifyWrite(Mockito.times(2), WeightedFairQueueByteDistributorTest.STREAM_C, 5);
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_D, 10);
    }

    /**
     * In this test, we verify re-prioritizing a stream. We start out with B blocked:
     *
     * <pre>
     *         0
     *        / \
     *       A  [B]
     *      / \
     *     C   D
     * </pre>
     *
     * We then re-prioritize D so that it's directly off of the connection and verify that A and D split the written
     * bytes between them.
     *
     * <pre>
     *           0
     *          /|\
     *        /  |  \
     *       A  [B]  D
     *      /
     *     C
     * </pre>
     */
    @Test
    public void reprioritizeShouldAdjustOutboundFlow() throws Http2Exception {
        // B cannot stream.
        initState(WeightedFairQueueByteDistributorTest.STREAM_A, 10, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_C, 10, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_D, 10, true);
        // Re-prioritize D as a direct child of the connection.
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_D, 0, Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT, false);
        Assert.assertTrue(write(10));
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_A, 10);
        verifyNeverWrite(WeightedFairQueueByteDistributorTest.STREAM_B);
        verifyNeverWrite(WeightedFairQueueByteDistributorTest.STREAM_C);
        verifyWrite(Mockito.atMost(1), WeightedFairQueueByteDistributorTest.STREAM_D, 0);
        Assert.assertFalse(write(20));
        verifyAnyWrite(WeightedFairQueueByteDistributorTest.STREAM_A, 1);
        verifyNeverWrite(WeightedFairQueueByteDistributorTest.STREAM_B);
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_C, 10);
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_D, 10);
    }

    /**
     * Test that the maximum allowed amount the flow controller allows to be sent is always fully allocated if
     * the streams have at least this much data to send. See https://github.com/netty/netty/issues/4266.
     * <pre>
     *            0
     *          / | \
     *        /   |   \
     *      A(0) B(0) C(0)
     *     /
     *    D(> allowed to send in 1 allocation attempt)
     * </pre>
     */
    @Test
    public void unstreamableParentsShouldFeedHungryChildren() throws Http2Exception {
        // Setup the priority tree.
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_A, 0, ((short) (32)), false);
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_B, 0, ((short) (16)), false);
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_C, 0, ((short) (16)), false);
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_D, WeightedFairQueueByteDistributorTest.STREAM_A, ((short) (16)), false);
        final int writableBytes = 100;
        // Send enough so it can not be completely written out
        final int expectedUnsentAmount = 1;
        initState(WeightedFairQueueByteDistributorTest.STREAM_D, (writableBytes + expectedUnsentAmount), true);
        Assert.assertTrue(write(writableBytes));
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_D, writableBytes);
        Assert.assertFalse(write(expectedUnsentAmount));
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_D, expectedUnsentAmount);
    }

    /**
     * In this test, we root all streams at the connection, and then verify that data is split appropriately based on
     * weight (all available data is the same).
     *
     * <pre>
     *           0
     *        / / \ \
     *       A B   C D
     * </pre>
     */
    @Test
    public void writeShouldPreferHighestWeight() throws Http2Exception {
        // Root the streams at the connection and assign weights.
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_A, 0, ((short) (50)), false);
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_B, 0, ((short) (200)), false);
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_C, 0, ((short) (100)), false);
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_D, 0, ((short) (100)), false);
        initState(WeightedFairQueueByteDistributorTest.STREAM_A, 1000, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_B, 1000, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_C, 1000, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_D, 1000, true);
        // Set allocation quantum to 1 so it is easier to see the ratio of total bytes written between each stream.
        distributor.allocationQuantum(1);
        Assert.assertTrue(write(1000));
        Assert.assertEquals(100, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_A));
        Assert.assertEquals(450, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_B));
        Assert.assertEquals(225, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_C));
        Assert.assertEquals(225, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_D));
    }

    /**
     * In this test, we root all streams at the connection, block streams C and D, and then verify that data is
     * prioritized toward stream B which has a higher weight than stream A.
     * <p>
     * We also verify that the amount that is written is not uniform, and not always the allocation quantum.
     *
     * <pre>
     *            0
     *        / /  \  \
     *       A B   [C] [D]
     * </pre>
     */
    @Test
    public void writeShouldFavorPriority() throws Http2Exception {
        // Root the streams at the connection and assign weights.
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_A, 0, ((short) (50)), false);
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_B, 0, ((short) (200)), false);
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_C, 0, ((short) (100)), false);
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_D, 0, ((short) (100)), false);
        initState(WeightedFairQueueByteDistributorTest.STREAM_A, 1000, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_B, 1000, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_C, 1000, false);
        initState(WeightedFairQueueByteDistributorTest.STREAM_D, 1000, false);
        // Set allocation quantum to 1 so it is easier to see the ratio of total bytes written between each stream.
        distributor.allocationQuantum(1);
        Assert.assertTrue(write(100));
        Assert.assertEquals(20, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_A));
        verifyWrite(Mockito.times(20), WeightedFairQueueByteDistributorTest.STREAM_A, 1);
        Assert.assertEquals(80, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_B));
        verifyWrite(Mockito.times(0), WeightedFairQueueByteDistributorTest.STREAM_B, 1);
        verifyNeverWrite(WeightedFairQueueByteDistributorTest.STREAM_C);
        verifyNeverWrite(WeightedFairQueueByteDistributorTest.STREAM_D);
        Assert.assertTrue(write(100));
        Assert.assertEquals(40, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_A));
        verifyWrite(Mockito.times(40), WeightedFairQueueByteDistributorTest.STREAM_A, 1);
        Assert.assertEquals(160, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_B));
        verifyWrite(Mockito.atMost(1), WeightedFairQueueByteDistributorTest.STREAM_B, 1);
        verifyNeverWrite(WeightedFairQueueByteDistributorTest.STREAM_C);
        verifyNeverWrite(WeightedFairQueueByteDistributorTest.STREAM_D);
        Assert.assertTrue(write(1050));
        Assert.assertEquals(250, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_A));
        verifyWrite(Mockito.times(250), WeightedFairQueueByteDistributorTest.STREAM_A, 1);
        Assert.assertEquals(1000, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_B));
        verifyWrite(Mockito.atMost(2), WeightedFairQueueByteDistributorTest.STREAM_B, 1);
        verifyNeverWrite(WeightedFairQueueByteDistributorTest.STREAM_C);
        verifyNeverWrite(WeightedFairQueueByteDistributorTest.STREAM_D);
        Assert.assertFalse(write(750));
        Assert.assertEquals(1000, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_A));
        verifyWrite(Mockito.times(1), WeightedFairQueueByteDistributorTest.STREAM_A, 750);
        Assert.assertEquals(1000, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_B));
        verifyWrite(Mockito.times(0), WeightedFairQueueByteDistributorTest.STREAM_B, 0);
        verifyNeverWrite(WeightedFairQueueByteDistributorTest.STREAM_C);
        verifyNeverWrite(WeightedFairQueueByteDistributorTest.STREAM_D);
    }

    /**
     * In this test, we root all streams at the connection, and then verify that data is split equally among the stream,
     * since they all have the same weight.
     *
     * <pre>
     *           0
     *        / / \ \
     *       A B   C D
     * </pre>
     */
    @Test
    public void samePriorityShouldDistributeBasedOnData() throws Http2Exception {
        // Root the streams at the connection with the same weights.
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_A, 0, Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT, false);
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_B, 0, Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT, false);
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_C, 0, Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT, false);
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_D, 0, Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT, false);
        initState(WeightedFairQueueByteDistributorTest.STREAM_A, 400, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_B, 500, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_C, 0, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_D, 700, true);
        // Set allocation quantum to 1 so it is easier to see the ratio of total bytes written between each stream.
        distributor.allocationQuantum(1);
        Assert.assertTrue(write(999));
        Assert.assertEquals(333, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_A));
        Assert.assertEquals(333, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_B));
        verifyWrite(Mockito.times(1), WeightedFairQueueByteDistributorTest.STREAM_C, 0);
        Assert.assertEquals(333, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_D));
    }

    /**
     * In this test, we call distribute with 0 bytes and verify that all streams with 0 bytes are written.
     *
     * <pre>
     *         0
     *        / \
     *       A   B
     *      / \
     *     C   D
     * </pre>
     *
     * After the tree shift:
     *
     * <pre>
     *         0
     *         |
     *        [A]
     *         |
     *         B
     *        / \
     *       C   D
     * </pre>
     */
    @Test
    public void zeroDistributeShouldWriteAllZeroFrames() throws Http2Exception {
        initState(WeightedFairQueueByteDistributorTest.STREAM_A, 400, false);
        initState(WeightedFairQueueByteDistributorTest.STREAM_B, 0, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_C, 0, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_D, 0, true);
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_B, WeightedFairQueueByteDistributorTest.STREAM_A, Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT, true);
        Assert.assertFalse(write(0));
        verifyNeverWrite(WeightedFairQueueByteDistributorTest.STREAM_A);
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_B, 0);
        verifyAnyWrite(WeightedFairQueueByteDistributorTest.STREAM_B, 1);
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_C, 0);
        verifyAnyWrite(WeightedFairQueueByteDistributorTest.STREAM_C, 1);
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_D, 0);
        verifyAnyWrite(WeightedFairQueueByteDistributorTest.STREAM_D, 1);
    }

    /**
     * In this test, we call distribute with 100 bytes which is the total amount eligible to be written, and also have
     * streams with 0 bytes to write. All of these streams should be written with a single call to distribute.
     *
     * <pre>
     *         0
     *        / \
     *       A   B
     *      / \
     *     C   D
     * </pre>
     *
     * After the tree shift:
     *
     * <pre>
     *         0
     *         |
     *        [A]
     *         |
     *         B
     *        / \
     *       C   D
     * </pre>
     */
    @Test
    public void nonZeroDistributeShouldWriteAllZeroFramesIfAllEligibleDataIsWritten() throws Http2Exception {
        initState(WeightedFairQueueByteDistributorTest.STREAM_A, 400, false);
        initState(WeightedFairQueueByteDistributorTest.STREAM_B, 100, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_C, 0, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_D, 0, true);
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_B, WeightedFairQueueByteDistributorTest.STREAM_A, Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT, true);
        Assert.assertFalse(write(100));
        verifyNeverWrite(WeightedFairQueueByteDistributorTest.STREAM_A);
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_B, 100);
        verifyAnyWrite(WeightedFairQueueByteDistributorTest.STREAM_B, 1);
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_C, 0);
        verifyAnyWrite(WeightedFairQueueByteDistributorTest.STREAM_C, 1);
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_D, 0);
        verifyAnyWrite(WeightedFairQueueByteDistributorTest.STREAM_D, 1);
    }

    /**
     * In this test, we shift the priority tree and verify priority bytes for each subtree are correct
     *
     * <pre>
     *         0
     *        / \
     *       A   B
     *      / \
     *     C   D
     * </pre>
     *
     * After the tree shift:
     *
     * <pre>
     *         0
     *         |
     *         A
     *         |
     *         B
     *        / \
     *       C   D
     * </pre>
     */
    @Test
    public void bytesDistributedWithRestructureShouldBeCorrect() throws Http2Exception {
        initState(WeightedFairQueueByteDistributorTest.STREAM_A, 400, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_B, 500, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_C, 600, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_D, 700, true);
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_B, WeightedFairQueueByteDistributorTest.STREAM_A, Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT, true);
        Assert.assertTrue(write(500));
        Assert.assertEquals(400, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_A));
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_B, 100);
        verifyNeverWrite(WeightedFairQueueByteDistributorTest.STREAM_C);
        verifyNeverWrite(WeightedFairQueueByteDistributorTest.STREAM_D);
        Assert.assertTrue(write(400));
        Assert.assertEquals(400, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_A));
        Assert.assertEquals(500, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_B));
        verifyWrite(Mockito.atMost(1), WeightedFairQueueByteDistributorTest.STREAM_C, 0);
        verifyWrite(Mockito.atMost(1), WeightedFairQueueByteDistributorTest.STREAM_D, 0);
        Assert.assertFalse(write(1300));
        Assert.assertEquals(400, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_A));
        Assert.assertEquals(500, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_B));
        Assert.assertEquals(600, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_C));
        Assert.assertEquals(700, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_D));
    }

    /**
     * In this test, we add a node to the priority tree and verify
     *
     * <pre>
     *         0
     *        / \
     *       A   B
     *      / \
     *     C   D
     * </pre>
     *
     * After the tree shift:
     *
     * <pre>
     *         0
     *        / \
     *       A   B
     *       |
     *       E
     *      / \
     *     C   D
     * </pre>
     */
    @Test
    public void bytesDistributedWithAdditionShouldBeCorrect() throws Http2Exception {
        Http2Stream streamE = connection.local().createStream(WeightedFairQueueByteDistributorTest.STREAM_E, false);
        setPriority(streamE.id(), WeightedFairQueueByteDistributorTest.STREAM_A, Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT, true);
        // Send a bunch of data on each stream.
        initState(WeightedFairQueueByteDistributorTest.STREAM_A, 400, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_B, 500, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_C, 600, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_D, 700, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_E, 900, true);
        Assert.assertTrue(write(900));
        Assert.assertEquals(400, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_A));
        Assert.assertEquals(500, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_B));
        verifyNeverWrite(WeightedFairQueueByteDistributorTest.STREAM_C);
        verifyNeverWrite(WeightedFairQueueByteDistributorTest.STREAM_D);
        verifyWrite(Mockito.atMost(1), WeightedFairQueueByteDistributorTest.STREAM_E, 0);
        Assert.assertTrue(write(900));
        Assert.assertEquals(400, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_A));
        Assert.assertEquals(500, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_B));
        verifyWrite(Mockito.atMost(1), WeightedFairQueueByteDistributorTest.STREAM_C, 0);
        verifyWrite(Mockito.atMost(1), WeightedFairQueueByteDistributorTest.STREAM_D, 0);
        Assert.assertEquals(900, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_E));
        Assert.assertFalse(write(1301));
        Assert.assertEquals(400, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_A));
        Assert.assertEquals(500, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_B));
        Assert.assertEquals(600, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_C));
        Assert.assertEquals(700, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_D));
        Assert.assertEquals(900, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_E));
    }

    /**
     * In this test, we close an internal stream in the priority tree.
     *
     * <pre>
     *         0
     *        / \
     *       A   B
     *      / \
     *     C   D
     * </pre>
     *
     * After the close:
     * <pre>
     *          0
     *        / | \
     *       C  D  B
     * </pre>
     */
    @Test
    public void bytesDistributedShouldBeCorrectWithInternalStreamClose() throws Http2Exception {
        initState(WeightedFairQueueByteDistributorTest.STREAM_A, 400, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_B, 500, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_C, 600, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_D, 700, true);
        stream(WeightedFairQueueByteDistributorTest.STREAM_A).close();
        Assert.assertTrue(write(500));
        verifyNeverWrite(WeightedFairQueueByteDistributorTest.STREAM_A);
        Assert.assertEquals(500, (((captureWrites(WeightedFairQueueByteDistributorTest.STREAM_B)) + (captureWrites(WeightedFairQueueByteDistributorTest.STREAM_C))) + (captureWrites(WeightedFairQueueByteDistributorTest.STREAM_D))));
        Assert.assertFalse(write(1300));
        verifyNeverWrite(WeightedFairQueueByteDistributorTest.STREAM_A);
        Assert.assertEquals(500, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_B));
        Assert.assertEquals(600, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_C));
        Assert.assertEquals(700, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_D));
    }

    /**
     * In this test, we close a leaf stream in the priority tree and verify distribution.
     *
     * <pre>
     *         0
     *        / \
     *       A   B
     *      / \
     *     C   D
     * </pre>
     *
     * After the close:
     * <pre>
     *         0
     *        / \
     *       A   B
     *       |
     *       D
     * </pre>
     */
    @Test
    public void bytesDistributedShouldBeCorrectWithLeafStreamClose() throws Http2Exception {
        initState(WeightedFairQueueByteDistributorTest.STREAM_A, 400, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_B, 500, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_C, 600, true);
        initState(WeightedFairQueueByteDistributorTest.STREAM_D, 700, true);
        stream(WeightedFairQueueByteDistributorTest.STREAM_C).close();
        Assert.assertTrue(write(900));
        Assert.assertEquals(400, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_A));
        Assert.assertEquals(500, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_B));
        verifyNeverWrite(WeightedFairQueueByteDistributorTest.STREAM_C);
        verifyWrite(Mockito.atMost(1), WeightedFairQueueByteDistributorTest.STREAM_D, 0);
        Assert.assertFalse(write(700));
        Assert.assertEquals(400, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_A));
        Assert.assertEquals(500, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_B));
        verifyNeverWrite(WeightedFairQueueByteDistributorTest.STREAM_C);
        Assert.assertEquals(700, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_D));
    }

    @Test
    public void activeStreamDependentOnNewNonActiveStreamGetsQuantum() throws Http2Exception {
        setup(0);
        initState(WeightedFairQueueByteDistributorTest.STREAM_D, 700, true);
        setPriority(WeightedFairQueueByteDistributorTest.STREAM_D, WeightedFairQueueByteDistributorTest.STREAM_E, Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT, true);
        Assert.assertFalse(write(700));
        Assert.assertEquals(700, captureWrites(WeightedFairQueueByteDistributorTest.STREAM_D));
    }

    @Test
    public void streamWindowLargerThanIntDoesNotInfiniteLoop() throws Http2Exception {
        initState(WeightedFairQueueByteDistributorTest.STREAM_A, ((Integer.MAX_VALUE) + 1L), true, true);
        Assert.assertTrue(write(Integer.MAX_VALUE));
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_A, Integer.MAX_VALUE);
        Assert.assertFalse(write(1));
        verifyWrite(WeightedFairQueueByteDistributorTest.STREAM_A, 1);
    }
}

