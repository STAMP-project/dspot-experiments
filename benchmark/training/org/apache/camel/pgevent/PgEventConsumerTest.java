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
import java.sql.PreparedStatement;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.pgevent.PgEventConsumer;
import org.apache.camel.component.pgevent.PgEventEndpoint;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class PgEventConsumerTest {
    @Test
    public void testPgEventConsumerStart() throws Exception {
        PGDataSource dataSource = Mockito.mock(PGDataSource.class);
        PGConnection connection = Mockito.mock(PGConnection.class);
        PreparedStatement statement = Mockito.mock(PreparedStatement.class);
        PgEventEndpoint endpoint = Mockito.mock(PgEventEndpoint.class);
        Processor processor = Mockito.mock(Processor.class);
        Mockito.when(endpoint.getDatasource()).thenReturn(dataSource);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(connection.prepareStatement("LISTEN camel")).thenReturn(statement);
        Mockito.when(endpoint.getChannel()).thenReturn("camel");
        PgEventConsumer consumer = new PgEventConsumer(endpoint, processor);
        consumer.start();
        Mockito.verify(connection).addNotificationListener("camel", "camel", consumer);
        Assert.assertTrue(consumer.isStarted());
    }

    @Test
    public void testPgEventConsumerStop() throws Exception {
        PGDataSource dataSource = Mockito.mock(PGDataSource.class);
        PGConnection connection = Mockito.mock(PGConnection.class);
        PreparedStatement statement = Mockito.mock(PreparedStatement.class);
        PgEventEndpoint endpoint = Mockito.mock(PgEventEndpoint.class);
        Processor processor = Mockito.mock(Processor.class);
        Mockito.when(endpoint.getDatasource()).thenReturn(dataSource);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(connection.prepareStatement("LISTEN camel")).thenReturn(statement);
        Mockito.when(endpoint.getChannel()).thenReturn("camel");
        Mockito.when(connection.prepareStatement("UNLISTEN camel")).thenReturn(statement);
        PgEventConsumer consumer = new PgEventConsumer(endpoint, processor);
        consumer.start();
        consumer.stop();
        Mockito.verify(connection).removeNotificationListener("camel");
        Mockito.verify(connection).close();
        Assert.assertTrue(consumer.isStopped());
    }

    @Test
    public void testPgEventNotification() throws Exception {
        PgEventEndpoint endpoint = Mockito.mock(PgEventEndpoint.class);
        Processor processor = Mockito.mock(Processor.class);
        Exchange exchange = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(endpoint.createExchange()).thenReturn(exchange);
        Mockito.when(exchange.getIn()).thenReturn(message);
        PgEventConsumer consumer = new PgEventConsumer(endpoint, processor);
        consumer.notification(1, "camel", "some event");
        Mockito.verify(message).setHeader("channel", "camel");
        Mockito.verify(message).setBody("some event");
        Mockito.verify(processor).process(exchange);
    }
}

