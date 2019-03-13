/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.store.jdbc.adapter;


import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.activemq.store.jdbc.Statements;
import org.apache.activemq.store.jdbc.TransactionContext;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultJDBCAdapterDoCreateTablesTest {
    private static final String CREATE_STATEMENT1 = "createStatement1";

    private static final String CREATE_STATEMENT2 = "createStatement2";

    private static final String[] CREATE_STATEMENTS = new String[]{ DefaultJDBCAdapterDoCreateTablesTest.CREATE_STATEMENT1, DefaultJDBCAdapterDoCreateTablesTest.CREATE_STATEMENT2 };

    private static final int VENDOR_CODE = 1;

    private static final String SQL_STATE = "SqlState";

    private static final String MY_REASON = "MyReason";

    private DefaultJDBCAdapter defaultJDBCAdapter;

    private List<LoggingEvent> loggingEvents = new ArrayList<>();

    @Mock
    private TransactionContext transactionContext;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Connection connection;

    @Mock
    private Statements statements;

    @Mock
    private ResultSet resultSet;

    @Mock
    private Statement statement1;

    @Mock
    private Statement statement2;

    @Test
    public void createsTheTablesWhenNoMessageTableExistsAndLogsSqlExceptionsInWarnLevel() throws IOException, SQLException {
        Mockito.when(resultSet.next()).thenReturn(false);
        Mockito.when(statement2.execute(DefaultJDBCAdapterDoCreateTablesTest.CREATE_STATEMENT2)).thenThrow(new SQLException(DefaultJDBCAdapterDoCreateTablesTest.MY_REASON, DefaultJDBCAdapterDoCreateTablesTest.SQL_STATE, DefaultJDBCAdapterDoCreateTablesTest.VENDOR_CODE));
        defaultJDBCAdapter.doCreateTables(transactionContext);
        InOrder inOrder = Mockito.inOrder(resultSet, connection, statement1, statement2);
        inOrder.verify(resultSet).next();
        inOrder.verify(resultSet).close();
        inOrder.verify(connection).createStatement();
        inOrder.verify(statement1).execute(DefaultJDBCAdapterDoCreateTablesTest.CREATE_STATEMENT1);
        inOrder.verify(statement1).close();
        inOrder.verify(connection).createStatement();
        inOrder.verify(statement2).execute(DefaultJDBCAdapterDoCreateTablesTest.CREATE_STATEMENT2);
        inOrder.verify(statement2).close();
        Assert.assertEquals(4, loggingEvents.size());
        assertLog(0, Level.DEBUG, ("Executing SQL: " + (DefaultJDBCAdapterDoCreateTablesTest.CREATE_STATEMENT1)));
        assertLog(1, Level.DEBUG, ("Executing SQL: " + (DefaultJDBCAdapterDoCreateTablesTest.CREATE_STATEMENT2)));
        assertLog(2, Level.WARN, ((((((("Could not create JDBC tables; they could already exist. Failure was: " + (DefaultJDBCAdapterDoCreateTablesTest.CREATE_STATEMENT2)) + " Message: ") + (DefaultJDBCAdapterDoCreateTablesTest.MY_REASON)) + " SQLState: ") + (DefaultJDBCAdapterDoCreateTablesTest.SQL_STATE)) + " Vendor code: ") + (DefaultJDBCAdapterDoCreateTablesTest.VENDOR_CODE)));
        assertLog(3, Level.WARN, ("Failure details: " + (DefaultJDBCAdapterDoCreateTablesTest.MY_REASON)));
    }

    @Test
    public void triesTocreateTheTablesWhenMessageTableExistsAndLogsSqlExceptionsInDebugLevel() throws IOException, SQLException {
        Mockito.when(resultSet.next()).thenReturn(true);
        Mockito.when(statement1.execute(DefaultJDBCAdapterDoCreateTablesTest.CREATE_STATEMENT1)).thenThrow(new SQLException(DefaultJDBCAdapterDoCreateTablesTest.MY_REASON, DefaultJDBCAdapterDoCreateTablesTest.SQL_STATE, DefaultJDBCAdapterDoCreateTablesTest.VENDOR_CODE));
        defaultJDBCAdapter.doCreateTables(transactionContext);
        InOrder inOrder = Mockito.inOrder(resultSet, connection, statement1, statement2);
        inOrder.verify(resultSet).next();
        inOrder.verify(resultSet).close();
        inOrder.verify(connection).createStatement();
        inOrder.verify(statement1).execute(DefaultJDBCAdapterDoCreateTablesTest.CREATE_STATEMENT1);
        inOrder.verify(statement1).close();
        inOrder.verify(connection).createStatement();
        inOrder.verify(statement2).execute(DefaultJDBCAdapterDoCreateTablesTest.CREATE_STATEMENT2);
        inOrder.verify(statement2).close();
        Assert.assertEquals(3, loggingEvents.size());
        assertLog(0, Level.DEBUG, ("Executing SQL: " + (DefaultJDBCAdapterDoCreateTablesTest.CREATE_STATEMENT1)));
        assertLog(1, Level.DEBUG, ((((((("Could not create JDBC tables; The message table already existed. Failure was: " + (DefaultJDBCAdapterDoCreateTablesTest.CREATE_STATEMENT1)) + " Message: ") + (DefaultJDBCAdapterDoCreateTablesTest.MY_REASON)) + " SQLState: ") + (DefaultJDBCAdapterDoCreateTablesTest.SQL_STATE)) + " Vendor code: ") + (DefaultJDBCAdapterDoCreateTablesTest.VENDOR_CODE)));
        assertLog(2, Level.DEBUG, ("Executing SQL: " + (DefaultJDBCAdapterDoCreateTablesTest.CREATE_STATEMENT2)));
    }

    @Test
    public void commitsTheTransactionWhenAutoCommitIsDisabled() throws IOException, SQLException {
        Mockito.when(connection.getAutoCommit()).thenReturn(false);
        Mockito.when(resultSet.next()).thenReturn(false);
        defaultJDBCAdapter.doCreateTables(transactionContext);
        InOrder inOrder = Mockito.inOrder(resultSet, connection, statement1, statement2);
        inOrder.verify(resultSet).next();
        inOrder.verify(resultSet).close();
        inOrder.verify(connection).createStatement();
        inOrder.verify(statement1).execute(DefaultJDBCAdapterDoCreateTablesTest.CREATE_STATEMENT1);
        inOrder.verify(connection).commit();
        inOrder.verify(statement1).close();
        inOrder.verify(connection).createStatement();
        inOrder.verify(statement2).execute(DefaultJDBCAdapterDoCreateTablesTest.CREATE_STATEMENT2);
        inOrder.verify(connection).commit();
        inOrder.verify(statement2).close();
        Assert.assertEquals(2, loggingEvents.size());
        assertLog(0, Level.DEBUG, ("Executing SQL: " + (DefaultJDBCAdapterDoCreateTablesTest.CREATE_STATEMENT1)));
        assertLog(1, Level.DEBUG, ("Executing SQL: " + (DefaultJDBCAdapterDoCreateTablesTest.CREATE_STATEMENT2)));
    }
}

