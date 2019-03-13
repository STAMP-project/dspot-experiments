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


import BranchType.LONG;
import Component.Type.FILE;
import Component.Type.PROJECT;
import Constants.Severity.BLOCKER;
import ScannerReport.Issue;
import Severity.MAJOR;
import System2.INSTANCE;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.System2;
import org.sonar.ce.task.projectanalysis.analysis.AnalysisMetadataHolder;
import org.sonar.ce.task.projectanalysis.analysis.Branch;
import org.sonar.ce.task.projectanalysis.batch.BatchReportReaderRule;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.component.MergeBranchComponentUuids;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolderRule;
import org.sonar.ce.task.projectanalysis.component.TypeAwareVisitor;
import org.sonar.ce.task.projectanalysis.filemove.MovedFilesRepository;
import org.sonar.ce.task.projectanalysis.issue.filter.IssueFilter;
import org.sonar.ce.task.projectanalysis.qualityprofile.ActiveRulesHolder;
import org.sonar.ce.task.projectanalysis.qualityprofile.ActiveRulesHolderRule;
import org.sonar.ce.task.projectanalysis.qualityprofile.AlwaysActiveRulesHolderImpl;
import org.sonar.ce.task.projectanalysis.source.NewLinesRepository;
import org.sonar.ce.task.projectanalysis.source.SourceLinesHashRepository;
import org.sonar.ce.task.projectanalysis.source.SourceLinesRepositoryRule;
import org.sonar.core.issue.DefaultIssue;
import org.sonar.db.DbTester;
import org.sonar.db.rule.RuleTesting;
import org.sonar.scanner.protocol.output.ScannerReport;


public class IntegrateIssuesVisitorTest {
    private static final String FILE_UUID = "FILE_UUID";

    private static final String FILE_UUID_ON_BRANCH = "FILE_UUID_BRANCH";

    private static final String FILE_KEY = "FILE_KEY";

    private static final int FILE_REF = 2;

    private static final Component FILE = ReportComponent.builder(Component.Type.FILE, IntegrateIssuesVisitorTest.FILE_REF).setKey(IntegrateIssuesVisitorTest.FILE_KEY).setUuid(IntegrateIssuesVisitorTest.FILE_UUID).build();

    private static final String PROJECT_KEY = "PROJECT_KEY";

    private static final String PROJECT_UUID = "PROJECT_UUID";

    private static final String PROJECT_UUID_ON_BRANCH = "PROJECT_UUID_BRANCH";

    private static final int PROJECT_REF = 1;

    private static final Component PROJECT = ReportComponent.builder(Component.Type.PROJECT, IntegrateIssuesVisitorTest.PROJECT_REF).setKey(IntegrateIssuesVisitorTest.PROJECT_KEY).setUuid(IntegrateIssuesVisitorTest.PROJECT_UUID).addChildren(IntegrateIssuesVisitorTest.FILE).build();

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule();

    @Rule
    public BatchReportReaderRule reportReader = new BatchReportReaderRule();

    @Rule
    public ActiveRulesHolderRule activeRulesHolderRule = new ActiveRulesHolderRule();

    @Rule
    public RuleRepositoryRule ruleRepositoryRule = new RuleRepositoryRule();

    @Rule
    public SourceLinesRepositoryRule fileSourceRepository = new SourceLinesRepositoryRule();

    private AnalysisMetadataHolder analysisMetadataHolder = Mockito.mock(AnalysisMetadataHolder.class);

    private IssueFilter issueFilter = Mockito.mock(IssueFilter.class);

    private MovedFilesRepository movedFilesRepository = Mockito.mock(MovedFilesRepository.class);

    private IssueLifecycle issueLifecycle = Mockito.mock(IssueLifecycle.class);

    private IssueVisitor issueVisitor = Mockito.mock(IssueVisitor.class);

    private MergeBranchComponentUuids mergeBranchComponentsUuids = Mockito.mock(MergeBranchComponentUuids.class);

    private ShortBranchIssueMerger issueStatusCopier = Mockito.mock(ShortBranchIssueMerger.class);

    private MergeBranchComponentUuids mergeBranchComponentUuids = Mockito.mock(MergeBranchComponentUuids.class);

    private SourceLinesHashRepository sourceLinesHash = Mockito.mock(SourceLinesHashRepository.class);

    private IssueRelocationToRoot issueRelocationToRoot = Mockito.mock(IssueRelocationToRoot.class);

    private NewLinesRepository newLinesRepository = Mockito.mock(NewLinesRepository.class);

    private ArgumentCaptor<DefaultIssue> defaultIssueCaptor;

    private ComponentIssuesLoader issuesLoader = new ComponentIssuesLoader(dbTester.getDbClient(), ruleRepositoryRule, activeRulesHolderRule, new MapSettings().asConfig(), System2.INSTANCE);

    private IssueTrackingDelegator trackingDelegator;

    private TrackerExecution tracker;

    private ShortBranchOrPullRequestTrackerExecution shortBranchTracker;

    private MergeBranchTrackerExecution mergeBranchTracker;

    private ActiveRulesHolder activeRulesHolder = new AlwaysActiveRulesHolderImpl();

    private IssueCache issueCache;

    private TypeAwareVisitor underTest;

    @Test
    public void process_new_issue() {
        Mockito.when(analysisMetadataHolder.isLongLivingBranch()).thenReturn(true);
        ScannerReport.Issue reportIssue = Issue.newBuilder().setMsg("the message").setRuleRepository("xoo").setRuleKey("S001").setSeverity(BLOCKER).build();
        reportReader.putIssues(IntegrateIssuesVisitorTest.FILE_REF, Arrays.asList(reportIssue));
        fileSourceRepository.addLine(IntegrateIssuesVisitorTest.FILE_REF, "line1");
        underTest.visitAny(IntegrateIssuesVisitorTest.FILE);
        Mockito.verify(issueLifecycle).initNewOpenIssue(defaultIssueCaptor.capture());
        DefaultIssue capturedIssue = defaultIssueCaptor.getValue();
        assertThat(capturedIssue.ruleKey().rule()).isEqualTo("S001");
        Mockito.verify(issueStatusCopier).tryMerge(IntegrateIssuesVisitorTest.FILE, Collections.singletonList(capturedIssue));
        Mockito.verify(issueLifecycle).doAutomaticTransition(capturedIssue);
        assertThat(Lists.newArrayList(issueCache.traverse())).hasSize(1);
    }

    @Test
    public void process_existing_issue() {
        RuleKey ruleKey = RuleTesting.XOO_X1;
        // Issue from db has severity major
        addBaseIssue(ruleKey);
        // Issue from report has severity blocker
        ScannerReport.Issue reportIssue = Issue.newBuilder().setMsg("the message").setRuleRepository(ruleKey.repository()).setRuleKey(ruleKey.rule()).setSeverity(BLOCKER).build();
        reportReader.putIssues(IntegrateIssuesVisitorTest.FILE_REF, Arrays.asList(reportIssue));
        fileSourceRepository.addLine(IntegrateIssuesVisitorTest.FILE_REF, "line1");
        underTest.visitAny(IntegrateIssuesVisitorTest.FILE);
        ArgumentCaptor<DefaultIssue> rawIssueCaptor = ArgumentCaptor.forClass(DefaultIssue.class);
        ArgumentCaptor<DefaultIssue> baseIssueCaptor = ArgumentCaptor.forClass(DefaultIssue.class);
        Mockito.verify(issueLifecycle).mergeExistingOpenIssue(rawIssueCaptor.capture(), baseIssueCaptor.capture());
        assertThat(rawIssueCaptor.getValue().severity()).isEqualTo(Severity.BLOCKER);
        assertThat(baseIssueCaptor.getValue().severity()).isEqualTo(MAJOR);
        Mockito.verify(issueLifecycle).doAutomaticTransition(defaultIssueCaptor.capture());
        assertThat(defaultIssueCaptor.getValue().ruleKey()).isEqualTo(ruleKey);
        List<DefaultIssue> issues = Lists.newArrayList(issueCache.traverse());
        assertThat(issues).hasSize(1);
        assertThat(issues.get(0).severity()).isEqualTo(Severity.BLOCKER);
    }

    @Test
    public void execute_issue_visitors() {
        ScannerReport.Issue reportIssue = Issue.newBuilder().setMsg("the message").setRuleRepository("xoo").setRuleKey("S001").setSeverity(BLOCKER).build();
        reportReader.putIssues(IntegrateIssuesVisitorTest.FILE_REF, Arrays.asList(reportIssue));
        fileSourceRepository.addLine(IntegrateIssuesVisitorTest.FILE_REF, "line1");
        underTest.visitAny(IntegrateIssuesVisitorTest.FILE);
        Mockito.verify(issueVisitor).beforeComponent(IntegrateIssuesVisitorTest.FILE);
        Mockito.verify(issueVisitor).afterComponent(IntegrateIssuesVisitorTest.FILE);
        Mockito.verify(issueVisitor).onIssue(ArgumentMatchers.eq(IntegrateIssuesVisitorTest.FILE), defaultIssueCaptor.capture());
        assertThat(defaultIssueCaptor.getValue().ruleKey().rule()).isEqualTo("S001");
    }

    @Test
    public void close_unmatched_base_issue() {
        RuleKey ruleKey = RuleTesting.XOO_X1;
        addBaseIssue(ruleKey);
        // No issue in the report
        underTest.visitAny(IntegrateIssuesVisitorTest.FILE);
        Mockito.verify(issueLifecycle).doAutomaticTransition(defaultIssueCaptor.capture());
        assertThat(defaultIssueCaptor.getValue().isBeingClosed()).isTrue();
        List<DefaultIssue> issues = Lists.newArrayList(issueCache.traverse());
        assertThat(issues).hasSize(1);
    }

    @Test
    public void remove_uuid_of_original_file_from_componentsWithUnprocessedIssues_if_component_has_one() {
        String originalFileUuid = "original file uuid";
        Mockito.when(movedFilesRepository.getOriginalFile(IntegrateIssuesVisitorTest.FILE)).thenReturn(Optional.of(new MovedFilesRepository.OriginalFile(4851, originalFileUuid, "original file key")));
        underTest.visitAny(IntegrateIssuesVisitorTest.FILE);
    }

    @Test
    public void copy_issues_when_creating_new_long_living_branch() {
        Mockito.when(mergeBranchComponentsUuids.getUuid(IntegrateIssuesVisitorTest.FILE_KEY)).thenReturn(IntegrateIssuesVisitorTest.FILE_UUID_ON_BRANCH);
        Mockito.when(mergeBranchComponentUuids.getMergeBranchName()).thenReturn("master");
        Mockito.when(analysisMetadataHolder.isLongLivingBranch()).thenReturn(true);
        Mockito.when(analysisMetadataHolder.isFirstAnalysis()).thenReturn(true);
        Branch branch = Mockito.mock(Branch.class);
        Mockito.when(branch.isMain()).thenReturn(false);
        Mockito.when(branch.getType()).thenReturn(LONG);
        Mockito.when(analysisMetadataHolder.getBranch()).thenReturn(branch);
        RuleKey ruleKey = RuleTesting.XOO_X1;
        // Issue from main branch has severity major
        addBaseIssueOnBranch(ruleKey);
        // Issue from report has severity blocker
        ScannerReport.Issue reportIssue = Issue.newBuilder().setMsg("the message").setRuleRepository(ruleKey.repository()).setRuleKey(ruleKey.rule()).setSeverity(BLOCKER).build();
        reportReader.putIssues(IntegrateIssuesVisitorTest.FILE_REF, Arrays.asList(reportIssue));
        fileSourceRepository.addLine(IntegrateIssuesVisitorTest.FILE_REF, "line1");
        underTest.visitAny(IntegrateIssuesVisitorTest.FILE);
        ArgumentCaptor<DefaultIssue> rawIssueCaptor = ArgumentCaptor.forClass(DefaultIssue.class);
        ArgumentCaptor<DefaultIssue> baseIssueCaptor = ArgumentCaptor.forClass(DefaultIssue.class);
        Mockito.verify(issueLifecycle).copyExistingOpenIssueFromLongLivingBranch(rawIssueCaptor.capture(), baseIssueCaptor.capture(), ArgumentMatchers.eq("master"));
        assertThat(rawIssueCaptor.getValue().severity()).isEqualTo(Severity.BLOCKER);
        assertThat(baseIssueCaptor.getValue().severity()).isEqualTo(MAJOR);
        Mockito.verify(issueLifecycle).doAutomaticTransition(defaultIssueCaptor.capture());
        assertThat(defaultIssueCaptor.getValue().ruleKey()).isEqualTo(ruleKey);
        List<DefaultIssue> issues = Lists.newArrayList(issueCache.traverse());
        assertThat(issues).hasSize(1);
        assertThat(issues.get(0).severity()).isEqualTo(Severity.BLOCKER);
    }
}

