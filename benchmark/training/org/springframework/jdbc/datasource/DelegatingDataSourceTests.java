/**
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.jdbc.datasource;


import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 * Tests for {@link DelegatingDataSource}.
 *
 * @author Phillip Webb
 */
public class DelegatingDataSourceTests {
    private final DataSource delegate = Mockito.mock(DataSource.class);

    private DelegatingDataSource dataSource = new DelegatingDataSource(delegate);

    @Test
    public void shouldDelegateGetConnection() throws Exception {
        Connection connection = Mockito.mock(Connection.class);
        BDDMockito.given(delegate.getConnection()).willReturn(connection);
        Assert.assertThat(dataSource.getConnection(), is(connection));
    }

    @Test
    public void shouldDelegateGetConnectionWithUsernameAndPassword() throws Exception {
        Connection connection = Mockito.mock(Connection.class);
        String username = "username";
        String password = "password";
        BDDMockito.given(delegate.getConnection(username, password)).willReturn(connection);
        Assert.assertThat(dataSource.getConnection(username, password), is(connection));
    }

    @Test
    public void shouldDelegateGetLogWriter() throws Exception {
        PrintWriter writer = new PrintWriter(new ByteArrayOutputStream());
        BDDMockito.given(delegate.getLogWriter()).willReturn(writer);
        Assert.assertThat(dataSource.getLogWriter(), is(writer));
    }

    @Test
    public void shouldDelegateSetLogWriter() throws Exception {
        PrintWriter writer = new PrintWriter(new ByteArrayOutputStream());
        dataSource.setLogWriter(writer);
        Mockito.verify(delegate).setLogWriter(writer);
    }

    @Test
    public void shouldDelegateGetLoginTimeout() throws Exception {
        int timeout = 123;
        BDDMockito.given(delegate.getLoginTimeout()).willReturn(timeout);
        Assert.assertThat(dataSource.getLoginTimeout(), is(timeout));
    }

    @Test
    public void shouldDelegateSetLoginTimeoutWithSeconds() throws Exception {
        int timeout = 123;
        dataSource.setLoginTimeout(timeout);
        Mockito.verify(delegate).setLoginTimeout(timeout);
    }

    @Test
    public void shouldDelegateUnwrapWithoutImplementing() throws Exception {
        DelegatingDataSourceTests.ExampleWrapper wrapper = Mockito.mock(DelegatingDataSourceTests.ExampleWrapper.class);
        BDDMockito.given(delegate.unwrap(DelegatingDataSourceTests.ExampleWrapper.class)).willReturn(wrapper);
        Assert.assertThat(dataSource.unwrap(DelegatingDataSourceTests.ExampleWrapper.class), is(wrapper));
    }

    @Test
    public void shouldDelegateUnwrapImplementing() throws Exception {
        dataSource = new DelegatingDataSourceTests.DelegatingDataSourceWithWrapper();
        Assert.assertThat(dataSource.unwrap(DelegatingDataSourceTests.ExampleWrapper.class), is(((DelegatingDataSourceTests.ExampleWrapper) (dataSource))));
    }

    @Test
    public void shouldDelegateIsWrapperForWithoutImplementing() throws Exception {
        BDDMockito.given(delegate.isWrapperFor(DelegatingDataSourceTests.ExampleWrapper.class)).willReturn(true);
        Assert.assertThat(dataSource.isWrapperFor(DelegatingDataSourceTests.ExampleWrapper.class), is(true));
    }

    @Test
    public void shouldDelegateIsWrapperForImplementing() throws Exception {
        dataSource = new DelegatingDataSourceTests.DelegatingDataSourceWithWrapper();
        Assert.assertThat(dataSource.isWrapperFor(DelegatingDataSourceTests.ExampleWrapper.class), is(true));
    }

    public static interface ExampleWrapper {}

    private static class DelegatingDataSourceWithWrapper extends DelegatingDataSource implements DelegatingDataSourceTests.ExampleWrapper {}
}

