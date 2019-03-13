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


import io.restassured.config.LogConfig;
import io.restassured.module.webtestclient.setup.support.Greeting;
import java.io.PrintStream;
import java.io.StringWriter;
import org.apache.commons.io.output.WriterOutputStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PutTest {
    @Test
    public void doesnt_automatically_add_x_www_form_urlencoded_as_content_type_when_putting_params() {
        StringWriter writer = new StringWriter();
        PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        RestAssuredWebTestClient.given().config(newConfig().logConfig(new LogConfig(captor, true))).param("name", "Johan").when().put("/greetingPut").then().log().all().statusCode(415);
        Assert.assertThat(writer.toString(), Matchers.equalTo(String.format("415: Unsupported Media Type%n")));
    }

    @Test
    public void automatically_adds_x_www_form_urlencoded_as_content_type_when_putting_form_params() {
        RestAssuredWebTestClient.given().formParam("name", "Johan").when().put("/greetingPut").then().body("id", Matchers.equalTo(1)).body("content", Matchers.equalTo("Hello, Johan!"));
    }

    @Test
    public void can_supply_string_as_body_for_put() {
        RestAssuredWebTestClient.given().body("a string").when().put("/stringBody").then().body(Matchers.equalTo("a string"));
    }

    @Test
    public void can_supply_object_as_body_and_serialize_as_json() {
        Greeting greeting = new Greeting();
        greeting.setFirstName("John");
        greeting.setLastName("Doe");
        RestAssuredWebTestClient.given().contentType(JSON).body(greeting).when().put("/jsonReflect").then().statusCode(200).body("firstName", Matchers.equalTo("John")).body("lastName", Matchers.equalTo("Doe"));
    }
}

