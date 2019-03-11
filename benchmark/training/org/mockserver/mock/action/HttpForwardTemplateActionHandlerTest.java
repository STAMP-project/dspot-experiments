package org.mockserver.mock.action;


import HttpTemplate.TemplateType.JAVASCRIPT;
import HttpTemplate.TemplateType.VELOCITY;
import com.google.common.util.concurrent.SettableFuture;
import javax.script.ScriptEngineManager;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsSame;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockserver.client.NettyHttpClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.HttpTemplate;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpForwardTemplateActionHandlerTest {
    private NettyHttpClient mockHttpClient;

    private HttpForwardTemplateActionHandler httpForwardTemplateActionHandler;

    @Test
    public void shouldHandleHttpRequestsWithJavaScriptTemplateFirstExample() throws Exception {
        // given
        HttpTemplate template = HttpTemplate.template(JAVASCRIPT, "return { \'path\': \"somePath\", \'body\': JSON.stringify({name: \'value\'}) };");
        HttpRequest httpRequest = HttpRequest.request("somePath").withBody("{\"name\":\"value\"}");
        SettableFuture<HttpResponse> httpResponse = SettableFuture.create();
        Mockito.when(mockHttpClient.sendRequest(httpRequest, null)).thenReturn(httpResponse);
        // when
        SettableFuture<HttpResponse> actualHttpResponse = httpForwardTemplateActionHandler.handle(template, HttpRequest.request().withPath("/somePath").withMethod("POST").withBody("some_body")).getHttpResponse();
        // then
        if ((new ScriptEngineManager().getEngineByName("nashorn")) != null) {
            Mockito.verify(mockHttpClient).sendRequest(httpRequest, null);
            MatcherAssert.assertThat(actualHttpResponse, Is.is(IsSame.sameInstance(httpResponse)));
        } else {
            MatcherAssert.assertThat(actualHttpResponse.get(), Is.is(HttpResponse.notFoundResponse()));
        }
    }

    @Test
    public void shouldHandleHttpRequestsWithVelocityTemplateFirstExample() {
        // given
        HttpTemplate template = HttpTemplate.template(VELOCITY, (((((((("{" + (NEW_LINE)) + "    \'path\': \"$!request.path\",") + (NEW_LINE)) + "    \'method\': \"$!request.method\",") + (NEW_LINE)) + "    \'body\': \"$!request.body\"") + (NEW_LINE)) + "}"));
        HttpRequest httpRequest = HttpRequest.request("/somePath").withMethod("POST").withBody("some_body");
        SettableFuture<HttpResponse> httpResponse = SettableFuture.create();
        Mockito.when(mockHttpClient.sendRequest(httpRequest, null)).thenReturn(httpResponse);
        // when
        SettableFuture<HttpResponse> actualHttpResponse = httpForwardTemplateActionHandler.handle(template, httpRequest).getHttpResponse();
        // then
        Mockito.verify(mockHttpClient).sendRequest(httpRequest, null);
        MatcherAssert.assertThat(actualHttpResponse, Is.is(IsSame.sameInstance(httpResponse)));
    }
}

