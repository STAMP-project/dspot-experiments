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


import CommonRuleKeys.INSUFFICIENT_COMMENT_DENSITY;
import CommonRuleKeys.INSUFFICIENT_COMMENT_DENSITY_PROPERTY;
import Component.Type.FILE;
import CoreMetrics.COMMENT_LINES;
import CoreMetrics.COMMENT_LINES_DENSITY;
import CoreMetrics.COMMENT_LINES_DENSITY_KEY;
import CoreMetrics.NCLOC;
import Severity.CRITICAL;
import com.google.common.collect.ImmutableMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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


public class CommentDensityRuleTest {
    private static final String PLUGIN_KEY = "java";

    private static final String QP_KEY = "qp1";

    static RuleKey RULE_KEY = RuleKey.of(CommonRuleKeys.commonRepositoryForLang(CommentDensityRuleTest.PLUGIN_KEY), INSUFFICIENT_COMMENT_DENSITY);

    static ReportComponent FILE = ReportComponent.builder(Component.Type.FILE, 1).setFileAttributes(new FileAttributes(false, CommentDensityRuleTest.PLUGIN_KEY, 1)).build();

    static ReportComponent TEST_FILE = ReportComponent.builder(Component.Type.FILE, 1).setFileAttributes(new FileAttributes(true, CommentDensityRuleTest.PLUGIN_KEY, 1)).build();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public ActiveRulesHolderRule activeRuleHolder = new ActiveRulesHolderRule();

    @Rule
    public MetricRepositoryRule metricRepository = new MetricRepositoryRule().add(COMMENT_LINES_DENSITY).add(COMMENT_LINES).add(NCLOC);

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule().setRoot(ReportComponent.DUMB_PROJECT);

    @Rule
    public MeasureRepositoryRule measureRepository = MeasureRepositoryRule.create(treeRootHolder, metricRepository);

    CommentDensityRule underTest = new CommentDensityRule(activeRuleHolder, measureRepository, metricRepository);

    @Test
    public void no_issues_if_enough_comments() {
        activeRuleHolder.put(new org.sonar.ce.task.projectanalysis.qualityprofile.ActiveRule(CommentDensityRuleTest.RULE_KEY, Severity.CRITICAL, ImmutableMap.of(INSUFFICIENT_COMMENT_DENSITY_PROPERTY, "25"), 1000L, CommentDensityRuleTest.PLUGIN_KEY, CommentDensityRuleTest.QP_KEY));
        measureRepository.addRawMeasure(CommentDensityRuleTest.FILE.getReportAttributes().getRef(), COMMENT_LINES_DENSITY_KEY, Measure.newMeasureBuilder().create(90.0, 1));
        DefaultIssue issue = underTest.processFile(CommentDensityRuleTest.FILE, CommentDensityRuleTest.PLUGIN_KEY);
        assertThat(issue).isNull();
    }

    @Test
    public void issue_if_not_enough_comments() {
        prepareForIssue("25", CommentDensityRuleTest.FILE, 10.0, 40, 360);
        DefaultIssue issue = underTest.processFile(CommentDensityRuleTest.FILE, CommentDensityRuleTest.PLUGIN_KEY);
        assertThat(issue.ruleKey()).isEqualTo(CommentDensityRuleTest.RULE_KEY);
        assertThat(issue.severity()).isEqualTo(CRITICAL);
        // min_comments = (min_percent * ncloc) / (1 - min_percent)
        // -> threshold of 25% for 360 ncloc is 120 comment lines. 40 are already written.
        assertThat(issue.gap()).isEqualTo((120.0 - 40.0));
        assertThat(issue.message()).isEqualTo("80 more comment lines need to be written to reach the minimum threshold of 25.0% comment density.");
    }

    @Test
    public void no_issues_on_tests() {
        prepareForIssue("25", CommentDensityRuleTest.TEST_FILE, 10.0, 40, 360);
        DefaultIssue issue = underTest.processFile(CommentDensityRuleTest.TEST_FILE, CommentDensityRuleTest.PLUGIN_KEY);
        assertThat(issue).isNull();
    }

    @Test
    public void issue_if_not_enough_comments__test_ceil() {
        prepareForIssue("25", CommentDensityRuleTest.FILE, 0.0, 0, 1);
        DefaultIssue issue = underTest.processFile(CommentDensityRuleTest.FILE, CommentDensityRuleTest.PLUGIN_KEY);
        assertThat(issue.ruleKey()).isEqualTo(CommentDensityRuleTest.RULE_KEY);
        assertThat(issue.severity()).isEqualTo(CRITICAL);
        // 1 ncloc requires 1 comment line to reach 25% of comment density
        assertThat(issue.gap()).isEqualTo(1.0);
        assertThat(issue.message()).isEqualTo("1 more comment lines need to be written to reach the minimum threshold of 25.0% comment density.");
    }

    /**
     * SQALE-110
     */
    @Test
    public void fail_if_min_density_is_100() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Minimum density of rule [common-java:InsufficientCommentDensity] is incorrect. Got [100] but must be strictly less than 100.");
        prepareForIssue("100", CommentDensityRuleTest.FILE, 0.0, 0, 1);
        underTest.processFile(CommentDensityRuleTest.FILE, CommentDensityRuleTest.PLUGIN_KEY);
    }
}

