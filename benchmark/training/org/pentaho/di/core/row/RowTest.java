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


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;


public class RowTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    @Test
    public void testNormalStringConversion() throws Exception {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Object[] rowData1 = new Object[]{ "sampleString", fmt.parse("2007/05/07 13:04:13.203"), new Double(9123.0), new Long(12345), new BigDecimal("123456789012345678.9349"), Boolean.TRUE };
        RowMetaInterface rowMeta1 = createTestRowMetaNormalStringConversion1();
        Assert.assertEquals("sampleString", rowMeta1.getString(rowData1, 0));
        Assert.assertEquals("2007/05/07 13:04:13.203", rowMeta1.getString(rowData1, 1));
        Assert.assertEquals("9,123.00", rowMeta1.getString(rowData1, 2));
        Assert.assertEquals("0012345", rowMeta1.getString(rowData1, 3));
        Assert.assertEquals("123456789012345678.9349", rowMeta1.getString(rowData1, 4));
        Assert.assertEquals("Y", rowMeta1.getString(rowData1, 5));
        fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        Object[] rowData2 = new Object[]{ null, fmt.parse("20070507130413"), new Double(9123.9), new Long(12345), new BigDecimal("123456789012345678.9349"), Boolean.FALSE };
        RowMetaInterface rowMeta2 = createTestRowMetaNormalStringConversion2();
        Assert.assertTrue(((rowMeta2.getString(rowData2, 0)) == null));
        Assert.assertEquals("20070507130413", rowMeta2.getString(rowData2, 1));
        Assert.assertEquals("9.123,9", rowMeta2.getString(rowData2, 2));
        Assert.assertEquals("0012345", rowMeta2.getString(rowData2, 3));
        Assert.assertEquals("123456789012345678.9349", rowMeta2.getString(rowData2, 4));
        Assert.assertEquals("false", rowMeta2.getString(rowData2, 5));
    }

    @Test
    public void testIndexedStringConversion() throws Exception {
        String[] colors = new String[]{ "Green", "Red", "Blue", "Yellow", null };
        // create some timezone friendly dates
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date[] dates = new Date[]{ fmt.parse("2007/05/07 13:04:13.203"), null, fmt.parse("2007/05/05 05:15:49.349"), fmt.parse("2007/05/05 19:08:44.736") };
        RowMetaInterface rowMeta = createTestRowMetaIndexedStringConversion1(colors, dates);
        Object[] rowData1 = new Object[]{ Integer.valueOf(0), Integer.valueOf(0) };
        Object[] rowData2 = new Object[]{ Integer.valueOf(1), Integer.valueOf(1) };
        Object[] rowData3 = new Object[]{ Integer.valueOf(2), Integer.valueOf(2) };
        Object[] rowData4 = new Object[]{ Integer.valueOf(3), Integer.valueOf(3) };
        Object[] rowData5 = new Object[]{ Integer.valueOf(4), Integer.valueOf(0) };
        Assert.assertEquals("Green", rowMeta.getString(rowData1, 0));
        Assert.assertEquals("2007/05/07 13:04:13.203", rowMeta.getString(rowData1, 1));
        Assert.assertEquals("Red", rowMeta.getString(rowData2, 0));
        Assert.assertTrue((null == (rowMeta.getString(rowData2, 1))));
        Assert.assertEquals("Blue", rowMeta.getString(rowData3, 0));
        Assert.assertEquals("2007/05/05 05:15:49.349", rowMeta.getString(rowData3, 1));
        Assert.assertEquals("Yellow", rowMeta.getString(rowData4, 0));
        Assert.assertEquals("2007/05/05 19:08:44.736", rowMeta.getString(rowData4, 1));
        Assert.assertTrue((null == (rowMeta.getString(rowData5, 0))));
        Assert.assertEquals("2007/05/07 13:04:13.203", rowMeta.getString(rowData5, 1));
    }

    @Test
    public void testExtractDataWithTimestampConversion() throws Exception {
        RowMetaInterface rowMeta = createTestRowMetaNormalTimestampConversion();
        Timestamp constTimestamp = Timestamp.valueOf("2012-04-05 04:03:02.123456");
        Timestamp constTimestampForDate = Timestamp.valueOf("2012-04-05 04:03:02.123");
        makeTestExtractDataWithTimestampConversion(rowMeta, " Test1", constTimestamp, constTimestamp);
        makeTestExtractDataWithTimestampConversion(rowMeta, " Test2", new Date(constTimestamp.getTime()), constTimestampForDate);
        makeTestExtractDataWithTimestampConversion(rowMeta, " Test3", new java.sql.Date(constTimestamp.getTime()), constTimestampForDate);
    }
}

