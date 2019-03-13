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
package org.sonar.server.platform.db.migration.version.v65;


import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.CoreDbTester;


public class UpdateOrgQProfilesToPointToBuiltInProfilesTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(UpdateOrgQProfilesToPointToBuiltInProfilesTest.class, "initial.sql");

    private UpdateOrgQProfilesToPointToBuiltInProfiles underTest = new UpdateOrgQProfilesToPointToBuiltInProfiles(db.database());

    @Test
    public void has_no_effect_if_table_is_empty() throws SQLException {
        underTest.execute();
        assertThat(db.countRowsOfTable("rules_profiles")).isEqualTo(0);
    }

    @Test
    public void has_no_effect_if_organization_are_disabled() throws SQLException {
        String defaultOrgUuid = "DEFAULT_ORG_UUID";
        setDefaultOrganization(defaultOrgUuid);
        String sonarWayJava = "RP_UUID_1";
        String sonarWayJavascript = "RP_UUID_2";
        insertProfile(defaultOrgUuid, "OQP_UUID_1", sonarWayJava, "Sonar way", "Java", true, 1000000000L);
        insertProfile(defaultOrgUuid, "OQP_UUID_2", sonarWayJavascript, "Sonar way", "Javascript", true, null);
        insertProfile(defaultOrgUuid, "OQP_UUID_3", "RP_UUID_3", "Sonar way", "Cobol", true, null);
        insertProfile(defaultOrgUuid, "OQP_UUID_4", "RP_UUID_4", "My Sonar way", "Java", false, null);
        underTest.execute();
        assertThat(selectRulesProfiles()).containsExactlyInAnyOrder(tuple("OQP_UUID_1", sonarWayJava), tuple("OQP_UUID_2", sonarWayJavascript), tuple("OQP_UUID_3", "RP_UUID_3"), tuple("OQP_UUID_4", "RP_UUID_4"));
    }

    @Test
    public void update_org_qprofiles_to_point_to_built_in_rules_profiles() throws SQLException {
        enableOrganization();
        String defaultOrgUuid = "DEFAULT_ORG_UUID";
        setDefaultOrganization(defaultOrgUuid);
        String sonarWayJava = "RP_UUID_1";
        String sonarWayJavascript = "RP_UUID_2";
        insertProfile(defaultOrgUuid, "OQP_UUID_1", sonarWayJava, "Sonar way", "Java", true, 1000000000L);
        insertProfile(defaultOrgUuid, "OQP_UUID_2", sonarWayJavascript, "Sonar way", "Javascript", true, null);
        insertProfile(defaultOrgUuid, "OQP_UUID_3", "RP_UUID_3", "Sonar way", "Cobol", true, null);
        insertProfile("ORG_UUID_1", "OQP_UUID_4", "RP_UUID_4", "Sonar way", "Java", false, null);
        insertProfile("ORG_UUID_1", "OQP_UUID_5", "RP_UUID_5", "My Sonar way", "Java", false, null);
        insertProfile("ORG_UUID_2", "OQP_UUID_6", "RP_UUID_6", "Sonar way", "Javascript", false, null);
        insertProfile("ORG_UUID_2", "OQP_UUID_7", "RP_UUID_7", "Sonar way", "Python", false, null);
        insertProfile("ORG_UUID_2", "OQP_UUID_8", "RP_UUID_8", "Sonar way", "Java", false, 2000000000L);
        underTest.execute();
        assertThat(selectRulesProfiles()).containsExactlyInAnyOrder(tuple("OQP_UUID_1", sonarWayJava), tuple("OQP_UUID_2", sonarWayJavascript), tuple("OQP_UUID_3", "RP_UUID_3"), tuple("OQP_UUID_4", sonarWayJava), tuple("OQP_UUID_5", "RP_UUID_5"), tuple("OQP_UUID_6", sonarWayJavascript), tuple("OQP_UUID_7", "RP_UUID_7"), tuple("OQP_UUID_8", "RP_UUID_8"));
    }

    @Test
    public void migration_is_reentrant() throws SQLException {
        enableOrganization();
        String defaultOrgUuid = "DEFAULT_ORG_UUID";
        setDefaultOrganization(defaultOrgUuid);
        String sonarWayJava = "RP_UUID_1";
        String sonarWayJavascript = "RP_UUID_2";
        insertProfile(defaultOrgUuid, "OQP_UUID_1", sonarWayJava, "Sonar way", "Java", true, 1000000000L);
        insertProfile(defaultOrgUuid, "OQP_UUID_2", sonarWayJavascript, "Sonar way", "Javascript", true, null);
        insertProfile("ORG_UUID_1", "OQP_UUID_4", "RP_UUID_4", "Sonar way", "Java", false, null);
        insertProfile("ORG_UUID_1", "OQP_UUID_5", "RP_UUID_5", "My Sonar way", "Java", false, null);
        underTest.execute();
        underTest.execute();
        assertThat(selectRulesProfiles()).containsExactlyInAnyOrder(tuple("OQP_UUID_1", sonarWayJava), tuple("OQP_UUID_2", sonarWayJavascript), tuple("OQP_UUID_4", sonarWayJava), tuple("OQP_UUID_5", "RP_UUID_5"));
    }

    @Test
    public void crashed_migration_is_reentrant() throws SQLException {
        enableOrganization();
        String defaultOrgUuid = "DEFAULT_ORG_UUID";
        setDefaultOrganization(defaultOrgUuid);
        String sonarWayJava = "RP_UUID_1";
        String sonarWayJavascript = "RP_UUID_2";
        insertProfile(defaultOrgUuid, "OQP_UUID_1", sonarWayJava, "Sonar way", "Java", true, 1000000000L);
        insertProfile(defaultOrgUuid, "OQP_UUID_2", sonarWayJavascript, "Sonar way", "Javascript", true, null);
        insertOrgQProfile("ORG_UUID_1", "OQP_UUID_3", sonarWayJava);
        insertProfile("ORG_UUID_1", "OQP_UUID_4", "RP_UUID_4", "Sonar way", "Javascript", false, null);
        insertProfile("ORG_UUID_1", "OQP_UUID_5", "RP_UUID_5", "My Sonar way", "Java", false, null);
        underTest.execute();
        assertThat(selectRulesProfiles()).containsExactlyInAnyOrder(tuple("OQP_UUID_1", sonarWayJava), tuple("OQP_UUID_2", sonarWayJavascript), tuple("OQP_UUID_3", sonarWayJava), tuple("OQP_UUID_4", sonarWayJavascript), tuple("OQP_UUID_5", "RP_UUID_5"));
    }

    @Test
    public void fail_if_no_default_org_and_org_activated() throws SQLException {
        enableOrganization();
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Missing internal property: 'organization.default'");
        underTest.execute();
    }
}

