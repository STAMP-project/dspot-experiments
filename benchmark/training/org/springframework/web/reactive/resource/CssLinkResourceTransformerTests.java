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


import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.resource.EncodedResourceResolver.EncodedResource;
import reactor.test.StepVerifier;


/**
 * Unit tests for {@link CssLinkResourceTransformer}.
 *
 * @author Rossen Stoyanchev
 */
public class CssLinkResourceTransformerTests {
    private ResourceTransformerChain transformerChain;

    @Test
    public void transform() {
        MockServerWebExchange exchange = MockServerWebExchange.from(get("/static/main.css"));
        Resource css = getResource("main.css");
        String expected = "\n" + ((((("@import url(\"/static/bar-11e16cf79faee7ac698c805cf28248d2.css?#iefix\");\n" + "@import url(\'/static/bar-11e16cf79faee7ac698c805cf28248d2.css#bla-normal\');\n") + "@import url(/static/bar-11e16cf79faee7ac698c805cf28248d2.css);\n\n") + "@import \"/static/foo-e36d2e05253c6c7085a91522ce43a0b4.css\";\n") + "@import \'/static/foo-e36d2e05253c6c7085a91522ce43a0b4.css\';\n\n") + "body { background: url(\"/static/images/image-f448cd1d5dba82b774f3202c878230b3.png?#iefix\") }\n");
        StepVerifier.create(this.transformerChain.transform(exchange, css).cast(TransformedResource.class)).consumeNextWith(( transformedResource) -> {
            String result = new String(transformedResource.getByteArray(), StandardCharsets.UTF_8);
            result = StringUtils.deleteAny(result, "\r");
            assertEquals(expected, result);
        }).expectComplete().verify();
    }

    @Test
    public void transformNoLinks() {
        MockServerWebExchange exchange = MockServerWebExchange.from(get("/static/foo.css"));
        Resource expected = getResource("foo.css");
        StepVerifier.create(this.transformerChain.transform(exchange, expected)).consumeNextWith(( resource) -> assertSame(expected, resource)).expectComplete().verify();
    }

    @Test
    public void transformExtLinksNotAllowed() {
        MockServerWebExchange exchange = MockServerWebExchange.from(get("/static/external.css"));
        List<ResourceTransformer> transformers = Collections.singletonList(new CssLinkResourceTransformer());
        ResourceResolverChain mockChain = Mockito.mock(DefaultResourceResolverChain.class);
        ResourceTransformerChain chain = new DefaultResourceTransformerChain(mockChain, transformers);
        Resource resource = getResource("external.css");
        String expected = "@import url(\"http://example.org/fonts/css\");\n" + ("body { background: url(\"file:///home/spring/image.png\") }\n" + "figure { background: url(\"//example.org/style.css\")}");
        StepVerifier.create(chain.transform(exchange, resource).cast(TransformedResource.class)).consumeNextWith(( transformedResource) -> {
            String result = new String(transformedResource.getByteArray(), StandardCharsets.UTF_8);
            result = StringUtils.deleteAny(result, "\r");
            assertEquals(expected, result);
        }).expectComplete().verify();
        List<Resource> locations = Collections.singletonList(resource);
        Mockito.verify(mockChain, Mockito.never()).resolveUrlPath("http://example.org/fonts/css", locations);
        Mockito.verify(mockChain, Mockito.never()).resolveUrlPath("file:///home/spring/image.png", locations);
        Mockito.verify(mockChain, Mockito.never()).resolveUrlPath("//example.org/style.css", locations);
    }

    @Test
    public void transformSkippedForNonCssResource() {
        MockServerWebExchange exchange = MockServerWebExchange.from(get("/static/images/image.png"));
        Resource expected = getResource("images/image.png");
        StepVerifier.create(this.transformerChain.transform(exchange, expected)).expectNext(expected).expectComplete().verify();
    }

    @Test
    public void transformSkippedForGzippedResource() throws Exception {
        EncodedResourceResolverTests.createGzippedFile("main.css");
        MockServerWebExchange exchange = MockServerWebExchange.from(get("/static/main.css"));
        Resource resource = getResource("main.css");
        EncodedResource gzipped = new EncodedResource(resource, "gzip", ".gz");
        StepVerifier.create(this.transformerChain.transform(exchange, gzipped)).expectNext(gzipped).expectComplete().verify();
    }
}

