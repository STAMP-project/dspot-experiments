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


import org.apache.shardingsphere.core.parsing.parser.dialect.mysql.statement.UseStatement;
import org.apache.shardingsphere.shardingproxy.backend.communication.jdbc.connection.BackendConnection;
import org.apache.shardingsphere.shardingproxy.backend.response.BackendResponse;
import org.apache.shardingsphere.shardingproxy.backend.response.error.ErrorResponse;
import org.apache.shardingsphere.shardingproxy.backend.response.update.UpdateResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class UseDatabaseBackendHandlerTest {
    @Mock
    private BackendConnection backendConnection;

    @Test
    public void assertExecuteUseStatementBackendHandler() {
        UseStatement useStatement = Mockito.mock(UseStatement.class);
        Mockito.when(useStatement.getSchema()).thenReturn("schema_0");
        UseDatabaseBackendHandler useSchemaBackendHandler = new UseDatabaseBackendHandler(useStatement, backendConnection);
        BackendResponse actual = useSchemaBackendHandler.execute();
        Mockito.verify(backendConnection).setCurrentSchema(ArgumentMatchers.anyString());
        Assert.assertThat(actual, CoreMatchers.instanceOf(UpdateResponse.class));
    }

    @Test
    public void assertExecuteUseStatementNotExist() {
        UseStatement useStatement = Mockito.mock(UseStatement.class);
        Mockito.when(useStatement.getSchema()).thenReturn("not_exist");
        UseDatabaseBackendHandler useSchemaBackendHandler = new UseDatabaseBackendHandler(useStatement, backendConnection);
        BackendResponse actual = useSchemaBackendHandler.execute();
        Assert.assertThat(actual, CoreMatchers.instanceOf(ErrorResponse.class));
        Mockito.verify(backendConnection, Mockito.times(0)).setCurrentSchema(ArgumentMatchers.anyString());
    }
}

