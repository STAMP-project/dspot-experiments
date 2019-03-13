package com.bumptech.glide.load.resource.bitmap;


import Bitmap.Config.ARGB_8888;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.tests.KeyTester;
import com.bumptech.glide.tests.Util;
import java.security.MessageDigest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class DrawableTransformationTest {
    @Rule
    public final KeyTester keyTester = new KeyTester();

    @Mock
    private Transformation<Bitmap> bitmapTransformation;

    private BitmapPool bitmapPool;

    private DrawableTransformation transformation;

    private Context context;

    @Test
    public void transform_withBitmapDrawable_andUnitBitmapTransformation_doesNotRecycle() {
        Mockito.when(bitmapTransformation.transform(ArgumentMatchers.any(Context.class), DrawableTransformationTest.anyBitmapResource(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenAnswer(new DrawableTransformationTest.ReturnGivenResource());
        Bitmap bitmap = Bitmap.createBitmap(100, 200, ARGB_8888);
        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
        @SuppressWarnings("unchecked")
        Resource<Drawable> input = ((Resource<Drawable>) ((Resource<?>) (new BitmapDrawableResource(drawable, bitmapPool))));
        /* outWidth= */
        /* outHeight= */
        transformation.transform(context, input, 100, 200);
        assertThat(bitmap.isRecycled()).isFalse();
    }

    @Test
    public void transform_withBitmapDrawable_andFunctionalBitmapTransformation_doesNotRecycle() {
        Mockito.when(bitmapTransformation.transform(ArgumentMatchers.any(Context.class), DrawableTransformationTest.anyBitmapResource(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenAnswer(new Answer<Resource<Bitmap>>() {
            @Override
            public Resource<Bitmap> answer(InvocationOnMock invocationOnMock) throws Throwable {
                return BitmapResource.obtain(Bitmap.createBitmap(200, 200, ARGB_8888), bitmapPool);
            }
        });
        Bitmap bitmap = Bitmap.createBitmap(100, 200, ARGB_8888);
        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
        @SuppressWarnings("unchecked")
        Resource<Drawable> input = ((Resource<Drawable>) ((Resource<?>) (new BitmapDrawableResource(drawable, bitmapPool))));
        /* outWidth= */
        /* outHeight= */
        transformation.transform(context, input, 100, 200);
        assertThat(bitmap.isRecycled()).isFalse();
    }

    @Test
    public void transform_withColorDrawable_andUnitBitmapTransformation_recycles() {
        bitmapPool = Mockito.mock(BitmapPool.class);
        Glide.tearDown();
        Glide.init(context, new GlideBuilder().setBitmapPool(bitmapPool));
        Mockito.when(bitmapTransformation.transform(ArgumentMatchers.any(Context.class), DrawableTransformationTest.anyBitmapResource(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenAnswer(new DrawableTransformationTest.ReturnGivenResource());
        ColorDrawable colorDrawable = new ColorDrawable(Color.RED);
        final Resource<Drawable> input = new com.bumptech.glide.load.resource.SimpleResource<Drawable>(colorDrawable);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                Bitmap bitmap = ((Bitmap) (invocationOnMock.getArguments()[0]));
                assertThat(bitmap.getWidth()).isEqualTo(100);
                assertThat(bitmap.getHeight()).isEqualTo(200);
                return null;
            }
        }).when(bitmapPool).put(ArgumentMatchers.any(Bitmap.class));
        Mockito.when(bitmapPool.get(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Config.class))).thenAnswer(new Answer<Bitmap>() {
            @Override
            public Bitmap answer(InvocationOnMock invocationOnMock) throws Throwable {
                int width = ((Integer) (invocationOnMock.getArguments()[0]));
                int height = ((Integer) (invocationOnMock.getArguments()[1]));
                Bitmap.Config config = ((Bitmap.Config) (invocationOnMock.getArguments()[2]));
                return Bitmap.createBitmap(width, height, config);
            }
        });
        /* outWidth= */
        /* outHeight= */
        transformation.transform(context, input, 100, 200);
        Mockito.verify(bitmapPool).put(ArgumentMatchers.isA(Bitmap.class));
    }

    @Test
    public void testEquals() {
        BitmapTransformation otherBitmapTransformation = Mockito.mock(BitmapTransformation.class);
        Mockito.doAnswer(new Util.WriteDigest("bitmapTransformation")).when(bitmapTransformation).updateDiskCacheKey(ArgumentMatchers.any(MessageDigest.class));
        Mockito.doAnswer(new Util.WriteDigest("otherBitmapTransformation")).when(otherBitmapTransformation).updateDiskCacheKey(ArgumentMatchers.any(MessageDigest.class));
        keyTester.addEquivalenceGroup(transformation, /* isRequired= */
        new DrawableTransformation(bitmapTransformation, true), /* isRequired= */
        new DrawableTransformation(bitmapTransformation, false)).addEquivalenceGroup(bitmapTransformation).addEquivalenceGroup(otherBitmapTransformation).addEquivalenceGroup(/* isRequired= */
        new DrawableTransformation(otherBitmapTransformation, true), /* isRequired= */
        new DrawableTransformation(otherBitmapTransformation, false)).addRegressionTest(/* isRequired= */
        new DrawableTransformation(bitmapTransformation, true), "eddf60c557a6315a489b8a3a19b12439a90381256289fbe9a503afa726230bd9").addRegressionTest(/* isRequired= */
        new DrawableTransformation(otherBitmapTransformation, false), "40931536ed0ec97c39d4be10c44f5b69a86030ec575317f5a0f17e15a0ea9be8").test();
    }

    private static final class ReturnGivenResource implements Answer<Resource<Bitmap>> {
        @Override
        public Resource<Bitmap> answer(InvocationOnMock invocationOnMock) throws Throwable {
            @SuppressWarnings("unchecked")
            Resource<Bitmap> input = ((Resource<Bitmap>) (invocationOnMock.getArguments()[1]));
            return input;
        }
    }
}

