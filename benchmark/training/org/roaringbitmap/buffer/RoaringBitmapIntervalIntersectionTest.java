package org.roaringbitmap.buffer;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.roaringbitmap.RoaringBitmap;


@RunWith(Parameterized.class)
public class RoaringBitmapIntervalIntersectionTest {
    private final MutableRoaringBitmap bitmap;

    private final long minimum;

    private final long supremum;

    public RoaringBitmapIntervalIntersectionTest(RoaringBitmap bitmap, long minimum, long supremum) {
        this.bitmap = bitmap.toMutableRoaringBitmap();
        this.minimum = minimum;
        this.supremum = supremum;
    }

    @Test
    public void test() {
        MutableRoaringBitmap test = new MutableRoaringBitmap();
        test.add(minimum, supremum);
        Assert.assertEquals(ImmutableRoaringBitmap.intersects(bitmap, test), bitmap.intersects(minimum, supremum));
    }

    @Test
    public void testIntersects() {
        MutableRoaringBitmap test = new MutableRoaringBitmap();
        test.add(minimum, supremum);
        Assert.assertEquals(MutableRoaringBitmap.intersects(bitmap, test), bitmap.intersects(minimum, supremum));
    }

    @Test
    public void testContains() {
        MutableRoaringBitmap test = new MutableRoaringBitmap();
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

