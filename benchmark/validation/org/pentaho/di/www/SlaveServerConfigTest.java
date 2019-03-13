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


import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleXMLException;
import org.w3c.dom.Node;


/**
 *
 *
 * @author Tatsiana_Kasiankova
 */
public class SlaveServerConfigTest {
    public static final String XML_TAG_SLAVE_CONFIG = "slave_config";

    public static final String XML_TAG_JETTY_OPTIONS = "jetty_options";

    public static final String XML_TAG_ACCEPTORS = "acceptors";

    public static final String XML_TAG_ACCEPT_QUEUE_SIZE = "acceptQueueSize";

    public static final String XML_TAG_LOW_RES_MAX_IDLE_TIME = "lowResourcesMaxIdleTime";

    public static final String ACCEPTORS_VALUE = "10";

    public static final String EXPECTED_ACCEPTORS_VALUE = "10";

    public static final String EXPECTED_ACCEPTORS_KEY = Const.KETTLE_CARTE_JETTY_ACCEPTORS;

    public static final String ACCEPT_QUEUE_SIZE_VALUE = "8000";

    public static final String EXPECTED_ACCEPT_QUEUE_SIZE_VALUE = "8000";

    public static final String EXPECTED_ACCEPT_QUEUE_SIZE_KEY = Const.KETTLE_CARTE_JETTY_ACCEPT_QUEUE_SIZE;

    public static final String LOW_RES_MAX_IDLE_TIME_VALUE = "300";

    public static final String EXPECTED_LOW_RES_MAX_IDLE_TIME_VALUE = "300";

    public static final String EXPECTED_LOW_RES_MAX_IDLE_TIME_KEY = Const.KETTLE_CARTE_JETTY_RES_MAX_IDLE_TIME;

    Map<String, String> jettyOptions;

    SlaveServerConfig slServerConfig;

    @Test
    public void testSetUpJettyOptionsAsSystemParameters() throws KettleXMLException {
        Node configNode = getConfigNode(getConfigWithAllOptions());
        slServerConfig.setUpJettyOptions(configNode);
        Assert.assertTrue(("Expected containing jetty option " + (SlaveServerConfigTest.EXPECTED_ACCEPTORS_KEY)), System.getProperties().containsKey(SlaveServerConfigTest.EXPECTED_ACCEPTORS_KEY));
        Assert.assertEquals(SlaveServerConfigTest.EXPECTED_ACCEPTORS_VALUE, System.getProperty(SlaveServerConfigTest.EXPECTED_ACCEPTORS_KEY));
        Assert.assertTrue(("Expected containing jetty option " + (SlaveServerConfigTest.EXPECTED_ACCEPT_QUEUE_SIZE_KEY)), System.getProperties().containsKey(SlaveServerConfigTest.EXPECTED_ACCEPT_QUEUE_SIZE_KEY));
        Assert.assertEquals(SlaveServerConfigTest.EXPECTED_ACCEPT_QUEUE_SIZE_VALUE, System.getProperty(SlaveServerConfigTest.EXPECTED_ACCEPT_QUEUE_SIZE_KEY));
        Assert.assertTrue(("Expected containing jetty option " + (SlaveServerConfigTest.EXPECTED_LOW_RES_MAX_IDLE_TIME_KEY)), System.getProperties().containsKey(SlaveServerConfigTest.EXPECTED_LOW_RES_MAX_IDLE_TIME_KEY));
        Assert.assertEquals(SlaveServerConfigTest.EXPECTED_LOW_RES_MAX_IDLE_TIME_VALUE, System.getProperty(SlaveServerConfigTest.EXPECTED_LOW_RES_MAX_IDLE_TIME_KEY));
    }

    @Test
    public void testDoNotSetUpJettyOptionsAsSystemParameters_WhenNoOptionsNode() throws KettleXMLException {
        Node configNode = getConfigNode(getConfigWithNoOptionsNode());
        slServerConfig.setUpJettyOptions(configNode);
        Assert.assertFalse(("There should not be any jetty option but it is here:  " + (SlaveServerConfigTest.EXPECTED_ACCEPTORS_KEY)), System.getProperties().containsKey(SlaveServerConfigTest.EXPECTED_ACCEPTORS_KEY));
        Assert.assertFalse(("There should not be any jetty option but it is here:  " + (SlaveServerConfigTest.EXPECTED_ACCEPT_QUEUE_SIZE_KEY)), System.getProperties().containsKey(SlaveServerConfigTest.EXPECTED_ACCEPT_QUEUE_SIZE_KEY));
        Assert.assertFalse(("There should not be any jetty option but it is here:  " + (SlaveServerConfigTest.EXPECTED_LOW_RES_MAX_IDLE_TIME_KEY)), System.getProperties().containsKey(SlaveServerConfigTest.EXPECTED_LOW_RES_MAX_IDLE_TIME_KEY));
    }

    @Test
    public void testDoNotSetUpJettyOptionsAsSystemParameters_WhenEmptyOptionsNode() throws KettleXMLException {
        Node configNode = getConfigNode(getConfigWithEmptyOptionsNode());
        slServerConfig.setUpJettyOptions(configNode);
        Assert.assertFalse(("There should not be any jetty option but it is here:  " + (SlaveServerConfigTest.EXPECTED_ACCEPTORS_KEY)), System.getProperties().containsKey(SlaveServerConfigTest.EXPECTED_ACCEPTORS_KEY));
        Assert.assertFalse(("There should not be any jetty option but it is here:  " + (SlaveServerConfigTest.EXPECTED_ACCEPT_QUEUE_SIZE_KEY)), System.getProperties().containsKey(SlaveServerConfigTest.EXPECTED_ACCEPT_QUEUE_SIZE_KEY));
        Assert.assertFalse(("There should not be any jetty option but it is here:  " + (SlaveServerConfigTest.EXPECTED_LOW_RES_MAX_IDLE_TIME_KEY)), System.getProperties().containsKey(SlaveServerConfigTest.EXPECTED_LOW_RES_MAX_IDLE_TIME_KEY));
    }

    @Test
    public void testParseJettyOption_Acceptors() throws KettleXMLException {
        Node configNode = getConfigNode(getConfigWithAcceptorsOnlyOption());
        Map<String, String> parseJettyOptions = slServerConfig.parseJettyOptions(configNode);
        Assert.assertNotNull(parseJettyOptions);
        Assert.assertEquals(1, parseJettyOptions.size());
        Assert.assertTrue(("Expected containing key=" + (SlaveServerConfigTest.EXPECTED_ACCEPTORS_KEY)), parseJettyOptions.containsKey(SlaveServerConfigTest.EXPECTED_ACCEPTORS_KEY));
        Assert.assertEquals(SlaveServerConfigTest.EXPECTED_ACCEPTORS_VALUE, parseJettyOptions.get(SlaveServerConfigTest.EXPECTED_ACCEPTORS_KEY));
    }

    @Test
    public void testParseJettyOption_AcceptQueueSize() throws KettleXMLException {
        Node configNode = getConfigNode(getConfigWithAcceptQueueSizeOnlyOption());
        Map<String, String> parseJettyOptions = slServerConfig.parseJettyOptions(configNode);
        Assert.assertNotNull(parseJettyOptions);
        Assert.assertEquals(1, parseJettyOptions.size());
        Assert.assertTrue(("Expected containing key=" + (SlaveServerConfigTest.EXPECTED_ACCEPT_QUEUE_SIZE_KEY)), parseJettyOptions.containsKey(SlaveServerConfigTest.EXPECTED_ACCEPT_QUEUE_SIZE_KEY));
        Assert.assertEquals(SlaveServerConfigTest.EXPECTED_ACCEPT_QUEUE_SIZE_VALUE, parseJettyOptions.get(SlaveServerConfigTest.EXPECTED_ACCEPT_QUEUE_SIZE_KEY));
    }

    @Test
    public void testParseJettyOption_LowResourcesMaxIdleTime() throws KettleXMLException {
        Node configNode = getConfigNode(getConfigWithLowResourcesMaxIdleTimeeOnlyOption());
        Map<String, String> parseJettyOptions = slServerConfig.parseJettyOptions(configNode);
        Assert.assertNotNull(parseJettyOptions);
        Assert.assertEquals(1, parseJettyOptions.size());
        Assert.assertTrue(("Expected containing key=" + (SlaveServerConfigTest.EXPECTED_LOW_RES_MAX_IDLE_TIME_KEY)), parseJettyOptions.containsKey(SlaveServerConfigTest.EXPECTED_LOW_RES_MAX_IDLE_TIME_KEY));
        Assert.assertEquals(SlaveServerConfigTest.EXPECTED_LOW_RES_MAX_IDLE_TIME_VALUE, parseJettyOptions.get(SlaveServerConfigTest.EXPECTED_LOW_RES_MAX_IDLE_TIME_KEY));
    }

    @Test
    public void testParseJettyOption_AllOptions() throws KettleXMLException {
        Node configNode = getConfigNode(getConfigWithAllOptions());
        Map<String, String> parseJettyOptions = slServerConfig.parseJettyOptions(configNode);
        Assert.assertNotNull(parseJettyOptions);
        Assert.assertEquals(3, parseJettyOptions.size());
        Assert.assertTrue(("Expected containing key=" + (SlaveServerConfigTest.EXPECTED_ACCEPTORS_KEY)), parseJettyOptions.containsKey(SlaveServerConfigTest.EXPECTED_ACCEPTORS_KEY));
        Assert.assertEquals(SlaveServerConfigTest.EXPECTED_ACCEPTORS_VALUE, parseJettyOptions.get(SlaveServerConfigTest.EXPECTED_ACCEPTORS_KEY));
        Assert.assertTrue(("Expected containing key=" + (SlaveServerConfigTest.EXPECTED_ACCEPT_QUEUE_SIZE_KEY)), parseJettyOptions.containsKey(SlaveServerConfigTest.EXPECTED_ACCEPT_QUEUE_SIZE_KEY));
        Assert.assertEquals(SlaveServerConfigTest.EXPECTED_ACCEPT_QUEUE_SIZE_VALUE, parseJettyOptions.get(SlaveServerConfigTest.EXPECTED_ACCEPT_QUEUE_SIZE_KEY));
        Assert.assertTrue(("Expected containing key=" + (SlaveServerConfigTest.EXPECTED_LOW_RES_MAX_IDLE_TIME_KEY)), parseJettyOptions.containsKey(SlaveServerConfigTest.EXPECTED_LOW_RES_MAX_IDLE_TIME_KEY));
        Assert.assertEquals(SlaveServerConfigTest.EXPECTED_LOW_RES_MAX_IDLE_TIME_VALUE, parseJettyOptions.get(SlaveServerConfigTest.EXPECTED_LOW_RES_MAX_IDLE_TIME_KEY));
    }

    @Test
    public void testParseJettyOption_EmptyOptionsNode() throws KettleXMLException {
        Node configNode = getConfigNode(getConfigWithEmptyOptionsNode());
        Map<String, String> parseJettyOptions = slServerConfig.parseJettyOptions(configNode);
        Assert.assertNotNull(parseJettyOptions);
        Assert.assertEquals(0, parseJettyOptions.size());
    }

    @Test
    public void testParseJettyOption_NoOptionsNode() throws KettleXMLException {
        Node configNode = getConfigNode(getConfigWithNoOptionsNode());
        Map<String, String> parseJettyOptions = slServerConfig.parseJettyOptions(configNode);
        Assert.assertNull(parseJettyOptions);
    }
}

