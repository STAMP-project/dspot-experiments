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
package org.apache.shardingsphere.transaction.xa.jta.connection;


import java.sql.Connection;
import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;
import lombok.SneakyThrows;
import org.apache.shardingsphere.transaction.xa.spi.SingleXAResource;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class SingleXAConnectionTest {
    @Mock
    private XAConnection xaConnection;

    @Mock
    private Connection connection;

    private SingleXAConnection singleXAConnection;

    @Test
    @SneakyThrows
    public void assertGetConnection() {
        Connection actual = singleXAConnection.getConnection();
        Assert.assertThat(actual, CoreMatchers.is(connection));
    }

    @Test
    @SneakyThrows
    public void assertGetXAResource() {
        XAResource actual = singleXAConnection.getXAResource();
        Assert.assertThat(actual, CoreMatchers.instanceOf(SingleXAResource.class));
    }

    @Test
    @SneakyThrows
    public void close() {
        singleXAConnection.close();
        Mockito.verify(xaConnection).close();
    }

    @Test
    public void assertAddConnectionEventListener() {
        singleXAConnection.addConnectionEventListener(Mockito.mock(ConnectionEventListener.class));
        Mockito.verify(xaConnection).addConnectionEventListener(ArgumentMatchers.any(ConnectionEventListener.class));
    }

    @Test
    public void assertRemoveConnectionEventListener() {
        singleXAConnection.removeConnectionEventListener(Mockito.mock(ConnectionEventListener.class));
        Mockito.verify(xaConnection).removeConnectionEventListener(ArgumentMatchers.any(ConnectionEventListener.class));
    }

    @Test
    public void assertAddStatementEventListener() {
        singleXAConnection.addStatementEventListener(Mockito.mock(StatementEventListener.class));
        Mockito.verify(xaConnection).addStatementEventListener(ArgumentMatchers.any(StatementEventListener.class));
    }

    @Test
    public void removeStatementEventListener() {
        singleXAConnection.removeStatementEventListener(Mockito.mock(StatementEventListener.class));
        Mockito.verify(xaConnection).removeStatementEventListener(ArgumentMatchers.any(StatementEventListener.class));
    }
}

