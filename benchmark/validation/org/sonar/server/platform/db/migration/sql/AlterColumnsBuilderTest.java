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
import org.sonar.server.platform.db.migration.def.BooleanColumnDef;


public class AlterColumnsBuilderTest {
    private static final String TABLE_NAME = "issues";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void update_columns_on_h2() {
        assertThat(build()).containsOnly("ALTER TABLE issues ALTER COLUMN value DOUBLE NULL", "ALTER TABLE issues ALTER COLUMN name VARCHAR (10) NULL");
    }

    @Test
    public void update_not_nullable_column_on_h2() {
        assertThat(build()).containsOnly("ALTER TABLE issues ALTER COLUMN name VARCHAR (10) NOT NULL");
    }

    @Test
    public void update_columns_on_mssql() {
        assertThat(build()).containsOnly("ALTER TABLE issues ALTER COLUMN value DECIMAL (30,20) NULL", "ALTER TABLE issues ALTER COLUMN name NVARCHAR (10) NULL");
    }

    @Test
    public void update_not_nullable_column_on_mssql() {
        assertThat(build()).containsOnly("ALTER TABLE issues ALTER COLUMN name NVARCHAR (10) NOT NULL");
    }

    @Test
    public void update_columns_on_postgres() {
        assertThat(build()).containsOnly(("ALTER TABLE issues " + ("ALTER COLUMN value TYPE NUMERIC (30,20), ALTER COLUMN value DROP NOT NULL, " + "ALTER COLUMN name TYPE VARCHAR (10), ALTER COLUMN name DROP NOT NULL")));
    }

    @Test
    public void update_not_nullable_column_on_postgres() {
        assertThat(build()).containsOnly("ALTER TABLE issues ALTER COLUMN name TYPE VARCHAR (10), ALTER COLUMN name SET NOT NULL");
    }

    @Test
    public void update_columns_on_mysql() {
        assertThat(build()).containsOnly("ALTER TABLE issues MODIFY COLUMN value DECIMAL (30,20) NULL, MODIFY COLUMN name VARCHAR (10) NULL");
    }

    @Test
    public void update_not_nullable_column_on_mysql() {
        assertThat(build()).containsOnly("ALTER TABLE issues MODIFY COLUMN name VARCHAR (10) NOT NULL");
    }

    @Test
    public void update_columns_on_oracle() {
        assertThat(build()).containsOnly("ALTER TABLE issues MODIFY (value NUMERIC (30,20) NULL)", "ALTER TABLE issues MODIFY (name VARCHAR2 (10 CHAR) NULL)");
    }

    @Test
    public void update_not_nullable_column_on_oracle() {
        assertThat(build()).containsOnly("ALTER TABLE issues MODIFY (name VARCHAR2 (10 CHAR) NOT NULL)");
    }

    @Test
    public void fail_with_ISE_if_no_column() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("No column has been defined");
        build();
    }

    /**
     * As we want DEFAULT value to be removed from all tables, it is supported
     * only on creation of tables and columns, not on alter.
     */
    @Test
    public void updateColumn_throws_IAE_if_default_value_is_defined() {
        BooleanColumnDef column = BooleanColumnDef.newBooleanColumnDefBuilder().setColumnName("enabled").setIsNullable(false).setDefaultValue(false).build();
        AlterColumnsBuilder alterColumnsBuilder = new AlterColumnsBuilder(new H2(), AlterColumnsBuilderTest.TABLE_NAME);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Default value is not supported on alter of column 'enabled'");
        alterColumnsBuilder.updateColumn(column);
    }
}

