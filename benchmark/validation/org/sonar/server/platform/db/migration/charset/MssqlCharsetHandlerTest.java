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
import DatabaseCharsetChecker.State.UPGRADE;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.utils.MessageException;


@RunWith(DataProviderRunner.class)
public class MssqlCharsetHandlerTest {
    private static final String TABLE_ISSUES = "issues";

    private static final String TABLE_PROJECTS = "projects";

    private static final String COLUMN_KEE = "kee";

    private static final String COLUMN_NAME = "name";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SqlExecutor sqlExecutor = Mockito.mock(SqlExecutor.class);

    private MssqlMetadataReader metadata = Mockito.mock(MssqlMetadataReader.class);

    private MssqlCharsetHandler underTest = new MssqlCharsetHandler(sqlExecutor, metadata);

    private Connection connection = Mockito.mock(Connection.class);

    @Test
    public void fresh_install_verifies_that_default_collation_is_CS_AS() throws SQLException {
        answerDefaultCollation("Latin1_General_CS_AS");
        underTest.handle(connection, FRESH_INSTALL);
        Mockito.verify(metadata).getDefaultCollation(connection);
    }

    @Test
    public void fresh_install_fails_if_default_collation_is_not_CS_AS() throws SQLException {
        answerDefaultCollation("Latin1_General_CI_AI");
        expectedException.expect(MessageException.class);
        expectedException.expectMessage("Database collation must be case-sensitive and accent-sensitive. It is Latin1_General_CI_AI but should be Latin1_General_CS_AS.");
        underTest.handle(connection, FRESH_INSTALL);
    }

    @Test
    public void upgrade_fails_if_default_collation_is_not_CS_AS() throws SQLException {
        answerDefaultCollation("Latin1_General_CI_AI");
        expectedException.expect(MessageException.class);
        expectedException.expectMessage("Database collation must be case-sensitive and accent-sensitive. It is Latin1_General_CI_AI but should be Latin1_General_CS_AS.");
        underTest.handle(connection, UPGRADE);
    }

    @Test
    public void upgrade_checks_that_columns_are_CS_AS() throws SQLException {
        answerDefaultCollation("Latin1_General_CS_AS");
        answerColumnDefs(new ColumnDef(MssqlCharsetHandlerTest.TABLE_ISSUES, MssqlCharsetHandlerTest.COLUMN_KEE, "Latin1_General", "Latin1_General_CS_AS", "varchar", 10, false), new ColumnDef(MssqlCharsetHandlerTest.TABLE_PROJECTS, MssqlCharsetHandlerTest.COLUMN_NAME, "Latin1_General", "Latin1_General_CS_AS", "varchar", 10, false));
        // do not fail
        underTest.handle(connection, UPGRADE);
    }

    @Test
    public void upgrade_repairs_CI_AI_columns() throws SQLException {
        answerDefaultCollation("Latin1_General_CS_AS");
        answerColumnDefs(new ColumnDef(MssqlCharsetHandlerTest.TABLE_ISSUES, MssqlCharsetHandlerTest.COLUMN_KEE, "Latin1_General", "Latin1_General_CS_AS", "varchar", 10, false), new ColumnDef(MssqlCharsetHandlerTest.TABLE_PROJECTS, MssqlCharsetHandlerTest.COLUMN_NAME, "Latin1_General", "Latin1_General_CI_AI", "varchar", 10, false));
        underTest.handle(connection, UPGRADE);
        Mockito.verify(sqlExecutor).executeDdl(connection, "ALTER TABLE projects ALTER COLUMN name varchar(10) COLLATE Latin1_General_CS_AS NOT NULL");
    }

    @Test
    public void upgrade_repairs_indexed_CI_AI_columns() throws SQLException {
        answerDefaultCollation("Latin1_General_CS_AS");
        answerColumnDefs(new ColumnDef(MssqlCharsetHandlerTest.TABLE_ISSUES, MssqlCharsetHandlerTest.COLUMN_KEE, "Latin1_General", "Latin1_General_CS_AS", "varchar", 10, false), new ColumnDef(MssqlCharsetHandlerTest.TABLE_PROJECTS, MssqlCharsetHandlerTest.COLUMN_NAME, "Latin1_General", "Latin1_General_CI_AI", "varchar", 10, false));
        // This index is on two columns. Note that it does not make sense for table "projects" !
        answerIndices(new MssqlCharsetHandler.ColumnIndex("projects_name", false, "name"), new MssqlCharsetHandler.ColumnIndex("projects_login_and_name", true, "login,name"));
        underTest.handle(connection, UPGRADE);
        Mockito.verify(sqlExecutor).executeDdl(connection, "DROP INDEX projects.projects_name");
        Mockito.verify(sqlExecutor).executeDdl(connection, "DROP INDEX projects.projects_login_and_name");
        Mockito.verify(sqlExecutor).executeDdl(connection, "ALTER TABLE projects ALTER COLUMN name varchar(10) COLLATE Latin1_General_CS_AS NOT NULL");
        Mockito.verify(sqlExecutor).executeDdl(connection, "CREATE  INDEX projects_name ON projects (name)");
        Mockito.verify(sqlExecutor).executeDdl(connection, "CREATE UNIQUE INDEX projects_login_and_name ON projects (login,name)");
    }

    @Test
    public void support_the_max_size_of_varchar_column() throws Exception {
        answerDefaultCollation("Latin1_General_CS_AS");
        // returned size is -1
        answerColumnDefs(new ColumnDef(MssqlCharsetHandlerTest.TABLE_PROJECTS, MssqlCharsetHandlerTest.COLUMN_NAME, "Latin1_General", "Latin1_General_CI_AI", "nvarchar", (-1), false));
        answerIndices();
        underTest.handle(connection, UPGRADE);
        Mockito.verify(sqlExecutor).executeDdl(connection, "ALTER TABLE projects ALTER COLUMN name nvarchar(max) COLLATE Latin1_General_CS_AS NOT NULL");
    }

    @Test
    public void do_not_repair_system_tables_of_sql_azure() throws Exception {
        answerDefaultCollation("Latin1_General_CS_AS");
        answerColumnDefs(new ColumnDef("sys.sysusers", MssqlCharsetHandlerTest.COLUMN_NAME, "Latin1_General", "Latin1_General_CI_AI", "varchar", 10, false));
        underTest.handle(connection, UPGRADE);
        Mockito.verify(sqlExecutor, Mockito.never()).executeDdl(ArgumentMatchers.any(Connection.class), ArgumentMatchers.anyString());
    }

    /**
     * SONAR-7988
     */
    @Test
    public void fix_Latin1_CS_AS_columns_created_in_5_x() throws SQLException {
        answerDefaultCollation("SQL_Latin1_General_CP1_CS_AS");
        answerColumnDefs(new ColumnDef(MssqlCharsetHandlerTest.TABLE_PROJECTS, MssqlCharsetHandlerTest.COLUMN_NAME, "Latin1_General", "Latin1_General_CS_AS", "nvarchar", 10, false));
        underTest.handle(connection, UPGRADE);
        Mockito.verify(sqlExecutor).executeDdl(connection, "ALTER TABLE projects ALTER COLUMN name nvarchar(10) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL");
    }
}

