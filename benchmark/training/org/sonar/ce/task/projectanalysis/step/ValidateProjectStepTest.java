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
package org.sonar.ce.task.projectanalysis.step;


import BranchType.LONG;
import BranchType.PULL_REQUEST;
import BranchType.SHORT;
import Component.Type.PROJECT;
import System2.INSTANCE;
import java.util.Date;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.DateUtils;
import org.sonar.api.utils.MessageException;
import org.sonar.ce.task.projectanalysis.analysis.AnalysisMetadataHolderRule;
import org.sonar.ce.task.projectanalysis.analysis.Branch;
import org.sonar.ce.task.projectanalysis.component.DefaultBranchImpl;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolderRule;
import org.sonar.ce.task.step.TestComputationStepContext;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.component.SnapshotTesting;


public class ValidateProjectStepTest {
    static long DEFAULT_ANALYSIS_TIME = 1433131200000L;// 2015-06-01


    static final String PROJECT_KEY = "PROJECT_KEY";

    static final Branch DEFAULT_BRANCH = new DefaultBranchImpl();

    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule();

    @Rule
    public AnalysisMetadataHolderRule analysisMetadataHolder = new AnalysisMetadataHolderRule().setAnalysisDate(new Date(ValidateProjectStepTest.DEFAULT_ANALYSIS_TIME)).setBranch(ValidateProjectStepTest.DEFAULT_BRANCH);

    DbClient dbClient = dbTester.getDbClient();

    ValidateProjectStep underTest = new ValidateProjectStep(dbClient, treeRootHolder, analysisMetadataHolder);

    @Test
    public void fail_if_slb_is_targeting_master_with_modules() {
        ComponentDto masterProject = dbTester.components().insertMainBranch();
        dbClient.componentDao().insert(dbTester.getSession(), ComponentTesting.newModuleDto(masterProject));
        setBranch(SHORT, masterProject.uuid());
        dbTester.getSession().commit();
        treeRootHolder.setRoot(ReportComponent.builder(PROJECT, 1).setUuid("DEFG").setKey("branch").build());
        thrown.expect(MessageException.class);
        thrown.expectMessage("Due to an upgrade, you need first to re-analyze the target branch 'master' before analyzing this short-lived branch.");
        underTest.execute(new TestComputationStepContext());
    }

    @Test
    public void fail_if_pr_is_targeting_branch_with_modules() {
        ComponentDto masterProject = dbTester.components().insertMainBranch();
        ComponentDto mergeBranch = dbTester.components().insertProjectBranch(masterProject, ( b) -> b.setKey("mergeBranch"));
        dbClient.componentDao().insert(dbTester.getSession(), ComponentTesting.newModuleDto(mergeBranch));
        setBranch(PULL_REQUEST, mergeBranch.uuid());
        dbTester.getSession().commit();
        treeRootHolder.setRoot(ReportComponent.builder(PROJECT, 1).setUuid("DEFG").setKey("branch").build());
        thrown.expect(MessageException.class);
        thrown.expectMessage("Due to an upgrade, you need first to re-analyze the target branch 'mergeBranch' before analyzing this pull request.");
        underTest.execute(new TestComputationStepContext());
    }

    @Test
    public void dont_fail_if_slb_is_targeting_branch_without_modules() {
        ComponentDto masterProject = dbTester.components().insertMainBranch();
        setBranch(SHORT, masterProject.uuid());
        dbTester.getSession().commit();
        treeRootHolder.setRoot(ReportComponent.builder(PROJECT, 1).setUuid("DEFG").setKey("branch").build());
        underTest.execute(new TestComputationStepContext());
    }

    @Test
    public void dont_fail_for_long_forked_from_master_with_modules() {
        ComponentDto masterProject = dbTester.components().insertMainBranch();
        dbClient.componentDao().insert(dbTester.getSession(), ComponentTesting.newModuleDto(masterProject));
        setBranch(LONG, masterProject.uuid());
        dbTester.getSession().commit();
        treeRootHolder.setRoot(ReportComponent.builder(PROJECT, 1).setUuid("DEFG").setKey("branch").build());
        underTest.execute(new TestComputationStepContext());
    }

    @Test
    public void not_fail_if_analysis_date_is_after_last_analysis() {
        ComponentDto project = ComponentTesting.newPrivateProjectDto(dbTester.organizations().insert(), "ABCD").setDbKey(ValidateProjectStepTest.PROJECT_KEY);
        dbClient.componentDao().insert(dbTester.getSession(), project);
        dbClient.snapshotDao().insert(dbTester.getSession(), SnapshotTesting.newAnalysis(project).setCreatedAt(1420088400000L));// 2015-01-01

        dbTester.getSession().commit();
        treeRootHolder.setRoot(ReportComponent.builder(PROJECT, 1).setUuid("ABCD").setKey(ValidateProjectStepTest.PROJECT_KEY).build());
        underTest.execute(new TestComputationStepContext());
    }

    @Test
    public void fail_if_analysis_date_is_before_last_analysis() {
        analysisMetadataHolder.setAnalysisDate(DateUtils.parseDate("2015-01-01"));
        ComponentDto project = ComponentTesting.newPrivateProjectDto(dbTester.organizations().insert(), "ABCD").setDbKey(ValidateProjectStepTest.PROJECT_KEY);
        dbClient.componentDao().insert(dbTester.getSession(), project);
        dbClient.snapshotDao().insert(dbTester.getSession(), SnapshotTesting.newAnalysis(project).setCreatedAt(1433131200000L));// 2015-06-01

        dbTester.getSession().commit();
        treeRootHolder.setRoot(ReportComponent.builder(PROJECT, 1).setUuid("ABCD").setKey(ValidateProjectStepTest.PROJECT_KEY).build());
        thrown.expect(MessageException.class);
        thrown.expectMessage("Validation of project failed:");
        thrown.expectMessage("Date of analysis cannot be older than the date of the last known analysis on this project. Value: ");
        thrown.expectMessage("Latest analysis: ");
        underTest.execute(new TestComputationStepContext());
    }
}

