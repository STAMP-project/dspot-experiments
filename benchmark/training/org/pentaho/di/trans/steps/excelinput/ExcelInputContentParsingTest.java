/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2019 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.excelinput;


import Const.KETTLE_ZIP_MAX_ENTRY_SIZE;
import Const.KETTLE_ZIP_MAX_ENTRY_SIZE_DEFAULT;
import Const.KETTLE_ZIP_MAX_TEXT_SIZE;
import Const.KETTLE_ZIP_MAX_TEXT_SIZE_DEFAULT;
import Const.KETTLE_ZIP_MIN_INFLATE_RATIO;
import Const.KETTLE_ZIP_MIN_INFLATE_RATIO_DEFAULT;
import SpreadSheetType.JXL;
import SpreadSheetType.ODS;
import SpreadSheetType.POI;
import SpreadSheetType.SAX_POI;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.fileinput.BaseParsingTest;


public class ExcelInputContentParsingTest extends BaseExcelParsingTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private static final String[] CNST_3_SHEET_NAME_ARRAY = new String[]{ "Sheet1", "Sheet2", "Sheet3" };

    private static final String[] CNST_1_SHEET_NAME_ARRAY = new String[]{ "Sheet1" };

    private static final int[] CNST_3_SHEET_START_ROW_ARRAY = new int[]{ 23, 3, 7 };

    private static final int[] CNST_3_ZERO_INT_ARRAY = new int[]{ 0, 0, 0 };

    private static final int[] CNST_1_ZERO_INT_ARRAY = new int[]{ 0 };

    private static final int PDI_17765_ROW_LIMIT_SINGLE_SHEET = 10;

    private static final int PDI_17765_ROW_LIMIT_MULTIPLE_SHEET = 20;

    @Test
    public void testXLS() throws Exception {
        meta.setSpreadSheetType(JXL);
        init("sample.xls");
        setFields(new ExcelInputField("f1", (-1), (-1)), new ExcelInputField("f2", (-1), (-1)));
        process();
        check(new Object[][]{ new Object[]{ "test", null }, new Object[]{ "test", "test" } });
    }

    @Test
    public void testXLSX() throws Exception {
        meta.setSpreadSheetType(POI);
        init("sample.xlsx");
        setFields(new ExcelInputField("f1", (-1), (-1)), new ExcelInputField("f2", (-1), (-1)));
        process();
        check(new Object[][]{ new Object[]{ "test", null }, new Object[]{ "test", "test" } });
    }

    @Test
    public void testXLSXStream() throws Exception {
        meta.setSpreadSheetType(SAX_POI);
        init("sample.xlsx");
        setFields(new ExcelInputField("f1", (-1), (-1)), new ExcelInputField("f2", (-1), (-1)));
        process();
        check(new Object[][]{ new Object[]{ "test", null }, new Object[]{ "test", "test" } });
    }

    @Test
    public void testODS24() throws Exception {
        meta.setSpreadSheetType(ODS);
        init("sample-2.4.ods");
        setFields(new ExcelInputField("f1", (-1), (-1)), new ExcelInputField("f2", (-1), (-1)));
        process();
        check(new Object[][]{ new Object[]{ "test", null }, new Object[]{ "test", "test" } });
    }

    @Test
    public void testODS341() throws Exception {
        meta.setSpreadSheetType(ODS);
        init("sample-3.4.1.ods");
        setFields(new ExcelInputField("f1", (-1), (-1)), new ExcelInputField("f2", (-1), (-1)));
        process();
        check(new Object[][]{ new Object[]{ "AAABBC", "Nissan" }, new Object[]{ "AAABBC", "Nissan" }, new Object[]{ "AAABBC", "Nissan" }, new Object[]{ "AAABBC", "Nissan" } });
    }

    @Test
    public void testZipBombConfiguration_Default() throws Exception {
        // First set some random values
        Long bogusMaxEntrySize = 1000L;
        ZipSecureFile.setMaxEntrySize(bogusMaxEntrySize);
        Long bogusMaxTextSize = 1000L;
        ZipSecureFile.setMaxTextSize(bogusMaxTextSize);
        Double bogusMinInflateRatio = 0.5;
        ZipSecureFile.setMinInflateRatio(bogusMinInflateRatio);
        // Verify that the bogus values were set
        Assert.assertEquals(bogusMaxEntrySize, ((Long) (ZipSecureFile.getMaxEntrySize())));
        Assert.assertEquals(bogusMaxTextSize, ((Long) (ZipSecureFile.getMaxTextSize())));
        Assert.assertEquals(bogusMinInflateRatio, ((Double) (ZipSecureFile.getMinInflateRatio())));
        // Initializing the ExcelInput step should make the new values to be set
        meta.setSpreadSheetType(SAX_POI);
        init("Balance_Type_Codes.xlsx");
        // Verify that the default values were used
        Assert.assertEquals(KETTLE_ZIP_MAX_ENTRY_SIZE_DEFAULT, ((Long) (ZipSecureFile.getMaxEntrySize())));
        Assert.assertEquals(KETTLE_ZIP_MAX_TEXT_SIZE_DEFAULT, ((Long) (ZipSecureFile.getMaxTextSize())));
        Assert.assertEquals(KETTLE_ZIP_MIN_INFLATE_RATIO_DEFAULT, ((Double) (ZipSecureFile.getMinInflateRatio())));
    }

    @Test
    public void testZipBombConfiguration() throws Exception {
        Long maxEntrySizeVal = ((3L * 1024) * 1024) * 1024;
        Long maxTextSizeVal = ((2L * 1024) * 1024) * 1024;
        Double minInflateRatioVal = 0.123;
        // First set the property values
        System.setProperty(KETTLE_ZIP_MAX_ENTRY_SIZE, maxEntrySizeVal.toString());
        System.setProperty(KETTLE_ZIP_MAX_TEXT_SIZE, maxTextSizeVal.toString());
        System.setProperty(KETTLE_ZIP_MIN_INFLATE_RATIO, minInflateRatioVal.toString());
        // ExcelInput excelInput = new ExcelInput( null, null, 0, null, null );
        // Initializing the ExcelInput step should make the new values to be set
        meta.setSpreadSheetType(SAX_POI);
        init("Balance_Type_Codes.xlsx");
        // Verify that the setted values were used
        Assert.assertEquals(maxEntrySizeVal, ((Long) (ZipSecureFile.getMaxEntrySize())));
        Assert.assertEquals(maxTextSizeVal, ((Long) (ZipSecureFile.getMaxTextSize())));
        Assert.assertEquals(minInflateRatioVal, ((Double) (ZipSecureFile.getMinInflateRatio())));
    }

    @Test
    public void testXLSXCompressionRatioIsBig() throws Exception {
        // For this zip to be correctly handed, we need to allow a lower inflate ratio
        Double minInflateRatio = 0.007;
        System.setProperty(KETTLE_ZIP_MIN_INFLATE_RATIO, minInflateRatio.toString());
        meta.setSpreadSheetType(SAX_POI);
        init("Balance_Type_Codes.xlsx");
        // Verify that the minimum allowed inflate ratio is the expected
        Assert.assertEquals(minInflateRatio, ((Double) (ZipSecureFile.getMinInflateRatio())));
        setFields(new ExcelInputField("FIST ID", (-1), (-1)), new ExcelInputField("SOURCE SYSTEM", (-1), (-1)));
        process();
        checkErrors();
        checkContent(new Object[][]{ new Object[]{ "FIST0200", "ACM" } });
    }

    @Test
    public void test_PDI_17765_SingleSheet_Header_StartRow0() throws Exception {
        String firstResult = "1.0";
        String lastResult = "10.0";
        test_PDI_17765_SingleSheet(ExcelInputContentParsingTest.PDI_17765_ROW_LIMIT_SINGLE_SHEET, true, 0, firstResult, lastResult);
    }

    @Test
    public void test_PDI_17765_SingleSheet_NoHeader_StartRow0() throws Exception {
        String firstResult = "col";
        String lastResult = "9.0";
        test_PDI_17765_SingleSheet(ExcelInputContentParsingTest.PDI_17765_ROW_LIMIT_SINGLE_SHEET, false, 0, firstResult, lastResult);
    }

    @Test
    public void test_PDI_17765_SingleSheet_Header_StartRow5() throws Exception {
        String firstResult = "6.0";
        String lastResult = "15.0";
        test_PDI_17765_SingleSheet(ExcelInputContentParsingTest.PDI_17765_ROW_LIMIT_SINGLE_SHEET, true, 5, firstResult, lastResult);
    }

    @Test
    public void test_PDI_17765_SingleSheet_NoHeader_StartRow5() throws Exception {
        String firstResult = "5.0";
        String lastResult = "14.0";
        test_PDI_17765_SingleSheet(ExcelInputContentParsingTest.PDI_17765_ROW_LIMIT_SINGLE_SHEET, false, 5, firstResult, lastResult);
    }

    @Test
    public void test_PDI_17765_SingleSheet_Header_StartRow12() throws Exception {
        String firstResult = "13.0";
        String lastResult = "22.0";
        test_PDI_17765_SingleSheet(ExcelInputContentParsingTest.PDI_17765_ROW_LIMIT_SINGLE_SHEET, true, 12, firstResult, lastResult);
    }

    @Test
    public void test_PDI_17765_SingleSheet_NoHeader_StartRow12() throws Exception {
        String firstResult = "12.0";
        String lastResult = "21.0";
        test_PDI_17765_SingleSheet(ExcelInputContentParsingTest.PDI_17765_ROW_LIMIT_SINGLE_SHEET, false, 12, firstResult, lastResult);
    }

    @Test
    public void test_PDI_17765_MultipleSheets_Header_StartRow0() throws Exception {
        String firstResult = "1.0";
        String lastResult = "20.0";
        test_PDI_17765(ExcelInputContentParsingTest.PDI_17765_ROW_LIMIT_MULTIPLE_SHEET, true, ExcelInputContentParsingTest.CNST_3_ZERO_INT_ARRAY, ExcelInputContentParsingTest.CNST_3_ZERO_INT_ARRAY, ExcelInputContentParsingTest.CNST_3_SHEET_NAME_ARRAY);
        // Checks
        Assert.assertEquals("Wrong first result", firstResult, rows.get(0)[0]);
        Assert.assertEquals("Wrong last result", lastResult, rows.get(((ExcelInputContentParsingTest.PDI_17765_ROW_LIMIT_MULTIPLE_SHEET) - 1))[0]);
    }

    @Test
    public void test_PDI_17765_MultipleSheets_NoHeader_StartRow0() throws Exception {
        String firstResult = "col";
        String lastResult = "19.0";
        test_PDI_17765(ExcelInputContentParsingTest.PDI_17765_ROW_LIMIT_MULTIPLE_SHEET, false, ExcelInputContentParsingTest.CNST_3_ZERO_INT_ARRAY, ExcelInputContentParsingTest.CNST_3_ZERO_INT_ARRAY, ExcelInputContentParsingTest.CNST_3_SHEET_NAME_ARRAY);
        // Checks
        Assert.assertEquals("Wrong first result", firstResult, rows.get(0)[0]);
        Assert.assertEquals("Wrong last result", lastResult, rows.get(((ExcelInputContentParsingTest.PDI_17765_ROW_LIMIT_MULTIPLE_SHEET) - 1))[0]);
    }

    @Test
    public void test_PDI_17765_MultipleSheets_Header_StartRowX() throws Exception {
        String firstResult = "24.0";
        String lastResult = "132.0";
        test_PDI_17765(ExcelInputContentParsingTest.PDI_17765_ROW_LIMIT_MULTIPLE_SHEET, true, ExcelInputContentParsingTest.CNST_3_SHEET_START_ROW_ARRAY, ExcelInputContentParsingTest.CNST_3_ZERO_INT_ARRAY, ExcelInputContentParsingTest.CNST_3_SHEET_NAME_ARRAY);
        // Checks
        Assert.assertEquals("Wrong first result", firstResult, rows.get(0)[0]);
        Assert.assertEquals("Wrong last result", lastResult, rows.get(((ExcelInputContentParsingTest.PDI_17765_ROW_LIMIT_MULTIPLE_SHEET) - 1))[0]);
    }

    @Test
    public void test_PDI_17765_MultipleSheets_NoHeader_StartRowX() throws Exception {
        String firstResult = "23.0";
        String lastResult = "102.0";
        test_PDI_17765(ExcelInputContentParsingTest.PDI_17765_ROW_LIMIT_MULTIPLE_SHEET, false, ExcelInputContentParsingTest.CNST_3_SHEET_START_ROW_ARRAY, ExcelInputContentParsingTest.CNST_3_ZERO_INT_ARRAY, ExcelInputContentParsingTest.CNST_3_SHEET_NAME_ARRAY);
        // Checks
        Assert.assertEquals("Wrong first result", firstResult, rows.get(0)[0]);
        Assert.assertEquals("Wrong last result", lastResult, rows.get(((ExcelInputContentParsingTest.PDI_17765_ROW_LIMIT_MULTIPLE_SHEET) - 1))[0]);
    }
}

