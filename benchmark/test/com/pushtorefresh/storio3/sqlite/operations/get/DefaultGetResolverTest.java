package com.pushtorefresh.storio3.sqlite.operations.get;


import StorIOSQLite.LowLevel;
import android.database.Cursor;
import android.support.annotation.NonNull;
import com.pushtorefresh.storio3.sqlite.StorIOSQLite;
import com.pushtorefresh.storio3.sqlite.queries.Query;
import com.pushtorefresh.storio3.sqlite.queries.RawQuery;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;


public class DefaultGetResolverTest {
    @Test
    public void rawQuery() {
        final StorIOSQLite storIOSQLite = Mockito.mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = Mockito.mock(LowLevel.class);
        final RawQuery rawQuery = RawQuery.builder().query("test sql").build();
        final Cursor expectedCursor = Mockito.mock(Cursor.class);
        Mockito.when(storIOSQLite.lowLevel()).thenReturn(lowLevel);
        Mockito.when(lowLevel.rawQuery(rawQuery)).thenReturn(expectedCursor);
        final DefaultGetResolver<DefaultGetResolverTest.TestItem> defaultGetResolver = new DefaultGetResolver<DefaultGetResolverTest.TestItem>() {
            @NonNull
            @Override
            public DefaultGetResolverTest.TestItem mapFromCursor(@NonNull
            StorIOSQLite storIOSQLite, @NonNull
            Cursor cursor) {
                return Mockito.mock(DefaultGetResolverTest.TestItem.class);
            }
        };
        final Cursor actualCursor = defaultGetResolver.performGet(storIOSQLite, rawQuery);
        // only one request should occur
        Mockito.verify(lowLevel, Mockito.times(1)).rawQuery(any(RawQuery.class));
        // and this request should be equals to original
        Mockito.verify(lowLevel, Mockito.times(1)).rawQuery(rawQuery);
        assertThat(actualCursor).isSameAs(expectedCursor);
    }

    @Test
    public void query() {
        final StorIOSQLite storIOSQLite = Mockito.mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = Mockito.mock(LowLevel.class);
        final Query query = Query.builder().table("test_table").build();
        final Cursor expectedCursor = Mockito.mock(Cursor.class);
        Mockito.when(storIOSQLite.lowLevel()).thenReturn(lowLevel);
        Mockito.when(lowLevel.query(query)).thenReturn(expectedCursor);
        final DefaultGetResolver<DefaultGetResolverTest.TestItem> defaultGetResolver = new DefaultGetResolver<DefaultGetResolverTest.TestItem>() {
            @NonNull
            @Override
            public DefaultGetResolverTest.TestItem mapFromCursor(@NonNull
            StorIOSQLite storIOSQLite, @NonNull
            Cursor cursor) {
                return Mockito.mock(DefaultGetResolverTest.TestItem.class);
            }
        };
        final Cursor actualCursor = defaultGetResolver.performGet(storIOSQLite, query);
        // only one request should occur
        Mockito.verify(lowLevel, Mockito.times(1)).query(org.mockito.ArgumentMatchers.any(Query.class));
        // and this request should be equals to original
        Mockito.verify(lowLevel, Mockito.times(1)).query(query);
        assertThat(actualCursor).isSameAs(expectedCursor);
    }

    private static class TestItem {}
}

