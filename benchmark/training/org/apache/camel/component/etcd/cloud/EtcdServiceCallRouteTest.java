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
package org.apache.camel.component.etcd.cloud;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import mousio.etcd4j.EtcdClient;
import org.apache.camel.component.etcd.EtcdHelper;
import org.apache.camel.component.etcd.EtcdTestSupport;
import org.junit.Test;


public class EtcdServiceCallRouteTest extends EtcdTestSupport {
    private static final ObjectMapper MAPPER = EtcdHelper.createObjectMapper();

    private static final String SERVICE_NAME = "http-service";

    private static final int SERVICE_COUNT = 5;

    private static final int SERVICE_PORT_BASE = 8080;

    private EtcdClient client;

    private List<Map<String, Object>> servers;

    private List<String> expectedBodies;

    // *************************************************************************
    // Test
    // *************************************************************************
    @Test
    public void testServiceCall() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(EtcdServiceCallRouteTest.SERVICE_COUNT);
        getMockEndpoint("mock:result").expectedBodiesReceivedInAnyOrder(expectedBodies);
        servers.forEach(( s) -> template.sendBody("direct:start", "ping"));
        assertMockEndpointsSatisfied();
    }
}

