/**
 * Copyright 2019 the original author or authors.
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
import io.restassured.listener.ResponseValidationFailureListener;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class FailureConfigITest extends WithJetty {
    @Test
    public void it_is_possible_to_configure_rest_assured_to_use_a_response_validation_failure_listener() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        ResponseValidationFailureListener responseValidationFailureListener = ( reqSpec, respSpec, resp) -> atomicBoolean.set(true);
        try {
            RestAssured.given().config(failureConfig(failureConfig().with().failureListeners(responseValidationFailureListener))).when().get("/reflect").then().assertThat().statusCode(400);
            Assert.fail(("Should have thrown an " + (AssertionError.class.getSimpleName())));
        } catch (AssertionError ignored) {
            // Status code is actually 200
        }
        Assert.assertThat(atomicBoolean.get(), Matchers.is(true));
    }
}

