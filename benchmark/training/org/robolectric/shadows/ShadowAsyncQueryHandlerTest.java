package org.robolectric.shadows;


import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.fakes.RoboCursor;


/**
 * Unit tests for {@link ShadowAsyncQueryHandler}.
 */
@RunWith(AndroidJUnit4.class)
public final class ShadowAsyncQueryHandlerTest {
    private static final int TOKEN = 22;

    private static final Object COOKIE = new Object();

    private static final RoboCursor CURSOR = new RoboCursor();

    private ContentResolver contentResolver;

    @Test
    public void startQuery_callbackIsCalled() throws Exception {
        ShadowAsyncQueryHandlerTest.FakeAsyncQueryHandler asyncQueryHandler = new ShadowAsyncQueryHandlerTest.FakeAsyncQueryHandler(contentResolver);
        Shadows.shadowOf(contentResolver).setCursor(EXTERNAL_CONTENT_URI, ShadowAsyncQueryHandlerTest.CURSOR);
        /* projection */
        /* selection */
        /* selectionArgs */
        /* orderBy */
        asyncQueryHandler.startQuery(ShadowAsyncQueryHandlerTest.TOKEN, ShadowAsyncQueryHandlerTest.COOKIE, EXTERNAL_CONTENT_URI, null, null, null, null);
        assertThat(asyncQueryHandler.token).isEqualTo(ShadowAsyncQueryHandlerTest.TOKEN);
        assertThat(asyncQueryHandler.cookie).isEqualTo(ShadowAsyncQueryHandlerTest.COOKIE);
        assertThat(asyncQueryHandler.cursor).isEqualTo(ShadowAsyncQueryHandlerTest.CURSOR);
    }

    @Test
    public void startInsert_callbackIsCalled() throws Exception {
        ShadowAsyncQueryHandlerTest.FakeAsyncQueryHandler asyncQueryHandler = new ShadowAsyncQueryHandlerTest.FakeAsyncQueryHandler(contentResolver);
        /* initialValues */
        asyncQueryHandler.startInsert(ShadowAsyncQueryHandlerTest.TOKEN, ShadowAsyncQueryHandlerTest.COOKIE, EXTERNAL_CONTENT_URI, null);
        assertThat(asyncQueryHandler.token).isEqualTo(ShadowAsyncQueryHandlerTest.TOKEN);
        assertThat(asyncQueryHandler.cookie).isEqualTo(ShadowAsyncQueryHandlerTest.COOKIE);
        assertThat(asyncQueryHandler.uri).isEqualTo(ContentUris.withAppendedId(EXTERNAL_CONTENT_URI, 1));
    }

    @Test
    public void startUpdate_callbackIsCalled() throws Exception {
        ShadowAsyncQueryHandlerTest.FakeAsyncQueryHandler asyncQueryHandler = new ShadowAsyncQueryHandlerTest.FakeAsyncQueryHandler(contentResolver);
        contentResolver.insert(EXTERNAL_CONTENT_URI, new ContentValues());
        /* values */
        /* selection */
        /* selectionArgs */
        asyncQueryHandler.startUpdate(ShadowAsyncQueryHandlerTest.TOKEN, ShadowAsyncQueryHandlerTest.COOKIE, EXTERNAL_CONTENT_URI, null, null, null);
        assertThat(asyncQueryHandler.token).isEqualTo(ShadowAsyncQueryHandlerTest.TOKEN);
        assertThat(asyncQueryHandler.cookie).isEqualTo(ShadowAsyncQueryHandlerTest.COOKIE);
        assertThat(asyncQueryHandler.result).isEqualTo(1);
    }

    @Test
    public void startDelete_callbackIsCalled() throws Exception {
        ShadowAsyncQueryHandlerTest.FakeAsyncQueryHandler asyncQueryHandler = new ShadowAsyncQueryHandlerTest.FakeAsyncQueryHandler(contentResolver);
        contentResolver.insert(EXTERNAL_CONTENT_URI, new ContentValues());
        /* selection */
        /* selectionArgs */
        asyncQueryHandler.startDelete(ShadowAsyncQueryHandlerTest.TOKEN, ShadowAsyncQueryHandlerTest.COOKIE, EXTERNAL_CONTENT_URI, null, null);
        assertThat(asyncQueryHandler.token).isEqualTo(ShadowAsyncQueryHandlerTest.TOKEN);
        assertThat(asyncQueryHandler.cookie).isEqualTo(ShadowAsyncQueryHandlerTest.COOKIE);
        assertThat(asyncQueryHandler.result).isEqualTo(1);
    }

    private static class FakeAsyncQueryHandler extends AsyncQueryHandler {
        int token;

        Object cookie;

        Cursor cursor;

        Uri uri;

        int result;

        FakeAsyncQueryHandler(ContentResolver contentResolver) {
            super(contentResolver);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            this.token = token;
            this.cookie = cookie;
            this.cursor = cursor;
        }

        @Override
        protected void onInsertComplete(int token, Object cookie, Uri uri) {
            this.token = token;
            this.cookie = cookie;
            this.uri = uri;
        }

        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {
            this.token = token;
            this.cookie = cookie;
            this.result = result;
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            this.token = token;
            this.cookie = cookie;
            this.result = result;
        }
    }
}

