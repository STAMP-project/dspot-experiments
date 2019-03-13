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
import java.sql.Types;
import org.apache.shardingsphere.shardingproxy.backend.response.query.QueryData;
import org.apache.shardingsphere.shardingproxy.backend.response.query.QueryResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class ShowDatabasesBackendHandlerTest {
    private ShowDatabasesBackendHandler showDatabasesBackendHandler = new ShowDatabasesBackendHandler();

    @Test
    public void assertExecuteShowDatabaseBackendHandler() {
        QueryResponse actual = ((QueryResponse) (showDatabasesBackendHandler.execute()));
        Assert.assertThat(actual, CoreMatchers.instanceOf(QueryResponse.class));
        Assert.assertThat(actual.getQueryHeaders().size(), CoreMatchers.is(1));
    }

    @Test
    public void assertShowDatabaseUsingStream() throws SQLException {
        showDatabasesBackendHandler.execute();
        while (showDatabasesBackendHandler.next()) {
            QueryData queryData = showDatabasesBackendHandler.getQueryData();
            Assert.assertThat(queryData.getColumnTypes().size(), CoreMatchers.is(1));
            Assert.assertThat(queryData.getColumnTypes().iterator().next(), CoreMatchers.is(Types.VARCHAR));
            Assert.assertThat(queryData.getData().size(), CoreMatchers.is(1));
        } 
    }
}

