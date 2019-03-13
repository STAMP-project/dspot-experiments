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
package org.apache.calcite.prepare;


import CalcitePrepare.Context;
import SqlFunctionCategory.USER_DEFINED_FUNCTION;
import SqlFunctionCategory.USER_DEFINED_TABLE_FUNCTION;
import SqlSyntax.FUNCTION;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.jdbc.CalcitePrepare;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.TableFunction;
import org.apache.calcite.schema.impl.AbstractSchema;
import org.apache.calcite.schema.impl.TableFunctionImpl;
import org.apache.calcite.server.CalciteServerStatement;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.util.Smalls;
import org.junit.Test;


/**
 * Test for lookupOperatorOverloads() in {@link CalciteCatalogReader}.
 */
public class LookupOperatorOverloadsTest {
    @Test
    public void testIsUserDefined() throws SQLException {
        List<SqlFunctionCategory> cats = new ArrayList<>();
        for (SqlFunctionCategory c : SqlFunctionCategory.values()) {
            if (c.isUserDefined()) {
                cats.add(c);
            }
        }
        LookupOperatorOverloadsTest.check(cats, SqlFunctionCategory.USER_DEFINED_FUNCTION, SqlFunctionCategory.USER_DEFINED_PROCEDURE, SqlFunctionCategory.USER_DEFINED_CONSTRUCTOR, SqlFunctionCategory.USER_DEFINED_SPECIFIC_FUNCTION, SqlFunctionCategory.USER_DEFINED_TABLE_FUNCTION, SqlFunctionCategory.USER_DEFINED_TABLE_SPECIFIC_FUNCTION);
    }

    @Test
    public void testIsTableFunction() throws SQLException {
        List<SqlFunctionCategory> cats = new ArrayList<>();
        for (SqlFunctionCategory c : SqlFunctionCategory.values()) {
            if (c.isTableFunction()) {
                cats.add(c);
            }
        }
        LookupOperatorOverloadsTest.check(cats, SqlFunctionCategory.USER_DEFINED_TABLE_FUNCTION, SqlFunctionCategory.USER_DEFINED_TABLE_SPECIFIC_FUNCTION, SqlFunctionCategory.MATCH_RECOGNIZE);
    }

    @Test
    public void testIsSpecific() throws SQLException {
        List<SqlFunctionCategory> cats = new ArrayList<>();
        for (SqlFunctionCategory c : SqlFunctionCategory.values()) {
            if (c.isSpecific()) {
                cats.add(c);
            }
        }
        LookupOperatorOverloadsTest.check(cats, SqlFunctionCategory.USER_DEFINED_SPECIFIC_FUNCTION, SqlFunctionCategory.USER_DEFINED_TABLE_SPECIFIC_FUNCTION);
    }

    @Test
    public void testIsUserDefinedNotSpecificFunction() throws SQLException {
        List<SqlFunctionCategory> cats = new ArrayList<>();
        for (SqlFunctionCategory sqlFunctionCategory : SqlFunctionCategory.values()) {
            if (sqlFunctionCategory.isUserDefinedNotSpecificFunction()) {
                cats.add(sqlFunctionCategory);
            }
        }
        LookupOperatorOverloadsTest.check(cats, SqlFunctionCategory.USER_DEFINED_FUNCTION, SqlFunctionCategory.USER_DEFINED_TABLE_FUNCTION);
    }

    @Test
    public void test() throws SQLException {
        final String schemaName = "MySchema";
        final String funcName = "MyFUNC";
        final String anotherName = "AnotherFunc";
        try (Connection connection = DriverManager.getConnection("jdbc:calcite:")) {
            CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
            SchemaPlus rootSchema = calciteConnection.getRootSchema();
            SchemaPlus schema = rootSchema.add(schemaName, new AbstractSchema());
            final TableFunction table = TableFunctionImpl.create(Smalls.MAZE_METHOD);
            schema.add(funcName, table);
            schema.add(anotherName, table);
            final TableFunction table2 = TableFunctionImpl.create(Smalls.MAZE3_METHOD);
            schema.add(funcName, table2);
            final CalciteServerStatement statement = connection.createStatement().unwrap(CalciteServerStatement.class);
            final CalcitePrepare.Context prepareContext = statement.createPrepareContext();
            final JavaTypeFactory typeFactory = prepareContext.getTypeFactory();
            CalciteCatalogReader reader = new CalciteCatalogReader(prepareContext.getRootSchema(), ImmutableList.of(), typeFactory, prepareContext.config());
            final List<SqlOperator> operatorList = new ArrayList<>();
            SqlIdentifier myFuncIdentifier = new SqlIdentifier(Lists.newArrayList(schemaName, funcName), null, SqlParserPos.ZERO, null);
            reader.lookupOperatorOverloads(myFuncIdentifier, USER_DEFINED_TABLE_FUNCTION, FUNCTION, operatorList);
            checkFunctionType(2, funcName, operatorList);
            operatorList.clear();
            reader.lookupOperatorOverloads(myFuncIdentifier, USER_DEFINED_FUNCTION, FUNCTION, operatorList);
            checkFunctionType(0, null, operatorList);
            operatorList.clear();
            SqlIdentifier anotherFuncIdentifier = new SqlIdentifier(Lists.newArrayList(schemaName, anotherName), null, SqlParserPos.ZERO, null);
            reader.lookupOperatorOverloads(anotherFuncIdentifier, USER_DEFINED_TABLE_FUNCTION, FUNCTION, operatorList);
            checkFunctionType(1, anotherName, operatorList);
        }
    }
}

/**
 * End LookupOperatorOverloadsTest.java
 */
