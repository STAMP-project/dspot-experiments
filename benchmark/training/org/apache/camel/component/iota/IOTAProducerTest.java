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
package org.apache.camel.component.iota;


import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class IOTAProducerTest extends CamelTestSupport {
    private static final String SEED = "IHDEENZYITYVYSPKAURUZAQKGVJEREFDJMYTANNXXGPZ9GJWTEOJJ9IPMXOGZNQLSNMFDSQOTZAEETUEA";

    private static final String ADDRESS = "LXQHWNY9CQOHPNMKFJFIJHGEPAENAOVFRDIBF99PPHDTWJDCGHLYETXT9NPUVSNKT9XDTDYNJKJCPQMZCCOZVXMTXC";

    private static final String IOTA_NODE_URL = "https://nodes.thetangle.org:443";

    @Test
    public void sendTransferTest() throws Exception {
        final String message = "ILOVEAPACHECAMEL";
        MockEndpoint mock = getMockEndpoint("mock:iota-send-message-response");
        mock.expectedMinimumMessageCount(1);
        template.sendBody("direct:iota-send-message", message);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void getNewAddressTest() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:iota-new-address-response");
        mock.expectedMinimumMessageCount(1);
        template.sendBody("direct:iota-new-address", new String());
        assertMockEndpointsSatisfied();
    }

    @Test
    public void getTransfersTest() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:iota-get-transfers-response");
        mock.expectedMinimumMessageCount(1);
        template.sendBody("direct:iota-get-transfers", new String());
        assertMockEndpointsSatisfied();
    }
}

