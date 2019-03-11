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
package org.sonar.ce.task.projectanalysis.source.linereader;


import Component.Type.FILE;
import DbFileSources.Data.Builder;
import LineReader.ReadError;
import ScannerReport.SyntaxHighlightingRule;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.utils.log.LogTester;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.source.linereader.RangeOffsetConverter.RangeOffsetConverterException;
import org.sonar.db.protobuf.DbFileSources;
import org.sonar.scanner.protocol.output.ScannerReport;
import org.sonar.scanner.protocol.output.ScannerReport.TextRange;

import static Data.HIGHLIGHTING;


public class HighlightingLineReaderTest {
    @Rule
    public LogTester logTester = new LogTester();

    private static final Component FILE = ReportComponent.builder(Component.Type.FILE, 1).setUuid("FILE_UUID").setKey("FILE_KEY").build();

    private static final int DEFAULT_LINE_LENGTH = 5;

    private static final int LINE_1 = 1;

    private static final int LINE_2 = 2;

    private static final int LINE_3 = 3;

    private static final int LINE_4 = 4;

    private static final String RANGE_LABEL_1 = "1,2";

    private static final String RANGE_LABEL_2 = "2,3";

    private static final String RANGE_LABEL_3 = "3,4";

    private static final String RANGE_LABEL_4 = "0,2";

    private static final String RANGE_LABEL_5 = "0,3";

    private RangeOffsetConverter rangeOffsetConverter = Mockito.mock(RangeOffsetConverter.class);

    private Builder sourceData = newBuilder();

    private DbFileSources.Line.Builder line1 = sourceData.addLinesBuilder().setSource("line1").setLine(1);

    private DbFileSources.Line.Builder line2 = sourceData.addLinesBuilder().setSource("line2").setLine(2);

    private DbFileSources.Line.Builder line3 = sourceData.addLinesBuilder().setSource("line3").setLine(3);

    private DbFileSources.Line.Builder line4 = sourceData.addLinesBuilder().setSource("line4").setLine(4);

    @Test
    public void nothing_to_read() {
        HighlightingLineReader highlightingLineReader = newReader(Collections.emptyMap());
        DbFileSources.Line.Builder lineBuilder = newBuilder().addLinesBuilder().setLine(1);
        assertThat(highlightingLineReader.read(lineBuilder)).isEmpty();
        assertThat(lineBuilder.hasHighlighting()).isFalse();
    }

    @Test
    public void read_one_line() {
        HighlightingLineReader highlightingLineReader = newReader(ImmutableMap.of(newSingleLineTextRangeWithExpectingLabel(HighlightingLineReaderTest.LINE_1, HighlightingLineReaderTest.RANGE_LABEL_1), ANNOTATION));
        assertThat(highlightingLineReader.read(line1)).isEmpty();
        assertThat(line1.getHighlighting()).isEqualTo(((HighlightingLineReaderTest.RANGE_LABEL_1) + ",a"));
    }

    @Test
    public void read_many_lines() {
        HighlightingLineReader highlightingLineReader = newReader(ImmutableMap.of(newSingleLineTextRangeWithExpectingLabel(HighlightingLineReaderTest.LINE_1, HighlightingLineReaderTest.RANGE_LABEL_1), ANNOTATION, newSingleLineTextRangeWithExpectingLabel(HighlightingLineReaderTest.LINE_2, HighlightingLineReaderTest.RANGE_LABEL_2), COMMENT, newSingleLineTextRangeWithExpectingLabel(HighlightingLineReaderTest.LINE_4, HighlightingLineReaderTest.RANGE_LABEL_3), CONSTANT));
        assertThat(highlightingLineReader.read(line1)).isEmpty();
        assertThat(highlightingLineReader.read(line2)).isEmpty();
        assertThat(highlightingLineReader.read(line3)).isEmpty();
        assertThat(highlightingLineReader.read(line4)).isEmpty();
        assertThat(line1.getHighlighting()).isEqualTo(((HighlightingLineReaderTest.RANGE_LABEL_1) + ",a"));
        assertThat(line2.getHighlighting()).isEqualTo(((HighlightingLineReaderTest.RANGE_LABEL_2) + ",cd"));
        assertThat(line4.getHighlighting()).isEqualTo(((HighlightingLineReaderTest.RANGE_LABEL_3) + ",c"));
    }

    @Test
    public void supports_highlighting_over_multiple_lines_including_an_empty_one() {
        List<ScannerReport.SyntaxHighlightingRule> syntaxHighlightingList = new ArrayList<>();
        addHighlighting(syntaxHighlightingList, 1, 0, 1, 7, KEYWORD);// package

        addHighlighting(syntaxHighlightingList, 2, 0, 4, 6, CPP_DOC);// comment over 3 lines

        addHighlighting(syntaxHighlightingList, 5, 0, 5, 6, KEYWORD);// public

        addHighlighting(syntaxHighlightingList, 5, 7, 5, 12, KEYWORD);// class

        HighlightingLineReader highlightingLineReader = new HighlightingLineReader(HighlightingLineReaderTest.FILE, syntaxHighlightingList.iterator(), new RangeOffsetConverter());
        DbFileSources[] builders = new DbFileSources.Line.Builder[]{ addSourceLine(highlightingLineReader, 1, "package example;"), addSourceLine(highlightingLineReader, 2, "/*"), addSourceLine(highlightingLineReader, 3, ""), addSourceLine(highlightingLineReader, 4, " foo*/"), addSourceLine(highlightingLineReader, 5, "public class One {"), addSourceLine(highlightingLineReader, 6, "}") };
        assertThat(builders).extracting("highlighting").containsExactly("0,7,k", "0,2,cppd", "", "0,6,cppd", "0,6,k;7,12,k", "");
    }

    @Test
    public void read_many_syntax_highlighting_on_same_line() {
        HighlightingLineReader highlightingLineReader = newReader(ImmutableMap.of(newSingleLineTextRangeWithExpectingLabel(HighlightingLineReaderTest.LINE_1, HighlightingLineReaderTest.RANGE_LABEL_1), ANNOTATION, newSingleLineTextRangeWithExpectingLabel(HighlightingLineReaderTest.LINE_1, HighlightingLineReaderTest.RANGE_LABEL_2), COMMENT));
        assertThat(highlightingLineReader.read(line1)).isEmpty();
        assertThat(line1.getHighlighting()).isEqualTo(((((HighlightingLineReaderTest.RANGE_LABEL_1) + ",a;") + (HighlightingLineReaderTest.RANGE_LABEL_2)) + ",cd"));
    }

    @Test
    public void read_one_syntax_highlighting_on_many_lines() {
        // This highlighting begin on line 1 and finish on line 3
        TextRange textRange = HighlightingLineReaderTest.newTextRange(HighlightingLineReaderTest.LINE_1, HighlightingLineReaderTest.LINE_3);
        Mockito.when(rangeOffsetConverter.offsetToString(textRange, HighlightingLineReaderTest.LINE_1, HighlightingLineReaderTest.DEFAULT_LINE_LENGTH)).thenReturn(HighlightingLineReaderTest.RANGE_LABEL_1);
        Mockito.when(rangeOffsetConverter.offsetToString(textRange, HighlightingLineReaderTest.LINE_2, 6)).thenReturn(HighlightingLineReaderTest.RANGE_LABEL_2);
        Mockito.when(rangeOffsetConverter.offsetToString(textRange, HighlightingLineReaderTest.LINE_3, HighlightingLineReaderTest.DEFAULT_LINE_LENGTH)).thenReturn(HighlightingLineReaderTest.RANGE_LABEL_3);
        HighlightingLineReader highlightingLineReader = newReader(ImmutableMap.of(textRange, ANNOTATION));
        assertThat(highlightingLineReader.read(line1)).isEmpty();
        DbFileSources.Line.Builder line2 = sourceData.addLinesBuilder().setSource("line 2").setLine(2);
        assertThat(highlightingLineReader.read(line2)).isEmpty();
        assertThat(highlightingLineReader.read(line3)).isEmpty();
        assertThat(line1.getHighlighting()).isEqualTo(((HighlightingLineReaderTest.RANGE_LABEL_1) + ",a"));
        assertThat(line2.getHighlighting()).isEqualTo(((HighlightingLineReaderTest.RANGE_LABEL_2) + ",a"));
        assertThat(line3.getHighlighting()).isEqualTo(((HighlightingLineReaderTest.RANGE_LABEL_3) + ",a"));
    }

    @Test
    public void read_many_syntax_highlighting_on_many_lines() {
        TextRange textRange1 = HighlightingLineReaderTest.newTextRange(HighlightingLineReaderTest.LINE_1, HighlightingLineReaderTest.LINE_3);
        Mockito.when(rangeOffsetConverter.offsetToString(textRange1, HighlightingLineReaderTest.LINE_1, HighlightingLineReaderTest.DEFAULT_LINE_LENGTH)).thenReturn(HighlightingLineReaderTest.RANGE_LABEL_1);
        Mockito.when(rangeOffsetConverter.offsetToString(textRange1, HighlightingLineReaderTest.LINE_2, HighlightingLineReaderTest.DEFAULT_LINE_LENGTH)).thenReturn(HighlightingLineReaderTest.RANGE_LABEL_2);
        Mockito.when(rangeOffsetConverter.offsetToString(textRange1, HighlightingLineReaderTest.LINE_3, HighlightingLineReaderTest.DEFAULT_LINE_LENGTH)).thenReturn(HighlightingLineReaderTest.RANGE_LABEL_3);
        TextRange textRange2 = HighlightingLineReaderTest.newTextRange(HighlightingLineReaderTest.LINE_2, HighlightingLineReaderTest.LINE_4);
        Mockito.when(rangeOffsetConverter.offsetToString(textRange2, HighlightingLineReaderTest.LINE_2, HighlightingLineReaderTest.DEFAULT_LINE_LENGTH)).thenReturn(HighlightingLineReaderTest.RANGE_LABEL_2);
        Mockito.when(rangeOffsetConverter.offsetToString(textRange2, HighlightingLineReaderTest.LINE_3, HighlightingLineReaderTest.DEFAULT_LINE_LENGTH)).thenReturn(HighlightingLineReaderTest.RANGE_LABEL_2);
        Mockito.when(rangeOffsetConverter.offsetToString(textRange2, HighlightingLineReaderTest.LINE_4, HighlightingLineReaderTest.DEFAULT_LINE_LENGTH)).thenReturn(HighlightingLineReaderTest.RANGE_LABEL_4);
        TextRange textRange3 = HighlightingLineReaderTest.newTextRange(HighlightingLineReaderTest.LINE_2, HighlightingLineReaderTest.LINE_2);
        Mockito.when(rangeOffsetConverter.offsetToString(textRange3, HighlightingLineReaderTest.LINE_2, HighlightingLineReaderTest.DEFAULT_LINE_LENGTH)).thenReturn(HighlightingLineReaderTest.RANGE_LABEL_5);
        HighlightingLineReader highlightingLineReader = newReader(ImmutableMap.of(textRange1, ANNOTATION, textRange2, HIGHLIGHTING_STRING, textRange3, COMMENT));
        assertThat(highlightingLineReader.read(line1)).isEmpty();
        assertThat(highlightingLineReader.read(line2)).isEmpty();
        assertThat(highlightingLineReader.read(line3)).isEmpty();
        assertThat(highlightingLineReader.read(line4)).isEmpty();
        assertThat(line1.getHighlighting()).isEqualTo(((HighlightingLineReaderTest.RANGE_LABEL_1) + ",a"));
        assertThat(line2.getHighlighting()).isEqualTo(((((((HighlightingLineReaderTest.RANGE_LABEL_2) + ",a;") + (HighlightingLineReaderTest.RANGE_LABEL_2)) + ",s;") + (HighlightingLineReaderTest.RANGE_LABEL_5)) + ",cd"));
        assertThat(line3.getHighlighting()).isEqualTo(((((HighlightingLineReaderTest.RANGE_LABEL_3) + ",a;") + (HighlightingLineReaderTest.RANGE_LABEL_2)) + ",s"));
        assertThat(line4.getHighlighting()).isEqualTo(((HighlightingLineReaderTest.RANGE_LABEL_4) + ",s"));
    }

    @Test
    public void read_highlighting_declared_on_a_whole_line() {
        TextRange textRange = HighlightingLineReaderTest.newTextRange(HighlightingLineReaderTest.LINE_1, HighlightingLineReaderTest.LINE_2);
        Mockito.when(rangeOffsetConverter.offsetToString(textRange, HighlightingLineReaderTest.LINE_1, HighlightingLineReaderTest.DEFAULT_LINE_LENGTH)).thenReturn(HighlightingLineReaderTest.RANGE_LABEL_1);
        Mockito.when(rangeOffsetConverter.offsetToString(textRange, HighlightingLineReaderTest.LINE_2, HighlightingLineReaderTest.DEFAULT_LINE_LENGTH)).thenReturn("");
        HighlightingLineReader highlightingLineReader = newReader(ImmutableMap.of(textRange, ANNOTATION));
        assertThat(highlightingLineReader.read(line1)).isEmpty();
        assertThat(highlightingLineReader.read(line2)).isEmpty();
        assertThat(highlightingLineReader.read(line3)).isEmpty();
        assertThat(line1.getHighlighting()).isEqualTo(((HighlightingLineReaderTest.RANGE_LABEL_1) + ",a"));
        // Nothing should be set on line 2
        assertThat(line2.getHighlighting()).isEmpty();
        assertThat(line3.getHighlighting()).isEmpty();
    }

    @Test
    public void not_fail_and_stop_processing_when_range_offset_converter_throw_RangeOffsetConverterException() {
        TextRange textRange1 = HighlightingLineReaderTest.newTextRange(HighlightingLineReaderTest.LINE_1, HighlightingLineReaderTest.LINE_1);
        Mockito.doThrow(RangeOffsetConverterException.class).when(rangeOffsetConverter).offsetToString(textRange1, HighlightingLineReaderTest.LINE_1, HighlightingLineReaderTest.DEFAULT_LINE_LENGTH);
        HighlightingLineReader highlightingLineReader = newReader(ImmutableMap.of(textRange1, HighlightingType.ANNOTATION, newSingleLineTextRangeWithExpectingLabel(HighlightingLineReaderTest.LINE_2, HighlightingLineReaderTest.RANGE_LABEL_1), HIGHLIGHTING_STRING));
        LineReader.ReadError readErrorLine1 = new LineReader.ReadError(HIGHLIGHTING, HighlightingLineReaderTest.LINE_1);
        assertThat(highlightingLineReader.read(line1)).contains(readErrorLine1);
        assertThat(highlightingLineReader.read(line2)).contains(readErrorLine1);
        assertNoHighlighting();
        assertThat(logTester.logs(DEBUG)).isNotEmpty();
    }

    @Test
    public void keep_existing_processed_highlighting_when_range_offset_converter_throw_RangeOffsetConverterException() {
        TextRange textRange2 = HighlightingLineReaderTest.newTextRange(HighlightingLineReaderTest.LINE_2, HighlightingLineReaderTest.LINE_2);
        Mockito.doThrow(RangeOffsetConverterException.class).when(rangeOffsetConverter).offsetToString(textRange2, HighlightingLineReaderTest.LINE_2, HighlightingLineReaderTest.DEFAULT_LINE_LENGTH);
        TextRange textRange3 = HighlightingLineReaderTest.newTextRange(HighlightingLineReaderTest.LINE_3, HighlightingLineReaderTest.LINE_3);
        HighlightingLineReader highlightingLineReader = newReader(ImmutableMap.of(newSingleLineTextRangeWithExpectingLabel(HighlightingLineReaderTest.LINE_1, HighlightingLineReaderTest.RANGE_LABEL_1), ANNOTATION, textRange2, HIGHLIGHTING_STRING, textRange3, COMMENT));
        assertThat(highlightingLineReader.read(line1)).isEmpty();
        LineReader.ReadError readErrorLine2 = new LineReader.ReadError(HIGHLIGHTING, HighlightingLineReaderTest.LINE_2);
        assertThat(highlightingLineReader.read(line2)).contains(readErrorLine2);
        assertThat(highlightingLineReader.read(line3)).contains(readErrorLine2);
        assertThat(line1.hasHighlighting()).isTrue();
        assertThat(line2.hasHighlighting()).isFalse();
        assertThat(line3.hasHighlighting()).isFalse();
        assertThat(logTester.logs(DEBUG)).isNotEmpty();
    }

    @Test
    public void display_file_key_in_debug_when_range_offset_converter_throw_RangeOffsetConverterException() {
        TextRange textRange1 = HighlightingLineReaderTest.newTextRange(HighlightingLineReaderTest.LINE_1, HighlightingLineReaderTest.LINE_1);
        Mockito.doThrow(RangeOffsetConverterException.class).when(rangeOffsetConverter).offsetToString(textRange1, HighlightingLineReaderTest.LINE_1, HighlightingLineReaderTest.DEFAULT_LINE_LENGTH);
        HighlightingLineReader highlightingLineReader = newReader(ImmutableMap.of(textRange1, ANNOTATION));
        assertThat(highlightingLineReader.read(line1)).contains(new LineReader.ReadError(HIGHLIGHTING, 1));
        assertThat(logTester.logs(DEBUG)).containsOnly("Inconsistency detected in Highlighting data. Highlighting will be ignored for file 'FILE_KEY'");
    }
}

