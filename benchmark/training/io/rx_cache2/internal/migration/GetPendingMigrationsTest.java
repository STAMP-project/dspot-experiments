/**
 * Copyright 2015 Victor Albertos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rx_cache2.internal.migration;


import io.reactivex.observers.TestObserver;
import io.rx_cache2.MigrationCache;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class GetPendingMigrationsTest {
    private GetPendingMigrations getPendingMigrationsUT;

    @Test
    public void When_No_Scheme_Migration_Supplied_Then_Retrieve_Empty() {
        getPendingMigrationsUT = new io.rx_cache2.internal.migration.GetPendingMigrations();
        TestObserver<List<MigrationCache>> testObserver = getPendingMigrationsUT.react().test();
        testObserver.awaitTerminalEvent();
        List<MigrationCache> migrations = testObserver.values().get(0);
        Assert.assertThat(migrations.size(), CoreMatchers.is(0));
    }

    @Test
    public void When_Migrations_Supplied_Then_Retrieve_Them() {
        getPendingMigrationsUT = new io.rx_cache2.internal.migration.GetPendingMigrations();
        TestObserver<List<MigrationCache>> testObserver = getPendingMigrationsUT.with(0, migrations()).react().test();
        testObserver.awaitTerminalEvent();
        List<MigrationCache> migrations = testObserver.values().get(0);
        Assert.assertThat(migrations.size(), CoreMatchers.is(1));
    }

    @Test
    public void When_Migrations_Supplied_Are_Sorted_Then_Retrieve_Them_Sorted_By_Version() {
        getPendingMigrationsUT = new io.rx_cache2.internal.migration.GetPendingMigrations();
        TestObserver<List<MigrationCache>> testObserver = getPendingMigrationsUT.with(0, migrationsSorted()).react().test();
        testObserver.awaitTerminalEvent();
        List<MigrationCache> migrations = testObserver.values().get(0);
        Assert.assertThat(migrations.get(0).version(), CoreMatchers.is(1));
        Assert.assertThat(migrations.get(1).version(), CoreMatchers.is(2));
        Assert.assertThat(migrations.get(2).version(), CoreMatchers.is(3));
        Assert.assertThat(migrations.get(3).version(), CoreMatchers.is(4));
    }

    @Test
    public void When_Migrations_Supplied_Are_Not_Sorted_Then_Retrieve_Them_Sorted_By_Version() {
        getPendingMigrationsUT = new io.rx_cache2.internal.migration.GetPendingMigrations();
        TestObserver<List<MigrationCache>> testObserver = getPendingMigrationsUT.with(0, migrationsNoSorted()).react().test();
        testObserver.awaitTerminalEvent();
        List<MigrationCache> migrations = testObserver.values().get(0);
        Assert.assertThat(migrations.get(0).version(), CoreMatchers.is(1));
        Assert.assertThat(migrations.get(1).version(), CoreMatchers.is(2));
        Assert.assertThat(migrations.get(2).version(), CoreMatchers.is(3));
        Assert.assertThat(migrations.get(3).version(), CoreMatchers.is(4));
    }

    @Test
    public void When_Migrations_Supplied_And_Version_Cache_Then_Get_Only_Pending_Migrations() {
        getPendingMigrationsUT = new io.rx_cache2.internal.migration.GetPendingMigrations();
        TestObserver<List<MigrationCache>> testObserver = getPendingMigrationsUT.with(2, migrationsSorted()).react().test();
        testObserver.awaitTerminalEvent();
        List<MigrationCache> migrations = testObserver.values().get(0);
        Assert.assertThat(migrations.get(0).version(), CoreMatchers.is(3));
        Assert.assertThat(migrations.get(1).version(), CoreMatchers.is(4));
        getPendingMigrationsUT = new io.rx_cache2.internal.migration.GetPendingMigrations();
        testObserver = getPendingMigrationsUT.with(0, migrationsSorted()).react().test();
        testObserver.awaitTerminalEvent();
        migrations = testObserver.values().get(0);
        Assert.assertThat(migrations.get(0).version(), CoreMatchers.is(1));
        Assert.assertThat(migrations.get(1).version(), CoreMatchers.is(2));
        Assert.assertThat(migrations.get(2).version(), CoreMatchers.is(3));
        Assert.assertThat(migrations.get(3).version(), CoreMatchers.is(4));
        getPendingMigrationsUT = new io.rx_cache2.internal.migration.GetPendingMigrations();
        testObserver = getPendingMigrationsUT.with(4, migrationsSorted()).react().test();
        testObserver.awaitTerminalEvent();
        migrations = testObserver.values().get(0);
        Assert.assertThat(migrations.size(), CoreMatchers.is(0));
    }
}

