/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.cloud.netflix.zuul;


import HttpMethod.POST;
import HttpStatus.OK;
import MediaType.APPLICATION_FORM_URLENCODED;
import MediaType.MULTIPART_FORM_DATA;
import MediaType.TEXT_PLAIN;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.MultiValueMap;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = FormZuulServletProxyApplication.class, webEnvironment = RANDOM_PORT, properties = { "zuul.routes[simplefzspat].path:/simplefzspat/**", "zuul.routes[simplefzspat].serviceId:simplefzspat" })
@DirtiesContext
public class FormZuulServletProxyApplicationTests {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void postWithForm() {
        MultiValueMap<String, String> form = new org.springframework.util.LinkedMultiValueMap();
        form.set("foo", "bar");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_FORM_URLENCODED);
        ResponseEntity<String> result = testRestTemplate.exchange("/zuul/simplefzspat/form", POST, new org.springframework.http.HttpEntity(form, headers), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Posted! {foo=[bar]}");
    }

    @Test
    public void postWithMultipartForm() {
        MultiValueMap<String, String> form = new org.springframework.util.LinkedMultiValueMap();
        form.set("foo", "bar");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MULTIPART_FORM_DATA);
        ResponseEntity<String> result = testRestTemplate.exchange("/zuul/simplefzspat/form", POST, new org.springframework.http.HttpEntity(form, headers), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Posted! {foo=[bar]}");
    }

    @Test
    public void postWithMultipartFile() {
        MultiValueMap<String, Object> form = new org.springframework.util.LinkedMultiValueMap();
        HttpHeaders part = new HttpHeaders();
        part.setContentType(TEXT_PLAIN);
        part.setContentDispositionFormData("file", "foo.txt");
        form.set("foo", new org.springframework.http.HttpEntity("bar".getBytes(), part));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MULTIPART_FORM_DATA);
        headers.set("Transfer-Encoding", "chunked");
        headers.setContentLength((-1));
        ResponseEntity<String> result = testRestTemplate.exchange("/zuul/simplefzspat/file", POST, new org.springframework.http.HttpEntity(form, headers), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Posted! bar");
    }

    @Test
    public void postWithUTF8Form() {
        MultiValueMap<String, String> form = new org.springframework.util.LinkedMultiValueMap();
        form.set("foo", "bar");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(((MediaType.APPLICATION_FORM_URLENCODED_VALUE) + "; charset=UTF-8")));
        ResponseEntity<String> result = testRestTemplate.exchange("/zuul/simplefzspat/form", POST, new org.springframework.http.HttpEntity(form, headers), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Posted! {foo=[bar]}");
    }
}

