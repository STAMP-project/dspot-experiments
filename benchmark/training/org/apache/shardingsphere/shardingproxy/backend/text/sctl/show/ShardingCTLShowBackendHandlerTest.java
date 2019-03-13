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
package org.apache.shardingsphere.shardingproxy.backend.text.sctl.show;


import java.sql.SQLException;
import org.apache.shardingsphere.shardingproxy.backend.communication.jdbc.connection.BackendConnection;
import org.apache.shardingsphere.shardingproxy.backend.response.BackendResponse;
import org.apache.shardingsphere.shardingproxy.backend.response.error.ErrorResponse;
import org.apache.shardingsphere.shardingproxy.backend.response.query.QueryData;
import org.apache.shardingsphere.shardingproxy.backend.response.query.QueryResponse;
import org.apache.shardingsphere.shardingproxy.backend.text.sctl.exception.InvalidShardingCTLFormatException;
import org.apache.shardingsphere.shardingproxy.backend.text.sctl.exception.UnsupportedShardingCTLTypeException;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class ShardingCTLShowBackendHandlerTest {
    private BackendConnection backendConnection = new BackendConnection(TransactionType.LOCAL);

    @Test
    public void assertShowTransactionType() throws SQLException {
        backendConnection.setCurrentSchema("schema");
        ShardingCTLShowBackendHandler backendHandler = new ShardingCTLShowBackendHandler("sctl:show transaction_type", backendConnection);
        BackendResponse actual = backendHandler.execute();
        Assert.assertThat(actual, CoreMatchers.instanceOf(QueryResponse.class));
        Assert.assertThat(getQueryHeaders().size(), CoreMatchers.is(1));
        backendHandler.next();
        QueryData queryData = backendHandler.getQueryData();
        Assert.assertThat(queryData.getData().iterator().next(), CoreMatchers.<Object>is("LOCAL"));
    }

    @Test
    public void assertShowCachedConnections() throws SQLException {
        backendConnection.setCurrentSchema("schema");
        ShardingCTLShowBackendHandler backendHandler = new ShardingCTLShowBackendHandler("sctl:show cached_connections", backendConnection);
        BackendResponse actual = backendHandler.execute();
        Assert.assertThat(actual, CoreMatchers.instanceOf(QueryResponse.class));
        Assert.assertThat(getQueryHeaders().size(), CoreMatchers.is(1));
        backendHandler.next();
        QueryData queryData = backendHandler.getQueryData();
        Assert.assertThat(queryData.getData().iterator().next(), CoreMatchers.<Object>is(0));
    }

    @Test
    public void assertShowCachedConnectionFailed() {
        backendConnection.setCurrentSchema("schema");
        ShardingCTLShowBackendHandler backendHandler = new ShardingCTLShowBackendHandler("sctl:show cached_connectionss", backendConnection);
        BackendResponse actual = backendHandler.execute();
        Assert.assertThat(actual, CoreMatchers.instanceOf(ErrorResponse.class));
        Assert.assertThat(getCause(), CoreMatchers.instanceOf(UnsupportedShardingCTLTypeException.class));
    }

    @Test
    public void assertShowCTLFormatError() {
        backendConnection.setCurrentSchema("schema");
        ShardingCTLShowBackendHandler backendHandler = new ShardingCTLShowBackendHandler("sctl:show=xx", backendConnection);
        BackendResponse actual = backendHandler.execute();
        Assert.assertThat(actual, CoreMatchers.instanceOf(ErrorResponse.class));
        Assert.assertThat(getCause(), CoreMatchers.instanceOf(InvalidShardingCTLFormatException.class));
    }
}

