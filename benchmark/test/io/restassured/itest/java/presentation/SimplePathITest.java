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
package io.restassured.itest.java.presentation;


import io.restassured.RestAssured;
import io.restassured.itest.java.support.WithJetty;
import io.restassured.path.json.JsonPath;
import io.restassured.path.xml.XmlPath;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class SimplePathITest extends WithJetty {
    @Test
    public void simpleJsonValidationWithJsonPath() throws Exception {
        final String body = RestAssured.get("/greetJSON?firstName=John&lastName=Doe").asString();
        final JsonPath json = new JsonPath(body).setRoot("greeting");
        final String firstName = json.getString("firstName");
        final String lastName = json.getString("lastName");
        Assert.assertThat(firstName, Matchers.equalTo("John"));
        Assert.assertThat(lastName, Matchers.equalTo("Doe"));
    }

    @Test
    public void simpleXmlValidationWithXmlPath() throws Exception {
        final String body = RestAssured.get("/greetXML?firstName=John&lastName=Doe").asString();
        final XmlPath xml = new XmlPath(body).setRoot("greeting");
        final String firstName = xml.getString("firstName");
        final String lastName = xml.getString("lastName");
        Assert.assertThat(firstName, Matchers.equalTo("John"));
        Assert.assertThat(lastName, Matchers.equalTo("Doe"));
    }
}

