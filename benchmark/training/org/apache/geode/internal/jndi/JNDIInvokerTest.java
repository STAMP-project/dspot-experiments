/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.geode.internal.jndi;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.naming.Context;
import javax.sql.DataSource;
import org.apache.geode.internal.datasource.DataSourceCreateException;
import org.apache.geode.internal.datasource.DataSourceFactory;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class JNDIInvokerTest {
    @Test
    public void verifyThatMapDataSourceWithGetSimpleDataSourceThrowingWillThrowThatException() throws Exception {
        Map<String, String> inputs = new HashMap<>();
        Context context = Mockito.mock(Context.class);
        DataSourceFactory dataSourceFactory = Mockito.mock(DataSourceFactory.class);
        DataSourceCreateException exception = Mockito.mock(DataSourceCreateException.class);
        Mockito.doThrow(exception).when(dataSourceFactory).getSimpleDataSource(ArgumentMatchers.any());
        inputs.put("type", "SimpleDataSource");
        Throwable thrown = catchThrowable(() -> JNDIInvoker.mapDatasource(inputs, null, dataSourceFactory, context));
        assertThat(thrown).isSameAs(exception);
    }

    @Test
    public void mapDataSourceWithGetConnectionExceptionThrowsDataSourceCreateException() throws Exception {
        Map<String, String> inputs = new HashMap<>();
        Context context = Mockito.mock(Context.class);
        DataSource dataSource = Mockito.mock(DataSource.class);
        SQLException exception = Mockito.mock(SQLException.class);
        Mockito.doThrow(exception).when(dataSource).getConnection();
        DataSourceFactory dataSourceFactory = Mockito.mock(DataSourceFactory.class);
        Mockito.when(dataSourceFactory.getSimpleDataSource(ArgumentMatchers.any())).thenReturn(dataSource);
        inputs.put("type", "SimpleDataSource");
        String jndiName = "myJndiBinding";
        inputs.put("jndi-name", jndiName);
        Throwable thrown = catchThrowable(() -> JNDIInvoker.mapDatasource(inputs, null, dataSourceFactory, context));
        assertThat(thrown).isInstanceOf(DataSourceCreateException.class).hasMessage((("Failed to connect to \"" + jndiName) + "\". See log for details"));
        Mockito.verify(context, Mockito.never()).rebind(((String) (ArgumentMatchers.any())), ArgumentMatchers.any());
    }

    @Test
    public void mapDataSourceWithGetConnectionSuccessClosesConnectionAndRebinds() throws Exception {
        Map<String, String> inputs = new HashMap<>();
        Context context = Mockito.mock(Context.class);
        DataSource dataSource = Mockito.mock(DataSource.class);
        Connection connection = Mockito.mock(Connection.class);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        DataSourceFactory dataSourceFactory = Mockito.mock(DataSourceFactory.class);
        Mockito.when(dataSourceFactory.getSimpleDataSource(ArgumentMatchers.any())).thenReturn(dataSource);
        inputs.put("type", "SimpleDataSource");
        String jndiName = "myJndiBinding";
        inputs.put("jndi-name", jndiName);
        JNDIInvoker.mapDatasource(inputs, null, dataSourceFactory, context);
        Mockito.verify(connection).close();
        Mockito.verify(context).rebind(((String) (ArgumentMatchers.any())), ArgumentMatchers.same(dataSource));
    }
}

