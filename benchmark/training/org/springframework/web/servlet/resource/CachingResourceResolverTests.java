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
package org.springframework.web.servlet.resource;


import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.cache.Cache;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.test.MockHttpServletRequest;

import static CachingResourceResolver.RESOLVED_URL_PATH_CACHE_KEY_PREFIX;


/**
 * Unit tests for
 * {@link org.springframework.web.servlet.resource.CachingResourceResolver}.
 *
 * @author Rossen Stoyanchev
 */
public class CachingResourceResolverTests {
    private Cache cache;

    private ResourceResolverChain chain;

    private List<Resource> locations;

    @Test
    public void resolveResourceInternal() {
        Resource expected = new ClassPathResource("test/bar.css", getClass());
        Resource actual = this.chain.resolveResource(null, "bar.css", this.locations);
        Assert.assertNotSame(expected, actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void resolveResourceInternalFromCache() {
        Resource expected = Mockito.mock(Resource.class);
        this.cache.put(CachingResourceResolverTests.resourceKey("bar.css"), expected);
        Resource actual = this.chain.resolveResource(null, "bar.css", this.locations);
        Assert.assertSame(expected, actual);
    }

    @Test
    public void resolveResourceInternalNoMatch() {
        Assert.assertNull(this.chain.resolveResource(null, "invalid.css", this.locations));
    }

    @Test
    public void resolverUrlPath() {
        String expected = "/foo.css";
        String actual = this.chain.resolveUrlPath(expected, this.locations);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void resolverUrlPathFromCache() {
        String expected = "cached-imaginary.css";
        this.cache.put(((RESOLVED_URL_PATH_CACHE_KEY_PREFIX) + "imaginary.css"), expected);
        String actual = this.chain.resolveUrlPath("imaginary.css", this.locations);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void resolverUrlPathNoMatch() {
        Assert.assertNull(this.chain.resolveUrlPath("invalid.css", this.locations));
    }

    @Test
    public void resolveResourceAcceptEncodingInCacheKey() throws IOException {
        String file = "bar.css";
        EncodedResourceResolverTests.createGzippedFile(file);
        // 1. Resolve plain resource
        MockHttpServletRequest request = new MockHttpServletRequest("GET", file);
        Resource expected = this.chain.resolveResource(request, file, this.locations);
        String cacheKey = CachingResourceResolverTests.resourceKey(file);
        Assert.assertSame(expected, this.cache.get(cacheKey).get());
        // 2. Resolve with Accept-Encoding
        request = new MockHttpServletRequest("GET", file);
        request.addHeader("Accept-Encoding", "gzip ; a=b  , deflate ,  br  ; c=d ");
        expected = this.chain.resolveResource(request, file, this.locations);
        cacheKey = CachingResourceResolverTests.resourceKey((file + "+encoding=br,gzip"));
        Assert.assertSame(expected, this.cache.get(cacheKey).get());
        // 3. Resolve with Accept-Encoding but no matching codings
        request = new MockHttpServletRequest("GET", file);
        request.addHeader("Accept-Encoding", "deflate");
        expected = this.chain.resolveResource(request, file, this.locations);
        cacheKey = CachingResourceResolverTests.resourceKey(file);
        Assert.assertSame(expected, this.cache.get(cacheKey).get());
    }

    @Test
    public void resolveResourceNoAcceptEncoding() {
        String file = "bar.css";
        MockHttpServletRequest request = new MockHttpServletRequest("GET", file);
        Resource expected = this.chain.resolveResource(request, file, this.locations);
        String cacheKey = CachingResourceResolverTests.resourceKey(file);
        Object actual = this.cache.get(cacheKey).get();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void resolveResourceMatchingEncoding() {
        Resource resource = Mockito.mock(Resource.class);
        Resource gzipped = Mockito.mock(Resource.class);
        this.cache.put(CachingResourceResolverTests.resourceKey("bar.css"), resource);
        this.cache.put(CachingResourceResolverTests.resourceKey("bar.css+encoding=gzip"), gzipped);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "bar.css");
        Assert.assertSame(resource, this.chain.resolveResource(request, "bar.css", this.locations));
        request = new MockHttpServletRequest("GET", "bar.css");
        request.addHeader("Accept-Encoding", "gzip");
        Assert.assertSame(gzipped, this.chain.resolveResource(request, "bar.css", this.locations));
    }
}

