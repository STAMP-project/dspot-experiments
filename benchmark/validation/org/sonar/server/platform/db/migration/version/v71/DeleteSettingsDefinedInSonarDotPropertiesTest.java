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
package org.sonar.server.platform.db.migration.version.v71;


import LoggerLevel.WARN;
import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.utils.log.LogTester;
import org.sonar.db.CoreDbTester;
import org.sonar.server.platform.db.migration.step.DataChange;


public class DeleteSettingsDefinedInSonarDotPropertiesTest {
    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(DeleteSettingsDefinedInSonarDotPropertiesTest.class, "properties.sql");

    @Rule
    public LogTester logTester = new LogTester();

    private DataChange underTest = new DeleteSettingsDefinedInSonarDotProperties(db.database());

    @Test
    public void delete_sonar_dot_properties_settings() throws SQLException {
        insertSetting("sonar.jdbc.url");
        insertSetting("sonar.path.data");
        insertSetting("sonar.cluster.enabled");
        insertSetting("sonar.updatecenter.activate");
        underTest.execute();
        assertNoSettings();
    }

    @Test
    public void log_removed_settings() throws SQLException {
        insertSetting("sonar.jdbc.url");
        insertSetting("not.to.be.removed");
        underTest.execute();
        assertThat(logTester.logs(WARN)).containsExactlyInAnyOrder("System setting 'sonar.jdbc.url' was defined in database, it has been removed");
    }

    @Test
    public void delete_setting_at_global_and_component_level() throws SQLException {
        insertSetting("sonar.jdbc.url", null);
        insertSetting("sonar.jdbc.url", 100L);
        underTest.execute();
        assertNoSettings();
    }

    @Test
    public void migration_is_reentrant() throws SQLException {
        insertSetting("sonar.jdbc.url");
        underTest.execute();
        assertNoSettings();
        underTest.execute();
        assertNoSettings();
    }

    @Test
    public void does_nothing_when_no_sonar_dot_properties_settings() throws SQLException {
        insertSetting("other");
        underTest.execute();
        assertSettings(tuple("other", null));
    }

    @Test
    public void does_nothing_on_empty_table() throws SQLException {
        underTest.execute();
        assertSettings();
    }
}

