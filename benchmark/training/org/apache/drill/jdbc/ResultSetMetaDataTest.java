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
package org.apache.drill.jdbc;


import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import org.apache.drill.categories.JdbcTest;
import org.hamcrest.CoreMatchers;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test class for Drill's java.sql.ResultSetMetaData implementation.
 * <p>
 *   Based on JDBC 4.1 (Java 7).
 * </p>
 */
// TODO(DRILL-3253):  Do more types when we have all-types test storage plugin.
@Category(JdbcTest.class)
public class ResultSetMetaDataTest extends JdbcTestBase {
    private static final String VIEW_NAME = (ResultSetMetaDataTest.class.getSimpleName()) + "_View";

    /**
     * The one shared JDBC connection to Drill.
     */
    private static Connection connection;

    // Result set with test columns of various types.  Is positioned at first row
    // for, and must not be modified by, test methods.
    private static ResultSet viewRow;

    // Metadata for result set.
    private static ResultSetMetaData rowMetadata;

    // ////////
    // For columns in temporary test view (types accessible via casting):
    // (Dynamic to make it simpler to add or remove columns.)
    private static int columnCount;

    private static int ordOptBOOLEAN;

    private static int ordReqBOOLEAN;

    private static int ordReqSMALLINT;

    private static int ordReqINTEGER;

    private static int ordReqBIGINT;

    private static int ordReqREAL;

    private static int ordReqFLOAT;

    private static int ordReqDOUBLE;

    private static int ordReqDECIMAL_5_3;

    // No NUMERIC while Drill just maps it to DECIMAL.
    private static int ordReqVARCHAR_10;

    private static int ordOptVARCHAR;

    private static int ordReqCHAR_5;

    // No NCHAR, etc., in Drill (?).
    private static int ordOptVARBINARY_16;

    private static int ordOptBINARY_1048576;

    private static int ordReqDATE;

    private static int ordReqTIME_2;

    private static int ordOptTIME_7;

    private static int ordReqTIMESTAMP_4;

    // No "... WITH TIME ZONE" in Drill.
    private static int ordReqINTERVAL_Y;

    private static int ordReqINTERVAL_3Y_Mo;

    private static int ordReqINTERVAL_10Y_Mo;

    private static int ordReqINTERVAL_Mo;

    private static int ordReqINTERVAL_D;

    private static int ordReqINTERVAL_4D_H;

    private static int ordReqINTERVAL_3D_Mi;

    private static int ordReqINTERVAL_2D_S5;

    private static int ordReqINTERVAL_H;

    private static int ordReqINTERVAL_1H_Mi;

    private static int ordReqINTERVAL_3H_S1;

    private static int ordReqINTERVAL_Mi;

    private static int ordReqINTERVAL_5Mi_S;

    private static int ordReqINTERVAL_S;

    private static int ordReqINTERVAL_3S;

    private static int ordReqINTERVAL_3S1;

    // ////////////////////////////////////////////////////////////////////
    // Tests:
    // //////////////////////////////////////////////////////////
    // getColumnCount(...):
    // JDBC: "Returns the number of columns in this ResultSet object."
    @Test
    public void test_getColumnCount() throws SQLException {
        Assert.assertThat("column count", ResultSetMetaDataTest.rowMetadata.getColumnCount(), CoreMatchers.equalTo(ResultSetMetaDataTest.columnCount));
    }

    // //////////////////////////////////////////////////////////
    // isAutoIncrement(...):
    // JDBC: "Indicates whether the designated column is automatically numbered."
    @Test
    public void test_isAutoIncrement_returnsFalse() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.isAutoIncrement(ResultSetMetaDataTest.ordOptBOOLEAN), CoreMatchers.equalTo(false));
    }

    // //////////////////////////////////////////////////////////
    // isCaseSensitive(...):
    // JDBC: "Indicates whether a column's case matters."
    // (Presumably that refers to the column's name, not values.)
    // Matters for what (for which operations)?
    @Test
    public void test_isCaseSensitive_nameThisNonSpecific() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.isCaseSensitive(ResultSetMetaDataTest.ordOptBOOLEAN), CoreMatchers.equalTo(false));
    }

    // //////////////////////////////////////////////////////////
    // isSearchable(...):
    // JDBC: "Indicates whether the designated column can be used in a where
    // clause."
    // (Is there any reason a column couldn't be used in a WHERE clause?)
    @Test
    public void test_isSearchable_returnsTrue() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.isSearchable(ResultSetMetaDataTest.ordOptBOOLEAN), CoreMatchers.equalTo(true));
    }

    // //////////////////////////////////////////////////////////
    // isCurrency(...):
    // JDBC: "Indicates whether the designated column is a cash value."
    @Test
    public void test_isCurrency_returnsFalse() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.isCurrency(ResultSetMetaDataTest.ordOptBOOLEAN), CoreMatchers.equalTo(false));
    }

    // //////////////////////////////////////////////////////////
    // isNullable(...):
    // JDBC: "Indicates the nullability of values in the designated column."
    @Test
    public void test_isNullable_forNullable() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.isNullable(ResultSetMetaDataTest.ordOptBOOLEAN), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }

    @Test
    public void test_isNullable_forRequired() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.isNullable(ResultSetMetaDataTest.ordReqINTEGER), CoreMatchers.equalTo(ResultSetMetaData.columnNoNulls));
    }

    // //////////////////////////////////////////////////////////
    // isSigned(...):
    // JDBC: "Indicates whether values in the designated column are signed numbers."
    // (Does "signed numbers" include intervals (which are signed)?
    @Test
    public void test_isSigned_forBOOLEAN() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.isSigned(ResultSetMetaDataTest.ordOptBOOLEAN), CoreMatchers.equalTo(false));
    }

    @Test
    public void test_isSigned_forINTEGER() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.isSigned(ResultSetMetaDataTest.ordReqINTEGER), CoreMatchers.equalTo(true));
    }

    @Test
    public void test_isSigned_forDOUBLE() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.isSigned(ResultSetMetaDataTest.ordReqDOUBLE), CoreMatchers.equalTo(true));
    }

    @Test
    public void test_isSigned_forDECIMAL_5_3() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.isSigned(ResultSetMetaDataTest.ordReqDECIMAL_5_3), CoreMatchers.equalTo(true));
    }

    @Test
    public void test_isSigned_forVARCHAR() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.isSigned(ResultSetMetaDataTest.ordReqVARCHAR_10), CoreMatchers.equalTo(false));
    }

    @Test
    public void test_isSigned_forDate() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.isSigned(ResultSetMetaDataTest.ordReqDATE), CoreMatchers.equalTo(false));
    }

    @Test
    public void test_isSigned_forTIME_2() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.isSigned(ResultSetMetaDataTest.ordReqTIME_2), CoreMatchers.equalTo(false));
    }

    @Test
    public void test_isSigned_forTIMESTAMP_4() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.isSigned(ResultSetMetaDataTest.ordReqTIMESTAMP_4), CoreMatchers.equalTo(false));
    }

    @Test
    public void test_isSigned_forINTERVAL_Y() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.isSigned(ResultSetMetaDataTest.ordReqINTERVAL_Y), CoreMatchers.equalTo(true));
    }

    // TODO(DRILL-3253):  Do more types when we have all-types test storage plugin.
    // //////////////////////////////////////////////////////////
    // getColumnDisplaySize(...):
    // JDBC: "Indicates the designated column's normal maximum width in characters.
    // ... the normal maximum number of characters allowed as the width of the
    // designated column"
    // (What exactly is the "normal maximum" number of characters?)
    @Test
    public void test_getColumnDisplaySize_forBOOLEAN() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnDisplaySize(ResultSetMetaDataTest.ordOptBOOLEAN), CoreMatchers.equalTo(1));
    }

    // TODO(DRILL-3355):  Do more types when metadata is available.
    // TODO(DRILL-3253):  Do more types when we have all-types test storage plugin.
    // //////////////////////////////////////////////////////////
    // getColumnLabel(...):
    // JDBC: "Gets the designated column's suggested title for use in printouts
    // and displays. The suggested title is usually specified by the SQL
    // AS clause.  If a SQL AS is not specified, the value returned from
    // getColumnLabel will be the same as the value returned by the
    // getColumnName method."
    @Test
    public void test_getColumnLabel_getsName() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnLabel(ResultSetMetaDataTest.ordOptBOOLEAN), CoreMatchers.equalTo("mdrOptBOOLEAN"));
    }

    // //////////////////////////////////////////////////////////
    // getColumnName(...):
    // JDBC: "Get the designated column's name."
    @Test
    public void test_getColumnName_getsName() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnName(ResultSetMetaDataTest.ordOptBOOLEAN), CoreMatchers.equalTo("mdrOptBOOLEAN"));
    }

    // //////////////////////////////////////////////////////////
    // getSchemaName(...):
    // JDBC: "Get the designated column's table's schema. ... schema name
    // or "" if not applicable"
    // Note: Schema _name_, not schema, of course.
    // (Are result-set tables in a schema?)
    @Test
    public void test_getSchemaName_forViewGetsName() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getSchemaName(ResultSetMetaDataTest.ordOptBOOLEAN), CoreMatchers.anyOf(CoreMatchers.equalTo(DFS_TMP_SCHEMA), CoreMatchers.equalTo("")));
    }

    // //////////////////////////////////////////////////////////
    // getPrecision(...):
    // JDBC: "Get the designated column's specified column size.
    // For numeric data, this is the maximum precision.
    // For character data, this is the length in characters.
    // For datetime datatypes, this is the length in characters of the String
    // representation (assuming the maximum allowed precision of the
    // fractional seconds component).
    // For binary data, this is the length in bytes.
    // For the ROWID datatype, this is the length in bytes.
    // 0 is returned for data types where the column size is not applicable."
    // TODO(DRILL-3355):  Resolve:
    // - Confirm:  This seems to be the same as getColumns's COLUMN_SIZE.
    // - Is numeric "maximum precision" in bits, in digits, or per some radix
    // specified somewhere?
    // - For which unmentioned types is column size applicable or not applicable?
    // E.g., what about interval types?
    @Test
    public void test_getPrecision_forBOOLEAN() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getPrecision(ResultSetMetaDataTest.ordOptBOOLEAN), CoreMatchers.equalTo(0));
    }

    // TODO(DRILL-3355):  Do more types when metadata is available.
    // - Copy in tests for DatabaseMetaData.getColumns(...)'s COLUMN_SIZE (since
    // ResultSetMetaData.getPrecision(...) seems to be defined the same.
    // TODO(DRILL-3253):  Do more types when we have all-types test storage plugin.
    // //////////////////////////////////////////////////////////
    // getScale(...):
    // JDBC: "Gets the designated column's number of digits to right of the
    // decimal point.  0 is returned for data types where the scale is not
    // applicable."
    // (When exactly is scale not applicable?  What about for TIME or INTERVAL?)
    @Test
    public void test_getScale_forBOOLEAN() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getScale(ResultSetMetaDataTest.ordOptBOOLEAN), CoreMatchers.equalTo(0));
    }

    @Test
    public void test_getScale_forINTEGER() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getScale(ResultSetMetaDataTest.ordReqINTEGER), CoreMatchers.equalTo(0));
    }

    // TODO(DRILL-3355):  Do more types when metadata is available.
    // - especially TIME and INTERVAL cases.
    // TODO(DRILL-3253):  Do more types when we have all-types test storage plugin.
    // //////////////////////////////////////////////////////////
    // getTableName(...):
    // JDBC: "Gets the designated column's table name. ... table name or "" if
    // not applicable"
    // (When exactly is this applicable or not applicable?)
    @Test
    public void test_getTableName_forViewGetsName() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getTableName(ResultSetMetaDataTest.ordOptBOOLEAN), CoreMatchers.anyOf(CoreMatchers.equalTo(ResultSetMetaDataTest.VIEW_NAME), CoreMatchers.equalTo("")));
    }

    // //////////////////////////////////////////////////////////
    // getCatalogName(...):
    // JDBC: "Gets the designated column's table's catalog name.  ... the name of
    // the catalog for the table in which the given column appears or "" if not
    // applicable"
    // (What if the result set is not directly from a base table?  Since Drill has
    // has only one catalog ("DRILL") should this return "DRILL" for everything,
    // or only for base tables?)
    @Test
    public void test_getCatalogName_getsCatalogName() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getCatalogName(ResultSetMetaDataTest.ordOptBOOLEAN), CoreMatchers.anyOf(CoreMatchers.equalTo("DRILL"), CoreMatchers.equalTo("")));
    }

    // //////////////////////////////////////////////////////////
    // getColumnType(...):
    // JDBC: "Retrieves the designated column's SQL type.  ... SQL type from
    // java.sql.Types"
    // NOTE:  JDBC representation of data type or data type family.
    @Test
    public void test_getColumnType_forBOOLEAN() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnType(ResultSetMetaDataTest.ordOptBOOLEAN), CoreMatchers.equalTo(Types.BOOLEAN));
    }

    @Test
    public void test_getColumnType_forINTEGER() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnType(ResultSetMetaDataTest.ordReqINTEGER), CoreMatchers.equalTo(Types.INTEGER));
    }

    @Test
    public void test_getColumnType_forBIGINT() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnType(ResultSetMetaDataTest.ordReqBIGINT), CoreMatchers.equalTo(Types.BIGINT));
    }

    @Test
    public void test_getColumnType_forFLOAT() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnType(ResultSetMetaDataTest.ordReqFLOAT), CoreMatchers.equalTo(Types.FLOAT));
    }

    @Test
    public void test_getColumnType_forDOUBLE() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnType(ResultSetMetaDataTest.ordReqDOUBLE), CoreMatchers.equalTo(Types.DOUBLE));
    }

    @Test
    public void test_getColumnType_forVARCHAR_10() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnType(ResultSetMetaDataTest.ordReqVARCHAR_10), CoreMatchers.equalTo(Types.VARCHAR));
    }

    @Test
    public void test_getColumnType_forVARCHAR() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnType(ResultSetMetaDataTest.ordOptVARCHAR), CoreMatchers.equalTo(Types.VARCHAR));
    }

    @Test
    public void test_getColumnType_forDATE() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnType(ResultSetMetaDataTest.ordReqDATE), CoreMatchers.equalTo(Types.DATE));
    }

    @Test
    public void test_getColumnType_forTIME_2() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnType(ResultSetMetaDataTest.ordReqTIME_2), CoreMatchers.equalTo(Types.TIME));
    }

    @Test
    public void test_getColumnType_forTIME_7() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnType(ResultSetMetaDataTest.ordOptTIME_7), CoreMatchers.equalTo(Types.TIME));
    }

    @Test
    public void test_getColumnType_forTIMESTAMP_4() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnType(ResultSetMetaDataTest.ordReqTIMESTAMP_4), CoreMatchers.equalTo(Types.TIMESTAMP));
    }

    @Test
    public void test_getColumnType_forINTERVAL_Y() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnType(ResultSetMetaDataTest.ordReqINTERVAL_Y), CoreMatchers.equalTo(Types.OTHER));
    }

    @Test
    public void test_getColumnType_forINTERVAL_H_S3() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnType(ResultSetMetaDataTest.ordReqINTERVAL_3H_S1), CoreMatchers.equalTo(Types.OTHER));
    }

    // TODO(DRILL-3253):  Do more types when we have all-types test storage plugin.
    // //////////////////////////////////////////////////////////
    // getColumnTypeName(...):
    // JDBC: "Retrieves the designated column's database-specific type name.
    // ... type name used by the database.  If the column type is a user-defined
    // type, then a fully-qualified type name is returned."
    // (Is this expected to match INFORMATION_SCHEMA.COLUMNS.TYPE_NAME?)
    @Test
    public void test_getColumnTypeName_forBOOLEAN() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnTypeName(ResultSetMetaDataTest.ordOptBOOLEAN), CoreMatchers.equalTo("BOOLEAN"));
    }

    @Test
    public void test_getColumnTypeName_forINTEGER() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnTypeName(ResultSetMetaDataTest.ordReqINTEGER), CoreMatchers.equalTo("INTEGER"));
    }

    @Test
    public void test_getColumnTypeName_forBIGINT() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnTypeName(ResultSetMetaDataTest.ordReqBIGINT), CoreMatchers.equalTo("BIGINT"));
    }

    @Test
    public void test_getColumnTypeName_forFLOAT() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnTypeName(ResultSetMetaDataTest.ordReqFLOAT), CoreMatchers.equalTo("FLOAT"));
    }

    @Test
    public void test_getColumnTypeName_forDOUBLE() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnTypeName(ResultSetMetaDataTest.ordReqDOUBLE), CoreMatchers.equalTo("DOUBLE"));
    }

    @Test
    public void test_getColumnTypeName_forVARCHAR() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnTypeName(ResultSetMetaDataTest.ordOptVARCHAR), CoreMatchers.equalTo("CHARACTER VARYING"));
    }

    @Test
    public void test_getColumnTypeName_forDATE() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnTypeName(ResultSetMetaDataTest.ordReqDATE), CoreMatchers.equalTo("DATE"));
    }

    @Test
    public void test_getColumnTypeName_forTIME_2() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnTypeName(ResultSetMetaDataTest.ordReqTIME_2), CoreMatchers.equalTo("TIME"));
    }

    @Test
    public void test_getColumnTypeName_forTIMESTAMP_4() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnTypeName(ResultSetMetaDataTest.ordReqTIMESTAMP_4), CoreMatchers.equalTo("TIMESTAMP"));
    }

    @Test
    public void test_getColumnTypeName_forINTERVAL_Y() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnTypeName(ResultSetMetaDataTest.ordReqINTERVAL_Y), CoreMatchers.equalTo("INTERVAL YEAR TO MONTH"));
    }

    @Test
    public void test_getColumnTypeName_forINTERVAL_D() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnTypeName(ResultSetMetaDataTest.ordReqINTERVAL_4D_H), CoreMatchers.equalTo("INTERVAL DAY TO SECOND"));
    }

    // TODO(DRILL-3253):  Do more types when we have all-types test storage plugin.
    // //////////////////////////////////////////////////////////
    // isReadOnly(...):
    // JDBC: "Indicates whether the designated column is definitely not writable."
    // (Writable in what context?  By current user in current connection? Some
    // other context?)
    @Test
    public void test_isReadOnly_nameThisNonSpecific() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.isReadOnly(ResultSetMetaDataTest.ordOptBOOLEAN), CoreMatchers.equalTo(true));
    }

    // //////////////////////////////////////////////////////////
    // isWritable(...):
    // JDBC: "Indicates whether it is possible for a write on the designated
    // column to succeed."
    // (Possible in what context?  By current user in current connection?  Some
    // other context?
    @Test
    public void test_isWritable_nameThisNonSpecific() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.isWritable(ResultSetMetaDataTest.ordOptBOOLEAN), CoreMatchers.equalTo(false));
    }

    // //////////////////////////////////////////////////////////
    // isDefinitelyWritable(...):
    // JDBC: "Indicates whether a write on the designated column will definitely
    // succeed."
    // (Will succeed in what context?  By current user in current connection?
    // Some other context?)
    @Test
    public void test_isDefinitelyWritable_nameThisNonSpecific() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.isDefinitelyWritable(ResultSetMetaDataTest.ordOptBOOLEAN), CoreMatchers.equalTo(false));
    }

    // //////////////////////////////////////////////////////////
    // getColumnClassName(...):
    // JDBC: "Returns the fully-qualified name of the Java class whose instances
    // are manufactured if the method ResultSet.getObject is called to retrieve
    // a value from the column.  ResultSet.getObject may return a subclass of
    // the class returned by this method. ... the fully-qualified name of the
    // class in the Java programming language that would be used by the method"
    // BOOLEAN:
    @Test
    public void test_getColumnClassName_forBOOLEAN_isBoolean() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnClassName(ResultSetMetaDataTest.ordOptBOOLEAN), CoreMatchers.equalTo(Boolean.class.getName()));
    }

    @Test
    public void test_getColumnClassName_forBOOLEAN_matches() throws SQLException {
        // (equalTo because Boolean is final)
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnClassName(ResultSetMetaDataTest.ordReqBOOLEAN), CoreMatchers.equalTo(ResultSetMetaDataTest.viewRow.getObject(ResultSetMetaDataTest.ordReqBOOLEAN).getClass().getName()));
    }

    // INTEGER:
    @Test
    public void test_getColumnClassName_forINTEGER_isInteger() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnClassName(ResultSetMetaDataTest.ordReqINTEGER), CoreMatchers.equalTo(Integer.class.getName()));
    }

    @Test
    public void test_getColumnClassName_forINTEGER_matches() throws SQLException {
        // (equalTo because Integer is final)
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnClassName(ResultSetMetaDataTest.ordReqINTEGER), CoreMatchers.equalTo(ResultSetMetaDataTest.viewRow.getObject(ResultSetMetaDataTest.ordReqINTEGER).getClass().getName()));
    }

    // BIGINT:
    @Test
    public void test_getColumnClassName_forBIGINT_isLong() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnClassName(ResultSetMetaDataTest.ordReqBIGINT), CoreMatchers.equalTo(Long.class.getName()));
    }

    @Test
    public void test_getColumnClassName_forBIGINT_matches() throws SQLException {
        // (equalTo because Long is final)
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnClassName(ResultSetMetaDataTest.ordReqBIGINT), CoreMatchers.equalTo(ResultSetMetaDataTest.viewRow.getObject(ResultSetMetaDataTest.ordReqBIGINT).getClass().getName()));
    }

    // FLOAT:
    @Test
    public void test_getColumnClassName_forFLOAT_isFloat() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnClassName(ResultSetMetaDataTest.ordReqFLOAT), CoreMatchers.anyOf(CoreMatchers.equalTo(Float.class.getName()), CoreMatchers.equalTo(Double.class.getName())));
    }

    @Test
    public void test_getColumnClassName_forFLOAT_matches() throws SQLException {
        // (equalTo because Float is final)
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnClassName(ResultSetMetaDataTest.ordReqFLOAT), CoreMatchers.equalTo(ResultSetMetaDataTest.viewRow.getObject(ResultSetMetaDataTest.ordReqFLOAT).getClass().getName()));
    }

    // DOUBLE:
    @Test
    public void test_getColumnClassName_forDOUBLE_isDouble() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnClassName(ResultSetMetaDataTest.ordReqDOUBLE), CoreMatchers.equalTo(Double.class.getName()));
    }

    @Test
    public void test_getColumnClassName_forDOUBLE_matches() throws SQLException {
        // (equalTo because Double is final)
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnClassName(ResultSetMetaDataTest.ordReqDOUBLE), CoreMatchers.equalTo(ResultSetMetaDataTest.viewRow.getObject(ResultSetMetaDataTest.ordReqDOUBLE).getClass().getName()));
    }

    @Test
    public void test_getColumnClassName_forDECIMAL_5_3_matches() throws ClassNotFoundException, SQLException {
        final Class<?> requiredClass = Class.forName(ResultSetMetaDataTest.rowMetadata.getColumnClassName(ResultSetMetaDataTest.ordReqDECIMAL_5_3));
        final Class<?> actualClass = ResultSetMetaDataTest.viewRow.getObject(ResultSetMetaDataTest.ordReqDECIMAL_5_3).getClass();
        Assert.assertTrue(((("actual class " + (actualClass.getName())) + " is not assignable to required class ") + requiredClass), requiredClass.isAssignableFrom(actualClass));
    }

    // VARCHAR_10:
    @Test
    public void test_getColumnClassName_forVARCHAR_10_isString() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnClassName(ResultSetMetaDataTest.ordReqVARCHAR_10), CoreMatchers.equalTo(String.class.getName()));
    }

    @Test
    public void test_getColumnClassName_forVARCHAR_10_matches() throws ClassNotFoundException, SQLException {
        final Class<?> requiredClass = Class.forName(ResultSetMetaDataTest.rowMetadata.getColumnClassName(ResultSetMetaDataTest.ordReqVARCHAR_10));
        final Class<?> actualClass = ResultSetMetaDataTest.viewRow.getObject(ResultSetMetaDataTest.ordReqVARCHAR_10).getClass();
        Assert.assertTrue(((("actual class " + (actualClass.getName())) + " is not assignable to required class ") + requiredClass), requiredClass.isAssignableFrom(actualClass));
    }

    // TODO(DRILL-3369):  Add test when CHAR is no longer VARCHAR:
    // CHAR_5:
    // TODO(DRILL-3368):  Add test when VARBINARY is implemented enough:
    // VARBINARY_16
    // TODO(DRILL-3368):  Add test when BINARY is implemented enough:
    // BINARY_1048576:
    // DATE:
    @Test
    public void test_getColumnClassName_forDATE_isDate() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnClassName(ResultSetMetaDataTest.ordReqDATE), CoreMatchers.equalTo(Date.class.getName()));
    }

    @Test
    public void test_getColumnClassName_forDATE_matches() throws ClassNotFoundException, SQLException {
        final Class<?> requiredClass = Class.forName(ResultSetMetaDataTest.rowMetadata.getColumnClassName(ResultSetMetaDataTest.ordReqDATE));
        final Class<?> actualClass = ResultSetMetaDataTest.viewRow.getObject(ResultSetMetaDataTest.ordReqDATE).getClass();
        Assert.assertTrue(((("actual class " + (actualClass.getName())) + " is not assignable to required class ") + requiredClass), requiredClass.isAssignableFrom(actualClass));
    }

    // TIME:
    @Test
    public void test_getColumnClassName_forTIME_2_isTime() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnClassName(ResultSetMetaDataTest.ordReqTIME_2), CoreMatchers.equalTo(Time.class.getName()));
    }

    @Test
    public void test_getColumnClassName_forTIME_2_matches() throws ClassNotFoundException, SQLException {
        final Class<?> requiredClass = Class.forName(ResultSetMetaDataTest.rowMetadata.getColumnClassName(ResultSetMetaDataTest.ordReqTIME_2));
        final Class<?> actualClass = ResultSetMetaDataTest.viewRow.getObject(ResultSetMetaDataTest.ordReqTIME_2).getClass();
        Assert.assertTrue(((("actual class " + (actualClass.getName())) + " is not assignable to required class ") + requiredClass), requiredClass.isAssignableFrom(actualClass));
    }

    // TIME_7:
    // TIMESTAMP:
    @Test
    public void test_getColumnClassName_forTIMESTAMP_4_isDate() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnClassName(ResultSetMetaDataTest.ordReqTIMESTAMP_4), CoreMatchers.equalTo(Timestamp.class.getName()));
    }

    @Test
    public void test_getColumnClassName_forTIMESTAMP_4_matches() throws ClassNotFoundException, SQLException {
        final Class<?> requiredClass = Class.forName(ResultSetMetaDataTest.rowMetadata.getColumnClassName(ResultSetMetaDataTest.ordReqTIMESTAMP_4));
        final Class<?> actualClass = ResultSetMetaDataTest.viewRow.getObject(ResultSetMetaDataTest.ordReqTIMESTAMP_4).getClass();
        Assert.assertTrue(((("actual class " + (actualClass.getName())) + " is not assignable to required class ") + requiredClass), requiredClass.isAssignableFrom(actualClass));
    }

    // No "... WITH TIME ZONE" in Drill.
    // INTERVAL_Y:
    // INTERVAL_3Y_Mo:
    // INTERVAL_10Y_Mo:
    @Test
    public void test_getColumnClassName_forINTERVAL_10Y_Mo_isJodaPeriod() throws SQLException {
        Assert.assertThat(ResultSetMetaDataTest.rowMetadata.getColumnClassName(ResultSetMetaDataTest.ordReqINTERVAL_10Y_Mo), CoreMatchers.equalTo(Period.class.getName()));
    }

    @Test
    public void test_getColumnClassName_forINTERVAL_10Y_Mo_matches() throws ClassNotFoundException, SQLException {
        final Class<?> requiredClass = Class.forName(ResultSetMetaDataTest.rowMetadata.getColumnClassName(ResultSetMetaDataTest.ordReqINTERVAL_10Y_Mo));
        final Class<?> actualClass = ResultSetMetaDataTest.viewRow.getObject(ResultSetMetaDataTest.ordReqINTERVAL_10Y_Mo).getClass();
        Assert.assertTrue(((("actual class " + (actualClass.getName())) + " is not assignable to required class ") + requiredClass), requiredClass.isAssignableFrom(actualClass));
    }
}

