/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.sink.http;


import Fault.RANDOM_DATA_THEN_CLOSE;
import Status.BACKOFF;
import Status.READY;
import com.github.tomakehurst.wiremock.global.RequestDelaySpec;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestListener;
import com.github.tomakehurst.wiremock.http.Response;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.flume.channel.MemoryChannel;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Runs a set of tests against a mocked HTTP endpoint.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestHttpSinkIT {
    private static final int RESPONSE_TIMEOUT = 4000;

    private static final int CONNECT_TIMEOUT = 2500;

    private MemoryChannel channel;

    private HttpSink httpSink;

    private final int port = TestHttpSinkIT.findFreePort();

    @Rule
    public WireMockRule service = new WireMockRule(wireMockConfig().port(port));

    @Test
    public void ensureSuccessfulMessageDelivery() throws Exception {
        service.stubFor(post(urlEqualTo("/endpoint")).withRequestBody(equalToJson(event("SUCCESS"))).willReturn(aResponse().withStatus(200)));
        addEventToChannel(event("SUCCESS"));
        service.verify(1, postRequestedFor(urlEqualTo("/endpoint")).withRequestBody(equalToJson(event("SUCCESS"))));
    }

    @Test
    public void ensureEventsResentOn503Failure() throws Exception {
        String errorScenario = "Error Scenario";
        service.stubFor(post(urlEqualTo("/endpoint")).inScenario(errorScenario).whenScenarioStateIs(STARTED).withRequestBody(equalToJson(event("TRANSIENT_ERROR"))).willReturn(aResponse().withStatus(503)).willSetStateTo("Error Sent"));
        service.stubFor(post(urlEqualTo("/endpoint")).inScenario(errorScenario).whenScenarioStateIs("Error Sent").withRequestBody(equalToJson(event("TRANSIENT_ERROR"))).willReturn(aResponse().withStatus(200)));
        addEventToChannel(event("TRANSIENT_ERROR"), BACKOFF);
        addEventToChannel(event("TRANSIENT_ERROR"), READY);
        service.verify(2, postRequestedFor(urlEqualTo("/endpoint")).withRequestBody(equalToJson(event("TRANSIENT_ERROR"))));
    }

    @Test
    public void ensureEventsNotResentOn401Failure() throws Exception {
        String errorScenario = "Error skip scenario";
        service.stubFor(post(urlEqualTo("/endpoint")).inScenario(errorScenario).whenScenarioStateIs(STARTED).withRequestBody(equalToJson(event("UNAUTHORIZED REQUEST"))).willReturn(aResponse().withStatus(401).withHeader("Content-Type", "text/plain").withBody("Not allowed!")).willSetStateTo("Error Sent"));
        service.stubFor(post(urlEqualTo("/endpoint")).inScenario(errorScenario).whenScenarioStateIs("Error Sent").withRequestBody(equalToJson(event("NEXT EVENT"))).willReturn(aResponse().withStatus(200)));
        addEventToChannel(event("UNAUTHORIZED REQUEST"), READY);
        addEventToChannel(event("NEXT EVENT"), READY);
        service.verify(1, postRequestedFor(urlEqualTo("/endpoint")).withRequestBody(equalToJson(event("UNAUTHORIZED REQUEST"))));
        service.verify(1, postRequestedFor(urlEqualTo("/endpoint")).withRequestBody(equalToJson(event("NEXT EVENT"))));
    }

    @Test
    public void ensureEventsResentOnNetworkFailure() throws Exception {
        String errorScenario = "Error Scenario";
        service.stubFor(post(urlEqualTo("/endpoint")).inScenario(errorScenario).whenScenarioStateIs(STARTED).withRequestBody(equalToJson(event("NETWORK_ERROR"))).willReturn(aResponse().withFault(RANDOM_DATA_THEN_CLOSE)).willSetStateTo("Error Sent"));
        service.stubFor(post(urlEqualTo("/endpoint")).inScenario(errorScenario).whenScenarioStateIs("Error Sent").withRequestBody(equalToJson(event("NETWORK_ERROR"))).willReturn(aResponse().withStatus(200)));
        addEventToChannel(event("NETWORK_ERROR"), BACKOFF);
        addEventToChannel(event("NETWORK_ERROR"), READY);
        service.verify(2, postRequestedFor(urlEqualTo("/endpoint")).withRequestBody(equalToJson(event("NETWORK_ERROR"))));
    }

    @Test
    public void ensureEventsResentOnConnectionTimeout() throws Exception {
        final CountDownLatch firstRequestReceived = new CountDownLatch(1);
        service.addSocketAcceptDelay(new RequestDelaySpec(TestHttpSinkIT.CONNECT_TIMEOUT));
        service.addMockServiceRequestListener(new RequestListener() {
            @Override
            public void requestReceived(Request request, Response response) {
                service.addSocketAcceptDelay(new RequestDelaySpec(0));
                firstRequestReceived.countDown();
            }
        });
        service.stubFor(post(urlEqualTo("/endpoint")).withRequestBody(equalToJson(event("SLOW_SOCKET"))).willReturn(aResponse().withStatus(200)));
        addEventToChannel(event("SLOW_SOCKET"), BACKOFF);
        // wait until the socket is connected
        firstRequestReceived.await(2000, TimeUnit.MILLISECONDS);
        addEventToChannel(event("SLOW_SOCKET"), READY);
        service.verify(2, postRequestedFor(urlEqualTo("/endpoint")).withRequestBody(equalToJson(event("SLOW_SOCKET"))));
    }

    @Test
    public void ensureEventsResentOnRequestTimeout() throws Exception {
        String errorScenario = "Error Scenario";
        service.stubFor(post(urlEqualTo("/endpoint")).inScenario(errorScenario).whenScenarioStateIs(STARTED).withRequestBody(equalToJson(event("SLOW_RESPONSE"))).willReturn(aResponse().withFixedDelay(TestHttpSinkIT.RESPONSE_TIMEOUT).withStatus(200)).willSetStateTo("Slow Response Sent"));
        service.stubFor(post(urlEqualTo("/endpoint")).inScenario(errorScenario).whenScenarioStateIs("Slow Response Sent").withRequestBody(equalToJson(event("SLOW_RESPONSE"))).willReturn(aResponse().withStatus(200)));
        addEventToChannel(event("SLOW_RESPONSE"), BACKOFF);
        addEventToChannel(event("SLOW_RESPONSE"), READY);
        service.verify(2, postRequestedFor(urlEqualTo("/endpoint")).withRequestBody(equalToJson(event("SLOW_RESPONSE"))));
    }

    @Test
    public void ensureHttpConnectionReusedForSuccessfulRequests() throws Exception {
        // we should only get one delay when establishing a connection
        service.addSocketAcceptDelay(new RequestDelaySpec(1000));
        service.stubFor(post(urlEqualTo("/endpoint")).withRequestBody(equalToJson(event("SUCCESS"))).willReturn(aResponse().withStatus(200)));
        long startTime = System.currentTimeMillis();
        addEventToChannel(event("SUCCESS"), READY);
        addEventToChannel(event("SUCCESS"), READY);
        addEventToChannel(event("SUCCESS"), READY);
        long endTime = System.currentTimeMillis();
        Assert.assertTrue("Test should have completed faster", ((endTime - startTime) < 2500));
        service.verify(3, postRequestedFor(urlEqualTo("/endpoint")).withRequestBody(equalToJson(event("SUCCESS"))));
    }
}

