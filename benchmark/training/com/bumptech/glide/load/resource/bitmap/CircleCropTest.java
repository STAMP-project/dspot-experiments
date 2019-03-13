package com.bumptech.glide.load.resource.bitmap;


import Bitmap.Config.ARGB_8888;
import android.graphics.Bitmap;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.tests.KeyTester;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class CircleCropTest {
    @Rule
    public final KeyTester keyTester = new KeyTester();

    @Mock
    private BitmapPool bitmapPool;

    private CircleCrop circleCrop;

    @Test
    public void testTransform_withSquare() {
        Bitmap redSquare = createSolidRedBitmap(50, 50);
        Bitmap result = circleCrop.transform(bitmapPool, redSquare, 50, 50);
        Bitmap expected = createBitmapWithRedCircle(50, 50);
        assertSamePixels(expected, result);
    }

    @Test
    public void testTransform_reusesBitmap() {
        Bitmap toReuse = Bitmap.createBitmap(50, 50, ARGB_8888);
        Mockito.when(bitmapPool.get(50, 50, ARGB_8888)).thenReturn(toReuse);
        Bitmap redSquare = createSolidRedBitmap(50, 50);
        Bitmap result = circleCrop.transform(bitmapPool, redSquare, 50, 50);
        Assert.assertEquals(toReuse, result);
    }

    @Test
    public void testTransform_withWideRectangle() {
        Bitmap redWideRectangle = createSolidRedBitmap(100, 50);
        Bitmap result = circleCrop.transform(bitmapPool, redWideRectangle, 80, 50);
        Bitmap expected = createBitmapWithRedCircle(80, 50);
        assertSamePixels(expected, result);
    }

    @Test
    public void testTransform_withNarrowRectangle() {
        Bitmap redNarrowRectangle = createSolidRedBitmap(20, 50);
        Bitmap result = circleCrop.transform(bitmapPool, redNarrowRectangle, 40, 80);
        Bitmap expected = createBitmapWithRedCircle(40, 80);
        assertSamePixels(expected, result);
    }

    @Test
    public void testEquals() {
        keyTester.addEquivalenceGroup(circleCrop, new CircleCrop()).addEquivalenceGroup(Mockito.mock(Transformation.class)).addRegressionTest(new CircleCrop(), "1442365bcc658f89310e39844ef4be58f4b16e52c283254e5a458020f56acb90").test();
    }
}

