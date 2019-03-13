/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.platform.ws;


import DatabaseVersion.Status.REQUIRES_DOWNGRADE;
import DatabaseVersion.Status.UP_TO_DATE;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sonar.api.server.ws.Request;
import org.sonar.api.utils.DateUtils;
import org.sonar.db.Database;
import org.sonar.db.dialect.Dialect;
import org.sonar.server.platform.db.migration.DatabaseMigration;
import org.sonar.server.platform.db.migration.DatabaseMigrationState;
import org.sonar.server.platform.db.migration.version.DatabaseVersion;
import org.sonar.server.ws.WsTester;


@RunWith(DataProviderRunner.class)
public class MigrateDbActionTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final Date SOME_DATE = new Date();

    private static final String SOME_THROWABLE_MSG = "blablabla pop !";

    private static final String DEFAULT_ERROR_MSG = "No failure error";

    private static final String STATUS_NO_MIGRATION = "NO_MIGRATION";

    private static final String STATUS_NOT_SUPPORTED = "NOT_SUPPORTED";

    private static final String STATUS_MIGRATION_RUNNING = "MIGRATION_RUNNING";

    private static final String STATUS_MIGRATION_FAILED = "MIGRATION_FAILED";

    private static final String STATUS_MIGRATION_SUCCEEDED = "MIGRATION_SUCCEEDED";

    private static final String MESSAGE_NO_MIGRATION_ON_EMBEDDED_DATABASE = "Upgrade is not supported on embedded database.";

    private static final String MESSAGE_STATUS_NONE = "Database is up-to-date, no migration needed.";

    private static final String MESSAGE_STATUS_RUNNING = "Database migration is running.";

    private static final String MESSAGE_STATUS_SUCCEEDED = "Migration succeeded.";

    private DatabaseVersion databaseVersion = Mockito.mock(DatabaseVersion.class);

    private Database database = Mockito.mock(Database.class);

    private Dialect dialect = Mockito.mock(Dialect.class);

    private DatabaseMigration databaseMigration = Mockito.mock(DatabaseMigration.class);

    private DatabaseMigrationState migrationState = Mockito.mock(DatabaseMigrationState.class);

    private MigrateDbAction underTest = new MigrateDbAction(databaseVersion, database, migrationState, databaseMigration);

    private Request request = Mockito.mock(Request.class);

    private WsTester.TestResponse response = new WsTester.TestResponse();

    @Test
    public void ISE_is_thrown_when_version_can_not_be_retrieved_from_database() throws Exception {
        Mockito.reset(databaseVersion);
        Mockito.when(databaseVersion.getVersion()).thenReturn(Optional.empty());
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Cannot connect to Database.");
        underTest.handle(request, response);
    }

    @Test
    public void verify_example() throws Exception {
        Mockito.when(dialect.supportsMigration()).thenReturn(true);
        Mockito.when(migrationState.getStatus()).thenReturn(Status.RUNNING);
        Mockito.when(migrationState.getStartedAt()).thenReturn(DateUtils.parseDateTime("2015-02-23T18:54:23+0100"));
        underTest.handle(request, response);
        assertJson(response.outputAsString()).isSimilarTo(getClass().getResource("example-migrate_db.json"));
    }

    @Test
    public void msg_is_operational_and_state_from_database_migration_when_databaseversion_status_is_UP_TO_DATE() throws Exception {
        Mockito.when(databaseVersion.getStatus()).thenReturn(UP_TO_DATE);
        Mockito.when(migrationState.getStatus()).thenReturn(Status.NONE);
        underTest.handle(request, response);
        assertJson(response.outputAsString()).isSimilarTo(MigrateDbActionTest.expectedResponse(MigrateDbActionTest.STATUS_NO_MIGRATION, MigrateDbActionTest.MESSAGE_STATUS_NONE));
    }

    // this test will raise a IllegalArgumentException when an unsupported value is added to the Status enum
    @Test
    public void defensive_test_all_values_of_migration_Status_must_be_supported() throws Exception {
        for (org.sonar.server.platform.db.migration.DatabaseMigrationState.Status status : Iterables.filter(Arrays.asList(DatabaseMigrationState.Status.values()), Predicates.not(Predicates.in(ImmutableList.of(Status.NONE, Status.RUNNING, Status.FAILED, Status.SUCCEEDED))))) {
            Mockito.when(migrationState.getStatus()).thenReturn(status);
            underTest.handle(request, response);
        }
    }

    @Test
    public void state_from_database_migration_when_databaseversion_status_is_REQUIRES_DOWNGRADE() throws Exception {
        Mockito.when(databaseVersion.getStatus()).thenReturn(REQUIRES_DOWNGRADE);
        Mockito.when(migrationState.getStatus()).thenReturn(Status.NONE);
        underTest.handle(request, response);
        assertJson(response.outputAsString()).isSimilarTo(MigrateDbActionTest.expectedResponse(MigrateDbActionTest.STATUS_NO_MIGRATION, MigrateDbActionTest.MESSAGE_STATUS_NONE));
    }
}

