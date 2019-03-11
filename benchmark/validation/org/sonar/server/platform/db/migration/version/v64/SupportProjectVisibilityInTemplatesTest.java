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
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.CoreDbTester;


public class SupportProjectVisibilityInTemplatesTest {
    private static final Integer GROUP_ANYONE = null;

    private static final String PERMISSION_USER = "user";

    private static final String PERMISSION_CODEVIEWER = "codeviewer";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(SupportProjectVisibilityInTemplatesTest.class, "permission_templates_and_groups.sql");

    private SupportProjectVisibilityInTemplates underTest = new SupportProjectVisibilityInTemplates(db.database());

    @Test
    public void execute_has_no_effect_if_tables_are_empty() throws SQLException {
        underTest.execute();
    }

    @Test
    public void execute_has_no_effect_if_templates_has_no_group() throws SQLException {
        insertPermissionTemplate("key");
        underTest.execute();
        assertUnchanged("key");
        assertNoGroup();
    }

    @Test
    public void execute_deletes_permission_USER_of_group_AnyOne_in_permission_template() throws SQLException {
        int ptId = insertPermissionTemplate("key");
        insertGroupPermission(ptId, SupportProjectVisibilityInTemplatesTest.GROUP_ANYONE, SupportProjectVisibilityInTemplatesTest.PERMISSION_USER);
        underTest.execute();
        assertUnchanged("key");
        assertNoGroup();
    }

    @Test
    public void execute_does_not_delete_permissions_different_from_USER_and_CODEVIEWER_for_group_AnyOne_in_permission_template() throws SQLException {
        int ptId = insertPermissionTemplate("key");
        insertGroupPermission(ptId, SupportProjectVisibilityInTemplatesTest.GROUP_ANYONE, "admin");
        insertGroupPermission(ptId, SupportProjectVisibilityInTemplatesTest.GROUP_ANYONE, "issueadmin");
        insertGroupPermission(ptId, SupportProjectVisibilityInTemplatesTest.GROUP_ANYONE, "scan");
        insertGroupPermission(ptId, SupportProjectVisibilityInTemplatesTest.GROUP_ANYONE, "foo");
        underTest.execute();
        assertUnchanged("key");
        assertHasGroupPermissions(ptId, SupportProjectVisibilityInTemplatesTest.GROUP_ANYONE, "admin", "issueadmin", "scan", "foo");
    }

    @Test
    public void execute_does_not_delete_any_permissions_from_other_group_in_permission_template() throws SQLException {
        int ptId = insertPermissionTemplate("key");
        insertGroupPermission(ptId, 12, SupportProjectVisibilityInTemplatesTest.PERMISSION_USER);
        insertGroupPermission(ptId, 12, SupportProjectVisibilityInTemplatesTest.PERMISSION_CODEVIEWER);
        insertGroupPermission(ptId, 12, "admin");
        insertGroupPermission(ptId, 12, "issueadmin");
        insertGroupPermission(ptId, 12, "scan");
        insertGroupPermission(ptId, 12, "bar");
        underTest.execute();
        assertUnchanged("key");
        assertHasGroupPermissions(ptId, 12, SupportProjectVisibilityInTemplatesTest.PERMISSION_CODEVIEWER, SupportProjectVisibilityInTemplatesTest.PERMISSION_USER, "admin", "issueadmin", "scan", "bar");
    }

    @Test
    public void execute_deletes_permission_CODEVIEWER_of_group_AnyOne_in_permission_template() throws SQLException {
        int ptId = insertPermissionTemplate("key");
        insertGroupPermission(ptId, SupportProjectVisibilityInTemplatesTest.GROUP_ANYONE, SupportProjectVisibilityInTemplatesTest.PERMISSION_CODEVIEWER);
        underTest.execute();
        assertUnchanged("key");
        assertNoGroup();
    }

    @Test
    public void execute_is_reentrant() throws SQLException {
        int ptId1 = insertPermissionTemplate("key1");
        insertGroupPermission(ptId1, SupportProjectVisibilityInTemplatesTest.GROUP_ANYONE, SupportProjectVisibilityInTemplatesTest.PERMISSION_USER);
        insertGroupPermission(ptId1, SupportProjectVisibilityInTemplatesTest.GROUP_ANYONE, SupportProjectVisibilityInTemplatesTest.PERMISSION_CODEVIEWER);
        insertGroupPermission(ptId1, 11, SupportProjectVisibilityInTemplatesTest.PERMISSION_USER);
        insertGroupPermission(ptId1, 12, "foo");
        insertGroupPermission(ptId1, 12, SupportProjectVisibilityInTemplatesTest.PERMISSION_CODEVIEWER);
        insertGroupPermission(ptId1, 12, "bar");
        int ptId2 = insertPermissionTemplate("key2");
        insertGroupPermission(ptId2, SupportProjectVisibilityInTemplatesTest.GROUP_ANYONE, SupportProjectVisibilityInTemplatesTest.PERMISSION_CODEVIEWER);
        insertGroupPermission(ptId2, SupportProjectVisibilityInTemplatesTest.GROUP_ANYONE, "moh");
        insertGroupPermission(ptId2, 50, SupportProjectVisibilityInTemplatesTest.PERMISSION_USER);
        insertGroupPermission(ptId2, 51, "admin");
        underTest.execute();
        verifyFor_execute_is_reentrant(ptId1, ptId2);
        underTest.execute();
        verifyFor_execute_is_reentrant(ptId1, ptId2);
    }
}

