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
package sample.hateoas;


import HttpHeaders.ACCEPT;
import HttpMethod.GET;
import HttpStatus.OK;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SampleHateoasApplicationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void hasHalLinks() {
        ResponseEntity<String> entity = this.restTemplate.getForEntity("/customers/1", String.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
        assertThat(entity.getBody()).startsWith(("{\"id\":1,\"firstName\":\"Oliver\"" + ",\"lastName\":\"Gierke\""));
        assertThat(entity.getBody()).contains("_links\":{\"self\":{\"href\"");
    }

    @Test
    public void producesJsonWhenXmlIsPreferred() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(ACCEPT, "application/xml;q=0.9,application/json;q=0.8");
        HttpEntity<?> request = new HttpEntity(headers);
        ResponseEntity<String> response = this.restTemplate.exchange("/customers/1", GET, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.parseMediaType("application/json;charset=UTF-8"));
    }
}

