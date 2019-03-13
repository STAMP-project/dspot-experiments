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


import io.restassured.itest.java.support.WithJetty;
import org.junit.Test;


public class XMLPostITest extends WithJetty {
    @Test
    public void xmlParameterSupport() throws Exception {
        expect().body("greeting.firstName", equalTo("John")).when().post("/greetXML");
    }

    @Test
    public void xmlParameterSupportWithAnotherAssertion() throws Exception {
        expect().body("greeting.lastName", equalTo("Doe")).when().post("/greetXML");
    }

    @Test
    public void xmlWithLists() throws Exception {
        expect().body("greeting.children()", hasItems("John", "Doe")).when().post("/greetXML");
    }

    @Test
    public void postWithXPath() throws Exception {
        with().parameters("firstName", "John", "lastName", "Doe").post("/anotherGreetXML");
    }

    @Test
    public void postWithXPathContainingHamcrestMatcher() throws Exception {
        with().parameters("firstName", "John", "lastName", "Doe").post("/anotherGreetXML");
    }

    @Test
    public void postWithQueryParams() throws Exception {
        expect().body("greeting.lastName", equalTo("Doe")).when().post("/greetXML?firstName=John&lastName=Doe");
    }

    @Test
    public void postWithFormParamAndQueryParams() throws Exception {
        expect().body("greeting.lastName", equalTo("Doe")).when().post("/greetXML");
    }

    @Test
    public void postWithOnlyQueryParams() throws Exception {
        expect().body("greeting.lastName", equalTo("Doe")).when().post("/greetXML");
    }

    @Test
    public void customXmlCompatibleContentTypeWithBody() throws Exception {
        byte[] bytes = "Some Text".getBytes();
        expect().body(equalTo("Some Text")).when().put("/reflect");
    }

    @Test
    public void requestIncludesContentTypeWhenSendingBinaryDataAsXml() throws Exception {
        byte[] bytes = "<tag attr='value'>/".getBytes("UTF-8");
        expect().statusCode(200).contentType("application/xml").body(is(new String(bytes, "UTF-8"))).when().post("/validateContentTypeIsDefinedAndReturnBody");
    }
}

