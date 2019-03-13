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
package org.sonar.server.platform.db.migration.version.v60;


import org.junit.Test;
import org.sonar.server.platform.db.migration.version.DbVersionTestUtils;


public class DbVersion60Test {
    private DbVersion60 underTest = new DbVersion60();

    @Test
    public void verify_supports_components() {
        assertThat(underTest.getSupportComponents()).containsExactly(FixProjectUuidOfDeveloperProjects.class, CleanUsurperRootComponents.class);
    }

    @Test
    public void migrationNumber_starts_at_1200() {
        DbVersionTestUtils.verifyMinimumMigrationNumber(underTest, 1200);
    }

    @Test
    public void verify_migration_count() {
        DbVersionTestUtils.verifyMigrationCount(underTest, 71);
    }
}

