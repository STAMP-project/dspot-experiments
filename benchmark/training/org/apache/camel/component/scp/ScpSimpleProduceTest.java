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
package org.apache.camel.component.scp;


import Exchange.FILE_NAME;
import org.junit.Assume;
import org.junit.Test;


public class ScpSimpleProduceTest extends ScpServerTestSupport {
    @Test
    public void testScpSimpleProduce() throws Exception {
        Assume.assumeTrue(this.isSetupComplete());
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World");
        String uri = ((getScpUri()) + "?username=admin&password=admin&knownHostsFile=") + (getKnownHostsFile());
        template.sendBodyAndHeader(uri, "Hello World", FILE_NAME, "hello.txt");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testScpSimpleProduceTwoTimes() throws Exception {
        Assume.assumeTrue(this.isSetupComplete());
        getMockEndpoint("mock:result").expectedBodiesReceivedInAnyOrder("Hello World", "Bye World");
        String uri = ((getScpUri()) + "?username=admin&password=admin&knownHostsFile=") + (getKnownHostsFile());
        template.sendBodyAndHeader(uri, "Hello World", FILE_NAME, "hello.txt");
        template.sendBodyAndHeader(uri, "Bye World", FILE_NAME, "bye.txt");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testScpSimpleSubPathProduce() throws Exception {
        Assume.assumeTrue(this.isSetupComplete());
        getMockEndpoint("mock:result").expectedBodiesReceived("Bye World");
        String uri = ((getScpUri()) + "?username=admin&password=admin&knownHostsFile=") + (getKnownHostsFile());
        template.sendBodyAndHeader(uri, "Bye World", FILE_NAME, "mysub/bye.txt");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testScpSimpleTwoSubPathProduce() throws Exception {
        Assume.assumeTrue(this.isSetupComplete());
        getMockEndpoint("mock:result").expectedBodiesReceived("Farewell World");
        String uri = ((getScpUri()) + "?username=admin&password=admin&knownHostsFile=") + (getKnownHostsFile());
        template.sendBodyAndHeader(uri, "Farewell World", FILE_NAME, "mysub/mysubsub/farewell.txt");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testScpProduceChmod() throws Exception {
        Assume.assumeTrue(this.isSetupComplete());
        getMockEndpoint("mock:result").expectedBodiesReceived("Bonjour Monde");
        String uri = ((getScpUri()) + "?username=admin&password=admin&chmod=640&knownHostsFile=") + (getKnownHostsFile());
        template.sendBodyAndHeader(uri, "Bonjour Monde", FILE_NAME, "monde.txt");
        assertMockEndpointsSatisfied();
    }
}

