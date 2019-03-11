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
package org.apache.calcite.test;


import com.google.common.collect.ImmutableMultiset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import org.apache.calcite.jdbc.CalciteConnection;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for CALCITE-542.
 */
public class RelMdColumnOriginsTest {
    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-542">[CALCITE-542]
     * Support for Aggregate with grouping sets in RelMdColumnOrigins</a>.
     */
    @Test
    public void testQueryWithAggregateGroupingSets() throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:calcite:");
        CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
        calciteConnection.getRootSchema().add("T1", new TableInRootSchemaTest.SimpleTable());
        Statement statement = calciteConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(("SELECT TABLE1.ID, TABLE2.ID FROM " + ((((("(SELECT GROUPING(A) AS ID FROM T1 " + "GROUP BY ROLLUP(A,B)) TABLE1 ") + "JOIN ") + "(SELECT GROUPING(A) AS ID FROM T1 ") + "GROUP BY ROLLUP(A,B)) TABLE2 ") + "ON TABLE1.ID = TABLE2.ID")));
        final String result1 = "ID=0; ID=0";
        final String result2 = "ID=1; ID=1";
        final ImmutableMultiset<String> expectedResult = ImmutableMultiset.<String>builder().addCopies(result1, 25).add(result2).build();
        Assert.assertThat(CalciteAssert.toSet(resultSet), CoreMatchers.equalTo(expectedResult));
        final ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        Assert.assertThat(resultSetMetaData.getColumnName(1), CoreMatchers.equalTo("ID"));
        Assert.assertThat(resultSetMetaData.getTableName(1), CoreMatchers.nullValue());
        Assert.assertThat(resultSetMetaData.getSchemaName(1), CoreMatchers.nullValue());
        Assert.assertThat(resultSetMetaData.getColumnName(2), CoreMatchers.equalTo("ID"));
        Assert.assertThat(resultSetMetaData.getTableName(2), CoreMatchers.nullValue());
        Assert.assertThat(resultSetMetaData.getSchemaName(2), CoreMatchers.nullValue());
        resultSet.close();
        statement.close();
        connection.close();
    }
}

/**
 * End RelMdColumnOriginsTest.java
 */
