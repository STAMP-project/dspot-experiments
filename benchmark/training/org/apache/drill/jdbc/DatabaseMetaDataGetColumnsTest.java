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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import org.apache.drill.categories.JdbcTest;
import org.apache.drill.common.types.Types.MAX_VARCHAR_LENGTH;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


// NOTE: TestInformationSchemaColumns and DatabaseMetaDataGetColumnsTest
// have identical sections.  (Cross-maintain them for now; factor out later.)
// TODO:  MOVE notes to implementation (have this not (just) in test).
// TODO:  Determine for each numeric type whether its precision is reported in
// decimal or binary (and NUM_PREC_RADIX is 10 or 2, respectively).
// The SQL specification for INFORMATION_SCHEMA.COLUMNS seems to specify the
// radix for each numeric type:
// - 2 or 10 for SMALLINT, INTEGER, and BIGINT;
// - only 10 for NUMERIC and DECIMAL; and
// - only 2  for REAL, FLOAT, and DOUBLE PRECISION.
// However, it is not clear what the JDBC API intends:
// - It has NUM_PREC_RADIX, specifying a radix or 10 or 2, but doesn't specify
// exactly what is applies to.  Apparently, it applies to COLUMN_SIZE abd
// ResultMetaData.getPrecision() (which are defined in terms of maximum
// precision for numeric types).
// - Is has DECIMAL_DIGITS, which is <em>not</em> the number of decimal digits
// of precision, but which it defines as the number of fractional digits--
// without actually specifying that it's in decimal.
// TODO:  Review nullability (NULLABLE and IS_NULLABLE columns):
// - It's not clear what JDBC's requirements are.
// - It does seem obvious that metadata should not contradictorily say that a
// - column cannot contain nulls when the column currently does contain nulls.
// - It's not clear whether metadata must say that a column cannot contains
// nulls if JDBC specifies that the column always has a non-null value.
// - It's not clear why Drill reports that columns that will never contain nulls
// can contain nulls.
// - It's not clear why Drill sets INFORMATION_SCHEMA.COLUMNS.IS_NULLABLE to
// 'NO' for some columns that contain only null (e.g., for
// "CREATE VIEW x AS SELECT CAST(NULL AS ...) ..."
/**
 * Test class for Drill's java.sql.DatabaseMetaData.getColumns() implementation.
 * <p>
 *   Based on JDBC 4.1 (Java 7).
 * </p>
 */
@Category(JdbcTest.class)
public class DatabaseMetaDataGetColumnsTest extends JdbcTestBase {
    private static final String VIEW_NAME = (DatabaseMetaDataGetColumnsTest.class.getSimpleName()) + "_View";

    /**
     * The one shared JDBC connection to Drill.
     */
    protected static Connection connection;

    /**
     * Overall (connection-level) metadata.
     */
    protected static DatabaseMetaData dbMetadata;

    /**
     * getColumns result metadata.  For checking columns themselves (not cell
     *  values or row order).
     */
    protected static ResultSetMetaData rowsMetadata;

    // //////////////////
    // Results from getColumns for test columns of various types.
    // Each ResultSet is positioned at first row for, and must not be modified by,
    // test methods.
    // ////////
    // For columns in temporary test view (types accessible via casting):
    private static ResultSet mdrOptBOOLEAN;

    // TODO(DRILL-2470): re-enable TINYINT, SMALLINT, and REAL.
    private static ResultSet mdrReqTINYINT;

    private static ResultSet mdrOptSMALLINT;

    private static ResultSet mdrReqINTEGER;

    private static ResultSet mdrOptBIGINT;

    // TODO(DRILL-2470): re-enable TINYINT, SMALLINT, and REAL.
    private static ResultSet mdrOptREAL;

    private static ResultSet mdrOptFLOAT;

    private static ResultSet mdrReqDOUBLE;

    private static ResultSet mdrReqDECIMAL_5_3;

    // No NUMERIC while Drill just maps it to DECIMAL.
    private static ResultSet mdrReqVARCHAR_10;

    private static ResultSet mdrOptVARCHAR;

    private static ResultSet mdrReqCHAR_5;

    // No NCHAR, etc., in Drill (?).
    private static ResultSet mdrOptVARBINARY_16;

    private static ResultSet mdrOptBINARY_65536;

    private static ResultSet mdrReqDATE;

    private static ResultSet mdrReqTIME;

    private static ResultSet mdrOptTIME_7;

    private static ResultSet mdrOptTIMESTAMP;

    // No "... WITH TIME ZONE" in Drill.
    private static ResultSet mdrReqINTERVAL_Y;

    private static ResultSet mdrReqINTERVAL_3Y_Mo;

    private static ResultSet mdrReqINTERVAL_Mo;

    private static ResultSet mdrReqINTERVAL_D;

    private static ResultSet mdrReqINTERVAL_4D_H;

    private static ResultSet mdrReqINTERVAL_3D_Mi;

    private static ResultSet mdrReqINTERVAL_2D_S5;

    private static ResultSet mdrReqINTERVAL_H;

    private static ResultSet mdrReqINTERVAL_1H_Mi;

    private static ResultSet mdrReqINTERVAL_3H_S1;

    private static ResultSet mdrReqINTERVAL_Mi;

    private static ResultSet mdrReqINTERVAL_5Mi_S;

    private static ResultSet mdrReqINTERVAL_S;

    private static ResultSet mdrReqINTERVAL_3S;

    private static ResultSet mdrReqINTERVAL_3S1;

    // For columns in schema hive_test.default's infoschematest table:
    // listtype column:      VARCHAR(65535) ARRAY, non-null(?):
    private static ResultSet mdrReqARRAY;

    // maptype column:       (VARCHAR(65535), INTEGER) MAP, non-null(?):
    private static ResultSet mdrReqMAP;

    // structtype column:    STRUCT(INTEGER sint, BOOLEAN sboolean,
    // VARCHAR(65535) sstring), non-null(?):
    private static ResultSet mdrUnkSTRUCT;

    // uniontypetype column: OTHER (?), non=nullable(?):
    private static ResultSet mdrUnkUnion;

    // ////////////////////////////////////////////////////////////////////
    // Tests:
    // //////////////////////////////////////////////////////////
    // Number of columns.
    @Test
    public void testMetadataHasRightNumberOfColumns() throws SQLException {
        // TODO:  Review:  Is this check valid?  (Are extra columns allowed?)
        Assert.assertThat("column count", DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnCount(), CoreMatchers.equalTo(24));
    }

    // //////////////////////////////////////////////////////////
    // #1: TABLE_CAT:
    // - JDBC:   "1. ... String => table catalog (may be null)"
    // - Drill:  Apparently chooses always "DRILL".
    // - (Meta): VARCHAR (NVARCHAR?); Non-nullable(?);
    @Test
    public void test_TABLE_CAT_isAtRightPosition() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnLabel(1), CoreMatchers.equalTo("TABLE_CAT"));
    }

    @Test
    public void test_TABLE_CAT_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrOptBOOLEAN.getString("TABLE_CAT"), CoreMatchers.equalTo("DRILL"));
    }

    // Not bothering with other test columns for TABLE_CAT.
    @Test
    public void test_TABLE_CAT_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnName(1), CoreMatchers.equalTo("TABLE_CAT"));
    }

    @Test
    public void test_TABLE_CAT_hasRightTypeString() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnTypeName(1), CoreMatchers.equalTo("CHARACTER VARYING"));
    }

    @Test
    public void test_TABLE_CAT_hasRightTypeCode() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnType(1), CoreMatchers.equalTo(Types.VARCHAR));
    }

    @Test
    public void test_TABLE_CAT_hasRightClass() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnClassName(1), CoreMatchers.equalTo(String.class.getName()));
    }

    // //////////////////////////////////////////////////////////
    // #2: TABLE_SCHEM:
    // - JDBC:   "2. ... String => table schema (may be null)"
    // - Drill:  Always reports a schema name.
    // - (Meta): VARCHAR (NVARCHAR?); Nullable?;
    @Test
    public void test_TABLE_SCHEM_isAtRightPosition() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnLabel(2), CoreMatchers.equalTo("TABLE_SCHEM"));
    }

    @Test
    public void test_TABLE_SCHEM_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrOptBOOLEAN.getString("TABLE_SCHEM"), CoreMatchers.equalTo(DFS_TMP_SCHEMA));
    }

    // Not bothering with other Hive test columns for TABLE_SCHEM.
    @Test
    public void test_TABLE_SCHEM_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnName(2), CoreMatchers.equalTo("TABLE_SCHEM"));
    }

    @Test
    public void test_TABLE_SCHEM_hasRightTypeString() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnTypeName(2), CoreMatchers.equalTo("CHARACTER VARYING"));
    }

    @Test
    public void test_TABLE_SCHEM_hasRightTypeCode() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnType(2), CoreMatchers.equalTo(Types.VARCHAR));
    }

    @Test
    public void test_TABLE_SCHEM_hasRightClass() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnClassName(2), CoreMatchers.equalTo(String.class.getName()));
    }

    // //////////////////////////////////////////////////////////
    // #3: TABLE_NAME:
    // - JDBC:  "3. ... String => table name"
    // - Drill:
    // - (Meta): VARCHAR (NVARCHAR?); Non-nullable?;
    @Test
    public void test_TABLE_NAME_isAtRightPosition() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnLabel(3), CoreMatchers.equalTo("TABLE_NAME"));
    }

    @Test
    public void test_TABLE_NAME_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrOptBOOLEAN.getString("TABLE_NAME"), CoreMatchers.equalTo(DatabaseMetaDataGetColumnsTest.VIEW_NAME));
    }

    // Not bothering with other _local_view_ test columns for TABLE_NAME.
    @Test
    public void test_TABLE_NAME_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnName(3), CoreMatchers.equalTo("TABLE_NAME"));
    }

    @Test
    public void test_TABLE_NAME_hasRightTypeString() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnTypeName(3), CoreMatchers.equalTo("CHARACTER VARYING"));
    }

    @Test
    public void test_TABLE_NAME_hasRightTypeCode() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnType(3), CoreMatchers.equalTo(Types.VARCHAR));
    }

    @Test
    public void test_TABLE_NAME_hasRightClass() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnClassName(3), CoreMatchers.equalTo(String.class.getName()));
    }

    // //////////////////////////////////////////////////////////
    // #4: COLUMN_NAME:
    // - JDBC:  "4. ... String => column name"
    // - Drill:
    // - (Meta): VARCHAR (NVARCHAR?); Non-nullable(?);
    @Test
    public void test_COLUMN_NAME_isAtRightPosition() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnLabel(4), CoreMatchers.equalTo("COLUMN_NAME"));
    }

    @Test
    public void test_COLUMN_NAME_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrOptBOOLEAN.getString("COLUMN_NAME"), CoreMatchers.equalTo("mdrOptBOOLEAN"));
    }

    // Not bothering with other Hive test columns for TABLE_SCHEM.
    @Test
    public void test_COLUMN_NAME_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnName(4), CoreMatchers.equalTo("COLUMN_NAME"));
    }

    @Test
    public void test_COLUMN_NAME_hasRightTypeString() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnTypeName(4), CoreMatchers.equalTo("CHARACTER VARYING"));
    }

    @Test
    public void test_COLUMN_NAME_hasRightTypeCode() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnType(4), CoreMatchers.equalTo(Types.VARCHAR));
    }

    @Test
    public void test_COLUMN_NAME_hasRightClass() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnClassName(4), CoreMatchers.equalTo(String.class.getName()));
    }

    // //////////////////////////////////////////////////////////
    // #5: DATA_TYPE:
    // - JDBC:  "5. ... int => SQL type from java.sql.Types"
    // - Drill:
    // - (Meta): INTEGER(?);  Non-nullable(?);
    @Test
    public void test_DATA_TYPE_isAtRightPosition() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnLabel(5), CoreMatchers.equalTo("DATA_TYPE"));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptBOOLEAN, "DATA_TYPE"), CoreMatchers.equalTo(Types.BOOLEAN));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrReqINTEGER() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTEGER, "DATA_TYPE"), CoreMatchers.equalTo(Types.INTEGER));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrOptBIGINT() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptBIGINT, "DATA_TYPE"), CoreMatchers.equalTo(Types.BIGINT));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrOptFLOAT() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptFLOAT, "DATA_TYPE"), CoreMatchers.equalTo(Types.FLOAT));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrReqDOUBLE() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqDOUBLE, "DATA_TYPE"), CoreMatchers.equalTo(Types.DOUBLE));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrReqDECIMAL_5_3() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqDECIMAL_5_3, "DATA_TYPE"), CoreMatchers.equalTo(Types.DECIMAL));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrReqVARCHAR_10() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqVARCHAR_10, "DATA_TYPE"), CoreMatchers.equalTo(Types.VARCHAR));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrOptVARCHAR() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptVARCHAR, "DATA_TYPE"), CoreMatchers.equalTo(Types.VARCHAR));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrReqCHAR_5() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqCHAR_5, "DATA_TYPE"), CoreMatchers.equalTo(Types.CHAR));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrOptVARBINARY_16() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptVARBINARY_16, "DATA_TYPE"), CoreMatchers.equalTo(Types.VARBINARY));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrOptBINARY_1048576CHECK() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptBINARY_65536, "DATA_TYPE"), CoreMatchers.equalTo(Types.VARBINARY));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrReqDATE() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqDATE, "DATA_TYPE"), CoreMatchers.equalTo(Types.DATE));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrReqTIME() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqTIME, "DATA_TYPE"), CoreMatchers.equalTo(Types.TIME));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrOptTIME_7() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptTIME_7, "DATA_TYPE"), CoreMatchers.equalTo(Types.TIME));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrOptTIMESTAMP() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptTIMESTAMP, "DATA_TYPE"), CoreMatchers.equalTo(Types.TIMESTAMP));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrReqINTERVAL_Y() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_Y, "DATA_TYPE"), CoreMatchers.equalTo(Types.OTHER));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrReqINTERVAL_H_S3() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_3H_S1, "DATA_TYPE"), CoreMatchers.equalTo(Types.OTHER));
    }

    @Test
    public void test_DATA_TYPE_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnName(5), CoreMatchers.equalTo("DATA_TYPE"));
    }

    @Test
    public void test_DATA_TYPE_hasRightTypeString() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnTypeName(5), CoreMatchers.equalTo("INTEGER"));
    }

    @Test
    public void test_DATA_TYPE_hasRightTypeCode() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnType(5), CoreMatchers.equalTo(Types.INTEGER));
    }

    @Test
    public void test_DATA_TYPE_hasRightClass() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnClassName(5), CoreMatchers.equalTo(Integer.class.getName()));
    }

    @Test
    public void test_DATA_TYPE_hasRightNullability() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", DatabaseMetaDataGetColumnsTest.rowsMetadata.isNullable(5), CoreMatchers.equalTo(ResultSetMetaData.columnNoNulls));
    }

    // //////////////////////////////////////////////////////////
    // #6: TYPE_NAME:
    // - JDBC:  "6. ... String => Data source dependent type name, for a UDT the
    // type name is fully qualified"
    // - Drill:
    // - (Meta): VARCHAR (NVARCHAR?); Non-nullable?;
    @Test
    public void test_TYPE_NAME_isAtRightPosition() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnLabel(6), CoreMatchers.equalTo("TYPE_NAME"));
    }

    @Test
    public void test_TYPE_NAME_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrOptBOOLEAN.getString("TYPE_NAME"), CoreMatchers.equalTo("BOOLEAN"));
    }

    @Test
    public void test_TYPE_NAME_hasRightValue_mdrReqINTEGER() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrReqINTEGER.getString("TYPE_NAME"), CoreMatchers.equalTo("INTEGER"));
    }

    @Test
    public void test_TYPE_NAME_hasRightValue_mdrOptBIGINT() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrOptBIGINT.getString("TYPE_NAME"), CoreMatchers.equalTo("BIGINT"));
    }

    @Test
    public void test_TYPE_NAME_hasRightValue_mdrOptFLOAT() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrOptFLOAT.getString("TYPE_NAME"), CoreMatchers.equalTo("FLOAT"));
    }

    @Test
    public void test_TYPE_NAME_hasRightValue_mdrReqDOUBLE() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrReqDOUBLE.getString("TYPE_NAME"), CoreMatchers.equalTo("DOUBLE"));
    }

    @Test
    public void test_TYPE_NAME_hasRightValue_mdrReqDECIMAL_5_3() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrReqDECIMAL_5_3.getString("TYPE_NAME"), CoreMatchers.equalTo("DECIMAL"));
    }

    @Test
    public void test_TYPE_NAME_hasRightValue_mdrReqVARCHAR_10() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrReqVARCHAR_10.getString("TYPE_NAME"), CoreMatchers.equalTo("CHARACTER VARYING"));
    }

    @Test
    public void test_TYPE_NAME_hasRightValue_mdrOptVARCHAR() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrOptVARCHAR.getString("TYPE_NAME"), CoreMatchers.equalTo("CHARACTER VARYING"));
    }

    @Test
    public void test_TYPE_NAME_hasRightValue_mdrReqCHAR_5() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrReqCHAR_5.getString("TYPE_NAME"), CoreMatchers.equalTo("CHARACTER"));
    }

    @Test
    public void test_TYPE_NAME_hasRightValue_mdrOptVARBINARY_16() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrOptVARBINARY_16.getString("TYPE_NAME"), CoreMatchers.equalTo("BINARY VARYING"));
    }

    @Test
    public void test_TYPE_NAME_hasRightValue_mdrOptBINARY_1048576CHECK() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrOptBINARY_65536.getString("TYPE_NAME"), CoreMatchers.equalTo("BINARY VARYING"));
    }

    @Test
    public void test_TYPE_NAME_hasRightValue_mdrReqDATE() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrReqDATE.getString("TYPE_NAME"), CoreMatchers.equalTo("DATE"));
    }

    @Test
    public void test_TYPE_NAME_hasRightValue_mdrReqTIME() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrReqTIME.getString("TYPE_NAME"), CoreMatchers.equalTo("TIME"));
    }

    @Test
    public void test_TYPE_NAME_hasRightValue_mdrOptTIME_7() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrOptTIME_7.getString("TYPE_NAME"), CoreMatchers.equalTo("TIME"));
    }

    @Test
    public void test_TYPE_NAME_hasRightValue_mdrOptTIMESTAMP() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrOptTIMESTAMP.getString("TYPE_NAME"), CoreMatchers.equalTo("TIMESTAMP"));
    }

    @Test
    public void test_TYPE_NAME_hasRightValue_mdrReqINTERVAL_Y() throws SQLException {
        // (What SQL standard specifies for DATA_TYPE in INFORMATION_SCHEMA.COLUMNS:)
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_Y.getString("TYPE_NAME"), CoreMatchers.equalTo("INTERVAL"));
    }

    @Test
    public void test_TYPE_NAME_hasRightValue_mdrReqINTERVAL_H_S3() throws SQLException {
        // (What SQL standard specifies for DATA_TYPE in INFORMATION_SCHEMA.COLUMNS:)
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_3H_S1.getString("TYPE_NAME"), CoreMatchers.equalTo("INTERVAL"));
    }

    @Test
    public void test_TYPE_NAME_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnName(6), CoreMatchers.equalTo("TYPE_NAME"));
    }

    @Test
    public void test_TYPE_NAME_hasRightTypeString() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnTypeName(6), CoreMatchers.equalTo("CHARACTER VARYING"));
    }

    @Test
    public void test_TYPE_NAME_hasRightTypeCode() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnType(6), CoreMatchers.equalTo(Types.VARCHAR));
    }

    @Test
    public void test_TYPE_NAME_hasRightClass() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnClassName(6), CoreMatchers.equalTo(String.class.getName()));
    }

    // //////////////////////////////////////////////////////////
    // #7: COLUMN_SIZE:
    // - JDBC:  "7. ... int => column size."
    // "The COLUMN_SIZE column specifies the column size for the given column.
    // For numeric data, this is the maximum precision.
    // For character data, this is the length in characters.
    // For datetime datatypes, this is the length in characters of the String
    // representation (assuming the maximum allowed precision of the
    // fractional seconds component).
    // For binary data, this is the length in bytes.
    // For the ROWID datatype, this is the length in bytes.
    // Null is returned for data types where the column size is not applicable."
    // - "Maximum precision" seem to mean maximum number of digits that can
    // appear.
    // - (Meta): INTEGER(?); Nullable;
    @Test
    public void test_COLUMN_SIZE_isAtRightPosition() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnLabel(7), CoreMatchers.equalTo("COLUMN_SIZE"));
    }

    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptBOOLEAN, "COLUMN_SIZE"), CoreMatchers.equalTo(1));
    }

    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrReqINTEGER() throws SQLException {
        // 32 bits
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTEGER, "COLUMN_SIZE"), CoreMatchers.equalTo(32));
    }

    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrOptBIGINT() throws SQLException {
        // 64 bits
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptBIGINT, "COLUMN_SIZE"), CoreMatchers.equalTo(64));
    }

    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrOptFLOAT() throws SQLException {
        // 24 bits of precision (same as REAL--current Drill behavior)
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptFLOAT, "COLUMN_SIZE"), CoreMatchers.equalTo(24));
    }

    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrReqDOUBLE() throws SQLException {
        // 53 bits of precision
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqDOUBLE, "COLUMN_SIZE"), CoreMatchers.equalTo(53));
    }

    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrReqDECIMAL_5_3() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqDECIMAL_5_3, "COLUMN_SIZE"), CoreMatchers.equalTo(5));
    }

    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrReqVARCHAR_10() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqVARCHAR_10, "COLUMN_SIZE"), CoreMatchers.equalTo(10));
    }

    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrOptVARCHAR() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptVARCHAR, "COLUMN_SIZE"), CoreMatchers.equalTo(MAX_VARCHAR_LENGTH));
    }

    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrReqCHAR_5() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqCHAR_5, "COLUMN_SIZE"), CoreMatchers.equalTo(5));
    }

    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrOptVARBINARY_16() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptVARBINARY_16, "COLUMN_SIZE"), CoreMatchers.equalTo(16));
    }

    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrOptBINARY_1048576() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptBINARY_65536, "COLUMN_SIZE"), CoreMatchers.equalTo(65536));
    }

    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrReqDATE() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqDATE, "COLUMN_SIZE"), CoreMatchers.equalTo(10));
    }

    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrReqTIME() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqTIME, "COLUMN_SIZE"), /* HH:MM:SS */
        CoreMatchers.equalTo(8));
    }

    @Test
    public void test_COLUMN_SIZE_hasINTERIMValue_mdrOptTIME_7() throws SQLException {
        Assert.assertThat("When datetime precision is implemented, un-ignore above method and purge this.", getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptTIME_7, "COLUMN_SIZE"), /* HH:MM:SS */
        CoreMatchers.equalTo(8));
    }

    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrOptTIMESTAMP() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptTIMESTAMP, "COLUMN_SIZE"), /* YYYY-MM-DDTHH:MM:SS */
        CoreMatchers.equalTo(19));
    }

    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrReqINTERVAL_Y() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_Y, "COLUMN_SIZE"), CoreMatchers.equalTo(4));// "P12Y"

    }

    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrReqINTERVAL_3Y_Mo() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_3Y_Mo, "COLUMN_SIZE"), CoreMatchers.equalTo(8));// "P123Y12M"

    }

    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrReqINTERVAL_Mo() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_Mo, "COLUMN_SIZE"), CoreMatchers.equalTo(4));// "P12M"

    }

    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrReqINTERVAL_D() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_D, "COLUMN_SIZE"), CoreMatchers.equalTo(4));// "P12D"

    }

    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrReqINTERVAL_4D_H() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_4D_H, "COLUMN_SIZE"), CoreMatchers.equalTo(10));// "P1234DT12H"

    }

    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrReqINTERVAL_3D_Mi() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_3D_Mi, "COLUMN_SIZE"), CoreMatchers.equalTo(12));// "P123DT12H12M"

    }

    // Fixed with Calcite update
    // @Ignore( "TODO(DRILL-3244): unignore when fractional secs. prec. is right" )
    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrReqINTERVAL_2D_S5() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_2D_S5, "COLUMN_SIZE"), CoreMatchers.equalTo(20));// "P12DT12H12M12.12345S"

    }

    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrReqINTERVAL_3H() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_H, "COLUMN_SIZE"), CoreMatchers.equalTo(5));// "PT12H"

    }

    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrReqINTERVAL_4H_Mi() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_1H_Mi, "COLUMN_SIZE"), CoreMatchers.equalTo(7));// "PT1H12M"

    }

    // Fixed with Calcite update
    // @Ignore( "TODO(DRILL-3244): unignore when fractional secs. prec. is right" )
    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrReqINTERVAL_3H_S1() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_3H_S1, "COLUMN_SIZE"), CoreMatchers.equalTo(14));// "PT123H12M12.1S"

    }

    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrReqINTERVAL_Mi() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_Mi, "COLUMN_SIZE"), CoreMatchers.equalTo(5));// "PT12M"

    }

    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrReqINTERVAL_5Mi_S() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_5Mi_S, "COLUMN_SIZE"), CoreMatchers.equalTo(18));// "PT12345M12.123456S"

    }

    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrReqINTERVAL_S() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_S, "COLUMN_SIZE"), CoreMatchers.equalTo(12));// "PT12.123456S"

    }

    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrReqINTERVAL_3S() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_3S, "COLUMN_SIZE"), CoreMatchers.equalTo(13));// "PT123.123456S"

    }

    // Fixed with Calcite update
    // @Ignore( "TODO(DRILL-3244): unignore when fractional secs. prec. is right" )
    @Test
    public void test_COLUMN_SIZE_hasRightValue_mdrReqINTERVAL_3S1() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_3S1, "COLUMN_SIZE"), CoreMatchers.equalTo(8));// "PT123.1S"

    }

    @Test
    public void test_COLUMN_SIZE_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnName(7), CoreMatchers.equalTo("COLUMN_SIZE"));
    }

    @Test
    public void test_COLUMN_SIZE_hasRightTypeString() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnTypeName(7), CoreMatchers.equalTo("INTEGER"));
    }

    @Test
    public void test_COLUMN_SIZE_hasRightTypeCode() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnType(7), CoreMatchers.equalTo(Types.INTEGER));
    }

    @Test
    public void test_COLUMN_SIZE_hasRightClass() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnClassName(7), CoreMatchers.equalTo(Integer.class.getName()));
    }

    @Test
    public void test_COLUMN_SIZE_hasRightNullability() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", DatabaseMetaDataGetColumnsTest.rowsMetadata.isNullable(7), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }

    // //////////////////////////////////////////////////////////
    // #8: BUFFER_LENGTH:
    // - JDBC:   "8. ... is not used"
    // - Drill:
    // - (Meta):
    // Since "unused," check only certain meta-metadata.
    @Test
    public void test_BUFFER_LENGTH_isAtRightPosition() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnLabel(8), CoreMatchers.equalTo("BUFFER_LENGTH"));
    }

    // No specific value or even type to check for.
    @Test
    public void test_BUFFER_LENGTH_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnName(8), CoreMatchers.equalTo("BUFFER_LENGTH"));
    }

    // //////////////////////////////////////////////////////////
    // #9: DECIMAL_DIGITS:
    // - JDBC:  "9. ... int => the number of fractional digits. Null is
    // returned for data types where DECIMAL_DIGITS is not applicable."
    // - Resolve:  When exactly null?
    // - Drill:
    // - (Meta):  INTEGER(?); Nullable;
    @Test
    public void test_DECIMAL_DIGITS_isAtRightPosition() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnLabel(9), CoreMatchers.equalTo("DECIMAL_DIGITS"));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptBOOLEAN, "DECIMAL_DIGITS"), CoreMatchers.nullValue());
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrReqINTEGER() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTEGER, "DECIMAL_DIGITS"), CoreMatchers.equalTo(0));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrOptBIGINT() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptBIGINT, "DECIMAL_DIGITS"), CoreMatchers.equalTo(0));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrOptFLOAT() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptFLOAT, "DECIMAL_DIGITS"), CoreMatchers.equalTo(7));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrReqDOUBLE() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqDOUBLE, "DECIMAL_DIGITS"), CoreMatchers.equalTo(15));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrReqDECIMAL_5_3() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqDECIMAL_5_3, "DECIMAL_DIGITS"), CoreMatchers.equalTo(3));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrReqVARCHAR_10() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqVARCHAR_10, "DECIMAL_DIGITS"), CoreMatchers.nullValue());
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrOptVARCHAR() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptVARCHAR, "DECIMAL_DIGITS"), CoreMatchers.nullValue());
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrReqCHAR_5() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqCHAR_5, "DECIMAL_DIGITS"), CoreMatchers.nullValue());
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrOptVARBINARY_16() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptVARBINARY_16, "DECIMAL_DIGITS"), CoreMatchers.nullValue());
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrOptBINARY_1048576CHECK() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptBINARY_65536, "DECIMAL_DIGITS"), CoreMatchers.nullValue());
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrReqDATE() throws SQLException {
        // Zero because, per SQL spec.,  DATE doesn't (seem to) have a datetime
        // precision, but its DATETIME_PRECISION value must not be null.
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqDATE, "DECIMAL_DIGITS"), CoreMatchers.equalTo(0));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrReqTIME() throws SQLException {
        // Zero is default datetime precision for TIME in SQL DATETIME_PRECISION.
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqTIME, "DECIMAL_DIGITS"), CoreMatchers.equalTo(0));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasINTERIMValue_mdrOptTIME_7() throws SQLException {
        Assert.assertThat("When datetime precision is implemented, un-ignore above method and purge this.", getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptTIME_7, "DECIMAL_DIGITS"), CoreMatchers.equalTo(0));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrReqINTERVAL_Y() throws SQLException {
        Assert.assertThat("When datetime precision is implemented, un-ignore above method and purge this.", getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_Y, "DECIMAL_DIGITS"), CoreMatchers.equalTo(0));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrReqINTERVAL_3Y_Mo() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_3Y_Mo, "DECIMAL_DIGITS"), CoreMatchers.equalTo(0));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrReqINTERVAL_Mo() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_Mo, "DECIMAL_DIGITS"), CoreMatchers.equalTo(0));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrReqINTERVAL_D() throws SQLException {
        // 6 seems to be Drill's (Calcite's) choice (to use default value for the
        // fractional seconds precision for when SECOND _is_ present) since the SQL
        // spec. (ISO/IEC 9075-2:2011(E) 10.1 <interval qualifier>) doesn't seem to
        // specify the fractional seconds precision when SECOND is _not_ present.
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_D, "DECIMAL_DIGITS"), CoreMatchers.equalTo(6));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrReqINTERVAL_4D_H() throws SQLException {
        // 6 seems to be Drill's (Calcite's) choice (to use default value for the
        // fractional seconds precision for when SECOND _is_ present) since the SQL
        // spec. (ISO/IEC 9075-2:2011(E) 10.1 <interval qualifier>) doesn't seem to
        // specify the fractional seconds precision when SECOND is _not_ present.
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_4D_H, "DECIMAL_DIGITS"), CoreMatchers.equalTo(6));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrReqINTERVAL_3D_Mi() throws SQLException {
        // 6 seems to be Drill's (Calcite's) choice (to use default value for the
        // fractional seconds precision for when SECOND _is_ present) since the SQL
        // spec. (ISO/IEC 9075-2:2011(E) 10.1 <interval qualifier>) doesn't seem to
        // specify the fractional seconds precision when SECOND is _not_ present.
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_3D_Mi, "DECIMAL_DIGITS"), CoreMatchers.equalTo(6));
    }

    // Fixed with Calcite update
    // @Ignore( "TODO(DRILL-3244): unignore when fractional secs. prec. is right" )
    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrReqINTERVAL_2D_S5() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_2D_S5, "DECIMAL_DIGITS"), CoreMatchers.equalTo(5));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrReqINTERVAL_3H() throws SQLException {
        // 6 seems to be Drill's (Calcite's) choice (to use default value for the
        // fractional seconds precision for when SECOND _is_ present) since the SQL
        // spec. (ISO/IEC 9075-2:2011(E) 10.1 <interval qualifier>) doesn't seem to
        // specify the fractional seconds precision when SECOND is _not_ present.
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_H, "DECIMAL_DIGITS"), CoreMatchers.equalTo(6));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrReqINTERVAL_1H_Mi() throws SQLException {
        // 6 seems to be Drill's (Calcite's) choice (to use default value for the
        // fractional seconds precision for when SECOND _is_ present) since the SQL
        // spec. (ISO/IEC 9075-2:2011(E) 10.1 <interval qualifier>) doesn't seem to
        // specify the fractional seconds precision when SECOND is _not_ present.
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_1H_Mi, "DECIMAL_DIGITS"), CoreMatchers.equalTo(6));
    }

    // Fixed with Calcite update
    // @Ignore( "TODO(DRILL-3244): unignore when fractional secs. prec. is right" )
    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrReqINTERVAL_3H_S1() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_3H_S1, "DECIMAL_DIGITS"), CoreMatchers.equalTo(1));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrReqINTERVAL_Mi() throws SQLException {
        // 6 seems to be Drill's (Calcite's) choice (to use default value for the
        // fractional seconds precision for when SECOND _is_ present) since the SQL
        // spec. (ISO/IEC 9075-2:2011(E) 10.1 <interval qualifier>) doesn't seem to
        // specify the fractional seconds precision when SECOND is _not_ present.
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_Mi, "DECIMAL_DIGITS"), CoreMatchers.equalTo(6));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrReqINTERVAL_5Mi_S() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_5Mi_S, "DECIMAL_DIGITS"), CoreMatchers.equalTo(6));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrReqINTERVAL_S() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_S, "DECIMAL_DIGITS"), CoreMatchers.equalTo(6));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightValue_mdrReqINTERVAL_3S() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_3S, "DECIMAL_DIGITS"), CoreMatchers.equalTo(6));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasINTERIMValue_mdrReqINTERVAL_3S() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_3S, "DECIMAL_DIGITS"), CoreMatchers.equalTo(6));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasINTERIMValue_mdrReqINTERVAL_3S1() throws SQLException {
        Assert.assertThat("When DRILL-3244 fixed, un-ignore above method and purge this.", getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_3S, "DECIMAL_DIGITS"), CoreMatchers.equalTo(6));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnName(9), CoreMatchers.equalTo("DECIMAL_DIGITS"));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightTypeString() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnTypeName(9), CoreMatchers.equalTo("INTEGER"));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightTypeCode() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnType(9), CoreMatchers.equalTo(Types.INTEGER));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightClass() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnClassName(9), CoreMatchers.equalTo(Integer.class.getName()));
    }

    @Test
    public void test_DECIMAL_DIGITS_hasRightNullability() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", DatabaseMetaDataGetColumnsTest.rowsMetadata.isNullable(9), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }

    // //////////////////////////////////////////////////////////
    // #10: NUM_PREC_RADIX:
    // - JDBC:  "10. ... int => Radix (typically either 10 or 2)"
    // - Seems should be null for non-numeric, but unclear.
    // - Drill:  ?
    // - (Meta): INTEGER?; Nullable?;
    // 
    // Note:  Some MS page says NUM_PREC_RADIX specifies the units (decimal digits
    // or binary bits COLUMN_SIZE, and is NULL for non-numeric columns.
    @Test
    public void test_NUM_PREC_RADIX_isAtRightPosition() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnLabel(10), CoreMatchers.equalTo("NUM_PREC_RADIX"));
    }

    @Test
    public void test_NUM_PREC_RADIX_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptBOOLEAN, "NUM_PREC_RADIX"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUM_PREC_RADIX_hasRightValue_mdrReqINTEGER() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTEGER, "NUM_PREC_RADIX"), CoreMatchers.equalTo(2));
    }

    @Test
    public void test_NUM_PREC_RADIX_hasRightValue_mdrOptBIGINT() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptBIGINT, "NUM_PREC_RADIX"), CoreMatchers.equalTo(2));
    }

    @Test
    public void test_NUM_PREC_RADIX_hasRightValue_mdrOptFLOAT() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptFLOAT, "NUM_PREC_RADIX"), CoreMatchers.equalTo(2));
    }

    @Test
    public void test_NUM_PREC_RADIX_hasRightValue_mdrReqDOUBLE() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqDOUBLE, "NUM_PREC_RADIX"), CoreMatchers.equalTo(2));
    }

    @Test
    public void test_NUM_PREC_RADIX_hasRightValue_mdrReqDECIMAL_5_3() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqDECIMAL_5_3, "NUM_PREC_RADIX"), CoreMatchers.equalTo(10));
    }

    @Test
    public void test_NUM_PREC_RADIX_hasRightValue_mdrReqVARCHAR_10() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqVARCHAR_10, "NUM_PREC_RADIX"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUM_PREC_RADIX_hasRightValue_mdrOptVARCHAR() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptVARCHAR, "NUM_PREC_RADIX"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUM_PREC_RADIX_hasRightValue_mdrReqCHAR_5() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqCHAR_5, "NUM_PREC_RADIX"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUM_PREC_RADIX_hasRightValue_mdrOptVARBINARY_16() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptVARBINARY_16, "NUM_PREC_RADIX"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUM_PREC_RADIX_hasRightValue_mdrOptBINARY_1048576CHECK() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptBINARY_65536, "NUM_PREC_RADIX"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUM_PREC_RADIX_hasRightValue_mdrReqDATE() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqDATE, "NUM_PREC_RADIX"), CoreMatchers.equalTo(10));
    }

    @Test
    public void test_NUM_PREC_RADIX_hasRightValue_mdrReqTIME() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqTIME, "NUM_PREC_RADIX"), CoreMatchers.equalTo(10));
    }

    @Test
    public void test_NUM_PREC_RADIX_hasRightValue_mdrOptTIME_7() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptTIME_7, "NUM_PREC_RADIX"), CoreMatchers.equalTo(10));
    }

    @Test
    public void test_NUM_PREC_RADIX_hasRightValue_mdrOptTIMESTAMP() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptTIMESTAMP, "NUM_PREC_RADIX"), CoreMatchers.equalTo(10));
    }

    @Test
    public void test_NUM_PREC_RADIX_hasRightValue_mdrReqINTERVAL_Y() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_Y, "NUM_PREC_RADIX"), CoreMatchers.equalTo(10));
    }

    @Test
    public void test_NUM_PREC_RADIX_hasRightValue_mdrReqINTERVAL_3H_S1() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_3H_S1, "NUM_PREC_RADIX"), CoreMatchers.equalTo(10));
    }

    @Test
    public void test_NUM_PREC_RADIX_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnName(10), CoreMatchers.equalTo("NUM_PREC_RADIX"));
    }

    @Test
    public void test_NUM_PREC_RADIX_hasRightTypeString() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnTypeName(10), CoreMatchers.equalTo("INTEGER"));
    }

    @Test
    public void test_NUM_PREC_RADIX_hasRightTypeCode() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnType(10), CoreMatchers.equalTo(Types.INTEGER));
    }

    @Test
    public void test_NUM_PREC_RADIX_hasRightClass() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnClassName(10), CoreMatchers.equalTo(Integer.class.getName()));
    }

    @Test
    public void test_NUM_PREC_RADIX_hasRightNullability() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", DatabaseMetaDataGetColumnsTest.rowsMetadata.isNullable(10), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }

    // //////////////////////////////////////////////////////////
    // #11: NULLABLE:
    // - JDBC:  "11. ... int => is NULL allowed.
    // columnNoNulls - might not allow NULL values
    // columnNullable - definitely allows NULL values
    // columnNullableUnknown - nullability unknown"
    // - Drill:
    // - (Meta): INTEGER(?); Non-nullable(?).
    @Test
    public void test_NULLABLE_isAtRightPosition() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnLabel(11), CoreMatchers.equalTo("NULLABLE"));
    }

    @Test
    public void test_NULLABLE_hasRightValue_mdrOptBIGINT() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptBIGINT, "NULLABLE"), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }

    @Test
    public void test_NULLABLE_hasRightValue_mdrOptFLOAT() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptFLOAT, "NULLABLE"), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }

    @Test
    public void test_NULLABLE_hasRightValue_mdrReqDOUBLE() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqDOUBLE, "NULLABLE"), CoreMatchers.equalTo(ResultSetMetaData.columnNoNulls));
    }

    @Test
    public void test_NULLABLE_hasRightValue_mdrReqINTEGER() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTEGER, "NULLABLE"), CoreMatchers.equalTo(ResultSetMetaData.columnNoNulls));
    }

    @Test
    public void test_NULLABLE_hasRightValue_mdrReqDECIMAL_5_3() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqDECIMAL_5_3, "NULLABLE"), CoreMatchers.equalTo(ResultSetMetaData.columnNoNulls));
    }

    @Test
    public void test_NULLABLE_hasRightValue_mdrReqVARCHAR_10() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqVARCHAR_10, "NULLABLE"), CoreMatchers.equalTo(ResultSetMetaData.columnNoNulls));
    }

    @Test
    public void test_NULLABLE_hasRightValue_mdrOptVARCHAR() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptVARCHAR, "NULLABLE"), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }

    @Test
    public void test_NULLABLE_hasRightValue_mdrReqCHAR_5() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqCHAR_5, "NULLABLE"), CoreMatchers.equalTo(ResultSetMetaData.columnNoNulls));
    }

    @Test
    public void test_NULLABLE_hasRightValue_mdrOptVARBINARY_16() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptVARBINARY_16, "NULLABLE"), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }

    @Test
    public void test_NULLABLE_hasRightValue_mdrOptBINARY_1048576() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptBINARY_65536, "NULLABLE"), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }

    @Test
    public void test_NULLABLE_hasRightValue_mdrReqDATE() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqDATE, "NULLABLE"), CoreMatchers.equalTo(ResultSetMetaData.columnNoNulls));
    }

    @Test
    public void test_NULLABLE_hasRightValue_mdrReqTIME() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqTIME, "NULLABLE"), CoreMatchers.equalTo(ResultSetMetaData.columnNoNulls));
    }

    @Test
    public void test_NULLABLE_hasRightValue_mdrOptTIME_7() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptTIME_7, "NULLABLE"), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }

    @Test
    public void test_NULLABLE_hasRightValue_mdrOptTIMESTAMP() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptTIMESTAMP, "NULLABLE"), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }

    @Test
    public void test_NULLABLE_hasRightValue_mdrReqINTERVAL_Y() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_Y, "NULLABLE"), CoreMatchers.equalTo(ResultSetMetaData.columnNoNulls));
    }

    @Test
    public void test_NULLABLE_hasRightValue_mdrReqINTERVAL_3H_S1() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_3H_S1, "NULLABLE"), CoreMatchers.equalTo(ResultSetMetaData.columnNoNulls));
    }

    @Test
    public void test_NULLABLE_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnName(11), CoreMatchers.equalTo("NULLABLE"));
    }

    @Test
    public void test_NULLABLE_hasRightTypeString() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnTypeName(11), CoreMatchers.equalTo("INTEGER"));
    }

    @Test
    public void test_NULLABLE_hasRightTypeCode() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnType(11), CoreMatchers.equalTo(Types.INTEGER));
    }

    @Test
    public void test_NULLABLE_hasRightClass() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnClassName(11), CoreMatchers.equalTo(Integer.class.getName()));
    }

    @Test
    public void test_NULLABLE_hasRightNullability() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", DatabaseMetaDataGetColumnsTest.rowsMetadata.isNullable(11), CoreMatchers.equalTo(ResultSetMetaData.columnNoNulls));
    }

    // //////////////////////////////////////////////////////////
    // #12: REMARKS:
    // - JDBC:  "12. ... String => comment describing column (may be null)"
    // - Drill: none, so always null
    // - (Meta): VARCHAR (NVARCHAR?); Nullable;
    @Test
    public void test_REMARKS_isAtRightPosition() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnLabel(12), CoreMatchers.equalTo("REMARKS"));
    }

    @Test
    public void test_REMARKS_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrOptBOOLEAN.getString("REMARKS"), CoreMatchers.nullValue());
    }

    @Test
    public void test_REMARKS_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnName(12), CoreMatchers.equalTo("REMARKS"));
    }

    @Test
    public void test_REMARKS_hasRightTypeString() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnTypeName(12), CoreMatchers.equalTo("CHARACTER VARYING"));
    }

    @Test
    public void test_REMARKS_hasRightTypeCode() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnType(12), CoreMatchers.equalTo(Types.VARCHAR));
    }

    @Test
    public void test_REMARKS_hasRightClass() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnClassName(12), CoreMatchers.equalTo(String.class.getName()));
    }

    @Test
    public void test_REMARKS_hasRightNullability() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", DatabaseMetaDataGetColumnsTest.rowsMetadata.isNullable(12), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }

    // //////////////////////////////////////////////////////////
    // #13: COLUMN_DEF:
    // - JDBC:  "13. ... String => default value for the column, which should be
    // interpreted as a string when the value is enclosed in single quotes
    // (may be null)"
    // - Drill:  no real default values, right?
    // - (Meta): VARCHAR (NVARCHAR?);  Nullable;
    @Test
    public void test_COLUMN_DEF_isAtRightPosition() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnLabel(13), CoreMatchers.equalTo("COLUMN_DEF"));
    }

    @Test
    public void test_COLUMN_DEF_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrOptBOOLEAN.getString("COLUMN_DEF"), CoreMatchers.nullValue());
    }

    @Test
    public void test_COLUMN_DEF_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnName(13), CoreMatchers.equalTo("COLUMN_DEF"));
    }

    @Test
    public void test_COLUMN_DEF_hasRightTypeString() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnTypeName(13), CoreMatchers.equalTo("CHARACTER VARYING"));
    }

    @Test
    public void test_COLUMN_DEF_hasRightTypeCode() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnType(13), CoreMatchers.equalTo(Types.VARCHAR));
    }

    @Test
    public void test_COLUMN_DEF_hasRightClass() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnClassName(13), CoreMatchers.equalTo(String.class.getName()));// ???Text

    }

    @Test
    public void test_COLUMN_DEF_hasRightNullability() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", DatabaseMetaDataGetColumnsTest.rowsMetadata.isNullable(13), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }

    // //////////////////////////////////////////////////////////
    // #14: SQL_DATA_TYPE:
    // - JDBC:  "14. ... int => unused"
    // - Drill:
    // - (Meta): INTEGER(?);
    // Since "unused," check only certain meta-metadata.
    @Test
    public void test_SQL_DATA_TYPE_isAtRightPosition() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnLabel(14), CoreMatchers.equalTo("SQL_DATA_TYPE"));
    }

    // No specific value to check for.
    @Test
    public void test_SQL_DATA_TYPE_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnName(14), CoreMatchers.equalTo("SQL_DATA_TYPE"));
    }

    @Test
    public void test_SQL_DATA_TYPE_hasRightTypeString() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnTypeName(14), CoreMatchers.equalTo("INTEGER"));
    }

    @Test
    public void test_SQL_DATA_TYPE_hasRightTypeCode() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnType(14), CoreMatchers.equalTo(Types.INTEGER));
    }

    @Test
    public void test_SQL_DATA_TYPE_hasRightClass() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnClassName(14), CoreMatchers.equalTo(Integer.class.getName()));
    }

    // //////////////////////////////////////////////////////////
    // #15: SQL_DATETIME_SUB:
    // - JDBC:  "15. ... int => unused"
    // - Drill:
    // - (Meta):  INTEGER(?);
    // Since "unused," check only certain meta-metadata.
    @Test
    public void test_SQL_DATETIME_SUB_isAtRightPosition() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnLabel(15), CoreMatchers.equalTo("SQL_DATETIME_SUB"));
    }

    // No specific value to check for.
    @Test
    public void test_SQL_DATETIME_SUB_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnName(15), CoreMatchers.equalTo("SQL_DATETIME_SUB"));
    }

    @Test
    public void test_SQL_DATETIME_SUB_hasRightTypeString() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnTypeName(15), CoreMatchers.equalTo("INTEGER"));
    }

    @Test
    public void test_SQL_DATETIME_SUB_hasRightTypeCode() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnType(15), CoreMatchers.equalTo(Types.INTEGER));
    }

    @Test
    public void test_SQL_DATETIME_SUB_hasRightClass() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnClassName(15), CoreMatchers.equalTo(Integer.class.getName()));
    }

    // //////////////////////////////////////////////////////////
    // #16: CHAR_OCTET_LENGTH:
    // - JDBC:  "16. ... int => for char types the maximum number of bytes
    // in the column"
    // - apparently should be null for non-character types
    // - Drill:
    // - (Meta): INTEGER(?); Nullable(?);
    @Test
    public void test_CHAR_OCTET_LENGTH_isAtRightPosition() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnLabel(16), CoreMatchers.equalTo("CHAR_OCTET_LENGTH"));
    }

    @Test
    public void test_CHAR_OCTET_LENGTH_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptBOOLEAN, "CHAR_OCTET_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHAR_OCTET_LENGTH_hasRightValue_mdrReqINTEGER() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTEGER, "CHAR_OCTET_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHAR_OCTET_LENGTH_hasRightValue_mdrOptFLOAT() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptFLOAT, "CHAR_OCTET_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHAR_OCTET_LENGTH_hasRightValue_mdrReqDOUBLE() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqDOUBLE, "CHAR_OCTET_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHAR_OCTET_LENGTH_hasRightValue_mdrReqDECIMAL_5_3() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqDECIMAL_5_3, "CHAR_OCTET_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHAR_OCTET_LENGTH_hasRightValue_mdrReqVARCHAR_10() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqVARCHAR_10, "CHAR_OCTET_LENGTH"), /* max. UTF-8 bytes per char. */
        CoreMatchers.equalTo((10/* chars. */
         * 4)));
    }

    @Test
    public void test_CHAR_OCTET_LENGTH_hasRightValue_mdrOptVARCHAR() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptVARCHAR, "CHAR_OCTET_LENGTH"), /* max. UTF-8 bytes per char. */
        CoreMatchers.equalTo(((Types.MAX_VARCHAR_LENGTH)/* chars. (default of 65535) */
         * 4)));
    }

    @Test
    public void test_CHAR_OCTET_LENGTH_hasRightValue_mdrReqCHAR_5() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqCHAR_5, "CHAR_OCTET_LENGTH"), /* max. UTF-8 bytes per char. */
        CoreMatchers.equalTo((5/* chars. */
         * 4)));
    }

    @Test
    public void test_CHAR_OCTET_LENGTH_hasRightValue_mdrOptVARBINARY_16() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptVARBINARY_16, "CHAR_OCTET_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHAR_OCTET_LENGTH_hasRightValue_mdrOptBINARY_1048576CHECK() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptBINARY_65536, "CHAR_OCTET_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHAR_OCTET_LENGTH_hasRightValue_mdrReqDATE() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqDATE, "CHAR_OCTET_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHAR_OCTET_LENGTH_hasRightValue_mdrReqTIME() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqTIME, "CHAR_OCTET_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHAR_OCTET_LENGTH_hasRightValue_mdrOptTIME_7() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptTIME_7, "CHAR_OCTET_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHAR_OCTET_LENGTH_hasRightValue_mdrOptTIMESTAMP() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptTIMESTAMP, "CHAR_OCTET_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHAR_OCTET_LENGTH_hasRightValue_mdrReqINTERVAL_Y() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_Y, "CHAR_OCTET_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHAR_OCTET_LENGTH_hasRightValue_mdrReqINTERVAL_3H_S1() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_3H_S1, "CHAR_OCTET_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHAR_OCTET_LENGTH_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnName(16), CoreMatchers.equalTo("CHAR_OCTET_LENGTH"));
    }

    @Test
    public void test_CHAR_OCTET_LENGTH_hasRightTypeString() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnTypeName(16), CoreMatchers.equalTo("INTEGER"));
    }

    @Test
    public void test_CHAR_OCTET_LENGTH_hasRightTypeCode() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnType(16), CoreMatchers.equalTo(Types.INTEGER));
    }

    @Test
    public void test_CHAR_OCTET_LENGTH_hasRightClass() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnClassName(16), CoreMatchers.equalTo(Integer.class.getName()));
    }

    @Test
    public void test_CHAR_OCTET_LENGTH_hasRightNullability() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", DatabaseMetaDataGetColumnsTest.rowsMetadata.isNullable(16), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }

    // //////////////////////////////////////////////////////////
    // #17: ORDINAL_POSITION:
    // - JDBC:  "17. ... int => index of column in table (starting at 1)"
    // - Drill:
    // - (Meta):  INTEGER(?); Non-nullable(?).
    @Test
    public void test_ORDINAL_POSITION_isAtRightPosition() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnLabel(17), CoreMatchers.equalTo("ORDINAL_POSITION"));
    }

    @Test
    public void test_ORDINAL_POSITION_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptBOOLEAN, "ORDINAL_POSITION"), CoreMatchers.equalTo(1));
    }

    @Test
    public void test_ORDINAL_POSITION_hasRightValue_mdrReqINTEGER() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqINTEGER, "ORDINAL_POSITION"), CoreMatchers.equalTo(4));
    }

    @Test
    public void test_ORDINAL_POSITION_hasRightValue_mdrOptBIGINT() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptBIGINT, "ORDINAL_POSITION"), CoreMatchers.equalTo(5));
    }

    @Test
    public void test_ORDINAL_POSITION_hasRightValue_mdrOptFLOAT() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrOptFLOAT, "ORDINAL_POSITION"), CoreMatchers.equalTo(7));
    }

    @Test
    public void test_ORDINAL_POSITION_hasRightValue_mdrReqDOUBLE() throws SQLException {
        Assert.assertThat(getIntOrNull(DatabaseMetaDataGetColumnsTest.mdrReqDOUBLE, "ORDINAL_POSITION"), CoreMatchers.equalTo(8));
    }

    @Test
    public void test_ORDINAL_POSITION_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnName(17), CoreMatchers.equalTo("ORDINAL_POSITION"));
    }

    @Test
    public void test_ORDINAL_POSITION_hasRightTypeString() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnTypeName(17), CoreMatchers.equalTo("INTEGER"));
    }

    @Test
    public void test_ORDINAL_POSITION_hasRightTypeCode() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnType(17), CoreMatchers.equalTo(Types.INTEGER));
    }

    @Test
    public void test_ORDINAL_POSITION_hasRightClass() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnClassName(17), CoreMatchers.equalTo(Integer.class.getName()));
    }

    @Test
    public void test_ORDINAL_POSITION_hasRightNullability() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", DatabaseMetaDataGetColumnsTest.rowsMetadata.isNullable(17), CoreMatchers.equalTo(ResultSetMetaData.columnNoNulls));
    }

    // //////////////////////////////////////////////////////////
    // #18: IS_NULLABLE:
    // - JDBC:  "18. ... String => ISO rules are used to determine the nullability for a column.
    // YES --- if the column can include NULLs
    // NO --- if the column cannot include NULLs
    // empty string --- if the nullability for the column is unknown"
    // - Drill:  ?
    // - (Meta): VARCHAR (NVARCHAR?); Not nullable?
    @Test
    public void test_IS_NULLABLE_isAtRightPosition() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnLabel(18), CoreMatchers.equalTo("IS_NULLABLE"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrOptBOOLEAN.getString("IS_NULLABLE"), CoreMatchers.equalTo("YES"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrReqINTEGER() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrReqINTEGER.getString("IS_NULLABLE"), CoreMatchers.equalTo("NO"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrOptBIGINT() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrOptBIGINT.getString("IS_NULLABLE"), CoreMatchers.equalTo("YES"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrOptFLOAT() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrOptFLOAT.getString("IS_NULLABLE"), CoreMatchers.equalTo("YES"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrReqDOUBLE() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrReqDOUBLE.getString("IS_NULLABLE"), CoreMatchers.equalTo("NO"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrReqDECIMAL_5_3() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrReqDECIMAL_5_3.getString("IS_NULLABLE"), CoreMatchers.equalTo("NO"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrReqVARCHAR_10() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrReqVARCHAR_10.getString("IS_NULLABLE"), CoreMatchers.equalTo("NO"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrOptVARCHAR() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrOptVARCHAR.getString("IS_NULLABLE"), CoreMatchers.equalTo("YES"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrReqCHAR_5() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrReqCHAR_5.getString("IS_NULLABLE"), CoreMatchers.equalTo("NO"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrOptVARBINARY_16() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrOptVARBINARY_16.getString("IS_NULLABLE"), CoreMatchers.equalTo("YES"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrOptBINARY_1048576CHECK() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrOptBINARY_65536.getString("IS_NULLABLE"), CoreMatchers.equalTo("YES"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrReqDATE() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrReqDATE.getString("IS_NULLABLE"), CoreMatchers.equalTo("NO"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrReqTIME() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrReqTIME.getString("IS_NULLABLE"), CoreMatchers.equalTo("NO"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrOptTIME_7() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrOptTIME_7.getString("IS_NULLABLE"), CoreMatchers.equalTo("YES"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrOptTIMESTAMP() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrOptTIMESTAMP.getString("IS_NULLABLE"), CoreMatchers.equalTo("YES"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrReqINTERVAL_Y() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_Y.getString("IS_NULLABLE"), CoreMatchers.equalTo("NO"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrReqINTERVAL_3H_S1() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrReqINTERVAL_3H_S1.getString("IS_NULLABLE"), CoreMatchers.equalTo("NO"));
    }

    @Test
    public void test_IS_NULLABLE_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnName(18), CoreMatchers.equalTo("IS_NULLABLE"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightTypeString() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnTypeName(18), CoreMatchers.equalTo("CHARACTER VARYING"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightTypeCode() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnType(18), CoreMatchers.equalTo(Types.VARCHAR));
    }

    @Test
    public void test_IS_NULLABLE_hasRightClass() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnClassName(18), CoreMatchers.equalTo(String.class.getName()));
    }

    // //////////////////////////////////////////////////////////
    // #19: SCOPE_CATALOG:
    // - JDBC:  "19. ... String => catalog of table that is the scope of a
    // reference attribute (null if DATA_TYPE isn't REF)"
    // - Drill:
    // - (Meta): VARCHAR (NVARCHAR?); Nullable;
    @Test
    public void test_SCOPE_CATALOG_isAtRightPosition() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnLabel(19), CoreMatchers.equalTo("SCOPE_CATALOG"));
    }

    @Test
    public void test_SCOPE_CATALOG_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        final String value = DatabaseMetaDataGetColumnsTest.mdrOptBOOLEAN.getString("SCOPE_SCHEMA");
        Assert.assertThat(value, CoreMatchers.nullValue());
    }

    @Test
    public void test_SCOPE_CATALOG_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnName(19), CoreMatchers.equalTo("SCOPE_CATALOG"));
    }

    @Test
    public void test_SCOPE_CATALOG_hasRightTypeString() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnTypeName(19), CoreMatchers.equalTo("CHARACTER VARYING"));
    }

    @Test
    public void test_SCOPE_CATALOG_hasRightTypeCode() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnType(19), CoreMatchers.equalTo(Types.VARCHAR));
    }

    @Test
    public void test_SCOPE_CATALOG_hasRightClass() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnClassName(19), CoreMatchers.equalTo(String.class.getName()));
    }

    @Test
    public void test_SCOPE_CATALOG_hasRightNullability() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", DatabaseMetaDataGetColumnsTest.rowsMetadata.isNullable(19), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }

    // //////////////////////////////////////////////////////////
    // #20: SCOPE_SCHEMA:
    // - JDBC:  "20. ... String => schema of table that is the scope of a
    // reference attribute (null if the DATA_TYPE isn't REF)"
    // - Drill:  no REF, so always null?
    // - (Meta): VARCHAR?; Nullable;
    @Test
    public void test_SCOPE_SCHEMA_isAtRightPosition() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnLabel(20), CoreMatchers.equalTo("SCOPE_SCHEMA"));
    }

    @Test
    public void test_SCOPE_SCHEMA_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        final String value = DatabaseMetaDataGetColumnsTest.mdrOptBOOLEAN.getString("SCOPE_SCHEMA");
        Assert.assertThat(value, CoreMatchers.nullValue());
    }

    @Test
    public void test_SCOPE_SCHEMA_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnName(20), CoreMatchers.equalTo("SCOPE_SCHEMA"));
    }

    @Test
    public void test_SCOPE_SCHEMA_hasRightTypeString() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnTypeName(20), CoreMatchers.equalTo("CHARACTER VARYING"));
    }

    @Test
    public void test_SCOPE_SCHEMA_hasRightTypeCode() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnType(20), CoreMatchers.equalTo(Types.VARCHAR));
    }

    @Test
    public void test_SCOPE_SCHEMA_hasRightClass() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnClassName(20), CoreMatchers.equalTo(String.class.getName()));
    }

    @Test
    public void test_SCOPE_SCHEMA_hasRightNullability() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", DatabaseMetaDataGetColumnsTest.rowsMetadata.isNullable(20), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }

    // //////////////////////////////////////////////////////////
    // #21: SCOPE_TABLE:
    // - JDBC:  "21. ... String => table name that this the scope of a reference
    // attribute (null if the DATA_TYPE isn't REF)"
    // - Drill:
    // - (Meta): VARCHAR (NVARCHAR?); Nullable;
    @Test
    public void test_SCOPE_TABLE_isAtRightPosition() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnLabel(21), CoreMatchers.equalTo("SCOPE_TABLE"));
    }

    @Test
    public void test_SCOPE_TABLE_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        final String value = DatabaseMetaDataGetColumnsTest.mdrOptBOOLEAN.getString("SCOPE_TABLE");
        Assert.assertThat(value, CoreMatchers.nullValue());
    }

    @Test
    public void test_SCOPE_TABLE_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnName(21), CoreMatchers.equalTo("SCOPE_TABLE"));
    }

    @Test
    public void test_SCOPE_TABLE_hasRightTypeString() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnTypeName(21), CoreMatchers.equalTo("CHARACTER VARYING"));
    }

    @Test
    public void test_SCOPE_TABLE_hasRightTypeCode() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnType(21), CoreMatchers.equalTo(Types.VARCHAR));
    }

    @Test
    public void test_SCOPE_TABLE_hasRightClass() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnClassName(21), CoreMatchers.equalTo(String.class.getName()));
    }

    @Test
    public void test_SCOPE_TABLE_hasRightNullability() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", DatabaseMetaDataGetColumnsTest.rowsMetadata.isNullable(21), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }

    // //////////////////////////////////////////////////////////
    // #22: SOURCE_DATA_TYPE:
    // - JDBC:  "22. ... short => source type of a distinct type or user-generated
    // Ref type, SQL type from java.sql.Types (null if DATA_TYPE isn't
    // DISTINCT or user-generated REF)"
    // - Drill:  not DISTINCT or REF, so null?
    // - (Meta): SMALLINT(?);  Nullable;
    @Test
    public void test_SOURCE_DATA_TYPE_isAtRightPosition() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnLabel(22), CoreMatchers.equalTo("SOURCE_DATA_TYPE"));
    }

    @Test
    public void test_SOURCE_DATA_TYPE_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrOptBOOLEAN.getString("SOURCE_DATA_TYPE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_SOURCE_DATA_TYPE_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnName(22), CoreMatchers.equalTo("SOURCE_DATA_TYPE"));
    }

    @Test
    public void test_SOURCE_DATA_TYPE_hasRightTypeString() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnTypeName(22), CoreMatchers.equalTo("SMALLINT"));
    }

    @Test
    public void test_SOURCE_DATA_TYPE_hasRightTypeCode() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnType(22), CoreMatchers.equalTo(Types.SMALLINT));
    }

    @Test
    public void test_SOURCE_DATA_TYPE_hasRightClass() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnClassName(22), CoreMatchers.equalTo(Short.class.getName()));
    }

    @Test
    public void test_SOURCE_DATA_TYPE_hasRightNullability() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", DatabaseMetaDataGetColumnsTest.rowsMetadata.isNullable(22), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }

    // //////////////////////////////////////////////////////////
    // #23: IS_AUTOINCREMENT:
    // - JDBC:  "23. ... String => Indicates whether this column is auto incremented
    // YES --- if the column is auto incremented
    // NO --- if the column is not auto incremented
    // empty string --- if it cannot be determined whether the column is auto incremented"
    // - Drill:
    // - (Meta): VARCHAR (NVARCHAR?); Non-nullable(?);
    @Test
    public void test_IS_AUTOINCREMENT_isAtRightPosition() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnLabel(23), CoreMatchers.equalTo("IS_AUTOINCREMENT"));
    }

    @Test
    public void test_IS_AUTOINCREMENT_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        // TODO:  Can it be 'NO' (not auto-increment) rather than '' (unknown)?
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrOptBOOLEAN.getString("IS_AUTOINCREMENT"), CoreMatchers.equalTo(""));
    }

    // Not bothering with other test columns for IS_AUTOINCREMENT.
    @Test
    public void test_IS_AUTOINCREMENT_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnName(23), CoreMatchers.equalTo("IS_AUTOINCREMENT"));
    }

    @Test
    public void test_IS_AUTOINCREMENT_hasRightTypeString() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnTypeName(23), CoreMatchers.equalTo("CHARACTER VARYING"));
    }

    @Test
    public void test_IS_AUTOINCREMENT_hasRightTypeCode() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnType(23), CoreMatchers.equalTo(Types.VARCHAR));
    }

    @Test
    public void test_IS_AUTOINCREMENT_hasRightClass() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnClassName(23), CoreMatchers.equalTo(String.class.getName()));
    }

    @Test
    public void test_IS_AUTOINCREMENT_hasRightNullability() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", DatabaseMetaDataGetColumnsTest.rowsMetadata.isNullable(23), CoreMatchers.equalTo(ResultSetMetaData.columnNoNulls));
    }

    // //////////////////////////////////////////////////////////
    // #24: IS_GENERATEDCOLUMN:
    // - JDBC:  "24. ... String => Indicates whether this is a generated column
    // YES --- if this a generated column
    // NO --- if this not a generated column
    // empty string --- if it cannot be determined whether this is a generated column"
    // - Drill:
    // - (Meta): VARCHAR (NVARCHAR?); Non-nullable(?)
    @Test
    public void test_IS_GENERATEDCOLUMN_isAtRightPosition() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnLabel(24), CoreMatchers.equalTo("IS_GENERATEDCOLUMN"));
    }

    @Test
    public void test_IS_GENERATEDCOLUMN_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        // TODO:  Can it be 'NO' (not auto-increment) rather than '' (unknown)?
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.mdrOptBOOLEAN.getString("IS_GENERATEDCOLUMN"), CoreMatchers.equalTo(""));
    }

    // Not bothering with other test columns for IS_GENERATEDCOLUMN.
    @Test
    public void test_IS_GENERATEDCOLUMN_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnName(24), CoreMatchers.equalTo("IS_GENERATEDCOLUMN"));
    }

    @Test
    public void test_IS_GENERATEDCOLUMN_hasRightTypeString() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnTypeName(24), CoreMatchers.equalTo("CHARACTER VARYING"));
    }

    @Test
    public void test_IS_GENERATEDCOLUMN_hasRightTypeCode() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnType(24), CoreMatchers.equalTo(Types.VARCHAR));
    }

    @Test
    public void test_IS_GENERATEDCOLUMN_hasRightClass() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnClassName(24), CoreMatchers.equalTo(String.class.getName()));
    }

    @Test
    public void test_IS_GENERATEDCOLUMN_hasRightNullability() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", DatabaseMetaDataGetColumnsTest.rowsMetadata.isNullable(24), CoreMatchers.equalTo(ResultSetMetaData.columnNoNulls));
    }
}

