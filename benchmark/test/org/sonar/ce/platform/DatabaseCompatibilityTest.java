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
package org.sonar.ce.platform;


import DatabaseVersion.Status.FRESH_INSTALL;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sonar.server.platform.db.migration.version.DatabaseVersion;


@RunWith(DataProviderRunner.class)
public class DatabaseCompatibilityTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private DatabaseVersion databaseVersion = Mockito.mock(DatabaseVersion.class);

    private DatabaseCompatibility underTest = new DatabaseCompatibility(databaseVersion);

    @Test
    public void start_has_no_effect_if_status_is_UP_TO_DATE() {
        Mockito.when(databaseVersion.getStatus()).thenReturn(UP_TO_DATE);
        underTest.start();
        Mockito.verify(databaseVersion).getStatus();
        Mockito.verifyNoMoreInteractions(databaseVersion);
    }

    @Test
    public void start_has_no_effect_if_status_is_FRESH_INSTALL() {
        Mockito.when(databaseVersion.getStatus()).thenReturn(FRESH_INSTALL);
        underTest.start();
        Mockito.verify(databaseVersion).getStatus();
        Mockito.verifyNoMoreInteractions(databaseVersion);
    }

    @Test
    public void stop_has_no_effect() {
        underTest.stop();
        Mockito.verifyZeroInteractions(databaseVersion);
    }
}

