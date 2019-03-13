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
package org.sonar.server.qualitygate.ws;


import ProjectStatusResponse.Comparator.GT;
import ProjectStatusResponse.Comparator.LT;
import ProjectStatusResponse.Condition;
import ProjectStatusResponse.Period;
import ProjectStatusResponse.Status.ERROR;
import ProjectStatusResponse.Status.OK;
import ProjectStatusResponse.Status.WARN;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.component.SnapshotDto;
import org.sonarqube.ws.Qualitygates.ProjectStatusResponse;
import org.sonarqube.ws.Qualitygates.ProjectStatusResponse.ProjectStatus;


public class QualityGateDetailsFormatterTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private QualityGateDetailsFormatter underTest;

    @Test
    public void map_level_conditions_and_periods() throws IOException {
        String measureData = IOUtils.toString(getClass().getResource("QualityGateDetailsFormatterTest/quality_gate_details.json"));
        SnapshotDto snapshot = new SnapshotDto().setPeriodMode("last_version").setPeriodParam("2015-12-07").setPeriodDate(1449404331764L);
        underTest = QualityGateDetailsFormatterTest.newQualityGateDetailsFormatter(measureData, snapshot);
        ProjectStatus result = underTest.format();
        assertThat(result.getStatus()).isEqualTo(ERROR);
        // check conditions
        assertThat(result.getConditionsCount()).isEqualTo(3);
        List<ProjectStatusResponse.Condition> conditions = result.getConditionsList();
        assertThat(conditions).extracting("status").containsExactly(ERROR, WARN, OK);
        assertThat(conditions).extracting("metricKey").containsExactly("new_coverage", "new_blocker_violations", "new_critical_violations");
        assertThat(conditions).extracting("comparator").containsExactly(LT, GT, GT);
        assertThat(conditions).extracting("periodIndex").containsExactly(1, 1, 1);
        assertThat(conditions).extracting("warningThreshold").containsOnly("80", "");
        assertThat(conditions).extracting("errorThreshold").containsOnly("85", "0", "0");
        assertThat(conditions).extracting("actualValue").containsExactly("82.2985024398452", "1", "0");
        // check periods
        assertThat(result.getPeriodsCount()).isEqualTo(1);
        List<ProjectStatusResponse.Period> periods = result.getPeriodsList();
        assertThat(periods).extracting("index").containsExactly(1);
        assertThat(periods).extracting("mode").containsExactly("last_version");
        assertThat(periods).extracting("parameter").containsExactly("2015-12-07");
        assertThat(periods.get(0).getDate()).isEqualTo(formatDateTime(snapshot.getPeriodDate()));
    }

    @Test
    public void ignore_period_not_set_on_leak_period() throws IOException {
        String measureData = IOUtils.toString(getClass().getResource("QualityGateDetailsFormatterTest/non_leak_period.json"));
        SnapshotDto snapshot = new SnapshotDto().setPeriodMode("last_version").setPeriodParam("2015-12-07").setPeriodDate(1449404331764L);
        underTest = QualityGateDetailsFormatterTest.newQualityGateDetailsFormatter(measureData, snapshot);
        ProjectStatus result = underTest.format();
        // check conditions
        assertThat(result.getConditionsCount()).isEqualTo(1);
        List<ProjectStatusResponse.Condition> conditions = result.getConditionsList();
        assertThat(conditions).extracting("status").containsExactly(ERROR);
        assertThat(conditions).extracting("metricKey").containsExactly("new_coverage");
        assertThat(conditions).extracting("comparator").containsExactly(LT);
        assertThat(conditions).extracting("periodIndex").containsExactly(1);
        assertThat(conditions).extracting("errorThreshold").containsOnly("85");
        assertThat(conditions).extracting("actualValue").containsExactly("82.2985024398452");
    }

    @Test
    public void fail_when_measure_level_is_unknown() {
        String measureData = "{\n" + (((((((((((("  \"level\": \"UNKNOWN\",\n" + "  \"conditions\": [\n") + "    {\n") + "      \"metric\": \"new_coverage\",\n") + "      \"op\": \"LT\",\n") + "      \"period\": 1,\n") + "      \"warning\": \"80\",\n") + "      \"error\": \"85\",\n") + "      \"actual\": \"82.2985024398452\",\n") + "      \"level\": \"ERROR\"\n") + "    }\n") + "  ]\n") + "}");
        underTest = QualityGateDetailsFormatterTest.newQualityGateDetailsFormatter(measureData, new SnapshotDto());
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Unknown quality gate status 'UNKNOWN'");
        underTest.format();
    }

    @Test
    public void fail_when_measure_op_is_unknown() {
        String measureData = "{\n" + (((((((((((("  \"level\": \"ERROR\",\n" + "  \"conditions\": [\n") + "    {\n") + "      \"metric\": \"new_coverage\",\n") + "      \"op\": \"UNKNOWN\",\n") + "      \"period\": 1,\n") + "      \"warning\": \"80\",\n") + "      \"error\": \"85\",\n") + "      \"actual\": \"82.2985024398452\",\n") + "      \"level\": \"ERROR\"\n") + "    }\n") + "  ]\n") + "}");
        underTest = QualityGateDetailsFormatterTest.newQualityGateDetailsFormatter(measureData, new SnapshotDto());
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Unknown quality gate comparator 'UNKNOWN'");
        underTest.format();
    }
}

