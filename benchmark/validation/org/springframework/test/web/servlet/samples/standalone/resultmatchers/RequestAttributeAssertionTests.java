/**
 * Copyright 2002-2012 the original author or authors.
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
package org.springframework.test.web.servlet.samples.standalone.resultmatchers;


import HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE;
import HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE;
import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;


/**
 * Examples of expectations on created request attributes.
 *
 * @author Rossen Stoyanchev
 */
public class RequestAttributeAssertionTests {
    private MockMvc mockMvc;

    @Test
    public void testRequestAttributeEqualTo() throws Exception {
        this.mockMvc.perform(get("/main/1").servletPath("/main")).andExpect(MockMvcResultMatchers.request().attribute(BEST_MATCHING_PATTERN_ATTRIBUTE, "/{id}")).andExpect(MockMvcResultMatchers.request().attribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, "/1")).andExpect(MockMvcResultMatchers.request().attribute(BEST_MATCHING_PATTERN_ATTRIBUTE, equalTo("/{id}"))).andExpect(MockMvcResultMatchers.request().attribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, equalTo("/1")));
    }

    @Test
    public void testRequestAttributeMatcher() throws Exception {
        String producibleMediaTypes = HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE;
        this.mockMvc.perform(get("/1")).andExpect(MockMvcResultMatchers.request().attribute(producibleMediaTypes, hasItem(MediaType.APPLICATION_JSON))).andExpect(MockMvcResultMatchers.request().attribute(producibleMediaTypes, not(hasItem(MediaType.APPLICATION_XML))));
    }

    @Controller
    private static class SimpleController {
        @RequestMapping(value = "/{id}", produces = "application/json")
        public String show() {
            return "view";
        }
    }
}

