package org.mockserver.mappers;


import HttpStatusCode.OK_200;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockserver.model.Cookie;
import org.mockserver.model.Header;
import org.mockserver.model.HttpResponse;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 *
 *
 * @author jamesdbloom
 */
public class MockServerResponseToHttpServletResponseEncoderBasicMappingTest {
    @Test
    public void shouldMapHttpResponseToHttpServletResponse() throws UnsupportedEncodingException {
        // given
        // - an HttpResponse
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.withStatusCode(OK_200.code());
        httpResponse.withReasonPhrase("randomReason");
        httpResponse.withBody("somebody");
        httpResponse.withHeaders(new Header("headerName1", "headerValue1"), new Header("headerName2", "headerValue2"));
        httpResponse.withCookies(new Cookie("cookieName1", "cookieValue1"), new Cookie("cookieName2", "cookieValue2"));
        // - an HttpServletResponse
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
        // when
        new MockServerResponseToHttpServletResponseEncoder().mapMockServerResponseToHttpServletResponse(httpResponse, httpServletResponse);
        // then
        Assert.assertEquals(OK_200.code(), httpServletResponse.getStatus());
        Assert.assertEquals("somebody", httpServletResponse.getContentAsString());
        Assert.assertEquals("headerValue1", httpServletResponse.getHeader("headerName1"));
        Assert.assertEquals("headerValue2", httpServletResponse.getHeader("headerName2"));
        Assert.assertEquals(Arrays.asList("cookieName1=cookieValue1", "cookieName2=cookieValue2"), httpServletResponse.getHeaders("Set-Cookie"));
    }

    @Test(expected = RuntimeException.class)
    public void shouldHandleExceptionWhenReadingBody() throws IOException {
        // given
        // - an HttpResponse
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.withStatusCode(OK_200.code());
        httpResponse.withBody("somebody");
        httpResponse.withHeaders(new Header("headerName1", "headerValue1"), new Header("headerName2", "headerValue2"));
        httpResponse.withCookies(new Cookie("cookieName1", "cookieValue1"), new Cookie("cookieName2", "cookieValue2"));
        // - an HttpServletResponse
        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);
        Mockito.when(httpServletResponse.getOutputStream()).thenThrow(new IOException("TEST EXCEPTION"));
        // when
        new MockServerResponseToHttpServletResponseEncoder().mapMockServerResponseToHttpServletResponse(httpResponse, httpServletResponse);
    }
}

