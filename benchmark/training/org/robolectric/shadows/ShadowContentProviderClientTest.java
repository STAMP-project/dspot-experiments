package org.robolectric.shadows;


import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.CancellationSignal;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
public class ShadowContentProviderClientTest {
    private static final String AUTHORITY = "org.robolectric";

    private final Uri URI = Uri.parse(("content://" + (ShadowContentProviderClientTest.AUTHORITY)));

    private final ContentValues VALUES = new ContentValues();

    private static final String[] PROJECTION = null;

    private static final String SELECTION = "1=?";

    private static final String[] SELECTION_ARGS = new String[]{ "1" };

    private static final String SORT_ORDER = "DESC";

    private static final String MIME_TYPE = "application/octet-stream";

    @Mock
    ContentProvider provider;

    ContentResolver contentResolver = getContentResolver();

    @Test
    public void acquireContentProviderClient_isStable() {
        ContentProviderClient client = contentResolver.acquireContentProviderClient(ShadowContentProviderClientTest.AUTHORITY);
        assertThat(Shadows.shadowOf(client).isStable()).isTrue();
    }

    @Test
    public void acquireUnstableContentProviderClient_isUnstable() {
        ContentProviderClient client = contentResolver.acquireUnstableContentProviderClient(ShadowContentProviderClientTest.AUTHORITY);
        assertThat(Shadows.shadowOf(client).isStable()).isFalse();
    }

    @Test
    public void release_shouldRelease() {
        ContentProviderClient client = contentResolver.acquireContentProviderClient(ShadowContentProviderClientTest.AUTHORITY);
        ShadowContentProviderClient shadow = Shadows.shadowOf(client);
        assertThat(shadow.isReleased()).isFalse();
        client.release();
        assertThat(shadow.isReleased()).isTrue();
    }

    @Test(expected = IllegalStateException.class)
    public void release_shouldFailWhenCalledTwice() {
        ContentProviderClient client = contentResolver.acquireContentProviderClient(ShadowContentProviderClientTest.AUTHORITY);
        client.release();
        client.release();
        Assert.fail("client.release() was called twice and did not throw");
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR2)
    public void shouldDelegateToContentProvider() throws Exception {
        ContentProviderClient client = contentResolver.acquireContentProviderClient(ShadowContentProviderClientTest.AUTHORITY);
        client.query(URI, ShadowContentProviderClientTest.PROJECTION, ShadowContentProviderClientTest.SELECTION, ShadowContentProviderClientTest.SELECTION_ARGS, ShadowContentProviderClientTest.SORT_ORDER);
        Mockito.verify(provider).query(URI, ShadowContentProviderClientTest.PROJECTION, ShadowContentProviderClientTest.SELECTION, ShadowContentProviderClientTest.SELECTION_ARGS, ShadowContentProviderClientTest.SORT_ORDER);
        CancellationSignal signal = new CancellationSignal();
        client.query(URI, ShadowContentProviderClientTest.PROJECTION, ShadowContentProviderClientTest.SELECTION, ShadowContentProviderClientTest.SELECTION_ARGS, ShadowContentProviderClientTest.SORT_ORDER, signal);
        Mockito.verify(provider).query(URI, ShadowContentProviderClientTest.PROJECTION, ShadowContentProviderClientTest.SELECTION, ShadowContentProviderClientTest.SELECTION_ARGS, ShadowContentProviderClientTest.SORT_ORDER, signal);
        client.insert(URI, VALUES);
        Mockito.verify(provider).insert(URI, VALUES);
        client.update(URI, VALUES, ShadowContentProviderClientTest.SELECTION, ShadowContentProviderClientTest.SELECTION_ARGS);
        Mockito.verify(provider).update(URI, VALUES, ShadowContentProviderClientTest.SELECTION, ShadowContentProviderClientTest.SELECTION_ARGS);
        client.delete(URI, ShadowContentProviderClientTest.SELECTION, ShadowContentProviderClientTest.SELECTION_ARGS);
        Mockito.verify(provider).delete(URI, ShadowContentProviderClientTest.SELECTION, ShadowContentProviderClientTest.SELECTION_ARGS);
        client.getType(URI);
        Mockito.verify(provider).getType(URI);
        client.openFile(URI, "rw");
        Mockito.verify(provider).openFile(URI, "rw");
        client.openAssetFile(URI, "r");
        Mockito.verify(provider).openAssetFile(URI, "r");
        final Bundle opts = new Bundle();
        client.openTypedAssetFileDescriptor(URI, ShadowContentProviderClientTest.MIME_TYPE, opts);
        Mockito.verify(provider).openTypedAssetFile(URI, ShadowContentProviderClientTest.MIME_TYPE, opts);
        client.getStreamTypes(URI, ShadowContentProviderClientTest.MIME_TYPE);
        Mockito.verify(provider).getStreamTypes(URI, ShadowContentProviderClientTest.MIME_TYPE);
        final ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        client.applyBatch(ops);
        Mockito.verify(provider).applyBatch(ops);
        final ContentValues[] values = new ContentValues[]{ VALUES };
        client.bulkInsert(URI, values);
        Mockito.verify(provider).bulkInsert(URI, values);
        final String method = "method";
        final String arg = "arg";
        final Bundle extras = new Bundle();
        client.call(method, arg, extras);
        Mockito.verify(provider).call(method, arg, extras);
    }
}

