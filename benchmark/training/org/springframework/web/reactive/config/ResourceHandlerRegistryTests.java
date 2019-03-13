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
package org.springframework.web.reactive.config;


import HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE;
import java.time.Duration;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.buffer.support.DataBufferTestUtils;
import org.springframework.http.CacheControl;
import org.springframework.http.server.PathContainer;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.reactive.resource.AppCacheManifestTransformer;
import org.springframework.web.reactive.resource.CachingResourceResolver;
import org.springframework.web.reactive.resource.CachingResourceTransformer;
import org.springframework.web.reactive.resource.CssLinkResourceTransformer;
import org.springframework.web.reactive.resource.PathResourceResolver;
import org.springframework.web.reactive.resource.ResourceResolver;
import org.springframework.web.reactive.resource.ResourceTransformer;
import org.springframework.web.reactive.resource.ResourceTransformerSupport;
import org.springframework.web.reactive.resource.ResourceUrlProvider;
import org.springframework.web.reactive.resource.ResourceWebHandler;
import org.springframework.web.reactive.resource.VersionResourceResolver;
import org.springframework.web.reactive.resource.WebJarsResourceResolver;
import reactor.test.StepVerifier;

import static java.util.concurrent.TimeUnit.MILLISECONDS;


/**
 * Unit tests for {@link ResourceHandlerRegistry}.
 *
 * @author Rossen Stoyanchev
 */
public class ResourceHandlerRegistryTests {
    private ResourceHandlerRegistry registry;

    private ResourceHandlerRegistration registration;

    @Test
    public void noResourceHandlers() throws Exception {
        this.registry = new ResourceHandlerRegistry(new GenericApplicationContext());
        Assert.assertNull(this.registry.getHandlerMapping());
    }

    @Test
    public void mapPathToLocation() throws Exception {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(""));
        exchange.getAttributes().put(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, PathContainer.parsePath("/testStylesheet.css"));
        ResourceWebHandler handler = getHandler("/resources/**");
        handler.handle(exchange).block(Duration.ofSeconds(5));
        StepVerifier.create(exchange.getResponse().getBody()).consumeNextWith(( buf) -> assertEquals("test stylesheet content", DataBufferTestUtils.dumpString(buf, StandardCharsets.UTF_8))).expectComplete().verify();
    }

    @Test
    public void cacheControl() {
        Assert.assertThat(getHandler("/resources/**").getCacheControl(), Matchers.nullValue());
        this.registration.setCacheControl(CacheControl.noCache().cachePrivate());
        Assert.assertThat(getHandler("/resources/**").getCacheControl().getHeaderValue(), Matchers.equalTo(CacheControl.noCache().cachePrivate().getHeaderValue()));
    }

    @Test
    public void order() {
        Assert.assertEquals(((Integer.MAX_VALUE) - 1), this.registry.getHandlerMapping().getOrder());
        this.registry.setOrder(0);
        Assert.assertEquals(0, this.registry.getHandlerMapping().getOrder());
    }

    @Test
    public void hasMappingForPattern() {
        Assert.assertTrue(this.registry.hasMappingForPattern("/resources/**"));
        Assert.assertFalse(this.registry.hasMappingForPattern("/whatever"));
    }

    @Test
    public void resourceChain() throws Exception {
        ResourceUrlProvider resourceUrlProvider = Mockito.mock(ResourceUrlProvider.class);
        this.registry.setResourceUrlProvider(resourceUrlProvider);
        ResourceResolver mockResolver = Mockito.mock(ResourceResolver.class);
        ResourceTransformerSupport mockTransformer = Mockito.mock(ResourceTransformerSupport.class);
        this.registration.resourceChain(true).addResolver(mockResolver).addTransformer(mockTransformer);
        ResourceWebHandler handler = getHandler("/resources/**");
        List<ResourceResolver> resolvers = handler.getResourceResolvers();
        Assert.assertThat(resolvers.toString(), resolvers, Matchers.hasSize(4));
        Assert.assertThat(resolvers.get(0), Matchers.instanceOf(CachingResourceResolver.class));
        CachingResourceResolver cachingResolver = ((CachingResourceResolver) (resolvers.get(0)));
        Assert.assertThat(cachingResolver.getCache(), Matchers.instanceOf(ConcurrentMapCache.class));
        Assert.assertThat(resolvers.get(1), Matchers.equalTo(mockResolver));
        Assert.assertThat(resolvers.get(2), Matchers.instanceOf(WebJarsResourceResolver.class));
        Assert.assertThat(resolvers.get(3), Matchers.instanceOf(PathResourceResolver.class));
        List<ResourceTransformer> transformers = handler.getResourceTransformers();
        Assert.assertThat(transformers, Matchers.hasSize(2));
        Assert.assertThat(transformers.get(0), Matchers.instanceOf(CachingResourceTransformer.class));
        Assert.assertThat(transformers.get(1), Matchers.equalTo(mockTransformer));
        Mockito.verify(mockTransformer).setResourceUrlProvider(resourceUrlProvider);
    }

    @Test
    public void resourceChainWithoutCaching() throws Exception {
        this.registration.resourceChain(false);
        ResourceWebHandler handler = getHandler("/resources/**");
        List<ResourceResolver> resolvers = handler.getResourceResolvers();
        Assert.assertThat(resolvers, Matchers.hasSize(2));
        Assert.assertThat(resolvers.get(0), Matchers.instanceOf(WebJarsResourceResolver.class));
        Assert.assertThat(resolvers.get(1), Matchers.instanceOf(PathResourceResolver.class));
        List<ResourceTransformer> transformers = handler.getResourceTransformers();
        Assert.assertThat(transformers, Matchers.hasSize(0));
    }

    @Test
    public void resourceChainWithVersionResolver() throws Exception {
        VersionResourceResolver versionResolver = new VersionResourceResolver().addFixedVersionStrategy("fixed", "/**/*.js").addContentVersionStrategy("/**");
        this.registration.resourceChain(true).addResolver(versionResolver).addTransformer(new AppCacheManifestTransformer());
        ResourceWebHandler handler = getHandler("/resources/**");
        List<ResourceResolver> resolvers = handler.getResourceResolvers();
        Assert.assertThat(resolvers.toString(), resolvers, Matchers.hasSize(4));
        Assert.assertThat(resolvers.get(0), Matchers.instanceOf(CachingResourceResolver.class));
        Assert.assertThat(resolvers.get(1), Matchers.sameInstance(versionResolver));
        Assert.assertThat(resolvers.get(2), Matchers.instanceOf(WebJarsResourceResolver.class));
        Assert.assertThat(resolvers.get(3), Matchers.instanceOf(PathResourceResolver.class));
        List<ResourceTransformer> transformers = handler.getResourceTransformers();
        Assert.assertThat(transformers, Matchers.hasSize(3));
        Assert.assertThat(transformers.get(0), Matchers.instanceOf(CachingResourceTransformer.class));
        Assert.assertThat(transformers.get(1), Matchers.instanceOf(CssLinkResourceTransformer.class));
        Assert.assertThat(transformers.get(2), Matchers.instanceOf(AppCacheManifestTransformer.class));
    }

    @Test
    public void resourceChainWithOverrides() throws Exception {
        CachingResourceResolver cachingResolver = Mockito.mock(CachingResourceResolver.class);
        VersionResourceResolver versionResolver = Mockito.mock(VersionResourceResolver.class);
        WebJarsResourceResolver webjarsResolver = Mockito.mock(WebJarsResourceResolver.class);
        PathResourceResolver pathResourceResolver = new PathResourceResolver();
        CachingResourceTransformer cachingTransformer = Mockito.mock(CachingResourceTransformer.class);
        AppCacheManifestTransformer appCacheTransformer = Mockito.mock(AppCacheManifestTransformer.class);
        CssLinkResourceTransformer cssLinkTransformer = new CssLinkResourceTransformer();
        this.registration.setCacheControl(CacheControl.maxAge(3600, MILLISECONDS)).resourceChain(false).addResolver(cachingResolver).addResolver(versionResolver).addResolver(webjarsResolver).addResolver(pathResourceResolver).addTransformer(cachingTransformer).addTransformer(appCacheTransformer).addTransformer(cssLinkTransformer);
        ResourceWebHandler handler = getHandler("/resources/**");
        List<ResourceResolver> resolvers = handler.getResourceResolvers();
        Assert.assertThat(resolvers.toString(), resolvers, Matchers.hasSize(4));
        Assert.assertThat(resolvers.get(0), Matchers.sameInstance(cachingResolver));
        Assert.assertThat(resolvers.get(1), Matchers.sameInstance(versionResolver));
        Assert.assertThat(resolvers.get(2), Matchers.sameInstance(webjarsResolver));
        Assert.assertThat(resolvers.get(3), Matchers.sameInstance(pathResourceResolver));
        List<ResourceTransformer> transformers = handler.getResourceTransformers();
        Assert.assertThat(transformers, Matchers.hasSize(3));
        Assert.assertThat(transformers.get(0), Matchers.sameInstance(cachingTransformer));
        Assert.assertThat(transformers.get(1), Matchers.sameInstance(appCacheTransformer));
        Assert.assertThat(transformers.get(2), Matchers.sameInstance(cssLinkTransformer));
    }
}

