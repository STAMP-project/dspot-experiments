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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class MysqlCharsetHandlerTest {
    private static final String TABLE_ISSUES = "issues";

    private static final String TABLE_PROJECTS = "projects";

    private static final String COLUMN_KEE = "kee";

    private static final String COLUMN_NAME = "name";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SqlExecutor sqlExecutor = Mockito.mock(SqlExecutor.class);

    private Connection connection = Mockito.mock(Connection.class);

    private MysqlCharsetHandler underTest = new MysqlCharsetHandler(sqlExecutor);

    @Test
    public void upgrade_verifies_that_columns_are_utf8_and_case_sensitive() throws Exception {
        answerColumnDef(new ColumnDef(MysqlCharsetHandlerTest.TABLE_ISSUES, MysqlCharsetHandlerTest.COLUMN_KEE, "utf8", "utf8_bin", "varchar", 10, false), new ColumnDef(MysqlCharsetHandlerTest.TABLE_PROJECTS, MysqlCharsetHandlerTest.COLUMN_NAME, "utf8", "utf8_bin", "varchar", 10, false));
        // all columns are utf8
        underTest.handle(connection, UPGRADE);
    }

    @Test
    public void fresh_install_does_not_verify_anything() throws Exception {
        underTest.handle(connection, FRESH_INSTALL);
        Mockito.verifyZeroInteractions(sqlExecutor);
    }

    @Test
    public void regular_startup_does_not_verify_anything() throws Exception {
        underTest.handle(connection, STARTUP);
        Mockito.verifyZeroInteractions(sqlExecutor);
    }

    @Test
    public void repair_case_insensitive_column() throws Exception {
        answerColumnDef(new ColumnDef(MysqlCharsetHandlerTest.TABLE_ISSUES, MysqlCharsetHandlerTest.COLUMN_KEE, "big5_chinese", "big5_chinese_ci", "varchar", 10, false), new ColumnDef(MysqlCharsetHandlerTest.TABLE_PROJECTS, MysqlCharsetHandlerTest.COLUMN_NAME, "latin1", "latin1_swedish_ci", "varchar", 10, false));
        underTest.handle(connection, UPGRADE);
        Mockito.verify(sqlExecutor).executeDdl(connection, "ALTER TABLE issues MODIFY kee varchar(10) CHARACTER SET 'big5_chinese' COLLATE 'big5_bin' NOT NULL");
        Mockito.verify(sqlExecutor).executeDdl(connection, "ALTER TABLE projects MODIFY name varchar(10) CHARACTER SET 'latin1' COLLATE 'latin1_bin' NOT NULL");
    }

    @Test
    public void size_should_be_ignored_on_longtext_column() throws Exception {
        answerColumnDef(new ColumnDef(MysqlCharsetHandlerTest.TABLE_ISSUES, MysqlCharsetHandlerTest.COLUMN_KEE, "latin1", "latin1_german1_ci", "longtext", 4294967295L, false));
        underTest.handle(connection, UPGRADE);
        Mockito.verify(sqlExecutor).executeDdl(connection, (((("ALTER TABLE " + (MysqlCharsetHandlerTest.TABLE_ISSUES)) + " MODIFY ") + (MysqlCharsetHandlerTest.COLUMN_KEE)) + " longtext CHARACTER SET 'latin1' COLLATE 'latin1_bin' NOT NULL"));
    }
}

