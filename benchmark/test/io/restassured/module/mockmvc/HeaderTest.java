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
/**
 * @formatter:off
 */
package io.restassured.module.mockmvc;


import io.restassured.http.Header;
import io.restassured.module.mockmvc.http.RedirectController;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class HeaderTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void can_send_header_using_header_class() {
        RestAssuredMockMvc.given().header(new Header("headerName", "John Doe")).when().get("/header").then().statusCode(200).body("headerName", equalTo("John Doe"));
    }

    @Test
    public void can_use_mapping_function_when_validating_header_value() {
        RestAssuredMockMvc.given().header(new Header("headerName", "200")).when().get("/header").then().header("Content-Length", new io.restassured.function.RestAssuredFunction<String, Integer>() {
            public Integer apply(String s) {
                return Integer.parseInt(s);
            }
        }, lessThanOrEqualTo(1000));
    }

    @Test
    public void validate_may_fail_when_using_mapping_function_when_validating_header_value() {
        exception.expect(AssertionError.class);
        exception.expectMessage("Expected header \"Content-Length\" was not a value greater than <1000>, was \"45\". Headers are:");
        RestAssuredMockMvc.given().header(new Header("headerName", "200")).when().get("/header").then().header("Content-Length", new io.restassured.function.RestAssuredFunction<String, Integer>() {
            public Integer apply(String s) {
                return Integer.parseInt(s);
            }
        }, greaterThan(1000));
    }

    @Test
    public void can_send_header_using_header_name_and_value() {
        RestAssuredMockMvc.given().header("headerName", "John Doe").when().get("/header").then().statusCode(200).body("headerName", equalTo("John Doe"));
    }

    @Test
    public void can_send_multiple_headers() {
        RestAssuredMockMvc.given().header("headerName", "John Doe").header("user-agent", "rest assured").when().get("/header").then().statusCode(200).body("headerName", equalTo("John Doe")).body("user-agent", equalTo("rest assured"));
    }

    @Test
    public void can_send_headers_using_map() {
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("headerName", "John Doe");
        headers.put("user-agent", "rest assured");
        RestAssuredMockMvc.given().headers(headers).when().get("/header").then().statusCode(200).body("headerName", equalTo("John Doe")).body("user-agent", equalTo("rest assured"));
    }

    @Test
    public void can_send_headers_using_headers_class() {
        RestAssuredMockMvc.given().headers(new io.restassured.http.Headers(new Header("headerName", "John Doe"), new Header("user-agent", "rest assured"))).when().get("/header").then().statusCode(200).body("headerName", equalTo("John Doe")).body("user-agent", equalTo("rest assured"));
    }

    @Test
    public void canUseResponseAwareMatchersForHeaderValidation() throws Exception {
        RestAssuredMockMvc.given().standaloneSetup(new RedirectController()).when().get("/redirect").then().statusCode(301).header("Location", new io.restassured.matcher.ResponseAwareMatcher<MockMvcResponse>() {
            public Matcher<?> matcher(MockMvcResponse response) throws Exception {
                return endsWith(("/redirect/" + (response.path("id"))));
            }
        });
    }
}

/**
 * @formatter:on
 */
