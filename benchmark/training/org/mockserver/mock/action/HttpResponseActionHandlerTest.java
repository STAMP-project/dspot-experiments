package org.mockserver.mock.action;


import org.junit.Test;
import org.mockito.Mockito;
import org.mockserver.model.HttpResponse;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpResponseActionHandlerTest {
    @Test
    public void shouldHandleHttpRequests() {
        // given
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        HttpResponseActionHandler httpResponseActionHandler = new HttpResponseActionHandler();
        // when
        httpResponseActionHandler.handle(httpResponse);
        // then
        Mockito.verify(httpResponse).clone();
    }
}

