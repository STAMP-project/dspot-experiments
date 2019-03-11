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
package org.apache.shardingsphere.shardingproxy.backend;


import org.apache.shardingsphere.shardingproxy.backend.communication.jdbc.connection.BackendConnection;
import org.apache.shardingsphere.shardingproxy.backend.communication.jdbc.connection.ConnectionStateHandler;
import org.apache.shardingsphere.shardingproxy.backend.text.TextProtocolBackendHandler;
import org.apache.shardingsphere.shardingproxy.backend.text.TextProtocolBackendHandlerFactory;
import org.apache.shardingsphere.shardingproxy.backend.text.admin.BroadcastBackendHandler;
import org.apache.shardingsphere.shardingproxy.backend.text.admin.ShowDatabasesBackendHandler;
import org.apache.shardingsphere.shardingproxy.backend.text.admin.UseDatabaseBackendHandler;
import org.apache.shardingsphere.shardingproxy.backend.text.query.QueryBackendHandler;
import org.apache.shardingsphere.shardingproxy.backend.text.sctl.set.ShardingCTLSetBackendHandler;
import org.apache.shardingsphere.shardingproxy.backend.text.transaction.SkipBackendHandler;
import org.apache.shardingsphere.shardingproxy.backend.text.transaction.TransactionBackendHandler;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class TextProtocolBackendHandlerFactoryTest {
    @Mock
    private BackendConnection backendConnection;

    @Test
    public void assertNewInstance() {
        String sql = "BEGIN";
        TextProtocolBackendHandler actual = TextProtocolBackendHandlerFactory.newInstance(sql, backendConnection);
        Assert.assertThat(actual, CoreMatchers.instanceOf(TransactionBackendHandler.class));
    }

    @Test
    public void assertNewTransactionBackendHandlerInstanceOfCommitOperate() {
        String sql = "SET AUTOCOMMIT=1";
        ConnectionStateHandler stateHandler = Mockito.mock(ConnectionStateHandler.class);
        Mockito.when(backendConnection.getStateHandler()).thenReturn(stateHandler);
        Mockito.when(stateHandler.isInTransaction()).thenReturn(true);
        TextProtocolBackendHandler actual = TextProtocolBackendHandlerFactory.newInstance(sql, backendConnection);
        Assert.assertThat(actual, CoreMatchers.instanceOf(TransactionBackendHandler.class));
    }

    @Test
    public void assertNewIgnoreBackendHandlerInstance() {
        String sql = "SET AUTOCOMMIT=1";
        ConnectionStateHandler stateHandler = Mockito.mock(ConnectionStateHandler.class);
        Mockito.when(backendConnection.getStateHandler()).thenReturn(stateHandler);
        Mockito.when(stateHandler.isInTransaction()).thenReturn(false);
        TextProtocolBackendHandler actual = TextProtocolBackendHandlerFactory.newInstance(sql, backendConnection);
        Assert.assertThat(actual, CoreMatchers.instanceOf(SkipBackendHandler.class));
    }

    @Test
    public void assertNewShardingCTLBackendHandlerInstance() {
        String sql = "sctl:set transaction_type=XA";
        TextProtocolBackendHandler actual = TextProtocolBackendHandlerFactory.newInstance(sql, backendConnection);
        Assert.assertThat(actual, CoreMatchers.instanceOf(ShardingCTLSetBackendHandler.class));
    }

    @Test
    public void assertNewSchemaBroadcastBackendHandlerInstance() {
        String sql = "set @num=1";
        TextProtocolBackendHandler actual = TextProtocolBackendHandlerFactory.newInstance(sql, backendConnection);
        Assert.assertThat(actual, CoreMatchers.instanceOf(BroadcastBackendHandler.class));
    }

    @Test
    public void assertNewUseSchemaBackendHandlerInstance() {
        String sql = "use sharding_db";
        TextProtocolBackendHandler actual = TextProtocolBackendHandlerFactory.newInstance(sql, backendConnection);
        Assert.assertThat(actual, CoreMatchers.instanceOf(UseDatabaseBackendHandler.class));
    }

    @Test
    public void assertNewShowDatabasesBackendHandlerInstance() {
        String sql = "show databases;";
        TextProtocolBackendHandler actual = TextProtocolBackendHandlerFactory.newInstance(sql, backendConnection);
        Assert.assertThat(actual, CoreMatchers.instanceOf(ShowDatabasesBackendHandler.class));
    }

    @Test
    public void assertNewDefaultInstance() {
        String sql = "select * from t_order limit 1";
        TextProtocolBackendHandler actual = TextProtocolBackendHandlerFactory.newInstance(sql, backendConnection);
        Assert.assertThat(actual, CoreMatchers.instanceOf(QueryBackendHandler.class));
    }
}

