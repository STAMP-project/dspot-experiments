/**
 * Copyright 2014-2018 the original author or authors.
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
package de.codecentric.boot.admin.client;


import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import de.codecentric.boot.admin.client.registration.ApplicationRegistrator;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;


public abstract class AbstractClientApplicationTest {
    @Rule
    public WireMockRule wireMock = new WireMockRule(options().dynamicPort().notifier(new ConsoleNotifier(true)));

    private static final CountDownLatch cdl = new CountDownLatch(1);

    @Test
    public void test_context() throws InterruptedException, UnknownHostException {
        AbstractClientApplicationTest.cdl.await();
        Thread.sleep(2500);
        String hostName = InetAddress.getLocalHost().getCanonicalHostName();
        String serviceHost = (("http://" + hostName) + ":") + (getServerPort());
        String managementHost = (("http://" + hostName) + ":") + (getManagementPort());
        RequestPatternBuilder request = postRequestedFor(urlEqualTo("/instances"));
        request.withHeader("Content-Type", equalTo("application/json")).withRequestBody(matchingJsonPath("$.name", equalTo("Test-Client"))).withRequestBody(matchingJsonPath("$.healthUrl", equalTo((managementHost + "/mgmt/health")))).withRequestBody(matchingJsonPath("$.managementUrl", equalTo((managementHost + "/mgmt")))).withRequestBody(matchingJsonPath("$.serviceUrl", equalTo((serviceHost + "/")))).withRequestBody(matchingJsonPath("$.metadata.startup", matching(".+")));
        verify(request);
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    public static class TestClientApplication {
        @Autowired
        private ApplicationRegistrator registrator;

        @EventListener
        public void ping(ApplicationReadyEvent ev) {
            new Thread(() -> {
                try {
                    while ((registrator.getRegisteredId()) == null) {
                        Thread.sleep(500);
                    } 
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
                AbstractClientApplicationTest.cdl.countDown();
            }).start();
        }
    }
}

