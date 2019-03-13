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


public class CreateTableInternalPropertiesTest {
    private static final String TABLE_INTERNAL_PROPERTIES = "internal_properties";

    @Rule
    public final CoreDbTester dbTester = CoreDbTester.createForSchema(CreateTableInternalPropertiesTest.class, "empty.sql");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private CreateTableInternalProperties underTest = new CreateTableInternalProperties(dbTester.database());

    @Test
    public void creates_table_on_empty_db() throws SQLException {
        underTest.execute();
        assertThat(dbTester.countRowsOfTable(CreateTableInternalPropertiesTest.TABLE_INTERNAL_PROPERTIES)).isEqualTo(0);
        dbTester.assertColumnDefinition(CreateTableInternalPropertiesTest.TABLE_INTERNAL_PROPERTIES, "kee", Types.VARCHAR, 20, false);
        dbTester.assertColumnDefinition(CreateTableInternalPropertiesTest.TABLE_INTERNAL_PROPERTIES, "is_empty", Types.BOOLEAN, null, false);
        dbTester.assertColumnDefinition(CreateTableInternalPropertiesTest.TABLE_INTERNAL_PROPERTIES, "text_value", Types.VARCHAR, 4000, true);
        dbTester.assertColumnDefinition(CreateTableInternalPropertiesTest.TABLE_INTERNAL_PROPERTIES, "clob_value", Types.CLOB, null, true);
        dbTester.assertColumnDefinition(CreateTableInternalPropertiesTest.TABLE_INTERNAL_PROPERTIES, "created_at", Types.BIGINT, null, false);
        dbTester.assertPrimaryKey(CreateTableInternalPropertiesTest.TABLE_INTERNAL_PROPERTIES, ("pk_" + (CreateTableInternalPropertiesTest.TABLE_INTERNAL_PROPERTIES)), "kee");
    }

    @Test
    public void migration_is_not_reentrant() throws SQLException {
        underTest.execute();
        expectedException.expect(IllegalStateException.class);
        underTest.execute();
    }
}

