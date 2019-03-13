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
package org.sonar.server.platform.db.migration.version.v66;


import java.sql.SQLException;
import java.sql.Types;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.CoreDbTester;


public class CreateTableQProfileEditGroupsTest {
    private static final String TABLE = "qprofile_edit_groups";

    @Rule
    public final CoreDbTester db = CoreDbTester.createForSchema(CreateTableQProfileEditGroupsTest.class, "empty.sql");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private CreateTableQProfileEditGroups underTest = new CreateTableQProfileEditGroups(db.database());

    @Test
    public void creates_table_on_empty_db() throws SQLException {
        underTest.execute();
        assertThat(db.countRowsOfTable(CreateTableQProfileEditGroupsTest.TABLE)).isEqualTo(0);
        db.assertColumnDefinition(CreateTableQProfileEditGroupsTest.TABLE, "uuid", Types.VARCHAR, 40, false);
        db.assertColumnDefinition(CreateTableQProfileEditGroupsTest.TABLE, "group_id", Types.INTEGER, null, false);
        db.assertColumnDefinition(CreateTableQProfileEditGroupsTest.TABLE, "qprofile_uuid", Types.VARCHAR, 255, false);
        db.assertColumnDefinition(CreateTableQProfileEditGroupsTest.TABLE, "created_at", Types.BIGINT, null, false);
        db.assertPrimaryKey(CreateTableQProfileEditGroupsTest.TABLE, ("pk_" + (CreateTableQProfileEditGroupsTest.TABLE)), "uuid");
        db.assertIndex(CreateTableQProfileEditGroupsTest.TABLE, "qprofile_edit_groups_qprofile", "qprofile_uuid");
        db.assertUniqueIndex(CreateTableQProfileEditGroupsTest.TABLE, "qprofile_edit_groups_unique", "group_id", "qprofile_uuid");
    }

    @Test
    public void migration_is_not_reentrant() throws SQLException {
        underTest.execute();
        expectedException.expect(IllegalStateException.class);
        underTest.execute();
    }
}

