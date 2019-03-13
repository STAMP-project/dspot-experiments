package com.bumptech.glide.load.resource.bitmap;


import Bitmap.Config.ARGB_8888;
import Bitmap.Config.RGB_565;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.tests.KeyTester;
import com.bumptech.glide.tests.Util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
@SuppressWarnings("deprecation")
public class BitmapDrawableTransformationTest {
    @Rule
    public final KeyTester keyTester = new KeyTester();

    @Mock
    private BitmapPool bitmapPool;

    @Mock
    private Transformation<Bitmap> wrapped;

    @Mock
    private Resource<BitmapDrawable> drawableResourceToTransform;

    private BitmapDrawableTransformation transformation;

    private Bitmap bitmapToTransform;

    private Application context;

    @Test
    public void testReturnsOriginalResourceIfTransformationDoesNotTransform() {
        int outWidth = 123;
        int outHeight = 456;
        Mockito.when(wrapped.transform(Util.anyContext(), Util.<Bitmap>anyResource(), ArgumentMatchers.eq(outWidth), ArgumentMatchers.eq(outHeight))).thenAnswer(new Answer<Resource<Bitmap>>() {
            @SuppressWarnings("unchecked")
            @Override
            public Resource<Bitmap> answer(InvocationOnMock invocation) throws Throwable {
                return ((Resource<Bitmap>) (invocation.getArguments()[1]));
            }
        });
        Resource<BitmapDrawable> transformed = transformation.transform(context, drawableResourceToTransform, outWidth, outHeight);
        assertThat(transformed).isEqualTo(drawableResourceToTransform);
    }

    @Test
    public void testReturnsNewResourceIfTransformationDoesTransform() {
        int outWidth = 999;
        int outHeight = 555;
        Bitmap transformedBitmap = Bitmap.createBitmap(outWidth, outHeight, RGB_565);
        Resource<Bitmap> transformedBitmapResource = Util.mockResource();
        Mockito.when(transformedBitmapResource.get()).thenReturn(transformedBitmap);
        Mockito.when(wrapped.transform(Util.anyContext(), Util.<Bitmap>anyResource(), ArgumentMatchers.eq(outWidth), ArgumentMatchers.eq(outHeight))).thenReturn(transformedBitmapResource);
        Resource<BitmapDrawable> transformed = transformation.transform(context, drawableResourceToTransform, outWidth, outHeight);
        assertThat(transformed.get().getBitmap()).isEqualTo(transformedBitmap);
    }

    @Test
    public void testProvidesBitmapFromGivenResourceToWrappedTransformation() {
        int outWidth = 332;
        int outHeight = 111;
        Resource<Bitmap> transformed = Util.mockResource();
        Mockito.when(transformed.get()).thenReturn(Bitmap.createBitmap(outWidth, outHeight, ARGB_8888));
        Mockito.when(wrapped.transform(Util.anyContext(), Util.<Bitmap>anyResource(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(transformed);
        transformation.transform(context, drawableResourceToTransform, outWidth, outHeight);
        ArgumentCaptor<Resource<Bitmap>> captor = Util.cast(ArgumentCaptor.forClass(Resource.class));
        Mockito.verify(wrapped).transform(Util.anyContext(), captor.capture(), ArgumentMatchers.eq(outWidth), ArgumentMatchers.eq(outHeight));
        assertThat(captor.getValue().get()).isEqualTo(bitmapToTransform);
    }

    @Test
    public void testEquals() throws NoSuchAlgorithmException {
        Mockito.doAnswer(new Util.WriteDigest("wrapped")).when(wrapped).updateDiskCacheKey(ArgumentMatchers.any(MessageDigest.class));
        @SuppressWarnings("unchecked")
        Transformation<Bitmap> other = Mockito.mock(Transformation.class);
        Mockito.doAnswer(new Util.WriteDigest("other")).when(other).updateDiskCacheKey(ArgumentMatchers.any(MessageDigest.class));
        keyTester.addEquivalenceGroup(transformation, new BitmapDrawableTransformation(wrapped)).addEquivalenceGroup(new BitmapDrawableTransformation(other)).addEquivalenceGroup(wrapped).addRegressionTest(transformation, "adbf45b08ad6468aa147e5b2a23758ef56ab631a2b70ad52501ca358441a34f3").test();
    }
}

