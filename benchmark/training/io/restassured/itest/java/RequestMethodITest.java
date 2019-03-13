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
package io.restassured.itest.java;


import io.restassured.itest.java.support.WithJetty;
import org.hamcrest.Matchers;
import org.junit.Test;


public class RequestMethodITest extends WithJetty {
    @Test
    public void request_method_accepts_enum_verb() {
        when().request(GET, "/lotto").then().body("lotto.lottoId", Matchers.is(5));
    }

    @Test
    public void request_method_accepts_enum_verb_and_unnamed_path_params() {
        when().request(GET, "/{firstName}/{lastName}", "John", "Doe").then().body("firstName", Matchers.equalTo("John")).body("lastName", Matchers.equalTo("Doe"));
    }

    @Test
    public void request_method_accepts_string_verb() {
        when().request(" opTions  ", "/greetXML").then().body("greeting.firstName", Matchers.is("John")).body("greeting.lastName", Matchers.is("Doe"));
    }

    @Test
    public void request_method_accepts_string_verb_and_unnamed_path_params() {
        when().request("GET", "/{firstName}/{lastName}", "John", "Doe").then().body("firstName", Matchers.equalTo("John")).body("lastName", Matchers.equalTo("Doe"));
    }

    @Test
    public void static_request_method_accepts_string_verb() {
        request("get", "/John/Doe").then().assertThat().body("firstName", Matchers.equalTo("John")).and().body("lastName", Matchers.equalTo("Doe"));
    }

    @Test
    public void static_request_method_accepts_enum_verb_and_path_params() {
        request(GET, "/{firstName}/{lastName}", "John", "Doe").then().assertThat().body("firstName", Matchers.equalTo("John")).and().body("lastName", Matchers.equalTo("Doe"));
    }
}

