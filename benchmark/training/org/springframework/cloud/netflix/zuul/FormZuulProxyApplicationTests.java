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


import HttpStatus.OK;
import MediaType.APPLICATION_FORM_URLENCODED;
import MediaType.APPLICATION_JSON;
import MediaType.MULTIPART_FORM_DATA;
import MediaType.TEXT_PLAIN;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.MultiValueMap;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = FormZuulProxyApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT, value = { "zuul.routes.simplefzpat:/simplefzpat/**" })
@DirtiesContext
public class FormZuulProxyApplicationTests {
    @Inject
    private TestRestTemplate restTemplate;

    @Test
    public void postWithForm() {
        MultiValueMap<String, String> form = new org.springframework.util.LinkedMultiValueMap();
        form.set("foo", "bar");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_FORM_URLENCODED);
        ResponseEntity result = sendPost("/simplefzpat/form", form, headers);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Posted! {foo=[bar]}");
    }

    @Test
    public void postWithMultipartForm() {
        MultiValueMap<String, String> form = new org.springframework.util.LinkedMultiValueMap();
        form.set("foo", "bar");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MULTIPART_FORM_DATA);
        ResponseEntity result = sendPost("/simplefzpat/form", form, headers);
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
        ResponseEntity result = sendPost("/simplefzpat/file", form, headers);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Posted! bar");
    }

    @Test
    public void postWithMultipartFileAndForm() {
        MultiValueMap<String, Object> form = new org.springframework.util.LinkedMultiValueMap();
        HttpHeaders part = new HttpHeaders();
        part.setContentType(TEXT_PLAIN);
        part.setContentDispositionFormData("file", "foo.txt");
        form.set("foo", new org.springframework.http.HttpEntity("bar".getBytes(), part));
        form.set("field", "data");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MULTIPART_FORM_DATA);
        ResponseEntity result = sendPost("/simplefzpat/fileandform", form, headers);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Posted! bar!field!data");
    }

    @Test
    public void postWithMultipartApplicationJson() {
        MultiValueMap<String, Object> form = new org.springframework.util.LinkedMultiValueMap();
        HttpHeaders partHeaders = new HttpHeaders();
        partHeaders.setContentType(APPLICATION_JSON);
        form.set("field", new org.springframework.http.HttpEntity("{foo=[bar]}", partHeaders));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MULTIPART_FORM_DATA);
        ResponseEntity result = sendPost("/simplefzpat/json", form, headers);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Posted! {foo=[bar]} as application/json");
    }

    @Test
    public void postWithUTF8Form() {
        MultiValueMap<String, String> form = new org.springframework.util.LinkedMultiValueMap();
        form.set("foo", "bar");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(((MediaType.APPLICATION_FORM_URLENCODED_VALUE) + "; charset=UTF-8")));
        ResponseEntity result = sendPost("/simplefzpat/form", form, headers);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Posted! {foo=[bar]}");
    }

    @Test
    public void postWithUrlParams() throws Exception {
        MultiValueMap<String, String> form = new org.springframework.util.LinkedMultiValueMap();
        form.set("foo", "bar");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(((MediaType.APPLICATION_FORM_URLENCODED_VALUE) + "; charset=UTF-8")));
        ResponseEntity result = sendPost("/simplefzpat/form?uriParam=uriValue", form, headers);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Posted! {uriParam=[uriValue], foo=[bar]}");
    }

    @Test
    public void getWithUrlParams() throws Exception {
        ResponseEntity<String> result = sendGet("/simplefzpat/form?uriParam=uriValue");
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Posted! {uriParam=[uriValue]}");
    }
}

