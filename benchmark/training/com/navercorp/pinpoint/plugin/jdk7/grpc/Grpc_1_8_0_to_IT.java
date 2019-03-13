/**
 * Copyright 2019 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.plugin.jdk7.grpc;


import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifier;
import com.navercorp.pinpoint.plugin.AgentPath;
import com.navercorp.pinpoint.test.plugin.Dependency;
import com.navercorp.pinpoint.test.plugin.PinpointAgent;
import com.navercorp.pinpoint.test.plugin.PinpointConfig;
import com.navercorp.pinpoint.test.plugin.PinpointPluginTestSuite;
import java.util.Random;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Taejin Koo
 */
@RunWith(PinpointPluginTestSuite.class)
@PinpointAgent(AgentPath.PATH)
@Dependency({ "io.grpc:grpc-stub:[1.8.0,)", "io.grpc:grpc-netty:[1.8.0]", "io.grpc:grpc-protobuf:[1.8.0]" })
@PinpointConfig("pinpoint-grpc-plugin-test.config")
public class Grpc_1_8_0_to_IT {
    private final Logger logger = Logger.getLogger(Grpc_1_8_0_to_IT.class.getName());

    private final String REQUEST = "hello";

    @Test
    public void requestResponseTest() throws Exception {
        HelloWorldSimpleServer server = null;
        HelloWorldSimpleClient client = null;
        try {
            server = new HelloWorldSimpleServer();
            server.start();
            client = new HelloWorldSimpleClient("127.0.0.1", server.getBindPort());
            String response = client.greet(REQUEST);
            Assert.assertEquals(REQUEST.toUpperCase(), response);
            PluginTestVerifier verifier = getPluginTestVerifier();
            assertTrace(server, verifier);
            verifier.awaitTraceCount(8, 20, 3000);
            verifier.verifyTraceCount(8);
        } finally {
            clearResources(client, server);
        }
    }

    @Test
    public void streamingTest() throws Exception {
        HelloWorldStreamServer server = null;
        HelloWorldStreamClient client = null;
        Random random = new Random(System.currentTimeMillis());
        int requestCount = (random.nextInt(5)) + 1;
        try {
            server = new HelloWorldStreamServer();
            server.start();
            client = new HelloWorldStreamClient("127.0.0.1", server.getBindPort());
            client.greet(requestCount);
            Assert.assertEquals(requestCount, server.getRequestCount());
            PluginTestVerifier verifier = getPluginTestVerifier();
            assertTrace(server, verifier);
            verifier.awaitTraceCount((6 + (requestCount * 2)), 20, 3000);
            verifier.verifyTraceCount((6 + (requestCount * 2)));
        } finally {
            clearResources(client, server);
        }
    }
}

