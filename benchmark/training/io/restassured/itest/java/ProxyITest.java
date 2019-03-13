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
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.itest.java.support.WithJetty;
import io.restassured.specification.RequestSpecification;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.io.output.WriterOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.littleshoot.proxy.HttpProxyServer;


public class ProxyITest extends WithJetty {
    static HttpProxyServer proxyServer;

    @Test
    public void using_proxy_with_hostname_and_port() {
        RestAssured.given().proxy("127.0.0.1", 8888).param("firstName", "John").param("lastName", "Doe").when().get("/greetJSON").then().header("Via", not(isEmptyOrNullString()));
    }

    @Test
    public void using_proxy_with_hostname() {
        RestAssured.given().proxy("127.0.0.1").param("firstName", "John").param("lastName", "Doe").when().get("/greetJSON").then().header("Via", not(isEmptyOrNullString()));
    }

    @Test
    public void using_proxy_with_hostname_as_a_uri() {
        RestAssured.given().proxy("http://127.0.0.1:8888").param("firstName", "John").param("lastName", "Doe").when().get("/greetJSON").then().header("Via", not(isEmptyOrNullString()));
    }

    @Test
    public void using_proxy_with_uri() throws URISyntaxException {
        RestAssured.given().proxy(new URI("http://127.0.0.1:8888")).param("firstName", "John").param("lastName", "Doe").when().get("/greetJSON").then().header("Via", not(isEmptyOrNullString()));
    }

    @Test
    public void using_proxy_with_proxy_specification() {
        RestAssured.given().proxy(host("localhost").and().withPort(8888).and().withScheme("http")).param("firstName", "John").param("lastName", "Doe").when().get("/greetJSON").then().header("Via", not(isEmptyOrNullString())).body("greeting.firstName", equalTo("John")).body("greeting.lastName", equalTo("Doe"));
    }

    @Test
    public void using_proxy_with_specification() {
        RequestSpecification specification = new RequestSpecBuilder().setProxy("localhost").build();
        RestAssured.given().specification(specification).param("firstName", "John").param("lastName", "Doe").when().get("/greetJSON").then().header("Via", not(isEmptyOrNullString()));
    }

    @Test
    public void using_statically_configured_proxy_defined_using_method() {
        RestAssured.proxy("http://127.0.0.1:8888");
        try {
            RestAssured.given().param("firstName", "John").param("lastName", "Doe").when().get("/greetJSON").then().header("Via", not(isEmptyOrNullString()));
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void using_statically_configured_proxy_defined_using_field() {
        RestAssured.proxy = host("127.0.0.1").withPort(8888);
        try {
            RestAssured.given().param("firstName", "John").param("lastName", "Doe").when().get("/greetJSON").then().header("Via", not(isEmptyOrNullString()));
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void using_statically_configured_proxy_defined_using_string_uri_without_port() {
        exception.expect(ConnectException.class);// Because it will try to connect to port 80

        RestAssured.proxy("http://127.0.0.1");
        try {
            RestAssured.given().param("firstName", "John").param("lastName", "Doe").when().get("/greetJSON").then().header("Via", not(isEmptyOrNullString()));
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void proxy_details_are_shown_in_the_request_log() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        RestAssured.given().filter(new RequestLoggingFilter(captor)).proxy("127.0.0.1").param("firstName", "John").param("lastName", "Doe").when().get("/greetJSON").then().header("Via", not(isEmptyOrNullString()));
        Assert.assertThat(writer.toString(), equalTo(String.format(("Request method:\tGET%n" + (((((((((("Request URI:\thttp://localhost:8080/greetJSON?firstName=John&lastName=Doe%n" + "Proxy:\t\t\thttp://127.0.0.1:8888%n") + "Request params:\tfirstName=John%n") + "\t\t\t\tlastName=Doe%n") + "Query params:\t<none>%n") + "Form params:\t<none>%n") + "Path params:\t<none>%n") + "Headers:\t\tAccept=*/*%n") + "Cookies:\t\t<none>%n") + "Multiparts:\t\t<none>%n") + "Body:\t\t\t<none>%n")))));
    }
}

