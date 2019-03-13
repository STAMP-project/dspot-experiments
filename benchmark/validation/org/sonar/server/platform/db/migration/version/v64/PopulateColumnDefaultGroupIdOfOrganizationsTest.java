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


import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.db.CoreDbTester;


public class PopulateColumnDefaultGroupIdOfOrganizationsTest {
    private static final long PAST = 100000000000L;

    private static final long NOW = 500000000000L;

    private static final String DEFAULT_ORGANIZATION_UUID = "def-org";

    private static final String ORGANIZATION_1 = "ORGANIZATION_1";

    private static final String ORGANIZATION_2 = "ORGANIZATION_2";

    private static final String SONAR_USERS_NAME = "sonar-users";

    private static final String MEMBERS_NAME = "Members";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateColumnDefaultGroupIdOfOrganizationsTest.class, "initial.sql");

    private System2 system2 = Mockito.mock(System2.class);

    private PopulateColumnDefaultGroupIdOfOrganizations underTest = new PopulateColumnDefaultGroupIdOfOrganizations(db.database(), system2);

    @Test
    public void set_sonar_users_group_id_on_default_organization_when_organization_disabled() throws Exception {
        setupDefaultOrganization();
        long groupId = insertGroup(PopulateColumnDefaultGroupIdOfOrganizationsTest.DEFAULT_ORGANIZATION_UUID, PopulateColumnDefaultGroupIdOfOrganizationsTest.SONAR_USERS_NAME);
        underTest.execute();
        checkOrganizations(tuple(PopulateColumnDefaultGroupIdOfOrganizationsTest.DEFAULT_ORGANIZATION_UUID, groupId, PopulateColumnDefaultGroupIdOfOrganizationsTest.NOW));
    }

    @Test
    public void set_members_group_id_on_organizations_when_organization_enabled() throws Exception {
        setupDefaultOrganization();
        enableOrganization();
        long group1 = insertGroup(PopulateColumnDefaultGroupIdOfOrganizationsTest.ORGANIZATION_1, PopulateColumnDefaultGroupIdOfOrganizationsTest.MEMBERS_NAME);
        insertOrganization(PopulateColumnDefaultGroupIdOfOrganizationsTest.ORGANIZATION_1, null);
        long group2 = insertGroup(PopulateColumnDefaultGroupIdOfOrganizationsTest.ORGANIZATION_2, PopulateColumnDefaultGroupIdOfOrganizationsTest.MEMBERS_NAME);
        insertOrganization(PopulateColumnDefaultGroupIdOfOrganizationsTest.ORGANIZATION_2, null);
        underTest.execute();
        checkOrganizations(tuple(PopulateColumnDefaultGroupIdOfOrganizationsTest.ORGANIZATION_1, group1, PopulateColumnDefaultGroupIdOfOrganizationsTest.NOW), tuple(PopulateColumnDefaultGroupIdOfOrganizationsTest.ORGANIZATION_2, group2, PopulateColumnDefaultGroupIdOfOrganizationsTest.NOW), tuple(PopulateColumnDefaultGroupIdOfOrganizationsTest.DEFAULT_ORGANIZATION_UUID, null, PopulateColumnDefaultGroupIdOfOrganizationsTest.PAST));
    }

    @Test
    public void does_nothing_when_default_group_id_already_set() throws Exception {
        setupDefaultOrganization();
        enableOrganization();
        long group1 = insertGroup(PopulateColumnDefaultGroupIdOfOrganizationsTest.ORGANIZATION_1, PopulateColumnDefaultGroupIdOfOrganizationsTest.MEMBERS_NAME);
        insertOrganization(PopulateColumnDefaultGroupIdOfOrganizationsTest.ORGANIZATION_1, group1);
        long group2 = insertGroup(PopulateColumnDefaultGroupIdOfOrganizationsTest.ORGANIZATION_2, PopulateColumnDefaultGroupIdOfOrganizationsTest.MEMBERS_NAME);
        insertOrganization(PopulateColumnDefaultGroupIdOfOrganizationsTest.ORGANIZATION_2, group2);
        underTest.execute();
        checkOrganizations(tuple(PopulateColumnDefaultGroupIdOfOrganizationsTest.ORGANIZATION_1, group1, PopulateColumnDefaultGroupIdOfOrganizationsTest.PAST), tuple(PopulateColumnDefaultGroupIdOfOrganizationsTest.ORGANIZATION_2, group2, PopulateColumnDefaultGroupIdOfOrganizationsTest.PAST), tuple(PopulateColumnDefaultGroupIdOfOrganizationsTest.DEFAULT_ORGANIZATION_UUID, null, PopulateColumnDefaultGroupIdOfOrganizationsTest.PAST));
    }

    @Test
    public void set_members_group_id_on_organizations_only_when_not_already_et() throws Exception {
        setupDefaultOrganization();
        enableOrganization();
        long group1 = insertGroup(PopulateColumnDefaultGroupIdOfOrganizationsTest.ORGANIZATION_1, PopulateColumnDefaultGroupIdOfOrganizationsTest.MEMBERS_NAME);
        insertOrganization(PopulateColumnDefaultGroupIdOfOrganizationsTest.ORGANIZATION_1, null);
        long group2 = insertGroup(PopulateColumnDefaultGroupIdOfOrganizationsTest.ORGANIZATION_2, PopulateColumnDefaultGroupIdOfOrganizationsTest.MEMBERS_NAME);
        insertOrganization(PopulateColumnDefaultGroupIdOfOrganizationsTest.ORGANIZATION_2, group2);
        underTest.execute();
        checkOrganizations(tuple(PopulateColumnDefaultGroupIdOfOrganizationsTest.ORGANIZATION_1, group1, PopulateColumnDefaultGroupIdOfOrganizationsTest.NOW), tuple(PopulateColumnDefaultGroupIdOfOrganizationsTest.ORGANIZATION_2, group2, PopulateColumnDefaultGroupIdOfOrganizationsTest.PAST), tuple(PopulateColumnDefaultGroupIdOfOrganizationsTest.DEFAULT_ORGANIZATION_UUID, null, PopulateColumnDefaultGroupIdOfOrganizationsTest.PAST));
    }

    @Test
    public void migration_is_reentrant() throws Exception {
        setupDefaultOrganization();
        enableOrganization();
        long group1 = insertGroup(PopulateColumnDefaultGroupIdOfOrganizationsTest.ORGANIZATION_1, PopulateColumnDefaultGroupIdOfOrganizationsTest.MEMBERS_NAME);
        insertOrganization(PopulateColumnDefaultGroupIdOfOrganizationsTest.ORGANIZATION_1, null);
        long group2 = insertGroup(PopulateColumnDefaultGroupIdOfOrganizationsTest.ORGANIZATION_2, PopulateColumnDefaultGroupIdOfOrganizationsTest.MEMBERS_NAME);
        insertOrganization(PopulateColumnDefaultGroupIdOfOrganizationsTest.ORGANIZATION_2, null);
        underTest.execute();
        checkOrganizations(tuple(PopulateColumnDefaultGroupIdOfOrganizationsTest.ORGANIZATION_1, group1, PopulateColumnDefaultGroupIdOfOrganizationsTest.NOW), tuple(PopulateColumnDefaultGroupIdOfOrganizationsTest.ORGANIZATION_2, group2, PopulateColumnDefaultGroupIdOfOrganizationsTest.NOW), tuple(PopulateColumnDefaultGroupIdOfOrganizationsTest.DEFAULT_ORGANIZATION_UUID, null, PopulateColumnDefaultGroupIdOfOrganizationsTest.PAST));
        underTest.execute();
        checkOrganizations(tuple(PopulateColumnDefaultGroupIdOfOrganizationsTest.ORGANIZATION_1, group1, PopulateColumnDefaultGroupIdOfOrganizationsTest.NOW), tuple(PopulateColumnDefaultGroupIdOfOrganizationsTest.ORGANIZATION_2, group2, PopulateColumnDefaultGroupIdOfOrganizationsTest.NOW), tuple(PopulateColumnDefaultGroupIdOfOrganizationsTest.DEFAULT_ORGANIZATION_UUID, null, PopulateColumnDefaultGroupIdOfOrganizationsTest.PAST));
    }
}

