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
package org.sonar.server.platform;


import java.util.Optional;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.server.platform.db.migration.step.MigrationSteps;
import org.sonar.server.platform.db.migration.version.DatabaseVersion;


public class DefaultServerUpgradeStatusTest {
    private static final long LAST_VERSION = 150;

    private MigrationSteps migrationSteps = Mockito.mock(MigrationSteps.class);

    private DatabaseVersion dbVersion = Mockito.mock(DatabaseVersion.class);

    private MapSettings settings = new MapSettings();

    private DefaultServerUpgradeStatus underTest = new DefaultServerUpgradeStatus(dbVersion, migrationSteps, new org.sonar.api.config.internal.ConfigurationBridge(settings));

    @Test
    public void shouldBeFreshInstallation() {
        Mockito.when(migrationSteps.getMaxMigrationNumber()).thenReturn(150L);
        Mockito.when(dbVersion.getVersion()).thenReturn(Optional.empty());
        underTest.start();
        assertThat(underTest.isFreshInstall()).isTrue();
        assertThat(underTest.isUpgraded()).isFalse();
        assertThat(underTest.getInitialDbVersion()).isEqualTo((-1));
    }

    @Test
    public void shouldBeUpgraded() {
        Mockito.when(dbVersion.getVersion()).thenReturn(Optional.of(50L));
        underTest.start();
        assertThat(underTest.isFreshInstall()).isFalse();
        assertThat(underTest.isUpgraded()).isTrue();
        assertThat(underTest.getInitialDbVersion()).isEqualTo(50);
    }

    @Test
    public void shouldNotBeUpgraded() {
        Mockito.when(dbVersion.getVersion()).thenReturn(Optional.of(DefaultServerUpgradeStatusTest.LAST_VERSION));
        underTest.start();
        assertThat(underTest.isFreshInstall()).isFalse();
        assertThat(underTest.isUpgraded()).isFalse();
        assertThat(underTest.getInitialDbVersion()).isEqualTo(((int) (DefaultServerUpgradeStatusTest.LAST_VERSION)));
    }

    @Test
    public void isBlueGreen() {
        settings.clear();
        assertThat(underTest.isBlueGreen()).isFalse();
        settings.setProperty("sonar.blueGreenEnabled", true);
        assertThat(underTest.isBlueGreen()).isTrue();
        settings.setProperty("sonar.blueGreenEnabled", false);
        assertThat(underTest.isBlueGreen()).isFalse();
    }
}

