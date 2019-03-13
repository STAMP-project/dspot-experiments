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


import ContentType.URLENC;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.module.webtestclient.config.RestAssuredWebTestClientConfig;
import io.restassured.module.webtestclient.setup.PostController;
import io.restassured.module.webtestclient.specification.WebTestClientRequestSpecBuilder;
import java.io.PrintStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import org.apache.commons.io.output.WriterOutputStream;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.util.UriUtils;

import static RestAssuredWebTestClient.config;
import static RestAssuredWebTestClient.requestSpecification;
import static RestAssuredWebTestClient.responseSpecification;


public class LoggingIfValidationFailsTest {
    private StringWriter writer;

    private PrintStream captor;

    @Test
    public void logging_of_both_request_and_response_validation_works_when_test_fails() {
        config = new RestAssuredWebTestClientConfig().logConfig(new LogConfig(captor, true).enableLoggingOfRequestAndResponseIfValidationFails());
        try {
            RestAssuredWebTestClient.given().standaloneSetup(new PostController()).param("name", "Johan").when().post("/greetingPost").then().body("id", equalTo(1)).body("content", equalTo("Hello, Johan2!"));
            Assert.fail("Should throw AssertionError");
        } catch (AssertionError e) {
            Assert.assertThat(writer.toString(), equalTo(String.format(("Request method:\tPOST%n" + (((((((((((((((((("Request URI:\thttp://localhost:8080/greetingPost%n" + "Proxy:\t\t\t<none>%n") + "Request params:\tname=Johan%n") + "Query params:\t<none>%n") + "Form params:\t<none>%n") + "Path params:\t<none>%n") + "Headers:\t\tContent-Type=application/x-www-form-urlencoded;charset=%s%n") + "Cookies:\t\t<none>%n") + "Multiparts:\t\t<none>%n") + "Body:\t\t\t<none>%n") + "%n") + "200%n") + "Content-Type: application/json;charset=UTF-8%n") + "Content-Length: 34%n") + "%n") + "{\n") + "    \"id\": 1,\n") + "    \"content\": \"Hello, Johan!\"\n") + "}%n")), config().getEncoderConfig().defaultContentCharset())));
        }
    }

    @Test
    public void logging_of_both_request_with_uri_function_and_response_validation_works_when_test_fails() {
        config = new RestAssuredWebTestClientConfig().logConfig(new LogConfig(captor, true).enableLoggingOfRequestAndResponseIfValidationFails());
        try {
            RestAssuredWebTestClient.given().standaloneSetup(new PostController()).contentType(URLENC).when().post(( uriBuilder) -> uriBuilder.path("/greetingPost").queryParam("name", "Johan").build()).then().body("id", equalTo(1)).body("content", equalTo("Hello, Johan2!"));
            Assert.fail("Should throw AssertionError");
        } catch (AssertionError e) {
            Assert.assertThat(writer.toString(), StringContains.containsString(UriUtils.encode("Request from uri function", Charset.defaultCharset())));
            Assert.assertThat(writer.toString(), StringContains.containsString(String.format("Request method:\tPOST%n")));
            Assert.assertThat(writer.toString(), StringContains.containsString(String.format(("Proxy:\t\t\t<none>%n" + (((((((((((((((("Request params:\t<none>%n" + "Query params:\t<none>%n") + "Form params:\t<none>%n") + "Path params:\t<none>%n") + "Headers:\t\tContent-Type=application/x-www-form-urlencoded%n") + "Cookies:\t\t<none>%n") + "Multiparts:\t\t<none>%n") + "Body:\t\t\t<none>%n") + "%n") + "200%n") + "Content-Type: application/json;charset=UTF-8%n") + "Content-Length: 34%n") + "%n") + "{\n") + "    \"id\": 1,\n") + "    \"content\": \"Hello, Johan!\"\n") + "}%n")))));
        }
    }

    @Test
    public void logging_of_both_request_and_response_validation_works_when_test_fails_when_configured_with_log_detail() {
        config = new RestAssuredWebTestClientConfig().logConfig(new LogConfig(captor, true).enableLoggingOfRequestAndResponseIfValidationFails(HEADERS));
        try {
            RestAssuredWebTestClient.given().standaloneSetup(new PostController()).param("name", "Johan").when().post("/greetingPost").then().body("id", equalTo(1)).body("content", equalTo("Hello, Johan2!"));
            Assert.fail("Should throw AssertionError");
        } catch (AssertionError e) {
            Assert.assertThat(writer.toString(), equalTo(String.format(("" + ((("Headers:\t\tContent-Type=application/x-www-form-urlencoded;charset=%s%n" + "%n") + "Content-Type: application/json;charset=UTF-8%n") + "Content-Length: 34%n")), config().getEncoderConfig().defaultContentCharset())));
        }
    }

    @Test
    public void logging_of_both_request_and_response_validation_works_when_test_fails_when_using_static_response_and_request_specs_declared_before_enable_logging() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        responseSpecification = new ResponseSpecBuilder().expectStatusCode(200).build();
        requestSpecification = new WebTestClientRequestSpecBuilder().setConfig(config().logConfig(new LogConfig(captor, true))).addHeader("Api-Key", "1234").build();
        RestAssuredWebTestClient.enableLoggingOfRequestAndResponseIfValidationFails(HEADERS);
        try {
            RestAssuredWebTestClient.given().standaloneSetup(new PostController()).param("name", "Johan").when().post("/greetingPost").then().body("id", equalTo(1)).body("content", equalTo("Hello, Johan2!"));
            Assert.fail("Should throw AssertionError");
        } catch (AssertionError e) {
            Assert.assertThat(writer.toString(), equalTo(String.format(("Headers:\t\tApi-Key=1234%n" + ((("\t\t\t\tContent-Type=application/x-www-form-urlencoded;charset=%s%n" + "%n") + "Content-Type: application/json;charset=UTF-8%n") + "Content-Length: 34%n")), RestAssuredWebTestClientConfig.config().getEncoderConfig().defaultContentCharset())));
        }
    }

    @Test
    public void doesnt_log_if_request_or_response_when_validation_succeeds_when_request_and_response_logging_if_validation_fails_is_enabled() {
        config = new RestAssuredWebTestClientConfig().logConfig(new LogConfig(captor, true).enableLoggingOfRequestAndResponseIfValidationFails());
        RestAssuredWebTestClient.given().standaloneSetup(new PostController()).param("name", "Johan").when().post("/greetingPost").then().body("id", equalTo(1)).body("content", equalTo("Hello, Johan!"));
        Assert.assertThat(writer.toString(), isEmptyString());
    }

    @Test
    public void logging_is_applied_when_using_non_static_response_specifications() {
        config = new RestAssuredWebTestClientConfig().logConfig(new LogConfig(captor, true).enableLoggingOfRequestAndResponseIfValidationFails());
        try {
            RestAssuredWebTestClient.given().standaloneSetup(new PostController()).param("name", "Johan").when().post("/greetingPost").then().spec(new ResponseSpecBuilder().expectBody("id", equalTo(2)).expectBody("content", equalTo("Hello, Johan2!")).build());
            Assert.fail("Should throw AssertionError");
        } catch (AssertionError e) {
            Assert.assertThat(writer.toString(), not(isEmptyOrNullString()));
        }
    }
}

