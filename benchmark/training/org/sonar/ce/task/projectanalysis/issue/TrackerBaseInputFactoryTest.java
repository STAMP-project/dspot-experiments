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


import Component.Type.DIRECTORY;
import Component.Type.FILE;
import com.google.common.base.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.analysis.AnalysisMetadataHolderRule;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.component.ReportModulesPath;
import org.sonar.ce.task.projectanalysis.filemove.MovedFilesRepository;
import org.sonar.core.issue.DefaultIssue;
import org.sonar.core.issue.tracking.Input;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.source.FileSourceDao;
import org.sonar.server.issue.IssueFieldsSetter;


public class TrackerBaseInputFactoryTest {
    private static final String FILE_UUID = "uuid";

    private static final String DIR_UUID = "dir";

    private static final ReportComponent FILE = ReportComponent.builder(Component.Type.FILE, 1).setUuid(TrackerBaseInputFactoryTest.FILE_UUID).build();

    private static final ReportComponent FOLDER = ReportComponent.builder(DIRECTORY, 2).setUuid(TrackerBaseInputFactoryTest.DIR_UUID).build();

    @Rule
    public AnalysisMetadataHolderRule analysisMetadataHolder = new AnalysisMetadataHolderRule();

    private ComponentIssuesLoader issuesLoader = Mockito.mock(ComponentIssuesLoader.class);

    private DbClient dbClient = Mockito.mock(DbClient.class);

    private DbSession dbSession = Mockito.mock(DbSession.class);

    private FileSourceDao fileSourceDao = Mockito.mock(FileSourceDao.class);

    private MovedFilesRepository movedFilesRepository = Mockito.mock(MovedFilesRepository.class);

    private TrackerBaseInputFactory underTest = new TrackerBaseInputFactory(issuesLoader, dbClient, movedFilesRepository, Mockito.mock(ReportModulesPath.class), analysisMetadataHolder, new IssueFieldsSetter(), Mockito.mock(ComponentsWithUnprocessedIssues.class));

    @Test
    public void create_returns_Input_which_retrieves_lines_hashes_of_specified_file_component_when_it_has_no_original_file() {
        underTest.create(TrackerBaseInputFactoryTest.FILE).getLineHashSequence();
        Mockito.verify(fileSourceDao).selectLineHashes(dbSession, TrackerBaseInputFactoryTest.FILE_UUID);
    }

    @Test
    public void create_returns_empty_Input_for_folders() {
        Input<DefaultIssue> input = underTest.create(TrackerBaseInputFactoryTest.FOLDER);
        assertThat(input.getIssues()).isEmpty();
    }

    @Test
    public void create_returns_Input_which_retrieves_lines_hashes_of_original_file_of_component_when_it_has_one() {
        String originalUuid = "original uuid";
        Mockito.when(movedFilesRepository.getOriginalFile(TrackerBaseInputFactoryTest.FILE)).thenReturn(Optional.of(new MovedFilesRepository.OriginalFile(6542, originalUuid, "original key")));
        underTest.create(TrackerBaseInputFactoryTest.FILE).getLineHashSequence();
        Mockito.verify(fileSourceDao).selectLineHashes(dbSession, originalUuid);
        Mockito.verify(fileSourceDao, Mockito.times(0)).selectLineHashes(dbSession, TrackerBaseInputFactoryTest.FILE_UUID);
    }

    @Test
    public void create_returns_Input_which_retrieves_issues_of_specified_file_component_when_it_has_no_original_file() {
        underTest.create(TrackerBaseInputFactoryTest.FILE).getIssues();
        Mockito.verify(issuesLoader).loadOpenIssues(TrackerBaseInputFactoryTest.FILE_UUID);
    }

    @Test
    public void create_returns_Input_which_retrieves_issues_of_original_file_of_component_when_it_has_one() {
        String originalUuid = "original uuid";
        Mockito.when(movedFilesRepository.getOriginalFile(TrackerBaseInputFactoryTest.FILE)).thenReturn(Optional.of(new MovedFilesRepository.OriginalFile(6542, originalUuid, "original key")));
        underTest.create(TrackerBaseInputFactoryTest.FILE).getIssues();
        Mockito.verify(issuesLoader).loadOpenIssues(originalUuid);
        Mockito.verify(issuesLoader, Mockito.times(0)).loadOpenIssues(TrackerBaseInputFactoryTest.FILE_UUID);
    }
}

