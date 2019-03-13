/**
 * Copyright 2016-2018 the original author or authors.
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
package io.restassured.module.webtestclient;


import Parser.JSON;
import io.restassured.builder.ResponseSpecBuilder;
import org.hamcrest.Matchers;
import org.junit.Test;

import static RestAssuredWebTestClient.responseSpecification;


public class ParserTest {
    @Test
    public void using_static_parser_its_possible_to_parse_unknown_content_types() {
        responseSpecification = new ResponseSpecBuilder().registerParser("some/thing", JSON).build();
        RestAssuredWebTestClient.given().param("param", "my param").when().get("/parserWithUnknownContentType").then().statusCode(200).contentType(Matchers.equalTo("some/thing;charset=UTF-8")).body("param", Matchers.equalTo("my param"));
    }

    @Test
    public void using_non_static_parser_its_possible_to_parse_unknown_content_types() {
        RestAssuredWebTestClient.given().param("param", "my param").when().get("/parserWithUnknownContentType").then().parser("some/thing", JSON).statusCode(200).contentType(Matchers.equalTo("some/thing;charset=UTF-8")).body("param", Matchers.equalTo("my param"));
    }
}

