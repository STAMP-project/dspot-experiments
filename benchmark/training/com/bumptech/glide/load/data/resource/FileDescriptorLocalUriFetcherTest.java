package com.bumptech.glide.load.data.resource;


import Priority.NORMAL;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.FileDescriptorLocalUriFetcher;
import com.bumptech.glide.tests.ContentResolverShadow;
import java.io.FileNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadow.api.Shadow;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18, shadows = { ContentResolverShadow.class })
public class FileDescriptorLocalUriFetcherTest {
    @Mock
    private DataFetcher.DataCallback<ParcelFileDescriptor> callback;

    @Test
    public void testLoadResource_returnsFileDescriptor() throws Exception {
        Context context = RuntimeEnvironment.application;
        Uri uri = Uri.parse("file://nothing");
        ContentResolver contentResolver = context.getContentResolver();
        ContentResolverShadow shadow = Shadow.extract(contentResolver);
        AssetFileDescriptor assetFileDescriptor = Mockito.mock(AssetFileDescriptor.class);
        ParcelFileDescriptor parcelFileDescriptor = Mockito.mock(ParcelFileDescriptor.class);
        Mockito.when(assetFileDescriptor.getParcelFileDescriptor()).thenReturn(parcelFileDescriptor);
        shadow.registerFileDescriptor(uri, assetFileDescriptor);
        FileDescriptorLocalUriFetcher fetcher = new FileDescriptorLocalUriFetcher(context.getContentResolver(), uri);
        fetcher.loadData(NORMAL, callback);
        Mockito.verify(callback).onDataReady(ArgumentMatchers.eq(parcelFileDescriptor));
    }

    @Test
    public void testLoadResource_withNullFileDescriptor_callsLoadFailed() {
        Context context = RuntimeEnvironment.application;
        Uri uri = Uri.parse("file://nothing");
        ContentResolver contentResolver = context.getContentResolver();
        ContentResolverShadow shadow = Shadow.extract(contentResolver);
        /* fileDescriptor */
        shadow.registerFileDescriptor(uri, null);
        FileDescriptorLocalUriFetcher fetcher = new FileDescriptorLocalUriFetcher(context.getContentResolver(), uri);
        fetcher.loadData(NORMAL, callback);
        Mockito.verify(callback).onLoadFailed(ArgumentMatchers.isA(FileNotFoundException.class));
    }
}

