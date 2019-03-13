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
package org.sonar.scanner.protocol.output;


import FileStructure.Domain.SOURCE;
import HighlightingType.ANNOTATION;
import ScannerReport.Changesets.Changeset;
import ScannerReport.CpdTextBlock;
import ScannerReport.Duplicate;
import ScannerReport.Duplication;
import ScannerReport.ExternalIssue;
import ScannerReport.Issue;
import ScannerReport.LineCoverage;
import ScannerReport.Metadata.Builder;
import ScannerReport.Symbol;
import ScannerReport.SyntaxHighlightingRule;
import ScannerReport.TextRange;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonar.core.util.CloseableIterator;
import org.sonar.scanner.protocol.output.ScannerReport.Measure.StringValue;


public class ScannerReportReaderTest {
    private static int UNKNOWN_COMPONENT_REF = 123;

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private File dir;

    private ScannerReportReader underTest;

    @Test
    public void read_metadata() {
        ScannerReportWriter writer = new ScannerReportWriter(dir);
        ScannerReport.Metadata.Builder metadata = ScannerReport.Metadata.newBuilder().setAnalysisDate(15000000L).setProjectKey("PROJECT_A").setRootComponentRef(1).setCrossProjectDuplicationActivated(true);
        writer.writeMetadata(metadata.build());
        ScannerReport.Metadata readMetadata = underTest.readMetadata();
        assertThat(readMetadata.getAnalysisDate()).isEqualTo(15000000L);
        assertThat(readMetadata.getProjectKey()).isEqualTo("PROJECT_A");
        assertThat(readMetadata.getRootComponentRef()).isEqualTo(1);
        assertThat(readMetadata.getCrossProjectDuplicationActivated()).isTrue();
    }

    @Test(expected = IllegalStateException.class)
    public void fail_if_missing_metadata_file() {
        underTest.readMetadata();
    }

    @Test
    public void read_components() {
        ScannerReportWriter writer = new ScannerReportWriter(dir);
        ScannerReport.Component.Builder component = ScannerReport.Component.newBuilder().setRef(1).setProjectRelativePath("src/main/java/Foo.java");
        writer.writeComponent(component.build());
        assertThat(underTest.readComponent(1).getProjectRelativePath()).isEqualTo("src/main/java/Foo.java");
    }

    @Test(expected = IllegalStateException.class)
    public void fail_if_missing_file_on_component() {
        underTest.readComponent(ScannerReportReaderTest.UNKNOWN_COMPONENT_REF);
    }

    @Test
    public void read_issues() {
        ScannerReportWriter writer = new ScannerReportWriter(dir);
        ScannerReport.Issue issue = Issue.newBuilder().build();
        writer.writeComponentIssues(1, Arrays.asList(issue));
        assertThat(underTest.readComponentIssues(1)).hasSize(1);
        assertThat(underTest.readComponentIssues(200)).isEmpty();
    }

    @Test
    public void read_external_issues() {
        ScannerReportWriter writer = new ScannerReportWriter(dir);
        ScannerReport.ExternalIssue issue = ExternalIssue.newBuilder().build();
        writer.appendComponentExternalIssue(1, issue);
        assertThat(underTest.readComponentExternalIssues(1)).hasSize(1);
        assertThat(underTest.readComponentExternalIssues(200)).isEmpty();
    }

    @Test
    public void empty_list_if_no_issue_found() {
        assertThat(underTest.readComponentIssues(ScannerReportReaderTest.UNKNOWN_COMPONENT_REF)).isEmpty();
    }

    @Test
    public void read_measures() {
        ScannerReportWriter writer = new ScannerReportWriter(dir);
        ScannerReport.Measure.Builder measure = ScannerReport.Measure.newBuilder().setStringValue(StringValue.newBuilder().setValue("value_a"));
        writer.appendComponentMeasure(1, measure.build());
        assertThat(underTest.readComponentMeasures(1)).hasSize(1);
    }

    @Test
    public void empty_list_if_no_measure_found() {
        assertThat(underTest.readComponentMeasures(ScannerReportReaderTest.UNKNOWN_COMPONENT_REF)).isEmpty();
    }

    @Test
    public void read_changesets() {
        ScannerReportWriter writer = new ScannerReportWriter(dir);
        ScannerReport.Changesets.Builder scm = ScannerReport.Changesets.newBuilder().setComponentRef(1).addChangeset(Changeset.newBuilder().setDate(123456789).setAuthor("jack.daniels").setRevision("123-456-789"));
        writer.writeComponentChangesets(scm.build());
        assertThat(underTest.readChangesets(1).getChangesetList()).hasSize(1);
        assertThat(underTest.readChangesets(1).getChangeset(0).getDate()).isEqualTo(123456789L);
    }

    @Test
    public void null_if_no_changeset_found() {
        assertThat(underTest.readChangesets(ScannerReportReaderTest.UNKNOWN_COMPONENT_REF)).isNull();
    }

    @Test
    public void read_duplications() {
        ScannerReportWriter writer = new ScannerReportWriter(dir);
        writer.writeMetadata(ScannerReport.Metadata.newBuilder().setRootComponentRef(1).build());
        writer.writeComponent(ScannerReport.Component.newBuilder().setRef(1).build());
        ScannerReport.Duplication duplication = Duplication.newBuilder().setOriginPosition(TextRange.newBuilder().setStartLine(1).setEndLine(5).build()).addDuplicate(Duplicate.newBuilder().setOtherFileRef(2).setRange(TextRange.newBuilder().setStartLine(6).setEndLine(10).build()).build()).build();
        writer.writeComponentDuplications(1, Arrays.asList(duplication));
        ScannerReportReader sut = new ScannerReportReader(dir);
        assertThat(sut.readComponentDuplications(1)).hasSize(1);
    }

    @Test
    public void empty_list_if_no_duplication_found() {
        assertThat(underTest.readComponentDuplications(ScannerReportReaderTest.UNKNOWN_COMPONENT_REF)).isEmpty();
    }

    @Test
    public void read_duplication_blocks() {
        ScannerReportWriter writer = new ScannerReportWriter(dir);
        writer.writeMetadata(ScannerReport.Metadata.newBuilder().setRootComponentRef(1).build());
        writer.writeComponent(ScannerReport.Component.newBuilder().setRef(1).build());
        ScannerReport.CpdTextBlock duplicationBlock = CpdTextBlock.newBuilder().setHash("abcdefghijklmnop").setStartLine(1).setEndLine(2).setStartTokenIndex(10).setEndTokenIndex(15).build();
        writer.writeCpdTextBlocks(1, Collections.singletonList(duplicationBlock));
        ScannerReportReader sut = new ScannerReportReader(dir);
        assertThat(sut.readCpdTextBlocks(1)).hasSize(1);
    }

    @Test
    public void empty_list_if_no_duplication_block_found() {
        assertThat(underTest.readComponentDuplications(ScannerReportReaderTest.UNKNOWN_COMPONENT_REF)).isEmpty();
    }

    @Test
    public void read_syntax_highlighting() {
        ScannerReportWriter writer = new ScannerReportWriter(dir);
        writer.writeMetadata(ScannerReport.Metadata.newBuilder().setRootComponentRef(1).build());
        writer.writeComponent(ScannerReport.Component.newBuilder().setRef(1).build());
        writer.writeComponentSyntaxHighlighting(1, Arrays.asList(SyntaxHighlightingRule.newBuilder().setRange(TextRange.newBuilder().setStartLine(1).setEndLine(10).build()).setType(ANNOTATION).build()));
        try (CloseableIterator<ScannerReport.SyntaxHighlightingRule> it = underTest.readComponentSyntaxHighlighting(1)) {
            ScannerReport.SyntaxHighlightingRule syntaxHighlighting = it.next();
            assertThat(syntaxHighlighting.getRange()).isNotNull();
            assertThat(syntaxHighlighting.getRange().getStartLine()).isEqualTo(1);
            assertThat(syntaxHighlighting.getRange().getEndLine()).isEqualTo(10);
            assertThat(syntaxHighlighting.getType()).isEqualTo(ANNOTATION);
        }
    }

    @Test
    public void return_empty_if_no_highlighting_found() {
        assertThat(underTest.readComponentSyntaxHighlighting(ScannerReportReaderTest.UNKNOWN_COMPONENT_REF)).isEmpty();
    }

    @Test
    public void read_symbols() {
        ScannerReportWriter writer = new ScannerReportWriter(dir);
        writer.writeMetadata(ScannerReport.Metadata.newBuilder().setRootComponentRef(1).build());
        writer.writeComponent(ScannerReport.Component.newBuilder().setRef(1).build());
        writer.writeComponentSymbols(1, Arrays.asList(Symbol.newBuilder().setDeclaration(TextRange.newBuilder().setStartLine(1).setStartOffset(3).setEndLine(1).setEndOffset(5).build()).addReference(TextRange.newBuilder().setStartLine(10).setStartOffset(15).setEndLine(11).setEndOffset(2).build()).build()));
        underTest = new ScannerReportReader(dir);
        assertThat(underTest.readComponentSymbols(1)).hasSize(1);
    }

    @Test
    public void empty_list_if_no_symbol_found() {
        assertThat(underTest.readComponentSymbols(ScannerReportReaderTest.UNKNOWN_COMPONENT_REF)).isEmpty();
    }

    @Test
    public void read_coverage() {
        ScannerReportWriter writer = new ScannerReportWriter(dir);
        writer.writeMetadata(ScannerReport.Metadata.newBuilder().setRootComponentRef(1).build());
        writer.writeComponent(ScannerReport.Component.newBuilder().setRef(1).build());
        writer.writeComponentCoverage(1, Arrays.asList(LineCoverage.newBuilder().setLine(1).setConditions(1).setHits(true).setCoveredConditions(1).build(), LineCoverage.newBuilder().setLine(2).setConditions(5).setHits(false).setCoveredConditions(4).build()));
        underTest = new ScannerReportReader(dir);
        try (CloseableIterator<ScannerReport.LineCoverage> it = new ScannerReportReader(dir).readComponentCoverage(1)) {
            ScannerReport.LineCoverage coverage = it.next();
            assertThat(coverage.getLine()).isEqualTo(1);
            assertThat(coverage.getConditions()).isEqualTo(1);
            assertThat(coverage.getHits()).isTrue();
            assertThat(coverage.getCoveredConditions()).isEqualTo(1);
        }
    }

    @Test
    public void return_empty_iterator_if_no_coverage_found() {
        assertThat(underTest.readComponentCoverage(ScannerReportReaderTest.UNKNOWN_COMPONENT_REF)).isEmpty();
    }

    @Test
    public void read_source_lines() throws Exception {
        ScannerReportWriter writer = new ScannerReportWriter(dir);
        File file = writer.getFileStructure().fileFor(SOURCE, 1);
        FileUtils.writeLines(file, Lists.newArrayList("line1", "line2"));
        File sourceFile = new ScannerReportReader(dir).readFileSource(1);
        assertThat(sourceFile).isEqualTo(file);
    }

    @Test
    public void read_file_source() throws Exception {
        ScannerReportWriter writer = new ScannerReportWriter(dir);
        try (FileOutputStream outputStream = new FileOutputStream(writer.getSourceFile(1))) {
            IOUtils.write("line1\nline2", outputStream);
        }
        try (InputStream inputStream = FileUtils.openInputStream(underTest.readFileSource(1))) {
            assertThat(IOUtils.readLines(inputStream)).containsOnly("line1", "line2");
        }
    }

    @Test
    public void return_null_when_no_file_source() {
        assertThat(underTest.readFileSource(ScannerReportReaderTest.UNKNOWN_COMPONENT_REF)).isNull();
    }
}

