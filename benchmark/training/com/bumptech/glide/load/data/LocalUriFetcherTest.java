package com.bumptech.glide.load.data;


import Priority.NORMAL;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class LocalUriFetcherTest {
    private LocalUriFetcherTest.TestLocalUriFetcher fetcher;

    @Mock
    private DataFetcher.DataCallback<Closeable> callback;

    @Test
    public void testClosesDataOnCleanup() throws Exception {
        fetcher.loadData(NORMAL, callback);
        cleanup();
        Mockito.verify(fetcher.closeable).close();
    }

    @Test
    public void testDoesNotCloseNullData() throws IOException {
        cleanup();
        Mockito.verify(fetcher.closeable, Mockito.never()).close();
    }

    @Test
    public void testHandlesExceptionOnClose() throws Exception {
        fetcher.loadData(NORMAL, callback);
        Mockito.doThrow(new IOException("Test")).when(fetcher.closeable).close();
        cleanup();
        Mockito.verify(fetcher.closeable).close();
    }

    private static class TestLocalUriFetcher extends LocalUriFetcher<Closeable> {
        final Closeable closeable = Mockito.mock(Closeable.class);

        TestLocalUriFetcher(Context context, Uri uri) {
            super(context.getContentResolver(), uri);
        }

        @Override
        protected Closeable loadResource(Uri uri, ContentResolver contentResolver) throws FileNotFoundException {
            return closeable;
        }

        @Override
        protected void close(Closeable data) throws IOException {
            data.close();
        }

        @NonNull
        @Override
        public Class<Closeable> getDataClass() {
            return Closeable.class;
        }
    }
}

