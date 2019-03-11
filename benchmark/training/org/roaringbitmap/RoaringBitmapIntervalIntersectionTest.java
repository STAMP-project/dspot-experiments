package org.roaringbitmap;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class RoaringBitmapIntervalIntersectionTest {
    private final RoaringBitmap bitmap;

    private final long minimum;

    private final long supremum;

    public RoaringBitmapIntervalIntersectionTest(RoaringBitmap bitmap, long minimum, long supremum) {
        this.bitmap = bitmap;
        this.minimum = minimum;
        this.supremum = supremum;
    }

    @Test
    public void testIntersects() {
        RoaringBitmap test = new RoaringBitmap();
        test.add(minimum, supremum);
        Assert.assertEquals(RoaringBitmap.intersects(bitmap, test), bitmap.intersects(minimum, supremum));
    }

    @Test
    public void testContains() {
        RoaringBitmap test = new RoaringBitmap();
        test.add(minimum, supremum);
        Assert.assertEquals(bitmap.contains(test), bitmap.contains(minimum, supremum));
        Assert.assertTrue(test.contains(minimum, supremum));
    }

    @Test
    public void ifContainsThenIntersects() {
        boolean contains = bitmap.contains(minimum, supremum);
        boolean intersects = bitmap.intersects(minimum, supremum);
        Assert.assertTrue(((!contains) || intersects));
    }
}

