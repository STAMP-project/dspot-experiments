package com.bumptech.glide.load.model;


import android.content.res.AssetManager;
import android.net.Uri;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.util.Preconditions;
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
public class AssetUriLoaderTest {
    private static final int IMAGE_SIDE = 10;

    @Mock
    private AssetUriLoader.AssetFetcherFactory<Object> factory;

    @Mock
    private DataFetcher<Object> fetcher;

    private AssetUriLoader<Object> loader;

    @Test
    public void testHandlesAssetUris() {
        Uri assetUri = Uri.parse("file:///android_asset/assetName");
        Mockito.when(factory.buildFetcher(ArgumentMatchers.any(AssetManager.class), ArgumentMatchers.eq("assetName"))).thenReturn(fetcher);
        Assert.assertTrue(loader.handles(assetUri));
        Assert.assertEquals(fetcher, Preconditions.checkNotNull(loader.buildLoadData(assetUri, AssetUriLoaderTest.IMAGE_SIDE, AssetUriLoaderTest.IMAGE_SIDE, new Options())).fetcher);
    }
}

