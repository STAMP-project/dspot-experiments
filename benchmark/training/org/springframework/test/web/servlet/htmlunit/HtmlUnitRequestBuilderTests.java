/**
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.test.web.servlet.htmlunit;


import FormEncodingType.URL_ENCODED;
import HttpMethod.POST;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


/**
 * Unit tests for {@link HtmlUnitRequestBuilder}.
 *
 * @author Rob Winch
 * @author Sam Brannen
 * @since 4.2
 */
public class HtmlUnitRequestBuilderTests {
    private final WebClient webClient = new WebClient();

    private final ServletContext servletContext = new MockServletContext();

    private final Map<String, MockHttpSession> sessions = new HashMap<>();

    private WebRequest webRequest;

    private HtmlUnitRequestBuilder requestBuilder;

    // --- constructor
    @Test(expected = IllegalArgumentException.class)
    public void constructorNullSessions() {
        new HtmlUnitRequestBuilder(null, webClient, webRequest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullWebClient() {
        new HtmlUnitRequestBuilder(sessions, null, webRequest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullWebRequest() {
        new HtmlUnitRequestBuilder(sessions, webClient, null);
    }

    // --- buildRequest
    @Test
    @SuppressWarnings("deprecation")
    public void buildRequestBasicAuth() {
        String base64Credentials = "dXNlcm5hbWU6cGFzc3dvcmQ=";
        String authzHeaderValue = "Basic: " + base64Credentials;
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(base64Credentials);
        webRequest.setCredentials(credentials);
        webRequest.setAdditionalHeader("Authorization", authzHeaderValue);
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getAuthType(), Matchers.equalTo("Basic"));
        Assert.assertThat(actualRequest.getHeader("Authorization"), Matchers.equalTo(authzHeaderValue));
    }

    @Test
    public void buildRequestCharacterEncoding() {
        webRequest.setCharset(StandardCharsets.UTF_8);
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getCharacterEncoding(), Matchers.equalTo("UTF-8"));
    }

    @Test
    public void buildRequestDefaultCharacterEncoding() {
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getCharacterEncoding(), Matchers.equalTo("ISO-8859-1"));
    }

    @Test
    public void buildRequestContentLength() {
        String content = "some content that has length";
        webRequest.setHttpMethod(POST);
        webRequest.setRequestBody(content);
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getContentLength(), Matchers.equalTo(content.length()));
    }

    @Test
    public void buildRequestContentType() {
        String contentType = "text/html;charset=UTF-8";
        webRequest.setAdditionalHeader("Content-Type", contentType);
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getContentType(), Matchers.equalTo(contentType));
        Assert.assertThat(actualRequest.getHeader("Content-Type"), Matchers.equalTo(contentType));
    }

    // SPR-14916
    @Test
    public void buildRequestContentTypeWithFormSubmission() {
        webRequest.setEncodingType(URL_ENCODED);
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getContentType(), Matchers.equalTo("application/x-www-form-urlencoded"));
        Assert.assertThat(actualRequest.getHeader("Content-Type"), Matchers.equalTo("application/x-www-form-urlencoded;charset=ISO-8859-1"));
    }

    @Test
    public void buildRequestContextPathUsesFirstSegmentByDefault() {
        String contextPath = requestBuilder.buildRequest(servletContext).getContextPath();
        Assert.assertThat(contextPath, Matchers.equalTo("/test"));
    }

    @Test
    public void buildRequestContextPathUsesNoFirstSegmentWithDefault() throws MalformedURLException {
        webRequest.setUrl(new URL("http://example.com/"));
        String contextPath = requestBuilder.buildRequest(servletContext).getContextPath();
        Assert.assertThat(contextPath, Matchers.equalTo(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildRequestContextPathInvalid() {
        requestBuilder.setContextPath("/invalid");
        requestBuilder.buildRequest(servletContext).getContextPath();
    }

    @Test
    public void buildRequestContextPathEmpty() {
        String expected = "";
        requestBuilder.setContextPath(expected);
        String contextPath = requestBuilder.buildRequest(servletContext).getContextPath();
        Assert.assertThat(contextPath, Matchers.equalTo(expected));
    }

    @Test
    public void buildRequestContextPathExplicit() {
        String expected = "/test";
        requestBuilder.setContextPath(expected);
        String contextPath = requestBuilder.buildRequest(servletContext).getContextPath();
        Assert.assertThat(contextPath, Matchers.equalTo(expected));
    }

    @Test
    public void buildRequestContextPathMulti() {
        String expected = "/test/this";
        requestBuilder.setContextPath(expected);
        String contextPath = requestBuilder.buildRequest(servletContext).getContextPath();
        Assert.assertThat(contextPath, Matchers.equalTo(expected));
    }

    @Test
    public void buildRequestCookiesNull() {
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getCookies(), Matchers.nullValue());
    }

    @Test
    public void buildRequestCookiesSingle() {
        webRequest.setAdditionalHeader("Cookie", "name=value");
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Cookie[] cookies = actualRequest.getCookies();
        Assert.assertThat(cookies.length, Matchers.equalTo(1));
        Assert.assertThat(cookies[0].getName(), Matchers.equalTo("name"));
        Assert.assertThat(cookies[0].getValue(), Matchers.equalTo("value"));
    }

    @Test
    public void buildRequestCookiesMulti() {
        webRequest.setAdditionalHeader("Cookie", "name=value; name2=value2");
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Cookie[] cookies = actualRequest.getCookies();
        Assert.assertThat(cookies.length, Matchers.equalTo(2));
        Cookie cookie = cookies[0];
        Assert.assertThat(cookie.getName(), Matchers.equalTo("name"));
        Assert.assertThat(cookie.getValue(), Matchers.equalTo("value"));
        cookie = cookies[1];
        Assert.assertThat(cookie.getName(), Matchers.equalTo("name2"));
        Assert.assertThat(cookie.getValue(), Matchers.equalTo("value2"));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void buildRequestInputStream() throws Exception {
        String content = "some content that has length";
        webRequest.setHttpMethod(POST);
        webRequest.setRequestBody(content);
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(IOUtils.toString(actualRequest.getInputStream()), Matchers.equalTo(content));
    }

    @Test
    public void buildRequestLocalAddr() {
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getLocalAddr(), Matchers.equalTo("127.0.0.1"));
    }

    @Test
    public void buildRequestLocaleDefault() {
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getLocale(), Matchers.equalTo(Locale.getDefault()));
    }

    @Test
    public void buildRequestLocaleDa() {
        webRequest.setAdditionalHeader("Accept-Language", "da");
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getLocale(), Matchers.equalTo(new Locale("da")));
    }

    @Test
    public void buildRequestLocaleEnGbQ08() {
        webRequest.setAdditionalHeader("Accept-Language", "en-gb;q=0.8");
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getLocale(), Matchers.equalTo(new Locale("en", "gb")));
    }

    @Test
    public void buildRequestLocaleEnQ07() {
        webRequest.setAdditionalHeader("Accept-Language", "en");
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getLocale(), Matchers.equalTo(new Locale("en", "")));
    }

    @Test
    public void buildRequestLocaleEnUs() {
        webRequest.setAdditionalHeader("Accept-Language", "en-US");
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getLocale(), Matchers.equalTo(Locale.US));
    }

    @Test
    public void buildRequestLocaleFr() {
        webRequest.setAdditionalHeader("Accept-Language", "fr");
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getLocale(), Matchers.equalTo(Locale.FRENCH));
    }

    @Test
    public void buildRequestLocaleMulti() {
        webRequest.setAdditionalHeader("Accept-Language", "en-gb;q=0.8, da, en;q=0.7");
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        List<Locale> expected = Arrays.asList(new Locale("da"), new Locale("en", "gb"), new Locale("en", ""));
        Assert.assertThat(Collections.list(actualRequest.getLocales()), Matchers.equalTo(expected));
    }

    @Test
    public void buildRequestLocalName() {
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getLocalName(), Matchers.equalTo("localhost"));
    }

    @Test
    public void buildRequestLocalPort() {
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getLocalPort(), Matchers.equalTo(80));
    }

    @Test
    public void buildRequestLocalMissing() throws Exception {
        webRequest.setUrl(new URL("http://localhost/test/this"));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getLocalPort(), Matchers.equalTo((-1)));
    }

    @Test
    public void buildRequestMethods() {
        for (HttpMethod expectedMethod : HttpMethod.values()) {
            webRequest.setHttpMethod(expectedMethod);
            String actualMethod = requestBuilder.buildRequest(servletContext).getMethod();
            Assert.assertThat(actualMethod, Matchers.equalTo(expectedMethod.name()));
        }
    }

    @Test
    public void buildRequestParameterMapViaWebRequestDotSetRequestParametersWithSingleRequestParam() {
        webRequest.setRequestParameters(Arrays.asList(new NameValuePair("name", "value")));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getParameterMap().size(), Matchers.equalTo(1));
        Assert.assertThat(actualRequest.getParameter("name"), Matchers.equalTo("value"));
    }

    @Test
    public void buildRequestParameterMapViaWebRequestDotSetRequestParametersWithSingleRequestParamWithNullValue() {
        webRequest.setRequestParameters(Arrays.asList(new NameValuePair("name", null)));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getParameterMap().size(), Matchers.equalTo(1));
        Assert.assertThat(actualRequest.getParameter("name"), Matchers.nullValue());
    }

    @Test
    public void buildRequestParameterMapViaWebRequestDotSetRequestParametersWithSingleRequestParamWithEmptyValue() {
        webRequest.setRequestParameters(Arrays.asList(new NameValuePair("name", "")));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getParameterMap().size(), Matchers.equalTo(1));
        Assert.assertThat(actualRequest.getParameter("name"), Matchers.equalTo(""));
    }

    @Test
    public void buildRequestParameterMapViaWebRequestDotSetRequestParametersWithSingleRequestParamWithValueSetToSpace() {
        webRequest.setRequestParameters(Arrays.asList(new NameValuePair("name", " ")));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getParameterMap().size(), Matchers.equalTo(1));
        Assert.assertThat(actualRequest.getParameter("name"), Matchers.equalTo(" "));
    }

    @Test
    public void buildRequestParameterMapViaWebRequestDotSetRequestParametersWithMultipleRequestParams() {
        webRequest.setRequestParameters(Arrays.asList(new NameValuePair("name1", "value1"), new NameValuePair("name2", "value2")));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getParameterMap().size(), Matchers.equalTo(2));
        Assert.assertThat(actualRequest.getParameter("name1"), Matchers.equalTo("value1"));
        Assert.assertThat(actualRequest.getParameter("name2"), Matchers.equalTo("value2"));
    }

    @Test
    public void buildRequestParameterMapFromSingleQueryParam() throws Exception {
        webRequest.setUrl(new URL("http://example.com/example/?name=value"));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getParameterMap().size(), Matchers.equalTo(1));
        Assert.assertThat(actualRequest.getParameter("name"), Matchers.equalTo("value"));
    }

    // SPR-14177
    @Test
    public void buildRequestParameterMapDecodesParameterName() throws Exception {
        webRequest.setUrl(new URL("http://example.com/example/?row%5B0%5D=value"));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getParameterMap().size(), Matchers.equalTo(1));
        Assert.assertThat(actualRequest.getParameter("row[0]"), Matchers.equalTo("value"));
    }

    @Test
    public void buildRequestParameterMapDecodesParameterValue() throws Exception {
        webRequest.setUrl(new URL("http://example.com/example/?name=row%5B0%5D"));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getParameterMap().size(), Matchers.equalTo(1));
        Assert.assertThat(actualRequest.getParameter("name"), Matchers.equalTo("row[0]"));
    }

    @Test
    public void buildRequestParameterMapFromSingleQueryParamWithoutValueAndWithoutEqualsSign() throws Exception {
        webRequest.setUrl(new URL("http://example.com/example/?name"));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getParameterMap().size(), Matchers.equalTo(1));
        Assert.assertThat(actualRequest.getParameter("name"), Matchers.equalTo(""));
    }

    @Test
    public void buildRequestParameterMapFromSingleQueryParamWithoutValueButWithEqualsSign() throws Exception {
        webRequest.setUrl(new URL("http://example.com/example/?name="));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getParameterMap().size(), Matchers.equalTo(1));
        Assert.assertThat(actualRequest.getParameter("name"), Matchers.equalTo(""));
    }

    @Test
    public void buildRequestParameterMapFromSingleQueryParamWithValueSetToEncodedSpace() throws Exception {
        webRequest.setUrl(new URL("http://example.com/example/?name=%20"));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getParameterMap().size(), Matchers.equalTo(1));
        Assert.assertThat(actualRequest.getParameter("name"), Matchers.equalTo(" "));
    }

    @Test
    public void buildRequestParameterMapFromMultipleQueryParams() throws Exception {
        webRequest.setUrl(new URL("http://example.com/example/?name=value&param2=value+2"));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getParameterMap().size(), Matchers.equalTo(2));
        Assert.assertThat(actualRequest.getParameter("name"), Matchers.equalTo("value"));
        Assert.assertThat(actualRequest.getParameter("param2"), Matchers.equalTo("value 2"));
    }

    @Test
    public void buildRequestPathInfo() throws Exception {
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getPathInfo(), Matchers.nullValue());
    }

    @Test
    public void buildRequestPathInfoNull() throws Exception {
        webRequest.setUrl(new URL("http://example.com/example"));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getPathInfo(), Matchers.nullValue());
    }

    @Test
    public void buildRequestAndAntPathRequestMatcher() throws Exception {
        webRequest.setUrl(new URL("http://example.com/app/login/authenticate"));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        // verify it is going to work with Spring Security's AntPathRequestMatcher
        Assert.assertThat(actualRequest.getPathInfo(), Matchers.nullValue());
        Assert.assertThat(actualRequest.getServletPath(), Matchers.equalTo("/login/authenticate"));
    }

    @Test
    public void buildRequestProtocol() throws Exception {
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getProtocol(), Matchers.equalTo("HTTP/1.1"));
    }

    @Test
    public void buildRequestQueryWithSingleQueryParam() throws Exception {
        String expectedQuery = "param=value";
        webRequest.setUrl(new URL(("http://example.com/example?" + expectedQuery)));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getQueryString(), Matchers.equalTo(expectedQuery));
    }

    @Test
    public void buildRequestQueryWithSingleQueryParamWithoutValueAndWithoutEqualsSign() throws Exception {
        String expectedQuery = "param";
        webRequest.setUrl(new URL(("http://example.com/example?" + expectedQuery)));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getQueryString(), Matchers.equalTo(expectedQuery));
    }

    @Test
    public void buildRequestQueryWithSingleQueryParamWithoutValueButWithEqualsSign() throws Exception {
        String expectedQuery = "param=";
        webRequest.setUrl(new URL(("http://example.com/example?" + expectedQuery)));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getQueryString(), Matchers.equalTo(expectedQuery));
    }

    @Test
    public void buildRequestQueryWithSingleQueryParamWithValueSetToEncodedSpace() throws Exception {
        String expectedQuery = "param=%20";
        webRequest.setUrl(new URL(("http://example.com/example?" + expectedQuery)));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getQueryString(), Matchers.equalTo(expectedQuery));
    }

    @Test
    public void buildRequestQueryWithMultipleQueryParams() throws Exception {
        String expectedQuery = "param1=value1&param2=value2";
        webRequest.setUrl(new URL(("http://example.com/example?" + expectedQuery)));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getQueryString(), Matchers.equalTo(expectedQuery));
    }

    @Test
    public void buildRequestReader() throws Exception {
        String expectedBody = "request body";
        webRequest.setHttpMethod(POST);
        webRequest.setRequestBody(expectedBody);
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(IOUtils.toString(actualRequest.getReader()), Matchers.equalTo(expectedBody));
    }

    @Test
    public void buildRequestRemoteAddr() throws Exception {
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getRemoteAddr(), Matchers.equalTo("127.0.0.1"));
    }

    @Test
    public void buildRequestRemoteHost() throws Exception {
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getRemoteAddr(), Matchers.equalTo("127.0.0.1"));
    }

    @Test
    public void buildRequestRemotePort() throws Exception {
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getRemotePort(), Matchers.equalTo(80));
    }

    @Test
    public void buildRequestRemotePort8080() throws Exception {
        webRequest.setUrl(new URL("http://example.com:8080/"));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getRemotePort(), Matchers.equalTo(8080));
    }

    @Test
    public void buildRequestRemotePort80WithDefault() throws Exception {
        webRequest.setUrl(new URL("http://example.com/"));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getRemotePort(), Matchers.equalTo(80));
    }

    @Test
    public void buildRequestRequestedSessionId() throws Exception {
        String sessionId = "session-id";
        webRequest.setAdditionalHeader("Cookie", ("JSESSIONID=" + sessionId));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getRequestedSessionId(), Matchers.equalTo(sessionId));
    }

    @Test
    public void buildRequestRequestedSessionIdNull() throws Exception {
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getRequestedSessionId(), Matchers.nullValue());
    }

    @Test
    public void buildRequestUri() {
        String uri = requestBuilder.buildRequest(servletContext).getRequestURI();
        Assert.assertThat(uri, Matchers.equalTo("/test/this/here"));
    }

    @Test
    public void buildRequestUrl() {
        String uri = requestBuilder.buildRequest(servletContext).getRequestURL().toString();
        Assert.assertThat(uri, Matchers.equalTo("http://example.com/test/this/here"));
    }

    @Test
    public void buildRequestSchemeHttp() throws Exception {
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getScheme(), Matchers.equalTo("http"));
    }

    @Test
    public void buildRequestSchemeHttps() throws Exception {
        webRequest.setUrl(new URL("https://example.com/"));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getScheme(), Matchers.equalTo("https"));
    }

    @Test
    public void buildRequestServerName() throws Exception {
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getServerName(), Matchers.equalTo("example.com"));
    }

    @Test
    public void buildRequestServerPort() throws Exception {
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getServerPort(), Matchers.equalTo(80));
    }

    @Test
    public void buildRequestServerPortDefault() throws Exception {
        webRequest.setUrl(new URL("https://example.com/"));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getServerPort(), Matchers.equalTo((-1)));
    }

    @Test
    public void buildRequestServletContext() throws Exception {
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getServletContext(), Matchers.equalTo(servletContext));
    }

    @Test
    public void buildRequestServletPath() throws Exception {
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getServletPath(), Matchers.equalTo("/this/here"));
    }

    @Test
    public void buildRequestSession() throws Exception {
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        HttpSession newSession = actualRequest.getSession();
        Assert.assertThat(newSession, Matchers.notNullValue());
        assertSingleSessionCookie((("JSESSIONID=" + (newSession.getId())) + "; Path=/test; Domain=example.com"));
        webRequest.setAdditionalHeader("Cookie", ("JSESSIONID=" + (newSession.getId())));
        requestBuilder = new HtmlUnitRequestBuilder(sessions, webClient, webRequest);
        actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getSession(), Matchers.sameInstance(newSession));
    }

    @Test
    public void buildRequestSessionWithExistingSession() throws Exception {
        String sessionId = "session-id";
        webRequest.setAdditionalHeader("Cookie", ("JSESSIONID=" + sessionId));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        HttpSession session = actualRequest.getSession();
        Assert.assertThat(session.getId(), Matchers.equalTo(sessionId));
        assertSingleSessionCookie((("JSESSIONID=" + (session.getId())) + "; Path=/test; Domain=example.com"));
        requestBuilder = new HtmlUnitRequestBuilder(sessions, webClient, webRequest);
        actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getSession(), Matchers.equalTo(session));
        webRequest.setAdditionalHeader("Cookie", (("JSESSIONID=" + sessionId) + "NEW"));
        actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getSession(), Matchers.not(Matchers.equalTo(session)));
        assertSingleSessionCookie((("JSESSIONID=" + (actualRequest.getSession().getId())) + "; Path=/test; Domain=example.com"));
    }

    @Test
    public void buildRequestSessionTrue() throws Exception {
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        HttpSession session = actualRequest.getSession(true);
        Assert.assertThat(session, Matchers.notNullValue());
    }

    @Test
    public void buildRequestSessionFalseIsNull() throws Exception {
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        HttpSession session = actualRequest.getSession(false);
        Assert.assertThat(session, Matchers.nullValue());
    }

    @Test
    public void buildRequestSessionFalseWithExistingSession() throws Exception {
        String sessionId = "session-id";
        webRequest.setAdditionalHeader("Cookie", ("JSESSIONID=" + sessionId));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        HttpSession session = actualRequest.getSession(false);
        Assert.assertThat(session, Matchers.notNullValue());
    }

    @Test
    public void buildRequestSessionIsNew() throws Exception {
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getSession().isNew(), Matchers.equalTo(true));
    }

    @Test
    public void buildRequestSessionIsNewFalse() throws Exception {
        String sessionId = "session-id";
        webRequest.setAdditionalHeader("Cookie", ("JSESSIONID=" + sessionId));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getSession().isNew(), Matchers.equalTo(false));
    }

    @Test
    public void buildRequestSessionInvalidate() throws Exception {
        String sessionId = "session-id";
        webRequest.setAdditionalHeader("Cookie", ("JSESSIONID=" + sessionId));
        MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
        HttpSession sessionToRemove = actualRequest.getSession();
        sessionToRemove.invalidate();
        Assert.assertThat(sessions.containsKey(sessionToRemove.getId()), Matchers.equalTo(false));
        assertSingleSessionCookie((("JSESSIONID=" + (sessionToRemove.getId())) + "; Expires=Thu, 01-Jan-1970 00:00:01 GMT; Path=/test; Domain=example.com"));
        webRequest.removeAdditionalHeader("Cookie");
        requestBuilder = new HtmlUnitRequestBuilder(sessions, webClient, webRequest);
        actualRequest = requestBuilder.buildRequest(servletContext);
        Assert.assertThat(actualRequest.getSession().isNew(), Matchers.equalTo(true));
        Assert.assertThat(sessions.containsKey(sessionToRemove.getId()), Matchers.equalTo(false));
    }

    // --- setContextPath
    @Test
    public void setContextPathNull() {
        requestBuilder.setContextPath(null);
        Assert.assertThat(getContextPath(), Matchers.nullValue());
    }

    @Test
    public void setContextPathEmptyString() {
        requestBuilder.setContextPath("");
        Assert.assertThat(getContextPath(), Matchers.isEmptyString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setContextPathDoesNotStartWithSlash() {
        requestBuilder.setContextPath("abc/def");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setContextPathEndsWithSlash() {
        requestBuilder.setContextPath("/abc/def/");
    }

    @Test
    public void setContextPath() {
        String expectedContextPath = "/abc/def";
        requestBuilder.setContextPath(expectedContextPath);
        Assert.assertThat(getContextPath(), Matchers.equalTo(expectedContextPath));
    }

    @Test
    public void mergeHeader() throws Exception {
        String headerName = "PARENT";
        String headerValue = "VALUE";
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new HelloController()).defaultRequest(MockMvcRequestBuilders.get("/").header(headerName, headerValue)).build();
        Assert.assertThat(mockMvc.perform(requestBuilder).andReturn().getRequest().getHeader(headerName), Matchers.equalTo(headerValue));
    }

    @Test
    public void mergeSession() throws Exception {
        String attrName = "PARENT";
        String attrValue = "VALUE";
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new HelloController()).defaultRequest(MockMvcRequestBuilders.get("/").sessionAttr(attrName, attrValue)).build();
        Assert.assertThat(mockMvc.perform(requestBuilder).andReturn().getRequest().getSession().getAttribute(attrName), Matchers.equalTo(attrValue));
    }

    @Test
    public void mergeSessionNotInitialized() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new HelloController()).defaultRequest(MockMvcRequestBuilders.get("/")).build();
        Assert.assertThat(mockMvc.perform(requestBuilder).andReturn().getRequest().getSession(false), Matchers.nullValue());
    }

    @Test
    public void mergeParameter() throws Exception {
        String paramName = "PARENT";
        String paramValue = "VALUE";
        String paramValue2 = "VALUE2";
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new HelloController()).defaultRequest(MockMvcRequestBuilders.get("/").param(paramName, paramValue, paramValue2)).build();
        MockHttpServletRequest performedRequest = mockMvc.perform(requestBuilder).andReturn().getRequest();
        Assert.assertThat(Arrays.asList(performedRequest.getParameterValues(paramName)), Matchers.contains(paramValue, paramValue2));
    }

    @Test
    public void mergeCookie() throws Exception {
        String cookieName = "PARENT";
        String cookieValue = "VALUE";
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new HelloController()).defaultRequest(MockMvcRequestBuilders.get("/").cookie(new Cookie(cookieName, cookieValue))).build();
        Cookie[] cookies = mockMvc.perform(requestBuilder).andReturn().getRequest().getCookies();
        Assert.assertThat(cookies, Matchers.notNullValue());
        Assert.assertThat(cookies.length, Matchers.equalTo(1));
        Cookie cookie = cookies[0];
        Assert.assertThat(cookie.getName(), Matchers.equalTo(cookieName));
        Assert.assertThat(cookie.getValue(), Matchers.equalTo(cookieValue));
    }

    @Test
    public void mergeRequestAttribute() throws Exception {
        String attrName = "PARENT";
        String attrValue = "VALUE";
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new HelloController()).defaultRequest(MockMvcRequestBuilders.get("/").requestAttr(attrName, attrValue)).build();
        Assert.assertThat(mockMvc.perform(requestBuilder).andReturn().getRequest().getAttribute(attrName), Matchers.equalTo(attrValue));
    }

    // SPR-14584
    @Test
    public void mergeDoesNotCorruptPathInfoOnParent() throws Exception {
        String pathInfo = "/foo/bar";
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new HelloController()).defaultRequest(MockMvcRequestBuilders.get("/")).build();
        Assert.assertThat(mockMvc.perform(MockMvcRequestBuilders.get(pathInfo)).andReturn().getRequest().getPathInfo(), Matchers.equalTo(pathInfo));
        mockMvc.perform(requestBuilder);
        Assert.assertThat(mockMvc.perform(MockMvcRequestBuilders.get(pathInfo)).andReturn().getRequest().getPathInfo(), Matchers.equalTo(pathInfo));
    }
}

