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
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.Cookies;
import io.restassured.itest.java.support.WithJetty;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.Arrays;
import org.apache.commons.io.output.WriterOutputStream;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class SpecificationBuilderITest extends WithJetty {
    @Test
    public void expectingSpecificationMergesTheCurrentSpecificationWithTheSuppliedOne() {
        final ResponseSpecBuilder builder = new ResponseSpecBuilder();
        builder.expectBody("store.book.size()", is(4)).expectStatusCode(200);
        final ResponseSpecification responseSpecification = builder.build();
        expect().specification(responseSpecification).body("store.book[0].author", equalTo("Nigel Rees")).when().get("/jsonStore");
    }

    @Test
    public void supportsSpecifyingDefaultResponseSpec() {
        RestAssured.responseSpecification = new ResponseSpecBuilder().expectBody("store.book.size()", is(4)).expectStatusCode(200).build();
        try {
            expect().body("store.book[0].author", equalTo("Nigel Rees")).when().get("/jsonStore");
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void expectingSpecMergesTheCurrentSpecificationWithTheSuppliedOne() {
        final ResponseSpecBuilder builder = new ResponseSpecBuilder();
        builder.expectBody("store.book.size()", is(4)).expectStatusCode(200);
        final ResponseSpecification responseSpecification = builder.build();
        expect().spec(responseSpecification).body("store.book[0].author", equalTo("Nigel Rees")).when().get("/jsonStore");
    }

    @Test
    public void bodyExpectationsAreNotOverwritten() {
        final ResponseSpecBuilder builder = new ResponseSpecBuilder();
        builder.expectBody("store.book.size()", is(4)).expectStatusCode(200);
        final ResponseSpecification responseSpecification = builder.build();
        expect().body("store.book.author", hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien")).spec(responseSpecification).body("store.book[0].author", equalTo("Nigel Rees")).when().get("/jsonStore");
    }

    @Test
    public void responseSpecificationSupportsMergingWithAnotherResponseSpecification() {
        final ResponseSpecification specification = expect().body("store.book.size()", equalTo(4));
        final ResponseSpecification built = new ResponseSpecBuilder().expectStatusCode(200).addResponseSpecification(specification).build();
        expect().body("store.book.author", hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien")).spec(built).body("store.book[0].author", equalTo("Nigel Rees")).when().get("/jsonStore");
    }

    @Test
    public void responseSpecificationCanExpectBodyWithArgs() {
        final ResponseSpecification spec = new ResponseSpecBuilder().rootPath("store.book[%d]").expectBody("author", withArgs(0), equalTo("Nigel Rees")).build();
        expect().spec(spec).body("title", withArgs(1), equalTo("Sword of Honour")).when().get("/jsonStore");
    }

    @Test
    public void responseSpecificationCanExpectContentWithArgs() {
        final ResponseSpecification spec = new ResponseSpecBuilder().rootPath("store.book[%d]").expectContent("author", withArgs(0), equalTo("Nigel Rees")).build();
        expect().spec(spec).content("title", withArgs(1), equalTo("Sword of Honour")).when().get("/jsonStore");
    }

    @Test
    public void supportsSpecifyingParametersInRequestSpecBuilder() {
        final RequestSpecification spec = new RequestSpecBuilder().addParameter("firstName", "John").addParam("lastName", "Doe").build();
        expect().body("greeting.firstName", equalTo("John")).body("greeting.lastName", equalTo("Doe")).when().get("/greetXML");
    }

    @Test
    public void supportsSpecifyingDefaultRequestSpec() {
        RestAssured.requestSpecification = new RequestSpecBuilder().addParameter("firstName", "John").addParam("lastName", "Doe").build();
        try {
            expect().body("greeting.firstName", equalTo("John")).body("greeting.lastName", equalTo("Doe")).when().get("/greetXML");
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void supportsSpecifyingQueryParametersInRequestSpecBuilderWhenGet() {
        final RequestSpecification spec = new RequestSpecBuilder().addQueryParameter("firstName", "John").addQueryParam("lastName", "Doe").build();
        expect().body("greeting.firstName", equalTo("John")).body("greeting.lastName", equalTo("Doe")).when().get("/greetXML");
    }

    @Test
    public void supportsSpecifyingQueryParametersInRequestSpecBuilderWhenPost() {
        final RequestSpecification spec = new RequestSpecBuilder().addQueryParameter("firstName", "John").addQueryParam("lastName", "Doe").build();
        expect().body("greeting.firstName", equalTo("John")).body("greeting.lastName", equalTo("Doe")).when().post("/greetXML");
    }

    @Test
    public void supportsMergesParametersWhenUsingRequestSpecBuilder() {
        final RequestSpecification spec = new RequestSpecBuilder().addParameter("firstName", "John").build();
        expect().body("greeting.firstName", equalTo("John")).body("greeting.lastName", equalTo("Doe")).when().get("/greetXML");
    }

    @Test
    public void supportsMergingCookiesWhenUsingRequestSpecBuilder() {
        final RequestSpecification spec1 = new RequestSpecBuilder().addCookie("cookie3", "value3").build();
        final RequestSpecification spec2 = new RequestSpecBuilder().addCookie("cookie1", "value1").addRequestSpecification(spec1).build();
        expect().body(equalTo("cookie1, cookie3, cookie2")).when().get("/cookie");
    }

    @Test
    public void supportsMergingHeadersWhenUsingRequestSpecBuilder() {
        final RequestSpecification spec = new RequestSpecBuilder().addHeader("header1", "value1").build();
        expect().body(containsString("header1")).body(containsString("header2")).when().get("/header");
    }

    @Test
    public void supportsMergingRequestSpecHeadersUsingTheBuilder() {
        final RequestSpecification spec = given().header("header2", "value2");
        final RequestSpecification spec2 = new RequestSpecBuilder().addHeader("header1", "value1").addRequestSpecification(spec).build();
        expect().body(containsString("header1")).body(containsString("header2")).body(containsString("header3")).when().get("/header");
    }

    @Test
    public void requestSpecBuilderSupportsSettingAuthentication() {
        final RequestSpecification spec = new RequestSpecBuilder().setAuth(basic("jetty", "jetty")).build();
        expect().statusCode(200).when().get("/secured/hello");
    }

    @Test
    public void supportsMergingMultiValueParametersWhenUsingRequestSpecBuilder() {
        final RequestSpecification spec = new RequestSpecBuilder().addParam("list", "1", "2", "3").build();
        expect().body("list", equalTo("1,2,3")).when().get("/multiValueParam");
    }

    @Test
    public void supportsMergingMultiValueQueryParametersWhenUsingRequestSpecBuilder() {
        final RequestSpecification spec = new RequestSpecBuilder().addQueryParam("list", "1", "2", "3").build();
        expect().body("list", equalTo("1,2,3")).when().get("/multiValueParam");
    }

    @Test
    public void supportsMergingMultiValueFormParametersWhenUsingRequestSpecBuilder() {
        final RequestSpecification spec = new RequestSpecBuilder().addFormParam("list", "1", "2", "3").build();
        expect().body("list", equalTo("1,2,3")).when().put("/multiValueParam");
    }

    @Test
    public void supportsMergingMultiValueParametersUsingListWhenUsingRequestSpecBuilder() {
        final RequestSpecification spec = new RequestSpecBuilder().addParam("list", Arrays.asList("1", "2", "3")).build();
        expect().body("list", equalTo("1,2,3")).when().get("/multiValueParam");
    }

    @Test
    public void supportsMergingMultiValueQueryParametersUsingListWhenUsingRequestSpecBuilder() {
        final RequestSpecification spec = new RequestSpecBuilder().addQueryParam("list", Arrays.asList("1", "2", "3")).build();
        expect().body("list", equalTo("1,2,3")).when().get("/multiValueParam");
    }

    @Test
    public void supportsMergingMultiValueFormParametersUsingListWhenUsingRequestSpecBuilder() {
        final RequestSpecification spec = new RequestSpecBuilder().addFormParam("list", Arrays.asList("1", "2", "3")).build();
        expect().body("list", equalTo("1,2,3")).when().put("/multiValueParam");
    }

    @Test
    public void supportsMergingFormParametersWhenUsingRequestSpecBuilder() {
        final RequestSpecification spec = new RequestSpecBuilder().addFormParam("lastName", "Doe").build();
        expect().body("greeting", Matchers.equalTo("Greetings John Doe")).when().put("/greetPut");
    }

    @Test
    public void supportsMergingPathParametersWhenUsingRequestSpecBuilder() {
        final RequestSpecification spec = new RequestSpecBuilder().addPathParam("lastName", "Doe").build();
        expect().body("fullName", equalTo("John Doe")).when().get("/{firstName}/{lastName}");
    }

    @Test
    public void supportsSettingLoggingWhenUsingRequestSpecBuilder() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        final RequestSpecification spec = new RequestSpecBuilder().setConfig(logConfig(logConfig().defaultStream(captor))).and().log(ALL).build();
        given().spec(spec).pathParameter("firstName", "John").pathParameter("lastName", "Doe").when().get("/{firstName}/{lastName}").then().body("fullName", equalTo("John Doe"));
        Assert.assertThat(writer.toString(), equalTo(String.format(("Request method:\tGET%n" + (((((((((("Request URI:\thttp://localhost:8080/John/Doe%n" + "Proxy:\t\t\t<none>%n") + "Request params:\t<none>%n") + "Query params:\t<none>%n") + "Form params:\t<none>%n") + "Path params:\tfirstName=John%n") + "\t\t\t\tlastName=Doe%n") + "Headers:\t\tAccept=*/*%n") + "Cookies:\t\t<none>%n") + "Multiparts:\t\t<none>%n") + "Body:\t\t\t<none>%n")))));
    }

    @Test
    public void supportsSettingLoggingWhenUsingRequestAndResponseSpecBuilder() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        final RequestSpecification requestSpec = new RequestSpecBuilder().setConfig(logConfig(logConfig().defaultStream(captor))).addPathParam("firstName", "John").addPathParam("lastName", "Doe").build();
        final ResponseSpecification responseSpec = new ResponseSpecBuilder().expectStatusCode(HttpStatus.SC_OK).log(ALL).build();
        given(requestSpec, responseSpec).get("/{firstName}/{lastName}");
        Assert.assertThat(writer.toString(), equalTo(String.format(("HTTP/1.1 200 OK%n" + (((((((("Content-Type: application/json;charset=utf-8%n" + "Content-Length: 59%n") + "Server: Jetty(9.3.2.v20150730)%n") + "%n") + "{%n") + "    \"firstName\": \"John\",%n") + "    \"lastName\": \"Doe\",%n") + "    \"fullName\": \"John Doe\"%n") + "}%n")))));
    }

    @Test
    public void supportsSettingLoggingWhenUsingOnlyResponseSpecBuilder() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        final ResponseSpecification responseSpec = new ResponseSpecBuilder().expectStatusCode(HttpStatus.SC_OK).log(ALL).build();
        given().config(logConfig(logConfig().defaultStream(captor))).pathParameter("firstName", "John").pathParameter("lastName", "Doe").get("/{firstName}/{lastName}").then().spec(responseSpec).body("fullName", equalTo("John Doe"));
        Assert.assertThat(writer.toString(), equalTo(String.format(("HTTP/1.1 200 OK%n" + (((((((("Content-Type: application/json;charset=utf-8%n" + "Content-Length: 59%n") + "Server: Jetty(9.3.2.v20150730)%n") + "%n") + "{%n") + "    \"firstName\": \"John\",%n") + "    \"lastName\": \"Doe\",%n") + "    \"fullName\": \"John Doe\"%n") + "}%n")))));
    }

    @Test
    public void supportsSettingConfigWhenUsingRequestSpecBuilder() {
        final RequestSpecification spec = new RequestSpecBuilder().setConfig(newConfig().redirect(redirectConfig().followRedirects(false))).build();
        expect().statusCode(302).header("Location", is("http://localhost:8080/hello")).when().get("/redirect");
    }

    @Test
    public void mergesStaticallyDefinedResponseSpecificationsCorrectly() {
        RestAssured.responseSpecification = new ResponseSpecBuilder().expectCookie("Cookie1", "Value1").build();
        ResponseSpecification reqSpec1 = new ResponseSpecBuilder().expectCookie("Cookie2", "Value2").build();
        ResponseSpecification reqSpec2 = new ResponseSpecBuilder().expectCookie("Cookie3", "Value3").build();
        try {
            Cookies cookies = given().cookie("Cookie1", "Value1").cookie("Cookie2", "Value2").when().get("/reflect").then().assertThat().spec(reqSpec1).extract().detailedCookies();
            Assert.assertThat(cookies.size(), is(2));
            Assert.assertThat(cookies.hasCookieWithName("Cookie1"), is(true));
            Assert.assertThat(cookies.hasCookieWithName("Cookie2"), is(true));
            cookies = given().cookie("Cookie1", "Value1").cookie("Cookie3", "Value3").when().get("/reflect").then().assertThat().spec(reqSpec2).extract().detailedCookies();
            Assert.assertThat(cookies.size(), is(2));
            Assert.assertThat(cookies.hasCookieWithName("Cookie1"), is(true));
            Assert.assertThat(cookies.hasCookieWithName("Cookie3"), is(true));
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void mergesStaticallyDefinedRequestSpecificationsCorrectly() {
        RestAssured.requestSpecification = new RequestSpecBuilder().addCookie("Cookie1", "Value1").build();
        RequestSpecification reqSpec1 = new RequestSpecBuilder().addCookie("Cookie2", "Value2").build();
        RequestSpecification reqSpec2 = new RequestSpecBuilder().addCookie("Cookie3", "Value3").build();
        try {
            Cookies cookies = given().spec(reqSpec1).when().get("/reflect").then().extract().detailedCookies();
            Assert.assertThat(cookies.size(), is(2));
            Assert.assertThat(cookies.hasCookieWithName("Cookie1"), is(true));
            Assert.assertThat(cookies.hasCookieWithName("Cookie2"), is(true));
            cookies = given().spec(reqSpec2).when().get("/reflect").then().extract().detailedCookies();
            Assert.assertThat(cookies.size(), is(2));
            Assert.assertThat(cookies.hasCookieWithName("Cookie1"), is(true));
            Assert.assertThat(cookies.hasCookieWithName("Cookie3"), is(true));
        } finally {
            RestAssured.reset();
        }
    }
}

