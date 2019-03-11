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
package org.apache.activemq.store.jdbc;


import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sql.DataSource;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.ft.SyncCreateDataSource;
import org.apache.activemq.bugs.embedded.ThreadExplorer;
import org.apache.activemq.util.DefaultTestAppender;
import org.apache.activemq.util.LeaseLockerIOExceptionHandler;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.log4j.Logger.getRootLogger;


/**
 * Test to see if the JDBCExceptionIOHandler will restart the transport connectors correctly after
 * the underlying DB has been stopped and restarted
 *
 * see AMQ-4575
 */
public class JDBCIOExceptionHandlerTest {
    private static final Logger LOG = LoggerFactory.getLogger(JDBCIOExceptionHandlerTest.class);

    private static final String TRANSPORT_URL = "tcp://0.0.0.0:0";

    private ActiveMQConnectionFactory factory;

    private JDBCIOExceptionHandlerTest.ReconnectingEmbeddedDataSource dataSource;

    private BrokerService broker;

    @Test
    public void testStartWithDatabaseDown() throws Exception {
        final AtomicBoolean connectorStarted = new AtomicBoolean(false);
        final AtomicBoolean connectorStopped = new AtomicBoolean(false);
        DefaultTestAppender appender = new DefaultTestAppender() {
            @Override
            public void doAppend(LoggingEvent event) {
                if (event.getMessage().toString().startsWith("JMX consoles can connect to")) {
                    connectorStarted.set(true);
                }
                if (event.getMessage().toString().equals("Stopping jmx connector")) {
                    connectorStopped.set(true);
                }
            }
        };
        org.apache.log4j.Logger rootLogger = getRootLogger();
        Level previousLevel = rootLogger.getLevel();
        rootLogger.setLevel(Level.DEBUG);
        rootLogger.addAppender(appender);
        BrokerService broker = new BrokerService();
        broker.getManagementContext().setCreateConnector(true);
        broker.getManagementContext().setCreateMBeanServer(true);
        JDBCPersistenceAdapter jdbc = new JDBCPersistenceAdapter();
        EmbeddedDataSource embeddedDataSource = ((EmbeddedDataSource) (jdbc.getDataSource()));
        // create a wrapper to EmbeddedDataSource to allow the connection be
        // reestablished to derby db
        dataSource = new JDBCIOExceptionHandlerTest.ReconnectingEmbeddedDataSource(new SyncCreateDataSource(embeddedDataSource));
        dataSource.stopDB();
        jdbc.setDataSource(dataSource);
        jdbc.setLockKeepAlivePeriod(1000L);
        LeaseDatabaseLocker leaseDatabaseLocker = new LeaseDatabaseLocker();
        leaseDatabaseLocker.setHandleStartException(true);
        leaseDatabaseLocker.setLockAcquireSleepInterval(2000L);
        jdbc.setLocker(leaseDatabaseLocker);
        broker.setPersistenceAdapter(jdbc);
        LeaseLockerIOExceptionHandler ioExceptionHandler = new LeaseLockerIOExceptionHandler();
        ioExceptionHandler.setResumeCheckSleepPeriod(1000L);
        ioExceptionHandler.setStopStartConnectors(true);
        broker.setIoExceptionHandler(ioExceptionHandler);
        try {
            broker.start();
            Assert.fail("Broker should have been stopped!");
        } catch (Exception e) {
            Thread.sleep(5000);
            Assert.assertTrue("Broker should have been stopped!", broker.isStopped());
            Thread[] threads = ThreadExplorer.listThreads();
            for (int i = 0; i < (threads.length); i++) {
                if (threads[i].getName().startsWith("IOExceptionHandler")) {
                    Assert.fail("IOExceptionHanlder still active");
                }
            }
            if ((connectorStarted.get()) && (!(connectorStopped.get()))) {
                Assert.fail("JMX Server Connector should have been stopped!");
            }
        } finally {
            dataSource = null;
            broker = null;
            rootLogger.removeAppender(appender);
            rootLogger.setLevel(previousLevel);
        }
    }

    /* run test without JMX enabled */
    @Test
    public void testRecoverWithOutJMX() throws Exception {
        recoverFromDisconnectDB(false);
    }

    /* run test with JMX enabled */
    @Test
    public void testRecoverWithJMX() throws Exception {
        recoverFromDisconnectDB(true);
    }

    @Test
    public void testSlaveStoppedLease() throws Exception {
        testSlaveStopped(true);
    }

    @Test
    public void testSlaveStoppedDefault() throws Exception {
        testSlaveStopped(false);
    }

    /* Wrapped the derby datasource object to get DB reconnect functionality as I not
    manage to get that working directly on the EmbeddedDataSource
     */
    public class ReconnectingEmbeddedDataSource implements DataSource {
        private SyncCreateDataSource realDatasource;

        public ReconnectingEmbeddedDataSource(SyncCreateDataSource datasource) {
            this.realDatasource = datasource;
        }

        @Override
        public PrintWriter getLogWriter() throws SQLException {
            return this.realDatasource.getLogWriter();
        }

        @Override
        public void setLogWriter(PrintWriter out) throws SQLException {
            this.realDatasource.setLogWriter(out);
        }

        @Override
        public void setLoginTimeout(int seconds) throws SQLException {
            this.realDatasource.setLoginTimeout(seconds);
        }

        @Override
        public int getLoginTimeout() throws SQLException {
            return this.realDatasource.getLoginTimeout();
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return this.unwrap(iface);
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return this.isWrapperFor(iface);
        }

        @Override
        public Connection getConnection() throws SQLException {
            return this.realDatasource.getConnection();
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            return this.getConnection(username, password);
        }

        /**
         * To simulate a db reconnect I just create a new EmbeddedDataSource .
         *
         * @throws SQLException
         * 		
         */
        public void restartDB() throws Exception {
            EmbeddedDataSource newDatasource = ((EmbeddedDataSource) (DataSourceServiceSupport.createDataSource(broker.getDataDirectoryFile().getCanonicalPath())));
            newDatasource.getConnection();
            JDBCIOExceptionHandlerTest.LOG.info("*** DB restarted now...");
            Object existingDataSource = realDatasource;
            synchronized(existingDataSource) {
                this.realDatasource = new SyncCreateDataSource(newDatasource);
            }
        }

        public void stopDB() {
            JDBCIOExceptionHandlerTest.LOG.info("***DB is being shutdown...");
            synchronized(realDatasource) {
                DataSourceServiceSupport.shutdownDefaultDataSource(realDatasource.getDelegate());
            }
        }

        public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return null;
        }
    }
}

