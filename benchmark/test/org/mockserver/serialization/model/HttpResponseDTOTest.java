package org.mockserver.serialization.model;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.Cookie;
import org.mockserver.model.Header;
import org.mockserver.model.StringBody;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpResponseDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // given
        BodyDTO body = BodyDTO.createDTO(StringBody.exact("body"));
        Cookies cookies = new Cookies().withEntries(Cookie.cookie("name", "value"));
        Headers headers = new Headers().withEntries(Header.header("name", "value"));
        Integer statusCode = 200;
        String randomPhrase = "randomPhrase";
        ConnectionOptionsDTO connectionOptions = new ConnectionOptionsDTO().setContentLengthHeaderOverride(50);
        HttpResponse httpResponse = new HttpResponse().withBody("body").withCookies(new Cookie("name", "value")).withHeaders(new Header("name", "value")).withStatusCode(statusCode).withReasonPhrase(randomPhrase).withConnectionOptions(new ConnectionOptions().withContentLengthHeaderOverride(50));
        // when
        HttpResponseDTO httpResponseDTO = new HttpResponseDTO(httpResponse);
        // then
        MatcherAssert.assertThat(httpResponseDTO.getBody(), Is.is(body));
        MatcherAssert.assertThat(httpResponseDTO.getCookies(), Is.is(cookies));
        MatcherAssert.assertThat(httpResponseDTO.getHeaders(), Is.is(headers));
        MatcherAssert.assertThat(httpResponseDTO.getStatusCode(), Is.is(statusCode));
        MatcherAssert.assertThat(httpResponseDTO.getReasonPhrase(), Is.is(randomPhrase));
        MatcherAssert.assertThat(httpResponseDTO.getConnectionOptions(), Is.is(connectionOptions));
    }

    @Test
    public void shouldBuildObject() {
        // given
        String body = "body";
        Cookie cookie = new Cookie("name", "value");
        Header header = new Header("name", "value");
        Integer statusCode = 200;
        String randomPhrase = "randomPhrase";
        ConnectionOptions connectionOptions = new ConnectionOptions().withContentLengthHeaderOverride(50);
        HttpResponse httpResponse = new HttpResponse().withBody(body).withCookies(cookie).withHeaders(header).withStatusCode(statusCode).withReasonPhrase(randomPhrase).withConnectionOptions(connectionOptions);
        // when
        HttpResponse builtHttpResponse = buildObject();
        // then
        MatcherAssert.assertThat(builtHttpResponse.getBody(), Is.<org.mockserver.model.Body>is(StringBody.exact(body)));
        MatcherAssert.assertThat(builtHttpResponse.getCookieList(), containsInAnyOrder(cookie));
        MatcherAssert.assertThat(builtHttpResponse.getHeaderList(), containsInAnyOrder(header));
        MatcherAssert.assertThat(builtHttpResponse.getStatusCode(), Is.is(statusCode));
        MatcherAssert.assertThat(builtHttpResponse.getReasonPhrase(), Is.is(randomPhrase));
        MatcherAssert.assertThat(builtHttpResponse.getConnectionOptions(), Is.is(connectionOptions));
    }

    @Test
    public void shouldReturnValuesSetInSetter() {
        // given
        BodyWithContentTypeDTO body = BodyWithContentTypeDTO.createDTO(StringBody.exact("body"));
        Cookies cookies = new Cookies().withEntries(Cookie.cookie("name", "value"));
        Headers headers = new Headers().withEntries(Header.header("name", "value"));
        Integer statusCode = 200;
        String randomPhrase = "randomPhrase";
        ConnectionOptionsDTO connectionOptions = new ConnectionOptionsDTO().setContentLengthHeaderOverride(50);
        HttpResponse httpResponse = new HttpResponse();
        // when
        HttpResponseDTO httpResponseDTO = new HttpResponseDTO(httpResponse);
        httpResponseDTO.setBody(body);
        httpResponseDTO.setCookies(cookies);
        httpResponseDTO.setHeaders(headers);
        httpResponseDTO.setStatusCode(statusCode);
        httpResponseDTO.setReasonPhrase(randomPhrase);
        httpResponseDTO.setConnectionOptions(connectionOptions);
        // then
        MatcherAssert.assertThat(httpResponseDTO.getBody(), Is.is(body));
        MatcherAssert.assertThat(httpResponseDTO.getCookies(), Is.is(cookies));
        MatcherAssert.assertThat(httpResponseDTO.getHeaders(), Is.is(headers));
        MatcherAssert.assertThat(httpResponseDTO.getStatusCode(), Is.is(statusCode));
        MatcherAssert.assertThat(httpResponseDTO.getReasonPhrase(), Is.is(randomPhrase));
        MatcherAssert.assertThat(httpResponseDTO.getConnectionOptions(), Is.is(connectionOptions));
    }

    @Test
    public void shouldHandleNullObjectInput() {
        // when
        HttpResponseDTO httpResponseDTO = new HttpResponseDTO(null);
        // then
        MatcherAssert.assertThat(httpResponseDTO.getBody(), Is.is(CoreMatchers.nullValue()));
        Assert.assertTrue(httpResponseDTO.getCookies().isEmpty());
        Assert.assertTrue(httpResponseDTO.getHeaders().isEmpty());
        MatcherAssert.assertThat(httpResponseDTO.getStatusCode(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(httpResponseDTO.getReasonPhrase(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(httpResponseDTO.getConnectionOptions(), Is.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldHandleNullFieldInput() {
        // when
        HttpResponseDTO httpResponseDTO = new HttpResponseDTO(new HttpResponse());
        // then
        MatcherAssert.assertThat(httpResponseDTO.getBody(), Is.is(CoreMatchers.nullValue()));
        Assert.assertTrue(httpResponseDTO.getCookies().isEmpty());
        Assert.assertTrue(httpResponseDTO.getHeaders().isEmpty());
        MatcherAssert.assertThat(httpResponseDTO.getStatusCode(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(httpResponseDTO.getReasonPhrase(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(httpResponseDTO.getConnectionOptions(), Is.is(CoreMatchers.nullValue()));
    }
}

