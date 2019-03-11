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
package ninja.utils;


import ContentTypes.APPLICATION_POST_FORM;
import ContentTypes.APPLICATION_XML;
import Context.NINJA_PROPERTIES_X_FORWARDED_FOR;
import Context.X_FORWARD_HEADER;
import Result.APPLICATION_JSON;
import Result.TEXT_HTML;
import Result.TEXT_PLAIN;
import com.google.common.collect.Maps;
import java.util.Map;
import ninja.Cookie;
import ninja.Result;
import ninja.Results;
import ninja.Route;
import ninja.bodyparser.BodyParserEngine;
import ninja.bodyparser.BodyParserEngineManager;
import ninja.session.FlashScope;
import ninja.session.Session;
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
public class AbstractContextTest {
    @Mock
    private Session sessionCookie;

    @Mock
    private FlashScope flashCookie;

    @Mock
    private BodyParserEngineManager bodyParserEngineManager;

    @Mock
    private Route route;

    @Mock
    private Validation validation;

    @Mock
    private BodyParserEngine bodyParserEngine;

    @Mock
    private NinjaProperties ninjaProperties;

    private AbstractContextImpl abstractContext;

    @Test
    public void getRemoteAddr() {
        AbstractContextImpl context = Mockito.spy(abstractContext);
        Mockito.doReturn("1.1.1.1").when(context).getRealRemoteAddr();
        Assert.assertThat(context.getRemoteAddr(), CoreMatchers.is("1.1.1.1"));
    }

    @Test
    public void getRemoteAddrIgnoresXForwardHeader() {
        AbstractContextImpl context = Mockito.spy(abstractContext);
        Mockito.when(ninjaProperties.getBooleanWithDefault(NINJA_PROPERTIES_X_FORWARDED_FOR, false)).thenReturn(Boolean.FALSE);
        Mockito.doReturn("1.1.1.1").when(context).getRealRemoteAddr();
        Mockito.doReturn("2.2.2.2").when(context).getHeader(X_FORWARD_HEADER);
        Assert.assertThat(context.getRemoteAddr(), CoreMatchers.is("1.1.1.1"));
    }

    @Test
    public void getRemoteAddrUsesXForwardHeader() {
        AbstractContextImpl context = Mockito.spy(abstractContext);
        Mockito.when(ninjaProperties.getBooleanWithDefault(NINJA_PROPERTIES_X_FORWARDED_FOR, false)).thenReturn(Boolean.TRUE);
        Mockito.doReturn("1.1.1.1").when(context).getRealRemoteAddr();
        Mockito.doReturn("2.2.2.2").when(context).getHeader(X_FORWARD_HEADER);
        Assert.assertThat(context.getRemoteAddr(), CoreMatchers.is("2.2.2.2"));
    }

    @Test
    public void getRemoteAddrParsesXForwardedForIfMoreThanOneHostPresent() {
        AbstractContextImpl context = Mockito.spy(abstractContext);
        Mockito.when(ninjaProperties.getBooleanWithDefault(NINJA_PROPERTIES_X_FORWARDED_FOR, false)).thenReturn(Boolean.TRUE);
        Mockito.doReturn("1.1.1.1").when(context).getRealRemoteAddr();
        Mockito.doReturn("192.168.1.1, 192.168.1.2, 192.168.1.3").when(context).getHeader(X_FORWARD_HEADER);
        // make sure this is correct
        Assert.assertThat(context.getRemoteAddr(), CoreMatchers.is("192.168.1.1"));
    }

    @Test
    public void getRemoteAddrUsesFallbackIfXForwardedForIsNotValidInetAddr() {
        AbstractContextImpl context = Mockito.spy(abstractContext);
        Mockito.when(ninjaProperties.getBooleanWithDefault(NINJA_PROPERTIES_X_FORWARDED_FOR, false)).thenReturn(Boolean.TRUE);
        Mockito.doReturn("1.1.1.1").when(context).getRealRemoteAddr();
        Mockito.doReturn("I_AM_NOT_A_VALID_ADDRESS").when(context).getHeader(X_FORWARD_HEADER);
        Assert.assertThat(context.getRemoteAddr(), CoreMatchers.is("1.1.1.1"));
    }

    @Test
    public void addCookieViaResult() {
        AbstractContextImpl context = Mockito.spy(abstractContext);
        Cookie cookie0 = Cookie.builder("cookie0", "yum0").setDomain("domain").build();
        Cookie cookie1 = Cookie.builder("cookie1", "yum1").setDomain("domain").build();
        // adding a cookie in the result will eventually trigger addCookie()...
        Result result = Results.html();
        result.addCookie(cookie0);
        result.addCookie(cookie1);
        Mockito.doNothing().when(context).addCookie(cookie0);
        Mockito.doNothing().when(context).addCookie(cookie1);
        // finalize the headers => the cookies must be copied over to the servletcookies
        context.finalizeHeaders(result);
        Mockito.verify(context, Mockito.times(1)).addCookie(cookie0);
        Mockito.verify(context, Mockito.times(1)).addCookie(cookie1);
    }

    @Test
    public void unsetCookieAddsCookieWithMaxAgeZero() {
        AbstractContextImpl context = Mockito.spy(abstractContext);
        Cookie cookie = Cookie.builder("cookie", "yummy").setDomain("domain").build();
        ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass(Cookie.class);
        Mockito.doNothing().when(context).addCookie(argument.capture());
        context.unsetCookie(cookie);
        Assert.assertThat(argument.getValue().getMaxAge(), CoreMatchers.is(0));
    }

    @Test
    public void getPathParameter() {
        AbstractContextImpl context = Mockito.spy(abstractContext);
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
    public void getPathParameterDecodingWorks() {
        AbstractContextImpl context = Mockito.spy(abstractContext);
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
    public void getPathParameterAsInteger() {
        AbstractContextImpl context = Mockito.spy(abstractContext);
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
    public void getParameterAsInteger() {
        AbstractContextImpl context = Mockito.spy(abstractContext);
        // this will not work and return null
        Mockito.doReturn(null).when(context).getParameter("key_not_there");
        Assert.assertEquals(null, context.getParameterAsInteger("key_not_there"));
        // this will return the default value:
        Mockito.doReturn(null).when(context).getParameter("key_not_there");
        Assert.assertEquals(new Integer(100), context.getParameterAsInteger("key_not_there", 100));
        // this will work as the value is there...
        Mockito.doReturn("1").when(context).getParameter("key");
        Assert.assertEquals(new Integer(1), context.getParameterAsInteger("key"));
    }

    @Test
    public void getParameterAs() {
        AbstractContextImpl context = Mockito.spy(abstractContext);
        Mockito.doReturn(null).when(context).getParameter("key");
        Mockito.doReturn("100").when(context).getParameter("key1");
        Mockito.doReturn("true").when(context).getParameter("key2");
        Mockito.doReturn("10.1").when(context).getParameter("key3");
        Mockito.doReturn("x").when(context).getParameter("key4");
        // this will not work and return null
        Assert.assertEquals(null, context.getParameterAs("key", Long.class));
        Assert.assertEquals(new Integer(100), context.getParameterAs("key1", Integer.class));
        Assert.assertEquals(new Long(100), context.getParameterAs("key1", Long.class));
        Assert.assertEquals(Boolean.TRUE, context.getParameterAs("key2", Boolean.class));
        Assert.assertEquals(new Float(10.1), context.getParameterAs("key3", Float.class));
        Assert.assertEquals(new Character('x'), context.getParameterAs("key4", Character.class));
    }

    @Test
    public void finalizeInAbstractContextSavesFlashSessionCookies() {
        AbstractContextImpl context = Mockito.spy(abstractContext);
        Result result = Results.json();
        Cookie cookie = Cookie.builder("TEST", "value").build();
        result.addCookie(cookie);
        Mockito.doNothing().when(context).addCookie(cookie);
        ResponseStreams streams = context.finalizeHeaders(result);
        // abstract finalizeHeaders does not return anything
        Assert.assertThat(streams, CoreMatchers.is(CoreMatchers.nullValue()));
        Mockito.verify(flashCookie, Mockito.times(1)).save(context);
        Mockito.verify(sessionCookie, Mockito.times(1)).save(context);
        Mockito.verify(context, Mockito.times(1)).addCookie(cookie);
    }

    @Test
    public void getAcceptContentType() {
        AbstractContextImpl context = Mockito.spy(abstractContext);
        Mockito.doReturn(null).when(context).getHeader("accept");
        Assert.assertEquals(TEXT_HTML, context.getAcceptContentType());
        Mockito.doReturn("").when(context).getHeader("accept");
        Assert.assertEquals(TEXT_HTML, context.getAcceptContentType());
        Mockito.doReturn("totally_unknown").when(context).getHeader("accept");
        Assert.assertEquals(TEXT_HTML, context.getAcceptContentType());
        Mockito.doReturn("application/json").when(context).getHeader("accept");
        Assert.assertEquals(APPLICATION_JSON, context.getAcceptContentType());
        Mockito.doReturn("text/html, application/json").when(context).getHeader("accept");
        Assert.assertEquals(TEXT_HTML, context.getAcceptContentType());
        Mockito.doReturn("application/xhtml, application/json").when(context).getHeader("accept");
        Assert.assertEquals(TEXT_HTML, context.getAcceptContentType());
        Mockito.doReturn("text/plain").when(context).getHeader("accept");
        Assert.assertEquals(TEXT_PLAIN, context.getAcceptContentType());
        Mockito.doReturn("text/plain, application/json").when(context).getHeader("accept");
        Assert.assertEquals(APPLICATION_JSON, context.getAcceptContentType());
    }

    @Test
    public void getAcceptEncoding() {
        AbstractContextImpl context = Mockito.spy(abstractContext);
        String encoding = "compress, gzip";
        Mockito.doReturn(encoding).when(context).getHeader("accept-encoding");
        Assert.assertEquals(encoding, context.getAcceptEncoding());
        encoding = null;
        Mockito.doReturn(encoding).when(context).getHeader("accept-encoding");
        Assert.assertNull(context.getAcceptEncoding());
        encoding = "gzip;q=1.0, identity; q=0.5, *;q=0";
        Mockito.doReturn(encoding).when(context).getHeader("accept-encoding");
        Assert.assertEquals(encoding, context.getAcceptEncoding());
    }

    @Test
    public void getAcceptLanguage() {
        AbstractContextImpl context = Mockito.spy(abstractContext);
        String language = "de";
        Mockito.doReturn(language).when(context).getHeader("accept-language");
        Assert.assertEquals(language, context.getAcceptLanguage());
        language = null;
        Mockito.doReturn(language).when(context).getHeader("accept-language");
        Assert.assertNull(context.getAcceptLanguage());
        language = "da, en-gb;q=0.8, en;q=0.7";
        Mockito.doReturn(language).when(context).getHeader("accept-language");
        Assert.assertEquals(language, context.getAcceptLanguage());
    }

    @Test
    public void getAcceptCharset() {
        AbstractContextImpl context = Mockito.spy(abstractContext);
        String charset = "UTF-8";
        Mockito.doReturn(charset).when(context).getHeader("accept-charset");
        Assert.assertEquals(charset, context.getAcceptCharset());
        charset = null;
        Mockito.doReturn(charset).when(context).getHeader("accept-charset");
        Assert.assertNull(context.getAcceptCharset());
        charset = "iso-8859-5, unicode-1-1;q=0.8";
        Mockito.doReturn(charset).when(context).getHeader("accept-charset");
        Assert.assertEquals(charset, context.getAcceptCharset());
    }

    public static class Dummy {
        String name;

        Long count;
    }

    @Test
    public void testParseBodyJsonWorks() {
        AbstractContextImpl context = Mockito.spy(abstractContext);
        Mockito.doReturn("application/json; charset=utf-8").when(context).getRequestContentType();
        Mockito.when(bodyParserEngineManager.getBodyParserEngineForContentType("application/json")).thenReturn(bodyParserEngine);
        Mockito.when(bodyParserEngine.invoke(context, AbstractContextTest.Dummy.class)).thenReturn(new AbstractContextTest.Dummy());
        Object o = parseBody(AbstractContextTest.Dummy.class);
        Mockito.verify(bodyParserEngineManager).getBodyParserEngineForContentType("application/json");
        Assert.assertTrue((o instanceof AbstractContextTest.Dummy));
    }

    @Test
    public void testParseBodyPostWorks() {
        AbstractContextImpl context = Mockito.spy(abstractContext);
        Mockito.doReturn(APPLICATION_POST_FORM).when(context).getRequestContentType();
        Mockito.when(bodyParserEngineManager.getBodyParserEngineForContentType(APPLICATION_POST_FORM)).thenReturn(bodyParserEngine);
        AbstractContextTest.Dummy dummy = new AbstractContextTest.Dummy();
        dummy.name = "post";
        dummy.count = 245L;
        Mockito.when(bodyParserEngine.invoke(context, AbstractContextTest.Dummy.class)).thenReturn(dummy);
        AbstractContextTest.Dummy o = context.parseBody(AbstractContextTest.Dummy.class);
        Mockito.verify(bodyParserEngineManager).getBodyParserEngineForContentType(APPLICATION_POST_FORM);
        Assert.assertTrue((o instanceof AbstractContextTest.Dummy));
        Assert.assertTrue(o.name.equals(dummy.name));
        Assert.assertTrue(o.count.equals(dummy.count));
    }

    @Test
    public void testIsJsonWorks() {
        AbstractContextImpl context = Mockito.spy(abstractContext);
        Mockito.doReturn(ContentTypes.APPLICATION_JSON).when(context).getRequestContentType();
        Assert.assertTrue(isRequestJson());
    }

    @Test
    public void testIsXmlWorks() {
        AbstractContextImpl context = Mockito.spy(abstractContext);
        Mockito.doReturn(APPLICATION_XML).when(context).getRequestContentType();
        Assert.assertTrue(isRequestXml());
    }

    @Test
    public void testParseBodyXmlWorks() {
        AbstractContextImpl context = Mockito.spy(abstractContext);
        Mockito.doReturn(APPLICATION_XML).when(context).getRequestContentType();
        Mockito.when(bodyParserEngineManager.getBodyParserEngineForContentType("application/xml")).thenReturn(bodyParserEngine);
        Mockito.when(bodyParserEngine.invoke(context, AbstractContextTest.Dummy.class)).thenReturn(new AbstractContextTest.Dummy());
        Object o = parseBody(AbstractContextTest.Dummy.class);
        Mockito.verify(bodyParserEngineManager).getBodyParserEngineForContentType("application/xml");
        Assert.assertTrue((o instanceof AbstractContextTest.Dummy));
    }

    @Test
    public void testParseBodyWithUnkownContentTypeWorks() {
        AbstractContextImpl context = Mockito.spy(abstractContext);
        Mockito.doReturn(null).when(context).getRequestContentType();
        Object o = parseBody(AbstractContextTest.Dummy.class);
        Assert.assertNull(o);
    }

    @Test
    public void testParseBodyWithUnknownRequestContentTypeWorks() {
        AbstractContextImpl context = Mockito.spy(abstractContext);
        Mockito.doReturn("application/UNKNOWN").when(context).getRequestContentType();
        Object o = parseBody(AbstractContextTest.Dummy.class);
        Assert.assertNull(o);
    }
}

