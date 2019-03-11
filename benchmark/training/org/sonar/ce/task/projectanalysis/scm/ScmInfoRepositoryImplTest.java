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
package org.sonar.ce.task.projectanalysis.scm;


import Component.Type.FILE;
import Status.SAME;
import Type.DIRECTORY;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Date;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sonar.api.utils.log.LogTester;
import org.sonar.ce.task.projectanalysis.analysis.AnalysisMetadataHolderRule;
import org.sonar.ce.task.projectanalysis.batch.BatchReportReaderRule;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.component.FileAttributes;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.source.SourceHashRepository;
import org.sonar.ce.task.projectanalysis.source.SourceLinesDiff;


@RunWith(DataProviderRunner.class)
public class ScmInfoRepositoryImplTest {
    static final int FILE_REF = 1;

    static final FileAttributes attributes = new FileAttributes(false, "java", 3);

    static final Component FILE = ReportComponent.builder(Component.Type.FILE, ScmInfoRepositoryImplTest.FILE_REF).setKey("FILE_KEY").setUuid("FILE_UUID").setFileAttributes(ScmInfoRepositoryImplTest.attributes).build();

    static final Component FILE_SAME = ReportComponent.builder(Component.Type.FILE, ScmInfoRepositoryImplTest.FILE_REF).setStatus(SAME).setKey("FILE_KEY").setUuid("FILE_UUID").setFileAttributes(ScmInfoRepositoryImplTest.attributes).build();

    static final long DATE_1 = 123456789L;

    static final long DATE_2 = 1234567810L;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public LogTester logTester = new LogTester();

    @Rule
    public BatchReportReaderRule reportReader = new BatchReportReaderRule();

    @Rule
    public AnalysisMetadataHolderRule analysisMetadata = new AnalysisMetadataHolderRule();

    private SourceHashRepository sourceHashRepository = Mockito.mock(SourceHashRepository.class);

    private SourceLinesDiff diff = Mockito.mock(SourceLinesDiff.class);

    private ScmInfoDbLoader dbLoader = Mockito.mock(ScmInfoDbLoader.class);

    private Date analysisDate = new Date();

    private ScmInfoRepositoryImpl underTest = new ScmInfoRepositoryImpl(reportReader, analysisMetadata, dbLoader, diff, sourceHashRepository);

    @Test
    public void return_empty_if_component_is_not_file() {
        Component c = Mockito.mock(Component.class);
        Mockito.when(c.getType()).thenReturn(DIRECTORY);
        assertThat(underTest.getScmInfo(c)).isEmpty();
    }

    @Test
    public void load_scm_info_from_cache_when_already_loaded() {
        addChangesetInReport("john", ScmInfoRepositoryImplTest.DATE_1, "rev-1");
        ScmInfo scmInfo = underTest.getScmInfo(ScmInfoRepositoryImplTest.FILE).get();
        assertThat(scmInfo.getAllChangesets()).hasSize(1);
        assertThat(logTester.logs(TRACE)).hasSize(1);
        logTester.clear();
        underTest.getScmInfo(ScmInfoRepositoryImplTest.FILE);
        assertThat(logTester.logs(TRACE)).isEmpty();
        Mockito.verifyZeroInteractions(dbLoader);
        Mockito.verifyZeroInteractions(sourceHashRepository);
        Mockito.verifyZeroInteractions(diff);
    }

    @Test
    public void read_from_report() {
        addChangesetInReport("john", ScmInfoRepositoryImplTest.DATE_1, "rev-1");
        ScmInfo scmInfo = underTest.getScmInfo(ScmInfoRepositoryImplTest.FILE).get();
        assertThat(scmInfo.getAllChangesets()).hasSize(1);
        Changeset changeset = scmInfo.getChangesetForLine(1);
        assertThat(changeset.getAuthor()).isEqualTo("john");
        assertThat(changeset.getDate()).isEqualTo(ScmInfoRepositoryImplTest.DATE_1);
        assertThat(changeset.getRevision()).isEqualTo("rev-1");
        assertThat(logTester.logs(TRACE)).containsOnly("Reading SCM info from report for file 'FILE_KEY'");
        Mockito.verifyZeroInteractions(dbLoader);
        Mockito.verifyZeroInteractions(sourceHashRepository);
        Mockito.verifyZeroInteractions(diff);
    }

    @Test
    public void read_from_DB_if_no_report_and_file_unchanged() {
        createDbScmInfoWithOneLine("hash");
        Mockito.when(sourceHashRepository.getRawSourceHash(ScmInfoRepositoryImplTest.FILE_SAME)).thenReturn("hash");
        // should clear revision and author
        ScmInfo scmInfo = underTest.getScmInfo(ScmInfoRepositoryImplTest.FILE_SAME).get();
        assertThat(scmInfo.getAllChangesets()).hasSize(1);
        assertChangeset(scmInfo.getChangesetForLine(1), null, null, 10L);
        Mockito.verify(sourceHashRepository).getRawSourceHash(ScmInfoRepositoryImplTest.FILE_SAME);
        Mockito.verify(dbLoader).getScmInfo(ScmInfoRepositoryImplTest.FILE_SAME);
        Mockito.verifyNoMoreInteractions(dbLoader);
        Mockito.verifyNoMoreInteractions(sourceHashRepository);
        Mockito.verifyZeroInteractions(diff);
    }

    @Test
    public void read_from_DB_if_no_report_and_file_unchanged_and_copyFromPrevious_is_true() {
        createDbScmInfoWithOneLine("hash");
        Mockito.when(sourceHashRepository.getRawSourceHash(ScmInfoRepositoryImplTest.FILE_SAME)).thenReturn("hash");
        addFileSourceInReport(1);
        addCopyFromPrevious();
        ScmInfo scmInfo = underTest.getScmInfo(ScmInfoRepositoryImplTest.FILE_SAME).get();
        assertThat(scmInfo.getAllChangesets()).hasSize(1);
        assertChangeset(scmInfo.getChangesetForLine(1), "rev1", "author1", 10L);
        Mockito.verify(sourceHashRepository).getRawSourceHash(ScmInfoRepositoryImplTest.FILE_SAME);
        Mockito.verify(dbLoader).getScmInfo(ScmInfoRepositoryImplTest.FILE_SAME);
        Mockito.verifyNoMoreInteractions(dbLoader);
        Mockito.verifyNoMoreInteractions(sourceHashRepository);
        Mockito.verifyZeroInteractions(diff);
    }

    @Test
    public void generate_scm_info_when_nothing_in_report_nor_db() {
        Mockito.when(dbLoader.getScmInfo(ScmInfoRepositoryImplTest.FILE)).thenReturn(Optional.empty());
        ScmInfo scmInfo = underTest.getScmInfo(ScmInfoRepositoryImplTest.FILE).get();
        assertThat(scmInfo.getAllChangesets()).hasSize(3);
        for (int i = 1; i <= 3; i++) {
            assertChangeset(scmInfo.getChangesetForLine(i), null, null, analysisDate.getTime());
        }
        Mockito.verify(dbLoader).getScmInfo(ScmInfoRepositoryImplTest.FILE);
        Mockito.verifyNoMoreInteractions(dbLoader);
        Mockito.verifyZeroInteractions(sourceHashRepository);
        Mockito.verifyZeroInteractions(diff);
    }

    @Test
    public void generate_scm_info_when_nothing_in_db_and_report_is_has_no_changesets() {
        Mockito.when(dbLoader.getScmInfo(ScmInfoRepositoryImplTest.FILE)).thenReturn(Optional.empty());
        addFileSourceInReport(3);
        ScmInfo scmInfo = underTest.getScmInfo(ScmInfoRepositoryImplTest.FILE).get();
        assertThat(scmInfo.getAllChangesets()).hasSize(3);
        for (int i = 1; i <= 3; i++) {
            assertChangeset(scmInfo.getChangesetForLine(i), null, null, analysisDate.getTime());
        }
        Mockito.verify(dbLoader).getScmInfo(ScmInfoRepositoryImplTest.FILE);
        Mockito.verifyNoMoreInteractions(dbLoader);
        Mockito.verifyZeroInteractions(sourceHashRepository);
        Mockito.verifyZeroInteractions(diff);
    }

    @Test
    public void generate_scm_info_for_new_and_changed_lines_when_report_is_empty() {
        createDbScmInfoWithOneLine("hash");
        Mockito.when(diff.computeMatchingLines(ScmInfoRepositoryImplTest.FILE)).thenReturn(new int[]{ 1, 0, 0 });
        addFileSourceInReport(3);
        ScmInfo scmInfo = underTest.getScmInfo(ScmInfoRepositoryImplTest.FILE).get();
        assertThat(scmInfo.getAllChangesets()).hasSize(3);
        assertChangeset(scmInfo.getChangesetForLine(1), null, null, 10L);
        assertChangeset(scmInfo.getChangesetForLine(2), null, null, analysisDate.getTime());
        assertChangeset(scmInfo.getChangesetForLine(3), null, null, analysisDate.getTime());
        Mockito.verify(dbLoader).getScmInfo(ScmInfoRepositoryImplTest.FILE);
        Mockito.verify(diff).computeMatchingLines(ScmInfoRepositoryImplTest.FILE);
        Mockito.verifyNoMoreInteractions(dbLoader);
        Mockito.verifyZeroInteractions(sourceHashRepository);
        Mockito.verifyNoMoreInteractions(diff);
    }

    @Test
    public void fail_with_NPE_when_component_is_null() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Component cannot be null");
        underTest.getScmInfo(null);
    }
}

