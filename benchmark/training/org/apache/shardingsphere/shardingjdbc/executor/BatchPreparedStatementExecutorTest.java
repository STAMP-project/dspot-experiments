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


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public final class BatchPreparedStatementExecutorTest extends AbstractBaseExecutorTest {
    private static final String SQL = "DELETE FROM table_x WHERE id=?";

    private BatchPreparedStatementExecutor actual;

    @SuppressWarnings("unchecked")
    @Test
    public void assertNoPreparedStatement() throws SQLException {
        PreparedStatement preparedStatement = getPreparedStatement();
        Mockito.when(preparedStatement.executeBatch()).thenReturn(new int[]{ 0, 0 });
        setExecuteGroups(Collections.singletonList(preparedStatement));
        Assert.assertThat(actual.executeBatch(), CoreMatchers.is(new int[]{ 0, 0 }));
    }

    @Test
    public void assertExecuteBatchForSinglePreparedStatementSuccess() throws SQLException {
        PreparedStatement preparedStatement = getPreparedStatement();
        Mockito.when(preparedStatement.executeBatch()).thenReturn(new int[]{ 10, 20 });
        setExecuteGroups(Collections.singletonList(preparedStatement));
        Assert.assertThat(actual.executeBatch(), CoreMatchers.is(new int[]{ 10, 20 }));
        Mockito.verify(preparedStatement).executeBatch();
    }

    @Test
    public void assertExecuteBatchForMultiplePreparedStatementsSuccess() throws SQLException {
        PreparedStatement preparedStatement1 = getPreparedStatement();
        PreparedStatement preparedStatement2 = getPreparedStatement();
        Mockito.when(preparedStatement1.executeBatch()).thenReturn(new int[]{ 10, 20 });
        Mockito.when(preparedStatement2.executeBatch()).thenReturn(new int[]{ 20, 40 });
        setExecuteGroups(Arrays.asList(preparedStatement1, preparedStatement2));
        Assert.assertThat(actual.executeBatch(), CoreMatchers.is(new int[]{ 30, 60 }));
        Mockito.verify(preparedStatement1).executeBatch();
        Mockito.verify(preparedStatement2).executeBatch();
    }

    @Test
    public void assertExecuteBatchForSinglePreparedStatementFailure() throws SQLException {
        PreparedStatement preparedStatement = getPreparedStatement();
        SQLException exp = new SQLException();
        Mockito.when(preparedStatement.executeBatch()).thenThrow(exp);
        setExecuteGroups(Collections.singletonList(preparedStatement));
        Assert.assertThat(actual.executeBatch(), CoreMatchers.is(new int[]{ 0, 0 }));
        Mockito.verify(preparedStatement).executeBatch();
    }

    @Test
    public void assertExecuteBatchForMultiplePreparedStatementsFailure() throws SQLException {
        PreparedStatement preparedStatement1 = getPreparedStatement();
        PreparedStatement preparedStatement2 = getPreparedStatement();
        SQLException exp = new SQLException();
        Mockito.when(preparedStatement1.executeBatch()).thenThrow(exp);
        Mockito.when(preparedStatement2.executeBatch()).thenThrow(exp);
        setExecuteGroups(Arrays.asList(preparedStatement1, preparedStatement2));
        Assert.assertThat(actual.executeBatch(), CoreMatchers.is(new int[]{ 0, 0 }));
        Mockito.verify(preparedStatement1).executeBatch();
        Mockito.verify(preparedStatement2).executeBatch();
    }
}

