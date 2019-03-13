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
import org.junit.Test;


public class ParamITest extends WithJetty {
    @Test
    public void noValueParamWhenUsingParamWithGetRequest() throws Exception {
        RestAssured.given().param("some").expect().body(is("Params: some=")).when().get("/noValueParam");
    }

    @Test
    public void noValueParamWhenUsingQueryParamWithGetRequest() throws Exception {
        RestAssured.given().queryParam("some").expect().body(is("Params: some=")).when().get("/noValueParam");
    }

    @Test
    public void multipleNoValueQueryParamWhenUsingQueryParamInUrlForGetRequest() throws Exception {
        // For some reason Scalatra returns the order different when running in Intellij and Maven
        RestAssured.expect().body(anyOf(is("Params: some=some1="), is("Params: some1=some="))).when().get("/noValueParam?some&some1");
    }

    @Test
    public void singleNoValueQueryParamWhenUsingQueryParamInUrlForGetRequest() throws Exception {
        RestAssured.expect().body(is("Params: some=")).when().get("/noValueParam?some");
    }

    @Test
    public void mixingStartingNoValueQueryParamWhenUsingQueryParamInUrlForGetRequest() throws Exception {
        RestAssured.expect().body(is("Params: some1=some2=one")).when().get("/noValueParam?some1&some2=one");
    }

    @Test
    public void mixingEndingNoValueQueryParamWhenUsingQueryParamInUrlForGetRequest() throws Exception {
        RestAssured.expect().body(is("Params: some1=onesome2=")).when().get("/noValueParam?some1=one&some2");
    }

    @Test
    public void noValueParamWhenUsingFormParamWithPutRequest() throws Exception {
        RestAssured.given().formParam("some").expect().body(is("OK")).when().put("/noValueParam");
    }

    @Test
    public void noValueParamWhenUsingFormParamWithPostRequest() throws Exception {
        RestAssured.given().formParam("some").expect().body(is("Params: some=")).when().post("/noValueParam");
    }

    @Test
    public void multipleNoValueParamWhenUsingFormParamWithPostRequest() throws Exception {
        // For some reason Scalatra returns the order different when running in Intellij and Maven
        RestAssured.given().formParam("some").and().formParam("some1").expect().body(anyOf(is("Params: some=some1="), is("Params: some1=some="))).when().post("/noValueParam");
    }

    @Test
    public void formParamsAreUrlEncoded() throws Exception {
        RestAssured.given().formParam("firstName", "Some & firstname").formParam("lastName", "<lastname>").expect().body("greeting", equalTo("Greetings Some & firstname <lastname>")).when().post("/greet");
    }

    @Test
    public void formParamsAreUrlEncodedWithDefinedCharset() throws Exception {
        // Jetty 9 always send charset as lowercase
        RestAssured.given().contentType("application/x-www-form-urlencoded; charset=ISO-8859-1").formParam("ikk", "&&&").expect().body(is("iso-8859-1")).when().post("/charEncoding");
    }

    @Test
    public void charsetIsReallyDefined() throws Exception {
        RestAssured.given().contentType("application/x-www-form-urlencoded; charset=ISO-8859-1").formParam("firstName", "Some & firstname").formParam("lastName", "<lastname>").expect().body("greeting", equalTo("Greetings Some & firstname <lastname>")).when().post("/greet");
    }

    @Test
    public void formParamsAreUrlEncodedWithUtf8WhenCharsetDefinedWithNoEqualSign() throws Exception {
        RestAssured.given().contentType("application/x-www-form-urlencoded; charset").formParam("firstName", "Some & firstname").formParam("lastName", "<lastname>").expect().body("greeting", equalTo("Greetings Some & firstname <lastname>")).when().post("/greet");
    }

    @Test
    public void mixingNoValueAndValueParamWhenUsingFormParamWithPostRequest() throws Exception {
        // For some reason Scalatra returns the order different when running in Intellij and Maven
        RestAssured.given().formParam("some").and().formParam("some1", "one").expect().body(anyOf(is("Params: some=some1=one"), is("Params: some1=onesome="))).when().post("/noValueParam");
    }

    @Test
    public void noValueParamWhenUsingParamWithPostRequest() throws Exception {
        RestAssured.given().param("some").expect().body(is("Params: some=")).when().post("/noValueParam");
    }

    @Test
    public void whenLastParamInGetRequestEndsWithEqualItsTreatedAsANoValueParam() throws Exception {
        RestAssured.expect().body("greeting", equalTo("Greetings John ")).when().get("/greet?firstName=John&lastName=");
    }

    @Test
    public void whenFirstParamInGetRequestEndsWithEqualItsTreatedAsANoValueParam() throws Exception {
        RestAssured.expect().body("greeting", equalTo("Greetings  Doe")).when().get("/greet?firstName=&lastName=Doe");
    }

    @Test
    public void multiPartUploadingWorksForFormParamsAndByteArray() throws Exception {
        RestAssured.given().formParam("formParam1").formParam("formParam2", "formParamValue").multiPart("file", "juX").multiPart("string", "body").expect().statusCode(200).body(containsString("formParam1 -> WrappedArray()")).when().post("/multipart/multiple");
    }
}

