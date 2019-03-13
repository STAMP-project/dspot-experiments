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


import HttpStatus.FORBIDDEN;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class HelloWebfluxMethodApplicationTests {
    WebTestClient rest;

    @Test
    public void messageWhenNotAuthenticated() throws Exception {
        this.rest.get().uri("/message").exchange().expectStatus().isUnauthorized();
    }

    // --- Basic Authentication ---
    @Test
    public void messageWhenUserThenForbidden() throws Exception {
        this.rest.get().uri("/message").headers(robsCredentials()).exchange().expectStatus().isEqualTo(FORBIDDEN);
    }

    @Test
    public void messageWhenAdminThenOk() throws Exception {
        this.rest.get().uri("/message").headers(adminCredentials()).exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("Hello World!");
    }

    // --- WithMockUser ---
    @Test
    @WithMockUser
    public void messageWhenWithMockUserThenForbidden() throws Exception {
        this.rest.get().uri("/message").exchange().expectStatus().isEqualTo(FORBIDDEN);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void messageWhenWithMockAdminThenOk() throws Exception {
        this.rest.get().uri("/message").exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("Hello World!");
    }

    // --- mutateWith mockUser ---
    @Test
    public void messageWhenMutateWithMockUserThenForbidden() throws Exception {
        this.rest.mutateWith(mockUser()).get().uri("/message").exchange().expectStatus().isEqualTo(FORBIDDEN);
    }

    @Test
    public void messageWhenMutateWithMockAdminThenOk() throws Exception {
        this.rest.mutateWith(mockUser().roles("ADMIN")).get().uri("/message").exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("Hello World!");
    }
}

