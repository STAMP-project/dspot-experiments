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
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RedirectConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.itest.java.support.WithJetty;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.hamcrest.Matchers;
import org.junit.Test;


public class RedirectITest extends WithJetty {
    @Test
    public void followsRedirectsByDefault() throws Exception {
        RestAssured.given().param("url", "/hello").expect().body("hello", Matchers.equalTo("Hello Scalatra")).when().get("/redirect");
    }

    @Test
    public void doesntFollowRedirectsIfExplicitlySpecified() throws Exception {
        RestAssured.given().redirects().follow(false).param("url", "/hello").expect().statusCode(302).header("Location", Matchers.is("http://localhost:8080/hello")).when().get("/redirect");
    }

    @Test
    public void doesntFollowRedirectsIfSpecifiedStaticallyInRedirectConfig() throws Exception {
        RestAssured.config = RestAssuredConfig.newConfig().redirect(RedirectConfig.redirectConfig().followRedirects(false));
        try {
            RestAssured.given().param("url", "/hello").expect().statusCode(302).header("Location", Matchers.is("http://localhost:8080/hello")).when().get("/redirect");
        } finally {
            RestAssured.reset();
        }
    }

    @Test(expected = ClientProtocolException.class)
    public void throwsClientProtocolExceptionIfMaxNumberOfRedirectAreExceeded() throws Exception {
        RestAssured.config = RestAssuredConfig.config().redirect(RedirectConfig.redirectConfig().followRedirects(true).and().maxRedirects(0));
        try {
            RestAssured.given().param("url", "/hello").expect().statusCode(302).header("Location", Matchers.is("http://localhost:8080/hello")).when().get("/redirect");
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void definingRedirectConfigInTheDSLOverridesSettingsFromDefaultConfig() throws Exception {
        RestAssured.config = RestAssuredConfig.config().redirect(RedirectConfig.redirectConfig().followRedirects(false).and().maxRedirects(0));
        try {
            RestAssured.given().redirects().follow(true).and().redirects().max(1).param("url", "/hello").expect().statusCode(200).body("hello", Matchers.equalTo("Hello Scalatra")).when().get("/redirect");
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void cookiesAreReceivedOnWhenServerReturns302() throws Exception {
        RestAssured.given().redirects().follow(false).param("url", "/hello").expect().statusCode(302).cookie("cookieName", "cookieValue").header("location", "http://localhost:8080/hello").when().get("/redirect-and-set-cookie");
    }

    @Test
    public void cookiesAreIncludedInRedirectsWhenCookiePolicyIsBrowserCompatibility() throws Exception {
        RestAssured.given().config(RestAssuredConfig.newConfig().httpClient(HttpClientConfig.httpClientConfig().setParam(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY))).param("url", "/reflect").expect().statusCode(200).cookie("cookieName", "cookieValue").when().get("/redirect-and-set-cookie");
    }
}

