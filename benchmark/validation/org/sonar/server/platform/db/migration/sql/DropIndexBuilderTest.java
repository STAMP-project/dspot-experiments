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
package org.sonar.server.platform.db.migration.sql;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.dialect.H2;
import org.sonar.db.dialect.MsSql;
import org.sonar.db.dialect.MySql;
import org.sonar.db.dialect.Oracle;
import org.sonar.db.dialect.PostgreSql;


public class DropIndexBuilderTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void drop_index_in_table() {
        DropIndexBuilderTest.verifySql(new H2(), "DROP INDEX IF EXISTS issues_key");
        DropIndexBuilderTest.verifySql(new MsSql(), "DROP INDEX issues_key ON issues");
        DropIndexBuilderTest.verifySql(new MySql(), "DROP INDEX issues_key ON issues");
        DropIndexBuilderTest.verifySql(new Oracle(), "DROP INDEX issues_key");
        DropIndexBuilderTest.verifySql(new PostgreSql(), "DROP INDEX IF EXISTS issues_key");
    }

    @Test
    public void throw_NPE_if_table_name_is_missing() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Table name cannot be null");
        setName("issues_key").build();
    }

    @Test
    public void throw_IAE_if_table_name_is_not_valid() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Table name must be lower case and contain only alphanumeric chars or '_', got '(not valid)'");
        setName("issues_key").build();
    }

    @Test
    public void throw_NPE_if_index_name_is_missing() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Index name cannot be null");
        setTable("issues").build();
    }

    @Test
    public void throw_IAE_if_index_name_is_not_valid() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Index name must be lower case and contain only alphanumeric chars or '_', got '(not valid)'");
        setName("(not valid)").build();
    }
}

