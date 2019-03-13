package com.bumptech.glide.load.data.mediastore;


import Priority.HIGH;
import Priority.LOW;
import android.net.Uri;
import com.bumptech.glide.load.data.DataFetcher;
import java.io.InputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class ThumbFetcherTest {
    @Mock
    private ThumbnailStreamOpener opener;

    @Mock
    private DataFetcher.DataCallback<InputStream> callback;

    @Mock
    private InputStream expected;

    private ThumbFetcher fetcher;

    private Uri uri;

    @Test
    public void testReturnsInputStreamFromThumbnailOpener() throws Exception {
        Mockito.when(opener.open(ArgumentMatchers.eq(uri))).thenReturn(expected);
        fetcher.loadData(LOW, callback);
        Mockito.verify(callback).onDataReady(ArgumentMatchers.isNotNull(InputStream.class));
    }

    @Test
    public void testClosesInputStreamFromThumbnailOpenerOnCleanup() throws Exception {
        Mockito.when(opener.open(ArgumentMatchers.eq(uri))).thenReturn(expected);
        fetcher.loadData(HIGH, callback);
        fetcher.cleanup();
        Mockito.verify(expected).close();
    }

    @Test
    public void testDoesNotThrowIfCleanupWithNullInputStream() {
        fetcher.cleanup();
    }
}

