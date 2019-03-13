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
package org.sonar.server.platform.db.migration.version.v561;


import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.db.CoreDbTester;


public class UpdateUsersExternalIdentityWhenEmptyTest {
    private static final long PAST = 1000000000000L;

    private static final long NOW = 1500000000000L;

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(UpdateUsersExternalIdentityWhenEmptyTest.class, "schema.sql");

    private System2 system = Mockito.mock(System2.class);

    private UpdateUsersExternalIdentityWhenEmpty underTest = new UpdateUsersExternalIdentityWhenEmpty(db.database(), system);

    @Test
    public void migrate_users() throws Exception {
        insertUser("user-without-eternal-identity", null, null, UpdateUsersExternalIdentityWhenEmptyTest.PAST);
        insertUser("user-with-only-eternal-identity-provider", "github", null, UpdateUsersExternalIdentityWhenEmptyTest.PAST);
        insertUser("user-with-only-eternal-identity", null, "login1", UpdateUsersExternalIdentityWhenEmptyTest.PAST);
        insertUser("user-with-both-eternal-identity", "github", "login2", UpdateUsersExternalIdentityWhenEmptyTest.PAST);
        underTest.execute();
        checkUserIsUpdated("user-without-eternal-identity");
        checkUserIsUpdated("user-with-only-eternal-identity-provider");
        checkUserIsUpdated("user-with-only-eternal-identity");
        checkUserIsNotUpdated("user-with-both-eternal-identity");
    }

    @Test
    public void doest_not_fail_when_no_user() throws Exception {
        underTest.execute();
    }
}

