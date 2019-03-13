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
package org.sonar.ce.task.projectanalysis.batch;


import ScannerReport.AnalysisWarning;
import ScannerReport.Changesets;
import ScannerReport.Component;
import ScannerReport.CpdTextBlock;
import ScannerReport.Duplication;
import ScannerReport.Issue;
import ScannerReport.LineCoverage;
import ScannerReport.Measure;
import ScannerReport.Metadata;
import ScannerReport.Symbol;
import ScannerReport.SyntaxHighlightingRule;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.utils.internal.JUnitTempFolder;
import org.sonar.core.util.CloseableIterator;
import org.sonar.scanner.protocol.output.ScannerReport;
import org.sonar.scanner.protocol.output.ScannerReportWriter;


public class BatchReportReaderImplTest {
    private static final int COMPONENT_REF = 1;

    private static final Changesets CHANGESETS = Changesets.newBuilder().setComponentRef(BatchReportReaderImplTest.COMPONENT_REF).build();

    private static final Measure MEASURE = Measure.newBuilder().build();

    private static final Component COMPONENT = Component.newBuilder().setRef(BatchReportReaderImplTest.COMPONENT_REF).build();

    private static final Issue ISSUE = Issue.newBuilder().build();

    private static final Duplication DUPLICATION = Duplication.newBuilder().build();

    private static final CpdTextBlock DUPLICATION_BLOCK = CpdTextBlock.newBuilder().build();

    private static final Symbol SYMBOL = Symbol.newBuilder().build();

    private static final SyntaxHighlightingRule SYNTAX_HIGHLIGHTING_1 = SyntaxHighlightingRule.newBuilder().build();

    private static final SyntaxHighlightingRule SYNTAX_HIGHLIGHTING_2 = SyntaxHighlightingRule.newBuilder().build();

    private static final LineCoverage COVERAGE_1 = LineCoverage.newBuilder().build();

    private static final LineCoverage COVERAGE_2 = LineCoverage.newBuilder().build();

    @Rule
    public JUnitTempFolder tempFolder = new JUnitTempFolder();

    private ScannerReportWriter writer;

    private BatchReportReaderImpl underTest;

    @Test(expected = IllegalStateException.class)
    public void readMetadata_throws_ISE_if_no_metadata() {
        underTest.readMetadata();
    }

    @Test
    public void readMetadata_result_is_cached() {
        ScannerReport.Metadata metadata = Metadata.newBuilder().build();
        writer.writeMetadata(metadata);
        ScannerReport.Metadata res = underTest.readMetadata();
        assertThat(res).isEqualTo(metadata);
        assertThat(underTest.readMetadata()).isSameAs(res);
    }

    @Test
    public void readScannerLogs() throws IOException {
        File scannerLogFile = writer.getFileStructure().analysisLog();
        FileUtils.write(scannerLogFile, "log1\nlog2");
        CloseableIterator<String> logs = underTest.readScannerLogs();
        assertThat(logs).containsExactly("log1", "log2");
    }

    @Test
    public void readScannerLogs_no_logs() {
        CloseableIterator<String> logs = underTest.readScannerLogs();
        assertThat(logs.hasNext()).isFalse();
    }

    @Test
    public void readComponentMeasures_returns_empty_list_if_there_is_no_measure() {
        assertThat(underTest.readComponentMeasures(BatchReportReaderImplTest.COMPONENT_REF)).isEmpty();
    }

    @Test
    public void verify_readComponentMeasures_returns_measures() {
        writer.appendComponentMeasure(BatchReportReaderImplTest.COMPONENT_REF, BatchReportReaderImplTest.MEASURE);
        try (CloseableIterator<ScannerReport.Measure> measures = underTest.readComponentMeasures(BatchReportReaderImplTest.COMPONENT_REF)) {
            assertThat(measures.next()).isEqualTo(BatchReportReaderImplTest.MEASURE);
            assertThat(measures.hasNext()).isFalse();
        }
    }

    @Test
    public void readComponentMeasures_is_not_cached() {
        writer.appendComponentMeasure(BatchReportReaderImplTest.COMPONENT_REF, BatchReportReaderImplTest.MEASURE);
        assertThat(underTest.readComponentMeasures(BatchReportReaderImplTest.COMPONENT_REF)).isNotSameAs(underTest.readComponentMeasures(BatchReportReaderImplTest.COMPONENT_REF));
    }

    @Test
    public void readChangesets_returns_null_if_no_changeset() {
        assertThat(underTest.readChangesets(BatchReportReaderImplTest.COMPONENT_REF)).isNull();
    }

    @Test
    public void verify_readChangesets_returns_changesets() {
        writer.writeComponentChangesets(BatchReportReaderImplTest.CHANGESETS);
        ScannerReport.Changesets res = underTest.readChangesets(BatchReportReaderImplTest.COMPONENT_REF);
        assertThat(res).isEqualTo(BatchReportReaderImplTest.CHANGESETS);
    }

    @Test
    public void readChangesets_is_not_cached() {
        writer.writeComponentChangesets(BatchReportReaderImplTest.CHANGESETS);
        assertThat(underTest.readChangesets(BatchReportReaderImplTest.COMPONENT_REF)).isNotSameAs(underTest.readChangesets(BatchReportReaderImplTest.COMPONENT_REF));
    }

    @Test(expected = IllegalStateException.class)
    public void readComponent_throws_ISE_if_file_does_not_exist() {
        underTest.readComponent(BatchReportReaderImplTest.COMPONENT_REF);
    }

    @Test
    public void verify_readComponent_returns_Component() {
        writer.writeComponent(BatchReportReaderImplTest.COMPONENT);
        assertThat(underTest.readComponent(BatchReportReaderImplTest.COMPONENT_REF)).isEqualTo(BatchReportReaderImplTest.COMPONENT);
    }

    @Test
    public void readComponent_is_not_cached() {
        writer.writeComponent(BatchReportReaderImplTest.COMPONENT);
        assertThat(underTest.readComponent(BatchReportReaderImplTest.COMPONENT_REF)).isNotSameAs(underTest.readComponent(BatchReportReaderImplTest.COMPONENT_REF));
    }

    @Test
    public void readComponentIssues_returns_empty_list_if_file_does_not_exist() {
        assertThat(underTest.readComponentIssues(BatchReportReaderImplTest.COMPONENT_REF)).isEmpty();
    }

    @Test
    public void verify_readComponentIssues_returns_Issues() {
        writer.writeComponentIssues(BatchReportReaderImplTest.COMPONENT_REF, ImmutableList.of(BatchReportReaderImplTest.ISSUE));
        try (CloseableIterator<ScannerReport.Issue> res = underTest.readComponentIssues(BatchReportReaderImplTest.COMPONENT_REF)) {
            assertThat(res.next()).isEqualTo(BatchReportReaderImplTest.ISSUE);
            assertThat(res.hasNext()).isFalse();
        }
    }

    @Test
    public void readComponentIssues_it_not_cached() {
        writer.writeComponentIssues(BatchReportReaderImplTest.COMPONENT_REF, ImmutableList.of(BatchReportReaderImplTest.ISSUE));
        assertThat(underTest.readComponentIssues(BatchReportReaderImplTest.COMPONENT_REF)).isNotSameAs(underTest.readComponentIssues(BatchReportReaderImplTest.COMPONENT_REF));
    }

    @Test
    public void readComponentDuplications_returns_empty_list_if_file_does_not_exist() {
        assertThat(underTest.readComponentDuplications(BatchReportReaderImplTest.COMPONENT_REF)).isEmpty();
    }

    @Test
    public void verify_readComponentDuplications_returns_Issues() {
        writer.writeComponentDuplications(BatchReportReaderImplTest.COMPONENT_REF, ImmutableList.of(BatchReportReaderImplTest.DUPLICATION));
        try (CloseableIterator<ScannerReport.Duplication> res = underTest.readComponentDuplications(BatchReportReaderImplTest.COMPONENT_REF)) {
            assertThat(res.next()).isEqualTo(BatchReportReaderImplTest.DUPLICATION);
            assertThat(res.hasNext()).isFalse();
        }
    }

    @Test
    public void readComponentDuplications_it_not_cached() {
        writer.writeComponentDuplications(BatchReportReaderImplTest.COMPONENT_REF, ImmutableList.of(BatchReportReaderImplTest.DUPLICATION));
        assertThat(underTest.readComponentDuplications(BatchReportReaderImplTest.COMPONENT_REF)).isNotSameAs(underTest.readComponentDuplications(BatchReportReaderImplTest.COMPONENT_REF));
    }

    @Test
    public void readComponentDuplicationBlocks_returns_empty_list_if_file_does_not_exist() {
        assertThat(underTest.readCpdTextBlocks(BatchReportReaderImplTest.COMPONENT_REF)).isEmpty();
    }

    @Test
    public void verify_readComponentDuplicationBlocks_returns_Issues() {
        writer.writeCpdTextBlocks(BatchReportReaderImplTest.COMPONENT_REF, ImmutableList.of(BatchReportReaderImplTest.DUPLICATION_BLOCK));
        try (CloseableIterator<ScannerReport.CpdTextBlock> res = underTest.readCpdTextBlocks(BatchReportReaderImplTest.COMPONENT_REF)) {
            assertThat(res.next()).isEqualTo(BatchReportReaderImplTest.DUPLICATION_BLOCK);
            assertThat(res.hasNext()).isFalse();
        }
    }

    @Test
    public void readComponentDuplicationBlocks_is_not_cached() {
        writer.writeCpdTextBlocks(BatchReportReaderImplTest.COMPONENT_REF, ImmutableList.of(BatchReportReaderImplTest.DUPLICATION_BLOCK));
        assertThat(underTest.readCpdTextBlocks(BatchReportReaderImplTest.COMPONENT_REF)).isNotSameAs(underTest.readCpdTextBlocks(BatchReportReaderImplTest.COMPONENT_REF));
    }

    @Test
    public void readComponentSymbols_returns_empty_list_if_file_does_not_exist() {
        assertThat(underTest.readComponentSymbols(BatchReportReaderImplTest.COMPONENT_REF)).isEmpty();
    }

    @Test
    public void verify_readComponentSymbols_returns_Issues() {
        writer.writeComponentSymbols(BatchReportReaderImplTest.COMPONENT_REF, ImmutableList.of(BatchReportReaderImplTest.SYMBOL));
        try (CloseableIterator<ScannerReport.Symbol> res = underTest.readComponentSymbols(BatchReportReaderImplTest.COMPONENT_REF)) {
            assertThat(res.next()).isEqualTo(BatchReportReaderImplTest.SYMBOL);
            assertThat(res.hasNext()).isFalse();
        }
    }

    @Test
    public void readComponentSymbols_it_not_cached() {
        writer.writeComponentSymbols(BatchReportReaderImplTest.COMPONENT_REF, ImmutableList.of(BatchReportReaderImplTest.SYMBOL));
        assertThat(underTest.readComponentSymbols(BatchReportReaderImplTest.COMPONENT_REF)).isNotSameAs(underTest.readComponentSymbols(BatchReportReaderImplTest.COMPONENT_REF));
    }

    @Test
    public void readComponentSyntaxHighlighting_returns_empty_CloseableIterator_when_file_does_not_exist() {
        assertThat(underTest.readComponentSyntaxHighlighting(BatchReportReaderImplTest.COMPONENT_REF)).isEmpty();
    }

    @Test
    public void verify_readComponentSyntaxHighlighting() {
        writer.writeComponentSyntaxHighlighting(BatchReportReaderImplTest.COMPONENT_REF, ImmutableList.of(BatchReportReaderImplTest.SYNTAX_HIGHLIGHTING_1, BatchReportReaderImplTest.SYNTAX_HIGHLIGHTING_2));
        CloseableIterator<ScannerReport.SyntaxHighlightingRule> res = underTest.readComponentSyntaxHighlighting(BatchReportReaderImplTest.COMPONENT_REF);
        assertThat(res).containsExactly(BatchReportReaderImplTest.SYNTAX_HIGHLIGHTING_1, BatchReportReaderImplTest.SYNTAX_HIGHLIGHTING_2);
        res.close();
    }

    @Test
    public void readComponentCoverage_returns_empty_CloseableIterator_when_file_does_not_exist() {
        assertThat(underTest.readComponentCoverage(BatchReportReaderImplTest.COMPONENT_REF)).isEmpty();
    }

    @Test
    public void verify_readComponentCoverage() {
        writer.writeComponentCoverage(BatchReportReaderImplTest.COMPONENT_REF, ImmutableList.of(BatchReportReaderImplTest.COVERAGE_1, BatchReportReaderImplTest.COVERAGE_2));
        CloseableIterator<ScannerReport.LineCoverage> res = underTest.readComponentCoverage(BatchReportReaderImplTest.COMPONENT_REF);
        assertThat(res).containsExactly(BatchReportReaderImplTest.COVERAGE_1, BatchReportReaderImplTest.COVERAGE_2);
        res.close();
    }

    @Test
    public void readFileSource_returns_absent_optional_when_file_does_not_exist() {
        assertThat(underTest.readFileSource(BatchReportReaderImplTest.COMPONENT_REF)).isEmpty();
    }

    @Test
    public void verify_readFileSource() throws IOException {
        File file = writer.getSourceFile(BatchReportReaderImplTest.COMPONENT_REF);
        FileUtils.writeLines(file, ImmutableList.of("1", "2", "3"));
        CloseableIterator<String> res = underTest.readFileSource(BatchReportReaderImplTest.COMPONENT_REF).get();
        assertThat(res).containsExactly("1", "2", "3");
        res.close();
    }

    @Test
    public void verify_readAnalysisWarnings() {
        ScannerReport.AnalysisWarning warning1 = AnalysisWarning.newBuilder().setText("warning 1").build();
        ScannerReport.AnalysisWarning warning2 = AnalysisWarning.newBuilder().setText("warning 2").build();
        ImmutableList<ScannerReport.AnalysisWarning> warnings = ImmutableList.of(warning1, warning2);
        writer.writeAnalysisWarnings(warnings);
        CloseableIterator<ScannerReport.AnalysisWarning> res = underTest.readAnalysisWarnings();
        assertThat(res).containsExactlyElementsOf(warnings);
        res.close();
    }
}

