/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.jdbc.impl;


import MinorType.BIGINT;
import MinorType.FLOAT4;
import MinorType.FLOAT8;
import MinorType.INT;
import MinorType.SMALLINT;
import MinorType.TINYINT;
import org.apache.drill.categories.JdbcTest;
import org.apache.drill.common.types.TypeProtos.MajorType;
import org.apache.drill.common.types.Types;
import org.apache.drill.exec.vector.accessor.AbstractSqlAccessor;
import org.apache.drill.exec.vector.accessor.InvalidAccessException;
import org.apache.drill.exec.vector.accessor.SqlAccessor;
import org.apache.drill.jdbc.SQLConversionOverflowException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Class-level unit test for {@link TypeConvertingSqlAccessor}.
 * (Also see {@link org.apache.drill.jdbc.ResultSetGetMethodConversionsTest}.
 */
// //////////////////////////////////////
// - getBigDecimal:
// - TINYINT, SMALLINT, INTEGER, BIGINT; REAL, FLOAT, DOUBLE; DECIMAL, NUMERIC;
// - BIT, BOOLEAN;
// - CHAR, VARCHAR, LONGVARCHAR;
// //////////////////////////////////////
// - getBoolean:
// - TINYINT, SMALLINT, INTEGER, BIGINT; REAL, FLOAT, DOUBLE; DECIMAL, NUMERIC;
// - BIT, BOOLEAN;
// - CHAR, VARCHAR, LONGVARCHAR;
// //////////////////////////////////////
// - getString:
// - TINYINT, SMALLINT, INTEGER, BIGINT; REAL, FLOAT, DOUBLE; DECIMAL, NUMERIC;
// - BIT, BOOLEAN;
// - CHAR, VARCHAR, LONGVARCHAR;
// - NCHAR, NVARCHAR, LONGNVARCHAR;
// - BINARY, VARBINARY, LONGVARBINARY;
// - DATE, TIME, TIMESTAMP;
// - DATALINK;
// //////////////////////////////////////
// - getNString:
// - TINYINT, SMALLINT, INTEGER, BIGINT; REAL, FLOAT, DOUBLE; DECIMAL, NUMERIC;
// - BIT, BOOLEAN;
// - CHAR, VARCHAR, LONGVARCHAR;
// - NCHAR, NVARCHAR, LONGNVARCHAR;
// - BINARY, VARBINARY, LONGVARBINARY;
// - DATE, TIME, TIMESTAMP;
// - DATALINK;
// //////////////////////////////////////
// - getBytes:
// - BINARY, VARBINARY, LONGVARBINARY;
// //////////////////////////////////////
// - getDate:
// - CHAR, VARCHAR, LONGVARCHAR;
// - DATE, TIMESTAMP;
// //////////////////////////////////////
// - getTime:
// - CHAR, VARCHAR, LONGVARCHAR;
// - TIME, TIMESTAMP;
// //////////////////////////////////////
// - getTimestamp:
// - CHAR, VARCHAR, LONGVARCHAR;
// - DATE, TIME, TIMESTAMP;
// //////////////////////////////////////
// - getAsciiStream:
// - CHAR, VARCHAR, LONGVARCHAR;
// - BINARY, VARBINARY, LONGVARBINARY;
// - CLOB, NCLOB;
// //////////////////////////////////////
// - getBinaryStream:
// - BINARY, VARBINARY, LONGVARBINARY;
// //////////////////////////////////////
// - getCharacterStream:
// - CHAR, VARCHAR, LONGVARCHAR;
// - NCHAR, NVARCHAR, LONGNVARCHAR;
// - BINARY, VARBINARY, LONGVARBINARY;
// - CLOB, NCLOB;
// - SQLXML;
// //////////////////////////////////////
// - getNCharacterStream:
// - CHAR, VARCHAR, LONGVARCHAR;
// - NCHAR, NVARCHAR, LONGNVARCHAR;
// - BINARY, VARBINARY, LONGVARBINARY;
// - CLOB, NCLOB;
// - SQLXML;
// //////////////////////////////////////
// - getClob:
// - CLOB, NCLOB;
// //////////////////////////////////////
// - getNClob:
// - CLOB, NCLOB;
// //////////////////////////////////////
// - getBlob:
// - BLOB;
// //////////////////////////////////////
// - getArray:
// - ARRAY;
// //////////////////////////////////////
// - getRef:
// - REF;
// //////////////////////////////////////
// - getURL:
// - DATALINK;
// //////////////////////////////////////
// - getObject:
// - TINYINT, SMALLINT, INTEGER, BIGINT; REAL, FLOAT, DOUBLE; DECIMAL, NUMERIC;
// - BIT, BOOLEAN;
// - CHAR, VARCHAR, LONGVARCHAR;
// - NCHAR, NVARCHAR, LONGNVARCHAR;
// - BINARY, VARBINARY, LONGVARBINARY;
// - CLOB, NCLOB;
// - BLOB;
// - DATE, TIME, TIMESTAMP;
// - TIME_WITH_TIMEZONE;
// - TIMESTAMP_WITH_TIMEZONE;
// - DATALINK;
// - ROWID;
// - SQLXML;
// - ARRAY;
// - REF;
// - STRUCT;
// - JAVA_OBJECT;
// //////////////////////////////////////
// - getRowId:
// - ROWID;
// //////////////////////////////////////
// - getSQLXML:
// - SQLXML SQLXML
@Category(JdbcTest.class)
public class TypeConvertingSqlAccessorTest {
    /**
     * Base test stub(?) for accessors underlying TypeConvertingSqlAccessor.
     * Carries type and (Object form of) one value.
     */
    private abstract static class BaseStubAccessor extends AbstractSqlAccessor implements SqlAccessor {
        private final MajorType type;

        private final Object value;

        BaseStubAccessor(MajorType type, Object value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public Class<?> getObjectClass() {
            throw new RuntimeException("Unexpected use of getObjectClass(...)");
        }

        @Override
        public MajorType getType() {
            return type;
        }

        protected Object getValue() {
            return value;
        }

        @Override
        public boolean isNull(int rowOffset) {
            return false;
        }

        @Override
        public Object getObject(int rowOffset) throws InvalidAccessException {
            throw new RuntimeException("Unexpected use of getObject(...)");
        }
    }

    // Byte?  TinyInt?  TINYINT?
    private static class TinyIntStubAccessor extends TypeConvertingSqlAccessorTest.BaseStubAccessor {
        TinyIntStubAccessor(byte value) {
            super(Types.required(TINYINT), value);
        }

        public byte getByte(int rowOffset) {
            return ((Byte) (getValue()));
        }
    }

    // Short?  SmallInt?  SMALLINT?
    private static class SmallIntStubAccessor extends TypeConvertingSqlAccessorTest.BaseStubAccessor {
        SmallIntStubAccessor(short value) {
            super(Types.required(SMALLINT), value);
        }

        public short getShort(int rowOffset) {
            return ((Short) (getValue()));
        }
    }

    // Int?  Int?  INT?
    private static class IntegerStubAccessor extends TypeConvertingSqlAccessorTest.BaseStubAccessor {
        IntegerStubAccessor(int value) {
            super(Types.required(INT), value);
        }

        public int getInt(int rowOffset) {
            return ((Integer) (getValue()));
        }
    }

    // Long?  Bigint?  BIGINT?
    private static class BigIntStubAccessor extends TypeConvertingSqlAccessorTest.BaseStubAccessor {
        BigIntStubAccessor(long value) {
            super(Types.required(BIGINT), value);
        }

        public long getLong(int rowOffset) {
            return ((Long) (getValue()));
        }
    }

    // Float?  Float4?  FLOAT? (REAL?)
    private static class FloatStubAccessor extends TypeConvertingSqlAccessorTest.BaseStubAccessor {
        FloatStubAccessor(float value) {
            super(Types.required(FLOAT4), value);
        }

        public float getFloat(int rowOffset) {
            return ((Float) (getValue()));
        }
    }

    // Double?  Float8?  DOUBLE?
    private static class DoubleStubAccessor extends TypeConvertingSqlAccessorTest.BaseStubAccessor {
        DoubleStubAccessor(double value) {
            super(Types.required(FLOAT8), value);
        }

        public double getDouble(int rowOffset) {
            return ((double) (getValue()));
        }
    }

    // ////////////////////////////////////////////////////////////////////
    // Column accessor (getXxx(...)) methods, in same order as in JDBC 4.2 spec.
    // TABLE B-6 ("Use of ResultSet getter Methods to Retrieve JDBC Data Types"):
    // //////////////////////////////////////
    // - getByte:
    // - TINYINT, SMALLINT, INTEGER, BIGINT; REAL, FLOAT, DOUBLE; DECIMAL, NUMERIC;
    // - BIT, BOOLEAN;
    // - CHAR, VARCHAR, LONGVARCHAR;
    // - ROWID;
    @Test
    public void test_getByte_on_TINYINT_getsIt() throws InvalidAccessException {
        final SqlAccessor uut1 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.TinyIntStubAccessor(((byte) (127))));
        Assert.assertThat(uut1.getByte(0), CoreMatchers.equalTo(((byte) (127))));
        final SqlAccessor uut2 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.TinyIntStubAccessor(((byte) (-128))));
        Assert.assertThat(uut2.getByte(0), CoreMatchers.equalTo(((byte) (-128))));
    }

    @Test
    public void test_getByte_on_SMALLINT_thatFits_getsIt() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.SmallIntStubAccessor(((short) (127))));
        Assert.assertThat(uut.getByte(0), CoreMatchers.equalTo(((byte) (127))));
    }

    @Test(expected = SQLConversionOverflowException.class)
    public void test_getByte_on_SMALLINT_thatOverflows_rejectsIt() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.SmallIntStubAccessor(((short) (128))));
        try {
            uut.getByte(0);
        } catch (Throwable e) {
            // Expect the too-big source value in error message:
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("128"));
            // Probably expect the method name:
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("getByte"));
            // Expect something about source type (original SQL type and default Java
            // type, currently).
            Assert.assertThat(e.getMessage(), CoreMatchers.allOf(CoreMatchers.containsString("short"), CoreMatchers.containsString("SMALLINT")));
            throw e;
        }
    }

    @Test
    public void test_getByte_on_INTEGER_thatFits_getsIt() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.IntegerStubAccessor((-128)));
        Assert.assertThat(uut.getByte(0), CoreMatchers.equalTo(((byte) (-128))));
    }

    @Test(expected = SQLConversionOverflowException.class)
    public void test_getByte_on_INTEGER_thatOverflows_rejectsIt() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.IntegerStubAccessor((-129)));
        try {
            uut.getByte(0);
        } catch (Throwable e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("-129"));
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("getByte"));
            Assert.assertThat(e.getMessage(), CoreMatchers.allOf(CoreMatchers.containsString("int"), CoreMatchers.containsString("INTEGER")));
            throw e;
        }
    }

    @Test
    public void test_getByte_on_BIGINT_thatFits_getsIt() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.BigIntStubAccessor((-128)));
        Assert.assertThat(uut.getByte(0), CoreMatchers.equalTo(((byte) (-128))));
    }

    @Test(expected = SQLConversionOverflowException.class)
    public void test_getByte_on_BIGINT_thatOverflows_rejectsIt() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.BigIntStubAccessor(129));
        try {
            uut.getByte(0);
        } catch (Throwable e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("129"));
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("getByte"));
            Assert.assertThat(e.getMessage(), CoreMatchers.allOf(CoreMatchers.containsString("long"), CoreMatchers.containsString("BIGINT")));
            throw e;
        }
    }

    @Test
    public void test_getByte_on_FLOAT_thatFits_getsIt() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.FloatStubAccessor((-128.0F)));
        Assert.assertThat(uut.getByte(0), CoreMatchers.equalTo(((byte) (-128))));
    }

    @Test(expected = SQLConversionOverflowException.class)
    public void test_getByte_on_FLOAT_thatOverflows_rejectsIt() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.FloatStubAccessor((-130.0F)));
        try {
            uut.getByte(0);
        } catch (Throwable e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("-130"));
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("getByte"));
            Assert.assertThat(e.getMessage(), CoreMatchers.allOf(CoreMatchers.containsString("float"), CoreMatchers.anyOf(CoreMatchers.containsString("REAL"), CoreMatchers.containsString("FLOAT"))));
            throw e;
        }
    }

    @Test
    public void test_getByte_on_DOUBLE_thatFits_getsIt() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.DoubleStubAccessor(127.0));
        Assert.assertThat(uut.getByte(0), CoreMatchers.equalTo(((byte) (127))));
    }

    @Test(expected = SQLConversionOverflowException.class)
    public void test_getByte_on_DOUBLE_thatOverflows_rejectsIt() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.DoubleStubAccessor((-130)));
        try {
            uut.getByte(0);
        } catch (Throwable e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("-130"));
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("getByte"));
            Assert.assertThat(e.getMessage(), CoreMatchers.allOf(CoreMatchers.containsString("double"), CoreMatchers.anyOf(CoreMatchers.containsString("DOUBLE PRECISION"), CoreMatchers.containsString("FLOAT("))));
            throw e;
        }
    }

    // //////////////////////////////////////
    // - getShort:
    // - TINYINT, SMALLINT, INTEGER, BIGINT; REAL, FLOAT, DOUBLE; DECIMAL, NUMERIC;
    // - BIT, BOOLEAN;
    // - CHAR, VARCHAR, LONGVARCHAR;
    @Test
    public void test_getShort_on_TINYINT_getsIt() throws InvalidAccessException {
        final SqlAccessor uut1 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.TinyIntStubAccessor(((byte) (127))));
        Assert.assertThat(uut1.getShort(0), CoreMatchers.equalTo(((short) (127))));
        final SqlAccessor uut2 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.TinyIntStubAccessor(((byte) (-128))));
        Assert.assertThat(uut2.getShort(0), CoreMatchers.equalTo(((short) (-128))));
    }

    @Test
    public void test_getShort_on_SMALLINT_getsIt() throws InvalidAccessException {
        final SqlAccessor uut1 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.SmallIntStubAccessor(((short) (32767))));
        Assert.assertThat(uut1.getShort(0), CoreMatchers.equalTo(((short) (32767))));
        final SqlAccessor uut2 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.SmallIntStubAccessor(((short) (-32768))));
        Assert.assertThat(uut2.getShort(0), CoreMatchers.equalTo(((short) (-32768))));
    }

    @Test
    public void test_getShort_on_INTEGER_thatFits_getsIt() throws InvalidAccessException {
        final SqlAccessor uut1 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.IntegerStubAccessor(32767));
        Assert.assertThat(uut1.getShort(0), CoreMatchers.equalTo(((short) (32767))));
        final SqlAccessor uut2 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.IntegerStubAccessor((-32768)));
        Assert.assertThat(uut2.getShort(0), CoreMatchers.equalTo(((short) (-32768))));
    }

    @Test(expected = SQLConversionOverflowException.class)
    public void test_getShort_on_INTEGER_thatOverflows_throws() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.IntegerStubAccessor((-32769)));
        try {
            uut.getShort(0);
        } catch (Throwable e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("-32769"));
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("getShort"));
            Assert.assertThat(e.getMessage(), CoreMatchers.allOf(CoreMatchers.containsString("int"), CoreMatchers.containsString("INTEGER")));
            throw e;
        }
    }

    @Test
    public void test_getShort_BIGINT_thatFits_getsIt() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.BigIntStubAccessor((-32678)));
        Assert.assertThat(uut.getShort(0), CoreMatchers.equalTo(((short) (-32678))));
    }

    @Test(expected = SQLConversionOverflowException.class)
    public void test_getShort_on_BIGINT_thatOverflows_throws() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.BigIntStubAccessor(65535));
        try {
            uut.getShort(0);
        } catch (Throwable e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("65535"));
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("getShort"));
            Assert.assertThat(e.getMessage(), CoreMatchers.allOf(CoreMatchers.containsString("long"), CoreMatchers.containsString("BIGINT")));
            throw e;
        }
    }

    @Test
    public void test_getShort_on_FLOAT_thatFits_getsIt() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.FloatStubAccessor((-32768.0F)));
        Assert.assertThat(uut.getShort(0), CoreMatchers.equalTo(((short) (-32768))));
    }

    @Test(expected = SQLConversionOverflowException.class)
    public void test_getShort_on_FLOAT_thatOverflows_rejectsIt() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.FloatStubAccessor((-32769.0F)));
        try {
            uut.getShort(0);
        } catch (Throwable e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("-32769"));
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("getShort"));
            Assert.assertThat(e.getMessage(), CoreMatchers.allOf(CoreMatchers.containsString("float"), CoreMatchers.anyOf(CoreMatchers.containsString("REAL"), CoreMatchers.containsString("FLOAT"))));
            throw e;
        }
    }

    @Test
    public void test_getShort_on_DOUBLE_thatFits_getsIt() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.DoubleStubAccessor(32767.0));
        Assert.assertThat(uut.getShort(0), CoreMatchers.equalTo(((short) (32767))));
    }

    @Test(expected = SQLConversionOverflowException.class)
    public void test_getShort_on_DOUBLE_thatOverflows_rejectsIt() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.DoubleStubAccessor(32768));
        try {
            uut.getShort(0);
        } catch (Throwable e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("32768"));
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("getShort"));
            Assert.assertThat(e.getMessage(), CoreMatchers.allOf(CoreMatchers.containsString("double"), CoreMatchers.anyOf(CoreMatchers.containsString("DOUBLE PRECISION"), CoreMatchers.containsString("FLOAT"))));
            throw e;
        }
    }

    // //////////////////////////////////////
    // - getInt:
    // - TINYINT, SMALLINT, INTEGER, BIGINT; REAL, FLOAT, DOUBLE; DECIMAL, NUMERIC;
    // - BIT, BOOLEAN;
    // - CHAR, VARCHAR, LONGVARCHAR;
    @Test
    public void test_getInt_on_TINYINT_getsIt() throws InvalidAccessException {
        final SqlAccessor uut1 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.TinyIntStubAccessor(((byte) (127))));
        Assert.assertThat(uut1.getInt(0), CoreMatchers.equalTo(127));
        final SqlAccessor uut2 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.TinyIntStubAccessor(((byte) (-128))));
        Assert.assertThat(uut2.getInt(0), CoreMatchers.equalTo((-128)));
    }

    @Test
    public void test_getInt_on_SMALLINT_getsIt() throws InvalidAccessException {
        final SqlAccessor uut1 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.SmallIntStubAccessor(((short) (32767))));
        Assert.assertThat(uut1.getInt(0), CoreMatchers.equalTo(32767));
        final SqlAccessor uut2 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.SmallIntStubAccessor(((short) (-32768))));
        Assert.assertThat(uut2.getInt(0), CoreMatchers.equalTo((-32768)));
    }

    @Test
    public void test_getInt_on_INTEGER_getsIt() throws InvalidAccessException {
        final SqlAccessor uut1 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.IntegerStubAccessor(2147483647));
        Assert.assertThat(uut1.getInt(0), CoreMatchers.equalTo(2147483647));
        final SqlAccessor uut2 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.IntegerStubAccessor(-2147483648));
        Assert.assertThat(uut2.getInt(0), CoreMatchers.equalTo(-2147483648));
    }

    @Test
    public void test_getInt_on_BIGINT_thatFits_getsIt() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.BigIntStubAccessor(2147483647));
        Assert.assertThat(uut.getInt(0), CoreMatchers.equalTo(2147483647));
    }

    @Test(expected = SQLConversionOverflowException.class)
    public void test_getInt_on_BIGINT_thatOverflows_throws() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.BigIntStubAccessor(2147483648L));
        try {
            uut.getInt(0);
        } catch (Throwable e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("2147483648"));
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("getInt"));
            Assert.assertThat(e.getMessage(), CoreMatchers.allOf(CoreMatchers.containsString("long"), CoreMatchers.containsString("BIGINT")));
            throw e;
        }
    }

    @Test
    public void test_getInt_on_FLOAT_thatFits_getsIt() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.FloatStubAccessor(1.0E9F));
        Assert.assertThat(uut.getInt(0), CoreMatchers.equalTo(1000000000));
    }

    @Test(expected = SQLConversionOverflowException.class)
    public void test_getInt_on_FLOAT_thatOverflows_rejectsIt() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.FloatStubAccessor(1.0E10F));
        try {
            uut.getInt(0);
        } catch (Throwable e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("1.0E10"));
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("getInt"));
            Assert.assertThat(e.getMessage(), CoreMatchers.allOf(CoreMatchers.containsString("float"), CoreMatchers.anyOf(CoreMatchers.containsString("REAL"), CoreMatchers.containsString("FLOAT"))));
            throw e;
        }
    }

    @Test
    public void test_getInt_on_DOUBLE_thatFits_getsIt() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.DoubleStubAccessor((-2.147483648E9)));
        Assert.assertThat(uut.getInt(0), CoreMatchers.equalTo(-2147483648));
    }

    @Test(expected = SQLConversionOverflowException.class)
    public void test_getInt_on_DOUBLE_thatOverflows_rejectsIt() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.DoubleStubAccessor((-2.147483649E9)));
        try {
            uut.getInt(0);
        } catch (Throwable e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("-2.147483649E9"));
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("getInt"));
            Assert.assertThat(e.getMessage(), CoreMatchers.allOf(CoreMatchers.containsString("double"), CoreMatchers.containsString("DOUBLE PRECISION")));
            throw e;
        }
    }

    // //////////////////////////////////////
    // - getLong:
    // - TINYINT, SMALLINT, INTEGER, BIGINT; REAL, FLOAT, DOUBLE; DECIMAL, NUMERIC;
    // - BIT, BOOLEAN;
    // - CHAR, VARCHAR, LONGVARCHAR;
    @Test
    public void test_getLong_on_TINYINT_getsIt() throws InvalidAccessException {
        final SqlAccessor uut1 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.TinyIntStubAccessor(((byte) (127))));
        Assert.assertThat(uut1.getLong(0), CoreMatchers.equalTo(127L));
        final SqlAccessor uut2 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.TinyIntStubAccessor(((byte) (-128))));
        Assert.assertThat(uut2.getLong(0), CoreMatchers.equalTo((-128L)));
    }

    @Test
    public void test_getLong_on_SMALLINT_getsIt() throws InvalidAccessException {
        final SqlAccessor uut1 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.SmallIntStubAccessor(((short) (32767))));
        Assert.assertThat(uut1.getLong(0), CoreMatchers.equalTo(32767L));
        final SqlAccessor uut2 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.SmallIntStubAccessor(((short) (-32768))));
        Assert.assertThat(uut2.getLong(0), CoreMatchers.equalTo((-32768L)));
    }

    @Test
    public void test_getLong_on_INTEGER_getsIt() throws InvalidAccessException {
        final SqlAccessor uut1 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.IntegerStubAccessor(2147483647));
        Assert.assertThat(uut1.getLong(0), CoreMatchers.equalTo(2147483647L));
        final SqlAccessor uut2 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.IntegerStubAccessor(-2147483648));
        Assert.assertThat(uut2.getLong(0), CoreMatchers.equalTo((-2147483648L)));
    }

    @Test
    public void test_getLong_on_BIGINT_getsIt() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.BigIntStubAccessor(2147483648L));
        Assert.assertThat(uut.getLong(0), CoreMatchers.equalTo(2147483648L));
    }

    @Test
    public void test_getLong_on_FLOAT_thatFits_getsIt() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.FloatStubAccessor((9223372036854775807L * 1.0F)));
        Assert.assertThat(uut.getLong(0), CoreMatchers.equalTo(9223372036854775807L));
    }

    @Test(expected = SQLConversionOverflowException.class)
    public void test_getLong_on_FLOAT_thatOverflows_rejectsIt() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.FloatStubAccessor(1.5E20F));
        try {
            uut.getLong(0);
        } catch (Throwable e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("1.5000"));
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("getLong"));
            Assert.assertThat(e.getMessage(), CoreMatchers.allOf(CoreMatchers.containsString("float"), CoreMatchers.anyOf(CoreMatchers.containsString("REAL"), CoreMatchers.containsString("FLOAT"))));
            throw e;
        }
    }

    @Test
    public void test_getLong_on_DOUBLE_thatFits_getsIt() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.DoubleStubAccessor((9223372036854775807L * 1.0)));
        Assert.assertThat(uut.getLong(0), CoreMatchers.equalTo(9223372036854775807L));
    }

    @Test(expected = SQLConversionOverflowException.class)
    public void test_getLong_on_DOUBLE_thatOverflows_rejectsIt() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.DoubleStubAccessor(1.0E20));
        try {
            uut.getLong(0);
        } catch (Throwable e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("1.0E20"));
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("getLong"));
            Assert.assertThat(e.getMessage(), CoreMatchers.allOf(CoreMatchers.containsString("double"), CoreMatchers.containsString("DOUBLE PRECISION")));
            throw e;
        }
    }

    // //////////////////////////////////////
    // - getFloat:
    // - TINYINT, SMALLINT, INTEGER, BIGINT; REAL, FLOAT, DOUBLE; DECIMAL, NUMERIC;
    // - BIT, BOOLEAN;
    // - CHAR, VARCHAR, LONGVARCHAR;
    @Test
    public void test_getFloat_on_FLOAT_getsIt() throws InvalidAccessException {
        final SqlAccessor uut1 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.FloatStubAccessor(1.23F));
        Assert.assertThat(uut1.getFloat(0), CoreMatchers.equalTo(1.23F));
        final SqlAccessor uut2 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.FloatStubAccessor(Float.MAX_VALUE));
        Assert.assertThat(uut2.getFloat(0), CoreMatchers.equalTo(Float.MAX_VALUE));
        final SqlAccessor uut3 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.FloatStubAccessor(Float.MIN_VALUE));
        Assert.assertThat(uut3.getFloat(0), CoreMatchers.equalTo(Float.MIN_VALUE));
    }

    @Test
    public void test_getFloat_on_DOUBLE_thatFits_getsIt() throws InvalidAccessException {
        final SqlAccessor uut1 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.DoubleStubAccessor(1.125));
        Assert.assertThat(uut1.getFloat(0), CoreMatchers.equalTo(1.125F));
        final SqlAccessor uut2 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.DoubleStubAccessor(Float.MAX_VALUE));
        Assert.assertThat(uut2.getFloat(0), CoreMatchers.equalTo(Float.MAX_VALUE));
    }

    @Test(expected = SQLConversionOverflowException.class)
    public void test_getFloat_on_DOUBLE_thatOverflows_throws() throws InvalidAccessException {
        final SqlAccessor uut = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.DoubleStubAccessor(1.0E100));
        try {
            uut.getFloat(0);
        } catch (Throwable e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("1.0E100"));
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("getFloat"));
            Assert.assertThat(e.getMessage(), CoreMatchers.allOf(CoreMatchers.containsString("double"), CoreMatchers.anyOf(CoreMatchers.containsString("DOUBLE PRECISION"), CoreMatchers.containsString("FLOAT"))));
            throw e;
        }
    }

    // //////////////////////////////////////
    // - getDouble:
    // - TINYINT, SMALLINT, INTEGER, BIGINT; REAL, FLOAT, DOUBLE; DECIMAL, NUMERIC;
    // - BIT, BOOLEAN;
    // - CHAR, VARCHAR, LONGVARCHAR;
    @Test
    public void test_getDouble_on_FLOAT_getsIt() throws InvalidAccessException {
        final SqlAccessor uut1 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.FloatStubAccessor(6.02E23F));
        Assert.assertThat(uut1.getDouble(0), CoreMatchers.equalTo(((double) (6.02E23F))));
        final SqlAccessor uut2 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.FloatStubAccessor(Float.MAX_VALUE));
        Assert.assertThat(uut2.getDouble(0), CoreMatchers.equalTo(((double) (Float.MAX_VALUE))));
        final SqlAccessor uut3 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.FloatStubAccessor(Float.MIN_VALUE));
        Assert.assertThat(uut3.getDouble(0), CoreMatchers.equalTo(((double) (Float.MIN_VALUE))));
    }

    @Test
    public void test_getDouble_on_DOUBLE_getsIt() throws InvalidAccessException {
        final SqlAccessor uut1 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.DoubleStubAccessor((-1.0E100)));
        Assert.assertThat(uut1.getDouble(0), CoreMatchers.equalTo((-1.0E100)));
        final SqlAccessor uut2 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.DoubleStubAccessor(Double.MAX_VALUE));
        Assert.assertThat(uut2.getDouble(0), CoreMatchers.equalTo(Double.MAX_VALUE));
        final SqlAccessor uut3 = new TypeConvertingSqlAccessor(new TypeConvertingSqlAccessorTest.DoubleStubAccessor(Double.MIN_VALUE));
        Assert.assertThat(uut3.getDouble(0), CoreMatchers.equalTo(Double.MIN_VALUE));
    }
}

