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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * Unit tests for {@link VersionResourceResolver}.
 *
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 */
public class VersionResourceResolverTests {
    private List<Resource> locations;

    private VersionResourceResolver resolver;

    private ResourceResolverChain chain;

    private VersionStrategy versionStrategy;

    @Test
    public void resolveResourceExisting() {
        String file = "bar.css";
        Resource expected = new ClassPathResource(("test/" + file), getClass());
        BDDMockito.given(this.chain.resolveResource(null, file, this.locations)).willReturn(Mono.just(expected));
        this.resolver.setStrategyMap(Collections.singletonMap("/**", this.versionStrategy));
        Resource actual = this.resolver.resolveResourceInternal(null, file, this.locations, this.chain).block(Duration.ofMillis(5000));
        Assert.assertEquals(expected, actual);
        Mockito.verify(this.chain, Mockito.times(1)).resolveResource(null, file, this.locations);
        Mockito.verify(this.versionStrategy, Mockito.never()).extractVersion(file);
    }

    @Test
    public void resolveResourceNoVersionStrategy() {
        String file = "missing.css";
        BDDMockito.given(this.chain.resolveResource(null, file, this.locations)).willReturn(Mono.empty());
        this.resolver.setStrategyMap(Collections.emptyMap());
        Resource actual = this.resolver.resolveResourceInternal(null, file, this.locations, this.chain).block(Duration.ofMillis(5000));
        Assert.assertNull(actual);
        Mockito.verify(this.chain, Mockito.times(1)).resolveResource(null, file, this.locations);
    }

    @Test
    public void resolveResourceNoVersionInPath() {
        String file = "bar.css";
        BDDMockito.given(this.chain.resolveResource(null, file, this.locations)).willReturn(Mono.empty());
        BDDMockito.given(this.versionStrategy.extractVersion(file)).willReturn("");
        this.resolver.setStrategyMap(Collections.singletonMap("/**", this.versionStrategy));
        Resource actual = this.resolver.resolveResourceInternal(null, file, this.locations, this.chain).block(Duration.ofMillis(5000));
        Assert.assertNull(actual);
        Mockito.verify(this.chain, Mockito.times(1)).resolveResource(null, file, this.locations);
        Mockito.verify(this.versionStrategy, Mockito.times(1)).extractVersion(file);
    }

    @Test
    public void resolveResourceNoResourceAfterVersionRemoved() {
        String versionFile = "bar-version.css";
        String version = "version";
        String file = "bar.css";
        BDDMockito.given(this.chain.resolveResource(null, versionFile, this.locations)).willReturn(Mono.empty());
        BDDMockito.given(this.chain.resolveResource(null, file, this.locations)).willReturn(Mono.empty());
        BDDMockito.given(this.versionStrategy.extractVersion(versionFile)).willReturn(version);
        BDDMockito.given(this.versionStrategy.removeVersion(versionFile, version)).willReturn(file);
        this.resolver.setStrategyMap(Collections.singletonMap("/**", this.versionStrategy));
        Resource actual = this.resolver.resolveResourceInternal(null, versionFile, this.locations, this.chain).block(Duration.ofMillis(5000));
        Assert.assertNull(actual);
        Mockito.verify(this.versionStrategy, Mockito.times(1)).removeVersion(versionFile, version);
    }

    @Test
    public void resolveResourceVersionDoesNotMatch() {
        String versionFile = "bar-version.css";
        String version = "version";
        String file = "bar.css";
        Resource expected = new ClassPathResource(("test/" + file), getClass());
        BDDMockito.given(this.chain.resolveResource(null, versionFile, this.locations)).willReturn(Mono.empty());
        BDDMockito.given(this.chain.resolveResource(null, file, this.locations)).willReturn(Mono.just(expected));
        BDDMockito.given(this.versionStrategy.extractVersion(versionFile)).willReturn(version);
        BDDMockito.given(this.versionStrategy.removeVersion(versionFile, version)).willReturn(file);
        BDDMockito.given(this.versionStrategy.getResourceVersion(expected)).willReturn(Mono.just("newer-version"));
        this.resolver.setStrategyMap(Collections.singletonMap("/**", this.versionStrategy));
        Resource actual = this.resolver.resolveResourceInternal(null, versionFile, this.locations, this.chain).block(Duration.ofMillis(5000));
        Assert.assertNull(actual);
        Mockito.verify(this.versionStrategy, Mockito.times(1)).getResourceVersion(expected);
    }

    @Test
    public void resolveResourceSuccess() {
        String versionFile = "bar-version.css";
        String version = "version";
        String file = "bar.css";
        Resource expected = new ClassPathResource(("test/" + file), getClass());
        MockServerHttpRequest request = MockServerHttpRequest.get("/resources/bar-version.css").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        BDDMockito.given(this.chain.resolveResource(exchange, versionFile, this.locations)).willReturn(Mono.empty());
        BDDMockito.given(this.chain.resolveResource(exchange, file, this.locations)).willReturn(Mono.just(expected));
        BDDMockito.given(this.versionStrategy.extractVersion(versionFile)).willReturn(version);
        BDDMockito.given(this.versionStrategy.removeVersion(versionFile, version)).willReturn(file);
        BDDMockito.given(this.versionStrategy.getResourceVersion(expected)).willReturn(Mono.just(version));
        this.resolver.setStrategyMap(Collections.singletonMap("/**", this.versionStrategy));
        Resource actual = this.resolver.resolveResourceInternal(exchange, versionFile, this.locations, this.chain).block(Duration.ofMillis(5000));
        Assert.assertEquals(expected.getFilename(), actual.getFilename());
        Mockito.verify(this.versionStrategy, Mockito.times(1)).getResourceVersion(expected);
        Assert.assertThat(actual, instanceOf(HttpResource.class));
        Assert.assertEquals((("\"" + version) + "\""), getResponseHeaders().getETag());
    }

    @Test
    public void getStrategyForPath() {
        Map<String, VersionStrategy> strategies = new HashMap<>();
        VersionStrategy jsStrategy = Mockito.mock(VersionStrategy.class);
        VersionStrategy catchAllStrategy = Mockito.mock(VersionStrategy.class);
        strategies.put("/**", catchAllStrategy);
        strategies.put("/**/*.js", jsStrategy);
        this.resolver.setStrategyMap(strategies);
        Assert.assertEquals(catchAllStrategy, this.resolver.getStrategyForPath("foo.css"));
        Assert.assertEquals(catchAllStrategy, this.resolver.getStrategyForPath("foo-js.css"));
        Assert.assertEquals(jsStrategy, this.resolver.getStrategyForPath("foo.js"));
        Assert.assertEquals(jsStrategy, this.resolver.getStrategyForPath("bar/foo.js"));
    }

    // SPR-13883
    @Test
    public void shouldConfigureFixedPrefixAutomatically() {
        this.resolver.addFixedVersionStrategy("fixedversion", "/js/**", "/css/**", "/fixedversion/css/**");
        Assert.assertThat(this.resolver.getStrategyMap().size(), is(4));
        Assert.assertThat(this.resolver.getStrategyForPath("js/something.js"), Matchers.instanceOf(FixedVersionStrategy.class));
        Assert.assertThat(this.resolver.getStrategyForPath("fixedversion/js/something.js"), Matchers.instanceOf(FixedVersionStrategy.class));
        Assert.assertThat(this.resolver.getStrategyForPath("css/something.css"), Matchers.instanceOf(FixedVersionStrategy.class));
        Assert.assertThat(this.resolver.getStrategyForPath("fixedversion/css/something.css"), Matchers.instanceOf(FixedVersionStrategy.class));
    }

    // SPR-15372
    @Test
    public void resolveUrlPathNoVersionStrategy() {
        BDDMockito.given(this.chain.resolveUrlPath("/foo.css", this.locations)).willReturn(Mono.just("/foo.css"));
        String resolved = this.resolver.resolveUrlPathInternal("/foo.css", this.locations, this.chain).block(Duration.ofMillis(1000));
        Assert.assertThat(resolved, is("/foo.css"));
    }
}

