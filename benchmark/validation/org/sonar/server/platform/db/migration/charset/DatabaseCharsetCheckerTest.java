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
package org.sonar.server.platform.db.migration.charset;


import DatabaseCharsetChecker.State;
import DatabaseCharsetChecker.State.UPGRADE;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.db.Database;
import org.sonar.db.dialect.Dialect;
import org.sonar.db.dialect.H2;
import org.sonar.db.dialect.MsSql;
import org.sonar.db.dialect.MySql;
import org.sonar.db.dialect.Oracle;
import org.sonar.db.dialect.PostgreSql;


public class DatabaseCharsetCheckerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Database db = Mockito.mock(Database.class, Mockito.RETURNS_MOCKS);

    private CharsetHandler handler = Mockito.mock(CharsetHandler.class);

    private DatabaseCharsetChecker underTest = Mockito.spy(new DatabaseCharsetChecker(db));

    @Test
    public void executes_handler() throws Exception {
        Oracle dialect = new Oracle();
        Mockito.when(underTest.getHandler(dialect)).thenReturn(handler);
        Mockito.when(db.getDialect()).thenReturn(dialect);
        underTest.check(UPGRADE);
        Mockito.verify(handler).handle(ArgumentMatchers.any(Connection.class), ArgumentMatchers.eq(UPGRADE));
    }

    @Test
    public void throws_ISE_if_handler_fails() throws Exception {
        Oracle dialect = new Oracle();
        Mockito.when(underTest.getHandler(dialect)).thenReturn(handler);
        Mockito.when(db.getDialect()).thenReturn(dialect);
        Mockito.doThrow(new SQLException("failure")).when(handler).handle(ArgumentMatchers.any(Connection.class), ArgumentMatchers.any(State.class));
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("failure");
        underTest.check(UPGRADE);
    }

    @Test
    public void does_nothing_if_h2() {
        assertThat(underTest.getHandler(new H2())).isNull();
    }

    @Test
    public void getHandler_returns_MysqlCharsetHandler_if_mysql() {
        assertThat(underTest.getHandler(new MySql())).isInstanceOf(MysqlCharsetHandler.class);
    }

    @Test
    public void getHandler_returns_MssqlCharsetHandler_if_mssql() {
        assertThat(underTest.getHandler(new MsSql())).isInstanceOf(MssqlCharsetHandler.class);
    }

    @Test
    public void getHandler_returns_OracleCharsetHandler_if_oracle() {
        assertThat(underTest.getHandler(new Oracle())).isInstanceOf(OracleCharsetHandler.class);
    }

    @Test
    public void getHandler_returns_PostgresCharsetHandler_if_postgres() {
        assertThat(underTest.getHandler(new PostgreSql())).isInstanceOf(PostgresCharsetHandler.class);
    }

    @Test
    public void getHandler_throws_IAE_if_unsupported_db() {
        Dialect unsupportedDialect = Mockito.mock(Dialect.class);
        Mockito.when(unsupportedDialect.getId()).thenReturn("foo");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Database not supported: foo");
        underTest.getHandler(unsupportedDialect);
    }
}

