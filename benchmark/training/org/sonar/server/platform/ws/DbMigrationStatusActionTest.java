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
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
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
import org.sonar.server.platform.db.migration.DatabaseMigrationState;
import org.sonar.server.platform.db.migration.version.DatabaseVersion;
import org.sonar.server.ws.WsTester;


@RunWith(DataProviderRunner.class)
public class DbMigrationStatusActionTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final Date SOME_DATE = new Date();

    private static final String SOME_THROWABLE_MSG = "blablabla pop !";

    private static final String DEFAULT_ERROR_MSG = "No failure error";

    private DatabaseVersion databaseVersion = Mockito.mock(DatabaseVersion.class);

    private Database database = Mockito.mock(Database.class);

    private Dialect dialect = Mockito.mock(Dialect.class);

    private DatabaseMigrationState migrationState = Mockito.mock(DatabaseMigrationState.class);

    private DbMigrationStatusAction underTest = new DbMigrationStatusAction(databaseVersion, database, migrationState);

    private Request request = Mockito.mock(Request.class);

    private WsTester.TestResponse response = new WsTester.TestResponse();

    @Test
    public void verify_example() throws Exception {
        Mockito.when(dialect.supportsMigration()).thenReturn(true);
        Mockito.when(migrationState.getStatus()).thenReturn(Status.RUNNING);
        Mockito.when(migrationState.getStartedAt()).thenReturn(DateUtils.parseDateTime("2015-02-23T18:54:23+0100"));
        underTest.handle(request, response);
        assertJson(response.outputAsString()).isSimilarTo(getClass().getResource("example-migrate_db.json"));
    }

    @Test
    public void throws_ISE_when_database_has_no_version() throws Exception {
        Mockito.reset(database);
        Mockito.when(databaseVersion.getVersion()).thenReturn(Optional.empty());
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Cannot connect to Database.");
        underTest.handle(request, response);
    }

    @Test
    public void msg_is_operational_and_state_from_databasemigration_when_databaseversion_status_is_UP_TO_DATE() throws Exception {
        Mockito.when(databaseVersion.getStatus()).thenReturn(UP_TO_DATE);
        Mockito.when(migrationState.getStatus()).thenReturn(Status.NONE);
        underTest.handle(request, response);
        assertJson(response.outputAsString()).isSimilarTo(DbMigrationStatusActionTest.expectedResponse(DbMigrationJsonWriter.STATUS_NO_MIGRATION, DbMigrationJsonWriter.MESSAGE_STATUS_NONE));
    }

    @Test
    public void state_from_databasemigration_when_databaseversion_status_is_REQUIRES_DOWNGRADE() throws Exception {
        Mockito.when(databaseVersion.getStatus()).thenReturn(REQUIRES_DOWNGRADE);
        Mockito.when(migrationState.getStatus()).thenReturn(Status.NONE);
        underTest.handle(request, response);
        assertJson(response.outputAsString()).isSimilarTo(DbMigrationStatusActionTest.expectedResponse(DbMigrationJsonWriter.STATUS_NO_MIGRATION, DbMigrationJsonWriter.MESSAGE_STATUS_NONE));
    }
}

