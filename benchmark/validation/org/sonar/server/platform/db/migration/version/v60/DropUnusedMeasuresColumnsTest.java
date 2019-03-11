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
package org.sonar.server.platform.db.migration.version.v60;


import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.db.Database;
import org.sonar.db.dialect.PostgreSql;


public class DropUnusedMeasuresColumnsTest {
    DropUnusedMeasuresColumns underTest;

    Database database;

    @Test
    public void generate_sql_on_postgresql() {
        Mockito.when(database.getDialect()).thenReturn(new PostgreSql());
        assertThat(underTest.generateSql()).containsOnly("ALTER TABLE project_measures DROP COLUMN rules_category_id, DROP COLUMN tendency, DROP COLUMN measure_date, DROP COLUMN url, DROP COLUMN rule_priority, DROP COLUMN characteristic_id, DROP COLUMN rule_id");
    }
}

