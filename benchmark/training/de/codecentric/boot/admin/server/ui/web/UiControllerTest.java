/**
 * Copyright 2014-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codecentric.boot.admin.server.ui.web;


import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;


public class UiControllerTest {
    @Test
    public void should_use_default_url() throws Exception {
        MockMvc mockMvc = setupController("");
        mockMvc.perform(get("http://example/")).andExpect(status().isOk()).andExpect(view().name("index")).andExpect(model().attribute("baseUrl", "http://example/"));
    }

    @Test
    public void should_use_path_from_public_url() throws Exception {
        MockMvc mockMvc = setupController("/public");
        mockMvc.perform(get("http://example/")).andExpect(status().isOk()).andExpect(view().name("index")).andExpect(model().attribute("baseUrl", "http://example/public/"));
    }

    @Test
    public void should_use_host_and_path_from_public_url() throws Exception {
        MockMvc mockMvc = setupController("http://public/public");
        mockMvc.perform(get("http://example/")).andExpect(status().isOk()).andExpect(view().name("index")).andExpect(model().attribute("baseUrl", "http://public/public/"));
    }

    @Test
    public void should_use_scheme_host_and_path_from_public_url() throws Exception {
        MockMvc mockMvc = setupController("https://public/public");
        mockMvc.perform(get("http://example/")).andExpect(status().isOk()).andExpect(view().name("index")).andExpect(model().attribute("baseUrl", "https://public/public/"));
    }
}

