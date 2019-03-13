/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.www;


import Const.KETTLE_CARTE_JETTY_ACCEPTORS;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


/**
 *
 *
 * @author Tatsiana_Kasiankova
 */
public class WebServerTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    /**
     *
     */
    private static final String EMPTY_STRING = "";

    private static final boolean SHOULD_JOIN = false;

    private static final String HOST_NAME = "localhost";

    private static final int PORT = 8099;

    private static final String ACCEPTORS = "5";

    private static final int EXPECTED_ACCEPTORS = 5;

    private static final String ACCEPT_QUEUE_SIZE = "5000";

    private static final int EXPECTED_ACCEPT_QUEUE_SIZE = 5000;

    private static final String RES_MAX_IDLE_TIME = "200";

    private static final int EXPECTED_RES_MAX_IDLE_TIME = 200;

    private static final int EXPECTED_CONNECTORS_SIZE = 1;

    private WebServer webServer;

    private WebServer webServerNg;

    private TransformationMap trMapMock = Mockito.mock(TransformationMap.class);

    private SlaveServerConfig sServerConfMock = Mockito.mock(SlaveServerConfig.class);

    private SlaveServer sServer = Mockito.mock(SlaveServer.class);

    private JobMap jbMapMock = Mockito.mock(JobMap.class);

    private SocketRepository sRepoMock = Mockito.mock(SocketRepository.class);

    private List<SlaveServerDetection> detections = new ArrayList<SlaveServerDetection>();

    private LogChannelInterface logMock = Mockito.mock(LogChannelInterface.class);

    private static final SocketConnector defSocketConnector = new SocketConnector();

    @Test
    public void testJettyOption_AcceptorsSetUp() throws Exception {
        Assert.assertEquals(getSocketConnectors(webServer).size(), WebServerTest.EXPECTED_CONNECTORS_SIZE);
        for (SocketConnector sc : getSocketConnectors(webServer)) {
            Assert.assertEquals(WebServerTest.EXPECTED_ACCEPTORS, sc.getAcceptors());
        }
    }

    @Test
    public void testJettyOption_AcceptQueueSizeSetUp() throws Exception {
        Assert.assertEquals(getSocketConnectors(webServer).size(), WebServerTest.EXPECTED_CONNECTORS_SIZE);
        for (SocketConnector sc : getSocketConnectors(webServer)) {
            Assert.assertEquals(WebServerTest.EXPECTED_ACCEPT_QUEUE_SIZE, sc.getAcceptQueueSize());
        }
    }

    @Test
    public void testJettyOption_LowResourceMaxIdleTimeSetUp() throws Exception {
        Assert.assertEquals(getSocketConnectors(webServer).size(), WebServerTest.EXPECTED_CONNECTORS_SIZE);
        for (SocketConnector sc : getSocketConnectors(webServer)) {
            Assert.assertEquals(WebServerTest.EXPECTED_RES_MAX_IDLE_TIME, sc.getLowResourceMaxIdleTime());
        }
    }

    @Test
    public void testNoExceptionAndUsingDefaultServerValue_WhenJettyOptionSetAsInvalidValue() throws Exception {
        System.setProperty(KETTLE_CARTE_JETTY_ACCEPTORS, "TEST");
        try {
            webServerNg = new WebServer(logMock, trMapMock, jbMapMock, sRepoMock, detections, WebServerTest.HOST_NAME, ((WebServerTest.PORT) + 1), WebServerTest.SHOULD_JOIN, null);
        } catch (NumberFormatException nmbfExc) {
            Assert.fail(("Should not have thrown any NumberFormatException but it does: " + nmbfExc));
        }
        Assert.assertEquals(getSocketConnectors(webServerNg).size(), WebServerTest.EXPECTED_CONNECTORS_SIZE);
        for (SocketConnector sc : getSocketConnectors(webServerNg)) {
            Assert.assertEquals(WebServerTest.defSocketConnector.getAcceptors(), sc.getAcceptors());
        }
        webServerNg.setWebServerShutdownHandler(null);// disable system.exit

        webServerNg.stopServer();
    }

    @Test
    public void testNoExceptionAndUsingDefaultServerValue_WhenJettyOptionSetAsEmpty() throws Exception {
        System.setProperty(KETTLE_CARTE_JETTY_ACCEPTORS, WebServerTest.EMPTY_STRING);
        try {
            webServerNg = new WebServer(logMock, trMapMock, jbMapMock, sRepoMock, detections, WebServerTest.HOST_NAME, ((WebServerTest.PORT) + 1), WebServerTest.SHOULD_JOIN, null);
        } catch (NumberFormatException nmbfExc) {
            Assert.fail(("Should not have thrown any NumberFormatException but it does: " + nmbfExc));
        }
        Assert.assertEquals(getSocketConnectors(webServerNg).size(), WebServerTest.EXPECTED_CONNECTORS_SIZE);
        for (SocketConnector sc : getSocketConnectors(webServerNg)) {
            Assert.assertEquals(WebServerTest.defSocketConnector.getAcceptors(), sc.getAcceptors());
        }
        webServerNg.setWebServerShutdownHandler(null);// disable system.exit

        webServerNg.stopServer();
    }
}

