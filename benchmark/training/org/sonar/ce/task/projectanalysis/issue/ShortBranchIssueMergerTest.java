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


import Issue.RESOLUTION_FALSE_POSITIVE;
import Issue.STATUS_CONFIRMED;
import Issue.STATUS_OPEN;
import Issue.STATUS_REOPENED;
import Issue.STATUS_RESOLVED;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.component.Component.Type.FILE;
import org.sonar.ce.task.projectanalysis.component.Component.Type.PROJECT;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolderRule;
import org.sonar.core.issue.DefaultIssue;
import org.sonar.core.issue.FieldDiffs;
import org.sonar.core.issue.tracking.SimpleTracker;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.issue.IssueDto;
import org.sonar.db.issue.IssueTesting;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.db.user.UserDto;


public class ShortBranchIssueMergerTest {
    @Mock
    private IssueLifecycle issueLifecycle;

    @Rule
    public DbTester db = DbTester.create();

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule().setRoot(ReportComponent.builder(PROJECT, ShortBranchIssueMergerTest.PROJECT_REF).setKey(ShortBranchIssueMergerTest.PROJECT_KEY).setUuid(ShortBranchIssueMergerTest.PROJECT_UUID).addChildren(ShortBranchIssueMergerTest.FILE_1).build());

    private static final String PROJECT_KEY = "project";

    private static final int PROJECT_REF = 1;

    private static final String PROJECT_UUID = "projectUuid";

    private static final int FILE_1_REF = 12341;

    private static final String FILE_1_KEY = "fileKey";

    private static final String FILE_1_UUID = "fileUuid";

    private static final Component FILE_1 = ReportComponent.builder(FILE, ShortBranchIssueMergerTest.FILE_1_REF).setKey(ShortBranchIssueMergerTest.FILE_1_KEY).setUuid(ShortBranchIssueMergerTest.FILE_1_UUID).build();

    private SimpleTracker<DefaultIssue, ShortBranchIssue> tracker = new SimpleTracker();

    private ShortBranchIssueMerger copier;

    private ComponentDto fileOnBranch1Dto;

    private ComponentDto fileOnBranch2Dto;

    private ComponentDto fileOnBranch3Dto;

    private ComponentDto projectDto;

    private ComponentDto branch1Dto;

    private ComponentDto branch2Dto;

    private ComponentDto branch3Dto;

    private RuleDefinitionDto rule;

    @Test
    public void do_nothing_if_no_match() {
        DefaultIssue i = ShortBranchIssueMergerTest.createIssue("issue1", rule.getKey(), STATUS_CONFIRMED, null, new Date());
        copier.tryMerge(ShortBranchIssueMergerTest.FILE_1, Collections.singleton(i));
        Mockito.verifyZeroInteractions(issueLifecycle);
    }

    @Test
    public void do_nothing_if_no_new_issue() {
        db.issues().insertIssue(IssueTesting.newIssue(rule, branch1Dto, fileOnBranch1Dto).setKee("issue1").setStatus(STATUS_CONFIRMED).setLine(1).setChecksum("checksum"));
        copier.tryMerge(ShortBranchIssueMergerTest.FILE_1, Collections.emptyList());
        Mockito.verifyZeroInteractions(issueLifecycle);
    }

    @Test
    public void merge_confirmed_issues() {
        db.issues().insertIssue(IssueTesting.newIssue(rule, branch1Dto, fileOnBranch1Dto).setKee("issue1").setStatus(STATUS_CONFIRMED).setLine(1).setChecksum("checksum"));
        DefaultIssue newIssue = ShortBranchIssueMergerTest.createIssue("issue2", rule.getKey(), STATUS_OPEN, null, new Date());
        copier.tryMerge(ShortBranchIssueMergerTest.FILE_1, Collections.singleton(newIssue));
        ArgumentCaptor<DefaultIssue> issueToMerge = ArgumentCaptor.forClass(DefaultIssue.class);
        Mockito.verify(issueLifecycle).mergeConfirmedOrResolvedFromShortLivingBranch(ArgumentMatchers.eq(newIssue), issueToMerge.capture(), ArgumentMatchers.eq("myBranch1"));
        assertThat(issueToMerge.getValue().key()).isEqualTo("issue1");
    }

    @Test
    public void prefer_resolved_issues() {
        db.issues().insertIssue(IssueTesting.newIssue(rule, branch1Dto, fileOnBranch1Dto).setKee("issue1").setStatus(STATUS_REOPENED).setLine(1).setChecksum("checksum"));
        db.issues().insertIssue(IssueTesting.newIssue(rule, branch2Dto, fileOnBranch2Dto).setKee("issue2").setStatus(STATUS_CONFIRMED).setLine(1).setChecksum("checksum"));
        db.issues().insertIssue(IssueTesting.newIssue(rule, branch3Dto, fileOnBranch3Dto).setKee("issue3").setStatus(STATUS_RESOLVED).setResolution(RESOLUTION_FALSE_POSITIVE).setLine(1).setChecksum("checksum"));
        DefaultIssue newIssue = ShortBranchIssueMergerTest.createIssue("newIssue", rule.getKey(), STATUS_OPEN, null, new Date());
        copier.tryMerge(ShortBranchIssueMergerTest.FILE_1, Collections.singleton(newIssue));
        ArgumentCaptor<DefaultIssue> issueToMerge = ArgumentCaptor.forClass(DefaultIssue.class);
        Mockito.verify(issueLifecycle).mergeConfirmedOrResolvedFromShortLivingBranch(ArgumentMatchers.eq(newIssue), issueToMerge.capture(), ArgumentMatchers.eq("myBranch3"));
        assertThat(issueToMerge.getValue().key()).isEqualTo("issue3");
    }

    @Test
    public void prefer_confirmed_issues_if_no_resolved() {
        db.issues().insertIssue(IssueTesting.newIssue(rule, branch1Dto, fileOnBranch1Dto).setKee("issue1").setStatus(STATUS_REOPENED).setLine(1).setChecksum("checksum"));
        db.issues().insertIssue(IssueTesting.newIssue(rule, branch2Dto, fileOnBranch2Dto).setKee("issue2").setStatus(STATUS_OPEN).setLine(1).setChecksum("checksum"));
        db.issues().insertIssue(IssueTesting.newIssue(rule, branch3Dto, fileOnBranch3Dto).setKee("issue3").setStatus(STATUS_CONFIRMED).setLine(1).setChecksum("checksum"));
        DefaultIssue newIssue = ShortBranchIssueMergerTest.createIssue("newIssue", rule.getKey(), STATUS_OPEN, null, new Date());
        copier.tryMerge(ShortBranchIssueMergerTest.FILE_1, Collections.singleton(newIssue));
        ArgumentCaptor<DefaultIssue> issueToMerge = ArgumentCaptor.forClass(DefaultIssue.class);
        Mockito.verify(issueLifecycle).mergeConfirmedOrResolvedFromShortLivingBranch(ArgumentMatchers.eq(newIssue), issueToMerge.capture(), ArgumentMatchers.eq("myBranch3"));
        assertThat(issueToMerge.getValue().key()).isEqualTo("issue3");
    }

    @Test
    public void prefer_older_issues() {
        Instant now = Instant.now();
        db.issues().insertIssue(IssueTesting.newIssue(rule, branch1Dto, fileOnBranch1Dto).setKee("issue1").setStatus(STATUS_REOPENED).setLine(1).setChecksum("checksum").setIssueCreationDate(Date.from(now.plus(2, ChronoUnit.SECONDS))));
        db.issues().insertIssue(IssueTesting.newIssue(rule, branch2Dto, fileOnBranch2Dto).setKee("issue2").setStatus(STATUS_OPEN).setLine(1).setChecksum("checksum").setIssueCreationDate(Date.from(now.plus(1, ChronoUnit.SECONDS))));
        db.issues().insertIssue(IssueTesting.newIssue(rule, branch3Dto, fileOnBranch3Dto).setKee("issue3").setStatus(STATUS_OPEN).setLine(1).setChecksum("checksum").setIssueCreationDate(Date.from(now)));
        DefaultIssue newIssue = ShortBranchIssueMergerTest.createIssue("newIssue", rule.getKey(), STATUS_OPEN, null, new Date());
        copier.tryMerge(ShortBranchIssueMergerTest.FILE_1, Collections.singleton(newIssue));
        ArgumentCaptor<DefaultIssue> issueToMerge = ArgumentCaptor.forClass(DefaultIssue.class);
        Mockito.verify(issueLifecycle).mergeConfirmedOrResolvedFromShortLivingBranch(ArgumentMatchers.eq(newIssue), issueToMerge.capture(), ArgumentMatchers.eq("myBranch3"));
        assertThat(issueToMerge.getValue().key()).isEqualTo("issue3");
    }

    @Test
    public void lazy_load_changes() {
        UserDto user1 = db.users().insertUser();
        IssueDto issue1 = db.issues().insertIssue(IssueTesting.newIssue(rule, branch1Dto, fileOnBranch1Dto).setKee("issue1").setStatus(STATUS_REOPENED).setLine(1).setChecksum("checksum"));
        db.issues().insertComment(issue1, user1, "A comment 1");
        db.issues().insertFieldDiffs(issue1, FieldDiffs.parse("severity=BLOCKER|INFO,assignee=toto|titi").setCreationDate(new Date()));
        UserDto user2 = db.users().insertUser();
        IssueDto issue2 = db.issues().insertIssue(IssueTesting.newIssue(rule, branch2Dto, fileOnBranch2Dto).setKee("issue2").setStatus(STATUS_CONFIRMED).setLine(1).setChecksum("checksum"));
        db.issues().insertComment(issue2, user2, "A comment 2");
        db.issues().insertFieldDiffs(issue2, FieldDiffs.parse("severity=BLOCKER|MINOR,assignee=foo|bar").setCreationDate(new Date()));
        DefaultIssue newIssue = ShortBranchIssueMergerTest.createIssue("newIssue", rule.getKey(), STATUS_OPEN, null, new Date());
        copier.tryMerge(ShortBranchIssueMergerTest.FILE_1, Collections.singleton(newIssue));
        ArgumentCaptor<DefaultIssue> issueToMerge = ArgumentCaptor.forClass(DefaultIssue.class);
        Mockito.verify(issueLifecycle).mergeConfirmedOrResolvedFromShortLivingBranch(ArgumentMatchers.eq(newIssue), issueToMerge.capture(), ArgumentMatchers.eq("myBranch2"));
        assertThat(issueToMerge.getValue().key()).isEqualTo("issue2");
        assertThat(issueToMerge.getValue().defaultIssueComments()).isNotEmpty();
        assertThat(issueToMerge.getValue().changes()).isNotEmpty();
    }
}

