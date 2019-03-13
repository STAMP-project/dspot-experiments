package org.roaringbitmap;


import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestRoaringBitmapWriter {
    private final Supplier<RoaringBitmapWriter<? extends BitmapDataProvider>> supplier;

    public TestRoaringBitmapWriter(Supplier<RoaringBitmapWriter<? extends BitmapDataProvider>> supplier) {
        this.supplier = supplier;
    }

    @Test
    public void addInReverseOrder() {
        RoaringBitmapWriter<? extends BitmapDataProvider> writer = supplier.get();
        writer.add((1 << 17));
        writer.add(0);
        writer.flush();
        Assert.assertArrayEquals(RoaringBitmap.bitmapOf(0, (1 << 17)).toArray(), writer.getUnderlying().toArray());
    }

    @Test
    public void bitmapShouldContainAllValuesAfterFlush() {
        RoaringBitmapWriter<? extends BitmapDataProvider> writer = supplier.get();
        writer.add(0);
        writer.add((1 << 17));
        writer.flush();
        Assert.assertTrue(writer.getUnderlying().contains(0));
        Assert.assertTrue(writer.getUnderlying().contains((1 << 17)));
    }

    @Test
    public void newKeyShouldTriggerFlush() {
        RoaringBitmapWriter<? extends BitmapDataProvider> writer = supplier.get();
        writer.add(0);
        writer.add((1 << 17));
        Assert.assertTrue(writer.getUnderlying().contains(0));
        writer.add((1 << 18));
        Assert.assertTrue(writer.getUnderlying().contains((1 << 17)));
    }

    @Test
    public void writeSameKeyAfterManualFlush() {
        RoaringBitmapWriter<? extends BitmapDataProvider> writer = supplier.get();
        writer.add(0);
        writer.flush();
        writer.add(1);
        writer.flush();
        Assert.assertArrayEquals(RoaringBitmap.bitmapOf(0, 1).toArray(), writer.getUnderlying().toArray());
    }

    @Test
    public void writeRange() {
        RoaringBitmapWriter<? extends BitmapDataProvider> writer = supplier.get();
        writer.add(0);
        writer.add(65500L, 65600L);
        writer.add(1);
        writer.add(65610);
        writer.flush();
        RoaringBitmap expected = RoaringBitmap.bitmapOf(0, 1, 65610);
        expected.add(65500L, 65600L);
        Assert.assertArrayEquals(expected.toArray(), writer.getUnderlying().toArray());
    }

    @Test
    public void testWriteToMaxKeyAfterFlush() {
        RoaringBitmapWriter writer = supplier.get();
        writer.add(0);
        writer.add((-2));
        writer.flush();
        Assert.assertArrayEquals(RoaringBitmap.bitmapOf(0, (-2)).toArray(), writer.get().toArray());
        writer.add((-1));
        Assert.assertArrayEquals(RoaringBitmap.bitmapOf(0, (-2), (-1)).toArray(), writer.get().toArray());
    }

    @Test
    public void testWriteBitmapAfterReset() {
        RoaringBitmapWriter writer = supplier.get();
        writer.add(0);
        writer.add((-2));
        Assert.assertArrayEquals(new int[]{ 0, -2 }, writer.get().toArray());
        writer.reset();
        writer.add(100);
        writer.addMany(4, 5, 6);
        Assert.assertArrayEquals(new int[]{ 4, 5, 6, 100 }, writer.get().toArray());
    }
}

