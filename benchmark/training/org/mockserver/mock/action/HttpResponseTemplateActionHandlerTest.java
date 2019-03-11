package org.mockserver.mock.action;


import HttpTemplate.TemplateType.JAVASCRIPT;
import HttpTemplate.TemplateType.VELOCITY;
import javax.script.ScriptEngineManager;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.HttpTemplate;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpResponseTemplateActionHandlerTest {
    private HttpResponseTemplateActionHandler httpResponseTemplateActionHandler;

    private MockServerLogger mockLogFormatter;

    @Test
    public void shouldHandleHttpRequestsWithJavaScriptTemplateFirstExample() {
        // given
        HttpTemplate template = HttpTemplate.template(JAVASCRIPT, (((((((((((((((((((("if (request.method === 'POST' && request.path === '/somePath') {" + (NEW_LINE)) + "    return {") + (NEW_LINE)) + "        'statusCode': 200,") + (NEW_LINE)) + "        'body': JSON.stringify({name: 'value'})") + (NEW_LINE)) + "    };") + (NEW_LINE)) + "} else {") + (NEW_LINE)) + "    return {") + (NEW_LINE)) + "        'statusCode': 406,") + (NEW_LINE)) + "        'body': request.body") + (NEW_LINE)) + "    };") + (NEW_LINE)) + "}"));
        // when
        HttpResponse actualHttpResponse = httpResponseTemplateActionHandler.handle(template, HttpRequest.request().withPath("/somePath").withMethod("POST").withBody("some_body"));
        // then
        if ((new ScriptEngineManager().getEngineByName("nashorn")) != null) {
            MatcherAssert.assertThat(actualHttpResponse, Is.is(HttpResponse.response().withStatusCode(200).withBody("{\"name\":\"value\"}")));
        } else {
            MatcherAssert.assertThat(actualHttpResponse, Is.is(HttpResponse.notFoundResponse()));
        }
    }

    @Test
    public void shouldHandleHttpRequestsWithJavaScriptTemplateSecondExample() {
        // given
        HttpTemplate template = HttpTemplate.template(JAVASCRIPT, (((((((((((((((((((("if (request.method === 'POST' && request.path === '/somePath') {" + (NEW_LINE)) + "    return {") + (NEW_LINE)) + "        'statusCode': 200,") + (NEW_LINE)) + "        'body': JSON.stringify({name: 'value'})") + (NEW_LINE)) + "    };") + (NEW_LINE)) + "} else {") + (NEW_LINE)) + "    return {") + (NEW_LINE)) + "        'statusCode': 406,") + (NEW_LINE)) + "        'body': request.body") + (NEW_LINE)) + "    };") + (NEW_LINE)) + "}"));
        // when
        HttpResponse actualHttpResponse = httpResponseTemplateActionHandler.handle(template, HttpRequest.request().withPath("/someOtherPath").withBody("some_body"));
        // then
        if ((new ScriptEngineManager().getEngineByName("nashorn")) != null) {
            MatcherAssert.assertThat(actualHttpResponse, Is.is(HttpResponse.response().withStatusCode(406).withBody("some_body")));
        } else {
            MatcherAssert.assertThat(actualHttpResponse, Is.is(HttpResponse.notFoundResponse()));
        }
    }

    @Test
    public void shouldHandleHttpRequestsWithVelocityTemplateFirstExample() {
        // given
        HttpTemplate template = HttpTemplate.template(VELOCITY, (((((((((((((((((((("#if ( $request.method == 'POST' && $request.path == '/somePath' )" + (NEW_LINE)) + "    {") + (NEW_LINE)) + "        'statusCode': 200,") + (NEW_LINE)) + "        \'body\': \"{\'name\': \'value\'}\"") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "#else") + (NEW_LINE)) + "    {") + (NEW_LINE)) + "        'statusCode': 406,") + (NEW_LINE)) + "        \'body\': \"$!request.body\"") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "#end"));
        // when
        HttpResponse actualHttpResponse = httpResponseTemplateActionHandler.handle(template, HttpRequest.request().withPath("/somePath").withMethod("POST").withBody("some_body"));
        // then
        MatcherAssert.assertThat(actualHttpResponse, Is.is(HttpResponse.response().withStatusCode(200).withBody("{'name': 'value'}")));
    }

    @Test
    public void shouldHandleHttpRequestsWithVelocityTemplateSecondExample() {
        // given
        HttpTemplate template = HttpTemplate.template(VELOCITY, (((((((((((((((((((("#if ( $request.method == 'POST' && $request.path == '/somePath' )" + (NEW_LINE)) + "    {") + (NEW_LINE)) + "        'statusCode': 200,") + (NEW_LINE)) + "        \'body\': \"{\'name\': \'value\'}\"") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "#else") + (NEW_LINE)) + "    {") + (NEW_LINE)) + "        'statusCode': 406,") + (NEW_LINE)) + "        \'body\': \"$!request.body\"") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "#end"));
        // when
        HttpResponse actualHttpResponse = httpResponseTemplateActionHandler.handle(template, HttpRequest.request().withPath("/someOtherPath").withBody("some_body"));
        // then
        MatcherAssert.assertThat(actualHttpResponse, Is.is(HttpResponse.response().withStatusCode(406).withBody("some_body")));
    }
}

