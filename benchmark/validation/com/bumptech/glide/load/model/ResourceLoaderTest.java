package com.bumptech.glide.load.model;


import android.R.drawable;
import android.net.Uri;
import com.bumptech.glide.load.Key;
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


/**
 * Tests for the {@link com.bumptech.glide.load.model.ResourceLoader} class.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class ResourceLoaderTest {
    @Mock
    private ModelLoader<Uri, Object> uriLoader;

    @Mock
    private DataFetcher<Object> fetcher;

    @Mock
    private Key key;

    private Options options;

    private ResourceLoader<Object> loader;

    @Test
    public void testCanHandleId() {
        int id = drawable.star_off;
        Uri contentUri = Uri.parse("android.resource://android/drawable/star_off");
        Mockito.when(uriLoader.buildLoadData(ArgumentMatchers.eq(contentUri), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Options.class))).thenReturn(new ModelLoader.LoadData<>(key, fetcher));
        Assert.assertTrue(loader.handles(id));
        Assert.assertEquals(fetcher, Preconditions.checkNotNull(loader.buildLoadData(id, 100, 100, new Options())).fetcher);
    }

    @Test
    public void testDoesNotThrowOnInvalidOrMissingId() {
        assertThat(loader.buildLoadData(1234, 0, 0, options)).isNull();
        Mockito.verify(uriLoader, Mockito.never()).buildLoadData(ArgumentMatchers.any(Uri.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Options.class));
    }
}

