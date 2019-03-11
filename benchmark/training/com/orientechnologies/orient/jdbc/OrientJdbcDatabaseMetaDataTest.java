/**
 * Copyright 2010-2016 OrientDB LTD (http://orientdb.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 * <p>
 * For more information: http://orientdb.com
 */
package com.orientechnologies.orient.jdbc;


import com.orientechnologies.orient.core.OConstants;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class OrientJdbcDatabaseMetaDataTest extends OrientJdbcDbPerClassTemplateTest {
    private DatabaseMetaData metaData;

    @Test
    public void verifyDriverAndDatabaseVersions() throws SQLException {
        // assertEquals("memory:" + name.getMethodName(), metaData.getURL());
        Assert.assertEquals("admin", metaData.getUserName());
        Assert.assertEquals("OrientDB", metaData.getDatabaseProductName());
        Assert.assertEquals(OConstants.getVersion(), metaData.getDatabaseProductVersion());
        Assert.assertEquals(3, metaData.getDatabaseMajorVersion());
        Assert.assertEquals(1, metaData.getDatabaseMinorVersion());
        Assert.assertEquals("OrientDB JDBC Driver", metaData.getDriverName());
        Assert.assertEquals((("OrientDB " + (OConstants.getVersion())) + " JDBC Driver"), metaData.getDriverVersion());
        Assert.assertEquals(3, metaData.getDriverMajorVersion());
        Assert.assertEquals(1, metaData.getDriverMinorVersion());
    }

    @Test
    public void shouldRetrievePrimaryKeysMetadata() throws SQLException {
        ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, "Item");
        Assert.assertTrue(primaryKeys.next());
        Assert.assertEquals("intKey", primaryKeys.getString(4));
        Assert.assertEquals("Item.intKey", primaryKeys.getString(6));
        Assert.assertEquals(1, primaryKeys.getInt(5));
        Assert.assertTrue(primaryKeys.next());
        Assert.assertEquals("stringKey", primaryKeys.getString("COLUMN_NAME"));
        Assert.assertEquals("Item.stringKey", primaryKeys.getString("PK_NAME"));
        Assert.assertEquals(1, primaryKeys.getInt("KEY_SEQ"));
    }

    @Test
    public void shouldRetrieveTableTypes() throws SQLException {
        ResultSet tableTypes = metaData.getTableTypes();
        // Assertions.
        Assert.assertTrue(tableTypes.next());
        Assert.assertEquals("TABLE", tableTypes.getString(1));
        Assert.assertTrue(tableTypes.next());
        Assert.assertEquals("SYSTEM TABLE", tableTypes.getString(1));
        Assert.assertFalse(tableTypes.next());
    }

    @Test
    public void shouldRetrieveKeywords() throws SQLException {
        final String keywordsStr = metaData.getSQLKeywords();
        Assert.assertNotNull(keywordsStr);
        Assert.assertThat(Arrays.asList(keywordsStr.toUpperCase(Locale.ENGLISH).split(",\\s*"))).contains("TRAVERSE");
    }

    @Test
    public void shouldRetrieveUniqueIndexInfoForTable() throws Exception {
        ResultSet indexInfo = metaData.getIndexInfo("OrientJdbcDatabaseMetaDataTest", "OrientJdbcDatabaseMetaDataTest", "Item", true, false);
        indexInfo.next();
        Assert.assertThat(indexInfo.getString("INDEX_NAME")).isEqualTo("Item.intKey");
        Assert.assertThat(indexInfo.getBoolean("NON_UNIQUE")).isFalse();
        indexInfo.next();
        Assert.assertThat(indexInfo.getString("INDEX_NAME")).isEqualTo("Item.stringKey");
        Assert.assertThat(indexInfo.getBoolean("NON_UNIQUE")).isFalse();
    }

    @Test
    public void getFields() throws SQLException {
        ResultSet rs = OrientJdbcDbPerClassTemplateTest.conn.createStatement().executeQuery("select from OUser");
        ResultSetMetaData rsMetaData = rs.getMetaData();
        int cc = rsMetaData.getColumnCount();
        Set<String> colset = new HashSet<>();
        List<Map<String, Object>> columns = new ArrayList<>(cc);
        for (int i = 1; i <= cc; i++) {
            String name = rsMetaData.getColumnLabel(i);
            // if (colset.contains(name))
            // continue;
            colset.add(name);
            Map<String, Object> field = new HashMap<>();
            field.put("name", name);
            try {
                String catalog = rsMetaData.getCatalogName(i);
                String schema = rsMetaData.getSchemaName(i);
                String table = rsMetaData.getTableName(i);
                ResultSet rsmc = OrientJdbcDbPerClassTemplateTest.conn.getMetaData().getColumns(catalog, schema, table, name);
                while (rsmc.next()) {
                    field.put("description", rsmc.getString("REMARKS"));
                    break;
                } 
            } catch (SQLException se) {
                se.printStackTrace();
            }
            columns.add(field);
        }
        for (Map<String, Object> c : columns) {
            System.out.println(c);
        }
    }

    @Test
    public void shouldFetchAllTables() throws SQLException {
        ResultSet rs = metaData.getTables(null, null, null, null);
        int tableCount = sizeOf(rs);
        Assert.assertThat(tableCount).isEqualTo(16);
    }

    @Test
    public void shouldFillSchemaAndCatalogWithDatabaseName() throws SQLException {
        ResultSet rs = metaData.getTables(null, null, null, null);
        while (rs.next()) {
            Assert.assertThat(rs.getString("TABLE_SCHEM")).isEqualTo("perClassTestDatabase");
            Assert.assertThat(rs.getString("TABLE_CAT")).isEqualTo("perClassTestDatabase");
        } 
    }

    @Test
    public void shouldGetAllTablesFilteredByAllTypes() throws SQLException {
        ResultSet rs = metaData.getTableTypes();
        List<String> tableTypes = new ArrayList<>(2);
        while (rs.next()) {
            tableTypes.add(rs.getString(1));
        } 
        rs = metaData.getTables(null, null, null, tableTypes.toArray(new String[2]));
        int tableCount = sizeOf(rs);
        Assert.assertThat(tableCount).isEqualTo(16);
    }

    @Test
    public void getNoTablesFilteredByEmptySetOfTypes() throws SQLException {
        final ResultSet rs = metaData.getTables(null, null, null, new String[0]);
        int tableCount = sizeOf(rs);
        Assert.assertThat(tableCount).isEqualTo(0);
    }

    @Test
    public void getSingleTable() throws SQLException {
        ResultSet rs = metaData.getTables(null, null, "ouser", null);
        rs.next();
        Assert.assertThat(rs.getString("TABLE_NAME")).isEqualTo("OUser");
        Assert.assertThat(rs.getString("TABLE_CAT")).isEqualTo("perClassTestDatabase");
        Assert.assertThat(rs.getString("TABLE_SCHEM")).isEqualTo("perClassTestDatabase");
        Assert.assertThat(rs.getString("REMARKS")).isNull();
        Assert.assertThat(rs.getString("REF_GENERATION")).isNull();
        Assert.assertThat(rs.getString("TYPE_NAME")).isNull();
        Assert.assertThat(rs.next()).isFalse();
    }

    @Test
    public void shouldGetSingleColumnOfArticle() throws SQLException {
        ResultSet rs = metaData.getColumns(null, null, "Article", "uuid");
        rs.next();
        Assert.assertThat(rs.getString("TABLE_NAME")).isEqualTo("Article");
        Assert.assertThat(rs.getString("COLUMN_NAME")).isEqualTo("uuid");
        Assert.assertThat(rs.getString("TYPE_NAME")).isEqualTo("LONG");
        Assert.assertThat(rs.getInt("DATA_TYPE")).isEqualTo((-5));
        Assert.assertThat(rs.next()).isFalse();
    }

    @Test
    public void shouldGetAllColumnsOfArticle() throws SQLException {
        ResultSet rs = metaData.getColumns(null, null, "Article", null);
        while (rs.next()) {
            Assert.assertThat(rs.getString("TABLE_NAME")).isEqualTo("Article");
            Assert.assertThat(rs.getString("COLUMN_NAME")).isIn("date", "uuid", "author", "title", "content");
            // System.out.println("rs = " + rs.getInt("DATA_TYPE"));
            Assert.assertThat(rs.getInt("DATA_TYPE")).isIn((-5), 12, 91, 2000);
            Assert.assertThat(rs.getString("TYPE_NAME")).isIn("LONG", "LINK", "DATE", "STRING", "INTEGER");
        } 
    }

    @Test
    public void shouldGetAllIndexesOnArticle() throws Exception {
        ResultSet rs = metaData.getIndexInfo(null, null, "Article", true, true);
        rs.next();
        Assert.assertThat(rs.getString("COLUMN_NAME")).isEqualTo("uuid");
        Assert.assertThat(rs.getString("INDEX_NAME")).isEqualTo("Article.uuid");
        Assert.assertThat(rs.getBoolean("NON_UNIQUE")).isFalse();
    }

    @Test
    public void shouldGetPrimaryKeyOfArticle() throws Exception {
        ResultSet rs = metaData.getPrimaryKeys(null, null, "Article");
        rs.next();
        Assert.assertThat(rs.getString("TABLE_NAME")).isEqualTo("Article");
        Assert.assertThat(rs.getString("COLUMN_NAME")).isEqualTo("uuid");
        Assert.assertThat(rs.getString("PK_NAME")).isEqualTo("Article.uuid");
        Assert.assertThat(rs.getInt("KEY_SEQ")).isEqualTo(1);
    }
}

