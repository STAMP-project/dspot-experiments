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
package org.sonar.server.platform.db.migration.version.v60;


import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.CoreDbTester;


public class PopulateLastUsedColumnOfRulesProfilesTest {
    private static final String QUALITY_PROFILES_TABLE = "rules_profiles";

    private static final String METRICS_TABLE = "metrics";

    private static final String MEASURES_TABLE = "project_measures";

    private static final String SNAPSHOTS_TABLE = "snapshots";

    private static final String METRIC_ID = "1";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateLastUsedColumnOfRulesProfilesTest.class, "rules_profiles.sql");

    PopulateLastUsedColumnOfRulesProfiles underTest = new PopulateLastUsedColumnOfRulesProfiles(db.database());

    @Test
    public void migration_has_no_effect_on_empty_tables() throws SQLException {
        underTest.execute();
        assertThat(db.countRowsOfTable(PopulateLastUsedColumnOfRulesProfilesTest.QUALITY_PROFILES_TABLE)).isEqualTo(0);
        assertThat(db.countRowsOfTable(PopulateLastUsedColumnOfRulesProfilesTest.METRICS_TABLE)).isEqualTo(0);
        assertThat(db.countRowsOfTable(PopulateLastUsedColumnOfRulesProfilesTest.MEASURES_TABLE)).isEqualTo(0);
        assertThat(db.countRowsOfTable(PopulateLastUsedColumnOfRulesProfilesTest.SNAPSHOTS_TABLE)).isEqualTo(0);
    }

    @Test
    public void migration_update_quality_profiles_last_used() throws SQLException {
        insertQualityProfilesMetric();
        insertQualityProfile(1, "first-quality-profile");
        insertQualityProfile(2, "second-quality-profile");
        insertQualityProfile(3, "third-quality-profile");
        insertQualityProfile(4, "fourth-quality-profile");
        insertMeasure(1, "first-quality-profile", "second-quality-profile");
        insertMeasure(2, "second-quality-profile", "third-quality-profile");
        underTest.execute();
        assertLastUsedForQP("first-quality-profile", 1);
        assertLastUsedForQP("second-quality-profile", 2);
        assertLastUsedForQP("third-quality-profile", 2);
        assertNoLastUsedForQP("fourth-quality-profile");
    }

    @Test
    public void migration_is_reentrant() throws SQLException {
        insertQualityProfilesMetric();
        insertQualityProfile(1, "first-quality-profile");
        insertMeasure(1, "first-quality-profile");
        underTest.execute();
        assertLastUsedForQP("first-quality-profile", 1);
        underTest.execute();
        assertLastUsedForQP("first-quality-profile", 1);
    }
}

