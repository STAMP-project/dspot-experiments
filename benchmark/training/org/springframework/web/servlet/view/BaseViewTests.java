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


import View.PATH_VARIABLES;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContextException;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.web.context.WebApplicationContext;


/**
 * Base tests for {@link AbstractView}.
 *
 * <p>Not called {@code AbstractViewTests} since doing so would cause it
 * to be ignored in the Gradle build.
 *
 * @author Rod Johnson
 * @author Sam Brannen
 */
public class BaseViewTests {
    @Test
    public void renderWithoutStaticAttributes() throws Exception {
        WebApplicationContext wac = Mockito.mock(WebApplicationContext.class);
        BDDMockito.given(wac.getServletContext()).willReturn(new MockServletContext());
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        BaseViewTests.TestView tv = new BaseViewTests.TestView(wac);
        // Check superclass handles duplicate init
        tv.setApplicationContext(wac);
        tv.setApplicationContext(wac);
        Map<String, Object> model = new HashMap<>();
        model.put("foo", "bar");
        model.put("something", new Object());
        tv.render(model, request, response);
        checkContainsAll(model, tv.model);
        Assert.assertTrue(tv.initialized);
    }

    /**
     * Test attribute passing, NOT CSV parsing.
     */
    @Test
    public void renderWithStaticAttributesNoCollision() throws Exception {
        WebApplicationContext wac = Mockito.mock(WebApplicationContext.class);
        BDDMockito.given(wac.getServletContext()).willReturn(new MockServletContext());
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        BaseViewTests.TestView tv = new BaseViewTests.TestView(wac);
        tv.setApplicationContext(wac);
        Properties p = new Properties();
        p.setProperty("foo", "bar");
        p.setProperty("something", "else");
        setAttributes(p);
        Map<String, Object> model = new HashMap<>();
        model.put("one", new HashMap<>());
        model.put("two", new Object());
        tv.render(model, request, response);
        checkContainsAll(model, tv.model);
        checkContainsAll(p, tv.model);
        Assert.assertTrue(tv.initialized);
    }

    @Test
    public void pathVarsOverrideStaticAttributes() throws Exception {
        WebApplicationContext wac = Mockito.mock(WebApplicationContext.class);
        BDDMockito.given(wac.getServletContext()).willReturn(new MockServletContext());
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        BaseViewTests.TestView tv = new BaseViewTests.TestView(wac);
        tv.setApplicationContext(wac);
        Properties p = new Properties();
        p.setProperty("one", "bar");
        p.setProperty("something", "else");
        setAttributes(p);
        Map<String, Object> pathVars = new HashMap<>();
        pathVars.put("one", new HashMap<>());
        pathVars.put("two", new Object());
        request.setAttribute(PATH_VARIABLES, pathVars);
        tv.render(new HashMap(), request, response);
        checkContainsAll(pathVars, tv.model);
        Assert.assertEquals(3, tv.model.size());
        Assert.assertEquals("else", tv.model.get("something"));
        Assert.assertTrue(tv.initialized);
    }

    @Test
    public void dynamicModelOverridesStaticAttributesIfCollision() throws Exception {
        WebApplicationContext wac = Mockito.mock(WebApplicationContext.class);
        BDDMockito.given(wac.getServletContext()).willReturn(new MockServletContext());
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        BaseViewTests.TestView tv = new BaseViewTests.TestView(wac);
        tv.setApplicationContext(wac);
        Properties p = new Properties();
        p.setProperty("one", "bar");
        p.setProperty("something", "else");
        setAttributes(p);
        Map<String, Object> model = new HashMap<>();
        model.put("one", new HashMap<>());
        model.put("two", new Object());
        tv.render(model, request, response);
        // Check it contains all
        checkContainsAll(model, tv.model);
        Assert.assertEquals(3, tv.model.size());
        Assert.assertEquals("else", tv.model.get("something"));
        Assert.assertTrue(tv.initialized);
    }

    @Test
    public void dynamicModelOverridesPathVariables() throws Exception {
        WebApplicationContext wac = Mockito.mock(WebApplicationContext.class);
        BDDMockito.given(wac.getServletContext()).willReturn(new MockServletContext());
        BaseViewTests.TestView tv = new BaseViewTests.TestView(wac);
        tv.setApplicationContext(wac);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Map<String, Object> pathVars = new HashMap<>();
        pathVars.put("one", "bar");
        pathVars.put("something", "else");
        request.setAttribute(PATH_VARIABLES, pathVars);
        Map<String, Object> model = new HashMap<>();
        model.put("one", new HashMap<>());
        model.put("two", new Object());
        tv.render(model, request, response);
        checkContainsAll(model, tv.model);
        Assert.assertEquals(3, tv.model.size());
        Assert.assertEquals("else", tv.model.get("something"));
        Assert.assertTrue(tv.initialized);
    }

    @Test
    public void ignoresNullAttributes() {
        AbstractView v = new BaseViewTests.ConcreteView();
        v.setAttributes(null);
        Assert.assertEquals(0, v.getStaticAttributes().size());
    }

    /**
     * Test only the CSV parsing implementation.
     */
    @Test
    public void attributeCSVParsingIgnoresNull() {
        AbstractView v = new BaseViewTests.ConcreteView();
        v.setAttributesCSV(null);
        Assert.assertEquals(0, v.getStaticAttributes().size());
    }

    @Test
    public void attributeCSVParsingIgnoresEmptyString() {
        AbstractView v = new BaseViewTests.ConcreteView();
        v.setAttributesCSV("");
        Assert.assertEquals(0, v.getStaticAttributes().size());
    }

    /**
     * Format is attname0={value1},attname1={value1}
     */
    @Test
    public void attributeCSVParsingValid() {
        AbstractView v = new BaseViewTests.ConcreteView();
        v.setAttributesCSV("foo=[bar],king=[kong]");
        Assert.assertTrue(((v.getStaticAttributes().size()) == 2));
        Assert.assertTrue(v.getStaticAttributes().get("foo").equals("bar"));
        Assert.assertTrue(v.getStaticAttributes().get("king").equals("kong"));
    }

    @Test
    public void attributeCSVParsingValidWithWeirdCharacters() {
        AbstractView v = new BaseViewTests.ConcreteView();
        String fooval = "owfie   fue&3[][[[2 \n\n \r  \t 8\ufffd3";
        // Also tests empty value
        String kingval = "";
        v.setAttributesCSV((((("foo=(" + fooval) + "),king={") + kingval) + "},f1=[we]"));
        Assert.assertTrue(((v.getStaticAttributes().size()) == 3));
        Assert.assertTrue(v.getStaticAttributes().get("foo").equals(fooval));
        Assert.assertTrue(v.getStaticAttributes().get("king").equals(kingval));
    }

    @Test
    public void attributeCSVParsingInvalid() {
        AbstractView v = new BaseViewTests.ConcreteView();
        try {
            // No equals
            v.setAttributesCSV("fweoiruiu");
            Assert.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            // No value
            v.setAttributesCSV("fweoiruiu=");
            Assert.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            // No closing ]
            v.setAttributesCSV("fweoiruiu=[");
            Assert.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            // Second one is bogus
            v.setAttributesCSV("fweoiruiu=[de],=");
            Assert.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void attributeCSVParsingIgnoresTrailingComma() {
        AbstractView v = new BaseViewTests.ConcreteView();
        v.setAttributesCSV("foo=[de],");
        Assert.assertEquals(1, v.getStaticAttributes().size());
    }

    /**
     * Trivial concrete subclass we can use when we're interested only
     * in CSV parsing, which doesn't require lifecycle management
     */
    private static class ConcreteView extends AbstractView {
        // Do-nothing concrete subclass
        @Override
        protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Single threaded subclass of AbstractView to check superclass behavior.
     */
    private static class TestView extends AbstractView {
        private final WebApplicationContext wac;

        boolean initialized;

        /**
         * Captured model in render
         */
        Map<String, Object> model;

        TestView(WebApplicationContext wac) {
            this.wac = wac;
        }

        @Override
        protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            this.model = model;
        }

        /**
         *
         *
         * @see org.springframework.context.support.ApplicationObjectSupport#initApplicationContext()
         */
        @Override
        protected void initApplicationContext() throws ApplicationContextException {
            if (initialized) {
                throw new RuntimeException("Already initialized");
            }
            this.initialized = true;
            Assert.assertTrue(((getApplicationContext()) == (wac)));
        }
    }
}

