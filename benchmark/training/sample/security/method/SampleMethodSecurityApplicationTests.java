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
package sample.security.method;


import HttpMethod.GET;
import HttpMethod.POST;
import HttpStatus.FORBIDDEN;
import HttpStatus.FOUND;
import HttpStatus.OK;
import HttpStatus.UNAUTHORIZED;
import MediaType.APPLICATION_JSON;
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
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MultiValueMap;


/**
 * Basic integration tests for demo application.
 *
 * @author Dave Syer
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SampleMethodSecurityApplicationTests {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testHome() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(TEXT_HTML));
        ResponseEntity<String> entity = this.restTemplate.exchange("/", GET, new org.springframework.http.HttpEntity<Void>(headers), String.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
        assertThat(entity.getBody()).contains("<title>Login");
    }

    @Test
    public void testLogin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(TEXT_HTML));
        MultiValueMap<String, String> form = new org.springframework.util.LinkedMultiValueMap();
        form.set("username", "admin");
        form.set("password", "admin");
        getCsrf(form, headers);
        ResponseEntity<String> entity = this.restTemplate.exchange("/login", POST, new org.springframework.http.HttpEntity(form, headers), String.class);
        assertThat(entity.getStatusCode()).isEqualTo(FOUND);
        assertThat(entity.getHeaders().getLocation().toString()).isEqualTo((("http://localhost:" + (this.port)) + "/"));
    }

    @Test
    public void testDenied() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(TEXT_HTML));
        MultiValueMap<String, String> form = new org.springframework.util.LinkedMultiValueMap();
        form.set("username", "user");
        form.set("password", "user");
        getCsrf(form, headers);
        ResponseEntity<String> entity = this.restTemplate.exchange("/login", POST, new org.springframework.http.HttpEntity(form, headers), String.class);
        assertThat(entity.getStatusCode()).isEqualTo(FOUND);
        String cookie = entity.getHeaders().getFirst("Set-Cookie");
        headers.set("Cookie", cookie);
        ResponseEntity<String> page = this.restTemplate.exchange(entity.getHeaders().getLocation(), GET, new org.springframework.http.HttpEntity<Void>(headers), String.class);
        assertThat(page.getStatusCode()).isEqualTo(FORBIDDEN);
        assertThat(page.getBody()).contains("Access denied");
    }

    @Test
    public void testManagementProtected() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(APPLICATION_JSON));
        ResponseEntity<String> entity = this.restTemplate.exchange("/actuator/beans", GET, new org.springframework.http.HttpEntity<Void>(headers), String.class);
        assertThat(entity.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void testManagementAuthorizedAccess() {
        BasicAuthenticationInterceptor basicAuthInterceptor = new BasicAuthenticationInterceptor("admin", "admin");
        this.restTemplate.getRestTemplate().getInterceptors().add(basicAuthInterceptor);
        try {
            ResponseEntity<String> entity = this.restTemplate.getForEntity("/actuator/beans", String.class);
            assertThat(entity.getStatusCode()).isEqualTo(OK);
        } finally {
            this.restTemplate.getRestTemplate().getInterceptors().remove(basicAuthInterceptor);
        }
    }
}

