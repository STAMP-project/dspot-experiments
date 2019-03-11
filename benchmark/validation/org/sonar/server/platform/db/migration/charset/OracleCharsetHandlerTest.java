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
import org.sonar.api.utils.MessageException;


public class OracleCharsetHandlerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SqlExecutor sqlExecutor = Mockito.mock(SqlExecutor.class);

    private Connection connection = Mockito.mock(Connection.class);

    private OracleCharsetHandler underTest = new OracleCharsetHandler(sqlExecutor);

    @Test
    public void fresh_install_verifies_utf8_charset() throws Exception {
        answerCharset("UTF8");
        underTest.handle(connection, FRESH_INSTALL);
    }

    @Test
    public void upgrade_does_not_verify_utf8_charset() throws Exception {
        underTest.handle(connection, UPGRADE);
        Mockito.verifyZeroInteractions(sqlExecutor);
    }

    @Test
    public void fresh_install_supports_al32utf8() throws Exception {
        answerCharset("AL32UTF8");
        underTest.handle(connection, FRESH_INSTALL);
    }

    @Test
    public void fresh_install_fails_if_charset_is_not_utf8() throws Exception {
        answerCharset("LATIN");
        expectedException.expect(MessageException.class);
        expectedException.expectMessage("Oracle NLS_CHARACTERSET does not support UTF8: LATIN");
        underTest.handle(connection, FRESH_INSTALL);
    }

    @Test
    public void fails_if_can_not_get_charset() throws Exception {
        answerCharset(null);
        expectedException.expect(MessageException.class);
        underTest.handle(connection, FRESH_INSTALL);
    }

    @Test
    public void does_nothing_if_regular_startup() throws Exception {
        underTest.handle(connection, STARTUP);
        Mockito.verifyZeroInteractions(sqlExecutor);
    }
}

