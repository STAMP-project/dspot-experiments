package org.mockserver.client;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockserver.mock.Expectation;
import org.mockserver.websocket.WebSocketClient;


public class ForwardChainExpectationTest {
    private MockServerClient mockAbstractClient;

    private Expectation mockExpectation;

    @Mock
    private WebSocketClient webSocketClient;

    @InjectMocks
    private ForwardChainExpectation forwardChainExpectation;

    @Test
    public void shouldSetResponse() {
        // given
        HttpResponse response = response();
        // when
        forwardChainExpectation.respond(response);
        // then
        Mockito.verify(mockExpectation).thenRespond(ArgumentMatchers.same(response));
        Mockito.verify(mockAbstractClient).sendExpectation(mockExpectation);
    }

    @Test
    public void shouldSetResponseTemplate() {
        // given
        HttpTemplate template = template(HttpTemplate.TemplateType.VELOCITY, "some_template");
        // when
        forwardChainExpectation.respond(template);
        // then
        Mockito.verify(mockExpectation).thenRespond(ArgumentMatchers.same(template));
        Mockito.verify(mockAbstractClient).sendExpectation(mockExpectation);
    }

    @Test
    public void shouldSetResponseClassCallback() {
        // given
        HttpClassCallback callback = callback();
        // when
        forwardChainExpectation.respond(callback);
        // then
        Mockito.verify(mockExpectation).thenRespond(ArgumentMatchers.same(callback));
        Mockito.verify(mockAbstractClient).sendExpectation(mockExpectation);
    }

    @Test
    public void shouldSetForward() {
        // given
        HttpForward forward = forward();
        // when
        forwardChainExpectation.forward(forward);
        // then
        Mockito.verify(mockExpectation).thenForward(ArgumentMatchers.same(forward));
        Mockito.verify(mockAbstractClient).sendExpectation(mockExpectation);
    }

    @Test
    public void shouldSetForwardTemplate() {
        // given
        HttpTemplate template = template(HttpTemplate.TemplateType.VELOCITY, "some_template");
        // when
        forwardChainExpectation.forward(template);
        // then
        Mockito.verify(mockExpectation).thenForward(ArgumentMatchers.same(template));
        Mockito.verify(mockAbstractClient).sendExpectation(mockExpectation);
    }

    @Test
    public void shouldSetForwardClassCallback() {
        // given
        HttpClassCallback callback = callback();
        // when
        forwardChainExpectation.forward(callback);
        // then
        Mockito.verify(mockExpectation).thenForward(ArgumentMatchers.same(callback));
        Mockito.verify(mockAbstractClient).sendExpectation(mockExpectation);
    }

    @Test
    public void shouldSetOverrideForwardedRequest() {
        // when
        forwardChainExpectation.forward(forwardOverriddenRequest(request().withBody("some_replaced_body")));
        // then
        Mockito.verify(mockExpectation).thenForward(forwardOverriddenRequest(request().withBody("some_replaced_body")));
        Mockito.verify(mockAbstractClient).sendExpectation(mockExpectation);
    }

    @Test
    public void shouldSetError() {
        // given
        HttpError error = error();
        // when
        forwardChainExpectation.error(error);
        // then
        Mockito.verify(mockExpectation).thenError(ArgumentMatchers.same(error));
        Mockito.verify(mockAbstractClient).sendExpectation(mockExpectation);
    }
}

