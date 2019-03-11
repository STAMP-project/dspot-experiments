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
package org.sonar.server.platform.db.migration.version.v76;


import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.sql.SQLException;
import java.util.Random;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.db.CoreDbTester;


@RunWith(DataProviderRunner.class)
public class MigrateNoMoreUsedQualityGateConditionsTest {
    private static final int DIRECTION_WORST = -1;

    private static final int DIRECTION_BETTER = 1;

    private static final int DIRECTION_NONE = 0;

    private static final long PAST = 10000000000L;

    private static final long NOW = 50000000000L;

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(MigrateNoMoreUsedQualityGateConditionsTest.class, "qg-schema.sql");

    private System2 system2 = new TestSystem2().setNow(MigrateNoMoreUsedQualityGateConditionsTest.NOW);

    private Random random = new Random();

    private MigrateNoMoreUsedQualityGateConditions underTest = new MigrateNoMoreUsedQualityGateConditions(db.database(), system2);

    @Test
    public void remove_conditions_using_only_warning() throws SQLException {
        long qualityGate = insertQualityGate(false);
        long ncloc = insertMetric("ncloc", MigrateNoMoreUsedQualityGateConditionsTest.DIRECTION_WORST, "INT");
        long lines = insertMetric("lines", MigrateNoMoreUsedQualityGateConditionsTest.DIRECTION_WORST, "INT");
        long issues = insertMetric("violations", MigrateNoMoreUsedQualityGateConditionsTest.DIRECTION_WORST, "INT");
        long coverage = insertMetric("coverage", MigrateNoMoreUsedQualityGateConditionsTest.DIRECTION_BETTER, "PERCENT");
        long conditionWithWarning1 = insertCondition(qualityGate, ncloc, "GT", null, "10", null);
        long conditionWithWarning2 = insertCondition(qualityGate, lines, "GT", null, "15", null);
        long conditionWithError = insertCondition(qualityGate, issues, "GT", "5", null, null);
        underTest.execute();
        assertConditions(tuple(conditionWithError, issues, "5", null, null));
    }

    @Test
    public void update_conditions_using_error_and_warning() throws SQLException {
        long qualityGate = insertQualityGate(false);
        long issues = insertMetric("violations", MigrateNoMoreUsedQualityGateConditionsTest.DIRECTION_WORST, "INT");
        long coverage = insertMetric("coverage", MigrateNoMoreUsedQualityGateConditionsTest.DIRECTION_BETTER, "PERCENT");
        long newLines = insertMetric("new_lines", MigrateNoMoreUsedQualityGateConditionsTest.DIRECTION_WORST, "INT");
        long conditionWithError = insertCondition(qualityGate, issues, "GT", "5", null, null);
        long conditionWithErrorAndWarning1 = insertCondition(qualityGate, coverage, "LT", "5", "10", null);
        long conditionWithErrorAndWarning2 = insertCondition(qualityGate, newLines, "GT", "7", "13", 1);
        underTest.execute();
        assertConditions(tuple(conditionWithError, issues, "5", null, null), tuple(conditionWithErrorAndWarning1, coverage, "5", null, null), tuple(conditionWithErrorAndWarning2, newLines, "7", null, 1L));
    }

    @Test
    public void update_condition_using_leak_period_metric_when_condition_on_new_metric_exists_but_using_bad_operator() throws SQLException {
        long qualityGate = insertQualityGate(false);
        long linesToCover = insertMetric("lines_to_cover", MigrateNoMoreUsedQualityGateConditionsTest.DIRECTION_WORST, "INT");
        long newLinesToCover = insertMetric("new_lines_to_cover", MigrateNoMoreUsedQualityGateConditionsTest.DIRECTION_WORST, "INT");
        // This condition should be migrated to use new_lines_to_cover metric
        long conditionOnLinesToCover = insertCondition(qualityGate, linesToCover, "GT", "10", null, 1);
        // This condition should be removed as using a no more supported operator
        long conditionOnNewLinesToCover = insertCondition(qualityGate, newLinesToCover, "EQ", "5", null, 1);
        underTest.execute();
        assertConditions(tuple(conditionOnLinesToCover, newLinesToCover, "10", null, 1L));
    }
}

