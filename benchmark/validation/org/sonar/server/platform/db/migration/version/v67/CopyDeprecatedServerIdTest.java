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
package org.sonar.server.platform.db.migration.version.v67;


import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.CoreDbTester;


public class CopyDeprecatedServerIdTest {
    private static final String DEPRECATED_KEY = "sonar.server_id";

    private static final String TARGET_KEY = "sonar.core.id";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(CopyDeprecatedServerIdTest.class, "properties.sql");

    private CopyDeprecatedServerId underTest = new CopyDeprecatedServerId(db.database());

    @Test
    public void override_server_id_with_deprecated_value_if_present() throws SQLException {
        insertProperty(CopyDeprecatedServerIdTest.DEPRECATED_KEY, "foo");
        insertProperty(CopyDeprecatedServerIdTest.TARGET_KEY, "bar");
        underTest.execute();
        assertThatTargetKeyHasValue("foo");
        assertThatDeprecatedKeyDoesNotExist();
    }

    @Test
    public void set_server_id_with_deprecated_value_if_present() throws SQLException {
        // the target property does not exist
        insertProperty(CopyDeprecatedServerIdTest.DEPRECATED_KEY, "foo");
        underTest.execute();
        assertThatTargetKeyHasValue("foo");
        assertThatDeprecatedKeyDoesNotExist();
    }

    @Test
    public void keep_existing_server_id_if_deprecated_value_if_absent() throws SQLException {
        insertProperty(CopyDeprecatedServerIdTest.TARGET_KEY, "foo");
        underTest.execute();
        assertThatTargetKeyHasValue("foo");
        assertThatDeprecatedKeyDoesNotExist();
    }
}

