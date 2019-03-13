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


import ContentType.JSON;
import ContentType.XML;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.filter.Filter;
import io.restassured.filter.log.LogDetail;
import io.restassured.itest.java.objects.Greeting;
import io.restassured.itest.java.objects.Message;
import io.restassured.itest.java.objects.ScalatraObject;
import io.restassured.itest.java.support.WithJetty;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.WriterOutputStream;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Test;


public class LoggingITest extends WithJetty {
    @Test
    public void errorLoggingFilterWorks() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/409");
        Assert.assertThat(writer.toString(), containsString("ERROR"));
    }

    @Test
    public void logErrorsUsingRequestSpec() {
        when().get("/409");
    }

    @Test
    public void logUsingRequestSpec() {
        when().get("/409");
    }

    @Test
    public void logUsingResponseSpec() {
        when().get("/409");
    }

    @Test
    public void logUsingResponseSpecLogDetail() {
        when().get("/409");
    }

    @Test
    public void logResponseThatHasCookiesWithLogDetailAll() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/multiCookie");
        Assert.assertThat(writer.toString(), allOf(startsWith(String.format(("HTTP/1.1 200 OK%n" + (("Content-Type: text/plain;charset=utf-8%n" + "Set-Cookie: cookie1=cookieValue1;Domain=localhost%n") + "Expires:")))), containsString("Set-Cookie: cookie1=cookieValue2;Version=1;Path=/;Domain=localhost;Expires="), endsWith(String.format((";Max-Age=1234567;Secure;Comment=\"My Purpose\"%n" + ((("Content-Length: 2%n" + "Server: Jetty(9.3.2.v20150730)%n") + "%n") + "OK%n"))))));
    }

    @Test
    public void logResponseThatHasCookiesWithLogDetailCookies() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/multiCookie");
        Assert.assertThat(writer.toString(), allOf(startsWith("cookie1=cookieValue1;Domain=localhost\ncookie1=cookieValue2;Comment=\"My Purpose\";Path=/;Domain=localhost;Max-Age=1234567;Secure;Expires="), endsWith(String.format(";Version=1%n"))));
    }

    @Test
    public void loggingResponseFilterLogsErrors() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/409");
        Assert.assertThat(writer.toString(), containsString("ERROR"));
    }

    @Test
    public void loggingResponseFilterLogsNonErrors() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/greet?firstName=John&lastName=Doe");
        Assert.assertThat(writer.toString(), containsString("{\"greeting\":\"Greetings John Doe\"}"));
    }

    @Test
    public void loggingByResponseSpecLogDetailNonErrors() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/greet?firstName=John&lastName=Doe");
        Assert.assertThat(writer.toString(), containsString("{\"greeting\":\"Greetings John Doe\"}"));
    }

    @Test
    public void loggingResponseFilterLogsToSpecifiedWriterWhenMatcherIsFulfilled() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/greet?firstName=John&lastName=Doe");
        Assert.assertThat(writer.toString(), containsString("{\"greeting\":\"Greetings John Doe\"}"));
    }

    @Test
    public void loggingResponseFilterDoesntLogWhenSpecifiedMatcherIsNotFulfilled() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/greet?firstName=John&lastName=Doe");
        Assert.assertThat(writer.toString(), is(""));
    }

    @Test
    public void loggingResponseFilterLogsWhenExpectationsFail() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        try {
            when().get("/greet?firstName=John&lastName=Doe");
            Assert.fail("Should throw exception");
        } catch (AssertionError e) {
            Assert.assertThat(writer.toString(), containsString("{\"greeting\":\"Greetings John Doe\"}"));
        }
    }

    @Test
    public void loggingRequestFilterWithParamsCookiesAndHeaders() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().post("/greet");
        Assert.assertThat(writer.toString(), equalTo(String.format(("Request method:\tPOST%n" + ((((((((((((((((((((("Request URI:\thttp://localhost:8080/greet?something1=else1&something2=else2&something3=else3%n" + "Proxy:\t\t\t<none>%n") + "Request params:\thello1=world1%n") + "\t\t\t\thello2=world2%n") + "\t\t\t\tmultiParam=[multi1, multi2]%n") + "Query params:\tsomething1=else1%n") + "\t\t\t\tsomething2=else2%n") + "\t\t\t\tsomething3=else3%n") + "Form params:\tfirstName=John%n") + "\t\t\t\tlastName=Doe%n") + "Path params:\t<none>%n") + "Headers:\t\tmultiHeader=headerValue1%n") + "\t\t\t\tmultiHeader=headerValue2%n") + "\t\t\t\tstandardHeader=standard header value%n") + "\t\t\t\tAccept=*/*%n") + "") + "\t\t\t\tContent-Type=application/x-www-form-urlencoded; charset=%s%n") + "Cookies:\t\tmultiCookie=value1%n") + "\t\t\t\tmultiCookie=value2%n") + "\t\t\t\tstandardCookie=standard value%n") + "Multiparts:\t\t<none>%n") + "Body:\t\t\t<none>%n")), RestAssured.config().getEncoderConfig().defaultContentCharset())));
    }

    @Test(expected = IllegalArgumentException.class)
    public void loggingRequestFilterDoesntAcceptStatusAsLogDetail() {
        new io.restassured.filter.log.RequestLoggingFilter(LogDetail.STATUS);
    }

    @Test
    public void loggingRequestFilterWithExplicitContentType() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/greet");
        Assert.assertThat(writer.toString(), equalTo(String.format(("Request method:\tGET%n" + ((((((((((("Request URI:\thttp://localhost:8080/greet?firstName=John&lastName=Doe%n" + "Proxy:\t\t\t<none>%n") + "Request params:\tfirstName=John%n") + "\t\t\t\tlastName=Doe%n") + "Query params:\t<none>%n") + "Form params:\t<none>%n") + "Path params:\t<none>%n") + "Headers:\t\tAccept=*/*%n") + "\t\t\t\tContent-Type=application/json; charset=%s%n") + "Cookies:\t\t<none>%n") + "Multiparts:\t\t<none>%n") + "Body:\t\t\t<none>%n")), RestAssured.config().getEncoderConfig().defaultCharsetForContentType(JSON))));
    }

    @Test
    public void loggingRequestFilterPathParams() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/{firstName}/{lastName}");
        Assert.assertThat(writer.toString(), equalTo(String.format(("Request method:\tGET%n" + (((((((((("Request URI:\thttp://localhost:8080/John/Doe%n" + "Proxy:\t\t\t<none>%n") + "Request params:\t<none>%n") + "Query params:\t<none>%n") + "Form params:\t<none>%n") + "Path params:\tfirstName=John%n") + "\t\t\t\tlastName=Doe%n") + "Headers:\t\tAccept=*/*%n") + "Cookies:\t\t<none>%n") + "Multiparts:\t\t<none>%n") + "Body:\t\t\t<none>%n")))));
    }

    @Test
    public void loggingRequestFilterWithBody() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        final ScalatraObject object = new ScalatraObject();
        object.setHello("Hello world");
        when().post("/reflect");
        Assert.assertThat(writer.toString(), equalTo(String.format(("Request method:\tPOST%n" + ((((((((((("Request URI:\thttp://localhost:8080/reflect%n" + "Proxy:\t\t\t<none>%n") + "Request params:\t<none>%n") + "Query params:\t<none>%n") + "Form params:\t<none>%n") + "Path params:\t<none>%n") + "Headers:\t\tAccept=*/*%n") + "\t\t\t\tContent-Type=text/plain; charset=%s%n") + "Cookies:\t\t<none>%n") + "Multiparts:\t\t<none>%n") + "Body:%n") + "{\"hello\":\"Hello world\"}%n")), RestAssured.config().getEncoderConfig().defaultContentCharset())));
    }

    @Test
    public void loggingRequestAndResponseAtTheSameTimeWhenRequestFilterIsAddedBeforeResponseFilter() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        final ScalatraObject object = new ScalatraObject();
        object.setHello("Hello world");
        when().post("/reflect");
        Assert.assertThat(writer.toString(), equalTo(String.format(("Request method:\tPOST%n" + ((((((((((((((((("Request URI:\thttp://localhost:8080/reflect%n" + "Proxy:\t\t\t<none>%n") + "Request params:\t<none>%n") + "Query params:\t<none>%n") + "Form params:\t<none>%n") + "Path params:\t<none>%n") + "Headers:\t\tAccept=*/*%n") + "\t\t\t\tContent-Type=text/plain; charset=%s%n") + "Cookies:\t\t<none>%n") + "Multiparts:\t\t<none>%n") + "Body:%n") + "{\"hello\":\"Hello world\"}%n") + "HTTP/1.1 200 OK%n") + "Content-Type: text/plain;charset=iso-8859-1%n") + "Content-Length: 23%n") + "Server: Jetty(9.3.2.v20150730)%n") + "%n") + "{\"hello\":\"Hello world\"}%n")), RestAssured.config().getEncoderConfig().defaultContentCharset())));
    }

    @Test
    public void loggingRequestAndResponseAtTheSameTimeWhenResponseFilterIsAddedBeforeRequestFilter() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        final ScalatraObject object = new ScalatraObject();
        object.setHello("Hello world");
        when().post("/reflect");
        Assert.assertThat(writer.toString(), equalTo(String.format(("Request method:\tPOST%n" + ((((((((((((((((("Request URI:\thttp://localhost:8080/reflect%n" + "Proxy:\t\t\t<none>%n") + "Request params:\t<none>%n") + "Query params:\t<none>%n") + "Form params:\t<none>%n") + "Path params:\t<none>%n") + "Headers:\t\tAccept=*/*%n") + "\t\t\t\tContent-Type=text/plain; charset=%s%n") + "Cookies:\t\t<none>%n") + "Multiparts:\t\t<none>%n") + "Body:%n") + "{\"hello\":\"Hello world\"}%n") + "HTTP/1.1 200 OK%n") + "Content-Type: text/plain;charset=iso-8859-1%n") + "Content-Length: 23%n") + "Server: Jetty(9.3.2.v20150730)%n") + "%n") + "{\"hello\":\"Hello world\"}%n")), RestAssured.config().getEncoderConfig().defaultContentCharset())));
    }

    @Test
    public void logEverythingResponseUsingLogSpec() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/{firstName}/{lastName}");
        Assert.assertThat(writer.toString(), equalTo(String.format(("HTTP/1.1 200 OK%n" + (((("Content-Type: application/json;charset=utf-8%n" + "Content-Length: 59%n") + "Server: Jetty(9.3.2.v20150730)%n") + "%n") + "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"fullName\":\"John Doe\"}%n")))));
    }

    @Test
    public void logIfStatusCodeIsEqualToResponseUsingLogSpec() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/409");
        Assert.assertThat(writer.toString(), equalTo(String.format(("HTTP/1.1 409 Conflict%n" + (((("Content-Type: text/plain;charset=utf-8%n" + "Content-Length: 5%n") + "Server: Jetty(9.3.2.v20150730)%n") + "%n") + "ERROR%n")))));
    }

    @Test
    public void doesntLogIfStatusCodeIsNotEqualToResponseUsingLogSpec() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/409");
        Assert.assertThat(writer.toString(), equalTo(""));
    }

    @Test
    public void logIfStatusCodeMatchesResponseUsingLogSpec() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/409");
        Assert.assertThat(writer.toString(), equalTo(String.format(("HTTP/1.1 409 Conflict%n" + (((("Content-Type: text/plain;charset=utf-8%n" + "Content-Length: 5%n") + "Server: Jetty(9.3.2.v20150730)%n") + "%n") + "ERROR%n")))));
    }

    @Test
    public void logOnlyBodyUsingResponseUsingLogSpec() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/{firstName}/{lastName}");
        Assert.assertThat(writer.toString(), equalTo(String.format("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"fullName\":\"John Doe\"}%n")));
    }

    @Test
    public void logOnlyResponseBodyWithPrettyPrintingWhenJson() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/{firstName}/{lastName}");
        Assert.assertThat(writer.toString(), equalTo(String.format(("{\n" + ((("    \"firstName\": \"John\",\n" + "    \"lastName\": \"Doe\",\n") + "    \"fullName\": \"John Doe\"\n") + "}%n")))));
    }

    @Test
    public void logOnlyResponseBodyWithPrettyPrintingWhenXml() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/videos-not-formatted");
        Assert.assertThat(writer.toString(), equalTo(String.format(("<videos>\n" + ((((((((("  <music>\n" + "    <title>Video Title 1</title>\n") + "    <artist>Artist 1</artist>\n") + "  </music>\n") + "  <music>\n") + "    <title>Video Title 2</title>\n") + "    <artist>Artist 2</artist>\n") + "    <artist>Artist 3</artist>\n") + "  </music>\n") + "</videos>%n")))));
    }

    @Test
    public void logOnlyResponseBodyWithPrettyPrintingWhenHtml() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/textHTML-not-formatted");
        Assert.assertThat(writer.toString(), equalTo(String.format(("<html>\n" + ((((((("  <head>\n" + "    <title>my title</title>\n") + "  </head>\n") + "  <body>\n") + "    <p>paragraph 1</p>\n") + "    <p>paragraph 2</p>\n") + "  </body>\n") + "</html>%n")))));
    }

    @Test
    public void logAllWithPrettyPrintingWhenJson() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/{firstName}/{lastName}");
        Assert.assertThat(writer.toString(), equalTo(String.format(("HTTP/1.1 200 OK%n" + (((((((("Content-Type: application/json;charset=utf-8%n" + "Content-Length: 59%n") + "Server: Jetty(9.3.2.v20150730)%n") + "%n") + "{\n") + "    \"firstName\": \"John\",\n") + "    \"lastName\": \"Doe\",\n") + "    \"fullName\": \"John Doe\"\n") + "}%n")))));
    }

    @Test
    public void logAllWithPrettyPrintingUsingDSLWhenJson() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/{firstName}/{lastName}");
        Assert.assertThat(writer.toString(), equalTo(String.format(("HTTP/1.1 200 OK%n" + (((((((("Content-Type: application/json;charset=utf-8%n" + "Content-Length: 59%n") + "Server: Jetty(9.3.2.v20150730)%n") + "%n") + "{\n") + "    \"firstName\": \"John\",\n") + "    \"lastName\": \"Doe\",\n") + "    \"fullName\": \"John Doe\"\n") + "}%n")))));
    }

    @Test
    public void logAllWithNoPrettyPrintingUsingDSLWhenJson() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/{firstName}/{lastName}");
        Assert.assertThat(writer.toString(), equalTo(String.format(("HTTP/1.1 200 OK%n" + (((("Content-Type: application/json;charset=utf-8%n" + "Content-Length: 59%n") + "Server: Jetty(9.3.2.v20150730)%n") + "%n") + "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"fullName\":\"John Doe\"}%n")))));
    }

    @Test
    public void logOnlyResponseBodyWithPrettyPrintingUsingDSLWhenXml() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/videos-not-formatted");
        Assert.assertThat(writer.toString(), equalTo(String.format(("<videos>\n" + ((((((((("  <music>\n" + "    <title>Video Title 1</title>\n") + "    <artist>Artist 1</artist>\n") + "  </music>\n") + "  <music>\n") + "    <title>Video Title 2</title>\n") + "    <artist>Artist 2</artist>\n") + "    <artist>Artist 3</artist>\n") + "  </music>\n") + "</videos>%n")))));
    }

    @Test
    public void logOnlyResponseBodyWithNoPrettyPrintingUsingDSLWhenXml() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/videos-not-formatted");
        Assert.assertThat(writer.toString(), equalTo(String.format("<videos><music><title>Video Title 1</title><artist>Artist 1</artist></music><music><title>Video Title 2</title><artist>Artist 2</artist><artist>Artist 3</artist></music></videos>%n")));
    }

    @Test
    public void logOnlyStatusUsingResponseUsingLogSpec() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/{firstName}/{lastName}");
        Assert.assertThat(writer.toString(), equalTo(String.format("HTTP/1.1 200 OK%n")));
    }

    @Test
    public void logOnlyHeadersUsingResponseUsingLogSpec() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/{firstName}/{lastName}");
        Assert.assertThat(writer.toString(), equalTo(String.format(("Content-Type: application/json;charset=utf-8%n" + ("Content-Length: 59%n" + "Server: Jetty(9.3.2.v20150730)%n")))));
    }

    @Test
    public void logOnlyHeadersUsingResponseUsingLogSpecWhenMultiHeaders() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/multiValueHeader");
        Assert.assertThat(writer.toString(), equalTo(String.format(("Content-Type: text/plain;charset=utf-8%n" + ((("MultiHeader: Value 1%n" + "MultiHeader: Value 2%n") + "Content-Length: 0%n") + "Server: Jetty(9.3.2.v20150730)%n")))));
    }

    @Test
    public void logOnlyCookiesUsingResponseLogSpec() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/multiCookie");
        Assert.assertThat(writer.toString(), allOf(startsWith(("cookie1=cookieValue1;Domain=localhost\n" + "cookie1=cookieValue2;Comment=\"My Purpose\";Path=/;Domain=localhost;Max-Age=1234567;Secure;Expires=")), endsWith(String.format(";Version=1%n"))));
    }

    @Test
    public void logBodyPrettyPrintedUsingResponseLogSpecWhenContentTypeDoesntMatchContent() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/contentTypeJsonButContentIsNotJson");
        Assert.assertThat(writer.toString(), equalTo(String.format(("HTTP/1.1 200 OK%n" + (((("Content-Type: application/json;charset=utf-8%n" + "Content-Length: 33%n") + "Server: Jetty(9.3.2.v20150730)%n") + "%n") + "This is not a valid JSON document%n")))));
    }

    @Test
    public void logAllUsingRequestLogSpec() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/greetJSON");
        Assert.assertThat(writer.toString(), equalTo(String.format(("Request method:\tGET%n" + ((((((((("Request URI:\thttp://localhost:8080/greetJSON?firstName=John&lastName=Doe%n" + "Proxy:\t\t\t<none>%n") + "Request params:\tfirstName=John%n") + "Query params:\tlastName=Doe%n") + "Form params:\t<none>%n") + "Path params:\t<none>%n") + "Headers:\t\tAccept=*/*%n") + "Cookies:\t\t<none>%n") + "Multiparts:\t\t<none>%n") + "Body:\t\t\t<none>%n")))));
    }

    @Test
    public void logParamsUsingRequestLogSpec() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/greetJSON");
        Assert.assertThat(writer.toString(), equalTo(String.format(("Request params:\tfirstName=John%n" + ((("Query params:\tlastName=Doe%n" + "Form params:\t<none>%n") + "Path params:\t<none>%n") + "Multiparts:\t\t<none>%n")))));
    }

    @Test
    public void logNoValueParamsUsingRequestLogSpec() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().post("/noValueParam");
        Assert.assertThat(writer.toString(), equalTo(String.format(("Request params:\t<none>%n" + ((("Query params:\tqueryParam%n" + "Form params:\tformParam%n") + "Path params:\t<none>%n") + "Multiparts:\t\t<none>%n")))));
    }

    @Test
    public void logBodyUsingRequestLogSpec() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/greetJSON");
        Assert.assertThat(writer.toString(), equalTo(String.format("Body:\t\t\t<none>%n")));
    }

    @Test
    public void logBodyWithPrettyPrintingUsingRequestLogSpec() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().post("/body");
        Assert.assertThat(writer.toString(), equalTo(String.format(("Body:%n" + (("{\n" + "    \"something\": \"else\"\n") + "}%n")))));
    }

    @Test
    public void logBodyWithPrettyPrintingUsingDslAndRequestLogSpec() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().post("/body");
        Assert.assertThat(writer.toString(), equalTo(String.format(("Body:%n" + (("{\n" + "    \"something\": \"else\"\n") + "}%n")))));
    }

    @Test
    public void logBodyWithPrettyPrintingUsingRequestLogSpecAndObjectMapping() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        final Message message = new Message();
        message.setMessage("My message");
        when().post("/body");
        Assert.assertThat(writer.toString(), equalTo(String.format("Body:%n{\n    \"message\": \"My message\"\n}%n")));
    }

    @Test
    public void logBodyWithPrettyPrintingUsingRequestLogSpecAndObjectMappingWhenXML() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        final Greeting greeting = new Greeting();
        greeting.setFirstName("John");
        greeting.setLastName("Doe");
        when().post("/body");
        Assert.assertThat(writer.toString(), equalTo(String.format(("Body:%n" + ((("<greeting>\n" + "  <firstName>John</firstName>\n") + "  <lastName>Doe</lastName>\n") + "</greeting>%n")))));
    }

    @Test
    public void logCookiesUsingRequestLogSpec() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().post("/reflect");
        Assert.assertThat(writer.toString(), equalTo(String.format(("Cookies:\t\tmyCookie1=myCookieValue1%n" + (("\t\t\t\tmyCookie2=myCookieValue2%n" + "\t\t\t\tmyMultiCookie=myMultiCookieValue1%n") + "\t\t\t\tmyMultiCookie=myMultiCookieValue2%n")))));
    }

    @Test
    public void logHeadersUsingRequestLogSpec() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/multiHeaderReflect");
        Assert.assertThat(writer.toString(), equalTo(String.format(("Headers:\t\tmyHeader1=myHeaderValue1%n" + ((("\t\t\t\tmyHeader2=myHeaderValue2%n" + "\t\t\t\tmyMultiHeader=myMultiHeaderValue1%n") + "\t\t\t\tmyMultiHeader=myMultiHeaderValue2%n") + "\t\t\t\tAccept=*/*%n")))));
    }

    @Test
    public void logBodyPrettyPrintedUsingRequestLogSpecWhenContentTypeDoesntMatchContent() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().post("/reflect");
        Assert.assertThat(writer.toString(), equalTo(String.format(((((((("Request method:\tPOST%n" + ((((((("Request URI:\thttp://localhost:8080/reflect%n" + "Proxy:\t\t\t<none>%n") + "Request params:\t<none>%n") + "Query params:\t<none>%n") + "Form params:\t<none>%n") + "Path params:\t<none>%n") + "Headers:\t\tAccept=*/*%n") + "\t\t\t\tContent-Type=application/json; charset=")) + (RestAssured.config().getEncoderConfig().defaultCharsetForContentType(JSON))) + "%n") + "Cookies:\t\t<none>%n") + "Multiparts:\t\t<none>%n") + "Body:%n") + "This is not JSON%n"))));
    }

    @Test
    public void logAllWhenBasePathIsDefinedUsingRequestLogSpec() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        RestAssured.basePath = "/reflect";
        try {
            when().post("/");
        } finally {
            RestAssured.reset();
        }
        Assert.assertThat(writer.toString(), equalTo(String.format(("Request method:\tPOST%n" + ((((((((((("Request URI:\thttp://localhost:8080/reflect/%n" + "Proxy:\t\t\t<none>%n") + "Request params:\t<none>%n") + "Query params:\t<none>%n") + "Form params:\t<none>%n") + "Path params:\t<none>%n") + "Headers:\t\tAccept=*/*%n") + "\t\t\t\tContent-Type=text/plain; charset=%s%n") + "Cookies:\t\t<none>%n") + "Multiparts:\t\t<none>%n") + "Body:%n") + "hello%n")), RestAssured.config().getEncoderConfig().defaultContentCharset())));
    }

    @Test
    public void logAllWhenBaseURIIsDefinedUsingRequestLogSpec() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        RestAssured.baseURI = "http://localhost:8080/reflect";
        try {
            when().post("/");
        } finally {
            RestAssured.reset();
        }
        Assert.assertThat(writer.toString(), equalTo(String.format(("Request method:\tPOST%n" + ((((((((((("Request URI:\thttp://localhost:8080/reflect/%n" + "Proxy:\t\t\t<none>%n") + "Request params:\t<none>%n") + "Query params:\t<none>%n") + "Form params:\t<none>%n") + "Path params:\t<none>%n") + "Headers:\t\tAccept=*/*%n") + "\t\t\t\tContent-Type=text/plain; charset=%s%n") + "Cookies:\t\t<none>%n") + "Multiparts:\t\t<none>%n") + "Body:%n") + "hello%n")), RestAssured.config().getEncoderConfig().defaultContentCharset())));
    }

    @Test
    public void logAllWhenBasePathAndBasePortAndBaseURIIsDefinedUsingRequestLogSpec() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/reflect";
        try {
            when().post("/");
        } finally {
            RestAssured.reset();
        }
        Assert.assertThat(writer.toString(), equalTo(String.format(("Request method:\tPOST%n" + ((((((((((("Request URI:\thttp://localhost:8080/reflect/%n" + "Proxy:\t\t\t<none>%n") + "Request params:\t<none>%n") + "Query params:\t<none>%n") + "Form params:\t<none>%n") + "Path params:\t<none>%n") + "Headers:\t\tAccept=*/*%n") + "\t\t\t\tContent-Type=text/plain; charset=%s%n") + "Cookies:\t\t<none>%n") + "Multiparts:\t\t<none>%n") + "Body:%n") + "hello%n")), RestAssured.config().getEncoderConfig().defaultContentCharset())));
    }

    @Test
    public void logsFullyQualifiedUrlsAreLoggedCorrectly() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        config(config().logConfig(new LogConfig(captor, true))).log().all().filter(new Filter() {
            public io.restassured.response.Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec, io.restassured.specification.FilterableResponseSpecification responseSpec, io.restassured.filter.FilterContext ctx) {
                return new io.restassured.builder.ResponseBuilder().setStatusCode(200).setBody("changed").build();
            }
        }).get("http://www.beijingchina.net.cn/transportation/train/train-to-shanghai.html");
        Assert.assertThat(writer.toString(), startsWith(String.format(("Request method:\tGET%n" + "Request URI:\thttp://www.beijingchina.net.cn/transportation/train/train-to-shanghai.html"))));
    }

    @Test
    public void logsXmlNamespacesCorrectly() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/namespace-example");
        Assert.assertThat(writer.toString(), containsString(String.format(("foo xmlns:ns=\"http://localhost/\">\n" + (("      <bar>sudo </bar>\n" + "      <ns:bar>make me a sandwich!</ns:bar>\n") + "    </foo>")))));
    }

    @Test
    public void logsMultiPartParamsOnLogAll() throws Exception {
        // Given
        final byte[] bytes = IOUtils.toByteArray(getClass().getResourceAsStream("/car-records.xsd"));
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        InputStream powermock = getClass().getResourceAsStream("/powermock-easymock-junit-1.4.12.zip");
        // When
        Greeting greeting = new Greeting();
        greeting.setFirstName("John");
        greeting.setLastName("Doe");
        when().post("/multipart/file").then().statusCode(200).body(is(new String(bytes)));
        Assert.assertThat(writer.toString(), allOf(startsWith(String.format(("HTTP/1.1 200 OK%n" + ("Content-Type: text/plain;charset=utf-8%n" + "Content-Length: ")))), endsWith(String.format(("Server: Jetty(9.3.2.v20150730)%n" + ((((((((((((((((((((((((((((((((((((((((("%n" + "<!--%n") + "  ~ Copyright 2013 the original author or authors.%n") + "  ~%n") + "  ~ Licensed under the Apache License, Version 2.0 (the \"License\");%n") + "  ~ you may not use this file except in compliance with the License.%n") + "  ~ You may obtain a copy of the License at%n") + "  ~%n") + "  ~        http://www.apache.org/licenses/LICENSE-2.0%n") + "  ~%n") + "  ~ Unless required by applicable law or agreed to in writing, software%n") + "  ~ distributed under the License is distributed on an \"AS IS\" BASIS,%n") + "  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.%n") + "  ~ See the License for the specific language governing permissions and%n") + "  ~ limitations under the License.%n") + "  -->%n") + "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\">%n") + "  <xs:element name=\"records\">%n") + "    <xs:complexType>%n") + "      <xs:sequence>%n") + "        <xs:element maxOccurs=\"unbounded\" ref=\"car\"/>%n") + "      </xs:sequence>%n") + "    </xs:complexType>%n") + "  </xs:element>%n") + "  <xs:element name=\"car\">%n") + "    <xs:complexType>%n") + "      <xs:sequence>%n") + "        <xs:element ref=\"country\"/>%n") + "        <xs:element ref=\"record\"/>%n") + "      </xs:sequence>%n") + "      <xs:attribute name=\"make\" use=\"required\" type=\"xs:NCName\"/>%n") + "      <xs:attribute name=\"name\" use=\"required\"/>%n") + "      <xs:attribute name=\"year\" use=\"required\" type=\"xs:integer\"/>%n") + "    </xs:complexType>%n") + "  </xs:element>%n") + "  <xs:element name=\"country\" type=\"xs:string\"/>%n") + "  <xs:element name=\"record\">%n") + "    <xs:complexType mixed=\"true\">%n") + "      <xs:attribute name=\"type\" use=\"required\" type=\"xs:NCName\"/>%n") + "    </xs:complexType>%n") + "  </xs:element>%n") + "</xs:schema>%n"))))));
    }

    @Test
    public void doesnt_include_default_charset_in_request_log_when_it_is_configured_not_to_be_added() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().post("/contentTypeAsBody").then().statusCode(200);
        Assert.assertThat(writer.toString(), equalTo(String.format(("Request method:\tPOST%n" + (((((((((("Request URI:\thttp://localhost:8080/contentTypeAsBody%n" + "Proxy:\t\t\t<none>%n") + "Request params:\tfoo=bar%n") + "Query params:\t<none>%n") + "Form params:\t<none>%n") + "Path params:\t<none>%n") + "Headers:\t\tAccept=*/*%n") + "\t\t\t\tContent-Type=application/xml%n") + "Cookies:\t\t<none>%n") + "Multiparts:\t\t<none>%n") + "Body:\t\t\t<none>%n")))));
    }

    @Test
    public void includes_default_charset_in_request_log_by_default() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().post("/contentTypeAsBody").then().statusCode(200);
        Assert.assertThat(writer.toString(), equalTo(String.format(("Request method:\tPOST%n" + (((((((((("Request URI:\thttp://localhost:8080/contentTypeAsBody%n" + "Proxy:\t\t\t<none>%n") + "Request params:\tfoo=bar%n") + "Query params:\t<none>%n") + "Form params:\t<none>%n") + "Path params:\t<none>%n") + "Headers:\t\tAccept=*/*%n") + "\t\t\t\tContent-Type=application/xml; charset=%s%n") + "Cookies:\t\t<none>%n") + "Multiparts:\t\t<none>%n") + "Body:\t\t\t<none>%n")), RestAssured.config().getEncoderConfig().defaultContentCharset())));
    }

    @Test
    public void form_param_are_logged_as_query_params_for_get_requests() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/greet").then().body("greeting", equalTo("Greetings John Doe"));
        Assert.assertThat(writer.toString(), equalTo(String.format(("Request method:\tGET%n" + ((((((((((("Request URI:\thttp://localhost:8080/greet?firstName=John&lastName=Doe%n" + "Proxy:\t\t\t<none>%n") + "Request params:\t<none>%n") + "Query params:\t<none>%n") + "Form params:\tfirstName=John%n") + "\t\t\t\tlastName=Doe%n") + "Path params:\t<none>%n") + "Headers:\t\tAccept=*/*%n") + "\t\t\t\tContent-Type=application/x-www-form-urlencoded; charset=%s%n") + "Cookies:\t\t<none>%n") + "Multiparts:\t\t<none>%n") + "Body:\t\t\t<none>%n")), RestAssured.config().getEncoderConfig().defaultContentCharset())));
    }

    @Test
    public void using_log_detail_method_only_logs_the_request_method() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/greet").then().statusCode(200);
        Assert.assertThat(writer.toString(), equalTo(String.format("Request method:\tGET%n")));
    }

    @Test
    public void using_log_detail_uri_only_logs_the_request_uri() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/greet").then().statusCode(200);
        Assert.assertThat(writer.toString(), equalTo(String.format("Request URI:\thttp://localhost:8080/greet?firstName=John&lastName=Doe%n")));
    }

    @Test
    public void shows_request_log_as_url_encoded_when_explicitly_instructing_request_logging_filter_to_do_so() throws UnsupportedEncodingException {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/greet").then().statusCode(200);
        Assert.assertThat(writer.toString(), equalTo(((("Request URI:\thttp://localhost:8080/greet?firstName=John" + (URLEncoder.encode("#?", "UTF-8"))) + "&lastName=Doe") + (SystemUtils.LINE_SEPARATOR))));
    }

    @Test
    public void shows_request_log_as_without_url_encoding_when_explicitly_instructing_request_logging_filter_to_do_so() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/greet").then().statusCode(200);
        Assert.assertThat(writer.toString(), equalTo(String.format("Request URI:\thttp://localhost:8080/greet?firstName=John#\u20ac&lastName=Doe%n")));
    }

    // This was previously a bug (https://github.com/rest-assured/rest-assured/issues/684)
    @Test
    public void assert_that_register_text_json_content_type_can_be_used_in_conjunction_with_enable_logging_of_request_and_response_if_validation_fails() {
        RestAssured.baseURI = "http://127.0.0.1";
        RestAssured.port = 8080;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.registerParser("text/json", Parser.JSON);
        when().get("/text-json").then().body("test", is(true));
    }

    // This was previously a bug (https://github.com/rest-assured/rest-assured/issues/960)
    @Test
    public void prettifying_empty_xml_body_doesnt_log_premature_end_of_file() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
        when().get("/xml-empty-body").then().statusCode(200);
        Assert.assertThat(writer.toString(), equalTo(String.format("200%nContent-Type: application/xml%n")));
    }
}

