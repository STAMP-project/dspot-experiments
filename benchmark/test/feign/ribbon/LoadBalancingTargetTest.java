/**
 * Copyright 2012-2019 The Feign Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package feign.ribbon;


import feign.Feign;
import feign.RequestLine;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class LoadBalancingTargetTest {
    @Rule
    public final MockWebServer server1 = new MockWebServer();

    @Rule
    public final MockWebServer server2 = new MockWebServer();

    @Test
    public void loadBalancingDefaultPolicyRoundRobin() throws IOException, InterruptedException {
        String name = "LoadBalancingTargetTest-loadBalancingDefaultPolicyRoundRobin";
        String serverListKey = name + ".ribbon.listOfServers";
        server1.enqueue(new MockResponse().setBody("success!"));
        server2.enqueue(new MockResponse().setBody("success!"));
        getConfigInstance().setProperty(serverListKey, (((LoadBalancingTargetTest.hostAndPort(server1.url("").url())) + ",") + (LoadBalancingTargetTest.hostAndPort(server2.url("").url()))));
        try {
            LoadBalancingTarget<LoadBalancingTargetTest.TestInterface> target = LoadBalancingTarget.create(LoadBalancingTargetTest.TestInterface.class, ("http://" + name));
            LoadBalancingTargetTest.TestInterface api = Feign.builder().target(target);
            api.post();
            api.post();
            Assert.assertEquals(1, server1.getRequestCount());
            Assert.assertEquals(1, server2.getRequestCount());
            // TODO: verify ribbon stats match
            // assertEquals(target.lb().getLoadBalancerStats().getSingleServerStat())
        } finally {
            getConfigInstance().clearProperty(serverListKey);
        }
    }

    @Test
    public void loadBalancingTargetPath() throws InterruptedException {
        String name = "LoadBalancingTargetTest-loadBalancingDefaultPolicyRoundRobin";
        String serverListKey = name + ".ribbon.listOfServers";
        server1.enqueue(new MockResponse().setBody("success!"));
        getConfigInstance().setProperty(serverListKey, LoadBalancingTargetTest.hostAndPort(server1.url("").url()));
        try {
            LoadBalancingTarget<LoadBalancingTargetTest.TestInterface> target = LoadBalancingTarget.create(LoadBalancingTargetTest.TestInterface.class, (("http://" + name) + "/context-path"));
            LoadBalancingTargetTest.TestInterface api = Feign.builder().target(target);
            api.get();
            Assert.assertEquals("http:///context-path", target.url());
            Assert.assertEquals("/context-path/servers", server1.takeRequest().getPath());
        } finally {
            getConfigInstance().clearProperty(serverListKey);
        }
    }

    interface TestInterface {
        @RequestLine("POST /")
        void post();

        @RequestLine("GET /servers")
        void get();
    }
}

