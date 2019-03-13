package com.liskovsoft.smartyoutubetv;


import com.liskovsoft.smartyoutubetv.misc.oldyoutubeinfoparser.VideoInfoParser;
import com.liskovsoft.smartyoutubetv.misc.oldyoutubeinfoparser.YouTubeVideoInfoParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class VideoInfoParserTest {
    private InputStream mVideoInfo;

    private InputStream mHDVideoLink;

    @Test
    public void testGetHDVideoLink() throws IOException {
        VideoInfoParser videoInfoParser = new YouTubeVideoInfoParser(mVideoInfo);
        String result = videoInfoParser.getHDVideoLink();
        Assert.assertTrue(IOUtils.contentEquals(new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8)), mHDVideoLink));
    }
}

