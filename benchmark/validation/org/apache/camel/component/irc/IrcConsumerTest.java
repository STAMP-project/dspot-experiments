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
package org.apache.camel.component.irc;


import org.apache.camel.Processor;
import org.junit.Test;
import org.mockito.Mockito;
import org.schwering.irc.lib.IRCConnection;
import org.schwering.irc.lib.IRCEventAdapter;


public class IrcConsumerTest {
    private IRCConnection connection;

    private Processor processor;

    private IrcEndpoint endpoint;

    private IrcConfiguration configuration;

    private IrcConsumer consumer;

    private IRCEventAdapter listener;

    @Test
    public void doStopTest() throws Exception {
        consumer.doStop();
        Mockito.verify(connection).doPart("#chan1");
        Mockito.verify(connection).doPart("#chan2");
        Mockito.verify(connection).removeIRCEventListener(listener);
    }

    @Test
    public void doStartTest() throws Exception {
        consumer.doStart();
        Mockito.verify(connection).addIRCEventListener(listener);
        Mockito.verify(endpoint).joinChannels();
    }
}

