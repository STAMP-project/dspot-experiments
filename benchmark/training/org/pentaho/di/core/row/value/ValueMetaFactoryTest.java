/**
 * ******************************************************************************
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


import ValueMetaInterface.TYPE_BIGNUMBER;
import ValueMetaInterface.TYPE_BINARY;
import ValueMetaInterface.TYPE_BOOLEAN;
import ValueMetaInterface.TYPE_DATE;
import ValueMetaInterface.TYPE_INET;
import ValueMetaInterface.TYPE_INTEGER;
import ValueMetaInterface.TYPE_NONE;
import ValueMetaInterface.TYPE_NUMBER;
import ValueMetaInterface.TYPE_SERIALIZABLE;
import ValueMetaInterface.TYPE_STRING;
import ValueMetaInterface.TYPE_TIMESTAMP;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;


public class ValueMetaFactoryTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    @Test
    public void testClone() throws KettleException {
        ValueMetaInterface original = new ValueMetaString();
        original.setCollatorLocale(Locale.CANADA);
        original.setCollatorStrength(3);
        ValueMetaInterface cloned = ValueMetaFactory.cloneValueMeta(original);
        Assert.assertNotNull(cloned);
        Assert.assertNotSame(original, cloned);
        ValueMetaFactoryTest.valueMetaDeepEquals(original, cloned);
    }

    @Test
    public void testCreateValueMeta() throws KettlePluginException {
        ValueMetaInterface testObject;
        try {
            testObject = ValueMetaFactory.createValueMeta(Integer.MIN_VALUE);
            Assert.fail();
        } catch (KettlePluginException expected) {
            // Do nothing, Integer.MIN_VALUE is not a valid option
        }
        try {
            testObject = ValueMetaFactory.createValueMeta(null, Integer.MIN_VALUE);
            Assert.fail();
        } catch (KettlePluginException expected) {
            // Do nothing, Integer.MIN_VALUE is not a valid option
        }
        try {
            testObject = ValueMetaFactory.createValueMeta(null, Integer.MIN_VALUE, 10, 10);
            Assert.fail();
        } catch (KettlePluginException expected) {
            // Do nothing, Integer.MIN_VALUE is not a valid option
        }
        testObject = ValueMetaFactory.createValueMeta(TYPE_NONE);
        Assert.assertTrue((testObject instanceof ValueMetaNone));
        Assert.assertEquals(null, testObject.getName());
        Assert.assertEquals((-1), testObject.getLength());
        Assert.assertEquals((-1), testObject.getPrecision());
        testObject = ValueMetaFactory.createValueMeta("testNone", TYPE_NONE);
        Assert.assertTrue((testObject instanceof ValueMetaNone));
        Assert.assertEquals("testNone", testObject.getName());
        Assert.assertEquals((-1), testObject.getLength());
        Assert.assertEquals((-1), testObject.getPrecision());
        testObject = ValueMetaFactory.createValueMeta("testNone", TYPE_NONE, 10, 20);
        Assert.assertTrue((testObject instanceof ValueMetaNone));
        Assert.assertEquals("testNone", testObject.getName());
        Assert.assertEquals(10, testObject.getLength());
        Assert.assertEquals(20, testObject.getPrecision());
        testObject = ValueMetaFactory.createValueMeta(TYPE_NUMBER);
        Assert.assertTrue((testObject instanceof ValueMetaNumber));
        Assert.assertEquals(null, testObject.getName());
        Assert.assertEquals((-1), testObject.getLength());
        Assert.assertEquals((-1), testObject.getPrecision());
        testObject = ValueMetaFactory.createValueMeta("testNumber", TYPE_NUMBER);
        Assert.assertTrue((testObject instanceof ValueMetaNumber));
        Assert.assertEquals("testNumber", testObject.getName());
        Assert.assertEquals((-1), testObject.getLength());
        Assert.assertEquals((-1), testObject.getPrecision());
        testObject = ValueMetaFactory.createValueMeta("testNumber", TYPE_NUMBER, 10, 20);
        Assert.assertTrue((testObject instanceof ValueMetaNumber));
        Assert.assertEquals("testNumber", testObject.getName());
        Assert.assertEquals(10, testObject.getLength());
        Assert.assertEquals(20, testObject.getPrecision());
        testObject = ValueMetaFactory.createValueMeta(TYPE_STRING);
        Assert.assertTrue((testObject instanceof ValueMetaString));
        Assert.assertEquals(null, testObject.getName());
        Assert.assertEquals((-1), testObject.getLength());
        Assert.assertEquals((-1), testObject.getPrecision());
        testObject = ValueMetaFactory.createValueMeta("testString", TYPE_STRING);
        Assert.assertTrue((testObject instanceof ValueMetaString));
        Assert.assertEquals("testString", testObject.getName());
        Assert.assertEquals((-1), testObject.getLength());
        Assert.assertEquals((-1), testObject.getPrecision());
        testObject = ValueMetaFactory.createValueMeta("testString", TYPE_STRING, 1000, 50);
        Assert.assertTrue((testObject instanceof ValueMetaString));
        Assert.assertEquals("testString", testObject.getName());
        Assert.assertEquals(1000, testObject.getLength());
        Assert.assertEquals((-1), testObject.getPrecision());// Special case for String

        testObject = ValueMetaFactory.createValueMeta(TYPE_DATE);
        Assert.assertTrue((testObject instanceof ValueMetaDate));
        Assert.assertEquals(null, testObject.getName());
        Assert.assertEquals((-1), testObject.getLength());
        Assert.assertEquals((-1), testObject.getPrecision());
        testObject = ValueMetaFactory.createValueMeta("testDate", TYPE_DATE);
        Assert.assertTrue((testObject instanceof ValueMetaDate));
        Assert.assertEquals("testDate", testObject.getName());
        Assert.assertEquals((-1), testObject.getLength());
        Assert.assertEquals((-1), testObject.getPrecision());
        testObject = ValueMetaFactory.createValueMeta("testDate", TYPE_DATE, 10, 20);
        Assert.assertTrue((testObject instanceof ValueMetaDate));
        Assert.assertEquals("testDate", testObject.getName());
        Assert.assertEquals(10, testObject.getLength());
        Assert.assertEquals(20, testObject.getPrecision());
        testObject = ValueMetaFactory.createValueMeta(TYPE_BOOLEAN);
        Assert.assertTrue((testObject instanceof ValueMetaBoolean));
        Assert.assertEquals(null, testObject.getName());
        Assert.assertEquals((-1), testObject.getLength());
        Assert.assertEquals((-1), testObject.getPrecision());
        testObject = ValueMetaFactory.createValueMeta("testBoolean", TYPE_BOOLEAN);
        Assert.assertTrue((testObject instanceof ValueMetaBoolean));
        Assert.assertEquals("testBoolean", testObject.getName());
        Assert.assertEquals((-1), testObject.getLength());
        Assert.assertEquals((-1), testObject.getPrecision());
        testObject = ValueMetaFactory.createValueMeta("testBoolean", TYPE_BOOLEAN, 10, 20);
        Assert.assertTrue((testObject instanceof ValueMetaBoolean));
        Assert.assertEquals("testBoolean", testObject.getName());
        Assert.assertEquals(10, testObject.getLength());
        Assert.assertEquals((-1), testObject.getPrecision());
        testObject = ValueMetaFactory.createValueMeta(TYPE_INTEGER);
        Assert.assertTrue((testObject instanceof ValueMetaInteger));
        Assert.assertEquals(null, testObject.getName());
        Assert.assertEquals((-1), testObject.getLength());
        Assert.assertEquals(0, testObject.getPrecision());// Special case for Integer

        testObject = ValueMetaFactory.createValueMeta("testInteger", TYPE_INTEGER);
        Assert.assertTrue((testObject instanceof ValueMetaInteger));
        Assert.assertEquals("testInteger", testObject.getName());
        Assert.assertEquals((-1), testObject.getLength());
        Assert.assertEquals(0, testObject.getPrecision());// Special case for Integer

        testObject = ValueMetaFactory.createValueMeta("testInteger", TYPE_INTEGER, 10, 20);
        Assert.assertTrue((testObject instanceof ValueMetaInteger));
        Assert.assertEquals("testInteger", testObject.getName());
        Assert.assertEquals(10, testObject.getLength());
        Assert.assertEquals(0, testObject.getPrecision());// Special case for Integer

        testObject = ValueMetaFactory.createValueMeta(TYPE_BIGNUMBER);
        Assert.assertTrue((testObject instanceof ValueMetaBigNumber));
        Assert.assertEquals(null, testObject.getName());
        Assert.assertEquals((-1), testObject.getLength());
        Assert.assertEquals((-1), testObject.getPrecision());
        testObject = ValueMetaFactory.createValueMeta("testBigNumber", TYPE_BIGNUMBER);
        Assert.assertTrue((testObject instanceof ValueMetaBigNumber));
        Assert.assertEquals("testBigNumber", testObject.getName());
        Assert.assertEquals((-1), testObject.getLength());
        Assert.assertEquals((-1), testObject.getPrecision());
        testObject = ValueMetaFactory.createValueMeta("testBigNumber", TYPE_BIGNUMBER, 10, 20);
        Assert.assertTrue((testObject instanceof ValueMetaBigNumber));
        Assert.assertEquals("testBigNumber", testObject.getName());
        Assert.assertEquals(10, testObject.getLength());
        Assert.assertEquals(20, testObject.getPrecision());
        testObject = ValueMetaFactory.createValueMeta(TYPE_SERIALIZABLE);
        Assert.assertTrue((testObject instanceof ValueMetaSerializable));
        Assert.assertEquals(null, testObject.getName());
        Assert.assertEquals((-1), testObject.getLength());
        Assert.assertEquals((-1), testObject.getPrecision());
        testObject = ValueMetaFactory.createValueMeta("testSerializable", TYPE_SERIALIZABLE);
        Assert.assertTrue((testObject instanceof ValueMetaSerializable));
        Assert.assertEquals("testSerializable", testObject.getName());
        Assert.assertEquals((-1), testObject.getLength());
        Assert.assertEquals((-1), testObject.getPrecision());
        testObject = ValueMetaFactory.createValueMeta("testSerializable", TYPE_SERIALIZABLE, 10, 20);
        Assert.assertTrue((testObject instanceof ValueMetaSerializable));
        Assert.assertEquals("testSerializable", testObject.getName());
        Assert.assertEquals(10, testObject.getLength());
        Assert.assertEquals(20, testObject.getPrecision());
        testObject = ValueMetaFactory.createValueMeta(TYPE_BINARY);
        Assert.assertTrue((testObject instanceof ValueMetaBinary));
        Assert.assertEquals(null, testObject.getName());
        Assert.assertEquals((-1), testObject.getLength());
        Assert.assertEquals(0, testObject.getPrecision());// Special case for Binary

        testObject = ValueMetaFactory.createValueMeta("testBinary", TYPE_BINARY);
        Assert.assertTrue((testObject instanceof ValueMetaBinary));
        Assert.assertEquals("testBinary", testObject.getName());
        Assert.assertEquals((-1), testObject.getLength());
        Assert.assertEquals(0, testObject.getPrecision());// Special case for Binary

        testObject = ValueMetaFactory.createValueMeta("testBinary", TYPE_BINARY, 10, 20);
        Assert.assertTrue((testObject instanceof ValueMetaBinary));
        Assert.assertEquals("testBinary", testObject.getName());
        Assert.assertEquals(10, testObject.getLength());
        Assert.assertEquals(0, testObject.getPrecision());// Special case for Binary

        testObject = ValueMetaFactory.createValueMeta(TYPE_TIMESTAMP);
        Assert.assertTrue((testObject instanceof ValueMetaTimestamp));
        Assert.assertEquals(null, testObject.getName());
        Assert.assertEquals((-1), testObject.getLength());
        Assert.assertEquals((-1), testObject.getPrecision());
        testObject = ValueMetaFactory.createValueMeta("testTimestamp", TYPE_TIMESTAMP);
        Assert.assertTrue((testObject instanceof ValueMetaTimestamp));
        Assert.assertEquals("testTimestamp", testObject.getName());
        Assert.assertEquals((-1), testObject.getLength());
        Assert.assertEquals((-1), testObject.getPrecision());
        testObject = ValueMetaFactory.createValueMeta("testTimestamp", TYPE_TIMESTAMP, 10, 20);
        Assert.assertTrue((testObject instanceof ValueMetaTimestamp));
        Assert.assertEquals("testTimestamp", testObject.getName());
        Assert.assertEquals(10, testObject.getLength());
        Assert.assertEquals(20, testObject.getPrecision());
        testObject = ValueMetaFactory.createValueMeta(TYPE_INET);
        Assert.assertTrue((testObject instanceof ValueMetaInternetAddress));
        Assert.assertEquals(null, testObject.getName());
        Assert.assertEquals((-1), testObject.getLength());
        Assert.assertEquals((-1), testObject.getPrecision());
        testObject = ValueMetaFactory.createValueMeta("testInternetAddress", TYPE_INET);
        Assert.assertTrue((testObject instanceof ValueMetaInternetAddress));
        Assert.assertEquals("testInternetAddress", testObject.getName());
        Assert.assertEquals((-1), testObject.getLength());
        Assert.assertEquals((-1), testObject.getPrecision());
        testObject = ValueMetaFactory.createValueMeta("testInternetAddress", TYPE_INET, 10, 20);
        Assert.assertTrue((testObject instanceof ValueMetaInternetAddress));
        Assert.assertEquals("testInternetAddress", testObject.getName());
        Assert.assertEquals(10, testObject.getLength());
        Assert.assertEquals(20, testObject.getPrecision());
    }

    @Test
    public void testGetValueMetaNames() {
        List<String> dataTypes = Arrays.<String>asList(ValueMetaFactory.getValueMetaNames());
        Assert.assertTrue(dataTypes.contains("Number"));
        Assert.assertTrue(dataTypes.contains("String"));
        Assert.assertTrue(dataTypes.contains("Date"));
        Assert.assertTrue(dataTypes.contains("Boolean"));
        Assert.assertTrue(dataTypes.contains("Integer"));
        Assert.assertTrue(dataTypes.contains("BigNumber"));
        Assert.assertFalse(dataTypes.contains("Serializable"));
        Assert.assertTrue(dataTypes.contains("Binary"));
        Assert.assertTrue(dataTypes.contains("Timestamp"));
        Assert.assertTrue(dataTypes.contains("Internet Address"));
    }

    @Test
    public void testGetAllValueMetaNames() {
        List<String> dataTypes = Arrays.<String>asList(ValueMetaFactory.getAllValueMetaNames());
        Assert.assertTrue(dataTypes.contains("Number"));
        Assert.assertTrue(dataTypes.contains("String"));
        Assert.assertTrue(dataTypes.contains("Date"));
        Assert.assertTrue(dataTypes.contains("Boolean"));
        Assert.assertTrue(dataTypes.contains("Integer"));
        Assert.assertTrue(dataTypes.contains("BigNumber"));
        Assert.assertTrue(dataTypes.contains("Serializable"));
        Assert.assertTrue(dataTypes.contains("Binary"));
        Assert.assertTrue(dataTypes.contains("Timestamp"));
        Assert.assertTrue(dataTypes.contains("Internet Address"));
    }

    @Test
    public void testGetValueMetaName() {
        Assert.assertEquals("-", ValueMetaFactory.getValueMetaName(Integer.MIN_VALUE));
        Assert.assertEquals("None", ValueMetaFactory.getValueMetaName(TYPE_NONE));
        Assert.assertEquals("Number", ValueMetaFactory.getValueMetaName(TYPE_NUMBER));
        Assert.assertEquals("String", ValueMetaFactory.getValueMetaName(TYPE_STRING));
        Assert.assertEquals("Date", ValueMetaFactory.getValueMetaName(TYPE_DATE));
        Assert.assertEquals("Boolean", ValueMetaFactory.getValueMetaName(TYPE_BOOLEAN));
        Assert.assertEquals("Integer", ValueMetaFactory.getValueMetaName(TYPE_INTEGER));
        Assert.assertEquals("BigNumber", ValueMetaFactory.getValueMetaName(TYPE_BIGNUMBER));
        Assert.assertEquals("Serializable", ValueMetaFactory.getValueMetaName(TYPE_SERIALIZABLE));
        Assert.assertEquals("Binary", ValueMetaFactory.getValueMetaName(TYPE_BINARY));
        Assert.assertEquals("Timestamp", ValueMetaFactory.getValueMetaName(TYPE_TIMESTAMP));
        Assert.assertEquals("Internet Address", ValueMetaFactory.getValueMetaName(TYPE_INET));
    }

    @Test
    public void testGetIdForValueMeta() {
        Assert.assertEquals(TYPE_NONE, ValueMetaFactory.getIdForValueMeta(null));
        Assert.assertEquals(TYPE_NONE, ValueMetaFactory.getIdForValueMeta(""));
        Assert.assertEquals(TYPE_NONE, ValueMetaFactory.getIdForValueMeta("None"));
        Assert.assertEquals(TYPE_NUMBER, ValueMetaFactory.getIdForValueMeta("Number"));
        Assert.assertEquals(TYPE_STRING, ValueMetaFactory.getIdForValueMeta("String"));
        Assert.assertEquals(TYPE_DATE, ValueMetaFactory.getIdForValueMeta("Date"));
        Assert.assertEquals(TYPE_BOOLEAN, ValueMetaFactory.getIdForValueMeta("Boolean"));
        Assert.assertEquals(TYPE_INTEGER, ValueMetaFactory.getIdForValueMeta("Integer"));
        Assert.assertEquals(TYPE_BIGNUMBER, ValueMetaFactory.getIdForValueMeta("BigNumber"));
        Assert.assertEquals(TYPE_SERIALIZABLE, ValueMetaFactory.getIdForValueMeta("Serializable"));
        Assert.assertEquals(TYPE_BINARY, ValueMetaFactory.getIdForValueMeta("Binary"));
        Assert.assertEquals(TYPE_TIMESTAMP, ValueMetaFactory.getIdForValueMeta("Timestamp"));
        Assert.assertEquals(TYPE_INET, ValueMetaFactory.getIdForValueMeta("Internet Address"));
    }

    @Test
    public void testGetValueMetaPluginClasses() throws KettlePluginException {
        List<ValueMetaInterface> dataTypes = ValueMetaFactory.getValueMetaPluginClasses();
        boolean numberExists = false;
        boolean stringExists = false;
        boolean dateExists = false;
        boolean booleanExists = false;
        boolean integerExists = false;
        boolean bignumberExists = false;
        boolean serializableExists = false;
        boolean binaryExists = false;
        boolean timestampExists = false;
        boolean inetExists = false;
        for (ValueMetaInterface obj : dataTypes) {
            if (obj instanceof ValueMetaNumber) {
                numberExists = true;
            }
            if (obj.getClass().equals(ValueMetaString.class)) {
                stringExists = true;
            }
            if (obj.getClass().equals(ValueMetaDate.class)) {
                dateExists = true;
            }
            if (obj.getClass().equals(ValueMetaBoolean.class)) {
                booleanExists = true;
            }
            if (obj.getClass().equals(ValueMetaInteger.class)) {
                integerExists = true;
            }
            if (obj.getClass().equals(ValueMetaBigNumber.class)) {
                bignumberExists = true;
            }
            if (obj.getClass().equals(ValueMetaSerializable.class)) {
                serializableExists = true;
            }
            if (obj.getClass().equals(ValueMetaBinary.class)) {
                binaryExists = true;
            }
            if (obj.getClass().equals(ValueMetaTimestamp.class)) {
                timestampExists = true;
            }
            if (obj.getClass().equals(ValueMetaInternetAddress.class)) {
                inetExists = true;
            }
        }
        Assert.assertTrue(numberExists);
        Assert.assertTrue(stringExists);
        Assert.assertTrue(dateExists);
        Assert.assertTrue(booleanExists);
        Assert.assertTrue(integerExists);
        Assert.assertTrue(bignumberExists);
        Assert.assertTrue(serializableExists);
        Assert.assertTrue(binaryExists);
        Assert.assertTrue(timestampExists);
        Assert.assertTrue(inetExists);
    }

    @Test
    public void testGuessValueMetaInterface() {
        Assert.assertTrue(((ValueMetaFactory.guessValueMetaInterface(new BigDecimal(1.0))) instanceof ValueMetaBigNumber));
        Assert.assertTrue(((ValueMetaFactory.guessValueMetaInterface(new Double(1.0))) instanceof ValueMetaNumber));
        Assert.assertTrue(((ValueMetaFactory.guessValueMetaInterface(new Long(1))) instanceof ValueMetaInteger));
        Assert.assertTrue(((ValueMetaFactory.guessValueMetaInterface(new String())) instanceof ValueMetaString));
        Assert.assertTrue(((ValueMetaFactory.guessValueMetaInterface(new Date())) instanceof ValueMetaDate));
        Assert.assertTrue(((ValueMetaFactory.guessValueMetaInterface(new Boolean(false))) instanceof ValueMetaBoolean));
        Assert.assertTrue(((ValueMetaFactory.guessValueMetaInterface(new Boolean(true))) instanceof ValueMetaBoolean));
        Assert.assertTrue(((ValueMetaFactory.guessValueMetaInterface(false)) instanceof ValueMetaBoolean));
        Assert.assertTrue(((ValueMetaFactory.guessValueMetaInterface(true)) instanceof ValueMetaBoolean));
        Assert.assertTrue(((ValueMetaFactory.guessValueMetaInterface(new byte[10])) instanceof ValueMetaBinary));
        // Test Unsupported Data Types
        Assert.assertEquals(null, ValueMetaFactory.guessValueMetaInterface(null));
        Assert.assertEquals(null, ValueMetaFactory.guessValueMetaInterface(new Short(((short) (1)))));
        Assert.assertEquals(null, ValueMetaFactory.guessValueMetaInterface(new Byte(((byte) (1)))));
        Assert.assertEquals(null, ValueMetaFactory.guessValueMetaInterface(new Float(1.0)));
        Assert.assertEquals(null, ValueMetaFactory.guessValueMetaInterface(new StringBuilder()));
        Assert.assertEquals(null, ValueMetaFactory.guessValueMetaInterface(((byte) (1))));
    }

    @Test
    public void testGetNativeDataTypeClass() throws KettlePluginException {
        for (String valueMetaName : ValueMetaFactory.getValueMetaNames()) {
            int valueMetaID = ValueMetaFactory.getIdForValueMeta(valueMetaName);
            ValueMetaInterface valueMeta = ValueMetaFactory.createValueMeta(valueMetaID);
            try {
                Class<?> clazz = valueMeta.getNativeDataTypeClass();
                Assert.assertNotNull(clazz);
            } catch (KettleValueException kve) {
                Assert.fail((valueMetaName + " should implement getNativeDataTypeClass()"));
            }
        }
    }
}

