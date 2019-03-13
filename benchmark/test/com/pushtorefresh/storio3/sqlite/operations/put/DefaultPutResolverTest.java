package com.pushtorefresh.storio3.sqlite.operations.put;


import StorIOSQLite.LowLevel;
import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pushtorefresh.storio3.sqlite.StorIOSQLite;
import com.pushtorefresh.storio3.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio3.sqlite.queries.Query;
import com.pushtorefresh.storio3.sqlite.queries.UpdateQuery;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DefaultPutResolverTest {
    /**
     * Verifies behavior of {@link DefaultPutResolver} for "insert"
     */
    @Test
    public void insert() {
        final StorIOSQLite storIOSQLite = Mockito.mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = Mockito.mock(LowLevel.class);
        final DefaultPutResolverTest.TestItem testItem = new DefaultPutResolverTest.TestItem(null);// item without id, should be inserted

        Mockito.when(storIOSQLite.lowLevel()).thenReturn(lowLevel);
        final Long expectedInsertedId = 24L;
        final Query expectedQuery = Query.builder().table(DefaultPutResolverTest.TestItem.TABLE).where(((DefaultPutResolverTest.TestItem.COLUMN_ID) + " = ?")).whereArgs(testItem.getId()).build();
        final Cursor cursor = Mockito.mock(Cursor.class);
        Mockito.when(lowLevel.query(ArgumentMatchers.eq(expectedQuery))).thenReturn(cursor);
        Mockito.when(cursor.getCount()).thenReturn(0);// No results -> insert should be performed

        Mockito.when(lowLevel.insert(ArgumentMatchers.any(InsertQuery.class), ArgumentMatchers.any(ContentValues.class))).thenReturn(expectedInsertedId);
        final Set<String> tags = Collections.singleton("test_tag");
        final InsertQuery expectedInsertQuery = InsertQuery.builder().table(DefaultPutResolverTest.TestItem.TABLE).affectsTags(tags).nullColumnHack(null).build();
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
                return UpdateQuery.builder().table(DefaultPutResolverTest.TestItem.TABLE).where(((DefaultPutResolverTest.TestItem.COLUMN_ID) + " = ?")).whereArgs(object.getId()).build();
            }

            @NonNull
            @Override
            protected ContentValues mapToContentValues(@NonNull
            DefaultPutResolverTest.TestItem object) {
                return DefaultPutResolverTest.TestItem.MAP_TO_CONTENT_VALUES.apply(object);
            }
        };
        final ContentValues expectedContentValues = DefaultPutResolverTest.TestItem.MAP_TO_CONTENT_VALUES.apply(testItem);
        // Performing Put that should "insert"
        final PutResult putResult = putResolver.performPut(storIOSQLite, testItem);
        Mockito.verify(lowLevel).beginTransaction();
        Mockito.verify(lowLevel).setTransactionSuccessful();
        Mockito.verify(lowLevel).endTransaction();
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
        assertThat(putResult.insertedId()).isEqualTo(expectedInsertedId);
        assertThat(putResult.numberOfRowsUpdated()).isNull();
        assertThat(putResult.affectedTables()).containsExactly(DefaultPutResolverTest.TestItem.TABLE);
        assertThat(putResult.affectedTags()).isEqualTo(tags);
    }

    /**
     * Verifies behavior of {@link DefaultPutResolver} for "update"
     */
    @Test
    public void update() {
        final StorIOSQLite storIOSQLite = Mockito.mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = Mockito.mock(LowLevel.class);
        final DefaultPutResolverTest.TestItem testItem = new DefaultPutResolverTest.TestItem(null);// item with some id, should be updated

        Mockito.when(storIOSQLite.lowLevel()).thenReturn(lowLevel);
        final Query expectedQuery = Query.builder().table(DefaultPutResolverTest.TestItem.TABLE).where(((DefaultPutResolverTest.TestItem.COLUMN_ID) + " = ?")).whereArgs(testItem.getId()).build();
        final Cursor cursor = Mockito.mock(Cursor.class);
        Mockito.when(lowLevel.query(ArgumentMatchers.eq(expectedQuery))).thenReturn(cursor);
        Mockito.when(cursor.getCount()).thenReturn(1);// Some rows already in db -> update should be performed

        final Integer expectedNumberOfRowsUpdated = 1;
        Mockito.when(lowLevel.update(ArgumentMatchers.any(UpdateQuery.class), ArgumentMatchers.any(ContentValues.class))).thenReturn(expectedNumberOfRowsUpdated);
        final Set<String> tags = Collections.singleton("test_tag");
        final UpdateQuery expectedUpdateQuery = UpdateQuery.builder().table(DefaultPutResolverTest.TestItem.TABLE).affectsTags(tags).where(((DefaultPutResolverTest.TestItem.COLUMN_ID) + " = ?")).whereArgs(testItem.getId()).build();
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
                return expectedUpdateQuery;
            }

            @NonNull
            @Override
            protected ContentValues mapToContentValues(@NonNull
            DefaultPutResolverTest.TestItem object) {
                return DefaultPutResolverTest.TestItem.MAP_TO_CONTENT_VALUES.apply(object);
            }
        };
        final ContentValues expectedContentValues = DefaultPutResolverTest.TestItem.MAP_TO_CONTENT_VALUES.apply(testItem);
        // Performing Put that should "update"
        final PutResult putResult = putResolver.performPut(storIOSQLite, testItem);
        Mockito.verify(lowLevel).beginTransaction();
        Mockito.verify(lowLevel).setTransactionSuccessful();
        Mockito.verify(lowLevel).endTransaction();
        // checks that it asks db for results
        Mockito.verify(lowLevel).query(ArgumentMatchers.eq(expectedQuery));
        // checks that cursor was closed
        Mockito.verify(cursor).close();
        // only one query should occur
        Mockito.verify(lowLevel).query(ArgumentMatchers.any(Query.class));
        // checks that required update was performed
        Mockito.verify(lowLevel).update(ArgumentMatchers.eq(expectedUpdateQuery), ArgumentMatchers.eq(expectedContentValues));
        // only one update should occur
        Mockito.verify(lowLevel).update(ArgumentMatchers.any(UpdateQuery.class), ArgumentMatchers.any(ContentValues.class));
        // no inserts should occur
        Mockito.verify(lowLevel, Mockito.never()).insert(ArgumentMatchers.any(InsertQuery.class), ArgumentMatchers.any(ContentValues.class));
        // put result checks
        assertThat(putResult.wasInserted()).isFalse();
        assertThat(putResult.wasUpdated()).isTrue();
        assertThat(putResult.numberOfRowsUpdated()).isEqualTo(expectedNumberOfRowsUpdated);
        assertThat(putResult.insertedId()).isNull();
        assertThat(putResult.affectedTables()).containsExactly(DefaultPutResolverTest.TestItem.TABLE);
        assertThat(putResult.affectedTags()).isEqualTo(tags);
    }

    private static class TestItem {
        static final String TABLE = "someTable";

        static final String COLUMN_ID = "customIdColumnName";

        @Nullable
        private final Long id;

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

