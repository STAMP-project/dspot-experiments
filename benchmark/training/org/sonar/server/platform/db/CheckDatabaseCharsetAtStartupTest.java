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
package org.sonar.server.platform.db;


import DatabaseCharsetChecker.State.FRESH_INSTALL;
import DatabaseCharsetChecker.State.STARTUP;
import DatabaseCharsetChecker.State.UPGRADE;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.platform.ServerUpgradeStatus;
import org.sonar.server.platform.db.migration.charset.DatabaseCharsetChecker;


public class CheckDatabaseCharsetAtStartupTest {
    private ServerUpgradeStatus upgradeStatus = Mockito.mock(ServerUpgradeStatus.class);

    private DatabaseCharsetChecker charsetChecker = Mockito.mock(DatabaseCharsetChecker.class);

    private CheckDatabaseCharsetAtStartup underTest = new CheckDatabaseCharsetAtStartup(upgradeStatus, charsetChecker);

    @Test
    public void test_fresh_install() {
        Mockito.when(upgradeStatus.isFreshInstall()).thenReturn(true);
        underTest.start();
        Mockito.verify(charsetChecker).check(FRESH_INSTALL);
    }

    @Test
    public void test_upgrade() {
        Mockito.when(upgradeStatus.isUpgraded()).thenReturn(true);
        underTest.start();
        Mockito.verify(charsetChecker).check(UPGRADE);
    }

    @Test
    public void test_regular_startup() {
        Mockito.when(upgradeStatus.isFreshInstall()).thenReturn(false);
        underTest.start();
        Mockito.verify(charsetChecker).check(STARTUP);
    }
}

