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
package org.sonar.server.platform.db.migration.history;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.CoreDbTester;


public class NoTableMigrationHistoryImplTest {
    @Rule
    public CoreDbTester dbTester = CoreDbTester.createEmpty();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private MigrationHistoryImpl underTest = new MigrationHistoryImpl(dbTester.database());

    @Test
    public void start_fails_with_ISE_if_table_history_does_not_exist() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Migration history table is missing");
        underTest.start();
    }
}

