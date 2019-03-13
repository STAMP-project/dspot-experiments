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
package org.sonar.server.platform.db.migration.version.v62;


import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.sql.SQLException;
import java.util.Map;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.sonar.db.CoreDbTester;


@RunWith(DataProviderRunner.class)
public class UpdateQualityGateConditionsOnCoverageTest {
    private static final String TABLE_QUALITY_GATES = "quality_gates";

    private static final String TABLE_QUALITY_GATE_CONDITIONS = "quality_gate_conditions";

    @Rule
    public CoreDbTester dbTester = CoreDbTester.createForSchema(UpdateQualityGateConditionsOnCoverageTest.class, "schema.sql");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private UpdateQualityGateConditionsOnCoverage underTest = new UpdateQualityGateConditionsOnCoverage(dbTester.database());

    @Test
    public void move_overall_coverage_condition_to_coverage() throws SQLException {
        Map<String, Long> metricIdsByMetricKeys = insertSampleMetrics();
        long qualityGateId = insertQualityGate("default");
        insertQualityGateCondition(qualityGateId, metricIdsByMetricKeys.get("overall_coverage"), null, "GT", "10", null);
        underTest.execute();
        assertThat(dbTester.countRowsOfTable(UpdateQualityGateConditionsOnCoverageTest.TABLE_QUALITY_GATE_CONDITIONS)).isEqualTo(1);
        verifyConditions(qualityGateId, new UpdateQualityGateConditionsOnCoverageTest.QualityGateCondition("coverage", null, "GT", "10", null));
    }

    @Test
    public void move_overall_coverage_condition_to_coverage_when_overall_coverage_exists_condition_on_overall_coverage_exists() throws SQLException {
        Map<String, Long> metricIdsByMetricKeys = insertSampleMetrics();
        long qualityGateId = insertQualityGate("default");
        insertQualityGateCondition(qualityGateId, metricIdsByMetricKeys.get("overall_coverage"), null, "GT", "10", null);
        insertQualityGateCondition(qualityGateId, metricIdsByMetricKeys.get("coverage"), null, "LT", null, "20");
        underTest.execute();
        assertThat(dbTester.countRowsOfTable(UpdateQualityGateConditionsOnCoverageTest.TABLE_QUALITY_GATE_CONDITIONS)).isEqualTo(1);
        verifyConditions(qualityGateId, new UpdateQualityGateConditionsOnCoverageTest.QualityGateCondition("coverage", null, "GT", "10", null));
    }

    @Test
    public void remove_it_coverage_condition_when_overall_coverage_condition_exists_and_no_coverage_condition() throws Exception {
        Map<String, Long> metricIdsByMetricKeys = insertSampleMetrics();
        long qualityGateId = insertQualityGate("default");
        insertQualityGateCondition(qualityGateId, metricIdsByMetricKeys.get("overall_coverage"), null, "GT", "10", null);
        insertQualityGateCondition(qualityGateId, metricIdsByMetricKeys.get("it_coverage"), null, "LT", null, "20");
        underTest.execute();
        assertThat(dbTester.countRowsOfTable(UpdateQualityGateConditionsOnCoverageTest.TABLE_QUALITY_GATE_CONDITIONS)).isEqualTo(1);
        verifyConditions(qualityGateId, new UpdateQualityGateConditionsOnCoverageTest.QualityGateCondition("coverage", null, "GT", "10", null));
    }

    @Test
    public void keep_coverage_condition_when_no_overall_and_it_coverage() throws SQLException {
        Map<String, Long> metricIdsByMetricKeys = insertSampleMetrics();
        long qualityGateId = insertQualityGate("default");
        insertQualityGateCondition(qualityGateId, metricIdsByMetricKeys.get("coverage"), null, "GT", "10", null);
        underTest.execute();
        assertThat(dbTester.countRowsOfTable(UpdateQualityGateConditionsOnCoverageTest.TABLE_QUALITY_GATE_CONDITIONS)).isEqualTo(1);
        verifyConditions(qualityGateId, new UpdateQualityGateConditionsOnCoverageTest.QualityGateCondition("coverage", null, "GT", "10", null));
    }

    @Test
    public void remove_it_coverage_condition_when_coverage_condition_exists_and_no_overall_coverage_condition() throws SQLException {
        Map<String, Long> metricIdsByMetricKeys = insertSampleMetrics();
        long qualityGateId = insertQualityGate("default");
        insertQualityGateCondition(qualityGateId, metricIdsByMetricKeys.get("coverage"), null, "GT", "10", null);
        insertQualityGateCondition(qualityGateId, metricIdsByMetricKeys.get("it_coverage"), null, "LT", null, "20");
        underTest.execute();
        assertThat(dbTester.countRowsOfTable(UpdateQualityGateConditionsOnCoverageTest.TABLE_QUALITY_GATE_CONDITIONS)).isEqualTo(1);
        verifyConditions(qualityGateId, new UpdateQualityGateConditionsOnCoverageTest.QualityGateCondition("coverage", null, "GT", "10", null));
    }

    @Test
    public void move_it_coverage_condition_to_coverage_when_only_it_coverage_condition() throws SQLException {
        Map<String, Long> metricIdsByMetricKeys = insertSampleMetrics();
        long qualityGateId = insertQualityGate("default");
        insertQualityGateCondition(qualityGateId, metricIdsByMetricKeys.get("it_coverage"), null, "GT", "10", null);
        underTest.execute();
        assertThat(dbTester.countRowsOfTable(UpdateQualityGateConditionsOnCoverageTest.TABLE_QUALITY_GATE_CONDITIONS)).isEqualTo(1);
        verifyConditions(qualityGateId, new UpdateQualityGateConditionsOnCoverageTest.QualityGateCondition("coverage", null, "GT", "10", null));
    }

    @Test
    public void move_new_coverage_conditions() throws SQLException {
        Map<String, Long> metricIdsByMetricKeys = insertMetrics("new_coverage", "new_overall_coverage", "new_it_coverage");
        long qualityGate1 = insertQualityGate("qualityGate1");
        insertQualityGateCondition(qualityGate1, metricIdsByMetricKeys.get("new_coverage"), 1L, "GT", "10", null);
        insertQualityGateCondition(qualityGate1, metricIdsByMetricKeys.get("new_overall_coverage"), 1L, "GT", "7", "15");
        insertQualityGateCondition(qualityGate1, metricIdsByMetricKeys.get("new_it_coverage"), 2L, "LT", "8", null);
        long qualityGate2 = insertQualityGate("qualityGate2");
        insertQualityGateCondition(qualityGate2, metricIdsByMetricKeys.get("new_overall_coverage"), 2L, "GT", "15", null);
        insertQualityGateCondition(qualityGate2, metricIdsByMetricKeys.get("new_it_coverage"), 2L, "GT", null, "5");
        long qualityGate3 = insertQualityGate("qualityGate3");
        insertQualityGateCondition(qualityGate3, metricIdsByMetricKeys.get("new_it_coverage"), 3L, "GT", null, "5");
        underTest.execute();
        assertThat(dbTester.countRowsOfTable(UpdateQualityGateConditionsOnCoverageTest.TABLE_QUALITY_GATE_CONDITIONS)).isEqualTo(3);
        verifyConditions(qualityGate1, new UpdateQualityGateConditionsOnCoverageTest.QualityGateCondition("new_coverage", 1L, "GT", "7", "15"));
        verifyConditions(qualityGate2, new UpdateQualityGateConditionsOnCoverageTest.QualityGateCondition("new_coverage", 2L, "GT", "15", null));
        verifyConditions(qualityGate3, new UpdateQualityGateConditionsOnCoverageTest.QualityGateCondition("new_coverage", 3L, "GT", null, "5"));
    }

    @Test
    public void does_not_update_conditions_on_none_related_coverage_metrics() throws Exception {
        insertMetrics();
        long metric1 = insertMetric("metric1");
        long metric2 = insertMetric("metric2");
        long qualityGate1 = insertQualityGate("qualityGate1");
        insertQualityGateCondition(qualityGate1, metric1, null, "GT", "10", null);
        long qualityGate2 = insertQualityGate("qualityGate2");
        insertQualityGateCondition(qualityGate2, metric2, null, "LT", null, "20");
        underTest.execute();
        assertThat(dbTester.countRowsOfTable(UpdateQualityGateConditionsOnCoverageTest.TABLE_QUALITY_GATE_CONDITIONS)).isEqualTo(2);
        verifyConditions(qualityGate1, new UpdateQualityGateConditionsOnCoverageTest.QualityGateCondition("metric1", null, "GT", "10", null));
        verifyConditions(qualityGate2, new UpdateQualityGateConditionsOnCoverageTest.QualityGateCondition("metric2", null, "LT", null, "20"));
    }

    @Test
    public void move_conditions_linked_to_same_metric() throws Exception {
        insertMetric("coverage");
        long metricId = insertMetric("overall_coverage");
        long qualityGate = insertQualityGate("qualityGate");
        insertQualityGateCondition(qualityGate, metricId, null, "GT", "7", "15");
        insertQualityGateCondition(qualityGate, metricId, 1L, "GT", "10", null);
        underTest.execute();
        assertThat(dbTester.countRowsOfTable(UpdateQualityGateConditionsOnCoverageTest.TABLE_QUALITY_GATE_CONDITIONS)).isEqualTo(2);
        verifyConditions(qualityGate, new UpdateQualityGateConditionsOnCoverageTest.QualityGateCondition("coverage", null, "GT", "7", "15"), new UpdateQualityGateConditionsOnCoverageTest.QualityGateCondition("coverage", 1L, "GT", "10", null));
    }

    @Test
    public void does_nothing_when_no_quality_gates() throws Exception {
        insertMetrics("coverage", "new_coverage", "overall_coverage");
        underTest.execute();
        assertThat(dbTester.countRowsOfTable(UpdateQualityGateConditionsOnCoverageTest.TABLE_QUALITY_GATE_CONDITIONS)).isZero();
    }

    @Test
    public void does_nothing_when_no_metrics() throws Exception {
        underTest.execute();
        assertThat(dbTester.countRowsOfTable(UpdateQualityGateConditionsOnCoverageTest.TABLE_QUALITY_GATE_CONDITIONS)).isZero();
    }

    private static class QualityGateCondition {
        String metricKey;

        Long period;

        String operator;

        String valueError;

        String valueWarning;

        public QualityGateCondition(String metricKey, @Nullable
        Long period, String operator, @Nullable
        String valueError, @Nullable
        String valueWarning) {
            this.metricKey = metricKey;
            this.period = period;
            this.operator = operator;
            this.valueError = valueError;
            this.valueWarning = valueWarning;
        }

        QualityGateCondition(Map<String, Object> map) {
            this.metricKey = ((String) (map.get("metricKey")));
            this.period = ((Long) (map.get("period")));
            this.operator = ((String) (map.get("operator")));
            this.valueError = ((String) (map.get("error")));
            this.valueWarning = ((String) (map.get("warning")));
        }

        public String getMetricKey() {
            return metricKey;
        }

        @CheckForNull
        public Long getPeriod() {
            return period;
        }

        public String getOperator() {
            return operator;
        }

        @CheckForNull
        public String getValueError() {
            return valueError;
        }

        @CheckForNull
        public String getValueWarning() {
            return valueWarning;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            UpdateQualityGateConditionsOnCoverageTest.QualityGateCondition that = ((UpdateQualityGateConditionsOnCoverageTest.QualityGateCondition) (o));
            return new EqualsBuilder().append(metricKey, that.getMetricKey()).append(period, that.getPeriod()).append(operator, that.getOperator()).append(valueError, that.getValueError()).append(valueWarning, that.getValueWarning()).isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(15, 31).append(metricKey).append(period).append(operator).append(valueError).append(valueWarning).toHashCode();
        }

        @Override
        public String toString() {
            return ((((((((((((("QualityGateCondition{" + "metricKey='") + (metricKey)) + '\'') + ", period=") + (period)) + ", operator=") + (operator)) + ", valueError='") + (valueError)) + '\'') + ", valueWarning='") + (valueWarning)) + '\'') + '}';
        }
    }
}

