/**
 * Copyright 2012-2018 the original author or authors.
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
package sample.atmosphere;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = SampleAtmosphereApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class SampleAtmosphereApplicationTests {
    private static Log logger = LogFactory.getLog(SampleAtmosphereApplicationTests.class);

    @LocalServerPort
    private int port = 1234;

    @Test
    public void chatEndpoint() {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(SampleAtmosphereApplicationTests.ClientConfiguration.class, PropertyPlaceholderAutoConfiguration.class).properties((("websocket.uri:ws://localhost:" + (this.port)) + "/chat/websocket")).run("--spring.main.web-application-type=none");
        long count = context.getBean(SampleAtmosphereApplicationTests.ClientConfiguration.class).latch.getCount();
        AtomicReference<String> messagePayloadReference = context.getBean(SampleAtmosphereApplicationTests.ClientConfiguration.class).messagePayload;
        context.close();
        assertThat(count).isEqualTo(0L);
        assertThat(messagePayloadReference.get()).contains("{\"message\":\"test\",\"author\":\"test\",\"time\":");
    }

    @Configuration
    static class ClientConfiguration implements CommandLineRunner {
        @Value("${websocket.uri}")
        private String webSocketUri;

        private final CountDownLatch latch = new CountDownLatch(1);

        private final AtomicReference<String> messagePayload = new AtomicReference<>();

        @Override
        public void run(String... args) throws Exception {
            SampleAtmosphereApplicationTests.logger.info(("Waiting for response: latch=" + (this.latch.getCount())));
            if (this.latch.await(10, TimeUnit.SECONDS)) {
                SampleAtmosphereApplicationTests.logger.info(("Got response: " + (this.messagePayload.get())));
            } else {
                SampleAtmosphereApplicationTests.logger.info(("Response not received: latch=" + (this.latch.getCount())));
            }
        }

        @Bean
        public WebSocketConnectionManager wsConnectionManager() {
            WebSocketConnectionManager manager = new WebSocketConnectionManager(client(), handler(), this.webSocketUri);
            manager.setAutoStartup(true);
            return manager;
        }

        @Bean
        public StandardWebSocketClient client() {
            return new StandardWebSocketClient();
        }

        @Bean
        public TextWebSocketHandler handler() {
            return new TextWebSocketHandler() {
                @Override
                public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                    session.sendMessage(new TextMessage("{\"author\":\"test\",\"message\":\"test\"}"));
                }

                @Override
                protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                    SampleAtmosphereApplicationTests.logger.info((((("Received: " + message) + " (") + (SampleAtmosphereApplicationTests.ClientConfiguration.this.latch.getCount())) + ")"));
                    session.close();
                    SampleAtmosphereApplicationTests.ClientConfiguration.this.messagePayload.set(message.getPayload());
                    SampleAtmosphereApplicationTests.ClientConfiguration.this.latch.countDown();
                }
            };
        }
    }
}

