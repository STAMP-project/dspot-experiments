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


import io.restassured.http.Cookie;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Test;


public class GetCookiesTest {
    @Test
    public void can_send_cookie_using_cookie_class() {
        RestAssuredWebTestClient.given().cookie(new Cookie.Builder("cookieName1", "John Doe").build()).when().get("/cookie").then().statusCode(200).body("cookieValue1", Matchers.equalTo("John Doe"));
    }

    @Test
    public void can_send_cookie_using_cookie_name_and_value() {
        RestAssuredWebTestClient.given().cookie("cookieName1", "John Doe").when().get("/cookie").then().statusCode(200).body("cookieValue1", Matchers.equalTo("John Doe"));
    }

    @Test
    public void can_send_multiple_cookies() {
        RestAssuredWebTestClient.given().cookie("cookieName1", "John Doe").cookie("cookieName2", "rest assured").when().get("/cookie").then().statusCode(200).body("cookieValue1", Matchers.equalTo("John Doe")).body("cookieValue2", Matchers.equalTo("rest assured"));
    }

    @Test
    public void can_send_cookies_using_map() {
        Map<String, Object> cookies = new HashMap<String, Object>();
        cookies.put("cookieName1", "John Doe");
        cookies.put("cookieName2", "rest assured");
        RestAssuredWebTestClient.given().cookies(cookies).when().get("/cookie").then().statusCode(200).body("cookieValue1", Matchers.equalTo("John Doe")).body("cookieValue2", Matchers.equalTo("rest assured"));
    }

    @Test
    public void can_send_cookies_using_cookies_class() {
        RestAssuredWebTestClient.given().cookies(new io.restassured.http.Cookies(new Cookie.Builder("cookieName1", "John Doe").build(), new Cookie.Builder("cookieName2", "rest assured").build())).when().get("/cookie").then().statusCode(200).body("cookieValue1", Matchers.equalTo("John Doe")).body("cookieValue2", Matchers.equalTo("rest assured"));
    }
}

