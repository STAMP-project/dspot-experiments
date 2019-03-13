/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.jdbc.core.simple;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.metadata.TableMetaDataContext;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;


/**
 * Mock object based tests for TableMetaDataContext.
 *
 * @author Thomas Risberg
 */
public class TableMetaDataContextTests {
    private Connection connection;

    private DataSource dataSource;

    private DatabaseMetaData databaseMetaData;

    private TableMetaDataContext context = new TableMetaDataContext();

    @Test
    public void testMatchInParametersAndSqlTypeInfoWrapping() throws Exception {
        final String TABLE = "customers";
        final String USER = "me";
        ResultSet metaDataResultSet = Mockito.mock(ResultSet.class);
        BDDMockito.given(metaDataResultSet.next()).willReturn(true, false);
        BDDMockito.given(metaDataResultSet.getString("TABLE_SCHEM")).willReturn(USER);
        BDDMockito.given(metaDataResultSet.getString("TABLE_NAME")).willReturn(TABLE);
        BDDMockito.given(metaDataResultSet.getString("TABLE_TYPE")).willReturn("TABLE");
        ResultSet columnsResultSet = Mockito.mock(ResultSet.class);
        BDDMockito.given(columnsResultSet.next()).willReturn(true, true, true, true, false);
        BDDMockito.given(columnsResultSet.getString("COLUMN_NAME")).willReturn("id", "name", "customersince", "version");
        BDDMockito.given(columnsResultSet.getInt("DATA_TYPE")).willReturn(Types.INTEGER, Types.VARCHAR, Types.DATE, Types.NUMERIC);
        BDDMockito.given(columnsResultSet.getBoolean("NULLABLE")).willReturn(false, true, true, false);
        BDDMockito.given(databaseMetaData.getDatabaseProductName()).willReturn("MyDB");
        BDDMockito.given(databaseMetaData.getDatabaseProductName()).willReturn("1.0");
        BDDMockito.given(databaseMetaData.getUserName()).willReturn(USER);
        BDDMockito.given(databaseMetaData.storesLowerCaseIdentifiers()).willReturn(true);
        BDDMockito.given(databaseMetaData.getTables(null, null, TABLE, null)).willReturn(metaDataResultSet);
        BDDMockito.given(databaseMetaData.getColumns(null, USER, TABLE, null)).willReturn(columnsResultSet);
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("id", 1);
        map.addValue("name", "Sven");
        map.addValue("customersince", new Date());
        map.addValue("version", 0);
        map.registerSqlType("customersince", Types.DATE);
        map.registerSqlType("version", Types.NUMERIC);
        context.setTableName(TABLE);
        context.processMetaData(dataSource, new ArrayList(), new String[]{  });
        List<Object> values = context.matchInParameterValuesWithInsertColumns(map);
        Assert.assertEquals("wrong number of parameters: ", 4, values.size());
        Assert.assertTrue("id not wrapped with type info", ((values.get(0)) instanceof Number));
        Assert.assertTrue("name not wrapped with type info", ((values.get(1)) instanceof String));
        Assert.assertTrue("date wrapped with type info", ((values.get(2)) instanceof SqlParameterValue));
        Assert.assertTrue("version wrapped with type info", ((values.get(3)) instanceof SqlParameterValue));
        Mockito.verify(metaDataResultSet, Mockito.atLeastOnce()).next();
        Mockito.verify(columnsResultSet, Mockito.atLeastOnce()).next();
        Mockito.verify(metaDataResultSet).close();
        Mockito.verify(columnsResultSet).close();
    }

    @Test
    public void testTableWithSingleColumnGeneratedKey() throws Exception {
        final String TABLE = "customers";
        final String USER = "me";
        ResultSet metaDataResultSet = Mockito.mock(ResultSet.class);
        BDDMockito.given(metaDataResultSet.next()).willReturn(true, false);
        BDDMockito.given(metaDataResultSet.getString("TABLE_SCHEM")).willReturn(USER);
        BDDMockito.given(metaDataResultSet.getString("TABLE_NAME")).willReturn(TABLE);
        BDDMockito.given(metaDataResultSet.getString("TABLE_TYPE")).willReturn("TABLE");
        ResultSet columnsResultSet = Mockito.mock(ResultSet.class);
        BDDMockito.given(columnsResultSet.next()).willReturn(true, false);
        BDDMockito.given(columnsResultSet.getString("COLUMN_NAME")).willReturn("id");
        BDDMockito.given(columnsResultSet.getInt("DATA_TYPE")).willReturn(Types.INTEGER);
        BDDMockito.given(columnsResultSet.getBoolean("NULLABLE")).willReturn(false);
        BDDMockito.given(databaseMetaData.getDatabaseProductName()).willReturn("MyDB");
        BDDMockito.given(databaseMetaData.getDatabaseProductName()).willReturn("1.0");
        BDDMockito.given(databaseMetaData.getUserName()).willReturn(USER);
        BDDMockito.given(databaseMetaData.storesLowerCaseIdentifiers()).willReturn(true);
        BDDMockito.given(databaseMetaData.getTables(null, null, TABLE, null)).willReturn(metaDataResultSet);
        BDDMockito.given(databaseMetaData.getColumns(null, USER, TABLE, null)).willReturn(columnsResultSet);
        MapSqlParameterSource map = new MapSqlParameterSource();
        String[] keyCols = new String[]{ "id" };
        context.setTableName(TABLE);
        context.processMetaData(dataSource, new ArrayList(), keyCols);
        List<Object> values = context.matchInParameterValuesWithInsertColumns(map);
        String insertString = context.createInsertString(keyCols);
        Assert.assertEquals("wrong number of parameters: ", 0, values.size());
        Assert.assertEquals("empty insert not generated correctly", "INSERT INTO customers () VALUES()", insertString);
        Mockito.verify(metaDataResultSet, Mockito.atLeastOnce()).next();
        Mockito.verify(columnsResultSet, Mockito.atLeastOnce()).next();
        Mockito.verify(metaDataResultSet).close();
        Mockito.verify(columnsResultSet).close();
    }
}

