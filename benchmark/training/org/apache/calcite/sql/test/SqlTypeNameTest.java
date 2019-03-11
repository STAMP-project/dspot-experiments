/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.sql.test;


import ExtraSqlTypes.LONGNVARCHAR;
import ExtraSqlTypes.NCHAR;
import ExtraSqlTypes.NCLOB;
import ExtraSqlTypes.NVARCHAR;
import ExtraSqlTypes.ROWID;
import ExtraSqlTypes.SQLXML;
import SqlTypeName.ARRAY;
import SqlTypeName.BIGINT;
import SqlTypeName.BINARY;
import SqlTypeName.BOOLEAN;
import SqlTypeName.CHAR;
import SqlTypeName.DATE;
import SqlTypeName.DECIMAL;
import SqlTypeName.DISTINCT;
import SqlTypeName.DOUBLE;
import SqlTypeName.FLOAT;
import SqlTypeName.INTEGER;
import SqlTypeName.REAL;
import SqlTypeName.SMALLINT;
import SqlTypeName.STRUCTURED;
import SqlTypeName.TIME;
import SqlTypeName.TIMESTAMP;
import SqlTypeName.TINYINT;
import SqlTypeName.VARBINARY;
import SqlTypeName.VARCHAR;
import java.sql.Types;
import org.apache.calcite.sql.type.SqlTypeName;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests types supported by {@link SqlTypeName}.
 */
public class SqlTypeNameTest {
    @Test
    public void testBit() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.BIT);
        Assert.assertEquals("BIT did not map to BOOLEAN", BOOLEAN, tn);
    }

    @Test
    public void testTinyint() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.TINYINT);
        Assert.assertEquals("TINYINT did not map to TINYINT", TINYINT, tn);
    }

    @Test
    public void testSmallint() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.SMALLINT);
        Assert.assertEquals("SMALLINT did not map to SMALLINT", SMALLINT, tn);
    }

    @Test
    public void testInteger() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.INTEGER);
        Assert.assertEquals("INTEGER did not map to INTEGER", INTEGER, tn);
    }

    @Test
    public void testBigint() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.BIGINT);
        Assert.assertEquals("BIGINT did not map to BIGINT", BIGINT, tn);
    }

    @Test
    public void testFloat() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.FLOAT);
        Assert.assertEquals("FLOAT did not map to FLOAT", FLOAT, tn);
    }

    @Test
    public void testReal() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.REAL);
        Assert.assertEquals("REAL did not map to REAL", REAL, tn);
    }

    @Test
    public void testDouble() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.DOUBLE);
        Assert.assertEquals("DOUBLE did not map to DOUBLE", DOUBLE, tn);
    }

    @Test
    public void testNumeric() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.NUMERIC);
        Assert.assertEquals("NUMERIC did not map to DECIMAL", DECIMAL, tn);
    }

    @Test
    public void testDecimal() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.DECIMAL);
        Assert.assertEquals("DECIMAL did not map to DECIMAL", DECIMAL, tn);
    }

    @Test
    public void testChar() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.CHAR);
        Assert.assertEquals("CHAR did not map to CHAR", CHAR, tn);
    }

    @Test
    public void testVarchar() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.VARCHAR);
        Assert.assertEquals("VARCHAR did not map to VARCHAR", VARCHAR, tn);
    }

    @Test
    public void testLongvarchar() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.LONGVARCHAR);
        Assert.assertEquals("LONGVARCHAR did not map to null", null, tn);
    }

    @Test
    public void testDate() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.DATE);
        Assert.assertEquals("DATE did not map to DATE", DATE, tn);
    }

    @Test
    public void testTime() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.TIME);
        Assert.assertEquals("TIME did not map to TIME", TIME, tn);
    }

    @Test
    public void testTimestamp() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.TIMESTAMP);
        Assert.assertEquals("TIMESTAMP did not map to TIMESTAMP", TIMESTAMP, tn);
    }

    @Test
    public void testBinary() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.BINARY);
        Assert.assertEquals("BINARY did not map to BINARY", BINARY, tn);
    }

    @Test
    public void testVarbinary() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.VARBINARY);
        Assert.assertEquals("VARBINARY did not map to VARBINARY", VARBINARY, tn);
    }

    @Test
    public void testLongvarbinary() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.LONGVARBINARY);
        Assert.assertEquals("LONGVARBINARY did not map to null", null, tn);
    }

    @Test
    public void testNull() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.NULL);
        Assert.assertEquals("NULL did not map to null", null, tn);
    }

    @Test
    public void testOther() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.OTHER);
        Assert.assertEquals("OTHER did not map to null", null, tn);
    }

    @Test
    public void testJavaobject() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.JAVA_OBJECT);
        Assert.assertEquals("JAVA_OBJECT did not map to null", null, tn);
    }

    @Test
    public void testDistinct() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.DISTINCT);
        Assert.assertEquals("DISTINCT did not map to DISTINCT", DISTINCT, tn);
    }

    @Test
    public void testStruct() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.STRUCT);
        Assert.assertEquals("STRUCT did not map to null", STRUCTURED, tn);
    }

    @Test
    public void testArray() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.ARRAY);
        Assert.assertEquals("ARRAY did not map to ARRAY", ARRAY, tn);
    }

    @Test
    public void testBlob() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.BLOB);
        Assert.assertEquals("BLOB did not map to null", null, tn);
    }

    @Test
    public void testClob() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.CLOB);
        Assert.assertEquals("CLOB did not map to null", null, tn);
    }

    @Test
    public void testRef() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.REF);
        Assert.assertEquals("REF did not map to null", null, tn);
    }

    @Test
    public void testDatalink() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.DATALINK);
        Assert.assertEquals("DATALINK did not map to null", null, tn);
    }

    @Test
    public void testBoolean() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(Types.BOOLEAN);
        Assert.assertEquals("BOOLEAN did not map to BOOLEAN", BOOLEAN, tn);
    }

    @Test
    public void testRowid() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(ROWID);
        // ROWID not supported yet
        Assert.assertEquals("ROWID maps to non-null type", null, tn);
    }

    @Test
    public void testNchar() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(NCHAR);
        // NCHAR not supported yet, currently maps to CHAR
        Assert.assertEquals("NCHAR did not map to CHAR", CHAR, tn);
    }

    @Test
    public void testNvarchar() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(NVARCHAR);
        // NVARCHAR not supported yet, currently maps to VARCHAR
        Assert.assertEquals("NVARCHAR did not map to VARCHAR", VARCHAR, tn);
    }

    @Test
    public void testLongnvarchar() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(LONGNVARCHAR);
        // LONGNVARCHAR not supported yet
        Assert.assertEquals("LONGNVARCHAR maps to non-null type", null, tn);
    }

    @Test
    public void testNclob() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(NCLOB);
        // NCLOB not supported yet
        Assert.assertEquals("NCLOB maps to non-null type", null, tn);
    }

    @Test
    public void testSqlxml() {
        SqlTypeName tn = SqlTypeName.getNameForJdbcType(SQLXML);
        // SQLXML not supported yet
        Assert.assertEquals("SQLXML maps to non-null type", null, tn);
    }
}

/**
 * End SqlTypeNameTest.java
 */
