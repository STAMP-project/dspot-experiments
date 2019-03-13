package org.robolectric.shadows;


import Intent.FLAG_GRANT_READ_URI_PERMISSION;
import Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
import ParcelFileDescriptor.MODE_READ_ONLY;
import RuntimeEnvironment.systemContext;
import ShadowContentResolver.NotifiedUri;
import ShadowContentResolver.Status;
import android.accounts.Account;
import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.PeriodicSync;
import android.content.SyncAdapterType;
import android.content.UriPermission;
import android.content.pm.ProviderInfo;
import android.content.res.AssetFileDescriptor;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.BaseCursor;


@RunWith(AndroidJUnit4.class)
public class ShadowContentResolverTest {
    private static final String AUTHORITY = "org.robolectric";

    private ContentResolver contentResolver;

    private ShadowContentResolver shadowContentResolver;

    private Uri uri21;

    private Uri uri22;

    private Account a;

    private Account b;

    @Test
    public void insert_shouldReturnIncreasingUris() {
        shadowContentResolver.setNextDatabaseIdForInserts(20);
        assertThat(contentResolver.insert(EXTERNAL_CONTENT_URI, new ContentValues())).isEqualTo(uri21);
        assertThat(contentResolver.insert(EXTERNAL_CONTENT_URI, new ContentValues())).isEqualTo(uri22);
    }

    @Test
    public void getType_shouldDefaultToNull() {
        assertThat(contentResolver.getType(uri21)).isNull();
    }

    @Test
    public void getType_shouldReturnProviderValue() {
        ShadowContentResolver.registerProviderInternal(ShadowContentResolverTest.AUTHORITY, new ContentProvider() {
            @Override
            public boolean onCreate() {
                return false;
            }

            @Override
            public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
                return new BaseCursor();
            }

            @Override
            public Uri insert(Uri uri, ContentValues values) {
                return null;
            }

            @Override
            public int delete(Uri uri, String selection, String[] selectionArgs) {
                return -1;
            }

            @Override
            public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
                return -1;
            }

            @Override
            public String getType(Uri uri) {
                return "mytype";
            }
        });
        final Uri uri = Uri.parse((("content://" + (ShadowContentResolverTest.AUTHORITY)) + "/some/path"));
        assertThat(contentResolver.getType(uri)).isEqualTo("mytype");
    }

    @Test
    public void insert_shouldTrackInsertStatements() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("foo", "bar");
        contentResolver.insert(EXTERNAL_CONTENT_URI, contentValues);
        assertThat(shadowContentResolver.getInsertStatements().size()).isEqualTo(1);
        assertThat(shadowContentResolver.getInsertStatements().get(0).getUri()).isEqualTo(EXTERNAL_CONTENT_URI);
        assertThat(shadowContentResolver.getInsertStatements().get(0).getContentValues().getAsString("foo")).isEqualTo("bar");
        contentValues = new ContentValues();
        contentValues.put("hello", "world");
        contentResolver.insert(EXTERNAL_CONTENT_URI, contentValues);
        assertThat(shadowContentResolver.getInsertStatements().size()).isEqualTo(2);
        assertThat(shadowContentResolver.getInsertStatements().get(1).getContentValues().getAsString("hello")).isEqualTo("world");
    }

    @Test
    public void insert_shouldTrackUpdateStatements() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("foo", "bar");
        contentResolver.update(EXTERNAL_CONTENT_URI, contentValues, "robolectric", new String[]{ "awesome" });
        assertThat(shadowContentResolver.getUpdateStatements().size()).isEqualTo(1);
        assertThat(shadowContentResolver.getUpdateStatements().get(0).getUri()).isEqualTo(EXTERNAL_CONTENT_URI);
        assertThat(shadowContentResolver.getUpdateStatements().get(0).getContentValues().getAsString("foo")).isEqualTo("bar");
        assertThat(shadowContentResolver.getUpdateStatements().get(0).getWhere()).isEqualTo("robolectric");
        assertThat(shadowContentResolver.getUpdateStatements().get(0).getSelectionArgs()).isEqualTo(new String[]{ "awesome" });
        contentValues = new ContentValues();
        contentValues.put("hello", "world");
        contentResolver.update(EXTERNAL_CONTENT_URI, contentValues, null, null);
        assertThat(shadowContentResolver.getUpdateStatements().size()).isEqualTo(2);
        assertThat(shadowContentResolver.getUpdateStatements().get(1).getUri()).isEqualTo(EXTERNAL_CONTENT_URI);
        assertThat(shadowContentResolver.getUpdateStatements().get(1).getContentValues().getAsString("hello")).isEqualTo("world");
        assertThat(shadowContentResolver.getUpdateStatements().get(1).getWhere()).isNull();
        assertThat(shadowContentResolver.getUpdateStatements().get(1).getSelectionArgs()).isNull();
    }

    @Test
    public void insert_supportsNullContentValues() {
        contentResolver.insert(EXTERNAL_CONTENT_URI, null);
        assertThat(shadowContentResolver.getInsertStatements().get(0).getContentValues()).isNull();
    }

    @Test
    public void update_supportsNullContentValues() {
        contentResolver.update(EXTERNAL_CONTENT_URI, null, null, null);
        assertThat(shadowContentResolver.getUpdateStatements().get(0).getContentValues()).isNull();
    }

    @Test
    public void delete_shouldTrackDeletedUris() {
        assertThat(shadowContentResolver.getDeletedUris().size()).isEqualTo(0);
        assertThat(contentResolver.delete(uri21, null, null)).isEqualTo(1);
        assertThat(shadowContentResolver.getDeletedUris()).contains(uri21);
        assertThat(shadowContentResolver.getDeletedUris().size()).isEqualTo(1);
        assertThat(contentResolver.delete(uri22, null, null)).isEqualTo(1);
        assertThat(shadowContentResolver.getDeletedUris()).contains(uri22);
        assertThat(shadowContentResolver.getDeletedUris().size()).isEqualTo(2);
    }

    @Test
    public void delete_shouldTrackDeletedStatements() {
        assertThat(shadowContentResolver.getDeleteStatements().size()).isEqualTo(0);
        assertThat(contentResolver.delete(uri21, "id", new String[]{ "5" })).isEqualTo(1);
        assertThat(shadowContentResolver.getDeleteStatements().size()).isEqualTo(1);
        assertThat(shadowContentResolver.getDeleteStatements().get(0).getUri()).isEqualTo(uri21);
        assertThat(shadowContentResolver.getDeleteStatements().get(0).getContentProvider()).isNull();
        assertThat(shadowContentResolver.getDeleteStatements().get(0).getWhere()).isEqualTo("id");
        assertThat(shadowContentResolver.getDeleteStatements().get(0).getSelectionArgs()[0]).isEqualTo("5");
        assertThat(contentResolver.delete(uri21, "foo", new String[]{ "bar" })).isEqualTo(1);
        assertThat(shadowContentResolver.getDeleteStatements().size()).isEqualTo(2);
        assertThat(shadowContentResolver.getDeleteStatements().get(1).getUri()).isEqualTo(uri21);
        assertThat(shadowContentResolver.getDeleteStatements().get(1).getWhere()).isEqualTo("foo");
        assertThat(shadowContentResolver.getDeleteStatements().get(1).getSelectionArgs()[0]).isEqualTo("bar");
    }

    @Test
    public void whenCursorHasBeenSet_query_shouldReturnTheCursor() {
        assertThat(shadowContentResolver.query(null, null, null, null, null)).isNull();
        BaseCursor cursor = new BaseCursor();
        shadowContentResolver.setCursor(cursor);
        assertThat(((BaseCursor) (shadowContentResolver.query(null, null, null, null, null)))).isSameAs(cursor);
    }

    @Test
    public void whenCursorHasBeenSet_queryWithCancellationSignal_shouldReturnTheCursor() {
        assertThat(shadowContentResolver.query(null, null, null, null, null, new CancellationSignal())).isNull();
        BaseCursor cursor = new BaseCursor();
        shadowContentResolver.setCursor(cursor);
        assertThat(((BaseCursor) (shadowContentResolver.query(null, null, null, null, null, new CancellationSignal())))).isSameAs(cursor);
    }

    @Test
    public void query_shouldReturnSpecificCursorsForSpecificUris() {
        assertThat(shadowContentResolver.query(uri21, null, null, null, null)).isNull();
        assertThat(shadowContentResolver.query(uri22, null, null, null, null)).isNull();
        BaseCursor cursor21 = new BaseCursor();
        BaseCursor cursor22 = new BaseCursor();
        shadowContentResolver.setCursor(uri21, cursor21);
        shadowContentResolver.setCursor(uri22, cursor22);
        assertThat(((BaseCursor) (shadowContentResolver.query(uri21, null, null, null, null)))).isSameAs(cursor21);
        assertThat(((BaseCursor) (shadowContentResolver.query(uri22, null, null, null, null)))).isSameAs(cursor22);
    }

    @Test
    public void query_shouldKnowWhatItsParamsWere() {
        String[] projection = new String[]{  };
        String selection = "select";
        String[] selectionArgs = new String[]{  };
        String sortOrder = "order";
        ShadowContentResolverTest.QueryParamTrackingCursor testCursor = new ShadowContentResolverTest.QueryParamTrackingCursor();
        shadowContentResolver.setCursor(testCursor);
        Cursor cursor = shadowContentResolver.query(uri21, projection, selection, selectionArgs, sortOrder);
        assertThat(((ShadowContentResolverTest.QueryParamTrackingCursor) (cursor))).isEqualTo(testCursor);
        assertThat(testCursor.uri).isEqualTo(uri21);
        assertThat(testCursor.projection).isEqualTo(projection);
        assertThat(testCursor.selection).isEqualTo(selection);
        assertThat(testCursor.selectionArgs).isEqualTo(selectionArgs);
        assertThat(testCursor.sortOrder).isEqualTo(sortOrder);
    }

    @Test
    public void acquireUnstableProvider_shouldDefaultToNull() {
        assertThat(contentResolver.acquireUnstableProvider(uri21)).isNull();
    }

    @Test
    public void acquireUnstableProvider_shouldReturnWithUri() {
        ContentProvider cp = Mockito.mock(ContentProvider.class);
        ShadowContentResolver.registerProviderInternal(ShadowContentResolverTest.AUTHORITY, cp);
        final Uri uri = Uri.parse(("content://" + (ShadowContentResolverTest.AUTHORITY)));
        assertThat(contentResolver.acquireUnstableProvider(uri)).isSameAs(cp.getIContentProvider());
    }

    @Test
    public void acquireUnstableProvider_shouldReturnWithString() {
        ContentProvider cp = Mockito.mock(ContentProvider.class);
        ShadowContentResolver.registerProviderInternal(ShadowContentResolverTest.AUTHORITY, cp);
        assertThat(contentResolver.acquireUnstableProvider(ShadowContentResolverTest.AUTHORITY)).isSameAs(cp.getIContentProvider());
    }

    @Test
    public void call_shouldCallProvider() {
        final String METHOD = "method";
        final String ARG = "arg";
        final Bundle EXTRAS = new Bundle();
        final Uri uri = Uri.parse(("content://" + (ShadowContentResolverTest.AUTHORITY)));
        ContentProvider provider = Mockito.mock(ContentProvider.class);
        Mockito.doReturn(null).when(provider).call(METHOD, ARG, EXTRAS);
        ShadowContentResolver.registerProviderInternal(ShadowContentResolverTest.AUTHORITY, provider);
        contentResolver.call(uri, METHOD, ARG, EXTRAS);
        Mockito.verify(provider).call(METHOD, ARG, EXTRAS);
    }

    @Test
    public void registerProvider_shouldAttachProviderInfo() {
        ContentProvider mock = Mockito.mock(ContentProvider.class);
        ProviderInfo providerInfo0 = new ProviderInfo();
        providerInfo0.authority = "the-authority";// todo: support multiple authorities

        providerInfo0.grantUriPermissions = true;
        mock.attachInfo(ApplicationProvider.getApplicationContext(), providerInfo0);
        mock.onCreate();
        ArgumentCaptor<ProviderInfo> captor = ArgumentCaptor.forClass(ProviderInfo.class);
        Mockito.verify(mock).attachInfo(ArgumentMatchers.same(((Application) (ApplicationProvider.getApplicationContext()))), captor.capture());
        ProviderInfo providerInfo = captor.getValue();
        assertThat(providerInfo.authority).isEqualTo("the-authority");
        assertThat(providerInfo.grantUriPermissions).isEqualTo(true);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void openInputStream_shouldReturnAnInputStreamThatExceptionsOnRead() throws Exception {
        InputStream inputStream = contentResolver.openInputStream(uri21);
        inputStream.read();
    }

    @Test
    public void openInputStream_returnsPreRegisteredStream() throws Exception {
        shadowContentResolver.registerInputStream(uri21, new ByteArrayInputStream("ourStream".getBytes(StandardCharsets.UTF_8)));
        InputStream inputStream = contentResolver.openInputStream(uri21);
        byte[] data = new byte[9];
        inputStream.read(data);
        assertThat(new String(data, StandardCharsets.UTF_8)).isEqualTo("ourStream");
    }

    @Test
    public void openOutputStream_shouldReturnAnOutputStream() throws Exception {
        assertThat(contentResolver.openOutputStream(uri21)).isInstanceOf(OutputStream.class);
    }

    @Test
    public void openOutputStream_shouldReturnRegisteredStream() throws Exception {
        final Uri uri = Uri.parse("content://registeredProvider/path");
        AtomicInteger callCount = new AtomicInteger();
        OutputStream outputStream = new OutputStream() {
            @Override
            public void write(int arg0) throws IOException {
                callCount.incrementAndGet();
            }

            @Override
            public String toString() {
                return "outputstream for " + uri;
            }
        };
        Shadows.shadowOf(contentResolver).registerOutputStream(uri, outputStream);
        assertThat(callCount.get()).isEqualTo(0);
        contentResolver.openOutputStream(uri).write(5);
        assertThat(callCount.get()).isEqualTo(1);
        contentResolver.openOutputStream(uri21).write(5);
        assertThat(callCount.get()).isEqualTo(1);
    }

    @Test
    public void shouldTrackNotifiedUris() {
        contentResolver.notifyChange(Uri.parse("foo"), null, true);
        contentResolver.notifyChange(Uri.parse("bar"), null);
        assertThat(shadowContentResolver.getNotifiedUris().size()).isEqualTo(2);
        ShadowContentResolver.NotifiedUri uri = shadowContentResolver.getNotifiedUris().get(0);
        assertThat(uri.uri.toString()).isEqualTo("foo");
        assertThat(uri.syncToNetwork).isTrue();
        assertThat(uri.observer).isNull();
        uri = shadowContentResolver.getNotifiedUris().get(1);
        assertThat(uri.uri.toString()).isEqualTo("bar");
        assertThat(uri.syncToNetwork).isFalse();
        assertThat(uri.observer).isNull();
    }

    @SuppressWarnings("serial")
    @Test
    public void applyBatchForRegisteredProvider() throws OperationApplicationException, RemoteException {
        final List<String> operations = new ArrayList<>();
        ShadowContentResolver.registerProviderInternal("registeredProvider", new ContentProvider() {
            @Override
            public boolean onCreate() {
                return true;
            }

            @Override
            public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
                operations.add("query");
                MatrixCursor cursor = new MatrixCursor(new String[]{ "a" });
                cursor.addRow(new Object[]{ "b" });
                return cursor;
            }

            @Override
            public String getType(Uri uri) {
                return null;
            }

            @Override
            public Uri insert(Uri uri, ContentValues values) {
                operations.add("insert");
                return ContentUris.withAppendedId(uri, 1);
            }

            @Override
            public int delete(Uri uri, String selection, String[] selectionArgs) {
                operations.add("delete");
                return 0;
            }

            @Override
            public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
                operations.add("update");
                return 0;
            }
        });
        final Uri uri = Uri.parse("content://registeredProvider/path");
        List<ContentProviderOperation> contentProviderOperations = Arrays.asList(ContentProviderOperation.newInsert(uri).withValue("a", "b").build(), ContentProviderOperation.newUpdate(uri).withValue("a", "b").build(), ContentProviderOperation.newDelete(uri).build(), ContentProviderOperation.newAssertQuery(uri).withValue("a", "b").build());
        contentResolver.applyBatch("registeredProvider", new ArrayList(contentProviderOperations));
        assertThat(operations).containsExactly("insert", "update", "delete", "query");
    }

    @Test
    public void applyBatchForUnregisteredProvider() throws OperationApplicationException, RemoteException {
        List<ContentProviderOperation> resultOperations = shadowContentResolver.getContentProviderOperations(ShadowContentResolverTest.AUTHORITY);
        assertThat(resultOperations).isNotNull();
        assertThat(resultOperations.size()).isEqualTo(0);
        ContentProviderResult[] contentProviderResults = new ContentProviderResult[]{ new ContentProviderResult(1), new ContentProviderResult(1) };
        shadowContentResolver.setContentProviderResult(contentProviderResults);
        Uri uri = Uri.parse("content://org.robolectric");
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        operations.add(ContentProviderOperation.newInsert(uri).withValue("column1", "foo").withValue("column2", 5).build());
        operations.add(ContentProviderOperation.newUpdate(uri).withSelection("id_column", new String[]{ "99" }).withValue("column1", "bar").build());
        operations.add(ContentProviderOperation.newDelete(uri).withSelection("id_column", new String[]{ "11" }).build());
        ContentProviderResult[] result = contentResolver.applyBatch(ShadowContentResolverTest.AUTHORITY, operations);
        resultOperations = shadowContentResolver.getContentProviderOperations(ShadowContentResolverTest.AUTHORITY);
        assertThat(resultOperations).isEqualTo(operations);
        assertThat(result).isEqualTo(contentProviderResults);
    }

    @Test
    public void shouldKeepTrackOfSyncRequests() {
        ShadowContentResolver.Status status = ShadowContentResolver.getStatus(a, ShadowContentResolverTest.AUTHORITY, true);
        assertThat(status).isNotNull();
        assertThat(status.syncRequests).isEqualTo(0);
        ContentResolver.requestSync(a, ShadowContentResolverTest.AUTHORITY, new Bundle());
        assertThat(status.syncRequests).isEqualTo(1);
        assertThat(status.syncExtras).isNotNull();
    }

    @Test
    public void shouldKnowIfSyncIsActive() {
        assertThat(ContentResolver.isSyncActive(a, ShadowContentResolverTest.AUTHORITY)).isFalse();
        ContentResolver.requestSync(a, ShadowContentResolverTest.AUTHORITY, new Bundle());
        assertThat(ContentResolver.isSyncActive(a, ShadowContentResolverTest.AUTHORITY)).isTrue();
    }

    @Test
    public void shouldCancelSync() {
        ContentResolver.requestSync(a, ShadowContentResolverTest.AUTHORITY, new Bundle());
        ContentResolver.requestSync(b, ShadowContentResolverTest.AUTHORITY, new Bundle());
        assertThat(ContentResolver.isSyncActive(a, ShadowContentResolverTest.AUTHORITY)).isTrue();
        assertThat(ContentResolver.isSyncActive(b, ShadowContentResolverTest.AUTHORITY)).isTrue();
        ContentResolver.cancelSync(a, ShadowContentResolverTest.AUTHORITY);
        assertThat(ContentResolver.isSyncActive(a, ShadowContentResolverTest.AUTHORITY)).isFalse();
        assertThat(ContentResolver.isSyncActive(b, ShadowContentResolverTest.AUTHORITY)).isTrue();
    }

    @Test
    public void shouldSetIsSyncable() {
        assertThat(ContentResolver.getIsSyncable(a, ShadowContentResolverTest.AUTHORITY)).isEqualTo((-1));
        assertThat(ContentResolver.getIsSyncable(b, ShadowContentResolverTest.AUTHORITY)).isEqualTo((-1));
        ContentResolver.setIsSyncable(a, ShadowContentResolverTest.AUTHORITY, 1);
        ContentResolver.setIsSyncable(b, ShadowContentResolverTest.AUTHORITY, 2);
        assertThat(ContentResolver.getIsSyncable(a, ShadowContentResolverTest.AUTHORITY)).isEqualTo(1);
        assertThat(ContentResolver.getIsSyncable(b, ShadowContentResolverTest.AUTHORITY)).isEqualTo(2);
    }

    @Test
    public void shouldSetSyncAutomatically() {
        assertThat(ContentResolver.getSyncAutomatically(a, ShadowContentResolverTest.AUTHORITY)).isFalse();
        ContentResolver.setSyncAutomatically(a, ShadowContentResolverTest.AUTHORITY, true);
        assertThat(ContentResolver.getSyncAutomatically(a, ShadowContentResolverTest.AUTHORITY)).isTrue();
    }

    @Test
    public void shouldAddPeriodicSync() {
        Bundle fooBar = new Bundle();
        fooBar.putString("foo", "bar");
        Bundle fooBaz = new Bundle();
        fooBaz.putString("foo", "baz");
        ContentResolver.addPeriodicSync(a, ShadowContentResolverTest.AUTHORITY, fooBar, 6000L);
        ContentResolver.addPeriodicSync(a, ShadowContentResolverTest.AUTHORITY, fooBaz, 6000L);
        ContentResolver.addPeriodicSync(b, ShadowContentResolverTest.AUTHORITY, fooBar, 6000L);
        ContentResolver.addPeriodicSync(b, ShadowContentResolverTest.AUTHORITY, fooBaz, 6000L);
        assertThat(ShadowContentResolver.getPeriodicSyncs(a, ShadowContentResolverTest.AUTHORITY)).containsExactly(new PeriodicSync(a, ShadowContentResolverTest.AUTHORITY, fooBar, 6000L), new PeriodicSync(a, ShadowContentResolverTest.AUTHORITY, fooBaz, 6000L));
        assertThat(ShadowContentResolver.getPeriodicSyncs(b, ShadowContentResolverTest.AUTHORITY)).containsExactly(new PeriodicSync(b, ShadowContentResolverTest.AUTHORITY, fooBar, 6000L), new PeriodicSync(b, ShadowContentResolverTest.AUTHORITY, fooBaz, 6000L));
        // If same extras, but different time, simply update the time.
        ContentResolver.addPeriodicSync(a, ShadowContentResolverTest.AUTHORITY, fooBar, 42L);
        ContentResolver.addPeriodicSync(b, ShadowContentResolverTest.AUTHORITY, fooBaz, 42L);
        assertThat(ShadowContentResolver.getPeriodicSyncs(a, ShadowContentResolverTest.AUTHORITY)).containsExactly(new PeriodicSync(a, ShadowContentResolverTest.AUTHORITY, fooBar, 42L), new PeriodicSync(a, ShadowContentResolverTest.AUTHORITY, fooBaz, 6000L));
        assertThat(ShadowContentResolver.getPeriodicSyncs(b, ShadowContentResolverTest.AUTHORITY)).containsExactly(new PeriodicSync(b, ShadowContentResolverTest.AUTHORITY, fooBar, 6000L), new PeriodicSync(b, ShadowContentResolverTest.AUTHORITY, fooBaz, 42L));
    }

    @Test
    public void shouldRemovePeriodSync() {
        Bundle fooBar = new Bundle();
        fooBar.putString("foo", "bar");
        Bundle fooBaz = new Bundle();
        fooBaz.putString("foo", "baz");
        Bundle foo42 = new Bundle();
        foo42.putInt("foo", 42);
        assertThat(ShadowContentResolver.getPeriodicSyncs(b, ShadowContentResolverTest.AUTHORITY)).isEmpty();
        assertThat(ShadowContentResolver.getPeriodicSyncs(a, ShadowContentResolverTest.AUTHORITY)).isEmpty();
        ContentResolver.addPeriodicSync(a, ShadowContentResolverTest.AUTHORITY, fooBar, 6000L);
        ContentResolver.addPeriodicSync(a, ShadowContentResolverTest.AUTHORITY, fooBaz, 6000L);
        ContentResolver.addPeriodicSync(a, ShadowContentResolverTest.AUTHORITY, foo42, 6000L);
        ContentResolver.addPeriodicSync(b, ShadowContentResolverTest.AUTHORITY, fooBar, 6000L);
        ContentResolver.addPeriodicSync(b, ShadowContentResolverTest.AUTHORITY, fooBaz, 6000L);
        ContentResolver.addPeriodicSync(b, ShadowContentResolverTest.AUTHORITY, foo42, 6000L);
        assertThat(ShadowContentResolver.getPeriodicSyncs(a, ShadowContentResolverTest.AUTHORITY)).containsExactly(new PeriodicSync(a, ShadowContentResolverTest.AUTHORITY, fooBar, 6000L), new PeriodicSync(a, ShadowContentResolverTest.AUTHORITY, fooBaz, 6000L), new PeriodicSync(a, ShadowContentResolverTest.AUTHORITY, foo42, 6000L));
        ContentResolver.removePeriodicSync(a, ShadowContentResolverTest.AUTHORITY, fooBar);
        assertThat(ShadowContentResolver.getPeriodicSyncs(a, ShadowContentResolverTest.AUTHORITY)).containsExactly(new PeriodicSync(a, ShadowContentResolverTest.AUTHORITY, fooBaz, 6000L), new PeriodicSync(a, ShadowContentResolverTest.AUTHORITY, foo42, 6000L));
        ContentResolver.removePeriodicSync(a, ShadowContentResolverTest.AUTHORITY, fooBaz);
        assertThat(ShadowContentResolver.getPeriodicSyncs(a, ShadowContentResolverTest.AUTHORITY)).containsExactly(new PeriodicSync(a, ShadowContentResolverTest.AUTHORITY, foo42, 6000L));
        ContentResolver.removePeriodicSync(a, ShadowContentResolverTest.AUTHORITY, foo42);
        assertThat(ShadowContentResolver.getPeriodicSyncs(a, ShadowContentResolverTest.AUTHORITY)).isEmpty();
        assertThat(ShadowContentResolver.getPeriodicSyncs(b, ShadowContentResolverTest.AUTHORITY)).containsExactly(new PeriodicSync(b, ShadowContentResolverTest.AUTHORITY, fooBar, 6000L), new PeriodicSync(b, ShadowContentResolverTest.AUTHORITY, fooBaz, 6000L), new PeriodicSync(b, ShadowContentResolverTest.AUTHORITY, foo42, 6000L));
    }

    @Test
    public void shouldGetPeriodSyncs() {
        assertThat(ContentResolver.getPeriodicSyncs(a, ShadowContentResolverTest.AUTHORITY).size()).isEqualTo(0);
        ContentResolver.addPeriodicSync(a, ShadowContentResolverTest.AUTHORITY, new Bundle(), 6000L);
        List<PeriodicSync> syncs = ContentResolver.getPeriodicSyncs(a, ShadowContentResolverTest.AUTHORITY);
        assertThat(syncs.size()).isEqualTo(1);
        PeriodicSync first = syncs.get(0);
        assertThat(first.account).isEqualTo(a);
        assertThat(first.authority).isEqualTo(ShadowContentResolverTest.AUTHORITY);
        assertThat(first.period).isEqualTo(6000L);
        assertThat(first.extras).isNotNull();
    }

    @Test
    public void shouldValidateSyncExtras() {
        Bundle bundle = new Bundle();
        bundle.putString("foo", "strings");
        bundle.putLong("long", 10L);
        bundle.putDouble("double", 10.0);
        bundle.putFloat("float", 10.0F);
        bundle.putInt("int", 10);
        bundle.putParcelable("account", a);
        ContentResolver.validateSyncExtrasBundle(bundle);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldValidateSyncExtrasAndThrow() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("intent", new Intent());
        ContentResolver.validateSyncExtrasBundle(bundle);
    }

    @Test
    public void shouldSetMasterSyncAutomatically() {
        assertThat(ContentResolver.getMasterSyncAutomatically()).isFalse();
        ContentResolver.setMasterSyncAutomatically(true);
        assertThat(ContentResolver.getMasterSyncAutomatically()).isTrue();
    }

    @Test
    public void shouldDelegateCallsToRegisteredProvider() {
        ShadowContentResolver.registerProviderInternal(ShadowContentResolverTest.AUTHORITY, new ContentProvider() {
            @Override
            public boolean onCreate() {
                return false;
            }

            @Override
            public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
                return new BaseCursor();
            }

            @Override
            public Uri insert(Uri uri, ContentValues values) {
                return null;
            }

            @Override
            public int delete(Uri uri, String selection, String[] selectionArgs) {
                return -1;
            }

            @Override
            public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
                return -1;
            }

            @Override
            public String getType(Uri uri) {
                return null;
            }
        });
        final Uri uri = Uri.parse((("content://" + (ShadowContentResolverTest.AUTHORITY)) + "/some/path"));
        final Uri unrelated = Uri.parse("content://unrelated/some/path");
        assertThat(contentResolver.query(uri, null, null, null, null)).isNotNull();
        assertThat(contentResolver.insert(uri, new ContentValues())).isNull();
        assertThat(contentResolver.delete(uri, null, null)).isEqualTo((-1));
        assertThat(contentResolver.update(uri, new ContentValues(), null, null)).isEqualTo((-1));
        assertThat(contentResolver.query(unrelated, null, null, null, null)).isNull();
        assertThat(contentResolver.insert(unrelated, new ContentValues())).isNotNull();
        assertThat(contentResolver.delete(unrelated, null, null)).isEqualTo(1);
        assertThat(contentResolver.update(unrelated, new ContentValues(), null, null)).isEqualTo(1);
    }

    @Test
    public void shouldRegisterContentObservers() {
        ShadowContentResolverTest.TestContentObserver co = new ShadowContentResolverTest.TestContentObserver(null);
        ShadowContentResolver scr = Shadows.shadowOf(contentResolver);
        assertThat(scr.getContentObservers(EXTERNAL_CONTENT_URI)).isEmpty();
        contentResolver.registerContentObserver(EXTERNAL_CONTENT_URI, true, co);
        assertThat(scr.getContentObservers(EXTERNAL_CONTENT_URI)).containsExactly(((ContentObserver) (co)));
        assertThat(co.changed).isFalse();
        contentResolver.notifyChange(EXTERNAL_CONTENT_URI, null);
        assertThat(co.changed).isTrue();
        contentResolver.unregisterContentObserver(co);
        assertThat(scr.getContentObservers(EXTERNAL_CONTENT_URI)).isEmpty();
    }

    @Test
    public void shouldUnregisterContentObservers() {
        ShadowContentResolverTest.TestContentObserver co = new ShadowContentResolverTest.TestContentObserver(null);
        ShadowContentResolver scr = Shadows.shadowOf(contentResolver);
        contentResolver.registerContentObserver(EXTERNAL_CONTENT_URI, true, co);
        assertThat(scr.getContentObservers(EXTERNAL_CONTENT_URI)).contains(co);
        contentResolver.unregisterContentObserver(co);
        assertThat(scr.getContentObservers(EXTERNAL_CONTENT_URI)).isEmpty();
        assertThat(co.changed).isFalse();
        contentResolver.notifyChange(EXTERNAL_CONTENT_URI, null);
        assertThat(co.changed).isFalse();
    }

    @Test
    public void shouldNotifyChildContentObservers() throws Exception {
        ShadowContentResolverTest.TestContentObserver co1 = new ShadowContentResolverTest.TestContentObserver(null);
        ShadowContentResolverTest.TestContentObserver co2 = new ShadowContentResolverTest.TestContentObserver(null);
        Uri childUri = EXTERNAL_CONTENT_URI.buildUpon().appendPath("path").build();
        contentResolver.registerContentObserver(EXTERNAL_CONTENT_URI, true, co1);
        contentResolver.registerContentObserver(childUri, false, co2);
        co1.changed = co2.changed = false;
        contentResolver.notifyChange(childUri, null);
        assertThat(co1.changed).isTrue();
        assertThat(co2.changed).isTrue();
        co1.changed = co2.changed = false;
        contentResolver.notifyChange(EXTERNAL_CONTENT_URI, null);
        assertThat(co1.changed).isTrue();
        assertThat(co2.changed).isFalse();
        co1.changed = co2.changed = false;
        contentResolver.notifyChange(childUri.buildUpon().appendPath("extra").build(), null);
        assertThat(co1.changed).isTrue();
        assertThat(co2.changed).isFalse();
    }

    @Test
    public void getProvider_shouldCreateProviderFromManifest() throws Exception {
        Uri uri = Uri.parse("content://org.robolectric.authority1/shadows");
        ContentProvider provider = ShadowContentResolver.getProvider(uri);
        assertThat(provider).isNotNull();
        assertThat(provider.getReadPermission()).isEqualTo("READ_PERMISSION");
        assertThat(provider.getWritePermission()).isEqualTo("WRITE_PERMISSION");
        assertThat(provider.getPathPermissions()).asList().hasSize(1);
        // unfortunately, there is no direct way of testing if authority is set or not
        // however, it's checked in ContentProvider.Transport method calls (validateIncomingUri), so
        // it's the closest we can test against
        provider.getIContentProvider().getType(uri);// should not throw

    }

    @Test
    @Config(manifest = NONE)
    public void getProvider_shouldNotReturnAnyProviderWhenManifestIsNull() {
        Application application = new Application();
        Shadows.shadowOf(application).callAttach(systemContext);
        assertThat(ShadowContentResolver.getProvider(Uri.parse("content://"))).isNull();
    }

    @Test
    public void openTypedAssetFileDescriptor_shouldOpenDescriptor() throws RemoteException, IOException {
        Robolectric.setupContentProvider(ShadowContentResolverTest.MyContentProvider.class, ShadowContentResolverTest.AUTHORITY);
        AssetFileDescriptor afd = contentResolver.openTypedAssetFileDescriptor(Uri.parse((("content://" + (ShadowContentResolverTest.AUTHORITY)) + "/whatever")), "*/*", null);
        FileDescriptor descriptor = afd.getFileDescriptor();
        assertThat(descriptor).isNotNull();
    }

    @Test
    @Config(minSdk = VERSION_CODES.KITKAT)
    public void takeAndReleasePersistableUriPermissions() {
        List<UriPermission> permissions = contentResolver.getPersistedUriPermissions();
        assertThat(permissions).isEmpty();
        // Take the read permission for the uri.
        Uri uri = Uri.parse((("content://" + (ShadowContentResolverTest.AUTHORITY)) + "/whatever"));
        contentResolver.takePersistableUriPermission(uri, FLAG_GRANT_READ_URI_PERMISSION);
        assertThat(permissions).hasSize(1);
        assertThat(permissions.get(0).getUri()).isSameAs(uri);
        assertThat(permissions.get(0).isReadPermission()).isTrue();
        assertThat(permissions.get(0).isWritePermission()).isFalse();
        // Take the write permission for the uri.
        contentResolver.takePersistableUriPermission(uri, FLAG_GRANT_WRITE_URI_PERMISSION);
        assertThat(permissions).hasSize(1);
        assertThat(permissions.get(0).getUri()).isSameAs(uri);
        assertThat(permissions.get(0).isReadPermission()).isTrue();
        assertThat(permissions.get(0).isWritePermission()).isTrue();
        // Release the read permission for the uri.
        contentResolver.releasePersistableUriPermission(uri, FLAG_GRANT_READ_URI_PERMISSION);
        assertThat(permissions).hasSize(1);
        assertThat(permissions.get(0).getUri()).isSameAs(uri);
        assertThat(permissions.get(0).isReadPermission()).isFalse();
        assertThat(permissions.get(0).isWritePermission()).isTrue();
        // Release the write permission for the uri.
        contentResolver.releasePersistableUriPermission(uri, FLAG_GRANT_WRITE_URI_PERMISSION);
        assertThat(permissions).isEmpty();
    }

    @Test
    public void getSyncAdapterTypes() {
        SyncAdapterType[] syncAdapterTypes = new SyncAdapterType[]{ /* userVisible= */
        /* supportsUploading= */
        new SyncAdapterType("authority1", "accountType1", false, false), /* userVisible= */
        /* supportsUploading= */
        new SyncAdapterType("authority2", "accountType2", true, false), /* userVisible= */
        /* supportsUploading= */
        new SyncAdapterType("authority3", "accountType3", true, true) };
        ShadowContentResolver.setSyncAdapterTypes(syncAdapterTypes);
        assertThat(ContentResolver.getSyncAdapterTypes()).isEqualTo(syncAdapterTypes);
    }

    private static class QueryParamTrackingCursor extends BaseCursor {
        public Uri uri;

        public String[] projection;

        public String selection;

        public String[] selectionArgs;

        public String sortOrder;

        @Override
        public void setQuery(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
            this.uri = uri;
            this.projection = projection;
            this.selection = selection;
            this.selectionArgs = selectionArgs;
            this.sortOrder = sortOrder;
        }
    }

    private static class TestContentObserver extends ContentObserver {
        public TestContentObserver(Handler handler) {
            super(handler);
        }

        public boolean changed = false;

        @Override
        public void onChange(boolean selfChange) {
            changed = true;
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            changed = true;
        }
    }

    /**
     * Provider that opens a temporary file.
     */
    public static class MyContentProvider extends ContentProvider {
        @Override
        public boolean onCreate() {
            return true;
        }

        @Override
        public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
            return null;
        }

        @Override
        public String getType(Uri uri) {
            return null;
        }

        @Override
        public Uri insert(Uri uri, ContentValues contentValues) {
            return null;
        }

        @Override
        public int delete(Uri uri, String s, String[] strings) {
            return 0;
        }

        @Override
        public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
            return 0;
        }

        @Override
        public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
            final File file = new File(ApplicationProvider.getApplicationContext().getFilesDir(), "test_file");
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("error creating new file", e);
            }
            return ParcelFileDescriptor.open(file, MODE_READ_ONLY);
        }
    }
}

