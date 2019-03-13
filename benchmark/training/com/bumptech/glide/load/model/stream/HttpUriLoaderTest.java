package com.bumptech.glide.load.model.stream;


import android.net.Uri;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import java.io.InputStream;
import java.net.MalformedURLException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class HttpUriLoaderTest {
    private static final int IMAGE_SIDE = 100;

    private static final Options OPTIONS = new Options();

    @Mock
    private ModelLoader<GlideUrl, InputStream> urlLoader;

    private HttpUriLoader loader;

    @Test
    public void testHandlesHttpUris() throws MalformedURLException {
        Uri httpUri = Uri.parse("http://www.google.com");
        loader.buildLoadData(httpUri, HttpUriLoaderTest.IMAGE_SIDE, HttpUriLoaderTest.IMAGE_SIDE, HttpUriLoaderTest.OPTIONS);
        Assert.assertTrue(loader.handles(httpUri));
        Mockito.verify(urlLoader).buildLoadData(ArgumentMatchers.eq(new GlideUrl(httpUri.toString())), ArgumentMatchers.eq(HttpUriLoaderTest.IMAGE_SIDE), ArgumentMatchers.eq(HttpUriLoaderTest.IMAGE_SIDE), ArgumentMatchers.eq(HttpUriLoaderTest.OPTIONS));
    }

    @Test
    public void testHandlesHttpsUris() throws MalformedURLException {
        Uri httpsUri = Uri.parse("https://www.google.com");
        loader.buildLoadData(httpsUri, HttpUriLoaderTest.IMAGE_SIDE, HttpUriLoaderTest.IMAGE_SIDE, HttpUriLoaderTest.OPTIONS);
        Assert.assertTrue(loader.handles(httpsUri));
        Mockito.verify(urlLoader).buildLoadData(ArgumentMatchers.eq(new GlideUrl(httpsUri.toString())), ArgumentMatchers.eq(HttpUriLoaderTest.IMAGE_SIDE), ArgumentMatchers.eq(HttpUriLoaderTest.IMAGE_SIDE), ArgumentMatchers.eq(HttpUriLoaderTest.OPTIONS));
    }

    // Test for https://github.com/bumptech/glide/issues/71.
    @Test
    public void testHandlesMostlyInvalidHttpUris() {
        Uri mostlyInvalidHttpUri = Uri.parse("http://myserver_url.com:80http://myserver_url.com/webapp/images/no_image.png?size=100");
        Assert.assertTrue(loader.handles(mostlyInvalidHttpUri));
        loader.buildLoadData(mostlyInvalidHttpUri, HttpUriLoaderTest.IMAGE_SIDE, HttpUriLoaderTest.IMAGE_SIDE, HttpUriLoaderTest.OPTIONS);
        Mockito.verify(urlLoader).buildLoadData(ArgumentMatchers.eq(new GlideUrl(mostlyInvalidHttpUri.toString())), ArgumentMatchers.eq(HttpUriLoaderTest.IMAGE_SIDE), ArgumentMatchers.eq(HttpUriLoaderTest.IMAGE_SIDE), ArgumentMatchers.eq(HttpUriLoaderTest.OPTIONS));
    }
}

