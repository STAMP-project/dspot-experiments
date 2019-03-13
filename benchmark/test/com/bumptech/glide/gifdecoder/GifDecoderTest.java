package com.bumptech.glide.gifdecoder;


import GifDecoder.STATUS_OK;
import GifDecoder.TOTAL_ITERATION_COUNT_FOREVER;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import com.bumptech.glide.testutil.TestUtil;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowBitmap;

import static GifHeader.NETSCAPE_LOOP_COUNT_DOES_NOT_EXIST;
import static GifHeader.NETSCAPE_LOOP_COUNT_FOREVER;


/**
 * Tests for {@link com.bumptech.glide.gifdecoder.GifDecoder}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class GifDecoderTest {
    private GifDecoderTest.MockProvider provider;

    @Test
    public void testCorrectPixelsDecoded() throws IOException {
        byte[] data = TestUtil.resourceToBytes(getClass(), "white_black_row.gif");
        GifHeaderParser headerParser = new GifHeaderParser();
        headerParser.setData(data);
        GifHeader header = headerParser.parseHeader();
        GifDecoder decoder = new StandardGifDecoder(provider);
        decoder.setData(header, data);
        decoder.advance();
        Bitmap bitmap = decoder.getNextFrame();
        Assert.assertNotNull(bitmap);
        Assert.assertEquals(bitmap.getPixel(2, 0), bitmap.getPixel(0, 0));
        Assert.assertEquals(bitmap.getPixel(3, 0), bitmap.getPixel(1, 0));
    }

    @Test
    public void testCanDecodeFramesFromTestGif() throws IOException {
        byte[] data = TestUtil.resourceToBytes(getClass(), "partial_gif_decode.gif");
        GifHeaderParser headerParser = new GifHeaderParser();
        headerParser.setData(data);
        GifHeader header = headerParser.parseHeader();
        GifDecoder decoder = new StandardGifDecoder(provider);
        decoder.setData(header, data);
        decoder.advance();
        Bitmap bitmap = decoder.getNextFrame();
        Assert.assertNotNull(bitmap);
        Assert.assertEquals(STATUS_OK, decoder.getStatus());
    }

    @Test
    public void testFrameIndexStartsAtNegativeOne() {
        GifHeader gifheader = new GifHeader();
        gifheader.frameCount = 4;
        byte[] data = new byte[0];
        GifDecoder decoder = new StandardGifDecoder(provider);
        decoder.setData(gifheader, data);
        Assert.assertEquals((-1), decoder.getCurrentFrameIndex());
    }

    @Test
    public void testTotalIterationCountIsOneIfNetscapeLoopCountDoesntExist() {
        GifHeader gifheader = new GifHeader();
        gifheader.loopCount = NETSCAPE_LOOP_COUNT_DOES_NOT_EXIST;
        byte[] data = new byte[0];
        GifDecoder decoder = new StandardGifDecoder(provider);
        decoder.setData(gifheader, data);
        Assert.assertEquals(1, decoder.getTotalIterationCount());
    }

    @Test
    public void testTotalIterationCountIsForeverIfNetscapeLoopCountIsForever() {
        GifHeader gifheader = new GifHeader();
        gifheader.loopCount = NETSCAPE_LOOP_COUNT_FOREVER;
        byte[] data = new byte[0];
        GifDecoder decoder = new StandardGifDecoder(provider);
        decoder.setData(gifheader, data);
        Assert.assertEquals(TOTAL_ITERATION_COUNT_FOREVER, decoder.getTotalIterationCount());
    }

    @Test
    public void testTotalIterationCountIsTwoIfNetscapeLoopCountIsOne() {
        GifHeader gifheader = new GifHeader();
        gifheader.loopCount = 1;
        byte[] data = new byte[0];
        GifDecoder decoder = new StandardGifDecoder(provider);
        decoder.setData(gifheader, data);
        Assert.assertEquals(2, decoder.getTotalIterationCount());
    }

    @Test
    public void testAdvanceIncrementsFrameIndex() {
        GifHeader gifheader = new GifHeader();
        gifheader.frameCount = 4;
        byte[] data = new byte[0];
        GifDecoder decoder = new StandardGifDecoder(provider);
        decoder.setData(gifheader, data);
        decoder.advance();
        Assert.assertEquals(0, decoder.getCurrentFrameIndex());
    }

    @Test
    public void testAdvanceWrapsIndexBackToZero() {
        GifHeader gifheader = new GifHeader();
        gifheader.frameCount = 2;
        byte[] data = new byte[0];
        GifDecoder decoder = new StandardGifDecoder(provider);
        decoder.setData(gifheader, data);
        decoder.advance();
        decoder.advance();
        decoder.advance();
        Assert.assertEquals(0, decoder.getCurrentFrameIndex());
    }

    @Test
    public void testSettingDataResetsFramePointer() {
        GifHeader gifheader = new GifHeader();
        gifheader.frameCount = 4;
        byte[] data = new byte[0];
        GifDecoder decoder = new StandardGifDecoder(provider);
        decoder.setData(gifheader, data);
        decoder.advance();
        decoder.advance();
        Assert.assertEquals(1, decoder.getCurrentFrameIndex());
        decoder.setData(gifheader, data);
        Assert.assertEquals((-1), decoder.getCurrentFrameIndex());
    }

    @Test
    @Config(shadows = { GifDecoderTest.CustomShadowBitmap.class })
    public void testFirstFrameMustClearBeforeDrawingWhenLastFrameIsDisposalBackground() throws IOException {
        byte[] data = TestUtil.resourceToBytes(getClass(), "transparent_disposal_background.gif");
        GifHeaderParser headerParser = new GifHeaderParser();
        headerParser.setData(data);
        GifHeader header = headerParser.parseHeader();
        GifDecoder decoder = new StandardGifDecoder(provider);
        decoder.setData(header, data);
        decoder.advance();
        Bitmap firstFrame = decoder.getNextFrame();
        decoder.advance();
        decoder.getNextFrame();
        decoder.advance();
        Bitmap firstFrameTwice = decoder.getNextFrame();
        Assert.assertTrue(Arrays.equals(((GifDecoderTest.CustomShadowBitmap) (shadowOf(firstFrame))).getPixels(), ((GifDecoderTest.CustomShadowBitmap) (shadowOf(firstFrameTwice))).getPixels()));
    }

    @Test
    @Config(shadows = { GifDecoderTest.CustomShadowBitmap.class })
    public void testFirstFrameMustClearBeforeDrawingWhenLastFrameIsDisposalNone() throws IOException {
        byte[] data = TestUtil.resourceToBytes(getClass(), "transparent_disposal_none.gif");
        GifHeaderParser headerParser = new GifHeaderParser();
        headerParser.setData(data);
        GifHeader header = headerParser.parseHeader();
        GifDecoder decoder = new StandardGifDecoder(provider);
        decoder.setData(header, data);
        decoder.advance();
        Bitmap firstFrame = decoder.getNextFrame();
        decoder.advance();
        decoder.getNextFrame();
        decoder.advance();
        Bitmap firstFrameTwice = decoder.getNextFrame();
        Assert.assertTrue(Arrays.equals(((GifDecoderTest.CustomShadowBitmap) (shadowOf(firstFrame))).getPixels(), ((GifDecoderTest.CustomShadowBitmap) (shadowOf(firstFrameTwice))).getPixels()));
    }

    /**
     * Preserve generated bitmap data for checking.
     */
    @Implements(Bitmap.class)
    public static class CustomShadowBitmap extends ShadowBitmap {
        private int[] pixels;

        @Implementation
        public void setPixels(int[] pixels, int offset, int stride, int x, int y, int width, int height) {
            this.pixels = new int[pixels.length];
            System.arraycopy(pixels, 0, this.pixels, 0, this.pixels.length);
        }

        public int[] getPixels() {
            return pixels;
        }
    }

    private static class MockProvider implements GifDecoder.BitmapProvider {
        @NonNull
        @Override
        public Bitmap obtain(int width, int height, Bitmap.Config config) {
            Bitmap result = Bitmap.createBitmap(width, height, config);
            Shadows.shadowOf(result).setMutable(true);
            return result;
        }

        @Override
        public void release(@NonNull
        Bitmap bitmap) {
            // Do nothing.
        }

        @NonNull
        @Override
        public byte[] obtainByteArray(int size) {
            return new byte[size];
        }

        @Override
        public void release(@NonNull
        byte[] bytes) {
            // Do nothing.
        }

        @NonNull
        @Override
        public int[] obtainIntArray(int size) {
            return new int[size];
        }

        @Override
        public void release(@NonNull
        int[] array) {
            // Do Nothing
        }
    }
}

