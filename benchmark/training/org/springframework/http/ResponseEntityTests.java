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
package org.springframework.http;


import HttpHeaders.CACHE_CONTROL;
import HttpStatus.ACCEPTED;
import HttpStatus.BAD_REQUEST;
import HttpStatus.CREATED;
import HttpStatus.NOT_FOUND;
import HttpStatus.NO_CONTENT;
import HttpStatus.OK;
import HttpStatus.UNPROCESSABLE_ENTITY;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static MediaType.TEXT_PLAIN;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;


/**
 *
 *
 * @author Arjen Poutsma
 * @author Marcel Overdijk
 * @author Kazuki Shimizu
 */
public class ResponseEntityTests {
    @Test
    public void normal() {
        String headerName = "My-Custom-Header";
        String headerValue1 = "HeaderValue1";
        String headerValue2 = "HeaderValue2";
        Integer entity = 42;
        ResponseEntity<Integer> responseEntity = ResponseEntity.status(OK).header(headerName, headerValue1, headerValue2).body(entity);
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(OK, responseEntity.getStatusCode());
        Assert.assertTrue(responseEntity.getHeaders().containsKey(headerName));
        List<String> list = responseEntity.getHeaders().get(headerName);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(headerValue1, list.get(0));
        Assert.assertEquals(headerValue2, list.get(1));
        Assert.assertEquals(entity, responseEntity.getBody());
    }

    @Test
    public void okNoBody() {
        ResponseEntity<Void> responseEntity = ResponseEntity.ok().build();
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(OK, responseEntity.getStatusCode());
        Assert.assertNull(responseEntity.getBody());
    }

    @Test
    public void okEntity() {
        Integer entity = 42;
        ResponseEntity<Integer> responseEntity = ResponseEntity.ok(entity);
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(OK, responseEntity.getStatusCode());
        Assert.assertEquals(entity, responseEntity.getBody());
    }

    @Test
    public void ofOptional() {
        Integer entity = 42;
        ResponseEntity<Integer> responseEntity = ResponseEntity.of(Optional.of(entity));
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(OK, responseEntity.getStatusCode());
        Assert.assertEquals(entity, responseEntity.getBody());
    }

    @Test
    public void ofEmptyOptional() {
        ResponseEntity<Integer> responseEntity = ResponseEntity.of(Optional.empty());
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(NOT_FOUND, responseEntity.getStatusCode());
        Assert.assertNull(responseEntity.getBody());
    }

    @Test
    public void createdLocation() throws URISyntaxException {
        URI location = new URI("location");
        ResponseEntity<Void> responseEntity = ResponseEntity.created(location).build();
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(CREATED, responseEntity.getStatusCode());
        Assert.assertTrue(responseEntity.getHeaders().containsKey("Location"));
        Assert.assertEquals(location.toString(), responseEntity.getHeaders().getFirst("Location"));
        Assert.assertNull(responseEntity.getBody());
        ResponseEntity.created(location).header("MyResponseHeader", "MyValue").body("Hello World");
    }

    @Test
    public void acceptedNoBody() throws URISyntaxException {
        ResponseEntity<Void> responseEntity = ResponseEntity.accepted().build();
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(ACCEPTED, responseEntity.getStatusCode());
        Assert.assertNull(responseEntity.getBody());
    }

    // SPR-14939
    @Test
    public void acceptedNoBodyWithAlternativeBodyType() throws URISyntaxException {
        ResponseEntity<String> responseEntity = ResponseEntity.accepted().build();
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(ACCEPTED, responseEntity.getStatusCode());
        Assert.assertNull(responseEntity.getBody());
    }

    @Test
    public void noContent() throws URISyntaxException {
        ResponseEntity<Void> responseEntity = ResponseEntity.noContent().build();
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(NO_CONTENT, responseEntity.getStatusCode());
        Assert.assertNull(responseEntity.getBody());
    }

    @Test
    public void badRequest() throws URISyntaxException {
        ResponseEntity<Void> responseEntity = ResponseEntity.badRequest().build();
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
        Assert.assertNull(responseEntity.getBody());
    }

    @Test
    public void notFound() throws URISyntaxException {
        ResponseEntity<Void> responseEntity = ResponseEntity.notFound().build();
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(NOT_FOUND, responseEntity.getStatusCode());
        Assert.assertNull(responseEntity.getBody());
    }

    @Test
    public void unprocessableEntity() throws URISyntaxException {
        ResponseEntity<String> responseEntity = ResponseEntity.unprocessableEntity().body("error");
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
        Assert.assertEquals("error", responseEntity.getBody());
    }

    @Test
    public void headers() throws URISyntaxException {
        URI location = new URI("location");
        long contentLength = 67890;
        MediaType contentType = TEXT_PLAIN;
        ResponseEntity<Void> responseEntity = ResponseEntity.ok().allow(HttpMethod.GET).lastModified(12345L).location(location).contentLength(contentLength).contentType(contentType).build();
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(OK, responseEntity.getStatusCode());
        HttpHeaders responseHeaders = responseEntity.getHeaders();
        Assert.assertEquals("GET", responseHeaders.getFirst("Allow"));
        Assert.assertEquals("Thu, 1 Jan 1970 00:00:12 GMT", responseHeaders.getFirst("Last-Modified"));
        Assert.assertEquals(location.toASCIIString(), responseHeaders.getFirst("Location"));
        Assert.assertEquals(String.valueOf(contentLength), responseHeaders.getFirst("Content-Length"));
        Assert.assertEquals(contentType.toString(), responseHeaders.getFirst("Content-Type"));
        Assert.assertNull(responseEntity.getBody());
    }

    @Test
    public void Etagheader() throws URISyntaxException {
        ResponseEntity<Void> responseEntity = ResponseEntity.ok().eTag("\"foo\"").build();
        Assert.assertEquals("\"foo\"", responseEntity.getHeaders().getETag());
        responseEntity = ResponseEntity.ok().eTag("foo").build();
        Assert.assertEquals("\"foo\"", responseEntity.getHeaders().getETag());
        responseEntity = ResponseEntity.ok().eTag("W/\"foo\"").build();
        Assert.assertEquals("W/\"foo\"", responseEntity.getHeaders().getETag());
    }

    @Test
    public void headersCopy() {
        HttpHeaders customHeaders = new HttpHeaders();
        customHeaders.set("X-CustomHeader", "vale");
        ResponseEntity<Void> responseEntity = ResponseEntity.ok().headers(customHeaders).build();
        HttpHeaders responseHeaders = responseEntity.getHeaders();
        Assert.assertEquals(OK, responseEntity.getStatusCode());
        Assert.assertEquals(1, responseHeaders.size());
        Assert.assertEquals(1, responseHeaders.get("X-CustomHeader").size());
        Assert.assertEquals("vale", responseHeaders.getFirst("X-CustomHeader"));
    }

    // SPR-12792
    @Test
    public void headersCopyWithEmptyAndNull() {
        ResponseEntity<Void> responseEntityWithEmptyHeaders = ResponseEntity.ok().headers(new HttpHeaders()).build();
        ResponseEntity<Void> responseEntityWithNullHeaders = ResponseEntity.ok().headers(null).build();
        Assert.assertEquals(OK, responseEntityWithEmptyHeaders.getStatusCode());
        Assert.assertTrue(responseEntityWithEmptyHeaders.getHeaders().isEmpty());
        Assert.assertEquals(responseEntityWithEmptyHeaders.toString(), responseEntityWithNullHeaders.toString());
    }

    @Test
    public void emptyCacheControl() {
        Integer entity = 42;
        ResponseEntity<Integer> responseEntity = ResponseEntity.status(OK).cacheControl(CacheControl.empty()).body(entity);
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(OK, responseEntity.getStatusCode());
        Assert.assertFalse(responseEntity.getHeaders().containsKey(CACHE_CONTROL));
        Assert.assertEquals(entity, responseEntity.getBody());
    }

    @Test
    public void cacheControl() {
        Integer entity = 42;
        ResponseEntity<Integer> responseEntity = ResponseEntity.status(OK).cacheControl(CacheControl.maxAge(1, HOURS).cachePrivate().mustRevalidate().proxyRevalidate().sMaxAge(30, MINUTES)).body(entity);
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(OK, responseEntity.getStatusCode());
        Assert.assertTrue(responseEntity.getHeaders().containsKey(CACHE_CONTROL));
        Assert.assertEquals(entity, responseEntity.getBody());
        String cacheControlHeader = responseEntity.getHeaders().getCacheControl();
        Assert.assertThat(cacheControlHeader, Matchers.equalTo("max-age=3600, must-revalidate, private, proxy-revalidate, s-maxage=1800"));
    }

    @Test
    public void cacheControlNoCache() {
        Integer entity = 42;
        ResponseEntity<Integer> responseEntity = ResponseEntity.status(OK).cacheControl(CacheControl.noStore()).body(entity);
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(OK, responseEntity.getStatusCode());
        Assert.assertTrue(responseEntity.getHeaders().containsKey(CACHE_CONTROL));
        Assert.assertEquals(entity, responseEntity.getBody());
        String cacheControlHeader = responseEntity.getHeaders().getCacheControl();
        Assert.assertThat(cacheControlHeader, Matchers.equalTo("no-store"));
    }

    @Test
    public void statusCodeAsInt() {
        Integer entity = 42;
        ResponseEntity<Integer> responseEntity = ResponseEntity.status(200).body(entity);
        Assert.assertEquals(200, responseEntity.getStatusCode().value());
        Assert.assertEquals(entity, responseEntity.getBody());
    }

    @Test
    public void customStatusCode() {
        Integer entity = 42;
        ResponseEntity<Integer> responseEntity = ResponseEntity.status(299).body(entity);
        Assert.assertEquals(299, responseEntity.getStatusCodeValue());
        Assert.assertEquals(entity, responseEntity.getBody());
    }
}

