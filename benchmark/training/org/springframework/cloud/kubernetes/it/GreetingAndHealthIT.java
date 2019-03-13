/**
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.kubernetes.it;


import io.restassured.RestAssured;
import org.arquillian.cube.kubernetes.impl.requirement.RequiresKubernetes;
import org.hamcrest.core.Is;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;


@RequiresKubernetes
@RunWith(Arquillian.class)
public class GreetingAndHealthIT {
    private static final String HOST = System.getProperty("service.host");

    private static final Integer PORT = Integer.valueOf(System.getProperty("service.port"));

    @Test
    public void testGreetingEndpoint() {
        RestAssured.given().baseUri(String.format("http://%s:%d", GreetingAndHealthIT.HOST, GreetingAndHealthIT.PORT)).get("greeting").then().statusCode(200).body("message", Is.is("Hello Spring Boot"));
    }

    @Test
    public void testHealthEndpoint() {
        RestAssured.given().baseUri(String.format("http://%s:%d", GreetingAndHealthIT.HOST, GreetingAndHealthIT.PORT)).contentType("application/json").get("actuator/health").then().statusCode(200).body("details.kubernetes.details.inside", Is.is(true));
    }
}

