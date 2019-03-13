package com.bumptech.glide.load.resource.gif;


import ByteBufferGifDecoder.GifDecoderFactory;
import ByteBufferGifDecoder.GifHeaderParserPool;
import GifDecoder.STATUS_FORMAT_ERROR;
import GifDecoder.STATUS_OK;
import GifDecoder.STATUS_OPEN_ERROR;
import GifOptions.DISABLE_ANIMATION;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.gifdecoder.GifHeader;
import com.bumptech.glide.gifdecoder.GifHeaderParser;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.tests.GlideShadowLooper;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18, shadows = GlideShadowLooper.class)
public class ByteBufferGifDecoderTest {
    private static final byte[] GIF_HEADER = new byte[]{ 71, 73, 70 };

    private static final int ARRAY_POOL_SIZE_BYTES = (4 * 1024) * 1024;

    private ByteBufferGifDecoder decoder;

    private GifHeader gifHeader;

    private Options options;

    @Mock
    private BitmapPool bitmapPool;

    @Mock
    private GifHeaderParser parser;

    @Mock
    private GifDecoder gifDecoder;

    @Mock
    private GifHeaderParserPool parserPool;

    @Mock
    private GifDecoderFactory decoderFactory;

    @Test
    public void testDoesNotHandleStreamIfEnabledButNotAGif() throws IOException {
        assertThat(decoder.handles(ByteBuffer.allocate(0), options)).isFalse();
    }

    @Test
    public void testHandlesStreamIfContainsGifHeaderAndDisabledIsNotSet() throws IOException {
        assertThat(decoder.handles(ByteBuffer.wrap(ByteBufferGifDecoderTest.GIF_HEADER), options)).isTrue();
    }

    @Test
    public void testHandlesStreamIfContainsGifHeaderAndDisabledIsFalse() throws IOException {
        options.set(DISABLE_ANIMATION, false);
        assertThat(decoder.handles(ByteBuffer.wrap(ByteBufferGifDecoderTest.GIF_HEADER), options)).isTrue();
    }

    @Test
    public void testDoesNotHandleStreamIfDisabled() throws IOException {
        options.set(DISABLE_ANIMATION, true);
        assertThat(decoder.handles(ByteBuffer.wrap(ByteBufferGifDecoderTest.GIF_HEADER), options)).isFalse();
    }

    @Test
    public void testReturnsNullIfParsedHeaderHasZeroFrames() throws IOException {
        Mockito.when(gifHeader.getNumFrames()).thenReturn(0);
        Assert.assertNull(decoder.decode(ByteBuffer.allocate(10), 100, 100, options));
    }

    @Test
    public void testReturnsNullIfParsedHeaderHasFormatError() {
        Mockito.when(gifHeader.getStatus()).thenReturn(STATUS_FORMAT_ERROR);
        Assert.assertNull(decoder.decode(ByteBuffer.allocate(10), 100, 100, options));
    }

    @Test
    public void testReturnsNullIfParsedHeaderHasOpenError() {
        Mockito.when(gifHeader.getStatus()).thenReturn(STATUS_OPEN_ERROR);
        Assert.assertNull(decoder.decode(ByteBuffer.allocate(10), 100, 100, options));
    }

    @Test
    public void testReturnsParserToPool() throws IOException {
        decoder.decode(ByteBuffer.allocate(10), 100, 100, options);
        Mockito.verify(parserPool).release(ArgumentMatchers.eq(parser));
    }

    @Test
    public void testReturnsParserToPoolWhenParserThrows() {
        Mockito.when(parser.parseHeader()).thenThrow(new RuntimeException("Test"));
        try {
            decoder.decode(ByteBuffer.allocate(10), 100, 100, options);
            Assert.fail("Failed to receive expected exception");
        } catch (RuntimeException e) {
            // Expected.
        }
        Mockito.verify(parserPool).release(ArgumentMatchers.eq(parser));
    }

    @Test
    public void testReturnsNullIfGifDecoderFailsToDecodeFirstFrame() {
        Mockito.when(gifHeader.getNumFrames()).thenReturn(1);
        Mockito.when(gifHeader.getStatus()).thenReturn(STATUS_OK);
        Mockito.when(gifDecoder.getNextFrame()).thenReturn(null);
        Assert.assertNull(decoder.decode(ByteBuffer.allocate(10), 100, 100, options));
    }
}

