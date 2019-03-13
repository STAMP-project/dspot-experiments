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


import MediaType.TEXT_PLAIN;
import java.net.URI;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.MultiValueMap;

import static HttpMethod.GET;
import static HttpStatus.OK;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class HttpEntityTests {
    @Test
    public void noHeaders() {
        String body = "foo";
        HttpEntity<String> entity = new HttpEntity(body);
        Assert.assertSame(body, entity.getBody());
        Assert.assertTrue(entity.getHeaders().isEmpty());
    }

    @Test
    public void httpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(TEXT_PLAIN);
        String body = "foo";
        HttpEntity<String> entity = new HttpEntity(body, headers);
        Assert.assertEquals(body, entity.getBody());
        Assert.assertEquals(TEXT_PLAIN, entity.getHeaders().getContentType());
        Assert.assertEquals("text/plain", entity.getHeaders().getFirst("Content-Type"));
    }

    @Test
    public void multiValueMap() {
        MultiValueMap<String, String> map = new org.springframework.util.LinkedMultiValueMap();
        map.set("Content-Type", "text/plain");
        String body = "foo";
        HttpEntity<String> entity = new HttpEntity(body, map);
        Assert.assertEquals(body, entity.getBody());
        Assert.assertEquals(TEXT_PLAIN, entity.getHeaders().getContentType());
        Assert.assertEquals("text/plain", entity.getHeaders().getFirst("Content-Type"));
    }

    @Test
    public void testEquals() {
        MultiValueMap<String, String> map1 = new org.springframework.util.LinkedMultiValueMap();
        map1.set("Content-Type", "text/plain");
        MultiValueMap<String, String> map2 = new org.springframework.util.LinkedMultiValueMap();
        map2.set("Content-Type", "application/json");
        Assert.assertTrue(new HttpEntity().equals(new HttpEntity<Object>()));
        Assert.assertFalse(new HttpEntity(map1).equals(new HttpEntity<Object>()));
        Assert.assertFalse(new HttpEntity().equals(new HttpEntity<Object>(map2)));
        Assert.assertTrue(new HttpEntity(map1).equals(new HttpEntity<Object>(map1)));
        Assert.assertFalse(new HttpEntity(map1).equals(new HttpEntity<Object>(map2)));
        Assert.assertTrue(new HttpEntity<String>(null, null).equals(new HttpEntity<String>(null, null)));
        Assert.assertFalse(new HttpEntity("foo", null).equals(new HttpEntity<String>(null, null)));
        Assert.assertFalse(new HttpEntity<String>(null, null).equals(new HttpEntity("bar", null)));
        Assert.assertTrue(new HttpEntity("foo", map1).equals(new HttpEntity<String>("foo", map1)));
        Assert.assertFalse(new HttpEntity("foo", map1).equals(new HttpEntity<String>("bar", map1)));
    }

    @Test
    public void responseEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(TEXT_PLAIN);
        String body = "foo";
        HttpEntity<String> httpEntity = new HttpEntity(body, headers);
        ResponseEntity<String> responseEntity = new ResponseEntity(body, headers, OK);
        ResponseEntity<String> responseEntity2 = new ResponseEntity(body, headers, OK);
        Assert.assertEquals(body, responseEntity.getBody());
        Assert.assertEquals(TEXT_PLAIN, responseEntity.getHeaders().getContentType());
        Assert.assertEquals("text/plain", responseEntity.getHeaders().getFirst("Content-Type"));
        Assert.assertEquals("text/plain", responseEntity.getHeaders().getFirst("Content-Type"));
        Assert.assertFalse(httpEntity.equals(responseEntity));
        Assert.assertFalse(responseEntity.equals(httpEntity));
        Assert.assertTrue(responseEntity.equals(responseEntity2));
        Assert.assertTrue(responseEntity2.equals(responseEntity));
    }

    @Test
    public void requestEntity() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(TEXT_PLAIN);
        String body = "foo";
        HttpEntity<String> httpEntity = new HttpEntity(body, headers);
        RequestEntity<String> requestEntity = new RequestEntity(body, headers, GET, new URI("/"));
        RequestEntity<String> requestEntity2 = new RequestEntity(body, headers, GET, new URI("/"));
        Assert.assertEquals(body, requestEntity.getBody());
        Assert.assertEquals(TEXT_PLAIN, requestEntity.getHeaders().getContentType());
        Assert.assertEquals("text/plain", requestEntity.getHeaders().getFirst("Content-Type"));
        Assert.assertEquals("text/plain", requestEntity.getHeaders().getFirst("Content-Type"));
        Assert.assertFalse(httpEntity.equals(requestEntity));
        Assert.assertFalse(requestEntity.equals(httpEntity));
        Assert.assertTrue(requestEntity.equals(requestEntity2));
        Assert.assertTrue(requestEntity2.equals(requestEntity));
    }
}

