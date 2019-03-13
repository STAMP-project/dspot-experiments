/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja.servlet;


import ContentTypes.APPLICATION_POST_FORM;
import ContentTypes.APPLICATION_XML;
import Context.NINJA_PROPERTIES_X_FORWARDED_FOR;
import Context.X_FORWARD_HEADER;
import NinjaConstant.UPLOADS_MAX_FILE_SIZE;
import NinjaConstant.UPLOADS_MAX_TOTAL_SIZE;
import NinjaConstant.UTF_8;
import Result.APPLICATION_JSON;
import Result.TEXT_HTML;
import Result.TEXT_PLAIN;
import com.google.common.collect.Maps;
import com.google.inject.Injector;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ninja.Cookie;
import ninja.Result;
import ninja.Results;
import ninja.Route;
import ninja.bodyparser.BodyParserEngine;
import ninja.bodyparser.BodyParserEngineManager;
import ninja.ninja.Cookie;
import ninja.session.FlashScope;
import ninja.session.Session;
import ninja.utils.NinjaProperties;
import ninja.utils.ResultHandler;
import ninja.validation.Validation;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class NinjaServletContextTest {
    @Mock
    private Session sessionCookie;

    @Mock
    private FlashScope flashCookie;

    @Mock
    private BodyParserEngineManager bodyParserEngineManager;

    @Mock
    private ServletContext servletContext;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private Route route;

    @Mock
    private ResultHandler resultHandler;

    @Mock
    private Validation validation;

    @Mock
    private BodyParserEngine bodyParserEngine;

    @Mock
    private NinjaProperties ninjaProperties;

    @Mock
    private Injector injector;

    private NinjaServletContext context;

    @Test
    public void testGetRequestUri() {
        // say the httpServletRequest to return a certain value:
        Mockito.when(httpServletRequest.getRequestURI()).thenReturn("/index");
        // init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);
        // make sure this is correct
        Assert.assertEquals("/index", context.getRequestPath());
    }

    @Test
    public void testGetHostname() {
        // say the httpServletRequest to return a certain value:
        Mockito.when(httpServletRequest.getHeader("host")).thenReturn("test.com");
        // init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);
        // make sure this is correct
        Assert.assertEquals("test.com", context.getHostname());
    }

    @Test
    public void testGetRemoteAddrReturnsDefaultRemoteAddr() {
        // say the httpServletRequest to return a certain value:
        Mockito.when(httpServletRequest.getRemoteAddr()).thenReturn("mockedRemoteAddr");
        Mockito.when(httpServletRequest.getHeader(X_FORWARD_HEADER)).thenReturn("x-forwarded-for-mockedRemoteAddr");
        // init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);
        // make sure this is correct
        Assert.assertEquals("mockedRemoteAddr", context.getRemoteAddr());
    }

    @Test
    public void testGetRemoteAddrParsesXForwardedForIfSetInApplicationConf() {
        // say the httpServletRequest to return a certain value:
        Mockito.when(httpServletRequest.getRemoteAddr()).thenReturn("mockedRemoteAddr");
        Mockito.when(httpServletRequest.getHeader(X_FORWARD_HEADER)).thenReturn("192.168.1.44");
        Mockito.when(ninjaProperties.getBooleanWithDefault(NINJA_PROPERTIES_X_FORWARDED_FOR, false)).thenReturn(Boolean.TRUE);
        // init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);
        // make sure this is correct
        Assert.assertEquals("192.168.1.44", context.getRemoteAddr());
    }

    @Test
    public void testGetRemoteAddrParsesXForwardedForIfMoreThanOneHostPresent() {
        // say the httpServletRequest to return a certain value:
        Mockito.when(httpServletRequest.getRemoteAddr()).thenReturn("mockedRemoteAddr");
        Mockito.when(httpServletRequest.getHeader(X_FORWARD_HEADER)).thenReturn("192.168.1.1, 192.168.1.2, 192.168.1.3");
        Mockito.when(ninjaProperties.getBooleanWithDefault(NINJA_PROPERTIES_X_FORWARDED_FOR, false)).thenReturn(Boolean.TRUE);
        // init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);
        // make sure this is correct
        Assert.assertEquals("192.168.1.1", context.getRemoteAddr());
    }

    @Test
    public void testGetRemoteAddrUsesFallbackIfXForwardedForIsNotValidInetAddr() {
        // say the httpServletRequest to return a certain value:
        Mockito.when(httpServletRequest.getRemoteAddr()).thenReturn("mockedRemoteAddr");
        Mockito.when(httpServletRequest.getHeader(X_FORWARD_HEADER)).thenReturn("I_AM_NOT_A_VALID_ADDRESS");
        Mockito.when(ninjaProperties.getBooleanWithDefault(NINJA_PROPERTIES_X_FORWARDED_FOR, false)).thenReturn(Boolean.TRUE);
        // init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);
        // make sure this is correct
        Assert.assertEquals("mockedRemoteAddr", context.getRemoteAddr());
    }

    @Test
    public void testAddCookieViaResult() {
        Cookie cookie = Cookie.builder("cookie", "yum").setDomain("domain").build();
        context.init(servletContext, httpServletRequest, httpServletResponse);
        // context.addCookie(cookie);
        // generate an arbitrary result:
        Result result = Results.html();
        result.addCookie(cookie);
        // finalize the headers => the cookies must be copied over to the servletcookies
        context.finalizeHeaders(result);
        // and verify the stuff:
        ArgumentCaptor<javax.servlet.http.Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        Mockito.verify(httpServletResponse).addCookie(cookieCaptor.capture());
        javax.servlet.http.Cookie resultCookie = cookieCaptor.getValue();
        Assert.assertThat(resultCookie.getName(), CoreMatchers.equalTo("cookie"));
        Assert.assertThat(resultCookie.getValue(), CoreMatchers.equalTo("yum"));
        Assert.assertThat(resultCookie.getPath(), CoreMatchers.equalTo("/"));
        Assert.assertThat(resultCookie.getSecure(), CoreMatchers.equalTo(false));
        Assert.assertThat(resultCookie.getMaxAge(), CoreMatchers.equalTo((-1)));
    }

    @Test
    public void testAddCookieViaContext() {
        Cookie cookie = Cookie.builder("cookie", "yummy").setDomain("domain").build();
        context.init(servletContext, httpServletRequest, httpServletResponse);
        context.addCookie(cookie);
        // finalize the headers => the cookies must be copied over to the servletcookies
        context.finalizeHeaders(Results.html());
        // and verify the stuff:
        ArgumentCaptor<javax.servlet.http.Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        Mockito.verify(httpServletResponse).addCookie(cookieCaptor.capture());
        javax.servlet.http.Cookie resultCookie = cookieCaptor.getValue();
        Assert.assertThat(resultCookie.getName(), CoreMatchers.equalTo("cookie"));
        Assert.assertThat(resultCookie.getValue(), CoreMatchers.equalTo("yummy"));
        Assert.assertThat(resultCookie.getPath(), CoreMatchers.equalTo("/"));
        Assert.assertThat(resultCookie.getSecure(), CoreMatchers.equalTo(false));
        Assert.assertThat(resultCookie.getMaxAge(), CoreMatchers.equalTo((-1)));
    }

    @Test
    public void testUnsetCookieViaContext() {
        Cookie cookie = Cookie.builder("cookie", "yummy").setDomain("domain").build();
        context.init(servletContext, httpServletRequest, httpServletResponse);
        context.unsetCookie(cookie);
        // finalize the headers => the cookies must be copied over to the servletcookies
        context.finalizeHeaders(Results.html());
        // and verify the stuff:
        ArgumentCaptor<javax.servlet.http.Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        Mockito.verify(httpServletResponse).addCookie(cookieCaptor.capture());
        javax.servlet.http.Cookie resultCookie = cookieCaptor.getValue();
        Assert.assertThat(resultCookie.getName(), CoreMatchers.equalTo("cookie"));
        Assert.assertThat(resultCookie.getValue(), CoreMatchers.equalTo("yummy"));
        Assert.assertThat(resultCookie.getPath(), CoreMatchers.equalTo("/"));
        Assert.assertThat(resultCookie.getSecure(), CoreMatchers.equalTo(false));
        Assert.assertThat(resultCookie.getMaxAge(), CoreMatchers.equalTo(0));
    }

    @Test
    public void getCookieTest() {
        javax.servlet.http.Cookie servletCookie1 = new javax.servlet.http.Cookie("contextCookie1", "theValue1");
        javax.servlet.http.Cookie servletCookie2 = new javax.servlet.http.Cookie("contextCookie2", "theValue2");
        javax.servlet[] servletCookies = new Cookie[]{ servletCookie1, servletCookie2 };
        Mockito.when(httpServletRequest.getCookies()).thenReturn(servletCookies);
        context.init(servletContext, httpServletRequest, httpServletResponse);
        // negative test:
        ninja.Cookie doesNotExist = context.getCookie("doesNotExist");
        Assert.assertNull(doesNotExist);
        // test  against cookie that is really there
        ninja.Cookie cookie1 = context.getCookie("contextCookie1");
        Assert.assertEquals(cookie1.getName(), "contextCookie1");
        Assert.assertEquals(cookie1.getValue(), "theValue1");
        // test 2 against cookie that is really there
        ninja.Cookie cookie2 = context.getCookie("contextCookie2");
        Assert.assertEquals(cookie2.getName(), "contextCookie2");
        Assert.assertEquals(cookie2.getValue(), "theValue2");
    }

    @Test
    public void hasCookieTest() {
        javax.servlet.http.Cookie servletCookie1 = new javax.servlet.http.Cookie("contextCookie1", "theValue1");
        javax.servlet.http.Cookie servletCookie2 = new javax.servlet.http.Cookie("contextCookie2", "theValue2");
        javax.servlet[] servletCookies = new Cookie[]{ servletCookie1, servletCookie2 };
        Mockito.when(httpServletRequest.getCookies()).thenReturn(servletCookies);
        context.init(servletContext, httpServletRequest, httpServletResponse);
        // negative test:
        Assert.assertFalse(context.hasCookie("doesNotExist"));
        // test  against cookie that is really there
        Assert.assertTrue(context.hasCookie("contextCookie1"));
        // test 2 against cookie that is really there
        Assert.assertTrue(context.hasCookie("contextCookie2"));
    }

    @Test
    public void getCookiesTest() {
        javax.servlet.http.Cookie servletCookie1 = new javax.servlet.http.Cookie("contextCookie1", "theValue");
        javax.servlet.http.Cookie servletCookie2 = new javax.servlet.http.Cookie("contextCookie2", "theValue");
        javax.servlet[] servletCookiesEmpty = new Cookie[]{  };
        javax.servlet[] servletCookies = new Cookie[]{ servletCookie1, servletCookie2 };
        Mockito.when(httpServletRequest.getCookies()).thenReturn(servletCookiesEmpty);
        context.init(servletContext, httpServletRequest, httpServletResponse);
        // test when there are no cookies.
        Assert.assertEquals(0, context.getCookies().size());
        // now return some cookies:
        Mockito.when(httpServletRequest.getCookies()).thenReturn(servletCookies);
        Assert.assertEquals(2, context.getCookies().size());
    }

    @Test
    public void testGetPathParameter() {
        // init the context
        context.init(servletContext, httpServletRequest, httpServletResponse);
        // mock a parametermap:
        Map<String, String> parameterMap = Maps.newHashMap();
        parameterMap.put("parameter", "parameter");
        // and return the parameter map when any parameter is called...
        Mockito.when(route.getPathParametersEncoded(Matchers.anyString())).thenReturn(parameterMap);
        context.setRoute(route);
        // this parameter is not there and must return null
        Assert.assertEquals(null, context.getPathParameter("parameter_not_set"));
        Assert.assertEquals("parameter", context.getPathParameter("parameter"));
    }

    @Test
    public void testGetPathParameterDecodingWorks() {
        // init the context
        context.init(servletContext, httpServletRequest, httpServletResponse);
        // mock a parametermap:
        Map<String, String> parameterMap = Maps.newHashMap();
        parameterMap.put("parameter", "blue%2Fred%3Fand+green%E2%82%AC%2f");
        // and return the parameter map when any parameter is called...
        Mockito.when(route.getPathParametersEncoded(Matchers.anyString())).thenReturn(parameterMap);
        context.setRoute(route);
        // that is how the above parameter looks decoded correctly:
        Assert.assertEquals("blue/red?and+green?/", context.getPathParameter("parameter"));
    }

    @Test
    public void testGetPathParameterAsInteger() {
        // init the context
        context.init(servletContext, httpServletRequest, httpServletResponse);
        // mock a parametermap:
        Map<String, String> parameterMap = Maps.newHashMap();
        parameterMap.put("parameter", "parameter");
        // and return the parameter map when any parameter is called...
        Mockito.when(route.getPathParametersEncoded(Matchers.anyString())).thenReturn(parameterMap);
        context.setRoute(route);
        // this will not work and return null
        Assert.assertEquals(null, context.getPathParameterAsInteger("parameter"));
        // now set an integer into the parametermap:
        parameterMap.put("parameter", "1");
        // this will work and return 1
        Assert.assertEquals(new Integer(1), context.getPathParameterAsInteger("parameter"));
    }

    @Test
    public void testGetParameter() {
        // init the context
        context.init(servletContext, httpServletRequest, httpServletResponse);
        // and return the parameter map when any parameter is called...
        Mockito.when(httpServletRequest.getParameter("key")).thenReturn("value");
        // this will not work and return null
        Assert.assertEquals(null, context.getParameter("key_not_there"));
        // this will return the default value:
        Assert.assertEquals("defaultValue", context.getParameter("key_not_there", "defaultValue"));
        // this will work as the value is there...
        Assert.assertEquals("value", context.getParameter("key"));
    }

    @Test
    public void testGetParameterAsInteger() {
        // init the context
        context.init(servletContext, httpServletRequest, httpServletResponse);
        // and return the parameter map when any parameter is called...
        Mockito.when(httpServletRequest.getParameter("key")).thenReturn("1");
        // this will not work and return null
        Assert.assertEquals(null, context.getParameterAsInteger("key_not_there"));
        // this will return the default value:
        Assert.assertEquals(new Integer(100), context.getParameterAsInteger("key_not_there", 100));
        // this will work as the value is there...
        Assert.assertEquals(new Integer(1), context.getParameterAsInteger("key"));
    }

    @Test
    public void testGetParameterAs() {
        // init the context
        context.init(servletContext, httpServletRequest, httpServletResponse);
        // and return the parameter map when any parameter is called...
        Mockito.when(httpServletRequest.getParameter("key1")).thenReturn("100");
        Mockito.when(httpServletRequest.getParameter("key2")).thenReturn("true");
        Mockito.when(httpServletRequest.getParameter("key3")).thenReturn("10.1");
        Mockito.when(httpServletRequest.getParameter("key4")).thenReturn("x");
        // this will not work and return null
        Assert.assertEquals(null, context.getParameterAs("key", Long.class));
        Assert.assertEquals(new Integer(100), context.getParameterAs("key1", Integer.class));
        Assert.assertEquals(new Long(100), context.getParameterAs("key1", Long.class));
        Assert.assertEquals(Boolean.TRUE, context.getParameterAs("key2", Boolean.class));
        Assert.assertEquals(new Float(10.1), context.getParameterAs("key3", Float.class));
        Assert.assertEquals(new Character('x'), context.getParameterAs("key4", Character.class));
    }

    @Test
    public void testContentTypeGetsConvertedProperlyUponFinalize() {
        // init the context
        context.init(servletContext, httpServletRequest, httpServletResponse);
        // this must be Content-Type: application/json; encoding=utf-8
        Result result = Results.json();
        context.finalizeHeaders(result);
        Mockito.verify(httpServletResponse).setCharacterEncoding(result.getCharset());
        Mockito.verify(httpServletResponse).setContentType(result.getContentType());
    }

    @Test
    public void testContentTypeWithNullEncodingGetsConvertedProperlyUponFinalize() {
        // init the context
        context.init(servletContext, httpServletRequest, httpServletResponse);
        // this must be Content-Type: application/json; encoding=utf-8
        Result result = Results.json();
        // force a characterset that is not there. Stupid but tests that its working.
        result.charset(null);
        context.finalizeHeaders(result);
        // make sure utf-8 is used under all circumstances:
        Mockito.verify(httpServletResponse).setCharacterEncoding(UTF_8);
    }

    @Test
    public void testGetRequestPathWorksAsExpectedWithContext() {
        // we got a context
        Mockito.when(httpServletRequest.getContextPath()).thenReturn("/my/funky/prefix");
        // we got a request uri
        Mockito.when(httpServletRequest.getRequestURI()).thenReturn("/my/funky/prefix/myapp/is/here");
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertEquals("/myapp/is/here", context.getRequestPath());
    }

    @Test
    public void testGetRequestPathWorksAsExpectedWithOutContext() {
        // we got not context.
        // according to spec it will return an empty string
        Mockito.when(httpServletRequest.getContextPath()).thenReturn("");
        Mockito.when(httpServletRequest.getRequestURI()).thenReturn("/index");
        // we got a request uri
        Mockito.when(httpServletRequest.getRequestURI()).thenReturn("/myapp/is/here");
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertEquals("/myapp/is/here", context.getRequestPath());
    }

    @Test
    public void testGetRequestContentType() {
        String contentType = "text/html";
        Mockito.when(httpServletRequest.getContentType()).thenReturn(contentType);
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertEquals(contentType, context.getRequestContentType());
        contentType = null;
        Mockito.when(httpServletRequest.getContentType()).thenReturn(contentType);
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertNull(context.getRequestContentType());
        contentType = "text/html; charset=UTF-8";
        Mockito.when(httpServletRequest.getContentType()).thenReturn(contentType);
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertEquals(contentType, context.getRequestContentType());
    }

    @Test
    public void testGetAcceptContentType() {
        Mockito.when(httpServletRequest.getHeader("accept")).thenReturn(null);
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertEquals(TEXT_HTML, context.getAcceptContentType());
        Mockito.when(httpServletRequest.getHeader("accept")).thenReturn("");
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertEquals(TEXT_HTML, context.getAcceptContentType());
        Mockito.when(httpServletRequest.getHeader("accept")).thenReturn("totally_unknown");
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertEquals(TEXT_HTML, context.getAcceptContentType());
        Mockito.when(httpServletRequest.getHeader("accept")).thenReturn("application/json");
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertEquals(APPLICATION_JSON, context.getAcceptContentType());
        Mockito.when(httpServletRequest.getHeader("accept")).thenReturn("text/html, application/json");
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertEquals(TEXT_HTML, context.getAcceptContentType());
        Mockito.when(httpServletRequest.getHeader("accept")).thenReturn("application/xhtml, application/json");
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertEquals(TEXT_HTML, context.getAcceptContentType());
        Mockito.when(httpServletRequest.getHeader("accept")).thenReturn("text/plain");
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertEquals(TEXT_PLAIN, context.getAcceptContentType());
        Mockito.when(httpServletRequest.getHeader("accept")).thenReturn("text/plain, application/json");
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertEquals(APPLICATION_JSON, context.getAcceptContentType());
    }

    @Test
    public void testGetAcceptEncoding() {
        String encoding = "compress, gzip";
        Mockito.when(httpServletRequest.getHeader("accept-encoding")).thenReturn(encoding);
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertEquals(encoding, context.getAcceptEncoding());
        encoding = null;
        Mockito.when(httpServletRequest.getHeader("accept-encoding")).thenReturn(encoding);
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertNull(context.getAcceptEncoding());
        encoding = "gzip;q=1.0, identity; q=0.5, *;q=0";
        Mockito.when(httpServletRequest.getHeader("accept-encoding")).thenReturn(encoding);
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertEquals(encoding, context.getAcceptEncoding());
    }

    @Test
    public void testGetAcceptLanguage() {
        String language = "de";
        Mockito.when(httpServletRequest.getHeader("accept-language")).thenReturn(language);
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertEquals(language, context.getAcceptLanguage());
        language = null;
        Mockito.when(httpServletRequest.getHeader("accept-language")).thenReturn(language);
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertNull(context.getAcceptLanguage());
        language = "da, en-gb;q=0.8, en;q=0.7";
        Mockito.when(httpServletRequest.getHeader("accept-language")).thenReturn(language);
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertEquals(language, context.getAcceptLanguage());
    }

    @Test
    public void testGetAcceptCharset() {
        String charset = "UTF-8";
        Mockito.when(httpServletRequest.getHeader("accept-charset")).thenReturn(charset);
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertEquals(charset, context.getAcceptCharset());
        charset = null;
        Mockito.when(httpServletRequest.getHeader("accept-charset")).thenReturn(charset);
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertNull(context.getAcceptCharset());
        charset = "iso-8859-5, unicode-1-1;q=0.8";
        Mockito.when(httpServletRequest.getHeader("accept-charset")).thenReturn(charset);
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertEquals(charset, context.getAcceptCharset());
    }

    /**
     * This is the default mode.
     *
     * We get a Content-Type: application/json and want to parse the incoming json.
     */
    @Test
    public void testParseBodyJsonWorks() {
        Mockito.when(httpServletRequest.getContentType()).thenReturn("application/json; charset=utf-8");
        // init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Mockito.when(bodyParserEngineManager.getBodyParserEngineForContentType("application/json")).thenReturn(bodyParserEngine);
        Mockito.when(bodyParserEngine.invoke(context, NinjaServletContextTest.Dummy.class)).thenReturn(new NinjaServletContextTest.Dummy());
        Object o = context.parseBody(NinjaServletContextTest.Dummy.class);
        Mockito.verify(bodyParserEngineManager).getBodyParserEngineForContentType("application/json");
        Assert.assertTrue((o instanceof NinjaServletContextTest.Dummy));
    }

    @Test
    public void testParseBodyPostWorks() {
        Mockito.when(httpServletRequest.getContentType()).thenReturn(APPLICATION_POST_FORM);
        // init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Mockito.when(bodyParserEngineManager.getBodyParserEngineForContentType(APPLICATION_POST_FORM)).thenReturn(bodyParserEngine);
        NinjaServletContextTest.Dummy dummy = new NinjaServletContextTest.Dummy();
        dummy.name = "post";
        dummy.count = 245L;
        Mockito.when(bodyParserEngine.invoke(context, NinjaServletContextTest.Dummy.class)).thenReturn(dummy);
        NinjaServletContextTest.Dummy o = context.parseBody(NinjaServletContextTest.Dummy.class);
        Mockito.verify(bodyParserEngineManager).getBodyParserEngineForContentType(APPLICATION_POST_FORM);
        Assert.assertTrue((o instanceof NinjaServletContextTest.Dummy));
        Assert.assertTrue(o.name.equals(dummy.name));
        Assert.assertTrue(o.count.equals(dummy.count));
    }

    /**
     * Test for isJson
     */
    @Test
    public void testIsJsonWorks() {
        Mockito.when(httpServletRequest.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
        // init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertTrue(context.isRequestJson());
    }

    /**
     * Test is isXml
     */
    @Test
    public void testIsXmlWorks() {
        Mockito.when(httpServletRequest.getContentType()).thenReturn(APPLICATION_XML);
        // init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertTrue(context.isRequestXml());
    }

    /**
     * This is the default mode.
     *
     * We get a Content-Type: application/json and want to parse the incoming json.
     */
    @Test
    public void testParseBodyXmlWorks() {
        Mockito.when(httpServletRequest.getContentType()).thenReturn("application/xml");
        // init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Mockito.when(bodyParserEngineManager.getBodyParserEngineForContentType("application/xml")).thenReturn(bodyParserEngine);
        Mockito.when(bodyParserEngine.invoke(context, NinjaServletContextTest.Dummy.class)).thenReturn(new NinjaServletContextTest.Dummy());
        Object o = context.parseBody(NinjaServletContextTest.Dummy.class);
        Mockito.verify(bodyParserEngineManager).getBodyParserEngineForContentType("application/xml");
        Assert.assertTrue((o instanceof NinjaServletContextTest.Dummy));
    }

    /**
     * The request does not have the Content-Type set => we get a null response.
     */
    @Test
    public void testParseBodyWithUnkownContentTypeWorks() {
        Mockito.when(httpServletRequest.getContentType()).thenReturn(null);
        // init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Object o = context.parseBody(NinjaServletContextTest.Dummy.class);
        Assert.assertNull(o);
    }

    /**
     * We get an conetnt type that does not match any registered parsers.
     * This must also return null safely.
     */
    @Test
    public void testParseBodyWithUnknownRequestContentTypeWorks() {
        Mockito.when(httpServletRequest.getContentType()).thenReturn("application/UNKNOWN");
        // init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Object o = context.parseBody(NinjaServletContextTest.Dummy.class);
        Assert.assertNull(o);
    }

    // Dummy class used for parseBody tests.
    class Dummy {
        public String name;

        public Long count;
    }

    /**
     * Make sure the correct character encoding is set after init.
     */
    @Test
    public void testInitEnforcingOfCorrectEncoding() throws Exception {
        context.init(servletContext, httpServletRequest, httpServletResponse);
        // this proofs that the encoding has been set:
        Mockito.verify(httpServletRequest).setCharacterEncoding(UTF_8);
    }

    /**
     * Make sure the correct character encoding is set before the
     * reader is returned.
     */
    @Test
    public void testGetReaderEnforcingOfCorrectEncoding() throws Exception {
        context.init(servletContext, httpServletRequest, httpServletResponse);
        context.getReader();
        // this proofs that the encoding has been set:
        Mockito.verify(httpServletRequest).setCharacterEncoding(ArgumentMatchers.anyString());
    }

    /**
     * Make sure the correct character encoding is set before the
     * inputStream is returned.
     */
    @Test
    public void testGetInputStreamEnforcingOfCorrectEncoding() throws Exception {
        context.init(servletContext, httpServletRequest, httpServletResponse);
        context.getInputStream();
        // this proofs that the encoding has been set:
        Mockito.verify(httpServletRequest).setCharacterEncoding(ArgumentMatchers.anyString());
    }

    /**
     * We get an conetnt type that does not match any registered parsers.
     * This must also return null safely.
     */
    @Test
    public void testGetServletContext() {
        // init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Object o = context.getServletContext();
        Assert.assertNotNull(o);
        Assert.assertEquals(servletContext, o);
    }

    @Test
    public void testGetScheme() {
        final String scheme = "http";
        Mockito.when(httpServletRequest.getScheme()).thenReturn(scheme);
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertEquals(scheme, httpServletRequest.getScheme());
    }

    @Test
    public void testIsMultipart() {
        Mockito.when(httpServletRequest.getContentType()).thenReturn("multipart/form-data");
        Mockito.when(httpServletRequest.getMethod()).thenReturn("POST");
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertEquals(true, context.isMultipart());
    }

    @Test
    public void testGetUTF8ParameterInMultipart() throws Exception {
        String body = "------Ninja\r\n" + ((("Content-Disposition: form-data; name=\"utf8\"\r\n" + "\r\n") + "\u2713\r\n") + "------Ninja--\r\n");
        ServletInputStream sis = createHttpServletRequestInputStream(body.getBytes(UTF_8));
        Mockito.when(httpServletRequest.getContentType()).thenReturn("multipart/form-data; boundary=----Ninja");
        Mockito.when(httpServletRequest.getMethod()).thenReturn("POST");
        Mockito.when(ninjaProperties.getIntegerWithDefault(UPLOADS_MAX_FILE_SIZE, (-1))).thenReturn(1024);
        Mockito.when(ninjaProperties.getIntegerWithDefault(UPLOADS_MAX_TOTAL_SIZE, (-1))).thenReturn(1024);
        Mockito.when(httpServletRequest.getInputStream()).thenReturn(sis);
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertEquals("?", context.getParameter("utf8"));
    }

    @Test
    public void testGetWindows1250ParameterInMultipart() throws Exception {
        String body = "------Ninja\r\n" + ((((("content-disposition: form-data; name=\"field1\"\r\n" + "content-type: text/plain; charset=windows-1250\r\n") + "content-transfer-encoding: quoted-printable\r\n") + "\r\n") + "Joe owes \u20ac100.\r\n") + "------Ninja--\r\n");
        ServletInputStream sis = createHttpServletRequestInputStream(body.getBytes("windows-1250"));
        Mockito.when(httpServletRequest.getContentType()).thenReturn("multipart/form-data; boundary=----Ninja");
        Mockito.when(httpServletRequest.getMethod()).thenReturn("POST");
        Mockito.when(ninjaProperties.getIntegerWithDefault(UPLOADS_MAX_FILE_SIZE, (-1))).thenReturn(1024);
        Mockito.when(ninjaProperties.getIntegerWithDefault(UPLOADS_MAX_TOTAL_SIZE, (-1))).thenReturn(1024);
        Mockito.when(httpServletRequest.getInputStream()).thenReturn(sis);
        context.init(servletContext, httpServletRequest, httpServletResponse);
        Assert.assertEquals("Joe owes ?100.", context.getParameter("field1"));
    }
}

