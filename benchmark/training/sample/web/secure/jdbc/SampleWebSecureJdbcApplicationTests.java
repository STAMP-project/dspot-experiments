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
package sample.web.secure.jdbc;


import HttpMethod.GET;
import HttpMethod.POST;
import HttpStatus.FOUND;
import HttpStatus.OK;
import MediaType.APPLICATION_FORM_URLENCODED;
import MediaType.TEXT_HTML;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MultiValueMap;


/**
 * Basic integration tests for demo application.
 *
 * @author Dave Syer
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SampleWebSecureJdbcApplicationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    public void testHome() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(TEXT_HTML));
        ResponseEntity<String> entity = this.restTemplate.exchange("/", GET, new org.springframework.http.HttpEntity<Void>(headers), String.class);
        assertThat(entity.getStatusCode()).isEqualTo(FOUND);
        assertThat(entity.getHeaders().getLocation().toString()).endsWith(((this.port) + "/login"));
    }

    @Test
    public void testLoginPage() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(TEXT_HTML));
        ResponseEntity<String> entity = this.restTemplate.exchange("/login", GET, new org.springframework.http.HttpEntity<Void>(headers), String.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
        assertThat(entity.getBody()).contains("_csrf");
    }

    @Test
    public void testLogin() {
        HttpHeaders headers = getHeaders();
        headers.setAccept(Arrays.asList(TEXT_HTML));
        headers.setContentType(APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> form = new org.springframework.util.LinkedMultiValueMap();
        form.set("username", "user");
        form.set("password", "user");
        ResponseEntity<String> entity = this.restTemplate.exchange("/login", POST, new org.springframework.http.HttpEntity(form, headers), String.class);
        assertThat(entity.getStatusCode()).isEqualTo(FOUND);
        assertThat(entity.getHeaders().getLocation().toString()).endsWith(((this.port) + "/"));
        assertThat(entity.getHeaders().get("Set-Cookie")).isNotNull();
    }

    @Test
    public void testCss() {
        ResponseEntity<String> entity = this.restTemplate.getForEntity("/css/bootstrap.min.css", String.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
        assertThat(entity.getBody()).contains("body");
    }
}

