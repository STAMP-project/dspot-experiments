package com.bumptech.glide.load.resource.gif;


import GifOptions.DISABLE_ANIMATION;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class StreamGifDecoderTest {
    private static final byte[] GIF_HEADER = new byte[]{ 71, 73, 70 };

    @Mock
    private ResourceDecoder<ByteBuffer, GifDrawable> byteBufferDecoder;

    private StreamGifDecoder decoder;

    private Options options;

    @Test
    public void testDoesNotHandleStreamIfEnabledButNotAGif() throws IOException {
        assertThat(decoder.handles(new ByteArrayInputStream(new byte[0]), options)).isFalse();
    }

    @Test
    public void testHandlesStreamIfContainsGifHeaderAndDisabledIsNotSet() throws IOException {
        assertThat(decoder.handles(new ByteArrayInputStream(StreamGifDecoderTest.GIF_HEADER), options)).isTrue();
    }

    @Test
    public void testHandlesStreamIfContainsGifHeaderAndDisabledIsFalse() throws IOException {
        options.set(DISABLE_ANIMATION, false);
        assertThat(decoder.handles(new ByteArrayInputStream(StreamGifDecoderTest.GIF_HEADER), options)).isTrue();
    }

    @Test
    public void testDoesNotHandleStreamIfDisabled() throws IOException {
        options.set(DISABLE_ANIMATION, true);
        assertThat(decoder.handles(new ByteArrayInputStream(StreamGifDecoderTest.GIF_HEADER), options)).isFalse();
    }
}

