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
import FileLogFactory.SETTING_FILE_LOG_PATH;
import FileStoreFactory.SETTING_FILE_STORE_PATH;
import Initiator.SETTING_SOCKET_CONNECT_PORT;
import JdbcSetting.SETTING_JDBC_DRIVER;
import JdbcSetting.SETTING_JDBC_DS_NAME;
import JdbcSetting.SETTING_LOG_EVENT_TABLE;
import QuickfixjEngine.SETTING_THREAD_MODEL;
import QuickfixjEngine.SETTING_USE_JMX;
import QuickfixjEngine.ThreadModel.ThreadPerSession;
import SLF4JLogFactory.SETTING_EVENT_CATEGORY;
import ScreenLogFactory.SETTING_LOG_EVENTS;
import SessionFactory.ACCEPTOR_CONNECTION_TYPE;
import SessionFactory.INITIATOR_CONNECTION_TYPE;
import SessionFactory.SETTING_CONNECTION_TYPE;
import SleepycatStoreFactory.SETTING_SLEEPYCAT_DATABASE_DIR;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.camel.test.junit4.TestSupport;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.mockito.Mockito;
import quickfix.DefaultMessageFactory;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.FixVersions;
import quickfix.JdbcLogFactory;
import quickfix.JdbcStoreFactory;
import quickfix.LogFactory;
import quickfix.MemoryStoreFactory;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.SLF4JLogFactory;
import quickfix.ScreenLogFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SleepycatStoreFactory;
import quickfix.SocketAcceptor;
import quickfix.SocketInitiator;
import quickfix.ThreadedSocketAcceptor;
import quickfix.ThreadedSocketInitiator;


public class QuickfixjEngineTest extends TestSupport {
    private File settingsFile;

    private ClassLoader contextClassLoader;

    private SessionSettings settings;

    private SessionID sessionID;

    private File tempdir;

    private QuickfixjEngine quickfixjEngine;

    @Test(expected = IllegalArgumentException.class)
    public void missingSettingsResource() throws Exception {
        new QuickfixjEngine("quickfix:test", "bogus.cfg");
    }

    @Test
    public void defaultInitiator() throws Exception {
        settings.setString(sessionID, SETTING_CONNECTION_TYPE, INITIATOR_CONNECTION_TYPE);
        writeSettings();
        quickfixjEngine = new QuickfixjEngine("quickfix:test", settingsFile.getName());
        assertThat(quickfixjEngine.getInitiator(), CoreMatchers.instanceOf(SocketInitiator.class));
        assertThat(quickfixjEngine.getAcceptor(), CoreMatchers.nullValue());
        assertDefaultConfiguration(quickfixjEngine);
    }

    @Test
    public void threadPerSessionInitiator() throws Exception {
        settings.setString(SETTING_THREAD_MODEL, ThreadPerSession.toString());
        settings.setString(sessionID, SETTING_CONNECTION_TYPE, INITIATOR_CONNECTION_TYPE);
        writeSettings();
        quickfixjEngine = new QuickfixjEngine("quickfix:test", settingsFile.getName());
        assertThat(quickfixjEngine.getInitiator(), CoreMatchers.instanceOf(ThreadedSocketInitiator.class));
        assertThat(quickfixjEngine.getAcceptor(), CoreMatchers.nullValue());
        assertDefaultConfiguration(quickfixjEngine);
    }

    @Test
    public void defaultAcceptor() throws Exception {
        settings.setString(sessionID, SETTING_CONNECTION_TYPE, ACCEPTOR_CONNECTION_TYPE);
        settings.setLong(sessionID, SETTING_SOCKET_ACCEPT_PORT, 1234);
        writeSettings();
        quickfixjEngine = new QuickfixjEngine("quickfix:test", settingsFile.getName());
        assertThat(quickfixjEngine.getInitiator(), CoreMatchers.nullValue());
        assertThat(quickfixjEngine.getAcceptor(), CoreMatchers.instanceOf(SocketAcceptor.class));
        assertDefaultConfiguration(quickfixjEngine);
    }

    @Test
    public void threadPerSessionAcceptor() throws Exception {
        settings.setString(SETTING_THREAD_MODEL, ThreadPerSession.toString());
        settings.setString(sessionID, SETTING_CONNECTION_TYPE, ACCEPTOR_CONNECTION_TYPE);
        settings.setLong(sessionID, SETTING_SOCKET_ACCEPT_PORT, 1234);
        writeSettings();
        quickfixjEngine = new QuickfixjEngine("quickfix:test", settingsFile.getName());
        assertThat(quickfixjEngine.getInitiator(), CoreMatchers.nullValue());
        assertThat(quickfixjEngine.getAcceptor(), CoreMatchers.instanceOf(ThreadedSocketAcceptor.class));
        assertDefaultConfiguration(quickfixjEngine);
    }

    @Test
    public void minimalInitiatorAndAcceptor() throws Exception {
        settings.setString(sessionID, SETTING_CONNECTION_TYPE, ACCEPTOR_CONNECTION_TYPE);
        settings.setLong(sessionID, SETTING_SOCKET_ACCEPT_PORT, 1234);
        SessionID initiatorSessionID = new SessionID(FixVersions.BEGINSTRING_FIX44, "FARGLE", "BARGLE");
        settings.setString(initiatorSessionID, SETTING_CONNECTION_TYPE, INITIATOR_CONNECTION_TYPE);
        TestSupport.setSessionID(settings, initiatorSessionID);
        writeSettings();
        quickfixjEngine = new QuickfixjEngine("quickfix:test", settingsFile.getName());
        assertThat(quickfixjEngine.getInitiator(), CoreMatchers.notNullValue());
        assertThat(quickfixjEngine.getAcceptor(), CoreMatchers.notNullValue());
        assertDefaultConfiguration(quickfixjEngine);
    }

    @Test
    public void inferFileStore() throws Exception {
        settings.setString(SETTING_FILE_STORE_PATH, tempdir.toString());
        settings.setString(sessionID, SETTING_CONNECTION_TYPE, INITIATOR_CONNECTION_TYPE);
        writeSettings();
        quickfixjEngine = new QuickfixjEngine("quickfix:test", settingsFile.getName());
        assertThat(quickfixjEngine.getInitiator(), CoreMatchers.notNullValue());
        assertThat(quickfixjEngine.getAcceptor(), CoreMatchers.nullValue());
        assertThat(quickfixjEngine.getUri(), CoreMatchers.is("quickfix:test"));
        assertThat(quickfixjEngine.getMessageStoreFactory(), CoreMatchers.instanceOf(FileStoreFactory.class));
        assertThat(quickfixjEngine.getLogFactory(), CoreMatchers.instanceOf(ScreenLogFactory.class));
        assertThat(quickfixjEngine.getMessageFactory(), CoreMatchers.instanceOf(DefaultMessageFactory.class));
    }

    // NOTE This is a little strange. If the JDBC driver is set and no log settings are found,
    // then we use JDBC for both the message store and the log.
    @Test
    public void inferJdbcStoreAndLog() throws Exception {
        // If there is a setting of the LOG_EVENT_TABLE, we should create a jdbcLogFactory for it
        settings.setString(SETTING_JDBC_DRIVER, "driver");
        settings.setString(SETTING_LOG_EVENT_TABLE, "table");
        settings.setString(sessionID, SETTING_CONNECTION_TYPE, INITIATOR_CONNECTION_TYPE);
        writeSettings();
        quickfixjEngine = new QuickfixjEngine("quickfix:test", settingsFile.getName());
        assertThat(quickfixjEngine.getInitiator(), CoreMatchers.notNullValue());
        assertThat(quickfixjEngine.getAcceptor(), CoreMatchers.nullValue());
        assertThat(quickfixjEngine.getMessageStoreFactory(), CoreMatchers.instanceOf(JdbcStoreFactory.class));
        assertThat(quickfixjEngine.getLogFactory(), CoreMatchers.instanceOf(JdbcLogFactory.class));
        assertThat(quickfixjEngine.getMessageFactory(), CoreMatchers.instanceOf(DefaultMessageFactory.class));
    }

    @Test
    public void inferJdbcStoreViaJNDI() throws Exception {
        // If there is a setting of the LOG_EVENT_TABLE, we should create a jdbcLogFactory for it
        settings.setString(SETTING_JDBC_DS_NAME, "ds_name");
        settings.setString(SETTING_LOG_EVENT_TABLE, "table");
        settings.setString(sessionID, SETTING_CONNECTION_TYPE, INITIATOR_CONNECTION_TYPE);
        writeSettings();
        quickfixjEngine = new QuickfixjEngine("quickfix:test", settingsFile.getName());
        assertThat(quickfixjEngine.getInitiator(), CoreMatchers.notNullValue());
        assertThat(quickfixjEngine.getAcceptor(), CoreMatchers.nullValue());
        assertThat(quickfixjEngine.getMessageStoreFactory(), CoreMatchers.instanceOf(JdbcStoreFactory.class));
        assertThat(quickfixjEngine.getLogFactory(), CoreMatchers.instanceOf(JdbcLogFactory.class));
        assertThat(quickfixjEngine.getMessageFactory(), CoreMatchers.instanceOf(DefaultMessageFactory.class));
    }

    @Test
    public void ambiguousMessageStore() throws Exception {
        settings.setString(SETTING_FILE_STORE_PATH, tempdir.toString());
        settings.setString(SETTING_JDBC_DRIVER, "driver");
        settings.setString(sessionID, SETTING_CONNECTION_TYPE, INITIATOR_CONNECTION_TYPE);
        writeSettings();
        doAmbiguityTest("Ambiguous message store");
    }

    @Test
    public void inferJdbcStoreWithInferredLog() throws Exception {
        settings.setString(SETTING_JDBC_DRIVER, "driver");
        settings.setBool(SETTING_LOG_EVENTS, true);
        settings.setString(sessionID, SETTING_CONNECTION_TYPE, INITIATOR_CONNECTION_TYPE);
        writeSettings();
        quickfixjEngine = new QuickfixjEngine("quickfix:test", settingsFile.getName());
        assertThat(quickfixjEngine.getInitiator(), CoreMatchers.notNullValue());
        assertThat(quickfixjEngine.getAcceptor(), CoreMatchers.nullValue());
        assertThat(quickfixjEngine.getMessageStoreFactory(), CoreMatchers.instanceOf(JdbcStoreFactory.class));
        assertThat(quickfixjEngine.getLogFactory(), CoreMatchers.instanceOf(ScreenLogFactory.class));
        assertThat(quickfixjEngine.getMessageFactory(), CoreMatchers.instanceOf(DefaultMessageFactory.class));
    }

    @Test
    public void inferSleepycatStore() throws Exception {
        settings.setString(SETTING_SLEEPYCAT_DATABASE_DIR, tempdir.toString());
        settings.setString(sessionID, SETTING_CONNECTION_TYPE, INITIATOR_CONNECTION_TYPE);
        writeSettings();
        quickfixjEngine = new QuickfixjEngine("quickfix:test", settingsFile.getName());
        assertThat(quickfixjEngine.getInitiator(), CoreMatchers.notNullValue());
        assertThat(quickfixjEngine.getAcceptor(), CoreMatchers.nullValue());
        assertThat(quickfixjEngine.getMessageStoreFactory(), CoreMatchers.instanceOf(SleepycatStoreFactory.class));
        assertThat(quickfixjEngine.getLogFactory(), CoreMatchers.instanceOf(ScreenLogFactory.class));
        assertThat(quickfixjEngine.getMessageFactory(), CoreMatchers.instanceOf(DefaultMessageFactory.class));
    }

    @Test
    public void inferFileLog() throws Exception {
        settings.setString(SETTING_FILE_LOG_PATH, tempdir.toString());
        settings.setString(sessionID, SETTING_CONNECTION_TYPE, INITIATOR_CONNECTION_TYPE);
        writeSettings();
        quickfixjEngine = new QuickfixjEngine("quickfix:test", settingsFile.getName());
        assertThat(quickfixjEngine.getInitiator(), CoreMatchers.notNullValue());
        assertThat(quickfixjEngine.getAcceptor(), CoreMatchers.nullValue());
        assertThat(quickfixjEngine.getMessageStoreFactory(), CoreMatchers.instanceOf(MemoryStoreFactory.class));
        assertThat(quickfixjEngine.getLogFactory(), CoreMatchers.instanceOf(FileLogFactory.class));
        assertThat(quickfixjEngine.getMessageFactory(), CoreMatchers.instanceOf(DefaultMessageFactory.class));
    }

    @Test
    public void inferSlf4jLog() throws Exception {
        settings.setString(SETTING_EVENT_CATEGORY, "Events");
        settings.setString(sessionID, SETTING_CONNECTION_TYPE, INITIATOR_CONNECTION_TYPE);
        writeSettings();
        quickfixjEngine = new QuickfixjEngine("quickfix:test", settingsFile.getName());
        assertThat(quickfixjEngine.getInitiator(), CoreMatchers.notNullValue());
        assertThat(quickfixjEngine.getAcceptor(), CoreMatchers.nullValue());
        assertThat(quickfixjEngine.getMessageStoreFactory(), CoreMatchers.instanceOf(MemoryStoreFactory.class));
        assertThat(quickfixjEngine.getLogFactory(), CoreMatchers.instanceOf(SLF4JLogFactory.class));
        assertThat(quickfixjEngine.getMessageFactory(), CoreMatchers.instanceOf(DefaultMessageFactory.class));
    }

    @Test
    public void ambiguousLog() throws Exception {
        settings.setString(SETTING_FILE_LOG_PATH, tempdir.toString());
        settings.setBool(SETTING_LOG_EVENTS, true);
        settings.setString(sessionID, SETTING_CONNECTION_TYPE, INITIATOR_CONNECTION_TYPE);
        writeSettings();
        doAmbiguityTest("Ambiguous log");
    }

    @Test
    public void useExplicitComponentImplementations() throws Exception {
        settings.setString(SETTING_EVENT_CATEGORY, "Events");
        settings.setString(sessionID, SETTING_CONNECTION_TYPE, INITIATOR_CONNECTION_TYPE);
        writeSettings();
        MessageStoreFactory messageStoreFactory = Mockito.mock(MessageStoreFactory.class);
        LogFactory logFactory = Mockito.mock(LogFactory.class);
        MessageFactory messageFactory = Mockito.mock(MessageFactory.class);
        quickfixjEngine = new QuickfixjEngine("quickfix:test", settingsFile.getName(), messageStoreFactory, logFactory, messageFactory);
        assertThat(quickfixjEngine.getMessageStoreFactory(), CoreMatchers.is(messageStoreFactory));
        assertThat(quickfixjEngine.getLogFactory(), CoreMatchers.is(logFactory));
        assertThat(quickfixjEngine.getMessageFactory(), CoreMatchers.is(messageFactory));
    }

    @Test
    public void enableJmxForInitiator() throws Exception {
        settings.setBool(SETTING_USE_JMX, true);
        settings.setString(sessionID, SETTING_CONNECTION_TYPE, INITIATOR_CONNECTION_TYPE);
        settings.setLong(sessionID, SETTING_SOCKET_CONNECT_PORT, 1234);
        writeSettings();
        quickfixjEngine = new QuickfixjEngine("quickfix:test", settingsFile.getName());
        quickfixjEngine.start();
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectName> n = mbeanServer.queryNames(new ObjectName("org.quickfixj:type=Connector,role=Initiator,*"), null);
        assertFalse("QFJ mbean not registered", n.isEmpty());
    }

    @Test
    public void enableJmxForAcceptor() throws Exception {
        settings.setBool(SETTING_USE_JMX, true);
        settings.setString(sessionID, SETTING_CONNECTION_TYPE, ACCEPTOR_CONNECTION_TYPE);
        settings.setLong(sessionID, SETTING_SOCKET_ACCEPT_PORT, 1234);
        writeSettings();
        quickfixjEngine = new QuickfixjEngine("quickfix:test", settingsFile.getName());
        quickfixjEngine.start();
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectName> n = mbeanServer.queryNames(new ObjectName("org.quickfixj:type=Connector,role=Acceptor,*"), null);
        assertFalse("QFJ mbean not registered", n.isEmpty());
    }

    @Test
    public void sessionEvents() throws Exception {
        SessionID acceptorSessionID = new SessionID(FixVersions.BEGINSTRING_FIX42, "MARKET", "TRADER");
        SessionID initiatorSessionID = new SessionID(FixVersions.BEGINSTRING_FIX42, "TRADER", "MARKET");
        quickfixjEngine = new QuickfixjEngine("quickfix:test", "examples/inprocess.cfg");
        doLogonEventsTest(acceptorSessionID, initiatorSessionID, quickfixjEngine);
        doApplicationMessageEventsTest(acceptorSessionID, initiatorSessionID, quickfixjEngine);
        doLogoffEventsTest(acceptorSessionID, initiatorSessionID, quickfixjEngine);
    }

    private static class EventRecord {
        final QuickfixjEventCategory eventCategory;

        final SessionID sessionID;

        final Message message;

        EventRecord(QuickfixjEventCategory eventCategory, SessionID sessionID, Message message) {
            this.eventCategory = eventCategory;
            this.sessionID = sessionID;
            this.message = message;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + ((eventCategory) == null ? 0 : eventCategory.hashCode());
            result = (prime * result) + ((sessionID) == null ? 0 : sessionID.hashCode());
            result = (prime * result) + ((message) == null ? 1231 : 1237);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof QuickfixjEngineTest.EventRecord)) {
                return false;
            }
            QuickfixjEngineTest.EventRecord other = ((QuickfixjEngineTest.EventRecord) (obj));
            boolean answer = (equal(eventCategory, other.eventCategory)) && (equal(sessionID, other.sessionID));
            // we do just require a "relaxed" comparison of the messages, that's they should both be either null or both not null.
            // this is required so that we can properly assert on the events being fired
            if ((message) == null) {
                answer &= (other.message) == null;
            } else {
                answer &= (other.message) != null;
            }
            return answer;
        }

        @Override
        public String toString() {
            return ((((("EventRecord [eventCategory=" + (eventCategory)) + ", sessionID=") + (sessionID)) + ", message=") + (message)) + "]";
        }
    }
}

