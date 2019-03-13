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


import Component.Type.PROJECT;
import SnapshotDto.STATUS_PROCESSED;
import SnapshotDto.STATUS_UNPROCESSED;
import System2.INSTANCE;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.ce.task.projectanalysis.analysis.MutableAnalysisMetadataHolderRule;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolderRule;
import org.sonar.ce.task.step.TestComputationStepContext;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.organization.OrganizationDto;


public class EnableAnalysisStepTest {
    private static final ReportComponent REPORT_PROJECT = ReportComponent.builder(PROJECT, 1).build();

    private static final String PREVIOUS_ANALYSIS_UUID = "ANALYSIS_1";

    private static final String CURRENT_ANALYSIS_UUID = "ANALYSIS_2";

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule();

    @Rule
    public MutableAnalysisMetadataHolderRule analysisMetadataHolder = new MutableAnalysisMetadataHolderRule();

    private EnableAnalysisStep underTest = new EnableAnalysisStep(db.getDbClient(), treeRootHolder, analysisMetadataHolder);

    @Test
    public void switch_islast_flag_and_mark_analysis_as_processed() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = ComponentTesting.newPrivateProjectDto(organization, EnableAnalysisStepTest.REPORT_PROJECT.getUuid());
        db.getDbClient().componentDao().insert(db.getSession(), project);
        insertAnalysis(project, EnableAnalysisStepTest.PREVIOUS_ANALYSIS_UUID, STATUS_PROCESSED, true);
        insertAnalysis(project, EnableAnalysisStepTest.CURRENT_ANALYSIS_UUID, STATUS_UNPROCESSED, false);
        db.commit();
        treeRootHolder.setRoot(EnableAnalysisStepTest.REPORT_PROJECT);
        analysisMetadataHolder.setUuid(EnableAnalysisStepTest.CURRENT_ANALYSIS_UUID);
        underTest.execute(new TestComputationStepContext());
        verifyAnalysis(EnableAnalysisStepTest.PREVIOUS_ANALYSIS_UUID, STATUS_PROCESSED, false);
        verifyAnalysis(EnableAnalysisStepTest.CURRENT_ANALYSIS_UUID, STATUS_PROCESSED, true);
    }

    @Test
    public void set_islast_flag_and_mark_as_processed_if_no_previous_analysis() {
        ComponentDto project = ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization(), EnableAnalysisStepTest.REPORT_PROJECT.getUuid());
        db.getDbClient().componentDao().insert(db.getSession(), project);
        insertAnalysis(project, EnableAnalysisStepTest.CURRENT_ANALYSIS_UUID, STATUS_UNPROCESSED, false);
        db.commit();
        treeRootHolder.setRoot(EnableAnalysisStepTest.REPORT_PROJECT);
        analysisMetadataHolder.setUuid(EnableAnalysisStepTest.CURRENT_ANALYSIS_UUID);
        underTest.execute(new TestComputationStepContext());
        verifyAnalysis(EnableAnalysisStepTest.CURRENT_ANALYSIS_UUID, STATUS_PROCESSED, true);
    }
}

