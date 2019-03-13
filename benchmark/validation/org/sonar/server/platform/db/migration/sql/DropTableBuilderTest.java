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
import org.sonar.db.dialect.PostgreSql;


public class DropTableBuilderTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void drop_tables_on_mysql() {
        assertThat(build()).containsOnly("drop table if exists issues");
    }

    @Test
    public void drop_tables_on_postgresql() {
        assertThat(build()).containsOnly("drop table if exists issues");
    }

    @Test
    public void drop_tables_on_mssql() {
        assertThat(build()).containsOnly("drop table issues");
    }

    @Test
    public void drop_tables_on_h2() {
        assertThat(build()).containsOnly("drop table if exists issues");
    }

    @Test
    public void drop_columns_on_oracle() {
        assertThat(build()).containsExactly(("BEGIN\n" + (((((("EXECUTE IMMEDIATE \'DROP SEQUENCE issues_seq\';\n" + "EXCEPTION\n") + "WHEN OTHERS THEN\n") + "  IF SQLCODE != -2289 THEN\n") + "  RAISE;\n") + "  END IF;\n") + "END;")), ("BEGIN\n" + (((((("EXECUTE IMMEDIATE \'DROP TRIGGER issues_idt\';\n" + "EXCEPTION\n") + "WHEN OTHERS THEN\n") + "  IF SQLCODE != -4080 THEN\n") + "  RAISE;\n") + "  END IF;\n") + "END;")), ("BEGIN\n" + (((((("EXECUTE IMMEDIATE \'DROP TABLE issues\';\n" + "EXCEPTION\n") + "WHEN OTHERS THEN\n") + "  IF SQLCODE != -942 THEN\n") + "  RAISE;\n") + "  END IF;\n") + "END;")));
    }

    @Test
    public void fail_when_dialect_is_null() {
        expectedException.expect(NullPointerException.class);
        new DropTableBuilder(null, "issues");
    }

    @Test
    public void fail_when_table_is_null() {
        expectedException.expect(NullPointerException.class);
        new DropTableBuilder(new PostgreSql(), null);
    }
}

