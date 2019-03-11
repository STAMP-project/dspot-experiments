package net.glxn.qrgen.core.scheme;


import org.junit.Assert;
import org.junit.Test;

import static YouTube.YOUTUBE;


public class YouTubeTest {
    private static final String VIDEO = "w3jLJU7DT5E";

    @Test
    public void testParse() {
        Assert.assertTrue(YouTube.parse((((YOUTUBE) + ":") + (YouTubeTest.VIDEO))).getVideoId().equals(YouTubeTest.VIDEO));
    }

    @Test
    public void testToString() {
        Assert.assertTrue(YouTube.parse((((YOUTUBE) + ":") + (YouTubeTest.VIDEO))).toString().equals((((YOUTUBE) + ":") + (YouTubeTest.VIDEO))));
    }
}

