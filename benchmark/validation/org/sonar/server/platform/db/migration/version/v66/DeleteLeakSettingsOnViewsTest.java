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
package org.sonar.server.platform.db.migration.version.v66;


import Scopes.PROJECT;
import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.CoreDbTester;


public class DeleteLeakSettingsOnViewsTest {
    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(DeleteLeakSettingsOnViewsTest.class, "settings_and_projects.sql");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private DeleteLeakSettingsOnViews underTest = new DeleteLeakSettingsOnViews(db.database());

    @Test
    public void migrate_leak_settings_on_views() throws SQLException {
        long viewId = insertComponent(PROJECT, VIEW);
        long subViewId = insertComponent(PROJECT, SUBVIEW);
        insertProperty("sonar.leak.period", viewId);
        insertProperty("sonar.leak.period", subViewId);
        underTest.execute();
        assertPropertiesIsEmpty();
    }

    @Test
    public void migrate_old_leak_settings_on_views() throws SQLException {
        long viewId = insertComponent(PROJECT, VIEW);
        long subViewId = insertComponent(PROJECT, SUBVIEW);
        insertProperty("sonar.timemachine.period1", viewId);
        insertProperty("sonar.timemachine.period1", subViewId);
        underTest.execute();
        assertPropertiesIsEmpty();
    }

    @Test
    public void does_nothing_on_leak_settings_not_on_views() throws SQLException {
        long projectId = insertComponent(PROJECT, PROJECT);
        insertProperty("sonar.leak.period", projectId);
        insertProperty("sonar.leak.period", null);
        underTest.execute();
        assertProperties(tuple("sonar.leak.period", projectId), tuple("sonar.leak.period", null));
    }

    @Test
    public void does_nothing_on_non_leak_settings() throws SQLException {
        long projectId = insertComponent(PROJECT, PROJECT);
        insertProperty("sonar.component", projectId);
        insertProperty("sonar.global", null);
        underTest.execute();
        assertProperties(tuple("sonar.component", projectId), tuple("sonar.global", null));
    }
}

