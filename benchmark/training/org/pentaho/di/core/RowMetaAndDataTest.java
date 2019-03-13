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
package org.pentaho.di.core;


import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.injection.DefaultInjectionTypeConverter;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBigNumber;
import org.pentaho.di.core.row.value.ValueMetaBinary;
import org.pentaho.di.core.row.value.ValueMetaBoolean;
import org.pentaho.di.core.row.value.ValueMetaDate;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaInternetAddress;
import org.pentaho.di.core.row.value.ValueMetaNumber;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.row.value.ValueMetaTimestamp;


public class RowMetaAndDataTest {
    RowMeta rowsMeta;

    RowMetaAndData row;

    DefaultInjectionTypeConverter converter = new DefaultInjectionTypeConverter();

    enum TestEnum {

        ONE,
        Two,
        three;}

    @Test
    public void testMergeRowAndMetaData() {
        row = new RowMetaAndData(rowsMeta, "text", true, 1);
        RowMeta addRowMeta = new RowMeta();
        ValueMetaInterface valueMetaString = new ValueMetaString("str");
        addRowMeta.addValueMeta(valueMetaString);
        RowMetaAndData addRow = new RowMetaAndData(addRowMeta, "text1");
        row.mergeRowMetaAndData(addRow, "originName");
        Assert.assertEquals(4, row.size());
        Assert.assertEquals("text", row.getData()[0]);
        Assert.assertEquals("text1", row.getData()[3]);
        Assert.assertEquals("originName", row.getValueMeta(3).getOrigin());
    }

    @Test
    public void testStringConversion() throws Exception {
        row = new RowMetaAndData(rowsMeta, "text", null, null);
        Assert.assertEquals("text", row.getAsJavaType("str", String.class, converter));
        row = new RowMetaAndData(rowsMeta, "7", null, null);
        Assert.assertEquals(7, row.getAsJavaType("str", int.class, converter));
        Assert.assertEquals(7, row.getAsJavaType("str", Integer.class, converter));
        Assert.assertEquals(7L, row.getAsJavaType("str", long.class, converter));
        Assert.assertEquals(7L, row.getAsJavaType("str", Long.class, converter));
        row = new RowMetaAndData(rowsMeta, "y", null, null);
        Assert.assertEquals(true, row.getAsJavaType("str", boolean.class, converter));
        Assert.assertEquals(true, row.getAsJavaType("str", Boolean.class, converter));
        row = new RowMetaAndData(rowsMeta, "yes", null, null);
        Assert.assertEquals(true, row.getAsJavaType("str", boolean.class, converter));
        Assert.assertEquals(true, row.getAsJavaType("str", Boolean.class, converter));
        row = new RowMetaAndData(rowsMeta, "true", null, null);
        Assert.assertEquals(true, row.getAsJavaType("str", boolean.class, converter));
        Assert.assertEquals(true, row.getAsJavaType("str", Boolean.class, converter));
        row = new RowMetaAndData(rowsMeta, "no", null, null);
        Assert.assertEquals(false, row.getAsJavaType("str", boolean.class, converter));
        Assert.assertEquals(false, row.getAsJavaType("str", Boolean.class, converter));
        row = new RowMetaAndData(rowsMeta, "n", null, null);
        Assert.assertEquals(false, row.getAsJavaType("str", boolean.class, converter));
        Assert.assertEquals(false, row.getAsJavaType("str", Boolean.class, converter));
        row = new RowMetaAndData(rowsMeta, "false", null, null);
        Assert.assertEquals(false, row.getAsJavaType("str", boolean.class, converter));
        Assert.assertEquals(false, row.getAsJavaType("str", Boolean.class, converter));
        row = new RowMetaAndData(rowsMeta, "f", null, null);
        Assert.assertEquals(false, row.getAsJavaType("str", boolean.class, converter));
        Assert.assertEquals(false, row.getAsJavaType("str", Boolean.class, converter));
        row = new RowMetaAndData(rowsMeta, "other", null, null);
        Assert.assertEquals(false, row.getAsJavaType("str", boolean.class, converter));
        Assert.assertEquals(false, row.getAsJavaType("str", Boolean.class, converter));
        row = new RowMetaAndData(rowsMeta, RowMetaAndDataTest.TestEnum.ONE.name(), null, null);
        Assert.assertEquals(RowMetaAndDataTest.TestEnum.ONE, row.getAsJavaType("str", RowMetaAndDataTest.TestEnum.class, converter));
        row = new RowMetaAndData(rowsMeta, RowMetaAndDataTest.TestEnum.Two.name(), null, null);
        Assert.assertEquals(RowMetaAndDataTest.TestEnum.Two, row.getAsJavaType("str", RowMetaAndDataTest.TestEnum.class, converter));
        row = new RowMetaAndData(rowsMeta, RowMetaAndDataTest.TestEnum.three.name(), null, null);
        Assert.assertEquals(RowMetaAndDataTest.TestEnum.three, row.getAsJavaType("str", RowMetaAndDataTest.TestEnum.class, converter));
        row = new RowMetaAndData(rowsMeta, null, null, null);
        Assert.assertEquals(null, row.getAsJavaType("str", String.class, converter));
        Assert.assertEquals(null, row.getAsJavaType("str", Integer.class, converter));
        Assert.assertEquals(null, row.getAsJavaType("str", Long.class, converter));
        Assert.assertEquals(null, row.getAsJavaType("str", Boolean.class, converter));
    }

    @Test
    public void testBooleanConversion() throws Exception {
        row = new RowMetaAndData(rowsMeta, null, true, null);
        Assert.assertEquals(true, row.getAsJavaType("bool", boolean.class, converter));
        Assert.assertEquals(true, row.getAsJavaType("bool", Boolean.class, converter));
        Assert.assertEquals(1, row.getAsJavaType("bool", int.class, converter));
        Assert.assertEquals(1, row.getAsJavaType("bool", Integer.class, converter));
        Assert.assertEquals(1L, row.getAsJavaType("bool", long.class, converter));
        Assert.assertEquals(1L, row.getAsJavaType("bool", Long.class, converter));
        Assert.assertEquals("Y", row.getAsJavaType("bool", String.class, converter));
        row = new RowMetaAndData(rowsMeta, null, false, null);
        Assert.assertEquals(false, row.getAsJavaType("bool", boolean.class, converter));
        Assert.assertEquals(false, row.getAsJavaType("bool", Boolean.class, converter));
        Assert.assertEquals(0, row.getAsJavaType("bool", int.class, converter));
        Assert.assertEquals(0, row.getAsJavaType("bool", Integer.class, converter));
        Assert.assertEquals(0L, row.getAsJavaType("bool", long.class, converter));
        Assert.assertEquals(0L, row.getAsJavaType("bool", Long.class, converter));
        Assert.assertEquals("N", row.getAsJavaType("bool", String.class, converter));
        row = new RowMetaAndData(rowsMeta, null, null, null);
        Assert.assertEquals(null, row.getAsJavaType("bool", String.class, converter));
        Assert.assertEquals(null, row.getAsJavaType("bool", Integer.class, converter));
        Assert.assertEquals(null, row.getAsJavaType("bool", Long.class, converter));
        Assert.assertEquals(null, row.getAsJavaType("bool", Boolean.class, converter));
    }

    @Test
    public void testIntegerConversion() throws Exception {
        row = new RowMetaAndData(rowsMeta, null, null, 7L);
        Assert.assertEquals(true, row.getAsJavaType("int", boolean.class, converter));
        Assert.assertEquals(true, row.getAsJavaType("int", Boolean.class, converter));
        Assert.assertEquals(7, row.getAsJavaType("int", int.class, converter));
        Assert.assertEquals(7, row.getAsJavaType("int", Integer.class, converter));
        Assert.assertEquals(7L, row.getAsJavaType("int", long.class, converter));
        Assert.assertEquals(7L, row.getAsJavaType("int", Long.class, converter));
        Assert.assertEquals("7", row.getAsJavaType("int", String.class, converter));
        row = new RowMetaAndData(rowsMeta, null, null, 0L);
        Assert.assertEquals(false, row.getAsJavaType("int", boolean.class, converter));
        Assert.assertEquals(false, row.getAsJavaType("int", Boolean.class, converter));
        row = new RowMetaAndData(rowsMeta, null, null, null);
        Assert.assertEquals(null, row.getAsJavaType("int", String.class, converter));
        Assert.assertEquals(null, row.getAsJavaType("int", Integer.class, converter));
        Assert.assertEquals(null, row.getAsJavaType("int", Long.class, converter));
        Assert.assertEquals(null, row.getAsJavaType("int", Boolean.class, converter));
    }

    @Test
    public void testEmptyValues() throws Exception {
        RowMeta rowsMetaEmpty = new RowMeta();
        rowsMetaEmpty.addValueMeta(new ValueMetaString("str"));
        rowsMetaEmpty.addValueMeta(new ValueMetaBoolean("bool"));
        rowsMetaEmpty.addValueMeta(new ValueMetaInteger("int"));
        rowsMetaEmpty.addValueMeta(new ValueMetaNumber("num"));
        rowsMetaEmpty.addValueMeta(new ValueMetaBigNumber("bignum"));
        rowsMetaEmpty.addValueMeta(new ValueMetaBinary("bin"));
        rowsMetaEmpty.addValueMeta(new ValueMetaDate("date"));
        rowsMetaEmpty.addValueMeta(new ValueMetaTimestamp("timestamp"));
        rowsMetaEmpty.addValueMeta(new ValueMetaInternetAddress("inet"));
        row = new RowMetaAndData(rowsMetaEmpty, null, null, null, null, null, null, null, null, null);
        Assert.assertTrue(row.isEmptyValue("str"));
        Assert.assertTrue(row.isEmptyValue("bool"));
        Assert.assertTrue(row.isEmptyValue("int"));
        Assert.assertTrue(row.isEmptyValue("num"));
        Assert.assertTrue(row.isEmptyValue("bignum"));
        Assert.assertTrue(row.isEmptyValue("bin"));
        Assert.assertTrue(row.isEmptyValue("date"));
        Assert.assertTrue(row.isEmptyValue("timestamp"));
        Assert.assertTrue(row.isEmptyValue("inet"));
    }
}

