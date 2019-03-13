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
package org.apache.camel.component.smpp;


import java.net.URI;
import java.net.URISyntaxException;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit test class for <code>org.apache.camel.component.smpp.SmppConfiguration</code>
 */
public class SmppConfigurationTest {
    private SmppConfiguration configuration;

    @Test
    public void getterShouldReturnTheDefaultValues() {
        Assert.assertEquals("1717", configuration.getDestAddr());
        Assert.assertEquals(0, configuration.getDestAddrNpi());
        Assert.assertEquals(0, configuration.getDestAddrTon());
        Assert.assertEquals("", configuration.getAddressRange());
        Assert.assertEquals(new Integer(5000), configuration.getEnquireLinkTimer());
        Assert.assertEquals("localhost", configuration.getHost());
        Assert.assertEquals("password", configuration.getPassword());
        Assert.assertEquals(new Integer(2775), configuration.getPort());
        Assert.assertEquals(1, configuration.getPriorityFlag());
        Assert.assertEquals(0, configuration.getProtocolId());
        Assert.assertEquals(1, configuration.getRegisteredDelivery());
        Assert.assertEquals(0, configuration.getReplaceIfPresentFlag());
        Assert.assertEquals("CMT", configuration.getServiceType());
        Assert.assertEquals("1616", configuration.getSourceAddr());
        Assert.assertEquals(0, configuration.getSourceAddrNpi());
        Assert.assertEquals(0, configuration.getSourceAddrTon());
        Assert.assertEquals("smppclient", configuration.getSystemId());
        Assert.assertEquals("", configuration.getSystemType());
        Assert.assertEquals(new Integer(10000), configuration.getTransactionTimer());
        Assert.assertEquals("ISO-8859-1", configuration.getEncoding());
        Assert.assertEquals(0, configuration.getNumberingPlanIndicator());
        Assert.assertEquals(0, configuration.getTypeOfNumber());
        Assert.assertEquals(false, configuration.getUsingSSL());
        Assert.assertEquals(5000, configuration.getInitialReconnectDelay());
        Assert.assertEquals(5000, configuration.getReconnectDelay());
        Assert.assertEquals(null, configuration.getHttpProxyHost());
        Assert.assertEquals(new Integer(3128), configuration.getHttpProxyPort());
        Assert.assertEquals(null, configuration.getHttpProxyUsername());
        Assert.assertEquals(null, configuration.getHttpProxyPassword());
        Assert.assertEquals(null, configuration.getSessionStateListener());
    }

    @Test
    public void getterShouldReturnTheSetValues() {
        setNoneDefaultValues(configuration);
        Assert.assertEquals("1919", configuration.getDestAddr());
        Assert.assertEquals(8, configuration.getDestAddrNpi());
        Assert.assertEquals(2, configuration.getDestAddrTon());
        Assert.assertEquals(new Integer(5001), configuration.getEnquireLinkTimer());
        Assert.assertEquals("127.0.0.1", configuration.getHost());
        Assert.assertEquals("secret", configuration.getPassword());
        Assert.assertEquals(new Integer(2776), configuration.getPort());
        Assert.assertEquals(0, configuration.getPriorityFlag());
        Assert.assertEquals(1, configuration.getProtocolId());
        Assert.assertEquals(0, configuration.getRegisteredDelivery());
        Assert.assertEquals(1, configuration.getReplaceIfPresentFlag());
        Assert.assertEquals("XXX", configuration.getServiceType());
        Assert.assertEquals("1818", configuration.getSourceAddr());
        Assert.assertEquals(8, configuration.getSourceAddrNpi());
        Assert.assertEquals(2, configuration.getSourceAddrTon());
        Assert.assertEquals("client", configuration.getSystemId());
        Assert.assertEquals("xx", configuration.getSystemType());
        Assert.assertEquals(new Integer(10001), configuration.getTransactionTimer());
        Assert.assertEquals("UTF-8", configuration.getEncoding());
        Assert.assertEquals(8, configuration.getNumberingPlanIndicator());
        Assert.assertEquals(2, configuration.getTypeOfNumber());
        Assert.assertEquals(true, configuration.getUsingSSL());
        Assert.assertEquals(5001, configuration.getInitialReconnectDelay());
        Assert.assertEquals(5002, configuration.getReconnectDelay());
        Assert.assertEquals("127.0.0.1", configuration.getHttpProxyHost());
        Assert.assertEquals(new Integer(3129), configuration.getHttpProxyPort());
        Assert.assertEquals("user", configuration.getHttpProxyUsername());
        Assert.assertEquals("secret", configuration.getHttpProxyPassword());
        Assert.assertNotNull(configuration.getSessionStateListener());
        Assert.assertEquals("1", configuration.getProxyHeaders().get("X-Proxy-Header"));
    }

    @Test
    public void getterShouldReturnTheConfigureValuesFromURI() throws URISyntaxException {
        configuration.configureFromURI(new URI("smpp://client@127.0.0.1:2776"));
        Assert.assertEquals("127.0.0.1", configuration.getHost());
        Assert.assertEquals(new Integer(2776), configuration.getPort());
        Assert.assertEquals("client", configuration.getSystemId());
    }

    @Test
    public void hostPortAndSystemIdFromComponentConfigurationShouldBeUsedIfAbsentFromUri() throws URISyntaxException {
        configuration.setHost("host");
        configuration.setPort(123);
        configuration.setSystemId("systemId");
        configuration.configureFromURI(new URI("smpp://?password=pw"));
        Assert.assertEquals("host", configuration.getHost());
        Assert.assertEquals(new Integer(123), configuration.getPort());
        Assert.assertEquals("systemId", configuration.getSystemId());
    }

    @Test
    public void cloneShouldReturnAnEqualInstance() {
        setNoneDefaultValues(configuration);
        SmppConfiguration config = configuration.copy();
        Assert.assertEquals(config.getDestAddr(), configuration.getDestAddr());
        Assert.assertEquals(config.getDestAddrNpi(), configuration.getDestAddrNpi());
        Assert.assertEquals(config.getDestAddrTon(), configuration.getDestAddrTon());
        Assert.assertEquals(config.getEnquireLinkTimer(), configuration.getEnquireLinkTimer());
        Assert.assertEquals(config.getHost(), configuration.getHost());
        Assert.assertEquals(config.getPassword(), configuration.getPassword());
        Assert.assertEquals(config.getPort(), configuration.getPort());
        Assert.assertEquals(config.getPriorityFlag(), configuration.getPriorityFlag());
        Assert.assertEquals(config.getProtocolId(), configuration.getProtocolId());
        Assert.assertEquals(config.getRegisteredDelivery(), configuration.getRegisteredDelivery());
        Assert.assertEquals(config.getReplaceIfPresentFlag(), configuration.getReplaceIfPresentFlag());
        Assert.assertEquals(config.getServiceType(), configuration.getServiceType());
        Assert.assertEquals(config.getSourceAddr(), configuration.getSourceAddr());
        Assert.assertEquals(config.getSourceAddrNpi(), configuration.getSourceAddrNpi());
        Assert.assertEquals(config.getSourceAddrTon(), configuration.getSourceAddrTon());
        Assert.assertEquals(config.getSystemId(), configuration.getSystemId());
        Assert.assertEquals(config.getSystemType(), configuration.getSystemType());
        Assert.assertEquals(config.getTransactionTimer(), configuration.getTransactionTimer());
        Assert.assertEquals(config.getEncoding(), configuration.getEncoding());
        Assert.assertEquals(config.getNumberingPlanIndicator(), configuration.getNumberingPlanIndicator());
        Assert.assertEquals(config.getTypeOfNumber(), configuration.getTypeOfNumber());
        Assert.assertEquals(config.getUsingSSL(), configuration.getUsingSSL());
        Assert.assertEquals(config.getInitialReconnectDelay(), configuration.getInitialReconnectDelay());
        Assert.assertEquals(config.getReconnectDelay(), configuration.getReconnectDelay());
        Assert.assertEquals(config.getHttpProxyHost(), configuration.getHttpProxyHost());
        Assert.assertEquals(config.getHttpProxyPort(), configuration.getHttpProxyPort());
        Assert.assertEquals(config.getHttpProxyUsername(), configuration.getHttpProxyUsername());
        Assert.assertEquals(config.getHttpProxyPassword(), configuration.getHttpProxyPassword());
        Assert.assertEquals(config.getSessionStateListener(), configuration.getSessionStateListener());
        Assert.assertEquals(config.getProxyHeaders(), configuration.getProxyHeaders());
    }

    @Test
    public void toStringShouldListAllInstanceVariables() {
        String expected = "SmppConfiguration[" + (((((((((((((((((((((((((((((((((("usingSSL=false, " + "enquireLinkTimer=5000, ") + "host=localhost, ") + "password=password, ") + "port=2775, ") + "systemId=smppclient, ") + "systemType=, ") + "dataCoding=0, ") + "alphabet=0, ") + "encoding=ISO-8859-1, ") + "transactionTimer=10000, ") + "registeredDelivery=1, ") + "serviceType=CMT, ") + "sourceAddrTon=0, ") + "destAddrTon=0, ") + "sourceAddrNpi=0, ") + "destAddrNpi=0, ") + "addressRange=, ") + "protocolId=0, ") + "priorityFlag=1, ") + "replaceIfPresentFlag=0, ") + "sourceAddr=1616, ") + "destAddr=1717, ") + "typeOfNumber=0, ") + "numberingPlanIndicator=0, ") + "initialReconnectDelay=5000, ") + "reconnectDelay=5000, ") + "maxReconnect=2147483647, ") + "lazySessionCreation=false, ") + "httpProxyHost=null, ") + "httpProxyPort=3128, ") + "httpProxyUsername=null, ") + "httpProxyPassword=null, ") + "splittingPolicy=ALLOW, ") + "proxyHeaders=null]");
        Assert.assertEquals(expected, configuration.toString());
    }
}

