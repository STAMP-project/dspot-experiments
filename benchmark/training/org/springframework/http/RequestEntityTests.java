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
package org.springframework.http;


import HttpMethod.DELETE;
import HttpMethod.GET;
import HttpMethod.HEAD;
import HttpMethod.OPTIONS;
import HttpMethod.PATCH;
import HttpMethod.POST;
import HttpMethod.PUT;
import MediaType.IMAGE_GIF;
import MediaType.IMAGE_JPEG;
import MediaType.IMAGE_PNG;
import MediaType.TEXT_PLAIN;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.util.UriTemplate;

import static MediaType.TEXT_PLAIN;
import static java.nio.charset.StandardCharsets.UTF_8;


/**
 * Unit tests for {@link org.springframework.http.RequestEntity}.
 *
 * @author Arjen Poutsma
 */
public class RequestEntityTests {
    @Test
    public void normal() throws URISyntaxException {
        String headerName = "My-Custom-Header";
        String headerValue = "HeaderValue";
        URI url = new URI("http://example.com");
        Integer entity = 42;
        RequestEntity<Object> requestEntity = RequestEntity.method(GET, url).header(headerName, headerValue).body(entity);
        Assert.assertNotNull(requestEntity);
        Assert.assertEquals(GET, requestEntity.getMethod());
        Assert.assertTrue(requestEntity.getHeaders().containsKey(headerName));
        Assert.assertEquals(headerValue, requestEntity.getHeaders().getFirst(headerName));
        Assert.assertEquals(entity, requestEntity.getBody());
    }

    @Test
    public void uriVariablesExpansion() throws URISyntaxException {
        URI uri = new UriTemplate("http://example.com/{foo}").expand("bar");
        RequestEntity.get(uri).accept(TEXT_PLAIN).build();
        String url = "http://www.{host}.com/{path}";
        String host = "example";
        String path = "foo/bar";
        URI expected = new URI("http://www.example.com/foo/bar");
        uri = new UriTemplate(url).expand(host, path);
        RequestEntity<?> entity = RequestEntity.get(uri).build();
        Assert.assertEquals(expected, entity.getUrl());
        Map<String, String> uriVariables = new HashMap<>(2);
        uriVariables.put("host", host);
        uriVariables.put("path", path);
        uri = new UriTemplate(url).expand(uriVariables);
        entity = RequestEntity.get(uri).build();
        Assert.assertEquals(expected, entity.getUrl());
    }

    @Test
    public void get() {
        RequestEntity<Void> requestEntity = RequestEntity.get(URI.create("http://example.com")).accept(IMAGE_GIF, IMAGE_JPEG, IMAGE_PNG).build();
        Assert.assertNotNull(requestEntity);
        Assert.assertEquals(GET, requestEntity.getMethod());
        Assert.assertTrue(requestEntity.getHeaders().containsKey("Accept"));
        Assert.assertEquals("image/gif, image/jpeg, image/png", requestEntity.getHeaders().getFirst("Accept"));
        Assert.assertNull(requestEntity.getBody());
    }

    @Test
    public void headers() throws URISyntaxException {
        MediaType accept = TEXT_PLAIN;
        long ifModifiedSince = 12345L;
        String ifNoneMatch = "\"foo\"";
        long contentLength = 67890;
        MediaType contentType = TEXT_PLAIN;
        RequestEntity<Void> responseEntity = RequestEntity.post(new URI("http://example.com")).accept(accept).acceptCharset(UTF_8).ifModifiedSince(ifModifiedSince).ifNoneMatch(ifNoneMatch).contentLength(contentLength).contentType(contentType).build();
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(POST, responseEntity.getMethod());
        Assert.assertEquals(new URI("http://example.com"), responseEntity.getUrl());
        HttpHeaders responseHeaders = responseEntity.getHeaders();
        Assert.assertEquals("text/plain", responseHeaders.getFirst("Accept"));
        Assert.assertEquals("utf-8", responseHeaders.getFirst("Accept-Charset"));
        Assert.assertEquals("Thu, 1 Jan 1970 00:00:12 GMT", responseHeaders.getFirst("If-Modified-Since"));
        Assert.assertEquals(ifNoneMatch, responseHeaders.getFirst("If-None-Match"));
        Assert.assertEquals(String.valueOf(contentLength), responseHeaders.getFirst("Content-Length"));
        Assert.assertEquals(contentType.toString(), responseHeaders.getFirst("Content-Type"));
        Assert.assertNull(responseEntity.getBody());
    }

    @Test
    public void methods() throws URISyntaxException {
        URI url = new URI("http://example.com");
        RequestEntity<?> entity = RequestEntity.get(url).build();
        Assert.assertEquals(GET, entity.getMethod());
        entity = RequestEntity.post(url).build();
        Assert.assertEquals(POST, entity.getMethod());
        entity = RequestEntity.head(url).build();
        Assert.assertEquals(HEAD, entity.getMethod());
        entity = RequestEntity.options(url).build();
        Assert.assertEquals(OPTIONS, entity.getMethod());
        entity = RequestEntity.put(url).build();
        Assert.assertEquals(PUT, entity.getMethod());
        entity = RequestEntity.patch(url).build();
        Assert.assertEquals(PATCH, entity.getMethod());
        entity = RequestEntity.delete(url).build();
        Assert.assertEquals(DELETE, entity.getMethod());
    }

    // SPR-13154
    @Test
    public void types() throws URISyntaxException {
        URI url = new URI("http://example.com");
        List<String> body = Arrays.asList("foo", "bar");
        ParameterizedTypeReference<?> typeReference = new ParameterizedTypeReference<List<String>>() {};
        RequestEntity<?> entity = RequestEntity.post(url).body(body, typeReference.getType());
        Assert.assertEquals(typeReference.getType(), entity.getType());
    }
}

