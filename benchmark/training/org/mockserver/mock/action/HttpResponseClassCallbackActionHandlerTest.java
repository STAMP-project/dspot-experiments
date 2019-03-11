package org.mockserver.mock.action;


import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.model.HttpClassCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpResponseClassCallbackActionHandlerTest {
    @Test
    public void shouldHandleInvalidClass() {
        // given
        HttpClassCallback httpClassCallback = HttpClassCallback.callback("org.mockserver.mock.action.FooBar");
        // when
        HttpResponse actualHttpResponse = new HttpResponseClassCallbackActionHandler(new MockServerLogger()).handle(httpClassCallback, HttpRequest.request().withBody("some_body"));
        // then
        MatcherAssert.assertThat(actualHttpResponse, Is.is(HttpResponse.notFoundResponse()));
    }

    @Test
    public void shouldHandleValidLocalClass() {
        // given
        HttpClassCallback httpClassCallback = HttpClassCallback.callback("org.mockserver.mock.action.HttpResponseClassCallbackActionHandlerTest$TestCallback");
        // when
        HttpResponse actualHttpResponse = new HttpResponseClassCallbackActionHandler(new MockServerLogger()).handle(httpClassCallback, HttpRequest.request().withBody("some_body"));
        // then
        MatcherAssert.assertThat(actualHttpResponse, Is.is(HttpResponse.response("some_body")));
    }

    public static class TestCallback implements ExpectationResponseCallback {
        @Override
        public HttpResponse handle(HttpRequest httpRequest) {
            return HttpResponse.response(httpRequest.getBodyAsString());
        }
    }
}

