package com.pushtorefresh.storio3.contentresolver.operations.get;


import StorIOContentResolver.LowLevel;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.pushtorefresh.storio3.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio3.contentresolver.queries.Query;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;


public class DefaultGetResolverTest {
    @Test
    public void query() {
        final StorIOContentResolver storIOContentResolver = Mockito.mock(StorIOContentResolver.class);
        final StorIOContentResolver.LowLevel lowLevel = Mockito.mock(LowLevel.class);
        final Query query = Query.builder().uri(Mockito.mock(Uri.class)).build();
        final Cursor expectedCursor = Mockito.mock(Cursor.class);
        Mockito.when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);
        Mockito.when(lowLevel.query(query)).thenReturn(expectedCursor);
        final GetResolver<DefaultGetResolverTest.TestItem> defaultGetResolver = new DefaultGetResolver<DefaultGetResolverTest.TestItem>() {
            @NonNull
            @Override
            public DefaultGetResolverTest.TestItem mapFromCursor(@NonNull
            StorIOContentResolver storIOContentResolver, @NonNull
            Cursor cursor) {
                assertThat(cursor).isSameAs(expectedCursor);
                return new DefaultGetResolverTest.TestItem();
            }
        };
        final Cursor actualCursor = defaultGetResolver.performGet(storIOContentResolver, query);
        // only one request should occur
        Mockito.verify(lowLevel, Mockito.times(1)).query(any(Query.class));
        // and this request should be equals to original
        Mockito.verify(lowLevel, Mockito.times(1)).query(query);
        assertThat(actualCursor).isSameAs(expectedCursor);
    }

    private static class TestItem {}
}

