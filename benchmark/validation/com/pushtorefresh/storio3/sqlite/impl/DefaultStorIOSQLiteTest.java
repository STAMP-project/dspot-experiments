package com.pushtorefresh.storio3.sqlite.impl;


import DefaultStorIOSQLite.Builder;
import DefaultStorIOSQLite.CompleteBuilder;
import SQLiteDatabase.CONFLICT_ROLLBACK;
import StorIOSQLite.LowLevel;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pushtorefresh.storio3.Interceptor;
import com.pushtorefresh.storio3.TypeMappingFinder;
import com.pushtorefresh.storio3.internal.ChangesBus;
import com.pushtorefresh.storio3.internal.TypeMappingFinderImpl;
import com.pushtorefresh.storio3.sqlite.Changes;
import com.pushtorefresh.storio3.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio3.sqlite.StorIOSQLite;
import com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResolver;
import com.pushtorefresh.storio3.sqlite.operations.get.GetResolver;
import com.pushtorefresh.storio3.sqlite.operations.put.PutResolver;
import com.pushtorefresh.storio3.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio3.sqlite.queries.RawQuery;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class DefaultStorIOSQLiteTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    @NonNull
    private SQLiteOpenHelper sqLiteOpenHelper;

    @Mock
    @NonNull
    private SQLiteDatabase sqLiteDatabase;

    @NonNull
    private DefaultStorIOSQLite storIOSQLite;

    @Test
    public void nullSQLiteOpenHelper() {
        DefaultStorIOSQLite.Builder builder = DefaultStorIOSQLite.builder();
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Please specify SQLiteOpenHelper instance");
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        builder.sqliteOpenHelper(null);
    }

    @Test
    public void lowLevelReturnsSameInstanceOfSQLiteOpenHelper() {
        assertThat(storIOSQLite.lowLevel().sqliteOpenHelper()).isSameAs(sqLiteOpenHelper);
    }

    @Test
    public void addTypeMappingNullType() {
        DefaultStorIOSQLite.CompleteBuilder builder = DefaultStorIOSQLite.builder().sqliteOpenHelper(sqLiteOpenHelper);
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Please specify type");
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection unchecked,ConstantConditions
        builder.addTypeMapping(null, SQLiteTypeMapping.builder().putResolver(Mockito.mock(PutResolver.class)).getResolver(Mockito.mock(GetResolver.class)).deleteResolver(Mockito.mock(DeleteResolver.class)).build());
    }

    @Test
    public void addTypeMappingNullMapping() {
        DefaultStorIOSQLite.CompleteBuilder builder = DefaultStorIOSQLite.builder().sqliteOpenHelper(sqLiteOpenHelper);
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Please specify type mapping");
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        builder.addTypeMapping(Object.class, null);
    }

    @Test
    public void nullTypeMappingFinder() {
        DefaultStorIOSQLite.CompleteBuilder builder = DefaultStorIOSQLite.builder().sqliteOpenHelper(sqLiteOpenHelper);
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Please specify typeMappingFinder");
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        builder.typeMappingFinder(null);
    }

    @Test
    public void shouldUseSpecifiedTypeMappingFinder() throws IllegalAccessException, NoSuchFieldException {
        TypeMappingFinder typeMappingFinder = Mockito.mock(TypeMappingFinder.class);
        DefaultStorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder().sqliteOpenHelper(sqLiteOpenHelper).typeMappingFinder(typeMappingFinder).build();
        assertThat(DefaultStorIOSQLiteTest.getTypeMappingFinder(storIOSQLite)).isEqualTo(typeMappingFinder);
    }

    @Test
    public void typeMappingShouldWorkWithoutSpecifiedTypeMappingFinder() {
        // noinspection unchecked
        SQLiteTypeMapping<DefaultStorIOSQLiteTest.ClassEntity> typeMapping = SQLiteTypeMapping.builder().putResolver(Mockito.mock(PutResolver.class)).getResolver(Mockito.mock(GetResolver.class)).deleteResolver(Mockito.mock(DeleteResolver.class)).build();
        DefaultStorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder().sqliteOpenHelper(sqLiteOpenHelper).addTypeMapping(DefaultStorIOSQLiteTest.ClassEntity.class, typeMapping).build();
        assertThat(storIOSQLite.lowLevel().typeMapping(DefaultStorIOSQLiteTest.ClassEntity.class)).isEqualTo(typeMapping);
    }

    @Test
    public void typeMappingShouldWorkWithSpecifiedTypeMappingFinder() {
        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        // noinspection unchecked
        SQLiteTypeMapping<DefaultStorIOSQLiteTest.ClassEntity> typeMapping = SQLiteTypeMapping.builder().putResolver(Mockito.mock(PutResolver.class)).getResolver(Mockito.mock(GetResolver.class)).deleteResolver(Mockito.mock(DeleteResolver.class)).build();
        DefaultStorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder().sqliteOpenHelper(sqLiteOpenHelper).typeMappingFinder(typeMappingFinder).addTypeMapping(DefaultStorIOSQLiteTest.ClassEntity.class, typeMapping).build();
        assertThat(storIOSQLite.lowLevel().typeMapping(DefaultStorIOSQLiteTest.ClassEntity.class)).isEqualTo(typeMapping);
    }

    @Test
    public void typeMappingShouldWorkForMultipleTypes() {
        class AnotherEntity {}
        // noinspection unchecked
        SQLiteTypeMapping<DefaultStorIOSQLiteTest.ClassEntity> entityMapping = SQLiteTypeMapping.builder().putResolver(Mockito.mock(PutResolver.class)).getResolver(Mockito.mock(GetResolver.class)).deleteResolver(Mockito.mock(DeleteResolver.class)).build();
        // noinspection unchecked
        SQLiteTypeMapping<AnotherEntity> anotherMapping = SQLiteTypeMapping.builder().putResolver(Mockito.mock(PutResolver.class)).getResolver(Mockito.mock(GetResolver.class)).deleteResolver(Mockito.mock(DeleteResolver.class)).build();
        DefaultStorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder().sqliteOpenHelper(sqLiteOpenHelper).addTypeMapping(DefaultStorIOSQLiteTest.ClassEntity.class, entityMapping).addTypeMapping(AnotherEntity.class, anotherMapping).build();
        assertThat(storIOSQLite.lowLevel().typeMapping(DefaultStorIOSQLiteTest.ClassEntity.class)).isEqualTo(entityMapping);
        assertThat(storIOSQLite.lowLevel().typeMapping(AnotherEntity.class)).isEqualTo(anotherMapping);
    }

    @Test
    public void shouldCloseSQLiteOpenHelper() throws IOException {
        // Should not call close before explicit call to close
        Mockito.verify(sqLiteOpenHelper, Mockito.never()).close();
        storIOSQLite.close();
        // Should call close on SQLiteOpenHelper
        Mockito.verify(sqLiteOpenHelper).close();
    }

    @Test
    public void shouldPassSQLWithArgsToExecSQL() {
        RawQuery rawQuery = RawQuery.builder().query("DROP TABLE users").args("arg1", "arg2").build();
        storIOSQLite.lowLevel().executeSQL(rawQuery);
        Mockito.verify(sqLiteOpenHelper).getWritableDatabase();
        Mockito.verify(sqLiteDatabase).execSQL(ArgumentMatchers.eq(rawQuery.query()), ArgumentMatchers.eq(new String[]{ "arg1", "arg2" }));
        Mockito.verifyNoMoreInteractions(sqLiteOpenHelper, sqLiteDatabase);
    }

    @Test
    public void shouldPassSQLWithoutArgsToExecSQL() {
        RawQuery rawQuery = RawQuery.builder().query("DROP TABLE IF EXISTS someTable").build();// No args!

        storIOSQLite.lowLevel().executeSQL(rawQuery);
        Mockito.verify(sqLiteOpenHelper).getWritableDatabase();
        Mockito.verify(sqLiteDatabase).execSQL(ArgumentMatchers.eq(rawQuery.query()));
        Mockito.verifyNoMoreInteractions(sqLiteOpenHelper, sqLiteDatabase);
    }

    // See https://github.com/pushtorefresh/storio/issues/478
    @Test
    public void nestedTransactionShouldWorkNormally() {
        // External transaction
        storIOSQLite.lowLevel().beginTransaction();
        try {
            try {
                // Nested transaction
                storIOSQLite.lowLevel().beginTransaction();
                storIOSQLite.lowLevel().notifyAboutChanges(Changes.newInstance("table1"));
                storIOSQLite.lowLevel().notifyAboutChanges(Changes.newInstance("table2"));
                // Finishing nested transaction
                storIOSQLite.lowLevel().setTransactionSuccessful();
            } finally {
                storIOSQLite.lowLevel().endTransaction();
            }
            // Marking external transaction as successful
            storIOSQLite.lowLevel().setTransactionSuccessful();
        } finally {
            // Finishing external transaction
            storIOSQLite.lowLevel().endTransaction();
        }
    }

    @Test
    public void shouldPassArgsToInsertWithOnConflict() {
        InsertQuery insertQuery = InsertQuery.builder().table("test_table").nullColumnHack("custom_null_hack").build();
        ContentValues contentValues = Mockito.mock(ContentValues.class);
        int conflictAlgorithm = SQLiteDatabase.CONFLICT_ROLLBACK;
        storIOSQLite.lowLevel().insertWithOnConflict(insertQuery, contentValues, conflictAlgorithm);
        Mockito.verify(sqLiteDatabase).insertWithOnConflict(ArgumentMatchers.eq("test_table"), ArgumentMatchers.eq("custom_null_hack"), ArgumentMatchers.same(contentValues), ArgumentMatchers.eq(CONFLICT_ROLLBACK));
    }

    @Test
    public void notifyAboutChangesShouldNotAcceptNullAsChanges() {
        StorIOSQLite.LowLevel lowLevel = storIOSQLite.lowLevel();
        assertThat(lowLevel).isNotNull();
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Changes can not be null");
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        lowLevel.notifyAboutChanges(null);
    }

    @Test
    public void observeChangesAndNotifyAboutChangesShouldWorkCorrectly() {
        TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();
        storIOSQLite.observeChanges(BackpressureStrategy.LATEST).subscribe(testSubscriber);
        testSubscriber.assertNoValues();
        Changes changes = Changes.newInstance("test_table", "tag");
        storIOSQLite.lowLevel().notifyAboutChanges(changes);
        testSubscriber.assertValue(changes);
        testSubscriber.assertNoErrors();
        testSubscriber.dispose();
    }

    @Test
    public void observeChangesShouldThrowIfRxJavaNotInClassPath() throws IllegalAccessException, NoSuchFieldException {
        // noinspection unchecked
        ChangesBus<Changes> changesBus = Mockito.mock(ChangesBus.class);
        DefaultStorIOSQLiteTest.setChangesBus(storIOSQLite, changesBus);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Observing changes in StorIOSQLite requires RxJava");
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        storIOSQLite.observeChanges(BackpressureStrategy.LATEST);
    }

    @Test
    public void observeChangesInTablesShouldNotAcceptNullAsTables() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Set of tables can not be null");
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        storIOSQLite.observeChangesInTables(null, BackpressureStrategy.LATEST);
    }

    @Test
    public void observeChangesOfTagsShouldNotAcceptNullAsTags() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Set of tags can not be null");
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        storIOSQLite.observeChangesOfTags(null, BackpressureStrategy.LATEST);
    }

    @Test
    public void observeChangesInTables_shouldReceiveIfTableWasChanged() {
        TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();
        Set<String> tables = new HashSet<String>(2);
        tables.add("table1");
        tables.add("table2");
        storIOSQLite.observeChangesInTables(tables, BackpressureStrategy.LATEST).subscribe(testSubscriber);
        testSubscriber.assertNoValues();
        Changes changes = Changes.newInstance("table2");
        storIOSQLite.lowLevel().notifyAboutChanges(changes);
        testSubscriber.assertValues(changes);
        testSubscriber.assertNoErrors();
        testSubscriber.dispose();
    }

    @Test
    public void observeChangesInTables_shouldNotReceiveIfTableWasNotChanged() {
        TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();
        Set<String> tables = new HashSet<String>(2);
        tables.add("table1");
        tables.add("table2");
        storIOSQLite.observeChangesInTables(tables, BackpressureStrategy.LATEST).subscribe(testSubscriber);
        storIOSQLite.lowLevel().notifyAboutChanges(Changes.newInstance("table3"));
        testSubscriber.assertNoValues();
        testSubscriber.assertNoErrors();
        testSubscriber.dispose();
    }

    @Test
    public void observeChangesOfTags_shouldReceiveIfObservedTagExistInChanges() {
        TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();
        String tag1 = "tag1";
        String tag2 = "tag2";
        Set<String> tags = new HashSet<String>(2);
        tags.add(tag1);
        tags.add(tag2);
        storIOSQLite.observeChangesOfTags(tags, BackpressureStrategy.LATEST).subscribe(testSubscriber);
        testSubscriber.assertNoValues();
        Changes changes = Changes.newInstance("table1", tag1);
        storIOSQLite.lowLevel().notifyAboutChanges(changes);
        testSubscriber.assertValues(changes);
        testSubscriber.assertNoErrors();
        testSubscriber.dispose();
    }

    @Test
    public void observeChangesOfTags_shouldNotReceiveIfObservedTagDoesNotExistInChanges() {
        TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();
        Set<String> tags = new HashSet<String>(2);
        tags.add("tag1");
        tags.add("tag2");
        storIOSQLite.observeChangesOfTags(tags, BackpressureStrategy.LATEST).subscribe(testSubscriber);
        storIOSQLite.lowLevel().notifyAboutChanges(Changes.newInstance("table3", "tag3"));
        testSubscriber.assertNoValues();
        testSubscriber.assertNoErrors();
        testSubscriber.dispose();
    }

    @Test
    public void observeChangesInTable_shouldNotAcceptNullAsTable() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Table can not be null or empty");
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        storIOSQLite.observeChangesInTable(null, BackpressureStrategy.LATEST);
    }

    @Test
    public void observeChangeOfTag_shouldNotAcceptNullAsTag() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Tag can not be null or empty");
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        storIOSQLite.observeChangesOfTag(null, BackpressureStrategy.LATEST);
    }

    @Test
    public void observeChangeOfTag_shouldNotAcceptEmptyTag() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Tag can not be null or empty");
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        storIOSQLite.observeChangesOfTag("", BackpressureStrategy.LATEST);
    }

    @Test
    public void observeChangesInTable() {
        TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();
        storIOSQLite.observeChangesInTable("table1", BackpressureStrategy.LATEST).subscribe(testSubscriber);
        testSubscriber.assertNoValues();
        Changes changes1 = Changes.newInstance("table2");
        storIOSQLite.lowLevel().notifyAboutChanges(changes1);
        testSubscriber.assertNoValues();
        Changes changes2 = Changes.newInstance("table1");
        storIOSQLite.lowLevel().notifyAboutChanges(changes2);
        testSubscriber.assertValue(changes2);
        Changes changes3 = Changes.newInstance("table3");
        storIOSQLite.lowLevel().notifyAboutChanges(changes3);
        // Subscriber should not see changes of table2 and table3
        testSubscriber.assertValue(changes2);
        testSubscriber.assertNoErrors();
        testSubscriber.dispose();
    }

    @Test
    public void deprecatedInternalImplShouldReturnSentToConstructorTypeMapping() throws IllegalAccessException, NoSuchFieldException {
        TypeMappingFinder typeMappingFinder = Mockito.mock(TypeMappingFinder.class);
        DefaultStorIOSQLiteTest.TestDefaultStorIOSQLite storIOSQLite = new DefaultStorIOSQLiteTest.TestDefaultStorIOSQLite(sqLiteOpenHelper, typeMappingFinder);
        assertThat(storIOSQLite.typeMappingFinder()).isSameAs(typeMappingFinder);
    }

    @Test
    public void defaultSchedulerReturnsIOSchedulerIfNotSpecified() {
        assertThat(storIOSQLite.defaultRxScheduler()).isSameAs(Schedulers.io());
    }

    @Test
    public void defaultSchedulerReturnsSpecifiedScheduler() {
        Scheduler scheduler = Mockito.mock(Scheduler.class);
        StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder().sqliteOpenHelper(sqLiteOpenHelper).defaultRxScheduler(scheduler).build();
        assertThat(storIOSQLite.defaultRxScheduler()).isSameAs(scheduler);
    }

    @Test
    public void defaultSchedulerReturnsNullIfSpecifiedSchedulerNull() {
        StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder().sqliteOpenHelper(sqLiteOpenHelper).defaultRxScheduler(null).build();
        assertThat(storIOSQLite.defaultRxScheduler()).isNull();
    }

    static class ClassEntity {}

    class TestDefaultStorIOSQLite extends DefaultStorIOSQLite {
        private final LowLevel lowLevel;

        TestDefaultStorIOSQLite(@NonNull
        SQLiteOpenHelper sqLiteOpenHelper, @NonNull
        TypeMappingFinder typeMappingFinder) {
            super(sqLiteOpenHelper, typeMappingFinder, null, Collections.<Interceptor>emptyList());
            lowLevel = new LowLevelImpl(typeMappingFinder);
        }

        @Nullable
        TypeMappingFinder typeMappingFinder() throws IllegalAccessException, NoSuchFieldException {
            Field field = .class.getDeclaredField("typeMappingFinder");
            field.setAccessible(true);
            return ((TypeMappingFinder) (field.get(lowLevel)));
        }
    }
}

