/**
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.kubernetes.ribbon;


import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.server.mock.KubernetesServer;
import io.fabric8.mockwebserver.DefaultMockServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;


/**
 *
 *
 * @author Charles Moulliard
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class, properties = { "spring.application.name=testapp", "spring.cloud.kubernetes.client.namespace=testns", "spring.cloud.kubernetes.client.trustCerts=true", "spring.cloud.kubernetes.config.namespace=testns" })
@EnableAutoConfiguration
@EnableDiscoveryClient
public class RibbonFallbackTest {
    private static final Log LOG = LogFactory.getLog(RibbonFallbackTest.class);

    @ClassRule
    public static KubernetesServer mockServer = new KubernetesServer(false);

    public static DefaultMockServer mockEndpoint;

    public static KubernetesClient mockClient;

    @Autowired
    RestTemplate restTemplate;

    @Value("${service.occurrence}")
    private int serviceOccurrence;

    @Value("${testapp.ribbon.ServerListRefreshInterval}")
    private int serverListRefreshInterval;

    @Test
    public void testFallBackGreetingEndpoint() {
        /**
         * Scenario tested 1. Register the mock endpoint of the service into
         * KubeMockServer and call /greeting service 2. Unregister the mock endpoint and
         * verify that Ribbon doesn't have any instances anymore in its list 3. Re
         * register the mock endpoint and play step 1)
         */
        RibbonFallbackTest.LOG.info(">>>>>>>>>> BEGIN PART 1 <<<<<<<<<<<<<");
        // As Ribbon refreshes its list every serverListRefreshInterval ms,
        // we configure the API Server endpoint to reply to exactly serviceOccurrence
        // attempts
        // to be sure that Ribbon will get the mockendpoint to access it for the call
        RibbonFallbackTest.mockServer.expect().get().withPath("/api/v1/namespaces/testns/endpoints/testapp").andReturn(200, RibbonFallbackTest.newEndpoint("testapp-a", "testns", RibbonFallbackTest.mockEndpoint)).times(this.serviceOccurrence);
        RibbonFallbackTest.mockEndpoint.expect().get().withPath("/greeting").andReturn(200, "Hello from A").once();
        String response = this.restTemplate.getForObject("http://testapp/greeting", String.class);
        assertThat(response).isEqualTo("Hello from A");
        RibbonFallbackTest.LOG.info(">>>>>>>>>> END PART 1 <<<<<<<<<<<<<");
        RibbonFallbackTest.LOG.info(">>>>>>>>>> BEGIN PART 2 <<<<<<<<<<<<<");
        try {
            ensureEndpointsNoLongerReturnedByAPIServer();
            this.restTemplate.getForObject("http://testapp/greeting", String.class);
            fail("Ribbon was supposed to throw an Exception due to not knowing of any endpoints to route the request to");
        } catch (Exception e) {
            // No endpoint is available anymore and Ribbon list is empty
            assertThat(e.getMessage()).isEqualTo("No instances available for testapp");
        }
        RibbonFallbackTest.LOG.info(">>>>>>>>>> END PART 2 <<<<<<<<<<<<<");
        RibbonFallbackTest.LOG.info(">>>>>>>>>> BEGIN PART 3 <<<<<<<<<<<<<");
        RibbonFallbackTest.mockServer.expect().get().withPath("/api/v1/namespaces/testns/endpoints/testapp").andReturn(200, RibbonFallbackTest.newEndpoint("testapp-a", "testns", RibbonFallbackTest.mockEndpoint)).always();
        // the purpose of sleeping here is to make sure that even after some refreshes to
        // it's list
        // Ribbon still has endpoints to route to
        // This is different than the first part of the test because the API server has
        // now been
        // configured to always respond with some endpoints as opposed to only a certain
        // amount of
        // requests which was the case in part 1
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        RibbonFallbackTest.mockEndpoint.expect().get().withPath("/greeting").andReturn(200, "Hello from A").once();
        response = this.restTemplate.getForObject("http://testapp/greeting", String.class);
        assertThat(response).isEqualTo("Hello from A");
        RibbonFallbackTest.LOG.info(">>>>>>>>>> END PART 3 <<<<<<<<<<<<<");
    }
}

