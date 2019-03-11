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


import ContentType.JSON;
import io.restassured.config.EncoderConfig;
import java.nio.charset.StandardCharsets;
import org.hamcrest.Matchers;
import org.junit.Test;


public class ContentTypeTest {
    @Test
    public void adds_default_charset_to_content_type_by_default() {
        RestAssuredWebTestClient.given().contentType(JSON).when().get("/contentType").then().body("requestContentType", Matchers.equalTo(("application/json;charset=" + (RestAssuredWebTestClient.config().getEncoderConfig().defaultContentCharset()))));
    }

    @Test
    public void adds_specific_charset_to_content_type_by_default() {
        RestAssuredWebTestClient.given().config(RestAssuredWebTestClient.config().encoderConfig(EncoderConfig.encoderConfig().defaultCharsetForContentType(StandardCharsets.UTF_16.toString(), JSON))).contentType(JSON).when().get("/contentType").then().statusCode(200).body("requestContentType", Matchers.equalTo(("application/json;charset=" + (StandardCharsets.UTF_16.toString())))).body("requestContentType", Matchers.not(Matchers.contains(RestAssuredWebTestClient.config().getEncoderConfig().defaultContentCharset())));
    }

    @Test
    public void doesnt_add_default_charset_to_content_type_if_charset_is_defined_explicitly() {
        RestAssuredWebTestClient.given().contentType(JSON.withCharset("UTF-16")).when().get("/contentType").then().statusCode(200).body("requestContentType", Matchers.equalTo("application/json;charset=UTF-16"));
    }

    @Test
    public void doesnt_add_default_charset_to_content_type_if_configured_not_to_do_so() {
        RestAssuredWebTestClient.given().config(RestAssuredWebTestClient.config().encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false))).contentType(JSON).when().get("/contentType").then().statusCode(200).body("requestContentType", Matchers.equalTo("application/json"));
    }
}

