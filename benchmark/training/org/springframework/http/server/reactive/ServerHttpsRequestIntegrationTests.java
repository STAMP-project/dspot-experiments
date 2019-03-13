/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.http.server.reactive;


import HttpStatus.OK;
import java.net.URI;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.bootstrap.HttpServer;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;


/**
 * HTTPS-specific integration test for {@link ServerHttpRequest}.
 *
 * @author Arjen Poutsma
 */
@RunWith(Parameterized.class)
public class ServerHttpsRequestIntegrationTests {
    private int port;

    @Parameterized.Parameter(0)
    public HttpServer server;

    private RestTemplate restTemplate;

    @Test
    public void checkUri() throws Exception {
        URI url = new URI((("https://localhost:" + (port)) + "/foo?param=bar"));
        RequestEntity<Void> request = RequestEntity.post(url).build();
        ResponseEntity<Void> response = this.restTemplate.exchange(request, Void.class);
        Assert.assertEquals(OK, response.getStatusCode());
    }

    public static class CheckRequestHandler implements HttpHandler {
        @Override
        public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
            URI uri = request.getURI();
            Assert.assertEquals("https", uri.getScheme());
            Assert.assertNotNull(uri.getHost());
            Assert.assertNotEquals((-1), uri.getPort());
            Assert.assertNotNull(request.getRemoteAddress());
            Assert.assertEquals("/foo", uri.getPath());
            Assert.assertEquals("param=bar", uri.getQuery());
            return Mono.empty();
        }
    }
}

