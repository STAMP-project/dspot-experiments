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


import System2.INSTANCE;
import java.util.Arrays;
import java.util.Collections;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.ce.task.CeTask;
import org.sonar.ce.task.projectanalysis.analysis.AnalysisMetadataHolderRule;
import org.sonar.ce.task.projectanalysis.batch.BatchReportReaderRule;
import org.sonar.ce.task.step.TestComputationStepContext;
import org.sonar.core.util.CloseableIterator;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;


public class PersistScannerContextStepTest {
    private static final String ANALYSIS_UUID = "UUID";

    @ClassRule
    public static final DbTester dbTester = DbTester.create(INSTANCE);

    @Rule
    public BatchReportReaderRule reportReader = new BatchReportReaderRule();

    @Rule
    public AnalysisMetadataHolderRule analysisMetadataHolder = new AnalysisMetadataHolderRule().setUuid(PersistScannerContextStepTest.ANALYSIS_UUID);

    private DbClient dbClient = PersistScannerContextStepTest.dbTester.getDbClient();

    private CeTask ceTask = Mockito.mock(CeTask.class);

    private PersistScannerContextStep underTest = new PersistScannerContextStep(reportReader, dbClient, ceTask);

    @Test
    public void getDescription() {
        assertThat(underTest.getDescription()).isEqualTo("Persist scanner context");
    }

    @Test
    public void executes_persist_lines_of_reportReader() {
        String taskUuid = "task uuid";
        Mockito.when(ceTask.getUuid()).thenReturn(taskUuid);
        reportReader.setScannerLogs(Arrays.asList("log1", "log2"));
        underTest.execute(new TestComputationStepContext());
        assertThat(dbClient.ceScannerContextDao().selectScannerContext(PersistScannerContextStepTest.dbTester.getSession(), taskUuid)).contains((("log1" + '\n') + "log2"));
    }

    @Test
    public void executes_persist_does_not_persist_any_scanner_context_if_iterator_is_empty() {
        reportReader.setScannerLogs(Collections.emptyList());
        underTest.execute(new TestComputationStepContext());
        assertThat(dbClient.ceScannerContextDao().selectScannerContext(PersistScannerContextStepTest.dbTester.getSession(), PersistScannerContextStepTest.ANALYSIS_UUID)).isEmpty();
    }

    /**
     * SONAR-8306
     */
    @Test
    public void execute_does_not_fail_if_scanner_context_has_already_been_persisted() {
        dbClient.ceScannerContextDao().insert(PersistScannerContextStepTest.dbTester.getSession(), PersistScannerContextStepTest.ANALYSIS_UUID, CloseableIterator.from(Arrays.asList("a", "b", "c").iterator()));
        PersistScannerContextStepTest.dbTester.commit();
        reportReader.setScannerLogs(Arrays.asList("1", "2", "3"));
        Mockito.when(ceTask.getUuid()).thenReturn(PersistScannerContextStepTest.ANALYSIS_UUID);
        underTest.execute(new TestComputationStepContext());
        assertThat(dbClient.ceScannerContextDao().selectScannerContext(PersistScannerContextStepTest.dbTester.getSession(), PersistScannerContextStepTest.ANALYSIS_UUID)).contains((((("1" + '\n') + "2") + '\n') + "3"));
    }
}

