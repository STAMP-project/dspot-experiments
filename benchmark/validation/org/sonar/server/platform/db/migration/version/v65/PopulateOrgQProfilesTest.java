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
import java.util.Map;
import java.util.Random;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.AlwaysIncreasingSystem2;
import org.sonar.db.CoreDbTester;


public class PopulateOrgQProfilesTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateOrgQProfilesTest.class, "initial.sql");

    private System2 system2 = new AlwaysIncreasingSystem2();

    private PopulateOrgQProfiles underTest = new PopulateOrgQProfiles(db.database(), system2);

    @Test
    public void migration_is_reentrant() throws SQLException {
        insertRulesProfile("ORG_1", "java", "u1", null, true, 1000L, 1100L);
        insertRulesProfile("ORG_2", "js", "u2", "u1", true, 1000L, 1100L);
        // org1 is already processed
        insertOrgQProfile("u1", "ORG_1", "RPU1");
        underTest.execute();
        assertThat(countRows()).isEqualTo(2);
        Map<String, Object> qprofile1 = selectOrgQProfile("u1", "ORG_1");
        Map<String, Object> qprofile2 = selectOrgQProfile("u2", "ORG_2");
        assertThat(qprofile1.get("UUID")).isEqualTo("u1");
        assertThat(qprofile1.get("ORGANIZATION_UUID")).isEqualTo("ORG_1");
        assertThat(qprofile1.get("RULES_PROFILE_UUID")).isEqualTo("RPU1");// Ok if not overridden ?

        assertThat(qprofile1.get("PARENT_UUID")).isNull();
        assertThat(qprofile2.get("UUID")).isEqualTo("u2");
        assertThat(qprofile2.get("ORGANIZATION_UUID")).isEqualTo("ORG_2");
        assertThat(qprofile2.get("RULES_PROFILE_UUID")).isEqualTo("u2");
        assertThat(qprofile2.get("PARENT_UUID")).isEqualTo("u1");
        assertThat(qprofile2.get("LAST_USED")).isEqualTo(1000L);
        assertThat(qprofile2.get("USER_UPDATED_AT")).isEqualTo(1100L);
    }

    @Test
    public void migration_must_create_as_much_as_rules_profile() throws SQLException {
        Random random = new Random();
        int nbRulesProfile = 100 + (random.nextInt(100));
        IntStream.range(0, nbRulesProfile).forEach(( i) -> insertRulesProfile(("ORG_" + i), "java", ("uuid" + i), (random.nextBoolean() ? "ORG_" + (random.nextInt((i + 1))) : null), random.nextBoolean(), null, null));
        underTest.execute();
        assertThat(countRows()).isEqualTo(nbRulesProfile);
    }
}

