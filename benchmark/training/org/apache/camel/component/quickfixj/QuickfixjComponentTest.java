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


import Acceptor.SETTING_SOCKET_ACCEPT_PORT;
import Acceptor.SETTING_SOCKET_ACCEPT_PROTOCOL;
import ExchangePattern.InOnly;
import Initiator.SETTING_RECONNECT_INTERVAL;
import Initiator.SETTING_SOCKET_CONNECT_PORT;
import Initiator.SETTING_SOCKET_CONNECT_PROTOCOL;
import ProtocolFactory.VM_PIPE;
import QuickfixjEndpoint.EVENT_CATEGORY_KEY;
import SenderCompID.FIELD;
import Session.SETTING_USE_DATA_DICTIONARY;
import SessionFactory.ACCEPTOR_CONNECTION_TYPE;
import SessionFactory.INITIATOR_CONNECTION_TYPE;
import SessionFactory.SETTING_CONNECTION_TYPE;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.camel.CamelContext;
import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.support.service.ServiceHelper;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import quickfix.FixVersions;
import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.field.EmailThreadID;
import quickfix.field.EmailType;
import quickfix.field.Subject;
import quickfix.fix44.Email;
import quickfix.mina.ProtocolFactory;

import static QuickfixjEventCategory.AppMessageReceived;
import static QuickfixjEventCategory.SessionCreated;
import static QuickfixjEventCategory.SessionLogon;


public class QuickfixjComponentTest {
    private File settingsFile;

    private File settingsFile2;

    private File tempdir;

    private File tempdir2;

    private ClassLoader contextClassLoader;

    private SessionID sessionID;

    private SessionSettings settings;

    private QuickfixjComponent component;

    private CamelContext camelContext;

    private MessageFactory engineMessageFactory;

    private MessageStoreFactory engineMessageStoreFactory;

    private LogFactory engineLogFactory;

    @Test
    public void createEndpointBeforeComponentStart() throws Exception {
        setUpComponent();
        settings.setString(sessionID, SETTING_CONNECTION_TYPE, INITIATOR_CONNECTION_TYPE);
        settings.setLong(sessionID, SETTING_SOCKET_CONNECT_PORT, 1234);
        writeSettings(settings, true);
        // Should use cached QFJ engine
        Endpoint e1 = component.createEndpoint(getEndpointUri(settingsFile.getName(), null));
        Assert.assertThat(component.getProvisionalEngines().size(), CoreMatchers.is(1));
        Assert.assertThat(component.getProvisionalEngines().get(settingsFile.getName()), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(component.getProvisionalEngines().get(settingsFile.getName()).isInitialized(), CoreMatchers.is(true));
        Assert.assertThat(isStarted(), CoreMatchers.is(false));
        Assert.assertThat(component.getEngines().size(), CoreMatchers.is(0));
        Assert.assertThat(getSessionID(), CoreMatchers.is(CoreMatchers.nullValue()));
        writeSettings(settings, false);
        // Should use cached QFJ engine
        Endpoint e2 = component.createEndpoint(getEndpointUri(settingsFile2.getName(), null));
        Assert.assertThat(component.getProvisionalEngines().size(), CoreMatchers.is(2));
        Assert.assertThat(component.getProvisionalEngines().get(settingsFile.getName()), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(component.getProvisionalEngines().get(settingsFile.getName()).isInitialized(), CoreMatchers.is(true));
        Assert.assertThat(isStarted(), CoreMatchers.is(false));
        Assert.assertThat(component.getEngines().size(), CoreMatchers.is(0));
        Assert.assertThat(getSessionID(), CoreMatchers.is(CoreMatchers.nullValue()));
        // will start the component
        camelContext.start();
        Assert.assertThat(component.getProvisionalEngines().size(), CoreMatchers.is(0));
        Assert.assertThat(component.getEngines().size(), CoreMatchers.is(2));
        Assert.assertThat(component.getEngines().get(settingsFile.getName()).isInitialized(), CoreMatchers.is(true));
        Assert.assertThat(isStarted(), CoreMatchers.is(true));
        // Move these too an endpoint testcase if one exists
        Assert.assertThat(e1.isSingleton(), CoreMatchers.is(true));
        Assert.assertThat(isMultipleConsumersSupported(), CoreMatchers.is(true));
        Assert.assertThat(e2.isSingleton(), CoreMatchers.is(true));
        Assert.assertThat(isMultipleConsumersSupported(), CoreMatchers.is(true));
    }

    @Test
    public void createEndpointAfterComponentStart() throws Exception {
        setUpComponent();
        settings.setString(sessionID, SETTING_CONNECTION_TYPE, INITIATOR_CONNECTION_TYPE);
        settings.setLong(sessionID, SETTING_SOCKET_CONNECT_PORT, 1234);
        writeSettings();
        // will start the component
        camelContext.start();
        Endpoint e1 = component.createEndpoint(getEndpointUri(settingsFile.getName(), null));
        Assert.assertThat(component.getEngines().size(), CoreMatchers.is(1));
        Assert.assertThat(component.getEngines().get(settingsFile.getName()), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(component.getEngines().get(settingsFile.getName()).isInitialized(), CoreMatchers.is(true));
        Assert.assertThat(isStarted(), CoreMatchers.is(true));
        Assert.assertThat(component.getProvisionalEngines().size(), CoreMatchers.is(0));
        Assert.assertThat(getSessionID(), CoreMatchers.is(CoreMatchers.nullValue()));
        Endpoint e2 = component.createEndpoint(getEndpointUri(settingsFile.getName(), sessionID));
        Assert.assertThat(component.getEngines().size(), CoreMatchers.is(1));
        Assert.assertThat(component.getEngines().get(settingsFile.getName()), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(component.getEngines().get(settingsFile.getName()).isInitialized(), CoreMatchers.is(true));
        Assert.assertThat(isStarted(), CoreMatchers.is(true));
        Assert.assertThat(component.getProvisionalEngines().size(), CoreMatchers.is(0));
        Assert.assertThat(getSessionID(), CoreMatchers.is(sessionID));
    }

    @Test
    public void createEnginesLazily() throws Exception {
        setUpComponent();
        component.setLazyCreateEngines(true);
        settings.setString(sessionID, SETTING_CONNECTION_TYPE, INITIATOR_CONNECTION_TYPE);
        settings.setLong(sessionID, SETTING_SOCKET_CONNECT_PORT, 1234);
        writeSettings();
        // start the component
        camelContext.start();
        QuickfixjEndpoint e1 = ((QuickfixjEndpoint) (component.createEndpoint(getEndpointUri(settingsFile.getName(), null))));
        Assert.assertThat(component.getEngines().size(), CoreMatchers.is(1));
        Assert.assertThat(component.getProvisionalEngines().size(), CoreMatchers.is(0));
        Assert.assertThat(component.getEngines().get(settingsFile.getName()), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(component.getEngines().get(settingsFile.getName()).isInitialized(), CoreMatchers.is(false));
        Assert.assertThat(isStarted(), CoreMatchers.is(false));
        e1.ensureInitialized();
        Assert.assertThat(component.getEngines().get(settingsFile.getName()).isInitialized(), CoreMatchers.is(true));
        Assert.assertThat(isStarted(), CoreMatchers.is(true));
    }

    @Test
    public void createEndpointsInNonLazyComponent() throws Exception {
        setUpComponent();
        // configuration will be done per endpoint
        component.setLazyCreateEngines(false);
        settings.setString(sessionID, SETTING_CONNECTION_TYPE, INITIATOR_CONNECTION_TYPE);
        settings.setLong(sessionID, SETTING_SOCKET_CONNECT_PORT, 1234);
        writeSettings();
        // will start the component
        camelContext.start();
        QuickfixjEndpoint e1 = ((QuickfixjEndpoint) (component.createEndpoint(((getEndpointUri(settingsFile.getName(), null)) + "?lazyCreateEngine=true"))));
        Assert.assertThat(component.getEngines().get(settingsFile.getName()).isInitialized(), CoreMatchers.is(false));
        Assert.assertThat(isStarted(), CoreMatchers.is(false));
        Assert.assertThat(component.getEngines().get(settingsFile.getName()).isLazy(), CoreMatchers.is(true));
        e1.ensureInitialized();
        Assert.assertThat(component.getEngines().get(settingsFile.getName()).isInitialized(), CoreMatchers.is(true));
        Assert.assertThat(isStarted(), CoreMatchers.is(true));
        writeSettings(settings, false);
        // will use connector's lazyCreateEngines setting
        component.createEndpoint(getEndpointUri(settingsFile2.getName(), sessionID));
        Assert.assertThat(component.getEngines().get(settingsFile2.getName()).isInitialized(), CoreMatchers.is(true));
        Assert.assertThat(isStarted(), CoreMatchers.is(true));
        Assert.assertThat(component.getEngines().get(settingsFile2.getName()).isLazy(), CoreMatchers.is(false));
    }

    @Test
    public void createEndpointsInLazyComponent() throws Exception {
        setUpComponent();
        component.setLazyCreateEngines(true);
        settings.setString(sessionID, SETTING_CONNECTION_TYPE, INITIATOR_CONNECTION_TYPE);
        settings.setLong(sessionID, SETTING_SOCKET_CONNECT_PORT, 1234);
        writeSettings();
        // will start the component
        camelContext.start();
        // will use connector's lazyCreateEngines setting
        QuickfixjEndpoint e1 = ((QuickfixjEndpoint) (component.createEndpoint(getEndpointUri(settingsFile.getName(), null))));
        Assert.assertThat(component.getEngines().get(settingsFile.getName()).isInitialized(), CoreMatchers.is(false));
        Assert.assertThat(isStarted(), CoreMatchers.is(false));
        Assert.assertThat(component.getEngines().get(settingsFile.getName()).isLazy(), CoreMatchers.is(true));
        e1.ensureInitialized();
        Assert.assertThat(component.getEngines().get(settingsFile.getName()).isInitialized(), CoreMatchers.is(true));
        Assert.assertThat(isStarted(), CoreMatchers.is(true));
        writeSettings(settings, false);
        // will override connector's lazyCreateEngines setting
        component.createEndpoint(((getEndpointUri(settingsFile2.getName(), sessionID)) + "&lazyCreateEngine=false"));
        Assert.assertThat(component.getEngines().get(settingsFile2.getName()).isInitialized(), CoreMatchers.is(true));
        Assert.assertThat(isStarted(), CoreMatchers.is(true));
        Assert.assertThat(component.getEngines().get(settingsFile2.getName()).isLazy(), CoreMatchers.is(false));
    }

    @Test
    public void componentStop() throws Exception {
        setUpComponent();
        settings.setString(sessionID, SETTING_CONNECTION_TYPE, INITIATOR_CONNECTION_TYPE);
        settings.setLong(sessionID, SETTING_SOCKET_CONNECT_PORT, 1234);
        writeSettings();
        Endpoint endpoint = component.createEndpoint(getEndpointUri(settingsFile.getName(), null));
        final CountDownLatch latch = new CountDownLatch(1);
        Consumer consumer = endpoint.createConsumer(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                QuickfixjEventCategory eventCategory = ((QuickfixjEventCategory) (exchange.getIn().getHeader(EVENT_CATEGORY_KEY)));
                if (eventCategory == (SessionCreated)) {
                    latch.countDown();
                }
            }
        });
        ServiceHelper.startService(consumer);
        // Endpoint automatically starts the consumer
        Assert.assertThat(isStarted(), CoreMatchers.is(true));
        // will start the component
        camelContext.start();
        Assert.assertTrue("Session not created", latch.await(5000, TimeUnit.MILLISECONDS));
        component.stop();
        Assert.assertThat(isStarted(), CoreMatchers.is(false));
        // it should still be initialized (ready to start again)
        Assert.assertThat(component.getEngines().get(settingsFile.getName()).isInitialized(), CoreMatchers.is(true));
    }

    @Test
    public void messagePublication() throws Exception {
        setUpComponent();
        // Create settings file with both acceptor and initiator
        SessionSettings settings = new SessionSettings();
        settings.setString(SETTING_SOCKET_ACCEPT_PROTOCOL, ProtocolFactory.getTypeString(VM_PIPE));
        settings.setString(SETTING_SOCKET_CONNECT_PROTOCOL, ProtocolFactory.getTypeString(VM_PIPE));
        settings.setBool(SETTING_USE_DATA_DICTIONARY, false);
        SessionID acceptorSessionID = new SessionID(FixVersions.BEGINSTRING_FIX44, "ACCEPTOR", "INITIATOR");
        settings.setString(acceptorSessionID, SETTING_CONNECTION_TYPE, ACCEPTOR_CONNECTION_TYPE);
        settings.setLong(acceptorSessionID, SETTING_SOCKET_ACCEPT_PORT, 1234);
        setSessionID(settings, acceptorSessionID);
        SessionID initiatorSessionID = new SessionID(FixVersions.BEGINSTRING_FIX44, "INITIATOR", "ACCEPTOR");
        settings.setString(initiatorSessionID, SETTING_CONNECTION_TYPE, INITIATOR_CONNECTION_TYPE);
        settings.setLong(initiatorSessionID, SETTING_SOCKET_CONNECT_PORT, 1234);
        settings.setLong(initiatorSessionID, SETTING_RECONNECT_INTERVAL, 1);
        setSessionID(settings, initiatorSessionID);
        writeSettings(settings, true);
        Endpoint endpoint = component.createEndpoint(getEndpointUri(settingsFile.getName(), null));
        // Start the component and wait for the FIX sessions to be logged on
        final CountDownLatch logonLatch = new CountDownLatch(2);
        final CountDownLatch messageLatch = new CountDownLatch(2);
        Consumer consumer = endpoint.createConsumer(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                QuickfixjEventCategory eventCategory = ((QuickfixjEventCategory) (exchange.getIn().getHeader(EVENT_CATEGORY_KEY)));
                if (eventCategory == (SessionLogon)) {
                    logonLatch.countDown();
                } else
                    if (eventCategory == (AppMessageReceived)) {
                        messageLatch.countDown();
                    }

            }
        });
        ServiceHelper.startService(consumer);
        // will start the component
        camelContext.start();
        Assert.assertTrue("Session not created", logonLatch.await(5000, TimeUnit.MILLISECONDS));
        Endpoint producerEndpoint = component.createEndpoint(getEndpointUri(settingsFile.getName(), acceptorSessionID));
        Producer producer = producerEndpoint.createProducer();
        // FIX message to send
        Email email = new Email(new EmailThreadID("ID"), new EmailType(EmailType.NEW), new Subject("Test"));
        Exchange exchange = producer.getEndpoint().createExchange(InOnly);
        exchange.getIn().setBody(email);
        producer.process(exchange);
        // Produce with no session ID specified, session ID must be in message
        Producer producer2 = endpoint.createProducer();
        email.getHeader().setString(FIELD, acceptorSessionID.getSenderCompID());
        email.getHeader().setString(TargetCompID.FIELD, acceptorSessionID.getTargetCompID());
        producer2.process(exchange);
        Assert.assertTrue("Messages not received", messageLatch.await(5000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void userSpecifiedQuickfixjPlugins() throws Exception {
        setUpComponent(true);
        settings.setString(sessionID, SETTING_CONNECTION_TYPE, INITIATOR_CONNECTION_TYPE);
        settings.setLong(sessionID, SETTING_SOCKET_CONNECT_PORT, 1234);
        writeSettings();
        component.createEndpoint(getEndpointUri(settingsFile.getName(), null));
        // will start the component
        camelContext.start();
        Assert.assertThat(component.getEngines().size(), CoreMatchers.is(1));
        QuickfixjEngine engine = component.getEngines().values().iterator().next();
        Assert.assertThat(engine.getMessageFactory(), CoreMatchers.is(engineMessageFactory));
        Assert.assertThat(engine.getMessageStoreFactory(), CoreMatchers.is(engineMessageStoreFactory));
        Assert.assertThat(engine.getLogFactory(), CoreMatchers.is(engineLogFactory));
    }
}

