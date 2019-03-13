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
package org.apache.camel.component.quickfixj;


import MsgType.FIELD;
import MsgType.ORDER_SINGLE;
import QuickfixjEndpoint.DATA_DICTIONARY_KEY;
import QuickfixjEndpoint.EVENT_CATEGORY_KEY;
import QuickfixjEndpoint.MESSAGE_TYPE_KEY;
import QuickfixjEndpoint.SESSION_ID_KEY;
import QuickfixjEventCategory.AppMessageSent;
import java.io.File;
import org.apache.camel.Exchange;
import org.apache.camel.component.quickfixj.converter.QuickfixjConverters;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import quickfix.DataDictionary;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.fix44.Message.Header.NoHops;


public class QuickfixjConvertersTest extends CamelTestSupport {
    private File settingsFile;

    private ClassLoader contextClassLoader;

    private SessionSettings settings;

    private File tempdir;

    private QuickfixjEngine quickfixjEngine;

    @Test
    public void convertSessionID() {
        Object value = context.getTypeConverter().convertTo(SessionID.class, "FIX.4.0:FOO->BAR");
        assertThat(value, CoreMatchers.instanceOf(SessionID.class));
        assertThat(((SessionID) (value)), CoreMatchers.is(new SessionID("FIX.4.0", "FOO", "BAR")));
    }

    @Test
    public void convertToExchange() {
        SessionID sessionID = new SessionID("FIX.4.0", "FOO", "BAR");
        QuickfixjEndpoint endpoint = new QuickfixjEndpoint(null, "", new QuickfixjComponent(new DefaultCamelContext()));
        Message message = new Message();
        message.getHeader().setString(FIELD, ORDER_SINGLE);
        Exchange exchange = QuickfixjConverters.toExchange(endpoint, sessionID, message, AppMessageSent);
        assertThat(((SessionID) (exchange.getIn().getHeader(SESSION_ID_KEY))), CoreMatchers.is(sessionID));
        assertThat(((QuickfixjEventCategory) (exchange.getIn().getHeader(EVENT_CATEGORY_KEY))), CoreMatchers.is(AppMessageSent));
        assertThat(((String) (exchange.getIn().getHeader(MESSAGE_TYPE_KEY))), CoreMatchers.is(ORDER_SINGLE));
    }

    @Test
    public void convertToExchangeWithNullMessage() {
        SessionID sessionID = new SessionID("FIX.4.0", "FOO", "BAR");
        QuickfixjEndpoint endpoint = new QuickfixjEndpoint(null, "", new QuickfixjComponent(new DefaultCamelContext()));
        Exchange exchange = QuickfixjConverters.toExchange(endpoint, sessionID, null, AppMessageSent);
        assertThat(((SessionID) (exchange.getIn().getHeader(SESSION_ID_KEY))), CoreMatchers.is(sessionID));
        assertThat(((QuickfixjEventCategory) (exchange.getIn().getHeader(EVENT_CATEGORY_KEY))), CoreMatchers.is(AppMessageSent));
        assertThat(exchange.getIn().getHeader(MESSAGE_TYPE_KEY), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void convertMessageWithoutRepeatingGroups() {
        String data = "8=FIX.4.0\u00019=100\u000135=D\u000134=2\u000149=TW\u000156=ISLD\u000111=ID\u000121=1\u0001" + "40=1\u000154=1\u000140=2\u000138=200\u000155=INTC\u000110=160\u0001";
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        Object value = context.getTypeConverter().convertTo(Message.class, exchange, data);
        assertThat(value, CoreMatchers.instanceOf(Message.class));
    }

    @Test
    public void convertMessageWithRepeatingGroupsUsingSessionID() throws Exception {
        SessionID sessionID = new SessionID("FIX.4.4", "FOO", "BAR");
        createSession(sessionID);
        try {
            String data = "8=FIX.4.4\u00019=40\u000135=A\u0001" + ("627=2\u0001628=FOO\u0001628=BAR\u0001" + "98=0\u0001384=2\u0001372=D\u0001385=R\u0001372=8\u0001385=S\u000110=230\u0001");
            Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
            exchange.getIn().setHeader(SESSION_ID_KEY, sessionID);
            exchange.getIn().setBody(data);
            Message message = exchange.getIn().getBody(Message.class);
            NoHops hop = new NoHops();
            message.getHeader().getGroup(1, hop);
            assertEquals("FOO", hop.getString(HopCompID.FIELD));
            message.getHeader().getGroup(2, hop);
            assertEquals("BAR", hop.getString(HopCompID.FIELD));
        } finally {
            quickfixjEngine.stop();
        }
    }

    @Test
    public void convertMessageWithRepeatingGroupsUsingExchangeDictionary() throws Exception {
        SessionID sessionID = new SessionID("FIX.4.4", "FOO", "BAR");
        createSession(sessionID);
        try {
            String data = "8=FIX.4.4\u00019=40\u000135=A\u0001" + ("627=2\u0001628=FOO\u0001628=BAR\u0001" + "98=0\u0001384=2\u0001372=D\u0001385=R\u0001372=8\u0001385=S\u000110=230\u0001");
            Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
            exchange.setProperty(DATA_DICTIONARY_KEY, new DataDictionary("FIX44.xml"));
            exchange.getIn().setBody(data);
            Message message = exchange.getIn().getBody(Message.class);
            NoHops hop = new NoHops();
            message.getHeader().getGroup(1, hop);
            assertEquals("FOO", hop.getString(HopCompID.FIELD));
            message.getHeader().getGroup(2, hop);
            assertEquals("BAR", hop.getString(HopCompID.FIELD));
        } finally {
            quickfixjEngine.stop();
        }
    }

    @Test
    public void convertMessageWithRepeatingGroupsUsingExchangeDictionaryResource() throws Exception {
        SessionID sessionID = new SessionID("FIX.4.4", "FOO", "BAR");
        createSession(sessionID);
        try {
            String data = "8=FIX.4.4\u00019=40\u000135=A\u0001" + ("627=2\u0001628=FOO\u0001628=BAR\u0001" + "98=0\u0001384=2\u0001372=D\u0001385=R\u0001372=8\u0001385=S\u000110=230\u0001");
            Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
            exchange.setProperty(DATA_DICTIONARY_KEY, "FIX44.xml");
            exchange.getIn().setBody(data);
            Message message = exchange.getIn().getBody(Message.class);
            NoHops hop = new NoHops();
            message.getHeader().getGroup(1, hop);
            assertEquals("FOO", hop.getString(HopCompID.FIELD));
            message.getHeader().getGroup(2, hop);
            assertEquals("BAR", hop.getString(HopCompID.FIELD));
        } finally {
            quickfixjEngine.stop();
        }
    }
}

