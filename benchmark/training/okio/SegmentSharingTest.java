/**
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package okio;


import ByteString.EMPTY;
import SegmentPool.next;
import org.junit.Assert;
import org.junit.Test;

import static Segment.SIZE;
import static SegmentPool.byteCount;
import static SegmentPool.next;


/**
 * Tests behavior optimized by sharing segments between buffers and byte strings.
 */
public final class SegmentSharingTest {
    private static final String us = TestUtil.repeat('u', (((SIZE) / 2) - 2));

    private static final String vs = TestUtil.repeat('v', (((SIZE) / 2) - 1));

    private static final String ws = TestUtil.repeat('w', ((SIZE) / 2));

    private static final String xs = TestUtil.repeat('x', (((SIZE) / 2) + 1));

    private static final String ys = TestUtil.repeat('y', (((SIZE) / 2) + 2));

    private static final String zs = TestUtil.repeat('z', (((SIZE) / 2) + 3));

    @Test
    public void snapshotOfEmptyBuffer() throws Exception {
        ByteString snapshot = new Buffer().snapshot();
        TestUtil.assertEquivalent(snapshot, EMPTY);
    }

    @Test
    public void snapshotsAreEquivalent() throws Exception {
        ByteString byteString = concatenateBuffers(SegmentSharingTest.xs, SegmentSharingTest.ys, SegmentSharingTest.zs).snapshot();
        TestUtil.assertEquivalent(byteString, concatenateBuffers(SegmentSharingTest.xs, ((SegmentSharingTest.ys) + (SegmentSharingTest.zs))).snapshot());
        TestUtil.assertEquivalent(byteString, concatenateBuffers((((SegmentSharingTest.xs) + (SegmentSharingTest.ys)) + (SegmentSharingTest.zs))).snapshot());
        TestUtil.assertEquivalent(byteString, ByteString.encodeUtf8((((SegmentSharingTest.xs) + (SegmentSharingTest.ys)) + (SegmentSharingTest.zs))));
    }

    @Test
    public void snapshotGetByte() throws Exception {
        ByteString byteString = concatenateBuffers(SegmentSharingTest.xs, SegmentSharingTest.ys, SegmentSharingTest.zs).snapshot();
        Assert.assertEquals('x', byteString.getByte(0));
        Assert.assertEquals('x', byteString.getByte(((SegmentSharingTest.xs.length()) - 1)));
        Assert.assertEquals('y', byteString.getByte(SegmentSharingTest.xs.length()));
        Assert.assertEquals('y', byteString.getByte((((SegmentSharingTest.xs.length()) + (SegmentSharingTest.ys.length())) - 1)));
        Assert.assertEquals('z', byteString.getByte(((SegmentSharingTest.xs.length()) + (SegmentSharingTest.ys.length()))));
        Assert.assertEquals('z', byteString.getByte(((((SegmentSharingTest.xs.length()) + (SegmentSharingTest.ys.length())) + (SegmentSharingTest.zs.length())) - 1)));
        try {
            byteString.getByte((-1));
            Assert.fail();
        } catch (IndexOutOfBoundsException expected) {
        }
        try {
            byteString.getByte((((SegmentSharingTest.xs.length()) + (SegmentSharingTest.ys.length())) + (SegmentSharingTest.zs.length())));
            Assert.fail();
        } catch (IndexOutOfBoundsException expected) {
        }
    }

    @Test
    public void snapshotWriteToOutputStream() throws Exception {
        ByteString byteString = concatenateBuffers(SegmentSharingTest.xs, SegmentSharingTest.ys, SegmentSharingTest.zs).snapshot();
        Buffer out = new Buffer();
        byteString.write(out.outputStream());
        Assert.assertEquals((((SegmentSharingTest.xs) + (SegmentSharingTest.ys)) + (SegmentSharingTest.zs)), out.readUtf8());
    }

    /**
     * Snapshots share their backing byte arrays with the source buffers. Those byte arrays must not
     * be recycled, otherwise the new writer could corrupt the segment.
     */
    @Test
    public void snapshotSegmentsAreNotRecycled() throws Exception {
        Buffer buffer = concatenateBuffers(SegmentSharingTest.xs, SegmentSharingTest.ys, SegmentSharingTest.zs);
        ByteString snapshot = buffer.snapshot();
        Assert.assertEquals((((SegmentSharingTest.xs) + (SegmentSharingTest.ys)) + (SegmentSharingTest.zs)), snapshot.utf8());
        // While locking the pool, confirm that clearing the buffer doesn't release its segments.
        synchronized(SegmentPool.class) {
            next = null;
            byteCount = 0L;
            buffer.clear();
            Assert.assertEquals(null, next);
        }
    }

    /**
     * Clones share their backing byte arrays with the source buffers. Those byte arrays must not
     * be recycled, otherwise the new writer could corrupt the segment.
     */
    @Test
    public void cloneSegmentsAreNotRecycled() throws Exception {
        Buffer buffer = concatenateBuffers(SegmentSharingTest.xs, SegmentSharingTest.ys, SegmentSharingTest.zs);
        Buffer clone = buffer.clone();
        // While locking the pool, confirm that clearing the buffer doesn't release its segments.
        synchronized(SegmentPool.class) {
            next = null;
            byteCount = 0L;
            buffer.clear();
            Assert.assertEquals(null, next);
            clone.clear();
            Assert.assertEquals(null, next);
        }
    }

    @Test
    public void snapshotJavaSerialization() throws Exception {
        ByteString byteString = concatenateBuffers(SegmentSharingTest.xs, SegmentSharingTest.ys, SegmentSharingTest.zs).snapshot();
        TestUtil.assertEquivalent(byteString, TestUtil.reserialize(byteString));
    }

    @Test
    public void clonesAreEquivalent() throws Exception {
        Buffer bufferA = concatenateBuffers(SegmentSharingTest.xs, SegmentSharingTest.ys, SegmentSharingTest.zs);
        Buffer bufferB = bufferA.clone();
        TestUtil.assertEquivalent(bufferA, bufferB);
        TestUtil.assertEquivalent(bufferA, concatenateBuffers(((SegmentSharingTest.xs) + (SegmentSharingTest.ys)), SegmentSharingTest.zs));
    }

    /**
     * Even though some segments are shared, clones can be mutated independently.
     */
    @Test
    public void mutateAfterClone() throws Exception {
        Buffer bufferA = new Buffer();
        bufferA.writeUtf8("abc");
        Buffer bufferB = bufferA.clone();
        bufferA.writeUtf8("def");
        bufferB.writeUtf8("DEF");
        Assert.assertEquals("abcdef", bufferA.readUtf8());
        Assert.assertEquals("abcDEF", bufferB.readUtf8());
    }

    @Test
    public void concatenateSegmentsCanCombine() throws Exception {
        Buffer bufferA = new Buffer().writeUtf8(SegmentSharingTest.ys).writeUtf8(SegmentSharingTest.us);
        Assert.assertEquals(SegmentSharingTest.ys, bufferA.readUtf8(SegmentSharingTest.ys.length()));
        Buffer bufferB = new Buffer().writeUtf8(SegmentSharingTest.vs).writeUtf8(SegmentSharingTest.ws);
        Buffer bufferC = bufferA.clone();
        bufferA.write(bufferB, SegmentSharingTest.vs.length());
        bufferC.writeUtf8(SegmentSharingTest.xs);
        Assert.assertEquals(((SegmentSharingTest.us) + (SegmentSharingTest.vs)), bufferA.readUtf8());
        Assert.assertEquals(SegmentSharingTest.ws, bufferB.readUtf8());
        Assert.assertEquals(((SegmentSharingTest.us) + (SegmentSharingTest.xs)), bufferC.readUtf8());
    }

    @Test
    public void shareAndSplit() throws Exception {
        Buffer bufferA = new Buffer().writeUtf8("xxxx");
        ByteString snapshot = bufferA.snapshot();// Share the segment.

        Buffer bufferB = new Buffer();
        bufferB.write(bufferA, 2);// Split the shared segment in two.

        bufferB.writeUtf8("yy");// Append to the first half of the shared segment.

        Assert.assertEquals("xxxx", snapshot.utf8());
    }

    @Test
    public void appendSnapshotToEmptyBuffer() throws Exception {
        Buffer bufferA = concatenateBuffers(SegmentSharingTest.xs, SegmentSharingTest.ys);
        ByteString snapshot = bufferA.snapshot();
        Buffer bufferB = new Buffer();
        bufferB.write(snapshot);
        TestUtil.assertEquivalent(bufferB, bufferA);
    }

    @Test
    public void appendSnapshotToNonEmptyBuffer() throws Exception {
        Buffer bufferA = concatenateBuffers(SegmentSharingTest.xs, SegmentSharingTest.ys);
        ByteString snapshot = bufferA.snapshot();
        Buffer bufferB = new Buffer().writeUtf8(SegmentSharingTest.us);
        bufferB.write(snapshot);
        TestUtil.assertEquivalent(bufferB, new Buffer().writeUtf8((((SegmentSharingTest.us) + (SegmentSharingTest.xs)) + (SegmentSharingTest.ys))));
    }

    @Test
    public void copyToSegmentSharing() throws Exception {
        Buffer bufferA = concatenateBuffers(SegmentSharingTest.ws, ((SegmentSharingTest.xs) + "aaaa"), SegmentSharingTest.ys, ("bbbb" + (SegmentSharingTest.zs)));
        Buffer bufferB = concatenateBuffers(SegmentSharingTest.us);
        bufferA.copyTo(bufferB, ((SegmentSharingTest.ws.length()) + (SegmentSharingTest.xs.length())), ((4 + (SegmentSharingTest.ys.length())) + 4));
        TestUtil.assertEquivalent(bufferB, new Buffer().writeUtf8(((((SegmentSharingTest.us) + "aaaa") + (SegmentSharingTest.ys)) + "bbbb")));
    }
}

