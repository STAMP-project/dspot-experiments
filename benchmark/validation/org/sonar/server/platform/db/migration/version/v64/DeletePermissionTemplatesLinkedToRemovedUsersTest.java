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
import org.sonar.db.CoreDbTester;


public class DeletePermissionTemplatesLinkedToRemovedUsersTest {
    private static long PAST_TIME = 10000000000L;

    private static long TEMPLATE_ID = 1000L;

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(DeletePermissionTemplatesLinkedToRemovedUsersTest.class, "initial.sql");

    private DeletePermissionTemplatesLinkedToRemovedUsers underTest = new DeletePermissionTemplatesLinkedToRemovedUsers(db.database());

    @Test
    public void remove_permission_template_users_from_disabled_users() throws Exception {
        long userId1 = insertUser(false);
        long userId2 = insertUser(false);
        insertPermissionTemplateUser(userId1, DeletePermissionTemplatesLinkedToRemovedUsersTest.TEMPLATE_ID, "user");
        insertPermissionTemplateUser(userId1, DeletePermissionTemplatesLinkedToRemovedUsersTest.TEMPLATE_ID, "codeviewer");
        insertPermissionTemplateUser(userId2, DeletePermissionTemplatesLinkedToRemovedUsersTest.TEMPLATE_ID, "user");
        underTest.execute();
        checkNoCheckPermissionTemplateUsers();
    }

    @Test
    public void remove_permission_template_users_from_non_existing_users() throws Exception {
        insertPermissionTemplateUser(123L, DeletePermissionTemplatesLinkedToRemovedUsersTest.TEMPLATE_ID, "user");
        insertPermissionTemplateUser(321L, DeletePermissionTemplatesLinkedToRemovedUsersTest.TEMPLATE_ID, "codeviewer");
        underTest.execute();
        checkNoCheckPermissionTemplateUsers();
    }

    @Test
    public void does_not_remove_permission_template_users_from_active_user() throws Exception {
        long activeUserId = insertUser(true);
        long inactiveUserId = insertUser(false);
        long permissionTemplateUserOnActiveUser = insertPermissionTemplateUser(activeUserId, DeletePermissionTemplatesLinkedToRemovedUsersTest.TEMPLATE_ID, "user");
        insertPermissionTemplateUser(inactiveUserId, DeletePermissionTemplatesLinkedToRemovedUsersTest.TEMPLATE_ID, "user");
        underTest.execute();
        checkPermissionTemplateUsers(permissionTemplateUserOnActiveUser);
    }

    @Test
    public void does_not_fail_when_no_permission_template_users() throws Exception {
        insertUser(false);
        underTest.execute();
        checkNoCheckPermissionTemplateUsers();
    }

    @Test
    public void migration_is_reentrant() throws Exception {
        long userId = insertUser(false);
        insertPermissionTemplateUser(userId, DeletePermissionTemplatesLinkedToRemovedUsersTest.TEMPLATE_ID, "user");
        underTest.execute();
        checkNoCheckPermissionTemplateUsers();
        underTest.execute();
        checkNoCheckPermissionTemplateUsers();
    }
}

