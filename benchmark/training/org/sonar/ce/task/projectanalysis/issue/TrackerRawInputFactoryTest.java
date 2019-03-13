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
package org.sonar.ce.task.projectanalysis.issue;


import Component.Type.FILE;
import Component.Type.PROJECT;
import Constants.Severity.BLOCKER;
import DbIssues.Locations;
import ScannerReport.ExternalIssue;
import ScannerReport.Flow;
import ScannerReport.Issue;
import ScannerReport.IssueLocation;
import ScannerReport.IssueType.BUG;
import ScannerReport.IssueType.SECURITY_HOTSPOT;
import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.Duration;
import org.sonar.ce.task.projectanalysis.batch.BatchReportReaderRule;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolderRule;
import org.sonar.ce.task.projectanalysis.issue.commonrule.CommonRuleEngine;
import org.sonar.ce.task.projectanalysis.issue.filter.IssueFilter;
import org.sonar.ce.task.projectanalysis.qualityprofile.ActiveRulesHolderRule;
import org.sonar.ce.task.projectanalysis.source.SourceLinesHashRepository;
import org.sonar.core.issue.DefaultIssue;
import org.sonar.core.issue.tracking.Input;
import org.sonar.db.protobuf.DbIssues;
import org.sonar.scanner.protocol.output.ScannerReport;
import org.sonar.scanner.protocol.output.ScannerReport.TextRange;
import org.sonar.server.rule.CommonRuleKeys;


public class TrackerRawInputFactoryTest {
    private static final String FILE_UUID = "fake_uuid";

    private static final String ANOTHER_FILE_UUID = "another_fake_uuid";

    private static int FILE_REF = 2;

    private static int NOT_IN_REPORT_FILE_REF = 3;

    private static int ANOTHER_FILE_REF = 4;

    private static ReportComponent FILE = ReportComponent.builder(Component.Type.FILE, TrackerRawInputFactoryTest.FILE_REF).setUuid(TrackerRawInputFactoryTest.FILE_UUID).build();

    private static ReportComponent ANOTHER_FILE = ReportComponent.builder(Component.Type.FILE, TrackerRawInputFactoryTest.ANOTHER_FILE_REF).setUuid(TrackerRawInputFactoryTest.ANOTHER_FILE_UUID).build();

    private static ReportComponent PROJECT = ReportComponent.builder(Component.Type.PROJECT, 1).addChildren(TrackerRawInputFactoryTest.FILE, TrackerRawInputFactoryTest.ANOTHER_FILE).build();

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule().setRoot(TrackerRawInputFactoryTest.PROJECT);

    @Rule
    public BatchReportReaderRule reportReader = new BatchReportReaderRule();

    @Rule
    public ActiveRulesHolderRule activeRulesHolder = new ActiveRulesHolderRule();

    @Rule
    public RuleRepositoryRule ruleRepository = new RuleRepositoryRule();

    private SourceLinesHashRepository sourceLinesHash = Mockito.mock(SourceLinesHashRepository.class);

    private CommonRuleEngine commonRuleEngine = Mockito.mock(CommonRuleEngine.class);

    private IssueFilter issueFilter = Mockito.mock(IssueFilter.class);

    private IssueRelocationToRoot issueRelocationToRoot = Mockito.mock(IssueRelocationToRoot.class);

    private TrackerRawInputFactory underTest = new TrackerRawInputFactory(treeRootHolder, reportReader, sourceLinesHash, commonRuleEngine, issueFilter, ruleRepository, activeRulesHolder, issueRelocationToRoot);

    @Test
    public void load_source_hash_sequences() {
        Mockito.when(sourceLinesHash.getLineHashesMatchingDBVersion(TrackerRawInputFactoryTest.FILE)).thenReturn(Collections.singletonList("line"));
        Input<DefaultIssue> input = underTest.create(TrackerRawInputFactoryTest.FILE);
        assertThat(input.getLineHashSequence()).isNotNull();
        assertThat(input.getLineHashSequence().getHashForLine(1)).isEqualTo("line");
        assertThat(input.getLineHashSequence().getHashForLine(2)).isEmpty();
        assertThat(input.getLineHashSequence().getHashForLine(3)).isEmpty();
        assertThat(input.getBlockHashSequence()).isNotNull();
    }

    @Test
    public void load_source_hash_sequences_only_on_files() {
        Input<DefaultIssue> input = underTest.create(TrackerRawInputFactoryTest.PROJECT);
        assertThat(input.getLineHashSequence()).isNotNull();
        assertThat(input.getBlockHashSequence()).isNotNull();
    }

    @Test
    public void load_issues_from_report() {
        RuleKey ruleKey = RuleKey.of("java", "S001");
        markRuleAsActive(ruleKey);
        Mockito.when(issueFilter.accept(ArgumentMatchers.any(), ArgumentMatchers.eq(TrackerRawInputFactoryTest.FILE))).thenReturn(true);
        Mockito.when(sourceLinesHash.getLineHashesMatchingDBVersion(TrackerRawInputFactoryTest.FILE)).thenReturn(Collections.singletonList("line"));
        ScannerReport.Issue reportIssue = Issue.newBuilder().setTextRange(TextRange.newBuilder().setStartLine(2).build()).setMsg("the message").setRuleRepository(ruleKey.repository()).setRuleKey(ruleKey.rule()).setSeverity(BLOCKER).setGap(3.14).build();
        reportReader.putIssues(TrackerRawInputFactoryTest.FILE.getReportAttributes().getRef(), Collections.singletonList(reportIssue));
        Input<DefaultIssue> input = underTest.create(TrackerRawInputFactoryTest.FILE);
        Collection<DefaultIssue> issues = input.getIssues();
        assertThat(issues).hasSize(1);
        DefaultIssue issue = Iterators.getOnlyElement(issues.iterator());
        // fields set by analysis report
        assertThat(issue.ruleKey()).isEqualTo(ruleKey);
        assertThat(issue.severity()).isEqualTo(Severity.BLOCKER);
        assertThat(issue.line()).isEqualTo(2);
        assertThat(issue.gap()).isEqualTo(3.14);
        assertThat(issue.message()).isEqualTo("the message");
        // fields set by compute engine
        assertThat(issue.checksum()).isEqualTo(input.getLineHashSequence().getHashForLine(2));
        assertThat(issue.tags()).isEmpty();
        assertInitializedIssue(issue);
        assertThat(issue.effort()).isNull();
    }

    @Test
    public void set_rule_name_as_message_when_issue_message_from_report_is_empty() {
        RuleKey ruleKey = RuleKey.of("java", "S001");
        markRuleAsActive(ruleKey);
        registerRule(ruleKey, "Rule 1");
        Mockito.when(issueFilter.accept(ArgumentMatchers.any(), ArgumentMatchers.eq(TrackerRawInputFactoryTest.FILE))).thenReturn(true);
        Mockito.when(sourceLinesHash.getLineHashesMatchingDBVersion(TrackerRawInputFactoryTest.FILE)).thenReturn(Collections.singletonList("line"));
        ScannerReport.Issue reportIssue = Issue.newBuilder().setRuleRepository(ruleKey.repository()).setRuleKey(ruleKey.rule()).setMsg("").build();
        reportReader.putIssues(TrackerRawInputFactoryTest.FILE.getReportAttributes().getRef(), Collections.singletonList(reportIssue));
        Input<DefaultIssue> input = underTest.create(TrackerRawInputFactoryTest.FILE);
        Collection<DefaultIssue> issues = input.getIssues();
        assertThat(issues).hasSize(1);
        DefaultIssue issue = Iterators.getOnlyElement(issues.iterator());
        // fields set by analysis report
        assertThat(issue.ruleKey()).isEqualTo(ruleKey);
        // fields set by compute engine
        assertInitializedIssue(issue);
        assertThat(issue.message()).isEqualTo("Rule 1");
    }

    // SONAR-10781
    @Test
    public void load_issues_from_report_missing_secondary_location_component() {
        RuleKey ruleKey = RuleKey.of("java", "S001");
        markRuleAsActive(ruleKey);
        Mockito.when(issueFilter.accept(ArgumentMatchers.any(), ArgumentMatchers.eq(TrackerRawInputFactoryTest.FILE))).thenReturn(true);
        Mockito.when(sourceLinesHash.getLineHashesMatchingDBVersion(TrackerRawInputFactoryTest.FILE)).thenReturn(Collections.singletonList("line"));
        ScannerReport.Issue reportIssue = Issue.newBuilder().setTextRange(TextRange.newBuilder().setStartLine(2).build()).setMsg("the message").setRuleRepository(ruleKey.repository()).setRuleKey(ruleKey.rule()).setSeverity(BLOCKER).setGap(3.14).addFlow(Flow.newBuilder().addLocation(IssueLocation.newBuilder().setComponentRef(TrackerRawInputFactoryTest.FILE_REF).setMsg("Secondary location in same file").setTextRange(TextRange.newBuilder().setStartLine(2).build())).addLocation(IssueLocation.newBuilder().setComponentRef(TrackerRawInputFactoryTest.NOT_IN_REPORT_FILE_REF).setMsg("Secondary location in a missing file").setTextRange(TextRange.newBuilder().setStartLine(3).build())).addLocation(IssueLocation.newBuilder().setComponentRef(TrackerRawInputFactoryTest.ANOTHER_FILE_REF).setMsg("Secondary location in another file").setTextRange(TextRange.newBuilder().setStartLine(3).build())).build()).build();
        reportReader.putIssues(TrackerRawInputFactoryTest.FILE.getReportAttributes().getRef(), Collections.singletonList(reportIssue));
        Input<DefaultIssue> input = underTest.create(TrackerRawInputFactoryTest.FILE);
        Collection<DefaultIssue> issues = input.getIssues();
        assertThat(issues).hasSize(1);
        DefaultIssue issue = Iterators.getOnlyElement(issues.iterator());
        DbIssues.Locations locations = issue.getLocations();
        // fields set by analysis report
        assertThat(locations.getFlowList()).hasSize(1);
        assertThat(locations.getFlow(0).getLocationList()).hasSize(2);
        // Not component id if location is in the same file
        assertThat(locations.getFlow(0).getLocation(0).getComponentId()).isEmpty();
        assertThat(locations.getFlow(0).getLocation(1).getComponentId()).isEqualTo(TrackerRawInputFactoryTest.ANOTHER_FILE_UUID);
    }

    @Test
    public void load_external_issues_from_report() {
        Mockito.when(sourceLinesHash.getLineHashesMatchingDBVersion(TrackerRawInputFactoryTest.FILE)).thenReturn(Collections.singletonList("line"));
        ScannerReport.ExternalIssue reportIssue = ExternalIssue.newBuilder().setTextRange(TextRange.newBuilder().setStartLine(2).build()).setMsg("the message").setEngineId("eslint").setRuleId("S001").setSeverity(BLOCKER).setEffort(20L).setType(SECURITY_HOTSPOT).build();
        reportReader.putExternalIssues(TrackerRawInputFactoryTest.FILE.getReportAttributes().getRef(), Arrays.asList(reportIssue));
        Input<DefaultIssue> input = underTest.create(TrackerRawInputFactoryTest.FILE);
        Collection<DefaultIssue> issues = input.getIssues();
        assertThat(issues).hasSize(1);
        DefaultIssue issue = Iterators.getOnlyElement(issues.iterator());
        // fields set by analysis report
        assertThat(issue.ruleKey()).isEqualTo(RuleKey.of("external_eslint", "S001"));
        assertThat(issue.severity()).isEqualTo(Severity.BLOCKER);
        assertThat(issue.line()).isEqualTo(2);
        assertThat(issue.effort()).isEqualTo(Duration.create(20L));
        assertThat(issue.message()).isEqualTo("the message");
        assertThat(issue.type()).isEqualTo(RuleType.SECURITY_HOTSPOT);
        // fields set by compute engine
        assertThat(issue.checksum()).isEqualTo(input.getLineHashSequence().getHashForLine(2));
        assertThat(issue.tags()).isEmpty();
        assertInitializedExternalIssue(issue);
    }

    @Test
    public void load_external_issues_from_report_with_default_effort() {
        Mockito.when(sourceLinesHash.getLineHashesMatchingDBVersion(TrackerRawInputFactoryTest.FILE)).thenReturn(Collections.singletonList("line"));
        ScannerReport.ExternalIssue reportIssue = ExternalIssue.newBuilder().setTextRange(TextRange.newBuilder().setStartLine(2).build()).setMsg("the message").setEngineId("eslint").setRuleId("S001").setSeverity(BLOCKER).setType(BUG).build();
        reportReader.putExternalIssues(TrackerRawInputFactoryTest.FILE.getReportAttributes().getRef(), Arrays.asList(reportIssue));
        Input<DefaultIssue> input = underTest.create(TrackerRawInputFactoryTest.FILE);
        Collection<DefaultIssue> issues = input.getIssues();
        assertThat(issues).hasSize(1);
        DefaultIssue issue = Iterators.getOnlyElement(issues.iterator());
        // fields set by analysis report
        assertThat(issue.ruleKey()).isEqualTo(RuleKey.of("external_eslint", "S001"));
        assertThat(issue.severity()).isEqualTo(Severity.BLOCKER);
        assertThat(issue.line()).isEqualTo(2);
        assertThat(issue.effort()).isEqualTo(Duration.create(0L));
        assertThat(issue.message()).isEqualTo("the message");
        // fields set by compute engine
        assertThat(issue.checksum()).isEqualTo(input.getLineHashSequence().getHashForLine(2));
        assertThat(issue.tags()).isEmpty();
        assertInitializedExternalIssue(issue);
    }

    @Test
    public void excludes_issues_on_inactive_rules() {
        RuleKey ruleKey = RuleKey.of("java", "S001");
        Mockito.when(issueFilter.accept(ArgumentMatchers.any(), ArgumentMatchers.eq(TrackerRawInputFactoryTest.FILE))).thenReturn(true);
        Mockito.when(sourceLinesHash.getLineHashesMatchingDBVersion(TrackerRawInputFactoryTest.FILE)).thenReturn(Collections.singletonList("line"));
        ScannerReport.Issue reportIssue = Issue.newBuilder().setTextRange(TextRange.newBuilder().setStartLine(2).build()).setMsg("the message").setRuleRepository(ruleKey.repository()).setRuleKey(ruleKey.rule()).setSeverity(BLOCKER).setGap(3.14).build();
        reportReader.putIssues(TrackerRawInputFactoryTest.FILE.getReportAttributes().getRef(), Collections.singletonList(reportIssue));
        Input<DefaultIssue> input = underTest.create(TrackerRawInputFactoryTest.FILE);
        Collection<DefaultIssue> issues = input.getIssues();
        assertThat(issues).isEmpty();
    }

    @Test
    public void filter_excludes_issues_from_report() {
        RuleKey ruleKey = RuleKey.of("java", "S001");
        markRuleAsActive(ruleKey);
        Mockito.when(issueFilter.accept(ArgumentMatchers.any(), ArgumentMatchers.eq(TrackerRawInputFactoryTest.FILE))).thenReturn(false);
        Mockito.when(sourceLinesHash.getLineHashesMatchingDBVersion(TrackerRawInputFactoryTest.FILE)).thenReturn(Collections.singletonList("line"));
        ScannerReport.Issue reportIssue = Issue.newBuilder().setTextRange(TextRange.newBuilder().setStartLine(2).build()).setMsg("the message").setRuleRepository(ruleKey.repository()).setRuleKey(ruleKey.rule()).setSeverity(BLOCKER).setGap(3.14).build();
        reportReader.putIssues(TrackerRawInputFactoryTest.FILE.getReportAttributes().getRef(), Collections.singletonList(reportIssue));
        Input<DefaultIssue> input = underTest.create(TrackerRawInputFactoryTest.FILE);
        Collection<DefaultIssue> issues = input.getIssues();
        assertThat(issues).isEmpty();
    }

    @Test
    public void exclude_issues_on_common_rules() {
        RuleKey ruleKey = RuleKey.of(CommonRuleKeys.commonRepositoryForLang("java"), "S001");
        markRuleAsActive(ruleKey);
        Mockito.when(sourceLinesHash.getLineHashesMatchingDBVersion(TrackerRawInputFactoryTest.FILE)).thenReturn(Collections.singletonList("line"));
        ScannerReport.Issue reportIssue = Issue.newBuilder().setMsg("the message").setRuleRepository(ruleKey.repository()).setRuleKey(ruleKey.rule()).setSeverity(BLOCKER).build();
        reportReader.putIssues(TrackerRawInputFactoryTest.FILE.getReportAttributes().getRef(), Collections.singletonList(reportIssue));
        Input<DefaultIssue> input = underTest.create(TrackerRawInputFactoryTest.FILE);
        assertThat(input.getIssues()).isEmpty();
    }

    @Test
    public void load_issues_of_compute_engine_common_rules() {
        RuleKey ruleKey = RuleKey.of(CommonRuleKeys.commonRepositoryForLang("java"), "InsufficientCoverage");
        markRuleAsActive(ruleKey);
        Mockito.when(issueFilter.accept(ArgumentMatchers.any(), ArgumentMatchers.eq(TrackerRawInputFactoryTest.FILE))).thenReturn(true);
        Mockito.when(sourceLinesHash.getLineHashesMatchingDBVersion(TrackerRawInputFactoryTest.FILE)).thenReturn(Collections.singletonList("line"));
        DefaultIssue ceIssue = new DefaultIssue().setRuleKey(ruleKey).setMessage("not enough coverage").setGap(10.0);
        Mockito.when(commonRuleEngine.process(TrackerRawInputFactoryTest.FILE)).thenReturn(Collections.singletonList(ceIssue));
        Input<DefaultIssue> input = underTest.create(TrackerRawInputFactoryTest.FILE);
        assertThat(input.getIssues()).containsOnly(ceIssue);
        assertInitializedIssue(input.getIssues().iterator().next());
    }

    @Test
    public void filter_exclude_issues_on_common_rule() {
        RuleKey ruleKey = RuleKey.of(CommonRuleKeys.commonRepositoryForLang("java"), "InsufficientCoverage");
        markRuleAsActive(ruleKey);
        Mockito.when(issueFilter.accept(ArgumentMatchers.any(), ArgumentMatchers.eq(TrackerRawInputFactoryTest.FILE))).thenReturn(false);
        Mockito.when(sourceLinesHash.getLineHashesMatchingDBVersion(TrackerRawInputFactoryTest.FILE)).thenReturn(Collections.singletonList("line"));
        DefaultIssue ceIssue = new DefaultIssue().setRuleKey(ruleKey).setMessage("not enough coverage").setGap(10.0);
        Mockito.when(commonRuleEngine.process(TrackerRawInputFactoryTest.FILE)).thenReturn(Collections.singletonList(ceIssue));
        Input<DefaultIssue> input = underTest.create(TrackerRawInputFactoryTest.FILE);
        assertThat(input.getIssues()).isEmpty();
    }
}

