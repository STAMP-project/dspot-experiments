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
package org.sonar.server.platform.db.migration.version.v67;


import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.CoreDbTester;


public class MigratePreviousAnalysisToPreviousVersionTest {
    private static final String SELECT_PROPERTIES = "SELECT prop_key, is_empty, text_value, clob_value FROM properties";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(MigratePreviousAnalysisToPreviousVersionTest.class, "properties.sql");

    private MigratePreviousAnalysisToPreviousVersion underTest = new MigratePreviousAnalysisToPreviousVersion(db.database());

    @Test
    public void migration_must_update_the_database() throws SQLException {
        insertProperty("sonar.leak.period", "any.value_here", null, false);
        insertProperty("sonar.leak.period", "previous_version", null, false);
        insertProperty("sonar.leak.period", "previous_analysis", null, false);
        insertProperty("whatever.property", "nothingspecial", null, false);
        insertProperty("whatever.property", null, "nothing.special", false);
        underTest.execute();
        // Single change
        assertPropertyContainsInAnyOrder(tuple("sonar.leak.period", "any.value_here", null, false), tuple("sonar.leak.period", "previous_version", null, false), tuple("sonar.leak.period", "previous_version", null, false), tuple("whatever.property", "nothingspecial", null, false), tuple("whatever.property", null, "nothing.special", false));
    }

    @Test
    public void migration_must_be_reentrant() throws SQLException {
        insertProperty("sonar.leak.period", "any.value_here", null, false);
        insertProperty("sonar.leak.period", "previous_version", null, false);
        insertProperty("sonar.leak.period", "previous_analysis", null, false);
        insertProperty("whatever.property", "nothingspecial", null, false);
        insertProperty("whatever.property", null, "nothing.special", false);
        underTest.execute();
        underTest.execute();
        // Single change
        assertPropertyContainsInAnyOrder(tuple("sonar.leak.period", "any.value_here", null, false), tuple("sonar.leak.period", "previous_version", null, false), tuple("sonar.leak.period", "previous_version", null, false), tuple("whatever.property", "nothingspecial", null, false), tuple("whatever.property", null, "nothing.special", false));
    }

    @Test
    public void migration_is_doing_nothing_when_no_data() throws SQLException {
        assertThat(db.countRowsOfTable("properties")).isEqualTo(0);
        underTest.execute();
        assertThat(db.countRowsOfTable("properties")).isEqualTo(0);
    }
}

