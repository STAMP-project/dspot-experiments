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
package org.sonar.server.health;


import Health.GREEN;
import Platform.Status;
import Platform.Status.UP;
import java.util.Arrays;
import java.util.Random;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.server.app.RestartFlagHolder;
import org.sonar.server.platform.Platform;
import org.sonar.server.platform.db.migration.DatabaseMigrationState;

import static DatabaseMigrationState.Status.NONE;
import static DatabaseMigrationState.Status.SUCCEEDED;


public class WebServerStatusNodeCheckTest {
    private final DatabaseMigrationState migrationState = Mockito.mock(DatabaseMigrationState.class);

    private final Platform platform = Mockito.mock(Platform.class);

    private final RestartFlagHolder restartFlagHolder = Mockito.mock(RestartFlagHolder.class);

    private final Random random = new Random();

    private WebServerStatusNodeCheck underTest = new WebServerStatusNodeCheck(migrationState, platform, restartFlagHolder);

    @Test
    public void returns_RED_status_with_cause_if_platform_status_is_not_UP() {
        Platform[] statusesButUp = Arrays.stream(Status.values()).filter(( s) -> s != Platform.Status.UP).toArray(Platform[]::new);
        Platform.Status randomStatusButUp = statusesButUp[random.nextInt(statusesButUp.length)];
        Mockito.when(platform.status()).thenReturn(randomStatusButUp);
        Health health = underTest.check();
        verifyRedHealthWithCause(health);
    }

    @Test
    public void returns_RED_status_with_cause_if_platform_status_is_UP_but_migrationStatus_is_neither_NONE_nor_SUCCEED() {
        Mockito.when(platform.status()).thenReturn(UP);
        DatabaseMigrationState[] statusesButValidOnes = Arrays.stream(DatabaseMigrationState.Status.values()).filter(( s) -> s != DatabaseMigrationState.Status.NONE).filter(( s) -> s != DatabaseMigrationState.Status.SUCCEEDED).toArray(DatabaseMigrationState[]::new);
        DatabaseMigrationState.Status randomInvalidStatus = statusesButValidOnes[random.nextInt(statusesButValidOnes.length)];
        Mockito.when(migrationState.getStatus()).thenReturn(randomInvalidStatus);
        Health health = underTest.check();
        verifyRedHealthWithCause(health);
    }

    @Test
    public void returns_RED_with_cause_if_platform_status_is_UP_migration_status_is_valid_but_SQ_is_restarting() {
        Mockito.when(platform.status()).thenReturn(UP);
        Mockito.when(migrationState.getStatus()).thenReturn((random.nextBoolean() ? NONE : SUCCEEDED));
        Mockito.when(restartFlagHolder.isRestarting()).thenReturn(true);
        Health health = underTest.check();
        verifyRedHealthWithCause(health);
    }

    @Test
    public void returns_GREEN_without_cause_if_platform_status_is_UP_migration_status_is_valid_and_SQ_is_not_restarting() {
        Mockito.when(platform.status()).thenReturn(UP);
        Mockito.when(migrationState.getStatus()).thenReturn((random.nextBoolean() ? NONE : SUCCEEDED));
        Mockito.when(restartFlagHolder.isRestarting()).thenReturn(false);
        Health health = underTest.check();
        assertThat(health).isEqualTo(GREEN);
    }
}

