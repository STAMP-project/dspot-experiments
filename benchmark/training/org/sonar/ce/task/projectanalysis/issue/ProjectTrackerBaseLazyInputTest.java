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


import System2.INSTANCE;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.Date;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.utils.System2;
import org.sonar.ce.task.projectanalysis.analysis.AnalysisMetadataHolderRule;
import org.sonar.ce.task.projectanalysis.component.ReportModulesPath;
import org.sonar.ce.task.projectanalysis.qualityprofile.ActiveRulesHolderRule;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.issue.IssueDto;
import org.sonar.db.rule.RuleDefinitionDto;


public class ProjectTrackerBaseLazyInputTest {
    private static final Date ANALYSIS_DATE = parseDate("2016-06-01");

    @Rule
    public AnalysisMetadataHolderRule analysisMetadataHolder = new AnalysisMetadataHolderRule().setAnalysisDate(ProjectTrackerBaseLazyInputTest.ANALYSIS_DATE);

    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    @Rule
    public ActiveRulesHolderRule activeRulesHolderRule = new ActiveRulesHolderRule();

    @Rule
    public RuleRepositoryRule ruleRepositoryRule = new RuleRepositoryRule();

    private DbClient dbClient = dbTester.getDbClient();

    private ProjectTrackerBaseLazyInput underTest;

    private RuleDefinitionDto rule;

    private ComponentDto rootProjectDto;

    private ComponentIssuesLoader issuesLoader = new ComponentIssuesLoader(dbTester.getDbClient(), ruleRepositoryRule, activeRulesHolderRule, new MapSettings().asConfig(), System2.INSTANCE);

    private ReportModulesPath reportModulesPath;

    @Test
    public void return_only_open_project_issues_if_no_modules_and_folders() {
        ComponentDto file = dbTester.components().insertComponent(ComponentTesting.newFileDto(rootProjectDto));
        IssueDto openIssueOnProject = dbTester.issues().insert(rule, rootProjectDto, rootProjectDto, ( i) -> i.setStatus("OPEN").setResolution(null));
        IssueDto closedIssueOnProject = dbTester.issues().insert(rule, rootProjectDto, rootProjectDto, ( i) -> i.setStatus("CLOSED").setResolution("FIXED"));
        IssueDto openIssue1OnFile = dbTester.issues().insert(rule, rootProjectDto, file, ( i) -> i.setStatus("OPEN").setResolution(null));
        assertThat(underTest.loadIssues()).extracting(DefaultIssue::key).containsOnly(openIssueOnProject.getKey());
    }

    @Test
    public void migrate_and_return_folder_issues_on_root_project() {
        Mockito.when(reportModulesPath.get()).thenReturn(Collections.emptyMap());
        ComponentDto folder = dbTester.components().insertComponent(ComponentTesting.newDirectory(rootProjectDto, "src"));
        ComponentDto file = dbTester.components().insertComponent(ComponentTesting.newFileDto(rootProjectDto));
        IssueDto openIssueOnProject = dbTester.issues().insert(rule, rootProjectDto, rootProjectDto, ( i) -> i.setStatus("OPEN").setResolution(null));
        IssueDto openIssueOnDir = dbTester.issues().insert(rule, rootProjectDto, folder, ( i) -> i.setStatus("OPEN").setMessage("Issue on dir").setResolution(null));
        IssueDto openIssue1OnFile = dbTester.issues().insert(rule, rootProjectDto, file, ( i) -> i.setStatus("OPEN").setResolution(null));
        assertThat(underTest.loadIssues()).extracting(DefaultIssue::key, DefaultIssue::getMessage).containsExactlyInAnyOrder(tuple(openIssueOnProject.getKey(), openIssueOnProject.getMessage()), tuple(openIssueOnDir.getKey(), "[src] Issue on dir"));
    }

    @Test
    public void migrate_and_return_module_and_folder_issues_on_module() {
        ComponentDto module = dbTester.components().insertComponent(ComponentTesting.newModuleDto(rootProjectDto).setPath("moduleAInDb"));
        Mockito.when(reportModulesPath.get()).thenReturn(ImmutableMap.of(module.getDbKey(), "moduleAInReport"));
        ComponentDto folder = dbTester.components().insertComponent(ComponentTesting.newDirectory(module, "src"));
        ComponentDto file = dbTester.components().insertComponent(ComponentTesting.newFileDto(module));
        IssueDto openIssueOnProject = dbTester.issues().insert(rule, rootProjectDto, rootProjectDto, ( i) -> i.setStatus("OPEN").setResolution(null));
        IssueDto openIssueOnModule = dbTester.issues().insert(rule, rootProjectDto, module, ( i) -> i.setStatus("OPEN").setMessage("Issue on module").setResolution(null));
        IssueDto openIssueOnDir = dbTester.issues().insert(rule, rootProjectDto, folder, ( i) -> i.setStatus("OPEN").setMessage("Issue on dir").setResolution(null));
        IssueDto openIssue1OnFile = dbTester.issues().insert(rule, rootProjectDto, file, ( i) -> i.setStatus("OPEN").setResolution(null));
        assertThat(underTest.loadIssues()).extracting(DefaultIssue::key, DefaultIssue::getMessage).containsExactlyInAnyOrder(tuple(openIssueOnProject.getKey(), openIssueOnProject.getMessage()), tuple(openIssueOnModule.getKey(), "[moduleAInReport] Issue on module"), tuple(openIssueOnDir.getKey(), "[moduleAInReport/src] Issue on dir"));
    }

    @Test
    public void use_db_path_if_module_missing_in_report() {
        ComponentDto module = dbTester.components().insertComponent(ComponentTesting.newModuleDto(rootProjectDto).setPath("moduleAInDb"));
        Mockito.when(reportModulesPath.get()).thenReturn(Collections.emptyMap());
        ComponentDto folder = dbTester.components().insertComponent(ComponentTesting.newDirectory(module, "src"));
        IssueDto openIssueOnProject = dbTester.issues().insert(rule, rootProjectDto, rootProjectDto, ( i) -> i.setStatus("OPEN").setResolution(null));
        IssueDto openIssueOnModule = dbTester.issues().insert(rule, rootProjectDto, module, ( i) -> i.setStatus("OPEN").setMessage("Issue on module").setResolution(null));
        IssueDto openIssueOnDir = dbTester.issues().insert(rule, rootProjectDto, folder, ( i) -> i.setStatus("OPEN").setMessage("Issue on dir").setResolution(null));
        assertThat(underTest.loadIssues()).extracting(DefaultIssue::key, DefaultIssue::getMessage).containsExactlyInAnyOrder(tuple(openIssueOnProject.getKey(), openIssueOnProject.getMessage()), tuple(openIssueOnModule.getKey(), "[moduleAInDb] Issue on module"), tuple(openIssueOnDir.getKey(), "[moduleAInDb/src] Issue on dir"));
    }

    @Test
    public void empty_path_if_module_missing_in_report_and_db_and_for_slash_folder() {
        ComponentDto module = dbTester.components().insertComponent(ComponentTesting.newModuleDto(rootProjectDto).setPath(null));
        Mockito.when(reportModulesPath.get()).thenReturn(Collections.emptyMap());
        ComponentDto folder = dbTester.components().insertComponent(ComponentTesting.newDirectory(module, "/"));
        IssueDto openIssueOnProject = dbTester.issues().insert(rule, rootProjectDto, rootProjectDto, ( i) -> i.setStatus("OPEN").setResolution(null));
        IssueDto openIssueOnModule = dbTester.issues().insert(rule, rootProjectDto, module, ( i) -> i.setStatus("OPEN").setMessage("Issue on module").setResolution(null));
        IssueDto openIssueOnDir = dbTester.issues().insert(rule, rootProjectDto, folder, ( i) -> i.setStatus("OPEN").setMessage("Issue on dir").setResolution(null));
        assertThat(underTest.loadIssues()).extracting(DefaultIssue::key, DefaultIssue::getMessage).containsExactlyInAnyOrder(tuple(openIssueOnProject.getKey(), openIssueOnProject.getMessage()), tuple(openIssueOnModule.getKey(), "Issue on module"), tuple(openIssueOnDir.getKey(), "Issue on dir"));
    }
}

