package com.bumptech.glide.load.resource.transcode;


import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class GifDrawableBytesTranscoderTest {
    private GifDrawableBytesTranscoder transcoder;

    private GifDrawable gifDrawable;

    private Resource<GifDrawable> resource;

    @Test
    public void testReturnsBytesOfGivenGifDrawable() {
        for (String fakeData : new String[]{ "test", "1235asfklaw3", "@$@#" }) {
            ByteBuffer expected = ByteBuffer.wrap(fakeData.getBytes(Charset.defaultCharset()));
            Mockito.when(gifDrawable.getBuffer()).thenReturn(expected);
            Resource<byte[]> transcoded = transcoder.transcode(resource, new Options());
            Assert.assertArrayEquals(expected.array(), transcoded.get());
        }
    }
}

