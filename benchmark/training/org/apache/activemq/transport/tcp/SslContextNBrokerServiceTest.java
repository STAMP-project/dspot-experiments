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
package org.apache.activemq.transport.tcp;


import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import javax.net.ssl.X509TrustManager;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class SslContextNBrokerServiceTest {
    private static final transient Logger LOG = LoggerFactory.getLogger(SslContextNBrokerServiceTest.class);

    private ClassPathXmlApplicationContext context;

    Map<String, BrokerService> beansOfType;

    @Test(timeout = (3 * 60) * 1000)
    public void testDummyConfigurationIsolation() throws Exception {
        Assert.assertTrue("dummy bean has dummy cert", verifyCredentials("dummy"));
    }

    @Test(timeout = (3 * 60) * 1000)
    public void testActiveMQDotOrgConfigurationIsolation() throws Exception {
        Assert.assertTrue("good bean has amq cert", verifyCredentials("activemq.org"));
    }

    class CertChainCatcher implements X509TrustManager {
        X509Certificate[] serverCerts;

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            serverCerts = arg0;
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}

