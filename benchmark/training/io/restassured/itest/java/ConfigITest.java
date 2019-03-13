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


import JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.itest.java.support.WithJetty;
import io.restassured.listener.ResponseValidationFailureListener;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ConfigITest extends WithJetty {
    @Test
    public void configCanBeSetPerRequest() throws Exception {
        RestAssured.given().config(RestAssuredConfig.newConfig().redirect(RedirectConfig.redirectConfig().followRedirects(false))).param("url", "/hello").expect().statusCode(302).header("Location", Matchers.is("http://localhost:8080/hello")).when().get("/redirect");
    }

    @Test
    public void supportsSpecifyingDefaultContentCharset() throws Exception {
        RestAssured.given().config(RestAssuredConfig.newConfig().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("US-ASCII"))).body("Something {\\+\u00a3???").expect().header("Content-Type", Matchers.is("text/plain; charset=US-ASCII")).when().post("/reflect");
    }

    @Test
    public void supportsConfiguringJsonConfigProperties() throws Exception {
        RestAssured.given().config(RestAssuredConfig.newConfig().jsonConfig(JsonConfig.jsonConfig().numberReturnType(BIG_DECIMAL))).expect().root("store.book").body("price.min()", Matchers.is(new BigDecimal("8.95"))).body("price.max()", Matchers.is(new BigDecimal("22.99"))).when().get("/jsonStore");
    }

    @Test
    public void supportsConfiguringJsonConfigStatically() throws Exception {
        RestAssured.config = RestAssuredConfig.newConfig().jsonConfig(JsonConfig.jsonConfig().numberReturnType(BIG_DECIMAL));
        try {
            RestAssured.expect().root("store.book").body("price.min()", Matchers.is(new BigDecimal("8.95"))).body("price.max()", Matchers.is(new BigDecimal("22.99"))).when().get("/jsonStore");
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void configurationsDefinedGloballyAreAppliedWhenUsingResponseSpecBuilders() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        ResponseValidationFailureListener failureListener = ( reqSpec, respSpec, resp) -> atomicBoolean.set(true);
        try {
            RestAssured.given().config(failureConfig(failureConfig().failureListeners(failureListener))).get("http://jsonplaceholder.typicode.com/todos/1").then().spec(new ResponseSpecBuilder().expectStatusCode(400).build());
            Assert.fail("Should throw exception");
        } catch (Error ignored) {
        }
        MatcherAssert.assertThat(atomicBoolean.get(), Matchers.is(true));
    }

    @Test
    public void configurationsDefinedInDslAreAppliedWhenUsingResponseSpecBuilders() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        ResponseValidationFailureListener failureListener = ( reqSpec, respSpec, resp) -> atomicBoolean.set(true);
        try {
            RestAssured.given().config(failureConfig(failureConfig().failureListeners(failureListener))).get("http://jsonplaceholder.typicode.com/todos/1").then().spec(new ResponseSpecBuilder().expectStatusCode(400).build());
            Assert.fail("Should throw exception");
        } catch (Error ignored) {
        }
        MatcherAssert.assertThat(atomicBoolean.get(), Matchers.is(true));
    }
}

