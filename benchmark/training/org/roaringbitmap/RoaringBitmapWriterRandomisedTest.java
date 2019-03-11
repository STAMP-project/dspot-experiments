package org.roaringbitmap;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class RoaringBitmapWriterRandomisedTest {
    private final int[] values;

    public RoaringBitmapWriterRandomisedTest(int[] values) {
        this.values = values;
    }

    @Test
    public void shouldBuildSameBitmapAsBitmapOf() {
        RoaringBitmapWriter<RoaringBitmap> writer = RoaringBitmapWriter.writer().expectedRange(Util.toUnsignedLong(min()), Util.toUnsignedLong(max())).get();
        for (int i : values) {
            writer.add(i);
        }
        writer.flush();
        verify(writer.getUnderlying());
    }

    @Test
    public void shouldBuildSameBitmapAsBitmapOfWithAddMany() {
        RoaringBitmapWriter<RoaringBitmap> writer = RoaringBitmapWriter.writer().expectedRange(Util.toUnsignedLong(min()), Util.toUnsignedLong(max())).get();
        writer.addMany(values);
        writer.flush();
        verify(writer.getUnderlying());
    }

    @Test
    public void getShouldFlushToTheUnderlyingBitmap() {
        RoaringBitmapWriter<RoaringBitmap> writer = RoaringBitmapWriter.writer().expectedRange(Util.toUnsignedLong(min()), Util.toUnsignedLong(max())).get();
        writer.addMany(values);
        verify(writer.get());
    }

    @Test
    public void getShouldFlushToTheUnderlyingBitmap_ConstantMemory() {
        RoaringBitmapWriter<RoaringBitmap> writer = RoaringBitmapWriter.writer().constantMemory().get();
        writer.addMany(values);
        verify(writer.get());
    }

    @Test
    public void shouldBuildSameBitmapAsBitmapOf_ConstantMemory() {
        RoaringBitmapWriter<RoaringBitmap> writer = RoaringBitmapWriter.writer().constantMemory().expectedRange(Util.toUnsignedLong(min()), Util.toUnsignedLong(max())).get();
        for (int i : values) {
            writer.add(i);
        }
        writer.flush();
        verify(writer.getUnderlying());
    }

    @Test
    public void shouldBuildSameBitmapAsBitmapOfWithAddMany_ConstantMemory() {
        RoaringBitmapWriter<RoaringBitmap> writer = RoaringBitmapWriter.writer().constantMemory().expectedRange(Util.toUnsignedLong(min()), Util.toUnsignedLong(max())).get();
        writer.addMany(values);
        writer.flush();
        verify(writer.getUnderlying());
    }
}

