/**
 * Copyright 2018-2019 the original author or authors.
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
package org.springframework.cloud.gateway.rsocket.server;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.gateway.rsocket.autoconfigure.GatewayRSocketProperties;
import org.springframework.cloud.gateway.rsocket.test.PingPongApp;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = PingPongApp.class, properties = { "ping.take=5" }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class GatewayRSocketIntegrationTests {
    private static int port;

    @Autowired
    private PingPongApp.Ping ping;

    @Autowired
    private PingPongApp.Pong pong;

    @Autowired
    private GatewayRSocketProperties properties;

    @Autowired
    private PingPongApp.MySocketAcceptorFilter mySocketAcceptorFilter;

    @Autowired
    private GatewayRSocketServer server;

    @Test
    public void contextLoads() {
        StepVerifier.create(ping.getPongFlux()).expectSubscription().then(() -> server.stop()).thenConsumeWhile(( s) -> true).verifyComplete();
        assertThat(ping.getPongsReceived()).isGreaterThan(0);
        assertThat(pong.getPingsReceived()).isGreaterThan(0);
        assertThat(properties.getServer().getPort()).isNotEqualTo(7002);
        assertThat(mySocketAcceptorFilter.invoked()).isTrue();
        assertThat(server.isRunning()).isFalse();
    }
}

