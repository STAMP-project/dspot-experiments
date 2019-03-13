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
package org.apache.activemq.config;


import Session.AUTO_ACKNOWLEDGE;
import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.List;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.sql.DataSource;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.broker.region.policy.FixedSizedSubscriptionRecoveryPolicy;
import org.apache.activemq.broker.region.policy.LastImageSubscriptionRecoveryPolicy;
import org.apache.activemq.broker.region.policy.NoSubscriptionRecoveryPolicy;
import org.apache.activemq.broker.region.policy.RoundRobinDispatchPolicy;
import org.apache.activemq.broker.region.policy.SimpleDispatchPolicy;
import org.apache.activemq.broker.region.policy.StrictOrderDispatchPolicy;
import org.apache.activemq.broker.region.policy.SubscriptionRecoveryPolicy;
import org.apache.activemq.broker.region.policy.TimedSubscriptionRecoveryPolicy;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.network.NetworkConnector;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.store.jdbc.JDBCPersistenceAdapter;
import org.apache.activemq.store.jdbc.adapter.TransactDatabaseLocker;
import org.apache.activemq.store.journal.JournalPersistenceAdapter;
import org.apache.activemq.store.memory.MemoryPersistenceAdapter;
import org.apache.activemq.transport.tcp.TcpTransportServer;
import org.apache.activemq.usage.SystemUsage;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;


public class ConfigTest {
    protected static final String JOURNAL_ROOT = "target/test-data/";

    protected static final String DERBY_ROOT = "target/test-data/";

    protected static final String CONF_ROOT = "src/test/resources/org/apache/activemq/config/sample-conf/";

    private static final Logger LOG = LoggerFactory.getLogger(ConfigTest.class);

    static {
        System.setProperty("javax.net.ssl.trustStore", "src/test/resources/client.keystore");
        System.setProperty("javax.net.ssl.trustStorePassword", "password");
        System.setProperty("javax.net.ssl.trustStoreType", "jks");
        System.setProperty("javax.net.ssl.keyStore", "src/test/resources/server.keystore");
        System.setProperty("javax.net.ssl.keyStorePassword", "password");
        System.setProperty("javax.net.ssl.keyStoreType", "jks");
    }

    /* IMPORTANT NOTE: Assertions checking for the existence of the derby
    directory will fail if the first derby directory is not created under
    target/test-data/. The test in unable to change the derby root directory
    for succeeding creation. It uses the first created directory as the root.
     */
    /* This tests creating a journal persistence adapter using the persistence
    adapter factory bean
     */
    @Test
    public void testJournaledJDBCConfig() throws Exception {
        File journalFile = new File(((ConfigTest.JOURNAL_ROOT) + "testJournaledJDBCConfig/journal"));
        ConfigTest.recursiveDelete(journalFile);
        File derbyFile = new File(((ConfigTest.DERBY_ROOT) + "testJournaledJDBCConfig/derbydb"));// Default

        ConfigTest.recursiveDelete(derbyFile);
        BrokerService broker;
        broker = createBroker(new FileSystemResource(((ConfigTest.CONF_ROOT) + "journaledjdbc-example.xml")));
        try {
            Assert.assertEquals("Broker Config Error (brokerName)", "brokerJournaledJDBCConfigTest", broker.getBrokerName());
            PersistenceAdapter adapter = broker.getPersistenceAdapter();
            Assert.assertTrue("Should have created a journal persistence adapter", (adapter instanceof JournalPersistenceAdapter));
            Assert.assertTrue(("Should have created a derby directory at " + (derbyFile.getAbsolutePath())), derbyFile.exists());
            Assert.assertTrue(("Should have created a journal directory at " + (journalFile.getAbsolutePath())), journalFile.exists());
            // Check persistence factory configurations
            broker.getPersistenceAdapter();
            Assert.assertTrue(((broker.getSystemUsage().getStoreUsage().getStore()) instanceof JournalPersistenceAdapter));
            ConfigTest.LOG.info("Success");
        } finally {
            if (broker != null) {
                broker.stop();
            }
        }
    }

    @Test
    public void testJdbcLockConfigOverride() throws Exception {
        JDBCPersistenceAdapter adapter = new JDBCPersistenceAdapter();
        Mockery context = new Mockery();
        final DataSource dataSource = context.mock(DataSource.class);
        final Connection connection = context.mock(Connection.class);
        final DatabaseMetaData metadata = context.mock(DatabaseMetaData.class);
        final ResultSet result = context.mock(ResultSet.class);
        adapter.setDataSource(dataSource);
        adapter.setCreateTablesOnStartup(false);
        context.checking(new Expectations() {
            {
                allowing(dataSource).getConnection();
                will(returnValue(connection));
                allowing(connection).getMetaData();
                will(returnValue(metadata));
                allowing(connection);
                allowing(metadata).getDriverName();
                will(returnValue("Microsoft_SQL_Server_2005_jdbc_driver"));
                allowing(result).next();
                will(returnValue(true));
            }
        });
        adapter.start();
        Assert.assertTrue("has the locker override", ((adapter.getLocker()) instanceof TransactDatabaseLocker));
        adapter.stop();
    }

    /* This tests configuring the different broker properties using
    xbeans-spring
     */
    @Test
    public void testBrokerConfig() throws Exception {
        ActiveMQTopic dest;
        BrokerService broker;
        File journalFile = new File(ConfigTest.JOURNAL_ROOT);
        ConfigTest.recursiveDelete(journalFile);
        // Create broker from resource
        // System.out.print("Creating broker... ");
        broker = createBroker("org/apache/activemq/config/example.xml");
        ConfigTest.LOG.info("Success");
        try {
            // Check broker configuration
            // System.out.print("Checking broker configurations... ");
            Assert.assertEquals("Broker Config Error (brokerName)", "brokerConfigTest", broker.getBrokerName());
            Assert.assertEquals("Broker Config Error (populateJMSXUserID)", false, broker.isPopulateJMSXUserID());
            Assert.assertEquals("Broker Config Error (useLoggingForShutdownErrors)", true, broker.isUseLoggingForShutdownErrors());
            Assert.assertEquals("Broker Config Error (useJmx)", true, broker.isUseJmx());
            Assert.assertEquals("Broker Config Error (persistent)", false, broker.isPersistent());
            Assert.assertEquals("Broker Config Error (useShutdownHook)", false, broker.isUseShutdownHook());
            Assert.assertEquals("Broker Config Error (deleteAllMessagesOnStartup)", true, broker.isDeleteAllMessagesOnStartup());
            ConfigTest.LOG.info("Success");
            // Check specific vm transport
            // System.out.print("Checking vm connector... ");
            Assert.assertEquals("Should have a specific VM Connector", "vm://javacoola", broker.getVmConnectorURI().toString());
            ConfigTest.LOG.info("Success");
            // Check transport connectors list
            // System.out.print("Checking transport connectors... ");
            List<TransportConnector> connectors = broker.getTransportConnectors();
            Assert.assertTrue("Should have created at least 3 connectors", ((connectors.size()) >= 3));
            Assert.assertTrue("1st connector should be TcpTransportServer", ((connectors.get(0).getServer()) instanceof TcpTransportServer));
            Assert.assertTrue("2nd connector should be TcpTransportServer", ((connectors.get(1).getServer()) instanceof TcpTransportServer));
            Assert.assertTrue("3rd connector should be TcpTransportServer", ((connectors.get(2).getServer()) instanceof TcpTransportServer));
            // Check network connectors
            // System.out.print("Checking network connectors... ");
            List<NetworkConnector> networkConnectors = broker.getNetworkConnectors();
            Assert.assertEquals("Should have a single network connector", 1, networkConnectors.size());
            ConfigTest.LOG.info("Success");
            // Check dispatch policy configuration
            // System.out.print("Checking dispatch policies... ");
            dest = new ActiveMQTopic("Topic.SimpleDispatch");
            Assert.assertTrue(("Should have a simple dispatch policy for " + (dest.getTopicName())), ((broker.getDestinationPolicy().getEntryFor(dest).getDispatchPolicy()) instanceof SimpleDispatchPolicy));
            dest = new ActiveMQTopic("Topic.RoundRobinDispatch");
            Assert.assertTrue(("Should have a round robin dispatch policy for " + (dest.getTopicName())), ((broker.getDestinationPolicy().getEntryFor(dest).getDispatchPolicy()) instanceof RoundRobinDispatchPolicy));
            dest = new ActiveMQTopic("Topic.StrictOrderDispatch");
            Assert.assertTrue(("Should have a strict order dispatch policy for " + (dest.getTopicName())), ((broker.getDestinationPolicy().getEntryFor(dest).getDispatchPolicy()) instanceof StrictOrderDispatchPolicy));
            ConfigTest.LOG.info("Success");
            // Check subscription policy configuration
            // System.out.print("Checking subscription recovery policies... ");
            SubscriptionRecoveryPolicy subsPolicy;
            dest = new ActiveMQTopic("Topic.FixedSizedSubs");
            subsPolicy = broker.getDestinationPolicy().getEntryFor(dest).getSubscriptionRecoveryPolicy();
            Assert.assertTrue(("Should have a fixed sized subscription recovery policy for " + (dest.getTopicName())), (subsPolicy instanceof FixedSizedSubscriptionRecoveryPolicy));
            Assert.assertEquals("FixedSizedSubsPolicy Config Error (maximumSize)", 2000000, getMaximumSize());
            Assert.assertEquals("FixedSizedSubsPolicy Config Error (useSharedBuffer)", false, isUseSharedBuffer());
            dest = new ActiveMQTopic("Topic.LastImageSubs");
            subsPolicy = broker.getDestinationPolicy().getEntryFor(dest).getSubscriptionRecoveryPolicy();
            Assert.assertTrue(("Should have a last image subscription recovery policy for " + (dest.getTopicName())), (subsPolicy instanceof LastImageSubscriptionRecoveryPolicy));
            dest = new ActiveMQTopic("Topic.NoSubs");
            subsPolicy = broker.getDestinationPolicy().getEntryFor(dest).getSubscriptionRecoveryPolicy();
            Assert.assertTrue(("Should have no subscription recovery policy for " + (dest.getTopicName())), (subsPolicy instanceof NoSubscriptionRecoveryPolicy));
            dest = new ActiveMQTopic("Topic.TimedSubs");
            subsPolicy = broker.getDestinationPolicy().getEntryFor(dest).getSubscriptionRecoveryPolicy();
            Assert.assertTrue(("Should have a timed subscription recovery policy for " + (dest.getTopicName())), (subsPolicy instanceof TimedSubscriptionRecoveryPolicy));
            Assert.assertEquals("TimedSubsPolicy Config Error (recoverDuration)", 25000, getRecoverDuration());
            ConfigTest.LOG.info("Success");
            // Check usage manager
            // System.out.print("Checking memory manager configurations... ");
            SystemUsage systemUsage = broker.getSystemUsage();
            Assert.assertTrue("Should have a SystemUsage", (systemUsage != null));
            Assert.assertEquals("SystemUsage Config Error (MemoryUsage.limit)", ((1024 * 1024) * 10), systemUsage.getMemoryUsage().getLimit());
            Assert.assertEquals("SystemUsage Config Error (MemoryUsage.percentUsageMinDelta)", 20, systemUsage.getMemoryUsage().getPercentUsageMinDelta());
            Assert.assertEquals("SystemUsage Config Error (TempUsage.limit)", ((1024 * 1024) * 100), systemUsage.getTempUsage().getLimit());
            Assert.assertEquals("SystemUsage Config Error (StoreUsage.limit)", ((1024 * 1024) * 1024), systemUsage.getStoreUsage().getLimit());
            Assert.assertEquals("SystemUsage Config Error (StoreUsage.name)", "foo", systemUsage.getStoreUsage().getName());
            Assert.assertNotNull(systemUsage.getStoreUsage().getStore());
            Assert.assertTrue(((systemUsage.getStoreUsage().getStore()) instanceof MemoryPersistenceAdapter));
            ConfigTest.LOG.info("Success");
        } finally {
            if (broker != null) {
                broker.stop();
            }
        }
    }

    /* This tests creating a journal persistence adapter using xbeans-spring */
    @Test
    public void testJournalConfig() throws Exception {
        File journalFile = new File(((ConfigTest.JOURNAL_ROOT) + "testJournalConfig/journal"));
        ConfigTest.recursiveDelete(journalFile);
        BrokerService broker;
        broker = createBroker(new FileSystemResource(((ConfigTest.CONF_ROOT) + "journal-example.xml")));
        try {
            Assert.assertEquals("Broker Config Error (brokerName)", "brokerJournalConfigTest", broker.getBrokerName());
            PersistenceAdapter adapter = broker.getPersistenceAdapter();
            Assert.assertTrue("Should have created a journal persistence adapter", (adapter instanceof JournalPersistenceAdapter));
            Assert.assertTrue(("Should have created a journal directory at " + (journalFile.getAbsolutePath())), journalFile.exists());
            ConfigTest.LOG.info("Success");
        } finally {
            if (broker != null) {
                broker.stop();
            }
        }
    }

    /* This tests creating a memory persistence adapter using xbeans-spring */
    @Test
    public void testMemoryConfig() throws Exception {
        File journalFile = new File(((ConfigTest.JOURNAL_ROOT) + "testMemoryConfig"));
        ConfigTest.recursiveDelete(journalFile);
        File derbyFile = new File(((ConfigTest.DERBY_ROOT) + "testMemoryConfig"));
        ConfigTest.recursiveDelete(derbyFile);
        BrokerService broker;
        broker = createBroker(new FileSystemResource(((ConfigTest.CONF_ROOT) + "memory-example.xml")));
        try {
            Assert.assertEquals("Broker Config Error (brokerName)", "brokerMemoryConfigTest", broker.getBrokerName());
            PersistenceAdapter adapter = broker.getPersistenceAdapter();
            Assert.assertTrue("Should have created a memory persistence adapter", (adapter instanceof MemoryPersistenceAdapter));
            Assert.assertTrue(("Should have not created a derby directory at " + (derbyFile.getAbsolutePath())), (!(derbyFile.exists())));
            Assert.assertTrue(("Should have not created a journal directory at " + (journalFile.getAbsolutePath())), (!(journalFile.exists())));
            ConfigTest.LOG.info("Success");
        } finally {
            if (broker != null) {
                broker.stop();
            }
        }
    }

    @Test
    public void testConnectorConfig() throws Exception {
        File journalFile = new File(((ConfigTest.JOURNAL_ROOT) + "testMemoryConfig"));
        ConfigTest.recursiveDelete(journalFile);
        File derbyFile = new File(((ConfigTest.DERBY_ROOT) + "testMemoryConfig"));
        ConfigTest.recursiveDelete(derbyFile);
        final int MAX_PRODUCERS = 5;
        final int MAX_CONSUMERS = 10;
        BrokerService broker = createBroker(new FileSystemResource(((ConfigTest.CONF_ROOT) + "connector-properties.xml")));
        broker.start();
        try {
            Assert.assertEquals(broker.getTransportConnectorByScheme("tcp").getMaximumProducersAllowedPerConnection(), MAX_PRODUCERS);
            Assert.assertEquals(broker.getTransportConnectorByScheme("tcp").getMaximumConsumersAllowedPerConnection(), MAX_CONSUMERS);
            ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61631");
            javax.jms.Connection connection = activeMQConnectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic("test.foo");
            for (int i = 0; i < MAX_PRODUCERS; i++) {
                session.createProducer(topic);
            }
            try {
                session.createProducer(topic);
                Assert.fail("Should have got an exception on exceeding MAX_PRODUCERS");
            } catch (JMSException expected) {
            }
            // Tests the anonymous producer case also counts.
            try {
                session.createProducer(null);
                Assert.fail("Should have got an exception on exceeding MAX_PRODUCERS");
            } catch (JMSException expected) {
            }
            try {
                for (int i = 0; i < (MAX_CONSUMERS + 1); i++) {
                    MessageConsumer consumer = session.createConsumer(topic);
                    Assert.assertNotNull(consumer);
                }
                Assert.fail("Should have caught an exception");
            } catch (JMSException e) {
            }
            ConfigTest.LOG.info("Success");
        } finally {
            if (broker != null) {
                broker.stop();
            }
        }
    }

    @Test
    public void testXmlConfigHelper() throws Exception {
        BrokerService broker;
        broker = createBroker(new FileSystemResource(((ConfigTest.CONF_ROOT) + "memory-example.xml")));
        try {
            Assert.assertEquals("Broker Config Error (brokerName)", "brokerMemoryConfigTest", broker.getBrokerName());
        } finally {
            if (broker != null) {
                broker.stop();
            }
        }
        broker = createBroker("org/apache/activemq/config/config.xml");
        try {
            Assert.assertEquals("Broker Config Error (brokerName)", "brokerXmlConfigHelper", broker.getBrokerName());
        } finally {
            if (broker != null) {
                broker.stop();
            }
        }
    }
}

