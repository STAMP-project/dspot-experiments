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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.apache.drill.categories.JdbcTest;
import org.apache.drill.jdbc.JdbcTestBase;
import org.apache.drill.test.TestTools;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestRule;


/**
 * Basic (spot-check/incomplete) tests for DRILL-2128 bugs (many
 * DatabaseMetaData.getColumns(...) result table problems).
 */
@Category(JdbcTest.class)
public class Drill2128GetColumnsDataTypeNotTypeCodeIntBugsTest extends JdbcTestBase {
    private static Connection connection;

    private static DatabaseMetaData dbMetadata;

    @Rule
    public final TestRule TIMEOUT = /* ms */
    TestTools.getTimeoutRule(120000);

    /**
     * Basic test that column DATA_TYPE is integer type codes (not strings such
     * as "VARCHAR" or "INTEGER").
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testColumn_DATA_TYPE_isInteger() throws Exception {
        // Get metadata for some column(s).
        final ResultSet columns = Drill2128GetColumnsDataTypeNotTypeCodeIntBugsTest.dbMetadata.getColumns(null, null, null, null);
        Assert.assertTrue("DatabaseMetaData.getColumns(...) returned no rows", columns.next());
        do {
            // DATA_TYPE should be INTEGER, so getInt( "DATA_TYPE" ) should succeed:
            final int typeCode1 = columns.getInt("DATA_TYPE");
            // DATA_TYPE should be at ordinal position 5 (seemingly):
            Assert.assertThat("Column 5's label", columns.getMetaData().getColumnLabel(5), CoreMatchers.equalTo("DATA_TYPE"));
            // Also, getInt( 5 ) should succeed and return the same type code as above:
            final int typeCode2 = columns.getInt(5);
            Assert.assertThat("getInt( 5 ) (expected to be same as getInt( \"DATA_TYPE\" ))", typeCode2, CoreMatchers.equalTo(typeCode1));
            // Type code should be one of java.sql.Types.*:
            Assert.assertThat(typeCode1, // List is from java.sql.Types
            // TODO:  Resolve:  Is it not clear whether Types.NULL can re-
            // present a type (e.g., the type of NULL), or whether a column
            // can ever have that type, and therefore whether Types.NULL
            // can appear.  Currently, exclude NULL so we'll notice if it
            // does appear:
            // No equalTo( Types.NULL ).
            CoreMatchers.anyOf(CoreMatchers.equalTo(Types.ARRAY), CoreMatchers.equalTo(Types.BIGINT), CoreMatchers.equalTo(Types.BINARY), CoreMatchers.equalTo(Types.BIT), CoreMatchers.equalTo(Types.BLOB), CoreMatchers.equalTo(Types.BOOLEAN), CoreMatchers.equalTo(Types.CHAR), CoreMatchers.equalTo(Types.CLOB), CoreMatchers.equalTo(Types.DATALINK), CoreMatchers.equalTo(Types.DATE), CoreMatchers.equalTo(Types.DECIMAL), CoreMatchers.equalTo(Types.DISTINCT), CoreMatchers.equalTo(Types.DOUBLE), CoreMatchers.equalTo(Types.FLOAT), CoreMatchers.equalTo(Types.INTEGER), CoreMatchers.equalTo(Types.JAVA_OBJECT), CoreMatchers.equalTo(Types.LONGNVARCHAR), CoreMatchers.equalTo(Types.LONGVARBINARY), CoreMatchers.equalTo(Types.LONGVARCHAR), CoreMatchers.equalTo(Types.NCHAR), CoreMatchers.equalTo(Types.NCLOB), CoreMatchers.equalTo(Types.NUMERIC), CoreMatchers.equalTo(Types.NVARCHAR), CoreMatchers.equalTo(Types.OTHER), CoreMatchers.equalTo(Types.REAL), CoreMatchers.equalTo(Types.REF), CoreMatchers.equalTo(Types.ROWID), CoreMatchers.equalTo(Types.SMALLINT), CoreMatchers.equalTo(Types.SQLXML), CoreMatchers.equalTo(Types.STRUCT), CoreMatchers.equalTo(Types.TIME), CoreMatchers.equalTo(Types.TIMESTAMP), CoreMatchers.equalTo(Types.TINYINT), CoreMatchers.equalTo(Types.VARBINARY), CoreMatchers.equalTo(Types.VARCHAR)));
        } while (columns.next() );
    }

    /**
     * Basic test that column TYPE_NAME exists and is strings (such "INTEGER").
     */
    @Test
    public void testColumn_TYPE_NAME_isString() throws Exception {
        // Get metadata for some INTEGER column.
        final ResultSet columns = Drill2128GetColumnsDataTypeNotTypeCodeIntBugsTest.dbMetadata.getColumns(null, "INFORMATION_SCHEMA", "COLUMNS", "ORDINAL_POSITION");
        Assert.assertTrue("DatabaseMetaData.getColumns(...) returned no rows", columns.next());
        // TYPE_NAME should be character string for type name "INTEGER", so
        // getString( "TYPE_NAME" ) should succeed and getInt( "TYPE_NAME" ) should
        // fail:
        final String typeName1 = columns.getString("TYPE_NAME");
        Assert.assertThat("getString( \"TYPE_NAME\" )", typeName1, CoreMatchers.equalTo("INTEGER"));
        try {
            final int unexpected = columns.getInt("TYPE_NAME");
            Assert.fail((("getInt( \"TYPE_NAME\" ) didn\'t throw exception (and returned " + unexpected) + ")"));
        } catch (SQLException e) {
            // Expected.
        }
        // TYPE_NAME should be at ordinal position 6 (seemingly):
        Assert.assertThat("Column 6's label", columns.getMetaData().getColumnLabel(6), CoreMatchers.equalTo("TYPE_NAME"));
        // Also, getString( 6 ) should succeed and return the same type name as above:
        final String typeName2 = columns.getString(6);
        Assert.assertThat("getString( 6 ) (expected to be same as getString( \"TYPE_NAME\" ))", typeName2, CoreMatchers.equalTo(typeName1));
    }
}

