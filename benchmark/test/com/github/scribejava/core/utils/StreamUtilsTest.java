package com.github.scribejava.core.utils;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;


public class StreamUtilsTest {
    private static final InputStream ALLWAYS_ERROR_INPUT_STREAM = new StreamUtilsTest.AllwaysErrorInputStream();

    private static class AllwaysErrorInputStream extends InputStream {
        @Override
        public int read() throws IOException {
            throw new IOException();
        }
    }

    @Test
    public void shouldCorrectlyDecodeAStream() throws IOException {
        final String value = "expected";
        final InputStream is = new ByteArrayInputStream(value.getBytes());
        final String decoded = StreamUtils.getStreamContents(is);
        Assert.assertEquals("expected", decoded);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailForNullParameter() throws IOException {
        StreamUtils.getStreamContents(null);
        Assert.fail("Must throw exception before getting here");
    }

    @Test(expected = IOException.class)
    public void shouldFailWithBrokenStream() throws IOException {
        // This object simulates problems with input stream.
        StreamUtils.getStreamContents(StreamUtilsTest.ALLWAYS_ERROR_INPUT_STREAM);
        Assert.fail("Must throw exception before getting here");
    }
}

