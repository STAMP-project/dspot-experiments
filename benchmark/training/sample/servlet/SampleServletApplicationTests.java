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
package sample.servlet;


import HttpMethod.GET;
import HttpStatus.OK;
import HttpStatus.UNAUTHORIZED;
import MediaType.APPLICATION_JSON;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Basic integration tests for demo application.
 *
 * @author Dave Syer
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SampleServletApplicationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testHomeIsSecure() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(APPLICATION_JSON));
        ResponseEntity<String> entity = this.restTemplate.exchange("/", GET, new org.springframework.http.HttpEntity<Void>(headers), String.class);
        assertThat(entity.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void testHome() {
        ResponseEntity<String> entity = this.restTemplate.withBasicAuth("user", getPassword()).getForEntity("/", String.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
        assertThat(entity.getBody()).isEqualTo("Hello World");
    }
}

