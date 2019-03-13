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
package org.springframework.boot.test.autoconfigure.web.servlet.mockmvc;


import HttpHeaders.CONTENT_TYPE;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


/**
 * Integration tests for {@link WebMvcTest} and Spring HATEOAS.
 *
 * @author Andy Wilkinson
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@WithMockUser
public class WebMvcTestHateoasIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void plainResponse() throws Exception {
        this.mockMvc.perform(get("/hateoas/plain")).andExpect(header().string(CONTENT_TYPE, "application/json;charset=UTF-8"));
    }

    @Test
    public void hateoasResponse() throws Exception {
        this.mockMvc.perform(get("/hateoas/resource")).andExpect(header().string(CONTENT_TYPE, "application/hal+json;charset=UTF-8"));
    }
}

