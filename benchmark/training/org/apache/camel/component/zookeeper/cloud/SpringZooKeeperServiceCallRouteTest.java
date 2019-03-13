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
package org.apache.camel.component.zookeeper.cloud;


import org.apache.camel.component.zookeeper.ZooKeeperTestSupport;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.junit.Test;


public class SpringZooKeeperServiceCallRouteTest extends CamelSpringTestSupport {
    private static final int SERVER_PORT = 9001;

    private static final String SERVICE_NAME = "http-service";

    private static final String SERVICE_PATH = "/camel";

    private ZooKeeperTestSupport.TestZookeeperServer server;

    private CuratorFramework curator;

    private ServiceDiscovery<ZooKeeperServiceDiscovery.MetaData> discovery;

    // ***********************
    // Test
    // ***********************
    @Test
    public void testServiceCall() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(3);
        getMockEndpoint("mock:result").expectedBodiesReceivedInAnyOrder("ping 9011", "ping 9012", "ping 9013");
        template.sendBody("direct:start", "ping");
        template.sendBody("direct:start", "ping");
        template.sendBody("direct:start", "ping");
        assertMockEndpointsSatisfied();
    }
}

