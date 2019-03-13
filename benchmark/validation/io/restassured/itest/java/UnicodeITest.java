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


import ContentType.JSON;
import io.restassured.RestAssured;
import io.restassured.config.DecoderConfig;
import io.restassured.itest.java.support.WithJetty;
import java.util.HashMap;
import org.hamcrest.Matchers;
import org.junit.Test;


public class UnicodeITest extends WithJetty {
    @Test
    public void pure_body_expectations_work_for_unicode_content() {
        RestAssured.given().config(RestAssured.config().decoderConfig(DecoderConfig.decoderConfig().defaultContentCharset("UTF-8"))).when().get("/utf8-body-json").then().body(Matchers.containsString("? ?"));
    }

    @Test
    public void json_body_expectations_work_for_unicode_content() {
        RestAssured.given().config(RestAssured.config().decoderConfig(DecoderConfig.decoderConfig().defaultContentCharset("UTF-8"))).when().get("/utf8-body-json").then().body("value", Matchers.equalTo("? ?"));
    }

    @Test
    public void xml_body_expectations_work_for_unicode_content() {
        RestAssured.given().config(RestAssured.config().decoderConfig(DecoderConfig.decoderConfig().defaultContentCharset("UTF-8"))).when().get("/utf8-body-xml").then().body("value", Matchers.equalTo("? ?"));
    }

    @Test
    public void unicode_values_works_in_json_content() {
        RestAssured.given().contentType(JSON).body(new HashMap<String, String>() {
            {
                put("title", "??????");
            }
        }).when().post("/reflect").then().statusCode(200).body("title", Matchers.equalTo("??????"));
    }
}

