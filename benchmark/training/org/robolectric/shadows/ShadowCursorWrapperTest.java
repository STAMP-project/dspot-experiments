package org.robolectric.shadows;


import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowCursorWrapperTest {
    private static class ForwardVerifier {
        final Cursor mockCursor;

        final CursorWrapper cursorWrapper;

        final HashMap<String, Method> cursorMethod;

        public ForwardVerifier() {
            mockCursor = Mockito.mock(Cursor.class);
            cursorWrapper = new CursorWrapper(mockCursor);
            cursorMethod = new HashMap<>();
            // This works because no two methods in the Cursor interface have the same name
            for (Method m : Cursor.class.getMethods()) {
                cursorMethod.put(m.getName(), m);
            }
        }

        public void verifyForward(String methodName, Object... params) throws Exception {
            assertThat(cursorMethod.keySet()).contains(methodName);
            Method method = cursorMethod.get(methodName);
            method.invoke(cursorWrapper, params);
            method.invoke(Mockito.verify(mockCursor, Mockito.times(1)), params);
            Mockito.verifyNoMoreInteractions(mockCursor);
        }
    }

    @Test
    public void testCursorMethodsAreForwarded() throws Exception {
        ShadowCursorWrapperTest.ForwardVerifier v = new ShadowCursorWrapperTest.ForwardVerifier();
        v.verifyForward("close");
        v.verifyForward("copyStringToBuffer", 1, Mockito.mock(CharArrayBuffer.class));
        v.verifyForward("deactivate");
        v.verifyForward("getBlob", 2);
        v.verifyForward("getColumnCount");
        v.verifyForward("getColumnIndex", "foo");
        v.verifyForward("getColumnIndexOrThrow", "foo");
        v.verifyForward("getColumnName", 3);
        v.verifyForward("getColumnNames");
        v.verifyForward("getCount");
        v.verifyForward("getDouble", 12);
        v.verifyForward("getExtras");
        v.verifyForward("getFloat", 4);
        v.verifyForward("getInt", 5);
        v.verifyForward("getLong", 6);
        v.verifyForward("getPosition");
        v.verifyForward("getShort", 7);
        v.verifyForward("getString", 8);
        v.verifyForward("getType", 9);
        v.verifyForward("getWantsAllOnMoveCalls");
        v.verifyForward("isAfterLast");
        v.verifyForward("isBeforeFirst");
        v.verifyForward("isClosed");
        v.verifyForward("isFirst");
        v.verifyForward("isLast");
        v.verifyForward("isNull", 10);
        v.verifyForward("move", 11);
        v.verifyForward("moveToFirst");
        v.verifyForward("moveToLast");
        v.verifyForward("moveToNext");
        v.verifyForward("moveToPosition", 13);
        v.verifyForward("moveToPrevious");
        v.verifyForward("registerContentObserver", Mockito.mock(ContentObserver.class));
        v.verifyForward("registerDataSetObserver", Mockito.mock(DataSetObserver.class));
        v.verifyForward("requery");
        v.verifyForward("respond", Mockito.mock(Bundle.class));
        v.verifyForward("setNotificationUri", Mockito.mock(ContentResolver.class), Mockito.mock(Uri.class));
        v.verifyForward("unregisterContentObserver", Mockito.mock(ContentObserver.class));
        v.verifyForward("unregisterDataSetObserver", Mockito.mock(DataSetObserver.class));
    }

    @Test
    public void getWrappedCursor() {
        Cursor mockCursor = Mockito.mock(Cursor.class);
        CursorWrapper cursorWrapper = new CursorWrapper(mockCursor);
        ShadowCursorWrapper shadow = Shadows.shadowOf(cursorWrapper);
        assertThat(shadow.getWrappedCursor()).isSameAs(mockCursor);
    }
}

