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
package org.sonar.server.platform.db.migration.version.v64;


import java.sql.SQLException;
import java.sql.Types;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.CoreDbTester;


public class AddColumnNewProjectPrivateTest {
    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(AddColumnNewProjectPrivateTest.class, "organizations_without_new_project_private_column.sql");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private AddColumnNewProjectPrivate underTest = new AddColumnNewProjectPrivate(db.database());

    @Test
    public void execute_adds_nullable_boolean_column_private_to_table_PROJECTS() throws SQLException {
        underTest.execute();
        db.assertColumnDefinition("organizations", "new_project_private", Types.BOOLEAN, null, true);
    }

    @Test
    public void execute_is_not_reentrant() throws SQLException {
        underTest.execute();
        expectedException.expect(IllegalStateException.class);
        underTest.execute();
    }
}

