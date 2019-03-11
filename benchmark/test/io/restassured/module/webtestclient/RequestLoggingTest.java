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


import io.restassured.module.webtestclient.config.RestAssuredWebTestClientConfig;
import io.restassured.module.webtestclient.setup.BasePathController;
import io.restassured.module.webtestclient.setup.GreetingController;
import io.restassured.module.webtestclient.setup.PostController;
import java.io.StringWriter;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static RestAssuredWebTestClient.basePath;


public class RequestLoggingTest {
    private StringWriter writer;

    @Test
    public void logging_param_works() {
        RestAssuredWebTestClient.given().log().all().standaloneSetup(new PostController()).param("name", "Johan").when().post("/greetingPost").then().body("id", Matchers.equalTo(1)).body("content", Matchers.equalTo("Hello, Johan!"));
        Assert.assertThat(writer.toString(), Matchers.equalTo(String.format(("Request method:\tPOST%n" + ((((((((("Request URI:\thttp://localhost:8080/greetingPost%n" + "Proxy:\t\t\t<none>%n") + "Request params:\tname=Johan%n") + "Query params:\t<none>%n") + "Form params:\t<none>%n") + "Path params:\t<none>%n") + "Headers:\t\tContent-Type=application/x-www-form-urlencoded;charset=%s%n") + "Cookies:\t\t<none>%n") + "Multiparts:\t\t<none>%n") + "Body:\t\t\t<none>%n")), RestAssuredWebTestClientConfig.config().getEncoderConfig().defaultContentCharset())));
    }

    @Test
    public void logging_query_param_works() {
        RestAssuredWebTestClient.given().log().all().standaloneSetup(new GreetingController()).queryParam("name", "Johan").when().get("/greeting").then().body("id", Matchers.equalTo(1)).body("content", Matchers.equalTo("Hello, Johan!"));
        Assert.assertThat(writer.toString(), Matchers.equalTo(String.format(("Request method:\tGET%n" + (((((((("Request URI:\thttp://localhost:8080/greeting?name=Johan%nProxy:\t\t\t<none>%n" + "Request params:\t<none>%n") + "Query params:\tname=Johan%n") + "Form params:\t<none>%n") + "Path params:\t<none>%n") + "Headers:\t\t<none>%n") + "Cookies:\t\t<none>%n") + "Multiparts:\t\t<none>%n") + "Body:\t\t\t<none>%n")))));
    }

    @Test
    public void logging_form_param_works() {
        RestAssuredWebTestClient.given().log().all().standaloneSetup(new PostController()).formParam("name", "Johan").when().post("/greetingPost").then().body("id", Matchers.equalTo(1)).body("content", Matchers.equalTo("Hello, Johan!"));
        Assert.assertThat(writer.toString(), Matchers.equalTo(String.format(("Request method:\tPOST%n" + ((((((((("Request URI:\thttp://localhost:8080/greetingPost%n" + "Proxy:\t\t\t<none>%n") + "Request params:\t<none>%n") + "Query params:\t<none>%n") + "Form params:\tname=Johan%n") + "Path params:\t<none>%n") + "Headers:\t\tContent-Type=application/x-www-form-urlencoded;charset=%s%n") + "Cookies:\t\t<none>%n") + "Multiparts:\t\t<none>%n") + "Body:\t\t\t<none>%n")), RestAssuredWebTestClientConfig.config().getEncoderConfig().defaultContentCharset())));
    }

    @Test
    public void can_supply_string_as_body_for_post() {
        RestAssuredWebTestClient.given().standaloneSetup(new PostController()).log().all().body("a string").when().post("/stringBody").then().body(Matchers.equalTo("a string"));
        Assert.assertThat(writer.toString(), Matchers.equalTo(String.format(("Request method:\tPOST%n" + (((((((((("Request URI:\thttp://localhost:8080/stringBody%n" + "Proxy:\t\t\t<none>%n") + "Request params:\t<none>%n") + "Query params:\t<none>%n") + "Form params:\t<none>%n") + "Path params:\t<none>%n") + "Headers:\t\t<none>%n") + "Cookies:\t\t<none>%n") + "Multiparts:\t\t<none>%n") + "Body:%n") + "a string%n")))));
    }

    @Test
    public void base_path_is_prepended_to_path_when_logging() {
        basePath = "/my-path";
        try {
            RestAssuredWebTestClient.given().log().all().standaloneSetup(new BasePathController()).param("name", "Johan").when().get("/greetingPath").then().statusCode(200).body("content", Matchers.equalTo("Hello, Johan!"));
        } finally {
            RestAssuredWebTestClient.reset();
        }
        Assert.assertThat(writer.toString(), Matchers.equalTo(String.format(("Request method:\tGET%n" + ((((((((("Request URI:\thttp://localhost:8080/my-path/greetingPath?name=Johan%n" + "Proxy:\t\t\t<none>%n") + "Request params:\tname=Johan%n") + "Query params:\t<none>%n") + "Form params:\t<none>%n") + "Path params:\t<none>%n") + "Headers:\t\t<none>%n") + "Cookies:\t\t<none>%n") + "Multiparts:\t\t<none>%n") + "Body:\t\t\t<none>%n")))));
    }

    @Test
    public void logging_if_request_validation_fails_works() {
        try {
            RestAssuredWebTestClient.given().log().ifValidationFails().standaloneSetup(new PostController()).param("name", "Johan").when().post("/greetingPost").then().body("id", Matchers.equalTo(1)).body("content", Matchers.equalTo("Hello, Johan2!"));
            Assert.fail("Should throw AssertionError");
        } catch (AssertionError e) {
            Assert.assertThat(writer.toString(), Matchers.equalTo(String.format(("Request method:\tPOST%n" + ((((((((("Request URI:\thttp://localhost:8080/greetingPost%n" + "Proxy:\t\t\t<none>%n") + "Request params:\tname=Johan%n") + "Query params:\t<none>%n") + "Form params:\t<none>%n") + "Path params:\t<none>%n") + "Headers:\t\tContent-Type=application/x-www-form-urlencoded;charset=%s%n") + "Cookies:\t\t<none>%n") + "Multiparts:\t\t<none>%n") + "Body:\t\t\t<none>%n")), RestAssuredWebTestClientConfig.config().getEncoderConfig().defaultContentCharset())));
        }
    }

    @Test
    public void doesnt_log_if_request_validation_succeeds_when_request_logging_if_validation_fails_is_enabled() {
        RestAssuredWebTestClient.given().log().ifValidationFails().standaloneSetup(new PostController()).param("name", "Johan").when().post("/greetingPost").then().body("id", Matchers.equalTo(1)).body("content", Matchers.equalTo("Hello, Johan!"));
        Assert.assertThat(writer.toString(), Matchers.isEmptyString());
    }
}

