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
package org.springframework.web.reactive.function.server;


import HttpStatus.OK;
import MediaType.TEXT_PLAIN;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.result.view.View;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Arjen Poutsma
 * @since 5.0
 */
public class RenderingResponseIntegrationTests extends AbstractRouterFunctionIntegrationTests {
    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    public void normal() {
        ResponseEntity<String> result = restTemplate.getForEntity((("http://localhost:" + (port)) + "/normal"), String.class);
        Assert.assertEquals(OK, result.getStatusCode());
        Map<String, String> body = parseBody(result.getBody());
        Assert.assertEquals(2, body.size());
        Assert.assertEquals("foo", body.get("name"));
        Assert.assertEquals("baz", body.get("bar"));
    }

    @Test
    public void filter() {
        ResponseEntity<String> result = restTemplate.getForEntity((("http://localhost:" + (port)) + "/filter"), String.class);
        Assert.assertEquals(OK, result.getStatusCode());
        Map<String, String> body = parseBody(result.getBody());
        Assert.assertEquals(3, body.size());
        Assert.assertEquals("foo", body.get("name"));
        Assert.assertEquals("baz", body.get("bar"));
        Assert.assertEquals("quux", body.get("qux"));
    }

    private static class RenderingResponseHandler {
        public Mono<RenderingResponse> render(ServerRequest request) {
            return RenderingResponse.create("foo").modelAttribute("bar", "baz").build();
        }
    }

    private static class DummyViewResolver implements ViewResolver {
        @Override
        public Mono<View> resolveViewName(String viewName, Locale locale) {
            return Mono.just(new RenderingResponseIntegrationTests.DummyView(viewName));
        }
    }

    private static class DummyView implements View {
        private final String name;

        public DummyView(String name) {
            this.name = name;
        }

        @Override
        public List<MediaType> getSupportedMediaTypes() {
            return Collections.singletonList(TEXT_PLAIN);
        }

        @Override
        public Mono<Void> render(@Nullable
        Map<String, ?> model, @Nullable
        MediaType contentType, ServerWebExchange exchange) {
            StringBuilder builder = new StringBuilder();
            builder.append("name=").append(this.name).append('\n');
            for (Map.Entry<String, ?> entry : model.entrySet()) {
                builder.append(entry.getKey()).append('=').append(entry.getValue()).append('\n');
            }
            builder.setLength(((builder.length()) - 1));
            byte[] bytes = builder.toString().getBytes(StandardCharsets.UTF_8);
            ServerHttpResponse response = exchange.getResponse();
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            response.getHeaders().setContentType(TEXT_PLAIN);
            return response.writeWith(Mono.just(buffer));
        }
    }
}

