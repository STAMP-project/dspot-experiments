package com.bumptech.glide.load.resource.file;


import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.Preconditions;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class FileDecoderTest {
    private FileDecoder decoder;

    private Options options;

    @Test
    public void testReturnsGivenFileAsResource() throws IOException {
        File expected = new File("testFile");
        Resource<File> decoded = Preconditions.checkNotNull(decoder.decode(expected, 1, 1, options));
        Assert.assertEquals(expected, decoded.get());
    }
}

