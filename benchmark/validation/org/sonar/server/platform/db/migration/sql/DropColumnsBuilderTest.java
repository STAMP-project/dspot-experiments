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


import org.junit.Test;


public class DropColumnsBuilderTest {
    @Test
    public void drop_columns_on_mysql() {
        assertThat(build()).containsOnly("ALTER TABLE issues DROP COLUMN date_in_ms, DROP COLUMN name");
    }

    @Test
    public void drop_columns_on_oracle() {
        assertThat(build()).containsOnly("ALTER TABLE issues SET UNUSED (date_in_ms, name)");
    }

    @Test
    public void drop_columns_on_postgresql() {
        assertThat(build()).containsOnly("ALTER TABLE issues DROP COLUMN date_in_ms, DROP COLUMN name");
    }

    @Test
    public void drop_columns_on_mssql() {
        assertThat(build()).containsOnly("ALTER TABLE issues DROP COLUMN date_in_ms, name");
    }

    @Test
    public void drop_columns_on_h2() {
        assertThat(build()).containsOnly("ALTER TABLE issues DROP COLUMN date_in_ms", "ALTER TABLE issues DROP COLUMN name");
    }
}

