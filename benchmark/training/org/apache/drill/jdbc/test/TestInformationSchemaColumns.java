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
package org.apache.drill.jdbc.test;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import org.apache.drill.categories.JdbcTest;
import org.apache.drill.common.types.Types.MAX_VARCHAR_LENGTH;
import org.apache.drill.jdbc.JdbcTestBase;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


// NOTE: TestInformationSchemaColumns and DatabaseMetaDataGetColumnsTest
// have identical sections.  (Cross-maintain them for now; factor out later.)
// TODO:  Review nullability (NULLABLE and IS_NULLABLE columns):
// - It's not clear why Drill sets INFORMATION_SCHEMA.COLUMNS.IS_NULLABLE to
// 'NO' for some columns that contain only null (e.g., for
// "CREATE VIEW x AS SELECT CAST(NULL AS ...) ..."
/**
 * Test class for Drill's INFORMATION_SCHEMA.COLUMNS implementation.
 */
// //////////////////////////////////////////////////////////
// Not (yet) implemented by Drill:
// 
// #17: CHARACTER_SET_CATALOG:
// #18: CHARACTER_SET_SCHEMA:
// #19: CHARACTER_SET_NAME:
// #20: COLLATION_CATALOG:
// #21: COLLATION_SCHEMA:
// #22: COLLATION_NAME:
// #23: DOMAIN_CATALOG:
// #24: DOMAIN_SCHEMA:
// #25: DOMAIN_NAME:
// #26: UDT_CATALOG:
// #27: UDT_SCHEMA:
// #28: UDT_NAME:
// #29: SCOPE_CATALOG:
// #30: SCOPE_SCHEMA:
// #31: SCOPE_NAME:
// #32: MAXIMUM_CARDINALITY:
// #33: DTD_IDENTIFIER:
// #34: IS_SELF_REFERENCING:
// #35: IS_IDENTITY:
// #36: IDENTITY_GENERATION:
// #37: IDENTITY_START:
// #38: IDENTITY_INCREMENT:
// #39: IDENTITY_MAXIMUM:
// #40: IDENTITY_MINIMUM:
// #41: IDENTITY_CYCLE:
// #42: IS_GENERATED:
// #43: GENERATION_EXPRESSION:
// #44: IS_SYSTEM_TIME_PERIOD_START:
// #45: IS_SYSTEM_TIME_PERIOD_END:
// #46: SYSTEM_TIME_PERIOD_TIMESTAMP_GENERATION:
// #47: IS_UPDATABLE:
// #48: DECLARED_DATA_TYPE:
// #49: DECLARED_NUMERIC_PRECISION:
// #50: DECLARED_NUMERIC_SCALE:
@Category(JdbcTest.class)
public class TestInformationSchemaColumns extends JdbcTestBase {
    private static final String VIEW_NAME = (TestInformationSchemaColumns.class.getSimpleName()) + "_View";

    /**
     * The one shared JDBC connection to Drill.
     */
    private static Connection connection;

    /**
     * Result set metadata.  For checking columns themselves (not cell
     *  values or row order).
     */
    private static ResultSetMetaData rowsMetadata;

    // //////////////////
    // Results from INFORMATION_SCHEMA.COLUMN for test columns of various types.
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

    @Test
    public void testMetadataHasInterimNumberOfColumns() throws SQLException {
        Assert.assertThat("column count", TestInformationSchemaColumns.rowsMetadata.getColumnCount(), CoreMatchers.equalTo(16));
    }

    // //////////////////////////////////////////////////////////
    // #1: TABLE_CATALOG:
    // - SQL:
    // - Drill:  Always "DRILL".
    // - (Meta): VARCHAR; Non-nullable(?);
    @Test
    public void test_TABLE_CATALOG_isAtRightPosition() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnLabel(1), CoreMatchers.equalTo("TABLE_CATALOG"));
    }

    @Test
    public void test_TABLE_CATALOG_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrOptBOOLEAN.getString("TABLE_CATALOG"), CoreMatchers.equalTo("DRILL"));
    }

    // Not bothering with other test columns for TABLE_CAT.
    @Test
    public void test_TABLE_CATALOG_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnName(1), CoreMatchers.equalTo("TABLE_CATALOG"));
    }

    @Test
    public void test_TABLE_CATALOG_hasRightTypeString() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnTypeName(1), CoreMatchers.equalTo("CHARACTER VARYING"));
    }

    @Test
    public void test_TABLE_CATALOG_hasRightTypeCode() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnType(1), CoreMatchers.equalTo(Types.VARCHAR));
    }

    // //////////////////////////////////////////////////////////
    // #2: TABLE_SCHEMA:
    // - SQL:
    // - Drill:
    // - (Meta): VARCHAR; Non-nullable(?);
    @Test
    public void test_TABLE_SCHEMA_isAtRightPosition() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnLabel(2), CoreMatchers.equalTo("TABLE_SCHEMA"));
    }

    @Test
    public void test_TABLE_SCHEMA_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrOptBOOLEAN.getString("TABLE_SCHEMA"), CoreMatchers.equalTo(DFS_TMP_SCHEMA));
    }

    // Not bothering with other Hive test columns for TABLE_SCHEM.
    @Test
    public void test_TABLE_SCHEMA_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnName(2), CoreMatchers.equalTo("TABLE_SCHEMA"));
    }

    @Test
    public void test_TABLE_SCHEMA_hasRightTypeString() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnTypeName(2), CoreMatchers.equalTo("CHARACTER VARYING"));
    }

    @Test
    public void test_TABLE_SCHEMA_hasRightTypeCode() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnType(2), CoreMatchers.equalTo(Types.VARCHAR));
    }

    // //////////////////////////////////////////////////////////
    // #3: TABLE_NAME:
    // - SQL:
    // - Drill:
    // - (Meta): VARCHAR; Non-nullable(?);
    @Test
    public void test_TABLE_NAME_isAtRightPosition() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnLabel(3), CoreMatchers.equalTo("TABLE_NAME"));
    }

    @Test
    public void test_TABLE_NAME_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrOptBOOLEAN.getString("TABLE_NAME"), CoreMatchers.equalTo(TestInformationSchemaColumns.VIEW_NAME));
    }

    // Not bothering with other _local_view_ test columns for TABLE_NAME.
    @Test
    public void test_TABLE_NAME_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnName(3), CoreMatchers.equalTo("TABLE_NAME"));
    }

    @Test
    public void test_TABLE_NAME_hasRightTypeString() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnTypeName(3), CoreMatchers.equalTo("CHARACTER VARYING"));
    }

    @Test
    public void test_TABLE_NAME_hasRightTypeCode() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnType(3), CoreMatchers.equalTo(Types.VARCHAR));
    }

    // //////////////////////////////////////////////////////////
    // #4: COLUMN_NAME:
    // - SQL:
    // - Drill:
    // - (Meta): VARCHAR; Non-nullable(?);
    @Test
    public void test_COLUMN_NAME_isAtRightPosition() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnLabel(4), CoreMatchers.equalTo("COLUMN_NAME"));
    }

    @Test
    public void test_COLUMN_NAME_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrOptBOOLEAN.getString("COLUMN_NAME"), CoreMatchers.equalTo("mdrOptBOOLEAN"));
    }

    // Not bothering with other Hive test columns for TABLE_SCHEM.
    @Test
    public void test_COLUMN_NAME_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnName(4), CoreMatchers.equalTo("COLUMN_NAME"));
    }

    @Test
    public void test_COLUMN_NAME_hasRightTypeString() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnTypeName(4), CoreMatchers.equalTo("CHARACTER VARYING"));
    }

    @Test
    public void test_COLUMN_NAME_hasRightTypeCode() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnType(4), CoreMatchers.equalTo(Types.VARCHAR));
    }

    // //////////////////////////////////////////////////////////
    // #5: ORDINAL_POSITION:
    // - SQL:
    // - Drill:
    // - (Meta):  INTEGER NOT NULL
    @Test
    public void test_ORDINAL_POSITION_isAtRightPosition() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnLabel(5), CoreMatchers.equalTo("ORDINAL_POSITION"));
    }

    @Test
    public void test_ORDINAL_POSITION_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrOptBOOLEAN.getInt("ORDINAL_POSITION"), CoreMatchers.equalTo(1));
    }

    @Test
    public void test_ORDINAL_POSITION_hasRightValue_mdrReqINTEGER() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqINTEGER.getInt("ORDINAL_POSITION"), CoreMatchers.equalTo(4));
    }

    @Test
    public void test_ORDINAL_POSITION_hasRightValue_mdrOptBIGINT() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrOptBIGINT.getInt("ORDINAL_POSITION"), CoreMatchers.equalTo(5));
    }

    @Test
    public void test_ORDINAL_POSITION_hasRightValue_mdrOptFLOAT() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrOptFLOAT.getInt("ORDINAL_POSITION"), CoreMatchers.equalTo(7));
    }

    @Test
    public void test_ORDINAL_POSITION_hasRightValue_mdrReqDOUBLE() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqDOUBLE.getInt("ORDINAL_POSITION"), CoreMatchers.equalTo(8));
    }

    @Test
    public void test_ORDINAL_POSITION_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnName(5), CoreMatchers.equalTo("ORDINAL_POSITION"));
    }

    @Test
    public void test_ORDINAL_POSITION_hasRightTypeString() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnTypeName(5), CoreMatchers.equalTo("INTEGER"));
    }

    @Test
    public void test_ORDINAL_POSITION_hasRightTypeCode() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnType(5), CoreMatchers.equalTo(Types.INTEGER));
    }

    @Test
    public void test_ORDINAL_POSITION_hasRightNullability() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.rowsMetadata.isNullable(5), CoreMatchers.equalTo(ResultSetMetaData.columnNoNulls));
    }

    // //////////////////////////////////////////////////////////
    // #6: COLUMN_DEFAULT:
    // - SQL:
    // - Drill:
    // - (Meta): VARCHAR; Nullable;
    @Test
    public void test_COLUMN_DEFAULT_isAtRightPosition() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnLabel(6), CoreMatchers.equalTo("COLUMN_DEFAULT"));
    }

    @Test
    public void test_COLUMN_DEFAULT_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrOptBOOLEAN.getString("COLUMN_DEFAULT"), CoreMatchers.nullValue());
    }

    // Not bothering with other Hive test columns for TABLE_SCHEM.
    @Test
    public void test_COLUMN_DEFAULT_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnName(6), CoreMatchers.equalTo("COLUMN_DEFAULT"));
    }

    @Test
    public void test_COLUMN_DEFAULT_hasRightTypeString() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnTypeName(6), CoreMatchers.equalTo("CHARACTER VARYING"));
    }

    @Test
    public void test_COLUMN_DEFAULT_hasRightTypeCode() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnType(6), CoreMatchers.equalTo(Types.VARCHAR));
    }

    @Test
    public void test_COLUMN_DEFAULT_hasRightNullability() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.rowsMetadata.isNullable(6), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }

    // //////////////////////////////////////////////////////////
    // #7: IS_NULLABLE:
    // - SQL:
    // YES  The column is possibly nullable.
    // NO   The column is known not nullable.
    // - Drill:
    // - (Meta): VARCHAR; Non-nullable(?).
    @Test
    public void test_IS_NULLABLE_isAtRightPosition() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnLabel(7), CoreMatchers.equalTo("IS_NULLABLE"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.mdrOptBOOLEAN.getString("IS_NULLABLE"), CoreMatchers.equalTo("YES"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrReqINTEGER() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.mdrReqINTEGER.getString("IS_NULLABLE"), CoreMatchers.equalTo("NO"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrOptBIGINT() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.mdrOptBIGINT.getString("IS_NULLABLE"), CoreMatchers.equalTo("YES"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrOptFLOAT() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.mdrOptFLOAT.getString("IS_NULLABLE"), CoreMatchers.equalTo("YES"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrReqDOUBLE() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.mdrReqDOUBLE.getString("IS_NULLABLE"), CoreMatchers.equalTo("NO"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrReqDECIMAL_5_3() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.mdrReqDECIMAL_5_3.getString("IS_NULLABLE"), CoreMatchers.equalTo("NO"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrReqVARCHAR_10() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.mdrReqVARCHAR_10.getString("IS_NULLABLE"), CoreMatchers.equalTo("NO"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrOptVARCHAR() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.mdrOptVARCHAR.getString("IS_NULLABLE"), CoreMatchers.equalTo("YES"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrReqCHAR_5() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.mdrReqCHAR_5.getString("IS_NULLABLE"), CoreMatchers.equalTo("NO"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrOptVARBINARY_16() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.mdrOptVARBINARY_16.getString("IS_NULLABLE"), CoreMatchers.equalTo("YES"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrOptBINARY_1048576() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.mdrOptBINARY_65536.getString("IS_NULLABLE"), CoreMatchers.equalTo("YES"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrReqDATE() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.mdrReqDATE.getString("IS_NULLABLE"), CoreMatchers.equalTo("NO"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrReqTIME() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.mdrReqTIME.getString("IS_NULLABLE"), CoreMatchers.equalTo("NO"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrOptTIME_7() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.mdrOptTIME_7.getString("IS_NULLABLE"), CoreMatchers.equalTo("YES"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrOptTIMESTAMP() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.mdrOptTIMESTAMP.getString("IS_NULLABLE"), CoreMatchers.equalTo("YES"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrReqINTERVAL_Y() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.mdrReqINTERVAL_Y.getString("IS_NULLABLE"), CoreMatchers.equalTo("NO"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightValue_mdrReqINTERVAL_H_S3() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.mdrReqINTERVAL_3H_S1.getString("IS_NULLABLE"), CoreMatchers.equalTo("NO"));
    }

    @Test
    public void test_IS_NULLABLE_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnName(7), CoreMatchers.equalTo("IS_NULLABLE"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightTypeString() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnTypeName(7), CoreMatchers.equalTo("CHARACTER VARYING"));
    }

    @Test
    public void test_IS_NULLABLE_hasRightTypeCode() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnType(7), CoreMatchers.equalTo(Types.VARCHAR));
    }

    // //////////////////////////////////////////////////////////
    // #8: DATA_TYPE:
    // - SQL:
    // - Drill:
    // - (Meta): VARCHAR; Non-nullable(?);
    @Test
    public void test_DATA_TYPE_isAtRightPosition() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnLabel(8), CoreMatchers.equalTo("DATA_TYPE"));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrOptBOOLEAN.getString("DATA_TYPE"), CoreMatchers.equalTo("BOOLEAN"));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrReqINTEGER() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqINTEGER.getString("DATA_TYPE"), CoreMatchers.equalTo("INTEGER"));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrOptBIGINT() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrOptBIGINT.getString("DATA_TYPE"), CoreMatchers.equalTo("BIGINT"));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrOptFLOAT() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrOptFLOAT.getString("DATA_TYPE"), CoreMatchers.equalTo("FLOAT"));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrReqDOUBLE() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqDOUBLE.getString("DATA_TYPE"), CoreMatchers.equalTo("DOUBLE"));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrReqDECIMAL_5_3() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqDECIMAL_5_3.getString("DATA_TYPE"), CoreMatchers.equalTo("DECIMAL"));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrReqVARCHAR_10() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqVARCHAR_10.getString("DATA_TYPE"), CoreMatchers.equalTo("CHARACTER VARYING"));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrOptVARCHAR() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrOptVARCHAR.getString("DATA_TYPE"), CoreMatchers.equalTo("CHARACTER VARYING"));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrReqCHAR_5() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqCHAR_5.getString("DATA_TYPE"), CoreMatchers.equalTo("CHARACTER"));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrOptVARBINARY_16() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrOptVARBINARY_16.getString("DATA_TYPE"), CoreMatchers.equalTo("BINARY VARYING"));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrReqDATE() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqDATE.getString("DATA_TYPE"), CoreMatchers.equalTo("DATE"));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrReqTIME() throws SQLException {
        // (TIME defaults to TIME WITHOUT TIME ZONE, which uses "TIME" here.)
        Assert.assertThat(TestInformationSchemaColumns.mdrReqTIME.getString("DATA_TYPE"), CoreMatchers.equalTo("TIME"));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrOptTIME_7() throws SQLException {
        // (TIME defaults to TIME WITHOUT TIME ZONE, which uses "TIME" here.)
        Assert.assertThat(TestInformationSchemaColumns.mdrOptTIME_7.getString("DATA_TYPE"), CoreMatchers.equalTo("TIME"));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrOptTIMESTAMP() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrOptTIMESTAMP.getString("DATA_TYPE"), CoreMatchers.equalTo("TIMESTAMP"));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrReqINTERVAL_Y() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqINTERVAL_Y.getString("DATA_TYPE"), CoreMatchers.equalTo("INTERVAL"));
    }

    @Test
    public void test_DATA_TYPE_hasRightValue_mdrReqINTERVAL_H_S3() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqINTERVAL_3H_S1.getString("DATA_TYPE"), CoreMatchers.equalTo("INTERVAL"));
    }

    @Test
    public void test_DATA_TYPE_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnName(8), CoreMatchers.equalTo("DATA_TYPE"));
    }

    @Test
    public void test_DATA_TYPE_hasRightTypeString() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnTypeName(8), CoreMatchers.equalTo("CHARACTER VARYING"));
    }

    @Test
    public void test_DATA_TYPE_hasRightTypeCode() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnType(8), CoreMatchers.equalTo(Types.VARCHAR));
    }

    // //////////////////////////////////////////////////////////
    // #9: CHARACTER_MAXIMUM_LENGTH:
    // - SQL:
    // - Drill:
    // - (Meta): INTEGER; Nullable;
    @Test
    public void test_CHARACTER_MAXIMUM_LENGTH_isAtRightPosition() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnLabel(9), CoreMatchers.equalTo("CHARACTER_MAXIMUM_LENGTH"));
    }

    @Test
    public void test_CHARACTER_MAXIMUM_LENGTH_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptBOOLEAN, "CHARACTER_MAXIMUM_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHARACTER_MAXIMUM_LENGTH_hasRightValue_mdrReqINTEGER() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTEGER, "CHARACTER_MAXIMUM_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHARACTER_MAXIMUM_LENGTH_hasRightValue_mdrOptBIGINT() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptBIGINT, "CHARACTER_MAXIMUM_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHARACTER_MAXIMUM_LENGTH_hasRightValue_mdrOptFLOAT() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptFLOAT, "CHARACTER_MAXIMUM_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHARACTER_MAXIMUM_LENGTH_hasRightValue_mdrReqDOUBLE() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqDOUBLE, "CHARACTER_MAXIMUM_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHARACTER_MAXIMUM_LENGTH_hasRightValue_mdrReqDECIMAL_5_3() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqDECIMAL_5_3, "CHARACTER_MAXIMUM_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHARACTER_MAXIMUM_LENGTH_hasRightValue_mdrReqVARCHAR_10() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqVARCHAR_10, "CHARACTER_MAXIMUM_LENGTH"), CoreMatchers.equalTo(10));
    }

    @Test
    public void test_CHARACTER_MAXIMUM_LENGTH_hasRightValue_mdrOptVARCHAR() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptVARCHAR, "CHARACTER_MAXIMUM_LENGTH"), CoreMatchers.equalTo(MAX_VARCHAR_LENGTH));
    }

    @Test
    public void test_CHARACTER_MAXIMUM_LENGTH_hasRightValue_mdrReqCHAR_5() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqCHAR_5, "CHARACTER_MAXIMUM_LENGTH"), CoreMatchers.equalTo(5));
    }

    @Test
    public void test_CHARACTER_MAXIMUM_LENGTH_hasRightValue_mdrOptVARBINARY_16() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptVARBINARY_16, "CHARACTER_MAXIMUM_LENGTH"), CoreMatchers.equalTo(16));
    }

    @Test
    public void test_CHARACTER_MAXIMUM_LENGTH_hasRightValue_mdrOptBINARY_1048576() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptBINARY_65536, "CHARACTER_MAXIMUM_LENGTH"), CoreMatchers.equalTo(65536));
    }

    @Test
    public void test_CHARACTER_MAXIMUM_LENGTH_hasRightValue_mdrReqDATE() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqDATE, "CHARACTER_MAXIMUM_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHARACTER_MAXIMUM_LENGTH_hasRightValue_mdrReqTIME() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqTIME, "CHARACTER_MAXIMUM_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHARACTER_MAXIMUM_LENGTH_hasRightValue_mdrOptTIME_7() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptTIME_7, "CHARACTER_MAXIMUM_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHARACTER_MAXIMUM_LENGTH_hasRightValue_mdrOptTIMESTAMP() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptTIMESTAMP, "CHARACTER_MAXIMUM_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHARACTER_MAXIMUM_LENGTH_hasRightValue_mdrReqINTERVAL_Y() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_Y, "CHARACTER_MAXIMUM_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHARACTER_MAXIMUM_LENGTH_hasRightValue_mdrReqINTERVAL_H_S3() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_3H_S1, "CHARACTER_MAXIMUM_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHARACTER_MAXIMUM_LENGTH_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnName(9), CoreMatchers.equalTo("CHARACTER_MAXIMUM_LENGTH"));
    }

    @Test
    public void test_CHARACTER_MAXIMUM_LENGTH_hasRightTypeString() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnTypeName(9), CoreMatchers.equalTo("INTEGER"));
    }

    @Test
    public void test_CHARACTER_MAXIMUM_LENGTH_hasRightTypeCode() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnType(9), CoreMatchers.equalTo(Types.INTEGER));
    }

    @Test
    public void test_CHARACTER_MAXIMUM_LENGTH_hasRightNullability() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.rowsMetadata.isNullable(9), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }

    // //////////////////////////////////////////////////////////
    // #10: CHARACTER_OCTET_LENGTH:
    // - SQL:
    // - Drill:
    // - (Meta): INTEGER; Nullable;
    @Test
    public void test_CHARACTER_OCTET_LENGTH_isAtRightPosition() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnLabel(10), CoreMatchers.equalTo("CHARACTER_OCTET_LENGTH"));
    }

    @Test
    public void test_CHARACTER_OCTET_LENGTH_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptBOOLEAN, "CHARACTER_OCTET_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHARACTER_OCTET_LENGTH_hasRightValue_mdrReqINTEGER() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTEGER, "CHARACTER_OCTET_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHARACTER_OCTET_LENGTH_hasRightValue_mdrOptFLOAT() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptFLOAT, "CHARACTER_OCTET_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHARACTER_OCTET_LENGTH_hasRightValue_mdrReqDOUBLE() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqDOUBLE, "CHARACTER_OCTET_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHARACTER_OCTET_LENGTH_hasRightValue_mdrReqDECIMAL_5_3() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqDECIMAL_5_3, "CHARACTER_OCTET_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHARACTER_OCTET_LENGTH_hasRightValue_mdrReqVARCHAR_10() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqVARCHAR_10, "CHARACTER_OCTET_LENGTH"), /* max. UTF-8 bytes per char. */
        CoreMatchers.equalTo((10/* chars. */
         * 4)));
    }

    @Test
    public void test_CHARACTER_OCTET_LENGTH_hasRightValue_mdrOptVARCHAR() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptVARCHAR, "CHARACTER_OCTET_LENGTH"), /* max. UTF-8 bytes per char. */
        CoreMatchers.equalTo(((Types.MAX_VARCHAR_LENGTH)/* chars. (default of 65535) */
         * 4)));
    }

    @Test
    public void test_CHARACTER_OCTET_LENGTH_hasRightValue_mdrReqCHAR_5() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqCHAR_5, "CHARACTER_OCTET_LENGTH"), /* max. UTF-8 bytes per char. */
        CoreMatchers.equalTo((5/* chars. */
         * 4)));
    }

    @Test
    public void test_CHARACTER_OCTET_LENGTH_hasRightValue_mdrOptVARBINARY_16() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptVARBINARY_16, "CHARACTER_OCTET_LENGTH"), CoreMatchers.equalTo(16));
    }

    @Test
    public void test_CHARACTER_OCTET_LENGTH_hasRightValue_mdrOptBINARY_1048576() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptBINARY_65536, "CHARACTER_OCTET_LENGTH"), CoreMatchers.equalTo(65536));
    }

    @Test
    public void test_CHARACTER_OCTET_LENGTH_hasRightValue_mdrReqDATE() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqDATE, "CHARACTER_OCTET_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHARACTER_OCTET_LENGTH_hasRightValue_mdrReqTIME() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqTIME, "CHARACTER_OCTET_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHARACTER_OCTET_LENGTH_hasRightValue_mdrOptTIME_7() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptTIME_7, "CHARACTER_OCTET_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHARACTER_OCTET_LENGTH_hasRightValue_mdrOptTIMESTAMP() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptTIMESTAMP, "CHARACTER_OCTET_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHARACTER_OCTET_LENGTH_hasRightValue_mdrReqINTERVAL_Y() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_Y, "CHARACTER_OCTET_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHARACTER_OCTET_LENGTH_hasRightValue_mdrReqINTERVAL_H_S3() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_3H_S1, "CHARACTER_OCTET_LENGTH"), CoreMatchers.nullValue());
    }

    @Test
    public void test_CHARACTER_OCTET_LENGTH_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnName(10), CoreMatchers.equalTo("CHARACTER_OCTET_LENGTH"));
    }

    @Test
    public void test_CHARACTER_OCTET_LENGTH_hasRightTypeString() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnTypeName(10), CoreMatchers.equalTo("INTEGER"));
    }

    @Test
    public void test_CHARACTER_OCTET_LENGTH_hasRightTypeCode() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnType(10), CoreMatchers.equalTo(Types.INTEGER));
    }

    @Test
    public void test_CHARACTER_OCTET_LENGTH_hasRightNullability() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.rowsMetadata.isNullable(10), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }

    // //////////////////////////////////////////////////////////
    // #11: NUMERIC_PRECISION:
    // - SQL:
    // - Drill:
    // - (Meta): INTEGER; Nullable;
    @Test
    public void test_NUMERIC_PRECISION_isAtRightPosition() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnLabel(11), CoreMatchers.equalTo("NUMERIC_PRECISION"));
    }

    @Test
    public void test_NUMERIC_PRECISION_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptBOOLEAN, "NUMERIC_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_PRECISION_hasRightValue_mdrReqINTEGER() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTEGER, "NUMERIC_PRECISION"), CoreMatchers.equalTo(32));
    }

    @Test
    public void test_NUMERIC_PRECISION_hasRightValue_mdrOptBIGINT() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptBIGINT, "NUMERIC_PRECISION"), CoreMatchers.equalTo(64));
    }

    @Test
    public void test_NUMERIC_PRECISION_hasRightValue_mdrOptFLOAT() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptFLOAT, "NUMERIC_PRECISION"), CoreMatchers.equalTo(24));
    }

    @Test
    public void test_NUMERIC_PRECISION_hasRightValue_mdrReqDOUBLE() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqDOUBLE, "NUMERIC_PRECISION"), CoreMatchers.equalTo(53));
    }

    @Test
    public void test_NUMERIC_PRECISION_hasRightValue_mdrReqDECIMAL_5_3() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqDECIMAL_5_3, "NUMERIC_PRECISION"), CoreMatchers.equalTo(5));
    }

    @Test
    public void test_NUMERIC_PRECISION_hasRightValue_mdrReqVARCHAR_10() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqVARCHAR_10, "NUMERIC_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_PRECISION_hasRightValue_mdrOptVARCHAR() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptVARCHAR, "NUMERIC_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_PRECISION_hasRightValue_mdrReqCHAR_5() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqCHAR_5, "NUMERIC_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_PRECISION_hasRightValue_mdrOptVARBINARY_16() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptVARBINARY_16, "NUMERIC_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_PRECISION_hasRightValue_mdrOptBINARY_1048576() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptBINARY_65536, "NUMERIC_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_PRECISION_hasRightValue_mdrReqDATE() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqDATE, "NUMERIC_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_PRECISION_hasRightValue_mdrReqTIME() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqTIME, "NUMERIC_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_PRECISION_hasRightValue_mdrOptTIME_7() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptTIME_7, "NUMERIC_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_PRECISION_hasRightValue_mdrOptTIMESTAMP() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptTIMESTAMP, "NUMERIC_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_PRECISION_hasRightValue_mdrReqINTERVAL_Y() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_Y, "NUMERIC_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_PRECISION_hasRightValue_mdrReqINTERVAL_H_S3() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_3H_S1, "NUMERIC_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_PRECISION_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnName(11), CoreMatchers.equalTo("NUMERIC_PRECISION"));
    }

    @Test
    public void test_NUMERIC_PRECISION_hasRightTypeString() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnTypeName(11), CoreMatchers.equalTo("INTEGER"));
    }

    @Test
    public void test_NUMERIC_PRECISION_hasRightTypeCode() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnType(11), CoreMatchers.equalTo(Types.INTEGER));
    }

    @Test
    public void test_NUMERIC_PRECISION_hasRightNullability() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.rowsMetadata.isNullable(11), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }

    // //////////////////////////////////////////////////////////
    // #12: NUMERIC_PRECISION_RADIX:
    // - SQL:
    // - Drill:
    // - (Meta): INTEGER; Nullable;
    @Test
    public void test_NUMERIC_PRECISION_RADIX_isAtRightPosition() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnLabel(12), CoreMatchers.equalTo("NUMERIC_PRECISION_RADIX"));
    }

    @Test
    public void test_NUMERIC_PRECISION_RADIX_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptBOOLEAN, "NUMERIC_PRECISION_RADIX"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_PRECISION_RADIX_hasRightValue_mdrReqINTEGER() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTEGER, "NUMERIC_PRECISION_RADIX"), CoreMatchers.equalTo(2));
    }

    @Test
    public void test_NUMERIC_PRECISION_RADIX_hasRightValue_mdrOptBIGINT() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptBIGINT, "NUMERIC_PRECISION_RADIX"), CoreMatchers.equalTo(2));
    }

    @Test
    public void test_NUMERIC_PRECISION_RADIX_hasRightValue_mdrOptFLOAT() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptFLOAT, "NUMERIC_PRECISION_RADIX"), CoreMatchers.equalTo(2));
    }

    @Test
    public void test_NUMERIC_PRECISION_RADIX_hasRightValue_mdrReqDOUBLE() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqDOUBLE, "NUMERIC_PRECISION_RADIX"), CoreMatchers.equalTo(2));
    }

    @Test
    public void test_NUMERIC_PRECISION_RADIX_hasRightValue_mdrReqDECIMAL_5_3() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqDECIMAL_5_3, "NUMERIC_PRECISION_RADIX"), CoreMatchers.equalTo(10));
    }

    @Test
    public void test_NUMERIC_PRECISION_RADIX_hasRightValue_mdrReqVARCHAR_10() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqVARCHAR_10, "NUMERIC_PRECISION_RADIX"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_PRECISION_RADIX_hasRightValue_mdrOptVARCHAR() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptVARCHAR, "NUMERIC_PRECISION_RADIX"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_PRECISION_RADIX_hasRightValue_mdrReqCHAR_5() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqCHAR_5, "NUMERIC_PRECISION_RADIX"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_PRECISION_RADIX_hasRightValue_mdrOptVARBINARY_16() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptVARBINARY_16, "NUMERIC_PRECISION_RADIX"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_PRECISION_RADIX_hasRightValue_mdrOptBINARY_1048576() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptBINARY_65536, "NUMERIC_PRECISION_RADIX"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_PRECISION_RADIX_hasRightValue_mdrReqDATE() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqDATE, "NUMERIC_PRECISION_RADIX"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_PRECISION_RADIX_hasRightValue_mdrReqTIME() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqTIME, "NUMERIC_PRECISION_RADIX"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_PRECISION_RADIX_hasRightValue_mdrOptTIME_7() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptTIME_7, "NUMERIC_PRECISION_RADIX"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_PRECISION_RADIX_hasRightValue_mdrOptTIMESTAMP() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptTIMESTAMP, "NUMERIC_PRECISION_RADIX"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_PRECISION_RADIX_hasRightValue_mdrReqINTERVAL_Y() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_Y, "NUMERIC_PRECISION_RADIX"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_PRECISION_RADIX_hasRightValue_mdrReqINTERVAL_H_S3() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_3H_S1, "NUMERIC_PRECISION_RADIX"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_PRECISION_RADIX_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnName(12), CoreMatchers.equalTo("NUMERIC_PRECISION_RADIX"));
    }

    @Test
    public void test_NUMERIC_PRECISION_RADIX_hasRightTypeString() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnTypeName(12), CoreMatchers.equalTo("INTEGER"));
    }

    @Test
    public void test_NUMERIC_PRECISION_RADIX_hasRightTypeCode() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnType(12), CoreMatchers.equalTo(Types.INTEGER));
    }

    @Test
    public void test_NUMERIC_PRECISION_RADIX_hasRightNullability() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.rowsMetadata.isNullable(12), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }

    // //////////////////////////////////////////////////////////
    // #13: NUMERIC_SCALE:
    // - SQL:
    // - Drill:
    // - (Meta):  INTEGER; Nullable;
    @Test
    public void test_NUMERIC_SCALE_isAtRightPosition() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnLabel(13), CoreMatchers.equalTo("NUMERIC_SCALE"));
    }

    @Test
    public void test_NUMERIC_SCALE_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptBOOLEAN, "NUMERIC_SCALE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_SCALE_hasRightValue_mdrReqINTEGER() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTEGER, "NUMERIC_SCALE"), CoreMatchers.equalTo(0));
    }

    @Test
    public void test_NUMERIC_SCALE_hasRightValue_mdrOptBIGINT() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptBIGINT, "NUMERIC_SCALE"), CoreMatchers.equalTo(0));
    }

    @Test
    public void test_NUMERIC_SCALE_hasRightValue_mdrOptFLOAT() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptFLOAT, "NUMERIC_SCALE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_SCALE_hasRightValue_mdrReqDOUBLE() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqDOUBLE, "NUMERIC_SCALE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_SCALE_hasRightValue_mdrReqDECIMAL_5_3() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqDECIMAL_5_3, "NUMERIC_SCALE"), CoreMatchers.equalTo(3));
    }

    @Test
    public void test_NUMERIC_SCALE_hasRightValue_mdrReqVARCHAR_10() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqVARCHAR_10, "NUMERIC_SCALE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_SCALE_hasRightValue_mdrOptVARCHAR() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptVARCHAR, "NUMERIC_SCALE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_SCALE_hasRightValue_mdrReqCHAR_5() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqCHAR_5, "NUMERIC_SCALE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_SCALE_hasRightValue_mdrOptVARBINARY_16() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptVARBINARY_16, "NUMERIC_SCALE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_SCALE_hasRightValue_mdrOptBINARY_1048576() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptBINARY_65536, "NUMERIC_SCALE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_SCALE_hasRightValue_mdrReqDATE() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqDATE, "NUMERIC_SCALE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_SCALE_hasRightValue_mdrReqTIME() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqTIME, "NUMERIC_SCALE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_SCALE_hasRightValue_mdrOptTIME_7() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptTIME_7, "NUMERIC_SCALE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_SCALE_hasRightValue_mdrOptTIMESTAMP() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptTIMESTAMP, "NUMERIC_SCALE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_SCALE_hasRightValue_mdrReqINTERVAL_Y() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_Y, "NUMERIC_SCALE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_SCALE_hasRightValue_mdrReqINTERVAL_H_S3() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_3H_S1, "NUMERIC_SCALE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_NUMERIC_SCALE_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnName(13), CoreMatchers.equalTo("NUMERIC_SCALE"));
    }

    @Test
    public void test_NUMERIC_SCALE_hasRightTypeString() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnTypeName(13), CoreMatchers.equalTo("INTEGER"));
    }

    @Test
    public void test_NUMERIC_SCALE_hasRightTypeCode() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnType(13), CoreMatchers.equalTo(Types.INTEGER));
    }

    @Test
    public void test_NUMERIC_SCALE_hasRightNullability() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.rowsMetadata.isNullable(13), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }

    // //////////////////////////////////////////////////////////
    // #14: DATETIME_PRECISION:
    // - SQL:
    // - Drill:
    // - (Meta): INTEGER; Nullable;
    @Test
    public void test_DATETIME_PRECISION_isAtRightPosition() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnLabel(14), CoreMatchers.equalTo("DATETIME_PRECISION"));
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptBOOLEAN, "DATETIME_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrReqINTEGER() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTEGER, "DATETIME_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrOptBIGINT() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptBIGINT, "DATETIME_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrOptFLOAT() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptFLOAT, "DATETIME_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrReqDOUBLE() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqDOUBLE, "DATETIME_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrReqDECIMAL_5_3() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqDECIMAL_5_3, "DATETIME_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrReqVARCHAR_10() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqVARCHAR_10, "DATETIME_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrOptVARCHAR() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptVARCHAR, "DATETIME_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrReqCHAR_5() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqCHAR_5, "DATETIME_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrOptVARBINARY_16() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptVARBINARY_16, "DATETIME_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrOptBINARY_1048576() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptBINARY_65536, "DATETIME_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrReqDATE() throws SQLException {
        // Zero because DATE doesn't (seem to) have a datetime precision, but its
        // DATETIME_PRECISION value must not be null.
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqDATE, "DATETIME_PRECISION"), CoreMatchers.equalTo(0));
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrReqTIME() throws SQLException {
        // Zero is default datetime precision for TIME.
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqTIME, "DATETIME_PRECISION"), CoreMatchers.equalTo(0));
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrReqINTERVAL_Y() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_Y, "DATETIME_PRECISION"), CoreMatchers.equalTo(0));
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrReqINTERVAL_3Y_Mo() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_3Y_Mo, "DATETIME_PRECISION"), CoreMatchers.equalTo(0));
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrReqINTERVAL_Mo() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_Mo, "DATETIME_PRECISION"), CoreMatchers.equalTo(0));
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrReqINTERVAL_D() throws SQLException {
        // 6 seems to be Drill's (Calcite's) choice (to use default value for the
        // fractional seconds precision for when SECOND _is_ present) since the SQL
        // spec. (ISO/IEC 9075-2:2011(E) 10.1 <interval qualifier>) doesn't seem to
        // specify the fractional seconds precision when SECOND is _not_ present.
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_D, "DATETIME_PRECISION"), CoreMatchers.equalTo(6));
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrReqINTERVAL_4D_H() throws SQLException {
        // 6 seems to be Drill's (Calcite's) choice (to use default value for the
        // fractional seconds precision for when SECOND _is_ present) since the SQL
        // spec. (ISO/IEC 9075-2:2011(E) 10.1 <interval qualifier>) doesn't seem to
        // specify the fractional seconds precision when SECOND is _not_ present.
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_4D_H, "DATETIME_PRECISION"), CoreMatchers.equalTo(6));
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrReqINTERVAL_3D_Mi() throws SQLException {
        // 6 seems to be Drill's (Calcite's) choice (to use default value for the
        // fractional seconds precision for when SECOND _is_ present) since the SQL
        // spec. (ISO/IEC 9075-2:2011(E) 10.1 <interval qualifier>) doesn't seem to
        // specify the fractional seconds precision when SECOND is _not_ present.
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_3D_Mi, "DATETIME_PRECISION"), CoreMatchers.equalTo(6));
    }

    // Fixed with Calcite update
    // @Ignore( "TODO(DRILL-3244): unignore when fractional secs. prec. is right" )
    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrReqINTERVAL_2D_S5() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_2D_S5, "DATETIME_PRECISION"), CoreMatchers.equalTo(5));
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrReqINTERVAL_H() throws SQLException {
        // 6 seems to be Drill's (Calcite's) choice (to use default value for the
        // fractional seconds precision for when SECOND _is_ present) since the SQL
        // spec. (ISO/IEC 9075-2:2011(E) 10.1 <interval qualifier>) doesn't seem to
        // specify the fractional seconds precision when SECOND is _not_ present.
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_H, "DATETIME_PRECISION"), CoreMatchers.equalTo(6));
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrReqINTERVAL_1H_Mi() throws SQLException {
        // 6 seems to be Drill's (Calcite's) choice (to use default value for the
        // fractional seconds precision for when SECOND _is_ present) since the SQL
        // spec. (ISO/IEC 9075-2:2011(E) 10.1 <interval qualifier>) doesn't seem to
        // specify the fractional seconds precision when SECOND is _not_ present.
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_1H_Mi, "DATETIME_PRECISION"), CoreMatchers.equalTo(6));
    }

    // Fixed with Calcite update
    // @Ignore( "TODO(DRILL-3244): unignore when fractional secs. prec. is right" )
    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrReqINTERVAL_H_S3() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_3H_S1, "DATETIME_PRECISION"), CoreMatchers.equalTo(1));
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrReqINTERVAL_Mi() throws SQLException {
        // 6 seems to be Drill's (Calcite's) choice (to use default value for the
        // fractional seconds precision for when SECOND _is_ present) since the SQL
        // spec. (ISO/IEC 9075-2:2011(E) 10.1 <interval qualifier>) doesn't seem to
        // specify the fractional seconds precision when SECOND is _not_ present.
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_Mi, "DATETIME_PRECISION"), CoreMatchers.equalTo(6));
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrReqINTERVAL_5Mi_S() throws SQLException {
        // 6 is default fractional seconds precision when SECOND field is present.
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_5Mi_S, "DATETIME_PRECISION"), CoreMatchers.equalTo(6));
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrReqINTERVAL_S() throws SQLException {
        // 6 is default for interval fraction seconds precision.
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_S, "DATETIME_PRECISION"), CoreMatchers.equalTo(6));
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrReqINTERVAL_3S() throws SQLException {
        // 6 is default fractional seconds precision when SECOND field is present.
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_3S, "DATETIME_PRECISION"), CoreMatchers.equalTo(6));
    }

    // Fixed with Calcite update
    // @Ignore( "TODO(DRILL-3244): unignore when fractional secs. prec. is right" )
    @Test
    public void test_DATETIME_PRECISION_hasRightValue_mdrReqINTERVAL_3S1() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_3S1, "DATETIME_PRECISION"), CoreMatchers.equalTo(1));
    }

    @Test
    public void test_DATETIME_PRECISION_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnName(14), CoreMatchers.equalTo("DATETIME_PRECISION"));
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightTypeString() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnTypeName(14), CoreMatchers.equalTo("INTEGER"));
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightTypeCode() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnType(14), CoreMatchers.equalTo(Types.INTEGER));
    }

    @Test
    public void test_DATETIME_PRECISION_hasRightNullability() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.rowsMetadata.isNullable(14), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }

    // //////////////////////////////////////////////////////////
    // #15: INTERVAL_TYPE:
    // - SQL:
    // - Drill:
    // - (Meta): VARCHAR; Nullable;
    @Test
    public void test_INTERVAL_TYPE_isAtRightPosition() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnLabel(15), CoreMatchers.equalTo("INTERVAL_TYPE"));
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrOptBOOLEAN.getString("INTERVAL_TYPE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrReqINTEGER() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqINTEGER.getString("INTERVAL_TYPE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrOptBIGINT() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrOptBIGINT.getString("INTERVAL_TYPE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrOptFLOAT() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrOptFLOAT.getString("INTERVAL_TYPE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrReqDOUBLE() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqDOUBLE.getString("INTERVAL_TYPE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrReqDECIMAL_5_3() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqDECIMAL_5_3.getString("INTERVAL_TYPE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrReqVARCHAR_10() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqVARCHAR_10.getString("INTERVAL_TYPE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrOptVARCHAR() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrOptVARCHAR.getString("INTERVAL_TYPE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrReqCHAR_5() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqCHAR_5.getString("INTERVAL_TYPE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrOptVARBINARY_16() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrOptVARBINARY_16.getString("INTERVAL_TYPE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrOptBINARY_1048576() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrOptBINARY_65536.getString("INTERVAL_TYPE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrReqDATE() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqDATE.getString("INTERVAL_TYPE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrReqTIME() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqTIME.getString("INTERVAL_TYPE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrOptTIME_7() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrOptTIME_7.getString("INTERVAL_TYPE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrOptTIMESTAMP() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrOptTIMESTAMP.getString("INTERVAL_TYPE"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrReqINTERVAL_Y() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqINTERVAL_Y.getString("INTERVAL_TYPE"), CoreMatchers.equalTo("YEAR"));
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrReqINTERVAL_3Y_Mo() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqINTERVAL_3Y_Mo.getString("INTERVAL_TYPE"), CoreMatchers.equalTo("YEAR TO MONTH"));
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrReqINTERVAL_2Mo() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqINTERVAL_Mo.getString("INTERVAL_TYPE"), CoreMatchers.equalTo("MONTH"));
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrReqINTERVAL_D() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqINTERVAL_D.getString("INTERVAL_TYPE"), CoreMatchers.equalTo("DAY"));
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrReqINTERVAL_4D_H() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqINTERVAL_4D_H.getString("INTERVAL_TYPE"), CoreMatchers.equalTo("DAY TO HOUR"));
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrReqINTERVAL_3D_Mi() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqINTERVAL_3D_Mi.getString("INTERVAL_TYPE"), CoreMatchers.equalTo("DAY TO MINUTE"));
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrReqINTERVAL_H_S3() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqINTERVAL_3H_S1.getString("INTERVAL_TYPE"), CoreMatchers.equalTo("HOUR TO SECOND"));
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrReqINTERVAL_2D_S5() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqINTERVAL_2D_S5.getString("INTERVAL_TYPE"), CoreMatchers.equalTo("DAY TO SECOND"));
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrReqINTERVAL_H() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqINTERVAL_H.getString("INTERVAL_TYPE"), CoreMatchers.equalTo("HOUR"));
        // fail( "???VERIFY" );
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrReqINTERVAL_1H_Mi() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqINTERVAL_1H_Mi.getString("INTERVAL_TYPE"), CoreMatchers.equalTo("HOUR TO MINUTE"));
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrReqINTERVAL_3H_S1() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqINTERVAL_3H_S1.getString("INTERVAL_TYPE"), CoreMatchers.equalTo("HOUR TO SECOND"));
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrReqINTERVAL_Mi() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqINTERVAL_Mi.getString("INTERVAL_TYPE"), CoreMatchers.equalTo("MINUTE"));
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrReqINTERVAL_5Mi_S() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqINTERVAL_5Mi_S.getString("INTERVAL_TYPE"), CoreMatchers.equalTo("MINUTE TO SECOND"));
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrReqINTERVAL_S() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqINTERVAL_S.getString("INTERVAL_TYPE"), CoreMatchers.equalTo("SECOND"));
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrReqINTERVAL_3S() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqINTERVAL_3S.getString("INTERVAL_TYPE"), CoreMatchers.equalTo("SECOND"));
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightValue_mdrReqINTERVAL_3S1() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.mdrReqINTERVAL_3S1.getString("INTERVAL_TYPE"), CoreMatchers.equalTo("SECOND"));
    }

    @Test
    public void test_INTERVAL_TYPE_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnName(15), CoreMatchers.equalTo("INTERVAL_TYPE"));
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightTypeString() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnTypeName(15), CoreMatchers.equalTo("CHARACTER VARYING"));
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightTypeCode() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnType(15), CoreMatchers.equalTo(Types.VARCHAR));
    }

    @Test
    public void test_INTERVAL_TYPE_hasRightNullability() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.rowsMetadata.isNullable(15), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }

    // //////////////////////////////////////////////////////////
    // #16: INTERVAL_PRECISION:
    // - SQL:
    // - Drill:
    // - (Meta): INTEGER; Nullable;
    @Test
    public void test_INTERVAL_PRECISION_isAtRightPosition() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnLabel(16), CoreMatchers.equalTo("INTERVAL_PRECISION"));
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrOptBOOLEAN() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptBOOLEAN, "INTERVAL_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrReqINTEGER() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTEGER, "INTERVAL_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrOptBIGINT() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptBIGINT, "INTERVAL_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrOptFLOAT() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptFLOAT, "INTERVAL_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrReqDOUBLE() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqDOUBLE, "INTERVAL_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrReqDECIMAL_5_3() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqDECIMAL_5_3, "INTERVAL_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrReqVARCHAR_10() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqVARCHAR_10, "INTERVAL_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrOptVARCHAR() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptVARCHAR, "INTERVAL_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrReqCHAR_5() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqCHAR_5, "INTERVAL_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrOptVARBINARY_16() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptVARBINARY_16, "INTERVAL_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrOptBINARY_1048576() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptBINARY_65536, "INTERVAL_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrReqDATE() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqDATE, "INTERVAL_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrReqTIME() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqTIME, "INTERVAL_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrOptTIME_7() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptTIME_7, "INTERVAL_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrOptTIMESTAMP() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrOptTIMESTAMP, "INTERVAL_PRECISION"), CoreMatchers.nullValue());
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrReqINTERVAL_Y() throws SQLException {
        // 2 is default field precision.
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_Y, "INTERVAL_PRECISION"), CoreMatchers.equalTo(2));
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrReqINTERVAL_3Y_Mo() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_3Y_Mo, "INTERVAL_PRECISION"), CoreMatchers.equalTo(3));
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrReqINTERVAL_2Mo() throws SQLException {
        // 2 is default field precision.
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_Mo, "INTERVAL_PRECISION"), CoreMatchers.equalTo(2));
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrReqINTERVAL_D() throws SQLException {
        // 2 is default field precision.
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_D, "INTERVAL_PRECISION"), CoreMatchers.equalTo(2));
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrReqINTERVAL_4D_H() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_4D_H, "INTERVAL_PRECISION"), CoreMatchers.equalTo(4));
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrReqINTERVAL_3D_Mi() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_3D_Mi, "INTERVAL_PRECISION"), CoreMatchers.equalTo(3));
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrReqINTERVAL_2D_S5() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_2D_S5, "INTERVAL_PRECISION"), CoreMatchers.equalTo(2));
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrReqINTERVAL_H() throws SQLException {
        // 2 is default field precision.
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_H, "INTERVAL_PRECISION"), CoreMatchers.equalTo(2));
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrReqINTERVAL_1H_Mi() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_1H_Mi, "INTERVAL_PRECISION"), CoreMatchers.equalTo(1));
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrReqINTERVAL_3H_S1() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_3H_S1, "INTERVAL_PRECISION"), CoreMatchers.equalTo(3));
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrReqINTERVAL_Mi() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_Mi, "INTERVAL_PRECISION"), CoreMatchers.equalTo(2));
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrReqINTERVAL_5Mi_S() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_5Mi_S, "INTERVAL_PRECISION"), CoreMatchers.equalTo(5));
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrReqINTERVAL_S() throws SQLException {
        // 2 is default field precision.
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_S, "INTERVAL_PRECISION"), CoreMatchers.equalTo(2));
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrReqINTERVAL_3S() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_3S, "INTERVAL_PRECISION"), CoreMatchers.equalTo(3));
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightValue_mdrReqINTERVAL_3S1() throws SQLException {
        Assert.assertThat(getIntOrNull(TestInformationSchemaColumns.mdrReqINTERVAL_3S1, "INTERVAL_PRECISION"), CoreMatchers.equalTo(3));
    }

    @Test
    public void test_INTERVAL_PRECISION_hasSameNameAndLabel() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnName(16), CoreMatchers.equalTo("INTERVAL_PRECISION"));
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightTypeString() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnTypeName(16), CoreMatchers.equalTo("INTEGER"));
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightTypeCode() throws SQLException {
        Assert.assertThat(TestInformationSchemaColumns.rowsMetadata.getColumnType(16), CoreMatchers.equalTo(Types.INTEGER));
    }

    @Test
    public void test_INTERVAL_PRECISION_hasRightNullability() throws SQLException {
        Assert.assertThat("ResultSetMetaData.column...Null... nullability code:", TestInformationSchemaColumns.rowsMetadata.isNullable(16), CoreMatchers.equalTo(ResultSetMetaData.columnNullable));
    }
}

