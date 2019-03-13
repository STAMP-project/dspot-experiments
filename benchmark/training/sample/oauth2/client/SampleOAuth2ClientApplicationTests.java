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
package sample.oauth2.client;


import HttpStatus.FOUND;
import HttpStatus.OK;
import SpringBootTest.WebEnvironment;
import java.net.URI;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = { "APP-CLIENT-ID=my-client-id", "APP-CLIENT-SECRET=my-client-secret", "YAHOO-CLIENT-ID=my-yahoo-client-id", "YAHOO-CLIENT-SECRET=my-yahoo-client-secret" })
public class SampleOAuth2ClientApplicationTests {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void everythingShouldRedirectToLogin() {
        ResponseEntity<String> entity = this.restTemplate.getForEntity("/", String.class);
        assertThat(entity.getStatusCode()).isEqualTo(FOUND);
        assertThat(entity.getHeaders().getLocation()).isEqualTo(URI.create((("http://localhost:" + (this.port)) + "/login")));
    }

    @Test
    public void loginShouldHaveAllOAuth2ClientsToChooseFrom() {
        ResponseEntity<String> entity = this.restTemplate.getForEntity("/login", String.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
        assertThat(entity.getBody()).contains("/oauth2/authorization/yahoo");
        assertThat(entity.getBody()).contains("/oauth2/authorization/github-client-1");
        assertThat(entity.getBody()).contains("/oauth2/authorization/github-client-2");
        assertThat(entity.getBody()).contains("/oauth2/authorization/github-repos");
    }
}

