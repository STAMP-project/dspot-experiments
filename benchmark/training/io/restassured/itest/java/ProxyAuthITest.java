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
import org.hamcrest.Matchers;
import org.junit.Test;
import org.littleshoot.proxy.HttpProxyServer;


public class ProxyAuthITest extends WithJetty {
    static HttpProxyServer proxyServer;

    @Test
    public void using_proxy_with_host_port_and_auth() {
        RestAssured.given().proxy(host("127.0.0.1").withPort(8888).withAuth("admin", "pass")).param("firstName", "John").param("lastName", "Doe").when().get("/greetJSON").then().header("Via", Matchers.not(Matchers.isEmptyOrNullString()));
    }

    @Test
    public void using_proxy_with_auth() {
        RestAssured.given().proxy(auth("admin", "pass")).param("firstName", "John").param("lastName", "Doe").when().get("/greetJSON").then().header("Via", Matchers.not(Matchers.isEmptyOrNullString()));
    }

    @Test
    public void using_proxy_without_ok_auth() {
        RestAssured.given().proxy(host("127.0.0.1").withPort(8888).withAuth("wrong", "pass")).param("firstName", "John").param("lastName", "Doe").when().get("/greetJSON").then().statusCode(407);
    }
}

