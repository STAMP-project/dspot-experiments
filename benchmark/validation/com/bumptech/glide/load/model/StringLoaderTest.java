package com.bumptech.glide.load.model;


import RuntimeEnvironment.application;
import android.net.Uri;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.tests.Util;
import com.bumptech.glide.util.Preconditions;
import java.io.File;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Tests for the {@link com.bumptech.glide.load.model.StringLoader} class.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class StringLoaderTest {
    // Not a magic number, just an arbitrary non zero value.
    private static final int IMAGE_SIDE = 100;

    @Mock
    private ModelLoader<Uri, Object> uriLoader;

    @Mock
    private DataFetcher<Object> fetcher;

    @Mock
    private Key key;

    private StringLoader<Object> loader;

    private Options options;

    @Test
    public void testHandlesPaths() {
        // TODO fix drive letter parsing somehow
        Assume.assumeTrue("it will fail with schema being the drive letter (C:\\... -> C)", (!(Util.isWindows())));
        File f = application.getCacheDir();
        Uri expected = Uri.fromFile(f);
        Mockito.when(uriLoader.buildLoadData(ArgumentMatchers.eq(expected), ArgumentMatchers.eq(StringLoaderTest.IMAGE_SIDE), ArgumentMatchers.eq(StringLoaderTest.IMAGE_SIDE), ArgumentMatchers.eq(options))).thenReturn(new ModelLoader.LoadData<>(key, fetcher));
        Assert.assertTrue(loader.handles(f.getAbsolutePath()));
        Assert.assertEquals(fetcher, Preconditions.checkNotNull(loader.buildLoadData(f.getAbsolutePath(), StringLoaderTest.IMAGE_SIDE, StringLoaderTest.IMAGE_SIDE, options)).fetcher);
    }

    @Test
    public void testCanHandleComplexFilePaths() {
        String testPath = "/storage/emulated/0/DCIM/Camera/IMG_20140520_100001:nopm:.jpg,mimeType=image/jpeg," + "2448x3264,orientation=0,date=Tue";
        Uri expected = Uri.fromFile(new File(testPath));
        Mockito.when(uriLoader.buildLoadData(ArgumentMatchers.eq(expected), ArgumentMatchers.eq(StringLoaderTest.IMAGE_SIDE), ArgumentMatchers.eq(StringLoaderTest.IMAGE_SIDE), ArgumentMatchers.eq(options))).thenReturn(new ModelLoader.LoadData<>(key, fetcher));
        Assert.assertTrue(loader.handles(testPath));
        Assert.assertEquals(fetcher, Preconditions.checkNotNull(loader.buildLoadData(testPath, StringLoaderTest.IMAGE_SIDE, StringLoaderTest.IMAGE_SIDE, options)).fetcher);
    }

    @Test
    public void testHandlesFileUris() {
        File f = application.getCacheDir();
        Uri expected = Uri.fromFile(f);
        Mockito.when(uriLoader.buildLoadData(ArgumentMatchers.eq(expected), ArgumentMatchers.eq(StringLoaderTest.IMAGE_SIDE), ArgumentMatchers.eq(StringLoaderTest.IMAGE_SIDE), ArgumentMatchers.eq(options))).thenReturn(new ModelLoader.LoadData<>(key, fetcher));
        Assert.assertTrue(loader.handles(f.getAbsolutePath()));
        Assert.assertEquals(fetcher, Preconditions.checkNotNull(loader.buildLoadData(expected.toString(), StringLoaderTest.IMAGE_SIDE, StringLoaderTest.IMAGE_SIDE, options)).fetcher);
    }

    @Test
    public void testHandlesResourceUris() {
        Uri resourceUri = Uri.parse("android.resource://com.bumptech.glide.tests/raw/ic_launcher");
        Mockito.when(uriLoader.buildLoadData(ArgumentMatchers.eq(resourceUri), ArgumentMatchers.eq(StringLoaderTest.IMAGE_SIDE), ArgumentMatchers.eq(StringLoaderTest.IMAGE_SIDE), ArgumentMatchers.eq(options))).thenReturn(new ModelLoader.LoadData<>(key, fetcher));
        Assert.assertTrue(loader.handles(resourceUri.toString()));
        Assert.assertEquals(fetcher, Preconditions.checkNotNull(loader.buildLoadData(resourceUri.toString(), StringLoaderTest.IMAGE_SIDE, StringLoaderTest.IMAGE_SIDE, options)).fetcher);
    }

    @Test
    public void testHandlesHttp() {
        String url = "http://www.google.com";
        Uri expected = Uri.parse(url);
        Mockito.when(uriLoader.buildLoadData(ArgumentMatchers.eq(expected), ArgumentMatchers.eq(StringLoaderTest.IMAGE_SIDE), ArgumentMatchers.eq(StringLoaderTest.IMAGE_SIDE), ArgumentMatchers.eq(options))).thenReturn(new ModelLoader.LoadData<>(key, fetcher));
        Assert.assertTrue(loader.handles(url));
        Assert.assertEquals(fetcher, Preconditions.checkNotNull(loader.buildLoadData(url, StringLoaderTest.IMAGE_SIDE, StringLoaderTest.IMAGE_SIDE, options)).fetcher);
    }

    @Test
    public void testHandlesHttps() {
        String url = "https://www.google.com";
        Uri expected = Uri.parse(url);
        Mockito.when(uriLoader.buildLoadData(ArgumentMatchers.eq(expected), ArgumentMatchers.eq(StringLoaderTest.IMAGE_SIDE), ArgumentMatchers.eq(StringLoaderTest.IMAGE_SIDE), ArgumentMatchers.eq(options))).thenReturn(new ModelLoader.LoadData<>(key, fetcher));
        Assert.assertTrue(loader.handles(url));
        Assert.assertEquals(fetcher, Preconditions.checkNotNull(loader.buildLoadData(url, StringLoaderTest.IMAGE_SIDE, StringLoaderTest.IMAGE_SIDE, options)).fetcher);
    }

    @Test
    public void testHandlesContent() {
        String content = "content://com.bumptech.glide";
        Uri expected = Uri.parse(content);
        Mockito.when(uriLoader.buildLoadData(ArgumentMatchers.eq(expected), ArgumentMatchers.eq(StringLoaderTest.IMAGE_SIDE), ArgumentMatchers.eq(StringLoaderTest.IMAGE_SIDE), ArgumentMatchers.eq(options))).thenReturn(new ModelLoader.LoadData<>(key, fetcher));
        Assert.assertTrue(loader.handles(content));
        Assert.assertEquals(fetcher, Preconditions.checkNotNull(loader.buildLoadData(content, StringLoaderTest.IMAGE_SIDE, StringLoaderTest.IMAGE_SIDE, options)).fetcher);
    }

    @Test
    public void testGetResourceFetcher_withEmptyString_returnsNull() {
        assertThat(loader.buildLoadData("", StringLoaderTest.IMAGE_SIDE, StringLoaderTest.IMAGE_SIDE, options)).isNull();
        assertThat(loader.buildLoadData("    ", StringLoaderTest.IMAGE_SIDE, StringLoaderTest.IMAGE_SIDE, options)).isNull();
        assertThat(loader.buildLoadData("  \n", StringLoaderTest.IMAGE_SIDE, StringLoaderTest.IMAGE_SIDE, options)).isNull();
    }
}

