/**
 * Copyright 2017 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.table;


import Color.BLUE;
import Color.GREEN;
import com.twosigma.beakerx.chart.Color;
import com.twosigma.beakerx.table.format.ValueStringFormat;
import com.twosigma.beakerx.table.highlight.ValueHighlighter;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class TableDisplayClosureTest {
    private TableDisplay tableDisplay;

    @Test
    public void setStringFormatForColumnClosure_shouldFormatValues() throws Exception {
        String colName = "col1";
        // when
        tableDisplay.setStringFormatForColumn(colName, new ClosureTest() {
            @Override
            public String call(Object value, Object row, Object col, Object tableDisplay) {
                return ((int) (value)) < 8 ? ":(" : ":)";
            }

            @Override
            public int getMaximumNumberOfParameters() {
                return 4;
            }
        });
        // then
        ValueStringFormat values = ((ValueStringFormat) (tableDisplay.getStringFormatForColumn().get(colName)));
        Assertions.assertThat(values.getValues().get(colName).get(0)).isEqualTo(":(");
        Assertions.assertThat(values.getValues().get(colName).get(1)).isEqualTo(":)");
    }

    @Test
    public void addCellHighlighterClosure_shouldFormatCellColors() throws Exception {
        // when
        tableDisplay.addCellHighlighter(new ClosureTest() {
            @Override
            public Color call(Object row, Object col, Object tbl) {
                return (((int) (row)) % 2) == 0 ? Color.GREEN : Color.BLUE;
            }

            @Override
            public int getMaximumNumberOfParameters() {
                return 3;
            }
        });
        // then
        ValueHighlighter highlighter = ((ValueHighlighter) (tableDisplay.getCellHighlighters().get(0)));
        Assertions.assertThat(highlighter.getColors().get(0)).isEqualTo(GREEN);
        Assertions.assertThat(highlighter.getColors().get(1)).isEqualTo(BLUE);
    }

    @Test
    public void setToolTipClosure_shouldFormatCellToolTips() throws Exception {
        // when
        tableDisplay.setToolTip(new ClosureTest() {
            @Override
            public String call(Object row, Object col, Object tbl) {
                return (((int) (row)) % 2) == 0 ? "even row" : "odd row";
            }

            @Override
            public int getMaximumNumberOfParameters() {
                return 3;
            }
        });
        // then
        List<List<String>> tooltips = tableDisplay.getTooltips();
        Assertions.assertThat(tooltips.get(0).get(0)).isEqualTo("even row");
        Assertions.assertThat(tooltips.get(1).get(0)).isEqualTo("odd row");
    }

    @Test
    public void setFontColorProviderClosure_shouldFormatFontColor() throws Exception {
        // when
        tableDisplay.setFontColorProvider(new ClosureTest() {
            @Override
            public Color call(Object row, Object col, Object tbl) {
                return (((int) (row)) % 2) == 0 ? Color.GREEN : Color.BLUE;
            }

            @Override
            public int getMaximumNumberOfParameters() {
                return 3;
            }
        });
        // then
        List<List<Color>> fontColor = tableDisplay.getFontColor();
        Assertions.assertThat(fontColor.get(0).get(0)).isEqualTo(GREEN);
        Assertions.assertThat(fontColor.get(1).get(0)).isEqualTo(BLUE);
    }

    @Test
    public void setRowFilterClosure_shouldFilterRows() throws Exception {
        // when
        tableDisplay.setRowFilter(new ClosureTest() {
            @Override
            public Boolean call(Object row, Object tbl) {
                return ((int) (row)) == 1;
            }

            @Override
            public int getMaximumNumberOfParameters() {
                return 2;
            }
        });
        // then
        List<List<?>> fValues = tableDisplay.getFilteredValues();
        Assertions.assertThat(fValues.size()).isEqualTo(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setStringFormatForColumnEmptyColName_throwIllegalArgumentException() throws Exception {
        // when
        tableDisplay.setStringFormatForColumn("", new Object());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setStringFormatForColumnWithEmptyObject_throwIllegalArgumentException() throws Exception {
        // when
        tableDisplay.setStringFormatForColumn("colName", new Object());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addCellHighlighterWithEmptyObject_throwIllegalArgumentException() throws Exception {
        // when
        tableDisplay.addCellHighlighter(new Object());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setToolTipWithEmptyObject_throwIllegalArgumentException() throws Exception {
        // when
        tableDisplay.setToolTip(new Object());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setFontColorProviderWithEmptyObject_throwIllegalArgumentException() throws Exception {
        // when
        tableDisplay.setFontColorProvider(new Object());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setRowFilterWithEmptyObject_throwIllegalArgumentException() throws Exception {
        // when
        tableDisplay.setRowFilter(new Object());
    }
}

