/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.common.jdbc;


import ConnectionWrapperFactory.ConnectionCloseHandler;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Allard Buijze
 */
public class ConnectionWrapperFactoryTest {
    private ConnectionCloseHandler closeHandler;

    private Connection connection;

    @Test
    public void testWrapperDelegatesAllButClose() throws Exception {
        Connection wrapped = ConnectionWrapperFactory.wrap(connection, closeHandler);
        wrapped.commit();
        Mockito.verify(closeHandler).commit(connection);
        wrapped.getAutoCommit();
        Mockito.verify(connection).getAutoCommit();
        Mockito.verifyZeroInteractions(closeHandler);
        wrapped.close();
        Mockito.verify(connection, Mockito.never()).close();
        Mockito.verify(closeHandler).close(connection);
    }

    @Test
    public void testEquals_WithWrapper() {
        final Runnable runnable = Mockito.mock(Runnable.class);
        Connection wrapped = ConnectionWrapperFactory.wrap(connection, Runnable.class, runnable, closeHandler);
        Assert.assertNotEquals(wrapped, connection);
        Assert.assertEquals(wrapped, wrapped);
    }

    @Test
    public void testEquals_WithoutWrapper() {
        Connection wrapped = ConnectionWrapperFactory.wrap(connection, closeHandler);
        Assert.assertNotEquals(wrapped, connection);
        Assert.assertEquals(wrapped, wrapped);
    }

    @Test
    public void testHashCode_WithWrapper() {
        final Runnable runnable = Mockito.mock(Runnable.class);
        Connection wrapped = ConnectionWrapperFactory.wrap(connection, Runnable.class, runnable, closeHandler);
        Assert.assertEquals(wrapped.hashCode(), wrapped.hashCode());
    }

    @Test
    public void testHashCode_WithoutWrapper() {
        Connection wrapped = ConnectionWrapperFactory.wrap(connection, closeHandler);
        Assert.assertEquals(wrapped.hashCode(), wrapped.hashCode());
    }

    @Test(expected = SQLException.class)
    public void testUnwrapInvocationTargetException() throws Exception {
        Mockito.when(connection.prepareStatement(ArgumentMatchers.anyString())).thenThrow(new SQLException());
        Connection wrapper = ConnectionWrapperFactory.wrap(connection, closeHandler);
        wrapper.prepareStatement("foo");
    }

    @Test(expected = SQLException.class)
    public void testUnwrapInvocationTargetExceptionWithAdditionalWrapperInterface1() throws Exception {
        ConnectionWrapperFactoryTest.WrapperInterface wrapperImplementation = Mockito.mock(ConnectionWrapperFactoryTest.WrapperInterface.class);
        Mockito.when(connection.prepareStatement(ArgumentMatchers.anyString())).thenThrow(new SQLException());
        Connection wrapper = ConnectionWrapperFactory.wrap(connection, ConnectionWrapperFactoryTest.WrapperInterface.class, wrapperImplementation, closeHandler);
        wrapper.prepareStatement("foo");
    }

    @Test(expected = SQLException.class)
    public void testUnwrapInvocationTargetExceptionWithAdditionalWrapperInterface2() throws Exception {
        ConnectionWrapperFactoryTest.WrapperInterface wrapperImplementation = Mockito.mock(ConnectionWrapperFactoryTest.WrapperInterface.class);
        Mockito.doThrow(new SQLException()).when(wrapperImplementation).foo();
        ConnectionWrapperFactoryTest.WrapperInterface wrapper = ((ConnectionWrapperFactoryTest.WrapperInterface) (ConnectionWrapperFactory.wrap(connection, ConnectionWrapperFactoryTest.WrapperInterface.class, wrapperImplementation, closeHandler)));
        wrapper.foo();
    }

    private interface WrapperInterface {
        void foo() throws SQLException;
    }
}

