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


import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.itest.java.objects.Greeting;
import io.restassured.itest.java.objects.Message;
import io.restassured.itest.java.support.WithJetty;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;
import io.restassured.path.json.mapper.factory.GsonObjectMapperFactory;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CustomObjectMappingITest extends WithJetty {
    public AtomicBoolean customSerializationUsed = new AtomicBoolean(false);

    public AtomicBoolean customDeserializationUsed = new AtomicBoolean(false);

    @Test
    public void using_explicit_custom_object_mapper() throws Exception {
        final Message message = new Message();
        message.setMessage("A message");
        final ObjectMapper mapper = new ObjectMapper() {
            public Object deserialize(ObjectMapperDeserializationContext context) {
                final String toDeserialize = context.getDataToDeserialize().asString();
                final String unquoted = StringUtils.remove(toDeserialize, "#");
                final Message message = new Message();
                message.setMessage(unquoted);
                customDeserializationUsed.set(true);
                return message;
            }

            public Object serialize(ObjectMapperSerializationContext context) {
                final Message objectToSerialize = context.getObjectToSerializeAs(Message.class);
                final String message = objectToSerialize.getMessage();
                customSerializationUsed.set(true);
                return ("##" + message) + "##";
            }
        };
        final Message returnedMessage = RestAssured.given().body(message, mapper).when().post("/reflect").as(Message.class, mapper);
        Assert.assertThat(returnedMessage.getMessage(), Matchers.equalTo("A message"));
        Assert.assertThat(customSerializationUsed.get(), Matchers.is(true));
        Assert.assertThat(customDeserializationUsed.get(), Matchers.is(true));
    }

    @Test
    public void using_custom_object_mapper_statically() {
        final Message message = new Message();
        message.setMessage("A message");
        final ObjectMapper mapper = new ObjectMapper() {
            public Object deserialize(ObjectMapperDeserializationContext context) {
                final String toDeserialize = context.getDataToDeserialize().asString();
                final String unquoted = StringUtils.remove(toDeserialize, "##");
                final Message message = new Message();
                message.setMessage(unquoted);
                customDeserializationUsed.set(true);
                return message;
            }

            public Object serialize(ObjectMapperSerializationContext context) {
                final Message objectToSerialize = context.getObjectToSerializeAs(Message.class);
                final String message = objectToSerialize.getMessage();
                customSerializationUsed.set(true);
                return ("##" + message) + "##";
            }
        };
        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(new io.restassured.config.ObjectMapperConfig(mapper));
        final Message returnedMessage = RestAssured.given().body(message).when().post("/reflect").as(Message.class);
        Assert.assertThat(returnedMessage.getMessage(), Matchers.equalTo("A message"));
        Assert.assertThat(customSerializationUsed.get(), Matchers.is(true));
        Assert.assertThat(customDeserializationUsed.get(), Matchers.is(true));
    }

    @Test
    public void using_default_object_mapper_type_if_specified() {
        final Message message = new Message();
        message.setMessage("A message");
        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(new io.restassured.config.ObjectMapperConfig(GSON));
        final Message returnedMessage = RestAssured.given().body(message).when().post("/reflect").as(Message.class);
        Assert.assertThat(returnedMessage.getMessage(), Matchers.equalTo("A message"));
    }

    @Test
    public void using_as_specified_object() {
        final Message message = new Message();
        message.setMessage("A message");
        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(new io.restassured.config.ObjectMapperConfig(GSON));
        final String returnedMessage = RestAssured.given().body(message).when().post("/reflect").as(Message.class).getMessage();
        Assert.assertThat(returnedMessage, Matchers.equalTo("A message"));
    }

    @Test
    public void using_custom_object_mapper_factory() {
        final Greeting greeting = new Greeting();
        greeting.setFirstName("John");
        greeting.setLastName("Doe");
        RestAssured.config = objectMapperConfig(objectMapperConfig().gsonObjectMapperFactory(new GsonObjectMapperFactory() {
            public Gson create(Type cls, String charset) {
                return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
            }
        }));
        final Greeting returnedGreeting = RestAssured.given().contentType("application/json").body(greeting, GSON).expect().body("first_name", Matchers.equalTo("John")).when().post("/reflect").as(Greeting.class, GSON);
        Assert.assertThat(returnedGreeting.getFirstName(), Matchers.equalTo("John"));
        Assert.assertThat(returnedGreeting.getLastName(), Matchers.equalTo("Doe"));
    }
}

