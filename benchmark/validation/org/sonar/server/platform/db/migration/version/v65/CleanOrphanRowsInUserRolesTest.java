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
package org.sonar.server.platform.db.migration.version.v65;


import java.sql.SQLException;
import java.util.Random;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.CoreDbTester;


public class CleanOrphanRowsInUserRolesTest {
    private static final String TABLE_USER_ROLES = "user_roles";

    private static final String PROJECT_SCOPE = "PRJ";

    private static final String QUALIFIER_VW = "VW";

    private static final String QUALIFIER_TRK = "TRK";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(CleanOrphanRowsInUserRolesTest.class, "user_roles_and_projects.sql");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private CleanOrphanRowsInUserRoles underTest = new CleanOrphanRowsInUserRoles(db.database());

    @Test
    public void execute_has_no_effect_on_empty_table() throws SQLException {
        underTest.execute();
        assertThat(db.countRowsOfTable(CleanOrphanRowsInUserRolesTest.TABLE_USER_ROLES)).isZero();
    }

    @Test
    public void execute_does_not_delete_rows_without_resource_id() throws SQLException {
        insertUserRole(null);
        underTest.execute();
        assertThat(db.countRowsOfTable(CleanOrphanRowsInUserRolesTest.TABLE_USER_ROLES)).isEqualTo(1);
    }

    @Test
    public void execute_deletes_rows_of_non_existent_component() throws SQLException {
        insertUserRole(new Random().nextInt());
        underTest.execute();
        assertThat(db.countRowsOfTable(CleanOrphanRowsInUserRolesTest.TABLE_USER_ROLES)).isEqualTo(0);
    }

    @Test
    public void execute_deletes_rows_of_component_without_qualifier_PRJ() throws SQLException {
        String scope = randomAlphanumeric(3);
        insertUserRole(insertComponent(scope, CleanOrphanRowsInUserRolesTest.QUALIFIER_TRK));
        insertUserRole(insertComponent(scope, CleanOrphanRowsInUserRolesTest.QUALIFIER_VW));
        insertUserRole(insertComponent(scope, randomAlphanumeric(3)));
        assertThat(db.countRowsOfTable(CleanOrphanRowsInUserRolesTest.TABLE_USER_ROLES)).isEqualTo(3);
        underTest.execute();
        assertThat(db.countRowsOfTable(CleanOrphanRowsInUserRolesTest.TABLE_USER_ROLES)).isEqualTo(0);
    }

    @Test
    public void execute_keeps_rows_with_qualifier_TRK() throws SQLException {
        executeKeepsRowsWithSpecifiedQualifierAndScopeProject(CleanOrphanRowsInUserRolesTest.QUALIFIER_TRK);
    }

    @Test
    public void execute_keeps_rows_with_qualifier_VW() throws SQLException {
        executeKeepsRowsWithSpecifiedQualifierAndScopeProject(CleanOrphanRowsInUserRolesTest.QUALIFIER_VW);
    }

    @Test
    public void execute_deletes_rows_of_component_with_qualifier_DEV() throws SQLException {
        executeDeletesRowsWithSpecifiedQualifierAndScopeProject("DEV");
    }

    @Test
    public void execute_deletes_rows_of_component_with_qualifier_DEV_PRJ() throws SQLException {
        executeDeletesRowsWithSpecifiedQualifierAndScopeProject("DEV_PRJ");
    }

    @Test
    public void execute_deletes_rows_of_component_with_qualifier_BRC() throws SQLException {
        executeDeletesRowsWithSpecifiedQualifierAndScopeProject("BRC");
    }

    @Test
    public void execute_deletes_rows_of_component_with_qualifier_DIR() throws SQLException {
        executeDeletesRowsWithSpecifiedQualifierAndScopeProject("DIR");
    }

    @Test
    public void execute_deletes_rows_of_component_with_qualifier_FIL() throws SQLException {
        executeDeletesRowsWithSpecifiedQualifierAndScopeProject("FIL");
    }

    @Test
    public void execute_deletes_rows_of_component_with_qualifier_UTS() throws SQLException {
        executeDeletesRowsWithSpecifiedQualifierAndScopeProject("UTS");
    }

    @Test
    public void execute_deletes_rows_of_component_with_unknown_qualifier() throws SQLException {
        executeDeletesRowsWithSpecifiedQualifierAndScopeProject(randomAlphanumeric(3));
    }
}

