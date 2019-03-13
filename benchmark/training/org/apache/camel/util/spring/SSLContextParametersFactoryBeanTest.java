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
package org.apache.camel.util.spring;


import ClientAuthentication.WANT;
import javax.annotation.Resource;
import org.apache.camel.support.jsse.SSLContextParameters;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SSLContextParametersFactoryBeanTest {
    @Resource
    SSLContextParameters scp;

    @Resource(name = "&scp")
    SSLContextParametersFactoryBean scpfb;

    @Test
    public void testKeyStoreParameters() {
        Assert.assertEquals("provider", scp.getProvider());
        Assert.assertEquals("protocol", scp.getSecureSocketProtocol());
        Assert.assertEquals("alice", scp.getCertAlias());
        validateBaseSSLContextParameters(scp);
        Assert.assertNotNull(scp.getKeyManagers());
        Assert.assertEquals("keyPassword", scp.getKeyManagers().getKeyPassword());
        Assert.assertEquals("provider", scp.getKeyManagers().getProvider());
        Assert.assertNotNull(scp.getKeyManagers().getKeyStore());
        Assert.assertEquals("type", scp.getKeyManagers().getKeyStore().getType());
        Assert.assertNotNull(scp.getTrustManagers());
        Assert.assertEquals("provider", scp.getTrustManagers().getProvider());
        Assert.assertNotNull(scp.getTrustManagers().getKeyStore());
        Assert.assertEquals("type", scp.getTrustManagers().getKeyStore().getType());
        Assert.assertNotNull(scp.getSecureRandom());
        Assert.assertEquals("provider", scp.getSecureRandom().getProvider());
        Assert.assertEquals("algorithm", scp.getSecureRandom().getAlgorithm());
        Assert.assertNotNull(scp.getClientParameters());
        validateBaseSSLContextParameters(scp.getClientParameters());
        Assert.assertNotNull(scp.getServerParameters());
        Assert.assertEquals(WANT.name(), scp.getServerParameters().getClientAuthentication());
        validateBaseSSLContextParameters(scp.getServerParameters());
        Assert.assertEquals("test", scpfb.getCamelContext().getName());
        Assert.assertNotNull(scp.getCamelContext());
        Assert.assertNotNull(scp.getCipherSuitesFilter().getCamelContext());
        Assert.assertNotNull(scp.getSecureSocketProtocolsFilter().getCamelContext());
        Assert.assertNotNull(scp.getSecureRandom().getCamelContext());
        Assert.assertNotNull(scp.getKeyManagers().getCamelContext());
        Assert.assertNotNull(scp.getKeyManagers().getKeyStore().getCamelContext());
        Assert.assertNotNull(scp.getTrustManagers().getCamelContext());
        Assert.assertNotNull(scp.getTrustManagers().getKeyStore().getCamelContext());
        Assert.assertNotNull(scp.getClientParameters().getCamelContext());
        Assert.assertNotNull(scp.getClientParameters().getCipherSuitesFilter().getCamelContext());
        Assert.assertNotNull(scp.getClientParameters().getSecureSocketProtocolsFilter().getCamelContext());
        Assert.assertNotNull(scp.getServerParameters().getCamelContext());
        Assert.assertNotNull(scp.getServerParameters().getCipherSuitesFilter().getCamelContext());
        Assert.assertNotNull(scp.getServerParameters().getSecureSocketProtocolsFilter().getCamelContext());
    }
}

