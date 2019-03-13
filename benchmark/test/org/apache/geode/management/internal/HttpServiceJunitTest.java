/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal;


import SecurableCommunicationChannel.WEB;
import org.apache.geode.distributed.internal.DistributionConfig;
import org.apache.geode.internal.cache.HttpService;
import org.apache.geode.internal.net.SSLConfigurationFactory;
import org.junit.Test;


/**
 * The HttpServiceJunitTest class is a test suite of test cases testing the contract and
 * functionality of the JettyHelper class. Does not start Jetty.
 *
 * @see org.junit.Test
 */
public class HttpServiceJunitTest {
    private DistributionConfig distributionConfig;

    @Test
    public void testSetPortNoBindAddress() {
        final HttpService jetty = new HttpService(null, 8090, SSLConfigurationFactory.getSSLConfigForComponent(distributionConfig, WEB));
        assertThat(jetty).isNotNull();
        assertThat(jetty.getHttpServer().getConnectors()[0]).isNotNull();
        assertThat(getPort()).isEqualTo(8090);
    }

    @Test
    public void testSetPortWithBindAddress() {
        final HttpService jetty = new HttpService("10.123.50.1", 10480, SSLConfigurationFactory.getSSLConfigForComponent(distributionConfig, WEB));
        assertThat(jetty).isNotNull();
        assertThat(jetty.getHttpServer().getConnectors()[0]).isNotNull();
        assertThat(getPort()).isEqualTo(10480);
    }
}

