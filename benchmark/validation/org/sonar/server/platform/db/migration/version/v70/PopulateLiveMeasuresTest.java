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
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.db.CoreDbTester;


public class PopulateLiveMeasuresTest {
    private System2 system2 = new TestSystem2().setNow(1500000000000L);

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateLiveMeasuresTest.class, "initial.sql");

    private PopulateLiveMeasures underTest = new PopulateLiveMeasures(db.database(), system2);

    @Test
    public void do_nothing_when_no_data() throws SQLException {
        assertThat(db.countRowsOfTable("PROJECT_MEASURES")).isEqualTo(0);
        underTest.execute();
        assertThat(db.countRowsOfTable("LIVE_MEASURES")).isEqualTo(0);
    }

    @Test
    public void execute_must_update_database() throws SQLException {
        generateProjectMeasures();
        underTest.execute();
        assertThat(getLiveMeasures()).extracting(field("COMPONENT_UUID"), field("PROJECT_UUID"), field("METRIC_ID"), field("VALUE"), field("TEXT_VALUE"), field("VARIATION"), field("MEASURE_DATA")).containsExactlyInAnyOrder(generateLiveMeasures());
    }

    @Test
    public void migration_is_reentrant() throws SQLException {
        generateProjectMeasures();
        underTest.execute();
        underTest.execute();
        assertThat(getLiveMeasures()).extracting(field("COMPONENT_UUID"), field("PROJECT_UUID"), field("METRIC_ID"), field("VALUE"), field("TEXT_VALUE"), field("VARIATION"), field("MEASURE_DATA")).containsExactlyInAnyOrder(generateLiveMeasures());
    }
}

