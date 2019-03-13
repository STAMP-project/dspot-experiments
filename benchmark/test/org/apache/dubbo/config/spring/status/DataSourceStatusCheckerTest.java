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
package org.apache.dubbo.config.spring.status;


import Status.Level.ERROR;
import Status.Level.OK;
import Status.Level.UNKNOWN;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.dubbo.common.status.Status;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;


public class DataSourceStatusCheckerTest {
    private DataSourceStatusChecker dataSourceStatusChecker;

    @Mock
    private ApplicationContext applicationContext;

    @Test
    public void testWithoutApplicationContext() {
        Status status = dataSourceStatusChecker.check();
        MatcherAssert.assertThat(status.getLevel(), CoreMatchers.is(UNKNOWN));
    }

    @Test
    public void testWithoutDatasource() {
        Map<String, DataSource> map = new HashMap<String, DataSource>();
        BDDMockito.given(applicationContext.getBeansOfType(ArgumentMatchers.eq(DataSource.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).willReturn(map);
        Status status = dataSourceStatusChecker.check();
        MatcherAssert.assertThat(status.getLevel(), CoreMatchers.is(UNKNOWN));
    }

    @Test
    public void testWithDatasourceHasNextResult() throws SQLException {
        Map<String, DataSource> map = new HashMap<String, DataSource>();
        DataSource dataSource = Mockito.mock(DataSource.class);
        Connection connection = Mockito.mock(Connection.class, Answers.RETURNS_DEEP_STUBS);
        BDDMockito.given(dataSource.getConnection()).willReturn(connection);
        BDDMockito.given(connection.getMetaData().getTypeInfo().next()).willReturn(true);
        map.put("mockDatabase", dataSource);
        BDDMockito.given(applicationContext.getBeansOfType(ArgumentMatchers.eq(DataSource.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).willReturn(map);
        Status status = dataSourceStatusChecker.check();
        MatcherAssert.assertThat(status.getLevel(), CoreMatchers.is(OK));
    }

    @Test
    public void testWithDatasourceNotHasNextResult() throws SQLException {
        Map<String, DataSource> map = new HashMap<String, DataSource>();
        DataSource dataSource = Mockito.mock(DataSource.class);
        Connection connection = Mockito.mock(Connection.class, Answers.RETURNS_DEEP_STUBS);
        BDDMockito.given(dataSource.getConnection()).willReturn(connection);
        BDDMockito.given(connection.getMetaData().getTypeInfo().next()).willReturn(false);
        map.put("mockDatabase", dataSource);
        BDDMockito.given(applicationContext.getBeansOfType(ArgumentMatchers.eq(DataSource.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).willReturn(map);
        Status status = dataSourceStatusChecker.check();
        MatcherAssert.assertThat(status.getLevel(), CoreMatchers.is(ERROR));
    }
}

