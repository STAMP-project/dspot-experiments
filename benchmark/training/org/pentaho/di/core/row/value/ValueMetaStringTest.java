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
package org.pentaho.di.core.row.value;


import ValueMetaInterface.TRIM_TYPE_BOTH;
import ValueMetaInterface.TRIM_TYPE_LEFT;
import ValueMetaInterface.TRIM_TYPE_NONE;
import ValueMetaInterface.TRIM_TYPE_RIGHT;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;


public class ValueMetaStringTest {
    private static final String BASE_VALUE = "Some text";

    private static final String TEST_VALUE = "Some text";

    private ValueMetaStringTest.ConfigurableMeta meta;

    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    @Test
    public void testGetNativeData_emptyIsNotNull() throws Exception {
        meta.setNullsAndEmptyAreDifferent(true);
        Assert.assertEquals(ValueMetaStringTest.BASE_VALUE, getNativeDataType(ValueMetaStringTest.BASE_VALUE));
        Assert.assertEquals(ValueMetaStringTest.TEST_VALUE, getNativeDataType(ValueMetaStringTest.TEST_VALUE));
        Assert.assertEquals(null, meta.getNativeDataType(null));
        Assert.assertEquals("1", meta.getNativeDataType(1));
        Assert.assertEquals("1.0", meta.getNativeDataType(1.0));
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2012-11-10 09:08:07.654");
        Assert.assertEquals(d.toString(), meta.getNativeDataType(d));
        Timestamp ts = Timestamp.valueOf("2012-11-10 09:08:07.654321");
        Assert.assertEquals("2012-11-10 09:08:07.654321", meta.getNativeDataType(ts));
        meta.setTrimType(TRIM_TYPE_NONE);
        Assert.assertEquals("", getNativeDataType(""));
        Assert.assertEquals("1", getNativeDataType("1"));
        Assert.assertEquals("    ", getNativeDataType("    "));
        Assert.assertEquals("  1  ", getNativeDataType("  1  "));
        meta.setTrimType(TRIM_TYPE_LEFT);
        Assert.assertEquals("", getNativeDataType(""));
        Assert.assertEquals("1", getNativeDataType("1"));
        Assert.assertEquals("", getNativeDataType("    "));
        Assert.assertEquals("1  ", getNativeDataType("  1  "));
        meta.setTrimType(TRIM_TYPE_RIGHT);
        Assert.assertEquals("", getNativeDataType(""));
        Assert.assertEquals("1", getNativeDataType("1"));
        Assert.assertEquals("", getNativeDataType("    "));
        Assert.assertEquals("  1", getNativeDataType("  1  "));
        meta.setTrimType(TRIM_TYPE_BOTH);
        Assert.assertEquals("", getNativeDataType(""));
        Assert.assertEquals("1", getNativeDataType("1"));
        Assert.assertEquals("", getNativeDataType("    "));
        Assert.assertEquals("1", getNativeDataType("  1  "));
    }

    @Test
    public void testGetNativeData_emptyIsNull() throws Exception {
        meta.setNullsAndEmptyAreDifferent(false);
        Assert.assertEquals(ValueMetaStringTest.BASE_VALUE, getNativeDataType(ValueMetaStringTest.BASE_VALUE));
        Assert.assertEquals(ValueMetaStringTest.TEST_VALUE, getNativeDataType(ValueMetaStringTest.TEST_VALUE));
        Assert.assertEquals(null, meta.getNativeDataType(null));
        Assert.assertEquals("1", meta.getNativeDataType(1));
        Assert.assertEquals("1.0", meta.getNativeDataType(1.0));
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2012-11-10 09:08:07.654");
        Assert.assertEquals(d.toString(), meta.getNativeDataType(d));
        Timestamp ts = Timestamp.valueOf("2012-11-10 09:08:07.654321");
        Assert.assertEquals("2012-11-10 09:08:07.654321", meta.getNativeDataType(ts));
        meta.setTrimType(TRIM_TYPE_NONE);
        // assertEquals( null, meta.getNativeDataType( "" ) ); //TODO: is it correct?
        Assert.assertEquals("", getNativeDataType(""));// TODO: is it correct?

        Assert.assertEquals("1", getNativeDataType("1"));
        Assert.assertEquals("    ", getNativeDataType("    "));
        Assert.assertEquals("  1  ", getNativeDataType("  1  "));
        meta.setTrimType(TRIM_TYPE_LEFT);
        // assertEquals( null, meta.getNativeDataType( "" ) ); //TODO: is it correct?
        Assert.assertEquals("", getNativeDataType(""));// TODO: is it correct?

        Assert.assertEquals("1", getNativeDataType("1"));
        // assertEquals( null, meta.getNativeDataType( "    " ) ); //TODO: is it correct?
        Assert.assertEquals("", getNativeDataType("    "));// TODO: is it correct?

        Assert.assertEquals("1  ", getNativeDataType("  1  "));
        meta.setTrimType(TRIM_TYPE_RIGHT);
        // assertEquals( null, meta.getNativeDataType( "" ) ); //TODO: is it correct?
        Assert.assertEquals("", getNativeDataType(""));// TODO: is it correct?

        Assert.assertEquals("1", getNativeDataType("1"));
        // assertEquals( null, meta.getNativeDataType( "    " ) ); //TODO: is it correct?
        Assert.assertEquals("", getNativeDataType("    "));// TODO: is it correct?

        Assert.assertEquals("  1", getNativeDataType("  1  "));
        meta.setTrimType(TRIM_TYPE_BOTH);
        // assertEquals( null, meta.getNativeDataType( "" ) ); //TODO: is it correct?
        Assert.assertEquals("", getNativeDataType(""));// TODO: is it correct?

        Assert.assertEquals("1", getNativeDataType("1"));
        // assertEquals( null, meta.getNativeDataType( "    " ) ); //TODO: is it correct?
        Assert.assertEquals("", getNativeDataType("    "));// TODO: is it correct?

        Assert.assertEquals("1", getNativeDataType("  1  "));
    }

    @Test
    public void testIsNull_emptyIsNotNull() throws KettleValueException {
        meta.setNullsAndEmptyAreDifferent(true);
        Assert.assertEquals(true, meta.isNull(null));
        Assert.assertEquals(false, meta.isNull(""));
        Assert.assertEquals(false, meta.isNull("1"));
        meta.setTrimType(TRIM_TYPE_NONE);
        Assert.assertEquals(false, meta.isNull("    "));
        Assert.assertEquals(false, meta.isNull("  1  "));
        meta.setTrimType(TRIM_TYPE_LEFT);
        Assert.assertEquals(false, meta.isNull("    "));
        Assert.assertEquals(false, meta.isNull("  1  "));
        meta.setTrimType(TRIM_TYPE_RIGHT);
        Assert.assertEquals(false, meta.isNull("    "));
        Assert.assertEquals(false, meta.isNull("  1  "));
        meta.setTrimType(TRIM_TYPE_BOTH);
        Assert.assertEquals(false, meta.isNull("    "));
        Assert.assertEquals(false, meta.isNull("  1  "));
    }

    @Test
    public void testIsNull_emptyIsNull() throws KettleValueException {
        meta.setNullsAndEmptyAreDifferent(false);
        Assert.assertEquals(true, meta.isNull(null));
        Assert.assertEquals(true, meta.isNull(""));
        Assert.assertEquals(false, meta.isNull("1"));
        meta.setTrimType(TRIM_TYPE_NONE);
        Assert.assertEquals(false, meta.isNull("    "));
        Assert.assertEquals(false, meta.isNull(getString("    ")));
        Assert.assertEquals(false, meta.isNull("  1  "));
        Assert.assertEquals(false, meta.isNull(getString("  1  ")));
        meta.setTrimType(TRIM_TYPE_LEFT);
        // assertEquals( true, meta.isNull( "    " ) ); //TODO: is it correct?
        Assert.assertEquals(false, meta.isNull("    "));// TODO: is it correct?

        Assert.assertEquals(true, meta.isNull(getString("    ")));
        Assert.assertEquals(false, meta.isNull("  1  "));
        Assert.assertEquals(false, meta.isNull(getString("  1  ")));
        meta.setTrimType(TRIM_TYPE_RIGHT);
        // assertEquals( true, meta.isNull( "    " ) ); //TODO: is it correct?
        Assert.assertEquals(false, meta.isNull("    "));// TODO: is it correct?

        Assert.assertEquals(true, meta.isNull(getString("    ")));
        Assert.assertEquals(false, meta.isNull("  1  "));
        Assert.assertEquals(false, meta.isNull(getString("  1  ")));
        meta.setTrimType(TRIM_TYPE_BOTH);
        // assertEquals( true, meta.isNull( "    " ) ); //TODO: is it correct?
        Assert.assertEquals(false, meta.isNull("    "));// TODO: is it correct?

        Assert.assertEquals(true, meta.isNull(getString("    ")));
        Assert.assertEquals(false, meta.isNull("  1  "));
        Assert.assertEquals(false, meta.isNull(getString("  1  ")));
    }

    @Test
    public void testGetString_emptyIsNotNull() throws KettleValueException {
        meta.setNullsAndEmptyAreDifferent(true);
        Assert.assertEquals(null, meta.getString(null));
        Assert.assertEquals("", getString(""));
        meta.setTrimType(TRIM_TYPE_NONE);
        Assert.assertEquals("    ", getString("    "));
        Assert.assertEquals("  1  ", getString("  1  "));
        meta.setTrimType(TRIM_TYPE_LEFT);
        Assert.assertEquals("", getString("    "));
        Assert.assertEquals("1  ", getString("  1  "));
        meta.setTrimType(TRIM_TYPE_RIGHT);
        Assert.assertEquals("", getString("    "));
        Assert.assertEquals("  1", getString("  1  "));
        meta.setTrimType(TRIM_TYPE_BOTH);
        Assert.assertEquals("", getString("    "));
        Assert.assertEquals("1", getString("  1  "));
    }

    @Test
    public void testGetString_emptyIsNull() throws KettleValueException {
        meta.setNullsAndEmptyAreDifferent(false);
        Assert.assertEquals(null, meta.getString(null));
        // assertEquals( null, meta.getString( "" ) ); // TODO: is it correct?
        Assert.assertEquals("", getString(""));// TODO: is it correct?

        meta.setTrimType(TRIM_TYPE_NONE);
        Assert.assertEquals("    ", getString("    "));
        Assert.assertEquals("  1  ", getString("  1  "));
        meta.setTrimType(TRIM_TYPE_LEFT);
        // assertEquals( null, meta.getString( "    " ) ); // TODO: is it correct?
        Assert.assertEquals("", getString("    "));// TODO: is it correct?

        Assert.assertEquals("1  ", getString("  1  "));
        meta.setTrimType(TRIM_TYPE_RIGHT);
        // assertEquals( null, meta.getString( "    " ) ); // TODO: is it correct?
        Assert.assertEquals("", getString("    "));// TODO: is it correct?

        Assert.assertEquals("  1", getString("  1  "));
        meta.setTrimType(TRIM_TYPE_BOTH);
        // assertEquals( null, meta.getString( "    " ) ); // TODO: is it correct?
        Assert.assertEquals("", getString("    "));// TODO: is it correct?

        Assert.assertEquals("1", getString("  1  "));
    }

    @Test
    public void testCompare_emptyIsNotNull() throws KettleValueException {
        meta.setNullsAndEmptyAreDifferent(true);
        meta.setTrimType(TRIM_TYPE_NONE);
        ValueMetaStringTest.assertSignum(0, meta.compare(null, null));// null == null

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, ""));// null < ""

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " "));// null < " "

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " 1"));// null < " 1"

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " 1 "));// null < " 1 "

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, "1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, "1 "));// null < "1 "

        ValueMetaStringTest.assertSignum(1, meta.compare("", null));// "" > null

        ValueMetaStringTest.assertSignum(0, compare("", ""));// "" == ""

        ValueMetaStringTest.assertSignum((-1), compare("", " "));// "" < " "

        ValueMetaStringTest.assertSignum((-1), compare("", " 1"));// "" < " 1"

        ValueMetaStringTest.assertSignum((-1), compare("", " 1 "));// "" < " 1 "

        ValueMetaStringTest.assertSignum((-1), compare("", "1"));// "" < "1"

        ValueMetaStringTest.assertSignum((-1), compare("", "1 "));// "" < "1 "

        ValueMetaStringTest.assertSignum(1, meta.compare(" ", null));// " " > null

        ValueMetaStringTest.assertSignum(1, compare(" ", ""));// " " > ""

        ValueMetaStringTest.assertSignum(0, compare(" ", " "));// " " == " "

        ValueMetaStringTest.assertSignum((-1), compare(" ", " 1"));// " " < " 1"

        ValueMetaStringTest.assertSignum((-1), compare(" ", " 1 "));// " " < " 1 "

        ValueMetaStringTest.assertSignum((-1), compare(" ", "1"));// " " < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" ", "1 "));// " " < "1 "

        ValueMetaStringTest.assertSignum(1, meta.compare(" 1", null));// " 1" > null

        ValueMetaStringTest.assertSignum(1, compare(" 1", ""));// " 1" > ""

        ValueMetaStringTest.assertSignum(1, compare(" 1", " "));// " 1" > " "

        ValueMetaStringTest.assertSignum(0, compare(" 1", " 1"));// " 1" == " 1"

        ValueMetaStringTest.assertSignum((-1), compare(" 1", " 1 "));// " 1" < " 1 "

        ValueMetaStringTest.assertSignum((-1), compare(" 1", "1"));// " 1" < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" 1", "1 "));// " 1" < "1 "

        ValueMetaStringTest.assertSignum(1, meta.compare(" 1 ", null));// " 1 " > null

        ValueMetaStringTest.assertSignum(1, compare(" 1 ", ""));// " 1 " > ""

        ValueMetaStringTest.assertSignum(1, compare(" 1 ", " "));// " 1 " > " "

        ValueMetaStringTest.assertSignum(1, compare(" 1 ", " 1"));// " 1 " > " 1"

        ValueMetaStringTest.assertSignum(0, compare(" 1 ", " 1 "));// " 1 " == " 1 "

        ValueMetaStringTest.assertSignum((-1), compare(" 1 ", "1"));// " 1 " < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" 1 ", "1 "));// " 1 " < "1 "

        ValueMetaStringTest.assertSignum(1, meta.compare("1", null));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare("1", ""));// "1" > ""

        ValueMetaStringTest.assertSignum(1, compare("1", " "));// "1" > " "

        ValueMetaStringTest.assertSignum(1, compare("1", " 1"));// "1" > " 1"

        ValueMetaStringTest.assertSignum(1, compare("1", " 1 "));// "1" > " 1 "

        ValueMetaStringTest.assertSignum(0, compare("1", "1"));// "1" == "1"

        ValueMetaStringTest.assertSignum((-1), compare("1", "1 "));// "1" < "1 "

        ValueMetaStringTest.assertSignum(1, meta.compare("1 ", null));// "1 " > null

        ValueMetaStringTest.assertSignum(1, compare("1 ", ""));// "1 " > ""

        ValueMetaStringTest.assertSignum(1, compare("1 ", " "));// "1 " > " "

        ValueMetaStringTest.assertSignum(1, compare("1 ", " 1"));// "1 " > " 1"

        ValueMetaStringTest.assertSignum(1, compare("1 ", " 1 "));// "1 " > " 1 "

        ValueMetaStringTest.assertSignum(1, compare("1 ", "1"));// "1 " > "1"

        ValueMetaStringTest.assertSignum(0, compare("1 ", "1 "));// "1 " == "1 "

        meta.setTrimType(TRIM_TYPE_LEFT);
        ValueMetaStringTest.assertSignum(0, meta.compare(null, null));// null == null

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, ""));// null < ""

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " "));// null < ""

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " 1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " 1 "));// null < "1 "

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, "1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, "1 "));// null < "1 "

        ValueMetaStringTest.assertSignum(1, meta.compare("", null));// "" > null

        ValueMetaStringTest.assertSignum(0, compare("", ""));// "" == ""

        ValueMetaStringTest.assertSignum(0, compare("", " "));// "" == ""

        ValueMetaStringTest.assertSignum((-1), compare("", " 1"));// "" < "1"

        ValueMetaStringTest.assertSignum((-1), compare("", " 1 "));// "" < "1 "

        ValueMetaStringTest.assertSignum((-1), compare("", "1"));// "" < "1"

        ValueMetaStringTest.assertSignum((-1), compare("", "1 "));// "" < "1 "

        ValueMetaStringTest.assertSignum(1, meta.compare(" ", null));// "" > null

        ValueMetaStringTest.assertSignum(0, compare(" ", ""));// "" == ""

        ValueMetaStringTest.assertSignum(0, compare(" ", " "));// "" == ""

        ValueMetaStringTest.assertSignum((-1), compare(" ", " 1"));// "" < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" ", " 1 "));// "" < "1 "

        ValueMetaStringTest.assertSignum((-1), compare(" ", "1"));// "" < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" ", "1 "));// "" < "1 "

        ValueMetaStringTest.assertSignum(1, meta.compare(" 1", null));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare(" 1", ""));// "1" > ""

        ValueMetaStringTest.assertSignum(1, compare(" 1", " "));// "1" > ""

        ValueMetaStringTest.assertSignum(0, compare(" 1", " 1"));// "1" == "1"

        ValueMetaStringTest.assertSignum((-1), compare(" 1", " 1 "));// "1" < "1 "

        ValueMetaStringTest.assertSignum(0, compare(" 1", "1"));// "1" == "1"

        ValueMetaStringTest.assertSignum((-1), compare(" 1", "1 "));// "1" < "1 "

        ValueMetaStringTest.assertSignum(1, meta.compare(" 1 ", null));// "1 " > null

        ValueMetaStringTest.assertSignum(1, compare(" 1 ", ""));// "1 " > ""

        ValueMetaStringTest.assertSignum(1, compare(" 1 ", " "));// "1 " > ""

        ValueMetaStringTest.assertSignum(1, compare(" 1 ", " 1"));// "1 " > "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1 ", " 1 "));// "1 " == "1 "

        ValueMetaStringTest.assertSignum(1, compare(" 1 ", "1"));// "1 " > "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1 ", "1 "));// "1 " == "1 "

        ValueMetaStringTest.assertSignum(1, meta.compare("1", null));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare("1", ""));// "1" > ""

        ValueMetaStringTest.assertSignum(1, compare("1", " "));// "1" > ""

        ValueMetaStringTest.assertSignum(0, compare("1", " 1"));// "1" == "1"

        ValueMetaStringTest.assertSignum((-1), compare("1", " 1 "));// "1" < "1 "

        ValueMetaStringTest.assertSignum(0, compare("1", "1"));// "1" == "1"

        ValueMetaStringTest.assertSignum((-1), compare("1", "1 "));// "1" < "1 "

        ValueMetaStringTest.assertSignum(1, meta.compare("1 ", null));// "1 " > null

        ValueMetaStringTest.assertSignum(1, compare("1 ", ""));// "1 " > ""

        ValueMetaStringTest.assertSignum(1, compare("1 ", " "));// "1 " > ""

        ValueMetaStringTest.assertSignum(1, compare("1 ", " 1"));// "1 " > "1"

        ValueMetaStringTest.assertSignum(0, compare("1 ", " 1 "));// "1 " == "1 "

        ValueMetaStringTest.assertSignum(1, compare("1 ", "1"));// "1 " > "1"

        ValueMetaStringTest.assertSignum(0, compare("1 ", "1 "));// "1 " == "1 "

        meta.setTrimType(TRIM_TYPE_RIGHT);
        ValueMetaStringTest.assertSignum(0, meta.compare(null, null));// null == null

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, ""));// null < ""

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " "));// null < ""

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " 1"));// null < " 1"

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " 1 "));// null < " 1"

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, "1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, "1 "));// null < "1"

        ValueMetaStringTest.assertSignum(1, meta.compare("", null));// "" > null

        ValueMetaStringTest.assertSignum(0, compare("", ""));// "" == ""

        ValueMetaStringTest.assertSignum(0, compare("", " "));// "" == ""

        ValueMetaStringTest.assertSignum((-1), compare("", " 1"));// "" < " 1"

        ValueMetaStringTest.assertSignum((-1), compare("", " 1 "));// "" < " 1"

        ValueMetaStringTest.assertSignum((-1), compare("", "1"));// "" < "1"

        ValueMetaStringTest.assertSignum((-1), compare("", "1 "));// "" < "1"

        ValueMetaStringTest.assertSignum(1, meta.compare(" ", null));// "" > null

        ValueMetaStringTest.assertSignum(0, compare(" ", ""));// "" == ""

        ValueMetaStringTest.assertSignum(0, compare(" ", " "));// "" == ""

        ValueMetaStringTest.assertSignum((-1), compare(" ", " 1"));// "" < " 1"

        ValueMetaStringTest.assertSignum((-1), compare(" ", " 1 "));// "" < " 1"

        ValueMetaStringTest.assertSignum((-1), compare(" ", "1"));// "" < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" ", "1 "));// "" < "1"

        ValueMetaStringTest.assertSignum(1, meta.compare(" 1", null));// " 1" > null

        ValueMetaStringTest.assertSignum(1, compare(" 1", ""));// " 1" > ""

        ValueMetaStringTest.assertSignum(1, compare(" 1", " "));// " 1" > ""

        ValueMetaStringTest.assertSignum(0, compare(" 1", " 1"));// " 1" == " 1"

        ValueMetaStringTest.assertSignum(0, compare(" 1", " 1 "));// " 1" == " 1"

        ValueMetaStringTest.assertSignum((-1), compare(" 1", "1"));// " 1" < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" 1", "1 "));// " 1" < "1"

        ValueMetaStringTest.assertSignum(1, meta.compare(" 1 ", null));// " 1" > null

        ValueMetaStringTest.assertSignum(1, compare(" 1 ", ""));// " 1" > ""

        ValueMetaStringTest.assertSignum(1, compare(" 1 ", " "));// " 1" > ""

        ValueMetaStringTest.assertSignum(0, compare(" 1 ", " 1"));// " 1" == " 1"

        ValueMetaStringTest.assertSignum(0, compare(" 1 ", " 1 "));// " 1" == " 1"

        ValueMetaStringTest.assertSignum((-1), compare(" 1 ", "1"));// " 1" < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" 1 ", "1 "));// " 1" < "1"

        ValueMetaStringTest.assertSignum(1, meta.compare("1", null));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare("1", ""));// "1" > ""

        ValueMetaStringTest.assertSignum(1, compare("1", " "));// "1" > ""

        ValueMetaStringTest.assertSignum(1, compare("1", " 1"));// "1" > " 1"

        ValueMetaStringTest.assertSignum(1, compare("1", " 1 "));// "1" > " 1"

        ValueMetaStringTest.assertSignum(0, compare("1", "1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1", "1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(1, meta.compare("1 ", null));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare("1 ", ""));// "1" > ""

        ValueMetaStringTest.assertSignum(1, compare("1 ", " "));// "1" > ""

        ValueMetaStringTest.assertSignum(1, compare("1 ", " 1"));// "1" > " 1"

        ValueMetaStringTest.assertSignum(1, compare("1 ", " 1 "));// "1" > " 1"

        ValueMetaStringTest.assertSignum(0, compare("1 ", "1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1 ", "1 "));// "1" == "1"

        meta.setTrimType(TRIM_TYPE_BOTH);
        ValueMetaStringTest.assertSignum(0, meta.compare(null, null));// null == null

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, ""));// null < ""

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " "));// null < ""

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " 1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " 1 "));// null < "1"

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, "1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, "1 "));// null < "1"

        ValueMetaStringTest.assertSignum(1, meta.compare("", null));// "" > null

        ValueMetaStringTest.assertSignum(0, compare("", ""));// "" == ""

        ValueMetaStringTest.assertSignum(0, compare("", " "));// "" == ""

        ValueMetaStringTest.assertSignum((-1), compare("", " 1"));// "" < "1"

        ValueMetaStringTest.assertSignum((-1), compare("", " 1 "));// "" < "1"

        ValueMetaStringTest.assertSignum((-1), compare("", "1"));// "" < "1"

        ValueMetaStringTest.assertSignum((-1), compare("", "1 "));// "" < "1"

        ValueMetaStringTest.assertSignum(1, meta.compare(" ", null));// "" > null

        ValueMetaStringTest.assertSignum(0, compare(" ", ""));// "" == ""

        ValueMetaStringTest.assertSignum(0, compare(" ", " "));// "" == ""

        ValueMetaStringTest.assertSignum((-1), compare(" ", " 1"));// "" < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" ", " 1 "));// "" < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" ", "1"));// "" < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" ", "1 "));// "" < "1"

        ValueMetaStringTest.assertSignum(1, meta.compare(" 1", null));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare(" 1", ""));// "1" > ""

        ValueMetaStringTest.assertSignum(1, compare(" 1", " "));// "1" > ""

        ValueMetaStringTest.assertSignum(0, compare(" 1", " 1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1", " 1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1", "1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1", "1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(1, meta.compare(" 1 ", null));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare(" 1 ", ""));// "1" > ""

        ValueMetaStringTest.assertSignum(1, compare(" 1 ", " "));// "1" > ""

        ValueMetaStringTest.assertSignum(0, compare(" 1 ", " 1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1 ", " 1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1 ", "1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1 ", "1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(1, meta.compare("1", null));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare("1", ""));// "1" > ""

        ValueMetaStringTest.assertSignum(1, compare("1", " "));// "1" > ""

        ValueMetaStringTest.assertSignum(0, compare("1", " 1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1", " 1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1", "1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1", "1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(1, meta.compare("1 ", null));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare("1 ", ""));// "1" > ""

        ValueMetaStringTest.assertSignum(1, compare("1 ", " "));// "1" > ""

        ValueMetaStringTest.assertSignum(0, compare("1 ", " 1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1 ", " 1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1 ", "1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1 ", "1 "));// "1" == "1"

        meta.setTrimType(TRIM_TYPE_NONE);
        setIgnoreWhitespace(true);
        ValueMetaStringTest.assertSignum(0, meta.compare(null, null));// null == null

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, ""));// null < ""

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " "));// null < ""

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " 1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " 1 "));// null < "1"

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, "1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, "1 "));// null < "1"

        ValueMetaStringTest.assertSignum(1, meta.compare("", null));// "" > null

        ValueMetaStringTest.assertSignum(0, compare("", ""));// "" == ""

        ValueMetaStringTest.assertSignum(0, compare("", " "));// "" == ""

        ValueMetaStringTest.assertSignum((-1), compare("", " 1"));// "" < "1"

        ValueMetaStringTest.assertSignum((-1), compare("", " 1 "));// "" < "1"

        ValueMetaStringTest.assertSignum((-1), compare("", "1"));// "" < "1"

        ValueMetaStringTest.assertSignum((-1), compare("", "1 "));// "" < "1"

        ValueMetaStringTest.assertSignum(1, meta.compare(" ", null));// "" > null

        ValueMetaStringTest.assertSignum(0, compare(" ", ""));// "" == ""

        ValueMetaStringTest.assertSignum(0, compare(" ", " "));// "" == ""

        ValueMetaStringTest.assertSignum((-1), compare(" ", " 1"));// "" < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" ", " 1 "));// "" < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" ", "1"));// "" < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" ", "1 "));// "" < "1"

        ValueMetaStringTest.assertSignum(1, meta.compare(" 1", null));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare(" 1", ""));// "1" > ""

        ValueMetaStringTest.assertSignum(1, compare(" 1", " "));// "1" > ""

        ValueMetaStringTest.assertSignum(0, compare(" 1", " 1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1", " 1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1", "1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1", "1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(1, meta.compare(" 1 ", null));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare(" 1 ", ""));// "1" > ""

        ValueMetaStringTest.assertSignum(1, compare(" 1 ", " "));// "1" > ""

        ValueMetaStringTest.assertSignum(0, compare(" 1 ", " 1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1 ", " 1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1 ", "1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1 ", "1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(1, meta.compare("1", null));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare("1", ""));// "1" > ""

        ValueMetaStringTest.assertSignum(1, compare("1", " "));// "1" > ""

        ValueMetaStringTest.assertSignum(0, compare("1", " 1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1", " 1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1", "1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1", "1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(1, meta.compare("1 ", null));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare("1 ", ""));// "1" > ""

        ValueMetaStringTest.assertSignum(1, compare("1 ", " "));// "1" > ""

        ValueMetaStringTest.assertSignum(0, compare("1 ", " 1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1 ", " 1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1 ", "1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1 ", "1 "));// "1" == "1"

    }

    @Test
    public void testCompare_emptyIsNull() throws KettleValueException {
        meta.setNullsAndEmptyAreDifferent(false);
        meta.setTrimType(TRIM_TYPE_NONE);
        ValueMetaStringTest.assertSignum(0, meta.compare(null, null));// null == null

        ValueMetaStringTest.assertSignum(0, meta.compare(null, ""));// null == null

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " "));// null < " "

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " 1"));// null < " 1"

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " 1 "));// null < " 1 "

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, "1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, "1 "));// null < "1 "

        ValueMetaStringTest.assertSignum(0, meta.compare("", null));// null > null

        ValueMetaStringTest.assertSignum(0, compare("", ""));// null == null

        ValueMetaStringTest.assertSignum((-1), compare("", " "));// null < " "

        ValueMetaStringTest.assertSignum((-1), compare("", " 1"));// null < " 1"

        ValueMetaStringTest.assertSignum((-1), compare("", " 1 "));// null < " 1 "

        ValueMetaStringTest.assertSignum((-1), compare("", "1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), compare("", "1 "));// null < "1 "

        ValueMetaStringTest.assertSignum(1, meta.compare(" ", null));// " " > null

        ValueMetaStringTest.assertSignum(1, compare(" ", ""));// " " > null

        ValueMetaStringTest.assertSignum(0, compare(" ", " "));// " " == " "

        ValueMetaStringTest.assertSignum((-1), compare(" ", " 1"));// " " < " 1"

        ValueMetaStringTest.assertSignum((-1), compare(" ", " 1 "));// " " < " 1 "

        ValueMetaStringTest.assertSignum((-1), compare(" ", "1"));// " " < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" ", "1 "));// " " < "1 "

        ValueMetaStringTest.assertSignum(1, meta.compare(" 1", null));// " 1" > null

        ValueMetaStringTest.assertSignum(1, compare(" 1", ""));// " 1" > null

        ValueMetaStringTest.assertSignum(1, compare(" 1", " "));// " 1" > " "

        ValueMetaStringTest.assertSignum(0, compare(" 1", " 1"));// " 1" == " 1"

        ValueMetaStringTest.assertSignum((-1), compare(" 1", " 1 "));// " 1" < " 1 "

        ValueMetaStringTest.assertSignum((-1), compare(" 1", "1"));// " 1" < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" 1", "1 "));// " 1" < "1 "

        ValueMetaStringTest.assertSignum(1, meta.compare(" 1 ", null));// " 1 " > null

        ValueMetaStringTest.assertSignum(1, compare(" 1 ", ""));// " 1 " > null

        ValueMetaStringTest.assertSignum(1, compare(" 1 ", " "));// " 1 " > " "

        ValueMetaStringTest.assertSignum(1, compare(" 1 ", " 1"));// " 1 " > " 1"

        ValueMetaStringTest.assertSignum(0, compare(" 1 ", " 1 "));// " 1 " == " 1 "

        ValueMetaStringTest.assertSignum((-1), compare(" 1 ", "1"));// " 1 " < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" 1 ", "1 "));// " 1 " < "1 "

        ValueMetaStringTest.assertSignum(1, meta.compare("1", null));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare("1", ""));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare("1", " "));// "1" > " "

        ValueMetaStringTest.assertSignum(1, compare("1", " 1"));// "1" > " 1"

        ValueMetaStringTest.assertSignum(1, compare("1", " 1 "));// "1" > " 1 "

        ValueMetaStringTest.assertSignum(0, compare("1", "1"));// "1" == "1"

        ValueMetaStringTest.assertSignum((-1), compare("1", "1 "));// "1" < "1 "

        ValueMetaStringTest.assertSignum(1, meta.compare("1 ", null));// "1 " > null

        ValueMetaStringTest.assertSignum(1, compare("1 ", ""));// "1 " > null

        ValueMetaStringTest.assertSignum(1, compare("1 ", " "));// "1 " > " "

        ValueMetaStringTest.assertSignum(1, compare("1 ", " 1"));// "1 " > " 1"

        ValueMetaStringTest.assertSignum(1, compare("1 ", " 1 "));// "1 " > " 1 "

        ValueMetaStringTest.assertSignum(1, compare("1 ", "1"));// "1 " > "1"

        ValueMetaStringTest.assertSignum(0, compare("1 ", "1 "));// "1 " == "1 "

        meta.setTrimType(TRIM_TYPE_LEFT);
        ValueMetaStringTest.assertSignum(0, meta.compare(null, null));// null == null

        ValueMetaStringTest.assertSignum(0, meta.compare(null, ""));// null < null

        // assertSignum( 0, meta.compare( null, " " ) ); // null == null //TODO: Is it correct?
        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " "));// null < null //TODO: Is it correct?

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " 1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " 1 "));// null < "1 "

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, "1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, "1 "));// null < "1 "

        ValueMetaStringTest.assertSignum(0, meta.compare("", null));// null == null

        ValueMetaStringTest.assertSignum(0, compare("", ""));// null == null

        // assertSignum( 0, meta.compare( "", " " ) ); // null == null //TODO: Is it correct?
        ValueMetaStringTest.assertSignum((-1), compare("", " "));// null < null //TODO: Is it correct?

        ValueMetaStringTest.assertSignum((-1), compare("", " 1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), compare("", " 1 "));// null < "1 "

        ValueMetaStringTest.assertSignum((-1), compare("", "1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), compare("", "1 "));// null < "1 "

        ValueMetaStringTest.assertSignum(1, meta.compare(" ", null));// null > null

        // assertSignum( 0, meta.compare( " ", "" ) ); // null == null //TODO: Is it correct?
        ValueMetaStringTest.assertSignum(1, compare(" ", ""));// null > null //TODO: Is it correct?

        ValueMetaStringTest.assertSignum(0, compare(" ", " "));// null == null

        ValueMetaStringTest.assertSignum((-1), compare(" ", " 1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" ", " 1 "));// null < "1 "

        ValueMetaStringTest.assertSignum((-1), compare(" ", "1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" ", "1 "));// null < "1 "

        ValueMetaStringTest.assertSignum(1, meta.compare(" 1", null));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare(" 1", ""));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare(" 1", " "));// "1" > null

        ValueMetaStringTest.assertSignum(0, compare(" 1", " 1"));// "1" == "1"

        ValueMetaStringTest.assertSignum((-1), compare(" 1", " 1 "));// "1" < "1 "

        ValueMetaStringTest.assertSignum(0, compare(" 1", "1"));// "1" == "1"

        ValueMetaStringTest.assertSignum((-1), compare(" 1", "1 "));// "1" < "1 "

        ValueMetaStringTest.assertSignum(1, meta.compare(" 1 ", null));// "1 " > null

        ValueMetaStringTest.assertSignum(1, compare(" 1 ", ""));// "1 " > null

        ValueMetaStringTest.assertSignum(1, compare(" 1 ", " "));// "1 " > null

        ValueMetaStringTest.assertSignum(1, compare(" 1 ", " 1"));// "1 " > "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1 ", " 1 "));// "1 " == "1 "

        ValueMetaStringTest.assertSignum(1, compare(" 1 ", "1"));// "1 " > "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1 ", "1 "));// "1 " == "1 "

        ValueMetaStringTest.assertSignum(1, meta.compare("1", null));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare("1", ""));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare("1", " "));// "1" > null

        ValueMetaStringTest.assertSignum(0, compare("1", " 1"));// "1" == "1"

        ValueMetaStringTest.assertSignum((-1), compare("1", " 1 "));// "1" < "1 "

        ValueMetaStringTest.assertSignum(0, compare("1", "1"));// "1" == "1"

        ValueMetaStringTest.assertSignum((-1), compare("1", "1 "));// "1" < "1 "

        ValueMetaStringTest.assertSignum(1, meta.compare("1 ", null));// "1 " > null

        ValueMetaStringTest.assertSignum(1, compare("1 ", ""));// "1 " > null

        ValueMetaStringTest.assertSignum(1, compare("1 ", " "));// "1 " > null

        ValueMetaStringTest.assertSignum(1, compare("1 ", " 1"));// "1 " > "1"

        ValueMetaStringTest.assertSignum(0, compare("1 ", " 1 "));// "1 " == "1 "

        ValueMetaStringTest.assertSignum(1, compare("1 ", "1"));// "1 " > "1"

        ValueMetaStringTest.assertSignum(0, compare("1 ", "1 "));// "1 " == "1 "

        meta.setTrimType(TRIM_TYPE_RIGHT);
        ValueMetaStringTest.assertSignum(0, meta.compare(null, null));// null == null

        ValueMetaStringTest.assertSignum(0, meta.compare(null, ""));// null == null

        // assertSignum( 0, meta.compare( null, " " ) ); // null == null //TODO: Is it correct?
        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " "));// null < null //TODO: Is it correct?

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " 1"));// null < " 1"

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " 1 "));// null < " 1"

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, "1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, "1 "));// null < "1"

        ValueMetaStringTest.assertSignum(0, meta.compare("", null));// null == null

        ValueMetaStringTest.assertSignum(0, compare("", ""));// null == null

        // assertSignum( 0, meta.compare( "", " " ) ); // null == null //TODO: Is it correct?
        ValueMetaStringTest.assertSignum((-1), compare("", " "));// null < null //TODO: Is it correct?

        ValueMetaStringTest.assertSignum((-1), compare("", " 1"));// null < " 1"

        ValueMetaStringTest.assertSignum((-1), compare("", " 1 "));// null < " 1"

        ValueMetaStringTest.assertSignum((-1), compare("", "1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), compare("", "1 "));// null < "1"

        // assertSignum( 0, meta.compare( " ", null ) ); // null == null //TODO: Is it correct?
        ValueMetaStringTest.assertSignum(1, meta.compare(" ", null));// null > null //TODO: Is it correct?

        // assertSignum( 0, meta.compare( " ", "" ) ); // null == null //TODO: Is it correct?
        ValueMetaStringTest.assertSignum(1, compare(" ", ""));// null > null //TODO: Is it correct?

        ValueMetaStringTest.assertSignum(0, compare(" ", " "));// null == null

        ValueMetaStringTest.assertSignum((-1), compare(" ", " 1"));// null < " 1"

        ValueMetaStringTest.assertSignum((-1), compare(" ", " 1 "));// null < " 1"

        ValueMetaStringTest.assertSignum((-1), compare(" ", "1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" ", "1 "));// null < "1"

        ValueMetaStringTest.assertSignum(1, meta.compare(" 1", null));// " 1" > null

        ValueMetaStringTest.assertSignum(1, compare(" 1", ""));// " 1" > null

        ValueMetaStringTest.assertSignum(1, compare(" 1", " "));// " 1" > null

        ValueMetaStringTest.assertSignum(0, compare(" 1", " 1"));// " 1" == " 1"

        ValueMetaStringTest.assertSignum(0, compare(" 1", " 1 "));// " 1" == " 1"

        ValueMetaStringTest.assertSignum((-1), compare(" 1", "1"));// " 1" < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" 1", "1 "));// " 1" < "1"

        ValueMetaStringTest.assertSignum(1, meta.compare(" 1 ", null));// " 1" > null

        ValueMetaStringTest.assertSignum(1, compare(" 1 ", ""));// " 1" > null

        ValueMetaStringTest.assertSignum(1, compare(" 1 ", " "));// " 1" > null

        ValueMetaStringTest.assertSignum(0, compare(" 1 ", " 1"));// " 1" == " 1"

        ValueMetaStringTest.assertSignum(0, compare(" 1 ", " 1 "));// " 1" == " 1"

        ValueMetaStringTest.assertSignum((-1), compare(" 1 ", "1"));// " 1" < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" 1 ", "1 "));// " 1" < "1"

        ValueMetaStringTest.assertSignum(1, meta.compare("1", null));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare("1", ""));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare("1", " "));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare("1", " 1"));// "1" > " 1"

        ValueMetaStringTest.assertSignum(1, compare("1", " 1 "));// "1" > " 1"

        ValueMetaStringTest.assertSignum(0, compare("1", "1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1", "1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(1, meta.compare("1 ", null));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare("1 ", ""));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare("1 ", " "));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare("1 ", " 1"));// "1" > " 1"

        ValueMetaStringTest.assertSignum(1, compare("1 ", " 1 "));// "1" > " 1"

        ValueMetaStringTest.assertSignum(0, compare("1 ", "1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1 ", "1 "));// "1" == "1"

        meta.setTrimType(TRIM_TYPE_BOTH);
        ValueMetaStringTest.assertSignum(0, meta.compare(null, null));// null == null

        ValueMetaStringTest.assertSignum(0, meta.compare(null, ""));// null == null

        // assertSignum( 0, meta.compare( null, " " ) ); // null == null //TODO: Is it correct?
        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " "));// null < null //TODO: Is it correct?

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " 1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " 1 "));// null < "1"

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, "1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, "1 "));// null < "1"

        ValueMetaStringTest.assertSignum(0, meta.compare("", null));// null == null

        ValueMetaStringTest.assertSignum(0, compare("", ""));// null == null

        // assertSignum( 0, meta.compare( "", " " ) ); // null == null //TODO: Is it correct?
        ValueMetaStringTest.assertSignum((-1), compare("", " "));// null < null //TODO: Is it correct?

        ValueMetaStringTest.assertSignum((-1), compare("", " 1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), compare("", " 1 "));// null < "1"

        ValueMetaStringTest.assertSignum((-1), compare("", "1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), compare("", "1 "));// null < "1"

        // assertSignum( 0, meta.compare( " ", null ) ); // null == null //TODO: Is it correct?
        ValueMetaStringTest.assertSignum(1, meta.compare(" ", null));// null > null //TODO: Is it correct?

        // assertSignum( 0, meta.compare( " ", "" ) ); // null == null //TODO: Is it correct?
        ValueMetaStringTest.assertSignum(1, compare(" ", ""));// null > null //TODO: Is it correct?

        ValueMetaStringTest.assertSignum(0, compare(" ", " "));// null == null

        ValueMetaStringTest.assertSignum((-1), compare(" ", " 1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" ", " 1 "));// null < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" ", "1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" ", "1 "));// null < "1"

        ValueMetaStringTest.assertSignum(1, meta.compare(" 1", null));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare(" 1", ""));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare(" 1", " "));// "1" > null

        ValueMetaStringTest.assertSignum(0, compare(" 1", " 1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1", " 1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1", "1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1", "1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(1, meta.compare(" 1 ", null));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare(" 1 ", ""));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare(" 1 ", " "));// "1" > null

        ValueMetaStringTest.assertSignum(0, compare(" 1 ", " 1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1 ", " 1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1 ", "1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1 ", "1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(1, meta.compare("1", null));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare("1", ""));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare("1", " "));// "1" > null

        ValueMetaStringTest.assertSignum(0, compare("1", " 1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1", " 1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1", "1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1", "1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(1, meta.compare("1 ", null));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare("1 ", ""));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare("1 ", " "));// "1" > null

        ValueMetaStringTest.assertSignum(0, compare("1 ", " 1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1 ", " 1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1 ", "1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1 ", "1 "));// "1" == "1"

        meta.setTrimType(TRIM_TYPE_NONE);
        setIgnoreWhitespace(true);
        ValueMetaStringTest.assertSignum(0, meta.compare(null, null));// null == null

        ValueMetaStringTest.assertSignum(0, meta.compare(null, ""));// null == null

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " "));// null < null //TODO: Is it correct?

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " 1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, " 1 "));// null < "1"

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, "1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), meta.compare(null, "1 "));// null < "1"

        ValueMetaStringTest.assertSignum(0, meta.compare("", null));// null == null

        ValueMetaStringTest.assertSignum(0, compare("", ""));// null == null

        ValueMetaStringTest.assertSignum((-1), compare("", " "));// null < null //TODO: Is it correct?

        ValueMetaStringTest.assertSignum((-1), compare("", " 1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), compare("", " 1 "));// null < "1"

        ValueMetaStringTest.assertSignum((-1), compare("", "1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), compare("", "1 "));// null < "1"

        ValueMetaStringTest.assertSignum(1, meta.compare(" ", null));// null > null //TODO: Is it correct?

        ValueMetaStringTest.assertSignum(1, compare(" ", ""));// null > null //TODO: Is it correct?

        ValueMetaStringTest.assertSignum(0, compare(" ", " "));// null == null

        ValueMetaStringTest.assertSignum((-1), compare(" ", " 1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" ", " 1 "));// null < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" ", "1"));// null < "1"

        ValueMetaStringTest.assertSignum((-1), compare(" ", "1 "));// null < "1"

        ValueMetaStringTest.assertSignum(1, meta.compare(" 1", null));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare(" 1", ""));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare(" 1", " "));// "1" > null

        ValueMetaStringTest.assertSignum(0, compare(" 1", " 1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1", " 1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1", "1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1", "1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(1, meta.compare(" 1 ", null));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare(" 1 ", ""));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare(" 1 ", " "));// "1" > null

        ValueMetaStringTest.assertSignum(0, compare(" 1 ", " 1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1 ", " 1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1 ", "1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare(" 1 ", "1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(1, meta.compare("1", null));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare("1", ""));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare("1", " "));// "1" > null

        ValueMetaStringTest.assertSignum(0, compare("1", " 1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1", " 1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1", "1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1", "1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(1, meta.compare("1 ", null));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare("1 ", ""));// "1" > null

        ValueMetaStringTest.assertSignum(1, compare("1 ", " "));// "1" > null

        ValueMetaStringTest.assertSignum(0, compare("1 ", " 1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1 ", " 1 "));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1 ", "1"));// "1" == "1"

        ValueMetaStringTest.assertSignum(0, compare("1 ", "1 "));// "1" == "1"

    }

    @Test
    public void testCompare_collatorEnabled() throws KettleValueException {
        ValueMetaString meta = new ValueMetaString(ValueMetaStringTest.BASE_VALUE);
        meta.setCollatorDisabled(false);
        meta.setCollatorLocale(Locale.FRENCH);
        meta.setCollatorStrength(3);
        ValueMetaStringTest.assertSignum((-1), meta.compare("E", "F"));
        ValueMetaStringTest.assertSignum((-1), meta.compare("e", "\u00e9"));
        ValueMetaStringTest.assertSignum((-1), meta.compare("e", "E"));
        ValueMetaStringTest.assertSignum((-1), meta.compare("\u0001", "\u0002"));
        ValueMetaStringTest.assertSignum(0, meta.compare("e", "e"));
        meta.setCollatorStrength(2);
        ValueMetaStringTest.assertSignum((-1), meta.compare("E", "F"));
        ValueMetaStringTest.assertSignum((-1), meta.compare("e", "\u00e9"));
        ValueMetaStringTest.assertSignum((-1), meta.compare("e", "E"));
        ValueMetaStringTest.assertSignum(0, meta.compare("\u0001", "\u0002"));
        ValueMetaStringTest.assertSignum(0, meta.compare("e", "e"));
        meta.setCollatorStrength(1);
        ValueMetaStringTest.assertSignum((-1), meta.compare("E", "F"));
        ValueMetaStringTest.assertSignum((-1), meta.compare("e", "\u00e9"));
        ValueMetaStringTest.assertSignum(0, meta.compare("e", "E"));
        ValueMetaStringTest.assertSignum(0, meta.compare("\u0001", "\u0002"));
        ValueMetaStringTest.assertSignum(0, meta.compare("e", "e"));
        meta.setCollatorStrength(0);
        ValueMetaStringTest.assertSignum((-1), meta.compare("E", "F"));
        ValueMetaStringTest.assertSignum(0, meta.compare("e", "\u00e9"));
        ValueMetaStringTest.assertSignum(0, meta.compare("e", "E"));
        ValueMetaStringTest.assertSignum(0, meta.compare("\u0001", "\u0002"));
        ValueMetaStringTest.assertSignum(0, meta.compare("e", "e"));
    }

    @Test
    public void testGetIntegerWithoutConversionMask() throws ParseException, KettleValueException {
        String value = "100.56";
        ValueMetaInterface stringValueMeta = new ValueMetaString("test");
        Long expected = 100L;
        Long result = stringValueMeta.getInteger(value);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testGetNumberWithoutConversionMask() throws ParseException, KettleValueException {
        String value = "100.56";
        ValueMetaInterface stringValueMeta = new ValueMetaString("test");
        Double expected = 100.56;
        Double result = stringValueMeta.getNumber(value);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testGetBigNumberWithoutConversionMask() throws ParseException, KettleValueException {
        String value = "100.5";
        ValueMetaInterface stringValueMeta = new ValueMetaString("test");
        BigDecimal expected = new BigDecimal(100.5);
        BigDecimal result = stringValueMeta.getBigNumber(value);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testGetDateWithoutConversionMask() throws ParseException, KettleValueException {
        Calendar date = new GregorianCalendar(2017, 9, 20);// month 9 = Oct

        String value = "2017/10/20 00:00:00.000";
        ValueMetaInterface stringValueMeta = new ValueMetaString("test");
        Date expected = Date.from(date.toInstant());
        Date result = stringValueMeta.getDate(value);
        Assert.assertEquals(expected, result);
    }

    @SuppressWarnings("deprecation")
    private static class ConfigurableMeta extends ValueMetaString {
        private boolean nullsAndEmptyAreDifferent;

        public ConfigurableMeta(String name) {
            super(name);
        }

        public void setNullsAndEmptyAreDifferent(boolean nullsAndEmptyAreDifferent) {
            this.nullsAndEmptyAreDifferent = nullsAndEmptyAreDifferent;
        }

        @Override
        public boolean isNull(Object data) throws KettleValueException {
            return super.isNull(data, nullsAndEmptyAreDifferent);
        }

        @Override
        protected String convertBinaryStringToString(byte[] binary) throws KettleValueException {
            return super.convertBinaryStringToString(binary, nullsAndEmptyAreDifferent);
        }
    }
}

