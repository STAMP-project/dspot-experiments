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
package org.apache.shardingsphere.shardingproxy.backend.text.admin;


import java.sql.SQLException;
import org.apache.shardingsphere.shardingproxy.backend.MockLogicSchemasUtil;
import org.apache.shardingsphere.shardingproxy.backend.communication.DatabaseCommunicationEngine;
import org.apache.shardingsphere.shardingproxy.backend.communication.DatabaseCommunicationEngineFactory;
import org.apache.shardingsphere.shardingproxy.backend.communication.jdbc.connection.BackendConnection;
import org.apache.shardingsphere.shardingproxy.backend.response.BackendResponse;
import org.apache.shardingsphere.shardingproxy.backend.response.error.ErrorResponse;
import org.apache.shardingsphere.shardingproxy.backend.response.update.UpdateResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class BroadcastBackendHandlerTest {
    @Mock
    private BackendConnection backendConnection;

    @Mock
    private DatabaseCommunicationEngineFactory databaseCommunicationEngineFactory;

    @Mock
    private DatabaseCommunicationEngine databaseCommunicationEngine;

    @Test
    public void assertExecuteSuccess() {
        MockLogicSchemasUtil.setLogicSchemas("schema", 10);
        mockDatabaseCommunicationEngine(new UpdateResponse());
        BroadcastBackendHandler broadcastBackendHandler = new BroadcastBackendHandler("SET timeout = 1000", backendConnection);
        setBackendHandlerFactory(broadcastBackendHandler);
        BackendResponse actual = broadcastBackendHandler.execute();
        Assert.assertThat(actual, CoreMatchers.instanceOf(UpdateResponse.class));
        Assert.assertThat(getUpdateCount(), CoreMatchers.is(0L));
        Assert.assertThat(getLastInsertId(), CoreMatchers.is(0L));
        Mockito.verify(databaseCommunicationEngine, Mockito.times(10)).execute();
    }

    @Test
    public void assertExecuteFailure() {
        MockLogicSchemasUtil.setLogicSchemas("schema", 10);
        ErrorResponse errorResponse = new ErrorResponse(new SQLException("no reason", "X999", (-1)));
        mockDatabaseCommunicationEngine(errorResponse);
        BroadcastBackendHandler broadcastBackendHandler = new BroadcastBackendHandler("SET timeout = 1000", backendConnection);
        setBackendHandlerFactory(broadcastBackendHandler);
        Assert.assertThat(broadcastBackendHandler.execute(), CoreMatchers.instanceOf(ErrorResponse.class));
        Mockito.verify(databaseCommunicationEngine, Mockito.times(10)).execute();
    }
}

