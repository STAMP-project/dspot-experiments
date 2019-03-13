/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.shardingjdbc.executor;


import SQLType.DML;
import SQLType.DQL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.shardingsphere.core.merger.QueryResult;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public final class PreparedStatementExecutorTest extends AbstractBaseExecutorTest {
    private static final String DQL_SQL = "SELECT * FROM table_x";

    private static final String DML_SQL = "DELETE FROM table_x";

    private PreparedStatementExecutor actual;

    @Test
    public void assertNoStatement() throws SQLException {
        Assert.assertFalse(actual.execute());
        Assert.assertThat(actual.executeUpdate(), CoreMatchers.is(0));
        Assert.assertThat(actual.executeQuery().size(), CoreMatchers.is(0));
    }

    @Test
    public void assertExecuteQueryForSinglePreparedStatementSuccess() throws SQLException {
        PreparedStatement preparedStatement = getPreparedStatement();
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        ResultSetMetaData resultSetMetaData = Mockito.mock(ResultSetMetaData.class);
        Mockito.when(resultSetMetaData.getColumnName(1)).thenReturn("column");
        Mockito.when(resultSetMetaData.getTableName(1)).thenReturn("table_x");
        Mockito.when(resultSetMetaData.getColumnCount()).thenReturn(1);
        Mockito.when(resultSet.getString(1)).thenReturn("value");
        Mockito.when(resultSet.getMetaData()).thenReturn(resultSetMetaData);
        Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);
        setExecuteGroups(Collections.singletonList(preparedStatement), DQL);
        Assert.assertThat(((String) (actual.executeQuery().iterator().next().getValue(1, String.class))), CoreMatchers.is("decryptValue"));
    }

    @Test
    public void assertExecuteQueryForMultiplePreparedStatementsSuccess() throws SQLException {
        PreparedStatement preparedStatement1 = getPreparedStatement();
        PreparedStatement preparedStatement2 = getPreparedStatement();
        ResultSet resultSet1 = Mockito.mock(ResultSet.class);
        ResultSet resultSet2 = Mockito.mock(ResultSet.class);
        ResultSetMetaData resultSetMetaData = Mockito.mock(ResultSetMetaData.class);
        Mockito.when(resultSetMetaData.getColumnName(1)).thenReturn("column");
        Mockito.when(resultSetMetaData.getTableName(1)).thenReturn("table_x");
        Mockito.when(resultSetMetaData.getColumnCount()).thenReturn(1);
        Mockito.when(resultSet1.getMetaData()).thenReturn(resultSetMetaData);
        Mockito.when(resultSet2.getMetaData()).thenReturn(resultSetMetaData);
        Mockito.when(resultSet1.getInt(1)).thenReturn(1);
        Mockito.when(resultSet2.getInt(1)).thenReturn(2);
        Mockito.when(preparedStatement1.executeQuery()).thenReturn(resultSet1);
        Mockito.when(preparedStatement2.executeQuery()).thenReturn(resultSet2);
        setExecuteGroups(Arrays.asList(preparedStatement1, preparedStatement2), DQL);
        List<QueryResult> result = actual.executeQuery();
        for (QueryResult each : result) {
            Assert.assertThat(String.valueOf(each.getValue(1, int.class)), CoreMatchers.is("decryptValue"));
        }
        Mockito.verify(preparedStatement1).executeQuery();
        Mockito.verify(preparedStatement2).executeQuery();
    }

    @Test
    public void assertExecuteQueryForSinglePreparedStatementFailure() throws SQLException {
        PreparedStatement preparedStatement = getPreparedStatement();
        SQLException exp = new SQLException();
        Mockito.when(preparedStatement.executeQuery()).thenThrow(exp);
        setExecuteGroups(Collections.singletonList(preparedStatement), DQL);
        Assert.assertThat(actual.executeQuery(), CoreMatchers.is(Collections.singletonList(((QueryResult) (null)))));
        Mockito.verify(preparedStatement).executeQuery();
    }

    @Test
    public void assertExecuteQueryForMultiplePreparedStatementsFailure() throws SQLException {
        PreparedStatement preparedStatement1 = getPreparedStatement();
        PreparedStatement preparedStatement2 = getPreparedStatement();
        SQLException exp = new SQLException();
        Mockito.when(preparedStatement1.executeQuery()).thenThrow(exp);
        Mockito.when(preparedStatement2.executeQuery()).thenThrow(exp);
        setExecuteGroups(Arrays.asList(preparedStatement1, preparedStatement2), DQL);
        List<QueryResult> actualResultSets = actual.executeQuery();
        Assert.assertThat(actualResultSets, CoreMatchers.is(Arrays.asList(((QueryResult) (null)), null)));
        Mockito.verify(preparedStatement1).executeQuery();
        Mockito.verify(preparedStatement2).executeQuery();
    }

    @Test
    public void assertExecuteUpdateForSinglePreparedStatementSuccess() throws SQLException {
        PreparedStatement preparedStatement = getPreparedStatement();
        Mockito.when(preparedStatement.executeUpdate()).thenReturn(10);
        setExecuteGroups(Collections.singletonList(preparedStatement), DML);
        Assert.assertThat(actual.executeUpdate(), CoreMatchers.is(10));
        Mockito.verify(preparedStatement).executeUpdate();
    }

    @Test
    public void assertExecuteUpdateForMultiplePreparedStatementsSuccess() throws SQLException {
        PreparedStatement preparedStatement1 = getPreparedStatement();
        PreparedStatement preparedStatement2 = getPreparedStatement();
        Mockito.when(preparedStatement1.executeUpdate()).thenReturn(10);
        Mockito.when(preparedStatement2.executeUpdate()).thenReturn(20);
        setExecuteGroups(Arrays.asList(preparedStatement1, preparedStatement2), DML);
        Assert.assertThat(actual.executeUpdate(), CoreMatchers.is(30));
        Mockito.verify(preparedStatement1).executeUpdate();
        Mockito.verify(preparedStatement2).executeUpdate();
    }

    @Test
    public void assertExecuteUpdateForSinglePreparedStatementFailure() throws SQLException {
        PreparedStatement preparedStatement = getPreparedStatement();
        SQLException exp = new SQLException();
        Mockito.when(preparedStatement.executeUpdate()).thenThrow(exp);
        setExecuteGroups(Collections.singletonList(preparedStatement), DML);
        Assert.assertThat(actual.executeUpdate(), CoreMatchers.is(0));
        Mockito.verify(preparedStatement).executeUpdate();
    }

    @Test
    public void assertExecuteUpdateForMultiplePreparedStatementsFailure() throws SQLException {
        PreparedStatement preparedStatement1 = getPreparedStatement();
        PreparedStatement preparedStatement2 = getPreparedStatement();
        SQLException exp = new SQLException();
        Mockito.when(preparedStatement1.executeUpdate()).thenThrow(exp);
        Mockito.when(preparedStatement2.executeUpdate()).thenThrow(exp);
        setExecuteGroups(Arrays.asList(preparedStatement1, preparedStatement2), DML);
        Assert.assertThat(actual.executeUpdate(), CoreMatchers.is(0));
        Mockito.verify(preparedStatement1).executeUpdate();
        Mockito.verify(preparedStatement2).executeUpdate();
    }

    @Test
    public void assertExecuteForSinglePreparedStatementSuccessWithDML() throws SQLException {
        PreparedStatement preparedStatement = getPreparedStatement();
        Mockito.when(preparedStatement.execute()).thenReturn(false);
        setExecuteGroups(Collections.singletonList(preparedStatement), DML);
        Assert.assertFalse(actual.execute());
        Mockito.verify(preparedStatement).execute();
    }

    @Test
    public void assertExecuteForMultiplePreparedStatementsSuccessWithDML() throws SQLException {
        PreparedStatement preparedStatement1 = getPreparedStatement();
        PreparedStatement preparedStatement2 = getPreparedStatement();
        Mockito.when(preparedStatement1.execute()).thenReturn(false);
        Mockito.when(preparedStatement2.execute()).thenReturn(false);
        setExecuteGroups(Arrays.asList(preparedStatement1, preparedStatement2), DML);
        Assert.assertFalse(actual.execute());
        Mockito.verify(preparedStatement1).execute();
        Mockito.verify(preparedStatement2).execute();
    }

    @Test
    public void assertExecuteForSinglePreparedStatementFailureWithDML() throws SQLException {
        PreparedStatement preparedStatement = getPreparedStatement();
        SQLException exp = new SQLException();
        Mockito.when(preparedStatement.execute()).thenThrow(exp);
        setExecuteGroups(Collections.singletonList(preparedStatement), DML);
        Assert.assertFalse(actual.execute());
        Mockito.verify(preparedStatement).execute();
    }

    @Test
    public void assertExecuteForMultiplePreparedStatementsFailureWithDML() throws SQLException {
        PreparedStatement preparedStatement1 = getPreparedStatement();
        PreparedStatement preparedStatement2 = getPreparedStatement();
        SQLException exp = new SQLException();
        Mockito.when(preparedStatement1.execute()).thenThrow(exp);
        Mockito.when(preparedStatement2.execute()).thenThrow(exp);
        setExecuteGroups(Arrays.asList(preparedStatement1, preparedStatement2), DML);
        Assert.assertFalse(actual.execute());
        Mockito.verify(preparedStatement1).execute();
        Mockito.verify(preparedStatement2).execute();
    }

    @Test
    public void assertExecuteForSinglePreparedStatementWithDQL() throws SQLException {
        PreparedStatement preparedStatement = getPreparedStatement();
        Mockito.when(preparedStatement.execute()).thenReturn(true);
        setExecuteGroups(Collections.singletonList(preparedStatement), DQL);
        Assert.assertTrue(actual.execute());
        Mockito.verify(preparedStatement).execute();
    }

    @Test
    public void assertExecuteForMultiplePreparedStatements() throws SQLException {
        PreparedStatement preparedStatement1 = getPreparedStatement();
        PreparedStatement preparedStatement2 = getPreparedStatement();
        Mockito.when(preparedStatement1.execute()).thenReturn(true);
        Mockito.when(preparedStatement2.execute()).thenReturn(true);
        setExecuteGroups(Arrays.asList(preparedStatement1, preparedStatement2), DQL);
        Assert.assertTrue(actual.execute());
        Mockito.verify(preparedStatement1).execute();
        Mockito.verify(preparedStatement2).execute();
    }
}

