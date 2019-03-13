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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.shardingsphere.core.executor.sql.execute.threadlocal.ExecutorExceptionHandler;
import org.apache.shardingsphere.core.merger.QueryResult;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public final class StatementExecutorTest extends AbstractBaseExecutorTest {
    private static final String DQL_SQL = "SELECT * FROM table_x";

    private static final String DML_SQL = "DELETE FROM table_x";

    private StatementExecutor actual;

    @Test
    public void assertNoStatement() throws SQLException {
        Assert.assertFalse(actual.execute());
        Assert.assertThat(actual.executeUpdate(), CoreMatchers.is(0));
        Assert.assertThat(actual.executeQuery().size(), CoreMatchers.is(0));
    }

    @Test
    public void assertExecuteQueryForSingleStatementSuccess() throws SQLException {
        Statement statement = getStatement();
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        ResultSetMetaData resultSetMetaData = Mockito.mock(ResultSetMetaData.class);
        Mockito.when(resultSetMetaData.getColumnName(1)).thenReturn("column");
        Mockito.when(resultSetMetaData.getTableName(1)).thenReturn("table_x");
        Mockito.when(resultSetMetaData.getColumnCount()).thenReturn(1);
        Mockito.when(resultSet.getString(1)).thenReturn("value");
        Mockito.when(resultSet.getMetaData()).thenReturn(resultSetMetaData);
        Mockito.when(statement.executeQuery(StatementExecutorTest.DQL_SQL)).thenReturn(resultSet);
        setExecuteGroups(Collections.singletonList(statement), DQL);
        Assert.assertThat(((String) (actual.executeQuery().iterator().next().getValue(1, String.class))), CoreMatchers.is("decryptValue"));
        Mockito.verify(statement).executeQuery(StatementExecutorTest.DQL_SQL);
    }

    @Test
    public void assertExecuteQueryForMultipleStatementsSuccess() throws SQLException {
        Statement statement1 = getStatement();
        Statement statement2 = getStatement();
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
        Mockito.when(statement1.executeQuery(StatementExecutorTest.DQL_SQL)).thenReturn(resultSet1);
        Mockito.when(statement2.executeQuery(StatementExecutorTest.DQL_SQL)).thenReturn(resultSet2);
        setExecuteGroups(Arrays.asList(statement1, statement2), DQL);
        List<QueryResult> result = actual.executeQuery();
        for (QueryResult each : result) {
            Assert.assertThat(String.valueOf(each.getValue(1, int.class)), CoreMatchers.is("decryptValue"));
        }
        Mockito.verify(statement1).executeQuery(StatementExecutorTest.DQL_SQL);
        Mockito.verify(statement2).executeQuery(StatementExecutorTest.DQL_SQL);
    }

    @Test
    public void assertExecuteQueryForSingleStatementFailure() throws SQLException {
        Statement statement = getStatement();
        SQLException exp = new SQLException();
        Mockito.when(statement.executeQuery(StatementExecutorTest.DQL_SQL)).thenThrow(exp);
        setExecuteGroups(Collections.singletonList(statement), DQL);
        Assert.assertThat(actual.executeQuery(), CoreMatchers.is(Collections.singletonList(((QueryResult) (null)))));
        Mockito.verify(statement).executeQuery(StatementExecutorTest.DQL_SQL);
    }

    @Test
    public void assertExecuteQueryForMultipleStatementsFailure() throws SQLException {
        Statement statement1 = getStatement();
        Statement statement2 = getStatement();
        SQLException exp = new SQLException();
        Mockito.when(statement1.executeQuery(StatementExecutorTest.DQL_SQL)).thenThrow(exp);
        Mockito.when(statement2.executeQuery(StatementExecutorTest.DQL_SQL)).thenThrow(exp);
        setExecuteGroups(Arrays.asList(statement1, statement2), DQL);
        List<QueryResult> actualResultSets = actual.executeQuery();
        Assert.assertThat(actualResultSets, CoreMatchers.is(Arrays.asList(((QueryResult) (null)), null)));
        Mockito.verify(statement1).executeQuery(StatementExecutorTest.DQL_SQL);
        Mockito.verify(statement2).executeQuery(StatementExecutorTest.DQL_SQL);
    }

    @Test
    public void assertExecuteUpdateForSingleStatementSuccess() throws SQLException {
        Statement statement = getStatement();
        Mockito.when(statement.executeUpdate(StatementExecutorTest.DML_SQL)).thenReturn(10);
        setExecuteGroups(Collections.singletonList(statement), DML);
        Assert.assertThat(actual.executeUpdate(), CoreMatchers.is(10));
        Mockito.verify(statement).executeUpdate(StatementExecutorTest.DML_SQL);
    }

    @Test
    public void assertExecuteUpdateForMultipleStatementsSuccess() throws SQLException {
        Statement statement1 = getStatement();
        Statement statement2 = getStatement();
        Mockito.when(statement1.executeUpdate(StatementExecutorTest.DML_SQL)).thenReturn(10);
        Mockito.when(statement2.executeUpdate(StatementExecutorTest.DML_SQL)).thenReturn(20);
        setExecuteGroups(Arrays.asList(statement1, statement2), DML);
        Assert.assertThat(actual.executeUpdate(), CoreMatchers.is(30));
        Mockito.verify(statement1).executeUpdate(StatementExecutorTest.DML_SQL);
        Mockito.verify(statement2).executeUpdate(StatementExecutorTest.DML_SQL);
    }

    @Test
    public void assertExecuteUpdateForSingleStatementFailure() throws SQLException {
        Statement statement = getStatement();
        SQLException exp = new SQLException();
        Mockito.when(statement.executeUpdate(StatementExecutorTest.DML_SQL)).thenThrow(exp);
        setExecuteGroups(Collections.singletonList(statement), DML);
        Assert.assertThat(actual.executeUpdate(), CoreMatchers.is(0));
        Mockito.verify(statement).executeUpdate(StatementExecutorTest.DML_SQL);
    }

    @Test
    public void assertExecuteUpdateForMultipleStatementsFailure() throws SQLException {
        Statement statement1 = getStatement();
        Statement statement2 = getStatement();
        SQLException exp = new SQLException();
        Mockito.when(statement1.executeUpdate(StatementExecutorTest.DML_SQL)).thenThrow(exp);
        Mockito.when(statement2.executeUpdate(StatementExecutorTest.DML_SQL)).thenThrow(exp);
        setExecuteGroups(Arrays.asList(statement1, statement2), DML);
        Assert.assertThat(actual.executeUpdate(), CoreMatchers.is(0));
        Mockito.verify(statement1).executeUpdate(StatementExecutorTest.DML_SQL);
        Mockito.verify(statement2).executeUpdate(StatementExecutorTest.DML_SQL);
    }

    @Test
    public void assertExecuteUpdateWithAutoGeneratedKeys() throws SQLException {
        Statement statement = getStatement();
        Mockito.when(statement.executeUpdate(StatementExecutorTest.DML_SQL, Statement.NO_GENERATED_KEYS)).thenReturn(10);
        setExecuteGroups(Collections.singletonList(statement), DML);
        Assert.assertThat(actual.executeUpdate(Statement.NO_GENERATED_KEYS), CoreMatchers.is(10));
        Mockito.verify(statement).executeUpdate(StatementExecutorTest.DML_SQL, Statement.NO_GENERATED_KEYS);
    }

    @Test
    public void assertExecuteUpdateWithColumnIndexes() throws SQLException {
        Statement statement = getStatement();
        Mockito.when(statement.executeUpdate(StatementExecutorTest.DML_SQL, new int[]{ 1 })).thenReturn(10);
        setExecuteGroups(Collections.singletonList(statement), DML);
        Assert.assertThat(actual.executeUpdate(new int[]{ 1 }), CoreMatchers.is(10));
        Mockito.verify(statement).executeUpdate(StatementExecutorTest.DML_SQL, new int[]{ 1 });
    }

    @Test
    public void assertExecuteUpdateWithColumnNames() throws SQLException {
        Statement statement = getStatement();
        Mockito.when(statement.executeUpdate(StatementExecutorTest.DML_SQL, new String[]{ "col" })).thenReturn(10);
        setExecuteGroups(Collections.singletonList(statement), DML);
        Assert.assertThat(actual.executeUpdate(new String[]{ "col" }), CoreMatchers.is(10));
        Mockito.verify(statement).executeUpdate(StatementExecutorTest.DML_SQL, new String[]{ "col" });
    }

    @Test
    public void assertExecuteForSingleStatementSuccessWithDML() throws SQLException {
        Statement statement = getStatement();
        Mockito.when(statement.execute(StatementExecutorTest.DML_SQL)).thenReturn(false);
        setExecuteGroups(Collections.singletonList(statement), DML);
        Assert.assertFalse(actual.execute());
        Mockito.verify(statement).execute(StatementExecutorTest.DML_SQL);
    }

    @Test
    public void assertExecuteForMultipleStatementsSuccessWithDML() throws SQLException {
        Statement statement1 = getStatement();
        Statement statement2 = getStatement();
        Mockito.when(statement1.execute(StatementExecutorTest.DML_SQL)).thenReturn(false);
        Mockito.when(statement2.execute(StatementExecutorTest.DML_SQL)).thenReturn(false);
        setExecuteGroups(Arrays.asList(statement1, statement2), DML);
        Assert.assertFalse(actual.execute());
        Mockito.verify(statement1).execute(StatementExecutorTest.DML_SQL);
        Mockito.verify(statement2).execute(StatementExecutorTest.DML_SQL);
    }

    @Test
    public void assertExecuteForSingleStatementFailureWithDML() throws SQLException {
        Statement statement = getStatement();
        SQLException exp = new SQLException();
        Mockito.when(statement.execute(StatementExecutorTest.DML_SQL)).thenThrow(exp);
        setExecuteGroups(Collections.singletonList(statement), DML);
        Assert.assertFalse(actual.execute());
        Mockito.verify(statement).execute(StatementExecutorTest.DML_SQL);
    }

    @Test
    public void assertExecuteForMultipleStatementsFailureWithDML() throws SQLException {
        Statement statement1 = getStatement();
        Statement statement2 = getStatement();
        SQLException exp = new SQLException();
        Mockito.when(statement1.execute(StatementExecutorTest.DML_SQL)).thenThrow(exp);
        Mockito.when(statement2.execute(StatementExecutorTest.DML_SQL)).thenThrow(exp);
        setExecuteGroups(Arrays.asList(statement1, statement2), DML);
        Assert.assertFalse(actual.execute());
        Mockito.verify(statement1).execute(StatementExecutorTest.DML_SQL);
        Mockito.verify(statement2).execute(StatementExecutorTest.DML_SQL);
    }

    @Test
    public void assertExecuteForSingleStatementWithDQL() throws SQLException {
        Statement statement = getStatement();
        Mockito.when(statement.execute(StatementExecutorTest.DQL_SQL)).thenReturn(true);
        setExecuteGroups(Collections.singletonList(statement), DQL);
        Assert.assertTrue(actual.execute());
        Mockito.verify(statement).execute(StatementExecutorTest.DQL_SQL);
    }

    @Test
    public void assertExecuteForMultipleStatements() throws SQLException {
        Statement statement1 = getStatement();
        Statement statement2 = getStatement();
        Mockito.when(statement1.execute(StatementExecutorTest.DQL_SQL)).thenReturn(true);
        Mockito.when(statement2.execute(StatementExecutorTest.DQL_SQL)).thenReturn(true);
        setExecuteGroups(Arrays.asList(statement1, statement2), DQL);
        Assert.assertTrue(actual.execute());
        Mockito.verify(statement1).execute(StatementExecutorTest.DQL_SQL);
        Mockito.verify(statement2).execute(StatementExecutorTest.DQL_SQL);
    }

    @Test
    public void assertExecuteWithAutoGeneratedKeys() throws SQLException {
        Statement statement = getStatement();
        Mockito.when(statement.execute(StatementExecutorTest.DML_SQL, Statement.NO_GENERATED_KEYS)).thenReturn(false);
        setExecuteGroups(Collections.singletonList(statement), DML);
        setExecuteGroups(Collections.singletonList(statement), DML);
        Assert.assertFalse(actual.execute(Statement.NO_GENERATED_KEYS));
        Mockito.verify(statement).execute(StatementExecutorTest.DML_SQL, Statement.NO_GENERATED_KEYS);
    }

    @Test
    public void assertExecuteWithColumnIndexes() throws SQLException {
        Statement statement = getStatement();
        Mockito.when(statement.execute(StatementExecutorTest.DML_SQL, new int[]{ 1 })).thenReturn(false);
        setExecuteGroups(Collections.singletonList(statement), DML);
        Assert.assertFalse(actual.execute(new int[]{ 1 }));
        Mockito.verify(statement).execute(StatementExecutorTest.DML_SQL, new int[]{ 1 });
    }

    @Test
    public void assertExecuteWithColumnNames() throws SQLException {
        Statement statement = getStatement();
        Mockito.when(statement.execute(StatementExecutorTest.DML_SQL, new String[]{ "col" })).thenReturn(false);
        setExecuteGroups(Collections.singletonList(statement), DML);
        Assert.assertFalse(actual.execute(new String[]{ "col" }));
        Mockito.verify(statement).execute(StatementExecutorTest.DML_SQL, new String[]{ "col" });
    }

    @Test
    public void assertOverallExceptionFailure() throws SQLException {
        ExecutorExceptionHandler.setExceptionThrown(true);
        Statement statement = getStatement();
        SQLException exp = new SQLException();
        Mockito.when(statement.execute(StatementExecutorTest.DML_SQL)).thenThrow(exp);
        setExecuteGroups(Collections.singletonList(statement), DML);
        try {
            Assert.assertFalse(actual.execute());
        } catch (final SQLException ignored) {
        }
    }
}

