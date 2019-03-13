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


import org.hamcrest.Matchers;
import org.junit.Test;

import static RestAssuredMockMvc.basePath;


public class BasePathTest {
    @Test
    public void base_path_is_prepended_to_path() {
        basePath = "/my-path";
        RestAssuredMockMvc.given().param("name", "Johan").when().get("/greetingPath").then().statusCode(200).body("content", Matchers.equalTo("Hello, Johan!"));
    }

    @Test
    public void default_base_path_is_slash() {
        RestAssuredMockMvc.given().param("name", "Johan").when().get().then().statusCode(200).body("content", Matchers.equalTo("Hello, Johan!"));
    }

    @Test
    public void double_slashes_are_prevented() {
        basePath = "/my-path/";
        RestAssuredMockMvc.given().param("name", "Johan").when().get("/greetingPath").then().statusCode(200).body("content", Matchers.equalTo("Hello, Johan!"));
    }

    @Test
    public void base_path_can_end_with_slash_and_path_doesnt_have_to_begin_with_slash() {
        basePath = "/my-path/";
        RestAssuredMockMvc.given().param("name", "Johan").when().get("greetingPath").then().statusCode(200).body("content", Matchers.equalTo("Hello, Johan!"));
    }

    @Test
    public void base_path_doesnt_have_to_end_with_slash_even_though_path_doesnt_begin_with_slash2() {
        basePath = "/my-path";
        RestAssuredMockMvc.given().param("name", "Johan").when().get("greetingPath").then().statusCode(200).body("content", Matchers.equalTo("Hello, Johan!"));
    }
}

