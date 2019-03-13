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
package org.sonar.server.platform.db.migration.version.v73;


import java.sql.SQLException;
import java.util.Date;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.db.CoreDbTester;


public class PopulateHotspotAdminPermissionOnTemplatesUsersTest {
    private static final Date PAST = new Date(100000000000L);

    private static final Date NOW = new Date(500000000000L);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateHotspotAdminPermissionOnTemplatesUsersTest.class, "perm_templates_users.sql");

    private System2 system2 = Mockito.mock(System2.class);

    private PopulateHotspotAdminPermissionOnTemplatesUsers underTest = new PopulateHotspotAdminPermissionOnTemplatesUsers(db.database(), system2);

    @Test
    public void insert_missing_permission() throws SQLException {
        Mockito.when(system2.now()).thenReturn(PopulateHotspotAdminPermissionOnTemplatesUsersTest.NOW.getTime());
        insertPermTemplateUserRole(1, 2, "noissueadmin");
        insertPermTemplateUserRole(3, 4, "issueadmin");
        insertPermTemplateUserRole(3, 4, "another");
        insertPermTemplateUserRole(5, 6, "securityhotspotadmin");
        underTest.execute();
        assertPermTemplateUserRoles(tuple(1L, 2L, "noissueadmin", PopulateHotspotAdminPermissionOnTemplatesUsersTest.PAST, PopulateHotspotAdminPermissionOnTemplatesUsersTest.PAST), tuple(3L, 4L, "issueadmin", PopulateHotspotAdminPermissionOnTemplatesUsersTest.PAST, PopulateHotspotAdminPermissionOnTemplatesUsersTest.PAST), tuple(3L, 4L, "another", PopulateHotspotAdminPermissionOnTemplatesUsersTest.PAST, PopulateHotspotAdminPermissionOnTemplatesUsersTest.PAST), tuple(3L, 4L, "securityhotspotadmin", PopulateHotspotAdminPermissionOnTemplatesUsersTest.NOW, PopulateHotspotAdminPermissionOnTemplatesUsersTest.NOW), tuple(5L, 6L, "securityhotspotadmin", PopulateHotspotAdminPermissionOnTemplatesUsersTest.PAST, PopulateHotspotAdminPermissionOnTemplatesUsersTest.PAST));
    }
}

