package org.mockserver.serialization.model;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.Cookie;
import org.mockserver.model.Header;
import org.mockserver.model.NottableString;
import org.mockserver.model.Parameter;
import org.mockserver.model.StringBody;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpRequestDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // given
        BodyDTO body = BodyDTO.createDTO(StringBody.exact("body"));
        Cookies cookies = new Cookies().withEntries(Cookie.cookie("name", "value"));
        Headers headers = new Headers().withEntries(Header.header("name", "value"));
        String method = "METHOD";
        String path = "path";
        Parameters queryStringParameters = new Parameters().withEntries(Parameter.param("name", "value"));
        HttpRequest httpRequest = new HttpRequest().withBody("body").withCookies(new Cookie("name", "value")).withHeaders(new Header("name", "value")).withMethod(method).withPath(path).withQueryStringParameter(new Parameter("name", "value")).withKeepAlive(true).withSecure(true);
        // when
        HttpRequestDTO httpRequestDTO = new HttpRequestDTO(httpRequest);
        // then
        MatcherAssert.assertThat(httpRequestDTO.getBody(), Is.is(body));
        MatcherAssert.assertThat(httpRequestDTO.getCookies(), Is.is(cookies));
        MatcherAssert.assertThat(httpRequestDTO.getHeaders(), Is.is(headers));
        MatcherAssert.assertThat(httpRequestDTO.getMethod(), Is.is(NottableString.string(method)));
        MatcherAssert.assertThat(httpRequestDTO.getPath(), Is.is(NottableString.string(path)));
        MatcherAssert.assertThat(httpRequestDTO.getQueryStringParameters(), Is.is(queryStringParameters));
        MatcherAssert.assertThat(httpRequestDTO.getKeepAlive(), Is.is(Boolean.TRUE));
        MatcherAssert.assertThat(httpRequestDTO.getSecure(), Is.is(Boolean.TRUE));
    }

    @Test
    public void shouldBuildObject() {
        // given
        String body = "body";
        Cookie cookie = new Cookie("name", "value");
        Header header = new Header("name", "value");
        String method = "METHOD";
        String path = "path";
        Parameter parameter = new Parameter("name", "value");
        HttpRequest httpRequest = new HttpRequest().withBody(body).withCookies(cookie).withHeaders(header).withMethod(method).withPath(path).withQueryStringParameter(parameter).withKeepAlive(true).withSecure(true);
        // when
        HttpRequest builtHttpRequest = buildObject();
        // then
        MatcherAssert.assertThat(builtHttpRequest.getBody(), Is.<org.mockserver.model.Body>is(StringBody.exact(body)));
        MatcherAssert.assertThat(builtHttpRequest.getCookieList(), containsInAnyOrder(cookie));
        MatcherAssert.assertThat(builtHttpRequest.getHeaderList(), containsInAnyOrder(header));
        MatcherAssert.assertThat(builtHttpRequest.getMethod(), Is.is(NottableString.string(method)));
        MatcherAssert.assertThat(builtHttpRequest.getPath(), Is.is(NottableString.string(path)));
        MatcherAssert.assertThat(builtHttpRequest.getQueryStringParameterList(), containsInAnyOrder(parameter));
        MatcherAssert.assertThat(builtHttpRequest.isKeepAlive(), Is.is(Boolean.TRUE));
        MatcherAssert.assertThat(builtHttpRequest.isSecure(), Is.is(Boolean.TRUE));
    }

    @Test
    public void shouldReturnValuesSetInSetter() {
        // given
        BodyDTO body = BodyDTO.createDTO(StringBody.exact("body"));
        Cookies cookies = new Cookies().withEntries(Cookie.cookie("name", "value"));
        Headers headers = new Headers().withEntries(Header.header("name", "value"));
        String method = "METHOD";
        String path = "path";
        Parameters queryStringParameters = new Parameters().withEntries(Parameter.param("name", "value"));
        HttpRequest httpRequest = new HttpRequest();
        // when
        HttpRequestDTO httpRequestDTO = new HttpRequestDTO(httpRequest);
        httpRequestDTO.setBody(body);
        httpRequestDTO.setCookies(cookies);
        httpRequestDTO.setHeaders(headers);
        httpRequestDTO.setMethod(NottableString.string(method));
        httpRequestDTO.setPath(NottableString.string(path));
        httpRequestDTO.setQueryStringParameters(queryStringParameters);
        httpRequestDTO.setKeepAlive(Boolean.TRUE);
        httpRequestDTO.setSecure(Boolean.TRUE);
        // then
        MatcherAssert.assertThat(httpRequestDTO.getBody(), Is.is(body));
        MatcherAssert.assertThat(httpRequestDTO.getCookies(), Is.is(cookies));
        MatcherAssert.assertThat(httpRequestDTO.getHeaders(), Is.is(headers));
        MatcherAssert.assertThat(httpRequestDTO.getMethod(), Is.is(NottableString.string(method)));
        MatcherAssert.assertThat(httpRequestDTO.getPath(), Is.is(NottableString.string(path)));
        MatcherAssert.assertThat(httpRequestDTO.getQueryStringParameters(), Is.is(queryStringParameters));
        MatcherAssert.assertThat(httpRequestDTO.getKeepAlive(), Is.is(Boolean.TRUE));
        MatcherAssert.assertThat(httpRequestDTO.getSecure(), Is.is(Boolean.TRUE));
    }

    @Test
    public void shouldHandleNullObjectInput() {
        // when
        HttpRequestDTO httpRequestDTO = new HttpRequestDTO(null);
        // then
        MatcherAssert.assertThat(httpRequestDTO.getBody(), Is.is(CoreMatchers.nullValue()));
        Assert.assertTrue(httpRequestDTO.getCookies().isEmpty());
        Assert.assertTrue(httpRequestDTO.getHeaders().isEmpty());
        MatcherAssert.assertThat(httpRequestDTO.getMethod(), Is.is(NottableString.string("")));
        MatcherAssert.assertThat(httpRequestDTO.getPath(), Is.is(NottableString.string("")));
        Assert.assertTrue(httpRequestDTO.getQueryStringParameters().isEmpty());
        MatcherAssert.assertThat(httpRequestDTO.getKeepAlive(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(httpRequestDTO.getSecure(), Is.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldHandleNullFieldInput() {
        // when
        HttpRequestDTO httpRequestDTO = new HttpRequestDTO(new HttpRequest());
        // then
        MatcherAssert.assertThat(httpRequestDTO.getBody(), Is.is(CoreMatchers.nullValue()));
        Assert.assertTrue(httpRequestDTO.getCookies().isEmpty());
        Assert.assertTrue(httpRequestDTO.getHeaders().isEmpty());
        MatcherAssert.assertThat(httpRequestDTO.getMethod(), Is.is(NottableString.string("")));
        MatcherAssert.assertThat(httpRequestDTO.getPath(), Is.is(NottableString.string("")));
        Assert.assertTrue(httpRequestDTO.getQueryStringParameters().isEmpty());
        MatcherAssert.assertThat(httpRequestDTO.getKeepAlive(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(httpRequestDTO.getSecure(), Is.is(CoreMatchers.nullValue()));
    }
}

