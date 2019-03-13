package com.bumptech.glide.load.data;


import Priority.NORMAL;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.ParcelFileDescriptor;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class FileDescriptorAssetPathFetcherTest {
    @Mock
    private AssetManager assetManager;

    @Mock
    private AssetFileDescriptor assetFileDescriptor;

    @Mock
    private DataFetcher.DataCallback<ParcelFileDescriptor> callback;

    private FileDescriptorAssetPathFetcher fetcher;

    private ParcelFileDescriptor expected;

    @Test
    public void testOpensInputStreamForPathWithAssetManager() throws Exception {
        fetcher.loadData(NORMAL, callback);
        Mockito.verify(callback).onDataReady(ArgumentMatchers.eq(expected));
    }

    @Test
    public void testClosesOpenedInputStreamOnCleanup() throws Exception {
        fetcher.loadData(NORMAL, callback);
        fetcher.cleanup();
        Mockito.verify(expected).close();
    }

    @Test
    public void testDoesNothingOnCleanupIfNoDataLoaded() throws IOException {
        fetcher.cleanup();
        Mockito.verify(expected, Mockito.never()).close();
    }

    @Test
    public void testDoesNothingOnCancel() throws Exception {
        fetcher.loadData(NORMAL, callback);
        fetcher.cancel();
        Mockito.verify(expected, Mockito.never()).close();
    }
}

