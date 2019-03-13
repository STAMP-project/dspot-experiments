package com.bumptech.glide.load.model;


import android.net.Uri;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.util.Preconditions;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Tests for the {@link UriLoader} class.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class UriLoaderTest {
    // Not a magic number, just arbitrary non zero.
    private static final int IMAGE_SIDE = 120;

    @Mock
    private DataFetcher<Object> localUriFetcher;

    @Mock
    private UriLoader.LocalUriFetcherFactory<Object> factory;

    private UriLoader<Object> loader;

    private Options options;

    @Test
    public void testHandlesFileUris() throws IOException {
        Uri fileUri = Uri.fromFile(new File("f"));
        Mockito.when(factory.build(ArgumentMatchers.eq(fileUri))).thenReturn(localUriFetcher);
        Assert.assertTrue(loader.handles(fileUri));
        Assert.assertEquals(localUriFetcher, Preconditions.checkNotNull(loader.buildLoadData(fileUri, UriLoaderTest.IMAGE_SIDE, UriLoaderTest.IMAGE_SIDE, options)).fetcher);
    }

    @Test
    public void testHandlesResourceUris() throws IOException {
        Uri resourceUri = Uri.parse("android.resource://com.bumptech.glide.tests/raw/ic_launcher");
        Mockito.when(factory.build(ArgumentMatchers.eq(resourceUri))).thenReturn(localUriFetcher);
        Assert.assertTrue(loader.handles(resourceUri));
        Assert.assertEquals(localUriFetcher, Preconditions.checkNotNull(loader.buildLoadData(resourceUri, UriLoaderTest.IMAGE_SIDE, UriLoaderTest.IMAGE_SIDE, options)).fetcher);
    }

    @Test
    public void testHandlesContentUris() {
        Uri contentUri = Uri.parse("content://com.bumptech.glide");
        Mockito.when(factory.build(ArgumentMatchers.eq(contentUri))).thenReturn(localUriFetcher);
        Assert.assertTrue(loader.handles(contentUri));
        Assert.assertEquals(localUriFetcher, Preconditions.checkNotNull(loader.buildLoadData(contentUri, UriLoaderTest.IMAGE_SIDE, UriLoaderTest.IMAGE_SIDE, options)).fetcher);
    }
}

