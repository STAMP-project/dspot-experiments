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
package org.sonar.ce.task.projectanalysis.purge;


import Component.Type.PROJECT;
import Component.Type.VIEW;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.analysis.AnalysisMetadataHolderRule;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.component.ConfigurationRepository;
import org.sonar.ce.task.projectanalysis.component.MutableDisabledComponentsHolder;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolderRule;
import org.sonar.ce.task.projectanalysis.component.ViewsComponent;
import org.sonar.ce.task.projectanalysis.step.BaseStepTest;
import org.sonar.db.DbClient;


@RunWith(DataProviderRunner.class)
public class PurgeDatastoresStepTest extends BaseStepTest {
    private static final String PROJECT_KEY = "PROJECT_KEY";

    private static final String PROJECT_UUID = "UUID-1234";

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule();

    @Rule
    public AnalysisMetadataHolderRule analysisMetadataHolder = new AnalysisMetadataHolderRule();

    private ProjectCleaner projectCleaner = Mockito.mock(ProjectCleaner.class);

    private ConfigurationRepository settingsRepository = Mockito.mock(ConfigurationRepository.class);

    private MutableDisabledComponentsHolder disabledComponentsHolder = Mockito.mock(MutableDisabledComponentsHolder.class, Mockito.RETURNS_DEEP_STUBS);

    private PurgeDatastoresStep underTest = new PurgeDatastoresStep(Mockito.mock(DbClient.class, Mockito.RETURNS_DEEP_STUBS), projectCleaner, treeRootHolder, settingsRepository, disabledComponentsHolder, analysisMetadataHolder);

    @Test
    public void call_purge_method_of_the_purge_task_for_project() {
        Component project = ReportComponent.builder(PROJECT, 1).setUuid(PurgeDatastoresStepTest.PROJECT_UUID).setKey(PurgeDatastoresStepTest.PROJECT_KEY).build();
        verify_call_purge_method_of_the_purge_task(project);
    }

    @Test
    public void call_purge_method_of_the_purge_task_for_view() {
        Component project = ViewsComponent.builder(VIEW, PurgeDatastoresStepTest.PROJECT_KEY).setUuid(PurgeDatastoresStepTest.PROJECT_UUID).build();
        verify_call_purge_method_of_the_purge_task(project);
    }
}

