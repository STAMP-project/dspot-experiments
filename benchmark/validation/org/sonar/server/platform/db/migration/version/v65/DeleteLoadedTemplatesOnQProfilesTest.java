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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.CoreDbTester;


public class DeleteLoadedTemplatesOnQProfilesTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(DeleteLoadedTemplatesOnQProfilesTest.class, "initial.sql");

    private DeleteLoadedTemplatesOnQProfiles underTest = new DeleteLoadedTemplatesOnQProfiles(db.database());

    @Test
    public void does_nothing_if_table_is_empty() throws SQLException {
        underTest.execute();
        assertThat(db.countRowsOfTable("loaded_templates")).isEqualTo(0);
    }

    @Test
    public void deletes_rows_with_qprofile_type() throws SQLException {
        insertRow("ORG_UUID_1", "QUALITY_PROFILE.HASH_OF_ORG_UUID_1");
        insertRow("ORG_UUID_2", "QUALITY_PROFILE.HASH_OF_ORG_UUID_2");
        insertRow("foo", "QUALITY_GATE");
        underTest.execute();
        assertThat(selectAllKeys()).containsExactly("foo");
    }
}

