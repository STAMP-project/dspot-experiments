/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2017-2018 by Hitachi Vantara : http://www.pentaho.com
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


import ValueMetaInterface.TYPE_NONE;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;


/**
 * Created by tkafalas on 12/6/2017.
 */
public class ValueMetaConverterTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    private static final int startSource = 1;

    private static final int endSource = 10;

    private static final int startTarget = 1;

    private static final int endTarget = 10;

    private static final boolean IS_VERBOSE = false;// Change to true to display information


    @Test
    public void convertFromSourceToTargetDataTypeTest() throws Exception {
        // "-", "Number", "String", "Date", "Boolean", "Integer", "BigNumber", "Serializable", "Binary", "Timestamp",
        // "Internet Address", }
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date date1 = dateFormat.parse("1999/12/31 00:00:00.000");
        Date timeStamp1 = new Timestamp(dateFormat.parse("2001/11/01 20:30:15.123").getTime());
        final String inetHost = "127.0.0.1";
        InetAddress inetAddress1 = InetAddress.getByName(inetHost);
        // All combination not listed here should generate ValueMetaConversionExceptions
        // source type, destination type, source object, expected result
        Object[][] tests = new Object[][]{ new Object[]{ ValueMetaInterface.TYPE_NUMBER, ValueMetaInterface.TYPE_NONE, 1234.56, null }, new Object[]{ ValueMetaInterface.TYPE_NUMBER, ValueMetaInterface.TYPE_STRING, 1234.56, "1234.56" }, new Object[]{ ValueMetaInterface.TYPE_NUMBER, ValueMetaInterface.TYPE_NUMBER, 1234.56, 1234.56 }, new Object[]{ ValueMetaInterface.TYPE_NUMBER, ValueMetaInterface.TYPE_INTEGER, 1234.56, 1234L }, new Object[]{ ValueMetaInterface.TYPE_NUMBER, ValueMetaInterface.TYPE_BIGNUMBER, 1234.56, new BigDecimal(1234.56) }, new Object[]{ ValueMetaInterface.TYPE_STRING, ValueMetaInterface.TYPE_NONE, inetHost, null }, new Object[]{ ValueMetaInterface.TYPE_STRING, ValueMetaInterface.TYPE_STRING, "foobar", "foobar" }, new Object[]{ ValueMetaInterface.TYPE_STRING, ValueMetaInterface.TYPE_INET, inetHost, inetAddress1 }, new Object[]{ ValueMetaInterface.TYPE_STRING, ValueMetaInterface.TYPE_INTEGER, "1234", 1234L }, new Object[]{ ValueMetaInterface.TYPE_STRING, ValueMetaInterface.TYPE_NUMBER, "1234.56", 1234.56 }, new Object[]{ ValueMetaInterface.TYPE_STRING, ValueMetaInterface.TYPE_BIGNUMBER, "123456789.123456789", new BigDecimal("123456789.123456789") }, new Object[]{ ValueMetaInterface.TYPE_STRING, ValueMetaInterface.TYPE_TIMESTAMP, "2001/11/01 20:30:15.123", timeStamp1 }, new Object[]{ ValueMetaInterface.TYPE_STRING, ValueMetaInterface.TYPE_DATE, "1999/12/31 00:00:00.000", date1 }, new Object[]{ ValueMetaInterface.TYPE_STRING, ValueMetaInterface.TYPE_BOOLEAN, "true", true }, new Object[]{ ValueMetaInterface.TYPE_STRING, ValueMetaInterface.TYPE_BINARY, "foobar", "foobar".getBytes() }, new Object[]{ ValueMetaInterface.TYPE_DATE, ValueMetaInterface.TYPE_NONE, date1, null }, new Object[]{ ValueMetaInterface.TYPE_DATE, ValueMetaInterface.TYPE_DATE, date1, date1 }, new Object[]{ ValueMetaInterface.TYPE_DATE, ValueMetaInterface.TYPE_INTEGER, date1, date1.getTime() }, new Object[]{ ValueMetaInterface.TYPE_DATE, ValueMetaInterface.TYPE_STRING, date1, "1999/12/31 00:00:00.000" }, new Object[]{ ValueMetaInterface.TYPE_DATE, ValueMetaInterface.TYPE_TIMESTAMP, date1, new Timestamp(date1.getTime()) }, new Object[]{ ValueMetaInterface.TYPE_BOOLEAN, ValueMetaInterface.TYPE_NONE, true, null }, new Object[]{ ValueMetaInterface.TYPE_BOOLEAN, ValueMetaInterface.TYPE_STRING, true, "true" }, new Object[]{ ValueMetaInterface.TYPE_BOOLEAN, ValueMetaInterface.TYPE_BOOLEAN, true, true }, new Object[]{ ValueMetaInterface.TYPE_INTEGER, ValueMetaInterface.TYPE_NONE, 1234L, null }, new Object[]{ ValueMetaInterface.TYPE_INTEGER, ValueMetaInterface.TYPE_DATE, date1.getTime(), date1 }, new Object[]{ ValueMetaInterface.TYPE_INTEGER, ValueMetaInterface.TYPE_STRING, 1234L, "1234" }, new Object[]{ ValueMetaInterface.TYPE_INTEGER, ValueMetaInterface.TYPE_INTEGER, 1234L, 1234L }, new Object[]{ ValueMetaInterface.TYPE_INTEGER, ValueMetaInterface.TYPE_NUMBER, 1234L, 1234.0 }, new Object[]{ ValueMetaInterface.TYPE_INTEGER, ValueMetaInterface.TYPE_BIGNUMBER, 1234L, new BigDecimal("1234") }, new Object[]{ ValueMetaInterface.TYPE_INTEGER, ValueMetaInterface.TYPE_TIMESTAMP, timeStamp1.getTime(), timeStamp1 }, new Object[]{ ValueMetaInterface.TYPE_BIGNUMBER, ValueMetaInterface.TYPE_NONE, new BigDecimal("123456.123456"), null }, new Object[]{ ValueMetaInterface.TYPE_BIGNUMBER, ValueMetaInterface.TYPE_STRING, new BigDecimal("123456.123456"), "123456.123456" }, new Object[]{ ValueMetaInterface.TYPE_BIGNUMBER, ValueMetaInterface.TYPE_NUMBER, new BigDecimal("123456.123456"), 123456.123456 }, new Object[]{ ValueMetaInterface.TYPE_BIGNUMBER, ValueMetaInterface.TYPE_BIGNUMBER, new BigDecimal("123456.123456"), new BigDecimal("123456.123456") }, new Object[]{ ValueMetaInterface.TYPE_SERIALIZABLE, ValueMetaInterface.TYPE_NONE, "foobar", null }, new Object[]{ ValueMetaInterface.TYPE_SERIALIZABLE, ValueMetaInterface.TYPE_SERIALIZABLE, "foobar", "foobar" }, new Object[]{ ValueMetaInterface.TYPE_BINARY, ValueMetaInterface.TYPE_NONE, "foobar".getBytes(), null }, new Object[]{ ValueMetaInterface.TYPE_BINARY, ValueMetaInterface.TYPE_BINARY, "foobar".getBytes(), "foobar".getBytes() }, new Object[]{ ValueMetaInterface.TYPE_TIMESTAMP, ValueMetaInterface.TYPE_NONE, timeStamp1, null }, new Object[]{ ValueMetaInterface.TYPE_TIMESTAMP, ValueMetaInterface.TYPE_STRING, timeStamp1, "2001/11/01 20:30:15.123" }, new Object[]{ ValueMetaInterface.TYPE_TIMESTAMP, ValueMetaInterface.TYPE_INTEGER, timeStamp1, timeStamp1.getTime() }, new Object[]{ ValueMetaInterface.TYPE_TIMESTAMP, ValueMetaInterface.TYPE_TIMESTAMP, timeStamp1, timeStamp1 }, new Object[]{ ValueMetaInterface.TYPE_TIMESTAMP, ValueMetaInterface.TYPE_DATE, timeStamp1, new Date(timeStamp1.getTime()) }, new Object[]{ ValueMetaInterface.TYPE_INET, ValueMetaInterface.TYPE_NONE, inetAddress1, null }, new Object[]{ ValueMetaInterface.TYPE_INET, ValueMetaInterface.TYPE_STRING, inetAddress1, inetAddress1.getHostAddress() }, new Object[]{ ValueMetaInterface.TYPE_INET, ValueMetaInterface.TYPE_INET, inetAddress1, inetAddress1 } };
        // Get the tests in a map so that they can be quickly referenced while testing all permutations
        Map<String, Object[]> testMap = new HashMap<>();
        for (Object[] testSpec : tests) {
            testMap.put(getKey(((Integer) (testSpec[0])), ((Integer) (testSpec[1]))), testSpec);
        }
        ValueMetaConverter converter = new ValueMetaConverter();
        for (int sourceType = ValueMetaConverterTest.startSource; sourceType <= (ValueMetaConverterTest.endSource); sourceType++) {
            for (int targetType = ValueMetaConverterTest.startTarget; targetType <= (ValueMetaConverterTest.endTarget); targetType++) {
                Object[] testSpec = testMap.get(getKey(sourceType, targetType));
                if (testSpec != null) {
                    Object targetValue = converter.convertFromSourceToTargetDataType(sourceType, targetType, testSpec[2]);
                    if (ValueMetaConverterTest.IS_VERBOSE) {
                        System.out.println(((((((("type " + sourceType) + "/") + targetType) + ":") + (testSpec[3].toString())) + "=") + (targetValue.toString())));
                    }
                    if (targetType == (ValueMetaInterface.TYPE_BINARY)) {
                        Arrays.equals(((byte[]) (testSpec[3])), ((byte[]) (targetValue)));
                    } else {
                        Assert.assertEquals(testSpec[3], targetValue);
                    }
                } else {
                    // Attempt a non-defined conversion.  Should throw an exception.
                    try {
                        // Get a source object of the correct type
                        testSpec = testMap.get(getKey(sourceType, TYPE_NONE));
                        if (ValueMetaConverterTest.IS_VERBOSE) {
                            System.out.println((((((("type " + sourceType) + "/") + targetType) + ":") + (testSpec[2].toString())) + " should throw Exception"));
                        }
                        converter.convertFromSourceToTargetDataType(sourceType, targetType, testSpec[2]);
                        Assert.fail("Did not throw exception.  Probably need to make a test entry for this combination.");
                    } catch (ValueMetaConversionException e) {
                        // We are expecting this exception.  Any combination we are not testing should not be supported
                        if (!(e.getMessage().contains("Error.  Can not convert from"))) {
                            Assert.fail("Got a diferent exception than what was expected");
                        }
                    }
                }
                // Now Try and send a null, should always return null
                Assert.assertNull(converter.convertFromSourceToTargetDataType(sourceType, targetType, null));
                // Now Try to send in a source that is an invalid type - should always fail
                try {
                    converter.convertFromSourceToTargetDataType(sourceType, targetType, new Object());
                } catch (ValueMetaConversionException e) {
                    // We are expecting this exception.  Any combination we are not testing should not be supported
                    if (!(e.getMessage().contains("Error.  Expecting value of type"))) {
                        Assert.fail("Got a diferent exception than what was expected");
                    }
                }
            }
        }
    }
}

