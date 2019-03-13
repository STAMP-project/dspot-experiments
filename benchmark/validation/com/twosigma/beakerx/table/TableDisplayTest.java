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


import CSV.TIME_COLUMN;
import Color.ORANGE;
import Color.PINK;
import ColumnType.Double;
import ColumnType.Time;
import DataBarsRendererSerializer.INCLUDE_TEXT;
import DataBarsRendererSerializer.VALUE_DATA_BARS;
import DateSerializer.TIMESTAMP;
import DateSerializer.VALUE_DATE;
import HeatmapHighlighterSerializer.STYLE;
import HeatmapHighlighterSerializer.TYPE;
import TableDisplay.DICTIONARY_SUBTYPE;
import TableDisplay.LIST_OF_MAPS_SUBTYPE;
import TableDisplay.MODEL;
import TableDisplay.MODEL_NAME_VALUE;
import TableDisplay.TABLE_DISPLAY_SUBTYPE;
import TableDisplay.VIEW_NAME_VALUE;
import TableDisplayAlignmentProvider.CENTER_ALIGNMENT;
import TableDisplayCellHighlighter.FULL_ROW;
import TableDisplayCellHighlighter.SINGLE_COLUMN;
import ThreeColorHeatmapHighlighterSerializer.MID_COLOR;
import ThreeColorHeatmapHighlighterSerializer.MID_VAL;
import TimeStringFormatSerializer.HUMAN_FRIENDLY;
import TimeStringFormatSerializer.UNIT;
import TimeStringFormatSerializer.VALUE_TIME;
import UniqueEntriesHighlighterSerializer.COL_NAME;
import ValueHighlighterSerializer.COLORS;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.chart.Color;
import com.twosigma.beakerx.fileloader.CSV;
import com.twosigma.beakerx.fileloader.CSVTest;
import com.twosigma.beakerx.table.format.TableDisplayStringFormat;
import com.twosigma.beakerx.table.highlight.HeatmapHighlighter;
import com.twosigma.beakerx.table.highlight.TableDisplayCellHighlighter;
import com.twosigma.beakerx.table.highlight.ThreeColorHeatmapHighlighter;
import com.twosigma.beakerx.table.highlight.UniqueEntriesHighlighter;
import com.twosigma.beakerx.table.highlight.ValueHighlighter;
import com.twosigma.beakerx.table.renderer.TableDisplayCellRenderer;
import com.twosigma.beakerx.table.serializer.ObservableTableDisplaySerializer;
import com.twosigma.beakerx.table.serializer.TableDisplaySerializer;
import com.twosigma.beakerx.table.serializer.ValueStringFormatSerializer;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;

import static TableDisplayAlignmentProvider.CENTER_ALIGNMENT;


public class TableDisplayTest {
    public static final String COL_1 = "str1";

    public static final String COL_3 = "str3";

    protected KernelTest kernel;

    private TableDisplay tableDisplay;

    @Test
    public void shouldSendCommMsgWhenAlignmentProviderForColumnChange() throws Exception {
        // given
        TableDisplayAlignmentProvider centerAlignment = CENTER_ALIGNMENT;
        // when
        tableDisplay.setAlignmentProviderForColumn(TableDisplayTest.COL_1, centerAlignment);
        // then
        assertThat(tableDisplay.getAlignmentForColumn().get(TableDisplayTest.COL_1)).isEqualTo(centerAlignment);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        Map actual = getValueAsMap(model, TableDisplaySerializer.ALIGNMENT_FOR_COLUMN);
        String value = ((String) (actual.get(TableDisplayTest.COL_1)));
        assertThat(value).isEqualTo(CENTER_ALIGNMENT.toString());
    }

    @Test
    public void shouldSendCommMsgWhenAlignmentProviderForTypeChange() throws Exception {
        // given
        TableDisplayAlignmentProvider centerAlignment = CENTER_ALIGNMENT;
        // when
        tableDisplay.setAlignmentProviderForType(ColumnType.String, centerAlignment);
        // then
        assertThat(tableDisplay.getAlignmentForType().get(ColumnType.String)).isEqualTo(centerAlignment);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        Map actual = ((Map) (model.get(TableDisplaySerializer.ALIGNMENT_FOR_TYPE)));
        assertThat(actual.get(ColumnType.String.toString())).isEqualTo(centerAlignment.toString());
    }

    @Test
    public void shouldSendCommMsgWhenColumnFrozenChange() throws Exception {
        // given
        // when
        tableDisplay.setColumnFrozen(TableDisplayTest.COL_1, true);
        // then
        assertThat(tableDisplay.getColumnsFrozen().get(TableDisplayTest.COL_1)).isEqualTo(true);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        Map actual = ((Map) (model.get(TableDisplaySerializer.COLUMNS_FROZEN)));
        assertThat(actual.get(TableDisplayTest.COL_1)).isEqualTo(true);
    }

    @Test
    public void shouldSendCommMsgWhenColumnFrozenRightChange() throws Exception {
        // given
        // when
        tableDisplay.setColumnFrozenRight(TableDisplayTest.COL_1, true);
        // then
        assertThat(tableDisplay.getColumnsFrozenRight().get(TableDisplayTest.COL_1)).isEqualTo(true);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        Map actual = ((Map) (model.get(TableDisplaySerializer.COLUMNS_FROZEN_RIGHT)));
        assertThat(actual.get(TableDisplayTest.COL_1)).isEqualTo(true);
    }

    @Test
    public void shouldSendCommMsgWhenColumnOrderChange() throws Exception {
        // given
        // when
        tableDisplay.setColumnOrder(TableDisplayTest.getStringList());
        // then
        assertThat(tableDisplay.getColumnOrder()).isEqualTo(TableDisplayTest.getStringList());
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(TableDisplaySerializer.COLUMN_ORDER)).isEqualTo(TableDisplayTest.getStringList());
    }

    @Test
    public void shouldSendCommMsgWhenColumnVisibleChange() throws Exception {
        // given
        // when
        tableDisplay.setColumnVisible(TableDisplayTest.COL_1, true);
        // then
        assertThat(tableDisplay.getColumnsVisible().get(TableDisplayTest.COL_1)).isEqualTo(true);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        assertThat(getValueAsMap(model, TableDisplaySerializer.COLUMNS_VISIBLE).get(TableDisplayTest.COL_1)).isEqualTo(true);
    }

    @Test
    public void shouldSendCommMsgWhenDataFontSizeChange() throws Exception {
        // given
        // when
        tableDisplay.setDataFontSize(12);
        // then
        assertThat(tableDisplay.getDataFontSize()).isEqualTo(12);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(TableDisplaySerializer.DATA_FONT_SIZE)).isEqualTo(12);
    }

    @Test
    public void shouldSendCommMsgWhenDoubleClickActionChange() throws Exception {
        // given
        // when
        tableDisplay.setDoubleClickAction(new Object());
        // then
        assertThat(tableDisplay.hasDoubleClickAction()).isEqualTo(true);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(2);
        assertThat(model.get(ObservableTableDisplaySerializer.HAS_DOUBLE_CLICK_ACTION)).isEqualTo(true);
    }

    @Test
    public void shouldSendCommMsgWhenDoubleClickActionTagChange() throws Exception {
        // given
        String clickTag = "ClickTag";
        // when
        tableDisplay.setDoubleClickAction(clickTag);
        // then
        assertThat(tableDisplay.getDoubleClickTag()).isEqualTo(clickTag);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(2);
        assertThat(model.get(ObservableTableDisplaySerializer.DOUBLE_CLICK_TAG)).isEqualTo(clickTag);
    }

    @Test
    public void shouldSendCommMsgWhenHasIndexChange() throws Exception {
        // given
        String index1 = "index1";
        // when
        tableDisplay.setHasIndex(index1);
        // then
        assertThat(tableDisplay.getHasIndex()).isEqualTo(index1);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(TableDisplaySerializer.HAS_INDEX)).isEqualTo(index1);
    }

    @Test
    public void shouldSendCommMsgWhenHeaderFontSizeChange() throws Exception {
        // given
        // when
        tableDisplay.setHeaderFontSize(12);
        // then
        assertThat(tableDisplay.getHeaderFontSize()).isEqualTo(12);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(TableDisplaySerializer.HEADER_FONT_SIZE)).isEqualTo(12);
    }

    @Test
    public void shouldSendCommMsgWhenHeadersVerticalChange() throws Exception {
        // given
        // when
        tableDisplay.setHeadersVertical(true);
        // then
        assertThat(tableDisplay.getHeadersVertical()).isEqualTo(true);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(TableDisplaySerializer.HEADERS_VERTICAL)).isEqualTo(true);
    }

    @Test
    public void shouldSendCommMsgWhenAddHeatmapHighlighterForColumnChange() throws Exception {
        // given;
        // when
        TableDisplayCellHighlighter heatmapHighlighter = TableDisplayCellHighlighter.getHeatmapHighlighter(TableDisplayTest.COL_1, FULL_ROW);
        tableDisplay.addCellHighlighter(heatmapHighlighter);
        // then
        assertThat(tableDisplay.getCellHighlighters().get(0)).isEqualTo(heatmapHighlighter);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        List actual = getValueAsList(model, TableDisplaySerializer.CELL_HIGHLIGHTERS);
        Map column = ((Map) (actual.get(0)));
        assertThat(column.get(TYPE)).isEqualTo(HeatmapHighlighter.class.getSimpleName());
        assertThat(column.get(STYLE)).isEqualTo(FULL_ROW.toString());
    }

    @Test
    public void shouldSendCommMsgWhenAddThreeColorHighlighterForColumnChange() throws Exception {
        // given;
        ThreeColorHeatmapHighlighter highlighter = new ThreeColorHeatmapHighlighter(TableDisplayTest.COL_1, TableDisplayCellHighlighter.SINGLE_COLUMN, 4, 6, 8, new Color(247, 106, 106), new Color(239, 218, 82), new Color(100, 189, 122));
        // when
        tableDisplay.addCellHighlighter(highlighter);
        // then
        assertThat(tableDisplay.getCellHighlighters().get(0)).isEqualTo(highlighter);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        List actual = getValueAsList(model, TableDisplaySerializer.CELL_HIGHLIGHTERS);
        Map column = ((Map) (actual.get(0)));
        assertThat(column.get(ThreeColorHeatmapHighlighterSerializer.TYPE)).isEqualTo(ThreeColorHeatmapHighlighter.class.getSimpleName());
        assertThat(column.get(ThreeColorHeatmapHighlighterSerializer.STYLE)).isEqualTo(SINGLE_COLUMN.toString());
        assertThat(column.get(MID_VAL)).isEqualTo(6);
        assertThat(column.get(MID_COLOR)).isNotNull();
    }

    @Test
    public void shouldSendCommMsgWhenAddUniqueEntriesHighlighterForColumnChange() throws Exception {
        // given;
        TableDisplayCellHighlighter highlighter = TableDisplayCellHighlighter.getUniqueEntriesHighlighter(TableDisplayTest.COL_1, FULL_ROW);
        // when
        tableDisplay.addCellHighlighter(highlighter);
        // then
        assertThat(tableDisplay.getCellHighlighters().get(0)).isEqualTo(highlighter);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        List actual = getValueAsList(model, TableDisplaySerializer.CELL_HIGHLIGHTERS);
        Map column = ((Map) (actual.get(0)));
        assertThat(column.get(UniqueEntriesHighlighterSerializer.TYPE)).isEqualTo(UniqueEntriesHighlighter.class.getSimpleName());
        assertThat(column.get(UniqueEntriesHighlighterSerializer.STYLE)).isEqualTo(FULL_ROW.toString());
        assertThat(column.get(COL_NAME)).isEqualTo(TableDisplayTest.COL_1);
    }

    @Test
    public void shouldSendCommMsgWhenAddValueHighlighterForColumnChange() throws Exception {
        // given;
        ValueHighlighter highlighter = new ValueHighlighter(TableDisplayTest.COL_1, Arrays.asList(new Color(247, 106, 106)));
        // when
        tableDisplay.addCellHighlighter(highlighter);
        // then
        assertThat(tableDisplay.getCellHighlighters().get(0)).isEqualTo(highlighter);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        List actual = getValueAsList(model, TableDisplaySerializer.CELL_HIGHLIGHTERS);
        Map column = ((Map) (actual.get(0)));
        assertThat(column.get(ValueHighlighterSerializer.TYPE)).isEqualTo(ValueHighlighter.class.getSimpleName());
        assertThat(column.get(ValueHighlighterSerializer.COL_NAME)).isEqualTo(TableDisplayTest.COL_1);
        assertThat(column.get(COLORS)).isNotNull();
    }

    @Test
    public void shouldSendCommMsgWhenAddValueHighlighterClosureForColumnChange() throws Exception {
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
        List actual = getValueAsList(getModelUpdate(), TableDisplaySerializer.CELL_HIGHLIGHTERS);
        Map column = ((Map) (actual.get(0)));
        assertThat(column.get(ValueHighlighterSerializer.TYPE)).isEqualTo(ValueHighlighter.class.getSimpleName());
    }

    @Test
    public void shouldSendCommMsgWhenSetToolTipClojureChange() throws Exception {
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
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        List valueAsList = getValueAsList(model, TableDisplaySerializer.TOOLTIPS);
        assertThat(valueAsList.get(0)).isNotNull();
        assertThat(valueAsList.get(1)).isNotNull();
    }

    @Test
    public void shouldSendCommMsgWhenSetFontColorProviderClojureChange() throws Exception {
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
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        List colors = getValueAsList(model, TableDisplaySerializer.FONT_COLOR);
        List actual = ((List) (colors.get(0)));
        assertThat(actual.get(0).toString()).startsWith("#");
    }

    @Test
    public void shouldSendCommMsgWhenSetRowFilterClojureChange() throws Exception {
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
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        List filteredValues = getValueAsList(model, TableDisplaySerializer.FILTERED_VALUES);
        assertThat(filteredValues).isNotEmpty();
    }

    @Test
    public void shouldSendCommMsgWhenRendererForColumnChange() throws Exception {
        // given
        TableDisplayCellRenderer dataBarsRenderer = TableDisplayCellRenderer.getDataBarsRenderer();
        // when
        tableDisplay.setRendererForColumn(TableDisplayTest.COL_1, dataBarsRenderer);
        // then
        assertThat(tableDisplay.getRendererForColumn().get(TableDisplayTest.COL_1)).isEqualTo(dataBarsRenderer);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        Map actual = getValueAsMap(model, TableDisplaySerializer.RENDERER_FOR_COLUMN);
        Map column = getValueAsMap(actual, TableDisplayTest.COL_1);
        assertThat(column.get(DataBarsRendererSerializer.TYPE)).isEqualTo(VALUE_DATA_BARS);
        assertThat(column.get(INCLUDE_TEXT)).isEqualTo(true);
    }

    @Test
    public void shouldSendCommMsgWhenRendererForTypeChange() throws Exception {
        // given
        TableDisplayCellRenderer dataBarsRenderer = TableDisplayCellRenderer.getDataBarsRenderer();
        // when
        tableDisplay.setRendererForType(ColumnType.String, dataBarsRenderer);
        // then
        assertThat(tableDisplay.getRendererForType().get(ColumnType.String)).isEqualTo(dataBarsRenderer);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        Map actual = ((Map) (((Map) (model.get(TableDisplaySerializer.RENDERER_FOR_TYPE))).get(ColumnType.String.toString())));
        assertThat(actual.get(DataBarsRendererSerializer.TYPE)).isEqualTo(VALUE_DATA_BARS);
        assertThat(actual.get(INCLUDE_TEXT)).isEqualTo(true);
    }

    @Test
    public void shouldSendCommMsgWhenStringFormatForColumnChange() throws Exception {
        // given
        TableDisplayStringFormat timeFormat = TableDisplayStringFormat.getTimeFormat();
        // when
        tableDisplay.setStringFormatForColumn(TableDisplayTest.COL_1, timeFormat);
        // then
        assertThat(tableDisplay.getStringFormatForColumn().get(TableDisplayTest.COL_1)).isEqualTo(timeFormat);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        Map column = ((Map) (((Map) (model.get(TableDisplaySerializer.STRING_FORMAT_FOR_COLUMN))).get(TableDisplayTest.COL_1)));
        assertThat(column.get(TimeStringFormatSerializer.TYPE)).isEqualTo(VALUE_TIME);
        assertThat(column.get(UNIT)).isEqualTo(TimeUnit.MILLISECONDS.toString());
        assertThat(column.get(HUMAN_FRIENDLY)).isEqualTo(false);
    }

    @Test
    public void shouldSendCommMsgWhenClojureFormatForColumnChange() throws Exception {
        // given
        // when
        tableDisplay.setStringFormatForColumn(TableDisplayTest.COL_1, new ClosureTest() {
            @Override
            public String call(Object value, Object row, Object col, Object tableDisplay) {
                return ((float) (value)) < 8 ? ":(" : ":)";
            }

            @Override
            public int getMaximumNumberOfParameters() {
                return 4;
            }
        });
        // then
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        Map actual = getValueAsMap(model, TableDisplaySerializer.STRING_FORMAT_FOR_COLUMN);
        Map column = getValueAsMap(actual, TableDisplayTest.COL_1);
        Map values = getValueAsMap(column, TableDisplaySerializer.VALUES);
        String type = ((String) (column.get(TableDisplaySerializer.TYPE)));
        assertThat(type).isEqualTo(ValueStringFormatSerializer.VALUE_STRING);
        ArrayList valuesForColumn = ((ArrayList) (values.get(TableDisplayTest.COL_1)));
        assertThat(valuesForColumn.get(0)).isEqualTo(":(");
        assertThat(valuesForColumn.get(1)).isEqualTo(":(");
    }

    @Test
    public void shouldSendCommMsgWhenStringFormatForTimesChange() throws Exception {
        // given
        TimeUnit days = TimeUnit.DAYS;
        // when
        tableDisplay.setStringFormatForTimes(days);
        // then
        assertThat(tableDisplay.getStringFormatForTimes()).isEqualTo(days);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        Map time = ((Map) (((Map) (model.get(TableDisplaySerializer.STRING_FORMAT_FOR_TYPE))).get(Time.toString())));
        assertThat(time.get("unit")).isEqualTo(days.toString());
    }

    @Test
    public void shouldSendCommMsgWhenStringFormatForTypeChange() throws Exception {
        // given
        TableDisplayStringFormat timeFormat = TableDisplayStringFormat.getTimeFormat();
        // when
        tableDisplay.setStringFormatForType(ColumnType.String, timeFormat);
        // then
        assertThat(tableDisplay.getStringFormatForType()).isNotNull();
        Map actual = getValueAsMap(getModelUpdate(), TableDisplaySerializer.STRING_FORMAT_FOR_TYPE);
        Map column = getValueAsMap(actual, ColumnType.String.toString());
        assertThat(column.get(TimeStringFormatSerializer.TYPE)).isEqualTo(VALUE_TIME);
        assertThat(column.get(UNIT)).isNotNull();
        assertThat(column.get(HUMAN_FRIENDLY)).isNotNull();
    }

    @Test
    public void shouldSendCommMsgWhenDecimalFormatForTypeChange() throws Exception {
        // given
        TableDisplayStringFormat decimalFormat = TableDisplayStringFormat.getDecimalFormat(9, 9);
        // when
        tableDisplay.setStringFormatForType(Double, decimalFormat);
        kernel.clearMessages();
        tableDisplay.setStringFormatForType(ColumnType.String, decimalFormat);
        // then
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        Map actual = getValueAsMap(model, TableDisplaySerializer.STRING_FORMAT_FOR_TYPE);
        verifyDecimalFormat(actual, Double.toString());
        verifyDecimalFormat(actual, ColumnType.String.toString());
    }

    @Test
    public void shouldSendCommMsgWhenTimeZoneChange() throws Exception {
        // given
        String timezone = "TZ1";
        // when
        tableDisplay.setTimeZone(timezone);
        // then
        assertThat(tableDisplay.getTimeZone()).isEqualTo(timezone);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(TableDisplaySerializer.TIME_ZONE)).isEqualTo(timezone);
    }

    @Test
    public void shouldSendCommMsgWithAllModelWhenDisplay() throws Exception {
        // given
        kernel.clearMessages();
        ArrayList<Map<String, Object>> v = new ArrayList<>();
        TableDisplay tableDisplay = new TableDisplay(v);
        // when
        tableDisplay.display();
        // then
        TestWidgetUtils.verifyOpenCommMsgWitoutLayout(kernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE);
        Map valueForProperty = TestWidgetUtils.getValueForProperty(kernel.getPublishedMessages().get(1), MODEL, Map.class);
        assertThat(valueForProperty.get(TableDisplaySerializer.TYPE)).isEqualTo(TableDisplaySerializer.TABLE_DISPLAY);
        assertThat(tableDisplay.getValues()).isEqualTo(v);
        LinkedHashMap model = getModel();
        assertThat(model.get(TableDisplaySerializer.VALUES)).isNotNull();
    }

    @Test
    public void createWithMultipleTypesPerColumnParam_hasSafeTypes() throws Exception {
        TableDisplay tableDisplay = new TableDisplay(TableDisplayTest.getListOfMapsWithInconsistentTypes());
        assertThat(tableDisplay.getSubtype()).isEqualTo(LIST_OF_MAPS_SUBTYPE);
        List<String> expectedValues = Arrays.asList("string", "string", "string");
        assertThat(tableDisplay.getTypes()).isEqualTo(expectedValues);
    }

    @Test
    public void createWithMultipleTypesPerColumnParam_hasSafeTypesInOrder() throws Exception {
        TableDisplay tableDisplay = new TableDisplay(TableDisplayTest.getListOfMapsWithMostlyInconsistentTypes());
        assertThat(tableDisplay.getSubtype()).isEqualTo(LIST_OF_MAPS_SUBTYPE);
        System.out.println(tableDisplay.getTypes());
        List<String> expectedValues = Arrays.asList("string", "string", "double");
        assertThat(tableDisplay.getTypes()).isEqualTo(expectedValues);
    }

    @Test
    public void createWithUndefinedTypes() throws Exception {
        TableDisplay tableDisplay = new TableDisplay(TableDisplayTest.getListOfMapsWithEmptyTypes());
        assertThat(tableDisplay.getSubtype()).isEqualTo(LIST_OF_MAPS_SUBTYPE);
        System.out.println(tableDisplay.getTypes());
        List<String> expectedValues = Arrays.asList("string", "double", "integer");
        assertThat(tableDisplay.getTypes()).isEqualTo(expectedValues);
    }

    @Test
    public void createWithListOfMapsParam_hasListOfMapsSubtype() throws Exception {
        // when
        TableDisplay tableDisplay = new TableDisplay(TableDisplayTest.getListOfMapsData());
        // then
        assertThat(tableDisplay.getSubtype()).isEqualTo(LIST_OF_MAPS_SUBTYPE);
        assertThat(tableDisplay.getValues().size()).isEqualTo(2);
        assertThat(tableDisplay.getColumnNames().size()).isEqualTo(3);
        assertThat(tableDisplay.getTypes().size()).isEqualTo(3);
    }

    @Test
    public void createWithListsParams_hasTableDisplaySubtype() throws Exception {
        // when
        TableDisplay tableDisplay = new TableDisplay(Arrays.asList(TableDisplayTest.getRowData(), TableDisplayTest.getRowData()), TableDisplayTest.getStringList(), TableDisplayTest.getStringList());
        // then
        assertThat(tableDisplay.getSubtype()).isEqualTo(TABLE_DISPLAY_SUBTYPE);
        assertThat(tableDisplay.getValues().size()).isEqualTo(2);
        assertThat(tableDisplay.getColumnNames().size()).isEqualTo(3);
        assertThat(tableDisplay.getTypes().size()).isEqualTo(3);
    }

    @Test
    public void createTableDisplayForMap_hasDictionarySubtype() throws Exception {
        // when
        TableDisplay tableDisplay = new TableDisplay(getMapData());
        // then
        assertThat(tableDisplay.getSubtype()).isEqualTo(DICTIONARY_SUBTYPE);
        assertThat(tableDisplay.getValues().size()).isEqualTo(3);
        assertThat(tableDisplay.getColumnNames().size()).isEqualTo(2);
        assertThat(tableDisplay.getTypes().size()).isEqualTo(0);
    }

    @Test
    public void createTableDisplay_hasCommIsNotNull() throws Exception {
        // when
        TableDisplay tableDisplay = new TableDisplay(TableDisplayTest.getListOfMapsData());
        // then
        assertThat(tableDisplay.getComm()).isNotNull();
    }

    @Test
    public void getValuesAsRowsWithoutParams_returnedListOfMapsIsNotEmpty() throws Exception {
        // given
        TableDisplay tableDisplay = new TableDisplay(TableDisplayTest.getListOfMapsData());
        // when
        List<Map<String, Object>> rows = tableDisplay.getValuesAsRows();
        // then
        assertThat(rows.size()).isEqualTo(2);
        assertThat(rows.get(0).size()).isEqualTo(3);
    }

    @Test
    public void getValuesAsRowsWithTwoParams_returnedListOfMapsIsNotEmpty() throws Exception {
        // when
        List<Map<String, Object>> rows = TableDisplay.getValuesAsRows(Arrays.asList(TableDisplayTest.getRowData(), TableDisplayTest.getRowData()), TableDisplayTest.getStringList());
        // then
        assertThat(rows.size()).isEqualTo(2);
        assertThat(rows.get(0).size()).isEqualTo(3);
    }

    @Test
    public void getValuesAsMatrixWithoutParams_returnedListOfListIsNotEmpty() throws Exception {
        // given
        TableDisplay tableDisplay = new TableDisplay(TableDisplayTest.getListOfMapsData());
        // when
        List<List<?>> values = tableDisplay.getValuesAsMatrix();
        // then
        assertThat(values).isNotEmpty();
    }

    @Test
    public void getValuesAsMatrixWithParam_returnedListOfListIsNotEmpty() throws Exception {
        // when
        List<List<?>> values = TableDisplay.getValuesAsMatrix(Arrays.asList(TableDisplayTest.getStringList(), TableDisplayTest.getRowData()));
        // then
        assertThat(values).isNotEmpty();
    }

    @Test
    public void getValuesAsDictionaryWithoutParam_returnedMapIsNotEmpty() throws Exception {
        // given
        TableDisplay tableDisplay = new TableDisplay(getMapData());
        // when
        Map<String, Object> dictionary = tableDisplay.getValuesAsDictionary();
        // then
        assertThat(dictionary).isNotEmpty();
    }

    @Test
    public void getValuesAsDictionaryWithParam_returnedMapIsNotEmpty() throws Exception {
        // when
        Map<String, Object> dictionary = TableDisplay.getValuesAsDictionary(Arrays.asList(Arrays.asList("k1", 1), Arrays.asList("k2", 2)));
        // then
        assertThat(dictionary).isNotEmpty();
    }

    @Test
    public void shouldContainTime() throws Exception {
        // given
        List<Map<String, Object>> data = new CSV().read(CSVTest.getOsAppropriatePath(getClass().getClassLoader(), CSVTest.TABLE_ROWS_TEST_CSV));
        TableDisplay tableDisplay = new TableDisplay(data);
        // when
        tableDisplay.display();
        // then
        assertThat(tableDisplay.getTypes()).contains(TIME_COLUMN);
        LinkedHashMap model = getModel();
        List values = ((List) (model.get(TableDisplaySerializer.VALUES)));
        List row0 = ((List) (values.get(0)));
        Map date = ((Map) (row0.get(7)));
        assertThat(date.get(DateSerializer.TYPE)).isEqualTo(VALUE_DATE);
        assertThat(date.get(TIMESTAMP)).isNotNull();
    }

    @Test
    public void shouldSendCommMsgWhenRemoveAllCellHighlighters() throws Exception {
        // given;
        TableDisplayCellHighlighter uniqueEntriesHighlighter = TableDisplayCellHighlighter.getUniqueEntriesHighlighter(TableDisplayTest.COL_1, FULL_ROW);
        TableDisplayCellHighlighter heatmapHighlighter = TableDisplayCellHighlighter.getHeatmapHighlighter(TableDisplayTest.COL_1, 0, 8, ORANGE, PINK);
        ThreeColorHeatmapHighlighter colorHeatmapHighlighter = new ThreeColorHeatmapHighlighter(TableDisplayTest.COL_1, TableDisplayCellHighlighter.SINGLE_COLUMN, 4, 6, 8, new Color(247, 106, 106), new Color(239, 218, 82), new Color(100, 189, 122));
        tableDisplay.addCellHighlighter(uniqueEntriesHighlighter);
        tableDisplay.addCellHighlighter(heatmapHighlighter);
        tableDisplay.addCellHighlighter(colorHeatmapHighlighter);
        kernel.clearMessages();
        // when
        tableDisplay.removeAllCellHighlighters();
        // then
        assertThat(tableDisplay.getCellHighlighters()).isEmpty();
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        List actual = getValueAsList(model, TableDisplaySerializer.CELL_HIGHLIGHTERS);
        assertThat(actual).isEmpty();
    }

    @Test
    public void shouldUpdateCellByColumnName() throws Exception {
        // given
        // when
        tableDisplay.updateCell(0, TableDisplayTest.COL_3, 121);
        // then
        int indexOfCol3 = tableDisplay.getColumnNames().indexOf(TableDisplayTest.COL_3);
        assertThat(tableDisplay.getValues().get(0).get(indexOfCol3)).isEqualTo(121);
    }

    @Test
    public void shouldThrowExceptionWhenUpdateCellByNotExistingColumnName() throws Exception {
        // given
        // when
        try {
            tableDisplay.updateCell(0, "UnknownColumnName", 121);
            Assert.fail("Should not update cell for unknown column name");
        } catch (Exception e) {
            // then
            assertThat(e.getMessage()).contains("UnknownColumnName");
        }
    }
}

