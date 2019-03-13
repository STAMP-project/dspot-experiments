/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.pgevent;


import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.jdbc.PGDataSource;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.pgevent.InvalidStateException;
import org.apache.camel.component.pgevent.PgEventEndpoint;
import org.apache.camel.component.pgevent.PgEventProducer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class PgEventProducerTest {
    private PgEventEndpoint endpoint = Mockito.mock(PgEventEndpoint.class);

    private PGDataSource dataSource = Mockito.mock(PGDataSource.class);

    private PGConnection connection = Mockito.mock(PGConnection.class);

    private PreparedStatement statement = Mockito.mock(PreparedStatement.class);

    private Exchange exchange = Mockito.mock(Exchange.class);

    private Message message = Mockito.mock(Message.class);

    @Test
    public void testPgEventProducerStart() throws Exception {
        Mockito.when(endpoint.getDatasource()).thenReturn(dataSource);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        PgEventProducer producer = new PgEventProducer(endpoint);
        producer.start();
        Assert.assertTrue(producer.isStarted());
    }

    @Test
    public void testPgEventProducerStop() throws Exception {
        Mockito.when(endpoint.getDatasource()).thenReturn(dataSource);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        PgEventProducer producer = new PgEventProducer(endpoint);
        producer.start();
        producer.stop();
        Mockito.verify(connection).close();
        Assert.assertTrue(producer.isStopped());
    }

    @Test(expected = InvalidStateException.class)
    public void testPgEventProducerProcessDbThrowsInvalidStateException() throws Exception {
        Mockito.when(endpoint.getDatasource()).thenReturn(dataSource);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(connection.isClosed()).thenThrow(new SQLException("DB problem occurred"));
        PgEventProducer producer = new PgEventProducer(endpoint);
        producer.start();
        producer.process(exchange);
    }

    @Test
    public void testPgEventProducerProcessDbConnectionClosed() throws Exception {
        PGConnection connectionNew = Mockito.mock(PGConnection.class);
        Mockito.when(endpoint.getDatasource()).thenReturn(dataSource);
        Mockito.when(dataSource.getConnection()).thenReturn(connection, connectionNew);
        Mockito.when(connection.isClosed()).thenReturn(true);
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getBody(String.class)).thenReturn("pgevent");
        Mockito.when(endpoint.getChannel()).thenReturn("camel");
        Mockito.when(connectionNew.prepareStatement("NOTIFY camel, 'pgevent'")).thenReturn(statement);
        PgEventProducer producer = new PgEventProducer(endpoint);
        producer.start();
        producer.process(exchange);
        Mockito.verify(statement).execute();
    }

    @Test
    public void testPgEventProducerProcessServerMinimumVersionMatched() throws Exception {
        CallableStatement statement = Mockito.mock(CallableStatement.class);
        Mockito.when(endpoint.getDatasource()).thenReturn(dataSource);
        Mockito.when(connection.isClosed()).thenReturn(false);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getBody(String.class)).thenReturn("pgevent");
        Mockito.when(endpoint.getChannel()).thenReturn("camel");
        Mockito.when(connection.isServerMinimumVersion(9, 0)).thenReturn(true);
        Mockito.when(connection.prepareCall(ArgumentMatchers.anyString())).thenReturn(statement);
        PgEventProducer producer = new PgEventProducer(endpoint);
        producer.start();
        producer.process(exchange);
        Mockito.verify(statement).execute();
    }

    @Test
    public void testPgEventProducerProcessServerMinimumVersionNotMatched() throws Exception {
        Mockito.when(endpoint.getDatasource()).thenReturn(dataSource);
        Mockito.when(connection.isClosed()).thenReturn(false);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(exchange.getIn()).thenReturn(message);
        Mockito.when(message.getBody(String.class)).thenReturn("pgevent");
        Mockito.when(endpoint.getChannel()).thenReturn("camel");
        Mockito.when(connection.isServerMinimumVersion(9, 0)).thenReturn(false);
        Mockito.when(connection.prepareStatement("NOTIFY camel, 'pgevent'")).thenReturn(statement);
        PgEventProducer producer = new PgEventProducer(endpoint);
        producer.start();
        producer.process(exchange);
        Mockito.verify(statement).execute();
    }
}

