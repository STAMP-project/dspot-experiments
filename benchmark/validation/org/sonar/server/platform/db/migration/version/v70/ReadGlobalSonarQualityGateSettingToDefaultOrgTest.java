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
package org.sonar.server.platform.db.migration.version.v70;


import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.db.CoreDbTester;
import org.sonar.server.platform.db.migration.step.DataChange;


public class ReadGlobalSonarQualityGateSettingToDefaultOrgTest {
    private static final long PAST = 10000000000L;

    private static final long NOW = 20000000000L;

    private System2 system2 = new TestSystem2().setNow(ReadGlobalSonarQualityGateSettingToDefaultOrgTest.NOW);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(ReadGlobalSonarQualityGateSettingToDefaultOrgTest.class, "initial.sql");

    private DataChange underTest = new ReadGlobalSonarQualityGateSettingToDefaultOrg(db.database(), system2);

    @Test
    public void read_sonar_quality_gate_setting_and_update_default_organization() throws SQLException {
        String defaultQualityGate = insertQualityGate();
        String otherQualityGate = insertQualityGate();
        String defaultOrganization = insertOrganization(otherQualityGate);
        String otherOrganization = insertOrganization(otherQualityGate);
        insertDefaultOrgProperty(defaultOrganization);
        insertSetting("sonar.qualitygate", selectQualityGateId(defaultQualityGate));
        underTest.execute();
        assertDefaultQualityGate(defaultOrganization, tuple(defaultQualityGate, ReadGlobalSonarQualityGateSettingToDefaultOrgTest.NOW));
    }

    @Test
    public void does_nothing_when_no_default_quality_gate_setting() throws Exception {
        String defaultQualityGate = insertQualityGate();
        String defaultOrganization = insertOrganization(defaultQualityGate);
        insertDefaultOrgProperty(defaultOrganization);
        insertQualityGate();
        underTest.execute();
        assertDefaultQualityGate(defaultOrganization, tuple(defaultQualityGate, ReadGlobalSonarQualityGateSettingToDefaultOrgTest.PAST));
    }

    @Test
    public void migration_is_reentrant() throws Exception {
        String defaultOrganization = insertOrganization(null);
        insertDefaultOrgProperty(defaultOrganization);
        String qualityGate = insertQualityGate();
        insertSetting("sonar.qualitygate", selectQualityGateId(qualityGate));
        underTest.execute();
        assertDefaultQualityGate(defaultOrganization, tuple(qualityGate, ReadGlobalSonarQualityGateSettingToDefaultOrgTest.NOW));
        underTest.execute();
        assertDefaultQualityGate(defaultOrganization, tuple(qualityGate, ReadGlobalSonarQualityGateSettingToDefaultOrgTest.NOW));
    }

    @Test
    public void fail_when_no_default_organization() throws Exception {
        String qualityGate = insertQualityGate();
        insertSetting("sonar.qualitygate", selectQualityGateId(qualityGate));
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Default organization uuid is missing");
        underTest.execute();
    }
}

