package com.bumptech.glide.load.resource.gif;


import Bitmap.Config.ARGB_4444;
import android.graphics.Bitmap;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.util.Preconditions;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class GifFrameResourceDecoderTest {
    private GifDecoder gifDecoder;

    private GifFrameResourceDecoder resourceDecoder;

    private Options options;

    @Test
    public void testReturnsFrameFromGifDecoder() throws IOException {
        Bitmap expected = Bitmap.createBitmap(100, 100, ARGB_4444);
        Mockito.when(gifDecoder.getNextFrame()).thenReturn(expected);
        Assert.assertEquals(expected, Preconditions.checkNotNull(resourceDecoder.decode(gifDecoder, 100, 100, options)).get());
    }

    @Test
    public void testReturnsNullIfGifDecoderReturnsNullFrame() {
        Mockito.when(gifDecoder.getNextFrame()).thenReturn(null);
        Assert.assertNull(resourceDecoder.decode(gifDecoder, 100, 100, options));
    }
}

