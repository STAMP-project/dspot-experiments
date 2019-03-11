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
import java.util.Date;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.db.CoreDbTester;


public class PopulateQualityGatesIsBuiltInTest {
    private static final long PAST = 10000000000L;

    private static final long NOW = 50000000000L;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateQualityGatesIsBuiltInTest.class, "quality_gates.sql");

    private System2 system2 = new TestSystem2().setNow(PopulateQualityGatesIsBuiltInTest.NOW);

    private PopulateQualityGatesIsBuiltIn underTest = new PopulateQualityGatesIsBuiltIn(db.database(), system2);

    @Test
    public void has_no_effect_if_table_is_empty() throws SQLException {
        underTest.execute();
        assertThat(db.countRowsOfTable("quality_gates")).isEqualTo(0);
    }

    @Test
    public void updates_sonarqube_way_is_build_in_column_to_true() throws SQLException {
        insertQualityGate("SonarQube way", null);
        underTest.execute();
        assertQualityGates(tuple("SonarQube way", true, new Date(PopulateQualityGatesIsBuiltInTest.PAST), new Date(PopulateQualityGatesIsBuiltInTest.NOW)));
    }

    @Test
    public void updates_none_sonarqube_way_is_build_in_column_to_false() throws SQLException {
        insertQualityGate("Other 1", null);
        insertQualityGate("Other 2", null);
        underTest.execute();
        assertQualityGates(tuple("Other 1", false, new Date(PopulateQualityGatesIsBuiltInTest.PAST), new Date(PopulateQualityGatesIsBuiltInTest.NOW)), tuple("Other 2", false, new Date(PopulateQualityGatesIsBuiltInTest.PAST), new Date(PopulateQualityGatesIsBuiltInTest.NOW)));
    }

    @Test
    public void does_nothing_when_built_in_column_is_set() throws SQLException {
        insertQualityGate("SonarQube way", true);
        insertQualityGate("Other way", false);
        underTest.execute();
        assertQualityGates(tuple("SonarQube way", true, new Date(PopulateQualityGatesIsBuiltInTest.PAST), new Date(PopulateQualityGatesIsBuiltInTest.PAST)), tuple("Other way", false, new Date(PopulateQualityGatesIsBuiltInTest.PAST), new Date(PopulateQualityGatesIsBuiltInTest.PAST)));
    }

    @Test
    public void execute_is_reentreant() throws SQLException {
        insertQualityGate("SonarQube way", null);
        insertQualityGate("Other way", null);
        underTest.execute();
        underTest.execute();
        assertQualityGates(tuple("SonarQube way", true, new Date(PopulateQualityGatesIsBuiltInTest.PAST), new Date(PopulateQualityGatesIsBuiltInTest.NOW)), tuple("Other way", false, new Date(PopulateQualityGatesIsBuiltInTest.PAST), new Date(PopulateQualityGatesIsBuiltInTest.NOW)));
    }
}

