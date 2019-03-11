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
package org.sonar.server.platform.db.migration.version;


import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.server.platform.db.migration.history.MigrationHistory;
import org.sonar.server.platform.db.migration.step.MigrationSteps;


public class DatabaseVersionTest {
    private MigrationHistory migrationHistory = Mockito.mock(MigrationHistory.class);

    private MigrationSteps migrationSteps = Mockito.mock(MigrationSteps.class);

    private DatabaseVersion underTest = new DatabaseVersion(migrationSteps, migrationHistory);

    @Test
    public void getStatus_returns_FRESH_INSTALL_when_table_is_empty() {
        mockMaxMigrationNumberInDb(null);
        mockMaxMigrationNumberInConfig(150L);
        assertThat(underTest.getStatus()).isEqualTo(Status.FRESH_INSTALL);
    }

    @Test
    public void getStatus_returns_REQUIRES_UPGRADE_when_max_migration_number_in_table_is_less_than_max_migration_number_in_configuration() {
        mockMaxMigrationNumberInDb(123L);
        mockMaxMigrationNumberInConfig(150L);
        assertThat(underTest.getStatus()).isEqualTo(Status.REQUIRES_UPGRADE);
    }

    @Test
    public void getStatus_returns_UP_TO_DATE_when_max_migration_number_in_table_is_equal_to_max_migration_number_in_configuration() {
        mockMaxMigrationNumberInDb(150L);
        mockMaxMigrationNumberInConfig(150L);
        assertThat(underTest.getStatus()).isEqualTo(Status.UP_TO_DATE);
    }

    @Test
    public void getStatus_returns_REQUIRES_DOWNGRADE_when_max_migration_number_in_table_is_greater_than_max_migration_number_in_configuration() {
        mockMaxMigrationNumberInDb(200L);
        mockMaxMigrationNumberInConfig(150L);
        assertThat(underTest.getStatus()).isEqualTo(Status.REQUIRES_DOWNGRADE);
    }
}

