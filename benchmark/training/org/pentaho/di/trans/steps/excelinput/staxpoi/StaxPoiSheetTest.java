/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.steps.excelinput.staxpoi;


import KCellType.BOOLEAN;
import KCellType.DATE;
import KCellType.LABEL;
import KCellType.NUMBER;
import KCellType.NUMBER_FORMULA;
import KCellType.STRING_FORMULA;
import java.util.Collections;
import java.util.Date;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.spreadsheet.KCell;
import org.pentaho.di.core.spreadsheet.KSheet;


public class StaxPoiSheetTest {
    private static final String BP_SHEET = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + ((("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" " + "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">") + "%s") + "</worksheet>");

    private static final String SHEET_DATE_NO_V = String.format(StaxPoiSheetTest.BP_SHEET, (" <dimension ref=\"A1:A3\"/>" + ((((((((((" <sheetData>" + "   <row r=\"1\" spans=\"1:1\">") + "     <c r=\"A1\" s=\"1\" t=\"s\"><v>0</v></c>") + "   </row>") + "   <row r=\"2\" spans=\"1:1\">") + "     <c r=\"A2\" s=\"2\"><v>42248</v></c>") + "   </row>") + "   <row r=\"3\" spans=\"1:1\">") + "     <c r=\"A3\" s=\"2\"/>") + "   </row>") + " </sheetData>")));

    private static final String SHEET_1 = String.format(StaxPoiSheetTest.BP_SHEET, (" <dimension ref=\"B2:F5\"/>" + (((((((((" <sheetData>" + "  <row r=\"2\" spans=\"2:6\"><c r=\"B2\" t=\"s\"><v>0</v></c><c r=\"C2\" t=\"s\"><v>1</v></c>") + "    <c r=\"D2\" t=\"s\"><v>2</v></c><c r=\"E2\" t=\"s\"><v>3</v></c><c r=\"F2\" t=\"s\"><v>4</v></c></row>") + "  <row r=\"3\" spans=\"2:6\"><c r=\"B3\" t=\"s\"><v>5</v></c><c r=\"C3\" s=\"1\"><v>40428</v></c>") + "    <c r=\"D3\"><v>75</v></c><c r=\"E3\" t=\"b\"><v>1</v></c><c r=\"F3\"><f>D3</f><v>75</v></c></row>") + "  <row r=\"4\" spans=\"2:6\"><c r=\"B4\" t=\"s\"><v>6</v></c><c r=\"C4\" s=\"1\"><v>40429</v></c>") + "    <c r=\"D4\"><v>42</v></c><c r=\"E4\" t=\"b\"><v>0</v></c><c r=\"F4\"><f>F3+D4</f><v>117</v></c></row>") + "  <row r=\"5\" spans=\"2:6\"><c r=\"B5\" t=\"s\"><v>7</v></c><c r=\"C5\" s=\"1\"><v>40430</v></c>") + "    <c r=\"D5\"><v>93</v></c><c r=\"E5\" t=\"b\"><v>1</v></c><c r=\"F5\"><f>F4+D5</f><v>210</v></c></row>") + " </sheetData>")));

    private static final String SHEET_EMPTY = String.format(StaxPoiSheetTest.BP_SHEET, "<dimension ref=\"A1\"/><sheetData/>");

    private static final String SHEET_INLINE_STRINGS = String.format(StaxPoiSheetTest.BP_SHEET, ("<dimension ref=\"A1:B3\"/>" + ((((((((((((((((((("<sheetViews>" + "<sheetView tabSelected=\"1\" workbookViewId=\"0\" rightToLeft=\"false\">") + "<selection activeCell=\"C5\" sqref=\"C5\"/>") + "</sheetView>") + "</sheetViews>") + "<sheetFormatPr defaultRowHeight=\"15\"/>") + "<sheetData>") + "<row outlineLevel=\"0\" r=\"1\">") + "<c r=\"A1\" s=\"0\" t=\"inlineStr\"><is><t>Test1</t></is></c>") + "<c r=\"B1\" s=\"0\" t=\"inlineStr\"><is><t>Test2</t></is></c>") + "</row>") + "<row outlineLevel=\"0\" r=\"2\">") + "<c r=\"A2\" s=\"0\" t=\"inlineStr\"><is><t>value 1 1</t></is></c>") + "<c r=\"B2\" s=\"0\" t=\"inlineStr\"><is><t>value 2 1</t></is></c>") + "</row>") + "<row outlineLevel=\"0\" r=\"3\">") + "<c r=\"A3\" s=\"0\" t=\"inlineStr\"><is><t>value 1 2</t></is></c>") + "<c r=\"B3\" s=\"0\" t=\"inlineStr\"><is><t>value 2 2</t></is></c>") + "</row>") + "</sheetData>")));

    private static final String SHEET_NO_USED_RANGE_SPECIFIED = String.format(StaxPoiSheetTest.BP_SHEET, ("<dimension ref=\"A1\" />" + ((((((((((((((((((((((((((((((((("<sheetViews>" + "<sheetView tabSelected=\"1\" workbookViewId=\"0\">") + "<selection/>") + "</sheetView>") + "</sheetViews>") + "<sheetFormatPr defaultRowHeight=\"12.750000\" customHeight=\"true\"/>") + "<sheetData>") + "<row r=\"2\">") + "<c r=\"A2\" s=\"9\" t=\"s\">") + "<v>0</v>") + "</c><c r=\"B2\" s=\"9\" t=\"s\">") + "<v>0</v>") + "</c><c r=\"C2\" s=\"9\" t=\"s\">") + "<v>1</v>") + "</c><c r=\"D2\" s=\"9\" t=\"s\">") + "<v>2</v>") + "</c><c r=\"E2\" s=\"9\" t=\"s\">") + "<v>3</v>") + "</c>") + "</row>") + "<row r=\"3\">") + "<c r=\"A3\" s=\"11\" t=\"s\">") + "<v>4</v>") + "</c><c r=\"B3\" s=\"11\" t=\"s\">") + "<v>4</v>") + "</c><c r=\"C3\" s=\"11\" t=\"s\">") + "<v>5</v>") + "</c><c r=\"D3\" s=\"12\">") + "<v>2623</v>") + "</c><c r=\"E3\" s=\"11\" t=\"s\">") + "<v>6</v>") + "</c>") + "</row>") + "</sheetData>")));

    @Test
    public void testNullDateCell() throws Exception {
        // cell had null value instead of being null
        final String sheetId = "1";
        final String sheetName = "Sheet 1";
        XSSFReader reader = mockXSSFReader(sheetId, StaxPoiSheetTest.SHEET_DATE_NO_V, mockSharedStringsTable("Some Date"), mockStylesTable(Collections.singletonMap(2, 165), Collections.singletonMap(165, "M/D/YYYY")));
        StaxPoiSheet spSheet = Mockito.spy(new StaxPoiSheet(reader, sheetName, sheetId));
        Mockito.doReturn(true).when(spSheet).isDateCell(ArgumentMatchers.any());
        KCell cell = spSheet.getRow(1)[0];
        Assert.assertNotNull(cell);
        Assert.assertEquals(DATE, cell.getType());
        cell = spSheet.getRow(2)[0];
        Assert.assertNull("cell must be null", cell);
    }

    @Test
    public void testEmptySheet() throws Exception {
        XSSFReader reader = mockXSSFReader("sheet1", StaxPoiSheetTest.SHEET_EMPTY, Mockito.mock(SharedStringsTable.class), Mockito.mock(StylesTable.class));
        // check no exceptions
        StaxPoiSheet sheet = new StaxPoiSheet(reader, "empty", "sheet1");
        for (int j = 0; j < (sheet.getRows()); j++) {
            sheet.getRow(j);
        }
    }

    @Test
    public void testReadSameRow() throws Exception {
        KSheet sheet1 = getSampleSheet();
        KCell[] row = sheet1.getRow(3);
        Assert.assertEquals("Two", row[1].getValue());
        row = sheet1.getRow(3);
        Assert.assertEquals("Two", row[1].getValue());
    }

    @Test
    public void testReadRowRA() throws Exception {
        KSheet sheet1 = getSampleSheet();
        KCell[] row = sheet1.getRow(4);
        Assert.assertEquals("Three", row[1].getValue());
        row = sheet1.getRow(2);
        Assert.assertEquals("One", row[1].getValue());
    }

    @Test
    public void testReadEmptyRow() throws Exception {
        KSheet sheet1 = getSampleSheet();
        KCell[] row = sheet1.getRow(0);
        Assert.assertEquals("empty row expected", 0, row.length);
    }

    @Test
    public void testReadCells() throws Exception {
        KSheet sheet = getSampleSheet();
        KCell cell = sheet.getCell(1, 2);
        Assert.assertEquals("One", cell.getValue());
        Assert.assertEquals(LABEL, cell.getType());
        cell = sheet.getCell(2, 2);
        Assert.assertEquals(DATE, cell.getType());
        Assert.assertEquals(new Date(1283817600000L), cell.getValue());
        cell = sheet.getCell(1, 3);
        Assert.assertEquals("Two", cell.getValue());
        Assert.assertEquals(LABEL, cell.getType());
    }

    @Test
    public void testReadData() throws Exception {
        KSheet sheet1 = getSampleSheet();
        Assert.assertEquals(5, sheet1.getRows());
        KCell[] row = sheet1.getRow(2);
        Assert.assertEquals(LABEL, row[1].getType());
        Assert.assertEquals("One", row[1].getValue());
        Assert.assertEquals(DATE, row[2].getType());
        Assert.assertEquals(new Date(1283817600000L), row[2].getValue());
        Assert.assertEquals(NUMBER, row[3].getType());
        Assert.assertEquals(Double.valueOf("75"), row[3].getValue());
        Assert.assertEquals(BOOLEAN, row[4].getType());
        Assert.assertEquals(Boolean.TRUE, row[4].getValue());
        Assert.assertEquals(NUMBER_FORMULA, row[5].getType());
        Assert.assertEquals(Double.valueOf("75"), row[5].getValue());
        row = sheet1.getRow(3);
        Assert.assertEquals(LABEL, row[1].getType());
        Assert.assertEquals("Two", row[1].getValue());
        Assert.assertEquals(DATE, row[2].getType());
        Assert.assertEquals(new Date(1283904000000L), row[2].getValue());
        Assert.assertEquals(NUMBER, row[3].getType());
        Assert.assertEquals(Double.valueOf("42"), row[3].getValue());
        Assert.assertEquals(BOOLEAN, row[4].getType());
        Assert.assertEquals(Boolean.FALSE, row[4].getValue());
        Assert.assertEquals(NUMBER_FORMULA, row[5].getType());
        Assert.assertEquals(Double.valueOf("117"), row[5].getValue());
        row = sheet1.getRow(4);
        Assert.assertEquals(LABEL, row[1].getType());
        Assert.assertEquals("Three", row[1].getValue());
        Assert.assertEquals(DATE, row[2].getType());
        Assert.assertEquals(new Date(1283990400000L), row[2].getValue());
        Assert.assertEquals(NUMBER, row[3].getType());
        Assert.assertEquals(Double.valueOf("93"), row[3].getValue());
        Assert.assertEquals(BOOLEAN, row[4].getType());
        Assert.assertEquals(Boolean.TRUE, row[4].getValue());
        Assert.assertEquals(NUMBER_FORMULA, row[5].getType());
        Assert.assertEquals(Double.valueOf("210"), row[5].getValue());
        try {
            row = sheet1.getRow(5);
            Assert.fail("No out of bounds exception thrown when expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // OK!
        }
    }

    @Test
    public void testInlineString() throws Exception {
        final String sheetId = "1";
        final String sheetName = "Sheet 1";
        XSSFReader reader = mockXSSFReader(sheetId, StaxPoiSheetTest.SHEET_INLINE_STRINGS, Mockito.mock(SharedStringsTable.class), Mockito.mock(StylesTable.class));
        StaxPoiSheet spSheet = new StaxPoiSheet(reader, sheetName, sheetId);
        KCell[] rowCells = spSheet.getRow(0);
        Assert.assertEquals("Test1", rowCells[0].getValue());
        Assert.assertEquals(STRING_FORMULA, rowCells[0].getType());
        Assert.assertEquals("Test2", rowCells[1].getValue());
        Assert.assertEquals(STRING_FORMULA, rowCells[1].getType());
        rowCells = spSheet.getRow(1);
        Assert.assertEquals("value 1 1", rowCells[0].getValue());
        Assert.assertEquals(STRING_FORMULA, rowCells[0].getType());
        Assert.assertEquals("value 2 1", rowCells[1].getValue());
        Assert.assertEquals(STRING_FORMULA, rowCells[1].getType());
        rowCells = spSheet.getRow(2);
        Assert.assertEquals("value 1 2", rowCells[0].getValue());
        Assert.assertEquals(STRING_FORMULA, rowCells[0].getType());
        Assert.assertEquals("value 2 2", rowCells[1].getValue());
        Assert.assertEquals(STRING_FORMULA, rowCells[1].getType());
    }

    // The row and column bounds of all cells in the worksheet are specified in ref attribute of Dimension tag in sheet
    // xml
    // But ref can be present as range: <dimension ref="A1:C2"/> or as just one start cell: <dimension ref="A1"/>.
    // Below tests to validate correct work for such cases
    @Test
    public void testNoUsedRangeSpecified() throws Exception {
        final String sheetId = "1";
        final String sheetName = "Sheet 1";
        SharedStringsTable sharedStringsTableMock = mockSharedStringsTable("Report ID", "Report ID", "Approval Status", "Total Report Amount", "Policy", "ReportIdValue_1", "ReportIdValue_1", "ApprovalStatusValue_1", "PolicyValue_1");
        XSSFReader reader = mockXSSFReader(sheetId, StaxPoiSheetTest.SHEET_NO_USED_RANGE_SPECIFIED, sharedStringsTableMock, Mockito.mock(StylesTable.class));
        StaxPoiSheet spSheet = new StaxPoiSheet(reader, sheetName, sheetId);
        // The first row is empty - it should have empty rowCells
        KCell[] rowCells = spSheet.getRow(0);
        Assert.assertEquals(0, rowCells.length);
        // The second row - is the header - just skip it
        rowCells = spSheet.getRow(1);
        Assert.assertEquals(0, rowCells.length);
        // The row3 - is the first row with data - validating it
        rowCells = spSheet.getRow(2);
        Assert.assertEquals(LABEL, rowCells[0].getType());
        Assert.assertEquals("ReportIdValue_1", rowCells[0].getValue());
        Assert.assertEquals(LABEL, rowCells[1].getType());
        Assert.assertEquals("ReportIdValue_1", rowCells[1].getValue());
        Assert.assertEquals(LABEL, rowCells[2].getType());
        Assert.assertEquals("ApprovalStatusValue_1", rowCells[2].getValue());
        Assert.assertEquals(NUMBER, rowCells[3].getType());
        Assert.assertEquals(2623.0, rowCells[3].getValue());
        Assert.assertEquals(LABEL, rowCells[4].getType());
        Assert.assertEquals("PolicyValue_1", rowCells[4].getValue());
    }
}

