/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
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
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.util.FileCopyUtils;


/**
 * Unit tests for {@link AppCacheManifestTransformer}.
 *
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 */
public class AppCacheManifestTransformerTests {
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private AppCacheManifestTransformer transformer;

    private ResourceTransformerChain chain;

    @Test
    public void noTransformIfExtensionDoesNotMatch() {
        MockServerWebExchange exchange = MockServerWebExchange.from(get("/static/foo.css"));
        Resource expected = getResource("foo.css");
        Resource actual = this.transformer.transform(exchange, expected, this.chain).block(AppCacheManifestTransformerTests.TIMEOUT);
        Assert.assertSame(expected, actual);
    }

    @Test
    public void syntaxErrorInManifest() {
        MockServerWebExchange exchange = MockServerWebExchange.from(get("/static/error.appcache"));
        Resource expected = getResource("error.appcache");
        Resource actual = this.transformer.transform(exchange, expected, this.chain).block(AppCacheManifestTransformerTests.TIMEOUT);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void transformManifest() throws Exception {
        MockServerWebExchange exchange = MockServerWebExchange.from(get("/static/test.appcache"));
        Resource resource = getResource("test.appcache");
        Resource actual = this.transformer.transform(exchange, resource, this.chain).block(AppCacheManifestTransformerTests.TIMEOUT);
        Assert.assertNotNull(actual);
        byte[] bytes = FileCopyUtils.copyToByteArray(actual.getInputStream());
        String content = new String(bytes, "UTF-8");
        Assert.assertThat("should rewrite resource links", content, containsString("/static/foo-e36d2e05253c6c7085a91522ce43a0b4.css"));
        Assert.assertThat("should rewrite resource links", content, containsString("/static/bar-11e16cf79faee7ac698c805cf28248d2.css"));
        Assert.assertThat("should rewrite resource links", content, containsString("/static/js/bar-bd508c62235b832d960298ca6c0b7645.js"));
        Assert.assertThat("should not rewrite external resources", content, containsString("//example.org/style.css"));
        Assert.assertThat("should not rewrite external resources", content, containsString("http://example.org/image.png"));
        // Not the same hash as Spring MVC
        // Hash is computed from links, and not from the linked content
        Assert.assertThat("should generate fingerprint", content, containsString("# Hash: 8eefc904df3bd46537fa7bdbbc5ab9fb"));
    }
}

