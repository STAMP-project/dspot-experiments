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
package org.sonar.server.platform.db.migration.version.v74;


import java.sql.SQLException;
import java.util.Date;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.core.util.UuidFactoryFast;
import org.sonar.db.CoreDbTester;


public class CreateApplicationsAndPortfoliosCreatorPermissionsTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(CreateApplicationsAndPortfoliosCreatorPermissionsTest.class, "perm_templates_groups.sql");

    private static final Date PAST = new Date(100000000000L);

    private static final Date NOW = new Date(500000000000L);

    private static final String DEFAULT_ORGANIZATION_UUID = UuidFactoryFast.getInstance().create();

    private static final String DEFAULT_PERM_TEMPLATE_VIEW = "default_view_template";

    private static final String ANOTHER_PERM_TEMPLATE_VIEW = "another_template";

    private System2 system2 = Mockito.mock(System2.class);

    private CreateApplicationsAndPortfoliosCreatorPermissions underTest = new CreateApplicationsAndPortfoliosCreatorPermissions(db.database(), system2);

    @Test
    public void migration_is_reentrant() throws SQLException {
        Mockito.when(system2.now()).thenReturn(CreateApplicationsAndPortfoliosCreatorPermissionsTest.NOW.getTime());
        underTest.execute();
        underTest.execute();
        Long idOfDefaultPermissionTemplate = getIdOfPermissionTemplate(CreateApplicationsAndPortfoliosCreatorPermissionsTest.DEFAULT_PERM_TEMPLATE_VIEW);
        Long idOfAdministratorGroup = getIdOfGroup("sonar-administrators");
        assertPermTemplateGroupRoles(tuple(idOfDefaultPermissionTemplate, idOfAdministratorGroup, "applicationcreator", CreateApplicationsAndPortfoliosCreatorPermissionsTest.NOW, CreateApplicationsAndPortfoliosCreatorPermissionsTest.NOW), tuple(idOfDefaultPermissionTemplate, idOfAdministratorGroup, "portfoliocreator", CreateApplicationsAndPortfoliosCreatorPermissionsTest.NOW, CreateApplicationsAndPortfoliosCreatorPermissionsTest.NOW));
    }

    @Test
    public void insert_missing_permissions() throws SQLException {
        Mockito.when(system2.now()).thenReturn(CreateApplicationsAndPortfoliosCreatorPermissionsTest.NOW.getTime());
        underTest.execute();
        Long idOfDefaultPermissionTemplate = getIdOfPermissionTemplate(CreateApplicationsAndPortfoliosCreatorPermissionsTest.DEFAULT_PERM_TEMPLATE_VIEW);
        Long idOfAdministratorGroup = getIdOfGroup("sonar-administrators");
        assertPermTemplateGroupRoles(tuple(idOfDefaultPermissionTemplate, idOfAdministratorGroup, "applicationcreator", CreateApplicationsAndPortfoliosCreatorPermissionsTest.NOW, CreateApplicationsAndPortfoliosCreatorPermissionsTest.NOW), tuple(idOfDefaultPermissionTemplate, idOfAdministratorGroup, "portfoliocreator", CreateApplicationsAndPortfoliosCreatorPermissionsTest.NOW, CreateApplicationsAndPortfoliosCreatorPermissionsTest.NOW));
    }

    @Test
    public void does_nothing_if_template_group_has_the_permissions_already() throws SQLException {
        Long idOfDefaultPermissionTemplate = getIdOfPermissionTemplate(CreateApplicationsAndPortfoliosCreatorPermissionsTest.DEFAULT_PERM_TEMPLATE_VIEW);
        Long idOfAdministratorGroup = getIdOfGroup("sonar-administrators");
        insertPermTemplateGroupRole(1, 2, "noissueadmin");
        insertPermTemplateGroupRole(3, 4, "issueadmin");
        insertPermTemplateGroupRole(3, 4, "another");
        insertPermTemplateGroupRole(5, 6, "securityhotspotadmin");
        insertPermTemplateGroupRole(idOfDefaultPermissionTemplate.intValue(), idOfAdministratorGroup.intValue(), "applicationcreator");
        insertPermTemplateGroupRole(idOfDefaultPermissionTemplate.intValue(), idOfAdministratorGroup.intValue(), "portfoliocreator");
        Mockito.when(system2.now()).thenReturn(CreateApplicationsAndPortfoliosCreatorPermissionsTest.NOW.getTime());
        underTest.execute();
        assertPermTemplateGroupRoles(tuple(1L, 2L, "noissueadmin", CreateApplicationsAndPortfoliosCreatorPermissionsTest.PAST, CreateApplicationsAndPortfoliosCreatorPermissionsTest.PAST), tuple(3L, 4L, "issueadmin", CreateApplicationsAndPortfoliosCreatorPermissionsTest.PAST, CreateApplicationsAndPortfoliosCreatorPermissionsTest.PAST), tuple(3L, 4L, "another", CreateApplicationsAndPortfoliosCreatorPermissionsTest.PAST, CreateApplicationsAndPortfoliosCreatorPermissionsTest.PAST), tuple(5L, 6L, "securityhotspotadmin", CreateApplicationsAndPortfoliosCreatorPermissionsTest.PAST, CreateApplicationsAndPortfoliosCreatorPermissionsTest.PAST), tuple(idOfDefaultPermissionTemplate, idOfAdministratorGroup, "applicationcreator", CreateApplicationsAndPortfoliosCreatorPermissionsTest.PAST, CreateApplicationsAndPortfoliosCreatorPermissionsTest.PAST), tuple(idOfDefaultPermissionTemplate, idOfAdministratorGroup, "portfoliocreator", CreateApplicationsAndPortfoliosCreatorPermissionsTest.PAST, CreateApplicationsAndPortfoliosCreatorPermissionsTest.PAST));
    }

    @Test
    public void insert_missing_permission_keeping_other_template_group_permissions() throws SQLException {
        Mockito.when(system2.now()).thenReturn(CreateApplicationsAndPortfoliosCreatorPermissionsTest.NOW.getTime());
        insertPermTemplateGroupRole(1, 2, "noissueadmin");
        insertPermTemplateGroupRole(3, 4, "issueadmin");
        insertPermTemplateGroupRole(3, 4, "another");
        insertPermTemplateGroupRole(5, 6, "securityhotspotadmin");
        underTest.execute();
        Long idOfDefaultPermissionTemplate = getIdOfPermissionTemplate(CreateApplicationsAndPortfoliosCreatorPermissionsTest.DEFAULT_PERM_TEMPLATE_VIEW);
        Long idOfAdministratorGroup = getIdOfGroup("sonar-administrators");
        assertPermTemplateGroupRoles(tuple(1L, 2L, "noissueadmin", CreateApplicationsAndPortfoliosCreatorPermissionsTest.PAST, CreateApplicationsAndPortfoliosCreatorPermissionsTest.PAST), tuple(3L, 4L, "issueadmin", CreateApplicationsAndPortfoliosCreatorPermissionsTest.PAST, CreateApplicationsAndPortfoliosCreatorPermissionsTest.PAST), tuple(3L, 4L, "another", CreateApplicationsAndPortfoliosCreatorPermissionsTest.PAST, CreateApplicationsAndPortfoliosCreatorPermissionsTest.PAST), tuple(5L, 6L, "securityhotspotadmin", CreateApplicationsAndPortfoliosCreatorPermissionsTest.PAST, CreateApplicationsAndPortfoliosCreatorPermissionsTest.PAST), tuple(idOfDefaultPermissionTemplate, idOfAdministratorGroup, "applicationcreator", CreateApplicationsAndPortfoliosCreatorPermissionsTest.NOW, CreateApplicationsAndPortfoliosCreatorPermissionsTest.NOW), tuple(idOfDefaultPermissionTemplate, idOfAdministratorGroup, "portfoliocreator", CreateApplicationsAndPortfoliosCreatorPermissionsTest.NOW, CreateApplicationsAndPortfoliosCreatorPermissionsTest.NOW));
    }
}

