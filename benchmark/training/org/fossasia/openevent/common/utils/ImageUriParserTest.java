package org.fossasia.openevent.common.utils;


import org.junit.Assert;
import org.junit.Test;


public class ImageUriParserTest {
    @Test
    public void shouldReturnNullOnNullOrEmpty() throws Exception {
        Assert.assertNull(Utils.parseImageUri(""));
        Assert.assertNull(Utils.parseImageUri(null));
    }

    @Test
    public void shouldReturnUrl() throws Exception {
        String url = "http://website.ext/resource.ext";
        String https_url = "http://website.ext/resource.ext";
        Assert.assertEquals(url, Utils.parseImageUri(url));
        Assert.assertEquals(https_url, Utils.parseImageUri(https_url));
    }

    @Test
    public void shouldReturnAssetUri() throws Exception {
        String uri = "/images/speakers/JohnWick.jpg";
        String sponsors_uri = "/images/sponsors/Google_1.png";
        Assert.assertEquals("file:///android_asset/images/speakers/JohnWick.jpg", Utils.parseImageUri(uri));
        Assert.assertEquals("file:///android_asset/images/sponsors/Google_1.png", Utils.parseImageUri(sponsors_uri));
    }

    @Test
    public void shouldReturnNullOnMalformedUri() throws Exception {
        String uri = "a_different/format.pop";
        Assert.assertNull(Utils.parseImageUri(uri));
    }
}

