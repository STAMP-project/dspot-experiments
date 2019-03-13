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
package org.sonar.ce.task.projectanalysis.component;


import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.issue.IssueTesting;
import org.sonar.db.rule.RuleDefinitionDto;


public class ShortBranchComponentsWithIssuesTest {
    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule();

    @Rule
    public DbTester db = DbTester.create();

    private ShortBranchComponentsWithIssues underTest;

    private ComponentDto long1;

    private ComponentDto fileWithNoIssues;

    private ComponentDto fileWithOneOpenIssue;

    private ComponentDto fileWithOneResolvedIssue;

    private ComponentDto fileWithOneOpenTwoResolvedIssues;

    private ComponentDto fileWithOneResolvedIssueInLong1Short1;

    private ComponentDto fileWithOneResolvedIssueInLong1Short2;

    private ComponentDto long2;

    private ComponentDto fileWithOneOpenIssueOnLong2;

    private ComponentDto fileWithOneResolvedIssueOnLong2;

    @Test
    public void should_find_components_with_issues_to_merge_on_long1() {
        setRootUuid(long1.uuid());
        assertThat(underTest.getUuids(fileWithNoIssues.getKey())).isEmpty();
        assertThat(underTest.getUuids(fileWithOneOpenIssue.getKey())).containsOnly(fileWithOneOpenIssue.uuid());
        assertThat(underTest.getUuids(fileWithOneResolvedIssue.getKey())).containsOnly(fileWithOneResolvedIssue.uuid());
        assertThat(underTest.getUuids(fileWithOneOpenTwoResolvedIssues.getKey())).containsOnly(fileWithOneOpenTwoResolvedIssues.uuid());
        assertThat(fileWithOneResolvedIssueInLong1Short1.getKey()).isEqualTo(fileWithOneResolvedIssueInLong1Short2.getKey());
        assertThat(underTest.getUuids(fileWithOneResolvedIssueInLong1Short1.getKey())).containsOnly(fileWithOneResolvedIssueInLong1Short1.uuid(), fileWithOneResolvedIssueInLong1Short2.uuid());
    }

    @Test
    public void should_find_components_with_issues_to_merge_on_long2() {
        setRootUuid(long2.uuid());
        underTest = new ShortBranchComponentsWithIssues(treeRootHolder, db.getDbClient());
        assertThat(underTest.getUuids(fileWithOneResolvedIssue.getKey())).isEmpty();
        assertThat(underTest.getUuids(fileWithOneResolvedIssueOnLong2.getKey())).containsOnly(fileWithOneResolvedIssueOnLong2.uuid());
        assertThat(underTest.getUuids(fileWithOneOpenIssueOnLong2.getKey())).containsOnly(fileWithOneOpenIssueOnLong2.uuid());
    }

    @Test
    public void should_find_components_with_issues_to_merge_on_derived_short() {
        ComponentDto project = db.components().insertMainBranch();
        setRootUuid(project.uuid());
        ComponentDto branch = db.components().insertProjectBranch(project, ( b) -> b.setBranchType(BranchType.SHORT), ( b) -> b.setMergeBranchUuid(project.uuid()));
        RuleDefinitionDto rule = db.rules().insert();
        ComponentDto fileWithResolvedIssue = db.components().insertComponent(ComponentTesting.newFileDto(branch, null));
        db.issues().insertIssue(IssueTesting.newIssue(rule, branch, fileWithResolvedIssue).setStatus("RESOLVED"));
        underTest = new ShortBranchComponentsWithIssues(treeRootHolder, db.getDbClient());
        assertThat(underTest.getUuids(fileWithResolvedIssue.getKey())).hasSize(1);
    }

    @Test
    public void should_find_components_with_issues_to_merge_on_derived_pull_request() {
        ComponentDto project = db.components().insertMainBranch();
        setRootUuid(project.uuid());
        ComponentDto pullRequest = db.components().insertProjectBranch(project, ( b) -> b.setBranchType(BranchType.PULL_REQUEST), ( b) -> b.setMergeBranchUuid(project.uuid()));
        RuleDefinitionDto rule = db.rules().insert();
        ComponentDto fileWithResolvedIssue = db.components().insertComponent(ComponentTesting.newFileDto(pullRequest, null));
        db.issues().insertIssue(IssueTesting.newIssue(rule, pullRequest, fileWithResolvedIssue).setStatus("RESOLVED"));
        underTest = new ShortBranchComponentsWithIssues(treeRootHolder, db.getDbClient());
        assertThat(underTest.getUuids(fileWithResolvedIssue.getKey())).hasSize(1);
    }

    @Test
    public void should_not_find_components_with_issues_to_merge_on_derived_long() {
        ComponentDto project = db.components().insertMainBranch();
        setRootUuid(project.uuid());
        ComponentDto branch = db.components().insertProjectBranch(project, ( b) -> b.setBranchType(BranchType.LONG), ( b) -> b.setMergeBranchUuid(project.uuid()));
        RuleDefinitionDto rule = db.rules().insert();
        ComponentDto fileWithResolvedIssue = db.components().insertComponent(ComponentTesting.newFileDto(branch, null));
        db.issues().insertIssue(IssueTesting.newIssue(rule, branch, fileWithResolvedIssue).setStatus("RESOLVED"));
        underTest = new ShortBranchComponentsWithIssues(treeRootHolder, db.getDbClient());
        assertThat(underTest.getUuids(fileWithResolvedIssue.getKey())).isEmpty();
    }
}

