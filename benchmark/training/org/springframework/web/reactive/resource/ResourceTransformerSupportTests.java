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
package org.springframework.web.reactive.resource;


import java.time.Duration;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * Unit tests for {@code ResourceTransformerSupport}.
 *
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 */
public class ResourceTransformerSupportTests {
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private ResourceTransformerChain chain;

    private ResourceTransformerSupportTests.TestResourceTransformerSupport transformer;

    @Test
    public void resolveUrlPath() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/resources/main.css"));
        String resourcePath = "/resources/bar.css";
        Resource resource = getResource("main.css");
        String actual = this.transformer.resolveUrlPath(resourcePath, exchange, resource, this.chain).block(ResourceTransformerSupportTests.TIMEOUT);
        Assert.assertEquals("/resources/bar-11e16cf79faee7ac698c805cf28248d2.css", actual);
        Assert.assertEquals("/resources/bar-11e16cf79faee7ac698c805cf28248d2.css", actual);
    }

    @Test
    public void resolveUrlPathWithRelativePath() {
        Resource resource = getResource("main.css");
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(""));
        String actual = this.transformer.resolveUrlPath("bar.css", exchange, resource, this.chain).block(ResourceTransformerSupportTests.TIMEOUT);
        Assert.assertEquals("bar-11e16cf79faee7ac698c805cf28248d2.css", actual);
    }

    @Test
    public void resolveUrlPathWithRelativePathInParentDirectory() {
        Resource resource = getResource("images/image.png");
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(""));
        String actual = this.transformer.resolveUrlPath("../bar.css", exchange, resource, this.chain).block(ResourceTransformerSupportTests.TIMEOUT);
        Assert.assertEquals("../bar-11e16cf79faee7ac698c805cf28248d2.css", actual);
    }

    @Test
    public void toAbsolutePath() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/resources/main.css"));
        String absolute = this.transformer.toAbsolutePath("img/image.png", exchange);
        Assert.assertEquals("/resources/img/image.png", absolute);
        absolute = this.transformer.toAbsolutePath("/img/image.png", exchange);
        Assert.assertEquals("/img/image.png", absolute);
    }

    private static class TestResourceTransformerSupport extends ResourceTransformerSupport {
        @Override
        public Mono<Resource> transform(ServerWebExchange ex, Resource res, ResourceTransformerChain chain) {
            return Mono.error(new IllegalStateException("Should never be called"));
        }
    }
}

