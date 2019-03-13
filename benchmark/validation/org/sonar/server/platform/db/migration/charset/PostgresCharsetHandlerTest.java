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


import DatabaseCharsetChecker.State.FRESH_INSTALL;
import DatabaseCharsetChecker.State.STARTUP;
import DatabaseCharsetChecker.State.UPGRADE;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.utils.MessageException;


public class PostgresCharsetHandlerTest {
    private static final String TABLE_ISSUES = "issues";

    private static final String TABLE_PROJECTS = "projects";

    private static final String COLUMN_KEE = "kee";

    private static final String COLUMN_NAME = "name";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SqlExecutor sqlExecutor = Mockito.mock(SqlExecutor.class);

    private Connection connection = Mockito.mock(Connection.class);

    private PostgresMetadataReader metadata = Mockito.mock(PostgresMetadataReader.class);

    private PostgresCharsetHandler underTest = new PostgresCharsetHandler(sqlExecutor, metadata);

    @Test
    public void fresh_install_verifies_that_default_charset_is_utf8() throws SQLException {
        answerDefaultCharset("utf8");
        underTest.handle(connection, FRESH_INSTALL);
        // no errors, charset has been verified
        Mockito.verify(metadata).getDefaultCharset(ArgumentMatchers.same(connection));
        Mockito.verifyZeroInteractions(sqlExecutor);
    }

    @Test
    public void upgrade_verifies_that_default_charset_and_columns_are_utf8() throws Exception {
        answerDefaultCharset("utf8");
        answerColumns(Arrays.asList(new String[]{ PostgresCharsetHandlerTest.TABLE_ISSUES, PostgresCharsetHandlerTest.COLUMN_KEE, "utf8" }, new String[]{ PostgresCharsetHandlerTest.TABLE_PROJECTS, PostgresCharsetHandlerTest.COLUMN_NAME, "utf8" }));
        underTest.handle(connection, UPGRADE);
        // no errors, charsets have been verified
        Mockito.verify(metadata).getDefaultCharset(ArgumentMatchers.same(connection));
    }

    @Test
    public void regular_startup_verifies_that_default_charset_and_columns_are_utf8() throws Exception {
        answerDefaultCharset("utf8");
        answerColumns(Arrays.asList(new String[]{ PostgresCharsetHandlerTest.TABLE_ISSUES, PostgresCharsetHandlerTest.COLUMN_KEE, "utf8" }, new String[]{ PostgresCharsetHandlerTest.TABLE_PROJECTS, PostgresCharsetHandlerTest.COLUMN_NAME, "utf8" }));
        underTest.handle(connection, STARTUP);
        // no errors, charsets have been verified
        Mockito.verify(metadata).getDefaultCharset(ArgumentMatchers.same(connection));
    }

    @Test
    public void column_charset_can_be_empty() throws Exception {
        answerDefaultCharset("utf8");
        answerColumns(Arrays.asList(new String[]{ PostgresCharsetHandlerTest.TABLE_ISSUES, PostgresCharsetHandlerTest.COLUMN_KEE, "utf8" }, new String[]{ PostgresCharsetHandlerTest.TABLE_PROJECTS, PostgresCharsetHandlerTest.COLUMN_NAME, ""/* unset -> uses db collation */
         }));
        // no error
        underTest.handle(connection, UPGRADE);
    }

    @Test
    public void upgrade_fails_if_non_utf8_column() throws Exception {
        // default charset is ok but two columns are not
        answerDefaultCharset("utf8");
        answerColumns(Arrays.asList(new String[]{ PostgresCharsetHandlerTest.TABLE_ISSUES, PostgresCharsetHandlerTest.COLUMN_KEE, "utf8" }, new String[]{ PostgresCharsetHandlerTest.TABLE_PROJECTS, PostgresCharsetHandlerTest.COLUMN_KEE, "latin" }, new String[]{ PostgresCharsetHandlerTest.TABLE_PROJECTS, PostgresCharsetHandlerTest.COLUMN_NAME, "latin" }));
        expectedException.expect(MessageException.class);
        expectedException.expectMessage("Database columns [projects.kee, projects.name] must have UTF8 charset.");
        underTest.handle(connection, UPGRADE);
    }

    @Test
    public void upgrade_fails_if_default_charset_is_not_utf8() throws Exception {
        answerDefaultCharset("latin");
        answerColumns(Arrays.<String[]>asList(new String[]{ PostgresCharsetHandlerTest.TABLE_ISSUES, PostgresCharsetHandlerTest.COLUMN_KEE, "utf8" }));
        expectedException.expect(MessageException.class);
        expectedException.expectMessage("Database charset is latin. It must support UTF8.");
        underTest.handle(connection, UPGRADE);
    }
}

