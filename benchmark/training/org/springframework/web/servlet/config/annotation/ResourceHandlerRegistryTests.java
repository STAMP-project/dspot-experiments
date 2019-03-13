/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.web.servlet.config.annotation;


import HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.resource.AppCacheManifestTransformer;
import org.springframework.web.servlet.resource.CachingResourceResolver;
import org.springframework.web.servlet.resource.CachingResourceTransformer;
import org.springframework.web.servlet.resource.CssLinkResourceTransformer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import org.springframework.web.servlet.resource.WebJarsResourceResolver;


/**
 * Unit tests for {@link ResourceHandlerRegistry}.
 *
 * @author Rossen Stoyanchev
 */
public class ResourceHandlerRegistryTests {
    private ResourceHandlerRegistry registry;

    private ResourceHandlerRegistration registration;

    private MockHttpServletResponse response;

    @Test
    public void noResourceHandlers() throws Exception {
        this.registry = new ResourceHandlerRegistry(new GenericWebApplicationContext(), new MockServletContext());
        Assert.assertNull(this.registry.getHandlerMapping());
    }

    @Test
    public void mapPathToLocation() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "/testStylesheet.css");
        ResourceHttpRequestHandler handler = getHandler("/resources/**");
        handler.handleRequest(request, this.response);
        Assert.assertEquals("test stylesheet content", this.response.getContentAsString());
    }

    @Test
    public void cachePeriod() {
        Assert.assertEquals((-1), getHandler("/resources/**").getCacheSeconds());
        this.registration.setCachePeriod(0);
        Assert.assertEquals(0, getHandler("/resources/**").getCacheSeconds());
    }

    @Test
    public void cacheControl() {
        Assert.assertThat(getHandler("/resources/**").getCacheControl(), Matchers.nullValue());
        this.registration.setCacheControl(CacheControl.noCache().cachePrivate());
        Assert.assertThat(getHandler("/resources/**").getCacheControl().getHeaderValue(), Matchers.equalTo(CacheControl.noCache().cachePrivate().getHeaderValue()));
    }

    @Test
    public void order() {
        Assert.assertEquals(((Integer.MAX_VALUE) - 1), registry.getHandlerMapping().getOrder());
        registry.setOrder(0);
        Assert.assertEquals(0, registry.getHandlerMapping().getOrder());
    }

    @Test
    public void hasMappingForPattern() {
        Assert.assertTrue(this.registry.hasMappingForPattern("/resources/**"));
        Assert.assertFalse(this.registry.hasMappingForPattern("/whatever"));
    }

    @Test
    public void resourceChain() throws Exception {
        ResourceResolver mockResolver = Mockito.mock(ResourceResolver.class);
        ResourceTransformer mockTransformer = Mockito.mock(ResourceTransformer.class);
        this.registration.resourceChain(true).addResolver(mockResolver).addTransformer(mockTransformer);
        ResourceHttpRequestHandler handler = getHandler("/resources/**");
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
    }

    @Test
    public void resourceChainWithoutCaching() throws Exception {
        this.registration.resourceChain(false);
        ResourceHttpRequestHandler handler = getHandler("/resources/**");
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
        ResourceHttpRequestHandler handler = getHandler("/resources/**");
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
        this.registration.setCachePeriod(3600).resourceChain(false).addResolver(cachingResolver).addResolver(versionResolver).addResolver(webjarsResolver).addResolver(pathResourceResolver).addTransformer(cachingTransformer).addTransformer(appCacheTransformer).addTransformer(cssLinkTransformer);
        ResourceHttpRequestHandler handler = getHandler("/resources/**");
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

    @Test
    public void urlResourceWithCharset() throws Exception {
        this.registration.addResourceLocations("[charset=ISO-8859-1]file:///tmp");
        this.registration.resourceChain(true);
        ResourceHttpRequestHandler handler = getHandler("/resources/**");
        UrlResource resource = ((UrlResource) (handler.getLocations().get(1)));
        Assert.assertEquals("file:/tmp", resource.getURL().toString());
        Assert.assertNotNull(handler.getUrlPathHelper());
        List<ResourceResolver> resolvers = handler.getResourceResolvers();
        PathResourceResolver resolver = ((PathResourceResolver) (resolvers.get(((resolvers.size()) - 1))));
        Map<Resource, Charset> locationCharsets = resolver.getLocationCharsets();
        Assert.assertEquals(1, locationCharsets.size());
        Assert.assertEquals(StandardCharsets.ISO_8859_1, locationCharsets.values().iterator().next());
    }
}

