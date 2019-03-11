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
package org.apache.activemq.maven;


import StartBrokerMojo.DEFAULT_CONNECTOR_PROPERTY_NAME_FORMAT;
import java.util.List;
import java.util.Properties;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class StartBrokerMojoTest {
    /**
     * Main object-under-test; configured with mocks and defaults.
     */
    private StartBrokerMojo startBrokerMojo;

    /**
     * Secondary object-under-test primarily for setter/getter testing; available for all tests without any
     * configuration (i.e. raw).
     */
    private StartBrokerMojo startBrokerMojoRaw;

    // //
    // MOCKS
    // //
    private MavenProject mockMavenProject;

    private Build mockBuild;

    private Properties mockMavenProperties;

    private MavenBrokerManager mockBrokerManager;

    private XBeanFileResolver mockXbeanFileResolver;

    private BrokerService mockBrokerService;

    private Log mockMavenLog;

    // //
    // Test Objects
    // //
    private Properties systemProperties;

    private List<TransportConnector> transportConnectorList;

    private Exception testException;

    /**
     * Test the setter and getter for propertyNameFormat.
     */
    @Test
    public void testSetGetConnectorPropertyNameFormat() {
        Assert.assertEquals(DEFAULT_CONNECTOR_PROPERTY_NAME_FORMAT, this.startBrokerMojoRaw.getConnectorPropertyNameFormat());
        this.startBrokerMojoRaw.setConnectorPropertyNameFormat("x-name-format-x");
        Assert.assertEquals("x-name-format-x", this.startBrokerMojoRaw.getConnectorPropertyNameFormat());
    }

    /**
     * Test the setter and getter for configUri.
     */
    @Test
    public void testSetGetConfigUri() {
        Assert.assertNull(this.startBrokerMojoRaw.getConfigUri());
        this.startBrokerMojoRaw.setConfigUri("x-config-uri-x");
        Assert.assertEquals("x-config-uri-x", this.startBrokerMojoRaw.getConfigUri());
    }

    /**
     * Test the setter and getter for mavenProject.
     */
    @Test
    public void testSetGetMavenProject() {
        Assert.assertNull(this.startBrokerMojoRaw.getProject());
        this.startBrokerMojoRaw.setProject(this.mockMavenProject);
        Assert.assertSame(this.mockMavenProject, this.startBrokerMojoRaw.getProject());
    }

    /**
     * Test the setter and getter for fork.
     */
    @Test
    public void testSetGetFork() {
        Assert.assertFalse(this.startBrokerMojoRaw.isFork());
        this.startBrokerMojoRaw.setFork(true);
        Assert.assertTrue(this.startBrokerMojoRaw.isFork());
    }

    /**
     * Test the setter and getter for skip.
     */
    @Test
    public void testSetGetSkip() {
        Assert.assertFalse(this.startBrokerMojoRaw.isSkip());
        this.startBrokerMojoRaw.setSkip(true);
        Assert.assertTrue(this.startBrokerMojoRaw.isSkip());
    }

    /**
     * Test the setter and getter for systemProperties.
     */
    @Test
    public void testSetGetSystemProperties() {
        Assert.assertNull(this.startBrokerMojoRaw.getSystemProperties());
        this.startBrokerMojoRaw.setSystemProperties(this.systemProperties);
        Assert.assertSame(this.systemProperties, this.startBrokerMojoRaw.getSystemProperties());
    }

    /**
     * Test the setter and getter for brokerManager.
     */
    @Test
    public void testSetGetBrokerManager() {
        Assert.assertNull(this.startBrokerMojoRaw.getBrokerManager());
        this.startBrokerMojoRaw.setBrokerManager(this.mockBrokerManager);
        Assert.assertSame(this.mockBrokerManager, this.startBrokerMojoRaw.getBrokerManager());
    }

    /**
     * Test the setter and getter for xbeanFileResolver.
     */
    @Test
    public void testSetGetXbeanFileResolver() {
        Assert.assertTrue(((this.startBrokerMojoRaw.getxBeanFileResolver()) instanceof XBeanFileResolver));
        Assert.assertNotSame(this.mockXbeanFileResolver, this.startBrokerMojoRaw.getxBeanFileResolver());
        this.startBrokerMojoRaw.setxBeanFileResolver(this.mockXbeanFileResolver);
        Assert.assertSame(this.mockXbeanFileResolver, this.startBrokerMojoRaw.getxBeanFileResolver());
    }

    /**
     * Test normal execution of the mojo leads to startup of the broker.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testExecute() throws Exception {
        this.startBrokerMojo.execute();
        Mockito.verify(this.mockBrokerManager).start(false, "x-config-uri-x");
    }

    /**
     * Test the registration of a single transport connector URI when a broker with only one connector is started.
     */
    @Test
    public void testExecuteRegistersTransportConnectorOneUri() throws Exception {
        this.startBrokerMojo.setProject(this.mockMavenProject);
        this.createTestTransportConnectors("openwire-client");
        this.startBrokerMojo.execute();
        Mockito.verify(this.mockMavenProperties).setProperty("x-name-openwire-client", "x-pub-addr-for-openwire-client");
    }

    /**
     * Test the registration of multiple transport connector URIs when a broker with multiple connectors is started.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testExecuteRegistersTransportConnectorMultiUri() throws Exception {
        this.createTestTransportConnectors("connector1", "connector2", "connector3");
        this.startBrokerMojo.execute();
        Mockito.verify(this.mockMavenProperties).setProperty("x-name-connector1", "x-pub-addr-for-connector1");
        Mockito.verify(this.mockMavenProperties).setProperty("x-name-connector2", "x-pub-addr-for-connector2");
        Mockito.verify(this.mockMavenProperties).setProperty("x-name-connector3", "x-pub-addr-for-connector3");
    }

    /**
     * Test handling when TransportConnector.getPublishableConnectString() throws an exception.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testExceptionOnGetPublishableConnectString() throws Exception {
        TransportConnector mockTransportConnector = Mockito.mock(TransportConnector.class);
        Mockito.when(mockTransportConnector.toString()).thenReturn("x-conn-x");
        Mockito.when(mockTransportConnector.getPublishableConnectString()).thenThrow(testException);
        this.transportConnectorList.add(mockTransportConnector);
        this.startBrokerMojo.execute();
        Mockito.verify(this.mockMavenLog).warn("error on obtaining broker connector uri; connector=x-conn-x", this.testException);
    }

    /**
     * Test that an xbean configuration file URI is transformed on use.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testUseXbeanConfigFile() throws Exception {
        Mockito.when(this.mockXbeanFileResolver.isXBeanFile("x-config-uri-x")).thenReturn(true);
        Mockito.when(this.mockXbeanFileResolver.toUrlCompliantAbsolutePath("x-config-uri-x")).thenReturn("x-transformed-uri-x");
        this.startBrokerMojo.execute();
        Mockito.verify(this.mockMavenLog).debug("configUri before transformation: x-config-uri-x");
        Mockito.verify(this.mockMavenLog).debug("configUri after transformation: x-transformed-uri-x");
    }

    /**
     * Test that a URI that does not represent an xbean configuration file is not translated.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDoNotUseXbeanConfigFile() throws Exception {
        Mockito.when(this.mockXbeanFileResolver.isXBeanFile("x-config-uri-x")).thenReturn(false);
        this.startBrokerMojo.execute();
        Mockito.verify(this.mockMavenLog, Mockito.times(0)).debug("configUri before transformation: x-config-uri-x");
        Mockito.verify(this.mockMavenLog, Mockito.times(0)).debug("configUri after transformation: x-transformed-uri-x");
    }

    /**
     * Test that execution of the mojo is skipped if so configured.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSkip() throws Exception {
        this.startBrokerMojo.setSkip(true);
        this.startBrokerMojo.execute();
        Mockito.verify(this.mockMavenLog).info("Skipped execution of ActiveMQ Broker");
        Mockito.verifyNoMoreInteractions(this.mockBrokerManager);
    }
}

