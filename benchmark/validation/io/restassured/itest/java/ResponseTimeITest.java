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
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.itest.java.support.WithJetty;
import io.restassured.specification.ResponseSpecification;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;

import static io.restassured.RestAssured.given;


public class ResponseTimeITest extends WithJetty {
    @Test
    public void response_time_can_be_extracted() {
        long time = given().param("firstName", "John").param("lastName", "Doe").when().get("/greet").then().extract().response().time();
        Assert.assertThat(time, greaterThan(0L));
    }

    @Test
    public void response_time_can_be_be_converted() {
        long timeNanos = RestAssured.get("/lotto").timeIn(TimeUnit.NANOSECONDS);
        long timeMillis = RestAssured.get("/lotto").timeIn(TimeUnit.MILLISECONDS);
        Assert.assertThat(timeNanos, greaterThan(0L));
        Assert.assertThat(timeMillis, greaterThan(0L));
        Assert.assertThat(timeNanos, greaterThan(timeMillis));
    }

    @Test
    public void response_time_can_be_validated_with_implicit_time_unit() {
        given().param("firstName", "John").param("lastName", "Doe").when().get("/greet").then().time(allOf(greaterThan(0L), lessThan(2000L)));
    }

    @Test
    public void response_time_can_be_validated_with_explicit_time_unit() {
        given().param("firstName", "John").param("lastName", "Doe").when().get("/greet").then().time(lessThan(2L), TimeUnit.SECONDS);
    }

    @Test
    public void response_time_validation_can_fail() {
        exception.expect(AssertionError.class);
        exception.expectMessage(allOf(containsString("Expected response time was not a value greater than <2L> days, was "), endsWith(" milliseconds (0 days).")));
        given().param("firstName", "John").param("lastName", "Doe").when().get("/greet").then().time(greaterThan(2L), TimeUnit.DAYS);
    }

    @Test
    public void response_time_validation_can_be_specified_in_specification() {
        ResponseSpecification spec = new ResponseSpecBuilder().expectResponseTime(lessThanOrEqualTo(3000L)).build();
        given().param("firstName", "John").param("lastName", "Doe").when().get("/greet").then().spec(spec);
    }

    @Test
    public void response_time_validation_can_be_specified_in_specification_using_time_unit() {
        ResponseSpecification spec = new ResponseSpecBuilder().expectResponseTime(lessThanOrEqualTo(3L), TimeUnit.SECONDS).build();
        given().param("firstName", "John").param("lastName", "Doe").when().get("/greet").then().spec(spec);
    }

    @Test
    public void response_time_validation_can_fail_when_specified_in_specification() {
        exception.expect(AssertionError.class);
        exception.expectMessage("Expected response time was not a value less than or equal to <3L> nanoseconds, was");
        ResponseSpecification spec = new ResponseSpecBuilder().expectResponseTime(lessThanOrEqualTo(3L), TimeUnit.NANOSECONDS).build();
        given().param("firstName", "John").param("lastName", "Doe").when().get("/greet").then().spec(spec);
    }

    @Test
    public void can_use_response_time_validation_in_legacy_syntax() {
        given().param("firstName", "John").param("lastName", "Doe").expect().time(lessThan(2000L)).when().get("/greet");
    }
}

