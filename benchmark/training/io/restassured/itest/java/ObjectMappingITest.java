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


import ContentType.XML;
import ObjectMapperType.GSON;
import ObjectMapperType.JACKSON_1;
import ObjectMapperType.JACKSON_2;
import ObjectMapperType.JAXB;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.itest.java.objects.Greeting;
import io.restassured.itest.java.objects.Message;
import io.restassured.itest.java.objects.ScalatraObject;
import io.restassured.itest.java.support.WithJetty;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ObjectMappingITest extends WithJetty {
    @Test
    public void mapResponseToObjectUsingJackson() throws Exception {
        final ScalatraObject object = get("/hello").as(ScalatraObject.class);
        Assert.assertThat(object.getHello(), Matchers.equalTo("Hello Scalatra"));
    }

    @Test
    public void mapResponseToObjectUsingJaxb() throws Exception {
        final Greeting object = get("/greetXML").as(Greeting.class);
        Assert.assertThat(object.getFirstName(), Matchers.equalTo("John"));
        Assert.assertThat(object.getLastName(), Matchers.equalTo("Doe"));
    }

    @Test
    public void mapResponseToObjectUsingJacksonWhenNoContentTypeIsDefined() throws Exception {
        final Message message = get("/noContentTypeJsonCompatible").as(Message.class);
        Assert.assertThat(message.getMessage(), Matchers.equalTo("It works"));
    }

    @Test
    public void contentTypesEndingWithPlusJsonWorksForJsonObjectMapping() throws Exception {
        final Message message = get("/mimeTypeWithPlusJson").as(Message.class);
        Assert.assertThat(message.getMessage(), Matchers.equalTo("It works"));
    }

    @Test
    public void whenNoRequestContentTypeIsSpecifiedThenRestAssuredSerializesToJSON() throws Exception {
        final ScalatraObject object = new ScalatraObject();
        object.setHello("Hello world");
        final ScalatraObject actual = given().body(object).when().post("/reflect").as(ScalatraObject.class);
        Assert.assertThat(object, Matchers.equalTo(actual));
    }

    @Test
    public void whenRequestContentTypeIsJsonThenRestAssuredSerializesToJSON() throws Exception {
        final ScalatraObject object = new ScalatraObject();
        object.setHello("Hello world");
        final ScalatraObject actual = given().contentType(ContentType.JSON).and().body(object).when().post("/reflect").as(ScalatraObject.class);
        Assert.assertThat(object, Matchers.equalTo(actual));
    }

    @Test
    public void whenRequestContentTypeIsXmlThenRestAssuredSerializesToXML() throws Exception {
        final Greeting object = new Greeting();
        object.setFirstName("John");
        object.setLastName("Doe");
        final Greeting actual = given().contentType(XML).and().body(object).when().post("/reflect").as(Greeting.class);
        Assert.assertThat(object, Matchers.equalTo(actual));
    }

    @Test
    public void whenRequestContentTypeIsXmlAndCharsetIsUsAsciiThenRestAssuredSerializesToJSON() throws Exception {
        final Greeting object = new Greeting();
        object.setFirstName("John");
        object.setLastName("Doe");
        final Greeting actual = given().contentType("application/xml; charset=US-ASCII").and().body(object).when().post("/reflect").as(Greeting.class);
        Assert.assertThat(object, Matchers.equalTo(actual));
    }

    @Test
    public void whenRequestContentTypeIsJsonAndCharsetIsUsAsciiThenRestAssuredSerializesToJSON() throws Exception {
        final Greeting object = new Greeting();
        object.setFirstName("John");
        object.setLastName("Doe");
        final Greeting actual = given().contentType("application/json; charset=US-ASCII").and().body(object).when().post("/reflect").as(Greeting.class);
        Assert.assertThat(object, Matchers.equalTo(actual));
    }

    @Test
    public void whenRequestContentTypeIsXmlAndCharsetIsUtf16ThenRestAssuredSerializesToJSON() throws Exception {
        final Greeting object = new Greeting();
        object.setFirstName("John");
        object.setLastName("Doe");
        final Greeting actual = given().contentType("application/xml; charset=UTF-16").and().body(object).when().post("/reflect").as(Greeting.class);
        Assert.assertThat(object, Matchers.equalTo(actual));
    }

    @Test
    public void whenRequestContentTypeIsXmlAndDefaultCharsetIsUtf16ThenRestAssuredSerializesToJSON() throws Exception {
        RestAssured.config = encoderConfig(encoderConfig().defaultContentCharset("UTF-16"));
        try {
            final Greeting object = new Greeting();
            object.setFirstName("John");
            object.setLastName("Doe");
            final Greeting actual = given().contentType("application/xml").and().body(object).when().post("/reflect").as(Greeting.class);
            Assert.assertThat(object, Matchers.equalTo(actual));
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void whenRequestContentTypeIsJsonAndCharsetIsUtf16ThenRestAssuredSerializesToJSON() throws Exception {
        final Greeting object = new Greeting();
        object.setFirstName("John");
        object.setLastName("Doe");
        final Greeting actual = given().contentType("application/json; charset=UTF-16").and().body(object).when().post("/reflect").as(Greeting.class);
        Assert.assertThat(object, Matchers.equalTo(actual));
    }

    @Test
    public void mapResponseToObjectUsingJaxbWithJaxObjectMapperDefined() throws Exception {
        final Greeting object = get("/greetXML").as(Greeting.class, JAXB);
        Assert.assertThat(object.getFirstName(), Matchers.equalTo("John"));
        Assert.assertThat(object.getLastName(), Matchers.equalTo("Doe"));
    }

    @Test
    public void mapResponseToObjectUsingJackson1WithJacksonObjectMapperDefined() throws Exception {
        final ScalatraObject object = get("/hello").as(ScalatraObject.class, JACKSON_1);
        Assert.assertThat(object.getHello(), Matchers.equalTo("Hello Scalatra"));
    }

    @Test
    public void mapResponseToObjectUsingJackson2WithJacksonObjectMapperDefined() throws Exception {
        final ScalatraObject object = get("/hello").as(ScalatraObject.class, JACKSON_2);
        Assert.assertThat(object.getHello(), Matchers.equalTo("Hello Scalatra"));
    }

    @Test
    public void mapResponseToObjectUsingGsonWithGsonObjectMapperDefined() throws Exception {
        final ScalatraObject object = get("/hello").as(ScalatraObject.class, GSON);
        Assert.assertThat(object.getHello(), Matchers.equalTo("Hello Scalatra"));
    }

    @Test
    public void serializesUsingJAXBWhenJAXBObjectMapperIsSpecified() throws Exception {
        final Greeting object = new Greeting();
        object.setFirstName("John");
        object.setLastName("Doe");
        final Greeting actual = given().body(object, JAXB).when().post("/reflect").as(Greeting.class, JAXB);
        Assert.assertThat(object, Matchers.equalTo(actual));
    }

    @Test
    public void serializesUsingJAXBWhenJAXBObjectMapperIsSpecifiedForPatchVerb() throws Exception {
        final Greeting object = new Greeting();
        object.setFirstName("John");
        object.setLastName("Doe");
        final Greeting actual = given().body(object, JAXB).when().patch("/reflect").as(Greeting.class, JAXB);
        Assert.assertThat(object, Matchers.equalTo(actual));
    }

    @Test
    public void serializesUsingGsonWhenGsonObjectMapperIsSpecified() throws Exception {
        final Greeting object = new Greeting();
        object.setFirstName("John");
        object.setLastName("Doe");
        final Greeting actual = given().body(object, GSON).when().post("/reflect").as(Greeting.class, GSON);
        Assert.assertThat(object, Matchers.equalTo(actual));
    }

    @Test
    public void serializesUsingJacksonWhenJacksonObjectMapperIsSpecified() throws Exception {
        final Greeting object = new Greeting();
        object.setFirstName("John");
        object.setLastName("Doe");
        final Greeting actual = given().body(object, JACKSON_1).when().post("/reflect").as(Greeting.class, JACKSON_1);
        Assert.assertThat(object, Matchers.equalTo(actual));
    }

    @Test
    public void serializesNormalParams() throws Exception {
        final Greeting object = new Greeting();
        object.setFirstName("John");
        object.setLastName("Doe");
        final Greeting actual = given().contentType(ContentType.JSON).param("something", "something").param("serialized", object).when().put("/serializedJsonParameter").as(Greeting.class);
        Assert.assertThat(actual, Matchers.equalTo(object));
    }
}

