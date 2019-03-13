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


import HttpHeaders.CONTENT_ENCODING;
import HttpHeaders.VARY;
import java.time.Duration;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;


/**
 * Unit tests for {@link EncodedResourceResolver}.
 *
 * @author Rossen Stoyanchev
 */
public class EncodedResourceResolverTests {
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private ResourceResolverChain resolver;

    private List<Resource> locations;

    @Test
    public void resolveGzipped() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("").header("Accept-Encoding", "gzip"));
        String file = "js/foo.js";
        Resource actual = this.resolver.resolveResource(exchange, file, this.locations).block(EncodedResourceResolverTests.TIMEOUT);
        Assert.assertEquals(getResource((file + ".gz")).getDescription(), actual.getDescription());
        Assert.assertEquals(getResource(file).getFilename(), actual.getFilename());
        Assert.assertTrue((actual instanceof HttpResource));
        HttpHeaders headers = getResponseHeaders();
        Assert.assertEquals("gzip", headers.getFirst(CONTENT_ENCODING));
        Assert.assertEquals("Accept-Encoding", headers.getFirst(VARY));
    }

    @Test
    public void resolveGzippedWithVersion() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("").header("Accept-Encoding", "gzip"));
        String file = "foo-e36d2e05253c6c7085a91522ce43a0b4.css";
        Resource actual = this.resolver.resolveResource(exchange, file, this.locations).block(EncodedResourceResolverTests.TIMEOUT);
        Assert.assertEquals(getResource("foo.css.gz").getDescription(), actual.getDescription());
        Assert.assertEquals(getResource("foo.css").getFilename(), actual.getFilename());
        Assert.assertTrue((actual instanceof HttpResource));
    }

    @Test
    public void resolveFromCacheWithEncodingVariants() {
        // 1. Resolve, and cache .gz variant
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("").header("Accept-Encoding", "gzip"));
        String file = "js/foo.js";
        Resource resolved = this.resolver.resolveResource(exchange, file, this.locations).block(EncodedResourceResolverTests.TIMEOUT);
        Assert.assertEquals(getResource((file + ".gz")).getDescription(), resolved.getDescription());
        Assert.assertEquals(getResource(file).getFilename(), resolved.getFilename());
        Assert.assertTrue((resolved instanceof HttpResource));
        // 2. Resolve unencoded resource
        exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/js/foo.js"));
        resolved = this.resolver.resolveResource(exchange, file, this.locations).block(EncodedResourceResolverTests.TIMEOUT);
        Assert.assertEquals(getResource(file).getDescription(), resolved.getDescription());
        Assert.assertEquals(getResource(file).getFilename(), resolved.getFilename());
        Assert.assertFalse((resolved instanceof HttpResource));
    }

    // SPR-13149
    @Test
    public void resolveWithNullRequest() {
        String file = "js/foo.js";
        Resource resolved = this.resolver.resolveResource(null, file, this.locations).block(EncodedResourceResolverTests.TIMEOUT);
        Assert.assertEquals(getResource(file).getDescription(), resolved.getDescription());
        Assert.assertEquals(getResource(file).getFilename(), resolved.getFilename());
    }
}

