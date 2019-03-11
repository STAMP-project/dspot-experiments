/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.controller;


import AmbariServer.VELOCITY_LOG_CATEGORY;
import DispatcherType.REQUEST;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.EnumSet;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.SessionCookieConfig;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.checks.DatabaseConsistencyCheckHelper;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.orm.DBAccessor;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.stack.OsFamily;
import org.apache.velocity.app.Velocity;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlets.GzipFilter;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;


public class AmbariServerTest {
    private Injector injector;

    @Test
    public void testVelocityLogger() throws Exception {
        new AmbariServer();
        Assert.assertEquals(VELOCITY_LOG_CATEGORY, Velocity.getProperty("runtime.log.logsystem.log4j.logger"));
    }

    @Test
    public void testConfigureSessionManager() throws Exception {
        SessionHandlerConfigurer sessionHandlerConfigurer = new SessionHandlerConfigurer();
        Configuration configuration = createNiceMock(Configuration.class);
        SessionHandler sessionHandler = createNiceMock(SessionHandler.class);
        SessionCookieConfig sessionCookieConfig = createNiceMock(SessionCookieConfig.class);
        sessionHandlerConfigurer.configuration = configuration;
        expect(sessionHandler.getSessionCookieConfig()).andReturn(sessionCookieConfig).anyTimes();
        expect(configuration.getApiSSLAuthentication()).andReturn(false);
        sessionCookieConfig.setHttpOnly(true);
        expect(configuration.getApiSSLAuthentication()).andReturn(true);
        sessionCookieConfig.setHttpOnly(true);
        sessionCookieConfig.setSecure(true);
        replay(configuration, sessionHandler, sessionCookieConfig);
        // getApiSSLAuthentication == false
        sessionHandlerConfigurer.configureSessionHandler(sessionHandler);
        // getApiSSLAuthentication == true
        sessionHandlerConfigurer.configureSessionHandler(sessionHandler);
        verify(configuration, sessionHandler, sessionCookieConfig);
    }

    @Test
    public void testSystemProperties() throws Exception {
        Configuration configuration = EasyMock.createNiceMock(Configuration.class);
        expect(configuration.getServerTempDir()).andReturn("/ambari/server/temp/dir").anyTimes();
        replay(configuration);
        AmbariServer.setSystemProperties(configuration);
        Assert.assertEquals(System.getProperty("java.io.tmpdir"), "/ambari/server/temp/dir");
    }

    @Test
    public void testProxyUser() throws Exception {
        PasswordAuthentication pa = Authenticator.requestPasswordAuthentication(InetAddress.getLocalHost(), 80, null, null, null);
        Assert.assertNull(pa);
        System.setProperty("http.proxyUser", "abc");
        System.setProperty("http.proxyPassword", "def");
        AmbariServer.setupProxyAuth();
        pa = Authenticator.requestPasswordAuthentication(InetAddress.getLocalHost(), 80, null, null, null);
        Assert.assertNotNull(pa);
        Assert.assertEquals("abc", pa.getUserName());
        Assert.assertArrayEquals("def".toCharArray(), pa.getPassword());
    }

    @Test
    public void testConfigureRootHandler() throws Exception {
        final ServletContextHandler handler = EasyMock.createNiceMock(ServletContextHandler.class);
        final FilterHolder filter = EasyMock.createNiceMock(FilterHolder.class);
        handler.setMaxFormContentSize((-1));
        EasyMock.expectLastCall().once();
        EasyMock.expect(handler.addFilter(GzipFilter.class, "/*", EnumSet.of(REQUEST))).andReturn(filter).once();
        EasyMock.expect(handler.getMimeTypes()).andReturn(new MimeTypes()).anyTimes();
        replay(handler, filter);
        injector.getInstance(AmbariServer.class).configureRootHandler(handler);
        EasyMock.verify(handler);
    }

    @Test
    public void testConfigureCompression() throws Exception {
        final ServletContextHandler handler = EasyMock.createNiceMock(ServletContextHandler.class);
        final FilterHolder filter = EasyMock.createNiceMock(FilterHolder.class);
        EasyMock.expect(handler.addFilter(GzipFilter.class, "/*", EnumSet.of(REQUEST))).andReturn(filter).once();
        filter.setInitParameter(anyObject(String.class), anyObject(String.class));
        EasyMock.expectLastCall().times(3);
        replay(handler, filter);
        injector.getInstance(AmbariServer.class).configureHandlerCompression(handler);
        EasyMock.verify(handler);
    }

    @Test
    public void testConfigureContentTypes() throws Exception {
        ServletContextHandler handler = EasyMock.createNiceMock(ServletContextHandler.class);
        FilterHolder filter = EasyMock.createNiceMock(FilterHolder.class);
        MimeTypes expectedMimeTypes = new MimeTypes();
        EasyMock.expect(handler.getMimeTypes()).andReturn(expectedMimeTypes).anyTimes();
        EasyMock.expect(handler.addFilter(isA(Class.class), anyString(), isA(EnumSet.class))).andReturn(filter).anyTimes();
        replay(handler, filter);
        injector.getInstance(AmbariServer.class).configureRootHandler(handler);
        assertEquals("application/font-woff", expectedMimeTypes.getMimeByExtension("/file.woff").toString());
        assertEquals("application/font-sfnt", expectedMimeTypes.getMimeByExtension("/file.ttf").toString());
        EasyMock.verify(handler);
    }

    /**
     * Tests that Jetty pools are configured with the correct number of
     * Acceptor/Selector threads.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testJettyThreadPoolCalculation() throws Exception {
        Server server = new Server();
        AmbariServer ambariServer = new AmbariServer();
        // 12 acceptors (48 core machine) with a configured pool size of 25
        server = ambariServer.configureJettyThreadPool(12, "mock-pool", 25);
        Assert.assertEquals(44, getMaxThreads());
        // 2 acceptors (8 core machine) with a configured pool size of 25
        server = ambariServer.configureJettyThreadPool(2, "mock-pool", 25);
        Assert.assertEquals(25, getMaxThreads());
        // 16 acceptors (64 core machine) with a configured pool size of 35
        server = ambariServer.configureJettyThreadPool(16, "mock-pool", 35);
        Assert.assertEquals(52, getMaxThreads());
    }

    @Test
    public void testRunDatabaseConsistencyCheck() throws Exception {
        EasyMockSupport easyMockSupport = new EasyMockSupport();
        final AmbariMetaInfo mockAmbariMetainfo = easyMockSupport.createNiceMock(AmbariMetaInfo.class);
        final DBAccessor mockDBDbAccessor = easyMockSupport.createNiceMock(DBAccessor.class);
        final Connection mockConnection = easyMockSupport.createNiceMock(Connection.class);
        final Statement mockStatement = easyMockSupport.createNiceMock(Statement.class);
        final OsFamily mockOSFamily = easyMockSupport.createNiceMock(OsFamily.class);
        final EntityManager mockEntityManager = easyMockSupport.createNiceMock(EntityManager.class);
        final Clusters mockClusters = easyMockSupport.createNiceMock(Clusters.class);
        AmbariServer ambariServer = new AmbariServer();
        final Configuration mockConfiguration = partialMockBuilder(Configuration.class).withConstructor().addMockedMethod("getDatabaseType").createMock();
        final TypedQuery mockQuery = easyMockSupport.createNiceMock(TypedQuery.class);
        expect(mockConfiguration.getDatabaseType()).andReturn(null).anyTimes();
        expect(mockEntityManager.createNamedQuery(anyString(), anyObject(Class.class))).andReturn(mockQuery);
        expect(mockQuery.getResultList()).andReturn(new ArrayList());
        replay(mockConfiguration);
        final Injector mockInjector = createMockInjector(mockAmbariMetainfo, mockDBDbAccessor, mockOSFamily, mockEntityManager, mockClusters, mockConfiguration);
        expect(mockDBDbAccessor.getConnection()).andReturn(mockConnection).atLeastOnce();
        expect(mockConnection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)).andReturn(mockStatement).atLeastOnce();
        expect(mockStatement.executeQuery(anyString())).andReturn(null).atLeastOnce();
        DatabaseConsistencyCheckHelper.setInjector(mockInjector);
        easyMockSupport.replayAll();
        mockAmbariMetainfo.init();
        ambariServer.runDatabaseConsistencyCheck();
        easyMockSupport.verifyAll();
    }

    @Test
    public void testRunDatabaseConsistencyCheck_IgnoreDBCheck() throws Exception {
        AmbariServer ambariServer = new AmbariServer();
        System.setProperty("skipDatabaseConsistencyCheck", "");
        final Injector mockInjector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
            }
        });
        DatabaseConsistencyCheckHelper.setInjector(mockInjector);
        ambariServer.runDatabaseConsistencyCheck();
        System.clearProperty("skipDatabaseConsistencyCheck");
    }

    @Test
    public void testRunDatabaseConsistencyCheck_ThrowException() throws Exception {
        EasyMockSupport easyMockSupport = new EasyMockSupport();
        final AmbariMetaInfo mockAmbariMetainfo = easyMockSupport.createNiceMock(AmbariMetaInfo.class);
        final DBAccessor mockDBDbAccessor = easyMockSupport.createNiceMock(DBAccessor.class);
        final OsFamily mockOSFamily = easyMockSupport.createNiceMock(OsFamily.class);
        final EntityManager mockEntityManager = easyMockSupport.createNiceMock(EntityManager.class);
        final Clusters mockClusters = easyMockSupport.createNiceMock(Clusters.class);
        AmbariServer ambariServer = new AmbariServer();
        final Configuration mockConfiguration = partialMockBuilder(Configuration.class).withConstructor().addMockedMethod("getDatabaseType").createMock();
        final TypedQuery mockQuery = easyMockSupport.createNiceMock(TypedQuery.class);
        expect(mockConfiguration.getDatabaseType()).andReturn(null).anyTimes();
        expect(mockEntityManager.createNamedQuery(anyString(), anyObject(Class.class))).andReturn(mockQuery);
        expect(mockQuery.getResultList()).andReturn(new ArrayList());
        replay(mockConfiguration);
        final Injector mockInjector = createMockInjector(mockAmbariMetainfo, mockDBDbAccessor, mockOSFamily, mockEntityManager, mockClusters, mockConfiguration);
        expect(mockDBDbAccessor.getConnection()).andReturn(null);
        DatabaseConsistencyCheckHelper.setInjector(mockInjector);
        easyMockSupport.replayAll();
        mockAmbariMetainfo.init();
        boolean errorOccurred = false;
        try {
            ambariServer.runDatabaseConsistencyCheck();
        } catch (Exception e) {
            errorOccurred = true;
        }
        assertTrue(errorOccurred);
        easyMockSupport.verifyAll();
    }
}

