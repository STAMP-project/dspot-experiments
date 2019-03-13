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
package org.sonar.ce.task.projectanalysis.issue.commonrule;


import Component.Type.FILE;
import CoreMetrics.BRANCH_COVERAGE;
import CoreMetrics.CONDITIONS_TO_COVER;
import CoreMetrics.LINES_TO_COVER;
import CoreMetrics.LINE_COVERAGE;
import CoreMetrics.UNCOVERED_CONDITIONS;
import CoreMetrics.UNCOVERED_LINES;
import Severity.CRITICAL;
import com.google.common.collect.ImmutableMap;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.ce.task.projectanalysis.component.FileAttributes;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolderRule;
import org.sonar.ce.task.projectanalysis.measure.Measure;
import org.sonar.ce.task.projectanalysis.measure.MeasureRepositoryRule;
import org.sonar.ce.task.projectanalysis.metric.MetricRepositoryRule;
import org.sonar.ce.task.projectanalysis.qualityprofile.ActiveRulesHolderRule;
import org.sonar.core.issue.DefaultIssue;


public abstract class CoverageRuleTest {
    private static final String PLUGIN_KEY = "java";

    private static final String QP_KEY = "qp1";

    static ReportComponent FILE = ReportComponent.builder(Component.Type.FILE, 1).setFileAttributes(new FileAttributes(false, "java", 1)).build();

    @Rule
    public ActiveRulesHolderRule activeRuleHolder = new ActiveRulesHolderRule();

    @Rule
    public MetricRepositoryRule metricRepository = new MetricRepositoryRule().add(LINE_COVERAGE).add(LINES_TO_COVER).add(UNCOVERED_LINES).add(BRANCH_COVERAGE).add(CONDITIONS_TO_COVER).add(UNCOVERED_CONDITIONS);

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule();

    @Rule
    public MeasureRepositoryRule measureRepository = MeasureRepositoryRule.create(treeRootHolder, metricRepository);

    CommonRule underTest = createRule();

    @Test
    public void no_issue_if_enough_coverage() {
        activeRuleHolder.put(new org.sonar.ce.task.projectanalysis.qualityprofile.ActiveRule(getRuleKey(), Severity.CRITICAL, ImmutableMap.of(getMinPropertyKey(), "65"), 1000L, CoverageRuleTest.PLUGIN_KEY, CoverageRuleTest.QP_KEY));
        measureRepository.addRawMeasure(CoverageRuleTest.FILE.getReportAttributes().getRef(), getCoverageMetricKey(), Measure.newMeasureBuilder().create(90.0, 1));
        DefaultIssue issue = underTest.processFile(CoverageRuleTest.FILE, "java");
        assertThat(issue).isNull();
    }

    @Test
    public void issue_if_coverage_is_too_low() {
        activeRuleHolder.put(new org.sonar.ce.task.projectanalysis.qualityprofile.ActiveRule(getRuleKey(), Severity.CRITICAL, ImmutableMap.of(getMinPropertyKey(), "65"), 1000L, CoverageRuleTest.PLUGIN_KEY, CoverageRuleTest.QP_KEY));
        measureRepository.addRawMeasure(CoverageRuleTest.FILE.getReportAttributes().getRef(), getCoverageMetricKey(), Measure.newMeasureBuilder().create(20.0, 1));
        measureRepository.addRawMeasure(CoverageRuleTest.FILE.getReportAttributes().getRef(), getUncoveredMetricKey(), Measure.newMeasureBuilder().create(40));
        measureRepository.addRawMeasure(CoverageRuleTest.FILE.getReportAttributes().getRef(), getToCoverMetricKey(), Measure.newMeasureBuilder().create(50));
        DefaultIssue issue = underTest.processFile(CoverageRuleTest.FILE, "java");
        assertThat(issue.ruleKey()).isEqualTo(getRuleKey());
        assertThat(issue.severity()).isEqualTo(CRITICAL);
        // FIXME explain
        assertThat(issue.gap()).isEqualTo(23.0);
        assertThat(issue.message()).isEqualTo(getExpectedIssueMessage());
    }

    @Test
    public void no_issue_if_coverage_is_not_set() {
        activeRuleHolder.put(new org.sonar.ce.task.projectanalysis.qualityprofile.ActiveRule(getRuleKey(), Severity.CRITICAL, ImmutableMap.of(getMinPropertyKey(), "65"), 1000L, CoverageRuleTest.PLUGIN_KEY, CoverageRuleTest.QP_KEY));
        DefaultIssue issue = underTest.processFile(CoverageRuleTest.FILE, "java");
        assertThat(issue).isNull();
    }

    @Test
    public void ignored_if_rule_is_deactivated() {
        // coverage is too low, but rule is not activated
        measureRepository.addRawMeasure(CoverageRuleTest.FILE.getReportAttributes().getRef(), getCoverageMetricKey(), Measure.newMeasureBuilder().create(20.0, 1));
        DefaultIssue issue = underTest.processFile(CoverageRuleTest.FILE, "java");
        assertThat(issue).isNull();
    }
}

