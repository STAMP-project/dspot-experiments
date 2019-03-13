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
package org.pentaho.di.trans.steps.userdefinedjavaclass;


import java.net.InetAddress;
import java.sql.Timestamp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBigNumber;
import org.pentaho.di.core.row.value.ValueMetaBinary;
import org.pentaho.di.core.row.value.ValueMetaBoolean;
import org.pentaho.di.core.row.value.ValueMetaDate;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaInternetAddress;
import org.pentaho.di.core.row.value.ValueMetaNumber;
import org.pentaho.di.core.row.value.ValueMetaSerializable;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.row.value.ValueMetaTimestamp;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ FieldHelper.class, FieldHelperTest.class })
public class FieldHelperTest {
    @Test
    public void getNativeDataTypeSimpleName_Unknown() throws Exception {
        KettleValueException e = new KettleValueException();
        ValueMetaInterface v = Mockito.mock(ValueMetaInterface.class);
        Mockito.doThrow(e).when(v).getNativeDataTypeClass();
        LogChannel log = Mockito.mock(LogChannel.class);
        whenNew(LogChannel.class).withAnyArguments().thenReturn(log);
        Assert.assertEquals("Object", FieldHelper.getNativeDataTypeSimpleName(v));
        Mockito.verify(log, Mockito.times(1)).logDebug("Unable to get name from data type");
    }

    @Test
    public void getNativeDataTypeSimpleName_String() {
        ValueMetaString v = new ValueMetaString();
        Assert.assertEquals("String", FieldHelper.getNativeDataTypeSimpleName(v));
    }

    @Test
    public void getNativeDataTypeSimpleName_InetAddress() {
        ValueMetaInternetAddress v = new ValueMetaInternetAddress();
        Assert.assertEquals("InetAddress", FieldHelper.getNativeDataTypeSimpleName(v));
    }

    @Test
    public void getNativeDataTypeSimpleName_Timestamp() {
        ValueMetaTimestamp v = new ValueMetaTimestamp();
        Assert.assertEquals("Timestamp", FieldHelper.getNativeDataTypeSimpleName(v));
    }

    @Test
    public void getNativeDataTypeSimpleName_Binary() {
        ValueMetaBinary v = new ValueMetaBinary();
        Assert.assertEquals("Binary", FieldHelper.getNativeDataTypeSimpleName(v));
    }

    @Test
    public void getGetSignature_String() {
        ValueMetaString v = new ValueMetaString("Name");
        String accessor = FieldHelper.getAccessor(true, "Name");
        Assert.assertEquals("String Name = get(Fields.In, \"Name\").getString(r);", FieldHelper.getGetSignature(accessor, v));
        Assert.assertNotNull(getMethod(FieldHelper.class, "getString", Object[].class));
    }

    @Test
    public void getGetSignature_InetAddress() {
        ValueMetaInternetAddress v = new ValueMetaInternetAddress("IP");
        String accessor = FieldHelper.getAccessor(true, "IP");
        Assert.assertEquals("InetAddress IP = get(Fields.In, \"IP\").getInetAddress(r);", FieldHelper.getGetSignature(accessor, v));
        Assert.assertNotNull(getMethod(FieldHelper.class, "getInetAddress", Object[].class));
    }

    @Test
    public void getGetSignature_Timestamp() {
        ValueMetaTimestamp v = new ValueMetaTimestamp("TS");
        String accessor = FieldHelper.getAccessor(true, "TS");
        Assert.assertEquals("Timestamp TS = get(Fields.In, \"TS\").getTimestamp(r);", FieldHelper.getGetSignature(accessor, v));
        Assert.assertNotNull(getMethod(FieldHelper.class, "getTimestamp", Object[].class));
    }

    @Test
    public void getGetSignature_Binary() {
        ValueMetaBinary v = new ValueMetaBinary("Data");
        String accessor = FieldHelper.getAccessor(true, "Data");
        Assert.assertEquals("byte[] Data = get(Fields.In, \"Data\").getBinary(r);", FieldHelper.getGetSignature(accessor, v));
        Assert.assertNotNull(getMethod(FieldHelper.class, "getBinary", Object[].class));
    }

    @Test
    public void getGetSignature_BigNumber() {
        ValueMetaBigNumber v = new ValueMetaBigNumber("Number");
        String accessor = FieldHelper.getAccessor(true, "Number");
        Assert.assertEquals("BigDecimal Number = get(Fields.In, \"Number\").getBigDecimal(r);", FieldHelper.getGetSignature(accessor, v));
        Assert.assertNotNull(getMethod(FieldHelper.class, "getBigDecimal", Object[].class));
    }

    @Test
    public void getGetSignature_Boolean() {
        ValueMetaBoolean v = new ValueMetaBoolean("Value");
        String accessor = FieldHelper.getAccessor(true, "Value");
        Assert.assertEquals("Boolean Value = get(Fields.In, \"Value\").getBoolean(r);", FieldHelper.getGetSignature(accessor, v));
        Assert.assertNotNull(getMethod(FieldHelper.class, "getBoolean", Object[].class));
    }

    @Test
    public void getGetSignature_Date() {
        ValueMetaDate v = new ValueMetaDate("DT");
        String accessor = FieldHelper.getAccessor(true, "DT");
        Assert.assertEquals("Date DT = get(Fields.In, \"DT\").getDate(r);", FieldHelper.getGetSignature(accessor, v));
        Assert.assertNotNull(getMethod(FieldHelper.class, "getDate", Object[].class));
    }

    @Test
    public void getGetSignature_Integer() {
        ValueMetaInteger v = new ValueMetaInteger("Value");
        String accessor = FieldHelper.getAccessor(true, "Value");
        Assert.assertEquals("Long Value = get(Fields.In, \"Value\").getLong(r);", FieldHelper.getGetSignature(accessor, v));
        Assert.assertNotNull(getMethod(FieldHelper.class, "getLong", Object[].class));
    }

    @Test
    public void getGetSignature_Number() {
        ValueMetaNumber v = new ValueMetaNumber("Value");
        String accessor = FieldHelper.getAccessor(true, "Value");
        Assert.assertEquals("Double Value = get(Fields.In, \"Value\").getDouble(r);", FieldHelper.getGetSignature(accessor, v));
        Assert.assertNotNull(getMethod(FieldHelper.class, "getDouble", Object[].class));
    }

    @Test
    public void getGetSignature_Serializable() throws Exception {
        LogChannel log = Mockito.mock(LogChannel.class);
        whenNew(LogChannel.class).withAnyArguments().thenReturn(log);
        ValueMetaSerializable v = new ValueMetaSerializable("Data");
        String accessor = FieldHelper.getAccessor(true, "Data");
        Assert.assertEquals("Object Data = get(Fields.In, \"Data\").getObject(r);", FieldHelper.getGetSignature(accessor, v));
        Assert.assertNotNull(getMethod(FieldHelper.class, "getObject", Object[].class));
    }

    @Test
    public void getInetAddress_Test() throws Exception {
        ValueMetaInternetAddress v = new ValueMetaInternetAddress("IP");
        RowMetaInterface row = Mockito.mock(RowMetaInterface.class);
        Mockito.doReturn(v).when(row).searchValueMeta(ArgumentMatchers.anyString());
        Mockito.doReturn(0).when(row).indexOfValue(ArgumentMatchers.anyString());
        Assert.assertEquals(InetAddress.getLoopbackAddress(), getInetAddress(new Object[]{ InetAddress.getLoopbackAddress() }));
    }

    @Test
    public void getTimestamp_Test() throws Exception {
        ValueMetaTimestamp v = new ValueMetaTimestamp("TS");
        RowMetaInterface row = Mockito.mock(RowMetaInterface.class);
        Mockito.doReturn(v).when(row).searchValueMeta(ArgumentMatchers.anyString());
        Mockito.doReturn(0).when(row).indexOfValue(ArgumentMatchers.anyString());
        Assert.assertEquals(Timestamp.valueOf("2018-07-23 12:40:55"), getTimestamp(new Object[]{ Timestamp.valueOf("2018-07-23 12:40:55") }));
    }

    @Test
    public void getSerializable_Test() throws Exception {
        ValueMetaSerializable v = new ValueMetaSerializable("Data");
        RowMetaInterface row = Mockito.mock(RowMetaInterface.class);
        Mockito.doReturn(v).when(row).searchValueMeta(ArgumentMatchers.anyString());
        Mockito.doReturn(0).when(row).indexOfValue(ArgumentMatchers.anyString());
        Assert.assertEquals("...", getSerializable(new Object[]{ "..." }));
    }

    @Test
    public void getBinary_Test() throws Exception {
        ValueMetaBinary v = new ValueMetaBinary("Data");
        RowMetaInterface row = Mockito.mock(RowMetaInterface.class);
        Mockito.doReturn(v).when(row).searchValueMeta(ArgumentMatchers.anyString());
        Mockito.doReturn(0).when(row).indexOfValue(ArgumentMatchers.anyString());
        Assert.assertArrayEquals(new byte[]{ 0, 1, 2 }, getBinary(new Object[]{ new byte[]{ 0, 1, 2 } }));
    }

    @Test
    public void setValue_String() {
        ValueMetaString v = new ValueMetaString("Name");
        RowMetaInterface row = Mockito.mock(RowMetaInterface.class);
        Mockito.doReturn(v).when(row).searchValueMeta(ArgumentMatchers.anyString());
        Mockito.doReturn(0).when(row).indexOfValue(ArgumentMatchers.anyString());
        Object[] data = new Object[1];
        new FieldHelper(row, "Name").setValue(data, "Hitachi Vantara");
        Assert.assertEquals("Hitachi Vantara", data[0]);
    }

    @Test
    public void setValue_InetAddress() throws Exception {
        ValueMetaInternetAddress v = new ValueMetaInternetAddress("IP");
        RowMetaInterface row = Mockito.mock(RowMetaInterface.class);
        Mockito.doReturn(v).when(row).searchValueMeta(ArgumentMatchers.anyString());
        Mockito.doReturn(0).when(row).indexOfValue(ArgumentMatchers.anyString());
        Object[] data = new Object[1];
        new FieldHelper(row, "IP").setValue(data, InetAddress.getLoopbackAddress());
        Assert.assertEquals(InetAddress.getLoopbackAddress(), data[0]);
    }

    @Test
    public void setValue_ValueMetaBinary() throws Exception {
        ValueMetaBinary v = new ValueMetaBinary("Data");
        RowMetaInterface row = Mockito.mock(RowMetaInterface.class);
        Mockito.doReturn(v).when(row).searchValueMeta(ArgumentMatchers.anyString());
        Mockito.doReturn(0).when(row).indexOfValue(ArgumentMatchers.anyString());
        Object[] data = new Object[1];
        setValue(data, new byte[]{ 0, 1, 2 });
        Assert.assertArrayEquals(new byte[]{ 0, 1, 2 }, ((byte[]) (data[0])));
    }
}

