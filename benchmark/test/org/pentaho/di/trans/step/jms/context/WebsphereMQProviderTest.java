/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.step.jms.context;


import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Topic;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.trans.step.jms.JmsDelegate;


@RunWith(MockitoJUnitRunner.class)
public class WebsphereMQProviderTest {
    private static final String HOST_NAME_VAL = "HOSTNAMEVAL";

    private static final String PORT_VAL = "1234";

    private static final String QUEUE_MANAGER_VAL = "QUEUEMANAGERVAL";

    private static final String CHANNEL_VAL = "CHANNELVAL";

    private static final String IBM_URL_BASE = (((((("mq://" + (WebsphereMQProviderTest.HOST_NAME_VAL)) + ":") + (WebsphereMQProviderTest.PORT_VAL)) + "/") + (WebsphereMQProviderTest.QUEUE_MANAGER_VAL)) + "?channel=") + (WebsphereMQProviderTest.CHANNEL_VAL);

    private static final String TRUST_STORE_PATH_VAL = "TRUST_STORE_PATH_VAL";

    private static final String TRUST_STORE_PASS_VAL = "TRUST_STORE_PASS_VAL";

    private static final String TRUST_STORE_TYPE_VAL = "TRUST_STORE_TYPE_VAL";

    private static final String KEY_STORE_PATH_VAL = "KEY_STORE_PATH_VAL";

    private static final String KEY_STORE_PASS_VAL = "KEY_STORE_PASS_VAL";

    private static final String KEY_STORE_TYPE_VAL = "KEY_STORE_TPYE_VAL";

    private static final String ENABLED_CIPHER_SUITES_VAL = "ENABLED_CIPHER_SUITES_VAL";

    private static final String ENABLED_PROTOCOLS_VAL = "ENABLED_PROTOCOLS_VAL";

    private static final String VERIFY_HOST_VAL = "VERIFY_HOST_VAL";

    private static final String TRUST_ALL_VAL = "TRUST_ALL_VAL";

    private static final String SSL_PROVIDER_VAL = "SSL_PROVIDER_VAL";

    private static final String FIPS_REQUIRED_VAL = "FIPS_REQUIRED_VAL";

    private static final String IBM_USERNAME_VAL = "IBM_USERNAME_VAL";

    private static final String IBM_PASSWORD_VAL = "IBM_PASSWORD_VAL";

    private static final boolean USE_DEFAULT_SSL_CONTEXT_VAL = true;

    private JmsProvider jmsProvider = new WebsphereMQProvider();

    @Mock
    private JmsDelegate jmsDelegate;

    @Test
    public void onlySupportsWebsphere() {
        TestCase.assertTrue(jmsProvider.supports(ConnectionType.WEBSPHERE));
        TestCase.assertFalse(jmsProvider.supports(ConnectionType.ACTIVEMQ));
    }

    @Test
    public void getQueueDestination() {
        jmsDelegate.destinationType = DestinationType.QUEUE.name();
        jmsDelegate.destinationName = "somename";
        Destination dest = jmsProvider.getDestination(jmsDelegate);
        TestCase.assertTrue((dest instanceof Queue));
    }

    @Test
    public void getTopicDestination() {
        jmsDelegate.destinationType = DestinationType.TOPIC.name();
        jmsDelegate.destinationName = "somename";
        Destination dest = jmsProvider.getDestination(jmsDelegate);
        TestCase.assertTrue((dest instanceof Topic));
    }

    @Test
    public void noDestinationNameSetCausesError() {
        jmsDelegate.destinationType = DestinationType.QUEUE.name();
        jmsDelegate.destinationName = null;
        try {
            jmsProvider.getDestination(jmsDelegate);
            Assert.fail();
        } catch (Exception e) {
            TestCase.assertTrue(e.getMessage().contains("Destination name must be set."));
        }
    }

    @Test
    public void getConnectionParams() {
        jmsDelegate.ibmUrl = WebsphereMQProviderTest.IBM_URL_BASE;
        jmsDelegate.ibmUsername = WebsphereMQProviderTest.IBM_USERNAME_VAL;
        jmsDelegate.ibmPassword = WebsphereMQProviderTest.IBM_PASSWORD_VAL;
        jmsDelegate.sslEnabled = true;
        jmsDelegate.sslTruststorePath = WebsphereMQProviderTest.TRUST_STORE_PATH_VAL;
        jmsDelegate.sslTruststorePassword = WebsphereMQProviderTest.TRUST_STORE_PASS_VAL;
        jmsDelegate.sslTruststoreType = WebsphereMQProviderTest.TRUST_STORE_TYPE_VAL;
        jmsDelegate.sslKeystorePath = WebsphereMQProviderTest.KEY_STORE_PATH_VAL;
        jmsDelegate.sslKeystorePassword = WebsphereMQProviderTest.KEY_STORE_PASS_VAL;
        jmsDelegate.sslKeystoreType = WebsphereMQProviderTest.KEY_STORE_TYPE_VAL;
        jmsDelegate.sslCipherSuite = WebsphereMQProviderTest.ENABLED_CIPHER_SUITES_VAL;
        jmsDelegate.sslContextAlgorithm = WebsphereMQProviderTest.ENABLED_PROTOCOLS_VAL;
        jmsDelegate.ibmSslFipsRequired = WebsphereMQProviderTest.FIPS_REQUIRED_VAL;
        jmsDelegate.sslUseDefaultContext = WebsphereMQProviderTest.USE_DEFAULT_SSL_CONTEXT_VAL;
        String debugString = jmsProvider.getConnectionDetails(jmsDelegate);
        TestCase.assertTrue("Missing trust store path", debugString.contains(("Trust Store: " + (WebsphereMQProviderTest.TRUST_STORE_PATH_VAL))));
        TestCase.assertTrue("Missing trust store password", debugString.contains(("Trust Store Pass: " + (WebsphereMQProviderTest.TRUST_STORE_PASS_VAL))));
        TestCase.assertTrue("Missing trust store type", debugString.contains(("Trust Store Type: " + (WebsphereMQProviderTest.TRUST_STORE_TYPE_VAL))));
        TestCase.assertTrue("Missing key store path", debugString.contains(("Key Store: " + (WebsphereMQProviderTest.KEY_STORE_PATH_VAL))));
        TestCase.assertTrue("Missing key store password", debugString.contains(("Key Store Pass: " + (WebsphereMQProviderTest.KEY_STORE_PASS_VAL))));
        TestCase.assertTrue("Missing key store type", debugString.contains(("Key Store Type: " + (WebsphereMQProviderTest.KEY_STORE_TYPE_VAL))));
        TestCase.assertTrue("Missing cipher suite", debugString.contains(("Cipher Suite: " + (WebsphereMQProviderTest.ENABLED_CIPHER_SUITES_VAL))));
        TestCase.assertTrue("Missing protocols", debugString.contains(("SSL Context Algorithm: " + (WebsphereMQProviderTest.ENABLED_PROTOCOLS_VAL))));
        TestCase.assertTrue("Missing host name", debugString.contains(("Hostname: " + (WebsphereMQProviderTest.HOST_NAME_VAL))));
        TestCase.assertTrue("Missing port", debugString.contains(("Port: " + (WebsphereMQProviderTest.PORT_VAL))));
        TestCase.assertTrue("Missing channel", debugString.contains(("Channel: " + (WebsphereMQProviderTest.CHANNEL_VAL))));
        TestCase.assertTrue("Missing queue manager", debugString.contains(("QueueManager: " + (WebsphereMQProviderTest.QUEUE_MANAGER_VAL))));
        TestCase.assertTrue("Missing username", debugString.contains(("User Name: " + (WebsphereMQProviderTest.IBM_USERNAME_VAL))));
        TestCase.assertTrue("Missing password", debugString.contains(("Password: " + (WebsphereMQProviderTest.IBM_PASSWORD_VAL))));
        TestCase.assertTrue("Missing use default SSL context", debugString.contains(("Use Default SSL Context:" + (WebsphereMQProviderTest.USE_DEFAULT_SSL_CONTEXT_VAL))));
    }
}

