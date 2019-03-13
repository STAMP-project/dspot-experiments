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


public class PopulateUserUpdatedAtOfRulesProfilesTest {
    private static final String TABLE_QUALITY_PROFILES = "rules_profiles";

    private static final String TABLE_ACTIVITIES = "activities";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateUserUpdatedAtOfRulesProfilesTest.class, "schema.sql");

    PopulateUserUpdatedAtOfRulesProfiles underTest = new PopulateUserUpdatedAtOfRulesProfiles(db.database());

    @Test
    public void migration_has_no_effect_on_empty_tables() throws SQLException {
        underTest.execute();
        assertThat(db.countRowsOfTable(PopulateUserUpdatedAtOfRulesProfilesTest.TABLE_QUALITY_PROFILES)).isEqualTo(0);
        assertThat(db.countRowsOfTable(PopulateUserUpdatedAtOfRulesProfilesTest.TABLE_ACTIVITIES)).isEqualTo(0);
    }

    @Test
    public void migration_update_quality_profiles_user_updated_at() throws SQLException {
        insertQualityProfile(1, "first-quality-profile");
        insertActivity("first-quality-profile", "my-login", 100000000L);
        insertActivity("first-quality-profile", null, 2000000000L);
        insertActivity("first-quality-profile", "my-login", 1100000000L);
        insertQualityProfile(2, "second-quality-profile");
        insertActivity("second-quality-profile", null, 100000000L);
        insertQualityProfile(3, "third-quality-profile");
        insertQualityProfile(4, "fourth-quality-profile");
        insertActivity("fourth-quality-profile", "my-login", 100000000L);
        underTest.execute();
        assertUserUpdatedAt("first-quality-profile", 1100000000L);
        assertNoUserUpdatedAtDate("second-quality-profile");
        assertNoUserUpdatedAtDate("third-quality-profile");
        assertUserUpdatedAt("fourth-quality-profile", 100000000L);
    }

    @Test
    public void migration_is_reentrant() throws SQLException {
        insertQualityProfile(1, "first-quality-profile");
        insertActivity("first-quality-profile", "my-login", 1000000000L);
        underTest.execute();
        assertUserUpdatedAt("first-quality-profile", 1000000000L);
        underTest.execute();
        assertUserUpdatedAt("first-quality-profile", 1000000000L);
    }
}

