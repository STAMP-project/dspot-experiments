/**
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.room;


import RoomDatabase.JournalMode.TRUNCATE;
import RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING;
import RoomDatabase.MigrationContainer;
import SupportSQLiteOpenHelper.Factory;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
@RunWith(JUnit4.class)
public class BuilderTest {
    @Test(expected = IllegalArgumentException.class)
    public void nullContext() {
        // noinspection ConstantConditions
        Room.databaseBuilder(null, RoomDatabase.class, "bla").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullContext2() {
        // noinspection ConstantConditions
        Room.inMemoryDatabaseBuilder(null, RoomDatabase.class).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullName() {
        // noinspection ConstantConditions
        Room.databaseBuilder(Mockito.mock(Context.class), RoomDatabase.class, null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyName() {
        Room.databaseBuilder(Mockito.mock(Context.class), RoomDatabase.class, "  ").build();
    }

    @Test
    public void migration() {
        Migration m1 = new BuilderTest.EmptyMigration(0, 1);
        Migration m2 = new BuilderTest.EmptyMigration(1, 2);
        BuilderTest.TestDatabase db = Room.databaseBuilder(Mockito.mock(Context.class), BuilderTest.TestDatabase.class, "foo").addMigrations(m1, m2).build();
        DatabaseConfiguration config = ((BuilderTest_TestDatabase_Impl) (db)).mConfig;
        RoomDatabase.MigrationContainer migrations = config.migrationContainer;
        MatcherAssert.assertThat(migrations.findMigrationPath(0, 1), CoreMatchers.is(Arrays.asList(m1)));
        MatcherAssert.assertThat(migrations.findMigrationPath(1, 2), CoreMatchers.is(Arrays.asList(m2)));
        MatcherAssert.assertThat(migrations.findMigrationPath(0, 2), CoreMatchers.is(Arrays.asList(m1, m2)));
        MatcherAssert.assertThat(migrations.findMigrationPath(2, 0), CoreMatchers.<List<Migration>>nullValue());
        MatcherAssert.assertThat(migrations.findMigrationPath(0, 3), CoreMatchers.<List<Migration>>nullValue());
    }

    @Test
    public void migrationOverride() {
        Migration m1 = new BuilderTest.EmptyMigration(0, 1);
        Migration m2 = new BuilderTest.EmptyMigration(1, 2);
        Migration m3 = new BuilderTest.EmptyMigration(0, 1);
        BuilderTest.TestDatabase db = Room.databaseBuilder(Mockito.mock(Context.class), BuilderTest.TestDatabase.class, "foo").addMigrations(m1, m2, m3).build();
        DatabaseConfiguration config = ((BuilderTest_TestDatabase_Impl) (db)).mConfig;
        RoomDatabase.MigrationContainer migrations = config.migrationContainer;
        MatcherAssert.assertThat(migrations.findMigrationPath(0, 1), CoreMatchers.is(Arrays.asList(m3)));
        MatcherAssert.assertThat(migrations.findMigrationPath(1, 2), CoreMatchers.is(Arrays.asList(m2)));
        MatcherAssert.assertThat(migrations.findMigrationPath(0, 3), CoreMatchers.<List<Migration>>nullValue());
    }

    @Test
    public void migrationJump() {
        Migration m1 = new BuilderTest.EmptyMigration(0, 1);
        Migration m2 = new BuilderTest.EmptyMigration(1, 2);
        Migration m3 = new BuilderTest.EmptyMigration(2, 3);
        Migration m4 = new BuilderTest.EmptyMigration(0, 3);
        BuilderTest.TestDatabase db = Room.databaseBuilder(Mockito.mock(Context.class), BuilderTest.TestDatabase.class, "foo").addMigrations(m1, m2, m3, m4).build();
        DatabaseConfiguration config = ((BuilderTest_TestDatabase_Impl) (db)).mConfig;
        RoomDatabase.MigrationContainer migrations = config.migrationContainer;
        MatcherAssert.assertThat(migrations.findMigrationPath(0, 3), CoreMatchers.is(Arrays.asList(m4)));
        MatcherAssert.assertThat(migrations.findMigrationPath(1, 3), CoreMatchers.is(Arrays.asList(m2, m3)));
    }

    @Test
    public void migrationDowngrade() {
        Migration m1_2 = new BuilderTest.EmptyMigration(1, 2);
        Migration m2_3 = new BuilderTest.EmptyMigration(2, 3);
        Migration m3_4 = new BuilderTest.EmptyMigration(3, 4);
        Migration m3_2 = new BuilderTest.EmptyMigration(3, 2);
        Migration m2_1 = new BuilderTest.EmptyMigration(2, 1);
        BuilderTest.TestDatabase db = Room.databaseBuilder(Mockito.mock(Context.class), BuilderTest.TestDatabase.class, "foo").addMigrations(m1_2, m2_3, m3_4, m3_2, m2_1).build();
        DatabaseConfiguration config = ((BuilderTest_TestDatabase_Impl) (db)).mConfig;
        RoomDatabase.MigrationContainer migrations = config.migrationContainer;
        MatcherAssert.assertThat(migrations.findMigrationPath(3, 2), CoreMatchers.is(Arrays.asList(m3_2)));
        MatcherAssert.assertThat(migrations.findMigrationPath(3, 1), CoreMatchers.is(Arrays.asList(m3_2, m2_1)));
    }

    @Test
    public void skipMigration() {
        Context context = Mockito.mock(Context.class);
        BuilderTest.TestDatabase db = Room.inMemoryDatabaseBuilder(context, BuilderTest.TestDatabase.class).fallbackToDestructiveMigration().build();
        DatabaseConfiguration config = ((BuilderTest_TestDatabase_Impl) (db)).mConfig;
        MatcherAssert.assertThat(config.requireMigration, CoreMatchers.is(false));
    }

    @Test
    public void fallbackToDestructiveMigrationFrom_calledOnce_migrationsNotRequiredForValues() {
        Context context = Mockito.mock(Context.class);
        BuilderTest.TestDatabase db = Room.inMemoryDatabaseBuilder(context, BuilderTest.TestDatabase.class).fallbackToDestructiveMigrationFrom(1, 2).build();
        DatabaseConfiguration config = ((BuilderTest_TestDatabase_Impl) (db)).mConfig;
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(1), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(2), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(1, 2), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(2, 3), CoreMatchers.is(false));
    }

    @Test
    public void fallbackToDestructiveMigrationFrom_calledTwice_migrationsNotRequiredForValues() {
        Context context = Mockito.mock(Context.class);
        BuilderTest.TestDatabase db = Room.inMemoryDatabaseBuilder(context, BuilderTest.TestDatabase.class).fallbackToDestructiveMigrationFrom(1, 2).fallbackToDestructiveMigrationFrom(3, 4).build();
        DatabaseConfiguration config = ((BuilderTest_TestDatabase_Impl) (db)).mConfig;
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(1), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(2), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(3), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(4), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(1, 2), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(2, 3), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(3, 4), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(4, 5), CoreMatchers.is(false));
    }

    @Test
    public void isMigrationRequiredFrom_fallBackToDestructiveCalled_alwaysReturnsFalse() {
        Context context = Mockito.mock(Context.class);
        BuilderTest.TestDatabase db = Room.inMemoryDatabaseBuilder(context, BuilderTest.TestDatabase.class).fallbackToDestructiveMigration().build();
        DatabaseConfiguration config = ((BuilderTest_TestDatabase_Impl) (db)).mConfig;
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(0), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(1), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(5), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(12), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(132), CoreMatchers.is(false));
        // Upgrades
        MatcherAssert.assertThat(config.isMigrationRequired(0, 1), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(1, 2), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(5, 6), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(7, 12), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(132, 150), CoreMatchers.is(false));
        // Downgrades
        MatcherAssert.assertThat(config.isMigrationRequired(1, 0), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(2, 1), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(6, 5), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(7, 12), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(150, 132), CoreMatchers.is(false));
    }

    @Test
    public void isMigrationRequired_destructiveMigrationOnDowngrade_returnTrueWhenUpgrading() {
        Context context = Mockito.mock(Context.class);
        BuilderTest.TestDatabase db = Room.inMemoryDatabaseBuilder(context, BuilderTest.TestDatabase.class).fallbackToDestructiveMigrationOnDowngrade().build();
        DatabaseConfiguration config = ((BuilderTest_TestDatabase_Impl) (db)).mConfig;
        // isMigrationRequiredFrom doesn't know about downgrade only so it always returns true
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(0), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(1), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(5), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(12), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(132), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequired(0, 1), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequired(1, 2), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequired(5, 6), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequired(7, 12), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequired(132, 150), CoreMatchers.is(true));
    }

    @Test
    public void isMigrationRequired_destructiveMigrationOnDowngrade_returnFalseWhenDowngrading() {
        Context context = Mockito.mock(Context.class);
        BuilderTest.TestDatabase db = Room.inMemoryDatabaseBuilder(context, BuilderTest.TestDatabase.class).fallbackToDestructiveMigrationOnDowngrade().build();
        DatabaseConfiguration config = ((BuilderTest_TestDatabase_Impl) (db)).mConfig;
        // isMigrationRequiredFrom doesn't know about downgrade only so it always returns true
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(0), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(1), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(5), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(12), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(132), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequired(1, 0), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(2, 1), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(6, 5), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(12, 7), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(150, 132), CoreMatchers.is(false));
    }

    @Test
    public void isMigrationRequiredFrom_byDefault_alwaysReturnsTrue() {
        Context context = Mockito.mock(Context.class);
        BuilderTest.TestDatabase db = Room.inMemoryDatabaseBuilder(context, BuilderTest.TestDatabase.class).build();
        DatabaseConfiguration config = ((BuilderTest_TestDatabase_Impl) (db)).mConfig;
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(0), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(1), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(5), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(12), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(132), CoreMatchers.is(true));
        // Upgrades
        MatcherAssert.assertThat(config.isMigrationRequired(0, 1), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequired(1, 2), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequired(5, 6), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequired(7, 12), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequired(132, 150), CoreMatchers.is(true));
        // Downgrades
        MatcherAssert.assertThat(config.isMigrationRequired(1, 0), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequired(2, 1), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequired(6, 5), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequired(7, 12), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequired(150, 132), CoreMatchers.is(true));
    }

    @Test
    public void isMigrationRequiredFrom_fallBackToDestFromCalled_falseForProvidedValues() {
        Context context = Mockito.mock(Context.class);
        BuilderTest.TestDatabase db = Room.inMemoryDatabaseBuilder(context, BuilderTest.TestDatabase.class).fallbackToDestructiveMigrationFrom(1, 4, 81).build();
        DatabaseConfiguration config = ((BuilderTest_TestDatabase_Impl) (db)).mConfig;
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(1), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(4), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(81), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(1, 2), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(4, 8), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(81, 90), CoreMatchers.is(false));
    }

    @Test
    public void isMigrationRequiredFrom_fallBackToDestFromCalled_trueForNonProvidedValues() {
        Context context = Mockito.mock(Context.class);
        BuilderTest.TestDatabase db = Room.inMemoryDatabaseBuilder(context, BuilderTest.TestDatabase.class).fallbackToDestructiveMigrationFrom(1, 4, 81).build();
        DatabaseConfiguration config = ((BuilderTest_TestDatabase_Impl) (db)).mConfig;
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(2), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(3), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequiredFrom(73), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequired(2, 3), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequired(3, 4), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequired(73, 80), CoreMatchers.is(true));
    }

    @Test
    public void fallbackToDestructiveMigrationOnDowngrade_withProvidedValues_falseForDowngrades() {
        Context context = Mockito.mock(Context.class);
        BuilderTest.TestDatabase db = Room.inMemoryDatabaseBuilder(context, BuilderTest.TestDatabase.class).fallbackToDestructiveMigrationOnDowngrade().fallbackToDestructiveMigrationFrom(2, 4).build();
        DatabaseConfiguration config = ((BuilderTest_TestDatabase_Impl) (db)).mConfig;
        MatcherAssert.assertThat(config.isMigrationRequired(1, 2), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequired(2, 3), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(3, 4), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequired(4, 5), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(5, 6), CoreMatchers.is(true));
        MatcherAssert.assertThat(config.isMigrationRequired(2, 1), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(3, 2), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(4, 3), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(5, 4), CoreMatchers.is(false));
        MatcherAssert.assertThat(config.isMigrationRequired(6, 5), CoreMatchers.is(false));
    }

    @Test
    public void createBasic() {
        Context context = Mockito.mock(Context.class);
        BuilderTest.TestDatabase db = Room.inMemoryDatabaseBuilder(context, BuilderTest.TestDatabase.class).build();
        MatcherAssert.assertThat(db, CoreMatchers.instanceOf(BuilderTest_TestDatabase_Impl.class));
        DatabaseConfiguration config = ((BuilderTest_TestDatabase_Impl) (db)).mConfig;
        MatcherAssert.assertThat(config, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(config.context, CoreMatchers.is(context));
        MatcherAssert.assertThat(config.name, CoreMatchers.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(config.allowMainThreadQueries, CoreMatchers.is(false));
        MatcherAssert.assertThat(config.journalMode, CoreMatchers.is(TRUNCATE));
        MatcherAssert.assertThat(config.sqliteOpenHelperFactory, CoreMatchers.instanceOf(FrameworkSQLiteOpenHelperFactory.class));
    }

    @Test
    public void createAllowMainThread() {
        Context context = Mockito.mock(Context.class);
        BuilderTest.TestDatabase db = Room.inMemoryDatabaseBuilder(context, BuilderTest.TestDatabase.class).allowMainThreadQueries().build();
        DatabaseConfiguration config = ((BuilderTest_TestDatabase_Impl) (db)).mConfig;
        MatcherAssert.assertThat(config.allowMainThreadQueries, CoreMatchers.is(true));
    }

    @Test
    public void createWriteAheadLogging() {
        Context context = Mockito.mock(Context.class);
        BuilderTest.TestDatabase db = Room.databaseBuilder(context, BuilderTest.TestDatabase.class, "foo").setJournalMode(WRITE_AHEAD_LOGGING).build();
        MatcherAssert.assertThat(db, CoreMatchers.instanceOf(BuilderTest_TestDatabase_Impl.class));
        DatabaseConfiguration config = ((BuilderTest_TestDatabase_Impl) (db)).mConfig;
        MatcherAssert.assertThat(config.journalMode, CoreMatchers.is(WRITE_AHEAD_LOGGING));
    }

    @Test
    public void createWithFactoryAndVersion() {
        Context context = Mockito.mock(Context.class);
        SupportSQLiteOpenHelper.Factory factory = Mockito.mock(Factory.class);
        BuilderTest.TestDatabase db = Room.inMemoryDatabaseBuilder(context, BuilderTest.TestDatabase.class).openHelperFactory(factory).build();
        MatcherAssert.assertThat(db, CoreMatchers.instanceOf(BuilderTest_TestDatabase_Impl.class));
        DatabaseConfiguration config = ((BuilderTest_TestDatabase_Impl) (db)).mConfig;
        MatcherAssert.assertThat(config, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(config.sqliteOpenHelperFactory, CoreMatchers.is(factory));
    }

    abstract static class TestDatabase extends RoomDatabase {}

    static class EmptyMigration extends Migration {
        EmptyMigration(int start, int end) {
            super(start, end);
        }

        @Override
        public void migrate(@NonNull
        SupportSQLiteDatabase database) {
        }
    }
}

