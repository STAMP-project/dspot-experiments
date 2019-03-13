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
package org.sonar.server.platform.db.migration.version.v73;


import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.core.util.UuidFactoryFast;
import org.sonar.db.CoreDbTester;


public class FixMissingQualityProfilesOnOrganizationsTest {
    private static final long PAST = 10000000000L;

    private static final long NOW = 50000000000L;

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(FixMissingQualityProfilesOnOrganizationsTest.class, "schema.sql");

    private MapSettings settings = new MapSettings();

    private System2 system2 = new TestSystem2().setNow(FixMissingQualityProfilesOnOrganizationsTest.NOW);

    private FixMissingQualityProfilesOnOrganizations underTest = new FixMissingQualityProfilesOnOrganizations(db.database(), system2, UuidFactoryFast.getInstance(), settings.asConfig());

    @Test
    public void migration_is_reentrant_on_sonarqube() throws SQLException {
        underTest.execute();
        underTest.execute();
    }

    @Test
    public void create_missing_links_with_builtin() throws SQLException {
        setSonarCloud();
        String orgUuid = insertOrganization();
        String qProfileUuid = insertRulesProfiles("xoo profile", "xoo", true);
        underTest.execute();
        assertDefaultQProfiles(tuple(orgUuid, "xoo", retrieveOrgQProfile(orgUuid, qProfileUuid)));
        assertOrgQProfiles(tuple(orgUuid, qProfileUuid));
    }

    @Test
    public void does_nothing_when_no_missing_built_in_profile() throws SQLException {
        setSonarCloud();
        insertOrganization();
        insertRulesProfiles("xoo profile", "xoo", false);
        underTest.execute();
        assertDefaultQProfiles();
        assertOrgQProfiles();
    }

    @Test
    public void prefer_SonarWay_BuiltIn_quality_profile_as_default() throws SQLException {
        setSonarCloud();
        String orgUuid = insertOrganization();
        String anotherRuleProfileUuid = insertRulesProfiles("xoo profile", "xoo", true);
        String sonarWayQProfileUuid = insertRulesProfiles("Sonar way", "xoo", true);
        underTest.execute();
        assertOrgQProfiles(tuple(orgUuid, anotherRuleProfileUuid), tuple(orgUuid, sonarWayQProfileUuid));
        assertDefaultQProfiles(tuple(orgUuid, "xoo", retrieveOrgQProfile(orgUuid, sonarWayQProfileUuid)));
    }

    @Test
    public void dont_create_duplicates_when_records_exist() throws SQLException {
        setSonarCloud();
        String orgUuid = insertOrganization();
        String qProfileUuid = insertRulesProfiles("xoo profile", "xoo", true);
        insertDefaultQProfiles(orgUuid, "xoo", qProfileUuid);
        insertOrgQProfiles(orgUuid, qProfileUuid);
        underTest.execute();
        assertDefaultQProfiles(tuple(orgUuid, "xoo", qProfileUuid));
        assertOrgQProfiles(tuple(orgUuid, qProfileUuid));
    }

    @Test
    public void create_missing_default_qprofiles() throws SQLException {
        setSonarCloud();
        String orgUuid = insertOrganization();
        String qProfileUuid = insertRulesProfiles("xoo profile", "xoo", true);
        String orgQProfileUuid = insertOrgQProfiles(orgUuid, qProfileUuid);
        underTest.execute();
        assertDefaultQProfiles(tuple(orgUuid, "xoo", orgQProfileUuid));
        assertOrgQProfiles(tuple(orgUuid, qProfileUuid));
    }

    @Test
    public void migration_is_reentrant_on_sonarcloud() throws SQLException {
        setSonarCloud();
        underTest.execute();
        underTest.execute();
    }
}

