/**
 * Copyright 2012-2018 the original author or authors.
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
package sample.mustache;


import HttpMethod.GET;
import HttpStatus.INSUFFICIENT_STORAGE;
import HttpStatus.INTERNAL_SERVER_ERROR;
import HttpStatus.NOT_FOUND;
import HttpStatus.OK;
import HttpStatus.SERVICE_UNAVAILABLE;
import MediaType.TEXT_HTML;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Basic integration tests for Mustache application.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SampleWebMustacheApplicationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testMustacheTemplate() {
        ResponseEntity<String> entity = this.restTemplate.getForEntity("/", String.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
        assertThat(entity.getBody()).contains("Hello, Andy");
    }

    @Test
    public void testMustacheErrorTemplate() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(TEXT_HTML));
        HttpEntity<String> requestEntity = new HttpEntity(headers);
        ResponseEntity<String> responseEntity = this.restTemplate.exchange("/does-not-exist", GET, requestEntity, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(responseEntity.getBody()).contains("Something went wrong: 404 Not Found");
    }

    @Test
    public void test503HtmlResource() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(TEXT_HTML));
        HttpEntity<String> requestEntity = new HttpEntity(headers);
        ResponseEntity<String> entity = this.restTemplate.exchange("/serviceUnavailable", GET, requestEntity, String.class);
        assertThat(entity.getStatusCode()).isEqualTo(SERVICE_UNAVAILABLE);
        assertThat(entity.getBody()).contains("I'm a 503");
    }

    @Test
    public void test5xxHtmlResource() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(TEXT_HTML));
        HttpEntity<String> requestEntity = new HttpEntity(headers);
        ResponseEntity<String> entity = this.restTemplate.exchange("/bang", GET, requestEntity, String.class);
        assertThat(entity.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
        assertThat(entity.getBody()).contains("I'm a 5xx");
    }

    @Test
    public void test507Template() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(TEXT_HTML));
        HttpEntity<String> requestEntity = new HttpEntity(headers);
        ResponseEntity<String> entity = this.restTemplate.exchange("/insufficientStorage", GET, requestEntity, String.class);
        assertThat(entity.getStatusCode()).isEqualTo(INSUFFICIENT_STORAGE);
        assertThat(entity.getBody()).contains("I'm a 507");
    }
}

