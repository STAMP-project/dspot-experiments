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
import DbFileSources.Data;
import DbFileSources.Data.Builder;
import LineReader.ReadError;
import RangeOffsetConverter.RangeOffsetConverterException;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.utils.log.LogTester;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.scanner.protocol.output.ScannerReport.TextRange;

import static Data.SYMBOLS;


public class SymbolsLineReaderTest {
    @Rule
    public LogTester logTester = new LogTester();

    private static final Component FILE = ReportComponent.builder(Component.Type.FILE, 1).setUuid("FILE_UUID").setKey("FILE_KEY").build();

    private static final int DEFAULT_LINE_LENGTH = 5;

    private static final int LINE_1 = 1;

    private static final int LINE_2 = 2;

    private static final int LINE_3 = 3;

    private static final int LINE_4 = 4;

    private static final int OFFSET_0 = 0;

    private static final int OFFSET_1 = 1;

    private static final int OFFSET_2 = 2;

    private static final int OFFSET_3 = 3;

    private static final int OFFSET_4 = 4;

    private static final String RANGE_LABEL_1 = "1,2";

    private static final String RANGE_LABEL_2 = "2,3";

    private static final String RANGE_LABEL_3 = "3,4";

    private static final String RANGE_LABEL_4 = "0,2";

    private RangeOffsetConverter rangeOffsetConverter = Mockito.mock(RangeOffsetConverter.class);

    private Builder sourceData = Data.newBuilder();

    private DbFileSources.Line.Builder line1 = sourceData.addLinesBuilder().setSource("line1").setLine(1);

    private DbFileSources.Line.Builder line2 = sourceData.addLinesBuilder().setSource("line2").setLine(2);

    private DbFileSources.Line.Builder line3 = sourceData.addLinesBuilder().setSource("line3").setLine(3);

    private DbFileSources.Line.Builder line4 = sourceData.addLinesBuilder().setSource("line4").setLine(4);

    @Test
    public void read_nothing() {
        SymbolsLineReader symbolsLineReader = newReader();
        assertThat(symbolsLineReader.read(line1)).isEmpty();
        assertThat(line1.getSymbols()).isEmpty();
    }

    @Test
    public void read_symbols() {
        SymbolsLineReader symbolsLineReader = newReader(newSymbol(newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_1, SymbolsLineReaderTest.OFFSET_2, SymbolsLineReaderTest.OFFSET_4, SymbolsLineReaderTest.RANGE_LABEL_1), newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_3, SymbolsLineReaderTest.OFFSET_1, SymbolsLineReaderTest.OFFSET_3, SymbolsLineReaderTest.RANGE_LABEL_2)));
        assertThat(symbolsLineReader.read(line1)).isEmpty();
        assertThat(symbolsLineReader.read(line2)).isEmpty();
        assertThat(symbolsLineReader.read(line3)).isEmpty();
        assertThat(line1.getSymbols()).isEqualTo(((SymbolsLineReaderTest.RANGE_LABEL_1) + ",1"));
        assertThat(line2.getSymbols()).isEmpty();
        assertThat(line3.getSymbols()).isEqualTo(((SymbolsLineReaderTest.RANGE_LABEL_2) + ",1"));
    }

    @Test
    public void read_symbols_with_reference_on_same_line() {
        SymbolsLineReader symbolsLineReader = newReader(newSymbol(newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_1, SymbolsLineReaderTest.OFFSET_0, SymbolsLineReaderTest.OFFSET_1, SymbolsLineReaderTest.RANGE_LABEL_1), newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_1, SymbolsLineReaderTest.OFFSET_2, SymbolsLineReaderTest.OFFSET_3, SymbolsLineReaderTest.RANGE_LABEL_2)));
        assertThat(symbolsLineReader.read(line1)).isEmpty();
        assertThat(line1.getSymbols()).isEqualTo(((((SymbolsLineReaderTest.RANGE_LABEL_1) + ",1;") + (SymbolsLineReaderTest.RANGE_LABEL_2)) + ",1"));
    }

    @Test
    public void read_symbols_with_two_references() {
        SymbolsLineReader symbolsLineReader = newReader(newSymbol(newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_1, SymbolsLineReaderTest.OFFSET_2, SymbolsLineReaderTest.OFFSET_4, SymbolsLineReaderTest.RANGE_LABEL_1), newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_3, SymbolsLineReaderTest.OFFSET_1, SymbolsLineReaderTest.OFFSET_3, SymbolsLineReaderTest.RANGE_LABEL_2), newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_2, SymbolsLineReaderTest.OFFSET_0, SymbolsLineReaderTest.OFFSET_2, SymbolsLineReaderTest.RANGE_LABEL_3)));
        assertThat(symbolsLineReader.read(line1)).isEmpty();
        assertThat(symbolsLineReader.read(line2)).isEmpty();
        assertThat(symbolsLineReader.read(line3)).isEmpty();
        assertThat(line1.getSymbols()).isEqualTo(((SymbolsLineReaderTest.RANGE_LABEL_1) + ",1"));
        assertThat(line2.getSymbols()).isEqualTo(((SymbolsLineReaderTest.RANGE_LABEL_3) + ",1"));
        assertThat(line3.getSymbols()).isEqualTo(((SymbolsLineReaderTest.RANGE_LABEL_2) + ",1"));
    }

    @Test
    public void read_symbols_with_two_references_on_the_same_line() {
        SymbolsLineReader symbolsLineReader = newReader(newSymbol(newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_1, SymbolsLineReaderTest.OFFSET_2, SymbolsLineReaderTest.OFFSET_3, SymbolsLineReaderTest.RANGE_LABEL_1), newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_2, SymbolsLineReaderTest.OFFSET_0, SymbolsLineReaderTest.OFFSET_1, SymbolsLineReaderTest.RANGE_LABEL_2), newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_2, SymbolsLineReaderTest.OFFSET_2, SymbolsLineReaderTest.OFFSET_3, SymbolsLineReaderTest.RANGE_LABEL_3)));
        assertThat(symbolsLineReader.read(line1)).isEmpty();
        assertThat(symbolsLineReader.read(line2)).isEmpty();
        assertThat(line1.getSymbols()).isEqualTo(((SymbolsLineReaderTest.RANGE_LABEL_1) + ",1"));
        assertThat(line2.getSymbols()).isEqualTo(((((SymbolsLineReaderTest.RANGE_LABEL_2) + ",1;") + (SymbolsLineReaderTest.RANGE_LABEL_3)) + ",1"));
    }

    @Test
    public void read_symbols_when_reference_line_is_before_declaration_line() {
        SymbolsLineReader symbolsLineReader = newReader(newSymbol(newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_2, SymbolsLineReaderTest.OFFSET_3, SymbolsLineReaderTest.OFFSET_4, SymbolsLineReaderTest.RANGE_LABEL_1), newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_1, SymbolsLineReaderTest.OFFSET_1, SymbolsLineReaderTest.OFFSET_2, SymbolsLineReaderTest.RANGE_LABEL_2)));
        assertThat(symbolsLineReader.read(line1)).isEmpty();
        assertThat(symbolsLineReader.read(line2)).isEmpty();
        assertThat(line1.getSymbols()).isEqualTo(((SymbolsLineReaderTest.RANGE_LABEL_2) + ",1"));
        assertThat(line2.getSymbols()).isEqualTo(((SymbolsLineReaderTest.RANGE_LABEL_1) + ",1"));
    }

    @Test
    public void read_many_symbols_on_lines() {
        SymbolsLineReader symbolsLineReader = newReader(newSymbol(newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_1, SymbolsLineReaderTest.OFFSET_1, SymbolsLineReaderTest.OFFSET_2, SymbolsLineReaderTest.RANGE_LABEL_1), newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_3, SymbolsLineReaderTest.OFFSET_2, SymbolsLineReaderTest.OFFSET_3, SymbolsLineReaderTest.RANGE_LABEL_2)), newSymbol(newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_1, SymbolsLineReaderTest.OFFSET_3, SymbolsLineReaderTest.OFFSET_4, SymbolsLineReaderTest.RANGE_LABEL_3), newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_3, SymbolsLineReaderTest.OFFSET_0, SymbolsLineReaderTest.OFFSET_1, SymbolsLineReaderTest.RANGE_LABEL_4)));
        assertThat(symbolsLineReader.read(line1)).isEmpty();
        assertThat(symbolsLineReader.read(line2)).isEmpty();
        assertThat(symbolsLineReader.read(line3)).isEmpty();
        assertThat(line1.getSymbols()).isEqualTo(((((SymbolsLineReaderTest.RANGE_LABEL_1) + ",1;") + (SymbolsLineReaderTest.RANGE_LABEL_3)) + ",2"));
        assertThat(line2.getSymbols()).isEmpty();
        assertThat(line3.getSymbols()).isEqualTo(((((SymbolsLineReaderTest.RANGE_LABEL_2) + ",1;") + (SymbolsLineReaderTest.RANGE_LABEL_4)) + ",2"));
    }

    @Test
    public void symbol_declaration_should_be_sorted_by_offset() {
        SymbolsLineReader symbolsLineReader = newReader(// This symbol begins after the second symbol, it should appear in second place
        newSymbol(newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_1, SymbolsLineReaderTest.OFFSET_2, SymbolsLineReaderTest.OFFSET_3, SymbolsLineReaderTest.RANGE_LABEL_1), newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_3, SymbolsLineReaderTest.OFFSET_2, SymbolsLineReaderTest.OFFSET_3, SymbolsLineReaderTest.RANGE_LABEL_1)), newSymbol(newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_1, SymbolsLineReaderTest.OFFSET_0, SymbolsLineReaderTest.OFFSET_1, SymbolsLineReaderTest.RANGE_LABEL_2), newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_3, SymbolsLineReaderTest.OFFSET_0, SymbolsLineReaderTest.OFFSET_1, SymbolsLineReaderTest.RANGE_LABEL_2)));
        assertThat(symbolsLineReader.read(line1)).isEmpty();
        assertThat(symbolsLineReader.read(line2)).isEmpty();
        assertThat(symbolsLineReader.read(line3)).isEmpty();
        assertThat(line1.getSymbols()).isEqualTo(((((SymbolsLineReaderTest.RANGE_LABEL_2) + ",1;") + (SymbolsLineReaderTest.RANGE_LABEL_1)) + ",2"));
        assertThat(line2.getSymbols()).isEmpty();
        assertThat(line3.getSymbols()).isEqualTo(((((SymbolsLineReaderTest.RANGE_LABEL_2) + ",1;") + (SymbolsLineReaderTest.RANGE_LABEL_1)) + ",2"));
    }

    @Test
    public void symbol_declaration_should_be_sorted_by_line() {
        SymbolsLineReader symbolsLineReader = newReader(newSymbol(newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_2, SymbolsLineReaderTest.OFFSET_0, SymbolsLineReaderTest.OFFSET_1, SymbolsLineReaderTest.RANGE_LABEL_1), newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_3, SymbolsLineReaderTest.OFFSET_2, SymbolsLineReaderTest.OFFSET_3, SymbolsLineReaderTest.RANGE_LABEL_2)), newSymbol(newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_1, SymbolsLineReaderTest.OFFSET_0, SymbolsLineReaderTest.OFFSET_1, SymbolsLineReaderTest.RANGE_LABEL_1), newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_3, SymbolsLineReaderTest.OFFSET_0, SymbolsLineReaderTest.OFFSET_1, SymbolsLineReaderTest.RANGE_LABEL_1)));
        assertThat(symbolsLineReader.read(line1)).isEmpty();
        symbolsLineReader.read(line2);
        symbolsLineReader.read(line3);
        assertThat(line1.getSymbols()).isEqualTo(((SymbolsLineReaderTest.RANGE_LABEL_1) + ",1"));
        assertThat(line2.getSymbols()).isEqualTo(((SymbolsLineReaderTest.RANGE_LABEL_1) + ",2"));
        assertThat(line3.getSymbols()).isEqualTo(((((SymbolsLineReaderTest.RANGE_LABEL_1) + ",1;") + (SymbolsLineReaderTest.RANGE_LABEL_2)) + ",2"));
    }

    @Test
    public void read_symbols_defined_on_many_lines() {
        TextRange declaration = SymbolsLineReaderTest.newTextRange(SymbolsLineReaderTest.LINE_1, SymbolsLineReaderTest.LINE_2, SymbolsLineReaderTest.OFFSET_1, SymbolsLineReaderTest.OFFSET_3);
        Mockito.when(rangeOffsetConverter.offsetToString(declaration, SymbolsLineReaderTest.LINE_1, SymbolsLineReaderTest.DEFAULT_LINE_LENGTH)).thenReturn(SymbolsLineReaderTest.RANGE_LABEL_1);
        Mockito.when(rangeOffsetConverter.offsetToString(declaration, SymbolsLineReaderTest.LINE_2, SymbolsLineReaderTest.DEFAULT_LINE_LENGTH)).thenReturn(SymbolsLineReaderTest.RANGE_LABEL_2);
        TextRange reference = SymbolsLineReaderTest.newTextRange(SymbolsLineReaderTest.LINE_3, SymbolsLineReaderTest.LINE_4, SymbolsLineReaderTest.OFFSET_1, SymbolsLineReaderTest.OFFSET_3);
        Mockito.when(rangeOffsetConverter.offsetToString(reference, SymbolsLineReaderTest.LINE_3, SymbolsLineReaderTest.DEFAULT_LINE_LENGTH)).thenReturn(SymbolsLineReaderTest.RANGE_LABEL_1);
        Mockito.when(rangeOffsetConverter.offsetToString(reference, SymbolsLineReaderTest.LINE_4, SymbolsLineReaderTest.DEFAULT_LINE_LENGTH)).thenReturn(SymbolsLineReaderTest.RANGE_LABEL_2);
        SymbolsLineReader symbolsLineReader = newReader(newSymbol(declaration, reference));
        assertThat(symbolsLineReader.read(line1)).isEmpty();
        assertThat(symbolsLineReader.read(line2)).isEmpty();
        assertThat(symbolsLineReader.read(line3)).isEmpty();
        assertThat(symbolsLineReader.read(line4)).isEmpty();
        assertThat(line1.getSymbols()).isEqualTo(((SymbolsLineReaderTest.RANGE_LABEL_1) + ",1"));
        assertThat(line2.getSymbols()).isEqualTo(((SymbolsLineReaderTest.RANGE_LABEL_2) + ",1"));
        assertThat(line3.getSymbols()).isEqualTo(((SymbolsLineReaderTest.RANGE_LABEL_1) + ",1"));
        assertThat(line4.getSymbols()).isEqualTo(((SymbolsLineReaderTest.RANGE_LABEL_2) + ",1"));
    }

    @Test
    public void read_symbols_declared_on_a_whole_line() {
        TextRange declaration = SymbolsLineReaderTest.newTextRange(SymbolsLineReaderTest.LINE_1, SymbolsLineReaderTest.LINE_2, SymbolsLineReaderTest.OFFSET_0, SymbolsLineReaderTest.OFFSET_0);
        Mockito.when(rangeOffsetConverter.offsetToString(declaration, SymbolsLineReaderTest.LINE_1, SymbolsLineReaderTest.DEFAULT_LINE_LENGTH)).thenReturn(SymbolsLineReaderTest.RANGE_LABEL_1);
        Mockito.when(rangeOffsetConverter.offsetToString(declaration, SymbolsLineReaderTest.LINE_2, SymbolsLineReaderTest.DEFAULT_LINE_LENGTH)).thenReturn("");
        TextRange reference = newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_3, SymbolsLineReaderTest.OFFSET_1, SymbolsLineReaderTest.OFFSET_3, SymbolsLineReaderTest.RANGE_LABEL_2);
        SymbolsLineReader symbolsLineReader = newReader(newSymbol(declaration, reference));
        assertThat(symbolsLineReader.read(line1)).isEmpty();
        assertThat(symbolsLineReader.read(line2)).isEmpty();
        assertThat(symbolsLineReader.read(line3)).isEmpty();
        assertThat(symbolsLineReader.read(line4)).isEmpty();
        assertThat(line1.getSymbols()).isEqualTo(((SymbolsLineReaderTest.RANGE_LABEL_1) + ",1"));
        assertThat(line2.getSymbols()).isEmpty();
        assertThat(line3.getSymbols()).isEqualTo(((SymbolsLineReaderTest.RANGE_LABEL_2) + ",1"));
        assertThat(line4.getSymbols()).isEmpty();
    }

    @Test
    public void not_fail_and_stop_processing_when_range_offset_converter_throw_RangeOffsetConverterException() {
        TextRange declaration = SymbolsLineReaderTest.newTextRange(SymbolsLineReaderTest.LINE_1, SymbolsLineReaderTest.LINE_1, SymbolsLineReaderTest.OFFSET_1, SymbolsLineReaderTest.OFFSET_3);
        Mockito.doThrow(RangeOffsetConverterException.class).when(rangeOffsetConverter).offsetToString(declaration, SymbolsLineReaderTest.LINE_1, SymbolsLineReaderTest.DEFAULT_LINE_LENGTH);
        TextRange reference = newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_2, SymbolsLineReaderTest.OFFSET_1, SymbolsLineReaderTest.OFFSET_3, SymbolsLineReaderTest.RANGE_LABEL_2);
        SymbolsLineReader symbolsLineReader = newReader(newSymbol(declaration, reference));
        LineReader.ReadError readErrorLine1 = new LineReader.ReadError(SYMBOLS, SymbolsLineReaderTest.LINE_1);
        assertThat(symbolsLineReader.read(line1)).contains(readErrorLine1);
        assertThat(symbolsLineReader.read(line2)).contains(readErrorLine1);
        assertNoSymbol();
        assertThat(logTester.logs(WARN)).isNotEmpty();
    }

    @Test
    public void keep_existing_processed_symbols_when_range_offset_converter_throw_RangeOffsetConverterException() {
        TextRange declaration = newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_1, SymbolsLineReaderTest.OFFSET_1, SymbolsLineReaderTest.OFFSET_3, SymbolsLineReaderTest.RANGE_LABEL_2);
        TextRange reference = SymbolsLineReaderTest.newTextRange(SymbolsLineReaderTest.LINE_2, SymbolsLineReaderTest.LINE_2, SymbolsLineReaderTest.OFFSET_1, SymbolsLineReaderTest.OFFSET_3);
        Mockito.doThrow(RangeOffsetConverterException.class).when(rangeOffsetConverter).offsetToString(reference, SymbolsLineReaderTest.LINE_2, SymbolsLineReaderTest.DEFAULT_LINE_LENGTH);
        SymbolsLineReader symbolsLineReader = newReader(newSymbol(declaration, reference));
        assertThat(symbolsLineReader.read(line1)).isEmpty();
        assertThat(symbolsLineReader.read(line2)).contains(new LineReader.ReadError(SYMBOLS, SymbolsLineReaderTest.LINE_2));
        assertThat(line1.hasSymbols()).isTrue();
        assertThat(line2.hasSymbols()).isFalse();
        assertThat(logTester.logs(WARN)).isNotEmpty();
    }

    @Test
    public void display_file_key_in_warning_when_range_offset_converter_throw_RangeOffsetConverterException() {
        TextRange declaration = SymbolsLineReaderTest.newTextRange(SymbolsLineReaderTest.LINE_1, SymbolsLineReaderTest.LINE_1, SymbolsLineReaderTest.OFFSET_1, SymbolsLineReaderTest.OFFSET_3);
        Mockito.doThrow(RangeOffsetConverterException.class).when(rangeOffsetConverter).offsetToString(declaration, SymbolsLineReaderTest.LINE_1, SymbolsLineReaderTest.DEFAULT_LINE_LENGTH);
        SymbolsLineReader symbolsLineReader = newReader(newSymbol(declaration, newSingleLineTextRangeWithExpectedLabel(SymbolsLineReaderTest.LINE_2, SymbolsLineReaderTest.OFFSET_1, SymbolsLineReaderTest.OFFSET_3, SymbolsLineReaderTest.RANGE_LABEL_2)));
        assertThat(symbolsLineReader.read(line1)).contains(new LineReader.ReadError(SYMBOLS, SymbolsLineReaderTest.LINE_1));
        assertThat(logTester.logs(WARN)).containsOnly("Inconsistency detected in Symbols data. Symbols will be ignored for file 'FILE_KEY'");
    }
}

