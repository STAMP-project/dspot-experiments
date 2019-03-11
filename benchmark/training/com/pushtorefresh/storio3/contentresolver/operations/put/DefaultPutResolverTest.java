package com.pushtorefresh.storio3.contentresolver.operations.put;


import StorIOContentResolver.LowLevel;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pushtorefresh.storio3.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio3.contentresolver.queries.InsertQuery;
import com.pushtorefresh.storio3.contentresolver.queries.Query;
import com.pushtorefresh.storio3.contentresolver.queries.UpdateQuery;
import io.reactivex.functions.Function;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DefaultPutResolverTest {
    /**
     * Verifies behavior of {@link DefaultPutResolver} for "insert"
     */
    @Test
    public void insert() throws Exception {
        final StorIOContentResolver storIOContentResolver = Mockito.mock(StorIOContentResolver.class);
        final StorIOContentResolver.LowLevel lowLevel = Mockito.mock(LowLevel.class);
        final DefaultPutResolverTest.TestItem testItem = new DefaultPutResolverTest.TestItem(null);// item without id, should be inserted

        Mockito.when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);
        final Uri expectedInsertedUri = Mockito.mock(Uri.class);
        final Query expectedQuery = Query.builder().uri(DefaultPutResolverTest.TestItem.CONTENT_URI).where(((DefaultPutResolverTest.TestItem.COLUMN_ID) + " = ?")).whereArgs(testItem.getId()).build();
        final Cursor cursor = Mockito.mock(Cursor.class);
        Mockito.when(lowLevel.query(ArgumentMatchers.eq(expectedQuery))).thenReturn(cursor);
        Mockito.when(cursor.getCount()).thenReturn(0);// No results -> insert should be performed

        Mockito.when(lowLevel.insert(ArgumentMatchers.any(InsertQuery.class), ArgumentMatchers.any(ContentValues.class))).thenReturn(expectedInsertedUri);
        final InsertQuery expectedInsertQuery = InsertQuery.builder().uri(DefaultPutResolverTest.TestItem.CONTENT_URI).build();
        final PutResolver<DefaultPutResolverTest.TestItem> putResolver = new DefaultPutResolver<DefaultPutResolverTest.TestItem>() {
            @NonNull
            @Override
            protected InsertQuery mapToInsertQuery(@NonNull
            DefaultPutResolverTest.TestItem object) {
                return expectedInsertQuery;
            }

            @NonNull
            @Override
            protected UpdateQuery mapToUpdateQuery(@NonNull
            DefaultPutResolverTest.TestItem object) {
                return UpdateQuery.builder().uri(DefaultPutResolverTest.TestItem.CONTENT_URI).where(((DefaultPutResolverTest.TestItem.COLUMN_ID) + " = ?")).whereArgs(object.getId()).build();
            }

            @NonNull
            @Override
            protected ContentValues mapToContentValues(@NonNull
            DefaultPutResolverTest.TestItem object) {
                try {
                    return DefaultPutResolverTest.TestItem.MAP_TO_CONTENT_VALUES.apply(object);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        final ContentValues expectedContentValues = DefaultPutResolverTest.TestItem.MAP_TO_CONTENT_VALUES.apply(testItem);
        // Performing Put that should "insert"
        final PutResult putResult = putResolver.performPut(storIOContentResolver, testItem);
        // checks that it asks db for results
        Mockito.verify(lowLevel).query(ArgumentMatchers.eq(expectedQuery));
        // checks that cursor was closed
        Mockito.verify(cursor).close();
        // only one query should occur
        Mockito.verify(lowLevel).query(ArgumentMatchers.any(Query.class));
        // checks that required insert was performed
        Mockito.verify(lowLevel).insert(ArgumentMatchers.eq(expectedInsertQuery), ArgumentMatchers.eq(expectedContentValues));
        // only one insert should occur
        Mockito.verify(lowLevel).insert(ArgumentMatchers.any(InsertQuery.class), ArgumentMatchers.any(ContentValues.class));
        // no updates should occur
        Mockito.verify(lowLevel, Mockito.never()).update(ArgumentMatchers.any(UpdateQuery.class), ArgumentMatchers.any(ContentValues.class));
        // put result checks
        assertThat(putResult.wasInserted()).isTrue();
        assertThat(putResult.wasUpdated()).isFalse();
        assertThat(putResult.insertedUri()).isEqualTo(expectedInsertedUri);
        assertThat(putResult.numberOfRowsUpdated()).isNull();
    }

    /**
     * Verifies behavior of {@link DefaultPutResolver} for "update"
     */
    @Test
    public void update() throws Exception {
        final StorIOContentResolver storIOContentResolver = Mockito.mock(StorIOContentResolver.class);
        final StorIOContentResolver.LowLevel lowLevel = Mockito.mock(LowLevel.class);
        final DefaultPutResolverTest.TestItem testItem = new DefaultPutResolverTest.TestItem(1L);// item with some id, should be updated

        Mockito.when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);
        final Query expectedQuery = Query.builder().uri(DefaultPutResolverTest.TestItem.CONTENT_URI).where(((DefaultPutResolverTest.TestItem.COLUMN_ID) + " = ?")).whereArgs(testItem.getId()).build();
        final Cursor cursor = Mockito.mock(Cursor.class);
        Mockito.when(lowLevel.query(ArgumentMatchers.eq(expectedQuery))).thenReturn(cursor);
        Mockito.when(cursor.getCount()).thenReturn(1);// Some rows already in db -> update should be performed

        final Integer expectedNumberOfRowsUpdated = 1;
        Mockito.when(lowLevel.update(ArgumentMatchers.any(UpdateQuery.class), ArgumentMatchers.any(ContentValues.class))).thenReturn(expectedNumberOfRowsUpdated);
        final UpdateQuery expectedUpdateQuery = UpdateQuery.builder().uri(DefaultPutResolverTest.TestItem.CONTENT_URI).where(((DefaultPutResolverTest.TestItem.COLUMN_ID) + " = ?")).whereArgs(testItem.getId()).build();
        final PutResolver<DefaultPutResolverTest.TestItem> putResolver = new DefaultPutResolver<DefaultPutResolverTest.TestItem>() {
            @NonNull
            @Override
            protected InsertQuery mapToInsertQuery(@NonNull
            DefaultPutResolverTest.TestItem object) {
                fail("Should not be called");
                return null;
            }

            @NonNull
            @Override
            protected UpdateQuery mapToUpdateQuery(@NonNull
            DefaultPutResolverTest.TestItem object) {
                return UpdateQuery.builder().uri(DefaultPutResolverTest.TestItem.CONTENT_URI).where(((DefaultPutResolverTest.TestItem.COLUMN_ID) + " = ?")).whereArgs(object.getId()).build();
            }

            @NonNull
            @Override
            protected ContentValues mapToContentValues(@NonNull
            DefaultPutResolverTest.TestItem object) {
                try {
                    return DefaultPutResolverTest.TestItem.MAP_TO_CONTENT_VALUES.apply(object);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        final ContentValues expectedContentValues = DefaultPutResolverTest.TestItem.MAP_TO_CONTENT_VALUES.apply(testItem);
        // Performing Put that should "update"
        final PutResult putResult = putResolver.performPut(storIOContentResolver, testItem);
        // checks that it asks db for results
        Mockito.verify(lowLevel, Mockito.times(1)).query(ArgumentMatchers.eq(expectedQuery));
        // checks that cursor was closed
        Mockito.verify(cursor, Mockito.times(1)).close();
        // only one query should occur
        Mockito.verify(lowLevel, Mockito.times(1)).query(ArgumentMatchers.any(Query.class));
        // checks that required update was performed
        Mockito.verify(lowLevel, Mockito.times(1)).update(ArgumentMatchers.eq(expectedUpdateQuery), ArgumentMatchers.eq(expectedContentValues));
        // only one update should occur
        Mockito.verify(lowLevel, Mockito.times(1)).update(ArgumentMatchers.any(UpdateQuery.class), ArgumentMatchers.any(ContentValues.class));
        // no inserts should occur
        Mockito.verify(lowLevel, Mockito.times(0)).insert(ArgumentMatchers.any(InsertQuery.class), ArgumentMatchers.any(ContentValues.class));
        // put result checks
        assertThat(putResult.wasInserted()).isFalse();
        assertThat(putResult.wasUpdated()).isTrue();
        assertThat(putResult.numberOfRowsUpdated()).isEqualTo(expectedNumberOfRowsUpdated);
        assertThat(putResult.insertedUri()).isNull();
    }

    @Test
    public void shouldCloseCursorIfUpdateThrowsException() {
        final StorIOContentResolver storIOContentResolver = Mockito.mock(StorIOContentResolver.class);
        final StorIOContentResolver.LowLevel lowLevel = Mockito.mock(LowLevel.class);
        Mockito.when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);
        final DefaultPutResolverTest.TestItem testItem = new DefaultPutResolverTest.TestItem(1L);// item with some id, should be updated

        final Query expectedQuery = Query.builder().uri(DefaultPutResolverTest.TestItem.CONTENT_URI).where(((DefaultPutResolverTest.TestItem.COLUMN_ID) + " = ?")).whereArgs(testItem.getId()).build();
        final Cursor cursor = Mockito.mock(Cursor.class);
        Mockito.when(lowLevel.query(ArgumentMatchers.eq(expectedQuery))).thenReturn(cursor);
        Mockito.when(cursor.getCount()).thenReturn(1);// One result -> update should be performed

        final UpdateQuery expectedUpdateQuery = UpdateQuery.builder().uri(DefaultPutResolverTest.TestItem.CONTENT_URI).where(((DefaultPutResolverTest.TestItem.COLUMN_ID) + " = ?")).whereArgs(testItem.getId()).build();
        Mockito.when(lowLevel.update(ArgumentMatchers.eq(expectedUpdateQuery), ArgumentMatchers.any(ContentValues.class))).thenThrow(new IllegalStateException("Fake exception from ContentResolver"));
        final PutResolver<DefaultPutResolverTest.TestItem> putResolver = new DefaultPutResolver<DefaultPutResolverTest.TestItem>() {
            @NonNull
            @Override
            protected InsertQuery mapToInsertQuery(@NonNull
            DefaultPutResolverTest.TestItem object) {
                return InsertQuery.builder().uri(DefaultPutResolverTest.TestItem.CONTENT_URI).build();
            }

            @NonNull
            @Override
            protected UpdateQuery mapToUpdateQuery(@NonNull
            DefaultPutResolverTest.TestItem object) {
                return UpdateQuery.builder().uri(DefaultPutResolverTest.TestItem.CONTENT_URI).where(((DefaultPutResolverTest.TestItem.COLUMN_ID) + " = ?")).whereArgs(object.getId()).build();
            }

            @NonNull
            @Override
            protected ContentValues mapToContentValues(@NonNull
            DefaultPutResolverTest.TestItem object) {
                try {
                    return DefaultPutResolverTest.TestItem.MAP_TO_CONTENT_VALUES.apply(object);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        try {
            putResolver.performPut(storIOContentResolver, testItem);
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("Fake exception from ContentResolver");
            Mockito.verify(storIOContentResolver).lowLevel();
            // Checks that it asks actual ContentResolver for results
            Mockito.verify(lowLevel).query(ArgumentMatchers.eq(expectedQuery));
            Mockito.verify(cursor).getCount();
            // Cursor must be closed in case of exception!
            Mockito.verify(cursor).close();
            Mockito.verify(lowLevel).update(ArgumentMatchers.eq(expectedUpdateQuery), ArgumentMatchers.any(ContentValues.class));
            Mockito.verifyNoMoreInteractions(storIOContentResolver, lowLevel, cursor);
        }
    }

    @Test
    public void shouldCloseCursorIfInsertThrowsException() {
        final StorIOContentResolver storIOContentResolver = Mockito.mock(StorIOContentResolver.class);
        final StorIOContentResolver.LowLevel lowLevel = Mockito.mock(LowLevel.class);
        Mockito.when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);
        final DefaultPutResolverTest.TestItem testItem = new DefaultPutResolverTest.TestItem(null);// item without id, should be inserted

        final Query expectedQuery = Query.builder().uri(DefaultPutResolverTest.TestItem.CONTENT_URI).where(((DefaultPutResolverTest.TestItem.COLUMN_ID) + " = ?")).whereArgs(testItem.getId()).build();
        final Cursor cursor = Mockito.mock(Cursor.class);
        Mockito.when(lowLevel.query(ArgumentMatchers.eq(expectedQuery))).thenReturn(cursor);
        Mockito.when(cursor.getCount()).thenReturn(0);// No results -> insert should be performed

        final InsertQuery expectedInsertQuery = InsertQuery.builder().uri(DefaultPutResolverTest.TestItem.CONTENT_URI).build();
        Mockito.when(lowLevel.insert(ArgumentMatchers.eq(expectedInsertQuery), ArgumentMatchers.any(ContentValues.class))).thenThrow(new IllegalStateException("Fake exception from ContentResolver"));
        final PutResolver<DefaultPutResolverTest.TestItem> putResolver = new DefaultPutResolver<DefaultPutResolverTest.TestItem>() {
            @NonNull
            @Override
            protected InsertQuery mapToInsertQuery(@NonNull
            DefaultPutResolverTest.TestItem object) {
                return InsertQuery.builder().uri(DefaultPutResolverTest.TestItem.CONTENT_URI).build();
            }

            @NonNull
            @Override
            protected UpdateQuery mapToUpdateQuery(@NonNull
            DefaultPutResolverTest.TestItem object) {
                return UpdateQuery.builder().uri(DefaultPutResolverTest.TestItem.CONTENT_URI).where(((DefaultPutResolverTest.TestItem.COLUMN_ID) + " = ?")).whereArgs(object.getId()).build();
            }

            @NonNull
            @Override
            protected ContentValues mapToContentValues(@NonNull
            DefaultPutResolverTest.TestItem object) {
                try {
                    return DefaultPutResolverTest.TestItem.MAP_TO_CONTENT_VALUES.apply(object);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        try {
            putResolver.performPut(storIOContentResolver, testItem);
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("Fake exception from ContentResolver");
            Mockito.verify(storIOContentResolver).lowLevel();
            // Checks that it asks actual ContentResolver for results
            Mockito.verify(lowLevel).query(ArgumentMatchers.eq(expectedQuery));
            Mockito.verify(cursor).getCount();
            // Cursor must be closed in case of exception!
            Mockito.verify(cursor).close();
            Mockito.verify(lowLevel).insert(ArgumentMatchers.eq(expectedInsertQuery), ArgumentMatchers.any(ContentValues.class));
            Mockito.verifyNoMoreInteractions(storIOContentResolver, lowLevel, cursor);
        }
    }

    private static class TestItem {
        @NonNull
        static final Uri CONTENT_URI = Mockito.mock(Uri.class);

        @NonNull
        static final String COLUMN_ID = "customIdColumnName";

        @Nullable
        private final Long id;

        @NonNull
        static final Function<DefaultPutResolverTest.TestItem, ContentValues> MAP_TO_CONTENT_VALUES = new Function<DefaultPutResolverTest.TestItem, ContentValues>() {
            // ContentValues should be mocked for usage in tests (damn you Android...)
            // but we can not mock equals() method
            // so, we will return SAME ContentValues for object and assertEquals() will pass
            @NonNull
            private final Map<DefaultPutResolverTest.TestItem, ContentValues> map = new HashMap<DefaultPutResolverTest.TestItem, ContentValues>();

            @NonNull
            @Override
            public ContentValues apply(@NonNull
            DefaultPutResolverTest.TestItem testItem) {
                if (map.containsKey(testItem)) {
                    return map.get(testItem);
                } else {
                    final ContentValues contentValues = Mockito.mock(ContentValues.class);
                    Mockito.when(contentValues.get(DefaultPutResolverTest.TestItem.COLUMN_ID)).thenReturn(testItem.id);
                    map.put(testItem, contentValues);// storing pair of mapping

                    return contentValues;
                }
            }
        };

        TestItem(@Nullable
        Long id) {
            this.id = id;
        }

        @Nullable
        Long getId() {
            return id;
        }
    }
}

