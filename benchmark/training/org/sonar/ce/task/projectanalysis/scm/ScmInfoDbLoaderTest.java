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
import System2.INSTANCE;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.utils.log.LogTester;
import org.sonar.ce.task.projectanalysis.analysis.Analysis;
import org.sonar.ce.task.projectanalysis.analysis.AnalysisMetadataHolderRule;
import org.sonar.ce.task.projectanalysis.analysis.Branch;
import org.sonar.ce.task.projectanalysis.batch.BatchReportReaderRule;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.component.MergeBranchComponentUuids;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.db.DbTester;


public class ScmInfoDbLoaderTest {
    static final int FILE_REF = 1;

    static final Component FILE = ReportComponent.builder(Component.Type.FILE, ScmInfoDbLoaderTest.FILE_REF).setKey("FILE_KEY").setUuid("FILE_UUID").build();

    static final long DATE_1 = 123456789L;

    static final long DATE_2 = 1234567810L;

    static Analysis baseProjectAnalysis = new Analysis.Builder().setId(1).setUuid("uuid_1").setCreatedAt(123456789L).build();

    @Rule
    public LogTester logTester = new LogTester();

    @Rule
    public AnalysisMetadataHolderRule analysisMetadataHolder = new AnalysisMetadataHolderRule();

    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    @Rule
    public BatchReportReaderRule reportReader = new BatchReportReaderRule();

    private Branch branch = Mockito.mock(Branch.class);

    private MergeBranchComponentUuids mergeBranchComponentUuids = Mockito.mock(MergeBranchComponentUuids.class);

    private ScmInfoDbLoader underTest = new ScmInfoDbLoader(analysisMetadataHolder, dbTester.getDbClient(), mergeBranchComponentUuids);

    @Test
    public void returns_ScmInfo_from_DB() {
        analysisMetadataHolder.setBaseAnalysis(ScmInfoDbLoaderTest.baseProjectAnalysis);
        analysisMetadataHolder.setBranch(null);
        String hash = ScmInfoDbLoaderTest.computeSourceHash(1);
        addFileSourceInDb("henry", ScmInfoDbLoaderTest.DATE_1, "rev-1", hash);
        DbScmInfo scmInfo = underTest.getScmInfo(ScmInfoDbLoaderTest.FILE).get();
        assertThat(scmInfo.getAllChangesets()).hasSize(1);
        assertThat(scmInfo.fileHash()).isEqualTo(hash);
        assertThat(logTester.logs(TRACE)).containsOnly("Reading SCM info from DB for file 'FILE_UUID'");
    }

    @Test
    public void read_from_merge_branch_if_no_base() {
        analysisMetadataHolder.setBaseAnalysis(null);
        analysisMetadataHolder.setBranch(branch);
        String mergeFileUuid = "mergeFileUuid";
        String hash = ScmInfoDbLoaderTest.computeSourceHash(1);
        Mockito.when(branch.getMergeBranchUuid()).thenReturn(Optional.of("mergeBranchUuid"));
        Mockito.when(mergeBranchComponentUuids.getUuid(ScmInfoDbLoaderTest.FILE.getDbKey())).thenReturn(mergeFileUuid);
        addFileSourceInDb("henry", ScmInfoDbLoaderTest.DATE_1, "rev-1", hash, mergeFileUuid);
        DbScmInfo scmInfo = underTest.getScmInfo(ScmInfoDbLoaderTest.FILE).get();
        assertThat(scmInfo.getAllChangesets()).hasSize(1);
        assertThat(scmInfo.fileHash()).isEqualTo(hash);
        assertThat(logTester.logs(TRACE)).containsOnly("Reading SCM info from DB for file 'mergeFileUuid'");
    }

    @Test
    public void return_empty_if_no_dto_available() {
        analysisMetadataHolder.setBaseAnalysis(ScmInfoDbLoaderTest.baseProjectAnalysis);
        analysisMetadataHolder.setBranch(null);
        Optional<DbScmInfo> scmInfo = underTest.getScmInfo(ScmInfoDbLoaderTest.FILE);
        assertThat(logTester.logs(TRACE)).containsOnly("Reading SCM info from DB for file 'FILE_UUID'");
        assertThat(scmInfo).isEmpty();
    }

    @Test
    public void do_not_read_from_db_on_first_analysis_and_no_merge_branch() {
        Branch branch = Mockito.mock(Branch.class);
        Mockito.when(branch.getMergeBranchUuid()).thenReturn(Optional.empty());
        analysisMetadataHolder.setBaseAnalysis(null);
        analysisMetadataHolder.setBranch(branch);
        assertThat(underTest.getScmInfo(ScmInfoDbLoaderTest.FILE)).isEmpty();
        assertThat(logTester.logs(TRACE)).isEmpty();
    }
}

