package com.bumptech.glide.load.resource.bitmap;


import Bitmap.Config.ARGB_4444;
import Bitmap.Config.ARGB_8888;
import android.app.Application;
import android.graphics.Bitmap;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.tests.KeyTester;
import com.bumptech.glide.tests.Util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 28)
public class CenterCropTest {
    @Rule
    public final KeyTester keyTester = new KeyTester();

    @Mock
    private Resource<Bitmap> resource;

    @Mock
    private BitmapPool pool;

    @Mock
    private Transformation<Bitmap> transformation;

    private CenterCrop centerCrop;

    private int bitmapWidth;

    private int bitmapHeight;

    private Bitmap bitmap;

    private Application context;

    @Test
    public void testDoesNotPutNullBitmapAcquiredFromPool() {
        Mockito.reset(pool);
        Mockito.when(pool.get(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Config.class))).thenReturn(null);
        centerCrop.transform(context, resource, 100, 100);
        Mockito.verify(pool, Mockito.never()).put(ArgumentMatchers.any(Bitmap.class));
    }

    @Test
    public void testReturnsGivenResourceIfMatchesSizeExactly() {
        Resource<Bitmap> result = centerCrop.transform(context, resource, bitmapWidth, bitmapHeight);
        Assert.assertEquals(resource, result);
    }

    @Test
    public void testDoesNotRecycleGivenResourceIfMatchesSizeExactly() {
        centerCrop.transform(context, resource, bitmapWidth, bitmapHeight);
        Mockito.verify(resource, Mockito.never()).recycle();
    }

    @Test
    public void testDoesNotRecycleGivenResource() {
        centerCrop.transform(context, resource, 50, 50);
        Mockito.verify(resource, Mockito.never()).recycle();
    }

    @Test
    @Config(sdk = 19)
    public void testAsksBitmapPoolForArgb8888IfInConfigIsNull() {
        bitmap.setConfig(null);
        centerCrop.transform(context, resource, 10, 10);
        Mockito.verify(pool).get(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(ARGB_8888));
        Mockito.verify(pool, Mockito.never()).get(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ((Bitmap.Config) (ArgumentMatchers.isNull())));
    }

    @Test
    public void testReturnsBitmapWithExactlyGivenDimensionsIfBitmapIsLargerThanTarget() {
        int expectedWidth = 75;
        int expectedHeight = 74;
        for (int[] dimens : new int[][]{ new int[]{ 800, 200 }, new int[]{ 450, 100 }, new int[]{ 78, 78 } }) {
            Bitmap toTransform = Bitmap.createBitmap(dimens[0], dimens[1], ARGB_4444);
            Mockito.when(resource.get()).thenReturn(toTransform);
            Resource<Bitmap> result = centerCrop.transform(context, resource, expectedWidth, expectedHeight);
            Bitmap transformed = result.get();
            Assert.assertEquals(expectedWidth, transformed.getWidth());
            Assert.assertEquals(expectedHeight, transformed.getHeight());
        }
    }

    @Test
    public void testReturnsBitmapWithExactlyGivenDimensionsIfBitmapIsSmallerThanTarget() {
        int expectedWidth = 100;
        int expectedHeight = 100;
        for (int[] dimens : new int[][]{ new int[]{ 50, 90 }, new int[]{ 150, 2 }, new int[]{ 78, 78 } }) {
            Bitmap toTransform = Bitmap.createBitmap(dimens[0], dimens[1], ARGB_4444);
            Mockito.when(resource.get()).thenReturn(toTransform);
            Resource<Bitmap> result = centerCrop.transform(context, resource, expectedWidth, expectedHeight);
            Bitmap transformed = result.get();
            Assert.assertEquals(expectedWidth, transformed.getWidth());
            Assert.assertEquals(expectedHeight, transformed.getHeight());
        }
    }

    @Test
    public void testEquals() throws NoSuchAlgorithmException {
        Mockito.doAnswer(new Util.WriteDigest("other")).when(transformation).updateDiskCacheKey(ArgumentMatchers.any(MessageDigest.class));
        keyTester.addEquivalenceGroup(new CenterCrop(), new CenterCrop()).addEquivalenceGroup(transformation).addRegressionTest(new CenterCrop(), "68bd5819c42b37efbe7124bb851443a6388ee3e2e9034213da6eaa15381d3457").test();
    }
}

