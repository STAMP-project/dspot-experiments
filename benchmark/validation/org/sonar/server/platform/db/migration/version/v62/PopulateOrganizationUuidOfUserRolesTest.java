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
package org.sonar.server.platform.db.migration.version.v62;


import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.CoreDbTester;


public class PopulateOrganizationUuidOfUserRolesTest {
    private static final String TABLE_USER_ROLES = "user_roles";

    private static final String AN_ORG_UUID = "org1";

    @Rule
    public CoreDbTester dbTester = CoreDbTester.createForSchema(PopulateOrganizationUuidOfUserRolesTest.class, "user_roles.sql");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private PopulateOrganizationUuidOfUserRoles underTest = new PopulateOrganizationUuidOfUserRoles(dbTester.database());

    @Test
    public void execute_fails_with_ISE_if_default_organization_internal_property_is_not_set() throws SQLException {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Default organization uuid is missing");
        underTest.execute();
    }

    @Test
    public void migration_populates_missing_organization_uuids() throws SQLException {
        dbTester.executeInsert("user_roles", "user_id", "1", "role", "admin", "organization_uuid", null);
        dbTester.executeInsert("user_roles", "user_id", "2", "role", "viewever", "organization_uuid", null);
        dbTester.executeInsert("user_roles", "user_id", "3", "role", "viewever", "organization_uuid", PopulateOrganizationUuidOfUserRolesTest.AN_ORG_UUID);
        insertDefaultOrganizationInternalProperty(PopulateOrganizationUuidOfUserRolesTest.AN_ORG_UUID);
        underTest.execute();
        assertThat(dbTester.countRowsOfTable(PopulateOrganizationUuidOfUserRolesTest.TABLE_USER_ROLES)).isEqualTo(3);
        assertThat(dbTester.countSql((("select count(1) from user_roles where organization_uuid='" + (PopulateOrganizationUuidOfUserRolesTest.AN_ORG_UUID)) + "'"))).isEqualTo(3);
    }

    @Test
    public void migration_has_no_effect_on_empty_table() throws SQLException {
        insertDefaultOrganizationInternalProperty(PopulateOrganizationUuidOfUserRolesTest.AN_ORG_UUID);
        underTest.execute();
        assertThat(dbTester.countRowsOfTable(PopulateOrganizationUuidOfUserRolesTest.TABLE_USER_ROLES)).isEqualTo(0);
    }
}

