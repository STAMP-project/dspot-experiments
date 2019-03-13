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
package org.sonar.server.platform.db.migration.version.v63;


import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.db.CoreDbTester;


public class UnsetUserRootFlagsTest {
    private static final long CREATED_AT = 1500L;

    private static final long FIXED_AT = 1600L;

    private TestSystem2 system = new TestSystem2().setNow(UnsetUserRootFlagsTest.FIXED_AT);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(UnsetUserRootFlagsTest.class, "in_progress_users.sql");

    private UnsetUserRootFlags underTest = new UnsetUserRootFlags(db.database(), system);

    @Test
    public void sets_USERS_IS_ROOT_to_false() throws SQLException {
        createUser("root1", true);
        createUser("nonRoot1", false);
        createUser("root2", true);
        createUser("nonRoot2", false);
        underTest.execute();
        verifyNotRoot(UnsetUserRootFlagsTest.CREATED_AT, "nonRoot1", "nonRoot2");
        verifyNotRoot(UnsetUserRootFlagsTest.FIXED_AT, "root1", "root2");
    }

    @Test
    public void migration_is_reentrant() throws SQLException {
        createUser("root", true);
        underTest.execute();
        verifyNotRoot(UnsetUserRootFlagsTest.FIXED_AT, "root");
        system.setNow(((UnsetUserRootFlagsTest.FIXED_AT) + 100L));
        underTest.execute();
        verifyNotRoot(UnsetUserRootFlagsTest.FIXED_AT, "root");
    }
}

