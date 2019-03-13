/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.boot.test.autoconfigure.security;


import HttpHeaders.AUTHORIZATION;
import MediaType.APPLICATION_JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Base64Utils;


/**
 * Integration tests for MockMvc security.
 *
 * @author Andy Wilkinson
 */
@WebMvcTest
@RunWith(SpringRunner.class)
@TestPropertySource(properties = { "debug=true" })
public class MockMvcSecurityIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "test", password = "test", roles = "USER")
    public void okResponseWithMockUser() throws Exception {
        this.mockMvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    public void unauthorizedResponseWithNoUser() throws Exception {
        this.mockMvc.perform(get("/").accept(APPLICATION_JSON)).andExpect(status().isUnauthorized());
    }

    @Test
    public void okResponseWithBasicAuthCredentialsForKnownUser() throws Exception {
        this.mockMvc.perform(get("/").header(AUTHORIZATION, ("Basic " + (Base64Utils.encodeToString("user:secret".getBytes()))))).andExpect(status().isOk());
    }
}

