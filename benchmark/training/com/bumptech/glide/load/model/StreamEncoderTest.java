package com.bumptech.glide.load.model;


import com.bumptech.glide.load.Options;
import com.bumptech.glide.util.ByteBufferUtil;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class StreamEncoderTest {
    private StreamEncoder encoder;

    private File file;

    @Test
    public void testWritesDataFromInputStreamToOutputStream() throws IOException {
        String fakeData = "SomeRandomFakeData";
        ByteArrayInputStream is = new ByteArrayInputStream(fakeData.getBytes("UTF-8"));
        encoder.encode(is, file, new Options());
        byte[] data = ByteBufferUtil.toBytes(ByteBufferUtil.fromFile(file));
        Assert.assertEquals(fakeData, new String(data, "UTF-8"));
    }
}

