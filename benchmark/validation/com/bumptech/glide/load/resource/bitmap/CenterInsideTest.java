package com.bumptech.glide.load.resource.bitmap;


import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
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
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowCanvas;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18, shadows = { CenterInsideTest.DrawNothingCanvas.class })
public class CenterInsideTest {
    @Rule
    public final KeyTester keyTester = new KeyTester();

    @Mock
    private Resource<Bitmap> resource;

    @Mock
    private Transformation<Bitmap> transformation;

    private CenterInside centerInside;

    private int bitmapWidth;

    private int bitmapHeight;

    private Application context;

    @Test
    public void testReturnsGivenResourceIfMatchesSizeExactly() {
        Resource<Bitmap> result = centerInside.transform(context, resource, bitmapWidth, bitmapHeight);
        Assert.assertEquals(resource, result);
    }

    @Test
    public void testReturnsGivenResourceIfSmallerThanTarget() {
        Resource<Bitmap> result = centerInside.transform(context, resource, 150, 150);
        Assert.assertEquals(resource, result);
    }

    @Test
    public void testReturnsNewResourceIfLargerThanTarget() {
        Resource<Bitmap> result = centerInside.transform(context, resource, 50, 50);
        Assert.assertNotEquals(resource, result);
    }

    @Test
    public void testDoesNotRecycleGivenResourceIfMatchesSizeExactly() {
        centerInside.transform(context, resource, bitmapWidth, bitmapHeight);
        Mockito.verify(resource, Mockito.never()).recycle();
    }

    @Test
    public void testDoesNotRecycleGivenResource() {
        centerInside.transform(context, resource, 50, 50);
        Mockito.verify(resource, Mockito.never()).recycle();
    }

    @Test
    public void testEquals() throws NoSuchAlgorithmException {
        Mockito.doAnswer(new Util.WriteDigest("other")).when(transformation).updateDiskCacheKey(ArgumentMatchers.any(MessageDigest.class));
        keyTester.addEquivalenceGroup(new CenterInside(), new CenterInside(), centerInside).addEquivalenceGroup(transformation).addRegressionTest(new CenterInside(), "acf83850a2e8e9e809c8bfb999e2aede9e932cb897a15367fac9856b96f3ba33").test();
    }

    @Implements(Canvas.class)
    public static final class DrawNothingCanvas extends ShadowCanvas {
        @Implementation
        @Override
        public void drawBitmap(Bitmap bitmap, Matrix matrix, Paint paint) {
            // Do nothing.
        }
    }
}

