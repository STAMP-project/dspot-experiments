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


import HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE;
import HttpServletResponse.SC_NOT_MODIFIED;
import HttpServletResponse.SC_OK;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;


/**
 * Unit tests for {@link ResourceHttpRequestHandler}.
 *
 * @author Keith Donald
 * @author Jeremy Grelle
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 */
public class ResourceHttpRequestHandlerTests {
    private ResourceHttpRequestHandler handler;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    @Test
    public void getResource() throws Exception {
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "foo.css");
        this.handler.handleRequest(this.request, this.response);
        Assert.assertEquals("text/css", this.response.getContentType());
        Assert.assertEquals(17, this.response.getContentLength());
        Assert.assertEquals("max-age=3600", this.response.getHeader("Cache-Control"));
        Assert.assertTrue(this.response.containsHeader("Last-Modified"));
        Assert.assertEquals(((resourceLastModified("test/foo.css")) / 1000), ((this.response.getDateHeader("Last-Modified")) / 1000));
        Assert.assertEquals("bytes", this.response.getHeader("Accept-Ranges"));
        Assert.assertEquals(1, this.response.getHeaders("Accept-Ranges").size());
        Assert.assertEquals("h1 { color:red; }", this.response.getContentAsString());
    }

    @Test
    public void getResourceHttpHeader() throws Exception {
        this.request.setMethod("HEAD");
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "foo.css");
        this.handler.handleRequest(this.request, this.response);
        Assert.assertEquals(200, this.response.getStatus());
        Assert.assertEquals("text/css", this.response.getContentType());
        Assert.assertEquals(17, this.response.getContentLength());
        Assert.assertEquals("max-age=3600", this.response.getHeader("Cache-Control"));
        Assert.assertTrue(this.response.containsHeader("Last-Modified"));
        Assert.assertEquals(((resourceLastModified("test/foo.css")) / 1000), ((this.response.getDateHeader("Last-Modified")) / 1000));
        Assert.assertEquals("bytes", this.response.getHeader("Accept-Ranges"));
        Assert.assertEquals(1, this.response.getHeaders("Accept-Ranges").size());
        Assert.assertEquals(0, this.response.getContentAsByteArray().length);
    }

    @Test
    public void getResourceHttpOptions() throws Exception {
        this.request.setMethod("OPTIONS");
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "foo.css");
        this.handler.handleRequest(this.request, this.response);
        Assert.assertEquals(200, this.response.getStatus());
        Assert.assertEquals("GET,HEAD,OPTIONS", this.response.getHeader("Allow"));
    }

    @Test
    public void getResourceNoCache() throws Exception {
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "foo.css");
        this.handler.setCacheSeconds(0);
        this.handler.handleRequest(this.request, this.response);
        Assert.assertEquals("no-store", this.response.getHeader("Cache-Control"));
        Assert.assertTrue(this.response.containsHeader("Last-Modified"));
        Assert.assertEquals(((resourceLastModified("test/foo.css")) / 1000), ((this.response.getDateHeader("Last-Modified")) / 1000));
        Assert.assertEquals("bytes", this.response.getHeader("Accept-Ranges"));
        Assert.assertEquals(1, this.response.getHeaders("Accept-Ranges").size());
    }

    @Test
    public void getVersionedResource() throws Exception {
        VersionResourceResolver versionResolver = new VersionResourceResolver().addFixedVersionStrategy("versionString", "/**");
        this.handler.setResourceResolvers(Arrays.asList(versionResolver, new PathResourceResolver()));
        this.handler.afterPropertiesSet();
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "versionString/foo.css");
        this.handler.handleRequest(this.request, this.response);
        Assert.assertEquals("\"versionString\"", this.response.getHeader("ETag"));
        Assert.assertEquals("bytes", this.response.getHeader("Accept-Ranges"));
        Assert.assertEquals(1, this.response.getHeaders("Accept-Ranges").size());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void getResourceHttp10BehaviorCache() throws Exception {
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "foo.css");
        this.handler.setCacheSeconds(3600);
        this.handler.setUseExpiresHeader(true);
        this.handler.setUseCacheControlHeader(true);
        this.handler.setAlwaysMustRevalidate(true);
        this.handler.handleRequest(this.request, this.response);
        Assert.assertEquals("max-age=3600, must-revalidate", this.response.getHeader("Cache-Control"));
        Assert.assertTrue(((this.response.getDateHeader("Expires")) >= (((System.currentTimeMillis()) - 1000) + (3600 * 1000))));
        Assert.assertTrue(this.response.containsHeader("Last-Modified"));
        Assert.assertEquals(((resourceLastModified("test/foo.css")) / 1000), ((this.response.getDateHeader("Last-Modified")) / 1000));
        Assert.assertEquals("bytes", this.response.getHeader("Accept-Ranges"));
        Assert.assertEquals(1, this.response.getHeaders("Accept-Ranges").size());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void getResourceHttp10BehaviorNoCache() throws Exception {
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "foo.css");
        this.handler.setCacheSeconds(0);
        this.handler.setUseExpiresHeader(true);
        this.handler.setUseCacheControlNoStore(false);
        this.handler.setUseCacheControlHeader(true);
        this.handler.handleRequest(this.request, this.response);
        Assert.assertEquals("no-cache", this.response.getHeader("Pragma"));
        Assert.assertThat(this.response.getHeaderValues("Cache-Control"), Matchers.iterableWithSize(1));
        Assert.assertEquals("no-cache", this.response.getHeader("Cache-Control"));
        Assert.assertTrue(((this.response.getDateHeader("Expires")) <= (System.currentTimeMillis())));
        Assert.assertTrue(this.response.containsHeader("Last-Modified"));
        Assert.assertEquals(((resourceLastModified("test/foo.css")) / 1000), ((this.response.getDateHeader("Last-Modified")) / 1000));
        Assert.assertEquals("bytes", this.response.getHeader("Accept-Ranges"));
        Assert.assertEquals(1, this.response.getHeaders("Accept-Ranges").size());
    }

    @Test
    public void getResourceWithHtmlMediaType() throws Exception {
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "foo.html");
        this.handler.handleRequest(this.request, this.response);
        Assert.assertEquals("text/html", this.response.getContentType());
        Assert.assertEquals("max-age=3600", this.response.getHeader("Cache-Control"));
        Assert.assertTrue(this.response.containsHeader("Last-Modified"));
        Assert.assertEquals(((resourceLastModified("test/foo.html")) / 1000), ((this.response.getDateHeader("Last-Modified")) / 1000));
        Assert.assertEquals("bytes", this.response.getHeader("Accept-Ranges"));
        Assert.assertEquals(1, this.response.getHeaders("Accept-Ranges").size());
    }

    @Test
    public void getResourceFromAlternatePath() throws Exception {
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "baz.css");
        this.handler.handleRequest(this.request, this.response);
        Assert.assertEquals("text/css", this.response.getContentType());
        Assert.assertEquals(17, this.response.getContentLength());
        Assert.assertEquals("max-age=3600", this.response.getHeader("Cache-Control"));
        Assert.assertTrue(this.response.containsHeader("Last-Modified"));
        Assert.assertEquals(((resourceLastModified("testalternatepath/baz.css")) / 1000), ((this.response.getDateHeader("Last-Modified")) / 1000));
        Assert.assertEquals("bytes", this.response.getHeader("Accept-Ranges"));
        Assert.assertEquals(1, this.response.getHeaders("Accept-Ranges").size());
        Assert.assertEquals("h1 { color:red; }", this.response.getContentAsString());
    }

    @Test
    public void getResourceFromSubDirectory() throws Exception {
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "js/foo.js");
        this.handler.handleRequest(this.request, this.response);
        Assert.assertEquals("text/javascript", this.response.getContentType());
        Assert.assertEquals("function foo() { console.log(\"hello world\"); }", this.response.getContentAsString());
    }

    @Test
    public void getResourceFromSubDirectoryOfAlternatePath() throws Exception {
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "js/baz.js");
        this.handler.handleRequest(this.request, this.response);
        Assert.assertEquals("text/javascript", this.response.getContentType());
        Assert.assertEquals("function foo() { console.log(\"hello world\"); }", this.response.getContentAsString());
    }

    // SPR-13658
    @Test
    public void getResourceWithRegisteredMediaType() throws Exception {
        ContentNegotiationManagerFactoryBean factory = new ContentNegotiationManagerFactoryBean();
        factory.addMediaType("bar", new MediaType("foo", "bar"));
        factory.afterPropertiesSet();
        ContentNegotiationManager manager = factory.getObject();
        List<Resource> paths = Collections.singletonList(new ClassPathResource("test/", getClass()));
        ResourceHttpRequestHandler handler = new ResourceHttpRequestHandler();
        handler.setServletContext(new MockServletContext());
        handler.setLocations(paths);
        handler.setContentNegotiationManager(manager);
        handler.afterPropertiesSet();
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "foo.bar");
        handler.handleRequest(this.request, this.response);
        Assert.assertEquals("foo/bar", this.response.getContentType());
        Assert.assertEquals("h1 { color:red; }", this.response.getContentAsString());
    }

    // SPR-14577
    @Test
    public void getMediaTypeWithFavorPathExtensionOff() throws Exception {
        ContentNegotiationManagerFactoryBean factory = new ContentNegotiationManagerFactoryBean();
        factory.setFavorPathExtension(false);
        factory.afterPropertiesSet();
        ContentNegotiationManager manager = factory.getObject();
        List<Resource> paths = Collections.singletonList(new ClassPathResource("test/", getClass()));
        ResourceHttpRequestHandler handler = new ResourceHttpRequestHandler();
        handler.setServletContext(new MockServletContext());
        handler.setLocations(paths);
        handler.setContentNegotiationManager(manager);
        handler.afterPropertiesSet();
        this.request.addHeader("Accept", "application/json,text/plain,*/*");
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "foo.html");
        handler.handleRequest(this.request, this.response);
        Assert.assertEquals("text/html", this.response.getContentType());
    }

    // SPR-14368
    @Test
    public void getResourceWithMediaTypeResolvedThroughServletContext() throws Exception {
        MockServletContext servletContext = new MockServletContext() {
            @Override
            public String getMimeType(String filePath) {
                return "foo/bar";
            }

            @Override
            public String getVirtualServerName() {
                return "";
            }
        };
        List<Resource> paths = Collections.singletonList(new ClassPathResource("test/", getClass()));
        ResourceHttpRequestHandler handler = new ResourceHttpRequestHandler();
        handler.setServletContext(servletContext);
        handler.setLocations(paths);
        handler.afterPropertiesSet();
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "foo.css");
        handler.handleRequest(this.request, this.response);
        Assert.assertEquals("foo/bar", this.response.getContentType());
        Assert.assertEquals("h1 { color:red; }", this.response.getContentAsString());
    }

    @Test
    public void testInvalidPath() throws Exception {
        // Use mock ResourceResolver: i.e. we're only testing upfront validations...
        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(resource.getFilename()).thenThrow(new AssertionError("Resource should not be resolved"));
        Mockito.when(resource.getInputStream()).thenThrow(new AssertionError("Resource should not be resolved"));
        ResourceResolver resolver = Mockito.mock(ResourceResolver.class);
        Mockito.when(resolver.resolveResource(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(resource);
        ResourceHttpRequestHandler handler = new ResourceHttpRequestHandler();
        handler.setLocations(Collections.singletonList(new ClassPathResource("test/", getClass())));
        handler.setResourceResolvers(Collections.singletonList(resolver));
        handler.setServletContext(new ResourceHttpRequestHandlerTests.TestServletContext());
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
    public void resolvePathWithTraversal() throws Exception {
        for (HttpMethod method : HttpMethod.values()) {
            this.request = new MockHttpServletRequest("GET", "");
            this.response = new MockHttpServletResponse();
            testResolvePathWithTraversal(method);
        }
    }

    @Test
    public void ignoreInvalidEscapeSequence() throws Exception {
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "/%foo%/bar.txt");
        this.response = new MockHttpServletResponse();
        this.handler.handleRequest(this.request, this.response);
        Assert.assertEquals(404, this.response.getStatus());
    }

    @Test
    public void processPath() {
        // Unchanged
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
        Assert.assertEquals("/", this.handler.processPath("\\/ \\/   \\/ "));
        // duplicate slash or backslash
        Assert.assertEquals("/foo/ /bar/baz/", this.handler.processPath("//foo/ /bar//baz//"));
        Assert.assertEquals("/foo/ /bar/baz/", this.handler.processPath("\\\\foo\\ \\bar\\\\baz\\\\"));
        Assert.assertEquals("foo/bar", this.handler.processPath("foo\\\\/\\////bar"));
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
        ResourceHttpRequestHandler handler = new ResourceHttpRequestHandler();
        handler.setResourceResolvers(Collections.singletonList(pathResolver));
        handler.setServletContext(new MockServletContext());
        handler.setLocations(Arrays.asList(location1, location2));
        handler.afterPropertiesSet();
        Resource[] locations = pathResolver.getAllowedLocations();
        Assert.assertEquals(1, locations.length);
        Assert.assertEquals("test/", getPath());
    }

    @Test
    public void notModified() throws Exception {
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "foo.css");
        this.request.addHeader("If-Modified-Since", resourceLastModified("test/foo.css"));
        this.handler.handleRequest(this.request, this.response);
        Assert.assertEquals(SC_NOT_MODIFIED, this.response.getStatus());
    }

    @Test
    public void modified() throws Exception {
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "foo.css");
        this.request.addHeader("If-Modified-Since", ((((resourceLastModified("test/foo.css")) / 1000) * 1000) - 1));
        this.handler.handleRequest(this.request, this.response);
        Assert.assertEquals(SC_OK, this.response.getStatus());
        Assert.assertEquals("h1 { color:red; }", this.response.getContentAsString());
    }

    @Test
    public void directory() throws Exception {
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "js/");
        this.handler.handleRequest(this.request, this.response);
        Assert.assertEquals(404, this.response.getStatus());
    }

    @Test
    public void directoryInJarFile() throws Exception {
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "underscorejs/");
        this.handler.handleRequest(this.request, this.response);
        Assert.assertEquals(404, this.response.getStatus());
    }

    @Test
    public void missingResourcePath() throws Exception {
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "");
        this.handler.handleRequest(this.request, this.response);
        Assert.assertEquals(404, this.response.getStatus());
    }

    @Test(expected = IllegalStateException.class)
    public void noPathWithinHandlerMappingAttribute() throws Exception {
        this.handler.handleRequest(this.request, this.response);
    }

    @Test(expected = HttpRequestMethodNotSupportedException.class)
    public void unsupportedHttpMethod() throws Exception {
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "foo.css");
        this.request.setMethod("POST");
        this.handler.handleRequest(this.request, this.response);
    }

    @Test
    public void resourceNotFound() throws Exception {
        for (HttpMethod method : HttpMethod.values()) {
            this.request = new MockHttpServletRequest("GET", "");
            this.response = new MockHttpServletResponse();
            resourceNotFound(method);
        }
    }

    @Test
    public void partialContentByteRange() throws Exception {
        this.request.addHeader("Range", "bytes=0-1");
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "foo.txt");
        this.handler.handleRequest(this.request, this.response);
        Assert.assertEquals(206, this.response.getStatus());
        Assert.assertEquals("text/plain", this.response.getContentType());
        Assert.assertEquals(2, this.response.getContentLength());
        Assert.assertEquals("bytes 0-1/10", this.response.getHeader("Content-Range"));
        Assert.assertEquals("So", this.response.getContentAsString());
        Assert.assertEquals("bytes", this.response.getHeader("Accept-Ranges"));
        Assert.assertEquals(1, this.response.getHeaders("Accept-Ranges").size());
    }

    @Test
    public void partialContentByteRangeNoEnd() throws Exception {
        this.request.addHeader("Range", "bytes=9-");
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "foo.txt");
        this.handler.handleRequest(this.request, this.response);
        Assert.assertEquals(206, this.response.getStatus());
        Assert.assertEquals("text/plain", this.response.getContentType());
        Assert.assertEquals(1, this.response.getContentLength());
        Assert.assertEquals("bytes 9-9/10", this.response.getHeader("Content-Range"));
        Assert.assertEquals(".", this.response.getContentAsString());
        Assert.assertEquals("bytes", this.response.getHeader("Accept-Ranges"));
        Assert.assertEquals(1, this.response.getHeaders("Accept-Ranges").size());
    }

    @Test
    public void partialContentByteRangeLargeEnd() throws Exception {
        this.request.addHeader("Range", "bytes=9-10000");
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "foo.txt");
        this.handler.handleRequest(this.request, this.response);
        Assert.assertEquals(206, this.response.getStatus());
        Assert.assertEquals("text/plain", this.response.getContentType());
        Assert.assertEquals(1, this.response.getContentLength());
        Assert.assertEquals("bytes 9-9/10", this.response.getHeader("Content-Range"));
        Assert.assertEquals(".", this.response.getContentAsString());
        Assert.assertEquals("bytes", this.response.getHeader("Accept-Ranges"));
        Assert.assertEquals(1, this.response.getHeaders("Accept-Ranges").size());
    }

    @Test
    public void partialContentSuffixRange() throws Exception {
        this.request.addHeader("Range", "bytes=-1");
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "foo.txt");
        this.handler.handleRequest(this.request, this.response);
        Assert.assertEquals(206, this.response.getStatus());
        Assert.assertEquals("text/plain", this.response.getContentType());
        Assert.assertEquals(1, this.response.getContentLength());
        Assert.assertEquals("bytes 9-9/10", this.response.getHeader("Content-Range"));
        Assert.assertEquals(".", this.response.getContentAsString());
        Assert.assertEquals("bytes", this.response.getHeader("Accept-Ranges"));
        Assert.assertEquals(1, this.response.getHeaders("Accept-Ranges").size());
    }

    @Test
    public void partialContentSuffixRangeLargeSuffix() throws Exception {
        this.request.addHeader("Range", "bytes=-11");
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "foo.txt");
        this.handler.handleRequest(this.request, this.response);
        Assert.assertEquals(206, this.response.getStatus());
        Assert.assertEquals("text/plain", this.response.getContentType());
        Assert.assertEquals(10, this.response.getContentLength());
        Assert.assertEquals("bytes 0-9/10", this.response.getHeader("Content-Range"));
        Assert.assertEquals("Some text.", this.response.getContentAsString());
        Assert.assertEquals("bytes", this.response.getHeader("Accept-Ranges"));
        Assert.assertEquals(1, this.response.getHeaders("Accept-Ranges").size());
    }

    @Test
    public void partialContentInvalidRangeHeader() throws Exception {
        this.request.addHeader("Range", "bytes= foo bar");
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "foo.txt");
        this.handler.handleRequest(this.request, this.response);
        Assert.assertEquals(416, this.response.getStatus());
        Assert.assertEquals("bytes */10", this.response.getHeader("Content-Range"));
        Assert.assertEquals("bytes", this.response.getHeader("Accept-Ranges"));
        Assert.assertEquals(1, this.response.getHeaders("Accept-Ranges").size());
    }

    @Test
    public void partialContentMultipleByteRanges() throws Exception {
        this.request.addHeader("Range", "bytes=0-1, 4-5, 8-9");
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "foo.txt");
        this.handler.handleRequest(this.request, this.response);
        Assert.assertEquals(206, this.response.getStatus());
        Assert.assertTrue(this.response.getContentType().startsWith("multipart/byteranges; boundary="));
        String boundary = "--" + (this.response.getContentType().substring(31));
        String content = this.response.getContentAsString();
        String[] ranges = StringUtils.tokenizeToStringArray(content, "\r\n", false, true);
        Assert.assertEquals(boundary, ranges[0]);
        Assert.assertEquals("Content-Type: text/plain", ranges[1]);
        Assert.assertEquals("Content-Range: bytes 0-1/10", ranges[2]);
        Assert.assertEquals("So", ranges[3]);
        Assert.assertEquals(boundary, ranges[4]);
        Assert.assertEquals("Content-Type: text/plain", ranges[5]);
        Assert.assertEquals("Content-Range: bytes 4-5/10", ranges[6]);
        Assert.assertEquals(" t", ranges[7]);
        Assert.assertEquals(boundary, ranges[8]);
        Assert.assertEquals("Content-Type: text/plain", ranges[9]);
        Assert.assertEquals("Content-Range: bytes 8-9/10", ranges[10]);
        Assert.assertEquals("t.", ranges[11]);
    }

    // SPR-14005
    @Test
    public void doOverwriteExistingCacheControlHeaders() throws Exception {
        this.request.setAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "foo.css");
        this.response.setHeader("Cache-Control", "no-store");
        this.handler.handleRequest(this.request, this.response);
        Assert.assertEquals("max-age=3600", this.response.getHeader("Cache-Control"));
    }

    private static class TestServletContext extends MockServletContext {
        @Override
        public String getMimeType(String filePath) {
            if (filePath.endsWith(".css")) {
                return "text/css";
            } else
                if (filePath.endsWith(".js")) {
                    return "text/javascript";
                } else {
                    return super.getMimeType(filePath);
                }

        }
    }
}

