package org.mockserver.templates.engine.javascript;


import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.serialization.model.HttpRequestDTO;
import org.mockserver.serialization.model.HttpResponseDTO;


/**
 *
 *
 * @author jamesdbloom
 */
public class JavaScriptTemplateEngineTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private MockServerLogger logFormatter;

    @Test
    public void shouldHandleHttpRequestsWithJavaScriptTemplateFirstExample() {
        // given
        String template = ((((((((((((((((((("if (request.method === 'POST' && request.path === '/somePath') {" + (NEW_LINE)) + "    return {") + (NEW_LINE)) + "        'statusCode': 200,") + (NEW_LINE)) + "        'body': JSON.stringify({name: 'value'})") + (NEW_LINE)) + "    };") + (NEW_LINE)) + "} else {") + (NEW_LINE)) + "    return {") + (NEW_LINE)) + "        'statusCode': 406,") + (NEW_LINE)) + "        'body': request.body") + (NEW_LINE)) + "    };") + (NEW_LINE)) + "}";
        // when
        HttpResponse actualHttpResponse = new JavaScriptTemplateEngine(logFormatter).executeTemplate(template, HttpRequest.request().withPath("/somePath").withMethod("POST").withBody("some_body"), HttpResponseDTO.class);
        // then
        if ((new ScriptEngineManager().getEngineByName("nashorn")) != null) {
            MatcherAssert.assertThat(actualHttpResponse, Is.is(HttpResponse.response().withStatusCode(200).withBody("{\"name\":\"value\"}")));
        } else {
            MatcherAssert.assertThat(actualHttpResponse, CoreMatchers.nullValue());
        }
    }

    @Test
    public void shouldHandleHttpRequestsWithJavaScriptTemplateSecondExample() {
        // given
        String template = ((((((((((((((((((("if (request.method === 'POST' && request.path === '/somePath') {" + (NEW_LINE)) + "    return {") + (NEW_LINE)) + "        'statusCode': 200,") + (NEW_LINE)) + "        'body': JSON.stringify({name: 'value'})") + (NEW_LINE)) + "    };") + (NEW_LINE)) + "} else {") + (NEW_LINE)) + "    return {") + (NEW_LINE)) + "        'statusCode': 406,") + (NEW_LINE)) + "        'body': request.body") + (NEW_LINE)) + "    };") + (NEW_LINE)) + "}";
        // when
        HttpResponse actualHttpResponse = new JavaScriptTemplateEngine(logFormatter).executeTemplate(template, HttpRequest.request().withPath("/someOtherPath").withBody("some_body"), HttpResponseDTO.class);
        // then
        if ((new ScriptEngineManager().getEngineByName("nashorn")) != null) {
            MatcherAssert.assertThat(actualHttpResponse, Is.is(HttpResponse.response().withStatusCode(406).withBody("some_body")));
        } else {
            MatcherAssert.assertThat(actualHttpResponse, CoreMatchers.nullValue());
        }
    }

    @Test
    public void shouldHandleHttpRequestsWithJavaScriptForwardTemplateFirstExample() {
        // given
        String template = ((((((((((((((((((((((("return {" + (NEW_LINE)) + "    \'path\' : \"/somePath\",") + (NEW_LINE)) + "    'cookies' : [ {") + (NEW_LINE)) + "        'name' : request.cookies['someCookie'],") + (NEW_LINE)) + "        \'value\' : \"someCookie\"") + (NEW_LINE)) + "    }, {") + (NEW_LINE)) + "        \'name\' : \"someCookie\",") + (NEW_LINE)) + "        'value' : request.cookies['someCookie']") + (NEW_LINE)) + "    } ],") + (NEW_LINE)) + "    'keepAlive' : true,") + (NEW_LINE)) + "    'secure' : true,") + (NEW_LINE)) + "    \'body\' : \"some_body\"") + (NEW_LINE)) + "};";
        // when
        HttpRequest actualHttpRequest = new JavaScriptTemplateEngine(logFormatter).executeTemplate(template, HttpRequest.request().withPath("/somePath").withCookie("someCookie", "someValue").withMethod("POST").withBody("some_body"), HttpRequestDTO.class);
        // then
        if ((new ScriptEngineManager().getEngineByName("nashorn")) != null) {
            MatcherAssert.assertThat(actualHttpRequest, Is.is(HttpRequest.request().withPath("/somePath").withCookie("someCookie", "someValue").withCookie("someValue", "someCookie").withKeepAlive(true).withSecure(true).withBody("some_body")));
        } else {
            MatcherAssert.assertThat(actualHttpRequest, CoreMatchers.nullValue());
        }
    }

    @Test
    public void shouldHandleHttpRequestsWithJavaScriptForwardTemplateSecondExample() {
        // given
        String template = ((((((((((((((((((((("return {" + (NEW_LINE)) + "    \'path\' : \"/somePath\",") + (NEW_LINE)) + "    'queryStringParameters' : [ {") + (NEW_LINE)) + "        \'name\' : \"queryParameter\",") + (NEW_LINE)) + "        'values' : request.queryStringParameters['queryParameter']") + (NEW_LINE)) + "    } ],") + (NEW_LINE)) + "    'headers' : [ {") + (NEW_LINE)) + "        \'name\' : \"Host\",") + (NEW_LINE)) + "        \'values\' : [ \"localhost:1080\" ]") + (NEW_LINE)) + "    } ],") + (NEW_LINE)) + "    \'body\': \"{\'name\': \'value\'}\"") + (NEW_LINE)) + "};";
        // when
        HttpRequest actualHttpRequest = new JavaScriptTemplateEngine(logFormatter).executeTemplate(template, HttpRequest.request().withPath("/someOtherPath").withQueryStringParameter("queryParameter", "someValue").withBody("some_body"), HttpRequestDTO.class);
        // then
        if ((new ScriptEngineManager().getEngineByName("nashorn")) != null) {
            MatcherAssert.assertThat(actualHttpRequest, Is.is(HttpRequest.request().withHeader("Host", "localhost:1080").withPath("/somePath").withQueryStringParameter("queryParameter", "someValue").withBody("{'name': 'value'}")));
        } else {
            MatcherAssert.assertThat(actualHttpRequest, CoreMatchers.nullValue());
        }
    }

    @Test
    public void shouldHandleInvalidJavaScript() {
        // given
        String template = ((((((((((((((((((((("{" + (NEW_LINE)) + "    \'path\' : \"/somePath\",") + (NEW_LINE)) + "    'queryStringParameters' : [ {") + (NEW_LINE)) + "        \'name\' : \"queryParameter\",") + (NEW_LINE)) + "        'values' : request.queryStringParameters['queryParameter']") + (NEW_LINE)) + "    } ],") + (NEW_LINE)) + "    'headers' : [ {") + (NEW_LINE)) + "        \'name\' : \"Host\",") + (NEW_LINE)) + "        \'values\' : [ \"localhost:1080\" ]") + (NEW_LINE)) + "    } ],") + (NEW_LINE)) + "    \'body\': \"{\'name\': \'value\'}\"") + (NEW_LINE)) + "};";
        if ((new ScriptEngineManager().getEngineByName("nashorn")) != null) {
            exception.expect(RuntimeException.class);
            exception.expectCause(Is.isA(ScriptException.class));
            exception.expectMessage(Matchers.containsString(((((((((((((((((((((((((((((((((((((((((((((((((("Exception transforming template:" + (NEW_LINE)) + (NEW_LINE)) + "\tfunction handle(request) {") + (NEW_LINE)) + "\t") + (NEW_LINE)) + "\t\t{") + (NEW_LINE)) + "\t\t    \'path\' : \"/somePath\",") + (NEW_LINE)) + "\t\t    \'queryStringParameters\' : [ {") + (NEW_LINE)) + "\t\t        \'name\' : \"queryParameter\",") + (NEW_LINE)) + "\t\t        \'values\' : request.queryStringParameters[\'queryParameter\']") + (NEW_LINE)) + "\t\t    } ],") + (NEW_LINE)) + "\t\t    \'headers\' : [ {") + (NEW_LINE)) + "\t\t        \'name\' : \"Host\",") + (NEW_LINE)) + "\t\t        \'values\' : [ \"localhost:1080\" ]") + (NEW_LINE)) + "\t\t    } ],") + (NEW_LINE)) + "\t\t    \'body\': \"{\'name\': \'value\'}\"") + (NEW_LINE)) + "\t\t};") + (NEW_LINE)) + "\t}") + (NEW_LINE)) + (NEW_LINE)) + " for request:") + (NEW_LINE)) + (NEW_LINE)) + "\t{") + (NEW_LINE)) + "\t  \"path\" : \"/someOtherPath\",") + (NEW_LINE)) + "\t  \"queryStringParameters\" : {") + (NEW_LINE)) + "\t    \"queryParameter\" : [ \"someValue\" ]") + (NEW_LINE)) + "\t  },") + (NEW_LINE)) + "\t  \"body\" : \"some_body\"") + (NEW_LINE)) + "\t}")));
        }
        // when
        new JavaScriptTemplateEngine(logFormatter).executeTemplate(template, HttpRequest.request().withPath("/someOtherPath").withQueryStringParameter("queryParameter", "someValue").withBody("some_body"), HttpRequestDTO.class);
    }
}

