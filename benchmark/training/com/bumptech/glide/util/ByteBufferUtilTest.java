package com.bumptech.glide.util;


import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class ByteBufferUtilTest {
    private static final int BUFFER_SIZE = 16384;

    @Test
    public void testFromStream_small() throws IOException {
        testFromStream(4);
    }

    @Test
    public void testFromStream_empty() throws IOException {
        testFromStream(0);
    }

    @Test
    public void testFromStream_bufferAndAHalf() throws IOException {
        testFromStream(((ByteBufferUtilTest.BUFFER_SIZE) + ((ByteBufferUtilTest.BUFFER_SIZE) / 2)));
    }

    @Test
    public void testFromStream_massive() throws IOException {
        testFromStream(((12 * (ByteBufferUtilTest.BUFFER_SIZE)) + 12345));
    }
}

