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


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import org.junit.Test;
import org.mockito.Mockito;


public class MssqlMetadataReaderTest {
    private SqlExecutor sqlExecutor = Mockito.mock(SqlExecutor.class);

    private Connection connection = Mockito.mock(Connection.class);

    private MssqlMetadataReader underTest = new MssqlMetadataReader(sqlExecutor);

    @Test
    public void test_getDefaultCollation() throws SQLException {
        answerSelect(Arrays.<String[]>asList(new String[]{ "Latin1_General_CS_AS" }));
        assertThat(underTest.getDefaultCollation(connection)).isEqualTo("Latin1_General_CS_AS");
    }
}

