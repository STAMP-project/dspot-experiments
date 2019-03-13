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
package org.apache.camel.component.cxf;


import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Assert;
import org.junit.Test;


public class LoadDistributorFeatureTest {
    private static int port1 = CXFTestSupport.getPort1();

    private static int port2 = CXFTestSupport.getPort2();

    private static int port3 = CXFTestSupport.getPort3();

    private static final String SERVICE_ADDRESS_1 = ("http://localhost:" + (LoadDistributorFeatureTest.port1)) + "/LoadDistributorFeatureTest/service1";

    private static final String SERVICE_ADDRESS_2 = ("http://localhost:" + (LoadDistributorFeatureTest.port1)) + "/LoadDistributorFeatureTest/service2";

    private static final String PAYLOAD_PROXY_ADDRESS = ("http://localhost:" + (LoadDistributorFeatureTest.port2)) + "/LoadDistributorFeatureTest/proxy";

    private static final String POJO_PROXY_ADDRESS = ("http://localhost:" + (LoadDistributorFeatureTest.port3)) + "/LoadDistributorFeatureTest/proxy";

    private DefaultCamelContext context1;

    private DefaultCamelContext context2;

    @Test
    public void testPojo() throws Exception {
        startRoutePojo();
        Assert.assertEquals("hello Server1", tryLoadDistributor(LoadDistributorFeatureTest.POJO_PROXY_ADDRESS));
        Assert.assertEquals("hello Server2", tryLoadDistributor(LoadDistributorFeatureTest.POJO_PROXY_ADDRESS));
        if ((context2) != null) {
            context2.stop();
        }
    }

    @Test
    public void testPayload() throws Exception {
        startRoutePayload();
        Assert.assertEquals("hello Server1", tryLoadDistributor(LoadDistributorFeatureTest.PAYLOAD_PROXY_ADDRESS));
        Assert.assertEquals("hello Server2", tryLoadDistributor(LoadDistributorFeatureTest.PAYLOAD_PROXY_ADDRESS));
        if ((context1) != null) {
            context1.stop();
        }
    }
}

