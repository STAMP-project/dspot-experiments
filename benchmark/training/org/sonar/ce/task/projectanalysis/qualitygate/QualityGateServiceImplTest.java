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
package org.sonar.ce.task.projectanalysis.qualitygate;


import CoreMetrics.BUGS_KEY;
import CoreMetrics.CODE_SMELLS_KEY;
import CoreMetrics.OPEN_ISSUES_KEY;
import CoreMetrics.REOPENED_ISSUES_KEY;
import CoreMetrics.VULNERABILITIES_KEY;
import ShortLivingBranchQualityGate.ID;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.Optional;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.metric.Metric;
import org.sonar.ce.task.projectanalysis.metric.MetricImpl;
import org.sonar.ce.task.projectanalysis.metric.MetricRepository;
import org.sonar.db.DbClient;
import org.sonar.db.qualitygate.QualityGateConditionDao;
import org.sonar.db.qualitygate.QualityGateConditionDto;
import org.sonar.db.qualitygate.QualityGateDao;
import org.sonar.db.qualitygate.QualityGateDto;


public class QualityGateServiceImplTest {
    private static final long SOME_ID = 123;

    private static final String SOME_NAME = "some name";

    private static final QualityGateDto QUALITY_GATE_DTO = new QualityGateDto().setId(QualityGateServiceImplTest.SOME_ID).setName(QualityGateServiceImplTest.SOME_NAME);

    private static final long METRIC_ID_1 = 951;

    private static final long METRIC_ID_2 = 753;

    private static final Metric METRIC_1 = Mockito.mock(Metric.class);

    private static final Metric METRIC_2 = Mockito.mock(Metric.class);

    private static final QualityGateConditionDto CONDITION_1 = new QualityGateConditionDto().setId(321).setMetricId(QualityGateServiceImplTest.METRIC_ID_1).setOperator("LT").setErrorThreshold("error_th");

    private static final QualityGateConditionDto CONDITION_2 = new QualityGateConditionDto().setId(456).setMetricId(QualityGateServiceImplTest.METRIC_ID_2).setOperator("GT").setErrorThreshold("error_th");

    private QualityGateDao qualityGateDao = Mockito.mock(QualityGateDao.class);

    private QualityGateConditionDao qualityGateConditionDao = Mockito.mock(QualityGateConditionDao.class);

    private MetricRepository metricRepository = Mockito.mock(MetricRepository.class);

    private DbClient dbClient = Mockito.mock(DbClient.class);

    private QualityGateServiceImpl underTest = new QualityGateServiceImpl(dbClient, metricRepository);

    @Test
    public void findById_returns_absent_when_QualityGateDto_does_not_exist() {
        assertThat(underTest.findById(QualityGateServiceImplTest.SOME_ID)).isNotPresent();
    }

    @Test
    public void findById_returns_QualityGate_with_empty_set_of_conditions_when_there_is_none_in_DB() {
        Mockito.when(qualityGateDao.selectById(ArgumentMatchers.any(), ArgumentMatchers.eq(QualityGateServiceImplTest.SOME_ID))).thenReturn(QualityGateServiceImplTest.QUALITY_GATE_DTO);
        Mockito.when(qualityGateConditionDao.selectForQualityGate(ArgumentMatchers.any(), ArgumentMatchers.eq(QualityGateServiceImplTest.SOME_ID))).thenReturn(Collections.emptyList());
        Optional<QualityGate> res = underTest.findById(QualityGateServiceImplTest.SOME_ID);
        assertThat(res).isPresent();
        assertThat(res.get().getId()).isEqualTo(QualityGateServiceImplTest.SOME_ID);
        assertThat(res.get().getName()).isEqualTo(QualityGateServiceImplTest.SOME_NAME);
        assertThat(res.get().getConditions()).isEmpty();
    }

    @Test
    public void findById_returns_conditions_when_there_is_some_in_DB() {
        Mockito.when(qualityGateDao.selectById(ArgumentMatchers.any(), ArgumentMatchers.eq(QualityGateServiceImplTest.SOME_ID))).thenReturn(QualityGateServiceImplTest.QUALITY_GATE_DTO);
        Mockito.when(qualityGateConditionDao.selectForQualityGate(ArgumentMatchers.any(), ArgumentMatchers.eq(QualityGateServiceImplTest.SOME_ID))).thenReturn(ImmutableList.of(QualityGateServiceImplTest.CONDITION_1, QualityGateServiceImplTest.CONDITION_2));
        // metrics are always supposed to be there
        Mockito.when(metricRepository.getOptionalById(QualityGateServiceImplTest.METRIC_ID_1)).thenReturn(Optional.of(QualityGateServiceImplTest.METRIC_1));
        Mockito.when(metricRepository.getOptionalById(QualityGateServiceImplTest.METRIC_ID_2)).thenReturn(Optional.of(QualityGateServiceImplTest.METRIC_2));
        Optional<QualityGate> res = underTest.findById(QualityGateServiceImplTest.SOME_ID);
        assertThat(res).isPresent();
        assertThat(res.get().getId()).isEqualTo(QualityGateServiceImplTest.SOME_ID);
        assertThat(res.get().getName()).isEqualTo(QualityGateServiceImplTest.SOME_NAME);
        assertThat(res.get().getConditions()).containsOnly(new Condition(QualityGateServiceImplTest.METRIC_1, QualityGateServiceImplTest.CONDITION_1.getOperator(), QualityGateServiceImplTest.CONDITION_1.getErrorThreshold()), new Condition(QualityGateServiceImplTest.METRIC_2, QualityGateServiceImplTest.CONDITION_2.getOperator(), QualityGateServiceImplTest.CONDITION_2.getErrorThreshold()));
    }

    @Test
    public void findById_ignores_conditions_on_missing_metrics() {
        Mockito.when(qualityGateDao.selectById(ArgumentMatchers.any(), ArgumentMatchers.eq(QualityGateServiceImplTest.SOME_ID))).thenReturn(QualityGateServiceImplTest.QUALITY_GATE_DTO);
        Mockito.when(qualityGateConditionDao.selectForQualityGate(ArgumentMatchers.any(), ArgumentMatchers.eq(QualityGateServiceImplTest.SOME_ID))).thenReturn(ImmutableList.of(QualityGateServiceImplTest.CONDITION_1, QualityGateServiceImplTest.CONDITION_2));
        // metrics are always supposed to be there
        Mockito.when(metricRepository.getOptionalById(QualityGateServiceImplTest.METRIC_ID_1)).thenReturn(Optional.empty());
        Mockito.when(metricRepository.getOptionalById(QualityGateServiceImplTest.METRIC_ID_2)).thenReturn(Optional.of(QualityGateServiceImplTest.METRIC_2));
        Optional<QualityGate> res = underTest.findById(QualityGateServiceImplTest.SOME_ID);
        assertThat(res).isPresent();
        assertThat(res.get().getId()).isEqualTo(QualityGateServiceImplTest.SOME_ID);
        assertThat(res.get().getName()).isEqualTo(QualityGateServiceImplTest.SOME_NAME);
        assertThat(res.get().getConditions()).containsOnly(new Condition(QualityGateServiceImplTest.METRIC_2, QualityGateServiceImplTest.CONDITION_2.getOperator(), QualityGateServiceImplTest.CONDITION_2.getErrorThreshold()));
    }

    @Test
    public void findById_of_hardcoded_short_living_branch_returns_hardcoded_qg() {
        MetricImpl bugsMetric = mockMetricInRepository(BUGS_KEY);
        MetricImpl vulnerabilitiesMetric = mockMetricInRepository(VULNERABILITIES_KEY);
        MetricImpl codeSmellsMetric = mockMetricInRepository(CODE_SMELLS_KEY);
        MetricImpl openedIssueMetric = mockMetricInRepository(OPEN_ISSUES_KEY);
        MetricImpl reOpenedIssueMetric = mockMetricInRepository(REOPENED_ISSUES_KEY);
        Optional<QualityGate> res = underTest.findById(ID);
        assertThat(res).isPresent();
        QualityGate qualityGate = res.get();
        assertThat(qualityGate.getId()).isEqualTo(ID);
        assertThat(qualityGate.getName()).isEqualTo("Hardcoded short living branch quality gate");
        assertThat(qualityGate.getConditions()).extracting(Condition::getMetric, Condition::getOperator, Condition::getErrorThreshold).containsOnly(tuple(openedIssueMetric, Operator.GREATER_THAN, "0"), tuple(reOpenedIssueMetric, Operator.GREATER_THAN, "0"));
    }
}

