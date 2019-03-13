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
package org.sonar.scanner.issue;


import RuleType.BUG;
import ScannerReport.ExternalIssue;
import ScannerReport.Issue;
import Severity.INFO;
import java.util.Collections;
import java.util.HashSet;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.sonar.api.batch.fs.InputComponent;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.DefaultInputProject;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.rule.Severity.CRITICAL;
import org.sonar.api.batch.rule.internal.ActiveRulesBuilder;
import org.sonar.api.batch.rule.internal.NewActiveRule;
import org.sonar.api.batch.rule.internal.RulesBuilder;
import org.sonar.api.batch.sensor.issue.internal.DefaultExternalIssue;
import org.sonar.api.batch.sensor.issue.internal.DefaultIssue;
import org.sonar.api.batch.sensor.issue.internal.DefaultIssueLocation;
import org.sonar.api.rule.RuleKey;
import org.sonar.scanner.protocol.output.ScannerReport;
import org.sonar.scanner.report.ReportPublisher;


@RunWith(MockitoJUnitRunner.class)
public class IssuePublisherTest {
    static final RuleKey SQUID_RULE_KEY = RuleKey.of("squid", "AvoidCycle");

    static final String SQUID_RULE_NAME = "Avoid Cycle";

    private static final RuleKey NOSONAR_RULE_KEY = RuleKey.of("squid", "NoSonarCheck");

    private DefaultInputProject project;

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Mock
    IssueFilters filters;

    ActiveRulesBuilder activeRulesBuilder = new ActiveRulesBuilder();

    RulesBuilder ruleBuilder = new RulesBuilder();

    IssuePublisher moduleIssues;

    DefaultInputFile file = new TestInputFileBuilder("foo", "src/Foo.php").initMetadata("Foo\nBar\nBiz\n").build();

    ReportPublisher reportPublisher = Mockito.mock(ReportPublisher.class, Mockito.RETURNS_DEEP_STUBS);

    @Test
    public void ignore_null_active_rule() {
        ruleBuilder.add(IssuePublisherTest.SQUID_RULE_KEY).setName(IssuePublisherTest.SQUID_RULE_NAME);
        initModuleIssues();
        DefaultIssue issue = new DefaultIssue(project).at(new DefaultIssueLocation().on(file).at(file.selectLine(3)).message("Foo")).forRule(IssuePublisherTest.SQUID_RULE_KEY);
        boolean added = moduleIssues.initAndAddIssue(issue);
        assertThat(added).isFalse();
        Mockito.verifyZeroInteractions(reportPublisher);
    }

    @Test
    public void ignore_null_rule_of_active_rule() {
        ruleBuilder.add(IssuePublisherTest.SQUID_RULE_KEY).setName(IssuePublisherTest.SQUID_RULE_NAME);
        activeRulesBuilder.addRule(new NewActiveRule.Builder().setRuleKey(IssuePublisherTest.SQUID_RULE_KEY).setQProfileKey("qp-1").build());
        initModuleIssues();
        DefaultIssue issue = new DefaultIssue(project).at(new DefaultIssueLocation().on(file).at(file.selectLine(3)).message("Foo")).forRule(IssuePublisherTest.SQUID_RULE_KEY);
        boolean added = moduleIssues.initAndAddIssue(issue);
        assertThat(added).isFalse();
        Mockito.verifyZeroInteractions(reportPublisher);
    }

    @Test
    public void add_issue_to_cache() {
        ruleBuilder.add(IssuePublisherTest.SQUID_RULE_KEY).setName(IssuePublisherTest.SQUID_RULE_NAME);
        activeRulesBuilder.addRule(new NewActiveRule.Builder().setRuleKey(IssuePublisherTest.SQUID_RULE_KEY).setSeverity(INFO).setQProfileKey("qp-1").build());
        initModuleIssues();
        DefaultIssue issue = new DefaultIssue(project).at(new DefaultIssueLocation().on(file).at(file.selectLine(3)).message("Foo")).forRule(IssuePublisherTest.SQUID_RULE_KEY).overrideSeverity(CRITICAL);
        Mockito.when(filters.accept(ArgumentMatchers.any(InputComponent.class), ArgumentMatchers.any(Issue.class))).thenReturn(true);
        boolean added = moduleIssues.initAndAddIssue(issue);
        assertThat(added).isTrue();
        ArgumentCaptor<ScannerReport.Issue> argument = ArgumentCaptor.forClass(Issue.class);
        Mockito.verify(reportPublisher.getWriter()).appendComponentIssue(ArgumentMatchers.eq(file.scannerId()), argument.capture());
        assertThat(argument.getValue().getSeverity()).isEqualTo(org.sonar.scanner.protocol.Constants.Severity.CRITICAL);
    }

    @Test
    public void add_external_issue_to_cache() {
        ruleBuilder.add(IssuePublisherTest.SQUID_RULE_KEY).setName(IssuePublisherTest.SQUID_RULE_NAME);
        initModuleIssues();
        DefaultExternalIssue issue = new DefaultExternalIssue(project).at(new DefaultIssueLocation().on(file).at(file.selectLine(3)).message("Foo")).type(BUG).forRule(IssuePublisherTest.SQUID_RULE_KEY).severity(CRITICAL);
        moduleIssues.initAndAddExternalIssue(issue);
        ArgumentCaptor<ScannerReport.ExternalIssue> argument = ArgumentCaptor.forClass(ExternalIssue.class);
        Mockito.verify(reportPublisher.getWriter()).appendComponentExternalIssue(ArgumentMatchers.eq(file.scannerId()), argument.capture());
        assertThat(argument.getValue().getSeverity()).isEqualTo(org.sonar.scanner.protocol.Constants.Severity.CRITICAL);
    }

    @Test
    public void use_severity_from_active_rule_if_no_severity_on_issue() {
        ruleBuilder.add(IssuePublisherTest.SQUID_RULE_KEY).setName(IssuePublisherTest.SQUID_RULE_NAME);
        activeRulesBuilder.addRule(new NewActiveRule.Builder().setRuleKey(IssuePublisherTest.SQUID_RULE_KEY).setSeverity(INFO).setQProfileKey("qp-1").build());
        initModuleIssues();
        DefaultIssue issue = new DefaultIssue(project).at(new DefaultIssueLocation().on(file).at(file.selectLine(3)).message("Foo")).forRule(IssuePublisherTest.SQUID_RULE_KEY);
        Mockito.when(filters.accept(ArgumentMatchers.any(InputComponent.class), ArgumentMatchers.any(Issue.class))).thenReturn(true);
        moduleIssues.initAndAddIssue(issue);
        ArgumentCaptor<ScannerReport.Issue> argument = ArgumentCaptor.forClass(Issue.class);
        Mockito.verify(reportPublisher.getWriter()).appendComponentIssue(ArgumentMatchers.eq(file.scannerId()), argument.capture());
        assertThat(argument.getValue().getSeverity()).isEqualTo(org.sonar.scanner.protocol.Constants.Severity.INFO);
    }

    @Test
    public void filter_issue() {
        ruleBuilder.add(IssuePublisherTest.SQUID_RULE_KEY).setName(IssuePublisherTest.SQUID_RULE_NAME);
        activeRulesBuilder.addRule(new NewActiveRule.Builder().setRuleKey(IssuePublisherTest.SQUID_RULE_KEY).setSeverity(INFO).setQProfileKey("qp-1").build());
        initModuleIssues();
        DefaultIssue issue = new DefaultIssue(project).at(new DefaultIssueLocation().on(file).at(file.selectLine(3)).message("")).forRule(IssuePublisherTest.SQUID_RULE_KEY);
        Mockito.when(filters.accept(ArgumentMatchers.any(InputComponent.class), ArgumentMatchers.any(Issue.class))).thenReturn(false);
        boolean added = moduleIssues.initAndAddIssue(issue);
        assertThat(added).isFalse();
        Mockito.verifyZeroInteractions(reportPublisher);
    }

    @Test
    public void should_ignore_lines_commented_with_nosonar() {
        ruleBuilder.add(IssuePublisherTest.SQUID_RULE_KEY).setName(IssuePublisherTest.SQUID_RULE_NAME);
        activeRulesBuilder.addRule(new NewActiveRule.Builder().setRuleKey(IssuePublisherTest.SQUID_RULE_KEY).setSeverity(INFO).setQProfileKey("qp-1").build());
        initModuleIssues();
        DefaultIssue issue = new DefaultIssue(project).at(new DefaultIssueLocation().on(file).at(file.selectLine(3)).message("")).forRule(IssuePublisherTest.SQUID_RULE_KEY);
        file.noSonarAt(new HashSet(Collections.singletonList(3)));
        boolean added = moduleIssues.initAndAddIssue(issue);
        assertThat(added).isFalse();
        Mockito.verifyZeroInteractions(reportPublisher);
    }

    @Test
    public void should_accept_issues_on_no_sonar_rules() {
        // The "No Sonar" rule logs violations on the lines that are flagged with "NOSONAR" !!
        ruleBuilder.add(IssuePublisherTest.NOSONAR_RULE_KEY).setName("No Sonar");
        activeRulesBuilder.addRule(new NewActiveRule.Builder().setRuleKey(IssuePublisherTest.NOSONAR_RULE_KEY).setSeverity(INFO).setQProfileKey("qp-1").build());
        initModuleIssues();
        file.noSonarAt(new HashSet(Collections.singletonList(3)));
        DefaultIssue issue = new DefaultIssue(project).at(new DefaultIssueLocation().on(file).at(file.selectLine(3)).message("")).forRule(IssuePublisherTest.NOSONAR_RULE_KEY);
        Mockito.when(filters.accept(ArgumentMatchers.any(InputComponent.class), ArgumentMatchers.any(Issue.class))).thenReturn(true);
        boolean added = moduleIssues.initAndAddIssue(issue);
        assertThat(added).isTrue();
        Mockito.verify(reportPublisher.getWriter()).appendComponentIssue(ArgumentMatchers.eq(file.scannerId()), ArgumentMatchers.any());
    }
}

