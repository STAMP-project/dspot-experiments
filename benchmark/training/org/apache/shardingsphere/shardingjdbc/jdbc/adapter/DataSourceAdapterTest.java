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
package org.apache.shardingsphere.shardingjdbc.jdbc.adapter;


import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;
import org.apache.shardingsphere.shardingjdbc.common.base.AbstractShardingJDBCDatabaseAndTableTest;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.connection.ShardingConnection;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public final class DataSourceAdapterTest extends AbstractShardingJDBCDatabaseAndTableTest {
    @Test
    public void assertUnwrapSuccess() throws SQLException {
        Assert.assertThat(getShardingDataSource().unwrap(Object.class), CoreMatchers.is(((Object) (getShardingDataSource()))));
    }

    @Test(expected = SQLException.class)
    public void assertUnwrapFailure() throws SQLException {
        getShardingDataSource().unwrap(String.class);
    }

    @Test
    public void assertIsWrapperFor() {
        Assert.assertTrue(getShardingDataSource().isWrapperFor(Object.class));
    }

    @Test
    public void assertIsNotWrapperFor() {
        Assert.assertFalse(getShardingDataSource().isWrapperFor(String.class));
    }

    @Test
    public void assertRecordMethodInvocationSuccess() {
        List<?> list = Mockito.mock(List.class);
        Mockito.when(list.isEmpty()).thenReturn(true);
        getShardingDataSource().recordMethodInvocation(List.class, "isEmpty", new Class[]{  }, new Object[]{  });
        getShardingDataSource().replayMethodsInvocation(list);
    }

    @Test(expected = NoSuchMethodException.class)
    public void assertRecordMethodInvocationFailure() {
        getShardingDataSource().recordMethodInvocation(String.class, "none", new Class[]{  }, new Object[]{  });
    }

    @Test
    public void assertSetLogWriter() {
        Assert.assertThat(getShardingDataSource().getLogWriter(), CoreMatchers.instanceOf(PrintWriter.class));
        getShardingDataSource().setLogWriter(null);
        Assert.assertNull(getShardingDataSource().getLogWriter());
    }

    @Test
    public void assertGetParentLogger() {
        Assert.assertThat(getShardingDataSource().getParentLogger().getName(), CoreMatchers.is(Logger.GLOBAL_LOGGER_NAME));
    }

    @Test
    public void assertGetConnectionWithUsername() throws SQLException {
        Assert.assertThat(getShardingDataSource().getConnection("username", "password"), CoreMatchers.instanceOf(ShardingConnection.class));
    }
}

