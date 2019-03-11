package org.roaringbitmap;


import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.roaringbitmap.buffer.MutableRoaringBitmap;


@RunWith(Parameterized.class)
public class TestConcatenation {
    private final RoaringBitmap bitmap;

    private final int offset;

    public TestConcatenation(RoaringBitmap bitmap, int offset) {
        this.bitmap = bitmap;
        this.offset = offset;
    }

    @Test
    public void testElementwiseOffsetAppliedCorrectly() {
        int[] array1 = bitmap.toArray();
        for (int i = 0; i < (array1.length); ++i) {
            array1[i] += offset;
        }
        RoaringBitmap shifted = RoaringBitmap.addOffset(bitmap, offset);
        Assert.assertArrayEquals(TestConcatenation.failureMessage(bitmap), array1, shifted.toArray());
    }

    @Test
    public void testElementwiseOffsetAppliedCorrectlyBuffer() {
        int[] array1 = bitmap.toArray();
        for (int i = 0; i < (array1.length); ++i) {
            array1[i] += offset;
        }
        MutableRoaringBitmap shifted = MutableRoaringBitmap.addOffset(bitmap.toMutableRoaringBitmap(), offset);
        Assert.assertArrayEquals(TestConcatenation.failureMessage(bitmap), array1, shifted.toArray());
    }

    @Test
    public void testCardinalityPreserved() {
        RoaringBitmap shifted = RoaringBitmap.addOffset(bitmap, offset);
        Assert.assertEquals(TestConcatenation.failureMessage(bitmap), bitmap.getCardinality(), shifted.getCardinality());
    }

    @Test
    public void testCardinalityPreservedBuffer() {
        MutableRoaringBitmap shifted = MutableRoaringBitmap.addOffset(bitmap.toMutableRoaringBitmap(), offset);
        Assert.assertEquals(TestConcatenation.failureMessage(bitmap), bitmap.getCardinality(), shifted.getCardinality());
    }

    @Test
    public void canSerializeAndDeserialize() throws IOException {
        RoaringBitmap shifted = RoaringBitmap.addOffset(bitmap, offset);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        shifted.serialize(out);
        RoaringBitmap deserialized = new RoaringBitmap();
        deserialized.deserialize(ByteStreams.newDataInput(out.toByteArray()));
        Assert.assertEquals(TestConcatenation.failureMessage(bitmap), shifted, deserialized);
    }

    @Test
    public void canSerializeAndDeserializeBuffer() throws IOException {
        MutableRoaringBitmap shifted = MutableRoaringBitmap.addOffset(bitmap.toMutableRoaringBitmap(), offset);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        shifted.serialize(out);
        MutableRoaringBitmap deserialized = new MutableRoaringBitmap();
        deserialized.deserialize(ByteStreams.newDataInput(out.toByteArray()));
        Assert.assertEquals(TestConcatenation.failureMessage(bitmap), shifted, deserialized);
    }
}

