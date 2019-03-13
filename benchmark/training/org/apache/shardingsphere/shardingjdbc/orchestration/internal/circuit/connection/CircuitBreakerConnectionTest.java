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
package org.apache.shardingsphere.shardingjdbc.orchestration.internal.circuit.connection;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.shardingsphere.shardingjdbc.orchestration.internal.circuit.metadata.CircuitBreakerDatabaseMetaData;
import org.apache.shardingsphere.shardingjdbc.orchestration.internal.circuit.statement.CircuitBreakerPreparedStatement;
import org.apache.shardingsphere.shardingjdbc.orchestration.internal.circuit.statement.CircuitBreakerStatement;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class CircuitBreakerConnectionTest {
    private final CircuitBreakerConnection connection = new CircuitBreakerConnection();

    private final String sql = "select 1";

    @Test
    public void assertGetMetaData() {
        Assert.assertTrue(((connection.getMetaData()) instanceof CircuitBreakerDatabaseMetaData));
    }

    @Test
    public void setReadOnly() {
        connection.setReadOnly(true);
        Assert.assertFalse(connection.isReadOnly());
    }

    @Test
    public void assertIsReadOnly() {
        Assert.assertFalse(connection.isReadOnly());
    }

    @Test
    public void assertSetTransactionIsolation() {
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        Assert.assertThat(connection.getTransactionIsolation(), CoreMatchers.is(Connection.TRANSACTION_NONE));
    }

    @Test
    public void assertGetTransactionIsolation() {
        Assert.assertThat(connection.getTransactionIsolation(), CoreMatchers.is(Connection.TRANSACTION_NONE));
    }

    @Test
    public void assertGetWarnings() {
        Assert.assertNull(connection.getWarnings());
    }

    @Test
    public void assertClearWarnings() {
        connection.clearWarnings();
    }

    @Test
    public void assertSetAutoCommit() {
        connection.setAutoCommit(true);
        Assert.assertFalse(connection.getAutoCommit());
    }

    @Test
    public void assertGetAutoCommit() {
        Assert.assertFalse(connection.getAutoCommit());
    }

    @Test
    public void assertCommit() {
        connection.commit();
    }

    @Test
    public void assertRollback() {
        connection.rollback();
    }

    @Test
    public void assertSetHoldability() {
        connection.setHoldability((-1));
        Assert.assertThat(connection.getHoldability(), CoreMatchers.is(0));
    }

    @Test
    public void assertGetHoldability() {
        Assert.assertThat(connection.getHoldability(), CoreMatchers.is(0));
    }

    @Test
    public void assertPrepareStatement() {
        Assert.assertTrue(((connection.prepareStatement(sql)) instanceof CircuitBreakerPreparedStatement));
        Assert.assertTrue(((connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) instanceof CircuitBreakerPreparedStatement));
        Assert.assertTrue(((connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT)) instanceof CircuitBreakerPreparedStatement));
        Assert.assertTrue(((connection.prepareStatement(sql, Statement.NO_GENERATED_KEYS)) instanceof CircuitBreakerPreparedStatement));
        Assert.assertTrue(((connection.prepareStatement(sql, new int[]{ 0 })) instanceof CircuitBreakerPreparedStatement));
        Assert.assertTrue(((connection.prepareStatement(sql, new String[]{ "" })) instanceof CircuitBreakerPreparedStatement));
    }

    @Test
    public void assertCreateStatement() {
        Assert.assertTrue(((connection.createStatement()) instanceof CircuitBreakerStatement));
        Assert.assertTrue(((connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) instanceof CircuitBreakerStatement));
        Assert.assertTrue(((connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT)) instanceof CircuitBreakerStatement));
    }

    @Test
    public void assertClose() {
        connection.close();
    }

    @Test
    public void assertIsClosed() {
        Assert.assertFalse(connection.isClosed());
    }
}

