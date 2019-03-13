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
package org.sonar.server.platform.db.migration.version.v71;


import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.CoreDbTester;


public class RenameTableProjectLinks2ToProjectLinksTest {
    @Rule
    public final CoreDbTester dbTester = CoreDbTester.createForSchema(RenameTableProjectLinks2ToProjectLinksTest.class, "project_links2.sql");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private RenameTableProjectLinks2ToProjectLinks underTest = new RenameTableProjectLinks2ToProjectLinks(dbTester.database());

    @Test
    public void creates_table_on_empty_db() throws SQLException {
        underTest.execute();
        dbTester.assertTableDoesNotExist("project_links2");
        assertThat(dbTester.countRowsOfTable("project_links")).isZero();
        dbTester.assertIndex("project_links", "project_links_project", "project_uuid");
    }

    @Test
    public void migration_is_not_reentrant() throws SQLException {
        underTest.execute();
        expectedException.expect(IllegalStateException.class);
        underTest.execute();
    }
}

