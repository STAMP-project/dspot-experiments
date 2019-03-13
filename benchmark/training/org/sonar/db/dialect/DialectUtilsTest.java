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
package org.sonar.db.dialect;


import org.junit.Test;
import org.sonar.api.utils.MessageException;


public class DialectUtilsTest {
    @Test
    public void testFindById() {
        Dialect d = DialectUtils.find("mysql", null);
        assertThat(d).isInstanceOf(MySql.class);
    }

    @Test
    public void testFindByJdbcUrl() {
        Dialect d = DialectUtils.find(null, "jdbc:mysql:foo:bar");
        assertThat(d).isInstanceOf(MySql.class);
    }

    @Test(expected = MessageException.class)
    public void testFindNoMatch() {
        DialectUtils.find("foo", "bar");
    }
}

