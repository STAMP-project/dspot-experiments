/**
 * Copyright (C) 2014 Square, Inc.
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


import Segment.SIZE;
import SegmentPool.MAX_SIZE;
import SegmentPool.byteCount;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

import static Segment.SIZE;
import static SegmentPool.MAX_SIZE;


/**
 * Tests solely for the behavior of Buffer's implementation. For generic BufferedSink or
 * BufferedSource behavior use BufferedSinkTest or BufferedSourceTest, respectively.
 */
public final class BufferTest {
    @Test
    public void readAndWriteUtf8() throws Exception {
        Buffer buffer = new Buffer();
        buffer.writeUtf8("ab");
        Assert.assertEquals(2, buffer.size());
        buffer.writeUtf8("cdef");
        Assert.assertEquals(6, buffer.size());
        Assert.assertEquals("abcd", buffer.readUtf8(4));
        Assert.assertEquals(2, buffer.size());
        Assert.assertEquals("ef", buffer.readUtf8(2));
        Assert.assertEquals(0, buffer.size());
        try {
            buffer.readUtf8(1);
            Assert.fail();
        } catch (ArrayIndexOutOfBoundsException expected) {
        }
    }

    @Test
    public void completeSegmentByteCountOnEmptyBuffer() throws Exception {
        Buffer buffer = new Buffer();
        Assert.assertEquals(0, buffer.completeSegmentByteCount());
    }

    @Test
    public void completeSegmentByteCountOnBufferWithFullSegments() throws Exception {
        Buffer buffer = new Buffer();
        buffer.writeUtf8(TestUtil.repeat('a', ((SIZE) * 4)));
        Assert.assertEquals(((SIZE) * 4), buffer.completeSegmentByteCount());
    }

    @Test
    public void completeSegmentByteCountOnBufferWithIncompleteTailSegment() throws Exception {
        Buffer buffer = new Buffer();
        buffer.writeUtf8(TestUtil.repeat('a', (((SIZE) * 4) - 10)));
        Assert.assertEquals(((SIZE) * 3), buffer.completeSegmentByteCount());
    }

    @Test
    public void toStringOnEmptyBuffer() throws Exception {
        Buffer buffer = new Buffer();
        Assert.assertEquals("Buffer[size=0]", buffer.toString());
    }

    @Test
    public void toStringOnSmallBufferIncludesContents() throws Exception {
        Buffer buffer = new Buffer();
        buffer.write(ByteString.decodeHex("a1b2c3d4e5f61a2b3c4d5e6f10203040"));
        Assert.assertEquals("Buffer[size=16 data=a1b2c3d4e5f61a2b3c4d5e6f10203040]", buffer.toString());
    }

    @Test
    public void toStringOnLargeBufferIncludesMd5() throws Exception {
        Buffer buffer = new Buffer();
        buffer.write(ByteString.encodeUtf8("12345678901234567"));
        Assert.assertEquals("Buffer[size=17 md5=2c9728a2138b2f25e9f89f99bdccf8db]", buffer.toString());
    }

    @Test
    public void toStringOnMultipleSegmentBuffer() throws Exception {
        Buffer buffer = new Buffer();
        buffer.writeUtf8(TestUtil.repeat('a', 6144));
        Assert.assertEquals("Buffer[size=6144 md5=d890021f28522533c1cc1b9b1f83ce73]", buffer.toString());
    }

    @Test
    public void multipleSegmentBuffers() throws Exception {
        Buffer buffer = new Buffer();
        buffer.writeUtf8(TestUtil.repeat('a', 1000));
        buffer.writeUtf8(TestUtil.repeat('b', 2500));
        buffer.writeUtf8(TestUtil.repeat('c', 5000));
        buffer.writeUtf8(TestUtil.repeat('d', 10000));
        buffer.writeUtf8(TestUtil.repeat('e', 25000));
        buffer.writeUtf8(TestUtil.repeat('f', 50000));
        Assert.assertEquals(TestUtil.repeat('a', 999), buffer.readUtf8(999));// a...a

        Assert.assertEquals((("a" + (TestUtil.repeat('b', 2500))) + "c"), buffer.readUtf8(2502));// ab...bc

        Assert.assertEquals(TestUtil.repeat('c', 4998), buffer.readUtf8(4998));// c...c

        Assert.assertEquals((("c" + (TestUtil.repeat('d', 10000))) + "e"), buffer.readUtf8(10002));// cd...de

        Assert.assertEquals(TestUtil.repeat('e', 24998), buffer.readUtf8(24998));// e...e

        Assert.assertEquals(("e" + (TestUtil.repeat('f', 50000))), buffer.readUtf8(50001));// ef...f

        Assert.assertEquals(0, buffer.size());
    }

    @Test
    public void fillAndDrainPool() throws Exception {
        Buffer buffer = new Buffer();
        // Take 2 * MAX_SIZE segments. This will drain the pool, even if other tests filled it.
        buffer.write(new byte[((int) (MAX_SIZE))]);
        buffer.write(new byte[((int) (MAX_SIZE))]);
        Assert.assertEquals(0, byteCount);
        // Recycle MAX_SIZE segments. They're all in the pool.
        buffer.readByteString(MAX_SIZE);
        Assert.assertEquals(MAX_SIZE, byteCount);
        // Recycle MAX_SIZE more segments. The pool is full so they get garbage collected.
        buffer.readByteString(MAX_SIZE);
        Assert.assertEquals(MAX_SIZE, byteCount);
        // Take MAX_SIZE segments to drain the pool.
        buffer.write(new byte[((int) (MAX_SIZE))]);
        Assert.assertEquals(0, byteCount);
        // Take MAX_SIZE more segments. The pool is drained so these will need to be allocated.
        buffer.write(new byte[((int) (MAX_SIZE))]);
        Assert.assertEquals(0, byteCount);
    }

    @Test
    public void moveBytesBetweenBuffersShareSegment() throws Exception {
        int size = ((SIZE) / 2) - 1;
        List<Integer> segmentSizes = moveBytesBetweenBuffers(TestUtil.repeat('a', size), TestUtil.repeat('b', size));
        Assert.assertEquals(Arrays.asList((size * 2)), segmentSizes);
    }

    @Test
    public void moveBytesBetweenBuffersReassignSegment() throws Exception {
        int size = ((SIZE) / 2) + 1;
        List<Integer> segmentSizes = moveBytesBetweenBuffers(TestUtil.repeat('a', size), TestUtil.repeat('b', size));
        Assert.assertEquals(Arrays.asList(size, size), segmentSizes);
    }

    @Test
    public void moveBytesBetweenBuffersMultipleSegments() throws Exception {
        int size = (3 * (SIZE)) + 1;
        List<Integer> segmentSizes = moveBytesBetweenBuffers(TestUtil.repeat('a', size), TestUtil.repeat('b', size));
        Assert.assertEquals(Arrays.asList(SIZE, SIZE, SIZE, 1, SIZE, SIZE, SIZE, 1), segmentSizes);
    }

    /**
     * The big part of source's first segment is being moved.
     */
    @Test
    public void writeSplitSourceBufferLeft() throws Exception {
        int writeSize = ((SIZE) / 2) + 1;
        Buffer sink = new Buffer();
        sink.writeUtf8(TestUtil.repeat('b', ((SIZE) - 10)));
        Buffer source = new Buffer();
        source.writeUtf8(TestUtil.repeat('a', ((SIZE) * 2)));
        sink.write(source, writeSize);
        Assert.assertEquals(Arrays.asList(((SIZE) - 10), writeSize), sink.segmentSizes());
        Assert.assertEquals(Arrays.asList(((SIZE) - writeSize), SIZE), source.segmentSizes());
    }

    /**
     * The big part of source's first segment is staying put.
     */
    @Test
    public void writeSplitSourceBufferRight() throws Exception {
        int writeSize = ((SIZE) / 2) - 1;
        Buffer sink = new Buffer();
        sink.writeUtf8(TestUtil.repeat('b', ((SIZE) - 10)));
        Buffer source = new Buffer();
        source.writeUtf8(TestUtil.repeat('a', ((SIZE) * 2)));
        sink.write(source, writeSize);
        Assert.assertEquals(Arrays.asList(((SIZE) - 10), writeSize), sink.segmentSizes());
        Assert.assertEquals(Arrays.asList(((SIZE) - writeSize), SIZE), source.segmentSizes());
    }

    @Test
    public void writePrefixDoesntSplit() throws Exception {
        Buffer sink = new Buffer();
        sink.writeUtf8(TestUtil.repeat('b', 10));
        Buffer source = new Buffer();
        source.writeUtf8(TestUtil.repeat('a', ((SIZE) * 2)));
        sink.write(source, 20);
        Assert.assertEquals(Arrays.asList(30), sink.segmentSizes());
        Assert.assertEquals(Arrays.asList(((SIZE) - 20), SIZE), source.segmentSizes());
        Assert.assertEquals(30, sink.size());
        Assert.assertEquals((((SIZE) * 2) - 20), source.size());
    }

    @Test
    public void writePrefixDoesntSplitButRequiresCompact() throws Exception {
        Buffer sink = new Buffer();
        sink.writeUtf8(TestUtil.repeat('b', ((SIZE) - 10)));// limit = size - 10

        sink.readUtf8(((SIZE) - 20));// pos = size = 20

        Buffer source = new Buffer();
        source.writeUtf8(TestUtil.repeat('a', ((SIZE) * 2)));
        sink.write(source, 20);
        Assert.assertEquals(Arrays.asList(30), sink.segmentSizes());
        Assert.assertEquals(Arrays.asList(((SIZE) - 20), SIZE), source.segmentSizes());
        Assert.assertEquals(30, sink.size());
        Assert.assertEquals((((SIZE) * 2) - 20), source.size());
    }

    @Test
    public void copyToSpanningSegments() throws Exception {
        Buffer source = new Buffer();
        source.writeUtf8(TestUtil.repeat('a', ((SIZE) * 2)));
        source.writeUtf8(TestUtil.repeat('b', ((SIZE) * 2)));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        source.copyTo(out, 10, ((SIZE) * 3));
        Assert.assertEquals(((TestUtil.repeat('a', (((SIZE) * 2) - 10))) + (TestUtil.repeat('b', ((SIZE) + 10)))), out.toString());
        Assert.assertEquals(((TestUtil.repeat('a', ((SIZE) * 2))) + (TestUtil.repeat('b', ((SIZE) * 2)))), source.readUtf8(((SIZE) * 4)));
    }

    @Test
    public void copyToStream() throws Exception {
        Buffer buffer = new Buffer().writeUtf8("hello, world!");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        buffer.copyTo(out);
        String outString = new String(out.toByteArray(), Util.UTF_8);
        Assert.assertEquals("hello, world!", outString);
        Assert.assertEquals("hello, world!", buffer.readUtf8());
    }

    @Test
    public void writeToSpanningSegments() throws Exception {
        Buffer buffer = new Buffer();
        buffer.writeUtf8(TestUtil.repeat('a', ((SIZE) * 2)));
        buffer.writeUtf8(TestUtil.repeat('b', ((SIZE) * 2)));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        buffer.skip(10);
        buffer.writeTo(out, ((SIZE) * 3));
        Assert.assertEquals(((TestUtil.repeat('a', (((SIZE) * 2) - 10))) + (TestUtil.repeat('b', ((SIZE) + 10)))), out.toString());
        Assert.assertEquals(TestUtil.repeat('b', ((SIZE) - 10)), buffer.readUtf8(buffer.size));
    }

    @Test
    public void writeToStream() throws Exception {
        Buffer buffer = new Buffer().writeUtf8("hello, world!");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        buffer.writeTo(out);
        String outString = new String(out.toByteArray(), Util.UTF_8);
        Assert.assertEquals("hello, world!", outString);
        Assert.assertEquals(0, buffer.size());
    }

    @Test
    public void readFromStream() throws Exception {
        InputStream in = new ByteArrayInputStream("hello, world!".getBytes(Util.UTF_8));
        Buffer buffer = new Buffer();
        buffer.readFrom(in);
        String out = buffer.readUtf8();
        Assert.assertEquals("hello, world!", out);
    }

    @Test
    public void readFromSpanningSegments() throws Exception {
        InputStream in = new ByteArrayInputStream("hello, world!".getBytes(Util.UTF_8));
        Buffer buffer = new Buffer().writeUtf8(TestUtil.repeat('a', ((SIZE) - 10)));
        buffer.readFrom(in);
        String out = buffer.readUtf8();
        Assert.assertEquals(((TestUtil.repeat('a', ((SIZE) - 10))) + "hello, world!"), out);
    }

    @Test
    public void readFromStreamWithCount() throws Exception {
        InputStream in = new ByteArrayInputStream("hello, world!".getBytes(Util.UTF_8));
        Buffer buffer = new Buffer();
        buffer.readFrom(in, 10);
        String out = buffer.readUtf8();
        Assert.assertEquals("hello, wor", out);
    }

    @Test
    public void moveAllRequestedBytesWithRead() throws Exception {
        Buffer sink = new Buffer();
        sink.writeUtf8(TestUtil.repeat('a', 10));
        Buffer source = new Buffer();
        source.writeUtf8(TestUtil.repeat('b', 15));
        Assert.assertEquals(10, source.read(sink, 10));
        Assert.assertEquals(20, sink.size());
        Assert.assertEquals(5, source.size());
        Assert.assertEquals(((TestUtil.repeat('a', 10)) + (TestUtil.repeat('b', 10))), sink.readUtf8(20));
    }

    @Test
    public void moveFewerThanRequestedBytesWithRead() throws Exception {
        Buffer sink = new Buffer();
        sink.writeUtf8(TestUtil.repeat('a', 10));
        Buffer source = new Buffer();
        source.writeUtf8(TestUtil.repeat('b', 20));
        Assert.assertEquals(20, source.read(sink, 25));
        Assert.assertEquals(30, sink.size());
        Assert.assertEquals(0, source.size());
        Assert.assertEquals(((TestUtil.repeat('a', 10)) + (TestUtil.repeat('b', 20))), sink.readUtf8(30));
    }

    @Test
    public void indexOfWithOffset() throws Exception {
        Buffer buffer = new Buffer();
        int halfSegment = (SIZE) / 2;
        buffer.writeUtf8(TestUtil.repeat('a', halfSegment));
        buffer.writeUtf8(TestUtil.repeat('b', halfSegment));
        buffer.writeUtf8(TestUtil.repeat('c', halfSegment));
        buffer.writeUtf8(TestUtil.repeat('d', halfSegment));
        Assert.assertEquals(0, buffer.indexOf(((byte) ('a')), 0));
        Assert.assertEquals((halfSegment - 1), buffer.indexOf(((byte) ('a')), (halfSegment - 1)));
        Assert.assertEquals(halfSegment, buffer.indexOf(((byte) ('b')), (halfSegment - 1)));
        Assert.assertEquals((halfSegment * 2), buffer.indexOf(((byte) ('c')), (halfSegment - 1)));
        Assert.assertEquals((halfSegment * 3), buffer.indexOf(((byte) ('d')), (halfSegment - 1)));
        Assert.assertEquals((halfSegment * 3), buffer.indexOf(((byte) ('d')), (halfSegment * 2)));
        Assert.assertEquals((halfSegment * 3), buffer.indexOf(((byte) ('d')), (halfSegment * 3)));
        Assert.assertEquals(((halfSegment * 4) - 1), buffer.indexOf(((byte) ('d')), ((halfSegment * 4) - 1)));
    }

    @Test
    public void byteAt() throws Exception {
        Buffer buffer = new Buffer();
        buffer.writeUtf8("a");
        buffer.writeUtf8(TestUtil.repeat('b', SIZE));
        buffer.writeUtf8("c");
        Assert.assertEquals('a', buffer.getByte(0));
        Assert.assertEquals('a', buffer.getByte(0));// getByte doesn't mutate!

        Assert.assertEquals('c', buffer.getByte(((buffer.size) - 1)));
        Assert.assertEquals('b', buffer.getByte(((buffer.size) - 2)));
        Assert.assertEquals('b', buffer.getByte(((buffer.size) - 3)));
    }

    @Test
    public void getByteOfEmptyBuffer() throws Exception {
        Buffer buffer = new Buffer();
        try {
            buffer.getByte(0);
            Assert.fail();
        } catch (IndexOutOfBoundsException expected) {
        }
    }

    @Test
    public void writePrefixToEmptyBuffer() throws IOException {
        Buffer sink = new Buffer();
        Buffer source = new Buffer();
        source.writeUtf8("abcd");
        sink.write(source, 2);
        Assert.assertEquals("ab", sink.readUtf8(2));
    }

    @Test
    public void cloneDoesNotObserveWritesToOriginal() throws Exception {
        Buffer original = new Buffer();
        Buffer clone = original.clone();
        original.writeUtf8("abc");
        Assert.assertEquals(0, clone.size());
    }

    @Test
    public void cloneDoesNotObserveReadsFromOriginal() throws Exception {
        Buffer original = new Buffer();
        original.writeUtf8("abc");
        Buffer clone = original.clone();
        Assert.assertEquals("abc", original.readUtf8(3));
        Assert.assertEquals(3, clone.size());
        Assert.assertEquals("ab", clone.readUtf8(2));
    }

    @Test
    public void originalDoesNotObserveWritesToClone() throws Exception {
        Buffer original = new Buffer();
        Buffer clone = original.clone();
        clone.writeUtf8("abc");
        Assert.assertEquals(0, original.size());
    }

    @Test
    public void originalDoesNotObserveReadsFromClone() throws Exception {
        Buffer original = new Buffer();
        original.writeUtf8("abc");
        Buffer clone = original.clone();
        Assert.assertEquals("abc", clone.readUtf8(3));
        Assert.assertEquals(3, original.size());
        Assert.assertEquals("ab", original.readUtf8(2));
    }

    @Test
    public void cloneMultipleSegments() throws Exception {
        Buffer original = new Buffer();
        original.writeUtf8(TestUtil.repeat('a', ((SIZE) * 3)));
        Buffer clone = original.clone();
        original.writeUtf8(TestUtil.repeat('b', ((SIZE) * 3)));
        clone.writeUtf8(TestUtil.repeat('c', ((SIZE) * 3)));
        Assert.assertEquals(((TestUtil.repeat('a', ((SIZE) * 3))) + (TestUtil.repeat('b', ((SIZE) * 3)))), original.readUtf8(((SIZE) * 6)));
        Assert.assertEquals(((TestUtil.repeat('a', ((SIZE) * 3))) + (TestUtil.repeat('c', ((SIZE) * 3)))), clone.readUtf8(((SIZE) * 6)));
    }

    @Test
    public void equalsAndHashCodeEmpty() throws Exception {
        Buffer a = new Buffer();
        Buffer b = new Buffer();
        Assert.assertTrue(a.equals(b));
        Assert.assertTrue(((a.hashCode()) == (b.hashCode())));
    }

    @Test
    public void equalsAndHashCode() throws Exception {
        Buffer a = new Buffer().writeUtf8("dog");
        Buffer b = new Buffer().writeUtf8("hotdog");
        Assert.assertFalse(a.equals(b));
        Assert.assertFalse(((a.hashCode()) == (b.hashCode())));
        b.readUtf8(3);// Leaves b containing 'dog'.

        Assert.assertTrue(a.equals(b));
        Assert.assertTrue(((a.hashCode()) == (b.hashCode())));
    }

    @Test
    public void equalsAndHashCodeSpanningSegments() throws Exception {
        byte[] data = new byte[1024 * 1024];
        Random dice = new Random(0);
        dice.nextBytes(data);
        Buffer a = bufferWithRandomSegmentLayout(dice, data);
        Buffer b = bufferWithRandomSegmentLayout(dice, data);
        Assert.assertTrue(a.equals(b));
        Assert.assertTrue(((a.hashCode()) == (b.hashCode())));
        (data[((data.length) / 2)])++;// Change a single byte.

        Buffer c = bufferWithRandomSegmentLayout(dice, data);
        Assert.assertFalse(a.equals(c));
        Assert.assertFalse(((a.hashCode()) == (c.hashCode())));
    }

    @Test
    public void bufferInputStreamByteByByte() throws Exception {
        Buffer source = new Buffer();
        source.writeUtf8("abc");
        InputStream in = source.inputStream();
        Assert.assertEquals(3, in.available());
        Assert.assertEquals('a', in.read());
        Assert.assertEquals('b', in.read());
        Assert.assertEquals('c', in.read());
        Assert.assertEquals((-1), in.read());
        Assert.assertEquals(0, in.available());
    }

    @Test
    public void bufferInputStreamBulkReads() throws Exception {
        Buffer source = new Buffer();
        source.writeUtf8("abc");
        byte[] byteArray = new byte[4];
        Arrays.fill(byteArray, ((byte) (-5)));
        InputStream in = source.inputStream();
        Assert.assertEquals(3, in.read(byteArray));
        Assert.assertEquals("[97, 98, 99, -5]", Arrays.toString(byteArray));
        Arrays.fill(byteArray, ((byte) (-7)));
        Assert.assertEquals((-1), in.read(byteArray));
        Assert.assertEquals("[-7, -7, -7, -7]", Arrays.toString(byteArray));
    }

    /**
     * When writing data that's already buffered, there's no reason to page the
     * data by segment.
     */
    @Test
    public void readAllWritesAllSegmentsAtOnce() throws Exception {
        Buffer write1 = new Buffer().writeUtf8(((("" + (TestUtil.repeat('a', SIZE))) + (TestUtil.repeat('b', SIZE))) + (TestUtil.repeat('c', SIZE))));
        Buffer source = new Buffer().writeUtf8(((("" + (TestUtil.repeat('a', SIZE))) + (TestUtil.repeat('b', SIZE))) + (TestUtil.repeat('c', SIZE))));
        MockSink mockSink = new MockSink();
        Assert.assertEquals(((SIZE) * 3), source.readAll(mockSink));
        Assert.assertEquals(0, source.size());
        mockSink.assertLog((((("write(" + write1) + ", ") + (write1.size())) + ")"));
    }

    @Test
    public void writeAllMultipleSegments() throws Exception {
        Buffer source = new Buffer().writeUtf8(TestUtil.repeat('a', ((SIZE) * 3)));
        Buffer sink = new Buffer();
        Assert.assertEquals(((SIZE) * 3), sink.writeAll(source));
        Assert.assertEquals(0, source.size());
        Assert.assertEquals(TestUtil.repeat('a', ((SIZE) * 3)), sink.readUtf8());
    }

    @Test
    public void copyTo() throws Exception {
        Buffer source = new Buffer();
        source.writeUtf8("party");
        Buffer target = new Buffer();
        source.copyTo(target, 1, 3);
        Assert.assertEquals("art", target.readUtf8());
        Assert.assertEquals("party", source.readUtf8());
    }

    @Test
    public void copyToOnSegmentBoundary() throws Exception {
        String as = TestUtil.repeat('a', SIZE);
        String bs = TestUtil.repeat('b', SIZE);
        String cs = TestUtil.repeat('c', SIZE);
        String ds = TestUtil.repeat('d', SIZE);
        Buffer source = new Buffer();
        source.writeUtf8(as);
        source.writeUtf8(bs);
        source.writeUtf8(cs);
        Buffer target = new Buffer();
        target.writeUtf8(ds);
        source.copyTo(target, as.length(), ((bs.length()) + (cs.length())));
        Assert.assertEquals(((ds + bs) + cs), target.readUtf8());
    }

    @Test
    public void copyToOffSegmentBoundary() throws Exception {
        String as = TestUtil.repeat('a', ((SIZE) - 1));
        String bs = TestUtil.repeat('b', ((SIZE) + 2));
        String cs = TestUtil.repeat('c', ((SIZE) - 4));
        String ds = TestUtil.repeat('d', ((SIZE) + 8));
        Buffer source = new Buffer();
        source.writeUtf8(as);
        source.writeUtf8(bs);
        source.writeUtf8(cs);
        Buffer target = new Buffer();
        target.writeUtf8(ds);
        source.copyTo(target, as.length(), ((bs.length()) + (cs.length())));
        Assert.assertEquals(((ds + bs) + cs), target.readUtf8());
    }

    @Test
    public void copyToSourceAndTargetCanBeTheSame() throws Exception {
        String as = TestUtil.repeat('a', SIZE);
        String bs = TestUtil.repeat('b', SIZE);
        Buffer source = new Buffer();
        source.writeUtf8(as);
        source.writeUtf8(bs);
        source.copyTo(source, 0, source.size());
        Assert.assertEquals((((as + bs) + as) + bs), source.readUtf8());
    }

    @Test
    public void copyToEmptySource() throws Exception {
        Buffer source = new Buffer();
        Buffer target = new Buffer().writeUtf8("aaa");
        source.copyTo(target, 0L, 0L);
        Assert.assertEquals("", source.readUtf8());
        Assert.assertEquals("aaa", target.readUtf8());
    }

    @Test
    public void copyToEmptyTarget() throws Exception {
        Buffer source = new Buffer().writeUtf8("aaa");
        Buffer target = new Buffer();
        source.copyTo(target, 0L, 3L);
        Assert.assertEquals("aaa", source.readUtf8());
        Assert.assertEquals("aaa", target.readUtf8());
    }
}

