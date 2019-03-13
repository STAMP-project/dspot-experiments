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
import java.net.URI;
import mousio.etcd4j.EtcdClient;
import org.apache.camel.component.etcd.EtcdHelper;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;


public class SpringEtcdServiceCallRouteTest extends CamelSpringTestSupport {
    private static final ObjectMapper MAPPER = EtcdHelper.createObjectMapper();

    private static final EtcdClient CLIENT = new EtcdClient(URI.create("http://localhost:2379"));

    // *************************************************************************
    // Test
    // *************************************************************************
    @Test
    public void testServiceCall() throws Exception {
        getMockEndpoint("mock:result-1").expectedMessageCount(2);
        getMockEndpoint("mock:result-1").expectedBodiesReceivedInAnyOrder("service-1 9091", "service-1 9092");
        getMockEndpoint("mock:result-2").expectedMessageCount(2);
        getMockEndpoint("mock:result-2").expectedBodiesReceivedInAnyOrder("service-2 9093", "service-2 9094");
        template.sendBody("direct:start", "service-1");
        template.sendBody("direct:start", "service-1");
        template.sendBody("direct:start", "service-2");
        template.sendBody("direct:start", "service-2");
        assertMockEndpointsSatisfied();
    }
}

