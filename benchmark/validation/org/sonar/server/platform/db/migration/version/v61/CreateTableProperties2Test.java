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
package org.sonar.server.platform.db.migration.version.v61;


import java.sql.SQLException;
import java.sql.Types;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.CoreDbTester;


public class CreateTableProperties2Test {
    private static final String TABLE_PROPERTIES_2 = "properties2";

    @Rule
    public final CoreDbTester dbTester = CoreDbTester.createForSchema(CreateTableProperties2Test.class, "empty.sql");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private CreateTableProperties2 underTest = new CreateTableProperties2(dbTester.database());

    @Test
    public void creates_table_on_empty_db() throws SQLException {
        underTest.execute();
        assertThat(dbTester.countRowsOfTable(CreateTableProperties2Test.TABLE_PROPERTIES_2)).isEqualTo(0);
        dbTester.assertColumnDefinition(CreateTableProperties2Test.TABLE_PROPERTIES_2, "id", Types.INTEGER, null, false);
        dbTester.assertColumnDefinition(CreateTableProperties2Test.TABLE_PROPERTIES_2, "prop_key", Types.VARCHAR, 512, false);
        dbTester.assertColumnDefinition(CreateTableProperties2Test.TABLE_PROPERTIES_2, "resource_id", Types.BIGINT, null, true);
        dbTester.assertColumnDefinition(CreateTableProperties2Test.TABLE_PROPERTIES_2, "user_id", Types.BIGINT, null, true);
        dbTester.assertColumnDefinition(CreateTableProperties2Test.TABLE_PROPERTIES_2, "is_empty", Types.BOOLEAN, null, false);
        dbTester.assertColumnDefinition(CreateTableProperties2Test.TABLE_PROPERTIES_2, "text_value", Types.VARCHAR, 4000, true);
        dbTester.assertColumnDefinition(CreateTableProperties2Test.TABLE_PROPERTIES_2, "clob_value", Types.CLOB, null, true);
        dbTester.assertColumnDefinition(CreateTableProperties2Test.TABLE_PROPERTIES_2, "created_at", Types.BIGINT, null, false);
        dbTester.assertPrimaryKey(CreateTableProperties2Test.TABLE_PROPERTIES_2, "pk_properties", "id");
    }

    @Test
    public void migration_is_not_reentrant() throws SQLException {
        underTest.execute();
        expectedException.expect(IllegalStateException.class);
        underTest.execute();
    }
}

