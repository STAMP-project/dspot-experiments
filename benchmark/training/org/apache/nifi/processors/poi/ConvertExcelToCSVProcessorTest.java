/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.poi;


import CSVUtils.QUOTE_MINIMAL;
import CSVUtils.QUOTE_MODE;
import CSVUtils.RECORD_SEPARATOR;
import CSVUtils.VALUE_SEPARATOR;
import ConvertExcelToCSVProcessor.COLUMNS_TO_SKIP;
import ConvertExcelToCSVProcessor.DESIRED_SHEETS;
import ConvertExcelToCSVProcessor.FAILURE;
import ConvertExcelToCSVProcessor.FORMAT_VALUES;
import ConvertExcelToCSVProcessor.ORIGINAL;
import ConvertExcelToCSVProcessor.ROWS_TO_SKIP;
import ConvertExcelToCSVProcessor.ROW_NUM;
import ConvertExcelToCSVProcessor.SHEET_NAME;
import ConvertExcelToCSVProcessor.SOURCE_FILE_NAME;
import ConvertExcelToCSVProcessor.SUCCESS;
import CoreAttributes.FILENAME;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.util.LogMessage;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


public class ConvertExcelToCSVProcessorTest {
    private TestRunner testRunner;

    @Test
    public void testMultipleSheetsGeneratesMultipleFlowFiles() throws Exception {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("test", "attribute");
        testRunner.enqueue(new File("src/test/resources/TwoSheets.xlsx").toPath(), attributes);
        testRunner.run();
        testRunner.assertTransferCount(SUCCESS, 2);
        testRunner.assertTransferCount(ORIGINAL, 1);
        testRunner.assertTransferCount(FAILURE, 0);
        MockFlowFile ffSheetA = testRunner.getFlowFilesForRelationship(SUCCESS).get(0);
        Long rowsSheetA = new Long(ffSheetA.getAttribute(ROW_NUM));
        Assert.assertTrue((rowsSheetA == 4L));
        Assert.assertTrue(ffSheetA.getAttribute(SHEET_NAME).equalsIgnoreCase("TestSheetA"));
        Assert.assertTrue(ffSheetA.getAttribute(SOURCE_FILE_NAME).equals("TwoSheets.xlsx"));
        // Since TestRunner.run() will create a random filename even if the attribute is set in enqueue manually we just check that "_{SHEETNAME}.csv is present
        Assert.assertTrue(ffSheetA.getAttribute(FILENAME.key()).endsWith("_TestSheetA.csv"));
        Assert.assertTrue(ffSheetA.getAttribute("test").equals("attribute"));
        MockFlowFile ffSheetB = testRunner.getFlowFilesForRelationship(SUCCESS).get(1);
        Long rowsSheetB = new Long(ffSheetB.getAttribute(ROW_NUM));
        Assert.assertTrue((rowsSheetB == 3L));
        Assert.assertTrue(ffSheetB.getAttribute(SHEET_NAME).equalsIgnoreCase("TestSheetB"));
        Assert.assertTrue(ffSheetB.getAttribute(SOURCE_FILE_NAME).equals("TwoSheets.xlsx"));
        // Since TestRunner.run() will create a random filename even if the attribute is set in enqueue manually we just check that "_{SHEETNAME}.csv is present
        Assert.assertTrue(ffSheetB.getAttribute(FILENAME.key()).endsWith("_TestSheetB.csv"));
        Assert.assertTrue(ffSheetB.getAttribute("test").equals("attribute"));
    }

    @Test
    public void testDataFormatting() throws Exception {
        testRunner.enqueue(new File("src/test/resources/dataformatting.xlsx").toPath());
        testRunner.setProperty(FORMAT_VALUES, "false");
        testRunner.run();
        testRunner.assertTransferCount(SUCCESS, 1);
        testRunner.assertTransferCount(ORIGINAL, 1);
        testRunner.assertTransferCount(FAILURE, 0);
        MockFlowFile ff = testRunner.getFlowFilesForRelationship(SUCCESS).get(0);
        Long rowsSheet = new Long(ff.getAttribute(ROW_NUM));
        Assert.assertTrue((rowsSheet == 9));
        ff.assertContentEquals(("Numbers,Timestamps,Money\n" + ((((((("1234.4559999999999,42736.5,123.45\n" + "1234.4559999999999,42736.5,123.45\n") + "1234.4559999999999,42736.5,123.45\n") + "1234.4559999999999,42736.5,1023.45\n") + "1234.4559999999999,42736.5,1023.45\n") + "987654321,42736.5,1023.45\n") + "987654321,,\n") + "987654321,,\n")));
    }

    @Test
    public void testQuoting() throws Exception {
        testRunner.enqueue(new File("src/test/resources/dataformatting.xlsx").toPath());
        testRunner.setProperty(QUOTE_MODE, QUOTE_MINIMAL);
        testRunner.setProperty(FORMAT_VALUES, "true");
        testRunner.run();
        testRunner.assertTransferCount(SUCCESS, 1);
        testRunner.assertTransferCount(ORIGINAL, 1);
        testRunner.assertTransferCount(FAILURE, 0);
        MockFlowFile ff = testRunner.getFlowFilesForRelationship(SUCCESS).get(0);
        Long rowsSheet = new Long(ff.getAttribute(ROW_NUM));
        Assert.assertTrue((rowsSheet == 9));
        LocalDateTime localDt = LocalDateTime.of(2017, 1, 1, 12, 0, 0);
        ff.assertContentEquals((((((((((((((((((((("Numbers,Timestamps,Money\n" + "1234.456,") + (DateTimeFormatter.ofPattern("d/M/yy").format(localDt))) + ",$   123.45\n") + "1234.46,") + (DateTimeFormatter.ofPattern("hh:mm:ss a").format(localDt))) + ",\u00a3   123.45\n") + "1234.5,\"") + (DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy").format(localDt))) + "\",\u00a5   123.45\n") + "\"1,234.46\",") + (DateTimeFormatter.ofPattern("d/M/yy HH:mm").format(localDt))) + ",\"$   1,023.45\"\n") + "\"1,234.4560\",") + (DateTimeFormatter.ofPattern("hh:mm a").format(localDt))) + ",\"\u00a3   1,023.45\"\n") + "9.88E+08,") + (DateTimeFormatter.ofPattern("yyyy/MM/dd/ HH:mm").format(localDt))) + ",\"\u00a5   1,023.45\"\n") + "9.877E+08,,\n") + "9.8765E+08,,\n"));
    }

    @Test
    public void testSkipRows() throws Exception {
        testRunner.enqueue(new File("src/test/resources/dataformatting.xlsx").toPath());
        testRunner.setProperty(ROWS_TO_SKIP, "2");
        testRunner.setProperty(FORMAT_VALUES, "true");
        testRunner.run();
        testRunner.assertTransferCount(SUCCESS, 1);
        testRunner.assertTransferCount(ORIGINAL, 1);
        testRunner.assertTransferCount(FAILURE, 0);
        MockFlowFile ff = testRunner.getFlowFilesForRelationship(SUCCESS).get(0);
        Long rowsSheet = new Long(ff.getAttribute(ROW_NUM));
        Assert.assertEquals("Row count does match expected value.", "7", rowsSheet.toString());
        LocalDateTime localDt = LocalDateTime.of(2017, 1, 1, 12, 0, 0);
        ff.assertContentEquals((((((((((((((((("1234.46," + (DateTimeFormatter.ofPattern("hh:mm:ss a").format(localDt))) + ",\u00a3   123.45\n") + "1234.5,") + (DateTimeFormatter.ofPattern("EEEE\\, MMMM dd\\, yyyy").format(localDt))) + ",\u00a5   123.45\n") + "1\\,234.46,") + (DateTimeFormatter.ofPattern("d/M/yy HH:mm").format(localDt))) + ",$   1\\,023.45\n") + "1\\,234.4560,") + (DateTimeFormatter.ofPattern("hh:mm a").format(localDt))) + ",\u00a3   1\\,023.45\n") + "9.88E+08,") + (DateTimeFormatter.ofPattern("yyyy/MM/dd/ HH:mm").format(localDt))) + ",\u00a5   1\\,023.45\n") + "9.877E+08,,\n") + "9.8765E+08,,\n"));
    }

    @Test
    public void testSkipRowsWithEL() throws Exception {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("rowsToSkip", "2");
        testRunner.enqueue(new File("src/test/resources/dataformatting.xlsx").toPath(), attributes);
        testRunner.setProperty(ROWS_TO_SKIP, "${rowsToSkip}");
        testRunner.setProperty(FORMAT_VALUES, "true");
        testRunner.run();
        testRunner.assertTransferCount(SUCCESS, 1);
        testRunner.assertTransferCount(ORIGINAL, 1);
        testRunner.assertTransferCount(FAILURE, 0);
        MockFlowFile ff = testRunner.getFlowFilesForRelationship(SUCCESS).get(0);
        Long rowsSheet = new Long(ff.getAttribute(ROW_NUM));
        Assert.assertEquals("Row count does match expected value.", "7", rowsSheet.toString());
        LocalDateTime localDt = LocalDateTime.of(2017, 1, 1, 12, 0, 0);
        ff.assertContentEquals((((((((((((((((("1234.46," + (DateTimeFormatter.ofPattern("hh:mm:ss a").format(localDt))) + ",\u00a3   123.45\n") + "1234.5,") + (DateTimeFormatter.ofPattern("EEEE\\, MMMM dd\\, yyyy").format(localDt))) + ",\u00a5   123.45\n") + "1\\,234.46,") + (DateTimeFormatter.ofPattern("d/M/yy HH:mm").format(localDt))) + ",$   1\\,023.45\n") + "1\\,234.4560,") + (DateTimeFormatter.ofPattern("hh:mm a").format(localDt))) + ",\u00a3   1\\,023.45\n") + "9.88E+08,") + (DateTimeFormatter.ofPattern("yyyy/MM/dd/ HH:mm").format(localDt))) + ",\u00a5   1\\,023.45\n") + "9.877E+08,,\n") + "9.8765E+08,,\n"));
    }

    @Test
    public void testSkipColumns() throws Exception {
        testRunner.enqueue(new File("src/test/resources/dataformatting.xlsx").toPath());
        testRunner.setProperty(COLUMNS_TO_SKIP, "2");
        testRunner.setProperty(FORMAT_VALUES, "true");
        testRunner.run();
        testRunner.assertTransferCount(SUCCESS, 1);
        testRunner.assertTransferCount(ORIGINAL, 1);
        testRunner.assertTransferCount(FAILURE, 0);
        MockFlowFile ff = testRunner.getFlowFilesForRelationship(SUCCESS).get(0);
        Long rowsSheet = new Long(ff.getAttribute(ROW_NUM));
        Assert.assertTrue((rowsSheet == 9));
        ff.assertContentEquals(("Numbers,Money\n" + ((((((("1234.456,$   123.45\n" + "1234.46,\u00a3   123.45\n") + "1234.5,\u00a5   123.45\n") + "1\\,234.46,$   1\\,023.45\n") + "1\\,234.4560,\u00a3   1\\,023.45\n") + "9.88E+08,\u00a5   1\\,023.45\n") + "9.877E+08,\n") + "9.8765E+08,\n")));
    }

    @Test
    public void testSkipColumnsWithEL() throws Exception {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("columnsToSkip", "2");
        testRunner.enqueue(new File("src/test/resources/dataformatting.xlsx").toPath(), attributes);
        testRunner.setProperty(COLUMNS_TO_SKIP, "${columnsToSkip}");
        testRunner.setProperty(FORMAT_VALUES, "true");
        testRunner.run();
        testRunner.assertTransferCount(SUCCESS, 1);
        testRunner.assertTransferCount(ORIGINAL, 1);
        testRunner.assertTransferCount(FAILURE, 0);
        MockFlowFile ff = testRunner.getFlowFilesForRelationship(SUCCESS).get(0);
        Long rowsSheet = new Long(ff.getAttribute(ROW_NUM));
        Assert.assertTrue((rowsSheet == 9));
        ff.assertContentEquals(("Numbers,Money\n" + ((((((("1234.456,$   123.45\n" + "1234.46,\u00a3   123.45\n") + "1234.5,\u00a5   123.45\n") + "1\\,234.46,$   1\\,023.45\n") + "1\\,234.4560,\u00a3   1\\,023.45\n") + "9.88E+08,\u00a5   1\\,023.45\n") + "9.877E+08,\n") + "9.8765E+08,\n")));
    }

    @Test
    public void testCustomDelimiters() throws Exception {
        testRunner.enqueue(new File("src/test/resources/dataformatting.xlsx").toPath());
        testRunner.setProperty(VALUE_SEPARATOR, "|");
        testRunner.setProperty(RECORD_SEPARATOR, "\\r\\n");
        testRunner.setProperty(FORMAT_VALUES, "true");
        testRunner.run();
        testRunner.assertTransferCount(SUCCESS, 1);
        testRunner.assertTransferCount(ORIGINAL, 1);
        testRunner.assertTransferCount(FAILURE, 0);
        MockFlowFile ff = testRunner.getFlowFilesForRelationship(SUCCESS).get(0);
        Long rowsSheet = new Long(ff.getAttribute(ROW_NUM));
        Assert.assertTrue((rowsSheet == 9));
        LocalDateTime localDt = LocalDateTime.of(2017, 1, 1, 12, 0, 0);
        ff.assertContentEquals((((((((((((((((((((("Numbers|Timestamps|Money\r\n" + "1234.456|") + (DateTimeFormatter.ofPattern("d/M/yy").format(localDt))) + "|$   123.45\r\n") + "1234.46|") + (DateTimeFormatter.ofPattern("hh:mm:ss a").format(localDt))) + "|\u00a3   123.45\r\n") + "1234.5|") + (DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy").format(localDt))) + "|\u00a5   123.45\r\n") + "1,234.46|") + (DateTimeFormatter.ofPattern("d/M/yy HH:mm").format(localDt))) + "|$   1,023.45\r\n") + "1,234.4560|") + (DateTimeFormatter.ofPattern("hh:mm a").format(localDt))) + "|\u00a3   1,023.45\r\n") + "9.88E+08|") + (DateTimeFormatter.ofPattern("yyyy/MM/dd/ HH:mm").format(localDt))) + "|\u00a5   1,023.45\r\n") + "9.877E+08||\r\n") + "9.8765E+08||\r\n"));
    }

    /**
     * Validates that all sheets in the Excel document are exported.
     *
     * @throws Exception
     * 		Any exception thrown during execution.
     */
    @Test
    public void testProcessAllSheets() throws Exception {
        testRunner.enqueue(new File("src/test/resources/CollegeScorecard.xlsx").toPath());
        testRunner.run();
        testRunner.assertTransferCount(SUCCESS, 1);
        testRunner.assertTransferCount(ORIGINAL, 1);
        testRunner.assertTransferCount(FAILURE, 0);
        MockFlowFile ff = testRunner.getFlowFilesForRelationship(SUCCESS).get(0);
        Long l = new Long(ff.getAttribute(ROW_NUM));
        Assert.assertTrue((l == 7805L));
        testRunner.clearProvenanceEvents();
        testRunner.clearTransferState();
        testRunner.enqueue(new File("src/test/resources/TwoSheets.xlsx").toPath());
        testRunner.run();
        testRunner.assertTransferCount(SUCCESS, 2);
        testRunner.assertTransferCount(ORIGINAL, 1);
        testRunner.assertTransferCount(FAILURE, 0);
        ff = testRunner.getFlowFilesForRelationship(SUCCESS).get(0);
        l = new Long(ff.getAttribute(ROW_NUM));
        Assert.assertTrue((l == 4L));
        ff = testRunner.getFlowFilesForRelationship(SUCCESS).get(1);
        l = new Long(ff.getAttribute(ROW_NUM));
        Assert.assertTrue((l == 3L));
    }

    /**
     * Validates that the manually specified sheet is exported from the Excel document.
     *
     * @throws Exception
     * 		Any exception thrown during execution.
     */
    @Test
    public void testProcessASpecificSheetThatDoesExist() throws Exception {
        testRunner.setProperty(DESIRED_SHEETS, "Scorecard");
        testRunner.enqueue(new File("src/test/resources/CollegeScorecard.xlsx").toPath());
        testRunner.run();
        testRunner.assertTransferCount(SUCCESS, 1);
        testRunner.assertTransferCount(ORIGINAL, 1);
        testRunner.assertTransferCount(FAILURE, 0);
        MockFlowFile ff = testRunner.getFlowFilesForRelationship(SUCCESS).get(0);
        Long l = new Long(ff.getAttribute(ROW_NUM));
        Assert.assertTrue((l == 7805L));
    }

    /**
     * Tests for a syntactically valid Excel XSSF document with a manually specified Excel sheet that does not exist.
     * In this scenario only the Original relationship should be invoked.
     *
     * @throws Exception
     * 		Any exception thrown during execution.
     */
    @Test
    public void testNonExistantSpecifiedSheetName() throws Exception {
        testRunner.setProperty(DESIRED_SHEETS, "NopeIDoNotExist");
        testRunner.enqueue(new File("src/test/resources/CollegeScorecard.xlsx").toPath());
        testRunner.run();
        testRunner.assertTransferCount(SUCCESS, 0);// We aren't expecting any output to success here because the sheet doesn't exist

        testRunner.assertTransferCount(ORIGINAL, 1);
        testRunner.assertTransferCount(FAILURE, 0);
    }

    /**
     * Validates that a sheet contains blank cells can be converted to a CSV without missing columns.
     *
     * @throws Exception
     * 		Any exception thrown during execution.
     */
    @Test
    public void testProcessASheetWithBlankCells() throws Exception {
        testRunner.setProperty(DESIRED_SHEETS, "Sheet1");
        testRunner.enqueue(new File("src/test/resources/with-blank-cells.xlsx").toPath());
        testRunner.run();
        testRunner.assertTransferCount(SUCCESS, 1);
        testRunner.assertTransferCount(ORIGINAL, 1);
        testRunner.assertTransferCount(FAILURE, 0);
        MockFlowFile ff = testRunner.getFlowFilesForRelationship(SUCCESS).get(0);
        Long l = new Long(ff.getAttribute(ROW_NUM));
        Assert.assertTrue((l == 8L));
        ff.assertContentEquals(new File("src/test/resources/with-blank-cells.csv"));
    }

    /**
     * Tests for graceful handling and error messaging of unsupported .XLS files.
     */
    @Test
    public void testHandleUnsupportedXlsFile() throws Exception {
        testRunner.enqueue(new File("src/test/resources/Unsupported.xls").toPath());
        testRunner.run();
        testRunner.assertTransferCount(SUCCESS, 0);
        testRunner.assertTransferCount(ORIGINAL, 0);
        testRunner.assertTransferCount(FAILURE, 1);
        List<LogMessage> errorMessages = testRunner.getLogger().getErrorMessages();
        Assert.assertEquals(1, errorMessages.size());
        String messageText = errorMessages.get(0).getMsg();
        Assert.assertTrue(((messageText.contains("Excel")) && (messageText.contains("OLE2"))));
    }
}

