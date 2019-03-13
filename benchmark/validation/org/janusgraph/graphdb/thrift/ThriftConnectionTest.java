/**
 * Copyright 2017 JanusGraph Authors
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package org.janusgraph.graphdb.thrift;


import org.apache.cassandra.config.Config;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.janusgraph.diskstorage.cassandra.thrift.thriftpool.CTConnection;
import org.janusgraph.diskstorage.cassandra.thrift.thriftpool.CTConnectionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ThriftConnectionTest {
    private static final int FRAME_SIZE = (15 * 1024) * 1024;

    private static final int TIMEOUT_MS = 5 * 1000;

    private static final int BACKLOG_PORT = 9098;

    private static final int LISTEN_PORT = 9099;

    private final Logger log = LoggerFactory.getLogger(ThriftConnectionTest.class);

    private Config factoryConfig;

    @Test
    public void testConnectionDropped() throws Exception {
        CTConnectionFactory connectionFactory = Mockito.spy(factoryConfig.build());
        CTConnection mockConnection = Mockito.spy(connectionFactory.makeObject("janusgraph"));
        Mockito.when(mockConnection.getConfig()).thenReturn(factoryConfig);
        Mockito.when(mockConnection.isOpen()).thenReturn(true);
        TTransport mockClient = Mockito.spy(mockConnection.getTransport());
        Assertions.assertTrue(connectionFactory.validateObject(null, mockConnection));
        Mockito.when(mockClient.readAll(new byte[0], 0, 0)).thenThrow(new TTransportException("Broken Pipe"));
        Assertions.assertTrue(mockClient.isOpen());
    }
}

