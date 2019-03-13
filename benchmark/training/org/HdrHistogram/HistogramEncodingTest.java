/**
 * HistogramTest.java
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 * @author Gil Tene
 */
package org.HdrHistogram;


import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;


/**
 * JUnit test for {@link org.HdrHistogram.Histogram}
 */
@RunWith(Theories.class)
public class HistogramEncodingTest {
    static final long highestTrackableValue = (3600L * 1000) * 1000;// e.g. for 1 hr in usec units


    @Test
    public void testHistogramEncoding_ByteBufferHasCorrectPositionSetAfterEncoding() throws Exception {
        Histogram histogram = new Histogram(HistogramEncodingTest.highestTrackableValue, 3);
        int size = histogram.getNeededByteBufferCapacity();
        ByteBuffer buffer = ByteBuffer.allocate(size);
        int bytesWritten = histogram.encodeIntoCompressedByteBuffer(buffer);
        Assert.assertEquals(bytesWritten, buffer.position());
        buffer.rewind();
        bytesWritten = histogram.encodeIntoByteBuffer(buffer);
        Assert.assertEquals(bytesWritten, buffer.position());
    }

    public enum BufferAllocator {

        DIRECT() {
            @Override
            public ByteBuffer allocate(final int size) {
                return ByteBuffer.allocateDirect(size);
            }
        },
        HEAP() {
            @Override
            public ByteBuffer allocate(final int size) {
                return ByteBuffer.allocate(size);
            }
        };
        public abstract ByteBuffer allocate(int size);
    }

    @DataPoints
    public static HistogramEncodingTest.BufferAllocator[] ALLOCATORS = new HistogramEncodingTest.BufferAllocator[]{ HistogramEncodingTest.BufferAllocator.DIRECT, HistogramEncodingTest.BufferAllocator.HEAP };

    @Test
    public void testSimpleIntegerHistogramEncoding() throws Exception {
        Histogram histogram = new Histogram(274877906943L, 3);
        histogram.recordValue(6147);
        histogram.recordValue(1024);
        histogram.recordValue(0);
        ByteBuffer targetBuffer = ByteBuffer.allocate(histogram.getNeededByteBufferCapacity());
        histogram.encodeIntoCompressedByteBuffer(targetBuffer);
        targetBuffer.rewind();
        Histogram decodedHistogram = Histogram.decodeFromCompressedByteBuffer(targetBuffer, 0);
        Assert.assertEquals(histogram, decodedHistogram);
        histogram.recordValueWithCount(100, (1L << 4));// Make total count > 2^4

        targetBuffer.clear();
        histogram.encodeIntoCompressedByteBuffer(targetBuffer);
        targetBuffer.rewind();
        decodedHistogram = Histogram.decodeFromCompressedByteBuffer(targetBuffer, 0);
        Assert.assertEquals(histogram, decodedHistogram);
        histogram.recordValueWithCount(200, (1L << 16));// Make total count > 2^16

        targetBuffer.clear();
        histogram.encodeIntoCompressedByteBuffer(targetBuffer);
        targetBuffer.rewind();
        decodedHistogram = Histogram.decodeFromCompressedByteBuffer(targetBuffer, 0);
        Assert.assertEquals(histogram, decodedHistogram);
        histogram.recordValueWithCount(300, (1L << 20));// Make total count > 2^20

        targetBuffer.clear();
        histogram.encodeIntoCompressedByteBuffer(targetBuffer);
        targetBuffer.rewind();
        decodedHistogram = Histogram.decodeFromCompressedByteBuffer(targetBuffer, 0);
        Assert.assertEquals(histogram, decodedHistogram);
        histogram.recordValueWithCount(400, (1L << 32));// Make total count > 2^32

        targetBuffer.clear();
        histogram.encodeIntoCompressedByteBuffer(targetBuffer);
        targetBuffer.rewind();
        decodedHistogram = Histogram.decodeFromCompressedByteBuffer(targetBuffer, 0);
        Assert.assertEquals(histogram, decodedHistogram);
        histogram.recordValueWithCount(500, (1L << 52));// Make total count > 2^52

        targetBuffer.clear();
        histogram.encodeIntoCompressedByteBuffer(targetBuffer);
        targetBuffer.rewind();
        decodedHistogram = Histogram.decodeFromCompressedByteBuffer(targetBuffer, 0);
        Assert.assertEquals(histogram, decodedHistogram);
    }

    @Test
    public void testSimpleDoubleHistogramEncoding() throws Exception {
        DoubleHistogram histogram = new DoubleHistogram(100000000L, 3);
        histogram.recordValue(6.0);
        histogram.recordValue(1.0);
        histogram.recordValue(0.0);
        ByteBuffer targetBuffer = ByteBuffer.allocate(histogram.getNeededByteBufferCapacity());
        histogram.encodeIntoCompressedByteBuffer(targetBuffer);
        targetBuffer.rewind();
        DoubleHistogram decodedHistogram = DoubleHistogram.decodeFromCompressedByteBuffer(targetBuffer, 0);
        Assert.assertEquals(histogram, decodedHistogram);
    }

    @Test
    public void testResizingHistogramBetweenCompressedEncodings() throws Exception {
        Histogram histogram = new Histogram(3);
        histogram.recordValue(1);
        ByteBuffer targetCompressedBuffer = ByteBuffer.allocate(histogram.getNeededByteBufferCapacity());
        histogram.encodeIntoCompressedByteBuffer(targetCompressedBuffer);
        histogram.recordValue(10000);
        targetCompressedBuffer = ByteBuffer.allocate(histogram.getNeededByteBufferCapacity());
        histogram.encodeIntoCompressedByteBuffer(targetCompressedBuffer);
        targetCompressedBuffer.rewind();
        Histogram histogram2 = Histogram.decodeFromCompressedByteBuffer(targetCompressedBuffer, 0);
        Assert.assertEquals(histogram, histogram2);
    }
}

