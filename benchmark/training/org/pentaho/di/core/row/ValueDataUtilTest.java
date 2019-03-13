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
package org.pentaho.di.core.row;


import CalculatorMetaFunction.CALC_ADD;
import CalculatorMetaFunction.CALC_ADD3;
import CalculatorMetaFunction.CALC_COMBINATION_1;
import CalculatorMetaFunction.CALC_COMBINATION_2;
import CalculatorMetaFunction.CALC_DATE_DIFF;
import CalculatorMetaFunction.CALC_DATE_WORKING_DIFF;
import CalculatorMetaFunction.CALC_DIVIDE;
import CalculatorMetaFunction.CALC_JARO;
import CalculatorMetaFunction.CALC_JARO_WINKLER;
import CalculatorMetaFunction.CALC_NVL;
import CalculatorMetaFunction.CALC_PERCENT_1;
import CalculatorMetaFunction.CALC_PERCENT_2;
import CalculatorMetaFunction.CALC_PERCENT_3;
import CalculatorMetaFunction.CALC_REMAINDER;
import CalculatorMetaFunction.CALC_ROUND_1;
import CalculatorMetaFunction.CALC_ROUND_2;
import CalculatorMetaFunction.CALC_SUBTRACT;
import StringUtils.EMPTY;
import ValueMetaInterface.STORAGE_TYPE_BINARY_STRING;
import ValueMetaInterface.STORAGE_TYPE_NORMAL;
import ValueMetaInterface.TYPE_BIGNUMBER;
import ValueMetaInterface.TYPE_BINARY;
import ValueMetaInterface.TYPE_BOOLEAN;
import ValueMetaInterface.TYPE_DATE;
import ValueMetaInterface.TYPE_INTEGER;
import ValueMetaInterface.TYPE_NUMBER;
import ValueMetaInterface.TYPE_STRING;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.di.core.exception.KettleFileNotFoundException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.value.ValueMetaBigNumber;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class ValueDataUtilTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private static String yyyy_MM_dd = "yyyy-MM-dd";

    // private enum DateCalc {WORKING_DAYS, DATE_DIFF};
    /**
     *
     *
     * @deprecated Use {@link Const#ltrim(String)} instead
     * @throws KettleValueException
     * 		
     */
    @Deprecated
    @Test
    public void testLeftTrim() throws KettleValueException {
        Assert.assertEquals("", ValueDataUtil.leftTrim(""));
        Assert.assertEquals("string", ValueDataUtil.leftTrim("string"));
        Assert.assertEquals("string", ValueDataUtil.leftTrim(" string"));
        Assert.assertEquals("string", ValueDataUtil.leftTrim("  string"));
        Assert.assertEquals("string", ValueDataUtil.leftTrim("   string"));
        Assert.assertEquals("string", ValueDataUtil.leftTrim("     string"));
        Assert.assertEquals("string ", ValueDataUtil.leftTrim(" string "));
        Assert.assertEquals("string  ", ValueDataUtil.leftTrim("  string  "));
        Assert.assertEquals("string   ", ValueDataUtil.leftTrim("   string   "));
        Assert.assertEquals("string    ", ValueDataUtil.leftTrim("    string    "));
        Assert.assertEquals("", ValueDataUtil.leftTrim(" "));
        Assert.assertEquals("", ValueDataUtil.leftTrim("  "));
        Assert.assertEquals("", ValueDataUtil.leftTrim("   "));
    }

    /**
     *
     *
     * @deprecated Use {@link Const#rtrim(String)} instead
     * @throws KettleValueException
     * 		
     */
    @Deprecated
    @Test
    public void testRightTrim() throws KettleValueException {
        Assert.assertEquals("", ValueDataUtil.rightTrim(""));
        Assert.assertEquals("string", ValueDataUtil.rightTrim("string"));
        Assert.assertEquals("string", ValueDataUtil.rightTrim("string "));
        Assert.assertEquals("string", ValueDataUtil.rightTrim("string  "));
        Assert.assertEquals("string", ValueDataUtil.rightTrim("string   "));
        Assert.assertEquals("string", ValueDataUtil.rightTrim("string    "));
        Assert.assertEquals(" string", ValueDataUtil.rightTrim(" string "));
        Assert.assertEquals("  string", ValueDataUtil.rightTrim("  string  "));
        Assert.assertEquals("   string", ValueDataUtil.rightTrim("   string   "));
        Assert.assertEquals("    string", ValueDataUtil.rightTrim("    string    "));
        Assert.assertEquals("", ValueDataUtil.rightTrim(" "));
        Assert.assertEquals("", ValueDataUtil.rightTrim("  "));
        Assert.assertEquals("", ValueDataUtil.rightTrim("   "));
    }

    /**
     *
     *
     * @deprecated Use {@link Const#isSpace(char)} instead
     * @throws KettleValueException
     * 		
     */
    @Deprecated
    @Test
    public void testIsSpace() throws KettleValueException {
        Assert.assertTrue(ValueDataUtil.isSpace(' '));
        Assert.assertTrue(ValueDataUtil.isSpace('\t'));
        Assert.assertTrue(ValueDataUtil.isSpace('\r'));
        Assert.assertTrue(ValueDataUtil.isSpace('\n'));
        Assert.assertFalse(ValueDataUtil.isSpace('S'));
        Assert.assertFalse(ValueDataUtil.isSpace('b'));
    }

    /**
     *
     *
     * @deprecated Use {@link Const#trim(String)} instead
     * @throws KettleValueException
     * 		
     */
    @Deprecated
    @Test
    public void testTrim() throws KettleValueException {
        Assert.assertEquals("", ValueDataUtil.trim(""));
        Assert.assertEquals("string", ValueDataUtil.trim("string"));
        Assert.assertEquals("string", ValueDataUtil.trim("string "));
        Assert.assertEquals("string", ValueDataUtil.trim("string  "));
        Assert.assertEquals("string", ValueDataUtil.trim("string   "));
        Assert.assertEquals("string", ValueDataUtil.trim("string    "));
        Assert.assertEquals("string", ValueDataUtil.trim(" string "));
        Assert.assertEquals("string", ValueDataUtil.trim("  string  "));
        Assert.assertEquals("string", ValueDataUtil.trim("   string   "));
        Assert.assertEquals("string", ValueDataUtil.trim("    string    "));
        Assert.assertEquals("string", ValueDataUtil.trim(" string"));
        Assert.assertEquals("string", ValueDataUtil.trim("  string"));
        Assert.assertEquals("string", ValueDataUtil.trim("   string"));
        Assert.assertEquals("string", ValueDataUtil.trim("    string"));
        Assert.assertEquals("", ValueDataUtil.rightTrim(" "));
        Assert.assertEquals("", ValueDataUtil.rightTrim("  "));
        Assert.assertEquals("", ValueDataUtil.rightTrim("   "));
    }

    @Test
    public void testDateDiff_A_GT_B() {
        Object daysDiff = calculate("2010-05-12", "2010-01-01", TYPE_DATE, CALC_DATE_DIFF);
        Assert.assertEquals(new Long(131), daysDiff);
    }

    @Test
    public void testDateDiff_A_LT_B() {
        Object daysDiff = calculate("2010-12-31", "2011-02-10", TYPE_DATE, CALC_DATE_DIFF);
        Assert.assertEquals(new Long((-41)), daysDiff);
    }

    @Test
    public void testWorkingDaysDays_A_GT_B() {
        Object daysDiff = calculate("2010-05-12", "2010-01-01", TYPE_DATE, CALC_DATE_WORKING_DIFF);
        Assert.assertEquals(new Long(94), daysDiff);
    }

    @Test
    public void testWorkingDaysDays_A_LT_B() {
        Object daysDiff = calculate("2010-12-31", "2011-02-10", TYPE_DATE, CALC_DATE_WORKING_DIFF);
        Assert.assertEquals(new Long((-30)), daysDiff);
    }

    @Test
    public void testPlus() throws KettleValueException {
        long longValue = 1;
        Assert.assertEquals(longValue, ValueDataUtil.plus(new ValueMetaInteger(), longValue, new ValueMetaString(), EMPTY));
    }

    @Test
    public void checksumTest() {
        String path = getClass().getResource("txt-sample.txt").getPath();
        String checksum = ValueDataUtil.createChecksum(new ValueMetaString(), path, "MD5");
        Assert.assertEquals("098f6bcd4621d373cade4e832627b4f6", checksum);
    }

    @Test
    public void checksumMissingFileTest() {
        String nonExistingFile = "nonExistingFile";
        String checksum = ValueDataUtil.createChecksum(new ValueMetaString(), nonExistingFile, "MD5");
        Assert.assertNull(checksum);
    }

    @Test
    public void checksumNullPathTest() {
        String nonExistingFile = "nonExistingFile";
        String checksum = ValueDataUtil.createChecksum(new ValueMetaString(), nonExistingFile, "MD5");
        Assert.assertNull(checksum);
    }

    @Test
    public void checksumWithFailIfNoFileTest() throws Exception {
        String path = getClass().getResource("txt-sample.txt").getPath();
        String checksum = ValueDataUtil.createChecksum(new ValueMetaString(), path, "MD5", true);
        Assert.assertEquals("098f6bcd4621d373cade4e832627b4f6", checksum);
    }

    @Test
    public void checksumWithoutFailIfNoFileTest() throws Exception {
        String path = getClass().getResource("txt-sample.txt").getPath();
        String checksum = ValueDataUtil.createChecksum(new ValueMetaString(), path, "MD5", false);
        Assert.assertEquals("098f6bcd4621d373cade4e832627b4f6", checksum);
    }

    @Test
    public void checksumNoFailIfNoFileTest() throws KettleFileNotFoundException {
        String nonExistingFile = "nonExistingFile";
        String checksum = ValueDataUtil.createChecksum(new ValueMetaString(), nonExistingFile, "MD5", false);
        Assert.assertNull(checksum);
    }

    @Test(expected = KettleFileNotFoundException.class)
    public void checksumFailIfNoFileTest() throws KettleFileNotFoundException {
        String nonExistingPath = "nonExistingPath";
        ValueDataUtil.createChecksum(new ValueMetaString(), nonExistingPath, "MD5", true);
    }

    @Test
    public void checksumNullPathNoFailTest() throws KettleFileNotFoundException {
        Assert.assertNull(ValueDataUtil.createChecksum(new ValueMetaString(), null, "MD5", false));
    }

    @Test
    public void checksumNullPathFailTest() throws KettleFileNotFoundException {
        Assert.assertNull(ValueDataUtil.createChecksum(new ValueMetaString(), null, "MD5", true));
    }

    @Test
    public void checksumCRC32Test() {
        String path = getClass().getResource("txt-sample.txt").getPath();
        long checksum = ValueDataUtil.ChecksumCRC32(new ValueMetaString(), path);
        Assert.assertEquals(3632233996L, checksum);
    }

    @Test
    public void checksumCRC32MissingFileTest() {
        String nonExistingFile = "nonExistingFile";
        long checksum = ValueDataUtil.ChecksumCRC32(new ValueMetaString(), nonExistingFile);
        Assert.assertEquals(0, checksum);
    }

    @Test
    public void checksumCRC32NullPathTest() throws Exception {
        String nonExistingFile = "nonExistingFile";
        long checksum = ValueDataUtil.ChecksumCRC32(new ValueMetaString(), nonExistingFile);
        Assert.assertEquals(0, checksum);
    }

    @Test
    public void checksumCRC32WithoutFailIfNoFileTest() throws Exception {
        String path = getClass().getResource("txt-sample.txt").getPath();
        long checksum = ValueDataUtil.checksumCRC32(new ValueMetaString(), path, false);
        Assert.assertEquals(3632233996L, checksum);
    }

    @Test
    public void checksumCRC32NoFailIfNoFileTest() throws KettleFileNotFoundException {
        String nonExistingPath = "nonExistingPath";
        long checksum = ValueDataUtil.checksumCRC32(new ValueMetaString(), nonExistingPath, false);
        Assert.assertEquals(0, checksum);
    }

    @Test(expected = KettleFileNotFoundException.class)
    public void checksumCRC32FailIfNoFileTest() throws KettleFileNotFoundException {
        String nonExistingPath = "nonExistingPath";
        ValueDataUtil.checksumCRC32(new ValueMetaString(), nonExistingPath, true);
    }

    @Test
    public void checksumCRC32NullPathNoFailTest() throws KettleFileNotFoundException {
        long checksum = ValueDataUtil.checksumCRC32(new ValueMetaString(), null, false);
        Assert.assertEquals(0, checksum);
    }

    @Test
    public void checksumCRC32NullPathFailTest() throws KettleFileNotFoundException {
        long checksum = ValueDataUtil.checksumCRC32(new ValueMetaString(), null, true);
        Assert.assertEquals(0, checksum);
    }

    @Test
    public void checksumAdlerTest() {
        String path = getClass().getResource("txt-sample.txt").getPath();
        long checksum = ValueDataUtil.ChecksumAdler32(new ValueMetaString(), path);
        Assert.assertEquals(73204161L, checksum);
    }

    @Test
    public void checksumAdlerMissingFileTest() {
        String nonExistingFile = "nonExistingFile";
        long checksum = ValueDataUtil.ChecksumAdler32(new ValueMetaString(), nonExistingFile);
        Assert.assertEquals(0, checksum);
    }

    @Test
    public void checksumAdlerNullPathTest() {
        String nonExistingFile = "nonExistingFile";
        long checksum = ValueDataUtil.ChecksumAdler32(new ValueMetaString(), nonExistingFile);
        Assert.assertEquals(0, checksum);
    }

    @Test
    public void checksumAdlerWithFailIfNoFileTest() throws Exception {
        String path = getClass().getResource("txt-sample.txt").getPath();
        long checksum = ValueDataUtil.checksumAdler32(new ValueMetaString(), path, true);
        Assert.assertEquals(73204161L, checksum);
    }

    @Test
    public void checksumAdlerWithoutFailIfNoFileTest() throws Exception {
        String path = getClass().getResource("txt-sample.txt").getPath();
        long checksum = ValueDataUtil.checksumAdler32(new ValueMetaString(), path, false);
        Assert.assertEquals(73204161L, checksum);
    }

    @Test
    public void checksumAdlerNoFailIfNoFileTest() throws KettleFileNotFoundException {
        String nonExistingPath = "nonExistingPath";
        long checksum = ValueDataUtil.checksumAdler32(new ValueMetaString(), nonExistingPath, false);
        Assert.assertEquals(0, checksum);
    }

    @Test(expected = KettleFileNotFoundException.class)
    public void checksumAdlerFailIfNoFileTest() throws KettleFileNotFoundException {
        String nonExistingPath = "nonExistingPath";
        ValueDataUtil.checksumAdler32(new ValueMetaString(), nonExistingPath, true);
    }

    @Test
    public void checksumAdlerNullPathNoFailTest() throws KettleFileNotFoundException {
        long checksum = ValueDataUtil.checksumAdler32(new ValueMetaString(), null, false);
        Assert.assertEquals(0, checksum);
    }

    @Test
    public void checksumAdlerNullPathFailTest() throws KettleFileNotFoundException {
        long checksum = ValueDataUtil.checksumAdler32(new ValueMetaString(), null, true);
        Assert.assertEquals(0, checksum);
    }

    @Test
    public void xmlFileWellFormedTest() {
        String xmlFilePath = getClass().getResource("xml-sample.xml").getPath();
        boolean wellFormed = ValueDataUtil.isXMLFileWellFormed(new ValueMetaString(), xmlFilePath);
        Assert.assertTrue(wellFormed);
    }

    @Test
    public void xmlFileBadlyFormedTest() {
        String invalidXmlFilePath = getClass().getResource("invalid-xml-sample.xml").getPath();
        boolean wellFormed = ValueDataUtil.isXMLFileWellFormed(new ValueMetaString(), invalidXmlFilePath);
        Assert.assertFalse(wellFormed);
    }

    @Test
    public void xmlFileWellFormedWithFailIfNoFileTest() throws KettleFileNotFoundException {
        String xmlFilePath = getClass().getResource("xml-sample.xml").getPath();
        boolean wellFormed = ValueDataUtil.isXMLFileWellFormed(new ValueMetaString(), xmlFilePath, true);
        Assert.assertTrue(wellFormed);
    }

    @Test
    public void xmlFileWellFormedWithoutFailIfNoFileTest() throws KettleFileNotFoundException {
        String xmlFilePath = getClass().getResource("xml-sample.xml").getPath();
        boolean wellFormed = ValueDataUtil.isXMLFileWellFormed(new ValueMetaString(), xmlFilePath, false);
        Assert.assertTrue(wellFormed);
    }

    @Test
    public void xmlFileBadlyFormedWithFailIfNoFileTest() throws KettleFileNotFoundException {
        String invalidXmlFilePath = getClass().getResource("invalid-xml-sample.xml").getPath();
        boolean wellFormed = ValueDataUtil.isXMLFileWellFormed(new ValueMetaString(), invalidXmlFilePath, true);
        Assert.assertFalse(wellFormed);
    }

    @Test
    public void xmlFileBadlyFormedWithNoFailIfNoFileTest() throws KettleFileNotFoundException {
        String invalidXmlFilePath = getClass().getResource("invalid-xml-sample.xml").getPath();
        boolean wellFormed = ValueDataUtil.isXMLFileWellFormed(new ValueMetaString(), invalidXmlFilePath, false);
        Assert.assertFalse(wellFormed);
    }

    @Test
    public void xmlFileWellFormedNoFailIfNoFileTest() throws KettleFileNotFoundException {
        String nonExistingPath = "nonExistingPath";
        boolean wellFormed = ValueDataUtil.isXMLFileWellFormed(new ValueMetaString(), nonExistingPath, false);
        Assert.assertFalse(wellFormed);
    }

    @Test(expected = KettleFileNotFoundException.class)
    public void xmlFileWellFormedFailIfNoFileTest() throws KettleFileNotFoundException {
        String nonExistingPath = "nonExistingPath";
        ValueDataUtil.isXMLFileWellFormed(new ValueMetaString(), nonExistingPath, true);
    }

    @Test
    public void xmlFileWellFormedNullPathNoFailTest() throws KettleFileNotFoundException {
        boolean wellFormed = ValueDataUtil.isXMLFileWellFormed(new ValueMetaString(), null, false);
        Assert.assertFalse(wellFormed);
    }

    @Test
    public void xmlFileWellFormedNullPathFailTest() throws KettleFileNotFoundException {
        boolean wellFormed = ValueDataUtil.isXMLFileWellFormed(new ValueMetaString(), null, true);
        Assert.assertFalse(wellFormed);
    }

    @Test
    public void loadFileContentInBinary() throws Exception {
        String path = getClass().getResource("txt-sample.txt").getPath();
        byte[] content = ValueDataUtil.loadFileContentInBinary(new ValueMetaString(), path, true);
        Assert.assertTrue(Arrays.equals("test".getBytes(), content));
    }

    @Test
    public void loadFileContentInBinaryNoFailIfNoFileTest() throws Exception {
        String nonExistingPath = "nonExistingPath";
        Assert.assertNull(ValueDataUtil.loadFileContentInBinary(new ValueMetaString(), nonExistingPath, false));
    }

    @Test(expected = KettleFileNotFoundException.class)
    public void loadFileContentInBinaryFailIfNoFileTest() throws KettleFileNotFoundException, KettleValueException {
        String nonExistingPath = "nonExistingPath";
        ValueDataUtil.loadFileContentInBinary(new ValueMetaString(), nonExistingPath, true);
    }

    @Test
    public void loadFileContentInBinaryNullPathNoFailTest() throws Exception {
        Assert.assertNull(ValueDataUtil.loadFileContentInBinary(new ValueMetaString(), null, false));
    }

    @Test
    public void loadFileContentInBinaryNullPathFailTest() throws KettleFileNotFoundException, KettleValueException {
        Assert.assertNull(ValueDataUtil.loadFileContentInBinary(new ValueMetaString(), null, true));
    }

    @Test
    public void getFileEncodingTest() throws Exception {
        String path = getClass().getResource("txt-sample.txt").getPath();
        String encoding = ValueDataUtil.getFileEncoding(new ValueMetaString(), path);
        Assert.assertEquals("US-ASCII", encoding);
    }

    @Test(expected = KettleValueException.class)
    public void getFileEncodingMissingFileTest() throws KettleValueException {
        String nonExistingPath = "nonExistingPath";
        ValueDataUtil.getFileEncoding(new ValueMetaString(), nonExistingPath);
    }

    @Test
    public void getFileEncodingNullPathTest() throws Exception {
        Assert.assertNull(ValueDataUtil.getFileEncoding(new ValueMetaString(), null));
    }

    @Test
    public void getFileEncodingWithFailIfNoFileTest() throws Exception {
        String path = getClass().getResource("txt-sample.txt").getPath();
        String encoding = ValueDataUtil.getFileEncoding(new ValueMetaString(), path, true);
        Assert.assertEquals("US-ASCII", encoding);
    }

    @Test
    public void getFileEncodingWithoutFailIfNoFileTest() throws Exception {
        String path = getClass().getResource("txt-sample.txt").getPath();
        String encoding = ValueDataUtil.getFileEncoding(new ValueMetaString(), path, false);
        Assert.assertEquals("US-ASCII", encoding);
    }

    @Test
    public void getFileEncodingNoFailIfNoFileTest() throws Exception {
        String nonExistingPath = "nonExistingPath";
        String encoding = ValueDataUtil.getFileEncoding(new ValueMetaString(), nonExistingPath, false);
        Assert.assertNull(encoding);
    }

    @Test(expected = KettleFileNotFoundException.class)
    public void getFileEncodingFailIfNoFileTest() throws KettleFileNotFoundException, KettleValueException {
        String nonExistingPath = "nonExistingPath";
        ValueDataUtil.getFileEncoding(new ValueMetaString(), nonExistingPath, true);
    }

    @Test
    public void getFileEncodingNullPathNoFailTest() throws Exception {
        String encoding = ValueDataUtil.getFileEncoding(new ValueMetaString(), null, false);
        Assert.assertNull(encoding);
    }

    @Test
    public void getFileEncodingNullPathFailTest() throws KettleFileNotFoundException, KettleValueException {
        String encoding = ValueDataUtil.getFileEncoding(new ValueMetaString(), null, true);
        Assert.assertNull(encoding);
    }

    @Test
    public void testAdd() {
        // Test Kettle number types
        Assert.assertEquals(Double.valueOf("3.0"), calculate("1", "2", TYPE_NUMBER, CALC_ADD));
        Assert.assertEquals(Double.valueOf("0.0"), calculate("2", "-2", TYPE_NUMBER, CALC_ADD));
        Assert.assertEquals(Double.valueOf("30.0"), calculate("10", "20", TYPE_NUMBER, CALC_ADD));
        Assert.assertEquals(Double.valueOf("-50.0"), calculate("-100", "50", TYPE_NUMBER, CALC_ADD));
        // Test Kettle Integer (Java Long) types
        Assert.assertEquals(Long.valueOf("3"), calculate("1", "2", TYPE_INTEGER, CALC_ADD));
        Assert.assertEquals(Long.valueOf("0"), calculate("2", "-2", TYPE_INTEGER, CALC_ADD));
        Assert.assertEquals(Long.valueOf("30"), calculate("10", "20", TYPE_INTEGER, CALC_ADD));
        Assert.assertEquals(Long.valueOf("-50"), calculate("-100", "50", TYPE_INTEGER, CALC_ADD));
        // Test Kettle big Number types
        Assert.assertEquals(0, new BigDecimal("2.0").compareTo(((BigDecimal) (calculate("1", "1", TYPE_BIGNUMBER, CALC_ADD)))));
        Assert.assertEquals(0, new BigDecimal("0.0").compareTo(((BigDecimal) (calculate("2", "-2", TYPE_BIGNUMBER, CALC_ADD)))));
        Assert.assertEquals(0, new BigDecimal("30.0").compareTo(((BigDecimal) (calculate("10", "20", TYPE_BIGNUMBER, CALC_ADD)))));
        Assert.assertEquals(0, new BigDecimal("-50.0").compareTo(((BigDecimal) (calculate("-100", "50", TYPE_BIGNUMBER, CALC_ADD)))));
    }

    @Test
    public void testAdd3() {
        // Test Kettle number types
        Assert.assertEquals(Double.valueOf("6.0"), calculate("1", "2", "3", TYPE_NUMBER, CALC_ADD3));
        Assert.assertEquals(Double.valueOf("10.0"), calculate("2", "-2", "10", TYPE_NUMBER, CALC_ADD3));
        Assert.assertEquals(Double.valueOf("27.0"), calculate("10", "20", "-3", TYPE_NUMBER, CALC_ADD3));
        Assert.assertEquals(Double.valueOf("-55.0"), calculate("-100", "50", "-5", TYPE_NUMBER, CALC_ADD3));
        // Test Kettle Integer (Java Long) types
        Assert.assertEquals(Long.valueOf("3"), calculate("1", "1", "1", TYPE_INTEGER, CALC_ADD3));
        Assert.assertEquals(Long.valueOf("10"), calculate("2", "-2", "10", TYPE_INTEGER, CALC_ADD3));
        Assert.assertEquals(Long.valueOf("27"), calculate("10", "20", "-3", TYPE_INTEGER, CALC_ADD3));
        Assert.assertEquals(Long.valueOf("-55"), calculate("-100", "50", "-5", TYPE_INTEGER, CALC_ADD3));
        // Test Kettle big Number types
        Assert.assertEquals(0, new BigDecimal("6.0").compareTo(((BigDecimal) (calculate("1", "2", "3", TYPE_BIGNUMBER, CALC_ADD3)))));
        Assert.assertEquals(0, new BigDecimal("10.0").compareTo(((BigDecimal) (calculate("2", "-2", "10", TYPE_BIGNUMBER, CALC_ADD3)))));
        Assert.assertEquals(0, new BigDecimal("27.0").compareTo(((BigDecimal) (calculate("10", "20", "-3", TYPE_BIGNUMBER, CALC_ADD3)))));
        Assert.assertEquals(0, new BigDecimal("-55.0").compareTo(((BigDecimal) (calculate("-100", "50", "-5", TYPE_BIGNUMBER, CALC_ADD3)))));
    }

    @Test
    public void testSubtract() {
        // Test Kettle number types
        Assert.assertEquals(Double.valueOf("10.0"), calculate("20", "10", TYPE_NUMBER, CALC_SUBTRACT));
        Assert.assertEquals(Double.valueOf("-10.0"), calculate("10", "20", TYPE_NUMBER, CALC_SUBTRACT));
        // Test Kettle Integer (Java Long) types
        Assert.assertEquals(Long.valueOf("10"), calculate("20", "10", TYPE_INTEGER, CALC_SUBTRACT));
        Assert.assertEquals(Long.valueOf("-10"), calculate("10", "20", TYPE_INTEGER, CALC_SUBTRACT));
        // Test Kettle big Number types
        Assert.assertEquals(0, new BigDecimal("10").compareTo(((BigDecimal) (calculate("20", "10", TYPE_BIGNUMBER, CALC_SUBTRACT)))));
        Assert.assertEquals(0, new BigDecimal("-10").compareTo(((BigDecimal) (calculate("10", "20", TYPE_BIGNUMBER, CALC_SUBTRACT)))));
    }

    @Test
    public void testDivide() {
        // Test Kettle number types
        Assert.assertEquals(Double.valueOf("2.0"), calculate("2", "1", TYPE_NUMBER, CALC_DIVIDE));
        Assert.assertEquals(Double.valueOf("2.0"), calculate("4", "2", TYPE_NUMBER, CALC_DIVIDE));
        Assert.assertEquals(Double.valueOf("0.5"), calculate("10", "20", TYPE_NUMBER, CALC_DIVIDE));
        Assert.assertEquals(Double.valueOf("2.0"), calculate("100", "50", TYPE_NUMBER, CALC_DIVIDE));
        // Test Kettle Integer (Java Long) types
        Assert.assertEquals(Long.valueOf("2"), calculate("2", "1", TYPE_INTEGER, CALC_DIVIDE));
        Assert.assertEquals(Long.valueOf("2"), calculate("4", "2", TYPE_INTEGER, CALC_DIVIDE));
        Assert.assertEquals(Long.valueOf("0"), calculate("10", "20", TYPE_INTEGER, CALC_DIVIDE));
        Assert.assertEquals(Long.valueOf("2"), calculate("100", "50", TYPE_INTEGER, CALC_DIVIDE));
        // Test Kettle big Number types
        Assert.assertEquals(BigDecimal.valueOf(Long.valueOf("2")), calculate("2", "1", TYPE_BIGNUMBER, CALC_DIVIDE));
        Assert.assertEquals(BigDecimal.valueOf(Long.valueOf("2")), calculate("4", "2", TYPE_BIGNUMBER, CALC_DIVIDE));
        Assert.assertEquals(BigDecimal.valueOf(Double.valueOf("0.5")), calculate("10", "20", TYPE_BIGNUMBER, CALC_DIVIDE));
        Assert.assertEquals(BigDecimal.valueOf(Long.valueOf("2")), calculate("100", "50", TYPE_BIGNUMBER, CALC_DIVIDE));
    }

    @Test
    public void testMulitplyBigNumbers() throws Exception {
        BigDecimal field1 = new BigDecimal("123456789012345678901.1234567890123456789");
        BigDecimal field2 = new BigDecimal("1.0");
        BigDecimal field3 = new BigDecimal("2.0");
        BigDecimal expResult1 = new BigDecimal("123456789012345678901.1234567890123456789");
        BigDecimal expResult2 = new BigDecimal("246913578024691357802.2469135780246913578");
        BigDecimal expResult3 = new BigDecimal("123456789012345678901.1200000000000000000");
        BigDecimal expResult4 = new BigDecimal("246913578024691357802");
        Assert.assertEquals(expResult1, ValueDataUtil.multiplyBigDecimals(field1, field2, null));
        Assert.assertEquals(expResult2, ValueDataUtil.multiplyBigDecimals(field1, field3, null));
        Assert.assertEquals(expResult3, ValueDataUtil.multiplyBigDecimals(field1, field2, new MathContext(23)));
        Assert.assertEquals(expResult4, ValueDataUtil.multiplyBigDecimals(field1, field3, new MathContext(21)));
    }

    @Test
    public void testDivisionBigNumbers() throws Exception {
        BigDecimal field1 = new BigDecimal("123456789012345678901.1234567890123456789");
        BigDecimal field2 = new BigDecimal("1.0");
        BigDecimal field3 = new BigDecimal("2.0");
        BigDecimal expResult1 = new BigDecimal("123456789012345678901.1234567890123456789");
        BigDecimal expResult2 = new BigDecimal("61728394506172839450.56172839450617283945");
        BigDecimal expResult3 = new BigDecimal("123456789012345678901.12");
        BigDecimal expResult4 = new BigDecimal("61728394506172839450.6");
        Assert.assertEquals(expResult1, ValueDataUtil.divideBigDecimals(field1, field2, null));
        Assert.assertEquals(expResult2, ValueDataUtil.divideBigDecimals(field1, field3, null));
        Assert.assertEquals(expResult3, ValueDataUtil.divideBigDecimals(field1, field2, new MathContext(23)));
        Assert.assertEquals(expResult4, ValueDataUtil.divideBigDecimals(field1, field3, new MathContext(21)));
    }

    @Test
    public void testRemainderBigNumbers() throws Exception {
        BigDecimal field1 = new BigDecimal("123456789012345678901.1234567890123456789");
        BigDecimal field2 = new BigDecimal("1.0");
        BigDecimal field3 = new BigDecimal("2.0");
        BigDecimal expResult1 = new BigDecimal("0.1234567890123456789");
        BigDecimal expResult2 = new BigDecimal("1.1234567890123456789");
        Assert.assertEquals(expResult1, ValueDataUtil.remainder(new ValueMetaBigNumber(), field1, new ValueMetaBigNumber(), field2));
        Assert.assertEquals(expResult2, ValueDataUtil.remainder(new ValueMetaBigNumber(), field1, new ValueMetaBigNumber(), field3));
    }

    @Test
    public void testPercent1() {
        // Test Kettle number types
        Assert.assertEquals(Double.valueOf("10.0"), calculate("10", "100", TYPE_NUMBER, CALC_PERCENT_1));
        Assert.assertEquals(Double.valueOf("100.0"), calculate("2", "2", TYPE_NUMBER, CALC_PERCENT_1));
        Assert.assertEquals(Double.valueOf("50.0"), calculate("10", "20", TYPE_NUMBER, CALC_PERCENT_1));
        Assert.assertEquals(Double.valueOf("200.0"), calculate("100", "50", TYPE_NUMBER, CALC_PERCENT_1));
        // Test Kettle Integer (Java Long) types
        Assert.assertEquals(Long.valueOf("10"), calculate("10", "100", TYPE_INTEGER, CALC_PERCENT_1));
        Assert.assertEquals(Long.valueOf("100"), calculate("2", "2", TYPE_INTEGER, CALC_PERCENT_1));
        Assert.assertEquals(Long.valueOf("50"), calculate("10", "20", TYPE_INTEGER, CALC_PERCENT_1));
        Assert.assertEquals(Long.valueOf("200"), calculate("100", "50", TYPE_INTEGER, CALC_PERCENT_1));
        // Test Kettle big Number types
        Assert.assertEquals(BigDecimal.valueOf(Long.valueOf("10")), calculate("10", "100", TYPE_BIGNUMBER, CALC_PERCENT_1));
        Assert.assertEquals(BigDecimal.valueOf(Long.valueOf("100")), calculate("2", "2", TYPE_BIGNUMBER, CALC_PERCENT_1));
        Assert.assertEquals(BigDecimal.valueOf(Long.valueOf("50")), calculate("10", "20", TYPE_BIGNUMBER, CALC_PERCENT_1));
        Assert.assertEquals(BigDecimal.valueOf(Long.valueOf("200")), calculate("100", "50", TYPE_BIGNUMBER, CALC_PERCENT_1));
    }

    @Test
    public void testPercent2() {
        // Test Kettle number types
        Assert.assertEquals(Double.valueOf("0.99"), calculate("1", "1", TYPE_NUMBER, CALC_PERCENT_2));
        Assert.assertEquals(Double.valueOf("1.96"), calculate("2", "2", TYPE_NUMBER, CALC_PERCENT_2));
        Assert.assertEquals(Double.valueOf("8.0"), calculate("10", "20", TYPE_NUMBER, CALC_PERCENT_2));
        Assert.assertEquals(Double.valueOf("50.0"), calculate("100", "50", TYPE_NUMBER, CALC_PERCENT_2));
        // Test Kettle Integer (Java Long) types
        Assert.assertEquals(Long.valueOf("1"), calculate("1", "1", TYPE_INTEGER, CALC_PERCENT_2));
        Assert.assertEquals(Long.valueOf("2"), calculate("2", "2", TYPE_INTEGER, CALC_PERCENT_2));
        Assert.assertEquals(Long.valueOf("8"), calculate("10", "20", TYPE_INTEGER, CALC_PERCENT_2));
        Assert.assertEquals(Long.valueOf("50"), calculate("100", "50", TYPE_INTEGER, CALC_PERCENT_2));
        // Test Kettle big Number types
        Assert.assertEquals(BigDecimal.valueOf(Double.valueOf("0.99")), calculate("1", "1", TYPE_BIGNUMBER, CALC_PERCENT_2));
        Assert.assertEquals(BigDecimal.valueOf(Double.valueOf("1.96")), calculate("2", "2", TYPE_BIGNUMBER, CALC_PERCENT_2));
        Assert.assertEquals(new BigDecimal("8.0"), calculate("10", "20", TYPE_BIGNUMBER, CALC_PERCENT_2));
        Assert.assertEquals(new BigDecimal("50.0"), calculate("100", "50", TYPE_BIGNUMBER, CALC_PERCENT_2));
    }

    @Test
    public void testPercent3() {
        // Test Kettle number types
        Assert.assertEquals(Double.valueOf("1.01"), calculate("1", "1", TYPE_NUMBER, CALC_PERCENT_3));
        Assert.assertEquals(Double.valueOf("2.04"), calculate("2", "2", TYPE_NUMBER, CALC_PERCENT_3));
        Assert.assertEquals(Double.valueOf("12.0"), calculate("10", "20", TYPE_NUMBER, CALC_PERCENT_3));
        Assert.assertEquals(Double.valueOf("150.0"), calculate("100", "50", TYPE_NUMBER, CALC_PERCENT_3));
        // Test Kettle Integer (Java Long) types
        Assert.assertEquals(Long.valueOf("1"), calculate("1", "1", TYPE_INTEGER, CALC_PERCENT_3));
        Assert.assertEquals(Long.valueOf("2"), calculate("2", "2", TYPE_INTEGER, CALC_PERCENT_3));
        Assert.assertEquals(Long.valueOf("12"), calculate("10", "20", TYPE_INTEGER, CALC_PERCENT_3));
        Assert.assertEquals(Long.valueOf("150"), calculate("100", "50", TYPE_INTEGER, CALC_PERCENT_3));
        // Test Kettle big Number types
        Assert.assertEquals(0, new BigDecimal("1.01").compareTo(((BigDecimal) (calculate("1", "1", TYPE_BIGNUMBER, CALC_PERCENT_3)))));
        Assert.assertEquals(0, new BigDecimal("2.04").compareTo(((BigDecimal) (calculate("2", "2", TYPE_BIGNUMBER, CALC_PERCENT_3)))));
        Assert.assertEquals(0, new BigDecimal("12").compareTo(((BigDecimal) (calculate("10", "20", TYPE_BIGNUMBER, CALC_PERCENT_3)))));
        Assert.assertEquals(0, new BigDecimal("150").compareTo(((BigDecimal) (calculate("100", "50", TYPE_BIGNUMBER, CALC_PERCENT_3)))));
    }

    @Test
    public void testCombination1() {
        // Test Kettle number types
        Assert.assertEquals(Double.valueOf("2.0"), calculate("1", "1", "1", TYPE_NUMBER, CALC_COMBINATION_1));
        Assert.assertEquals(Double.valueOf("22.0"), calculate("2", "2", "10", TYPE_NUMBER, CALC_COMBINATION_1));
        Assert.assertEquals(Double.valueOf("70.0"), calculate("10", "20", "3", TYPE_NUMBER, CALC_COMBINATION_1));
        Assert.assertEquals(Double.valueOf("350"), calculate("100", "50", "5", TYPE_NUMBER, CALC_COMBINATION_1));
        // Test Kettle Integer (Java Long) types
        Assert.assertEquals(Long.valueOf("2"), calculate("1", "1", "1", TYPE_INTEGER, CALC_COMBINATION_1));
        Assert.assertEquals(Long.valueOf("22"), calculate("2", "2", "10", TYPE_INTEGER, CALC_COMBINATION_1));
        Assert.assertEquals(Long.valueOf("70"), calculate("10", "20", "3", TYPE_INTEGER, CALC_COMBINATION_1));
        Assert.assertEquals(Long.valueOf("350"), calculate("100", "50", "5", TYPE_INTEGER, CALC_COMBINATION_1));
        // Test Kettle big Number types
        Assert.assertEquals(0, new BigDecimal("2.0").compareTo(((BigDecimal) (calculate("1", "1", "1", TYPE_BIGNUMBER, CALC_COMBINATION_1)))));
        Assert.assertEquals(0, new BigDecimal("22.0").compareTo(((BigDecimal) (calculate("2", "2", "10", TYPE_BIGNUMBER, CALC_COMBINATION_1)))));
        Assert.assertEquals(0, new BigDecimal("70.0").compareTo(((BigDecimal) (calculate("10", "20", "3", TYPE_BIGNUMBER, CALC_COMBINATION_1)))));
        Assert.assertEquals(0, new BigDecimal("350.0").compareTo(((BigDecimal) (calculate("100", "50", "5", TYPE_BIGNUMBER, CALC_COMBINATION_1)))));
    }

    @Test
    public void testCombination2() {
        // Test Kettle number types
        Assert.assertEquals(Double.valueOf("1.4142135623730951"), calculate("1", "1", TYPE_NUMBER, CALC_COMBINATION_2));
        Assert.assertEquals(Double.valueOf("2.8284271247461903"), calculate("2", "2", TYPE_NUMBER, CALC_COMBINATION_2));
        Assert.assertEquals(Double.valueOf("22.360679774997898"), calculate("10", "20", TYPE_NUMBER, CALC_COMBINATION_2));
        Assert.assertEquals(Double.valueOf("111.80339887498948"), calculate("100", "50", TYPE_NUMBER, CALC_COMBINATION_2));
        // Test Kettle Integer (Java Long) types
        Assert.assertEquals(Long.valueOf("1"), calculate("1", "1", TYPE_INTEGER, CALC_COMBINATION_2));
        Assert.assertEquals(Long.valueOf("2"), calculate("2", "2", TYPE_INTEGER, CALC_COMBINATION_2));
        Assert.assertEquals(Long.valueOf("10"), calculate("10", "20", TYPE_INTEGER, CALC_COMBINATION_2));
        Assert.assertEquals(Long.valueOf("100"), calculate("100", "50", TYPE_INTEGER, CALC_COMBINATION_2));
        // Test Kettle big Number types
        Assert.assertEquals(0, new BigDecimal("1.4142135623730951").compareTo(((BigDecimal) (calculate("1", "1", TYPE_BIGNUMBER, CALC_COMBINATION_2)))));
        Assert.assertEquals(0, new BigDecimal("2.8284271247461903").compareTo(((BigDecimal) (calculate("2", "2", TYPE_BIGNUMBER, CALC_COMBINATION_2)))));
        Assert.assertEquals(0, new BigDecimal("22.360679774997898").compareTo(((BigDecimal) (calculate("10", "20", TYPE_BIGNUMBER, CALC_COMBINATION_2)))));
        Assert.assertEquals(0, new BigDecimal("111.80339887498948").compareTo(((BigDecimal) (calculate("100", "50", TYPE_BIGNUMBER, CALC_COMBINATION_2)))));
    }

    @Test
    public void testRound() {
        // Test Kettle number types
        Assert.assertEquals(Double.valueOf("1.0"), calculate("1", TYPE_NUMBER, CALC_ROUND_1));
        Assert.assertEquals(Double.valueOf("103.0"), calculate("103.01", TYPE_NUMBER, CALC_ROUND_1));
        Assert.assertEquals(Double.valueOf("1235.0"), calculate("1234.6", TYPE_NUMBER, CALC_ROUND_1));
        // half
        Assert.assertEquals(Double.valueOf("1235.0"), calculate("1234.5", TYPE_NUMBER, CALC_ROUND_1));
        Assert.assertEquals(Double.valueOf("1236.0"), calculate("1235.5", TYPE_NUMBER, CALC_ROUND_1));
        Assert.assertEquals(Double.valueOf("-1234.0"), calculate("-1234.5", TYPE_NUMBER, CALC_ROUND_1));
        Assert.assertEquals(Double.valueOf("-1235.0"), calculate("-1235.5", TYPE_NUMBER, CALC_ROUND_1));
        // Test Kettle Integer (Java Long) types
        Assert.assertEquals(Long.valueOf("1"), calculate("1", TYPE_INTEGER, CALC_ROUND_1));
        Assert.assertEquals(Long.valueOf("2"), calculate("2", TYPE_INTEGER, CALC_ROUND_1));
        Assert.assertEquals(Long.valueOf("-103"), calculate("-103", TYPE_INTEGER, CALC_ROUND_1));
        // Test Kettle big Number types
        Assert.assertEquals(BigDecimal.ONE, calculate("1", TYPE_BIGNUMBER, CALC_ROUND_1));
        Assert.assertEquals(BigDecimal.valueOf(Long.valueOf("103")), calculate("103.01", TYPE_BIGNUMBER, CALC_ROUND_1));
        Assert.assertEquals(BigDecimal.valueOf(Long.valueOf("1235")), calculate("1234.6", TYPE_BIGNUMBER, CALC_ROUND_1));
        // half
        Assert.assertEquals(BigDecimal.valueOf(Long.valueOf("1235")), calculate("1234.5", TYPE_BIGNUMBER, CALC_ROUND_1));
        Assert.assertEquals(BigDecimal.valueOf(Long.valueOf("1236")), calculate("1235.5", TYPE_BIGNUMBER, CALC_ROUND_1));
        Assert.assertEquals(BigDecimal.valueOf(Long.valueOf("-1234")), calculate("-1234.5", TYPE_BIGNUMBER, CALC_ROUND_1));
        Assert.assertEquals(BigDecimal.valueOf(Long.valueOf("-1235")), calculate("-1235.5", TYPE_BIGNUMBER, CALC_ROUND_1));
    }

    @Test
    public void testRound2() {
        // Test Kettle number types
        Assert.assertEquals(Double.valueOf("1.0"), calculate("1", "1", TYPE_NUMBER, CALC_ROUND_2));
        Assert.assertEquals(Double.valueOf("2.1"), calculate("2.06", "1", TYPE_NUMBER, CALC_ROUND_2));
        Assert.assertEquals(Double.valueOf("103.0"), calculate("103.01", "1", TYPE_NUMBER, CALC_ROUND_2));
        Assert.assertEquals(Double.valueOf("12.35"), calculate("12.346", "2", TYPE_NUMBER, CALC_ROUND_2));
        // scale < 0
        Assert.assertEquals(Double.valueOf("10.0"), calculate("12.0", "-1", TYPE_NUMBER, CALC_ROUND_2));
        // half
        Assert.assertEquals(Double.valueOf("12.35"), calculate("12.345", "2", TYPE_NUMBER, CALC_ROUND_2));
        Assert.assertEquals(Double.valueOf("12.36"), calculate("12.355", "2", TYPE_NUMBER, CALC_ROUND_2));
        Assert.assertEquals(Double.valueOf("-12.34"), calculate("-12.345", "2", TYPE_NUMBER, CALC_ROUND_2));
        Assert.assertEquals(Double.valueOf("-12.35"), calculate("-12.355", "2", TYPE_NUMBER, CALC_ROUND_2));
        // Test Kettle Integer (Java Long) types
        Assert.assertEquals(Long.valueOf("1"), calculate("1", "1", TYPE_INTEGER, CALC_ROUND_2));
        Assert.assertEquals(Long.valueOf("2"), calculate("2", "2", TYPE_INTEGER, CALC_ROUND_2));
        Assert.assertEquals(Long.valueOf("103"), calculate("103", "3", TYPE_INTEGER, CALC_ROUND_2));
        Assert.assertEquals(Long.valueOf("12"), calculate("12", "4", TYPE_INTEGER, CALC_ROUND_2));
        // scale < 0
        Assert.assertEquals(Long.valueOf("100"), calculate("120", "-2", TYPE_INTEGER, CALC_ROUND_2));
        // half
        Assert.assertEquals(Long.valueOf("12350"), calculate("12345", "-1", TYPE_INTEGER, CALC_ROUND_2));
        Assert.assertEquals(Long.valueOf("12360"), calculate("12355", "-1", TYPE_INTEGER, CALC_ROUND_2));
        Assert.assertEquals(Long.valueOf("-12340"), calculate("-12345", "-1", TYPE_INTEGER, CALC_ROUND_2));
        Assert.assertEquals(Long.valueOf("-12350"), calculate("-12355", "-1", TYPE_INTEGER, CALC_ROUND_2));
        // Test Kettle big Number types
        Assert.assertEquals(BigDecimal.valueOf(Double.valueOf("1.0")), calculate("1", "1", TYPE_BIGNUMBER, CALC_ROUND_2));
        Assert.assertEquals(BigDecimal.valueOf(Double.valueOf("2.1")), calculate("2.06", "1", TYPE_BIGNUMBER, CALC_ROUND_2));
        Assert.assertEquals(BigDecimal.valueOf(Double.valueOf("103.0")), calculate("103.01", "1", TYPE_BIGNUMBER, CALC_ROUND_2));
        Assert.assertEquals(BigDecimal.valueOf(Double.valueOf("12.35")), calculate("12.346", "2", TYPE_BIGNUMBER, CALC_ROUND_2));
        // scale < 0
        Assert.assertEquals(BigDecimal.valueOf(Double.valueOf("10.0")).setScale((-1)), calculate("12.0", "-1", TYPE_BIGNUMBER, CALC_ROUND_2));
        // half
        Assert.assertEquals(BigDecimal.valueOf(Double.valueOf("12.35")), calculate("12.345", "2", TYPE_BIGNUMBER, CALC_ROUND_2));
        Assert.assertEquals(BigDecimal.valueOf(Double.valueOf("12.36")), calculate("12.355", "2", TYPE_BIGNUMBER, CALC_ROUND_2));
        Assert.assertEquals(BigDecimal.valueOf(Double.valueOf("-12.34")), calculate("-12.345", "2", TYPE_BIGNUMBER, CALC_ROUND_2));
        Assert.assertEquals(BigDecimal.valueOf(Double.valueOf("-12.35")), calculate("-12.355", "2", TYPE_BIGNUMBER, CALC_ROUND_2));
    }

    @Test
    public void testNVL() {
        // Test Kettle number types
        Assert.assertEquals(Double.valueOf("1.0"), calculate("1", "", TYPE_NUMBER, CALC_NVL));
        Assert.assertEquals(Double.valueOf("2.0"), calculate("", "2", TYPE_NUMBER, CALC_NVL));
        Assert.assertEquals(Double.valueOf("10.0"), calculate("10", "20", TYPE_NUMBER, CALC_NVL));
        Assert.assertEquals(null, calculate("", "", TYPE_NUMBER, CALC_NVL));
        // Test Kettle string types
        Assert.assertEquals("1", calculate("1", "", TYPE_STRING, CALC_NVL));
        Assert.assertEquals("2", calculate("", "2", TYPE_STRING, CALC_NVL));
        Assert.assertEquals("10", calculate("10", "20", TYPE_STRING, CALC_NVL));
        Assert.assertEquals(null, calculate("", "", TYPE_STRING, CALC_NVL));
        // Test Kettle Integer (Java Long) types
        Assert.assertEquals(Long.valueOf("1"), calculate("1", "", TYPE_INTEGER, CALC_NVL));
        Assert.assertEquals(Long.valueOf("2"), calculate("", "2", TYPE_INTEGER, CALC_NVL));
        Assert.assertEquals(Long.valueOf("10"), calculate("10", "20", TYPE_INTEGER, CALC_NVL));
        Assert.assertEquals(null, calculate("", "", TYPE_INTEGER, CALC_NVL));
        // Test Kettle big Number types
        Assert.assertEquals(0, new BigDecimal("1").compareTo(((BigDecimal) (calculate("1", "", TYPE_BIGNUMBER, CALC_NVL)))));
        Assert.assertEquals(0, new BigDecimal("2").compareTo(((BigDecimal) (calculate("", "2", TYPE_BIGNUMBER, CALC_NVL)))));
        Assert.assertEquals(0, new BigDecimal("10").compareTo(((BigDecimal) (calculate("10", "20", TYPE_BIGNUMBER, CALC_NVL)))));
        Assert.assertEquals(null, calculate("", "", TYPE_BIGNUMBER, CALC_NVL));
        // boolean
        Assert.assertEquals(true, calculate("true", "", TYPE_BOOLEAN, CALC_NVL));
        Assert.assertEquals(false, calculate("", "false", TYPE_BOOLEAN, CALC_NVL));
        Assert.assertEquals(false, calculate("false", "true", TYPE_BOOLEAN, CALC_NVL));
        Assert.assertEquals(null, calculate("", "", TYPE_BOOLEAN, CALC_NVL));
        // Test Kettle date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ValueDataUtilTest.yyyy_MM_dd);
        try {
            Assert.assertEquals(simpleDateFormat.parse("2012-04-11"), calculate("2012-04-11", "", TYPE_DATE, CALC_NVL));
            Assert.assertEquals(simpleDateFormat.parse("2012-11-04"), calculate("", "2012-11-04", TYPE_DATE, CALC_NVL));
            Assert.assertEquals(simpleDateFormat.parse("1965-07-01"), calculate("1965-07-01", "1967-04-11", TYPE_DATE, CALC_NVL));
            Assert.assertNull(calculate("", "", TYPE_DATE, CALC_NVL));
        } catch (ParseException pe) {
            Assert.fail(pe.getMessage());
        }
        // assertEquals(0, calculate("", "2012-11-04", ValueMetaInterface.TYPE_DATE, CalculatorMetaFunction.CALC_NVL)));
        // assertEquals(0, calculate("2012-11-04", "2010-04-11", ValueMetaInterface.TYPE_DATE,
        // CalculatorMetaFunction.CALC_NVL)));
        // assertEquals(null, calculate("", "", ValueMetaInterface.TYPE_DATE, CalculatorMetaFunction.CALC_NVL));
        // binary
        ValueMetaInterface stringValueMeta = new ValueMetaString("string");
        try {
            byte[] data = stringValueMeta.getBinary("101");
            byte[] calculated = ((byte[]) (calculate("101", "", TYPE_BINARY, CALC_NVL)));
            Assert.assertTrue(Arrays.equals(data, calculated));
            data = stringValueMeta.getBinary("011");
            calculated = ((byte[]) (calculate("", "011", TYPE_BINARY, CALC_NVL)));
            Assert.assertTrue(Arrays.equals(data, calculated));
            data = stringValueMeta.getBinary("110");
            calculated = ((byte[]) (calculate("110", "011", TYPE_BINARY, CALC_NVL)));
            Assert.assertTrue(Arrays.equals(data, calculated));
            calculated = ((byte[]) (calculate("", "", TYPE_BINARY, CALC_NVL)));
            Assert.assertNull(calculated);
            // assertEquals(binaryValueMeta.convertData(new ValueMeta("dummy", ValueMeta.TYPE_STRING), "101"),
            // calculate("101", "", ValueMetaInterface.TYPE_BINARY, CalculatorMetaFunction.CALC_NVL));
        } catch (KettleValueException kve) {
            Assert.fail(kve.getMessage());
        }
    }

    @Test
    public void testRemainder() throws Exception {
        Assert.assertNull(calculate(null, null, TYPE_INTEGER, CALC_REMAINDER));
        Assert.assertNull(calculate(null, "3", TYPE_INTEGER, CALC_REMAINDER));
        Assert.assertNull(calculate("10", null, TYPE_INTEGER, CALC_REMAINDER));
        Assert.assertEquals(new Long("1"), calculate("10", "3", TYPE_INTEGER, CALC_REMAINDER));
        Assert.assertEquals(new Long("-1"), calculate("-10", "3", TYPE_INTEGER, CALC_REMAINDER));
        Double comparisonDelta = new Double("0.0000000000001");
        Assert.assertNull(calculate(null, null, TYPE_NUMBER, CALC_REMAINDER));
        Assert.assertNull(calculate(null, "4.1", TYPE_NUMBER, CALC_REMAINDER));
        Assert.assertNull(calculate("17.8", null, TYPE_NUMBER, CALC_REMAINDER));
        Assert.assertEquals(new Double("1.4").doubleValue(), ((Double) (calculate("17.8", "4.1", TYPE_NUMBER, CALC_REMAINDER))).doubleValue(), comparisonDelta.doubleValue());
        Assert.assertEquals(new Double("1.4").doubleValue(), ((Double) (calculate("17.8", "-4.1", TYPE_NUMBER, CALC_REMAINDER))).doubleValue(), comparisonDelta.doubleValue());
        Assert.assertEquals(new Double("-1.4").doubleValue(), ((Double) (calculate("-17.8", "-4.1", TYPE_NUMBER, CALC_REMAINDER))).doubleValue(), comparisonDelta.doubleValue());
        Assert.assertNull(calculate(null, null, TYPE_BIGNUMBER, CALC_REMAINDER));
        Assert.assertNull(calculate(null, "16.12", TYPE_BIGNUMBER, CALC_REMAINDER));
        Assert.assertNull(calculate("-144.144", null, TYPE_BIGNUMBER, CALC_REMAINDER));
        Assert.assertEquals(new BigDecimal("-15.184"), calculate("-144.144", "16.12", TYPE_BIGNUMBER, CALC_REMAINDER));
        Assert.assertEquals(new Double("2.6000000000000005").doubleValue(), calculate("12.5", "3.3", TYPE_NUMBER, CALC_REMAINDER));
        Assert.assertEquals(new Double("4.0").doubleValue(), calculate("12.5", "4.25", TYPE_NUMBER, CALC_REMAINDER));
        Assert.assertEquals(new Long("1").longValue(), calculate("10", "3.3", null, TYPE_INTEGER, TYPE_NUMBER, TYPE_NUMBER, CALC_REMAINDER));
    }

    @Test
    public void testSumWithNullValues() throws Exception {
        ValueMetaInterface metaA = new ValueMetaInteger();
        metaA.setStorageType(STORAGE_TYPE_NORMAL);
        ValueMetaInterface metaB = new ValueMetaInteger();
        metaA.setStorageType(STORAGE_TYPE_NORMAL);
        Assert.assertNull(ValueDataUtil.sum(metaA, null, metaB, null));
        Long valueB = new Long(2);
        ValueDataUtil.sum(metaA, null, metaB, valueB);
    }

    @Test
    public void testSumConvertingStorageTypeToNormal() throws Exception {
        ValueMetaInterface metaA = Mockito.mock(ValueMetaInteger.class);
        metaA.setStorageType(STORAGE_TYPE_BINARY_STRING);
        ValueMetaInterface metaB = new ValueMetaInteger();
        metaB.setStorageType(STORAGE_TYPE_BINARY_STRING);
        Object valueB = "2";
        Mockito.when(metaA.convertData(metaB, valueB)).thenAnswer(new Answer<Long>() {
            @Override
            public Long answer(InvocationOnMock invocation) throws Throwable {
                return new Long(2);
            }
        });
        Object returnValue = ValueDataUtil.sum(metaA, null, metaB, valueB);
        Mockito.verify(metaA).convertData(metaB, valueB);
        Assert.assertEquals(2L, returnValue);
        Assert.assertEquals(metaA.getStorageType(), STORAGE_TYPE_NORMAL);
    }

    @Test
    public void testJaro() {
        Assert.assertEquals(new Double("0.0"), calculate("abcd", "defg", TYPE_STRING, CALC_JARO));
        Assert.assertEquals(new Double("0.44166666666666665"), calculate("elephant", "hippo", TYPE_STRING, CALC_JARO));
        Assert.assertEquals(new Double("0.8666666666666667"), calculate("hello", "hallo", TYPE_STRING, CALC_JARO));
    }

    @Test
    public void testJaroWinkler() {
        Assert.assertEquals(new Double("0.0"), calculate("abcd", "defg", TYPE_STRING, CALC_JARO_WINKLER));
    }
}

