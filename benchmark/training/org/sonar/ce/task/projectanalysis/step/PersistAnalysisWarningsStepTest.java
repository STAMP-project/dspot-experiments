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


import CeTaskMessages.Message;
import PersistAnalysisWarningsStep.DESCRIPTION;
import ScannerReport.AnalysisWarning;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.ce.task.log.CeTaskMessages;
import org.sonar.ce.task.projectanalysis.batch.BatchReportReaderRule;
import org.sonar.ce.task.step.TestComputationStepContext;
import org.sonar.scanner.protocol.output.ScannerReport;


public class PersistAnalysisWarningsStepTest {
    @Rule
    public BatchReportReaderRule reportReader = new BatchReportReaderRule();

    private final CeTaskMessages ceTaskMessages = Mockito.mock(CeTaskMessages.class);

    private final PersistAnalysisWarningsStep underTest = new PersistAnalysisWarningsStep(reportReader, ceTaskMessages);

    @Test
    public void getDescription() {
        assertThat(underTest.getDescription()).isEqualTo(DESCRIPTION);
    }

    @Test
    public void execute_persists_warnings_from_reportReader() {
        ScannerReport.AnalysisWarning warning1 = AnalysisWarning.newBuilder().setText("warning 1").build();
        ScannerReport.AnalysisWarning warning2 = AnalysisWarning.newBuilder().setText("warning 2").build();
        ImmutableList<ScannerReport.AnalysisWarning> warnings = ImmutableList.of(warning1, warning2);
        reportReader.setAnalysisWarnings(warnings);
        underTest.execute(new TestComputationStepContext());
        List<CeTaskMessages.Message> messages = warnings.stream().map(( w) -> new CeTaskMessages.Message(w.getText(), w.getTimestamp())).collect(Collectors.toList());
        Mockito.verify(ceTaskMessages).addAll(messages);
    }

    @Test
    public void execute_does_not_persist_warnings_from_reportReader_when_empty() {
        reportReader.setScannerLogs(Collections.emptyList());
        underTest.execute(new TestComputationStepContext());
        Mockito.verifyZeroInteractions(ceTaskMessages);
    }
}

