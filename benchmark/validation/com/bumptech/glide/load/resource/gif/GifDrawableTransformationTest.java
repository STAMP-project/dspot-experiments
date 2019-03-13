package com.bumptech.glide.load.resource.gif;


import Bitmap.Config.ARGB_8888;
import android.content.Context;
import android.graphics.Bitmap;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.UnitTransformation;
import com.bumptech.glide.tests.KeyTester;
import com.bumptech.glide.tests.Util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class GifDrawableTransformationTest {
    @Rule
    public final KeyTester keyTester = new KeyTester();

    @Mock
    private Transformation<Bitmap> wrapped;

    @Mock
    private BitmapPool bitmapPool;

    private GifDrawableTransformation transformation;

    private Context context;

    @Test
    @SuppressWarnings("unchecked")
    public void testSetsTransformationAsFrameTransformation() {
        Resource<GifDrawable> resource = Util.mockResource();
        GifDrawable gifDrawable = Mockito.mock(GifDrawable.class);
        Transformation<Bitmap> unitTransformation = UnitTransformation.get();
        Mockito.when(gifDrawable.getFrameTransformation()).thenReturn(unitTransformation);
        Mockito.when(gifDrawable.getIntrinsicWidth()).thenReturn(500);
        Mockito.when(gifDrawable.getIntrinsicHeight()).thenReturn(500);
        Mockito.when(resource.get()).thenReturn(gifDrawable);
        Bitmap firstFrame = Bitmap.createBitmap(100, 100, ARGB_8888);
        Mockito.when(gifDrawable.getFirstFrame()).thenReturn(firstFrame);
        final int width = 123;
        final int height = 456;
        Bitmap expectedBitmap = Bitmap.createBitmap(width, height, ARGB_8888);
        Resource<Bitmap> expectedResource = Util.mockResource();
        Mockito.when(expectedResource.get()).thenReturn(expectedBitmap);
        Mockito.when(wrapped.transform(ArgumentMatchers.any(Context.class), Util.<Bitmap>anyResource(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(expectedResource);
        transformation.transform(context, resource, width, height);
        Mockito.verify(gifDrawable).setFrameTransformation(ArgumentMatchers.isA(Transformation.class), ArgumentMatchers.eq(expectedBitmap));
    }

    @Test
    public void testEquals() throws NoSuchAlgorithmException {
        Mockito.doAnswer(new Util.WriteDigest("first")).when(wrapped).updateDiskCacheKey(ArgumentMatchers.isA(MessageDigest.class));
        @SuppressWarnings("unchecked")
        Transformation<Bitmap> other = Mockito.mock(Transformation.class);
        Mockito.doAnswer(new Util.WriteDigest("other")).when(other).updateDiskCacheKey(ArgumentMatchers.isA(MessageDigest.class));
        keyTester.addEquivalenceGroup(transformation, new GifDrawableTransformation(wrapped), new GifDrawableTransformation(wrapped)).addEquivalenceGroup(wrapped).addEquivalenceGroup(new GifDrawableTransformation(other)).addRegressionTest(new GifDrawableTransformation(wrapped), "a7937b64b8caa58f03721bb6bacf5c78cb235febe0e70b1b84cd99541461a08e").test();
    }
}

