package com.liskovsoft.smartyoutubetv;


import VideoFormat._1080p_;
import com.liskovsoft.smartyoutubetv.misc.oldyoutubeinfoparser.VideoFormat;
import com.liskovsoft.smartyoutubetv.misc.oldyoutubeinfoparser.VideoInfoBuilder;
import com.liskovsoft.smartyoutubetv.misc.oldyoutubeinfoparser.YouTubeVideoInfoBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class VideoInfoBuilderTest {
    private InputStream mOriginalInfo;

    private InputStream mFullHDRemovedInfo;

    private InputStream mFullHDInfo;

    @Test
    public void testFormatsRemoving() throws Exception {
        VideoInfoBuilder builder = new YouTubeVideoInfoBuilder(mOriginalInfo);
        // builder.removeFormat(248); // webm 1920x1080
        // builder.removeFormat(137); // mp4 1920x1080
        builder.removeFormat(_1080p_);
        InputStream result = builder.get();
        Assert.assertTrue(IOUtils.contentEquals(result, mFullHDRemovedInfo));
    }

    @Test
    public void testSelectFormat() throws IOException {
        VideoInfoBuilder builder = new YouTubeVideoInfoBuilder(mOriginalInfo);
        builder.selectFormat(_1080p_);
        InputStream result = builder.get();
        Assert.assertTrue(IOUtils.contentEquals(result, mFullHDInfo));
    }

    @Test
    public void testGetAllSupportedFormats() {
        Set<VideoFormat> testFormats = createTestFormats();
        VideoInfoBuilder builder = new YouTubeVideoInfoBuilder(mOriginalInfo);
        Set<VideoFormat> allSupportedFormats = builder.getSupportedFormats();
        testFormats.removeAll(allSupportedFormats);
        Assert.assertTrue(testFormats.isEmpty());
    }
}

