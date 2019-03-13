/**
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.restassured.module.mockmvc;


import MediaType.APPLICATION_FORM_URLENCODED;
import io.restassured.module.mockmvc.http.GreetingController;
import io.restassured.module.mockmvc.http.PostController;
import io.restassured.path.json.JsonPath;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;


public class GreetingControllerVanillaMockMvcTest {
    @Test
    public void mock_mvc_example_for_get_greeting_controller() throws Exception {
        MockMvc mockMvc = standaloneSetup(new GreetingController()).setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
        String contentAsString = mockMvc.perform(get("/greeting?name={name}", "Johan").accept(APPLICATION_JSON)).andReturn().getResponse().getContentAsString();
        JsonPath jsonPath = new JsonPath(contentAsString);
        MatcherAssert.assertThat(jsonPath.getInt("id"), Matchers.equalTo(1));
        MatcherAssert.assertThat(jsonPath.getString("content"), Matchers.equalTo("Hello, Johan!"));
    }

    @Test
    public void mock_mvc_example_for_post_greeting_controller() throws Exception {
        MockMvc mockMvc = standaloneSetup(new PostController()).setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
        String contentAsString = mockMvc.perform(post("/greetingPost").param("name", "Johan").contentType(APPLICATION_FORM_URLENCODED)).andReturn().getResponse().getContentAsString();
        JsonPath jsonPath = new JsonPath(contentAsString);
        MatcherAssert.assertThat(jsonPath.getInt("id"), Matchers.equalTo(1));
        MatcherAssert.assertThat(jsonPath.getString("content"), Matchers.equalTo("Hello, Johan!"));
    }
}

