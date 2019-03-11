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


import LogDetail.PARAMS;
import LogDetail.STATUS;
import io.restassured.RestAssured;
import io.restassured.authentication.FormAuthConfig;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.filter.session.SessionFilter;
import io.restassured.itest.java.support.WithJetty;
import io.restassured.specification.RequestSpecification;
import java.io.PrintStream;
import java.io.StringWriter;
import org.apache.commons.io.output.WriterOutputStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class AuthenticationITest extends WithJetty {
    @Test
    public void basicAuthentication() throws Exception {
        expect().statusCode(200).when().get("/secured/hello");
    }

    @Test
    public void basicAuthenticationUsingDefault() throws Exception {
        authentication = basic("jetty", "jetty");
        try {
            expect().statusCode(200).when().get("/secured/hello");
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void explicitExcludeOfBasicAuthenticationWhenUsingDefault() throws Exception {
        authentication = basic("jetty", "jetty");
        try {
            expect().statusCode(401).when().get("/secured/hello");
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void supportsExpectingStatusCodeWhenAuthenticationError() throws Exception {
        expect().statusCode(401).when().get("/secured/hello");
    }

    @Test
    public void supportsPreemptiveBasicAuthentication() throws Exception {
        expect().statusCode(200).when().get("/secured/hello");
    }

    @Test
    public void supportsExpectingStatusCodeWhenPreemptiveBasicAuthenticationError() throws Exception {
        expect().statusCode(401).when().get("/secured/hello");
    }

    @Test
    public void preemptiveBasicAuthenticationUsingDefault() throws Exception {
        authentication = basic("jetty", "jetty");
        try {
            expect().statusCode(200).when().get("/secured/hello");
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void explicitExcludeOfPreemptiveBasicAuthenticationWhenUsingDefault() throws Exception {
        authentication = basic("jetty", "jetty");
        try {
            expect().statusCode(401).when().get("/secured/hello");
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void formAuthenticationUsingSpringAuthConf() throws Exception {
        expect().statusCode(200).body(Matchers.equalTo("OK")).when().get("/formAuth");
    }

    @Test
    public void formAuthenticationWithAutoFormDetailsAndAutoCsrfDetection() throws Exception {
        given().auth().form("John", "Doe", formAuthConfig().withAutoDetectionOfCsrf()).when().get("/formAuthCsrf").then().statusCode(200).body(Matchers.equalTo("OK"));
    }

    @Test
    public void formAuthenticationWithDefinedCsrfField() throws Exception {
        given().auth().form("John", "Doe", new FormAuthConfig("j_spring_security_check_with_csrf", "j_username", "j_password").withCsrfFieldName("_csrf")).when().get("/formAuthCsrf").then().statusCode(200).body(Matchers.equalTo("OK"));
    }

    @Test
    public void formAuthenticationWithDefinedCsrfFieldAsHeader() throws Exception {
        given().auth().form("John", "Doe", new FormAuthConfig("j_spring_security_check_with_csrf_header", "j_username", "j_password").withCsrfFieldName("_csrf").sendCsrfTokenAsHeader()).when().get("/formAuthCsrfInHeader").then().statusCode(200).body(Matchers.equalTo("OK"));
    }

    @Test
    public void formAuthenticationWithAdditionalFields() {
        given().auth().form("John", "Doe", formAuthConfig().withAdditionalFields("smquerydata", "smauthreason", "smagentname").withAdditionalField("postpreservationdata")).when().get("/formAuthAdditionalFields").then().statusCode(200).body(Matchers.equalTo("OK"));
    }

    @Test
    public void formAuthenticationWithCsrfAutoDetectionButSpecifiedFormDetails() throws Exception {
        given().auth().form("John", "Doe", new FormAuthConfig("j_spring_security_check_with_csrf", "j_username", "j_password").withAutoDetectionOfCsrf()).when().get("/formAuthCsrf").then().statusCode(200).body(Matchers.equalTo("OK"));
    }

    @Test
    public void formAuthenticationUsingLogging() throws Exception {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        given().auth().form("John", "Doe", FormAuthConfig.springSecurity().withLoggingEnabled(new LogConfig(captor, true))).when().get("/formAuth").then().statusCode(200).body(Matchers.equalTo("OK"));
        Assert.assertThat(writer.toString(), Matchers.equalTo(String.format(("Request method:\tPOST%n" + (((((((((((((((("Request URI:\thttp://localhost:8080/j_spring_security_check%n" + "Proxy:\t\t\t<none>%n") + "Request params:\t<none>%n") + "Query params:\t<none>%n") + "Form params:\tj_username=John%n") + "\t\t\t\tj_password=Doe%n") + "Path params:\t<none>%n") + "Headers:\t\tAccept=*/*%n") + "\t\t\t\tContent-Type=application/x-www-form-urlencoded; charset=%s%n") + "Cookies:\t\t<none>%n") + "Multiparts:\t\t<none>%n") + "Body:\t\t\t<none>%n") + "HTTP/1.1 200 OK%n") + "Content-Type: text/plain;charset=utf-8%n") + "Set-Cookie: jsessionid=1234%n") + "Content-Length: 0%n") + "Server: Jetty(9.3.2.v20150730)%n")), RestAssured.config().getEncoderConfig().defaultContentCharset())));
    }

    @Test
    public void formAuthenticationUsingLoggingWithLogDetailEqualToParams() throws Exception {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        given().auth().form("John", "Doe", FormAuthConfig.springSecurity().withLoggingEnabled(PARAMS, new LogConfig(captor, true))).when().get("/formAuth").then().statusCode(200).body(Matchers.equalTo("OK"));
        Assert.assertThat(writer.toString(), Matchers.equalTo(String.format(("Request params:\t<none>%n" + (((("Query params:\t<none>%n" + "Form params:\tj_username=John%n") + "\t\t\t\tj_password=Doe%n") + "Path params:\t<none>%n") + "Multiparts:\t\t<none>%n")))));
    }

    @Test
    public void formAuthenticationUsingLoggingWithLogDetailEqualToStatus() throws Exception {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        given().auth().form("John", "Doe", FormAuthConfig.springSecurity().withLoggingEnabled(STATUS, new LogConfig(captor, true))).when().get("/formAuth").then().statusCode(200).body(Matchers.equalTo("OK"));
        Assert.assertThat(writer.toString(), Matchers.equalTo(String.format("HTTP/1.1 200 OK%n")));
    }

    @Test
    public void formAuthenticationUsingSpringAuthConfDefinedInRequestSpec() throws Exception {
        final RequestSpecification specification = new RequestSpecBuilder().setAuth(form("John", "Doe", FormAuthConfig.springSecurity())).build();
        expect().statusCode(200).body(Matchers.equalTo("OK")).when().get("/formAuth");
    }

    @Test
    public void formAuthenticationWithLoginPageParsing() throws Exception {
        expect().statusCode(200).body(Matchers.equalTo("OK")).when().get("/formAuth");
    }

    @Test
    public void sessionFilterRecordsAndProvidesTheSessionId() throws Exception {
        final SessionFilter sessionFilter = new SessionFilter();
        expect().statusCode(200).body(Matchers.equalTo("OK")).when().get("/formAuth");
        Assert.assertThat(sessionFilter.getSessionId(), Matchers.equalTo("1234"));
    }

    @Test
    public void reusingSameSessionFilterInDifferentRequestsAppliesTheSessionIdToTheNewRequest() throws Exception {
        final SessionFilter sessionFilter = new SessionFilter();
        expect().statusCode(200).body(Matchers.equalTo("OK")).when().get("/formAuth");
        expect().statusCode(200).body(Matchers.equalTo("OK")).when().get("/formAuth");
    }

    @Test
    public void sessionFilterRecordsAndProvidesTheSessionIdWhenSessionNameIsNotDefault() throws Exception {
        final SessionFilter sessionFilter = new SessionFilter();
        expect().statusCode(200).body(Matchers.equalTo("OK")).when().get("/formAuth");
        Assert.assertThat(sessionFilter.getSessionId(), Matchers.equalTo("1234"));
    }

    @Test
    public void formAuthenticationUsingDefaultWithLoginPageParsing() throws Exception {
        RestAssured.authentication = form("John", "Doe");
        try {
            expect().statusCode(200).body(Matchers.equalTo("OK")).when().get("/formAuth");
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void formAuthenticationUsingDefaultWithSpringAuthConf() throws Exception {
        RestAssured.authentication = form("John", "Doe", FormAuthConfig.springSecurity());
        try {
            expect().statusCode(200).body(Matchers.equalTo("OK")).when().get("/formAuth");
        } finally {
            RestAssured.reset();
        }
    }

    /**
     * Asserts that <a href="http://code.google.com/p/rest-assured/issues/detail?id=95">issue 95</a> is resolved.
     */
    @Test
    public void canSpecifyPortWhenUsingFormAuth() throws Exception {
        RestAssured.port = 8091;// Specify an unused port

        try {
            expect().statusCode(200).body(Matchers.equalTo("OK")).when().get("/formAuth");
        } finally {
            RestAssured.port = 8080;
        }
    }

    /**
     * Asserts that <a href="http://code.google.com/p/rest-assured/issues/detail?id=233">issue 233</a> is resolved.
     */
    @Test
    public void canOverridePreemptiveBasicAuthFromStaticConfiguration() throws Exception {
        RestAssured.authentication = basic("invalid", "password");
        try {
            expect().statusCode(200).when().get("/secured/hello");
        } finally {
            RestAssured.reset();
        }
    }
}

