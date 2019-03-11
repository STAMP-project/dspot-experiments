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
package org.sonar.server.platform.db.migration.version.v63;


import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.CoreDbTester;


public class DeleteUselessPropertiesTest {
    private static final String TABLE_PROPERTIES = "properties";

    private static final int COMPONENT_ID_1 = 125;

    private static final int COMPONENT_ID_2 = 604;

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(DeleteUselessPropertiesTest.class, "properties.sql");

    private DeleteUselessProperties underTest = new DeleteUselessProperties(db.database());

    @Test
    public void migration_has_no_effect_on_empty_tables() throws SQLException {
        underTest.execute();
        assertThat(db.countRowsOfTable(DeleteUselessPropertiesTest.TABLE_PROPERTIES)).isZero();
    }

    @Test
    public void migration_removes_hours_in_day_setting() throws SQLException {
        insertProperty("sonar.technicalDebt.hoursInDay", null);
        underTest.execute();
        assertThat(db.countRowsOfTable(DeleteUselessPropertiesTest.TABLE_PROPERTIES)).isZero();
    }

    @Test
    public void migration_removes_create_user_setting() throws SQLException {
        insertProperty("sonar.authenticator.createUser", null);
        underTest.execute();
        assertThat(db.countRowsOfTable(DeleteUselessPropertiesTest.TABLE_PROPERTIES)).isZero();
    }

    @Test
    public void migration_removes_period2_to_period5_settings() throws SQLException {
        insertProperty("sonar.timemachine.period2", null);
        insertProperty("sonar.timemachine.period3", null);
        insertProperty("sonar.timemachine.period4", null);
        insertProperty("sonar.timemachine.period5", null);
        underTest.execute();
        assertThat(db.countRowsOfTable(DeleteUselessPropertiesTest.TABLE_PROPERTIES)).isZero();
    }

    @Test
    public void migration_removes_period2_to_period5_settings_related_to_component() throws SQLException {
        insertProperty("sonar.timemachine.period2", DeleteUselessPropertiesTest.COMPONENT_ID_1);
        insertProperty("sonar.timemachine.period2", DeleteUselessPropertiesTest.COMPONENT_ID_1);
        insertProperty("sonar.timemachine.period2", DeleteUselessPropertiesTest.COMPONENT_ID_2);
        insertProperty("sonar.timemachine.period3", DeleteUselessPropertiesTest.COMPONENT_ID_1);
        insertProperty("sonar.timemachine.period4", DeleteUselessPropertiesTest.COMPONENT_ID_2);
        insertProperty("sonar.timemachine.period5", DeleteUselessPropertiesTest.COMPONENT_ID_2);
        underTest.execute();
        assertThat(db.countRowsOfTable(DeleteUselessPropertiesTest.TABLE_PROPERTIES)).isZero();
    }

    @Test
    public void migration_removes_period2_to_period5_settings_related_to_qualifiers() throws SQLException {
        insertProperty("sonar.timemachine.period2.TRK", DeleteUselessPropertiesTest.COMPONENT_ID_1);
        insertProperty("sonar.timemachine.period2.TRK", null);
        insertProperty("sonar.timemachine.period2.VW", DeleteUselessPropertiesTest.COMPONENT_ID_1);
        insertProperty("sonar.timemachine.period2.DEV", DeleteUselessPropertiesTest.COMPONENT_ID_1);
        insertProperty("sonar.timemachine.period2", DeleteUselessPropertiesTest.COMPONENT_ID_1);
        insertProperty("sonar.timemachine.period3", DeleteUselessPropertiesTest.COMPONENT_ID_2);
        insertProperty("sonar.timemachine.period3.TRK", DeleteUselessPropertiesTest.COMPONENT_ID_2);
        underTest.execute();
        assertThat(db.countRowsOfTable(DeleteUselessPropertiesTest.TABLE_PROPERTIES)).isZero();
    }

    @Test
    public void migration_ignores_not_relevant_settings() throws SQLException {
        insertProperty("sonar.core.serverBaseURL", null);
        insertProperty("sonar.dbcleaner.cleanDirectory", null);
        insertProperty("sonar.dbcleaner.cleanDirectory", DeleteUselessPropertiesTest.COMPONENT_ID_1);
        // Only these settings should be removed
        insertProperty("sonar.timemachine.period4", null);
        insertProperty("sonar.timemachine.period5", null);
        underTest.execute();
        verifyPropertyKeys("sonar.core.serverBaseURL", "sonar.dbcleaner.cleanDirectory");
        assertThat(db.countRowsOfTable(DeleteUselessPropertiesTest.TABLE_PROPERTIES)).isEqualTo(3);
    }

    @Test
    public void migration_is_reentrant() throws SQLException {
        insertProperty("sonar.core.serverBaseURL", null);
        insertProperty("sonar.dbcleaner.cleanDirectory", null);
        insertProperty("sonar.dbcleaner.cleanDirectory", DeleteUselessPropertiesTest.COMPONENT_ID_1);
        // Only these settings should be removed
        insertProperty("sonar.timemachine.period4", null);
        insertProperty("sonar.timemachine.period5", null);
        underTest.execute();
        assertThat(db.countRowsOfTable(DeleteUselessPropertiesTest.TABLE_PROPERTIES)).isEqualTo(3);
        underTest.execute();
        assertThat(db.countRowsOfTable(DeleteUselessPropertiesTest.TABLE_PROPERTIES)).isEqualTo(3);
    }
}

