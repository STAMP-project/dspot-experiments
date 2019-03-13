/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.web.reactive.result;


import HttpStatus.NOT_FOUND;
import HttpStatus.OK;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.AbstractHttpHandlerIntegrationTests;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * Integration tests with requests mapped via
 * {@link SimpleUrlHandlerMapping} to plain {@link WebHandler}s.
 *
 * @author Rossen Stoyanchev
 */
public class SimpleUrlHandlerMappingIntegrationTests extends AbstractHttpHandlerIntegrationTests {
    @Test
    public void testRequestToFooHandler() throws Exception {
        URI url = new URI((("http://localhost:" + (this.port)) + "/foo"));
        RequestEntity<Void> request = RequestEntity.get(url).build();
        ResponseEntity<byte[]> response = new RestTemplate().exchange(request, byte[].class);
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertArrayEquals("foo".getBytes("UTF-8"), response.getBody());
    }

    @Test
    public void testRequestToBarHandler() throws Exception {
        URI url = new URI((("http://localhost:" + (this.port)) + "/bar"));
        RequestEntity<Void> request = RequestEntity.get(url).build();
        ResponseEntity<byte[]> response = new RestTemplate().exchange(request, byte[].class);
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertArrayEquals("bar".getBytes("UTF-8"), response.getBody());
    }

    @Test
    public void testRequestToHeaderSettingHandler() throws Exception {
        URI url = new URI((("http://localhost:" + (this.port)) + "/header"));
        RequestEntity<Void> request = RequestEntity.get(url).build();
        ResponseEntity<byte[]> response = new RestTemplate().exchange(request, byte[].class);
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertEquals("bar", response.getHeaders().getFirst("foo"));
    }

    @Test
    public void testHandlerNotFound() throws Exception {
        URI url = new URI((("http://localhost:" + (this.port)) + "/oops"));
        RequestEntity<Void> request = RequestEntity.get(url).build();
        try {
            new RestTemplate().exchange(request, byte[].class);
        } catch (HttpClientErrorException ex) {
            Assert.assertEquals(NOT_FOUND, ex.getStatusCode());
        }
    }

    @Configuration
    static class WebConfig {
        @Bean
        public SimpleUrlHandlerMapping handlerMapping() {
            return new SimpleUrlHandlerMapping() {
                {
                    Map<String, Object> map = new HashMap<>();
                    map.put("/foo", ((WebHandler) (( exchange) -> exchange.getResponse().writeWith(Flux.just(asDataBuffer("foo"))))));
                    map.put("/bar", ((WebHandler) (( exchange) -> exchange.getResponse().writeWith(Flux.just(asDataBuffer("bar"))))));
                    map.put("/header", ((WebHandler) (( exchange) -> {
                        exchange.getResponse().getHeaders().add("foo", "bar");
                        return Mono.empty();
                    })));
                    setUrlMap(map);
                }
            };
        }

        @Bean
        public SimpleHandlerAdapter handlerAdapter() {
            return new SimpleHandlerAdapter();
        }
    }
}

