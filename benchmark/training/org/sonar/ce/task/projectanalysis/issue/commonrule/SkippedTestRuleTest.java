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


import CommonRuleKeys.SKIPPED_UNIT_TESTS;
import Component.Type.FILE;
import CoreMetrics.SKIPPED_TESTS;
import CoreMetrics.SKIPPED_TESTS_KEY;
import Severity.CRITICAL;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rule.Severity;
import org.sonar.ce.task.projectanalysis.component.FileAttributes;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolderRule;
import org.sonar.ce.task.projectanalysis.measure.Measure;
import org.sonar.ce.task.projectanalysis.measure.MeasureRepositoryRule;
import org.sonar.ce.task.projectanalysis.metric.MetricRepositoryRule;
import org.sonar.ce.task.projectanalysis.qualityprofile.ActiveRulesHolderRule;
import org.sonar.core.issue.DefaultIssue;
import org.sonar.server.rule.CommonRuleKeys;


public class SkippedTestRuleTest {
    private static final String PLUGIN_KEY = "java";

    private static final String QP_KEY = "qp1";

    static RuleKey RULE_KEY = RuleKey.of(CommonRuleKeys.commonRepositoryForLang("java"), SKIPPED_UNIT_TESTS);

    static ReportComponent FILE = ReportComponent.builder(Component.Type.FILE, 1).setFileAttributes(new FileAttributes(true, "java", 1)).setName("FooTest.java").build();

    @Rule
    public ActiveRulesHolderRule activeRuleHolder = new ActiveRulesHolderRule();

    @Rule
    public MetricRepositoryRule metricRepository = new MetricRepositoryRule().add(SKIPPED_TESTS);

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule().setRoot(ReportComponent.DUMB_PROJECT);

    @Rule
    public MeasureRepositoryRule measureRepository = MeasureRepositoryRule.create(treeRootHolder, metricRepository);

    CommonRule underTest = new SkippedTestRule(activeRuleHolder, measureRepository, metricRepository);

    @Test
    public void issue_if_skipped_tests() {
        activeRuleHolder.put(new org.sonar.ce.task.projectanalysis.qualityprofile.ActiveRule(SkippedTestRuleTest.RULE_KEY, Severity.CRITICAL, Collections.emptyMap(), 1000L, SkippedTestRuleTest.PLUGIN_KEY, SkippedTestRuleTest.QP_KEY));
        measureRepository.addRawMeasure(SkippedTestRuleTest.FILE.getReportAttributes().getRef(), SKIPPED_TESTS_KEY, Measure.newMeasureBuilder().create(2));
        DefaultIssue issue = underTest.processFile(SkippedTestRuleTest.FILE, "java");
        assertThat(issue.ruleKey()).isEqualTo(SkippedTestRuleTest.RULE_KEY);
        assertThat(issue.severity()).isEqualTo(CRITICAL);
        assertThat(issue.gap()).isEqualTo(2.0);
        assertThat(issue.message()).isEqualTo("Fix or remove skipped unit tests in file \"FooTest.java\".");
    }

    @Test
    public void no_issues_if_zero_skipped_tests() {
        activeRuleHolder.put(new org.sonar.ce.task.projectanalysis.qualityprofile.ActiveRule(SkippedTestRuleTest.RULE_KEY, Severity.CRITICAL, Collections.emptyMap(), 1000L, SkippedTestRuleTest.PLUGIN_KEY, SkippedTestRuleTest.QP_KEY));
        measureRepository.addRawMeasure(SkippedTestRuleTest.FILE.getReportAttributes().getRef(), SKIPPED_TESTS_KEY, Measure.newMeasureBuilder().create(0));
        DefaultIssue issue = underTest.processFile(SkippedTestRuleTest.FILE, "java");
        assertThat(issue).isNull();
    }

    @Test
    public void no_issues_if_measure_is_absent() {
        activeRuleHolder.put(new org.sonar.ce.task.projectanalysis.qualityprofile.ActiveRule(SkippedTestRuleTest.RULE_KEY, Severity.CRITICAL, Collections.emptyMap(), 1000L, SkippedTestRuleTest.PLUGIN_KEY, SkippedTestRuleTest.QP_KEY));
        DefaultIssue issue = underTest.processFile(SkippedTestRuleTest.FILE, "java");
        assertThat(issue).isNull();
    }
}

