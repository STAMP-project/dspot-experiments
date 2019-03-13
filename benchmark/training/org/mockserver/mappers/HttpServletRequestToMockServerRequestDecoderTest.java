package org.mockserver.mappers;


import com.google.common.collect.Lists;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockserver.model.NottableString;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 *
 *
 * @author jamesdbloom
 */
@SuppressWarnings("unchecked")
public class HttpServletRequestToMockServerRequestDecoderTest {
    @Test
    public void shouldMapHttpServletRequestToHttpRequest() {
        // given
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("GET", "/requestURI");
        httpServletRequest.setContextPath(null);
        httpServletRequest.setQueryString("queryStringParameterNameOne=queryStringParameterValueOne_One&queryStringParameterNameOne=queryStringParameterValueOne_Two&queryStringParameterNameTwo=queryStringParameterValueTwo_One");
        httpServletRequest.addHeader("headerName1", "headerValue1_1");
        httpServletRequest.addHeader("headerName1", "headerValue1_2");
        httpServletRequest.addHeader("headerName2", "headerValue2");
        httpServletRequest.setCookies(new Cookie("cookieName1", "cookieValue1"), new Cookie("cookieName2", "cookieValue2"));
        httpServletRequest.setContent("bodyParameterNameOne=bodyParameterValueOne_One&bodyParameterNameOne=bodyParameterValueOne_Two&bodyParameterNameTwo=bodyParameterValueTwo_One".getBytes(StandardCharsets.UTF_8));
        // when
        HttpRequest httpRequest = new HttpServletRequestToMockServerRequestDecoder().mapHttpServletRequestToMockServerRequest(httpServletRequest);
        // then
        Assert.assertEquals(NottableString.string("/requestURI"), httpRequest.getPath());
        Assert.assertEquals(new ParameterBody(new Parameter("bodyParameterNameOne", "bodyParameterValueOne_One"), new Parameter("bodyParameterNameOne", "bodyParameterValueOne_Two"), new Parameter("bodyParameterNameTwo", "bodyParameterValueTwo_One")).toString(), httpRequest.getBody().toString());
        Assert.assertEquals(Arrays.asList(new Parameter("queryStringParameterNameOne", "queryStringParameterValueOne_One", "queryStringParameterValueOne_Two"), new Parameter("queryStringParameterNameTwo", "queryStringParameterValueTwo_One")), httpRequest.getQueryStringParameterList());
        Assert.assertEquals(Lists.newArrayList(new Header("headerName1", "headerValue1_1", "headerValue1_2"), new Header("headerName2", "headerValue2")), httpRequest.getHeaderList());
        Assert.assertEquals(Lists.newArrayList(new Cookie("cookieName1", "cookieValue1"), new Cookie("cookieName2", "cookieValue2")), httpRequest.getCookieList());
    }

    @Test
    public void shouldMapPathForRequestsWithAContextPath() {
        // given
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("GET", "/requestURI");
        httpServletRequest.setContextPath("contextPath");
        httpServletRequest.setPathInfo("/pathInfo");
        httpServletRequest.setContent("".getBytes(StandardCharsets.UTF_8));
        // when
        HttpRequest httpRequest = new HttpServletRequestToMockServerRequestDecoder().mapHttpServletRequestToMockServerRequest(httpServletRequest);
        // then
        Assert.assertEquals(NottableString.string("/pathInfo"), httpRequest.getPath());
    }

    @Test(expected = RuntimeException.class)
    public void shouldHandleExceptionWhenReadingBody() throws IOException {
        // given
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(httpServletRequest.getMethod()).thenReturn("GET");
        Mockito.when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("requestURI"));
        Mockito.when(httpServletRequest.getQueryString()).thenReturn("parameterName=parameterValue");
        Enumeration<String> enumeration = Mockito.mock(Enumeration.class);
        Mockito.when(enumeration.hasMoreElements()).thenReturn(false);
        Mockito.when(httpServletRequest.getHeaderNames()).thenReturn(enumeration);
        Mockito.when(httpServletRequest.getInputStream()).thenThrow(new IOException("TEST EXCEPTION"));
        // when
        new HttpServletRequestToMockServerRequestDecoder().mapHttpServletRequestToMockServerRequest(httpServletRequest);
    }
}

