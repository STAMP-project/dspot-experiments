/**
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.decisiontable.parser.csv;


import DataListener.NON_MERGED;
import java.util.HashMap;
import java.util.Map;
import org.drools.template.parser.DataListener;
import org.junit.Assert;
import org.junit.Test;


public class CsvParserTest {
    @Test
    public void testCsv() {
        final CsvParserTest.MockSheetListener listener = new CsvParserTest.MockSheetListener();
        final CsvLineParser lineParser = new CsvLineParser();
        final CsvParser parser = new CsvParser(listener, lineParser);
        parser.parseFile(getClass().getResourceAsStream("/data/TestCsv.csv"));
        Assert.assertEquals("A", listener.getCell(0, 0));
        Assert.assertEquals("B", listener.getCell(0, 1));
        Assert.assertEquals("", listener.getCell(2, 0));
        Assert.assertEquals("C", listener.getCell(1, 0));
        Assert.assertEquals("D", listener.getCell(1, 1));
        Assert.assertEquals("E", listener.getCell(1, 3));
    }

    /**
     * Test the handling of merged cells.
     */
    @Test
    public void testCellMergeHandling() {
        CsvParser parser = new CsvParser(((DataListener) (null)), null);
        Assert.assertEquals(NON_MERGED, parser.calcStartMerge(NON_MERGED, 1, "foo"));
        Assert.assertEquals(42, parser.calcStartMerge(NON_MERGED, 42, "..."));
        Assert.assertEquals(42, parser.calcStartMerge(42, 43, "..."));
        Assert.assertEquals(NON_MERGED, parser.calcStartMerge(42, 44, "VanHalen"));
        Assert.assertEquals("VanHalen", parser.calcCellText(NON_MERGED, "VanHalen"));
        Assert.assertEquals("VanHalen", parser.calcCellText(42, "VanHalen..."));
        Assert.assertEquals("", parser.calcCellText(42, "..."));
    }

    static class MockSheetListener implements DataListener {
        Map<String, String> data = new HashMap<String, String>();

        public String getCell(final int row, final int col) {
            return this.data.get(cellKey(row, col));
        }

        public void startSheet(final String name) {
        }

        public void finishSheet() {
        }

        public void newRow(final int rowNumber, final int columns) {
        }

        public void newCell(final int row, final int column, final String value, final int mergeCellStart) {
            this.data.put(cellKey(row, column), value);
        }

        String cellKey(final int row, final int column) {
            return (("R" + row) + "C") + column;
        }
    }
}

