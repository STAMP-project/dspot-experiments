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
package org.springframework.web.servlet.view;


import Config.FMT_LOCALE;
import Config.FMT_LOCALIZATION_CONTEXT;
import DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE;
import DispatcherServlet.THEME_RESOLVER_ATTRIBUTE;
import DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE;
import XmlViewResolver.DEFAULT_LOCATION;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.ApplicationContextException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.mock.web.test.MockRequestDispatcher;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.springframework.web.servlet.theme.FixedThemeResolver;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 18.06.2003
 */
public class ViewResolverTests {
    @Test
    public void testBeanNameViewResolver() throws ServletException {
        StaticWebApplicationContext wac = new StaticWebApplicationContext();
        wac.setServletContext(new MockServletContext());
        MutablePropertyValues pvs1 = new MutablePropertyValues();
        pvs1.addPropertyValue(new PropertyValue("url", "/example1.jsp"));
        wac.registerSingleton("example1", InternalResourceView.class, pvs1);
        MutablePropertyValues pvs2 = new MutablePropertyValues();
        pvs2.addPropertyValue(new PropertyValue("url", "/example2.jsp"));
        wac.registerSingleton("example2", JstlView.class, pvs2);
        BeanNameViewResolver vr = new BeanNameViewResolver();
        vr.setApplicationContext(wac);
        wac.refresh();
        View view = vr.resolveViewName("example1", Locale.getDefault());
        Assert.assertEquals("Correct view class", InternalResourceView.class, view.getClass());
        Assert.assertEquals("Correct URL", "/example1.jsp", getUrl());
        view = vr.resolveViewName("example2", Locale.getDefault());
        Assert.assertEquals("Correct view class", JstlView.class, view.getClass());
        Assert.assertEquals("Correct URL", "/example2.jsp", getUrl());
    }

    @Test
    public void testUrlBasedViewResolverWithoutPrefixes() throws Exception {
        UrlBasedViewResolver vr = new UrlBasedViewResolver();
        vr.setViewClass(JstlView.class);
        doTestUrlBasedViewResolverWithoutPrefixes(vr);
    }

    @Test
    public void testUrlBasedViewResolverWithPrefixes() throws Exception {
        UrlBasedViewResolver vr = new UrlBasedViewResolver();
        vr.setViewClass(JstlView.class);
        doTestUrlBasedViewResolverWithPrefixes(vr);
    }

    @Test
    public void testInternalResourceViewResolverWithoutPrefixes() throws Exception {
        doTestUrlBasedViewResolverWithoutPrefixes(new InternalResourceViewResolver());
    }

    @Test
    public void testInternalResourceViewResolverWithPrefixes() throws Exception {
        doTestUrlBasedViewResolverWithPrefixes(new InternalResourceViewResolver());
    }

    @Test
    public void testInternalResourceViewResolverWithAttributes() throws Exception {
        MockServletContext sc = new MockServletContext();
        StaticWebApplicationContext wac = new StaticWebApplicationContext();
        wac.setServletContext(sc);
        wac.refresh();
        InternalResourceViewResolver vr = new InternalResourceViewResolver();
        Properties props = new Properties();
        props.setProperty("key1", "value1");
        vr.setAttributes(props);
        Map<String, Object> map = new HashMap<>();
        map.put("key2", new Integer(2));
        vr.setAttributesMap(map);
        vr.setApplicationContext(wac);
        View view = vr.resolveViewName("example1", Locale.getDefault());
        Assert.assertEquals("Correct view class", JstlView.class, view.getClass());
        Assert.assertEquals("Correct URL", "example1", getUrl());
        Map<String, Object> attributes = getStaticAttributes();
        Assert.assertEquals("value1", attributes.get("key1"));
        Assert.assertEquals(new Integer(2), attributes.get("key2"));
        view = vr.resolveViewName("example2", Locale.getDefault());
        Assert.assertEquals("Correct view class", JstlView.class, view.getClass());
        Assert.assertEquals("Correct URL", "example2", getUrl());
        attributes = getStaticAttributes();
        Assert.assertEquals("value1", attributes.get("key1"));
        Assert.assertEquals(new Integer(2), attributes.get("key2"));
        MockHttpServletRequest request = new MockHttpServletRequest(sc);
        HttpServletResponse response = new MockHttpServletResponse();
        request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, new AcceptHeaderLocaleResolver());
        Map<String, Object> model = new HashMap<>();
        TestBean tb = new TestBean();
        model.put("tb", tb);
        view.render(model, request, response);
        Assert.assertTrue("Correct tb attribute", tb.equals(request.getAttribute("tb")));
        Assert.assertTrue("Correct rc attribute", ((request.getAttribute("rc")) == null));
        Assert.assertEquals("value1", request.getAttribute("key1"));
        Assert.assertEquals(new Integer(2), request.getAttribute("key2"));
    }

    @Test
    public void testInternalResourceViewResolverWithContextBeans() throws Exception {
        MockServletContext sc = new MockServletContext();
        final StaticWebApplicationContext wac = new StaticWebApplicationContext();
        wac.registerSingleton("myBean", TestBean.class);
        wac.registerSingleton("myBean2", TestBean.class);
        wac.setServletContext(sc);
        wac.refresh();
        InternalResourceViewResolver vr = new InternalResourceViewResolver();
        Properties props = new Properties();
        props.setProperty("key1", "value1");
        vr.setAttributes(props);
        Map<String, Object> map = new HashMap<>();
        map.put("key2", new Integer(2));
        vr.setAttributesMap(map);
        vr.setExposeContextBeansAsAttributes(true);
        vr.setApplicationContext(wac);
        MockHttpServletRequest request = new MockHttpServletRequest(sc) {
            @Override
            public RequestDispatcher getRequestDispatcher(String path) {
                return new MockRequestDispatcher(path) {
                    @Override
                    public void forward(ServletRequest forwardRequest, ServletResponse forwardResponse) {
                        Assert.assertTrue("Correct rc attribute", ((forwardRequest.getAttribute("rc")) == null));
                        Assert.assertEquals("value1", forwardRequest.getAttribute("key1"));
                        Assert.assertEquals(new Integer(2), forwardRequest.getAttribute("key2"));
                        Assert.assertSame(wac.getBean("myBean"), forwardRequest.getAttribute("myBean"));
                        Assert.assertSame(wac.getBean("myBean2"), forwardRequest.getAttribute("myBean2"));
                    }
                };
            }
        };
        HttpServletResponse response = new MockHttpServletResponse();
        request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, new AcceptHeaderLocaleResolver());
        View view = vr.resolveViewName("example1", Locale.getDefault());
        view.render(new HashMap<String, Object>(), request, response);
    }

    @Test
    public void testInternalResourceViewResolverWithSpecificContextBeans() throws Exception {
        MockServletContext sc = new MockServletContext();
        final StaticWebApplicationContext wac = new StaticWebApplicationContext();
        wac.registerSingleton("myBean", TestBean.class);
        wac.registerSingleton("myBean2", TestBean.class);
        wac.setServletContext(sc);
        wac.refresh();
        InternalResourceViewResolver vr = new InternalResourceViewResolver();
        Properties props = new Properties();
        props.setProperty("key1", "value1");
        vr.setAttributes(props);
        Map<String, Object> map = new HashMap<>();
        map.put("key2", new Integer(2));
        vr.setAttributesMap(map);
        vr.setExposedContextBeanNames(new String[]{ "myBean2" });
        vr.setApplicationContext(wac);
        MockHttpServletRequest request = new MockHttpServletRequest(sc) {
            @Override
            public RequestDispatcher getRequestDispatcher(String path) {
                return new MockRequestDispatcher(path) {
                    @Override
                    public void forward(ServletRequest forwardRequest, ServletResponse forwardResponse) {
                        Assert.assertTrue("Correct rc attribute", ((forwardRequest.getAttribute("rc")) == null));
                        Assert.assertEquals("value1", forwardRequest.getAttribute("key1"));
                        Assert.assertEquals(new Integer(2), forwardRequest.getAttribute("key2"));
                        Assert.assertNull(forwardRequest.getAttribute("myBean"));
                        Assert.assertSame(wac.getBean("myBean2"), forwardRequest.getAttribute("myBean2"));
                    }
                };
            }
        };
        HttpServletResponse response = new MockHttpServletResponse();
        request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, new AcceptHeaderLocaleResolver());
        View view = vr.resolveViewName("example1", Locale.getDefault());
        view.render(new HashMap<String, Object>(), request, response);
    }

    @Test
    public void testInternalResourceViewResolverWithJstl() throws Exception {
        Locale locale = (!(Locale.GERMAN.equals(Locale.getDefault()))) ? Locale.GERMAN : Locale.FRENCH;
        MockServletContext sc = new MockServletContext();
        StaticWebApplicationContext wac = new StaticWebApplicationContext();
        wac.setServletContext(sc);
        wac.addMessage("code1", locale, "messageX");
        wac.refresh();
        InternalResourceViewResolver vr = new InternalResourceViewResolver();
        vr.setViewClass(JstlView.class);
        vr.setApplicationContext(wac);
        View view = vr.resolveViewName("example1", Locale.getDefault());
        Assert.assertEquals("Correct view class", JstlView.class, view.getClass());
        Assert.assertEquals("Correct URL", "example1", getUrl());
        view = vr.resolveViewName("example2", Locale.getDefault());
        Assert.assertEquals("Correct view class", JstlView.class, view.getClass());
        Assert.assertEquals("Correct URL", "example2", getUrl());
        MockHttpServletRequest request = new MockHttpServletRequest(sc);
        HttpServletResponse response = new MockHttpServletResponse();
        request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, new FixedLocaleResolver(locale));
        Map<String, Object> model = new HashMap<>();
        TestBean tb = new TestBean();
        model.put("tb", tb);
        view.render(model, request, response);
        Assert.assertTrue("Correct tb attribute", tb.equals(request.getAttribute("tb")));
        Assert.assertTrue("Correct rc attribute", ((request.getAttribute("rc")) == null));
        Assert.assertEquals(locale, Config.get(request, FMT_LOCALE));
        LocalizationContext lc = ((LocalizationContext) (Config.get(request, FMT_LOCALIZATION_CONTEXT)));
        Assert.assertEquals("messageX", lc.getResourceBundle().getString("code1"));
    }

    @Test
    public void testInternalResourceViewResolverWithJstlAndContextParam() throws Exception {
        Locale locale = (!(Locale.GERMAN.equals(Locale.getDefault()))) ? Locale.GERMAN : Locale.FRENCH;
        MockServletContext sc = new MockServletContext();
        sc.addInitParameter(FMT_LOCALIZATION_CONTEXT, "org/springframework/web/context/WEB-INF/context-messages");
        StaticWebApplicationContext wac = new StaticWebApplicationContext();
        wac.setServletContext(sc);
        wac.addMessage("code1", locale, "messageX");
        wac.refresh();
        InternalResourceViewResolver vr = new InternalResourceViewResolver();
        vr.setViewClass(JstlView.class);
        vr.setApplicationContext(wac);
        View view = vr.resolveViewName("example1", Locale.getDefault());
        Assert.assertEquals("Correct view class", JstlView.class, view.getClass());
        Assert.assertEquals("Correct URL", "example1", getUrl());
        view = vr.resolveViewName("example2", Locale.getDefault());
        Assert.assertEquals("Correct view class", JstlView.class, view.getClass());
        Assert.assertEquals("Correct URL", "example2", getUrl());
        MockHttpServletRequest request = new MockHttpServletRequest(sc);
        HttpServletResponse response = new MockHttpServletResponse();
        request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, new FixedLocaleResolver(locale));
        Map<String, Object> model = new HashMap<>();
        TestBean tb = new TestBean();
        model.put("tb", tb);
        view.render(model, request, response);
        Assert.assertTrue("Correct tb attribute", tb.equals(request.getAttribute("tb")));
        Assert.assertTrue("Correct rc attribute", ((request.getAttribute("rc")) == null));
        Assert.assertEquals(locale, Config.get(request, FMT_LOCALE));
        LocalizationContext lc = ((LocalizationContext) (Config.get(request, FMT_LOCALIZATION_CONTEXT)));
        Assert.assertEquals("message1", lc.getResourceBundle().getString("code1"));
        Assert.assertEquals("message2", lc.getResourceBundle().getString("code2"));
    }

    @Test
    public void testXmlViewResolver() throws Exception {
        StaticWebApplicationContext wac = new StaticWebApplicationContext();
        wac.registerSingleton("testBean", TestBean.class);
        wac.setServletContext(new MockServletContext());
        wac.refresh();
        TestBean testBean = ((TestBean) (wac.getBean("testBean")));
        XmlViewResolver vr = new XmlViewResolver();
        vr.setLocation(new ClassPathResource("org/springframework/web/servlet/view/views.xml"));
        vr.setApplicationContext(wac);
        View view1 = vr.resolveViewName("example1", Locale.getDefault());
        Assert.assertTrue("Correct view class", ViewResolverTests.TestView.class.equals(view1.getClass()));
        Assert.assertTrue("Correct URL", "/example1.jsp".equals(getUrl()));
        View view2 = vr.resolveViewName("example2", Locale.getDefault());
        Assert.assertTrue("Correct view class", JstlView.class.equals(view2.getClass()));
        Assert.assertTrue("Correct URL", "/example2new.jsp".equals(getUrl()));
        ServletContext sc = new MockServletContext();
        Map<String, Object> model = new HashMap<>();
        TestBean tb = new TestBean();
        model.put("tb", tb);
        HttpServletRequest request = new MockHttpServletRequest(sc);
        HttpServletResponse response = new MockHttpServletResponse();
        request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, new AcceptHeaderLocaleResolver());
        request.setAttribute(THEME_RESOLVER_ATTRIBUTE, new FixedThemeResolver());
        view1.render(model, request, response);
        Assert.assertTrue("Correct tb attribute", tb.equals(request.getAttribute("tb")));
        Assert.assertTrue("Correct test1 attribute", "testvalue1".equals(request.getAttribute("test1")));
        Assert.assertTrue("Correct test2 attribute", testBean.equals(request.getAttribute("test2")));
        request = new MockHttpServletRequest(sc);
        response = new MockHttpServletResponse();
        request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, new AcceptHeaderLocaleResolver());
        request.setAttribute(THEME_RESOLVER_ATTRIBUTE, new FixedThemeResolver());
        view2.render(model, request, response);
        Assert.assertTrue("Correct tb attribute", tb.equals(request.getAttribute("tb")));
        Assert.assertTrue("Correct test1 attribute", "testvalue1".equals(request.getAttribute("test1")));
        Assert.assertTrue("Correct test2 attribute", "testvalue2".equals(request.getAttribute("test2")));
    }

    @Test
    public void testXmlViewResolverDefaultLocation() {
        StaticWebApplicationContext wac = new StaticWebApplicationContext() {
            @Override
            protected Resource getResourceByPath(String path) {
                Assert.assertTrue("Correct default location", DEFAULT_LOCATION.equals(path));
                return super.getResourceByPath(path);
            }
        };
        wac.setServletContext(new MockServletContext());
        wac.refresh();
        XmlViewResolver vr = new XmlViewResolver();
        try {
            vr.setApplicationContext(wac);
            vr.afterPropertiesSet();
            Assert.fail("Should have thrown BeanDefinitionStoreException");
        } catch (BeanDefinitionStoreException ex) {
            // expected
        }
    }

    @Test
    public void testXmlViewResolverWithoutCache() throws Exception {
        StaticWebApplicationContext wac = new StaticWebApplicationContext() {
            @Override
            protected Resource getResourceByPath(String path) {
                Assert.assertTrue("Correct default location", DEFAULT_LOCATION.equals(path));
                return super.getResourceByPath(path);
            }
        };
        wac.setServletContext(new MockServletContext());
        wac.refresh();
        XmlViewResolver vr = new XmlViewResolver();
        vr.setCache(false);
        try {
            vr.setApplicationContext(wac);
        } catch (ApplicationContextException ex) {
            Assert.fail(("Should not have thrown ApplicationContextException: " + (ex.getMessage())));
        }
        try {
            vr.resolveViewName("example1", Locale.getDefault());
            Assert.fail("Should have thrown BeanDefinitionStoreException");
        } catch (BeanDefinitionStoreException ex) {
            // expected
        }
    }

    @Test
    public void testCacheRemoval() throws Exception {
        StaticWebApplicationContext wac = new StaticWebApplicationContext();
        wac.setServletContext(new MockServletContext());
        wac.refresh();
        InternalResourceViewResolver vr = new InternalResourceViewResolver();
        vr.setViewClass(JstlView.class);
        vr.setApplicationContext(wac);
        View view = vr.resolveViewName("example1", Locale.getDefault());
        View cached = vr.resolveViewName("example1", Locale.getDefault());
        if (view != cached) {
            Assert.fail("Caching doesn't work");
        }
        vr.removeFromCache("example1", Locale.getDefault());
        cached = vr.resolveViewName("example1", Locale.getDefault());
        if (view == cached) {
            // the chance of having the same reference (hashCode) twice if negligible).
            Assert.fail("View wasn't removed from cache");
        }
    }

    @Test
    public void testCacheUnresolved() throws Exception {
        final AtomicInteger count = new AtomicInteger();
        AbstractCachingViewResolver viewResolver = new AbstractCachingViewResolver() {
            @Override
            protected View loadView(String viewName, Locale locale) throws Exception {
                count.incrementAndGet();
                return null;
            }
        };
        viewResolver.setCacheUnresolved(false);
        viewResolver.resolveViewName("view", Locale.getDefault());
        viewResolver.resolveViewName("view", Locale.getDefault());
        Assert.assertEquals(2, count.intValue());
        viewResolver.setCacheUnresolved(true);
        viewResolver.resolveViewName("view", Locale.getDefault());
        viewResolver.resolveViewName("view", Locale.getDefault());
        viewResolver.resolveViewName("view", Locale.getDefault());
        viewResolver.resolveViewName("view", Locale.getDefault());
        viewResolver.resolveViewName("view", Locale.getDefault());
        Assert.assertEquals(3, count.intValue());
    }

    public static class TestView extends InternalResourceView {
        public void setLocation(Resource location) {
            if (!(location instanceof ServletContextResource)) {
                throw new IllegalArgumentException(("Expecting ClassPathResource, not " + (location.getClass().getName())));
            }
        }
    }
}

