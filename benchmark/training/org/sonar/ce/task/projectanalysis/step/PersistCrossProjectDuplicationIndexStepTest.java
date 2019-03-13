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


import Component.Status.SAME;
import Component.Type.FILE;
import Component.Type.PROJECT;
import ScannerReport.CpdTextBlock;
import System2.INSTANCE;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.analysis.Analysis;
import org.sonar.ce.task.projectanalysis.analysis.AnalysisMetadataHolderRule;
import org.sonar.ce.task.projectanalysis.batch.BatchReportReaderRule;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolderRule;
import org.sonar.ce.task.projectanalysis.duplication.CrossProjectDuplicationStatusHolder;
import org.sonar.ce.task.step.ComputationStep;
import org.sonar.ce.task.step.TestComputationStepContext;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;


public class PersistCrossProjectDuplicationIndexStepTest {
    private static final int FILE_1_REF = 2;

    private static final int FILE_2_REF = 3;

    private static final String FILE_2_UUID = "file2";

    private static final Component FILE_1 = ReportComponent.builder(FILE, PersistCrossProjectDuplicationIndexStepTest.FILE_1_REF).build();

    private static final Component FILE_2 = ReportComponent.builder(FILE, PersistCrossProjectDuplicationIndexStepTest.FILE_2_REF).setStatus(SAME).setUuid(PersistCrossProjectDuplicationIndexStepTest.FILE_2_UUID).build();

    private static final Component PROJECT = ReportComponent.builder(Component.Type.PROJECT, 1).addChildren(PersistCrossProjectDuplicationIndexStepTest.FILE_1).addChildren(PersistCrossProjectDuplicationIndexStepTest.FILE_2).build();

    private static final CpdTextBlock CPD_TEXT_BLOCK = CpdTextBlock.newBuilder().setHash("a8998353e96320ec").setStartLine(30).setEndLine(45).build();

    private static final String ANALYSIS_UUID = "analysis uuid";

    private static final String BASE_ANALYSIS_UUID = "base analysis uuid";

    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    @Rule
    public BatchReportReaderRule reportReader = new BatchReportReaderRule();

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule().setRoot(PersistCrossProjectDuplicationIndexStepTest.PROJECT);

    @Rule
    public AnalysisMetadataHolderRule analysisMetadataHolder = new AnalysisMetadataHolderRule();

    private CrossProjectDuplicationStatusHolder crossProjectDuplicationStatusHolder = Mockito.mock(CrossProjectDuplicationStatusHolder.class);

    private Analysis baseAnalysis = Mockito.mock(Analysis.class);

    private DbClient dbClient = dbTester.getDbClient();

    private ComputationStep underTest;

    @Test
    public void persist_cpd_text_block() {
        Mockito.when(crossProjectDuplicationStatusHolder.isEnabled()).thenReturn(true);
        reportReader.putDuplicationBlocks(PersistCrossProjectDuplicationIndexStepTest.FILE_1_REF, Collections.singletonList(PersistCrossProjectDuplicationIndexStepTest.CPD_TEXT_BLOCK));
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        Map<String, Object> dto = dbTester.selectFirst("select HASH, START_LINE, END_LINE, INDEX_IN_FILE, COMPONENT_UUID, ANALYSIS_UUID from duplications_index");
        assertThat(dto.get("HASH")).isEqualTo(PersistCrossProjectDuplicationIndexStepTest.CPD_TEXT_BLOCK.getHash());
        assertThat(dto.get("START_LINE")).isEqualTo(30L);
        assertThat(dto.get("END_LINE")).isEqualTo(45L);
        assertThat(dto.get("INDEX_IN_FILE")).isEqualTo(0L);
        assertThat(dto.get("COMPONENT_UUID")).isEqualTo(PersistCrossProjectDuplicationIndexStepTest.FILE_1.getUuid());
        assertThat(dto.get("ANALYSIS_UUID")).isEqualTo(PersistCrossProjectDuplicationIndexStepTest.ANALYSIS_UUID);
        context.getStatistics().assertValue("inserts", 1);
    }

    @Test
    public void persist_many_cpd_text_blocks() {
        Mockito.when(crossProjectDuplicationStatusHolder.isEnabled()).thenReturn(true);
        reportReader.putDuplicationBlocks(PersistCrossProjectDuplicationIndexStepTest.FILE_1_REF, Arrays.asList(PersistCrossProjectDuplicationIndexStepTest.CPD_TEXT_BLOCK, CpdTextBlock.newBuilder().setHash("b1234353e96320ff").setStartLine(20).setEndLine(15).build()));
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        List<Map<String, Object>> dtos = dbTester.select("select HASH, START_LINE, END_LINE, INDEX_IN_FILE, COMPONENT_UUID, ANALYSIS_UUID from duplications_index");
        assertThat(dtos).extracting("HASH").containsOnly(PersistCrossProjectDuplicationIndexStepTest.CPD_TEXT_BLOCK.getHash(), "b1234353e96320ff");
        assertThat(dtos).extracting("START_LINE").containsOnly(30L, 20L);
        assertThat(dtos).extracting("END_LINE").containsOnly(45L, 15L);
        assertThat(dtos).extracting("INDEX_IN_FILE").containsOnly(0L, 1L);
        assertThat(dtos).extracting("COMPONENT_UUID").containsOnly(PersistCrossProjectDuplicationIndexStepTest.FILE_1.getUuid());
        assertThat(dtos).extracting("ANALYSIS_UUID").containsOnly(PersistCrossProjectDuplicationIndexStepTest.ANALYSIS_UUID);
        context.getStatistics().assertValue("inserts", 2);
    }

    @Test
    public void nothing_to_persist_when_no_cpd_text_blocks_in_report() {
        Mockito.when(crossProjectDuplicationStatusHolder.isEnabled()).thenReturn(true);
        reportReader.putDuplicationBlocks(PersistCrossProjectDuplicationIndexStepTest.FILE_1_REF, Collections.emptyList());
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        assertThat(dbTester.countRowsOfTable("duplications_index")).isEqualTo(0);
        context.getStatistics().assertValue("inserts", 0);
    }

    @Test
    public void nothing_to_do_when_cross_project_duplication_is_disabled() {
        Mockito.when(crossProjectDuplicationStatusHolder.isEnabled()).thenReturn(false);
        reportReader.putDuplicationBlocks(PersistCrossProjectDuplicationIndexStepTest.FILE_1_REF, Collections.singletonList(PersistCrossProjectDuplicationIndexStepTest.CPD_TEXT_BLOCK));
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        assertThat(dbTester.countRowsOfTable("duplications_index")).isEqualTo(0);
        context.getStatistics().assertValue("inserts", null);
    }
}

