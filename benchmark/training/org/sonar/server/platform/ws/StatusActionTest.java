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


import DatabaseMigrationState.Status;
import DatabaseMigrationState.Status.FAILED;
import DatabaseMigrationState.Status.NONE;
import DatabaseMigrationState.Status.RUNNING;
import DatabaseMigrationState.Status.SUCCEEDED;
import Platform.Status.BOOTING;
import Platform.Status.SAFEMODE;
import Platform.Status.STARTING;
import Platform.Status.UP;
import WebService.Action;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.platform.Server;
import org.sonar.api.server.ws.WebService;
import org.sonar.server.app.RestartFlagHolder;
import org.sonar.server.app.RestartFlagHolderImpl;
import org.sonar.server.platform.Platform;
import org.sonar.server.platform.db.migration.DatabaseMigrationState;
import org.sonar.server.ws.WsActionTester;


public class StatusActionTest {
    private static final String SERVER_ID = "20150504120436";

    private static final String SERVER_VERSION = "5.1";

    private static final String STATUS_UP = "UP";

    private static final String STATUS_STARTING = "STARTING";

    private static final String STATUS_DOWN = "DOWN";

    private static final String STATUS_MIGRATION_NEEDED = "DB_MIGRATION_NEEDED";

    private static final String STATUS_MIGRATION_RUNNING = "DB_MIGRATION_RUNNING";

    private static final String STATUS_RESTARTING = "RESTARTING";

    private static final Set<DatabaseMigrationState.Status> SUPPORTED_DATABASE_MIGRATION_STATUSES = ImmutableSet.of(FAILED, NONE, SUCCEEDED, RUNNING);

    private static final Set<Platform.Status> SUPPORTED_PLATFORM_STATUSES = ImmutableSet.of(BOOTING, SAFEMODE, STARTING, UP);

    private static Server server = new StatusActionTest.Dummy51Server();

    private DatabaseMigrationState migrationState = Mockito.mock(DatabaseMigrationState.class);

    private Platform platform = Mockito.mock(Platform.class);

    private RestartFlagHolder restartFlagHolder = new RestartFlagHolderImpl();

    private WsActionTester underTest = new WsActionTester(new StatusAction(StatusActionTest.server, migrationState, platform, restartFlagHolder));

    @Test
    public void action_status_is_defined() {
        WebService.Action action = underTest.getDef();
        assertThat(action.isPost()).isFalse();
        assertThat(action.description()).isNotEmpty();
        assertThat(action.responseExample()).isNotNull();
        assertThat(action.params()).isEmpty();
    }

    @Test
    public void verify_example() {
        Mockito.when(platform.status()).thenReturn(UP);
        restartFlagHolder.unset();
        assertJson(underTest.newRequest().execute().getInput()).isSimilarTo(getClass().getResource("example-status.json"));
    }

    @Test
    public void status_is_UP_if_platform_is_UP_and_restartFlag_is_false_whatever_databaseMigration_status_is() throws Exception {
        for (DatabaseMigrationState.Status databaseMigrationStatus : Status.values()) {
            verifyStatus(UP, databaseMigrationStatus, StatusActionTest.STATUS_UP);
        }
    }

    @Test
    public void status_is_RESTARTING_if_platform_is_UP_and_restartFlag_is_true_whatever_databaseMigration_status_is() throws Exception {
        restartFlagHolder.set();
        for (DatabaseMigrationState.Status databaseMigrationStatus : Status.values()) {
            verifyStatus(UP, databaseMigrationStatus, StatusActionTest.STATUS_RESTARTING);
        }
    }

    @Test
    public void status_is_DOWN_if_platform_is_BOOTING_whatever_databaseMigration_status_is() throws Exception {
        for (DatabaseMigrationState.Status databaseMigrationStatus : Status.values()) {
            verifyStatus(BOOTING, databaseMigrationStatus, StatusActionTest.STATUS_DOWN);
        }
    }

    @Test
    public void status_is_DB_MIGRATION_NEEDED_if_platform_is_SAFEMODE_and_databaseMigration_is_NONE() throws Exception {
        verifyStatus(SAFEMODE, NONE, StatusActionTest.STATUS_MIGRATION_NEEDED);
    }

    @Test
    public void status_is_DB_MIGRATION_RUNNING_if_platform_is_SAFEMODE_and_databaseMigration_is_RUNNING() throws Exception {
        verifyStatus(SAFEMODE, RUNNING, StatusActionTest.STATUS_MIGRATION_RUNNING);
    }

    @Test
    public void status_is_STATUS_STARTING_if_platform_is_SAFEMODE_and_databaseMigration_is_SUCCEEDED() throws Exception {
        verifyStatus(SAFEMODE, SUCCEEDED, StatusActionTest.STATUS_STARTING);
    }

    @Test
    public void status_is_DOWN_if_platform_is_SAFEMODE_and_databaseMigration_is_FAILED() throws Exception {
        verifyStatus(SAFEMODE, FAILED, StatusActionTest.STATUS_DOWN);
    }

    @Test
    public void status_is_STARTING_if_platform_is_STARTING_and_databaseMigration_is_NONE() throws Exception {
        verifyStatus(STARTING, NONE, StatusActionTest.STATUS_STARTING);
    }

    @Test
    public void status_is_DB_MIGRATION_RUNNING_if_platform_is_STARTING_and_databaseMigration_is_RUNNING() throws Exception {
        verifyStatus(STARTING, RUNNING, StatusActionTest.STATUS_MIGRATION_RUNNING);
    }

    @Test
    public void status_is_STARTING_if_platform_is_STARTING_and_databaseMigration_is_SUCCEEDED() throws Exception {
        verifyStatus(STARTING, SUCCEEDED, StatusActionTest.STATUS_STARTING);
    }

    @Test
    public void status_is_DOWN_if_platform_is_STARTING_and_databaseMigration_is_FAILED() throws Exception {
        verifyStatus(STARTING, FAILED, StatusActionTest.STATUS_DOWN);
    }

    @Test
    public void safety_test_for_new_platform_status() throws Exception {
        for (Platform.Status platformStatus : Iterables.filter(Arrays.asList(Platform.Status.values()), Predicates.not(Predicates.in(StatusActionTest.SUPPORTED_PLATFORM_STATUSES)))) {
            for (DatabaseMigrationState.Status databaseMigrationStatus : Status.values()) {
                verifyStatus(platformStatus, databaseMigrationStatus, StatusActionTest.STATUS_DOWN);
            }
        }
    }

    @Test
    public void safety_test_for_new_databaseMigration_status_when_platform_is_SAFEMODE() {
        for (DatabaseMigrationState.Status databaseMigrationStatus : Iterables.filter(Arrays.asList(Status.values()), Predicates.not(Predicates.in(StatusActionTest.SUPPORTED_DATABASE_MIGRATION_STATUSES)))) {
            Mockito.when(platform.status()).thenReturn(SAFEMODE);
            Mockito.when(migrationState.getStatus()).thenReturn(databaseMigrationStatus);
            underTest.newRequest().execute();
        }
    }

    private static class Dummy51Server extends Server {
        @Override
        public String getId() {
            return StatusActionTest.SERVER_ID;
        }

        @Override
        public String getVersion() {
            return StatusActionTest.SERVER_VERSION;
        }

        @Override
        public Date getStartedAt() {
            throw new UnsupportedOperationException();
        }

        @Override
        public File getRootDir() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getContextPath() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getPublicRootUrl() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isDev() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isSecured() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getURL() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getPermanentServerId() {
            throw new UnsupportedOperationException();
        }
    }
}

