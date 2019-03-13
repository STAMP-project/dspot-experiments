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
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.itest.java.support.WithJetty;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.client.utils.DateUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CookieITest extends WithJetty {
    @Test
    public void cookiesReturnsAMapWhereTheLastValueOfAMultiValueCookieIsUsed() throws Exception {
        final Map<String, String> cookies = get("/multiCookie").cookies();
        Assert.assertThat(cookies, hasEntry("cookie1", "cookieValue2"));
    }

    @Test
    public void detailedCookiesAllowsToGetMultiValues() throws Exception {
        final Cookies cookies = get("/multiCookie").detailedCookies();
        Assert.assertThat(cookies.getValues("cookie1"), hasItems("cookieValue1", "cookieValue2"));
    }

    @Test
    public void whenUsingTheDslAndExpectingAMultiValueCookieThenTheLastValueIsUsed() throws Exception {
        get("/multiCookie");
    }

    @Test
    public void supportsDetailedCookieMatchingUsingDsl() {
        get("/multiCookie");
    }

    @Test
    public void supportsDetailedCookieMatcher() {
        get("/multiCookie").then().cookie("cookie1", detailedCookie().maxAge(1234567).path(Matchers.notNullValue()));
    }

    @Test
    public void supportsCookieStringMatchingUsingTheDsl() throws Exception {
        get("/setCookies");
    }

    @Test
    public void canSpecifyMultiValueCookiesUsingByPassingInSeveralValuesToTheCookieMethod() throws Exception {
        get("/multiCookieRequest").then().body("key1", contains("value1", "value2")).body("size()", is(2));
    }

    @Test
    public void multipleCookieStatementsAreConcatenated() throws Exception {
        get("/setCookies");
    }

    @Test
    public void multipleCookiesShortVersionUsingPlainStrings() throws Exception {
        get("/setCookies");
    }

    @Test
    public void multipleCookiesShortVersionUsingHamcrestMatching() throws Exception {
        get("/setCookies");
    }

    @Test
    public void multipleCookiesShortVersionUsingMixOfHamcrestMatchingAndStringMatching() throws Exception {
        get("/setCookies");
    }

    @Test
    public void multipleCookiesUsingMap() throws Exception {
        Map expectedCookies = new HashMap();
        expectedCookies.put("key1", "value1");
        expectedCookies.put("key2", "value2");
        get("/setCookies");
    }

    @Test
    public void multipleCookiesUsingMapWithHamcrestMatcher() throws Exception {
        Map expectedCookies = new HashMap();
        expectedCookies.put("key1", containsString("1"));
        expectedCookies.put("key3", equalTo("value3"));
        get("/setCookies");
    }

    @Test
    public void multipleCookiesUsingMapWithMixOfStringAndHamcrestMatcher() throws Exception {
        Map expectedCookies = new HashMap();
        expectedCookies.put("key1", containsString("1"));
        expectedCookies.put("key2", "value2");
        get("/setCookies");
    }

    @Test
    public void whenExpectedCookieDoesntMatchAnAssertionThenAssertionErrorIsThrown() throws Exception {
        exception.expect(AssertionError.class);
        exception.expectMessage(equalTo(("1 expectation failed.\n" + "Expected cookie \"key1\" was not \"value2\", was \"value1\".\n")));
        get("/setCookies");
    }

    @Test
    public void whenExpectedCookieIsNotFoundThenAnAssertionErrorIsThrown() throws Exception {
        exception.expect(AssertionError.class);
        exception.expectMessage(equalTo(("1 expectation failed.\n" + ((("Cookie \"Not-Defined\" was not defined in the response. Cookies are: \n" + "key1=value1\n") + "key2=value2\n") + "key3=value3\n"))));
        get("/setCookies");
    }

    @Test
    public void requestSpecificationAllowsSpecifyingCookieWithNoValue() throws Exception {
        get("/cookie_with_no_value");
    }

    @Test
    public void responseSpecificationAllowsParsingCookieWithNoValue() throws Exception {
        get("/response_cookie_with_no_value");
    }

    @Test
    public void requestSpecificationAllowsSpecifyingCookies() throws Exception {
        get("/cookie");
    }

    @Test
    public void requestSpecificationAllowsSpecifyingCookieUsingMap() throws Exception {
        Map<String, String> cookies = new HashMap<String, String>();
        cookies.put("username", "John");
        cookies.put("token", "1234");
        get("/cookie");
    }

    @Test
    public void requestSpecificationAllowsSpecifyingMultipleCookies() throws Exception {
        Map<String, String> cookies = new HashMap<String, String>();
        cookies.put("username", "John");
        cookies.put("token", "1234");
        get("/cookie");
    }

    @Test
    public void canGetCookieDetails() throws Exception {
        final List<Cookie> cookies = get("/multiCookie").detailedCookies().getList("cookie1");
        Assert.assertThat(cookies.size(), is(2));
        final Cookie firstCookie = cookies.get(0);
        Assert.assertThat(firstCookie.getValue(), equalTo("cookieValue1"));
        Assert.assertThat(firstCookie.getDomain(), equalTo("localhost"));
        final Cookie secondCookie = cookies.get(1);
        Assert.assertThat(secondCookie.getValue(), equalTo("cookieValue2"));
        Assert.assertThat(secondCookie.getDomain(), equalTo("localhost"));
        Assert.assertThat(secondCookie.getPath(), equalTo("/"));
        Assert.assertThat(secondCookie.getMaxAge(), is(1234567));
        Assert.assertThat(secondCookie.isSecured(), is(true));
        Assert.assertThat(secondCookie.getVersion(), is(1));
    }

    @Test
    public void cookiesSupportEqualCharacterInCookieValue() throws Exception {
        when().post("/reflect");
    }

    @Test
    public void cookiesParsingSupportsNoValueCookies() throws Exception {
        when().post("/reflect");
    }

    @Test
    public void detailedCookieWorks() throws Exception {
        final Response response = get("/html_with_cookie");
        final Cookie detailedCookieJsessionId = response.detailedCookie("JSESSIONID");
        Assert.assertThat(detailedCookieJsessionId, notNullValue());
        Assert.assertThat(detailedCookieJsessionId.getPath(), equalTo("/"));
        Assert.assertThat(detailedCookieJsessionId.getValue(), equalTo("B3134D534F40968A3805968207273EF5"));
    }

    @Test
    public void getDetailedCookieWorks() throws Exception {
        final Response response = get("/html_with_cookie");
        final Cookie detailedCookieJsessionId = response.getDetailedCookie("JSESSIONID");
        Assert.assertThat(detailedCookieJsessionId, notNullValue());
        Assert.assertThat(detailedCookieJsessionId.getPath(), equalTo("/"));
        Assert.assertThat(detailedCookieJsessionId.getValue(), equalTo("B3134D534F40968A3805968207273EF5"));
    }

    @Test
    public void multipleCookiesWithSameKey() throws Exception {
        final Response response = get("/setCommonIdCookies");
        Map<String, String> map = new HashMap<String, String>();
        map = response.cookies();
        Assert.assertThat(map.get("key1"), equalTo("value3"));
    }

    @Test
    public void usesCookiesDefinedInAStaticRequestSpecification() throws Exception {
        RestAssured.requestSpecification = new RequestSpecBuilder().addCookie("my-cookie", "1234").build();
        try {
            when().post("/reflect").then().log().ifValidationFails().cookie("my-cookie", "1234");
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void parsesValidExpiresDateCorrectly() throws Exception {
        Cookies cookies = get("/cookieWithValidExpiresDate").then().extract().detailedCookies();
        Assert.assertThat(cookies.asList(), hasSize(1));
        Cookie cookie = cookies.get("name");
        Assert.assertThat(cookie.getExpiryDate(), equalTo(DateUtils.parseDate("Sat, 02 May 2009 23:38:25 GMT")));
    }

    @Test
    public void removesDoubleQuotesFromCookieWithExpiresDate() throws Exception {
        Cookies cookies = get("/cookieWithDoubleQuoteExpiresDate").then().extract().detailedCookies();
        Assert.assertThat(cookies.asList(), hasSize(1));
        Cookie cookie = cookies.get("name");
        Assert.assertThat(cookie.getExpiryDate(), equalTo(DateUtils.parseDate("Sat, 02 May 2009 23:38:25 GMT")));
    }

    @Test
    public void setsExpiresPropertyToNullWhenCookieHasInvalidExpiresDate() throws Exception {
        Cookies cookies = get("/cookieWithInvalidExpiresDate").then().extract().detailedCookies();
        Assert.assertThat(cookies.asList(), hasSize(1));
        Cookie cookie = cookies.get("name");
        Assert.assertThat(cookie.getExpiryDate(), nullValue());
    }
}

