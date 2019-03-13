/**
 * Copyright 2002-2015 the original author or authors.
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


import AbstractView.DEFAULT_CONTENT_TYPE;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.springframework.beans.factory.BeanIsAbstractException;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.View;


/**
 *
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
public class ResourceBundleViewResolverTests {
    /**
     * Comes from this package
     */
    private static String PROPS_FILE = "org.springframework.web.servlet.view.testviews";

    private final ResourceBundleViewResolver rb = new ResourceBundleViewResolver();

    private final StaticWebApplicationContext wac = new StaticWebApplicationContext();

    @Test
    public void parentsAreAbstract() throws Exception {
        try {
            rb.resolveViewName("debug.Parent", Locale.ENGLISH);
            Assert.fail("Should have thrown BeanIsAbstractException");
        } catch (BeanIsAbstractException ex) {
            // expected
        }
        try {
            rb.resolveViewName("testParent", Locale.ENGLISH);
            Assert.fail("Should have thrown BeanIsAbstractException");
        } catch (BeanIsAbstractException ex) {
            // expected
        }
    }

    @Test
    public void debugViewEnglish() throws Exception {
        View v = rb.resolveViewName("debugView", Locale.ENGLISH);
        Assert.assertThat(v, CoreMatchers.instanceOf(InternalResourceView.class));
        InternalResourceView jv = ((InternalResourceView) (v));
        Assert.assertEquals("debugView must have correct URL", "jsp/debug/debug.jsp", jv.getUrl());
        Map<String, Object> m = jv.getStaticAttributes();
        Assert.assertEquals("Must have 2 static attributes", 2, m.size());
        Assert.assertEquals("attribute foo", "bar", m.get("foo"));
        Assert.assertEquals("attribute postcode", "SE10 9JY", m.get("postcode"));
        Assert.assertEquals("Correct default content type", DEFAULT_CONTENT_TYPE, jv.getContentType());
    }

    @Test
    public void debugViewFrench() throws Exception {
        View v = rb.resolveViewName("debugView", Locale.FRENCH);
        Assert.assertThat(v, CoreMatchers.instanceOf(InternalResourceView.class));
        InternalResourceView jv = ((InternalResourceView) (v));
        Assert.assertEquals("French debugView must have correct URL", "jsp/debug/deboug.jsp", jv.getUrl());
        Assert.assertEquals("Correct overridden (XML) content type", "text/xml;charset=ISO-8859-1", jv.getContentType());
    }

    @Test
    public void eagerInitialization() throws Exception {
        ResourceBundleViewResolver rb = new ResourceBundleViewResolver();
        rb.setBasename(ResourceBundleViewResolverTests.PROPS_FILE);
        rb.setCache(getCache());
        rb.setDefaultParentView("testParent");
        rb.setLocalesToInitialize(new Locale[]{ Locale.ENGLISH, Locale.FRENCH });
        rb.setApplicationContext(wac);
        View v = rb.resolveViewName("debugView", Locale.FRENCH);
        Assert.assertThat(v, CoreMatchers.instanceOf(InternalResourceView.class));
        InternalResourceView jv = ((InternalResourceView) (v));
        Assert.assertEquals("French debugView must have correct URL", "jsp/debug/deboug.jsp", jv.getUrl());
        Assert.assertEquals("Correct overridden (XML) content type", "text/xml;charset=ISO-8859-1", jv.getContentType());
    }

    @Test
    public void sameBundleOnlyCachedOnce() throws Exception {
        Assume.assumeTrue(rb.isCache());
        View v1 = rb.resolveViewName("debugView", Locale.ENGLISH);
        View v2 = rb.resolveViewName("debugView", Locale.UK);
        Assert.assertSame(v1, v2);
    }

    @Test
    public void noSuchViewEnglish() throws Exception {
        Assert.assertNull(rb.resolveViewName("xxxxxxweorqiwuopeir", Locale.ENGLISH));
    }

    @Test
    public void onSetContextCalledOnce() throws Exception {
        ResourceBundleViewResolverTests.TestView tv = ((ResourceBundleViewResolverTests.TestView) (rb.resolveViewName("test", Locale.ENGLISH)));
        tv = ((ResourceBundleViewResolverTests.TestView) (rb.resolveViewName("test", Locale.ENGLISH)));
        tv = ((ResourceBundleViewResolverTests.TestView) (rb.resolveViewName("test", Locale.ENGLISH)));
        Assert.assertEquals("test has correct name", "test", getBeanName());
        Assert.assertEquals("test should have been initialized once, not ", 1, tv.initCount);
    }

    @Test(expected = MissingResourceException.class)
    public void noSuchBasename() throws Exception {
        rb.setBasename("weoriwoierqupowiuer");
        rb.resolveViewName("debugView", Locale.ENGLISH);
    }

    static class TestView extends AbstractView {
        public int initCount;

        public void setLocation(Resource location) {
            if (!(location instanceof ServletContextResource)) {
                throw new IllegalArgumentException(("Expecting ServletContextResource, not " + (location.getClass().getName())));
            }
        }

        @Override
        protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
        }

        @Override
        protected void initApplicationContext() {
            ++(initCount);
        }
    }
}

