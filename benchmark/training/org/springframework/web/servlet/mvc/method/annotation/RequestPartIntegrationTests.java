/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.web.servlet.mvc.method.annotation;


import HttpStatus.OK;
import MediaType.MULTIPART_FORM_DATA;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.eclipse.jetty.server.Server;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * Test access to parts of a multipart request with {@link RequestPart}.
 *
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 * @author Sam Brannen
 */
public class RequestPartIntegrationTests {
    private RestTemplate restTemplate;

    private static Server server;

    private static String baseUrl;

    @Test
    public void commonsMultipartResolver() throws Exception {
        testCreate(((RequestPartIntegrationTests.baseUrl) + "/commons-resolver/test"), "Jason");
        testCreate(((RequestPartIntegrationTests.baseUrl) + "/commons-resolver/test"), "Arjen");
    }

    @Test
    public void standardMultipartResolver() throws Exception {
        testCreate(((RequestPartIntegrationTests.baseUrl) + "/standard-resolver/test"), "Jason");
        testCreate(((RequestPartIntegrationTests.baseUrl) + "/standard-resolver/test"), "Arjen");
    }

    // SPR-13319
    @Test
    public void standardMultipartResolverWithEncodedFileName() throws Exception {
        byte[] boundary = MimeTypeUtils.generateMultipartBoundary();
        String boundaryText = new String(boundary, "US-ASCII");
        Map<String, String> params = Collections.singletonMap("boundary", boundaryText);
        String content = ((((((((("--" + boundaryText) + "\n") + "Content-Disposition: form-data; name=\"file\"; filename*=\"utf-8\'\'%C3%A9l%C3%A8ve.txt\"\n") + "Content-Type: text/plain\n") + "Content-Length: 7\n") + "\n") + "content\n") + "--") + boundaryText) + "--";
        RequestEntity<byte[]> requestEntity = RequestEntity.post(new URI(((RequestPartIntegrationTests.baseUrl) + "/standard-resolver/spr13319"))).contentType(new MediaType(MediaType.MULTIPART_FORM_DATA, params)).body(content.getBytes(StandardCharsets.US_ASCII));
        ByteArrayHttpMessageConverter converter = new ByteArrayHttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MULTIPART_FORM_DATA));
        this.restTemplate.setMessageConverters(Collections.singletonList(converter));
        ResponseEntity<Void> responseEntity = restTemplate.exchange(requestEntity, Void.class);
        Assert.assertEquals(OK, responseEntity.getStatusCode());
    }

    @Configuration
    @EnableWebMvc
    static class RequestPartTestConfig implements WebMvcConfigurer {
        @Bean
        public RequestPartIntegrationTests.RequestPartTestController controller() {
            return new RequestPartIntegrationTests.RequestPartTestController();
        }
    }

    @Configuration
    @SuppressWarnings("unused")
    static class CommonsMultipartResolverTestConfig extends RequestPartIntegrationTests.RequestPartTestConfig {
        @Bean
        public MultipartResolver multipartResolver() {
            return new CommonsMultipartResolver();
        }
    }

    @Configuration
    @SuppressWarnings("unused")
    static class StandardMultipartResolverTestConfig extends RequestPartIntegrationTests.RequestPartTestConfig {
        @Bean
        public MultipartResolver multipartResolver() {
            return new StandardServletMultipartResolver();
        }
    }

    @Controller
    @SuppressWarnings("unused")
    private static class RequestPartTestController {
        @RequestMapping(value = "/test", method = POST, consumes = { "multipart/mixed", "multipart/form-data" })
        public ResponseEntity<Object> create(@RequestPart(name = "json-data")
        RequestPartIntegrationTests.TestData testData, @RequestPart("file-data")
        Optional<MultipartFile> file, @RequestPart(name = "empty-data", required = false)
        RequestPartIntegrationTests.TestData emptyData, @RequestPart(name = "iso-8859-1-data")
        byte[] iso88591Data) {
            Assert.assertArrayEquals(new byte[]{ ((byte) (196)) }, iso88591Data);
            String url = (("http://localhost:8080/test/" + (testData.getName())) + "/") + (file.get().getOriginalFilename());
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(url));
            return new ResponseEntity(headers, HttpStatus.CREATED);
        }

        @RequestMapping(value = "/spr13319", method = POST, consumes = "multipart/form-data")
        public ResponseEntity<Void> create(@RequestPart("file")
        MultipartFile multipartFile) {
            Assert.assertEquals("?l?ve.txt", multipartFile.getOriginalFilename());
            return ResponseEntity.ok().build();
        }
    }

    @SuppressWarnings("unused")
    private static class TestData {
        private String name;

        public TestData() {
            super();
        }

        public TestData(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

