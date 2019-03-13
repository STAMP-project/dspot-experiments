package com.bumptech.glide.load.resource.transcode;


import Bitmap.Config.ARGB_8888;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.tests.Util;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class BitmapDrawableTranscoderTest {
    private BitmapDrawableTranscoder transcoder;

    @Test
    public void testReturnsBitmapDrawableResourceContainingGivenBitmap() {
        Bitmap expected = Bitmap.createBitmap(100, 100, ARGB_8888);
        Resource<Bitmap> resource = Util.mockResource();
        Mockito.when(resource.get()).thenReturn(expected);
        Resource<BitmapDrawable> transcoded = transcoder.transcode(resource, new Options());
        Assert.assertEquals(expected, transcoded.get().getBitmap());
    }
}

