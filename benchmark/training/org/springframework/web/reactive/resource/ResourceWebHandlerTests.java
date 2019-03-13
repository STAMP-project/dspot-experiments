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


import HttpStatus.NOT_MODIFIED;
import HttpStatus.PARTIAL_CONTENT;
import HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE;
import MediaType.TEXT_HTML;
import MediaType.TEXT_PLAIN;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.core.io.buffer.support.DataBufferTestUtils;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.test.MockServerHttpResponse;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.util.StringUtils;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Unit tests for {@link ResourceWebHandler}.
 *
 * @author Rossen Stoyanchev
 */
public class ResourceWebHandlerTests {
    private static final Duration TIMEOUT = Duration.ofSeconds(1);

    private ResourceWebHandler handler;

    private DataBufferFactory bufferFactory = new DefaultDataBufferFactory();

    @Test
    public void getResource() throws Exception {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(""));
        setPathWithinHandlerMapping(exchange, "foo.css");
        this.handler.handle(exchange).block(ResourceWebHandlerTests.TIMEOUT);
        HttpHeaders headers = exchange.getResponse().getHeaders();
        Assert.assertEquals(MediaType.parseMediaType("text/css"), headers.getContentType());
        Assert.assertEquals(17, headers.getContentLength());
        Assert.assertEquals("max-age=3600", headers.getCacheControl());
        Assert.assertTrue(headers.containsKey("Last-Modified"));
        Assert.assertEquals(((headers.getLastModified()) / 1000), ((resourceLastModifiedDate("test/foo.css")) / 1000));
        Assert.assertEquals("bytes", headers.getFirst("Accept-Ranges"));
        Assert.assertEquals(1, headers.get("Accept-Ranges").size());
        assertResponseBody(exchange, "h1 { color:red; }");
    }

    @Test
    public void getResourceHttpHeader() throws Exception {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.head(""));
        setPathWithinHandlerMapping(exchange, "foo.css");
        this.handler.handle(exchange).block(ResourceWebHandlerTests.TIMEOUT);
        Assert.assertNull(exchange.getResponse().getStatusCode());
        HttpHeaders headers = exchange.getResponse().getHeaders();
        Assert.assertEquals(MediaType.parseMediaType("text/css"), headers.getContentType());
        Assert.assertEquals(17, headers.getContentLength());
        Assert.assertEquals("max-age=3600", headers.getCacheControl());
        Assert.assertTrue(headers.containsKey("Last-Modified"));
        Assert.assertEquals(((headers.getLastModified()) / 1000), ((resourceLastModifiedDate("test/foo.css")) / 1000));
        Assert.assertEquals("bytes", headers.getFirst("Accept-Ranges"));
        Assert.assertEquals(1, headers.get("Accept-Ranges").size());
        StepVerifier.create(exchange.getResponse().getBody()).expectErrorMatches(( ex) -> ex.getMessage().startsWith("No content was written")).verify();
    }

    @Test
    public void getResourceHttpOptions() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.options(""));
        setPathWithinHandlerMapping(exchange, "foo.css");
        this.handler.handle(exchange).block(ResourceWebHandlerTests.TIMEOUT);
        Assert.assertNull(exchange.getResponse().getStatusCode());
        Assert.assertEquals("GET,HEAD,OPTIONS", exchange.getResponse().getHeaders().getFirst("Allow"));
    }

    @Test
    public void getResourceNoCache() throws Exception {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(""));
        setPathWithinHandlerMapping(exchange, "foo.css");
        this.handler.setCacheControl(CacheControl.noStore());
        this.handler.handle(exchange).block(ResourceWebHandlerTests.TIMEOUT);
        MockServerHttpResponse response = exchange.getResponse();
        Assert.assertEquals("no-store", response.getHeaders().getCacheControl());
        Assert.assertTrue(response.getHeaders().containsKey("Last-Modified"));
        Assert.assertEquals(((response.getHeaders().getLastModified()) / 1000), ((resourceLastModifiedDate("test/foo.css")) / 1000));
        Assert.assertEquals("bytes", response.getHeaders().getFirst("Accept-Ranges"));
        Assert.assertEquals(1, response.getHeaders().get("Accept-Ranges").size());
    }

    @Test
    public void getVersionedResource() throws Exception {
        VersionResourceResolver versionResolver = new VersionResourceResolver();
        versionResolver.addFixedVersionStrategy("versionString", "/**");
        this.handler.setResourceResolvers(Arrays.asList(versionResolver, new PathResourceResolver()));
        this.handler.afterPropertiesSet();
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(""));
        setPathWithinHandlerMapping(exchange, "versionString/foo.css");
        this.handler.handle(exchange).block(ResourceWebHandlerTests.TIMEOUT);
        Assert.assertEquals("\"versionString\"", exchange.getResponse().getHeaders().getETag());
        Assert.assertEquals("bytes", exchange.getResponse().getHeaders().getFirst("Accept-Ranges"));
        Assert.assertEquals(1, exchange.getResponse().getHeaders().get("Accept-Ranges").size());
    }

    @Test
    public void getResourceWithHtmlMediaType() throws Exception {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(""));
        setPathWithinHandlerMapping(exchange, "foo.html");
        this.handler.handle(exchange).block(ResourceWebHandlerTests.TIMEOUT);
        HttpHeaders headers = exchange.getResponse().getHeaders();
        Assert.assertEquals(TEXT_HTML, headers.getContentType());
        Assert.assertEquals("max-age=3600", headers.getCacheControl());
        Assert.assertTrue(headers.containsKey("Last-Modified"));
        Assert.assertEquals(((headers.getLastModified()) / 1000), ((resourceLastModifiedDate("test/foo.html")) / 1000));
        Assert.assertEquals("bytes", headers.getFirst("Accept-Ranges"));
        Assert.assertEquals(1, headers.get("Accept-Ranges").size());
    }

    @Test
    public void getResourceFromAlternatePath() throws Exception {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(""));
        setPathWithinHandlerMapping(exchange, "baz.css");
        this.handler.handle(exchange).block(ResourceWebHandlerTests.TIMEOUT);
        HttpHeaders headers = exchange.getResponse().getHeaders();
        Assert.assertEquals(MediaType.parseMediaType("text/css"), headers.getContentType());
        Assert.assertEquals(17, headers.getContentLength());
        Assert.assertEquals("max-age=3600", headers.getCacheControl());
        Assert.assertTrue(headers.containsKey("Last-Modified"));
        Assert.assertEquals(((headers.getLastModified()) / 1000), ((resourceLastModifiedDate("testalternatepath/baz.css")) / 1000));
        Assert.assertEquals("bytes", headers.getFirst("Accept-Ranges"));
        Assert.assertEquals(1, headers.get("Accept-Ranges").size());
        assertResponseBody(exchange, "h1 { color:red; }");
    }

    @Test
    public void getResourceFromSubDirectory() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(""));
        setPathWithinHandlerMapping(exchange, "js/foo.js");
        this.handler.handle(exchange).block(ResourceWebHandlerTests.TIMEOUT);
        Assert.assertEquals(MediaType.parseMediaType("application/javascript"), exchange.getResponse().getHeaders().getContentType());
        assertResponseBody(exchange, "function foo() { console.log(\"hello world\"); }");
    }

    @Test
    public void getResourceFromSubDirectoryOfAlternatePath() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(""));
        setPathWithinHandlerMapping(exchange, "js/baz.js");
        this.handler.handle(exchange).block(ResourceWebHandlerTests.TIMEOUT);
        HttpHeaders headers = exchange.getResponse().getHeaders();
        Assert.assertEquals(MediaType.parseMediaType("application/javascript"), headers.getContentType());
        assertResponseBody(exchange, "function foo() { console.log(\"hello world\"); }");
    }

    // SPR-14577
    @Test
    public void getMediaTypeWithFavorPathExtensionOff() throws Exception {
        List<Resource> paths = Collections.singletonList(new ClassPathResource("test/", getClass()));
        ResourceWebHandler handler = new ResourceWebHandler();
        handler.setLocations(paths);
        handler.afterPropertiesSet();
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("").header("Accept", "application/json,text/plain,*/*"));
        setPathWithinHandlerMapping(exchange, "foo.html");
        handler.handle(exchange).block(ResourceWebHandlerTests.TIMEOUT);
        Assert.assertEquals(TEXT_HTML, exchange.getResponse().getHeaders().getContentType());
    }

    @Test
    public void testInvalidPath() throws Exception {
        // Use mock ResourceResolver: i.e. we're only testing upfront validations...
        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(resource.getFilename()).thenThrow(new AssertionError("Resource should not be resolved"));
        Mockito.when(resource.getInputStream()).thenThrow(new AssertionError("Resource should not be resolved"));
        ResourceResolver resolver = Mockito.mock(ResourceResolver.class);
        Mockito.when(resolver.resolveResource(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mono.just(resource));
        ResourceWebHandler handler = new ResourceWebHandler();
        handler.setLocations(Collections.singletonList(new ClassPathResource("test/", getClass())));
        handler.setResourceResolvers(Collections.singletonList(resolver));
        handler.afterPropertiesSet();
        testInvalidPath("../testsecret/secret.txt", handler);
        testInvalidPath("test/../../testsecret/secret.txt", handler);
        testInvalidPath(":/../../testsecret/secret.txt", handler);
        Resource location = new UrlResource(getClass().getResource("./test/"));
        this.handler.setLocations(Collections.singletonList(location));
        Resource secretResource = new UrlResource(getClass().getResource("testsecret/secret.txt"));
        String secretPath = secretResource.getURL().getPath();
        testInvalidPath(("file:" + secretPath), handler);
        testInvalidPath(("/file:" + secretPath), handler);
        testInvalidPath(("url:" + secretPath), handler);
        testInvalidPath(("/url:" + secretPath), handler);
        testInvalidPath(("/../.." + secretPath), handler);
        testInvalidPath("/%2E%2E/testsecret/secret.txt", handler);
        testInvalidPath("/%2E%2E/testsecret/secret.txt", handler);
        testInvalidPath(("%2F%2F%2E%2E%2F%2F%2E%2E" + secretPath), handler);
    }

    @Test
    public void testResolvePathWithTraversal() throws Exception {
        for (HttpMethod method : HttpMethod.values()) {
            testResolvePathWithTraversal(method);
        }
    }

    @Test
    public void processPath() {
        Assert.assertSame("/foo/bar", this.handler.processPath("/foo/bar"));
        Assert.assertSame("foo/bar", this.handler.processPath("foo/bar"));
        // leading whitespace control characters (00-1F)
        Assert.assertEquals("/foo/bar", this.handler.processPath("  /foo/bar"));
        Assert.assertEquals("/foo/bar", this.handler.processPath((((char) (1)) + "/foo/bar")));
        Assert.assertEquals("/foo/bar", this.handler.processPath((((char) (31)) + "/foo/bar")));
        Assert.assertEquals("foo/bar", this.handler.processPath("  foo/bar"));
        Assert.assertEquals("foo/bar", this.handler.processPath((((char) (31)) + "foo/bar")));
        // leading control character 0x7F (DEL)
        Assert.assertEquals("/foo/bar", this.handler.processPath((((char) (127)) + "/foo/bar")));
        Assert.assertEquals("/foo/bar", this.handler.processPath((((char) (127)) + "/foo/bar")));
        // leading control and '/' characters
        Assert.assertEquals("/foo/bar", this.handler.processPath("  /  foo/bar"));
        Assert.assertEquals("/foo/bar", this.handler.processPath("  /  /  foo/bar"));
        Assert.assertEquals("/foo/bar", this.handler.processPath("  // /// ////  foo/bar"));
        Assert.assertEquals("/foo/bar", this.handler.processPath((((((char) (1)) + " / ") + ((char) (127))) + " // foo/bar")));
        // root or empty path
        Assert.assertEquals("", this.handler.processPath("   "));
        Assert.assertEquals("/", this.handler.processPath("/"));
        Assert.assertEquals("/", this.handler.processPath("///"));
        Assert.assertEquals("/", this.handler.processPath("/ /   / "));
    }

    @Test
    public void initAllowedLocations() {
        PathResourceResolver resolver = ((PathResourceResolver) (this.handler.getResourceResolvers().get(0)));
        Resource[] locations = resolver.getAllowedLocations();
        Assert.assertEquals(3, locations.length);
        Assert.assertEquals("test/", getPath());
        Assert.assertEquals("testalternatepath/", getPath());
        Assert.assertEquals("META-INF/resources/webjars/", getPath());
    }

    @Test
    public void initAllowedLocationsWithExplicitConfiguration() throws Exception {
        ClassPathResource location1 = new ClassPathResource("test/", getClass());
        ClassPathResource location2 = new ClassPathResource("testalternatepath/", getClass());
        PathResourceResolver pathResolver = new PathResourceResolver();
        pathResolver.setAllowedLocations(location1);
        ResourceWebHandler handler = new ResourceWebHandler();
        handler.setResourceResolvers(Collections.singletonList(pathResolver));
        handler.setLocations(Arrays.asList(location1, location2));
        handler.afterPropertiesSet();
        Resource[] locations = pathResolver.getAllowedLocations();
        Assert.assertEquals(1, locations.length);
        Assert.assertEquals("test/", getPath());
    }

    @Test
    public void notModified() throws Exception {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("").ifModifiedSince(resourceLastModified("test/foo.css")));
        setPathWithinHandlerMapping(exchange, "foo.css");
        this.handler.handle(exchange).block(ResourceWebHandlerTests.TIMEOUT);
        Assert.assertEquals(NOT_MODIFIED, exchange.getResponse().getStatusCode());
    }

    @Test
    public void modified() throws Exception {
        long timestamp = (((resourceLastModified("test/foo.css")) / 1000) * 1000) - 1;
        MockServerHttpRequest request = MockServerHttpRequest.get("").ifModifiedSince(timestamp).build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        setPathWithinHandlerMapping(exchange, "foo.css");
        this.handler.handle(exchange).block(ResourceWebHandlerTests.TIMEOUT);
        Assert.assertNull(exchange.getResponse().getStatusCode());
        assertResponseBody(exchange, "h1 { color:red; }");
    }

    @Test
    public void directory() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(""));
        setPathWithinHandlerMapping(exchange, "js/");
        StepVerifier.create(this.handler.handle(exchange)).expectErrorSatisfies(( err) -> {
            assertThat(err, instanceOf(.class));
            assertEquals(HttpStatus.NOT_FOUND, ((ResponseStatusException) (err)).getStatus());
        }).verify(ResourceWebHandlerTests.TIMEOUT);
    }

    @Test
    public void directoryInJarFile() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(""));
        setPathWithinHandlerMapping(exchange, "underscorejs/");
        StepVerifier.create(this.handler.handle(exchange)).expectErrorSatisfies(( err) -> {
            assertThat(err, instanceOf(.class));
            assertEquals(HttpStatus.NOT_FOUND, ((ResponseStatusException) (err)).getStatus());
        }).verify(ResourceWebHandlerTests.TIMEOUT);
    }

    @Test
    public void missingResourcePath() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(""));
        setPathWithinHandlerMapping(exchange, "");
        StepVerifier.create(this.handler.handle(exchange)).expectErrorSatisfies(( err) -> {
            assertThat(err, instanceOf(.class));
            assertEquals(HttpStatus.NOT_FOUND, ((ResponseStatusException) (err)).getStatus());
        }).verify(ResourceWebHandlerTests.TIMEOUT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void noPathWithinHandlerMappingAttribute() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(""));
        this.handler.handle(exchange).block(ResourceWebHandlerTests.TIMEOUT);
    }

    @Test(expected = MethodNotAllowedException.class)
    public void unsupportedHttpMethod() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.post(""));
        setPathWithinHandlerMapping(exchange, "foo.css");
        this.handler.handle(exchange).block(ResourceWebHandlerTests.TIMEOUT);
    }

    @Test
    public void resourceNotFound() throws Exception {
        for (HttpMethod method : HttpMethod.values()) {
            resourceNotFound(method);
        }
    }

    @Test
    public void partialContentByteRange() {
        MockServerHttpRequest request = MockServerHttpRequest.get("").header("Range", "bytes=0-1").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        setPathWithinHandlerMapping(exchange, "foo.txt");
        this.handler.handle(exchange).block(ResourceWebHandlerTests.TIMEOUT);
        Assert.assertEquals(PARTIAL_CONTENT, exchange.getResponse().getStatusCode());
        Assert.assertEquals(TEXT_PLAIN, exchange.getResponse().getHeaders().getContentType());
        Assert.assertEquals(2, exchange.getResponse().getHeaders().getContentLength());
        Assert.assertEquals("bytes 0-1/10", exchange.getResponse().getHeaders().getFirst("Content-Range"));
        Assert.assertEquals("bytes", exchange.getResponse().getHeaders().getFirst("Accept-Ranges"));
        Assert.assertEquals(1, exchange.getResponse().getHeaders().get("Accept-Ranges").size());
        assertResponseBody(exchange, "So");
    }

    @Test
    public void partialContentByteRangeNoEnd() {
        MockServerHttpRequest request = MockServerHttpRequest.get("").header("range", "bytes=9-").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        setPathWithinHandlerMapping(exchange, "foo.txt");
        this.handler.handle(exchange).block(ResourceWebHandlerTests.TIMEOUT);
        Assert.assertEquals(PARTIAL_CONTENT, exchange.getResponse().getStatusCode());
        Assert.assertEquals(TEXT_PLAIN, exchange.getResponse().getHeaders().getContentType());
        Assert.assertEquals(1, exchange.getResponse().getHeaders().getContentLength());
        Assert.assertEquals("bytes 9-9/10", exchange.getResponse().getHeaders().getFirst("Content-Range"));
        Assert.assertEquals("bytes", exchange.getResponse().getHeaders().getFirst("Accept-Ranges"));
        Assert.assertEquals(1, exchange.getResponse().getHeaders().get("Accept-Ranges").size());
        assertResponseBody(exchange, ".");
    }

    @Test
    public void partialContentByteRangeLargeEnd() {
        MockServerHttpRequest request = MockServerHttpRequest.get("").header("range", "bytes=9-10000").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        setPathWithinHandlerMapping(exchange, "foo.txt");
        this.handler.handle(exchange).block(ResourceWebHandlerTests.TIMEOUT);
        Assert.assertEquals(PARTIAL_CONTENT, exchange.getResponse().getStatusCode());
        Assert.assertEquals(TEXT_PLAIN, exchange.getResponse().getHeaders().getContentType());
        Assert.assertEquals(1, exchange.getResponse().getHeaders().getContentLength());
        Assert.assertEquals("bytes 9-9/10", exchange.getResponse().getHeaders().getFirst("Content-Range"));
        Assert.assertEquals("bytes", exchange.getResponse().getHeaders().getFirst("Accept-Ranges"));
        Assert.assertEquals(1, exchange.getResponse().getHeaders().get("Accept-Ranges").size());
        assertResponseBody(exchange, ".");
    }

    @Test
    public void partialContentSuffixRange() {
        MockServerHttpRequest request = MockServerHttpRequest.get("").header("range", "bytes=-1").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        setPathWithinHandlerMapping(exchange, "foo.txt");
        this.handler.handle(exchange).block(ResourceWebHandlerTests.TIMEOUT);
        Assert.assertEquals(PARTIAL_CONTENT, exchange.getResponse().getStatusCode());
        Assert.assertEquals(TEXT_PLAIN, exchange.getResponse().getHeaders().getContentType());
        Assert.assertEquals(1, exchange.getResponse().getHeaders().getContentLength());
        Assert.assertEquals("bytes 9-9/10", exchange.getResponse().getHeaders().getFirst("Content-Range"));
        Assert.assertEquals("bytes", exchange.getResponse().getHeaders().getFirst("Accept-Ranges"));
        Assert.assertEquals(1, exchange.getResponse().getHeaders().get("Accept-Ranges").size());
        assertResponseBody(exchange, ".");
    }

    @Test
    public void partialContentSuffixRangeLargeSuffix() {
        MockServerHttpRequest request = MockServerHttpRequest.get("").header("range", "bytes=-11").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        setPathWithinHandlerMapping(exchange, "foo.txt");
        this.handler.handle(exchange).block(ResourceWebHandlerTests.TIMEOUT);
        Assert.assertEquals(PARTIAL_CONTENT, exchange.getResponse().getStatusCode());
        Assert.assertEquals(TEXT_PLAIN, exchange.getResponse().getHeaders().getContentType());
        Assert.assertEquals(10, exchange.getResponse().getHeaders().getContentLength());
        Assert.assertEquals("bytes 0-9/10", exchange.getResponse().getHeaders().getFirst("Content-Range"));
        Assert.assertEquals("bytes", exchange.getResponse().getHeaders().getFirst("Accept-Ranges"));
        Assert.assertEquals(1, exchange.getResponse().getHeaders().get("Accept-Ranges").size());
        assertResponseBody(exchange, "Some text.");
    }

    @Test
    public void partialContentInvalidRangeHeader() {
        MockServerHttpRequest request = MockServerHttpRequest.get("").header("range", "bytes=foo bar").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        setPathWithinHandlerMapping(exchange, "foo.txt");
        StepVerifier.create(this.handler.handle(exchange)).expectNextCount(0).expectComplete().verify();
        Assert.assertEquals(REQUESTED_RANGE_NOT_SATISFIABLE, exchange.getResponse().getStatusCode());
        Assert.assertEquals("bytes", exchange.getResponse().getHeaders().getFirst("Accept-Ranges"));
    }

    @Test
    public void partialContentMultipleByteRanges() {
        MockServerHttpRequest request = MockServerHttpRequest.get("").header("Range", "bytes=0-1, 4-5, 8-9").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        setPathWithinHandlerMapping(exchange, "foo.txt");
        this.handler.handle(exchange).block(ResourceWebHandlerTests.TIMEOUT);
        Assert.assertEquals(PARTIAL_CONTENT, exchange.getResponse().getStatusCode());
        Assert.assertTrue(exchange.getResponse().getHeaders().getContentType().toString().startsWith("multipart/byteranges;boundary="));
        String boundary = "--" + (exchange.getResponse().getHeaders().getContentType().toString().substring(30));
        Mono<DataBuffer> reduced = Flux.from(exchange.getResponse().getBody()).reduce(this.bufferFactory.allocateBuffer(), ( previous, current) -> {
            previous.write(current);
            DataBufferUtils.release(current);
            return previous;
        });
        StepVerifier.create(reduced).consumeNextWith(( buf) -> {
            String content = DataBufferTestUtils.dumpString(buf, StandardCharsets.UTF_8);
            String[] ranges = StringUtils.tokenizeToStringArray(content, "\r\n", false, true);
            assertEquals(boundary, ranges[0]);
            assertEquals("Content-Type: text/plain", ranges[1]);
            assertEquals("Content-Range: bytes 0-1/10", ranges[2]);
            assertEquals("So", ranges[3]);
            assertEquals(boundary, ranges[4]);
            assertEquals("Content-Type: text/plain", ranges[5]);
            assertEquals("Content-Range: bytes 4-5/10", ranges[6]);
            assertEquals(" t", ranges[7]);
            assertEquals(boundary, ranges[8]);
            assertEquals("Content-Type: text/plain", ranges[9]);
            assertEquals("Content-Range: bytes 8-9/10", ranges[10]);
            assertEquals("t.", ranges[11]);
        }).expectComplete().verify();
    }

    // SPR-14005
    @Test
    public void doOverwriteExistingCacheControlHeaders() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(""));
        exchange.getResponse().getHeaders().setCacheControl(CacheControl.noStore().getHeaderValue());
        setPathWithinHandlerMapping(exchange, "foo.css");
        this.handler.handle(exchange).block(ResourceWebHandlerTests.TIMEOUT);
        Assert.assertEquals("max-age=3600", exchange.getResponse().getHeaders().getCacheControl());
    }
}

