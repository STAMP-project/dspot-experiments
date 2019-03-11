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


import ComponentType.FILE;
import Constants.Severity.BLOCKER;
import FileStructure.Domain.CHANGED_LINES;
import FileStructure.Domain.CHANGESETS;
import FileStructure.Domain.COMPONENT;
import FileStructure.Domain.COVERAGES;
import FileStructure.Domain.CPD_TEXT_BLOCKS;
import FileStructure.Domain.DUPLICATIONS;
import FileStructure.Domain.EXTERNAL_ISSUES;
import FileStructure.Domain.ISSUES;
import FileStructure.Domain.MEASURES;
import FileStructure.Domain.SGNIFICANT_CODE;
import FileStructure.Domain.SYMBOLS;
import FileStructure.Domain.SYNTAX_HIGHLIGHTINGS;
import HighlightingType.ANNOTATION;
import ScannerReport.AdHocRule;
import ScannerReport.ChangedLines;
import ScannerReport.Changesets;
import ScannerReport.Changesets.Changeset;
import ScannerReport.CpdTextBlock;
import ScannerReport.Duplicate;
import ScannerReport.Duplication;
import ScannerReport.ExternalIssue;
import ScannerReport.Issue;
import ScannerReport.IssueType.BUG;
import ScannerReport.LineCoverage;
import ScannerReport.LineSgnificantCode;
import ScannerReport.Measure;
import ScannerReport.Metadata.Builder;
import ScannerReport.Symbol;
import ScannerReport.SyntaxHighlightingRule;
import ScannerReport.TextRange;
import com.google.common.collect.Iterators;
import java.io.File;
import java.util.Arrays;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonar.core.util.CloseableIterator;
import org.sonar.core.util.Protobuf;
import org.sonar.scanner.protocol.output.ScannerReport.Measure.DoubleValue;


public class ScannerReportWriterTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    File dir;

    ScannerReportWriter underTest;

    @Test
    public void create_dir_if_does_not_exist() {
        FileUtils.deleteQuietly(dir);
        underTest = new ScannerReportWriter(dir);
        assertThat(dir).isDirectory().exists();
    }

    @Test
    public void write_metadata() {
        ScannerReport.Metadata.Builder metadata = ScannerReport.Metadata.newBuilder().setAnalysisDate(15000000L).setProjectKey("PROJECT_A").setRootComponentRef(1);
        underTest.writeMetadata(metadata.build());
        ScannerReport.Metadata read = Protobuf.read(underTest.getFileStructure().metadataFile(), ScannerReport.Metadata.parser());
        assertThat(read.getAnalysisDate()).isEqualTo(15000000L);
        assertThat(read.getProjectKey()).isEqualTo("PROJECT_A");
        assertThat(read.getRootComponentRef()).isEqualTo(1);
    }

    @Test
    public void write_component() {
        // no data yet
        assertThat(underTest.hasComponentData(COMPONENT, 1)).isFalse();
        // write data
        ScannerReport.Component.Builder component = ScannerReport.Component.newBuilder().setRef(1).setLanguage("java").setProjectRelativePath("src/Foo.java").setType(FILE).setIsTest(false).addChildRef(5).addChildRef(42);
        underTest.writeComponent(component.build());
        assertThat(underTest.hasComponentData(COMPONENT, 1)).isTrue();
        File file = underTest.getFileStructure().fileFor(COMPONENT, 1);
        assertThat(file).exists().isFile();
        ScannerReport.Component read = Protobuf.read(file, ScannerReport.Component.parser());
        assertThat(read.getRef()).isEqualTo(1);
        assertThat(read.getChildRefList()).containsOnly(5, 42);
        assertThat(read.getName()).isEmpty();
        assertThat(read.getIsTest()).isFalse();
    }

    @Test
    public void write_issues() {
        // no data yet
        assertThat(underTest.hasComponentData(ISSUES, 1)).isFalse();
        // write data
        ScannerReport.Issue issue = Issue.newBuilder().setMsg("the message").build();
        underTest.writeComponentIssues(1, Arrays.asList(issue));
        assertThat(underTest.hasComponentData(ISSUES, 1)).isTrue();
        File file = underTest.getFileStructure().fileFor(ISSUES, 1);
        assertThat(file).exists().isFile();
        try (CloseableIterator<ScannerReport.Issue> read = Protobuf.readStream(file, Issue.parser())) {
            assertThat(Iterators.size(read)).isEqualTo(1);
        }
    }

    @Test
    public void write_external_issues() {
        // no data yet
        assertThat(underTest.hasComponentData(EXTERNAL_ISSUES, 1)).isFalse();
        // write data
        ScannerReport.ExternalIssue issue = ExternalIssue.newBuilder().setMsg("the message").build();
        underTest.appendComponentExternalIssue(1, issue);
        assertThat(underTest.hasComponentData(EXTERNAL_ISSUES, 1)).isTrue();
        File file = underTest.getFileStructure().fileFor(EXTERNAL_ISSUES, 1);
        assertThat(file).exists().isFile();
        try (CloseableIterator<ScannerReport.ExternalIssue> read = Protobuf.readStream(file, ExternalIssue.parser())) {
            assertThat(Iterators.size(read)).isEqualTo(1);
        }
    }

    @Test
    public void write_adhoc_rule() {
        // write data
        ScannerReport.AdHocRule rule = AdHocRule.newBuilder().setEngineId("eslint").setRuleId("123").setName("Foo").setDescription("Description").setSeverity(BLOCKER).setType(BUG).build();
        underTest.appendAdHocRule(rule);
        File file = underTest.getFileStructure().adHocRules();
        assertThat(file).exists().isFile();
        try (CloseableIterator<ScannerReport.AdHocRule> read = Protobuf.readStream(file, AdHocRule.parser())) {
            assertThat(Iterators.size(read)).isEqualTo(1);
        }
    }

    @Test
    public void write_changed_lines() {
        assertThat(underTest.hasComponentData(CHANGED_LINES, 1)).isFalse();
        ScannerReport.ChangedLines changedLines = ChangedLines.newBuilder().addLine(1).addLine(3).build();
        underTest.writeComponentChangedLines(1, changedLines);
        assertThat(underTest.hasComponentData(CHANGED_LINES, 1)).isTrue();
        File file = underTest.getFileStructure().fileFor(CHANGED_LINES, 1);
        assertThat(file).exists().isFile();
        ScannerReport.ChangedLines loadedChangedLines = Protobuf.read(file, ChangedLines.parser());
        assertThat(loadedChangedLines.getLineList()).containsExactly(1, 3);
    }

    @Test
    public void write_measures() {
        assertThat(underTest.hasComponentData(MEASURES, 1)).isFalse();
        ScannerReport.Measure measure = Measure.newBuilder().setDoubleValue(DoubleValue.newBuilder().setValue(2.5).setData("text-value")).build();
        underTest.writeComponentMeasures(1, Arrays.asList(measure));
        assertThat(underTest.hasComponentData(MEASURES, 1)).isTrue();
        File file = underTest.getFileStructure().fileFor(MEASURES, 1);
        assertThat(file).exists().isFile();
        try (CloseableIterator<ScannerReport.Measure> read = Protobuf.readStream(file, Measure.parser())) {
            assertThat(Iterators.size(read)).isEqualTo(1);
        }
    }

    @Test
    public void write_scm() {
        assertThat(underTest.hasComponentData(CHANGESETS, 1)).isFalse();
        ScannerReport.Changesets scm = Changesets.newBuilder().setComponentRef(1).addChangesetIndexByLine(0).addChangeset(Changeset.newBuilder().setRevision("123-456-789").setAuthor("author").setDate(123456789L)).build();
        underTest.writeComponentChangesets(scm);
        assertThat(underTest.hasComponentData(CHANGESETS, 1)).isTrue();
        File file = underTest.getFileStructure().fileFor(CHANGESETS, 1);
        assertThat(file).exists().isFile();
        ScannerReport.Changesets read = Protobuf.read(file, Changesets.parser());
        assertThat(read.getComponentRef()).isEqualTo(1);
        assertThat(read.getChangesetCount()).isEqualTo(1);
        assertThat(read.getChangesetList()).hasSize(1);
        assertThat(read.getChangeset(0).getDate()).isEqualTo(123456789L);
    }

    @Test
    public void write_duplications() {
        assertThat(underTest.hasComponentData(DUPLICATIONS, 1)).isFalse();
        ScannerReport.Duplication duplication = Duplication.newBuilder().setOriginPosition(TextRange.newBuilder().setStartLine(1).setEndLine(5).build()).addDuplicate(Duplicate.newBuilder().setOtherFileRef(2).setRange(TextRange.newBuilder().setStartLine(6).setEndLine(10).build()).build()).build();
        underTest.writeComponentDuplications(1, Arrays.asList(duplication));
        assertThat(underTest.hasComponentData(DUPLICATIONS, 1)).isTrue();
        File file = underTest.getFileStructure().fileFor(DUPLICATIONS, 1);
        assertThat(file).exists().isFile();
        try (CloseableIterator<ScannerReport.Duplication> duplications = Protobuf.readStream(file, Duplication.parser())) {
            ScannerReport.Duplication dup = duplications.next();
            assertThat(dup.getOriginPosition()).isNotNull();
            assertThat(dup.getDuplicateList()).hasSize(1);
        }
    }

    @Test
    public void write_duplication_blocks() {
        assertThat(underTest.hasComponentData(CPD_TEXT_BLOCKS, 1)).isFalse();
        ScannerReport.CpdTextBlock duplicationBlock = CpdTextBlock.newBuilder().setHash("abcdefghijklmnop").setStartLine(1).setEndLine(2).setStartTokenIndex(10).setEndTokenIndex(15).build();
        underTest.writeCpdTextBlocks(1, Arrays.asList(duplicationBlock));
        assertThat(underTest.hasComponentData(CPD_TEXT_BLOCKS, 1)).isTrue();
        File file = underTest.getFileStructure().fileFor(CPD_TEXT_BLOCKS, 1);
        assertThat(file).exists().isFile();
        try (CloseableIterator<ScannerReport.CpdTextBlock> duplicationBlocks = Protobuf.readStream(file, CpdTextBlock.parser())) {
            ScannerReport.CpdTextBlock duplicationBlockResult = duplicationBlocks.next();
            assertThat(duplicationBlockResult.getHash()).isEqualTo("abcdefghijklmnop");
            assertThat(duplicationBlockResult.getStartLine()).isEqualTo(1);
            assertThat(duplicationBlockResult.getEndLine()).isEqualTo(2);
            assertThat(duplicationBlockResult.getStartTokenIndex()).isEqualTo(10);
            assertThat(duplicationBlockResult.getEndTokenIndex()).isEqualTo(15);
        }
    }

    @Test
    public void write_symbols() {
        // no data yet
        assertThat(underTest.hasComponentData(SYMBOLS, 1)).isFalse();
        // write data
        ScannerReport.Symbol symbol = Symbol.newBuilder().setDeclaration(TextRange.newBuilder().setStartLine(1).setStartOffset(3).setEndLine(1).setEndOffset(5).build()).addReference(TextRange.newBuilder().setStartLine(10).setStartOffset(15).setEndLine(11).setEndOffset(2).build()).build();
        underTest.writeComponentSymbols(1, Arrays.asList(symbol));
        assertThat(underTest.hasComponentData(SYMBOLS, 1)).isTrue();
        File file = underTest.getFileStructure().fileFor(SYMBOLS, 1);
        assertThat(file).exists().isFile();
        try (CloseableIterator<ScannerReport.Symbol> read = Protobuf.readStream(file, Symbol.parser())) {
            assertThat(read).hasSize(1);
        }
    }

    @Test
    public void write_syntax_highlighting() {
        // no data yet
        assertThat(underTest.hasComponentData(SYNTAX_HIGHLIGHTINGS, 1)).isFalse();
        underTest.writeComponentSyntaxHighlighting(1, Arrays.asList(SyntaxHighlightingRule.newBuilder().setRange(TextRange.newBuilder().setStartLine(1).setEndLine(1).build()).setType(ANNOTATION).build()));
        assertThat(underTest.hasComponentData(SYNTAX_HIGHLIGHTINGS, 1)).isTrue();
    }

    @Test
    public void write_line_significant_code() {
        // no data yet
        assertThat(underTest.hasComponentData(SGNIFICANT_CODE, 1)).isFalse();
        underTest.writeComponentSignificantCode(1, Arrays.asList(LineSgnificantCode.newBuilder().setLine(1).setStartOffset(2).setEndOffset(3).build()));
        assertThat(underTest.hasComponentData(SGNIFICANT_CODE, 1)).isTrue();
    }

    @Test
    public void write_coverage() {
        // no data yet
        assertThat(underTest.hasComponentData(COVERAGES, 1)).isFalse();
        underTest.writeComponentCoverage(1, Arrays.asList(LineCoverage.newBuilder().setLine(1).setConditions(1).setHits(true).setCoveredConditions(1).build()));
        assertThat(underTest.hasComponentData(COVERAGES, 1)).isTrue();
    }
}

