/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.web.servlet.view;


import DispatcherServlet.OUTPUT_FLASH_MAP_ATTRIBUTE;
import DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE;
import HttpStatus.CREATED;
import HttpStatus.MOVED_PERMANENTLY;
import View.RESPONSE_STATUS_ATTRIBUTE;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestDataValueProcessor;
import org.springframework.web.servlet.support.RequestDataValueProcessorWrapper;


/**
 * Tests for redirect view, and query string construction.
 * Doesn't test URL encoding, although it does check that it's called.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 27.05.2003
 */
public class RedirectViewTests {
    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    @Test(expected = IllegalArgumentException.class)
    public void noUrlSet() throws Exception {
        RedirectView rv = new RedirectView();
        rv.afterPropertiesSet();
    }

    @Test
    public void http11() throws Exception {
        RedirectView rv = new RedirectView();
        rv.setUrl("http://url.somewhere.com");
        rv.setHttp10Compatible(false);
        rv.render(new HashMap(), request, response);
        Assert.assertEquals(303, response.getStatus());
        Assert.assertEquals("http://url.somewhere.com", response.getHeader("Location"));
    }

    @Test
    public void explicitStatusCodeHttp11() throws Exception {
        RedirectView rv = new RedirectView();
        rv.setUrl("http://url.somewhere.com");
        rv.setHttp10Compatible(false);
        rv.setStatusCode(MOVED_PERMANENTLY);
        rv.render(new HashMap(), request, response);
        Assert.assertEquals(301, response.getStatus());
        Assert.assertEquals("http://url.somewhere.com", response.getHeader("Location"));
    }

    @Test
    public void explicitStatusCodeHttp10() throws Exception {
        RedirectView rv = new RedirectView();
        rv.setUrl("http://url.somewhere.com");
        rv.setStatusCode(MOVED_PERMANENTLY);
        rv.render(new HashMap(), request, response);
        Assert.assertEquals(301, response.getStatus());
        Assert.assertEquals("http://url.somewhere.com", response.getHeader("Location"));
    }

    @Test
    public void attributeStatusCodeHttp10() throws Exception {
        RedirectView rv = new RedirectView();
        rv.setUrl("http://url.somewhere.com");
        request.setAttribute(RESPONSE_STATUS_ATTRIBUTE, CREATED);
        rv.render(new HashMap(), request, response);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertEquals("http://url.somewhere.com", response.getHeader("Location"));
    }

    @Test
    public void attributeStatusCodeHttp11() throws Exception {
        RedirectView rv = new RedirectView();
        rv.setUrl("http://url.somewhere.com");
        rv.setHttp10Compatible(false);
        request.setAttribute(RESPONSE_STATUS_ATTRIBUTE, CREATED);
        rv.render(new HashMap(), request, response);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertEquals("http://url.somewhere.com", response.getHeader("Location"));
    }

    @Test
    @SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
    public void flashMap() throws Exception {
        RedirectView rv = new RedirectView();
        rv.setUrl("http://url.somewhere.com/path");
        rv.setHttp10Compatible(false);
        FlashMap flashMap = new FlashMap();
        flashMap.put("successMessage", "yay!");
        request.setAttribute(OUTPUT_FLASH_MAP_ATTRIBUTE, flashMap);
        ModelMap model = new ModelMap("id", "1");
        rv.render(model, request, response);
        Assert.assertEquals(303, response.getStatus());
        Assert.assertEquals("http://url.somewhere.com/path?id=1", response.getHeader("Location"));
        Assert.assertEquals("/path", flashMap.getTargetRequestPath());
        Assert.assertEquals(model, flashMap.getTargetRequestParams().toSingleValueMap());
    }

    @Test
    public void updateTargetUrl() throws Exception {
        StaticWebApplicationContext wac = new StaticWebApplicationContext();
        wac.registerSingleton("requestDataValueProcessor", RequestDataValueProcessorWrapper.class);
        wac.setServletContext(new MockServletContext());
        wac.refresh();
        RequestDataValueProcessor mockProcessor = Mockito.mock(RequestDataValueProcessor.class);
        wac.getBean(RequestDataValueProcessorWrapper.class).setRequestDataValueProcessor(mockProcessor);
        RedirectView rv = new RedirectView();
        rv.setApplicationContext(wac);// Init RedirectView with WebAppCxt

        rv.setUrl("/path");
        request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        BDDMockito.given(mockProcessor.processUrl(request, "/path")).willReturn("/path?key=123");
        rv.render(new ModelMap(), request, response);
        Mockito.verify(mockProcessor).processUrl(request, "/path");
    }

    @Test
    public void updateTargetUrlWithContextLoader() throws Exception {
        StaticWebApplicationContext wac = new StaticWebApplicationContext();
        wac.registerSingleton("requestDataValueProcessor", RequestDataValueProcessorWrapper.class);
        MockServletContext servletContext = new MockServletContext();
        ContextLoader contextLoader = new ContextLoader(wac);
        contextLoader.initWebApplicationContext(servletContext);
        try {
            RequestDataValueProcessor mockProcessor = Mockito.mock(RequestDataValueProcessor.class);
            wac.getBean(RequestDataValueProcessorWrapper.class).setRequestDataValueProcessor(mockProcessor);
            RedirectView rv = new RedirectView();
            rv.setUrl("/path");
            BDDMockito.given(mockProcessor.processUrl(request, "/path")).willReturn("/path?key=123");
            rv.render(new ModelMap(), request, response);
            Mockito.verify(mockProcessor).processUrl(request, "/path");
        } finally {
            contextLoader.closeWebApplicationContext(servletContext);
        }
    }

    // SPR-13693
    @Test
    public void remoteHost() throws Exception {
        RedirectView rv = new RedirectView();
        Assert.assertFalse(rv.isRemoteHost("http://url.somewhere.com"));
        Assert.assertFalse(rv.isRemoteHost("/path"));
        Assert.assertFalse(rv.isRemoteHost("http://url.somewhereelse.com"));
        rv.setHosts(new String[]{ "url.somewhere.com" });
        Assert.assertFalse(rv.isRemoteHost("http://url.somewhere.com"));
        Assert.assertFalse(rv.isRemoteHost("/path"));
        Assert.assertTrue(rv.isRemoteHost("http://url.somewhereelse.com"));
    }

    // SPR-16752
    @Test
    public void contextRelativeWithValidatedContextPath() throws Exception {
        String url = "/myUrl";
        this.request.setContextPath("//context");
        this.response = new MockHttpServletResponse();
        doTest(new HashMap<>(), url, true, ("/context" + url));
        this.request.setContextPath("///context");
        this.response = new MockHttpServletResponse();
        doTest(new HashMap<>(), url, true, ("/context" + url));
    }

    @Test
    public void emptyMap() throws Exception {
        String url = "/myUrl";
        doTest(new HashMap<>(), url, false, url);
    }

    @Test
    public void emptyMapWithContextRelative() throws Exception {
        String url = "/myUrl";
        doTest(new HashMap<>(), url, true, ("/context" + url));
    }

    @Test
    public void singleParam() throws Exception {
        String url = "http://url.somewhere.com";
        String key = "foo";
        String val = "bar";
        Map<String, String> model = new HashMap<>();
        model.put(key, val);
        String expectedUrlForEncoding = (((url + "?") + key) + "=") + val;
        doTest(model, url, false, expectedUrlForEncoding);
    }

    @Test
    public void singleParamWithoutExposingModelAttributes() throws Exception {
        String url = "http://url.somewhere.com";
        Map<String, String> model = Collections.singletonMap("foo", "bar");
        RedirectViewTests.TestRedirectView rv = new RedirectViewTests.TestRedirectView(url, false, model);
        setExposeModelAttributes(false);
        rv.render(model, request, response);
        Assert.assertEquals(url, this.response.getRedirectedUrl());
    }

    @Test
    public void paramWithAnchor() throws Exception {
        String url = "http://url.somewhere.com/test.htm#myAnchor";
        String key = "foo";
        String val = "bar";
        Map<String, String> model = new HashMap<>();
        model.put(key, val);
        String expectedUrlForEncoding = (((("http://url.somewhere.com/test.htm" + "?") + key) + "=") + val) + "#myAnchor";
        doTest(model, url, false, expectedUrlForEncoding);
    }

    @Test
    public void contextRelativeQueryParam() throws Exception {
        String url = "/test.html?id=1";
        doTest(new HashMap<>(), url, true, ("/context" + url));
    }

    @Test
    public void twoParams() throws Exception {
        String url = "http://url.somewhere.com";
        String key = "foo";
        String val = "bar";
        String key2 = "thisIsKey2";
        String val2 = "andThisIsVal2";
        Map<String, String> model = new HashMap<>();
        model.put(key, val);
        model.put(key2, val2);
        try {
            String expectedUrlForEncoding = (((((((url + "?") + key) + "=") + val) + "&") + key2) + "=") + val2;
            doTest(model, url, false, expectedUrlForEncoding);
        } catch (AssertionError err) {
            // OK, so it's the other order... probably on Sun JDK 1.6 or IBM JDK 1.5
            String expectedUrlForEncoding = (((((((url + "?") + key2) + "=") + val2) + "&") + key) + "=") + val;
            doTest(model, url, false, expectedUrlForEncoding);
        }
    }

    @Test
    public void arrayParam() throws Exception {
        String url = "http://url.somewhere.com";
        String key = "foo";
        String[] val = new String[]{ "bar", "baz" };
        Map<String, String[]> model = new HashMap<>();
        model.put(key, val);
        try {
            String expectedUrlForEncoding = (((((((url + "?") + key) + "=") + (val[0])) + "&") + key) + "=") + (val[1]);
            doTest(model, url, false, expectedUrlForEncoding);
        } catch (AssertionError err) {
            // OK, so it's the other order... probably on Sun JDK 1.6 or IBM JDK 1.5
            String expectedUrlForEncoding = (((((((url + "?") + key) + "=") + (val[1])) + "&") + key) + "=") + (val[0]);
            doTest(model, url, false, expectedUrlForEncoding);
        }
    }

    @Test
    public void collectionParam() throws Exception {
        String url = "http://url.somewhere.com";
        String key = "foo";
        List<String> val = new ArrayList<>();
        val.add("bar");
        val.add("baz");
        Map<String, List<String>> model = new HashMap<>();
        model.put(key, val);
        try {
            String expectedUrlForEncoding = (((((((url + "?") + key) + "=") + (val.get(0))) + "&") + key) + "=") + (val.get(1));
            doTest(model, url, false, expectedUrlForEncoding);
        } catch (AssertionError err) {
            // OK, so it's the other order... probably on Sun JDK 1.6 or IBM JDK 1.5
            String expectedUrlForEncoding = (((((((url + "?") + key) + "=") + (val.get(1))) + "&") + key) + "=") + (val.get(0));
            doTest(model, url, false, expectedUrlForEncoding);
        }
    }

    @Test
    public void objectConversion() throws Exception {
        String url = "http://url.somewhere.com";
        String key = "foo";
        String val = "bar";
        String key2 = "int2";
        Object val2 = 611;
        String key3 = "tb";
        Object val3 = new TestBean();
        Map<String, Object> model = new LinkedHashMap<>();
        model.put(key, val);
        model.put(key2, val2);
        model.put(key3, val3);
        String expectedUrlForEncoding = (((((((url + "?") + key) + "=") + val) + "&") + key2) + "=") + val2;
        doTest(model, url, false, expectedUrlForEncoding);
    }

    @Test
    public void propagateQueryParams() throws Exception {
        RedirectView rv = new RedirectView();
        rv.setPropagateQueryParams(true);
        rv.setUrl("http://url.somewhere.com?foo=bar#bazz");
        request.setQueryString("a=b&c=d");
        rv.render(new HashMap(), request, response);
        Assert.assertEquals(302, response.getStatus());
        Assert.assertEquals("http://url.somewhere.com?foo=bar&a=b&c=d#bazz", response.getHeader("Location"));
    }

    private static class TestRedirectView extends RedirectView {
        private Map<String, ?> expectedModel;

        private boolean queryPropertiesCalled = false;

        public TestRedirectView(String url, boolean contextRelative, Map<String, ?> expectedModel) {
            super(url, contextRelative);
            this.expectedModel = expectedModel;
        }

        /**
         * Test whether this callback method is called with correct args
         */
        @Override
        protected Map<String, Object> queryProperties(Map<String, Object> model) {
            Assert.assertTrue("Map and model must be equal.", this.expectedModel.equals(model));
            this.queryPropertiesCalled = true;
            return super.queryProperties(model);
        }
    }
}

