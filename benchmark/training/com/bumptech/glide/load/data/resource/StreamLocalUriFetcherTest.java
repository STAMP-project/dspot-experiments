package com.bumptech.glide.load.data.resource;


import Priority.LOW;
import Priority.NORMAL;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.StreamLocalUriFetcher;
import com.bumptech.glide.tests.ContentResolverShadow;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
public class StreamLocalUriFetcherTest {
    @Mock
    private DataFetcher.DataCallback<InputStream> callback;

    @Test
    public void testLoadResource_returnsInputStream() throws Exception {
        Context context = RuntimeEnvironment.application;
        Uri uri = Uri.parse("file://nothing");
        ContentResolver contentResolver = context.getContentResolver();
        ContentResolverShadow shadow = Shadow.extract(contentResolver);
        shadow.registerInputStream(uri, new ByteArrayInputStream(new byte[0]));
        StreamLocalUriFetcher fetcher = new StreamLocalUriFetcher(context.getContentResolver(), uri);
        fetcher.loadData(NORMAL, callback);
        Mockito.verify(callback).onDataReady(ArgumentMatchers.isNotNull(InputStream.class));
    }

    @Test
    public void testLoadResource_withNullInputStream_callsLoadFailed() {
        Context context = RuntimeEnvironment.application;
        Uri uri = Uri.parse("file://nothing");
        ContentResolver contentResolver = context.getContentResolver();
        ContentResolverShadow shadow = Shadow.extract(contentResolver);
        /* inputStream */
        shadow.registerInputStream(uri, null);
        StreamLocalUriFetcher fetcher = new StreamLocalUriFetcher(context.getContentResolver(), uri);
        fetcher.loadData(LOW, callback);
        Mockito.verify(callback).onLoadFailed(ArgumentMatchers.isA(FileNotFoundException.class));
    }
}

