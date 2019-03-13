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
package sample;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;


/**
 *
 *
 * @author Rob Winch
 * @since 5.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class HelloWebfluxFnApplicationTests {
    WebTestClient rest;

    @Test
    public void basicWhenNoCredentialsThenUnauthorized() throws Exception {
        this.rest.get().uri("/").exchange().expectStatus().isUnauthorized();
    }

    @Test
    public void basicWhenValidCredentialsThenOk() throws Exception {
        this.rest.get().uri("/").headers(userCredentials()).exchange().expectStatus().isOk().expectBody().json("{\"message\":\"Hello user!\"}");
    }

    @Test
    public void basicWhenInvalidCredentialsThenUnauthorized() throws Exception {
        this.rest.get().uri("/").headers(invalidCredentials()).exchange().expectStatus().isUnauthorized().expectBody().isEmpty();
    }

    @Test
    public void mockSupportWhenMutateWithMockUserThenOk() throws Exception {
        this.rest.mutateWith(mockUser()).get().uri("/").exchange().expectStatus().isOk().expectBody().json("{\"message\":\"Hello user!\"}");
    }

    @Test
    @WithMockUser
    public void mockSupportWhenWithMockUserThenOk() throws Exception {
        this.rest.get().uri("/").exchange().expectStatus().isOk().expectBody().json("{\"message\":\"Hello user!\"}");
    }
}

