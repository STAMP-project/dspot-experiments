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
package org.springframework.web.client;


import HttpClientErrorException.BadRequest;
import HttpMethod.GET;
import HttpMethod.HEAD;
import HttpMethod.OPTIONS;
import HttpMethod.TRACE;
import HttpStatus.BAD_REQUEST;
import HttpStatus.INTERNAL_SERVER_ERROR;
import HttpStatus.NOT_FOUND;
import HttpStatus.NOT_MODIFIED;
import HttpStatus.NO_CONTENT;
import HttpStatus.OK;
import MediaType.TEXT_PLAIN;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.util.MultiValueMap;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;


/**
 *
 *
 * @author Arjen Poutsma
 * @author Brian Clozel
 */
@RunWith(Parameterized.class)
public class RestTemplateIntegrationTests extends AbstractMockWebServerTestCase {
    private RestTemplate template;

    @Parameterized.Parameter
    public ClientHttpRequestFactory clientHttpRequestFactory;

    @Test
    public void getString() {
        String s = template.getForObject(((baseUrl) + "/{method}"), String.class, "get");
        Assert.assertEquals("Invalid content", AbstractMockWebServerTestCase.helloWorld, s);
    }

    @Test
    public void getEntity() {
        ResponseEntity<String> entity = template.getForEntity(((baseUrl) + "/{method}"), String.class, "get");
        Assert.assertEquals("Invalid content", AbstractMockWebServerTestCase.helloWorld, entity.getBody());
        Assert.assertFalse("No headers", entity.getHeaders().isEmpty());
        Assert.assertEquals("Invalid content-type", AbstractMockWebServerTestCase.textContentType, entity.getHeaders().getContentType());
        Assert.assertEquals("Invalid status code", OK, entity.getStatusCode());
    }

    @Test
    public void getNoResponse() {
        String s = template.getForObject(((baseUrl) + "/get/nothing"), String.class);
        Assert.assertNull("Invalid content", s);
    }

    @Test
    public void getNoContentTypeHeader() throws UnsupportedEncodingException {
        byte[] bytes = template.getForObject(((baseUrl) + "/get/nocontenttype"), byte[].class);
        Assert.assertArrayEquals("Invalid content", AbstractMockWebServerTestCase.helloWorld.getBytes("UTF-8"), bytes);
    }

    @Test
    public void getNoContent() {
        String s = template.getForObject(((baseUrl) + "/status/nocontent"), String.class);
        Assert.assertNull("Invalid content", s);
        ResponseEntity<String> entity = template.getForEntity(((baseUrl) + "/status/nocontent"), String.class);
        Assert.assertEquals("Invalid response code", NO_CONTENT, entity.getStatusCode());
        Assert.assertNull("Invalid content", entity.getBody());
    }

    @Test
    public void getNotModified() {
        String s = template.getForObject(((baseUrl) + "/status/notmodified"), String.class);
        Assert.assertNull("Invalid content", s);
        ResponseEntity<String> entity = template.getForEntity(((baseUrl) + "/status/notmodified"), String.class);
        Assert.assertEquals("Invalid response code", NOT_MODIFIED, entity.getStatusCode());
        Assert.assertNull("Invalid content", entity.getBody());
    }

    @Test
    public void postForLocation() throws URISyntaxException {
        URI location = template.postForLocation(((baseUrl) + "/{method}"), AbstractMockWebServerTestCase.helloWorld, "post");
        Assert.assertEquals("Invalid location", new URI(((baseUrl) + "/post/1")), location);
    }

    @Test
    public void postForLocationEntity() throws URISyntaxException {
        org.springframework.http.HttpHeaders entityHeaders = new org.springframework.http.HttpHeaders();
        entityHeaders.setContentType(new MediaType("text", "plain", StandardCharsets.ISO_8859_1));
        HttpEntity<String> entity = new HttpEntity(AbstractMockWebServerTestCase.helloWorld, entityHeaders);
        URI location = template.postForLocation(((baseUrl) + "/{method}"), entity, "post");
        Assert.assertEquals("Invalid location", new URI(((baseUrl) + "/post/1")), location);
    }

    @Test
    public void postForObject() throws URISyntaxException {
        String s = template.postForObject(((baseUrl) + "/{method}"), AbstractMockWebServerTestCase.helloWorld, String.class, "post");
        Assert.assertEquals("Invalid content", AbstractMockWebServerTestCase.helloWorld, s);
    }

    @Test
    public void patchForObject() throws URISyntaxException {
        // JDK client does not support the PATCH method
        Assume.assumeThat(this.clientHttpRequestFactory, Matchers.not(Matchers.instanceOf(SimpleClientHttpRequestFactory.class)));
        String s = template.patchForObject(((baseUrl) + "/{method}"), AbstractMockWebServerTestCase.helloWorld, String.class, "patch");
        Assert.assertEquals("Invalid content", AbstractMockWebServerTestCase.helloWorld, s);
    }

    @Test
    public void notFound() {
        try {
            template.execute(((baseUrl) + "/status/notfound"), GET, null, null);
            Assert.fail("HttpClientErrorException expected");
        } catch (HttpClientErrorException ex) {
            Assert.assertEquals(NOT_FOUND, ex.getStatusCode());
            Assert.assertNotNull(ex.getStatusText());
            Assert.assertNotNull(ex.getResponseBodyAsString());
        }
    }

    @Test
    public void badRequest() {
        try {
            template.execute(((baseUrl) + "/status/badrequest"), GET, null, null);
            Assert.fail("HttpClientErrorException.BadRequest expected");
        } catch (HttpClientErrorException ex) {
            Assert.assertEquals(BAD_REQUEST, ex.getStatusCode());
            Assert.assertEquals("400 Client Error", ex.getMessage());
        }
    }

    @Test
    public void serverError() {
        try {
            template.execute(((baseUrl) + "/status/server"), GET, null, null);
            Assert.fail("HttpServerErrorException expected");
        } catch (HttpServerErrorException ex) {
            Assert.assertEquals(INTERNAL_SERVER_ERROR, ex.getStatusCode());
            Assert.assertNotNull(ex.getStatusText());
            Assert.assertNotNull(ex.getResponseBodyAsString());
        }
    }

    @Test
    public void optionsForAllow() throws URISyntaxException {
        Set<HttpMethod> allowed = template.optionsForAllow(new URI(((baseUrl) + "/get")));
        Assert.assertEquals("Invalid response", EnumSet.of(GET, OPTIONS, HEAD, TRACE), allowed);
    }

    @Test
    public void uri() throws InterruptedException, URISyntaxException {
        String result = template.getForObject(((baseUrl) + "/uri/{query}"), String.class, "Z\u00fcrich");
        Assert.assertEquals("Invalid request URI", "/uri/Z%C3%BCrich", result);
        result = template.getForObject(((baseUrl) + "/uri/query={query}"), String.class, "foo@bar");
        Assert.assertEquals("Invalid request URI", "/uri/query=foo@bar", result);
        result = template.getForObject(((baseUrl) + "/uri/query={query}"), String.class, "T\u014dky\u014d");
        Assert.assertEquals("Invalid request URI", "/uri/query=T%C5%8Dky%C5%8D", result);
    }

    @Test
    public void multipart() throws UnsupportedEncodingException {
        MultiValueMap<String, Object> parts = new org.springframework.util.LinkedMultiValueMap();
        parts.add("name 1", "value 1");
        parts.add("name 2", "value 2+1");
        parts.add("name 2", "value 2+2");
        Resource logo = new ClassPathResource("/org/springframework/http/converter/logo.jpg");
        parts.add("logo", logo);
        template.postForLocation(((baseUrl) + "/multipart"), parts);
    }

    @Test
    public void form() throws UnsupportedEncodingException {
        MultiValueMap<String, String> form = new org.springframework.util.LinkedMultiValueMap();
        form.add("name 1", "value 1");
        form.add("name 2", "value 2+1");
        form.add("name 2", "value 2+2");
        template.postForLocation(((baseUrl) + "/form"), form);
    }

    @Test
    public void exchangeGet() throws Exception {
        org.springframework.http.HttpHeaders requestHeaders = new org.springframework.http.HttpHeaders();
        requestHeaders.set("MyHeader", "MyValue");
        HttpEntity<String> requestEntity = new HttpEntity(requestHeaders);
        ResponseEntity<String> response = template.exchange(((baseUrl) + "/{method}"), GET, requestEntity, String.class, "get");
        Assert.assertEquals("Invalid content", AbstractMockWebServerTestCase.helloWorld, response.getBody());
    }

    @Test
    public void exchangePost() throws Exception {
        org.springframework.http.HttpHeaders requestHeaders = new org.springframework.http.HttpHeaders();
        requestHeaders.set("MyHeader", "MyValue");
        requestHeaders.setContentType(TEXT_PLAIN);
        HttpEntity<String> entity = new HttpEntity(AbstractMockWebServerTestCase.helloWorld, requestHeaders);
        HttpEntity<Void> result = template.exchange(((baseUrl) + "/{method}"), HttpMethod.POST, entity, Void.class, "post");
        Assert.assertEquals("Invalid location", new URI(((baseUrl) + "/post/1")), result.getHeaders().getLocation());
        Assert.assertFalse(result.hasBody());
    }

    @Test
    public void jsonPostForObject() throws URISyntaxException {
        org.springframework.http.HttpHeaders entityHeaders = new org.springframework.http.HttpHeaders();
        entityHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        RestTemplateIntegrationTests.MySampleBean bean = new RestTemplateIntegrationTests.MySampleBean();
        bean.setWith1("with");
        bean.setWith2("with");
        bean.setWithout("without");
        HttpEntity<RestTemplateIntegrationTests.MySampleBean> entity = new HttpEntity(bean, entityHeaders);
        String s = template.postForObject(((baseUrl) + "/jsonpost"), entity, String.class);
        Assert.assertTrue(s.contains("\"with1\":\"with\""));
        Assert.assertTrue(s.contains("\"with2\":\"with\""));
        Assert.assertTrue(s.contains("\"without\":\"without\""));
    }

    @Test
    public void jsonPostForObjectWithJacksonView() throws URISyntaxException {
        org.springframework.http.HttpHeaders entityHeaders = new org.springframework.http.HttpHeaders();
        entityHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        RestTemplateIntegrationTests.MySampleBean bean = new RestTemplateIntegrationTests.MySampleBean("with", "with", "without");
        MappingJacksonValue jacksonValue = new MappingJacksonValue(bean);
        jacksonValue.setSerializationView(RestTemplateIntegrationTests.MyJacksonView1.class);
        HttpEntity<MappingJacksonValue> entity = new HttpEntity(jacksonValue, entityHeaders);
        String s = template.postForObject(((baseUrl) + "/jsonpost"), entity, String.class);
        Assert.assertTrue(s.contains("\"with1\":\"with\""));
        Assert.assertFalse(s.contains("\"with2\":\"with\""));
        Assert.assertFalse(s.contains("\"without\":\"without\""));
    }

    // SPR-12123
    @Test
    public void serverPort() {
        String s = template.getForObject("http://localhost:{port}/get", String.class, port);
        Assert.assertEquals("Invalid content", AbstractMockWebServerTestCase.helloWorld, s);
    }

    // SPR-13154
    @Test
    public void jsonPostForObjectWithJacksonTypeInfoList() throws URISyntaxException {
        List<RestTemplateIntegrationTests.ParentClass> list = new ArrayList<>();
        list.add(new RestTemplateIntegrationTests.Foo("foo"));
        list.add(new RestTemplateIntegrationTests.Bar("bar"));
        ParameterizedTypeReference<?> typeReference = new ParameterizedTypeReference<List<RestTemplateIntegrationTests.ParentClass>>() {};
        RequestEntity<List<RestTemplateIntegrationTests.ParentClass>> entity = RequestEntity.post(new URI(((baseUrl) + "/jsonpost"))).contentType(new MediaType("application", "json", StandardCharsets.UTF_8)).body(list, typeReference.getType());
        String content = template.exchange(entity, String.class).getBody();
        Assert.assertTrue(content.contains("\"type\":\"foo\""));
        Assert.assertTrue(content.contains("\"type\":\"bar\""));
    }

    // SPR-15015
    @Test
    public void postWithoutBody() throws Exception {
        Assert.assertNull(template.postForObject(((baseUrl) + "/jsonpost"), null, String.class));
    }

    public interface MyJacksonView1 {}

    public interface MyJacksonView2 {}

    public static class MySampleBean {
        @JsonView(RestTemplateIntegrationTests.MyJacksonView1.class)
        private String with1;

        @JsonView(RestTemplateIntegrationTests.MyJacksonView2.class)
        private String with2;

        private String without;

        private MySampleBean() {
        }

        private MySampleBean(String with1, String with2, String without) {
            this.with1 = with1;
            this.with2 = with2;
            this.without = without;
        }

        public String getWith1() {
            return with1;
        }

        public void setWith1(String with1) {
            this.with1 = with1;
        }

        public String getWith2() {
            return with2;
        }

        public void setWith2(String with2) {
            this.with2 = with2;
        }

        public String getWithout() {
            return without;
        }

        public void setWithout(String without) {
            this.without = without;
        }
    }

    @JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
    public static class ParentClass {
        private String parentProperty;

        public ParentClass() {
        }

        public ParentClass(String parentProperty) {
            this.parentProperty = parentProperty;
        }

        public String getParentProperty() {
            return parentProperty;
        }

        public void setParentProperty(String parentProperty) {
            this.parentProperty = parentProperty;
        }
    }

    @JsonTypeName("foo")
    public static class Foo extends RestTemplateIntegrationTests.ParentClass {
        public Foo() {
        }

        public Foo(String parentProperty) {
            super(parentProperty);
        }
    }

    @JsonTypeName("bar")
    public static class Bar extends RestTemplateIntegrationTests.ParentClass {
        public Bar() {
        }

        public Bar(String parentProperty) {
            super(parentProperty);
        }
    }
}

