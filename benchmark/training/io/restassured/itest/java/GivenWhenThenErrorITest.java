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


import io.restassured.RestAssured;
import io.restassured.itest.java.support.WithJetty;
import org.hamcrest.Matchers;
import org.junit.Test;


public class GivenWhenThenErrorITest extends WithJetty {
    @Test
    public void throws_assertion_error_when_a_body_assertion_is_incorrect() {
        exception.expect(AssertionError.class);
        exception.expectMessage(("JSON path greeting doesn\'t match.\n" + ("Expected: Greetings John Doe!\n" + "  Actual: Greetings John Doe")));
        RestAssured.given().param("firstName", "John").param("lastName", "Doe").when().get("/greet").then().statusCode(200).body("greeting", Matchers.equalTo("Greetings John Doe!"));
    }

    @Test
    public void throws_assertion_error_when_a_status_assertion_is_incorrect() {
        exception.expect(AssertionError.class);
        exception.expectMessage("Expected status code <202> but was <200>.");
        RestAssured.given().param("firstName", "John").param("lastName", "Doe").when().get("/greet").then().statusCode(202).body("greeting", Matchers.equalTo("Greetings John Doe"));
    }

    @Test
    public void throws_assertion_error_when_content_type_assertion_is_incorrect() {
        exception.expect(AssertionError.class);
        exception.expectMessage("Expected content-type \"XML\" doesn\'t match actual content-type \"application/json;charset=utf-8\".");
        RestAssured.given().param("firstName", "John").param("lastName", "Doe").when().get("/greet").then().statusCode(200).contentType(XML).body("greeting", Matchers.equalTo("Greetings John Doe"));
    }

    @Test
    public void throws_assertion_error_when_header_assertion_is_incorrect() {
        exception.expect(AssertionError.class);
        exception.expectMessage(("Expected header \"Ikk\" was not \"jux\", was \"null\". Headers are:\n" + ("Content-Type=application/json;charset=utf-8\n" + "Content-Length=33")));
        RestAssured.given().param("firstName", "John").param("lastName", "Doe").when().get("/greet").then().header("Ikk", Matchers.equalTo("jux"));
    }

    @Test
    public void throws_assertion_error_when_cookie_assertion_is_incorrect_due_to_no_cookies_in_the_response() {
        exception.expect(AssertionError.class);
        exception.expectMessage("No cookies defined in the response");
        RestAssured.given().param("firstName", "John").param("lastName", "Doe").when().get("/greet").then().cookie("mycookie", Matchers.equalTo("jux"));
    }
}

