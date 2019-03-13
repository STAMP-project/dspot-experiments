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


import IRCConstants.ERR_NICKNAMEINUSE;
import org.junit.Test;
import org.mockito.Mockito;
import org.schwering.irc.lib.IRCConnection;


public class IrcEndpointTest {
    private IrcComponent component;

    private IrcConfiguration configuration;

    private IRCConnection connection;

    private IrcEndpoint endpoint;

    @Test
    public void doJoinChannelTestNoKey() throws Exception {
        endpoint.joinChannel("#chan1");
        Mockito.verify(connection).doJoin("#chan1");
    }

    @Test
    public void doJoinChannelTestKey() throws Exception {
        endpoint.joinChannel("#chan2");
        Mockito.verify(connection).doJoin("#chan2", "chan2key");
    }

    @Test
    public void doJoinChannels() throws Exception {
        endpoint.joinChannels();
        Mockito.verify(connection).doJoin("#chan1");
        Mockito.verify(connection).doJoin("#chan2", "chan2key");
    }

    @Test
    public void doHandleIrcErrorNickInUse() throws Exception {
        Mockito.when(connection.getNick()).thenReturn("nick");
        endpoint.handleIrcError(ERR_NICKNAMEINUSE, "foo");
        Mockito.verify(connection).doNick("nick-");
        Mockito.when(connection.getNick()).thenReturn("nick---");
        // confirm doNick was not called
        Mockito.verify(connection, Mockito.never()).doNick("foo");
    }
}

