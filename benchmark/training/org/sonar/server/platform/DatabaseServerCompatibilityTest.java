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


import DatabaseVersion.MIN_UPGRADE_VERSION;
import DatabaseVersion.Status.REQUIRES_DOWNGRADE;
import DatabaseVersion.Status.REQUIRES_UPGRADE;
import DatabaseVersion.Status.UP_TO_DATE;
import LoggerLevel.WARN;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.utils.MessageException;
import org.sonar.api.utils.log.LogTester;
import org.sonar.server.platform.db.migration.version.DatabaseVersion;


public class DatabaseServerCompatibilityTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public LogTester logTester = new LogTester();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private MapSettings settings = new MapSettings();

    @Test
    public void fail_if_requires_downgrade() {
        thrown.expect(MessageException.class);
        thrown.expectMessage("Database was upgraded to a more recent of SonarQube. Backup must probably be restored or db settings are incorrect.");
        DatabaseVersion version = Mockito.mock(DatabaseVersion.class);
        Mockito.when(version.getStatus()).thenReturn(REQUIRES_DOWNGRADE);
        start();
    }

    @Test
    public void fail_if_requires_firstly_to_upgrade_to_lts() {
        thrown.expect(MessageException.class);
        thrown.expectMessage("Current version is too old. Please upgrade to Long Term Support version firstly.");
        DatabaseVersion version = Mockito.mock(DatabaseVersion.class);
        Mockito.when(version.getStatus()).thenReturn(REQUIRES_UPGRADE);
        Mockito.when(version.getVersion()).thenReturn(Optional.of(12L));
        start();
    }

    @Test
    public void log_warning_if_requires_upgrade() {
        DatabaseVersion version = Mockito.mock(DatabaseVersion.class);
        Mockito.when(version.getStatus()).thenReturn(REQUIRES_UPGRADE);
        Mockito.when(version.getVersion()).thenReturn(Optional.of(MIN_UPGRADE_VERSION));
        start();
        assertThat(logTester.logs()).hasSize(2);
        assertThat(logTester.logs(WARN)).contains("Database must be upgraded. Please backup database and browse /setup", ("\n################################################################################\n" + ("      Database must be upgraded. Please backup database and browse /setup\n" + "################################################################################")));
    }

    @Test
    public void do_nothing_if_up_to_date() {
        DatabaseVersion version = Mockito.mock(DatabaseVersion.class);
        Mockito.when(version.getStatus()).thenReturn(UP_TO_DATE);
        start();
        // no error
    }

    @Test
    public void upgrade_automatically_if_blue_green_deployment() {
        settings.setProperty("sonar.blueGreenEnabled", "true");
        DatabaseVersion version = Mockito.mock(DatabaseVersion.class);
        Mockito.when(version.getStatus()).thenReturn(REQUIRES_UPGRADE);
        Mockito.when(version.getVersion()).thenReturn(Optional.of(MIN_UPGRADE_VERSION));
        start();
        assertThat(logTester.logs(WARN)).isEmpty();
    }
}

